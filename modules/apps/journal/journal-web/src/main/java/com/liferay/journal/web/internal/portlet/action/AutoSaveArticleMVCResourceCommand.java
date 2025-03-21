/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.portlet.action;

import com.liferay.asset.display.page.portlet.AssetDisplayPageEntryFormProcessor;
import com.liferay.document.library.kernel.exception.DuplicateFileEntryException;
import com.liferay.document.library.kernel.exception.FileSizeException;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.dynamic.data.mapping.exception.NoSuchStructureException;
import com.liferay.dynamic.data.mapping.exception.NoSuchTemplateException;
import com.liferay.dynamic.data.mapping.exception.StorageFieldRequiredException;
import com.liferay.dynamic.data.mapping.form.values.factory.DDMFormValuesFactory;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesToFieldsConverter;
import com.liferay.exportimport.kernel.exception.ExportImportContentValidationException;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.exception.ArticleContentException;
import com.liferay.journal.exception.ArticleContentSizeException;
import com.liferay.journal.exception.ArticleFriendlyURLException;
import com.liferay.journal.exception.ArticleIdException;
import com.liferay.journal.exception.ArticleTitleException;
import com.liferay.journal.exception.ArticleVersionException;
import com.liferay.journal.exception.DuplicateArticleIdException;
import com.liferay.journal.exception.InvalidDDMStructureException;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleLocalization;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.service.JournalArticleService;
import com.liferay.journal.util.JournalConverter;
import com.liferay.journal.util.JournalHelper;
import com.liferay.journal.web.internal.util.JournalArticleUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.LocaleException;
import com.liferay.portal.kernel.exception.NoSuchImageException;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.FileItem;
import com.liferay.portal.kernel.upload.LiferayFileItemException;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mikel Lorza
 */
@Component(
	property = {
		"jakarta.portlet.name=" + JournalPortletKeys.JOURNAL,
		"mvc.command.name=/journal/auto_save_article"
	},
	service = MVCResourceCommand.class
)
public class AutoSaveArticleMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		JSONObject jsonObject;

		try {
			UploadPortletRequest uploadPortletRequest =
				_portal.getUploadPortletRequest(resourceRequest);

			String articleId = ParamUtil.getString(
				uploadPortletRequest, "articleId");

			JournalArticle journalArticle =
				_journalArticleLocalService.fetchArticle(
					ParamUtil.getLong(uploadPortletRequest, "groupId"),
					articleId);

			String actionName = "/journal/add_article";

			if (journalArticle != null) {
				actionName = "/journal/update_article";
			}

			JournalArticle article = JournalArticleUtil.addOrUpdateArticle(
				actionName, _assetDisplayPageEntryFormProcessor,
				_ddmFormValuesFactory, _ddmFormValuesToFieldsConverter,
				_ddmStructureLocalService, _journalArticleService,
				_journalConverter, _journalHelper, _localization, _portal,
				resourceRequest);

			jsonObject = JSONUtil.put(
				"articleId", article.getArticleId()
			).put(
				"friendlyURL",
				() -> {
					if (Validator.isNotNull(articleId)) {
						return null;
					}

					Map<Locale, String> friendlyURLMap =
						article.getFriendlyURLMap();

					return friendlyURLMap.get(
						LocaleUtil.fromLanguageId(
							article.getDefaultLanguageId()));
				}
			).put(
				"modifiedDate",
				article.getModifiedDate(
				).getTime()
			).put(
				"success", true
			).put(
				"version", String.valueOf(article.getVersion())
			);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.error("Unable to perform action", exception);
			}

			jsonObject = JSONUtil.put(
				"errorMessage", _getErrorMessage(exception, resourceRequest)
			).put(
				"success", false
			);
		}

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse, jsonObject);
	}

	private String _getErrorMessage(
		Exception exception, ResourceRequest resourceRequest) {

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			resourceRequest);

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (exception instanceof ArticleContentException) {
			return _language.get(
				httpServletRequest, "please-enter-valid-content");
		}
		else if (exception instanceof ArticleContentSizeException) {
			return _language.get(
				httpServletRequest,
				"you-have-exceeded-the-maximum-web-content-size-allowed");
		}
		else if (exception instanceof ArticleFriendlyURLException) {
			return _language.get(
				httpServletRequest,
				"you-must-define-a-friendly-url-for-the-default-language");
		}
		else if (exception instanceof ArticleIdException) {
			return _language.get(httpServletRequest, "please-enter-a-valid-id");
		}
		else if (exception instanceof ArticleTitleException) {
			return _language.format(
				httpServletRequest,
				"please-enter-a-valid-title-for-the-default-language-x",
				LocaleUtil.toW3cLanguageId(
					ParamUtil.getString(
						httpServletRequest, "defaultLanguageId")));
		}
		else if (exception instanceof
					ArticleTitleException.MustNotExceedMaximumLength) {

			return _language.format(
				httpServletRequest,
				"please-enter-a-title-with-fewer-than-x-characters",
				ModelHintsUtil.getMaxLength(
					JournalArticleLocalization.class.getName(), "title"));
		}
		else if (exception instanceof ArticleVersionException) {
			return _language.get(
				httpServletRequest,
				"another-user-has-made-changes-since-you-started-editing");
		}
		else if (exception instanceof DuplicateArticleIdException) {
			return _language.get(
				httpServletRequest, "please-enter-a-unique-id");
		}
		else if (exception instanceof DuplicateFileEntryException) {
			return _language.get(
				httpServletRequest, "a-file-with-that-name-already-exists");
		}
		else if (exception instanceof ExportImportContentValidationException) {
			ExportImportContentValidationException
				exportImportContentValidationException =
					(ExportImportContentValidationException)exception;

			if (exportImportContentValidationException.getType() ==
					ExportImportContentValidationException.ARTICLE_NOT_FOUND) {

				return _language.get(
					httpServletRequest,
					"unable-to-validate-referenced-web-content-article");
			}
			else if (exportImportContentValidationException.getType() ==
						ExportImportContentValidationException.
							FILE_ENTRY_NOT_FOUND) {

				return _language.format(
					httpServletRequest,
					"unable-to-validate-referenced-document-because-it-" +
						"cannot-be-found-with-the-following-parameters-x-" +
							"when-analyzing-link-x",
					new String[] {
						MapUtil.toString(
							exportImportContentValidationException.
								getDLReferenceParameters()),
						exportImportContentValidationException.getDLReference()
					});
			}
			else if (exportImportContentValidationException.getType() ==
						ExportImportContentValidationException.
							JOURNAL_FEED_NOT_FOUND) {

				return _language.format(
					httpServletRequest,
					"unable-to-validate-referenced-journal-feed-because-it-" +
						"cannot-be-found-with-url-x",
					exportImportContentValidationException.
						getJournalArticleFeedURL());
			}
			else if (exportImportContentValidationException.getType() ==
						ExportImportContentValidationException.
							LAYOUT_GROUP_NOT_FOUND) {

				return _language.format(
					httpServletRequest,
					"unable-to-validate-referenced-page-with-url-x-because-" +
						"the-page-group-with-url-x-cannot-be-found",
					new String[] {
						exportImportContentValidationException.getLayoutURL(),
						exportImportContentValidationException.
							getGroupFriendlyURL()
					});
			}
			else if (exportImportContentValidationException.getType() ==
						ExportImportContentValidationException.
							LAYOUT_NOT_FOUND) {

				return _language.format(
					httpServletRequest,
					"unable-to-validate-referenced-page-because-it-cannot-be-" +
						"found-with-the-following-parameters-x",
					MapUtil.toString(
						exportImportContentValidationException.
							getLayoutReferenceParameters()));
			}
			else if (exportImportContentValidationException.getType() ==
						ExportImportContentValidationException.
							LAYOUT_WITH_URL_NOT_FOUND) {

				return _language.format(
					httpServletRequest,
					"unable-to-validate-referenced-page-because-it-cannot-be-" +
						"found-with-url-x",
					exportImportContentValidationException.getLayoutURL());
			}
		}
		else if (exception instanceof FileSizeException) {
			FileSizeException fileSizeException = (FileSizeException)exception;

			return _language.format(
				httpServletRequest,
				"please-enter-a-file-with-a-valid-file-size-no-larger-than-x",
				_language.formatStorageSize(
					fileSizeException.getMaxSize(), themeDisplay.getLocale()),
				false);
		}
		else if (exception instanceof InvalidDDMStructureException) {
			return _language.get(
				httpServletRequest,
				"the-structure-you-selected-is-not-valid-for-this-folder");
		}
		else if (exception instanceof LiferayFileItemException) {
			return _language.format(
				httpServletRequest,
				"please-enter-valid-content-with-valid-content-size-no-" +
					"larger-than-x",
				_language.formatStorageSize(
					FileItem.THRESHOLD_SIZE, themeDisplay.getLocale()),
				false);
		}
		else if (exception instanceof LocaleException) {
			LocaleException localeException = (LocaleException)exception;

			if (localeException.getType() == LocaleException.TYPE_CONTENT) {
				return _language.format(
					httpServletRequest,
					"the-default-language-x-does-not-match-the-portal's-" +
						"available-languages-x",
					new String[] {
						StringUtil.merge(
							localeException.getSourceAvailableLanguageIds(),
							StringPool.COMMA_AND_SPACE),
						StringUtil.merge(
							localeException.getTargetAvailableLanguageIds(),
							StringPool.COMMA_AND_SPACE)
					});
			}
		}
		else if (exception instanceof NoSuchFileEntryException) {
			return _language.get(
				httpServletRequest,
				"the-content-references-a-missing-file-entry");
		}
		else if (exception instanceof NoSuchImageException) {
			return _language.get(
				httpServletRequest, "please-select-an-existing-small-image");
		}
		else if (exception instanceof NoSuchLayoutException) {
			NoSuchLayoutException noSuchLayoutException =
				(NoSuchLayoutException)exception;

			if (Objects.equals(
					noSuchLayoutException.getMessage(),
					JournalArticleConstants.DISPLAY_PAGE)) {

				return _language.get(
					httpServletRequest,
					"please-select-an-existing-display-page-template");
			}

			return _language.get(
				httpServletRequest, "the-content-references-a-missing-page");
		}
		else if (exception instanceof NoSuchStructureException) {
			return _language.get(
				httpServletRequest, "please-select-an-existing-structure");
		}
		else if (exception instanceof NoSuchTemplateException) {
			return _language.get(
				httpServletRequest, "please-select-an-existing-template");
		}
		else if (exception instanceof StorageFieldRequiredException) {
			return _language.get(
				httpServletRequest, "please-fill-out-all-required-fields");
		}

		return _language.get(
			httpServletRequest, "an-unexpected-error-occurred");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AutoSaveArticleMVCResourceCommand.class);

	@Reference
	private AssetDisplayPageEntryFormProcessor
		_assetDisplayPageEntryFormProcessor;

	@Reference
	private DDMFormValuesFactory _ddmFormValuesFactory;

	@Reference
	private DDMFormValuesToFieldsConverter _ddmFormValuesToFieldsConverter;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private JournalArticleService _journalArticleService;

	@Reference
	private JournalConverter _journalConverter;

	@Reference
	private JournalHelper _journalHelper;

	@Reference
	private Language _language;

	@Reference
	private Localization _localization;

	@Reference
	private Portal _portal;

}