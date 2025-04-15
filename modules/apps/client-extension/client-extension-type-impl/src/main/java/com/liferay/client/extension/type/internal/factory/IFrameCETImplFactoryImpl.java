/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.type.internal.factory;

import com.liferay.client.extension.exception.ClientExtensionEntryTypeSettingsException;
import com.liferay.client.extension.type.IFrameCET;
import com.liferay.client.extension.type.internal.IFrameCETImpl;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletRequest;

import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Iván Zaera Avellón
 */
public class IFrameCETImplFactoryImpl
	extends BaseCETImplFactoryImpl<IFrameCET> {

	public IFrameCETImplFactoryImpl() {
		super(IFrameCET.class);
	}

	@Override
	public IFrameCET create(
		String baseURL, long companyId, Date createDate, String description,
		String externalReferenceCode, Date modifiedDate, String name,
		Properties properties, boolean readOnly, String sourceCodeURL,
		int status, UnicodeProperties typeSettingsUnicodeProperties) {

		return new IFrameCETImpl(
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
			"friendlyURLMapping",
			ParamUtil.getString(portletRequest, "friendlyURLMapping")
		).put(
			"instanceable", ParamUtil.getBoolean(portletRequest, "instanceable")
		).put(
			"portletCategoryName",
			ParamUtil.getString(portletRequest, "portletCategoryName")
		).put(
			"url", ParamUtil.getString(portletRequest, "url")
		).build();
	}

	@Override
	public void validate(IFrameCET newIFrameCET, IFrameCET oldIFrameCET)
		throws PortalException {

		String friendlyURLMapping = newIFrameCET.getFriendlyURLMapping();

		Matcher matcher = _friendlyURLMappingPattern.matcher(
			friendlyURLMapping);

		if (!matcher.matches()) {
			throw new ClientExtensionEntryTypeSettingsException(
				"Invalid friendly URL mapping: " + friendlyURLMapping,
				"friendly-url-mapping-x-is-invalid", friendlyURLMapping);
		}

		String url = newIFrameCET.getURL();

		if (!Validator.isUrl(url)) {
			throw new ClientExtensionEntryTypeSettingsException(
				"Invalid URL: " + url, "url-x-is-invalid", url);
		}

		if ((oldIFrameCET != null) &&
			(newIFrameCET.isInstanceable() != oldIFrameCET.isInstanceable())) {

			throw new ClientExtensionEntryTypeSettingsException(
				"The instanceable value cannot be changed",
				"the-instanceable-value-cannot-be-changed");
		}
	}

	private static final Pattern _friendlyURLMappingPattern = Pattern.compile(
		"[A-Za-z0-9-_]*");

}