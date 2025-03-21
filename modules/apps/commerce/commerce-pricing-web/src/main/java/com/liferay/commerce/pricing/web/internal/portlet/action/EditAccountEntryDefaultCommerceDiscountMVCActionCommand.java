/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.web.internal.portlet.action;

import com.liferay.account.constants.AccountPortletKeys;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.product.exception.DuplicateCommerceChannelAccountEntryRelException;
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
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AccountPortletKeys.ACCOUNT_ENTRIES_ADMIN,
		"jakarta.portlet.name=" + AccountPortletKeys.ACCOUNT_ENTRIES_MANAGEMENT,
		"mvc.command.name=/commerce_pricing/edit_account_entry_default_commerce_discount"
	},
	service = MVCActionCommand.class
)
public class EditAccountEntryDefaultCommerceDiscountMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			long commerceChannelAccountEntryRelId = ParamUtil.getLong(
				actionRequest, "commerceChannelAccountEntryRelId");

			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				_updateCommerceChannelAccountEntryRel(
					actionRequest, commerceChannelAccountEntryRelId);
			}
			else if (cmd.equals(Constants.DELETE)) {
				_commerceChannelAccountEntryRelService.
					deleteCommerceChannelAccountEntryRel(
						commerceChannelAccountEntryRelId);
			}
		}
		catch (Exception exception) {
			if (exception instanceof PrincipalException) {
				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter(
					"mvcPath", "/account_entries_admin/error.jsp");
			}
			else if (exception instanceof
						DuplicateCommerceChannelAccountEntryRelException) {

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

	private void _updateCommerceChannelAccountEntryRel(
			ActionRequest actionRequest, long commerceChannelAccountEntryRelId)
		throws PortalException {

		long commerceChannelId = ParamUtil.getLong(
			actionRequest, "commerceChannelId");
		long classPK = ParamUtil.getLong(actionRequest, "classPK");
		boolean overrideEligibility = ParamUtil.getBoolean(
			actionRequest, "overrideEligibility");

		if (commerceChannelAccountEntryRelId > 0) {
			_commerceChannelAccountEntryRelService.
				updateCommerceChannelAccountEntryRel(
					commerceChannelAccountEntryRelId, commerceChannelId,
					classPK, overrideEligibility, 0);
		}
		else {
			long accountEntryId = ParamUtil.getLong(
				actionRequest, "accountEntryId");
			int type = ParamUtil.getInteger(actionRequest, "type");

			_commerceChannelAccountEntryRelService.
				addCommerceChannelAccountEntryRel(
					accountEntryId, CommerceDiscount.class.getName(), classPK,
					commerceChannelId, overrideEligibility, 0, type);
		}
	}

	@Reference
	private CommerceChannelAccountEntryRelService
		_commerceChannelAccountEntryRelService;

}