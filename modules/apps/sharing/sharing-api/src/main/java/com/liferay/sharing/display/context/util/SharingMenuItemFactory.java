/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.display.context.util;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.servlet.taglib.ui.MenuItem;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Adolfo Pérez
 */
public interface SharingMenuItemFactory {

	public MenuItem createManageCollaboratorsMenuItem(
			String className, long classPK,
			HttpServletRequest httpServletRequest)
		throws PortalException;

	public MenuItem createShareMenuItem(
			String className, long classPK,
			HttpServletRequest httpServletRequest)
		throws PortalException;

}