fetch("http://localhost:8080/api/notifikacije/1")
    .then(res => res.json())
    .then(data => {

        const ul = document.getElementById("notifikacije");

        data.forEach(n => {

            ul.innerHTML += `
                <li>
                    ${n.poruka}
                </li>
            `;
        });
    });