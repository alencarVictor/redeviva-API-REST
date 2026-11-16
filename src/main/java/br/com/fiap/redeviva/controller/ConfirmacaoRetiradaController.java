package br.com.fiap.redeviva.controller;

import br.com.fiap.redeviva.bo.ConfirmacaoRetiradaBO;
import br.com.fiap.redeviva.dto.ConfirmacaoRetiradaDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController //dizendo para o Spring que essa classe funcionara como Controller (MVC)
@RequestMapping("/confirmacoes") //definindo endpoint
public class ConfirmacaoRetiradaController {

    private final ConfirmacaoRetiradaBO confirmacaoRetiradaBO;

    public ConfirmacaoRetiradaController(ConfirmacaoRetiradaBO confirmacaoRetiradaBO) {
        this.confirmacaoRetiradaBO = confirmacaoRetiradaBO;
    }

    @GetMapping //requisicao get
    public ResponseEntity<List<ConfirmacaoRetiradaDTO>> findAll(){
        List<ConfirmacaoRetiradaDTO> confirmacoes = confirmacaoRetiradaBO.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(confirmacoes);
        //configurando o retorno com status ok 200
    }

    @GetMapping("/{cdConfirmacao}")
    public ResponseEntity<?> findByCodigo(@PathVariable Long cdConfirmacao){
        ConfirmacaoRetiradaDTO confirmacao = confirmacaoRetiradaBO.findByCodigo(cdConfirmacao);
        return ResponseEntity.status(HttpStatus.OK).body(confirmacao);
        //configurando o retorno com status ok 200
    }

    @DeleteMapping("/{cdConfirmacao}")
    public ResponseEntity<Void> delete(@PathVariable Long cdConfirmacao){
        confirmacaoRetiradaBO.delete(cdConfirmacao); //se nao achar, o BO lanca a exception e o handler devolve o 404
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        //configurando o retorno com status no content 204
    }

    @PutMapping("/{cdConfirmacao}")
    public ResponseEntity<?> update(@PathVariable Long cdConfirmacao,
                                    @RequestBody @Valid ConfirmacaoRetiradaDTO confirmacao){
        ConfirmacaoRetiradaDTO resultado = confirmacaoRetiradaBO.update(cdConfirmacao, confirmacao);
        return ResponseEntity.status(HttpStatus.OK).body(resultado);
        //configurando o retorno com status ok 200
    }
}