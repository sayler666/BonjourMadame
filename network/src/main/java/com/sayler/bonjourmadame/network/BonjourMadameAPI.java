package com.sayler.bonjourmadame.network;

import com.sayler.bonjourmadame.network.model.BaseParseResponse;
import com.sayler.bonjourmadame.network.model.Madame;
import retrofit.http.GET;
import rx.Observable;

public interface BonjourMadameAPI {

  @GET("/1/functions/getRandomImage")
  Observable<BaseParseResponse<Madame>> getRandomMadame();
}
