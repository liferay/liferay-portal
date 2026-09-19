/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.upgrade;

import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.UnsecureSAXReaderUtil;

import java.io.InputStream;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

import java.util.List;

/**
 * @author     Shuyang Zhou
 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
 *             MVCCVersionUpgradeProcess}
 */
@Deprecated
public class UpgradeMVCCVersion extends UpgradeProcess {

	public void upgradeMVCCVersion(
			DatabaseMetaData databaseMetaData, String tableName)
		throws Exception {

		for (String excludeTableName : getExcludedTableNames()) {
			if (StringUtil.equalsIgnoreCase(excludeTableName, tableName)) {
				return;
			}
		}

		DBInspector dbInspector = new DBInspector(connection);

		tableName = dbInspector.normalizeName(tableName, databaseMetaData);

		try (ResultSet tableResultSet = databaseMetaData.getTables(
				dbInspector.getCatalog(), dbInspector.getSchema(), tableName,
				null)) {

			if (!tableResultSet.next()) {
				return;
			}

			try (ResultSet columnResultSet = databaseMetaData.getColumns(
					dbInspector.getCatalog(), dbInspector.getSchema(),
					tableName,
					dbInspector.normalizeName(
						"mvccVersion", databaseMetaData))) {

				if (columnResultSet.next()) {
					return;
				}

				try (LoggingTimer loggingTimer = new LoggingTimer(tableName)) {
					alterTableAddColumn(
						tableName, "mvccVersion", "LONG default 0 not null");
				}
			}
		}
	}

	@Override
	protected void doUpgrade() throws Exception {
		upgradeClassElementMVCCVersions();
		upgradeModuleTableMVCCVersions();
	}

	protected List<Element> getClassElements() throws Exception {
		Thread currentThread = Thread.currentThread();

		ClassLoader classLoader = currentThread.getContextClassLoader();

		InputStream inputStream = classLoader.getResourceAsStream(
			"META-INF/portal-hbm.xml");

		Document document = UnsecureSAXReaderUtil.read(inputStream);

		Element rootElement = document.getRootElement();

		return rootElement.elements("class");
	}

	protected String[] getExcludedTableNames() {
		return new String[0];
	}

	protected String[] getModuleTableNames() {
		return new String[] {"BackgroundTask", "Lock_"};
	}

	protected void upgradeClassElementMVCCVersions() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			DatabaseMetaData databaseMetaData = connection.getMetaData();

			List<Element> classElements = getClassElements();

			for (Element classElement : classElements) {
				if (classElement.element("version") == null) {
					continue;
				}

				upgradeMVCCVersion(databaseMetaData, classElement);
			}
		}
	}

	protected void upgradeMVCCVersion(
			DatabaseMetaData databaseMetaData, Element classElement)
		throws Exception {

		String tableName = classElement.attributeValue("table");

		upgradeMVCCVersion(databaseMetaData, tableName);
	}

	protected void upgradeModuleTableMVCCVersions() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			DatabaseMetaData databaseMetaData = connection.getMetaData();

			String[] moduleTableNames = getModuleTableNames();

			for (String moduleTableName : moduleTableNames) {
				upgradeMVCCVersion(databaseMetaData, moduleTableName);
			}
		}
	}

}