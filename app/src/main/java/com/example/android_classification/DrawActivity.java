package com.example.android_classification;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.divyanshu.draw.widget.DrawView;

import java.util.Locale;

public class DrawActivity extends AppCompatActivity {

    private static String TAG = "DrawActivity";

    private DrawView drawView;
    private Button classifyButton, clearButton;
    private TextView resultText;

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

            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawView.clearCanvas();
            }
        });
    }
}