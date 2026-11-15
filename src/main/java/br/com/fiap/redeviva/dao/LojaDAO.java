package br.com.fiap.redeviva.dao;

import br.com.fiap.redeviva.model.Loja;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Repository
public class LojaDAO {

    private final DataSource dataSource;

    public LojaDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public ArrayList<Loja> findAll() throws SQLException { //Arraylist que vai retornar objetos de Loja
        ArrayList<Loja> lojas = new ArrayList<>();
        String sql = "select * from tb_loja order by cd_loja";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Loja loja = new Loja();
                loja.setCdLoja(rs.getLong("cd_loja"));
                loja.setNmLoja(rs.getString("nm_loja"));
                loja.setDsEndereco(rs.getString("ds_endereco"));
                loja.setNrLatitude(rs.getDouble("nr_latitude"));
                loja.setNrLongitude(rs.getDouble("nr_longitude"));
                loja.setDsEmail(rs.getString("ds_email"));
                lojas.add(loja);
            }
        }
        return lojas;
    }

    public Loja findByCodigo(Long cdLoja) throws SQLException {
        String sql = "select * from tb_loja where cd_loja = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, cdLoja);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Loja loja = new Loja();
                    loja.setCdLoja(rs.getLong("cd_loja"));
                    loja.setNmLoja(rs.getString("nm_loja"));
                    loja.setDsEndereco(rs.getString("ds_endereco"));
                    loja.setNrLatitude(rs.getDouble("nr_latitude"));
                    loja.setNrLongitude(rs.getDouble("nr_longitude"));
                    loja.setDsEmail(rs.getString("ds_email"));
                    return loja;
                }
            }
        }
        return null;
    }

    public Loja save(Loja loja) throws SQLException {
        String sql = "insert into tb_loja (cd_loja, nm_loja, ds_endereco, nr_latitude, nr_longitude, ds_email) "
                + "values (sq_tb_loja.nextval, ?, ?, ?, ?, ?)";
        //o array com o nome da coluna faz o Oracle devolver o id gerado pela sequence
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"cd_loja"})) {
            ps.setString(1, loja.getNmLoja());
            ps.setString(2, loja.getDsEndereco());
            ps.setDouble(3, loja.getNrLatitude());
            ps.setDouble(4, loja.getNrLongitude());
            ps.setString(5, loja.getDsEmail());
            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        loja.setCdLoja(rs.getLong(1));
                    }
                }
                return loja;
            }
        }
        return null;
    }

    public boolean delete(Long cdLoja) throws SQLException {
        String sql = "delete from tb_loja where cd_loja = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, cdLoja);
            return ps.executeUpdate() > 0;
        }
    }

    public Loja update(Loja loja) throws SQLException {
        String sql = "update tb_loja set nm_loja = ?, ds_endereco = ?, nr_latitude = ?, "
                + "nr_longitude = ?, ds_email = ? where cd_loja = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, loja.getNmLoja());
            ps.setString(2, loja.getDsEndereco());
            ps.setDouble(3, loja.getNrLatitude());
            ps.setDouble(4, loja.getNrLongitude());
            ps.setString(5, loja.getDsEmail());
            ps.setLong(6, loja.getCdLoja());
            if (ps.executeUpdate() > 0) {
                return loja;
            }
        }
        return null;
    }
}