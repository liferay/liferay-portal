/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.warehouse.web.internal.portlet.action;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountGroup;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseRelService;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.service.CommerceChannelRelService;
import com.liferay.portal.kernel.exception.PortalException;
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
 * @author Crescenzo Rega
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.COMMERCE_INVENTORY_WAREHOUSE,
		"mvc.command.name=/commerce_inventory_warehouse/edit_commerce_inventory_warehouse_qualifiers"
	},
	service = MVCActionCommand.class
)
public class EditCommerceInventoryWarehouseQualifiersMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				_updateCommerceInventoryWarehouseQualifiers(actionRequest);
			}
		}
		catch (Exception exception) {
			SessionErrors.add(actionRequest, exception.getClass());

			actionResponse.setRenderParameter("mvcPath", "/error.jsp");
		}
	}

	private void _updateCommerceInventoryWarehouseQualifiers(
			ActionRequest actionRequest)
		throws PortalException {

		long commerceInventoryWarehouseId = ParamUtil.getLong(
			actionRequest, "commerceInventoryWarehouseId");

		String accountQualifiers = ParamUtil.getString(
			actionRequest, "accountQualifiers");

		if (Objects.equals(accountQualifiers, "all")) {
			_commerceInventoryWarehouseRelService.
				deleteCommerceInventoryWarehouseRels(
					AccountEntry.class.getName(), commerceInventoryWarehouseId);
			_commerceInventoryWarehouseRelService.
				deleteCommerceInventoryWarehouseRels(
					AccountGroup.class.getName(), commerceInventoryWarehouseId);
		}
		else if (Objects.equals(accountQualifiers, "accounts")) {
			_commerceInventoryWarehouseRelService.
				deleteCommerceInventoryWarehouseRels(
					AccountGroup.class.getName(), commerceInventoryWarehouseId);
		}
		else {
			_commerceInventoryWarehouseRelService.
				deleteCommerceInventoryWarehouseRels(
					AccountEntry.class.getName(), commerceInventoryWarehouseId);
		}

		String channelQualifiers = ParamUtil.getString(
			actionRequest, "channelQualifiers");

		if (Objects.equals(channelQualifiers, "none")) {
			_commerceChannelRelService.deleteCommerceChannelRels(
				CommerceInventoryWarehouse.class.getName(),
				commerceInventoryWarehouseId);
		}
	}

	@Reference
	private CommerceChannelRelService _commerceChannelRelService;

	@Reference
	private CommerceInventoryWarehouseRelService
		_commerceInventoryWarehouseRelService;

}