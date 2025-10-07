/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.trash.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.trash.model.TrashEntry;

/**
 * Provides a wrapper for {@link TrashEntryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see TrashEntryLocalService
 * @generated
 */
public class TrashEntryLocalServiceWrapper
	implements ServiceWrapper<TrashEntryLocalService>, TrashEntryLocalService {

	public TrashEntryLocalServiceWrapper() {
		this(null);
	}

	public TrashEntryLocalServiceWrapper(
		TrashEntryLocalService trashEntryLocalService) {

		_trashEntryLocalService = trashEntryLocalService;
	}

	/**
	 * Moves an entry to trash.
	 *
	 * @param userId the primary key of the user removing the entity
	 * @param groupId the primary key of the entry's group
	 * @param className the class name of the entity
	 * @param classPK the primary key of the entity
	 * @param classUuid the UUID of the entity's class
	 * @param referrerClassName the referrer class name used to add a deletion
	 {@link SystemEvent}
	 * @param status the status of the entity prior to being moved to trash
	 * @param statusOVPs the primary keys and statuses of any of the entry's
	 versions (e.g., {@link
	 com.liferay.portlet.documentlibrary.model.DLFileVersion})
	 * @param typeSettingsUnicodeProperties the type settings properties
	 * @return the trashEntry
	 */
	@Override
	public TrashEntry addTrashEntry(
			long userId, long groupId, String className, long classPK,
			String classUuid, String referrerClassName, int status,
			java.util.List
				<com.liferay.portal.kernel.util.ObjectValuePair<Long, Integer>>
					statusOVPs,
			com.liferay.portal.kernel.util.UnicodeProperties
				typeSettingsUnicodeProperties)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _trashEntryLocalService.addTrashEntry(
			userId, groupId, className, classPK, classUuid, referrerClassName,
			status, statusOVPs, typeSettingsUnicodeProperties);
	}

	/**
	 * Adds the trash entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect TrashEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param trashEntry the trash entry
	 * @return the trash entry that was added
	 */
	@Override
	public TrashEntry addTrashEntry(TrashEntry trashEntry) {
		return _trashEntryLocalService.addTrashEntry(trashEntry);
	}

	@Override
	public void checkEntries()
		throws com.liferay.portal.kernel.exception.PortalException {

		_trashEntryLocalService.checkEntries();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _trashEntryLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Creates a new trash entry with the primary key. Does not add the trash entry to the database.
	 *
	 * @param entryId the primary key for the new trash entry
	 * @return the new trash entry
	 */
	@Override
	public TrashEntry createTrashEntry(long entryId) {
		return _trashEntryLocalService.createTrashEntry(entryId);
	}

	@Override
	public void deleteEntries(long groupId) {
		_trashEntryLocalService.deleteEntries(groupId);
	}

	@Override
	public void deleteEntries(long groupId, boolean deleteTrashedModels) {
		_trashEntryLocalService.deleteEntries(groupId, deleteTrashedModels);
	}

	/**
	 * Deletes the trash entry with the primary key.
	 *
	 * @param entryId the primary key of the trash entry
	 * @return the trash entry with the primary key
	 */
	@Override
	public TrashEntry deleteEntry(long entryId) {
		return _trashEntryLocalService.deleteEntry(entryId);
	}

	/**
	 * Deletes the trash entry with the entity class name and primary key.
	 *
	 * @param className the class name of entity
	 * @param classPK the primary key of the entry
	 * @return the trash entry with the entity class name and primary key
	 */
	@Override
	public TrashEntry deleteEntry(String className, long classPK) {
		return _trashEntryLocalService.deleteEntry(className, classPK);
	}

	@Override
	public TrashEntry deleteEntry(TrashEntry trashEntry) {
		return _trashEntryLocalService.deleteEntry(trashEntry);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _trashEntryLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public void deleteTrashEntries(long companyId, String className) {
		_trashEntryLocalService.deleteTrashEntries(companyId, className);
	}

	/**
	 * Deletes the trash entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect TrashEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param entryId the primary key of the trash entry
	 * @return the trash entry that was removed
	 * @throws PortalException if a trash entry with the primary key could not be found
	 */
	@Override
	public TrashEntry deleteTrashEntry(long entryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _trashEntryLocalService.deleteTrashEntry(entryId);
	}

	/**
	 * Deletes the trash entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect TrashEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param trashEntry the trash entry
	 * @return the trash entry that was removed
	 */
	@Override
	public TrashEntry deleteTrashEntry(TrashEntry trashEntry) {
		return _trashEntryLocalService.deleteTrashEntry(trashEntry);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _trashEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _trashEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _trashEntryLocalService.dynamicQuery();
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

		return _trashEntryLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.trash.model.impl.TrashEntryModelImpl</code>.
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

		return _trashEntryLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.trash.model.impl.TrashEntryModelImpl</code>.
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

		return _trashEntryLocalService.dynamicQuery(
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

		return _trashEntryLocalService.dynamicQueryCount(dynamicQuery);
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

		return _trashEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	/**
	 * Returns the trash entry with the primary key.
	 *
	 * @param entryId the primary key of the entry
	 * @return the trash entry with the primary key
	 */
	@Override
	public TrashEntry fetchEntry(long entryId) {
		return _trashEntryLocalService.fetchEntry(entryId);
	}

	/**
	 * Returns the trash entry with the entity class name and primary key.
	 *
	 * @param className the class name of the entity
	 * @param classPK the primary key of the entity
	 * @return the trash entry with the entity class name and primary key
	 */
	@Override
	public TrashEntry fetchEntry(String className, long classPK) {
		return _trashEntryLocalService.fetchEntry(className, classPK);
	}

	@Override
	public TrashEntry fetchTrashEntry(long entryId) {
		return _trashEntryLocalService.fetchTrashEntry(entryId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _trashEntryLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the trash entries with the matching group ID.
	 *
	 * @param groupId the primary key of the group
	 * @return the trash entries with the group ID
	 */
	@Override
	public java.util.List<TrashEntry> getEntries(long groupId) {
		return _trashEntryLocalService.getEntries(groupId);
	}

	/**
	 * Returns a range of all the trash entries matching the group ID.
	 *
	 * @param groupId the primary key of the group
	 * @param start the lower bound of the range of trash entries to return
	 * @param end the upper bound of the range of trash entries to return (not
	 inclusive)
	 * @return the range of matching trash entries
	 */
	@Override
	public java.util.List<TrashEntry> getEntries(
		long groupId, int start, int end) {

		return _trashEntryLocalService.getEntries(groupId, start, end);
	}

	/**
	 * Returns a range of all the trash entries matching the group ID.
	 *
	 * @param groupId the primary key of the group
	 * @param start the lower bound of the range of trash entries to return
	 * @param end the upper bound of the range of trash entries to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the trash entries
	 (optionally <code>null</code>)
	 * @return the range of matching trash entries ordered by comparator
	 <code>orderByComparator</code>
	 */
	@Override
	public java.util.List<TrashEntry> getEntries(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<TrashEntry>
			orderByComparator) {

		return _trashEntryLocalService.getEntries(
			groupId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<TrashEntry> getEntries(
		long groupId, String className) {

		return _trashEntryLocalService.getEntries(groupId, className);
	}

	/**
	 * Returns the number of trash entries with the group ID.
	 *
	 * @param groupId the primary key of the group
	 * @return the number of matching trash entries
	 */
	@Override
	public int getEntriesCount(long groupId) {
		return _trashEntryLocalService.getEntriesCount(groupId);
	}

	/**
	 * Returns the trash entry with the primary key.
	 *
	 * @param entryId the primary key of the trash entry
	 * @return the trash entry with the primary key
	 */
	@Override
	public TrashEntry getEntry(long entryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _trashEntryLocalService.getEntry(entryId);
	}

	/**
	 * Returns the entry with the entity class name and primary key.
	 *
	 * @param className the class name of the entity
	 * @param classPK the primary key of the entity
	 * @return the trash entry with the entity class name and primary key
	 */
	@Override
	public TrashEntry getEntry(String className, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _trashEntryLocalService.getEntry(className, classPK);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _trashEntryLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _trashEntryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _trashEntryLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns a range of all the trash entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.trash.model.impl.TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @return the range of trash entries
	 */
	@Override
	public java.util.List<TrashEntry> getTrashEntries(int start, int end) {
		return _trashEntryLocalService.getTrashEntries(start, end);
	}

	/**
	 * Returns the number of trash entries.
	 *
	 * @return the number of trash entries
	 */
	@Override
	public int getTrashEntriesCount() {
		return _trashEntryLocalService.getTrashEntriesCount();
	}

	/**
	 * Returns the trash entry with the primary key.
	 *
	 * @param entryId the primary key of the trash entry
	 * @return the trash entry
	 * @throws PortalException if a trash entry with the primary key could not be found
	 */
	@Override
	public TrashEntry getTrashEntry(long entryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _trashEntryLocalService.getTrashEntry(entryId);
	}

	@Override
	public com.liferay.portal.kernel.search.Hits search(
		long companyId, long groupId, long userId, String keywords, int start,
		int end, com.liferay.portal.kernel.search.Sort sort) {

		return _trashEntryLocalService.search(
			companyId, groupId, userId, keywords, start, end, sort);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult<TrashEntry>
		searchTrashEntries(
			long companyId, long groupId, long userId, String keywords,
			int start, int end, com.liferay.portal.kernel.search.Sort sort) {

		return _trashEntryLocalService.searchTrashEntries(
			companyId, groupId, userId, keywords, start, end, sort);
	}

	/**
	 * Updates the trash entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect TrashEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param trashEntry the trash entry
	 * @return the trash entry that was updated
	 */
	@Override
	public TrashEntry updateTrashEntry(TrashEntry trashEntry) {
		return _trashEntryLocalService.updateTrashEntry(trashEntry);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _trashEntryLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<TrashEntry> getCTPersistence() {
		return _trashEntryLocalService.getCTPersistence();
	}

	@Override
	public Class<TrashEntry> getModelClass() {
		return _trashEntryLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<TrashEntry>, R, E>
				updateUnsafeFunction)
		throws E {

		return _trashEntryLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public TrashEntryLocalService getWrappedService() {
		return _trashEntryLocalService;
	}

	@Override
	public void setWrappedService(
		TrashEntryLocalService trashEntryLocalService) {

		_trashEntryLocalService = trashEntryLocalService;
	}

	private TrashEntryLocalService _trashEntryLocalService;

}