package com.example.demo.service;

import com.example.demo.dto.PessoaDTO;
import com.example.demo.dto.TrabalhoDTO;
import com.example.demo.entity.Pessoa;
import com.example.demo.entity.Trabalho;
import com.example.demo.repository.PessoaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PessoaService {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Transactional
    public PessoaDTO salvar(PessoaDTO dto) {
        Pessoa pessoa = new Pessoa();
        pessoa.setNome(dto.getNome());
        pessoa.setCpf(dto.getCpf());

        if (dto.getTrabalhos() != null) {
            List<Trabalho> trabalhos = dto.getTrabalhos().stream().map(t -> {
                Trabalho trabalho = new Trabalho();
                trabalho.setEndereco(t.getEndereco());
                trabalho.setPessoa(pessoa);
                return trabalho;
            }).collect(Collectors.toList());

            pessoa.setTrabalhos(trabalhos);
        }

        return toDTO(pessoaRepository.save(pessoa));
    }

    public PessoaDTO buscarPorId(Long id) {
        Pessoa pessoa = pessoaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pessoa não encontrada"));
        return toDTO(pessoa);
    }

    public List<PessoaDTO> listarTodos() {
        return pessoaRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PessoaDTO atualizar(Long id, PessoaDTO dto) {
        Pessoa pessoa = pessoaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pessoa não encontrada"));

        pessoa.setNome(dto.getNome());
        pessoa.setCpf(dto.getCpf());

        // Limpa a lista de trabalhos existente
        if (pessoa.getTrabalhos() != null) {
            pessoa.getTrabalhos().clear();
        }

        // Adiciona os novos trabalhos
        if (dto.getTrabalhos() != null) {
            List<Trabalho> trabalhos = dto.getTrabalhos().stream().map(t -> {
                Trabalho trabalho = new Trabalho();
                trabalho.setEndereco(t.getEndereco());
                trabalho.setPessoa(pessoa);
                return trabalho;
            }).collect(Collectors.toList());

            pessoa.setTrabalhos(trabalhos);
        }

        return toDTO(pessoaRepository.save(pessoa));
    }

    @Transactional
    public void deletar(Long id) {
        pessoaRepository.deleteById(id);
    }

    private PessoaDTO toDTO(Pessoa pessoa) {
        PessoaDTO dto = new PessoaDTO();
        dto.setId(pessoa.getId());
        dto.setNome(pessoa.getNome());
        dto.setCpf(pessoa.getCpf());
        
        if (pessoa.getTrabalhos() != null) {
            dto.setTrabalhos(pessoa.getTrabalhos().stream()
                    .map(t -> {
                        TrabalhoDTO tDTO = new TrabalhoDTO();
                        tDTO.setId(t.getId());
                        tDTO.setEndereco(t.getEndereco());
                        return tDTO;
                    })
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }
} 