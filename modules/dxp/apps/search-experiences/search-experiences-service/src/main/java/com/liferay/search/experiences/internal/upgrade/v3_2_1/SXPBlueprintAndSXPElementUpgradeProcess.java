/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.internal.upgrade.v3_2_1;

import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.search.experiences.internal.util.RangeQueryUpgradeUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Rodrigo Guedes de Souza
 */
public class SXPBlueprintAndSXPElementUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		_upgradeSXPBlueprints();
		_upgradeSXPElements();
	}

	private JSONArray _createJSONArray(String json) throws JSONException {
		if (Validator.isBlank(json)) {
			return JSONFactoryUtil.createJSONArray();
		}

		return JSONFactoryUtil.createJSONArray(json);
	}

	private JSONObject _createJSONObject(String json) throws JSONException {
		if (Validator.isBlank(json)) {
			return JSONFactoryUtil.createJSONObject();
		}

		return JSONFactoryUtil.createJSONObject(json);
	}

	private void _upgradeSXPBlueprint(
			PreparedStatement preparedStatement, ResultSet resultSet)
		throws SQLException {

		long sxpBlueprintId = resultSet.getLong("sxpBlueprintId");

		try {
			JSONObject configurationJSONObject = _createJSONObject(
				resultSet.getString("configurationJSON"));

			boolean upgradedConfiguration =
				RangeQueryUpgradeUtil.upgradeRangeQueries(
					configurationJSONObject);

			JSONArray elementInstancesJSONArray = _createJSONArray(
				resultSet.getString("elementInstancesJSON"));

			boolean upgradedElementInstances =
				RangeQueryUpgradeUtil.upgradeRangeQueries(
					elementInstancesJSONArray);

			if (!upgradedConfiguration && !upgradedElementInstances) {
				return;
			}

			preparedStatement.setString(1, configurationJSONObject.toString());
			preparedStatement.setString(
				2, elementInstancesJSONArray.toString());
			preparedStatement.setLong(3, sxpBlueprintId);

			preparedStatement.addBatch();
		}
		catch (JSONException jsonException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to upgrade blueprint " + sxpBlueprintId,
					jsonException);
			}
		}
	}

	private void _upgradeSXPBlueprints() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select sxpBlueprintId, configurationJSON, " +
					"elementInstancesJSON from SXPBlueprint");
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update SXPBlueprint set configurationJSON = ?, " +
						"elementInstancesJSON = ? where sxpBlueprintId = ?")) {

			try (ResultSet resultSet = preparedStatement1.executeQuery()) {
				while (resultSet.next()) {
					_upgradeSXPBlueprint(preparedStatement2, resultSet);
				}

				preparedStatement2.executeBatch();
			}
		}
	}

	private void _upgradeSXPElement(
			PreparedStatement preparedStatement, ResultSet resultSet)
		throws SQLException {

		long sxpElementId = resultSet.getLong("sxpElementId");

		try {
			JSONObject elementDefinitionJSONObject = _createJSONObject(
				resultSet.getString("elementDefinitionJSON"));

			if (!RangeQueryUpgradeUtil.upgradeRangeQueries(
					elementDefinitionJSONObject)) {

				return;
			}

			preparedStatement.setString(
				1, elementDefinitionJSONObject.toString());
			preparedStatement.setLong(2, sxpElementId);

			preparedStatement.addBatch();
		}
		catch (JSONException jsonException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to upgrade element " + sxpElementId, jsonException);
			}
		}
	}

	private void _upgradeSXPElements() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select sxpElementId, elementDefinitionJSON from SXPElement");
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update SXPElement set elementDefinitionJSON = ? where " +
						"sxpElementId = ?")) {

			try (ResultSet resultSet = preparedStatement1.executeQuery()) {
				while (resultSet.next()) {
					_upgradeSXPElement(preparedStatement2, resultSet);
				}

				preparedStatement2.executeBatch();
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SXPBlueprintAndSXPElementUpgradeProcess.class);

}