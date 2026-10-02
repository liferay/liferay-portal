/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.changeset.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link ChangesetCollectionLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see ChangesetCollectionLocalService
 * @generated
 */
public class ChangesetCollectionLocalServiceWrapper
	implements ChangesetCollectionLocalService,
			   ServiceWrapper<ChangesetCollectionLocalService> {

	public ChangesetCollectionLocalServiceWrapper() {
		this(null);
	}

	public ChangesetCollectionLocalServiceWrapper(
		ChangesetCollectionLocalService changesetCollectionLocalService) {

		_changesetCollectionLocalService = changesetCollectionLocalService;
	}

	/**
	 * Adds the changeset collection to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ChangesetCollectionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param changesetCollection the changeset collection
	 * @return the changeset collection that was added
	 */
	@Override
	public com.liferay.changeset.model.ChangesetCollection
		addChangesetCollection(
			com.liferay.changeset.model.ChangesetCollection
				changesetCollection) {

		return _changesetCollectionLocalService.addChangesetCollection(
			changesetCollection);
	}

	@Override
	public com.liferay.changeset.model.ChangesetCollection
			addChangesetCollection(
				long userId, long groupId, String name, String description)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _changesetCollectionLocalService.addChangesetCollection(
			userId, groupId, name, description);
	}

	/**
	 * Creates a new changeset collection with the primary key. Does not add the changeset collection to the database.
	 *
	 * @param changesetCollectionId the primary key for the new changeset collection
	 * @return the new changeset collection
	 */
	@Override
	public com.liferay.changeset.model.ChangesetCollection
		createChangesetCollection(long changesetCollectionId) {

		return _changesetCollectionLocalService.createChangesetCollection(
			changesetCollectionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _changesetCollectionLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the changeset collection from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ChangesetCollectionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param changesetCollection the changeset collection
	 * @return the changeset collection that was removed
	 */
	@Override
	public com.liferay.changeset.model.ChangesetCollection
		deleteChangesetCollection(
			com.liferay.changeset.model.ChangesetCollection
				changesetCollection) {

		return _changesetCollectionLocalService.deleteChangesetCollection(
			changesetCollection);
	}

	/**
	 * Deletes the changeset collection with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ChangesetCollectionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param changesetCollectionId the primary key of the changeset collection
	 * @return the changeset collection that was removed
	 * @throws PortalException if a changeset collection with the primary key could not be found
	 */
	@Override
	public com.liferay.changeset.model.ChangesetCollection
			deleteChangesetCollection(long changesetCollectionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _changesetCollectionLocalService.deleteChangesetCollection(
			changesetCollectionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _changesetCollectionLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _changesetCollectionLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _changesetCollectionLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _changesetCollectionLocalService.dynamicQuery();
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

		return _changesetCollectionLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.changeset.model.impl.ChangesetCollectionModelImpl</code>.
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

		return _changesetCollectionLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.changeset.model.impl.ChangesetCollectionModelImpl</code>.
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

		return _changesetCollectionLocalService.dynamicQuery(
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

		return _changesetCollectionLocalService.dynamicQueryCount(dynamicQuery);
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

		return _changesetCollectionLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.changeset.model.ChangesetCollection
		fetchChangesetCollection(long changesetCollectionId) {

		return _changesetCollectionLocalService.fetchChangesetCollection(
			changesetCollectionId);
	}

	@Override
	public com.liferay.changeset.model.ChangesetCollection
		fetchChangesetCollection(long groupId, String name) {

		return _changesetCollectionLocalService.fetchChangesetCollection(
			groupId, name);
	}

	@Override
	public com.liferay.changeset.model.ChangesetCollection
			fetchOrAddChangesetCollection(long groupId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _changesetCollectionLocalService.fetchOrAddChangesetCollection(
			groupId, name);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _changesetCollectionLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the changeset collection with the primary key.
	 *
	 * @param changesetCollectionId the primary key of the changeset collection
	 * @return the changeset collection
	 * @throws PortalException if a changeset collection with the primary key could not be found
	 */
	@Override
	public com.liferay.changeset.model.ChangesetCollection
			getChangesetCollection(long changesetCollectionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _changesetCollectionLocalService.getChangesetCollection(
			changesetCollectionId);
	}

	/**
	 * Returns a range of all the changeset collections.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.changeset.model.impl.ChangesetCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of changeset collections
	 * @param end the upper bound of the range of changeset collections (not inclusive)
	 * @return the range of changeset collections
	 */
	@Override
	public java.util.List<com.liferay.changeset.model.ChangesetCollection>
		getChangesetCollections(int start, int end) {

		return _changesetCollectionLocalService.getChangesetCollections(
			start, end);
	}

	/**
	 * Returns the number of changeset collections.
	 *
	 * @return the number of changeset collections
	 */
	@Override
	public int getChangesetCollectionsCount() {
		return _changesetCollectionLocalService.getChangesetCollectionsCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _changesetCollectionLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _changesetCollectionLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _changesetCollectionLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the changeset collection in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ChangesetCollectionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param changesetCollection the changeset collection
	 * @return the changeset collection that was updated
	 */
	@Override
	public com.liferay.changeset.model.ChangesetCollection
		updateChangesetCollection(
			com.liferay.changeset.model.ChangesetCollection
				changesetCollection) {

		return _changesetCollectionLocalService.updateChangesetCollection(
			changesetCollection);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _changesetCollectionLocalService.getBasePersistence();
	}

	@Override
	public ChangesetCollectionLocalService getWrappedService() {
		return _changesetCollectionLocalService;
	}

	@Override
	public void setWrappedService(
		ChangesetCollectionLocalService changesetCollectionLocalService) {

		_changesetCollectionLocalService = changesetCollectionLocalService;
	}

	private ChangesetCollectionLocalService _changesetCollectionLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1702160150