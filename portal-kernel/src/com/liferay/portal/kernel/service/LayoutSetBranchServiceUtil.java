/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.LayoutSetBranch;

import java.util.List;

/**
 * Provides the remote service utility for LayoutSetBranch. This utility wraps
 * <code>com.liferay.portal.service.impl.LayoutSetBranchServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutSetBranchService
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 * @generated
 */
@Deprecated
public class LayoutSetBranchServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.service.impl.LayoutSetBranchServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static LayoutSetBranch addLayoutSetBranch(
			long groupId, boolean privateLayout, String name,
			String description, boolean master, long copyLayoutSetBranchId,
			ServiceContext serviceContext)
		throws PortalException {

		return getService().addLayoutSetBranch(
			groupId, privateLayout, name, description, master,
			copyLayoutSetBranchId, serviceContext);
	}

	public static void deleteLayoutSetBranch(long layoutSetBranchId)
		throws PortalException {

		getService().deleteLayoutSetBranch(layoutSetBranchId);
	}

	public static void deleteLayoutSetBranch(
			long currentLayoutPlid, long layoutSetBranchId)
		throws PortalException {

		getService().deleteLayoutSetBranch(
			currentLayoutPlid, layoutSetBranchId);
	}

	public static List<LayoutSetBranch> getLayoutSetBranches(
		long groupId, boolean privateLayout) {

		return getService().getLayoutSetBranches(groupId, privateLayout);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static LayoutSetBranch mergeLayoutSetBranch(
			long layoutSetBranchId, long mergeLayoutSetBranchId,
			ServiceContext serviceContext)
		throws PortalException {

		return getService().mergeLayoutSetBranch(
			layoutSetBranchId, mergeLayoutSetBranchId, serviceContext);
	}

	public static LayoutSetBranch updateLayoutSetBranch(
			long groupId, long layoutSetBranchId, String name,
			String description, ServiceContext serviceContext)
		throws PortalException {

		return getService().updateLayoutSetBranch(
			groupId, layoutSetBranchId, name, description, serviceContext);
	}

	public static LayoutSetBranchService getService() {
		return _service;
	}

	public static void setService(LayoutSetBranchService service) {
		_service = service;
	}

	private static volatile LayoutSetBranchService _service;

}
// LIFERAY-SERVICE-BUILDER-HASH:-136894551