/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.portal.kernel.model.WorkflowDefinitionLink;

/**
 * Provides a wrapper for {@link WorkflowDefinitionLinkService}.
 *
 * @author Brian Wing Shun Chan
 * @see WorkflowDefinitionLinkService
 * @generated
 */
public class WorkflowDefinitionLinkServiceWrapper
	implements ServiceWrapper<WorkflowDefinitionLinkService>,
			   WorkflowDefinitionLinkService {

	public WorkflowDefinitionLinkServiceWrapper() {
		this(null);
	}

	public WorkflowDefinitionLinkServiceWrapper(
		WorkflowDefinitionLinkService workflowDefinitionLinkService) {

		_workflowDefinitionLinkService = workflowDefinitionLinkService;
	}

	@Override
	public WorkflowDefinitionLink addWorkflowDefinitionLink(
			long userId, long companyId, long groupId, String className,
			long classPK, long typePK, String workflowDefinitionName,
			int workflowDefinitionVersion)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _workflowDefinitionLinkService.addWorkflowDefinitionLink(
			userId, companyId, groupId, className, classPK, typePK,
			workflowDefinitionName, workflowDefinitionVersion);
	}

	@Override
	public WorkflowDefinitionLink
			fetchWorkflowDefinitionLinkByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _workflowDefinitionLinkService.
			fetchWorkflowDefinitionLinkByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _workflowDefinitionLinkService.getOSGiServiceIdentifier();
	}

	@Override
	public java.util.List<WorkflowDefinitionLink> getWorkflowDefinitionLinks(
			long companyId, String workflowDefinitionName,
			int workflowDefinitionVersion)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _workflowDefinitionLinkService.getWorkflowDefinitionLinks(
			companyId, workflowDefinitionName, workflowDefinitionVersion);
	}

	@Override
	public WorkflowDefinitionLink updateWorkflowDefinitionLink(
			String externalReferenceCode, long userId, long companyId,
			long groupId, String className, long classPK, long typePK,
			String workflowDefinitionName, int workflowDefinitionVersion)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _workflowDefinitionLinkService.updateWorkflowDefinitionLink(
			externalReferenceCode, userId, companyId, groupId, className,
			classPK, typePK, workflowDefinitionName, workflowDefinitionVersion);
	}

	@Override
	public WorkflowDefinitionLinkService getWrappedService() {
		return _workflowDefinitionLinkService;
	}

	@Override
	public void setWrappedService(
		WorkflowDefinitionLinkService workflowDefinitionLinkService) {

		_workflowDefinitionLinkService = workflowDefinitionLinkService;
	}

	private WorkflowDefinitionLinkService _workflowDefinitionLinkService;

}