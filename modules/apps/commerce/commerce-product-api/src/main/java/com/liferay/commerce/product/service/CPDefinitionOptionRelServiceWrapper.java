/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link CPDefinitionOptionRelService}.
 *
 * @author Marco Leo
 * @see CPDefinitionOptionRelService
 * @generated
 */
public class CPDefinitionOptionRelServiceWrapper
	implements CPDefinitionOptionRelService,
			   ServiceWrapper<CPDefinitionOptionRelService> {

	public CPDefinitionOptionRelServiceWrapper() {
		this(null);
	}

	public CPDefinitionOptionRelServiceWrapper(
		CPDefinitionOptionRelService cpDefinitionOptionRelService) {

		_cpDefinitionOptionRelService = cpDefinitionOptionRelService;
	}

	@Override
	public CPDefinitionOptionRel addCPDefinitionOptionRel(
			long cpDefinitionId, long cpOptionId,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String commerceOptionTypeKey, double priority, boolean facetable,
			boolean required, boolean skuContributor, boolean importOptionValue,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionRelService.addCPDefinitionOptionRel(
			cpDefinitionId, cpOptionId, nameMap, descriptionMap,
			commerceOptionTypeKey, priority, facetable, required,
			skuContributor, importOptionValue, serviceContext);
	}

	@Override
	public CPDefinitionOptionRel addCPDefinitionOptionRel(
			long cpDefinitionId, long cpOptionId,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String commerceOptionTypeKey, String infoItemServiceKey,
			double priority, boolean definedExternally, boolean facetable,
			boolean required, boolean skuContributor, boolean importOptionValue,
			String priceType, String typeSettings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionRelService.addCPDefinitionOptionRel(
			cpDefinitionId, cpOptionId, nameMap, descriptionMap,
			commerceOptionTypeKey, infoItemServiceKey, priority,
			definedExternally, facetable, required, skuContributor,
			importOptionValue, priceType, typeSettings, serviceContext);
	}

	@Override
	public CPDefinitionOptionRel addCPDefinitionOptionRel(
			long cpDefinitionId, long cpOptionId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionRelService.addCPDefinitionOptionRel(
			cpDefinitionId, cpOptionId, serviceContext);
	}

	@Override
	public void deleteCPDefinitionOptionRel(long cpDefinitionOptionRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpDefinitionOptionRelService.deleteCPDefinitionOptionRel(
			cpDefinitionOptionRelId);
	}

	@Override
	public CPDefinitionOptionRel fetchCPDefinitionOptionRel(
			long cpDefinitionOptionRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionRelService.fetchCPDefinitionOptionRel(
			cpDefinitionOptionRelId);
	}

	@Override
	public CPDefinitionOptionRel fetchCPDefinitionOptionRel(
			long cpDefinitionId, long cpOptionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionRelService.fetchCPDefinitionOptionRel(
			cpDefinitionId, cpOptionId);
	}

	@Override
	public CPDefinitionOptionRel
			fetchCPDefinitionOptionRelByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionRelService.
			fetchCPDefinitionOptionRelByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public CPDefinitionOptionRel getCPDefinitionOptionRel(
			long cpDefinitionOptionRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionRelService.getCPDefinitionOptionRel(
			cpDefinitionOptionRelId);
	}

	@Override
	public CPDefinitionOptionRel
			getCPDefinitionOptionRelByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionRelService.
			getCPDefinitionOptionRelByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public java.util.Map<Long, java.util.List<Long>>
			getCPDefinitionOptionRelCPDefinitionOptionValueRelIds(
				long cpDefinitionId, String json)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionRelService.
			getCPDefinitionOptionRelCPDefinitionOptionValueRelIds(
				cpDefinitionId, json);
	}

	@Override
	public java.util.Map<String, java.util.List<String>>
			getCPDefinitionOptionRelKeysCPDefinitionOptionValueRelKeys(
				long cpInstanceId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionRelService.
			getCPDefinitionOptionRelKeysCPDefinitionOptionValueRelKeys(
				cpInstanceId);
	}

	@Override
	public java.util.List<CPDefinitionOptionRel> getCPDefinitionOptionRels(
			long cpDefinitionId, boolean skuContributor)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionRelService.getCPDefinitionOptionRels(
			cpDefinitionId, skuContributor);
	}

	@Override
	public java.util.List<CPDefinitionOptionRel> getCPDefinitionOptionRels(
			long cpDefinitionId, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionRelService.getCPDefinitionOptionRels(
			cpDefinitionId, start, end);
	}

	@Override
	public java.util.List<CPDefinitionOptionRel> getCPDefinitionOptionRels(
			long cpDefinitionId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPDefinitionOptionRel> orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionRelService.getCPDefinitionOptionRels(
			cpDefinitionId, start, end, orderByComparator);
	}

	@Override
	public int getCPDefinitionOptionRelsCount(long cpDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionRelService.getCPDefinitionOptionRelsCount(
			cpDefinitionId);
	}

	@Override
	public int getCPDefinitionOptionRelsCount(
			long cpDefinitionId, boolean skuContributor)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionRelService.getCPDefinitionOptionRelsCount(
			cpDefinitionId, skuContributor);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _cpDefinitionOptionRelService.getOSGiServiceIdentifier();
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult
		<CPDefinitionOptionRel> searchCPDefinitionOptionRels(
				long companyId, long groupId, long cpDefinitionId,
				String keywords, int start, int end,
				com.liferay.portal.kernel.search.Sort[] sorts)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionRelService.searchCPDefinitionOptionRels(
			companyId, groupId, cpDefinitionId, keywords, start, end, sorts);
	}

	@Override
	public int searchCPDefinitionOptionRelsCount(
			long companyId, long groupId, long cpDefinitionId, String keywords)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionRelService.searchCPDefinitionOptionRelsCount(
			companyId, groupId, cpDefinitionId, keywords);
	}

	@Override
	public CPDefinitionOptionRel updateCPDefinitionOptionRel(
			long cpDefinitionOptionRelId, long cpOptionId,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String commerceOptionTypeKey, double priority, boolean facetable,
			boolean required, boolean skuContributor,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionRelService.updateCPDefinitionOptionRel(
			cpDefinitionOptionRelId, cpOptionId, nameMap, descriptionMap,
			commerceOptionTypeKey, priority, facetable, required,
			skuContributor, serviceContext);
	}

	@Override
	public CPDefinitionOptionRel updateCPDefinitionOptionRel(
			long cpDefinitionOptionRelId, long cpOptionId,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String commerceOptionTypeKey, String infoItemServiceKey,
			double priority, boolean definedExternally, boolean facetable,
			boolean required, boolean skuContributor, String priceType,
			String typeSettings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionRelService.updateCPDefinitionOptionRel(
			cpDefinitionOptionRelId, cpOptionId, nameMap, descriptionMap,
			commerceOptionTypeKey, infoItemServiceKey, priority,
			definedExternally, facetable, required, skuContributor, priceType,
			typeSettings, serviceContext);
	}

	@Override
	public CPDefinitionOptionRel updateExternalReferenceCode(
			long cpDefinitionOptionRelId, String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionRelService.updateExternalReferenceCode(
			cpDefinitionOptionRelId, externalReferenceCode);
	}

	@Override
	public CPDefinitionOptionRelService getWrappedService() {
		return _cpDefinitionOptionRelService;
	}

	@Override
	public void setWrappedService(
		CPDefinitionOptionRelService cpDefinitionOptionRelService) {

		_cpDefinitionOptionRelService = cpDefinitionOptionRelService;
	}

	private CPDefinitionOptionRelService _cpDefinitionOptionRelService;

}
// LIFERAY-SERVICE-BUILDER-HASH:372059362