/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.manager;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.kernel.workflow.WorkflowException;

import java.util.List;

/**
 * @author Micha Kiener
 * @author Shuyang Zhou
 * @author Brian Wing Shun Chan
 * @author Marcellus Tavares
 * @author Eduardo Lundgren
 */
public interface WorkflowDefinitionManager {

	public default WorkflowDefinition deployWorkflowDefinition(
			byte[] bytes, long companyId, String externalReferenceCode,
			long groupId, String name, String scope, boolean system,
			String title, long userId)
		throws WorkflowException {

		throw new UnsupportedOperationException();
	}

	public default WorkflowDefinition deployWorkflowDefinition(
			byte[] bytes, long companyId, String externalReferenceCode,
			String name, String title, long userId)
		throws WorkflowException {

		throw new UnsupportedOperationException();
	}

	public List<WorkflowDefinition> getActiveWorkflowDefinitions(
			int end, int start)
		throws WorkflowException;

	public List<WorkflowDefinition> getActiveWorkflowDefinitions(
			long companyId, int end, String name,
			OrderByComparator<WorkflowDefinition> orderByComparator, int start)
		throws WorkflowException;

	public default int getActiveWorkflowDefinitionsCount(long companyId)
		throws WorkflowException {

		throw new UnsupportedOperationException();
	}

	public default WorkflowDefinition getLatestWorkflowDefinition(
			long companyId, String name)
		throws WorkflowException {

		throw new UnsupportedOperationException();
	}

	public default List<WorkflowDefinition> getLatestWorkflowDefinitions(
			Boolean active, long companyId, int end,
			OrderByComparator<WorkflowDefinition> orderByComparator,
			String scope, int start, long userId)
		throws WorkflowException {

		throw new UnsupportedOperationException();
	}

	public default int getLatestWorkflowDefinitionsCount(
			Boolean active, long companyId)
		throws WorkflowException {

		throw new UnsupportedOperationException();
	}

	public default WorkflowDefinition getWorkflowDefinition(
			long workflowDefinitionId)
		throws PortalException {

		throw new UnsupportedOperationException();
	}

	public default WorkflowDefinition getWorkflowDefinition(
			long companyId, String externalReferenceCode)
		throws PortalException {

		throw new UnsupportedOperationException();
	}

	public WorkflowDefinition getWorkflowDefinition(
			long companyId, String name, int version)
		throws PortalException;

	public default int getWorkflowDefinitionsCount(long companyId, String name)
		throws WorkflowException {

		throw new UnsupportedOperationException();
	}

	public default List<WorkflowDefinition> liberalGetActiveWorkflowDefinitions(
			long companyId, int end,
			OrderByComparator<WorkflowDefinition> orderByComparator, int start)
		throws WorkflowException {

		throw new UnsupportedOperationException();
	}

	public default WorkflowDefinition liberalGetLatestWorkflowDefinition(
			long companyId, String name)
		throws WorkflowException {

		throw new UnsupportedOperationException();
	}

	public default List<WorkflowDefinition> liberalGetLatestWorkflowDefinitions(
			long companyId, int end,
			OrderByComparator<WorkflowDefinition> orderByComparator,
			String scope, int start)
		throws WorkflowException {

		throw new UnsupportedOperationException();
	}

	public default WorkflowDefinition liberalGetWorkflowDefinition(
			long companyId, String name, int version)
		throws PortalException {

		throw new UnsupportedOperationException();
	}

	public default List<WorkflowDefinition> liberalGetWorkflowDefinitions(
			long companyId, int end, String name,
			OrderByComparator<WorkflowDefinition> orderByComparator, int start)
		throws WorkflowException {

		throw new UnsupportedOperationException();
	}

	public default WorkflowDefinition saveWorkflowDefinition(
			byte[] bytes, long companyId, String externalReferenceCode,
			long groupId, String name, String scope, boolean system,
			String title, long userId)
		throws WorkflowException {

		throw new UnsupportedOperationException();
	}

	public default WorkflowDefinition saveWorkflowDefinition(
			byte[] bytes, long companyId, String externalReferenceCode,
			String name, String title, long userId)
		throws WorkflowException {

		throw new UnsupportedOperationException();
	}

	public void undeployWorkflowDefinition(
			long companyId, String name, long userId, int version)
		throws WorkflowException;

	public WorkflowDefinition updateActive(
			boolean active, long companyId, String name, long userId,
			int version)
		throws WorkflowException;

	public void validateWorkflowDefinition(byte[] bytes)
		throws WorkflowException;

}