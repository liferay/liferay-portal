/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.dsr.internal.resource.v1_0;

import com.liferay.headless.dsr.dto.v1_0.UserAccount;
import com.liferay.headless.dsr.internal.dto.v1_0.converter.UserAccountDTOConverterContext;
import com.liferay.headless.dsr.resource.v1_0.UserAccountResource;
import com.liferay.login.web.constants.LoginPortletKeys;
import com.liferay.notification.context.NotificationContextBuilder;
import com.liferay.notification.model.NotificationTemplate;
import com.liferay.notification.service.NotificationTemplateLocalService;
import com.liferay.notification.type.NotificationType;
import com.liferay.notification.type.NotificationTypeServiceTracker;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.events.ServicePreAction;
import com.liferay.portal.events.ThemeServicePreAction;
import com.liferay.portal.kernel.exception.RoleAssignmentException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.TicketLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.DummyHttpServletResponse;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.liveusers.LiveUsers;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.site.dsr.site.initializer.constants.DSRPortletKeys;
import com.liferay.site.dsr.site.initializer.constants.DSRTicketConstants;

import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.WindowState;

import jakarta.servlet.http.HttpServletResponse;

import jakarta.validation.ValidationException;

import java.io.Serializable;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Stefano Motta
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/user-account.properties",
	scope = ServiceScope.PROTOTYPE, service = UserAccountResource.class
)
public class UserAccountResourceImpl extends BaseUserAccountResourceImpl {

	@Override
	public void deleteRoomUserAccount(Long roomId, Long userAccountId)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-66359")) {

			throw new UnsupportedOperationException();
		}

		Group group = _getGroup(roomId);

		LiveUsers.leaveGroup(
			contextCompany.getCompanyId(), group.getGroupId(), userAccountId);

		_userGroupRoleLocalService.deleteUserGroupRoles(
			new long[] {userAccountId}, group.getGroupId());

		_userLocalService.deleteGroupUser(group.getGroupId(), userAccountId);
	}

	@Override
	public Page<UserAccount> getRoomUserAccountsPage(
			Long roomId, Pagination pagination)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-66359")) {

			throw new UnsupportedOperationException();
		}

		Group group = _getGroup(roomId);

		return Page.of(
			null,
			transform(
				_userLocalService.getGroupUsers(
					group.getGroupId(), pagination.getStartPosition(),
					pagination.getEndPosition()),
				user -> _toUserAccount(group.getGroupId(), user)),
			pagination,
			_userLocalService.getGroupUsersCount(group.getGroupId()));
	}

	@Override
	public UserAccount patchRoomUserAccount(
			Long roomId, Long userAccountId, UserAccount userAccount)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-66359")) {

			throw new UnsupportedOperationException();
		}

		User user = _userLocalService.getUser(userAccountId);
		Group group = _getGroup(roomId);

		_userGroupRoleLocalService.deleteUserGroupRoles(
			new long[] {user.getUserId()}, group.getGroupId());

		if (Validator.isNotNull(userAccount.getRoleKey())) {
			Role role = _roleLocalService.getRole(
				group.getCompanyId(), userAccount.getRoleKey());

			if (role.getType() != RoleConstants.TYPE_SITE) {
				throw new RoleAssignmentException(
					StringBundler.concat(
						"Role type ",
						RoleConstants.getTypeLabel(role.getType()), " is not ",
						RoleConstants.getTypeLabel(RoleConstants.TYPE_SITE)));
			}

			_userGroupRoleLocalService.addUserGroupRoles(
				user.getUserId(), group.getGroupId(),
				new long[] {role.getRoleId()});
		}

		return _toUserAccount(group.getGroupId(), user);
	}

	@Override
	public UserAccount postRoomUserAccount(Long roomId, UserAccount userAccount)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-66359")) {

			throw new UnsupportedOperationException();
		}

		if (Validator.isNull(userAccount.getEmailAddress())) {
			throw new ValidationException("Email Address is null");
		}

		ObjectEntry objectEntry = _objectEntryService.getObjectEntry(roomId);

		Map<String, Serializable> values = objectEntry.getValues();

		Group group = _groupService.getGroup(
			GetterUtil.getLong(values.get("siteId")));

		Ticket ticket = _addInviteMemberTicket(
			group.getCompanyId(), group,
			GetterUtil.getString(
				values.get("name"),
				group.getName(contextAcceptLanguage.getPreferredLocale())),
			userAccount);

		User user = _userLocalService.fetchUserByEmailAddress(
			ticket.getCompanyId(), userAccount.getEmailAddress());

		if (user == null) {
			return new UserAccount() {
				{
					setEmailAddress(userAccount::getEmailAddress);
					setId(ticket::getTicketId);
					setRoleKey(userAccount::getRoleKey);
				}
			};
		}

		_userLocalService.addGroupUser(group.getGroupId(), user.getUserId());

		if (Validator.isNotNull(userAccount.getRoleKey())) {
			Role role = _roleLocalService.getRole(
				group.getCompanyId(), userAccount.getRoleKey());

			if (role.getType() != RoleConstants.TYPE_SITE) {
				throw new RoleAssignmentException(
					StringBundler.concat(
						"Role type ",
						RoleConstants.getTypeLabel(role.getType()), " is not ",
						RoleConstants.getTypeLabel(RoleConstants.TYPE_SITE)));
			}

			_userGroupRoleLocalService.addUserGroupRoles(
				user.getUserId(), group.getGroupId(),
				new long[] {role.getRoleId()});
		}

		LiveUsers.joinGroup(
			group.getCompanyId(), group.getGroupId(), user.getUserId());

		return _toUserAccount(group.getGroupId(), user);
	}

	private Ticket _addInviteMemberTicket(
			long companyId, Group group, String name, UserAccount userAccount)
		throws Exception {

		Ticket ticket = _ticketLocalService.addTicket(
			companyId, Group.class.getName(), group.getGroupId(),
			DSRTicketConstants.TYPE_INVITE_MEMBER,
			JSONUtil.put(
				"emailAddress", userAccount.getEmailAddress()
			).put(
				"roleKey", userAccount.getRoleKey()
			).toString(),
			new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(48)),
			new ServiceContext());

		NotificationTemplate notificationTemplate =
			_notificationTemplateLocalService.
				getNotificationTemplateByExternalReferenceCode(
					"L_DSR_INVITE_MEMBER_EMAIL_NOTIFICATION_TEMPLATE",
					companyId);

		NotificationType notificationType =
			_notificationTypeServiceTracker.getNotificationType(
				notificationTemplate.getType());

		notificationType.sendNotification(
			new NotificationContextBuilder(
			).className(
				Group.class.getName()
			).classPK(
				group.getGroupId()
			).companyId(
				companyId
			).externalReferenceCode(
				group.getExternalReferenceCode()
			).groupId(
				group.getGroupId()
			).notificationTemplate(
				notificationTemplate
			).termValues(
				HashMapBuilder.<String, Object>put(
					"[%DIGITAL_SALES_ROOM_NAME%]", name
				).put(
					"[%DIGITAL_SALES_ROOM_URL%]",
					() -> {
						_initThemeDisplay(group.getGroupId());

						return PortletURLBuilder.create(
							PortletURLFactoryUtil.create(
								contextHttpServletRequest,
								DSRPortletKeys.DIGITAL_SALES_ROOM_INVITE_MEMBER,
								_portal.getPlidFromPortletId(
									group.getGroupId(), LoginPortletKeys.LOGIN),
								PortletRequest.RENDER_PHASE)
						).setMVCRenderCommandName(
							"/digital_sales_room/invite_member"
						).setParameter(
							"ticketKey", ticket.getKey()
						).setPortletMode(
							PortletMode.VIEW
						).setWindowState(
							WindowState.MAXIMIZED
						).buildString();
					}
				).put(
					"[%TO%]", userAccount.getEmailAddress()
				).build()
			).userId(
				contextUser.getUserId()
			).build());

		return ticket;
	}

	private Group _getGroup(long roomId) throws Exception {
		ObjectEntry objectEntry = _objectEntryService.getObjectEntry(roomId);

		Map<String, Serializable> values = objectEntry.getValues();

		return _groupService.getGroup(GetterUtil.getLong(values.get("siteId")));
	}

	private void _initThemeDisplay(long groupId) throws Exception {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)contextHttpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (themeDisplay != null) {
			return;
		}

		ServicePreAction servicePreAction = new ServicePreAction();

		HttpServletResponse httpServletResponse =
			new DummyHttpServletResponse();

		servicePreAction.servicePre(
			contextHttpServletRequest, httpServletResponse, false);

		ThemeServicePreAction themeServicePreAction =
			new ThemeServicePreAction();

		themeServicePreAction.run(
			contextHttpServletRequest, httpServletResponse);

		themeDisplay = (ThemeDisplay)contextHttpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		themeDisplay.setScopeGroupId(groupId);
	}

	private UserAccount _toUserAccount(long groupId, User user)
		throws Exception {

		return _userAccountDTOConverter.toDTO(
			new UserAccountDTOConverterContext(
				true, null, _dtoConverterRegistry, groupId, user.getUserId(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser),
			user);
	}

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private GroupService _groupService;

	@Reference
	private NotificationTemplateLocalService _notificationTemplateLocalService;

	@Reference
	private NotificationTypeServiceTracker _notificationTypeServiceTracker;

	@Reference
	private ObjectEntryService _objectEntryService;

	@Reference
	private Portal _portal;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private TicketLocalService _ticketLocalService;

	@Reference(
		target = "(component.name=com.liferay.headless.dsr.internal.dto.v1_0.converter.UserAccountDTOConverter)"
	)
	private DTOConverter<User, UserAccount> _userAccountDTOConverter;

	@Reference
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Reference
	private UserLocalService _userLocalService;

}