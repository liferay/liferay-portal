/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.upgrade.v5_1_2;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Objects;

/**
 * @author Rodrigo Paulino
 */
public class DDMFormInstanceUpgradeProcess extends UpgradeProcess {

	public DDMFormInstanceUpgradeProcess(JSONFactory jsonFactory) {
		_jsonFactory = jsonFactory;
	}

	@Override
	protected void doUpgrade() throws Exception {
		StringBundler sb = new StringBundler(3);

		sb.append("select formInstanceId, ");

		boolean hasExpirationDate = hasColumn(
			"DDMFormInstance", "expirationDate");

		if (hasExpirationDate) {
			sb.append("expirationDate, ");
		}

		sb.append("settings_ from DDMFormInstance");

		try (PreparedStatement selectPreparedStatement1 =
				connection.prepareStatement(sb.toString());
			PreparedStatement selectPreparedStatement2 =
				connection.prepareStatement(
					"select formInstanceVersionId, settings_ from " +
						"DDMFormInstanceVersion where formInstanceId = ?");
			PreparedStatement updatePreparedStatement1 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update DDMFormInstance set settings_ = ? where " +
						"formInstanceId = ?");
			PreparedStatement updatePreparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update DDMFormInstanceVersion set settings_ = ? where " +
						"formInstanceVersionId = ?")) {

			_upgradeDDMFormInstance(
				hasExpirationDate, selectPreparedStatement1,
				selectPreparedStatement2, updatePreparedStatement1,
				updatePreparedStatement2);

			updatePreparedStatement1.executeBatch();

			updatePreparedStatement2.executeBatch();
		}
	}

	private Object _createFieldValue(String name, String value) {
		return HashMapBuilder.put(
			"fieldReference", name
		).put(
			"instanceId", StringUtil.randomString()
		).put(
			"name", name
		).put(
			"value", value
		).build();
	}

	private String _getString(Date expirationDate) {
		if (expirationDate == null) {
			return StringPool.BLANK;
		}

		return expirationDate.toString();
	}

	private String _getValue(boolean convertedPools, Date expirationDate) {
		if (convertedPools) {
			return Boolean.TRUE.toString();
		}

		return String.valueOf(expirationDate != null);
	}

	private void _upgradeDDMFormInstance(
			boolean hasExpirationDate,
			PreparedStatement selectPreparedStatement1,
			PreparedStatement selectPreparedStatement2,
			PreparedStatement updatePreparedStatement1,
			PreparedStatement updatePreparedStatement2)
		throws Exception {

		try (ResultSet resultSet = selectPreparedStatement1.executeQuery()) {
			while (resultSet.next()) {
				boolean convertedFromPolls = false;

				Date expirationDate = null;

				if (hasExpirationDate) {
					expirationDate = resultSet.getDate("expirationDate");
				}

				JSONObject settingsJSONObject = _jsonFactory.createJSONObject(
					resultSet.getString("settings_"));

				JSONArray fieldValuesJSONArray =
					settingsJSONObject.getJSONArray("fieldValues");

				for (int i = 0; i < fieldValuesJSONArray.length(); i++) {
					JSONObject fieldValueJSONObject =
						fieldValuesJSONArray.getJSONObject(i);

					if (Objects.equals(
							fieldValueJSONObject.getString("name"),
							"convertedFromPolls")) {

						convertedFromPolls = fieldValueJSONObject.getBoolean(
							"value");

						break;
					}
				}

				_upgradeSettings(
					convertedFromPolls, expirationDate, fieldValuesJSONArray);

				updatePreparedStatement1.setString(
					1, settingsJSONObject.toString());

				long formInstanceId = resultSet.getLong("formInstanceId");

				updatePreparedStatement1.setLong(2, formInstanceId);

				updatePreparedStatement1.addBatch();

				_upgradeDDMFormInstanceVersion(
					convertedFromPolls, expirationDate, formInstanceId,
					selectPreparedStatement2, updatePreparedStatement2);
			}
		}
	}

	private void _upgradeDDMFormInstanceVersion(
			boolean convertedFromPolls, Date expirationDate,
			long formInstanceId, PreparedStatement selectPreparedStatement,
			PreparedStatement updatePreparedStatement)
		throws Exception {

		selectPreparedStatement.setLong(1, formInstanceId);

		try (ResultSet resultSet = selectPreparedStatement.executeQuery()) {
			while (resultSet.next()) {
				String settings = resultSet.getString("settings_");

				if (Validator.isNull(settings)) {
					continue;
				}

				JSONObject settingsJSONObject = _jsonFactory.createJSONObject(
					settings);

				_upgradeSettings(
					convertedFromPolls, expirationDate,
					settingsJSONObject.getJSONArray("fieldValues"));

				updatePreparedStatement.setString(
					1, settingsJSONObject.toString());

				updatePreparedStatement.setLong(
					2, resultSet.getLong("formInstanceVersionId"));

				updatePreparedStatement.addBatch();
			}
		}
	}

	private void _upgradeSettings(
		boolean convertedFromPolls, Date expirationDate,
		JSONArray fieldValuesJSONArray) {

		boolean hasExpirationDate = false;
		boolean hasLimitToOneSubmissionPerUser = false;
		boolean hasNeverExpire = false;
		boolean hasShowPartialResultsToRespondents = false;

		for (int i = 0; i < fieldValuesJSONArray.length(); i++) {
			JSONObject fieldValueJSONObject =
				fieldValuesJSONArray.getJSONObject(i);

			if (Objects.equals(
					fieldValueJSONObject.getString("name"), "expirationDate")) {

				fieldValueJSONObject.put("value", _getString(expirationDate));

				hasExpirationDate = true;
			}
			else if (Objects.equals(
						fieldValueJSONObject.getString("name"),
						"limitToOneSubmissionPerUser")) {

				fieldValueJSONObject.put(
					"value", _getValue(convertedFromPolls, expirationDate));

				hasLimitToOneSubmissionPerUser = true;
			}
			else if (Objects.equals(
						fieldValueJSONObject.getString("name"),
						"neverExpire")) {

				fieldValueJSONObject.put(
					"value", String.valueOf(expirationDate == null));

				hasNeverExpire = true;
			}
			else if (Objects.equals(
						fieldValueJSONObject.getString("name"),
						"showPartialResultsToRespondents")) {

				fieldValueJSONObject.put(
					"value", _getValue(convertedFromPolls, expirationDate));

				hasShowPartialResultsToRespondents = true;
			}
			else if (convertedFromPolls &&
					 Objects.equals(
						 fieldValueJSONObject.getString("name"), "published")) {

				fieldValueJSONObject.put("value", Boolean.TRUE.toString());
			}
		}

		if (!hasExpirationDate) {
			fieldValuesJSONArray.put(
				_createFieldValue(
					"expirationDate", _getString(expirationDate)));
		}

		if (!hasLimitToOneSubmissionPerUser) {
			fieldValuesJSONArray.put(
				_createFieldValue(
					"limitToOneSubmissionPerUser",
					_getValue(convertedFromPolls, expirationDate)));
		}

		if (!hasNeverExpire) {
			fieldValuesJSONArray.put(
				_createFieldValue(
					"neverExpire", String.valueOf(expirationDate == null)));
		}

		if (!hasShowPartialResultsToRespondents) {
			fieldValuesJSONArray.put(
				_createFieldValue(
					"showPartialResultsToRespondents",
					_getValue(convertedFromPolls, expirationDate)));
		}
	}

	private final JSONFactory _jsonFactory;

}