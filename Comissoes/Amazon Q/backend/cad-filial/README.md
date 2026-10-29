# cad-filial

## Pré-requisitos

- Java 17+
- Maven 3.8+
- Driver JDBC do Informix (`ifxjdbc.jar`)

---

## 1. Instalar o driver Informix

O driver Informix **não está no Maven Central**. Você precisa:

1. Obter o arquivo `ifxjdbc.jar` (normalmente está em `C:\IBM\Informix\jdbc\lib\` ou no servidor Informix)
2. Copiar o `ifxjdbc.jar` para a pasta `lib/` do projeto:
   ```
   cad-filial/
   └── lib/
       └── ifxjdbc.jar   <-- coloque aqui
   ```

---

## 2. Compilar e rodar

```bash
cd cad-filial
mvn clean package
mvn spring-boot:run
```

A aplicação sobe em: `http://localhost:8080`

---

## 3. Endpoints para testar no Postman

### Listar todas as filiais
```
GET http://localhost:8080/filiais
```

---

### Buscar filial por código
```
GET http://localhost:8080/filiais/001
```

---

### Incluir filial
```
POST http://localhost:8080/filiais
Content-Type: application/json

{
  "filial": "001",
  "descr": "FILIAL CENTRO",
  "ativo": true,
  "codUsuario": "USUARIO01"
}
```

Resposta de sucesso: `201 Created`
```json
{
  "filial": "001",
  "descr": "FILIAL CENTRO",
  "iesAtivo": "S"
}
```

Resposta de erro (filial já existe): `400 Bad Request`
```json
{
  "erro": "Filial já cadastrada!"
}
```

---

### Alterar filial
```
PUT http://localhost:8080/filiais/001
Content-Type: application/json

{
  "filial": "001",
  "descr": "FILIAL CENTRO ATUALIZADA",
  "ativo": false,
  "codUsuario": "USUARIO01"
}
```

Resposta de sucesso: `200 OK`
```json
{
  "filial": "001",
  "descr": "FILIAL CENTRO ATUALIZADA",
  "iesAtivo": "N"
}
```

---

## 4. Campos do body (POST e PUT)

| Campo        | Tipo    | Obrigatório | Descrição                        |
|--------------|---------|-------------|----------------------------------|
| filial       | string  | Sim         | Código da filial                 |
| descr        | string  | Sim         | Descrição da filial              |
| ativo        | boolean | Não         | true = Ativo (S), false = Inativo (N) |
| codUsuario   | string  | Sim         | Código do usuário para o log     |
