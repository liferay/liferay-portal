/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link CPDefinitionOptionValueRelService}.
 *
 * @author Marco Leo
 * @see CPDefinitionOptionValueRelService
 * @generated
 */
public class CPDefinitionOptionValueRelServiceWrapper
	implements CPDefinitionOptionValueRelService,
			   ServiceWrapper<CPDefinitionOptionValueRelService> {

	public CPDefinitionOptionValueRelServiceWrapper() {
		this(null);
	}

	public CPDefinitionOptionValueRelServiceWrapper(
		CPDefinitionOptionValueRelService cpDefinitionOptionValueRelService) {

		_cpDefinitionOptionValueRelService = cpDefinitionOptionValueRelService;
	}

	@Override
	public CPDefinitionOptionValueRel addCPDefinitionOptionValueRel(
			long cpDefinitionOptionRelId, long cpInstanceId, String key,
			java.util.Map<java.util.Locale, String> nameMap,
			boolean preselected, java.math.BigDecimal deltaPrice,
			double priority, java.math.BigDecimal quantity,
			String unitOfMeasureKey,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionValueRelService.addCPDefinitionOptionValueRel(
			cpDefinitionOptionRelId, cpInstanceId, key, nameMap, preselected,
			deltaPrice, priority, quantity, unitOfMeasureKey, serviceContext);
	}

	@Override
	public CPDefinitionOptionValueRel addCPDefinitionOptionValueRel(
			long cpDefinitionOptionRelId, String key,
			java.util.Map<java.util.Locale, String> nameMap, double priority,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionValueRelService.addCPDefinitionOptionValueRel(
			cpDefinitionOptionRelId, key, nameMap, priority, serviceContext);
	}

	@Override
	public CPDefinitionOptionValueRel deleteCPDefinitionOptionValueRel(
			long cpDefinitionOptionValueRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionValueRelService.
			deleteCPDefinitionOptionValueRel(cpDefinitionOptionValueRelId);
	}

	@Override
	public CPDefinitionOptionValueRel fetchCPDefinitionOptionValueRel(
			long cpDefinitionOptionValueRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionValueRelService.
			fetchCPDefinitionOptionValueRel(cpDefinitionOptionValueRelId);
	}

	@Override
	public CPDefinitionOptionValueRel fetchCPDefinitionOptionValueRel(
			long cpDefinitionOptionRelId, String key)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionValueRelService.
			fetchCPDefinitionOptionValueRel(cpDefinitionOptionRelId, key);
	}

	@Override
	public CPDefinitionOptionValueRel
			fetchCPDefinitionOptionValueRelByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionValueRelService.
			fetchCPDefinitionOptionValueRelByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public CPDefinitionOptionValueRel getCPDefinitionOptionValueRel(
			long cpDefinitionOptionValueRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionValueRelService.getCPDefinitionOptionValueRel(
			cpDefinitionOptionValueRelId);
	}

	@Override
	public java.util.List<CPDefinitionOptionValueRel>
			getCPDefinitionOptionValueRels(
				long cpDefinitionOptionRelId, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionValueRelService.
			getCPDefinitionOptionValueRels(cpDefinitionOptionRelId, start, end);
	}

	@Override
	public java.util.List<CPDefinitionOptionValueRel>
			getCPDefinitionOptionValueRels(
				long cpDefinitionOptionRelId, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<CPDefinitionOptionValueRel> orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionValueRelService.
			getCPDefinitionOptionValueRels(
				cpDefinitionOptionRelId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<CPDefinitionOptionValueRel>
			getCPDefinitionOptionValueRels(
				long groupId, String key, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionValueRelService.
			getCPDefinitionOptionValueRels(groupId, key, start, end);
	}

	@Override
	public int getCPDefinitionOptionValueRelsCount(long cpDefinitionOptionRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionValueRelService.
			getCPDefinitionOptionValueRelsCount(cpDefinitionOptionRelId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _cpDefinitionOptionValueRelService.getOSGiServiceIdentifier();
	}

	@Override
	public CPDefinitionOptionValueRel resetCPInstanceCPDefinitionOptionValueRel(
			long cpDefinitionOptionValueRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionValueRelService.
			resetCPInstanceCPDefinitionOptionValueRel(
				cpDefinitionOptionValueRelId);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult
		<CPDefinitionOptionValueRel> searchCPDefinitionOptionValueRels(
				long companyId, long groupId, long cpDefinitionOptionRelId,
				String keywords, int start, int end,
				com.liferay.portal.kernel.search.Sort[] sorts)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionValueRelService.
			searchCPDefinitionOptionValueRels(
				companyId, groupId, cpDefinitionOptionRelId, keywords, start,
				end, sorts);
	}

	@Override
	public int searchCPDefinitionOptionValueRelsCount(
			long companyId, long groupId, long cpDefinitionOptionRelId,
			String keywords)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionValueRelService.
			searchCPDefinitionOptionValueRelsCount(
				companyId, groupId, cpDefinitionOptionRelId, keywords);
	}

	@Override
	public CPDefinitionOptionValueRel updateCPDefinitionOptionValueRel(
			long cpDefinitionOptionValueRelId, long cpInstanceId, String key,
			java.util.Map<java.util.Locale, String> nameMap,
			boolean preselected, java.math.BigDecimal price, double priority,
			java.math.BigDecimal quantity, String unitOfMeasureKey,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionValueRelService.
			updateCPDefinitionOptionValueRel(
				cpDefinitionOptionValueRelId, cpInstanceId, key, nameMap,
				preselected, price, priority, quantity, unitOfMeasureKey,
				serviceContext);
	}

	@Override
	public CPDefinitionOptionValueRel
			updateCPDefinitionOptionValueRelPreselected(
				long cpDefinitionOptionValueRelId, boolean preselected)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionValueRelService.
			updateCPDefinitionOptionValueRelPreselected(
				cpDefinitionOptionValueRelId, preselected);
	}

	@Override
	public CPDefinitionOptionValueRel updateExternalReferenceCode(
			long cpDefinitionOptionValueRelId, String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionValueRelService.updateExternalReferenceCode(
			cpDefinitionOptionValueRelId, externalReferenceCode);
	}

	@Override
	public CPDefinitionOptionValueRelService getWrappedService() {
		return _cpDefinitionOptionValueRelService;
	}

	@Override
	public void setWrappedService(
		CPDefinitionOptionValueRelService cpDefinitionOptionValueRelService) {

		_cpDefinitionOptionValueRelService = cpDefinitionOptionValueRelService;
	}

	private CPDefinitionOptionValueRelService
		_cpDefinitionOptionValueRelService;

}
// LIFERAY-SERVICE-BUILDER-HASH:342000825