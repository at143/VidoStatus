package com.growthskyinfotech.vidostatus;

import static android.content.Context.CONNECTIVITY_SERVICE;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.animation.AlphaAnimation;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

public class MyUtils {


    // TODO Enter your Device Test Id from Logcat
    public static final String ADMOB_TEST_ID = "13E2B304F0D054B034E2A31EF4EB57E0";

    public static AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);

    // Default values for Native Ads.
    public static final String ADS_ENABLE = "ADS_ENABLE";
    public static final String OPEN_AD_ID = "OPEN_AD_ID";

    public static String getPermissionStatus(Activity activity, String androidPermissionName) {
        if (ContextCompat.checkSelfPermission(activity, androidPermissionName) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, androidPermissionName)) {
                return "blocked";
            }
            return "denied";
        }
        return "granted";
    }

    public static final void saveWatermark(Activity context) {

        // TODO Add ic_watermark.png to @Drawable Folder.
        Bitmap decodeResource = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_watermark);
        File file = new File(context.getExternalCacheDir(), "water_mark.png");
        if (!file.exists()) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                decodeResource.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (Exception e10) {
                e10.printStackTrace();
            }
        }
    }

    public static final String ONESIGNAL_ID = "279591b6-cb43-46f2-935d-c151aec6595b";

    public static String getStoreVideoExternalStorage(Activity context) {

        String folder = "VideoOutput";

        File file = new File(Environment.getExternalStorageDirectory() + "/Download/" + context.getString(R.string.app_name), folder);

        if (!file.exists())
            file.mkdirs();

        return file.getAbsolutePath();

    }

    public static void shareImageVideoOnWhatsapp(Context context, String filePath, boolean isVideo) {

        try {


            Uri imageUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", new File(filePath));
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);

            PackageManager pm = context.getPackageManager();
            boolean isInstalled = isPackageInstalled("com.whatsapp", pm);

            if (isInstalled) {
                shareIntent.setPackage("com.whatsapp");
            } else {
                shareIntent.setPackage("com.whatsapp.w4b");
            }

            shareIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_txt) + context.getPackageName());
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            if (isVideo) {
                shareIntent.setType("video/*");
            } else {
                shareIntent.setType("image/*");
            }
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(shareIntent);
        } catch (Exception e) {
            setToast(context, "Whatsapp not installed.");
        }
    }


    public static boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static void setToast(Context context, String str) {
        Toast toast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }


}
