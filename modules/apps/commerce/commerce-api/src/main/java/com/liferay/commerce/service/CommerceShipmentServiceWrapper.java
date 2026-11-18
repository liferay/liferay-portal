/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link CommerceShipmentService}.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceShipmentService
 * @generated
 */
public class CommerceShipmentServiceWrapper
	implements CommerceShipmentService,
			   ServiceWrapper<CommerceShipmentService> {

	public CommerceShipmentServiceWrapper() {
		this(null);
	}

	public CommerceShipmentServiceWrapper(
		CommerceShipmentService commerceShipmentService) {

		_commerceShipmentService = commerceShipmentService;
	}

	@Override
	public com.liferay.commerce.model.CommerceShipment addCommerceShipment(
			long commerceOrderId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentService.addCommerceShipment(
			commerceOrderId, serviceContext);
	}

	@Override
	public com.liferay.commerce.model.CommerceShipment addCommerceShipment(
			String externalReferenceCode, long groupId, long commerceAccountId,
			long commerceAddressId, long commerceShippingMethodId,
			String commerceShippingOptionName,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentService.addCommerceShipment(
			externalReferenceCode, groupId, commerceAccountId,
			commerceAddressId, commerceShippingMethodId,
			commerceShippingOptionName, serviceContext);
	}

	@Override
	public void deleteCommerceShipment(
			long commerceShipmentId, boolean restoreStockQuantity)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceShipmentService.deleteCommerceShipment(
			commerceShipmentId, restoreStockQuantity);
	}

	@Override
	public com.liferay.commerce.model.CommerceShipment
			fetchCommerceShipmentByExternalReferenceCode(
				long companyId, String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentService.
			fetchCommerceShipmentByExternalReferenceCode(
				companyId, externalReferenceCode);
	}

	@Override
	public com.liferay.commerce.model.CommerceShipment getCommerceShipment(
			long commerceShipmentId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentService.getCommerceShipment(commerceShipmentId);
	}

	@Override
	public com.liferay.commerce.model.CommerceShipment getCommerceShipment(
			long groupId, long commerceShipmentId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentService.getCommerceShipment(
			groupId, commerceShipmentId);
	}

	@Override
	public java.util.List<com.liferay.commerce.model.CommerceShipment>
			getCommerceShipments(
				long companyId, int status, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.commerce.model.CommerceShipment>
						orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentService.getCommerceShipments(
			companyId, status, start, end, orderByComparator);
	}

	@Override
	public java.util.List<com.liferay.commerce.model.CommerceShipment>
			getCommerceShipments(
				long companyId, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.commerce.model.CommerceShipment>
						orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentService.getCommerceShipments(
			companyId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<com.liferay.commerce.model.CommerceShipment>
			getCommerceShipments(
				long companyId, long commerceAddressId, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.commerce.model.CommerceShipment>
						orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentService.getCommerceShipments(
			companyId, commerceAddressId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<com.liferay.commerce.model.CommerceShipment>
			getCommerceShipments(
				long companyId, long[] groupIds, long[] commerceAccountIds,
				String keywords, int[] shipmentStatuses,
				boolean excludeShipmentStatus, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentService.getCommerceShipments(
			companyId, groupIds, commerceAccountIds, keywords, shipmentStatuses,
			excludeShipmentStatus, start, end);
	}

	@Override
	public java.util.List<com.liferay.commerce.model.CommerceShipment>
			getCommerceShipmentsByOrderId(
				long commerceOrderId, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentService.getCommerceShipmentsByOrderId(
			commerceOrderId, start, end);
	}

	@Override
	public int getCommerceShipmentsCount(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentService.getCommerceShipmentsCount(companyId);
	}

	@Override
	public int getCommerceShipmentsCount(long companyId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentService.getCommerceShipmentsCount(
			companyId, status);
	}

	@Override
	public int getCommerceShipmentsCount(long companyId, long commerceAddressId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentService.getCommerceShipmentsCount(
			companyId, commerceAddressId);
	}

	@Override
	public int getCommerceShipmentsCount(
			long companyId, long[] groupIds, long[] commerceAccountIds,
			String keywords, int[] shipmentStatuses,
			boolean excludeShipmentStatus)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentService.getCommerceShipmentsCount(
			companyId, groupIds, commerceAccountIds, keywords, shipmentStatuses,
			excludeShipmentStatus);
	}

	@Override
	public int getCommerceShipmentsCountByOrderId(long commerceOrderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentService.getCommerceShipmentsCountByOrderId(
			commerceOrderId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commerceShipmentService.getOSGiServiceIdentifier();
	}

	@Override
	public com.liferay.commerce.model.CommerceShipment
			reprocessCommerceShipment(long commerceShipmentId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentService.reprocessCommerceShipment(
			commerceShipmentId);
	}

	@Override
	public com.liferay.commerce.model.CommerceShipment updateAddress(
			String externalReferenceCode, long commerceShipmentId, String name,
			String description, String street1, String street2, String street3,
			String city, String zip, long regionId, long countryId,
			String phoneNumber,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentService.updateAddress(
			externalReferenceCode, commerceShipmentId, name, description,
			street1, street2, street3, city, zip, regionId, countryId,
			phoneNumber, serviceContext);
	}

	@Override
	public com.liferay.commerce.model.CommerceShipment updateCarrierDetails(
			long commerceShipmentId, long commerceShippingMethodId,
			String carrier, String trackingNumber, String trackingURL)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentService.updateCarrierDetails(
			commerceShipmentId, commerceShippingMethodId, carrier,
			trackingNumber, trackingURL);
	}

	@Override
	public com.liferay.commerce.model.CommerceShipment updateCommerceShipment(
			com.liferay.commerce.model.CommerceShipment commerceShipment)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentService.updateCommerceShipment(
			commerceShipment);
	}

	@Override
	public com.liferay.commerce.model.CommerceShipment updateCommerceShipment(
			long commerceShipmentId, long commerceShippingMethodId,
			String carrier, int expectedDateMonth, int expectedDateDay,
			int expectedDateYear, int expectedDateHour, int expectedDateMinute,
			int shippingDateMonth, int shippingDateDay, int shippingDateYear,
			int shippingDateHour, int shippingDateMinute, String trackingNumber,
			String trackingURL, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentService.updateCommerceShipment(
			commerceShipmentId, commerceShippingMethodId, carrier,
			expectedDateMonth, expectedDateDay, expectedDateYear,
			expectedDateHour, expectedDateMinute, shippingDateMonth,
			shippingDateDay, shippingDateYear, shippingDateHour,
			shippingDateMinute, trackingNumber, trackingURL, status,
			serviceContext);
	}

	@Override
	public com.liferay.commerce.model.CommerceShipment updateCommerceShipment(
			long commerceShipmentId, long commerceShippingMethodId,
			String carrier, int expectedDateMonth, int expectedDateDay,
			int expectedDateYear, int expectedDateHour, int expectedDateMinute,
			int shippingDateMonth, int shippingDateDay, int shippingDateYear,
			int shippingDateHour, int shippingDateMinute, String trackingNumber,
			String trackingURL, int status, String name, String description,
			String street1, String street2, String street3, String city,
			String zip, long regionId, long countryId, String phoneNumber,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentService.updateCommerceShipment(
			commerceShipmentId, commerceShippingMethodId, carrier,
			expectedDateMonth, expectedDateDay, expectedDateYear,
			expectedDateHour, expectedDateMinute, shippingDateMonth,
			shippingDateDay, shippingDateYear, shippingDateHour,
			shippingDateMinute, trackingNumber, trackingURL, status, name,
			description, street1, street2, street3, city, zip, regionId,
			countryId, phoneNumber, serviceContext);
	}

	@Override
	public com.liferay.commerce.model.CommerceShipment updateExpectedDate(
			long commerceShipmentId, int expectedDateMonth, int expectedDateDay,
			int expectedDateYear, int expectedDateHour, int expectedDateMinute)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentService.updateExpectedDate(
			commerceShipmentId, expectedDateMonth, expectedDateDay,
			expectedDateYear, expectedDateHour, expectedDateMinute);
	}

	@Override
	public com.liferay.commerce.model.CommerceShipment
			updateExternalReferenceCode(
				long commerceShipmentId, String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentService.updateExternalReferenceCode(
			commerceShipmentId, externalReferenceCode);
	}

	@Override
	public com.liferay.commerce.model.CommerceShipment updateShippingDate(
			long commerceShipmentId, int shippingDateMonth, int shippingDateDay,
			int shippingDateYear, int shippingDateHour, int shippingDateMinute)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentService.updateShippingDate(
			commerceShipmentId, shippingDateMonth, shippingDateDay,
			shippingDateYear, shippingDateHour, shippingDateMinute);
	}

	@Override
	public com.liferay.commerce.model.CommerceShipment updateStatus(
			long commerceShipmentId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceShipmentService.updateStatus(
			commerceShipmentId, status);
	}

	@Override
	public CommerceShipmentService getWrappedService() {
		return _commerceShipmentService;
	}

	@Override
	public void setWrappedService(
		CommerceShipmentService commerceShipmentService) {

		_commerceShipmentService = commerceShipmentService;
	}

	private CommerceShipmentService _commerceShipmentService;

}
// LIFERAY-SERVICE-BUILDER-HASH:965836711