package com.moura.agrios.controllers;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class WebViewController {

    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    // --- CLIENTES & FAZENDAS ---
    @GetMapping(value = "/clientes", produces = MediaType.TEXT_HTML_VALUE)
    public String clientes() {
        return "clientes/index";
    }

    @GetMapping("/clientes/novo")
    public String novoCliente(Model model) {
        model.addAttribute("modo", "novo");
        return "clientes/form";
    }

    @GetMapping("/clientes/{id}/editar")
    public String editarCliente(@PathVariable Integer id, Model model) {
        model.addAttribute("modo", "editar");
        model.addAttribute("clienteId", id);
        return "clientes/form";
    }

    @GetMapping("/clientes/{id}/detalhes")
    public String detalhesCliente(@PathVariable Integer id, Model model) {
        model.addAttribute("clienteId", id);
        return "clientes/detalhes";
    }

    @GetMapping("/clientes/{clienteId}/fazendas/novo")
    public String novaFazenda(@PathVariable Integer clienteId, Model model) {
        model.addAttribute("modo", "novo");
        model.addAttribute("clienteId", clienteId);
        return "clientes/fazenda-form";
    }

    @GetMapping("/clientes/{clienteId}/fazendas/{fazendaId}/editar")
    public String editarFazenda(@PathVariable Integer clienteId, @PathVariable Integer fazendaId, Model model) {
        model.addAttribute("modo", "editar");
        model.addAttribute("clienteId", clienteId);
        model.addAttribute("fazendaId", fazendaId);
        return "clientes/fazenda-form";
    }

    // --- SERVIÇOS ---
    @GetMapping(value = "/servicos", produces = MediaType.TEXT_HTML_VALUE)
    public String servicos() {
        return "servicos/index";
    }

    @GetMapping("/servicos/novo")
    public String novoServico(Model model) {
        model.addAttribute("modo", "novo");
        return "servicos/form";
    }

    @GetMapping("/servicos/{id}/editar")
    public String editarServico(@PathVariable Integer id, Model model) {
        model.addAttribute("modo", "editar");
        model.addAttribute("servicoId", id);
        return "servicos/form";
    }

    // --- PRODUTOS ---
    @GetMapping(value = "/produtos", produces = MediaType.TEXT_HTML_VALUE)
    public String produtos() {
        return "produtos/index";
    }

    @GetMapping("/produtos/novo")
    public String novoProduto(Model model) {
        model.addAttribute("modo", "novo");
        return "produtos/form";
    }

    @GetMapping("/produtos/{id}/editar")
    public String editarProduto(@PathVariable Integer id, Model model) {
        model.addAttribute("modo", "editar");
        model.addAttribute("produtoId", id);
        return "produtos/form";
    }

    // --- MÁQUINAS ---
    @GetMapping(value = "/maquinas", produces = MediaType.TEXT_HTML_VALUE)
    public String maquinas() {
        return "maquinas/index";
    }

    @GetMapping("/maquinas/novo")
    public String novaMaquina(Model model) {
        model.addAttribute("modo", "novo");
        return "maquinas/form";
    }

    @GetMapping("/maquinas/{id}/editar")
    public String editarMaquina(@PathVariable Integer id, Model model) {
        model.addAttribute("modo", "editar");
        model.addAttribute("maquinaId", id);
        return "maquinas/form";
    }

    // --- ORDENS DE SERVIÇO ---
    @GetMapping(value = "/ordens-servico", produces = MediaType.TEXT_HTML_VALUE)
    public String ordensServico() {
        return "ordens-servico/index";
    }

    @GetMapping("/ordens-servico/nova")
    public String novaOrdemServico(Model model) {
        model.addAttribute("modo", "novo");
        return "ordens-servico/form";
    }

    @GetMapping("/ordens-servico/{id}/editar")
    public String editarOrdemServico(@PathVariable Integer id, Model model) {
        model.addAttribute("modo", "editar");
        model.addAttribute("osId", id);
        return "ordens-servico/form";
    }

    @GetMapping("/ordens-servico/{id}/detalhes")
    public String detalhesOrdemServico(@PathVariable Integer id, Model model) {
        model.addAttribute("osId", id);
        return "ordens-servico/detalhes";
    }

    // --- CONTAS A RECEBER ---
    @GetMapping(value = "/contas-receber", produces = MediaType.TEXT_HTML_VALUE)
    public String contasReceber() {
        return "contas-receber/index";
    }

    @GetMapping("/contas-receber/nova")
    public String novaContaReceber(Model model) {
        return "contas-receber/form";
    }

    @GetMapping("/contas-receber/{id}/detalhes")
    public String detalhesContaReceber(@PathVariable Integer id, Model model) {
        model.addAttribute("contaId", id);
        return "contas-receber/detalhes";
    }

    // --- USUÁRIOS ---
    @GetMapping("/usuarios/novo")
    public String novoUsuario() {
        return "usuarios/form";
    }
}
