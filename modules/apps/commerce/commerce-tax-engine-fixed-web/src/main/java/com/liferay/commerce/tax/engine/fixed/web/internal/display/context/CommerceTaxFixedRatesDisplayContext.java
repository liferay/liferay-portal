/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.engine.fixed.web.internal.display.context;

import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.constants.CommerceTaxScreenNavigationConstants;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.percentage.PercentageFormatter;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPTaxCategoryService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.tax.engine.fixed.model.CommerceTaxFixedRate;
import com.liferay.commerce.tax.engine.fixed.service.CommerceTaxFixedRateService;
import com.liferay.commerce.tax.service.CommerceTaxMethodService;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderRequest;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
public class CommerceTaxFixedRatesDisplayContext
	extends BaseCommerceTaxFixedRateDisplayContext {

	public CommerceTaxFixedRatesDisplayContext(
		CommerceChannelLocalService commerceChannelLocalService,
		ModelResourcePermission<CommerceChannel>
			commerceChannelModelResourcePermission,
		CommerceCurrencyLocalService commerceCurrencyLocalService,
		CommerceTaxFixedRateService commerceTaxFixedRateService,
		CommerceTaxMethodService commerceTaxMethodService,
		CPTaxCategoryService cpTaxCategoryService,
		PercentageFormatter percentageFormatter, RenderRequest renderRequest) {

		super(
			commerceChannelLocalService, commerceCurrencyLocalService,
			commerceTaxMethodService, cpTaxCategoryService,
			commerceChannelModelResourcePermission, percentageFormatter,
			renderRequest);

		_commerceTaxFixedRateService = commerceTaxFixedRateService;
	}

	public String getAddTaxRateURL() throws Exception {
		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				commerceTaxFixedRateRequestHelper.getRequest(),
				CommercePortletKeys.COMMERCE_TAX_METHODS,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/commerce_tax_methods/edit_commerce_tax_fixed_rate"
		).setParameter(
			"commerceTaxMethodId", getCommerceTaxMethodId()
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	public CommerceTaxFixedRate getCommerceTaxFixedRate()
		throws PortalException {

		long commerceTaxFixedRateId = ParamUtil.getLong(
			commerceTaxFixedRateRequestHelper.getRequest(),
			"commerceTaxFixedRateId");

		return _commerceTaxFixedRateService.fetchCommerceTaxFixedRate(
			commerceTaxFixedRateId);
	}

	public CreationMenu getCreationMenu() throws Exception {
		CreationMenu creationMenu = new CreationMenu();

		if (hasUpdateCommerceChannelPermission()) {
			creationMenu.addDropdownItem(
				dropdownItem -> {
					dropdownItem.setHref(getAddTaxRateURL());
					dropdownItem.setLabel(
						LanguageUtil.get(
							commerceTaxFixedRateRequestHelper.getRequest(),
							"add-tax-rate"));
					dropdownItem.setTarget("modal-lg");
				});
		}

		return creationMenu;
	}

	@Override
	public String getScreenNavigationCategoryKey() {
		return CommerceTaxScreenNavigationConstants.
			CATEGORY_KEY_COMMERCE_TAX_RATES;
	}

	private final CommerceTaxFixedRateService _commerceTaxFixedRateService;

}