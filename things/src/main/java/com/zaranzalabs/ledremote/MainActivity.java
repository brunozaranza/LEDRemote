package com.zaranzalabs.ledremote;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class MainActivity extends Activity {

    private Gpio ledGpio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PeripheralManager pioService = PeripheralManager.getInstance();
        try {
            ledGpio = pioService.openGpio(BoardDefaults.getGPIOForLED());
            ledGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
        } catch (IOException e) {
            Log.e("LOG", "Error configuring GPIO pins", e);
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbReference = database.getReference("status");

        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                boolean value = dataSnapshot.getValue(Boolean.class);

                try {
                    ledGpio.setValue(value);
                } catch (IOException e) {
                    Log.e("LOG", "Error updating GPIO value", e);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("LOG", "Failed to read value.", error.toException());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
