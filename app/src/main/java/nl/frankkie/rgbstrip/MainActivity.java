package nl.frankkie.rgbstrip;

import android.content.Context;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static LedStripRestService restService;
    private static OkHttpClient httpClient;
    String baseUrl = "";
    String ip = "";
    SeekBar sbRed;
    SeekBar sbGreen;
    SeekBar sbBlue;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_main);

        initUI();
    }

    public void initUI() {
        final EditText edIp = findViewById(R.id.ed_ip);
        Button btn = findViewById(R.id.btn_edit_ip);
        sbRed = findViewById(R.id.sb_red);
        sbGreen = findViewById(R.id.sb_green);
        sbBlue = findViewById(R.id.sb_blue);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ipString = edIp.getText().toString();
                ip = ipString;
                baseUrl = "http://" + ip + "/";
                restService = getRestService(baseUrl);
            }
        });

        sbRed.setMax(255);
        sbGreen.setMax(255);
        sbBlue.setMax(255);

        sbRed.setProgress(128);
        sbGreen.setProgress(128);
        sbBlue.setProgress(128);

        sbRed.setOnSeekBarChangeListener(seekBarListener);
        sbGreen.setOnSeekBarChangeListener(seekBarListener);
        sbBlue.setOnSeekBarChangeListener(seekBarListener);
    }

    private SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser){
                return;
            }
            if (restService != null) {
                int r = sbRed.getProgress();
                int g = sbGreen.getProgress();
                int b = sbBlue.getProgress();
                String content = "" + r + "," + g + "," + b;
                RequestBody rb = RequestBody.create(MediaType.parse("text/plain"),content);
                restService.send(rb).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Toast.makeText(context, "OK", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(context, "Error!", Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                    }
                });
            } else {
                Toast.makeText(context, "Druk eerst op Wijzig IP", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };


    public static LedStripRestService getRestService(String baseUrl) {
        if (restService == null) {
            OkHttpClient client = getHttpClient();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            restService = retrofit.create(LedStripRestService.class);
        }
        return restService;
    }

    public static OkHttpClient getHttpClient() {
        if (httpClient == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            if (BuildConfig.DEBUG) {
                //only in debug, because will log http-body.
                builder.addInterceptor(interceptor);
            }
            OkHttpClient client = builder.build();
            httpClient = client;
        }
        return httpClient;
    }
}
