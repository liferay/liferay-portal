/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.design.library.web.internal.display.context;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.design.library.web.internal.constants.DesignLibraryAdminFDSNames;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.HashMapBuilder;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Mario Leandro
 */
public class ConnectedSitesDesignLibraryDisplayContext
	extends BaseDesignLibraryDisplayContext {

	public ConnectedSitesDesignLibraryDisplayContext(
		DepotEntry depotEntry, HttpServletRequest httpServletRequest) {

		super(depotEntry, httpServletRequest);
	}

	public String getConnectedSitesAPIURL() throws PortalException {
		return getAssetLibraryURL(
			getGroup(), "/connected-sites?page=1&pageSize=10");
	}

	public Map<String, Object> getConnectedSitesEmptyState() {
		return buildEmptyState(
			"connect-sites-to-this-design-library",
			"no-sites-are-connected-yet");
	}

	public Map<String, Object> getConnectedSitesFDSAdditionalProps()
		throws PortalException {

		Group group = getGroup();

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		return HashMapBuilder.<String, Object>put(
			"externalReferenceCode", group.getExternalReferenceCode()
		).put(
			"hasConnectSitesPermission",
			permissionChecker.hasPermission(
				group, DepotEntry.class.getName(), group.getClassPK(),
				ActionKeys.UPDATE)
		).put(
			"refreshDataSetIds",
			new String[] {
				DesignLibraryAdminFDSNames.DESIGN_LIBRARY_CONNECTED_SITES
			}
		).build();
	}

	public Map<String, Object> getConnectedSitesSectionHeaderProps()
		throws PortalException {

		return HashMapBuilder.<String, Object>putAll(
			getConnectedSitesFDSAdditionalProps()
		).put(
			"count",
			() -> {
				DepotEntryGroupRelLocalService depotEntryGroupRelLocalService =
					_depotEntryGroupRelLocalServiceSnapshot.get();

				if (depotEntryGroupRelLocalService == null) {
					return 0;
				}

				return depotEntryGroupRelLocalService.
					getDepotEntryGroupRelsCount(depotEntry);
			}
		).build();
	}

	private static final Snapshot<DepotEntryGroupRelLocalService>
		_depotEntryGroupRelLocalServiceSnapshot = new Snapshot<>(
			ConnectedSitesDesignLibraryDisplayContext.class,
			DepotEntryGroupRelLocalService.class);

}