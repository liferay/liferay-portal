/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.comparison;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.setting.util.ObjectFieldSettingUtil;
import com.liferay.object.model.ObjectEntryVersion;
import com.liferay.object.model.ObjectField;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.service.ObjectEntryVersionService;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TimeZoneUtil;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author Verónica González
 * @author Jürgen Kappler
 */
public class ObjectEntryVersionFieldValueResolver {

	public ObjectEntryVersionFieldValueResolver(
		DLAppLocalService dlAppLocalService,
		DLFileEntryLocalService dlFileEntryLocalService,
		DLURLHelper dlURLHelper, Language language,
		ListTypeEntryLocalService listTypeEntryLocalService,
		ObjectEntryVersionService objectEntryVersionService) {

		_dlAppLocalService = dlAppLocalService;
		_dlFileEntryLocalService = dlFileEntryLocalService;
		_dlURLHelper = dlURLHelper;
		_language = language;
		_listTypeEntryLocalService = listTypeEntryLocalService;
		_objectEntryVersionService = objectEntryVersionService;
	}

	public Map<String, Object> getFieldValues(
			String languageId, long objectEntryId, int version)
		throws Exception {

		Map<String, Object> fieldValues = new HashMap<>();

		ObjectEntryVersion objectEntryVersion =
			_objectEntryVersionService.getObjectEntryVersion(
				objectEntryId, version);

		ObjectEntry objectEntry = ObjectEntry.unsafeToDTO(
			objectEntryVersion.getContent());

		Map<String, Object> properties = objectEntry.getProperties();

		Object nestedProperties = properties.get("properties");

		if (nestedProperties instanceof Map) {
			properties = (Map<String, Object>)nestedProperties;
		}

		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			String fieldName = entry.getKey();

			if (fieldName.endsWith("_i18n") || fieldName.endsWith("RawText")) {
				continue;
			}

			Object fieldValue = entry.getValue();

			Object localizedValues = properties.get(fieldName + "_i18n");

			if (localizedValues instanceof Map) {
				Map<String, Object> localizedValuesMap =
					(Map<String, Object>)localizedValues;

				fieldValue = localizedValuesMap.get(languageId);
			}

			fieldValues.put(fieldName, fieldValue);
		}

		Map<String, String> friendlyUrlPathI18n =
			objectEntry.getFriendlyUrlPath_i18n();

		if ((friendlyUrlPathI18n != null) &&
			friendlyUrlPathI18n.containsKey(languageId)) {

			fieldValues.put(
				"objectEntryFriendlyURL", friendlyUrlPathI18n.get(languageId));
		}
		else {
			fieldValues.put(
				"objectEntryFriendlyURL", objectEntry.getFriendlyUrlPath());
		}

		return fieldValues;
	}

	public boolean isDateBusinessType(ObjectField objectField) {
		String businessType =
			(objectField == null) ? null : objectField.getBusinessType();

		if (ObjectFieldConstants.BUSINESS_TYPE_DATE.equals(businessType) ||
			ObjectFieldConstants.BUSINESS_TYPE_DATE_TIME.equals(businessType)) {

			return true;
		}

		return false;
	}

	public String toDisplayValue(
		String languageId, ObjectField objectField, User user, Object value) {

		String businessType =
			(objectField == null) ? null : objectField.getBusinessType();

		if (ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT.equals(
				businessType)) {

			return _toAttachmentDisplayValue(value);
		}

		if (ObjectFieldConstants.BUSINESS_TYPE_BOOLEAN.equals(businessType)) {
			return _toBooleanDisplayValue(languageId, value);
		}

		if (ObjectFieldConstants.BUSINESS_TYPE_DATE.equals(businessType)) {
			return _toDateDisplayValue(_getDateFormat(languageId), value);
		}

		if (ObjectFieldConstants.BUSINESS_TYPE_DATE_TIME.equals(businessType)) {
			return _toDateDisplayValue(
				_getDateTimeFormat(languageId, objectField, user), value);
		}

		if (ObjectFieldConstants.BUSINESS_TYPE_MULTISELECT_PICKLIST.equals(
				businessType)) {

			return _toMultiselectPicklistDisplayValue(
				languageId, objectField, value);
		}

		if (ObjectFieldConstants.BUSINESS_TYPE_PICKLIST.equals(businessType)) {
			return _toPicklistDisplayValue(languageId, objectField, value);
		}

		if (value == null) {
			return StringPool.BLANK;
		}

		if (ObjectFieldConstants.BUSINESS_TYPE_RICH_TEXT.equals(businessType)) {
			return String.valueOf(value);
		}

		return HtmlUtil.escape(String.valueOf(value));
	}

	private Format _getDateFormat(String languageId) {
		Locale locale = LocaleUtil.fromLanguageId(languageId);

		return FastDateFormatFactoryUtil.getSimpleDateFormat(
			_getShortDatePattern(locale), locale,
			TimeZoneUtil.getTimeZone(StringPool.UTC));
	}

	private Format _getDateTimeFormat(
		String languageId, ObjectField objectField, User user) {

		Locale locale = LocaleUtil.fromLanguageId(languageId);

		SimpleDateFormat simpleDateFormat =
			(SimpleDateFormat)DateFormat.getTimeInstance(
				DateFormat.SHORT, locale);

		String timePattern = simpleDateFormat.toPattern();

		timePattern = timePattern.replaceAll("h+", "hh");
		timePattern = timePattern.replaceAll("H+", "HH");

		return FastDateFormatFactoryUtil.getSimpleDateFormat(
			_getShortDatePattern(locale) + ", " + timePattern, locale,
			_getTimeZone(objectField, user));
	}

	private String _getShortDatePattern(Locale locale) {
		SimpleDateFormat simpleDateFormat =
			(SimpleDateFormat)DateFormat.getDateInstance(
				DateFormat.SHORT, locale);

		String pattern = simpleDateFormat.toPattern();

		pattern = pattern.replaceAll("M+", "MM");
		pattern = pattern.replaceAll("d+", "dd");
		pattern = pattern.replaceAll("y+", "yyyy");

		return pattern;
	}

	private TimeZone _getTimeZone(ObjectField objectField, User user) {
		String timeZoneId = ObjectFieldSettingUtil.getTimeZoneId(
			objectField.getObjectFieldSettings(), user);

		if (timeZoneId == null) {
			return TimeZoneUtil.getTimeZone(StringPool.UTC);
		}

		return TimeZoneUtil.getTimeZone(timeZoneId);
	}

	private String _toAttachmentDisplayValue(Object value) {
		if (value == null) {
			return StringPool.BLANK;
		}

		Object idObject = value;

		if (value instanceof Map) {
			Map<?, ?> valueMap = (Map<?, ?>)value;

			idObject = valueMap.get("id");
		}

		long fileEntryId = GetterUtil.getLong(idObject);

		if (fileEntryId == 0) {
			return StringPool.BLANK;
		}

		DLFileEntry dlFileEntry = _dlFileEntryLocalService.fetchDLFileEntry(
			fileEntryId);

		if (dlFileEntry == null) {
			return StringPool.BLANK;
		}

		String fileName = HtmlUtil.escape(dlFileEntry.getFileName());

		try {
			FileEntry fileEntry = _dlAppLocalService.getFileEntry(fileEntryId);

			return StringBundler.concat(
				"<img alt=\"", fileName,
				"\" class=\"cms-compare-versions-attachment\" src=\"",
				_dlURLHelper.getPreviewURL(
					fileEntry, fileEntry.getFileVersion(), null,
					StringPool.BLANK),
				"\" /> ", fileName);
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}

			return fileName;
		}
	}

	private String _toBooleanDisplayValue(String languageId, Object value) {
		Locale locale = LocaleUtil.fromLanguageId(languageId);

		if (GetterUtil.getBoolean(value)) {
			return _language.get(locale, "yes");
		}

		return _language.get(locale, "no");
	}

	private Date _toDate(String valueString) {
		if (StringUtil.endsWith(valueString, CharPool.UPPER_CASE_Z)) {
			return Date.from(Instant.parse(valueString));
		}

		LocalDateTime localDateTime = LocalDateTime.parse(valueString);

		return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
	}

	private String _toDateDisplayValue(Format format, Object value) {
		if (value == null) {
			return StringPool.BLANK;
		}

		String valueString = String.valueOf(value);

		try {
			return format.format(_toDate(valueString));
		}
		catch (DateTimeParseException dateTimeParseException) {
			if (_log.isWarnEnabled()) {
				_log.warn(dateTimeParseException);
			}

			return valueString;
		}
	}

	private String _toMultiselectPicklistDisplayValue(
		String languageId, ObjectField objectField, Object value) {

		Object[] values = null;

		if (value instanceof List) {
			List<?> list = (List<?>)value;

			values = list.toArray();
		}
		else if (value instanceof Object[]) {
			values = (Object[])value;
		}
		else {
			return _toPicklistDisplayValue(languageId, objectField, value);
		}

		StringBundler sb = new StringBundler((values.length * 2) - 1);

		for (int i = 0; i < values.length; i++) {
			if (i > 0) {
				sb.append(", ");
			}

			sb.append(
				_toPicklistDisplayValue(languageId, objectField, values[i]));
		}

		return sb.toString();
	}

	private String _toPicklistDisplayValue(
		String languageId, ObjectField objectField, Object value) {

		Object keyObject = value;

		if (value instanceof Map) {
			Map<?, ?> valueMap = (Map<?, ?>)value;

			keyObject = valueMap.get("key");
		}

		if (keyObject == null) {
			return StringPool.BLANK;
		}

		String key = String.valueOf(keyObject);

		if (key.isEmpty()) {
			return StringPool.BLANK;
		}

		ListTypeEntry listTypeEntry =
			_listTypeEntryLocalService.fetchListTypeEntry(
				objectField.getListTypeDefinitionId(), key);

		if (listTypeEntry == null) {
			return HtmlUtil.escape(key);
		}

		return HtmlUtil.escape(listTypeEntry.getName(languageId));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectEntryVersionFieldValueResolver.class);

	private final DLAppLocalService _dlAppLocalService;
	private final DLFileEntryLocalService _dlFileEntryLocalService;
	private final DLURLHelper _dlURLHelper;
	private final Language _language;
	private final ListTypeEntryLocalService _listTypeEntryLocalService;
	private final ObjectEntryVersionService _objectEntryVersionService;

}