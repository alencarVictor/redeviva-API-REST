package br.com.fiap.redeviva.model;

import java.time.LocalDate;

public class Cliente {

    //atributos
    private Long cdCliente;
    private String nmCliente;
    private String nrCpf;
    private String nrCep;
    private Double nrLatitude;
    private Double nrLongitude;
    private String dsOrigem;
    private String stValidacao;
    private LocalDate dtValidadeCadastro;

    //construtores

    public Cliente() {}

    public Cliente(Long cdCliente, String nmCliente, String nrCpf, String nrCep, Double nrLatitude, Double nrLongitude, String dsOrigem, String stValidacao, LocalDate dtValidadeCadastro) {
        this.cdCliente = cdCliente;
        this.nmCliente = nmCliente;
        this.nrCpf = nrCpf;
        this.nrCep = nrCep;
        this.nrLatitude = nrLatitude;
        this.nrLongitude = nrLongitude;
        this.dsOrigem = dsOrigem;
        this.stValidacao = stValidacao;
        this.dtValidadeCadastro = dtValidadeCadastro;
    }

    //getters e setters

    public Long getCdCliente() {return cdCliente;}
    public void setCdCliente(Long cdCliente) {this.cdCliente = cdCliente;}

    public String getNmCliente() {return nmCliente;}
    public void setNmCliente(String nmCliente) {this.nmCliente = nmCliente;}

    public String getNrCpf() {return nrCpf;}
    public void setNrCpf(String nrCpf) {this.nrCpf = nrCpf;}

    public String getNrCep() {return nrCep;}
    public void setNrCep(String nrCep) {this.nrCep = nrCep;}

    public Double getNrLatitude() {return nrLatitude;}
    public void setNrLatitude(Double nrLatitude) {this.nrLatitude = nrLatitude;}

    public Double getNrLongitude() {return nrLongitude;}
    public void setNrLongitude(Double nrLongitude) {this.nrLongitude = nrLongitude;}

    public String getDsOrigem() {return dsOrigem;}
    public void setDsOrigem(String dsOrigem) {this.dsOrigem = dsOrigem;}

    public String getStValidacao() {return stValidacao;}
    public void setStValidacao(String stValidacao) {this.stValidacao = stValidacao;}

    public LocalDate getDtValidadeCadastro() {return dtValidadeCadastro;}
    public void setDtValidadeCadastro(LocalDate dtValidadeCadastro) {this.dtValidadeCadastro = dtValidadeCadastro;}
}
