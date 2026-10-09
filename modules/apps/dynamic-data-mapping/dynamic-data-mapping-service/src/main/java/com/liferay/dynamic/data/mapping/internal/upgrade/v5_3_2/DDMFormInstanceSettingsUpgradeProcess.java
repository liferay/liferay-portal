/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.upgrade.v5_3_2;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.StringUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Carolina Barbosa
 */
public class DDMFormInstanceSettingsUpgradeProcess extends UpgradeProcess {

	public DDMFormInstanceSettingsUpgradeProcess(JSONFactory jsonFactory) {
		_jsonFactory = jsonFactory;
	}

	@Override
	protected void doUpgrade() throws Exception {
		StringBundler sb = new StringBundler(2);

		sb.append("where settings_ like '%storageType\\\",\\\"");
		sb.append("value\\\":\\\"[\\\\\\\\\"object\\\\\\\\\"]\\\"%'");

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select ctCollectionId, formInstanceId, settings_ from " +
					"DDMFormInstance " + sb.toString());
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update DDMFormInstance set settings_ = ? where " +
						"ctCollectionId = ? and formInstanceId = ?");
			PreparedStatement preparedStatement3 = connection.prepareStatement(
				"select ctCollectionId, formInstanceVersionId, settings_ " +
					"from DDMFormInstanceVersion " + sb.toString());
			PreparedStatement preparedStatement4 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update DDMFormInstanceVersion set settings_ = ? where " +
						"ctCollectionId = ? and formInstanceVersionId = ?")) {

			_executePreparedStatements(
				"formInstanceId", preparedStatement1, preparedStatement2);
			_executePreparedStatements(
				"formInstanceVersionId", preparedStatement3,
				preparedStatement4);
		}
	}

	private void _executePreparedStatements(
			String columnName, PreparedStatement preparedStatement1,
			PreparedStatement preparedStatement2)
		throws Exception {

		try (ResultSet resultSet = preparedStatement1.executeQuery()) {
			while (resultSet.next()) {
				JSONObject settingsJSONObject = _jsonFactory.createJSONObject(
					resultSet.getString("settings_"));

				if (!_upgradeSettings(settingsJSONObject)) {
					continue;
				}

				preparedStatement2.setString(1, settingsJSONObject.toString());
				preparedStatement2.setLong(
					2, resultSet.getLong("ctCollectionId"));
				preparedStatement2.setLong(3, resultSet.getLong(columnName));

				preparedStatement2.addBatch();
			}

			preparedStatement2.executeBatch();
		}
	}

	private boolean _upgradeSettings(JSONObject settingsJSONObject) {
		JSONArray fieldValuesJSONArray = settingsJSONObject.getJSONArray(
			"fieldValues");

		for (int i = 0; i < fieldValuesJSONArray.length(); i++) {
			JSONObject fieldValueJSONObject =
				fieldValuesJSONArray.getJSONObject(i);

			if (StringUtil.equals(
					fieldValueJSONObject.getString("name"),
					"autosaveEnabled")) {

				if (fieldValueJSONObject.getBoolean("value")) {
					fieldValueJSONObject.put("value", Boolean.FALSE.toString());

					return true;
				}

				return false;
			}
		}

		return false;
	}

	private final JSONFactory _jsonFactory;

}