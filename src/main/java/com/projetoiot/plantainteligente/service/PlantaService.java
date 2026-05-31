package com.projetoiot.plantainteligente.service;

import com.projetoiot.plantainteligente.entity.Planta;

import com.projetoiot.plantainteligente.entity.Usuario;
import com.projetoiot.plantainteligente.repository.UsuarioRepository;
import com.projetoiot.plantainteligente.repository.PlantaRepository; // Adicione o import do seu repositório de planta
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlantaService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PlantaRepository plantaRepository;

    public Planta salvarOuAtualizarPlanta(Long userId, Planta dto) {
        // 1. Busca o usuário dono do Token para garantir o vínculo correto
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // 2. Busca se JÁ EXISTE uma planta cadastrada para o ID deste usuário
        Optional<Planta> plantaExistente = plantaRepository.findByUsuarioId(usuario.getId());

        Planta planta;

        if (plantaExistente.isPresent()) {
            // Se o quiz JÁ FOI FEITO: reaproveita a planta existente para apenas EDITAR
            planta = plantaExistente.get();
        } else {
            // Se NUNCA FEZ o quiz: CRIA uma nova do zero e vincula o usuário
            planta = new Planta();
        }

        // 3. Atualiza ou insere os dados vindos do formulário do Front
        // Em vez de setar direto, valide se o Front enviou o dado
        if (dto.getNomePlanta() != null) {
            planta.setNomePlanta(dto.getNomePlanta());
        }
        if (dto.getIcone() != null) {
            planta.setIcone(dto.getIcone());
        }
        if (dto.getTipoAmbiente() != null) {
            planta.setTipoAmbiente(dto.getTipoAmbiente());
        }
        if (dto.getNomeUsuario() != null) {
            planta.setNomeUsuario(dto.getNomeUsuario());
        }

        // 4. Salva a entidade planta (atualizando ou inserindo no banco Neon)
        return plantaRepository.save(planta);
    }
}