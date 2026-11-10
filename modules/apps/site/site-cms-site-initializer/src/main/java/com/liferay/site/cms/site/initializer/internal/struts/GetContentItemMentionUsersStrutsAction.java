/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.struts;

import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.comment.Comment;
import com.liferay.portal.kernel.comment.CommentManager;
import com.liferay.portal.kernel.comment.DiscussionPermission;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.util.comparator.UserFirstNameComparator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mikel Lorza
 */
@Component(
	property = "path=/cms/get_content_item_mention_users",
	service = StrutsAction.class
)
public class GetContentItemMentionUsersStrutsAction implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!FeatureFlagManagerUtil.isEnabled(
				themeDisplay.getCompanyId(), "LPD-104086")) {

			throw new UnsupportedOperationException();
		}

		String className1 = ParamUtil.getString(
			httpServletRequest, "className", null);

		if (className1 == null) {
			long classNameId = ParamUtil.getLong(
				httpServletRequest, "classNameId");

			ClassName className2 = _classNameLocalService.getClassName(
				classNameId);

			className1 = className2.getClassName();
		}

		long classPK = ParamUtil.getLong(httpServletRequest, "classPK");

		_discussionPermission.checkAddPermission(
			themeDisplay.getPermissionChecker(), themeDisplay.getCompanyId(),
			themeDisplay.getScopeGroupId(), className1, classPK);

		List<Comment> comments = ListUtil.copy(
			_commentManager.getComments(
				className1, classPK, WorkflowConstants.STATUS_APPROVED,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS));

		Collections.reverse(comments);

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			classPK);

		String keywords = ParamUtil.getString(httpServletRequest, "keywords");

		Set<Long> recentUserIds = new HashSet<>();
		Set<Long> userIds = new HashSet<>();
		Map<Long, User> users = new LinkedHashMap<>();

		for (Comment comment : comments) {
			User user = comment.getUser();

			if (comment.isRoot() || (user == null) ||
				!_isMatch(keywords, user) || !userIds.add(user.getUserId()) ||
				!_groupLocalService.hasUserGroup(
					user.getUserId(), objectEntry.getGroupId(), true) ||
				!_isMentionable(className1, classPK, themeDisplay, user)) {

				continue;
			}

			recentUserIds.add(user.getUserId());
			users.put(user.getUserId(), user);

			if (users.size() == _MAX_USERS) {
				break;
			}
		}

		int start = 0;

		while ((users.size() < _MAX_USERS) && (start < _MAX_SEARCHED_USERS)) {
			List<User> groupUsers = _userLocalService.search(
				themeDisplay.getCompanyId(), keywords,
				WorkflowConstants.STATUS_APPROVED,
				LinkedHashMapBuilder.<String, Object>put(
					"inherit", Boolean.TRUE
				).put(
					"usersGroups", objectEntry.getGroupId()
				).build(),
				start, start + _MAX_USERS,
				UserFirstNameComparator.getInstance(true));

			for (User groupUser : groupUsers) {
				if (!userIds.add(groupUser.getUserId()) ||
					!_isMentionable(
						className1, classPK, themeDisplay, groupUser)) {

					continue;
				}

				users.put(groupUser.getUserId(), groupUser);

				if (users.size() == _MAX_USERS) {
					break;
				}
			}

			if (groupUsers.size() < _MAX_USERS) {
				break;
			}

			start += _MAX_USERS;
		}

		ServletResponseUtil.write(
			httpServletResponse,
			JSONUtil.toString(
				JSONUtil.put(
					"users",
					JSONUtil.toJSONArray(
						users.values(),
						user -> _getUserJSONObject(
							recentUserIds, themeDisplay, user)))));

		return null;
	}

	private JSONObject _getUserJSONObject(
			Set<Long> recentUserIds, ThemeDisplay themeDisplay, User user)
		throws PortalException {

		String portraitURL = StringPool.BLANK;

		if (user.getPortraitId() > 0) {
			portraitURL = user.getPortraitURL(themeDisplay);
		}

		return JSONUtil.put(
			"id", user.getExternalReferenceCode()
		).put(
			"name", user.getFullName()
		).put(
			"portraitURL", portraitURL
		).put(
			"recent", recentUserIds.contains(user.getUserId())
		).put(
			"screenName", user.getScreenName()
		);
	}

	private boolean _isMatch(String keywords, User user) {
		if (Validator.isNull(keywords)) {
			return true;
		}

		for (String keyword : StringUtil.split(keywords, ' ')) {
			if (StringUtil.startsWith(user.getFirstName(), keyword) ||
				StringUtil.startsWith(user.getMiddleName(), keyword) ||
				StringUtil.startsWith(user.getLastName(), keyword) ||
				StringUtil.startsWith(user.getScreenName(), keyword)) {

				return true;
			}
		}

		return false;
	}

	private boolean _isMentionable(
			String className, long classPK, ThemeDisplay themeDisplay,
			User user)
		throws Exception {

		if ((user.getUserId() == themeDisplay.getUserId()) ||
			!_discussionPermission.hasViewPermission(
				_permissionCheckerFactory.create(user),
				themeDisplay.getCompanyId(), themeDisplay.getScopeGroupId(),
				className, classPK)) {

			return false;
		}

		return true;
	}

	private static final int _MAX_SEARCHED_USERS = 100;

	private static final int _MAX_USERS = 20;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CommentManager _commentManager;

	@Reference
	private DiscussionPermission _discussionPermission;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Reference
	private UserLocalService _userLocalService;

}