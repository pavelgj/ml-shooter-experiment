package com.experiment.mlshooter.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math.stat.StatUtils;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;

public class MLShooterEntryPoint implements EntryPoint {

	private static final int DATA_POINTS = 2;
	private static final int FEATURES_N = 21;
	private static final int POLY_POW = 5;
	
	private float barrelRotationDgrees = 0;
	private int mouseX;
	private int mouseY;
	private ShooterEngine engine;
	private long lastStepTime = -1;
	private List<Double[]> rawData = new ArrayList<Double[]>();
	private Layout layout;
	private boolean autoPilotOn;
	private boolean randomShooterOn;
	private boolean goingUp;
	private double[] latestTheta = new double[FEATURES_N];
	private float accuracy = 0;
	private float accuracy0 = 0;
	private float accuracy1 = 0;
	private List<Long> destroyedTargets = new ArrayList<Long>();
	private double lambda = 0;
	private static double means[] = new double[DATA_POINTS];
	private static double sigmas[] = new double[DATA_POINTS];
	
	@Override
	public void onModuleLoad() {
		layout = new Layout();
		final Canvas canvas = layout.getCanvas();
		
		if (canvas == null) {
			RootLayoutPanel.get().add(new HTML("<div class='sorryMessage'>Sorry, but it seems your browser does not support Canvas. Please try this page in Chrome or Firefox.</div>"));
			return;
		}
		
		RootLayoutPanel.get().add(layout);
		
		init(canvas);
	}

	private void init(final Canvas canvas) {
		engine = new ShooterEngine();
		
		layout.getAutoPilotButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				autoPilotOn = !autoPilotOn;
				layout.getAutoPilotButton().setText(autoPilotOn ? "Auto-Pilot On" : "Auto-Pilot Off");
			}
		});
		
		layout.getRandomShooterButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				randomShooterOn = !randomShooterOn;
				layout.getRandomShooterButton().setText(randomShooterOn ? "Random Shooter On" : "Random Shooter Off");
			}
		});
		
		layout.getImportThetaButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// ask for thetas
				StringBuffer init = new StringBuffer();
				for (int i = 0; i < FEATURES_N; i++) {
					if (init.length() > 0) {
						init.append(",");
					}
					init.append(latestTheta[i]);
				}
				String promptVal = Window.prompt("Comma separated list of theta values:", init.toString());
				String[] tkns = promptVal.split(",");
				if (tkns.length != FEATURES_N) {
					Window.alert("Expecting " + FEATURES_N + " theta values but got " + tkns.length + "!");
					return;
				}
				double[] tmpTheta = new double[FEATURES_N];
				for (int i = 0; i < tkns.length; i++) {
					try {
						tmpTheta[i] = Double.parseDouble(tkns[i].trim());
					} catch (Exception e) {
						Window.alert("Unable to path value #" + i + ": " + tkns[i]);
						return;
					}
				}
				latestTheta = tmpTheta;
				
				// ask for means
				init = new StringBuffer();
				for (int i = 0; i < DATA_POINTS; i++) {
					if (init.length() > 0) {
						init.append(",");
					}
					init.append(means[i]);
				}
				promptVal = Window.prompt("Comma separated list of normalization means:", init.toString());
				tkns = promptVal.split(",");
				if (tkns.length != DATA_POINTS) {
					Window.alert("Expecting " + DATA_POINTS + " mean values but got " + tkns.length + "!");
					return;
				}
				double[] tmpMeans = new double[DATA_POINTS];
				for (int i = 0; i < tkns.length; i++) {
					try {
						tmpMeans[i] = Double.parseDouble(tkns[i].trim());
					} catch (Exception e) {
						Window.alert("Unable to path value #" + i + ": " + tkns[i]);
						return;
					}
				}
				means = tmpMeans;
				
				// ask for variance
				init = new StringBuffer();
				for (int i = 0; i < DATA_POINTS; i++) {
					if (init.length() > 0) {
						init.append(",");
					}
					init.append(sigmas[i]);
				}
				promptVal = Window.prompt("Comma separated list of normalization sigmas:", init.toString());
				tkns = promptVal.split(",");
				if (tkns.length != DATA_POINTS) {
					Window.alert("Expecting " + DATA_POINTS + " sigma values but got " + tkns.length + "!");
					return;
				}
				double[] tmpSigmas = new double[DATA_POINTS];
				for (int i = 0; i < tkns.length; i++) {
					try {
						tmpSigmas[i] = Double.parseDouble(tkns[i].trim());
					} catch (Exception e) {
						Window.alert("Unable to path value #" + i + ": " + tkns[i]);
						return;
					}
				}
				sigmas = tmpSigmas;
			}
		});
		
		layout.getRunRegressionButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				lambda = Double.parseDouble(Window.prompt("Lambda:", "" + lambda));
				regress(lambda);
			}
		});
		
		layout.getExportDataButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				StringBuilder res = new StringBuilder();
				for (int m = 0; m < rawData.size(); m++) {
					for (int n = 0; n < DATA_POINTS; n++) {
						res.append(" " + rawData.get(m)[n]);
					}
					res.append(" " + rawData.get(m)[DATA_POINTS]);
					res.append("\n");
				}
				
				PopupPanel popup = new PopupPanel(true, true);
				TextArea area = new TextArea();
				DOM.setStyleAttribute(area.getElement(), "color", "black");
				area.setWidth("700px");
				area.setHeight("500px");
				area.setText(res.toString());
				popup.setWidget(area);
				popup.show();
				popup.center();
			}
		});
		
		new Timer() {
			@Override
			public void run() {
				List<Collision> collisions = null;
				if (lastStepTime > 0) {
					long elapsed = System.currentTimeMillis() - lastStepTime;
					elapsed *= 2;
					collisions = engine.step(elapsed);
				}
				draw(canvas, collisions);
				lastStepTime = System.currentTimeMillis();
				
			}
		}.scheduleRepeating(20);
		
		canvas.addMouseMoveHandler(new MouseMoveHandler() {
			
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				if (autoPilotOn) {
					return;
				}
				mouseX = event.getRelativeX(canvas.getElement());
				mouseY = event.getRelativeY(canvas.getElement());
				
				int a = mouseX - 60;
				int b = canvas.getOffsetHeight() - mouseY - 100;
				if (b < 0) {
					b = 0;
				}
				double z = a / Math.sqrt(a * a + b * b);
				
				barrelRotationDgrees = (float) (Math.toDegrees(Math.acos(z)));
			}
		});
		
		canvas.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (!autoPilotOn) {
					engine.addObject(new Bullet(70, canvas.getOffsetHeight() - 80, Math.toRadians(-barrelRotationDgrees), findActiveTargets()));
					System.out.println("Math.toRadians(-barrelRotationDgrees)" + Math.toRadians(-barrelRotationDgrees));
				}
			}
		});
		
		new Timer() {
			@Override
			public void run() {
				if (findActiveTargets().size() == 0)
					engine.addObject(new Target(-20, canvas.getOffsetHeight() - 500, 0 /*Math.toRadians(Math.random() * 30 - 5)*/));
			}
		}.scheduleRepeating(2000);
		
		new Timer() {
			@Override
			public void run() {
				if (randomShooterOn) {
					engine.addObject(new Bullet(70, canvas.getOffsetHeight() - 80, Math.toRadians(-barrelRotationDgrees), findActiveTargets()));
				}
			}
		}.scheduleRepeating(200);
		
		new Timer() {
			@Override
			public void run() {
				if (autoPilotOn && !randomShooterOn && maybeShoot(barrelRotationDgrees)) {
					engine.addObject(new Bullet(70, canvas.getOffsetHeight() - 80, Math.toRadians(-barrelRotationDgrees), findActiveTargets()));
				}
			}
		}.scheduleRepeating(100);
	}
	

	private void regress(double lambda) {
		if (rawData.size() < 10) {
			return;
		}
		
		double[][] X = new double[rawData.size()][DATA_POINTS];
		for (int i = 0; i < rawData.size(); i++) {
			for (int j = 0; j < DATA_POINTS; j++) {
				X[i][j] = (double) rawData.get(i)[j];
			}
		}
		
		double[] y = new double[rawData.size()];
		for (int i = 0; i < y.length; i++) {
			y[i] = rawData.get(i)[DATA_POINTS];
		}
		
		normalize(X, rawData.size(), DATA_POINTS);
		
		double[][] Xpow = new double[rawData.size()][FEATURES_N];
		for (int i = 0; i < rawData.size(); i++) {
			List<Double> poly = Utils.generatePolynomialFeatures(X[i], POLY_POW);
			for (int j = 0; j < poly.size(); j++) {
				Xpow[i][j] = poly.get(j);
			}
		}
		
		try {
			latestTheta = LogisticaRegression.regress(Xpow, y, lambda);
			for (double d : latestTheta) {
				System.out.print(d + ",");
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
			accuracy = 100 - (fault / (float)count) * 100;
			accuracy0 = 100 - (fault0 / (float)count0) * 100;
			accuracy1 = 100 - (fault1 / (float)count1) * 100;
			updateConsole();
		} catch (Exception e) {
			e.printStackTrace();
			Window.alert(e.getMessage());
		}
	}

	public static void normalize(double[][] X, int m, int n) {
		means = new double[n];
		sigmas = new double[n];
		for (int j = 0; j < n; j++) {
			double[] col = new double[m];
			for (int i = 0; i < m; i++) {
				col[i] = X[i][j];
			}
			means[j] = StatUtils.mean(col);
			sigmas[j] = Math.sqrt(StatUtils.variance(col, means[j]));
		}
		for (int i = 0; i < m; i++) {
			for (int j = 1; j < n; j++) {
				X[i][j] = (X[i][j] - means[j]) / sigmas[j];
			}
		}
	}

	private void updateConsole() {
		StringBuilder thetaStr = new StringBuilder();
		for (double d : latestTheta) {
			if (thetaStr.length() > 0) {
				thetaStr.append(", ");
			}
			thetaStr.append(d);
		}
		layout.setConsoleHtml("<h3>Stats</h3>" +
				"<div>m=" + rawData.size() + "</div>" +
				"<div>lambda=" + lambda + "</div>" +
				"<div>accuracy=" + (int)accuracy + "%</div>" +
				"<div>accuracy0=" + (int)accuracy0 + "%</div>" +
				"<div>accuracy1=" + (int)accuracy1 + "%</div><small>" + thetaStr + "</small>");
	}
	
	private boolean maybeShoot(float barrelRotationDgrees) {
		for (Target t : findActiveTargets()) {
			double[] init = new double[DATA_POINTS];
			int i = 0;
			init[i++] = Math.toRadians(-barrelRotationDgrees);
			init[i++] = (double) t.x;
//			init[i++] = (double) t.y;
//			init[i++] = t.rotation;
			
			for (int j = 0; j < init.length; j++) {
				init[j] = (init[j] - means[j]) / sigmas[j];
			}
			
			System.out.print("maybeShoot data: ");
			for (double d : init) {
				System.out.print(d + ", ");
			}
			System.out.println();

			List<Double> poly = Utils.generatePolynomialFeatures(init, POLY_POW);
			double[] res = new double[poly.size()];
			for (int j = 0; j < res.length; j++) {
				res[j] = poly.get(j);
			}

			boolean skip = false;
			for (double d : res) {
				if (Double.isNaN(d)) {
					skip = true;
				}
			}
			if (skip) {
				continue;
			}
			
			double s = 0;
			for (int j = 0; j < latestTheta.length; j++) {
				s += latestTheta[j] * res[j];
			}
			double sigmoid = LogisticaRegression.sigmoid(s);
			System.out.println("s=" + s + " sigmoid=" + sigmoid);
			s = sigmoid;
			if (s >= 0.5) {
				return true;
			}
		}
		return false;
	}

	private void draw(final Canvas canvas, List<Collision> collisions) {
		updateResize(canvas);
		
		if (autoPilotOn) {
			if (goingUp) {
				barrelRotationDgrees += 2 + 2 * Math.random();
				if (barrelRotationDgrees >= 90) {
					goingUp = false;
				}
			} else {
				barrelRotationDgrees -= 2 + 2 * Math.random();
				if (barrelRotationDgrees < 10) {
					goingUp = true;
				}
			}
		}
		
		Context2d ctx = canvas.getContext2d();
		ctx.setFillStyle(CssColor.make("#000000"));
		ctx.fillRect(0, 0, canvas.getOffsetWidth(), canvas.getOffsetHeight());
		
		ctx.drawImage(ImageElement.as(DOM.getElementById("img_moon")), -50, canvas.getOffsetHeight() - 150);
		ctx.drawImage(ImageElement.as(DOM.getElementById("img_cannon_base")), 30, canvas.getOffsetHeight() - 100);
		
		ctx.save();
		ctx.translate( 70, canvas.getOffsetHeight() - 80 );
		ctx.rotate( Math.toRadians(-barrelRotationDgrees) );
		ctx.translate( -30, -20 );
		ctx.drawImage(ImageElement.as(DOM.getElementById("img_cannon_barrel")), 0, 0);
		ctx.restore();
		
		if (collisions != null) {
			for (Collision collision : collisions) {
				if (collision.a.getClass() != collision.b.getClass()) {
					Bullet bullet;
					Target target;
					if (collision.a instanceof Bullet) {
						bullet = (Bullet) collision.a;
						target = (Target) collision.b;
					} else {
						bullet = (Bullet) collision.b;
						target = (Target) collision.a;
					}
					List<Target> currentTargets = bullet.getTargets();
					for (Target t : currentTargets) {
						if (t.x == Double.NaN)
							continue;
						addData(compileData(bullet, t, t.equals(target)));
					}
					collision.a.remove();
					collision.b.remove();
					engine.removeObject(collision.a);
					engine.removeObject(collision.b);
					destroyedTargets.add(target.getId());
				}
			}
		}
		
		for (MovingObject obj : engine.getObjects()) {
			if (obj.isRemoved()) {
				continue;
			}
			
			if (obj.y > canvas.getOffsetHeight() + 100 || obj.y < -30 || obj.x < -30 || obj.x > canvas.getOffsetWidth() + 20) {
				if (obj instanceof Bullet) {
					Bullet bullet = (Bullet) obj;
					List<Target> currentTargets = bullet.getTargets();
					for (Target target : currentTargets) {
						if (destroyedTargets.contains(target.getId())) {
							continue;
						}
						addData(compileData(bullet, target, false));
					}
				}
				engine.removeObject(obj);
				continue;
			}
			
			if (obj instanceof Bullet) {
				ctx.save();
				ctx.translate(obj.x, obj.y);
				ctx.rotate(obj.rotation);
				ctx.translate( -10, -10 );
				ctx.drawImage(ImageElement.as(DOM.getElementById("img_bullet")), 0, 0);
				ctx.restore();
			} else if (obj instanceof Target) {
				ctx.save();
				ctx.translate(obj.x, obj.y);
				ctx.rotate(obj.rotation);
				ctx.translate( -16, -40 );
				ctx.drawImage(ImageElement.as(DOM.getElementById("img_rocket")), 0, 0);
				ctx.restore();
			}
		}
	}
	
	private void addData(Double[] d) {
		for (Double z : d) {
			if (Double.isNaN(z)) 
				return;
		}
		if (d[0] < -1.57 || d[0] > -0.001) {
			return;
		}
		if (d[0] > 1500) {
			return;
		}
		rawData.add(d);
		updateConsole();
	}

	private List<Target> findActiveTargets() {
		List<Target> res = new ArrayList<Target>();
		for (MovingObject obj : engine.getObjects()) {
			if (obj instanceof Target && !obj.isRemoved()) {
				res.add((Target) obj);
			}
		}
		return res;
	}

	private Double[] compileData(Bullet b, Target t, boolean outcome) {
		Double[] init = new Double[DATA_POINTS + 1];
		int i = 0;
		double bulletRotation;
		if (b.rotation > 0) {
			bulletRotation = Math.toRadians(Math.toDegrees(b.rotation) - 360);
		} else {
			bulletRotation = b.rotation;
		}
		
		init[i++] = bulletRotation;
		init[i++] = (double) t.x;
//		init[i++] = (double) t.y;
//		init[i++] = t.rotation;
		init[DATA_POINTS] = outcome ? 1d : 0d;
		
		return init;
	}

	private void updateResize(final Canvas canvas) {
		canvas.setCoordinateSpaceHeight(canvas.getOffsetHeight());
		canvas.setCoordinateSpaceWidth(canvas.getOffsetWidth());
	}
}