/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPOptionCategory;
import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link CPOptionCategoryService}.
 *
 * @author Marco Leo
 * @see CPOptionCategoryService
 * @generated
 */
public class CPOptionCategoryServiceWrapper
	implements CPOptionCategoryService,
			   ServiceWrapper<CPOptionCategoryService> {

	public CPOptionCategoryServiceWrapper() {
		this(null);
	}

	public CPOptionCategoryServiceWrapper(
		CPOptionCategoryService cpOptionCategoryService) {

		_cpOptionCategoryService = cpOptionCategoryService;
	}

	@Override
	public CPOptionCategory addCPOptionCategory(
			String externalReferenceCode,
			java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			double priority, String key,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionCategoryService.addCPOptionCategory(
			externalReferenceCode, titleMap, descriptionMap, priority, key,
			serviceContext);
	}

	@Override
	public CPOptionCategory addOrUpdateCPOptionCategory(
			String externalReferenceCode, long cpOptionCategoryId,
			java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			double priority, String key,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionCategoryService.addOrUpdateCPOptionCategory(
			externalReferenceCode, cpOptionCategoryId, titleMap, descriptionMap,
			priority, key, serviceContext);
	}

	@Override
	public void deleteCPOptionCategory(long cpOptionCategoryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpOptionCategoryService.deleteCPOptionCategory(cpOptionCategoryId);
	}

	@Override
	public CPOptionCategory fetchCPOptionCategory(long cpOptionCategoryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionCategoryService.fetchCPOptionCategory(
			cpOptionCategoryId);
	}

	@Override
	public CPOptionCategory fetchCPOptionCategoryByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionCategoryService.
			fetchCPOptionCategoryByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public CPOptionCategory getCPOptionCategory(long cpOptionCategoryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionCategoryService.getCPOptionCategory(cpOptionCategoryId);
	}

	@Override
	public CPOptionCategory getCPOptionCategoryByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionCategoryService.
			getCPOptionCategoryByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public CPOptionCategory getOrAddEmptyCPOptionCategory(
			String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionCategoryService.getOrAddEmptyCPOptionCategory(
			externalReferenceCode);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _cpOptionCategoryService.getOSGiServiceIdentifier();
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult
		<CPOptionCategory> searchCPOptionCategories(
				long companyId, String keywords, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionCategoryService.searchCPOptionCategories(
			companyId, keywords, start, end, sort);
	}

	@Override
	public CPOptionCategory updateCPOptionCategory(
			String externalReferenceCode, long cpOptionCategoryId,
			java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			double priority, String key)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionCategoryService.updateCPOptionCategory(
			externalReferenceCode, cpOptionCategoryId, titleMap, descriptionMap,
			priority, key);
	}

	@Override
	public CPOptionCategoryService getWrappedService() {
		return _cpOptionCategoryService;
	}

	@Override
	public void setWrappedService(
		CPOptionCategoryService cpOptionCategoryService) {

		_cpOptionCategoryService = cpOptionCategoryService;
	}

	private CPOptionCategoryService _cpOptionCategoryService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1420062981