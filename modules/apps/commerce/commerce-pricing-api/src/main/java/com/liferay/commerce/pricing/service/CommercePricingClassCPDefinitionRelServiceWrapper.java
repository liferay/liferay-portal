/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.service;

import com.liferay.commerce.pricing.model.CommercePricingClassCPDefinitionRel;
import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link CommercePricingClassCPDefinitionRelService}.
 *
 * @author Riccardo Alberti
 * @see CommercePricingClassCPDefinitionRelService
 * @generated
 */
public class CommercePricingClassCPDefinitionRelServiceWrapper
	implements CommercePricingClassCPDefinitionRelService,
			   ServiceWrapper<CommercePricingClassCPDefinitionRelService> {

	public CommercePricingClassCPDefinitionRelServiceWrapper() {
		this(null);
	}

	public CommercePricingClassCPDefinitionRelServiceWrapper(
		CommercePricingClassCPDefinitionRelService
			commercePricingClassCPDefinitionRelService) {

		_commercePricingClassCPDefinitionRelService =
			commercePricingClassCPDefinitionRelService;
	}

	@Override
	public CommercePricingClassCPDefinitionRel
			addCommercePricingClassCPDefinitionRel(
				long commercePricingClassId, long cpDefinitionId,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePricingClassCPDefinitionRelService.
			addCommercePricingClassCPDefinitionRel(
				commercePricingClassId, cpDefinitionId, serviceContext);
	}

	@Override
	public CommercePricingClassCPDefinitionRel
			deleteCommercePricingClassCPDefinitionRel(
				long commercePricingClassCPDefinitionRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePricingClassCPDefinitionRelService.
			deleteCommercePricingClassCPDefinitionRel(
				commercePricingClassCPDefinitionRelId);
	}

	@Override
	public CommercePricingClassCPDefinitionRel
			fetchCommercePricingClassCPDefinitionRel(
				long commercePricingClassId, long cpDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePricingClassCPDefinitionRelService.
			fetchCommercePricingClassCPDefinitionRel(
				commercePricingClassId, cpDefinitionId);
	}

	@Override
	public CommercePricingClassCPDefinitionRel
			getCommercePricingClassCPDefinitionRel(
				long commercePricingClassCPDefinitionRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePricingClassCPDefinitionRelService.
			getCommercePricingClassCPDefinitionRel(
				commercePricingClassCPDefinitionRelId);
	}

	@Override
	public java.util.List<CommercePricingClassCPDefinitionRel>
			getCommercePricingClassCPDefinitionRelByClassId(
				long commercePricingClassId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePricingClassCPDefinitionRelService.
			getCommercePricingClassCPDefinitionRelByClassId(
				commercePricingClassId);
	}

	@Override
	public java.util.List<CommercePricingClassCPDefinitionRel>
			getCommercePricingClassCPDefinitionRels(
				long commercePricingClassId, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<CommercePricingClassCPDefinitionRel> orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePricingClassCPDefinitionRelService.
			getCommercePricingClassCPDefinitionRels(
				commercePricingClassId, start, end, orderByComparator);
	}

	@Override
	public int getCommercePricingClassCPDefinitionRelsCount(
			long commercePricingClassId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePricingClassCPDefinitionRelService.
			getCommercePricingClassCPDefinitionRelsCount(
				commercePricingClassId);
	}

	@Override
	public int getCommercePricingClassCPDefinitionRelsCount(
		long commercePricingClassId, String name, String languageId) {

		return _commercePricingClassCPDefinitionRelService.
			getCommercePricingClassCPDefinitionRelsCount(
				commercePricingClassId, name, languageId);
	}

	@Override
	public long[] getCPDefinitionIds(long commercePricingClassId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePricingClassCPDefinitionRelService.getCPDefinitionIds(
			commercePricingClassId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commercePricingClassCPDefinitionRelService.
			getOSGiServiceIdentifier();
	}

	@Override
	public java.util.List<CommercePricingClassCPDefinitionRel>
			searchByCommercePricingClassId(
				long commercePricingClassId, String name, String languageId,
				int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePricingClassCPDefinitionRelService.
			searchByCommercePricingClassId(
				commercePricingClassId, name, languageId, start, end);
	}

	@Override
	public CommercePricingClassCPDefinitionRelService getWrappedService() {
		return _commercePricingClassCPDefinitionRelService;
	}

	@Override
	public void setWrappedService(
		CommercePricingClassCPDefinitionRelService
			commercePricingClassCPDefinitionRelService) {

		_commercePricingClassCPDefinitionRelService =
			commercePricingClassCPDefinitionRelService;
	}

	private CommercePricingClassCPDefinitionRelService
		_commercePricingClassCPDefinitionRelService;

}
// LIFERAY-SERVICE-BUILDER-HASH:261191446