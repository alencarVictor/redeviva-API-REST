package br.com.fiap.redeviva.bo;

import br.com.fiap.redeviva.dao.LojaDAO;
import br.com.fiap.redeviva.dto.LojaDTO;
import br.com.fiap.redeviva.exception.RecursoNaoEncontradoException;
import br.com.fiap.redeviva.model.Loja;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class LojaBO {

    private final LojaDAO lojaDAO;
    private final ModelMapper modelMapper = new ModelMapper();

    public LojaBO(LojaDAO lojaDAO) {
        this.lojaDAO = lojaDAO;
    }

    public List<LojaDTO> findAll() throws SQLException {
        List<LojaDTO> lojas = new ArrayList<>();
        for (Loja loja : lojaDAO.findAll()) {
            lojas.add(modelMapper.map(loja, LojaDTO.class)); //model -> dto
        }
        return lojas;
    }

    public LojaDTO findByCodigo(Long cdLoja) throws SQLException {
        Loja loja = lojaDAO.findByCodigo(cdLoja);
        if (loja == null) {
            throw new RecursoNaoEncontradoException("Loja " + cdLoja + " não encontrada");
        }
        return modelMapper.map(loja, LojaDTO.class);
    }

    public LojaDTO save(LojaDTO dto) throws SQLException {
        Loja loja = modelMapper.map(dto, Loja.class); //dto -> model
        loja.setCdLoja(null); //o id vem da sequence, ignora o que chegar no JSON
        return modelMapper.map(lojaDAO.save(loja), LojaDTO.class);
    }

    public LojaDTO update(Long cdLoja, LojaDTO dto) throws SQLException {
        Loja loja = modelMapper.map(dto, Loja.class);
        loja.setCdLoja(cdLoja); //o id vem da rota
        if (lojaDAO.update(loja) == null) {
            throw new RecursoNaoEncontradoException("Loja " + cdLoja + " não encontrada");
        }
        return modelMapper.map(loja, LojaDTO.class);
    }

    public void delete(Long cdLoja) throws SQLException {
        if (!lojaDAO.delete(cdLoja)) {
            throw new RecursoNaoEncontradoException("Loja " + cdLoja + " não encontrada");
        }
    }
}