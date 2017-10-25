package com.zigsun.luo.projection.camera;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
* Created by Luo on 2015/5/15.
 * 二维码生成类.
 * @author Luo Wei
*/
public class QRCodeEncoder {
    private static final String TAG = QRCodeEncoder.class.getSimpleName();
    private  int dimension;
    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;
    private String content;
    private Bitmap bitmap;

    public QRCodeEncoder() {
    }

    public QRCodeEncoder(int dimension, String content) {
        setParameter(dimension, content);
    }

    public Bitmap encodeAsBitmap(int dimension, String content){
        setParameter(dimension, content);
        return encodeAsBitmap();
    }

    /**
     * @param dimension 生成二维码尺寸
     * @param content   二维码内容
     */
    private void setParameter(int dimension, String content) {
        this.dimension = dimension;
        this.content = content;
    }

    /**
     * 指定编码,否则中文为乱码
     * @param contents 二维码内容
     * @return  编码格式
     */
    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }
    public Bitmap encodeAsBitmap(){

        QRCodeWriter writer = new QRCodeWriter();

        Map<EncodeHintType,Object> hints = null;
        String encoding = guessAppropriateEncoding(content);
        if (encoding != null) {
            hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        BitMatrix result;
        try {
            result = writer.encode(content, BarcodeFormat.QR_CODE, dimension, dimension,hints);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }


    public void saveQRBitmapToFile(){
        Bitmap bitmap = this.bitmap==null? encodeAsBitmap():this.bitmap;
        File bsRoot = new File(Environment.getExternalStorageDirectory(), "QRCodeEncoder");
        if (!bsRoot.exists() && !bsRoot.mkdirs()){
            Log.w(TAG, "Couldn't make dir " + bsRoot);
            return;
        }
//        File barcodesRoot = new File(bsRoot, "Barcodes");
//        if (!barcodesRoot.exists() && !barcodesRoot.mkdirs()) {
//            Log.w(TAG, "Couldn't make dir " + barcodesRoot);
//            return;
//        }

        File barcodeFile = new File(bsRoot, makeBarcodeFileName(content) + ".png");
//        if (!barcodeFile.delete()) {
//            Log.w(TAG, "Could not delete " + barcodeFile);
//            // continue anyway
//        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(barcodeFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, fos);
        } catch (FileNotFoundException fnfe) {
            Log.w(TAG, "Couldn't access file " + barcodeFile + " due to " + fnfe);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ioe) {
                    // do nothing
                }
            }
        }
    }

    private  final Pattern NOT_ALPHANUMERIC = Pattern.compile("[^A-Za-z0-9]");
    private static final int MAX_BARCODE_FILENAME_LENGTH = 24;
    private  CharSequence makeBarcodeFileName(CharSequence contents) {
        String fileName = NOT_ALPHANUMERIC.matcher(contents).replaceAll("_");
        if (fileName.length() > MAX_BARCODE_FILENAME_LENGTH) {
            fileName = fileName.substring(0, MAX_BARCODE_FILENAME_LENGTH);
        }
        return fileName;
    }



}
