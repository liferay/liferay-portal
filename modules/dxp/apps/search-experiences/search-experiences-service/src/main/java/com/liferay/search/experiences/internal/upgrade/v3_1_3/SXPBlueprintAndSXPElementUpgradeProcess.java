/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.internal.upgrade.v3_1_3;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Objects;

/**
 * @author Joshua Cords
 * @author Felipe Lorenz
 * @author Selena Aungst
 */
public class SXPBlueprintAndSXPElementUpgradeProcess extends UpgradeProcess {

	public SXPBlueprintAndSXPElementUpgradeProcess(
		GroupLocalService groupLocalService, JSONFactory jsonFactory) {

		_groupLocalService = groupLocalService;
		_jsonFactory = jsonFactory;
	}

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select sxpBlueprintId, elementInstancesJSON from " +
					"SXPBlueprint");
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update SXPBlueprint set elementInstancesJSON = ? where " +
						"sxpBlueprintId = ?")) {

			try (ResultSet resultSet = preparedStatement1.executeQuery()) {
				while (resultSet.next()) {
					_upgradeSXPBlueprint(preparedStatement2, resultSet);
				}

				preparedStatement2.executeBatch();
			}
		}

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"update SXPElement set elementDefinitionJSON = ? where " +
					"externalReferenceCode = 'LIMIT_SEARCH_TO_THESE_SITES'")) {

			preparedStatement.setString(
				1,
				StringUtil.read(
					getClass(),
					"dependencies/limit_search_to_these_sites.json"));

			preparedStatement.executeUpdate();
		}

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

	private String _fixElementInstancesJSON(JSONArray elementInstanceJSONArray)
		throws Exception {

		for (int i = 0; i < elementInstanceJSONArray.length(); i++) {
			JSONObject elementInstanceJSONObject =
				elementInstanceJSONArray.getJSONObject(i);

			JSONObject sxpElementJSONObject =
				elementInstanceJSONObject.getJSONObject("sxpElement");

			if (sxpElementJSONObject == null) {
				continue;
			}

			String externalReferenceCode = sxpElementJSONObject.getString(
				"externalReferenceCode");

			if (!Objects.equals(
					externalReferenceCode, "LIMIT_SEARCH_TO_THESE_SITES")) {

				continue;
			}

			_upgradeConfigurationEntry(elementInstanceJSONObject);
			_upgradeElementDefinition(sxpElementJSONObject);
			_upgradeUIConfigurationValues(elementInstanceJSONObject);
		}

		return elementInstanceJSONArray.toString();
	}

	private boolean _hasLimitSearchToTheseSites(
		JSONArray elementInstanceJSONArray) {

		for (int i = 0; i < elementInstanceJSONArray.length(); i++) {
			JSONObject elementInstanceJSONObject =
				elementInstanceJSONArray.getJSONObject(i);

			JSONObject sxpElementJSONObject =
				elementInstanceJSONObject.getJSONObject("sxpElement");

			if (sxpElementJSONObject == null) {
				continue;
			}

			if (Objects.equals(
					sxpElementJSONObject.getString("externalReferenceCode"),
					"LIMIT_SEARCH_TO_THESE_SITES")) {

				return true;
			}
		}

		return false;
	}

	private boolean _isLegacyMultiselectFieldMappings(
		JSONArray fieldMappingsJSONArray) {

		boolean legacyMultiselect = false;

		for (int i = 0; i < fieldMappingsJSONArray.length(); i++) {
			JSONObject fieldMappingJSONObject =
				fieldMappingsJSONArray.getJSONObject(i);

			if (fieldMappingJSONObject == null) {
				continue;
			}

			if (fieldMappingJSONObject.has("field")) {
				return false;
			}

			if (!fieldMappingJSONObject.has("label") &&
				!fieldMappingJSONObject.has("value")) {

				continue;
			}

			legacyMultiselect = true;
		}

		return legacyMultiselect;
	}

	private void _upgradeConfigurationEntry(
			JSONObject elementInstanceJSONObject)
		throws Exception {

		JSONObject queryJSONObject = JSONUtil.getValueAsJSONObject(
			elementInstanceJSONObject, "JSONObject/configurationEntry",
			"JSONObject/queryConfiguration", "JSONArray/queryEntries",
			"JSONObject/0", "JSONArray/clauses", "JSONObject/0",
			"JSONObject/query");

		JSONObject termsJSONObject = queryJSONObject.getJSONObject("terms");

		long[] groupIds = JSONUtil.toLongArray(
			JSONUtil.getValueAsJSONArray(
				termsJSONObject, "JSONArray/scopeGroupId"));

		queryJSONObject.put(
			"terms",
			JSONUtil.put(
				"scopeGroupExternalReferenceCode",
				() -> {
					JSONArray jsonArray = _jsonFactory.createJSONArray();

					for (long groupId : groupIds) {
						Group group = _groupLocalService.getGroup(groupId);

						jsonArray.put(group.getExternalReferenceCode());
					}

					return jsonArray;
				}));
	}

	private void _upgradeElementDefinition(JSONObject sxpElementJSONObject) {
		JSONObject elementDefinitionJSONObject =
			sxpElementJSONObject.getJSONObject("elementDefinition");

		JSONUtil.getValueAsJSONObject(
			elementDefinitionJSONObject, "JSONObject/configuration",
			"JSONObject/queryConfiguration", "JSONArray/queryEntries",
			"JSONObject/0", "JSONArray/clauses", "JSONObject/0",
			"JSONObject/query"
		).getJSONObject(
			"terms"
		).put(
			"scopeGroupExternalReferenceCode",
			"${configuration.scope_group_external_reference_codes}"
		).remove(
			"scopeGroupId"
		);

		JSONObject uiConfigurationJSONObject =
			elementDefinitionJSONObject.getJSONObject("uiConfiguration");

		JSONArray fieldsJSONArray = JSONUtil.getValueAsJSONArray(
			uiConfigurationJSONObject, "JSONArray/fieldSets", "JSONObject/0",
			"JSONArray/fields");

		for (int i = 0; i < fieldsJSONArray.length(); i++) {
			JSONObject fieldJSONObject = fieldsJSONArray.getJSONObject(i);

			fieldJSONObject.put(
				"helpText", "scope-group-external-reference-codes-help"
			).put(
				"label", "scope-group-external-reference-codes"
			).put(
				"name", "scope_group_external_reference_codes"
			).remove(
				"helpTextLocalized"
			);

			fieldJSONObject.remove("labelLocalized");
		}
	}

	private boolean _upgradeLegacyFieldMappings(
		JSONArray elementInstanceJSONArray) {

		boolean modified = false;

		for (int i = 0; i < elementInstanceJSONArray.length(); i++) {
			JSONObject elementDefinitionJSONObject =
				JSONUtil.getValueAsJSONObject(
					elementInstanceJSONArray.getJSONObject(i),
					"JSONObject/sxpElement", "JSONObject/elementDefinition");

			if ((elementDefinitionJSONObject == null) ||
				!_upgradeLegacyFieldMappings(elementDefinitionJSONObject)) {

				continue;
			}

			modified = true;
		}

		return modified;
	}

	private boolean _upgradeLegacyFieldMappings(
		JSONObject elementDefinitionJSONObject) {

		JSONArray fieldSetsJSONArray = JSONUtil.getValueAsJSONArray(
			elementDefinitionJSONObject, "JSONObject/uiConfiguration",
			"JSONArray/fieldSets");

		if (fieldSetsJSONArray == null) {
			return false;
		}

		boolean modified = false;

		for (int i = 0; i < fieldSetsJSONArray.length(); i++) {
			JSONObject fieldSetJSONObject = fieldSetsJSONArray.getJSONObject(i);

			if (fieldSetJSONObject == null) {
				continue;
			}

			JSONArray fieldsJSONArray = fieldSetJSONObject.getJSONArray(
				"fields");

			if (fieldsJSONArray == null) {
				continue;
			}

			for (int j = 0; j < fieldsJSONArray.length(); j++) {
				JSONObject fieldJSONObject = fieldsJSONArray.getJSONObject(j);

				if (fieldJSONObject == null) {
					continue;
				}

				JSONArray fieldMappingsJSONArray = fieldJSONObject.getJSONArray(
					"fieldMappings");

				if (fieldMappingsJSONArray == null) {
					continue;
				}

				if (_isLegacyMultiselectFieldMappings(fieldMappingsJSONArray)) {
					fieldJSONObject.put(
						"defaultValue", fieldMappingsJSONArray
					).remove(
						"fieldMappings"
					);

					modified = true;

					continue;
				}

				for (int k = 0; k < fieldMappingsJSONArray.length(); k++) {
					JSONObject fieldMappingJSONObject =
						fieldMappingsJSONArray.getJSONObject(k);

					if ((fieldMappingJSONObject == null) ||
						!fieldMappingJSONObject.has("label")) {

						continue;
					}

					fieldMappingJSONObject.remove("label");

					modified = true;
				}
			}
		}

		return modified;
	}

	private void _upgradeSXPBlueprint(
			PreparedStatement preparedStatement2, ResultSet resultSet)
		throws SQLException {

		String elementInstancesJSON = resultSet.getString(
			"elementInstancesJSON");

		if (Validator.isBlank(elementInstancesJSON)) {
			return;
		}

		long sxpBlueprintId = resultSet.getLong("sxpBlueprintId");

		try {
			JSONArray elementInstanceJSONArray = _jsonFactory.createJSONArray(
				elementInstancesJSON);

			boolean modified = _upgradeLegacyFieldMappings(
				elementInstanceJSONArray);

			if (_hasLimitSearchToTheseSites(elementInstanceJSONArray)) {
				elementInstancesJSON = _fixElementInstancesJSON(
					elementInstanceJSONArray);
				modified = true;
			}
			else if (modified) {
				elementInstancesJSON = elementInstanceJSONArray.toString();
			}

			if (!modified) {
				return;
			}

			preparedStatement2.setString(1, elementInstancesJSON);
			preparedStatement2.setLong(2, sxpBlueprintId);

			preparedStatement2.addBatch();
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to upgrade search experiences blueprint " +
						sxpBlueprintId,
					exception);
			}
		}
	}

	private void _upgradeSXPElement(
			PreparedStatement preparedStatement2, ResultSet resultSet)
		throws SQLException {

		String elementDefinitionJSON = resultSet.getString(
			"elementDefinitionJSON");

		if (Validator.isBlank(elementDefinitionJSON)) {
			return;
		}

		long sxpElementId = resultSet.getLong("sxpElementId");

		try {
			JSONObject elementDefinitionJSONObject =
				_jsonFactory.createJSONObject(elementDefinitionJSON);

			if (!_upgradeLegacyFieldMappings(elementDefinitionJSONObject)) {
				return;
			}

			preparedStatement2.setString(
				1, elementDefinitionJSONObject.toString());
			preparedStatement2.setLong(2, sxpElementId);

			preparedStatement2.addBatch();
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to upgrade search experiences element " +
						sxpElementId,
					exception);
			}
		}
	}

	private void _upgradeUIConfigurationValues(
			JSONObject elementInstanceJSONObject)
		throws Exception {

		JSONObject uiConfigurationValuesJSONObject =
			elementInstanceJSONObject.getJSONObject("uiConfigurationValues");

		JSONArray scopeGroupIdsJSONArray =
			uiConfigurationValuesJSONObject.getJSONArray("scope_group_ids");

		if (scopeGroupIdsJSONArray == null) {
			return;
		}

		JSONArray scopeGroupExternalReferenceCodesJSONArray =
			_jsonFactory.createJSONArray();

		for (int i = 0; i < scopeGroupIdsJSONArray.length(); i++) {
			JSONObject scopeGroupIdJSONObject =
				scopeGroupIdsJSONArray.getJSONObject(i);

			Group group = _groupLocalService.getGroup(
				scopeGroupIdJSONObject.getLong("value"));

			scopeGroupExternalReferenceCodesJSONArray.put(
				JSONUtil.put(
					"label",
					StringBundler.concat(
						group.getDescriptiveName(), " (ERC: ",
						group.getExternalReferenceCode(), ")")
				).put(
					"value", group.getExternalReferenceCode()
				));
		}

		uiConfigurationValuesJSONObject.put(
			"scope_group_external_reference_codes",
			scopeGroupExternalReferenceCodesJSONArray
		).remove(
			"scope_group_ids"
		);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SXPBlueprintAndSXPElementUpgradeProcess.class);

	private final GroupLocalService _groupLocalService;
	private final JSONFactory _jsonFactory;

}