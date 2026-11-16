package br.com.fiap.redeviva.dao;

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

    private final DataSource dataSource;

    public ProdutoDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public ArrayList<Produto> findAll() throws SQLException { //Arraylist que vai retornar objetos de Produto
        ArrayList<Produto> produtos = new ArrayList<>();
        String sql = "select * from tb_produto order by cd_produto";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Produto produto = new Produto();
                produto.setCdProduto(rs.getLong("cd_produto"));
                produto.setCdLoja(rs.getLong("cd_loja"));
                produto.setCdBarras(rs.getString("cd_barras"));
                produto.setNmProduto(rs.getString("nm_produto"));
                produto.setDsCategoria(rs.getString("ds_categoria"));
                produto.setNrPesoKg(rs.getBigDecimal("nr_peso_kg"));
                produtos.add(produto);
            }
        }
        return produtos;
    }

    //produtos de uma loja (rota aninhada /lojas/{cdLoja}/produtos)
    public ArrayList<Produto> findByLoja(Long cdLoja) throws SQLException {
        ArrayList<Produto> produtos = new ArrayList<>();
        String sql = "select * from tb_produto where cd_loja = ? order by cd_produto";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, cdLoja);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Produto produto = new Produto();
                    produto.setCdProduto(rs.getLong("cd_produto"));
                    produto.setCdLoja(rs.getLong("cd_loja"));
                    produto.setCdBarras(rs.getString("cd_barras"));
                    produto.setNmProduto(rs.getString("nm_produto"));
                    produto.setDsCategoria(rs.getString("ds_categoria"));
                    produto.setNrPesoKg(rs.getBigDecimal("nr_peso_kg"));
                    produtos.add(produto);
                }
            }
        }
        return produtos;
    }

    public Produto findByCodigo(Long cdProduto) throws SQLException {
        String sql = "select * from tb_produto where cd_produto = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, cdProduto);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Produto produto = new Produto();
                    produto.setCdProduto(rs.getLong("cd_produto"));
                    produto.setCdLoja(rs.getLong("cd_loja"));
                    produto.setCdBarras(rs.getString("cd_barras"));
                    produto.setNmProduto(rs.getString("nm_produto"));
                    produto.setDsCategoria(rs.getString("ds_categoria"));
                    produto.setNrPesoKg(rs.getBigDecimal("nr_peso_kg"));
                    return produto;
                }
            }
        }
        return null;
    }

    public Produto save(Produto produto) throws SQLException {
        String sql = "insert into tb_produto (cd_produto, cd_loja, cd_barras, nm_produto, ds_categoria, nr_peso_kg) "
                + "values (sq_tb_produto.nextval, ?, ?, ?, ?, ?)";
        //o array com o nome da coluna faz o Oracle devolver o id gerado pela sequence
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"cd_produto"})) {
            ps.setLong(1, produto.getCdLoja());
            ps.setString(2, produto.getCdBarras());
            ps.setString(3, produto.getNmProduto());
            ps.setString(4, produto.getDsCategoria());
            ps.setBigDecimal(5, produto.getNrPesoKg());
            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        produto.setCdProduto(rs.getLong(1));
                    }
                }
                return produto;
            }
        }
        return null;
    }

    public boolean delete(Long cdProduto) throws SQLException {
        String sql = "delete from tb_produto where cd_produto = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, cdProduto);
            return ps.executeUpdate() > 0;
        }
    }

    //o update nao altera a loja do produto (cd_loja fica de fora)
    public Produto update(Produto produto) throws SQLException {
        String sql = "update tb_produto set cd_barras = ?, nm_produto = ?, ds_categoria = ?, "
                + "nr_peso_kg = ? where cd_produto = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, produto.getCdBarras());
            ps.setString(2, produto.getNmProduto());
            ps.setString(3, produto.getDsCategoria());
            ps.setBigDecimal(4, produto.getNrPesoKg());
            ps.setLong(5, produto.getCdProduto());
            if (ps.executeUpdate() > 0) {
                return produto;
            }
        }
        return null;
    }
}