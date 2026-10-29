import { Component, OnInit, ViewChild, ChangeDetectorRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { FilialService } from '../../service/filial.service';
import { Filial } from '../../model/filial.model';
import { ConfirmDialogComponent } from '../../../shared/components/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-cad-filial',
  templateUrl: './cad-filial.component.html',
  styleUrls: ['./cad-filial.component.scss']
})
export class CadFilialComponent implements OnInit {

  form: FormGroup;
  modoEdicao = false;
  codigoOriginal = '';
  codUsuario = 'SISTEMA';

  displayedColumns = ['filial', 'descr', 'iesAtivo'];
  dataSource = new MatTableDataSource<Filial>([]);
  linhaSelecionada: Filial | null = null;

  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    private fb: FormBuilder,
    private filialService: FilialService,
    private snack: MatSnackBar,
    private dialog: MatDialog,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      filial:   ['', [Validators.required, Validators.maxLength(3)]],
      descr:    ['', [Validators.required, Validators.maxLength(15)]],
      iesAtivo: [true]
    });
    this.dataSource.sort = this.sort;
    this.carregar();
  }

  carregar(): void {
    this.filialService.listarTodas().subscribe({
      next: lista => {
        // Atribui novo array e força detecção de mudança
        this.dataSource.data = [...lista];
        this.cdr.detectChanges();
      },
      error: () => this.snack.open('Erro ao carregar filiais.', 'Fechar',
        { duration: 4000, panelClass: 'snack-error' })
    });
  }

  novo(): void {
    this.modoEdicao = false;
    this.codigoOriginal = '';
    this.linhaSelecionada = null;
    this.form.reset({ filial: '', descr: '', iesAtivo: true });
    this.form.get('filial').enable();
  }

  selecionarLinha(row: Filial): void {
    this.linhaSelecionada = row;
    this.modoEdicao = true;
    this.codigoOriginal = row.filial;
    this.form.patchValue({
      filial:   row.filial,
      descr:    row.descr,
      iesAtivo: row.iesAtivo === 'S'
    });
    this.form.get('filial').disable();
  }

  cancelar(): void {
    this.novo();
  }

  salvar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const raw = this.form.getRawValue();
    const filial: Filial = {
      filial:   raw.filial.toUpperCase().trim(),
      descr:    raw.descr.toUpperCase().trim(),
      iesAtivo: raw.iesAtivo ? 'S' : 'N'
    };

    if (this.modoEdicao) {
      this.filialService.alterar(this.codigoOriginal, filial, this.codUsuario).subscribe({
        next: msg => {
          this.snack.open(msg || 'Filial alterada com sucesso.', 'OK',
            { duration: 3000, panelClass: 'snack-success' });
          // Atualiza a linha diretamente no array local antes de recarregar
          const idx = this.dataSource.data.findIndex(f => f.filial === this.codigoOriginal);
          if (idx >= 0) {
            this.dataSource.data[idx] = { ...filial };
            this.dataSource.data = [...this.dataSource.data];
            this.cdr.detectChanges();
          }
          // Recarrega do servidor para garantir consistência
          this.carregar();
          this.novo();
        },
        error: err => this.snack.open(err.error || 'Erro ao alterar.', 'Fechar',
          { duration: 4000, panelClass: 'snack-error' })
      });
    } else {
      this.filialService.incluir(filial, this.codUsuario).subscribe({
        next: msg => {
          this.snack.open(msg || 'Filial incluida com sucesso.', 'OK',
            { duration: 3000, panelClass: 'snack-success' });
          this.carregar();
          this.novo();
        },
        error: err => this.snack.open(err.error || 'Erro ao incluir.', 'Fechar',
          { duration: 4000, panelClass: 'snack-error' })
      });
    }
  }

  confirmarSair(): void {
    const ref = this.dialog.open(ConfirmDialogComponent, {
      width: '360px',
      data: { titulo: 'Sair', mensagem: 'Deseja fechar o cadastro de filiais?' }
    });
    ref.afterClosed().subscribe(ok => {
      if (ok) { window.close(); }
    });
  }

  getAtivo(val: string): string {
    return val === 'S' ? 'Sim' : 'Nao';
  }
}