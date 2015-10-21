package com.qhad.ads.sdk.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;

import com.qhad.ads.sdk.logs.QHADLog;

import org.apache.http.util.EncodingUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Duan
 */
@SuppressLint("WorldWriteableFiles")
public class LocalFileManager {

    /**
     * @Description: 删除指定路径的文件
     */
    public static void deleteFile(String filePath) {
        if (Utils.isNotEmpty(filePath)) {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    /**
     * @param fileName 文件名
     * @param writestr 文件内容
     * @param context  上下文
     * @throws IOException 异常
     */
    public static void writeFile(String fileName, String writestr, Context context) {
        try {
            FileOutputStream fout = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            byte[] bytes = writestr.getBytes();
            fout.write(bytes);
            fout.close();
        } catch (FileNotFoundException e) {
            QHADLog.e("写文件错误" + e.getMessage());
        } catch (IOException e) {
            QHADLog.e("写文件错误" + e.getMessage());
        }
    }

    /**
     * @param fileName 文件名
     * @param writestr 文件内容
     * @param context  上下文
     * @throws IOException 异常
     */
    public static void writeAppendFile(String fileName, String writestr, Context context) {
        try {
            FileOutputStream fout = context.openFileOutput(fileName, Context.MODE_APPEND);
            byte[] bytes = writestr.getBytes();
            fout.write(bytes);
            fout.close();
        } catch (FileNotFoundException e) {
            QHADLog.e("写追加错误：" + e.getMessage());
        } catch (IOException e) {
            QHADLog.e("写追加错误：" + e.getMessage());
        }
    }

    /**
     * @param fileName 文件名
     * @param context  上下文
     * @return 文件数据
     * @throws IOException 错误日志
     */
    public static String readFile(String fileName, Context context) {
        String res = "";
        try {
            File file = new File(context.getFilesDir().getAbsolutePath() + "/" + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileInputStream fin = context.openFileInput(fileName);
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read(buffer);
            res = EncodingUtils.getString(buffer, "UTF-8");
            fin.close();
        } catch (FileNotFoundException e) {
            QHADLog.e("读文件错误：" + e.getMessage());
        } catch (IOException e) {
            QHADLog.e("读文件错误：" + e.getMessage());
        }
        return res;
    }

    /**
     * 获取SD卡根目录
     *
     * @return 根目录
     * @throws Exception 异常
     */
    public static String getSDPath() throws Exception {
        String path = "";
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
            path = sdDir.toString();
        }
        return path;
    }

    public static void writeFile2SD(String fileName, String writestr) {
        try {
            File file = new File(getSDPath() + "/" + fileName);
            FileOutputStream fout = new FileOutputStream(file);
            byte[] bytes = writestr.getBytes();
            fout.write(bytes);
            fout.close();
        } catch (Exception e) {
            QHADLog.e("写文件到SD错误:" + e.getMessage());
        }
    }
}
