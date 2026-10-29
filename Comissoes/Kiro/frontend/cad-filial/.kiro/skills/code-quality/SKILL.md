---
name: code-quality
description: Analisa qualidade de código TypeScript em projetos Angular 9 com Angular Material. Verifica tipagem, padrões Angular, Reactive Forms, RxJS, tratamento de erros e boas práticas. Use quando pedir revisão de código, verificar qualidade, encontrar code smells, ou refatorar.
---

## Contexto do Projeto

Stack: Angular 9 com TypeScript ~3.8, Angular Material 9, Angular Flex Layout 9, RxJS 6.5.
Formulários reativos via `FormBuilder`/`FormGroup` (nunca template-driven).
HTTP via `HttpClient` com base em `environment.apiUrl`.
Locale `pt-BR`. UI e comentários em Português Brasileiro.

## Processo de Revisão

Analise o código na seguinte ordem de prioridade:

1. **Segurança** — Validação de input, sanitização, exposição de dados sensíveis
2. **Correção** — Bugs lógicos, edge cases, null handling, memory leaks em subscriptions
3. **Tipagem** — Tipos faltando, uso de `any`, type assertions desnecessárias
4. **Padrões Angular** — Consistência com convenções do projeto (estrutura, naming, ciclo de vida)
5. **RxJS** — Uso correto de operadores, unsubscribe, sintaxe `subscribe({ next, error })`
6. **Performance** — Change detection, queries desnecessárias, re-renders
7. **Legibilidade** — Naming em português, complexidade ciclomática, funções longas (>30 linhas)

## Regras de Qualidade

### TypeScript
- Tipos de retorno explícitos em todas as funções e métodos de serviço
- Nunca usar `any` — usar `unknown` ou tipos específicos
- Interfaces para modelos de domínio (em `model/`)
- Tratar null/undefined com optional chaining e nullish coalescing

### Angular — Componentes
- Usar `OnInit` para lógica de inicialização; carregar dados no `ngOnInit`
- Reactive Forms obrigatório: `FormBuilder`, `FormGroup`, `Validators`
- Injetar `ChangeDetectorRef` e chamar `cdr.detectChanges()` após mutações em `MatTableDataSource.data`
- Sempre reatribuir o array: `this.dataSource.data = [...novosDados]` para atualizar a tabela
- Usar `MatSnackBar` para feedback: `panelClass: 'snack-success'` ou `'snack-error'`
- Usar `MatDialog` + `ConfirmDialogComponent` para ações destrutivas
- Seletores com prefixo `app-` (ex: `app-cad-filial`)

### Angular — Serviços
- `@Injectable({ providedIn: 'root' })` em todos os serviços
- `private readonly url` construído a partir de `environment.apiUrl`
- Retornar `Observable<T>` tipado em todos os métodos
- Um serviço por entidade de domínio

### RxJS
- Sintaxe de subscribe com objeto: `subscribe({ next: ..., error: ... })`
- Nunca usar `.subscribe(fn, fn)` (sintaxe depreciada)
- Fazer unsubscribe em componentes que não usam `async` pipe (usar `takeUntil` ou `Subscription`)
- Preferir operadores pipeable (`map`, `catchError`, `tap`, `switchMap`)

### Formulários Reativos
- Validadores declarados no `FormBuilder` (não no template)
- Campos de código: `.toUpperCase().trim()` antes de enviar à API
- Status ativo/inativo: converter `boolean` ↔ `'S'`/`'N'` na camada do formulário
- Nunca acessar `.value` sem verificar `form.valid` antes de submeter

### Estrutura e Naming
- Arquivos: `kebab-case` com sufixo de tipo — `filial.service.ts`, `cad-filial.component.ts`
- Classes: `PascalCase` com sufixo Angular — `FilialService`, `CadFilialComponent`
- Métodos e variáveis: `camelCase` em Português — `carregar()`, `modoEdicao`, `linhaSelecionada`
- Pages = componentes roteados (smart), Components = apresentacionais (`@Input`/`@Output`)

### Estilo
- SCSS para todos os estilos de componente
- Reutilizar classes globais: `.page-container`, `.form-row`, `.btn-row`, `.card-title-bar`
- Sem estilos inline, exceto ajustes pontuais (cor, font-size)
- Cor primária Pamcary: `#003965`

## Bibliotecas Proibidas / Fora do Stack

Nunca sugerir: React, Vue, Next.js, Redux, Prisma, Docker, Bootstrap, Tailwind.
Não sugerir lazy loading ou feature modules — o projeto usa um único `AppModule`.

## Formato de Report

Para cada issue, use:

```
### [SEVERIDADE] Descrição

**Arquivo:** `caminho:linha`
**Categoria:** Segurança | Correção | Tipagem | Padrão Angular | RxJS | Performance | Legibilidade

**Problema:** O que está errado e por quê.

**Correção:**
```typescript
// código corrigido
```
```

Severidades:
- 🔴 CRÍTICO — Bugs, vulnerabilidades, perda de dados, memory leaks graves
- 🟡 IMPORTANTE — Afeta manutenibilidade ou pode causar bugs futuros
- 🔵 SUGESTÃO — Melhorias de estilo, performance menor ou boas práticas

## Resumo Final

Ao final, fornecer:
- Contagem por severidade
- Score de qualidade (1–10)
- Top 3 ações prioritárias
