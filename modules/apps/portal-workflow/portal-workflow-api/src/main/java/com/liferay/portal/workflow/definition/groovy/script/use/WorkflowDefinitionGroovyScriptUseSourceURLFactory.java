/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.definition.groovy.script.use;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.util.PropsValues;
import com.liferay.portal.workflow.constants.WorkflowPortletKeys;
import com.liferay.portal.workflow.constants.WorkflowWebKeys;
import com.liferay.portal.workflow.portlet.tab.WorkflowPortletTabRegistry;

import jakarta.portlet.PortletMode;
import jakarta.portlet.WindowState;

/**
 * @author Feliphe Marinho
 */
public class WorkflowDefinitionGroovyScriptUseSourceURLFactory {

	public static String create(
			Company company, Portal portal, String workflowDefinitionName,
			int workflowDefinitionVersion,
			WorkflowPortletTabRegistry workflowPortletTabRegistry)
		throws Exception {

		String controlPanelWorkflowPortletBaseURL = _getBaseURL(
			company, WorkflowPortletKeys.CONTROL_PANEL_WORKFLOW);

		if (workflowPortletTabRegistry.contains(
				WorkflowWebKeys.WORKFLOW_TAB_DEFINITION)) {

			String url = _getBaseURL(
				company, WorkflowPortletKeys.KALEO_DESIGNER);

			String namespace = portal.getPortletNamespace(
				WorkflowPortletKeys.KALEO_DESIGNER);

			url = HttpComponentsUtil.addParameter(
				url, namespace + "mvcPath",
				"/designer/edit_workflow_definition.jsp");
			url = HttpComponentsUtil.addParameter(
				url, namespace + "redirect",
				controlPanelWorkflowPortletBaseURL);
			url = HttpComponentsUtil.addParameter(
				url, namespace + "clearSessionMessage", true);
			url = HttpComponentsUtil.addParameter(
				url, namespace + "draftVersion",
				workflowDefinitionVersion + ".0");
			url = HttpComponentsUtil.addParameter(
				url, namespace + "name", workflowDefinitionName);

			return url;
		}

		String namespace = portal.getPortletNamespace(
			WorkflowPortletKeys.CONTROL_PANEL_WORKFLOW);

		String url = HttpComponentsUtil.addParameter(
			controlPanelWorkflowPortletBaseURL, namespace + "mvcPath",
			"/definition/edit_workflow_definition.jsp");

		return HttpComponentsUtil.addParameter(
			url, namespace + "redirect",
			HttpComponentsUtil.addParameter(
				controlPanelWorkflowPortletBaseURL, namespace + "mvcPath",
				"/view.jsp"));
	}

	private static String _getBaseURL(Company company, String portletId)
		throws Exception {

		String url = StringBundler.concat(
			company.getPortalURL(GroupConstants.DEFAULT_PARENT_GROUP_ID),
			PropsValues.LAYOUT_FRIENDLY_URL_PRIVATE_GROUP_SERVLET_MAPPING,
			GroupConstants.CONTROL_PANEL_FRIENDLY_URL,
			PropsValues.CONTROL_PANEL_LAYOUT_FRIENDLY_URL);

		url = HttpComponentsUtil.addParameter(url, "p_p_id", portletId);
		url = HttpComponentsUtil.addParameter(url, "p_p_lifecycle", "0");
		url = HttpComponentsUtil.addParameter(
			url, "p_p_state", WindowState.MAXIMIZED.toString());
		url = HttpComponentsUtil.addParameter(
			url, "p_p_mode", PortletMode.VIEW.toString());

		return url;
	}

}