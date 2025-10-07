/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link ObjectEntryVersionService}.
 *
 * @author Marco Leo
 * @see ObjectEntryVersionService
 * @generated
 */
public class ObjectEntryVersionServiceWrapper
	implements ObjectEntryVersionService,
			   ServiceWrapper<ObjectEntryVersionService> {

	public ObjectEntryVersionServiceWrapper() {
		this(null);
	}

	public ObjectEntryVersionServiceWrapper(
		ObjectEntryVersionService objectEntryVersionService) {

		_objectEntryVersionService = objectEntryVersionService;
	}

	@Override
	public com.liferay.object.model.ObjectEntryVersion deleteObjectEntryVersion(
			long objectEntryId, int version)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryVersionService.deleteObjectEntryVersion(
			objectEntryId, version);
	}

	@Override
	public com.liferay.object.model.ObjectEntryVersion expireObjectEntryVersion(
			com.liferay.object.model.ObjectEntry objectEntry,
			com.liferay.portal.kernel.service.ServiceContext serviceContext,
			int version)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryVersionService.expireObjectEntryVersion(
			objectEntry, serviceContext, version);
	}

	@Override
	public void expireObjectEntryVersions(
			com.liferay.object.model.ObjectEntry objectEntry,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws Exception {

		_objectEntryVersionService.expireObjectEntryVersions(
			objectEntry, serviceContext);
	}

	@Override
	public com.liferay.object.model.ObjectEntryVersion getObjectEntryVersion(
			long objectEntryId, int version)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryVersionService.getObjectEntryVersion(
			objectEntryId, version);
	}

	@Override
	public java.util.List<com.liferay.object.model.ObjectEntryVersion>
			getObjectEntryVersions(long objectEntryId, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryVersionService.getObjectEntryVersions(
			objectEntryId, start, end);
	}

	@Override
	public int getObjectEntryVersionsCount(long objectEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryVersionService.getObjectEntryVersionsCount(
			objectEntryId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _objectEntryVersionService.getOSGiServiceIdentifier();
	}

	@Override
	public ObjectEntryVersionService getWrappedService() {
		return _objectEntryVersionService;
	}

	@Override
	public void setWrappedService(
		ObjectEntryVersionService objectEntryVersionService) {

		_objectEntryVersionService = objectEntryVersionService;
	}

	private ObjectEntryVersionService _objectEntryVersionService;

}