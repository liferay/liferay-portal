/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;

import java.util.List;

/**
 * Provides the remote service utility for WorkflowDefinitionLink. This utility wraps
 * <code>com.liferay.portal.service.impl.WorkflowDefinitionLinkServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see WorkflowDefinitionLinkService
 * @generated
 */
public class WorkflowDefinitionLinkServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.service.impl.WorkflowDefinitionLinkServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static WorkflowDefinitionLink addWorkflowDefinitionLink(
			long userId, long companyId, long groupId, String className,
			long classPK, long typePK, String workflowDefinitionName,
			int workflowDefinitionVersion)
		throws PortalException {

		return getService().addWorkflowDefinitionLink(
			userId, companyId, groupId, className, classPK, typePK,
			workflowDefinitionName, workflowDefinitionVersion);
	}

	public static WorkflowDefinitionLink
			fetchWorkflowDefinitionLinkByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().fetchWorkflowDefinitionLinkByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static List<WorkflowDefinitionLink> getWorkflowDefinitionLinks(
			long companyId, String workflowDefinitionName,
			int workflowDefinitionVersion)
		throws PortalException {

		return getService().getWorkflowDefinitionLinks(
			companyId, workflowDefinitionName, workflowDefinitionVersion);
	}

	public static WorkflowDefinitionLink updateWorkflowDefinitionLink(
			String externalReferenceCode, long userId, long companyId,
			long groupId, String className, long classPK, long typePK,
			String workflowDefinitionName, int workflowDefinitionVersion)
		throws PortalException {

		return getService().updateWorkflowDefinitionLink(
			externalReferenceCode, userId, companyId, groupId, className,
			classPK, typePK, workflowDefinitionName, workflowDefinitionVersion);
	}

	public static WorkflowDefinitionLinkService getService() {
		return _service;
	}

	public static void setService(WorkflowDefinitionLinkService service) {
		_service = service;
	}

	private static volatile WorkflowDefinitionLinkService _service;

}