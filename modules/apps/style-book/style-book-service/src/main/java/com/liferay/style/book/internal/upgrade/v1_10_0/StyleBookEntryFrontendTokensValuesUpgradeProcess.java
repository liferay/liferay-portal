/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.internal.upgrade.v1_10_0;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.style.book.constants.StyleBookConstants;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thiago Buarque
 */
public class StyleBookEntryFrontendTokensValuesUpgradeProcess
	extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		_doUpgrade("styleBookEntryId", "StyleBookEntry");
		_doUpgrade("styleBookEntryVersionId", "StyleBookEntryVersion");
	}

	private void _doUpgrade(String primaryKeyColumnName, String tableName)
		throws Exception {

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select ctCollectionId, ", primaryKeyColumnName,
					", frontendTokenDefinition, frontendTokensValues, themeId ",
					"from ", tableName,
					" where frontendTokensValues is not null"));
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					StringBundler.concat(
						"update ", tableName,
						" set frontendTokensValues = ? where ctCollectionId = ",
						"? and ", primaryKeyColumnName, " = ?"));
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			while (resultSet.next()) {
				String normalizedFrontendTokensValues = null;

				try {
					normalizedFrontendTokensValues =
						_normalizeFrontendTokensValues(
							resultSet.getString("frontendTokenDefinition"),
							resultSet.getString("frontendTokensValues"),
							resultSet.getString("themeId"));
				}
				catch (JSONException jsonException) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							StringBundler.concat(
								"Unable to parse frontend tokens values of ",
								tableName, " with primary key ",
								resultSet.getLong(primaryKeyColumnName)),
							jsonException);
					}

					continue;
				}

				preparedStatement2.setString(1, normalizedFrontendTokensValues);
				preparedStatement2.setLong(
					2, resultSet.getLong("ctCollectionId"));
				preparedStatement2.setLong(
					3, resultSet.getLong(primaryKeyColumnName));

				preparedStatement2.addBatch();
			}

			preparedStatement2.executeBatch();
		}
	}

	private List<String> _getCustomFrontendTokenNames(
		String customFrontendTokenDefinition) {

		List<String> customFrontendTokenNames = new ArrayList<>();

		if (Validator.isNull(customFrontendTokenDefinition)) {
			return customFrontendTokenNames;
		}

		JSONObject frontendTokenDefinitionJSONObject = null;

		try {
			frontendTokenDefinitionJSONObject =
				JSONFactoryUtil.createJSONObject(customFrontendTokenDefinition);
		}
		catch (JSONException jsonException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to parse frontend token definition", jsonException);
			}

			return customFrontendTokenNames;
		}

		JSONArray frontendTokenCategoriesJSONArray =
			frontendTokenDefinitionJSONObject.getJSONArray(
				"frontendTokenCategories");

		if (frontendTokenCategoriesJSONArray == null) {
			return customFrontendTokenNames;
		}

		for (int i = 0; i < frontendTokenCategoriesJSONArray.length(); i++) {
			JSONObject frontendTokenCategoryJSONObject =
				frontendTokenCategoriesJSONArray.getJSONObject(i);

			JSONArray frontendTokenSetsJSONArray =
				frontendTokenCategoryJSONObject.getJSONArray(
					"frontendTokenSets");

			if (frontendTokenSetsJSONArray == null) {
				continue;
			}

			for (int j = 0; j < frontendTokenSetsJSONArray.length(); j++) {
				JSONObject frontendTokenSetJSONObject =
					frontendTokenSetsJSONArray.getJSONObject(j);

				JSONArray frontendTokensJSONArray =
					frontendTokenSetJSONObject.getJSONArray("frontendTokens");

				if (frontendTokensJSONArray == null) {
					continue;
				}

				for (int k = 0; k < frontendTokensJSONArray.length(); k++) {
					JSONObject frontendTokenJSONObject =
						frontendTokensJSONArray.getJSONObject(k);

					customFrontendTokenNames.add(
						frontendTokenJSONObject.getString("name"));
				}
			}
		}

		return customFrontendTokenNames;
	}

	private String _getFrontendTokenDefinitionId(
		List<String> customFrontendTokenNames, String frontendTokenName,
		String themeId) {

		if (customFrontendTokenNames.contains(frontendTokenName)) {
			return StyleBookConstants.FRONTEND_TOKEN_DEFINITION_ID_CUSTOM;
		}

		return themeId;
	}

	private boolean _isNamespaced(String frontendTokenName) {
		if (frontendTokenName.indexOf(CharPool.COLON) == -1) {
			return false;
		}

		return true;
	}

	private String _normalizeFrontendTokensValues(
			String customFrontendTokenDefinition, String frontendTokensValues,
			String themeId)
		throws JSONException {

		if (Validator.isBlank(themeId)) {
			return frontendTokensValues;
		}

		JSONObject frontendTokensValuesJSONObject =
			JSONFactoryUtil.createJSONObject(frontendTokensValues);

		List<String> customFrontendTokenNames = _getCustomFrontendTokenNames(
			customFrontendTokenDefinition);

		for (String key :
				ListUtil.fromCollection(
					frontendTokensValuesJSONObject.keySet())) {

			JSONObject frontendTokenValueJSONObject =
				frontendTokensValuesJSONObject.getJSONObject(key);

			if (frontendTokenValueJSONObject == null) {
				continue;
			}

			if (!_isNamespaced(key)) {
				frontendTokensValuesJSONObject.remove(key);

				String tokenDefinitionId =
					frontendTokenValueJSONObject.getString("tokenDefinitionId");

				if (Validator.isNull(tokenDefinitionId)) {
					tokenDefinitionId = _getFrontendTokenDefinitionId(
						customFrontendTokenNames, key, themeId);
				}

				String namespacedKey = StringBundler.concat(
					tokenDefinitionId, StringPool.COLON, key);

				if (frontendTokensValuesJSONObject.has(namespacedKey)) {
					continue;
				}

				frontendTokenValueJSONObject.put(
					"tokenDefinitionId", tokenDefinitionId);

				frontendTokensValuesJSONObject.put(
					namespacedKey, frontendTokenValueJSONObject);
			}

			String name = frontendTokenValueJSONObject.getString("name");

			if (Validator.isNull(name) || _isNamespaced(name)) {
				continue;
			}

			frontendTokenValueJSONObject.put(
				"name",
				StringBundler.concat(
					_getFrontendTokenDefinitionId(
						customFrontendTokenNames, name, themeId),
					StringPool.COLON, name));
		}

		return frontendTokensValuesJSONObject.toString();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		StyleBookEntryFrontendTokensValuesUpgradeProcess.class);

}