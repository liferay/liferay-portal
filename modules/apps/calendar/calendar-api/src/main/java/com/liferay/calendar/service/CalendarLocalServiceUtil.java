/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.service;

import com.liferay.calendar.model.Calendar;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for Calendar. This utility wraps
 * <code>com.liferay.calendar.service.impl.CalendarLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Eduardo Lundgren
 * @see CalendarLocalService
 * @generated
 */
public class CalendarLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.calendar.service.impl.CalendarLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

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
	public static Calendar addCalendar(Calendar calendar) {
		return getService().addCalendar(calendar);
	}

	public static Calendar addCalendar(
			long userId, long groupId, long calendarResourceId,
			Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap, String timeZoneId,
			int color, boolean defaultCalendar, boolean enableComments,
			boolean enableRatings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCalendar(
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
	public static Calendar createCalendar(long calendarId) {
		return getService().createCalendar(calendarId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
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
	public static Calendar deleteCalendar(Calendar calendar)
		throws PortalException {

		return getService().deleteCalendar(calendar);
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
	public static Calendar deleteCalendar(long calendarId)
		throws PortalException {

		return getService().deleteCalendar(calendarId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static <T> T dslQuery(DSLQuery dslQuery) {
		return getService().dslQuery(dslQuery);
	}

	public static int dslQueryCount(DSLQuery dslQuery) {
		return getService().dslQueryCount(dslQuery);
	}

	public static DynamicQuery dynamicQuery() {
		return getService().dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	public static <T> List<T> dynamicQuery(DynamicQuery dynamicQuery) {
		return getService().dynamicQuery(dynamicQuery);
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
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getService().dynamicQuery(dynamicQuery, start, end);
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
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator) {

		return getService().dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(DynamicQuery dynamicQuery) {
		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	public static String exportCalendar(long calendarId, String type)
		throws Exception {

		return getService().exportCalendar(calendarId, type);
	}

	public static Calendar fetchCalendar(long calendarId) {
		return getService().fetchCalendar(calendarId);
	}

	/**
	 * Returns the calendar matching the UUID and group.
	 *
	 * @param uuid the calendar's UUID
	 * @param groupId the primary key of the group
	 * @return the matching calendar, or <code>null</code> if a matching calendar could not be found
	 */
	public static Calendar fetchCalendarByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchCalendarByUuidAndGroupId(uuid, groupId);
	}

	public static Calendar fetchGroupCalendar(
		long companyId, long groupId, String name) {

		return getService().fetchGroupCalendar(companyId, groupId, name);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the calendar with the primary key.
	 *
	 * @param calendarId the primary key of the calendar
	 * @return the calendar
	 * @throws PortalException if a calendar with the primary key could not be found
	 */
	public static Calendar getCalendar(long calendarId) throws PortalException {
		return getService().getCalendar(calendarId);
	}

	/**
	 * Returns the calendar matching the UUID and group.
	 *
	 * @param uuid the calendar's UUID
	 * @param groupId the primary key of the group
	 * @return the matching calendar
	 * @throws PortalException if a matching calendar could not be found
	 */
	public static Calendar getCalendarByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getCalendarByUuidAndGroupId(uuid, groupId);
	}

	public static List<Calendar> getCalendarResourceCalendars(
		long groupId, long calendarResourceId) {

		return getService().getCalendarResourceCalendars(
			groupId, calendarResourceId);
	}

	public static List<Calendar> getCalendarResourceCalendars(
		long groupId, long calendarResourceId, boolean defaultCalendar) {

		return getService().getCalendarResourceCalendars(
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
	public static List<Calendar> getCalendars(int start, int end) {
		return getService().getCalendars(start, end);
	}

	/**
	 * Returns all the calendars matching the UUID and company.
	 *
	 * @param uuid the UUID of the calendars
	 * @param companyId the primary key of the company
	 * @return the matching calendars, or an empty list if no matches were found
	 */
	public static List<Calendar> getCalendarsByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getCalendarsByUuidAndCompanyId(uuid, companyId);
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
	public static List<Calendar> getCalendarsByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<Calendar> orderByComparator) {

		return getService().getCalendarsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of calendars.
	 *
	 * @return the number of calendars
	 */
	public static int getCalendarsCount() {
		return getService().getCalendarsCount();
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	public static boolean hasStagingCalendar(Calendar calendar) {
		return getService().hasStagingCalendar(calendar);
	}

	public static void importCalendar(long calendarId, String data, String type)
		throws Exception {

		getService().importCalendar(calendarId, data, type);
	}

	public static boolean isStagingCalendar(Calendar calendar) {
		return getService().isStagingCalendar(calendar);
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
	public static Calendar updateCalendar(Calendar calendar) {
		return getService().updateCalendar(calendar);
	}

	public static void updateCalendar(long calendarId, boolean defaultCalendar)
		throws PortalException {

		getService().updateCalendar(calendarId, defaultCalendar);
	}

	public static Calendar updateCalendar(
			long calendarId, Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap, int color,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCalendar(
			calendarId, nameMap, descriptionMap, color, serviceContext);
	}

	public static Calendar updateCalendar(
			long calendarId, Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap, String timeZoneId,
			int color, boolean defaultCalendar, boolean enableComments,
			boolean enableRatings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCalendar(
			calendarId, nameMap, descriptionMap, timeZoneId, color,
			defaultCalendar, enableComments, enableRatings, serviceContext);
	}

	public static Calendar updateColor(
			long calendarId, int color,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateColor(calendarId, color, serviceContext);
	}

	public static CalendarLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CalendarLocalService> _serviceSnapshot =
		new Snapshot<>(
			CalendarLocalServiceUtil.class, CalendarLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-57361511