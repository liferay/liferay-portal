/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPTaxCategory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;
import java.util.Map;

/**
 * Provides the remote service utility for CPTaxCategory. This utility wraps
 * <code>com.liferay.commerce.product.service.impl.CPTaxCategoryServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Marco Leo
 * @see CPTaxCategoryService
 * @generated
 */
public class CPTaxCategoryServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.product.service.impl.CPTaxCategoryServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static CPTaxCategory addCPTaxCategory(
			String externalReferenceCode, Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCPTaxCategory(
			externalReferenceCode, nameMap, descriptionMap, serviceContext);
	}

	public static int countCPTaxCategoriesByCompanyId(
			long companyId, String keyword)
		throws PortalException {

		return getService().countCPTaxCategoriesByCompanyId(companyId, keyword);
	}

	public static void deleteCPTaxCategory(long cpTaxCategoryId)
		throws PortalException {

		getService().deleteCPTaxCategory(cpTaxCategoryId);
	}

	public static CPTaxCategory fetchCPTaxCategoryByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().fetchCPTaxCategoryByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	public static List<CPTaxCategory> findCPTaxCategoriesByCompanyId(
			long companyId, String keyword, int start, int end)
		throws PortalException {

		return getService().findCPTaxCategoriesByCompanyId(
			companyId, keyword, start, end);
	}

	public static List<CPTaxCategory> getCPTaxCategories(long companyId)
		throws PortalException {

		return getService().getCPTaxCategories(companyId);
	}

	public static List<CPTaxCategory> getCPTaxCategories(
			long companyId, int start, int end,
			OrderByComparator<CPTaxCategory> orderByComparator)
		throws PortalException {

		return getService().getCPTaxCategories(
			companyId, start, end, orderByComparator);
	}

	public static int getCPTaxCategoriesCount(long companyId)
		throws PortalException {

		return getService().getCPTaxCategoriesCount(companyId);
	}

	public static CPTaxCategory getCPTaxCategory(long cpTaxCategoryId)
		throws PortalException {

		return getService().getCPTaxCategory(cpTaxCategoryId);
	}

	public static CPTaxCategory getCPTaxCategoryByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getCPTaxCategoryByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<CPTaxCategory> searchCPTaxCategories(
				long companyId, String keywords, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
			throws PortalException {

		return getService().searchCPTaxCategories(
			companyId, keywords, start, end, sort);
	}

	public static CPTaxCategory updateCPTaxCategory(
			String externalReferenceCode, long cpTaxCategoryId,
			Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap)
		throws PortalException {

		return getService().updateCPTaxCategory(
			externalReferenceCode, cpTaxCategoryId, nameMap, descriptionMap);
	}

	public static CPTaxCategoryService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CPTaxCategoryService> _serviceSnapshot =
		new Snapshot<>(
			CPTaxCategoryServiceUtil.class, CPTaxCategoryService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-420488718