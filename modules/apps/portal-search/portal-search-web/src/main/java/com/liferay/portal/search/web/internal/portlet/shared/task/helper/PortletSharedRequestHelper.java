/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.portlet.shared.task.helper;

import jakarta.portlet.RenderRequest;

/**
 * @author André de Oliveira
 */
public interface PortletSharedRequestHelper {

	public <T> T getAttribute(String name, RenderRequest renderRequest);

	public String getCompleteURL(RenderRequest renderRequest);

	public String getParameter(String name, RenderRequest renderRequest);

	public String[] getParameterValues(
		String name, RenderRequest renderRequest);

	public void setAttribute(
		String name, Object attributeValue, RenderRequest renderRequest);

}