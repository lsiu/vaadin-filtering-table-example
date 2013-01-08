package com.github.lsiu;

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
public class MyVaadinApplication extends Application {
	private Window window;

	@Override
	public void init() {
		window = new Window("My Vaadin Application");
		setMainWindow(window);
		Button button = new Button("Click Me");
		button.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				window.addComponent(new Label("Thank you for clicking"));
			}
		});
		window.addComponent(button);

	}

}
