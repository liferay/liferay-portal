/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.upgrade.v1_1_1;

import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.metadata.RawMetadataProcessor;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Lino Alves
 */
public class DDMStructureIndexTypeUpgradeProcess extends UpgradeProcess {

	public DDMStructureIndexTypeUpgradeProcess(JSONFactory jsonFactory) {
		_jsonFactory = jsonFactory;
	}

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select DDMStructure.definition, DDMStructure.structureId " +
					"from DDMStructure where structureKey = ? ");
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update DDMStructure set definition = ? where " +
						"structureId = ?");
			PreparedStatement preparedStatement3 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update DDMStructureVersion set definition = ? where " +
						"structureId = ?")) {

			preparedStatement1.setString(
				1, RawMetadataProcessor.TIKA_RAW_METADATA);

			try (ResultSet resultSet = preparedStatement1.executeQuery()) {
				while (resultSet.next()) {
					String newDefinition = _upgradeIndexType(
						resultSet.getString(1));

					preparedStatement2.setString(1, newDefinition);

					preparedStatement2.setLong(2, resultSet.getLong(2));

					preparedStatement2.addBatch();

					preparedStatement3.setString(1, newDefinition);
					preparedStatement3.setLong(2, resultSet.getLong(2));

					preparedStatement3.addBatch();
				}

				preparedStatement2.executeBatch();

				preparedStatement3.executeBatch();
			}
		}
	}

	private String _upgradeIndexType(String definition) throws PortalException {
		try {
			JSONObject definitionJSONObject = _jsonFactory.createJSONObject(
				definition);

			JSONArray fieldsJSONArray = definitionJSONObject.getJSONArray(
				"fields");

			for (int i = 0; i < fieldsJSONArray.length(); i++) {
				JSONObject jsonObject = fieldsJSONArray.getJSONObject(i);

				if (!jsonObject.has("indexType")) {
					jsonObject.put("indexType", "text");
				}
			}

			return definitionJSONObject.toString();
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}

			return definition;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDMStructureIndexTypeUpgradeProcess.class);

	private final JSONFactory _jsonFactory;

}