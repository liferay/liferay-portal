/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.web.internal.portlet.action;

import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.payment.service.CommercePaymentEntryService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_PAYMENT,
		"mvc.command.name=/commerce_payment/edit_commerce_payment_entry_external_reference_code"
	},
	service = MVCActionCommand.class
)
public class EditCommercePaymentEntryExternalReferenceCodeMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long commercePaymentEntryId = ParamUtil.getLong(
			actionRequest, "commercePaymentEntryId");

		String externalReferenceCode = ParamUtil.getString(
			actionRequest, "externalReferenceCode");

		_commercePaymentEntryService.updateExternalReferenceCode(
			commercePaymentEntryId, externalReferenceCode);
	}

	@Reference
	private CommercePaymentEntryService _commercePaymentEntryService;

}