/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.workflow.internal.resource.v1_0;

import com.liferay.headless.admin.workflow.dto.v1_0.Assignee;
import com.liferay.headless.admin.workflow.internal.dto.v1_0.util.AssigneeUtil;
import com.liferay.headless.admin.workflow.resource.v1_0.AssigneeResource;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.workflow.WorkflowTaskManager;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/assignee.properties",
	scope = ServiceScope.PROTOTYPE, service = AssigneeResource.class
)
public class AssigneeResourceImpl extends BaseAssigneeResourceImpl {

	@Override
	public Page<Assignee> getWorkflowTaskAssignableUsersPage(
			Long workflowTaskId, Pagination pagination)
		throws Exception {

		List<User> assignableUsers = _workflowTaskManager.getAssignableUsers(
			workflowTaskId);

		return Page.of(
			transform(
				ListUtil.subList(
					assignableUsers, pagination.getStartPosition(),
					pagination.getEndPosition()),
				AssigneeUtil::toAssignee),
			pagination, assignableUsers.size());
	}

	@Reference
	private WorkflowTaskManager _workflowTaskManager;

}