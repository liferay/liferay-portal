/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.type.internal.factory;

import com.liferay.client.extension.exception.ClientExtensionEntryTypeSettingsException;
import com.liferay.client.extension.type.FDSCellRendererCET;
import com.liferay.client.extension.type.internal.FDSCellRendererCETImpl;
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
public class FDSCellRendererCETImplFactoryImpl
	extends BaseCETImplFactoryImpl<FDSCellRendererCET> {

	public FDSCellRendererCETImplFactoryImpl() {
		super(FDSCellRendererCET.class);
	}

	@Override
	public FDSCellRendererCET create(
		String baseURL, long companyId, Date createDate, String description,
		String externalReferenceCode, Date modifiedDate, String name,
		Properties properties, boolean readOnly, String sourceCodeURL,
		int status, UnicodeProperties typeSettingsUnicodeProperties) {

		return new FDSCellRendererCETImpl(
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
			"url", ParamUtil.getString(portletRequest, "url")
		).build();
	}

	@Override
	public void validate(
			FDSCellRendererCET newFDSCellRendererCET,
			FDSCellRendererCET oldFDSCellRendererCET)
		throws PortalException {

		if (Validator.isNull(newFDSCellRendererCET.getURL())) {
			throw new ClientExtensionEntryTypeSettingsException(
				"At least one JavaScript URL is required",
				"please-enter-at-least-one-javascript-url");
		}
	}

}