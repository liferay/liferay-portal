/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.announcements.kernel.service;

import com.liferay.announcements.kernel.model.AnnouncementsEntry;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link AnnouncementsEntryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see AnnouncementsEntryLocalService
 * @generated
 */
public class AnnouncementsEntryLocalServiceWrapper
	implements AnnouncementsEntryLocalService,
			   ServiceWrapper<AnnouncementsEntryLocalService> {

	public AnnouncementsEntryLocalServiceWrapper() {
		this(null);
	}

	public AnnouncementsEntryLocalServiceWrapper(
		AnnouncementsEntryLocalService announcementsEntryLocalService) {

		_announcementsEntryLocalService = announcementsEntryLocalService;
	}

	/**
	 * Adds the announcements entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AnnouncementsEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param announcementsEntry the announcements entry
	 * @return the announcements entry that was added
	 */
	@Override
	public AnnouncementsEntry addAnnouncementsEntry(
		AnnouncementsEntry announcementsEntry) {

		return _announcementsEntryLocalService.addAnnouncementsEntry(
			announcementsEntry);
	}

	@Override
	public AnnouncementsEntry addEntry(
			long userId, long classNameId, long classPK, String title,
			String content, String url, String type, java.util.Date displayDate,
			java.util.Date expirationDate, int priority, boolean alert)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _announcementsEntryLocalService.addEntry(
			userId, classNameId, classPK, title, content, url, type,
			displayDate, expirationDate, priority, alert);
	}

	@Override
	public void checkEntries(java.util.Date startDate, java.util.Date endDate)
		throws com.liferay.portal.kernel.exception.PortalException {

		_announcementsEntryLocalService.checkEntries(startDate, endDate);
	}

	/**
	 * Creates a new announcements entry with the primary key. Does not add the announcements entry to the database.
	 *
	 * @param entryId the primary key for the new announcements entry
	 * @return the new announcements entry
	 */
	@Override
	public AnnouncementsEntry createAnnouncementsEntry(long entryId) {
		return _announcementsEntryLocalService.createAnnouncementsEntry(
			entryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _announcementsEntryLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the announcements entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AnnouncementsEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param announcementsEntry the announcements entry
	 * @return the announcements entry that was removed
	 */
	@Override
	public AnnouncementsEntry deleteAnnouncementsEntry(
		AnnouncementsEntry announcementsEntry) {

		return _announcementsEntryLocalService.deleteAnnouncementsEntry(
			announcementsEntry);
	}

	/**
	 * Deletes the announcements entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AnnouncementsEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param entryId the primary key of the announcements entry
	 * @return the announcements entry that was removed
	 * @throws PortalException if a announcements entry with the primary key could not be found
	 */
	@Override
	public AnnouncementsEntry deleteAnnouncementsEntry(long entryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _announcementsEntryLocalService.deleteAnnouncementsEntry(
			entryId);
	}

	@Override
	public void deleteEntries(long companyId) {
		_announcementsEntryLocalService.deleteEntries(companyId);
	}

	@Override
	public void deleteEntries(long classNameId, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		_announcementsEntryLocalService.deleteEntries(classNameId, classPK);
	}

	@Override
	public void deleteEntry(AnnouncementsEntry entry)
		throws com.liferay.portal.kernel.exception.PortalException {

		_announcementsEntryLocalService.deleteEntry(entry);
	}

	@Override
	public void deleteEntry(long entryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_announcementsEntryLocalService.deleteEntry(entryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _announcementsEntryLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _announcementsEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _announcementsEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _announcementsEntryLocalService.dynamicQuery();
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

		return _announcementsEntryLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.announcements.model.impl.AnnouncementsEntryModelImpl</code>.
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

		return _announcementsEntryLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.announcements.model.impl.AnnouncementsEntryModelImpl</code>.
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

		return _announcementsEntryLocalService.dynamicQuery(
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

		return _announcementsEntryLocalService.dynamicQueryCount(dynamicQuery);
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

		return _announcementsEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public AnnouncementsEntry fetchAnnouncementsEntry(long entryId) {
		return _announcementsEntryLocalService.fetchAnnouncementsEntry(entryId);
	}

	/**
	 * Returns the announcements entry with the matching UUID and company.
	 *
	 * @param uuid the announcements entry's UUID
	 * @param companyId the primary key of the company
	 * @return the matching announcements entry, or <code>null</code> if a matching announcements entry could not be found
	 */
	@Override
	public AnnouncementsEntry fetchAnnouncementsEntryByUuidAndCompanyId(
		String uuid, long companyId) {

		return _announcementsEntryLocalService.
			fetchAnnouncementsEntryByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _announcementsEntryLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the announcements entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.announcements.model.impl.AnnouncementsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of announcements entries
	 * @param end the upper bound of the range of announcements entries (not inclusive)
	 * @return the range of announcements entries
	 */
	@Override
	public java.util.List<AnnouncementsEntry> getAnnouncementsEntries(
		int start, int end) {

		return _announcementsEntryLocalService.getAnnouncementsEntries(
			start, end);
	}

	/**
	 * Returns the number of announcements entries.
	 *
	 * @return the number of announcements entries
	 */
	@Override
	public int getAnnouncementsEntriesCount() {
		return _announcementsEntryLocalService.getAnnouncementsEntriesCount();
	}

	/**
	 * Returns the announcements entry with the primary key.
	 *
	 * @param entryId the primary key of the announcements entry
	 * @return the announcements entry
	 * @throws PortalException if a announcements entry with the primary key could not be found
	 */
	@Override
	public AnnouncementsEntry getAnnouncementsEntry(long entryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _announcementsEntryLocalService.getAnnouncementsEntry(entryId);
	}

	/**
	 * Returns the announcements entry with the matching UUID and company.
	 *
	 * @param uuid the announcements entry's UUID
	 * @param companyId the primary key of the company
	 * @return the matching announcements entry
	 * @throws PortalException if a matching announcements entry could not be found
	 */
	@Override
	public AnnouncementsEntry getAnnouncementsEntryByUuidAndCompanyId(
			String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _announcementsEntryLocalService.
			getAnnouncementsEntryByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public java.util.List<AnnouncementsEntry> getEntries(
		long userId, java.util.LinkedHashMap<Long, long[]> scopes,
		boolean alert, int flagValue, int start, int end) {

		return _announcementsEntryLocalService.getEntries(
			userId, scopes, alert, flagValue, start, end);
	}

	@Override
	public java.util.List<AnnouncementsEntry> getEntries(
		long userId, java.util.LinkedHashMap<Long, long[]> scopes,
		int displayDateMonth, int displayDateDay, int displayDateYear,
		int displayDateHour, int displayDateMinute, int expirationDateMonth,
		int expirationDateDay, int expirationDateYear, int expirationDateHour,
		int expirationDateMinute, boolean alert, int flagValue, int start,
		int end) {

		return _announcementsEntryLocalService.getEntries(
			userId, scopes, displayDateMonth, displayDateDay, displayDateYear,
			displayDateHour, displayDateMinute, expirationDateMonth,
			expirationDateDay, expirationDateYear, expirationDateHour,
			expirationDateMinute, alert, flagValue, start, end);
	}

	@Override
	public java.util.List<AnnouncementsEntry> getEntries(
		long companyId, long classNameId, long classPK, boolean alert,
		int start, int end) {

		return _announcementsEntryLocalService.getEntries(
			companyId, classNameId, classPK, alert, start, end);
	}

	@Override
	public java.util.List<AnnouncementsEntry> getEntries(
		long companyId, long classNameId, long classPK, boolean alert,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AnnouncementsEntry>
			orderByComparator) {

		return _announcementsEntryLocalService.getEntries(
			companyId, classNameId, classPK, alert, start, end,
			orderByComparator);
	}

	@Override
	public int getEntriesCount(
		long userId, java.util.LinkedHashMap<Long, long[]> scopes,
		boolean alert, int flagValue) {

		return _announcementsEntryLocalService.getEntriesCount(
			userId, scopes, alert, flagValue);
	}

	@Override
	public int getEntriesCount(
		long userId, java.util.LinkedHashMap<Long, long[]> scopes,
		int displayDateMonth, int displayDateDay, int displayDateYear,
		int displayDateHour, int displayDateMinute, int expirationDateMonth,
		int expirationDateDay, int expirationDateYear, int expirationDateHour,
		int expirationDateMinute, boolean alert, int flagValue) {

		return _announcementsEntryLocalService.getEntriesCount(
			userId, scopes, displayDateMonth, displayDateDay, displayDateYear,
			displayDateHour, displayDateMinute, expirationDateMonth,
			expirationDateDay, expirationDateYear, expirationDateHour,
			expirationDateMinute, alert, flagValue);
	}

	@Override
	public int getEntriesCount(
		long companyId, long classNameId, long classPK, boolean alert) {

		return _announcementsEntryLocalService.getEntriesCount(
			companyId, classNameId, classPK, alert);
	}

	@Override
	public AnnouncementsEntry getEntry(long entryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _announcementsEntryLocalService.getEntry(entryId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _announcementsEntryLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _announcementsEntryLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _announcementsEntryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _announcementsEntryLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public java.util.List<AnnouncementsEntry> getUserEntries(
		long userId, int start, int end) {

		return _announcementsEntryLocalService.getUserEntries(
			userId, start, end);
	}

	@Override
	public int getUserEntriesCount(long userId) {
		return _announcementsEntryLocalService.getUserEntriesCount(userId);
	}

	/**
	 * Updates the announcements entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AnnouncementsEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param announcementsEntry the announcements entry
	 * @return the announcements entry that was updated
	 */
	@Override
	public AnnouncementsEntry updateAnnouncementsEntry(
		AnnouncementsEntry announcementsEntry) {

		return _announcementsEntryLocalService.updateAnnouncementsEntry(
			announcementsEntry);
	}

	@Override
	public AnnouncementsEntry updateEntry(
			long entryId, String title, String content, String url, String type,
			java.util.Date displayDate, java.util.Date expirationDate,
			int priority)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _announcementsEntryLocalService.updateEntry(
			entryId, title, content, url, type, displayDate, expirationDate,
			priority);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _announcementsEntryLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<AnnouncementsEntry> getCTPersistence() {
		return _announcementsEntryLocalService.getCTPersistence();
	}

	@Override
	public Class<AnnouncementsEntry> getModelClass() {
		return _announcementsEntryLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<AnnouncementsEntry>, R, E>
				updateUnsafeFunction)
		throws E {

		return _announcementsEntryLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public AnnouncementsEntryLocalService getWrappedService() {
		return _announcementsEntryLocalService;
	}

	@Override
	public void setWrappedService(
		AnnouncementsEntryLocalService announcementsEntryLocalService) {

		_announcementsEntryLocalService = announcementsEntryLocalService;
	}

	private AnnouncementsEntryLocalService _announcementsEntryLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:640553938