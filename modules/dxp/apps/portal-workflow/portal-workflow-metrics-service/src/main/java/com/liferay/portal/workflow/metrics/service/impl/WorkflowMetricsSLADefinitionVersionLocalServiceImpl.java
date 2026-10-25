/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.service.impl;

import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.workflow.metrics.exception.NoSuchSLADefinitionVersionException;
import com.liferay.portal.workflow.metrics.model.WorkflowMetricsSLADefinitionVersion;
import com.liferay.portal.workflow.metrics.service.base.WorkflowMetricsSLADefinitionVersionLocalServiceBaseImpl;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.portal.workflow.metrics.model.WorkflowMetricsSLADefinitionVersion",
	service = AopService.class
)
public class WorkflowMetricsSLADefinitionVersionLocalServiceImpl
	extends WorkflowMetricsSLADefinitionVersionLocalServiceBaseImpl {

	@Override
	public WorkflowMetricsSLADefinitionVersion
			getWorkflowMetricsSLADefinitionVersion(
				long workflowMetricsSLADefinitionId, String version)
		throws NoSuchSLADefinitionVersionException {

		return workflowMetricsSLADefinitionVersionPersistence.findByV_WMSLAD(
			version, workflowMetricsSLADefinitionId);
	}

	@Override
	public List<WorkflowMetricsSLADefinitionVersion>
		getWorkflowMetricsSLADefinitionVersions(
			long workflowMetricsSLADefinitionId) {

		return workflowMetricsSLADefinitionVersionPersistence.
			findByWorkflowMetricsSLADefinitionId(
				workflowMetricsSLADefinitionId);
	}

	@Override
	public List<WorkflowMetricsSLADefinitionVersion>
		getWorkflowMetricsSLADefinitionVersions(
			long workflowMetricsSLADefinitionId,
			OrderByComparator<WorkflowMetricsSLADefinitionVersion>
				orderByComparator) {

		return workflowMetricsSLADefinitionVersionPersistence.
			findByWorkflowMetricsSLADefinitionId(
				workflowMetricsSLADefinitionId, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, orderByComparator);
	}

}