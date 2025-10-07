/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.db.upgrade.client;

/**
 * @author David Truong
 */
public class Database {

	public static Database getDatabase(String databaseType) {
		if (databaseType.equals("db2")) {
			return new Database(
				"com.ibm.db2.jcc.DB2Driver", "jdbc:db2://", "localhost", 50000,
				":deferPrepares=false;fullyMaterializeInputStreams=true;" +
					"fullyMaterializeLobData=true;progressiveLocators=2;" +
						"progressiveStreaming=2;",
				"lportal");
		}

		if (databaseType.equals("mariadb")) {
			return new Database(
				"org.mariadb.jdbc.Driver", "jdbc:mariadb://", "localhost", 0,
				"?useUnicode=true&characterEncoding=UTF-8" +
					"&useFastDateParsing=false",
				"lportal");
		}

		if (databaseType.equals("mysql")) {
			return new Database(
				"com.mysql.cj.jdbc.Driver", "jdbc:mysql://", "localhost", 0,
				"?characterEncoding=UTF-8&dontTrackOpenResources=true" +
					"&holdResultsOpenOverStatementClose=true&serverTimezone=" +
						"GMT&useFastDateParsing=false&useUnicode=true",
				"lportal");
		}

		if (databaseType.equals("oracle")) {
			return new Database(
				"oracle.jdbc.OracleDriver", "jdbc:oracle:thin:@", "localhost",
				1521, "", "xe");
		}

		if (databaseType.equals("postgresql")) {
			return new Database(
				"org.postgresql.Driver", "jdbc:postgresql://", "localhost",
				5432, "", "lportal");
		}

		if (databaseType.equals("sqlserver")) {
			return new Database(
				"com.microsoft.sqlserver.jdbc.SQLServerDriver",
				"jdbc:sqlserver://", "localhost", 0, "", "lportal");
		}

		return null;
	}

	public String getClassName() {
		return _className;
	}

	public String getHost() {
		return _host;
	}

	public int getPort() {
		return _port;
	}

	public String getProtocol() {
		return _protocol;
	}

	public String getSchemaName() {
		return _schemaName;
	}

	public String getURL() {
		StringBuilder sb = new StringBuilder();

		sb.append(_protocol);
		sb.append(_host);

		if (_port > 0) {
			sb.append(':');
			sb.append(_port);
		}

		if (_protocol.contains("sqlserver")) {
			sb.append(";databaseName=");
		}
		else if (_protocol.contains("oracle")) {
			sb.append(":");
		}
		else {
			sb.append("/");
		}

		sb.append(_schemaName);
		sb.append(_params);

		return sb.toString();
	}

	public void setClassName(String className) {
		_className = className;
	}

	public void setHost(String host) {
		_host = host;
	}

	public void setParams(String params) {
		_params = params;
	}

	public void setPort(int port) {
		_port = port;
	}

	public void setProtocol(String protocol) {
		_protocol = protocol;
	}

	public void setSchemaName(String schemaName) {
		_schemaName = schemaName;
	}

	private Database(
		String className, String protocol, String host, int port, String params,
		String schemaName) {

		_className = className;
		_protocol = protocol;
		_host = host;
		_port = port;
		_params = params;
		_schemaName = schemaName;
	}

	private String _className;
	private String _host;
	private String _params;
	private int _port;
	private String _protocol;
	private String _schemaName;

}