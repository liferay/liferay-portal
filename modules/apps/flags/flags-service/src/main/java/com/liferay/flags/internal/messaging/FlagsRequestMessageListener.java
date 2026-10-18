/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.flags.internal.messaging;

import com.liferay.flags.configuration.FlagsGroupServiceConfiguration;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.DestinationConfiguration;
import com.liferay.portal.kernel.messaging.DestinationFactory;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroupGroupRole;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.SubscriptionSender;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Julio Camarero
 * @author Michael C. Han
 * @author Brian Wing Shun Chan
 * @author Peter Fellwock
 */
@Component(
	property = "destination.name=" + DestinationNames.FLAGS,
	service = MessageListener.class
)
public class FlagsRequestMessageListener extends BaseMessageListener {

	@Activate
	protected void activate(BundleContext bundleContext) {
		DestinationConfiguration destinationConfiguration =
			new DestinationConfiguration(
				DestinationConfiguration.DESTINATION_TYPE_PARALLEL,
				DestinationNames.FLAGS);

		Destination destination = _destinationFactory.createDestination(
			destinationConfiguration);

		_destinationServiceRegistration = bundleContext.registerService(
			Destination.class, destination,
			MapUtil.singletonDictionary(
				"destination.name", destination.getName()));
	}

	@Deactivate
	protected void deactivate() {
		_destinationServiceRegistration.unregister();
	}

	@Override
	protected void doReceive(Message message) throws Exception {
		FlagsRequest flagsRequest = (FlagsRequest)message.getPayload();

		// Service context

		ServiceContext serviceContext = flagsRequest.getServiceContext();

		// Company

		long companyId = serviceContext.getCompanyId();

		// Group

		Layout layout = _layoutLocalService.getLayout(serviceContext.getPlid());

		Group group = layout.getGroup();

		// Reporter user

		String reporterUserName = null;
		String reporterEmailAddress = null;

		User reporterUser = _userLocalService.getUserById(
			serviceContext.getUserId());

		Locale locale = LocaleUtil.getDefault();

		if (reporterUser.isGuestUser()) {
			reporterUserName = _language.get(locale, "anonymous");
		}
		else {
			reporterUserName = reporterUser.getFullName();
			reporterEmailAddress = reporterUser.getEmailAddress();
		}

		// Reported user

		String reportedUserName = StringPool.BLANK;
		String reportedEmailAddress = StringPool.BLANK;
		String reportedURL = StringPool.BLANK;

		User reportedUser = _userLocalService.getUserById(
			flagsRequest.getReportedUserId());

		if (reportedUser.isGuestUser()) {
			reportedUserName = group.getDescriptiveName();
		}
		else {
			reportedUserName = reportedUser.getFullName();
			reportedEmailAddress = reportedUser.getEmailAddress();
			reportedURL = flagsRequest.getReportedUserURL();
		}

		// Content

		String contentType = ResourceActionsUtil.getModelResource(
			locale, flagsRequest.getClassName());

		// Reason

		String reason = _language.get(locale, flagsRequest.getReason());

		// Email

		FlagsGroupServiceConfiguration flagsGroupServiceConfiguration =
			_configurationProvider.getCompanyConfiguration(
				FlagsGroupServiceConfiguration.class, companyId);

		String fromName = flagsGroupServiceConfiguration.emailFromName();
		String fromAddress = flagsGroupServiceConfiguration.emailFromAddress();

		String subject = StringUtil.read(
			FlagsRequestMessageListener.class.getClassLoader(),
			flagsGroupServiceConfiguration.emailSubject());
		String body = StringUtil.read(
			FlagsRequestMessageListener.class.getClassLoader(),
			flagsGroupServiceConfiguration.emailBody());

		// Users

		Set<User> users = _getRecipients(
			companyId, serviceContext.getScopeGroupId());

		for (User user : users) {
			try {
				_notify(
					reporterUser.getUserId(), group, reporterEmailAddress,
					reporterUserName, reportedEmailAddress, reportedUserName,
					reportedURL, flagsRequest.getClassPK(),
					flagsRequest.getContentTitle(), contentType,
					flagsRequest.getContentURL(), reason, fromName, fromAddress,
					user.getFullName(), user.getEmailAddress(), subject, body,
					serviceContext);
			}
			catch (IOException ioException) {
				if (_log.isWarnEnabled()) {
					_log.warn(ioException);
				}
			}
		}
	}

	private String _getGroupDescriptiveName(Group group, Locale locale) {
		try {
			return group.getDescriptiveName(locale);
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to get descriptive name for group " +
					group.getGroupId(),
				portalException);
		}

		return StringPool.BLANK;
	}

	private Set<User> _getRecipients(long companyId, long groupId)
		throws Exception {

		Set<User> users = new LinkedHashSet<>();

		List<String> roleNames = new ArrayList<>();

		Group group = _groupLocalService.getGroup(groupId);

		if (group.isSite()) {
			roleNames.add(RoleConstants.SITE_ADMINISTRATOR);
			roleNames.add(RoleConstants.SITE_OWNER);
		}

		if (group.isCompany()) {
			roleNames.add(RoleConstants.ADMINISTRATOR);
		}
		else if (group.isOrganization()) {
			roleNames.add(RoleConstants.ORGANIZATION_ADMINISTRATOR);
			roleNames.add(RoleConstants.ORGANIZATION_OWNER);
		}

		for (String roleName : roleNames) {
			Role role = _roleLocalService.getRole(companyId, roleName);

			List<UserGroupRole> userGroupRoles =
				_userGroupRoleLocalService.getUserGroupRolesByGroupAndRole(
					groupId, role.getRoleId());

			for (UserGroupRole userGroupRole : userGroupRoles) {
				users.add(userGroupRole.getUser());
			}

			List<UserGroupGroupRole> userGroupGroupRoles =
				_userGroupGroupRoleLocalService.
					getUserGroupGroupRolesByGroupAndRole(
						groupId, role.getRoleId());

			for (UserGroupGroupRole userGroupGroupRole : userGroupGroupRoles) {
				users.addAll(
					_userLocalService.getUserGroupUsers(
						userGroupGroupRole.getUserGroupId()));
			}
		}

		if (users.isEmpty()) {
			Role role = _roleLocalService.getRole(
				companyId, RoleConstants.ADMINISTRATOR);

			users.addAll(_userLocalService.getRoleUsers(role.getRoleId()));
		}

		return users;
	}

	private void _notify(
			long reporterUserId, Group group, String reporterEmailAddress,
			String reporterUserName, String reportedEmailAddress,
			String reportedUserName, String reportedUserURL, long contentId,
			String contentTitle, String contentType, String contentURL,
			String reason, String fromName, String fromAddress, String toName,
			String toAddress, String subject, String body,
			ServiceContext serviceContext)
		throws Exception {

		Date date = new Date();

		SubscriptionSender subscriptionSender = new SubscriptionSender();

		subscriptionSender.setBody(body);
		subscriptionSender.setContextAttributes(
			"[$CONTENT_ID$]", contentId, "[$CONTENT_TITLE$]", contentTitle,
			"[$CONTENT_TYPE$]", contentType, "[$CONTENT_URL$]", contentURL,
			"[$DATE$]", date.toString(), "[$REASON$]", reason,
			"[$REPORTED_USER_ADDRESS$]", reportedEmailAddress,
			"[$REPORTED_USER_NAME$]", reportedUserName, "[$REPORTED_USER_URL$]",
			reportedUserURL, "[$REPORTER_USER_ADDRESS$]", reporterEmailAddress,
			"[$REPORTER_USER_NAME$]", reporterUserName);
		subscriptionSender.setCreatorUserId(reporterUserId);
		subscriptionSender.setFrom(fromAddress, fromName);
		subscriptionSender.setHtmlFormat(true);
		subscriptionSender.setLocalizedContextAttributeWithFunction(
			"[$SITE_NAME$]", locale -> _getGroupDescriptiveName(group, locale));
		subscriptionSender.setMailId("flags_request", contentId);
		subscriptionSender.setPortletId(PortletKeys.FLAGS);
		subscriptionSender.setServiceContext(serviceContext);
		subscriptionSender.setSubject(subject);

		subscriptionSender.addRuntimeSubscribers(toAddress, toName);

		subscriptionSender.flushNotificationsAsync();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FlagsRequestMessageListener.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private DestinationFactory _destinationFactory;

	private ServiceRegistration<Destination> _destinationServiceRegistration;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserGroupGroupRoleLocalService _userGroupGroupRoleLocalService;

	@Reference
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Reference
	private UserLocalService _userLocalService;

}