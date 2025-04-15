/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.blogs.internal.item.action;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.content.dashboard.item.action.ContentDashboardItemAction;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

/**
 * @author Cristina González
 */
public class ViewBlogsEntryContentDashboardItemAction
	implements ContentDashboardItemAction {

	public ViewBlogsEntryContentDashboardItemAction(
		AssetDisplayPageFriendlyURLProvider assetDisplayPageFriendlyURLProvider,
		BlogsEntry blogsEntry, HttpServletRequest httpServletRequest,
		Language language) {

		_assetDisplayPageFriendlyURLProvider =
			assetDisplayPageFriendlyURLProvider;
		_blogsEntry = blogsEntry;
		_httpServletRequest = httpServletRequest;
		_language = language;
	}

	@Override
	public String getIcon() {
		return "view";
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "view");
	}

	@Override
	public String getName() {
		return "view";
	}

	@Override
	public Type getType() {
		return Type.VIEW;
	}

	@Override
	public String getURL() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return _getViewURL(themeDisplay.getLocale(), themeDisplay);
	}

	@Override
	public String getURL(Locale locale) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return _getViewURL(locale, themeDisplay);
	}

	private String _getViewURL(Locale locale, ThemeDisplay themeDisplay) {
		try {
			ThemeDisplay clonedThemeDisplay =
				(ThemeDisplay)themeDisplay.clone();

			clonedThemeDisplay.setScopeGroupId(_blogsEntry.getGroupId());

			String url = _assetDisplayPageFriendlyURLProvider.getFriendlyURL(
				new InfoItemReference(
					BlogsEntry.class.getName(),
					new ClassPKInfoItemIdentifier(_blogsEntry.getEntryId())),
				locale, clonedThemeDisplay);

			if (url == null) {
				return StringPool.BLANK;
			}

			String backURL = ParamUtil.getString(
				_httpServletRequest, "backURL");

			PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

			if (Validator.isNotNull(backURL)) {
				return HttpComponentsUtil.addParameters(
					url, "p_l_back_url", backURL, "p_l_back_url_title",
					portletDisplay.getPortletDisplayName());
			}

			return HttpComponentsUtil.addParameters(
				url, "p_l_back_url", themeDisplay.getURLCurrent(),
				"p_l_back_url_title", portletDisplay.getPortletDisplayName());
		}
		catch (CloneNotSupportedException | PortalException exception) {
			_log.error(exception);

			return StringPool.BLANK;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ViewBlogsEntryContentDashboardItemAction.class);

	private final AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;
	private final BlogsEntry _blogsEntry;
	private final HttpServletRequest _httpServletRequest;
	private final Language _language;

}