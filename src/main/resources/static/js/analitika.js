fetch("http://localhost:8080/api/dostavljaci")
    .then(res => res.json())
    .then(data => {

        const labels = data.map(d => d.ime);

        const values = data.map(d => d.brojDostava);

        new Chart(document.getElementById("chart"), {

            type: 'bar',

            data: {

                labels: labels,

                datasets: [{
                    label: 'Broj dostava',
                    data: values
                }]
            }
        });
    });