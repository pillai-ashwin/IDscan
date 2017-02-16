package siesgst.tml17.idscan;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
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

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONObject;
import org.w3c.dom.Text;

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
    boolean flag=false;
    String barcode_scan;
    private Request request;
    String responseString;
    SessionManager session;
    List<Player> player=new ArrayList<Player>();
    RecyclerViewAdapter adapter;
    TextView EventIdName;
    TextView Email;
    TextView Event;
    TextView Contact_number;
    Button logout;
    TextView add;
    RecyclerView rv;
    List<Player>a;
    StringBuffer sb=new StringBuffer();
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

          //  play(barcode_scan);
            return true;
        }
        else if (id == R.id.action_manually) {
            Log.d("add manually","in");
            AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
            LayoutInflater inflater = DetailActivity.this.getLayoutInflater();
            final View view=inflater.inflate(R.layout.dialog_box_layout, null);
            // Set up the input
            builder.setView(view);

            final  EditText input=(EditText)view.findViewById(R.id.ManualUID);
            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    barcode_scan = input.getText().toString();
                    Toast.makeText(DetailActivity.this, "Input="+barcode_scan, Toast.LENGTH_LONG).show();
                  //  play(barcode_scan);
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
        }

        else if(id==R.id.update_manually){

                a=adapter.getselectedList();
            if(a.size()!=0) {
                for (int i = 0; i < a.size(); i++) {
                    sb.append(a.get(i).getUID());
                    sb.append("\n");
                }
                Toast.makeText(DetailActivity.this, sb, Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(DetailActivity.this, "No items selected", Toast.LENGTH_SHORT).show();

            }
            return true;

        }
        else if(id==R.id.log_out_menu){
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
        session=new SessionManager(DetailActivity.this);
        rv= (RecyclerView)findViewById(R.id.rv);
        init();
        rv.setLayoutManager(new LinearLayoutManager(DetailActivity.this));

        adapter=new RecyclerViewAdapter(DetailActivity.this,player);
        rv.setAdapter(adapter);




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
        integrator.setOrientationLocked(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Log.d("MainActivity", "cancelled scan");
                flag=false;
                buildDialog(false);
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                barcode_scan="fail";
            } else {
                Log.d("MainActivity", "Scanned");
                barcode_scan = result.getContents();
                flag=true;
                barcode_scan="true";
                buildDialog(true);
                Toast.makeText(this, "Scanned: " + barcode_scan, Toast.LENGTH_LONG).show();
               // play(barcode_scan);
            }
        }
    }
    public void play(String prn)
    {
        Intent intent = getIntent();
        String id1 = intent.getStringExtra("id");
        String url = "http://192.168.1.100/play.php";
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
        Log.v("url",body.toString());
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                responseString = response.body().string();
                Log.v("response", responseString);
                try {
                    JSONObject root = new JSONObject(responseString);
                    String status = root.optString("s" +
                            "status");
                    String message = root.optString("message");
                    if(message.equalsIgnoreCase("play"))
                    {
                        Log.v("tag","Can play");
                    }
                    else{
                        Log.v("tag","Cannot play");
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                //scan();
            }
        });
    }

    public void UIDupdate(String prn)
    {
        Intent intent = getIntent();
        String id1 = intent.getStringExtra("id");
        String url = "http://192.168.1.100/update.php";
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("event_id", id1)
                .add("prn", prn)
                .build();
        request = new Request.Builder()
                .url(url)
                .method("POST", body.create(null, new byte[0]))
                .post(body)
                .build();
        Log.v("url",body.toString());
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
                  //  if(message.equalsIgnoreCase("play"))
                  //  {
                    //    Log.v("tag","Can play");
                   // }
                    //else{
                     //   Log.v("tag","Cannot play");
                    //}
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                //scan();
            }
        });
    }

    void init(){
        player= new ArrayList<Player>();
        player.add(new Player("115A1067","Aditya Nair","12:58","9987884770"));
        player.add(new Player("115A1061","Aditya Kulakarni","13:10","8767459127"));
        player.add(new Player("115A1077","Omkar Prabhu","15:07","9967721999"));
        player.add(new Player("115A1067","Aditya Nair","12:58","9987884770"));
        player.add(new Player("115A1061","Aditya Kulakarni","13:10","8767459127"));
        player.add(new Player("115A1077","Omkar Prabhu","15:07","9967721999"));
        player.add(new Player("115A1067","Aditya Nair","12:58","9987884770"));
        player.add(new Player("115A1061","Aditya Kulakarni","13:10","8767459127"));
        player.add(new Player("115A1077","Omkar Prabhu","15:07","9967721999"));
        player.add(new Player("115A1067","Aditya Nair","12:58","9987884770"));
        player.add(new Player("115A1061","Aditya Kulakarni","13:10","8767459127"));
        player.add(new Player("115A1077","Omkar Prabhu","15:07","9967721999"));
        player.add(new Player("115A1067","Aditya Nair","12:58","9987884770"));
        player.add(new Player("115A1061","Aditya Kulakarni","13:10","8767459127"));
        player.add(new Player("115A1077","Omkar Prabhu","15:07","9967721999"));
        player.add(new Player("115A1067","Aditya Nair","12:58","9987884770"));
        player.add(new Player("115A1061","Aditya Kulakarni","13:10","8767459127"));
        player.add(new Player("115A1077","Omkar Prabhu","15:07","9967721999"));
        player.add(new Player("115A1067","Aditya Nair","12:58","9987884770"));
        player.add(new Player("115A1061","Aditya Kulakarni","13:10","8767459127"));
        player.add(new Player("115A1077","Omkar Prabhu","15:07","9967721999"));
        player.add(new Player("115A1067","Aditya Nair","12:58","9987884770"));
        player.add(new Player("115A1061","Aditya Kulakarni","13:10","8767459127"));
        player.add(new Player("115A1077","Omkar Prabhu","15:07","9967721999"));
        player.add(new Player("115A1067","Aditya Nair","12:58","9987884770"));
        player.add(new Player("115A1061","Aditya Kulakarni","13:10","8767459127"));
        player.add(new Player("115A1077","Omkar Prabhu","15:07","9967721999"));


    }

    void buildDialog(boolean decide){

        final AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
        LayoutInflater inflater = DetailActivity.this.getLayoutInflater();
        final View view=inflater.inflate(R.layout.uid_result, null);
        // Set up the input
        builder.setView(view);


        final TextView button =(TextView)view.findViewById(R.id.scan_status);
        final ImageView img=(ImageView)view.findViewById(R.id.uid_result_image);
        final TextView name =(TextView)view.findViewById(R.id.result_name);

        //GradientDrawable imgBackground = (GradientDrawable) img.getBackground();

        name.setText(barcode_scan);

        if(decide){
            img.setBackgroundColor(Color.parseColor("#00E676"));
            button.setBackgroundColor(Color.parseColor("#00E676"));
            img.setImageResource(R.drawable.tick);
        }
        else{
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
}
