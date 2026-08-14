package br.com.runly.controller;

import br.com.runly.dto.EnviarMensagemRequest;
import br.com.runly.dto.MensagemGrupoResponse;
import br.com.runly.dto.UsuarioResponse;
import br.com.runly.service.grupo.GrupoService;
import br.com.runly.service.grupo.MensagemGrupoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GrupoControllerChatTest {

    @Mock
    private GrupoService grupoService;

    @Mock
    private MensagemGrupoService mensagemService;

    @Mock
    private Principal principal;

    @InjectMocks
    private GrupoController grupoController;

    @BeforeEach
    void setUp() {
        when(principal.getName()).thenReturn("vitor@runly.com");
    }

    @Test
    @DisplayName("POST /grupos/{id}/mensagens deve chamar service e retornar 200 OK")
    void deveEnviarMensagemController() {
        // Arrange
        EnviarMensagemRequest request = new EnviarMensagemRequest("Treino confirmado!");
        UsuarioResponse autor = new UsuarioResponse(1L, "Vitor", "vitor@runly.com", null, null, null);
        MensagemGrupoResponse mockResponse = new MensagemGrupoResponse(
                50L,
                10L,
                "Treino confirmado!",
                LocalDateTime.now(),
                autor
        );

        when(mensagemService.enviarMensagem(10L, "vitor@runly.com", request)).thenReturn(mockResponse);

        // Act
        ResponseEntity<MensagemGrupoResponse> response = grupoController.enviarMensagem(10L, request, principal);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(50L, response.getBody().id());
        assertEquals("Treino confirmado!", response.getBody().texto());
        verify(mensagemService, times(1)).enviarMensagem(10L, "vitor@runly.com", request);
    }

    @Test
    @DisplayName("GET /grupos/{id}/mensagens deve listar mensagens com sucesso")
    void deveListarMensagensController() {
        // Arrange
        UsuarioResponse autor = new UsuarioResponse(1L, "Vitor", "vitor@runly.com", null, null, null);
        MensagemGrupoResponse msg = new MensagemGrupoResponse(
                50L,
                10L,
                "Treino confirmado!",
                LocalDateTime.now(),
                autor
        );

        when(mensagemService.listarMensagens(10L, "vitor@runly.com")).thenReturn(List.of(msg));

        // Act
        ResponseEntity<List<MensagemGrupoResponse>> response = grupoController.listarMensagens(10L, principal);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Treino confirmado!", response.getBody().get(0).texto());
        verify(mensagemService, times(1)).listarMensagens(10L, "vitor@runly.com");
    }
}
