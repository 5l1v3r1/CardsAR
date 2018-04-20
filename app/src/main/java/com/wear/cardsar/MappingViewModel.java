package com.wear.cardsar;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import java.util.List;


public class MappingViewModel extends AndroidViewModel {

    private AppRepository mRepository;

    private LiveData<List<CardMapping>> mAllMappings;

    public MappingViewModel (Application application) {
        super(application);
        mRepository = new AppRepository(application);
        mAllMappings = null;
    }

    LiveData<List<CardMapping>> getAllMappings(String gameName) {
        mAllMappings = mRepository.getMappings(gameName);
        return mAllMappings;
    }

    public void insert(CardMapping mapping) {
        mRepository.insertMapping(mapping);
        Log.d("MappingViewModel", "insert mapping");
    }

    public void delete(CardMapping mapping){
        mRepository.deleteMapping(mapping);
        Log.d("MappingViewModel", "delete mapping");
    }
}