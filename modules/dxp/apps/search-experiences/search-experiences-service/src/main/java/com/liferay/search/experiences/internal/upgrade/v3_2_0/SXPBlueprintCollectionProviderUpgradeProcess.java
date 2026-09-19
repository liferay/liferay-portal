/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.internal.upgrade.v3_2_0;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.Validator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Joshua Cords
 */
public class SXPBlueprintCollectionProviderUpgradeProcess
	extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		CompanyLocalServiceUtil.forEachCompanyId(
			companyId -> _upgradeSXPBlueprints(companyId));

		_upgradeSXPBlueprintSchemaVersion();
	}

	private String _updateConfigurationStorage(
		String configurationJSON, long blueprintId) {

		if (Validator.isBlank(configurationJSON)) {
			return configurationJSON;
		}

		try {
			JSONObject configurationJSONObject =
				JSONFactoryUtil.createJSONObject(configurationJSON);

			JSONObject generalConfigurationJSONObject =
				configurationJSONObject.getJSONObject("generalConfiguration");

			if (generalConfigurationJSONObject == null) {
				return configurationJSON;
			}

			generalConfigurationJSONObject.put(
				"collectionProvider", true
			).put(
				"collectionProviderType", AssetEntry.class.getName()
			).put(
				"legacyAssetCollectionProvider", true
			);

			return configurationJSONObject.toString();
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to upgrade blueprint " + blueprintId, exception);
			}

			return configurationJSON;
		}
	}

	private void _upgradeSXPBlueprintSchemaVersion() throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"update SXPBlueprint set schemaVersion = ? where " +
					"schemaVersion <> ?")) {

			preparedStatement.setString(1, _SCHEMA_VERSION);
			preparedStatement.setString(2, _SCHEMA_VERSION);

			preparedStatement.executeUpdate();
		}
	}

	private void _upgradeSXPBlueprints(long companyId) throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select configurationJSON, sxpBlueprintId from SXPBlueprint " +
					"where companyId = ?");
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update SXPBlueprint set configurationJSON = ?, " +
						"schemaVersion = ? where sxpBlueprintId = ?")) {

			preparedStatement1.setLong(1, companyId);

			try (ResultSet resultSet1 = preparedStatement1.executeQuery()) {
				while (resultSet1.next()) {
					long blueprintId = resultSet1.getLong("sxpBlueprintId");

					preparedStatement2.setString(
						1,
						_updateConfigurationStorage(
							resultSet1.getString("configurationJSON"),
							blueprintId));

					preparedStatement2.setString(2, _SCHEMA_VERSION);
					preparedStatement2.setLong(3, blueprintId);

					preparedStatement2.addBatch();
				}

				preparedStatement2.executeBatch();
			}
		}
	}

	private static final String _SCHEMA_VERSION = "1.2";

	private static final Log _log = LogFactoryUtil.getLog(
		SXPBlueprintCollectionProviderUpgradeProcess.class);

}