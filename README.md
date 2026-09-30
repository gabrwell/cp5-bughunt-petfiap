# Checkpoint 5 - Bug Hunt PetFiap

## Identificacao

**Grupo:** PENDENTE - preencher antes da entrega

| Integrante | RM | Turma |
|---|---|---|
| PENDENTE | PENDENTE | PENDENTE |

| Campo | Resultado |
|---|---|
| **Total de bugs corrigidos** | 12 / 12 |
| **Total de ajustes de Clean Code** | 6 / 6 |
| **Total de testes novos escritos** | 6 / 6 |
| **Suite final (`mvn test`)** | 26 testes, 0 falhas |

---

## Parte 1 - Bugs encontrados

| # | Sintoma observado (o que fiz/vi) | Causa raiz (arquivo e linha aproximada) | Correcao aplicada | Conceito da disciplina |
|---|---|---|---|---|
| bug01 | O Builder criava um atendimento cujo nome do pet era `null`. | `AtendimentoBuilder.java:24`: `petNome = petNome` atribuía o parametro a ele mesmo. | Troquei por `this.petNome = petNome`. | Builder, atributos e uso de `this`. |
| bug02 | Era possivel construir atendimento sem nome ou sem porte; dois testes esperavam excecao. | `AtendimentoBuilder.construir()`: nao havia validacao dos campos obrigatorios. | Validei `null` e texto em branco para nome e porte antes de chamar a Factory. | Encapsulamento, invariantes e fail-fast. |
| bug03 | Ao pedir `TOSA`, a Factory devolvia um objeto `Banho`. | `AtendimentoFactory.java:18`: o `case "TOSA"` chamava o construtor errado. | O caso agora instancia `Tosa`. | Factory Method e polimorfismo. |
| bug04 | A consulta tinha nome, porte, tutor e data `null`, alem de protocolo zero. | `ConsultaVeterinaria.java:17`: o construtor chamava `super()` em vez de encaminhar os dados. | Passei todos os parametros para o construtor da superclasse. | Heranca e encadeamento de construtores. |
| bug05 | `getInstancia()` devolvia objetos diferentes e o protocolo sempre recomecava em 1. | `GeradorProtocolo`: uma nova instancia era criada, mas nunca guardada no campo estatico. | Criei uma instancia unica `static final`, retornei-a sempre e sincronizei `proximo()`. | Padrao Singleton, estado global e concorrencia. |
| bug06 | Banho pequeno custava R$ 100 e grande R$ 60, ao contrario do contrato. | `Banho.calcularPreco()`: os valores dos extremos estavam invertidos. | Ajustei para pequeno R$ 60, medio R$ 80 e grande R$ 100. | Polimorfismo e regra de negocio. |
| bug07 | A tosa retornava a duracao padrao de 30 minutos em vez de 60. | `Tosa`: existia `getDuracaoMinutos(String)`, uma sobrecarga, nao a sobrescrita do metodo sem parametro. | Removi o parametro e acrescentei `@Override`. | Sobrescrita versus sobrecarga. |
| bug08 | Dois valores iguais de pet e horario, vindos de objetos diferentes, nao geravam conflito. | `AgendaService.java:34`: Strings e `LocalDateTime` eram comparados com `==`. | Passei a comparar os valores com `.equals()`. | Igualdade por referencia versus igualdade por valor. |
| bug09 | Buscar um id inexistente devolvia `null` em vez de lancar `AtendimentoNaoEncontradoException`. | `AgendaService.buscarPorId()`: `catch (Exception)` capturava inclusive a excecao criada pelo `orElseThrow`. | Removi o `try/catch` generico e deixei a excecao chegar ao chamador. | Excecoes unchecked e tratamento no nivel correto. |
| bug10 | O servico aceitava agendamento em data/hora passada e consultava o banco. | `AgendaService.agendar()`: faltava validar `dataHora` antes de usar o repository. | Inclui validacao com `isBefore(LocalDateTime.now())` antes de qualquer acesso ao banco. | Validacao de dominio e fail-fast. |
| bug11 | Um atendimento concluido ou cancelado podia ser cancelado novamente. | `Atendimento.cancelar()`: o status era alterado sem conferir a transicao permitida. | Restrigi o cancelamento ao estado `AGENDADO` e lancei `StatusInvalidoException` nos demais. | Maquina de estados e invariantes. |
| bug12 | Um atendimento novo chegava ao JPA com `id` nulo e sem estrategia de geracao. | `Atendimento.java:15`: havia `@Id`, mas nao `@GeneratedValue`. | Adicionei `@GeneratedValue(strategy = GenerationType.IDENTITY)`. | JPA, chave primaria e persistencia. |

## Parte 2 - Ajustes de Clean Code

| # | Onde estava | Qual principio/boa pratica era violado | O que eu mudei |
|---|---|---|---|
| clean01 | `AtendimentoFactory.criar()` | Parametros de uma letra (`p`, `t`, `n`, `po`, `tu`, `d`) escondiam o significado. | Renomeei para `protocolo`, `tipo`, `petNome`, `petPorte`, `tutorNome` e `dataHora`. |
| clean02 | `AgendaService.repository` | Injecao em campo deixava a dependencia oculta e mutavel. | Tornei o campo `final` e usei injecao por construtor. |
| clean03 | `AtendimentoController.service` | O controller tambem dependia de injecao em campo. | Tornei o campo `final` e recebi `AgendaService` pelo construtor. |
| clean04 | `AgendaService.agendar()` | `System.out.println` nao oferece niveis nem integracao com o logging da aplicacao. | Troquei a impressao por SLF4J com mensagem parametrizada. |
| clean05 | Construtor de `GeradorProtocolo` | O model tinha efeito colateral de escrever no console ao ser criado. | Removi o `println`; construir o Singleton agora apenas inicializa seu estado. |
| clean06 | Final de `AtendimentoController` | Havia metodo privado de fidelidade nunca chamado e requisitos futuros comentados. | Removi o codigo morto; uma regra futura deve entrar apenas quando aprovada e testada. |

## Parte 3 - Testes novos (regras que estavam sem cobertura)

Os 20 testes recebidos nao foram alterados. Os seis novos ficam nas classes
`RegrasModeloSemCoberturaTest` e `AgendaServiceRegrasSemCoberturaTest`.

| # | Teste escrito (classe.metodo) | Regra coberta | Resultado contra o codigo original |
|---|---|---|---|
| teste01 | `RegrasModeloSemCoberturaTest.deveCalcularPrecoDoBanhoPorPorte` | Banho custa R$ 60/R$ 80/R$ 100 para pequeno/medio/grande. | Vermelho: revelou o bug06. |
| teste02 | `RegrasModeloSemCoberturaTest.deveDurar60MinutosNaTosa` | Tosa dura 60 minutos. | Vermelho: revelou o bug07. |
| teste03 | `RegrasModeloSemCoberturaTest.deveCobrar150ReaisNaConsultaIndependentementeDoPorte` | Consulta custa R$ 150 independentemente do porte. | Verde: a regra ja estava correta. |
| teste04 | `AgendaServiceRegrasSemCoberturaTest.deveRecusarAgendamentoNoPassadoSemConsultarBanco` | Data/hora passada deve ser recusada sem consultar nem salvar. | Vermelho: revelou o bug10. |
| teste05 | `AgendaServiceRegrasSemCoberturaTest.deveCancelarAtendimentoAgendado` | `AGENDADO` pode mudar para `CANCELADO` e deve ser salvo. | Verde: a transicao valida ja funcionava. |
| teste06 | `AgendaServiceRegrasSemCoberturaTest.deveRecusarCancelamentoDeAtendimentoConcluido` | `CONCLUIDO` nao pode ser cancelado nem salvo novamente. | Vermelho: revelou o bug11. |

---

## Parte 4 - Perguntas de reflexao

### 1. A suite como contrato (Aula 15)

Comecei rodando os 20 testes e confirmei as 9 falhas descritas no enunciado.
Mensagens como `expected: <Rex> but was: <null>` levaram diretamente ao Builder,
enquanto `expected Tosa but was Banho` apontou para o `case` errado da Factory.
Corrigi uma causa por vez e rodei a suite novamente para detectar regressoes.
Com `curl`, eu teria de preparar requests e conferir respostas manualmente, alem
de depender da API e do banco. A suite e repetivel, rapida e testa tambem excecoes,
tipos concretos e interacoes como `verify(repository, never()).save(any())`.

### 2. Mock e injecao de dependencia (Aulas 13 a 15)

Em producao, o Spring encontra `AgendaService` por causa de `@Service`, cria o
`AtendimentoRepository` e o entrega ao construtor do service. No teste, quem faz
esse papel e o Mockito: `@Mock` cria o repository falso e `@InjectMocks` monta o
service com esse objeto. Por isso o mesmo construtor expressa a dependencia nos
dois ambientes. Os `when(...)` definem respostas controladas em memoria e os
`verify(...)` conferem as chamadas, sem iniciar Spring, Oracle, rede ou tabela.

### 3. `==` vs `.equals()` (Aula 7)

`==` verifica se duas variaveis apontam para o mesmo objeto na memoria; nao se o
conteudo e igual. Literais como `"Rex"` podem parecer funcionar por causa do pool
de Strings da JVM, que reutiliza algumas referencias, mas uma String recebida em
outra requisicao pode ser outro objeto. O teste tambem recriou o mesmo horario com
`LocalDateTime.parse`, obtendo outra instancia de valor igual. Em `AgendaService`,
troquei as duas comparacoes por `.equals()`, entao o conflito depende do nome e do
instante representados, nao da identidade acidental dos objetos.

### 4. Sobrescrita vs sobrecarga (Aula 7)

Sobrescrever significa fornecer, na subclasse, a mesma assinatura do metodo da
superclasse. Sobrecarregar significa criar outro metodo, com parametros diferentes.
`Atendimento` declara `getDuracaoMinutos()` sem parametros, mas `Tosa` tinha
`getDuracaoMinutos(String porte)`: por isso chamadas polimorficas usavam os 30
minutos herdados. Corrigi para o metodo sem parametro e adicionei `@Override`.
Se a assinatura voltar a divergir, o compilador passa a acusar o erro imediatamente.

### 5. Singleton manual vs bean do Spring (Aula 14)

`GeradorProtocolo` deve fornecer a mesma instancia para toda a aplicacao, mantendo
um unico contador. O bug criava um objeto novo em cada `getInstancia()`, entao a
identidade mudava e a sequencia reiniciava. A instancia agora e `static final` e
`proximo()` e sincronizado para evitar perda de incremento concorrente. Ja o
`AgendaService` e gerenciado pelo container: `@Service` tem escopo singleton por
padrao, e o Spring controla sua criacao e injeta o repository pelo construtor.

### 6. Cobertura de testes: onde parar? (Aula 15)

Vale manter tambem os testes que nasceram verdes: eles registram regras importantes,
como o preco fixo da consulta e o cancelamento valido, e impedem regressoes futuras.
Em projeto real eu priorizaria primeiro os caminhos de maior risco: dinheiro,
persistencia, transicoes de status e erros que poderiam salvar dados invalidos.
Depois cobriria o caminho feliz essencial e casos de fronteira. Buscar 100% apenas
como numero pode gerar testes frageis e de pouco valor; cobertura ajuda a localizar
lacunas, mas a prioridade deve ser proteger comportamento de negocio observavel.

---

## Parte 5 - Espaco livre (opcional)

O principal aprendizado foi separar sintoma de causa raiz. Algumas falhas tinham
efeito em cascata: o Singleton defeituoso quebrava identidade e sequencia; a
assinatura errada na Tosa compilava normalmente, mas anulava o polimorfismo.

## Como executar

Requisito: JDK 17 ou superior e Maven.

```bash
mvn test
```

Resultado esperado: `Tests run: 26, Failures: 0, Errors: 0, Skipped: 0`.
