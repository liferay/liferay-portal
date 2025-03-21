/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.web.internal.portlet.configuration.icon;

import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.configuration.icon.BasePortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.configuration.icon.PortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.workflow.constants.WorkflowPortletKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Defines the icon triggering the deactivation of a workflow definition.
 *
 * @author Jeyvison Nascimento
 */
@Component(
	property = {
		"jakarta.portlet.name=" + WorkflowPortletKeys.CONTROL_PANEL_WORKFLOW,
		"path=/definition/edit_workflow_definition.jsp"
	},
	service = PortletConfigurationIcon.class
)
public class UnpublishDefinitionPortletConfigurationIcon
	extends BasePortletConfigurationIcon {

	@Override
	public String getMessage(PortletRequest portletRequest) {
		return _language.get(getLocale(portletRequest), "unpublish");
	}

	/**
	 * Creates and returns an action URL, setting the workflow definition name
	 * and version as URL parameters.
	 *
	 * @param portletRequest the portlet request from which to get the workflow
	 *        definition name and version
	 * @param portletResponse the portlet response
	 */
	@Override
	public String getURL(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				portletRequest, WorkflowPortletKeys.CONTROL_PANEL_WORKFLOW,
				PortletRequest.ACTION_PHASE)
		).setActionName(
			"/portal_workflow/deactivate_workflow_definition"
		).setMVCPath(
			portletRequest.getParameter("mvcPath")
		).setParameter(
			"name", portletRequest.getParameter("name")
		).setParameter(
			"version", portletRequest.getParameter("version")
		).buildString();
	}

	@Override
	public boolean isShow(PortletRequest portletRequest) {
		WorkflowDefinition workflowDefinition =
			(WorkflowDefinition)portletRequest.getAttribute(
				WebKeys.WORKFLOW_DEFINITION);

		if ((workflowDefinition != null) && workflowDefinition.isActive()) {
			return true;
		}

		return false;
	}

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}