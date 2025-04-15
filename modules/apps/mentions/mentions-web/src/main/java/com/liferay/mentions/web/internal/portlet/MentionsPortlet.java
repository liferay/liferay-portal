/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mentions.web.internal.portlet;

import com.liferay.mentions.constants.MentionsPortletKeys;
import com.liferay.mentions.strategy.MentionsStrategy;
import com.liferay.mentions.util.MentionsUtil;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.permission.LayoutPermission;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.user.taglib.servlet.taglib.UserPortraitTag;

import jakarta.portlet.Portlet;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Iván Zaera
 * @author Sergio González
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.icon=/icons/mentions.png",
		"jakarta.portlet.display-name=Mentions",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.name=" + MentionsPortletKeys.MENTIONS,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=administrator",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class MentionsPortlet extends MVCPortlet {

	@Override
	public void serveResource(
		ResourceRequest resourceRequest, ResourceResponse resourceResponse) {

		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)resourceRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			if (!MentionsUtil.isMentionsEnabled(
					themeDisplay.getSiteGroupId())) {

				return;
			}

			JSONArray jsonArray = _getJSONArray(
				_getSupplier(
					themeDisplay,
					ParamUtil.getString(resourceRequest, "strategy"),
					ParamUtil.getString(resourceRequest, "query"),
					ParamUtil.getString(
						resourceRequest, "discussionPortletId")),
				themeDisplay);

			HttpServletResponse httpServletResponse =
				_portal.getHttpServletResponse(resourceResponse);

			httpServletResponse.setContentType(ContentTypes.APPLICATION_JSON);

			ServletResponseUtil.write(
				httpServletResponse, jsonArray.toString());
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, MentionsStrategy.class, "mentions.strategy");
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private JSONArray _getJSONArray(
			Supplier<List<User>> usersSupplier, ThemeDisplay themeDisplay)
		throws PortalException {

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		for (User user : usersSupplier.get()) {
			if (user.isGuestUser() ||
				(themeDisplay.getUserId() == user.getUserId())) {

				continue;
			}

			String mention = "@" + HtmlUtil.escape(user.getScreenName());

			String profileURL = user.getDisplayURL(themeDisplay);

			if (Validator.isNotNull(profileURL)) {
				mention = StringBundler.concat(
					"<a href=\"", profileURL, "\">@",
					HtmlUtil.escape(user.getScreenName()), "</a>");
			}

			jsonArray.put(
				JSONUtil.put(
					"fullName", HtmlUtil.escape(user.getFullName())
				).put(
					"mention", mention
				).put(
					"portraitHTML",
					UserPortraitTag.getUserPortraitHTML(
						StringPool.BLANK, user, themeDisplay)
				).put(
					"screenName", HtmlUtil.escape(user.getScreenName())
				));
		}

		return jsonArray;
	}

	private JSONObject _getJSONObject(String strategyString)
		throws PortalException {

		if ((strategyString == null) || strategyString.isEmpty()) {
			return JSONUtil.put("strategy", "default");
		}

		JSONObject jsonObject = _jsonFactory.createJSONObject(strategyString);

		if (jsonObject.isNull("strategy")) {
			throw new PortalException(
				"Field \"strategy\" is missing in " + strategyString);
		}

		return jsonObject;
	}

	private Supplier<List<User>> _getSupplier(
			ThemeDisplay themeDisplay, String strategyString, String query,
			String discussionPortletId)
		throws PortalException {

		JSONObject jsonObject = _getJSONObject(strategyString);

		String strategy = jsonObject.getString("strategy");

		MentionsStrategy mentionsStrategy = _serviceTrackerMap.getService(
			strategy);

		if (mentionsStrategy == null) {
			throw new PortalException(
				"No mentions strategy is registered with " + strategy);
		}

		return () -> {
			try {
				return TransformUtil.transform(
					mentionsStrategy.getUsers(
						themeDisplay.getCompanyId(),
						themeDisplay.getSiteGroupId(), themeDisplay.getUserId(),
						query, jsonObject),
					user -> {
						PermissionChecker permissionChecker =
							PermissionCheckerFactoryUtil.create(user);

						Layout layout = themeDisplay.getLayout();

						if ((layout != null) &&
							_layoutPermission.contains(
								permissionChecker, layout, true,
								ActionKeys.VIEW) &&
							PortletPermissionUtil.contains(
								permissionChecker, layout, discussionPortletId,
								ActionKeys.VIEW)) {

							return user;
						}

						return null;
					});
			}
			catch (PortalException portalException) {
				_log.error(portalException);

				return Collections.emptyList();
			}
		};
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MentionsPortlet.class);

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private LayoutPermission _layoutPermission;

	@Reference
	private Portal _portal;

	private ServiceTrackerMap<String, MentionsStrategy> _serviceTrackerMap;

}