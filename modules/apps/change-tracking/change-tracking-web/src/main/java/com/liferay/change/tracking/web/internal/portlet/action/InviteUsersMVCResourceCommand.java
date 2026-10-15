/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.portlet.action;

import com.liferay.change.tracking.configuration.CTCollectionEmailConfiguration;
import com.liferay.change.tracking.constants.CTActionKeys;
import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.web.internal.helper.PublicationHelper;
import com.liferay.change.tracking.web.internal.security.permission.resource.CTCollectionPermission;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.notifications.NotificationEvent;
import com.liferay.portal.kernel.notifications.UserNotificationDefinition;
import com.liferay.portal.kernel.notifications.UserNotificationManagerUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.auth.AuthTokenUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.SubscriptionSender;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.util.HashMap;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Samuel Trong Tran
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CTPortletKeys.PUBLICATIONS,
		"mvc.command.name=/change_tracking/invite_users"
	},
	service = MVCResourceCommand.class
)
public class InviteUsersMVCResourceCommand
	extends BaseTransactionalMVCResourceCommand {

	@Override
	public boolean serveResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws PortletException {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setProductionModeWithSafeCloseable()) {

			return super.serveResource(resourceRequest, resourceResponse);
		}
	}

	@Override
	protected void doTransactionalCommand(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws IOException, PortalException {

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			resourceRequest);

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long ctCollectionId = ParamUtil.getLong(
			resourceRequest, "ctCollectionId");

		CTCollection ctCollection = _ctCollectionLocalService.fetchCTCollection(
			ctCollectionId);

		try {
			AuthTokenUtil.checkCSRFToken(
				httpServletRequest,
				InviteUsersMVCResourceCommand.class.getName());

			if ((ctCollection == null) &&
				(ctCollectionId != CTConstants.CT_COLLECTION_ID_PRODUCTION)) {

				throw new PrincipalException(
					"No CTCollection exists with the primary key " +
						ctCollectionId);
			}

			CTCollectionPermission.check(
				themeDisplay.getPermissionChecker(), ctCollection,
				CTActionKeys.INVITE_USERS);
		}
		catch (Exception exception) {
			if (exception instanceof PrincipalException) {
				JSONPortletResponseUtil.writeJSON(
					resourceRequest, resourceResponse,
					JSONUtil.put(
						"errorMessage",
						_language.get(
							httpServletRequest,
							"you-do-not-have-permission-to-perform-this-" +
								"action")));

				return;
			}

			throw exception;
		}

		long[] userIds = ParamUtil.getLongValues(resourceRequest, "userIds");

		for (long userId : userIds) {
			try {
				_userLocalService.getUserById(
					themeDisplay.getCompanyId(), userId);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}

				JSONPortletResponseUtil.writeJSON(
					resourceRequest, resourceResponse,
					JSONUtil.put(
						"errorMessage",
						_language.get(
							httpServletRequest,
							"your-request-failed-to-complete")));

				return;
			}
		}

		Group group = null;

		if (ctCollectionId == CTConstants.CT_COLLECTION_ID_PRODUCTION) {
			group = _groupLocalService.fetchUserGroup(
				themeDisplay.getCompanyId(), themeDisplay.getUserId());

			if (group == null) {
				group = _groupLocalService.addGroup(
					StringPool.BLANK, themeDisplay.getGuestUserId(),
					GroupConstants.DEFAULT_PARENT_GROUP_ID, null, 0,
					GroupConstants.DEFAULT_LIVE_GROUP_ID, new HashMap<>(), null,
					GroupConstants.TYPE_SITE_PRIVATE, null, false,
					GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION, null, false,
					false, true, null);
			}
		}
		else {
			group = _publicationHelper.getOrAddGroup(ctCollection);
		}

		long[] publicationsUserRoleUserIds = ParamUtil.getLongValues(
			resourceRequest, "publicationsUserRoleUserIds");

		if (publicationsUserRoleUserIds.length > 0) {
			Role publicationsUserRole = _roleLocalService.getRole(
				themeDisplay.getCompanyId(), RoleConstants.PUBLICATIONS_USER);

			_userLocalService.addRoleUsers(
				publicationsUserRole.getRoleId(), publicationsUserRoleUserIds);
		}

		int[] roleValues = ParamUtil.getIntegerValues(
			resourceRequest, "roleValues");

		for (int i = 0; i < userIds.length; i++) {
			List<UserGroupRole> userGroupRoles =
				_userGroupRoleLocalService.getUserGroupRoles(
					userIds[i], group.getGroupId());

			if (roleValues[i] < 0) {
				for (UserGroupRole userGroupRole : userGroupRoles) {
					_userGroupRoleLocalService.deleteUserGroupRole(
						userGroupRole);
				}

				continue;
			}

			Role role = _publicationHelper.getPublicationRole(
				themeDisplay.getCompanyId(), roleValues[i]);

			boolean addUserGroupRole = true;

			for (UserGroupRole userGroupRole : userGroupRoles) {
				if (userGroupRole.getRoleId() == role.getRoleId()) {
					addUserGroupRole = false;
				}
				else {
					_userGroupRoleLocalService.deleteUserGroupRole(
						userGroupRole);
				}
			}

			if (addUserGroupRole) {
				_userGroupRoleLocalService.addUserGroupRole(
					userIds[i], group.getGroupId(), role.getRoleId());
			}

			if (!userGroupRoles.isEmpty()) {
				continue;
			}

			_sendNotificationEvent(
				ctCollectionId, userIds[i], roleValues[i], themeDisplay);

			_sendEmail(ctCollectionId, userIds[i], themeDisplay);
		}

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse,
			JSONUtil.put(
				"successMessage",
				_language.get(
					httpServletRequest, "users-were-invited-successfully")));
	}

	private void _sendEmail(
			long ctCollectionId, long receiverUserId, ThemeDisplay themeDisplay)
		throws PortalException {

		if (!UserNotificationManagerUtil.isDeliver(
				receiverUserId, CTPortletKeys.PUBLICATIONS, 0,
				UserNotificationDefinition.NOTIFICATION_TYPE_ADD_ENTRY,
				UserNotificationDeliveryConstants.TYPE_EMAIL)) {

			return;
		}

		try {
			CTCollectionEmailConfiguration ctCollectionEmailConfiguration =
				_configurationProvider.getCompanyConfiguration(
					CTCollectionEmailConfiguration.class,
					themeDisplay.getCompanyId());

			User user = themeDisplay.getUser();

			String fromName = ctCollectionEmailConfiguration.emailFromName();

			if (Validator.isNull(fromName)) {
				fromName = user.getFullName();
			}

			String fromAddress =
				ctCollectionEmailConfiguration.emailFromAddress();

			if (Validator.isNull(fromAddress)) {
				fromAddress = user.getEmailAddress();
			}

			User receiverUser = _userLocalService.getUser(receiverUserId);

			String toName = receiverUser.getFullName();
			String toAddress = receiverUser.getEmailAddress();

			CTCollection ctCollection =
				_ctCollectionLocalService.getCTCollection(ctCollectionId);

			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			SubscriptionSender subscriptionSender = new SubscriptionSender();

			subscriptionSender.setContextAttribute(
				"[$PORTAL_PUBLICATION_REVIEW_CHANGES_URL$]",
				PortletURLBuilder.create(
					_portal.getControlPanelPortletURL(
						serviceContext.getRequest(),
						serviceContext.getScopeGroup(),
						CTPortletKeys.PUBLICATIONS, 0, 0,
						PortletRequest.RENDER_PHASE)
				).setMVCRenderCommandName(
					"/change_tracking/view_changes"
				).setParameter(
					"ctCollectionId", ctCollectionId
				).buildString(),
				false);
			subscriptionSender.setContextAttributes(
				"[$FROM_ADDRESS$]", fromAddress, "[$FROM_NAME$]", fromName,
				"[$PORTAL_URL$]", themeDisplay.getPortalURL(), "[$TO_NAME$]",
				toName, "[$PUBLICATION_NAME$]", ctCollection.getName());
			subscriptionSender.setFrom(fromAddress, fromName);
			subscriptionSender.setHtmlFormat(true);
			subscriptionSender.setLocalizedBodyMap(
				_localization.getMap(
					ctCollectionEmailConfiguration.invitationEmailBody()));
			subscriptionSender.setLocalizedSubjectMap(
				_localization.getMap(
					ctCollectionEmailConfiguration.invitationEmailSubject()));
			subscriptionSender.setMailId("ctCollectionId", ctCollectionId);
			subscriptionSender.setNotificationType(
				UserNotificationDefinition.NOTIFICATION_TYPE_ADD_ENTRY);
			subscriptionSender.setServiceContext(serviceContext);

			subscriptionSender.addRuntimeSubscribers(toAddress, toName);

			subscriptionSender.flushNotificationsAsync();
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	private void _sendNotificationEvent(
			long ctCollectionId, long receiverUserId, int roleValue,
			ThemeDisplay themeDisplay)
		throws PortalException {

		if (!UserNotificationManagerUtil.isDeliver(
				receiverUserId, CTPortletKeys.PUBLICATIONS, 0,
				UserNotificationDefinition.NOTIFICATION_TYPE_ADD_ENTRY,
				UserNotificationDeliveryConstants.TYPE_WEBSITE)) {

			return;
		}

		User user = themeDisplay.getUser();

		NotificationEvent notificationEvent = new NotificationEvent(
			System.currentTimeMillis(), CTPortletKeys.PUBLICATIONS,
			JSONUtil.put(
				"classPK", ctCollectionId
			).put(
				"notificationType",
				UserNotificationDefinition.NOTIFICATION_TYPE_ADD_ENTRY
			).put(
				"roleValue", roleValue
			).put(
				"userId", user.getUserId()
			).put(
				"userName", user.getFullName()
			));

		notificationEvent.setDeliveryType(
			UserNotificationDeliveryConstants.TYPE_WEBSITE);

		_userNotificationEventLocalService.addUserNotificationEvent(
			receiverUserId, notificationEvent);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		InviteUsersMVCResourceCommand.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private CTCollectionLocalService _ctCollectionLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference
	private Localization _localization;

	@Reference
	private Portal _portal;

	@Reference
	private PublicationHelper _publicationHelper;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

}