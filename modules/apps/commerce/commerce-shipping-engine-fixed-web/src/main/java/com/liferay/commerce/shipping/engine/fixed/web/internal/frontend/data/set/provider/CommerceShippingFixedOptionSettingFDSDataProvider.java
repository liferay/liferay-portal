/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipping.engine.fixed.web.internal.frontend.data.set.provider;

import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOption;
import com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOptionRel;
import com.liferay.commerce.shipping.engine.fixed.service.CommerceShippingFixedOptionRelService;
import com.liferay.commerce.shipping.engine.fixed.web.internal.constants.CommerceShippingFixedOptionFDSNames;
import com.liferay.commerce.shipping.engine.fixed.web.internal.model.ShippingFixedOptionSetting;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pedro Victor Silvestre
 */
@Component(
	property = "fds.data.provider.key=" + CommerceShippingFixedOptionFDSNames.SHIPPING_FIXED_OPTION_SETTINGS,
	service = FDSDataProvider.class
)
public class CommerceShippingFixedOptionSettingFDSDataProvider
	implements FDSDataProvider<ShippingFixedOptionSetting> {

	@Override
	public List<ShippingFixedOptionSetting> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return TransformUtil.transform(
			_commerceShippingFixedOptionRelService.
				getCommerceShippingMethodFixedOptionRels(
					ParamUtil.getLong(
						httpServletRequest, "commerceShippingMethodId"),
					fdsPagination.getStartPosition(),
					fdsPagination.getEndPosition(), null),
			commerceShippingFixedOptionRel -> {
				CommerceShippingFixedOption commerceShippingFixedOption =
					commerceShippingFixedOptionRel.
						getCommerceShippingFixedOption();
				CommerceShippingMethod commerceShippingMethod =
					commerceShippingFixedOptionRel.getCommerceShippingMethod();

				return new ShippingFixedOptionSetting(
					_getCountry(commerceShippingFixedOptionRel, themeDisplay),
					_getRegion(commerceShippingFixedOptionRel),
					commerceShippingFixedOptionRel.
						getCommerceShippingFixedOptionRelId(),
					commerceShippingMethod.getName(
						themeDisplay.getLanguageId()),
					commerceShippingFixedOption.getName(
						themeDisplay.getLanguageId()),
					_getWarehouse(
						commerceShippingFixedOptionRel,
						themeDisplay.getLocale()),
					_getZip(commerceShippingFixedOptionRel));
			});
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		long commerceShippingMethodId = ParamUtil.getLong(
			httpServletRequest, "commerceShippingMethodId");

		return _commerceShippingFixedOptionRelService.
			getCommerceShippingMethodFixedOptionRelsCount(
				commerceShippingMethodId);
	}

	private String _getCountry(
			CommerceShippingFixedOptionRel commerceShippingFixedOptionRel,
			ThemeDisplay themeDisplay)
		throws PortalException {

		Country country = commerceShippingFixedOptionRel.getCountry();

		if (country == null) {
			return StringPool.STAR;
		}

		return country.getTitle(themeDisplay.getLanguageId());
	}

	private String _getRegion(
			CommerceShippingFixedOptionRel commerceShippingFixedOptionRel)
		throws PortalException {

		Region region = commerceShippingFixedOptionRel.getRegion();

		if (region == null) {
			return StringPool.STAR;
		}

		return region.getName();
	}

	private String _getWarehouse(
			CommerceShippingFixedOptionRel commerceShippingFixedOptionRel,
			Locale locale)
		throws PortalException {

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			commerceShippingFixedOptionRel.getCommerceInventoryWarehouse();

		if (commerceInventoryWarehouse == null) {
			return StringPool.STAR;
		}

		return commerceInventoryWarehouse.getName(locale);
	}

	private String _getZip(
		CommerceShippingFixedOptionRel commerceShippingFixedOptionRel) {

		if (Validator.isNull(commerceShippingFixedOptionRel.getZip())) {
			return StringPool.STAR;
		}

		return commerceShippingFixedOptionRel.getZip();
	}

	@Reference
	private CommerceShippingFixedOptionRelService
		_commerceShippingFixedOptionRelService;

}