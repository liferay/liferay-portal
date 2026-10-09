/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.db.support.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.FileConverter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import java.nio.charset.StandardCharsets;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Andrea Di Giorgi
 */
@Parameters(
	commandDescription = "Cleans the Liferay database from the Service Builder tables and rows of a module.",
	commandNames = "clean-service-builder"
)
public class CleanServiceBuilderCommand extends BaseCommand {

	public void setServiceXmlFile(File serviceXmlFile) {
		_serviceXmlFile = serviceXmlFile;
	}

	public void setServletContextName(String servletContextName) {
		_servletContextName = servletContextName;
	}

	@Override
	protected void execute(Connection connection) throws Exception {
		DocumentBuilderFactory documentBuilderFactory =
			DocumentBuilderFactory.newInstance();

		DocumentBuilder documentBuilder =
			documentBuilderFactory.newDocumentBuilder();

		Document document = documentBuilder.parse(_serviceXmlFile);

		Element serviceBuilderElement = document.getDocumentElement();

		boolean autoNamespaceTables = true;

		String autoNamespaceTablesString = serviceBuilderElement.getAttribute(
			"auto-namespace-tables");

		if ((autoNamespaceTablesString != null) &&
			!autoNamespaceTablesString.isEmpty()) {

			autoNamespaceTables = Boolean.parseBoolean(
				autoNamespaceTablesString);
		}

		NodeList namespaceNodeList = serviceBuilderElement.getElementsByTagName(
			"namespace");

		if (namespaceNodeList.getLength() != 1) {
			throw new IllegalArgumentException(
				"Unable to get namespace from " + _serviceXmlFile);
		}

		Element namespaceElement = (Element)namespaceNodeList.item(0);

		String namespace = namespaceElement.getTextContent();

		NodeList entityNodeList = document.getElementsByTagName("entity");

		for (int i = 0; i < entityNodeList.getLength(); i++) {
			Element entityElement = (Element)entityNodeList.item(i);

			String tableName = entityElement.getAttribute("table");

			if ((tableName == null) || tableName.isEmpty()) {
				String entityName = entityElement.getAttribute("name");

				tableName = entityName;

				if (_badTableNames.contains(tableName)) {
					tableName += '_';
				}

				if (autoNamespaceTables) {
					tableName = namespace + "_" + entityName;
				}
			}

			_dropTable(connection, tableName);

			if (!_hasLocalizationTable(entityElement)) {
				continue;
			}

			_dropTable(connection, tableName + "Localization");
		}

		_deleteReleaseRows(connection);
		_deleteServiceComponentRows(connection, namespace);
	}

	private void _deleteReleaseRows(Connection connection) throws Exception {
		String sql = "delete from Release_ where servletContextName = ?";

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				sql)) {

			preparedStatement.setString(1, _servletContextName);

			preparedStatement.executeUpdate();
		}
	}

	private void _deleteServiceComponentRows(
			Connection connection, String namespace)
		throws Exception {

		String sql = "delete from ServiceComponent where buildNamespace = ?";

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				sql)) {

			preparedStatement.setString(1, namespace);

			preparedStatement.executeUpdate();
		}
	}

	private void _dropTable(Connection connection, String tableName)
		throws Exception {

		DatabaseMetaData databaseMetaData = connection.getMetaData();

		try (Statement statement = connection.createStatement();
			ResultSet resultSet1 = databaseMetaData.getTables(
				null, null, tableName, new String[] {"TABLE"})) {

			if (resultSet1.next()) {
				statement.executeUpdate("DROP TABLE " + tableName);
			}
			else {
				try (ResultSet resultSet2 = databaseMetaData.getTables(
						null, null, tableName.toUpperCase(),
						new String[] {"TABLE"})) {

					if (resultSet2.next()) {
						statement.executeUpdate("DROP TABLE " + tableName);
					}
				}
			}
		}
	}

	private boolean _hasLocalizationTable(Element entityElement) {
		NodeList columnNodeList = entityElement.getElementsByTagName("column");

		for (int i = 0; i < columnNodeList.getLength(); i++) {
			Element columnElement = (Element)columnNodeList.item(i);

			String localized = columnElement.getAttribute("localized");

			if (Objects.equals(localized, "extra-table")) {
				return true;
			}
		}

		return false;
	}

	private static final Set<String> _badTableNames = new HashSet<String>() {
		{
			ClassLoader classLoader =
				CleanServiceBuilderCommand.class.getClassLoader();

			try (BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(
						classLoader.getResourceAsStream(
							"com/liferay/portal/tools/service/builder" +
								"/dependencies/bad_table_names.txt"),
						StandardCharsets.UTF_8))) {

				String line = null;

				while ((line = bufferedReader.readLine()) != null) {
					add(line);
				}
			}
			catch (IOException ioException) {
				throw new ExceptionInInitializerError(ioException);
			}
		}
	};

	@Parameter(
		converter = FileConverter.class, description = "The service.xml file.",
		names = {"-x", "--service-xml-file"}, required = true
	)
	private File _serviceXmlFile;

	@Parameter(
		description = "The servlet context name (usually the value of the \"Bundle-Symbolic-Name\" manifest header) of the module.",
		names = {"-o", "--servlet-context-name"}, required = true
	)
	private String _servletContextName;

}