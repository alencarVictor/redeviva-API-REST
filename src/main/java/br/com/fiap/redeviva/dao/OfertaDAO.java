package br.com.fiap.redeviva.dao;

import br.com.fiap.redeviva.exception.BancoDeDadosException;
import br.com.fiap.redeviva.model.Oferta;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Repository
public class OfertaDAO {

    private DataSource dataSource;

    public OfertaDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public ArrayList<Oferta> findAll(){ //Arraylist que vai retornar objeto de Oferta
        ArrayList<Oferta> ofertas = new ArrayList<>();
        String sql = "select * from tb_oferta order by cd_oferta";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                Oferta oferta = new Oferta();
                oferta.setCdOferta(rs.getLong("cd_oferta"));
                oferta.setCdLoja(rs.getLong("cd_loja"));
                oferta.setCdProduto(rs.getLong("cd_produto"));
                oferta.setQtDisponivel(rs.getInt("qt_disponivel"));
                oferta.setVlPrecoSistema(rs.getBigDecimal("vl_preco_sistema"));
                oferta.setVlPrecoRede(rs.getBigDecimal("vl_preco_rede"));
                oferta.setDtValidade(rs.getDate("dt_validade").toLocalDate());
                oferta.setStOferta(rs.getString("st_oferta"));
                ofertas.add(oferta);
            }
        }catch (SQLException e){
            throw new BancoDeDadosException(e);
        }
        return ofertas;
    }

    //ofertas de uma loja (rota aninhada /lojas/{cdLoja}/ofertas)
    public ArrayList<Oferta> findByLoja(Long cdLoja){
        ArrayList<Oferta> ofertas = new ArrayList<>();
        String sql = "select * from tb_oferta where cd_loja = ? order by cd_oferta";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setLong(1, cdLoja);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                Oferta oferta = new Oferta();
                oferta.setCdOferta(rs.getLong("cd_oferta"));
                oferta.setCdLoja(rs.getLong("cd_loja"));
                oferta.setCdProduto(rs.getLong("cd_produto"));
                oferta.setQtDisponivel(rs.getInt("qt_disponivel"));
                oferta.setVlPrecoSistema(rs.getBigDecimal("vl_preco_sistema"));
                oferta.setVlPrecoRede(rs.getBigDecimal("vl_preco_rede"));
                oferta.setDtValidade(rs.getDate("dt_validade").toLocalDate());
                oferta.setStOferta(rs.getString("st_oferta"));
                ofertas.add(oferta);
            }
        }catch (SQLException e){
            throw new BancoDeDadosException(e);
        }
        return ofertas;
    }

    public Oferta findByCodigo(Long cdOferta){
        Oferta oferta = new Oferta();
        String sql = "select * from tb_oferta where cd_oferta = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setLong(1, cdOferta);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                oferta.setCdOferta(rs.getLong("cd_oferta"));
                oferta.setCdLoja(rs.getLong("cd_loja"));
                oferta.setCdProduto(rs.getLong("cd_produto"));
                oferta.setQtDisponivel(rs.getInt("qt_disponivel"));
                oferta.setVlPrecoSistema(rs.getBigDecimal("vl_preco_sistema"));
                oferta.setVlPrecoRede(rs.getBigDecimal("vl_preco_rede"));
                oferta.setDtValidade(rs.getDate("dt_validade").toLocalDate());
                oferta.setStOferta(rs.getString("st_oferta"));
            }else {
                return null;
            }
        } catch (SQLException e) {
            throw new BancoDeDadosException(e);
        }
        return oferta;
    }

    public Oferta save (Oferta oferta) {
        //a sequence gera o id, e o array com o nome da coluna faz o Oracle devolver esse id
        String sql = "insert into tb_oferta (cd_oferta, cd_loja, cd_produto, qt_disponivel, vl_preco_sistema, "
                + "vl_preco_rede, dt_validade, st_oferta) values (sq_tb_oferta.nextval, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"cd_oferta"})) {
            ps.setLong(1, oferta.getCdLoja());
            ps.setLong(2, oferta.getCdProduto());
            ps.setInt(3, oferta.getQtDisponivel());
            ps.setBigDecimal(4, oferta.getVlPrecoSistema());
            ps.setBigDecimal(5, oferta.getVlPrecoRede());
            ps.setDate(6, Date.valueOf(oferta.getDtValidade()));
            ps.setString(7, oferta.getStOferta());
            if (ps.executeUpdate() > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    oferta.setCdOferta(rs.getLong(1));
                }
                return oferta;
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new BancoDeDadosException(e);
        }
    }

    public boolean delete (Long cdOferta){
        String sql = "delete from tb_oferta where cd_oferta = ?";
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setLong(1, cdOferta);
            return ps.executeUpdate() > 0;

        }catch (SQLException e){
            throw new BancoDeDadosException(e);
        }
    }

    //o update nao altera a loja da oferta (cd_loja fica de fora)
    public Oferta update(Oferta oferta){
        String sql = "update tb_oferta set cd_produto = ?, qt_disponivel = ?, vl_preco_sistema = ?, "
                + "vl_preco_rede = ?, dt_validade = ?, st_oferta = ? where cd_oferta = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setLong(1, oferta.getCdProduto());
            ps.setInt(2, oferta.getQtDisponivel());
            ps.setBigDecimal(3, oferta.getVlPrecoSistema());
            ps.setBigDecimal(4, oferta.getVlPrecoRede());
            ps.setDate(5, Date.valueOf(oferta.getDtValidade()));
            ps.setString(6, oferta.getStOferta());
            ps.setLong(7, oferta.getCdOferta());

            if(ps.executeUpdate() > 0){
                return oferta;
            }else{
                return null;
            }

        } catch (SQLException e) {
            throw new BancoDeDadosException(e);
        }
    }
}