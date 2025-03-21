/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.channel.web.internal.portlet.action;

import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.exception.NoSuchChannelException;
import com.liferay.commerce.product.service.CommerceChannelRelService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Fabio Monaco
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.COMMERCE_CHANNELS,
		"mvc.command.name=/commerce_channels/edit_commerce_channel_commerce_currency"
	},
	service = MVCActionCommand.class
)
public class EditCommerceChannelCommerceCurrencyMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.DELETE)) {
				_commerceChannelRelService.deleteCommerceChannelRel(
					ParamUtil.getLong(actionRequest, "commerceChannelRelId"));
			}
			else if (cmd.equals(Constants.ADD_MULTIPLE)) {
				_addCommerceChannelRels(
					actionRequest,
					ParamUtil.getLong(actionRequest, "commerceChannelId"),
					ParamUtil.getLongValues(actionRequest, "currencyIds"));
			}
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchChannelException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
			else {
				throw exception;
			}
		}
	}

	private void _addCommerceChannelRels(
			ActionRequest actionRequest, long commerceChannelId,
			long[] currencyIds)
		throws Exception {

		_commerceChannelRelService.addCommerceChannelRels(
			CommerceCurrency.class.getName(), currencyIds, commerceChannelId,
			ServiceContextFactory.getInstance(actionRequest));
	}

	@Reference
	private CommerceChannelRelService _commerceChannelRelService;

}