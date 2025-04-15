/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.journal.internal.item.action.provider;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.content.dashboard.item.action.ContentDashboardItemAction;
import com.liferay.content.dashboard.item.action.provider.ContentDashboardItemActionProvider;
import com.liferay.content.dashboard.journal.internal.item.action.ViewJournalArticleContentDashboardItemAction;
import com.liferay.journal.model.JournalArticle;
import com.liferay.layout.display.page.LayoutDisplayPageProviderRegistry;
import com.liferay.layout.seo.kernel.LayoutSEOLinkManager;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 */
@Component(service = ContentDashboardItemActionProvider.class)
public class ViewJournalArticleContentDashboardItemActionProvider
	implements ContentDashboardItemActionProvider<JournalArticle> {

	@Override
	public ContentDashboardItemAction getContentDashboardItemAction(
		JournalArticle journalArticle, HttpServletRequest httpServletRequest) {

		if (!isShow(journalArticle, httpServletRequest)) {
			return null;
		}

		return _getContentDashboardItemAction(
			httpServletRequest, journalArticle);
	}

	@Override
	public String getKey() {
		return "view";
	}

	@Override
	public ContentDashboardItemAction.Type getType() {
		return ContentDashboardItemAction.Type.VIEW;
	}

	@Override
	public boolean isShow(
		JournalArticle journalArticle, HttpServletRequest httpServletRequest) {

		if (!journalArticle.hasApprovedVersion()) {
			return false;
		}

		ContentDashboardItemAction contentDashboardItemAction =
			_getContentDashboardItemAction(httpServletRequest, journalArticle);

		return Validator.isNotNull(contentDashboardItemAction.getURL());
	}

	private ContentDashboardItemAction _getContentDashboardItemAction(
		HttpServletRequest httpServletRequest, JournalArticle journalArticle) {

		return new ViewJournalArticleContentDashboardItemAction(
			_assetDisplayPageFriendlyURLProvider, httpServletRequest,
			journalArticle, _language, _layoutDisplayPageProviderRegistry,
			_layoutLocalService, _layoutSEOLinkManager, _portal);
	}

	@Reference
	private AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;

	@Reference
	private Language _language;

	@Reference
	private LayoutDisplayPageProviderRegistry
		_layoutDisplayPageProviderRegistry;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutSEOLinkManager _layoutSEOLinkManager;

	@Reference
	private Portal _portal;

}