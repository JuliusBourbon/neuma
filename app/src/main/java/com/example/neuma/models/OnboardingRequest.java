package com.example.neuma.models;

public class OnboardingRequest {
    private String school;
    private Integer age;
    private String grade;
    private String hobby;

    public OnboardingRequest(String school, Integer age, String grade, String hobby) {
        this.school = school;
        this.age = age;
        this.grade = grade;
        this.hobby = hobby;
    }

    public String getSchool() { return school; }
    public Integer getAge() { return age; }
    public String getGrade() { return grade; }
    public String getHobby() { return hobby; }
}
