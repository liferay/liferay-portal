/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portletdisplaytemplate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Leonardo Barros
 */
@ProviderType
public interface PortletDisplayTemplateManager {

	public static final String DISPLAY_STYLE_PREFIX = "ddmTemplate_";

	public static final String ENTRIES = "entries";

	public String renderDDMTemplate(
			long classNameId, Map<String, Object> contextObjects,
			String ddmTemplateKey, List<?> entries, long groupId,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, boolean useDefault)
		throws Exception;

}