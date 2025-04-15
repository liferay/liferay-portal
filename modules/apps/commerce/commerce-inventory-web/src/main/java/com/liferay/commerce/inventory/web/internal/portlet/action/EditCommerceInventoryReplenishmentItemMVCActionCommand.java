/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.web.internal.portlet.action;

import com.liferay.commerce.inventory.exception.CommerceInventoryReplenishmentQuantityException;
import com.liferay.commerce.inventory.exception.MVCCException;
import com.liferay.commerce.inventory.model.CommerceInventoryReplenishmentItem;
import com.liferay.commerce.inventory.service.CommerceInventoryReplenishmentItemService;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.util.CommerceOrderItemQuantityFormatter;
import com.liferay.portal.kernel.exception.PortalException;
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

import java.util.Calendar;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.COMMERCE_INVENTORY,
		"mvc.command.name=/commerce_inventory/edit_commerce_inventory_replenishment_item"
	},
	service = MVCActionCommand.class
)
public class EditCommerceInventoryReplenishmentItemMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.ADD)) {
				_addCommerceInventoryReplenishmentItem(actionRequest);
			}
			else if (cmd.equals(Constants.DELETE)) {
				_deleteCommerceInventoryReplenishmentItem(actionRequest);
			}
			else if (cmd.equals(Constants.UPDATE)) {
				_updateCommerceInventoryReplenishmentItem(actionRequest);
			}
		}
		catch (Exception exception) {
			if (exception instanceof
					CommerceInventoryReplenishmentQuantityException ||
				exception instanceof MVCCException ||
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

	private void _addCommerceInventoryReplenishmentItem(
			ActionRequest actionRequest)
		throws Exception {

		long commerceInventoryWarehouseId = ParamUtil.getLong(
			actionRequest, "commerceInventoryWarehouseId");

		int day = ParamUtil.getInteger(actionRequest, "dateDay");
		int month = ParamUtil.getInteger(actionRequest, "dateMonth");
		int year = ParamUtil.getInteger(actionRequest, "dateYear");

		Calendar calendar = Calendar.getInstance();

		calendar.set(year, month, day);

		_commerceInventoryReplenishmentItemService.
			addCommerceInventoryReplenishmentItem(
				null, commerceInventoryWarehouseId, calendar.getTime(),
				_commerceOrderItemQuantityFormatter.parse(
					actionRequest,
					CommerceInventoryReplenishmentItem.class.getName(),
					"quantity"),
				ParamUtil.getString(actionRequest, "sku"),
				ParamUtil.getString(actionRequest, "unitOfMeasureKey"));
	}

	private void _deleteCommerceInventoryReplenishmentItem(
			ActionRequest actionRequest)
		throws PortalException {

		long commerceInventoryReplenishmentItemId = ParamUtil.getLong(
			actionRequest, "commerceInventoryReplenishmentItemId");

		_commerceInventoryReplenishmentItemService.
			deleteCommerceInventoryReplenishmentItem(
				commerceInventoryReplenishmentItemId);
	}

	private void _updateCommerceInventoryReplenishmentItem(
			ActionRequest actionRequest)
		throws Exception {

		long commerceInventoryReplenishmentItemId = ParamUtil.getLong(
			actionRequest, "commerceInventoryReplenishmentItemId");

		CommerceInventoryReplenishmentItem commerceInventoryReplenishmentItem =
			_commerceInventoryReplenishmentItemService.
				getCommerceInventoryReplenishmentItem(
					commerceInventoryReplenishmentItemId);

		int day = ParamUtil.getInteger(actionRequest, "dateDay");
		int month = ParamUtil.getInteger(actionRequest, "dateMonth");
		int year = ParamUtil.getInteger(actionRequest, "dateYear");

		Calendar calendar = Calendar.getInstance();

		calendar.set(year, month, day);

		long mvccVersion = ParamUtil.getLong(actionRequest, "mvccVersion");

		_commerceInventoryReplenishmentItemService.
			updateCommerceInventoryReplenishmentItem(
				commerceInventoryReplenishmentItem.getExternalReferenceCode(),
				commerceInventoryReplenishmentItemId, calendar.getTime(),
				_commerceOrderItemQuantityFormatter.parse(
					actionRequest,
					CommerceInventoryReplenishmentItem.class.getName(),
					"quantity"),
				mvccVersion);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditCommerceInventoryReplenishmentItemMVCActionCommand.class);

	@Reference
	private CommerceInventoryReplenishmentItemService
		_commerceInventoryReplenishmentItemService;

	@Reference
	private CommerceOrderItemQuantityFormatter
		_commerceOrderItemQuantityFormatter;

}