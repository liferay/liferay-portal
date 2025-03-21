/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.type.internal.factory;

import com.liferay.client.extension.exception.ClientExtensionEntryTypeSettingsException;
import com.liferay.client.extension.type.ThemeCSSCET;
import com.liferay.client.extension.type.internal.ThemeCSSCETImpl;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletRequest;

import java.util.Date;
import java.util.Properties;

/**
 * @author Iván Zaera Avellón
 */
public class ThemeCSSCETImplFactoryImpl
	extends BaseCETImplFactoryImpl<ThemeCSSCET> {

	public ThemeCSSCETImplFactoryImpl(JSONFactory jsonFactory) {
		super(ThemeCSSCET.class);

		_jsonFactory = jsonFactory;
	}

	@Override
	public ThemeCSSCET create(
		String baseURL, long companyId, Date createDate, String description,
		String externalReferenceCode, Date modifiedDate, String name,
		Properties properties, boolean readOnly, String sourceCodeURL,
		int status, UnicodeProperties typeSettingsUnicodeProperties) {

		return new ThemeCSSCETImpl(
			baseURL, companyId, createDate, description, externalReferenceCode,
			modifiedDate, name, properties, readOnly, sourceCodeURL, status,
			typeSettingsUnicodeProperties);
	}

	@Override
	public UnicodeProperties getUnicodeProperties(
		PortletRequest portletRequest) {

		return UnicodePropertiesBuilder.create(
			true
		).put(
			"clayRTLURL", ParamUtil.getString(portletRequest, "clayRTLURL")
		).put(
			"clayURL", ParamUtil.getString(portletRequest, "clayURL")
		).put(
			"frontendTokenDefinitionJSON",
			ParamUtil.getString(portletRequest, "frontendTokenDefinitionJSON")
		).put(
			"mainRTLURL", ParamUtil.getString(portletRequest, "mainRTLURL")
		).put(
			"mainURL", ParamUtil.getString(portletRequest, "mainURL")
		).build();
	}

	@Override
	public void validate(ThemeCSSCET newThemeCSSCET, ThemeCSSCET oldThemeCSSCET)
		throws PortalException {

		String baseURL = newThemeCSSCET.getBaseURL();

		if (!Validator.isBlank(baseURL) && !Validator.isUrl(baseURL, true)) {
			throw new ClientExtensionEntryTypeSettingsException(
				"Invalid base URL: " + baseURL, "base-url-x-is-invalid",
				baseURL);
		}

		String clayURL = newThemeCSSCET.getClayURL();

		if (!Validator.isBlank(clayURL) && !Validator.isUrl(clayURL, true)) {
			throw new ClientExtensionEntryTypeSettingsException(
				"Invalid Clay CSS URL: " + clayURL, "clay-css-url-x-is-invalid",
				clayURL);
		}

		String mainURL = newThemeCSSCET.getMainURL();

		if (!Validator.isBlank(mainURL) && !Validator.isUrl(mainURL, true)) {
			throw new ClientExtensionEntryTypeSettingsException(
				"Invalid Main CSS URL: " + mainURL, "main-css-url-x-is-invalid",
				mainURL);
		}

		String frontendTokenDefinitionJSON =
			newThemeCSSCET.getFrontendTokenDefinitionJSON();

		if (Validator.isBlank(frontendTokenDefinitionJSON)) {
			return;
		}

		try {
			_jsonFactory.createJSONObject(frontendTokenDefinitionJSON);
		}
		catch (JSONException jsonException) {
			_log.error(jsonException);

			throw new ClientExtensionEntryTypeSettingsException(
				"Invalid Frontend Token Definition JSON: " +
					frontendTokenDefinitionJSON,
				"the-format-is-not-valid-please-upload-a-valid-frontend-" +
					"token-definition-json-file");
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ThemeCSSCETImplFactoryImpl.class);

	private final JSONFactory _jsonFactory;

}