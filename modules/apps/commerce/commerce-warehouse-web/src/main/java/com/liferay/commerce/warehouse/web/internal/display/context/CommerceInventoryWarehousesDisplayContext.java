/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.warehouse.web.internal.display.context;

import com.liferay.commerce.frontend.model.HeaderActionModel;
import com.liferay.commerce.inventory.constants.CommerceInventoryActionKeys;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseService;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.display.context.helper.CPRequestHelper;
import com.liferay.commerce.product.service.CommerceChannelRelService;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.RenderURL;
import jakarta.portlet.WindowStateException;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrea Di Giorgi
 * @author Alessio Antonio Rendina
 * @author Crescenzo Rega
 */
public class CommerceInventoryWarehousesDisplayContext {

	public CommerceInventoryWarehousesDisplayContext(
		CommerceChannelRelService commerceChannelRelService,
		CommerceInventoryWarehouseService commerceInventoryWarehouseService,
		HttpServletRequest httpServletRequest, Portal portal,
		ModelResourcePermission<CommerceInventoryWarehouse>
			commerceInventoryWarehouseModelResourcePermission) {

		this.commerceChannelRelService = commerceChannelRelService;

		_commerceInventoryWarehouseService = commerceInventoryWarehouseService;

		this.httpServletRequest = httpServletRequest;

		cpRequestHelper = new CPRequestHelper(httpServletRequest);

		this.portal = portal;

		_commerceInventoryWarehouseModelResourcePermission =
			commerceInventoryWarehouseModelResourcePermission;
	}

	public String getAddCommerceWarehouseRenderURL() throws Exception {
		return PortletURLBuilder.createRenderURL(
			cpRequestHelper.getLiferayPortletResponse()
		).setMVCRenderCommandName(
			"/commerce_inventory_warehouse/add_commerce_inventory_warehouse"
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	public CommerceInventoryWarehouse getCommerceInventoryWarehouse()
		throws PortalException {

		if (_commerceInventoryWarehouse != null) {
			return _commerceInventoryWarehouse;
		}

		long commerceInventoryWarehouseId = ParamUtil.getLong(
			cpRequestHelper.getRenderRequest(), "commerceInventoryWarehouseId");

		if (commerceInventoryWarehouseId > 0) {
			_commerceInventoryWarehouse =
				_commerceInventoryWarehouseService.
					getCommerceInventoryWarehouse(commerceInventoryWarehouseId);
		}

		return _commerceInventoryWarehouse;
	}

	public long getCommerceInventoryWarehouseId() throws PortalException {
		CommerceInventoryWarehouse commerceInventoryWarehouse =
			getCommerceInventoryWarehouse();

		if (commerceInventoryWarehouse == null) {
			return 0;
		}

		return commerceInventoryWarehouse.getCommerceInventoryWarehouseId();
	}

	public String getCountryTwoLettersIsoCode() {
		return ParamUtil.getString(
			cpRequestHelper.getRenderRequest(), "countryTwoLettersISOCode",
			null);
	}

	public PortletURL getEditCommerceWarehouseRenderURL() {
		return PortletURLBuilder.create(
			portal.getControlPanelPortletURL(
				cpRequestHelper.getRequest(),
				CPPortletKeys.COMMERCE_INVENTORY_WAREHOUSE,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/commerce_inventory_warehouse/edit_commerce_inventory_warehouse"
		).buildPortletURL();
	}

	public List<HeaderActionModel> getHeaderActionModels() throws Exception {
		List<HeaderActionModel> headerActionModels = new ArrayList<>();

		if (!hasPermission()) {
			return headerActionModels;
		}

		LiferayPortletResponse liferayPortletResponse =
			cpRequestHelper.getLiferayPortletResponse();

		RenderResponse renderResponse = cpRequestHelper.getRenderResponse();

		RenderURL cancelURL = renderResponse.createRenderURL();

		HeaderActionModel cancelHeaderActionModel = new HeaderActionModel(
			null, cancelURL.toString(), null, "cancel");

		headerActionModels.add(cancelHeaderActionModel);

		HeaderActionModel saveHeaderActionModel = new HeaderActionModel(
			"btn-primary", liferayPortletResponse.getNamespace() + "fm",
			PortletURLBuilder.createActionURL(
				liferayPortletResponse
			).setActionName(
				"/commerce_inventory_warehouse" +
					"/edit_commerce_inventory_warehouse"
			).buildString(),
			null, "save");

		headerActionModels.add(saveHeaderActionModel);

		return headerActionModels;
	}

	public PortletURL getPortletCommerceInventoryWarehouseURL() {
		LiferayPortletResponse liferayPortletResponse =
			cpRequestHelper.getLiferayPortletResponse();

		PortletURL portletURL = liferayPortletResponse.createRenderURL();

		String redirect = ParamUtil.getString(httpServletRequest, "redirect");

		if (Validator.isNotNull(redirect)) {
			portletURL.setParameter("redirect", redirect);
		}

		long commerceInventoryWarehouseId = ParamUtil.getLong(
			httpServletRequest, "commerceInventoryWarehouseId");

		if (commerceInventoryWarehouseId > 0) {
			portletURL.setParameter(
				"commerceInventoryWarehouseId",
				String.valueOf(commerceInventoryWarehouseId));
		}

		return portletURL;
	}

	public PortletURL getPortletURL() {
		return PortletURLBuilder.createRenderURL(
			cpRequestHelper.getRenderResponse()
		).setKeywords(
			_getKeywords()
		).setNavigation(
			_getNavigation()
		).setParameter(
			"countryTwoLettersISOCode", getCountryTwoLettersIsoCode()
		).setParameter(
			"delta",
			() -> {
				String delta = ParamUtil.getString(
					cpRequestHelper.getRenderRequest(), "delta");

				if (Validator.isNotNull(delta)) {
					return delta;
				}

				return null;
			}
		).buildPortletURL();
	}

	public CreationMenu getWarehouseCreationMenu() throws PortalException {
		CreationMenu creationMenu = new CreationMenu();

		if (hasAddWarehousePermission()) {
			creationMenu.addDropdownItem(
				dropdownItem -> {
					dropdownItem.setHref(getAddCommerceWarehouseRenderURL());
					dropdownItem.setLabel(
						LanguageUtil.get(
							cpRequestHelper.getRequest(), "add-warehouse"));
					dropdownItem.setTarget("modal");
				});
		}

		return creationMenu;
	}

	public List<FDSActionDropdownItem> getWarehouseFDSActionDropdownItems()
		throws PortalException {

		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				PortletURLBuilder.create(
					PortletProviderUtil.getPortletURL(
						cpRequestHelper.getRequest(),
						CommerceInventoryWarehouse.class.getName(),
						PortletProvider.Action.MANAGE)
				).setMVCRenderCommandName(
					"/commerce_inventory_warehouse" +
						"/edit_commerce_inventory_warehouse"
				).setParameter(
					"commerceInventoryWarehouseId", "{id}"
				).setParameter(
					"screenNavigationCategoryKey", "details"
				).buildString(),
				"pencil", "edit",
				LanguageUtil.get(cpRequestHelper.getRequest(), "edit"), "get",
				null, null),
			new FDSActionDropdownItem(
				null, "trash", "delete",
				LanguageUtil.get(cpRequestHelper.getRequest(), "delete"),
				"delete", "delete", "headless"),
			new FDSActionDropdownItem(
				_getManageWarehousePermissionsURL(), null, "permissions",
				LanguageUtil.get(cpRequestHelper.getRequest(), "permissions"),
				"get", "permissions", "modal-permissions"));
	}

	public boolean hasAddWarehousePermission() {
		PortletResourcePermission portletResourcePermission =
			_commerceInventoryWarehouseModelResourcePermission.
				getPortletResourcePermission();

		if (portletResourcePermission.contains(
				PermissionThreadLocal.getPermissionChecker(), null,
				CommerceInventoryActionKeys.MANAGE_INVENTORY)) {

			return true;
		}

		return portletResourcePermission.contains(
			cpRequestHelper.getPermissionChecker(), null,
			CommerceInventoryActionKeys.ADD_WAREHOUSE);
	}

	public boolean hasManageCommerceInventoryWarehousePermission() {
		return true;
	}

	public boolean hasPermission() throws PortalException {
		CommerceInventoryWarehouse commerceInventoryWarehouse =
			getCommerceInventoryWarehouse();

		return _commerceInventoryWarehouseModelResourcePermission.contains(
			cpRequestHelper.getPermissionChecker(),
			commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
			ActionKeys.UPDATE);
	}

	protected CommerceChannelRelService commerceChannelRelService;
	protected final CPRequestHelper cpRequestHelper;
	protected HttpServletRequest httpServletRequest;
	protected Portal portal;

	private String _getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(
			cpRequestHelper.getRenderRequest(), "keywords");

		return _keywords;
	}

	private String _getManageWarehousePermissionsURL() throws PortalException {
		PortletURL portletURL = PortletURLBuilder.create(
			portal.getControlPanelPortletURL(
				cpRequestHelper.getRequest(),
				"com_liferay_portlet_configuration_web_portlet_" +
					"PortletConfigurationPortlet",
				ActionRequest.RENDER_PHASE)
		).setMVCPath(
			"/edit_permissions.jsp"
		).setRedirect(
			cpRequestHelper.getCurrentURL()
		).setParameter(
			"modelResource", CommerceInventoryWarehouse.class.getName()
		).setParameter(
			"modelResourceDescription", "{name}"
		).setParameter(
			"resourcePrimKey", "{id}"
		).buildPortletURL();

		try {
			portletURL.setWindowState(LiferayWindowState.POP_UP);
		}
		catch (WindowStateException windowStateException) {
			throw new PortalException(windowStateException);
		}

		return portletURL.toString();
	}

	private String _getNavigation() {
		return ParamUtil.getString(
			cpRequestHelper.getRenderRequest(), "navigation");
	}

	private CommerceInventoryWarehouse _commerceInventoryWarehouse;
	private final ModelResourcePermission<CommerceInventoryWarehouse>
		_commerceInventoryWarehouseModelResourcePermission;
	private final CommerceInventoryWarehouseService
		_commerceInventoryWarehouseService;
	private String _keywords;

}