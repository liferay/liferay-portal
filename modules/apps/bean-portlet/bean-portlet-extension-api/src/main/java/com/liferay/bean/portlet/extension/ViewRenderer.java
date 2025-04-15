/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.extension;

import jakarta.portlet.MimeResponse;
import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Neil Griffin
 */
@ProviderType
public interface ViewRenderer {

	public static final String REDIRECT_PREFIX = "redirect:";

	public static final String REDIRECTED_VIEW = "redirectedView";

	public static final String VIEW_NAME = "viewName";

	public void render(
			MimeResponse mimeResponse, PortletConfig portletConfig,
			PortletRequest portletRequest)
		throws PortletException;

}