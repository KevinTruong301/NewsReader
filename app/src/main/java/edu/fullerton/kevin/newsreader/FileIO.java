package edu.fullerton.kevin.newsreader;

import android.content.Context;
import android.util.Log;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


/**
 * Created by Kevin on 7/21/2018.
 */

public class FileIO {
    private final String FILENAME = "news_feed.xml";
    private Context context = null;
    private String TAG = "FileIO";


    public FileIO(Context context) {
        this.context = context;
    }

    public void downloadFile(){
        try{
            URL url = new URL("http://rss.cnn.com/rss/cnn_tech.rss");
            InputStream in = url.openStream();

            FileOutputStream out = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);

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

    }

    public RSSFeed readFile(){
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader xmlReader = parser.getXMLReader();

            RSSFeedHandler theRSSFeedHandler = new RSSFeedhandler();
            xmlReader.setContentHandler(theRSSFeedHandler);

            FileInputStream in = context.openFileInput(FILENAME);

            InputSource is = new InputSource();
            xmlReader.parse(is);

            RSSFeed feed = theRSSFeedHandler.getFeed();
            return feed;

        }
        catch (Exception i){
            Log.d(TAG, "readFile: " + i);
            return null;
        }

    }
}
