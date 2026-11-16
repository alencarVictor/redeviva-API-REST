package br.com.fiap.redeviva.dao;

import br.com.fiap.redeviva.exception.BancoDeDadosException;
import br.com.fiap.redeviva.model.Reserva;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Repository
public class ReservaDAO {

    private DataSource dataSource;

    public ReservaDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public ArrayList<Reserva> findAll(){ //Arraylist que vai retornar objeto de Reserva
        ArrayList<Reserva> reservas = new ArrayList<>();
        String sql = "select * from tb_reserva order by cd_reserva";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                Reserva reserva = new Reserva();
                reserva.setCdReserva(rs.getLong("cd_reserva"));
                reserva.setCdCliente(rs.getLong("cd_cliente"));
                reserva.setCdLoja(rs.getLong("cd_loja"));
                reserva.setCdRetirada(rs.getString("cd_retirada"));
                reserva.setDtReserva(rs.getTimestamp("dt_reserva").toLocalDateTime());
                reserva.setDtExpiracao(rs.getTimestamp("dt_expiracao").toLocalDateTime());
                reserva.setStReserva(rs.getString("st_reserva"));
                reserva.setDsFormaPagamento(rs.getString("ds_forma_pagamento"));
                reservas.add(reserva);
            }
        }catch (SQLException e){
            throw new BancoDeDadosException(e);
        }
        return reservas;
    }

    //reservas de um cliente (rota aninhada /clientes/{cdCliente}/reservas)
    public ArrayList<Reserva> findByCliente(Long cdCliente){
        ArrayList<Reserva> reservas = new ArrayList<>();
        String sql = "select * from tb_reserva where cd_cliente = ? order by cd_reserva";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setLong(1, cdCliente);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                Reserva reserva = new Reserva();
                reserva.setCdReserva(rs.getLong("cd_reserva"));
                reserva.setCdCliente(rs.getLong("cd_cliente"));
                reserva.setCdLoja(rs.getLong("cd_loja"));
                reserva.setCdRetirada(rs.getString("cd_retirada"));
                reserva.setDtReserva(rs.getTimestamp("dt_reserva").toLocalDateTime());
                reserva.setDtExpiracao(rs.getTimestamp("dt_expiracao").toLocalDateTime());
                reserva.setStReserva(rs.getString("st_reserva"));
                reserva.setDsFormaPagamento(rs.getString("ds_forma_pagamento"));
                reservas.add(reserva);
            }
        }catch (SQLException e){
            throw new BancoDeDadosException(e);
        }
        return reservas;
    }

    public Reserva findByCodigo(Long cdReserva){
        Reserva reserva = new Reserva();
        String sql = "select * from tb_reserva where cd_reserva = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setLong(1, cdReserva);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                reserva.setCdReserva(rs.getLong("cd_reserva"));
                reserva.setCdCliente(rs.getLong("cd_cliente"));
                reserva.setCdLoja(rs.getLong("cd_loja"));
                reserva.setCdRetirada(rs.getString("cd_retirada"));
                reserva.setDtReserva(rs.getTimestamp("dt_reserva").toLocalDateTime());
                reserva.setDtExpiracao(rs.getTimestamp("dt_expiracao").toLocalDateTime());
                reserva.setStReserva(rs.getString("st_reserva"));
                reserva.setDsFormaPagamento(rs.getString("ds_forma_pagamento"));
            }else {
                return null;
            }
        } catch (SQLException e) {
            throw new BancoDeDadosException(e);
        }
        return reserva;
    }

    //conta as reservas do cliente desde uma data (as canceladas nao entram na conta)
    public int contarDesde(Long cdCliente, LocalDateTime desde){
        String sql = "select count(*) from tb_reserva where cd_cliente = ? "
                + "and st_reserva <> 'CANCELADA' and dt_reserva >= ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setLong(1, cdCliente);
            ps.setTimestamp(2, Timestamp.valueOf(desde));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new BancoDeDadosException(e);
        }
    }

    public Reserva save (Reserva reserva) {
        //a sequence gera o id, e o array com o nome da coluna faz o Oracle devolver esse id
        String sql = "insert into tb_reserva (cd_reserva, cd_cliente, cd_loja, cd_retirada, dt_reserva, "
                + "dt_expiracao, st_reserva, ds_forma_pagamento) values (sq_tb_reserva.nextval, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"cd_reserva"})) {
            ps.setLong(1, reserva.getCdCliente());
            ps.setLong(2, reserva.getCdLoja());
            ps.setString(3, reserva.getCdRetirada());
            ps.setTimestamp(4, Timestamp.valueOf(reserva.getDtReserva()));
            ps.setTimestamp(5, Timestamp.valueOf(reserva.getDtExpiracao()));
            ps.setString(6, reserva.getStReserva());
            ps.setString(7, reserva.getDsFormaPagamento());
            if (ps.executeUpdate() > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    reserva.setCdReserva(rs.getLong(1));
                }
                return reserva;
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new BancoDeDadosException(e);
        }
    }

    public boolean delete (Long cdReserva){
        String sql = "delete from tb_reserva where cd_reserva = ?";
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setLong(1, cdReserva);
            return ps.executeUpdate() > 0;

        }catch (SQLException e){
            throw new BancoDeDadosException(e);
        }
    }

    //o update so troca a forma de pagamento (cliente, loja, codigo e datas nao mudam)
    public Reserva update(Reserva reserva){
        String sql = "update tb_reserva set ds_forma_pagamento = ? where cd_reserva = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setString(1, reserva.getDsFormaPagamento());
            ps.setLong(2, reserva.getCdReserva());

            if(ps.executeUpdate() > 0){
                return reserva;
            }else{
                return null;
            }

        } catch (SQLException e) {
            throw new BancoDeDadosException(e);
        }
    }
}