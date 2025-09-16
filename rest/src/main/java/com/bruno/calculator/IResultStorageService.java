package com.bruno.calculator;

public interface IResultStorageService {
    void storeResult(String requestId, String result);

    String getResult(String requestId);
}

