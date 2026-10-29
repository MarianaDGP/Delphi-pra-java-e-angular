import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Filial, FilialRequest } from './filial.model';

@Injectable({ providedIn: 'root' })
export class FilialService {
  private readonly url = '/filiais';

  constructor(private http: HttpClient) {}

  listar(): Observable<Filial[]> {
    return this.http.get<Filial[]>(this.url);
  }

  buscar(codigo: string): Observable<Filial> {
    return this.http.get<Filial>(`${this.url}/${codigo}`);
  }

  incluir(body: FilialRequest): Observable<Filial> {
    return this.http.post<Filial>(this.url, body);
  }

  alterar(codigo: string, body: FilialRequest): Observable<Filial> {
    return this.http.put<Filial>(`${this.url}/${codigo}`, body);
  }
}
