package org.o7planning.hale_2.Charts;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.formatter.PercentFormatter;

import org.o7planning.hale_2.Charts.listviewitems.ChartItem;
import org.o7planning.hale_2.R;

import java.util.ArrayList;

public class ChartAdapter extends RecyclerView.Adapter<ChartAdapter.ItemViewHolder> {

    ArrayList<ChartItem> mChartItems;
    Context mContext;
    Typeface mTypeface;

    public ChartAdapter(Context context, ArrayList<ChartItem> arrayList) {
        mChartItems = arrayList;
        mContext = context;
        mTypeface = Typeface.createFromAsset(context.getAssets(), "OpenSans-Regular.ttf");
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        Chart mChart;
        View mView;
        int index;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            mChart = itemView.findViewById(R.id.chart);
            mView = itemView;
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;

        if (viewType == 0) {
            view = LayoutInflater.from(mContext).inflate(R.layout.list_item_barchart, parent, false);
        } else if (viewType == 1) {
            view = LayoutInflater.from(mContext).inflate(R.layout.list_item_linechart, parent, false);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.list_item_piechart, parent, false);
        }

        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        itemViewHolder.index = viewType;
        return itemViewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        // return the views type
        ChartItem ci = mChartItems.get(position);
        return ci != null ? ci.getItemType() : 0;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int position) {

//        itemViewHolder.mView = mChartItems.get(position).getView(position, itemViewHolder.mView, mContext);


        if (itemViewHolder.index == 0) {
            //BarChart
            BarChart chart = itemViewHolder.itemView.findViewById(R.id.chart);

            chart.getDescription().setEnabled(false);
            chart.setDrawGridBackground(false);
            chart.setDrawBarShadow(false);

            XAxis xAxis = chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setTypeface(mTypeface);
            xAxis.setDrawGridLines(false);
            xAxis.setDrawAxisLine(true);

            YAxis leftAxis = chart.getAxisLeft();
            leftAxis.setTypeface(mTypeface);
            leftAxis.setLabelCount(5, false);
            leftAxis.setSpaceTop(20f);
            leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

            YAxis rightAxis = chart.getAxisRight();
            rightAxis.setTypeface(mTypeface);
            rightAxis.setLabelCount(5, false);
            rightAxis.setSpaceTop(20f);
            rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

            mChartItems.get(position).mChartData.setValueTypeface(mTypeface);

            // set data
            chart.setData((BarData) mChartItems.get(position).mChartData);
            chart.setFitBars(true);

            // do not forget to refresh the chart
//        holder.chart.invalidate();
            chart.animateY(700);

        } else if (itemViewHolder.index == 1) {

            // LineChart
            LineChart chart = itemViewHolder.itemView.findViewById(R.id.chart);

            chart.getDescription().setEnabled(false);
            chart.setDrawGridBackground(false);

            XAxis xAxis = chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setTypeface(mTypeface);
            xAxis.setDrawGridLines(false);
            xAxis.setDrawAxisLine(true);

            YAxis leftAxis = chart.getAxisLeft();
            leftAxis.setTypeface(mTypeface);
            leftAxis.setLabelCount(5, false);
            leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

            YAxis rightAxis = chart.getAxisRight();
            rightAxis.setTypeface(mTypeface);
            rightAxis.setLabelCount(5, false);
            rightAxis.setDrawGridLines(false);
            rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

            // set data
            chart.setData((LineData) mChartItems.get(position).mChartData);

            // do not forget to refresh the chart
            // holder.chart.invalidate();
            chart.animateX(750);

//            chart.invalidate();

        } else {
            // Pie Chart
            PieChart chart = itemViewHolder.itemView.findViewById(R.id.chart);

            chart.getDescription().setEnabled(false);
            chart.setHoleRadius(52f);
            chart.setTransparentCircleRadius(57f);

//            SpannableString mCenterText = mChartItems.get(position);
//            chart.setCenterText(mCenterText);

            chart.setCenterTextTypeface(mTypeface);
            chart.setCenterTextSize(9f);
            chart.setUsePercentValues(true);
            chart.setExtraOffsets(5, 10, 50, 10);

            ChartData<?> mChartData = mChartItems.get(position).mChartData;
            mChartData.setValueFormatter(new PercentFormatter());
            mChartData.setValueTypeface(mTypeface);
            mChartData.setValueTextSize(11f);
            mChartData.setValueTextColor(Color.WHITE);
            // set data
            chart.setData((PieData) mChartData);

            Legend l = chart.getLegend();
            l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
            l.setOrientation(Legend.LegendOrientation.VERTICAL);
            l.setDrawInside(false);
            l.setYEntrySpace(0f);
            l.setYOffset(0f);

            // do not forget to refresh the chart
            // holder.chart.invalidate();
            chart.animateY(900);
        }

    }

    @Override
    public int getItemCount() {
        return mChartItems.size();
    }


}
