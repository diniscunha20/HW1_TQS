export async function fetchMunicipalities(q='') {
  const r = await fetch(`/api/municipalities?query=${encodeURIComponent(q)}`);
  if (!r.ok) throw new Error('Failed municipalities'); return r.json();
}
export async function createBooking(data) {
  const r = await fetch('/api/bookings', { method:'POST', headers:{'Content-Type':'application/json'}, body:JSON.stringify(data) });
  if (!r.ok) throw new Error('Failed create'); return r.json();
}
export async function getByToken(token) {
  const r = await fetch(`/api/bookings/${token}`); if (!r.ok) throw new Error('Not found'); return r.json();
}
export async function listBookings({municipality, status, page=0, size=20}) {
  const qs = new URLSearchParams({ municipality, status, page, size });
  const r = await fetch(`/api/bookings?${qs}`); if (!r.ok) throw new Error('Failed list'); return r.json();
}
export async function updateStatus(id, status) {
  const r = await fetch(`/api/bookings/${id}/status`, { method:'PATCH', headers:{'Content-Type':'application/json'}, body:JSON.stringify({status}) });
  if (!r.ok) throw new Error('Failed update'); return r.json();
}
