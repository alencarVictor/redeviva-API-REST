package br.com.fiap.redeviva.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ConfirmacaoRetirada {

    //atributos
    private Long cdConfirmacao;
    private Long cdReserva;
    private LocalDateTime dtRetirada;
    private String nmAtendente;
    private BigDecimal vlTotalCaixa;
    private BigDecimal vlDescontoAplicado;

    //cosntrutores

    public ConfirmacaoRetirada() {}

    public ConfirmacaoRetirada(Long cdConfirmacao, Long cdReserva, LocalDateTime dtRetirada, String nmAtendente, BigDecimal vlTotalCaixa, BigDecimal vlDescontoAplicado) {
        this.cdConfirmacao = cdConfirmacao;
        this.cdReserva = cdReserva;
        this.dtRetirada = dtRetirada;
        this.nmAtendente = nmAtendente;
        this.vlTotalCaixa = vlTotalCaixa;
        this.vlDescontoAplicado = vlDescontoAplicado;
    }

    //getters e setters

    public Long getCdConfirmacao() {return cdConfirmacao;}
    public void setCdConfirmacao(Long cdConfirmacao) {this.cdConfirmacao = cdConfirmacao;}

    public Long getCdReserva() {return cdReserva;}
    public void setCdReserva(Long cdReserva) {this.cdReserva = cdReserva;}

    public LocalDateTime getDtRetirada() {return dtRetirada;}
    public void setDtRetirada(LocalDateTime dtRetirada) {this.dtRetirada = dtRetirada;}

    public String getNmAtendente() {return nmAtendente;}
    public void setNmAtendente(String nmAtendente) {this.nmAtendente = nmAtendente;}

    public BigDecimal getVlTotalCaixa() {return vlTotalCaixa;}
    public void setVlTotalCaixa(BigDecimal vlTotalCaixa) {this.vlTotalCaixa = vlTotalCaixa;}

    public BigDecimal getVlDescontoAplicado() {return vlDescontoAplicado;}
    public void setVlDescontoAplicado(BigDecimal vlDescontoAplicado) {this.vlDescontoAplicado = vlDescontoAplicado;}
}
