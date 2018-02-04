package nl.frankkie.rgbstrip;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by frankb on 04/02/2018.
 */

public interface LedStripRestService {

    @POST("/")
    Call<ResponseBody> send(@Body RequestBody request);
}
