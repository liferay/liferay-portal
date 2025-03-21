/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.web.internal.portlet.action;

import com.liferay.commerce.inventory.exception.CommerceInventoryWarehouseItemQuantityException;
import com.liferay.commerce.inventory.exception.DuplicateCommerceInventoryWarehouseItemException;
import com.liferay.commerce.inventory.exception.MVCCException;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouseItem;
import com.liferay.commerce.inventory.service.CommerceInventoryReplenishmentItemService;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseItemService;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.exception.CPInstanceUnitOfMeasureKeyException;
import com.liferay.commerce.product.exception.NoSuchCPInstanceUnitOfMeasureException;
import com.liferay.commerce.util.CommerceOrderItemQuantityFormatter;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.math.BigDecimal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.COMMERCE_INVENTORY,
		"mvc.command.name=/commerce_inventory/edit_commerce_inventory_warehouse"
	},
	service = MVCActionCommand.class
)
public class EditCommerceInventoryWarehouseMVCActionCommand
	extends BaseTransactionalMVCActionCommand {

	@Override
	protected void doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.ADD)) {
				_addCommerceInventoryWarehouse(actionRequest);
			}
			else if (cmd.equals(Constants.DELETE)) {
				_deleteCommerceInventoryWarehouse(actionRequest);
			}
			else if (cmd.equals(Constants.UPDATE)) {
				_updateCommerceInventoryWarehouse(actionRequest);
			}
		}
		catch (Exception exception) {
			if (exception instanceof
					CommerceInventoryWarehouseItemQuantityException ||
				exception instanceof CPInstanceUnitOfMeasureKeyException ||
				exception instanceof
					DuplicateCommerceInventoryWarehouseItemException ||
				exception instanceof MVCCException ||
				exception instanceof NoSuchCPInstanceUnitOfMeasureException ||
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

	private void _addCommerceInventoryWarehouse(ActionRequest actionRequest)
		throws Exception {

		_commerceInventoryWarehouseItemService.
			addCommerceInventoryWarehouseItem(
				StringPool.BLANK,
				ParamUtil.getLong(
					actionRequest, "commerceInventoryWarehouseId"),
				_commerceOrderItemQuantityFormatter.parse(
					actionRequest,
					CommerceInventoryWarehouseItem.class.getName(), "quantity"),
				ParamUtil.getString(actionRequest, "sku"),
				ParamUtil.getString(actionRequest, "unitOfMeasure"));
	}

	private void _deleteCommerceInventoryWarehouse(ActionRequest actionRequest)
		throws PortalException {

		long companyId = _portal.getCompanyId(actionRequest);

		String sku = ParamUtil.getString(actionRequest, "sku");
		String unitOfMeasureKey = ParamUtil.getString(
			actionRequest, "unitOfMeasureKey");

		_commerceInventoryWarehouseItemService.
			deleteCommerceInventoryWarehouseItems(
				companyId, sku, unitOfMeasureKey);

		_commerceInventoryReplenishmentItemService.
			deleteCommerceInventoryReplenishmentItems(
				companyId, sku, unitOfMeasureKey);
	}

	private void _updateCommerceInventoryWarehouse(ActionRequest actionRequest)
		throws Exception {

		long commerceInventoryWarehouseId = ParamUtil.getLong(
			actionRequest, "commerceInventoryWarehouseId");

		String sku = ParamUtil.getString(actionRequest, "sku");
		String unitOfMeasureKey = ParamUtil.getString(
			actionRequest, "unitOfMeasureKey");

		CommerceInventoryWarehouseItem commerceInventoryWarehouseItem =
			_commerceInventoryWarehouseItemService.
				fetchCommerceInventoryWarehouseItem(
					commerceInventoryWarehouseId, sku, unitOfMeasureKey);

		BigDecimal quantity = _commerceOrderItemQuantityFormatter.parse(
			actionRequest, CommerceInventoryWarehouseItem.class.getName(),
			"quantity");

		if (commerceInventoryWarehouseItem == null) {
			_commerceInventoryWarehouseItemService.
				addCommerceInventoryWarehouseItem(
					StringPool.BLANK, commerceInventoryWarehouseId, quantity,
					sku, unitOfMeasureKey);
		}
		else {
			_commerceInventoryWarehouseItemService.
				increaseCommerceInventoryWarehouseItemQuantity(
					commerceInventoryWarehouseItem.
						getCommerceInventoryWarehouseItemId(),
					quantity);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditCommerceInventoryWarehouseMVCActionCommand.class);

	@Reference
	private CommerceInventoryReplenishmentItemService
		_commerceInventoryReplenishmentItemService;

	@Reference
	private CommerceInventoryWarehouseItemService
		_commerceInventoryWarehouseItemService;

	@Reference
	private CommerceOrderItemQuantityFormatter
		_commerceOrderItemQuantityFormatter;

	@Reference
	private Portal _portal;

}