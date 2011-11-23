

import java.applet.Applet;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import com.experiment.mlshooter.client.LogisticaRegression;
import com.experiment.mlshooter.client.Utils;
import com.experiment.mlshooter.client.Utils.NormalizationStats;

@SuppressWarnings("serial")
public class RegressionApplet extends Applet {

	public String regress(String data) {
		List<Double[]> allData = parseData(data);
		int m = allData.size();
		
		double[] y = new double[m];
		double[][] X = new double[m][5];
		for (int i = 0; i < allData.size(); i++) {
			Double[] dline = allData.get(i);
			for (int j = 0; j < 5; j++) {
				X[i][j] = dline[j];
			}
			y[i] = dline[5];
		}
		
		NormalizationStats normalizeStats = Utils.normalize(X, m, 5);
		double[] means = normalizeStats.getMeans();
		double[] sigmas = normalizeStats.getVariance();
		
		double[][] Xpow = new double[m][56];
		for (int i = 0; i < m; i++) {
			List<Double> poly = Utils.generatePolynomialFeatures(X[i], 3);
			for (int j = 0; j < poly.size(); j++) {
				Xpow[i][j] = poly.get(j);
			}
		}
		
		try {
			double[] latestTheta = LogisticaRegression.regress(Xpow, y, 0);
			
			int count = 0;
			int fault = 0;
			int fault0 = 0;
			int count0 = 0;
			int fault1 = 0;
			int count1 = 0;
			for (int i = 0; i < X.length; i++) {
				double s = 0;
				for (int j = 0; j < Xpow[i].length; j++) {
					s += latestTheta[j] * Xpow[i][j];
				}
				s = LogisticaRegression.sigmoid(s);
				double prediction = s >= 0.5 ? 1 : 0;
				count++;
				if (prediction != y[i]) {
					fault++;
				}
				if (y[i] == 0) {
					count0++;
					if (prediction != 0) {
						fault0++;
					}
				}
				if (y[i] == 1) {
					count1++;
					if (prediction != 1) {
						fault1++;
					}
				}
			}
			float accuracy = 100 - (fault / (float)count) * 100;
			float accuracy0 = 100 - (fault0 / (float)count0) * 100;
			float accuracy1 = 100 - (fault1 / (float)count1) * 100;
			StringBuffer res = new StringBuffer("{");
			res.append("theta='");
			for (int i = 0; i < latestTheta.length; i++) {
				res.append(latestTheta[i]);
				if (i + 1 < latestTheta.length) {
					res.append(",");
				}
			}
			res.append("',");
			res.append("means='");
			for (int i = 0; i < means.length; i++) {
				res.append(means[i]);
				if (i + 1 < means.length) {
					res.append(",");
				}
			}
			res.append("',");
			res.append("variance='");
			for (int i = 0; i < sigmas.length; i++) {
				res.append(sigmas[i]);
				if (i + 1 < sigmas.length) {
					res.append(",");
				}
			}
			res.append("',");
			res.append("accuracy='" + accuracy + "',");
			res.append("accuracy0='" + accuracy0 + "',");
			res.append("accuracy1='" + accuracy1 + "',");
			res.append("}");
			
			return res.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "" + e.getMessage();
		}

	}
	private static List<Double[]> parseData(String data) {
		List<Double[]> res = new ArrayList<Double[]>();
		try {
			BufferedReader br = new BufferedReader(new StringReader(data));
			String line;
			while ((line = br.readLine()) != null) {
				String[] split = line.trim().split(" ");
				Double[] arr = new Double[split.length];
				for (int i = 0; i < arr.length; i++) {
					arr[i] = Double.parseDouble(split[i]);
				}
				res.add(arr);
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		return res;
	}

}
