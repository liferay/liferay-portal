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
import com.liferay.commerce.tax.engine.fixed.configuration.CommerceTaxByAddressTypeConfiguration;
import com.liferay.commerce.tax.engine.fixed.model.CommerceTaxFixedRateAddressRel;
import com.liferay.commerce.tax.engine.fixed.service.CommerceTaxFixedRateAddressRelService;
import com.liferay.commerce.tax.engine.fixed.web.internal.constants.CommerceTaxRateSettingFDSNames;
import com.liferay.commerce.tax.model.CommerceTaxMethod;
import com.liferay.commerce.tax.service.CommerceTaxMethodService;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.CountryService;
import com.liferay.portal.kernel.service.RegionService;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderRequest;

import java.util.List;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
public class CommerceTaxFixedRateAddressRelsDisplayContext
	extends BaseCommerceTaxFixedRateDisplayContext {

	public CommerceTaxFixedRateAddressRelsDisplayContext(
		CommerceChannelLocalService commerceChannelLocalService,
		CommerceCurrencyLocalService commerceCurrencyLocalService,
		CommerceTaxFixedRateAddressRelService
			commerceTaxFixedRateAddressRelService,
		CommerceTaxMethodService commerceTaxMethodService,
		CountryService countryService,
		CPTaxCategoryService cpTaxCategoryService,
		ModelResourcePermission<CommerceChannel> modelResourcePermission,
		PercentageFormatter percentageFormatter, RegionService regionService,
		RenderRequest renderRequest) {

		super(
			commerceChannelLocalService, commerceCurrencyLocalService,
			commerceTaxMethodService, cpTaxCategoryService,
			modelResourcePermission, percentageFormatter, renderRequest);

		_commerceTaxFixedRateAddressRelService =
			commerceTaxFixedRateAddressRelService;
		_countryService = countryService;
		_regionService = regionService;
	}

	public String getAddTaxRateSettingURL() throws Exception {
		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				commerceTaxFixedRateRequestHelper.getRequest(),
				CommercePortletKeys.COMMERCE_TAX_METHODS,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/commerce_tax_methods/edit_commerce_tax_fixed_rate_address_rel"
		).setParameter(
			"commerceTaxMethodId", getCommerceTaxMethodId()
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	public CommerceTaxFixedRateAddressRel getCommerceTaxFixedRateAddressRel()
		throws PortalException {

		long commerceTaxFixedRateAddressRelId = ParamUtil.getLong(
			commerceTaxFixedRateRequestHelper.getRequest(),
			"commerceTaxFixedRateAddressRelId");

		return _commerceTaxFixedRateAddressRelService.
			fetchCommerceTaxFixedRateAddressRel(
				commerceTaxFixedRateAddressRelId);
	}

	public List<Country> getCountries() {
		return _countryService.getCompanyCountries(
			commerceTaxFixedRateRequestHelper.getCompanyId(), true);
	}

	public long getCountryId() throws PortalException {
		long countryId = 0;

		CommerceTaxFixedRateAddressRel commerceTaxFixedRateAddressRel =
			getCommerceTaxFixedRateAddressRel();

		if (commerceTaxFixedRateAddressRel != null) {
			countryId = commerceTaxFixedRateAddressRel.getCountryId();
		}

		return countryId;
	}

	public CreationMenu getCreationMenu() throws Exception {
		CreationMenu creationMenu = new CreationMenu();

		if (hasUpdateCommerceChannelPermission()) {
			creationMenu.addDropdownItem(
				dropdownItem -> {
					dropdownItem.setHref(getAddTaxRateSettingURL());
					dropdownItem.setLabel(
						LanguageUtil.get(
							commerceTaxFixedRateRequestHelper.getRequest(),
							"add-tax-rate-setting"));
					dropdownItem.setTarget("modal-lg");
				});
		}

		return creationMenu;
	}

	public String getFDSName() throws PortalException {
		CommerceTaxMethod commerceTaxMethod = getCommerceTaxMethod();

		if (commerceTaxMethod.isPercentage()) {
			return CommerceTaxRateSettingFDSNames.PERCENTAGE_TAX_RATE_SETTING;
		}

		return CommerceTaxRateSettingFDSNames.TAX_RATE_SETTING;
	}

	public long getRegionId() throws PortalException {
		long regionId = 0;

		CommerceTaxFixedRateAddressRel commerceTaxFixedRateAddressRel =
			getCommerceTaxFixedRateAddressRel();

		if (commerceTaxFixedRateAddressRel != null) {
			regionId = commerceTaxFixedRateAddressRel.getRegionId();
		}

		return regionId;
	}

	public List<Region> getRegions() throws PortalException {
		return _regionService.getRegions(getCountryId(), true);
	}

	@Override
	public String getScreenNavigationCategoryKey() {
		return CommerceTaxScreenNavigationConstants.
			CATEGORY_KEY_COMMERCE_TAX_RATING_SETTINGS;
	}

	public boolean isTaxAppliedToShippingAddress() throws PortalException {
		CommerceTaxMethod commerceTaxMethod = getCommerceTaxMethod();

		CommerceTaxByAddressTypeConfiguration
			commerceTaxByAddressTypeConfiguration =
				ConfigurationProviderUtil.getConfiguration(
					CommerceTaxByAddressTypeConfiguration.class,
					new GroupServiceSettingsLocator(
						commerceTaxMethod.getGroupId(),
						CommerceTaxByAddressTypeConfiguration.class.getName()));

		return commerceTaxByAddressTypeConfiguration.
			taxAppliedToShippingAddress();
	}

	private final CommerceTaxFixedRateAddressRelService
		_commerceTaxFixedRateAddressRelService;
	private final CountryService _countryService;
	private final RegionService _regionService;

}