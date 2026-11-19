/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link ObjectFolderLocalService}.
 *
 * @author Marco Leo
 * @see ObjectFolderLocalService
 * @generated
 */
public class ObjectFolderLocalServiceWrapper
	implements ObjectFolderLocalService,
			   ServiceWrapper<ObjectFolderLocalService> {

	public ObjectFolderLocalServiceWrapper() {
		this(null);
	}

	public ObjectFolderLocalServiceWrapper(
		ObjectFolderLocalService objectFolderLocalService) {

		_objectFolderLocalService = objectFolderLocalService;
	}

	/**
	 * Adds the object folder to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectFolderLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectFolder the object folder
	 * @return the object folder that was added
	 */
	@Override
	public com.liferay.object.model.ObjectFolder addObjectFolder(
		com.liferay.object.model.ObjectFolder objectFolder) {

		return _objectFolderLocalService.addObjectFolder(objectFolder);
	}

	@Override
	public com.liferay.object.model.ObjectFolder addObjectFolder(
			String externalReferenceCode, long userId,
			java.util.Map<java.util.Locale, String> labelMap, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectFolderLocalService.addObjectFolder(
			externalReferenceCode, userId, labelMap, name);
	}

	/**
	 * Creates a new object folder with the primary key. Does not add the object folder to the database.
	 *
	 * @param objectFolderId the primary key for the new object folder
	 * @return the new object folder
	 */
	@Override
	public com.liferay.object.model.ObjectFolder createObjectFolder(
		long objectFolderId) {

		return _objectFolderLocalService.createObjectFolder(objectFolderId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectFolderLocalService.createPersistedModel(primaryKeyObj);
	}

	@Override
	public void deleteCompanyObjectFolders(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_objectFolderLocalService.deleteCompanyObjectFolders(companyId);
	}

	/**
	 * Deletes the object folder with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectFolderLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectFolderId the primary key of the object folder
	 * @return the object folder that was removed
	 * @throws PortalException if a object folder with the primary key could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectFolder deleteObjectFolder(
			long objectFolderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectFolderLocalService.deleteObjectFolder(objectFolderId);
	}

	/**
	 * Deletes the object folder from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectFolderLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectFolder the object folder
	 * @return the object folder that was removed
	 * @throws PortalException
	 */
	@Override
	public com.liferay.object.model.ObjectFolder deleteObjectFolder(
			com.liferay.object.model.ObjectFolder objectFolder)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectFolderLocalService.deleteObjectFolder(objectFolder);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectFolderLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _objectFolderLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _objectFolderLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _objectFolderLocalService.dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _objectFolderLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFolderModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _objectFolderLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFolderModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _objectFolderLocalService.dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _objectFolderLocalService.dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return _objectFolderLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.object.model.ObjectFolder fetchDefaultObjectFolder(
		long companyId) {

		return _objectFolderLocalService.fetchDefaultObjectFolder(companyId);
	}

	@Override
	public com.liferay.object.model.ObjectFolder fetchObjectFolder(
		long objectFolderId) {

		return _objectFolderLocalService.fetchObjectFolder(objectFolderId);
	}

	@Override
	public com.liferay.object.model.ObjectFolder fetchObjectFolder(
		long companyId, String name) {

		return _objectFolderLocalService.fetchObjectFolder(companyId, name);
	}

	@Override
	public com.liferay.object.model.ObjectFolder
		fetchObjectFolderByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return _objectFolderLocalService.
			fetchObjectFolderByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the object folder with the matching UUID and company.
	 *
	 * @param uuid the object folder's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object folder, or <code>null</code> if a matching object folder could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectFolder
		fetchObjectFolderByUuidAndCompanyId(String uuid, long companyId) {

		return _objectFolderLocalService.fetchObjectFolderByUuidAndCompanyId(
			uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _objectFolderLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.object.model.ObjectFolder getDefaultObjectFolder(
			long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectFolderLocalService.getDefaultObjectFolder(companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _objectFolderLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _objectFolderLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the object folder with the primary key.
	 *
	 * @param objectFolderId the primary key of the object folder
	 * @return the object folder
	 * @throws PortalException if a object folder with the primary key could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectFolder getObjectFolder(
			long objectFolderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectFolderLocalService.getObjectFolder(objectFolderId);
	}

	@Override
	public com.liferay.object.model.ObjectFolder getObjectFolder(
			long companyId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectFolderLocalService.getObjectFolder(companyId, name);
	}

	@Override
	public com.liferay.object.model.ObjectFolder
			getObjectFolderByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectFolderLocalService.getObjectFolderByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the object folder with the matching UUID and company.
	 *
	 * @param uuid the object folder's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object folder
	 * @throws PortalException if a matching object folder could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectFolder
			getObjectFolderByUuidAndCompanyId(String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectFolderLocalService.getObjectFolderByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of all the object folders.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFolderModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object folders
	 * @param end the upper bound of the range of object folders (not inclusive)
	 * @return the range of object folders
	 */
	@Override
	public java.util.List<com.liferay.object.model.ObjectFolder>
		getObjectFolders(int start, int end) {

		return _objectFolderLocalService.getObjectFolders(start, end);
	}

	/**
	 * Returns the number of object folders.
	 *
	 * @return the number of object folders
	 */
	@Override
	public int getObjectFoldersCount() {
		return _objectFolderLocalService.getObjectFoldersCount();
	}

	@Override
	public com.liferay.object.model.ObjectFolder getOrAddDefaultObjectFolder(
			long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectFolderLocalService.getOrAddDefaultObjectFolder(companyId);
	}

	@Override
	public com.liferay.object.model.ObjectFolder getOrAddEmptyObjectFolder(
			String externalReferenceCode, long companyId, long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectFolderLocalService.getOrAddEmptyObjectFolder(
			externalReferenceCode, companyId, userId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _objectFolderLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectFolderLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the object folder in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectFolderLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectFolder the object folder
	 * @return the object folder that was updated
	 */
	@Override
	public com.liferay.object.model.ObjectFolder updateObjectFolder(
		com.liferay.object.model.ObjectFolder objectFolder) {

		return _objectFolderLocalService.updateObjectFolder(objectFolder);
	}

	@Override
	public com.liferay.object.model.ObjectFolder updateObjectFolder(
			String externalReferenceCode, long objectFolderId,
			java.util.Map<java.util.Locale, String> labelMap)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectFolderLocalService.updateObjectFolder(
			externalReferenceCode, objectFolderId, labelMap);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _objectFolderLocalService.getBasePersistence();
	}

	@Override
	public ObjectFolderLocalService getWrappedService() {
		return _objectFolderLocalService;
	}

	@Override
	public void setWrappedService(
		ObjectFolderLocalService objectFolderLocalService) {

		_objectFolderLocalService = objectFolderLocalService;
	}

	private ObjectFolderLocalService _objectFolderLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1039437113