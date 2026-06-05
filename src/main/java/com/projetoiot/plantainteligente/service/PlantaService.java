package com.projetoiot.plantainteligente.service;

import com.projetoiot.plantainteligente.dto.PlantaRequestDTO;
import com.projetoiot.plantainteligente.dto.PlantaResponseDTO;
import com.projetoiot.plantainteligente.entity.Planta;

import com.projetoiot.plantainteligente.entity.Usuario;
import com.projetoiot.plantainteligente.repository.UsuarioRepository;
import com.projetoiot.plantainteligente.repository.PlantaRepository; // Adicione o import do seu repositório de planta
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Service
public class PlantaService {

    @Autowired
    private UsuarioRepository usuarioRepository;



    @Autowired
    private PlantaRepository plantaRepository;

    public List<PlantaResponseDTO> listarPlantas() {
        List<Planta> plantas = plantaRepository.findAll();
        return plantas.stream()
                .map(planta -> {;
                    PlantaResponseDTO dto = new PlantaResponseDTO();
                    dto.setId(planta.getId());
                    dto.setUsuarioId(planta.getUsuarioId());
                    dto.setNomePlanta(planta.getNomePlanta());
                    dto.setIcone(planta.getIcone());
                    dto.setTempPlanta(planta.getTempPlanta());
                    dto.setUmidadePlanta(planta.getUmidadePlanta());
                    dto.setSolPlanta(planta.getSolPlanta());
                    return dto;
                })
                .toList();
    }

    public List<PlantaResponseDTO> listarPlantasPorUsuario(@RequestParam Long usuarioId) {
        List<Planta> plantas = plantaRepository.findByUsuarioId(usuarioId)
                .map(List::of) // Envolve a planta encontrada em uma lista
                .orElse(List.of()); // Retorna uma lista vazia se nenhuma planta for encontrada;

        return plantas.stream().map(planta -> {;
                    PlantaResponseDTO dto = new PlantaResponseDTO();
                    dto.setId(planta.getId());
                    dto.setUsuarioId(planta.getUsuarioId());
                    dto.setNomePlanta(planta.getNomePlanta());
                    dto.setIcone(planta.getIcone());
                    dto.setTempPlanta(planta.getTempPlanta());
                    dto.setUmidadePlanta(planta.getUmidadePlanta());
                    dto.setSolPlanta(planta.getSolPlanta());
                    return dto;
                })
                .toList();
    }

    public PlantaResponseDTO salvar(PlantaRequestDTO plantaRequestDTO) {
        // 1. Busca o usuário dono do Token para garantir o vínculo correto
        Usuario usuario = usuarioRepository.findById(plantaRequestDTO.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // 2. Cria uma nova planta e vincula ao usuário
        Planta planta = new Planta();
        planta.setUsuarioId(usuario.getId());
        planta.setNomePlanta(plantaRequestDTO.getNomePlanta());
        planta.setIcone(plantaRequestDTO.getIcone());
        planta.setTempPlanta(plantaRequestDTO.getTempPlanta());
        planta.setUmidadePlanta(plantaRequestDTO.getUmidadePlanta());
        planta.setSolPlanta(plantaRequestDTO.getSolPlanta());
        plantaRepository.save(planta);

        PlantaResponseDTO dto = new PlantaResponseDTO();
        dto.setId(planta.getId());
        dto.setUsuarioId(usuario.getId());
        dto.setNomePlanta(plantaRequestDTO.getNomePlanta());
        dto.setIcone(plantaRequestDTO.getIcone());
        dto.setTempPlanta(plantaRequestDTO.getTempPlanta());
        dto.setSolPlanta(plantaRequestDTO.getSolPlanta());
        dto.setUmidadePlanta(plantaRequestDTO.getUmidadePlanta());
        // 3. Salva a entidade planta no banco Neon
        return dto;
    }

    public PlantaResponseDTO salvarOuAtualizarPlanta(Long userId, PlantaRequestDTO dto) {
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
        if(dto.getTempPlanta() != null) {
            planta.setTempPlanta(dto.getTempPlanta());
        }
        if(dto.getUmidadePlanta() != null) {
            planta.setUmidadePlanta(dto.getUmidadePlanta());
        }
        if (dto.getSolPlanta() != null) {
            planta.setSolPlanta(dto.getSolPlanta());
        }

        plantaRepository.save(planta);
        PlantaResponseDTO dto2 = new PlantaResponseDTO();
        dto2.setId(planta.getId());
        dto2.setUsuarioId(usuario.getId());
        dto2.setNomePlanta(planta.getNomePlanta());
        dto2.setIcone(planta.getIcone());
        dto2.setTempPlanta(planta.getTempPlanta());
        dto2.setUmidadePlanta(planta.getUmidadePlanta());
        dto2.setSolPlanta(planta.getSolPlanta());


        // 4. Salva a entidade planta (atualizando ou inserindo no banco Neon)
        return dto2;
    }
}