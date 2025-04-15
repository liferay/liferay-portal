/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.address.content.web.internal.portlet.action;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.constants.CommerceAddressConstants;
import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.exception.CommerceAddressCityException;
import com.liferay.commerce.exception.CommerceAddressCountryException;
import com.liferay.commerce.exception.CommerceAddressStreetException;
import com.liferay.commerce.exception.NoSuchAddressException;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.service.CommerceAddressService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_ADDRESS_CONTENT,
		"mvc.command.name=/commerce_address_content/edit_commerce_address"
	},
	service = MVCActionCommand.class
)
public class EditCommerceAddressMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.DELETE)) {
				_deleteCommerceAddress(actionRequest);
			}
			else if (cmd.equals(Constants.ADD) ||
					 cmd.equals(Constants.UPDATE)) {

				_updateCommerceAddress(actionRequest);
			}
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchAddressException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
			else if (exception instanceof CommerceAddressCityException ||
					 exception instanceof CommerceAddressCountryException ||
					 exception instanceof CommerceAddressStreetException) {

				hideDefaultErrorMessage(actionRequest);

				SessionErrors.add(actionRequest, exception.getClass());

				String redirect = _portal.getCurrentURL(actionRequest);

				sendRedirect(actionRequest, actionResponse, redirect);
			}
			else {
				throw exception;
			}
		}

		hideDefaultSuccessMessage(actionRequest);
	}

	private void _deleteCommerceAddress(ActionRequest actionRequest)
		throws Exception {

		long commerceAddressId = ParamUtil.getLong(
			actionRequest, "commerceAddressId");

		if (commerceAddressId > 0) {
			_commerceAddressService.deleteCommerceAddress(commerceAddressId);
		}
	}

	private int _getType(ActionRequest actionRequest) {
		int type = CommerceAddressConstants.ADDRESS_TYPE_BILLING_AND_SHIPPING;

		boolean defaultBilling = ParamUtil.getBoolean(
			actionRequest, "defaultBilling");
		boolean defaultShipping = ParamUtil.getBoolean(
			actionRequest, "defaultShipping");

		if (defaultBilling && !defaultShipping) {
			type = CommerceAddressConstants.ADDRESS_TYPE_BILLING;
		}
		else if (!defaultBilling && defaultShipping) {
			type = CommerceAddressConstants.ADDRESS_TYPE_SHIPPING;
		}

		return type;
	}

	private void _updateCommerceAddress(ActionRequest actionRequest)
		throws Exception {

		long commerceAddressId = ParamUtil.getLong(
			actionRequest, "commerceAddressId");

		if (commerceAddressId <= 0) {
			long commerceAccountId = ParamUtil.getLong(
				actionRequest, "commerceAccountId");

			_commerceAddressService.addCommerceAddress(
				StringPool.BLANK, AccountEntry.class.getName(),
				commerceAccountId,
				ParamUtil.getLong(actionRequest, "countryId"),
				ParamUtil.getLong(actionRequest, "regionId"),
				ParamUtil.getString(actionRequest, "city"),
				ParamUtil.getString(actionRequest, "description"),
				ParamUtil.getString(actionRequest, "name"),
				ParamUtil.getString(actionRequest, "phoneNumber"),
				ParamUtil.getString(actionRequest, "street1"),
				ParamUtil.getString(actionRequest, "street2"),
				ParamUtil.getString(actionRequest, "street3"), StringPool.BLANK,
				_getType(actionRequest),
				ParamUtil.getString(actionRequest, "zip"),
				ServiceContextFactory.getInstance(
					CommerceAddress.class.getName(), actionRequest));
		}
		else {
			CommerceAddress commerceAddress =
				_commerceAddressService.getCommerceAddress(commerceAddressId);

			_commerceAddressService.updateCommerceAddress(
				commerceAddress.getExternalReferenceCode(), commerceAddressId,
				ParamUtil.getLong(actionRequest, "countryId"),
				ParamUtil.getLong(actionRequest, "regionId"),
				ParamUtil.getString(actionRequest, "city"),
				ParamUtil.getString(actionRequest, "description"),
				ParamUtil.getString(actionRequest, "name"),
				ParamUtil.getString(actionRequest, "phoneNumber"),
				ParamUtil.getString(actionRequest, "street1"),
				ParamUtil.getString(actionRequest, "street2"),
				ParamUtil.getString(actionRequest, "street3"),
				commerceAddress.getSubtype(), _getType(actionRequest),
				ParamUtil.getString(actionRequest, "zip"),
				ServiceContextFactory.getInstance(
					CommerceAddress.class.getName(), actionRequest));
		}
	}

	@Reference
	private CommerceAddressService _commerceAddressService;

	@Reference
	private Portal _portal;

}