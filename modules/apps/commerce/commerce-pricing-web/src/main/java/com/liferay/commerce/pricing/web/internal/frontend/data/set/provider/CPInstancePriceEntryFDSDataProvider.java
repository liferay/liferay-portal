/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.web.internal.frontend.data.set.provider;

import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.model.CommerceMoney;
import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceEntryService;
import com.liferay.commerce.pricing.web.internal.constants.CommercePricingFDSNames;
import com.liferay.commerce.pricing.web.internal.model.InstancePriceEntry;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "fds.data.provider.key=" + CommercePricingFDSNames.INSTANCE_PRICE_ENTRIES,
	service = FDSDataProvider.class
)
public class CPInstancePriceEntryFDSDataProvider
	implements FDSDataProvider<InstancePriceEntry> {

	@Override
	public List<InstancePriceEntry> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		long cpInstanceId = ParamUtil.getLong(
			httpServletRequest, "cpInstanceId");

		return TransformUtil.transform(
			_commercePriceEntryService.getInstanceCommercePriceEntries(
				cpInstanceId, fdsPagination.getStartPosition(),
				fdsPagination.getEndPosition()),
			commercePriceEntry -> {
				CommercePriceList commercePriceList =
					commercePriceEntry.getCommercePriceList();

				CommerceCurrency commerceCurrency =
					commercePriceList.getCommerceCurrency();

				CommerceMoney priceCommerceMoney =
					commercePriceEntry.getPriceCommerceMoney(
						commerceCurrency.getCommerceCurrencyId());

				Date createDate = commercePriceEntry.getCreateDate();

				String createDateDescription = _language.getTimeDescription(
					httpServletRequest,
					System.currentTimeMillis() - createDate.getTime(), true);

				return new InstancePriceEntry(
					commercePriceEntry.getCommercePriceEntryId(),
					_language.format(
						httpServletRequest, "x-ago", createDateDescription,
						false),
					commercePriceList.getName(),
					commercePriceEntry.isPriceOnApplication(),
					_getQuantity(commercePriceEntry),
					commercePriceEntry.getUnitOfMeasureKey(),
					HtmlUtil.escape(
						priceCommerceMoney.format(
							_portal.getLocale(httpServletRequest))));
			});
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		long cpInstanceId = ParamUtil.getLong(
			httpServletRequest, "cpInstanceId");

		return _commercePriceEntryService.getInstanceCommercePriceEntriesCount(
			cpInstanceId);
	}

	private String _getQuantity(CommercePriceEntry commercePriceEntry) {
		BigDecimal quantity = commercePriceEntry.getQuantity();

		if (quantity == null) {
			return StringPool.BLANK;
		}

		quantity = quantity.stripTrailingZeros();

		return quantity.toString();
	}

	@Reference
	private CommercePriceEntryService _commercePriceEntryService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}