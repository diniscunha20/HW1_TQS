const j = async (r) => {
let data = null;
const text = await r.text();
try { data = text ? JSON.parse(text) : null; } catch { /* ignore */ }
if (!r.ok) {
const msg = (data && (data.message || data.error)) || text || 'Erro de rede';
throw new Error(msg);
}
return data;
};


export async function fetchMunicipalities(q = '') {
const r = await fetch(`/api/municipalities?query=${encodeURIComponent(q)}`);
return j(r);
}


export async function createBooking({ name, municipalityCode, date, timeSlot, description }) {
const r = await fetch('/api/bookings', {
method: 'POST', headers: { 'Content-Type': 'application/json' },
body: JSON.stringify({ name, municipalityCode, date, timeSlot, description })
});
return j(r); // → { token, status, id? }
}


export async function getByToken(token) {
const r = await fetch(`/api/bookings/${encodeURIComponent(token)}`);
return j(r);
}


export async function listBookings({ municipalityCode, status, page = 0, size = 20 }) {
const qs = new URLSearchParams({ municipalityCode, status, page, size });
const r = await fetch(`/api/bookings?${qs.toString()}`);
return j(r); // → { items, total }
}


export async function updateStatus(id, status) {
const r = await fetch(`/api/bookings/${id}/status`, {
method: 'PATCH', headers: { 'Content-Type': 'application/json' },
body: JSON.stringify({ status })
});
return j(r);
}

export const getGlobalLimits = () =>
  fetch('/api/bookings/limits').then(r => {
    if (!r.ok) throw new Error('Falha ao obter limites');
    return r.json(); // { maxPerDay: number }
  });

export const updateGlobalLimits = (maxPerDay) =>
  fetch('/api/bookings/limits', {
    method: 'PUT',
    headers: {'Content-Type':'application/json'},
    body: JSON.stringify({ maxPerDay: Number(maxPerDay) })
  }).then(r => {
    if (!r.ok) throw new Error('Falha ao atualizar limites');
  });

  export async function countBookingsByDate(date, activeOnly = true) {
  const qs = new URLSearchParams({ date, activeOnly: String(activeOnly) });
  const r = await fetch(`/api/bookings/count?${qs.toString()}`);
  return j(r); // devolve um número
}
