package com.dattaraj.vibmonitor;

import java.text.DecimalFormat;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class DrawView extends View {
    Paint paint = new Paint();
    int z = 10;
    private float data[];
    private float dataFFT[];
    private String dataFFTLabels[];
	private int fftN = MainActivity.fftN;
	private final static int Y_LIMIT = 15;

	private GraphicalView mChart;
	private GraphicalView fftChart;
	
    public GraphicalView getFftChart() {
		return fftChart;
	}

	public void setFftChart(GraphicalView fftChart) {
		this.fftChart = fftChart;
	}

	public float[] getData() {
		return data;
	}

	public void setData(float[] data) {
		this.data = data;
		
		// Do the fft
		this.dataFFT = doFFT(data);
		this.dataFFT[0] = 0.0f;
		
		dataFFTLabels = new String[fftN];
		// Set frequency labels
		mRenderer2.setXLabels(0);
		DecimalFormat df = new DecimalFormat("0.00");
		df.setMaximumFractionDigits(2);
		for(int i=0; i<fftN; i=i + Math.round(fftN/10)) {
			float tmp = ((i+1)*1000.0f/MainActivity.DELAY)/fftN;
			mRenderer2.addXTextLabel(i+1, df.format(tmp));
		}
		
		reChart();
	}

	public GraphicalView getmChart() {
		return mChart;
	}

	public void setmChart(GraphicalView mChart) {
		this.mChart = mChart;
	}

	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	private XYMultipleSeriesDataset mDataset2 = new XYMultipleSeriesDataset();
    private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
    private XYMultipleSeriesRenderer mRenderer2 = new XYMultipleSeriesRenderer();
    private XYSeries mCurrentSeries,mCurrentSeries2;
    private XYSeriesRenderer mCurrentRenderer,mCurrentRenderer2;

    public DrawView(Context context) {
        super(context);
        data = new float[fftN];
        dataFFT = new float[fftN];

        // Chart 1 code
        mCurrentSeries = new XYSeries("Time domain data");
        mCurrentRenderer = new XYSeriesRenderer();
        mCurrentRenderer.setPointStyle(PointStyle.SQUARE);
        mCurrentRenderer.setFillPoints(true);
        mCurrentRenderer.setShowLegendItem(false);
        mRenderer.addSeriesRenderer(mCurrentRenderer);
        mDataset.addSeries(mCurrentSeries);
        setChartSettings(mRenderer, "Acceleration vs Time Chart", "Points of time", "Acceleration - m/s", 0, fftN, -Y_LIMIT, Y_LIMIT,
        		Color.BLUE,Color.CYAN);
        mRenderer.setXLabels(10);
        mRenderer.setYLabels(10);
        mRenderer.setShowGrid(false);
        mRenderer.setPanEnabled(false, false);
        mRenderer.setZoomEnabled(false, false);
        mChart = ChartFactory.getLineChartView(context, mDataset, mRenderer);
        mChart.setBackgroundColor(Color.TRANSPARENT);

        // Chart 2 code
        mCurrentSeries2 = new XYSeries("FFT data");
        mCurrentRenderer2 = new XYSeriesRenderer();
        mCurrentRenderer2.setPointStyle(PointStyle.SQUARE);
        mCurrentRenderer2.setFillPoints(true);
        mCurrentRenderer2.setShowLegendItem(false);
        mRenderer2.addSeriesRenderer(mCurrentRenderer2);
        mDataset2.addSeries(mCurrentSeries2);
        setChartSettings2(mRenderer2, "Frequency Distribution Chart for Acceleration", "Frequency Hz", "Acceleration - m/s", 0, fftN/2, 0, 2*Y_LIMIT,
        		Color.BLUE,Color.CYAN);
        mRenderer2.setXLabels(10);
        mRenderer2.setYLabels(10);
        mRenderer2.setShowLabels(true);
        mRenderer2.setPanEnabled(false, false);
        mRenderer2.setZoomEnabled(false, false);        
        fftChart = ChartFactory.getBarChartView(context, mDataset2, mRenderer2, Type.DEFAULT);
    }
    
    public void reChart() {
		mCurrentSeries.clear();
    	for(int i = 0; i < data.length; i++) {
            mCurrentSeries.add(i+1, data[i]);
    	}

		mCurrentSeries2.clear();
    	for(int i = 0; i < dataFFT.length; i++) {
            mCurrentSeries2.add(i+1, dataFFT[i]);
    	}
    }

    @Override
    public void onDraw(Canvas canvas) {
            // canvas.drawLine(0, 0, 10*z, 10*z, paint);
            // canvas.drawLine(10*z, 0, 0, 10*z, paint);
    }
    
    public void setCoordinates(int x, int y, int z) {
    	this.z = z;
    }

    private void setChartSettings(XYMultipleSeriesRenderer renderer,
            String title, String xTitle, String yTitle, double xMin,
            double xMax, double yMin, double yMax, int axesColor,
            int labelsColor) {
        // TODO Auto-generated method stub

        renderer.setChartTitle(title);
        renderer.setXTitle(xTitle);
        renderer.setYTitle(yTitle);
        renderer.setXAxisMin(xMin);
        renderer.setXAxisMax(xMax);
        renderer.setLabelsTextSize(20);
        renderer.setYAxisMin(yMin);
        renderer.setYAxisMax(yMax);
        renderer.setMargins(new int[] {15, 60, 0, 15});
        renderer.setAxesColor(axesColor);
        renderer.setLabelsColor(labelsColor);
        renderer.setYLabelsPadding(20f);
        renderer.setBarSpacing(0.5f);
    }

    private void setChartSettings2(XYMultipleSeriesRenderer renderer,
            String title, String xTitle, String yTitle, double xMin,
            double xMax, double yMin, double yMax, int axesColor,
            int labelsColor) {
        // TODO Auto-generated method stub

        renderer.setChartTitle(title);
        renderer.setXTitle(xTitle);
        renderer.setYTitle(yTitle);
        renderer.setXAxisMin(xMin);
        renderer.setXAxisMax(xMax);
        renderer.setLabelsTextSize(20);
        //renderer.setYAxisMin(yMin);
        //renderer.setYAxisMax(yMax);
        renderer.setMargins(new int[] {15, 60, 0, 15});
        renderer.setAxesColor(axesColor);
        renderer.setLabelsColor(labelsColor);
        renderer.setYLabelsPadding(20f);
        renderer.setBarSpacing(1f);
    }
    
    private float[] doFFT(float[] array) {
    	
        if(array == null)
    		return null;
    	
    	int n = array.length;
        double[] re = new double[n];
        double[] im = new double[n];
        float mag[] = new float[n];

    	FFT fft = new FFT(n);
    	for(int i = 0; i < n ; i++) {
    		re[i] = (double)(array[i]);
    		im[i] = 0;
    	}
    	
    	// Do fft and transform the matrix
    	fft.fft(re, im);
    	
    	for (int i = 0; i < n ; i++)
    		mag[i] = (float)(Math.sqrt(re[i]*re[i] + im[i]*im[i]));

    	return mag;
    }

    
    private float[] doFFT2(float[] array) {
    	
        if(array == null)
    		return null;
    	
    	int n = array.length;
        float mag[] = new float[n];
        Complex inputs[] = new Complex[n];

    	// FFT fft = new FFT(n);
    	for(int i = 0; i < n ; i++)
    		inputs[i] = new Complex((double)(array[i]), 0);
    	
    	Complex result[] = FFT2.fft(inputs);
    	
    	for (int i = 0; i < n ; i++)
    		mag[i] = (float)(result[i].abs()); // (float)(Math.sqrt(re[i]*re[i] + im[i]*im[i]));

    	return mag;
    }

}