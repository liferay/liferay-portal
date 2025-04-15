/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.servlet.taglib.util;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.journal.model.JournalFeed;
import com.liferay.journal.web.internal.security.permission.resource.JournalFeedPermission;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.security.PermissionsURLTag;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Diego Hu
 */
public class JournalFeedActionDropdownItemsProvider {

	public JournalFeedActionDropdownItemsProvider(
		JournalFeed feed, LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		_feed = feed;
		_liferayPortletResponse = liferayPortletResponse;

		_httpServletRequest = PortalUtil.getHttpServletRequest(
			liferayPortletRequest);
		_themeDisplay = (ThemeDisplay)liferayPortletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getActionDropdownItems() throws Exception {
		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> JournalFeedPermission.contains(
							_themeDisplay.getPermissionChecker(), _feed,
							ActionKeys.UPDATE),
						dropdownItem -> {
							dropdownItem.setHref(
								PortletURLBuilder.createRenderURL(
									_liferayPortletResponse
								).setMVCPath(
									"/edit_feed.jsp"
								).setRedirect(
									_themeDisplay.getURLCurrent()
								).setParameter(
									"feedId", _feed.getFeedId()
								).setParameter(
									"groupId", _feed.getGroupId()
								).buildString());
							dropdownItem.setIcon("pencil");
							dropdownItem.setLabel(
								LanguageUtil.get(_httpServletRequest, "edit"));
						}
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> JournalFeedPermission.contains(
							_themeDisplay.getPermissionChecker(), _feed,
							ActionKeys.PERMISSIONS),
						dropdownItem -> {
							dropdownItem.putData(
								"action", "permissionsJournalFeed");
							dropdownItem.putData(
								"permissionsJournalFeedURL",
								PermissionsURLTag.doTag(
									StringPool.BLANK,
									JournalFeed.class.getName(),
									_feed.getName(), null,
									String.valueOf(_feed.getId()),
									LiferayWindowState.POP_UP.toString(), null,
									_httpServletRequest));
							dropdownItem.setIcon("password-policies");
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, "permissions"));
						}
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> JournalFeedPermission.contains(
							_themeDisplay.getPermissionChecker(), _feed,
							ActionKeys.DELETE),
						dropdownItem -> {
							dropdownItem.putData("action", "deleteJournalFeed");
							dropdownItem.putData(
								"deleteJournalFeedURL",
								PortletURLBuilder.createActionURL(
									_liferayPortletResponse
								).setActionName(
									"/journal/delete_feeds"
								).setRedirect(
									_themeDisplay.getURLCurrent()
								).setParameter(
									"deleteFeedId", _feed.getFeedId()
								).buildString());
							dropdownItem.setIcon("trash");
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, "delete"));
						}
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).build();
	}

	private final JournalFeed _feed;
	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final ThemeDisplay _themeDisplay;

}