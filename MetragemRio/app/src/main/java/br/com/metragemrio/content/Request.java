package br.com.metragemrio.content;

import retrofit.http.GET;

public interface Request {

    @GET("/")
    void getData(DataCallback callback);
}
