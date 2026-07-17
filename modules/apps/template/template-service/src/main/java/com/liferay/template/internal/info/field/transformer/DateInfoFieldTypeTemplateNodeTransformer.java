/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.template.internal.info.field.transformer;

import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.field.type.InfoFieldType;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.templateparser.TemplateNode;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.template.info.field.transformer.BaseTemplateNodeTransformer;
import com.liferay.template.info.field.transformer.TemplateNodeTransformer;

import java.text.DateFormat;
import java.text.ParseException;

import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = "info.field.type.class.name=com.liferay.info.field.type.DateInfoFieldType",
	service = TemplateNodeTransformer.class
)
public class DateInfoFieldTypeTemplateNodeTransformer
	extends BaseTemplateNodeTransformer {

	@Override
	public TemplateNode transform(
		InfoFieldValue<Object> infoFieldValue, ThemeDisplay themeDisplay) {

		String stringValue = StringPool.BLANK;

		Object value = infoFieldValue.getValue(themeDisplay.getLocale());

		InfoField infoField = infoFieldValue.getInfoField();

		if (value instanceof Date) {
			DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
				_getShortTimeStylePattern(themeDisplay.getLocale()),
				themeDisplay.getLocale());

			stringValue = dateFormat.format((Date)value);
		}
		else if (value instanceof String) {
			Locale dateLocale = LocaleUtil.getSiteDefault();

			if (infoField.isLocalizable()) {
				InfoLocalizedValue<String> infoLocalizedValue =
					(InfoLocalizedValue<String>)infoFieldValue.getValue();

				dateLocale = infoLocalizedValue.getDefaultLocale();

				Set<Locale> availableLocales =
					infoLocalizedValue.getAvailableLocales();

				if (availableLocales.contains(themeDisplay.getLocale())) {
					dateLocale = themeDisplay.getLocale();
				}
			}

			DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
				_getShortTimeStylePattern(dateLocale),
				themeDisplay.getLocale());

			try {
				Date date = dateFormat.parse(value.toString());

				dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
					_getDefaultPattern(themeDisplay.getLocale()),
					themeDisplay.getLocale());

				stringValue = dateFormat.format(date);
			}
			catch (ParseException parseException1) {
				if (_log.isDebugEnabled()) {
					_log.debug(parseException1);
				}

				dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
					_getDefaultPattern(dateLocale), themeDisplay.getLocale());

				try {
					Date date = dateFormat.parse(value.toString());

					dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
						_getDefaultPattern(themeDisplay.getLocale()),
						themeDisplay.getLocale());

					stringValue = dateFormat.format(date);
				}
				catch (ParseException parseException2) {
					if (_log.isDebugEnabled()) {
						_log.debug(parseException2);
					}

					stringValue = value.toString();
				}
			}
		}

		InfoFieldType infoFieldType = infoField.getInfoFieldType();

		return new TemplateNode(
			themeDisplay, infoField.getName(), stringValue,
			infoFieldType.getName(), Collections.emptyMap());
	}

	private String _getDefaultPattern(Locale locale) {
		String defaultPattern = _defaultPatterns.get(locale);

		if (defaultPattern != null) {
			return defaultPattern;
		}

		defaultPattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(
			FormatStyle.SHORT, null, IsoChronology.INSTANCE, locale);

		_defaultPatterns.put(locale, defaultPattern);

		return defaultPattern;
	}

	private String _getShortTimeStylePattern(Locale locale) {
		String shortTimeStylePattern = _shortTimeStylePatterns.get(locale);

		if (shortTimeStylePattern != null) {
			return shortTimeStylePattern;
		}

		shortTimeStylePattern =
			DateTimeFormatterBuilder.getLocalizedDateTimePattern(
				FormatStyle.SHORT, FormatStyle.SHORT, IsoChronology.INSTANCE,
				locale);

		_shortTimeStylePatterns.put(locale, shortTimeStylePattern);

		return shortTimeStylePattern;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DateInfoFieldTypeTemplateNodeTransformer.class);

	private static final Map<Locale, String> _defaultPatterns = new HashMap<>();
	private static final Map<Locale, String> _shortTimeStylePatterns =
		new HashMap<>();

}