package br.com.fiap.redeviva.dto;

import jakarta.validation.constraints.*;

public class LojaDTO {

    //atributos
    private Long cdLoja;
    @NotBlank @Size(max=100)
    private String nmLoja;
    @NotBlank @Size(max=200)
    private String dsEndereco;
    @NotNull
    @DecimalMin("-90") @DecimalMax("90")
    private Double nrLatitude;
    @NotNull @DecimalMin("-180") @DecimalMax("180")
    private Double nrLongitude;
    @NotBlank @Email @Size(max=120)
    private String dsEmail;

    //construtores
    public LojaDTO() {}

    public LojaDTO(Long cdLoja, String nmLoja, String dsEndereco, Double nrLatitude, Double nrLongitude, String dsEmail) {
        this.cdLoja = cdLoja;
        this.nmLoja = nmLoja;
        this.dsEndereco = dsEndereco;
        this.nrLatitude = nrLatitude;
        this.nrLongitude = nrLongitude;
        this.dsEmail = dsEmail;
    }
    //getters e setters

    public Long getCdLoja() {return cdLoja;}
    public void setCdLoja(Long cdLoja) {this.cdLoja = cdLoja;}

    public String getNmLoja() {return nmLoja;}
    public void setNmLoja(String nmLoja) {this.nmLoja = nmLoja;}

    public String getDsEndereco() {return dsEndereco;}
    public void setDsEndereco(String dsEndereco) {this.dsEndereco = dsEndereco;}

    public Double getNrLatitude() {return nrLatitude;}
    public void setNrLatitude(Double nrLatitude) {this.nrLatitude = nrLatitude;}

    public Double getNrLongitude() {return nrLongitude;}
    public void setNrLongitude(Double nrLongitude) {this.nrLongitude = nrLongitude;}

    public String getDsEmail() {return dsEmail;}
    public void setDsEmail(String dsEmail) {this.dsEmail = dsEmail;}
}
