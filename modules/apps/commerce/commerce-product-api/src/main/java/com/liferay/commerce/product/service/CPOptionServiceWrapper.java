/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPOption;
import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link CPOptionService}.
 *
 * @author Marco Leo
 * @see CPOptionService
 * @generated
 */
public class CPOptionServiceWrapper
	implements CPOptionService, ServiceWrapper<CPOptionService> {

	public CPOptionServiceWrapper() {
		this(null);
	}

	public CPOptionServiceWrapper(CPOptionService cpOptionService) {
		_cpOptionService = cpOptionService;
	}

	@Override
	public CPOption addCPOption(
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String commerceOptionTypeKey, boolean facetable, boolean required,
			boolean skuContributor, String key,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionService.addCPOption(
			nameMap, descriptionMap, commerceOptionTypeKey, facetable, required,
			skuContributor, key, serviceContext);
	}

	@Override
	public CPOption addOrUpdateCPOption(
			String externalReferenceCode,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String commerceOptionTypeKey, boolean facetable, boolean required,
			boolean skuContributor, String key,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionService.addOrUpdateCPOption(
			externalReferenceCode, nameMap, descriptionMap,
			commerceOptionTypeKey, facetable, required, skuContributor, key,
			serviceContext);
	}

	@Override
	public void deleteCPOption(long cpOptionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpOptionService.deleteCPOption(cpOptionId);
	}

	@Override
	public CPOption fetchCPOption(long cpOptionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionService.fetchCPOption(cpOptionId);
	}

	@Override
	public CPOption fetchCPOption(long companyId, String key)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionService.fetchCPOption(companyId, key);
	}

	@Override
	public CPOption fetchCPOptionByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionService.fetchCPOptionByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	@Override
	public java.util.List<CPOption> findCPOptionByCompanyId(
			long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<CPOption>
				orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionService.findCPOptionByCompanyId(
			companyId, start, end, orderByComparator);
	}

	@Override
	public CPOption getCPOption(long cpOptionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionService.getCPOption(cpOptionId);
	}

	@Override
	public CPOption getOrAddEmptyCPOption(String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionService.getOrAddEmptyCPOption(externalReferenceCode);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _cpOptionService.getOSGiServiceIdentifier();
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult<CPOption>
			searchCPOptions(
				long companyId, String keywords, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionService.searchCPOptions(
			companyId, keywords, start, end, sort);
	}

	@Override
	public CPOption updateCPOption(
			long cpOptionId, java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String commerceOptionTypeKey, boolean facetable, boolean required,
			boolean skuContributor, String key,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionService.updateCPOption(
			cpOptionId, nameMap, descriptionMap, commerceOptionTypeKey,
			facetable, required, skuContributor, key, serviceContext);
	}

	@Override
	public CPOption updateCPOptionExternalReferenceCode(
			String externalReferenceCode, long cpOptionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionService.updateCPOptionExternalReferenceCode(
			externalReferenceCode, cpOptionId);
	}

	@Override
	public CPOptionService getWrappedService() {
		return _cpOptionService;
	}

	@Override
	public void setWrappedService(CPOptionService cpOptionService) {
		_cpOptionService = cpOptionService;
	}

	private CPOptionService _cpOptionService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1923882150