fetch("http://localhost:8080/api/dostavljaci")
    .then(res => res.json())
    .then(data => {

        const table = document.getElementById("dostavljaciTable");

        data.forEach(d => {

            table.innerHTML += `
                <tr>
                    <td>${d.id}</td>
                    <td>${d.ime} ${d.prezime}</td>
                    <td>
                        <span class="status ${d.status}">
                            ${d.status}
                        </span>
                    </td>
                    <td>${d.prosecnaOcena}</td>
                    <td>${d.brojDostava}</td>
                </tr>
            `;
        });
    });