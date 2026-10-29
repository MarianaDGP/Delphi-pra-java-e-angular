---
name: unit-testing
description: Cria e revisa testes unitários Karma/Jasmine para projetos Angular 9 com Angular Material. Gera specs para serviços, componentes e dialogs seguindo os padrões do projeto cad-filial-web. Use quando pedir para criar testes, escrever specs, cobrir um serviço ou componente, ou verificar se um spec está correto.
---

## Contexto do Projeto

Stack: Angular 9, TypeScript ~3.8, Angular Material 9, RxJS 6.5, Karma + Jasmine.
Formulários reativos (`FormBuilder`/`FormGroup`). HTTP via `HttpClient`.
Locale `pt-BR`. Nomes de métodos, variáveis e mensagens em Português Brasileiro.
`skipTests: true` nos schematics — specs são criados manualmente.
Comando para rodar uma única vez (sem watch):
```bash
npm test -- --watch=false --browsers=ChromeHeadless
```

## Estrutura de Arquivos de Spec

- Spec fica na mesma pasta do arquivo testado
- Nome: `<nome-do-arquivo>.spec.ts`
- Exemplos:
  - `src/app/filial/service/filial.service.spec.ts`
  - `src/app/filial/pages/cad-filial/cad-filial.component.spec.ts`
  - `src/app/shared/components/confirm-dialog/confirm-dialog.component.spec.ts`

## Modelos de Domínio

```typescript
// src/app/filial/model/filial.model.ts
export interface Filial {
  filial: string;   // código, até 3 chars, uppercase
  descr: string;    // descrição, até 15 chars, uppercase
  iesAtivo: string; // 'S' | 'N'
}
```

---

## Template: Spec de Serviço HTTP

Use `HttpClientTestingModule` + `HttpTestingController`. Nunca use `HttpClientModule` real em testes.

```typescript
import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { FilialService } from './filial.service';
import { Filial } from '../model/filial.model';
import { environment } from '../../../environments/environment';

describe('FilialService', () => {
  let service: FilialService;
  let httpMock: HttpTestingController;
  const baseUrl = `${environment.apiUrl}/filiais`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [FilialService]
    });
    service = TestBed.inject(FilialService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('deve ser criado', () => {
    expect(service).toBeTruthy();
  });

  it('listarTodas deve fazer GET em /filiais', () => {
    const mock: Filial[] = [{ filial: '001', descr: 'MATRIZ', iesAtivo: 'S' }];
    service.listarTodas().subscribe(res => expect(res).toEqual(mock));
    const req = httpMock.expectOne(baseUrl);
    expect(req.request.method).toBe('GET');
    req.flush(mock);
  });

  it('buscarPorCodigo deve fazer GET em /filiais/:codigo', () => {
    const mock: Filial = { filial: '001', descr: 'MATRIZ', iesAtivo: 'S' };
    service.buscarPorCodigo('001').subscribe(res => expect(res).toEqual(mock));
    const req = httpMock.expectOne(`${baseUrl}/001`);
    expect(req.request.method).toBe('GET');
    req.flush(mock);
  });

  it('incluir deve fazer POST com codUsuario como query param', () => {
    const filial: Filial = { filial: '002', descr: 'FILIAL SP', iesAtivo: 'S' };
    service.incluir(filial, 'SISTEMA').subscribe(res => expect(res).toBe('Incluído com sucesso.'));
    const req = httpMock.expectOne(r => r.url === baseUrl && r.params.get('codUsuario') === 'SISTEMA');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(filial);
    req.flush('Incluído com sucesso.');
  });

  it('alterar deve fazer PUT em /filiais/:codigo com codUsuario como query param', () => {
    const filial: Filial = { filial: '001', descr: 'MATRIZ ALT', iesAtivo: 'N' };
    service.alterar('001', filial, 'SISTEMA').subscribe(res => expect(res).toBe('Alterado com sucesso.'));
    const req = httpMock.expectOne(r => r.url === `${baseUrl}/001` && r.params.get('codUsuario') === 'SISTEMA');
    expect(req.request.method).toBe('PUT');
    req.flush('Alterado com sucesso.');
  });
});
```

---

## Template: Spec de Componente com Formulário e Serviço

Use `NO_ERRORS_SCHEMA` para ignorar componentes filhos (ex: `app-topo`) que não são o foco do teste.
Mocke serviços com `jasmine.createSpyObj`. Mocke `MatSnackBar` e `MatDialog` também.

```typescript
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { of, throwError } from 'rxjs';

import { CadFilialComponent } from './cad-filial.component';
import { FilialService } from '../../service/filial.service';
import { Filial } from '../../model/filial.model';

describe('CadFilialComponent', () => {
  let component: CadFilialComponent;
  let fixture: ComponentFixture<CadFilialComponent>;
  let filialServiceSpy: jasmine.SpyObj<FilialService>;
  let snackSpy: jasmine.SpyObj<MatSnackBar>;
  let dialogSpy: jasmine.SpyObj<MatDialog>;

  const filialMock: Filial = { filial: '001', descr: 'MATRIZ', iesAtivo: 'S' };

  beforeEach(async(() => {
    filialServiceSpy = jasmine.createSpyObj('FilialService', ['listarTodas', 'incluir', 'alterar']);
    snackSpy = jasmine.createSpyObj('MatSnackBar', ['open']);
    dialogSpy = jasmine.createSpyObj('MatDialog', ['open']);

    filialServiceSpy.listarTodas.and.returnValue(of([filialMock]));

    TestBed.configureTestingModule({
      declarations: [CadFilialComponent],
      imports: [ReactiveFormsModule, NoopAnimationsModule],
      providers: [
        { provide: FilialService, useValue: filialServiceSpy },
        { provide: MatSnackBar, useValue: snackSpy },
        { provide: MatDialog, useValue: dialogSpy }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CadFilialComponent);
    component = fixture.componentInstance;
    fixture.detectChanges(); // dispara ngOnInit → carregar()
  });

  it('deve ser criado', () => {
    expect(component).toBeTruthy();
  });

  it('ngOnInit deve carregar filiais e popular dataSource', () => {
    expect(filialServiceSpy.listarTodas).toHaveBeenCalled();
    expect(component.dataSource.data.length).toBe(1);
    expect(component.dataSource.data[0]).toEqual(filialMock);
  });

  it('form deve ser inválido quando vazio', () => {
    component.form.reset();
    expect(component.form.invalid).toBeTrue();
  });

  it('salvar não deve chamar serviço se form inválido', () => {
    component.form.reset();
    component.salvar();
    expect(filialServiceSpy.incluir).not.toHaveBeenCalled();
    expect(filialServiceSpy.alterar).not.toHaveBeenCalled();
  });

  it('salvar deve chamar incluir quando não está em modo edição', () => {
    filialServiceSpy.incluir.and.returnValue(of('Incluído com sucesso.'));
    component.form.setValue({ filial: '002', descr: 'FILIAL SP', iesAtivo: true });
    component.modoEdicao = false;
    component.salvar();
    expect(filialServiceSpy.incluir).toHaveBeenCalled();
    const args = filialServiceSpy.incluir.calls.mostRecent().args;
    expect(args[0].filial).toBe('002');
    expect(args[0].iesAtivo).toBe('S');
  });

  it('salvar deve chamar alterar quando está em modo edição', () => {
    filialServiceSpy.alterar.and.returnValue(of('Alterado com sucesso.'));
    component.selecionarLinha(filialMock);
    component.form.get('descr').setValue('NOVA DESCR');
    component.salvar();
    expect(filialServiceSpy.alterar).toHaveBeenCalledWith('001', jasmine.any(Object), 'SISTEMA');
  });

  it('selecionarLinha deve ativar modoEdicao e preencher o form', () => {
    component.selecionarLinha(filialMock);
    expect(component.modoEdicao).toBeTrue();
    expect(component.codigoOriginal).toBe('001');
    expect(component.form.get('iesAtivo').value).toBeTrue(); // 'S' → true
    expect(component.form.get('filial').disabled).toBeTrue();
  });

  it('novo deve resetar o form e sair do modo edição', () => {
    component.selecionarLinha(filialMock);
    component.novo();
    expect(component.modoEdicao).toBeFalse();
    expect(component.linhaSelecionada).toBeNull();
    expect(component.form.get('filial').enabled).toBeTrue();
  });

  it('getAtivo deve retornar "Sim" para S e "Nao" para N', () => {
    expect(component.getAtivo('S')).toBe('Sim');
    expect(component.getAtivo('N')).toBe('Nao');
  });

  it('carregar deve exibir snack de erro quando serviço falhar', () => {
    filialServiceSpy.listarTodas.and.returnValue(throwError('erro'));
    component.carregar();
    expect(snackSpy.open).toHaveBeenCalledWith(
      'Erro ao carregar filiais.', 'Fechar', jasmine.objectContaining({ panelClass: 'snack-error' })
    );
  });
});
```

---

## Template: Spec de Dialog

```typescript
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MatDialogModule } from '@angular/material/dialog';
import { ConfirmDialogComponent } from './confirm-dialog.component';

describe('ConfirmDialogComponent', () => {
  let component: ConfirmDialogComponent;
  let fixture: ComponentFixture<ConfirmDialogComponent>;
  let dialogRefSpy: jasmine.SpyObj<MatDialogRef<ConfirmDialogComponent>>;

  beforeEach(async(() => {
    dialogRefSpy = jasmine.createSpyObj('MatDialogRef', ['close']);

    TestBed.configureTestingModule({
      declarations: [ConfirmDialogComponent],
      imports: [NoopAnimationsModule, MatDialogModule],
      providers: [
        { provide: MatDialogRef, useValue: dialogRefSpy },
        { provide: MAT_DIALOG_DATA, useValue: { titulo: 'Confirmar', mensagem: 'Deseja continuar?' } }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfirmDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('deve ser criado', () => {
    expect(component).toBeTruthy();
  });

  it('deve exibir titulo e mensagem recebidos via MAT_DIALOG_DATA', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Confirmar');
    expect(compiled.textContent).toContain('Deseja continuar?');
  });

  it('fechar com true ao confirmar', () => {
    component.dialogRef.close(true);
    expect(dialogRefSpy.close).toHaveBeenCalledWith(true);
  });

  it('fechar com false ao cancelar', () => {
    component.dialogRef.close(false);
    expect(dialogRefSpy.close).toHaveBeenCalledWith(false);
  });
});
```

---

## Regras de Escrita de Testes

### Estrutura
- Um `describe` por arquivo testado, com o nome da classe
- `beforeEach` para setup; `afterEach` para `httpMock.verify()` em serviços HTTP
- Nomes de `it` em Português, descrevendo o comportamento esperado
- Padrão AAA: **Arrange** → **Act** → **Assert**

### Mocks e Spies
- Serviços: sempre `jasmine.createSpyObj` — nunca instanciar a classe real
- HTTP: sempre `HttpClientTestingModule` + `HttpTestingController`
- `MatSnackBar` e `MatDialog`: sempre mockar com `createSpyObj`
- Componentes filhos irrelevantes: usar `NO_ERRORS_SCHEMA`
- Animações: sempre `NoopAnimationsModule` em vez de `BrowserAnimationsModule`

### Conversões de Domínio
- Ao testar `salvar()`, verificar que `iesAtivo: true` vira `'S'` e `false` vira `'N'`
- Verificar que `filial` e `descr` são enviados em uppercase (`.toUpperCase().trim()`)
- Ao testar `selecionarLinha()`, verificar que `iesAtivo === 'S'` vira `true` no form

### RxJS em Testes
- Usar `of(valor)` para simular resposta de sucesso
- Usar `throwError('mensagem')` para simular erro
- Nunca usar `done` callback — preferir observables síncronos com `of`/`throwError`

### O que Testar em Serviços
1. Instância criada (`toBeTruthy`)
2. Método HTTP correto (GET, POST, PUT)
3. URL correta (incluindo path params)
4. Query params presentes e com valor correto
5. Body da requisição (POST/PUT)
6. Tipo de resposta (`responseType: 'text'` para write operations)

### O que Testar em Componentes
1. Instância criada
2. `ngOnInit` chama o serviço e popula `dataSource`
3. Validação do form (inválido quando vazio, válido quando preenchido)
4. `salvar()` não chama serviço se form inválido
5. `salvar()` chama `incluir` no modo inclusão e `alterar` no modo edição
6. `selecionarLinha()` ativa `modoEdicao`, preenche form, desabilita campo código
7. `novo()` / `cancelar()` resetam estado
8. Feedback de erro via `MatSnackBar` quando serviço falha
9. Conversões `'S'`/`'N'` ↔ `boolean`

### Cobertura Mínima Esperada
- Serviços: 100% dos métodos públicos
- Componentes: todos os métodos públicos + fluxos de erro

---

## Checklist Antes de Entregar um Spec

- [ ] `afterEach(() => httpMock.verify())` presente em specs de serviço
- [ ] `NoopAnimationsModule` importado em specs de componente
- [ ] Nenhum `HttpClientModule` real — apenas `HttpClientTestingModule`
- [ ] Serviços mockados com `createSpyObj`, não instanciados diretamente
- [ ] `NO_ERRORS_SCHEMA` usado para isolar o componente de dependências de template
- [ ] Nomes dos `it` em Português descrevendo comportamento
- [ ] Conversões `'S'`/`'N'` ↔ `boolean` verificadas nos testes de form
- [ ] Campos uppercase verificados nos testes de `salvar()`
- [ ] Fluxo de erro testado com `throwError` e verificação do `snack.open`

---

## Bibliotecas Proibidas / Fora do Stack

Nunca sugerir: Jest, Testing Library, Cypress, Spectator, ng-mocks, React Testing Library.
Usar apenas: Karma, Jasmine, `@angular/core/testing`, `HttpClientTestingModule`.
Não sugerir lazy loading, feature modules ou `TestBed.overrideComponent`.
