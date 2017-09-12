package com.a2l.jsontosqlite;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DBAdapter sqlite_obj;

    Button get, store, select;
    List<String> list1, list2;

    InputStream is = null;

    String ip = "http://10.0.2.2/webservice/select.php";
    String line = null;
    String result = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sqlite_obj = new DBAdapter(MainActivity.this);

        get = (Button) findViewById(R.id.button1);
        store = (Button) findViewById(R.id.button2);
        select = (Button) findViewById(R.id.button3);
        list1 = new ArrayList<String>();
        list2 = new ArrayList<String>();
        select.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                select_seqlite();
            }
        });

        get.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                webservice();
                Toast.makeText(getBaseContext(), "Success : Webservice Call", Toast.LENGTH_SHORT).show();
            }
        });

        store.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                sqlite();
                Toast.makeText(getBaseContext(), "Stored in SQLite DB", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void sqlite() {
        // TODO Auto-generated method stub
        sqlite_obj.open();
        sqlite_obj.deleteAll();

        for (int i = 0; i < list1.size(); i++) {
            sqlite_obj.insert(list1.get(i).toString(), list2.get(i).toString());
        }
        sqlite_obj.close();
    }

    private void select_seqlite() {
        // TODO Auto-generated method stub
        sqlite_obj.open();
        Cursor c = sqlite_obj.getAllData();
        if (c.moveToFirst()) {
            do {
                DisplayContact(c);
            } while (c.moveToNext());
        }
        sqlite_obj.close();
    }

    private void DisplayContact(Cursor c) {
        // TODO Auto-generated method stub
        Toast.makeText(getBaseContext(), "ID: " + c.getString(0) + "	\n"
                + "Name: " + c.getString(1), Toast.LENGTH_LONG).show();
    }

    private void webservice() {
        // TODO Auto-generated method stub

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(ip);
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
        } catch (Exception e) {
            Log.e("Webservice 1", e.toString());
        }
        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();

            while ((line = reader.readLine()) != null) {

                sb.append(line + "\n");
            }

            is.close();
            result = sb.toString();
        } catch (Exception e) {
            Log.e("Webservice 2", e.toString());
        }
        try {

            JSONArray ja = new JSONArray(result);
            JSONObject jo = null;


            for (int i = 0; i < ja.length(); i++) {

                jo = ja.getJSONObject(i);
                list1.add(jo.getString("id"));
                list2.add(jo.getString("uname"));
            }
        } catch (Exception e) {
            Log.e("Webservice 3", e.toString());
        }
    }

}
