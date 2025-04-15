/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.type.internal.factory;

import com.liferay.client.extension.type.CommerceCheckoutStepCET;
import com.liferay.client.extension.type.internal.CommerceCheckoutStepCETImpl;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;

import jakarta.portlet.PortletRequest;

import java.util.Date;
import java.util.Properties;

/**
 * @author Andrea Sbarra
 */
public class CommerceCheckoutStepCETImplFactoryImpl
	extends BaseCETImplFactoryImpl<CommerceCheckoutStepCET> {

	public CommerceCheckoutStepCETImplFactoryImpl() {
		super(CommerceCheckoutStepCET.class);
	}

	@Override
	public CommerceCheckoutStepCET create(
		String baseURL, long companyId, Date createDate, String description,
		String externalReferenceCode, Date modifiedDate, String name,
		Properties properties, boolean readOnly, String sourceCodeURL,
		int status, UnicodeProperties typeSettingsUnicodeProperties) {

		return new CommerceCheckoutStepCETImpl(
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
			"active", ParamUtil.getBoolean(portletRequest, "active")
		).put(
			"checkoutStepLabel",
			ParamUtil.getString(
				portletRequest, "checkoutStepLabel", "stepLabel")
		).put(
			"checkoutStepName",
			ParamUtil.getString(portletRequest, "checkoutStepName", "stepName")
		).put(
			"checkoutStepOrder",
			ParamUtil.getString(portletRequest, "checkoutStepOrder", "1")
		).put(
			"oAuth2ApplicationExternalReferenceCode",
			ParamUtil.getString(
				portletRequest, "oAuth2ApplicationExternalReferenceCode")
		).put(
			"order", ParamUtil.getBoolean(portletRequest, "order")
		).put(
			"paymentMethodKey",
			ParamUtil.getBoolean(portletRequest, "paymentMethodKey")
		).put(
			"sennaDisabled",
			ParamUtil.getBoolean(portletRequest, "sennaDisabled")
		).put(
			"showControls", ParamUtil.getBoolean(portletRequest, "showControls")
		).put(
			"visible", ParamUtil.getBoolean(portletRequest, "visible")
		).build();
	}

	@Override
	public void validate(
			CommerceCheckoutStepCET newCET, CommerceCheckoutStepCET oldCET)
		throws PortalException {
	}

}