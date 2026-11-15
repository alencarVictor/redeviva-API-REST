package br.com.fiap.redeviva.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public class OfertaDTO {

    //atributos
    private Long cdOferta;
    private Long cdLoja;
    @NotNull
    private Long cdProduto;
    @NotNull @Positive
    private Integer qtDisponivel;
    @NotNull @Positive
    private BigDecimal vlPrecoSistema;
    @NotNull @Positive
    private BigDecimal vlPrecoRede;
    @NotNull @Future
    private LocalDate dtValidade;
    private String stOferta;

    //construtores

    public OfertaDTO() {}

    public OfertaDTO(Long cdOferta, Long cdLoja, Long cdProduto, Integer qtDisponivel, BigDecimal vlPrecoSistema, BigDecimal vlPrecoRede, LocalDate dtValidade, String stOferta) {
        this.cdOferta = cdOferta;
        this.cdLoja = cdLoja;
        this.cdProduto = cdProduto;
        this.qtDisponivel = qtDisponivel;
        this.vlPrecoSistema = vlPrecoSistema;
        this.vlPrecoRede = vlPrecoRede;
        this.dtValidade = dtValidade;
        this.stOferta = stOferta;
    }

    //getters e setters

    public Long getCdOferta() {return cdOferta;}
    public void setCdOferta(Long cdOferta) {this.cdOferta = cdOferta;}

    public Long getCdLoja() {return cdLoja;}
    public void setCdLoja(Long cdLoja) {this.cdLoja = cdLoja;}

    public Long getCdProduto() {return cdProduto;}
    public void setCdProduto(Long cdProduto) {this.cdProduto = cdProduto;}

    public Integer getQtDisponivel() {return qtDisponivel;}
    public void setQtDisponivel(Integer qtDisponivel) {this.qtDisponivel = qtDisponivel;}

    public BigDecimal getVlPrecoSistema() {return vlPrecoSistema;}
    public void setVlPrecoSistema(BigDecimal vlPrecoSistema) {this.vlPrecoSistema = vlPrecoSistema;}

    public BigDecimal getVlPrecoRede() {return vlPrecoRede;}
    public void setVlPrecoRede(BigDecimal vlPrecoRede) {this.vlPrecoRede = vlPrecoRede;}

    public LocalDate getDtValidade() {return dtValidade;}
    public void setDtValidade(LocalDate dtValidade) {this.dtValidade = dtValidade;}

    public String getStOferta() {return stOferta;}
    public void setStOferta(String stOferta) {this.stOferta = stOferta;}
}
