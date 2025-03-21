/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.invitation.invite.members.service.impl;

import com.liferay.invitation.invite.members.constants.InviteMembersConstants;
import com.liferay.invitation.invite.members.exception.MemberRequestAlreadyUsedException;
import com.liferay.invitation.invite.members.exception.MemberRequestInvalidUserException;
import com.liferay.invitation.invite.members.model.MemberRequest;
import com.liferay.invitation.invite.members.service.base.MemberRequestLocalServiceBaseImpl;
import com.liferay.mail.kernel.model.MailMessage;
import com.liferay.mail.kernel.service.MailService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.MembershipRequestConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.notifications.NotificationEvent;
import com.liferay.portal.kernel.notifications.UserNotificationManagerUtil;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.mail.internet.InternetAddress;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Ryan Park
 * @author Jonathan Lee
 */
@Component(
	property = "model.class.name=com.liferay.invitation.invite.members.model.MemberRequest",
	service = AopService.class
)
public class MemberRequestLocalServiceImpl
	extends MemberRequestLocalServiceBaseImpl {

	@Override
	public MemberRequest addMemberRequest(
			long userId, long groupId, long receiverUserId,
			String receiverEmailAddress, long invitedRoleId, long invitedTeamId,
			ServiceContext serviceContext)
		throws PortalException {

		// Member request

		User user = _userLocalService.getUserById(userId);

		try {
			User receiverUser = _userLocalService.getUserByEmailAddress(
				serviceContext.getCompanyId(), receiverEmailAddress);

			receiverUserId = receiverUser.getUserId();
		}
		catch (NoSuchUserException noSuchUserException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(noSuchUserException);
			}
		}

		Date date = new Date();

		long memberRequestId = counterLocalService.increment();

		MemberRequest memberRequest = memberRequestPersistence.create(
			memberRequestId);

		memberRequest.setGroupId(groupId);
		memberRequest.setCompanyId(user.getCompanyId());
		memberRequest.setUserId(userId);
		memberRequest.setUserName(user.getFullName());
		memberRequest.setCreateDate(date);
		memberRequest.setModifiedDate(date);
		memberRequest.setKey(PortalUUIDUtil.generate());
		memberRequest.setReceiverUserId(receiverUserId);
		memberRequest.setInvitedRoleId(invitedRoleId);
		memberRequest.setInvitedTeamId(invitedTeamId);
		memberRequest.setStatus(InviteMembersConstants.STATUS_PENDING);

		memberRequest = memberRequestPersistence.update(memberRequest);

		// Email

		try {
			_sendEmail(receiverEmailAddress, memberRequest, serviceContext);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}

		// Notifications

		if (receiverUserId > 0) {
			_sendNotificationEvent(memberRequest);
		}

		return memberRequest;
	}

	@Override
	public void addMemberRequests(
			long userId, long groupId, long[] receiverUserIds,
			long invitedRoleId, long invitedTeamId,
			ServiceContext serviceContext)
		throws PortalException {

		for (long receiverUserId : receiverUserIds) {
			if (hasPendingMemberRequest(groupId, receiverUserId)) {
				continue;
			}

			User user = _userLocalService.getUser(receiverUserId);

			addMemberRequest(
				userId, groupId, receiverUserId, user.getEmailAddress(),
				invitedRoleId, invitedTeamId, serviceContext);
		}
	}

	@Override
	public void addMemberRequests(
			long userId, long groupId, String[] emailAddresses,
			long invitedRoleId, long invitedTeamId,
			ServiceContext serviceContext)
		throws PortalException {

		for (String emailAddress : emailAddresses) {
			if (!Validator.isEmailAddress(emailAddress)) {
				continue;
			}

			addMemberRequest(
				userId, groupId, 0, emailAddress, invitedRoleId, invitedTeamId,
				serviceContext);
		}
	}

	@Override
	public MemberRequest getMemberRequest(
			long groupId, long receiverUserId, int status)
		throws PortalException {

		return memberRequestPersistence.findByG_R_S(
			groupId, receiverUserId, status);
	}

	@Override
	public List<MemberRequest> getReceiverMemberRequest(
		long receiverUserId, int start, int end) {

		return memberRequestPersistence.findByReceiverUserId(receiverUserId);
	}

	@Override
	public int getReceiverMemberRequestCount(long receiverUserId) {
		return memberRequestPersistence.countByReceiverUserId(receiverUserId);
	}

	@Override
	public List<MemberRequest> getReceiverStatusMemberRequest(
		long receiverUserId, int status, int start, int end) {

		return memberRequestPersistence.findByR_S(
			receiverUserId, status, start, end);
	}

	@Override
	public int getReceiverStatusMemberRequestCount(
		long receiverUserId, int status) {

		return memberRequestPersistence.countByR_S(receiverUserId, status);
	}

	@Override
	public boolean hasPendingMemberRequest(long groupId, long receiverUserId) {
		MemberRequest memberRequest = memberRequestPersistence.fetchByG_R_S(
			groupId, receiverUserId, InviteMembersConstants.STATUS_PENDING);

		if (memberRequest != null) {
			return true;
		}

		return false;
	}

	@Override
	public MemberRequest updateMemberRequest(
			long userId, long memberRequestId, int status)
		throws Exception {

		MemberRequest memberRequest = memberRequestPersistence.findByPrimaryKey(
			memberRequestId);

		_validate(memberRequest, userId);

		memberRequest.setModifiedDate(new Date());
		memberRequest.setStatus(status);

		memberRequest = memberRequestPersistence.update(memberRequest);

		if (status == InviteMembersConstants.STATUS_ACCEPTED) {
			_userLocalService.addGroupUsers(
				memberRequest.getGroupId(),
				new long[] {memberRequest.getReceiverUserId()});

			if (memberRequest.getInvitedRoleId() > 0) {
				_userGroupRoleLocalService.addUserGroupRoles(
					new long[] {memberRequest.getReceiverUserId()},
					memberRequest.getGroupId(),
					memberRequest.getInvitedRoleId());
			}

			if (memberRequest.getInvitedTeamId() > 0) {
				_userLocalService.addTeamUsers(
					memberRequest.getInvitedTeamId(),
					new long[] {memberRequest.getReceiverUserId()});
			}
		}

		return memberRequest;
	}

	@Override
	public MemberRequest updateMemberRequest(String key, long receiverUserId)
		throws PortalException {

		MemberRequest memberRequest = memberRequestPersistence.findByKey(key);

		_validate(memberRequest, 0);

		memberRequest.setModifiedDate(new Date());
		memberRequest.setReceiverUserId(receiverUserId);

		memberRequest = memberRequestPersistence.update(memberRequest);

		if (receiverUserId > 0) {
			_sendNotificationEvent(memberRequest);
		}

		return memberRequest;
	}

	private String _addParameterWithPortletNamespace(
		String url, String name, String value) {

		String portletId = HttpComponentsUtil.getParameter(
			url, "p_p_id", false);

		if (Validator.isNotNull(portletId)) {
			name = _portal.getPortletNamespace(portletId) + name;
		}

		return HttpComponentsUtil.addParameter(url, name, value);
	}

	private String _getCreateAccountURL(
			MemberRequest memberRequest, ServiceContext serviceContext)
		throws Exception {

		String createAccountURL = (String)serviceContext.getAttribute(
			"createAccountURL");

		if (Validator.isNull(createAccountURL)) {
			createAccountURL = serviceContext.getPortalURL();
		}

		if (!_workflowDefinitionLinkLocalService.hasWorkflowDefinitionLink(
				memberRequest.getCompanyId(),
				WorkflowConstants.DEFAULT_GROUP_ID, User.class.getName(), 0)) {

			String redirectURL = _getRedirectURL(serviceContext);

			redirectURL = _addParameterWithPortletNamespace(
				redirectURL, "actionRequired", StringPool.TRUE);
			redirectURL = _addParameterWithPortletNamespace(
				redirectURL, "key", memberRequest.getKey());

			createAccountURL = _addParameterWithPortletNamespace(
				createAccountURL, "redirect", redirectURL);
		}

		return createAccountURL;
	}

	private String _getLoginURL(
		MemberRequest memberRequest, ServiceContext serviceContext) {

		String loginURL = (String)serviceContext.getAttribute("loginURL");

		if (Validator.isNull(loginURL)) {
			loginURL = serviceContext.getPortalURL();
		}

		String redirectURL = _getRedirectURL(serviceContext);

		redirectURL = _addParameterWithPortletNamespace(
			redirectURL, "actionRequired", StringPool.TRUE);
		redirectURL = _addParameterWithPortletNamespace(
			redirectURL, "key", memberRequest.getKey());

		return HttpComponentsUtil.addParameter(
			loginURL, "redirect", redirectURL);
	}

	private String _getRedirectURL(ServiceContext serviceContext) {
		String redirectURL = (String)serviceContext.getAttribute("redirectURL");

		if (Validator.isNull(redirectURL)) {
			redirectURL = serviceContext.getCurrentURL();
		}

		return redirectURL;
	}

	private void _sendEmail(
			String emailAddress, MemberRequest memberRequest,
			ServiceContext serviceContext)
		throws Exception {

		long companyId = memberRequest.getCompanyId();

		Group group = _groupLocalService.getGroup(memberRequest.getGroupId());

		User user = _userLocalService.getUser(memberRequest.getUserId());

		User receiverUser = null;

		if (memberRequest.getReceiverUserId() > 0) {
			receiverUser = _userLocalService.getUser(
				memberRequest.getReceiverUserId());
		}

		String fromName = PrefsPropsUtil.getString(
			companyId, PropsKeys.ADMIN_EMAIL_FROM_NAME);
		String fromAddress = PrefsPropsUtil.getString(
			companyId, PropsKeys.ADMIN_EMAIL_FROM_ADDRESS);

		String toName = StringPool.BLANK;
		String toAddress = emailAddress;

		if (receiverUser != null) {
			toName = receiverUser.getFullName();
		}

		String subject = StringUtil.read(
			getClassLoader(),
			"com/liferay/invitation/invite/members/dependencies/subject.tmpl");

		String body = StringPool.BLANK;

		if (memberRequest.getReceiverUserId() > 0) {
			body = StringUtil.read(
				getClassLoader(),
				"com/liferay/invitation/invite/members/dependencies" +
					"/existing_user_body.tmpl");
		}
		else {
			body = StringUtil.read(
				getClassLoader(),
				"com/liferay/invitation/invite/members/dependencies" +
					"/new_user_body.tmpl");
		}

		subject = StringUtil.replace(
			subject,
			new String[] {
				"[$MEMBER_REQUEST_GROUP$]", "[$MEMBER_REQUEST_USER$]"
			},
			new String[] {
				group.getDescriptiveName(serviceContext.getLocale()),
				user.getFullName()
			});

		body = StringUtil.replace(
			body,
			new String[] {
				"[$ADMIN_ADDRESS$]", "[$ADMIN_NAME$]",
				"[$MEMBER_REQUEST_CREATE_ACCOUNT_URL$]",
				"[$MEMBER_REQUEST_GROUP$]", "[$MEMBER_REQUEST_LOGIN_URL$]",
				"[$MEMBER_REQUEST_USER$]"
			},
			new String[] {
				fromAddress, fromName,
				_getCreateAccountURL(memberRequest, serviceContext),
				group.getDescriptiveName(serviceContext.getLocale()),
				_getLoginURL(memberRequest, serviceContext), user.getFullName()
			});

		InternetAddress from = new InternetAddress(fromAddress, fromName);

		InternetAddress to = new InternetAddress(toAddress, toName);

		MailMessage mailMessage = new MailMessage(
			from, to, subject, body, true);

		_mailService.sendEmail(mailMessage);
	}

	private void _sendNotificationEvent(MemberRequest memberRequest)
		throws PortalException {

		String portletId = PortletProviderUtil.getPortletId(
			MemberRequest.class.getName(), PortletProvider.Action.EDIT);

		if (UserNotificationManagerUtil.isDeliver(
				memberRequest.getReceiverUserId(), portletId, 0,
				MembershipRequestConstants.STATUS_PENDING,
				UserNotificationDeliveryConstants.TYPE_WEBSITE)) {

			NotificationEvent notificationEvent = new NotificationEvent(
				System.currentTimeMillis(), portletId,
				JSONUtil.put(
					"classPK", memberRequest.getMemberRequestId()
				).put(
					"userId", memberRequest.getUserId()
				));

			notificationEvent.setDeliveryRequired(0);
			notificationEvent.setDeliveryType(
				UserNotificationDeliveryConstants.TYPE_WEBSITE);

			_userNotificationEventLocalService.addUserNotificationEvent(
				memberRequest.getReceiverUserId(), true, true,
				notificationEvent);
		}
	}

	private void _validate(MemberRequest memberRequest, long userId)
		throws PortalException {

		if (memberRequest.getStatus() !=
				InviteMembersConstants.STATUS_PENDING) {

			throw new MemberRequestAlreadyUsedException();
		}
		else if (memberRequest.getReceiverUserId() != userId) {
			throw new MemberRequestInvalidUserException();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MemberRequestLocalServiceImpl.class);

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private MailService _mailService;

	@Reference
	private Portal _portal;

	@Reference
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

	@Reference
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

}