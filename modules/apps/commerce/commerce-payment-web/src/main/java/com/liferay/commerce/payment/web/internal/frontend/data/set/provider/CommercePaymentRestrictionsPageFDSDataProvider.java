/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.web.internal.frontend.data.set.provider;

import com.liferay.commerce.frontend.model.RestrictionField;
import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelService;
import com.liferay.commerce.payment.web.internal.constants.CommercePaymentMethodGroupRelFDSNames;
import com.liferay.commerce.payment.web.internal.model.PaymentRestriction;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.commerce.service.CommerceAddressRestrictionLocalService;
import com.liferay.commerce.util.CommerceUtil;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanProperties;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.CountryService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
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
	property = "fds.data.provider.key=" + CommercePaymentMethodGroupRelFDSNames.PAYMENT_RESTRICTIONS,
	service = FDSDataProvider.class
)
public class CommercePaymentRestrictionsPageFDSDataProvider
	implements FDSDataProvider<PaymentRestriction> {

	@Override
	public List<PaymentRestriction> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		List<PaymentRestriction> paymentRestrictions = new ArrayList<>();

		long commerceChannelId = ParamUtil.getLong(
			httpServletRequest, "commerceChannelId");

		CommerceChannel commerceChannel =
			_commerceChannelService.getCommerceChannel(commerceChannelId);

		List<CommercePaymentMethodGroupRel> commercePaymentMethodGroupRels =
			_commercePaymentMethodGroupRelService.
				getCommercePaymentMethodGroupRels(
					commerceChannel.getGroupId(), true);

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
			paymentRestrictions.add(
				new PaymentRestriction(
					country.getCountryId(),
					country.getTitle(themeDisplay.getLocale()),
					_getFields(
						commercePaymentMethodGroupRels, country.getCountryId(),
						themeDisplay.getLanguageId())));
		}

		return paymentRestrictions;
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		BaseModelSearchResult<Country> commerceCountryBaseModelSearchResult =
			_countryService.searchCountries(
				_portal.getCompanyId(httpServletRequest), true,
				fdsKeywords.getKeywords(), 0, 0, null);

		return commerceCountryBaseModelSearchResult.getLength();
	}

	private List<RestrictionField> _getFields(
		List<CommercePaymentMethodGroupRel> commercePaymentMethodGroupRels,
		long countryId, String languageId) {

		return TransformUtil.transform(
			commercePaymentMethodGroupRels,
			commercePaymentMethodGroupRel -> new RestrictionField(
				commercePaymentMethodGroupRel.getName(languageId),
				String.valueOf(
					commercePaymentMethodGroupRel.
						getCommercePaymentMethodGroupRelId()),
				_commerceAddressRestrictionLocalService.
					isCommerceAddressRestricted(
						CommercePaymentMethodGroupRel.class.getName(),
						commercePaymentMethodGroupRel.
							getCommercePaymentMethodGroupRelId(),
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
	private CommercePaymentMethodGroupRelService
		_commercePaymentMethodGroupRelService;

	@Reference
	private CountryService _countryService;

	@Reference
	private Portal _portal;

}