/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.portletconfiguration.util;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.filter.RenderRequestWrapper;

/**
 * @author Raymond Augé
 */
public class ConfigurationRenderRequest
	extends RenderRequestWrapper implements ConfigurationPortletRequest {

	public ConfigurationRenderRequest(
		RenderRequest renderRequest, PortletPreferences portletPreferences) {

		super(renderRequest);

		_portletPreferences = portletPreferences;
	}

	@Override
	public PortletPreferences getPreferences() {
		return _portletPreferences;
	}

	private final PortletPreferences _portletPreferences;

}