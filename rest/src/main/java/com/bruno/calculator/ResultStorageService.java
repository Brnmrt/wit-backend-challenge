package com.bruno.calculator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Este ficheiro seria para uma futura implementação totalmente assincrona de cache local, mas não foi usado
// algo que seria mais escalável seria usar algo como Redis

@Service
public class ResultStorageService implements IResultStorageService {
    private static final Logger logger = LoggerFactory.getLogger(ResultStorageService.class);
    private final Map<String, String> localCache = new ConcurrentHashMap<>();

    public ResultStorageService() {}

    public void storeResult(String requestId, String result) {
        logger.info("A armazenar na cache local - Chave: {}, Valor: {}", requestId, result);
        localCache.put(requestId, result);
    }

    public String getResult(String requestId) {
        logger.debug("A procurar na cache local - Chave: {}", requestId);
        String localValue = localCache.get(requestId);
        if (localValue != null) {
            logger.debug("Valor encontrado na cache local para a chave {}: {}", requestId, localValue);
        }
        return localValue;
    }
}
