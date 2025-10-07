/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.impl;

import com.liferay.dynamic.data.mapping.model.DDMField;
import com.liferay.dynamic.data.mapping.model.DDMFieldAttribute;
import com.liferay.dynamic.data.mapping.model.DDMFieldAttributeTable;
import com.liferay.dynamic.data.mapping.model.DDMFieldTable;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersionTable;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.model.impl.DDMFieldAttributeImpl;
import com.liferay.dynamic.data.mapping.service.base.DDMFieldLocalServiceBaseImpl;
import com.liferay.dynamic.data.mapping.service.persistence.DDMFieldAttributePersistence;
import com.liferay.dynamic.data.mapping.service.persistence.DDMStructurePersistence;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.util.DDMFormFieldUtil;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Expression;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.JoinStep;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONSerializer;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Preston Crary
 */
@Component(
	property = "model.class.name=com.liferay.dynamic.data.mapping.model.DDMField",
	service = AopService.class
)
public class DDMFieldLocalServiceImpl extends DDMFieldLocalServiceBaseImpl {

	@Override
	public void deleteDDMFields(long structureId) {
		for (DDMFieldAttribute ddmFieldAttribute :
				_ddmFieldAttributePersistence.<List<DDMFieldAttribute>>dslQuery(
					DSLQueryFactoryUtil.select(
						DDMFieldAttributeTable.INSTANCE
					).from(
						DDMFieldAttributeTable.INSTANCE
					).innerJoinON(
						DDMFieldTable.INSTANCE,
						DDMFieldTable.INSTANCE.fieldId.eq(
							DDMFieldAttributeTable.INSTANCE.fieldId)
					).innerJoinON(
						DDMStructureVersionTable.INSTANCE,
						DDMStructureVersionTable.INSTANCE.structureVersionId.eq(
							DDMFieldTable.INSTANCE.structureVersionId
						).and(
							DDMStructureVersionTable.INSTANCE.structureId.eq(
								structureId)
						)
					))) {

			_ddmFieldAttributePersistence.remove(ddmFieldAttribute);
		}

		for (DDMField ddmField :
				ddmFieldPersistence.<List<DDMField>>dslQuery(
					DSLQueryFactoryUtil.select(
						DDMFieldTable.INSTANCE
					).from(
						DDMFieldTable.INSTANCE
					).innerJoinON(
						DDMStructureVersionTable.INSTANCE,
						DDMStructureVersionTable.INSTANCE.structureVersionId.eq(
							DDMFieldTable.INSTANCE.structureVersionId
						).and(
							DDMStructureVersionTable.INSTANCE.structureId.eq(
								structureId)
						)
					))) {

			ddmFieldPersistence.remove(ddmField);
		}
	}

	@Override
	public void deleteDDMFormValues(long storageId) {
		ddmFieldPersistence.removeByStorageId(storageId);

		_ddmFieldAttributePersistence.removeByStorageId(storageId);
	}

	@Override
	public DDMFieldAttribute fetchDDMFieldAttribute(
		long fieldId, String attributeName, String languageId) {

		return _ddmFieldAttributePersistence.fetchByF_AN_L(
			fieldId, attributeName, languageId);
	}

	@Override
	public List<DDMFieldAttribute> getDDMFieldAttributes(
		long storageId, String attributeName) {

		return _ddmFieldAttributePersistence.findByS_AN(
			storageId, attributeName);
	}

	@Override
	public List<DDMField> getDDMFields(long storageId, String fieldName) {
		return ddmFieldPersistence.findByS_F(storageId, fieldName);
	}

	@Override
	public DDMFormValues getDDMFormValues(DDMForm ddmForm, long storageId) {
		List<DDMField> ddmFields = ddmFieldPersistence.findByStorageId(
			storageId);

		if (ddmFields.isEmpty()) {
			return null;
		}

		return _getDDMFormValues(
			_ddmFieldAttributePersistence.findByStorageId(storageId), ddmFields,
			ddmForm);
	}

	@Override
	public DDMFormValues getDDMFormValues(
		DDMForm ddmForm, long storageId, String languageId) {

		List<DDMField> ddmFields = ddmFieldPersistence.findByStorageId(
			storageId);

		if (ddmFields.isEmpty()) {
			return null;
		}

		try {
			DDMFieldAttribute ddmFieldAttribute =
				_ddmFieldAttributePersistence.findByS_AN_First(
					storageId, "availableLanguageIds", null);

			List<String> availableLanguageIds = StringUtil.split(
				ddmFieldAttribute.getAttributeValue());

			if (!availableLanguageIds.contains(languageId)) {
				ddmFieldAttribute =
					_ddmFieldAttributePersistence.findByS_AN_First(
						storageId, "defaultLanguageId", null);

				languageId = ddmFieldAttribute.getAttributeValue();
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return _getDDMFormValues(
			_ddmFieldAttributePersistence.findByS_L(
				storageId, new String[] {languageId, StringPool.BLANK}),
			ddmFields, ddmForm);
	}

	@Override
	public int getDDMFormValuesCount(long structureId) {
		return ddmFieldPersistence.dslQueryCount(
			DSLQueryFactoryUtil.count(
			).from(
				DDMFieldTable.INSTANCE
			).innerJoinON(
				DDMStructureVersionTable.INSTANCE,
				DDMStructureVersionTable.INSTANCE.structureVersionId.eq(
					DDMFieldTable.INSTANCE.structureVersionId
				).and(
					DDMStructureVersionTable.INSTANCE.structureId.eq(
						structureId)
				)
			));
	}

	@Override
	public int getDDMFormValuesCount(
		long companyId, String fieldType, Map<String, Object> attributes) {

		JoinStep joinStep = DSLQueryFactoryUtil.count(
		).from(
			DDMFieldTable.INSTANCE
		);

		int aliasCount = 0;

		Column<DDMFieldAttributeTable, String> languageIdColumn = null;

		JSONSerializer jsonSerializer = _jsonFactory.createJSONSerializer();

		for (Map.Entry<String, Object> entry : attributes.entrySet()) {
			aliasCount++;

			DDMFieldAttributeTable aliasDDMFieldAttributeTable =
				DDMFieldAttributeTable.INSTANCE.as(
					"aliasDDMFieldAttributeTable" + aliasCount);

			Predicate predicate = DDMFieldTable.INSTANCE.fieldId.eq(
				aliasDDMFieldAttributeTable.fieldId);

			if (languageIdColumn != null) {
				predicate = predicate.and(
					aliasDDMFieldAttributeTable.languageId.eq(
						languageIdColumn));
			}

			String key = entry.getKey();
			String value = null;

			if ((key == null) || key.isEmpty()) {
				predicate = predicate.and(
					aliasDDMFieldAttributeTable.attributeName.eq(
						key
					).or(
						aliasDDMFieldAttributeTable.attributeName.isNull()
					).withParentheses());

				value = String.valueOf(entry.getValue());
			}
			else {
				predicate = predicate.and(
					aliasDDMFieldAttributeTable.attributeName.eq(key));

				value = jsonSerializer.serialize(entry.getValue());
			}

			Expression<String> valueExpression =
				aliasDDMFieldAttributeTable.smallAttributeValue;

			if (value.length() >
					DDMFieldAttributeImpl.SMALL_ATTRIBUTE_VALUE_MAX_LENGTH) {

				valueExpression = DSLFunctionFactoryUtil.castClobText(
					aliasDDMFieldAttributeTable.largeAttributeValue);
			}

			joinStep = joinStep.innerJoinON(
				aliasDDMFieldAttributeTable,
				predicate.and(valueExpression.eq(value)));

			languageIdColumn = aliasDDMFieldAttributeTable.languageId;
		}

		return ddmFieldPersistence.dslQueryCount(
			joinStep.where(
				DDMFieldTable.INSTANCE.companyId.eq(
					companyId
				).and(
					DDMFieldTable.INSTANCE.fieldType.eq(fieldType)
				)));
	}

	@Override
	public void updateDDMFormValues(
			long structureId, long storageId, DDMFormValues ddmFormValues)
		throws PortalException {

		DDMStructure ddmStructure = _ddmStructurePersistence.findByPrimaryKey(
			structureId);

		DDMStructureVersion ddmStructureVersion =
			ddmStructure.getStructureVersion();

		DDMForm ddmForm = ddmFormValues.getDDMForm();

		Map<String, DDMFormField> ddmFormFieldsMap =
			ddmForm.getDDMFormFieldsMap(true);

		DDMFieldInfo rootDDMFieldInfo = new DDMFieldInfo(
			StringPool.BLANK, StringPool.BLANK, false, null);

		Map<String, DDMFieldInfo> ddmFieldInfoMap = LinkedHashMapBuilder.put(
			StringPool.BLANK, rootDDMFieldInfo
		).build();

		rootDDMFieldInfo._ddmFieldAttributeInfos.put(
			StringPool.BLANK,
			Arrays.asList(
				new DDMFieldAttributeInfo(
					"availableLanguageIds",
					StringUtil.merge(
						ddmFormValues.getAvailableLocales(),
						LocaleUtil::toLanguageId, StringPool.COMMA),
					rootDDMFieldInfo, StringPool.BLANK),
				new DDMFieldAttributeInfo(
					"defaultLanguageId",
					LocaleUtil.toLanguageId(ddmFormValues.getDefaultLocale()),
					rootDDMFieldInfo, StringPool.BLANK)));

		_collectDDMFieldInfos(
			ddmFieldInfoMap, ddmFormFieldsMap,
			ddmFormValues.getDDMFormFieldValues(), null);

		DDMFormUpdateContext ddmFormUpdateContext = _getDDMFormUpdateContext(
			ddmFieldInfoMap, ddmFormFieldsMap, storageId);

		long batchCounter = 0;

		if (ddmFormUpdateContext._newDDMFieldsCount > 0) {
			batchCounter = counterLocalService.increment(
				DDMField.class.getName(),
				ddmFormUpdateContext._newDDMFieldsCount);

			batchCounter -= ddmFormUpdateContext._newDDMFieldsCount;
		}

		int priority = 0;

		Map<String, Long> instanceToFieldIdMap = new HashMap<>();

		List<Map.Entry<DDMField, DDMFieldInfo>> childrenDDMFields =
			new ArrayList<>();
		List<Map.Entry<DDMField, DDMFieldInfo>> parentsDDMFields =
			new ArrayList<>();

		for (Map.Entry<DDMField, DDMFieldInfo> entry :
				ddmFormUpdateContext._ddmFieldEntries) {

			DDMFieldInfo ddmFieldInfo = entry.getValue();

			if ((ddmFieldInfo == null) ||
				(ddmFieldInfo._parentInstanceId == null)) {

				parentsDDMFields.add(entry);
			}
			else {
				childrenDDMFields.add(entry);
			}
		}

		ddmFormUpdateContext._ddmFieldEntries.clear();

		ddmFormUpdateContext._ddmFieldEntries.addAll(parentsDDMFields);

		ddmFormUpdateContext._ddmFieldEntries.addAll(childrenDDMFields);

		for (Map.Entry<DDMField, DDMFieldInfo> entry :
				ddmFormUpdateContext._ddmFieldEntries) {

			DDMField ddmField = entry.getKey();
			DDMFieldInfo ddmFieldInfo = entry.getValue();

			if (ddmFieldInfo == null) {
				ddmFieldPersistence.remove(ddmField);

				continue;
			}

			if (ddmField == null) {
				ddmField = ddmFieldPersistence.create(++batchCounter);
			}

			long parentFieldId = 0;

			if (ddmFieldInfo._parentInstanceId != null) {
				parentFieldId = instanceToFieldIdMap.get(
					ddmFieldInfo._parentInstanceId);
			}

			ddmField.setParentFieldId(parentFieldId);
			ddmField.setStorageId(storageId);
			ddmField.setStructureVersionId(
				ddmStructureVersion.getStructureVersionId());
			ddmField.setFieldName(ddmFieldInfo._fieldName);
			ddmField.setInstanceId(ddmFieldInfo._instanceId);
			ddmField.setPriority(priority);

			if (ddmFieldInfo != rootDDMFieldInfo) {
				DDMFormField ddmFormField = ddmFormFieldsMap.get(
					ddmFieldInfo._fieldName);

				ddmField.setFieldType(ddmFormField.getType());
				ddmField.setLocalizable(ddmFormField.isLocalizable());
			}

			ddmField = ddmFieldPersistence.update(ddmField);

			priority++;

			instanceToFieldIdMap.put(
				ddmField.getInstanceId(), ddmField.getFieldId());
		}

		if (ddmFormUpdateContext._newDDMFieldAttributesCount > 0) {
			batchCounter = counterLocalService.increment(
				DDMFieldAttribute.class.getName(),
				ddmFormUpdateContext._newDDMFieldAttributesCount);

			batchCounter -= ddmFormUpdateContext._newDDMFieldAttributesCount;
		}

		for (Map.Entry<DDMFieldAttribute, DDMFieldAttributeInfo> entry :
				ddmFormUpdateContext._ddmFieldAttributeEntries) {

			DDMFieldAttribute ddmFieldAttribute = entry.getKey();
			DDMFieldAttributeInfo ddmFieldAttributeInfo = entry.getValue();

			if (ddmFieldAttributeInfo == null) {
				_ddmFieldAttributePersistence.remove(ddmFieldAttribute);

				continue;
			}

			if (ddmFieldAttribute == null) {
				ddmFieldAttribute = _ddmFieldAttributePersistence.create(
					++batchCounter);
			}

			ddmFieldAttribute.setFieldId(
				instanceToFieldIdMap.get(
					ddmFieldAttributeInfo._ddmFieldInfo._instanceId));
			ddmFieldAttribute.setStorageId(storageId);
			ddmFieldAttribute.setAttributeName(
				ddmFieldAttributeInfo._attributeName);
			ddmFieldAttribute.setLanguageId(ddmFieldAttributeInfo._languageId);
			ddmFieldAttribute.setAttributeValue(
				ddmFieldAttributeInfo._attributeValue);

			_ddmFieldAttributePersistence.update(ddmFieldAttribute);
		}
	}

	private void _collectDDMFieldInfos(
		Map<String, DDMFieldInfo> ddmFieldInfoMap,
		Map<String, DDMFormField> ddmFormFieldMap,
		List<DDMFormFieldValue> ddmFormValues, String parentInstanceId) {

		for (DDMFormFieldValue ddmFormFieldValue : ddmFormValues) {
			DDMFormField ddmFormField = ddmFormFieldMap.get(
				ddmFormFieldValue.getName());

			if (ddmFormField == null) {
				continue;
			}

			String instanceId = ddmFormFieldValue.getInstanceId();

			while (ddmFieldInfoMap.containsKey(instanceId)) {
				instanceId =
					com.liferay.portal.kernel.util.StringUtil.randomString();
			}

			DDMFieldInfo ddmFieldInfo = new DDMFieldInfo(
				ddmFormFieldValue.getName(), instanceId,
				ddmFormField.isLocalizable(), parentInstanceId);

			ddmFieldInfoMap.put(instanceId, ddmFieldInfo);

			Value value = ddmFormFieldValue.getValue();

			if (value != null) {
				Map<Locale, String> values = value.getValues();

				for (Map.Entry<Locale, String> entry : values.entrySet()) {
					String languageId = _language.getLanguageId(entry.getKey());

					ddmFieldInfo._ddmFieldAttributeInfos.put(
						languageId,
						_getDDMFieldAttributeInfos(
							ddmFieldInfo, languageId, entry.getValue()));
				}
			}

			_collectDDMFieldInfos(
				ddmFieldInfoMap, ddmFormFieldMap,
				ddmFormFieldValue.getNestedDDMFormFieldValues(), instanceId);
		}
	}

	private List<DDMFieldAttributeInfo> _getDDMFieldAttributeInfos(
		DDMFieldInfo ddmFieldInfo, String languageId, String valueString) {

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
						ddmFieldAttributeInfos.add(
							new DDMFieldAttributeInfo(
								key,
								jsonSerializer.serialize(jsonObject.get(key)),
								ddmFieldInfo, languageId));
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

		return Collections.singletonList(
			new DDMFieldAttributeInfo(
				StringPool.BLANK, valueString, ddmFieldInfo, languageId));
	}

	private DDMFormFieldValue _getDDMFormFieldValue(
		DDMFieldInfo ddmFieldInfo, Locale defaultLocale) {

		DDMFormFieldValue ddmFormFieldValue = new DDMFormFieldValue();

		ddmFormFieldValue.setInstanceId(ddmFieldInfo._instanceId);
		ddmFormFieldValue.setName(ddmFieldInfo._fieldName);

		if (ddmFieldInfo._localizable) {
			Value value = new LocalizedValue(defaultLocale);

			for (Map.Entry<String, List<DDMFieldAttributeInfo>> entry :
					ddmFieldInfo._ddmFieldAttributeInfos.entrySet()) {

				Locale locale = LocaleUtil.fromLanguageId(
					entry.getKey(), true, false);

				if (locale != null) {
					value.addString(locale, _getValueString(entry.getValue()));
				}
			}

			ddmFormFieldValue.setValue(value);
		}
		else {
			List<DDMFieldAttributeInfo> ddmFieldAttributeInfos =
				ddmFieldInfo._ddmFieldAttributeInfos.get(StringPool.BLANK);

			if (ddmFieldAttributeInfos != null) {
				ddmFormFieldValue.setValue(
					new UnlocalizedValue(
						_getValueString(ddmFieldAttributeInfos)));
			}
		}

		for (DDMFieldInfo childDDMFieldInfo :
				ddmFieldInfo._childDDMFieldInfos) {

			ddmFormFieldValue.addNestedDDMFormFieldValue(
				_getDDMFormFieldValue(childDDMFieldInfo, defaultLocale));
		}

		return ddmFormFieldValue;
	}

	private DDMFormUpdateContext _getDDMFormUpdateContext(
		Map<String, DDMFieldInfo> ddmFieldInfoMap,
		Map<String, DDMFormField> ddmFormFieldsMap, long storageId) {

		List<Map.Entry<DDMField, DDMFieldInfo>> ddmFieldEntries =
			new ArrayList<>();

		int newDDMFieldsCount = 0;

		Map<String, DDMField> ddmFieldsMap = new HashMap<>();

		ListUtil.isNotEmptyForEach(
			ddmFieldPersistence.findByStorageId(storageId),
			ddmField -> ddmFieldsMap.put(
				_getKey(ddmField.getFieldName(), ddmField.getInstanceId()),
				ddmField));

		for (DDMFieldInfo ddmFieldInfo : ddmFieldInfoMap.values()) {
			String key = _getKey(
				ddmFieldInfo._fieldName, ddmFieldInfo._instanceId);

			if (ddmFieldsMap.containsKey(key)) {
				ddmFieldEntries.add(
					new AbstractMap.SimpleImmutableEntry<>(
						ddmFieldsMap.get(key), ddmFieldInfo));

				ddmFieldsMap.remove(key);

				continue;
			}

			String legacyDDMFormFieldName =
				DDMFormFieldUtil.getLegacyDDMFormFieldName(
					ddmFieldInfo._fieldName);

			if (!com.liferay.portal.kernel.util.StringUtil.equals(
					ddmFieldInfo._fieldName, legacyDDMFormFieldName)) {

				key = _getKey(legacyDDMFormFieldName, ddmFieldInfo._instanceId);

				if (ddmFieldsMap.containsKey(key)) {
					ddmFieldEntries.add(
						new AbstractMap.SimpleImmutableEntry<>(
							ddmFieldsMap.get(key), ddmFieldInfo));

					ddmFieldsMap.remove(key);

					continue;
				}
			}

			ddmFieldEntries.add(
				new AbstractMap.SimpleImmutableEntry<>(null, ddmFieldInfo));

			newDDMFieldsCount++;
		}

		for (DDMField ddmField : ddmFieldsMap.values()) {
			ddmFieldEntries.add(
				new AbstractMap.SimpleImmutableEntry<>(ddmField, null));
		}

		List<Map.Entry<DDMFieldAttribute, DDMFieldAttributeInfo>>
			ddmFieldAttributeEntries = new ArrayList<>();

		int newDDMFieldAttributesCount = 0;

		Map<Long, Map<String, DDMFieldAttribute>> ddmFieldsAttributesMap =
			new HashMap<>();

		ListUtil.isNotEmptyForEach(
			_ddmFieldAttributePersistence.findByStorageId(storageId),
			ddmFieldAttribute -> {
				Map<String, DDMFieldAttribute> ddmFieldAttributesMap =
					ddmFieldsAttributesMap.get(ddmFieldAttribute.getFieldId());

				if (ddmFieldAttributesMap == null) {
					ddmFieldAttributesMap = new HashMap<>();

					ddmFieldsAttributesMap.put(
						ddmFieldAttribute.getFieldId(), ddmFieldAttributesMap);
				}

				ddmFieldAttributesMap.put(
					_getKey(
						ddmFieldAttribute.getAttributeName(),
						ddmFieldAttribute.getLanguageId()),
					ddmFieldAttribute);
			});

		for (Map.Entry<DDMField, DDMFieldInfo> ddmFieldEntry :
				ddmFieldEntries) {

			DDMFieldInfo ddmFieldInfo = ddmFieldEntry.getValue();

			if (ddmFieldInfo == null) {
				continue;
			}

			DDMField ddmField = ddmFieldEntry.getKey();

			if (_persistReadOnlyValues(
					ddmField, ddmFieldAttributeEntries, ddmFieldInfoMap,
					ddmFieldsAttributesMap, ddmFormFieldsMap)) {

				continue;
			}

			for (List<DDMFieldAttributeInfo> ddmFieldAttributeInfos :
					ddmFieldInfo._ddmFieldAttributeInfos.values()) {

				for (DDMFieldAttributeInfo ddmFieldAttributeInfo :
						ddmFieldAttributeInfos) {

					if ((ddmField != null) &&
						ddmFieldsAttributesMap.containsKey(
							ddmField.getFieldId())) {

						Map<String, DDMFieldAttribute> ddmFieldAttributesMap =
							ddmFieldsAttributesMap.get(ddmField.getFieldId());

						String key = _getKey(
							ddmFieldAttributeInfo._attributeName,
							ddmFieldAttributeInfo._languageId);

						if (ddmFieldAttributesMap.containsKey(key)) {
							ddmFieldAttributeEntries.add(
								new AbstractMap.SimpleImmutableEntry<>(
									ddmFieldAttributesMap.get(key),
									ddmFieldAttributeInfo));

							ddmFieldAttributesMap.remove(key);

							if (MapUtil.isEmpty(ddmFieldAttributesMap)) {
								ddmFieldsAttributesMap.remove(
									ddmField.getFieldId());
							}

							continue;
						}
					}

					ddmFieldAttributeEntries.add(
						new AbstractMap.SimpleImmutableEntry<>(
							null, ddmFieldAttributeInfo));

					newDDMFieldAttributesCount++;
				}
			}
		}

		for (Map<String, DDMFieldAttribute> ddmFieldAttributesMap :
				ddmFieldsAttributesMap.values()) {

			for (DDMFieldAttribute ddmFieldAttribute :
					ddmFieldAttributesMap.values()) {

				ddmFieldAttributeEntries.add(
					new AbstractMap.SimpleImmutableEntry<>(
						ddmFieldAttribute, null));
			}
		}

		return new DDMFormUpdateContext(
			ddmFieldAttributeEntries, ddmFieldEntries,
			newDDMFieldAttributesCount, newDDMFieldsCount);
	}

	private DDMFormValues _getDDMFormValues(
		List<DDMFieldAttribute> ddmFieldAttributes, List<DDMField> ddmFields,
		DDMForm ddmForm) {

		Map<Long, DDMFieldInfo> ddmFieldInfoMap = new LinkedHashMap<>();

		for (DDMField ddmField : ddmFields) {
			if (ddmField.getParentFieldId() == 0) {
				ddmFieldInfoMap.put(
					ddmField.getFieldId(),
					new DDMFieldInfo(
						ddmField.getFieldName(), ddmField.getInstanceId(),
						ddmField.isLocalizable(), null));
			}
			else {
				DDMFieldInfo parentDDMFieldInfo = ddmFieldInfoMap.get(
					ddmField.getParentFieldId());

				DDMFieldInfo ddmFieldInfo = new DDMFieldInfo(
					ddmField.getFieldName(), ddmField.getInstanceId(),
					ddmField.isLocalizable(), parentDDMFieldInfo._instanceId);

				parentDDMFieldInfo._childDDMFieldInfos.add(ddmFieldInfo);

				ddmFieldInfoMap.put(ddmField.getFieldId(), ddmFieldInfo);
			}
		}

		for (DDMFieldAttribute ddmFieldAttribute : ddmFieldAttributes) {
			DDMFieldInfo ddmFieldInfo = ddmFieldInfoMap.get(
				ddmFieldAttribute.getFieldId());

			List<DDMFieldAttributeInfo> ddmFieldAttributeInfos =
				ddmFieldInfo._ddmFieldAttributeInfos.computeIfAbsent(
					ddmFieldAttribute.getLanguageId(),
					languageId -> new ArrayList<>());

			ddmFieldAttributeInfos.add(
				new DDMFieldAttributeInfo(
					ddmFieldAttribute.getAttributeName(),
					ddmFieldAttribute.getAttributeValue(), ddmFieldInfo,
					ddmFieldAttribute.getLanguageId()));
		}

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		DDMField rootDDMField = null;

		for (DDMField ddmField : ddmFields) {
			if (com.liferay.portal.kernel.util.StringUtil.equals(
					ddmField.getFieldName(), StringPool.BLANK)) {

				rootDDMField = ddmField;

				break;
			}
		}

		if (rootDDMField == null) {
			rootDDMField = ddmFields.get(0);
		}

		DDMFieldInfo rootDDMFieldInfo = ddmFieldInfoMap.remove(
			rootDDMField.getFieldId());

		for (DDMFieldAttributeInfo ddmFieldAttributeInfo :
				rootDDMFieldInfo._ddmFieldAttributeInfos.getOrDefault(
					StringPool.BLANK, Collections.emptyList())) {

			String attributeName = ddmFieldAttributeInfo._attributeName;

			if (Objects.equals(attributeName, "availableLanguageIds")) {
				for (String availableLanguageId :
						StringUtil.split(
							ddmFieldAttributeInfo._attributeValue)) {

					ddmFormValues.addAvailableLocale(
						LocaleUtil.fromLanguageId(availableLanguageId));
				}
			}
			else if (Objects.equals(attributeName, "defaultLanguageId")) {
				ddmFormValues.setDefaultLocale(
					LocaleUtil.fromLanguageId(
						ddmFieldAttributeInfo._attributeValue));
			}
		}

		for (DDMFieldInfo ddmFieldInfo : ddmFieldInfoMap.values()) {
			if (ddmFieldInfo._parentInstanceId == null) {
				ddmFormValues.addDDMFormFieldValue(
					_getDDMFormFieldValue(
						ddmFieldInfo, ddmFormValues.getDefaultLocale()));
			}
		}

		return ddmFormValues;
	}

	private String _getKey(String... parameters) {
		return StringUtil.merge(parameters, StringPool.POUND);
	}

	private String _getValueString(
		List<DDMFieldAttributeInfo> ddmFieldAttributeInfos) {

		if (ddmFieldAttributeInfos.size() == 1) {
			DDMFieldAttributeInfo ddmFieldAttributeInfo =
				ddmFieldAttributeInfos.get(0);

			if (ddmFieldAttributeInfo._attributeName.isEmpty()) {
				return ddmFieldAttributeInfo._attributeValue;
			}
		}

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		for (DDMFieldAttributeInfo ddmFieldAttributeInfo :
				ddmFieldAttributeInfos) {

			jsonObject.put(
				ddmFieldAttributeInfo._attributeName,
				_jsonFactory.looseDeserialize(
					ddmFieldAttributeInfo._attributeValue));
		}

		return jsonObject.toString();
	}

	private boolean _persistReadOnlyValues(
		DDMField ddmField,
		List<Map.Entry<DDMFieldAttribute, DDMFieldAttributeInfo>>
			ddmFieldAttributeEntries,
		Map<String, DDMFieldInfo> ddmFieldInfoMap,
		Map<Long, Map<String, DDMFieldAttribute>> ddmFieldsAttributesMap,
		Map<String, DDMFormField> ddmFormFieldsMap) {

		if ((ddmField == null) ||
			!ddmFieldsAttributesMap.containsKey(ddmField.getFieldId())) {

			return false;
		}

		DDMFormField ddmFormField = ddmFormFieldsMap.get(
			ddmField.getFieldName());

		if ((ddmFormField == null) ||
			!GetterUtil.getBoolean(
				ddmFormField.getProperty("persistReadOnlyValue"))) {

			return false;
		}

		Map<String, DDMFieldAttribute> ddmFieldAttributesMap =
			ddmFieldsAttributesMap.get(ddmField.getFieldId());

		for (DDMFieldAttribute ddmFieldAttribute :
				ddmFieldAttributesMap.values()) {

			ddmFieldAttributeEntries.add(
				new AbstractMap.SimpleImmutableEntry<>(
					ddmFieldAttribute,
					new DDMFieldAttributeInfo(
						ddmFieldAttribute.getAttributeName(),
						ddmFieldAttribute.getAttributeValue(),
						ddmFieldInfoMap.get(ddmField.getInstanceId()),
						ddmFieldAttribute.getLanguageId())));
		}

		ddmFieldsAttributesMap.remove(ddmField.getFieldId());

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDMFieldLocalServiceImpl.class);

	@Reference
	private DDMFieldAttributePersistence _ddmFieldAttributePersistence;

	@Reference
	private DDMStructurePersistence _ddmStructurePersistence;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	private static class DDMFieldAttributeInfo {

		private DDMFieldAttributeInfo(
			String attributeName, String attributeValue,
			DDMFieldInfo ddmFieldInfo, String languageId) {

			_attributeName = attributeName;
			_attributeValue = attributeValue;
			_ddmFieldInfo = ddmFieldInfo;
			_languageId = languageId;
		}

		private final String _attributeName;
		private final String _attributeValue;
		private final DDMFieldInfo _ddmFieldInfo;
		private final String _languageId;

	}

	private static class DDMFieldInfo {

		private DDMFieldInfo(
			String fieldName, String instanceId, boolean localizable,
			String parentInstanceId) {

			_fieldName = fieldName;
			_instanceId = instanceId;
			_localizable = localizable;
			_parentInstanceId = parentInstanceId;
		}

		private final List<DDMFieldInfo> _childDDMFieldInfos =
			new ArrayList<>();
		private final Map<String, List<DDMFieldAttributeInfo>>
			_ddmFieldAttributeInfos = new HashMap<>();
		private final String _fieldName;
		private final String _instanceId;
		private final boolean _localizable;
		private final String _parentInstanceId;

	}

	private static class DDMFormUpdateContext {

		private DDMFormUpdateContext(
			List<Map.Entry<DDMFieldAttribute, DDMFieldAttributeInfo>>
				ddmFieldAttributeEntries,
			List<Map.Entry<DDMField, DDMFieldInfo>> ddmFieldEntries,
			int newDDMFieldAttributesCount, int newDDMFieldsCount) {

			_ddmFieldAttributeEntries = ddmFieldAttributeEntries;
			_ddmFieldEntries = ddmFieldEntries;
			_newDDMFieldAttributesCount = newDDMFieldAttributesCount;
			_newDDMFieldsCount = newDDMFieldsCount;
		}

		private final List<Map.Entry<DDMFieldAttribute, DDMFieldAttributeInfo>>
			_ddmFieldAttributeEntries;
		private final List<Map.Entry<DDMField, DDMFieldInfo>> _ddmFieldEntries;
		private final int _newDDMFieldAttributesCount;
		private final int _newDDMFieldsCount;

	}

}