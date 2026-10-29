# Product

**cad-filial-web** is a single-page internal web application for managing company branches (*filiais*) within the Pamcary Comissões system.

## Core Functionality

- List all registered branches in a sortable data table
- Create new branches (código, descrição, ativo/inativo)
- Edit existing branches via double-click row selection
- Confirm destructive navigation actions via dialog

## Target Users

Internal back-office users. The UI is in Brazilian Portuguese (`pt-BR` locale).

## Business Rules

- Branch code (`filial`) is up to 3 characters, stored and displayed in uppercase
- Description (`descr`) is up to 15 characters, stored in uppercase
- Active status is stored as `'S'` / `'N'` strings in the API, mapped to a boolean in the form
- The `codUsuario` (currently hardcoded as `'SISTEMA'`) is passed as a query param on write operations
