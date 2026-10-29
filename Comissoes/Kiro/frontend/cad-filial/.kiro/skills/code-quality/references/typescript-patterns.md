# Padrões TypeScript — Team Retro Board

## Tipagem de Colunas

```typescript
// ✅ Correto — union type literal
type Column = 'went-well' | 'improve' | 'action-items';

// ❌ Errado — string genérica
function addCard(retroId: number, column: string, text: string): void {}
```

## Tipagem de Respostas da API

```typescript
// Interface para respostas de sucesso
interface ApiResponse<T> {
  data: T;
}

// Interface para respostas de erro
interface ApiError {
  error: string;
}

// Tipo union para handlers
type ApiResult<T> = ApiResponse<T> | ApiError;
```

## Modelos de Dados

```typescript
interface Retro {
  id: number;
  name: string;
  date: string; // ISO 8601
  createdAt: string;
}

interface Card {
  id: number;
  retroId: number;
  column: Column;
  text: string;
  votes: number;
  createdAt: string;
}

interface ActionItem extends Card {
  column: 'action-items';
  completed: boolean;
  completedAt: string | null;
}
```

## Funções Async

```typescript
// ✅ Tipo de retorno explícito com Promise
async function getRetroById(id: number): Promise<Retro | null> {
  const row = await db.get('SELECT * FROM retros WHERE id = ?', [id]);
  return row ?? null;
}

// ❌ Sem tipo de retorno
async function getRetroById(id: number) {
  return await db.get('SELECT * FROM retros WHERE id = ?', [id]);
}
```

## Validação de Input

```typescript
// ✅ Type guard para validação
function isValidColumn(value: unknown): value is Column {
  return typeof value === 'string' && 
    ['went-well', 'improve', 'action-items'].includes(value);
}

// Uso no handler
if (!isValidColumn(req.body.column)) {
  return res.status(400).json({ error: 'Coluna inválida' });
}
```
