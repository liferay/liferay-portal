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
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.service.MBMessageLocalService;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.comment.DiscussionPermission;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
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
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.social.kernel.util.SocialInteractionsConfiguration;
import com.liferay.social.kernel.util.SocialInteractionsConfigurationUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

		Set<String> mentionedUsersScreenNames = _getMentionedUsersScreenNames(
			userId, className, content);

		String discussionClassName = null;
		long discussionClassPK = 0;

		if ((serviceContext.getAttribute("discussionClassNameId") instanceof
				Long discussionClassNameId) &&
			(serviceContext.getAttribute("discussionClassPK") instanceof
				Long curDiscussionClassPK)) {

			discussionClassName = _portal.getClassName(discussionClassNameId);
			discussionClassPK = curDiscussionClassPK;

			String previousBody = (String)serviceContext.getAttribute(
				"previousBody");

			if (previousBody != null) {
				mentionedUsersScreenNames.removeAll(
					_getMentionedUsersScreenNames(
						userId, className, previousBody));
			}
		}

		if (mentionedUsersScreenNames.isEmpty()) {
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

		MentionsSubscriptionSender mentionsSubscriptionSender =
			new MentionsSubscriptionSender();

		mentionsSubscriptionSender.setLocalizedBodyMap(
			_localization.getMap(bodyLocalizedValuesMap));
		mentionsSubscriptionSender.setClassName(className);
		mentionsSubscriptionSender.setClassPK(classPK);
		mentionsSubscriptionSender.setContextAttribute(
			"[$CONTENT$]", content, false);
		mentionsSubscriptionSender.setContextAttributes(
			"[$USER_ADDRESS$]", messageUserEmailAddress, "[$USER_NAME$]",
			messageUserName, "[$CONTENT_URL$]", contentURL);
		mentionsSubscriptionSender.setCurrentUserId(userId);
		mentionsSubscriptionSender.setDiscussionClassName(discussionClassName);
		mentionsSubscriptionSender.setDiscussionClassPK(discussionClassPK);
		mentionsSubscriptionSender.setEntryTitle(title);
		mentionsSubscriptionSender.setEntryURL(contentURL);
		mentionsSubscriptionSender.setFrom(fromAddress, fromName);
		mentionsSubscriptionSender.setHtmlFormat(true);
		mentionsSubscriptionSender.setLocalizedContextAttributeWithFunction(
			"[$ASSET_ENTRY_NAME$]",
			locale -> _getAssetEntryName(className, locale));
		mentionsSubscriptionSender.setMailId("mb_discussion", classPK);
		mentionsSubscriptionSender.setNotificationType(
			MentionsConstants.NOTIFICATION_TYPE_MENTION);
		mentionsSubscriptionSender.setPortletId(MentionsPortletKeys.MENTIONS);
		mentionsSubscriptionSender.setScopeGroupId(groupId);
		mentionsSubscriptionSender.setServiceContext(serviceContext);
		mentionsSubscriptionSender.setLocalizedSubjectMap(
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

					if (FeatureFlagManagerUtil.isEnabled(
							user.getCompanyId(), "LPD-104086") &&
						Validator.isNull(themeDisplay.getPpid())) {

						if (!_hasDiscussionViewPermission(
								classPK, permissionChecker)) {

							continue;
						}
					}
					else if (!_layoutPermission.contains(
								permissionChecker, layout, true,
								ActionKeys.VIEW) ||
							 !PortletPermissionUtil.contains(
								 permissionChecker, layout,
								 themeDisplay.getPpid(), ActionKeys.VIEW)) {

						continue;
					}
				}
			}

			mentionsSubscriptionSender.addRuntimeSubscribers(
				mentionedUser.getEmailAddress(), mentionedUser.getFullName());
		}

		mentionsSubscriptionSender.flushNotificationsAsync();
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

	private Set<String> _getMentionedUsersScreenNames(
			long userId, String className, String content)
		throws PortalException {

		Set<String> mentionedUsersScreenNames = new HashSet<>();

		User user = _userLocalService.getUser(userId);

		SocialInteractionsConfiguration socialInteractionsConfiguration =
			SocialInteractionsConfigurationUtil.
				getSocialInteractionsConfiguration(
					user.getCompanyId(), MentionsPortletKeys.MENTIONS);

		if (FeatureFlagManagerUtil.isEnabled(
				user.getCompanyId(), "LPD-104086")) {

			Matcher matcher = _mentionUserPattern.matcher(content);

			while (matcher.find()) {
				User mentionedUser =
					_userLocalService.fetchUserByExternalReferenceCode(
						matcher.group(1), user.getCompanyId());

				if (mentionedUser == null) {
					continue;
				}

				mentionedUsersScreenNames.add(mentionedUser.getScreenName());
			}

			content = matcher.replaceAll(StringPool.BLANK);
		}

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

		return mentionedUsersScreenNames;
	}

	private boolean _hasDiscussionViewPermission(
			long messageId, PermissionChecker permissionChecker)
		throws PortalException {

		MBMessage mbMessage = _mbMessageLocalService.fetchMBMessage(messageId);

		if ((mbMessage == null) || !mbMessage.isDiscussion()) {
			return false;
		}

		return _discussionPermission.hasViewPermission(
			permissionChecker, mbMessage.getCompanyId(), mbMessage.getGroupId(),
			mbMessage.getClassName(), mbMessage.getClassPK());
	}

	private static final MentionsMatcher _defaultMentionsMatcher =
		new DefaultMentionsMatcher();
	private static final Pattern _mentionUserPattern = Pattern.compile(
		"<span\\s+class=\"mention mention-user-([^\"\\s]+)\">[^<]*</span>");

	@Reference
	private DiscussionPermission _discussionPermission;

	@Reference
	private LayoutPermission _layoutPermission;

	@Reference
	private Localization _localization;

	@Reference
	private MBMessageLocalService _mbMessageLocalService;

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