/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.info.item.provider;

import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.info.field.converter.DDMFormFieldInfoFieldConverter;
import com.liferay.dynamic.data.mapping.info.item.provider.DDMFormValuesInfoFieldValuesProvider;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldType;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.constants.FieldConstants;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.info.type.KeyLocalizedLabelPair;
import com.liferay.info.type.WebImage;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.GroupedModel;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.text.DateFormat;
import java.text.NumberFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(service = DDMFormValuesInfoFieldValuesProvider.class)
public class DDMFormValuesInfoFieldValuesProviderImpl
	implements DDMFormValuesInfoFieldValuesProvider<GroupedModel> {

	public List<InfoFieldValue<InfoLocalizedValue<Object>>> getInfoFieldValues(
		GroupedModel groupedModel, DDMFormValues ddmFormValues) {

		List<InfoFieldValue<InfoLocalizedValue<Object>>> infoFieldValues =
			new ArrayList<>();

		Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap =
			ddmFormValues.getDDMFormFieldValuesMap(true);

		for (List<DDMFormFieldValue> ddmFormFieldValues :
				ddmFormFieldValuesMap.values()) {

			for (DDMFormFieldValue ddmFormFieldValue : ddmFormFieldValues) {
				if (StringUtil.equals(
						ddmFormFieldValue.getType(),
						DDMFormFieldTypeConstants.FIELDSET) ||
					StringUtil.equals(
						ddmFormFieldValue.getType(),
						DDMFormFieldTypeConstants.SEPARATOR)) {

					continue;
				}

				Value value = ddmFormFieldValue.getValue();

				if ((value == null) ||
					(ddmFormFieldValue.getDDMFormField() == null)) {

					continue;
				}

				infoFieldValues.add(
					new InfoFieldValue<>(
						_ddmFormFieldInfoFieldConverter.convert(
							ddmFormFieldValue.getDDMFormField()),
						InfoLocalizedValue.builder(
						).defaultLocale(
							value.getDefaultLocale()
						).value(
							consumer -> {
								for (Locale locale :
										value.getAvailableLocales()) {

									consumer.accept(
										locale,
										_sanitizeDDMFormFieldValue(
											groupedModel, ddmFormFieldValue,
											locale));
								}
							}
						).build()));
			}
		}

		return infoFieldValues;
	}

	private WebImage _getWebImage(JSONObject jsonObject) {
		try {
			String uuid = jsonObject.getString("uuid");
			long groupId = jsonObject.getLong("groupId");

			if (Validator.isNull(uuid) && (groupId == 0)) {
				return null;
			}

			FileEntry fileEntry = _dlAppService.getFileEntryByUuidAndGroupId(
				uuid, groupId);

			WebImage webImage = new WebImage(
				_dlURLHelper.getDownloadURL(
					fileEntry, fileEntry.getFileVersion(), null,
					StringPool.BLANK),
				new InfoItemReference(
					FileEntry.class.getName(),
					new ClassPKInfoItemIdentifier(fileEntry.getFileEntryId())));

			webImage.setAlt(jsonObject.getString("alt"));

			return webImage;
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	private Object _sanitizeDDMFormFieldValue(
		GroupedModel groupedModel, DDMFormFieldValue ddmFormFieldValue,
		Locale locale) {

		Value value = ddmFormFieldValue.getValue();

		String valueString = value.getString(locale);

		try {
			if (Objects.equals(
					ddmFormFieldValue.getType(), DDMFormFieldType.CHECKBOX) ||
				Objects.equals(ddmFormFieldValue.getType(), "boolean")) {

				return GetterUtil.getBoolean(valueString);
			}

			if (Objects.equals(
					ddmFormFieldValue.getType(),
					DDMFormFieldTypeConstants.CHECKBOX_MULTIPLE) ||
				Objects.equals(
					ddmFormFieldValue.getType(),
					DDMFormFieldTypeConstants.SELECT)) {

				if (Validator.isNull(valueString)) {
					return null;
				}

				JSONArray optionValuesJSONArray = null;

				try {
					optionValuesJSONArray = _jsonFactory.createJSONArray(
						valueString);
				}
				catch (JSONException jsonException) {
					if (_log.isDebugEnabled()) {
						_log.debug(jsonException);
					}
				}

				if (optionValuesJSONArray == null) {
					return null;
				}

				DDMFormField ddmFormField = ddmFormFieldValue.getDDMFormField();

				DDMFormFieldOptions ddmFormFieldOptions =
					ddmFormField.getDDMFormFieldOptions();

				List<KeyLocalizedLabelPair> keyLocalizedLabelPairs =
					new ArrayList<>();

				for (int i = 0; i < optionValuesJSONArray.length(); i++) {
					String optionValue = optionValuesJSONArray.getString(i);

					LocalizedValue localizedValue =
						ddmFormFieldOptions.getOptionLabels(optionValue);

					if (localizedValue == null) {
						continue;
					}

					keyLocalizedLabelPairs.add(
						new KeyLocalizedLabelPair(
							optionValue,
							InfoLocalizedValue.<String>builder(
							).defaultLocale(
								localizedValue.getDefaultLocale()
							).values(
								localizedValue.getValues()
							).build()));
				}

				return keyLocalizedLabelPairs;
			}

			if (Objects.equals(
					ddmFormFieldValue.getType(), DDMFormFieldType.DATE) ||
				Objects.equals(
					ddmFormFieldValue.getType(),
					DDMFormFieldTypeConstants.DATE)) {

				if (Validator.isNull(valueString)) {
					return null;
				}

				if (locale.equals(LocaleUtil.ROOT)) {
					locale = LocaleUtil.getSiteDefault();
				}

				DateFormat dateFormat = DateFormat.getDateInstance(
					DateFormat.SHORT, locale);

				Date date = DateUtil.parseDate(
					"yyyy-MM-dd", valueString, locale);

				return dateFormat.format(date);
			}

			if (Objects.equals(
					ddmFormFieldValue.getType(),
					DDMFormFieldTypeConstants.DATE_TIME)) {

				if (Validator.isNull(valueString)) {
					return null;
				}

				if (locale.equals(LocaleUtil.ROOT)) {
					locale = LocaleUtil.getSiteDefault();
				}

				return DateUtil.parseDate(
					"yyyy-MM-dd hh:mm", valueString, locale);
			}

			if (Objects.equals(
					ddmFormFieldValue.getType(), DDMFormFieldType.DECIMAL) ||
				Objects.equals(
					ddmFormFieldValue.getType(), DDMFormFieldType.NUMERIC)) {

				if (Validator.isNull(valueString)) {
					return null;
				}

				NumberFormat numberFormat = NumberFormat.getNumberInstance(
					locale);

				DDMFormField ddmFormField = ddmFormFieldValue.getDDMFormField();

				String dataType = ddmFormField.getDataType();

				if (dataType.equals(FieldConstants.DOUBLE) ||
					dataType.equals(FieldConstants.FLOAT)) {

					numberFormat.setMinimumFractionDigits(1);
				}

				return numberFormat.format(numberFormat.parse(valueString));
			}

			if (Objects.equals(
					ddmFormFieldValue.getType(), DDMFormFieldType.IMAGE) ||
				Objects.equals(
					ddmFormFieldValue.getType(),
					DDMFormFieldTypeConstants.IMAGE)) {

				return _getWebImage(_jsonFactory.createJSONObject(valueString));
			}

			if (Objects.equals(
					ddmFormFieldValue.getType(),
					DDMFormFieldTypeConstants.LINK_TO_LAYOUT)) {

				if (Validator.isNull(valueString)) {
					return StringPool.BLANK;
				}

				JSONObject jsonObject = _jsonFactory.createJSONObject(
					valueString);

				Layout layout = _layoutLocalService.fetchLayout(
					jsonObject.getLong("groupId"),
					jsonObject.getBoolean("privateLayout"),
					jsonObject.getLong("layoutId"));

				if (layout == null) {
					return StringPool.BLANK;
				}

				ServiceContext serviceContext =
					ServiceContextThreadLocal.getServiceContext();

				if (serviceContext == null) {
					return StringPool.BLANK;
				}

				ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

				if (themeDisplay == null) {
					return StringPool.BLANK;
				}

				return _portal.getLayoutFriendlyURL(
					layout, themeDisplay, locale);
			}

			if (Objects.equals(
					ddmFormFieldValue.getType(),
					DDMFormFieldTypeConstants.RADIO)) {

				if (Validator.isNull(valueString)) {
					return null;
				}

				DDMFormField ddmFormField = ddmFormFieldValue.getDDMFormField();

				DDMFormFieldOptions ddmFormFieldOptions =
					ddmFormField.getDDMFormFieldOptions();

				LocalizedValue localizedValue =
					ddmFormFieldOptions.getOptionLabels(valueString);

				if (localizedValue == null) {
					return Collections.emptyList();
				}

				return ListUtil.fromArray(
					new KeyLocalizedLabelPair(
						valueString,
						InfoLocalizedValue.<String>builder(
						).defaultLocale(
							localizedValue.getDefaultLocale()
						).values(
							localizedValue.getValues()
						).build()));
			}

			if (Objects.equals(
					ddmFormFieldValue.getType(),
					DDMFormFieldTypeConstants.TEXT)) {

				return valueString;
			}

			return SanitizerUtil.sanitize(
				groupedModel.getCompanyId(), groupedModel.getGroupId(),
				groupedModel.getUserId(), groupedModel.getModelClassName(),
				(long)groupedModel.getPrimaryKeyObj(), ContentTypes.TEXT_HTML,
				Sanitizer.MODE_ALL, valueString, null);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to sanitize field " + ddmFormFieldValue.getName(),
				exception);

			return valueString;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDMFormValuesInfoFieldValuesProviderImpl.class);

	@Reference
	private DDMFormFieldInfoFieldConverter _ddmFormFieldInfoFieldConverter;

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private DLURLHelper _dlURLHelper;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

}