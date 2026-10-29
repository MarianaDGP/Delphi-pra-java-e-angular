package com.cadastro.filial.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "COR_FILIAIS")
public class Filial {

    @Id
    @Column(name = "FILIAL", length = 20)
    @NotBlank(message = "Código da Filial é obrigatório")
    private String filial;

    @Column(name = "DESCR", length = 100)
    @NotBlank(message = "Descrição da Filial é obrigatória")
    private String descr;

    @Column(name = "IES_ATIVO", length = 1)
    private String iesAtivo = "N";

    public String getFilial() { return filial; }
    public void setFilial(String filial) { this.filial = filial; }

    public String getDescr() { return descr; }
    public void setDescr(String descr) { this.descr = descr; }

    public String getIesAtivo() { return iesAtivo; }
    public void setIesAtivo(String iesAtivo) { this.iesAtivo = iesAtivo; }
}
