/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.social.service.impl;

import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portlet.social.service.base.SocialRequestLocalServiceBaseImpl;
import com.liferay.social.kernel.exception.RequestUserIdException;
import com.liferay.social.kernel.model.SocialRequest;
import com.liferay.social.kernel.model.SocialRequestConstants;
import com.liferay.social.kernel.service.SocialRequestInterpreterLocalService;

import java.util.List;

/**
 * The social request local service responsible for handling social requests
 * (e.g. friend requests).
 *
 * @author Brian Wing Shun Chan
 */
public class SocialRequestLocalServiceImpl
	extends SocialRequestLocalServiceBaseImpl {

	/**
	 * Adds a social request to the database.
	 *
	 * <p>
	 * In order to add a social request, both the requesting user and the
	 * receiving user must be from the same company and neither of them can be
	 * the default user.
	 * </p>
	 *
	 * @param  userId the primary key of the requesting user
	 * @param  groupId the primary key of the group
	 * @param  className the class name of the asset that is the subject of the
	 *         request
	 * @param  classPK the primary key of the asset that is the subject of the
	 *         request
	 * @param  type the request's type
	 * @param  extraData the extra data regarding the request
	 * @param  receiverUserId the primary key of the user receiving the request
	 * @return the social request
	 */
	@Override
	public SocialRequest addRequest(
			long userId, long groupId, String className, long classPK, int type,
			String extraData, long receiverUserId)
		throws PortalException {

		User user = _userPersistence.findByPrimaryKey(userId);
		User receiverUser = _userPersistence.findByPrimaryKey(receiverUserId);
		long now = System.currentTimeMillis();

		if ((userId == receiverUserId) || user.isGuestUser() ||
			receiverUser.isGuestUser() ||
			(user.getCompanyId() != receiverUser.getCompanyId())) {

			throw new RequestUserIdException();
		}

		long classNameId = _classNameLocalService.getClassNameId(className);

		SocialRequest request = socialRequestPersistence.fetchByU_C_C_T_R(
			userId, classNameId, classPK, type, receiverUserId);

		if (request == null) {
			long requestId = counterLocalService.increment(
				SocialRequest.class.getName());

			request = socialRequestPersistence.create(requestId);
		}

		request.setGroupId(groupId);
		request.setCompanyId(user.getCompanyId());
		request.setUserId(user.getUserId());
		request.setCreateDate(now);
		request.setModifiedDate(now);
		request.setClassNameId(classNameId);
		request.setClassPK(classPK);
		request.setType(type);
		request.setExtraData(extraData);
		request.setReceiverUserId(receiverUserId);
		request.setStatus(SocialRequestConstants.STATUS_PENDING);

		return socialRequestPersistence.update(request);
	}

	/**
	 * Removes all the social requests for the receiving user.
	 *
	 * @param receiverUserId the primary key of the receiving user
	 */
	@Override
	public void deleteReceiverUserRequests(long receiverUserId) {
		List<SocialRequest> requests =
			socialRequestPersistence.findByReceiverUserId(receiverUserId);

		for (SocialRequest request : requests) {
			deleteRequest(request);
		}
	}

	/**
	 * Removes the social request identified by its primary key from the
	 * database.
	 *
	 * @param requestId the primary key of the social request
	 */
	@Override
	public void deleteRequest(long requestId) throws PortalException {
		SocialRequest request = socialRequestPersistence.findByPrimaryKey(
			requestId);

		deleteRequest(request);
	}

	/**
	 * Removes the social request from the database.
	 *
	 * @param request the social request to be removed
	 */
	@Override
	public void deleteRequest(SocialRequest request) {
		socialRequestPersistence.remove(request);
	}

	@Override
	public void deleteRequests(long className, long classPK) {
		List<SocialRequest> requests = socialRequestPersistence.findByC_C(
			className, classPK);

		for (SocialRequest request : requests) {
			deleteRequest(request);
		}
	}

	/**
	 * Removes all the social requests for the requesting user.
	 *
	 * @param userId the primary key of the requesting user
	 */
	@Override
	public void deleteUserRequests(long userId) {
		List<SocialRequest> requests = socialRequestPersistence.findByUserId(
			userId);

		for (SocialRequest request : requests) {
			deleteRequest(request);
		}
	}

	/**
	 * Returns a range of all the social requests for the receiving user.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link
	 * com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  receiverUserId the primary key of the receiving user
	 * @param  start the lower bound of the range of results
	 * @param  end the upper bound of the range of results (not inclusive)
	 * @return the range of matching social requests
	 */
	@Override
	public List<SocialRequest> getReceiverUserRequests(
		long receiverUserId, int start, int end) {

		return socialRequestPersistence.findByReceiverUserId(
			receiverUserId, start, end);
	}

	/**
	 * Returns a range of all the social requests with the given status for the
	 * receiving user.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link
	 * com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  receiverUserId the primary key of the receiving user
	 * @param  status the social request's status
	 * @param  start the lower bound of the range of results
	 * @param  end the upper bound of the range of results (not inclusive)
	 * @return the range of matching social requests
	 */
	@Override
	public List<SocialRequest> getReceiverUserRequests(
		long receiverUserId, int status, int start, int end) {

		return socialRequestPersistence.findByR_S(
			receiverUserId, status, start, end);
	}

	/**
	 * Returns the number of social requests for the receiving user.
	 *
	 * @param  receiverUserId the primary key of the receiving user
	 * @return the number of matching social requests
	 */
	@Override
	public int getReceiverUserRequestsCount(long receiverUserId) {
		return socialRequestPersistence.countByReceiverUserId(receiverUserId);
	}

	/**
	 * Returns the number of social requests with the given status for the
	 * receiving user.
	 *
	 * @param  receiverUserId the primary key of the receiving user
	 * @param  status the social request's status
	 * @return the number of matching social requests
	 */
	@Override
	public int getReceiverUserRequestsCount(long receiverUserId, int status) {
		return socialRequestPersistence.countByR_S(receiverUserId, status);
	}

	/**
	 * Returns a range of all the social requests for the requesting user.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link
	 * com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  userId the primary key of the requesting user
	 * @param  start the lower bound of the range of results
	 * @param  end the upper bound of the range of results (not inclusive)
	 * @return the range of matching social requests
	 */
	@Override
	public List<SocialRequest> getUserRequests(
		long userId, int start, int end) {

		return socialRequestPersistence.findByUserId(userId, start, end);
	}

	/**
	 * Returns the number of social requests for the requesting user.
	 *
	 * @param  userId the primary key of the requesting user
	 * @return the number of matching social requests
	 */
	@Override
	public int getUserRequestsCount(long userId) {
		return socialRequestPersistence.countByUserId(userId);
	}

	/**
	 * Returns <code>true</code> if a matching social requests exists in the
	 * database.
	 *
	 * @param  userId the primary key of the requesting user
	 * @param  className the class name of the asset that is the subject of the
	 *         request
	 * @param  classPK the primary key of the asset that is the subject of the
	 *         request
	 * @param  type the request's type
	 * @param  status the social request's status
	 * @return <code>true</code> if the request exists; <code>false</code>
	 *         otherwise
	 */
	@Override
	public boolean hasRequest(
		long userId, String className, long classPK, int type, int status) {

		int count = socialRequestPersistence.countByU_C_C_T_S(
			userId, _classNameLocalService.getClassNameId(className), classPK,
			type, status);

		if (count <= 0) {
			return false;
		}

		return true;
	}

	/**
	 * Returns <code>true</code> if a matching social request exists in the
	 * database.
	 *
	 * @param  userId the primary key of the requesting user
	 * @param  className the class name of the asset that is the subject of the
	 *         request
	 * @param  classPK the primary key of the asset that is the subject of the
	 *         request
	 * @param  type the request's type
	 * @param  receiverUserId the primary key of the receiving user
	 * @param  status the social request's status
	 * @return <code>true</code> if the social request exists;
	 *         <code>false</code> otherwise
	 */
	@Override
	public boolean hasRequest(
		long userId, String className, long classPK, int type,
		long receiverUserId, int status) {

		SocialRequest socialRequest = socialRequestPersistence.fetchByU_C_C_T_R(
			userId, _classNameLocalService.getClassNameId(className), classPK,
			type, receiverUserId);

		if ((socialRequest == null) || (socialRequest.getStatus() != status)) {
			return false;
		}

		return true;
	}

	/**
	 * Updates the social request replacing its status.
	 *
	 * <p>
	 * If the status is updated to {@link SocialRequestConstants#STATUS_CONFIRM}
	 * then {@link
	 * SocialRequestInterpreterLocalService#processConfirmation(
	 * SocialRequest, ThemeDisplay)} is called. If the status is updated to
	 * {@link SocialRequestConstants#STATUS_IGNORE} then {@link
	 * SocialRequestInterpreterLocalService#processRejection(
	 * SocialRequest, ThemeDisplay)} is called.
	 * </p>
	 *
	 * @param  requestId the primary key of the social request
	 * @param  status the new status
	 * @param  themeDisplay the theme display
	 * @return the updated social request
	 */
	@Override
	public SocialRequest updateRequest(
			long requestId, int status, ThemeDisplay themeDisplay)
		throws PortalException {

		SocialRequest request = socialRequestPersistence.findByPrimaryKey(
			requestId);

		request.setModifiedDate(System.currentTimeMillis());
		request.setStatus(status);

		request = socialRequestPersistence.update(request);

		if (status == SocialRequestConstants.STATUS_CONFIRM) {
			_socialRequestInterpreterLocalService.processConfirmation(
				request, themeDisplay);
		}
		else if (status == SocialRequestConstants.STATUS_IGNORE) {
			_socialRequestInterpreterLocalService.processRejection(
				request, themeDisplay);
		}

		return request;
	}

	@BeanReference(type = ClassNameLocalService.class)
	private ClassNameLocalService _classNameLocalService;

	@BeanReference(type = SocialRequestInterpreterLocalService.class)
	private SocialRequestInterpreterLocalService
		_socialRequestInterpreterLocalService;

	@BeanReference(type = UserPersistence.class)
	private UserPersistence _userPersistence;

}