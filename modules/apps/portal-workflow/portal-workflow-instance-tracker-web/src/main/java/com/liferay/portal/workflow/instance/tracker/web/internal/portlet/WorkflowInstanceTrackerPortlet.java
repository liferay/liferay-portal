/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.instance.tracker.web.internal.portlet;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.workflow.instance.tracker.web.internal.constants.WorkflowInstanceTrackerPortletKeys;

import jakarta.portlet.Portlet;

import org.osgi.service.component.annotations.Component;

/**
 * @author Feliphe Marinho
 */
@Component(
	property = {
		"com.liferay.portlet.display-category=category.hidden",
		"jakarta.portlet.display-name=Workflow Instance Tracker",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + WorkflowInstanceTrackerPortletKeys.WORKFLOW_INSTANCE_TRACKER,
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class WorkflowInstanceTrackerPortlet extends MVCPortlet {
}