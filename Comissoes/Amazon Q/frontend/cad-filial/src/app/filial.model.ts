export interface Filial {
  filial: string;
  descr: string;
  iesAtivo: string;
}

export interface FilialRequest {
  filial: string;
  descr: string;
  ativo: boolean;
  codUsuario: string;
}
