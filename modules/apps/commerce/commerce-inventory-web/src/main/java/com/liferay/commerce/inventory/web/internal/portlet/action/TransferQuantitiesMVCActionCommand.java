/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.web.internal.portlet.action;

import com.liferay.commerce.inventory.exception.CommerceInventoryWarehouseItemQuantityException;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouseItem;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseItemService;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.util.CommerceOrderItemQuantityFormatter;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
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
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.COMMERCE_INVENTORY,
		"mvc.command.name=/commerce_inventory/transfer_quantities"
	},
	service = MVCActionCommand.class
)
public class TransferQuantitiesMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

			if (cmd.equals(Constants.MOVE)) {
				_moveQuantities(actionRequest);
			}
		}
		catch (Exception exception) {
			if (exception instanceof
					CommerceInventoryWarehouseItemQuantityException ||
				exception instanceof PrincipalException.MustHavePermission) {

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

	private void _moveQuantities(ActionRequest actionRequest) throws Exception {
		long fromCommerceInventoryWarehouseId = ParamUtil.getLong(
			actionRequest, "fromCommerceInventoryWarehouseId");
		long toCommerceInventoryWarehouseId = ParamUtil.getLong(
			actionRequest, "toCommerceInventoryWarehouseId");

		_commerceInventoryWarehouseItemService.moveQuantitiesBetweenWarehouses(
			fromCommerceInventoryWarehouseId, toCommerceInventoryWarehouseId,
			_commerceOrderItemQuantityFormatter.parse(
				actionRequest, CommerceInventoryWarehouseItem.class.getName(),
				"quantity"),
			ParamUtil.getString(actionRequest, "sku"),
			ParamUtil.getString(actionRequest, "unitOfMeasureKey"));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		TransferQuantitiesMVCActionCommand.class);

	@Reference
	private CommerceInventoryWarehouseItemService
		_commerceInventoryWarehouseItemService;

	@Reference
	private CommerceOrderItemQuantityFormatter
		_commerceOrderItemQuantityFormatter;

}