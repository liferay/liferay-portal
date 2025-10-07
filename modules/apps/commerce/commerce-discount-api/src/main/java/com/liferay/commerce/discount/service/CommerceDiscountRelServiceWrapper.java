/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link CommerceDiscountRelService}.
 *
 * @author Marco Leo
 * @see CommerceDiscountRelService
 * @generated
 */
public class CommerceDiscountRelServiceWrapper
	implements CommerceDiscountRelService,
			   ServiceWrapper<CommerceDiscountRelService> {

	public CommerceDiscountRelServiceWrapper() {
		this(null);
	}

	public CommerceDiscountRelServiceWrapper(
		CommerceDiscountRelService commerceDiscountRelService) {

		_commerceDiscountRelService = commerceDiscountRelService;
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountRel
			addCommerceDiscountRel(
				long commerceDiscountId, String className, long classPK,
				com.liferay.portal.kernel.util.UnicodeProperties
					typeSettingsUnicodeProperties,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRelService.addCommerceDiscountRel(
			commerceDiscountId, className, classPK,
			typeSettingsUnicodeProperties, serviceContext);
	}

	@Override
	public void deleteCommerceDiscountRel(long commerceDiscountRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceDiscountRelService.deleteCommerceDiscountRel(
			commerceDiscountRelId);
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountRel
			fetchCommerceDiscountRel(
				long commerceDiscountId, String className, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRelService.fetchCommerceDiscountRel(
			commerceDiscountId, className, classPK);
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountRel
			fetchCommerceDiscountRel(String className, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRelService.fetchCommerceDiscountRel(
			className, classPK);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.discount.model.CommerceDiscountRel>
				getCategoriesByCommerceDiscountId(
					long commerceDiscountId, String name, int start, int end)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRelService.getCategoriesByCommerceDiscountId(
			commerceDiscountId, name, start, end);
	}

	@Override
	public int getCategoriesByCommerceDiscountIdCount(
			long commerceDiscountId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRelService.
			getCategoriesByCommerceDiscountIdCount(commerceDiscountId, name);
	}

	@Override
	public long[] getClassPKs(long commerceDiscountId, String className)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRelService.getClassPKs(
			commerceDiscountId, className);
	}

	@Override
	public com.liferay.commerce.discount.model.CommerceDiscountRel
			getCommerceDiscountRel(long commerceDiscountRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRelService.getCommerceDiscountRel(
			commerceDiscountRelId);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.discount.model.CommerceDiscountRel>
				getCommerceDiscountRels(
					long commerceDiscountId, String className)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRelService.getCommerceDiscountRels(
			commerceDiscountId, className);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.discount.model.CommerceDiscountRel>
				getCommerceDiscountRels(
					long commerceDiscountId, String className, int start,
					int end,
					com.liferay.portal.kernel.util.OrderByComparator
						<com.liferay.commerce.discount.model.
							CommerceDiscountRel> orderByComparator)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRelService.getCommerceDiscountRels(
			commerceDiscountId, className, start, end, orderByComparator);
	}

	@Override
	public int getCommerceDiscountRelsCount(
			long commerceDiscountId, String className)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRelService.getCommerceDiscountRelsCount(
			commerceDiscountId, className);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.discount.model.CommerceDiscountRel>
				getCommercePricingClassesByCommerceDiscountId(
					long commerceDiscountId, String title, int start, int end)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRelService.
			getCommercePricingClassesByCommerceDiscountId(
				commerceDiscountId, title, start, end);
	}

	@Override
	public int getCommercePricingClassesByCommerceDiscountIdCount(
			long commerceDiscountId, String title)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRelService.
			getCommercePricingClassesByCommerceDiscountIdCount(
				commerceDiscountId, title);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.discount.model.CommerceDiscountRel>
				getCPDefinitionsByCommerceDiscountId(
					long commerceDiscountId, String name, String languageId,
					int start, int end)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRelService.getCPDefinitionsByCommerceDiscountId(
			commerceDiscountId, name, languageId, start, end);
	}

	@Override
	public int getCPDefinitionsByCommerceDiscountIdCount(
			long commerceDiscountId, String name, String languageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRelService.
			getCPDefinitionsByCommerceDiscountIdCount(
				commerceDiscountId, name, languageId);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.discount.model.CommerceDiscountRel>
				getCPInstancesByCommerceDiscountId(
					long commerceDiscountId, String sku, int start, int end)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRelService.getCPInstancesByCommerceDiscountId(
			commerceDiscountId, sku, start, end);
	}

	@Override
	public int getCPInstancesByCommerceDiscountIdCount(
			long commerceDiscountId, String sku)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceDiscountRelService.
			getCPInstancesByCommerceDiscountIdCount(commerceDiscountId, sku);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commerceDiscountRelService.getOSGiServiceIdentifier();
	}

	@Override
	public CommerceDiscountRelService getWrappedService() {
		return _commerceDiscountRelService;
	}

	@Override
	public void setWrappedService(
		CommerceDiscountRelService commerceDiscountRelService) {

		_commerceDiscountRelService = commerceDiscountRelService;
	}

	private CommerceDiscountRelService _commerceDiscountRelService;

}