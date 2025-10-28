/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.upgrade.v4_1_0;

import com.liferay.dynamic.data.mapping.io.DDMFormDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesDeserializerDeserializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesDeserializerDeserializeResponse;
import com.liferay.dynamic.data.mapping.model.DDMField;
import com.liferay.dynamic.data.mapping.model.DDMFieldAttribute;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.util.DDMFormDeserializeUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONSerializer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.UpgradeException;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.LRUMap;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Preston Crary
 */
public class DDMFieldUpgradeProcess extends UpgradeProcess {

	public DDMFieldUpgradeProcess(
		JSONFactory jsonFactory,
		DDMFormDeserializer jsonDDMFormJSONDeserializer,
		DDMFormValuesDeserializer jsonDDMFormValuesDeserializer) {

		_jsonFactory = jsonFactory;
		_jsonDDMFormJSONDeserializer = jsonDDMFormJSONDeserializer;
		_jsonDDMFormValuesDeserializer = jsonDDMFormValuesDeserializer;
	}

	@Override
	protected void doUpgrade() throws Exception {
		runSQL(
			StringBundler.concat(
				"create table DDMField (mvccVersion LONG default 0 not null, ",
				"ctCollectionId LONG default 0 not null, fieldId LONG not ",
				"null, companyId LONG, parentFieldId LONG, storageId LONG, ",
				"structureVersionId LONG, fieldName TEXT null, fieldType ",
				"VARCHAR(255) null, instanceId VARCHAR(75) null, localizable ",
				"BOOLEAN, priority INTEGER, primary key (fieldId, ",
				"ctCollectionId))"));

		runSQL(
			StringBundler.concat(
				"create table DDMFieldAttribute (mvccVersion LONG default 0 ",
				"not null, ctCollectionId LONG default 0 not null, ",
				"fieldAttributeId LONG not null, companyId LONG, fieldId ",
				"LONG, storageId LONG, attributeName VARCHAR(255) null, ",
				"languageId VARCHAR(75) null, largeAttributeValue TEXT null, ",
				"smallAttributeValue VARCHAR(255) null, primary key ",
				"(fieldAttributeId, ctCollectionId))"));

		try (PreparedStatement selectPreparedStatement =
				connection.prepareStatement(
					StringBundler.concat(
						"select DDMContent.contentId, DDMContent.companyId, ",
						"DDMContent.data_, DDMStorageLink.structureVersionId, ",
						"DDMStructure.structureId, DDMStructure.classNameId ",
						"from DDMContent inner join DDMStorageLink on ",
						"DDMStorageLink.classPK = DDMContent.contentId inner ",
						"join DDMStructureVersion on ",
						"DDMStructureVersion.structureVersionId = ",
						"DDMStorageLink.structureVersionId inner join ",
						"DDMStructure on DDMStructureVersion.structureId = ",
						"DDMStructure.structureId where ",
						"DDMStructure.storageType = 'json' and ",
						"DDMContent.ctCollectionId = 0 and ",
						"DDMStorageLink.ctCollectionId = 0 and ",
						"DDMStructureVersion.ctCollectionId = 0 and ",
						"DDMStructure.ctCollectionId = 0"));
			PreparedStatement insertDDMFieldPreparedStatement =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					StringBundler.concat(
						"insert into DDMField (mvccVersion, ctCollectionId, ",
						"fieldId, companyId, parentFieldId, storageId, ",
						"structureVersionId, fieldName, fieldType, ",
						"instanceId, localizable, priority) values (0, 0, ?, ",
						"?, ?, ?, ?, ?, ?, ?, ?, ?)"));
			PreparedStatement insertDDMFieldAttributePreparedStatement =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					StringBundler.concat(
						"insert into DDMFieldAttribute (mvccVersion, ",
						"ctCollectionId, fieldAttributeId, companyId, ",
						"fieldId, storageId, attributeName, languageId, ",
						"largeAttributeValue, smallAttributeValue) values (0, ",
						"0, ?, ?, ?, ?, ?, ?, ?, ?)"));
			PreparedStatement deleteDDMContentPreparedStatement =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					"delete from DDMContent where contentId = ? and " +
						"ctCollectionId = 0");
			ResultSet resultSet = selectPreparedStatement.executeQuery()) {

			while (resultSet.next()) {
				_upgradeDDMContent(
					resultSet.getLong("classNameId"),
					resultSet.getLong("companyId"),
					resultSet.getLong("contentId"),
					insertDDMFieldAttributePreparedStatement,
					insertDDMFieldPreparedStatement,
					resultSet.getString("data_"),
					deleteDDMContentPreparedStatement,
					resultSet.getLong("structureId"),
					resultSet.getLong("structureVersionId"));
			}

			insertDDMFieldPreparedStatement.executeBatch();

			insertDDMFieldAttributePreparedStatement.executeBatch();

			deleteDDMContentPreparedStatement.executeBatch();
		}

		_ddmForms.clear();

		_fullHierarchyDDMForms.clear();

		runSQL(
			"update DDMStructure set storageType = 'default' where " +
				"storageType = 'json'");

		runSQL(
			"update DDMStructureVersion set storageType = 'default' where " +
				"storageType = 'json'");

		try (PreparedStatement selectPreparedStatement =
				connection.prepareStatement(
					"select formInstanceId, settings_ from DDMFormInstance " +
						"where ctCollectionId = 0");
			ResultSet resultSet = selectPreparedStatement.executeQuery();
			PreparedStatement updatePreparedStatement =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					"update DDMFormInstance set settings_ = ? where " +
						"formInstanceId = ? and ctCollectionId = 0")) {

			while (resultSet.next()) {
				String settings = resultSet.getString("settings_");

				if (Validator.isNotNull(settings)) {
					JSONObject settingsJSONObject =
						_jsonFactory.createJSONObject(settings);

					JSONArray fieldValuesJSONArray =
						settingsJSONObject.getJSONArray("fieldValues");

					for (int i = 0; i < fieldValuesJSONArray.length(); i++) {
						JSONObject jsonObject =
							fieldValuesJSONArray.getJSONObject(i);

						if (Objects.equals(
								jsonObject.getString("name"), "storageType")) {

							JSONArray oldJSONArray =
								_jsonFactory.createJSONArray(
									jsonObject.getString("value"));

							JSONArray newJSONArray =
								_jsonFactory.createJSONArray();

							for (Object value : oldJSONArray) {
								if (Objects.equals(value, "json")) {
									value = "default";
								}

								newJSONArray.put(value);
							}

							jsonObject.put("value", newJSONArray);

							break;
						}
					}

					updatePreparedStatement.setString(
						1, settingsJSONObject.toString());
					updatePreparedStatement.setLong(
						2, resultSet.getLong("formInstanceId"));

					updatePreparedStatement.addBatch();
				}
			}

			updatePreparedStatement.executeBatch();
		}
	}

	private void _addDDMFieldAndDDMFieldAttribute(
			long companyId, long contentId,
			Map<String, DDMFieldInfo> ddmFieldInfoMap,
			Map<String, DDMFormField> ddmFormFieldsMap,
			List<DDMFormFieldValue> ddmFormValues,
			PreparedStatement insertDDMFieldAttributePreparedStatement,
			PreparedStatement insertDDMFieldPreparedStatement,
			Map<String, Long> instanceToFieldIdMap, String parentInstanceId,
			int priority, DDMFieldInfo rootDDMFieldInfo,
			long structureVersionId)
		throws Exception {

		for (DDMFormFieldValue ddmFormFieldValue : ddmFormValues) {
			long fieldId = increment(DDMField.class.getName());

			String instanceId = ddmFormFieldValue.getInstanceId();

			if (ddmFieldInfoMap.containsKey(instanceId)) {
				instanceId =
					com.liferay.portal.kernel.util.StringUtil.randomString(8);
			}

			DDMFieldInfo ddmFieldInfo = new DDMFieldInfo(
				ddmFormFieldValue.getName(), instanceId, parentInstanceId);

			ddmFieldInfoMap.put(ddmFieldInfo._instanceId, ddmFieldInfo);

			Value value = ddmFormFieldValue.getValue();

			if (value != null) {
				Map<Locale, String> values = value.getValues();

				for (Map.Entry<Locale, String> entry : values.entrySet()) {
					ddmFieldInfo._ddmFieldAttributeInfos.addAll(
						_getDDMFieldAttributeInfos(
							companyId, contentId, fieldId,
							insertDDMFieldAttributePreparedStatement,
							LanguageUtil.getLanguageId(entry.getKey()),
							entry.getValue()));
				}
			}

			long parentFieldId = 0;

			if (ddmFieldInfo._parentInstanceId != null) {
				parentFieldId = instanceToFieldIdMap.get(
					ddmFieldInfo._parentInstanceId);
			}

			String fieldType = StringPool.BLANK;
			boolean localizable = false;

			if (ddmFieldInfo != rootDDMFieldInfo) {
				DDMFormField ddmFormField = ddmFormFieldsMap.get(
					ddmFieldInfo._fieldName);

				if (ddmFormField != null) {
					fieldType = ddmFormField.getType();
					localizable = ddmFormField.isLocalizable();
				}
			}

			insertDDMFieldPreparedStatement.setLong(1, fieldId);
			insertDDMFieldPreparedStatement.setLong(2, companyId);
			insertDDMFieldPreparedStatement.setLong(3, parentFieldId);
			insertDDMFieldPreparedStatement.setLong(4, contentId);
			insertDDMFieldPreparedStatement.setLong(5, structureVersionId);
			insertDDMFieldPreparedStatement.setString(
				6, ddmFieldInfo._fieldName);
			insertDDMFieldPreparedStatement.setString(7, fieldType);
			insertDDMFieldPreparedStatement.setString(
				8, ddmFieldInfo._instanceId);
			insertDDMFieldPreparedStatement.setBoolean(9, localizable);
			insertDDMFieldPreparedStatement.setInt(10, priority);

			insertDDMFieldPreparedStatement.addBatch();

			priority++;

			instanceToFieldIdMap.put(ddmFieldInfo._instanceId, fieldId);

			_addDDMFieldAndDDMFieldAttribute(
				companyId, contentId, ddmFieldInfoMap, ddmFormFieldsMap,
				ddmFormFieldValue.getNestedDDMFormFieldValues(),
				insertDDMFieldAttributePreparedStatement,
				insertDDMFieldPreparedStatement, instanceToFieldIdMap,
				ddmFieldInfo._instanceId, priority, rootDDMFieldInfo,
				structureVersionId);
		}
	}

	private void _addRootDDMFieldAndDDMFieldAttribute(
			long companyId, long contentId,
			PreparedStatement insertDDMFieldAttributePreparedStatement,
			PreparedStatement insertDDMFieldPreparedStatement,
			Map<String, Long> instanceToFieldIdMap,
			DDMFieldInfo rootDDMFieldInfo, long structureVersionId)
		throws Exception {

		long fieldId = increment(DDMField.class.getName());

		insertDDMFieldPreparedStatement.setLong(1, fieldId);

		insertDDMFieldPreparedStatement.setLong(2, companyId);
		insertDDMFieldPreparedStatement.setLong(3, 0);
		insertDDMFieldPreparedStatement.setLong(4, contentId);
		insertDDMFieldPreparedStatement.setLong(5, structureVersionId);
		insertDDMFieldPreparedStatement.setString(
			6, rootDDMFieldInfo._fieldName);
		insertDDMFieldPreparedStatement.setString(7, StringPool.BLANK);
		insertDDMFieldPreparedStatement.setString(
			8, rootDDMFieldInfo._instanceId);
		insertDDMFieldPreparedStatement.setBoolean(9, false);
		insertDDMFieldPreparedStatement.setInt(10, 0);

		insertDDMFieldPreparedStatement.addBatch();

		instanceToFieldIdMap.put(rootDDMFieldInfo._instanceId, fieldId);

		for (DDMFieldAttributeInfo ddmFieldAttributeInfo :
				rootDDMFieldInfo._ddmFieldAttributeInfos) {

			_insertDDMFieldAttribute(
				companyId, contentId, ddmFieldAttributeInfo, fieldId,
				insertDDMFieldAttributePreparedStatement);
		}
	}

	private List<DDMFieldAttributeInfo> _getDDMFieldAttributeInfos(
			long companyId, long contentId, long fieldId,
			PreparedStatement insertDDMFieldAttributePreparedStatement,
			String languageId, String valueString)
		throws Exception {

		int length = valueString.length();

		if ((length > 1) &&
			(valueString.charAt(0) == CharPool.OPEN_CURLY_BRACE) &&
			(valueString.charAt(length - 1) == CharPool.CLOSE_CURLY_BRACE)) {

			try {
				JSONSerializer jsonSerializer =
					_jsonFactory.createJSONSerializer();

				JSONObject jsonObject = _jsonFactory.createJSONObject(
					valueString);

				Set<String> keySet = jsonObject.keySet();

				if (!keySet.isEmpty()) {
					List<DDMFieldAttributeInfo> ddmFieldAttributeInfos =
						new ArrayList<>(keySet.size());

					for (String key : jsonObject.keySet()) {
						DDMFieldAttributeInfo ddmFieldAttributeInfo =
							new DDMFieldAttributeInfo(
								key,
								jsonSerializer.serialize(jsonObject.get(key)),
								languageId);

						_insertDDMFieldAttribute(
							companyId, contentId, ddmFieldAttributeInfo,
							fieldId, insertDDMFieldAttributePreparedStatement);

						ddmFieldAttributeInfos.add(ddmFieldAttributeInfo);
					}

					return ddmFieldAttributeInfos;
				}
			}
			catch (JSONException jsonException) {
				if (_log.isWarnEnabled()) {
					_log.warn("Unable to parse: " + valueString, jsonException);
				}
			}
		}

		DDMFieldAttributeInfo ddmFieldAttributeInfo = new DDMFieldAttributeInfo(
			StringPool.BLANK, valueString, languageId);

		_insertDDMFieldAttribute(
			companyId, contentId, ddmFieldAttributeInfo, fieldId,
			insertDDMFieldAttributePreparedStatement);

		return Collections.singletonList(ddmFieldAttributeInfo);
	}

	private DDMForm _getDDMForm(long structureId, long structureVersionId)
		throws Exception {

		DDMForm ddmForm = _ddmForms.get(structureVersionId);

		if (ddmForm != null) {
			return ddmForm;
		}

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select definition from DDMStructureVersion where " +
					"structureId = ? and structureVersionId = ? and " +
						"ctCollectionId = 0")) {

			preparedStatement.setLong(1, structureId);
			preparedStatement.setLong(2, structureVersionId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					ddmForm = DDMFormDeserializeUtil.deserialize(
						_jsonDDMFormJSONDeserializer,
						resultSet.getString("definition"));

					_ddmForms.put(structureVersionId, ddmForm);

					return ddmForm;
				}
			}
		}

		throw new UpgradeException(
			"Unable to find dynamic data mapping structure with ID " +
				structureVersionId);
	}

	private DDMForm _getFullHierarchyDDMForm(
			long structureId, long structureVersionId)
		throws Exception {

		DDMForm fullHierarchyDDMForm = _fullHierarchyDDMForms.get(
			structureVersionId);

		if (fullHierarchyDDMForm != null) {
			return fullHierarchyDDMForm;
		}

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select parentStructureId from DDMStructureVersion where " +
					"structureId = ? and structureVersionId = ? and " +
						"ctCollectionId = 0");
			PreparedStatement preparedStatement2 = connection.prepareStatement(
				"select max(structureVersionId) from DDMStructureVersion " +
					"where structureId = ?")) {

			preparedStatement1.setLong(1, structureId);
			preparedStatement1.setLong(2, structureVersionId);

			try (ResultSet resultSet1 = preparedStatement1.executeQuery()) {
				if (resultSet1.next()) {
					long parentStructureId = resultSet1.getLong(
						"parentStructureId");

					fullHierarchyDDMForm = _getDDMForm(
						structureId, structureVersionId);

					_fullHierarchyDDMForms.put(
						structureVersionId, fullHierarchyDDMForm);

					if (parentStructureId <= 0) {
						return fullHierarchyDDMForm;
					}

					preparedStatement2.setLong(1, parentStructureId);

					try (ResultSet resultSet2 =
							preparedStatement2.executeQuery()) {

						if (resultSet2.next()) {
							DDMForm parentDDMForm = _getFullHierarchyDDMForm(
								parentStructureId, resultSet2.getLong(1));

							List<DDMFormField> ddmFormFields =
								fullHierarchyDDMForm.getDDMFormFields();

							ddmFormFields.addAll(
								parentDDMForm.getDDMFormFields());
						}
					}

					return fullHierarchyDDMForm;
				}
			}
		}

		throw new UpgradeException(
			"Unable to find dynamic data mapping structure with ID " +
				structureId);
	}

	private void _insertDDMFieldAttribute(
			long companyId, long contentId,
			DDMFieldAttributeInfo ddmFieldAttributeInfo, long fieldId,
			PreparedStatement insertDDMFieldAttributePreparedStatement)
		throws Exception {

		String smallAttributeValue = null;
		String largeAttributeValue = null;

		if (ddmFieldAttributeInfo._attributeValue != null) {
			if (ddmFieldAttributeInfo._attributeValue.length() > 255) {
				largeAttributeValue = ddmFieldAttributeInfo._attributeValue;
			}
			else {
				smallAttributeValue = ddmFieldAttributeInfo._attributeValue;
			}
		}

		insertDDMFieldAttributePreparedStatement.setLong(
			1, increment(DDMFieldAttribute.class.getName()));
		insertDDMFieldAttributePreparedStatement.setLong(2, companyId);
		insertDDMFieldAttributePreparedStatement.setLong(3, fieldId);
		insertDDMFieldAttributePreparedStatement.setLong(4, contentId);
		insertDDMFieldAttributePreparedStatement.setString(
			5, ddmFieldAttributeInfo._attributeName);
		insertDDMFieldAttributePreparedStatement.setString(
			6, ddmFieldAttributeInfo._languageId);
		insertDDMFieldAttributePreparedStatement.setString(
			7, largeAttributeValue);
		insertDDMFieldAttributePreparedStatement.setString(
			8, smallAttributeValue);

		insertDDMFieldAttributePreparedStatement.addBatch();
	}

	private void _upgradeDDMContent(
			long classNameId, long companyId, long contentId,
			PreparedStatement insertDDMFieldAttributePreparedStatement,
			PreparedStatement insertDDMFieldPreparedStatement, String data,
			PreparedStatement deleteDDMContentPreparedStatement,
			long structureId, long structureVersionId)
		throws Exception {

		DDMForm ddmForm = _getFullHierarchyDDMForm(
			structureId, structureVersionId);

		DDMFormValuesDeserializerDeserializeResponse
			ddmFormValuesDeserializerDeserializeResponse =
				_jsonDDMFormValuesDeserializer.deserialize(
					DDMFormValuesDeserializerDeserializeRequest.Builder.
						newBuilder(
							data, ddmForm
						).build());

		if (ddmFormValuesDeserializerDeserializeResponse.getException() !=
				null) {

			throw ddmFormValuesDeserializerDeserializeResponse.getException();
		}

		DDMFormValues ddmFormValues =
			ddmFormValuesDeserializerDeserializeResponse.getDDMFormValues();

		if (classNameId != PortalUtil.getClassNameId(
				_CLASS_NAME_DDL_RECORD_SET)) {

			ddmFormValues.setDDMFormFieldValues(
				_upgradeDDMFormValuesHierarchy(
					ddmFormValues.getDDMFormFieldValues(), contentId));
		}

		Map<String, DDMFormField> ddmFormFieldsMap =
			ddmForm.getDDMFormFieldsMap(true);

		DDMFieldInfo rootDDMFieldInfo = new DDMFieldInfo(
			StringPool.BLANK, StringPool.BLANK, null);

		Map<String, DDMFieldInfo> ddmFieldInfoMap = LinkedHashMapBuilder.put(
			StringPool.BLANK, rootDDMFieldInfo
		).build();

		Collections.addAll(
			rootDDMFieldInfo._ddmFieldAttributeInfos,
			new DDMFieldAttributeInfo(
				"availableLanguageIds",
				StringUtil.merge(
					ddmFormValues.getAvailableLocales(),
					LocaleUtil::toLanguageId, StringPool.COMMA),
				StringPool.BLANK),
			new DDMFieldAttributeInfo(
				"defaultLanguageId",
				LocaleUtil.toLanguageId(ddmFormValues.getDefaultLocale()),
				StringPool.BLANK));

		Map<String, Long> instanceToFieldIdMap = new HashMap<>();

		_addRootDDMFieldAndDDMFieldAttribute(
			companyId, contentId, insertDDMFieldAttributePreparedStatement,
			insertDDMFieldPreparedStatement, instanceToFieldIdMap,
			rootDDMFieldInfo, structureVersionId);

		_addDDMFieldAndDDMFieldAttribute(
			companyId, contentId, ddmFieldInfoMap, ddmFormFieldsMap,
			ddmFormValues.getDDMFormFieldValues(),
			insertDDMFieldAttributePreparedStatement,
			insertDDMFieldPreparedStatement, instanceToFieldIdMap, null, 1,
			rootDDMFieldInfo, structureVersionId);

		deleteDDMContentPreparedStatement.setLong(1, contentId);

		deleteDDMContentPreparedStatement.addBatch();
	}

	private List<DDMFormFieldValue> _upgradeDDMFormValuesHierarchy(
		List<DDMFormFieldValue> ddmFormFieldValues, long contentId) {

		return TransformUtil.transform(
			ddmFormFieldValues,
			ddmFormFieldValue -> {
				String type = ddmFormFieldValue.getType();

				if (ListUtil.isNotEmpty(
						ddmFormFieldValue.getNestedDDMFormFieldValues()) &&
					(type != null) &&
					!com.liferay.portal.kernel.util.StringUtil.equals(
						type, "fieldset")) {

					DDMFormFieldValue newDDMFormFieldValue =
						new DDMFormFieldValue() {
							{
								setInstanceId(
									com.liferay.portal.kernel.util.StringUtil.
										randomString());
								setName(
									ddmFormFieldValue.getName() + "FieldSet");
							}
						};

					List<DDMFormFieldValue> nestedDDMFormFieldValues =
						new ArrayList<>(
							ddmFormFieldValue.getNestedDDMFormFieldValues());

					ddmFormFieldValue.setNestedDDMFormFields(new ArrayList<>());

					newDDMFormFieldValue.setNestedDDMFormFields(
						ListUtil.concat(
							Collections.singletonList(ddmFormFieldValue),
							_upgradeDDMFormValuesHierarchy(
								nestedDDMFormFieldValues, contentId)));

					return newDDMFormFieldValue;
				}

				return ddmFormFieldValue;
			});
	}

	private static final String _CLASS_NAME_DDL_RECORD_SET =
		"com.liferay.dynamic.data.lists.model.DDLRecordSet";

	private static final Log _log = LogFactoryUtil.getLog(
		DDMFieldUpgradeProcess.class);

	private final Map<Long, DDMForm> _ddmForms = new LRUMap<>(1000);
	private final Map<Long, DDMForm> _fullHierarchyDDMForms = new LRUMap<>(
		1000);
	private final DDMFormDeserializer _jsonDDMFormJSONDeserializer;
	private final DDMFormValuesDeserializer _jsonDDMFormValuesDeserializer;
	private final JSONFactory _jsonFactory;

	private static class DDMFieldAttributeInfo {

		private DDMFieldAttributeInfo(
			String attributeName, String attributeValue, String languageId) {

			_attributeName = attributeName;
			_attributeValue = attributeValue;
			_languageId = languageId;
		}

		private final String _attributeName;
		private final String _attributeValue;
		private final String _languageId;

	}

	private static class DDMFieldInfo {

		private DDMFieldInfo(
			String fieldName, String instanceId, String parentInstanceId) {

			_fieldName = fieldName;
			_instanceId = instanceId;
			_parentInstanceId = parentInstanceId;
		}

		private final List<DDMFieldAttributeInfo> _ddmFieldAttributeInfos =
			new ArrayList<>();
		private final String _fieldName;
		private final String _instanceId;
		private final String _parentInstanceId;

	}

}