/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.upgrade;

import com.liferay.exportimport.kernel.staging.constants.StagingConstants;
import com.liferay.layout.admin.kernel.model.LayoutTypePortletConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.PortletPreferenceValue;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Brian Wing Shun Chan
 */
public abstract class BasePortletIdUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		upgradeInstanceablePortletIds();
		upgradeUninstanceablePortletIds();
	}

	protected String getNewTypeSettings(
		String typeSettings, String oldPropertyId, String newPropertyId) {

		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.create(
				true
			).fastLoad(
				typeSettings
			).build();

		String value = typeSettingsUnicodeProperties.remove(oldPropertyId);

		if (value != null) {
			typeSettingsUnicodeProperties.setProperty(newPropertyId, value);
		}

		return typeSettingsUnicodeProperties.toString();
	}

	protected String getNewTypeSettings(
		String typeSettings, String oldRootPortletId, String newRootPortletId,
		boolean exactMatch) {

		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.create(
				true
			).fastLoad(
				typeSettings
			).build();

		for (Map.Entry<String, String> entry :
				typeSettingsUnicodeProperties.entrySet()) {

			String typeSettingId = entry.getKey();

			if (!LayoutTypePortletConstants.hasPortletIds(typeSettingId)) {
				continue;
			}

			String[] portletIds = StringUtil.split(entry.getValue());

			for (int j = 0; j < portletIds.length; j++) {
				String portletId = portletIds[j];

				if (exactMatch) {
					if (portletId.equals(oldRootPortletId)) {
						portletIds[j] = newRootPortletId;
					}

					continue;
				}

				String rootPortletId = PortletIdCodec.decodePortletName(
					portletId);

				if (!rootPortletId.equals(oldRootPortletId)) {
					continue;
				}

				long userId = PortletIdCodec.decodeUserId(portletId);
				String instanceId = PortletIdCodec.decodeInstanceId(portletId);

				portletIds[j] = PortletIdCodec.encode(
					newRootPortletId, userId, instanceId);
			}

			String portletIdsString = StringUtil.merge(portletIds);

			typeSettingsUnicodeProperties.setProperty(
				typeSettingId, portletIdsString);
		}

		return typeSettingsUnicodeProperties.toString();
	}

	protected String[][] getRenamePortletIdsArray() {
		return new String[0][0];
	}

	protected String getTypeSettingsCriteria(String portletId) {
		return StringBundler.concat(
			"typeSettings like '%=", portletId, ",%' OR typeSettings like '%=",
			portletId, "\n%' OR typeSettings like '%=", portletId,
			"' OR typeSettings like '%,", portletId,
			",%' OR typeSettings like '%,", portletId,
			"\n%' OR typeSettings like '%,", portletId,
			"' OR typeSettings like '%=", portletId,
			"_INSTANCE_%' OR typeSettings like '%,", portletId,
			"_INSTANCE_%' OR typeSettings like '%=", portletId,
			"_USER_%' OR typeSettings like '%,", portletId, "_USER_%'");
	}

	protected String[] getUninstanceablePortletIds() {
		return new String[0];
	}

	protected void updateGroup(String oldRootPortletId, String newRootPortletId)
		throws Exception {

		String oldStagedPortletId = _getStagedPortletId(oldRootPortletId);

		String sql1 = StringBundler.concat(
			"select groupId, typeSettings from Group_ where typeSettings like ",
			"'%", oldStagedPortletId, "%'");

		String sql2 = "update Group_ set typeSettings = ? where groupId = ?";

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				sql1);
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection, sql2);
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			while (resultSet.next()) {
				String typeSettings = resultSet.getString("typeSettings");

				String newTypeSettings = getNewTypeSettings(
					typeSettings, oldStagedPortletId,
					_getStagedPortletId(newRootPortletId));

				if (!Objects.equals(typeSettings, newTypeSettings)) {
					preparedStatement2.setString(1, newTypeSettings);
					preparedStatement2.setLong(2, resultSet.getLong("groupId"));

					preparedStatement2.addBatch();
				}
			}

			preparedStatement2.executeBatch();
		}
	}

	protected void updateInstanceablePortletPreferences(
			String oldRootPortletId, String newRootPortletId)
		throws Exception {

		runSQL(
			StringBundler.concat(
				"update PortletPreferences set portletId = '", newRootPortletId,
				"' where portletId = '", oldRootPortletId, "'"));

		if (!newRootPortletId.contains("_INSTANCE_")) {
			runSQL(
				StringBundler.concat(
					"update PortletPreferences set portletId = replace(",
					"portletId, '", oldRootPortletId, "_INSTANCE_', '",
					newRootPortletId, "_INSTANCE_') where portletId like '",
					oldRootPortletId, "_INSTANCE_%'"));
		}

		runSQL(
			StringBundler.concat(
				"update PortletPreferences set portletId = replace(portletId, ",
				"'", oldRootPortletId, "_USER_', '", newRootPortletId,
				"_USER_') where portletId like '", oldRootPortletId,
				"_USER_%'"));

		if (hasColumn("PortletPreferences", "preferences")) {
			runSQL(
				StringBundler.concat(
					"update PortletPreferences set preferences = replace(",
					"preferences, '#p_p_id_", oldRootPortletId, "', '#p_p_id_",
					newRootPortletId, "') where portletId = '",
					newRootPortletId, "'"));
			runSQL(
				StringBundler.concat(
					"update PortletPreferences set preferences = replace(",
					"preferences, '#portlet_", oldRootPortletId,
					"', '#portlet_", newRootPortletId, "') where portletId = '",
					newRootPortletId, "'"));

			if (!newRootPortletId.contains("_INSTANCE_")) {
				runSQL(
					StringBundler.concat(
						"update PortletPreferences set preferences = replace(",
						"preferences, '#p_p_id_", oldRootPortletId,
						"_INSTANCE_', '#p_p_id_", newRootPortletId,
						"_INSTANCE_') where portletId like '", newRootPortletId,
						"_INSTANCE_%'"));
				runSQL(
					StringBundler.concat(
						"update PortletPreferences set preferences = replace(",
						"preferences, '#portlet_", oldRootPortletId,
						"_INSTANCE_', '#portlet_", newRootPortletId,
						"_INSTANCE_') where portletId like '", newRootPortletId,
						"_INSTANCE_%'"));
			}

			runSQL(
				StringBundler.concat(
					"update PortletPreferences set preferences = replace(",
					"preferences, '#p_p_id_", oldRootPortletId,
					"_USER_', '#p_p_id_", newRootPortletId,
					"_USER_') where portletId like '", newRootPortletId,
					"_USER_%'"));
			runSQL(
				StringBundler.concat(
					"update PortletPreferences set preferences = replace(",
					"preferences, '#portlet_", oldRootPortletId,
					"_USER_', '#portlet_", newRootPortletId,
					"_USER_') where portletId like '", newRootPortletId,
					"_USER_%'"));
		}
		else {
			int smallValueMaxLength = ModelHintsUtil.getMaxLength(
				PortletPreferenceValue.class.getName(), "smallValue");

			String selectSQL = StringBundler.concat(
				"select PortletPreferenceValue.portletPreferenceValueId, ",
				"PortletPreferenceValue.largeValue, ",
				"PortletPreferenceValue.smallValue from ",
				"PortletPreferenceValue inner join PortletPreferences on ",
				"PortletPreferences.portletPreferencesId = ",
				"PortletPreferenceValue.portletPreferencesId where ",
				"(PortletPreferences.portletId = '", newRootPortletId,
				"' or PortletPreferences.portletId like '", newRootPortletId,
				"_INSTANCE_%' or PortletPreferences.portletId like '",
				newRootPortletId, "_USER_%') and PortletPreferenceValue.name ",
				"= 'portletSetupCss'");

			String updateSQL =
				"update PortletPreferenceValue set largeValue = ?, " +
					"smallValue = ? where portletPreferenceValueId = ?";

			try (PreparedStatement selectPreparedStatement =
					connection.prepareStatement(selectSQL);
				PreparedStatement updatePreparedStatement =
					AutoBatchPreparedStatementUtil.autoBatch(
						connection, updateSQL);
				ResultSet resultSet = selectPreparedStatement.executeQuery()) {

				while (resultSet.next()) {
					String value = resultSet.getString("smallValue");

					if (Validator.isBlank(value)) {
						value = resultSet.getString("largeValue");
					}

					String newValue = StringUtil.replace(
						value,
						new String[] {
							"#p_p_id_" + oldRootPortletId,
							"#portlet_" + oldRootPortletId
						},
						new String[] {
							"#p_p_id_" + newRootPortletId,
							"#portlet_" + newRootPortletId
						});

					if (Objects.equals(value, newValue)) {
						continue;
					}

					String largeValue = null;
					String smallValue = null;

					if (newValue.length() > smallValueMaxLength) {
						largeValue = newValue;
					}
					else {
						smallValue = newValue;
					}

					updatePreparedStatement.setString(1, largeValue);
					updatePreparedStatement.setString(2, smallValue);

					updatePreparedStatement.setLong(
						3, resultSet.getLong("portletPreferenceValueId"));

					updatePreparedStatement.addBatch();
				}

				updatePreparedStatement.executeBatch();
			}
		}
	}

	protected void updateLayout(long plid, String typeSettings)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"update Layout set typeSettings = ? where plid = " + plid)) {

			preparedStatement.setString(1, typeSettings);

			preparedStatement.executeUpdate();
		}
		catch (SQLException sqlException) {
			if (_log.isWarnEnabled()) {
				_log.warn(sqlException);
			}
		}
	}

	protected void updateLayout(
			long plid, String oldPortletId, String newPortletId)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select typeSettings from Layout where plid = " + plid);
			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				String typeSettings = resultSet.getString("typeSettings");

				String newTypeSettings = StringUtil.replace(
					typeSettings, oldPortletId, newPortletId);

				if (!Objects.equals(typeSettings, newTypeSettings)) {
					updateLayout(plid, newTypeSettings);
				}
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}
	}

	protected void updateLayoutRevision(
			long layoutRevisionId, String typeSettings)
		throws Exception {

		String sql =
			"update LayoutRevision set typeSettings = ? where " +
				"layoutRevisionId = " + layoutRevisionId;

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				sql)) {

			preparedStatement.setString(1, typeSettings);

			preparedStatement.executeUpdate();
		}
		catch (SQLException sqlException) {
			if (_log.isWarnEnabled()) {
				_log.warn(sqlException);
			}
		}
	}

	protected void updateLayoutRevisions(
			String oldRootPortletId, String newRootPortletId,
			boolean exactMatch)
		throws Exception {

		String sql1 =
			"select layoutRevisionId, typeSettings from LayoutRevision where " +
				getTypeSettingsCriteria(oldRootPortletId);
		String sql2 =
			"update LayoutRevision set typeSettings = ? where " +
				"layoutRevisionId = ?";

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				sql1);
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection, sql2);
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			while (resultSet.next()) {
				String typeSettings = resultSet.getString("typeSettings");

				String newTypeSettings = getNewTypeSettings(
					typeSettings, oldRootPortletId, newRootPortletId,
					exactMatch);

				if (!Objects.equals(typeSettings, newTypeSettings)) {
					preparedStatement2.setString(1, newTypeSettings);
					preparedStatement2.setLong(
						2, resultSet.getLong("layoutRevisionId"));

					preparedStatement2.addBatch();
				}
			}

			preparedStatement2.executeBatch();
		}
	}

	protected void updateLayouts(
			String oldRootPortletId, String newRootPortletId,
			boolean exactMatch)
		throws Exception {

		String sql1 =
			"select plid, typeSettings from Layout where " +
				getTypeSettingsCriteria(oldRootPortletId);
		String sql2 = "update Layout set typeSettings = ? where plid = ?";

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				sql1);
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection, sql2);
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			while (resultSet.next()) {
				String typeSettings = resultSet.getString("typeSettings");

				String newTypeSettings = getNewTypeSettings(
					typeSettings, oldRootPortletId, newRootPortletId,
					exactMatch);

				if (!Objects.equals(typeSettings, newTypeSettings)) {
					preparedStatement2.setString(1, newTypeSettings);
					preparedStatement2.setLong(2, resultSet.getLong("plid"));

					preparedStatement2.addBatch();
				}
			}

			preparedStatement2.executeBatch();
		}
	}

	protected void updatePortlet(
			String oldRootPortletId, String newRootPortletId)
		throws Exception {

		try {
			updatePortletId(oldRootPortletId, newRootPortletId);

			updatePortletItem(oldRootPortletId, newRootPortletId);

			updateResourceAction(oldRootPortletId, newRootPortletId);

			updateResourcePermission(oldRootPortletId, newRootPortletId, true);

			updateUserNotificationDelivery(oldRootPortletId, newRootPortletId);

			updateUserNotificationEvent(oldRootPortletId, newRootPortletId);

			updateInstanceablePortletPreferences(
				oldRootPortletId, newRootPortletId);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}
	}

	protected void updatePortletId(
			String oldRootPortletId, String newRootPortletId)
		throws Exception {

		runSQL(
			StringBundler.concat(
				"update Portlet set portletId = '", newRootPortletId,
				"' where portletId = '", oldRootPortletId, "'"));
	}

	protected void updatePortletItem(
			String oldRootPortletId, String newRootPortletId)
		throws Exception {

		runSQL(
			StringBundler.concat(
				"update PortletItem set portletId = '", newRootPortletId,
				"' where portletId = '", oldRootPortletId, "'"));
	}

	protected void updateResourceAction(String oldName, String newName)
		throws Exception {

		List<String> actionIds = new ArrayList<>();

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select actionId from ResourceAction where name = '" + newName +
					"'");
			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				actionIds.add(resultSet.getString("actionId"));
			}
		}

		if (actionIds.isEmpty()) {
			runSQL(
				StringBundler.concat(
					"update ResourceAction set name = '", newName,
					"' where name = '", oldName, "'"));
		}
		else {
			StringBundler sb = new StringBundler(5 + (3 * actionIds.size()));

			sb.append("update ResourceAction set name = '");
			sb.append(newName);
			sb.append("' where name = '");
			sb.append(oldName);
			sb.append("' and actionId not in (");

			for (int i = 0; i < actionIds.size(); i++) {
				sb.append("'");
				sb.append(actionIds.get(i));

				if (i == (actionIds.size() - 1)) {
					sb.append("')");
				}
				else {
					sb.append("', ");
				}
			}

			runSQL(sb.toString());

			runSQL("delete from ResourceAction where name = '" + oldName + "'");
		}
	}

	protected void updateResourcePermission(
			String oldRootPortletId, String newRootPortletId,
			boolean updateName)
		throws Exception {

		runSQL(
			StringBundler.concat(
				"update ResourcePermission set primKey = replace(primKey, ",
				"'_LAYOUT_", oldRootPortletId, "', '_LAYOUT_", newRootPortletId,
				"') where name = '", oldRootPortletId,
				"' and primKey like '%_LAYOUT_", oldRootPortletId, "'"));

		if (!newRootPortletId.contains("_INSTANCE_")) {
			runSQL(
				StringBundler.concat(
					"update ResourcePermission set primKey = replace(primKey, ",
					"'_LAYOUT_", oldRootPortletId, "_INSTANCE_', '_LAYOUT_",
					newRootPortletId, "_INSTANCE_') where name = '",
					oldRootPortletId, "' and primKey like '%_LAYOUT_",
					oldRootPortletId, "_INSTANCE_%'"));
		}

		if (updateName) {
			runSQL(
				StringBundler.concat(
					"update ResourcePermission set primKey = '",
					newRootPortletId, "' where name = '", oldRootPortletId,
					"' and primKey = '", oldRootPortletId, "'"));

			runSQL(
				StringBundler.concat(
					"update ResourcePermission set name = '", newRootPortletId,
					"' where name = '", oldRootPortletId, "'"));
		}
	}

	protected void updateUserNotificationDelivery(
			String oldPortletId, String newPortletId)
		throws Exception {

		runSQL(
			StringBundler.concat(
				"update UserNotificationDelivery set portletId = '",
				newPortletId, "' where portletId = '", oldPortletId, "'"));
	}

	protected void updateUserNotificationEvent(
			String oldPortletId, String newPortletId)
		throws Exception {

		runSQL(
			StringBundler.concat(
				"update UserNotificationEvent set type_ = '", newPortletId,
				"' where type_ = '", oldPortletId, "'"));
	}

	protected void upgradeInstanceablePortletIds() throws Exception {

		// Rename instanceable portlet IDs. We expect the root form of the
		// portlet ID because we will rename all instances of the portlet ID.

		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			String[][] renamePortletIdsArray = getRenamePortletIdsArray();

			for (String[] renamePortletIds : renamePortletIdsArray) {
				String oldRootPortletId = renamePortletIds[0];
				String newRootPortletId = renamePortletIds[1];

				updateGroup(oldRootPortletId, newRootPortletId);
				updatePortlet(oldRootPortletId, newRootPortletId);
				updateLayoutRevisions(
					oldRootPortletId, newRootPortletId, false);
				updateLayouts(oldRootPortletId, newRootPortletId, false);

				_updateFragmentEntryLinks(oldRootPortletId, newRootPortletId);
			}
		}
	}

	protected void upgradeUninstanceablePortletIds() throws Exception {

		// Rename uninstanceable portlet IDs to instanceable portlet IDs

		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			String[] uninstanceablePortletIds = getUninstanceablePortletIds();

			for (String portletId : uninstanceablePortletIds) {
				if (PortletIdCodec.hasInstanceId(portletId)) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Portlet " + portletId +
								" is already instanceable");
					}

					continue;
				}

				PortletIdCodec.validatePortletName(portletId);

				String newPortletInstanceKey = PortletIdCodec.encode(portletId);

				updateInstanceablePortletPreferences(
					portletId, newPortletInstanceKey);
				updateResourcePermission(
					portletId, newPortletInstanceKey, false);
				updateLayoutRevisions(portletId, newPortletInstanceKey, true);
				updateLayouts(portletId, newPortletInstanceKey, true);
			}
		}
	}

	private String _getStagedPortletId(String portletId) {
		String key = portletId;

		if (key.startsWith(StagingConstants.STAGED_PORTLET)) {
			return key;
		}

		return StagingConstants.STAGED_PORTLET.concat(portletId);
	}

	private void _updateFragmentEntryLinks(
			String oldRootPortletId, String newRootPortletId)
		throws Exception {

		if (!hasTable("FragmentEntryLink")) {
			return;
		}

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select fragmentEntryLinkId, editableValues from ",
					"FragmentEntryLink where editableValues like '%",
					oldRootPortletId, "%'"));
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update FragmentEntryLink set editableValues = ? where " +
						"fragmentEntryLinkId = ?");
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			while (resultSet.next()) {
				String editableValues = resultSet.getString("editableValues");

				try {
					JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
						editableValues);

					String portletId = jsonObject.getString("portletId");

					if (Objects.equals(portletId, oldRootPortletId)) {
						jsonObject.put("portletId", newRootPortletId);

						preparedStatement2.setString(1, jsonObject.toString());
						preparedStatement2.setLong(
							2, resultSet.getLong("fragmentEntryLinkId"));

						preparedStatement2.addBatch();
					}
				}
				catch (JSONException jsonException) {
					_log.error(
						"Unable to create a JSON object from: " +
							editableValues,
						jsonException);
				}
			}

			preparedStatement2.executeBatch();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BasePortletIdUpgradeProcess.class);

}