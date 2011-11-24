package com.experiment.mlshooter.client;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.BivariateRealFunction;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.MatrixIndexException;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.stat.StatUtils;


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
	
	public static List<Double> generatePolynomialFeatures(double[] init, int pow) {
		List<Double> poly = new ArrayList<Double>();
		poly.add(1d);
		for (int i = 1; i <= pow; i++) {
			poly.addAll(raiseToPower2(init, i));
		}
		return poly;
	}
	
	private static List<Double> raiseToPower2(double[] init, int pow) {
		List<Double> poly = new ArrayList<Double>();
		List<List<Integer>> combos = new ArrayList<List<Integer>>();
		for (int i = 0; i < init.length; i++) {
			poly.add(init[i]);
			combos.add(asList(i));
		}
		for (int p = 2; p <= pow; p++) {
			List<Double> origPoly = new ArrayList<Double>(poly);
			poly = new ArrayList<Double>();
			for (int i = 0; i < origPoly.size(); i++) {
				for (int j = 0; j < init.length; j++) {
					List<Integer> newCombo = new ArrayList<Integer>(combos.get(i));
					newCombo.add(j);
					Collections.sort(newCombo);
					if (!combos.contains(newCombo)) {
						poly.add(init[j] * origPoly.get(i));
						combos.add(newCombo);
					}
				}
			}
			List<List<Integer>> oldCombos = new ArrayList<List<Integer>>(combos);
			combos = new ArrayList<List<Integer>>();
			for (int i = 0; i < oldCombos.size(); i++) {
				if (oldCombos.get(i).size() == p) {
					combos.add(oldCombos.get(i));
				}
			}
		}
		return poly;
	}

	private static List<Integer> asList(int... is) {
		List<Integer> res = new ArrayList<Integer>();
		for (int i : is) {
			res.add(i);
		}
		Collections.sort(res);
		return res;
	}

	public static NormalizationStats normalize(double[][] X, int m, int n) {
		double[] means = new double[n];
		double[] sigmas = new double[n];
		for (int j = 0; j < n; j++) {
			double[] col = new double[m];
			for (int i = 0; i < m; i++) {
				col[i] = X[i][j];
			}
			means[j] = StatUtils.mean(col);
			sigmas[j] = Math.sqrt(StatUtils.variance(col, means[j]));
		}
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				X[i][j] = (X[i][j] - means[j]) / sigmas[j];
			}
		}
		
		return new NormalizationStats(means, sigmas);
	}
	
	public static class NormalizationStats {
		
		private double[] means;
		private double[] variance;
		
		private NormalizationStats(double[] means, double[] variance) {
			this.means = means;
			this.variance = variance;
		}
		
		public double[] getMeans() {
			return means;
		}

		public double[] getVariance() {
			return variance;
		}
	}

	public static String join(double[] arr, String by) {
		StringBuffer res = new StringBuffer();
		for (int i = 0; i < arr.length; i++) {
			res.append(arr[i]);
			if (i + 1 < arr.length) {
				res.append(by);
			}
		}
		return res.toString();
	}
	
	public static double[] split(String in, String by) {
		String[] split = in.split(by);
		double[] res = new double[split.length];
		for (int i = 0; i < split.length; i++) {
			res[i] = Double.parseDouble(split[i]);
		}
		return res;
	}
}
