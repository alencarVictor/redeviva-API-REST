package br.com.fiap.redeviva.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ConfirmacaoRetiradaDTO {
    //atributos
    private Long cdConfirmacao;
    private Long cdReserva;
    private LocalDateTime dtRetirada;
    @NotBlank @Size(max=60)
    private String nmAtendente;
    @NotNull @Positive
    private BigDecimal vlTotalCaixa;
    private BigDecimal vlDescontoAplicado;

    //cosntrutores

    public ConfirmacaoRetiradaDTO() {}

    public ConfirmacaoRetiradaDTO(Long cdConfirmacao, Long cdReserva, LocalDateTime dtRetirada, String nmAtendente, BigDecimal vlTotalCaixa, BigDecimal vlDescontoAplicado) {
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
