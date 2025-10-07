/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.service.BaseService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the remote service interface for ObjectEntryFolder. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Marco Leo
 * @see ObjectEntryFolderServiceUtil
 * @generated
 */
@AccessControlled
@JSONWebService
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface ObjectEntryFolderService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.object.service.impl.ObjectEntryFolderServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the object entry folder remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link ObjectEntryFolderServiceUtil} if injection and service tracking are not available.
	 */
	public ObjectEntryFolder addObjectEntryFolder(
			String externalReferenceCode, long groupId,
			long parentObjectEntryFolderId, String description,
			Map<Locale, String> labelMap, String name,
			ServiceContext serviceContext)
		throws PortalException;

	public ObjectEntryFolder deleteObjectEntryFolder(long objectEntryFolderId)
		throws PortalException;

	public ObjectEntryFolder deleteObjectEntryFolderByExternalReferenceCode(
			String externalReferenceCode, long groupId, long companyId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectEntryFolder fetchObjectEntryFolder(long objectEntryFolderId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectEntryFolder fetchObjectEntryFolderByExternalReferenceCode(
			String externalReferenceCode, long groupId, long companyId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectEntryFolder getObjectEntryFolder(long objectEntryFolderId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectEntryFolder getObjectEntryFolderByExternalReferenceCode(
			String externalReferenceCode, long groupId, long companyId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ObjectEntryFolder> getObjectEntryFolders(
			long groupId, long companyId, long parentObjectEntryFolderId,
			int start, int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getObjectEntryFoldersCount(
			long groupId, long companyId, long parentObjectEntryFolderId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectEntryFolder getOrAddEmptyObjectEntryFolder(
			String externalReferenceCode, long groupId, long companyId,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	public ObjectEntryFolder moveObjectEntryFolderToTrash(
			ObjectEntryFolder objectEntryFolder, ServiceContext serviceContext)
		throws PortalException;

	public ObjectEntryFolder restoreObjectEntryFolderFromTrash(
			ObjectEntryFolder objectEntryFolder, ServiceContext serviceContext)
		throws PortalException;

	public void subscribeObjectEntryFolder(
			long groupId, long objectEntryFolderId)
		throws PortalException;

	public void unsubscribeObjectEntryFolder(
			long groupId, long objectEntryFolderId)
		throws PortalException;

	public ObjectEntryFolder updateObjectEntryFolder(
			long objectEntryFolderId, long parentObjectEntryFolderId,
			String description, Map<Locale, String> labelMap, String name,
			ServiceContext serviceContext)
		throws PortalException;

}