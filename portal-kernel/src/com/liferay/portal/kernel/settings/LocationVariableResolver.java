/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.settings;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.resource.ResourceRetriever;
import com.liferay.portal.kernel.resource.manager.ResourceManager;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.FileInputStream;
import java.io.IOException;

import java.util.Locale;
import java.util.Set;

/**
 * @author Iván Zaera
 */
public class LocationVariableResolver {

	public LocationVariableResolver(
		ResourceManager resourceManager,
		SettingsLocatorHelper settingsLocatorHelper) {

		_resourceManager = resourceManager;
		_settingsLocatorHelper = settingsLocatorHelper;
	}

	public boolean isLocationVariable(String value) {
		if (value == null) {
			return false;
		}

		if (value.startsWith(_LOCATION_VARIABLE_START) &&
			value.endsWith(_LOCATION_VARIABLE_END) &&
			value.contains(_LOCATION_VARIABLE_PROTOCOL_SEPARATOR) &&
			LocationVariableProtocol.isProtocol(_getProtocol(value))) {

			return true;
		}

		return false;
	}

	public boolean isLocationVariable(
		String value, LocationVariableProtocol locationVariableProtocol) {

		if (isLocationVariable(value) &&
			locationVariableProtocol.equals(_getProtocol(value))) {

			return true;
		}

		return false;
	}

	public String resolve(String value) {
		String protocol = _getProtocol(value);
		String location = _getLocation(value);

		if (LocationVariableProtocol.FILE.equals(protocol)) {
			return _resolveFile(location);
		}
		else if (LocationVariableProtocol.LANGUAGE.equals(protocol)) {
			return _resolveLanguage(location);
		}
		else if (LocationVariableProtocol.RESOURCE.equals(protocol)) {
			return _resolveResource(location);
		}
		else if (LocationVariableProtocol.SERVER_PROPERTY.equals(protocol)) {
			return _resolveServerProperty(location);
		}

		throw new UnsupportedOperationException(
			"Unsupported protocol " + protocol);
	}

	private String _getLocation(String value) {
		int i = value.indexOf(_LOCATION_VARIABLE_PROTOCOL_SEPARATOR);

		return value.substring(i + 1, value.length() - 1);
	}

	private String _getProtocol(String value) {
		int i = value.indexOf(_LOCATION_VARIABLE_PROTOCOL_SEPARATOR);

		return value.substring(2, i);
	}

	private String _resolveFile(String location) {
		if (!location.startsWith("///")) {
			throw new IllegalArgumentException(
				StringBundler.concat(
					"Invalid file location ", location, " because only local ",
					"file URIs starting with file:/// are supported"));
		}

		try {
			return StringUtil.read(new FileInputStream(location.substring(2)));
		}
		catch (IOException ioException) {
			throw new SystemException(
				"Unable to read file " + location, ioException);
		}
	}

	private String _resolveLanguage(String location) {
		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		Set<Locale> availableLocales = LanguageUtil.getCompanyAvailableLocales(
			CompanyThreadLocal.getCompanyId());

		for (Locale locale : availableLocales) {
			String message = LanguageUtil.get(locale, location);

			if (message.equals(location)) {
				continue;
			}

			jsonObject.put(LocaleUtil.toLanguageId(locale), message);
		}

		return jsonObject.toString();
	}

	private String _resolveResource(String location) {
		ResourceRetriever resourceRetriever =
			_resourceManager.getResourceRetriever(location);

		try {
			return StringUtil.read(resourceRetriever.getInputStream());
		}
		catch (IOException ioException) {
			throw new SystemException(
				"Unable to read resource " + location, ioException);
		}
	}

	private String _resolveServerProperty(String location) {
		if (!location.startsWith("//")) {
			throw new IllegalArgumentException(
				"Invalid server property location " + location);
		}

		location = location.substring(2);

		int i = location.indexOf("/");

		if (i == -1) {
			throw new IllegalArgumentException(
				"Invalid server property location " + location);
		}

		String serviceName = location.substring(0, i);

		Settings settings = _settingsLocatorHelper.getServerSettings(
			serviceName);

		String property = location.substring(i + 1);

		return settings.getValue(property, null);
	}

	private static final String _LOCATION_VARIABLE_END = "}";

	private static final String _LOCATION_VARIABLE_PROTOCOL_SEPARATOR = ":";

	private static final String _LOCATION_VARIABLE_START = "${";

	private final ResourceManager _resourceManager;
	private final SettingsLocatorHelper _settingsLocatorHelper;

}