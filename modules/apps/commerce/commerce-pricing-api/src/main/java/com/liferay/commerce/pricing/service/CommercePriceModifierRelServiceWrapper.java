/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.service;

import com.liferay.commerce.pricing.model.CommercePriceModifierRel;
import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link CommercePriceModifierRelService}.
 *
 * @author Riccardo Alberti
 * @see CommercePriceModifierRelService
 * @generated
 */
public class CommercePriceModifierRelServiceWrapper
	implements CommercePriceModifierRelService,
			   ServiceWrapper<CommercePriceModifierRelService> {

	public CommercePriceModifierRelServiceWrapper() {
		this(null);
	}

	public CommercePriceModifierRelServiceWrapper(
		CommercePriceModifierRelService commercePriceModifierRelService) {

		_commercePriceModifierRelService = commercePriceModifierRelService;
	}

	@Override
	public CommercePriceModifierRel addCommercePriceModifierRel(
			long commercePriceModifierId, String className, long classPK,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceModifierRelService.addCommercePriceModifierRel(
			commercePriceModifierId, className, classPK, serviceContext);
	}

	@Override
	public void deleteCommercePriceModifierRel(long commercePriceModifierRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commercePriceModifierRelService.deleteCommercePriceModifierRel(
			commercePriceModifierRelId);
	}

	@Override
	public CommercePriceModifierRel fetchCommercePriceModifierRel(
			long commercePriceModifierId, String className, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceModifierRelService.fetchCommercePriceModifierRel(
			commercePriceModifierId, className, classPK);
	}

	@Override
	public java.util.List<CommercePriceModifierRel>
		getCategoriesCommercePriceModifierRels(
			long commercePriceModifierId, String name, int start, int end) {

		return _commercePriceModifierRelService.
			getCategoriesCommercePriceModifierRels(
				commercePriceModifierId, name, start, end);
	}

	@Override
	public int getCategoriesCommercePriceModifierRelsCount(
		long commercePriceModifierId, String name) {

		return _commercePriceModifierRelService.
			getCategoriesCommercePriceModifierRelsCount(
				commercePriceModifierId, name);
	}

	@Override
	public long[] getClassPKs(long commercePriceModifierId, String className)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceModifierRelService.getClassPKs(
			commercePriceModifierId, className);
	}

	@Override
	public CommercePriceModifierRel getCommercePriceModifierRel(
			long commercePriceModifierRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceModifierRelService.getCommercePriceModifierRel(
			commercePriceModifierRelId);
	}

	@Override
	public java.util.List<CommercePriceModifierRel>
			getCommercePriceModifierRels(
				long commercePriceModifierId, String className)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceModifierRelService.getCommercePriceModifierRels(
			commercePriceModifierId, className);
	}

	@Override
	public java.util.List<CommercePriceModifierRel>
			getCommercePriceModifierRels(
				long commercePriceModifierId, String className, int start,
				int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<CommercePriceModifierRel> orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceModifierRelService.getCommercePriceModifierRels(
			commercePriceModifierId, className, start, end, orderByComparator);
	}

	@Override
	public int getCommercePriceModifierRelsCount(
			long commercePriceModifierId, String className)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceModifierRelService.
			getCommercePriceModifierRelsCount(
				commercePriceModifierId, className);
	}

	@Override
	public java.util.List<CommercePriceModifierRel>
		getCommercePriceModifiersRels(String className, long classPK) {

		return _commercePriceModifierRelService.getCommercePriceModifiersRels(
			className, classPK);
	}

	@Override
	public java.util.List<CommercePriceModifierRel>
		getCommercePricingClassesCommercePriceModifierRels(
			long commercePriceModifierId, String title, int start, int end) {

		return _commercePriceModifierRelService.
			getCommercePricingClassesCommercePriceModifierRels(
				commercePriceModifierId, title, start, end);
	}

	@Override
	public int getCommercePricingClassesCommercePriceModifierRelsCount(
		long commercePriceModifierId, String title) {

		return _commercePriceModifierRelService.
			getCommercePricingClassesCommercePriceModifierRelsCount(
				commercePriceModifierId, title);
	}

	@Override
	public java.util.List<CommercePriceModifierRel>
		getCPDefinitionsCommercePriceModifierRels(
			long commercePriceModifierId, String name, String languageId,
			int start, int end) {

		return _commercePriceModifierRelService.
			getCPDefinitionsCommercePriceModifierRels(
				commercePriceModifierId, name, languageId, start, end);
	}

	@Override
	public int getCPDefinitionsCommercePriceModifierRelsCount(
		long commercePriceModifierId, String name, String languageId) {

		return _commercePriceModifierRelService.
			getCPDefinitionsCommercePriceModifierRelsCount(
				commercePriceModifierId, name, languageId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commercePriceModifierRelService.getOSGiServiceIdentifier();
	}

	@Override
	public CommercePriceModifierRelService getWrappedService() {
		return _commercePriceModifierRelService;
	}

	@Override
	public void setWrappedService(
		CommercePriceModifierRelService commercePriceModifierRelService) {

		_commercePriceModifierRelService = commercePriceModifierRelService;
	}

	private CommercePriceModifierRelService _commercePriceModifierRelService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1613994927