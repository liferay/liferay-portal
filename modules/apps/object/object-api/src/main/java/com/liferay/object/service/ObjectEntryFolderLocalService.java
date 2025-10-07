/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for ObjectEntryFolder. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Marco Leo
 * @see ObjectEntryFolderLocalServiceUtil
 * @generated
 */
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface ObjectEntryFolderLocalService
	extends BaseLocalService, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.object.service.impl.ObjectEntryFolderLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the object entry folder local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link ObjectEntryFolderLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the object entry folder to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectEntryFolderLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectEntryFolder the object entry folder
	 * @return the object entry folder that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public ObjectEntryFolder addObjectEntryFolder(
		ObjectEntryFolder objectEntryFolder);

	@Indexable(type = IndexableType.REINDEX)
	public ObjectEntryFolder addObjectEntryFolder(
			String externalReferenceCode, long groupId, long userId,
			long parentObjectEntryFolderId, String description,
			Map<Locale, String> labelMap, String name,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Creates a new object entry folder with the primary key. Does not add the object entry folder to the database.
	 *
	 * @param objectEntryFolderId the primary key for the new object entry folder
	 * @return the new object entry folder
	 */
	@Transactional(enabled = false)
	public ObjectEntryFolder createObjectEntryFolder(long objectEntryFolderId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the object entry folder with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectEntryFolderLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectEntryFolderId the primary key of the object entry folder
	 * @return the object entry folder that was removed
	 * @throws PortalException if a object entry folder with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public ObjectEntryFolder deleteObjectEntryFolder(long objectEntryFolderId)
		throws PortalException;

	/**
	 * Deletes the object entry folder from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectEntryFolderLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectEntryFolder the object entry folder
	 * @return the object entry folder that was removed
	 * @throws PortalException
	 */
	@Indexable(type = IndexableType.DELETE)
	public ObjectEntryFolder deleteObjectEntryFolder(
			ObjectEntryFolder objectEntryFolder)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public ObjectEntryFolder deleteObjectEntryFolderByExternalReferenceCode(
			String externalReferenceCode, long groupId, long companyId)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> T dslQuery(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int dslQueryCount(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DynamicQuery dynamicQuery();

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(DynamicQuery dynamicQuery);

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end);

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(DynamicQuery dynamicQuery);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(
		DynamicQuery dynamicQuery, Projection projection);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectEntryFolder fetchObjectEntryFolder(long objectEntryFolderId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectEntryFolder fetchObjectEntryFolderByExternalReferenceCode(
		String externalReferenceCode, long groupId, long companyId);

	/**
	 * Returns the object entry folder matching the UUID and group.
	 *
	 * @param uuid the object entry folder's UUID
	 * @param groupId the primary key of the group
	 * @return the matching object entry folder, or <code>null</code> if a matching object entry folder could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectEntryFolder fetchObjectEntryFolderByUuidAndGroupId(
		String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	/**
	 * Returns the object entry folder with the primary key.
	 *
	 * @param objectEntryFolderId the primary key of the object entry folder
	 * @return the object entry folder
	 * @throws PortalException if a object entry folder with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectEntryFolder getObjectEntryFolder(long objectEntryFolderId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectEntryFolder getObjectEntryFolderByExternalReferenceCode(
			String externalReferenceCode, long groupId, long companyId)
		throws PortalException;

	/**
	 * Returns the object entry folder matching the UUID and group.
	 *
	 * @param uuid the object entry folder's UUID
	 * @param groupId the primary key of the group
	 * @return the matching object entry folder
	 * @throws PortalException if a matching object entry folder could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectEntryFolder getObjectEntryFolderByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException;

	/**
	 * Returns a range of all the object entry folders.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @return the range of object entry folders
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ObjectEntryFolder> getObjectEntryFolders(int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ObjectEntryFolder> getObjectEntryFolders(
		long groupId, long companyId, long parentObjectEntryFolderId, int start,
		int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ObjectEntryFolder> getObjectEntryFoldersByExternalReferenceCode(
		String externalReferenceCode, List<Long> groupIds, long companyId);

	/**
	 * Returns all the object entry folders matching the UUID and company.
	 *
	 * @param uuid the UUID of the object entry folders
	 * @param companyId the primary key of the company
	 * @return the matching object entry folders, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ObjectEntryFolder> getObjectEntryFoldersByUuidAndCompanyId(
		String uuid, long companyId);

	/**
	 * Returns a range of object entry folders matching the UUID and company.
	 *
	 * @param uuid the UUID of the object entry folders
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching object entry folders, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ObjectEntryFolder> getObjectEntryFoldersByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectEntryFolder> orderByComparator);

	/**
	 * Returns the number of object entry folders.
	 *
	 * @return the number of object entry folders
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getObjectEntryFoldersCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getObjectEntryFoldersCount(
		long groupId, long companyId, long parentObjectEntryFolderId);

	@Indexable(type = IndexableType.REINDEX)
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectEntryFolder getOrAddEmptyObjectEntryFolder(
			String externalReferenceCode, long groupId, long companyId,
			long userId, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	/**
	 * @throws PortalException
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	public void moveObjectEntryFoldersToTrash(
			long userId, ObjectEntryFolder parentObjectEntryFolder,
			ServiceContext serviceContext)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public ObjectEntryFolder moveObjectEntryFolderToTrash(
			long userId, ObjectEntryFolder objectEntryFolder,
			ServiceContext serviceContext)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public ObjectEntryFolder restoreObjectEntryFolderFromTrash(
			long userId, ObjectEntryFolder objectEntryFolder,
			ServiceContext serviceContext)
		throws PortalException;

	public void restoreObjectEntryFoldersFromTrash(
			long userId, ObjectEntryFolder parentObjectEntryFolder,
			ServiceContext serviceContext)
		throws PortalException;

	public void subscribeObjectEntryFolder(
			long userId, long groupId, long objectEntryFolderId)
		throws PortalException;

	public void unsubscribeObjectEntryFolder(
			long userId, long groupId, long objectEntryFolderId)
		throws PortalException;

	public ObjectEntryFolder updateObjectEntryFolder(
			long userId, long objectEntryFolderId,
			long parentObjectEntryFolderId, String description,
			Map<Locale, String> labelMap, String name,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Updates the object entry folder in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectEntryFolderLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectEntryFolder the object entry folder
	 * @return the object entry folder that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public ObjectEntryFolder updateObjectEntryFolder(
		ObjectEntryFolder objectEntryFolder);

	public ObjectEntryFolder updateStatus(
			ObjectEntryFolder objectEntryFolder, int status)
		throws PortalException;

}