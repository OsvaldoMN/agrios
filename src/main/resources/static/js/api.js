/**
 * AgriOS - Camada de Integração com API REST e Gestão de Sessão JWT
 */
const Api = (function () {
  const TOKEN_KEY = "agrios_token";
  const USER_KEY = "agrios_user";

  function getToken() {
    return localStorage.getItem(TOKEN_KEY);
  }

  function getUser() {
    const raw = localStorage.getItem(USER_KEY);
    if (!raw) return null;
    try {
      const parsed = JSON.parse(raw);
      if (parsed && (!parsed.role || parsed.role === "USER")) {
        const token = getToken();
        if (token) {
          try {
            const payload = JSON.parse(atob(token.split(".")[1]));
            if (payload.role) {
              parsed.role = payload.role;
              localStorage.setItem(USER_KEY, JSON.stringify(parsed));
            }
          } catch (ignored) {}
        }
      }
      return parsed;
    } catch (e) {
      return null;
    }
  }

  function setSession(token, username, role) {
    if (!role && token) {
      try {
        const payload = JSON.parse(atob(token.split(".")[1]));
        if (payload.role) role = payload.role;
      } catch (ignored) {}
    }
    localStorage.setItem(TOKEN_KEY, token);
    localStorage.setItem(USER_KEY, JSON.stringify({ username, role: role || "USER" }));
    document.cookie = `agrios_token=${encodeURIComponent(token)}; path=/; max-age=7200; SameSite=Lax`;
  }

  function clearSession() {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    document.cookie = "agrios_token=; path=/; max-age=0; SameSite=Lax";
  }

  function isAuthenticated() {
    return !!getToken();
  }

  function requireAuth() {
    if (!isAuthenticated()) {
      clearSession();
      window.location.href = "/login";
      return;
    }
    // Garante que o cookie esteja sincronizado para requisições de página do browser
    const token = getToken();
    if (token && !document.cookie.includes("agrios_token=")) {
      document.cookie = `agrios_token=${encodeURIComponent(token)}; path=/; max-age=7200; SameSite=Lax`;
    }
  }

  function logout() {
    clearSession();
    window.location.href = "/login";
  }

  async function request(endpoint, options = {}) {
    const token = getToken();
    const headers = {
      "Content-Type": "application/json",
      "Accept": "application/json",
      ...(options.headers || {})
    };

    if (token) {
      headers["Authorization"] = `Bearer ${token}`;
    }

    const config = {
      ...options,
      headers
    };

    try {
      const response = await fetch(endpoint, config);

      // Tratamento de sessão expirada / não autorizada
      if (response.status === 401) {
        clearSession();
        if (window.location.pathname !== "/login") {
          window.location.href = "/login?expired=true";
        }
        throw new Error("Sessão expirada. Faça login novamente.");
      }

      if (response.status === 403) {
        throw new Error("Acesso negado. Seu perfil não tem permissão para esta ação.");
      }

      // Se não há conteúdo retornado (204 No Content)
      if (response.status === 204) {
        return null;
      }

      const text = await response.text();
      let data = null;
      if (text) {
        try {
          data = JSON.parse(text);
        } catch (e) {
          data = text;
        }
      }

      if (!response.ok) {
        let errorMsg = "Ocorreu um erro na requisição.";
        if (data) {
          if (typeof data === "string") {
            errorMsg = data;
          } else if (data.message) {
            errorMsg = data.message;
          } else if (data.error) {
            errorMsg = data.error;
          } else if (Array.isArray(data.errors)) {
            errorMsg = data.errors.map(err => err.defaultMessage || err).join("; ");
          }
        }
        throw new Error(errorMsg);
      }

      return data;
    } catch (error) {
      throw error;
    }
  }

  return {
    getToken,
    getUser,
    setSession,
    clearSession,
    isAuthenticated,
    requireAuth,
    logout,
    get: (url) => request(url, { method: "GET" }),
    post: (url, body) => request(url, { method: "POST", body: JSON.stringify(body) }),
    put: (url, body) => request(url, { method: "PUT", body: JSON.stringify(body) }),
    patch: (url, body) => request(url, { method: "PATCH", body: body ? JSON.stringify(body) : undefined }),
    delete: (url) => request(url, { method: "DELETE" })
  };
})();
