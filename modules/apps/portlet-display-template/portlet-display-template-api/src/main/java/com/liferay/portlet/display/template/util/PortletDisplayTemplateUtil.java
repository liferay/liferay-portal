/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.display.template.util;

import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.template.TemplateHandler;
import com.liferay.portlet.display.template.PortletDisplayTemplate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;

/**
 * @author Lily Chi
 */
public class PortletDisplayTemplateUtil {

	public static long getDDMTemplateGroupId(long groupId) {
		PortletDisplayTemplate portletDisplayTemplate =
			_portletDisplayTemplateSnapshot.get();

		return portletDisplayTemplate.getDDMTemplateGroupId(groupId);
	}

	public static String getDisplayStyle(String ddmTemplateKey) {
		PortletDisplayTemplate portletDisplayTemplate =
			_portletDisplayTemplateSnapshot.get();

		return portletDisplayTemplate.getDisplayStyle(ddmTemplateKey);
	}

	public static PortletDisplayTemplate getPortletDisplayTemplate() {
		return _portletDisplayTemplateSnapshot.get();
	}

	public static DDMTemplate getPortletDisplayTemplateDDMTemplate(
		long groupId, long classNameId, String displayStyle,
		boolean useDefault) {

		PortletDisplayTemplate portletDisplayTemplate =
			_portletDisplayTemplateSnapshot.get();

		return portletDisplayTemplate.getPortletDisplayTemplateDDMTemplate(
			groupId, classNameId, displayStyle, useDefault);
	}

	public static List<TemplateHandler> getPortletDisplayTemplateHandlers() {
		PortletDisplayTemplate portletDisplayTemplate =
			_portletDisplayTemplateSnapshot.get();

		return portletDisplayTemplate.getPortletDisplayTemplateHandlers();
	}

	public static String renderDDMTemplate(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, long ddmTemplateId,
			List<?> entries, Map<String, Object> contextObjects)
		throws Exception {

		PortletDisplayTemplate portletDisplayTemplate =
			_portletDisplayTemplateSnapshot.get();

		return portletDisplayTemplate.renderDDMTemplate(
			httpServletRequest, httpServletResponse, ddmTemplateId, entries,
			contextObjects);
	}

	private static final Snapshot<PortletDisplayTemplate>
		_portletDisplayTemplateSnapshot = new Snapshot<>(
			PortletDisplayTemplateUtil.class, PortletDisplayTemplate.class);

}