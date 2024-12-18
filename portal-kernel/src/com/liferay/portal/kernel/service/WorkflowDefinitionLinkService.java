/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the remote service interface for WorkflowDefinitionLink. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see WorkflowDefinitionLinkServiceUtil
 * @generated
 */
@AccessControlled
@CTAware
@JSONWebService
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface WorkflowDefinitionLinkService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portal.service.impl.WorkflowDefinitionLinkServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the workflow definition link remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link WorkflowDefinitionLinkServiceUtil} if injection and service tracking are not available.
	 */
	public WorkflowDefinitionLink addWorkflowDefinitionLink(
			long userId, long companyId, long groupId, String className,
			long classPK, long typePK, String workflowDefinitionName,
			int workflowDefinitionVersion)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WorkflowDefinitionLink
			fetchWorkflowDefinitionLinkByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<WorkflowDefinitionLink> getWorkflowDefinitionLinks(
			long companyId, String workflowDefinitionName,
			int workflowDefinitionVersion)
		throws PortalException;

	public WorkflowDefinitionLink updateWorkflowDefinitionLink(
			String externalReferenceCode, long userId, long companyId,
			long groupId, String className, long classPK, long typePK,
			String workflowDefinitionName, int workflowDefinitionVersion)
		throws PortalException;

}