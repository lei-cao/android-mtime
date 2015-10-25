package com.lei_cao.android.lazylist;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.net.URLEncoder;

public class FileCache {

    private File cacheDir;

    public FileCache(Context context) {
        if (android.os.Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), "LazyList");
        } else {
            cacheDir = context.getCacheDir();
        }

        if (!cacheDir.exists()) {
            cacheDir.mkdir();
        }
    }

    public File getFile(String url) {
        String filename = URLEncoder.encode(url);
        File f = new File(cacheDir, filename);
        return f;
    }

    public void clear() {
        File[] files = cacheDir.listFiles();

        if (files == null) {
            return;
        }
        for (File f : files) {
            f.delete();
        }
    }
}

