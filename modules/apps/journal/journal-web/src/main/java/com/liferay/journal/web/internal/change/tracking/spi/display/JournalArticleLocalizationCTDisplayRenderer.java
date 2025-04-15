/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.change.tracking.spi.display;

import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.change.tracking.spi.display.context.DisplayContext;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleLocalization;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.translation.constants.TranslationPortletKeys;
import com.liferay.translation.model.TranslationEntry;
import com.liferay.translation.service.TranslationEntryLocalService;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cheryl Tang
 */
@Component(service = CTDisplayRenderer.class)
public class JournalArticleLocalizationCTDisplayRenderer
	extends BaseCTDisplayRenderer<JournalArticleLocalization> {

	@Override
	public String getEditURL(
			HttpServletRequest httpServletRequest,
			JournalArticleLocalization journalArticleLocalization)
		throws PortalException {

		JournalArticle journalArticle =
			_journalArticleLocalService.getJournalArticle(
				journalArticleLocalization.getArticlePK());

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest,
				_groupLocalService.getGroup(journalArticle.getGroupId()),
				TranslationPortletKeys.TRANSLATION, 0, 0,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/translation/translate"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).setParameter(
			"classNameId",
			_classNameLocalService.getClassNameId(
				JournalArticle.class.getName())
		).setParameter(
			"classPK", journalArticle.getResourcePrimKey()
		).setParameter(
			"targetLanguageId", journalArticleLocalization.getLanguageId()
		).buildString();
	}

	@Override
	public Class<JournalArticleLocalization> getModelClass() {
		return JournalArticleLocalization.class;
	}

	@Override
	public String getTitle(
		Locale locale, JournalArticleLocalization journalArticleLocalization) {

		return journalArticleLocalization.getTitle();
	}

	@Override
	public String getTypeName(Locale locale) {
		return _language.get(locale, "web-content-translation");
	}

	@Override
	public String renderPreview(
			DisplayContext<JournalArticleLocalization> displayContext)
		throws Exception {

		JournalArticleLocalization journalArticleLocalization =
			displayContext.getModel();

		return displayContext.renderPreview(
			_journalArticleLocalService.getJournalArticle(
				journalArticleLocalization.getArticlePK()),
			LocaleUtil.fromLanguageId(
				journalArticleLocalization.getLanguageId()));
	}

	@Override
	public boolean showPreviewDiff() {
		return true;
	}

	@Override
	protected void buildDisplay(
			DisplayBuilder<JournalArticleLocalization> displayBuilder)
		throws PortalException {

		JournalArticleLocalization journalArticleLocalization =
			displayBuilder.getModel();

		JournalArticle journalArticle =
			_journalArticleLocalService.getJournalArticle(
				journalArticleLocalization.getArticlePK());

		TranslationEntry translationEntry =
			_translationEntryLocalService.fetchTranslationEntry(
				JournalArticle.class.getName(),
				journalArticle.getResourcePrimKey(),
				journalArticleLocalization.getLanguageId());

		displayBuilder.display(
			"title", journalArticleLocalization.getTitle()
		).display(
			"description", journalArticleLocalization.getDescription()
		).display(
			"create-date",
			() -> {
				if (translationEntry == null) {
					return journalArticle.getCreateDate();
				}

				return translationEntry.getCreateDate();
			}
		).display(
			"last-modified",
			() -> {
				if (translationEntry == null) {
					return journalArticle.getModifiedDate();
				}

				return translationEntry.getModifiedDate();
			}
		).display(
			"language", journalArticleLocalization.getLanguageId()
		).display(
			"status",
			() -> {
				if (translationEntry == null) {
					return null;
				}

				return translationEntry.getStatus();
			}
		);
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private TranslationEntryLocalService _translationEntryLocalService;

}