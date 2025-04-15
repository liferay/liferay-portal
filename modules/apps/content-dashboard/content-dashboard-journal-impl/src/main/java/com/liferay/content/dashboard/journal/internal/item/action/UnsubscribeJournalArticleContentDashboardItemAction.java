/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.journal.internal.item.action;

import com.liferay.content.dashboard.item.action.ContentDashboardItemAction;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

/**
 * @author Cristina González
 */
public class UnsubscribeJournalArticleContentDashboardItemAction
	implements ContentDashboardItemAction {

	public UnsubscribeJournalArticleContentDashboardItemAction(
		HttpServletRequest httpServletRequest, JournalArticle journalArticle,
		ModelResourcePermission<JournalArticle>
			journalArticleModelResourcePermission,
		Language language,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		_httpServletRequest = httpServletRequest;
		_journalArticle = journalArticle;
		_journalArticleModelResourcePermission =
			journalArticleModelResourcePermission;
		_language = language;
		_requestBackedPortletURLFactory = requestBackedPortletURLFactory;
	}

	@Override
	public String getIcon() {
		return "bell-off";
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "unsubscribe");
	}

	@Override
	public String getName() {
		return "unsubscribe";
	}

	@Override
	public Type getType() {
		return Type.UNSUBSCRIBE;
	}

	@Override
	public String getURL() {
		return PortletURLBuilder.create(
			_requestBackedPortletURLFactory.createActionURL(
				JournalPortletKeys.JOURNAL)
		).setActionName(
			"/journal/unsubscribe_article"
		).setRedirect(
			ParamUtil.getString(_httpServletRequest, "backURL")
		).setParameter(
			"articleId", _journalArticle.getResourcePrimKey()
		).buildString();
	}

	@Override
	public String getURL(Locale locale) {
		return getURL();
	}

	@Override
	public boolean isDisabled() {
		try {
			_journalArticleModelResourcePermission.check(
				PermissionThreadLocal.getPermissionChecker(),
				_journalArticle.getResourcePrimKey(), ActionKeys.SUBSCRIBE);

			return false;
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return true;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UnsubscribeJournalArticleContentDashboardItemAction.class);

	private final HttpServletRequest _httpServletRequest;
	private final JournalArticle _journalArticle;
	private final ModelResourcePermission<JournalArticle>
		_journalArticleModelResourcePermission;
	private final Language _language;
	private final RequestBackedPortletURLFactory
		_requestBackedPortletURLFactory;

}