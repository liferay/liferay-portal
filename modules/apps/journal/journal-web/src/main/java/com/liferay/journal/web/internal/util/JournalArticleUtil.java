/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.util;

import com.liferay.asset.display.page.constants.AssetDisplayPageConstants;
import com.liferay.dynamic.data.mapping.form.values.factory.DDMFormValuesFactory;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.Fields;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesToFieldsConverter;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.exception.ArticleContentSizeException;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleService;
import com.liferay.journal.util.JournalConverter;
import com.liferay.journal.util.JournalHelper;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.upload.LiferayFileItemException;
import com.liferay.portal.kernel.upload.UploadException;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletRequest;

import java.io.File;

import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * @author Mikel Lorza
 */
public class JournalArticleUtil {

	public static JournalArticle addOrUpdateArticle(
			String actionName, DDMFormValuesFactory ddmFormValuesFactory,
			DDMFormValuesToFieldsConverter ddmFormValuesToFieldsConverter,
			DDMStructureLocalService ddmStructureLocalService,
			JournalArticleService journalArticleService,
			JournalConverter journalConverter, JournalHelper journalHelper,
			Localization localization, Portal portal,
			PortletRequest portletRequest)
		throws Exception {

		UploadException uploadException =
			(UploadException)portletRequest.getAttribute(
				WebKeys.UPLOAD_EXCEPTION);

		if (uploadException != null) {
			Throwable throwable = uploadException.getCause();

			if (uploadException.isExceededLiferayFileItemSizeLimit()) {
				throw new LiferayFileItemException(throwable);
			}

			if (uploadException.isExceededFileSizeLimit() ||
				uploadException.isExceededUploadRequestSizeLimit()) {

				throw new ArticleContentSizeException(throwable);
			}

			throw new PortalException(throwable);
		}

		UploadPortletRequest uploadPortletRequest =
			portal.getUploadPortletRequest(portletRequest);

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Updating article " +
					MapUtil.toString(uploadPortletRequest.getParameterMap()));
		}

		long groupId = ParamUtil.getLong(uploadPortletRequest, "groupId");

		long folderId = ParamUtil.getLong(uploadPortletRequest, "folderId");

		long newFolderId = ParamUtil.getLong(
			uploadPortletRequest, "newFolderId");

		if (newFolderId > 0) {
			folderId = newFolderId;
		}

		String articleId = ParamUtil.getString(
			uploadPortletRequest, "articleId");

		Map<Locale, String> titleMap = localization.getLocalizationMap(
			portletRequest, "titleMapAsXML");

		long ddmStructureId = ParamUtil.getLong(
			uploadPortletRequest, "ddmStructureId");

		DDMStructure ddmStructure = ddmStructureLocalService.getStructure(
			ddmStructureId);

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			JournalArticle.class.getName(), uploadPortletRequest);

		DDMFormValues ddmFormValues = ddmFormValuesFactory.create(
			portletRequest, ddmStructure.getDDMForm());

		Fields fields = ddmFormValuesToFieldsConverter.convert(
			ddmStructure, ddmFormValues);

		String content = journalConverter.getContent(
			ddmStructure, fields, groupId);

		Map<Locale, String> descriptionMap = localization.getLocalizationMap(
			portletRequest, "descriptionMapAsXML");
		Map<Locale, String> friendlyURLMap = localization.getLocalizationMap(
			portletRequest, "friendlyURL");

		String ddmTemplateKey = ParamUtil.getString(
			uploadPortletRequest, "ddmTemplateKey");
		int displayPageType = ParamUtil.getInteger(
			uploadPortletRequest, "displayPageType");

		String layoutUuid = ParamUtil.getString(
			uploadPortletRequest, "layoutUuid");

		JournalArticle latestArticle = journalArticleService.fetchArticle(
			groupId, articleId);

		if ((displayPageType == AssetDisplayPageConstants.TYPE_DEFAULT) ||
			(displayPageType == AssetDisplayPageConstants.TYPE_SPECIFIC)) {

			Layout targetLayout = journalHelper.getArticleLayout(
				layoutUuid, groupId);

			if ((displayPageType == AssetDisplayPageConstants.TYPE_SPECIFIC) &&
				(targetLayout == null) && (latestArticle != null) &&
				Validator.isNotNull(latestArticle.getLayoutUuid())) {

				Layout latestTargetLayout = journalHelper.getArticleLayout(
					latestArticle.getLayoutUuid(), groupId);

				if (latestTargetLayout == null) {
					layoutUuid = latestArticle.getLayoutUuid();
				}
			}
			else if ((displayPageType ==
						AssetDisplayPageConstants.TYPE_DEFAULT) ||
					 (targetLayout == null)) {

				layoutUuid = null;
			}
		}
		else {
			layoutUuid = null;
		}

		int displayDateMonth = ParamUtil.getInteger(
			uploadPortletRequest, "displayDateMonth");
		int displayDateDay = ParamUtil.getInteger(
			uploadPortletRequest, "displayDateDay");
		int displayDateYear = ParamUtil.getInteger(
			uploadPortletRequest, "displayDateYear");
		int displayDateHour = ParamUtil.getInteger(
			uploadPortletRequest, "displayDateHour");
		int displayDateMinute = ParamUtil.getInteger(
			uploadPortletRequest, "displayDateMinute");
		int displayDateAmPm = ParamUtil.getInteger(
			uploadPortletRequest, "displayDateAmPm");

		if (displayDateAmPm == Calendar.PM) {
			displayDateHour += 12;
		}

		int expirationDateMonth = ParamUtil.getInteger(
			uploadPortletRequest, "expirationDateMonth");
		int expirationDateDay = ParamUtil.getInteger(
			uploadPortletRequest, "expirationDateDay");
		int expirationDateYear = ParamUtil.getInteger(
			uploadPortletRequest, "expirationDateYear");
		int expirationDateHour = ParamUtil.getInteger(
			uploadPortletRequest, "expirationDateHour");
		int expirationDateMinute = ParamUtil.getInteger(
			uploadPortletRequest, "expirationDateMinute");
		int expirationDateAmPm = ParamUtil.getInteger(
			uploadPortletRequest, "expirationDateAmPm");

		boolean neverExpire = ParamUtil.getBoolean(
			uploadPortletRequest, "neverExpire", expirationDateYear == 0);

		if (!PropsValues.SCHEDULER_ENABLED) {
			neverExpire = true;
		}

		if (expirationDateAmPm == Calendar.PM) {
			expirationDateHour += 12;
		}

		int reviewDateMonth = ParamUtil.getInteger(
			uploadPortletRequest, "reviewDateMonth");
		int reviewDateDay = ParamUtil.getInteger(
			uploadPortletRequest, "reviewDateDay");
		int reviewDateYear = ParamUtil.getInteger(
			uploadPortletRequest, "reviewDateYear");
		int reviewDateHour = ParamUtil.getInteger(
			uploadPortletRequest, "reviewDateHour");
		int reviewDateMinute = ParamUtil.getInteger(
			uploadPortletRequest, "reviewDateMinute");
		int reviewDateAmPm = ParamUtil.getInteger(
			uploadPortletRequest, "reviewDateAmPm");

		boolean neverReview = ParamUtil.getBoolean(
			uploadPortletRequest, "neverReview", reviewDateYear == 0);

		if (!PropsValues.SCHEDULER_ENABLED) {
			neverReview = true;
		}

		if (reviewDateAmPm == Calendar.PM) {
			reviewDateHour += 12;
		}

		boolean indexable = ParamUtil.getBoolean(
			uploadPortletRequest, "indexable");

		int smallImageSource = ParamUtil.getInteger(
			uploadPortletRequest, "smallImageSource",
			JournalArticleConstants.SMALL_IMAGE_SOURCE_NONE);

		boolean smallImage = false;

		if (smallImageSource !=
				JournalArticleConstants.SMALL_IMAGE_SOURCE_NONE) {

			smallImage = true;
		}

		long smallImageId = 0;
		String smallImageURL = StringPool.BLANK;
		File smallFile = null;

		if (smallImageSource ==
				JournalArticleConstants.
					SMALL_IMAGE_SOURCE_DOCUMENTS_AND_MEDIA) {

			smallImageId = ParamUtil.getLong(
				uploadPortletRequest, "smallImageId");
		}
		else if (smallImageSource ==
					JournalArticleConstants.SMALL_IMAGE_SOURCE_URL) {

			smallImageURL = ParamUtil.getString(
				uploadPortletRequest, "smallImageURL");
		}
		else if (smallImageSource ==
					JournalArticleConstants.SMALL_IMAGE_SOURCE_USER_COMPUTER) {

			smallFile = uploadPortletRequest.getFile("smallFile");

			if (((smallFile == null) || (smallFile.length() == 0)) &&
				(latestArticle != null)) {

				smallImageId = latestArticle.getSmallImageId();
			}
		}

		String articleURL = ParamUtil.getString(
			uploadPortletRequest, "articleURL");

		serviceContext.setAttribute(
			"updateAutoTags",
			ParamUtil.getBoolean(portletRequest, "updateAutoTags"));

		JournalArticle article = null;

		if (actionName.equals("/journal/add_article")) {

			// Add article

			long classNameId = ParamUtil.getLong(
				uploadPortletRequest, "classNameId");
			long classPK = ParamUtil.getLong(uploadPortletRequest, "classPK");
			boolean autoArticleId = ParamUtil.getBoolean(
				uploadPortletRequest, "autoArticleId");

			article = journalArticleService.addArticle(
				null, groupId, folderId, classNameId, classPK, articleId,
				autoArticleId, titleMap, descriptionMap, friendlyURLMap,
				content, ddmStructureId, ddmTemplateKey, layoutUuid,
				displayDateMonth, displayDateDay, displayDateYear,
				displayDateHour, displayDateMinute, expirationDateMonth,
				expirationDateDay, expirationDateYear, expirationDateHour,
				expirationDateMinute, neverExpire, reviewDateMonth,
				reviewDateDay, reviewDateYear, reviewDateHour, reviewDateMinute,
				neverReview, indexable, smallImage, smallImageId,
				smallImageSource, smallImageURL, smallFile, null, articleURL,
				serviceContext);
		}
		else {

			// Update article

			double version = ParamUtil.getDouble(
				uploadPortletRequest, "version");

			article = journalArticleService.getArticle(
				groupId, articleId, version);

			if (article.isDraft() && (version == 1.0)) {
				Calendar calendar = CalendarFactoryUtil.getCalendar(
					serviceContext.getTimeZone());

				displayDateMinute = calendar.get(Calendar.MINUTE);
				displayDateHour = calendar.get(Calendar.HOUR_OF_DAY);
				displayDateDay = calendar.get(Calendar.DAY_OF_MONTH);
				displayDateMonth = calendar.get(Calendar.MONTH);
				displayDateYear = calendar.get(Calendar.YEAR);
			}

			int count = journalArticleService.getArticlesCountByArticleId(
				article.getGroupId(), article.getArticleId());

			if (!FeatureFlagManagerUtil.isEnabled("LPD-11228") || (count > 1) ||
				!Objects.equals(
					WorkflowConstants.STATUS_DRAFT, article.getStatus())) {

				serviceContext.setModelPermissions(null);
			}

			if (actionName.equals("/journal/update_article")) {
				article = journalArticleService.updateArticle(
					groupId, folderId, articleId, version, titleMap,
					descriptionMap, friendlyURLMap, content, ddmTemplateKey,
					layoutUuid, displayDateMonth, displayDateDay,
					displayDateYear, displayDateHour, displayDateMinute,
					expirationDateMonth, expirationDateDay, expirationDateYear,
					expirationDateHour, expirationDateMinute, neverExpire,
					reviewDateMonth, reviewDateDay, reviewDateYear,
					reviewDateHour, reviewDateMinute, neverReview, indexable,
					smallImage, smallImageId, smallImageSource, smallImageURL,
					smallFile, null, articleURL, serviceContext);
			}
		}

		return article;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JournalArticleUtil.class);

}