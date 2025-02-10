/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.service;

import com.liferay.depot.model.DepotEntryGroupRel;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;

import java.util.List;

/**
 * Provides the remote service utility for DepotEntryGroupRel. This utility wraps
 * <code>com.liferay.depot.service.impl.DepotEntryGroupRelServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see DepotEntryGroupRelService
 * @generated
 */
public class DepotEntryGroupRelServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.depot.service.impl.DepotEntryGroupRelServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static DepotEntryGroupRel addDepotEntryGroupRel(
			long depotEntryId, long toGroupId)
		throws PortalException {

		return getService().addDepotEntryGroupRel(depotEntryId, toGroupId);
	}

	public static DepotEntryGroupRel deleteDepotEntryGroupRel(
			long depotEntryGroupRelId)
		throws PortalException {

		return getService().deleteDepotEntryGroupRel(depotEntryGroupRelId);
	}

	public static DepotEntryGroupRel
			getDepotEntryGroupRelByDepotEntryIdToGroupId(
				long depotEntryId, long toGroupId)
		throws PortalException {

		return getService().getDepotEntryGroupRelByDepotEntryIdToGroupId(
			depotEntryId, toGroupId);
	}

	public static List<DepotEntryGroupRel> getDepotEntryGroupRels(
			long groupId, int start, int end)
		throws PortalException {

		return getService().getDepotEntryGroupRels(groupId, start, end);
	}

	public static int getDepotEntryGroupRelsCount(
			com.liferay.depot.model.DepotEntry depotEntry)
		throws PortalException {

		return getService().getDepotEntryGroupRelsCount(depotEntry);
	}

	public static int getDepotEntryGroupRelsCount(long groupId)
		throws PortalException {

		return getService().getDepotEntryGroupRelsCount(groupId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static DepotEntryGroupRel updateDDMStructuresAvailable(
			long depotEntryGroupRelId, boolean ddmStructuresAvailable)
		throws PortalException {

		return getService().updateDDMStructuresAvailable(
			depotEntryGroupRelId, ddmStructuresAvailable);
	}

	public static DepotEntryGroupRel updateSearchable(
			long depotEntryGroupRelId, boolean searchable)
		throws PortalException {

		return getService().updateSearchable(depotEntryGroupRelId, searchable);
	}

	public static DepotEntryGroupRelService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<DepotEntryGroupRelService> _serviceSnapshot =
		new Snapshot<>(
			DepotEntryGroupRelServiceUtil.class,
			DepotEntryGroupRelService.class);

}