/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.design.library.resource.type;

import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Thiago Buarque
 */
public interface DesignLibraryResourceType {

	public String getColor();

	public String getCreationItemsModule();

	public Map<String, Object> getCreationItemsProps(
		HttpServletRequest httpServletRequest, Group depotGroup,
		String backURL);

	public String getDefaultActionId();

	public String getEntryClassName();

	public List<FDSActionDropdownItem> getFDSActionDropdownItems(
		HttpServletRequest httpServletRequest, Group depotGroup,
		String backURL);

	public String getLabel(Locale locale);

	public String getSymbol();

	public boolean hasAddPermission(
		PermissionChecker permissionChecker, Group depotGroup);

	public boolean hasViewPermission(
		PermissionChecker permissionChecker, Group depotGroup);

}