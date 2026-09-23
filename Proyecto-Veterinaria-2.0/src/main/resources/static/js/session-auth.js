(function () {
    var TOKEN_KEY = 'dogtor_token';
    var RETRY_KEY = 'dogtor_retry';

    function getToken() {
        return sessionStorage.getItem(TOKEN_KEY);
    }

    // Si esta pestaña tiene token pero la página se renderizó sin conocerlo
    // (navegación normal, refresh, URL escrita a mano), se vuelve a pedir la
    // misma URL adjuntando el header Authorization y se reemplaza el documento
    // completo con la respuesta ya autenticada. Devuelve true si se disparó
    // ese refetch, para que el resto de la inicialización no corra sobre un
    // documento que está a punto de ser reemplazado.
    function rehidratar() {
        var token = getToken();
        var yaAutenticado = document.body.dataset.rol;

        if (token && !yaAutenticado && !sessionStorage.getItem(RETRY_KEY)) {
            sessionStorage.setItem(RETRY_KEY, '1');
            fetch(location.href, { headers: { Authorization: 'Bearer ' + token } })
                .then(function (r) {
                    return r.text().then(function (html) { return { html: html, url: r.url }; });
                })
                .then(function (resultado) {
                    document.open();
                    document.write(resultado.html);
                    document.close();
                    if (resultado.url && resultado.url !== location.href) {
                        history.replaceState(null, '', resultado.url);
                    }
                });
            return true;
        }

        sessionStorage.removeItem(RETRY_KEY);
        if (!token && document.body.dataset.authShell === 'true') {
            location.href = '/login';
        }
        return false;
    }

    function rellenarTokenEnFormularios() {
        var token = getToken() || '';
        document.querySelectorAll('form input[name="_token"]').forEach(function (input) {
            input.value = token;
        });
    }

    function iniciarLogin() {
        var form = document.getElementById('login-form');
        if (!form) return;
        form.addEventListener('submit', function (evento) {
            evento.preventDefault();
            var errorDiv = document.getElementById('login-error');
            fetch('/login', { method: 'POST', body: new FormData(form) })
                .then(function (r) {
                    return r.json().then(function (data) { return { ok: r.ok, data: data }; });
                })
                .then(function (resultado) {
                    if (!resultado.ok) {
                        if (errorDiv) {
                            errorDiv.textContent = resultado.data.error || 'Credenciales inválidas';
                            errorDiv.classList.remove('hidden');
                        }
                        return;
                    }
                    sessionStorage.setItem(TOKEN_KEY, resultado.data.token);
                    location.href = resultado.data.redirectUrl;
                })
                .catch(function () {
                    if (errorDiv) {
                        errorDiv.textContent = 'No se pudo conectar con el servidor.';
                        errorDiv.classList.remove('hidden');
                    }
                });
        });
    }

    function iniciarLogout() {
        document.querySelectorAll('.js-logout').forEach(function (link) {
            link.addEventListener('click', function (evento) {
                evento.preventDefault();
                sessionStorage.removeItem(TOKEN_KEY);
                location.href = '/';
            });
        });
    }

    if (!rehidratar()) {
        rellenarTokenEnFormularios();
        iniciarLogin();
        iniciarLogout();
    }
})();
