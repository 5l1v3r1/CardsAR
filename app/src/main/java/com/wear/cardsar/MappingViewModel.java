package com.wear.cardsar;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

/**
 * Created by Carlos on 4/14/2018.
 */
public class MappingViewModel extends AndroidViewModel {

    private AppRepository mRepository;

    private LiveData<List<CardMapping>> mAllMappings;

    private String gameName;

    public MappingViewModel (Application application, String gameName) {
        super(application);
        this.gameName = gameName;
        mRepository = new AppRepository(application);
        mAllMappings = mRepository.getMappings(gameName);
    }

    LiveData<List<CardMapping>> getAllMappings() { return mAllMappings; }

    public void insert(CardMapping mapping) { mRepository.insert(mapping); }
}