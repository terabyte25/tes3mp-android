package plugins;

import android.app.Activity;
import android.preference.PreferenceManager;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import constants.Constants;
import file.utils.FileUtils;
import parser.json.JsonReader;
import file.ConfigsFileStorageHelper;
import plugins.bsa.BsaUtils;
import plugins.PluginReader;
import utils.Utils;

/**
 * Created by sandstranger on 07.09.2016.
 */
public class PluginsStorage {
    private String dataPath;
    private List<PluginInfo> pluginsList = new ArrayList<PluginInfo>();
    private File dataDir;
    private final String JSON_FILE_LOCATION = ConfigsFileStorageHelper.CONFIGS_FILES_STORAGE_PATH + "/files.json";
    private final String CFG_FILE_LOCATION =  ConfigsFileStorageHelper.CONFIGS_FILES_STORAGE_PATH + "/openmw/openmw.cfg";
    private Activity activity;

    public PluginsStorage(Activity activity) {
        this.activity = activity;
        dataPath = PreferenceManager.getDefaultSharedPreferences(activity).getString("data_files", "");
        dataDir = new File(dataPath);
        loadPlugins(JSON_FILE_LOCATION);
    }

    public List<PluginInfo> getPluginsList() {
        return pluginsList;
    }

    public void loadPlugins(String path) {

        try {
            File json = new File(path);
            /**
            if (json.exists()) {
                String jsonDataPath = JsonReader.getPluginsPath(path);
                if (jsonDataPath.replace("\\","") != dataPath)
                    json.delete();
            */
            pluginsList = JsonReader.loadFile(path);
            removeDeletedFiles();
            addNewFiles();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isListContainsFile(File f) {
        for (PluginInfo plugin : pluginsList) {
            if (f.isFile() && !plugin.name.isEmpty() && f.getName().endsWith(plugin.name)) {
                return true;
            }
        }
        return false;
    }

    public void replacePlugins(int from, int to) {
        PluginInfo item = pluginsList.get(from);
        pluginsList.remove(from);
        pluginsList.add(to, item);
    }

    private void addNewFiles() throws JSONException, IOException {
        File[] files = dataDir.listFiles((d, name) -> name.toLowerCase().endsWith(".esp") || name.toLowerCase().endsWith(".esm") ||
                name.toLowerCase().endsWith(".omwgame") || name.toLowerCase().endsWith(".omwaddon"));
        boolean isFileAdded = false;
        for (File f : files) {
            if (!isListContainsFile(f)) {
                isFileAdded = true;
                PluginInfo pluginData = new PluginInfo();
                pluginData.name = f.getName();
                pluginData.nameBsa = f.getName().split("\\.")[0] + ".bsa";
                pluginData.gameFiles = PluginReader.read(dataPath + "/" + pluginData.name);
                pluginData.isPluginEsp = f.getName().endsWith(".ESP") || f.getName().endsWith(".esp");
                pluginData.pluginExtension = FileUtils.getFileName(f.getName(), true);
                pluginsList.add(pluginData);
            }
        }
        if (isFileAdded) {
            sortPlugins();
        }
    }

    private void sortPlugins() {
        // Alphabet sort
        Collections.sort(pluginsList,
            new Comparator<PluginInfo>()
            {
                public int compare(PluginInfo f1, PluginInfo f2)
                {
                    String fullname1 = f1.name;
                    String fullname2 = f2.name;
                    return fullname1.toLowerCase().compareTo(fullname2.toLowerCase());
                }        
            });
                 
        boolean movedFiles = true;
        int fileCount = pluginsList.size();

        //Dependency sort
        //iterate until no sorting of files occurs
        while (movedFiles)
        {
            movedFiles = false;
            //iterate each file, obtaining a reference to it's gamefiles list
            for (int i = 0; i < fileCount; i++)
            {
                String gamefiles = pluginsList.get(i).gameFiles;
                //iterate each file after the current file, verifying that none of it's
                //dependencies appear.
                for (int j = i + 1; j < fileCount; j++)
                {
                    if (gamefiles.contains(pluginsList.get(j).name)
                    || (gamefiles.isEmpty()
                    && pluginsList.get(j).name.contains("Morrowind.esm"))) // Hack: implicit dependency on Morrowind.esm for dependency-less files
                    {
                            move(pluginsList, j, i);

                            movedFiles = true;
                    }
                }
                if (movedFiles)
                    break;
            }
        }
        Collections.sort(pluginsList, (p1, p2) -> Boolean.compare(p1.isPluginEsp, p2.isPluginEsp));
    }

    // https://stackoverflow.com/questions/36011356/moving-elements-in-arraylist-java
    private void move(List myList, int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            myList.add(toPosition, myList.get(fromPosition));
            myList.remove(fromPosition);
        } else {
            myList.add(toPosition, myList.get(fromPosition));
            myList.remove(fromPosition + 1);
        }
    }

    private void removeDeletedFiles() {
        Iterator<PluginInfo> iterator = pluginsList.iterator();
        while (iterator.hasNext()) {
            PluginInfo pluginInfo = iterator.next();
            File file = new File(dataPath + "/" + pluginInfo.name);
            if (!file.exists()) {
                pluginsList.remove(pluginInfo);
            }
        }
    }

    public void updatePluginsStatus(boolean needEnableMods) {
        for (int i = 0; i < pluginsList.size(); i++) {
            pluginsList.get(i).enabled = needEnableMods;
        }
    }

    public void updatePluginStatus(int position, boolean status) {
        pluginsList.get(position).enabled = status;
    }

    public void saveJson(final String path) {
        String finalPath = path.isEmpty() ? JSON_FILE_LOCATION : path;
        try {
            JsonReader.saveFile(pluginsList, finalPath, dataPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void savePlugins() {
        try {
            boolean needRegisterBsaFiles = BsaUtils.getSaveAllBsaFilesValue(activity);
            BsaUtils bsaUtils = new BsaUtils(dataPath);
            StringBuilder stringBuilder = new StringBuilder();
            for (PluginInfo pluginInfo : pluginsList) {
                if (pluginInfo.enabled) {
                    stringBuilder.append("content= " + pluginInfo.name + "\n");
                    if (!needRegisterBsaFiles) {
                        String bsaFileNameName = bsaUtils.getBsaFileName(pluginInfo);
                        if (!bsaFileNameName.isEmpty()) {
                            stringBuilder.append("fallback-archive= "
                                    + bsaFileNameName + "\n");
                        }
                    }
                }
            }
            if (needRegisterBsaFiles) {
                List<File> list = bsaUtils.getBsaList();
                for (File f : list) {
                    stringBuilder.append("fallback-archive= " + f.getName() + "\n");
                }
            }
            FileUtils.saveDataToFile(stringBuilder.toString(), CFG_FILE_LOCATION, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

