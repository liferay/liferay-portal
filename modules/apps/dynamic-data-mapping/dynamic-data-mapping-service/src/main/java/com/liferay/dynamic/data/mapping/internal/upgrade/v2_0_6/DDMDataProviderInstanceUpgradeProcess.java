/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.upgrade.v2_0_6;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.StringUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Rodrigo Paulino
 */
public class DDMDataProviderInstanceUpgradeProcess extends UpgradeProcess {

	public DDMDataProviderInstanceUpgradeProcess(JSONFactory jsonFactory) {
		_jsonFactory = jsonFactory;
	}

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select * from DDMDataProviderInstance");
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update DDMDataProviderInstance set definition = ? where " +
						"dataProviderInstanceId = ?");
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			while (resultSet.next()) {
				preparedStatement2.setString(
					1,
					_updateDDMDataProviderInstance(
						resultSet.getLong("dataProviderInstanceId"),
						resultSet.getString("definition"),
						resultSet.getString("uuid_")));
				preparedStatement2.setLong(
					2, resultSet.getLong("dataProviderInstanceId"));

				preparedStatement2.addBatch();
			}

			preparedStatement2.executeBatch();

			_updateDDMStructures();
		}
	}

	private long _extractDDMDataProviderInstanceId(String json) {
		try {
			JSONArray jsonArray = _jsonFactory.createJSONArray(json);

			return jsonArray.getLong(0);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			return 0;
		}
	}

	private String _extractDDMDataProviderInstanceOutputName(String json) {
		try {
			JSONArray jsonArray = _jsonFactory.createJSONArray(json);

			return jsonArray.getString(0);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			return StringPool.BLANK;
		}
	}

	private String _updateDDMDataProviderInstance(
			long ddmDataProviderInstanceId, String definition, String uuid)
		throws JSONException {

		String instanceId = StringUtil.randomString();

		JSONObject definitionJSONObject = _jsonFactory.createJSONObject(
			definition);

		JSONArray fieldValuesJSONArray = definitionJSONObject.getJSONArray(
			"fieldValues");

		for (int i = 0; i < fieldValuesJSONArray.length(); i++) {
			JSONObject fieldValueJSONObject =
				fieldValuesJSONArray.getJSONObject(i);

			if (!StringUtil.equals(
					fieldValueJSONObject.getString("name"),
					"outputParameters")) {

				continue;
			}

			String outputParameterId = StringUtil.randomString();

			JSONArray nestedFieldValuesJSONArray =
				fieldValueJSONObject.getJSONArray("nestedFieldValues");

			_updateDDMDataProviderInstanceOutputParameters(
				ddmDataProviderInstanceId, nestedFieldValuesJSONArray,
				outputParameterId, uuid);

			nestedFieldValuesJSONArray.put(
				JSONUtil.put(
					"instanceId", instanceId
				).put(
					"name", "outputParameterId"
				).put(
					"value", outputParameterId
				));
		}

		return definitionJSONObject.toString();
	}

	private void _updateDDMDataProviderInstanceOutputParameters(
		long ddmDataProviderInstanceId, JSONArray fieldValuesJSONArray,
		String outputParameterId, String uuid) {

		Map<String, String> ddmDataProviderInstanceOutputParameterValues =
			_ddmDataProviderInstanceOutputParametersInstanceId.get(
				ddmDataProviderInstanceId);

		if (ddmDataProviderInstanceOutputParameterValues == null) {
			ddmDataProviderInstanceOutputParameterValues = new HashMap<>();

			_ddmDataProviderInstanceOutputParametersInstanceId.put(
				ddmDataProviderInstanceId,
				ddmDataProviderInstanceOutputParameterValues);

			_ddmDataProviderInstanceOutputParametersUUID.put(
				uuid, ddmDataProviderInstanceOutputParameterValues);
		}

		Map<String, JSONObject> outputParameters = JSONUtil.toJSONObjectMap(
			fieldValuesJSONArray, "name");

		JSONObject jsonObject = outputParameters.get("outputParameterName");

		if (jsonObject != null) {
			ddmDataProviderInstanceOutputParameterValues.put(
				jsonObject.getString("value"), outputParameterId);
		}
	}

	private boolean _updateDDMDataProviderRules(JSONArray rulesJSONArray) {
		boolean updated = false;

		for (int i = 0; i < rulesJSONArray.length(); i++) {
			JSONObject ruleJSONObject = rulesJSONArray.getJSONObject(i);

			JSONArray newActionsJSONArray = _jsonFactory.createJSONArray();

			List<String> actions = JSONUtil.toStringList(
				ruleJSONObject.getJSONArray("actions"));

			for (String action : actions) {
				if (!action.startsWith("call")) {
					newActionsJSONArray.put(action);

					continue;
				}

				String[] arguments = StringUtil.split(action, "', '");

				String uuid = arguments[0].substring(6);

				Map<String, String> ddmDataProviderOutputParameters =
					_ddmDataProviderInstanceOutputParametersUUID.get(uuid);

				String actionOutputsString = arguments[2].substring(
					0, arguments[2].length() - 2);

				String[] actionOutputs = StringUtil.split(
					actionOutputsString, CharPool.SEMICOLON);

				String newActionOutputsString = "";

				for (String actionOutput : actionOutputs) {
					String[] actionOutputParts = StringUtil.split(
						actionOutput, CharPool.EQUAL);

					newActionOutputsString = StringBundler.concat(
						newActionOutputsString, actionOutputParts[0],
						CharPool.EQUAL,
						ddmDataProviderOutputParameters.get(
							actionOutputParts[1]),
						CharPool.SEMICOLON);
				}

				if (newActionOutputsString.length() > 0) {
					newActionOutputsString = newActionOutputsString.substring(
						0, newActionOutputsString.length() - 1);
				}

				action = StringBundler.concat(
					"call('", uuid, "', '", arguments[1], "', '",
					newActionOutputsString, "')");

				updated =
					updated ||
					!newActionOutputsString.equals(actionOutputsString);

				newActionsJSONArray.put(action);
			}

			ruleJSONObject.put("actions", newActionsJSONArray);
		}

		return updated;
	}

	private boolean _updateDDMStructure(JSONObject jsonObject) {
		boolean updatedRules = false;

		JSONArray rulesJSONArray = jsonObject.getJSONArray("rules");

		if (rulesJSONArray != null) {
			updatedRules = _updateDDMDataProviderRules(rulesJSONArray);
		}

		boolean updatedFields = false;

		JSONArray fieldsJSONArray = jsonObject.getJSONArray("fields");

		if (fieldsJSONArray != null) {
			updatedFields = _updateFieldsWithDataProviderAssigned(
				fieldsJSONArray);
		}

		if (updatedRules || updatedFields) {
			return true;
		}

		return false;
	}

	private void _updateDDMStructures() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select DDMStructure.structureId, DDMStructure.definition ",
					"from DDMDataProviderInstanceLink join ",
					"DDMStructureVersion on DDMStructureVersion.structureId = ",
					"DDMDataProviderInstanceLink.structureId left join ",
					"DDMStructure on DDMStructure.structureId = ",
					"DDMDataProviderInstanceLink.structureId and ",
					"DDMStructure.version = DDMStructureVersion.version"));
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update DDMStructure set definition = ? where " +
						"structureId = ?");
			PreparedStatement preparedStatement3 = connection.prepareStatement(
				"select structureVersionId, definition from " +
					"DDMStructureVersion where structureId = ?");
			PreparedStatement preparedStatement4 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update DDMStructureVersion set definition = ? where " +
						"structureVersionId = ?")) {

			try (ResultSet resultSet1 = preparedStatement1.executeQuery()) {
				while (resultSet1.next()) {
					JSONObject jsonObject = _jsonFactory.createJSONObject(
						resultSet1.getString("definition"));

					boolean updated = _updateDDMStructure(jsonObject);

					long structureId = resultSet1.getLong("structureId");

					if (updated &&
						!_updatedStructureIds.contains(structureId)) {

						preparedStatement2.setString(1, jsonObject.toString());
						preparedStatement2.setLong(2, structureId);

						preparedStatement2.addBatch();

						_updatedStructureIds.add(structureId);
					}

					preparedStatement3.setLong(1, structureId);

					try (ResultSet resultSet2 =
							preparedStatement3.executeQuery()) {

						while (resultSet2.next()) {
							jsonObject = _jsonFactory.createJSONObject(
								resultSet2.getString("definition"));

							updated = _updateDDMStructure(jsonObject);

							long structureVersionId = resultSet2.getLong(
								"structureVersionId");

							if (!updated ||
								_updatedStructureVersionIds.contains(
									structureVersionId)) {

								continue;
							}

							preparedStatement4.setString(
								1, jsonObject.toString());
							preparedStatement4.setLong(2, structureVersionId);

							preparedStatement4.addBatch();

							_updatedStructureVersionIds.add(structureVersionId);
						}
					}
				}

				preparedStatement2.executeBatch();

				preparedStatement4.executeBatch();
			}
		}
	}

	private boolean _updateFieldsWithDataProviderAssigned(
		JSONArray fieldsJSONArray) {

		boolean updated = false;

		for (int i = 0; i < fieldsJSONArray.length(); i++) {
			JSONObject fieldJSONObject = fieldsJSONArray.getJSONObject(i);

			if (!StringUtil.equals(
					fieldJSONObject.getString("dataSourceType"),
					"[\"data-provider\"]")) {

				continue;
			}

			long ddmDataProviderInstanceId = _extractDDMDataProviderInstanceId(
				fieldJSONObject.getString("ddmDataProviderInstanceId"));

			if (!_ddmDataProviderInstanceOutputParametersInstanceId.containsKey(
					ddmDataProviderInstanceId)) {

				continue;
			}

			String ddmDataProviderInstanceOutputName =
				_extractDDMDataProviderInstanceOutputName(
					fieldJSONObject.getString("ddmDataProviderInstanceOutput"));

			Map<String, String> ddmDataProviderOutputParameters =
				_ddmDataProviderInstanceOutputParametersInstanceId.get(
					ddmDataProviderInstanceId);

			String outputParameterId = ddmDataProviderOutputParameters.get(
				ddmDataProviderInstanceOutputName);

			fieldJSONObject.put(
				"ddmDataProviderInstanceOutput",
				"[\"" + outputParameterId + "\"]");

			updated = true;
		}

		return updated;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDMDataProviderInstanceUpgradeProcess.class);

	private final Map<Long, Map<String, String>>
		_ddmDataProviderInstanceOutputParametersInstanceId = new HashMap<>();
	private final Map<String, Map<String, String>>
		_ddmDataProviderInstanceOutputParametersUUID = new HashMap<>();
	private final JSONFactory _jsonFactory;
	private final List<Long> _updatedStructureIds = new ArrayList<>();
	private final List<Long> _updatedStructureVersionIds = new ArrayList<>();

}