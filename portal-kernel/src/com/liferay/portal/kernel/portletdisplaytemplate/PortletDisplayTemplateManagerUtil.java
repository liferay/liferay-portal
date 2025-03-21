/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portletdisplaytemplate;

import com.liferay.portal.kernel.module.service.Snapshot;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;

/**
 * @author Leonardo Barros
 */
public class PortletDisplayTemplateManagerUtil {

	public static String renderDDMTemplate(
			long classNameId, Map<String, Object> contextObjects,
			String ddmTemplateKey, List<?> entries, long groupId,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, boolean useDefault)
		throws Exception {

		PortletDisplayTemplateManager portletDisplayTemplateManager =
			_portletDisplayTemplateManagerSnapshot.get();

		return portletDisplayTemplateManager.renderDDMTemplate(
			classNameId, contextObjects, ddmTemplateKey, entries, groupId,
			httpServletRequest, httpServletResponse, useDefault);
	}

	private static final Snapshot<PortletDisplayTemplateManager>
		_portletDisplayTemplateManagerSnapshot = new Snapshot<>(
			PortletDisplayTemplateManagerUtil.class,
			PortletDisplayTemplateManager.class);

}