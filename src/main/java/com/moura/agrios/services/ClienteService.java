package com.moura.agrios.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.moura.agrios.models.Cliente;
import com.moura.agrios.repositories.ClienteRepository;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Cliente cadastrar(Cliente cliente) {

        validarCliente(cliente);

        if (clienteRepository.existsByDocumento(cliente.getDocumento())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Já existe um cliente cadastrado com este documento");
        }

        if (cliente.getEmail() != null && !cliente.getEmail().isBlank()
                && clienteRepository.existsByEmail(cliente.getEmail())) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Já existe um cliente cadastrado com este email");
        }

        cliente.setAtivo(true);

        return clienteRepository.save(cliente);
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Cliente buscarPorId(Integer id) {

        return clienteRepository.findById(id)
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Cliente não encontrado"
                )
            );
    }

    private void validarCliente(Cliente cliente) {

        if (cliente.getTipoPessoa() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de pessoa é obrigatório");
        }

        if (cliente.getDocumento() == null || cliente.getDocumento().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Documento é obrigatório");
        }

        String documento = cliente.getDocumento().replaceAll("\\D", ""); //permite receber documento formatado ou não
        cliente.setDocumento(documento);

        if (cliente.getContato() != null) {
            cliente.setContato(cliente.getContato().replaceAll("\\D", ""));
        }

        switch (cliente.getTipoPessoa()) {
            case FISICA ->
                validarPessoaFisica(cliente);

            case JURIDICA ->
                validarPessoaJuridica(cliente);
        }
    }

    private void validarPessoaFisica(Cliente cliente) {

        if (cliente.getNome() == null || cliente.getNome().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome é obrigatório para pessoa física");
        }

        if (cliente.getDocumento().length() != 11) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF deve possuir 11 dígitos");
        }

        cliente.setRazaoSocial(null);
        cliente.setNomeFantasia(null);
    }

    private void validarPessoaJuridica(Cliente cliente) {

        if (cliente.getRazaoSocial() == null || cliente.getRazaoSocial().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Razão Social é obrigatória para pessoa jurídica");
        }

        if (cliente.getNomeFantasia() == null || cliente.getNomeFantasia().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome Fantasia é obrigatório para pessoa jurídica");
        }

        if (cliente.getDocumento().length() != 14) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CNPJ deve possuir 14 dígitos");
        }

        cliente.setNome(null);
        cliente.setDataNascimento(null);
    }
}