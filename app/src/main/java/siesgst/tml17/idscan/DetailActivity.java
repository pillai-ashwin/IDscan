package siesgst.tml17.idscan;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DetailActivity extends AppCompatActivity {
    Button scan;


    boolean flag = false;
    String barcode_scan;
    private Request request;
    String responseString;
    SessionManager session;
    List<Player> player = new ArrayList<Player>();
    RecyclerViewAdapter adapter;
    TextView EventIdName;
    TextView Email;
    TextView Event;
    TextView Contact_number;
    Button logout;
    TextView add;
    RecyclerView rv;
    List<Player> a;
    StringBuffer sb = new StringBuffer();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client2;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_scan) {
            //Toast.makeText(DetailActivity.this,"Scan", Toast.LENGTH_SHORT).show();
            scan();
            //play(barcode_scan);
            return true;
        } else if (id == R.id.action_manually) {
            Log.d("add manually", "in");
            AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
            LayoutInflater inflater = DetailActivity.this.getLayoutInflater();
            final View view = inflater.inflate(R.layout.dialog_box_layout, null);
            // Set up the input
            builder.setView(view);

            final EditText input = (EditText) view.findViewById(R.id.ManualUID);
            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    barcode_scan = input.getText().toString();
                    Toast.makeText(DetailActivity.this, "Input=" + barcode_scan, Toast.LENGTH_LONG).show();
                     play(barcode_scan);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
            return true;
        } else if (id == R.id.update_manually) {

            a = adapter.getselectedList();
            if (a.size() != 0) {
                for (int i = 0; i < a.size(); i++) {
                    String prn = a.get(i).getUID();
                    UIDupdate(prn);
                }
                Toast.makeText(DetailActivity.this, sb, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(DetailActivity.this, "No items selected", Toast.LENGTH_SHORT).show();

            }
            return true;

        } else if (id == R.id.log_out_menu) {
            session.logoutUser(DetailActivity.this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_detail);
        new ProgressDialog(DetailActivity.this);


        session = new SessionManager(DetailActivity.this);
        rv = (RecyclerView) findViewById(R.id.rv);
        PlayerList();
        //  init();
        adapter = new RecyclerViewAdapter(DetailActivity.this, player);
        rv.setLayoutManager(new LinearLayoutManager(DetailActivity.this));
        rv.setAdapter(adapter);
      //  prog.dismiss();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2 = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void scan() {
        final Activity activity = this;
        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Set over barcode of SIES GST College ID");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
        integrator.setOrientationLocked(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Log.d("MainActivity", "cancelled scan");
                flag = false;
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                barcode_scan = "fail";
            } else {
                Log.d("MainActivity", "Scanned");
                barcode_scan = result.getContents();
                flag = true;
                //buildDialog(true);
                Toast.makeText(this, "Scanned: " + barcode_scan, Toast.LENGTH_LONG).show();
                play(barcode_scan);
            }
        }
    }

    public void play(String prn) {
        Intent intent = getIntent();
        String id1 = session.getID();
        Log.v("tag", id1);

        String url = "http://development.siesgst.ac.in/play.php";
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("prn", prn)
                .add("event_id", id1)
                .build();
        request = new Request.Builder()
                .url(url)
                .method("POST", body.create(null, new byte[0]))
                .post(body)
                .build();
        Log.v("url", body.toString());
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.v("onfailure", "fail");

                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                responseString = response.body().string();
                Log.v("responseplay", responseString);
                try {
                    JSONObject root = new JSONObject(responseString);
                    String status = root.optString("s" +
                            "status");
                    String message = root.optString("message");
                    if (message.equalsIgnoreCase("play")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                buildDialog(true);
                            }
                        });
                        Log.v("tag", "Can play");
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                buildDialog(false);
                            }
                        });

                        Log.v("tag", "Cannot play");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //scan();
            }
        });
        PlayerList();
    }

    public void UIDupdate(String prn) {
        Log.v("prn",prn);

        String id1 = session.getID();
        String url = "http://development.siesgst.ac.in/update.php";
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("prn", prn)
                .add("event_id", id1)
                .build();
        request = new Request.Builder()
                .url(url)
                .method("POST", body.create(null, new byte[0]))
                .post(body)
                .build();
        Log.v("url", body.toString());
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                responseString = response.body().string();
                Log.v("responseUpdate", responseString);
                try {
                    JSONObject root = new JSONObject(responseString);
                    String status = root.optString("status");
                    String message = root.optString("message");
                    if (message.equalsIgnoreCase("true")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(DetailActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Log.v("tag", "Can play again");
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(DetailActivity.this, "NO", Toast.LENGTH_SHORT).show();
                            }
                        });

                        Log.v("tag", "Cannot play");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //scan();
            }
        });
        PlayerList();
    }


    void buildDialog(boolean decide) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
        LayoutInflater inflater = DetailActivity.this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.uid_result, null);
        // Set up the input
        builder.setView(view);


        final TextView button = (TextView) view.findViewById(R.id.scan_status);
        final ImageView img = (ImageView) view.findViewById(R.id.uid_result_image);
        final TextView name = (TextView) view.findViewById(R.id.result_name);

        //GradientDrawable imgBackground = (GradientDrawable) img.getBackground();

        name.setText(barcode_scan);

        if (decide) {
            img.setBackgroundColor(Color.parseColor("#00E676"));
            button.setBackgroundColor(Color.parseColor("#00E676"));
            img.setImageResource(R.drawable.tick);
        } else {
            img.setBackgroundColor(Color.parseColor("#F44336"));
            button.setBackgroundColor(Color.parseColor("#F44336"));

            img.setImageResource(R.drawable.cross);
        }

        // Set up the buttons
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        builder.show();
    }

    public void PlayerList() {
        final ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        if(activeNetwork!=null) {

            player = new ArrayList<Player>();

            ListAsync listAsync = new ListAsync(session.getID(), player, adapter, DetailActivity.this);
            listAsync.execute();
        }
        else{
            Toast.makeText(DetailActivity.this, "No Network Connection!", Toast.LENGTH_SHORT).show();

        }

        /*// required declarations
        String url = "http://192.168.43.221/list.php";
        String event_id = session.getID();
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("event_id",event_id)
                .build();
        request = new Request.Builder()
                .url(url)
                .method("POST",body.create(null,new byte[0]))
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.v("error","error");
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                responseString = response.body().string();
                Log.v("response", responseString);
                try {
                    JSONObject root = new JSONObject(responseString);
                    String status = root.optString("status");
                    JSONArray MessageArray = root.optJSONArray("message");
                    for(int i=0;i<MessageArray.length();i++)
                    {
                        JSONObject jsonObject = MessageArray.optJSONObject(i);
                        String id = jsonObject.optString("id");
                        String user_id = jsonObject.optString("user_id");
                        String event_id = jsonObject.optString("event_id");
                        String event_name = jsonObject.optString("event_name");
                        String event_credit = jsonObject.optString("event_credit");
                        String statusPlayer = jsonObject.optString("status");

                        if(statusPlayer.equalsIgnoreCase("0")){
                            player.add(new Player(user_id,event_name," ","Can Play"));
                        }
                        else{
                            player.add(new Player(user_id,event_name," ","Can't Play"));

                        }
                        // String created_at = MessageArray.optString("created_at");
                        // String updated_at = MessageArray.optString("updated_at");
                        // i have commented the above lines coz these values are always null. *Avoiding null pointer exception.*
                        // *flies away*
                    }
                    adapter=new RecyclerViewAdapter(DetailActivity.this,player);
                    prog.dismiss();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
*/



    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Detail Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2.connect();
        AppIndex.AppIndexApi.start(client2, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client2, getIndexApiAction());
        client2.disconnect();
    }
}
