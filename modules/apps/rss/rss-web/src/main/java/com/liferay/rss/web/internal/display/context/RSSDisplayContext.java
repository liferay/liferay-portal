/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.rss.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.TabsItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.TabsItemListBuilder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.rss.web.internal.configuration.RSSPortletInstanceConfiguration;
import com.liferay.rss.web.internal.configuration.RSSWebCacheConfiguration;
import com.liferay.rss.web.internal.util.RSSFeed;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class RSSDisplayContext {

	public RSSDisplayContext(
			HttpServletRequest httpServletRequest,
			RSSWebCacheConfiguration rssWebCacheConfiguration)
		throws ConfigurationException {

		_httpServletRequest = httpServletRequest;
		_rssWebCacheConfiguration = rssWebCacheConfiguration;

		_rssPortletInstanceConfiguration =
			ConfigurationProviderUtil.getPortletInstanceConfiguration(
				RSSPortletInstanceConfiguration.class,
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY));

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public long getDisplayStyleGroupId() {
		if (_displayStyleGroupId != null) {
			return _displayStyleGroupId;
		}

		String displayStyleGroupExternalReferenceCode =
			_rssPortletInstanceConfiguration.
				displayStyleGroupExternalReferenceCode();

		Group group = _themeDisplay.getScopeGroup();

		if (Validator.isNotNull(displayStyleGroupExternalReferenceCode)) {
			group = GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
				displayStyleGroupExternalReferenceCode,
				_themeDisplay.getCompanyId());
		}

		if (group != null) {
			_displayStyleGroupId = group.getGroupId();
		}
		else {
			_displayStyleGroupId = _themeDisplay.getScopeGroupId();
		}

		return _displayStyleGroupId;
	}

	public String getDisplayStyleGroupKey() {
		if (Validator.isNotNull(_displayStyleGroupKey)) {
			return _displayStyleGroupKey;
		}

		String displayStyleGroupExternalReferenceCode =
			_rssPortletInstanceConfiguration.
				displayStyleGroupExternalReferenceCode();

		Group group = _themeDisplay.getScopeGroup();

		if (Validator.isNotNull(displayStyleGroupExternalReferenceCode)) {
			group = GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
				displayStyleGroupExternalReferenceCode,
				_themeDisplay.getCompanyId());
		}

		if (group != null) {
			_displayStyleGroupKey = group.getGroupKey();
		}
		else {
			_displayStyleGroupKey = StringPool.BLANK;
		}

		return _displayStyleGroupKey;
	}

	public List<RSSFeed> getRSSFeeds() {
		List<RSSFeed> rssFeeds = new ArrayList<>();

		String[] titles = _rssPortletInstanceConfiguration.titles();

		String[] urls = _rssPortletInstanceConfiguration.urls();

		for (int i = 0; i < urls.length; i++) {
			String url = urls[i];

			String title = StringPool.BLANK;

			if (i < titles.length) {
				title = titles[i];
			}

			rssFeeds.add(new RSSFeed(_rssWebCacheConfiguration, url, title));
		}

		return rssFeeds;
	}

	public RSSPortletInstanceConfiguration
		getRSSPortletInstanceConfiguration() {

		return _rssPortletInstanceConfiguration;
	}

	public List<TabsItem> getTabsItems() {
		return TabsItemListBuilder.add(
			tabsItem -> {
				tabsItem.setActive(true);
				tabsItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "feeds"));
			}
		).add(
			tabsItem -> tabsItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "display-settings"))
		).build();
	}

	public boolean isShowConfigurationLink() throws PortalException {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		return PortletPermissionUtil.contains(
			themeDisplay.getPermissionChecker(), themeDisplay.getLayout(),
			portletDisplay.getId(), ActionKeys.CONFIGURATION);
	}

	private Long _displayStyleGroupId;
	private String _displayStyleGroupKey;
	private final HttpServletRequest _httpServletRequest;
	private final RSSPortletInstanceConfiguration
		_rssPortletInstanceConfiguration;
	private final RSSWebCacheConfiguration _rssWebCacheConfiguration;
	private final ThemeDisplay _themeDisplay;

}