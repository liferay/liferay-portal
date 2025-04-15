/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.journal.internal.item.action.provider;

import com.liferay.content.dashboard.item.action.ContentDashboardItemVersionAction;
import com.liferay.content.dashboard.item.action.provider.ContentDashboardItemVersionActionProvider;
import com.liferay.content.dashboard.journal.internal.item.action.CompareVersionsJournalArticleContentDashboardItemVersionAction;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = "service.ranking:Integer=500",
	service = ContentDashboardItemVersionActionProvider.class
)
public class
	CompareVersionsJournalArticleContentDashboardItemVersionActionProvider
		implements ContentDashboardItemVersionActionProvider<JournalArticle> {

	@Override
	public ContentDashboardItemVersionAction
		getContentDashboardItemVersionAction(
			JournalArticle journalArticle,
			HttpServletRequest httpServletRequest) {

		if (!isShow(journalArticle, httpServletRequest)) {
			return null;
		}

		JournalArticle latestJournalArticle = null;

		try {
			latestJournalArticle = _journalArticleService.getLatestArticle(
				journalArticle.getResourcePrimKey());
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return null;
		}

		return new CompareVersionsJournalArticleContentDashboardItemVersionAction(
			httpServletRequest, journalArticle, _language, latestJournalArticle,
			_portal,
			RequestBackedPortletURLFactoryUtil.create(httpServletRequest));
	}

	@Override
	public boolean isShow(
		JournalArticle journalArticle, HttpServletRequest httpServletRequest) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		User user = themeDisplay.getUser();

		int count = _journalArticleService.getArticlesCountByArticleId(
			journalArticle.getGroupId(), journalArticle.getArticleId());

		if ((count > 1) &&
			permissionChecker.isContentReviewer(
				user.getCompanyId(), themeDisplay.getScopeGroupId())) {

			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CompareVersionsJournalArticleContentDashboardItemVersionActionProvider.
			class);

	@Reference
	private JournalArticleService _journalArticleService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}