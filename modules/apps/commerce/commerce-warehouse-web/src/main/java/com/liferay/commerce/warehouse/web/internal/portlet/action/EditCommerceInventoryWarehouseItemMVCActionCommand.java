/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.warehouse.web.internal.portlet.action;

import com.liferay.commerce.exception.NoSuchWarehouseItemException;
import com.liferay.commerce.inventory.exception.CommerceInventoryWarehouseItemQuantityException;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouseItem;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseItemService;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.util.CommerceOrderItemQuantityFormatter;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.math.BigDecimal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Di Giorgi
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.CP_DEFINITIONS,
		"mvc.command.name=/cp_definitions/edit_commerce_inventory_warehouse_item"
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
			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				_updateCommerceInventoryWarehouseItem(actionRequest);
			}
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchWarehouseItemException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
			else if (exception instanceof
						CommerceInventoryWarehouseItemQuantityException) {

				SessionErrors.add(actionRequest, exception.getClass());

				String redirect = ParamUtil.getString(
					actionRequest, "redirect");

				sendRedirect(actionRequest, actionResponse, redirect);
			}
			else {
				throw exception;
			}
		}
	}

	private CommerceInventoryWarehouseItem
			_updateCommerceInventoryWarehouseItem(ActionRequest actionRequest)
		throws Exception {

		CommerceInventoryWarehouseItem commerceInventoryWarehouseItem = null;

		long commerceInventoryWarehouseItemId = ParamUtil.getLong(
			actionRequest, "commerceInventoryWarehouseItemId");

		BigDecimal quantity = _commerceOrderItemQuantityFormatter.parse(
			actionRequest, CommerceInventoryWarehouseItem.class.getName(),
			"quantity");
		String unitOfMeasureKey = ParamUtil.getString(
			actionRequest, "unitOfMeasureKey");

		if (commerceInventoryWarehouseItemId > 0) {
			commerceInventoryWarehouseItem =
				_commerceInventoryWarehouseItemService.
					updateCommerceInventoryWarehouseItem(
						commerceInventoryWarehouseItemId,
						ParamUtil.getLong(actionRequest, "mvccVersion"),
						quantity, unitOfMeasureKey);
		}
		else {
			commerceInventoryWarehouseItem =
				_commerceInventoryWarehouseItemService.
					addCommerceInventoryWarehouseItem(
						StringPool.BLANK,
						ParamUtil.getLong(
							actionRequest, "commerceInventoryWarehouseId"),
						quantity, ParamUtil.getString(actionRequest, "sku"),
						unitOfMeasureKey);
		}

		return commerceInventoryWarehouseItem;
	}

	@Reference
	private CommerceInventoryWarehouseItemService
		_commerceInventoryWarehouseItemService;

	@Reference
	private CommerceOrderItemQuantityFormatter
		_commerceOrderItemQuantityFormatter;

}