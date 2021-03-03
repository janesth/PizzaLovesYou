package com.jth.pizzalovesyou;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;


public class NotificationActivity extends AppCompatActivity {

    PeriodicWorkRequest notificationWorker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startService(View view) {
        if(validateCompany((EditText) findViewById(R.id.edit_company))) {

            Data inputData = new Data.Builder()
                    .putString("COMPANY", ((EditText) findViewById(R.id.edit_company)).getText().toString())
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
}
