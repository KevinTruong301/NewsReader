package edu.fullerton.kevin.newsreader;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Kevin on 7/17/2018.
 */

public class ItemsActivity extends Activity implements AdapterView.OnItemClickListener{


    private RSSFeed feed;
    private FileIO io;

    private ListView itemsListView;
    private TextView titleTextView;

    private String TAG = "News Reader";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_items);

        io = new FileIO(getApplicationContext());

        titleTextView = (TextView) findViewById(R.id.titleTextView);
        itemsListView = (ListView) findViewById(R.id.itemsListView);

        itemsListView.setOnItemClickListener(this);

        new DownloadFeed().execute();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        RSSItem item = feed.getItem(i);

        Intent intent = new Intent(this, ItemsActivity.class);

        intent.putExtra("pubdate", item.getPubDate());
        intent.putExtra("title", item.getTitle());
        intent.putExtra("decription", item.getDescription());
        intent.putExtra("link", item.getLink());

        this.startActivity(intent);
    }

    class DownloadFeed extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String urlString = strings[0];

            //download the feed and write it to a file
            io.downloadFile();
            return "Feed downloaded";
        }

        @Override
        protected void onPostExecute(String s) {
            //super.onPostExecute(s);
            Log.d(TAG, "Feed downloaded");
            new ReadFeed().execute();
        }
    }

    class ReadFeed extends AsyncTask<Void, Void, Void>{
        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d(TAG, "onPostExecute: Feed Read");

            ItemsActivity.this.updateDisplay();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            feed = io.readFile();
            return null;
        }
    }

    public void updateDisplay() {

        if(feed == null) {
            titleTextView.setText("Unable to get RSS feed");
            return;
        }

        titleTextView.setText(feed.getTitle());

        ArrayList<RSSItem> items =  feed.getAllItems();

        ArrayList <HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

        for(RSSItem item : items) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("date", item.getPubDateFormatted());
            map.put("title", item.getTitle());
            data.add(map);
        }
        
        int resource = R.layout.listview_item;
        String[] from = {"date","title"};
        int[] to = {R.id.pubDateTextView, R.id.titleTextView};
        
        SimpleAdapter adapter = new SimpleAdapter(this, data, resource, from, to);
        itemsListView.setAdapter(adapter);

        Log.d(TAG, "updateDisplay: Feed displayed");

    }

}
