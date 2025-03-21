/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.web.internal.display.context;

import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.model.CommerceMoney;
import com.liferay.commerce.currency.util.CommercePriceFormatter;
import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.model.CommerceTierPriceEntry;
import com.liferay.commerce.price.list.service.CommercePriceEntryService;
import com.liferay.commerce.price.list.service.CommercePriceListService;
import com.liferay.commerce.price.list.service.CommerceTierPriceEntryService;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.service.CommerceCatalogService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.util.CustomAttributesUtil;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Alessio Antonio Rendina
 */
public class CommerceTierCommercePriceEntryDisplayContext
	extends BaseCommercePriceListDisplayContext {

	public CommerceTierCommercePriceEntryDisplayContext(
		CommerceCatalogService commerceCatalogService,
		CommercePriceEntryService commercePriceEntryService,
		CommercePriceFormatter commercePriceFormatter,
		ModelResourcePermission<CommercePriceList>
			commercePriceListModelResourcePermission,
		CommercePriceListService commercePriceListService,
		CommerceTierPriceEntryService commerceTierPriceEntryService,
		CPInstanceLocalService cpInstanceLocalService,
		HttpServletRequest httpServletRequest) {

		super(
			commerceCatalogService, commercePriceListModelResourcePermission,
			commercePriceListService, httpServletRequest);

		_commercePriceEntryService = commercePriceEntryService;
		_commercePriceFormatter = commercePriceFormatter;
		_commerceTierPriceEntryService = commerceTierPriceEntryService;
		_cpInstanceLocalService = cpInstanceLocalService;
	}

	public CommercePriceEntry getCommercePriceEntry() throws PortalException {
		long commercePriceEntryId = ParamUtil.getLong(
			commercePricingRequestHelper.getRequest(), "commercePriceEntryId");

		if (commercePriceEntryId <= 0) {
			return null;
		}

		return _commercePriceEntryService.getCommercePriceEntry(
			commercePriceEntryId);
	}

	public long getCommercePriceEntryId() throws PortalException {
		CommercePriceEntry commercePriceEntry = getCommercePriceEntry();

		if (commercePriceEntry == null) {
			return 0;
		}

		return commercePriceEntry.getCommercePriceEntryId();
	}

	public CommerceCurrency getCommercePriceListCurrency()
		throws PortalException {

		CommercePriceList commercePriceList = getCommercePriceList();

		return commercePriceList.getCommerceCurrency();
	}

	public CommerceTierPriceEntry getCommerceTierPriceEntry()
		throws PortalException {

		if (_commerceTierPriceEntry != null) {
			return _commerceTierPriceEntry;
		}

		long commerceTierPriceEntryId = ParamUtil.getLong(
			commercePricingRequestHelper.getRequest(),
			"commerceTierPriceEntryId");

		if (commerceTierPriceEntryId > 0) {
			_commerceTierPriceEntry =
				_commerceTierPriceEntryService.getCommerceTierPriceEntry(
					commerceTierPriceEntryId);
		}

		return _commerceTierPriceEntry;
	}

	public long getCommerceTierPriceEntryId() throws PortalException {
		CommerceTierPriceEntry commerceTierPriceEntry =
			getCommerceTierPriceEntry();

		if (commerceTierPriceEntry == null) {
			return 0;
		}

		return commerceTierPriceEntry.getCommerceTierPriceEntryId();
	}

	public String getCommerceTierPriceEntryPrice(
			CommerceTierPriceEntry commerceTierPriceEntry)
		throws PortalException {

		if (commerceTierPriceEntry == null) {
			CommerceCurrency commerceCurrency = getCommercePriceListCurrency();

			CommerceMoney zeroCommerceMoney = commerceCurrency.getZero();

			return zeroCommerceMoney.format(
				commercePricingRequestHelper.getLocale());
		}

		CommercePriceList commercePriceList = getCommercePriceList();

		CommerceCurrency commerceCurrency =
			commercePriceList.getCommerceCurrency();

		CommerceMoney priceCommerceMoney =
			commerceTierPriceEntry.getPriceCommerceMoney(
				commerceCurrency.getCommerceCurrencyId());

		return priceCommerceMoney.format(
			commercePricingRequestHelper.getLocale());
	}

	public String getContextTitle() throws PortalException {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		StringBundler sb = new StringBundler(5);

		CommercePriceList commercePriceList = getCommercePriceList();

		if (commercePriceList != null) {
			sb.append(commercePriceList.getName());
		}

		CommercePriceEntry commercePriceEntry = getCommercePriceEntry();

		if (commercePriceEntry != null) {
			CPInstance cpInstance =
				_cpInstanceLocalService.fetchCProductInstance(
					commercePriceEntry.getCProductId(),
					commercePriceEntry.getCPInstanceUuid());

			if (cpInstance != null) {
				CPDefinition cpDefinition = cpInstance.getCPDefinition();

				if (cpDefinition != null) {
					sb.append(" - ");
					sb.append(
						cpDefinition.getName(themeDisplay.getLanguageId()));
					sb.append(" - ");
					sb.append(cpInstance.getSku());
				}
			}
		}

		CommerceTierPriceEntry commerceTierPriceEntry =
			getCommerceTierPriceEntry();

		String contextTitle = sb.toString();

		if (commerceTierPriceEntry == null) {
			contextTitle = LanguageUtil.format(
				themeDisplay.getRequest(), "add-tier-price-entry-to-x",
				contextTitle);
		}

		return contextTitle;
	}

	public String getDiscountLevel1() throws PortalException {
		CommerceTierPriceEntry commerceTierPriceEntry =
			getCommerceTierPriceEntry();

		if (commerceTierPriceEntry == null) {
			return StringPool.BLANK;
		}

		return _commercePriceFormatter.format(
			commerceTierPriceEntry.getDiscountLevel1(),
			cpRequestHelper.getLocale());
	}

	public String getDiscountLevel2() throws PortalException {
		CommerceTierPriceEntry commerceTierPriceEntry =
			getCommerceTierPriceEntry();

		if (commerceTierPriceEntry == null) {
			return StringPool.BLANK;
		}

		return _commercePriceFormatter.format(
			commerceTierPriceEntry.getDiscountLevel2(),
			cpRequestHelper.getLocale());
	}

	public String getDiscountLevel3() throws PortalException {
		CommerceTierPriceEntry commerceTierPriceEntry =
			getCommerceTierPriceEntry();

		if (commerceTierPriceEntry == null) {
			return StringPool.BLANK;
		}

		return _commercePriceFormatter.format(
			commerceTierPriceEntry.getDiscountLevel3(),
			cpRequestHelper.getLocale());
	}

	public String getDiscountLevel4() throws PortalException {
		CommerceTierPriceEntry commerceTierPriceEntry =
			getCommerceTierPriceEntry();

		if (commerceTierPriceEntry == null) {
			return StringPool.BLANK;
		}

		return _commercePriceFormatter.format(
			commerceTierPriceEntry.getDiscountLevel4(),
			cpRequestHelper.getLocale());
	}

	public String getPrice() throws PortalException {
		CommerceTierPriceEntry commerceTierPriceEntry =
			getCommerceTierPriceEntry();

		if (commerceTierPriceEntry == null) {
			return StringPool.BLANK;
		}

		return _commercePriceFormatter.format(
			commerceTierPriceEntry.getPrice(), cpRequestHelper.getLocale());
	}

	public boolean hasCustomAttributes() throws Exception {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return CustomAttributesUtil.hasCustomAttributes(
			themeDisplay.getCompanyId(), CommerceTierPriceEntry.class.getName(),
			getCommerceTierPriceEntryId(), null);
	}

	private final CommercePriceEntryService _commercePriceEntryService;
	private final CommercePriceFormatter _commercePriceFormatter;
	private CommerceTierPriceEntry _commerceTierPriceEntry;
	private final CommerceTierPriceEntryService _commerceTierPriceEntryService;
	private final CPInstanceLocalService _cpInstanceLocalService;

}