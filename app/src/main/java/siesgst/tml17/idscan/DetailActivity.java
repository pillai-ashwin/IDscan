package siesgst.tml17.idscan;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
            builder.setTitle("Input PRN");
            // Set up the input
            final EditText input = new EditText(DetailActivity.this);
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT );
            builder.setView(input);
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
                for(int i=0;i<a.size();i++ ){
                    sb.append(a.get(i).getUID());
                    sb.append("\n");
                }
                Toast.makeText(DetailActivity.this, sb, Toast.LENGTH_SHORT).show();

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
               // play(barcode_scan);
            }
        }
    }
    public void play(String prn)
    {
        Intent intent = getIntent();
        String id1 = intent.getStringExtra("id");
        String url = "http://192.168.43.221/play.php";
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
                    String status = root.optString("status");
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
        String url = "http://192.168.43.221/update.php";
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
}
/*
*
* package siesgst.tml17.idscan;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONObject;

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
    TextView add;

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
        add=(TextView)findViewById(R.id.manual_addition);
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
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                builder.setTitle("Input PRN");
                // Set up the input
                final EditText input = new EditText(DetailActivity.this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT );
                builder.setView(input);
                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        barcode_scan = input.getText().toString();
                        Toast.makeText(DetailActivity.this, "Input="+barcode_scan, Toast.LENGTH_LONG).show();
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
                play(barcode_scan);
            }
        }
    }
    public void play(String prn)
    {
        Intent intent = getIntent();
        String id1 = session.getID();
        String url = "http://192.168.43.10/play.php";
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
                    String status = root.optString("status");
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
}
*/
