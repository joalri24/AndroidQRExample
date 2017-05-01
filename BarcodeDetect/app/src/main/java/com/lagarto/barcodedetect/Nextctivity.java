package com.lagarto.barcodedetect;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class Nextctivity extends AppCompatActivity
{


    private TextView mCodeText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nextctivity);

        mCodeText = (TextView) findViewById(R.id.tv_code);

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity != null) {
            String stringData;

            // Search for the technician data using the id.
            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT))
            {
                stringData = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
                Log.d("NA", "string: " + stringData);
                mCodeText.setText(stringData);
            }


        }
    }
}
