function sacuvajSesiju(kupac) {
    localStorage.setItem('kupacId', kupac.korisnikId);
    localStorage.setItem('kupacIme', kupac.ime);
    localStorage.setItem('kupacEmail', kupac.email);
}

function getSesija() {
    return {
        kupacId: localStorage.getItem('kupacId'),
        ime: localStorage.getItem('kupacIme'),
        email: localStorage.getItem('kupacEmail'),
    };
}

function odjavi() {
    localStorage.clear();
    window.location.href = '/login.html';
}

function zastitiStranicu() {
    if (!localStorage.getItem('kupacId')) {
        window.location.href = '/login.html';
    }
}