/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.util;

import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleDisplay;
import com.liferay.portal.kernel.portlet.PortletRequestModel;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Raymond Augé
 */
@ProviderType
public interface JournalContent {

	public void clearCache();

	public void clearCache(long groupId, String articleId);

	public void clearCache(String ddmTemplateKey);

	public String getContent(
		long groupId, String articleId, String viewMode, String languageId);

	public String getContent(
		long groupId, String articleId, String viewMode, String languageId,
		PortletRequestModel portletRequestModel);

	public String getContent(
		long groupId, String articleId, String ddmTemplateKey, String viewMode,
		String languageId, PortletRequestModel portletRequestModel);

	public String getContent(
		long groupId, String articleId, String ddmTemplateKey, String viewMode,
		String languageId, PortletRequestModel portletRequestModel,
		ThemeDisplay themeDisplay);

	public String getContent(
		long groupId, String articleId, String ddmTemplateKey, String viewMode,
		String languageId, ThemeDisplay themeDisplay);

	public String getContent(
		long groupId, String articleId, String viewMode, String languageId,
		ThemeDisplay themeDisplay);

	public JournalArticleDisplay getDisplay(
		JournalArticle article, String ddmTemplateKey, String viewMode,
		String languageId, int page, PortletRequestModel portletRequestModel,
		ThemeDisplay themeDisplay);

	public JournalArticleDisplay getDisplay(
		long groupId, String articleId, double version, String ddmTemplateKey,
		String viewMode, String languageId, int page,
		PortletRequestModel portletRequestModel, ThemeDisplay themeDisplay);

	public JournalArticleDisplay getDisplay(
		long groupId, String articleId, String viewMode, String languageId,
		int page, ThemeDisplay themeDisplay);

	public JournalArticleDisplay getDisplay(
		long groupId, String articleId, String viewMode, String languageId,
		PortletRequestModel portletRequestModel);

	public JournalArticleDisplay getDisplay(
		long groupId, String articleId, String ddmTemplateKey, String viewMode,
		String languageId, int page, PortletRequestModel portletRequestModel,
		ThemeDisplay themeDisplay);

	public JournalArticleDisplay getDisplay(
		long groupId, String articleId, String ddmTemplateKey, String viewMode,
		String languageId, PortletRequestModel portletRequestModel);

	public JournalArticleDisplay getDisplay(
		long groupId, String articleId, String ddmTemplateKey, String viewMode,
		String languageId, ThemeDisplay themeDisplay);

	public JournalArticleDisplay getDisplay(
		long groupId, String articleId, String viewMode, String languageId,
		ThemeDisplay themeDisplay);

}