/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service;

import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.BaseService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the remote service interface for CommercePriceList. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Alessio Antonio Rendina
 * @see CommercePriceListServiceUtil
 * @generated
 */
@AccessControlled
@CTAware
@JSONWebService
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface CommercePriceListService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.commerce.price.list.service.impl.CommercePriceListServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the commerce price list remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link CommercePriceListServiceUtil} if injection and service tracking are not available.
	 */
	public CommercePriceList addCommercePriceList(
			String externalReferenceCode, long groupId,
			boolean catalogBasePriceList, String commerceCurrencyCode,
			int displayDateDay, int displayDateHour, int displayDateMinute,
			int displayDateMonth, int displayDateYear, int expirationDateDay,
			int expirationDateHour, int expirationDateMinute,
			int expirationDateMonth, int expirationDateYear, String name,
			boolean netPrice, boolean neverExpire,
			long parentCommercePriceListId, double priority, String type,
			ServiceContext serviceContext)
		throws PortalException;

	public CommercePriceList addOrUpdateCommercePriceList(
			String externalReferenceCode, long groupId,
			long commercePriceListId, boolean catalogBasePriceList,
			String commerceCurrencyCode, int displayDateDay,
			int displayDateHour, int displayDateMinute, int displayDateMonth,
			int displayDateYear, int expirationDateDay, int expirationDateHour,
			int expirationDateMinute, int expirationDateMonth,
			int expirationDateYear, String name, boolean netPrice,
			boolean neverExpire, long parentCommercePriceListId,
			double priority, String type, ServiceContext serviceContext)
		throws PortalException;

	public void deleteCommercePriceList(long commercePriceListId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommercePriceList fetchCatalogBaseCommercePriceListByType(
			long groupId, String type)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommercePriceList fetchCommercePriceList(long commercePriceListId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommercePriceList fetchCommercePriceListByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommercePriceList getCommercePriceList(long commercePriceListId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommercePriceList> getCommercePriceLists(
			long companyId, int status, int start, int end,
			OrderByComparator<CommercePriceList> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommercePriceList> getCommercePriceLists(
			long companyId, String type, int status, int start, int end,
			OrderByComparator<CommercePriceList> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommercePriceListsCount(long companyId, int status)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommercePriceListsCount(
			long commercePricingClassId, String title)
		throws PrincipalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommercePriceList> searchByCommercePricingClassId(
			long commercePricingClassId, String name, int start, int end)
		throws PrincipalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BaseModelSearchResult<CommercePriceList> searchCommercePriceLists(
			long companyId, String keywords, int status, int start, int end,
			Sort sort)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int searchCommercePriceListsCount(
			long companyId, String keywords, int status)
		throws PortalException;

	public void setCatalogBasePriceList(
			long groupId, long commercePriceListId, String type)
		throws PortalException;

	public CommercePriceList updateCommercePriceList(
			long commercePriceListId, boolean catalogBasePriceList,
			String commerceCurrencyCode, int displayDateDay,
			int displayDateHour, int displayDateMinute, int displayDateMonth,
			int displayDateYear, int expirationDateDay, int expirationDateHour,
			int expirationDateMinute, int expirationDateMonth,
			int expirationDateYear, String name, boolean netPrice,
			boolean neverExpire, long parentCommercePriceListId,
			double priority, String type, ServiceContext serviceContext)
		throws PortalException;

	public CommercePriceList updateExternalReferenceCode(
			CommercePriceList commercePriceList, String externalReferenceCode,
			long companyId)
		throws PortalException;

}