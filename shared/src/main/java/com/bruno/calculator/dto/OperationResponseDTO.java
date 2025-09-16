package com.bruno.calculator.dto;

public class OperationResponseDTO {
    private String requestId;
    private String resultUrl;
    private String status;
    private String result;
    private String errorMessage;

    public OperationResponseDTO() {}

    // Constructor para resposta de sucesso
    public OperationResponseDTO(String requestId, String status, String result) {
        this.requestId = requestId;
        this.status = status;
        this.result = result;
    }

    // Constructor para resposta de erro
    public OperationResponseDTO(String requestId, String status, String errorMessage, boolean isError) {
        this.requestId = requestId;
        this.status = status;
        this.errorMessage = errorMessage;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getResultUrl() {
        return resultUrl;
    }

    public void setResultUrl(String resultUrl) {
        this.resultUrl = resultUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "OperationResponseDTO{" +
                "requestId='" + requestId + '\'' +
                ", status='" + status + '\'' +
                ", result='" + result + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}