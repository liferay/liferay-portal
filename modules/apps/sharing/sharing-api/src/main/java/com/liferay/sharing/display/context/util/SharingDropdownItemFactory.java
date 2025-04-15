/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.display.context.util;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownContextItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.kernel.exception.PortalException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Alejandro Tardín
 */
public interface SharingDropdownItemFactory {

	public DropdownItem createCopyLinkDropdownItem(
		String className, long classPK, HttpServletRequest httpServletRequest);

	public DropdownItem createManageCollaboratorsDropdownItem(
			String className, long classPK,
			HttpServletRequest httpServletRequest)
		throws PortalException;

	public UnsafeConsumer<DropdownContextItem, Exception>
			createShareActionUnsafeConsumer(
				String className, long classPK,
				HttpServletRequest httpServletRequest)
		throws PortalException;

	public DropdownItem createShareDropdownItem(
			String className, long classPK,
			HttpServletRequest httpServletRequest)
		throws PortalException;

}