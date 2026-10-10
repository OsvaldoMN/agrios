/**
 * AgriOS - Funções Utilitárias, Layout e Componentes de Interface
 */
const App = (function () {

  // Inicialização Geral
  function init() {
    initSidebar();
    initUserProfile();
    highlightActiveNavLink();
  }

  // Sidebar responsiva
  function initSidebar() {
    const toggleBtn = document.getElementById("sidebarToggleBtn");
    const sidebar = document.getElementById("appSidebar");
    const backdrop = document.getElementById("sidebarBackdrop");

    if (toggleBtn && sidebar && backdrop) {
      toggleBtn.addEventListener("click", () => {
        sidebar.classList.toggle("open");
        backdrop.classList.toggle("active");
      });

      backdrop.addEventListener("click", () => {
        sidebar.classList.remove("open");
        backdrop.classList.remove("active");
      });
    }
  }

  // Identificação do Usuário no Cabeçalho
  function initUserProfile() {
    const user = Api.getUser();
    const nameEl = document.getElementById("headerUserName");
    const roleEl = document.getElementById("headerUserRole");
    const avatarEl = document.getElementById("headerUserAvatar");
    const logoutBtn = document.getElementById("headerLogoutBtn");

    if (user) {
      if (nameEl) nameEl.textContent = user.username;
      if (roleEl) roleEl.textContent = user.role || "USUÁRIO";
      if (avatarEl) {
        avatarEl.textContent = (user.username || "U").substring(0, 1).toUpperCase();
      }
    }

    if (logoutBtn) {
      logoutBtn.addEventListener("click", () => {
        App.confirm({
          title: "Sair do Sistema",
          message: "Deseja realmente encerrar sua sessão?",
          confirmText: "Sair",
          confirmClass: "btn-danger",
          onConfirm: () => Api.logout()
        });
      });
    }
  }

  // Destacar o menu ativo com base na rota atual
  function highlightActiveNavLink() {
    const currentPath = window.location.pathname;
    const links = document.querySelectorAll(".nav-link");

    links.forEach(link => {
      const href = link.getAttribute("href");
      if (!href) return;

      // Correspondência exata ou de prefixo
      if (href === currentPath || (href !== "/" && href !== "/dashboard" && currentPath.startsWith(href))) {
        link.classList.add("active");
      } else {
        link.classList.remove("active");
      }
    });
  }

  // Sistema de Notificações / Toasts
  function toast(message, type = "success", duration = 4000) {
    let container = document.getElementById("toastContainer");
    if (!container) {
      container = document.createElement("div");
      container.id = "toastContainer";
      container.className = "toast-container";
      document.body.appendChild(container);
    }

    const toastEl = document.createElement("div");
    toastEl.className = `toast toast-${type}`;

    let iconClass = "bi-check-circle-fill";
    if (type === "error") iconClass = "bi-exclamation-triangle-fill";
    if (type === "warning") iconClass = "bi-exclamation-circle-fill";
    if (type === "info") iconClass = "bi-info-circle-fill";

    toastEl.innerHTML = `
      <i class="bi ${iconClass} toast-icon"></i>
      <div class="toast-content">${message}</div>
      <button type="button" class="toast-close" aria-label="Fechar">&times;</button>
    `;

    const closeBtn = toastEl.querySelector(".toast-close");
    closeBtn.addEventListener("click", () => removeToast(toastEl));

    container.appendChild(toastEl);

    if (duration > 0) {
      setTimeout(() => {
        removeToast(toastEl);
      }, duration);
    }
  }

  function removeToast(toastEl) {
    if (toastEl && toastEl.parentNode) {
      toastEl.style.opacity = "0";
      toastEl.style.transform = "translateX(20px)";
      toastEl.style.transition = "all 0.2s ease";
      setTimeout(() => {
        if (toastEl.parentNode) toastEl.parentNode.removeChild(toastEl);
      }, 200);
    }
  }

  // Modal de Confirmação Reutilizável
  let confirmCallback = null;

  function confirm({ title, message, confirmText = "Confirmar", cancelText = "Cancelar", confirmClass = "btn-primary", onConfirm }) {
    const modal = document.getElementById("globalConfirmModal");
    if (!modal) {
      if (window.confirm(`${title}\n\n${message}`)) {
        if (onConfirm) onConfirm();
      }
      return;
    }

    const titleEl = document.getElementById("globalConfirmTitle");
    const msgEl = document.getElementById("globalConfirmMessage");
    const confirmBtn = document.getElementById("globalConfirmBtn");
    const cancelBtn = document.getElementById("globalCancelBtn");
    const closeBtn = document.getElementById("globalCloseBtn");

    if (titleEl) titleEl.textContent = title;
    if (msgEl) msgEl.textContent = message;

    if (confirmBtn) {
      confirmBtn.textContent = confirmText;
      confirmBtn.className = `btn ${confirmClass}`;
    }
    if (cancelBtn) cancelBtn.textContent = cancelText;

    confirmCallback = onConfirm;

    modal.classList.add("active");

    const closeModal = () => {
      modal.classList.remove("active");
      confirmCallback = null;
    };

    if (cancelBtn) cancelBtn.onclick = closeModal;
    if (closeBtn) closeBtn.onclick = closeModal;

    if (confirmBtn) {
      confirmBtn.onclick = () => {
        const cb = confirmCallback;
        closeModal();
        if (cb) cb();
      };
    }
  }

  // Formatadores
  function formatCurrency(val) {
    if (val === null || val === undefined || isNaN(val)) return "R$ 0,00";
    return Number(val).toLocaleString("pt-BR", { style: "currency", currency: "BRL" });
  }

  function formatDate(val) {
    if (!val) return "-";
    const parts = val.split("-");
    if (parts.length === 3) {
      return `${parts[2]}/${parts[1]}/${parts[0]}`;
    }
    return val;
  }

  function formatDateTime(val) {
    if (!val) return "-";
    try {
      const d = new Date(val);
      if (isNaN(d.getTime())) return val;
      return d.toLocaleDateString("pt-BR") + " " + d.toLocaleTimeString("pt-BR", { hour: "2-digit", minute: "2-digit" });
    } catch (e) {
      return val;
    }
  }

  function formatDocument(doc) {
    if (!doc) return "-";
    const clean = doc.replace(/\D/g, "");
    if (clean.length === 11) {
      return clean.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, "$1.$2.$3-$4");
    }
    if (clean.length === 14) {
      return clean.replace(/(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})/, "$1.$2.$3/$4-$5");
    }
    return doc;
  }

  function formatPhone(phone) {
    if (!phone) return "-";
    const clean = phone.replace(/\D/g, "");
    if (clean.length === 10) {
      return clean.replace(/(\d{2})(\d{4})(\d{4})/, "($1) $2-$3");
    }
    if (clean.length === 11) {
      return clean.replace(/(\d{2})(\d{5})(\d{4})/, "($1) $2-$3");
    }
    return phone;
  }

  function formatCep(cep) {
    if (!cep) return "-";
    const clean = cep.replace(/\D/g, "");
    if (clean.length === 8) {
      return clean.replace(/(\d{5})(\d{3})/, "$1-$2");
    }
    return cep;
  }

  function renderStatusBadge(status) {
    if (!status) return "";
    const s = String(status).toUpperCase();
    let label = s;
    let cls = "badge-inativo";

    switch (s) {
      case "ABERTA":
        cls = "badge-aberta";
        label = "Aberta";
        break;
      case "FINALIZADA":
        cls = "badge-finalizada";
        label = "Finalizada";
        break;
      case "CANCELADA":
        cls = "badge-cancelada";
        label = "Cancelada";
        break;
      case "PARCIAL":
        cls = "badge-parcial";
        label = "Parcial";
        break;
      case "ATRASADA":
        cls = "badge-atrasada";
        label = "Atrasada";
        break;
      case "QUITADA":
        cls = "badge-quitada";
        label = "Quitada";
        break;
      case "TRUE":
      case "ATIVO":
        cls = "badge-ativo";
        label = "Ativo";
        break;
      case "FALSE":
      case "INATIVO":
        cls = "badge-inativo";
        label = "Inativo";
        break;
    }

    return `<span class="badge ${cls}">${label}</span>`;
  }

  // Executa na inicialização do DOM
  document.addEventListener("DOMContentLoaded", init);

  return {
    init,
    toast,
    confirm,
    formatCurrency,
    formatDate,
    formatDateTime,
    formatDocument,
    formatPhone,
    formatCep,
    renderStatusBadge
  };
})();
