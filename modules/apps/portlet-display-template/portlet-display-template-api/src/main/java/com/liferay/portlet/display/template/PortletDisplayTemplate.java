/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.display.template;

import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.portal.kernel.template.TemplateHandler;
import com.liferay.portal.kernel.template.TemplateVariableGroup;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;

/**
 * @author Eduardo García
 */
public interface PortletDisplayTemplate {

	public static final String DISPLAY_STYLE_PREFIX = "ddmTemplate_";

	public DDMTemplate fetchDDMTemplate(long groupId, String displayStyle);

	public long getDDMTemplateGroupId(long groupId);

	public String getDDMTemplateKey(String displayStyle);

	public DDMTemplate getDefaultPortletDisplayTemplateDDMTemplate(
		long groupId, long classNameId);

	public String getDisplayStyle(String ddmTemplateKey);

	public DDMTemplate getPortletDisplayTemplateDDMTemplate(
		long groupId, long classNameId, String displayStyle);

	public DDMTemplate getPortletDisplayTemplateDDMTemplate(
		long groupId, long classNameId, String displayStyle,
		boolean useDefault);

	public List<TemplateHandler> getPortletDisplayTemplateHandlers();

	public Map<String, TemplateVariableGroup> getTemplateVariableGroups(
		String language);

	public String renderDDMTemplate(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, DDMTemplate ddmTemplate,
			List<?> entries)
		throws Exception;

	public String renderDDMTemplate(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, DDMTemplate ddmTemplate,
			List<?> entries, Map<String, Object> contextObjects)
		throws Exception;

	public String renderDDMTemplate(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, long ddmTemplateId,
			List<?> entries)
		throws Exception;

	public String renderDDMTemplate(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, long ddmTemplateId,
			List<?> entries, Map<String, Object> contextObjects)
		throws Exception;

}