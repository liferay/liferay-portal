/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemListBuilder;
import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.message.boards.settings.MBGroupServiceSettings;
import com.liferay.message.boards.web.internal.security.permission.MBResourcePermission;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;

/**
 * @author Adolfo Pérez
 */
public class MBNavigationDisplayContext {

	public MBNavigationDisplayContext(
		HttpServletRequest httpServletRequest,
		MBGroupServiceSettings mbGroupServiceSettings,
		RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_mbGroupServiceSettings = mbGroupServiceSettings;
		_renderResponse = renderResponse;

		_mvcRenderCommandName = ParamUtil.getString(
			httpServletRequest, "mvcRenderCommandName", "/message_boards/view");
		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<NavigationItem> getNavigationItems() {
		return NavigationItemListBuilder.add(
			navigationItem -> {
				navigationItem.setActive(
					_isViewCategoriesNavigationItemActive());
				navigationItem.setHref(_getViewCategoriesPortletURL());

				if (_isMBPortlet()) {
					navigationItem.setLabel(
						LanguageUtil.get(_httpServletRequest, "categories"));
				}
				else {
					navigationItem.setLabel(
						LanguageUtil.get(_httpServletRequest, "content"));
				}
			}
		).add(
			this::_isMBPortlet,
			navigationItem -> {
				navigationItem.setActive(
					_isViewRecentPostsNavigationItemActive());
				navigationItem.setHref(_getViewRecentPostsPortletURL());
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "recent-posts"));
			}
		).add(
			() -> _isMBPortlet() && _themeDisplay.isSignedIn(),
			navigationItem -> {
				navigationItem.setActive(_isViewMyPostsNavigationItemActive());
				navigationItem.setHref(_getViewMyPostsPortletURL());
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "my-posts"));
			}
		).add(
			() ->
				_isMBPortlet() && _themeDisplay.isSignedIn() &&
				(_mbGroupServiceSettings.isEmailMessageAddedEnabled() ||
				 _mbGroupServiceSettings.isEmailMessageUpdatedEnabled()),
			navigationItem -> {
				navigationItem.setActive(
					_isViewMySubscriptionsNavigationItemActive());
				navigationItem.setHref(_getViewMySubscriptionsPortletURL());
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "my-subscriptions"));
			}
		).add(
			navigationItem -> {
				navigationItem.setActive(
					_isViewStatisticsNavigationItemActive());
				navigationItem.setHref(_getViewStatisticsPortletURL());
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "statistics"));
			}
		).add(
			() ->
				!_isMBPortlet() &&
				MBResourcePermission.contains(
					_themeDisplay.getPermissionChecker(),
					_themeDisplay.getScopeGroupId(), ActionKeys.BAN_USER),
			navigationItem -> {
				navigationItem.setActive(
					_isViewBannedUsersNavigationItemActive());
				navigationItem.setHref(_getViewBannedUsersPortletURL());
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "banned-users"));
			}
		).build();
	}

	public boolean isInverted() {
		return !_isMBPortlet();
	}

	public boolean isShowAlert() {
		return _isMBPortlet();
	}

	private PortletURL _getViewBannedUsersPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCRenderCommandName(
			"/message_boards_admin/view_banned_users"
		).buildPortletURL();
	}

	private PortletURL _getViewCategoriesPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCRenderCommandName(
			"/message_boards/view"
		).setParameter(
			"tag", StringPool.BLANK
		).buildPortletURL();
	}

	private PortletURL _getViewMyPostsPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCRenderCommandName(
			"/message_boards/view_my_posts"
		).buildPortletURL();
	}

	private PortletURL _getViewMySubscriptionsPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCRenderCommandName(
			"/message_boards/view_my_subscriptions"
		).buildPortletURL();
	}

	private PortletURL _getViewRecentPostsPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCRenderCommandName(
			"/message_boards/view_recent_posts"
		).buildPortletURL();
	}

	private PortletURL _getViewStatisticsPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCRenderCommandName(
			"/message_boards/view_statistics"
		).buildPortletURL();
	}

	private boolean _isMBPortlet() {
		PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

		return Objects.equals(
			portletDisplay.getPortletName(), MBPortletKeys.MESSAGE_BOARDS);
	}

	private boolean _isViewBannedUsersNavigationItemActive() {
		return _mvcRenderCommandName.equals(
			"/message_boards_admin/view_banned_users");
	}

	private boolean _isViewCategoriesNavigationItemActive() {
		if (_mvcRenderCommandName.equals("/message_boards/edit_category") ||
			_mvcRenderCommandName.equals("/message_boards/edit_message") ||
			_mvcRenderCommandName.equals("/message_boards/view") ||
			_mvcRenderCommandName.equals("/message_boards/view_category") ||
			_mvcRenderCommandName.equals("/message_boards/view_message")) {

			return true;
		}

		return false;
	}

	private boolean _isViewMyPostsNavigationItemActive() {
		return _mvcRenderCommandName.equals("/message_boards/view_my_posts");
	}

	private boolean _isViewMySubscriptionsNavigationItemActive() {
		return _mvcRenderCommandName.equals(
			"/message_boards/view_my_subscriptions");
	}

	private boolean _isViewRecentPostsNavigationItemActive() {
		return _mvcRenderCommandName.equals(
			"/message_boards/view_recent_posts");
	}

	private boolean _isViewStatisticsNavigationItemActive() {
		return _mvcRenderCommandName.equals("/message_boards/view_statistics");
	}

	private final HttpServletRequest _httpServletRequest;
	private final MBGroupServiceSettings _mbGroupServiceSettings;
	private final String _mvcRenderCommandName;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}