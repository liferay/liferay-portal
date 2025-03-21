/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.web.internal.portlet.action;

import com.liferay.commerce.price.list.service.CommercePriceListAccountRelService;
import com.liferay.commerce.price.list.service.CommercePriceListChannelRelService;
import com.liferay.commerce.price.list.service.CommercePriceListCommerceAccountGroupRelService;
import com.liferay.commerce.pricing.constants.CommercePricingPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommercePricingPortletKeys.COMMERCE_PRICE_LIST,
		"jakarta.portlet.name=" + CommercePricingPortletKeys.COMMERCE_PROMOTION,
		"mvc.command.name=/commerce_price_list/edit_commerce_price_list_qualifiers"
	},
	service = MVCActionCommand.class
)
public class EditCommercePriceListQualifiersMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				_updateCommercePriceListQualifiers(actionRequest);
			}
		}
		catch (Exception exception) {
			SessionErrors.add(actionRequest, exception.getClass());

			actionResponse.setRenderParameter("mvcPath", "/error.jsp");
		}
	}

	private void _deleteCommercePriceListAccountGroupRels(
			long commercePriceListId)
		throws Exception {

		int count =
			_commercePriceListCommerceAccountGroupRelService.
				getCommercePriceListCommerceAccountGroupRelsCount(
					commercePriceListId);

		if (count == 0) {
			return;
		}

		_commercePriceListCommerceAccountGroupRelService.
			deleteCommercePriceListAccountGroupRelsByCommercePriceListId(
				commercePriceListId);
	}

	private void _deleteCommercePriceListAccountRels(long commercePriceListId)
		throws Exception {

		int count =
			_commercePriceListAccountRelService.
				getCommercePriceListAccountRelsCount(commercePriceListId);

		if (count == 0) {
			return;
		}

		_commercePriceListAccountRelService.
			deleteCommercePriceListAccountRelsByCommercePriceListId(
				commercePriceListId);
	}

	private void _updateCommercePriceListQualifiers(ActionRequest actionRequest)
		throws Exception {

		long commercePriceListId = ParamUtil.getLong(
			actionRequest, "commercePriceListId");

		String accountQualifiers = ParamUtil.getString(
			actionRequest, "accountQualifiers");

		String channelQualifiers = ParamUtil.getString(
			actionRequest, "channelQualifiers");

		if (Objects.equals(accountQualifiers, "all")) {
			_deleteCommercePriceListAccountRels(commercePriceListId);
			_deleteCommercePriceListAccountGroupRels(commercePriceListId);
		}
		else if (Objects.equals(accountQualifiers, "accounts")) {
			_deleteCommercePriceListAccountGroupRels(commercePriceListId);
		}
		else {
			_deleteCommercePriceListAccountRels(commercePriceListId);
		}

		if (Objects.equals(channelQualifiers, "all")) {
			_commercePriceListChannelRelService.
				deleteCommercePriceListChannelRelsByCommercePriceListId(
					commercePriceListId);
		}
	}

	@Reference
	private CommercePriceListAccountRelService
		_commercePriceListAccountRelService;

	@Reference
	private CommercePriceListChannelRelService
		_commercePriceListChannelRelService;

	@Reference
	private CommercePriceListCommerceAccountGroupRelService
		_commercePriceListCommerceAccountGroupRelService;

}