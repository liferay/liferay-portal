/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link LazyBlobEntryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see LazyBlobEntryLocalService
 * @generated
 */
public class LazyBlobEntryLocalServiceWrapper
	implements LazyBlobEntryLocalService,
			   ServiceWrapper<LazyBlobEntryLocalService> {

	public LazyBlobEntryLocalServiceWrapper() {
		this(null);
	}

	public LazyBlobEntryLocalServiceWrapper(
		LazyBlobEntryLocalService lazyBlobEntryLocalService) {

		_lazyBlobEntryLocalService = lazyBlobEntryLocalService;
	}

	/**
	 * Adds the lazy blob entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LazyBlobEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param lazyBlobEntry the lazy blob entry
	 * @return the lazy blob entry that was added
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.LazyBlobEntry
		addLazyBlobEntry(
			com.liferay.portal.tools.service.builder.test.model.LazyBlobEntry
				lazyBlobEntry) {

		return _lazyBlobEntryLocalService.addLazyBlobEntry(lazyBlobEntry);
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.model.LazyBlobEntry
		addLazyBlobEntry(
			long groupId, byte[] bytes,
			com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _lazyBlobEntryLocalService.addLazyBlobEntry(
			groupId, bytes, serviceContext);
	}

	/**
	 * Creates a new lazy blob entry with the primary key. Does not add the lazy blob entry to the database.
	 *
	 * @param lazyBlobEntryId the primary key for the new lazy blob entry
	 * @return the new lazy blob entry
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.LazyBlobEntry
		createLazyBlobEntry(long lazyBlobEntryId) {

		return _lazyBlobEntryLocalService.createLazyBlobEntry(lazyBlobEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _lazyBlobEntryLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the lazy blob entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LazyBlobEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param lazyBlobEntry the lazy blob entry
	 * @return the lazy blob entry that was removed
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.LazyBlobEntry
		deleteLazyBlobEntry(
			com.liferay.portal.tools.service.builder.test.model.LazyBlobEntry
				lazyBlobEntry) {

		return _lazyBlobEntryLocalService.deleteLazyBlobEntry(lazyBlobEntry);
	}

	/**
	 * Deletes the lazy blob entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LazyBlobEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param lazyBlobEntryId the primary key of the lazy blob entry
	 * @return the lazy blob entry that was removed
	 * @throws PortalException if a lazy blob entry with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.LazyBlobEntry
			deleteLazyBlobEntry(long lazyBlobEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _lazyBlobEntryLocalService.deleteLazyBlobEntry(lazyBlobEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _lazyBlobEntryLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _lazyBlobEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _lazyBlobEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _lazyBlobEntryLocalService.dynamicQuery();
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

		return _lazyBlobEntryLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.LazyBlobEntryModelImpl</code>.
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

		return _lazyBlobEntryLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.LazyBlobEntryModelImpl</code>.
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

		return _lazyBlobEntryLocalService.dynamicQuery(
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

		return _lazyBlobEntryLocalService.dynamicQueryCount(dynamicQuery);
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

		return _lazyBlobEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.model.LazyBlobEntry
		fetchLazyBlobEntry(long lazyBlobEntryId) {

		return _lazyBlobEntryLocalService.fetchLazyBlobEntry(lazyBlobEntryId);
	}

	/**
	 * Returns the lazy blob entry matching the UUID and group.
	 *
	 * @param uuid the lazy blob entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching lazy blob entry, or <code>null</code> if a matching lazy blob entry could not be found
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.LazyBlobEntry
		fetchLazyBlobEntryByUuidAndGroupId(String uuid, long groupId) {

		return _lazyBlobEntryLocalService.fetchLazyBlobEntryByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _lazyBlobEntryLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _lazyBlobEntryLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the lazy blob entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.LazyBlobEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of lazy blob entries
	 * @param end the upper bound of the range of lazy blob entries (not inclusive)
	 * @return the range of lazy blob entries
	 */
	@Override
	public java.util.List
		<com.liferay.portal.tools.service.builder.test.model.LazyBlobEntry>
			getLazyBlobEntries(int start, int end) {

		return _lazyBlobEntryLocalService.getLazyBlobEntries(start, end);
	}

	/**
	 * Returns the number of lazy blob entries.
	 *
	 * @return the number of lazy blob entries
	 */
	@Override
	public int getLazyBlobEntriesCount() {
		return _lazyBlobEntryLocalService.getLazyBlobEntriesCount();
	}

	/**
	 * Returns the lazy blob entry with the primary key.
	 *
	 * @param lazyBlobEntryId the primary key of the lazy blob entry
	 * @return the lazy blob entry
	 * @throws PortalException if a lazy blob entry with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.LazyBlobEntry
			getLazyBlobEntry(long lazyBlobEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _lazyBlobEntryLocalService.getLazyBlobEntry(lazyBlobEntryId);
	}

	/**
	 * Returns the lazy blob entry matching the UUID and group.
	 *
	 * @param uuid the lazy blob entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching lazy blob entry
	 * @throws PortalException if a matching lazy blob entry could not be found
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.LazyBlobEntry
			getLazyBlobEntryByUuidAndGroupId(String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _lazyBlobEntryLocalService.getLazyBlobEntryByUuidAndGroupId(
			uuid, groupId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _lazyBlobEntryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _lazyBlobEntryLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the lazy blob entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LazyBlobEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param lazyBlobEntry the lazy blob entry
	 * @return the lazy blob entry that was updated
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.LazyBlobEntry
		updateLazyBlobEntry(
			com.liferay.portal.tools.service.builder.test.model.LazyBlobEntry
				lazyBlobEntry) {

		return _lazyBlobEntryLocalService.updateLazyBlobEntry(lazyBlobEntry);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _lazyBlobEntryLocalService.getBasePersistence();
	}

	@Override
	public LazyBlobEntryLocalService getWrappedService() {
		return _lazyBlobEntryLocalService;
	}

	@Override
	public void setWrappedService(
		LazyBlobEntryLocalService lazyBlobEntryLocalService) {

		_lazyBlobEntryLocalService = lazyBlobEntryLocalService;
	}

	private LazyBlobEntryLocalService _lazyBlobEntryLocalService;

}