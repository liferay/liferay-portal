/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link ObjectEntryVersionLocalService}.
 *
 * @author Marco Leo
 * @see ObjectEntryVersionLocalService
 * @generated
 */
public class ObjectEntryVersionLocalServiceWrapper
	implements ObjectEntryVersionLocalService,
			   ServiceWrapper<ObjectEntryVersionLocalService> {

	public ObjectEntryVersionLocalServiceWrapper() {
		this(null);
	}

	public ObjectEntryVersionLocalServiceWrapper(
		ObjectEntryVersionLocalService objectEntryVersionLocalService) {

		_objectEntryVersionLocalService = objectEntryVersionLocalService;
	}

	@Override
	public com.liferay.object.model.ObjectEntryVersion addObjectEntryVersion(
			com.liferay.object.model.ObjectEntry objectEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryVersionLocalService.addObjectEntryVersion(
			objectEntry);
	}

	/**
	 * Adds the object entry version to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectEntryVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectEntryVersion the object entry version
	 * @return the object entry version that was added
	 */
	@Override
	public com.liferay.object.model.ObjectEntryVersion addObjectEntryVersion(
		com.liferay.object.model.ObjectEntryVersion objectEntryVersion) {

		return _objectEntryVersionLocalService.addObjectEntryVersion(
			objectEntryVersion);
	}

	@Override
	public void checkObjectEntryVersions(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_objectEntryVersionLocalService.checkObjectEntryVersions(companyId);
	}

	/**
	 * Creates a new object entry version with the primary key. Does not add the object entry version to the database.
	 *
	 * @param objectEntryVersionId the primary key for the new object entry version
	 * @return the new object entry version
	 */
	@Override
	public com.liferay.object.model.ObjectEntryVersion createObjectEntryVersion(
		long objectEntryVersionId) {

		return _objectEntryVersionLocalService.createObjectEntryVersion(
			objectEntryVersionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryVersionLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the object entry version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectEntryVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectEntryVersionId the primary key of the object entry version
	 * @return the object entry version that was removed
	 * @throws PortalException if a object entry version with the primary key could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectEntryVersion deleteObjectEntryVersion(
			long objectEntryVersionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryVersionLocalService.deleteObjectEntryVersion(
			objectEntryVersionId);
	}

	@Override
	public com.liferay.object.model.ObjectEntryVersion deleteObjectEntryVersion(
			long objectEntryId, int version)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryVersionLocalService.deleteObjectEntryVersion(
			objectEntryId, version);
	}

	/**
	 * Deletes the object entry version from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectEntryVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectEntryVersion the object entry version
	 * @return the object entry version that was removed
	 */
	@Override
	public com.liferay.object.model.ObjectEntryVersion deleteObjectEntryVersion(
		com.liferay.object.model.ObjectEntryVersion objectEntryVersion) {

		return _objectEntryVersionLocalService.deleteObjectEntryVersion(
			objectEntryVersion);
	}

	@Override
	public void deleteObjectEntryVersionByObjectDefinitionId(
		Long objectDefinitionId) {

		_objectEntryVersionLocalService.
			deleteObjectEntryVersionByObjectDefinitionId(objectDefinitionId);
	}

	@Override
	public void deleteObjectEntryVersions(long objectEntryId) {
		_objectEntryVersionLocalService.deleteObjectEntryVersions(
			objectEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryVersionLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _objectEntryVersionLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _objectEntryVersionLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _objectEntryVersionLocalService.dynamicQuery();
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

		return _objectEntryVersionLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryVersionModelImpl</code>.
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

		return _objectEntryVersionLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryVersionModelImpl</code>.
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

		return _objectEntryVersionLocalService.dynamicQuery(
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

		return _objectEntryVersionLocalService.dynamicQueryCount(dynamicQuery);
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

		return _objectEntryVersionLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.object.model.ObjectEntryVersion expireObjectEntryVersion(
			long userId, com.liferay.object.model.ObjectEntry objectEntry,
			int version,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryVersionLocalService.expireObjectEntryVersion(
			userId, objectEntry, version, serviceContext);
	}

	@Override
	public com.liferay.object.model.ObjectEntryVersion expireObjectEntryVersion(
			long userId,
			com.liferay.object.model.ObjectEntryVersion objectEntryVersion)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryVersionLocalService.expireObjectEntryVersion(
			userId, objectEntryVersion);
	}

	@Override
	public void expireObjectEntryVersions(
			long userId, com.liferay.object.model.ObjectEntry objectEntry,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws Exception {

		_objectEntryVersionLocalService.expireObjectEntryVersions(
			userId, objectEntry, serviceContext);
	}

	@Override
	public com.liferay.object.model.ObjectEntryVersion
		fetchLatestApprovedObjectEntryVersion(
			long objectEntryId,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.object.model.ObjectEntryVersion>
					orderByComparator) {

		return _objectEntryVersionLocalService.
			fetchLatestApprovedObjectEntryVersion(
				objectEntryId, orderByComparator);
	}

	@Override
	public com.liferay.object.model.ObjectEntryVersion fetchObjectEntryVersion(
		long objectEntryVersionId) {

		return _objectEntryVersionLocalService.fetchObjectEntryVersion(
			objectEntryVersionId);
	}

	@Override
	public com.liferay.object.model.ObjectEntryVersion fetchObjectEntryVersion(
		long objectEntryId, int version) {

		return _objectEntryVersionLocalService.fetchObjectEntryVersion(
			objectEntryId, version);
	}

	/**
	 * Returns the object entry version with the matching UUID and company.
	 *
	 * @param uuid the object entry version's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectEntryVersion
		fetchObjectEntryVersionByUuidAndCompanyId(String uuid, long companyId) {

		return _objectEntryVersionLocalService.
			fetchObjectEntryVersionByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _objectEntryVersionLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _objectEntryVersionLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _objectEntryVersionLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the object entry version with the primary key.
	 *
	 * @param objectEntryVersionId the primary key of the object entry version
	 * @return the object entry version
	 * @throws PortalException if a object entry version with the primary key could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectEntryVersion getObjectEntryVersion(
			long objectEntryVersionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryVersionLocalService.getObjectEntryVersion(
			objectEntryVersionId);
	}

	@Override
	public com.liferay.object.model.ObjectEntryVersion getObjectEntryVersion(
			long objectEntryId, int version)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryVersionLocalService.getObjectEntryVersion(
			objectEntryId, version);
	}

	/**
	 * Returns the object entry version with the matching UUID and company.
	 *
	 * @param uuid the object entry version's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object entry version
	 * @throws PortalException if a matching object entry version could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectEntryVersion
			getObjectEntryVersionByUuidAndCompanyId(String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryVersionLocalService.
			getObjectEntryVersionByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of all the object entry versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @return the range of object entry versions
	 */
	@Override
	public java.util.List<com.liferay.object.model.ObjectEntryVersion>
		getObjectEntryVersions(int start, int end) {

		return _objectEntryVersionLocalService.getObjectEntryVersions(
			start, end);
	}

	@Override
	public java.util.List<com.liferay.object.model.ObjectEntryVersion>
		getObjectEntryVersions(long objectEntryId) {

		return _objectEntryVersionLocalService.getObjectEntryVersions(
			objectEntryId);
	}

	@Override
	public java.util.List<com.liferay.object.model.ObjectEntryVersion>
		getObjectEntryVersions(long objectEntryId, int start, int end) {

		return _objectEntryVersionLocalService.getObjectEntryVersions(
			objectEntryId, start, end);
	}

	/**
	 * Returns the number of object entry versions.
	 *
	 * @return the number of object entry versions
	 */
	@Override
	public int getObjectEntryVersionsCount() {
		return _objectEntryVersionLocalService.getObjectEntryVersionsCount();
	}

	@Override
	public int getObjectEntryVersionsCount(long objectEntryId) {
		return _objectEntryVersionLocalService.getObjectEntryVersionsCount(
			objectEntryId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _objectEntryVersionLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryVersionLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public boolean isLatestObjectEntryVersion(long objectEntryId, int version)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryVersionLocalService.isLatestObjectEntryVersion(
			objectEntryId, version);
	}

	@Override
	public com.liferay.object.model.ObjectEntryVersion
			updateLatestObjectEntryVersion(
				com.liferay.object.model.ObjectEntry objectEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryVersionLocalService.updateLatestObjectEntryVersion(
			objectEntry);
	}

	/**
	 * Updates the object entry version in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectEntryVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectEntryVersion the object entry version
	 * @return the object entry version that was updated
	 */
	@Override
	public com.liferay.object.model.ObjectEntryVersion updateObjectEntryVersion(
		com.liferay.object.model.ObjectEntryVersion objectEntryVersion) {

		return _objectEntryVersionLocalService.updateObjectEntryVersion(
			objectEntryVersion);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _objectEntryVersionLocalService.getBasePersistence();
	}

	@Override
	public ObjectEntryVersionLocalService getWrappedService() {
		return _objectEntryVersionLocalService;
	}

	@Override
	public void setWrappedService(
		ObjectEntryVersionLocalService objectEntryVersionLocalService) {

		_objectEntryVersionLocalService = objectEntryVersionLocalService;
	}

	private ObjectEntryVersionLocalService _objectEntryVersionLocalService;

}