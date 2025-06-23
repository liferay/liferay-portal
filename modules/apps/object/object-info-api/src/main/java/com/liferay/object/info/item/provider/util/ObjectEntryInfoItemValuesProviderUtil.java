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
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.info.type.KeyLocalizedLabelPair;
import com.liferay.info.type.WebImage;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.info.field.converter.ObjectFieldInfoFieldConverter;
import com.liferay.object.info.field.type.util.ObjectFieldInfoFieldTypeUtil;
import com.liferay.object.info.item.ObjectEntryInfoItemFields;
import com.liferay.object.info.item.util.ObjectEntryInfoItemUtil;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.rest.dto.v1_0.ListEntry;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

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
			DLAppLocalService dlAppLocalService, DLURLHelper dlURLHelper,
			FriendlyURLEntryLocalService friendlyURLEntryLocalService,
			ListTypeEntryLocalService listTypeEntryLocalService,
			ObjectActionLocalService objectActionLocalService,
			ObjectDefinition objectDefinition,
			ObjectDefinitionLocalService objectDefinitionLocalService,
			ObjectEntryLocalService objectEntryLocalService,
			ObjectEntryManagerRegistry objectEntryManagerRegistry,
			ObjectFieldInfoFieldConverter objectFieldInfoFieldConverter,
			ObjectFieldLocalService objectFieldLocalService,
			List<ObjectField> objectFields,
			ObjectRelationshipLocalService objectRelationshipLocalService,
			ObjectScopeProviderRegistry objectScopeProviderRegistry,
			Portal portal, ThemeDisplay themeDisplay,
			Map<String, Object> values)
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
				dlAppLocalService, dlURLHelper, infoFieldValues,
				listTypeEntryLocalService, objectEntryLocalService, objectField,
				objectFieldInfoFieldConverter,
				ObjectField.class.getSimpleName(),
				objectRelationshipLocalService, themeDisplay, value);

			if (!objectField.compareBusinessType(
					ObjectFieldConstants.BUSINESS_TYPE_RELATIONSHIP)) {

				continue;
			}

			Map<String, Object> properties = new HashMap<>();

			ObjectRelationship objectRelationship =
				objectRelationshipLocalService.
					fetchObjectRelationshipByObjectFieldId2(
						objectField.getObjectFieldId());

			ObjectDefinition parentObjectDefinition =
				objectDefinitionLocalService.getObjectDefinition(
					objectRelationship.getObjectDefinitionId1());

			ObjectEntry objectEntry = ObjectEntryInfoItemUtil.getObjectEntry(
				parentObjectDefinition, objectEntryManagerRegistry,
				objectScopeProviderRegistry,
				objectEntryLocalService.fetchObjectEntry(
					GetterUtil.getLong(values.get(objectField.getName()))),
				themeDisplay);

			if (objectEntry != null) {
				properties = objectEntry.getProperties();
			}

			for (ObjectField relatedObjectField :
					objectFieldLocalService.getObjectFields(
						parentObjectDefinition.getObjectDefinitionId(),
						false)) {

				String namespace =
					ObjectEntryInfoItemUtil.getInfoFieldNamespace(
						parentObjectDefinition, objectRelationship);

				value = properties.get(relatedObjectField.getName());

				if (relatedObjectField.isLocalized()) {
					value = properties.get(
						relatedObjectField.getI18nObjectFieldName());
				}

				_addInfoFieldValue(
					dlAppLocalService, dlURLHelper, infoFieldValues,
					listTypeEntryLocalService, objectEntryLocalService,
					relatedObjectField, objectFieldInfoFieldConverter,
					namespace, objectRelationshipLocalService, themeDisplay,
					value);

				if (FeatureFlagManagerUtil.isEnabled(
						parentObjectDefinition.getCompanyId(), "LPD-21926")) {

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

	private static void _addInfoFieldValue(
			DLAppLocalService dlAppLocalService, DLURLHelper dlURLHelper,
			List<InfoFieldValue<Object>> infoFieldValues,
			ListTypeEntryLocalService listTypeEntryLocalService,
			ObjectEntryLocalService objectEntryLocalService,
			ObjectField objectField,
			ObjectFieldInfoFieldConverter objectFieldInfoFieldConverter,
			String objectFieldNamespace,
			ObjectRelationshipLocalService objectRelationshipLocalService,
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

			infoFieldValue = InfoLocalizedValue.builder(
			).defaultLocale(
				LocaleUtil.fromLanguageId(objectField.getDefaultLanguageId())
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
								objectRelationshipLocalService,
								entry.getValue()));
					}
				}
			).build();
		}
		else {
			infoFieldValue = _parseValue(
				listTypeEntryLocalService, locale, objectEntryLocalService,
				objectField, objectRelationshipLocalService, value);
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
				Object mimeTypeInfoFieldValue = null;
				Object previewURLInfoFieldValue = null;
				Object sizeInfoFieldValue = null;

				if (infoFieldValue instanceof Long) {
					Long fileEntryId = (Long)infoFieldValue;

					FileEntry fileEntry = dlAppLocalService.getFileEntry(
						GetterUtil.getLong(fileEntryId));

					downloadURLInfoFieldValue = dlURLHelper.getDownloadURL(
						fileEntry, fileEntry.getFileVersion(), null,
						StringPool.BLANK);

					fileNameInfoFieldValue = fileEntry.getFileName();
					mimeTypeInfoFieldValue = fileEntry.getMimeType();
					previewURLInfoFieldValue = dlURLHelper.getPreviewURL(
						fileEntry, fileEntry.getFileVersion(), null,
						StringPool.BLANK);
					sizeInfoFieldValue = fileEntry.getSize();
				}
				else if (infoFieldValue instanceof InfoLocalizedValue) {
					InfoLocalizedValue<Object> infoLocalizedValue =
						(InfoLocalizedValue<Object>)infoFieldValue;

					InfoLocalizedValue.Builder<Object>
						downloadURLInfoFieldValueBuilder =
							InfoLocalizedValue.builder();
					InfoLocalizedValue.Builder<Object>
						fileNameInfoFieldValueBuilder =
							InfoLocalizedValue.builder();
					InfoLocalizedValue.Builder<Object>
						mimeTypeInfoFieldValueBuilder =
							InfoLocalizedValue.builder();
					InfoLocalizedValue.Builder<Object>
						previewURLInfoFieldValueBuilder =
							InfoLocalizedValue.builder();
					InfoLocalizedValue.Builder<Object>
						sizeInfoFieldValueBuilder =
							InfoLocalizedValue.builder();

					Map<Locale, Object> values = infoLocalizedValue.getValues();

					for (Map.Entry<Locale, Object> entry : values.entrySet()) {
						Long fileEntryId = (Long)entry.getValue();

						FileEntry fileEntry = dlAppLocalService.getFileEntry(
							GetterUtil.getLong(fileEntryId));

						downloadURLInfoFieldValueBuilder.value(
							entry.getKey(),
							dlURLHelper.getDownloadURL(
								fileEntry, fileEntry.getFileVersion(), null,
								StringPool.BLANK));
						fileNameInfoFieldValueBuilder.value(
							entry.getKey(), fileEntry.getFileName());
						mimeTypeInfoFieldValueBuilder.value(
							entry.getKey(), fileEntry.getMimeType());
						previewURLInfoFieldValueBuilder.value(
							entry.getKey(),
							dlURLHelper.getPreviewURL(
								fileEntry, fileEntry.getFileVersion(), null,
								StringPool.BLANK));
						sizeInfoFieldValueBuilder.value(
							entry.getKey(), fileEntry.getSize());
					}

					downloadURLInfoFieldValue =
						downloadURLInfoFieldValueBuilder.build();

					fileNameInfoFieldValue =
						fileNameInfoFieldValueBuilder.build();
					mimeTypeInfoFieldValue =
						mimeTypeInfoFieldValueBuilder.build();
					previewURLInfoFieldValue =
						previewURLInfoFieldValueBuilder.build();
					sizeInfoFieldValue = sizeInfoFieldValueBuilder.build();
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

	private static KeyLocalizedLabelPair _getKeyLocalizedLabelPair(
		ListTypeEntryLocalService listTypeEntryLocalService, Object object,
		ObjectField objectField) {

		String key = null;

		if (object instanceof ListEntry) {
			ListEntry listEntry = (ListEntry)object;

			key = listEntry.getKey();
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
		Object value) {

		if (value == null) {
			return null;
		}

		if (Objects.equals(
				ObjectFieldInfoFieldTypeUtil.getInfoFieldType(objectField),
				ImageInfoFieldType.INSTANCE)) {

			try {
				JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
					new String((byte[])value));

				WebImage webImage = new WebImage(jsonObject.getString("url"));

				webImage.setAlt(jsonObject.getString("alt"));

				return webImage;
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);

					return null;
				}
			}
		}
		else if (objectField.compareBusinessType(
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

			return LocalDateTime.parse(
				value.toString(),
				DateTimeFormatter.ofPattern(
					ObjectFieldUtil.getDateTimePattern(value.toString())));
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

			ObjectRelationship objectRelationship =
				objectRelationshipLocalService.
					fetchObjectRelationshipByObjectFieldId2(
						objectField.getObjectFieldId());

			try {
				return new KeyValuePair(
					String.valueOf(value),
					objectEntryLocalService.getTitleValue(
						objectRelationship.getObjectDefinitionId1(),
						GetterUtil.getLong(value)));
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