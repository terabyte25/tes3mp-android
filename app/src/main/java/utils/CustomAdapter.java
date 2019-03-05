package utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.libopenmw.openmw.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import ui.fragments.FragmentBrowser;


/**
 * Created by anupamchugh on 09/02/16.
 */
public class CustomAdapter extends ArrayAdapter<Server> implements View.OnClickListener{

    private ArrayList<Server> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView serverName;
        TextView ip;
        TextView playerCount;
        TextView isPassworded;
    }



    public CustomAdapter(ArrayList<Server> data, Context context) {
        super(context, R.layout.rowlistview, data);
        this.dataSet = data;
        this.mContext=context;
        new GetData().execute();
    }

    public void refreshData() {
        new GetData().execute();
    }


    @Override
    public void onClick(View v) {

    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Server Server = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {


            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.rowlistview, parent, false);
            viewHolder.serverName = (TextView) convertView.findViewById(R.id.name);
            viewHolder.ip = (TextView) convertView.findViewById(R.id.ip);
            viewHolder.playerCount = (TextView) convertView.findViewById(R.id.playerCount);
            // viewHolder.info = (ImageView) convertView.findViewById(R.id.item_info);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;


        viewHolder.serverName.setText(Server.getserverName());
        viewHolder.ip.setText(Server.getip());
        viewHolder.playerCount.setText(String.valueOf(Server.getplayerCount()));
        // Return the completed view to render on screen
        return convertView;
    }
    // https://pastebin.com/a253vGJM from discordpeter
    class GetData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            String result = "";
            Log.e("App", "Fail");
            try {
                URL url = new URL("http://master.tes3mp.com:8081/api/servers");
                urlConnection = (HttpURLConnection) url.openConnection();

                int code = urlConnection.getResponseCode();

                if (code == 200) {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    if (in != null) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                        String line = "";

                        while ((line = bufferedReader.readLine()) != null)
                            result += line;
                    }
                    in.close();
                }

                return result;
            } catch (MalformedURLException e) {
                Log.e("App", "Success: ");
            } catch (IOException e) {
            } finally {
                urlConnection.disconnect();
            }
            return result;

        }

        @Override
        protected void onPostExecute(String result) {
            FragmentBrowser.Servers.clear();
            try {

                JSONObject obj = new JSONObject(result);


                JSONObject obj2 = obj.getJSONObject("list servers");

                Iterator<String> iterator = obj2.keys();


                while (iterator.hasNext()) {

                    String key = iterator.next();

                    try {

                        JSONObject anotherobject = obj2.getJSONObject(key);

                        String hostname = anotherobject.getString("hostname");
                        int playercount = anotherobject.getInt("players");
                        boolean passworded = anotherobject.getBoolean("passw");

                        FragmentBrowser.Servers.add(new Server(passworded, key, hostname, playercount));

                    } catch (JSONException e) {
                    }
                }

            } catch (Throwable t) {
            }
            // https://stackoverflow.com/questions/16441298/android-call-notifydatasetchanged-from-asynctask
            if (FragmentBrowser.sortPlayersFilter)
                FragmentBrowser.sortPlayers();
            else if (FragmentBrowser.sortAlphabetFilter)
                FragmentBrowser.sortAlphabet();
            
            CustomAdapter.this.notifyDataSetChanged();

            super.onPostExecute("yes");

        }
    }
}
