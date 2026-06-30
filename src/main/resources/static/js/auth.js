function sacuvajSesiju(korisnik) {
    localStorage.setItem('korisnikId', korisnik.korisnikId);
    localStorage.setItem('korisnikIme', korisnik.ime);
    localStorage.setItem('korisnikEmail', korisnik.email);
    localStorage.setItem('uloga', korisnik.uloga);
}

function getSesija() {
    return {
        korisnikId: localStorage.getItem('korisnikId'),
        ime: localStorage.getItem('korisnikIme'),
        email: localStorage.getItem('korisnikEmail'),
        uloga: localStorage.getItem('uloga')
    };
}

function getKupacId() {
    return localStorage.getItem('korisnikId');
}

function jeGost() {
    return !localStorage.getItem('uloga');
}

function odjavi() {
    localStorage.clear();
    window.location.href = '/izbor-restorana.html';
}

function zastitiStranicu() {
    const uloga = localStorage.getItem('uloga');
    const trenutnaStranica = window.location.pathname;

    // Stranice koje zahtevaju prijavu (nije dozvoljeno gostima)
    const samoPrijavljeni = [
        'omiljeni',
        'checkout',
        'moje-porudzbine',
        'porudzbine',
        'dostavljac-porudzbine',
        'kuponi'
    ];

    if (!uloga) {
        // Gost — blokiraj samo omiljene i preporuke
        if (samoPrijavljeni.some(s => trenutnaStranica.includes(s))) {
            window.location.href = '/login.html?poruka=morate-biti-prijavljeni';
            return;
        }
        // Sve ostalo je dozvoljeno gostima — nastavi
        return;
    }

    if (uloga === 'ADMIN' && !trenutnaStranica.includes('kuponi')) {
        window.location.href = '/kuponi.html';
        return;
    }

    if (uloga !== 'ADMIN' && trenutnaStranica.includes('kuponi')) {
        window.location.href = '/login.html?poruka=morate-biti-prijavljeni';
        return;
    }

    // Menadzer ne sme na kupacke stranice
    if (uloga === 'MENADZER' && (
        trenutnaStranica.includes('kategorije') ||
        trenutnaStranica.includes('omiljeni') ||
        trenutnaStranica.includes('proizvodi') ||
        trenutnaStranica.includes('preporuke') ||
        trenutnaStranica.includes('checkout') ||
        trenutnaStranica.includes('moje-porudzbine')
    )) {
        window.location.href = '/izbor-restorana.html';
    }

    if (uloga === 'DOSTAVLJAC' && (
        trenutnaStranica.includes('kategorije') ||
        trenutnaStranica.includes('omiljeni') ||
        trenutnaStranica.includes('proizvodi') ||
        trenutnaStranica.includes('preporuke') ||
        trenutnaStranica.includes('checkout') ||
        trenutnaStranica.endsWith('/porudzbine.html')
    )) {
        window.location.href = '/dostavljac-porudzbine.html';
    }

    // Kupac ili Gost bez izabranog restorana
    if ((uloga === 'KUPAC' || !uloga) && !localStorage.getItem('restoranId')) {
        if (
            trenutnaStranica.includes('proizvodi') ||
            trenutnaStranica.includes('kategorije')
        ) {
            window.location.href = '/izbor-restorana.html';
        }
    }
}

function osveziNavigaciju() {
    const navElement = document.querySelector('nav');
    if (!navElement) return;

    const sesija = getSesija();
    const uloga = sesija.uloga;

    let linkoviHtml = '';

    if (uloga === 'ADMIN') {
        linkoviHtml = `
            <a href="/kuponi.html" id="nav-kuponi">Kuponi</a>
        `;
    } else if (uloga === 'MENADZER') {
        linkoviHtml = `
            <a href="/izbor-restorana.html" id="nav-restorani">Moji restorani</a>
            <a href="/porudzbine.html" id="nav-porudzbine">Porudzbine</a>
        `;
    } else if (uloga === 'DOSTAVLJAC') {
        linkoviHtml = `
            <a href="/dostavljac-porudzbine.html" id="nav-dostavljac-porudzbine">Porudzbine</a>
            <a href="/dostave.html" id="nav-dostave">Dostave</a>
            <a href="/mapa.html" id="nav-mapa">Mapa</a>
        `;
    } else {
        // KUPAC ili GOST
        linkoviHtml = `
            <a href="/izbor-restorana.html" id="nav-restorani">Restorani</a>
            <a href="/proizvodi.html" id="nav-proizvodi">Proizvodi</a>
            <a href="/kategorije.html" id="nav-kategorije">Kategorije</a>
            <a href="/preporuke.html" id="nav-preporuke">Preporuke</a>
            ${uloga === 'KUPAC' ? `
            <a href="/moje-porudzbine.html" id="nav-moje-porudzbine">Moje porudzbine</a>
            <a href="/omiljeni.html" id="nav-omiljeni">Omiljeni</a>
            ` : ''}
        `;
    }

    // Desni deo navigacije — razlika između gosta i prijavljenog korisnika
    let navDesnoHtml = '';
    if (!uloga) {
        // GOST — prikaži Login i Registracija
        navDesnoHtml = `
            ${!uloga || uloga === 'KUPAC' ? `
            <button class="nav-korpa" onclick="prikaziKorpu ? prikaziKorpu() : null">
                🛒 Korpa
                <span class="korpa-badge" id="korpa-badge"></span>
            </button>` : ''}
            <a href="/login.html" class="btn-login">Prijava</a>
            <a href="/registracija.html" class="btn-registracija">Registracija</a>
        `;
    } else {
        navDesnoHtml = `
            ${uloga === 'KUPAC' ? `
            <button class="nav-korpa" onclick="prikaziKorpu()">
                🛒 Korpa
                <span class="korpa-badge" id="korpa-badge"></span>
            </button>` : ''}
            <span class="nav-user" id="nav-user">${sesija.ime || ''}</span>
            <button class="btn-odjava" onclick="odjavi()">Odjavi se</button>
        `;
    }

    navElement.innerHTML = `
        <a href="${uloga === 'ADMIN' ? '/kuponi.html' : (uloga === 'MENADZER' ? '/izbor-restorana.html' : (uloga === 'DOSTAVLJAC' ? '/dostavljac-porudzbine.html' : '/proizvodi.html'))}" class="nav-logo">
            <div class="nav-logo-icon">
                <img src="/asset/logo.png" alt="Big Bite" style="width: 100%; height: 100%; object-fit: cover; border-radius: 8px;">
            </div>
            <span class="nav-logo-text">Big Bite</span>
        </a>
        <div class="nav-links">
            ${linkoviHtml}
        </div>
        <div class="nav-right">
            ${navDesnoHtml}
        </div>
    `;

    const trenutnaStranica = window.location.pathname;
    document.querySelectorAll('.nav-links a').forEach(link => {
        if (trenutnaStranica.includes(link.getAttribute('href').replace('/', ''))) {
            link.classList.add('active');
        }
    });
}

// Dodaj stilove za btn-login i btn-registracija ako ih nema
(function injectGostStyles() {
    if (document.getElementById('gost-nav-styles')) return;
    const style = document.createElement('style');
    style.id = 'gost-nav-styles';
    style.textContent = `
        .btn-login {
            text-decoration: none;
            background: none;
            border: 1.5px solid #e5e0d8;
            border-radius: 8px;
            padding: 7px 14px;
            font-size: 13px;
            font-weight: 500;
            color: #6b7280;
            cursor: pointer;
            transition: all 0.15s;
        }
        .btn-login:hover { border-color: #2d6a2d; color: #2d6a2d; }
        .btn-registracija {
            text-decoration: none;
            background: #2d6a2d;
            border: 1.5px solid #2d6a2d;
            border-radius: 8px;
            padding: 7px 14px;
            font-size: 13px;
            font-weight: 600;
            color: white;
            cursor: pointer;
            transition: all 0.15s;
        }
        .btn-registracija:hover { background: #3a8a3a; border-color: #3a8a3a; }
    `;
    document.head.appendChild(style);
})();

document.addEventListener('DOMContentLoaded', osveziNavigaciju);




