package br.com.fiap.redeviva.controller;

import br.com.fiap.redeviva.bo.LojaBO;
import br.com.fiap.redeviva.bo.OfertaBO;
import br.com.fiap.redeviva.bo.ProdutoBO;
import br.com.fiap.redeviva.dto.LojaDTO;
import br.com.fiap.redeviva.dto.OfertaDTO;
import br.com.fiap.redeviva.dto.ProdutoDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController //dizendo para o Spring que essa classe funcionara como Controller (MVC)
@RequestMapping("/lojas") //definindo endpoint
public class LojaController {

    private final LojaBO lojaBO;
    private final ProdutoBO produtoBO;
    private final OfertaBO ofertaBO;

    public LojaController(LojaBO lojaBO, ProdutoBO produtoBO, OfertaBO ofertaBO) {
        this.lojaBO = lojaBO;
        this.produtoBO = produtoBO;
        this.ofertaBO = ofertaBO;
    }

    @GetMapping //requisicao get
    public ResponseEntity<List<LojaDTO>> findAll(){
        List<LojaDTO> lojas = lojaBO.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(lojas);
        //configurando o retorno com status ok 200
    }

    @GetMapping("/{cdLoja}")
    public ResponseEntity<?> findByCodigo(@PathVariable Long cdLoja){
        LojaDTO loja = lojaBO.findByCodigo(cdLoja);
        return ResponseEntity.status(HttpStatus.OK).body(loja);
        //configurando o retorno com status ok 200
        //se a loja nao existir, o BO lanca a exception e o handler devolve 404
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody @Valid LojaDTO loja){
        LojaDTO resultado = lojaBO.save(loja);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "/lojas/" + resultado.getCdLoja())
                .body(resultado);
        //configurando o retorno com status created 201 e o header Location
    }

    @DeleteMapping("/{cdLoja}")
    public ResponseEntity<Void> delete(@PathVariable Long cdLoja){
        lojaBO.delete(cdLoja); //se nao achar, o BO lanca a exception e o handler devolve o 404
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        //configurando o retorno com status no content 204
    }

    @PutMapping("/{cdLoja}")
    public ResponseEntity<?> update(@PathVariable Long cdLoja,
                                    @RequestBody @Valid LojaDTO loja){
        LojaDTO resultado = lojaBO.update(cdLoja, loja);
        return ResponseEntity.status(HttpStatus.OK).body(resultado);
        //configurando o retorno com status ok 200
    }

    //rotas aninhadas do relacionamento 1:N (loja -> produtos)

    @GetMapping("/{cdLoja}/produtos")
    public ResponseEntity<List<ProdutoDTO>> findProdutos(@PathVariable Long cdLoja){
        List<ProdutoDTO> produtos = produtoBO.findByLoja(cdLoja);
        return ResponseEntity.status(HttpStatus.OK).body(produtos);
        //configurando o retorno com status ok 200
    }

    @PostMapping("/{cdLoja}/produtos")
    public ResponseEntity<?> saveProduto(@PathVariable Long cdLoja,
                                         @RequestBody @Valid ProdutoDTO produto){
        ProdutoDTO resultado = produtoBO.save(cdLoja, produto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "/produtos/" + resultado.getCdProduto())
                .body(resultado);
        //configurando o retorno com status created 201 e o header Location
    }

    //rotas aninhadas do relacionamento 1:N (loja -> ofertas)

    @GetMapping("/{cdLoja}/ofertas")
    public ResponseEntity<List<OfertaDTO>> findOfertas(@PathVariable Long cdLoja){
        List<OfertaDTO> ofertas = ofertaBO.findByLoja(cdLoja);
        return ResponseEntity.status(HttpStatus.OK).body(ofertas);
        //configurando o retorno com status ok 200
    }

    @PostMapping("/{cdLoja}/ofertas")
    public ResponseEntity<?> saveOferta(@PathVariable Long cdLoja,
                                        @RequestBody @Valid OfertaDTO oferta){
        OfertaDTO resultado = ofertaBO.save(cdLoja, oferta);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "/ofertas/" + resultado.getCdOferta())
                .body(resultado);
        //configurando o retorno com status created 201 e o header Location
    }
}