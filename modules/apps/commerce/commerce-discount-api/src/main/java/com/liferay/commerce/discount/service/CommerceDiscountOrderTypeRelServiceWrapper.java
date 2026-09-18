/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link CommerceDiscountOrderTypeRelService}.
 *
 * @author Marco Leo
 * @see CommerceDiscountOrderTypeRelService
 * @generated
 */
public class CommerceDiscountOrderTypeRelServiceWrapper
	implements CommerceDiscountOrderTypeRelService,
			   ServiceWrapper<CommerceDiscountOrderTypeRelService> {

	public CommerceDiscountOrderTypeRelServiceWrapper() {
		this(null);
	}

	public CommerceDiscountOrderTypeRelServiceWrapper(
		CommerceDiscountOrderTypeRelService
			commerceDiscountOrderTypeRelService) {

		_commerceDiscountOrderTypeRelService =
			commerceDiscountOrderTypeRelService;
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountOrderTypeRel
			addCommerceDiscountOrderTypeRel(
				long commerceDiscountId, long commerceOrderTypeId, int priority,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountOrderTypeRelService.
			addCommerceDiscountOrderTypeRel(
				commerceDiscountId, commerceOrderTypeId, priority,
				serviceContext);
	}

	@Override
	public void deleteCommerceDiscountOrderTypeRel(
			long commerceDiscountOrderTypeRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceDiscountOrderTypeRelService.deleteCommerceDiscountOrderTypeRel(
			commerceDiscountOrderTypeRelId);
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountOrderTypeRel
			fetchCommerceDiscountOrderTypeRel(
				long commerceDiscountId, long commerceOrderTypeId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountOrderTypeRelService.
			fetchCommerceDiscountOrderTypeRel(
				commerceDiscountId, commerceOrderTypeId);
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountOrderTypeRel
			getCommerceDiscountOrderTypeRel(long commerceDiscountOrderTypeRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountOrderTypeRelService.
			getCommerceDiscountOrderTypeRel(commerceDiscountOrderTypeRelId);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.discount.model.CommerceDiscountOrderTypeRel>
				getCommerceDiscountOrderTypeRels(
					long commerceDiscountId, String name, int start, int end,
					com.liferay.portal.kernel.util.OrderByComparator
						<com.liferay.commerce.discount.model.
							CommerceDiscountOrderTypeRel> orderByComparator)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountOrderTypeRelService.
			getCommerceDiscountOrderTypeRels(
				commerceDiscountId, name, start, end, orderByComparator);
	}

	@Override
	public int getCommerceDiscountOrderTypeRelsCount(
			long commerceDiscountId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountOrderTypeRelService.
			getCommerceDiscountOrderTypeRelsCount(commerceDiscountId, name);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commerceDiscountOrderTypeRelService.getOSGiServiceIdentifier();
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountOrderTypeRel
			updatePriority(long commerceDiscountOrderTypeRelId, int priority)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountOrderTypeRelService.updatePriority(
			commerceDiscountOrderTypeRelId, priority);
	}

	@Override
	public CommerceDiscountOrderTypeRelService getWrappedService() {
		return _commerceDiscountOrderTypeRelService;
	}

	@Override
	public void setWrappedService(
		CommerceDiscountOrderTypeRelService
			commerceDiscountOrderTypeRelService) {

		_commerceDiscountOrderTypeRelService =
			commerceDiscountOrderTypeRelService;
	}

	private CommerceDiscountOrderTypeRelService
		_commerceDiscountOrderTypeRelService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-346357422