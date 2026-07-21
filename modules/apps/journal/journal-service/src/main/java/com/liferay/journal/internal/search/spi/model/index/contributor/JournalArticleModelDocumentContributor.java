/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.search.spi.model.index.contributor;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.util.DDMIndexer;
import com.liferay.journal.internal.util.JournalUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.ml.embedding.text.TextEmbeddingDocumentContributor;
import com.liferay.portal.search.model.uid.UIDFactory;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;
import com.liferay.trash.TrashHelper;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = "indexer.class.name=com.liferay.journal.model.JournalArticle",
	service = ModelDocumentContributor.class
)
public class JournalArticleModelDocumentContributor
	implements ModelDocumentContributor<JournalArticle> {

	@Override
	public void contribute(Document document, JournalArticle journalArticle) {
		if (_log.isDebugEnabled()) {
			_log.debug("Indexing journal article " + journalArticle);
		}

		document.addKeyword(Field.UID, _uidFactory.getUID(journalArticle));

		String articleId = journalArticle.getArticleId();

		if (journalArticle.isInTrash()) {
			articleId = _trashHelper.getOriginalTitle(articleId);
		}

		document.addKeywordSortable(Field.ARTICLE_ID, articleId);

		DDMFormValues ddmFormValues = null;

		DDMStructure ddmStructure = _ddmStructureLocalService.fetchStructure(
			journalArticle.getDDMStructureId());

		if (ddmStructure != null) {
			document.addKeyword(
				Field.CLASS_TYPE_ID, ddmStructure.getStructureId());

			document.addKeyword(
				"ddmStructureKey", ddmStructure.getStructureKey());

			ddmFormValues = journalArticle.getDDMFormValues();

			if (ddmFormValues != null) {
				for (Locale contentAvailableLocale :
						ddmFormValues.getAvailableLocales()) {

					String content = _ddmIndexer.extractIndexableAttributes(
						ddmStructure, ddmFormValues, contentAvailableLocale);

					document.addText(
						_localization.getLocalizedName(
							Field.CONTENT,
							LocaleUtil.toLanguageId(contentAvailableLocale)),
						content);
				}

				_ddmIndexer.addAttributes(
					document, ddmStructure, ddmFormValues);
			}
		}

		if (!document.hasField(Field.CREATE_DATE)) {
			document.addDate(Field.CREATE_DATE, journalArticle.getCreateDate());
		}

		String[] descriptionAvailableLanguageIds =
			_localization.getAvailableLanguageIds(
				journalArticle.getDescriptionMapAsXML());

		for (String descriptionAvailableLanguageId :
				descriptionAvailableLanguageIds) {

			String description = HtmlUtil.stripHtml(
				journalArticle.getDescription(descriptionAvailableLanguageId));

			document.addText(
				_localization.getLocalizedName(
					Field.DESCRIPTION, descriptionAvailableLanguageId),
				description);
		}

		document.addDate(Field.DISPLAY_DATE, journalArticle.getDisplayDate());
		document.addDate(
			Field.EXPIRATION_DATE, journalArticle.getExpirationDate());
		document.addKeyword(Field.FOLDER_ID, journalArticle.getFolderId());
		document.addKeyword(Field.LAYOUT_UUID, journalArticle.getLayoutUuid());

		if (!document.hasField(Field.MODIFIED_DATE)) {
			document.addDate(
				Field.MODIFIED_DATE, journalArticle.getModifiedDate());
		}

		if (!document.hasField(Field.PUBLISH_DATE)) {
			if (journalArticle.isApproved()) {
				document.addDate(
					Field.PUBLISH_DATE, journalArticle.getDisplayDate());
			}
			else {
				document.addDate(Field.PUBLISH_DATE, new Date(0));
			}
		}

		String[] titleAvailableLanguageIds =
			_localization.getAvailableLanguageIds(
				journalArticle.getTitleMapAsXML());

		for (String titleAvailableLanguageId : titleAvailableLanguageIds) {
			String title = journalArticle.getTitle(titleAvailableLanguageId);

			document.addText(
				_localization.getLocalizedName(
					Field.TITLE, titleAvailableLanguageId),
				title);
		}

		document.addKeyword(
			Field.TREE_PATH,
			StringUtil.split(journalArticle.getTreePath(), CharPool.SLASH));
		document.addKeyword(Field.VERSION, journalArticle.getVersion());
		document.addKeyword(
			"ddmTemplateKey", journalArticle.getDDMTemplateKey());

		if (ddmFormValues != null) {
			document.addText(
				"defaultLanguageId",
				LocaleUtil.toLanguageId(ddmFormValues.getDefaultLocale()));
		}
		else {
			document.addText(
				"defaultLanguageId",
				_language.getLanguageId(LocaleUtil.getSiteDefault()));
		}

		document.addKeyword("head", JournalUtil.isHead(journalArticle));

		boolean headListable = JournalUtil.isHeadListable(journalArticle);

		document.addKeyword("headListable", headListable);

		document.addKeyword(
			"latest", JournalUtil.isLatestArticle(journalArticle));

		if (!document.hasField("localized_title")) {
			document.addLocalizedKeyword(
				"localized_title",
				_localization.populateLocalizationMap(
					HashMapBuilder.putAll(
						journalArticle.getTitleMap()
					).build(),
					journalArticle.getDefaultLanguageId(),
					journalArticle.getGroupId()),
				true, true);
		}

		document.addDate("reviewDate", journalArticle.getReviewDate());

		// Scheduled listable articles should be visible in asset browser

		if (journalArticle.isScheduled() && headListable) {
			boolean visible = GetterUtil.getBoolean(document.get("visible"));

			if (!visible) {
				document.addKeyword("visible", true);
			}
		}

		for (Locale locale :
				_language.getAvailableLocales(journalArticle.getGroupId())) {

			String languageId = LocaleUtil.toLanguageId(locale);

			try {
				document.addKeywordSortable(
					_localization.getLocalizedName("urlTitle", languageId),
					journalArticle.getUrlTitle(locale));
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"Unable to get friendly URL for article ID ",
							journalArticle.getId(), " and language ID ",
							languageId),
						portalException);
				}
			}
		}

		document.addNumber(
			"versionCount", GetterUtil.getDouble(journalArticle.getVersion()));

		document.addKeyword(Field.UUID, journalArticle.getUuid());

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Journal article " + journalArticle + " indexed successfully");
		}

		_contributeTextEmbedding(ddmFormValues, document, journalArticle);
	}

	private void _contributeTextEmbedding(
		DDMFormValues ddmFormValues, Document document,
		JournalArticle journalArticle) {

		if (ddmFormValues == null) {
			return;
		}

		JournalArticle latestArticle =
			_journalArticleLocalService.fetchLatestArticle(
				journalArticle.getResourcePrimKey(),
				WorkflowConstants.STATUS_APPROVED);

		if ((latestArticle == null) ||
			(latestArticle.getVersion() != journalArticle.getVersion())) {

			return;
		}

		for (Locale locale :
				_language.getAvailableLocales(latestArticle.getGroupId())) {

			String languageId = LocaleUtil.toLanguageId(locale);

			_textEmbeddingDocumentContributor.contribute(
				document, languageId, journalArticle,
				StringBundler.concat(
					journalArticle.getTitle(locale), StringPool.PERIOD,
					StringPool.SPACE,
					_getEmbeddingText(ddmFormValues, locale)));
		}
	}

	private String _getEmbeddingText(
		DDMFormValues ddmFormValues, Locale locale) {

		Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap =
			ddmFormValues.getDDMFormFieldValuesMap(true);

		List<DDMFormFieldValue> ddmFormFieldValues = ddmFormFieldValuesMap.get(
			"content");

		if (ListUtil.isEmpty(ddmFormFieldValues)) {
			return StringPool.BLANK;
		}

		DDMFormFieldValue ddmFormFieldValue = ddmFormFieldValues.get(0);

		Value value = ddmFormFieldValue.getValue();

		if (value == null) {
			return StringPool.BLANK;
		}

		return value.getString(locale);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JournalArticleModelDocumentContributor.class);

	@Reference
	private DDMIndexer _ddmIndexer;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private Language _language;

	@Reference
	private Localization _localization;

	@Reference
	private TextEmbeddingDocumentContributor _textEmbeddingDocumentContributor;

	@Reference
	private TrashHelper _trashHelper;

	@Reference
	private UIDFactory _uidFactory;

}