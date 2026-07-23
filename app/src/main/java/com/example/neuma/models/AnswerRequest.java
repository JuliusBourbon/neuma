package com.example.neuma.models;

public class AnswerRequest {
    private String questionId;
    private String userAnswer;

    public AnswerRequest(String questionId, String userAnswer) {
        this.questionId = questionId;
        this.userAnswer = userAnswer;
    }

    public String getQuestionId() { return questionId; }
    public String getUserAnswer() { return userAnswer; }
}
