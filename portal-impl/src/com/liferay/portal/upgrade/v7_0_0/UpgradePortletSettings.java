/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_0_0;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.PortletPreferenceValue;
import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;

import java.lang.reflect.Method;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Set;
import java.util.TreeSet;

/**
 * @author Sergio González
 * @author Iván Zaera
 */
public abstract class UpgradePortletSettings extends UpgradeProcess {

	protected static String[] getKeys(Class<?>... classes) {
		Set<String> keys = new TreeSet<>();

		for (Class<?> clazz : classes) {
			for (Method method : clazz.getMethods()) {
				keys.add(method.getName());
			}
		}

		return keys.toArray(new String[0]);
	}

	protected static String[] getSettingsKeys(Class<?> settingsClass) {
		Set<String> keys = new TreeSet<>();

		for (Method method : settingsClass.getMethods()) {
			String name = method.getName();

			if (name.equals("getClass")) {
				continue;
			}

			if (name.startsWith("get")) {
				name = name.substring(3);
			}
			else if (name.startsWith("is")) {
				name = name.substring(2);
			}
			else {
				continue;
			}

			Settings.Property settingsProperty = method.getAnnotation(
				Settings.Property.class);

			if (settingsProperty != null) {
				if (settingsProperty.ignore()) {
					continue;
				}

				String propertyName = settingsProperty.name();

				if (!propertyName.isEmpty()) {
					keys.add(propertyName);

					continue;
				}
			}

			keys.add(StringUtil.lowerCaseFirstLetter(name));
		}

		return keys.toArray(new String[0]);
	}

	protected void copyPortletSettingsAsServiceSettings(
			String portletId, int ownerType, String serviceName)
		throws Exception {

		if (_log.isDebugEnabled()) {
			_log.debug("Copy portlet settings as service settings");
		}

		try (PreparedStatement selectPreparedStatement =
				connection.prepareStatement(
					StringBundler.concat(
						"select PortletPreferences.portletPreferencesId, ",
						"PortletPreferences.ownerId, PortletPreferences.plid, ",
						"Layout.groupId from PortletPreferences left join ",
						"Layout on Layout.plid = PortletPreferences.plid ",
						"where PortletPreferences.ownerType = ? and ",
						"PortletPreferences.portletId = ?"))) {

			selectPreparedStatement.setInt(1, ownerType);
			selectPreparedStatement.setString(2, portletId);

			try (PreparedStatement insertPreparedStatement =
					AutoBatchPreparedStatementUtil.autoBatch(
						connection,
						StringBundler.concat(
							"insert into PortletPreferences (",
							"mvccVersion, ctCollectionId, ",
							"portletPreferencesId, ownerId, ownerType, plid, ",
							"portletId) values (0, 0, ?, ?, ?, ?, ?)"));
				ResultSet resultSet = selectPreparedStatement.executeQuery()) {

				while (resultSet.next()) {
					long oldPortletPreferencesId = resultSet.getLong(
						"portletPreferencesId");

					long ownerId = 0;
					long plid = 0;

					if (ownerType == PortletKeys.PREFS_OWNER_TYPE_LAYOUT) {
						ownerId = resultSet.getLong("plid");
						plid = 0;
					}
					else {
						ownerId = resultSet.getLong("portletPreferencesId");
						plid = resultSet.getLong("ownerId");
					}

					long newPortletPreferencesId = increment();

					try {
						insertPreparedStatement.setLong(
							1, newPortletPreferencesId);
						insertPreparedStatement.setLong(2, ownerId);
						insertPreparedStatement.setInt(
							3, PortletKeys.PREFS_OWNER_TYPE_GROUP);
						insertPreparedStatement.setLong(4, plid);
						insertPreparedStatement.setString(5, serviceName);

						insertPreparedStatement.addBatch();

						_copyPortletPreferenceValues(
							oldPortletPreferencesId, newPortletPreferencesId);
					}
					catch (SQLException sqlException) {
						if (_log.isDebugEnabled()) {
							_log.debug(
								"Unable to copy portlet preferences " +
									oldPortletPreferencesId,
								sqlException);
						}
					}
				}

				insertPreparedStatement.executeBatch();
			}
		}
	}

	protected void resetPortletPreferencesValues(
			String portletId, int ownerType, String[] keys)
		throws Exception {

		if (keys.length == 0) {
			return;
		}

		StringBundler sb = new StringBundler(keys.length + 8);

		sb.append("delete from PortletPreferenceValue where ");
		sb.append("PortletPreferenceValue.portletPreferencesId in (select ");
		sb.append("PortletPreferences.portletPreferencesId from ");
		sb.append("PortletPreferences where PortletPreferences.ownerType = ");
		sb.append(ownerType);
		sb.append(" and PortletPreferences.portletId = '");
		sb.append(portletId);
		sb.append("') and PortletPreferenceValue.name in (?");

		for (int i = 1; i < keys.length; i++) {
			sb.append(", ?");
		}

		sb.append(")");

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				sb.toString())) {

			int i = 0;

			for (String key : keys) {
				preparedStatement.setString(++i, key);
			}

			preparedStatement.executeUpdate();
		}
	}

	protected void upgradeDisplayPortlet(
			String portletId, int ownerType, String[] serviceKeys)
		throws Exception {

		try (LoggingTimer loggingTimer = new LoggingTimer(portletId)) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Upgrading display portlet " + portletId + " settings");

				_log.debug("Delete service keys from portlet settings");
			}

			resetPortletPreferencesValues(portletId, ownerType, serviceKeys);

			resetPortletPreferencesValues(
				portletId, PortletKeys.PREFS_OWNER_TYPE_ARCHIVED, serviceKeys);
		}
	}

	protected void upgradeMainPortlet(
			String portletId, String serviceName, int ownerType,
			String[] portletInstanceKeys, String[] serviceKeys)
		throws Exception {

		try (LoggingTimer loggingTimer = new LoggingTimer(portletId)) {
			if (_log.isDebugEnabled()) {
				_log.debug("Upgrading main portlet " + portletId + " settings");
			}

			copyPortletSettingsAsServiceSettings(
				portletId, ownerType, serviceName);

			if (portletInstanceKeys.length > 0) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Delete portlet instance keys from service settings");
				}

				resetPortletPreferencesValues(
					serviceName, PortletKeys.PREFS_OWNER_TYPE_GROUP,
					portletInstanceKeys);
			}

			if (_log.isDebugEnabled()) {
				_log.debug("Delete service keys from portlet settings");
			}

			resetPortletPreferencesValues(portletId, ownerType, serviceKeys);

			resetPortletPreferencesValues(
				portletId, PortletKeys.PREFS_OWNER_TYPE_ARCHIVED, serviceKeys);
		}
	}

	private void _copyPortletPreferenceValues(
			long oldPortletPreferencesId, long newPortletPreferencesId)
		throws SQLException {

		try (PreparedStatement selectPreparedStatement =
				connection.prepareStatement(
					StringBundler.concat(
						"select portletPreferenceValueId from ",
						"PortletPreferenceValue where portletPreferencesId = ",
						"?"));
			PreparedStatement insertPreparedStatement =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					StringBundler.concat(
						"insert into PortletPreferenceValue (mvccVersion, ",
						"ctCollectionId, portletPreferenceValueId, companyId, ",
						"portletPreferencesId, index_, largeValue, name, ",
						"readOnly, smallValue) select 0 as mvccVersion, 0 as ",
						"ctCollectionId, ? as portletPreferenceValueId, ",
						"TEMP_TABLE.companyId, ? as portletPreferencesId, ",
						"TEMP_TABLE.index_, TEMP_TABLE.largeValue, ",
						"TEMP_TABLE.name, TEMP_TABLE.readOnly, ",
						"TEMP_TABLE.smallValue from PortletPreferenceValue ",
						"TEMP_TABLE where TEMP_TABLE.portletPreferenceValueId ",
						"= ?"))) {

			selectPreparedStatement.setLong(1, oldPortletPreferencesId);

			try (ResultSet resultSet = selectPreparedStatement.executeQuery()) {
				while (resultSet.next()) {
					insertPreparedStatement.setLong(
						1, increment(PortletPreferenceValue.class.getName()));
					insertPreparedStatement.setLong(2, newPortletPreferencesId);
					insertPreparedStatement.setLong(
						3, resultSet.getLong("portletPreferenceValueId"));

					insertPreparedStatement.addBatch();
				}

				insertPreparedStatement.executeBatch();
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UpgradePortletSettings.class);

}