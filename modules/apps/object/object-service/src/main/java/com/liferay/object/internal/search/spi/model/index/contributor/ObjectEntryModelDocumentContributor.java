/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.search.spi.model.index.contributor;

import com.liferay.account.model.AccountEntryOrganizationRel;
import com.liferay.account.model.AccountEntryOrganizationRelTable;
import com.liferay.account.service.AccountEntryOrganizationRelLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryTable;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalServiceUtil;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.model.bag.ObjectFieldBag;
import com.liferay.object.rest.dto.v1_0.ListEntry;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.base.BaseTable;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.FieldArray;
import com.liferay.portal.kernel.search.ReindexCacheThreadLocal;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlParserUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.ml.embedding.text.TextEmbeddingDocumentContributor;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;

import java.io.Serializable;

import java.math.BigDecimal;

import java.sql.Timestamp;
import java.sql.Types;

import java.text.Format;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
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
		DLFileEntryLocalService dlFileEntryLocalService,
		ObjectEntryFolderLocalService objectEntryFolderLocalService,
		TextEmbeddingDocumentContributor textEmbeddingDocumentContributor) {

		_accountEntryOrganizationRelLocalService =
			accountEntryOrganizationRelLocalService;
		_dlFileEntryLocalService = dlFileEntryLocalService;
		_objectEntryFolderLocalService = objectEntryFolderLocalService;
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
		ObjectField objectField) {

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
				ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT)) {

			fieldValue = _getFileName(
				GetterUtil.getLong(fieldValue), objectDefinition);
		}
		else if (StringUtil.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_RICH_TEXT)) {

			fieldValue = HtmlParserUtil.extractText(
				GetterUtil.getString(fieldValue));
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
				_getOrganizationIds(accountEntryId));
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

		ObjectDefinition objectDefinition = objectEntry.getObjectDefinition();

		document.addKeyword(
			"objectDefinitionExternalReferenceCode",
			objectDefinition.getExternalReferenceCode());
		document.addKeyword(
			"objectDefinitionName", objectDefinition.getShortName());

		ObjectFieldBag objectFieldBag = objectDefinition.getObjectFieldBag();

		List<ObjectField> objectFields = null;

		if (objectDefinition.isModifiableAndSystem()) {
			objectFields = ListUtil.filter(
				objectFieldBag.getIndexedObjectFields(),
				objectField -> !objectField.isMetadata());
		}
		else {
			objectFields = objectFieldBag.getNonsystemIndexedObjectFields();
		}

		ObjectContentHelper objectContentHelper = null;
		Map<String, Serializable> values = null;

		if (!objectFields.isEmpty()) {
			values = objectEntry.getIndexedValues();

			objectContentHelper = new ObjectContentHelper(
				objectEntry, objectFields, _textEmbeddingDocumentContributor);

			for (ObjectField objectField : objectFields) {
				if (objectField.isLocalized()) {
					Map<String, Object> localizedValues =
						(Map<String, Object>)values.get(
							objectField.getI18nObjectFieldName());

					if (MapUtil.isEmpty(localizedValues)) {
						continue;
					}

					for (Map.Entry<String, Object> entry :
							localizedValues.entrySet()) {

						_contribute(
							document, fieldArray, objectField.getName(),
							entry.getValue(), entry.getKey(),
							objectContentHelper, objectDefinition, objectEntry,
							objectField);
					}
				}
				else {
					_contribute(
						document, fieldArray, objectField.getName(),
						values.get(objectField.getName()), null,
						objectContentHelper, objectDefinition, objectEntry,
						objectField);
				}
			}

			objectContentHelper.trim();

			document.add(
				new Field(
					"objectEntryContent", objectContentHelper.getContent()));
		}

		document.addKeyword("objectEntryId", objectEntry.getObjectEntryId());
		document.add(
			new Field("objectEntryTitle", objectEntry.getTitleValue()));

		ObjectFolder objectFolder = objectDefinition.getObjectFolder();

		document.addKeyword(
			"objectFolderExternalReferenceCode",
			objectFolder.getExternalReferenceCode(), true);

		if (FeatureFlagManagerUtil.isEnabled(
				objectEntry.getCompanyId(), "LPD-17564")) {

			_contributeObjectEntryFolder(
				document, objectEntry.getObjectEntryFolderId());

			if (values == null) {
				values = objectEntry.getIndexedValues();
			}

			long fileEntryId = GetterUtil.getLong(values.get("file"));

			if (fileEntryId != 0) {
				_contributeFile(document, fileEntryId);
			}
		}

		if (objectDefinition.isCMP()) {
			if (values == null) {
				values = objectEntry.getIndexedValues();
			}

			Map<String, Long> assigneeMap = (Map<String, Long>)values.get(
				"assignTo");

			if (assigneeMap != null) {
				document.addKeyword(
					"cmpAssignTo",
					StringBundler.concat(
						assigneeMap.get("classNameId"), StringPool.UNDERLINE,
						assigneeMap.get("classPK")));
			}

			document.addDate("cmpDueDate", (Timestamp)values.get("dueDate"));
			document.addKeyword(
				"cmpProjectManagerUserId",
				MapUtil.getLong(values, "r_userToCMPProjectManager_userId"));
			document.addKeyword(
				"cmpProjectSponsorUserId",
				MapUtil.getLong(values, "r_userToCMPProjectSponsor_userId"));
			document.addKeyword("cmpState", MapUtil.getString(values, "state"));
			document.addKeyword(
				"cmpTaskCMPProjectId",
				MapUtil.getLong(
					values, "r_cmpProjectToCMPTasks_c_cmpProjectId"));
		}

		_contributeTextEmbeddings(document, objectContentHelper, objectEntry);
	}

	private void _contributeFile(Document document, long fileEntryId) {
		DLFileEntry fileEntry = DLFileEntryLocalServiceUtil.fetchDLFileEntry(
			fileEntryId);

		if (fileEntry != null) {
			document.addKeyword("extension", fileEntry.getExtension());
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
		ObjectEntry objectEntry) {

		if (objectContentHelper == null) {
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

	private String _getFileName(
		long dlFileEntryId, ObjectDefinition objectDefinition) {

		if (dlFileEntryId == 0) {
			return StringPool.BLANK;
		}

		Map<Long, String> fileNames =
			ReindexCacheThreadLocal.getScopeReindexCache(
				ObjectEntryModelDocumentContributor.class.getName() +
					"#_getFileName",
				String.valueOf(objectDefinition.getObjectDefinitionId()),
				() -> -1, () -> -1,
				count -> {
					Map<Long, String> localFileNames = new HashMap<>();

					ObjectFieldBag objectFieldBag =
						objectDefinition.getObjectFieldBag();

					for (ObjectField objectField :
							ListUtil.filter(
								objectFieldBag.getIndexedObjectFields(),
								objectField -> objectField.compareBusinessType(
									ObjectFieldConstants.
										BUSINESS_TYPE_ATTACHMENT))) {

						ObjectFieldTable objectFieldTable =
							new ObjectFieldTable(objectField);

						for (Object[] values :
								_dlFileEntryLocalService.
									<List<Object[]>>dslQuery(
										objectFieldTable.buildDSLQuery(),
										false)) {

							localFileNames.put(
								(Long)values[0], (String)values[1]);
						}
					}

					return localFileNames;
				});

		if (fileNames == null) {
			DLFileEntry dlFileEntry =
				DLFileEntryLocalServiceUtil.fetchDLFileEntry(dlFileEntryId);

			if (dlFileEntry != null) {
				return dlFileEntry.getFileName();
			}

			return StringPool.BLANK;
		}

		return fileNames.getOrDefault(dlFileEntryId, StringPool.BLANK);
	}

	private long[] _getOrganizationIds(Long accountEntryId) {
		Map<Long, long[]> organizationIdsMap =
			ReindexCacheThreadLocal.getGlobalReindexCache(
				() -> -1,
				ObjectEntryModelDocumentContributor.class.getName() +
					"#_getOrganizationIds",
				count -> {
					Map<Long, List<Long>> organizationIdListMap =
						new HashMap<>();

					for (Object[] values :
							_accountEntryOrganizationRelLocalService.
								<List<Object[]>>dslQuery(
									DSLQueryFactoryUtil.select(
										AccountEntryOrganizationRelTable.
											INSTANCE.accountEntryId,
										AccountEntryOrganizationRelTable.
											INSTANCE.organizationId
									).from(
										AccountEntryOrganizationRelTable.
											INSTANCE
									),
									false)) {

						List<Long> organizationIds =
							organizationIdListMap.computeIfAbsent(
								(Long)values[0], key -> new ArrayList<>());

						organizationIds.add((Long)values[1]);
					}

					Map<Long, long[]> localOrganizationIdsMap = new HashMap<>();

					for (Map.Entry<Long, List<Long>> entry :
							organizationIdListMap.entrySet()) {

						localOrganizationIdsMap.put(
							entry.getKey(),
							ArrayUtil.toLongArray(entry.getValue()));
					}

					return localOrganizationIdsMap;
				});

		if (organizationIdsMap == null) {
			return ListUtil.toLongArray(
				_accountEntryOrganizationRelLocalService.
					getAccountEntryOrganizationRels(accountEntryId),
				AccountEntryOrganizationRel::getOrganizationId);
		}

		return organizationIdsMap.get(accountEntryId);
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
	private final DLFileEntryLocalService _dlFileEntryLocalService;
	private final ObjectEntryFolderLocalService _objectEntryFolderLocalService;
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
			ObjectEntry objectEntry, List<ObjectField> objectFields,
			TextEmbeddingDocumentContributor textEmbeddingDocumentContributor) {

			_contentSB = new StringBundler(objectFields.size());

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

	private static class ObjectFieldTable extends BaseTable<ObjectFieldTable> {

		public DSLQuery buildDSLQuery() {
			return DSLQueryFactoryUtil.select(
				DLFileEntryTable.INSTANCE.fileEntryId,
				DLFileEntryTable.INSTANCE.fileName
			).from(
				DLFileEntryTable.INSTANCE
			).innerJoinON(
				this, DLFileEntryTable.INSTANCE.fileEntryId.eq(_column)
			);
		}

		private ObjectFieldTable(ObjectField objectField) {
			super(objectField.getDBTableName(), () -> null);

			_column = createColumn(
				objectField.getDBColumnName(), Long.class, Types.BIGINT,
				Column.FLAG_DEFAULT);
		}

		private final Column<ObjectFieldTable, Long> _column;

	}

}