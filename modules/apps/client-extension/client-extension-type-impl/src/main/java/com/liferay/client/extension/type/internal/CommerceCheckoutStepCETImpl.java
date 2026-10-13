/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.type.internal;

import com.liferay.client.extension.constants.ClientExtensionEntryConstants;
import com.liferay.client.extension.type.CommerceCheckoutStepCET;
import com.liferay.portal.kernel.util.UnicodeProperties;

import java.util.Date;
import java.util.Properties;

/**
 * @author Andrea Sbarra
 */
public class CommerceCheckoutStepCETImpl
	extends BaseCETImpl implements CommerceCheckoutStepCET {

	public CommerceCheckoutStepCETImpl(
		String baseURL, long companyId, Date createDate, String description,
		String externalReferenceCode, Date modifiedDate, String name,
		Properties properties, boolean readOnly, String sourceCodeURL,
		int status, UnicodeProperties typeSettingsUnicodeProperties) {

		super(
			baseURL, companyId, createDate, description, externalReferenceCode,
			modifiedDate, name, properties, readOnly, sourceCodeURL, status,
			typeSettingsUnicodeProperties);
	}

	@Override
	public boolean getActive() {
		return getBoolean("active");
	}

	@Override
	public String getCheckoutStepLabel() {
		return getString("checkoutStepLabel");
	}

	@Override
	public String getCheckoutStepName() {
		return getString("checkoutStepName");
	}

	@Override
	public int getCheckoutStepOrder() {
		return Integer.valueOf(getString("checkoutStepOrder"));
	}

	@Override
	public boolean getContinueAfterPayment() {
		return getBoolean("continueAfterPayment");
	}

	@Override
	public String getEditJSP() {
		return "/admin/edit_commerce_checkout_step.jsp";
	}

	@Override
	public String getOAuth2ApplicationExternalReferenceCode() {
		return getString("oAuth2ApplicationExternalReferenceCode");
	}

	@Override
	public boolean getOrder() {
		return getBoolean("order");
	}

	@Override
	public String getPaymentMethodKey() {
		return getString("paymentMethodKey");
	}

	@Override
	public boolean getSennaDisabled() {
		return getBoolean("sennaDisabled");
	}

	@Override
	public boolean getShowControls() {
		return getBoolean("showControls");
	}

	@Override
	public String getType() {
		return ClientExtensionEntryConstants.TYPE_COMMERCE_CHECKOUT_STEP;
	}

	@Override
	public boolean getVisible() {
		return getBoolean("visible");
	}

	@Override
	public boolean hasProperties() {
		return true;
	}

}