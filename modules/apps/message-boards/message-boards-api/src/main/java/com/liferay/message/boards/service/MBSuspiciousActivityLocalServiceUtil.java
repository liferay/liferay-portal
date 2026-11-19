/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service;

import com.liferay.message.boards.model.MBSuspiciousActivity;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for MBSuspiciousActivity. This utility wraps
 * <code>com.liferay.message.boards.service.impl.MBSuspiciousActivityLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see MBSuspiciousActivityLocalService
 * @generated
 */
public class MBSuspiciousActivityLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.message.boards.service.impl.MBSuspiciousActivityLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the message boards suspicious activity to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MBSuspiciousActivityLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param mbSuspiciousActivity the message boards suspicious activity
	 * @return the message boards suspicious activity that was added
	 */
	public static MBSuspiciousActivity addMBSuspiciousActivity(
		MBSuspiciousActivity mbSuspiciousActivity) {

		return getService().addMBSuspiciousActivity(mbSuspiciousActivity);
	}

	public static MBSuspiciousActivity addOrUpdateMessageSuspiciousActivity(
			long userId, long messageId, String reason)
		throws PortalException {

		return getService().addOrUpdateMessageSuspiciousActivity(
			userId, messageId, reason);
	}

	public static MBSuspiciousActivity addOrUpdateThreadSuspiciousActivity(
			long userId, long threadId, String reason)
		throws PortalException {

		return getService().addOrUpdateThreadSuspiciousActivity(
			userId, threadId, reason);
	}

	/**
	 * Creates a new message boards suspicious activity with the primary key. Does not add the message boards suspicious activity to the database.
	 *
	 * @param suspiciousActivityId the primary key for the new message boards suspicious activity
	 * @return the new message boards suspicious activity
	 */
	public static MBSuspiciousActivity createMBSuspiciousActivity(
		long suspiciousActivityId) {

		return getService().createMBSuspiciousActivity(suspiciousActivityId);
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
	 * Deletes the message boards suspicious activity with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MBSuspiciousActivityLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param suspiciousActivityId the primary key of the message boards suspicious activity
	 * @return the message boards suspicious activity that was removed
	 * @throws PortalException if a message boards suspicious activity with the primary key could not be found
	 */
	public static MBSuspiciousActivity deleteMBSuspiciousActivity(
			long suspiciousActivityId)
		throws PortalException {

		return getService().deleteMBSuspiciousActivity(suspiciousActivityId);
	}

	/**
	 * Deletes the message boards suspicious activity from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MBSuspiciousActivityLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param mbSuspiciousActivity the message boards suspicious activity
	 * @return the message boards suspicious activity that was removed
	 */
	public static MBSuspiciousActivity deleteMBSuspiciousActivity(
		MBSuspiciousActivity mbSuspiciousActivity) {

		return getService().deleteMBSuspiciousActivity(mbSuspiciousActivity);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static MBSuspiciousActivity deleteSuspiciousActivity(
			long suspiciousActivityId)
		throws PortalException {

		return getService().deleteSuspiciousActivity(suspiciousActivityId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBSuspiciousActivityModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBSuspiciousActivityModelImpl</code>.
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

	public static MBSuspiciousActivity fetchMBSuspiciousActivity(
		long suspiciousActivityId) {

		return getService().fetchMBSuspiciousActivity(suspiciousActivityId);
	}

	/**
	 * Returns the message boards suspicious activity matching the UUID and group.
	 *
	 * @param uuid the message boards suspicious activity's UUID
	 * @param groupId the primary key of the group
	 * @return the matching message boards suspicious activity, or <code>null</code> if a matching message boards suspicious activity could not be found
	 */
	public static MBSuspiciousActivity
		fetchMBSuspiciousActivityByUuidAndGroupId(String uuid, long groupId) {

		return getService().fetchMBSuspiciousActivityByUuidAndGroupId(
			uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
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
	 * Returns a range of all the message boards suspicious activities.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBSuspiciousActivityModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of message boards suspicious activities
	 * @param end the upper bound of the range of message boards suspicious activities (not inclusive)
	 * @return the range of message boards suspicious activities
	 */
	public static List<MBSuspiciousActivity> getMBSuspiciousActivities(
		int start, int end) {

		return getService().getMBSuspiciousActivities(start, end);
	}

	/**
	 * Returns all the message boards suspicious activities matching the UUID and company.
	 *
	 * @param uuid the UUID of the message boards suspicious activities
	 * @param companyId the primary key of the company
	 * @return the matching message boards suspicious activities, or an empty list if no matches were found
	 */
	public static List<MBSuspiciousActivity>
		getMBSuspiciousActivitiesByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().getMBSuspiciousActivitiesByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of message boards suspicious activities matching the UUID and company.
	 *
	 * @param uuid the UUID of the message boards suspicious activities
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of message boards suspicious activities
	 * @param end the upper bound of the range of message boards suspicious activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching message boards suspicious activities, or an empty list if no matches were found
	 */
	public static List<MBSuspiciousActivity>
		getMBSuspiciousActivitiesByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<MBSuspiciousActivity> orderByComparator) {

		return getService().getMBSuspiciousActivitiesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of message boards suspicious activities.
	 *
	 * @return the number of message boards suspicious activities
	 */
	public static int getMBSuspiciousActivitiesCount() {
		return getService().getMBSuspiciousActivitiesCount();
	}

	/**
	 * Returns the message boards suspicious activity with the primary key.
	 *
	 * @param suspiciousActivityId the primary key of the message boards suspicious activity
	 * @return the message boards suspicious activity
	 * @throws PortalException if a message boards suspicious activity with the primary key could not be found
	 */
	public static MBSuspiciousActivity getMBSuspiciousActivity(
			long suspiciousActivityId)
		throws PortalException {

		return getService().getMBSuspiciousActivity(suspiciousActivityId);
	}

	/**
	 * Returns the message boards suspicious activity matching the UUID and group.
	 *
	 * @param uuid the message boards suspicious activity's UUID
	 * @param groupId the primary key of the group
	 * @return the matching message boards suspicious activity
	 * @throws PortalException if a matching message boards suspicious activity could not be found
	 */
	public static MBSuspiciousActivity getMBSuspiciousActivityByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getMBSuspiciousActivityByUuidAndGroupId(
			uuid, groupId);
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
	 * Updates the message boards suspicious activity in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MBSuspiciousActivityLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param mbSuspiciousActivity the message boards suspicious activity
	 * @return the message boards suspicious activity that was updated
	 */
	public static MBSuspiciousActivity updateMBSuspiciousActivity(
		MBSuspiciousActivity mbSuspiciousActivity) {

		return getService().updateMBSuspiciousActivity(mbSuspiciousActivity);
	}

	public static MBSuspiciousActivity updateValidated(
			long suspiciousActivityId)
		throws PortalException {

		return getService().updateValidated(suspiciousActivityId);
	}

	public static MBSuspiciousActivityLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<MBSuspiciousActivityLocalService>
		_serviceSnapshot = new Snapshot<>(
			MBSuspiciousActivityLocalServiceUtil.class,
			MBSuspiciousActivityLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-2066520967