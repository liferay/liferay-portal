/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.workflow.resource.v1_0.test.util;

import com.liferay.headless.admin.workflow.client.dto.v1_0.ObjectReviewed;
import com.liferay.headless.admin.workflow.client.dto.v1_0.WorkflowDefinition;
import com.liferay.headless.admin.workflow.client.dto.v1_0.WorkflowInstance;
import com.liferay.headless.admin.workflow.client.dto.v1_0.WorkflowInstanceSubmit;
import com.liferay.headless.admin.workflow.client.resource.v1_0.WorkflowInstanceResource;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.io.Serializable;

/**
 * @author Rafael Praxedes
 */
public class WorkflowInstanceTestUtil {

	public static WorkflowInstance addWorkflowInstance(
			long groupId, ObjectReviewed objectReviewed,
			WorkflowDefinition workflowDefinition)
		throws Exception {

		WorkflowInstanceResource.Builder builder =
			WorkflowInstanceResource.builder();

		WorkflowInstanceResource workflowInstanceResource =
			builder.authentication(
				"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD
			).locale(
				LocaleUtil.getDefault()
			).build();

		WorkflowInstance workflowInstance = new WorkflowInstance() {
			{
				completed = false;
				dateCreated = RandomTestUtil.nextDate();
				id = RandomTestUtil.randomLong();
				workflowDefinitionName = workflowDefinition.getName();
				workflowDefinitionVersion = workflowDefinition.getVersion();
			}
		};

		workflowInstance.setObjectReviewed(objectReviewed);

		return workflowInstanceResource.postWorkflowInstanceSubmit(
			new WorkflowInstanceSubmit() {
				{
					context = HashMapBuilder.<String, Serializable>put(
						WorkflowConstants.CONTEXT_ENTRY_CLASS_NAME,
						ObjectReviewed.class.getName()
					).put(
						WorkflowConstants.CONTEXT_ENTRY_CLASS_PK,
						String.valueOf(objectReviewed.getId())
					).put(
						WorkflowConstants.CONTEXT_ENTRY_TYPE,
						objectReviewed.getAssetType()
					).build();
					siteId = groupId;
					workflowDefinitionName =
						workflowInstance.getWorkflowDefinitionName();
					workflowDefinitionVersion =
						workflowInstance.getWorkflowDefinitionVersion();
				}
			});
	}

}