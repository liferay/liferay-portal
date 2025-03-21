/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.dto.v1_0.util;

import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldType;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.headless.delivery.dto.v1_0.ContentDocument;
import com.liferay.headless.delivery.dto.v1_0.ContentField;
import com.liferay.headless.delivery.dto.v1_0.ContentFieldValue;
import com.liferay.headless.delivery.dto.v1_0.Geo;
import com.liferay.headless.delivery.dto.v1_0.StructuredContentLink;
import com.liferay.journal.article.dynamic.data.mapping.form.field.type.constants.JournalArticleDDMFormFieldTypeConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleService;
import com.liferay.layout.dynamic.data.mapping.form.field.type.constants.LayoutDDMFormFieldTypeConstants;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.ws.rs.BadRequestException;

import java.text.ParseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * @author Víctor Galán
 */
public class DDMValueUtil {

	public static Value toDDMValue(
		ContentField contentField, DDMFormField ddmFormField,
		DLAppService dlAppService, long groupId,
		JournalArticleService journalArticleService,
		LayoutLocalService layoutLocalService, Locale preferredLocale) {

		ContentFieldValue contentFieldValue =
			contentField.getContentFieldValue();

		if (contentFieldValue == null) {
			if (Validator.isNull(ddmFormField.getDataType())) {
				return null;
			}

			throw new BadRequestException(
				"No value is specified for field " +
					ddmFormField.getFieldReference());
		}

		Map<String, ContentFieldValue> localizedContentFieldValues =
			contentField.getContentFieldValue_i18n();

		if (Objects.equals(DDMFormFieldType.DATE, ddmFormField.getType()) ||
			Objects.equals(
				DDMFormFieldTypeConstants.DATE, ddmFormField.getType())) {

			return _toDateValue(
				contentFieldValue, ddmFormField, localizedContentFieldValues,
				preferredLocale);
		}
		else if (Objects.equals(
					DDMFormFieldType.DOCUMENT_LIBRARY,
					ddmFormField.getType()) ||
				 Objects.equals(
					 ddmFormField.getType(),
					 DDMFormFieldTypeConstants.DOCUMENT_LIBRARY)) {

			return _toDocumentLibraryValue(
				contentFieldValue, ddmFormField, dlAppService,
				localizedContentFieldValues, preferredLocale);
		}
		else if (Objects.equals(
					DDMFormFieldType.IMAGE, ddmFormField.getType()) ||
				 Objects.equals(
					 DDMFormFieldTypeConstants.IMAGE, ddmFormField.getType())) {

			return _toImageValue(
				contentFieldValue, ddmFormField, dlAppService,
				localizedContentFieldValues, preferredLocale);
		}
		else if (Objects.equals(
					DDMFormFieldType.JOURNAL_ARTICLE, ddmFormField.getType()) ||
				 Objects.equals(
					 ddmFormField.getType(),
					 JournalArticleDDMFormFieldTypeConstants.JOURNAL_ARTICLE)) {

			return _toJournalArticleValue(
				contentFieldValue, ddmFormField, journalArticleService,
				localizedContentFieldValues, preferredLocale);
		}
		else if (Objects.equals(
					DDMFormFieldTypeConstants.CHECKBOX_MULTIPLE,
					ddmFormField.getType()) ||
				 Objects.equals(
					 DDMFormFieldTypeConstants.RADIO, ddmFormField.getType()) ||
				 Objects.equals(
					 DDMFormFieldTypeConstants.SELECT,
					 ddmFormField.getType())) {

			return _toSelectValue(
				contentFieldValue, ddmFormField, localizedContentFieldValues,
				preferredLocale);
		}
		else if (Objects.equals(
					DDMFormFieldType.LINK_TO_PAGE, ddmFormField.getType()) ||
				 Objects.equals(
					 LayoutDDMFormFieldTypeConstants.LINK_TO_LAYOUT,
					 ddmFormField.getType())) {

			return _toLinkToPageValue(
				contentFieldValue, ddmFormField, groupId, layoutLocalService,
				localizedContentFieldValues, preferredLocale);
		}
		else if (Objects.equals(
					DDMFormFieldType.GEOLOCATION, ddmFormField.getType()) ||
				 Objects.equals(
					 DDMFormFieldTypeConstants.GEOLOCATION,
					 ddmFormField.getType())) {

			return _toGeolocationValue(
				contentFieldValue, ddmFormField, localizedContentFieldValues,
				preferredLocale);
		}

		if (ddmFormField.isLocalizable()) {
			return _toLocalizedValue(
				contentFieldValue, localizedContentFieldValues,
				(localizedContentFieldValue, locale) -> GetterUtil.getString(
					localizedContentFieldValue.getData()),
				preferredLocale);
		}

		return new UnlocalizedValue(
			GetterUtil.getString(contentFieldValue.getData()));
	}

	private static Layout _getLayout(
		long groupId, LayoutLocalService layoutLocalService, String link) {

		Layout layout = layoutLocalService.fetchLayoutByFriendlyURL(
			groupId, false, link);

		if (layout == null) {
			layout = layoutLocalService.fetchLayoutByFriendlyURL(
				groupId, true, link);
		}

		if (layout == null) {
			throw new BadRequestException(
				"No page found with friendly URL " + link);
		}

		try {
			LayoutPermissionUtil.check(
				PermissionThreadLocal.getPermissionChecker(), layout,
				ActionKeys.VIEW);
		}
		catch (PortalException portalException) {
			throw new BadRequestException(
				"No page found with friendly URL " + link, portalException);
		}

		return layout;
	}

	private static String _getOptionValues(
		DDMFormField ddmFormField, Locale locale, String optionValues) {

		try {
			List<String> values = new ArrayList<>();

			if (!ddmFormField.isMultiple() &&
				!Objects.equals(
					DDMFormFieldType.CHECKBOX_MULTIPLE,
					ddmFormField.getType())) {

				values.add(optionValues);
			}
			else {
				values.addAll(
					JSONUtil.toStringList(
						JSONFactoryUtil.createJSONArray(optionValues)));
			}

			values = _transformValuesToKeys(ddmFormField, locale, values);

			if ((values.size() == 1) &&
				DDMFormFieldType.RADIO.equals(ddmFormField.getType())) {

				return values.get(0);
			}

			return JSONUtil.toString(JSONFactoryUtil.createJSONArray(values));
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}

			return null;
		}
	}

	private static String _toDateString(
		ContentFieldValue contentFieldValue, Locale locale) {

		if (Validator.isNull(contentFieldValue.getData())) {
			return StringPool.BLANK;
		}

		try {
			return DateUtil.getDate(
				DateUtil.parseDate(
					"yyyy-MM-dd'T'HH:mm:ss'Z'", contentFieldValue.getData(),
					locale),
				"yyyy-MM-dd", locale);
		}
		catch (ParseException parseException) {
			throw new BadRequestException(
				"Unable to parse date that does not conform to ISO-8601",
				parseException);
		}
	}

	private static Value _toDateValue(
		ContentFieldValue contentFieldValue, DDMFormField ddmFormField,
		Map<String, ContentFieldValue> localizedContentFieldValues,
		Locale preferredLocale) {

		if (ddmFormField.isLocalizable()) {
			return _toLocalizedValue(
				contentFieldValue, localizedContentFieldValues,
				DDMValueUtil::_toDateString, preferredLocale);
		}

		return new UnlocalizedValue(
			_toDateString(contentFieldValue, preferredLocale));
	}

	private static Value _toDocumentLibraryValue(
		ContentFieldValue contentFieldValue, DDMFormField ddmFormField,
		DLAppService dlAppService,
		Map<String, ContentFieldValue> localizedContentFieldValues,
		Locale preferredLocale) {

		if (ddmFormField.isLocalizable()) {
			return _toLocalizedValue(
				contentFieldValue, localizedContentFieldValues,
				(localizedContentFieldValue, locale) -> _toDocumentString(
					localizedContentFieldValue, dlAppService),
				preferredLocale);
		}

		return new UnlocalizedValue(
			_toDocumentString(contentFieldValue, dlAppService));
	}

	private static String _toDocumentString(
		ContentFieldValue contentFieldValue, DLAppService dlAppService) {

		String valueString = StringPool.BLANK;

		ContentDocument contentDocument = contentFieldValue.getDocument();

		if ((contentDocument != null) && (contentDocument.getId() != null)) {
			valueString = _toJSON(
				StringPool.BLANK, dlAppService, contentDocument.getId());
		}

		return valueString;
	}

	private static Value _toGeolocationValue(
		ContentFieldValue contentFieldValue, DDMFormField ddmFormField,
		Map<String, ContentFieldValue> localizedContentFieldValues,
		Locale preferredLocale) {

		Geo geo = contentFieldValue.getGeo();

		if (Objects.isNull(geo) || Objects.isNull(geo.getLatitude()) ||
			Objects.isNull(geo.getLongitude())) {

			throw new BadRequestException("Invalid geo " + geo);
		}

		if (ddmFormField.isLocalizable()) {
			return _toLocalizedValue(
				contentFieldValue, localizedContentFieldValues,
				(localizedContentFieldValue, locale) -> JSONUtil.put(
					"lat", geo.getLatitude()
				).put(
					"lng", geo.getLongitude()
				).toString(),
				preferredLocale);
		}

		return new UnlocalizedValue(
			JSONUtil.put(
				"lat", geo.getLatitude()
			).put(
				"lng", geo.getLongitude()
			).toString());
	}

	private static String _toImageString(
		ContentFieldValue contentFieldValue, DLAppService dlAppService) {

		String valueString = StringPool.BLANK;

		ContentDocument contentDocument = contentFieldValue.getImage();

		if ((contentDocument != null) && (contentDocument.getId() != null)) {
			valueString = _toJSON(
				contentDocument.getDescription(), dlAppService,
				contentDocument.getId());
		}

		return valueString;
	}

	private static Value _toImageValue(
		ContentFieldValue contentFieldValue, DDMFormField ddmFormField,
		DLAppService dlAppService,
		Map<String, ContentFieldValue> localizedContentFieldValues,
		Locale preferredLocale) {

		if (ddmFormField.isLocalizable()) {
			return _toLocalizedValue(
				contentFieldValue, localizedContentFieldValues,
				(localizedContentFieldValue, locale) -> _toImageString(
					localizedContentFieldValue, dlAppService),
				preferredLocale);
		}

		return new UnlocalizedValue(
			_toImageString(contentFieldValue, dlAppService));
	}

	private static String _toJournalArticleString(
		ContentFieldValue contentFieldValue,
		JournalArticleService journalArticleService, Locale locale) {

		String valueString = StringPool.BLANK;

		StructuredContentLink structuredContentLink =
			contentFieldValue.getStructuredContentLink();

		if ((structuredContentLink != null) &&
			(structuredContentLink.getId() != null)) {

			JournalArticle journalArticle = null;

			try {
				journalArticle = journalArticleService.getLatestArticle(
					structuredContentLink.getId());
			}
			catch (Exception exception) {
				throw new BadRequestException(
					"No structured content exists with ID " +
						structuredContentLink.getId(),
					exception);
			}

			valueString = JSONUtil.put(
				"className", JournalArticle.class.getName()
			).put(
				"classPK", journalArticle.getResourcePrimKey()
			).put(
				"title", journalArticle.getTitle(locale)
			).toString();
		}

		return valueString;
	}

	private static Value _toJournalArticleValue(
		ContentFieldValue contentFieldValue, DDMFormField ddmFormField,
		JournalArticleService journalArticleService,
		Map<String, ContentFieldValue> localizedContentFieldValues,
		Locale preferredLocale) {

		if (ddmFormField.isLocalizable()) {
			return _toLocalizedValue(
				contentFieldValue, localizedContentFieldValues,
				(localizedContentFieldValue, locale) -> _toJournalArticleString(
					localizedContentFieldValue, journalArticleService, locale),
				preferredLocale);
		}

		return new UnlocalizedValue(
			_toJournalArticleString(
				contentFieldValue, journalArticleService, preferredLocale));
	}

	private static String _toJSON(
		String description, DLAppService dlAppService, long fileEntryId) {

		FileEntry fileEntry = null;

		try {
			fileEntry = dlAppService.getFileEntry(fileEntryId);
		}
		catch (Exception exception) {
			throw new BadRequestException(
				"No document exists with ID " + fileEntryId, exception);
		}

		return JSONUtil.put(
			"alt", description
		).put(
			"classPK", fileEntry.getFileEntryId()
		).put(
			"fileEntryId", fileEntry.getFileEntryId()
		).put(
			"groupId", fileEntry.getGroupId()
		).put(
			"name", fileEntry.getFileName()
		).put(
			"resourcePrimKey", fileEntry.getPrimaryKey()
		).put(
			"title", fileEntry.getFileName()
		).put(
			"type", "document"
		).put(
			"uuid", fileEntry.getUuid()
		).toString();
	}

	private static String _toLinkToPageString(
		ContentFieldValue contentFieldValue, long groupId,
		LayoutLocalService layoutLocalService, Locale locale) {

		String valueString = StringPool.BLANK;

		if (contentFieldValue.getLink() != null) {
			Layout layout = _getLayout(
				groupId, layoutLocalService, contentFieldValue.getLink());

			valueString = JSONUtil.put(
				"groupId", String.valueOf(layout.getGroupId())
			).put(
				"id", layout.getUuid()
			).put(
				"label", layout.getFriendlyURL()
			).put(
				"layoutId", layout.getLayoutId()
			).put(
				"name",
				() -> {
					try {
						return layout.getBreadcrumb(locale);
					}
					catch (Exception exception) {
						if (_log.isDebugEnabled()) {
							_log.debug(exception);
						}

						return StringPool.BLANK;
					}
				}
			).put(
				"privateLayout", layout.isPrivateLayout()
			).toString();
		}

		return valueString;
	}

	private static Value _toLinkToPageValue(
		ContentFieldValue contentFieldValue, DDMFormField ddmFormField,
		long groupId, LayoutLocalService layoutLocalService,
		Map<String, ContentFieldValue> localizedContentFieldValues,
		Locale preferredLocale) {

		if (ddmFormField.isLocalizable()) {
			return _toLocalizedValue(
				contentFieldValue, localizedContentFieldValues,
				(localizedContentFieldValue, locale) -> _toLinkToPageString(
					localizedContentFieldValue, groupId, layoutLocalService,
					locale),
				preferredLocale);
		}

		return new UnlocalizedValue(
			_toLinkToPageString(
				contentFieldValue, groupId, layoutLocalService,
				preferredLocale));
	}

	private static LocalizedValue _toLocalizedValue(
		ContentFieldValue contentFieldValue,
		Map<String, ContentFieldValue> localizedContentFieldValues,
		BiFunction<ContentFieldValue, Locale, String> localizedValueBiFunction,
		Locale preferredLocale) {

		LocalizedValue localizedValue = new LocalizedValue(preferredLocale);

		localizedValue.addString(
			preferredLocale,
			localizedValueBiFunction.apply(contentFieldValue, preferredLocale));

		if (localizedContentFieldValues == null) {
			localizedContentFieldValues = Collections.emptyMap();
		}

		for (Map.Entry<String, ContentFieldValue> entry :
				localizedContentFieldValues.entrySet()) {

			Locale locale = LocaleUtil.fromLanguageId(
				entry.getKey(), true, false);

			if (locale != null) {
				localizedValue.addString(
					locale,
					localizedValueBiFunction.apply(entry.getValue(), locale));
			}
		}

		return localizedValue;
	}

	private static Value _toSelectValue(
		ContentFieldValue contentFieldValue, DDMFormField ddmFormField,
		Map<String, ContentFieldValue> localizedContentFieldValues,
		Locale preferredLocale) {

		if (ddmFormField.isLocalizable()) {
			return _toLocalizedValue(
				contentFieldValue, localizedContentFieldValues,
				(localizedContentFieldValue, locale) -> {
					String optionValues = localizedContentFieldValue.getData();

					String value = localizedContentFieldValue.getValue();

					if (Validator.isNotNull(value)) {
						optionValues = value;
					}

					return _getOptionValues(ddmFormField, locale, optionValues);
				},
				preferredLocale);
		}

		return new UnlocalizedValue(
			_getOptionValues(
				ddmFormField, preferredLocale, contentFieldValue.getValue()));
	}

	private static List<String> _transformValuesToKeys(
		DDMFormField ddmFormField, Locale locale, List<String> values) {

		List<String> keys = new ArrayList<>();

		DDMFormFieldOptions ddmFormFieldOptions =
			ddmFormField.getDDMFormFieldOptions();

		Map<String, LocalizedValue> options = ddmFormFieldOptions.getOptions();
		Map<String, String> optionsReferences =
			ddmFormFieldOptions.getOptionsReferences();

		for (String value : values) {
			if (options.containsKey(value)) {
				keys.add(value);

				continue;
			}

			String key = StringPool.BLANK;

			for (Map.Entry<String, LocalizedValue> entry : options.entrySet()) {
				LocalizedValue localizedValue = entry.getValue();

				if (Objects.equals(localizedValue.getString(locale), value)) {
					key = entry.getKey();

					break;
				}
			}

			if (Validator.isNull(key)) {
				for (Map.Entry<String, String> entry :
						optionsReferences.entrySet()) {

					if (Objects.equals(entry.getValue(), value)) {
						key = entry.getKey();

						break;
					}
				}
			}

			keys.add(key);
		}

		return keys;
	}

	private static final Log _log = LogFactoryUtil.getLog(DDMValueUtil.class);

}