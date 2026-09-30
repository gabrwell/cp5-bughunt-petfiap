package br.com.fiap.petfiap.service;

import br.com.fiap.petfiap.exception.StatusInvalidoException;
import br.com.fiap.petfiap.model.Banho;
import br.com.fiap.petfiap.model.Atendimento;
import br.com.fiap.petfiap.repository.AtendimentoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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

    @Test
    public void deveCancelarAtendimentoAgendado() {
        // Arrange
        Banho agendado = new Banho(
                11, "Rex", "PEQUENO", "Ana", LocalDateTime.now().plusDays(1));
        when(repository.findById(1L)).thenReturn(Optional.of(agendado));
        when(repository.save(agendado)).thenReturn(agendado);

        // Act
        Atendimento cancelado = service.cancelar(1L);

        // Assert
        assertEquals("CANCELADO", cancelado.getStatus());
        verify(repository).save(agendado);
    }

    @Test
    public void deveRecusarCancelamentoDeAtendimentoConcluido() {
        // Arrange
        Banho concluido = new Banho(
                12, "Rex", "PEQUENO", "Ana", LocalDateTime.now().plusDays(1));
        concluido.setStatus("CONCLUIDO");
        when(repository.findById(1L)).thenReturn(Optional.of(concluido));

        // Act + Assert
        assertThrows(StatusInvalidoException.class, () -> service.cancelar(1L));
        verify(repository, never()).save(any());
    }
}
