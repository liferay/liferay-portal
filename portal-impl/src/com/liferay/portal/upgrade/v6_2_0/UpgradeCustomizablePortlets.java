/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v6_2_0;

import com.liferay.layout.admin.kernel.model.LayoutTypePortletConstants;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portlet.PortalPreferencesImpl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Raymond Augé
 */
public class UpgradeCustomizablePortlets extends UpgradeProcess {

	public static String namespacePlid(long plid) {
		return "com.liferay.portal.model.CustomizedPages".concat(
			String.valueOf(plid));
	}

	@Override
	protected void doUpgrade() throws Exception {
		upgradeCustomizablePreferences();
	}

	protected PortalPreferencesImpl getPortalPreferencesImpl(
		long ownerId, int ownerType, String xml) {

		return (PortalPreferencesImpl)PortletPreferencesFactoryUtil.fromXML(
			ownerId, ownerType, xml);
	}

	protected void upgradeCustomizablePreferences() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer();
			PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select portalPreferencesId, ownerId, ownerType, preferences " +
					"from PortalPreferences where preferences like " +
						"'%com.liferay.portal.model.CustomizedPages%'");
			ResultSet resultSet = preparedStatement1.executeQuery();
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update PortalPreferences set preferences = ? where " +
						"portalPreferencesId = ?")) {

			while (resultSet.next()) {
				long portalPreferencesId = resultSet.getLong(
					"portalPreferencesId");

				long ownerId = resultSet.getLong("ownerId");
				int ownerType = resultSet.getInt("ownerType");
				String preferences = resultSet.getString("preferences");

				PortalPreferencesImpl portalPreferencesImpl =
					getPortalPreferencesImpl(ownerId, ownerType, preferences);

				upgradeCustomizablePreferences(
					portalPreferencesImpl, ownerId, ownerType, preferences);

				preparedStatement2.setString(1, portalPreferencesImpl.toXML());

				preparedStatement2.setLong(2, portalPreferencesId);

				preparedStatement2.addBatch();
			}

			preparedStatement2.executeBatch();
		}
	}

	protected void upgradeCustomizablePreferences(
			PortalPreferencesImpl portalPreferencesImpl, long ownerId,
			int ownerType, String preferences)
		throws Exception {

		int x = preferences.indexOf(_PREFIX);
		int y = -1;

		if (x != -1) {
			x += _PREFIX.length();
			y = preferences.indexOf(_SUFFIX, x);
		}
		else {
			return;
		}

		while (x != -1) {

			// <name>com.liferay.portal.model.CustomizedPages10415#column-1
			// </name>

			String[] parts = StringUtil.split(
				preferences.substring(x, y), StringPool.POUND);

			String key = GetterUtil.getString(parts[1]);

			if (LayoutTypePortletConstants.isLayoutTemplateColumnName(key)) {
				long plid = GetterUtil.getLong(parts[0]);

				String value = portalPreferencesImpl.getValue(
					namespacePlid(plid), key);

				List<String> newPortletIds = new ArrayList<>();

				StringBundler sb = new StringBundler(4);

				sb.append("update PortletPreferences set ownerId = ?, ");
				sb.append("ownerType = ?, plid = ?, portletId = ? where ");
				sb.append("ownerId = ? and ownerType = ? and plid = ? and ");
				sb.append("portletId = ?");

				try (PreparedStatement preparedStatement =
						AutoBatchPreparedStatementUtil.autoBatch(
							connection, sb.toString())) {

					for (String customPortletId : StringUtil.split(value)) {
						if (!PortletIdCodec.hasInstanceId(customPortletId)) {
							newPortletIds.add(customPortletId);
						}
						else {
							String instanceId = PortletIdCodec.decodeInstanceId(
								customPortletId);

							String newPortletId = PortletIdCodec.encode(
								PortletIdCodec.decodePortletName(
									customPortletId),
								ownerId, instanceId);

							preparedStatement.setLong(1, ownerId);
							preparedStatement.setInt(
								2, PortletKeys.PREFS_OWNER_TYPE_USER);
							preparedStatement.setLong(3, plid);
							preparedStatement.setString(4, newPortletId);
							preparedStatement.setLong(5, 0L);
							preparedStatement.setInt(
								6, PortletKeys.PREFS_OWNER_TYPE_LAYOUT);
							preparedStatement.setLong(7, plid);
							preparedStatement.setString(8, newPortletId);

							newPortletIds.add(newPortletId);

							preparedStatement.addBatch();
						}
					}

					preparedStatement.executeBatch();
				}

				value = StringUtil.merge(newPortletIds);

				portalPreferencesImpl.setValue(namespacePlid(plid), key, value);
			}

			x = preferences.indexOf(_PREFIX, y);
			y = -1;

			if (x != -1) {
				x += _PREFIX.length();
				y = preferences.indexOf(_SUFFIX, x);
			}
		}
	}

	private static final String _PREFIX =
		"<name>com.liferay.portal.model.CustomizedPages";

	private static final String _SUFFIX = "</name>";

}