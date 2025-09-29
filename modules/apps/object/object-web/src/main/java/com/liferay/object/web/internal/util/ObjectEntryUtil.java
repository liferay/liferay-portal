/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.util;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLFileEntryLocalServiceUtil;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.field.type.DateInfoFieldType;
import com.liferay.info.field.type.DateTimeInfoFieldType;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeEntryLocalServiceUtil;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.relationship.util.ObjectRelationshipUtil;
import com.liferay.object.rest.dto.v1_0.FileEntry;
import com.liferay.object.rest.dto.v1_0.ListEntry;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectEntryLocalServiceUtil;
import com.liferay.object.service.ObjectRelationshipLocalServiceUtil;
import com.liferay.object.web.internal.model.ProxyObjectEntry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.text.Format;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class ObjectEntryUtil {

	public static Object getValue(
			Locale locale, ObjectField objectField, Map<String, Object> values)
		throws Exception {

		Object value = values.get(objectField.getName());

		if (value == null) {
			return null;
		}

		if (objectField.compareBusinessType(
				ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT)) {

			FileEntry fileEntry = (FileEntry)value;

			DLFileEntry dlFileEntry =
				DLFileEntryLocalServiceUtil.fetchDLFileEntry(
					GetterUtil.getLong(fileEntry.getId()));

			if (dlFileEntry == null) {
				return null;
			}

			return fileEntry;
		}
		else if (objectField.compareBusinessType(
					ObjectFieldConstants.BUSINESS_TYPE_DATE)) {

			return DateUtil.parseDate("yyyy-MM-dd", value.toString(), locale);
		}
		else if (objectField.compareBusinessType(
					ObjectFieldConstants.BUSINESS_TYPE_DATE_TIME)) {

			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(
				ObjectFieldUtil.getDateTimePattern(value.toString()));

			return LocalDateTime.parse(value.toString(), dateTimeFormatter);
		}
		else if (objectField.compareBusinessType(
					ObjectFieldConstants.BUSINESS_TYPE_MULTISELECT_PICKLIST)) {

			List<ListTypeEntry> listTypeEntries = new ArrayList<>();

			for (ListEntry listEntry : (List<ListEntry>)value) {
				ListTypeEntry listTypeEntry =
					ListTypeEntryLocalServiceUtil.fetchListTypeEntry(
						objectField.getListTypeDefinitionId(),
						listEntry.getKey());

				if (listTypeEntry == null) {
					continue;
				}

				listTypeEntries.add(listTypeEntry);
			}

			return listTypeEntries;
		}
		else if (objectField.compareBusinessType(
					ObjectFieldConstants.BUSINESS_TYPE_PICKLIST)) {

			ListEntry listEntry = (ListEntry)value;

			return ListTypeEntryLocalServiceUtil.fetchListTypeEntry(
				objectField.getListTypeDefinitionId(), listEntry.getKey());
		}
		else if (objectField.compareBusinessType(
					ObjectFieldConstants.BUSINESS_TYPE_RELATIONSHIP)) {

			long primaryKey = GetterUtil.getLong(value);

			if (primaryKey == 0) {
				return null;
			}

			ObjectRelationship objectRelationship =
				ObjectRelationshipLocalServiceUtil.
					fetchObjectRelationshipByObjectFieldId2(
						objectField.getObjectFieldId());

			return new KeyValuePair(
				String.valueOf(primaryKey),
				ObjectEntryLocalServiceUtil.getTitleValue(
					objectRelationship.getObjectDefinitionId1(), primaryKey));
		}

		return value;
	}

	public static ObjectEntry toObjectEntry(
		long objectDefinitionId,
		com.liferay.object.rest.dto.v1_0.ObjectEntry objectEntry) {

		ObjectEntry serviceBuilderObjectEntry =
			ObjectEntryLocalServiceUtil.createObjectEntry(0L);

		serviceBuilderObjectEntry.setExternalReferenceCode(
			objectEntry.getExternalReferenceCode());
		serviceBuilderObjectEntry.setObjectEntryId(
			GetterUtil.getLong(objectEntry.getId()));
		serviceBuilderObjectEntry.setObjectDefinitionId(objectDefinitionId);
		serviceBuilderObjectEntry.setDefaultLanguageId(
			objectEntry.getDefaultLanguageId());

		return new ProxyObjectEntry(serviceBuilderObjectEntry, objectEntry);
	}

	public static Map<String, Object> toProperties(
		long companyId, InfoItemFieldValues infoItemFieldValues) {

		Map<String, Object> properties = new HashMap<>();

		for (InfoFieldValue<Object> infoFieldValue :
				infoItemFieldValues.getInfoFieldValues()) {

			InfoField<?> infoField = infoFieldValue.getInfoField();

			if (!StringUtil.startsWith(
					infoField.getUniqueId(),
					ObjectRelationship.class.getSimpleName() +
						StringPool.POUND)) {

				_addPropertyValue(infoField, infoFieldValue, properties);

				continue;
			}

			String[] parts = StringUtil.split(
				StringUtil.removeLast(
					infoField.getUniqueId(),
					StringPool.UNDERLINE + infoField.getName()),
				StringPool.POUND);

			String objectRelationshipName = parts[2];

			Map<String, Object> relatedProperties =
				(Map<String, Object>)properties.computeIfAbsent(
					objectRelationshipName, key -> new HashMap<>());

			_addPropertyValue(infoField, infoFieldValue, relatedProperties);

			if (relatedProperties.containsKey("externalReferenceCode")) {
				continue;
			}

			ObjectDefinition relatedObjectDefinition =
				ObjectDefinitionLocalServiceUtil.fetchObjectDefinition(
					companyId, parts[1]);

			InfoFieldValue<Object> relatedInfoFieldValue =
				infoItemFieldValues.getInfoFieldValue(
					ObjectRelationshipUtil.getObjectRelationshipFieldName(
						relatedObjectDefinition, objectRelationshipName));

			if (relatedInfoFieldValue == null) {
				continue;
			}

			ObjectEntry relatedObjectEntry =
				ObjectEntryLocalServiceUtil.fetchObjectEntry(
					GetterUtil.getLong(relatedInfoFieldValue.getValue()));

			if (relatedObjectEntry == null) {
				continue;
			}

			relatedProperties.put(
				"externalReferenceCode",
				relatedObjectEntry.getExternalReferenceCode());
		}

		return properties;
	}

	private static void _addPropertyValue(
		InfoField<?> infoField, InfoFieldValue<Object> infoFieldValue,
		Map<String, Object> properties) {

		Object value = infoFieldValue.getValue();

		if (infoField.isLocalizable() &&
			(value instanceof InfoLocalizedValue)) {

			InfoLocalizedValue<Object> infoLocalizedValue =
				(InfoLocalizedValue<Object>)value;

			Map<Locale, Object> values = infoLocalizedValue.getValues();

			Map<String, Object> languageIdMap = new HashMap<>();

			values.forEach(
				(locale, localizedValue) -> languageIdMap.put(
					LocaleUtil.toLanguageId(locale),
					_parseValue(infoField, localizedValue)));

			properties.put(infoField.getName() + "_i18n", languageIdMap);
		}
		else {
			properties.put(infoField.getName(), _parseValue(infoField, value));
		}
	}

	private static Object _parseValue(InfoField<?> infoField, Object value) {
		if (Objects.equals(
				DateInfoFieldType.INSTANCE, infoField.getInfoFieldType()) &&
			(value instanceof Date)) {

			Format format = FastDateFormatFactoryUtil.getSimpleDateFormat(
				"yyyy-MM-dd");

			return format.format(value);
		}
		else if (Objects.equals(
					DateTimeInfoFieldType.INSTANCE,
					infoField.getInfoFieldType()) &&
				 (value instanceof LocalDateTime)) {

			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(
				"yyyy-MM-dd HH:mm");

			return dateTimeFormatter.format((LocalDateTime)value);
		}

		return value;
	}

}