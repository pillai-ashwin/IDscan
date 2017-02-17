package siesgst.tml17.idscan;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    SessionManager session;
    EditText username,password;
    TextView login;
    private Request request;
    String responseString;
    String event_id;
//    BackgroundWorker backgroundWorker

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        //scan();
        //android.base64.encode(NO_WARP);
        session=new SessionManager(MainActivity.this);
        Toolbar toolbar =(Toolbar)findViewById(R.id.toolbar);
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
        login = (TextView) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setCancelable(true);
                progressDialog.setMessage("Signing In...");
                Log.v("prog?","prog.");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
                String url = "http://192.168.43.10/login.php";
                if(username.getText().toString().equals("a")&&password.getText().toString().equals("a")){
                    startActivity(new Intent(MainActivity.this,DetailActivity.class));

                }
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
                Log.v("login",body.toString());
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("Nigga","Fail");
                        e.printStackTrace();
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        responseString = response.body().string();
                        Log.v("response", responseString);
                        if (responseString.contains("true")) {
                            JSONObject root = null;
                            try {
                                Log.d("Nigga","response");
                                root = new JSONObject(responseString);
                                String status = root.optString("status");
                                String message = root.optString("message");
                                JSONArray result = root.optJSONArray("result");
                                for(int i=0;i<result.length();i++)
                                {
                                    JSONObject resultArrayObject = result.optJSONObject(i);
                                    String id = resultArrayObject.optString("id");
                                    String fname = resultArrayObject.optString("fname");
                                    String lname = resultArrayObject.optString("lname");

                                    String email = resultArrayObject.optString("email");
                                    String contact = resultArrayObject.optString("contact");
                                    event_id = resultArrayObject.optString("event_id");
                                    String event_name = resultArrayObject.optString("event_name");
                                    String created = resultArrayObject.optString("created_at");
                                    String updated = resultArrayObject.optString("updated_at");
                                    session.createLoginSession(fname+"  "+lname,email,event_name,contact,event_id);
                                    //Long created_at = Long.parseLong(created);
                                    //Long updated_at = Long.parseLong(updated);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Intent intent = new Intent(MainActivity.this,DetailActivity.class);
                            startActivity(intent);
                            progressDialog.dismiss();
                            //scan();
                        }
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