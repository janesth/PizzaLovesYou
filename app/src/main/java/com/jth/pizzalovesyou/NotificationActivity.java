package com.jth.pizzalovesyou;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


public class NotificationActivity extends AppCompatActivity {

    PeriodicWorkRequest notificationWorker;
    String GLOBAL_COMPANY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!isServiceNotRunning()) {
            ((EditText) findViewById(R.id.edit_company)).setText(getResources().getString(R.string.msg_running));
            ((EditText) findViewById(R.id.edit_company)).setEnabled(false);
        }
    }

    public void startService(View view) {
        if(validateCompany((EditText) findViewById(R.id.edit_company))) {

            Data inputData = new Data.Builder()
                    .putString("data_company", ((EditText) findViewById(R.id.edit_company)).getText().toString())
                    .build();

            notificationWorker = new PeriodicWorkRequest.Builder(
                    NotificationWorker.class,
                    20,
                    TimeUnit.MINUTES,
                    PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS,
                    TimeUnit.MILLISECONDS).setInputData(inputData).build();

            WorkManager.getInstance()
                    .enqueueUniquePeriodicWork(
                            "notificationWorker",
                            ExistingPeriodicWorkPolicy.REPLACE,
                            notificationWorker);
            ((EditText) findViewById(R.id.edit_company)).setEnabled(false);
            Toast.makeText(getApplicationContext(), String.format(getResources().getString(R.string.msg_start), ((EditText) findViewById(R.id.edit_company)).getText()) , Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.msg_error) , Toast.LENGTH_LONG).show();
        }
    }

    public void stopService(View view) {
        WorkManager.getInstance().cancelUniqueWork("notificationWorker");
        ((EditText) findViewById(R.id.edit_company)).setEnabled(true);
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.msg_stop) , Toast.LENGTH_LONG).show();
    }

    protected boolean validateCompany(EditText editText) {
        if(editText.getText() == null) {
            return false;
        } else {
            if(editText.getText().toString().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    protected boolean isServiceNotRunning() {
        ListenableFuture<List<WorkInfo>> listenableFuture = WorkManager.getInstance().getWorkInfosByTag("notificationWorker");
        if(listenableFuture != null) {
            try {
                List<WorkInfo> workInfos = listenableFuture.get();
                if (!workInfos.isEmpty()) {
                    for(WorkInfo workInfo : workInfos) {
                        return workInfo.getState().isFinished();
                    }
                }
            } catch (InterruptedException ex) {
                Log.e(NotificationActivity.class.getName(), "An interruption of some sorts.");
            } catch (ExecutionException ex) {
                Log.e(NotificationActivity.class.getName(), "Something about execution.");
            }
        }
        return true;
    }
}
