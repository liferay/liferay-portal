/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service;

import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link CommercePriceListService}.
 *
 * @author Alessio Antonio Rendina
 * @see CommercePriceListService
 * @generated
 */
public class CommercePriceListServiceWrapper
	implements CommercePriceListService,
			   ServiceWrapper<CommercePriceListService> {

	public CommercePriceListServiceWrapper() {
		this(null);
	}

	public CommercePriceListServiceWrapper(
		CommercePriceListService commercePriceListService) {

		_commercePriceListService = commercePriceListService;
	}

	@Override
	public CommercePriceList addCommercePriceList(
			String externalReferenceCode, long groupId,
			boolean catalogBasePriceList, String commerceCurrencyCode,
			int displayDateDay, int displayDateHour, int displayDateMinute,
			int displayDateMonth, int displayDateYear, int expirationDateDay,
			int expirationDateHour, int expirationDateMinute,
			int expirationDateMonth, int expirationDateYear, String name,
			boolean netPrice, boolean neverExpire,
			long parentCommercePriceListId, double priority, String type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListService.addCommercePriceList(
			externalReferenceCode, groupId, catalogBasePriceList,
			commerceCurrencyCode, displayDateDay, displayDateHour,
			displayDateMinute, displayDateMonth, displayDateYear,
			expirationDateDay, expirationDateHour, expirationDateMinute,
			expirationDateMonth, expirationDateYear, name, netPrice,
			neverExpire, parentCommercePriceListId, priority, type,
			serviceContext);
	}

	@Override
	public CommercePriceList addOrUpdateCommercePriceList(
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
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListService.addOrUpdateCommercePriceList(
			externalReferenceCode, groupId, commercePriceListId,
			catalogBasePriceList, commerceCurrencyCode, displayDateDay,
			displayDateHour, displayDateMinute, displayDateMonth,
			displayDateYear, expirationDateDay, expirationDateHour,
			expirationDateMinute, expirationDateMonth, expirationDateYear, name,
			netPrice, neverExpire, parentCommercePriceListId, priority, type,
			serviceContext);
	}

	@Override
	public void deleteCommercePriceList(long commercePriceListId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commercePriceListService.deleteCommercePriceList(commercePriceListId);
	}

	@Override
	public CommercePriceList fetchCatalogBaseCommercePriceListByType(
			long groupId, String type)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListService.
			fetchCatalogBaseCommercePriceListByType(groupId, type);
	}

	@Override
	public CommercePriceList fetchCommercePriceList(long commercePriceListId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListService.fetchCommercePriceList(
			commercePriceListId);
	}

	@Override
	public CommercePriceList fetchCommercePriceListByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListService.
			fetchCommercePriceListByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public CommercePriceList getCommercePriceList(long commercePriceListId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListService.getCommercePriceList(
			commercePriceListId);
	}

	@Override
	public java.util.List<CommercePriceList> getCommercePriceLists(
			long companyId, int status, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<CommercePriceList>
				orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListService.getCommercePriceLists(
			companyId, status, start, end, orderByComparator);
	}

	@Override
	public java.util.List<CommercePriceList> getCommercePriceLists(
			long companyId, String type, int status, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<CommercePriceList>
				orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListService.getCommercePriceLists(
			companyId, type, status, start, end, orderByComparator);
	}

	@Override
	public int getCommercePriceListsCount(long companyId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListService.getCommercePriceListsCount(
			companyId, status);
	}

	@Override
	public int getCommercePriceListsCount(
			long commercePricingClassId, String title)
		throws com.liferay.portal.kernel.security.auth.PrincipalException {

		return _commercePriceListService.getCommercePriceListsCount(
			commercePricingClassId, title);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commercePriceListService.getOSGiServiceIdentifier();
	}

	@Override
	public java.util.List<CommercePriceList> searchByCommercePricingClassId(
			long commercePricingClassId, String name, int start, int end)
		throws com.liferay.portal.kernel.security.auth.PrincipalException {

		return _commercePriceListService.searchByCommercePricingClassId(
			commercePricingClassId, name, start, end);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult
		<CommercePriceList> searchCommercePriceLists(
				long companyId, String keywords, int status, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListService.searchCommercePriceLists(
			companyId, keywords, status, start, end, sort);
	}

	@Override
	public int searchCommercePriceListsCount(
			long companyId, String keywords, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListService.searchCommercePriceListsCount(
			companyId, keywords, status);
	}

	@Override
	public void setCatalogBasePriceList(
			long groupId, long commercePriceListId, String type)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commercePriceListService.setCatalogBasePriceList(
			groupId, commercePriceListId, type);
	}

	@Override
	public CommercePriceList updateCommercePriceList(
			long commercePriceListId, boolean catalogBasePriceList,
			String commerceCurrencyCode, int displayDateDay,
			int displayDateHour, int displayDateMinute, int displayDateMonth,
			int displayDateYear, int expirationDateDay, int expirationDateHour,
			int expirationDateMinute, int expirationDateMonth,
			int expirationDateYear, String name, boolean netPrice,
			boolean neverExpire, long parentCommercePriceListId,
			double priority, String type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListService.updateCommercePriceList(
			commercePriceListId, catalogBasePriceList, commerceCurrencyCode,
			displayDateDay, displayDateHour, displayDateMinute,
			displayDateMonth, displayDateYear, expirationDateDay,
			expirationDateHour, expirationDateMinute, expirationDateMonth,
			expirationDateYear, name, netPrice, neverExpire,
			parentCommercePriceListId, priority, type, serviceContext);
	}

	@Override
	public CommercePriceList updateExternalReferenceCode(
			CommercePriceList commercePriceList, String externalReferenceCode,
			long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceListService.updateExternalReferenceCode(
			commercePriceList, externalReferenceCode, companyId);
	}

	@Override
	public CommercePriceListService getWrappedService() {
		return _commercePriceListService;
	}

	@Override
	public void setWrappedService(
		CommercePriceListService commercePriceListService) {

		_commercePriceListService = commercePriceListService;
	}

	private CommercePriceListService _commercePriceListService;

}