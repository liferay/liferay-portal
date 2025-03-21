/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipping.web.internal.frontend.data.set.provider;

import com.liferay.commerce.frontend.model.RestrictionField;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.commerce.service.CommerceAddressRestrictionLocalService;
import com.liferay.commerce.service.CommerceShippingMethodService;
import com.liferay.commerce.shipping.web.internal.constants.CommerceShippingFDSNames;
import com.liferay.commerce.shipping.web.internal.model.ShippingRestriction;
import com.liferay.commerce.util.CommerceUtil;
import com.liferay.commerce.util.comparator.CommerceShippingMethodPriorityComparator;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanProperties;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.CountryService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pedro Victor Silvestre
 */
@Component(
	property = "fds.data.provider.key=" + CommerceShippingFDSNames.SHIPPING_RESTRICTIONS,
	service = FDSDataProvider.class
)
public class CommerceShippingRestrictionsPageFDSDataProvider
	implements FDSDataProvider<ShippingRestriction> {

	@Override
	public List<ShippingRestriction> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		List<ShippingRestriction> shippingRestrictions = new ArrayList<>();

		long commerceChannelId = ParamUtil.getLong(
			httpServletRequest, "commerceChannelId");

		CommerceChannel commerceChannel =
			_commerceChannelService.getCommerceChannel(commerceChannelId);

		List<CommerceShippingMethod> commerceShippingMethods =
			_commerceShippingMethodService.getCommerceShippingMethods(
				commerceChannel.getGroupId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS,
				CommerceShippingMethodPriorityComparator.getInstance(false));

		String orderByFieldName = _beanProperties.getString(
			sort, "fieldName", StringPool.BLANK);

		String orderByType = "asc";

		boolean reverse = _beanProperties.getBooleanSilent(
			sort, "reverse", false);

		if (reverse) {
			orderByType = "desc";
		}

		BaseModelSearchResult<Country> baseModelSearchResult =
			_countryService.searchCountries(
				_portal.getCompanyId(httpServletRequest), true,
				fdsKeywords.getKeywords(), fdsPagination.getStartPosition(),
				fdsPagination.getEndPosition(),
				CommerceUtil.getCountryOrderByComparator(
					orderByFieldName, orderByType));

		for (Country country : baseModelSearchResult.getBaseModels()) {
			shippingRestrictions.add(
				new ShippingRestriction(
					country.getCountryId(),
					country.getTitle(themeDisplay.getLocale()),
					_getFields(
						country.getCountryId(), commerceShippingMethods,
						LocaleUtil.toLanguageId(themeDisplay.getLocale()))));
		}

		return shippingRestrictions;
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		BaseModelSearchResult<Country> baseModelSearchResult =
			_countryService.searchCountries(
				_portal.getCompanyId(httpServletRequest), true,
				fdsKeywords.getKeywords(), 0, 0, null);

		return baseModelSearchResult.getLength();
	}

	private List<RestrictionField> _getFields(
		long countryId, List<CommerceShippingMethod> commerceShippingMethods,
		String languageId) {

		return TransformUtil.transform(
			commerceShippingMethods,
			commerceShippingMethod -> new RestrictionField(
				commerceShippingMethod.getName(languageId),
				String.valueOf(
					commerceShippingMethod.getCommerceShippingMethodId()),
				_commerceAddressRestrictionLocalService.
					isCommerceShippingMethodRestricted(
						commerceShippingMethod.getCommerceShippingMethodId(),
						countryId)));
	}

	@Reference
	private BeanProperties _beanProperties;

	@Reference
	private CommerceAddressRestrictionLocalService
		_commerceAddressRestrictionLocalService;

	@Reference
	private CommerceChannelService _commerceChannelService;

	@Reference
	private CommerceShippingMethodService _commerceShippingMethodService;

	@Reference
	private CountryService _countryService;

	@Reference
	private Portal _portal;

}