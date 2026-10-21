function locationQuery(address: string, district: string): string {
  return [address, district].map((part) => part.trim()).filter(Boolean).join(', ');
}

export function googleMapsSearchUrl(address: string, district: string): string {
  const query = locationQuery(address, district);
  return `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(query)}`;
}

export function googleMapsEmbedUrl(address: string, district: string): string {
  const query = locationQuery(address, district);
  return `https://maps.google.com/maps?q=${encodeURIComponent(query)}&output=embed`;
}
