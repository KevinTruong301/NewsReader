package edu.fullerton.kevin.newsreader;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by Kevin on 7/17/2018.
 */

public class ItemsActivity extends Activity{
    private static String URL_STRING = "http://rss.cnn.com/rss/cnn_tech.rss";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_items);

        new DownloadFeed().execute(URL_STRING);
    }

    class DownloadFeed extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String urlString = strings[0];

            //download the feed and write it to a file
            final String FILENAME = "news_feed.xml";
            try{
                URL url = new URL("http://rss.cnn.com/rss/cnn_tech.rss");
                InputStream in = url.openStream();

                FileOutputStream out = openFileOutput(FILENAME, MODE_PRIVATE);

                byte[] buffer = new byte[1024];
                int bytesRead = in.read(buffer);
                while(bytesRead != -1){
                    out.write(buffer, 0, bytesRead);
                    bytesRead = in.read(buffer);
                }
                out.close();
                in.close();
            }
            catch (IOException e){
                Log.e("NewsReader", e.toString());
            }
            return "Feed downloaded";
        }

        @Override
        protected void onPostExecute(String s) {
            //super.onPostExecute(s);
            Toast.makeText(ItemsActivity.this, s, Toast.LENGTH_SHORT).show();
        }
    }


}
