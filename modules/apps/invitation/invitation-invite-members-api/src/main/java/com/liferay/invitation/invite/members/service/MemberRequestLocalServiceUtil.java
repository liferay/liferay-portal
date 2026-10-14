/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.invitation.invite.members.service;

import com.liferay.invitation.invite.members.model.MemberRequest;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for MemberRequest. This utility wraps
 * <code>com.liferay.invitation.invite.members.service.impl.MemberRequestLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see MemberRequestLocalService
 * @generated
 */
public class MemberRequestLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.invitation.invite.members.service.impl.MemberRequestLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static MemberRequest addMemberRequest(
			long userId, long groupId, long receiverUserId,
			String receiverEmailAddress, long invitedRoleId, long invitedTeamId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addMemberRequest(
			userId, groupId, receiverUserId, receiverEmailAddress,
			invitedRoleId, invitedTeamId, serviceContext);
	}

	/**
	 * Adds the member request to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MemberRequestLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param memberRequest the member request
	 * @return the member request that was added
	 */
	public static MemberRequest addMemberRequest(MemberRequest memberRequest) {
		return getService().addMemberRequest(memberRequest);
	}

	public static void addMemberRequests(
			long userId, long groupId, long[] receiverUserIds,
			long invitedRoleId, long invitedTeamId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		getService().addMemberRequests(
			userId, groupId, receiverUserIds, invitedRoleId, invitedTeamId,
			serviceContext);
	}

	public static void addMemberRequests(
			long userId, long groupId, String[] emailAddresses,
			long invitedRoleId, long invitedTeamId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		getService().addMemberRequests(
			userId, groupId, emailAddresses, invitedRoleId, invitedTeamId,
			serviceContext);
	}

	/**
	 * Creates a new member request with the primary key. Does not add the member request to the database.
	 *
	 * @param memberRequestId the primary key for the new member request
	 * @return the new member request
	 */
	public static MemberRequest createMemberRequest(long memberRequestId) {
		return getService().createMemberRequest(memberRequestId);
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
	 * Deletes the member request with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MemberRequestLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param memberRequestId the primary key of the member request
	 * @return the member request that was removed
	 * @throws PortalException if a member request with the primary key could not be found
	 */
	public static MemberRequest deleteMemberRequest(long memberRequestId)
		throws PortalException {

		return getService().deleteMemberRequest(memberRequestId);
	}

	/**
	 * Deletes the member request from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MemberRequestLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param memberRequest the member request
	 * @return the member request that was removed
	 */
	public static MemberRequest deleteMemberRequest(
		MemberRequest memberRequest) {

		return getService().deleteMemberRequest(memberRequest);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.invitation.invite.members.model.impl.MemberRequestModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.invitation.invite.members.model.impl.MemberRequestModelImpl</code>.
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

	public static MemberRequest fetchMemberRequest(long memberRequestId) {
		return getService().fetchMemberRequest(memberRequestId);
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
	 * Returns the member request with the primary key.
	 *
	 * @param memberRequestId the primary key of the member request
	 * @return the member request
	 * @throws PortalException if a member request with the primary key could not be found
	 */
	public static MemberRequest getMemberRequest(long memberRequestId)
		throws PortalException {

		return getService().getMemberRequest(memberRequestId);
	}

	public static MemberRequest getMemberRequest(
			long groupId, long receiverUserId, int status)
		throws PortalException {

		return getService().getMemberRequest(groupId, receiverUserId, status);
	}

	/**
	 * Returns a range of all the member requests.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.invitation.invite.members.model.impl.MemberRequestModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of member requests
	 * @param end the upper bound of the range of member requests (not inclusive)
	 * @return the range of member requests
	 */
	public static List<MemberRequest> getMemberRequests(int start, int end) {
		return getService().getMemberRequests(start, end);
	}

	/**
	 * Returns the number of member requests.
	 *
	 * @return the number of member requests
	 */
	public static int getMemberRequestsCount() {
		return getService().getMemberRequestsCount();
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

	public static List<MemberRequest> getReceiverStatusMemberRequest(
		long receiverUserId, int status, int start, int end) {

		return getService().getReceiverStatusMemberRequest(
			receiverUserId, status, start, end);
	}

	public static int getReceiverStatusMemberRequestCount(
		long receiverUserId, int status) {

		return getService().getReceiverStatusMemberRequestCount(
			receiverUserId, status);
	}

	public static boolean hasPendingMemberRequest(
		long groupId, long receiverUserId) {

		return getService().hasPendingMemberRequest(groupId, receiverUserId);
	}

	public static MemberRequest updateMemberRequest(
			long userId, long memberRequestId, int status)
		throws Exception {

		return getService().updateMemberRequest(
			userId, memberRequestId, status);
	}

	/**
	 * Updates the member request in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MemberRequestLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param memberRequest the member request
	 * @return the member request that was updated
	 */
	public static MemberRequest updateMemberRequest(
		MemberRequest memberRequest) {

		return getService().updateMemberRequest(memberRequest);
	}

	public static MemberRequest updateMemberRequest(
			String key, long receiverUserId)
		throws PortalException {

		return getService().updateMemberRequest(key, receiverUserId);
	}

	public static MemberRequestLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<MemberRequestLocalService> _serviceSnapshot =
		new Snapshot<>(
			MemberRequestLocalServiceUtil.class,
			MemberRequestLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-543090717