/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipping.engine.fixed.web.internal.display.context;

import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseService;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.product.model.CPMeasurementUnit;
import com.liferay.commerce.product.service.CPMeasurementUnitLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceShippingMethodService;
import com.liferay.commerce.shipping.engine.fixed.constants.CommerceShippingEngineFixedWebKeys;
import com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOption;
import com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOptionRel;
import com.liferay.commerce.shipping.engine.fixed.service.CommerceShippingFixedOptionRelService;
import com.liferay.commerce.shipping.engine.fixed.service.CommerceShippingFixedOptionService;
import com.liferay.commerce.shipping.engine.fixed.web.internal.constants.CommerceShippingFixedOptionScreenNavigationConstants;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.CountryService;
import com.liferay.portal.kernel.service.RegionService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.List;

/**
 * @author Alessio Antonio Rendina
 */
public class CommerceShippingFixedOptionRelsDisplayContext
	extends BaseCommerceShippingFixedOptionDisplayContext {

	public CommerceShippingFixedOptionRelsDisplayContext(
		CommerceChannelLocalService commerceChannelLocalService,
		CommerceCurrencyLocalService commerceCurrencyLocalService,
		CommerceInventoryWarehouseService commerceInventoryWarehouseService,
		CommerceShippingFixedOptionRelService
			commerceShippingFixedOptionRelService,
		CommerceShippingFixedOptionService commerceShippingFixedOptionService,
		CommerceShippingMethodService commerceShippingMethodService,
		CountryService countryService,
		CPMeasurementUnitLocalService cpMeasurementUnitLocalService,
		Portal portal, RegionService regionService, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		super(
			commerceChannelLocalService, commerceCurrencyLocalService,
			commerceShippingMethodService, renderRequest, renderResponse);

		_commerceInventoryWarehouseService = commerceInventoryWarehouseService;
		_commerceShippingFixedOptionRelService =
			commerceShippingFixedOptionRelService;
		_commerceShippingFixedOptionService =
			commerceShippingFixedOptionService;
		_countryService = countryService;
		_cpMeasurementUnitLocalService = cpMeasurementUnitLocalService;
		_portal = portal;
		_regionService = regionService;
	}

	public String getAddShippingFixedOptionURL() throws Exception {
		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				renderRequest, CommercePortletKeys.COMMERCE_SHIPPING_METHODS,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/commerce_shipping_methods/edit_commerce_shipping_fixed_option_rel"
		).setParameter(
			"commerceShippingMethodId", getCommerceShippingMethodId()
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	public List<CommerceInventoryWarehouse> getCommerceInventoryWarehouses()
		throws PortalException {

		CommerceShippingMethod commerceShippingMethod =
			getCommerceShippingMethod();

		return _commerceInventoryWarehouseService.
			getCommerceInventoryWarehouses(
				commerceShippingMethod.getCompanyId(), 0,
				commerceShippingMethod.getGroupId(), true);
	}

	public CommerceShippingFixedOptionRel getCommerceShippingFixedOptionRel()
		throws PortalException {

		CommerceShippingFixedOptionRel commerceShippingFixedOptionRel =
			(CommerceShippingFixedOptionRel)renderRequest.getAttribute(
				CommerceShippingEngineFixedWebKeys.
					COMMERCE_SHIPPING_FIXED_OPTION_REL);

		if (commerceShippingFixedOptionRel != null) {
			return commerceShippingFixedOptionRel;
		}

		long commerceShippingFixedOptionRelId = ParamUtil.getLong(
			renderRequest, "commerceShippingFixedOptionRelId");

		if (commerceShippingFixedOptionRelId > 0) {
			commerceShippingFixedOptionRel =
				_commerceShippingFixedOptionRelService.
					fetchCommerceShippingFixedOptionRel(
						commerceShippingFixedOptionRelId);
		}

		if (commerceShippingFixedOptionRel != null) {
			renderRequest.setAttribute(
				CommerceShippingEngineFixedWebKeys.
					COMMERCE_SHIPPING_FIXED_OPTION_REL,
				commerceShippingFixedOptionRel);
		}

		return commerceShippingFixedOptionRel;
	}

	public List<CommerceShippingFixedOption> getCommerceShippingFixedOptions()
		throws PortalException {

		return _commerceShippingFixedOptionService.
			getCommerceShippingFixedOptions(
				getCommerceShippingMethodId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);
	}

	public List<Country> getCountries() {
		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return _countryService.getCompanyCountries(
			themeDisplay.getCompanyId(), true);
	}

	public long getCountryId() throws PortalException {
		long countryId = 0;

		CommerceShippingFixedOptionRel commerceShippingFixedOptionRel =
			getCommerceShippingFixedOptionRel();

		if (commerceShippingFixedOptionRel != null) {
			countryId = commerceShippingFixedOptionRel.getCountryId();
		}

		return countryId;
	}

	public String getCPMeasurementUnitName(int type) {
		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		CPMeasurementUnit cpMeasurementUnit =
			_cpMeasurementUnitLocalService.fetchPrimaryCPMeasurementUnit(
				themeDisplay.getCompanyId(), type);

		if (cpMeasurementUnit != null) {
			return cpMeasurementUnit.getName(themeDisplay.getLanguageId());
		}

		return StringPool.BLANK;
	}

	public CreationMenu getCreationMenu() throws Exception {
		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return CreationMenuBuilder.addDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(getAddShippingFixedOptionURL());
				dropdownItem.setLabel(
					LanguageUtil.get(
						themeDisplay.getRequest(),
						"add-shipping-option-setting"));
				dropdownItem.setTarget("sidePanel");
			}
		).build();
	}

	public long getRegionId() throws PortalException {
		long regionId = 0;

		CommerceShippingFixedOptionRel commerceShippingFixedOptionRel =
			getCommerceShippingFixedOptionRel();

		if (commerceShippingFixedOptionRel != null) {
			regionId = commerceShippingFixedOptionRel.getRegionId();
		}

		return regionId;
	}

	public List<Region> getRegions() throws PortalException {
		return _regionService.getRegions(getCountryId(), true);
	}

	@Override
	public String getScreenNavigationCategoryKey() {
		return CommerceShippingFixedOptionScreenNavigationConstants.
			CATEGORY_KEY_SHIPPING_OPTION_SETTINGS;
	}

	public boolean isVisible() throws PortalException {
		List<CommerceShippingFixedOption> commerceShippingFixedOptions =
			getCommerceShippingFixedOptions();

		return !commerceShippingFixedOptions.isEmpty();
	}

	private final CommerceInventoryWarehouseService
		_commerceInventoryWarehouseService;
	private final CommerceShippingFixedOptionRelService
		_commerceShippingFixedOptionRelService;
	private final CommerceShippingFixedOptionService
		_commerceShippingFixedOptionService;
	private final CountryService _countryService;
	private final CPMeasurementUnitLocalService _cpMeasurementUnitLocalService;
	private final Portal _portal;
	private final RegionService _regionService;

}