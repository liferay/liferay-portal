/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.kernel.service;

import com.liferay.exportimport.kernel.lar.MissingReferences;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Transactional;

import jakarta.portlet.PortletRequest;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for Staging. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see StagingLocalServiceUtil
 * @generated
 */
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface StagingLocalService extends BaseLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portlet.exportimport.service.impl.StagingLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the staging local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link StagingLocalServiceUtil} if injection and service tracking are not available.
	 */
	public void checkDefaultLayoutSetBranches(
			long userId, Group liveGroup, boolean branchingPublic,
			boolean branchingPrivate, boolean remote,
			ServiceContext serviceContext)
		throws PortalException;

	public void cleanUpStagingRequest(long stagingRequestId)
		throws PortalException;

	public long createStagingRequest(long userId, long groupId, String checksum)
		throws PortalException;

	public void disableStaging(Group liveGroup, ServiceContext serviceContext)
		throws PortalException;

	public void disableStaging(
			PortletRequest portletRequest, Group liveGroup,
			ServiceContext serviceContext)
		throws PortalException;

	public void enableLocalStaging(
			long userId, Group liveGroup, boolean branchingPublic,
			boolean branchingPrivate, ServiceContext serviceContext)
		throws PortalException;

	public void enableRemoteStaging(
			long userId, Group stagingGroup, boolean branchingPublic,
			boolean branchingPrivate, String remoteAddress, int remotePort,
			String remotePathContext, boolean secureConnection,
			long remoteGroupId, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	public MissingReferences publishStagingRequest(
			long userId, long stagingRequestId,
			ExportImportConfiguration exportImportConfiguration)
		throws PortalException;

	public void updateStagingRequest(
			long userId, long stagingRequestId, String fileName, byte[] bytes)
		throws PortalException;

}