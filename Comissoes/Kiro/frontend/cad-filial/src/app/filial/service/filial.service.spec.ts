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

  it('listarTodas deve retornar lista vazia quando não há filiais', () => {
    service.listarTodas().subscribe(res => expect(res).toEqual([]));

    const req = httpMock.expectOne(baseUrl);
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });

  it('buscarPorCodigo deve fazer GET em /filiais/:codigo', () => {
    const mock: Filial = { filial: '001', descr: 'MATRIZ', iesAtivo: 'S' };

    service.buscarPorCodigo('001').subscribe(res => expect(res).toEqual(mock));

    const req = httpMock.expectOne(`${baseUrl}/001`);
    expect(req.request.method).toBe('GET');
    req.flush(mock);
  });

  it('buscarPorCodigo deve usar o codigo informado na URL', () => {
    const mock: Filial = { filial: 'SP1', descr: 'FILIAL SP', iesAtivo: 'N' };

    service.buscarPorCodigo('SP1').subscribe(res => expect(res).toEqual(mock));

    const req = httpMock.expectOne(`${baseUrl}/SP1`);
    expect(req.request.method).toBe('GET');
    req.flush(mock);
  });

  it('incluir deve fazer POST em /filiais com o body correto', () => {
    const filial: Filial = { filial: '002', descr: 'FILIAL SP', iesAtivo: 'S' };

    service.incluir(filial, 'SISTEMA').subscribe(res => expect(res).toBe('Incluído com sucesso.'));

    const req = httpMock.expectOne(r => r.url === baseUrl && r.params.get('codUsuario') === 'SISTEMA');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(filial);
    req.flush('Incluído com sucesso.');
  });

  it('incluir deve enviar codUsuario como query param', () => {
    const filial: Filial = { filial: '003', descr: 'FILIAL RJ', iesAtivo: 'S' };

    service.incluir(filial, 'USUARIO01').subscribe();

    const req = httpMock.expectOne(r => r.url === baseUrl);
    expect(req.request.params.get('codUsuario')).toBe('USUARIO01');
    req.flush('ok');
  });

  it('alterar deve fazer PUT em /filiais/:codigo', () => {
    const filial: Filial = { filial: '001', descr: 'MATRIZ ALT', iesAtivo: 'N' };

    service.alterar('001', filial, 'SISTEMA').subscribe(res => expect(res).toBe('Alterado com sucesso.'));

    const req = httpMock.expectOne(r => r.url === `${baseUrl}/001` && r.params.get('codUsuario') === 'SISTEMA');
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(filial);
    req.flush('Alterado com sucesso.');
  });

  it('alterar deve usar o codigo informado na URL', () => {
    const filial: Filial = { filial: 'SP1', descr: 'FILIAL SP ALT', iesAtivo: 'S' };

    service.alterar('SP1', filial, 'SISTEMA').subscribe();

    const req = httpMock.expectOne(r => r.url === `${baseUrl}/SP1`);
    expect(req.request.method).toBe('PUT');
    req.flush('ok');
  });

  it('alterar deve enviar codUsuario como query param', () => {
    const filial: Filial = { filial: '001', descr: 'MATRIZ', iesAtivo: 'S' };

    service.alterar('001', filial, 'USUARIO02').subscribe();

    const req = httpMock.expectOne(r => r.url === `${baseUrl}/001`);
    expect(req.request.params.get('codUsuario')).toBe('USUARIO02');
    req.flush('ok');
  });
});
