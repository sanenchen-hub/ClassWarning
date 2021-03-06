package com.sanenchen.classWarring.UI;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sanenchen.classWarring.ListViewOrRecyclerView.SearchItem.ItemRAdapter;
import com.sanenchen.classWarring.ListViewOrRecyclerView.SearchItem.WarningSearchAd;
import com.sanenchen.classWarring.R;
import com.sanenchen.classWarring.getThings.getDataJson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ClassWarningSearchActivity extends AppCompatActivity {
    String query1;
    private List<WarningSearchAd> musicList = new ArrayList<>();
   // public String getT = null;
    static int HowMany = 0;
    static String[] WarningTitle = null;
    static String[] WarningStudent = null;
    static String[] WarningID = null;
    static String[] WarningDate = null;
    private List<WarningSearchAd> SearchList = new ArrayList<>();
    ProgressDialog progressDialog = null;
    public static final int ListViewSet = 1;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classwarning_search);
        SearchView searchView = findViewById(R.id.SearchView);
        searchView.onActionViewExpanded();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.listView2);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //单机搜索按钮时激发该方法
            @Override
            public boolean onQueryTextSubmit(final String query) {
                musicList.clear();
                query1 = query;
                progressDialog = new ProgressDialog(ClassWarningSearchActivity.this);
                progressDialog.setTitle("查询中");
                progressDialog.setMessage("正在查询，请稍后...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        char fir = query1.charAt(0);
                        getDataJson getDataJson = new getDataJson();
                        String jsonData = getDataJson.getSearchReply("SearchClassWarning", ClassWarningSearchActivity.this, null, fir+"");

                        try {
                            JSONArray jsonArray = new JSONArray(jsonData);
                            HowMany = jsonArray.length();
                            WarningTitle = new String[HowMany];
                            WarningStudent = new String[HowMany];
                            WarningID = new String[HowMany];
                            WarningDate = new String[HowMany];
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                WarningTitle[i] = jsonObject.getString("WarringT");
                                WarningStudent[i] = jsonObject.getString("WarringStudent");
                                WarningID[i] = jsonObject.getString("id");
                                WarningDate[i] = jsonObject.getString("uplodDate");
                            }
                        } catch (Exception e) {

                        }

                        if (HowMany == 0) {
                            Looper.prepare();

                            Toast.makeText(ClassWarningSearchActivity.this, "未找到有关" + query + "的信息", Toast.LENGTH_SHORT).show();
                            Message message = new Message();
                            message.what = 2;
                            handler.sendMessage(message);
                            Looper.loop();
                        } else {
                            SearchList = new ArrayList<>();
                            for (int i = HowMany - 1; i >= 0; i--) {
                                WarningSearchAd warningSearchAd = new WarningSearchAd(WarningTitle[i], WarningStudent[i], WarningID[i], WarningDate[i]);
                                musicList.add(warningSearchAd);
                            }

                            Message message = new Message();
                            message.what = ListViewSet;
                            handler.sendMessage(message);

                        }
//                        Toast.makeText(getApplicationContext(), WarningID[0], Toast.LENGTH_SHORT).show();

                    }
                }).start();


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case ListViewSet:
                    ItemRAdapter adapter = new ItemRAdapter(musicList);
                    recyclerView.setAdapter(adapter);
                    progressDialog.dismiss();
                    break;
                case 2:
                    progressDialog.dismiss();
                    break;
            }
            return true;
        }
    });
}
