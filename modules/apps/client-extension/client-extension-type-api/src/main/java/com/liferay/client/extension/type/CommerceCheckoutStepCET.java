/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.type;

import com.liferay.client.extension.type.annotation.CETProperty;
import com.liferay.client.extension.type.annotation.CETType;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Andrea Sbarra
 */
@CETType(description = "This is a description.", name = "commerceCheckoutStep")
@ProviderType
public interface CommerceCheckoutStepCET extends CET {

	@CETProperty(
		defaultValue = "", name = "active", type = CETProperty.Type.Boolean
	)
	public boolean getActive();

	@CETProperty(
		defaultValue = "", name = "checkoutStepLabel",
		type = CETProperty.Type.String
	)
	public String getCheckoutStepLabel();

	@CETProperty(
		defaultValue = "", name = "checkoutStepName",
		type = CETProperty.Type.String
	)
	public String getCheckoutStepName();

	@CETProperty(
		defaultValue = "", name = "checkoutStepOrder",
		type = CETProperty.Type.String
	)
	public int getCheckoutStepOrder();

	@CETProperty(
		defaultValue = "", name = "continueAfterPayment",
		type = CETProperty.Type.Boolean
	)
	public boolean getContinueAfterPayment();

	@CETProperty(
		defaultValue = "", name = "oAuth2ApplicationExternalReferenceCode",
		type = CETProperty.Type.String
	)
	public String getOAuth2ApplicationExternalReferenceCode();

	@CETProperty(
		defaultValue = "", name = "order", type = CETProperty.Type.Boolean
	)
	public boolean getOrder();

	@CETProperty(
		defaultValue = "", name = "paymentMethodKey",
		type = CETProperty.Type.String
	)
	public String getPaymentMethodKey();

	@CETProperty(
		defaultValue = "", name = "sennaDisabled",
		type = CETProperty.Type.Boolean
	)
	public boolean getSennaDisabled();

	@CETProperty(
		defaultValue = "", name = "showControls",
		type = CETProperty.Type.Boolean
	)
	public boolean getShowControls();

	@CETProperty(
		defaultValue = "", name = "visible", type = CETProperty.Type.Boolean
	)
	public boolean getVisible();

}