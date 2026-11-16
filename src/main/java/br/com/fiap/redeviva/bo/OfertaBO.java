package br.com.fiap.redeviva.bo;

import br.com.fiap.redeviva.dao.LojaDAO;
import br.com.fiap.redeviva.dao.OfertaDAO;
import br.com.fiap.redeviva.dao.ProdutoDAO;
import br.com.fiap.redeviva.dto.OfertaDTO;
import br.com.fiap.redeviva.exception.RecursoNaoEncontradoException;
import br.com.fiap.redeviva.exception.RegraDeNegocioException;
import br.com.fiap.redeviva.model.Oferta;
import br.com.fiap.redeviva.model.Produto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OfertaBO {

    private final OfertaDAO ofertaDAO;
    private final LojaDAO lojaDAO;
    private final ProdutoDAO produtoDAO;
    private final ModelMapper modelMapper = new ModelMapper();

    public OfertaBO(OfertaDAO ofertaDAO, LojaDAO lojaDAO, ProdutoDAO produtoDAO) {
        this.ofertaDAO = ofertaDAO;
        this.lojaDAO = lojaDAO;
        this.produtoDAO = produtoDAO;
    }

    public List<OfertaDTO> findAll() {
        List<OfertaDTO> ofertas = new ArrayList<>();
        for (Oferta oferta : ofertaDAO.findAll()) {
            ofertas.add(modelMapper.map(oferta, OfertaDTO.class)); //model -> dto
        }
        return ofertas;
    }

    public List<OfertaDTO> findByLoja(Long cdLoja) {
        confirmarLoja(cdLoja);
        List<OfertaDTO> ofertas = new ArrayList<>();
        for (Oferta oferta : ofertaDAO.findByLoja(cdLoja)) {
            ofertas.add(modelMapper.map(oferta, OfertaDTO.class));
        }
        return ofertas;
    }

    public OfertaDTO findByCodigo(Long cdOferta) {
        Oferta oferta = ofertaDAO.findByCodigo(cdOferta);
        if (oferta == null) {
            throw new RecursoNaoEncontradoException("Oferta " + cdOferta + " não encontrada");
        }
        return modelMapper.map(oferta, OfertaDTO.class);
    }

    public OfertaDTO save(Long cdLoja, OfertaDTO dto) {
        confirmarLoja(cdLoja);
        validarOferta(cdLoja, dto);
        Oferta oferta = modelMapper.map(dto, Oferta.class); //dto -> model
        oferta.setCdOferta(null); //o id vem da sequence, ignora o que chegar no JSON
        oferta.setCdLoja(cdLoja); //a loja vem da rota
        oferta.setStOferta("ATIVA"); //toda oferta nova comeca ativa
        return modelMapper.map(ofertaDAO.save(oferta), OfertaDTO.class);
    }

    public OfertaDTO update(Long cdOferta, OfertaDTO dto) {
        Oferta existente = ofertaDAO.findByCodigo(cdOferta);
        if (existente == null) {
            throw new RecursoNaoEncontradoException("Oferta " + cdOferta + " não encontrada");
        }
        validarOferta(existente.getCdLoja(), dto);
        Oferta oferta = modelMapper.map(dto, Oferta.class);
        oferta.setCdOferta(cdOferta); //o id vem da rota
        oferta.setCdLoja(existente.getCdLoja()); //a oferta continua na mesma loja
        //o status e controlado pelo sistema, so volta para ativa se ganhou estoque
        if (existente.getStOferta().equals("ESGOTADA")) {
            oferta.setStOferta("ATIVA");
        } else {
            oferta.setStOferta(existente.getStOferta());
        }
        ofertaDAO.update(oferta);
        return modelMapper.map(oferta, OfertaDTO.class);
    }

    public void delete(Long cdOferta) {
        if (!ofertaDAO.delete(cdOferta)) {
            throw new RecursoNaoEncontradoException("Oferta " + cdOferta + " não encontrada");
        }
    }

    //regra: a oferta so existe dentro de uma loja que existe
    private void confirmarLoja(Long cdLoja) {
        if (lojaDAO.findByCodigo(cdLoja) == null) {
            throw new RecursoNaoEncontradoException("Loja " + cdLoja + " não encontrada");
        }
    }

    //regras de negocio da oferta
    private void validarOferta(Long cdLoja, OfertaDTO dto) {
        //regra: o preco da rede precisa ser menor que o preco do sistema
        if (dto.getVlPrecoRede().compareTo(dto.getVlPrecoSistema()) >= 0) {
            throw new RegraDeNegocioException("O preço da rede deve ser menor que o preço do sistema");
        }
        //regra: o produto precisa existir e ser da mesma loja da oferta
        Produto produto = produtoDAO.findByCodigo(dto.getCdProduto());
        if (produto == null) {
            throw new RecursoNaoEncontradoException("Produto " + dto.getCdProduto() + " não encontrado");
        }
        if (!produto.getCdLoja().equals(cdLoja)) {
            throw new RegraDeNegocioException("O produto não pertence a esta loja");
        }
    }
}