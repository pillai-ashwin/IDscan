package siesgst.tml17.idscan;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by rohitramaswamy on 16/02/17.
 */

public class ListAsync extends AsyncTask<String,Void,String>
{
    String event_id;
    public List<Player> player;
    public RecyclerViewAdapter adapter;
    public Context context;
    public ProgressDialog prog;

    public ListAsync(String event_id, List<Player> player,RecyclerViewAdapter adapter,Context context,ProgressDialog prog)
    {
        this.event_id = event_id;
        this.player = player;
        this.adapter = adapter;
        this.context = context;
        this.prog = prog;
    }

    @Override
    protected String doInBackground(String... params) {

        String url2 = "http://192.168.43.221/login.php&event_id="+event_id;
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        StringBuilder stringBuilder = new StringBuilder();
        try{

            URL url = new URL(url2);
            httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            inputStream =  new BufferedInputStream(httpURLConnection.getInputStream());

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while((line = reader.readLine())!=null) {
                stringBuilder.append(line);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            if(httpURLConnection!=null)
            {
                httpURLConnection.disconnect();
            }
        }
        String result = stringBuilder.toString();
        Log.v("tag",result);
        return result;

    }

    @Override
    protected void onPostExecute(String s) {
        Log.v("response", s);
        try {
            JSONObject root = new JSONObject(s);
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
            adapter=new RecyclerViewAdapter(context,player);
            prog.dismiss();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }



    }
}
