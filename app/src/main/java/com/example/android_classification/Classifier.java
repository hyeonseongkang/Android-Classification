package com.example.android_classification;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Pair;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.Tensor;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

public class Classifier {

    Context context;

    private static final String MODEL_NAME = "keras_model.tflite";

    Interpreter interpreter = null;

    // 모델 입력 크기 변수
    int modelInputWidth, modelInputHeight, modelInputChannel;
    int modelOutputClasses;

    public Classifier(Context context) {
        this.context = context;
    }

    public void init() throws IOException {
        ByteBuffer model = loadModelFile(MODEL_NAME);
        model.order(ByteOrder.nativeOrder());
        interpreter = new Interpreter(model);

        // 모델 입출력 크기 계산 함수 호출
        initModelShape();
    }


    // tfflie 파일 로드
    private ByteBuffer loadModelFile(String modelName) throws IOException {
        AssetManager am = context.getAssets();
        AssetFileDescriptor afd = am.openFd(modelName);
        FileInputStream fis = new FileInputStream(afd.getFileDescriptor());
        FileChannel fc = fis.getChannel();
        long startOffset = afd.getStartOffset();
        long declaredLength = afd.getDeclaredLength();

        return fc.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    // 모델 입출력 크기 계산 함수
    private void initModelShape() {
        Tensor inputTensor = interpreter.getInputTensor(0);
        int[] inputShape = inputTensor.shape();
        modelInputChannel = inputShape[0];
        modelInputWidth = inputShape[1];
        modelInputHeight = inputShape[2];

        Tensor outputTensor = interpreter.getOutputTensor(0);
        int[] outputShape = outputTensor.shape();
        modelOutputClasses = outputShape[1];
    }

    // 이미지 크기 변환
    private Bitmap resizeBitmap(Bitmap bitmap) {
        return Bitmap.createScaledBitmap(bitmap, modelInputWidth, modelInputHeight, false);
    }

    // ARGB -> GrayScale, Bitmap -> ByteBuffer
    private ByteBuffer convertBitmapToGrayByteBuffer(Bitmap bitmap) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bitmap.getByteCount());
        byteBuffer.order(ByteOrder.nativeOrder());

        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        for (int pixel : pixels) {
            int r = pixel >> 16 & 0xFF;
            int g = pixel >> 8 & 0xFF;
            int b = pixel & 0xFF;

            float avgPixelValue = (r + g + b) / 3.0f;
            float normalizedPixelValue = avgPixelValue / 255.0f;

            byteBuffer.putFloat(normalizedPixelValue);
        }

        return byteBuffer;
    }

    // 손글씨 분류 모델 추론
    public Pair<Integer, Float> classify(Bitmap image) {
        ByteBuffer buffer = convertBitmapToGrayByteBuffer(resizeBitmap(image));
        float[][] result = new float[1][modelOutputClasses];
        interpreter.run(buffer, result);
        return argmax(result[0]);
    }

    // 추론 결과 해석
    private Pair<Integer, Float> argmax(float[] array) {
        int argmax = 0;
        float max = array[0];
        for(int i = 0; i < array.length; i++) {
            float f = array[i];
            if (f > max) {
                argmax = i;
                max = f;
            }
        }
        return new Pair<>(argmax, max);
    }

    // interpreter 자원 정리
    public void finish() {
        if (interpreter != null)
            interpreter.close();
    }

}
