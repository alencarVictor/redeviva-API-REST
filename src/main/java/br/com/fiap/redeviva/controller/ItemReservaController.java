package br.com.fiap.redeviva.controller;

import br.com.fiap.redeviva.bo.ItemReservaBO;
import br.com.fiap.redeviva.dto.ItemReservaDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController //dizendo para o Spring que essa classe funcionara como Controller (MVC)
@RequestMapping("/itens") //definindo endpoint
public class ItemReservaController {

    private final ItemReservaBO itemReservaBO;

    public ItemReservaController(ItemReservaBO itemReservaBO) {
        this.itemReservaBO = itemReservaBO;
    }

    @GetMapping //requisicao get
    public ResponseEntity<List<ItemReservaDTO>> findAll(){
        List<ItemReservaDTO> itens = itemReservaBO.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(itens);
        //configurando o retorno com status ok 200
    }

    @GetMapping("/{cdItem}")
    public ResponseEntity<?> findByCodigo(@PathVariable Long cdItem){
        ItemReservaDTO item = itemReservaBO.findByCodigo(cdItem);
        return ResponseEntity.status(HttpStatus.OK).body(item);
        //configurando o retorno com status ok 200
    }

    @DeleteMapping("/{cdItem}")
    public ResponseEntity<Void> delete(@PathVariable Long cdItem){
        itemReservaBO.delete(cdItem); //se nao achar, o BO lanca a exception e o handler devolve o 404
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        //configurando o retorno com status no content 204
    }

    @PutMapping("/{cdItem}")
    public ResponseEntity<?> update(@PathVariable Long cdItem,
                                    @RequestBody @Valid ItemReservaDTO item){
        ItemReservaDTO resultado = itemReservaBO.update(cdItem, item);
        return ResponseEntity.status(HttpStatus.OK).body(resultado);
        //configurando o retorno com status ok 200
    }
}