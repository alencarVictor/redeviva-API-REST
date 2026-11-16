package br.com.fiap.redeviva.bo;

import br.com.fiap.redeviva.dao.ItemReservaDAO;
import br.com.fiap.redeviva.dao.OfertaDAO;
import br.com.fiap.redeviva.dao.ReservaDAO;
import br.com.fiap.redeviva.dto.ItemReservaDTO;
import br.com.fiap.redeviva.exception.RecursoNaoEncontradoException;
import br.com.fiap.redeviva.exception.RegraDeNegocioException;
import br.com.fiap.redeviva.model.ItemReserva;
import br.com.fiap.redeviva.model.Oferta;
import br.com.fiap.redeviva.model.Reserva;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ItemReservaBO {

    private final ItemReservaDAO itemReservaDAO;
    private final ReservaDAO reservaDAO;
    private final OfertaDAO ofertaDAO;
    private final ModelMapper modelMapper = new ModelMapper();

    public ItemReservaBO(ItemReservaDAO itemReservaDAO, ReservaDAO reservaDAO, OfertaDAO ofertaDAO) {
        this.itemReservaDAO = itemReservaDAO;
        this.reservaDAO = reservaDAO;
        this.ofertaDAO = ofertaDAO;
    }

    public List<ItemReservaDTO> findAll() {
        List<ItemReservaDTO> itens = new ArrayList<>();
        for (ItemReserva item : itemReservaDAO.findAll()) {
            itens.add(modelMapper.map(item, ItemReservaDTO.class)); //model -> dto
        }
        return itens;
    }

    public List<ItemReservaDTO> findByReserva(Long cdReserva) {
        buscarReserva(cdReserva);
        List<ItemReservaDTO> itens = new ArrayList<>();
        for (ItemReserva item : itemReservaDAO.findByReserva(cdReserva)) {
            itens.add(modelMapper.map(item, ItemReservaDTO.class));
        }
        return itens;
    }

    public ItemReservaDTO findByCodigo(Long cdItem) {
        return modelMapper.map(buscarItem(cdItem), ItemReservaDTO.class);
    }

    public ItemReservaDTO save(Long cdReserva, ItemReservaDTO dto) {
        Reserva reserva = buscarReserva(cdReserva);
        //regra: so da para mexer nos itens de uma reserva ativa
        validarReservaAtiva(reserva);
        Oferta oferta = ofertaDAO.findByCodigo(dto.getCdOferta());
        if (oferta == null) {
            throw new RecursoNaoEncontradoException("Oferta " + dto.getCdOferta() + " não encontrada");
        }
        //regra: a reserva so pode ter ofertas da mesma loja
        if (!oferta.getCdLoja().equals(reserva.getCdLoja())) {
            throw new RegraDeNegocioException("A oferta não pertence à loja desta reserva");
        }
        //regra: a oferta precisa estar ativa e dentro da validade
        if (!oferta.getStOferta().equals("ATIVA") || oferta.getDtValidade().isBefore(LocalDate.now())) {
            throw new RegraDeNegocioException("Oferta indisponível para reserva");
        }
        ItemReserva item = modelMapper.map(dto, ItemReserva.class); //dto -> model
        item.setCdItem(null); //o id vem da sequence, ignora o que chegar no JSON
        item.setCdReserva(cdReserva); //a reserva vem da rota
        item.setVlUnitario(oferta.getVlPrecoRede()); //o preco do item e o preco da rede da oferta
        //o DAO abate o estoque e grava o item juntos, e devolve null se nao tiver estoque
        if (itemReservaDAO.save(item) == null) {
            throw new RegraDeNegocioException("Estoque insuficiente para a oferta " + dto.getCdOferta());
        }
        return modelMapper.map(item, ItemReservaDTO.class);
    }

    public ItemReservaDTO update(Long cdItem, ItemReservaDTO dto) {
        ItemReserva item = buscarItem(cdItem);
        validarReservaAtiva(buscarReserva(item.getCdReserva()));
        int diferenca = dto.getQtItem() - item.getQtItem(); //quanto muda no estoque
        item.setQtItem(dto.getQtItem()); //so a quantidade muda
        if (itemReservaDAO.update(item, diferenca) == null) {
            throw new RegraDeNegocioException("Estoque insuficiente para a oferta " + item.getCdOferta());
        }
        return modelMapper.map(item, ItemReservaDTO.class);
    }

    public void delete(Long cdItem) {
        ItemReserva item = buscarItem(cdItem);
        validarReservaAtiva(buscarReserva(item.getCdReserva()));
        if (!itemReservaDAO.delete(item)) {
            throw new RecursoNaoEncontradoException("Item " + cdItem + " não encontrado");
        }
    }

    private Reserva buscarReserva(Long cdReserva) {
        Reserva reserva = reservaDAO.findByCodigo(cdReserva);
        if (reserva == null) {
            throw new RecursoNaoEncontradoException("Reserva " + cdReserva + " não encontrada");
        }
        return reserva;
    }

    private ItemReserva buscarItem(Long cdItem) {
        ItemReserva item = itemReservaDAO.findByCodigo(cdItem);
        if (item == null) {
            throw new RecursoNaoEncontradoException("Item " + cdItem + " não encontrado");
        }
        return item;
    }

    //regra: so reserva ativa aceita mudanca nos itens
    private void validarReservaAtiva(Reserva reserva) {
        if (!reserva.getStReserva().equals("ATIVA")) {
            throw new RegraDeNegocioException("Só é possível alterar itens de uma reserva ativa");
        }
    }
}