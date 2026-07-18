/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.impl;

import com.liferay.commerce.constants.CommerceActionKeys;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceShipment;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.commerce.service.base.CommerceShipmentServiceBaseImpl;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommerceShipment"
	},
	service = AopService.class
)
public class CommerceShipmentServiceImpl
	extends CommerceShipmentServiceBaseImpl {

	@Override
	public CommerceShipment addCommerceShipment(
			long commerceOrderId, ServiceContext serviceContext)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, ActionKeys.VIEW);

		_checkPortletResourcePermission(
			CommerceActionKeys.ADD_COMMERCE_SHIPMENT);

		return commerceShipmentLocalService.addCommerceShipment(
			commerceOrderId, serviceContext);
	}

	@Override
	public CommerceShipment addCommerceShipment(
			String externalReferenceCode, long groupId, long commerceAccountId,
			long commerceAddressId, long commerceShippingMethodId,
			String commerceShippingOptionName, ServiceContext serviceContext)
		throws PortalException {

		_checkPortletResourcePermission(
			CommerceActionKeys.ADD_COMMERCE_SHIPMENT);

		return commerceShipmentLocalService.addCommerceShipment(
			externalReferenceCode, groupId, commerceAccountId,
			commerceAddressId, commerceShippingMethodId,
			commerceShippingOptionName, serviceContext);
	}

	@Override
	public void deleteCommerceShipment(
			long commerceShipmentId, boolean restoreStockQuantity)
		throws PortalException {

		_commerceShipmentModelResourcePermission.check(
			getPermissionChecker(), commerceShipmentId, ActionKeys.DELETE);

		commerceShipmentLocalService.deleteCommerceShipment(
			commerceShipmentPersistence.findByPrimaryKey(commerceShipmentId),
			restoreStockQuantity);
	}

	@Override
	public CommerceShipment fetchCommerceShipmentByExternalReferenceCode(
			long companyId, String externalReferenceCode)
		throws PortalException {

		CommerceShipment commerceShipment =
			commerceShipmentLocalService.
				fetchCommerceShipmentByExternalReferenceCode(
					externalReferenceCode, companyId);

		if (commerceShipment != null) {
			_commerceShipmentModelResourcePermission.check(
				getPermissionChecker(),
				commerceShipment.getCommerceShipmentId(), ActionKeys.VIEW);
		}

		return commerceShipment;
	}

	@Override
	public CommerceShipment getCommerceShipment(long commerceShipmentId)
		throws PortalException {

		_commerceShipmentModelResourcePermission.check(
			getPermissionChecker(), commerceShipmentId, ActionKeys.VIEW);

		return commerceShipmentPersistence.findByPrimaryKey(commerceShipmentId);
	}

	@Override
	public List<CommerceShipment> getCommerceShipments(
			long companyId, int status, int start, int end,
			OrderByComparator<CommerceShipment> orderByComparator)
		throws PortalException {

		_checkPortletResourcePermission(
			CommerceActionKeys.VIEW_COMMERCE_SHIPMENTS);

		List<CommerceChannel> commerceChannels = _commerceChannelService.search(
			companyId);

		if (commerceChannels.isEmpty()) {
			return Collections.emptyList();
		}

		return commerceShipmentLocalService.getCommerceShipments(
			TransformUtil.transformToLongArray(
				commerceChannels, CommerceChannel::getGroupId),
			status, start, end, orderByComparator);
	}

	@Override
	public List<CommerceShipment> getCommerceShipments(
			long companyId, int start, int end,
			OrderByComparator<CommerceShipment> orderByComparator)
		throws PortalException {

		_checkPortletResourcePermission(
			CommerceActionKeys.VIEW_COMMERCE_SHIPMENTS);

		List<CommerceChannel> commerceChannels = _commerceChannelService.search(
			companyId);

		if (commerceChannels.isEmpty()) {
			return Collections.emptyList();
		}

		return commerceShipmentPersistence.findByGroupId(
			TransformUtil.transformToLongArray(
				commerceChannels, CommerceChannel::getGroupId),
			start, end, orderByComparator);
	}

	@Override
	public List<CommerceShipment> getCommerceShipments(
			long companyId, long commerceAddressId, int start, int end,
			OrderByComparator<CommerceShipment> orderByComparator)
		throws PortalException {

		_checkPortletResourcePermission(
			CommerceActionKeys.VIEW_COMMERCE_SHIPMENTS);

		List<CommerceChannel> commerceChannels = _commerceChannelService.search(
			companyId);

		if (commerceChannels.isEmpty()) {
			return Collections.emptyList();
		}

		return commerceShipmentLocalService.getCommerceShipments(
			TransformUtil.transformToLongArray(
				commerceChannels, CommerceChannel::getGroupId),
			commerceAddressId, start, end, orderByComparator);
	}

	@Override
	public List<CommerceShipment> getCommerceShipments(
			long companyId, long[] groupIds, long[] commerceAccountIds,
			String keywords, int[] shipmentStatuses,
			boolean excludeShipmentStatus, int start, int end)
		throws PortalException {

		_checkPortletResourcePermission(
			CommerceActionKeys.VIEW_COMMERCE_SHIPMENTS);

		return commerceShipmentLocalService.getCommerceShipments(
			companyId, groupIds, commerceAccountIds, keywords, shipmentStatuses,
			excludeShipmentStatus, start, end);
	}

	@Override
	public List<CommerceShipment> getCommerceShipmentsByOrderId(
			long commerceOrderId, int start, int end)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, ActionKeys.VIEW);

		_checkPortletResourcePermission(
			CommerceActionKeys.VIEW_COMMERCE_SHIPMENTS);

		return commerceShipmentLocalService.getCommerceShipments(
			commerceOrderId, start, end);
	}

	@Override
	public int getCommerceShipmentsCount(long companyId)
		throws PortalException {

		_checkPortletResourcePermission(
			CommerceActionKeys.VIEW_COMMERCE_SHIPMENTS);

		List<CommerceChannel> commerceChannels = _commerceChannelService.search(
			companyId);

		if (commerceChannels.isEmpty()) {
			return 0;
		}

		return commerceShipmentLocalService.getCommerceShipmentsCount(
			TransformUtil.transformToLongArray(
				commerceChannels, CommerceChannel::getGroupId));
	}

	@Override
	public int getCommerceShipmentsCount(long companyId, int status)
		throws PortalException {

		_checkPortletResourcePermission(
			CommerceActionKeys.VIEW_COMMERCE_SHIPMENTS);

		List<CommerceChannel> commerceChannels = _commerceChannelService.search(
			companyId);

		if (commerceChannels.isEmpty()) {
			return 0;
		}

		return commerceShipmentLocalService.getCommerceShipmentsCount(
			TransformUtil.transformToLongArray(
				commerceChannels, CommerceChannel::getGroupId),
			status);
	}

	@Override
	public int getCommerceShipmentsCount(long companyId, long commerceAddressId)
		throws PortalException {

		_checkPortletResourcePermission(
			CommerceActionKeys.VIEW_COMMERCE_SHIPMENTS);

		List<CommerceChannel> commerceChannels = _commerceChannelService.search(
			companyId);

		if (commerceChannels.isEmpty()) {
			return 0;
		}

		return commerceShipmentLocalService.getCommerceShipmentsCount(
			TransformUtil.transformToLongArray(
				commerceChannels, CommerceChannel::getGroupId),
			commerceAddressId);
	}

	@Override
	public int getCommerceShipmentsCount(
			long companyId, long[] groupIds, long[] commerceAccountIds,
			String keywords, int[] shipmentStatuses,
			boolean excludeShipmentStatus)
		throws PortalException {

		_checkPortletResourcePermission(
			CommerceActionKeys.VIEW_COMMERCE_SHIPMENTS);

		return commerceShipmentLocalService.getCommerceShipmentsCount(
			companyId, groupIds, commerceAccountIds, keywords, shipmentStatuses,
			excludeShipmentStatus);
	}

	@Override
	public int getCommerceShipmentsCountByOrderId(long commerceOrderId)
		throws PortalException {

		_commerceOrderModelResourcePermission.check(
			getPermissionChecker(), commerceOrderId, ActionKeys.VIEW);

		_checkPortletResourcePermission(
			CommerceActionKeys.VIEW_COMMERCE_SHIPMENTS);

		return commerceShipmentLocalService.getCommerceShipmentsCount(
			commerceOrderId);
	}

	@Override
	public CommerceShipment reprocessCommerceShipment(long commerceShipmentId)
		throws PortalException {

		_commerceShipmentModelResourcePermission.check(
			getPermissionChecker(), commerceShipmentId, ActionKeys.UPDATE);

		return commerceShipmentLocalService.reprocessCommerceShipment(
			commerceShipmentId);
	}

	@Override
	public CommerceShipment updateAddress(
			String externalReferenceCode, long commerceShipmentId, String name,
			String description, String street1, String street2, String street3,
			String city, String zip, long regionId, long countryId,
			String phoneNumber, ServiceContext serviceContext)
		throws PortalException {

		_commerceShipmentModelResourcePermission.check(
			getPermissionChecker(), commerceShipmentId, ActionKeys.UPDATE);

		return commerceShipmentLocalService.updateAddress(
			externalReferenceCode, commerceShipmentId, name, description,
			street1, street2, street3, city, zip, regionId, countryId,
			phoneNumber, serviceContext);
	}

	@Override
	public CommerceShipment updateCarrierDetails(
			long commerceShipmentId, long commerceShippingMethodId,
			String carrier, String trackingNumber, String trackingURL)
		throws PortalException {

		_commerceShipmentModelResourcePermission.check(
			getPermissionChecker(), commerceShipmentId, ActionKeys.UPDATE);

		return commerceShipmentLocalService.updateCarrierDetails(
			commerceShipmentId, commerceShippingMethodId, carrier,
			trackingNumber, trackingURL);
	}

	@Override
	public CommerceShipment updateCommerceShipment(
			CommerceShipment commerceShipment)
		throws PortalException {

		_commerceShipmentModelResourcePermission.check(
			getPermissionChecker(), commerceShipment.getCommerceShipmentId(),
			ActionKeys.UPDATE);

		return commerceShipmentLocalService.updateCommerceShipment(
			commerceShipment);
	}

	@Override
	public CommerceShipment updateCommerceShipment(
			long commerceShipmentId, long commerceShippingMethodId,
			String carrier, int expectedDateMonth, int expectedDateDay,
			int expectedDateYear, int expectedDateHour, int expectedDateMinute,
			int shippingDateMonth, int shippingDateDay, int shippingDateYear,
			int shippingDateHour, int shippingDateMinute, String trackingNumber,
			String trackingURL, int status, ServiceContext serviceContext)
		throws PortalException {

		_commerceShipmentModelResourcePermission.check(
			getPermissionChecker(), commerceShipmentId, ActionKeys.UPDATE);

		return commerceShipmentLocalService.updateCommerceShipment(
			commerceShipmentId, commerceShippingMethodId, carrier,
			expectedDateMonth, expectedDateDay, expectedDateYear,
			expectedDateHour, expectedDateMinute, shippingDateMonth,
			shippingDateDay, shippingDateYear, shippingDateHour,
			shippingDateMinute, trackingNumber, trackingURL, status,
			serviceContext);
	}

	@Override
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
		throws PortalException {

		_commerceShipmentModelResourcePermission.check(
			getPermissionChecker(), commerceShipmentId, ActionKeys.UPDATE);

		return commerceShipmentLocalService.updateCommerceShipment(
			commerceShipmentId, commerceShippingMethodId, carrier,
			expectedDateMonth, expectedDateDay, expectedDateYear,
			expectedDateHour, expectedDateMinute, shippingDateMonth,
			shippingDateDay, shippingDateYear, shippingDateHour,
			shippingDateMinute, trackingNumber, trackingURL, status, name,
			description, street1, street2, street3, city, zip, regionId,
			countryId, phoneNumber, serviceContext);
	}

	@Override
	public CommerceShipment updateExpectedDate(
			long commerceShipmentId, int expectedDateMonth, int expectedDateDay,
			int expectedDateYear, int expectedDateHour, int expectedDateMinute)
		throws PortalException {

		_commerceShipmentModelResourcePermission.check(
			getPermissionChecker(), commerceShipmentId, ActionKeys.UPDATE);

		return commerceShipmentLocalService.updateExpectedDate(
			commerceShipmentId, expectedDateMonth, expectedDateDay,
			expectedDateYear, expectedDateHour, expectedDateMinute);
	}

	@Override
	public CommerceShipment updateExternalReferenceCode(
			long commerceShipmentId, String externalReferenceCode)
		throws PortalException {

		_commerceShipmentModelResourcePermission.check(
			getPermissionChecker(), commerceShipmentId, ActionKeys.UPDATE);

		return commerceShipmentLocalService.updateExternalReferenceCode(
			commerceShipmentId, externalReferenceCode);
	}

	@Override
	public CommerceShipment updateShippingDate(
			long commerceShipmentId, int shippingDateMonth, int shippingDateDay,
			int shippingDateYear, int shippingDateHour, int shippingDateMinute)
		throws PortalException {

		_commerceShipmentModelResourcePermission.check(
			getPermissionChecker(), commerceShipmentId, ActionKeys.UPDATE);

		return commerceShipmentLocalService.updateShippingDate(
			commerceShipmentId, shippingDateMonth, shippingDateDay,
			shippingDateYear, shippingDateHour, shippingDateMinute);
	}

	@Override
	public CommerceShipment updateStatus(long commerceShipmentId, int status)
		throws PortalException {

		_commerceShipmentModelResourcePermission.check(
			getPermissionChecker(), commerceShipmentId, ActionKeys.UPDATE);

		return commerceShipmentLocalService.updateStatus(
			commerceShipmentId, status);
	}

	private void _checkPortletResourcePermission(String actionId)
		throws PortalException {

		PortletResourcePermission portletResourcePermission =
			_commerceShipmentModelResourcePermission.
				getPortletResourcePermission();

		portletResourcePermission.check(getPermissionChecker(), null, actionId);
	}

	@Reference
	private CommerceChannelService _commerceChannelService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.model.CommerceOrder)"
	)
	private ModelResourcePermission<CommerceOrder>
		_commerceOrderModelResourcePermission;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.model.CommerceShipment)"
	)
	private ModelResourcePermission<CommerceShipment>
		_commerceShipmentModelResourcePermission;

}