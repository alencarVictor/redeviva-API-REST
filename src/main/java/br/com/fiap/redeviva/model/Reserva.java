package br.com.fiap.redeviva.model;

import java.time.LocalDateTime;

public class Reserva {

    //atributos
    private Long cdReserva;
    private Long cdCliente;
    private Long cdLoja;
    private String cdRetirada;
    private LocalDateTime dtReserva;
    private LocalDateTime dtExpiracao;
    private String stReserva;
    private String dsFormaPagamento;

    //construtores

    public Reserva() {}

    public Reserva(Long cdReserva, Long cdCliente, Long cdLoja, String cdRetirada, LocalDateTime dtReserva, LocalDateTime dtExpiracao, String stReserva, String dsFormaPagamento) {
        this.cdReserva = cdReserva;
        this.cdCliente = cdCliente;
        this.cdLoja = cdLoja;
        this.cdRetirada = cdRetirada;
        this.dtReserva = dtReserva;
        this.dtExpiracao = dtExpiracao;
        this.stReserva = stReserva;
        this.dsFormaPagamento = dsFormaPagamento;
    }
    //getters e setters

    public Long getCdReserva() {return cdReserva;}
    public void setCdReserva(Long cdReserva) {this.cdReserva = cdReserva;}

    public Long getCdCliente() {return cdCliente;}
    public void setCdCliente(Long cdCliente) {this.cdCliente = cdCliente;}

    public Long getCdLoja() {return cdLoja;}
    public void setCdLoja(Long cdLoja) {this.cdLoja = cdLoja;}

    public String getCdRetirada() {return cdRetirada;}
    public void setCdRetirada(String cdRetirada) {this.cdRetirada = cdRetirada;}

    public LocalDateTime getDtReserva() {return dtReserva;}
    public void setDtReserva(LocalDateTime dtReserva) {this.dtReserva = dtReserva;}

    public LocalDateTime getDtExpiracao() {return dtExpiracao;}
    public void setDtExpiracao(LocalDateTime dtExpiracao) {this.dtExpiracao = dtExpiracao;}

    public String getStReserva() {return stReserva;}
    public void setStReserva(String stReserva) {this.stReserva = stReserva;}

    public String getDsFormaPagamento() {return dsFormaPagamento;}
    public void setDsFormaPagamento(String dsFormaPagamento) {this.dsFormaPagamento = dsFormaPagamento;}
}
