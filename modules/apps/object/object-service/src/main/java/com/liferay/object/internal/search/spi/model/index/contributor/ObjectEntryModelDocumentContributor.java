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
import com.liferay.object.field.business.type.ObjectFieldBusinessTypeRegistry;
import com.liferay.object.internal.field.business.type.AssigneeObjectFieldBusinessType;
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
import com.liferay.portal.search.ml.embedding.text.helper.TextEmbeddingContentHelper;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;

import java.io.Serializable;

import java.math.BigDecimal;

import java.sql.Timestamp;
import java.sql.Types;

import java.text.Format;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
		ObjectFieldBusinessTypeRegistry objectFieldBusinessTypeRegistry,
		TextEmbeddingDocumentContributor textEmbeddingDocumentContributor) {

		_accountEntryOrganizationRelLocalService =
			accountEntryOrganizationRelLocalService;
		_dlFileEntryLocalService = dlFileEntryLocalService;
		_objectEntryFolderLocalService = objectEntryFolderLocalService;
		_objectFieldBusinessTypeRegistry = objectFieldBusinessTypeRegistry;
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

	private void _addMultiValuedKeywordField(
		FieldArray fieldArray, String fieldName, String[] values) {

		if (ArrayUtil.isEmpty(values)) {
			return;
		}

		Field field = new Field("");

		field.addField(new Field("fieldName", fieldName));
		field.addField(new Field("valueFieldName", "value_keyword"));

		String[] keywordValues = new String[values.length];

		for (int i = 0; i < values.length; i++) {
			keywordValues[i] = StringUtil.lowerCase(values[i]);
		}

		field.addField(new Field("value_keyword", keywordValues));

		fieldArray.addField(field);
	}

	private void _addTitleFields(
			Document document, ObjectDefinition objectDefinition,
			ObjectEntry objectEntry)
		throws Exception {

		ObjectFieldBag objectFieldBag = objectDefinition.getObjectFieldBag();

		ObjectField titleObjectField = objectFieldBag.getObjectField(
			objectDefinition.getTitleObjectFieldId());

		if ((titleObjectField == null) || !titleObjectField.isLocalized()) {
			document.add(
				new Field("objectEntryTitle", objectEntry.getTitleValue()));

			return;
		}

		Map<String, Serializable> values = objectEntry.getIndexedValues();

		Map<String, Object> localizedValues = (Map<String, Object>)values.get(
			titleObjectField.getI18nObjectFieldName());

		if (MapUtil.isEmpty(localizedValues)) {
			String titleValue = objectEntry.getTitleValue();

			if (!Validator.isBlank(titleValue)) {
				document.add(
					new Field(
						Field.getLocalizedName(
							objectEntry.getDefaultLanguageId(),
							"objectEntryTitle"),
						titleValue));
			}

			return;
		}

		for (Map.Entry<String, Object> entry : localizedValues.entrySet()) {
			Object value = entry.getValue();

			if (value == null) {
				continue;
			}

			String titleValue = GetterUtil.getString(value);

			if (Validator.isBlank(titleValue)) {
				continue;
			}

			document.add(
				new Field(
					Field.getLocalizedName(entry.getKey(), "objectEntryTitle"),
					titleValue));
		}
	}

	private void _appendToContent(
		String locale, String objectFieldName,
		TextEmbeddingContentHelper<ObjectEntry> textEmbeddingContentHelper,
		String valueString) {

		String content = StringBundler.concat(
			objectFieldName, ": ", valueString);

		if (locale != null) {
			textEmbeddingContentHelper.append(locale, content);
		}
		else {
			textEmbeddingContentHelper.append(content);
		}
	}

	private void _contribute(
		Document document, FieldArray fieldArray, String fieldName,
		Object fieldValue, String locale, ObjectDefinition objectDefinition,
		ObjectEntry objectEntry, ObjectField objectField,
		TextEmbeddingContentHelper<ObjectEntry> textEmbeddingContentHelper) {

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
				ObjectFieldConstants.BUSINESS_TYPE_ASSIGNEE) &&
			(fieldValue instanceof Map)) {

			Map<String, Long> assigneeMap = (Map<String, Long>)fieldValue;

			long classNameId = MapUtil.getLong(assigneeMap, "classNameId");
			long classPK = MapUtil.getLong(assigneeMap, "classPK");

			fieldValue = StringBundler.concat(
				classNameId, StringPool.UNDERLINE, classPK);

			AssigneeObjectFieldBusinessType assigneeObjectFieldBusinessType =
				(AssigneeObjectFieldBusinessType)
					_objectFieldBusinessTypeRegistry.getObjectFieldBusinessType(
						ObjectFieldConstants.BUSINESS_TYPE_ASSIGNEE);

			if (assigneeObjectFieldBusinessType != null) {
				String assigneeName =
					assigneeObjectFieldBusinessType.getDisplayName(
						classNameId, classPK);

				if (Validator.isNotNull(assigneeName)) {
					_addField(
						fieldArray, fieldName, "value_text", assigneeName);

					_appendToContent(
						locale, fieldName, textEmbeddingContentHelper,
						assigneeName);
				}
			}
		}
		else if (StringUtil.equals(
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
					ObjectFieldConstants.BUSINESS_TYPE_MULTISELECT_PICKLIST)) {

			String valueString = null;

			if (fieldValue instanceof List) {
				valueString = ListUtil.toString(
					(List)fieldValue, (String)null, StringPool.COMMA_AND_SPACE);
			}
			else {
				valueString = String.valueOf(fieldValue);
			}

			_addMultiValuedKeywordField(
				fieldArray, fieldName,
				StringUtil.split(valueString, StringPool.COMMA_AND_SPACE));

			if (objectField.isIndexedAsKeyword()) {
				_appendToContent(
					locale, fieldName, textEmbeddingContentHelper, valueString);

				return;
			}

			fieldValue = valueString;
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
				locale, fieldName, textEmbeddingContentHelper, valueString);
		}
		else if (fieldValue instanceof BigDecimal) {
			_addField(fieldArray, fieldName, "value_double", valueString);

			_appendToContent(
				locale, fieldName, textEmbeddingContentHelper, valueString);
		}
		else if (fieldValue instanceof Boolean) {
			_addField(fieldArray, fieldName, "value_boolean", valueString);
			_addField(
				fieldArray, fieldName, "value_keyword",
				_translate((Boolean)fieldValue));

			_appendToContent(
				locale, fieldName, textEmbeddingContentHelper, valueString);
		}
		else if (fieldValue instanceof Date) {
			_addField(
				fieldArray, fieldName, "value_date",
				_getDateString(fieldValue));

			_appendToContent(
				locale, fieldName, textEmbeddingContentHelper,
				_getDateString(fieldValue));
		}
		else if (fieldValue instanceof Double) {
			_addField(fieldArray, fieldName, "value_double", valueString);

			_appendToContent(
				locale, fieldName, textEmbeddingContentHelper, valueString);
		}
		else if (fieldValue instanceof Integer) {
			_addField(fieldArray, fieldName, "value_integer", valueString);

			_appendToContent(
				locale, fieldName, textEmbeddingContentHelper, valueString);
		}
		else if (fieldValue instanceof Long) {
			_addField(fieldArray, fieldName, "value_long", valueString);

			_appendToContent(
				locale, fieldName, textEmbeddingContentHelper, valueString);
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
					"value_" + objectEntry.getDefaultLanguageId(), valueString);
			}

			_addField(
				fieldArray, fieldName, "value_keyword_lowercase",
				_getSortableValue(valueString));

			_appendToContent(
				locale, fieldName, textEmbeddingContentHelper, valueString);
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

		document.addText(
			Field.DEFAULT_LANGUAGE_ID, objectEntry.getDefaultLanguageId());
		document.add(
			new Field(
				Field.getSortableFieldName(Field.ENTRY_CLASS_PK),
				document.get(Field.ENTRY_CLASS_PK)));

		ObjectDefinition objectDefinition = objectEntry.getObjectDefinition();

		if (objectDefinition == null) {
			return;
		}

		document.addDate(Field.DISPLAY_DATE, objectEntry.getDisplayDate());
		document.addDate(
			Field.EXPIRATION_DATE, objectEntry.getExpirationDate());
		document.addDate(Field.REVIEW_DATE, objectEntry.getReviewDate());

		FieldArray fieldArray = (FieldArray)document.getField(
			"nestedFieldArray");

		if (fieldArray == null) {
			fieldArray = new FieldArray("nestedFieldArray");

			document.add(fieldArray);
		}

		document.addKeyword(
			"objectDefinitionExternalReferenceCode",
			objectDefinition.getExternalReferenceCode(), true);
		document.addKeyword(
			"objectDefinitionId", objectEntry.getObjectDefinitionId());
		document.addKeyword(
			"objectDefinitionName", objectDefinition.getShortName());

		ObjectFieldBag objectFieldBag = objectDefinition.getObjectFieldBag();

		List<ObjectField> nestedIndexedObjectFields =
			objectFieldBag.getNestedIndexedObjectFields();

		TextEmbeddingContentHelper<ObjectEntry> textEmbeddingContentHelper =
			new TextEmbeddingContentHelper<>(
				objectEntry.getCompanyId(), objectEntry.getDefaultLanguageId(),
				StringPool.COMMA_AND_SPACE, objectEntry,
				nestedIndexedObjectFields.size(),
				_textEmbeddingDocumentContributor);

		Map<String, Serializable> values = null;

		if (!nestedIndexedObjectFields.isEmpty()) {
			values = objectEntry.getIndexedValues();

			for (ObjectField objectField : nestedIndexedObjectFields) {
				if (StringUtil.equals(
						objectField.getBusinessType(),
						ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT)) {

					long fileEntryId = GetterUtil.getLong(
						values.get(objectField.getName()));

					if (fileEntryId != 0) {
						_contributeFile(document, fileEntryId);
					}
				}

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
							entry.getValue(), entry.getKey(), objectDefinition,
							objectEntry, objectField,
							textEmbeddingContentHelper);
					}
				}
				else {
					_contribute(
						document, fieldArray, objectField.getName(),
						values.get(objectField.getName()), null,
						objectDefinition, objectEntry, objectField,
						textEmbeddingContentHelper);
				}
			}

			Map<String, String> localizedContentMap =
				textEmbeddingContentHelper.getLocalizedContentMap();

			for (Map.Entry<String, String> entry :
					localizedContentMap.entrySet()) {

				document.add(
					new Field(
						Field.getLocalizedName(
							entry.getKey(), "objectEntryContent"),
						entry.getValue()));
			}

			if (localizedContentMap.isEmpty()) {
				document.add(
					new Field(
						"objectEntryContent",
						textEmbeddingContentHelper.getNonlocalizedContent()));
			}
		}

		document.addKeyword("objectEntryId", objectEntry.getObjectEntryId());

		_addTitleFields(document, objectDefinition, objectEntry);

		ObjectFolder objectFolder = objectDefinition.getObjectFolder();

		document.addKeyword(
			"objectFolderExternalReferenceCode",
			objectFolder.getExternalReferenceCode(), true);

		document.addKeyword(
			"rootDescendantNode", objectEntry.isRootDescendantNode());

		_contributeObjectEntryFolder(
			document, objectEntry.getObjectEntryFolderId());

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

		textEmbeddingContentHelper.contribute(document);
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

		document.addKeyword(
			Field.TREE_PATH,
			StringUtil.split(objectEntryFolder.getTreePath(), CharPool.SLASH));

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

		document.addKeyword(
			"cms_root",
			rootObjectEntryFolder.getObjectEntryFolderId() ==
				objectEntryFolderId);
		document.addKeyword("cms_section", cmsSection);
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

						String dbTableName = objectField.getDBTableName();

						if (objectField.isLocalized()) {
							dbTableName =
								objectDefinition.getLocalizationDBTableName();
						}

						ObjectFieldTable objectFieldTable =
							new ObjectFieldTable(dbTableName, objectField);

						try {
							for (Object[] values :
									_dlFileEntryLocalService.
										<List<Object[]>>dslQuery(
											objectFieldTable.buildDSLQuery(),
											false)) {

								localFileNames.put(
									(Long)values[0], (String)values[1]);
							}
						}
						catch (Exception exception) {
							if (_log.isWarnEnabled()) {
								_log.warn(
									"Unable to get file names for object " +
										"field " + objectField.getName(),
									exception);
							}
						}
					}

					return localFileNames;
				});

		if (fileNames != null) {
			String fileName = fileNames.get(dlFileEntryId);

			if (fileName != null) {
				return fileName;
			}
		}

		DLFileEntry dlFileEntry = DLFileEntryLocalServiceUtil.fetchDLFileEntry(
			dlFileEntryId);

		if (dlFileEntry != null) {
			return dlFileEntry.getFileName();
		}

		return StringPool.BLANK;
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
	private final ObjectFieldBusinessTypeRegistry
		_objectFieldBusinessTypeRegistry;
	private final TextEmbeddingDocumentContributor
		_textEmbeddingDocumentContributor;

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

		private ObjectFieldTable(String dbTableName, ObjectField objectField) {
			super(dbTableName, () -> null);

			_column = createColumn(
				objectField.getDBColumnName(), Long.class, Types.BIGINT,
				Column.FLAG_DEFAULT);
		}

		private final Column<ObjectFieldTable, Long> _column;

	}

}