/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.design.library.resource.type;

import com.liferay.depot.model.DepotEntry;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Lourdes Fernández Besada
 * @author Thiago Buarque
 */
@ProviderType
public interface DesignLibraryResourceTypeContributor {

	public String getColor();

	public default List<DesignLibraryResourceCreationItem> getCreationItems(
			HttpServletRequest httpServletRequest, DepotEntry depotEntry,
			String backURL)
		throws PortalException {

		return Collections.emptyList();
	}

	public String getDefaultActionId();

	public String getEntryClassName();

	public List<FDSActionDropdownItem> getFDSActionDropdownItems(
			HttpServletRequest httpServletRequest, DepotEntry depotEntry,
			String backURL)
		throws PortalException;

	public String getIcon();

	public String getKey();

	public String getLabel(Locale locale);

	public default String getType() {
		return null;
	}

	public boolean hasAddPermission(
		PermissionChecker permissionChecker, DepotEntry depotEntry);

	public boolean hasViewPermission(
		PermissionChecker permissionChecker, DepotEntry depotEntry);

}