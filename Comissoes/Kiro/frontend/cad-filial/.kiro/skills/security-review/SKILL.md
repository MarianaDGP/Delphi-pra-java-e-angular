---
name: security-review
description: >
  Revisa código em busca de vulnerabilidades de segurança em projetos Angular 9 com TypeScript e Angular Material.
  Use ao revisar PRs, auditar mudanças, verificar validação de inputs, XSS, exposição de dados,
  dependências inseguras, ou quando o usuário disser "revisão de segurança", "verificar vulnerabilidades",
  "auditar segurança" ou "esse código é seguro".
metadata:
  author: pamcary
  version: 1.0.0
---

## Processo de Revisão de Segurança

Você é um revisor de segurança para uma aplicação Angular 9 + TypeScript + Angular Material que consome uma API REST.
Analise o código sistematicamente usando o checklist abaixo, classificando cada achado por severidade.

## Níveis de Severidade

| Nível | Descrição | Ação |
|-------|-----------|------|
| 🔴 CRÍTICO | Vulnerabilidade explorável, risco de vazamento de dados | Corrigir antes do merge |
| 🟠 ALTO | Fraqueza de segurança significativa | Corrigir antes do merge |
| 🟡 MÉDIO | Lacuna de defesa em profundidade | Corrigir na próxima sprint |
| 🟢 BAIXO | Melhoria de boas práticas | Registrar como dívida técnica |

## Checklist de Revisão

### 1. Validação e Sanitização de Input

- [ ] Todos os campos de formulário têm `Validators` adequados (`required`, `maxLength`, `pattern`)
- [ ] Campos de código (`filial`) limitados a 3 caracteres e convertidos para maiúsculas
- [ ] Campos de descrição (`descr`) limitados a 15 caracteres e convertidos para maiúsculas
- [ ] Nenhum valor de input é usado diretamente em operações sensíveis sem validação
- [ ] `form.valid` é verificado antes de qualquer chamada HTTP de escrita

**Ruim:**
```typescript
salvar() {
  this.service.salvar(this.form.value).subscribe(...);
}
```

**Bom:**
```typescript
salvar() {
  if (this.form.invalid) return;
  const payload = {
    ...this.form.value,
    filial: this.form.value.filial.toUpperCase().trim(),
    descr: this.form.value.descr.toUpperCase().trim(),
  };
  this.service.salvar(payload).subscribe(...);
}
```

### 2. Cross-Site Scripting (XSS)

- [ ] Nenhum uso de `[innerHTML]` com dados não sanitizados
- [ ] Conteúdo gerado pelo usuário não é interpolado diretamente no template sem escape
- [ ] Não usar `bypassSecurityTrust*` do `DomSanitizer` sem justificativa explícita
- [ ] URLs dinâmicas são validadas antes de uso em `[href]` ou `[src]`

**Ruim:**
```html
<div [innerHTML]="dadoDoUsuario"></div>
```

**Bom:**
```html
<div>{{ dadoDoUsuario }}</div>
```

### 3. Exposição de Dados Sensíveis

- [ ] Nenhum dado sensível (tokens, senhas, chaves) hardcoded no código-fonte
- [ ] `codUsuario` e outros identificadores não são expostos em logs do console em produção
- [ ] Variáveis de ambiente sensíveis ficam em `environment.ts` (não commitado) ou no servidor
- [ ] Respostas de erro da API não expõem detalhes internos ao usuário final

### 4. Autenticação e Autorização

- [ ] Rotas protegidas usam guards Angular (`CanActivate`) quando aplicável
- [ ] `codUsuario` não é manipulável pelo usuário final (atualmente hardcoded como `'SISTEMA'`)
- [ ] Parâmetros de query sensíveis (ex: `codUsuario`) são enviados via header ou body, não URL, quando possível
- [ ] Nenhuma lógica de autorização implementada apenas no frontend sem validação no backend

### 5. Comunicação HTTP

- [ ] Todas as chamadas HTTP usam HTTPS em produção (`environment.prod.ts`)
- [ ] Erros HTTP são tratados com `catchError` ou no callback `error` do `subscribe`
- [ ] Nenhuma informação sensível é enviada em query params de forma desnecessária
- [ ] Interceptors HTTP não logam payloads completos em produção

**Bom:**
```typescript
this.service.listar().subscribe({
  next: (dados) => { this.dataSource.data = dados; },
  error: (err) => {
    console.error('Erro ao carregar filiais', err.status);
    this.snackBar.open('Erro ao carregar dados.', 'Fechar', { panelClass: 'snack-error' });
  }
});
```

### 6. Tratamento de Erros e Disclosure de Informações

- [ ] Stack traces não são exibidos ao usuário final
- [ ] Mensagens de erro para o usuário são genéricas e em português
- [ ] Erros são logados no console apenas em desenvolvimento
- [ ] `MatSnackBar` exibe mensagens amigáveis, não detalhes técnicos da API

**Ruim:**
```typescript
error: (err) => this.snackBar.open(err.message, 'Fechar')
```

**Bom:**
```typescript
error: () => this.snackBar.open('Erro ao salvar. Tente novamente.', 'Fechar', { panelClass: 'snack-error' })
```

### 7. Segurança de Dependências

- [ ] `npm audit` sem vulnerabilidades críticas ou altas
- [ ] Dependências com versões fixas no `package.json` (sem ranges abertos `^` ou `~` para libs críticas)
- [ ] Nenhuma dependência desnecessária que aumente a superfície de ataque
- [ ] Angular e Angular Material na mesma versão major (9.x.x)

### 8. Configuração e Secrets

- [ ] Nenhum secret, token ou senha hardcoded no código-fonte ou nos arquivos `environment.ts`
- [ ] `environment.prod.ts` não está commitado com valores reais de produção
- [ ] `.gitignore` inclui arquivos de ambiente com dados sensíveis
- [ ] `apiUrl` de produção usa HTTPS

### 9. Proteção contra Ações Destrutivas

- [ ] Operações de exclusão ou sobrescrita usam `MatDialog` + `ConfirmDialogComponent`
- [ ] Confirmação de ação destrutiva não pode ser bypassada via manipulação de estado
- [ ] Duplo clique para edição não dispara múltiplas requisições simultâneas

### 10. Memory Leaks e Subscriptions

- [ ] Subscriptions de longa duração são canceladas no `ngOnDestroy`
- [ ] Uso de `takeUntil(this.destroy$)` ou `Subscription.unsubscribe()` em componentes com observables persistentes
- [ ] Nenhum observable de HTTP fica aberto indefinidamente

**Bom:**
```typescript
private destroy$ = new Subject<void>();

ngOnInit() {
  this.service.listar()
    .pipe(takeUntil(this.destroy$))
    .subscribe({ next: (dados) => this.dataSource.data = dados });
}

ngOnDestroy() {
  this.destroy$.next();
  this.destroy$.complete();
}
```

## Formato de Output

Para cada achado, reportar:

```
### [SEVERIDADE] Título do Achado

**Arquivo:** `caminho/para/arquivo.ts:linha`
**Categoria:** (ex: Validação de Input, XSS, Exposição de Dados, HTTP, Dependências)
**Descrição:** O que é a vulnerabilidade e como poderia ser explorada.
**Recomendação:** Correção específica com exemplo de código.
```

## Template de Resumo

Após revisar todos os arquivos, fornecer:

1. **Score de Segurança:** X/10 (baseado nos achados)
2. **Achados Críticos/Altos:** Lista que requer ação imediata
3. **Padrões Positivos:** Práticas de segurança já implementadas corretamente
4. **Recomendações:** Lista priorizada de melhorias
