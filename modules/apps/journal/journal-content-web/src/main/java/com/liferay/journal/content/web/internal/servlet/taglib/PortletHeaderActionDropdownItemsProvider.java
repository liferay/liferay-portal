/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.content.web.internal.servlet.taglib;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.journal.content.web.internal.display.context.JournalContentDisplayContext;
import com.liferay.journal.content.web.internal.security.permission.resource.JournalArticlePermission;
import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.security.PermissionsURLTag;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Diego Hu
 */
public class PortletHeaderActionDropdownItemsProvider {

	public PortletHeaderActionDropdownItemsProvider(
		HttpServletRequest httpServletRequest,
		JournalContentDisplayContext journalContentDisplayContext) {

		_httpServletRequest = httpServletRequest;
		_journalContentDisplayContext = journalContentDisplayContext;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getActionDropdownItems() throws Exception {
		JournalArticle article = _journalContentDisplayContext.getArticle();

		return DropdownItemListBuilder.add(
			_journalContentDisplayContext::isShowEditArticleIcon,
			dropdownItem -> {
				dropdownItem.setHref(
					_journalContentDisplayContext.getURLEdit());
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "edit-web-content"));
			}
		).add(
			_journalContentDisplayContext::isShowEditTemplateIcon,
			dropdownItem -> {
				dropdownItem.setHref(
					_journalContentDisplayContext.getURLEditTemplate());
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "edit-template"));
			}
		).add(
			() -> JournalArticlePermission.contains(
				_themeDisplay.getPermissionChecker(), article,
				ActionKeys.PERMISSIONS),
			dropdownItem -> {
				dropdownItem.putData("action", "permissions");
				dropdownItem.putData(
					"permissionsURL",
					PermissionsURLTag.doTag(
						StringPool.BLANK, JournalArticle.class.getName(),
						HtmlUtil.escape(
							article.getTitle(_themeDisplay.getLocale())),
						null, String.valueOf(article.getResourcePrimKey()),
						LiferayWindowState.POP_UP.toString(), null,
						_httpServletRequest));
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "permissions"));
			}
		).add(
			() -> JournalArticlePermission.contains(
				_themeDisplay.getPermissionChecker(), article,
				ActionKeys.UPDATE),
			dropdownItem -> {
				dropdownItem.setHref(
					_journalContentDisplayContext.getURLViewHistory());
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "view-history"));
			}
		).build();
	}

	private final HttpServletRequest _httpServletRequest;
	private final JournalContentDisplayContext _journalContentDisplayContext;
	private final ThemeDisplay _themeDisplay;

}