import { Component, OnInit } from '@angular/core';
import { FilialService } from './filial.service';
import { Filial, FilialRequest } from './filial.model';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  filiais: Filial[] = [];
  codigo = '';
  descricao = '';
  ativo = true;
  codUsuario = 'USUARIO01';
  modoEdicao = false;
  mensagem = '';
  tipoMensagem: 'sucesso' | 'erro' | '' = '';
  ano = new Date().getFullYear();

  constructor(private service: FilialService) {}

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.service.listar().subscribe({
      next: data => this.filiais = data,
      error: () => this.exibirMensagem('Erro ao carregar filiais.', 'erro')
    });
  }

  selecionar(filial: Filial): void {
    this.codigo = filial.filial;
    this.descricao = filial.descr;
    this.ativo = filial.iesAtivo === 'S';
    this.modoEdicao = true;
  }

  salvar(): void {
    const body: FilialRequest = {
      filial: this.codigo.toUpperCase(),
      descr: this.descricao.toUpperCase(),
      ativo: this.ativo,
      codUsuario: this.codUsuario
    };

    if (this.modoEdicao) {
      this.service.alterar(this.codigo, body).subscribe({
        next: () => { this.exibirMensagem('Filial alterada com sucesso!', 'sucesso'); this.limpar(); this.carregar(); },
        error: err => this.exibirMensagem(err.error?.erro || 'Erro ao alterar filial.', 'erro')
      });
    } else {
      this.service.incluir(body).subscribe({
        next: () => { this.exibirMensagem('Filial incluída com sucesso!', 'sucesso'); this.limpar(); this.carregar(); },
        error: err => this.exibirMensagem(err.error?.erro || 'Erro ao incluir filial.', 'erro')
      });
    }
  }

  limpar(): void {
    this.codigo = '';
    this.descricao = '';
    this.ativo = true;
    this.modoEdicao = false;
  }

  private exibirMensagem(msg: string, tipo: 'sucesso' | 'erro'): void {
    this.mensagem = msg;
    this.tipoMensagem = tipo;
    setTimeout(() => { this.mensagem = ''; this.tipoMensagem = ''; }, 4000);
  }
}
