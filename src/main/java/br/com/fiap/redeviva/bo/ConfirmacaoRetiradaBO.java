package br.com.fiap.redeviva.bo;

import br.com.fiap.redeviva.dao.ConfirmacaoRetiradaDAO;
import br.com.fiap.redeviva.dao.ItemReservaDAO;
import br.com.fiap.redeviva.dao.OfertaDAO;
import br.com.fiap.redeviva.dao.ReservaDAO;
import br.com.fiap.redeviva.dto.ConfirmacaoRetiradaDTO;
import br.com.fiap.redeviva.exception.RecursoNaoEncontradoException;
import br.com.fiap.redeviva.exception.RegraDeNegocioException;
import br.com.fiap.redeviva.model.ConfirmacaoRetirada;
import br.com.fiap.redeviva.model.ItemReserva;
import br.com.fiap.redeviva.model.Oferta;
import br.com.fiap.redeviva.model.Reserva;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ConfirmacaoRetiradaBO {

    private final ConfirmacaoRetiradaDAO confirmacaoRetiradaDAO;
    private final ReservaDAO reservaDAO;
    private final ItemReservaDAO itemReservaDAO;
    private final OfertaDAO ofertaDAO;
    private final ModelMapper modelMapper = new ModelMapper();

    public ConfirmacaoRetiradaBO(ConfirmacaoRetiradaDAO confirmacaoRetiradaDAO, ReservaDAO reservaDAO,
                                 ItemReservaDAO itemReservaDAO, OfertaDAO ofertaDAO) {
        this.confirmacaoRetiradaDAO = confirmacaoRetiradaDAO;
        this.reservaDAO = reservaDAO;
        this.itemReservaDAO = itemReservaDAO;
        this.ofertaDAO = ofertaDAO;
    }

    public List<ConfirmacaoRetiradaDTO> findAll() {
        List<ConfirmacaoRetiradaDTO> confirmacoes = new ArrayList<>();
        for (ConfirmacaoRetirada confirmacao : confirmacaoRetiradaDAO.findAll()) {
            confirmacoes.add(modelMapper.map(confirmacao, ConfirmacaoRetiradaDTO.class)); //model -> dto
        }
        return confirmacoes;
    }

    public ConfirmacaoRetiradaDTO findByCodigo(Long cdConfirmacao) {
        return modelMapper.map(buscarConfirmacao(cdConfirmacao), ConfirmacaoRetiradaDTO.class);
    }

    //retirada da reserva: grava a confirmacao e passa a reserva para RETIRADA
    public ConfirmacaoRetiradaDTO registrarRetirada(Long cdReserva, ConfirmacaoRetiradaDTO dto) {
        Reserva reserva = reservaDAO.findByCodigo(cdReserva);
        if (reserva == null) {
            throw new RecursoNaoEncontradoException("Reserva " + cdReserva + " não encontrada");
        }
        //regra: so retira reserva ativa
        if (!reserva.getStReserva().equals("ATIVA")) {
            throw new RegraDeNegocioException("Só é possível retirar uma reserva ativa");
        }
        //regra: a retirada precisa ser dentro das 24 horas da reserva
        LocalDateTime agora = LocalDateTime.now().withNano(0);
        if (reserva.getDtExpiracao().isBefore(agora)) {
            throw new RegraDeNegocioException("A reserva expirou, o prazo de 24 horas já passou");
        }
        //regra: reserva sem itens nao tem o que retirar
        List<ItemReserva> itens = itemReservaDAO.findByReserva(cdReserva);
        if (itens.isEmpty()) {
            throw new RegraDeNegocioException("A reserva não tem itens para retirar");
        }
        //desconto = quanto o cliente economizou (preco do sistema - preco da rede, vezes a quantidade)
        BigDecimal desconto = BigDecimal.ZERO;
        for (ItemReserva item : itens) {
            Oferta oferta = ofertaDAO.findByCodigo(item.getCdOferta());
            BigDecimal economia = oferta.getVlPrecoSistema().subtract(item.getVlUnitario())
                    .multiply(BigDecimal.valueOf(item.getQtItem()));
            desconto = desconto.add(economia);
        }
        if (desconto.compareTo(BigDecimal.ZERO) < 0) {
            desconto = BigDecimal.ZERO; //o desconto nunca fica negativo
        }
        //regra: o desconto precisa ser menor que o total do caixa
        if (desconto.compareTo(dto.getVlTotalCaixa()) >= 0) {
            throw new RegraDeNegocioException("O desconto aplicado deve ser menor que o total do caixa");
        }
        ConfirmacaoRetirada confirmacao = modelMapper.map(dto, ConfirmacaoRetirada.class); //dto -> model
        confirmacao.setCdConfirmacao(null); //o id vem da sequence, ignora o que chegar no JSON
        confirmacao.setCdReserva(cdReserva); //a reserva vem da rota
        confirmacao.setDtRetirada(agora);
        confirmacao.setVlDescontoAplicado(desconto);
        //o DAO muda a reserva e grava a confirmacao juntos, e devolve null se a reserva deixou de estar ativa
        if (confirmacaoRetiradaDAO.save(confirmacao) == null) {
            throw new RegraDeNegocioException("Só é possível retirar uma reserva ativa");
        }
        return modelMapper.map(confirmacao, ConfirmacaoRetiradaDTO.class);
    }

    public ConfirmacaoRetiradaDTO update(Long cdConfirmacao, ConfirmacaoRetiradaDTO dto) {
        ConfirmacaoRetirada confirmacao = buscarConfirmacao(cdConfirmacao);
        //regra: o desconto ja aplicado precisa continuar menor que o total do caixa
        if (confirmacao.getVlDescontoAplicado().compareTo(dto.getVlTotalCaixa()) >= 0) {
            throw new RegraDeNegocioException("O desconto aplicado deve ser menor que o total do caixa");
        }
        confirmacao.setNmAtendente(dto.getNmAtendente()); //so o atendente e o total mudam
        confirmacao.setVlTotalCaixa(dto.getVlTotalCaixa());
        confirmacaoRetiradaDAO.update(confirmacao);
        return modelMapper.map(confirmacao, ConfirmacaoRetiradaDTO.class);
    }

    public void delete(Long cdConfirmacao) {
        ConfirmacaoRetirada confirmacao = buscarConfirmacao(cdConfirmacao);
        if (!confirmacaoRetiradaDAO.delete(confirmacao)) {
            throw new RecursoNaoEncontradoException("Confirmação " + cdConfirmacao + " não encontrada");
        }
    }

    private ConfirmacaoRetirada buscarConfirmacao(Long cdConfirmacao) {
        ConfirmacaoRetirada confirmacao = confirmacaoRetiradaDAO.findByCodigo(cdConfirmacao);
        if (confirmacao == null) {
            throw new RecursoNaoEncontradoException("Confirmação " + cdConfirmacao + " não encontrada");
        }
        return confirmacao;
    }
}