package com.intent.cardspe.ui;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface APIInterface {
    //    @POST("createOrder")
//    Call<OrderResponse> createOrder(@HeaderMap Map<String, String> headers, @Body OrderRequest orderRequest);



    @POST("create")
    Call<ResponseBody> callCreateOrderAPI(
            @Body RequestBody apiRequest);


}
