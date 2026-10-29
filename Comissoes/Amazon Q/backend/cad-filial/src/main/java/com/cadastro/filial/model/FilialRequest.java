package com.cadastro.filial.model;

import javax.validation.constraints.NotBlank;

public class FilialRequest {

    @NotBlank(message = "Código da Filial é obrigatório")
    private String filial;

    @NotBlank(message = "Descrição da Filial é obrigatória")
    private String descr;

    private boolean ativo;

    @NotBlank(message = "Usuário é obrigatório")
    private String codUsuario;

    public String getFilial() { return filial; }
    public void setFilial(String filial) { this.filial = filial; }

    public String getDescr() { return descr; }
    public void setDescr(String descr) { this.descr = descr; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public String getCodUsuario() { return codUsuario; }
    public void setCodUsuario(String codUsuario) { this.codUsuario = codUsuario; }
}
