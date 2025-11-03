package hw1.e2e;

import com.microsoft.playwright.junit.UsePlaywright;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.AriaRole;

import org.junit.jupiter.api.*;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.*;

/**
 * E2E Test: Fluxo completo de criação de reserva de cidadão
 * 
 * Cenário: Cidadão acessa o sistema, preenche formulário de reserva,
 *          submete pedido e verifica o token gerado para consulta
 */
@UsePlaywright
@DisplayName("E2E: Create Booking Flow")
public class CreateBookingE2ETest {
  
  private static final String BASE_URL = "http://localhost:5173/";
  
  @Test
  @DisplayName("Deve criar reserva com sucesso e retornar token válido")
  void shouldCreateBookingSuccessfullyAndReturnToken(Page page) {
    page.navigate(BASE_URL);

    page.getByPlaceholder("Ex: João Silva").click();
    page.getByPlaceholder("Ex: João Silva").fill("João");
    
    page.getByPlaceholder("Selecione o município").click();
    page.getByPlaceholder("Selecione o município").fill("Barcelos");
    page.getByText("Barcelos").click();
    page.locator("input[type=\"date\"]").fill("2025-12-05");
    page.getByRole(AriaRole.COMBOBOX).selectOption("PM");
    page.getByPlaceholder("Descreva os monos a recolher").click();
    page.getByPlaceholder("Descreva os monos a recolher").fill("Cama Suja");
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submeter Pedido")).click();
    Locator tokenElement = page.locator("text=/[A-Z0-9]{8}/").first();
    assertThat(tokenElement).isVisible();
    String token = tokenElement.textContent();
    System.out.println("Token gerado: " + token);
    page.getByPlaceholder("Ex: ABCD1234").click();
    page.getByPlaceholder("Ex: ABCD1234").fill(token);
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Consultar")).click();
    assertThat(page.getByText("João")).isVisible();
    assertThat(page.getByText("Barcelos")).isVisible();
    assertThat(page.getByText("Cama Suja")).isVisible();
  }
  @Test
  @DisplayName("Deve validar campos obrigatórios do formulário")
  void shouldValidateRequiredFormFields(Page page) {
    page.navigate(BASE_URL);
    
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submeter Pedido")).click();

    assertThat(page.getByPlaceholder("Ex: João Silva")).isVisible();
  }
}
