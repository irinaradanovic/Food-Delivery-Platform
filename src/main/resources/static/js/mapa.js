const map = L.map('map').setView([45.2671, 19.8335], 12);

L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: 'OpenStreetMap'
}).addTo(map);

fetch("http://localhost:8080/api/dostavljaci")
    .then(res => res.json())
    .then(data => {

        data.forEach(d => {

            if(d.trenutnaLat && d.trenutnaLng) {

                L.marker([d.trenutnaLat, d.trenutnaLng])
                    .addTo(map)
                    .bindPopup(`
                        <b>${d.ime} ${d.prezime}</b><br>
                        Status: ${d.status}
                    `);
            }
        });
    });