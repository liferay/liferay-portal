/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.web.internal.frontend.data.set.provider;

import com.liferay.commerce.inventory.constants.CommerceInventoryActionKeys;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.inventory.web.internal.constants.CommerceInventoryFDSNames;
import com.liferay.commerce.inventory.web.internal.model.InventoryItem;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.frontend.data.set.provider.FDSActionProvider;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "fds.data.provider.key=" + CommerceInventoryFDSNames.INVENTORY_ITEMS,
	service = FDSActionProvider.class
)
public class CommerceInventoryItemFDSActionProvider
	implements FDSActionProvider {

	@Override
	public List<DropdownItem> getDropdownItems(
			long groupId, HttpServletRequest httpServletRequest, Object model)
		throws PortalException {

		InventoryItem inventoryItem = (InventoryItem)model;

		return DropdownItemListBuilder.add(
			dropdownItem -> {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				dropdownItem.setHref(
					_getCommerceInventoryItemEditURL(
						inventoryItem.getSku(),
						inventoryItem.getUnitOfMeasureKey(), themeDisplay));

				dropdownItem.setLabel(
					_language.get(httpServletRequest, "edit"));
			}
		).add(
			() -> _hasPermission(),
			dropdownItem -> {
				dropdownItem.setHref(
					_getInventoryItemDeleteURL(
						inventoryItem.getSku(), httpServletRequest));
				dropdownItem.setLabel(
					_language.get(httpServletRequest, "delete"));
			}
		).build();
	}

	private String _getCommerceInventoryItemEditURL(
		String sku, String unitOfMeasureKey, ThemeDisplay themeDisplay) {

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		return PortletURLBuilder.create(
			PortletURLFactoryUtil.create(
				themeDisplay.getRequest(), portletDisplay.getId(),
				themeDisplay.getPlid(), PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/commerce_inventory/edit_commerce_inventory_item"
		).setParameter(
			"sku", sku
		).setParameter(
			"unitOfMeasureKey", unitOfMeasureKey
		).buildString();
	}

	private String _getInventoryItemDeleteURL(
		String sku, HttpServletRequest httpServletRequest) {

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				_portal.getOriginalServletRequest(httpServletRequest),
				CPPortletKeys.COMMERCE_INVENTORY, PortletRequest.ACTION_PHASE)
		).setActionName(
			"/commerce_inventory/edit_commerce_inventory_warehouse"
		).setCMD(
			Constants.DELETE
		).setRedirect(
			ParamUtil.getString(
				httpServletRequest, "currentUrl",
				_portal.getCurrentURL(httpServletRequest))
		).setParameter(
			"sku", sku
		).buildString();
	}

	private boolean _hasPermission() {
		PortletResourcePermission portletResourcePermission =
			_commerceInventoryWarehouseModelResourcePermission.
				getPortletResourcePermission();

		return portletResourcePermission.contains(
			PermissionThreadLocal.getPermissionChecker(), null,
			CommerceInventoryActionKeys.MANAGE_INVENTORY);
	}

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