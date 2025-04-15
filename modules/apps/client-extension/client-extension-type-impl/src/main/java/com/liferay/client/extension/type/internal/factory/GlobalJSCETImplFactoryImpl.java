/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.type.internal.factory;

import com.liferay.client.extension.exception.ClientExtensionEntryTypeSettingsException;
import com.liferay.client.extension.type.GlobalJSCET;
import com.liferay.client.extension.type.internal.GlobalJSCETImpl;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletRequest;

import java.util.Date;
import java.util.Properties;
import java.util.Set;

/**
 * @author Iván Zaera Avellón
 */
public class GlobalJSCETImplFactoryImpl
	extends BaseCETImplFactoryImpl<GlobalJSCET> {

	public GlobalJSCETImplFactoryImpl(JSONFactory jsonFactory) {
		super(GlobalJSCET.class);

		_jsonFactory = jsonFactory;
	}

	@Override
	public GlobalJSCET create(
		String baseURL, long companyId, Date createDate, String description,
		String externalReferenceCode, Date modifiedDate, String name,
		Properties properties, boolean readOnly, String sourceCodeURL,
		int status, UnicodeProperties typeSettingsUnicodeProperties) {

		return new GlobalJSCETImpl(
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
			"scriptElementAttributesJSON",
			ParamUtil.getString(portletRequest, "scriptElementAttributesJSON")
		).put(
			"url", ParamUtil.getString(portletRequest, "url")
		).build();
	}

	@Override
	public void validate(GlobalJSCET newGlobalJSCET, GlobalJSCET oldGlobalJSCET)
		throws PortalException {

		String url = newGlobalJSCET.getURL();

		if (!Validator.isUrl(url)) {
			throw new ClientExtensionEntryTypeSettingsException(
				"Invalid JavaScript URL: " + url, "javascript-url-x-is-invalid",
				url);
		}

		JSONObject scriptElementAttributesJSONObject =
			_jsonFactory.createJSONObject(
				newGlobalJSCET.getScriptElementAttributesJSON());

		Set<String> keySet = scriptElementAttributesJSONObject.keySet();

		if (keySet.contains("src")) {
			throw new ClientExtensionEntryTypeSettingsException(
				"Use the \"JavaScript URL\" field instead of the attribute " +
					"\"src\"",
				"use-the-javascript-url-field-instead-of-the-attribute-src");
		}
	}

	private final JSONFactory _jsonFactory;

}