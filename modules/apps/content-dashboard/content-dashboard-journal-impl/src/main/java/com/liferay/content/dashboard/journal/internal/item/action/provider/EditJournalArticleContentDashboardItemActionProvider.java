/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.journal.internal.item.action.provider;

import com.liferay.content.dashboard.item.action.ContentDashboardItemAction;
import com.liferay.content.dashboard.item.action.provider.ContentDashboardItemActionProvider;
import com.liferay.content.dashboard.journal.internal.item.action.EditJournalArticleContentDashboardItemAction;
import com.liferay.info.display.url.provider.InfoEditURLProviderRegistry;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 */
@Component(service = ContentDashboardItemActionProvider.class)
public class EditJournalArticleContentDashboardItemActionProvider
	implements ContentDashboardItemActionProvider<JournalArticle> {

	@Override
	public ContentDashboardItemAction getContentDashboardItemAction(
		JournalArticle journalArticle, HttpServletRequest httpServletRequest) {

		if (!isShow(journalArticle, httpServletRequest)) {
			return null;
		}

		return new EditJournalArticleContentDashboardItemAction(
			_infoEditURLProviderRegistry.getInfoEditURLProvider(
				JournalArticle.class.getName()),
			httpServletRequest, journalArticle, _language, _portal,
			_portletLocalService);
	}

	@Override
	public String getKey() {
		return "edit";
	}

	@Override
	public ContentDashboardItemAction.Type getType() {
		return ContentDashboardItemAction.Type.EDIT;
	}

	@Override
	public boolean isShow(
		JournalArticle journalArticle, HttpServletRequest httpServletRequest) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		try {
			return _modelResourcePermission.contains(
				themeDisplay.getPermissionChecker(), journalArticle,
				ActionKeys.UPDATE);
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return false;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditJournalArticleContentDashboardItemActionProvider.class);

	@Reference
	private InfoEditURLProviderRegistry _infoEditURLProviderRegistry;

	@Reference
	private Language _language;

	@Reference(
		target = "(model.class.name=com.liferay.journal.model.JournalArticle)"
	)
	private ModelResourcePermission<JournalArticle> _modelResourcePermission;

	@Reference
	private Portal _portal;

	@Reference
	private PortletLocalService _portletLocalService;

}