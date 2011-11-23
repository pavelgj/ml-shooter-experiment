import java.util.List;

import com.experiment.mlshooter.client.LogisticaRegression;
import com.experiment.mlshooter.client.Utils;

public class Test {

	public static void main(String[] args) {
		List<Double> raiseToPower = Utils.generatePolynomialFeatures(new double[]{2, 3, 4, 5, 6}, 3);
		System.out.println(raiseToPower.size());
		System.out.println(raiseToPower.toString());
	}
	
	public static void main3(String[] args) {
		double[][] X = new double[][]{
				{-2, -2},
				{-1, -1},
				{0, 0},
				{1, 1},
				{2, 2},
				{3, 3},
				{4, 4},
		};

		Utils.normalize(X, X.length,  2 );
		
		for (int i = 0; i < X.length; i++) {
			for (int j = 0; j < X[i].length; j++) {
				System.out.print(X[i][j] + ", ");
			}
			System.out.println();
		}
	}
	
	public static void main2(String[] args) throws Exception {
		double[][] X = new double[][]{
			{ 0.08876232511005902, 129.99996948242188},
			{ 0.09727473885381215, 346.0002746582031},
			{ 0.10038733912728794, 431.0003967285156},
			{ 0.10138734340926361, 682.9995727539062},
			{ 0.10251624324836492, 790.9994506835938},
			{ 0.1038872781202982, 876.0046997070312},
			{ 0.10194050807372922, 189.99989318847656},
			{ 0.08921878668442916, 437.0003967285156},
			{ 0.09685540544721306, 659.9996948242188},
			{ 0.08894293581606894, 881.9989013671875},
			{ 0.09428850564596593, 1188.9984130859375},
			{ 0.10019634017679611, 1062.9986572265625},
			{ 0.1087783033166838, 1321.9976806640625},
			{ 0.10778691794506072, 173.00001525878906},
			{ 0.10564651118105126, 276.0001220703125},
			{ 0.09222898464588089, 499.00030517578125},
			{ 0.10333861659821947, 372.00018310546875},
			{ 0.08512111877326782, 585.0000610351562},
			{ 0.09226138569065427, 837.9987182617188},
			{ 0.10117941236168415, 734.9991455078125},
			{ 0.09333001141714135, 1122.9986572265625},
			{ 0.10370326307294421, 1014.9988403320312},
			{ 0.08605825588531897, 1195.99853515625},
			{ 0.09164585994216604, 165.99990844726562},
			{ 0.10924735661526161, 1432.00048828125},
			{ 0.10762104035821637, 111.99998474121094},
			{ 0.0839389648458982, 472.0004577636719},
			{ 0.10921665097298398, 797.999755859375},
			{ 0.10621706858208377, 899.999267578125},
			{ 0.08800835849453588, 1092.9981689453125},
			{ 0.09081912529645292, 1254.998291015625},
			{ 0.08235444011101165, 1314.998291015625},
			{ 0.1001738714596653, 1182.9981689453125}
		};
		
		double[] y = new double[] {
			   1,
			   1,
			   0,
			   1,
			   1,
			   1,
			   0,
			   0,
			   0,
			   0,
			   0,
			   0,
			   0,
			   0,
			   0,
			   0,
			   0,
			   0,
			   0,
			   0,
			   0,
			   0,
			   0,
			   1,
			   0,
			   0,
			   0,
			   0,
			   0,
			   0,
			   0,
			   0,
			   0
		};
		
		Utils.normalize(X, X.length, 2);
		
		int n = 10;
		
		double[][] Xpow = new double[X.length][n];
		for (int i = 0; i < X.length; i++) {
			List<Double> raiseToPower = Utils.generatePolynomialFeatures(X[i], 3);
			for (int j = 0; j < raiseToPower.size(); j++) {
				Xpow[i][j] = raiseToPower.get(j);
			}
		}
		
		double[] latestTheta = LogisticaRegression.regress(Xpow, y, 0);
		for (double d : latestTheta) {
			System.out.print(d + ";");
		}
		System.out.println();
		
		int count = 0;
		int fault = 0;
		int fault0 = 0;
		int count0 = 0;
		int fault1 = 0;
		int count1 = 0;
		for (int i = 0; i < X.length; i++) {
			double s = 0;
			for (int j = 0; j < X[i].length; j++) {
				s += latestTheta[j] * X[i][j];
			}
			s = LogisticaRegression.sigmoid(s);
			double prediction = s >= 0.5 ? 1 : 0;
			System.out.println(i + " = " + s + " y=" + y[i] + " prediction=" + prediction);
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
		
		System.out.println("accuracy=" + accuracy + "\naccuracy0=" + accuracy0 + "\naccuracy1=" + accuracy1);
	}
}
