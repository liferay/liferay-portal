/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.journal.internal.item.action;

import com.liferay.content.dashboard.item.action.ContentDashboardItemVersionAction;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

/**
 * @author Jürgen Kappler
 */
public class ExpireJournalArticleContentDashboardItemVersionAction
	implements ContentDashboardItemVersionAction {

	public ExpireJournalArticleContentDashboardItemVersionAction(
		HttpServletRequest httpServletRequest, JournalArticle journalArticle,
		Language language, Portal portal,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		_httpServletRequest = httpServletRequest;
		_journalArticle = journalArticle;
		_language = language;
		_portal = portal;
		_requestBackedPortletURLFactory = requestBackedPortletURLFactory;
	}

	@Override
	public String getIcon() {
		return "time";
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "expire");
	}

	@Override
	public String getName() {
		return "expire";
	}

	@Override
	public String getURL() {
		LiferayPortletResponse liferayPortletResponse =
			_portal.getLiferayPortletResponse(
				(PortletResponse)_httpServletRequest.getAttribute(
					JavaConstants.JAKARTA_PORTLET_RESPONSE));

		PortletURL portletURL = liferayPortletResponse.createRenderURL();

		return PortletURLBuilder.create(
			_requestBackedPortletURLFactory.createActionURL(
				JournalPortletKeys.JOURNAL)
		).setActionName(
			"/journal/expire_articles"
		).setRedirect(
			portletURL
		).setBackURL(
			portletURL.toString()
		).setParameter(
			"articleId",
			_journalArticle.getArticleId() + _VERSION_SEPARATOR +
				_journalArticle.getVersion()
		).setParameter(
			"groupId", _journalArticle.getGroupId()
		).buildString();
	}

	private static final String _VERSION_SEPARATOR = "_version_";

	private final HttpServletRequest _httpServletRequest;
	private final JournalArticle _journalArticle;
	private final Language _language;
	private final Portal _portal;
	private final RequestBackedPortletURLFactory
		_requestBackedPortletURLFactory;

}