/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.warehouse.web.internal.portlet.action;

import com.liferay.commerce.inventory.exception.DuplicateCommerceInventoryWarehouseExternalReferenceCodeException;
import com.liferay.commerce.inventory.exception.NoSuchInventoryWarehouseException;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseService;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Crescenzo Rega
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.COMMERCE_INVENTORY_WAREHOUSE,
		"mvc.command.name=/commerce_inventory_warehouse/edit_commerce_inventory_warehouse_external_reference_code"
	},
	service = MVCActionCommand.class
)
public class EditCommerceInventoryWarehouseExternalReferenceCodeMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			long commerceInventoryWarehouseId = ParamUtil.getLong(
				actionRequest, "commerceInventoryWarehouseId");

			CommerceInventoryWarehouse commerceInventoryWarehouse =
				_commerceInventoryWarehouseService.
					getCommerceInventoryWarehouse(commerceInventoryWarehouseId);

			String externalReferenceCode = ParamUtil.getString(
				actionRequest, "externalReferenceCode");

			_commerceInventoryWarehouseService.
				updateCommerceInventoryWarehouseExternalReferenceCode(
					externalReferenceCode,
					commerceInventoryWarehouse.
						getCommerceInventoryWarehouseId());
		}
		catch (Exception exception) {
			if (exception instanceof
					DuplicateCommerceInventoryWarehouseExternalReferenceCodeException ||
				exception instanceof NoSuchInventoryWarehouseException) {

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
			else {
				_log.error(exception);

				String redirect = ParamUtil.getString(
					actionRequest, "redirect");

				sendRedirect(actionRequest, actionResponse, redirect);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditCommerceInventoryWarehouseExternalReferenceCodeMVCActionCommand.
			class);

	@Reference
	private CommerceInventoryWarehouseService
		_commerceInventoryWarehouseService;

}