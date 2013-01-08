package com.github.lsiu;

import java.sql.SQLException;

import org.tepi.filtertable.FilterTable;

import com.github.lsiu.init.InitDb;
import com.vaadin.Application;
import com.vaadin.data.Container;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.QueryDelegate;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
public class MyVaadinApplication extends Application {
	
	private static JDBCConnectionPool connectionPool;

	static {
		try {
			connectionPool = new SimpleJDBCConnectionPool(InitDb.DB_DRIVER,
					InitDb.DB_URL, "", "");
		} catch (SQLException e) {
			throw new RuntimeException("Error creating connection pool.", e);
		}
	}
	
	private Window window;
	
	private FilterTable table;

	@Override
	public void init() {
		window = new Window("My Vaadin Application");
		setMainWindow(window);
		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();

		table = new FilterTable();
		initTable(table);
		
		HorizontalLayout topBar = new HorizontalLayout();
		initTopBar(topBar);
		layout.addComponent(topBar);

		layout.addComponent(table);
		layout.setExpandRatio(table, 1);
		window.setContent(layout);
	}
	
	private void initTopBar(HorizontalLayout topBar) {
		topBar.setWidth("100%");
		final CheckBox editableCheckBox = new CheckBox("Editable");
		topBar.addComponent(editableCheckBox);
		topBar.setComponentAlignment(editableCheckBox, Alignment.BOTTOM_RIGHT);
		editableCheckBox.setValue(table.isEditable());
		editableCheckBox.setImmediate(true);
		editableCheckBox.addListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				table.setEditable((Boolean)editableCheckBox.getValue());
			}
		});
	}

	private void initTable(FilterTable table) {
		table.setSizeFull();
		QueryDelegate delegate = new TableQuery("restaurants", connectionPool);
		SQLContainer container;
		try {
			container = new SQLContainer(delegate);
			container.setAutoCommit(true);
			table.setContainerDataSource(container);
		} catch (SQLException e) {
			throw new RuntimeException(
					"Error initializing container with query delegate", e);
		}
		
		table.setFilterBarVisible(true);
		table.setImmediate(true);

		table.setTableFieldFactory(new TableFieldFactory() {
			@Override
			public Field createField(Container container, Object itemId,
					Object propertyId, Component uiContext) {
				Field f = DefaultFieldFactory.get().createField(container,
						itemId, propertyId, uiContext);
				f.setSizeFull();
				if (f instanceof TextField) {
					((TextField)f).setNullRepresentation("");
				} 
				return f;
			}
		});
		
		table.setColumnWidth("ID", 30);
		table.setColumnWidth("TYPE_CODE", 30);
		table.setColumnWidth("DISTRICT_CODE", 30);
		table.setColumnWidth("LICENSE_NO", 100);
		table.setColumnExpandRatio("NAME", 0.2F);
		table.setColumnExpandRatio("ADDRESS", 0.8F);
		table.setColumnWidth("INFO_CODE", 100);
	}

}
