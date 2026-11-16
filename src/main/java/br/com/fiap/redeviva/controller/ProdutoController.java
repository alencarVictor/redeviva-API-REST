package br.com.fiap.redeviva.controller;

import br.com.fiap.redeviva.bo.ProdutoBO;
import br.com.fiap.redeviva.dto.ProdutoDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoBO produtoBO;

    public ProdutoController(ProdutoBO produtoBO) {
        this.produtoBO = produtoBO;
    }

    @GetMapping //requisicao get
    public ResponseEntity<List<ProdutoDTO>> findAll() throws SQLException {
        List<ProdutoDTO> produtos = produtoBO.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(produtos);
        //configurando o retorno com status ok 200
    }

    @GetMapping("/{cdProduto}")
    public ResponseEntity<?> findByCodigo(@PathVariable Long cdProduto) throws SQLException {
        ProdutoDTO produto = produtoBO.findByCodigo(cdProduto);
        return ResponseEntity.status(HttpStatus.OK).body(produto);
        //configurando o retorno com status ok 200
    }

    @DeleteMapping("/{cdProduto}")
    public ResponseEntity<Void> delete(@PathVariable Long cdProduto) throws SQLException {
        produtoBO.delete(cdProduto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        //configurando o retorno com status no content 204
    }

    @PutMapping("/{cdProduto}")
    public ResponseEntity<?> update(@PathVariable Long cdProduto,
                                    @RequestBody @Valid ProdutoDTO produto) throws SQLException {
        ProdutoDTO resultado = produtoBO.update(cdProduto, produto);
        return ResponseEntity.status(HttpStatus.OK).body(resultado);
        //configurando o retorno com status ok 200
    }
}