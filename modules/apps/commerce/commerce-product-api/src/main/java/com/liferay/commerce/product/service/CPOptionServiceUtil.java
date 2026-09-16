/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPOption;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;
import java.util.Map;

/**
 * Provides the remote service utility for CPOption. This utility wraps
 * <code>com.liferay.commerce.product.service.impl.CPOptionServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Marco Leo
 * @see CPOptionService
 * @generated
 */
public class CPOptionServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.product.service.impl.CPOptionServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static CPOption addCPOption(
			Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap,
			String commerceOptionTypeKey, boolean facetable, boolean required,
			boolean skuContributor, String key,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCPOption(
			nameMap, descriptionMap, commerceOptionTypeKey, facetable, required,
			skuContributor, key, serviceContext);
	}

	public static CPOption addOrUpdateCPOption(
			String externalReferenceCode, Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap,
			String commerceOptionTypeKey, boolean facetable, boolean required,
			boolean skuContributor, String key,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addOrUpdateCPOption(
			externalReferenceCode, nameMap, descriptionMap,
			commerceOptionTypeKey, facetable, required, skuContributor, key,
			serviceContext);
	}

	public static void deleteCPOption(long cpOptionId) throws PortalException {
		getService().deleteCPOption(cpOptionId);
	}

	public static CPOption fetchCPOption(long cpOptionId)
		throws PortalException {

		return getService().fetchCPOption(cpOptionId);
	}

	public static CPOption fetchCPOption(long companyId, String key)
		throws PortalException {

		return getService().fetchCPOption(companyId, key);
	}

	public static CPOption fetchCPOptionByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().fetchCPOptionByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	public static List<CPOption> findCPOptionByCompanyId(
			long companyId, int start, int end,
			OrderByComparator<CPOption> orderByComparator)
		throws PortalException {

		return getService().findCPOptionByCompanyId(
			companyId, start, end, orderByComparator);
	}

	public static CPOption getCPOption(long cpOptionId) throws PortalException {
		return getService().getCPOption(cpOptionId);
	}

	public static CPOption getOrAddEmptyCPOption(String externalReferenceCode)
		throws PortalException {

		return getService().getOrAddEmptyCPOption(externalReferenceCode);
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
		<CPOption> searchCPOptions(
				long companyId, String keywords, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
			throws PortalException {

		return getService().searchCPOptions(
			companyId, keywords, start, end, sort);
	}

	public static CPOption updateCPOption(
			long cpOptionId, Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap,
			String commerceOptionTypeKey, boolean facetable, boolean required,
			boolean skuContributor, String key,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCPOption(
			cpOptionId, nameMap, descriptionMap, commerceOptionTypeKey,
			facetable, required, skuContributor, key, serviceContext);
	}

	public static CPOption updateCPOptionExternalReferenceCode(
			String externalReferenceCode, long cpOptionId)
		throws PortalException {

		return getService().updateCPOptionExternalReferenceCode(
			externalReferenceCode, cpOptionId);
	}

	public static CPOptionService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CPOptionService> _serviceSnapshot =
		new Snapshot<>(CPOptionServiceUtil.class, CPOptionService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1332091979