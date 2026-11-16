package br.com.fiap.redeviva.dao;

import br.com.fiap.redeviva.exception.BancoDeDadosException;
import br.com.fiap.redeviva.model.ItemReserva;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Repository
public class ItemReservaDAO {

    private DataSource dataSource;

    public ItemReservaDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public ArrayList<ItemReserva> findAll(){ //Arraylist que vai retornar objeto de ItemReserva
        ArrayList<ItemReserva> itens = new ArrayList<>();
        String sql = "select * from tb_item_reserva order by cd_item";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                ItemReserva item = new ItemReserva();
                item.setCdItem(rs.getLong("cd_item"));
                item.setCdReserva(rs.getLong("cd_reserva"));
                item.setCdOferta(rs.getLong("cd_oferta"));
                item.setQtItem(rs.getInt("qt_item"));
                item.setVlUnitario(rs.getBigDecimal("vl_unitario"));
                itens.add(item);
            }
        }catch (SQLException e){
            throw new BancoDeDadosException(e);
        }
        return itens;
    }

    //itens de uma reserva (rota aninhada /reservas/{cdReserva}/itens)
    public ArrayList<ItemReserva> findByReserva(Long cdReserva){
        ArrayList<ItemReserva> itens = new ArrayList<>();
        String sql = "select * from tb_item_reserva where cd_reserva = ? order by cd_item";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setLong(1, cdReserva);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                ItemReserva item = new ItemReserva();
                item.setCdItem(rs.getLong("cd_item"));
                item.setCdReserva(rs.getLong("cd_reserva"));
                item.setCdOferta(rs.getLong("cd_oferta"));
                item.setQtItem(rs.getInt("qt_item"));
                item.setVlUnitario(rs.getBigDecimal("vl_unitario"));
                itens.add(item);
            }
        }catch (SQLException e){
            throw new BancoDeDadosException(e);
        }
        return itens;
    }

    public ItemReserva findByCodigo(Long cdItem){
        ItemReserva item = new ItemReserva();
        String sql = "select * from tb_item_reserva where cd_item = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setLong(1, cdItem);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                item.setCdItem(rs.getLong("cd_item"));
                item.setCdReserva(rs.getLong("cd_reserva"));
                item.setCdOferta(rs.getLong("cd_oferta"));
                item.setQtItem(rs.getInt("qt_item"));
                item.setVlUnitario(rs.getBigDecimal("vl_unitario"));
            }else {
                return null;
            }
        } catch (SQLException e) {
            throw new BancoDeDadosException(e);
        }
        return item;
    }

    //salva o item e abate o estoque da oferta na MESMA conexao (transacao): ou sobe tudo ou nada
    //retorna null quando nao tem estoque suficiente
    public ItemReserva save (ItemReserva item) {
        String sqlEstoque = "update tb_oferta set qt_disponivel = qt_disponivel - ? "
                + "where cd_oferta = ? and qt_disponivel - ? >= 0";
        String sqlItem = "insert into tb_item_reserva (cd_item, cd_reserva, cd_oferta, qt_item, vl_unitario) "
                + "values (sq_tb_item_reserva.nextval, ?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false); //comeca a transacao

            //1) abate o estoque (so abate se tiver quantidade suficiente)
            try (PreparedStatement ps = conn.prepareStatement(sqlEstoque)) {
                ps.setInt(1, item.getQtItem());
                ps.setLong(2, item.getCdOferta());
                ps.setInt(3, item.getQtItem());
                if (ps.executeUpdate() == 0) {
                    conn.rollback();
                    return null; //sem estoque suficiente
                }
            }

            //2) grava o item
            try (PreparedStatement ps = conn.prepareStatement(sqlItem, new String[]{"cd_item"})) {
                ps.setLong(1, item.getCdReserva());
                ps.setLong(2, item.getCdOferta());
                ps.setInt(3, item.getQtItem());
                ps.setBigDecimal(4, item.getVlUnitario());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    item.setCdItem(rs.getLong(1));
                }
            }

            //3) atualiza o status da oferta (esgotada ou ativa)
            atualizarStatusOferta(conn, item.getCdOferta());

            conn.commit(); //confirma a transacao
            return item;
        } catch (SQLException e) {
            desfazer(conn);
            throw new BancoDeDadosException(e);
        } finally {
            fechar(conn);
        }
    }

    //apaga o item e devolve a quantidade para o estoque da oferta (mesma transacao)
    public boolean delete (ItemReserva item){
        String sqlItem = "delete from tb_item_reserva where cd_item = ?";
        String sqlEstoque = "update tb_oferta set qt_disponivel = qt_disponivel + ? where cd_oferta = ?";
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sqlItem)) {
                ps.setLong(1, item.getCdItem());
                if (ps.executeUpdate() == 0) {
                    conn.rollback();
                    return false;
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(sqlEstoque)) {
                ps.setInt(1, item.getQtItem());
                ps.setLong(2, item.getCdOferta());
                ps.executeUpdate();
            }

            atualizarStatusOferta(conn, item.getCdOferta());

            conn.commit();
            return true;
        } catch (SQLException e) {
            desfazer(conn);
            throw new BancoDeDadosException(e);
        } finally {
            fechar(conn);
        }
    }

    //troca a quantidade do item e ajusta o estoque pela diferenca (mesma transacao)
    //retorna null quando nao tem estoque suficiente para o aumento
    public ItemReserva update(ItemReserva item, int diferenca){
        String sqlEstoque = "update tb_oferta set qt_disponivel = qt_disponivel - ? "
                + "where cd_oferta = ? and qt_disponivel - ? >= 0";
        String sqlItem = "update tb_item_reserva set qt_item = ? where cd_item = ?";
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            //diferenca positiva abate mais estoque, negativa devolve estoque
            try (PreparedStatement ps = conn.prepareStatement(sqlEstoque)) {
                ps.setInt(1, diferenca);
                ps.setLong(2, item.getCdOferta());
                ps.setInt(3, diferenca);
                if (ps.executeUpdate() == 0) {
                    conn.rollback();
                    return null; //sem estoque suficiente
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(sqlItem)) {
                ps.setInt(1, item.getQtItem());
                ps.setLong(2, item.getCdItem());
                ps.executeUpdate();
            }

            atualizarStatusOferta(conn, item.getCdOferta());

            conn.commit();
            return item;
        } catch (SQLException e) {
            desfazer(conn);
            throw new BancoDeDadosException(e);
        } finally {
            fechar(conn);
        }
    }

    //oferta com estoque zero vira ESGOTADA e volta a ATIVA quando ganha estoque (ENCERRADA nao mexe)
    private void atualizarStatusOferta(Connection conn, Long cdOferta) throws SQLException {
        String sql = "update tb_oferta set st_oferta = case when qt_disponivel = 0 then 'ESGOTADA' else 'ATIVA' end "
                + "where cd_oferta = ? and st_oferta in ('ATIVA', 'ESGOTADA')";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, cdOferta);
            ps.executeUpdate();
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