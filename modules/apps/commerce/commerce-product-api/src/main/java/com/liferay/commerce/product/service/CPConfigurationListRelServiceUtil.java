/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPConfigurationListRel;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

/**
 * Provides the remote service utility for CPConfigurationListRel. This utility wraps
 * <code>com.liferay.commerce.product.service.impl.CPConfigurationListRelServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Marco Leo
 * @see CPConfigurationListRelService
 * @generated
 */
public class CPConfigurationListRelServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.product.service.impl.CPConfigurationListRelServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static CPConfigurationListRel addCPConfigurationListRel(
			String className, long classPK, long cpConfigurationListId)
		throws PortalException {

		return getService().addCPConfigurationListRel(
			className, classPK, cpConfigurationListId);
	}

	public static CPConfigurationListRel deleteCPConfigurationListRel(
			long cpConfigurationListRelId)
		throws PortalException {

		return getService().deleteCPConfigurationListRel(
			cpConfigurationListRelId);
	}

	public static void deleteCPConfigurationListRels(long cpConfigurationListId)
		throws PortalException {

		getService().deleteCPConfigurationListRels(cpConfigurationListId);
	}

	public static void deleteCPConfigurationListRels(
			String className, long cpConfigurationListId)
		throws PortalException {

		getService().deleteCPConfigurationListRels(
			className, cpConfigurationListId);
	}

	public static CPConfigurationListRel fetchCPConfigurationListRel(
			String className, long classPK, long cpConfigurationListId)
		throws PortalException {

		return getService().fetchCPConfigurationListRel(
			className, classPK, cpConfigurationListId);
	}

	public static List<CPConfigurationListRel>
			getAccountEntryCPConfigurationListRels(
				long cpConfigurationListId, String keywords, int start, int end)
		throws PortalException {

		return getService().getAccountEntryCPConfigurationListRels(
			cpConfigurationListId, keywords, start, end);
	}

	public static int getAccountEntryCPConfigurationListRelsCount(
			long cpConfigurationListId, String keywords)
		throws PortalException {

		return getService().getAccountEntryCPConfigurationListRelsCount(
			cpConfigurationListId, keywords);
	}

	public static List<CPConfigurationListRel>
			getAccountGroupCPConfigurationListRels(
				long cpConfigurationListId, String keywords, int start, int end)
		throws PortalException {

		return getService().getAccountGroupCPConfigurationListRels(
			cpConfigurationListId, keywords, start, end);
	}

	public static int getAccountGroupCPConfigurationListRelsCount(
			long cpConfigurationListId, String keywords)
		throws PortalException {

		return getService().getAccountGroupCPConfigurationListRelsCount(
			cpConfigurationListId, keywords);
	}

	public static List<CPConfigurationListRel>
			getCommerceOrderTypeCPConfigurationListRels(
				long cpConfigurationListId, String keywords, int start, int end)
		throws PortalException {

		return getService().getCommerceOrderTypeCPConfigurationListRels(
			cpConfigurationListId, keywords, start, end);
	}

	public static int getCommerceOrderTypeCPConfigurationListRelsCount(
			long cpConfigurationListId, String keywords)
		throws PortalException {

		return getService().getCommerceOrderTypeCPConfigurationListRelsCount(
			cpConfigurationListId, keywords);
	}

	public static CPConfigurationListRel getCPConfigurationListRel(
			long cpConfigurationListRelId)
		throws PortalException {

		return getService().getCPConfigurationListRel(cpConfigurationListRelId);
	}

	public static List<CPConfigurationListRel> getCPConfigurationListRels(
			long cpConfigurationListId)
		throws PortalException {

		return getService().getCPConfigurationListRels(cpConfigurationListId);
	}

	public static List<CPConfigurationListRel> getCPConfigurationListRels(
			long cpConfigurationListId, int start, int end,
			OrderByComparator<CPConfigurationListRel> orderByComparator)
		throws PortalException {

		return getService().getCPConfigurationListRels(
			cpConfigurationListId, start, end, orderByComparator);
	}

	public static List<CPConfigurationListRel> getCPConfigurationListRels(
			String className, long cpConfigurationListId)
		throws PortalException {

		return getService().getCPConfigurationListRels(
			className, cpConfigurationListId);
	}

	public static List<CPConfigurationListRel> getCPConfigurationListRels(
			String className, long cpConfigurationListId, int start, int end,
			OrderByComparator<CPConfigurationListRel> orderByComparator)
		throws PortalException {

		return getService().getCPConfigurationListRels(
			className, cpConfigurationListId, start, end, orderByComparator);
	}

	public static int getCPConfigurationListRelsCount(
			long cpConfigurationListId)
		throws PortalException {

		return getService().getCPConfigurationListRelsCount(
			cpConfigurationListId);
	}

	public static int getCPConfigurationListRelsCount(
			String className, long cpConfigurationListId)
		throws PortalException {

		return getService().getCPConfigurationListRelsCount(
			className, cpConfigurationListId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static CPConfigurationListRelService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CPConfigurationListRelService>
		_serviceSnapshot = new Snapshot<>(
			CPConfigurationListRelServiceUtil.class,
			CPConfigurationListRelService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:2001387440