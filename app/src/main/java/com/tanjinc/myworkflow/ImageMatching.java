package com.tanjinc.myworkflow;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ImageMatching {

    class MIndex {
        public int index;
        public int newIndex;

        public MIndex(int a, int b) {
            this.index = a;
            this.newIndex = b;
        }
    }

    public int[][] imageMatching(String firstPath, String secondPath) {
        int num = 0;
        Rect rect1 = new Rect();
        Rect rect2 = new Rect();
        int[] pixel1 = readPixel(firstPath, rect1);
        int[] pixel2 = readPixel(secondPath, rect2);
        if (pixel1 == null || pixel2 == null) {
            return null;
        }
        int[] pixelsMax;
        int widthMax;
        int heightMax;
        int[] pixelsMin;
        int widthMin;
        int heightMin;
        int j;
        if (rect1.width() * rect1.height() >= rect2.width() * rect2.height()) {
            pixelsMax = pixel1;
            widthMax = rect1.width();
            heightMax = rect1.height();
            pixelsMin = pixel2;
            widthMin = rect2.width();
            heightMin = rect2.height();
        } else {
            pixelsMax = pixel2;
            widthMax = rect2.width();
            heightMax = rect2.height();
            pixelsMin = pixel1;
            widthMin = rect1.width();
            heightMin = rect1.height();
        }
        int lengthMax = pixelsMax.length;
        int lengthMin = pixelsMin.length;
        int deltaWidth = widthMax - widthMin;
        int deltaHeiht = heightMax - heightMin;
        int finalPoint = (widthMax * deltaHeiht) + deltaWidth;
        List<MIndex> list = new ArrayList();
        for (j = 0; j < lengthMin; j = (int) (((double) j) + (Math.random() * ((double) widthMin)))) {
            list.add(new MIndex(j, (j % widthMin) + ((j / widthMin) * widthMax)));
        }
        List<Integer> matchIndex = new ArrayList();
        int i = 0;
        while (i < lengthMax) {
            if (i % widthMax <= deltaWidth) {
                if ((deltaHeiht == 0 && i > deltaWidth) || (finalPoint != 0 && i > finalPoint)) {
                    break;
                }
                if (pixelsMax[i] == pixelsMin[0]) {
                    boolean flag = true;
                    int index = i;
                    int count = list.size();
                    for (j = 0; j < count; j++) {
                        MIndex mIndex = (MIndex) list.get(j);
                        index = i + mIndex.newIndex;
                        if (index >= lengthMax || pixelsMax[index] != pixelsMin[mIndex.index]) {
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        matchIndex.add(Integer.valueOf(i));
                        num++;
                    }
                }
            }
            i++;
        }
        int size = matchIndex.size();
        int[][] matchPoint = (int[][]) Array.newInstance(Integer.TYPE, new int[]{size, 2});
        for (i = 0; i < size; i++) {
            int y = ((Integer) matchIndex.get(i)).intValue() / widthMax;
            matchPoint[i][0] = ((Integer) matchIndex.get(i)).intValue() % widthMax;
            matchPoint[i][1] = y;
        }
        return matchPoint;
    }

    private int[] readPixel(String pathname, Rect rect) {
        File file = new File(pathname);
        if (file != null && file.isFile() && file.canRead()) {
            Bitmap bitmap = BitmapFactory.decodeFile(pathname);
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            rect.set(0, 0, width, height);
            int[] pixels = new int[(width * height)];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    pixels[(i * width) + j] = bitmap.getPixel(j, i);
                }
            }
            bitmap.recycle();
            return pixels;
        }
        throw new IllegalStateException("can not find image");
    }
}
