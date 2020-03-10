package com.flipphone.qrcode;

import android.graphics.Bitmap;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import java.util.Map;

public class BarcodeEncoder {
    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;


    BarcodeEncoder() {
    }

    Bitmap createBitmap(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = matrix.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private BitMatrix encode(String contents, BarcodeFormat format, int width, int height) throws WriterException {
        try {
            return new MultiFormatWriter().encode(contents, format, width, height);
        } catch (WriterException e) {
            throw e;
        } catch (Exception e) {
            // ZXing sometimes throws an IllegalArgumentException
            throw new WriterException(e);
        }
    }

    private BitMatrix encode(String contents, BarcodeFormat format, int width, int height, Map<EncodeHintType, ?> hints) throws WriterException {
        try {
            return new MultiFormatWriter().encode(contents, format, width, height, hints);
        } catch (WriterException e) {
            throw e;
        } catch (Exception e) {
            throw new WriterException(e);
        }
    }

    public Bitmap encodeBitmap(String contents, BarcodeFormat format, int width, int height) throws WriterException {
        return createBitmap(encode(contents, format, width, height));
    }

    public Bitmap encodeBitmap(String contents, BarcodeFormat format, int width, int height, Map<EncodeHintType, ?> hints) throws WriterException {
        return createBitmap(encode(contents, format, width, height, hints));
    }
}