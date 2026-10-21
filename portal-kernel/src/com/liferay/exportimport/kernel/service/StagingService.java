/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.kernel.service;

import com.liferay.exportimport.kernel.lar.MissingReferences;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.service.BaseService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;

import java.io.Serializable;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the remote service interface for Staging. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see StagingServiceUtil
 * @generated
 */
@AccessControlled
@JSONWebService
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface StagingService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portlet.exportimport.service.impl.StagingServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the staging remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link StagingServiceUtil} if injection and service tracking are not available.
	 */
	public void cleanUpStagingRequest(long stagingRequestId)
		throws PortalException;

	public long createStagingRequest(long groupId, String checksum)
		throws PortalException;

	public void enableLocalStaging(
			long groupId, boolean branchingPublic, boolean branchingPrivate,
			ServiceContext serviceContext)
		throws PortalException;

	public void enableRemoteStaging(
			long groupId, boolean branchingPublic, boolean branchingPrivate,
			String remoteAddress, int remotePort, String remotePathContext,
			boolean secureConnection, long remoteGroupId,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasRemoteLayout(
			String uuid, long groupId, boolean privateLayout)
		throws PortalException;

	public void propagateExportImportLifecycleEvent(
			int code, int processFlag, String processId,
			List<Serializable> arguments)
		throws PortalException;

	public MissingReferences publishStagingRequest(
			long stagingRequestId,
			ExportImportConfiguration exportImportConfiguration)
		throws PortalException;

	public void updateStagingRequest(
			long stagingRequestId, String fileName, byte[] bytes)
		throws PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1912302362