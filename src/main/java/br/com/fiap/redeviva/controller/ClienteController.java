package br.com.fiap.redeviva.controller;

import br.com.fiap.redeviva.bo.ClienteBO;
import br.com.fiap.redeviva.bo.ReservaBO;
import br.com.fiap.redeviva.dto.ClienteDTO;
import br.com.fiap.redeviva.dto.ReservaDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController //dizendo para o Spring que essa classe funcionara como Controller (MVC)
@RequestMapping("/clientes") //definindo endpoint
public class ClienteController {

    private final ClienteBO clienteBO;
    private final ReservaBO reservaBO;

    public ClienteController(ClienteBO clienteBO, ReservaBO reservaBO) {
        this.clienteBO = clienteBO;
        this.reservaBO = reservaBO;
    }

    @GetMapping //requisicao get
    public ResponseEntity<List<ClienteDTO>> findAll(){
        List<ClienteDTO> clientes = clienteBO.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(clientes);
        //configurando o retorno com status ok 200
    }

    @GetMapping("/{cdCliente}")
    public ResponseEntity<?> findByCodigo(@PathVariable Long cdCliente){
        ClienteDTO cliente = clienteBO.findByCodigo(cdCliente);
        return ResponseEntity.status(HttpStatus.OK).body(cliente);
        //configurando o retorno com status ok 200
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody @Valid ClienteDTO cliente){
        ClienteDTO resultado = clienteBO.save(cliente);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "/clientes/" + resultado.getCdCliente())
                .body(resultado);
        //configurando o retorno com status created 201 e o header Location
    }

    @DeleteMapping("/{cdCliente}")
    public ResponseEntity<Void> delete(@PathVariable Long cdCliente){
        clienteBO.delete(cdCliente); //se nao achar, o BO lanca a exception e o handler devolve o 404
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        //configurando o retorno com status no content 204
    }

    @PutMapping("/{cdCliente}")
    public ResponseEntity<?> update(@PathVariable Long cdCliente,
                                    @RequestBody @Valid ClienteDTO cliente){
        ClienteDTO resultado = clienteBO.update(cdCliente, cliente);
        return ResponseEntity.status(HttpStatus.OK).body(resultado);
        //configurando o retorno com status ok 200
    }

    //validacao do cadastro (sem body: a acao ja esta na rota)
    @PutMapping("/{cdCliente}/validacao/{status}")
    public ResponseEntity<?> alterarValidacao(@PathVariable Long cdCliente,
                                              @PathVariable String status){
        ClienteDTO resultado = clienteBO.alterarValidacao(cdCliente, status);
        return ResponseEntity.status(HttpStatus.OK).body(resultado);
        //configurando o retorno com status ok 200
    }

    //rotas aninhadas do relacionamento 1:N (cliente -> reservas)

    @GetMapping("/{cdCliente}/reservas")
    public ResponseEntity<List<ReservaDTO>> findReservas(@PathVariable Long cdCliente){
        List<ReservaDTO> reservas = reservaBO.findByCliente(cdCliente);
        return ResponseEntity.status(HttpStatus.OK).body(reservas);
        //configurando o retorno com status ok 200
    }

    @PostMapping("/{cdCliente}/reservas")
    public ResponseEntity<?> saveReserva(@PathVariable Long cdCliente,
                                         @RequestBody @Valid ReservaDTO reserva){
        ReservaDTO resultado = reservaBO.save(cdCliente, reserva);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "/reservas/" + resultado.getCdReserva())
                .body(resultado);
        //configurando o retorno com status created 201 e o header Location
    }
}