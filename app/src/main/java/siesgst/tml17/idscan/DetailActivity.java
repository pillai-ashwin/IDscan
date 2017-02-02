package siesgst.tml17.idscan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

public class DetailActivity extends AppCompatActivity {

    Button scan;
    String barcode_scan;
    private Request request;
    String responseString;
    SessionManager session;
    TextView EventIdName;
    TextView Email;
    TextView Event;
    TextView Contact_number;
    Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        session=new SessionManager(DetailActivity.this);
        scan = (Button)findViewById(R.id.Scan);
        EventIdName=(TextView) findViewById(R.id.head_name);
        Email=(TextView) findViewById(R.id.head_email);
        Event=(TextView) findViewById(R.id.event_name);
        Contact_number=(TextView) findViewById(R.id.head_contact);
        logout=(Button)findViewById(R.id.logout_id);
        EventIdName.setText(session.getName());
        Email.setText(session.getEmail());
        Event.setText(session.getEventName());
        Contact_number.setText(session.getContact());
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scan();
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DetailActivity.this,MainActivity.class));

            }
        });
    }
    public void scan(){
        final Activity activity = this;
        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Set over barcode of SIES GST College ID");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
        integrator.setOrientationLocked(true);
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



                    Intent intent = getIntent();
                    String id1 = intent.getStringExtra("id");


                String url = "http://192.168.43.221/play.php";
                OkHttpClient client = new OkHttpClient();

                RequestBody body = new FormBody.Builder()
                        .add("prn", barcode_scan)
                        .add("event_id", id1)
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
//                        if (responseString.contains("true")) {

                            //JSONObject root = null;
                            /*try {
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
                                    //Long created_at = Long.parseLong(created);
                                    //Long updated_at = Long.parseLong(updated);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }*/
                            //Intent intent = new Intent(MainActivity.this,DetailActivity.class);
//                            intent.putExtra("id",event_id);
//                            startActivity(intent);
//                            progressDialog.dismiss();
                            scan();
                        }
                    });
        }
            }
    }
//        } else {
            //to pass result to the fragment
            //super.onActivityResult(requestCode, resultCode, data);
        }
