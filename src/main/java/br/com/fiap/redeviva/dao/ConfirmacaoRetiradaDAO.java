package br.com.fiap.redeviva.dao;

import br.com.fiap.redeviva.exception.BancoDeDadosException;
import br.com.fiap.redeviva.model.ConfirmacaoRetirada;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

@Repository
public class ConfirmacaoRetiradaDAO {

    private DataSource dataSource;

    public ConfirmacaoRetiradaDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public ArrayList<ConfirmacaoRetirada> findAll(){ //Arraylist que vai retornar objeto de ConfirmacaoRetirada
        ArrayList<ConfirmacaoRetirada> confirmacoes = new ArrayList<>();
        String sql = "select * from tb_confirmacao_retirada order by cd_confirmacao";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                ConfirmacaoRetirada confirmacao = new ConfirmacaoRetirada();
                confirmacao.setCdConfirmacao(rs.getLong("cd_confirmacao"));
                confirmacao.setCdReserva(rs.getLong("cd_reserva"));
                confirmacao.setDtRetirada(rs.getTimestamp("dt_retirada").toLocalDateTime());
                confirmacao.setNmAtendente(rs.getString("nm_atendente"));
                confirmacao.setVlTotalCaixa(rs.getBigDecimal("vl_total_caixa"));
                confirmacao.setVlDescontoAplicado(rs.getBigDecimal("vl_desconto_aplicado"));
                confirmacoes.add(confirmacao);
            }
        }catch (SQLException e){
            throw new BancoDeDadosException(e);
        }
        return confirmacoes;
    }

    public ConfirmacaoRetirada findByCodigo(Long cdConfirmacao){
        ConfirmacaoRetirada confirmacao = new ConfirmacaoRetirada();
        String sql = "select * from tb_confirmacao_retirada where cd_confirmacao = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setLong(1, cdConfirmacao);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                confirmacao.setCdConfirmacao(rs.getLong("cd_confirmacao"));
                confirmacao.setCdReserva(rs.getLong("cd_reserva"));
                confirmacao.setDtRetirada(rs.getTimestamp("dt_retirada").toLocalDateTime());
                confirmacao.setNmAtendente(rs.getString("nm_atendente"));
                confirmacao.setVlTotalCaixa(rs.getBigDecimal("vl_total_caixa"));
                confirmacao.setVlDescontoAplicado(rs.getBigDecimal("vl_desconto_aplicado"));
            }else {
                return null;
            }
        } catch (SQLException e) {
            throw new BancoDeDadosException(e);
        }
        return confirmacao;
    }

    //passa a reserva para RETIRADA e grava a confirmacao na MESMA conexao (transacao)
    //retorna null quando a reserva nao esta mais ativa
    public ConfirmacaoRetirada save (ConfirmacaoRetirada confirmacao) {
        String sqlReserva = "update tb_reserva set st_reserva = 'RETIRADA' "
                + "where cd_reserva = ? and st_reserva = 'ATIVA'";
        String sqlConfirmacao = "insert into tb_confirmacao_retirada (cd_confirmacao, cd_reserva, dt_retirada, "
                + "nm_atendente, vl_total_caixa, vl_desconto_aplicado) "
                + "values (sq_tb_confirmacao_retirada.nextval, ?, ?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false); //comeca a transacao

            //1) muda a reserva para RETIRADA (so muda se ainda estiver ativa)
            try (PreparedStatement ps = conn.prepareStatement(sqlReserva)) {
                ps.setLong(1, confirmacao.getCdReserva());
                if (ps.executeUpdate() == 0) {
                    conn.rollback();
                    return null; //a reserva nao esta mais ativa
                }
            }

            //2) grava a confirmacao
            try (PreparedStatement ps = conn.prepareStatement(sqlConfirmacao, new String[]{"cd_confirmacao"})) {
                ps.setLong(1, confirmacao.getCdReserva());
                ps.setTimestamp(2, Timestamp.valueOf(confirmacao.getDtRetirada()));
                ps.setString(3, confirmacao.getNmAtendente());
                ps.setBigDecimal(4, confirmacao.getVlTotalCaixa());
                ps.setBigDecimal(5, confirmacao.getVlDescontoAplicado());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    confirmacao.setCdConfirmacao(rs.getLong(1));
                }
            }

            conn.commit(); //confirma a transacao
            return confirmacao;
        } catch (SQLException e) {
            desfazer(conn);
            throw new BancoDeDadosException(e);
        } finally {
            fechar(conn);
        }
    }

    //apaga a confirmacao e devolve a reserva para ATIVA (mesma transacao)
    public boolean delete (ConfirmacaoRetirada confirmacao){
        String sqlConfirmacao = "delete from tb_confirmacao_retirada where cd_confirmacao = ?";
        String sqlReserva = "update tb_reserva set st_reserva = 'ATIVA' where cd_reserva = ?";
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sqlConfirmacao)) {
                ps.setLong(1, confirmacao.getCdConfirmacao());
                if (ps.executeUpdate() == 0) {
                    conn.rollback();
                    return false;
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(sqlReserva)) {
                ps.setLong(1, confirmacao.getCdReserva());
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            desfazer(conn);
            throw new BancoDeDadosException(e);
        } finally {
            fechar(conn);
        }
    }

    //o update so troca o atendente e o total do caixa (reserva, data e desconto nao mudam)
    public ConfirmacaoRetirada update(ConfirmacaoRetirada confirmacao){
        String sql = "update tb_confirmacao_retirada set nm_atendente = ?, vl_total_caixa = ? "
                + "where cd_confirmacao = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setString(1, confirmacao.getNmAtendente());
            ps.setBigDecimal(2, confirmacao.getVlTotalCaixa());
            ps.setLong(3, confirmacao.getCdConfirmacao());

            if(ps.executeUpdate() > 0){
                return confirmacao;
            }else{
                return null;
            }

        } catch (SQLException e) {
            throw new BancoDeDadosException(e);
        }
    }

    //desfaz a transacao quando algo da errado
    private void desfazer(Connection conn) {
        try {
            if (conn != null) {
                conn.rollback();
            }
        } catch (SQLException e) {
            System.out.println("Erro ao desfazer a transacao: " + e.getMessage());
        }
    }

    //devolve a conexao no modo normal e fecha
    private void fechar(Connection conn) {
        try {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("Erro ao fechar a conexao: " + e.getMessage());
        }
    }
}