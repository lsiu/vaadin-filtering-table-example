package com.github.lsiu.init;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This programs import "LP_Restaurants_EN.XML" download from
 * http://www.gov.hk/en/theme/psi/datasets/restaurantlicences.htm and import it
 * into a derby database for demo purposes
 * 
 * @author lsiu
 * 
 */
public class InitDb {
	
	public static final String DB_DRIVER = "org.h2.Driver";
	public static final String DB_URL = "jdbc:h2:file:C:/workspaces/vaadin/vaadin-filtering-table-example/target/db";

	public static void main(String[] args) throws Exception {
		new InitDb().run();
	}

	public void run() throws Exception {
		Connection conn = getDbConnection();
		initDbAsRequired(conn);
		Element root = getXmlRootElement();
		try {
			NodeList nl = root.getElementsByTagName("LP");

			String sql = "INSERT INTO restaurants "
					+ "(type_code, district_code, license_no, name, address, info_code) "
					+ "VALUES (?,?,?,?,?,?)";
			PreparedStatement insertStmt = conn.prepareStatement(sql);

			for (int i = 0; i < nl.getLength(); i++) {
				Node n = nl.item(i);
				if (n.getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) n;
					String type = getTagValue("TYPE", e);
					String dist = getTagValue("DIST", e);
					String licno = getTagValue("LICNO", e);
					String name = getTagValue("SS", e);
					String adr = getTagValue("ADR", e);
					String info = getTagValue("INFO", e);
					System.out.println("Restaurant Name: " + name);

					insertStmt.setString(1, type);
					insertStmt.setString(2, dist);
					insertStmt.setString(3, licno);
					insertStmt.setString(4, name);
					insertStmt.setString(5, adr);
					insertStmt.setString(6, info);
					insertStmt.execute();
				}
			}
			conn.commit();
		} catch (Exception e) {
			conn.rollback();
			throw e;
		} finally {
			conn.close();
		}
	}

	private void initDbAsRequired(Connection conn) throws Exception {
		Statement stmt = conn.createStatement();
		try {
			stmt.execute("SELECT * FROM restaurants"); // this throws exception
														// if table don't exist
		} catch (org.h2.jdbc.JdbcSQLException e) {
			// 42S02 = table/view does not exist
			if ("42S02".equals(e.getSQLState())) { 
				stmt.execute("CREATE TABLE restaurants"
						+ "("
						+ "id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
						+ "type_code char(2)," + "district_code numeric(2),"
						+ "license_no numeric(10)," + "name varchar(32672),"
						+ "address varchar(32672)," + "info_code char(16),"
						+ "CONSTRAINT primary_key PRIMARY KEY (id)" + ")");
			}
		}
		stmt.close();
	}

	private Connection getDbConnection() throws Exception {
		Class.forName(DB_DRIVER);
		return DriverManager.getConnection(DB_URL);
	}

	private Element getXmlRootElement() throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new File("LP_Restaurants_EN.XML"));

		doc.getDocumentElement().normalize();
		return doc.getDocumentElement();
	}

	private String getTagValue(String sTag, Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0)
				.getChildNodes();

		Node nValue = (Node) nlList.item(0);
		if (nValue == null)
			return null;
		return nValue.getNodeValue();
	}

}
