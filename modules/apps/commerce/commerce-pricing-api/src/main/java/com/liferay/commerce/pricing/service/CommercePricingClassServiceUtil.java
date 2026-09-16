/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.service;

import com.liferay.commerce.pricing.model.CommercePricingClass;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;
import java.util.Map;

/**
 * Provides the remote service utility for CommercePricingClass. This utility wraps
 * <code>com.liferay.commerce.pricing.service.impl.CommercePricingClassServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Riccardo Alberti
 * @see CommercePricingClassService
 * @generated
 */
public class CommercePricingClassServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.pricing.service.impl.CommercePricingClassServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static CommercePricingClass addCommercePricingClass(
			String externalReferenceCode,
			Map<java.util.Locale, String> titleMap,
			Map<java.util.Locale, String> descriptionMap,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommercePricingClass(
			externalReferenceCode, titleMap, descriptionMap, serviceContext);
	}

	public static CommercePricingClass addOrUpdateCommercePricingClass(
			String externalReferenceCode, long commercePricingClassId,
			Map<java.util.Locale, String> titleMap,
			Map<java.util.Locale, String> descriptionMap,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addOrUpdateCommercePricingClass(
			externalReferenceCode, commercePricingClassId, titleMap,
			descriptionMap, serviceContext);
	}

	public static CommercePricingClass deleteCommercePricingClass(
			long commercePricingClassId)
		throws PortalException {

		return getService().deleteCommercePricingClass(commercePricingClassId);
	}

	public static CommercePricingClass fetchCommercePricingClass(
			long commercePricingClassId)
		throws PortalException {

		return getService().fetchCommercePricingClass(commercePricingClassId);
	}

	public static CommercePricingClass
			fetchCommercePricingClassByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().fetchCommercePricingClassByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	public static CommercePricingClass getCommercePricingClass(
			long commercePricingClassId)
		throws PortalException {

		return getService().getCommercePricingClass(commercePricingClassId);
	}

	public static int getCommercePricingClassCountByCPDefinitionId(
			long cpDefinitionId, String title)
		throws com.liferay.portal.kernel.security.auth.PrincipalException {

		return getService().getCommercePricingClassCountByCPDefinitionId(
			cpDefinitionId, title);
	}

	public static List<CommercePricingClass> getCommercePricingClasses(
			long companyId, int start, int end,
			OrderByComparator<CommercePricingClass> orderByComparator)
		throws PortalException {

		return getService().getCommercePricingClasses(
			companyId, start, end, orderByComparator);
	}

	public static int getCommercePricingClassesCount(long companyId)
		throws PortalException {

		return getService().getCommercePricingClassesCount(companyId);
	}

	public static int getCommercePricingClassesCount(
			long cpDefinitionId, String title)
		throws com.liferay.portal.kernel.security.auth.PrincipalException {

		return getService().getCommercePricingClassesCount(
			cpDefinitionId, title);
	}

	public static CommercePricingClass getOrAddEmptyCommercePricingClass(
			String externalReferenceCode)
		throws PortalException {

		return getService().getOrAddEmptyCommercePricingClass(
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

	public static List<CommercePricingClass> searchByCPDefinitionId(
			long cpDefinitionId, String title, int start, int end)
		throws com.liferay.portal.kernel.security.auth.PrincipalException {

		return getService().searchByCPDefinitionId(
			cpDefinitionId, title, start, end);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<CommercePricingClass> searchCommercePricingClasses(
				long companyId, String keywords, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
			throws PortalException {

		return getService().searchCommercePricingClasses(
			companyId, keywords, start, end, sort);
	}

	public static CommercePricingClass updateCommercePricingClass(
			long commercePricingClassId, Map<java.util.Locale, String> titleMap,
			Map<java.util.Locale, String> descriptionMap,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCommercePricingClass(
			commercePricingClassId, titleMap, descriptionMap, serviceContext);
	}

	public static CommercePricingClass
			updateCommercePricingClassExternalReferenceCode(
				String externalReferenceCode, long commercePricingClassId)
		throws PortalException {

		return getService().updateCommercePricingClassExternalReferenceCode(
			externalReferenceCode, commercePricingClassId);
	}

	public static CommercePricingClassService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommercePricingClassService>
		_serviceSnapshot = new Snapshot<>(
			CommercePricingClassServiceUtil.class,
			CommercePricingClassService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:495652909