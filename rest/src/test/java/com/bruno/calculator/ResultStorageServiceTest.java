package com.bruno.calculator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ResultStorageServiceTest {
    @Test
    void storeAndRetrieveFromLocalCache() {
        ResultStorageService service = new ResultStorageService();
        String requestId = "test-id";
        String result = "42";
        assertDoesNotThrow(() -> service.storeResult(requestId, result));
        assertEquals(result, service.getResult(requestId));
    }

    @Test
    void storeAndRetrieveMultipleResults() {
        ResultStorageService service = new ResultStorageService();
        String id1 = "id-1";
        String id2 = "id-2";
        String result1 = "100";
        String result2 = "200";
        service.storeResult(id1, result1);
        service.storeResult(id2, result2);
        assertEquals(result1, service.getResult(id1));
        assertEquals(result2, service.getResult(id2));
    }

    @Test
    void getResultReturnsNullIfNotStored() {
        ResultStorageService service = new ResultStorageService();
        assertEquals(null, service.getResult("not-exists"));
    }
}

