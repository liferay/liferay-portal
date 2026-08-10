/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link DateEntryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see DateEntryLocalService
 * @generated
 */
public class DateEntryLocalServiceWrapper
	implements DateEntryLocalService, ServiceWrapper<DateEntryLocalService> {

	public DateEntryLocalServiceWrapper() {
		this(null);
	}

	public DateEntryLocalServiceWrapper(
		DateEntryLocalService dateEntryLocalService) {

		_dateEntryLocalService = dateEntryLocalService;
	}

	/**
	 * Adds the date entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DateEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dateEntry the date entry
	 * @return the date entry that was added
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.DateEntry
		addDateEntry(
			com.liferay.portal.tools.service.builder.test.model.DateEntry
				dateEntry) {

		return _dateEntryLocalService.addDateEntry(dateEntry);
	}

	/**
	 * Creates a new date entry with the primary key. Does not add the date entry to the database.
	 *
	 * @param dateEntryId the primary key for the new date entry
	 * @return the new date entry
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.DateEntry
		createDateEntry(long dateEntryId) {

		return _dateEntryLocalService.createDateEntry(dateEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dateEntryLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the date entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DateEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dateEntry the date entry
	 * @return the date entry that was removed
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.DateEntry
		deleteDateEntry(
			com.liferay.portal.tools.service.builder.test.model.DateEntry
				dateEntry) {

		return _dateEntryLocalService.deleteDateEntry(dateEntry);
	}

	/**
	 * Deletes the date entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DateEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dateEntryId the primary key of the date entry
	 * @return the date entry that was removed
	 * @throws PortalException if a date entry with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.DateEntry
			deleteDateEntry(long dateEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dateEntryLocalService.deleteDateEntry(dateEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dateEntryLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _dateEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _dateEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _dateEntryLocalService.dynamicQuery();
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

		return _dateEntryLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.DateEntryModelImpl</code>.
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

		return _dateEntryLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.DateEntryModelImpl</code>.
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

		return _dateEntryLocalService.dynamicQuery(
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

		return _dateEntryLocalService.dynamicQueryCount(dynamicQuery);
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

		return _dateEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.model.DateEntry
		fetchDateEntry(long dateEntryId) {

		return _dateEntryLocalService.fetchDateEntry(dateEntryId);
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.model.DateEntry
		fetchDateEntry(long companyId, java.util.Date snapshotDate) {

		return _dateEntryLocalService.fetchDateEntry(companyId, snapshotDate);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _dateEntryLocalService.getActionableDynamicQuery();
	}

	@Override
	public java.util.List
		<com.liferay.portal.tools.service.builder.test.model.DateEntry>
			getDateEntries(java.util.Date snapshotDate) {

		return _dateEntryLocalService.getDateEntries(snapshotDate);
	}

	/**
	 * Returns a range of all the date entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.DateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of date entries
	 * @param end the upper bound of the range of date entries (not inclusive)
	 * @return the range of date entries
	 */
	@Override
	public java.util.List
		<com.liferay.portal.tools.service.builder.test.model.DateEntry>
			getDateEntries(int start, int end) {

		return _dateEntryLocalService.getDateEntries(start, end);
	}

	@Override
	public java.util.List<Object[]> getDateEntriesBySQLQuery(
		long companyId, com.liferay.portal.kernel.dao.orm.Type type) {

		return _dateEntryLocalService.getDateEntriesBySQLQuery(companyId, type);
	}

	/**
	 * Returns the number of date entries.
	 *
	 * @return the number of date entries
	 */
	@Override
	public int getDateEntriesCount() {
		return _dateEntryLocalService.getDateEntriesCount();
	}

	/**
	 * Returns the date entry with the primary key.
	 *
	 * @param dateEntryId the primary key of the date entry
	 * @return the date entry
	 * @throws PortalException if a date entry with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.DateEntry
			getDateEntry(long dateEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dateEntryLocalService.getDateEntry(dateEntryId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _dateEntryLocalService.getIndexableActionableDynamicQuery();
	}

	@Override
	public java.util.List<Object> getMaxSnapshotDatesBySQLQuery(
		long companyId, com.liferay.portal.kernel.dao.orm.Type type) {

		return _dateEntryLocalService.getMaxSnapshotDatesBySQLQuery(
			companyId, type);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _dateEntryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dateEntryLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the date entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DateEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dateEntry the date entry
	 * @return the date entry that was updated
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.DateEntry
		updateDateEntry(
			com.liferay.portal.tools.service.builder.test.model.DateEntry
				dateEntry) {

		return _dateEntryLocalService.updateDateEntry(dateEntry);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _dateEntryLocalService.getBasePersistence();
	}

	@Override
	public DateEntryLocalService getWrappedService() {
		return _dateEntryLocalService;
	}

	@Override
	public void setWrappedService(DateEntryLocalService dateEntryLocalService) {
		_dateEntryLocalService = dateEntryLocalService;
	}

	private DateEntryLocalService _dateEntryLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:860037901