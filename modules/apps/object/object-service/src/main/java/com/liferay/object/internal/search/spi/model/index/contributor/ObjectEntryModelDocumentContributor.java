/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.search.spi.model.index.contributor;

import com.liferay.account.model.AccountEntryOrganizationRel;
import com.liferay.account.service.AccountEntryOrganizationRelLocalService;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.entry.util.ObjectEntryValuesUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.rest.dto.v1_0.ListEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.FieldArray;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.ml.embedding.text.TextEmbeddingDocumentContributor;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;

import java.io.Serializable;

import java.math.BigDecimal;

import java.text.Format;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * @author Marco Leo
 * @author Brian Wing Shun Chan
 */
public class ObjectEntryModelDocumentContributor
	implements ModelDocumentContributor<ObjectEntry> {

	public ObjectEntryModelDocumentContributor(
		AccountEntryOrganizationRelLocalService
			accountEntryOrganizationRelLocalService,
		String className,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectEntryFolderLocalService objectEntryFolderLocalService,
		ObjectEntryLocalService objectEntryLocalService,
		ObjectFieldLocalService objectFieldLocalService,
		ObjectFolderLocalService objectFolderLocalService,
		TextEmbeddingDocumentContributor textEmbeddingDocumentContributor) {

		_accountEntryOrganizationRelLocalService =
			accountEntryOrganizationRelLocalService;
		_className = className;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectEntryFolderLocalService = objectEntryFolderLocalService;
		_objectEntryLocalService = objectEntryLocalService;
		_objectFieldLocalService = objectFieldLocalService;
		_objectFolderLocalService = objectFolderLocalService;
		_textEmbeddingDocumentContributor = textEmbeddingDocumentContributor;
	}

	@Override
	public void contribute(Document document, ObjectEntry objectEntry) {
		try {
			_contribute(document, objectEntry);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to index object entry " +
						objectEntry.getObjectEntryId(),
					exception);
			}
		}
	}

	private void _addField(
		FieldArray fieldArray, String fieldName, String valueFieldName,
		String value) {

		Field field = new Field("");

		field.addField(new Field("fieldName", fieldName));
		field.addField(new Field("valueFieldName", valueFieldName));
		field.addField(new Field(valueFieldName, value));

		fieldArray.addField(field);
	}

	private void _appendToContent(
		ObjectContentHelper objectContentHelper, String locale,
		String objectFieldName, String valueString) {

		StringBundler sb = new StringBundler(4);

		sb.append(objectFieldName);
		sb.append(": ");
		sb.append(valueString);
		sb.append(StringPool.COMMA_AND_SPACE);

		if (locale != null) {
			objectContentHelper.contributeToLocale(locale, sb);
		}
		else {
			objectContentHelper.contributeToAll(sb);
		}
	}

	private void _contribute(
		Document document, FieldArray fieldArray, String fieldName,
		Object fieldValue, String locale,
		ObjectContentHelper objectContentHelper,
		ObjectDefinition objectDefinition, ObjectEntry objectEntry,
		ObjectField objectField, Map<String, Serializable> values) {

		if (!objectField.isIndexed()) {
			return;
		}

		if (fieldValue == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Object entry ", objectEntry.getObjectEntryId(),
						" has object field \"", objectField.getName(),
						"\" with a null value"));
			}

			return;
		}

		if (StringUtil.equals(
				objectField.getBusinessType(),
				ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT) ||
			StringUtil.equals(
				objectField.getBusinessType(),
				ObjectFieldConstants.BUSINESS_TYPE_RICH_TEXT)) {

			fieldValue = ObjectEntryValuesUtil.getValueString(
				objectField, values);
		}
		else if (StringUtil.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_MULTISELECT_PICKLIST) &&
				 (fieldValue instanceof List)) {

			fieldValue = ListUtil.toString(
				(List)fieldValue, (String)null, StringPool.COMMA_AND_SPACE);
		}
		else if (StringUtil.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_PICKLIST) &&
				 (fieldValue instanceof ListEntry)) {

			ListEntry listEntry = (ListEntry)fieldValue;

			fieldValue = listEntry.getKey();
		}
		else if (StringUtil.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_PRECISION_DECIMAL)) {

			fieldValue = BigDecimalUtil.stripTrailingZeros(
				(BigDecimal)fieldValue);
		}
		else if (Objects.equals(
					objectDefinition.getAccountEntryRestrictedObjectFieldId(),
					objectField.getObjectFieldId())) {

			Long accountEntryId = (Long)fieldValue;

			document.addKeyword(
				"accountEntryRestrictedObjectFieldValue", accountEntryId);

			document.addKeyword(
				"accountEntryRestrictedOrganizationIds",
				TransformUtil.transformToArray(
					_accountEntryOrganizationRelLocalService.
						getAccountEntryOrganizationRels(accountEntryId),
					AccountEntryOrganizationRel::getOrganizationId,
					Long.class));
		}

		String valueString = String.valueOf(fieldValue);

		if (objectField.isIndexedAsKeyword()) {
			_addField(
				fieldArray, fieldName, "value_keyword",
				StringUtil.lowerCase(valueString));

			_appendToContent(
				objectContentHelper, locale, fieldName, valueString);
		}
		else if (fieldValue instanceof BigDecimal) {
			_addField(fieldArray, fieldName, "value_double", valueString);

			_appendToContent(
				objectContentHelper, locale, fieldName, valueString);
		}
		else if (fieldValue instanceof Boolean) {
			_addField(fieldArray, fieldName, "value_boolean", valueString);
			_addField(
				fieldArray, fieldName, "value_keyword",
				_translate((Boolean)fieldValue));

			_appendToContent(
				objectContentHelper, locale, fieldName, valueString);
		}
		else if (fieldValue instanceof Date) {
			_addField(
				fieldArray, fieldName, "value_date",
				_getDateString(fieldValue));

			_appendToContent(
				objectContentHelper, locale, fieldName,
				_getDateString(fieldValue));
		}
		else if (fieldValue instanceof Double) {
			_addField(fieldArray, fieldName, "value_double", valueString);

			_appendToContent(
				objectContentHelper, locale, fieldName, valueString);
		}
		else if (fieldValue instanceof Integer) {
			_addField(fieldArray, fieldName, "value_integer", valueString);

			_appendToContent(
				objectContentHelper, locale, fieldName, valueString);
		}
		else if (fieldValue instanceof Long) {
			_addField(fieldArray, fieldName, "value_long", valueString);

			_appendToContent(
				objectContentHelper, locale, fieldName, valueString);
		}
		else if (fieldValue instanceof String) {
			if (Validator.isBlank(objectField.getIndexedLanguageId())) {
				_addField(fieldArray, fieldName, "value_text", valueString);
			}
			else if (objectField.isLocalized()) {
				_addField(
					fieldArray, fieldName, "value_" + locale, valueString);
			}
			else {
				_addField(
					fieldArray, fieldName,
					"value_" + objectField.getIndexedLanguageId(), valueString);
			}

			_addField(
				fieldArray, fieldName, "value_keyword_lowercase",
				_getSortableValue(valueString));

			_appendToContent(
				objectContentHelper, locale, fieldName, valueString);
		}
		else if (fieldValue instanceof byte[]) {
			_addField(
				fieldArray, fieldName, "value_binary",
				Base64.encode((byte[])fieldValue));
		}
		else {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Object entry ", objectEntry.getObjectEntryId(),
						" has object field \"", fieldName,
						"\" with unsupported value ", fieldValue));
			}
		}
	}

	private void _contribute(Document document, ObjectEntry objectEntry)
		throws Exception {

		if (_log.isDebugEnabled()) {
			_log.debug("Document " + document);
			_log.debug("Object entry " + objectEntry);
		}

		document.add(
			new Field(
				Field.getSortableFieldName(Field.ENTRY_CLASS_PK),
				document.get(Field.ENTRY_CLASS_PK)));

		FieldArray fieldArray = (FieldArray)document.getField(
			"nestedFieldArray");

		if (fieldArray == null) {
			fieldArray = new FieldArray("nestedFieldArray");

			document.add(fieldArray);
		}

		document.addKeyword(
			"objectDefinitionId", objectEntry.getObjectDefinitionId());

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				objectEntry.getObjectDefinitionId());

		document.addKeyword(
			"objectDefinitionName", objectDefinition.getShortName());

		Map<String, Serializable> values = objectEntry.getValues();

		List<ObjectField> objectFields =
			_objectFieldLocalService.getObjectFields(
				objectEntry.getObjectDefinitionId(), false);

		ObjectContentHelper objectContentHelper = new ObjectContentHelper(
			objectDefinition.isEnableLocalization(), objectEntry, objectFields,
			_textEmbeddingDocumentContributor);

		for (ObjectField objectField : objectFields) {
			if (objectField.isLocalized()) {
				Map<String, Object> localizedValues =
					(Map<String, Object>)values.get(
						objectField.getI18nObjectFieldName());

				if (MapUtil.isEmpty(localizedValues)) {
					continue;
				}

				for (Map.Entry<String, Object> localeMap :
						localizedValues.entrySet()) {

					_contribute(
						document, fieldArray, objectField.getName(),
						localizedValues.get(localeMap.getKey()),
						LocaleUtil.fromLanguageId(
							localeMap.getKey(), true, false
						).toString(),
						objectContentHelper, objectDefinition, objectEntry,
						objectField, values);
				}
			}
			else {
				_contribute(
					document, fieldArray, objectField.getName(),
					values.get(objectField.getName()), null,
					objectContentHelper, objectDefinition, objectEntry,
					objectField, values);
			}
		}

		objectContentHelper.trim();

		document.add(
			new Field("objectEntryContent", objectContentHelper.getContent()));

		objectContentHelper.getLocalizedContentMap();

		document.addKeyword("objectEntryId", objectEntry.getObjectEntryId());
		document.add(
			new Field("objectEntryTitle", objectEntry.getTitleValue()));

		ObjectFolder objectFolder = _objectFolderLocalService.getObjectFolder(
			objectDefinition.getObjectFolderId());

		document.addKeyword(
			"objectFolderExternalReferenceCode",
			objectFolder.getExternalReferenceCode(), true);

		if (FeatureFlagManagerUtil.isEnabled(
				objectEntry.getCompanyId(), "LPD-17564")) {

			_contributeObjectEntryFolder(
				document, objectEntry.getObjectEntryFolderId());
		}

		if (FeatureFlagManagerUtil.isEnabled(
				objectEntry.getCompanyId(), "LPS-122920")) {

			_contributeTextEmbeddings(
				document, objectContentHelper, objectDefinition, objectEntry);
		}
	}

	private void _contributeObjectEntryFolder(
		Document document, long objectEntryFolderId) {

		document.addKeyword(Field.FOLDER_ID, objectEntryFolderId);

		ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderLocalService.fetchObjectEntryFolder(
				objectEntryFolderId);

		if (objectEntryFolder == null) {
			return;
		}

		ObjectEntryFolder rootObjectEntryFolder = _getRootObjectEntryFolder(
			objectEntryFolder);

		if (rootObjectEntryFolder == null) {
			return;
		}

		String cmsSection = _getCMSSection(
			rootObjectEntryFolder.getExternalReferenceCode());

		if (cmsSection == null) {
			return;
		}

		document.addKeyword("cms_kind", "object");
		document.addKeyword(
			"cms_root",
			rootObjectEntryFolder.getObjectEntryFolderId() ==
				objectEntryFolderId);
		document.addKeyword("cms_section", cmsSection);
	}

	private void _contributeTextEmbeddings(
		Document document, ObjectContentHelper objectContentHelper,
		ObjectDefinition objectDefinition, ObjectEntry objectEntry) {

		if (!objectDefinition.isEnableLocalization()) {
			_textEmbeddingDocumentContributor.contribute(
				document, objectEntry, objectContentHelper.getContent());

			return;
		}

		Map<String, String> localizedContentMap =
			objectContentHelper.getLocalizedContentMap();

		for (Map.Entry<String, String> localizedContent :
				localizedContentMap.entrySet()) {

			_textEmbeddingDocumentContributor.contribute(
				document, localizedContent.getKey(), objectEntry,
				localizedContent.getValue());
		}
	}

	private String _getCMSSection(String externalReferenceCode) {
		if (externalReferenceCode.equals(
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS)) {

			return "contents";
		}

		if (externalReferenceCode.equals(
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES)) {

			return "files";
		}

		return null;
	}

	private String _getDateString(Object value) {
		return _format.format(value);
	}

	private ObjectEntryFolder _getRootObjectEntryFolder(
		ObjectEntryFolder objectEntryFolder) {

		if (objectEntryFolder == null) {
			return null;
		}

		if (Objects.equals(
				objectEntryFolder.getExternalReferenceCode(),
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS) ||
			Objects.equals(
				objectEntryFolder.getExternalReferenceCode(),
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES)) {

			return objectEntryFolder;
		}

		String[] parts = StringUtil.split(
			objectEntryFolder.getTreePath(), CharPool.SLASH);

		if (parts.length <= 2) {
			return null;
		}

		return _objectEntryFolderLocalService.fetchObjectEntryFolder(
			GetterUtil.getLong(parts[1]));
	}

	private String _getSortableValue(String value) {
		if (value.length() > 256) {
			return value.substring(0, 256);
		}

		return value;
	}

	private String _translate(Boolean value) {
		if (value.booleanValue()) {
			return "yes";
		}

		return "no";
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectEntryModelDocumentContributor.class);

	private static final Format _format =
		FastDateFormatFactoryUtil.getSimpleDateFormat("yyyyMMddHHmmss");

	private final AccountEntryOrganizationRelLocalService
		_accountEntryOrganizationRelLocalService;
	private final String _className;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectEntryFolderLocalService _objectEntryFolderLocalService;
	private final ObjectEntryLocalService _objectEntryLocalService;
	private final ObjectFieldLocalService _objectFieldLocalService;
	private final ObjectFolderLocalService _objectFolderLocalService;
	private final TextEmbeddingDocumentContributor
		_textEmbeddingDocumentContributor;

	private static class ObjectContentHelper {

		public void contributeToAll(StringBundler sb) {
			_contentSB.append(sb);

			for (StringBundler localizedContentSB :
					_localizedContentSBMap.values()) {

				localizedContentSB.append(sb);
			}
		}

		public void contributeToLocale(String locale, StringBundler sb) {
			_contentSB.append(sb);

			StringBundler localizedContentSB = _localizedContentSBMap.get(
				locale);

			if (localizedContentSB != null) {
				localizedContentSB.append(sb);
			}
		}

		public String getContent() {
			return _contentSB.toString();
		}

		public Map<String, String> getLocalizedContentMap() {
			if (_localizedContentSBMap.isEmpty()) {
				return Collections.emptyMap();
			}

			Map<String, String> localizedContentMap = new TreeMap<>();

			for (Map.Entry<String, StringBundler> localizedContentEntry :
					_localizedContentSBMap.entrySet()) {

				StringBundler sb = localizedContentEntry.getValue();

				if (sb.index() > 0) {
					localizedContentMap.put(
						localizedContentEntry.getKey(), sb.toString());
				}
			}

			return localizedContentMap;
		}

		public void trim() {
			if (_contentSB.index() > 0) {
				_contentSB.setIndex(_contentSB.index() - 1);
			}

			for (StringBundler localizedContentSB :
					_localizedContentSBMap.values()) {

				if (localizedContentSB.index() > 0) {
					localizedContentSB.setIndex(localizedContentSB.index() - 1);
				}
			}
		}

		private ObjectContentHelper(
			boolean localizationEnabled, ObjectEntry objectEntry,
			List<ObjectField> objectFields,
			TextEmbeddingDocumentContributor textEmbeddingDocumentContributor) {

			_contentSB = new StringBundler(objectFields.size());

			if (!localizationEnabled ||
				!FeatureFlagManagerUtil.isEnabled(
					objectEntry.getCompanyId(), "LPS-122920")) {

				return;
			}

			for (String languageId :
					textEmbeddingDocumentContributor.getLanguageIds(
						objectEntry)) {

				_localizedContentSBMap.put(
					languageId, new StringBundler(objectFields.size() * 4));
			}
		}

		private final StringBundler _contentSB;
		private final Map<String, StringBundler> _localizedContentSBMap =
			new TreeMap<>();

	}

}