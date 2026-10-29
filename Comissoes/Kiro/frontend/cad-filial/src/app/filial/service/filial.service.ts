import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Filial } from '../model/filial.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class FilialService {

  private readonly url = `${environment.apiUrl}/filiais`;

  constructor(private http: HttpClient) {}

  listarTodas(): Observable<Filial[]> {
    return this.http.get<Filial[]>(this.url);
  }

  buscarPorCodigo(codigo: string): Observable<Filial> {
    return this.http.get<Filial>(`${this.url}/${codigo}`);
  }

  incluir(filial: Filial, codUsuario: string): Observable<string> {
    const params = new HttpParams().set('codUsuario', codUsuario);
    return this.http.post(this.url, filial, { params, responseType: 'text' });
  }

  alterar(codigo: string, filial: Filial, codUsuario: string): Observable<string> {
    const params = new HttpParams().set('codUsuario', codUsuario);
    return this.http.put(`${this.url}/${codigo}`, filial, { params, responseType: 'text' });
  }
}

//teste 2