/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.subscription.web.internal.portlet.action;

import com.liferay.commerce.exception.CommerceSubscriptionEntrySubscriptionStatusException;
import com.liferay.commerce.exception.CommerceSubscriptionTypeException;
import com.liferay.commerce.exception.NoSuchSubscriptionEntryException;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.subscription.CommerceSubscriptionEntryActionHelper;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.COMMERCE_SUBSCRIPTION_CONTENT_WEB,
		"mvc.command.name=/commerce_subscription_content_web/edit_commerce_subscription_content"
	},
	service = MVCActionCommand.class
)
public class EditCommerceSubscriptionContentMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long commerceSubscriptionEntryId = ParamUtil.getLong(
			actionRequest, "commerceSubscriptionEntryId");

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals("activate")) {
				_commerceSubscriptionEntryActionHelper.
					activateCommerceSubscriptionEntry(
						commerceSubscriptionEntryId);
			}
			else if (cmd.equals("cancel")) {
				_commerceSubscriptionEntryActionHelper.
					cancelCommerceSubscriptionEntry(
						commerceSubscriptionEntryId);
			}
			else if (cmd.equals("suspend")) {
				_commerceSubscriptionEntryActionHelper.
					suspendCommerceSubscriptionEntry(
						commerceSubscriptionEntryId);
			}
		}
		catch (Exception exception) {
			if (exception instanceof
					CommerceSubscriptionEntrySubscriptionStatusException ||
				exception instanceof CommerceSubscriptionTypeException) {

				hideDefaultErrorMessage(actionRequest);

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter(
					"mvcRenderCommandName",
					"/commerce_subscription_content_web" +
						"/edit_commerce_subscription_content");
			}
			else if (exception instanceof NoSuchSubscriptionEntryException ||
					 exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
			else {
				throw exception;
			}
		}
	}

	@Reference
	private CommerceSubscriptionEntryActionHelper
		_commerceSubscriptionEntryActionHelper;

}