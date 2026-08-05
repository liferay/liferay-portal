/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link CommerceAvailabilityEstimateService}.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceAvailabilityEstimateService
 * @generated
 */
public class CommerceAvailabilityEstimateServiceWrapper
	implements CommerceAvailabilityEstimateService,
			   ServiceWrapper<CommerceAvailabilityEstimateService> {

	public CommerceAvailabilityEstimateServiceWrapper() {
		this(null);
	}

	public CommerceAvailabilityEstimateServiceWrapper(
		CommerceAvailabilityEstimateService
			commerceAvailabilityEstimateService) {

		_commerceAvailabilityEstimateService =
			commerceAvailabilityEstimateService;
	}

	@Override
	public com.liferay.commerce.model.CommerceAvailabilityEstimate
			addCommerceAvailabilityEstimate(
				String externalReferenceCode,
				java.util.Map<java.util.Locale, String> titleMap,
				double priority,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceAvailabilityEstimateService.
			addCommerceAvailabilityEstimate(
				externalReferenceCode, titleMap, priority, serviceContext);
	}

	@Override
	public void deleteCommerceAvailabilityEstimate(
			long commerceAvailabilityEstimateId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceAvailabilityEstimateService.deleteCommerceAvailabilityEstimate(
			commerceAvailabilityEstimateId);
	}

	@Override
	public com.liferay.commerce.model.CommerceAvailabilityEstimate
			fetchCommerceAvailabilityEstimateByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceAvailabilityEstimateService.
			fetchCommerceAvailabilityEstimateByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public com.liferay.commerce.model.CommerceAvailabilityEstimate
			getCommerceAvailabilityEstimate(long commerceAvailabilityEstimateId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceAvailabilityEstimateService.
			getCommerceAvailabilityEstimate(commerceAvailabilityEstimateId);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.model.CommerceAvailabilityEstimate>
				getCommerceAvailabilityEstimates(
					long companyId, int start, int end,
					com.liferay.portal.kernel.util.OrderByComparator
						<com.liferay.commerce.model.
							CommerceAvailabilityEstimate> orderByComparator)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceAvailabilityEstimateService.
			getCommerceAvailabilityEstimates(
				companyId, start, end, orderByComparator);
	}

	@Override
	public int getCommerceAvailabilityEstimatesCount(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceAvailabilityEstimateService.
			getCommerceAvailabilityEstimatesCount(companyId);
	}

	@Override
	public com.liferay.commerce.model.CommerceAvailabilityEstimate
			getOrAddEmptyCommerceAvailabilityEstimate(
				String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceAvailabilityEstimateService.
			getOrAddEmptyCommerceAvailabilityEstimate(externalReferenceCode);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commerceAvailabilityEstimateService.getOSGiServiceIdentifier();
	}

	@Override
	public com.liferay.commerce.model.CommerceAvailabilityEstimate
			updateCommerceAvailabilityEstimate(
				String externalReferenceCode,
				long commerceAvailabilityEstimateId,
				java.util.Map<java.util.Locale, String> titleMap,
				double priority,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceAvailabilityEstimateService.
			updateCommerceAvailabilityEstimate(
				externalReferenceCode, commerceAvailabilityEstimateId, titleMap,
				priority, serviceContext);
	}

	@Override
	public CommerceAvailabilityEstimateService getWrappedService() {
		return _commerceAvailabilityEstimateService;
	}

	@Override
	public void setWrappedService(
		CommerceAvailabilityEstimateService
			commerceAvailabilityEstimateService) {

		_commerceAvailabilityEstimateService =
			commerceAvailabilityEstimateService;
	}

	private CommerceAvailabilityEstimateService
		_commerceAvailabilityEstimateService;

}
// LIFERAY-SERVICE-BUILDER-HASH:342748220