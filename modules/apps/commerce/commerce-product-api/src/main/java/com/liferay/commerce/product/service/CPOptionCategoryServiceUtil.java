/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPOptionCategory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;

import java.util.Map;

/**
 * Provides the remote service utility for CPOptionCategory. This utility wraps
 * <code>com.liferay.commerce.product.service.impl.CPOptionCategoryServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Marco Leo
 * @see CPOptionCategoryService
 * @generated
 */
public class CPOptionCategoryServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.product.service.impl.CPOptionCategoryServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static CPOptionCategory addCPOptionCategory(
			String externalReferenceCode,
			Map<java.util.Locale, String> titleMap,
			Map<java.util.Locale, String> descriptionMap, double priority,
			String key,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCPOptionCategory(
			externalReferenceCode, titleMap, descriptionMap, priority, key,
			serviceContext);
	}

	public static CPOptionCategory addOrUpdateCPOptionCategory(
			String externalReferenceCode, long cpOptionCategoryId,
			Map<java.util.Locale, String> titleMap,
			Map<java.util.Locale, String> descriptionMap, double priority,
			String key,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addOrUpdateCPOptionCategory(
			externalReferenceCode, cpOptionCategoryId, titleMap, descriptionMap,
			priority, key, serviceContext);
	}

	public static void deleteCPOptionCategory(long cpOptionCategoryId)
		throws PortalException {

		getService().deleteCPOptionCategory(cpOptionCategoryId);
	}

	public static CPOptionCategory fetchCPOptionCategory(
			long cpOptionCategoryId)
		throws PortalException {

		return getService().fetchCPOptionCategory(cpOptionCategoryId);
	}

	public static CPOptionCategory fetchCPOptionCategoryByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().fetchCPOptionCategoryByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	public static CPOptionCategory getCPOptionCategory(long cpOptionCategoryId)
		throws PortalException {

		return getService().getCPOptionCategory(cpOptionCategoryId);
	}

	public static CPOptionCategory getCPOptionCategoryByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getCPOptionCategoryByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	public static CPOptionCategory getOrAddEmptyCPOptionCategory(
			String externalReferenceCode)
		throws PortalException {

		return getService().getOrAddEmptyCPOptionCategory(
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

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<CPOptionCategory> searchCPOptionCategories(
				long companyId, String keywords, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
			throws PortalException {

		return getService().searchCPOptionCategories(
			companyId, keywords, start, end, sort);
	}

	public static CPOptionCategory updateCPOptionCategory(
			String externalReferenceCode, long cpOptionCategoryId,
			Map<java.util.Locale, String> titleMap,
			Map<java.util.Locale, String> descriptionMap, double priority,
			String key)
		throws PortalException {

		return getService().updateCPOptionCategory(
			externalReferenceCode, cpOptionCategoryId, titleMap, descriptionMap,
			priority, key);
	}

	public static CPOptionCategoryService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CPOptionCategoryService> _serviceSnapshot =
		new Snapshot<>(
			CPOptionCategoryServiceUtil.class, CPOptionCategoryService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:643949918