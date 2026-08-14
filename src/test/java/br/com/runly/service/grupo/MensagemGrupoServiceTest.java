package br.com.runly.service.grupo;

import br.com.runly.dto.EnviarMensagemRequest;
import br.com.runly.dto.MensagemGrupoResponse;
import br.com.runly.model.Usuario;
import br.com.runly.model.grupo.Grupo;
import br.com.runly.model.grupo.MensagemGrupo;
import br.com.runly.repository.GrupoRepository;
import br.com.runly.repository.MensagemGrupoRepository;
import br.com.runly.service.auth.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MensagemGrupoServiceTest {

    @Mock
    private MensagemGrupoRepository mensagemRepository;

    @Mock
    private GrupoRepository grupoRepository;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private MensagemGrupoService mensagemService;

    private Usuario usuarioMembro;
    private Usuario usuarioNaoMembro;
    private Grupo grupo;

    @BeforeEach
    void setUp() {
        usuarioMembro = new Usuario();
        usuarioMembro.setId(1L);
        usuarioMembro.setNome("Vitor Corredor");
        usuarioMembro.setEmail("vitor@runly.com");

        usuarioNaoMembro = new Usuario();
        usuarioNaoMembro.setId(2L);
        usuarioNaoMembro.setNome("Visitante");
        usuarioNaoMembro.setEmail("visitante@runly.com");

        grupo = new Grupo();
        grupo.setId(10L);
        grupo.setNome("Corredores da Madrugada");
        grupo.setMembros(new ArrayList<>(List.of(usuarioMembro)));
    }

    @Test
    @DisplayName("Deve enviar mensagem com sucesso quando o usuário for membro do grupo")
    void deveEnviarMensagemComSucesso() {
        // Arrange
        EnviarMensagemRequest request = new EnviarMensagemRequest("Bora correr amanhã às 6h!");
        
        when(grupoRepository.findById(10L)).thenReturn(Optional.of(grupo));
        when(usuarioService.buscarPorEmail("vitor@runly.com")).thenReturn(usuarioMembro);
        when(mensagemRepository.save(any(MensagemGrupo.class))).thenAnswer(invocation -> {
            MensagemGrupo msg = invocation.getArgument(0);
            msg.setId(100L);
            msg.setDataEnvio(LocalDateTime.now());
            return msg;
        });

        // Act
        MensagemGrupoResponse response = mensagemService.enviarMensagem(10L, "vitor@runly.com", request);

        // Assert
        assertNotNull(response);
        assertEquals(100L, response.id());
        assertEquals(10L, response.grupoId());
        assertEquals("Bora correr amanhã às 6h!", response.texto());
        assertEquals("Vitor Corredor", response.autor().nome());
        verify(mensagemRepository, times(1)).save(any(MensagemGrupo.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar enviar mensagem se o usuário NÃO for membro")
    void deveBloquearEnvioDeMensagemParaNaoMembro() {
        // Arrange
        EnviarMensagemRequest request = new EnviarMensagemRequest("Mensagem de invasor");
        
        when(grupoRepository.findById(10L)).thenReturn(Optional.of(grupo));
        when(usuarioService.buscarPorEmail("visitante@runly.com")).thenReturn(usuarioNaoMembro);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                mensagemService.enviarMensagem(10L, "visitante@runly.com", request)
        );

        assertTrue(exception.getMessage().contains("Você precisa ser membro"));
        verify(mensagemRepository, never()).save(any(MensagemGrupo.class));
    }

    @Test
    @DisplayName("Deve listar mensagens do grupo quando o usuário for membro")
    void deveListarMensagensComSucesso() {
        // Arrange
        MensagemGrupo msg1 = new MensagemGrupo();
        msg1.setId(1L);
        msg1.setTexto("Primeira mensagem!");
        msg1.setGrupo(grupo);
        msg1.setUsuario(usuarioMembro);
        msg1.setDataEnvio(LocalDateTime.now().minusMinutes(5));

        when(grupoRepository.findById(10L)).thenReturn(Optional.of(grupo));
        when(usuarioService.buscarPorEmail("vitor@runly.com")).thenReturn(usuarioMembro);
        when(mensagemRepository.findByGrupoIdOrderByDataEnvioAsc(10L)).thenReturn(List.of(msg1));

        // Act
        List<MensagemGrupoResponse> mensagens = mensagemService.listarMensagens(10L, "vitor@runly.com");

        // Assert
        assertNotNull(mensagens);
        assertEquals(1, mensagens.size());
        assertEquals("Primeira mensagem!", mensagens.get(0).texto());
        assertEquals(10L, mensagens.get(0).grupoId());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar listar mensagens se o usuário NÃO for membro")
    void deveBloquearListagemDeMensagensParaNaoMembro() {
        // Arrange
        when(grupoRepository.findById(10L)).thenReturn(Optional.of(grupo));
        when(usuarioService.buscarPorEmail("visitante@runly.com")).thenReturn(usuarioNaoMembro);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                mensagemService.listarMensagens(10L, "visitante@runly.com")
        );

        assertTrue(exception.getMessage().contains("Você precisa ser membro"));
        verify(mensagemRepository, never()).findByGrupoIdOrderByDataEnvioAsc(any());
    }
}
