/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.subscription.web.internal.portlet.action;

import com.liferay.commerce.constants.CommerceActionKeys;
import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.exception.CommerceSubscriptionEntrySubscriptionStatusException;
import com.liferay.commerce.exception.CommerceSubscriptionTypeException;
import com.liferay.commerce.exception.NoSuchSubscriptionEntryException;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceSubscriptionEntry;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.service.CommerceSubscriptionEntryLocalService;
import com.liferay.commerce.subscription.CommerceSubscriptionEntryActionHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

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

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			CommerceSubscriptionEntry commerceSubscriptionEntry =
				_getCommerceSubscriptionEntry(
					actionRequest,
					ParamUtil.getLong(
						actionRequest, "commerceSubscriptionEntryId"));

			if (cmd.equals("activate")) {
				_commerceSubscriptionEntryActionHelper.
					activateCommerceSubscriptionEntry(
						commerceSubscriptionEntry.
							getCommerceSubscriptionEntryId());
			}
			else if (cmd.equals("cancel")) {
				_commerceSubscriptionEntryActionHelper.
					cancelCommerceSubscriptionEntry(
						commerceSubscriptionEntry.
							getCommerceSubscriptionEntryId());
			}
			else if (cmd.equals("suspend")) {
				_commerceSubscriptionEntryActionHelper.
					suspendCommerceSubscriptionEntry(
						commerceSubscriptionEntry.
							getCommerceSubscriptionEntryId());
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

	private CommerceSubscriptionEntry _getCommerceSubscriptionEntry(
			ActionRequest actionRequest, long commerceSubscriptionEntryId)
		throws PortalException {

		CommerceSubscriptionEntry commerceSubscriptionEntry =
			_commerceSubscriptionEntryLocalService.getCommerceSubscriptionEntry(
				commerceSubscriptionEntryId);

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (!themeDisplay.isSignedIn() ||
			(commerceSubscriptionEntry.getCompanyId() !=
				themeDisplay.getCompanyId()) ||
			((commerceSubscriptionEntry.getUserId() !=
				themeDisplay.getUserId()) &&
			 !_portletResourcePermission.contains(
				 themeDisplay.getPermissionChecker(),
				 commerceSubscriptionEntry.getGroupId(),
				 CommerceActionKeys.MANAGE_COMMERCE_SUBSCRIPTIONS) &&
			 !_hasUpdateCommerceOrderPermission(
				 commerceSubscriptionEntry, themeDisplay))) {

			throw new PrincipalException();
		}

		return commerceSubscriptionEntry;
	}

	private boolean _hasUpdateCommerceOrderPermission(
			CommerceSubscriptionEntry commerceSubscriptionEntry,
			ThemeDisplay themeDisplay)
		throws PortalException {

		CommerceOrderItem commerceOrderItem =
			commerceSubscriptionEntry.fetchCommerceOrderItem();

		if (commerceOrderItem == null) {
			return false;
		}

		return _commerceOrderModelResourcePermission.contains(
			themeDisplay.getPermissionChecker(),
			commerceOrderItem.getCommerceOrderId(), ActionKeys.UPDATE);
	}

	@Reference(
		target = "(model.class.name=com.liferay.commerce.model.CommerceOrder)"
	)
	private ModelResourcePermission<CommerceOrder>
		_commerceOrderModelResourcePermission;

	@Reference
	private CommerceSubscriptionEntryActionHelper
		_commerceSubscriptionEntryActionHelper;

	@Reference
	private CommerceSubscriptionEntryLocalService
		_commerceSubscriptionEntryLocalService;

	@Reference(
		target = "(resource.name=" + CommerceConstants.RESOURCE_NAME_COMMERCE_SUBSCRIPTION + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}