const API_BASE = 'http://localhost:8080/api';

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
    getProizvod: (id) => apiFetch(`/proizvodi/${id}`),
    searchProizvodi: (naziv) => apiFetch(`/proizvodi/search?naziv=${encodeURIComponent(naziv)}`),
    getProizvodiByKategorija: (id) => apiFetch(`/proizvodi/kategorija/${id}`),

    // Kategorije
    getKategorije: () => apiFetch('/kategorije'),

    // Omiljeni
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

    // Porudžbine
    kreirajPorudzbinu: (payload) =>
        apiFetch('/porudzbine', { method: 'POST', body: JSON.stringify(payload) }),
    getPorudzbine: (kupacId) => apiFetch(`/porudzbine/kupac/${kupacId}`),

    // ── Tracking ──────────────────────────────────────────
    // Nikad ne baca grešku — tracking ne sme blokirati UI
    zabeleziKlik: (kupacId, proizvodId, tipAkcije = 'PREGLED') => {
        if (!kupacId || !proizvodId) return Promise.resolve();
        return apiFetch(`/tracking/klik/${kupacId}/proizvodi/${proizvodId}`, {
            method: 'POST',
            body: JSON.stringify({ tipAkcije })
        }).catch(e => console.warn('Tracking klik greška:', e));
    },

    zabeleziKlikKategorija: (kupacId, kategorijaId, tipAkcije = 'PREGLED') => {
        if (!kupacId || !kategorijaId) return Promise.resolve();
        return apiFetch(`/tracking/klik/${kupacId}/kategorije/${kategorijaId}`, {
            method: 'POST',
            body: JSON.stringify({ tipAkcije })
        }).catch(e => console.warn('Tracking klik kategorija greška:', e));
    },

    zabeleziPretragu: (kupacId, tekstUpita) => {
        if (!kupacId || !tekstUpita) return Promise.resolve();
        return apiFetch(`/tracking/pretraga/${kupacId}`, {
            method: 'POST',
            body: JSON.stringify({
                tekstUpita,
                tipPretrage: 'OPSTA'
            })
        }).catch(e => console.warn('Tracking pretraga greška:', e));
    },
};