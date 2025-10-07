/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.workflow.resource.v1_0.test.util;

import com.liferay.headless.admin.workflow.client.dto.v1_0.WorkflowTask;
import com.liferay.headless.admin.workflow.client.pagination.Page;
import com.liferay.headless.admin.workflow.client.pagination.Pagination;
import com.liferay.headless.admin.workflow.client.resource.v1_0.WorkflowTaskResource;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;

import java.util.List;

/**
 * @author Rafael Praxedes
 */
public class WorkflowTaskTestUtil {

	public static WorkflowTask getWorkflowTask(long workflowInstanceId)
		throws Exception {

		List<WorkflowTask> workflowTasks = getWorkflowTasks(workflowInstanceId);

		return workflowTasks.get(workflowTasks.size() - 1);
	}

	public static List<WorkflowTask> getWorkflowTasks(long workflowInstanceId)
		throws Exception {

		WorkflowTaskResource.Builder workflowTaskResourceBuilder =
			WorkflowTaskResource.builder();

		WorkflowTaskResource workflowTaskResource =
			workflowTaskResourceBuilder.authentication(
				"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD
			).locale(
				LocaleUtil.getDefault()
			).build();

		Page<WorkflowTask> page =
			workflowTaskResource.getWorkflowInstanceWorkflowTasksPage(
				workflowInstanceId, false, Pagination.of(-1, -1));

		return (List<WorkflowTask>)page.getItems();
	}

}