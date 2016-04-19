package br.com.metragemrio;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.analytics.HitBuilders;

import java.util.ArrayList;
import java.util.List;

import br.com.metragemrio.content.DataCallback;
import br.com.metragemrio.content.Request;
import br.com.metragemrio.content.RestClient;
import br.com.metragemrio.database.MeterageDataSource;
import br.com.metragemrio.model.Meterage;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    public static final String ANALYTICS_ID = "UA-68984326-1";
    public static final String BROADCAST_DATA_RECEIVED = "metragem_rio_main_activity";

    private MeterageAdapter mMeterageAdapter;
    protected StickyListHeadersListView mListView;
    private LineChart mChart;
    private List<Meterage> all;
    private ProgressDialog mProgressDialog;
    private View mHeaderView;
    private ListItemClick mListItemClick;
    private SwipeRefreshLayout mPullToRefresh;
    private boolean mOpenedScreen;
    private View mLastMeasure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Metragem Rio Itajaí-Açu");
        setActionBarColor();

        mOpenedScreen = true;
        Request request = RestClient.create();
        request.getData(new DataCallback());

        SharedPreferences settings = getSharedPreferences("settings", Context.MODE_PRIVATE);
        boolean isFirstOpen = settings.getBoolean("isFirstOpen", true);
        if (isFirstOpen) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Carregando dados...");
            mProgressDialog.show();
        }

        mPullToRefresh = (SwipeRefreshLayout) findViewById(R.id.pull_to_refresh);
        mPullToRefresh.setOnRefreshListener(this);

        mMeterageAdapter = new MeterageAdapter(getBaseContext());
        mListView = (StickyListHeadersListView) findViewById(R.id.list_view);

        MeterageDataSource dataSource = new MeterageDataSource();
        all = dataSource.getAll();
        setActionBarColor();
        if (mHeaderView == null) {
            mLastMeasure = LayoutInflater.from(this).inflate(R.layout.last_measure, null);
            mListView.addHeaderView(mLastMeasure);

            mHeaderView = LayoutInflater.from(this).inflate(R.layout.linechart, null);
            mListView.addHeaderView(mHeaderView, null, false);
        }
        setHeader();
        mListView.setAdapter(mMeterageAdapter);
        mMeterageAdapter.setContent(all);
        mListItemClick = new ListItemClick(all);
        mListView.setOnItemClickListener(mListItemClick);
    }

    private void setActionBarColor() {

        ColorDrawable colorDrawable;
        if (all != null && all.size() > 0) {
            Meterage meterage = all.get(0);
            int color = getColor(meterage.getStatus());
            colorDrawable = new ColorDrawable(getResources().getColor(color));
        } else {
            colorDrawable = new ColorDrawable(getResources().getColor(R.color.red));
        }
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
    }


    public static int getColor(String statusText) {
        if (statusText.equals("Atenção"))
            return R.color.yellow;
        else if (statusText.equals("Alerta"))
            return R.color.orange;
        else if (statusText.equals("Emergência"))
            return R.color.red;
        return R.color.green;
    }

    public void applyColor(ImageView imageView, int color) {
        Drawable image = imageView.getDrawable();
        image.mutate().setColorFilter(AppApplication.getContext().getResources().getColor(color), PorterDuff.Mode.SRC_ATOP);
        imageView.setImageDrawable(image);
    }

    private void setHeader() {

        MeterageDataSource dataSource = new MeterageDataSource();
        List<Meterage> m30 = dataSource.get30();
        if (m30 != null && m30.size() > 0) {

            Meterage firstMeterage = m30.get(0);
            ImageView imageView = (ImageView) mLastMeasure.findViewById(R.id.imageView);
            applyColor(imageView, getColor(firstMeterage.getStatus()));
            TextView lastMeasureLevel = (TextView) mLastMeasure.findViewById(R.id.level);
            lastMeasureLevel.setText(firstMeterage.getLevel() + "m");
            TextView lastMeasureHour = (TextView) mLastMeasure.findViewById(R.id.hour);
            lastMeasureHour.setText(DateFormat.format("kk:mm", firstMeterage.getTimestamp() * 1000));
            mLastMeasure.findViewById(R.id.layout).setVisibility(View.VISIBLE);
            mLastMeasure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AppApplication.getTracker().setScreenName("MainActivity");
                    AppApplication.getTracker().send(new HitBuilders.EventBuilder().setCategory("Click")
                            .setAction("Header").setLabel("Last Measure").build());

                    if (all.size() > 0) {
                        Meterage meterage = all.get(0);
                        DetailDialog.show(view.getContext(), meterage);
                    }
                }
            });

            mChart = (LineChart) mHeaderView.findViewById(R.id.chart1);

            mChart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AppApplication.getTracker().setScreenName("MainActivity");
                    AppApplication.getTracker().send(new HitBuilders.EventBuilder().setCategory("Click")
                            .setAction("Header").setLabel("Chart").build());
                }
            });

            mChart.clear();
            // no description text
            mChart.setDescription("");
            mChart.setNoDataTextDescription("Não foi possível recuperar os dados.");
//            mChart.setBackgroundColor(getResources().getColor(R.color.light_blue));

            // enable touch gestures
            mChart.setTouchEnabled(false);
            mChart.setClickable(false);

            mChart.setDragDecelerationFrictionCoef(0.9f);

            // enable scaling and dragging
            mChart.setDragEnabled(false);
            mChart.setScaleEnabled(false);
            mChart.setDrawGridBackground(false);
            mChart.setHighlightPerDragEnabled(false);

            // if disabled, scaling can be done on x- and y-axis separately
            mChart.setPinchZoom(true);

            if (mOpenedScreen) {
                mChart.animateX(2500);
                mOpenedScreen = !mOpenedScreen;
            } else {
                mChart.animateX(0);
            }

            XAxis xAxis = mChart.getXAxis();
            xAxis.disableGridDashedLine();
            xAxis.setEnabled(false);

            YAxis leftAxis = mChart.getAxisLeft();
            leftAxis.setTextColor(ColorTemplate.getHoloBlue());
            leftAxis.setStartAtZero(false);
            leftAxis.setAxisMinValue(getMin(m30) - 0.5f);
            leftAxis.setAxisMaxValue(getMax(m30) + 0.5f);
            leftAxis.setShowOnlyMinMax(true);
            leftAxis.setTextSize(14f);

            YAxis rightAxis = mChart.getAxisRight();
            rightAxis.disableGridDashedLine();
            rightAxis.setEnabled(false);

            // add data
            setData(m30);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            AppApplication.getTracker().setScreenName("MainActivity");
            AppApplication.getTracker().send(new HitBuilders.EventBuilder().setCategory("Click")
                    .setAction("Settings").setLabel("Settings").build());

            Intent i = new Intent(this, OptionsActivity.class);
            startActivityForResult(i, 1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean success = intent.getExtras().getBoolean("success");
            if (mProgressDialog != null)
                mProgressDialog.dismiss();

            if (mPullToRefresh != null)
                mPullToRefresh.setRefreshing(false);

            SharedPreferences settings = getSharedPreferences("settings", Context.MODE_PRIVATE);

            if (success) {

                AppApplication.getTracker().setScreenName("MainActivity");
                AppApplication.getTracker().send(new HitBuilders.EventBuilder().setCategory("Data Receiver")
                        .setAction("Success").setLabel("Success").build());

                SharedPreferences.Editor edit = settings.edit();
                edit.putBoolean("isFirstOpen", false);
                edit.commit();

                MeterageDataSource dataSource = new MeterageDataSource();
                all = dataSource.getAll();
                mListItemClick.updateContent(all);
                mMeterageAdapter.setContent(all);
                setHeader();
                Toast.makeText(getApplicationContext(), "Os dados foram atualizados.", Toast.LENGTH_LONG).show();
            } else {

                boolean isFirstOpen = settings.getBoolean("isFirstOpen", true);
                AppApplication.getTracker().setScreenName("MainActivity");
                AppApplication.getTracker().send(new HitBuilders.EventBuilder().setCategory("Data Receiver")
                        .setAction("Failure").setLabel(String.valueOf(isFirstOpen)).build());

                Toast.makeText(getApplicationContext(), "Não foi possível carregar os dados. Verifique a sua conexão com a internet.", Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mMessageReceiver,
                new IntentFilter(BROADCAST_DATA_RECEIVED));
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mMessageReceiver);
    }

    public float getMax(List<Meterage> list) {
        float max = 0f;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getLevel() > max) {
                max = list.get(i).getLevel();
            }
        }
        return max;
    }

    public float getMin(List<Meterage> list) {
        float min = 20f;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getLevel() < min) {
                min = list.get(i).getLevel();
            }
        }
        return min;
    }

    private void setData(List<Meterage> all) {

        int count;
        if (all.size() > 30)
            count = 30;
        else
            count = all.size();


        ArrayList<String> xVals = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            xVals.add("");
        }

        ArrayList<Entry> yVals1 = new ArrayList<>();

        int y = count - 1;
        for (int i = 0; i < count; i++) {
            yVals1.add(new Entry(all.get(y--).getLevel(), i));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals1, "Gráfico: Últimas medições (em metros)");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(ColorTemplate.getHoloBlue());
        set1.setLineWidth(4f);
        set1.setCircleSize(0f);
        set1.setFillAlpha(65);
        set1.setFillColor(ColorTemplate.getHoloBlue());
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setDrawCircleHole(false);

        ArrayList<LineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);
        data.setValueTextColor(Color.BLACK);
        data.setValueTextSize(0f);

        // set data
        mChart.setData(data);
    }

    @Override
    public void onRefresh() {
        Request request = RestClient.create();
        request.getData(new DataCallback());
    }
}
