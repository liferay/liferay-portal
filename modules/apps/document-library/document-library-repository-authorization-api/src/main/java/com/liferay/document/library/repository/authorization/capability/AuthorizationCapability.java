/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.repository.authorization.capability;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.capabilities.Capability;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * @author Adolfo Pérez
 */
public interface AuthorizationCapability extends Capability {

	public void authorize(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, PortalException;

	public void authorize(
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws IOException, PortalException;

	public boolean hasCustomRedirectFlow(
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws IOException, PortalException;

}