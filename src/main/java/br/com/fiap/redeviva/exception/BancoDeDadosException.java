package br.com.fiap.redeviva.exception;

import java.sql.SQLException;

public class BancoDeDadosException extends RuntimeException {

    private int codigo; //codigo do erro do Oracle (ORA-xxxxx)

    public BancoDeDadosException(SQLException e) {
        super(e.getMessage());
        this.codigo = e.getErrorCode();
    }

    public int getCodigo() {
        return codigo;
    }
}