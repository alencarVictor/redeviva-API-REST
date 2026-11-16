package br.com.fiap.redeviva.dao;

import br.com.fiap.redeviva.exception.BancoDeDadosException;
import br.com.fiap.redeviva.model.Cliente;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

@Repository
public class ClienteDAO {

    private DataSource dataSource;

    public ClienteDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public ArrayList<Cliente> findAll(){ //Arraylist que vai retornar objeto de Cliente
        ArrayList<Cliente> clientes = new ArrayList<>();
        String sql = "select * from tb_cliente order by cd_cliente";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                Cliente cliente = new Cliente();
                cliente.setCdCliente(rs.getLong("cd_cliente"));
                cliente.setNmCliente(rs.getString("nm_cliente"));
                cliente.setNrCpf(rs.getString("nr_cpf"));
                cliente.setNrCep(rs.getString("nr_cep"));
                cliente.setNrLatitude(rs.getDouble("nr_latitude"));
                cliente.setNrLongitude(rs.getDouble("nr_longitude"));
                cliente.setDsOrigem(rs.getString("ds_origem"));
                cliente.setStValidacao(rs.getString("st_validacao"));
                //a validade e nula enquanto o cliente nao for validado
                Date validade = rs.getDate("dt_validade_cadastro");
                if (validade != null) {
                    cliente.setDtValidadeCadastro(validade.toLocalDate());
                }
                clientes.add(cliente);
            }
        }catch (SQLException e){
            throw new BancoDeDadosException(e);
        }
        return clientes;
    }

    public Cliente findByCodigo(Long cdCliente){
        Cliente cliente = new Cliente();
        String sql = "select * from tb_cliente where cd_cliente = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setLong(1, cdCliente);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                cliente.setCdCliente(rs.getLong("cd_cliente"));
                cliente.setNmCliente(rs.getString("nm_cliente"));
                cliente.setNrCpf(rs.getString("nr_cpf"));
                cliente.setNrCep(rs.getString("nr_cep"));
                cliente.setNrLatitude(rs.getDouble("nr_latitude"));
                cliente.setNrLongitude(rs.getDouble("nr_longitude"));
                cliente.setDsOrigem(rs.getString("ds_origem"));
                cliente.setStValidacao(rs.getString("st_validacao"));
                Date validade = rs.getDate("dt_validade_cadastro");
                if (validade != null) {
                    cliente.setDtValidadeCadastro(validade.toLocalDate());
                }
            }else {
                return null;
            }
        } catch (SQLException e) {
            throw new BancoDeDadosException(e);
        }
        return cliente;
    }

    public Cliente save (Cliente cliente) {
        //a validade fica de fora do insert: o cliente nasce sem data (nulo) ate ser validado
        String sql = "insert into tb_cliente (cd_cliente, nm_cliente, nr_cpf, nr_cep, nr_latitude, nr_longitude, "
                + "ds_origem, st_validacao) values (sq_tb_cliente.nextval, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"cd_cliente"})) {
            ps.setString(1, cliente.getNmCliente());
            ps.setString(2, cliente.getNrCpf());
            ps.setString(3, cliente.getNrCep());
            ps.setDouble(4, cliente.getNrLatitude());
            ps.setDouble(5, cliente.getNrLongitude());
            ps.setString(6, cliente.getDsOrigem());
            ps.setString(7, cliente.getStValidacao());
            if (ps.executeUpdate() > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    cliente.setCdCliente(rs.getLong(1));
                }
                return cliente;
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new BancoDeDadosException(e);
        }
    }

    public boolean delete (Long cdCliente){
        String sql = "delete from tb_cliente where cd_cliente = ?";
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setLong(1, cdCliente);
            return ps.executeUpdate() > 0;

        }catch (SQLException e){
            throw new BancoDeDadosException(e);
        }
    }

    //o update dos dados cadastrais nao mexe no status nem na validade (so as rotas de validar e recusar mexem)
    public Cliente update(Cliente cliente){
        String sql = "update tb_cliente set nm_cliente = ?, nr_cpf = ?, nr_cep = ?, nr_latitude = ?, "
                + "nr_longitude = ?, ds_origem = ? where cd_cliente = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setString(1, cliente.getNmCliente());
            ps.setString(2, cliente.getNrCpf());
            ps.setString(3, cliente.getNrCep());
            ps.setDouble(4, cliente.getNrLatitude());
            ps.setDouble(5, cliente.getNrLongitude());
            ps.setString(6, cliente.getDsOrigem());
            ps.setLong(7, cliente.getCdCliente());

            if(ps.executeUpdate() > 0){
                return cliente;
            }else{
                return null;
            }

        } catch (SQLException e) {
            throw new BancoDeDadosException(e);
        }
    }

    //usado so pelas rotas de validar e recusar: atualiza o status e a validade do cadastro
    public Cliente atualizarValidacao(Cliente cliente){
        String sql = "update tb_cliente set st_validacao = ?, dt_validade_cadastro = ? where cd_cliente = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setString(1, cliente.getStValidacao());
            if (cliente.getDtValidadeCadastro() == null) {
                ps.setNull(2, Types.DATE); //cliente recusado fica sem validade
            } else {
                ps.setDate(2, Date.valueOf(cliente.getDtValidadeCadastro()));
            }
            ps.setLong(3, cliente.getCdCliente());

            if(ps.executeUpdate() > 0){
                return cliente;
            }else{
                return null;
            }

        } catch (SQLException e) {
            throw new BancoDeDadosException(e);
        }
    }
}