/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import jakarta.portlet.PortletSession;

import jakarta.servlet.http.HttpSession;

/**
 * @author Brian Wing Shun Chan
 */
public interface LiferayPortletSession extends PortletSession {

	public static final String LAYOUT_SEPARATOR = "_LAYOUT_";

	public static final String PORTLET_SCOPE_NAMESPACE = "jakarta.portlet.p.";

	public void setHttpSession(HttpSession httpSession);

}