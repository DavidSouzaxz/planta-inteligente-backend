package com.projetoiot.plantainteligente.controller;

import com.projetoiot.plantainteligente.dto.AuthRequestDTO;
import com.projetoiot.plantainteligente.dto.LoginResponseDTO;
import com.projetoiot.plantainteligente.entity.Usuario;
import com.projetoiot.plantainteligente.repository.UsuarioRepository;
import com.projetoiot.plantainteligente.infra.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500", "*"})
@Tag(name = "Autenticação", description = "Endpoints para cadastro e login de usuários")
public class AuthController {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/cadastro")
    @Operation(summary = "Cria um novo usuário com senha criptografada")
    public ResponseEntity<?> cadastrar(@RequestBody AuthRequestDTO body) {
        Optional<Usuario> userOpt = repository.findByEmail(body.email());
        
        if (userOpt.isPresent()) {
            return ResponseEntity.badRequest().body("Erro: Este e-mail já está cadastrado!");
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setEmail(body.email());

        novoUsuario.setPassword(passwordEncoder.encode(body.password()));
        
        repository.save(novoUsuario);
        return ResponseEntity.ok("Usuário cadastrado com sucesso!");
    }

    @PostMapping("/login")
    @Operation(summary = "Valida as credenciais e retorna o Token JWT")
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO body) {
        Usuario usuario = repository.findByEmail(body.email())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));


        if (passwordEncoder.matches(body.password(), usuario.getPassword())) {
            String token = tokenService.gerarToken(usuario.getEmail());
            return ResponseEntity.ok(new LoginResponseDTO(token));
        }
        
        return ResponseEntity.status(401).body("Erro: Credenciais inválidas!");
    }
}