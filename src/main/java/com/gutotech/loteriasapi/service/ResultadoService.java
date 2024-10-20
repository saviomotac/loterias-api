package com.gutotech.loteriasapi.service;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.gutotech.loteriasapi.consumer.Consumer;
import com.gutotech.loteriasapi.model.Resultado;
import com.gutotech.loteriasapi.model.ResultadoId;
import com.gutotech.loteriasapi.repository.ResultadoRepository;

@Service
public class ResultadoService {
	
	private static final String CACHE_NAME = "resultados";

	@Autowired
    private CacheManager cacheManager;
	
	@Autowired
	private Consumer consumer;
	
	@Autowired
	private ResultadoRepository repository;

	@Cacheable(CACHE_NAME)
	public List<Resultado> findByLoteria(String loteria) {
		return repository.findById_Loteria(loteria) //
				.stream() //
				.sorted(Comparator.comparing(Resultado::getConcurso).reversed()) //
				.collect(Collectors.toList());
	}

	public Resultado findByLoteriaAndConcurso(String loteria, int concurso) {
		Resultado resultado = repository.findById(new ResultadoId(loteria, concurso)).orElse(null);
		
		if (resultado == null) {
			try {
				resultado = consumer.getResultado(loteria, concurso);
				save(resultado);
			} catch (IOException e) {
				return null;
			}
		}
		
		return resultado;
	}

	public Resultado findLatest(String loteria) {
		return repository.findTopById_Loteria(loteria).orElse(new Resultado());
	}

	public Resultado save(Resultado resultado) {
		Resultado result = repository.save(resultado);
		cacheManager.getCache(CACHE_NAME).clear();
		
		return result;
	}

	public void saveAll(List<Resultado> resultados) {
		repository.saveAll(resultados);
	}

	
	public void deleteAll() {
		repository.deleteAll();
		cacheManager.getCache(CACHE_NAME).clear();
	}
}
