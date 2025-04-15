/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.microblogs.web.internal.util;

import com.liferay.microblogs.constants.MicroblogsPortletKeys;
import com.liferay.microblogs.model.MicroblogsEntry;
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
public class MicroblogsWebUtil {

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

	private static final Log _log = LogFactoryUtil.getLog(
		MicroblogsWebUtil.class);

	private static final Pattern _hashtagPattern = Pattern.compile(
		"\\#[a-zA-Z]\\w*");
	private static final Pattern _userTagPattern = Pattern.compile(
		"\\[\\@\\S*\\]");

}