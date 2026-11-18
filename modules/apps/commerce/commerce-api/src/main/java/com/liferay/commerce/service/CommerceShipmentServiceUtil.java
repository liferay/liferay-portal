/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service;

import com.liferay.commerce.model.CommerceShipment;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

/**
 * Provides the remote service utility for CommerceShipment. This utility wraps
 * <code>com.liferay.commerce.service.impl.CommerceShipmentServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceShipmentService
 * @generated
 */
public class CommerceShipmentServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.service.impl.CommerceShipmentServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static CommerceShipment addCommerceShipment(
			long commerceOrderId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommerceShipment(
			commerceOrderId, serviceContext);
	}

	public static CommerceShipment addCommerceShipment(
			String externalReferenceCode, long groupId, long commerceAccountId,
			long commerceAddressId, long commerceShippingMethodId,
			String commerceShippingOptionName,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommerceShipment(
			externalReferenceCode, groupId, commerceAccountId,
			commerceAddressId, commerceShippingMethodId,
			commerceShippingOptionName, serviceContext);
	}

	public static void deleteCommerceShipment(
			long commerceShipmentId, boolean restoreStockQuantity)
		throws PortalException {

		getService().deleteCommerceShipment(
			commerceShipmentId, restoreStockQuantity);
	}

	public static CommerceShipment fetchCommerceShipmentByExternalReferenceCode(
			long companyId, String externalReferenceCode)
		throws PortalException {

		return getService().fetchCommerceShipmentByExternalReferenceCode(
			companyId, externalReferenceCode);
	}

	public static CommerceShipment getCommerceShipment(long commerceShipmentId)
		throws PortalException {

		return getService().getCommerceShipment(commerceShipmentId);
	}

	public static CommerceShipment getCommerceShipment(
			long groupId, long commerceShipmentId)
		throws PortalException {

		return getService().getCommerceShipment(groupId, commerceShipmentId);
	}

	public static List<CommerceShipment> getCommerceShipments(
			long companyId, int status, int start, int end,
			OrderByComparator<CommerceShipment> orderByComparator)
		throws PortalException {

		return getService().getCommerceShipments(
			companyId, status, start, end, orderByComparator);
	}

	public static List<CommerceShipment> getCommerceShipments(
			long companyId, int start, int end,
			OrderByComparator<CommerceShipment> orderByComparator)
		throws PortalException {

		return getService().getCommerceShipments(
			companyId, start, end, orderByComparator);
	}

	public static List<CommerceShipment> getCommerceShipments(
			long companyId, long commerceAddressId, int start, int end,
			OrderByComparator<CommerceShipment> orderByComparator)
		throws PortalException {

		return getService().getCommerceShipments(
			companyId, commerceAddressId, start, end, orderByComparator);
	}

	public static List<CommerceShipment> getCommerceShipments(
			long companyId, long[] groupIds, long[] commerceAccountIds,
			String keywords, int[] shipmentStatuses,
			boolean excludeShipmentStatus, int start, int end)
		throws PortalException {

		return getService().getCommerceShipments(
			companyId, groupIds, commerceAccountIds, keywords, shipmentStatuses,
			excludeShipmentStatus, start, end);
	}

	public static List<CommerceShipment> getCommerceShipmentsByOrderId(
			long commerceOrderId, int start, int end)
		throws PortalException {

		return getService().getCommerceShipmentsByOrderId(
			commerceOrderId, start, end);
	}

	public static int getCommerceShipmentsCount(long companyId)
		throws PortalException {

		return getService().getCommerceShipmentsCount(companyId);
	}

	public static int getCommerceShipmentsCount(long companyId, int status)
		throws PortalException {

		return getService().getCommerceShipmentsCount(companyId, status);
	}

	public static int getCommerceShipmentsCount(
			long companyId, long commerceAddressId)
		throws PortalException {

		return getService().getCommerceShipmentsCount(
			companyId, commerceAddressId);
	}

	public static int getCommerceShipmentsCount(
			long companyId, long[] groupIds, long[] commerceAccountIds,
			String keywords, int[] shipmentStatuses,
			boolean excludeShipmentStatus)
		throws PortalException {

		return getService().getCommerceShipmentsCount(
			companyId, groupIds, commerceAccountIds, keywords, shipmentStatuses,
			excludeShipmentStatus);
	}

	public static int getCommerceShipmentsCountByOrderId(long commerceOrderId)
		throws PortalException {

		return getService().getCommerceShipmentsCountByOrderId(commerceOrderId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static CommerceShipment reprocessCommerceShipment(
			long commerceShipmentId)
		throws PortalException {

		return getService().reprocessCommerceShipment(commerceShipmentId);
	}

	public static CommerceShipment updateAddress(
			String externalReferenceCode, long commerceShipmentId, String name,
			String description, String street1, String street2, String street3,
			String city, String zip, long regionId, long countryId,
			String phoneNumber,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateAddress(
			externalReferenceCode, commerceShipmentId, name, description,
			street1, street2, street3, city, zip, regionId, countryId,
			phoneNumber, serviceContext);
	}

	public static CommerceShipment updateCarrierDetails(
			long commerceShipmentId, long commerceShippingMethodId,
			String carrier, String trackingNumber, String trackingURL)
		throws PortalException {

		return getService().updateCarrierDetails(
			commerceShipmentId, commerceShippingMethodId, carrier,
			trackingNumber, trackingURL);
	}

	public static CommerceShipment updateCommerceShipment(
			CommerceShipment commerceShipment)
		throws PortalException {

		return getService().updateCommerceShipment(commerceShipment);
	}

	public static CommerceShipment updateCommerceShipment(
			long commerceShipmentId, long commerceShippingMethodId,
			String carrier, int expectedDateMonth, int expectedDateDay,
			int expectedDateYear, int expectedDateHour, int expectedDateMinute,
			int shippingDateMonth, int shippingDateDay, int shippingDateYear,
			int shippingDateHour, int shippingDateMinute, String trackingNumber,
			String trackingURL, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCommerceShipment(
			commerceShipmentId, commerceShippingMethodId, carrier,
			expectedDateMonth, expectedDateDay, expectedDateYear,
			expectedDateHour, expectedDateMinute, shippingDateMonth,
			shippingDateDay, shippingDateYear, shippingDateHour,
			shippingDateMinute, trackingNumber, trackingURL, status,
			serviceContext);
	}

	public static CommerceShipment updateCommerceShipment(
			long commerceShipmentId, long commerceShippingMethodId,
			String carrier, int expectedDateMonth, int expectedDateDay,
			int expectedDateYear, int expectedDateHour, int expectedDateMinute,
			int shippingDateMonth, int shippingDateDay, int shippingDateYear,
			int shippingDateHour, int shippingDateMinute, String trackingNumber,
			String trackingURL, int status, String name, String description,
			String street1, String street2, String street3, String city,
			String zip, long regionId, long countryId, String phoneNumber,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCommerceShipment(
			commerceShipmentId, commerceShippingMethodId, carrier,
			expectedDateMonth, expectedDateDay, expectedDateYear,
			expectedDateHour, expectedDateMinute, shippingDateMonth,
			shippingDateDay, shippingDateYear, shippingDateHour,
			shippingDateMinute, trackingNumber, trackingURL, status, name,
			description, street1, street2, street3, city, zip, regionId,
			countryId, phoneNumber, serviceContext);
	}

	public static CommerceShipment updateExpectedDate(
			long commerceShipmentId, int expectedDateMonth, int expectedDateDay,
			int expectedDateYear, int expectedDateHour, int expectedDateMinute)
		throws PortalException {

		return getService().updateExpectedDate(
			commerceShipmentId, expectedDateMonth, expectedDateDay,
			expectedDateYear, expectedDateHour, expectedDateMinute);
	}

	public static CommerceShipment updateExternalReferenceCode(
			long commerceShipmentId, String externalReferenceCode)
		throws PortalException {

		return getService().updateExternalReferenceCode(
			commerceShipmentId, externalReferenceCode);
	}

	public static CommerceShipment updateShippingDate(
			long commerceShipmentId, int shippingDateMonth, int shippingDateDay,
			int shippingDateYear, int shippingDateHour, int shippingDateMinute)
		throws PortalException {

		return getService().updateShippingDate(
			commerceShipmentId, shippingDateMonth, shippingDateDay,
			shippingDateYear, shippingDateHour, shippingDateMinute);
	}

	public static CommerceShipment updateStatus(
			long commerceShipmentId, int status)
		throws PortalException {

		return getService().updateStatus(commerceShipmentId, status);
	}

	public static CommerceShipmentService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceShipmentService> _serviceSnapshot =
		new Snapshot<>(
			CommerceShipmentServiceUtil.class, CommerceShipmentService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:690206363