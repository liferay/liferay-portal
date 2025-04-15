/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.journal.internal.item.action.provider;

import com.liferay.content.dashboard.item.action.ContentDashboardItemAction;
import com.liferay.content.dashboard.item.action.provider.ContentDashboardItemActionProvider;
import com.liferay.content.dashboard.journal.internal.item.action.SubscribeJournalArticleContentDashboardItemAction;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.subscription.service.SubscriptionLocalService;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 */
@Component(service = ContentDashboardItemActionProvider.class)
public class SubscribeJournalArticleContentDashboardItemActionProvider
	implements ContentDashboardItemActionProvider<JournalArticle> {

	@Override
	public ContentDashboardItemAction getContentDashboardItemAction(
		JournalArticle journalArticle, HttpServletRequest httpServletRequest) {

		if (!isShow(journalArticle, httpServletRequest)) {
			return null;
		}

		return new SubscribeJournalArticleContentDashboardItemAction(
			httpServletRequest, journalArticle,
			_journalArticleModelResourcePermission, _language,
			RequestBackedPortletURLFactoryUtil.create(httpServletRequest));
	}

	@Override
	public String getKey() {
		return "subscribe";
	}

	@Override
	public ContentDashboardItemAction.Type getType() {
		return ContentDashboardItemAction.Type.SUBSCRIBE;
	}

	@Override
	public boolean isShow(
		JournalArticle journalArticle, HttpServletRequest httpServletRequest) {

		return !_subscriptionLocalService.isSubscribed(
			journalArticle.getCompanyId(),
			_portal.getUserId(httpServletRequest),
			JournalArticle.class.getName(),
			journalArticle.getResourcePrimKey());
	}

	@Reference(
		target = "(model.class.name=com.liferay.journal.model.JournalArticle)"
	)
	private ModelResourcePermission<JournalArticle>
		_journalArticleModelResourcePermission;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private SubscriptionLocalService _subscriptionLocalService;

}