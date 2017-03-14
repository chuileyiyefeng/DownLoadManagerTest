package my.com.downloadmanagertest;

import android.app.Activity;
import android.app.DownloadManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
    private static final String TAG = "Tag";
    String ApkDownLoadUrl = "http://download.sj.qq.com/upload/connAssitantDownload/upload/MobileAssistant_1.apk";
    private DownloadManager downloadManager;
    private long id;
    Button btn_download;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_download = (Button) findViewById(R.id.btn_download);
        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DoawAsync().execute(ApkDownLoadUrl);
            }
        });
    }

    class DoawAsync extends AsyncTask<String, Integer, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(params[0]));
//            * 文件将存放在外部存储的确实download文件内，如果无此文件夹，创建之，如果有，下面将返回false。
//            * 系统有个下载文件夹，比如小米手机系统下载文件夹  SD卡--> Download文件夹
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdir();
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "my.apk");
//            指定下载的网络类型
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
//            定制Notification样式
            request.setTitle("标题");
            request.setDescription("描述");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//            设置下载文件类型
//            这是安卓.apk文件的类型。有些机型必须设置此方法，才能在下载完成后，点击通知栏的Notification时，才能正确的打开安装界面。
            request.setMimeType("application/vnd.android.package-archive");
//            开始下载:downloadManager.enqueue(request);
//            每下载的一个文件对应一个id，通过此id可以查询数据。
            id = downloadManager.enqueue(request);
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            DownloadManager.Query query = new DownloadManager.Query();
            Cursor cursor = downloadManager.query(query.setFilterById(id));
            if (cursor != null && cursor.moveToFirst()) {
                //下载的文件到本地的目录
                String address = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                //已经下载的字节数
                int bytes_downloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                //总需下载的字节数
                int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                //Notification 标题
                String title = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE));
                //描述
                String description = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION));
                //下载对应id
                long id = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_ID));
                //下载文件名称
                String filename = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                //下载文件的URL链接
                String url = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_URI));
                Log.e(TAG, "onPostExecute: " + address + " " + bytes_downloaded + bytes_total + " " + title + description + " " + id + "  name :" + filename + " " + url);
                cursor.close();
            }
        }
    }

}
