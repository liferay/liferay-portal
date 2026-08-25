/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;
import java.util.Map;

/**
 * Provides the remote service utility for CPDefinitionOptionRel. This utility wraps
 * <code>com.liferay.commerce.product.service.impl.CPDefinitionOptionRelServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Marco Leo
 * @see CPDefinitionOptionRelService
 * @generated
 */
public class CPDefinitionOptionRelServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.product.service.impl.CPDefinitionOptionRelServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static CPDefinitionOptionRel addCPDefinitionOptionRel(
			long cpDefinitionId, long cpOptionId,
			Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap,
			String commerceOptionTypeKey, double priority, boolean facetable,
			boolean required, boolean skuContributor, boolean importOptionValue,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCPDefinitionOptionRel(
			cpDefinitionId, cpOptionId, nameMap, descriptionMap,
			commerceOptionTypeKey, priority, facetable, required,
			skuContributor, importOptionValue, serviceContext);
	}

	public static CPDefinitionOptionRel addCPDefinitionOptionRel(
			long cpDefinitionId, long cpOptionId,
			Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap,
			String commerceOptionTypeKey, String infoItemServiceKey,
			double priority, boolean definedExternally, boolean facetable,
			boolean required, boolean skuContributor, boolean importOptionValue,
			String priceType, String typeSettings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCPDefinitionOptionRel(
			cpDefinitionId, cpOptionId, nameMap, descriptionMap,
			commerceOptionTypeKey, infoItemServiceKey, priority,
			definedExternally, facetable, required, skuContributor,
			importOptionValue, priceType, typeSettings, serviceContext);
	}

	public static CPDefinitionOptionRel addCPDefinitionOptionRel(
			long cpDefinitionId, long cpOptionId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCPDefinitionOptionRel(
			cpDefinitionId, cpOptionId, serviceContext);
	}

	public static void deleteCPDefinitionOptionRel(long cpDefinitionOptionRelId)
		throws PortalException {

		getService().deleteCPDefinitionOptionRel(cpDefinitionOptionRelId);
	}

	public static CPDefinitionOptionRel fetchCPDefinitionOptionRel(
			long cpDefinitionOptionRelId)
		throws PortalException {

		return getService().fetchCPDefinitionOptionRel(cpDefinitionOptionRelId);
	}

	public static CPDefinitionOptionRel fetchCPDefinitionOptionRel(
			long cpDefinitionId, long cpOptionId)
		throws PortalException {

		return getService().fetchCPDefinitionOptionRel(
			cpDefinitionId, cpOptionId);
	}

	public static CPDefinitionOptionRel
			fetchCPDefinitionOptionRelByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().fetchCPDefinitionOptionRelByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	public static CPDefinitionOptionRel getCPDefinitionOptionRel(
			long cpDefinitionOptionRelId)
		throws PortalException {

		return getService().getCPDefinitionOptionRel(cpDefinitionOptionRelId);
	}

	public static CPDefinitionOptionRel
			getCPDefinitionOptionRelByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getCPDefinitionOptionRelByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	public static Map<Long, List<Long>>
			getCPDefinitionOptionRelCPDefinitionOptionValueRelIds(
				long cpDefinitionId, String json)
		throws PortalException {

		return getService().
			getCPDefinitionOptionRelCPDefinitionOptionValueRelIds(
				cpDefinitionId, json);
	}

	public static Map<String, List<String>>
			getCPDefinitionOptionRelKeysCPDefinitionOptionValueRelKeys(
				long cpInstanceId)
		throws PortalException {

		return getService().
			getCPDefinitionOptionRelKeysCPDefinitionOptionValueRelKeys(
				cpInstanceId);
	}

	public static List<CPDefinitionOptionRel> getCPDefinitionOptionRels(
			long cpDefinitionId, boolean skuContributor)
		throws PortalException {

		return getService().getCPDefinitionOptionRels(
			cpDefinitionId, skuContributor);
	}

	public static List<CPDefinitionOptionRel> getCPDefinitionOptionRels(
			long cpDefinitionId, int start, int end)
		throws PortalException {

		return getService().getCPDefinitionOptionRels(
			cpDefinitionId, start, end);
	}

	public static List<CPDefinitionOptionRel> getCPDefinitionOptionRels(
			long cpDefinitionId, int start, int end,
			OrderByComparator<CPDefinitionOptionRel> orderByComparator)
		throws PortalException {

		return getService().getCPDefinitionOptionRels(
			cpDefinitionId, start, end, orderByComparator);
	}

	public static int getCPDefinitionOptionRelsCount(long cpDefinitionId)
		throws PortalException {

		return getService().getCPDefinitionOptionRelsCount(cpDefinitionId);
	}

	public static int getCPDefinitionOptionRelsCount(
			long cpDefinitionId, boolean skuContributor)
		throws PortalException {

		return getService().getCPDefinitionOptionRelsCount(
			cpDefinitionId, skuContributor);
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
		<CPDefinitionOptionRel> searchCPDefinitionOptionRels(
				long companyId, long groupId, long cpDefinitionId,
				String keywords, int start, int end,
				com.liferay.portal.kernel.search.Sort[] sorts)
			throws PortalException {

		return getService().searchCPDefinitionOptionRels(
			companyId, groupId, cpDefinitionId, keywords, start, end, sorts);
	}

	public static int searchCPDefinitionOptionRelsCount(
			long companyId, long groupId, long cpDefinitionId, String keywords)
		throws PortalException {

		return getService().searchCPDefinitionOptionRelsCount(
			companyId, groupId, cpDefinitionId, keywords);
	}

	public static CPDefinitionOptionRel updateCPDefinitionOptionRel(
			long cpDefinitionOptionRelId, long cpOptionId,
			Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap,
			String commerceOptionTypeKey, double priority, boolean facetable,
			boolean required, boolean skuContributor,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCPDefinitionOptionRel(
			cpDefinitionOptionRelId, cpOptionId, nameMap, descriptionMap,
			commerceOptionTypeKey, priority, facetable, required,
			skuContributor, serviceContext);
	}

	public static CPDefinitionOptionRel updateCPDefinitionOptionRel(
			long cpDefinitionOptionRelId, long cpOptionId,
			Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap,
			String commerceOptionTypeKey, String infoItemServiceKey,
			double priority, boolean definedExternally, boolean facetable,
			boolean required, boolean skuContributor, String priceType,
			String typeSettings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCPDefinitionOptionRel(
			cpDefinitionOptionRelId, cpOptionId, nameMap, descriptionMap,
			commerceOptionTypeKey, infoItemServiceKey, priority,
			definedExternally, facetable, required, skuContributor, priceType,
			typeSettings, serviceContext);
	}

	public static CPDefinitionOptionRel updateExternalReferenceCode(
			long cpDefinitionOptionRelId, String externalReferenceCode)
		throws PortalException {

		return getService().updateExternalReferenceCode(
			cpDefinitionOptionRelId, externalReferenceCode);
	}

	public static CPDefinitionOptionRelService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CPDefinitionOptionRelService>
		_serviceSnapshot = new Snapshot<>(
			CPDefinitionOptionRelServiceUtil.class,
			CPDefinitionOptionRelService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-140585944