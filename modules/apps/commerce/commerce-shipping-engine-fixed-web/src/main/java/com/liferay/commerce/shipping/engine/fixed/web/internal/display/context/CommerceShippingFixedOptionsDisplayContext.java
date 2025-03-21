/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipping.engine.fixed.web.internal.display.context;

import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceShippingMethodService;
import com.liferay.commerce.shipping.engine.fixed.constants.CommerceShippingEngineFixedWebKeys;
import com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOption;
import com.liferay.commerce.shipping.engine.fixed.service.CommerceShippingFixedOptionService;
import com.liferay.commerce.shipping.engine.fixed.web.internal.FixedCommerceShippingEngine;
import com.liferay.commerce.shipping.engine.fixed.web.internal.constants.CommerceShippingFixedOptionScreenNavigationConstants;
import com.liferay.frontend.data.set.model.FDSSortItemBuilder;
import com.liferay.frontend.data.set.model.FDSSortItemList;
import com.liferay.frontend.data.set.model.FDSSortItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.ResourceBundle;

/**
 * @author Alessio Antonio Rendina
 */
public class CommerceShippingFixedOptionsDisplayContext
	extends BaseCommerceShippingFixedOptionDisplayContext {

	public CommerceShippingFixedOptionsDisplayContext(
		CommerceChannelLocalService commerceChannelLocalService,
		CommerceCurrencyLocalService commerceCurrencyLocalService,
		CommerceShippingFixedOptionService commerceShippingFixedOptionService,
		CommerceShippingMethodService commerceShippingMethodService,
		Portal portal, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		super(
			commerceChannelLocalService, commerceCurrencyLocalService,
			commerceShippingMethodService, renderRequest, renderResponse);

		_commerceShippingFixedOptionService =
			commerceShippingFixedOptionService;
		_portal = portal;
	}

	public String getAddShippingFixedOptionURL() throws Exception {
		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				renderRequest, CommercePortletKeys.COMMERCE_SHIPPING_METHODS,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/commerce_shipping_methods/edit_commerce_shipping_fixed_option"
		).setParameter(
			"commerceShippingMethodId", getCommerceShippingMethodId()
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	public CommerceShippingFixedOption getCommerceShippingFixedOption()
		throws PortalException {

		CommerceShippingFixedOption commerceShippingFixedOption =
			(CommerceShippingFixedOption)renderRequest.getAttribute(
				CommerceShippingEngineFixedWebKeys.
					COMMERCE_SHIPPING_FIXED_OPTION);

		if (commerceShippingFixedOption != null) {
			return commerceShippingFixedOption;
		}

		long commerceShippingFixedOptionId = ParamUtil.getLong(
			renderRequest, "commerceShippingFixedOptionId");

		commerceShippingFixedOption =
			_commerceShippingFixedOptionService.
				fetchCommerceShippingFixedOption(commerceShippingFixedOptionId);

		renderRequest.setAttribute(
			CommerceShippingEngineFixedWebKeys.COMMERCE_SHIPPING_FIXED_OPTION,
			commerceShippingFixedOption);

		return commerceShippingFixedOption;
	}

	public String getCommerceShippingFixedOptionName(
			ResourceBundle resourceBundle)
		throws PortalException {

		CommerceShippingFixedOption commerceShippingFixedOption =
			getCommerceShippingFixedOption();

		if (commerceShippingFixedOption == null) {
			return LanguageUtil.get(resourceBundle, "shipping-option");
		}

		return commerceShippingFixedOption.getName(resourceBundle.getLocale());
	}

	public CreationMenu getCreationMenu() throws Exception {
		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return CreationMenuBuilder.addDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(getAddShippingFixedOptionURL());
				dropdownItem.setLabel(
					LanguageUtil.get(
						themeDisplay.getLocale(), "add-shipping-option"));
				dropdownItem.setTarget("sidePanel");
			}
		).build();
	}

	public FDSSortItemList getFDSSortItemList() {
		return FDSSortItemListBuilder.add(
			FDSSortItemBuilder.setDirection(
				"desc"
			).setKey(
				"priority"
			).build()
		).build();
	}

	@Override
	public String getScreenNavigationCategoryKey() {
		return CommerceShippingFixedOptionScreenNavigationConstants.
			CATEGORY_KEY_SHIPPING_OPTIONS;
	}

	public boolean isFixed() throws PortalException {
		CommerceShippingMethod commerceShippingMethod =
			getCommerceShippingMethod();

		if (commerceShippingMethod == null) {
			CommerceShippingFixedOption commerceShippingFixedOption =
				getCommerceShippingFixedOption();

			commerceShippingMethod =
				commerceShippingMethodService.getCommerceShippingMethod(
					commerceShippingFixedOption.getCommerceShippingMethodId());
		}

		String engineKey = commerceShippingMethod.getEngineKey();

		return engineKey.equals(FixedCommerceShippingEngine.KEY);
	}

	private final CommerceShippingFixedOptionService
		_commerceShippingFixedOptionService;
	private final Portal _portal;

}