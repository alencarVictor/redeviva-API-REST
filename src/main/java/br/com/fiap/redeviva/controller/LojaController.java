package br.com.fiap.redeviva.controller;

import br.com.fiap.redeviva.bo.LojaBO;
import br.com.fiap.redeviva.dto.LojaDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/lojas")
public class LojaController {

    private final LojaBO lojaBO;

    public LojaController(LojaBO lojaBO) {
        this.lojaBO = lojaBO;
    }

    @GetMapping //requisicao get
    public ResponseEntity<List<LojaDTO>> findAll() throws SQLException {
        List<LojaDTO> lojas = lojaBO.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(lojas);
        //configurando o retorno com status ok 200
    }

    @GetMapping("/{cdLoja}")
    public ResponseEntity<?> findByCodigo(@PathVariable Long cdLoja) throws SQLException {
        LojaDTO loja = lojaBO.findByCodigo(cdLoja);
        return ResponseEntity.status(HttpStatus.OK).body(loja);
        //configurando o retorno com status ok 200
        //se a loja nao existir, o BO lanca a exception e o handler devolve 404
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody @Valid LojaDTO loja) throws SQLException {
        LojaDTO resultado = lojaBO.save(loja);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{cdLoja}").buildAndExpand(resultado.getCdLoja()).toUri();
        return ResponseEntity.status(HttpStatus.CREATED).location(location).body(resultado);
        //configurando o retorno com status created 201 e o header Location
    }

    @DeleteMapping("/{cdLoja}")
    public ResponseEntity<Void> delete(@PathVariable Long cdLoja) throws SQLException {
        lojaBO.delete(cdLoja);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        //configurando o retorno com status no content 204
    }

    @PutMapping("/{cdLoja}")
    public ResponseEntity<?> update(@PathVariable Long cdLoja,
                                    @RequestBody @Valid LojaDTO loja) throws SQLException {
        LojaDTO resultado = lojaBO.update(cdLoja, loja);
        return ResponseEntity.status(HttpStatus.OK).body(resultado);
        //configurando o retorno com status ok 200
    }
}