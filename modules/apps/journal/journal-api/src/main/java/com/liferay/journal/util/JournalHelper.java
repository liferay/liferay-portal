/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.util;

import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.PortletRequestModel;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import jakarta.portlet.PortletRequest;

import java.util.List;
import java.util.Locale;

/**
 * @author Tom Wang
 */
public interface JournalHelper {

	public String createURLPattern(
			JournalArticle article, Locale locale, boolean privateLayout,
			String separator, ThemeDisplay themeDisplay)
		throws PortalException;

	public String diffHtml(
			long groupId, String articleId, double sourceVersion,
			double targetVersion, String languageId,
			PortletRequestModel portletRequestModel, ThemeDisplay themeDisplay)
		throws Exception;

	public String getAbsolutePath(PortletRequest portletRequest, long folderId)
		throws PortalException;

	public Layout getArticleLayout(String layoutUuid, long groupId);

	/**
	 * @deprecated As of Judson (7.1.x), with no direct replacement
	 */
	@Deprecated
	public List<JournalArticle> getArticles(Hits hits) throws PortalException;

	public int getRestrictionType(long folderId);

}