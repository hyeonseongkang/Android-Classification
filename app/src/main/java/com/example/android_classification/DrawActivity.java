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
    private TextView resultNumber, resultProbability;

    Classifier classifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        drawView = (DrawView) findViewById(R.id.drawView);
        classifyButton = (Button) findViewById(R.id.classifyButton);
        clearButton = (Button) findViewById(R.id.clearButton);
        resultNumber = (TextView) findViewById(R.id.resultNumber);
        resultProbability = (TextView) findViewById(R.id.resultProbability);

        drawView.setStrokeWidth(100.0f);
        drawView.setBackgroundColor(Color.BLACK);
        drawView.setColor(Color.WHITE);

        classifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number;
                String probability;

                Bitmap image = drawView.getBitmap();

                Pair<Integer, Float> res = classifier.classify(image);
                number = String.valueOf(res.first);
                probability = String.format("%.0f%%", res.second * 100.0f);

                resultNumber.setText(number);
                resultProbability.setText(probability);
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawView.clearCanvas();
                resultNumber.setText("");
                resultProbability.setText("");
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