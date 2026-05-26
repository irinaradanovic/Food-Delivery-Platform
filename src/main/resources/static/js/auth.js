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

function odjavi() {
    localStorage.clear();
    window.location.href = '/login.html';
}


function zastitiStranicu() {
    const uloga = localStorage.getItem('uloga');
    if (!uloga) {
        window.location.href = '/login.html';
        return;
    }

    const trenutnaStranica = window.location.pathname;
    if (uloga === 'MENADZER' && (trenutnaStranica.includes('kategorije') || trenutnaStranica.includes('omiljeni') || trenutnaStranica.includes('proizvodi'))) {
        window.location.href = '/izbor-restorana.html';
    }
}


function osveziNavigaciju() {
    const navElement = document.querySelector('nav');
    if (!navElement) return;

    const sesija = getSesija();
    const uloga = sesija.uloga || 'KUPAC';

    // Postavljanje imena korisnika
    const navUser = document.getElementById('nav-user');
    if (navUser) navUser.textContent = sesija.ime || '';

    let linkoviHtml = '';

    if (uloga === 'MENADZER') {
        linkoviHtml = `
            <a href="/izbor-restorana.html" id="nav-restorani">Moji restorani</a>
            <a href="/moji-meniji.html" id="nav-meniji">Moji meniji</a>
            <!--a href="/analitika.html" id="nav-analitika">Analitika</a> -->
            <!--a href="/profil.html" id="nav-profil">Profil</a> -->
        `;
    } else {
        linkoviHtml = `
            <a href="/proizvodi.html" id="nav-proizvodi">Proizvodi</a>
            <a href="/kategorije.html" id="nav-kategorije">Kategorije</a>
            <a href="/omiljeni.html" id="nav-omiljeni" class="fav">♥ Omiljeni</a>
        `;
    }

    navElement.innerHTML = `
        <a href="${uloga === 'MENADZER' ? '/izbor-restorana.html' : '/proizvodi.html'}" class="nav-logo">
            <div class="nav-logo-icon">
                <img src="/asset/logo.png" alt="Big Bite" style="width: 100%; height: 100%; object-fit: cover; border-radius: 8px;">
            </div>
            <span class="nav-logo-text">Big Bite</span>
        </a>
        <div class="nav-links">
            ${linkoviHtml}
        </div>
        <div class="nav-right">
            ${uloga !== 'MENADZER' ? `
            <button class="nav-korpa" onclick="prikaziKorpu()">
                🛒 Korpa
                <span class="korpa-badge" id="korpa-badge"></span>
            </button>` : ''}
            <span class="nav-user" id="nav-user">${sesija.ime || ''}</span>
            <button class="btn-odjava" onclick="odjavi()">Odjavi se</button>
        </div>
    `;

    const trenutnaStranica = window.location.pathname;
    document.querySelectorAll('.nav-links a').forEach(link => {
        if (trenutnaStranica.includes(link.getAttribute('href'))) {
            link.classList.add('active');
        }
    });
}

document.addEventListener('DOMContentLoaded', osveziNavigaciju);