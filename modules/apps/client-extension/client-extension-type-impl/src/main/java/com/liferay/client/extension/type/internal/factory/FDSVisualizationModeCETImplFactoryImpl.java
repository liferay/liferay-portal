/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.type.internal.factory;

import com.liferay.client.extension.exception.ClientExtensionEntryTypeSettingsException;
import com.liferay.client.extension.type.FDSVisualizationModeCET;
import com.liferay.client.extension.type.internal.FDSVisualizationModeCETImpl;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletRequest;

import java.util.Date;
import java.util.Properties;

/**
 * @author Antonio Ortega
 */
public class FDSVisualizationModeCETImplFactoryImpl
	extends BaseCETImplFactoryImpl<FDSVisualizationModeCET> {

	public FDSVisualizationModeCETImplFactoryImpl() {
		super(FDSVisualizationModeCET.class);
	}

	@Override
	public FDSVisualizationModeCET create(
		String baseURL, long companyId, Date createDate, String description,
		String externalReferenceCode, Date modifiedDate, String name,
		Properties properties, boolean readOnly, String sourceCodeURL,
		int status, UnicodeProperties typeSettingsUnicodeProperties) {

		return new FDSVisualizationModeCETImpl(
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
			"icon", ParamUtil.getString(portletRequest, "icon")
		).put(
			"url", ParamUtil.getString(portletRequest, "url")
		).build();
	}

	@Override
	public void validate(
			FDSVisualizationModeCET newFDSVisualizationModeCET,
			FDSVisualizationModeCET oldFDSVisualizationModeCET)
		throws PortalException {

		if (Validator.isNull(newFDSVisualizationModeCET.getURL())) {
			throw new ClientExtensionEntryTypeSettingsException(
				"At least one JavaScript URL is required",
				"please-enter-at-least-one-javascript-url");
		}

		if (Validator.isNull(newFDSVisualizationModeCET.getIcon())) {
			throw new ClientExtensionEntryTypeSettingsException(
				"Icon is null", "please-select-an-icon");
		}
	}

}