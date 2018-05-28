package com.imgod.kk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.imgod.kk.utils.LogUtils;
import com.imgod.kk.utils.MediaPlayUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import okhttp3.Call;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

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

    private RequestCall requestPlatformOrderSizeCall;

    private void requestPlatformOrderSize() {
        requestPlatformOrderSizeCall = OkHttpUtils.get().url(ORDER_LIST_URL).build();
        requestPlatformOrderSizeCall.execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                requestPlatformOrderSize();
            }

            @Override
            public void onResponse(String response, int id) {
                parseResponse(response);
            }
        });
    }


    private static final String GET_TASK_URL = "http://www.mf178.cn/customer/order/get_tasks";

    private void requestGetTask(String amount, String count) {
        requestPlatformOrderSizeCall = OkHttpUtils.get().url(GET_TASK_URL)
                .addParams("amount", amount)
                .addParams("count", count)
                .build();
        requestPlatformOrderSizeCall.execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                requestPlatformOrderSize();
            }

            @Override
            public void onResponse(String response, int id) {
                parseGetTaskResponse();
            }
        });
    }


    private void parseGetTaskResponse() {

    }


    /**
     * 解析网络请求得到的数据
     */
    private void parseResponse(String content) {
        Document document = null;
        try {
            document = Jsoup.parse(content);
            Elements elements = document.getElementsByClass("table table-striped table-bordered table-advance table-hover text-center");
            LogUtils.e(TAG, elements.html());
            Element element = elements.select("tr").first();
            LogUtils.e(TAG, element.html());
            Elements tdElements = element.select("td");
            for (int i = 0; i < tdElements.size(); i++) {
                Element tempElement = tdElements.get(i);
                LogUtils.e(TAG, tempElement.html());
                String techphoneChargeName = getTelephoneChargeName(tempElement.text());
                int orderNum = getTelephoneChargeOrderNum(tempElement.getElementsByClass("text-success").get(0).text());
                LogUtils.e(TAG, techphoneChargeName);
                LogUtils.e(TAG, "数量:" + orderNum);
                if (techphoneChargeName.equals(selectTechphoneChargeName)) {
                    if (orderNum > 0) {
                        //如果该选项还有剩余订单的话,那这个时候应该先发起抢订单的操作
                        LogUtils.e(TAG, techphoneChargeName + "话费单有库存,请及时去抢单");
//                        MediaPlayUtils.playSound(MainActivity.this, "memeda.wav");
                        requestGetTask(techphoneChargeName.replace("元", ""), "1");
                    } else {
                        //如果没有数量 那就应该执行刷新操作了
                        requestPlatformOrderSize();
                    }
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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
    protected void onDestroy() {
        super.onDestroy();
        MediaPlayUtils.stopPlay();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private int selectId;
    private String selectTechphoneChargeName;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == selectId) {
            return super.onOptionsItemSelected(item);
        }
        selectId = id;
        if (null != requestPlatformOrderSizeCall) {
            requestPlatformOrderSizeCall.cancel();
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
