package ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;

import com.libopenmw.openmw.R;

import ui.activity.MainActivity;
import utils.CustomAdapter;
import utils.Server;
// https://www.journaldev.com/10416/android-listview-with-custom-adapter-example-tutorial
public class BrowserActivity extends AppCompatActivity {
    public static ArrayList<Server> Servers = new ArrayList<>();
    public static CustomAdapter adapter;
    public static boolean sortPlayersFilter = false;
    public static boolean sortAlphabetFilter = false;
    private static SwipeRefreshLayout pullToRefresh;
    ListView listView;
    public static String CommandArgs = "";
    private String password = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.browser);
        listView = (ListView) findViewById(R.id.list);

        pullToRefresh = (SwipeRefreshLayout) findViewById(R.id.pullToRefresh);
        adapter = new CustomAdapter(Servers, this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Server Server = Servers.get(position);


                CommandArgs = ("--connect " + Server.getip());
                if (!PreferenceManager.getDefaultSharedPreferences(BrowserActivity.this).getBoolean("multiplayer", false)) {
                    Toast toast = Toast.makeText(BrowserActivity.this,
                        "You must enable multiplayer in the settings menu in order to join a server.", Toast.LENGTH_LONG);
                    toast.show();
                } else if (Server.getPassworded()) 
                    displayInput();
                else
                    MainActivity.main.startGame();
            }
        });
        // https://javapapers.com/android/android-swipe-down-to-refresh-a-listview/
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.refreshData();
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    public static String getArgv() {
        return CommandArgs;
    }

    // https://stackoverflow.com/questions/10903754/input-text-dialog-android
    public void displayInput() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter password");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                password = input.getText().toString();
                CommandArgs += (" --password " + password);
                MainActivity.main.startGame();
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

    public static void sortPlayers() {
        // https://stackoverflow.com/questions/31470385/sort-an-arraylist-based-on-integer
        adapter.sort(new Comparator<Server>(){
            public int compare(Server m1, Server m2) {
                return m2.getplayerCount() - m1.getplayerCount(); // sort order
            }
        });
        adapter.notifyDataSetChanged();
        sortPlayersFilter = true;
        sortAlphabetFilter = false;
    }

    public static void sortAlphabet() {
        adapter.sort(new Comparator<Server>(){
            public int compare(Server m1, Server m2) {
                return m1.getserverName().compareTo(m2.getserverName());
            }
        });
        adapter.notifyDataSetChanged();
        sortPlayersFilter = false;
        sortAlphabetFilter = true;
    }
}


