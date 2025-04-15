/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipment.web.internal.portlet.action;

import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.exception.CommerceShipmentItemQuantityException;
import com.liferay.commerce.exception.DuplicateCommerceShipmentItemException;
import com.liferay.commerce.exception.DuplicateCommerceShipmentItemExternalReferenceCodeException;
import com.liferay.commerce.exception.NoSuchShipmentException;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseLocalService;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceShipment;
import com.liferay.commerce.model.CommerceShipmentItem;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceOrderItemService;
import com.liferay.commerce.service.CommerceShipmentItemService;
import com.liferay.commerce.util.CommerceOrderItemQuantityFormatter;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowStateException;

import java.math.BigDecimal;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 * @author Alec Sloan
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_SHIPMENT,
		"mvc.command.name=/commerce_shipment/edit_commerce_shipment_item"
	},
	service = MVCActionCommand.class
)
public class EditCommerceShipmentItemMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.DELETE)) {
				_deleteCommerceShipmentItems(actionRequest);
			}
			else if (cmd.equals(Constants.UPDATE)) {
				try {
					_updateCommerceShipmentItem(actionRequest);
				}
				catch (Exception exception) {
					if (exception instanceof
							DuplicateCommerceShipmentItemExternalReferenceCodeException ||
						exception instanceof PrincipalException) {

						SessionErrors.add(actionRequest, exception.getClass());

						actionResponse.setRenderParameter(
							"mvcPath", "/error.jsp");
					}
					else if (exception instanceof
								CommerceShipmentItemQuantityException ||
							 exception instanceof
								 DuplicateCommerceShipmentItemException ||
							 exception instanceof NoSuchShipmentException) {

						hideDefaultErrorMessage(actionRequest);
						hideDefaultSuccessMessage(actionRequest);

						SessionErrors.add(actionRequest, exception.getClass());

						String redirect = ParamUtil.getString(
							actionRequest, "redirect");

						sendRedirect(actionRequest, actionResponse, redirect);
					}
					else {
						_log.error(exception);

						String redirect = ParamUtil.getString(
							actionRequest, "redirect");

						sendRedirect(actionRequest, actionResponse, redirect);
					}
				}
			}
		}
		catch (Exception exception) {
			SessionErrors.add(actionRequest, exception.getClass());

			String redirect = _getSaveAndContinueRedirect(actionRequest);

			sendRedirect(actionRequest, actionResponse, redirect);
		}
	}

	private void _deleteCommerceShipmentItems(ActionRequest actionRequest)
		throws PortalException {

		long[] deleteCommerceShipmentItemIds = null;

		long commerceShipmentItemId = ParamUtil.getLong(
			actionRequest, "commerceShipmentItemId");

		if (commerceShipmentItemId > 0) {
			deleteCommerceShipmentItemIds = new long[] {commerceShipmentItemId};
		}
		else {
			deleteCommerceShipmentItemIds = StringUtil.split(
				ParamUtil.getString(
					actionRequest, "deleteCommerceShipmentItemIds"),
				0L);
		}

		boolean restoreStockQuantity = ParamUtil.getBoolean(
			actionRequest, "restoreStockQuantity");

		for (long deleteCommerceShipmentItemId :
				deleteCommerceShipmentItemIds) {

			_commerceShipmentItemService.deleteCommerceShipmentItem(
				deleteCommerceShipmentItemId, restoreStockQuantity);
		}
	}

	private String _getSaveAndContinueRedirect(ActionRequest actionRequest)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PortletURL portletURL = PortletURLBuilder.create(
			PortletProviderUtil.getPortletURL(
				actionRequest, themeDisplay.getScopeGroup(),
				CommerceShipment.class.getName(), PortletProvider.Action.EDIT)
		).setMVCRenderCommandName(
			"/commerce_shipment/edit_commerce_shipment_item"
		).setParameter(
			"commerceOrderItemId",
			() -> {
				long commerceOrderItemId = ParamUtil.getLong(
					actionRequest, "commerceOrderItemId");

				if (commerceOrderItemId > 0) {
					return commerceOrderItemId;
				}

				return null;
			}
		).setParameter(
			"commerceShipmentId",
			() -> {
				long commerceShipmentId = ParamUtil.getLong(
					actionRequest, "commerceShipmentId");

				if (commerceShipmentId > 0) {
					return commerceShipmentId;
				}

				return null;
			}
		).setParameter(
			"commerceShipmentItemId",
			() -> {
				long commerceShipmentItemId = ParamUtil.getLong(
					actionRequest, "commerceShipmentItemId");

				if (commerceShipmentItemId > 0) {
					return commerceShipmentItemId;
				}

				return null;
			}
		).buildPortletURL();

		try {
			portletURL.setWindowState(LiferayWindowState.POP_UP);
		}
		catch (WindowStateException windowStateException) {
			_log.error(windowStateException);
		}

		return portletURL.toString();
	}

	private CommerceShipmentItem _updateCommerceShipmentItem(
			ActionRequest actionRequest)
		throws Exception {

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			CommerceShipmentItem.class.getName(), actionRequest);

		long commerceShipmentId = ParamUtil.getLong(
			actionRequest, "commerceShipmentId");

		long commerceOrderItemId = ParamUtil.getLong(
			actionRequest, "commerceOrderItemId");

		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemService.getCommerceOrderItem(commerceOrderItemId);

		CommerceOrder commerceOrder = commerceOrderItem.getCommerceOrder();

		CommerceShipmentItem initialCommerceShipmentItem =
			_commerceShipmentItemService.fetchCommerceShipmentItem(
				commerceShipmentId, commerceOrderItemId, 0);

		CommerceShipmentItem commerceShipmentItem = null;

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.fetchCommerceChannelByGroupClassPK(
				commerceOrderItem.getGroupId());

		_commerceChannelModelResourcePermission.check(
			PermissionThreadLocal.getPermissionChecker(),
			commerceChannel.getCommerceChannelId(), ActionKeys.VIEW);

		List<CommerceInventoryWarehouse> commerceInventoryWarehouses =
			_commerceInventoryWarehouseLocalService.
				getCommerceInventoryWarehouses(
					commerceOrderItem.getCompanyId(),
					commerceOrder.getCommerceAccountId(),
					commerceOrderItem.getGroupId(), true);

		for (CommerceInventoryWarehouse commerceInventoryWarehouse :
				commerceInventoryWarehouses) {

			long commerceInventoryWarehouseId =
				commerceInventoryWarehouse.getCommerceInventoryWarehouseId();

			commerceShipmentItem =
				_commerceShipmentItemService.fetchCommerceShipmentItem(
					commerceShipmentId, commerceOrderItemId,
					commerceInventoryWarehouseId);

			BigDecimal quantity = _commerceOrderItemQuantityFormatter.parse(
				actionRequest, CommerceShipmentItem.class.getName(),
				commerceInventoryWarehouseId + "_quantity");

			if ((initialCommerceShipmentItem != null) &&
				BigDecimalUtil.gt(quantity, BigDecimal.ZERO)) {

				commerceShipmentItem =
					_commerceShipmentItemService.updateCommerceShipmentItem(
						initialCommerceShipmentItem.getCommerceShipmentItemId(),
						commerceInventoryWarehouseId, quantity, true);

				initialCommerceShipmentItem = null;
			}
			else if ((commerceShipmentItem == null) &&
					 BigDecimalUtil.gt(quantity, BigDecimal.ZERO)) {

				commerceShipmentItem =
					_commerceShipmentItemService.addCommerceShipmentItem(
						null, commerceShipmentId, commerceOrderItemId,
						commerceInventoryWarehouseId, quantity, null, true,
						serviceContext);
			}
			else if ((commerceShipmentItem != null) &&
					 (quantity != commerceShipmentItem.getQuantity())) {

				commerceShipmentItem =
					_commerceShipmentItemService.updateCommerceShipmentItem(
						commerceShipmentItem.getCommerceShipmentItemId(),
						commerceInventoryWarehouseId, quantity, true);

				if (BigDecimalUtil.eq(quantity, BigDecimal.ZERO)) {
					commerceShipmentItem =
						_commerceShipmentItemService.updateCommerceShipmentItem(
							commerceShipmentItem.getCommerceShipmentItemId(), 0,
							quantity, true);
				}
			}
		}

		long commerceShipmentItemId = ParamUtil.getLong(
			actionRequest, "commerceShipmentItemId");

		String externalReferenceCode = ParamUtil.getString(
			actionRequest, "externalReferenceCode");

		return _commerceShipmentItemService.updateExternalReferenceCode(
			commerceShipmentItemId, externalReferenceCode);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditCommerceShipmentItemMVCActionCommand.class);

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.product.model.CommerceChannel)"
	)
	private ModelResourcePermission<CommerceChannel>
		_commerceChannelModelResourcePermission;

	@Reference
	private CommerceInventoryWarehouseLocalService
		_commerceInventoryWarehouseLocalService;

	@Reference
	private CommerceOrderItemQuantityFormatter
		_commerceOrderItemQuantityFormatter;

	@Reference
	private CommerceOrderItemService _commerceOrderItemService;

	@Reference
	private CommerceShipmentItemService _commerceShipmentItemService;

}