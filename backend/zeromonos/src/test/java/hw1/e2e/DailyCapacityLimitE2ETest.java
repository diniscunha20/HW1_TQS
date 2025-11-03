package hw1.e2e;

import com.microsoft.playwright.junit.UsePlaywright;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import org.junit.jupiter.api.*;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.*;

/**
 * E2E Test: Validar limite de capacidade diária de recolhas
 * 
 * Cenário: Staff configura capacidade diária para 2 recolhas
 *          Cidadão faz 2 reservas com sucesso (AM e PM)
 *          Terceira tentativa de reserva deve ser rejeitada
 */
@UsePlaywright
@DisplayName("E2E: Daily Capacity Limit")
public class DailyCapacityLimitE2ETest {
  
  private static final String BASE_URL = "http://localhost:5173/";
  private static final String TEST_DATE = "2025-12-12";
  private static final String TEST_MUNICIPALITY = "Aveiro";
  
  @Test
  @DisplayName("Deve rejeitar reserva quando capacidade diária está esgotada")
  void shouldRejectBookingWhenDailyCapacityIsExceeded(Page page) {
    
    // FASE 1: Staff configura capacidade diária para 2 recolhas
    page.navigate(BASE_URL);
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Staff")).click();
    
    // Define limite de 2 recolhas por dia
    page.getByRole(AriaRole.SPINBUTTON).click();
    page.getByRole(AriaRole.SPINBUTTON).fill("2");
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Guardar")).click();
    
    // Volta para a página do cidadão
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Cidadão")).click();
    
    // FASE 2: Primeira reserva (Manuel - turno AM) - Deve ter sucesso
    fillBookingForm(page, "Manuel", TEST_MUNICIPALITY, TEST_DATE, "AM", "colchao");
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submeter Pedido")).click();
    
    // Aguarda confirmação da primeira reserva (implícito - formulário limpa)
    
    // FASE 3: Segunda reserva (Joao - turno PM) - Deve ter sucesso
    fillBookingForm(page, "Joao", TEST_MUNICIPALITY, TEST_DATE, "PM", "colchao");
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submeter Pedido")).click();
    
    // Aguarda confirmação da segunda reserva
    
    // FASE 4: Terceira reserva (Vera - turno AM) - Deve ser REJEITADA
    fillBookingForm(page, "Vera", TEST_MUNICIPALITY, TEST_DATE, "AM", "colchao velho");
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submeter Pedido")).click();
    
    // ASSERÇÃO: Verifica que a mensagem de capacidade esgotada aparece
    assertThat(page.getByText("❌ Capacidade diária esgotada"))
        .isVisible();
  }
  
  /**
   * Método auxiliar para preencher o formulário de reserva
   */
  private void fillBookingForm(Page page, String name, String municipality, 
                                String date, String timeSlot, String description) {
    // Preenche nome
    page.getByPlaceholder("Ex: João Silva").click();
    page.getByPlaceholder("Ex: João Silva").fill(name);
    
    // Seleciona município
    page.getByPlaceholder("Selecione o município").click();
    page.getByPlaceholder("Selecione o município").fill(municipality.substring(0, 3).toLowerCase());
    page.getByText(municipality).click();
    
    // Preenche data
    page.locator("input[type=\"date\"]").fill(date);
    
    // Seleciona turno se for PM (AM é default)
    if ("PM".equals(timeSlot)) {
      page.getByRole(AriaRole.COMBOBOX).selectOption("PM");
    }
    
    // Preenche descrição se fornecida
    if (description != null && !description.isEmpty()) {
      page.getByPlaceholder("Descreva os monos a recolher").click();
      page.getByPlaceholder("Descreva os monos a recolher").fill(description);
    }
  }
}
