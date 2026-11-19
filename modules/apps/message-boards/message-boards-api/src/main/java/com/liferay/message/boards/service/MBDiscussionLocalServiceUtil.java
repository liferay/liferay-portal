/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service;

import com.liferay.message.boards.model.MBDiscussion;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for MBDiscussion. This utility wraps
 * <code>com.liferay.message.boards.service.impl.MBDiscussionLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see MBDiscussionLocalService
 * @generated
 */
public class MBDiscussionLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.message.boards.service.impl.MBDiscussionLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static MBDiscussion addDiscussion(
			long userId, long groupId, long classNameId, long classPK,
			long threadId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addDiscussion(
			userId, groupId, classNameId, classPK, threadId, serviceContext);
	}

	/**
	 * Adds the message boards discussion to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MBDiscussionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param mbDiscussion the message boards discussion
	 * @return the message boards discussion that was added
	 */
	public static MBDiscussion addMBDiscussion(MBDiscussion mbDiscussion) {
		return getService().addMBDiscussion(mbDiscussion);
	}

	/**
	 * Creates a new message boards discussion with the primary key. Does not add the message boards discussion to the database.
	 *
	 * @param discussionId the primary key for the new message boards discussion
	 * @return the new message boards discussion
	 */
	public static MBDiscussion createMBDiscussion(long discussionId) {
		return getService().createMBDiscussion(discussionId);
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
	 * Deletes the message boards discussion with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MBDiscussionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param discussionId the primary key of the message boards discussion
	 * @return the message boards discussion that was removed
	 * @throws PortalException if a message boards discussion with the primary key could not be found
	 */
	public static MBDiscussion deleteMBDiscussion(long discussionId)
		throws PortalException {

		return getService().deleteMBDiscussion(discussionId);
	}

	/**
	 * Deletes the message boards discussion from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MBDiscussionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param mbDiscussion the message boards discussion
	 * @return the message boards discussion that was removed
	 */
	public static MBDiscussion deleteMBDiscussion(MBDiscussion mbDiscussion) {
		return getService().deleteMBDiscussion(mbDiscussion);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBDiscussionModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBDiscussionModelImpl</code>.
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

	public static MBDiscussion fetchDiscussion(long discussionId) {
		return getService().fetchDiscussion(discussionId);
	}

	public static MBDiscussion fetchDiscussion(long classNameId, long classPK) {
		return getService().fetchDiscussion(classNameId, classPK);
	}

	public static MBDiscussion fetchDiscussion(String className, long classPK) {
		return getService().fetchDiscussion(className, classPK);
	}

	public static MBDiscussion fetchMBDiscussion(long discussionId) {
		return getService().fetchMBDiscussion(discussionId);
	}

	/**
	 * Returns the message boards discussion matching the UUID and group.
	 *
	 * @param uuid the message boards discussion's UUID
	 * @param groupId the primary key of the group
	 * @return the matching message boards discussion, or <code>null</code> if a matching message boards discussion could not be found
	 */
	public static MBDiscussion fetchMBDiscussionByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchMBDiscussionByUuidAndGroupId(uuid, groupId);
	}

	public static MBDiscussion fetchThreadDiscussion(long threadId) {
		return getService().fetchThreadDiscussion(threadId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static MBDiscussion getDiscussion(String className, long classPK)
		throws PortalException {

		return getService().getDiscussion(className, classPK);
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
	 * Returns the message boards discussion with the primary key.
	 *
	 * @param discussionId the primary key of the message boards discussion
	 * @return the message boards discussion
	 * @throws PortalException if a message boards discussion with the primary key could not be found
	 */
	public static MBDiscussion getMBDiscussion(long discussionId)
		throws PortalException {

		return getService().getMBDiscussion(discussionId);
	}

	/**
	 * Returns the message boards discussion matching the UUID and group.
	 *
	 * @param uuid the message boards discussion's UUID
	 * @param groupId the primary key of the group
	 * @return the matching message boards discussion
	 * @throws PortalException if a matching message boards discussion could not be found
	 */
	public static MBDiscussion getMBDiscussionByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getMBDiscussionByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns a range of all the message boards discussions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBDiscussionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of message boards discussions
	 * @param end the upper bound of the range of message boards discussions (not inclusive)
	 * @return the range of message boards discussions
	 */
	public static List<MBDiscussion> getMBDiscussions(int start, int end) {
		return getService().getMBDiscussions(start, end);
	}

	/**
	 * Returns all the message boards discussions matching the UUID and company.
	 *
	 * @param uuid the UUID of the message boards discussions
	 * @param companyId the primary key of the company
	 * @return the matching message boards discussions, or an empty list if no matches were found
	 */
	public static List<MBDiscussion> getMBDiscussionsByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getMBDiscussionsByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of message boards discussions matching the UUID and company.
	 *
	 * @param uuid the UUID of the message boards discussions
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of message boards discussions
	 * @param end the upper bound of the range of message boards discussions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching message boards discussions, or an empty list if no matches were found
	 */
	public static List<MBDiscussion> getMBDiscussionsByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<MBDiscussion> orderByComparator) {

		return getService().getMBDiscussionsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of message boards discussions.
	 *
	 * @return the number of message boards discussions
	 */
	public static int getMBDiscussionsCount() {
		return getService().getMBDiscussionsCount();
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

	public static MBDiscussion getThreadDiscussion(long threadId)
		throws PortalException {

		return getService().getThreadDiscussion(threadId);
	}

	public static void subscribeDiscussion(
			long userId, long groupId, String className, long classPK)
		throws PortalException {

		getService().subscribeDiscussion(userId, groupId, className, classPK);
	}

	public static void unsubscribeDiscussion(
			long userId, String className, long classPK)
		throws PortalException {

		getService().unsubscribeDiscussion(userId, className, classPK);
	}

	/**
	 * Updates the message boards discussion in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MBDiscussionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param mbDiscussion the message boards discussion
	 * @return the message boards discussion that was updated
	 */
	public static MBDiscussion updateMBDiscussion(MBDiscussion mbDiscussion) {
		return getService().updateMBDiscussion(mbDiscussion);
	}

	public static MBDiscussionLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<MBDiscussionLocalService> _serviceSnapshot =
		new Snapshot<>(
			MBDiscussionLocalServiceUtil.class, MBDiscussionLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1437353531