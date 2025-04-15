/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.renderer;

import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.portal.kernel.exception.PortalException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Pavel Savinov
 */
public interface FragmentPortletRenderer {

	public String renderPortlet(
			FragmentEntryLink fragmentEntryLink,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String portletName,
			String instanceId, String defaultPreferences)
		throws PortalException;

}