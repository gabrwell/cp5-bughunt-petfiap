package br.com.fiap.petfiap.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RegrasModeloSemCoberturaTest {

    private final LocalDateTime data = LocalDateTime.of(2026, 12, 1, 10, 0);

    @Test
    public void deveCalcularPrecoDoBanhoPorPorte() {
        // Arrange
        Banho pequeno = new Banho(1, "Rex", "PEQUENO", "Ana", data);
        Banho medio = new Banho(2, "Luna", "MEDIO", "Bia", data);
        Banho grande = new Banho(3, "Thor", "GRANDE", "Caio", data);

        // Act + Assert
        assertAll(
                () -> assertEquals(60.0, pequeno.calcularPreco(), 0.001),
                () -> assertEquals(80.0, medio.calcularPreco(), 0.001),
                () -> assertEquals(100.0, grande.calcularPreco(), 0.001));
    }

    @Test
    public void deveDurar60MinutosNaTosa() {
        // Arrange
        Tosa tosa = new Tosa(4, "Rex", "PEQUENO", "Ana", data);

        // Act
        int duracao = tosa.getDuracaoMinutos();

        // Assert
        assertEquals(60, duracao);
    }

    @Test
    public void deveCobrar150ReaisNaConsultaIndependentementeDoPorte() {
        // Arrange
        ConsultaVeterinaria pequeno = new ConsultaVeterinaria(5, "Rex", "PEQUENO", "Ana", data);
        ConsultaVeterinaria grande = new ConsultaVeterinaria(6, "Thor", "GRANDE", "Caio", data);

        // Act + Assert
        assertAll(
                () -> assertEquals(150.0, pequeno.calcularPreco(), 0.001),
                () -> assertEquals(150.0, grande.calcularPreco(), 0.001));
    }
}
