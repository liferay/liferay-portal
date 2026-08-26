/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.cleanup.internal.upgrade;

import com.liferay.data.cleanup.internal.upgrade.util.LayoutTypeSettingsUtil;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.ArrayUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Alberto Chaparro
 */
public abstract class BaseUpgradeProcess extends UpgradeProcess {

	protected void removeExpandoData(
			ExpandoTableLocalService expandoTableLocalService,
			String expandoTableName)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select tableId from ExpandoTable where name = ?")) {

			preparedStatement.setString(1, expandoTableName);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					expandoTableLocalService.deleteTable(
						resultSet.getLong("tableId"));
				}
			}
		}
	}

	protected void removePortletData(
			String[] bundleSymbolicNames, String[] oldPortletIds,
			String[] portletIds)
		throws Exception {

		if (ArrayUtil.getLength(oldPortletIds) > 0) {
			try (PreparedStatement preparedStatement =
					connection.prepareStatement(
						"select portletId from Portlet where portletId = ?")) {

				preparedStatement.setString(1, oldPortletIds[0]);

				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					if (resultSet.next()) {
						portletIds = oldPortletIds;
					}
				}
			}
		}

		LayoutTypeSettingsUtil.removePortletIds(connection, portletIds);

		_deleteFromPortlet(portletIds);

		_deleteFromPortletPreferences(portletIds);

		_deleteFromRelease(bundleSymbolicNames);

		_deleteFromResourceAction(portletIds);

		_deleteFromResourcePermission(portletIds);
	}

	protected void removeServiceData(
			String buildNamespace, String[] bundleSymbolicNames,
			String[] modelResourceNames, String[] tableNames)
		throws Exception {

		_deleteFromClassName(modelResourceNames);

		_deleteFromRelease(bundleSymbolicNames);

		_deleteFromResourceAction(modelResourceNames);

		_deleteFromResourcePermission(modelResourceNames);

		_deleteFromServiceComponent(buildNamespace);

		_dropTables(tableNames);
	}

	private void _deleteFrom(
			String tableName, String columnName, String... columnValues)
		throws Exception {

		if (columnValues == null) {
			return;
		}

		for (String columnValue : columnValues) {
			runSQL(
				StringBundler.concat(
					"delete from ", tableName, " where ", columnName, " = '",
					columnValue, "'"));
		}
	}

	private void _deleteFromClassName(String[] classNames) throws Exception {
		if (ArrayUtil.isEmpty(classNames)) {
			return;
		}

		_deleteFrom("ClassName_", "value", classNames);

		ClassNameLocalServiceUtil.invalidate();
	}

	private void _deleteFromPortlet(String[] portletIds) throws Exception {
		_deleteFrom("Portlet", "portletId", portletIds);
	}

	private void _deleteFromPortletPreferences(String[] portletIds)
		throws Exception {

		if (portletIds == null) {
			return;
		}

		for (String portletId : portletIds) {
			runSQL(
				StringBundler.concat(
					"delete from PortletPreferences where portletId like '",
					portletId, "%'"));
		}
	}

	private void _deleteFromRelease(String[] servletContextNames)
		throws Exception {

		_deleteFrom("Release_", "servletContextName", servletContextNames);
	}

	private void _deleteFromResourceAction(String[] names) throws Exception {
		_deleteFrom("ResourceAction", "name", names);
	}

	private void _deleteFromResourcePermission(String[] names)
		throws Exception {

		_deleteFrom("ResourcePermission", "name", names);
	}

	private void _deleteFromServiceComponent(String buildNamespace)
		throws Exception {

		_deleteFrom("ServiceComponent", "buildNamespace", buildNamespace);
	}

	private void _dropTables(String[] tableNames) throws Exception {
		if (tableNames == null) {
			return;
		}

		for (String tableName : tableNames) {
			if (hasTable(tableName)) {
				runSQL("drop table " + tableName);
			}
		}
	}

}