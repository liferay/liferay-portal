/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link ObjectEntryFolderService}.
 *
 * @author Marco Leo
 * @see ObjectEntryFolderService
 * @generated
 */
public class ObjectEntryFolderServiceWrapper
	implements ObjectEntryFolderService,
			   ServiceWrapper<ObjectEntryFolderService> {

	public ObjectEntryFolderServiceWrapper() {
		this(null);
	}

	public ObjectEntryFolderServiceWrapper(
		ObjectEntryFolderService objectEntryFolderService) {

		_objectEntryFolderService = objectEntryFolderService;
	}

	@Override
	public com.liferay.object.model.ObjectEntryFolder addObjectEntryFolder(
			String externalReferenceCode, long groupId,
			long parentObjectEntryFolderId, String description,
			java.util.Map<java.util.Locale, String> labelMap, String name,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryFolderService.addObjectEntryFolder(
			externalReferenceCode, groupId, parentObjectEntryFolderId,
			description, labelMap, name, serviceContext);
	}

	@Override
	public com.liferay.object.model.ObjectEntryFolder deleteObjectEntryFolder(
			long objectEntryFolderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryFolderService.deleteObjectEntryFolder(
			objectEntryFolderId);
	}

	@Override
	public com.liferay.object.model.ObjectEntryFolder
			deleteObjectEntryFolderByExternalReferenceCode(
				String externalReferenceCode, long groupId, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryFolderService.
			deleteObjectEntryFolderByExternalReferenceCode(
				externalReferenceCode, groupId, companyId);
	}

	@Override
	public com.liferay.object.model.ObjectEntryFolder fetchObjectEntryFolder(
			long objectEntryFolderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryFolderService.fetchObjectEntryFolder(
			objectEntryFolderId);
	}

	@Override
	public com.liferay.object.model.ObjectEntryFolder
			fetchObjectEntryFolderByExternalReferenceCode(
				String externalReferenceCode, long groupId, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryFolderService.
			fetchObjectEntryFolderByExternalReferenceCode(
				externalReferenceCode, groupId, companyId);
	}

	@Override
	public com.liferay.object.model.ObjectEntryFolder getObjectEntryFolder(
			long objectEntryFolderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryFolderService.getObjectEntryFolder(
			objectEntryFolderId);
	}

	@Override
	public com.liferay.object.model.ObjectEntryFolder
			getObjectEntryFolderByExternalReferenceCode(
				String externalReferenceCode, long groupId, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryFolderService.
			getObjectEntryFolderByExternalReferenceCode(
				externalReferenceCode, groupId, companyId);
	}

	@Override
	public java.util.List<com.liferay.object.model.ObjectEntryFolder>
			getObjectEntryFolders(
				long groupId, long companyId, long parentObjectEntryFolderId,
				int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryFolderService.getObjectEntryFolders(
			groupId, companyId, parentObjectEntryFolderId, start, end);
	}

	@Override
	public int getObjectEntryFoldersCount(
			long groupId, long companyId, long parentObjectEntryFolderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryFolderService.getObjectEntryFoldersCount(
			groupId, companyId, parentObjectEntryFolderId);
	}

	@Override
	public com.liferay.object.model.ObjectEntryFolder
			getOrAddEmptyObjectEntryFolder(
				String externalReferenceCode, long groupId, long companyId,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryFolderService.getOrAddEmptyObjectEntryFolder(
			externalReferenceCode, groupId, companyId, serviceContext);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _objectEntryFolderService.getOSGiServiceIdentifier();
	}

	@Override
	public com.liferay.object.model.ObjectEntryFolder
			moveObjectEntryFolderToTrash(
				com.liferay.object.model.ObjectEntryFolder objectEntryFolder,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryFolderService.moveObjectEntryFolderToTrash(
			objectEntryFolder, serviceContext);
	}

	@Override
	public com.liferay.object.model.ObjectEntryFolder
			restoreObjectEntryFolderFromTrash(
				com.liferay.object.model.ObjectEntryFolder objectEntryFolder,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryFolderService.restoreObjectEntryFolderFromTrash(
			objectEntryFolder, serviceContext);
	}

	@Override
	public void subscribeObjectEntryFolder(
			long groupId, long objectEntryFolderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_objectEntryFolderService.subscribeObjectEntryFolder(
			groupId, objectEntryFolderId);
	}

	@Override
	public void unsubscribeObjectEntryFolder(
			long groupId, long objectEntryFolderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_objectEntryFolderService.unsubscribeObjectEntryFolder(
			groupId, objectEntryFolderId);
	}

	@Override
	public com.liferay.object.model.ObjectEntryFolder updateObjectEntryFolder(
			long objectEntryFolderId, long parentObjectEntryFolderId,
			String description,
			java.util.Map<java.util.Locale, String> labelMap, String name,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryFolderService.updateObjectEntryFolder(
			objectEntryFolderId, parentObjectEntryFolderId, description,
			labelMap, name, serviceContext);
	}

	@Override
	public ObjectEntryFolderService getWrappedService() {
		return _objectEntryFolderService;
	}

	@Override
	public void setWrappedService(
		ObjectEntryFolderService objectEntryFolderService) {

		_objectEntryFolderService = objectEntryFolderService;
	}

	private ObjectEntryFolderService _objectEntryFolderService;

}