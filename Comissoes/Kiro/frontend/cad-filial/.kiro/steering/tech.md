# Tech Stack

## Framework & Language

- **Angular 9** (TypeScript ~3.8)
- **Angular Material 9** — UI component library (mat-table, mat-form-field, mat-dialog, mat-snack-bar, etc.)
- **Angular Flex Layout 9** — available but layout is primarily done with CSS flexbox in SCSS
- **ReactiveFormsModule** — all forms use `FormBuilder` / `FormGroup`, never template-driven forms
- **RxJS 6.5** — observables for HTTP calls; use `subscribe({ next, error })` object syntax

## HTTP & Environment

- `HttpClient` from `@angular/common/http`
- API base URL comes from `environment.apiUrl` (see `src/environments/`)
- Services append their resource path to `environment.apiUrl` (e.g. `${environment.apiUrl}/filiais`)

## Styling

- **SCSS** for all component styles (configured as default in `angular.json`)
- Global theme in `src/styles.scss` using Angular Material theming (`mat-palette`, `mat-light-theme`)
- Brand primary color: `#003965` (Pamcary blue)
- Global styles define layout helpers: `.page-container`, `.form-row`, `.btn-row`, `.card-title-bar`, `.table-wrapper`

## Build System

- **Angular CLI 9** (`@angular/cli`)
- **Nx 9** workspace tooling (`@nrwl/workspace`) — `nx.json` / `angular.json` at root
- AOT compilation enabled by default
- Output path: `dist/cad-filial-web`

## Common Commands

```bash
# Development server (run manually — do not start via agent)
npm start              # ng serve

# Production build
npm run build          # ng build (add -- --prod for production config)

# Unit tests (Karma + Jasmine)
npm test               # ng test (watch mode — run manually)

# Lint
npm run lint           # ng lint (TSLint)

# E2E tests
npm run e2e            # ng e2e (Protractor)
```

## Testing

- **Karma + Jasmine** for unit tests; config in `karma.conf.js`
- `skipTests: true` is set for all schematics in `angular.json` — test files are not auto-generated
- **Protractor** for e2e tests under `e2e/`

## Locale

- Registered locale: `pt-BR` (set as `LOCALE_ID` provider in `AppModule`)
