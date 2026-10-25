/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.service;

import com.liferay.calendar.model.Calendar;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link CalendarLocalService}.
 *
 * @author Eduardo Lundgren
 * @see CalendarLocalService
 * @generated
 */
public class CalendarLocalServiceWrapper
	implements CalendarLocalService, ServiceWrapper<CalendarLocalService> {

	public CalendarLocalServiceWrapper() {
		this(null);
	}

	public CalendarLocalServiceWrapper(
		CalendarLocalService calendarLocalService) {

		_calendarLocalService = calendarLocalService;
	}

	/**
	 * Adds the calendar to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CalendarLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param calendar the calendar
	 * @return the calendar that was added
	 */
	@Override
	public Calendar addCalendar(Calendar calendar) {
		return _calendarLocalService.addCalendar(calendar);
	}

	@Override
	public Calendar addCalendar(
			long userId, long groupId, long calendarResourceId,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String timeZoneId, int color, boolean defaultCalendar,
			boolean enableComments, boolean enableRatings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarLocalService.addCalendar(
			userId, groupId, calendarResourceId, nameMap, descriptionMap,
			timeZoneId, color, defaultCalendar, enableComments, enableRatings,
			serviceContext);
	}

	/**
	 * Creates a new calendar with the primary key. Does not add the calendar to the database.
	 *
	 * @param calendarId the primary key for the new calendar
	 * @return the new calendar
	 */
	@Override
	public Calendar createCalendar(long calendarId) {
		return _calendarLocalService.createCalendar(calendarId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the calendar from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CalendarLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param calendar the calendar
	 * @return the calendar that was removed
	 * @throws PortalException
	 */
	@Override
	public Calendar deleteCalendar(Calendar calendar)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarLocalService.deleteCalendar(calendar);
	}

	/**
	 * Deletes the calendar with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CalendarLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param calendarId the primary key of the calendar
	 * @return the calendar that was removed
	 * @throws PortalException if a calendar with the primary key could not be found
	 */
	@Override
	public Calendar deleteCalendar(long calendarId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarLocalService.deleteCalendar(calendarId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _calendarLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _calendarLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _calendarLocalService.dynamicQuery();
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

		return _calendarLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.calendar.model.impl.CalendarModelImpl</code>.
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

		return _calendarLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.calendar.model.impl.CalendarModelImpl</code>.
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

		return _calendarLocalService.dynamicQuery(
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

		return _calendarLocalService.dynamicQueryCount(dynamicQuery);
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

		return _calendarLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public String exportCalendar(long calendarId, String type)
		throws Exception {

		return _calendarLocalService.exportCalendar(calendarId, type);
	}

	@Override
	public Calendar fetchCalendar(long calendarId) {
		return _calendarLocalService.fetchCalendar(calendarId);
	}

	/**
	 * Returns the calendar matching the UUID and group.
	 *
	 * @param uuid the calendar's UUID
	 * @param groupId the primary key of the group
	 * @return the matching calendar, or <code>null</code> if a matching calendar could not be found
	 */
	@Override
	public Calendar fetchCalendarByUuidAndGroupId(String uuid, long groupId) {
		return _calendarLocalService.fetchCalendarByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public Calendar fetchGroupCalendar(
		long companyId, long groupId, String name) {

		return _calendarLocalService.fetchGroupCalendar(
			companyId, groupId, name);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _calendarLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the calendar with the primary key.
	 *
	 * @param calendarId the primary key of the calendar
	 * @return the calendar
	 * @throws PortalException if a calendar with the primary key could not be found
	 */
	@Override
	public Calendar getCalendar(long calendarId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarLocalService.getCalendar(calendarId);
	}

	/**
	 * Returns the calendar matching the UUID and group.
	 *
	 * @param uuid the calendar's UUID
	 * @param groupId the primary key of the group
	 * @return the matching calendar
	 * @throws PortalException if a matching calendar could not be found
	 */
	@Override
	public Calendar getCalendarByUuidAndGroupId(String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarLocalService.getCalendarByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public java.util.List<Calendar> getCalendarResourceCalendars(
		long groupId, long calendarResourceId) {

		return _calendarLocalService.getCalendarResourceCalendars(
			groupId, calendarResourceId);
	}

	@Override
	public java.util.List<Calendar> getCalendarResourceCalendars(
		long groupId, long calendarResourceId, boolean defaultCalendar) {

		return _calendarLocalService.getCalendarResourceCalendars(
			groupId, calendarResourceId, defaultCalendar);
	}

	/**
	 * Returns a range of all the calendars.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.calendar.model.impl.CalendarModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of calendars
	 * @param end the upper bound of the range of calendars (not inclusive)
	 * @return the range of calendars
	 */
	@Override
	public java.util.List<Calendar> getCalendars(int start, int end) {
		return _calendarLocalService.getCalendars(start, end);
	}

	/**
	 * Returns all the calendars matching the UUID and company.
	 *
	 * @param uuid the UUID of the calendars
	 * @param companyId the primary key of the company
	 * @return the matching calendars, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<Calendar> getCalendarsByUuidAndCompanyId(
		String uuid, long companyId) {

		return _calendarLocalService.getCalendarsByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of calendars matching the UUID and company.
	 *
	 * @param uuid the UUID of the calendars
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of calendars
	 * @param end the upper bound of the range of calendars (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching calendars, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<Calendar> getCalendarsByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Calendar>
			orderByComparator) {

		return _calendarLocalService.getCalendarsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of calendars.
	 *
	 * @return the number of calendars
	 */
	@Override
	public int getCalendarsCount() {
		return _calendarLocalService.getCalendarsCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _calendarLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _calendarLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _calendarLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public boolean hasStagingCalendar(Calendar calendar) {
		return _calendarLocalService.hasStagingCalendar(calendar);
	}

	@Override
	public void importCalendar(long calendarId, String data, String type)
		throws Exception {

		_calendarLocalService.importCalendar(calendarId, data, type);
	}

	@Override
	public boolean isStagingCalendar(Calendar calendar) {
		return _calendarLocalService.isStagingCalendar(calendar);
	}

	/**
	 * Updates the calendar in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CalendarLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param calendar the calendar
	 * @return the calendar that was updated
	 */
	@Override
	public Calendar updateCalendar(Calendar calendar) {
		return _calendarLocalService.updateCalendar(calendar);
	}

	@Override
	public void updateCalendar(long calendarId, boolean defaultCalendar)
		throws com.liferay.portal.kernel.exception.PortalException {

		_calendarLocalService.updateCalendar(calendarId, defaultCalendar);
	}

	@Override
	public Calendar updateCalendar(
			long calendarId, java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap, int color,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarLocalService.updateCalendar(
			calendarId, nameMap, descriptionMap, color, serviceContext);
	}

	@Override
	public Calendar updateCalendar(
			long calendarId, java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String timeZoneId, int color, boolean defaultCalendar,
			boolean enableComments, boolean enableRatings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarLocalService.updateCalendar(
			calendarId, nameMap, descriptionMap, timeZoneId, color,
			defaultCalendar, enableComments, enableRatings, serviceContext);
	}

	@Override
	public Calendar updateColor(
			long calendarId, int color,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarLocalService.updateColor(
			calendarId, color, serviceContext);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _calendarLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<Calendar> getCTPersistence() {
		return _calendarLocalService.getCTPersistence();
	}

	@Override
	public Class<Calendar> getModelClass() {
		return _calendarLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<Calendar>, R, E> updateUnsafeFunction)
		throws E {

		return _calendarLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public CalendarLocalService getWrappedService() {
		return _calendarLocalService;
	}

	@Override
	public void setWrappedService(CalendarLocalService calendarLocalService) {
		_calendarLocalService = calendarLocalService;
	}

	private CalendarLocalService _calendarLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:2076997840