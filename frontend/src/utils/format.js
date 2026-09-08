const formatter = new Intl.NumberFormat('en-TZ', { maximumFractionDigits: 0 });

export function formatAmount(value) {
  if (value === null || value === undefined || value === '') return 'No budget limit';
  return `${formatter.format(Number(value))} TZS`;
}

export function formatRange(min, max) {
  const minNum = Number(min);
  const maxNum = Number(max);
  if (minNum === 0 && maxNum === 0) return null; // e.g. restaurant items with no fixed cost in this MVP
  if (minNum === maxNum) return formatAmount(minNum);
  return `${formatter.format(minNum)} – ${formatAmount(maxNum)}`;
}
