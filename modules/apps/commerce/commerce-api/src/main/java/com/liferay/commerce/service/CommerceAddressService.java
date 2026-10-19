/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service;

import com.liferay.commerce.model.CommerceAddress;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.service.BaseService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the remote service interface for CommerceAddress. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceAddressServiceUtil
 * @generated
 */
@AccessControlled
@JSONWebService
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface CommerceAddressService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.commerce.service.impl.CommerceAddressServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the commerce address remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link CommerceAddressServiceUtil} if injection and service tracking are not available.
	 */
	public CommerceAddress addCommerceAddress(
			String externalReferenceCode, String className, long classPK,
			long countryId, long regionId, String city, String description,
			String name, String phoneNumber, String street1, String street2,
			String street3, String subtype, int type, String zip,
			ServiceContext serviceContext)
		throws PortalException;

	public void deleteCommerceAddress(long commerceAddressId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceAddress fetchCommerceAddress(long commerceAddressId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceAddress fetchCommerceAddressByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceAddress> getBillingCommerceAddresses(
			long companyId, String className, long classPK)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceAddress> getBillingCommerceAddresses(
			long channelId, String className, long classPK, int start, int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceAddress> getBillingCommerceAddresses(
			long companyId, String className, long classPK,
			long commerceChannelId, String keywords, int start, int end,
			Sort sort)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceAddress> getBillingCommerceAddressesCount(
			long channelId, String className, long classPK, int start, int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getBillingCommerceAddressesCount(
			long companyId, String className, long classPK,
			long commerceChannelId, String keywords)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceAddress getCommerceAddress(long commerceAddressId)
		throws PortalException;

	/**
	 * @deprecated As of Mueller (7.2.x), commerceAddress is scoped to Company use *ByCompanyId
	 */
	@Deprecated
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceAddress> getCommerceAddresses(
			long groupId, String className, long classPK)
		throws PortalException;

	/**
	 * @deprecated As of Mueller (7.2.x), commerceAddress is scoped to Company use *ByCompanyId
	 */
	@Deprecated
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceAddress> getCommerceAddresses(
			long groupId, String className, long classPK, int start, int end,
			OrderByComparator<CommerceAddress> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceAddress> getCommerceAddresses(
			String className, long classPK, int start, int end,
			OrderByComparator<CommerceAddress> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceAddress> getCommerceAddressesByCompanyId(
			long companyId, String className, long classPK)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceAddress> getCommerceAddressesByCompanyId(
			long companyId, String className, long classPK, int start, int end,
			OrderByComparator<CommerceAddress> orderByComparator)
		throws PortalException;

	/**
	 * @deprecated As of Mueller (7.2.x), commerceAddress is scoped to Company use *ByCompanyId
	 */
	@Deprecated
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceAddressesCount(
			long groupId, String className, long classPK)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceAddressesCount(String className, long classPK)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceAddressesCountByCompanyId(
			long companyId, String className, long classPK)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceAddress> getShippingCommerceAddresses(
			long companyId, String className, long classPK)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceAddress> getShippingCommerceAddresses(
			long channelId, String className, long classPK, int start, int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceAddress> getShippingCommerceAddresses(
			long companyId, String className, long classPK,
			long commerceChannelId, String keywords, int start, int end,
			Sort sort)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceAddress> getShippingCommerceAddressesCount(
			long channelId, String className, long classPK, int start, int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getShippingCommerceAddressesCount(
			long companyId, String className, long classPK,
			long commerceChannelId, String keywords)
		throws PortalException;

	/**
	 * @deprecated As of Mueller (7.2.x), commerceAddress is scoped to Company. Don't need to pass groupId
	 */
	@Deprecated
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BaseModelSearchResult<CommerceAddress> searchCommerceAddresses(
			long companyId, long groupId, String className, long classPK,
			String keywords, int start, int end, Sort sort)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BaseModelSearchResult<CommerceAddress> searchCommerceAddresses(
			long companyId, String className, long classPK, String keywords,
			int start, int end, Sort sort)
		throws PortalException;

	public CommerceAddress updateCommerceAddress(
			String externalReferenceCode, long commerceAddressId,
			long countryId, long regionId, String city, String description,
			String name, String phoneNumber, String street1, String street2,
			String street3, String subtype, int type, String zip,
			ServiceContext serviceContext)
		throws PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:1366753960