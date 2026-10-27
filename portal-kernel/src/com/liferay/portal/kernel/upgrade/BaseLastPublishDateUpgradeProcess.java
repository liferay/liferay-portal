/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.upgrade;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Máté Thurzó
 */
public abstract class BaseLastPublishDateUpgradeProcess extends UpgradeProcess {

	protected void addLastPublishDateColumn(String tableName) throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer(tableName)) {
			alterTableAddColumn(tableName, "lastPublishDate", "DATE null");
		}
	}

	protected Date getLayoutSetLastPublishDate(long groupId) throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select settings_ from LayoutSet where groupId = ? and " +
					"privateLayout = ?")) {

			preparedStatement.setLong(1, groupId);

			for (boolean privateLayout : new boolean[] {false, true}) {
				preparedStatement.setBoolean(2, privateLayout);

				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					while (resultSet.next()) {
						UnicodeProperties settingsUnicodeProperties =
							UnicodePropertiesBuilder.create(
								true
							).load(
								resultSet.getString("settings_")
							).build();

						String lastPublishDateString =
							settingsUnicodeProperties.getProperty(
								"last-publish-date");

						if (Validator.isNotNull(lastPublishDateString)) {
							return new Date(
								GetterUtil.getLong(lastPublishDateString));
						}
					}
				}
			}

			return null;
		}
	}

	protected Date getPortletLastPublishDate(long groupId, String portletId)
		throws Exception {

		if (!hasColumn("PortletPreferences", "preferences")) {
			try (PreparedStatement preparedStatement =
					connection.prepareStatement(
						StringBundler.concat(
							"select PortletPreferenceValue.smallValue from ",
							"PortletPreferenceValue inner join ",
							"PortletPreferences on ",
							"PortletPreferences.portletPreferencesId = ",
							"PortletPreferenceValue.portletPreferencesId ",
							"where PortletPreferences.plid = ? and ",
							"PortletPreferences.ownerType = ? and ",
							"PortletPreferences.ownerId = ? and ",
							"PortletPreferences.portletId = ? and ",
							"PortletPreferenceValue.name = ?"))) {

				preparedStatement.setLong(1, LayoutConstants.DEFAULT_PLID);
				preparedStatement.setInt(2, PortletKeys.PREFS_OWNER_TYPE_GROUP);
				preparedStatement.setLong(3, groupId);
				preparedStatement.setString(4, portletId);
				preparedStatement.setString(5, "last-publish-date");

				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					while (resultSet.next()) {
						String value = resultSet.getString("smallValue");

						if (Validator.isNotNull(value)) {
							return new Date(GetterUtil.getLong(value));
						}
					}
				}
			}

			return null;
		}

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select preferences from PortletPreferences where plid = ? " +
					"and ownerType = ? and ownerId = ? and portletId = ?")) {

			preparedStatement.setLong(1, LayoutConstants.DEFAULT_PLID);
			preparedStatement.setInt(2, PortletKeys.PREFS_OWNER_TYPE_GROUP);
			preparedStatement.setLong(3, groupId);
			preparedStatement.setString(4, portletId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					String preferences = resultSet.getString("preferences");

					if (Validator.isNull(preferences)) {
						continue;
					}

					int x = preferences.lastIndexOf(
						"last-publish-date</name><value>");

					if (x < 0) {
						break;
					}

					int y = preferences.indexOf("</value>", x);

					String lastPublishDateString = preferences.substring(x, y);

					if (Validator.isNotNull(lastPublishDateString)) {
						return new Date(
							GetterUtil.getLong(lastPublishDateString));
					}
				}

				return null;
			}
		}
	}

	protected List<Long> getStagedGroupIds() throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select groupId from Group_ where typeSettings like " +
					"'%staged=true%'");

			ResultSet resultSet = preparedStatement.executeQuery()) {

			List<Long> stagedGroupIds = new ArrayList<>();

			while (resultSet.next()) {
				long stagedGroupId = resultSet.getLong("groupId");

				stagedGroupIds.add(stagedGroupId);
			}

			return stagedGroupIds;
		}
	}

	protected void updateLastPublishDates(String portletId, String tableName)
		throws Exception {

		try (LoggingTimer loggingTimer = new LoggingTimer(tableName)) {
			List<Long> stagedGroupIds = getStagedGroupIds();

			for (long stagedGroupId : stagedGroupIds) {
				Date lastPublishDate = getPortletLastPublishDate(
					stagedGroupId, portletId);

				if (lastPublishDate == null) {
					lastPublishDate = getLayoutSetLastPublishDate(
						stagedGroupId);
				}

				if (lastPublishDate == null) {
					continue;
				}

				updateStagedModelLastPublishDates(
					stagedGroupId, tableName, lastPublishDate);
			}
		}
	}

	protected void updateStagedModelLastPublishDates(
			long groupId, String tableName, Date lastPublishDate)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"update ", tableName, " set lastPublishDate = ? where ",
					"groupId = ?"))) {

			preparedStatement.setDate(
				1, new java.sql.Date(lastPublishDate.getTime()));
			preparedStatement.setLong(2, groupId);

			preparedStatement.executeUpdate();
		}
	}

}