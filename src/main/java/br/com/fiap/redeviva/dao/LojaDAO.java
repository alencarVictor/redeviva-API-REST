package br.com.fiap.redeviva.dao;

import br.com.fiap.redeviva.exception.BancoDeDadosException;
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

    private DataSource dataSource;

    public LojaDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public ArrayList<Loja> findAll(){ //Arraylist que vai retornar objeto de Loja
        ArrayList<Loja> lojas = new ArrayList<>();
        String sql = "select * from tb_loja order by cd_loja";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                Loja loja = new Loja();
                loja.setCdLoja(rs.getLong("cd_loja"));
                loja.setNmLoja(rs.getString("nm_loja"));
                loja.setDsEndereco(rs.getString("ds_endereco"));
                loja.setNrLatitude(rs.getDouble("nr_latitude"));
                loja.setNrLongitude(rs.getDouble("nr_longitude"));
                loja.setDsEmail(rs.getString("ds_email"));
                lojas.add(loja);
            }
        }catch (SQLException e){
            throw new BancoDeDadosException(e);
        }
        return lojas;
    }

    public Loja findByCodigo(Long cdLoja){
        Loja loja = new Loja();
        String sql = "select * from tb_loja where cd_loja = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setLong(1, cdLoja);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                loja.setCdLoja(rs.getLong("cd_loja"));
                loja.setNmLoja(rs.getString("nm_loja"));
                loja.setDsEndereco(rs.getString("ds_endereco"));
                loja.setNrLatitude(rs.getDouble("nr_latitude"));
                loja.setNrLongitude(rs.getDouble("nr_longitude"));
                loja.setDsEmail(rs.getString("ds_email"));
            }else {
                return null;
            }
        } catch (SQLException e) {
            throw new BancoDeDadosException(e);
        }
        return loja;
    }

    public Loja save (Loja loja) {
        //a sequence gera o id, e o array com o nome da coluna faz o Oracle devolver esse id
        String sql = "insert into tb_loja (cd_loja, nm_loja, ds_endereco, nr_latitude, nr_longitude, ds_email) "
                + "values (sq_tb_loja.nextval, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"cd_loja"})) {
            ps.setString(1, loja.getNmLoja());
            ps.setString(2, loja.getDsEndereco());
            ps.setDouble(3, loja.getNrLatitude());
            ps.setDouble(4, loja.getNrLongitude());
            ps.setString(5, loja.getDsEmail());
            if (ps.executeUpdate() > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    loja.setCdLoja(rs.getLong(1));
                }
                return loja;
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new BancoDeDadosException(e);
        }
    }

    public boolean delete (Long cdLoja){
        String sql = "delete from tb_loja where cd_loja = ?";
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setLong(1, cdLoja);
            return ps.executeUpdate() > 0;

        }catch (SQLException e){
            throw new BancoDeDadosException(e);
        }
    }

    public Loja update(Loja loja){
        String sql = "update tb_loja set nm_loja = ?, ds_endereco = ?, nr_latitude = ?, "
                + "nr_longitude = ?, ds_email = ? where cd_loja = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setString(1, loja.getNmLoja());
            ps.setString(2, loja.getDsEndereco());
            ps.setDouble(3, loja.getNrLatitude());
            ps.setDouble(4, loja.getNrLongitude());
            ps.setString(5, loja.getDsEmail());
            ps.setLong(6, loja.getCdLoja());

            if(ps.executeUpdate() > 0){
                return loja;
            }else{
                return null;
            }

        } catch (SQLException e) {
            throw new BancoDeDadosException(e);
        }
    }
}