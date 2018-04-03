package com.caiyi.lottery;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.caiyi.data.TrendData;
import com.caiyi.ui.DDTrendChart;
import com.caiyi.ui.DDTrendChart.ISelectedChangeListener;
import com.caiyi.ui.LottoTrendView;
import com.lottery9188.Activity.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LottoTrendActivity extends Activity implements ISelectedChangeListener {

    private LottoTrendView mTrendView;
    final int maxSignleNum = 9;
    private DDTrendChart mTrendChart;

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_lotto_trend);
        initViews();
        loadData();
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message paramMessage) {
            super.handleMessage(paramMessage);
            LottoTrendActivity.this.mTrendChart.updateData("01", (ArrayList) paramMessage.obj);
        }
    };

    private void initViews() {
        this.mTrendView = (LottoTrendView) findViewById(R.id.ltv_trendView);
        this.mTrendChart = new DDTrendChart(this, this.mTrendView);
        this.mTrendView.setChart(this.mTrendChart);
        this.mTrendChart.setShowYilou(true);
        this.mTrendChart.setDrawLine(true);
        this.mTrendChart.setSelectedChangeListener(this);
    }

    private final OkHttpClient client = new OkHttpClient();

    private void loadData() {
        // 根据01/30.xml 或者是01/50.xm可以调整数字
        String url = "http://mobile.9188.com/data/app/zst/01/30.xml";

        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream inputStream = response.body().byteStream();
                try {
                    adapterData(inputStream);
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }
            }
        });
    }

    protected void adapterData(InputStream inputStream) throws XmlPullParserException, IOException {
        ArrayList arrayList = new ArrayList();
        Collection arrayList2 = new ArrayList();
        XmlPullParser newPullParser = XmlPullParserFactory.newInstance().newPullParser();
        newPullParser.setInput(inputStream, "utf-8");
        TrendData trendData;
        /**
         * <rows>
         <row pid="2018007" red="4,1,14,13,7,9,1,1,1,9,6,8,0,0,7,4,1,3,3,0,2,21,9,5,0,3,0,2,1,3,0,13,2" blue="46,23,24,4,7,2,40,11,17,15,1,0,12,14,39,5"/>
         <row pid="2018008" red="5,2,15,14,0,10,2,2,0,0,7,0,1,1,8,5,0,4,0,1,3,22,10,6,1,4,1,3,2,4,1,14,3" blue="47,24,25,5,8,3,41,12,18,16,2,1,0,15,40,6"/>
         <row pid="2018009" red="6,3,16,15,0,11,3,3,1,0,8,1,2,2,9,6,0,5,1,2,4,23,0,7,2,0,2,4,3,5,2,0,4" blue="48,25,26,6,9,4,0,13,19,17,3,2,1,16,41,7"/>
         <row pid="2018010" red="0,4,17,16,1,12,4,0,2,1,9,2,3,3,10,7,0,6,2,0,0,0,1,8,3,1,3,5,4,6,3,1,5" blue="49,26,0,7,10,5,1,14,20,18,4,3,2,17,42,8"/>
         <row pid="2018011" red="1,5,0,17,2,13,5,1,3,0,10,3,4,4,11,8,1,7,3,1,0,1,0,9,4,2,0,6,5,7,4,2,0" blue="50,27,1,8,11,6,2,15,21,19,0,4,3,18,43,9"/>
         <row pid="2018012" red="2,6,1,18,3,14,6,2,4,1,0,0,0,5,12,9,2,8,0,2,1,2,1,10,5,0,1,0,6,8,5,3,1" blue="51,28,2,9,12,7,3,16,22,20,1,0,4,19,44,10"/>
         <row pid="2018013" red="3,7,2,19,4,0,7,0,5,2,1,1,0,6,0,10,3,9,1,3,2,0,2,11,6,1,2,1,7,9,6,4,0" blue="52,29,3,10,13,0,4,17,23,21,2,1,5,20,45,11"/>
         <row pid="2018014" red="4,8,3,20,5,1,8,1,0,3,2,0,1,7,1,11,4,10,2,0,3,1,3,0,7,2,3,0,8,10,0,5,1" blue="53,30,4,11,14,1,0,18,24,22,3,2,6,21,46,12"/>
         <row pid="2018015" red="5,9,4,21,6,2,9,2,1,4,0,1,2,8,0,12,5,11,3,0,0,2,4,1,8,0,4,1,9,11,1,6,0" blue="54,31,5,12,15,2,1,19,25,23,4,3,7,22,0,13"/>
         <row pid="2018016" red="0,10,5,22,7,3,10,3,2,5,0,0,3,9,1,13,6,0,4,1,1,3,5,2,0,1,0,2,10,12,2,7,1" blue="55,32,6,13,16,3,2,20,26,24,5,4,8,23,1,0"/>
         <row pid="2018017" red="1,11,0,23,8,0,11,4,3,6,0,1,4,10,2,14,7,1,5,2,2,4,6,3,1,0,1,3,11,0,3,0,2" blue="56,33,7,14,17,4,3,21,27,25,6,0,9,24,2,1"/>
         <row pid="2018018" red="2,0,1,24,9,1,12,5,4,7,1,0,0,11,3,15,8,0,6,3,3,5,7,4,0,1,0,4,12,1,4,1,3" blue="57,34,8,15,18,5,0,22,28,26,7,1,10,25,3,2"/>
         <row pid="2018019" red="3,1,0,25,10,2,13,6,5,8,0,0,1,12,4,0,9,1,7,4,0,6,0,5,1,2,1,5,13,2,5,2,4" blue="58,35,9,16,19,6,1,23,0,27,8,2,11,26,4,3"/>
         <row pid="2018020" red="4,2,1,26,11,0,14,7,0,0,1,1,2,0,5,1,10,2,8,5,1,7,1,6,2,3,2,0,14,0,6,3,5" blue="59,36,10,17,0,7,2,24,1,28,9,3,12,27,5,4"/>
         <row pid="2018021" red="5,3,0,0,12,0,15,8,1,1,0,2,3,1,6,2,11,3,9,6,2,8,0,7,3,4,3,0,15,1,7,4,6" blue="60,37,11,18,1,8,3,25,2,29,10,4,13,0,6,5"/>
         <row pid="2018022" red="6,4,1,1,13,1,0,9,2,2,1,3,4,0,7,3,12,4,0,7,0,0,0,8,4,5,4,1,16,2,8,5,7" blue="61,38,0,19,2,9,4,26,3,30,11,5,14,1,7,6"/>
         <row pid="2018023" red="7,5,2,2,14,2,1,10,3,3,2,0,5,1,0,0,13,5,1,8,0,1,1,9,5,0,5,2,0,3,9,6,8" blue="62,39,1,20,3,10,5,27,4,31,12,6,15,2,8,0"/>
         <row pid="2018024" red="8,6,3,3,15,3,2,11,4,4,0,1,6,2,1,1,14,6,0,9,1,0,2,10,6,0,6,3,1,4,0,0,9" blue="63,0,2,21,4,11,6,28,5,32,13,7,16,3,9,1"/>
         <row pid="2018025" red="9,7,4,0,16,4,3,12,5,5,1,2,0,3,2,0,15,7,0,10,0,1,3,11,0,1,7,4,2,5,1,1,10" blue="64,1,3,22,5,12,7,29,6,33,14,8,17,0,10,2"/>
         <row pid="2018026" red="10,8,5,0,17,5,0,13,6,6,2,0,1,0,3,1,16,8,1,11,1,2,4,12,1,0,8,5,3,6,2,0,11" blue="65,2,4,0,6,13,8,30,7,34,15,9,18,1,11,3"/>
         <row pid="2018027" red="11,0,6,1,18,6,0,14,0,7,3,1,2,0,4,2,17,0,2,12,2,3,5,13,2,1,9,0,4,7,3,1,12" blue="66,3,5,1,0,14,9,31,8,35,16,10,19,2,12,4"/>
         <row pid="2018028" red="12,1,0,2,19,7,1,0,1,8,0,2,3,0,5,3,18,0,3,13,3,4,0,14,3,2,10,1,5,8,4,2,13" blue="67,4,6,2,1,15,10,32,9,36,17,11,20,3,13,0"/>
         <row pid="2018029" red="0,0,1,3,20,8,2,1,0,9,1,3,4,0,6,4,19,1,4,14,4,0,1,15,0,3,11,2,6,9,5,3,14" blue="68,5,7,3,0,16,11,33,10,37,18,12,21,4,14,1"/>
         <row pid="2018030" red="1,1,2,4,21,9,3,2,1,10,2,4,0,0,7,5,20,2,5,0,0,1,2,16,0,4,12,3,7,10,6,4,0" blue="69,6,8,4,1,17,0,34,11,38,19,13,22,5,15,2"/>
         <row pid="2018031" red="2,0,3,5,22,10,4,3,2,11,3,5,1,1,8,0,21,0,0,1,1,2,3,17,1,5,0,4,8,0,7,5,1" blue="70,7,9,5,2,18,1,35,12,39,20,14,23,0,16,3"/>
         <row pid="2018032" red="3,1,4,6,23,11,5,4,3,12,4,6,2,2,9,1,22,1,1,2,0,0,0,0,0,6,1,5,9,1,8,0,2" blue="71,8,10,6,3,0,2,36,13,40,21,15,24,1,17,4"/>
         <row pid="2018033" red="4,2,5,0,24,12,6,5,4,13,5,7,3,3,10,2,23,2,0,0,1,0,1,1,1,7,2,0,10,2,9,1,0" blue="72,9,11,7,4,0,3,37,14,41,22,16,25,2,18,5"/>
         <row pid="2018034" red="0,3,6,1,0,13,7,6,5,14,0,8,4,4,11,3,24,3,1,1,2,0,0,2,2,0,3,1,11,3,10,2,1" blue="73,10,12,8,5,1,4,38,15,42,23,17,26,3,0,6"/>
         <row pid="2018035" red="1,4,7,2,1,14,0,7,6,0,0,9,5,5,12,4,0,4,2,2,3,1,0,3,3,1,4,0,12,4,11,3,2" blue="74,11,13,9,6,2,5,39,16,43,24,18,27,4,0,7"/>
         <row pid="2018036" red="2,5,8,3,2,15,1,0,7,1,1,10,6,6,13,5,0,5,3,3,4,2,1,0,4,0,5,0,13,5,12,4,0" blue="75,12,14,0,7,3,6,40,17,44,25,19,28,5,1,8"/>
         <dis red="4,4,5,4,3,4,4,4,5,5,10,8,6,8,3,4,5,5,7,6,9,8,9,3,7,9,5,8,1,3,3,5,6" blue="0,1,2,2,3,3,4,0,1,0,1,3,1,3,3,3"/>
         <avg red="7,7,6,7,10,7,7,7,6,6,3,3,5,3,10,7,6,6,4,5,3,3,3,10,4,3,6,3,30,10,10,6,5" blue="30,30,15,15,10,10,7,30,30,30,30,10,30,10,10,10"/>
         <mmv red="12,11,17,26,24,15,15,14,7,14,10,10,6,12,13,15,24,11,9,14,4,23,10,17,8,7,12,6,16,12,12,14,14" blue="75,39,26,22,19,18,41,40,28,44,25,19,28,27,46,13"/>
         <mlv red="1,1,1,2,2,2,2,1,1,2,3,2,2,5,1,1,3,2,2,2,2,3,2,1,2,2,1,2,1,1,1,1,1" blue="0,1,1,1,1,2,1,0,1,0,1,1,1,1,2,1"/>
         </rows>
         */
        for (int eventType = newPullParser.getEventType(); XmlPullParser.END_DOCUMENT != eventType; eventType = newPullParser.next()) {
            String name = newPullParser.getName();
            if (eventType == XmlPullParser.START_TAG) {
                if ("row".equals(name)) {
                    trendData = new TrendData();
                    trendData.setType("row");
                    String attributeValue = newPullParser.getAttributeValue(null, "pid");
                    if (!(TextUtils.isEmpty(attributeValue) || attributeValue.length() <= 4)) {
                        attributeValue = attributeValue.substring(4);
                    }
                    trendData.setPid(attributeValue);
                    trendData.setRed(newPullParser.getAttributeValue(null, "red"));
                    trendData.setBlue(newPullParser.getAttributeValue(null, "blue"));
                    trendData.setBalls(newPullParser.getAttributeValue(null, "balls"));
                    trendData.setOes(newPullParser.getAttributeValue(null, "oe"));
                    trendData.setBss(newPullParser.getAttributeValue(null, "bs"));
                    trendData.setOne(newPullParser.getAttributeValue(null, "one"));
                    trendData.setTwo(newPullParser.getAttributeValue(null, "two"));
                    trendData.setThree(newPullParser.getAttributeValue(null, "three"));
                    trendData.setCodes(newPullParser.getAttributeValue(null, "codes"));
                    trendData.setSum(newPullParser.getAttributeValue(null, "sum"));
                    trendData.setSpace(newPullParser.getAttributeValue(null, "space"));
                    trendData.setNum(newPullParser.getAttributeValue(null, "num"));
                    trendData.setTimes(newPullParser.getAttributeValue(null, "times"));
                    trendData.setForm(newPullParser.getAttributeValue(null, "form"));
                    arrayList.add(trendData);
                } else if ("dis".equals(name)) {
                    trendData = new TrendData();
                    trendData.setType("dis");
                    trendData.setRed(newPullParser.getAttributeValue(null, "red"));
                    trendData.setBlue(newPullParser.getAttributeValue(null, "blue"));
                    trendData.setBalls(newPullParser.getAttributeValue(null, "balls"));
                    trendData.setOne(newPullParser.getAttributeValue(null, "one"));
                    trendData.setTwo(newPullParser.getAttributeValue(null, "two"));
                    trendData.setThree(newPullParser.getAttributeValue(null, "three"));
                    trendData.setNum(newPullParser.getAttributeValue(null, "num"));
                    arrayList2.add(trendData);
                } else if ("avg".equals(name)) {
                    trendData = new TrendData();
                    trendData.setType("avg");
                    trendData.setRed(newPullParser.getAttributeValue(null, "red"));
                    trendData.setBlue(newPullParser.getAttributeValue(null, "blue"));
                    trendData.setBalls(newPullParser.getAttributeValue(null, "balls"));
                    trendData.setOne(newPullParser.getAttributeValue(null, "one"));
                    trendData.setTwo(newPullParser.getAttributeValue(null, "two"));
                    trendData.setThree(newPullParser.getAttributeValue(null, "three"));
                    trendData.setNum(newPullParser.getAttributeValue(null, "num"));
                    arrayList2.add(trendData);
                } else if ("mmv".equals(name)) {
                    trendData = new TrendData();
                    trendData.setType("mmv");
                    trendData.setRed(newPullParser.getAttributeValue(null, "red"));
                    trendData.setBlue(newPullParser.getAttributeValue(null, "blue"));
                    trendData.setBalls(newPullParser.getAttributeValue(null, "balls"));
                    trendData.setOne(newPullParser.getAttributeValue(null, "one"));
                    trendData.setTwo(newPullParser.getAttributeValue(null, "two"));
                    trendData.setThree(newPullParser.getAttributeValue(null, "three"));
                    trendData.setNum(newPullParser.getAttributeValue(null, "num"));
                    arrayList2.add(trendData);
                } else if ("mlv".equals(name)) {
                    trendData = new TrendData();
                    trendData.setType("mlv");
                    trendData.setRed(newPullParser.getAttributeValue(null, "red"));
                    trendData.setBlue(newPullParser.getAttributeValue(null, "blue"));
                    trendData.setBalls(newPullParser.getAttributeValue(null, "balls"));
                    trendData.setOne(newPullParser.getAttributeValue(null, "one"));
                    trendData.setTwo(newPullParser.getAttributeValue(null, "two"));
                    trendData.setThree(newPullParser.getAttributeValue(null, "three"));
                    trendData.setNum(newPullParser.getAttributeValue(null, "num"));
                    arrayList2.add(trendData);
                }
            }
        }
        arrayList.addAll(arrayList2);
        mHandler.sendMessage(mHandler.obtainMessage(120, arrayList));
    }


    protected void onDestroy() {
        super.onDestroy();
    }

    public void onSelectedChange(TreeSet<Integer> treeSet, TreeSet<Integer> treeSet2) {

    }
}
