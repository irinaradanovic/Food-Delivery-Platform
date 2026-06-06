const map = L.map('map').setView([45.2671, 19.8335], 12);

L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: 'OpenStreetMap'
}).addTo(map);

const dostavljaci = [];

function generisiCilj() {

    return {
        lat: 45.20 + Math.random() * 0.12,
        lng: 19.75 + Math.random() * 0.15
    };
}

fetch("http://localhost:8080/api/dostavljaci")
    .then(res => res.json())
    .then(data => {

        data.forEach(d => {

            if (!d.trenutnaLat || !d.trenutnaLng) return;

            const marker = L.marker([
                d.trenutnaLat,
                d.trenutnaLng
            ]).addTo(map);

            marker.bindPopup(`
                <b>${d.ime} ${d.prezime}</b><br>
                Status: ${d.status}
            `);

            dostavljaci.push({
                marker: marker,
                ime: d.ime,
                prezime: d.prezime,
                status: d.status,

                lat: d.trenutnaLat,
                lng: d.trenutnaLng,

                cilj: generisiCilj()
            });
        });

        pokreniSimulaciju();
    });

function pokreniSimulaciju() {

    setInterval(() => {

        dostavljaci.forEach(d => {

            const dx = d.cilj.lat - d.lat;
            const dy = d.cilj.lng - d.lng;

            const udaljenost = Math.sqrt(dx * dx + dy * dy);

            if (udaljenost < 0.001) {

                d.cilj = generisiCilj();

                return;
            }

            const korak = 0.00005;

            d.lat += (dx / udaljenost) * korak;
            d.lng += (dy / udaljenost) * korak;

            d.marker.setLatLng([
                d.lat,
                d.lng
            ]);
        });

    }, 100);
}