/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.web.internal.frontend.data.set.provider;

import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.model.CommerceMoney;
import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.model.CommerceTierPriceEntry;
import com.liferay.commerce.price.list.service.CommercePriceEntryService;
import com.liferay.commerce.price.list.service.CommerceTierPriceEntryService;
import com.liferay.commerce.pricing.web.internal.constants.CommercePricingFDSNames;
import com.liferay.commerce.pricing.web.internal.model.TierPriceEntry;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.util.CommerceQuantityFormatter;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.text.DateFormat;
import java.text.Format;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "fds.data.provider.key=" + CommercePricingFDSNames.TIER_PRICE_ENTRIES,
	service = FDSDataProvider.class
)
public class CommerceTierPriceEntryFDSDataProvider
	implements FDSDataProvider<TierPriceEntry> {

	@Override
	public List<TierPriceEntry> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		List<TierPriceEntry> tierPriceEntries = new ArrayList<>();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Format dateTimeFormat = FastDateFormatFactoryUtil.getDateTime(
			DateFormat.MEDIUM, DateFormat.MEDIUM, themeDisplay.getLocale(),
			themeDisplay.getTimeZone());

		long commercePriceEntryId = ParamUtil.getLong(
			httpServletRequest, "commercePriceEntryId");

		CommercePriceEntry commercePriceEntry =
			_commercePriceEntryService.getCommercePriceEntry(
				commercePriceEntryId);

		CommercePriceList commercePriceList =
			commercePriceEntry.getCommercePriceList();

		BaseModelSearchResult<CommerceTierPriceEntry>
			commerceTierPriceEntryBaseModelSearchResult =
				_commerceTierPriceEntryService.searchCommerceTierPriceEntries(
					_portal.getCompanyId(httpServletRequest),
					commercePriceEntryId, fdsKeywords.getKeywords(),
					fdsPagination.getStartPosition(),
					fdsPagination.getEndPosition(), sort);

		CPInstance cpInstance = _cpInstanceLocalService.fetchCProductInstance(
			commercePriceEntry.getCProductId(),
			commercePriceEntry.getCPInstanceUuid());

		for (CommerceTierPriceEntry commerceTierPriceEntry :
				commerceTierPriceEntryBaseModelSearchResult.getBaseModels()) {

			CommerceCurrency commerceCurrency =
				commercePriceList.getCommerceCurrency();

			CommerceMoney priceCommerceMoney =
				commerceTierPriceEntry.getPriceCommerceMoney(
					commerceCurrency.getCommerceCurrencyId());

			tierPriceEntries.add(
				new TierPriceEntry(
					_getDiscountLevels(commerceTierPriceEntry),
					_getEndDate(commerceTierPriceEntry, dateTimeFormat),
					_getOverride(commerceTierPriceEntry, httpServletRequest),
					priceCommerceMoney.format(themeDisplay.getLocale()),
					_commerceQuantityFormatter.format(
						cpInstance, commerceTierPriceEntry.getMinQuantity(),
						commercePriceEntry.getUnitOfMeasureKey()),
					dateTimeFormat.format(
						commerceTierPriceEntry.getDisplayDate()),
					commerceTierPriceEntry.getCommerceTierPriceEntryId()));
		}

		return tierPriceEntries;
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		long commercePriceEntryId = ParamUtil.getLong(
			httpServletRequest, "commercePriceEntryId");

		return _commerceTierPriceEntryService.
			searchCommerceTierPriceEntriesCount(
				_portal.getCompanyId(httpServletRequest), commercePriceEntryId,
				fdsKeywords.getKeywords());
	}

	private String _getDiscountLevels(
		CommerceTierPriceEntry commerceTierPriceEntry) {

		if (commerceTierPriceEntry.isDiscountDiscovery()) {
			return StringPool.BLANK;
		}

		return StringBundler.concat(
			commerceTierPriceEntry.getDiscountLevel1(), " - ",
			commerceTierPriceEntry.getDiscountLevel2(), " - ",
			commerceTierPriceEntry.getDiscountLevel3(), " - ",
			commerceTierPriceEntry.getDiscountLevel4());
	}

	private String _getEndDate(
		CommerceTierPriceEntry commerceTierPriceEntry, Format dateTimeFormat) {

		if (commerceTierPriceEntry.getExpirationDate() == null) {
			return StringPool.BLANK;
		}

		return dateTimeFormat.format(
			commerceTierPriceEntry.getExpirationDate());
	}

	private String _getOverride(
		CommerceTierPriceEntry commerceTierPriceEntry,
		HttpServletRequest httpServletRequest) {

		if (commerceTierPriceEntry.isDiscountDiscovery()) {
			return _language.get(httpServletRequest, "no");
		}

		return _language.get(httpServletRequest, "yes");
	}

	@Reference
	private CommercePriceEntryService _commercePriceEntryService;

	@Reference
	private CommerceQuantityFormatter _commerceQuantityFormatter;

	@Reference
	private CommerceTierPriceEntryService _commerceTierPriceEntryService;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}