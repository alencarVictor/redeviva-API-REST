package br.com.fiap.redeviva.bo;

import br.com.fiap.redeviva.dao.LojaDAO;
import br.com.fiap.redeviva.dao.ProdutoDAO;
import br.com.fiap.redeviva.dto.ProdutoDTO;
import br.com.fiap.redeviva.exception.RecursoNaoEncontradoException;
import br.com.fiap.redeviva.model.Produto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProdutoBO {

    private final ProdutoDAO produtoDAO;
    private final LojaDAO lojaDAO;
    private final ModelMapper modelMapper = new ModelMapper();

    public ProdutoBO(ProdutoDAO produtoDAO, LojaDAO lojaDAO) {
        this.produtoDAO = produtoDAO;
        this.lojaDAO = lojaDAO;
    }

    public List<ProdutoDTO> findAll() {
        List<ProdutoDTO> produtos = new ArrayList<>();
        for (Produto produto : produtoDAO.findAll()) {
            produtos.add(modelMapper.map(produto, ProdutoDTO.class)); //model -> dto
        }
        return produtos;
    }

    public List<ProdutoDTO> findByLoja(Long cdLoja) {
        confirmarLoja(cdLoja);
        List<ProdutoDTO> produtos = new ArrayList<>();
        for (Produto produto : produtoDAO.findByLoja(cdLoja)) {
            produtos.add(modelMapper.map(produto, ProdutoDTO.class));
        }
        return produtos;
    }

    public ProdutoDTO findByCodigo(Long cdProduto) {
        Produto produto = produtoDAO.findByCodigo(cdProduto);
        if (produto == null) {
            throw new RecursoNaoEncontradoException("Produto " + cdProduto + " não encontrado");
        }
        return modelMapper.map(produto, ProdutoDTO.class);
    }

    public ProdutoDTO save(Long cdLoja, ProdutoDTO dto) {
        confirmarLoja(cdLoja);
        Produto produto = modelMapper.map(dto, Produto.class); //dto -> model
        produto.setCdProduto(null); //o id vem da sequence, ignora o que chegar no JSON
        produto.setCdLoja(cdLoja); //a loja vem da rota
        return modelMapper.map(produtoDAO.save(produto), ProdutoDTO.class);
    }

    public ProdutoDTO update(Long cdProduto, ProdutoDTO dto) {
        Produto existente = produtoDAO.findByCodigo(cdProduto);
        if (existente == null) {
            throw new RecursoNaoEncontradoException("Produto " + cdProduto + " não encontrado");
        }
        Produto produto = modelMapper.map(dto, Produto.class);
        produto.setCdProduto(cdProduto); //o id vem da rota
        produto.setCdLoja(existente.getCdLoja()); //o produto continua na mesma loja
        produtoDAO.update(produto);
        return modelMapper.map(produto, ProdutoDTO.class);
    }

    public void delete(Long cdProduto) {
        if (!produtoDAO.delete(cdProduto)) {
            throw new RecursoNaoEncontradoException("Produto " + cdProduto + " não encontrado");
        }
    }

    //regra: o produto so existe dentro de uma loja que existe
    private void confirmarLoja(Long cdLoja) {
        if (lojaDAO.findByCodigo(cdLoja) == null) {
            throw new RecursoNaoEncontradoException("Loja " + cdLoja + " não encontrada");
        }
    }
}