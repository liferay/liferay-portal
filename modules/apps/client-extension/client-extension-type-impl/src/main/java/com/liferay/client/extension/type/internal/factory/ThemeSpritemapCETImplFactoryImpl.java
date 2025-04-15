/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.type.internal.factory;

import com.liferay.client.extension.exception.ClientExtensionEntryTypeSettingsException;
import com.liferay.client.extension.type.ThemeSpritemapCET;
import com.liferay.client.extension.type.internal.ThemeSpritemapCETImpl;
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
public class ThemeSpritemapCETImplFactoryImpl
	extends BaseCETImplFactoryImpl<ThemeSpritemapCET> {

	public ThemeSpritemapCETImplFactoryImpl() {
		super(ThemeSpritemapCET.class);
	}

	@Override
	public ThemeSpritemapCET create(
		String baseURL, long companyId, Date createDate, String description,
		String externalReferenceCode, Date modifiedDate, String name,
		Properties properties, boolean readOnly, String sourceCodeURL,
		int status, UnicodeProperties typeSettingsUnicodeProperties) {

		return new ThemeSpritemapCETImpl(
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
			"enableSVG4Everybody",
			ParamUtil.getString(portletRequest, "enableSVG4Everybody")
		).put(
			"url", ParamUtil.getString(portletRequest, "url")
		).build();
	}

	@Override
	public void validate(
			ThemeSpritemapCET newThemeSpritemapCET,
			ThemeSpritemapCET oldThemeSpritemapCET)
		throws PortalException {

		String url = newThemeSpritemapCET.getURL();

		if (!Validator.isUrl(url)) {
			throw new ClientExtensionEntryTypeSettingsException(
				"Invalid URL: " + url, "url-x-is-invalid", url);
		}
	}

}