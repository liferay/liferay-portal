/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.web.internal.portlet.action;

import com.liferay.commerce.inventory.exception.CommerceInventoryWarehouseItemQuantityException;
import com.liferay.commerce.inventory.exception.MVCCException;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouseItem;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseItemService;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.util.CommerceOrderItemQuantityFormatter;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
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
		"jakarta.portlet.name=" + CPPortletKeys.COMMERCE_INVENTORY,
		"mvc.command.name=/commerce_inventory/edit_commerce_inventory_warehouse_item"
	},
	service = MVCActionCommand.class
)
public class EditCommerceInventoryWarehouseItemMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.DELETE)) {
				_deleteCommerceInventoryWarehouseItem(actionRequest);
			}
			else if (cmd.equals(Constants.UPDATE)) {
				_updateCommerceInventoryWarehouseItem(actionRequest);
			}
		}
		catch (Exception exception) {
			if (exception instanceof
					CommerceInventoryWarehouseItemQuantityException ||
				exception instanceof MVCCException) {

				SessionErrors.add(actionRequest, exception.getClass());

				hideDefaultErrorMessage(actionRequest);
				hideDefaultSuccessMessage(actionRequest);

				sendRedirect(actionRequest, actionResponse);
			}
			else {
				_log.error(exception);
			}
		}
	}

	private void _deleteCommerceInventoryWarehouseItem(
			ActionRequest actionRequest)
		throws PortalException {

		long commerceInventoryWarehouseItemId = ParamUtil.getLong(
			actionRequest, "commerceInventoryWarehouseItemId");

		_commerceInventoryWarehouseItemService.
			deleteCommerceInventoryWarehouseItem(
				commerceInventoryWarehouseItemId);
	}

	private void _updateCommerceInventoryWarehouseItem(
			ActionRequest actionRequest)
		throws Exception {

		long commerceInventoryWarehouseItemId = ParamUtil.getLong(
			actionRequest, "commerceInventoryWarehouseItemId");

		_commerceInventoryWarehouseItemService.
			updateCommerceInventoryWarehouseItem(
				commerceInventoryWarehouseItemId,
				_commerceOrderItemQuantityFormatter.parse(
					actionRequest,
					CommerceInventoryWarehouseItem.class.getName(), "quantity"),
				_commerceOrderItemQuantityFormatter.parse(
					actionRequest,
					CommerceInventoryWarehouseItem.class.getName(),
					"reservedQuantity"),
				ParamUtil.getLong(actionRequest, "mvccVersion"));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditCommerceInventoryWarehouseItemMVCActionCommand.class);

	@Reference
	private CommerceInventoryWarehouseItemService
		_commerceInventoryWarehouseItemService;

	@Reference
	private CommerceOrderItemQuantityFormatter
		_commerceOrderItemQuantityFormatter;

}