/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.definition.deployment;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.workflow.kaleo.definition.Definition;

/**
 * @author Michael C. Han
 */
public interface WorkflowDeployer {

	public WorkflowDefinition deploy(
			String externalReferenceCode, String title, String name,
			String scope, boolean system, Definition definition,
			ServiceContext serviceContext)
		throws PortalException;

	public WorkflowDefinition save(
			String externalReferenceCode, String title, String name,
			String scope, boolean system, Definition definition,
			ServiceContext serviceContext)
		throws PortalException;

}