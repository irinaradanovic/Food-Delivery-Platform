const API_BASE = 'http://localhost:8080/api';

function getKupacId() {
    return localStorage.getItem('kupacId');
}

async function apiFetch(url, options = {}) {
    const res = await fetch(`${API_BASE}${url}`, {
        headers: { 'Content-Type': 'application/json' },
        ...options
    });
    if (!res.ok) throw new Error(`${res.status}`);
    if (res.status === 204) return null;
    return res.json();
}

const api = {
    getKupciByEmail: (email) => apiFetch(`/kupci?email=${encodeURIComponent(email)}`),
    getProizvodi: () => apiFetch('/proizvodi'),
    searchProizvodi: (naziv) => apiFetch(`/proizvodi/search?naziv=${encodeURIComponent(naziv)}`),
    getProizvodiByKategorija: (id) => apiFetch(`/proizvodi/kategorija/${id}`),
    getKategorije: () => apiFetch('/kategorije'),
    getOmiljeni: (kupacId) => apiFetch(`/omiljeni/${kupacId}`),
    dodajOmiljeni: (kupacId, proizvodId) =>
        apiFetch(`/omiljeni/${kupacId}/proizvodi/${proizvodId}`, { method: 'POST' }),
    ukloniOmiljeni: (kupacId, proizvodId) =>
        apiFetch(`/omiljeni/${kupacId}/proizvodi/${proizvodId}`, { method: 'DELETE' }),
    zabeleziKlik: (kupacId, proizvodId) =>
        apiFetch(`/tracking/klik/${kupacId}/proizvodi/${proizvodId}`, { method: 'POST' }),
    zabeleziPretragu: (kupacId, tekst) =>
        apiFetch(`/tracking/pretraga/${kupacId}`, {
            method: 'POST',
            body: JSON.stringify({ tekstUpita: tekst, tipPretrage: 'OPSTA' })
        }),
};