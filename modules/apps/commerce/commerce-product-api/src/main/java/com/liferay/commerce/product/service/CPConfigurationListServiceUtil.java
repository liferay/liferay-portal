/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;

import java.util.List;

/**
 * Provides the remote service utility for CPConfigurationList. This utility wraps
 * <code>com.liferay.commerce.product.service.impl.CPConfigurationListServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Marco Leo
 * @see CPConfigurationListService
 * @generated
 */
public class CPConfigurationListServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.product.service.impl.CPConfigurationListServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static CPConfigurationList addCPConfigurationList(
			String externalReferenceCode, long groupId,
			long parentCPConfigurationListId, boolean masterCPConfigurationList,
			String name, double priority, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire)
		throws PortalException {

		return getService().addCPConfigurationList(
			externalReferenceCode, groupId, parentCPConfigurationListId,
			masterCPConfigurationList, name, priority, displayDateMonth,
			displayDateDay, displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire);
	}

	public static CPConfigurationList addOrUpdateCPConfigurationList(
			String externalReferenceCode, long companyId, long groupId,
			long parentCPConfigurationListId, boolean masterCPConfigurationList,
			String name, double priority, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire)
		throws PortalException {

		return getService().addOrUpdateCPConfigurationList(
			externalReferenceCode, companyId, groupId,
			parentCPConfigurationListId, masterCPConfigurationList, name,
			priority, displayDateMonth, displayDateDay, displayDateYear,
			displayDateHour, displayDateMinute, expirationDateMonth,
			expirationDateDay, expirationDateYear, expirationDateHour,
			expirationDateMinute, neverExpire);
	}

	public static CPConfigurationList deleteCPConfigurationList(
			CPConfigurationList cpConfigurationList)
		throws PortalException {

		return getService().deleteCPConfigurationList(cpConfigurationList);
	}

	public static CPConfigurationList deleteCPConfigurationList(
			long cpConfigurationListId)
		throws PortalException {

		return getService().deleteCPConfigurationList(cpConfigurationListId);
	}

	public static CPConfigurationList
			fetchCPConfigurationListByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().fetchCPConfigurationListByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	public static CPConfigurationList getCPConfigurationList(
			long cpConfigurationLisId)
		throws PortalException {

		return getService().getCPConfigurationList(cpConfigurationLisId);
	}

	public static CPConfigurationList
			getCPConfigurationListByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getCPConfigurationListByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	public static List<CPConfigurationList> getCPConfigurationLists(
			long groupId, long companyId)
		throws PortalException {

		return getService().getCPConfigurationLists(groupId, companyId);
	}

	public static CPConfigurationList getMasterCPConfigurationList(long groupId)
		throws PortalException {

		return getService().getMasterCPConfigurationList(groupId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static CPConfigurationList updateCPConfigurationList(
			String externalReferenceCode, long cpConfigurationListId,
			long groupId, long parentCPConfigurationListId,
			boolean masterCPConfigurationList, String name, double priority,
			int displayDateMonth, int displayDateDay, int displayDateYear,
			int displayDateHour, int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire)
		throws PortalException {

		return getService().updateCPConfigurationList(
			externalReferenceCode, cpConfigurationListId, groupId,
			parentCPConfigurationListId, masterCPConfigurationList, name,
			priority, displayDateMonth, displayDateDay, displayDateYear,
			displayDateHour, displayDateMinute, expirationDateMonth,
			expirationDateDay, expirationDateYear, expirationDateHour,
			expirationDateMinute, neverExpire);
	}

	public static CPConfigurationListService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CPConfigurationListService> _serviceSnapshot =
		new Snapshot<>(
			CPConfigurationListServiceUtil.class,
			CPConfigurationListService.class);

}