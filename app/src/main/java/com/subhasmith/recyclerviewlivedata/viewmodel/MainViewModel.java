package com.subhasmith.recyclerviewlivedata.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.subhasmith.recyclerviewlivedata.model.Data;
import com.subhasmith.recyclerviewlivedata.model.TestData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainViewModel extends AndroidViewModel {

    MutableLiveData<ArrayList<Data>> userLiveData;
    ArrayList<Data> testDataArrayList;

    public MainViewModel(@NonNull Application application) {
        super(application);
        userLiveData = new MutableLiveData<>();

        init();
    }

    public MutableLiveData<ArrayList<Data>> getUserMutableLiveData() {
        return userLiveData;
    }

    public void init() {
        populateList();
        userLiveData.setValue(testDataArrayList);
    }

    public void populateList() {
        TestData testData = new TestData();
        testDataArrayList = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(readJSONFromAsset());
            ObjectMapper mapper = new ObjectMapper();
            testData = mapper.readValue(obj.toString(), TestData.class);
            testDataArrayList.addAll(testData.getDataList());

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

    }

    public String readJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getApplication().getApplicationContext().getAssets().open("test.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

}
