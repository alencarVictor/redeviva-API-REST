package br.com.fiap.redeviva.model;

import java.math.BigDecimal;

public class Produto {

    //atributos
    private Long cdProduto;
    private Long cdLoja;
    private String cdBarras;
    private String nmProduto;
    private String dsCategoria;
    private BigDecimal nrPesoKg;

    //construtores

    public Produto() {}

    public Produto(Long cdProduto, Long cdLoja, String cdBarras, String nmProduto, String dsCategoria, BigDecimal nrPesoKg) {
        this.cdProduto = cdProduto;
        this.cdLoja = cdLoja;
        this.cdBarras = cdBarras;
        this.nmProduto = nmProduto;
        this.dsCategoria = dsCategoria;
        this.nrPesoKg = nrPesoKg;
    }

    //getters e setters

    public Long getCdProduto() {return cdProduto;}
    public void setCdProduto(Long cdProduto) {this.cdProduto = cdProduto;}

    public Long getCdLoja() {return cdLoja;}
    public void setCdLoja(Long cdLoja) {this.cdLoja = cdLoja;}

    public String getCdBarras() {return cdBarras;}
    public void setCdBarras(String cdBarras) {this.cdBarras = cdBarras;}

    public String getNmProduto() {return nmProduto;}
    public void setNmProduto(String nmProduto) {this.nmProduto = nmProduto;}

    public String getDsCategoria() {return dsCategoria;}
    public void setDsCategoria(String dsCategoria) {this.dsCategoria = dsCategoria;}

    public BigDecimal getNrPesoKg() {return nrPesoKg;}
    public void setNrPesoKg(BigDecimal nrPesoKg) {this.nrPesoKg = nrPesoKg;}
}
