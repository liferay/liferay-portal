/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service;

import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

/**
 * Provides the remote service utility for CommercePriceEntry. This utility wraps
 * <code>com.liferay.commerce.price.list.service.impl.CommercePriceEntryServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Alessio Antonio Rendina
 * @see CommercePriceEntryService
 * @generated
 */
public class CommercePriceEntryServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.price.list.service.impl.CommercePriceEntryServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static CommercePriceEntry addCommercePriceEntry(
			String externalReferenceCode, long cpInstanceId,
			long commercePriceListId, java.math.BigDecimal price,
			boolean priceOnApplication, java.math.BigDecimal promoPrice,
			String unitOfMeasureKey,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommercePriceEntry(
			externalReferenceCode, cpInstanceId, commercePriceListId, price,
			priceOnApplication, promoPrice, unitOfMeasureKey, serviceContext);
	}

	public static CommercePriceEntry addCommercePriceEntry(
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
		throws PortalException {

		return getService().addCommercePriceEntry(
			externalReferenceCode, cProductId, cpInstanceUuid,
			commercePriceListId, price, discountDiscovery, discountLevel1,
			discountLevel2, discountLevel3, discountLevel4, displayDateMonth,
			displayDateDay, displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			unitOfMeasureKey, serviceContext);
	}

	public static CommercePriceEntry addOrUpdateCommercePriceEntry(
			String externalReferenceCode, long commercePriceEntryId,
			long cProductId, String cpInstanceUuid, long commercePriceListId,
			java.math.BigDecimal price, java.math.BigDecimal promoPrice,
			String skuExternalReferenceCode, String unitOfMeasureKey,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addOrUpdateCommercePriceEntry(
			externalReferenceCode, commercePriceEntryId, cProductId,
			cpInstanceUuid, commercePriceListId, price, promoPrice,
			skuExternalReferenceCode, unitOfMeasureKey, serviceContext);
	}

	public static CommercePriceEntry addOrUpdateCommercePriceEntry(
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
		throws PortalException {

		return getService().addOrUpdateCommercePriceEntry(
			externalReferenceCode, commercePriceEntryId, cProductId,
			cpInstanceUuid, commercePriceListId, discountDiscovery,
			discountLevel1, discountLevel2, discountLevel3, discountLevel4,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire, price, priceOnApplication, skuExternalReferenceCode,
			unitOfMeasureKey, serviceContext);
	}

	public static void deleteCommercePriceEntry(long commercePriceEntryId)
		throws PortalException {

		getService().deleteCommercePriceEntry(commercePriceEntryId);
	}

	public static CommercePriceEntry fetchCommercePriceEntry(
			long commercePriceEntryId)
		throws PortalException {

		return getService().fetchCommercePriceEntry(commercePriceEntryId);
	}

	public static CommercePriceEntry
			fetchCommercePriceEntryByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().fetchCommercePriceEntryByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	public static List<CommercePriceEntry> getCommercePriceEntries(
			long commercePriceListId, int start, int end)
		throws PortalException {

		return getService().getCommercePriceEntries(
			commercePriceListId, start, end);
	}

	public static List<CommercePriceEntry> getCommercePriceEntries(
			long commercePriceListId, int start, int end,
			OrderByComparator<CommercePriceEntry> orderByComparator)
		throws PortalException {

		return getService().getCommercePriceEntries(
			commercePriceListId, start, end, orderByComparator);
	}

	public static int getCommercePriceEntriesCount(long commercePriceListId)
		throws PortalException {

		return getService().getCommercePriceEntriesCount(commercePriceListId);
	}

	public static CommercePriceEntry getCommercePriceEntry(
			long commercePriceEntryId)
		throws PortalException {

		return getService().getCommercePriceEntry(commercePriceEntryId);
	}

	public static CommercePriceEntry getInstanceBaseCommercePriceEntry(
		String cpInstanceUuid, String priceListType, String unitOfMeasureKey) {

		return getService().getInstanceBaseCommercePriceEntry(
			cpInstanceUuid, priceListType, unitOfMeasureKey);
	}

	public static List<CommercePriceEntry> getInstanceCommercePriceEntries(
			long cpInstanceId, int start, int end)
		throws PortalException {

		return getService().getInstanceCommercePriceEntries(
			cpInstanceId, start, end);
	}

	public static int getInstanceCommercePriceEntriesCount(long cpInstanceId)
		throws PortalException {

		return getService().getInstanceCommercePriceEntriesCount(cpInstanceId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<CommercePriceEntry> searchCommercePriceEntries(
				long companyId, long commercePriceListId, String keywords,
				int start, int end, com.liferay.portal.kernel.search.Sort sort)
			throws PortalException {

		return getService().searchCommercePriceEntries(
			companyId, commercePriceListId, keywords, start, end, sort);
	}

	public static int searchCommercePriceEntriesCount(
			long companyId, long commercePriceListId, String keywords)
		throws PortalException {

		return getService().searchCommercePriceEntriesCount(
			companyId, commercePriceListId, keywords);
	}

	public static CommercePriceEntry updateCommercePriceEntry(
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
		throws PortalException {

		return getService().updateCommercePriceEntry(
			commercePriceEntryId, bulkPricing, discountDiscovery,
			discountLevel1, discountLevel2, discountLevel3, discountLevel4,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire, price, priceOnApplication, unitOfMeasureKey,
			serviceContext);
	}

	public static CommercePriceEntry updateExternalReferenceCode(
			CommercePriceEntry commercePriceEntry, String externalReferenceCode)
		throws PortalException {

		return getService().updateExternalReferenceCode(
			commercePriceEntry, externalReferenceCode);
	}

	public static CommercePriceEntry updatePricingInfo(
			long commercePriceEntryId, boolean bulkPricing,
			java.math.BigDecimal price, boolean priceOnApplication,
			java.math.BigDecimal promoPrice, String unitOfMeasureKey,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updatePricingInfo(
			commercePriceEntryId, bulkPricing, price, priceOnApplication,
			promoPrice, unitOfMeasureKey, serviceContext);
	}

	public static CommercePriceEntryService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommercePriceEntryService> _serviceSnapshot =
		new Snapshot<>(
			CommercePriceEntryServiceUtil.class,
			CommercePriceEntryService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1749449969