# Checklist de Segurança — Team Retro Board

## SQLite — Prepared Statements

```typescript
// ✅ Parametrizado — seguro
await db.run('INSERT INTO cards (retro_id, column, text) VALUES (?, ?, ?)', [retroId, column, text]);

// ❌ Interpolação — vulnerável a SQL injection
await db.run(`INSERT INTO cards (retro_id, column, text) VALUES (${retroId}, '${column}', '${text}')`);
```

## XSS — Sanitização de Output

```typescript
// ✅ React escapa por padrão em JSX
<p>{card.text}</p>

// ❌ dangerouslySetInnerHTML sem sanitização
<div dangerouslySetInnerHTML={{ __html: card.text }} />
```

## Validação de Input no Backend

```typescript
// ✅ Validar antes de processar
app.post('/api/cards', async (req, res) => {
  const { retroId, column, text } = req.body;

  if (!retroId || typeof retroId !== 'number') {
    return res.status(400).json({ error: 'retroId é obrigatório e deve ser número' });
  }
  if (!isValidColumn(column)) {
    return res.status(400).json({ error: 'column deve ser went-well, improve ou action-items' });
  }
  if (!text || typeof text !== 'string' || text.trim().length === 0) {
    return res.status(400).json({ error: 'text é obrigatório' });
  }
  if (text.length > 500) {
    return res.status(400).json({ error: 'text deve ter no máximo 500 caracteres' });
  }

  // Processar...
});
```

## Error Handling em Handlers Async

```typescript
// ✅ try/catch com status codes apropriados
app.get('/api/retros/:id', async (req, res) => {
  try {
    const id = Number(req.params.id);
    if (isNaN(id)) {
      return res.status(400).json({ error: 'ID inválido' });
    }

    const retro = await getRetroById(id);
    if (!retro) {
      return res.status(404).json({ error: 'Retro não encontrada' });
    }

    return res.json({ data: retro });
  } catch (err) {
    console.error('Erro ao buscar retro:', err);
    return res.status(500).json({ error: 'Erro interno do servidor' });
  }
});
```

## Headers de Segurança

```typescript
import helmet from 'helmet'; // se necessário, mas manter simples

// Mínimo para aplicação local
app.use((req, res, next) => {
  res.setHeader('X-Content-Type-Options', 'nosniff');
  res.setHeader('X-Frame-Options', 'DENY');
  next();
});
```
