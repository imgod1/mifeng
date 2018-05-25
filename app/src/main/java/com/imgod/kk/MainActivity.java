package com.imgod.kk;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

    }

    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPlatformOrderSize();
            }
        });

        selectId = R.id.action_30;
        selectTechphoneChargeName = getString(R.string.action_30);
    }

    public static final String ORDER_LIST_URL = "http://www.mf178.cn/customer/order/mytasks";

    private List<TelephoneChargesBean> telephoneChargesBeanList = new ArrayList<>();

    AsyncTask asyncTask;

    private void requestPlatformOrderSize() {

        asyncTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Document document = null;
                try {
                    document = Jsoup.connect(ORDER_LIST_URL).get();
                    Elements elements = document.getElementsByClass("table table-striped table-bordered table-advance table-hover text-center");
                    Log.e("Main elements:", elements.html());
                    Element element = elements.select("tr").first();
                    Log.e("Main element:", element.html());
                    Elements tdElements = element.select("td");
                    telephoneChargesBeanList.clear();
                    for (int i = 0; i < tdElements.size(); i++) {
                        Element tempElement = tdElements.get(i);
                        Log.e("Main tempElement:", tempElement.html());
                        String techphoneChargeName = getTelephoneChargeName(tempElement.text());
                        int orderNum = getTelephoneChargeOrderNum(tempElement.getElementsByClass("text-success").get(0).text());
                        Log.e("Main tempElement text:", techphoneChargeName);
                        Log.e("Main tempElement span:", "数量:" + orderNum);
                        if (techphoneChargeName.equals(selectTechphoneChargeName)) {
                            if (orderNum > 0) {
                                //如果该选项还有剩余订单的话,那这个时候应该先发起抢订单的操作
                                Log.e("Main", techphoneChargeName + "话费单有库存,请及时去抢单");
                            } else {
                                //如果没有数量 那就应该执行刷新操作了
                                requestPlatformOrderSize();
                            }

                            break;
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();


    }

    private String getTelephoneChargeName(String text) {
        if (!TextUtils.isEmpty(text)) {
            int startPosition = 0;
            int endPosition = text.indexOf("元") + 1;
            return text.substring(startPosition, endPosition);
        }

        return null;
    }

    private int getTelephoneChargeOrderNum(String text) {
        if (!TextUtils.isEmpty(text)) {
            return Integer.parseInt(text.replace("单", ""));
        }
        return 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private int selectId;
    private String selectTechphoneChargeName;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == selectId) {
            return super.onOptionsItemSelected(item);
        }
        selectId = id;
        if (null != asyncTask && !asyncTask.isCancelled()) {
            asyncTask.cancel(true);
        }
        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_30:
                selectTechphoneChargeName = getString(R.string.action_30);
                break;
            case R.id.action_50:
                selectTechphoneChargeName = getString(R.string.action_50);
                break;
            case R.id.action_100:
                selectTechphoneChargeName = getString(R.string.action_100);
                break;
            case R.id.action_200:
                selectTechphoneChargeName = getString(R.string.action_200);
                break;
            case R.id.action_300:
                selectTechphoneChargeName = getString(R.string.action_300);
                break;
            case R.id.action_500:
                selectTechphoneChargeName = getString(R.string.action_500);
                break;
        }
        requestPlatformOrderSize();
        return super.onOptionsItemSelected(item);
    }
}
