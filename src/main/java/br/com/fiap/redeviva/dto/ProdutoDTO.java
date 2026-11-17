package br.com.fiap.redeviva.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class ProdutoDTO {
    //atributos
    private Long cdProduto;
    private Long cdLoja;
    @NotBlank @Size(max=14)
    private String cdBarras;
    @NotBlank @Size(max=100)
    private String nmProduto;
    @NotBlank @Pattern(regexp = "HORTIFRUTI|PADARIA|LATICINIOS|CARNES|FRIOS|MERCEARIA|CONGELADOS")
    private String dsCategoria;
    @NotNull @Positive @DecimalMax("999.999")
    private BigDecimal nrPesoKg;

    //construtores

    public ProdutoDTO() {}

    public ProdutoDTO(Long cdProduto, Long cdLoja, String cdBarras, String nmProduto, String dsCategoria, BigDecimal nrPesoKg) {
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
