package com.example.android_classification;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.divyanshu.draw.widget.DrawView;

import java.io.IOException;
import java.util.Locale;

public class DrawActivity extends AppCompatActivity {

    private static String TAG = "DrawActivity";

    private DrawView drawView;
    private Button classifyButton, clearButton;
    private TextView resultText;

    Classifier classifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        drawView = (DrawView) findViewById(R.id.drawView);
        classifyButton = (Button) findViewById(R.id.classifyButton);
        clearButton = (Button) findViewById(R.id.clearButton);
        resultText = (TextView) findViewById(R.id.resultText);

        drawView.setStrokeWidth(100.0f);
        drawView.setBackgroundColor(Color.BLACK);
        drawView.setColor(Color.WHITE);

        classifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap image = drawView.getBitmap();

                Pair<Integer, Float> res = classifier.classify(image);
                String outputString = String.format(Locale.ENGLISH, "%d, %.0f%%", res.first, res.second * 100.0f);
                resultText.setText(outputString);
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawView.clearCanvas();
            }
        });

        classifier = new Classifier(this);
        try {
            classifier.init();
        } catch (IOException e) {
            Log.d(TAG, e.toString());
        }
    }

    @Override
    protected void onDestroy() {
        classifier.finish();
        super.onDestroy();
    }
}