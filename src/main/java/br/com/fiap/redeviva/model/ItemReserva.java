package br.com.fiap.redeviva.model;

import java.math.BigDecimal;

public class ItemReserva {

    //atributos
    private Long cdItem;
    private Long cdReserva;
    private Long cdOferta;
    private Integer qtItem;
    private BigDecimal vlUnitario;

    //construtores

    public ItemReserva() {}

    public ItemReserva(Long cdItem, Long cdReserva, Long cdOferta, Integer qtItem, BigDecimal vlUnitario) {
        this.cdItem = cdItem;
        this.cdReserva = cdReserva;
        this.cdOferta = cdOferta;
        this.qtItem = qtItem;
        this.vlUnitario = vlUnitario;
    }

    //getters e setters

    public Long getCdItem() {return cdItem;}
    public void setCdItem(Long cdItem) {this.cdItem = cdItem;}

    public Long getCdReserva() {return cdReserva;}
    public void setCdReserva(Long cdReserva) {this.cdReserva = cdReserva;}

    public Long getCdOferta() {return cdOferta;}
    public void setCdOferta(Long cdOferta) {this.cdOferta = cdOferta;}

    public Integer getQtItem() {return qtItem;}
    public void setQtItem(Integer qtItem) {this.qtItem = qtItem;}

    public BigDecimal getVlUnitario() {return vlUnitario;}
    public void setVlUnitario(BigDecimal vlUnitario) {this.vlUnitario = vlUnitario;}
}
