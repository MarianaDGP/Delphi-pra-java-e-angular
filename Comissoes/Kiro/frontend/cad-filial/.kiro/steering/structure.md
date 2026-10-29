# Project Structure

## Root Layout

```
src/app/
├── core/           # Singleton, app-wide components (header/topo, guards, interceptors)
├── shared/         # Reusable components, pipes, directives used across features
├── filial/         # Feature module (domain-based folder)
│   ├── model/      # TypeScript interfaces for the domain
│   ├── service/    # Injectable services (HTTP calls)
│   ├── pages/      # Routed page components (smart/container components)
│   └── components/ # Feature-specific presentational components
├── app.module.ts
├── app-routing.module.ts
├── app.component.*
```

## Conventions

### Feature Organization
- Each feature gets its own folder under `src/app/` named after the domain (e.g. `filial/`)
- Within a feature, always separate `model/`, `service/`, `pages/`, and `components/`
- **Pages** = routed, smart components that own state and call services
- **Components** = presentational, receive data via `@Input` / emit via `@Output`

### Naming
- Files: `kebab-case` with type suffix — `filial.service.ts`, `cad-filial.component.ts`
- Classes: `PascalCase` with Angular suffix — `FilialService`, `CadFilialComponent`
- Selectors: `app-` prefix — `app-cad-filial`, `app-topo`
- Methods and variables: `camelCase` in Portuguese (e.g. `carregar()`, `modoEdicao`, `linhaSelecionada`)

### Services
- Provided at root: `@Injectable({ providedIn: 'root' })`
- One service per domain entity
- Private `readonly url` built from `environment.apiUrl`
- Return typed `Observable<T>` from all methods

### Components
- Use `OnInit` for initialization logic; call data-loading methods from `ngOnInit`
- Reactive forms only (`FormBuilder`, `FormGroup`, `Validators`)
- Inject `ChangeDetectorRef` and call `cdr.detectChanges()` after mutating `MatTableDataSource.data`
- Always reassign the array (`this.dataSource.data = [...newData]`) to trigger table refresh
- Use `MatSnackBar` for user feedback: `panelClass: 'snack-success'` or `'snack-error'`
- Use `MatDialog` + `ConfirmDialogComponent` for destructive action confirmations

### Shared Components
- `ConfirmDialogComponent` — generic confirm dialog; pass `{ titulo, mensagem }` via `MAT_DIALOG_DATA`
- Declare all shared components in `AppModule` (no feature modules currently)

### Routing
- Flat routing in `app-routing.module.ts`; wildcard `**` redirects to `''`
- No lazy loading currently — all components declared directly in `AppModule`

### Styling
- Component-specific styles go in the paired `.scss` file
- Reuse global layout classes from `styles.scss` (`.page-container`, `.form-row`, `.btn-row`, `.card-title-bar`)
- Do not use inline styles except for minor one-off adjustments (color, font-size)
- Follow the Pamcary blue (`#003965`) brand color for headers and primary actions

### String Values & Locale
- UI labels, messages, and comments are in **Brazilian Portuguese**
- Boolean flags stored as `'S'` / `'N'` strings in the API; convert to/from `boolean` in the form layer
- Text inputs that map to stored codes should be `.toUpperCase().trim()` before sending to the API
