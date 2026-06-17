const API_BASE = 'http://localhost:8080/api';

async function apiFetch(url, options = {}) {
    const res = await fetch(`${API_BASE}${url}`, {
        headers: { 'Content-Type': 'application/json' },
        ...options
    });
    if (!res.ok) {
        let message = 'Zahtev nije uspeo. Pokusajte ponovo.';
        try {
            const data = await res.json();
            message = data.greska || data.poruka || message;
        } catch (_) {
            // Keep the generic message when the server does not return JSON.
        }
        throw new Error(message);
    }
    if (res.status === 204) return null;
    return res.json();
}

const api = {

    // ── Kupci ──────────────────────────────────────────────
    getKupciByEmail: (email) => apiFetch(`/kupci?email=${encodeURIComponent(email)}`),

    // ── Restorani ──────────────────────────────────────────
    getSviRestorani: () => apiFetch('/restorani'),
    getRestoranByMenadzer: (menadzerId) => apiFetch(`/restorani/menadzer/${menadzerId}`),

    // ── Proizvodi ──────────────────────────────────────────
    // Menadzer - vidi sve
    getProizvodi: () => apiFetch('/proizvodi'),
    getProizvod: (id) => apiFetch(`/proizvodi/${id}`),
    searchProizvodi: (naziv) => apiFetch(`/proizvodi/search?naziv=${encodeURIComponent(naziv)}`),
    getProizvodiByKategorija: (id) => apiFetch(`/proizvodi/kategorija/${id}`),

    // Kupac - samo iz aktivnih menija izabranog restorana
    getProizvodiZaKupca: () => {
        const restoranId = localStorage.getItem('restoranId');
        if (!restoranId) return apiFetch('/proizvodi');
        return apiFetch(`/proizvodi/kupac/restoran/${restoranId}`);
    },
    searchProizvodiZaKupca: (naziv) => {
        const restoranId = localStorage.getItem('restoranId');
        if (!restoranId) return apiFetch(`/proizvodi/search?naziv=${encodeURIComponent(naziv)}`);
        return apiFetch(`/proizvodi/kupac/restoran/${restoranId}/search?naziv=${encodeURIComponent(naziv)}`);
    },
    getProizvodiZaKupcaByKategorija: (kategorijaId) => {
        const restoranId = localStorage.getItem('restoranId');
        if (!restoranId) return apiFetch(`/proizvodi/kategorija/${kategorijaId}`);
        return apiFetch(`/proizvodi/kupac/restoran/${restoranId}/kategorija/${kategorijaId}`);
    },

    // ── Kategorije ─────────────────────────────────────────
    // Menadzer - vidi sve
    getKategorije: () => apiFetch('/kategorije'),
    getKategorijeZaMenadzera: (menadzerId) => apiFetch(`/kategorije/menadzer`, {
        headers: { 'Content-Type': 'application/json', 'X-User-Id': menadzerId }
    }),

    // Kupac - samo iz aktivnih menija izabranog restorana
    getKategorijeZaKupca: () => {
        const restoranId = localStorage.getItem('restoranId');
        if (!restoranId) return apiFetch('/kategorije');
        return apiFetch(`/kategorije/kupac/restoran/${restoranId}`);
    },

    // ── Meniji ─────────────────────────────────────────────
    getMenijiZaMenadzera: (restoranId) => apiFetch(`/meni/restorani/${restoranId}`),
    getMenijiZaKupca: (restoranId) => apiFetch(`/meni/kupac/restorani/${restoranId}`),
    getMeni: (meniId) => apiFetch(`/meni/${meniId}`),
    getRestorani: (menadzerId) => apiFetch(`/meni/restorani?menadzerId=${menadzerId}`),

    // ── Stavke menija ──────────────────────────────────────
    getStavkeMenija: (meniId) => apiFetch(`/stavke-menija/meni/${meniId}`),
    getStavkeMenijaZaKupca: (meniId) => apiFetch(`/stavke-menija/kupac/meni/${meniId}`),
    getStavkeMenijaZaRestoranKupca: () => {
        const restoranId = localStorage.getItem('restoranId');
        if (!restoranId) return Promise.resolve([]);
        return apiFetch(`/stavke-menija/kupac/restoran/${restoranId}`);
    },

    // ── Omiljeni ───────────────────────────────────────────
    getOmiljeni: (kupacId) => apiFetch(`/omiljeni/${kupacId}`),
    dodajOmiljeni: (kupacId, proizvodId) =>
        apiFetch(`/omiljeni/${kupacId}/proizvodi/${proizvodId}`, { method: 'POST' }),
    ukloniOmiljeni: (kupacId, proizvodId) =>
        apiFetch(`/omiljeni/${kupacId}/proizvodi/${proizvodId}`, { method: 'DELETE' }),

    // ── Omiljene kategorije ────────────────────────────────
    getOmiljeneKategorije: (kupacId) => apiFetch(`/omiljene-kategorije/${kupacId}`),
    dodajOmiljenuKategoriju: (kupacId, kategorijaId) =>
        apiFetch(`/omiljene-kategorije/${kupacId}/kategorije/${kategorijaId}`, { method: 'POST' }),
    ukloniOmiljenuKategoriju: (kupacId, kategorijaId) =>
        apiFetch(`/omiljene-kategorije/${kupacId}/kategorije/${kategorijaId}`, { method: 'DELETE' }),

    // ── Porudžbine ─────────────────────────────────────────
    kreirajPorudzbinu: (payload) =>
        apiFetch('/porudzbine', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', 'X-User-Id': localStorage.getItem('korisnikId') },
            body: JSON.stringify(payload)
        }),
    previewPorudzbine: (payload) =>
        apiFetch('/porudzbine/preview', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', 'X-User-Id': localStorage.getItem('korisnikId') },
            body: JSON.stringify(payload)
        }),
    getPorudzbine: (kupacId) =>
        apiFetch(`/porudzbine?kupacId=${kupacId}`, {
            headers: { 'Content-Type': 'application/json', 'X-User-Id': localStorage.getItem('korisnikId') }
        }),
    getPorudzbineTrenutnogKorisnika: () =>
        apiFetch('/porudzbine', {
            headers: { 'Content-Type': 'application/json', 'X-User-Id': localStorage.getItem('korisnikId') }
        }),
    getIstorijaStatusaPorudzbine: (porudzbinaId) =>
        apiFetch(`/porudzbine/${porudzbinaId}/istorija-statusa`, {
            headers: { 'Content-Type': 'application/json', 'X-User-Id': localStorage.getItem('korisnikId') }
        }),

    // ── Preporuke ──────────────────────────────────────────
    getPorudzbineMenadzera: () =>
        api.getPorudzbineTrenutnogKorisnika(),
    promeniStatusPorudzbine: (porudzbinaId, noviStatus) =>
        apiFetch(`/porudzbine/${porudzbinaId}/status`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json', 'X-User-Id': localStorage.getItem('korisnikId') },
            body: JSON.stringify({ noviStatus })
        }),

    preuzmiPdfPorudzbine: async (porudzbinaId, tip = 'racun') => {
        const path = tip === 'storno' ? 'storno-racun' : 'racun';
        const res = await fetch(`${API_BASE}/porudzbine/${porudzbinaId}/${path}`, {
            headers: { 'X-User-Id': localStorage.getItem('korisnikId') }
        });
        if (!res.ok) {
            throw new Error('Preuzimanje PDF dokumenta nije uspelo.');
        }
        const blob = await res.blob();
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `${tip === 'storno' ? 'storno-racun' : 'racun'}-${porudzbinaId}.pdf`;
        document.body.appendChild(a);
        a.click();
        a.remove();
        URL.revokeObjectURL(url);
    },

    getPreporuke: (kupacId, limit = 10) =>
        apiFetch(`/preporuke/kupac/${kupacId}?limit=${limit}`),

    // ── Tracking ───────────────────────────────────────────
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
            body: JSON.stringify({ tekstUpita, tipPretrage: 'OPSTA' })
        }).catch(e => console.warn('Tracking pretraga greška:', e));
    },
};
