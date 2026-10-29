package com.cadastro.filial.controller;

import com.cadastro.filial.model.Filial;
import com.cadastro.filial.model.FilialRequest;
import com.cadastro.filial.service.FilialService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/filiais")
public class FilialController {

    private final FilialService filialService;

    public FilialController(FilialService filialService) {
        this.filialService = filialService;
    }

    @GetMapping
    public ResponseEntity<List<Filial>> listar() {
        return ResponseEntity.ok(filialService.listar());
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<Filial> buscar(@PathVariable String codigo) {
        return ResponseEntity.ok(filialService.buscar(codigo));
    }

    @PostMapping
    public ResponseEntity<Filial> incluir(@Valid @RequestBody FilialRequest req) {
        return ResponseEntity.status(201).body(filialService.incluir(req));
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<Filial> alterar(@PathVariable String codigo,
                                          @Valid @RequestBody FilialRequest req) {
        return ResponseEntity.ok(filialService.alterar(codigo, req));
    }
}
