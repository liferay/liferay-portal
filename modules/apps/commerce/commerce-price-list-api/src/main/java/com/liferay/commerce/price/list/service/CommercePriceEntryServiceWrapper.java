/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service;

import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link CommercePriceEntryService}.
 *
 * @author Alessio Antonio Rendina
 * @see CommercePriceEntryService
 * @generated
 */
public class CommercePriceEntryServiceWrapper
	implements CommercePriceEntryService,
			   ServiceWrapper<CommercePriceEntryService> {

	public CommercePriceEntryServiceWrapper() {
		this(null);
	}

	public CommercePriceEntryServiceWrapper(
		CommercePriceEntryService commercePriceEntryService) {

		_commercePriceEntryService = commercePriceEntryService;
	}

	@Override
	public CommercePriceEntry addCommercePriceEntry(
			String externalReferenceCode, long cpInstanceId,
			long commercePriceListId, java.math.BigDecimal price,
			boolean priceOnApplication, java.math.BigDecimal promoPrice,
			String unitOfMeasureKey,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceEntryService.addCommercePriceEntry(
			externalReferenceCode, cpInstanceId, commercePriceListId, price,
			priceOnApplication, promoPrice, unitOfMeasureKey, serviceContext);
	}

	@Override
	public CommercePriceEntry addCommercePriceEntry(
			String externalReferenceCode, long cProductId,
			String cpInstanceUuid, long commercePriceListId,
			java.math.BigDecimal price, boolean discountDiscovery,
			java.math.BigDecimal discountLevel1,
			java.math.BigDecimal discountLevel2,
			java.math.BigDecimal discountLevel3,
			java.math.BigDecimal discountLevel4, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, String unitOfMeasureKey,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceEntryService.addCommercePriceEntry(
			externalReferenceCode, cProductId, cpInstanceUuid,
			commercePriceListId, price, discountDiscovery, discountLevel1,
			discountLevel2, discountLevel3, discountLevel4, displayDateMonth,
			displayDateDay, displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			unitOfMeasureKey, serviceContext);
	}

	@Override
	public CommercePriceEntry addOrUpdateCommercePriceEntry(
			String externalReferenceCode, long commercePriceEntryId,
			long cProductId, String cpInstanceUuid, long commercePriceListId,
			java.math.BigDecimal price, java.math.BigDecimal promoPrice,
			String skuExternalReferenceCode, String unitOfMeasureKey,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceEntryService.addOrUpdateCommercePriceEntry(
			externalReferenceCode, commercePriceEntryId, cProductId,
			cpInstanceUuid, commercePriceListId, price, promoPrice,
			skuExternalReferenceCode, unitOfMeasureKey, serviceContext);
	}

	@Override
	public CommercePriceEntry addOrUpdateCommercePriceEntry(
			String externalReferenceCode, long commercePriceEntryId,
			long cProductId, String cpInstanceUuid, long commercePriceListId,
			boolean discountDiscovery, java.math.BigDecimal discountLevel1,
			java.math.BigDecimal discountLevel2,
			java.math.BigDecimal discountLevel3,
			java.math.BigDecimal discountLevel4, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, java.math.BigDecimal price,
			boolean priceOnApplication, String skuExternalReferenceCode,
			String unitOfMeasureKey,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceEntryService.addOrUpdateCommercePriceEntry(
			externalReferenceCode, commercePriceEntryId, cProductId,
			cpInstanceUuid, commercePriceListId, discountDiscovery,
			discountLevel1, discountLevel2, discountLevel3, discountLevel4,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire, price, priceOnApplication, skuExternalReferenceCode,
			unitOfMeasureKey, serviceContext);
	}

	@Override
	public void deleteCommercePriceEntry(long commercePriceEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commercePriceEntryService.deleteCommercePriceEntry(
			commercePriceEntryId);
	}

	@Override
	public CommercePriceEntry fetchCommercePriceEntry(long commercePriceEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceEntryService.fetchCommercePriceEntry(
			commercePriceEntryId);
	}

	@Override
	public CommercePriceEntry fetchCommercePriceEntryByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceEntryService.
			fetchCommercePriceEntryByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public java.util.List<CommercePriceEntry> getCommercePriceEntries(
			long commercePriceListId, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceEntryService.getCommercePriceEntries(
			commercePriceListId, start, end);
	}

	@Override
	public java.util.List<CommercePriceEntry> getCommercePriceEntries(
			long commercePriceListId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<CommercePriceEntry>
				orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceEntryService.getCommercePriceEntries(
			commercePriceListId, start, end, orderByComparator);
	}

	@Override
	public int getCommercePriceEntriesCount(long commercePriceListId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceEntryService.getCommercePriceEntriesCount(
			commercePriceListId);
	}

	@Override
	public CommercePriceEntry getCommercePriceEntry(long commercePriceEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceEntryService.getCommercePriceEntry(
			commercePriceEntryId);
	}

	@Override
	public CommercePriceEntry getInstanceBaseCommercePriceEntry(
		String cpInstanceUuid, String priceListType, String unitOfMeasureKey) {

		return _commercePriceEntryService.getInstanceBaseCommercePriceEntry(
			cpInstanceUuid, priceListType, unitOfMeasureKey);
	}

	@Override
	public java.util.List<CommercePriceEntry> getInstanceCommercePriceEntries(
			long cpInstanceId, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceEntryService.getInstanceCommercePriceEntries(
			cpInstanceId, start, end);
	}

	@Override
	public int getInstanceCommercePriceEntriesCount(long cpInstanceId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceEntryService.getInstanceCommercePriceEntriesCount(
			cpInstanceId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commercePriceEntryService.getOSGiServiceIdentifier();
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult
		<CommercePriceEntry> searchCommercePriceEntries(
				long companyId, long commercePriceListId, String keywords,
				int start, int end, com.liferay.portal.kernel.search.Sort sort)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceEntryService.searchCommercePriceEntries(
			companyId, commercePriceListId, keywords, start, end, sort);
	}

	@Override
	public int searchCommercePriceEntriesCount(
			long companyId, long commercePriceListId, String keywords)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceEntryService.searchCommercePriceEntriesCount(
			companyId, commercePriceListId, keywords);
	}

	@Override
	public CommercePriceEntry updateCommercePriceEntry(
			long commercePriceEntryId, boolean bulkPricing,
			boolean discountDiscovery, java.math.BigDecimal discountLevel1,
			java.math.BigDecimal discountLevel2,
			java.math.BigDecimal discountLevel3,
			java.math.BigDecimal discountLevel4, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, java.math.BigDecimal price,
			boolean priceOnApplication, String unitOfMeasureKey,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceEntryService.updateCommercePriceEntry(
			commercePriceEntryId, bulkPricing, discountDiscovery,
			discountLevel1, discountLevel2, discountLevel3, discountLevel4,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire, price, priceOnApplication, unitOfMeasureKey,
			serviceContext);
	}

	@Override
	public CommercePriceEntry updateExternalReferenceCode(
			CommercePriceEntry commercePriceEntry, String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceEntryService.updateExternalReferenceCode(
			commercePriceEntry, externalReferenceCode);
	}

	@Override
	public CommercePriceEntry updatePricingInfo(
			long commercePriceEntryId, boolean bulkPricing,
			java.math.BigDecimal price, boolean priceOnApplication,
			java.math.BigDecimal promoPrice, String unitOfMeasureKey,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceEntryService.updatePricingInfo(
			commercePriceEntryId, bulkPricing, price, priceOnApplication,
			promoPrice, unitOfMeasureKey, serviceContext);
	}

	@Override
	public CommercePriceEntryService getWrappedService() {
		return _commercePriceEntryService;
	}

	@Override
	public void setWrappedService(
		CommercePriceEntryService commercePriceEntryService) {

		_commercePriceEntryService = commercePriceEntryService;
	}

	private CommercePriceEntryService _commercePriceEntryService;

}
// LIFERAY-SERVICE-BUILDER-HASH:607813819