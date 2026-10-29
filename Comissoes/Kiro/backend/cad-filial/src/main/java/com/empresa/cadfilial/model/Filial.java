package com.empresa.cadfilial.model;

/**
 * Modelo que representa um registro da tabela COR_FILIAIS.
 *
 * Campos mapeados:
 *   FILIAL     -> filial
 *   DESCR      -> descr
 *   IES_ATIVO  -> iesAtivo  ('S' = ativo, 'N' = inativo)
 */
public class Filial {

    private String filial;
    private String descr;
    private String iesAtivo;

    public Filial() {}

    public Filial(String filial, String descr, String iesAtivo) {
        this.filial   = filial;
        this.descr    = descr;
        this.iesAtivo = iesAtivo;
    }

    // -------------------------------------------------------------------------
    // Getters / Setters
    // -------------------------------------------------------------------------

    public String getFilial() { return filial; }
    public void setFilial(String filial) { this.filial = filial; }

    public String getDescr() { return descr; }
    public void setDescr(String descr) { this.descr = descr; }

    public String getIesAtivo() { return iesAtivo; }
    public void setIesAtivo(String iesAtivo) { this.iesAtivo = iesAtivo; }

    /** Conveniência: retorna true quando IES_ATIVO = 'S'. */
    public boolean isAtivo() {
        return "S".equalsIgnoreCase(iesAtivo);
    }

    @Override
    public String toString() {
        return "Filial{filial='" + filial + "', descr='" + descr
                + "', iesAtivo='" + iesAtivo + "'}";
    }
}
