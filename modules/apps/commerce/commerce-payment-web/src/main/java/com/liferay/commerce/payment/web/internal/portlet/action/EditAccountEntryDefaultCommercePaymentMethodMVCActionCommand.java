/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.web.internal.portlet.action;

import com.liferay.account.constants.AccountPortletKeys;
import com.liferay.commerce.payment.exception.NoSuchPaymentMethodGroupRelException;
import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelService;
import com.liferay.commerce.product.constants.CommerceChannelAccountEntryRelConstants;
import com.liferay.commerce.product.model.CommerceChannelAccountEntryRel;
import com.liferay.commerce.product.service.CommerceChannelAccountEntryRelService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 * @author Crescenzo Rega
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AccountPortletKeys.ACCOUNT_ENTRIES_ADMIN,
		"jakarta.portlet.name=" + AccountPortletKeys.ACCOUNT_ENTRIES_MANAGEMENT,
		"mvc.command.name=/commerce_payment/edit_account_entry_default_commerce_payment_method"
	},
	service = MVCActionCommand.class
)
public class EditAccountEntryDefaultCommercePaymentMethodMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.UPDATE)) {
				_updateCommerceChannelAccountEntryRel(actionRequest);
			}
		}
		catch (Exception exception) {
			if (exception instanceof PrincipalException) {
				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
			else if (exception instanceof
						NoSuchPaymentMethodGroupRelException) {

				SessionErrors.add(actionRequest, exception.getClass());

				hideDefaultErrorMessage(actionRequest);
			}
			else {
				throw exception;
			}
		}

		String redirect = ParamUtil.getString(actionRequest, "redirect");

		if (Validator.isNotNull(redirect)) {
			sendRedirect(actionRequest, actionResponse, redirect);
		}
	}

	private CommerceChannelAccountEntryRel
			_updateCommerceChannelAccountEntryRel(ActionRequest actionRequest)
		throws PortalException {

		long accountEntryId = ParamUtil.getLong(
			actionRequest, "accountEntryId");
		long commerceChannelId = ParamUtil.getLong(
			actionRequest, "commerceChannelId");
		long commercePaymentMethodGroupRelId = ParamUtil.getLong(
			actionRequest, "commercePaymentMethodGroupRelId");

		CommerceChannelAccountEntryRel commerceChannelAccountEntryRel =
			_commerceChannelAccountEntryRelService.
				fetchCommerceChannelAccountEntryRel(
					accountEntryId, commerceChannelId,
					CommerceChannelAccountEntryRelConstants.TYPE_PAYMENT);

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			_commercePaymentMethodGroupRelService.
				fetchCommercePaymentMethodGroupRel(
					commercePaymentMethodGroupRelId);

		if (commercePaymentMethodGroupRel == null) {
			if (commerceChannelAccountEntryRel != null) {
				_commerceChannelAccountEntryRelService.
					deleteCommerceChannelAccountEntryRel(
						commerceChannelAccountEntryRel.
							getCommerceChannelAccountEntryRelId());
			}

			return null;
		}

		if (commerceChannelAccountEntryRel == null) {
			return _commerceChannelAccountEntryRelService.
				addCommerceChannelAccountEntryRel(
					accountEntryId,
					CommercePaymentMethodGroupRel.class.getName(),
					commercePaymentMethodGroupRelId, commerceChannelId, false,
					0, CommerceChannelAccountEntryRelConstants.TYPE_PAYMENT);
		}

		return _commerceChannelAccountEntryRelService.
			updateCommerceChannelAccountEntryRel(
				commerceChannelAccountEntryRel.
					getCommerceChannelAccountEntryRelId(),
				commerceChannelId, commercePaymentMethodGroupRelId, false, 0);
	}

	@Reference
	private CommerceChannelAccountEntryRelService
		_commerceChannelAccountEntryRelService;

	@Reference
	private CommercePaymentMethodGroupRelService
		_commercePaymentMethodGroupRelService;

}