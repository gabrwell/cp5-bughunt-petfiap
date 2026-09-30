package br.com.fiap.petfiap.service;

import br.com.fiap.petfiap.model.Banho;
import br.com.fiap.petfiap.repository.AtendimentoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class AgendaServiceRegrasSemCoberturaTest {

    @Mock
    private AtendimentoRepository repository;

    @InjectMocks
    private AgendaService service;

    @Test
    public void deveRecusarAgendamentoNoPassadoSemConsultarBanco() {
        // Arrange
        Banho atendimentoNoPassado = new Banho(
                10, "Rex", "PEQUENO", "Ana", LocalDateTime.now().minusMinutes(1));

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> service.agendar(atendimentoNoPassado));
        verifyNoInteractions(repository);
    }
}
