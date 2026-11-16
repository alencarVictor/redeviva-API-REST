package br.com.fiap.redeviva.controller;

import br.com.fiap.redeviva.bo.ConfirmacaoRetiradaBO;
import br.com.fiap.redeviva.bo.ItemReservaBO;
import br.com.fiap.redeviva.bo.ReservaBO;
import br.com.fiap.redeviva.dto.ConfirmacaoRetiradaDTO;
import br.com.fiap.redeviva.dto.ItemReservaDTO;
import br.com.fiap.redeviva.dto.ReservaDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController //dizendo para o Spring que essa classe funcionara como Controller (MVC)
@RequestMapping("/reservas") //definindo endpoint
public class ReservaController {

    private final ReservaBO reservaBO;
    private final ItemReservaBO itemReservaBO;
    private final ConfirmacaoRetiradaBO confirmacaoRetiradaBO;

    public ReservaController(ReservaBO reservaBO, ItemReservaBO itemReservaBO,
                             ConfirmacaoRetiradaBO confirmacaoRetiradaBO) {
        this.reservaBO = reservaBO;
        this.itemReservaBO = itemReservaBO;
        this.confirmacaoRetiradaBO = confirmacaoRetiradaBO;
    }

    @GetMapping //requisicao get
    public ResponseEntity<List<ReservaDTO>> findAll(){
        List<ReservaDTO> reservas = reservaBO.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(reservas);
        //configurando o retorno com status ok 200
    }

    @GetMapping("/{cdReserva}")
    public ResponseEntity<?> findByCodigo(@PathVariable Long cdReserva){
        ReservaDTO reserva = reservaBO.findByCodigo(cdReserva);
        return ResponseEntity.status(HttpStatus.OK).body(reserva);
        //configurando o retorno com status ok 200
    }

    @DeleteMapping("/{cdReserva}")
    public ResponseEntity<Void> delete(@PathVariable Long cdReserva){
        reservaBO.delete(cdReserva); //se nao achar, o BO lanca a exception e o handler devolve o 404
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        //configurando o retorno com status no content 204
    }

    @PutMapping("/{cdReserva}")
    public ResponseEntity<?> update(@PathVariable Long cdReserva,
                                    @RequestBody @Valid ReservaDTO reserva){
        ReservaDTO resultado = reservaBO.update(cdReserva, reserva);
        return ResponseEntity.status(HttpStatus.OK).body(resultado);
        //configurando o retorno com status ok 200
    }

    //retirada da reserva no balcao (grava a confirmacao e passa a reserva para RETIRADA)
    @PutMapping("/{cdReserva}/retirada")
    public ResponseEntity<?> retirada(@PathVariable Long cdReserva,
                                      @RequestBody @Valid ConfirmacaoRetiradaDTO confirmacao){
        ConfirmacaoRetiradaDTO resultado = confirmacaoRetiradaBO.registrarRetirada(cdReserva, confirmacao);
        return ResponseEntity.status(HttpStatus.OK).body(resultado);
        //configurando o retorno com status ok 200
    }

    //rotas aninhadas do relacionamento 1:N (reserva -> itens)

    @GetMapping("/{cdReserva}/itens")
    public ResponseEntity<List<ItemReservaDTO>> findItens(@PathVariable Long cdReserva){
        List<ItemReservaDTO> itens = itemReservaBO.findByReserva(cdReserva);
        return ResponseEntity.status(HttpStatus.OK).body(itens);
        //configurando o retorno com status ok 200
    }

    @PostMapping("/{cdReserva}/itens")
    public ResponseEntity<?> saveItem(@PathVariable Long cdReserva,
                                      @RequestBody @Valid ItemReservaDTO item){
        ItemReservaDTO resultado = itemReservaBO.save(cdReserva, item);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "/itens/" + resultado.getCdItem())
                .body(resultado);
        //configurando o retorno com status created 201 e o header Location
    }
}