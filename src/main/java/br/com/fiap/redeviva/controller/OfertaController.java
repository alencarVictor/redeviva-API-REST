package br.com.fiap.redeviva.controller;

import br.com.fiap.redeviva.bo.OfertaBO;
import br.com.fiap.redeviva.dto.OfertaDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController //dizendo para o Spring que essa classe funcionara como Controller (MVC)
@RequestMapping("/ofertas") //definindo endpoint
public class OfertaController {

    private final OfertaBO ofertaBO;

    public OfertaController(OfertaBO ofertaBO) {
        this.ofertaBO = ofertaBO;
    }

    @GetMapping //requisicao get
    public ResponseEntity<List<OfertaDTO>> findAll(){
        List<OfertaDTO> ofertas = ofertaBO.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(ofertas);
        //configurando o retorno com status ok 200
    }

    @GetMapping("/{cdOferta}")
    public ResponseEntity<?> findByCodigo(@PathVariable Long cdOferta){
        OfertaDTO oferta = ofertaBO.findByCodigo(cdOferta);
        return ResponseEntity.status(HttpStatus.OK).body(oferta);
        //configurando o retorno com status ok 200
    }

    @DeleteMapping("/{cdOferta}")
    public ResponseEntity<String> delete(@PathVariable Long cdOferta){
        ofertaBO.delete(cdOferta); //se nao achar, o BO lanca a exception e o handler devolve o 404
        return ResponseEntity.status(HttpStatus.OK).body("Oferta deletada com sucesso!");
        //configurando o retorno com status ok 200
    }

    @PutMapping("/{cdOferta}")
    public ResponseEntity<?> update(@PathVariable Long cdOferta,
                                    @RequestBody @Valid OfertaDTO oferta){
        OfertaDTO resultado = ofertaBO.update(cdOferta, oferta);
        return ResponseEntity.status(HttpStatus.OK).body(resultado);
        //configurando o retorno com status ok 200
    }
}