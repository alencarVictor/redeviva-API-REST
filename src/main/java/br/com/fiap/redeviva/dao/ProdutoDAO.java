package br.com.fiap.redeviva.dao;

import br.com.fiap.redeviva.exception.BancoDeDadosException;
import br.com.fiap.redeviva.model.Produto;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Repository
public class ProdutoDAO {

    private DataSource dataSource;

    public ProdutoDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public ArrayList<Produto> findAll(){ //Arraylist que vai retornar objeto de Produto
        ArrayList<Produto> produtos = new ArrayList<>();
        String sql = "select * from tb_produto order by cd_produto";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                Produto produto = new Produto();
                produto.setCdProduto(rs.getLong("cd_produto"));
                produto.setCdLoja(rs.getLong("cd_loja"));
                produto.setCdBarras(rs.getString("cd_barras"));
                produto.setNmProduto(rs.getString("nm_produto"));
                produto.setDsCategoria(rs.getString("ds_categoria"));
                produto.setNrPesoKg(rs.getBigDecimal("nr_peso_kg"));
                produtos.add(produto);
            }
        }catch (SQLException e){
            throw new BancoDeDadosException(e);
        }
        return produtos;
    }

    //produtos de uma loja (rota aninhada /lojas/{cdLoja}/produtos)
    public ArrayList<Produto> findByLoja(Long cdLoja){
        ArrayList<Produto> produtos = new ArrayList<>();
        String sql = "select * from tb_produto where cd_loja = ? order by cd_produto";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setLong(1, cdLoja);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                Produto produto = new Produto();
                produto.setCdProduto(rs.getLong("cd_produto"));
                produto.setCdLoja(rs.getLong("cd_loja"));
                produto.setCdBarras(rs.getString("cd_barras"));
                produto.setNmProduto(rs.getString("nm_produto"));
                produto.setDsCategoria(rs.getString("ds_categoria"));
                produto.setNrPesoKg(rs.getBigDecimal("nr_peso_kg"));
                produtos.add(produto);
            }
        }catch (SQLException e){
            throw new BancoDeDadosException(e);
        }
        return produtos;
    }

    public Produto findByCodigo(Long cdProduto){
        Produto produto = new Produto();
        String sql = "select * from tb_produto where cd_produto = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setLong(1, cdProduto);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                produto.setCdProduto(rs.getLong("cd_produto"));
                produto.setCdLoja(rs.getLong("cd_loja"));
                produto.setCdBarras(rs.getString("cd_barras"));
                produto.setNmProduto(rs.getString("nm_produto"));
                produto.setDsCategoria(rs.getString("ds_categoria"));
                produto.setNrPesoKg(rs.getBigDecimal("nr_peso_kg"));
            }else {
                return null;
            }
        } catch (SQLException e) {
            throw new BancoDeDadosException(e);
        }
        return produto;
    }

    public Produto save (Produto produto) {
        //a sequence gera o id, e o array com o nome da coluna faz o Oracle devolver esse id
        String sql = "insert into tb_produto (cd_produto, cd_loja, cd_barras, nm_produto, ds_categoria, nr_peso_kg) "
                + "values (sq_tb_produto.nextval, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"cd_produto"})) {
            ps.setLong(1, produto.getCdLoja());
            ps.setString(2, produto.getCdBarras());
            ps.setString(3, produto.getNmProduto());
            ps.setString(4, produto.getDsCategoria());
            ps.setBigDecimal(5, produto.getNrPesoKg());
            if (ps.executeUpdate() > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    produto.setCdProduto(rs.getLong(1));
                }
                return produto;
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new BancoDeDadosException(e);
        }
    }

    public boolean delete (Long cdProduto){
        String sql = "delete from tb_produto where cd_produto = ?";
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setLong(1, cdProduto);
            return ps.executeUpdate() > 0;

        }catch (SQLException e){
            throw new BancoDeDadosException(e);
        }
    }

    //o update nao altera a loja do produto (cd_loja fica de fora)
    public Produto update(Produto produto){
        String sql = "update tb_produto set cd_barras = ?, nm_produto = ?, ds_categoria = ?, "
                + "nr_peso_kg = ? where cd_produto = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setString(1, produto.getCdBarras());
            ps.setString(2, produto.getNmProduto());
            ps.setString(3, produto.getDsCategoria());
            ps.setBigDecimal(4, produto.getNrPesoKg());
            ps.setLong(5, produto.getCdProduto());

            if(ps.executeUpdate() > 0){
                return produto;
            }else{
                return null;
            }

        } catch (SQLException e) {
            throw new BancoDeDadosException(e);
        }
    }
}