/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link CommerceOrderTypeService}.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceOrderTypeService
 * @generated
 */
public class CommerceOrderTypeServiceWrapper
	implements CommerceOrderTypeService,
			   ServiceWrapper<CommerceOrderTypeService> {

	public CommerceOrderTypeServiceWrapper() {
		this(null);
	}

	public CommerceOrderTypeServiceWrapper(
		CommerceOrderTypeService commerceOrderTypeService) {

		_commerceOrderTypeService = commerceOrderTypeService;
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderType addCommerceOrderType(
			String externalReferenceCode,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			boolean active, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute,
			int displayOrder, int expirationDateMonth, int expirationDateDay,
			int expirationDateYear, int expirationDateHour,
			int expirationDateMinute, boolean neverExpire,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderTypeService.addCommerceOrderType(
			externalReferenceCode, nameMap, descriptionMap, active,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, displayOrder, expirationDateMonth,
			expirationDateDay, expirationDateYear, expirationDateHour,
			expirationDateMinute, neverExpire, serviceContext);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderType deleteCommerceOrderType(
			long commerceOrderTypeId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderTypeService.deleteCommerceOrderType(
			commerceOrderTypeId);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderType fetchCommerceOrderType(
			long commerceOrderTypeId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderTypeService.fetchCommerceOrderType(
			commerceOrderTypeId);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderType
			fetchCommerceOrderTypeByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderTypeService.
			fetchCommerceOrderTypeByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderType getCommerceOrderType(
			long commerceOrderTypeId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderTypeService.getCommerceOrderType(
			commerceOrderTypeId);
	}

	@Override
	public java.util.List<com.liferay.commerce.model.CommerceOrderType>
			getCommerceOrderTypes(
				String className, long classPK, boolean active, int start,
				int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderTypeService.getCommerceOrderTypes(
			className, classPK, active, start, end);
	}

	@Override
	public int getCommerceOrderTypesCount(
			String className, long classPK, boolean active)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderTypeService.getCommerceOrderTypesCount(
			className, classPK, active);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderType
			getOrAddEmptyCommerceOrderType(String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderTypeService.getOrAddEmptyCommerceOrderType(
			externalReferenceCode);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commerceOrderTypeService.getOSGiServiceIdentifier();
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderType updateCommerceOrderType(
			String externalReferenceCode, long commerceOrderTypeId,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			boolean active, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute,
			int displayOrder, int expirationDateMonth, int expirationDateDay,
			int expirationDateYear, int expirationDateHour,
			int expirationDateMinute, boolean neverExpire,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderTypeService.updateCommerceOrderType(
			externalReferenceCode, commerceOrderTypeId, nameMap, descriptionMap,
			active, displayDateMonth, displayDateDay, displayDateYear,
			displayDateHour, displayDateMinute, displayOrder,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			serviceContext);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderType
			updateCommerceOrderTypeExternalReferenceCode(
				String externalReferenceCode, long commerceOrderTypeId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderTypeService.
			updateCommerceOrderTypeExternalReferenceCode(
				externalReferenceCode, commerceOrderTypeId);
	}

	@Override
	public CommerceOrderTypeService getWrappedService() {
		return _commerceOrderTypeService;
	}

	@Override
	public void setWrappedService(
		CommerceOrderTypeService commerceOrderTypeService) {

		_commerceOrderTypeService = commerceOrderTypeService;
	}

	private CommerceOrderTypeService _commerceOrderTypeService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-811820988