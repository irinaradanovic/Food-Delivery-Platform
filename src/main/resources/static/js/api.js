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
    // Kupci
    getKupciByEmail: (email) => apiFetch(`/kupci?email=${encodeURIComponent(email)}`),

    // Proizvodi
    getProizvodi: () => apiFetch('/proizvodi'),
    searchProizvodi: (naziv) => apiFetch(`/proizvodi/search?naziv=${encodeURIComponent(naziv)}`),
    getProizvodiByKategorija: (id) => apiFetch(`/proizvodi/kategorija/${id}`),

    // Kategorije
    getKategorije: () => apiFetch('/kategorije'),

    // Omiljeni proizvodi
    getOmiljeni: (kupacId) => apiFetch(`/omiljeni/${kupacId}`),
    dodajOmiljeni: (kupacId, proizvodId) =>
        apiFetch(`/omiljeni/${kupacId}/proizvodi/${proizvodId}`, { method: 'POST' }),
    ukloniOmiljeni: (kupacId, proizvodId) =>
        apiFetch(`/omiljeni/${kupacId}/proizvodi/${proizvodId}`, { method: 'DELETE' }),

    // Omiljene kategorije
    getOmiljeneKategorije: (kupacId) => apiFetch(`/omiljene-kategorije/${kupacId}`),
    dodajOmiljenuKategoriju: (kupacId, kategorijaId) =>
        apiFetch(`/omiljene-kategorije/${kupacId}/kategorije/${kategorijaId}`, { method: 'POST' }),
    ukloniOmiljenuKategoriju: (kupacId, kategorijaId) =>
        apiFetch(`/omiljene-kategorije/${kupacId}/kategorije/${kategorijaId}`, { method: 'DELETE' }),

    // Tracking
    zabeleziKlik: (kupacId, proizvodId) =>
        apiFetch(`/tracking/klik/${kupacId}/proizvodi/${proizvodId}`, { method: 'POST' }),
    zabeleziPretragu: (kupacId, tekst) =>
        apiFetch(`/tracking/pretraga/${kupacId}`, {
            method: 'POST',
            body: JSON.stringify({ tekstUpita: tekst, tipPretrage: 'OPSTA' })
        }),
};