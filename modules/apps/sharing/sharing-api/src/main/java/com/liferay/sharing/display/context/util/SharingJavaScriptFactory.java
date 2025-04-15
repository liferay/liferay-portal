/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.display.context.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Alejandro Tardín
 */
public interface SharingJavaScriptFactory {

	public String createCopyLinkClickMethod(
			String className, long classPK,
			HttpServletRequest httpServletRequest)
		throws PortalException;

	/**
	 * @deprecated As of Mueller (7.2.x), with no direct replacement
	 */
	@Deprecated
	public default String createManageCollaboratorsJavaScript(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		return StringPool.BLANK;
	}

	public String createManageCollaboratorsOnClickMethod(
			String className, long classPK,
			HttpServletRequest httpServletRequest)
		throws PortalException;

	/**
	 * @deprecated As of Mueller (7.2.x), with no direct replacement
	 */
	@Deprecated
	public default String createSharingJavaScript(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		return StringPool.BLANK;
	}

	public String createSharingOnClickMethod(
			String className, long classPK,
			HttpServletRequest httpServletRequest)
		throws PortalException;

	public default void requestSharingJavaScript() {
	}

}