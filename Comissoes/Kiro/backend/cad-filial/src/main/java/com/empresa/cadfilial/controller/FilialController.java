package com.empresa.cadfilial.controller;

import com.empresa.cadfilial.model.Filial;
import com.empresa.cadfilial.service.FilialService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/filiais")
public class FilialController {

    private final FilialService filialService;

    public FilialController(FilialService filialService) {
        this.filialService = filialService;
    }

    @GetMapping
    public ResponseEntity<List<Filial>> listarTodas() {
        return ResponseEntity.ok(filialService.listarTodas());
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<Filial> buscarPorCodigo(@PathVariable String codigo) {
        Filial filial = filialService.buscarPorCodigo(codigo);
        if (filial == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(filial);
    }

    @PostMapping
    public ResponseEntity<String> incluir(
            @RequestBody Filial filial,
            @RequestParam String codUsuario) {
        try {
            filialService.incluir(filial, codUsuario);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Filial incluida com sucesso.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<String> alterar(
            @PathVariable String codigo,
            @RequestBody Filial filial,
            @RequestParam String codUsuario) {
        filial.setFilial(codigo);
        try {
            filialService.alterar(filial, codUsuario);
            return ResponseEntity.ok("Filial alterada com sucesso.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}