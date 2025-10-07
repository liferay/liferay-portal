/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.workflow.resource.v1_0.test.util;

import com.liferay.headless.admin.workflow.client.dto.v1_0.WorkflowDefinition;
import com.liferay.headless.admin.workflow.client.resource.v1_0.WorkflowDefinitionResource;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;

/**
 * @author Rafael Praxedes
 */
public class WorkflowDefinitionTestUtil {

	public static WorkflowDefinition addWorkflowDefinition() throws Exception {
		return addWorkflowDefinition("workflow-definition.xml");
	}

	public static WorkflowDefinition addWorkflowDefinition(
			String workflowDefinitionFileName)
		throws Exception {

		WorkflowDefinitionResource.Builder builder =
			WorkflowDefinitionResource.builder();

		WorkflowDefinitionResource workflowDefinitionResource =
			builder.authentication(
				"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD
			).locale(
				LocaleUtil.getDefault()
			).build();

		String workflowDefinitionName = StringUtil.toLowerCase(
			RandomTestUtil.randomString());

		return workflowDefinitionResource.postWorkflowDefinitionDeploy(
			new WorkflowDefinition() {
				{
					active = true;
					content = WorkflowDefinitionTestUtil.getContent(
						RandomTestUtil.randomString(),
						workflowDefinitionFileName, workflowDefinitionName);
					dateCreated = RandomTestUtil.nextDate();
					dateModified = RandomTestUtil.nextDate();
					description = StringUtil.toLowerCase(
						RandomTestUtil.randomString());
					name = workflowDefinitionName;
					title = StringUtil.toLowerCase(
						RandomTestUtil.randomString());
					version = StringUtil.toLowerCase(
						RandomTestUtil.randomString());
				}
			});
	}

	public static String getContent(
		String workflowDefinitionDescription, String workflowDefinitionFileName,
		String workflowDefinitionName) {

		return StringUtil.replace(
			StringUtil.read(
				WorkflowDefinitionTestUtil.class,
				"dependencies/" + workflowDefinitionFileName),
			new String[] {
				"[$WORKFLOW-DEFINITION-DESCRIPTION$]",
				"[$WORKFLOW-DEFINITION-NAME$]"
			},
			new String[] {
				workflowDefinitionDescription, workflowDefinitionName
			});
	}

}