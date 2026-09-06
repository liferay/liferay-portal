/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.info.item.provider.util;

import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.field.type.ActionInfoFieldType;
import com.liferay.info.field.type.ImageInfoFieldType;
import com.liferay.info.field.type.TextInfoFieldType;
import com.liferay.info.field.type.URLInfoFieldType;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.info.type.KeyLocalizedLabelPair;
import com.liferay.info.type.WebImage;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.field.setting.util.ObjectFieldSettingUtil;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.info.field.converter.ObjectFieldInfoFieldConverter;
import com.liferay.object.info.item.ObjectEntryInfoItemFields;
import com.liferay.object.info.item.util.ObjectEntryInfoItemUtil;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.related.models.ObjectRelatedModelsProvider;
import com.liferay.object.related.models.ObjectRelatedModelsProviderRegistry;
import com.liferay.object.rest.dto.v1_0.ListEntry;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.GroupThreadLocal;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Carolina Barbosa
 */
public class ObjectEntryInfoItemValuesProviderUtil {

	public static InfoLocalizedValue<?> getFriendlyURLInfoFieldValue(
		long classNameId,
		FriendlyURLEntryLocalService friendlyURLEntryLocalService,
		long objectEntryId) {

		try {
			FriendlyURLEntry friendlyURLEntry =
				friendlyURLEntryLocalService.getMainFriendlyURLEntry(
					classNameId, objectEntryId);

			if (friendlyURLEntry == null) {
				return null;
			}

			return InfoLocalizedValue.function(
				locale -> friendlyURLEntry.getUrlTitle(
					LocaleUtil.toLanguageId(locale)));
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return null;
	}

	public static List<InfoFieldValue<Object>> getInfoFieldValues(
			String defaultLanguageId, DLAppLocalService dlAppLocalService,
			DLURLHelper dlURLHelper,
			FriendlyURLEntryLocalService friendlyURLEntryLocalService,
			ListTypeEntryLocalService listTypeEntryLocalService,
			ObjectActionLocalService objectActionLocalService,
			ObjectDefinition objectDefinition,
			ObjectDefinitionLocalService objectDefinitionLocalService,
			ObjectEntryLocalService objectEntryLocalService,
			ObjectEntryManagerRegistry objectEntryManagerRegistry,
			ObjectEntryService objectEntryService,
			ObjectFieldInfoFieldConverter objectFieldInfoFieldConverter,
			ObjectFieldLocalService objectFieldLocalService,
			List<ObjectField> objectFields,
			ObjectRelationshipLocalService objectRelationshipLocalService,
			ObjectScopeProviderRegistry objectScopeProviderRegistry,
			Portal portal,
			com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry,
			ThemeDisplay themeDisplay, Map<String, Object> values)
		throws Exception {

		List<InfoFieldValue<Object>> infoFieldValues = new ArrayList<>();

		for (ObjectField objectField : objectFields) {
			if (objectField.isMetadata()) {
				continue;
			}

			Object value = values.get(objectField.getName());

			if (objectField.isLocalized()) {
				value = values.get(objectField.getI18nObjectFieldName());
			}

			_addInfoFieldValue(
				defaultLanguageId, dlAppLocalService, dlURLHelper,
				infoFieldValues, listTypeEntryLocalService, objectDefinition,
				objectEntryLocalService, objectEntryService, objectField,
				objectFieldInfoFieldConverter,
				ObjectField.class.getSimpleName(),
				objectRelationshipLocalService, serviceBuilderObjectEntry,
				themeDisplay, value);

			if (!objectField.compareBusinessType(
					ObjectFieldConstants.BUSINESS_TYPE_RELATIONSHIP)) {

				continue;
			}

			ObjectRelationship objectRelationship =
				objectRelationshipLocalService.
					fetchObjectRelationshipByObjectFieldId2(
						objectField.getObjectFieldId());

			ObjectDefinition parentObjectDefinition =
				objectDefinitionLocalService.getObjectDefinition(
					objectRelationship.getObjectDefinitionId1());

			com.liferay.object.model.ObjectEntry
				serviceBuilderRelatedObjectEntry =
					objectEntryLocalService.fetchObjectEntry(
						GetterUtil.getLong(values.get(objectField.getName())));

			ObjectEntry objectEntry = ObjectEntryInfoItemUtil.getObjectEntry(
				parentObjectDefinition, objectEntryManagerRegistry,
				objectScopeProviderRegistry, serviceBuilderRelatedObjectEntry,
				themeDisplay);

			Map<String, Object> properties = new HashMap<>();
			String relatedObjectEntryDefaultLanguageId = defaultLanguageId;

			if (objectEntry != null) {
				properties = objectEntry.getProperties();
				relatedObjectEntryDefaultLanguageId =
					objectEntry.getDefaultLanguageId();
			}

			for (ObjectField relatedObjectField :
					objectFieldLocalService.getObjectFields(
						parentObjectDefinition.getObjectDefinitionId())) {

				if (relatedObjectField.isMetadata()) {
					continue;
				}

				String namespace =
					ObjectEntryInfoItemUtil.getInfoFieldNamespace(
						parentObjectDefinition, objectRelationship);

				value = properties.get(relatedObjectField.getName());

				if (relatedObjectField.isLocalized()) {
					value = properties.get(
						relatedObjectField.getI18nObjectFieldName());
				}

				_addInfoFieldValue(
					relatedObjectEntryDefaultLanguageId, dlAppLocalService,
					dlURLHelper, infoFieldValues, listTypeEntryLocalService,
					parentObjectDefinition, objectEntryLocalService,
					objectEntryService, relatedObjectField,
					objectFieldInfoFieldConverter, namespace,
					objectRelationshipLocalService,
					serviceBuilderRelatedObjectEntry, themeDisplay, value);

				infoFieldValues.add(
					new InfoFieldValue<>(
						ObjectEntryInfoItemFields.getFriendlyURLInfoField(
							parentObjectDefinition.
								isEnableFriendlyURLCustomization(),
							objectRelationship.getName(), namespace),
						() -> {
							if (objectEntry == null) {
								return null;
							}

							return getFriendlyURLInfoFieldValue(
								portal.getClassNameId(
									parentObjectDefinition.getClassName()),
								friendlyURLEntryLocalService,
								GetterUtil.getLong(
									values.get(objectField.getName())));
						}));
			}
		}

		if (!objectDefinition.isDefaultStorageType()) {
			return infoFieldValues;
		}

		infoFieldValues.addAll(
			TransformUtil.transform(
				objectActionLocalService.getObjectActions(
					objectDefinition.getObjectDefinitionId(),
					ObjectActionTriggerConstants.KEY_STANDALONE),
				objectAction -> {
					InfoLocalizedValue<String> actionLabelLocalizedValue =
						InfoLocalizedValue.<String>builder(
						).defaultLocale(
							LocaleUtil.fromLanguageId(
								objectAction.getDefaultLanguageId())
						).values(
							objectAction.getLabelMap()
						).build();

					return new InfoFieldValue<>(
						InfoField.builder(
						).infoFieldType(
							ActionInfoFieldType.INSTANCE
						).namespace(
							ObjectAction.class.getSimpleName()
						).name(
							objectAction.getName()
						).labelInfoLocalizedValue(
							actionLabelLocalizedValue
						).build(),
						actionLabelLocalizedValue);
				}));

		return infoFieldValues;
	}

	public static Object getMultipleRelationshipInfoFieldValue(
		ObjectRelationship objectRelationship,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		long objectEntryId,
		ObjectRelatedModelsProviderRegistry
			objectRelatedModelsProviderRegistry) {

		if (!objectRelationship.compareType(
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY)) {

			return null;
		}

		try {
			ObjectDefinition relatedObjectDefinition =
				objectDefinitionLocalService.getObjectDefinition(
					objectRelationship.getObjectDefinitionId2());

			ObjectRelatedModelsProvider objectRelatedModelsProvider =
				objectRelatedModelsProviderRegistry.
					getObjectRelatedModelsProvider(
						relatedObjectDefinition.getClassName(),
						relatedObjectDefinition.getCompanyId(),
						objectRelationship.getType());

			long[] relatedPrimaryKeys = TransformUtil.transformToLongArray(
				(List<BaseModel<?>>)
					objectRelatedModelsProvider.getRelatedModels(
						GroupThreadLocal.getGroupId(),
						objectRelationship.getObjectRelationshipId(), null,
						false, objectEntryId, null, -1, -1, null),
				BaseModel::getPrimaryKeyObj);

			return StringUtil.merge(relatedPrimaryKeys, StringPool.COMMA);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return null;
	}

	private static void _addInfoFieldValue(
			String defaultLanguageId, DLAppLocalService dlAppLocalService,
			DLURLHelper dlURLHelper,
			List<InfoFieldValue<Object>> infoFieldValues,
			ListTypeEntryLocalService listTypeEntryLocalService,
			ObjectDefinition objectDefinition,
			ObjectEntryLocalService objectEntryLocalService,
			ObjectEntryService objectEntryService, ObjectField objectField,
			ObjectFieldInfoFieldConverter objectFieldInfoFieldConverter,
			String objectFieldNamespace,
			ObjectRelationshipLocalService objectRelationshipLocalService,
			com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry,
			ThemeDisplay themeDisplay, Object value)
		throws Exception {

		if (value == null) {
			infoFieldValues.add(
				new InfoFieldValue<>(
					objectFieldInfoFieldConverter.getInfoField(
						false, objectFieldNamespace, objectField),
					StringPool.BLANK));

			return;
		}

		Object infoFieldValue = null;

		Locale locale = LocaleUtil.getSiteDefault();

		if (themeDisplay != null) {
			locale = themeDisplay.getLocale();
		}

		if (objectField.isLocalized() && (value instanceof Map)) {
			Map<String, Object> map = (Map<String, Object>)value;

			Locale defaultLocale = null;

			if (Validator.isNotNull(defaultLanguageId)) {
				defaultLocale = LocaleUtil.fromLanguageId(defaultLanguageId);
			}

			infoFieldValue = InfoLocalizedValue.builder(
			).defaultLocale(
				defaultLocale
			).value(
				consumer -> {
					for (Map.Entry<String, Object> entry : map.entrySet()) {
						Locale curLocale = LocaleUtil.fromLanguageId(
							entry.getKey());

						consumer.accept(
							curLocale,
							_parseValue(
								listTypeEntryLocalService, curLocale,
								objectEntryLocalService, objectField,
								objectRelationshipLocalService, themeDisplay,
								entry.getValue()));
					}
				}
			).build();
		}
		else {
			infoFieldValue = _parseValue(
				listTypeEntryLocalService, locale, objectEntryLocalService,
				objectField, objectRelationshipLocalService, themeDisplay,
				value);
		}

		if (infoFieldValue == null) {
			infoFieldValues.add(
				new InfoFieldValue<>(
					objectFieldInfoFieldConverter.getInfoField(
						false, objectFieldNamespace, objectField),
					StringPool.BLANK));

			return;
		}

		if (objectField.compareBusinessType(
				ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT)) {

			try {
				Object downloadURLInfoFieldValue = null;
				Object fileNameInfoFieldValue = null;
				Object fileURLInfoFieldValue = null;
				Object mimeTypeInfoFieldValue = null;
				Object previewURLInfoFieldValue = null;
				Object sizeInfoFieldValue = null;

				if (infoFieldValue instanceof Long) {
					Long fileEntryId = (Long)infoFieldValue;

					FileEntry fileEntry = dlAppLocalService.getFileEntry(
						GetterUtil.getLong(fileEntryId));

					downloadURLInfoFieldValue = _getAttachmentDownloadURL(
						dlURLHelper, fileEntry, objectDefinition,
						objectEntryService, objectField,
						serviceBuilderObjectEntry, themeDisplay);
					fileNameInfoFieldValue = fileEntry.getFileName();

					String mimeType = fileEntry.getMimeType();

					mimeTypeInfoFieldValue = mimeType;

					WebImage fileURLWebImage = new WebImage(
						dlURLHelper.getPreviewURL(
							fileEntry, fileEntry.getFileVersion(), themeDisplay,
							StringPool.BLANK),
						new InfoItemReference(
							FileEntry.class.getName(),
							new ClassPKInfoItemIdentifier(
								fileEntry.getFileEntryId())));

					fileURLWebImage.setAlt(fileEntry.getDescription());

					if (mimeType.startsWith("image")) {
						fileURLInfoFieldValue = fileURLWebImage;
					}

					previewURLInfoFieldValue = fileURLWebImage;
					sizeInfoFieldValue = fileEntry.getSize();
				}
				else if (infoFieldValue instanceof InfoLocalizedValue) {
					InfoLocalizedValue.Builder<Object>
						downloadURLInfoFieldValueBuilder =
							InfoLocalizedValue.builder();
					InfoLocalizedValue.Builder<Object>
						fileNameInfoFieldValueBuilder =
							InfoLocalizedValue.builder();
					InfoLocalizedValue.Builder<Object>
						fileURLInfoFieldValueBuilder =
							InfoLocalizedValue.builder();
					boolean hasImage = false;
					InfoLocalizedValue.Builder<Object>
						mimeTypeInfoFieldValueBuilder =
							InfoLocalizedValue.builder();
					InfoLocalizedValue.Builder<Object>
						previewURLInfoFieldValueBuilder =
							InfoLocalizedValue.builder();
					InfoLocalizedValue.Builder<Object>
						sizeInfoFieldValueBuilder =
							InfoLocalizedValue.builder();

					InfoLocalizedValue<Object> infoLocalizedValue =
						(InfoLocalizedValue<Object>)infoFieldValue;

					Map<Locale, Object> values = infoLocalizedValue.getValues();

					for (Map.Entry<Locale, Object> entry : values.entrySet()) {
						FileEntry fileEntry = dlAppLocalService.fetchFileEntry(
							GetterUtil.getLong(entry.getValue()));

						if (fileEntry == null) {
							continue;
						}

						downloadURLInfoFieldValueBuilder.value(
							entry.getKey(),
							_getAttachmentDownloadURL(
								dlURLHelper, fileEntry, objectDefinition,
								objectEntryService, objectField,
								serviceBuilderObjectEntry, themeDisplay));
						fileNameInfoFieldValueBuilder.value(
							entry.getKey(), fileEntry.getFileName());
						mimeTypeInfoFieldValueBuilder.value(
							entry.getKey(), fileEntry.getMimeType());

						String mimeType = fileEntry.getMimeType();

						WebImage fileURLWebImage = new WebImage(
							dlURLHelper.getPreviewURL(
								fileEntry, fileEntry.getFileVersion(),
								themeDisplay, StringPool.BLANK),
							new InfoItemReference(
								FileEntry.class.getName(),
								new ClassPKInfoItemIdentifier(
									fileEntry.getFileEntryId())));

						fileURLWebImage.setAlt(fileEntry.getDescription());

						if (mimeType.startsWith("image")) {
							fileURLInfoFieldValueBuilder.value(
								entry.getKey(), fileURLWebImage);

							hasImage = true;
						}

						previewURLInfoFieldValueBuilder.value(
							entry.getKey(), fileURLWebImage);
						sizeInfoFieldValueBuilder.value(
							entry.getKey(), fileEntry.getSize());
					}

					downloadURLInfoFieldValue =
						downloadURLInfoFieldValueBuilder.build();

					fileNameInfoFieldValue =
						fileNameInfoFieldValueBuilder.build();

					if (hasImage) {
						fileURLInfoFieldValue =
							fileURLInfoFieldValueBuilder.build();
					}

					mimeTypeInfoFieldValue =
						mimeTypeInfoFieldValueBuilder.build();
					previewURLInfoFieldValue =
						previewURLInfoFieldValueBuilder.build();
					sizeInfoFieldValue = sizeInfoFieldValueBuilder.build();
				}

				if (fileURLInfoFieldValue != null) {
					infoFieldValues.add(
						new InfoFieldValue<>(
							InfoField.builder(
							).infoFieldType(
								ImageInfoFieldType.INSTANCE
							).namespace(
								objectFieldNamespace
							).name(
								objectField.getObjectFieldId() + "#fileURL"
							).labelInfoLocalizedValue(
								InfoLocalizedValue.localize(
									ObjectEntryInfoItemFields.class, "file-url")
							).build(),
							fileURLInfoFieldValue));
				}

				infoFieldValues.add(
					new InfoFieldValue<>(
						InfoField.builder(
						).infoFieldType(
							URLInfoFieldType.INSTANCE
						).namespace(
							objectFieldNamespace
						).name(
							objectField.getObjectFieldId() + "#downloadURL"
						).labelInfoLocalizedValue(
							InfoLocalizedValue.localize(
								ObjectEntryInfoItemFields.class, "download-url")
						).build(),
						downloadURLInfoFieldValue));
				infoFieldValues.add(
					new InfoFieldValue<>(
						InfoField.builder(
						).infoFieldType(
							TextInfoFieldType.INSTANCE
						).namespace(
							objectFieldNamespace
						).name(
							objectField.getObjectFieldId() + "#fileName"
						).labelInfoLocalizedValue(
							InfoLocalizedValue.localize(
								ObjectEntryInfoItemFields.class, "file-name")
						).build(),
						fileNameInfoFieldValue));
				infoFieldValues.add(
					new InfoFieldValue<>(
						InfoField.builder(
						).infoFieldType(
							TextInfoFieldType.INSTANCE
						).namespace(
							objectFieldNamespace
						).name(
							objectField.getObjectFieldId() + "#mimeType"
						).labelInfoLocalizedValue(
							InfoLocalizedValue.localize(
								ObjectEntryInfoItemFields.class, "mime-type")
						).build(),
						mimeTypeInfoFieldValue));
				infoFieldValues.add(
					new InfoFieldValue<>(
						InfoField.builder(
						).infoFieldType(
							ImageInfoFieldType.INSTANCE
						).namespace(
							objectFieldNamespace
						).name(
							objectField.getObjectFieldId() + "#previewURL"
						).labelInfoLocalizedValue(
							InfoLocalizedValue.localize(
								ObjectEntryInfoItemFields.class, "preview-url")
						).build(),
						previewURLInfoFieldValue));
				infoFieldValues.add(
					new InfoFieldValue<>(
						InfoField.builder(
						).infoFieldType(
							TextInfoFieldType.INSTANCE
						).namespace(
							objectFieldNamespace
						).name(
							objectField.getObjectFieldId() + "#size"
						).labelInfoLocalizedValue(
							InfoLocalizedValue.localize(
								ObjectEntryInfoItemFields.class, "size")
						).build(),
						sizeInfoFieldValue));
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}

		infoFieldValues.add(
			new InfoFieldValue<>(
				objectFieldInfoFieldConverter.getInfoField(
					false, objectFieldNamespace, objectField),
				GetterUtil.getObject(infoFieldValue, StringPool.BLANK)));
	}

	private static String _getAttachmentDownloadURL(
			DLURLHelper dlURLHelper, FileEntry fileEntry,
			ObjectDefinition objectDefinition,
			ObjectEntryService objectEntryService, ObjectField objectField,
			com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry,
			ThemeDisplay themeDisplay)
		throws PortalException {

		if (serviceBuilderObjectEntry == null) {
			return dlURLHelper.getDownloadURL(
				fileEntry, fileEntry.getFileVersion(), themeDisplay,
				StringPool.BLANK);
		}

		return ObjectFieldUtil.getAttachmentDownloadURL(
			dlURLHelper, fileEntry, serviceBuilderObjectEntry.getGroupId(),
			objectDefinition.getExternalReferenceCode(),
			serviceBuilderObjectEntry, objectEntryService, objectField,
			PermissionThreadLocal.getPermissionChecker(), themeDisplay);
	}

	private static KeyLocalizedLabelPair _getKeyLocalizedLabelPair(
		ListTypeEntryLocalService listTypeEntryLocalService, Object object,
		ObjectField objectField) {

		String key = null;

		if (object instanceof ListEntry) {
			ListEntry listEntry = (ListEntry)object;

			key = listEntry.getKey();
		}
		else if (object instanceof Map) {
			key = MapUtil.getString((Map)object, "key");
		}
		else {
			key = GetterUtil.getString(object);
		}

		ListTypeEntry listTypeEntry =
			listTypeEntryLocalService.fetchListTypeEntry(
				objectField.getListTypeDefinitionId(), key);

		if (listTypeEntry == null) {
			return null;
		}

		return new KeyLocalizedLabelPair(
			listTypeEntry.getKey(),
			InfoLocalizedValue.<String>builder(
			).defaultLocale(
				LocaleUtil.fromLanguageId(listTypeEntry.getDefaultLanguageId())
			).values(
				listTypeEntry.getNameMap()
			).build());
	}

	private static Object _parseValue(
		ListTypeEntryLocalService listTypeEntryLocalService, Locale locale,
		ObjectEntryLocalService objectEntryLocalService,
		ObjectField objectField,
		ObjectRelationshipLocalService objectRelationshipLocalService,
		ThemeDisplay themeDisplay, Object value) {

		if (value == null) {
			return null;
		}

		if (objectField.compareBusinessType(
				ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT)) {

			if (value instanceof Long) {
				return value;
			}

			com.liferay.object.rest.dto.v1_0.FileEntry dtoFileEntry =
				(com.liferay.object.rest.dto.v1_0.FileEntry)value;

			return dtoFileEntry.getId();
		}
		else if (objectField.compareBusinessType(
					ObjectFieldConstants.BUSINESS_TYPE_DATE)) {

			try {
				return DateUtil.parseDate(
					"yyyy-MM-dd", value.toString(), locale);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);

					return null;
				}
			}
		}
		else if (objectField.compareBusinessType(
					ObjectFieldConstants.BUSINESS_TYPE_DATE_TIME)) {

			LocalDateTime localDateTime = LocalDateTime.parse(
				value.toString(),
				DateTimeFormatter.ofPattern(
					ObjectFieldUtil.getDateTimePattern(value.toString())));

			String timeStorage = ObjectFieldSettingUtil.getValue(
				ObjectFieldSettingConstants.NAME_TIME_STORAGE, objectField);

			if (StringUtil.equals(
					timeStorage,
					ObjectFieldSettingConstants.VALUE_USE_INPUT_AS_ENTERED)) {

				return localDateTime;
			}

			ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneOffset.UTC);

			if (themeDisplay == null) {
				return zonedDateTime.toLocalDateTime();
			}

			String timeZoneId = ObjectFieldSettingUtil.getTimeZoneId(
				objectField.getObjectFieldSettings(), themeDisplay.getUser());

			if (timeZoneId == null) {
				return zonedDateTime.toLocalDateTime();
			}

			return zonedDateTime.withZoneSameInstant(
				ZoneId.of(timeZoneId)
			).toLocalDateTime();
		}
		else if (objectField.compareBusinessType(
					ObjectFieldConstants.BUSINESS_TYPE_MULTISELECT_PICKLIST)) {

			List<KeyLocalizedLabelPair> keyLocalizedLabelPairs =
				new ArrayList<>();

			List<Object> objects = new ArrayList<>();

			if (value instanceof String) {
				objects = ListUtil.fromArray(
					StringUtil.split(
						(String)value, StringPool.COMMA_AND_SPACE));
			}
			else {
				objects = (List<Object>)value;
			}

			for (Object object : objects) {
				KeyLocalizedLabelPair keyLocalizedLabelPair =
					_getKeyLocalizedLabelPair(
						listTypeEntryLocalService, object, objectField);

				if (keyLocalizedLabelPair != null) {
					keyLocalizedLabelPairs.add(keyLocalizedLabelPair);
				}
			}

			if (ListUtil.isNotEmpty(keyLocalizedLabelPairs)) {
				return keyLocalizedLabelPairs;
			}
		}
		else if (objectField.compareBusinessType(
					ObjectFieldConstants.BUSINESS_TYPE_PICKLIST)) {

			KeyLocalizedLabelPair keyLocalizedLabelPair =
				_getKeyLocalizedLabelPair(
					listTypeEntryLocalService, value, objectField);

			if (keyLocalizedLabelPair != null) {
				return ListUtil.fromArray(keyLocalizedLabelPair);
			}
		}
		else if (objectField.compareBusinessType(
					ObjectFieldConstants.BUSINESS_TYPE_RELATIONSHIP)) {

			long primaryKey = GetterUtil.getLong(value);

			if (primaryKey == 0) {
				return null;
			}

			ObjectRelationship objectRelationship =
				objectRelationshipLocalService.
					fetchObjectRelationshipByObjectFieldId2(
						objectField.getObjectFieldId());

			try {
				return new KeyValuePair(
					String.valueOf(value),
					objectEntryLocalService.getTitleValue(
						objectRelationship.getObjectDefinitionId1(),
						primaryKey));
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);

					return null;
				}
			}
		}

		return value;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectEntryInfoItemValuesProviderUtil.class);

}