/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.internal.request.helper;

import com.liferay.info.exception.InfoFormFileUploadException;
import com.liferay.info.exception.NoSuchFormVariationException;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.field.RelatedInfoFieldValue;
import com.liferay.info.field.type.BooleanInfoFieldType;
import com.liferay.info.field.type.DateInfoFieldType;
import com.liferay.info.field.type.DateTimeInfoFieldType;
import com.liferay.info.field.type.FileInfoFieldType;
import com.liferay.info.field.type.FriendlyURLInfoFieldType;
import com.liferay.info.field.type.HTMLInfoFieldType;
import com.liferay.info.field.type.LongTextInfoFieldType;
import com.liferay.info.field.type.MultiselectInfoFieldType;
import com.liferay.info.field.type.NumberInfoFieldType;
import com.liferay.info.field.type.RelationshipInfoFieldType;
import com.liferay.info.field.type.SelectInfoFieldType;
import com.liferay.info.field.type.TextInfoFieldType;
import com.liferay.info.form.InfoForm;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFormProvider;
import com.liferay.info.item.updater.InfoItemFieldValuesUpdater;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.FileItem;
import com.liferay.portal.kernel.upload.UploadServletRequest;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.math.BigDecimal;

import java.text.ParseException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author Rubén Pulido
 */
public class InfoRequestFieldValuesProviderHelper {

	public InfoRequestFieldValuesProviderHelper(
		InfoItemServiceRegistry infoItemServiceRegistry) {

		_infoItemServiceRegistry = infoItemServiceRegistry;
	}

	public Map<String, InfoFieldValue<Object>> getInfoFieldValues(
			HttpServletRequest httpServletRequest)
		throws InfoFormFileUploadException {

		Map<String, InfoFieldValue<Object>> infoFieldValues = new HashMap<>();

		UploadServletRequest uploadServletRequest =
			PortalUtil.getUploadServletRequest(httpServletRequest);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)uploadServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String[] checkboxNames = ParamUtil.getStringValues(
			uploadServletRequest, "checkboxNames");
		String className = PortalUtil.getClassName(
			ParamUtil.getLong(uploadServletRequest, "classNameId"));
		String classTypeId = ParamUtil.getString(
			uploadServletRequest, "classTypeId");
		String[] deletedItemIdentifiers = ParamUtil.getStringValues(
			uploadServletRequest, "deletedItemIdentifiers");
		long groupId = ParamUtil.getLong(uploadServletRequest, "groupId");

		Map<String, FileItem[]> multipartParameterMap =
			uploadServletRequest.getMultipartParameterMap();

		Map<String, List<String>> regularParameterMap =
			uploadServletRequest.getRegularParameterMap();

		for (InfoField<?> infoField :
				_getInfoFields(className, classTypeId, groupId)) {

			if (!infoField.isEditable() &&
				!ArrayUtil.contains(
					_ALLOWED_NONEDITABLE_FIELDS, infoField.getName())) {

				continue;
			}

			Set<Locale> availableLocales = LanguageUtil.getAvailableLocales(
				themeDisplay.getSiteGroupId());

			for (String inputName :
					_getInputNames(
						infoField, LocaleUtil.toLanguageIds(availableLocales),
						multipartParameterMap, regularParameterMap)) {

				if (infoField.isLocalizable()) {
					infoFieldValues.put(
						inputName,
						new InfoFieldValue<>(
							infoField,
							InfoLocalizedValue.builder(
							).defaultLocale(
								themeDisplay.getSiteDefaultLocale()
							).<InfoFormFileUploadException>value(
								unsafeBiConsumer -> {
									for (Locale locale : availableLocales) {
										String languageId =
											LanguageUtil.getLanguageId(locale);

										String localizedInputName =
											inputName + StringPool.UNDERLINE +
												languageId;

										Object value = _parseValue(
											groupId, infoField, locale,
											multipartParameterMap,
											localizedInputName,
											regularParameterMap,
											themeDisplay.getUserId());

										if (value != null) {
											unsafeBiConsumer.accept(
												locale, value);
										}
									}
								}
							).build()));

					continue;
				}

				List<String> regularParameters = regularParameterMap.get(
					inputName);

				if (regularParameters == null) {
					if ((infoField.getInfoFieldType() instanceof
							BooleanInfoFieldType) &&
						ArrayUtil.contains(
							checkboxNames, infoField.getUniqueId())) {

						infoFieldValues.put(
							infoField.getUniqueId(),
							_getInfoFieldValue(
								true, infoField, themeDisplay.getLocale(),
								false));

						continue;
					}

					if ((infoField.getInfoFieldType() instanceof
							MultiselectInfoFieldType) &&
						ArrayUtil.contains(
							checkboxNames, infoField.getUniqueId())) {

						infoFieldValues.put(
							infoField.getUniqueId(),
							_getInfoFieldValue(
								true, infoField, themeDisplay.getLocale(),
								Collections.emptyList()));

						continue;
					}
				}

				Object value = _parseValue(
					groupId, infoField, themeDisplay.getLocale(),
					multipartParameterMap, inputName, regularParameterMap,
					themeDisplay.getUserId());

				InfoFieldValue<Object> infoFieldValue = _getInfoFieldValue(
					regularParameters != null, infoField,
					themeDisplay.getLocale(), value);

				if (infoFieldValue != null) {
					infoFieldValues.put(inputName, infoFieldValue);
				}
			}
		}

		InfoField<MultiselectInfoFieldType> infoField = InfoField.builder(
		).infoFieldType(
			MultiselectInfoFieldType.INSTANCE
		).namespace(
			InfoItemFieldValuesUpdater.class.getSimpleName()
		).name(
			"deletedItemIdentifiers"
		).build();

		infoFieldValues.put(
			infoField.getUniqueId(),
			new InfoFieldValue<>(infoField, deletedItemIdentifiers));

		return _computeInfoFieldValues(infoFieldValues);
	}

	private Map<String, InfoFieldValue<Object>> _computeInfoFieldValues(
		Map<String, InfoFieldValue<Object>> initialInfoFieldValues) {

		Map<String, InfoFieldValue<Object>> infoFieldValues = new HashMap<>();

		for (Map.Entry<String, InfoFieldValue<Object>> entry :
				initialInfoFieldValues.entrySet()) {

			String key = entry.getKey();

			if (!_isRelatedKey(key)) {
				infoFieldValues.put(key, entry.getValue());

				continue;
			}

			InfoFieldValue<Object> initialInfoFieldValue = entry.getValue();

			InfoField infoField = initialInfoFieldValue.getInfoField();

			InfoFieldValue<?> infoFieldValue = infoFieldValues.get(
				infoField.getUniqueId());

			if (infoFieldValue == null) {
				infoFieldValue = new InfoFieldValue<>(
					infoField, new RelatedInfoFieldValue<>(new HashMap<>()));
			}

			RelatedInfoFieldValue relatedInfoFieldValue =
				(RelatedInfoFieldValue)infoFieldValue.getValue();

			Map
				<RelatedInfoFieldValue.RelatedInfoFieldValueIdentifier,
				 InfoFieldValue<?>> relatedInfoFieldValues =
					relatedInfoFieldValue.getRelatedInfoFieldValues();

			Tuple tuple = _extractIdentifiers(key);

			relatedInfoFieldValues.put(
				new RelatedInfoFieldValue.RelatedInfoFieldValueIdentifier(
					(String)tuple.getObject(1), (String)tuple.getObject(0)),
				initialInfoFieldValue);

			infoFieldValues.put(
				infoField.getUniqueId(),
				(InfoFieldValue<Object>)infoFieldValue);
		}

		return infoFieldValues;
	}

	private Tuple _extractIdentifiers(String key) {
		int start = key.indexOf("[$") + 2;

		String result = key.substring(
			key.indexOf("[$") + 2, key.indexOf("$]", start));

		return new Tuple(StringUtil.split(result, StringPool.DOLLAR));
	}

	private <T> List<InfoField<?>> _getInfoFields(
		String className, String formVariationKey, long groupId) {

		InfoItemFormProvider<T> infoItemFormProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormProvider.class, className);

		if (infoItemFormProvider == null) {
			return new ArrayList<>();
		}

		try {
			InfoForm infoForm = infoItemFormProvider.getInfoForm(
				formVariationKey, groupId);

			return infoForm.getAllInfoFields();
		}
		catch (NoSuchFormVariationException noSuchFormVariationException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchFormVariationException);
			}

			return new ArrayList<>();
		}
	}

	private InfoFieldValue<Object> _getInfoFieldValue(
		boolean includeNullValues, InfoField<?> infoField, Locale locale,
		Object value) {

		if ((value == null) && !includeNullValues) {
			return null;
		}

		if (infoField.isLocalizable()) {
			return new InfoFieldValue<>(
				infoField,
				InfoLocalizedValue.builder(
				).defaultLocale(
					locale
				).value(
					locale, value
				).build());
		}

		return new InfoFieldValue<>(infoField, value);
	}

	private Set<String> _getInputNames(
		InfoField infoField, String[] languageIds,
		Map<String, FileItem[]> multipartParameterNames,
		Map<String, List<String>> regularParameterNames) {

		Set<String> inputNames = new HashSet<>();

		Set<String> allParameterNames = new HashSet<>(
			multipartParameterNames.keySet());

		allParameterNames.addAll(regularParameterNames.keySet());

		for (String parameterName : allParameterNames) {
			if (parameterName.startsWith(infoField.getUniqueId())) {
				if (!infoField.isLocalizable()) {
					inputNames.add(parameterName);

					continue;
				}

				for (String languageId : languageIds) {
					if (parameterName.endsWith(languageId)) {
						inputNames.add(
							StringUtil.replaceLast(
								parameterName,
								StringPool.UNDERLINE + languageId,
								StringPool.BLANK));

						break;
					}
				}
			}
		}

		return inputNames;
	}

	private boolean _isRelatedKey(String key) {
		if (key.contains("[$") && key.contains("$]")) {
			return true;
		}

		return false;
	}

	private Object _parseValue(
			long groupId, InfoField<?> infoField, Locale locale,
			Map<String, FileItem[]> multipartParameterMap, String name,
			Map<String, List<String>> regularParameterMap, long userId)
		throws InfoFormFileUploadException {

		if ((infoField.getInfoFieldType() instanceof FileInfoFieldType) &&
			multipartParameterMap.containsKey(name)) {

			FileItem[] fileItems = multipartParameterMap.get(name);

			if (ArrayUtil.isEmpty(fileItems)) {
				return null;
			}

			FileItem fileItem = fileItems[0];

			if ((fileItem.getSize() < 0) ||
				Validator.isNull(fileItem.getFileName())) {

				return StringPool.BLANK;
			}

			try (InputStream inputStream = fileItem.getInputStream()) {
				if (inputStream == null) {
					throw new InfoFormFileUploadException(
						infoField.getUniqueId());
				}

				File file = FileUtil.createTempFile(inputStream);

				if (file == null) {
					throw new InfoFormFileUploadException(
						infoField.getUniqueId());
				}

				FileEntry fileEntry = TempFileEntryUtil.addTempFileEntry(
					groupId, userId,
					InfoRequestFieldValuesProviderHelper.class.getName(),
					TempFileEntryUtil.getTempFileName(fileItem.getFileName()),
					file, fileItem.getContentType());

				return fileEntry.getFileEntryId();
			}
			catch (IOException | PortalException exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}

				throw new InfoFormFileUploadException(infoField.getUniqueId());
			}
		}

		List<String> values = regularParameterMap.get(name);

		if (values == null) {
			return null;
		}

		if (infoField.getInfoFieldType() instanceof MultiselectInfoFieldType) {
			return ListUtil.filter(values, Validator::isNotNull);
		}

		if (ListUtil.isEmpty(values)) {
			return null;
		}

		String value = values.get(0);

		if (infoField.getInfoFieldType() instanceof BooleanInfoFieldType) {
			return GetterUtil.getBoolean(value);
		}

		if (infoField.getInfoFieldType() instanceof DateInfoFieldType) {
			if (Validator.isBlank(value)) {
				return StringPool.BLANK;
			}

			try {
				return DateUtil.parseDate("yyyy-MM-dd", value, locale);
			}
			catch (ParseException parseException) {
				if (_log.isDebugEnabled()) {
					_log.debug(parseException);
				}
			}
		}

		if (infoField.getInfoFieldType() instanceof DateTimeInfoFieldType) {
			if (Validator.isBlank(value)) {
				return StringPool.BLANK;
			}

			try {
				return LocalDateTime.parse(
					value, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
			}
			catch (DateTimeParseException dateTimeParseException) {
				if (_log.isDebugEnabled()) {
					_log.debug(dateTimeParseException);
				}
			}
		}

		if (infoField.getInfoFieldType() instanceof NumberInfoFieldType) {
			InfoField<NumberInfoFieldType> numberInfoFieldTypeInfoField =
				(InfoField<NumberInfoFieldType>)infoField;

			if (GetterUtil.getBoolean(
					numberInfoFieldTypeInfoField.getAttribute(
						NumberInfoFieldType.DECIMAL))) {

				if (Validator.isBlank(value)) {
					return StringPool.BLANK;
				}

				return new BigDecimal(value);
			}

			return GetterUtil.getLong(value);
		}

		if (infoField.getInfoFieldType() instanceof FileInfoFieldType ||
			infoField.getInfoFieldType() instanceof FriendlyURLInfoFieldType ||
			infoField.getInfoFieldType() instanceof HTMLInfoFieldType ||
			infoField.getInfoFieldType() instanceof LongTextInfoFieldType ||
			infoField.getInfoFieldType() instanceof RelationshipInfoFieldType ||
			infoField.getInfoFieldType() instanceof SelectInfoFieldType ||
			infoField.getInfoFieldType() instanceof TextInfoFieldType) {

			return value;
		}

		return null;
	}

	private static final String[] _ALLOWED_NONEDITABLE_FIELDS = {
		"expirationDate", "reviewDate"
	};

	private static final Log _log = LogFactoryUtil.getLog(
		InfoRequestFieldValuesProviderHelper.class);

	private final InfoItemServiceRegistry _infoItemServiceRegistry;

}