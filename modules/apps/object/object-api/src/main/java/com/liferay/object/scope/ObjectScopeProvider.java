/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.scope;

import com.liferay.portal.kernel.exception.PortalException;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

/**
 * @author Marco Leo
 */
public interface ObjectScopeProvider {

	public long getGroupId(HttpServletRequest httpServletRequest)
		throws PortalException;

	public String getKey();

	public String getLabel(Locale locale);

	public String[] getRootPanelCategoryKeys();

	public boolean isGroupAware();

	public boolean isValidGroupId(long groupId);

}