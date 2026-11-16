package br.com.fiap.redeviva.controller;

import br.com.fiap.redeviva.bo.ReservaBO;
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

    public ReservaController(ReservaBO reservaBO) {
        this.reservaBO = reservaBO;
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
        reservaBO.delete(cdReserva);
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
}