package ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.libopenmw.openmw.R;
import com.mobeta.android.dslv.DragSortListView;

import java.io.IOException;

import constants.Constants;
import plugins.PluginReader;
import plugins.PluginsAdapter;
import plugins.PluginsStorage;
import prefs.PreferencesHelper;
import ui.dialog.MaterialDialogInterface;
import ui.dialog.MaterialDialogManager;

public class FragmentPlugins extends Fragment {

    private boolean isDirMode = false;
    private PluginsAdapter adapter;
    public static PluginsStorage pluginsStorage;
    protected MaterialDialogManager materialDialogManager;
    private static final int REQUEST_PATH = 12;
    private static FragmentPlugins Instance = null;

    public static boolean isInView = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Instance = this;
        materialDialogManager = new MaterialDialogManager(FragmentPlugins.this.getActivity());
        if (pluginsStorage == null)
            pluginsStorage = new PluginsStorage(this.getActivity());
        View rootView = inflater.inflate(R.layout.listview, container, false);
        PreferencesHelper.getPrefValues(this.getActivity());
        setupViews(rootView);
        savePluginsDataToDisk();
        isInView = true;
        return rootView;
    }

    public void createStorage(Activity act) {
        if (pluginsStorage == null)
            pluginsStorage = new PluginsStorage(act);
    }

    public void savePluginsDataToDisk() {
        if (pluginsStorage != null && pluginsStorage.getPluginsList() != null) {
            try {
                pluginsStorage.saveJson("");
                pluginsStorage.savePlugins();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (true) {
            savePluginsDataToDisk();
        }
        Instance = null;
        isInView = false;
    }

    public static FragmentPlugins getInstance() {
        return Instance;
    }

    public PluginsStorage getPluginsStorage() {
        return pluginsStorage;
    }

    private void setupViews(View rootView) {
        DragSortListView listView = (DragSortListView) rootView
                .findViewById(R.id.listView1);
        adapter = new PluginsAdapter(FragmentPlugins.this);
        listView.setAdapter(adapter);
        listView.setDropListener(onDrop);
    }

    private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
        @Override
        public void drop(int from, int to) {
            if (pluginsStorage != null) {
                pluginsStorage.replacePlugins(from, to);
                reloadAdapter();
            }
        }
    };


    public void disableMods() {
        showModDialog(false, "Do you want to disable all mods ?");
    }

    public void enableMods() {
        showModDialog(true, "Do you want to enable all mods ?");
    }

    private void showModDialog(final boolean isModEnable, String message) {
        MaterialDialogInterface materialDialogInterface = new MaterialDialogInterface() {
            @Override
            public void onPositiveButtonPressed() {
                changeModsStatus(isModEnable);
            }

            @Override
            public void onNegativeButtonPressed() {
                reloadAdapter();
            }
        };
        materialDialogManager.showDialog("", message, "Cancel", "OK", materialDialogInterface);
    }



    public void showDependenciesDialog(final int pos) {
        String dependencies = "";
        try {
            dependencies = PluginReader.read(Constants.APPLICATION_DATA_STORAGE_PATH + "/"
                    + pluginsStorage.getPluginsList().get(pos).name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        materialDialogManager.showMessageDialogBox("Dependencies", dependencies, "Close");
    }


    private void changeModsStatus(boolean needEnableMods) {
        if (pluginsStorage != null) {
            pluginsStorage.updatePluginsStatus(needEnableMods);
        }
        reloadAdapter();
    }

    private void reloadAdapter() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PATH) {
            if (resultCode == Activity.RESULT_OK) {
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }


}
