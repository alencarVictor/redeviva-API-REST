package br.com.fiap.redeviva.bo;

import br.com.fiap.redeviva.dao.ClienteDAO;
import br.com.fiap.redeviva.dto.ClienteDTO;
import br.com.fiap.redeviva.exception.RecursoNaoEncontradoException;
import br.com.fiap.redeviva.exception.RegraDeNegocioException;
import br.com.fiap.redeviva.model.Cliente;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ClienteBO {

    private final ClienteDAO clienteDAO;
    private final ModelMapper modelMapper = new ModelMapper();

    public ClienteBO(ClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
    }

    public List<ClienteDTO> findAll() {
        List<ClienteDTO> clientes = new ArrayList<>();
        for (Cliente cliente : clienteDAO.findAll()) {
            clientes.add(modelMapper.map(cliente, ClienteDTO.class)); //model -> dto
        }
        return clientes;
    }

    public ClienteDTO findByCodigo(Long cdCliente) {
        Cliente cliente = clienteDAO.findByCodigo(cdCliente);
        if (cliente == null) {
            throw new RecursoNaoEncontradoException("Cliente " + cdCliente + " não encontrado");
        }
        return modelMapper.map(cliente, ClienteDTO.class);
    }

    public ClienteDTO save(ClienteDTO dto) {
        Cliente cliente = modelMapper.map(dto, Cliente.class); //dto -> model
        cliente.setCdCliente(null); //o id vem da sequence, ignora o que chegar no JSON
        cliente.setStValidacao("PENDENTE"); //todo cliente novo comeca pendente
        cliente.setDtValidadeCadastro(null); //so ganha validade quando for validado
        return modelMapper.map(clienteDAO.save(cliente), ClienteDTO.class);
    }

    public ClienteDTO update(Long cdCliente, ClienteDTO dto) {
        Cliente existente = clienteDAO.findByCodigo(cdCliente);
        if (existente == null) {
            throw new RecursoNaoEncontradoException("Cliente " + cdCliente + " não encontrado");
        }
        Cliente cliente = modelMapper.map(dto, Cliente.class);
        cliente.setCdCliente(cdCliente); //o id vem da rota
        //o status e a validade nao mudam pelo PUT, so pela rota de validacao
        cliente.setStValidacao(existente.getStValidacao());
        cliente.setDtValidadeCadastro(existente.getDtValidadeCadastro());
        clienteDAO.update(cliente);
        return modelMapper.map(cliente, ClienteDTO.class);
    }

    public void delete(Long cdCliente) {
        if (!clienteDAO.delete(cdCliente)) {
            throw new RecursoNaoEncontradoException("Cliente " + cdCliente + " não encontrado");
        }
    }

    public ClienteDTO alterarValidacao(Long cdCliente, String status) {
        //regra: so existem dois destinos para o cadastro
        if (!status.equals("VALIDADO") && !status.equals("RECUSADO")) {
            throw new RegraDeNegocioException("Status inválido, use VALIDADO ou RECUSADO");
        }
        Cliente cliente = clienteDAO.findByCodigo(cdCliente);
        if (cliente == null) {
            throw new RecursoNaoEncontradoException("Cliente " + cdCliente + " não encontrado");
        }
        //regra: so da para validar ou recusar quem esta pendente
        if (!cliente.getStValidacao().equals("PENDENTE")) {
            throw new RegraDeNegocioException("Só é possível alterar a validação de um cliente com cadastro pendente");
        }
        cliente.setStValidacao(status);
        if (status.equals("VALIDADO")) {
            cliente.setDtValidadeCadastro(LocalDate.now().plusYears(1)); //cadastro vale por 1 ano
        } else {
            cliente.setDtValidadeCadastro(null); //cliente recusado fica sem validade
        }
        clienteDAO.atualizarValidacao(cliente);
        return modelMapper.map(cliente, ClienteDTO.class);
    }
}