import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { CadFilialComponent } from './filial/pages/cad-filial/cad-filial.component';

const routes: Routes = [
  { path: '', component: CadFilialComponent },
  { path: '**', redirectTo: '' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
