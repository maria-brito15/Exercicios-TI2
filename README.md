## Descrição

Repositório contendo os exercícios desenvolvidos na disciplina Trabalho Interdisciplinar II: Back-End, do curso de Ciência da Computação (PUC Minas).

Os exercícios foram implementados em Java, utilizando a IDE Eclipse, ferramentas como Git e GitHub, PostgreSQL e outros para controle de versão e integração.

Cada pasta representa uma atividade prática com foco em fundamentos de programação orientada a objetos, lógica de programação, manipulação de dados e integração com ambientes de desenvolvimento.

### Exercício 1: Integração Eclipse e GitHub
Esta tarefa consiste na criação de um programa em Java utilizando o ambiente de desenvolvimento Eclipse, sendo que os alunos devem:

1. Criar um novo projeto Java.
2. Criar uma nova classe.
3. Implementar, compilar e executar um programa para somar dos números inteiros

### Exercício 2: Integração PostgreSQL
Esta tarefa consiste na criação de um programa em Java utilizando o ambiente de desenvolvimento Eclipse, sendo que os alunos devem:

1. Criar uma tabela X no SGBDR PostgreSQL.
2. Criar uma classe X em Java.
3. Criar uma classe DAO para manipular a classe X.
4. Criam uma classe Principal contendo um menu com as opções Listar, Inserir, Excluir, Atualizar e Sair para acessar as funções CRUD da DAO.

### Exercício 3: Integração Spark
**Tecnologias Necessárias:** Eclipse, Maven, PgSQL, e Spark Framework.

Esta tarefa consiste na criação de um programa em Java utilizando o ambiente de desenvolvimento Eclipse, sendo que os alunos devem:

1. Criar um formulário HTML.
2. Utilizar o formulário para ler e mostrar elementos de aplicação (produto de um back-end para cadastro de produtos).

**Estrutura de Pastas Principais:**
```
📁 src
└── 📁 main
    ├── 📁 java
    │   └── 📁 com
    │       └── 📁 sparkjava
    │           └── 📁 sparkcore
    │               ├── 📁 app
    │               │   └── App.java
    │               ├── 📁 controller
    │               │   └── ProdutoController.java
    │               ├── 📁 service
    │               │   └── ProdutoService.java
    │               └── 📁 dao
    │                   └── ProdutoDAO.java
    └── 📁 resources
        └── 📁 public
            └── index.html
```

- `App.java:` inicia e roda a aplicação
- `ProdutoDAO.java:` conecta e executa o banco de dados
- `ProdutoController.java:` recebe requisições e chama o service
- `ProdutoService.java:` chama determinada funcionalidade no DAO
- `index.html:` interface
