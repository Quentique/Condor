package com.cvlcondorcet.condor;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.util.List;

/**
 * Created by Quentin DE MUYNCK on 16/07/2017.
 */

public class TeachersLoader extends AsyncTaskLoader<List<TeachersAbsence>> {

    private List<TeachersAbsence> list;
    private Database db;

    public TeachersLoader(Context ctx) {
        super(ctx);
        db = new Database(ctx);
    }

    @Override
    public List<TeachersAbsence> loadInBackground() {
        db.open();
        List<TeachersAbsence> data = db.getTeachersAbsence();
        db.close();
        Log.i("HELLO", "Background done");
        return data;
    }

    @Override
    public void deliverResult(List<TeachersAbsence> data){
       /* if (isReset()) {
            releaseResources(data);
            return;
        }*/

        List<TeachersAbsence> oldData = list;
        list = data;

        if (isStarted()) { super.deliverResult(data); }
       // if (oldData != null & oldData != data) { releaseResources(oldData); }
    }

    @Override
    protected void onStartLoading() {
        if (list != null) {
            deliverResult(list);
        }

        if (takeContentChanged() || list == null ){
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        onStopLoading();
        if (list != null) { list = null; }
    }

    @Override
    public void onCanceled(List<TeachersAbsence> data) {
        super.onCanceled(data);
    }

}
