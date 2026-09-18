/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.internal.util.v2_0;

import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommerceTierPriceEntry;
import com.liferay.commerce.price.list.service.CommerceTierPriceEntryService;
import com.liferay.headless.commerce.admin.pricing.dto.v2_0.TierPrice;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.headless.commerce.core.util.DateConfig;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import java.math.BigDecimal;

/**
 * @author Riccardo Alberti
 */
public class TierPriceUtil {

	public static CommerceTierPriceEntry addOrUpdateCommerceTierPriceEntry(
			CommerceTierPriceEntryService commerceTierPriceEntryService,
			TierPrice tierPrice, CommercePriceEntry commercePriceEntry,
			ServiceContextHelper serviceContextHelper)
		throws PortalException {

		ServiceContext serviceContext =
			serviceContextHelper.getServiceContext();

		DateConfig displayDateConfig = DateConfig.toDisplayDateConfig(
			tierPrice.getDisplayDate(), serviceContext.getTimeZone());
		DateConfig expirationDateConfig = DateConfig.toExpirationDateConfig(
			tierPrice.getExpirationDate(), serviceContext.getTimeZone());

		long tierPriceId = 0;

		if (Validator.isNull(tierPrice.getExternalReferenceCode())) {
			tierPriceId = GetterUtil.getLong(tierPrice.getId());
		}

		return commerceTierPriceEntryService.addOrUpdateCommerceTierPriceEntry(
			tierPrice.getExternalReferenceCode(), tierPriceId,
			commercePriceEntry.getCommercePriceEntryId(),
			BigDecimal.valueOf(tierPrice.getPrice()),
			tierPrice.getMinimumQuantity(), commercePriceEntry.isBulkPricing(),
			GetterUtil.getBoolean(tierPrice.getDiscountDiscovery(), true),
			tierPrice.getDiscountLevel1(), tierPrice.getDiscountLevel2(),
			tierPrice.getDiscountLevel3(), tierPrice.getDiscountLevel4(),
			displayDateConfig.getMonth(), displayDateConfig.getDay(),
			displayDateConfig.getYear(), displayDateConfig.getHour(),
			displayDateConfig.getMinute(), expirationDateConfig.getMonth(),
			expirationDateConfig.getDay(), expirationDateConfig.getYear(),
			expirationDateConfig.getHour(), expirationDateConfig.getMinute(),
			GetterUtil.getBoolean(tierPrice.getNeverExpire(), true),
			tierPrice.getPriceEntryExternalReferenceCode(), serviceContext);
	}

}