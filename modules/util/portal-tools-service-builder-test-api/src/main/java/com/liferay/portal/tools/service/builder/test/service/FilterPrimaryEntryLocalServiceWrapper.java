/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link FilterPrimaryEntryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see FilterPrimaryEntryLocalService
 * @generated
 */
public class FilterPrimaryEntryLocalServiceWrapper
	implements FilterPrimaryEntryLocalService,
			   ServiceWrapper<FilterPrimaryEntryLocalService> {

	public FilterPrimaryEntryLocalServiceWrapper() {
		this(null);
	}

	public FilterPrimaryEntryLocalServiceWrapper(
		FilterPrimaryEntryLocalService filterPrimaryEntryLocalService) {

		_filterPrimaryEntryLocalService = filterPrimaryEntryLocalService;
	}

	/**
	 * Adds the filter primary entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FilterPrimaryEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param filterPrimaryEntry the filter primary entry
	 * @return the filter primary entry that was added
	 */
	@Override
	public
		com.liferay.portal.tools.service.builder.test.model.FilterPrimaryEntry
			addFilterPrimaryEntry(
				com.liferay.portal.tools.service.builder.test.model.
					FilterPrimaryEntry filterPrimaryEntry) {

		return _filterPrimaryEntryLocalService.addFilterPrimaryEntry(
			filterPrimaryEntry);
	}

	/**
	 * Creates a new filter primary entry with the primary key. Does not add the filter primary entry to the database.
	 *
	 * @param filterPrimaryEntryId the primary key for the new filter primary entry
	 * @return the new filter primary entry
	 */
	@Override
	public
		com.liferay.portal.tools.service.builder.test.model.FilterPrimaryEntry
			createFilterPrimaryEntry(long filterPrimaryEntryId) {

		return _filterPrimaryEntryLocalService.createFilterPrimaryEntry(
			filterPrimaryEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _filterPrimaryEntryLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the filter primary entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FilterPrimaryEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param filterPrimaryEntry the filter primary entry
	 * @return the filter primary entry that was removed
	 */
	@Override
	public
		com.liferay.portal.tools.service.builder.test.model.FilterPrimaryEntry
			deleteFilterPrimaryEntry(
				com.liferay.portal.tools.service.builder.test.model.
					FilterPrimaryEntry filterPrimaryEntry) {

		return _filterPrimaryEntryLocalService.deleteFilterPrimaryEntry(
			filterPrimaryEntry);
	}

	/**
	 * Deletes the filter primary entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FilterPrimaryEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param filterPrimaryEntryId the primary key of the filter primary entry
	 * @return the filter primary entry that was removed
	 * @throws PortalException if a filter primary entry with the primary key could not be found
	 */
	@Override
	public
		com.liferay.portal.tools.service.builder.test.model.FilterPrimaryEntry
				deleteFilterPrimaryEntry(long filterPrimaryEntryId)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _filterPrimaryEntryLocalService.deleteFilterPrimaryEntry(
			filterPrimaryEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _filterPrimaryEntryLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _filterPrimaryEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _filterPrimaryEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _filterPrimaryEntryLocalService.dynamicQuery();
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

		return _filterPrimaryEntryLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.FilterPrimaryEntryModelImpl</code>.
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

		return _filterPrimaryEntryLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.FilterPrimaryEntryModelImpl</code>.
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

		return _filterPrimaryEntryLocalService.dynamicQuery(
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

		return _filterPrimaryEntryLocalService.dynamicQueryCount(dynamicQuery);
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

		return _filterPrimaryEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public
		com.liferay.portal.tools.service.builder.test.model.FilterPrimaryEntry
			fetchFilterPrimaryEntry(long filterPrimaryEntryId) {

		return _filterPrimaryEntryLocalService.fetchFilterPrimaryEntry(
			filterPrimaryEntryId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _filterPrimaryEntryLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the filter primary entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.FilterPrimaryEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of filter primary entries
	 * @param end the upper bound of the range of filter primary entries (not inclusive)
	 * @return the range of filter primary entries
	 */
	@Override
	public java.util.List
		<com.liferay.portal.tools.service.builder.test.model.FilterPrimaryEntry>
			getFilterPrimaryEntries(int start, int end) {

		return _filterPrimaryEntryLocalService.getFilterPrimaryEntries(
			start, end);
	}

	/**
	 * Returns the number of filter primary entries.
	 *
	 * @return the number of filter primary entries
	 */
	@Override
	public int getFilterPrimaryEntriesCount() {
		return _filterPrimaryEntryLocalService.getFilterPrimaryEntriesCount();
	}

	/**
	 * Returns the filter primary entry with the primary key.
	 *
	 * @param filterPrimaryEntryId the primary key of the filter primary entry
	 * @return the filter primary entry
	 * @throws PortalException if a filter primary entry with the primary key could not be found
	 */
	@Override
	public
		com.liferay.portal.tools.service.builder.test.model.FilterPrimaryEntry
				getFilterPrimaryEntry(long filterPrimaryEntryId)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _filterPrimaryEntryLocalService.getFilterPrimaryEntry(
			filterPrimaryEntryId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _filterPrimaryEntryLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _filterPrimaryEntryLocalService.getOSGiServiceIdentifier();
	}

	@Override
	public java.util.List
		<? extends com.liferay.portal.kernel.model.PersistedModel>
				getPersistedModel(long resourcePrimKey)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _filterPrimaryEntryLocalService.getPersistedModel(
			resourcePrimKey);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _filterPrimaryEntryLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the filter primary entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FilterPrimaryEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param filterPrimaryEntry the filter primary entry
	 * @return the filter primary entry that was updated
	 */
	@Override
	public
		com.liferay.portal.tools.service.builder.test.model.FilterPrimaryEntry
			updateFilterPrimaryEntry(
				com.liferay.portal.tools.service.builder.test.model.
					FilterPrimaryEntry filterPrimaryEntry) {

		return _filterPrimaryEntryLocalService.updateFilterPrimaryEntry(
			filterPrimaryEntry);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _filterPrimaryEntryLocalService.getBasePersistence();
	}

	@Override
	public FilterPrimaryEntryLocalService getWrappedService() {
		return _filterPrimaryEntryLocalService;
	}

	@Override
	public void setWrappedService(
		FilterPrimaryEntryLocalService filterPrimaryEntryLocalService) {

		_filterPrimaryEntryLocalService = filterPrimaryEntryLocalService;
	}

	private FilterPrimaryEntryLocalService _filterPrimaryEntryLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-379058544