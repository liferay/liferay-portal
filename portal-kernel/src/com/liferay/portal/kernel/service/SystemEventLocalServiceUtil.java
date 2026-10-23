/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.SystemEvent;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for SystemEvent. This utility wraps
 * <code>com.liferay.portal.service.impl.SystemEventLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see SystemEventLocalService
 * @generated
 */
public class SystemEventLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.service.impl.SystemEventLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static SystemEvent addSystemEvent(
			long userId, long groupId, String classExternalReferenceCode,
			String className, long classPK, String classUuid,
			String referrerClassName, int type, String extraData)
		throws PortalException {

		return getService().addSystemEvent(
			userId, groupId, classExternalReferenceCode, className, classPK,
			classUuid, referrerClassName, type, extraData);
	}

	public static SystemEvent addSystemEvent(
			long companyId, String classExternalReferenceCode, String className,
			long classPK, String classUuid, String referrerClassName, int type,
			String extraData)
		throws PortalException {

		return getService().addSystemEvent(
			companyId, classExternalReferenceCode, className, classPK,
			classUuid, referrerClassName, type, extraData);
	}

	/**
	 * Adds the system event to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SystemEventLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param systemEvent the system event
	 * @return the system event that was added
	 */
	public static SystemEvent addSystemEvent(SystemEvent systemEvent) {
		return getService().addSystemEvent(systemEvent);
	}

	public static void checkSystemEvents() throws PortalException {
		getService().checkSystemEvents();
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
	 * Creates a new system event with the primary key. Does not add the system event to the database.
	 *
	 * @param systemEventId the primary key for the new system event
	 * @return the new system event
	 */
	public static SystemEvent createSystemEvent(long systemEventId) {
		return getService().createSystemEvent(systemEventId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	/**
	 * Deletes the system event with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SystemEventLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param systemEventId the primary key of the system event
	 * @return the system event that was removed
	 * @throws PortalException if a system event with the primary key could not be found
	 */
	public static SystemEvent deleteSystemEvent(long systemEventId)
		throws PortalException {

		return getService().deleteSystemEvent(systemEventId);
	}

	/**
	 * Deletes the system event from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SystemEventLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param systemEvent the system event
	 * @return the system event that was removed
	 */
	public static SystemEvent deleteSystemEvent(SystemEvent systemEvent) {
		return getService().deleteSystemEvent(systemEvent);
	}

	public static void deleteSystemEvents(long groupId) {
		getService().deleteSystemEvents(groupId);
	}

	public static void deleteSystemEvents(
		long groupId, long systemEventSetKey) {

		getService().deleteSystemEvents(groupId, systemEventSetKey);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.SystemEventModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.SystemEventModelImpl</code>.
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

	public static SystemEvent fetchSystemEvent(long systemEventId) {
		return getService().fetchSystemEvent(systemEventId);
	}

	public static SystemEvent fetchSystemEvent(
		long groupId, long classNameId, long classPK, int type) {

		return getService().fetchSystemEvent(
			groupId, classNameId, classPK, type);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
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

	/**
	 * Returns the system event with the primary key.
	 *
	 * @param systemEventId the primary key of the system event
	 * @return the system event
	 * @throws PortalException if a system event with the primary key could not be found
	 */
	public static SystemEvent getSystemEvent(long systemEventId)
		throws PortalException {

		return getService().getSystemEvent(systemEventId);
	}

	/**
	 * Returns a range of all the system events.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.SystemEventModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of system events
	 * @param end the upper bound of the range of system events (not inclusive)
	 * @return the range of system events
	 */
	public static List<SystemEvent> getSystemEvents(int start, int end) {
		return getService().getSystemEvents(start, end);
	}

	public static List<SystemEvent> getSystemEvents(
		long groupId, long classNameId, long classPK, int type) {

		return getService().getSystemEvents(
			groupId, classNameId, classPK, type);
	}

	/**
	 * Returns the number of system events.
	 *
	 * @return the number of system events
	 */
	public static int getSystemEventsCount() {
		return getService().getSystemEventsCount();
	}

	/**
	 * Updates the system event in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SystemEventLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param systemEvent the system event
	 * @return the system event that was updated
	 */
	public static SystemEvent updateSystemEvent(SystemEvent systemEvent) {
		return getService().updateSystemEvent(systemEvent);
	}

	public static boolean validateGroup(long groupId) throws PortalException {
		return getService().validateGroup(groupId);
	}

	public static SystemEventLocalService getService() {
		return _service;
	}

	public static void setService(SystemEventLocalService service) {
		_service = service;
	}

	private static volatile SystemEventLocalService _service;

}
// LIFERAY-SERVICE-BUILDER-HASH:-776669980