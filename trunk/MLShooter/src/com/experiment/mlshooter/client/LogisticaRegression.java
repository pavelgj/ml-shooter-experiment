package com.experiment.mlshooter.client;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.DifferentiableMultivariateRealFunction;
import org.apache.commons.math.analysis.MultivariateRealFunction;
import org.apache.commons.math.analysis.MultivariateVectorialFunction;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.MatrixIndexException;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.RealConvergenceChecker;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.apache.commons.math.optimization.direct.PowellOptimizer;

public class LogisticaRegression {

	public static double[] regress(double[][] Xdata, double[] ydata, double lambda) throws Exception {
		
		final RealMatrix X = new Array2DRowRealMatrix(Xdata);
		RealMatrix y = new Array2DRowRealMatrix(ydata);
		
		double[] initialTheta = Utils.zeros(X.getColumnDimension());
		
		final CostFunction J = new CostFunction(X, y, lambda);
		final DerivativeCostFunction Jgrad = new DerivativeCostFunction(X, y, lambda);
		
		PowellOptimizer optimizer = new PowellOptimizer();
		optimizer.setMaxIterations(200);
		optimizer.setConvergenceChecker(new RealConvergenceChecker() {
			
			@Override
			public boolean converged(int iteration, RealPointValuePair previous, RealPointValuePair current) {
				System.out.println("Iter " + iteration + " J=" + current.getValue());
				if (Double.isNaN(current.getValue())) {
					return true;
				}
				return iteration >= 200;
			}
		});
		
		DifferentiableMultivariateRealFunction optF = new DifferentiableMultivariateRealFunction() {
			@Override
			public double value(double[] point) throws FunctionEvaluationException, IllegalArgumentException {
				double value = J.value(point);
				return value;
			}
			
			@Override
			public MultivariateRealFunction partialDerivative(int k) {
				throw new RuntimeException("not implemented");
			}
			
			@Override
			public MultivariateVectorialFunction gradient() {
				return Jgrad;
			}
		};
		
		RealPointValuePair pair = optimizer.optimize(optF, GoalType.MINIMIZE, initialTheta);
		return pair.getPoint();
	}
	
	private static UnivariateRealFunction oneMinus = new UnivariateRealFunction() {
		
		@Override
		public double value(double x) throws FunctionEvaluationException {
			return 1 - x;
		}
	};
	
	private static UnivariateRealFunction onePlus = new UnivariateRealFunction() {
		
		@Override
		public double value(double x) throws FunctionEvaluationException {
			return 1 + x;
		}
	};
	
	private static UnivariateRealFunction exp = new UnivariateRealFunction() {
		
		@Override
		public double value(double x) throws FunctionEvaluationException {
			return Math.exp(x);
		}
	};

	private static class CostFunction implements MultivariateRealFunction {
		
		private final RealMatrix X;
		private final RealMatrix y;
		private final double lambda;

		public CostFunction(RealMatrix X, RealMatrix y, double lambda) {
			this.X = X;
			this.y = y;
			this.lambda = lambda;
		}

		@Override
		public double value(double[] thetaArray) throws FunctionEvaluationException, IllegalArgumentException {
			/*
			 h = sigmoid(X * theta);
			 s = sum(y .* log(h) + (1 - y) .* log(1 - h));
			 J = -1 / m  * s + (lambda / (2 * m)) * sum(theta(2:n).^2);
			 grad = 1 / m .* ((h - y)' * X)';
			*/
			int m = X.getRowDimension();
			RealMatrix theta = new Array2DRowRealMatrix(thetaArray.length, 1);
			theta.setColumn(0, thetaArray);
			
			RealMatrix h = sigmoid(X.multiply(theta));
			double J = -1 * Utils.sum(Utils.byElement(y, Utils.map(h, Utils.LOG), Utils.MULT).add(Utils.byElement(Utils.map(y, oneMinus), Utils.map(Utils.map(h, oneMinus), Utils.LOG), Utils.MULT))) / m;
			
			return J + Utils.sum(Utils.map(thetaArray, Utils.SQUARE), 1, thetaArray.length) * lambda / (2 * m);
		}
	}
	
	private static class DerivativeCostFunction implements MultivariateVectorialFunction {
		
		private final RealMatrix X;
		private final RealMatrix y;
		private final double lambda;

		public DerivativeCostFunction(RealMatrix X, RealMatrix y, double lambda) {
			this.X = X;
			this.y = y;
			this.lambda = lambda;
		}
		
		@Override
		public double[] value(double[] thetaArray) throws FunctionEvaluationException, IllegalArgumentException {
			/*
			 h = sigmoid(X * theta);
			 grad = 1 / m .* (((h - y)' * X)' + lambda .* [0;theta(2:n)]);;
			*/
			final int m = X.getRowDimension();
			RealMatrix theta = new Array2DRowRealMatrix(thetaArray.length, 1);
			theta.setColumn(0, thetaArray);
			
			RealMatrix h = sigmoid(X.multiply(theta));
			
			RealMatrix grad = Utils.map(h.subtract(y).transpose().multiply(X).transpose(), new UnivariateRealFunction() {
				@Override
				public double value(double x) throws FunctionEvaluationException {
					return x / m;
				}
			});
			
			RealMatrix regTheta = new Array2DRowRealMatrix(thetaArray);
			regTheta.setEntry(0, 0, 0);
			regTheta = regTheta.scalarMultiply(lambda);
			regTheta = regTheta.scalarMultiply(1d/m);
			return grad.add(regTheta).getColumn(0);
		}
	}
	
	private static RealMatrix sigmoid(RealMatrix matrix) throws MatrixIndexException, FunctionEvaluationException {
		return Utils.map(Utils.map(Utils.map(Utils.map(matrix, Utils.MINUS), exp), onePlus), Utils.ONE_OVER);
	}
	
	public static double sigmoid(double x) {
		return 1 / (1 + Math.pow(Math.E, -x));
	}
}
