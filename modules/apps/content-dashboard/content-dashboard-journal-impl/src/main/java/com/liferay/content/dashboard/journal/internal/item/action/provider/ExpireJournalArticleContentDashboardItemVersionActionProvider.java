/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.journal.internal.item.action.provider;

import com.liferay.content.dashboard.item.action.ContentDashboardItemVersionAction;
import com.liferay.content.dashboard.item.action.provider.ContentDashboardItemVersionActionProvider;
import com.liferay.content.dashboard.journal.internal.item.action.ExpireJournalArticleContentDashboardItemVersionAction;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = "service.ranking:Integer=400",
	service = ContentDashboardItemVersionActionProvider.class
)
public class ExpireJournalArticleContentDashboardItemVersionActionProvider
	implements ContentDashboardItemVersionActionProvider<JournalArticle> {

	@Override
	public ContentDashboardItemVersionAction
		getContentDashboardItemVersionAction(
			JournalArticle journalArticle,
			HttpServletRequest httpServletRequest) {

		if (!isShow(journalArticle, httpServletRequest)) {
			return null;
		}

		return new ExpireJournalArticleContentDashboardItemVersionAction(
			httpServletRequest, journalArticle, _language, _portal,
			RequestBackedPortletURLFactoryUtil.create(httpServletRequest));
	}

	@Override
	public boolean isShow(
		JournalArticle journalArticle, HttpServletRequest httpServletRequest) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		try {
			if (_modelResourcePermission.contains(
					themeDisplay.getPermissionChecker(), journalArticle,
					ActionKeys.EXPIRE) &&
				((journalArticle.getStatus() ==
					WorkflowConstants.STATUS_APPROVED) ||
				 (journalArticle.getStatus() ==
					 WorkflowConstants.STATUS_SCHEDULED))) {

				return true;
			}

			return false;
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return false;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ExpireJournalArticleContentDashboardItemVersionActionProvider.class);

	@Reference
	private Language _language;

	@Reference(
		target = "(model.class.name=com.liferay.journal.model.JournalArticle)"
	)
	private ModelResourcePermission<JournalArticle> _modelResourcePermission;

	@Reference
	private Portal _portal;

}