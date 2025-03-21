/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.microblogs.internal.util;

import com.liferay.microblogs.constants.MicroblogsEntryConstants;
import com.liferay.microblogs.constants.MicroblogsPortletKeys;
import com.liferay.microblogs.model.MicroblogsEntry;
import com.liferay.microblogs.service.MicroblogsEntryLocalServiceUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.notifications.UserNotificationManagerUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.comparator.UserFirstNameComparator;
import com.liferay.social.kernel.model.SocialRelationConstants;
import com.liferay.subscription.service.SubscriptionLocalServiceUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowState;
import jakarta.portlet.WindowStateException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jonathan Lee
 */
public class MicroblogsUtil {

	public static List<String> getHashtags(String content) {
		List<String> hashtags = new ArrayList<>();

		Matcher matcher = _hashtagPattern.matcher(content);

		while (matcher.find()) {
			String hashtag = matcher.group();

			hashtag = hashtag.substring(1);

			hashtags.add(hashtag);
		}

		return hashtags;
	}

	public static int getNotificationType(
			MicroblogsEntry microblogsEntry, long userId, int deliveryType)
		throws PortalException {

		if (isTaggedUser(
				microblogsEntry.getMicroblogsEntryId(), false, userId) &&
			UserNotificationManagerUtil.isDeliver(
				userId, MicroblogsPortletKeys.MICROBLOGS, 0,
				MicroblogsEntryConstants.NOTIFICATION_TYPE_TAG, deliveryType)) {

			return MicroblogsEntryConstants.NOTIFICATION_TYPE_TAG;
		}
		else if (microblogsEntry.getType() ==
					MicroblogsEntryConstants.TYPE_REPLY) {

			long rootMicroblogsEntryId = getRootMicroblogsEntryId(
				microblogsEntry);

			if ((getRootMicroblogsUserId(microblogsEntry) == userId) &&
				UserNotificationManagerUtil.isDeliver(
					userId, MicroblogsPortletKeys.MICROBLOGS, 0,
					MicroblogsEntryConstants.NOTIFICATION_TYPE_REPLY,
					deliveryType)) {

				return MicroblogsEntryConstants.NOTIFICATION_TYPE_REPLY;
			}
			else if (hasReplied(rootMicroblogsEntryId, userId) &&
					 UserNotificationManagerUtil.isDeliver(
						 userId, MicroblogsPortletKeys.MICROBLOGS, 0,
						 MicroblogsEntryConstants.
							 NOTIFICATION_TYPE_REPLY_TO_REPLIED,
						 deliveryType)) {

				return MicroblogsEntryConstants.
					NOTIFICATION_TYPE_REPLY_TO_REPLIED;
			}
			else if (isTaggedUser(rootMicroblogsEntryId, true, userId) &&
					 UserNotificationManagerUtil.isDeliver(
						 userId, MicroblogsPortletKeys.MICROBLOGS, 0,
						 MicroblogsEntryConstants.
							 NOTIFICATION_TYPE_REPLY_TO_TAGGED,
						 deliveryType)) {

				return MicroblogsEntryConstants.
					NOTIFICATION_TYPE_REPLY_TO_TAGGED;
			}
		}

		return MicroblogsEntryConstants.NOTIFICATION_TYPE_UNKNOWN;
	}

	public static String getProcessedContent(
			MicroblogsEntry microblogsEntry, ServiceContext serviceContext)
		throws PortalException {

		return getProcessedContent(
			microblogsEntry.getContent(), serviceContext);
	}

	public static String getProcessedContent(
			String content, ServiceContext serviceContext)
		throws PortalException {

		content = _replaceHashtags(content, serviceContext);

		content = _replaceUserTags(content, serviceContext);

		return content;
	}

	public static JSONArray getRecipientsJSONArray(
			long userId, ThemeDisplay themeDisplay)
		throws PortalException {

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		List<User> users = UserLocalServiceUtil.getSocialUsers(
			userId, SocialRelationConstants.TYPE_BI_CONNECTION,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			UserFirstNameComparator.getInstance(true));

		for (User user : users) {
			if (user.isGuestUser() || (userId == user.getUserId())) {
				continue;
			}

			jsonArray.put(
				JSONUtil.put(
					"emailAddress", user.getEmailAddress()
				).put(
					"fullName", user.getFullName()
				).put(
					"jobTitle", user.getJobTitle()
				).put(
					"portraitURL", user.getPortraitURL(themeDisplay)
				).put(
					"screenName", user.getScreenName()
				).put(
					"userId", user.getUserId()
				));
		}

		return jsonArray;
	}

	public static long getRootMicroblogsEntryId(
		MicroblogsEntry microblogsEntry) {

		if (microblogsEntry.getType() == MicroblogsEntryConstants.TYPE_REPOST) {
			return microblogsEntry.getMicroblogsEntryId();
		}

		return microblogsEntry.getParentMicroblogsEntryId();
	}

	public static long getRootMicroblogsUserId(MicroblogsEntry microblogsEntry)
		throws PortalException {

		if (microblogsEntry.getType() == MicroblogsEntryConstants.TYPE_REPOST) {
			return microblogsEntry.getUserId();
		}

		return microblogsEntry.getParentMicroblogsEntryUserId();
	}

	public static List<String> getScreenNames(String content) {
		List<String> screenNames = new ArrayList<>();

		Matcher matcher = _userTagPattern.matcher(content);

		while (matcher.find()) {
			String screenName = matcher.group();

			screenName = StringUtil.removeSubstring(screenName, "[@");
			screenName = StringUtil.replace(screenName, ']', StringPool.BLANK);

			screenNames.add(screenName);
		}

		return screenNames;
	}

	public static List<Long> getSubscriberUserIds(
		MicroblogsEntry microblogsEntry) {

		return TransformUtil.transform(
			SubscriptionLocalServiceUtil.getSubscriptions(
				microblogsEntry.getCompanyId(), MicroblogsEntry.class.getName(),
				getRootMicroblogsEntryId(microblogsEntry)),
			subscription -> {
				long userId = subscription.getUserId();

				if (microblogsEntry.getUserId() == userId) {
					return null;
				}

				return userId;
			});
	}

	public static boolean hasReplied(long parentMicroblogsEntryId, long userId)
		throws PortalException {

		List<MicroblogsEntry> microblogsEntries = new ArrayList<>();

		microblogsEntries.addAll(
			MicroblogsEntryLocalServiceUtil.
				getParentMicroblogsEntryMicroblogsEntries(
					MicroblogsEntryConstants.TYPE_REPLY,
					parentMicroblogsEntryId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS));

		microblogsEntries.add(
			MicroblogsEntryLocalServiceUtil.getMicroblogsEntry(
				parentMicroblogsEntryId));

		for (MicroblogsEntry microblogsEntry : microblogsEntries) {
			if (microblogsEntry.getUserId() == userId) {
				return true;
			}
		}

		return false;
	}

	public static boolean isTaggedUser(
			long microblogsEntryId, boolean checkParent, long userId)
		throws PortalException {

		MicroblogsEntry microblogsEntry =
			MicroblogsEntryLocalServiceUtil.fetchMicroblogsEntry(
				microblogsEntryId);

		if (microblogsEntry == null) {
			return false;
		}

		if (!checkParent) {
			return isTaggedUser(microblogsEntry, userId);
		}

		long rootMicroblogsEntryId = getRootMicroblogsEntryId(microblogsEntry);

		List<MicroblogsEntry> microblogsEntries = new ArrayList<>();

		microblogsEntries.addAll(
			MicroblogsEntryLocalServiceUtil.
				getParentMicroblogsEntryMicroblogsEntries(
					MicroblogsEntryConstants.TYPE_REPLY, rootMicroblogsEntryId,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS));

		microblogsEntries.add(
			MicroblogsEntryLocalServiceUtil.getMicroblogsEntry(
				rootMicroblogsEntryId));

		for (MicroblogsEntry curMicroblogsEntry : microblogsEntries) {
			if (isTaggedUser(curMicroblogsEntry, userId)) {
				return true;
			}
		}

		return false;
	}

	protected static boolean isTaggedUser(
			MicroblogsEntry microblogsEntry, long userId)
		throws PortalException {

		List<String> screenNames = getScreenNames(microblogsEntry.getContent());

		for (String screenName : screenNames) {
			long screenNameUserId = UserLocalServiceUtil.getUserIdByScreenName(
				microblogsEntry.getCompanyId(), screenName);

			if (screenNameUserId == userId) {
				return true;
			}
		}

		return false;
	}

	private static String _replaceHashtags(
			String content, ServiceContext serviceContext)
		throws PortalException {

		String escapedContent = HtmlUtil.escape(content);

		ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

		Matcher matcher = _hashtagPattern.matcher(content);

		while (matcher.find()) {
			String result = matcher.group();

			StringBundler sb = new StringBundler(6);

			sb.append("<span class=\"hashtag\">#</span>");
			sb.append("<a class=\"hashtag-link\" href=\"");

			PortletURL portletURL = null;

			Group group = GroupLocalServiceUtil.getUserGroup(
				themeDisplay.getCompanyId(), themeDisplay.getUserId());

			long portletPlid = PortalUtil.getPlidFromPortletId(
				group.getGroupId(), true, MicroblogsPortletKeys.MICROBLOGS);

			if (portletPlid != 0) {
				portletURL = PortletURLFactoryUtil.create(
					serviceContext.getLiferayPortletRequest(),
					MicroblogsPortletKeys.MICROBLOGS, portletPlid,
					PortletRequest.RENDER_PHASE);

				try {
					portletURL.setWindowState(LiferayWindowState.NORMAL);
				}
				catch (WindowStateException windowStateException) {
					if (_log.isDebugEnabled()) {
						_log.debug(windowStateException);
					}
				}
			}
			else {
				LiferayPortletResponse liferayPortletResponse =
					serviceContext.getLiferayPortletResponse();

				portletURL = liferayPortletResponse.createRenderURL(
					MicroblogsPortletKeys.MICROBLOGS);

				try {
					portletURL.setWindowState(WindowState.MAXIMIZED);
				}
				catch (WindowStateException windowStateException) {
					if (_log.isDebugEnabled()) {
						_log.debug(windowStateException);
					}
				}
			}

			portletURL.setParameter("mvcPath", "/microblogs/view.jsp");

			String assetTagName = result.substring(1);

			portletURL.setParameter("tabs1", assetTagName);
			portletURL.setParameter("assetTagName", assetTagName);

			sb.append(portletURL);

			sb.append("\">");
			sb.append(assetTagName);
			sb.append("</a>");

			String tagLink = sb.toString();

			escapedContent = StringUtil.replace(
				escapedContent, result, tagLink);
		}

		return escapedContent;
	}

	private static String _replaceUserTags(
			String content, ServiceContext serviceContext)
		throws PortalException {

		Matcher matcher = _userTagPattern.matcher(content);

		while (matcher.find()) {
			String result = matcher.group();

			try {
				StringBundler sb = new StringBundler(5);

				sb.append("<a href=\"");

				String assetTagScreenName = StringUtil.removeSubstring(
					result, "[@");

				assetTagScreenName = StringUtil.replace(
					assetTagScreenName, ']', StringPool.BLANK);

				ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

				User assetTagUser = UserLocalServiceUtil.getUserByScreenName(
					themeDisplay.getCompanyId(), assetTagScreenName);

				sb.append(assetTagUser.getDisplayURL(themeDisplay));

				sb.append("\">");

				String assetTagUserName = PortalUtil.getUserName(
					assetTagUser.getUserId(), assetTagScreenName);

				sb.append(assetTagUserName);

				sb.append("</a>");

				String userLink = sb.toString();

				content = StringUtil.replace(content, result, userLink);
			}
			catch (NoSuchUserException noSuchUserException) {
				if (_log.isDebugEnabled()) {
					_log.debug(noSuchUserException);
				}
			}
		}

		return content;
	}

	private static final Log _log = LogFactoryUtil.getLog(MicroblogsUtil.class);

	private static final Pattern _hashtagPattern = Pattern.compile(
		"\\#[a-zA-Z]\\w*");
	private static final Pattern _userTagPattern = Pattern.compile(
		"\\[\\@\\S*\\]");

}