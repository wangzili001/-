package cn.rocket.ml.linearRegression;

import java.awt.Color;
import java.io.IOException;

import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;
import org.ujmp.core.DenseMatrix;
import org.ujmp.core.Matrix;
import org.ujmp.core.calculation.Calculation.Ret;

import cn.rocket.data.DataSet;

public class MultiGradientDescientMethod {
	private Matrix X ;     //训练数据集
	private Matrix y ;     //标签集合
	private Matrix theta ; //待训练的参数
	private double alpha = 0.01 ;  //学习速率
	private int max_itea = 7000 ; //最大迭代步数
	

	private DataSet dataSet = new DataSet() ;
	
	public MultiGradientDescientMethod(String dataPath) throws IOException {
		dataSet.loadDataFromTxt(dataPath, ",",1);           //加载数据集 2表示标签在第2列从0开始
		X = DenseMatrix.Factory.zeros(dataSet.getSize(),dataSet.getDatas().get(0).size()+1); //将数据集转换成矩阵
		y = DenseMatrix.Factory.zeros(dataSet.getSize(),1);                                  //标签集
		theta = DenseMatrix.Factory.zeros(dataSet.getDatas().get(0).size()+1,1);             //参数集合 theta是一个列向量
		
		for(int i = 0 ; i < dataSet.getSize() ;i++) {
			X.setAsDouble(1, i,0);
			for(int j = 0 ; j < dataSet.getDatas().get(i).size() ;j++) 
				X.setAsDouble(dataSet.getDatas().get(i).get(j), i,j+1);  //除100是为了特种缩放，使得特征值都落在[0,1]之间
			y.setAsDouble(dataSet.getLabels().get(i), i,0);
		}
	}
	
	
	public double predict(Matrix rowVector){
		return  theta.transpose().mtimes(rowVector.transpose()).getValueSum();
	}
	
	/*
	 * rowVector: 当前样例数据，是一个行向量 
	 * y: 当前样例的标签数据
	 */
	public double calc_error(Matrix rowVector, double y) {
		 return predict(rowVector) - y ;
	}
	
	/*
	 * 函数功能：梯度下降求参数theta(一个列向量)
	 */
	private void gradientDescient() {
		//X.selectRows(Ret.NEW, i)
		Matrix sum = DenseMatrix.Factory.zeros(theta.getRowCount(),1);
		for(int i = 0 ; i < this.X.getRowCount() ;i++)
			for(int j =0 ; j < sum.getRowCount();j++)
				sum.setAsDouble(  sum.getAsDouble(j,0)+
						calc_error( this.X.selectRows(Ret.NEW, i) , this.y.selectRows(Ret.NEW, i).getValueSum() )
							*this.X.selectRows(Ret.NEW, i).getAsDouble(0,j),
					j,0);
		
		for(int i = 0 ; i < this.theta.getRowCount();i++) {
			theta.setAsDouble(theta.getAsDouble(i,0) - this.alpha*sum.getAsDouble(i,0)/this.X.getRowCount() ,i,0);
		}
	}
	
	public void main() {
		int itea = 0 ;
		while( itea< this.max_itea){//迭代求参，直到达到最大迭代步数为止
			System.out.println("The current step is :"+itea);
			System.out.println("theta\n "+this.getTheta());
			System.out.println();
			gradientDescient();
			itea ++ ;
		}
	}
	
	/*
	 * 函数功能：程序入口，测试逻辑回归准确率，使用训练数据作为测试数据
	 */
	public static void main(String[] args) throws IOException {
		MultiGradientDescientMethod gdm = new MultiGradientDescientMethod("datas/linearRegression/singleData.txt") ;
		gdm.main(); //梯度下降法求参数 
	
		//如果是单变量线性回归可以画出回归模型
		gdm.plot();
		
	}
	
	
	public void plot() {
		XYSeriesCollection c = new XYSeriesCollection();
		XYSeries trueSeries = new XYSeries("Train samples");
		XYSeries lineSeries = new XYSeries("Linear model");
		for(int i=0;i<this.dataSet.getSize();i++){
        		trueSeries.add(this.X.getAsDouble(i,1), this.y.getAsDouble(i,0));
	    }
		
		 for(double i=4 ; i < 25.0 ; i+=0.01) {
	        	lineSeries.add(i,this.theta.getAsDouble(0,0)+ this.theta.getAsDouble(1,0) * i);
		}
		
		c.addSeries(trueSeries);
		c.addSeries(lineSeries);
	
       //xydataset.addSeries("Method", falseData);
       final JFreeChart chart =ChartFactory.createScatterPlot("LinearRegression","x","y",c,PlotOrientation.VERTICAL,true,true,false);  
       chart.setBackgroundPaint(ChartColor.WHITE);
       XYPlot xyplot = (XYPlot) chart.getPlot();
       xyplot.setBackgroundPaint(new Color(255, 253, 246));  
       XYDotRenderer xydotrenderer = new XYDotRenderer();
		xydotrenderer.setDotWidth(5);
		xydotrenderer.setDotHeight(5);
		xyplot.setRenderer(xydotrenderer);		

		 
		 
		
       ChartFrame frame = new ChartFrame("Plot",chart);
        frame.pack();
        RefineryUtilities.centerFrameOnScreen(frame);
        frame.setVisible(true);
	}
	public Matrix getX() {
		return X;
	}

	public void setX(Matrix x) {
		X = x;
	}

	public Matrix getY() {
		return y;
	}

	public void setY(Matrix y) {
		this.y = y;
	}

	public Matrix getTheta() {
		return theta;
	}

	public void setTheta(Matrix theta) {
		this.theta = theta;
	}

	public DataSet getDataSet() {
		return dataSet;
	}

	public void setDataSet(DataSet dataSet) {
		this.dataSet = dataSet;
	}
	public int getMax_itea() {
		return max_itea;
	}

	public void setMax_itea(int max_itea) {
		this.max_itea = max_itea;
	}

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}
	
}
