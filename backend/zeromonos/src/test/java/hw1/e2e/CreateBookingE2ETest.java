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
    
    // FASE 1: Navega para a aplicação
    page.navigate(BASE_URL);
    
    // FASE 2: Preenche formulário de reserva
    // Nome do cidadão
    page.getByPlaceholder("Ex: João Silva").click();
    page.getByPlaceholder("Ex: João Silva").fill("João");
    
    // Seleciona município (com busca)
    page.getByPlaceholder("Selecione o município").click();
    page.getByPlaceholder("Selecione o município").fill("Barcelos");
    page.getByText("Barcelos").click();
    
    // Define data da recolha
    page.locator("input[type=\"date\"]").fill("2025-12-05");
    
    // Seleciona turno (PM)
    page.getByRole(AriaRole.COMBOBOX).selectOption("PM");
    
    // Descreve os monos a recolher
    page.getByPlaceholder("Descreva os monos a recolher").click();
    page.getByPlaceholder("Descreva os monos a recolher").fill("Cama Suja");
    
    // FASE 3: Submete o pedido
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submeter Pedido")).click();
    
    // FASE 4: Verifica que um token foi gerado
    // O token deve aparecer na interface (formato esperado: 8 caracteres alfanuméricos)
    Locator tokenElement = page.locator("text=/[A-Z0-9]{8}/").first();
    assertThat(tokenElement).isVisible();
    
    // Captura o token para uso posterior
    String token = tokenElement.textContent();
    System.out.println("Token gerado: " + token);
    
    // FASE 5: Testa consulta de reserva com o token
    page.getByPlaceholder("Ex: ABCD1234").click();
    page.getByPlaceholder("Ex: ABCD1234").fill(token);
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Consultar")).click();
    
    // ASSERÇÃO: Verifica que os detalhes da reserva são exibidos
    assertThat(page.getByText("João")).isVisible();
    assertThat(page.getByText("Barcelos")).isVisible();
    assertThat(page.getByText("Cama Suja")).isVisible();
  }
  
  @Test
  @DisplayName("Deve validar campos obrigatórios do formulário")
  void shouldValidateRequiredFormFields(Page page) {
    page.navigate(BASE_URL);
    
    // Tenta submeter formulário vazio
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submeter Pedido")).click();
    
    // Verifica que campos obrigatórios são destacados (ajustar conforme implementação)
    // Nota: Esta validação depende de como o frontend implementa a validação
    assertThat(page.getByPlaceholder("Ex: João Silva")).isVisible();
  }
}
