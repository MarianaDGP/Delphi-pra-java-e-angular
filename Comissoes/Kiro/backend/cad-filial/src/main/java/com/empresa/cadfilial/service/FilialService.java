package com.empresa.cadfilial.service;

import com.empresa.cadfilial.dao.FilialDAO;
import com.empresa.cadfilial.model.Filial;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Serviço de negócio para Filiais.
 * Concentra as regras equivalentes ao SpeedButton5Click do Delphi:
 *   - validação de duplicidade
 *   - montagem da descrição do log
 *   - orquestração de inclusão/alteração + log numa transação
 */
@Service
public class FilialService {

    private final FilialDAO filialDAO;

    public FilialService(FilialDAO filialDAO) {
        this.filialDAO = filialDAO;
    }

    // -------------------------------------------------------------------------
    // Consulta
    // -------------------------------------------------------------------------

    public List<Filial> listarTodas() {
        return filialDAO.listarTodas();
    }

    public Filial buscarPorCodigo(String filial) {
        return filialDAO.buscarPorCodigo(filial);
    }

    // -------------------------------------------------------------------------
    // Inclusão
    // -------------------------------------------------------------------------

    /**
     * Inclui uma nova filial e registra o log.
     * Equivalente ao bloco "if bAltera = false" do Delphi.
     *
     * @param filial      dados da nova filial
     * @param codUsuario  código do usuário logado (frmMain.cCdpFisic no Delphi)
     * @throws IllegalArgumentException se a filial já existir
     */
    @Transactional
    public void incluir(Filial filial, String codUsuario) {
        if (filialDAO.existeFilial(filial.getFilial())) {
            throw new IllegalArgumentException("Filial ja cadastrada: " + filial.getFilial());
        }

        filialDAO.incluir(filial);

        String situacao = filial.isAtivo() ? "Ativo" : "Inativo";
        String descrLog = "Inclusao da Filial: " + filial.getFilial().trim()
                + " - " + filial.getDescr().trim()
                + " - Situacao: " + situacao;

        filialDAO.inserirLog(filial.getFilial(), "INCLUSAO", descrLog, codUsuario);
    }

    // -------------------------------------------------------------------------
    // Alteração
    // -------------------------------------------------------------------------

    /**
     * Altera uma filial existente e registra o log.
     * Equivalente ao bloco "else" (bAltera=true) do Delphi.
     *
     * @param filialAtualizada  dados novos
     * @param codUsuario        código do usuário logado
     * @throws IllegalArgumentException se a filial não existir
     */
    @Transactional
    public void alterar(Filial filialAtualizada, String codUsuario) {
        Filial anterior = filialDAO.buscarPorCodigo(filialAtualizada.getFilial());
        if (anterior == null) {
            throw new IllegalArgumentException("Filial nao encontrada: " + filialAtualizada.getFilial());
        }

        filialDAO.alterar(filialAtualizada);

        // Monta prefixo do log conforme mudança de situação — igual ao Delphi
        String sLog = "";
        if ("S".equals(anterior.getIesAtivo()) && !filialAtualizada.isAtivo()) {
            sLog = "Inativacao da Filial ";
        } else if ("N".equals(anterior.getIesAtivo()) && filialAtualizada.isAtivo()) {
            sLog = "Ativacao da Filial ";
        }

        String descrLog = sLog + filialAtualizada.getFilial().trim()
                + " - " + filialAtualizada.getDescr().trim();

        filialDAO.inserirLog(filialAtualizada.getFilial(), "ALTERACAO", descrLog, codUsuario);
    }
}
