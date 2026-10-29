package com.cadastro.filial.service;

import com.cadastro.filial.model.Filial;
import com.cadastro.filial.model.FilialRequest;
import com.cadastro.filial.model.LogFilial;
import com.cadastro.filial.repository.FilialRepository;
import com.cadastro.filial.repository.LogFilialRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FilialService {

    private final FilialRepository filialRepository;
    private final LogFilialRepository logFilialRepository;

    public FilialService(FilialRepository filialRepository, LogFilialRepository logFilialRepository) {
        this.filialRepository = filialRepository;
        this.logFilialRepository = logFilialRepository;
    }

    public List<Filial> listar() {
        return filialRepository.findAll();
    }

    public Filial buscar(String codigo) {
        return filialRepository.findById(codigo.trim())
                .orElseThrow(() -> new RuntimeException("Filial não encontrada: " + codigo));
    }

    @Transactional
    public Filial incluir(FilialRequest req) {
        String codigo = req.getFilial().trim();

        if (filialRepository.existsById(codigo)) {
            throw new RuntimeException("Filial já cadastrada!");
        }

        Filial filial = new Filial();
        filial.setFilial(codigo);
        filial.setDescr(req.getDescr().trim());
        filial.setIesAtivo(req.isAtivo() ? "S" : "N");
        filialRepository.save(filial);

        String situacao = req.isAtivo() ? "Ativo" : "Inativo";
        gravarLog(codigo, "INCLUSÃO",
                "Inclusão da Filial: " + codigo + " - " + req.getDescr().trim() + " - Situação: " + situacao,
                req.getCodUsuario());

        return filial;
    }

    @Transactional
    public Filial alterar(String codigo, FilialRequest req) {
        Filial filial = filialRepository.findById(codigo.trim())
                .orElseThrow(() -> new RuntimeException("Filial não encontrada: " + codigo));

        String sLog = "";
        if ("S".equals(filial.getIesAtivo()) && !req.isAtivo()) {
            sLog = "Inativação da Filial ";
        } else if ("N".equals(filial.getIesAtivo()) && req.isAtivo()) {
            sLog = "Ativação da Filial ";
        }

        filial.setDescr(req.getDescr().trim());
        filial.setIesAtivo(req.isAtivo() ? "S" : "N");
        filialRepository.save(filial);

        gravarLog(codigo, "ALTERAÇÃO",
                sLog + codigo.trim() + " - " + req.getDescr().trim(),
                req.getCodUsuario());

        return filial;
    }

    private void gravarLog(String filial, String acao, String descrLog, String codUsuario) {
        LogFilial log = new LogFilial();
        log.setFilial(filial);
        log.setAcao(acao);
        log.setDescrLog(descrLog);
        log.setDatHorAcao(LocalDateTime.now());
        log.setCodUsuarioAcao(codUsuario);
        logFilialRepository.save(log);
    }
}
