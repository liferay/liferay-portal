/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPTaxCategory;
import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link CPTaxCategoryService}.
 *
 * @author Marco Leo
 * @see CPTaxCategoryService
 * @generated
 */
public class CPTaxCategoryServiceWrapper
	implements CPTaxCategoryService, ServiceWrapper<CPTaxCategoryService> {

	public CPTaxCategoryServiceWrapper() {
		this(null);
	}

	public CPTaxCategoryServiceWrapper(
		CPTaxCategoryService cpTaxCategoryService) {

		_cpTaxCategoryService = cpTaxCategoryService;
	}

	@Override
	public CPTaxCategory addCPTaxCategory(
			String externalReferenceCode,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpTaxCategoryService.addCPTaxCategory(
			externalReferenceCode, nameMap, descriptionMap, serviceContext);
	}

	@Override
	public int countCPTaxCategoriesByCompanyId(long companyId, String keyword)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpTaxCategoryService.countCPTaxCategoriesByCompanyId(
			companyId, keyword);
	}

	@Override
	public void deleteCPTaxCategory(long cpTaxCategoryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpTaxCategoryService.deleteCPTaxCategory(cpTaxCategoryId);
	}

	@Override
	public CPTaxCategory fetchCPTaxCategoryByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpTaxCategoryService.fetchCPTaxCategoryByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	@Override
	public java.util.List<CPTaxCategory> findCPTaxCategoriesByCompanyId(
			long companyId, String keyword, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpTaxCategoryService.findCPTaxCategoriesByCompanyId(
			companyId, keyword, start, end);
	}

	@Override
	public java.util.List<CPTaxCategory> getCPTaxCategories(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpTaxCategoryService.getCPTaxCategories(companyId);
	}

	@Override
	public java.util.List<CPTaxCategory> getCPTaxCategories(
			long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<CPTaxCategory>
				orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpTaxCategoryService.getCPTaxCategories(
			companyId, start, end, orderByComparator);
	}

	@Override
	public int getCPTaxCategoriesCount(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpTaxCategoryService.getCPTaxCategoriesCount(companyId);
	}

	@Override
	public CPTaxCategory getCPTaxCategory(long cpTaxCategoryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpTaxCategoryService.getCPTaxCategory(cpTaxCategoryId);
	}

	@Override
	public CPTaxCategory getCPTaxCategoryByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpTaxCategoryService.getCPTaxCategoryByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	@Override
	public CPTaxCategory getOrAddEmptyCPTaxCategory(
			String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpTaxCategoryService.getOrAddEmptyCPTaxCategory(
			externalReferenceCode);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _cpTaxCategoryService.getOSGiServiceIdentifier();
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult<CPTaxCategory>
			searchCPTaxCategories(
				long companyId, String keywords, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpTaxCategoryService.searchCPTaxCategories(
			companyId, keywords, start, end, sort);
	}

	@Override
	public CPTaxCategory updateCPTaxCategory(
			String externalReferenceCode, long cpTaxCategoryId,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpTaxCategoryService.updateCPTaxCategory(
			externalReferenceCode, cpTaxCategoryId, nameMap, descriptionMap);
	}

	@Override
	public CPTaxCategoryService getWrappedService() {
		return _cpTaxCategoryService;
	}

	@Override
	public void setWrappedService(CPTaxCategoryService cpTaxCategoryService) {
		_cpTaxCategoryService = cpTaxCategoryService;
	}

	private CPTaxCategoryService _cpTaxCategoryService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1987741531