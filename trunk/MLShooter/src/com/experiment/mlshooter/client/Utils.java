package com.experiment.mlshooter.client;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.BivariateRealFunction;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.MatrixIndexException;
import org.apache.commons.math.linear.RealMatrix;


public class Utils {
	
	public static final UnivariateRealFunction SQUARE = new UnivariateRealFunction() {
		@Override
		public double value(double x) throws FunctionEvaluationException {
			return Math.pow(x, 2);
		}
	};
	
	public static final UnivariateRealFunction LOG = new UnivariateRealFunction() {
		@Override
		public double value(double x) throws FunctionEvaluationException {
			return Math.log(x);
		}
	};
	
	public static final UnivariateRealFunction ONE_OVER = new UnivariateRealFunction() {
		@Override
		public double value(double x) throws FunctionEvaluationException {
			return 1 / x;
		}
	};
	
	public static final UnivariateRealFunction MINUS = new UnivariateRealFunction() {
		@Override
		public double value(double x) throws FunctionEvaluationException {
			return -x;
		}
	};
	
	public static final BivariateRealFunction MULT = new BivariateRealFunction() {
		
		@Override
		public double value(double x, double y) throws FunctionEvaluationException {
			return x * y;
		}
	};
	
	public static double sum(RealMatrix matrix) {
		double sum = 0;
		for (int i = 0; i < matrix.getRowDimension(); i++) {
			for (int j = 0; j < matrix.getColumnDimension(); j++) {
				sum  += matrix.getEntry(i, j);
			}
		}
		return sum;
	}
	
	public static RealMatrix map(RealMatrix matrix, UnivariateRealFunction f) throws MatrixIndexException, FunctionEvaluationException {
		RealMatrix copy = matrix.copy();
		for (int i = 0; i < matrix.getRowDimension(); i++) {
			for (int j = 0; j < matrix.getColumnDimension(); j++) {
				copy.setEntry(i, j, f.value(matrix.getEntry(i, j)));
			}
		}
		return copy;
	}

	public static String toString(double[] ds) {
		StringBuilder b = new StringBuilder();
		for (double d : ds) {
			if (b.length() > 0) {
				b.append(", ");
			}
			b.append(d);
		}
		return "double[]{" + b.toString() + "}";
	}

	public static RealMatrix byElement(RealMatrix a, RealMatrix b, BivariateRealFunction f) throws MatrixIndexException, FunctionEvaluationException {
		RealMatrix res = new Array2DRowRealMatrix(a.getRowDimension(), a.getColumnDimension());
		for (int i = 0; i < a.getRowDimension(); i++) {
			for (int j = 0; j < a.getColumnDimension(); j++) {
				res.setEntry(i, j, f.value(a.getEntry(i, j), b.getEntry(i, j)));
			}
		}
		return res;
	}

	public static double[] zeros(int columnDimension) {
		return new double[columnDimension];
	}

	public static double sum(double[] value) {
		double sum = 0;
		for (double d : value) {
			sum += d;
		}
		return sum;
	}

	public static double[] map(double[] in, UnivariateRealFunction f) throws FunctionEvaluationException {
		double[] res = new double[in.length];
		for (int i = 0; i < res.length; i++) {
			res[i] = f.value(in[i]);
		}
		return res;
	}

	public static double sum(double[] data, int start, int end) {
		double sum = 0;
		for (int i = start; i < end; i++) {
			sum += data[i];
		}
		return sum;
	}
	
	public static double[] clone(double[] d) {
		double[] res = new double[d.length];
		for (int i = 0; i < res.length; i++) {
			res[i] = d[i];
		}
		return res;
	}

	public static Object[] clone(Object[] os) {
		Object[] res = new Object[os.length];
		for (int i = 0; i < res.length; i++) {
			res[i] = os[i];
		}
		return res;
	}
	
}
