/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mentions.internal.util;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.mentions.constants.MentionsConstants;
import com.liferay.mentions.constants.MentionsPortletKeys;
import com.liferay.mentions.matcher.MentionsMatcher;
import com.liferay.mentions.util.MentionsNotifier;
import com.liferay.mentions.util.MentionsUserFinder;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.permission.LayoutPermission;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.SubscriptionSender;
import com.liferay.social.kernel.util.SocialInteractionsConfiguration;
import com.liferay.social.kernel.util.SocialInteractionsConfigurationUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sergio González
 */
@Component(service = MentionsNotifier.class)
public class DefaultMentionsNotifier implements MentionsNotifier {

	public MentionsMatcher getMentionsMatcher(String className) {
		MentionsMatcher mentionsMatcher = _serviceTrackerMap.getService(
			className);

		if (mentionsMatcher != null) {
			return mentionsMatcher;
		}

		return _defaultMentionsMatcher;
	}

	@Override
	public void notify(
			long userId, long groupId, String title, String content,
			String className, long classPK,
			LocalizedValuesMap subjectLocalizedValuesMap,
			LocalizedValuesMap bodyLocalizedValuesMap,
			ServiceContext serviceContext)
		throws PortalException {

		String[] mentionedUsersScreenNames = _getMentionedUsersScreenNames(
			userId, className, content);

		if (ArrayUtil.isEmpty(mentionedUsersScreenNames)) {
			return;
		}

		User user = _userLocalService.getUser(userId);

		String contentURL = (String)serviceContext.getAttribute("contentURL");

		String messageUserEmailAddress = _portal.getUserEmailAddress(userId);
		String messageUserName = _portal.getUserName(userId, StringPool.BLANK);

		String fromName = PrefsPropsUtil.getString(
			user.getCompanyId(), PropsKeys.ADMIN_EMAIL_FROM_NAME);
		String fromAddress = PrefsPropsUtil.getString(
			user.getCompanyId(), PropsKeys.ADMIN_EMAIL_FROM_ADDRESS);

		SubscriptionSender subscriptionSender = new SubscriptionSender();

		subscriptionSender.setLocalizedBodyMap(
			_localization.getMap(bodyLocalizedValuesMap));
		subscriptionSender.setClassName(className);
		subscriptionSender.setClassPK(classPK);
		subscriptionSender.setContextAttribute("[$CONTENT$]", content, false);
		subscriptionSender.setContextAttributes(
			"[$USER_ADDRESS$]", messageUserEmailAddress, "[$USER_NAME$]",
			messageUserName, "[$CONTENT_URL$]", contentURL);
		subscriptionSender.setCurrentUserId(userId);
		subscriptionSender.setEntryTitle(title);
		subscriptionSender.setEntryURL(contentURL);
		subscriptionSender.setFrom(fromAddress, fromName);
		subscriptionSender.setHtmlFormat(true);
		subscriptionSender.setLocalizedContextAttributeWithFunction(
			"[$ASSET_ENTRY_NAME$]",
			locale -> _getAssetEntryName(className, locale));
		subscriptionSender.setMailId("mb_discussion", classPK);
		subscriptionSender.setNotificationType(
			MentionsConstants.NOTIFICATION_TYPE_MENTION);
		subscriptionSender.setPortletId(MentionsPortletKeys.MENTIONS);
		subscriptionSender.setScopeGroupId(groupId);
		subscriptionSender.setServiceContext(serviceContext);
		subscriptionSender.setLocalizedSubjectMap(
			_localization.getMap(subjectLocalizedValuesMap));

		for (String mentionedUserScreenName : mentionedUsersScreenNames) {
			User mentionedUser = _userLocalService.fetchUserByScreenName(
				user.getCompanyId(), mentionedUserScreenName);

			if (mentionedUser == null) {
				continue;
			}

			ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

			if (themeDisplay != null) {
				Layout layout = themeDisplay.getLayout();

				if (layout != null) {
					PermissionChecker permissionChecker =
						_permissionCheckerFactory.create(mentionedUser);

					if (!_layoutPermission.contains(
							permissionChecker, layout, true, ActionKeys.VIEW) ||
						!PortletPermissionUtil.contains(
							permissionChecker, layout, themeDisplay.getPpid(),
							ActionKeys.VIEW)) {

						continue;
					}
				}
			}

			subscriptionSender.addRuntimeSubscribers(
				mentionedUser.getEmailAddress(), mentionedUser.getFullName());
		}

		subscriptionSender.flushNotificationsAsync();
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, MentionsMatcher.class, "model.class.name");
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private String _getAssetEntryName(String className, Locale locale) {
		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				className);

		if (assetRendererFactory != null) {
			return assetRendererFactory.getTypeName(locale);
		}

		return StringPool.BLANK;
	}

	private String[] _getMentionedUsersScreenNames(
			long userId, String className, String content)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		SocialInteractionsConfiguration socialInteractionsConfiguration =
			SocialInteractionsConfigurationUtil.
				getSocialInteractionsConfiguration(
					user.getCompanyId(), MentionsPortletKeys.MENTIONS);

		Set<String> mentionedUsersScreenNames = new HashSet<>();

		MentionsMatcher mentionsMatcher = getMentionsMatcher(className);

		for (String mentionedUserScreenName : mentionsMatcher.match(content)) {
			List<User> users = _mentionsUserFinder.getUsers(
				user.getCompanyId(), userId, mentionedUserScreenName,
				socialInteractionsConfiguration);

			for (User curUser : users) {
				if (mentionedUserScreenName.equals(curUser.getScreenName())) {
					mentionedUsersScreenNames.add(mentionedUserScreenName);

					break;
				}
			}
		}

		return mentionedUsersScreenNames.toArray(new String[0]);
	}

	private static final MentionsMatcher _defaultMentionsMatcher =
		new DefaultMentionsMatcher();

	@Reference
	private LayoutPermission _layoutPermission;

	@Reference
	private Localization _localization;

	@Reference
	private MentionsUserFinder _mentionsUserFinder;

	@Reference
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Reference
	private Portal _portal;

	private ServiceTrackerMap<String, MentionsMatcher> _serviceTrackerMap;

	@Reference
	private UserLocalService _userLocalService;

}