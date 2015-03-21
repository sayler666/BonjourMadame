package com.sayler.bonjourmadame.network;

import com.sayler.bonjourmadame.network.model.BaseParseResponse;
import com.sayler.bonjourmadame.network.model.MadameDto;
import retrofit.http.Headers;
import retrofit.http.POST;
import rx.Observable;

public interface BonjourMadameAPI {

  @Headers({
      "X-Parse-Application-Id:o0Q0QAqyW2b9RBrpdo5rOXqYUPkifIIbjztUM3sK",
      "X-Parse-REST-API-Key:sJYu1lgErfakMR7MvY6IqPPie0dgF2zif4ZcE0ov",
      "Content-Type: application/json"
  })
  @POST("/1/functions/getRandomImage")
  Observable<BaseParseResponse<MadameDto>> getRandomMadame();
}
