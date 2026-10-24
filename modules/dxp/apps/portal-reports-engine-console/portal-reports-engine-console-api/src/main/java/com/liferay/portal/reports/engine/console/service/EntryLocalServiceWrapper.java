/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine.console.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link EntryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see EntryLocalService
 * @generated
 */
public class EntryLocalServiceWrapper
	implements EntryLocalService, ServiceWrapper<EntryLocalService> {

	public EntryLocalServiceWrapper() {
		this(null);
	}

	public EntryLocalServiceWrapper(EntryLocalService entryLocalService) {
		_entryLocalService = entryLocalService;
	}

	/**
	 * Adds the entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect EntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param entry the entry
	 * @return the entry that was added
	 */
	@Override
	public com.liferay.portal.reports.engine.console.model.Entry addEntry(
		com.liferay.portal.reports.engine.console.model.Entry entry) {

		return _entryLocalService.addEntry(entry);
	}

	@Override
	public com.liferay.portal.reports.engine.console.model.Entry addEntry(
			long userId, long groupId, long definitionId, String format,
			boolean schedulerRequest, java.util.Date startDate,
			java.util.Date endDate, boolean repeating, String recurrence,
			String emailNotifications, String emailDelivery, String portletId,
			String pageURL, String reportName, String reportParameters,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _entryLocalService.addEntry(
			userId, groupId, definitionId, format, schedulerRequest, startDate,
			endDate, repeating, recurrence, emailNotifications, emailDelivery,
			portletId, pageURL, reportName, reportParameters, serviceContext);
	}

	@Override
	public void addEntryResources(
			com.liferay.portal.reports.engine.console.model.Entry entry,
			boolean addCommunityPermissions, boolean addGuestPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_entryLocalService.addEntryResources(
			entry, addCommunityPermissions, addGuestPermissions);
	}

	@Override
	public void addEntryResources(
			com.liferay.portal.reports.engine.console.model.Entry entry,
			String[] communityPermissions, String[] guestPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_entryLocalService.addEntryResources(
			entry, communityPermissions, guestPermissions);
	}

	/**
	 * Creates a new entry with the primary key. Does not add the entry to the database.
	 *
	 * @param entryId the primary key for the new entry
	 * @return the new entry
	 */
	@Override
	public com.liferay.portal.reports.engine.console.model.Entry createEntry(
		long entryId) {

		return _entryLocalService.createEntry(entryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _entryLocalService.createPersistedModel(primaryKeyObj);
	}

	@Override
	public void deleteAttachment(long companyId, String fileName)
		throws com.liferay.portal.kernel.exception.PortalException {

		_entryLocalService.deleteAttachment(companyId, fileName);
	}

	/**
	 * Deletes the entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect EntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param entry the entry
	 * @return the entry that was removed
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.reports.engine.console.model.Entry deleteEntry(
			com.liferay.portal.reports.engine.console.model.Entry entry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _entryLocalService.deleteEntry(entry);
	}

	/**
	 * Deletes the entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect EntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param entryId the primary key of the entry
	 * @return the entry that was removed
	 * @throws PortalException if a entry with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.reports.engine.console.model.Entry deleteEntry(
			long entryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _entryLocalService.deleteEntry(entryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _entryLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _entryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _entryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _entryLocalService.dynamicQuery();
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

		return _entryLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.reports.engine.console.model.impl.EntryModelImpl</code>.
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

		return _entryLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.reports.engine.console.model.impl.EntryModelImpl</code>.
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

		return _entryLocalService.dynamicQuery(
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

		return _entryLocalService.dynamicQueryCount(dynamicQuery);
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

		return _entryLocalService.dynamicQueryCount(dynamicQuery, projection);
	}

	@Override
	public com.liferay.portal.reports.engine.console.model.Entry fetchEntry(
		long entryId) {

		return _entryLocalService.fetchEntry(entryId);
	}

	@Override
	public void generateReport(long entryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_entryLocalService.generateReport(entryId);
	}

	@Override
	public void generateReport(long entryId, String reportName)
		throws com.liferay.portal.kernel.exception.PortalException {

		_entryLocalService.generateReport(entryId, reportName);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _entryLocalService.getActionableDynamicQuery();
	}

	@Override
	public String[] getAttachmentsFileNames(
		com.liferay.portal.reports.engine.console.model.Entry entry) {

		return _entryLocalService.getAttachmentsFileNames(entry);
	}

	/**
	 * Returns a range of all the entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.reports.engine.console.model.impl.EntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of entries
	 * @param end the upper bound of the range of entries (not inclusive)
	 * @return the range of entries
	 */
	@Override
	public java.util.List<com.liferay.portal.reports.engine.console.model.Entry>
		getEntries(int start, int end) {

		return _entryLocalService.getEntries(start, end);
	}

	/**
	 * Returns the number of entries.
	 *
	 * @return the number of entries
	 */
	@Override
	public int getEntriesCount() {
		return _entryLocalService.getEntriesCount();
	}

	/**
	 * Returns the entry with the primary key.
	 *
	 * @param entryId the primary key of the entry
	 * @return the entry
	 * @throws PortalException if a entry with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.reports.engine.console.model.Entry getEntry(
			long entryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _entryLocalService.getEntry(entryId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _entryLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _entryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _entryLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public void sendEmails(
			long entryId, String fileName, String[] emailAddresses,
			boolean notification)
		throws com.liferay.portal.kernel.exception.PortalException {

		_entryLocalService.sendEmails(
			entryId, fileName, emailAddresses, notification);
	}

	@Override
	public void unscheduleEntry(long entryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_entryLocalService.unscheduleEntry(entryId);
	}

	/**
	 * Updates the entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect EntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param entry the entry
	 * @return the entry that was updated
	 */
	@Override
	public com.liferay.portal.reports.engine.console.model.Entry updateEntry(
		com.liferay.portal.reports.engine.console.model.Entry entry) {

		return _entryLocalService.updateEntry(entry);
	}

	@Override
	public void updateEntry(
			long entryId, String reportName, byte[] reportResults)
		throws com.liferay.portal.kernel.exception.PortalException {

		_entryLocalService.updateEntry(entryId, reportName, reportResults);
	}

	@Override
	public void updateEntryStatus(
			long entryId,
			com.liferay.portal.reports.engine.console.status.ReportStatus
				status,
			String errorMessage)
		throws com.liferay.portal.kernel.exception.PortalException {

		_entryLocalService.updateEntryStatus(entryId, status, errorMessage);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _entryLocalService.getBasePersistence();
	}

	@Override
	public EntryLocalService getWrappedService() {
		return _entryLocalService;
	}

	@Override
	public void setWrappedService(EntryLocalService entryLocalService) {
		_entryLocalService = entryLocalService;
	}

	private EntryLocalService _entryLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:931924345