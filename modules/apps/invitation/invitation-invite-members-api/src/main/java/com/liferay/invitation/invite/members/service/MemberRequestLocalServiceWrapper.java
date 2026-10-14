/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.invitation.invite.members.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link MemberRequestLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see MemberRequestLocalService
 * @generated
 */
public class MemberRequestLocalServiceWrapper
	implements MemberRequestLocalService,
			   ServiceWrapper<MemberRequestLocalService> {

	public MemberRequestLocalServiceWrapper() {
		this(null);
	}

	public MemberRequestLocalServiceWrapper(
		MemberRequestLocalService memberRequestLocalService) {

		_memberRequestLocalService = memberRequestLocalService;
	}

	@Override
	public com.liferay.invitation.invite.members.model.MemberRequest
			addMemberRequest(
				long userId, long groupId, long receiverUserId,
				String receiverEmailAddress, long invitedRoleId,
				long invitedTeamId,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _memberRequestLocalService.addMemberRequest(
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
	@Override
	public com.liferay.invitation.invite.members.model.MemberRequest
		addMemberRequest(
			com.liferay.invitation.invite.members.model.MemberRequest
				memberRequest) {

		return _memberRequestLocalService.addMemberRequest(memberRequest);
	}

	@Override
	public void addMemberRequests(
			long userId, long groupId, long[] receiverUserIds,
			long invitedRoleId, long invitedTeamId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_memberRequestLocalService.addMemberRequests(
			userId, groupId, receiverUserIds, invitedRoleId, invitedTeamId,
			serviceContext);
	}

	@Override
	public void addMemberRequests(
			long userId, long groupId, String[] emailAddresses,
			long invitedRoleId, long invitedTeamId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_memberRequestLocalService.addMemberRequests(
			userId, groupId, emailAddresses, invitedRoleId, invitedTeamId,
			serviceContext);
	}

	/**
	 * Creates a new member request with the primary key. Does not add the member request to the database.
	 *
	 * @param memberRequestId the primary key for the new member request
	 * @return the new member request
	 */
	@Override
	public com.liferay.invitation.invite.members.model.MemberRequest
		createMemberRequest(long memberRequestId) {

		return _memberRequestLocalService.createMemberRequest(memberRequestId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _memberRequestLocalService.createPersistedModel(primaryKeyObj);
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
	@Override
	public com.liferay.invitation.invite.members.model.MemberRequest
			deleteMemberRequest(long memberRequestId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _memberRequestLocalService.deleteMemberRequest(memberRequestId);
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
	@Override
	public com.liferay.invitation.invite.members.model.MemberRequest
		deleteMemberRequest(
			com.liferay.invitation.invite.members.model.MemberRequest
				memberRequest) {

		return _memberRequestLocalService.deleteMemberRequest(memberRequest);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _memberRequestLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _memberRequestLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _memberRequestLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _memberRequestLocalService.dynamicQuery();
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

		return _memberRequestLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _memberRequestLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _memberRequestLocalService.dynamicQuery(
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

		return _memberRequestLocalService.dynamicQueryCount(dynamicQuery);
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

		return _memberRequestLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.invitation.invite.members.model.MemberRequest
		fetchMemberRequest(long memberRequestId) {

		return _memberRequestLocalService.fetchMemberRequest(memberRequestId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _memberRequestLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _memberRequestLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the member request with the primary key.
	 *
	 * @param memberRequestId the primary key of the member request
	 * @return the member request
	 * @throws PortalException if a member request with the primary key could not be found
	 */
	@Override
	public com.liferay.invitation.invite.members.model.MemberRequest
			getMemberRequest(long memberRequestId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _memberRequestLocalService.getMemberRequest(memberRequestId);
	}

	@Override
	public com.liferay.invitation.invite.members.model.MemberRequest
			getMemberRequest(long groupId, long receiverUserId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _memberRequestLocalService.getMemberRequest(
			groupId, receiverUserId, status);
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
	@Override
	public java.util.List
		<com.liferay.invitation.invite.members.model.MemberRequest>
			getMemberRequests(int start, int end) {

		return _memberRequestLocalService.getMemberRequests(start, end);
	}

	/**
	 * Returns the number of member requests.
	 *
	 * @return the number of member requests
	 */
	@Override
	public int getMemberRequestsCount() {
		return _memberRequestLocalService.getMemberRequestsCount();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _memberRequestLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _memberRequestLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public java.util.List
		<com.liferay.invitation.invite.members.model.MemberRequest>
			getReceiverStatusMemberRequest(
				long receiverUserId, int status, int start, int end) {

		return _memberRequestLocalService.getReceiverStatusMemberRequest(
			receiverUserId, status, start, end);
	}

	@Override
	public int getReceiverStatusMemberRequestCount(
		long receiverUserId, int status) {

		return _memberRequestLocalService.getReceiverStatusMemberRequestCount(
			receiverUserId, status);
	}

	@Override
	public boolean hasPendingMemberRequest(long groupId, long receiverUserId) {
		return _memberRequestLocalService.hasPendingMemberRequest(
			groupId, receiverUserId);
	}

	@Override
	public com.liferay.invitation.invite.members.model.MemberRequest
			updateMemberRequest(long userId, long memberRequestId, int status)
		throws Exception {

		return _memberRequestLocalService.updateMemberRequest(
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
	@Override
	public com.liferay.invitation.invite.members.model.MemberRequest
		updateMemberRequest(
			com.liferay.invitation.invite.members.model.MemberRequest
				memberRequest) {

		return _memberRequestLocalService.updateMemberRequest(memberRequest);
	}

	@Override
	public com.liferay.invitation.invite.members.model.MemberRequest
			updateMemberRequest(String key, long receiverUserId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _memberRequestLocalService.updateMemberRequest(
			key, receiverUserId);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _memberRequestLocalService.getBasePersistence();
	}

	@Override
	public MemberRequestLocalService getWrappedService() {
		return _memberRequestLocalService;
	}

	@Override
	public void setWrappedService(
		MemberRequestLocalService memberRequestLocalService) {

		_memberRequestLocalService = memberRequestLocalService;
	}

	private MemberRequestLocalService _memberRequestLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-2041823038