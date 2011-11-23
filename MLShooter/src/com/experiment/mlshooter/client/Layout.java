package com.experiment.mlshooter.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class Layout extends Composite {

	private static LayoutUiBinder uiBinder = GWT.create(LayoutUiBinder.class);

	interface LayoutUiBinder extends UiBinder<Widget, Layout> {
	}

	@UiField(provided=true)
	Canvas canvas;
	
	@UiField
	Button autoPilotButton;
	
	@UiField
	Button randomShooterButton;
	
	@UiField
	Button runRegressionButton;
	
	@UiField
	Button exportDataButton;
	
	@UiField
	HTML console;
	
	public Layout() {
		canvas = Canvas.createIfSupported();
		if (canvas == null) {
			return;
		}
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public Canvas getCanvas() {
		return canvas;
	}

	public Button getAutoPilotButton() {
		return autoPilotButton;
	}
	
	public Button getRandomShooterButton() {
		return randomShooterButton;
	}

	public Button getRunRegressionButton() {
		return runRegressionButton;
	}
	
	public Button getExportDataButton() {
		return exportDataButton;
	}
	
	public void setConsoleHtml(String html) {
		console.setHTML(html);
	}
}
