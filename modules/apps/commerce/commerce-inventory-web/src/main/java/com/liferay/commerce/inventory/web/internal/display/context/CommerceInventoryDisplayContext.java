/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.web.internal.display.context;

import com.liferay.commerce.frontend.model.HeaderActionModel;
import com.liferay.commerce.inventory.constants.CommerceInventoryActionKeys;
import com.liferay.commerce.inventory.model.CommerceInventoryReplenishmentItem;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouseItem;
import com.liferay.commerce.inventory.service.CommerceInventoryReplenishmentItemService;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseItemService;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseService;
import com.liferay.commerce.product.display.context.helper.CPRequestHelper;
import com.liferay.commerce.util.CommerceQuantityFormatter;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.RenderURL;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Luca Pellizzon
 * @author Alessio Antonio Rendina
 */
public class CommerceInventoryDisplayContext {

	public CommerceInventoryDisplayContext(
		CommerceInventoryReplenishmentItemService
			commerceInventoryReplenishmentItemService,
		CommerceInventoryWarehouseItemService
			commerceInventoryWarehouseItemService,
		ModelResourcePermission<CommerceInventoryWarehouse>
			commerceInventoryWarehouseModelResourcePermission,
		CommerceInventoryWarehouseService commerceInventoryWarehouseService,
		CommerceQuantityFormatter commerceQuantityFormatter,
		HttpServletRequest httpServletRequest) {

		_commerceInventoryReplenishmentItemService =
			commerceInventoryReplenishmentItemService;
		_commerceInventoryWarehouseItemService =
			commerceInventoryWarehouseItemService;
		_commerceInventoryWarehouseModelResourcePermission =
			commerceInventoryWarehouseModelResourcePermission;
		_commerceInventoryWarehouseService = commerceInventoryWarehouseService;
		_commerceQuantityFormatter = commerceQuantityFormatter;

		_cpRequestHelper = new CPRequestHelper(httpServletRequest);

		_sku = ParamUtil.getString(httpServletRequest, "sku");
		_unitOfMeasureKey = ParamUtil.getString(
			httpServletRequest, "unitOfMeasureKey");
	}

	public String getAddQuantityActionURL() throws Exception {
		return PortletURLBuilder.createRenderURL(
			_cpRequestHelper.getLiferayPortletResponse()
		).setMVCRenderCommandName(
			"/commerce_inventory/edit_commerce_inventory_warehouse"
		).setParameter(
			"sku", _sku
		).setParameter(
			"unitOfMeasureKey", _unitOfMeasureKey
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	public CommerceInventoryReplenishmentItem
			getCommerceInventoryReplenishmentItem()
		throws PortalException {

		long commerceInventoryReplenishmentItemId = ParamUtil.getLong(
			_cpRequestHelper.getRequest(),
			"commerceInventoryReplenishmentItemId");

		if (commerceInventoryReplenishmentItemId <= 0) {
			return null;
		}

		return _commerceInventoryReplenishmentItemService.
			getCommerceInventoryReplenishmentItem(
				commerceInventoryReplenishmentItemId);
	}

	public long getCommerceInventoryReplenishmentItemId()
		throws PortalException {

		CommerceInventoryReplenishmentItem commerceInventoryReplenishmentItem =
			getCommerceInventoryReplenishmentItem();

		if (commerceInventoryReplenishmentItem == null) {
			return 0;
		}

		return commerceInventoryReplenishmentItem.
			getCommerceInventoryReplenishmentItemId();
	}

	public CommerceInventoryWarehouseItem getCommerceInventoryWarehouseItem()
		throws PortalException {

		long commerceInventoryWarehouseItemId = ParamUtil.getLong(
			_cpRequestHelper.getRequest(), "commerceInventoryWarehouseItemId");

		if (commerceInventoryWarehouseItemId <= 0) {
			return null;
		}

		return _commerceInventoryWarehouseItemService.
			getCommerceInventoryWarehouseItem(commerceInventoryWarehouseItemId);
	}

	public long getCommerceInventoryWarehouseItemId() throws PortalException {
		CommerceInventoryWarehouseItem commerceInventoryWarehouseItem =
			getCommerceInventoryWarehouseItem();

		if (commerceInventoryWarehouseItem == null) {
			return 0;
		}

		return commerceInventoryWarehouseItem.
			getCommerceInventoryWarehouseItemId();
	}

	public List<CommerceInventoryWarehouse> getCommerceInventoryWarehouses()
		throws PrincipalException {

		return _commerceInventoryWarehouseService.
			getCommerceInventoryWarehouses(
				_cpRequestHelper.getCompanyId(), true, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);
	}

	public String getCreateInventoryItemActionURL() throws Exception {
		return PortletURLBuilder.createRenderURL(
			_cpRequestHelper.getLiferayPortletResponse()
		).setMVCRenderCommandName(
			"/commerce_inventory/add_commerce_inventory_item"
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	public String getCreateReplenishmentActionURL() throws Exception {
		return PortletURLBuilder.createRenderURL(
			_cpRequestHelper.getLiferayPortletResponse()
		).setMVCRenderCommandName(
			"/commerce_inventory/edit_commerce_inventory_replenishment_item"
		).setParameter(
			"sku", _sku
		).setParameter(
			"unitOfMeasureKey", _unitOfMeasureKey
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	public String getFormattedQuantity(BigDecimal quantity)
		throws PortalException {

		CommerceInventoryWarehouseItem commerceInventoryWarehouseItem =
			getCommerceInventoryWarehouseItem();

		if (commerceInventoryWarehouseItem == null) {
			return StringPool.BLANK;
		}

		BigDecimal formattedQuantity = _commerceQuantityFormatter.format(
			_cpRequestHelper.getCompanyId(), quantity,
			commerceInventoryWarehouseItem.getSku(),
			commerceInventoryWarehouseItem.getUnitOfMeasureKey());

		return formattedQuantity.toString();
	}

	public List<HeaderActionModel> getHeaderActionModels() {
		List<HeaderActionModel> headerActionModels = new ArrayList<>();

		if (_sku == null) {
			return headerActionModels;
		}

		RenderResponse renderResponse = _cpRequestHelper.getRenderResponse();

		RenderURL cancelURL = renderResponse.createRenderURL();

		headerActionModels.add(
			new HeaderActionModel(null, cancelURL.toString(), null, "cancel"));

		return headerActionModels;
	}

	public CreationMenu getInventoryItemCreationMenu() throws Exception {
		CreationMenu creationMenu = new CreationMenu();

		if (_hasPermission()) {
			creationMenu.addDropdownItem(
				dropdownItem -> {
					dropdownItem.setHref(getCreateInventoryItemActionURL());
					dropdownItem.setLabel(
						LanguageUtil.get(
							_cpRequestHelper.getRequest(),
							"add-inventory-item"));
					dropdownItem.setTarget("modal-lg");
				});
		}

		return creationMenu;
	}

	public PortletURL getPortletURL() {
		LiferayPortletResponse liferayPortletResponse =
			_cpRequestHelper.getLiferayPortletResponse();

		PortletURL portletURL = liferayPortletResponse.createRenderURL();

		String redirect = ParamUtil.getString(
			_cpRequestHelper.getRequest(), "redirect");

		if (Validator.isNotNull(redirect)) {
			portletURL.setParameter("redirect", redirect);
		}

		if (_sku != null) {
			portletURL.setParameter("sku", _sku);
		}

		return portletURL;
	}

	public String getQuantity(BigDecimal quantity) throws Exception {
		return _commerceQuantityFormatter.format(
			quantity, _cpRequestHelper.getLocale());
	}

	public CreationMenu getReplenishmentCreationMenu() throws Exception {
		CreationMenu creationMenu = new CreationMenu();

		if (_hasPermission()) {
			creationMenu.addDropdownItem(
				dropdownItem -> {
					dropdownItem.setHref(getCreateReplenishmentActionURL());
					dropdownItem.setLabel(
						LanguageUtil.get(
							_cpRequestHelper.getRequest(), "add-income"));
					dropdownItem.setTarget("modal-lg");
				});
		}

		return creationMenu;
	}

	public String getSku() {
		return _sku;
	}

	public String getTitle() {
		StringBundler sb = new StringBundler(getSku());

		if (Validator.isNotNull(getUnitOfMeasureKey())) {
			sb.append(
				StringPool.SPACE
			).append(
				getUnitOfMeasureKey()
			);
		}

		return sb.toString();
	}

	public String getTransferQuantitiesActionURL() throws Exception {
		return PortletURLBuilder.createRenderURL(
			_cpRequestHelper.getLiferayPortletResponse()
		).setMVCRenderCommandName(
			"/commerce_inventory/transfer_quantities"
		).setParameter(
			"sku", _sku
		).setParameter(
			"unitOfMeasureKey", _unitOfMeasureKey
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	public PortletURL getTransitionInventoryPortletURL() {
		return PortletURLBuilder.createActionURL(
			_cpRequestHelper.getLiferayPortletResponse()
		).setActionName(
			"/commerce_inventory/edit_commerce_inventory_item"
		).setCMD(
			"transition"
		).setRedirect(
			_cpRequestHelper.getCurrentURL()
		).setParameter(
			"sku", _sku
		).buildPortletURL();
	}

	public String getUnitOfMeasureKey() {
		return _unitOfMeasureKey;
	}

	public CreationMenu getWarehousesCreationMenu() throws Exception {
		CreationMenu creationMenu = new CreationMenu();

		if (_hasPermission()) {
			creationMenu.addDropdownItem(
				dropdownItem -> {
					dropdownItem.setHref(getAddQuantityActionURL());
					dropdownItem.setLabel(
						LanguageUtil.get(
							_cpRequestHelper.getRequest(), "add-inventory"));
					dropdownItem.setTarget("modal-lg");
				});

			creationMenu.addDropdownItem(
				dropdownItem -> {
					dropdownItem.setHref(getTransferQuantitiesActionURL());
					dropdownItem.setLabel(
						LanguageUtil.get(
							_cpRequestHelper.getRequest(),
							"create-a-transfer"));
					dropdownItem.setTarget("modal-lg");
				});
		}

		return creationMenu;
	}

	private boolean _hasPermission() {
		PortletResourcePermission portletResourcePermission =
			_commerceInventoryWarehouseModelResourcePermission.
				getPortletResourcePermission();

		if (portletResourcePermission.contains(
				PermissionThreadLocal.getPermissionChecker(), null,
				CommerceInventoryActionKeys.MANAGE_INVENTORY)) {

			return true;
		}

		return portletResourcePermission.contains(
			PermissionThreadLocal.getPermissionChecker(), null,
			CommerceInventoryActionKeys.ADD_WAREHOUSE);
	}

	private final CommerceInventoryReplenishmentItemService
		_commerceInventoryReplenishmentItemService;
	private final CommerceInventoryWarehouseItemService
		_commerceInventoryWarehouseItemService;
	private final ModelResourcePermission<CommerceInventoryWarehouse>
		_commerceInventoryWarehouseModelResourcePermission;
	private final CommerceInventoryWarehouseService
		_commerceInventoryWarehouseService;
	private final CommerceQuantityFormatter _commerceQuantityFormatter;
	private final CPRequestHelper _cpRequestHelper;
	private String _sku;
	private final String _unitOfMeasureKey;

}