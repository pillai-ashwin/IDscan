package siesgst.tml17.idscan;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    String barcode_scan;
    SessionManager sessionManager;
    EditText username,password;
    Button login;
    private Request request;
    String responseString;
//    BackgroundWorker backgroundWorker

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        //scan();
        Login_try();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Log.d("MainActivity", "cancelled scan");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Log.d("MainActivity", "Scanned");
                barcode_scan = result.getContents();
                Toast.makeText(this, "Scanned: " + barcode_scan, Toast.LENGTH_LONG).show();



            }

        } else {
            //to pass result to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    //Function to try checking database for event head record
    //Login try can be modified to display splash screen welcoming event head to his administration page
    public void Login_try(){
        //String scanned_code = barcode_scan;
        //String type = "login";
        /*backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(backgroundWorker.username,backgroundWorker.password);
        */
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // Post post = new Post(username.getText().toString(),password.getText().toString());
                //String response = post.postToDb();
                String url = "http://192.168.1.36/login.php";
                OkHttpClient client = new OkHttpClient();
                RequestBody body = new FormBody.Builder()
                        .add("email", username.getText().toString())
                        .add("password", password.getText().toString())
                        .build();
                request = new Request.Builder()
                        .url(url)
                        .method("POST", body.create(null, new byte[0]))
                        .post(body)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        responseString = response.body().string();
                        Log.v("response", responseString);
                        if (responseString.contains("true"))
                            scan();
                    }

                });
            }
        });
    }

    // Function to intialise scan mode
    public void scan(){
        final Activity activity = this;
        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Set over barcode of SIES GST College ID");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();

    }

}