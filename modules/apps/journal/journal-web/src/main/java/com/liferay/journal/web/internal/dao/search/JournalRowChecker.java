/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.dao.search;

import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
public class JournalRowChecker extends EmptyOnClickRowChecker {

	public JournalRowChecker(
		JournalArticle refererJournalArticle, PortletResponse portletResponse) {

		super(portletResponse);

		_refererJournalArticle = refererJournalArticle;
		_portletResponse = portletResponse;
	}

	@Override
	public String getAllRowsCheckBox() {
		return null;
	}

	@Override
	public String getAllRowsCheckBox(HttpServletRequest httpServletRequest) {
		return null;
	}

	@Override
	public String getRowCheckBox(
		HttpServletRequest httpServletRequest, boolean checked,
		boolean disabled, String primaryKey) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		JournalArticle article = JournalArticleLocalServiceUtil.fetchArticle(
			themeDisplay.getScopeGroupId(), primaryKey);

		if (article == null) {
			return StringPool.BLANK;
		}

		return super.getRowCheckBox(
			httpServletRequest, checked, disabled,
			StringBundler.concat(
				_portletResponse.getNamespace(), RowChecker.ROW_IDS,
				JournalArticle.class.getSimpleName(), StringPool.BLANK),
			primaryKey,
			StringBundler.concat(
				_portletResponse.getNamespace(), RowChecker.ROW_IDS,
				JournalArticle.class.getSimpleName(), "']"),
			"'#" + getAllRowIds() + "'", StringPool.BLANK);
	}

	@Override
	public boolean isDisabled(Object object) {
		if ((object instanceof JournalFolder) ||
			(_refererJournalArticle == null)) {

			return false;
		}

		JournalArticle article = (JournalArticle)object;

		if (article.getResourcePrimKey() !=
				_refererJournalArticle.getResourcePrimKey()) {

			return false;
		}

		return true;
	}

	private final PortletResponse _portletResponse;
	private final JournalArticle _refererJournalArticle;

}