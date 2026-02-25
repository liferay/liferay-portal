/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.web.internal.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.workflow.constants.WorkflowPortletKeys;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Praxedes
 */
@Component(
	property = {
		"panel.app.order:Integer=250",
		"panel.category.key=" + PanelCategoryKeys.CONTROL_PANEL_WORKFLOW
	},
	service = PanelApp.class
)
public class ControlPanelWorkflowInstancePanelApp extends BasePanelApp {

	@Override
	public String getIcon() {
		return "submission";
	}

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return WorkflowPortletKeys.CONTROL_PANEL_WORKFLOW_INSTANCE;
	}

	@Reference(
		target = "(jakarta.portlet.name=" + WorkflowPortletKeys.CONTROL_PANEL_WORKFLOW_INSTANCE + ")"
	)
	private Portlet _portlet;

}