package com.example.data

import android.content.Context
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import android.util.Base64
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import kotlinx.coroutines.tasks.await
import java.io.FileOutputStream

data class IssuedReceipt(
  val kind: String,
  val series: String,
  val number: String,
  val pdf: ByteArray
)

object TaxReceipts {
  suspend fun issue(
    chargeId: String,
    kind: String,
    customerName: String,
    document: String,
    items: List<Map<String, Any>>
  ): IssuedReceipt {
    return try {
      val result = FirebaseFunctions.getInstance("us-central1")
        .getHttpsCallable("issueTaxReceipt")
        .call(
          mapOf(
            "chargeId" to chargeId,
            "kind" to kind,
            "customerName" to customerName,
            "document" to document,
            "items" to items
          )
        )
        .await()
      val data = result.getData() as? Map<*, *>
      val pdf = data?.get("pdfBase64") as? String
      val series = data?.get("series") as? String
      val number = data?.get("number") as? String
      val issuedKind = data?.get("kind") as? String
      if (pdf.isNullOrBlank() || series.isNullOrBlank() || number.isNullOrBlank() || issuedKind.isNullOrBlank()) {
        error("No llegó el comprobante.")
      }
      IssuedReceipt(issuedKind, series, number, Base64.decode(pdf, Base64.DEFAULT))
    } catch (error: FirebaseFunctionsException) {
      error(error.message ?: "No se pudo emitir el comprobante.")
    }
  }

  fun print(context: Context, jobName: String, pdf: ByteArray) {
    val manager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
    manager.print(
      jobName,
      object : PrintDocumentAdapter() {
        override fun onLayout(
          oldAttributes: PrintAttributes?,
          newAttributes: PrintAttributes?,
          cancellationSignal: CancellationSignal?,
          callback: LayoutResultCallback?,
          extras: Bundle?
        ) {
          if (cancellationSignal?.isCanceled == true) {
            callback?.onLayoutCancelled()
            return
          }
          callback?.onLayoutFinished(
            PrintDocumentInfo.Builder(jobName)
              .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
              .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
              .build(),
            true
          )
        }

        override fun onWrite(
          pages: Array<out PageRange>?,
          destination: ParcelFileDescriptor?,
          cancellationSignal: CancellationSignal?,
          callback: WriteResultCallback?
        ) {
          if (destination == null) {
            callback?.onWriteFailed("No se pudo preparar la impresión.")
            return
          }
          FileOutputStream(destination.fileDescriptor).use { output ->
            output.write(pdf)
          }
          callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
        }
      },
      null
    )
  }
}
