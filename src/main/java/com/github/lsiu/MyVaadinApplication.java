package com.github.lsiu;

import java.sql.SQLException;

import org.tepi.filtertable.FilterTable;

import com.github.lsiu.init.InitDb;
import com.vaadin.Application;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.QueryDelegate;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
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

	@Override
	public void init() {
		window = new Window("My Vaadin Application");
		setMainWindow(window);
		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();

		FilterTable table = new FilterTable();
		initTable(table);

		layout.addComponent(table);
		window.setContent(layout);
	}

	private void initTable(FilterTable table) {
		table.setSizeFull();
		QueryDelegate delegate = new TableQuery("restaurants", connectionPool);
		SQLContainer container;
		try {
			container = new SQLContainer(delegate);
			table.setContainerDataSource(container);
		} catch (SQLException e) {
			throw new RuntimeException(
					"Error initializing container with query delegate", e);
		}
		
		table.setFilterBarVisible(true);
	}

}
