/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.service;

import com.liferay.commerce.pricing.model.CommercePricingClassCPDefinitionRel;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

/**
 * Provides the remote service utility for CommercePricingClassCPDefinitionRel. This utility wraps
 * <code>com.liferay.commerce.pricing.service.impl.CommercePricingClassCPDefinitionRelServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Riccardo Alberti
 * @see CommercePricingClassCPDefinitionRelService
 * @generated
 */
public class CommercePricingClassCPDefinitionRelServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.pricing.service.impl.CommercePricingClassCPDefinitionRelServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static CommercePricingClassCPDefinitionRel
			addCommercePricingClassCPDefinitionRel(
				long commercePricingClassId, long cpDefinitionId,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommercePricingClassCPDefinitionRel(
			commercePricingClassId, cpDefinitionId, serviceContext);
	}

	public static CommercePricingClassCPDefinitionRel
			deleteCommercePricingClassCPDefinitionRel(
				long commercePricingClassCPDefinitionRelId)
		throws PortalException {

		return getService().deleteCommercePricingClassCPDefinitionRel(
			commercePricingClassCPDefinitionRelId);
	}

	public static CommercePricingClassCPDefinitionRel
			fetchCommercePricingClassCPDefinitionRel(
				long commercePricingClassId, long cpDefinitionId)
		throws PortalException {

		return getService().fetchCommercePricingClassCPDefinitionRel(
			commercePricingClassId, cpDefinitionId);
	}

	public static CommercePricingClassCPDefinitionRel
			getCommercePricingClassCPDefinitionRel(
				long commercePricingClassCPDefinitionRelId)
		throws PortalException {

		return getService().getCommercePricingClassCPDefinitionRel(
			commercePricingClassCPDefinitionRelId);
	}

	public static List<CommercePricingClassCPDefinitionRel>
			getCommercePricingClassCPDefinitionRelByClassId(
				long commercePricingClassId)
		throws PortalException {

		return getService().getCommercePricingClassCPDefinitionRelByClassId(
			commercePricingClassId);
	}

	public static List<CommercePricingClassCPDefinitionRel>
			getCommercePricingClassCPDefinitionRels(
				long commercePricingClassId, int start, int end,
				OrderByComparator<CommercePricingClassCPDefinitionRel>
					orderByComparator)
		throws PortalException {

		return getService().getCommercePricingClassCPDefinitionRels(
			commercePricingClassId, start, end, orderByComparator);
	}

	public static int getCommercePricingClassCPDefinitionRelsCount(
			long commercePricingClassId)
		throws PortalException {

		return getService().getCommercePricingClassCPDefinitionRelsCount(
			commercePricingClassId);
	}

	public static int getCommercePricingClassCPDefinitionRelsCount(
		long commercePricingClassId, String name, String languageId) {

		return getService().getCommercePricingClassCPDefinitionRelsCount(
			commercePricingClassId, name, languageId);
	}

	public static long[] getCPDefinitionIds(long commercePricingClassId)
		throws PortalException {

		return getService().getCPDefinitionIds(commercePricingClassId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static List<CommercePricingClassCPDefinitionRel>
			searchByCommercePricingClassId(
				long commercePricingClassId, String name, String languageId,
				int start, int end)
		throws PortalException {

		return getService().searchByCommercePricingClassId(
			commercePricingClassId, name, languageId, start, end);
	}

	public static CommercePricingClassCPDefinitionRelService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommercePricingClassCPDefinitionRelService>
		_serviceSnapshot = new Snapshot<>(
			CommercePricingClassCPDefinitionRelServiceUtil.class,
			CommercePricingClassCPDefinitionRelService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-2049780743