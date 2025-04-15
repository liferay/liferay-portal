/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.kernel.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link StagingLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see StagingLocalService
 * @generated
 */
public class StagingLocalServiceWrapper
	implements ServiceWrapper<StagingLocalService>, StagingLocalService {

	public StagingLocalServiceWrapper() {
		this(null);
	}

	public StagingLocalServiceWrapper(StagingLocalService stagingLocalService) {
		_stagingLocalService = stagingLocalService;
	}

	@Override
	public void checkDefaultLayoutSetBranches(
			long userId, com.liferay.portal.kernel.model.Group liveGroup,
			boolean branchingPublic, boolean branchingPrivate, boolean remote,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_stagingLocalService.checkDefaultLayoutSetBranches(
			userId, liveGroup, branchingPublic, branchingPrivate, remote,
			serviceContext);
	}

	@Override
	public void cleanUpStagingRequest(long stagingRequestId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_stagingLocalService.cleanUpStagingRequest(stagingRequestId);
	}

	@Override
	public long createStagingRequest(long userId, long groupId, String checksum)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _stagingLocalService.createStagingRequest(
			userId, groupId, checksum);
	}

	@Override
	public void disableStaging(
			com.liferay.portal.kernel.model.Group liveGroup,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_stagingLocalService.disableStaging(liveGroup, serviceContext);
	}

	@Override
	public void disableStaging(
			jakarta.portlet.PortletRequest portletRequest,
			com.liferay.portal.kernel.model.Group liveGroup,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_stagingLocalService.disableStaging(
			portletRequest, liveGroup, serviceContext);
	}

	@Override
	public void enableLocalStaging(
			long userId, com.liferay.portal.kernel.model.Group liveGroup,
			boolean branchingPublic, boolean branchingPrivate,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_stagingLocalService.enableLocalStaging(
			userId, liveGroup, branchingPublic, branchingPrivate,
			serviceContext);
	}

	@Override
	public void enableRemoteStaging(
			long userId, com.liferay.portal.kernel.model.Group stagingGroup,
			boolean branchingPublic, boolean branchingPrivate,
			String remoteAddress, int remotePort, String remotePathContext,
			boolean secureConnection, long remoteGroupId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_stagingLocalService.enableRemoteStaging(
			userId, stagingGroup, branchingPublic, branchingPrivate,
			remoteAddress, remotePort, remotePathContext, secureConnection,
			remoteGroupId, serviceContext);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _stagingLocalService.getOSGiServiceIdentifier();
	}

	@Override
	public com.liferay.exportimport.kernel.lar.MissingReferences
			publishStagingRequest(
				long userId, long stagingRequestId,
				com.liferay.exportimport.kernel.model.ExportImportConfiguration
					exportImportConfiguration)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _stagingLocalService.publishStagingRequest(
			userId, stagingRequestId, exportImportConfiguration);
	}

	@Override
	public void updateStagingRequest(
			long userId, long stagingRequestId, String fileName, byte[] bytes)
		throws com.liferay.portal.kernel.exception.PortalException {

		_stagingLocalService.updateStagingRequest(
			userId, stagingRequestId, fileName, bytes);
	}

	@Override
	public StagingLocalService getWrappedService() {
		return _stagingLocalService;
	}

	@Override
	public void setWrappedService(StagingLocalService stagingLocalService) {
		_stagingLocalService = stagingLocalService;
	}

	private StagingLocalService _stagingLocalService;

}