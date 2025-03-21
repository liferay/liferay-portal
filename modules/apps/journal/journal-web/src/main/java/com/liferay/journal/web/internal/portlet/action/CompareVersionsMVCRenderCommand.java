/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.portlet.action;

import com.liferay.diff.exception.CompareVersionsException;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.service.JournalArticleService;
import com.liferay.journal.util.JournalHelper;
import com.liferay.journal.util.comparator.ArticleVersionComparator;
import com.liferay.journal.web.internal.portlet.JournalPortlet;
import com.liferay.portal.kernel.portlet.PortletRequestModel;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + JournalPortletKeys.JOURNAL,
		"mvc.command.name=/journal/compare_versions"
	},
	service = MVCRenderCommand.class
)
public class CompareVersionsMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			_compareVersions(renderRequest, renderResponse);
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}

		return "/compare_versions.jsp";
	}

	private void _compareVersions(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long groupId = ParamUtil.getLong(renderRequest, "groupId");
		String articleId = ParamUtil.getString(renderRequest, "articleId");

		String sourceArticleId = ParamUtil.getString(
			renderRequest, "sourceVersion");

		int index = sourceArticleId.lastIndexOf(
			JournalPortlet.VERSION_SEPARATOR);

		if (index != -1) {
			sourceArticleId = sourceArticleId.substring(
				index + JournalPortlet.VERSION_SEPARATOR.length());
		}

		double sourceVersion = GetterUtil.getDouble(sourceArticleId);

		String targetArticleId = ParamUtil.getString(
			renderRequest, "targetVersion");

		index = targetArticleId.lastIndexOf(JournalPortlet.VERSION_SEPARATOR);

		if (index != -1) {
			targetArticleId = targetArticleId.substring(
				index + JournalPortlet.VERSION_SEPARATOR.length());
		}

		double targetVersion = GetterUtil.getDouble(targetArticleId);

		if ((sourceVersion == 0) && (targetVersion == 0)) {
			List<JournalArticle> sourceArticles =
				_journalArticleService.getArticlesByArticleId(
					groupId, articleId, 0, 1,
					ArticleVersionComparator.getInstance(false));

			JournalArticle sourceArticle = sourceArticles.get(0);

			sourceVersion = sourceArticle.getVersion();

			List<JournalArticle> targetArticles =
				_journalArticleService.getArticlesByArticleId(
					groupId, articleId, 0, 1,
					ArticleVersionComparator.getInstance(true));

			JournalArticle targetArticle = targetArticles.get(0);

			targetVersion = targetArticle.getVersion();
		}

		if (sourceVersion > targetVersion) {
			double tempVersion = targetVersion;

			targetVersion = sourceVersion;
			sourceVersion = tempVersion;
		}

		String languageId = _getLanguageId(
			renderRequest, groupId, articleId, sourceVersion, targetVersion);

		String diffHtmlResults = null;

		try {
			diffHtmlResults = _journalHelper.diffHtml(
				groupId, articleId, sourceVersion, targetVersion, languageId,
				new PortletRequestModel(renderRequest, renderResponse),
				themeDisplay);
		}
		catch (CompareVersionsException compareVersionsException) {
			renderRequest.setAttribute(
				WebKeys.DIFF_VERSION, compareVersionsException.getVersion());
		}

		renderRequest.setAttribute(WebKeys.DIFF_HTML_RESULTS, diffHtmlResults);
		renderRequest.setAttribute(WebKeys.SOURCE_VERSION, sourceVersion);
		renderRequest.setAttribute(WebKeys.TARGET_VERSION, targetVersion);
	}

	private String _getLanguageId(
		RenderRequest renderRequest, long groupId, String articleId,
		double sourceVersion, double targetVersion) {

		JournalArticle sourceArticle = _journalArticleLocalService.fetchArticle(
			groupId, articleId, sourceVersion);

		JournalArticle targetArticle = _journalArticleLocalService.fetchArticle(
			groupId, articleId, targetVersion);

		Set<Locale> locales = new HashSet<>();

		for (String locale : sourceArticle.getAvailableLanguageIds()) {
			locales.add(LocaleUtil.fromLanguageId(locale));
		}

		for (String locale : targetArticle.getAvailableLanguageIds()) {
			locales.add(LocaleUtil.fromLanguageId(locale));
		}

		String languageId = ParamUtil.get(
			renderRequest, "languageId", targetArticle.getDefaultLanguageId());

		Locale locale = LocaleUtil.fromLanguageId(languageId);

		if (!locales.contains(locale)) {
			languageId = targetArticle.getDefaultLanguageId();
		}

		renderRequest.setAttribute(WebKeys.AVAILABLE_LOCALES, locales);
		renderRequest.setAttribute(WebKeys.LANGUAGE_ID, languageId);

		return languageId;
	}

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private JournalArticleService _journalArticleService;

	@Reference
	private JournalHelper _journalHelper;

}