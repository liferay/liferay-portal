/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.web.internal.frontend.data.set.provider;

import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.inventory.web.internal.constants.CommerceInventoryFDSNames;
import com.liferay.commerce.inventory.web.internal.model.Warehouse;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.frontend.data.set.provider.FDSActionProvider;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowStateException;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "fds.data.provider.key=" + CommerceInventoryFDSNames.INVENTORY_WAREHOUSES,
	service = FDSActionProvider.class
)
public class CommerceInventoryWarehouseFDSActionProvider
	implements FDSActionProvider {

	@Override
	public List<DropdownItem> getDropdownItems(
			long groupId, HttpServletRequest httpServletRequest, Object model)
		throws PortalException {

		Warehouse warehouse = (Warehouse)model;

		return DropdownItemListBuilder.add(
			() -> _hasPermission(
				warehouse.getCommerceInventoryWarehouseId(), ActionKeys.UPDATE),
			dropdownItem -> {
				dropdownItem.setHref(
					_getWarehouseEditURL(
						warehouse.getCommerceInventoryWarehouseItemId(),
						httpServletRequest));
				dropdownItem.setLabel(
					_language.get(httpServletRequest, "edit"));
				dropdownItem.setTarget("sidePanel");
			}
		).add(
			() -> _hasPermission(
				warehouse.getCommerceInventoryWarehouseId(), ActionKeys.DELETE),
			dropdownItem -> {
				dropdownItem.setHref(
					_getWarehouseDeleteURL(
						warehouse.getCommerceInventoryWarehouseItemId(),
						httpServletRequest));
				dropdownItem.setLabel(
					_language.get(httpServletRequest, "delete"));
			}
		).build();
	}

	private String _getWarehouseDeleteURL(
		long commerceInventoryWarehouseItemId,
		HttpServletRequest httpServletRequest) {

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				_portal.getOriginalServletRequest(httpServletRequest),
				CPPortletKeys.COMMERCE_INVENTORY, PortletRequest.ACTION_PHASE)
		).setActionName(
			"/commerce_inventory/edit_commerce_inventory_warehouse_item"
		).setCMD(
			Constants.DELETE
		).setRedirect(
			ParamUtil.getString(
				httpServletRequest, "currentUrl",
				_portal.getCurrentURL(httpServletRequest))
		).setParameter(
			"commerceInventoryWarehouseItemId", commerceInventoryWarehouseItemId
		).buildString();
	}

	private String _getWarehouseEditURL(
		long commerceInventoryWarehouseItemId,
		HttpServletRequest httpServletRequest) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		PortletURL portletURL = PortletURLBuilder.create(
			PortletURLFactoryUtil.create(
				themeDisplay.getRequest(), portletDisplay.getId(),
				themeDisplay.getPlid(), PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/commerce_inventory/edit_commerce_inventory_warehouse_item"
		).setRedirect(
			themeDisplay.getURLCurrent()
		).setParameter(
			"commerceInventoryWarehouseItemId", commerceInventoryWarehouseItemId
		).buildPortletURL();

		try {
			portletURL.setWindowState(LiferayWindowState.POP_UP);
		}
		catch (WindowStateException windowStateException) {
			_log.error(windowStateException);
		}

		return portletURL.toString();
	}

	private boolean _hasPermission(
			long commerceInventoryWarehouseId, String actionId)
		throws PortalException {

		return _commerceInventoryWarehouseModelResourcePermission.contains(
			PermissionThreadLocal.getPermissionChecker(),
			commerceInventoryWarehouseId, actionId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceInventoryWarehouseFDSActionProvider.class);

	@Reference(
		target = "(model.class.name=com.liferay.commerce.inventory.model.CommerceInventoryWarehouse)"
	)
	private ModelResourcePermission<CommerceInventoryWarehouse>
		_commerceInventoryWarehouseModelResourcePermission;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}