/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;
import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.xml.Document;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Stores and retrieves localized strings from XML, and provides utility methods
 * for updating localizations from JSON, portlet requests, and maps. Used for
 * adding localization to strings, most often for model properties.
 *
 * <p>
 * Localized values are cached in this class rather than in the value object
 * since value objects get flushed from cache fairly quickly. Though lookups
 * performed on a key based on an XML file are slower than lookups done at the
 * value object level in general, the value object will get flushed at a rate
 * which works against the performance gain. The cache is a soft hash map which
 * prevents memory leaks within the system while enabling the cache to live
 * longer than in a weak hash map.
 * </p>
 *
 * @author Alexander Chow
 * @author Jorge Ferrer
 * @author Mauro Mariuzzo
 * @author Julio Camarero
 * @author Brian Wing Shun Chan
 */
@ProviderType
public interface Localization {

	/**
	 * Deserializes the JSON object into a map of locales and localized strings.
	 *
	 * @param  jsonObject the JSON object
	 * @return the locales and localized strings
	 */
	public Object deserialize(JSONObject jsonObject);

	public String[] getAvailableLanguageIds(Document document);

	/**
	 * Returns the available locales from the localizations XML.
	 *
	 * @param  xml the localizations XML
	 * @return the language IDs of the available locales
	 */
	public String[] getAvailableLanguageIds(String xml);

	/**
	 * Returns a valid default locale for importing a localized entity.
	 *
	 * @param  className the class name of the entity
	 * @param  classPK the primary keys of the entity
	 * @param  contentDefaultLocale the default Locale of the entity
	 * @param  contentAvailableLocales the available locales of the entity
	 * @return the valid locale
	 */
	public Locale getDefaultImportLocale(
		String className, long classPK, Locale contentDefaultLocale,
		Locale[] contentAvailableLocales);

	/**
	 * Returns a valid default locale for importing a localized entity.
	 *
	 * @param  className the class name of the entity
	 * @param  primaryKey the primary keys of the entity
	 * @param  contentDefaultLocale the default Locale of the entity
	 * @param  contentAvailableLocales the available locales of the entity
	 * @return the valid locale
	 */
	public Locale getDefaultImportLocale(
		String className, Serializable primaryKey, Locale contentDefaultLocale,
		Locale[] contentAvailableLocales);

	public String getDefaultLanguageId(Document document);

	public String getDefaultLanguageId(Document document, Locale defaultLocale);

	/**
	 * Returns the default locale from the localizations XML.
	 *
	 * @param  xml the localizations XML
	 * @return the language ID of the default locale, or the system default
	 *         locale if the default locale cannot be retrieved from the XML
	 */
	public String getDefaultLanguageId(String xml);

	public String getDefaultLanguageId(String xml, Locale defaultLocale);

	public String getLocalization(
		Function<String, String> localizationFunction,
		String requestedLanguageId, String defaultLanguageId);

	/**
	 * Returns the localized string from the localizations XML in the language.
	 * The default language is used if no localization exists for the requested
	 * language.
	 *
	 * @param  xml the localizations XML
	 * @param  requestedLanguageId the ID of the language
	 * @return the localized string
	 */
	public String getLocalization(String xml, String requestedLanguageId);

	/**
	 * Returns the localized string from the localizations XML in the language,
	 * optionally using the default language if no localization exists for the
	 * requested language.
	 *
	 * @param  xml the localizations XML
	 * @param  requestedLanguageId the ID of the language
	 * @param  useDefault whether to use the default language if no localization
	 *         exists for the requested language
	 * @return the localized string, or an empty string if
	 *         <code>useDefault</code> is <code>false</code> and no localization
	 *         exists for the requested language
	 */
	public String getLocalization(
		String xml, String requestedLanguageId, boolean useDefault);

	/**
	 * Returns the localized string from the localizations XML in the language,
	 * optionally using the default language if no localization exists for the
	 * requested language. If no localization exists, the default value is
	 * returned.
	 *
	 * @param  xml the localizations XML
	 * @param  requestedLanguageId the ID of the language
	 * @param  useDefault whether to use the default language if no localization
	 *         exists for the requested language
	 * @param  defaultValue the value returned if no localization exists
	 * @return the localized string, or the <code>defaultValue</code> if
	 *         <code>useDefault</code> is <code>false</code> and no localization
	 *         exists for the requested language
	 */
	public String getLocalization(
		String xml, String requestedLanguageId, boolean useDefault,
		String defaultValue);

	/**
	 * Returns a map of locales and localized strings for the key. If no
	 * localization exists for a locale or the localization matches the default
	 * locale, that locale is not included in the map.
	 *
	 * @param  locales the locales to be used in the map
	 * @param  defaultLocale the default locale
	 * @param  key the language key to be translated
	 * @return the locales and localized strings for the key
	 */
	public Map<Locale, String> getLocalizationMap(
		Collection<Locale> locales, Locale defaultLocale, String key);

	/**
	 * Returns a map of locales and localized strings for the parameter in the
	 * request.
	 *
	 * @param  httpServletRequest the request
	 * @param  parameter the prefix of the parameters containing the localized
	 *         strings. Each localization is loaded from a parameter with this
	 *         prefix, followed by an underscore, and the language ID.
	 * @return the locales and localized strings
	 */
	public Map<Locale, String> getLocalizationMap(
		HttpServletRequest httpServletRequest, String parameter);

	/**
	 * Returns a map of locales and localized strings for the preference in the
	 * preferences container.
	 *
	 * @param  portletPreferences the preferences container
	 * @param  preferenceName the prefix of the preference containing the
	 *         localized strings. Each localization is loaded from a preference
	 *         with this prefix, followed by an underscore, and the language ID.
	 * @return the locales and localized strings
	 */
	public Map<Locale, String> getLocalizationMap(
		PortletPreferences portletPreferences, String preferenceName);

	/**
	 * Returns a map of locales and localized strings for the preference in the
	 * preferences container. If no localization exists for the preference in
	 * the default locale, the value of the property is used as the localization
	 * for it.
	 *
	 * @param  portletPreferences the preferences container
	 * @param  preferenceName the prefix of the preference containing the
	 *         localized strings. Each localization is loaded from a preference
	 *         with this prefix, followed by an underscore, and the language ID.
	 * @param  propertyName the name of the property whose value is used as the
	 *         localization for the default locale, if no localization exists
	 *         for it
	 * @return the locales and localized strings
	 */
	public Map<Locale, String> getLocalizationMap(
		PortletPreferences portletPreferences, String preferenceName,
		String propertyName);

	public Map<Locale, String> getLocalizationMap(
		PortletPreferences portletPreferences, String preferenceName,
		String propertyName, String defaultPropertyValue,
		ClassLoader classLoader);

	/**
	 * Returns a map of locales and localized strings for the parameter in the
	 * portlet request.
	 *
	 * @param  portletRequest the portlet request
	 * @param  parameter the prefix of the parameters containing the localized
	 *         strings. Each localization is loaded from a parameter with this
	 *         prefix, followed by an underscore, and the language ID.
	 * @return the locales and localized strings
	 */
	public Map<Locale, String> getLocalizationMap(
		PortletRequest portletRequest, String parameter);

	public Map<Locale, String> getLocalizationMap(
		PortletRequest portletRequest, String parameter,
		Map<Locale, String> defaultValues);

	/**
	 * Returns a map of locales and localized strings from the localizations
	 * XML.
	 *
	 * @param  xml the localizations XML
	 * @return the locales and localized strings
	 */
	public Map<Locale, String> getLocalizationMap(String xml);

	public Map<Locale, String> getLocalizationMap(
		String xml, boolean useDefault);

	public Map<Locale, String> getLocalizationMap(
		String bundleName, ClassLoader classLoader, String key,
		boolean includeBetaLocales);

	/**
	 * Returns a map of locales and localized strings for the given languageIds
	 * and values.
	 *
	 * @param  languageIds the languageIds of the localized Strings
	 * @param  values the localized strings for the different languageId
	 * @return the map of locales and values for the given parameters
	 */
	public Map<Locale, String> getLocalizationMap(
		String[] languageIds, String[] values);

	/**
	 * Returns the localizations XML for the parameter from the portlet request,
	 * attempting to get data from the preferences container if it is not
	 * available in the portlet request.
	 *
	 * @param  portletPreferences the preferences container
	 * @param  portletRequest the portlet request
	 * @param  parameter the prefix of the parameters containing the localized
	 *         strings. Each localization is loaded from a parameter with this
	 *         prefix, followed by an underscore, and the language ID.
	 * @return the locales and localized strings
	 */
	public String getLocalizationXmlFromPreferences(
		PortletPreferences portletPreferences, PortletRequest portletRequest,
		String parameter);

	/**
	 * Returns the localizations XML for the parameter from the portlet request,
	 * attempting to get data from the preferences container if it is not
	 * available in the portlet request. If no localization exists in the
	 * default locale, the default value is used as the localization for it.
	 *
	 * @param  portletPreferences the preferences container
	 * @param  portletRequest the portlet request
	 * @param  parameter the prefix of the parameters containing the localized
	 *         strings. Each localization is loaded from a parameter with this
	 *         prefix, followed by an underscore, and the language ID.
	 * @param  defaultValue the value used as the localization for the default
	 *         locale, if no localization exists for it
	 * @return the locales and localized strings
	 */
	public String getLocalizationXmlFromPreferences(
		PortletPreferences portletPreferences, PortletRequest portletRequest,
		String parameter, String defaultValue);

	/**
	 * Returns the localizations XML for the prefixed parameter from the portlet
	 * request, attempting to get data from the preferences container if it is
	 * not available in the portlet request. If no localization exists for the
	 * the default locale, the default value is used as the localization for it.
	 *
	 * @param  portletPreferences the preferences container
	 * @param  portletRequest the portlet request
	 * @param  parameter the prefix of the parameters containing the localized
	 *         strings. Each localization is loaded from a parameter with this
	 *         prefix, followed by an underscore, and the language ID.
	 * @param  prefix the value used in the request to prefix the parameter name
	 * @param  defaultValue the value used as the localization for the default
	 *         locale, if no localization exists for it
	 * @return the locales and localized strings, or the
	 *         <code>defaultValue</code> if no localization exists
	 */
	public String getLocalizationXmlFromPreferences(
		PortletPreferences portletPreferences, PortletRequest portletRequest,
		String parameter, String prefix, String defaultValue);

	/**
	 * Returns the localized name in the language. The localized name is the
	 * name, followed by an underscore, and the language ID.
	 *
	 * @param  name the name to be localized
	 * @param  languageId the ID of the language
	 * @return the localized name for the language
	 */
	public String getLocalizedName(String name, String languageId);

	public Map<Locale, String> getMap(LocalizedValuesMap localizedValuesMap);

	/**
	 * Returns the localized preferences value for the key in the language. The
	 * default language is used if no localization exists for the requested
	 * language.
	 *
	 * @param  portletPreferences the preferences container
	 * @param  key the preferences key
	 * @param  languageId the ID of the language
	 * @return the localized preferences value
	 */
	public String getPreferencesValue(
		PortletPreferences portletPreferences, String key, String languageId);

	/**
	 * Returns the localized preferences value for the key in the language,
	 * optionally using the default language if no localization exists for the
	 * requested language.
	 *
	 * @param  portletPreferences the preferences container
	 * @param  key the preferences key
	 * @param  languageId the ID of the language
	 * @param  useDefault whether to use the default language if no localization
	 *         exists for the requested language
	 * @return the localized preferences value, or an empty string if
	 *         <code>useDefault</code> is <code>false</code> and no localization
	 *         exists for the requested language
	 */
	public String getPreferencesValue(
		PortletPreferences portletPreferences, String key, String languageId,
		boolean useDefault);

	/**
	 * Returns the localized preferences values for the key in the language. The
	 * default language is used if no localization exists for the requested
	 * language.
	 *
	 * @param  portletPreferences the preferences container
	 * @param  key the preferences key
	 * @param  languageId the ID of the language
	 * @return the localized preferences values
	 */
	public String[] getPreferencesValues(
		PortletPreferences portletPreferences, String key, String languageId);

	/**
	 * Returns the localized preferences values for the key in the language,
	 * optionally using the default language if no localization exists for the
	 * requested language.
	 *
	 * @param  portletPreferences the preferences container
	 * @param  key the preferences key
	 * @param  languageId the ID of the language
	 * @param  useDefault whether to use the default language if no localization
	 *         exists for the requested language
	 * @return the localized preferences values, or an empty string if
	 *         <code>useDefault</code> is <code>false</code> and no localization
	 *         exists for the requested language
	 */
	public String[] getPreferencesValues(
		PortletPreferences portletPreferences, String key, String languageId,
		boolean useDefault);

	/**
	 * Returns the localized settings value for the key in the language. The
	 * default language is used if no localization exists for the requested
	 * language.
	 *
	 * @param  settings the settings
	 * @param  key the settings key
	 * @param  languageId the ID of the language
	 * @return the localized settings value
	 */
	public String getSettingsValue(
		Settings settings, String key, String languageId);

	/**
	 * Returns the localized settings value for the key in the language,
	 * optionally using the default language if no localization exists for the
	 * requested language.
	 *
	 * @param  settings the settings
	 * @param  key the settings key
	 * @param  languageId the ID of the language
	 * @param  useDefault whether to use the default language if no localization
	 *         exists for the requested language
	 * @return the localized settings value. If <code>useDefault</code> is
	 *         <code>false</code> and no localization exists for the requested
	 *         language, an empty string is returned.
	 */
	public String getSettingsValue(
		Settings settings, String key, String languageId, boolean useDefault);

	/**
	 * Returns the localized settings values for the key in the language. The
	 * default language is used if no localization exists for the requested
	 * language.
	 *
	 * @param  settings the settings
	 * @param  key the settings key
	 * @param  languageId the ID of the language
	 * @return the localized settings values
	 */
	public String[] getSettingsValues(
		Settings settings, String key, String languageId);

	/**
	 * Returns the localized settings values for the key in the language,
	 * optionally using the default language if no localization exists for the
	 * requested language.
	 *
	 * @param  settings the settings
	 * @param  key the settings key
	 * @param  languageId the ID of the language
	 * @param  useDefault whether to use the default language if no localization
	 *         exists for the requested language
	 * @return the localized settings values. If <code>useDefault</code> is
	 *         <code>false</code> and no localization exists for the requested
	 *         language, an empty array is returned.
	 */
	public String[] getSettingsValues(
		Settings settings, String key, String languageId, boolean useDefault);

	public String getXml(LocalizedValuesMap localizedValuesMap, String key);

	public String getXml(
		Map<String, String> map, String defaultLanguageId, String key);

	public String getXml(
		Map<String, String> map, String defaultLanguageId, String key,
		boolean cdata);

	public Map<Locale, String> populateLocalizationMap(
		Map<Locale, String> localizationMap, String defaultLanguageId,
		long groupId);

	/**
	 * Removes the localization for the language from the localizations XML. The
	 * localized strings are stored as characters in the XML.
	 *
	 * @param  xml the localizations XML
	 * @param  key the name of the localized string, such as &quot;Title&quot;
	 * @param  requestedLanguageId the ID of the language
	 * @return the localizations XML with the language removed
	 */
	public String removeLocalization(
		String xml, String key, String requestedLanguageId);

	/**
	 * Removes the localization for the language from the localizations XML,
	 * optionally storing the localized strings as CDATA in the XML.
	 *
	 * @param  xml the localizations XML
	 * @param  key the name of the localized string, such as &quot;Title&quot;
	 * @param  requestedLanguageId the ID of the language
	 * @param  cdata whether to store localized strings as CDATA in the XML
	 * @return the localizations XML with the language removed
	 */
	public String removeLocalization(
		String xml, String key, String requestedLanguageId, boolean cdata);

	/**
	 * Removes the localization for the language from the localizations XML,
	 * optionally storing the localized strings as CDATA in the XML.
	 *
	 * @param  xml the localizations XML
	 * @param  key the name of the localized string, such as &quot;Title&quot;
	 * @param  requestedLanguageId the ID of the language
	 * @param  cdata whether to store localized strings as CDATA in the XML
	 * @param  localized whether there is a localized field
	 * @return the localizations XML with the language removed
	 */
	public String removeLocalization(
		String xml, String key, String requestedLanguageId, boolean cdata,
		boolean localized);

	/**
	 * Sets the localized preferences values for the parameter in the portlet
	 * request.
	 *
	 * @param  portletRequest the portlet request
	 * @param  portletPreferences the preferences container
	 * @param  parameter the prefix of the parameters containing the localized
	 *         strings. Each localization is loaded from a parameter with this
	 *         prefix, followed by an underscore, and the language ID.
	 * @throws Exception if an exception occurred
	 */
	public void setLocalizedPreferencesValues(
			PortletRequest portletRequest,
			PortletPreferences portletPreferences, String parameter)
		throws Exception;

	/**
	 * Sets the localized preferences value for the key in the language.
	 *
	 * @param  portletPreferences the preferences container
	 * @param  key the preferences key
	 * @param  languageId the ID of the language
	 * @param  value the localized value
	 * @throws Exception if an exception occurred
	 */
	public void setPreferencesValue(
			PortletPreferences portletPreferences, String key,
			String languageId, String value)
		throws Exception;

	/**
	 * Sets the localized preferences values for the key in the language.
	 *
	 * @param  portletPreferences the preferences container
	 * @param  key the preferences key
	 * @param  languageId the ID of the language
	 * @param  values the localized values
	 * @throws Exception if an exception occurred
	 */
	public void setPreferencesValues(
			PortletPreferences portletPreferences, String key,
			String languageId, String[] values)
		throws Exception;

	/**
	 * Updates the localized string for all the available languages in the
	 * localizations XML for the map of locales and localized strings and
	 * changes the default language. The localized strings are stored as
	 * characters in the XML.
	 *
	 * @param  localizationMap the locales and localized strings
	 * @param  xml the localizations XML
	 * @param  key the name of the localized string, such as &quot;Title&quot;
	 * @param  defaultLanguageId the ID of the default language
	 * @return the updated localizations XML
	 */
	public String updateLocalization(
		Map<Locale, String> localizationMap, String xml, String key,
		String defaultLanguageId);

	/**
	 * Updates the localized string for the system default language in the
	 * localizations XML. The localized strings are stored as characters in the
	 * XML.
	 *
	 * @param  xml the localizations XML
	 * @param  key the name of the localized string, such as &quot;Title&quot;
	 * @param  value the localized string
	 * @return the updated localizations XML
	 */
	public String updateLocalization(String xml, String key, String value);

	/**
	 * Updates the localized string for the language in the localizations XML.
	 * The localized strings are stored as characters in the XML.
	 *
	 * @param  xml the localizations XML
	 * @param  key the name of the localized string, such as &quot;Title&quot;
	 * @param  value the localized string
	 * @param  requestedLanguageId the ID of the language
	 * @return the updated localizations XML
	 */
	public String updateLocalization(
		String xml, String key, String value, String requestedLanguageId);

	/**
	 * Updates the localized string for the language in the localizations XML
	 * and changes the default language. The localized strings are stored as
	 * characters in the XML.
	 *
	 * @param  xml the localizations XML
	 * @param  key the name of the localized string, such as &quot;Title&quot;
	 * @param  value the localized string
	 * @param  requestedLanguageId the ID of the language
	 * @param  defaultLanguageId the ID of the default language
	 * @return the updated localizations XML
	 */
	public String updateLocalization(
		String xml, String key, String value, String requestedLanguageId,
		String defaultLanguageId);

	/**
	 * Updates the localized string for the language in the localizations XML
	 * and changes the default language, optionally storing the localized
	 * strings as CDATA in the XML.
	 *
	 * @param  xml the localizations XML
	 * @param  key the name of the localized string, such as &quot;Title&quot;
	 * @param  value the localized string
	 * @param  requestedLanguageId the ID of the language
	 * @param  defaultLanguageId the ID of the default language
	 * @param  cdata whether to store localized strings as CDATA in the XML
	 * @return the updated localizations XML
	 */
	public String updateLocalization(
		String xml, String key, String value, String requestedLanguageId,
		String defaultLanguageId, boolean cdata);

	/**
	 * Updates the localized string for the language in the localizations XML
	 * and changes the default language, optionally storing the localized
	 * strings as CDATA in the XML.
	 *
	 * @param  xml the localizations XML
	 * @param  key the name of the localized string, such as &quot;Title&quot;
	 * @param  value the localized string
	 * @param  requestedLanguageId the ID of the language
	 * @param  defaultLanguageId the ID of the default language
	 * @param  cdata whether to store localized strings as CDATA in the XML
	 * @param  localized whether there is a localized field
	 * @return the updated localizations XML
	 */
	public String updateLocalization(
		String xml, String key, String value, String requestedLanguageId,
		String defaultLanguageId, boolean cdata, boolean localized);

}