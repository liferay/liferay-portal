/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.web.internal.display.context;

import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.workflow.constants.WorkflowPortletKeys;

import jakarta.portlet.PortletMode;
import jakarta.portlet.WindowState;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Feliphe Marinho
 * @author João Victor Alves
 */
public class TaskDefinitionDisplayContext {

	public TaskDefinitionDisplayContext(
		HttpServletRequest httpServletRequest, Portal portal) {

		_httpServletRequest = httpServletRequest;
		_portal = portal;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public String getAPIURL() {
		return "/o/ai-hub/v1.0/task-definitions";
	}

	public CreationMenu getCreationMenu() throws Exception {
		String namespace = _portal.getPortletNamespace(
			WorkflowPortletKeys.KALEO_DESIGNER);

		return CreationMenuBuilder.addDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					HttpComponentsUtil.addParameter(
						_getBaseURL(_themeDisplay.getCompany(), namespace),
						namespace + "scope", "ai"));
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "new-workflow"));
			}
		).build();
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems()
		throws Exception {

		String namespace = _portal.getPortletNamespace(
			WorkflowPortletKeys.KALEO_DESIGNER);

		return List.of(
			new FDSActionDropdownItem(
				HttpComponentsUtil.addParameter(
					_getBaseURL(_themeDisplay.getCompany(), namespace),
					namespace + "name", "{name}"),
				"view", "view", LanguageUtil.get(_httpServletRequest, "view"),
				"get", null, null),
			new FDSActionDropdownItem(
				getAPIURL() + "/{id}", "trash", "delete",
				LanguageUtil.get(_httpServletRequest, "delete"), "delete",
				"delete", "async"));
	}

	private String _getBaseURL(Company company, String namespace)
		throws Exception {

		String url = StringBundler.concat(
			company.getPortalURL(GroupConstants.DEFAULT_PARENT_GROUP_ID),
			PropsValues.LAYOUT_FRIENDLY_URL_PRIVATE_GROUP_SERVLET_MAPPING,
			GroupConstants.CONTROL_PANEL_FRIENDLY_URL,
			PropsValues.CONTROL_PANEL_LAYOUT_FRIENDLY_URL);

		return HttpComponentsUtil.addParameters(
			url, "p_p_id", WorkflowPortletKeys.KALEO_DESIGNER, "p_p_lifecycle",
			"0", "p_p_state", WindowState.MAXIMIZED.toString(), "p_p_mode",
			PortletMode.VIEW.toString(), namespace + "mvcPath",
			"/designer/edit_workflow_definition.jsp", namespace + "redirect",
			_portal.getPortalURL(_httpServletRequest) +
				_portal.getCurrentURL(_httpServletRequest),
			namespace + "clearSessionMessage", true);
	}

	private final HttpServletRequest _httpServletRequest;
	private final Portal _portal;
	private final ThemeDisplay _themeDisplay;

}