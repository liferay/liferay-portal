/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPConfigurationListRel;
import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link CPConfigurationListRelService}.
 *
 * @author Marco Leo
 * @see CPConfigurationListRelService
 * @generated
 */
public class CPConfigurationListRelServiceWrapper
	implements CPConfigurationListRelService,
			   ServiceWrapper<CPConfigurationListRelService> {

	public CPConfigurationListRelServiceWrapper() {
		this(null);
	}

	public CPConfigurationListRelServiceWrapper(
		CPConfigurationListRelService cpConfigurationListRelService) {

		_cpConfigurationListRelService = cpConfigurationListRelService;
	}

	@Override
	public CPConfigurationListRel addCPConfigurationListRel(
			String className, long classPK, long cpConfigurationListId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationListRelService.addCPConfigurationListRel(
			className, classPK, cpConfigurationListId);
	}

	@Override
	public CPConfigurationListRel deleteCPConfigurationListRel(
			long cpConfigurationListRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationListRelService.deleteCPConfigurationListRel(
			cpConfigurationListRelId);
	}

	@Override
	public void deleteCPConfigurationListRels(long cpConfigurationListId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpConfigurationListRelService.deleteCPConfigurationListRels(
			cpConfigurationListId);
	}

	@Override
	public void deleteCPConfigurationListRels(
			String className, long cpConfigurationListId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpConfigurationListRelService.deleteCPConfigurationListRels(
			className, cpConfigurationListId);
	}

	@Override
	public CPConfigurationListRel fetchCPConfigurationListRel(
			String className, long classPK, long cpConfigurationListId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationListRelService.fetchCPConfigurationListRel(
			className, classPK, cpConfigurationListId);
	}

	@Override
	public java.util.List<CPConfigurationListRel>
			getAccountEntryCPConfigurationListRels(
				long cpConfigurationListId, String keywords, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationListRelService.
			getAccountEntryCPConfigurationListRels(
				cpConfigurationListId, keywords, start, end);
	}

	@Override
	public int getAccountEntryCPConfigurationListRelsCount(
			long cpConfigurationListId, String keywords)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationListRelService.
			getAccountEntryCPConfigurationListRelsCount(
				cpConfigurationListId, keywords);
	}

	@Override
	public java.util.List<CPConfigurationListRel>
			getAccountGroupCPConfigurationListRels(
				long cpConfigurationListId, String keywords, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationListRelService.
			getAccountGroupCPConfigurationListRels(
				cpConfigurationListId, keywords, start, end);
	}

	@Override
	public int getAccountGroupCPConfigurationListRelsCount(
			long cpConfigurationListId, String keywords)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationListRelService.
			getAccountGroupCPConfigurationListRelsCount(
				cpConfigurationListId, keywords);
	}

	@Override
	public java.util.List<CPConfigurationListRel>
			getCommerceOrderTypeCPConfigurationListRels(
				long cpConfigurationListId, String keywords, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationListRelService.
			getCommerceOrderTypeCPConfigurationListRels(
				cpConfigurationListId, keywords, start, end);
	}

	@Override
	public int getCommerceOrderTypeCPConfigurationListRelsCount(
			long cpConfigurationListId, String keywords)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationListRelService.
			getCommerceOrderTypeCPConfigurationListRelsCount(
				cpConfigurationListId, keywords);
	}

	@Override
	public CPConfigurationListRel getCPConfigurationListRel(
			long cpConfigurationListRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationListRelService.getCPConfigurationListRel(
			cpConfigurationListRelId);
	}

	@Override
	public java.util.List<CPConfigurationListRel> getCPConfigurationListRels(
			long cpConfigurationListId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationListRelService.getCPConfigurationListRels(
			cpConfigurationListId);
	}

	@Override
	public java.util.List<CPConfigurationListRel> getCPConfigurationListRels(
			long cpConfigurationListId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPConfigurationListRel> orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationListRelService.getCPConfigurationListRels(
			cpConfigurationListId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<CPConfigurationListRel> getCPConfigurationListRels(
			String className, long cpConfigurationListId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationListRelService.getCPConfigurationListRels(
			className, cpConfigurationListId);
	}

	@Override
	public java.util.List<CPConfigurationListRel> getCPConfigurationListRels(
			String className, long cpConfigurationListId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPConfigurationListRel> orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationListRelService.getCPConfigurationListRels(
			className, cpConfigurationListId, start, end, orderByComparator);
	}

	@Override
	public int getCPConfigurationListRelsCount(long cpConfigurationListId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationListRelService.getCPConfigurationListRelsCount(
			cpConfigurationListId);
	}

	@Override
	public int getCPConfigurationListRelsCount(
			String className, long cpConfigurationListId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationListRelService.getCPConfigurationListRelsCount(
			className, cpConfigurationListId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _cpConfigurationListRelService.getOSGiServiceIdentifier();
	}

	@Override
	public CPConfigurationListRelService getWrappedService() {
		return _cpConfigurationListRelService;
	}

	@Override
	public void setWrappedService(
		CPConfigurationListRelService cpConfigurationListRelService) {

		_cpConfigurationListRelService = cpConfigurationListRelService;
	}

	private CPConfigurationListRelService _cpConfigurationListRelService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1399256063