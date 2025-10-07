/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.service;

import com.liferay.depot.model.DepotEntry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;

import java.util.List;
import java.util.Map;

/**
 * Provides the remote service utility for DepotEntry. This utility wraps
 * <code>com.liferay.depot.service.impl.DepotEntryServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see DepotEntryService
 * @generated
 */
public class DepotEntryServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.depot.service.impl.DepotEntryServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static DepotEntry addDepotEntry(
			Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap, int type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addDepotEntry(
			nameMap, descriptionMap, type, serviceContext);
	}

	public static DepotEntry deleteDepotEntry(long depotEntryId)
		throws PortalException {

		return getService().deleteDepotEntry(depotEntryId);
	}

	public static DepotEntry fetchGroupDepotEntry(long groupId)
		throws PortalException {

		return getService().fetchGroupDepotEntry(groupId);
	}

	public static List<DepotEntry> getCurrentAndGroupConnectedDepotEntries(
			long groupId, int type, int start, int end)
		throws PortalException {

		return getService().getCurrentAndGroupConnectedDepotEntries(
			groupId, type, start, end);
	}

	public static DepotEntry getDepotEntry(long depotEntryId)
		throws PortalException {

		return getService().getDepotEntry(depotEntryId);
	}

	public static List<DepotEntry> getGroupConnectedDepotEntries(
			long groupId, boolean ddmStructuresAvailable, int start, int end)
		throws PortalException {

		return getService().getGroupConnectedDepotEntries(
			groupId, ddmStructuresAvailable, start, end);
	}

	public static List<DepotEntry> getGroupConnectedDepotEntries(
			long groupId, int type, int start, int end)
		throws PortalException {

		return getService().getGroupConnectedDepotEntries(
			groupId, type, start, end);
	}

	public static int getGroupConnectedDepotEntriesCount(long groupId, int type)
		throws PortalException {

		return getService().getGroupConnectedDepotEntriesCount(groupId, type);
	}

	public static DepotEntry getGroupDepotEntry(long groupId)
		throws PortalException {

		return getService().getGroupDepotEntry(groupId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static DepotEntry updateDepotEntry(
			long depotEntryId, Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap,
			Map<String, Boolean> depotAppCustomizationMap,
			com.liferay.portal.kernel.util.UnicodeProperties
				typeSettingsUnicodeProperties,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateDepotEntry(
			depotEntryId, nameMap, descriptionMap, depotAppCustomizationMap,
			typeSettingsUnicodeProperties, serviceContext);
	}

	public static DepotEntryService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<DepotEntryService> _serviceSnapshot =
		new Snapshot<>(DepotEntryServiceUtil.class, DepotEntryService.class);

}