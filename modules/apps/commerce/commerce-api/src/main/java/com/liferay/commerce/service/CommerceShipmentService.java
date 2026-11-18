/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service;

import com.liferay.commerce.model.CommerceShipment;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
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
 * Provides the remote service interface for CommerceShipment. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceShipmentServiceUtil
 * @generated
 */
@AccessControlled
@JSONWebService
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface CommerceShipmentService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.commerce.service.impl.CommerceShipmentServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the commerce shipment remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link CommerceShipmentServiceUtil} if injection and service tracking are not available.
	 */
	public CommerceShipment addCommerceShipment(
			long commerceOrderId, ServiceContext serviceContext)
		throws PortalException;

	public CommerceShipment addCommerceShipment(
			String externalReferenceCode, long groupId, long commerceAccountId,
			long commerceAddressId, long commerceShippingMethodId,
			String commerceShippingOptionName, ServiceContext serviceContext)
		throws PortalException;

	public void deleteCommerceShipment(
			long commerceShipmentId, boolean restoreStockQuantity)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceShipment fetchCommerceShipmentByExternalReferenceCode(
			long companyId, String externalReferenceCode)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceShipment getCommerceShipment(long commerceShipmentId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommerceShipment getCommerceShipment(
			long groupId, long commerceShipmentId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceShipment> getCommerceShipments(
			long companyId, int status, int start, int end,
			OrderByComparator<CommerceShipment> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceShipment> getCommerceShipments(
			long companyId, int start, int end,
			OrderByComparator<CommerceShipment> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceShipment> getCommerceShipments(
			long companyId, long commerceAddressId, int start, int end,
			OrderByComparator<CommerceShipment> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceShipment> getCommerceShipments(
			long companyId, long[] groupIds, long[] commerceAccountIds,
			String keywords, int[] shipmentStatuses,
			boolean excludeShipmentStatus, int start, int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommerceShipment> getCommerceShipmentsByOrderId(
			long commerceOrderId, int start, int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceShipmentsCount(long companyId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceShipmentsCount(long companyId, int status)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceShipmentsCount(long companyId, long commerceAddressId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceShipmentsCount(
			long companyId, long[] groupIds, long[] commerceAccountIds,
			String keywords, int[] shipmentStatuses,
			boolean excludeShipmentStatus)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommerceShipmentsCountByOrderId(long commerceOrderId)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	public CommerceShipment reprocessCommerceShipment(long commerceShipmentId)
		throws PortalException;

	public CommerceShipment updateAddress(
			String externalReferenceCode, long commerceShipmentId, String name,
			String description, String street1, String street2, String street3,
			String city, String zip, long regionId, long countryId,
			String phoneNumber, ServiceContext serviceContext)
		throws PortalException;

	public CommerceShipment updateCarrierDetails(
			long commerceShipmentId, long commerceShippingMethodId,
			String carrier, String trackingNumber, String trackingURL)
		throws PortalException;

	public CommerceShipment updateCommerceShipment(
			CommerceShipment commerceShipment)
		throws PortalException;

	public CommerceShipment updateCommerceShipment(
			long commerceShipmentId, long commerceShippingMethodId,
			String carrier, int expectedDateMonth, int expectedDateDay,
			int expectedDateYear, int expectedDateHour, int expectedDateMinute,
			int shippingDateMonth, int shippingDateDay, int shippingDateYear,
			int shippingDateHour, int shippingDateMinute, String trackingNumber,
			String trackingURL, int status, ServiceContext serviceContext)
		throws PortalException;

	public CommerceShipment updateCommerceShipment(
			long commerceShipmentId, long commerceShippingMethodId,
			String carrier, int expectedDateMonth, int expectedDateDay,
			int expectedDateYear, int expectedDateHour, int expectedDateMinute,
			int shippingDateMonth, int shippingDateDay, int shippingDateYear,
			int shippingDateHour, int shippingDateMinute, String trackingNumber,
			String trackingURL, int status, String name, String description,
			String street1, String street2, String street3, String city,
			String zip, long regionId, long countryId, String phoneNumber,
			ServiceContext serviceContext)
		throws PortalException;

	public CommerceShipment updateExpectedDate(
			long commerceShipmentId, int expectedDateMonth, int expectedDateDay,
			int expectedDateYear, int expectedDateHour, int expectedDateMinute)
		throws PortalException;

	public CommerceShipment updateExternalReferenceCode(
			long commerceShipmentId, String externalReferenceCode)
		throws PortalException;

	public CommerceShipment updateShippingDate(
			long commerceShipmentId, int shippingDateMonth, int shippingDateDay,
			int shippingDateYear, int shippingDateHour, int shippingDateMinute)
		throws PortalException;

	public CommerceShipment updateStatus(long commerceShipmentId, int status)
		throws PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:840608334