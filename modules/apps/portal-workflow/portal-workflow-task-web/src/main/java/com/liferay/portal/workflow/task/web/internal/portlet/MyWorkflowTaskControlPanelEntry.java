/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.task.web.internal.portlet;

import com.liferay.portal.kernel.portlet.ControlPanelEntry;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.workflow.MyWorkflowTasksControlPanelEntry;

import org.osgi.service.component.annotations.Component;

/**
 * @author Leonardo Barros
 */
@Component(
	property = "jakarta.portlet.name=" + PortletKeys.MY_WORKFLOW_TASK,
	service = ControlPanelEntry.class
)
public class MyWorkflowTaskControlPanelEntry
	extends MyWorkflowTasksControlPanelEntry {
}