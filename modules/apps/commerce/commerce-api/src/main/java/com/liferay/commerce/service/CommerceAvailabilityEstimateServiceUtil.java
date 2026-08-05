/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service;

import com.liferay.commerce.model.CommerceAvailabilityEstimate;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;
import java.util.Map;

/**
 * Provides the remote service utility for CommerceAvailabilityEstimate. This utility wraps
 * <code>com.liferay.commerce.service.impl.CommerceAvailabilityEstimateServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceAvailabilityEstimateService
 * @generated
 */
public class CommerceAvailabilityEstimateServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.service.impl.CommerceAvailabilityEstimateServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static CommerceAvailabilityEstimate addCommerceAvailabilityEstimate(
			String externalReferenceCode,
			Map<java.util.Locale, String> titleMap, double priority,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommerceAvailabilityEstimate(
			externalReferenceCode, titleMap, priority, serviceContext);
	}

	public static void deleteCommerceAvailabilityEstimate(
			long commerceAvailabilityEstimateId)
		throws PortalException {

		getService().deleteCommerceAvailabilityEstimate(
			commerceAvailabilityEstimateId);
	}

	public static CommerceAvailabilityEstimate
			fetchCommerceAvailabilityEstimateByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().
			fetchCommerceAvailabilityEstimateByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	public static CommerceAvailabilityEstimate getCommerceAvailabilityEstimate(
			long commerceAvailabilityEstimateId)
		throws PortalException {

		return getService().getCommerceAvailabilityEstimate(
			commerceAvailabilityEstimateId);
	}

	public static List<CommerceAvailabilityEstimate>
			getCommerceAvailabilityEstimates(
				long companyId, int start, int end,
				OrderByComparator<CommerceAvailabilityEstimate>
					orderByComparator)
		throws PortalException {

		return getService().getCommerceAvailabilityEstimates(
			companyId, start, end, orderByComparator);
	}

	public static int getCommerceAvailabilityEstimatesCount(long companyId)
		throws PortalException {

		return getService().getCommerceAvailabilityEstimatesCount(companyId);
	}

	public static CommerceAvailabilityEstimate
			getOrAddEmptyCommerceAvailabilityEstimate(
				String externalReferenceCode)
		throws PortalException {

		return getService().getOrAddEmptyCommerceAvailabilityEstimate(
			externalReferenceCode);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static CommerceAvailabilityEstimate
			updateCommerceAvailabilityEstimate(
				String externalReferenceCode,
				long commerceAvailabilityEstimateId,
				Map<java.util.Locale, String> titleMap, double priority,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCommerceAvailabilityEstimate(
			externalReferenceCode, commerceAvailabilityEstimateId, titleMap,
			priority, serviceContext);
	}

	public static CommerceAvailabilityEstimateService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceAvailabilityEstimateService>
		_serviceSnapshot = new Snapshot<>(
			CommerceAvailabilityEstimateServiceUtil.class,
			CommerceAvailabilityEstimateService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-915011880