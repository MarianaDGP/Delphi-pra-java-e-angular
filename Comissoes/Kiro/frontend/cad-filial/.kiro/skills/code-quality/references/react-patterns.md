# Padrões React — Team Retro Board

## Componentes com Props Tipadas

```typescript
// ✅ Interface dedicada para props
interface CardItemProps {
  card: Card;
  onVote: (cardId: number) => void;
  onDelete: (cardId: number) => void;
}

function CardItem({ card, onVote, onDelete }: CardItemProps): JSX.Element {
  return (
    <article aria-label={`Card: ${card.text}`}>
      <p>{card.text}</p>
      <button onClick={() => onVote(card.id)} aria-label="Votar neste card">
        👍 {card.votes}
      </button>
    </article>
  );
}
```

## Context para Estado Global

```typescript
// ✅ Context com tipo forte
interface RetroContextValue {
  retros: Retro[];
  currentRetro: Retro | null;
  addCard: (column: Column, text: string) => Promise<void>;
  voteCard: (cardId: number) => Promise<void>;
  loading: boolean;
  error: string | null;
}

const RetroContext = createContext<RetroContextValue | null>(null);

// Hook customizado com type guard
function useRetro(): RetroContextValue {
  const context = useContext(RetroContext);
  if (!context) {
    throw new Error('useRetro deve ser usado dentro de RetroProvider');
  }
  return context;
}
```

## Listas com Keys Estáveis

```typescript
// ✅ Usar ID do banco
{cards.map((card) => (
  <CardItem key={card.id} card={card} onVote={handleVote} onDelete={handleDelete} />
))}

// ❌ Nunca usar índice como key
{cards.map((card, index) => (
  <CardItem key={index} card={card} />
))}
```

## useEffect Correto

```typescript
// ✅ Dependências corretas + cleanup
useEffect(() => {
  const controller = new AbortController();

  async function fetchCards(): Promise<void> {
    try {
      const response = await fetch(`/api/retros/${retroId}/cards`, {
        signal: controller.signal,
      });
      const { data } = await response.json();
      setCards(data);
    } catch (err) {
      if (!controller.signal.aborted) {
        setError('Falha ao carregar cards');
      }
    }
  }

  fetchCards();
  return () => controller.abort();
}, [retroId]);
```

## Acessibilidade

```typescript
// ✅ Elementos semânticos + ARIA
<main aria-label="Retrospectiva">
  <section aria-labelledby="went-well-heading">
    <h2 id="went-well-heading">O que foi bem</h2>
    <ul role="list">
      {cards.map(card => <li key={card.id}>{card.text}</li>)}
    </ul>
  </section>
</main>

// ✅ Formulário acessível
<form onSubmit={handleSubmit} aria-label="Adicionar card">
  <label htmlFor="card-text">Texto do card</label>
  <textarea
    id="card-text"
    value={text}
    onChange={(e) => setText(e.target.value)}
    required
    aria-describedby="card-help"
  />
  <span id="card-help">Máximo 500 caracteres</span>
  <button type="submit">Adicionar</button>
</form>
```
