/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link CPConfigurationListService}.
 *
 * @author Marco Leo
 * @see CPConfigurationListService
 * @generated
 */
public class CPConfigurationListServiceWrapper
	implements CPConfigurationListService,
			   ServiceWrapper<CPConfigurationListService> {

	public CPConfigurationListServiceWrapper() {
		this(null);
	}

	public CPConfigurationListServiceWrapper(
		CPConfigurationListService cpConfigurationListService) {

		_cpConfigurationListService = cpConfigurationListService;
	}

	@Override
	public CPConfigurationList addCPConfigurationList(
			String externalReferenceCode, long groupId,
			long parentCPConfigurationListId, boolean masterCPConfigurationList,
			String name, double priority, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationListService.addCPConfigurationList(
			externalReferenceCode, groupId, parentCPConfigurationListId,
			masterCPConfigurationList, name, priority, displayDateMonth,
			displayDateDay, displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire);
	}

	@Override
	public CPConfigurationList addOrUpdateCPConfigurationList(
			String externalReferenceCode, long companyId, long groupId,
			long parentCPConfigurationListId, boolean masterCPConfigurationList,
			String name, double priority, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationListService.addOrUpdateCPConfigurationList(
			externalReferenceCode, companyId, groupId,
			parentCPConfigurationListId, masterCPConfigurationList, name,
			priority, displayDateMonth, displayDateDay, displayDateYear,
			displayDateHour, displayDateMinute, expirationDateMonth,
			expirationDateDay, expirationDateYear, expirationDateHour,
			expirationDateMinute, neverExpire);
	}

	@Override
	public CPConfigurationList deleteCPConfigurationList(
			CPConfigurationList cpConfigurationList)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationListService.deleteCPConfigurationList(
			cpConfigurationList);
	}

	@Override
	public CPConfigurationList deleteCPConfigurationList(
			long cpConfigurationListId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationListService.deleteCPConfigurationList(
			cpConfigurationListId);
	}

	@Override
	public CPConfigurationList fetchCPConfigurationListByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationListService.
			fetchCPConfigurationListByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public CPConfigurationList getCPConfigurationList(long cpConfigurationLisId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationListService.getCPConfigurationList(
			cpConfigurationLisId);
	}

	@Override
	public CPConfigurationList getCPConfigurationListByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationListService.
			getCPConfigurationListByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public java.util.List<CPConfigurationList> getCPConfigurationLists(
			long groupId, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationListService.getCPConfigurationLists(
			groupId, companyId);
	}

	@Override
	public CPConfigurationList getMasterCPConfigurationList(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationListService.getMasterCPConfigurationList(
			groupId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _cpConfigurationListService.getOSGiServiceIdentifier();
	}

	@Override
	public CPConfigurationList updateCPConfigurationList(
			String externalReferenceCode, long cpConfigurationListId,
			long groupId, long parentCPConfigurationListId,
			boolean masterCPConfigurationList, String name, double priority,
			int displayDateMonth, int displayDateDay, int displayDateYear,
			int displayDateHour, int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationListService.updateCPConfigurationList(
			externalReferenceCode, cpConfigurationListId, groupId,
			parentCPConfigurationListId, masterCPConfigurationList, name,
			priority, displayDateMonth, displayDateDay, displayDateYear,
			displayDateHour, displayDateMinute, expirationDateMonth,
			expirationDateDay, expirationDateYear, expirationDateHour,
			expirationDateMinute, neverExpire);
	}

	@Override
	public CPConfigurationListService getWrappedService() {
		return _cpConfigurationListService;
	}

	@Override
	public void setWrappedService(
		CPConfigurationListService cpConfigurationListService) {

		_cpConfigurationListService = cpConfigurationListService;
	}

	private CPConfigurationListService _cpConfigurationListService;

}