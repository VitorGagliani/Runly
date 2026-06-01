package br.com.runly.service;

import br.com.runly.dto.AuthResponse;
import br.com.runly.dto.LoginRequest;
import br.com.runly.dto.RegisterRequest;
import br.com.runly.dto.UsuarioResponse;
import br.com.runly.exception.RegraNegocioException;
import br.com.runly.model.Usuario;
import br.com.runly.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse cadastrar(RegisterRequest request) {
        String emailNormalizado = request.email().trim().toLowerCase();

        if (usuarioRepository.existsByEmail(emailNormalizado)) {
            throw new RegraNegocioException("Já existe um usuário cadastrado com esse email");
        }

        Usuario usuario = new Usuario(
                request.nome().trim(),
                emailNormalizado,
                passwordEncoder.encode(request.senha())
        );

        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        String token = jwtService.gerarToken(usuarioSalvo);

        return new AuthResponse(token, UsuarioResponse.fromEntity(usuarioSalvo));
    }

    public AuthResponse login(LoginRequest request) {
        String emailNormalizado = request.email().trim().toLowerCase();

        Usuario usuario = usuarioRepository.findByEmail(emailNormalizado)
                .orElseThrow(() -> new RegraNegocioException("Email ou senha inválidos"));

        boolean senhaCorreta = passwordEncoder.matches(request.senha(), usuario.getSenha());
        if (!senhaCorreta) {
            throw new RegraNegocioException("Email ou senha inválidos");
        }

        String token = jwtService.gerarToken(usuario);
        return new AuthResponse(token, UsuarioResponse.fromEntity(usuario));
    }
}
