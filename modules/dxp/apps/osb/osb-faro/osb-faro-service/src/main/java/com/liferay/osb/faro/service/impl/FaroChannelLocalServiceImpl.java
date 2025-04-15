/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.service.impl;

import com.liferay.mail.kernel.model.MailMessage;
import com.liferay.mail.kernel.service.MailService;
import com.liferay.osb.faro.constants.FaroChannelConstants;
import com.liferay.osb.faro.model.FaroChannel;
import com.liferay.osb.faro.model.FaroUser;
import com.liferay.osb.faro.service.FaroUserLocalService;
import com.liferay.osb.faro.service.base.FaroChannelLocalServiceBaseImpl;
import com.liferay.osb.faro.util.EmailUtil;
import com.liferay.osb.faro.util.FaroPropsValues;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.mail.internet.InternetAddress;

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
	property = "model.class.name=com.liferay.osb.faro.model.FaroChannel",
	service = AopService.class
)
public class FaroChannelLocalServiceImpl
	extends FaroChannelLocalServiceBaseImpl {

	@Override
	public FaroChannel addFaroChannel(
			long userId, String name, String channelId, long workspaceGroupId)
		throws PortalException {

		long faroChannelId = counterLocalService.increment();

		Group group = _groupLocalService.addGroup(
			userId, workspaceGroupId, FaroChannel.class.getName(),
			faroChannelId, 0,
			Collections.singletonMap(LocaleUtil.getDefault(), name), null,
			GroupConstants.TYPE_SITE_PRIVATE, true,
			GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION, null, true, true,
			null);

		// friendlyURL will be derived from the group name if empty, so it has
		// to be removed after creation

		group.setFriendlyURL(null);

		group = _groupLocalService.updateGroup(group);

		FaroChannel faroChannel = faroChannelPersistence.create(faroChannelId);

		faroChannel.setGroupId(group.getGroupId());
		faroChannel.setUserId(userId);

		long now = System.currentTimeMillis();

		faroChannel.setCreateTime(now);
		faroChannel.setModifiedTime(now);

		faroChannel.setChannelId(channelId);
		faroChannel.setName(name);
		faroChannel.setWorkspaceGroupId(workspaceGroupId);

		return faroChannelPersistence.update(faroChannel);
	}

	@Override
	public void addUsers(
			long companyId, String channelId, List<Long> invitedUserIds,
			long userId, long workspaceGroupId)
		throws PortalException {

		FaroChannel faroChannel = faroChannelPersistence.findByC_W(
			channelId, workspaceGroupId);

		Role role = _roleLocalService.getRole(
			companyId, RoleConstants.SITE_MEMBER);

		for (long invitedUserId : invitedUserIds) {
			_groupLocalService.addUserGroup(
				invitedUserId, faroChannel.getGroupId());

			_userGroupRoleLocalService.deleteUserGroupRoles(
				invitedUserId, new long[] {faroChannel.getGroupId()});

			_userGroupRoleLocalService.addUserGroupRoles(
				invitedUserId, faroChannel.getGroupId(),
				new long[] {role.getRoleId()});

			try {
				_sendEmail(
					faroChannel, invitedUserId, role.getRoleId(), userId);
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}
	}

	@Override
	public FaroChannel deleteFaroChannel(FaroChannel faroChannel)
		throws PortalException {

		_groupLocalService.deleteGroup(faroChannel.getGroupId());

		return faroChannelPersistence.remove(faroChannel);
	}

	@Override
	public FaroChannel deleteFaroChannel(
			String channelId, long workspaceGroupId)
		throws PortalException {

		return deleteFaroChannel(
			faroChannelPersistence.findByC_W(channelId, workspaceGroupId));
	}

	@Override
	public void deleteFaroChannels(long workspaceGroupId)
		throws PortalException {

		for (FaroChannel faroChannel :
				faroChannelPersistence.findByWorkspaceGroupId(
					workspaceGroupId)) {

			deleteFaroChannel(faroChannel);
		}
	}

	@Override
	public FaroChannel getFaroChannel(String channelId, long workspaceGroupId)
		throws PortalException {

		return faroChannelPersistence.findByC_W(channelId, workspaceGroupId);
	}

	@Override
	public List<FaroUser> getFaroUsers(
			String channelId, boolean available, String query,
			List<Integer> statuses, long workspaceGroupId, int start, int end,
			OrderByComparator<FaroUser> orderByComparator)
		throws PortalException {

		FaroChannel faroChannel = faroChannelPersistence.findByC_W(
			channelId, workspaceGroupId);

		return _faroUserLocalService.getFaroUsers(
			faroChannel.getGroupId(), available, query, statuses,
			faroChannel.getWorkspaceGroupId(), start, end, orderByComparator);
	}

	@Override
	public int getFaroUsersCount(
			String channelId, boolean available, String query,
			List<Integer> statuses, long workspaceGroupId)
		throws PortalException {

		FaroChannel faroChannel = faroChannelPersistence.findByC_W(
			channelId, workspaceGroupId);

		return _faroUserLocalService.getFaroUsersCount(
			faroChannel.getGroupId(), available, query, statuses,
			faroChannel.getWorkspaceGroupId());
	}

	@Override
	public void removeUsers(
			String channelId, List<Long> userIds, long workspaceGroupId)
		throws PortalException {

		FaroChannel faroChannel = faroChannelPersistence.findByC_W(
			channelId, workspaceGroupId);

		for (long userId : userIds) {
			_userGroupRoleLocalService.deleteUserGroupRoles(
				userId, new long[] {faroChannel.getGroupId()});

			_groupLocalService.deleteUserGroup(
				userId, faroChannel.getGroupId());
		}
	}

	@Override
	public List<FaroChannel> search(
		long groupId, String query, int start, int end,
		OrderByComparator<FaroChannel> orderByComparator) {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		return faroChannelFinder.findByKeywords(
			groupId, FaroChannelConstants.PERMISSION_ALL_USERS, query,
			permissionChecker.getUserId(), start, end, orderByComparator);
	}

	@Override
	public int searchCount(long groupId, String query) {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		return faroChannelFinder.countByKeywords(
			groupId, FaroChannelConstants.PERMISSION_ALL_USERS, query,
			permissionChecker.getUserId());
	}

	private void _sendEmail(
			FaroChannel faroChannel, long invitedUserId, long roleId,
			long userId)
		throws Exception {

		User user = _userLocalService.getUser(userId);

		InternetAddress from = new InternetAddress(
			"ac@liferay.com", user.getFullName() + " (Analytics Cloud)");

		User invitedUser = _userLocalService.getUser(invitedUserId);

		InternetAddress to = new InternetAddress(
			invitedUser.getEmailAddress(), invitedUser.getFullName());

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", invitedUser.getLocale(), getClass());

		String subject = _language.get(resourceBundle, "new-property-access");

		String roleName = null;

		Role role = _roleLocalService.getRole(roleId);

		if (StringUtil.equals(
				role.getName(), RoleConstants.SITE_ADMINISTRATOR)) {

			roleName = "administrator-fragment";
		}
		else {
			roleName = "member-fragment";
		}

		String body = StringUtil.replace(
			StringUtil.read(
				getClassLoader(),
				"com/liferay/osb/faro/dependencies/property-invite.html"),
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
					"you-have-been-added-as-a-team-x-on-the-analytics-cloud-" +
						"x-workspace-property-by-x",
					new String[] {
						roleName, faroChannel.getName(), user.getEmailAddress()
					}),
				_language.get(
					resourceBundle,
					"log-in-to-your-workspace-to-access-this-property"),
				String.valueOf(DateUtil.getYear(new Date()))
			});

		_mailService.sendEmail(new MailMessage(from, to, subject, body, true));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FaroChannelLocalServiceImpl.class);

	@Reference
	private FaroUserLocalService _faroUserLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference
	private MailService _mailService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Reference
	private UserLocalService _userLocalService;

}