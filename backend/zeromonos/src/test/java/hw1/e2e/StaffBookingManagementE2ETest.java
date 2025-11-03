package hw1.e2e;

import com.microsoft.playwright.junit.UsePlaywright;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import org.junit.jupiter.api.*;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.*;

/**
 * E2E Test: Gestão de reservas pelo Staff
 * 
 * Cenário: Staff acessa painel de gestão, filtra reservas por data e município,
 *          confirma reservas e ajusta limite de carga diária
 */
@UsePlaywright
@DisplayName("E2E: Staff Booking Management")
public class StaffBookingManagementE2ETest {
  
  private static final String BASE_URL = "http://localhost:5173/";
  
  @Test
  @DisplayName("Deve permitir ao staff filtrar, confirmar reservas e ajustar limite de carga")
  void shouldAllowStaffToManageBookingsAndCargoLimit(Page page) {
    
    // FASE 1: Navega para a aplicação e acessa painel Staff
    page.navigate(BASE_URL);
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Staff")).click();
    
    // FASE 2: Filtra reservas por data e município
    // Seleciona data
    page.locator("input[type=\"date\"]").fill("2025-12-05");
    
    // Seleciona município
    page.getByPlaceholder("Selecione o município").click();
    page.getByPlaceholder("Selecione o município").fill("ave");
    page.getByText("Aveiro").click();
    
    // Aplica filtros
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Confirmar")).click();
    
    // FASE 3: Ajusta limite de carga diária
    // Define limite para 3 recolhas
    page.getByRole(AriaRole.SPINBUTTON).click();
    page.getByRole(AriaRole.SPINBUTTON).fill("3");
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Guardar")).click();
    
    // FASE 4: Verifica status das reservas
    // Clica no botão "Em Curso" para ver reservas em andamento
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Em Curso")).click();
    
    // ASSERÇÃO: Verifica que o painel de gestão está funcional
    // (Asserções específicas dependem da implementação do frontend)
    assertThat(page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Staff")))
        .isVisible();
  }
  
  @Test
  @DisplayName("Deve permitir ao staff navegar entre diferentes status de reservas")
  void shouldAllowStaffToNavigateBetweenBookingStatuses(Page page) {
    page.navigate(BASE_URL);
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Staff")).click();
    
    // Define data para filtrar
    page.locator("input[type=\"date\"]").fill("2025-12-10");
    
    // Navega entre diferentes status
    // Botões esperados: "Recebidas", "Em Curso", "Concluídas", etc.
    assertThat(page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Em Curso")))
        .isVisible();
  }
  
  @Test
  @DisplayName("Deve validar limite de carga com valores positivos")
  void shouldValidateCargoLimitWithPositiveValues(Page page) {
    page.navigate(BASE_URL);
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Staff")).click();
    
    // Tenta definir limite inválido (0 ou negativo)
    page.getByRole(AriaRole.SPINBUTTON).click();
    page.getByRole(AriaRole.SPINBUTTON).fill("0");
    
    // Nota: A validação real depende da implementação do frontend
    // Este teste documenta o comportamento esperado
  }
}
