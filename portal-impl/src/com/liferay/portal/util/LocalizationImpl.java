/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.util;

import com.liferay.petra.concurrent.ConcurrentReferenceKeyHashMap;
import com.liferay.petra.io.unsync.UnsyncStringReader;
import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;
import com.liferay.petra.memory.FinalizeManager;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.xml.SecureXMLFactoryProviderUtil;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;
import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.PrefsParamUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.XMLUtil;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.Serializable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Function;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

/**
 * @author Alexander Chow
 * @author Jorge Ferrer
 * @author Mauro Mariuzzo
 * @author Julio Camarero
 * @author Brian Wing Shun Chan
 * @author Connor McKay
 */
public class LocalizationImpl implements Localization {

	@Override
	public Object deserialize(JSONObject jsonObject) {
		Map<Locale, String> map = new HashMap<>();

		for (Locale locale : LanguageUtil.getAvailableLocales()) {
			String languageId = LocaleUtil.toLanguageId(locale);

			String value = jsonObject.getString(languageId);

			if (Validator.isNotNull(value)) {
				map.put(locale, value);
			}
		}

		return map;
	}

	@Override
	public String[] getAvailableLanguageIds(Document document) {
		if (document == null) {
			return new String[0];
		}

		String attributeValue = _getRootAttributeValue(
			document, _AVAILABLE_LOCALES, StringPool.BLANK);

		return StringUtil.split(attributeValue);
	}

	@Override
	public String[] getAvailableLanguageIds(String xml) {
		String attributeValue = _getRootAttributeValue(
			xml, _AVAILABLE_LOCALES, StringPool.BLANK);

		return StringUtil.split(attributeValue);
	}

	@Override
	public Locale getDefaultImportLocale(
		String className, long classPK, Locale contentDefaultLocale,
		Locale[] contentAvailableLocales) {

		return getDefaultImportLocale(
			className, (Serializable)classPK, contentDefaultLocale,
			contentAvailableLocales);
	}

	@Override
	public Locale getDefaultImportLocale(
		String className, Serializable primaryKey, Locale contentDefaultLocale,
		Locale[] contentAvailableLocales) {

		if (LanguageUtil.isAvailableLocale(contentDefaultLocale)) {
			return contentDefaultLocale;
		}

		Locale defaultLocale = LocaleUtil.getSiteDefault();

		if (ArrayUtil.contains(contentAvailableLocales, defaultLocale)) {
			return defaultLocale;
		}

		for (Locale contentAvailableLocale : contentAvailableLocales) {
			if (LanguageUtil.isAvailableLocale(contentAvailableLocale)) {
				return contentAvailableLocale;
			}
		}

		if (_log.isWarnEnabled()) {
			_log.warn(
				StringBundler.concat(
					"Language ", LocaleUtil.toLanguageId(contentDefaultLocale),
					" is missing for ", className, " with primary key ",
					primaryKey, ". Setting default language to ",
					LocaleUtil.toLanguageId(defaultLocale), "."));
		}

		return defaultLocale;
	}

	@Override
	public String getDefaultLanguageId(Document document) {
		return getDefaultLanguageId(document, LocaleUtil.getSiteDefault());
	}

	@Override
	public String getDefaultLanguageId(
		Document document, Locale defaultLocale) {

		String defaultLanguageId = LocaleUtil.toLanguageId(defaultLocale);

		return _getRootAttributeValue(
			document, _DEFAULT_LOCALE, defaultLanguageId);
	}

	@Override
	public String getDefaultLanguageId(String xml) {
		return getDefaultLanguageId(xml, LocaleUtil.getSiteDefault());
	}

	@Override
	public String getDefaultLanguageId(String xml, Locale defaultLocale) {
		String defaultLanguageId = LocaleUtil.toLanguageId(defaultLocale);

		return _getRootAttributeValue(xml, _DEFAULT_LOCALE, defaultLanguageId);
	}

	@Override
	public String getLocalization(
		Function<String, String> localizationFunction,
		String requestedLanguageId, String defaultLanguageId) {

		String value = localizationFunction.apply(requestedLanguageId);

		if (!Validator.isBlank(value)) {
			return value;
		}

		if (!Validator.isBlank(defaultLanguageId)) {
			return localizationFunction.apply(defaultLanguageId);
		}

		return StringPool.BLANK;
	}

	@Override
	public String getLocalization(String xml, String requestedLanguageId) {
		return getLocalization(xml, requestedLanguageId, true);
	}

	@Override
	public String getLocalization(
		String xml, String requestedLanguageId, boolean useDefault) {

		return getLocalization(
			xml, requestedLanguageId, useDefault, StringPool.BLANK);
	}

	@Override
	public String getLocalization(
		String xml, String requestedLanguageId, boolean useDefault,
		String defaultValue) {

		if (!Validator.isXml(xml)) {
			if (useDefault ||
				requestedLanguageId.equals(
					LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()))) {

				return xml;
			}

			return defaultValue;
		}

		String value = _getCachedValue(xml, requestedLanguageId, useDefault);

		if (value != null) {
			return value;
		}

		String priorityLanguageId = null;

		Locale requestedLocale = LocaleUtil.fromLanguageId(requestedLanguageId);

		if (useDefault &&
			LanguageUtil.isDuplicateLanguageCode(
				requestedLocale.getLanguage()) &&
			!requestedLanguageId.equals(priorityLanguageId)) {

			Locale priorityLocale = LanguageUtil.getLocale(
				requestedLocale.getLanguage());

			priorityLanguageId = LocaleUtil.toLanguageId(priorityLocale);
		}

		XMLStreamReader xmlStreamReader = null;

		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				PortalClassLoaderUtil.getClassLoader())) {

			XMLInputFactory xmlInputFactory =
				SecureXMLFactoryProviderUtil.newXMLInputFactory();

			xmlStreamReader = xmlInputFactory.createXMLStreamReader(
				new UnsyncStringReader(xml));

			String defaultLanguageId = StringPool.BLANK;

			// Skip root node

			if (xmlStreamReader.hasNext()) {
				xmlStreamReader.nextTag();

				defaultLanguageId = xmlStreamReader.getAttributeValue(
					null, _DEFAULT_LOCALE);

				if (Validator.isNull(defaultLanguageId)) {
					defaultLanguageId = LocaleUtil.toLanguageId(
						LocaleUtil.getSiteDefault());
				}
			}

			// Find specified language and/or default language

			String priorityValue = null;
			String defaultLocalizationValue = null;

			while (xmlStreamReader.hasNext()) {
				int event = xmlStreamReader.next();

				if (event == XMLStreamConstants.START_ELEMENT) {
					String languageId = xmlStreamReader.getAttributeValue(
						null, _LANGUAGE_ID);

					if (Validator.isNull(languageId)) {
						languageId = defaultLanguageId;
					}

					if (languageId.equals(defaultLanguageId) ||
						languageId.equals(priorityLanguageId) ||
						languageId.equals(requestedLanguageId)) {

						String text = xmlStreamReader.getElementText();

						if (languageId.equals(defaultLanguageId)) {
							defaultLocalizationValue = text;
						}

						if (languageId.equals(priorityLanguageId)) {
							priorityValue = text;
						}

						if (languageId.equals(requestedLanguageId)) {
							value = text;
						}

						if (Validator.isNotNull(value)) {
							break;
						}
					}
				}
				else if (event == XMLStreamConstants.END_DOCUMENT) {
					break;
				}
			}

			if (useDefault && Validator.isNotNull(priorityLanguageId) &&
				Validator.isNull(value) && Validator.isNotNull(priorityValue)) {

				value = priorityValue;
			}

			if (useDefault && Validator.isNull(value)) {
				value = defaultLocalizationValue;
			}

			if (Validator.isNull(value)) {
				value = defaultValue;
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}
		finally {
			if (xmlStreamReader != null) {
				try {
					xmlStreamReader.close();
				}
				catch (Exception exception) {
					if (_log.isDebugEnabled()) {
						_log.debug(exception);
					}
				}
			}
		}

		_setCachedValue(xml, requestedLanguageId, useDefault, value);

		return value;
	}

	@Override
	public Map<Locale, String> getLocalizationMap(
		Collection<Locale> locales, Locale defaultLocale, String key) {

		Map<Locale, String> map = new HashMap<>();

		String defaultValue = LanguageUtil.get(defaultLocale, key);

		for (Locale locale : locales) {
			String value = LanguageUtil.get(locale, key);

			if (!locale.equals(defaultLocale) && value.equals(defaultValue)) {
				continue;
			}

			map.put(locale, value);
		}

		return map;
	}

	@Override
	public Map<Locale, String> getLocalizationMap(
		HttpServletRequest httpServletRequest, String parameter) {

		Map<Locale, String> map = new HashMap<>();

		for (Locale locale : LanguageUtil.getAvailableLocales()) {
			String localizedParameter = getLocalizedName(
				parameter, LocaleUtil.toLanguageId(locale));

			map.put(
				locale,
				ParamUtil.getString(httpServletRequest, localizedParameter));
		}

		return map;
	}

	@Override
	public Map<Locale, String> getLocalizationMap(
		PortletPreferences portletPreferences, String preferenceName) {

		return getLocalizationMap(portletPreferences, preferenceName, null);
	}

	@Override
	public Map<Locale, String> getLocalizationMap(
		PortletPreferences portletPreferences, String preferenceName,
		String propertyName) {

		String defaultPropertyValue = null;

		if (propertyName != null) {
			defaultPropertyValue = PropsUtil.get(propertyName);
		}

		Class<?> clazz = getClass();

		return getLocalizationMap(
			portletPreferences, preferenceName, propertyName,
			defaultPropertyValue, clazz.getClassLoader());
	}

	@Override
	public Map<Locale, String> getLocalizationMap(
		PortletPreferences portletPreferences, String preferenceName,
		String propertyName, String defaultPropertyValue,
		ClassLoader classLoader) {

		Map<Locale, String> map = new HashMap<>();

		for (Locale locale : LanguageUtil.getAvailableLocales()) {
			String localizedPreference = getLocalizedName(
				preferenceName, LocaleUtil.toLanguageId(locale));

			map.put(
				locale,
				portletPreferences.getValue(
					localizedPreference, StringPool.BLANK));
		}

		if (Validator.isNull(propertyName)) {
			return map;
		}

		Locale defaultLocale = LocaleUtil.getSiteDefault();

		String defaultValue = map.get(defaultLocale);

		if (Validator.isNotNull(defaultValue)) {
			return map;
		}

		try {
			map.put(
				defaultLocale,
				StringUtil.read(classLoader, defaultPropertyValue));
		}
		catch (IOException ioException) {
			_log.error(
				"Unable to read the content for " + defaultPropertyValue,
				ioException);
		}

		return map;
	}

	@Override
	public Map<Locale, String> getLocalizationMap(
		PortletRequest portletRequest, String parameter) {

		return getLocalizationMap(
			portletRequest, parameter, new HashMap<Locale, String>());
	}

	@Override
	public Map<Locale, String> getLocalizationMap(
		PortletRequest portletRequest, String parameter,
		Map<Locale, String> defaultValues) {

		Map<Locale, String> map = new HashMap<>();

		for (Locale locale : LanguageUtil.getAvailableLocales()) {
			String localizedParameter = getLocalizedName(
				parameter, LocaleUtil.toLanguageId(locale));

			map.put(
				locale,
				ParamUtil.getString(
					portletRequest, localizedParameter,
					defaultValues.get(locale)));
		}

		return map;
	}

	@Override
	public Map<Locale, String> getLocalizationMap(String xml) {
		return getLocalizationMap(xml, false);
	}

	@Override
	public Map<Locale, String> getLocalizationMap(
		String xml, boolean useDefault) {

		Map<Locale, String> map = new HashMap<>();

		for (Locale locale : LanguageUtil.getAvailableLocales()) {
			String languageId = LocaleUtil.toLanguageId(locale);

			String value = getLocalization(xml, languageId, useDefault);

			if (Validator.isNotNull(value)) {
				map.put(locale, value);
			}
		}

		return map;
	}

	@Override
	public Map<Locale, String> getLocalizationMap(
		String bundleName, ClassLoader classLoader, String key,
		boolean includeBetaLocales) {

		if (key == null) {
			return null;
		}

		Locale defaultLocale = LocaleUtil.getSiteDefault();

		String defaultValue = _getLocalization(
			bundleName, defaultLocale, classLoader, key, key);

		Map<Locale, String> map = HashMapBuilder.put(
			defaultLocale, defaultValue
		).build();

		Set<Locale> locales = null;

		if (includeBetaLocales) {
			locales = LanguageUtil.getAvailableLocales();
		}
		else {
			locales = LanguageUtil.getSupportedLocales();
		}

		for (Locale locale : locales) {
			if (locale.equals(defaultLocale)) {
				continue;
			}

			String value = _getLocalization(
				bundleName, locale, classLoader, key, null);

			if (Validator.isNotNull(value) && !value.equals(defaultValue)) {
				map.put(locale, value);
			}
		}

		return map;
	}

	@Override
	public Map<Locale, String> getLocalizationMap(
		String[] languageIds, String[] values) {

		Map<Locale, String> map = new HashMap<>();

		for (int i = 0; i < values.length; i++) {
			Locale locale = LocaleUtil.fromLanguageId(languageIds[i]);

			map.put(locale, values[i]);
		}

		return map;
	}

	@Override
	public String getLocalizationXmlFromPreferences(
		PortletPreferences portletPreferences, PortletRequest portletRequest,
		String parameter) {

		return getLocalizationXmlFromPreferences(
			portletPreferences, portletRequest, parameter, null, null);
	}

	@Override
	public String getLocalizationXmlFromPreferences(
		PortletPreferences portletPreferences, PortletRequest portletRequest,
		String parameter, String defaultValue) {

		return getLocalizationXmlFromPreferences(
			portletPreferences, portletRequest, parameter, null, defaultValue);
	}

	@Override
	public String getLocalizationXmlFromPreferences(
		PortletPreferences portletPreferences, PortletRequest portletRequest,
		String parameter, String prefix, String defaultValue) {

		String xml = null;

		Locale defaultLocale = LocaleUtil.getSiteDefault();

		String defaultLanguageId = LocaleUtil.toLanguageId(defaultLocale);

		for (Locale locale : LanguageUtil.getAvailableLocales()) {
			String languageId = LocaleUtil.toLanguageId(locale);

			String localizedKey = getLocalizedName(parameter, languageId);

			String prefixedLocalizedKey = localizedKey;

			if (Validator.isNotNull(prefix)) {
				prefixedLocalizedKey = StringBundler.concat(
					prefix, "--", localizedKey, "--");
			}

			String value = ParamUtil.getString(
				portletRequest, prefixedLocalizedKey,
				portletPreferences.getValue(localizedKey, null));

			if (value != null) {
				xml = updateLocalization(xml, parameter, value, languageId);
			}
		}

		if (getLocalization(xml, defaultLanguageId, true, null) == null) {
			String oldValue = PrefsParamUtil.getString(
				portletPreferences, portletRequest, parameter, defaultValue);

			if (Validator.isNotNull(oldValue)) {
				xml = updateLocalization(xml, parameter, oldValue);
			}
		}

		return xml;
	}

	@Override
	public String getLocalizedName(String name, String languageId) {
		return StringBundler.concat(name, StringPool.UNDERLINE, languageId);
	}

	@Override
	public Map<Locale, String> getMap(LocalizedValuesMap localizedValuesMap) {
		Map<Locale, String> map = localizedValuesMap.getValues();

		Locale locale = LocaleUtil.getDefault();

		if (map.get(locale) == null) {
			map.put(locale, localizedValuesMap.getDefaultValue());
		}

		return map;
	}

	@Override
	public String getPreferencesValue(
		PortletPreferences portletPreferences, String key, String languageId) {

		return getPreferencesValue(portletPreferences, key, languageId, true);
	}

	@Override
	public String getPreferencesValue(
		PortletPreferences portletPreferences, String key, String languageId,
		boolean useDefault) {

		String localizedKey = getLocalizedName(key, languageId);

		String value = portletPreferences.getValue(
			localizedKey, StringPool.BLANK);

		if (useDefault && Validator.isNull(value)) {
			value = portletPreferences.getValue(
				_getDefaultLocalizedName(key), StringPool.BLANK);

			if (Validator.isNull(value)) {
				value = portletPreferences.getValue(key, StringPool.BLANK);
			}
		}

		return value;
	}

	@Override
	public String[] getPreferencesValues(
		PortletPreferences portletPreferences, String key, String languageId) {

		return getPreferencesValues(portletPreferences, key, languageId, true);
	}

	@Override
	public String[] getPreferencesValues(
		PortletPreferences portletPreferences, String key, String languageId,
		boolean useDefault) {

		String localizedKey = getLocalizedName(key, languageId);

		String[] values = portletPreferences.getValues(
			localizedKey, new String[0]);

		if (useDefault && ArrayUtil.isEmpty(values)) {
			values = portletPreferences.getValues(
				_getDefaultLocalizedName(key), new String[0]);

			if (ArrayUtil.isEmpty(values)) {
				values = portletPreferences.getValues(key, new String[0]);
			}
		}

		return values;
	}

	@Override
	public String getSettingsValue(
		Settings settings, String key, String languageId) {

		return getSettingsValue(settings, key, languageId, true);
	}

	@Override
	public String getSettingsValue(
		Settings settings, String key, String languageId, boolean useDefault) {

		String localizedKey = getLocalizedName(key, languageId);

		String value = settings.getValue(localizedKey, StringPool.BLANK);

		if (useDefault && Validator.isNull(value)) {
			value = settings.getValue(
				_getDefaultLocalizedName(key), StringPool.BLANK);

			if (Validator.isNull(value)) {
				value = settings.getValue(key, StringPool.BLANK);
			}
		}

		return value;
	}

	@Override
	public String[] getSettingsValues(
		Settings settings, String key, String languageId) {

		return getSettingsValues(settings, key, languageId, true);
	}

	@Override
	public String[] getSettingsValues(
		Settings settings, String key, String languageId, boolean useDefault) {

		String localizedKey = getLocalizedName(key, languageId);

		String[] values = settings.getValues(localizedKey, new String[0]);

		if (useDefault && ArrayUtil.isEmpty(values)) {
			values = settings.getValues(
				_getDefaultLocalizedName(key), new String[0]);

			if (ArrayUtil.isEmpty(values)) {
				values = settings.getValues(key, new String[0]);
			}
		}

		return values;
	}

	@Override
	public String getXml(LocalizedValuesMap localizedValuesMap, String key) {
		XMLStreamWriter xmlStreamWriter = null;

		try {
			UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

			XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();

			xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(
				unsyncStringWriter);

			xmlStreamWriter.writeStartDocument();

			xmlStreamWriter.writeStartElement("root");

			Set<Locale> availableLocales = LanguageUtil.getAvailableLocales();

			xmlStreamWriter.writeAttribute(
				"available-locales", StringUtil.merge(availableLocales));

			Locale defaultLocale = LocaleUtil.getSiteDefault();

			xmlStreamWriter.writeAttribute(
				"default-locale", defaultLocale.toString());

			for (Locale locale : availableLocales) {
				String value = localizedValuesMap.get(locale);

				if (value != null) {
					xmlStreamWriter.writeStartElement(key);

					xmlStreamWriter.writeAttribute(
						"language-id", locale.toString());

					xmlStreamWriter.writeCharacters(value);

					xmlStreamWriter.writeEndElement();
				}
			}

			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeEndDocument();

			return unsyncStringWriter.toString();
		}
		catch (XMLStreamException xmlStreamException) {
			throw new RuntimeException(xmlStreamException);
		}
		finally {
			_close(xmlStreamWriter);
		}
	}

	@Override
	public String getXml(
		Map<String, String> map, String defaultLanguageId, String key) {

		return getXml(map, defaultLanguageId, key, false);
	}

	@Override
	public String getXml(
		Map<String, String> map, String defaultLanguageId, String key,
		boolean cdata) {

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		XMLStreamWriter xmlStreamWriter = null;

		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				PortalClassLoaderUtil.getClassLoader())) {

			XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();

			xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(
				unsyncStringWriter);

			xmlStreamWriter.writeStartDocument();

			xmlStreamWriter.writeStartElement(_ROOT);

			StringBundler sb = new StringBundler((2 * map.size()) - 1);

			sb.append(defaultLanguageId);

			for (String languageId : map.keySet()) {
				if (!defaultLanguageId.equals(languageId)) {
					sb.append(StringPool.COMMA);
					sb.append(languageId);
				}
			}

			xmlStreamWriter.writeAttribute(_AVAILABLE_LOCALES, sb.toString());
			xmlStreamWriter.writeAttribute(_DEFAULT_LOCALE, defaultLanguageId);

			for (Map.Entry<String, String> entry : map.entrySet()) {
				String languageId = entry.getKey();

				xmlStreamWriter.writeStartElement(key);

				xmlStreamWriter.writeAttribute(_LANGUAGE_ID, languageId);

				if (cdata) {
					xmlStreamWriter.writeCData(
						XMLUtil.stripInvalidChars(entry.getValue()));
				}
				else {
					xmlStreamWriter.writeCharacters(
						XMLUtil.stripInvalidChars(entry.getValue()));
				}

				xmlStreamWriter.writeEndElement();
			}

			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeEndDocument();

			return unsyncStringWriter.toString();
		}
		catch (Exception exception) {
			_log.error(exception);
		}
		finally {
			if (xmlStreamWriter != null) {
				try {
					xmlStreamWriter.close();
				}
				catch (XMLStreamException xmlStreamException) {
					_log.error(xmlStreamException);
				}
			}
		}

		return StringPool.BLANK;
	}

	@Override
	public Map<Locale, String> populateLocalizationMap(
		Map<Locale, String> localizationMap, String defaultLanguageId,
		long groupId) {

		String defaultValue = localizationMap.get(
			LocaleUtil.fromLanguageId(defaultLanguageId));

		for (Locale availableLocale :
				LanguageUtil.getAvailableLocales(groupId)) {

			if (!localizationMap.containsKey(availableLocale) ||
				Validator.isNull(localizationMap.get(availableLocale))) {

				localizationMap.put(availableLocale, defaultValue);
			}
		}

		return localizationMap;
	}

	@Override
	public String removeLocalization(
		String xml, String key, String requestedLanguageId) {

		return removeLocalization(xml, key, requestedLanguageId, false);
	}

	@Override
	public String removeLocalization(
		String xml, String key, String requestedLanguageId, boolean cdata) {

		return removeLocalization(xml, key, requestedLanguageId, cdata, true);
	}

	@Override
	public String removeLocalization(
		String xml, String key, String requestedLanguageId, boolean cdata,
		boolean localized) {

		if (Validator.isNull(xml)) {
			return StringPool.BLANK;
		}

		if (!Validator.isXml(xml)) {
			return xml;
		}

		xml = _sanitizeXML(xml);

		XMLStreamReader xmlStreamReader = null;
		XMLStreamWriter xmlStreamWriter = null;

		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				PortalClassLoaderUtil.getClassLoader())) {

			XMLInputFactory xmlInputFactory =
				SecureXMLFactoryProviderUtil.newXMLInputFactory();

			xmlStreamReader = xmlInputFactory.createXMLStreamReader(
				new UnsyncStringReader(xml));

			String availableLocales = StringPool.BLANK;
			String defaultLanguageId = StringPool.BLANK;

			// Read root node

			if (xmlStreamReader.hasNext()) {
				xmlStreamReader.nextTag();

				availableLocales = xmlStreamReader.getAttributeValue(
					null, _AVAILABLE_LOCALES);

				defaultLanguageId = xmlStreamReader.getAttributeValue(
					null, _DEFAULT_LOCALE);

				if (Validator.isNull(defaultLanguageId)) {
					defaultLanguageId = LocaleUtil.toLanguageId(
						LocaleUtil.getSiteDefault());
				}
			}

			if ((availableLocales != null) &&
				availableLocales.contains(requestedLanguageId)) {

				UnsyncStringWriter unsyncStringWriter =
					new UnsyncStringWriter();

				XMLOutputFactory xmlOutputFactory =
					XMLOutputFactory.newInstance();

				xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(
					unsyncStringWriter);

				xmlStreamWriter.writeStartDocument();
				xmlStreamWriter.writeStartElement(_ROOT);

				if (localized) {
					availableLocales = StringUtil.removeFromList(
						availableLocales, requestedLanguageId);

					xmlStreamWriter.writeAttribute(
						_AVAILABLE_LOCALES, availableLocales);

					xmlStreamWriter.writeAttribute(
						_DEFAULT_LOCALE, defaultLanguageId);
				}

				_copyNonexempt(
					xmlStreamReader, xmlStreamWriter, requestedLanguageId,
					defaultLanguageId, cdata);

				xmlStreamWriter.writeEndElement();
				xmlStreamWriter.writeEndDocument();

				xmlStreamWriter.close();

				xmlStreamWriter = null;

				xml = unsyncStringWriter.toString();
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}
		finally {
			if (xmlStreamReader != null) {
				try {
					xmlStreamReader.close();
				}
				catch (Exception exception) {
					if (_log.isDebugEnabled()) {
						_log.debug(exception);
					}
				}
			}

			if (xmlStreamWriter != null) {
				try {
					xmlStreamWriter.close();
				}
				catch (Exception exception) {
					if (_log.isDebugEnabled()) {
						_log.debug(exception);
					}
				}
			}
		}

		return xml;
	}

	@Override
	public void setLocalizedPreferencesValues(
			PortletRequest portletRequest,
			PortletPreferences portletPreferences, String parameter)
		throws Exception {

		Map<Locale, String> map = getLocalizationMap(portletRequest, parameter);

		for (Map.Entry<Locale, String> entry : map.entrySet()) {
			String languageId = LocaleUtil.toLanguageId(entry.getKey());

			setPreferencesValue(
				portletPreferences, parameter, languageId, entry.getValue());
		}
	}

	@Override
	public void setPreferencesValue(
			PortletPreferences portletPreferences, String key,
			String languageId, String value)
		throws Exception {

		portletPreferences.setValue(getLocalizedName(key, languageId), value);
	}

	@Override
	public void setPreferencesValues(
			PortletPreferences portletPreferences, String key,
			String languageId, String[] values)
		throws Exception {

		portletPreferences.setValues(getLocalizedName(key, languageId), values);
	}

	@Override
	public String updateLocalization(
		Map<Locale, String> localizationMap, String xml, String key,
		String defaultLanguageId) {

		Set<Locale> availableLocales = LanguageUtil.getAvailableLocales();

		if (Validator.isBlank(xml)) {
			Map<String, String> map = new HashMap<>();

			for (Map.Entry<Locale, String> entry : localizationMap.entrySet()) {
				Locale locale = entry.getKey();
				String value = entry.getValue();

				if (availableLocales.contains(locale) &&
					Validator.isNotNull(value)) {

					map.put(LocaleUtil.toLanguageId(locale), value);
				}
			}

			if (map.isEmpty()) {
				return StringPool.BLANK;
			}

			return getXml(map, defaultLanguageId, key);
		}

		for (Locale locale : availableLocales) {
			String value = localizationMap.get(locale);

			String languageId = LocaleUtil.toLanguageId(locale);

			if (Validator.isNotNull(value)) {
				xml = updateLocalization(
					xml, key, value, languageId, defaultLanguageId);
			}
			else {
				xml = removeLocalization(xml, key, languageId);
			}
		}

		return xml;
	}

	@Override
	public String updateLocalization(String xml, String key, String value) {
		String defaultLanguageId = LocaleUtil.toLanguageId(
			LocaleUtil.getSiteDefault());

		return updateLocalization(
			xml, key, value, defaultLanguageId, defaultLanguageId);
	}

	@Override
	public String updateLocalization(
		String xml, String key, String value, String requestedLanguageId) {

		String defaultLanguageId = LocaleUtil.toLanguageId(
			LocaleUtil.getSiteDefault());

		return updateLocalization(
			xml, key, value, requestedLanguageId, defaultLanguageId);
	}

	@Override
	public String updateLocalization(
		String xml, String key, String value, String requestedLanguageId,
		String defaultLanguageId) {

		return updateLocalization(
			xml, key, value, requestedLanguageId, defaultLanguageId, false);
	}

	@Override
	public String updateLocalization(
		String xml, String key, String value, String requestedLanguageId,
		String defaultLanguageId, boolean cdata) {

		return updateLocalization(
			xml, key, value, requestedLanguageId, defaultLanguageId, cdata,
			true);
	}

	@Override
	public String updateLocalization(
		String xml, String key, String value, String requestedLanguageId,
		String defaultLanguageId, boolean cdata, boolean localized) {

		xml = _sanitizeXML(xml);

		XMLStreamReader xmlStreamReader = null;
		XMLStreamWriter xmlStreamWriter = null;

		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				PortalClassLoaderUtil.getClassLoader())) {

			XMLInputFactory xmlInputFactory =
				SecureXMLFactoryProviderUtil.newXMLInputFactory();

			xmlStreamReader = xmlInputFactory.createXMLStreamReader(
				new UnsyncStringReader(xml));

			String availableLocales = StringPool.BLANK;

			// Read root node

			if (xmlStreamReader.hasNext()) {
				xmlStreamReader.nextTag();

				availableLocales = xmlStreamReader.getAttributeValue(
					null, _AVAILABLE_LOCALES);

				if (Validator.isNull(availableLocales)) {
					availableLocales = defaultLanguageId;
				}

				if (!availableLocales.contains(requestedLanguageId)) {
					availableLocales = StringUtil.add(
						availableLocales, requestedLanguageId,
						StringPool.COMMA);
				}
			}

			UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

			XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();

			xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(
				unsyncStringWriter);

			xmlStreamWriter.writeStartDocument();
			xmlStreamWriter.writeStartElement(_ROOT);

			if (localized) {
				xmlStreamWriter.writeAttribute(
					_AVAILABLE_LOCALES, availableLocales);
				xmlStreamWriter.writeAttribute(
					_DEFAULT_LOCALE, defaultLanguageId);
			}

			_copyNonexempt(
				xmlStreamReader, xmlStreamWriter, requestedLanguageId,
				defaultLanguageId, cdata);

			xmlStreamWriter.writeStartElement(key);

			if (localized) {
				xmlStreamWriter.writeAttribute(
					_LANGUAGE_ID, requestedLanguageId);
			}

			String safeValue = XMLUtil.stripInvalidChars(value);

			if (cdata) {
				xmlStreamWriter.writeCData(safeValue);
			}
			else {
				xmlStreamWriter.writeCharacters(safeValue);
			}

			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndDocument();

			xmlStreamWriter.close();

			xmlStreamWriter = null;

			xml = unsyncStringWriter.toString();
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}
		finally {
			if (xmlStreamReader != null) {
				try {
					xmlStreamReader.close();
				}
				catch (Exception exception) {
					if (_log.isDebugEnabled()) {
						_log.debug(exception);
					}
				}
			}

			if (xmlStreamWriter != null) {
				try {
					xmlStreamWriter.close();
				}
				catch (Exception exception) {
					if (_log.isDebugEnabled()) {
						_log.debug(exception);
					}
				}
			}
		}

		return xml;
	}

	private void _close(XMLStreamWriter xmlStreamWriter) {
		if (xmlStreamWriter != null) {
			try {
				xmlStreamWriter.close();
			}
			catch (XMLStreamException xmlStreamException) {
				if (_log.isDebugEnabled()) {
					_log.debug(xmlStreamException);
				}
			}
		}
	}

	private void _copyNonexempt(
			XMLStreamReader xmlStreamReader, XMLStreamWriter xmlStreamWriter,
			String exemptLanguageId, String defaultLanguageId, boolean cdata)
		throws XMLStreamException {

		while (xmlStreamReader.hasNext()) {
			int event = xmlStreamReader.next();

			if (event == XMLStreamConstants.START_ELEMENT) {
				String languageId = xmlStreamReader.getAttributeValue(
					null, _LANGUAGE_ID);

				if (Validator.isNull(languageId)) {
					languageId = defaultLanguageId;
				}

				if (!languageId.equals(exemptLanguageId)) {
					xmlStreamWriter.writeStartElement(
						xmlStreamReader.getLocalName());
					xmlStreamWriter.writeAttribute(_LANGUAGE_ID, languageId);

					String text = xmlStreamReader.getElementText();

					if (cdata) {
						xmlStreamWriter.writeCData(text);
					}
					else {
						xmlStreamWriter.writeCharacters(text);
					}

					xmlStreamWriter.writeEndElement();
				}
			}
			else if (event == XMLStreamConstants.END_DOCUMENT) {
				break;
			}
		}
	}

	private String _getCachedValue(
		String xml, String requestedLanguageId, boolean useDefault) {

		String value = null;

		Map<Tuple, String> valueMap = _cache.get(xml);

		if (valueMap != null) {
			Tuple subkey = new Tuple(useDefault, requestedLanguageId);

			value = valueMap.get(subkey);
		}

		return value;
	}

	private String _getDefaultLocalizedName(String name) {
		String defaultLanguageId = LocaleUtil.toLanguageId(
			LocaleUtil.getSiteDefault());

		return getLocalizedName(name, defaultLanguageId);
	}

	private String _getLocalization(
		String bundleName, Locale locale, ClassLoader classLoader, String key,
		String defaultValue) {

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			bundleName, locale, classLoader);

		String value = null;

		if (resourceBundle.containsKey(key)) {
			try {
				value = ResourceBundleUtil.getString(resourceBundle, key);

				value = new String(
					value.getBytes(StringPool.ISO_8859_1), StringPool.UTF8);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}

		if (Validator.isNull(value)) {
			value = LanguageUtil.get(locale, key, defaultValue);
		}

		return value;
	}

	private String _getRootAttributeValue(
		Document document, String name, String defaultValue) {

		if (document == null) {
			return defaultValue;
		}

		Element rootElement = document.getRootElement();

		return rootElement.attributeValue(name, defaultValue);
	}

	private String _getRootAttributeValue(
		String xml, String name, String defaultValue) {

		if (!Validator.isXml(xml)) {
			return defaultValue;
		}

		String value = null;

		XMLStreamReader xmlStreamReader = null;

		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				PortalClassLoaderUtil.getClassLoader())) {

			XMLInputFactory xmlInputFactory =
				SecureXMLFactoryProviderUtil.newXMLInputFactory();

			xmlStreamReader = xmlInputFactory.createXMLStreamReader(
				new UnsyncStringReader(xml));

			if (xmlStreamReader.hasNext()) {
				xmlStreamReader.nextTag();

				value = xmlStreamReader.getAttributeValue(null, name);
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}
		finally {
			if (xmlStreamReader != null) {
				try {
					xmlStreamReader.close();
				}
				catch (Exception exception) {
					if (_log.isDebugEnabled()) {
						_log.debug(exception);
					}
				}
			}
		}

		if (Validator.isNull(value)) {
			value = defaultValue;
		}

		return value;
	}

	private String _sanitizeXML(String xml) {
		if (Validator.isNull(xml) || !xml.contains("<root")) {
			xml = _EMPTY_ROOT_NODE;
		}

		return xml;
	}

	private void _setCachedValue(
		String xml, String requestedLanguageId, boolean useDefault,
		String value) {

		if (Validator.isNotNull(xml) && !xml.equals(_EMPTY_ROOT_NODE)) {
			_cache.compute(
				xml,
				(key, map) -> {
					if (map == null) {
						map = new HashMap<>();
					}

					map.put(new Tuple(useDefault, requestedLanguageId), value);

					return map;
				});
		}
	}

	private static final String _AVAILABLE_LOCALES = "available-locales";

	private static final String _DEFAULT_LOCALE = "default-locale";

	private static final String _EMPTY_ROOT_NODE = "<root />";

	private static final String _LANGUAGE_ID = "language-id";

	private static final String _ROOT = "root";

	private static final Log _log = LogFactoryUtil.getLog(
		LocalizationImpl.class);

	private final Map<String, Map<Tuple, String>> _cache =
		new ConcurrentReferenceKeyHashMap<>(
			FinalizeManager.SOFT_REFERENCE_FACTORY);

}