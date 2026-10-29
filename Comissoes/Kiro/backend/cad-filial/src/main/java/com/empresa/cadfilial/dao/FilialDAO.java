package com.empresa.cadfilial.dao;

import com.empresa.cadfilial.model.Filial;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO de Filiais — usa JdbcTemplate do Spring.
 * O DataSource é injetado automaticamente pelo Spring Boot
 * a partir do application.properties (spring.datasource.*).
 *
 * Equivalente aos componentes SqlAux, CdsAux, SqlLog e SqlDsRelat do Delphi.
 */
@Repository
public class FilialDAO {

    private static final Logger logger = LoggerFactory.getLogger(FilialDAO.class);

    private final JdbcTemplate jdbc;

    public FilialDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // -------------------------------------------------------------------------
    // RowMapper — converte cada linha do ResultSet em um objeto Filial
    // -------------------------------------------------------------------------

    private static final RowMapper<Filial> FILIAL_MAPPER = new RowMapper<Filial>() {
        public Filial mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Filial(
                    rs.getString("FILIAL"),
                    rs.getString("DESCR"),
                    rs.getString("IES_ATIVO"));
        }
    };

    // -------------------------------------------------------------------------
    // Consultas
    // -------------------------------------------------------------------------

    /**
     * Lista todas as filiais.
     * Equivalente ao SqlDsRelat / CdsRelat do Delphi.
     */
    public List<Filial> listarTodas() {
        String sql = "SELECT FILIAL, DESCR, IES_ATIVO FROM COR_FILIAIS ORDER BY FILIAL";
        List<Filial> lista = jdbc.query(sql, FILIAL_MAPPER);
        logger.debug("listarTodas() retornou {} registros.", lista.size());
        return lista;
    }

    /**
     * Busca uma filial pelo código.
     * Retorna null se não encontrar.
     */
    public Filial buscarPorCodigo(String filial) {
        String sql = "SELECT FILIAL, DESCR, IES_ATIVO FROM COR_FILIAIS WHERE FILIAL = ?";
        List<Filial> resultado = jdbc.query(sql, FILIAL_MAPPER, filial.trim());
        return resultado.isEmpty() ? null : resultado.get(0);
    }

    /**
     * Verifica se já existe uma filial com o código informado.
     * Equivalente ao SELECT + CdsAux.Eof do Delphi.
     */
    public boolean existeFilial(String filial) {
        String sql = "SELECT COUNT(*) FROM COR_FILIAIS WHERE FILIAL = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, filial.trim());
        return count != null && count > 0;
    }

    // -------------------------------------------------------------------------
    // Inclusão
    // -------------------------------------------------------------------------

    /**
     * Inclui uma nova filial.
     * Equivalente ao INSERT do SqlAux no Delphi.
     */
    public void incluir(Filial filial) {
        String sql = "INSERT INTO COR_FILIAIS(FILIAL, DESCR, IES_ATIVO) VALUES(?, ?, ?)";
        jdbc.update(sql,
                filial.getFilial().trim(),
                filial.getDescr().trim(),
                filial.getIesAtivo());
        logger.info("Filial '{}' incluida.", filial.getFilial());
    }

    // -------------------------------------------------------------------------
    // Alteração
    // -------------------------------------------------------------------------

    /**
     * Altera uma filial existente.
     * Equivalente ao UPDATE do SqlAux no Delphi.
     */
    public void alterar(Filial filial) {
        String sql = "UPDATE COR_FILIAIS SET DESCR = ?, IES_ATIVO = ? WHERE FILIAL = ?";
        jdbc.update(sql,
                filial.getDescr().trim(),
                filial.getIesAtivo(),
                filial.getFilial().trim());
        logger.info("Filial '{}' alterada.", filial.getFilial());
    }

    // -------------------------------------------------------------------------
    // Log de auditoria
    // -------------------------------------------------------------------------

    /**
     * Insere um registro em COR_LOG_FILIAIS.
     * Equivalente ao bloco SqlLog do Delphi.
     */
    public void inserirLog(String filial, String acao, String descrLog, String codUsuario) {
        String sql = "INSERT INTO COR_LOG_FILIAIS(FILIAL, ACAO, DESCR_LOG, DAT_HOR_ACAO, COD_USUARIO_ACAO) "
                   + "VALUES(?, ?, ?, CURRENT, ?)";
        jdbc.update(sql, filial.trim(), acao, descrLog, codUsuario);
        logger.debug("Log registrado — filial: '{}', acao: '{}'.", filial, acao);
    }
}
