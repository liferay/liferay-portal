/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.type.internal.factory;

import com.liferay.client.extension.exception.ClientExtensionEntryTypeSettingsException;
import com.liferay.client.extension.type.JSImportMapsEntryCET;
import com.liferay.client.extension.type.internal.JSImportMapsEntryCETImpl;
import com.liferay.portal.kernel.exception.PortalException;
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
public class JSImportMapsEntryCETImplFactoryImpl
	extends BaseCETImplFactoryImpl<JSImportMapsEntryCET> {

	public JSImportMapsEntryCETImplFactoryImpl() {
		super(JSImportMapsEntryCET.class);
	}

	@Override
	public JSImportMapsEntryCET create(
		String baseURL, long companyId, Date createDate, String description,
		String externalReferenceCode, Date modifiedDate, String name,
		Properties properties, boolean readOnly, String sourceCodeURL,
		int status, UnicodeProperties typeSettingsUnicodeProperties) {

		return new JSImportMapsEntryCETImpl(
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
			"bareSpecifier",
			ParamUtil.getString(portletRequest, "bareSpecifier")
		).put(
			"url", ParamUtil.getString(portletRequest, "url")
		).build();
	}

	@Override
	public void validate(
			JSImportMapsEntryCET newJSImportMapsEntryCET,
			JSImportMapsEntryCET oldJSImportMapsEntryCET)
		throws PortalException {

		if (Validator.isNull(newJSImportMapsEntryCET.getBareSpecifier())) {
			throw new ClientExtensionEntryTypeSettingsException(
				"Bare specifier is null", "please-enter-a-bare-specifier");
		}

		String url = newJSImportMapsEntryCET.getURL();

		if (!Validator.isUrl(url, true)) {
			throw new ClientExtensionEntryTypeSettingsException(
				"Invalid JavaScript URL: " + url, "javascript-url-x-is-invalid",
				url);
		}
	}

}