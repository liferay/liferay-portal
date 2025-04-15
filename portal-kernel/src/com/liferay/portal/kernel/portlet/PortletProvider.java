/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
public interface PortletProvider {

	public String getPortletName();

	public PortletURL getPortletURL(HttpServletRequest httpServletRequest)
		throws PortalException;

	public PortletURL getPortletURL(
			HttpServletRequest httpServletRequest, Group group)
		throws PortalException;

	public Action[] getSupportedActions();

	public enum Action {

		ADD, BROWSE, EDIT, MANAGE, PREVIEW, VIEW

	}

}