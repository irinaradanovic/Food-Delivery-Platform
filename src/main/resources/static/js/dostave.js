// Podaci koji 100% odgovaraju tvom SQL podsistemu za dostavu
const dostavePodaci = [
    {
        id: 1,
        porudzbinaId: 101,
        dostavljac: "Petar Petrović (ID: 1)",
        adresaPreuzimanja: "Bulevar Oslobođenja 10",
        adresaIsporuke: "Cara Dušana 25",
        eta: "25 min",
        status: "U_TRANSPORTU"
    },
    {
        id: 2,
        porudzbinaId: 102,
        dostavljac: "Milan Milić (ID: 2)",
        adresaPreuzimanja: "Futoška 15",
        adresaIsporuke: "Narodnog Fronta 44",
        eta: "35 min",
        status: "DODELJENA"
    }
];

document.addEventListener("DOMContentLoaded", () => {
    prikaziDostave(dostavePodaci);
});

function prikaziDostave(podaci) {
    const tableBody = document.getElementById("dostaveTable");
    tableBody.innerHTML = "";

    podaci.forEach(d => {
        // Logika za selekciju odgovarajuće klase za status
        let statusKlasa = "";
        let statusTekst = "";

        if (d.status === "U_TRANSPORTU") {
            statusKlasa = "status-transport";
            statusTekst = "🚚 U transportu";
        } else if (d.status === "DODELJENA") {
            statusKlasa = "status-dodeljena";
            statusTekst = "📌 Dodeljena";
        } else {
            statusKlasa = "status-isporucena";
            statusTekst = "✅ Isporučena";
        }

        const row = `
            <tr>
                <td><strong>#${d.id}</strong></td>
                <td>#${d.porudzbinaId}</td>
                <td>${d.dostavljac}</td>
                <td>
                    <div class="address-container">
                        <div><span class="address-label">Od:</span> <span class="address-text">${d.adresaPreuzimanja}</span></div>
                        <div><span class="address-label">Do:</span> <span class="address-text">${d.adresaIsporuke}</span></div>
                    </div>
                </td>
                <td><strong style="color: #34495e;">${d.eta}</strong></td>
                <td><span class="badge ${statusKlasa}">${statusTekst}</span></td>
            </tr>
        `;
        tableBody.innerHTML += row;
    });
}