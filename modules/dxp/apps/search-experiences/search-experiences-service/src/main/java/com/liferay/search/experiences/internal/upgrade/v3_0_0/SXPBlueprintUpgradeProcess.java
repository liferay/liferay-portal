/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.internal.upgrade.v3_0_0;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Objects;

/**
 * @author Gustavo Lima
 */
public class SXPBlueprintUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		_upgradeLowLevelSearchOptionsPortlets();
		_upgradeSearchBarPortlets();
		_upgradeSXPBlueprintOptionsPortlets();
	}

	private long _getSXPBlueprintIdByLargeValue(String largeValue)
		throws Exception {

		if (Validator.isNull(largeValue)) {
			return 0;
		}

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray(
			StringBundler.concat(
				StringPool.OPEN_BRACKET, largeValue, StringPool.CLOSE_BRACKET));

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			if (jsonObject == null) {
				continue;
			}

			JSONObject attributesJSONObject = jsonObject.getJSONObject(
				"attributes");

			if ((attributesJSONObject != null) &&
				attributesJSONObject.has("sxpBlueprintId")) {

				return attributesJSONObject.getLong("sxpBlueprintId");
			}
		}

		return 0;
	}

	private long _getSXPBlueprintIdBySmallValue(String smallValue)
		throws Exception {

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray(smallValue);

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			if ((jsonObject != null) &&
				Objects.equals(
					jsonObject.getString("key"),
					"search.experiences.blueprint.id")) {

				return jsonObject.getLong("value");
			}
		}

		return 0;
	}

	private String _updateSmallValueJSON(
			String externalReferenceCode, String smallValue)
		throws Exception {

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray(smallValue);

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			if ((jsonObject != null) &&
				Objects.equals(
					jsonObject.getString("key"),
					"search.experiences.blueprint.id")) {

				jsonObject.put(
					"key",
					"search.experiences.blueprint.external.reference.code"
				).put(
					"value", externalReferenceCode
				);
			}
		}

		return jsonArray.toString();
	}

	private void _upgradeLowLevelSearchOptionsPortlets() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select PortletPreferenceValue.smallValue, ",
					"PortletPreferenceValue.portletPreferencesId from ",
					"PortletPreferenceValue inner join PortletPreferences on ",
					"PortletPreferences.portletPreferencesId  = ",
					"PortletPreferenceValue.portletPreferencesId where ",
					"PortletPreferences.portletId like ",
					"'%com_liferay_portal_search_web_low_level_search_options_",
					"portlet_LowLevelSearchOptionsPortlet_INSTANCE%' and ",
					"PortletPreferenceValue.name = 'attributes'"));

			ResultSet resultSet1 = preparedStatement1.executeQuery();

			PreparedStatement preparedStatement2 = connection.prepareStatement(
				"select externalReferenceCode from SXPBlueprint where " +
					"sxpBlueprintId = ?");
			PreparedStatement preparedStatement3 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update PortletPreferenceValue set smallValue = ? where " +
						"portletPreferencesId = ? and name = 'attributes'")) {

			while (resultSet1.next()) {
				String smallValue = resultSet1.getString("smallValue");

				preparedStatement2.setLong(
					1, _getSXPBlueprintIdBySmallValue(smallValue));

				ResultSet resultSet2 = preparedStatement2.executeQuery();

				if (!resultSet2.next()) {
					continue;
				}

				String newSmallValue = _updateSmallValueJSON(
					resultSet2.getString("externalReferenceCode"), smallValue);

				preparedStatement3.setString(1, newSmallValue);

				preparedStatement3.setLong(
					2, resultSet1.getLong("portletPreferencesId"));

				preparedStatement3.addBatch();
			}

			preparedStatement3.executeBatch();
		}
	}

	private void _upgradeSXPBlueprintOptionsPortlets() {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select PortletPreferenceValue.smallValue, ",
					"PortletPreferenceValue.portletPreferencesId from ",
					"PortletPreferenceValue inner join PortletPreferences on ",
					"PortletPreferences.portletPreferencesId  = ",
					"PortletPreferenceValue.portletPreferencesId where ",
					"PortletPreferences.portletId like ",
					"'%com_liferay_search_experiences_web_internal_blueprint_",
					"options_portlet_SXPBlueprintOptionsPortlet_INSTANCE_%' ",
					"and PortletPreferenceValue.name = 'sxpBlueprintId'"));

			ResultSet resultSet1 = preparedStatement1.executeQuery();

			PreparedStatement preparedStatement2 = connection.prepareStatement(
				"select externalReferenceCode from SXPBlueprint where " +
					"sxpBlueprintId = ?");
			PreparedStatement preparedStatement3 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update PortletPreferenceValue set name = ?, smallValue " +
						"= ? where portletPreferencesId = ? and name = " +
							"'sxpBlueprintId'")) {

			while (resultSet1.next()) {
				preparedStatement2.setLong(1, resultSet1.getLong("smallValue"));

				ResultSet resultSet2 = preparedStatement2.executeQuery();

				if (!resultSet2.next()) {
					continue;
				}

				preparedStatement3.setString(
					1, "sxpBlueprintExternalReferenceCode");
				preparedStatement3.setString(
					2, resultSet2.getString("externalReferenceCode"));
				preparedStatement3.setLong(
					3, resultSet1.getLong("portletPreferencesId"));

				preparedStatement3.addBatch();
			}

			preparedStatement3.executeBatch();
		}
		catch (SQLException sqlException) {
			throw new RuntimeException(sqlException);
		}
	}

	private void _upgradeSearchBarPortlets() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select PortletPreferenceValue.largeValue, ",
					"PortletPreferenceValue.portletPreferencesId from ",
					"PortletPreferenceValue inner join PortletPreferences on ",
					"PortletPreferences.portletPreferencesId  = ",
					"PortletPreferenceValue.portletPreferencesId where ",
					"PortletPreferences.portletId like ",
					"'%com_liferay_portal_search_web_search_bar_portlet_",
					"SearchBarPortlet_INSTANCE_%' and ",
					"PortletPreferenceValue.name = ",
					"'suggestionsContributorConfigurations'"));

			ResultSet resultSet1 = preparedStatement1.executeQuery();

			PreparedStatement preparedStatement2 = connection.prepareStatement(
				"select externalReferenceCode from SXPBlueprint where " +
					"sxpBlueprintId = ?");
			PreparedStatement preparedStatement3 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update PortletPreferenceValue set largeValue = ? where " +
						"portletPreferencesId = ? and name = " +
							"'suggestionsContributorConfigurations'")) {

			while (resultSet1.next()) {
				String largeValue = resultSet1.getString("largeValue");

				preparedStatement2.setLong(
					1, _getSXPBlueprintIdByLargeValue(largeValue));

				ResultSet resultSet2 = preparedStatement2.executeQuery();

				if (!resultSet2.next()) {
					continue;
				}

				String newLargeValue = StringUtil.replace(
					largeValue,
					StringBundler.concat(
						StringUtil.quote("sxpBlueprintId", "\""), ":",
						_getSXPBlueprintIdByLargeValue(largeValue)),
					StringBundler.concat(
						StringUtil.quote(
							"sxpBlueprintExternalReferenceCode", "\""),
						":",
						StringUtil.quote(
							resultSet2.getString("externalReferenceCode"),
							"\"")));

				preparedStatement3.setString(1, newLargeValue);

				preparedStatement3.setLong(
					2, resultSet1.getLong("portletPreferencesId"));

				preparedStatement3.addBatch();
			}

			preparedStatement3.executeBatch();
		}
	}

}