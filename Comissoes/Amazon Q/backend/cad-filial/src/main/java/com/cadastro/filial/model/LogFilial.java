package com.cadastro.filial.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "COR_LOG_FILIAIS")
public class LogFilial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "FILIAL", length = 20)
    private String filial;

    @Column(name = "ACAO", length = 20)
    private String acao;

    @Column(name = "DESCR_LOG", length = 255)
    private String descrLog;

    @Column(name = "DAT_HOR_ACAO")
    private LocalDateTime datHorAcao;

    @Column(name = "COD_USUARIO_ACAO", length = 20)
    private String codUsuarioAcao;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFilial() { return filial; }
    public void setFilial(String filial) { this.filial = filial; }

    public String getAcao() { return acao; }
    public void setAcao(String acao) { this.acao = acao; }

    public String getDescrLog() { return descrLog; }
    public void setDescrLog(String descrLog) { this.descrLog = descrLog; }

    public LocalDateTime getDatHorAcao() { return datHorAcao; }
    public void setDatHorAcao(LocalDateTime datHorAcao) { this.datHorAcao = datHorAcao; }

    public String getCodUsuarioAcao() { return codUsuarioAcao; }
    public void setCodUsuarioAcao(String codUsuarioAcao) { this.codUsuarioAcao = codUsuarioAcao; }
}
