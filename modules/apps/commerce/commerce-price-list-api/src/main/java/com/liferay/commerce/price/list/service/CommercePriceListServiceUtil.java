/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service;

import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

/**
 * Provides the remote service utility for CommercePriceList. This utility wraps
 * <code>com.liferay.commerce.price.list.service.impl.CommercePriceListServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Alessio Antonio Rendina
 * @see CommercePriceListService
 * @generated
 */
public class CommercePriceListServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.price.list.service.impl.CommercePriceListServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static CommercePriceList addCommercePriceList(
			String externalReferenceCode, long groupId,
			boolean catalogBasePriceList, String commerceCurrencyCode,
			int displayDateDay, int displayDateHour, int displayDateMinute,
			int displayDateMonth, int displayDateYear, int expirationDateDay,
			int expirationDateHour, int expirationDateMinute,
			int expirationDateMonth, int expirationDateYear, String name,
			boolean netPrice, boolean neverExpire,
			long parentCommercePriceListId, double priority, String type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommercePriceList(
			externalReferenceCode, groupId, catalogBasePriceList,
			commerceCurrencyCode, displayDateDay, displayDateHour,
			displayDateMinute, displayDateMonth, displayDateYear,
			expirationDateDay, expirationDateHour, expirationDateMinute,
			expirationDateMonth, expirationDateYear, name, netPrice,
			neverExpire, parentCommercePriceListId, priority, type,
			serviceContext);
	}

	public static CommercePriceList addOrUpdateCommercePriceList(
			String externalReferenceCode, long groupId,
			long commercePriceListId, boolean catalogBasePriceList,
			String commerceCurrencyCode, int displayDateDay,
			int displayDateHour, int displayDateMinute, int displayDateMonth,
			int displayDateYear, int expirationDateDay, int expirationDateHour,
			int expirationDateMinute, int expirationDateMonth,
			int expirationDateYear, String name, boolean netPrice,
			boolean neverExpire, long parentCommercePriceListId,
			double priority, String type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addOrUpdateCommercePriceList(
			externalReferenceCode, groupId, commercePriceListId,
			catalogBasePriceList, commerceCurrencyCode, displayDateDay,
			displayDateHour, displayDateMinute, displayDateMonth,
			displayDateYear, expirationDateDay, expirationDateHour,
			expirationDateMinute, expirationDateMonth, expirationDateYear, name,
			netPrice, neverExpire, parentCommercePriceListId, priority, type,
			serviceContext);
	}

	public static void deleteCommercePriceList(long commercePriceListId)
		throws PortalException {

		getService().deleteCommercePriceList(commercePriceListId);
	}

	public static CommercePriceList fetchCatalogBaseCommercePriceListByType(
			long groupId, String type)
		throws PortalException {

		return getService().fetchCatalogBaseCommercePriceListByType(
			groupId, type);
	}

	public static CommercePriceList fetchCommercePriceList(
			long commercePriceListId)
		throws PortalException {

		return getService().fetchCommercePriceList(commercePriceListId);
	}

	public static CommercePriceList
			fetchCommercePriceListByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().fetchCommercePriceListByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	public static CommercePriceList getCommercePriceList(
			long commercePriceListId)
		throws PortalException {

		return getService().getCommercePriceList(commercePriceListId);
	}

	public static List<CommercePriceList> getCommercePriceLists(
			long companyId, int status, int start, int end,
			OrderByComparator<CommercePriceList> orderByComparator)
		throws PortalException {

		return getService().getCommercePriceLists(
			companyId, status, start, end, orderByComparator);
	}

	public static List<CommercePriceList> getCommercePriceLists(
			long companyId, String type, int status, int start, int end,
			OrderByComparator<CommercePriceList> orderByComparator)
		throws PortalException {

		return getService().getCommercePriceLists(
			companyId, type, status, start, end, orderByComparator);
	}

	public static int getCommercePriceListsCount(long companyId, int status)
		throws PortalException {

		return getService().getCommercePriceListsCount(companyId, status);
	}

	public static int getCommercePriceListsCount(
			long commercePricingClassId, String title)
		throws com.liferay.portal.kernel.security.auth.PrincipalException {

		return getService().getCommercePriceListsCount(
			commercePricingClassId, title);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static List<CommercePriceList> searchByCommercePricingClassId(
			long commercePricingClassId, String name, int start, int end)
		throws com.liferay.portal.kernel.security.auth.PrincipalException {

		return getService().searchByCommercePricingClassId(
			commercePricingClassId, name, start, end);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<CommercePriceList> searchCommercePriceLists(
				long companyId, String keywords, int status, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
			throws PortalException {

		return getService().searchCommercePriceLists(
			companyId, keywords, status, start, end, sort);
	}

	public static int searchCommercePriceListsCount(
			long companyId, String keywords, int status)
		throws PortalException {

		return getService().searchCommercePriceListsCount(
			companyId, keywords, status);
	}

	public static void setCatalogBasePriceList(
			long groupId, long commercePriceListId, String type)
		throws PortalException {

		getService().setCatalogBasePriceList(
			groupId, commercePriceListId, type);
	}

	public static CommercePriceList updateCommercePriceList(
			long commercePriceListId, boolean catalogBasePriceList,
			String commerceCurrencyCode, int displayDateDay,
			int displayDateHour, int displayDateMinute, int displayDateMonth,
			int displayDateYear, int expirationDateDay, int expirationDateHour,
			int expirationDateMinute, int expirationDateMonth,
			int expirationDateYear, String name, boolean netPrice,
			boolean neverExpire, long parentCommercePriceListId,
			double priority, String type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCommercePriceList(
			commercePriceListId, catalogBasePriceList, commerceCurrencyCode,
			displayDateDay, displayDateHour, displayDateMinute,
			displayDateMonth, displayDateYear, expirationDateDay,
			expirationDateHour, expirationDateMinute, expirationDateMonth,
			expirationDateYear, name, netPrice, neverExpire,
			parentCommercePriceListId, priority, type, serviceContext);
	}

	public static CommercePriceList updateExternalReferenceCode(
			CommercePriceList commercePriceList, String externalReferenceCode,
			long companyId)
		throws PortalException {

		return getService().updateExternalReferenceCode(
			commercePriceList, externalReferenceCode, companyId);
	}

	public static CommercePriceListService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommercePriceListService> _serviceSnapshot =
		new Snapshot<>(
			CommercePriceListServiceUtil.class, CommercePriceListService.class);

}