package com.example.neuma.models;

public class SkipRequest {
    private String questionId;

    public SkipRequest(String questionId) {
        this.questionId = questionId;
    }

    public String getQuestionId() { return questionId; }
}
