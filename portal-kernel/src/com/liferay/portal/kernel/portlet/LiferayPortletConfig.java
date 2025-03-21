/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import com.liferay.portal.kernel.model.Portlet;

import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletContext;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Brian Wing Shun Chan
 */
@ProviderType
public interface LiferayPortletConfig extends PortletConfig {

	public static final String RUNTIME_OPTION_ESCAPE_XML =
		"jakarta.portlet.escapeXml";

	public static final String RUNTIME_OPTION_PORTAL_CONTEXT =
		"com.liferay.portal.portalContext";

	public Portlet getPortlet();

	@Override
	public PortletContext getPortletContext();

	public String getPortletId();

	public boolean isCopyRequestParameters();

	public boolean isWARFile();

}