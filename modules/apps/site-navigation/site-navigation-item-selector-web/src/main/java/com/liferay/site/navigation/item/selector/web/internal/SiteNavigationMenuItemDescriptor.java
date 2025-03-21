/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.item.selector.web.internal;

import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.navigation.model.SiteNavigationMenu;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;
import java.util.Locale;

/**
 * @author Eudaldo Alonso
 */
public class SiteNavigationMenuItemDescriptor
	implements ItemSelectorViewDescriptor.ItemDescriptor {

	public SiteNavigationMenuItemDescriptor(
		SiteNavigationMenu siteNavigationMenu,
		HttpServletRequest httpServletRequest) {

		_siteNavigationMenu = siteNavigationMenu;
		_httpServletRequest = httpServletRequest;
	}

	@Override
	public String getIcon() {
		return "pages-tree";
	}

	@Override
	public String getImageURL() {
		return null;
	}

	@Override
	public Date getModifiedDate() {
		return _siteNavigationMenu.getModifiedDate();
	}

	@Override
	public String getPayload() {
		return JSONUtil.put(
			"id", _siteNavigationMenu.getSiteNavigationMenuId()
		).put(
			"name", _getName()
		).toString();
	}

	@Override
	public String getSubtitle(Locale locale) {
		return StringPool.BLANK;
	}

	@Override
	public String getTitle(Locale locale) {
		return _getName();
	}

	@Override
	public long getUserId() {
		return _siteNavigationMenu.getUserId();
	}

	@Override
	public String getUserName() {
		return _siteNavigationMenu.getUserName();
	}

	@Override
	public boolean isCompact() {
		return true;
	}

	private String _getName() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (_siteNavigationMenu.getGroupId() ==
				themeDisplay.getScopeGroupId()) {

			return _siteNavigationMenu.getName();
		}

		Group group = GroupLocalServiceUtil.fetchGroup(
			_siteNavigationMenu.getGroupId());

		if (group == null) {
			return _siteNavigationMenu.getName();
		}

		try {
			return StringUtil.appendParentheticalSuffix(
				_siteNavigationMenu.getName(),
				group.getDescriptiveName(themeDisplay.getLocale()));
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return _siteNavigationMenu.getName();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SiteNavigationMenuItemDescriptor.class);

	private final HttpServletRequest _httpServletRequest;
	private final SiteNavigationMenu _siteNavigationMenu;

}