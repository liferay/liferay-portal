/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.LayoutBranch;

/**
 * Provides the remote service utility for LayoutBranch. This utility wraps
 * <code>com.liferay.portal.service.impl.LayoutBranchServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutBranchService
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 * @generated
 */
@Deprecated
public class LayoutBranchServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.service.impl.LayoutBranchServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static LayoutBranch addLayoutBranch(
			long layoutRevisionId, String name, String description,
			boolean master, ServiceContext serviceContext)
		throws PortalException {

		return getService().addLayoutBranch(
			layoutRevisionId, name, description, master, serviceContext);
	}

	public static void deleteLayoutBranch(long layoutBranchId)
		throws PortalException {

		getService().deleteLayoutBranch(layoutBranchId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static LayoutBranch updateLayoutBranch(
			long layoutBranchId, String name, String description,
			ServiceContext serviceContext)
		throws PortalException {

		return getService().updateLayoutBranch(
			layoutBranchId, name, description, serviceContext);
	}

	public static LayoutBranchService getService() {
		return _service;
	}

	public static void setService(LayoutBranchService service) {
		_service = service;
	}

	private static volatile LayoutBranchService _service;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1064885691