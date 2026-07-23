package com.example.neuma.network;

import com.example.neuma.models.AnswerRequest;
import com.example.neuma.models.AnswerResponse;
import com.example.neuma.models.AttemptResponse;
import com.example.neuma.models.FinishAttemptResponse;
import com.example.neuma.models.SkipRequest;
import com.example.neuma.models.StartAttemptRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AttemptApi {
    @POST("attempts")
    Call<AttemptResponse> startAttempt(@Body StartAttemptRequest request);

    @POST("attempts/{attemptId}/answers")
    Call<AnswerResponse> submitAnswer(
            @Path("attemptId") String attemptId,
            @Body AnswerRequest request
    );

    @POST("attempts/{attemptId}/skip")
    Call<Void> skipQuestion(
            @Path("attemptId") String attemptId,
            @Body SkipRequest request
    );

    @POST("attempts/{attemptId}/finish")
    Call<FinishAttemptResponse> finishAttempt(@Path("attemptId") String attemptId);
}
