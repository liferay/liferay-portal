/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.service.impl;

import com.liferay.mail.kernel.model.MailMessage;
import com.liferay.mail.kernel.service.MailService;
import com.liferay.osb.faro.constants.FaroUserConstants;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.model.FaroUser;
import com.liferay.osb.faro.service.FaroPreferencesLocalService;
import com.liferay.osb.faro.service.base.FaroUserLocalServiceBaseImpl;
import com.liferay.osb.faro.service.persistence.FaroProjectPersistence;
import com.liferay.osb.faro.util.EmailUtil;
import com.liferay.osb.faro.util.FaroPropsValues;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import jakarta.mail.internet.InternetAddress;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Matthew Kong
 */
@Component(
	property = "model.class.name=com.liferay.osb.faro.model.FaroUser",
	service = AopService.class
)
public class FaroUserLocalServiceImpl extends FaroUserLocalServiceBaseImpl {

	@Override
	public List<FaroUser> acceptInvitations(long userId, String key) {
		User user = _userLocalService.fetchUser(userId);

		if (user == null) {
			return Collections.emptyList();
		}

		List<FaroUser> faroUsers = new ArrayList<>(
			faroUserPersistence.findByE_S(
				user.getEmailAddress(), FaroUserConstants.STATUS_PENDING));

		FaroUser faroUser = faroUserPersistence.fetchByKey(key);

		if (faroUser != null) {
			faroUsers.add(faroUser);
		}

		for (FaroUser curFaroUser : faroUsers) {
			if (curFaroUser.getStatus() == FaroUserConstants.STATUS_APPROVED) {
				continue;
			}

			curFaroUser.setModifiedTime(System.currentTimeMillis());
			curFaroUser.setLiveUserId(userId);
			curFaroUser.setStatus(FaroUserConstants.STATUS_APPROVED);

			updateFaroUser(curFaroUser);
		}

		return faroUsers;
	}

	@Override
	public FaroUser addFaroUser(
			long userId, long groupId, long liveUserId, long roleId,
			String emailAddress, int status, boolean sendEmail)
		throws PortalException {

		FaroUser faroUser = faroUserPersistence.fetchByG_E(
			groupId, emailAddress);

		if (faroUser == null) {
			long faroUserId = counterLocalService.increment();

			faroUser = faroUserPersistence.create(faroUserId);

			faroUser.setGroupId(groupId);
			faroUser.setUserId(userId);

			User user = _userLocalService.getUser(userId);

			faroUser.setUserName(user.getFullName());

			long now = System.currentTimeMillis();

			faroUser.setCreateTime(now);
			faroUser.setModifiedTime(now);

			User liveUser = _userLocalService.fetchUserByEmailAddress(
				user.getCompanyId(), emailAddress);

			if (liveUser != null) {
				liveUserId = liveUser.getUserId();
			}

			faroUser.setLiveUserId(liveUserId);

			faroUser.setRoleId(roleId);
			faroUser.setEmailAddress(emailAddress);
			faroUser.setKey(PortalUUIDUtil.generate());
			faroUser.setStatus(status);

			faroUser = faroUserPersistence.update(faroUser);
		}

		if (sendEmail) {
			try {
				_sendEmail(faroUser, groupId, roleId, userId);
			}
			catch (Exception exception) {
				throw new SystemException(exception);
			}
		}

		return faroUser;
	}

	@Override
	public FaroUser deleteFaroUser(long faroUserId) throws PortalException {
		FaroUser faroUser = getFaroUser(faroUserId);

		if (faroUser.getLiveUserId() > 0) {
			_faroPreferencesLocalService.deleteFaroPreferences(
				faroUser.getGroupId(), faroUser.getLiveUserId());
			_groupLocalService.deleteUserGroup(
				faroUser.getLiveUserId(), faroUser.getGroupId());
			_userGroupRoleLocalService.deleteUserGroupRoles(
				faroUser.getLiveUserId(), new long[] {faroUser.getGroupId()});
		}

		return super.deleteFaroUser(faroUserId);
	}

	@Override
	public void deleteFaroUsers(long groupId) {
		faroUserPersistence.removeByGroupId(groupId);
	}

	@Override
	public void deleteFaroUsersByLiveUserId(long liveUserId)
		throws PortalException {

		List<FaroUser> faroUsers = faroUserPersistence.findByLiveUserId(
			liveUserId);

		for (FaroUser faroUser : faroUsers) {
			deleteFaroUser(faroUser.getFaroUserId());
		}
	}

	@Override
	public FaroUser fetchFaroUser(long groupId, long liveUserId) {
		return faroUserPersistence.fetchByG_L(groupId, liveUserId, false);
	}

	@Override
	public FaroUser fetchFaroUser(long groupId, String emailAddress) {
		return faroUserPersistence.fetchByG_E(groupId, emailAddress);
	}

	@Override
	public FaroUser fetchOwnerFaroUser(long groupId) {
		Role role = _roleLocalService.fetchRole(
			_portal.getDefaultCompanyId(), RoleConstants.SITE_OWNER);

		if (role == null) {
			return null;
		}

		return faroUserPersistence.fetchByG_R_First(
			groupId, role.getRoleId(), null);
	}

	@Override
	public FaroUser getFaroUser(long groupId, long liveUserId)
		throws PortalException {

		return faroUserPersistence.findByG_L(groupId, liveUserId);
	}

	@Override
	public List<FaroUser> getFaroUsers(
			long groupId, boolean available, String query,
			List<Integer> statuses, long workspaceGroupId, int start, int end,
			OrderByComparator<FaroUser> orderByComparator)
		throws PortalException {

		return faroUserFinder.findByChannelKeywords(
			groupId, available, query, statuses, workspaceGroupId, start, end,
			orderByComparator);
	}

	@Override
	public List<FaroUser> getFaroUsersByLiveUserId(
		long liveUserId, int status) {

		return faroUserPersistence.findByL_S(liveUserId, status);
	}

	@Override
	public List<FaroUser> getFaroUsersByRoleId(long groupId, long roleId) {
		return faroUserPersistence.findByG_R(groupId, roleId);
	}

	@Override
	public List<FaroUser> getFaroUsersByStatus(long groupId, int status) {
		return faroUserPersistence.findByG_S(groupId, status);
	}

	@Override
	public int getFaroUsersCount(
			long groupId, boolean available, String query,
			List<Integer> statuses, long workspaceGroupId)
		throws PortalException {

		return faroUserFinder.countByChannelKeywords(
			groupId, available, query, statuses, workspaceGroupId);
	}

	@Override
	public FaroUser getOwnerFaroUser(long groupId) throws PortalException {
		Role role = _roleLocalService.getRole(
			_portal.getDefaultCompanyId(), RoleConstants.SITE_OWNER);

		return faroUserPersistence.findByG_R_First(
			groupId, role.getRoleId(), null);
	}

	@Override
	public List<FaroUser> search(
		long groupId, String query, List<Integer> statuses, int start, int end,
		OrderByComparator<FaroUser> orderByComparator) {

		return faroUserFinder.findByKeywords(
			groupId, query, statuses, start, end, orderByComparator);
	}

	@Override
	public int searchCount(long groupId, String query, List<Integer> statuses) {
		return faroUserFinder.countByKeywords(groupId, query, statuses);
	}

	private String _getNotificationMessage(
			long roleId, long groupId, ResourceBundle resourceBundle,
			String userEmailAddress)
		throws Exception {

		String roleName = null;

		Role role = _roleLocalService.getRole(roleId);

		if (StringUtil.equals(
				role.getName(), RoleConstants.SITE_ADMINISTRATOR)) {

			roleName = "administrator-fragment";
		}
		else {
			roleName = "member-fragment";
		}

		FaroProject faroProject = _faroProjectPersistence.fetchByGroupId(
			groupId);

		return _language.format(
			resourceBundle,
			"you-have-been-added-as-a-team-x-on-the-analytics-cloud-x-" +
				"workspace-by-x",
			new String[] {roleName, faroProject.getName(), userEmailAddress});
	}

	private void _sendEmail(
			FaroUser faroUser, long groupId, long roleId, long userId)
		throws Exception {

		if (faroUser.getStatus() == FaroUserConstants.STATUS_REQUESTED) {
			_sendEmailRequest(userId, groupId);
		}
		else {
			_sendEmailNewUser(faroUser, groupId, roleId);
		}
	}

	private void _sendEmailNewUser(FaroUser faroUser, long groupId, long roleId)
		throws Exception {

		User user = _userLocalService.getUser(faroUser.getUserId());

		InternetAddress from = new InternetAddress(
			"ac@liferay.com", user.getFullName() + " (Analytics Cloud)");

		String toName = StringPool.BLANK;

		if (faroUser.getLiveUserId() > 0) {
			User receiverUser = _userLocalService.getUser(
				faroUser.getLiveUserId());

			toName = receiverUser.getFullName();
		}

		InternetAddress to = new InternetAddress(
			faroUser.getEmailAddress(), toName);

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", user.getLocale(), getClass());

		String body = null;
		String subject = null;

		if (faroUser.getLiveUserId() > 0) {
			body = StringUtil.read(
				getClassLoader(),
				"com/liferay/osb/faro/dependencies/invite-existing-user.html");
			subject = _language.get(resourceBundle, "new-workspace-access");
		}
		else {
			body = StringUtil.read(
				getClassLoader(),
				"com/liferay/osb/faro/dependencies/invite-new-user.html");
			subject = _language.get(
				resourceBundle, "welcome-to-analytics-cloud");
		}

		body = StringUtil.replace(
			body,
			new String[] {
				"[$BUTTON_TEXT$]", "[$BUTTON_URL$]", "[$EMAIL_HEADER_URL$]",
				"[$EMAIL_TITLE$]", "[$FARO_URL$]", "[$FOOTER_MENU_1$]",
				"[$FOOTER_MENU_2$]", "[$FOOTER_MENU_3$]", "[$FOOTER_MSG_1$]",
				"[$FOOTER_MSG_2$]", "[$FOOTER_MSG_3$]", "[$FOOTER_MSG_4$]",
				"[$HEADER_MSG_1$]", "[$LIFERAY_LOGO_URL$]",
				"[$NOTIFICATION_MSG_1$]", "[$NOTIFICATION_MSG_2$]", "[$YEAR$]"
			},
			new String[] {
				_language.get(resourceBundle, "go-to-analytics-cloud"),
				EmailUtil.getShareIconURL(), EmailUtil.getEmailHeaderURL(),
				subject, FaroPropsValues.FARO_URL,
				_language.get(resourceBundle, "contact-support"),
				_language.get(resourceBundle, "documentation"),
				_language.get(resourceBundle, "announcements"),
				_language.format(
					resourceBundle, "this-email-was-sent-by-x",
					new String[] {
						"<a style=\"color: #0b5fff; text-decoration: none;\" " +
							"href=\"https://liferay.com\" target=\"_blank\">",
						"</a>"
					}),
				_language.get(resourceBundle, "need-help"),
				_language.get(
					resourceBundle, "let-our-team-do-the-work-for-you"),
				_language.get(
					resourceBundle,
					"liferay-experts-are-available-to-answer-your-questions-" +
						"anytime"),
				subject, EmailUtil.getLiferayIconURL(),
				_getNotificationMessage(
					roleId, groupId, resourceBundle, user.getEmailAddress()),
				_language.format(
					resourceBundle,
					"sign-in-with-your-existing-liferay-username-and-" +
						"password-or-create-an-account-using-x",
					new String[] {
						"<a style=\"color: #0b5fff; text-decoration: none;\" " +
							"href=\"https://login.liferay.com/signin" +
								"/register\" target=\"_blank\">",
						"</a>", faroUser.getEmailAddress()
					}),
				String.valueOf(DateUtil.getYear(new Date()))
			});

		_mailService.sendEmail(new MailMessage(from, to, subject, body, true));

		if (_log.isInfoEnabled()) {
			_log.info(
				"New user invite email notification sent to " +
					to.getAddress());
		}
	}

	private void _sendEmailRequest(long userId, long groupId) throws Exception {
		User senderUser = _userLocalService.getUser(userId);

		InternetAddress from = new InternetAddress(
			"ac@liferay.com", senderUser.getFullName() + " (Analytics Cloud)");

		FaroProject faroProject = _faroProjectPersistence.findByGroupId(
			groupId);

		User receiverUser = _userLocalService.getUser(faroProject.getUserId());

		InternetAddress to = new InternetAddress(
			receiverUser.getEmailAddress(), receiverUser.getFullName());

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", receiverUser.getLocale(), getClass());

		String subject = _language.get(
			resourceBundle, "request-to-join-workspace");

		String body = StringUtil.replace(
			StringUtil.read(
				getClassLoader(),
				"com/liferay/osb/faro/dependencies/join-request.html"),
			new String[] {
				"[$BUTTON_TEXT$]", "[$BUTTON_URL$]", "[$EMAIL_HEADER_URL$]",
				"[$EMAIL_TITLE$]", "[$FARO_URL$]", "[$FOOTER_MENU_1$]",
				"[$FOOTER_MENU_2$]", "[$FOOTER_MENU_3$]", "[$FOOTER_MSG_1$]",
				"[$FOOTER_MSG_2$]", "[$FOOTER_MSG_3$]", "[$FOOTER_MSG_4$]",
				"[$HEADER_MSG_1$]", "[$LIFERAY_LOGO_URL$]",
				"[$NOTIFICATION_MSG_1$]", "[$NOTIFICATION_MSG_2$]", "[$YEAR$]"
			},
			new String[] {
				_language.get(resourceBundle, "go-to-analytics-cloud"),
				EmailUtil.getShareIconURL(), EmailUtil.getEmailHeaderURL(),
				subject, FaroPropsValues.FARO_URL,
				_language.get(resourceBundle, "contact-support"),
				_language.get(resourceBundle, "documentation"),
				_language.get(resourceBundle, "announcements"),
				_language.format(
					resourceBundle, "this-email-was-sent-by-x",
					new String[] {
						"<a style=\"color: #0b5fff; text-decoration: none;\" " +
							"href=\"https://liferay.com\" target=\"_blank\">",
						"</a>"
					}),
				_language.get(resourceBundle, "need-help"),
				_language.get(
					resourceBundle, "let-our-team-do-the-work-for-you"),
				_language.get(
					resourceBundle,
					"liferay-experts-are-available-to-answer-your-questions-" +
						"anytime"),
				subject, EmailUtil.getLiferayIconURL(),
				_language.format(
					resourceBundle,
					"x-has-requested-to-join-the-analytics-cloud-x-workspace",
					new String[] {
						StringBundler.concat(
							senderUser.getFullName(), "(",
							senderUser.getEmailAddress(), ")"),
						faroProject.getName()
					}),
				_language.get(
					resourceBundle, "email-sign-in-to-approve-or-deny"),
				String.valueOf(DateUtil.getYear(new Date()))
			});

		_mailService.sendEmail(new MailMessage(from, to, subject, body, true));

		if (_log.isInfoEnabled()) {
			_log.info(
				"Request to join workspace email notification sent to " +
					to.getAddress());
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FaroUserLocalServiceImpl.class);

	@Reference
	private FaroPreferencesLocalService _faroPreferencesLocalService;

	@Reference
	private FaroProjectPersistence _faroProjectPersistence;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference
	private MailService _mailService;

	@Reference
	private Portal _portal;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Reference
	private UserLocalService _userLocalService;

}