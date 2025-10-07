/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.service.impl;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureService;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalFolderService;
import com.liferay.journal.service.base.JournalArticleServiceBaseImpl;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.PortletRequestModel;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.io.File;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Provides the remote service for accessing, adding, deleting, and updating web
 * content articles. Its methods include permission checks.
 *
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 * @author Levente Hudák
 * @see    JournalArticleLocalServiceImpl
 */
@Component(
	property = {
		"json.web.service.context.name=journal",
		"json.web.service.context.path=JournalArticle"
	},
	service = AopService.class
)
public class JournalArticleServiceImpl extends JournalArticleServiceBaseImpl {

	/**
	 * Adds a web content article with additional parameters. All scheduling
	 * parameters (display date, expiration date, and review date) use the
	 * current user's timezone.
	 *
	 * @param  externalReferenceCode the external reference code of the web
	 *         content article
	 * @param  groupId the primary key of the web content article's group
	 * @param  folderId the primary key of the web content article folder
	 * @param  classNameId the primary key of the DDMStructure class if the web
	 *         content article is related to a DDM structure, the primary key of
	 *         the class name associated with the article, or
	 *         JournalArticleConstants.CLASS_NAME_ID_DEFAULT in the journal-api
	 *         module otherwise
	 * @param  classPK the primary key of the DDM structure, if the primary key
	 *         of the DDMStructure class is given as the
	 *         <code>classNameId</code> parameter, the primary key of the class
	 *         associated with the web content article, or <code>0</code>
	 *         otherwise
	 * @param  articleId the primary key of the web content article
	 * @param  autoArticleId whether to auto generate the web content article ID
	 * @param  titleMap the web content article's locales and localized titles
	 * @param  descriptionMap the web content article's locales and localized
	 *         descriptions
	 * @param  friendlyURLMap the web content article's locales and localized
	 *         friendly URLs
	 * @param  content the HTML content wrapped in XML. For more information,
	 *         see the content example in the {@link #updateArticle(long, long,
	 *         String, double, String, ServiceContext)} description.
	 * @param  ddmStructureId the primary key of the web content article's DDM
	 *         structure, if the article is related to a DDM structure, or
	 *         <code>0</code> otherwise
	 * @param  ddmTemplateKey the primary key of the web content article's DDM
	 *         template
	 * @param  layoutUuid the unique string identifying the web content
	 *         article's display page
	 * @param  displayDateMonth the month the web content article is set to
	 *         display
	 * @param  displayDateDay the calendar day the web content article is set to
	 *         display
	 * @param  displayDateYear the year the web content article is set to
	 *         display
	 * @param  displayDateHour the hour the web content article is set to
	 *         display
	 * @param  displayDateMinute the minute the web content article is set to
	 *         display
	 * @param  expirationDateMonth the month the web content article is set to
	 *         expire
	 * @param  expirationDateDay the calendar day the web content article is set
	 *         to expire
	 * @param  expirationDateYear the year the web content article is set to
	 *         expire
	 * @param  expirationDateHour the hour the web content article is set to
	 *         expire
	 * @param  expirationDateMinute the minute the web content article is set to
	 *         expire
	 * @param  neverExpire whether the web content article is not set to auto
	 *         expire
	 * @param  reviewDateMonth the month the web content article is set for
	 *         review
	 * @param  reviewDateDay the calendar day the web content article is set for
	 *         review
	 * @param  reviewDateYear the year the web content article is set for review
	 * @param  reviewDateHour the hour the web content article is set for review
	 * @param  reviewDateMinute the minute the web content article is set for
	 *         review
	 * @param  neverReview whether the web content article is not set for review
	 * @param  indexable whether the web content article is searchable
	 * @param  smallImage whether the web content article has a small image
	 * @param  smallImageSource the web content article's small image source
	 * @param  smallImageURL the web content article's small image URL
	 * @param  smallFile the web content article's small image file
	 * @param  images the web content's images
	 * @param  articleURL the web content article's accessible URL
	 * @param  serviceContext the service context to be applied. Can set the
	 *         UUID, creation date, modification date, expando bridge
	 *         attributes, guest permissions, group permissions, asset category
	 *         IDs, asset tag names, asset link entry IDs, asset priority, URL
	 *         title, and workflow actions for the web content article. Can also
	 *         set whether to add the default guest and group permissions.
	 * @return the web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle addArticle(
			String externalReferenceCode, long groupId, long folderId,
			long classNameId, long classPK, String articleId,
			boolean autoArticleId, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap,
			Map<Locale, String> friendlyURLMap, String content,
			long ddmStructureId, String ddmTemplateKey, String layoutUuid,
			int displayDateMonth, int displayDateDay, int displayDateYear,
			int displayDateHour, int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, int reviewDateMonth, int reviewDateDay,
			int reviewDateYear, int reviewDateHour, int reviewDateMinute,
			boolean neverReview, boolean indexable, boolean smallImage,
			long smallImageId, int smallImageSource, String smallImageURL,
			File smallFile, Map<String, byte[]> images, String articleURL,
			ServiceContext serviceContext)
		throws PortalException {

		ModelResourcePermissionUtil.check(
			_journalFolderModelResourcePermission, getPermissionChecker(),
			groupId, folderId, ActionKeys.ADD_ARTICLE);

		return journalArticleLocalService.addArticle(
			externalReferenceCode, getUserId(), groupId, folderId, classNameId,
			classPK, articleId, autoArticleId,
			JournalArticleConstants.VERSION_DEFAULT, titleMap, descriptionMap,
			friendlyURLMap, content, ddmStructureId, ddmTemplateKey, layoutUuid,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire, reviewDateMonth, reviewDateDay, reviewDateYear,
			reviewDateHour, reviewDateMinute, neverReview, indexable,
			smallImage, smallImageId, smallImageSource, smallImageURL,
			smallFile, images, articleURL, serviceContext);
	}

	/**
	 * Adds a web content article.
	 *
	 * @param  externalReferenceCode the external reference code of the web
	 *         content article
	 * @param  groupId the primary key of the web content article's group
	 * @param  folderId the primary key of the web content article folder
	 * @param  titleMap the web content article's locales and localized titles
	 * @param  descriptionMap the web content article's locales and localized
	 *         descriptions
	 * @param  content the HTML content wrapped in XML. For more information,
	 *         see the content example in the {@link #updateArticle(long, long,
	 *         String, double, String, ServiceContext)} description.
	 * @param  ddmStructureId the primary key of the web content article's DDM
	 *         structure, if the article is related to a DDM structure, or
	 *         <code>0</code> otherwise
	 * @param  ddmTemplateKey the primary key of the web content article's DDM
	 *         template
	 * @param  serviceContext the service context to be applied. Can set the
	 *         UUID, creation date, modification date, expando bridge
	 *         attributes, guest permissions, group permissions, asset category
	 *         IDs, asset tag names, asset link entry IDs, asset priority, URL
	 *         title, and workflow actions for the web content article. Can also
	 *         set whether to add the default guest and group permissions.
	 * @return the web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle addArticle(
			String externalReferenceCode, long groupId, long folderId,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			String content, long ddmStructureId, String ddmTemplateKey,
			ServiceContext serviceContext)
		throws PortalException {

		ModelResourcePermissionUtil.check(
			_journalFolderModelResourcePermission, getPermissionChecker(),
			groupId, folderId, ActionKeys.ADD_ARTICLE);

		return journalArticleLocalService.addArticle(
			externalReferenceCode, getUserId(), groupId, folderId, titleMap,
			descriptionMap, content, ddmStructureId, ddmTemplateKey,
			serviceContext);
	}

	@Override
	public JournalArticle addArticleDefaultValues(
			long groupId, long classNameId, long classPK,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			String content, long ddmStructureId, String ddmTemplateKey,
			String layoutUuid, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute,
			int expirationDateMonth, int expirationDateDay,
			int expirationDateYear, int expirationDateHour,
			int expirationDateMinute, boolean neverExpire, int reviewDateMonth,
			int reviewDateDay, int reviewDateYear, int reviewDateHour,
			int reviewDateMinute, boolean neverReview, boolean indexable,
			boolean smallImage, long smallImageId, int smallImageSource,
			String smallImageURL, File smallImageFile,
			ServiceContext serviceContext)
		throws PortalException {

		DDMStructure ddmStructure = _ddmStructureService.getStructure(
			ddmStructureId);

		_ddmStructureModelResourcePermission.contains(
			getPermissionChecker(), ddmStructure, ActionKeys.UPDATE);

		return journalArticleLocalService.addArticleDefaultValues(
			getUserId(), groupId, classNameId, classPK, titleMap,
			descriptionMap, content, ddmStructureId, ddmTemplateKey, layoutUuid,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire, reviewDateMonth, reviewDateDay, reviewDateYear,
			reviewDateHour, reviewDateMinute, neverReview, indexable,
			smallImage, smallImageId, smallImageSource, smallImageURL,
			smallImageFile, serviceContext);
	}

	/**
	 * Copies the web content article matching the group, article ID, and
	 * version. This method creates a new article, extracting all the values
	 * from the old one and updating its article ID.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  sourceArticleId the primary key of the old web content article
	 * @param  targetArticleId the primary key of the new web content article
	 * @param  autoArticleId whether to auto-generate the web content article ID
	 * @param  version the web content article's version
	 * @return the new web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle copyArticle(
			long groupId, String sourceArticleId, String targetArticleId,
			boolean autoArticleId, double version)
		throws PortalException {

		JournalArticle sourceArticle = getArticle(groupId, sourceArticleId);

		ModelResourcePermissionUtil.check(
			_journalFolderModelResourcePermission, getPermissionChecker(),
			groupId, sourceArticle.getFolderId(), ActionKeys.ADD_ARTICLE);

		return journalArticleLocalService.copyArticle(
			getUserId(), groupId, sourceArticleId, targetArticleId,
			autoArticleId, version);
	}

	/**
	 * Deletes the web content article and its resources matching the group,
	 * article ID, and version, optionally sending email notifying denial of the
	 * web content article if it had not yet been approved.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  articleId the primary key of the web content article
	 * @param  version the web content article's version
	 * @param  articleURL the web content article's accessible URL
	 * @param  serviceContext the service context to be applied. Can set the
	 *         portlet preferences that include email information to notify
	 *         recipients of the unapproved web content article's denial.
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void deleteArticle(
			long groupId, String articleId, double version, String articleURL,
			ServiceContext serviceContext)
		throws PortalException {

		JournalArticle article = journalArticlePersistence.findByG_A_V(
			groupId, articleId, version);

		_journalArticleModelResourcePermission.check(
			getPermissionChecker(), article, ActionKeys.DELETE);

		journalArticleLocalService.deleteArticle(
			article, articleURL, serviceContext);
	}

	/**
	 * Deletes all web content articles and their resources matching the group
	 * and article ID, optionally sending email notifying denial of article if
	 * it had not yet been approved.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  articleId the primary key of the web content article
	 * @param  articleURL the web content article's accessible URL
	 * @param  serviceContext the service context to be applied. Can set the
	 *         portlet preferences that include email information to notify
	 *         recipients of the unapproved web content article's denial.
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void deleteArticle(
			long groupId, String articleId, String articleURL,
			ServiceContext serviceContext)
		throws PortalException {

		_journalArticleModelResourcePermission.check(
			getPermissionChecker(),
			journalArticleLocalService.getArticle(groupId, articleId),
			ActionKeys.DELETE);

		journalArticleLocalService.deleteArticle(
			groupId, articleId, serviceContext);
	}

	@Override
	public void deleteArticleDefaultValues(
			long groupId, String articleId, long ddmStructureId)
		throws PortalException {

		DDMStructure ddmStructure = _ddmStructureService.getStructure(
			ddmStructureId);

		_ddmStructureModelResourcePermission.contains(
			getPermissionChecker(), ddmStructure, ActionKeys.UPDATE);

		journalArticleLocalService.deleteArticleDefaultValues(
			groupId, articleId, ddmStructureId);
	}

	/**
	 * Expires the web content article matching the group, article ID, and
	 * version.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  articleId the primary key of the web content article
	 * @param  version the web content article's version
	 * @param  articleURL the web content article's accessible URL
	 * @param  serviceContext the service context to be applied. Can set the
	 *         modification date, status date, portlet preferences, and can set
	 *         whether to add the default command update for the web content
	 *         article. With respect to social activities, by setting the
	 *         service context's command to {@link
	 *         com.liferay.portal.kernel.util.Constants#UPDATE}, the invocation
	 *         is considered a web content update activity; otherwise it is
	 *         considered a web content add activity.
	 * @return the web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle expireArticle(
			long groupId, String articleId, double version, String articleURL,
			ServiceContext serviceContext)
		throws PortalException {

		JournalArticle article = journalArticlePersistence.findByG_A_V(
			groupId, articleId, version);

		_journalArticleModelResourcePermission.check(
			getPermissionChecker(), article, ActionKeys.EXPIRE);

		return journalArticleLocalService.updateStatus(
			getUserId(), article, WorkflowConstants.STATUS_EXPIRED, articleURL,
			serviceContext, new HashMap<>());
	}

	/**
	 * Expires the web content article matching the group and article ID,
	 * expiring all of its versions if the
	 * <code>journal.article.expire.all.versions</code> portal property is
	 * <code>true</code>, otherwise expiring only its latest approved version.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  articleId the primary key of the web content article
	 * @param  articleURL the web content article's accessible URL
	 * @param  serviceContext the service context to be applied. Can set the
	 *         modification date, status date, portlet preferences, and can set
	 *         whether to add the default command update for the web content
	 *         article. With respect to social activities, by setting the
	 *         service context's command to {@link
	 *         com.liferay.portal.kernel.util.Constants#UPDATE}, the invocation
	 *         is considered a web content update activity; otherwise it is
	 *         considered a web content add activity.
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void expireArticle(
			long groupId, String articleId, String articleURL,
			ServiceContext serviceContext)
		throws PortalException {

		_journalArticleModelResourcePermission.check(
			getPermissionChecker(),
			journalArticleLocalService.getArticle(groupId, articleId),
			ActionKeys.EXPIRE);

		journalArticleLocalService.expireArticle(
			getUserId(), groupId, articleId, articleURL, serviceContext);
	}

	@Override
	public JournalArticle fetchArticle(long groupId, String articleId)
		throws PortalException {

		JournalArticle article = journalArticleLocalService.fetchArticle(
			groupId, articleId);

		if (article != null) {
			_journalArticleModelResourcePermission.check(
				getPermissionChecker(), article, ActionKeys.VIEW);
		}

		return article;
	}

	/**
	 * Returns the latest web content article matching the group and the
	 * external reference code.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  externalReferenceCode the external reference code of the web
	 *         content article
	 * @return the latest matching web content article, or <code>null</code> if
	 *         no matching web content article could be found
	 */
	@Override
	public JournalArticle fetchLatestArticleByExternalReferenceCode(
			long groupId, String externalReferenceCode)
		throws PortalException {

		JournalArticle article =
			journalArticleLocalService.
				fetchLatestArticleByExternalReferenceCode(
					groupId, externalReferenceCode);

		if (article != null) {
			_journalArticleModelResourcePermission.check(
				getPermissionChecker(), article, ActionKeys.VIEW);
		}

		return article;
	}

	/**
	 * Returns the web content article with the ID.
	 *
	 * @param  id the primary key of the web content article
	 * @return the web content article with the ID
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle getArticle(long id) throws PortalException {
		JournalArticle article = journalArticleLocalService.getArticle(id);

		_journalArticleModelResourcePermission.check(
			getPermissionChecker(), article, ActionKeys.VIEW);

		return article;
	}

	/**
	 * Returns the latest approved web content article, or the latest unapproved
	 * article if none are approved. Both approved and unapproved articles must
	 * match the group and article ID.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  articleId the primary key of the web content article
	 * @return the matching web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle getArticle(long groupId, String articleId)
		throws PortalException {

		JournalArticle article = journalArticleLocalService.getArticle(
			groupId, articleId);

		_journalArticleModelResourcePermission.check(
			getPermissionChecker(), article, ActionKeys.VIEW);

		return article;
	}

	/**
	 * Returns the web content article matching the group, article ID, and
	 * version.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  articleId the primary key of the web content article
	 * @param  version the web content article's version
	 * @return the matching web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle getArticle(
			long groupId, String articleId, double version)
		throws PortalException {

		JournalArticle article = journalArticleLocalService.getArticle(
			groupId, articleId, version);

		_journalArticleModelResourcePermission.check(
			getPermissionChecker(), article, ActionKeys.VIEW);

		return article;
	}

	/**
	 * Returns the web content article matching the group, class name, and class
	 * PK.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  className the DDMStructure class name if the web content article
	 *         is related to a DDM structure, the primary key of the class name
	 *         associated with the article, or
	 *         JournalArticleConstants.CLASS_NAME_ID_DEFAULT in the journal-api
	 *         module otherwise
	 * @param  classPK the primary key of the DDM structure, if the DDMStructure
	 *         class name is given as the <code>className</code> parameter, the
	 *         primary key of the class associated with the web content article,
	 *         or <code>0</code> otherwise
	 * @return the matching web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle getArticle(
			long groupId, String className, long classPK)
		throws PortalException {

		JournalArticle article = journalArticleLocalService.getArticle(
			groupId, className, classPK);

		_journalArticleModelResourcePermission.check(
			getPermissionChecker(), article, ActionKeys.VIEW);

		return article;
	}

	/**
	 * Returns the latest web content article that is approved, or the latest
	 * unapproved article if none are approved. Both approved and unapproved
	 * articles must match the group and URL title.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  urlTitle the web content article's accessible URL title
	 * @return the matching web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle getArticleByUrlTitle(long groupId, String urlTitle)
		throws PortalException {

		JournalArticle article =
			journalArticleLocalService.getArticleByUrlTitle(groupId, urlTitle);

		_journalArticleModelResourcePermission.check(
			getPermissionChecker(), article, ActionKeys.VIEW);

		return article;
	}

	/**
	 * Returns the web content from the web content article matching the group,
	 * article ID, and version.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  articleId the primary key of the web content article
	 * @param  version the web content article's version
	 * @param  languageId the primary key of the language translation to get
	 * @param  portletRequestModel the portlet request model
	 * @param  themeDisplay the theme display
	 * @return the matching web content
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public String getArticleContent(
			long groupId, String articleId, double version, String languageId,
			PortletRequestModel portletRequestModel, ThemeDisplay themeDisplay)
		throws PortalException {

		JournalArticle article = journalArticlePersistence.findByG_A_V(
			groupId, articleId, version);

		_journalArticleModelResourcePermission.check(
			getPermissionChecker(), article, ActionKeys.VIEW);

		return journalArticleLocalService.getArticleContent(
			groupId, articleId, version, null, article.getDDMTemplateKey(),
			languageId, portletRequestModel, themeDisplay);
	}

	/**
	 * Returns the latest web content from the web content article matching the
	 * group and article ID.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  articleId the primary key of the web content article
	 * @param  languageId the primary key of the language translation to get
	 * @param  portletRequestModel the portlet request model
	 * @param  themeDisplay the theme display
	 * @return the matching web content
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public String getArticleContent(
			long groupId, String articleId, String languageId,
			PortletRequestModel portletRequestModel, ThemeDisplay themeDisplay)
		throws PortalException {

		JournalArticle article = journalArticleLocalService.getArticle(
			groupId, articleId);

		_journalArticleModelResourcePermission.check(
			getPermissionChecker(), article, ActionKeys.VIEW);

		return journalArticleLocalService.getArticleContent(
			groupId, articleId, null, article.getDDMTemplateKey(), languageId,
			portletRequestModel, themeDisplay);
	}

	@Override
	public List<JournalArticle> getArticles(
		long groupId, long folderId, Locale locale) {

		List<Long> folderIds = new ArrayList<>();

		folderIds.add(folderId);

		QueryDefinition<JournalArticle> queryDefinition = new QueryDefinition<>(
			WorkflowConstants.STATUS_ANY);

		return journalArticleFinder.filterFindByG_F_L(
			groupId, folderIds, locale, queryDefinition);
	}

	@Override
	public List<JournalArticle> getArticles(
		long groupId, long folderId, Locale locale, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		List<Long> folderIds = new ArrayList<>();

		folderIds.add(folderId);

		QueryDefinition<JournalArticle> queryDefinition = new QueryDefinition<>(
			WorkflowConstants.STATUS_ANY, start, end, orderByComparator);

		return journalArticleFinder.filterFindByG_F_L(
			groupId, folderIds, locale, queryDefinition);
	}

	@Override
	public List<JournalArticle> getArticlesByArticleId(
		long groupId, String articleId, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return journalArticlePersistence.filterFindByG_A(
				groupId, articleId, start, end, orderByComparator);
		}

		return journalArticlePersistence.filterFindByG_A_ST(
			groupId, articleId, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the web content articles matching the
	 * group and article ID.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link
	 * com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  articleId the primary key of the web content article
	 * @param  start the lower bound of the range of web content articles to
	 *         return
	 * @param  end the upper bound of the range of web content articles to
	 *         return (not inclusive)
	 * @param  orderByComparator the comparator to order the web content
	 *         articles
	 * @return the range of matching web content articles ordered by the
	 *         comparator
	 */
	@Override
	public List<JournalArticle> getArticlesByArticleId(
		long groupId, String articleId, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return journalArticlePersistence.filterFindByG_A(
			groupId, articleId, start, end, orderByComparator);
	}

	/**
	 * Returns all the web content articles matching the group and layout UUID.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  layoutUuid the unique string identifying the web content
	 *         article's display page
	 * @return the matching web content articles
	 */
	@Override
	public List<JournalArticle> getArticlesByLayoutUuid(
		long groupId, String layoutUuid) {

		return journalArticlePersistence.filterFindByG_L(groupId, layoutUuid);
	}

	/**
	 * Returns all the web content articles that the user has permission to view
	 * matching the group and layout UUID.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link
	 * com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  layoutUuid the unique string identifying the web content
	 *         article's display page
	 * @param  start the lower bound of the range of web content articles to
	 *         return
	 * @param  end the upper bound of the range of web content articles to
	 *         return (not inclusive)
	 * @return the range of matching web content articles
	 */
	@Override
	public List<JournalArticle> getArticlesByLayoutUuid(
		long groupId, String layoutUuid, int start, int end) {

		return journalArticlePersistence.filterFindByG_L(
			groupId, layoutUuid, start, end);
	}

	/**
	 * Returns the number of web content articles that the user has permission
	 * to view matching the group and layout UUID.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  layoutUuid the unique string identifying the web content
	 *         article's display page
	 * @return the matching web content articles
	 */
	@Override
	public int getArticlesByLayoutUuidCount(long groupId, String layoutUuid) {
		return journalArticlePersistence.filterCountByG_L(groupId, layoutUuid);
	}

	/**
	 * Returns an ordered range of all the web content articles matching the
	 * group, default class name ID, and DDM structure key.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link
	 * com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  ddmStructureId the primary key of the web content article's DDM
	 *         structure
	 * @param  status the web content article's workflow status. For more
	 *         information see {@link WorkflowConstants} for constants starting
	 *         with the "STATUS_" prefix.
	 * @param  start the lower bound of the range of web content articles to
	 *         return
	 * @param  end the upper bound of the range of web content articles to
	 *         return (not inclusive)
	 * @param  orderByComparator the comparator to order the web content
	 *         articles
	 * @return the range of matching web content articles ordered by the
	 *         comparator
	 */
	@Override
	public List<JournalArticle> getArticlesByStructureId(
		long groupId, long ddmStructureId, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		QueryDefinition<JournalArticle> queryDefinition = new QueryDefinition<>(
			status, start, end, orderByComparator);

		return journalArticleFinder.filterFindByG_F_C_S_L(
			groupId, Collections.emptyList(),
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, ddmStructureId,
			LocaleUtil.getMostRelevantLocale(), queryDefinition);
	}

	/**
	 * Returns an ordered range of all the web content articles matching the
	 * group, default class name ID, and DDM structure key.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link
	 * com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  ddmStructureId the primary key of the web content article's DDM
	 *         structure
	 * @param  start the lower bound of the range of web content articles to
	 *         return
	 * @param  end the upper bound of the range of web content articles to
	 *         return (not inclusive)
	 * @param  orderByComparator the comparator to order the web content
	 *         articles
	 * @return the range of matching web content articles ordered by the
	 *         comparator
	 */
	@Override
	public List<JournalArticle> getArticlesByStructureId(
		long groupId, long ddmStructureId, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getArticlesByStructureId(
			groupId, ddmStructureId, WorkflowConstants.STATUS_ANY, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the web content articles matching the
	 * group, default class name ID, and DDM structure key.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link
	 * com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  ddmStructureId the primary key of the web content article's DDM
	 *         structure
	 * @param  locale web content articles locale
	 * @param  status the web content article's workflow status. For more
	 *         information see {@link WorkflowConstants} for constants starting
	 *         with the "STATUS_" prefix.
	 * @param  start the lower bound of the range of web content articles to
	 *         return
	 * @param  end the upper bound of the range of web content articles to
	 *         return (not inclusive)
	 * @param  orderByComparator the comparator to order the web content
	 *         articles
	 * @return the range of matching web content articles ordered by the
	 *         comparator
	 */
	@Override
	public List<JournalArticle> getArticlesByStructureId(
		long groupId, long ddmStructureId, Locale locale, int status, int start,
		int end, OrderByComparator<JournalArticle> orderByComparator) {

		QueryDefinition<JournalArticle> queryDefinition = new QueryDefinition<>(
			status, start, end, orderByComparator);

		return journalArticleFinder.filterFindByG_F_C_S_L(
			groupId, Collections.emptyList(),
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, ddmStructureId,
			locale, queryDefinition);
	}

	/**
	 * Returns an ordered range of all the web content articles matching the
	 * group, class name ID, DDM structure key, and workflow status.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link
	 * com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  classNameId the primary key of the DDMStructure class if the web
	 *         content article is related to a DDM structure, the primary key of
	 *         the class name associated with the article, or
	 *         JournalArticleConstants.CLASS_NAME_ID_DEFAULT in the journal-api
	 *         module otherwise
	 * @param  ddmStructureId the primary key of the web content article's DDM
	 *         structure
	 * @param  status the web content article's workflow status. For more
	 *         information see {@link WorkflowConstants} for constants starting
	 *         with the "STATUS_" prefix.
	 * @param  start the lower bound of the range of web content articles to
	 *         return
	 * @param  end the upper bound of the range of web content articles to
	 *         return (not inclusive)
	 * @param  orderByComparator the comparator to order the web content
	 *         articles
	 * @return the range of matching web content articles ordered by the
	 *         comparator
	 */
	@Override
	public List<JournalArticle> getArticlesByStructureId(
		long groupId, long classNameId, long ddmStructureId, int status,
		int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		QueryDefinition<JournalArticle> queryDefinition = new QueryDefinition<>(
			status, start, end, orderByComparator);

		return journalArticleFinder.filterFindByG_F_C_S_L(
			groupId, Collections.emptyList(), classNameId, ddmStructureId,
			LocaleUtil.getMostRelevantLocale(), queryDefinition);
	}

	/**
	 * Returns an ordered range of all the web content articles matching the
	 * group, class name ID, DDM structure key, and workflow status.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link
	 * com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  classNameId the primary key of the DDMStructure class if the web
	 *         content article is related to a DDM structure, the primary key of
	 *         the class name associated with the article, or
	 *         JournalArticleConstants.CLASS_NAME_ID_DEFAULT in the journal-api
	 *         module otherwise
	 * @param  ddmStructureId the primary key of the web content article's DDM
	 *         structure
	 * @param  status the web content article's workflow status. For more
	 *         information see {@link WorkflowConstants} for constants starting
	 *         with the "STATUS_" prefix.
	 * @param  start the lower bound of the range of web content articles to
	 *         return
	 * @param  end the upper bound of the range of web content articles to
	 *         return (not inclusive)
	 * @param  orderByComparator the comparator to order the web content
	 *         articles
	 * @return the range of matching web content articles ordered by the
	 *         comparator
	 */
	@Override
	public List<JournalArticle> getArticlesByStructureId(
		long groupId, long classNameId, long ddmStructureId, Locale locale,
		int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		QueryDefinition<JournalArticle> queryDefinition = new QueryDefinition<>(
			status, start, end, orderByComparator);

		return journalArticleFinder.filterFindByG_F_C_S_L(
			groupId, Collections.emptyList(), classNameId, ddmStructureId,
			locale, queryDefinition);
	}

	@Override
	public List<JournalArticle> getArticlesByStructureId(
		long groupId, long folderId, long classNameId, long ddmStructureId,
		int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		QueryDefinition<JournalArticle> queryDefinition = new QueryDefinition<>(
			status, start, end, orderByComparator);

		return journalArticleFinder.filterFindByG_F_C_S_L(
			groupId, ListUtil.fromArray(folderId),
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, ddmStructureId,
			LocaleUtil.getMostRelevantLocale(), queryDefinition);
	}

	/**
	 * Returns the number of web content articles matching the group and folder.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  folderId the primary key of the web content article folder
	 * @return the number of matching web content articles
	 */
	@Override
	public int getArticlesCount(long groupId, long folderId) {
		return getArticlesCount(
			groupId, folderId, WorkflowConstants.STATUS_ANY);
	}

	/**
	 * Returns the number of web content articles matching the group, folder,
	 * and status.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  folderId the primary key of the web content article's folder
	 * @param  status the web content article's workflow status. For more
	 *         information see {@link WorkflowConstants} for constants starting
	 *         with the "STATUS_" prefix.
	 * @return the number of matching web content articles
	 */
	@Override
	public int getArticlesCount(long groupId, long folderId, int status) {
		List<Long> folderIds = new ArrayList<>();

		folderIds.add(folderId);

		QueryDefinition<JournalArticle> queryDefinition = new QueryDefinition<>(
			status);

		return journalArticleFinder.filterCountByG_F(
			groupId, folderIds, queryDefinition);
	}

	/**
	 * Returns the number of web content articles matching the group and article
	 * ID.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  articleId the primary key of the web content article
	 * @return the number of matching web content articles
	 */
	@Override
	public int getArticlesCountByArticleId(long groupId, String articleId) {
		return journalArticlePersistence.filterCountByG_A(groupId, articleId);
	}

	@Override
	public int getArticlesCountByArticleId(
		long groupId, String articleId, int status) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return journalArticlePersistence.filterCountByG_A(
				groupId, articleId);
		}

		return journalArticlePersistence.filterCountByG_A_ST(
			groupId, articleId, status);
	}

	/**
	 * Returns the number of web content articles matching the group, default
	 * class name ID, and DDM structure key.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  ddmStructureId the primary key of the web content article's DDM
	 *         structure
	 * @return the number of matching web content articles
	 */
	@Override
	public int getArticlesCountByStructureId(
		long groupId, long ddmStructureId) {

		return getArticlesCountByStructureId(
			groupId, ddmStructureId, WorkflowConstants.STATUS_ANY);
	}

	/**
	 * Returns the number of web content articles matching the group, default
	 * class name ID, and DDM structure key.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  ddmStructureId the primary key of the web content article's DDM
	 *         structure
	 * @param  status the web content article's workflow status. For more
	 *         information see {@link WorkflowConstants} for constants starting
	 *         with the "STATUS_" prefix.
	 * @return the number of matching web content articles
	 */
	@Override
	public int getArticlesCountByStructureId(
		long groupId, long ddmStructureId, int status) {

		return getArticlesCountByStructureId(
			groupId, JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
			ddmStructureId, status);
	}

	/**
	 * Returns the number of web content articles matching the group, class name
	 * ID, DDM structure key, and workflow status.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  classNameId the primary key of the DDMStructure class if the web
	 *         content article is related to a DDM structure, the primary key of
	 *         the class name associated with the article, or
	 *         JournalArticleConstants.CLASS_NAME_ID_DEFAULT in the journal-api
	 *         module otherwise
	 * @param  ddmStructureId the primary key of the web content article's DDM
	 *         structure
	 * @param  status the web content article's workflow status. For more
	 *         information see {@link WorkflowConstants} for constants starting
	 *         with the "STATUS_" prefix.
	 * @return the number of matching web content articles
	 */
	@Override
	public int getArticlesCountByStructureId(
		long groupId, long classNameId, long ddmStructureId, int status) {

		return journalArticleFinder.filterCountByG_F_C_S(
			groupId, Collections.emptyList(), classNameId, ddmStructureId,
			new QueryDefinition<JournalArticle>(status));
	}

	@Override
	public int getArticlesCountByStructureId(
		long groupId, long folderId, long classNameId, long ddmStructureId,
		int status) {

		return journalArticleFinder.filterCountByG_F_C_S(
			groupId, ListUtil.fromArray(folderId), classNameId, ddmStructureId,
			new QueryDefinition<JournalArticle>(status));
	}

	/**
	 * Returns the web content article matching the URL title that is currently
	 * displayed or next to be displayed if no article is currently displayed.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  urlTitle the web content article's accessible URL title
	 * @return the web content article matching the URL title that is currently
	 *         displayed, or next one to be displayed if no version of the
	 *         article is currently displayed
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle getDisplayArticleByUrlTitle(
			long groupId, String urlTitle)
		throws PortalException {

		JournalArticle article =
			journalArticleLocalService.getDisplayArticleByUrlTitle(
				groupId, urlTitle);

		_journalArticleModelResourcePermission.check(
			getPermissionChecker(), article, ActionKeys.VIEW);

		return article;
	}

	/**
	 * Returns the number of folders containing web content articles belonging
	 * to the group.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  folderIds the primary keys of the web content article folders
	 *         (optionally {@link Collections#EMPTY_LIST})
	 * @return the number of matching folders containing web content articles
	 */
	@Override
	public int getFoldersAndArticlesCount(long groupId, List<Long> folderIds) {
		return journalArticlePersistence.filterCountByG_F(
			groupId, ArrayUtil.toArray(folderIds.toArray(new Long[0])));
	}

	/**
	 * Returns an ordered range of all the web content articles matching the
	 * group, user, the root folder or any of its subfolders.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  userId the primary key of the user (optionally <code>0</code>)
	 * @param  rootFolderId the primary key of the root folder to begin the
	 *         search
	 * @param  status the web content article's workflow status. For more
	 *         information see {@link WorkflowConstants} for constants starting
	 *         with the "STATUS_" prefix.
	 * @param  includeOwner whether to include the user's web content
	 * @param  start the lower bound of the range of web content articles to
	 *         return
	 * @param  end the upper bound of the range of web content articles to
	 *         return (not inclusive)
	 * @param  orderByComparator the comparator to order the web content
	 *         articles
	 * @return the range of matching web content articles ordered by the
	 *         comparator
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public List<JournalArticle> getGroupArticles(
			long groupId, long userId, long rootFolderId, int status,
			boolean includeOwner, int start, int end,
			OrderByComparator<JournalArticle> orderByComparator)
		throws PortalException {

		List<Long> folderIds = new ArrayList<>();

		if (rootFolderId != JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			folderIds = _journalFolderService.getFolderIds(
				groupId, rootFolderId);
		}

		QueryDefinition<JournalArticle> queryDefinition = new QueryDefinition<>(
			status, userId, includeOwner, start, end, orderByComparator);

		return journalArticleFinder.filterFindByG_F_C_L(
			groupId, folderIds, JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
			LocaleUtil.getMostRelevantLocale(), queryDefinition);
	}

	/**
	 * Returns an ordered range of all the web content articles matching the
	 * group, user, the root folder or any of its subfolders.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  userId the primary key of the user (optionally <code>0</code>)
	 * @param  rootFolderId the primary key of the root folder to begin the
	 *         search
	 * @param  status the web content article's workflow status. For more
	 *         information see {@link WorkflowConstants} for constants starting
	 *         with the "STATUS_" prefix.
	 * @param  includeOwner whether to include the user's web content
	 * @param  locale web content articles locale
	 * @param  start the lower bound of the range of web content articles to
	 *         return
	 * @param  end the upper bound of the range of web content articles to
	 *         return (not inclusive)
	 * @param  orderByComparator the comparator to order the web content
	 *         articles
	 * @return the range of matching web content articles ordered by the
	 *         comparator
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public List<JournalArticle> getGroupArticles(
			long groupId, long userId, long rootFolderId, int status,
			boolean includeOwner, Locale locale, int start, int end,
			OrderByComparator<JournalArticle> orderByComparator)
		throws PortalException {

		List<Long> folderIds = new ArrayList<>();

		if (rootFolderId != JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			folderIds = _journalFolderService.getFolderIds(
				groupId, rootFolderId);
		}

		QueryDefinition<JournalArticle> queryDefinition = new QueryDefinition<>(
			status, userId, includeOwner, start, end, orderByComparator);

		return journalArticleFinder.filterFindByG_F_C_L(
			groupId, folderIds, JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
			locale, queryDefinition);
	}

	/**
	 * Returns an ordered range of all the web content articles matching the
	 * group, user, the root folder or any of its subfolders.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link
	 * com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  userId the primary key of the user (optionally <code>0</code>)
	 * @param  rootFolderId the primary key of the root folder to begin the
	 *         search
	 * @param  status the web content article's workflow status. For more
	 *         information see {@link WorkflowConstants} for constants starting
	 *         with the "STATUS_" prefix.
	 * @param  start the lower bound of the range of web content articles to
	 *         return
	 * @param  end the upper bound of the range of web content articles to
	 *         return (not inclusive)
	 * @param  orderByComparator the comparator to order the web content
	 *         articles
	 * @return the range of matching web content articles ordered by the
	 *         comparator
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public List<JournalArticle> getGroupArticles(
			long groupId, long userId, long rootFolderId, int status, int start,
			int end, OrderByComparator<JournalArticle> orderByComparator)
		throws PortalException {

		return getGroupArticles(
			groupId, userId, rootFolderId, status, false, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the web content articles matching the
	 * group, user, the root folder or any of its subfolders.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link
	 * com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  userId the primary key of the user (optionally <code>0</code>)
	 * @param  rootFolderId the primary key of the root folder to begin the
	 *         search
	 * @param  start the lower bound of the range of web content articles to
	 *         return
	 * @param  end the upper bound of the range of web content articles to
	 *         return (not inclusive)
	 * @param  orderByComparator the comparator to order the web content
	 *         articles
	 * @return the range of matching web content articles ordered by the
	 *         comparator
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public List<JournalArticle> getGroupArticles(
			long groupId, long userId, long rootFolderId, int start, int end,
			OrderByComparator<JournalArticle> orderByComparator)
		throws PortalException {

		return getGroupArticles(
			groupId, userId, rootFolderId, WorkflowConstants.STATUS_ANY, start,
			end, orderByComparator);
	}

	/**
	 * Returns the number of web content articles matching the group, user, and
	 * the root folder or any of its subfolders.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  userId the primary key of the user (optionally <code>0</code>)
	 * @param  rootFolderId the primary key of the root folder to begin the
	 *         search
	 * @return the number of matching web content articles
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public int getGroupArticlesCount(
			long groupId, long userId, long rootFolderId)
		throws PortalException {

		return getGroupArticlesCount(
			groupId, userId, rootFolderId, WorkflowConstants.STATUS_ANY);
	}

	/**
	 * Returns the number of web content articles matching the group, user, and
	 * the root folder or any of its subfolders.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  userId the primary key of the user (optionally <code>0</code>)
	 * @param  rootFolderId the primary key of the root folder to begin the
	 *         search
	 * @param  status the web content article's workflow status. For more
	 *         information see {@link WorkflowConstants} for constants starting
	 *         with the "STATUS_" prefix.
	 * @return the number of matching web content articles
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public int getGroupArticlesCount(
			long groupId, long userId, long rootFolderId, int status)
		throws PortalException {

		return getGroupArticlesCount(
			groupId, userId, rootFolderId, status, false);
	}

	/**
	 * Returns the number of web content articles matching the group, user, the
	 * root folder or any of its subfolders.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  userId the primary key of the user (optionally <code>0</code>)
	 * @param  rootFolderId the primary key of the root folder to begin the
	 *         search
	 * @param  status the web content article's workflow status. For more
	 *         information see {@link WorkflowConstants} for constants starting
	 *         with the "STATUS_" prefix.
	 * @param  includeOwner whether to include the user's web content
	 * @return the range of matching web content articles ordered by the
	 *         comparator
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public int getGroupArticlesCount(
			long groupId, long userId, long rootFolderId, int status,
			boolean includeOwner)
		throws PortalException {

		List<Long> folderIds = new ArrayList<>();

		if (rootFolderId != JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			folderIds = _journalFolderService.getFolderIds(
				groupId, rootFolderId);
		}

		QueryDefinition<JournalArticle> queryDefinition = new QueryDefinition<>(
			status, userId, includeOwner);

		return journalArticleFinder.filterCountByG_F_C(
			groupId, folderIds, JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
			queryDefinition);
	}

	/**
	 * Returns the latest web content article matching the resource primary key,
	 * preferring articles with approved workflow status.
	 *
	 * @param  resourcePrimKey the primary key of the resource instance
	 * @return the latest web content article matching the resource primary key,
	 *         preferring articles with approved workflow status
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle getLatestArticle(long resourcePrimKey)
		throws PortalException {

		_journalArticleModelResourcePermission.check(
			getPermissionChecker(), resourcePrimKey, ActionKeys.VIEW);

		return journalArticleLocalService.getLatestArticle(resourcePrimKey);
	}

	/**
	 * Returns the latest web content article matching the group, article ID,
	 * and workflow status.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  articleId the primary key of the web content article
	 * @param  status the web content article's workflow status. For more
	 *         information see {@link WorkflowConstants} for constants starting
	 *         with the "STATUS_" prefix.
	 * @return the latest matching web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle getLatestArticle(
			long groupId, String articleId, int status)
		throws PortalException {

		JournalArticle article = journalArticleLocalService.getLatestArticle(
			groupId, articleId, status);

		_journalArticleModelResourcePermission.check(
			getPermissionChecker(), article, ActionKeys.VIEW);

		return article;
	}

	/**
	 * Returns the latest web content article matching the group, class name ID,
	 * and class PK.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  className the DDMStructure class name if the web content article
	 *         is related to a DDM structure, the class name associated with the
	 *         article, or JournalArticleConstants.CLASS_NAME_ID_DEFAULT in the
	 *         journal-api module otherwise
	 * @param  classPK the primary key of the DDM structure, if the DDMStructure
	 *         class name is given as the <code>className</code> parameter, the
	 *         primary key of the class associated with the web content article,
	 *         or <code>0</code> otherwise
	 * @return the latest matching web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle getLatestArticle(
			long groupId, String className, long classPK)
		throws PortalException {

		JournalArticle article = journalArticleLocalService.getLatestArticle(
			groupId, className, classPK);

		_journalArticleModelResourcePermission.check(
			getPermissionChecker(), article, ActionKeys.VIEW);

		return article;
	}

	/**
	 * Returns the latest web content article matching the group and the
	 * external reference code.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  externalReferenceCode the external reference code of the web
	 *         content article
	 * @return the latest matching web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle getLatestArticleByExternalReferenceCode(
			long groupId, String externalReferenceCode)
		throws PortalException {

		JournalArticle article =
			journalArticleLocalService.getLatestArticleByExternalReferenceCode(
				groupId, externalReferenceCode);

		_journalArticleModelResourcePermission.check(
			getPermissionChecker(), article, ActionKeys.VIEW);

		return article;
	}

	@Override
	public List<JournalArticle> getLatestArticles(
		long groupId, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		QueryDefinition<JournalArticle> queryDefinition = new QueryDefinition<>(
			status, start, end, orderByComparator);

		Locale locale = LocaleUtil.getMostRelevantLocale();

		return journalArticleFinder.filterFindByG_ST_L(
			groupId, status, locale, queryDefinition);
	}

	@Override
	public int getLatestArticlesCount(long groupId, int status) {
		return journalArticleFinder.filterCountByG_ST(
			groupId, status, new QueryDefinition<>(status));
	}

	/**
	 * Returns all the web content articles that the user has permission to view
	 * matching the group.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @return The matching web content articles
	 */
	@Override
	public List<JournalArticle> getLayoutArticles(long groupId) {
		return journalArticlePersistence.filterFindByG_NotL(
			groupId, new String[] {null, StringPool.BLANK});
	}

	/**
	 * Returns all the web content articles that the user has permission to view
	 * matching the group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link
	 * com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  start the lower bound of the range of web content articles to
	 *         return
	 * @param  end the upper bound of the range of web content articles to
	 *         return (not inclusive)
	 * @return the range of matching web content articles
	 */
	@Override
	public List<JournalArticle> getLayoutArticles(
		long groupId, int start, int end) {

		return journalArticlePersistence.filterFindByG_NotL(
			groupId, new String[] {null, StringPool.BLANK}, start, end);
	}

	/**
	 * Returns the number of web content articles that the user has permission
	 * to view matching the group.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @return the number of matching web content articles
	 */
	@Override
	public int getLayoutArticlesCount(long groupId) {
		return journalArticlePersistence.filterCountByG_NotL(
			groupId, new String[] {null, StringPool.BLANK});
	}

	/**
	 * Moves all versions of the web content article matching the group and
	 * article ID to the folder.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  articleId the primary key of the web content article
	 * @param  newFolderId the primary key of the web content article's new
	 *         folder
	 * @param  serviceContext the service context to be applied. Can set the
	 *         user ID, language ID, portlet preferences, portlet request,
	 *         portlet response, theme display, and can set whether to add the
	 *         default command update for the web content article. With respect
	 *         to social activities, by setting the service context's command to
	 *         {@link com.liferay.portal.kernel.util.Constants#UPDATE}, the
	 *         invocation is considered a web content update activity; otherwise
	 *         it is considered a web content add activity.
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void moveArticle(
			long groupId, String articleId, long newFolderId,
			ServiceContext serviceContext)
		throws PortalException {

		ModelResourcePermissionUtil.check(
			_journalFolderModelResourcePermission, getPermissionChecker(),
			groupId, newFolderId, ActionKeys.ADD_ARTICLE);

		_journalArticleModelResourcePermission.check(
			getPermissionChecker(),
			journalArticleLocalService.getArticle(groupId, articleId),
			ActionKeys.UPDATE);

		journalArticleLocalService.moveArticle(
			groupId, articleId, newFolderId, serviceContext);
	}

	/**
	 * Moves the web content article from the Recycle Bin to the folder.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  resourcePrimKey the primary key of the resource instance
	 * @param  newFolderId the primary key of the web content article's new
	 *         folder
	 * @param  serviceContext the service context to be applied. Can set the
	 *         modification date, portlet preferences, and can set whether to
	 *         add the default command update for the web content article. With
	 *         respect to social activities, by setting the service context's
	 *         command to {@link
	 *         com.liferay.portal.kernel.util.Constants#UPDATE}, the invocation
	 *         is considered a web content update activity; otherwise it is
	 *         considered a web content add activity.
	 * @return the updated web content article, which was moved from the Recycle
	 *         Bin to the folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle moveArticleFromTrash(
			long groupId, long resourcePrimKey, long newFolderId,
			ServiceContext serviceContext)
		throws PortalException {

		JournalArticle article = getLatestArticle(resourcePrimKey);

		_journalArticleModelResourcePermission.check(
			getPermissionChecker(), article, ActionKeys.UPDATE);

		return journalArticleLocalService.moveArticleFromTrash(
			getUserId(), groupId, article, newFolderId, serviceContext);
	}

	/**
	 * Moves the web content article from the Recycle Bin to the folder.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  articleId the primary key of the web content article
	 * @param  newFolderId the primary key of the web content article's new
	 *         folder
	 * @param  serviceContext the service context to be applied. Can set the
	 *         modification date, portlet preferences, and can set whether to
	 *         add the default command update for the web content article. With
	 *         respect to social activities, by setting the service context's
	 *         command to {@link
	 *         com.liferay.portal.kernel.util.Constants#UPDATE}, the invocation
	 *         is considered a web content update activity; otherwise it is
	 *         considered a web content add activity.
	 * @return the updated web content article, which was moved from the Recycle
	 *         Bin to the folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle moveArticleFromTrash(
			long groupId, String articleId, long newFolderId,
			ServiceContext serviceContext)
		throws PortalException {

		JournalArticle article = getLatestArticle(
			groupId, articleId, WorkflowConstants.STATUS_IN_TRASH);

		_journalArticleModelResourcePermission.check(
			getPermissionChecker(), article, ActionKeys.UPDATE);

		return journalArticleLocalService.moveArticleFromTrash(
			getUserId(), groupId, article, newFolderId, serviceContext);
	}

	/**
	 * Moves the latest version of the web content article matching the group
	 * and article ID to the recycle bin.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  articleId the primary key of the web content article
	 * @return the moved web content article or <code>null</code> if no matching
	 *         article was found
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle moveArticleToTrash(long groupId, String articleId)
		throws PortalException {

		_journalArticleModelResourcePermission.check(
			getPermissionChecker(),
			journalArticleLocalService.getArticle(groupId, articleId),
			ActionKeys.DELETE);

		return journalArticleLocalService.moveArticleToTrash(
			getUserId(), groupId, articleId);
	}

	/**
	 * Removes the web content of all the company's web content articles
	 * matching the language.
	 *
	 * @param  companyId the primary key of the web content article's company
	 * @param  languageId the primary key of the language locale to remove
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void removeArticleLocale(long companyId, String languageId)
		throws PortalException {

		for (JournalArticle article :
				journalArticlePersistence.findByCompanyId(companyId)) {

			removeArticleLocale(
				article.getGroupId(), article.getArticleId(),
				article.getVersion(), languageId);
		}
	}

	/**
	 * Removes the web content of the web content article matching the group,
	 * article ID, and version, and language.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  articleId the primary key of the web content article
	 * @param  version the web content article's version
	 * @param  languageId the primary key of the language locale to remove
	 * @return the updated web content article with the locale removed
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle removeArticleLocale(
			long groupId, String articleId, double version, String languageId)
		throws PortalException {

		JournalArticle article = journalArticlePersistence.findByG_A_V(
			groupId, articleId, version);

		_journalArticleModelResourcePermission.check(
			getPermissionChecker(), article, ActionKeys.UPDATE);

		return journalArticleLocalService.removeArticleLocale(
			groupId, articleId, version, languageId);
	}

	/**
	 * Restores the web content article associated with the resource primary key
	 * from the Recycle Bin.
	 *
	 * @param  resourcePrimKey the primary key of the resource instance
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void restoreArticleFromTrash(long resourcePrimKey)
		throws PortalException {

		JournalArticle article = getLatestArticle(resourcePrimKey);

		_journalArticleModelResourcePermission.check(
			getPermissionChecker(), article, ActionKeys.DELETE);

		journalArticleLocalService.restoreArticleFromTrash(
			getUserId(), article);
	}

	/**
	 * Restores the web content article from the Recycle Bin.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  articleId the primary key of the web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void restoreArticleFromTrash(long groupId, String articleId)
		throws PortalException {

		JournalArticle article = getLatestArticle(
			groupId, articleId, WorkflowConstants.STATUS_IN_TRASH);

		restoreArticleFromTrash(article.getResourcePrimKey());
	}

	@Override
	public JournalArticle revertArticle(
			long groupId, String articleId, double version)
		throws PortalException {

		JournalArticle article = journalArticlePersistence.findByG_A_V(
			groupId, articleId, version);

		_journalArticleModelResourcePermission.check(
			getPermissionChecker(), article, ActionKeys.UPDATE);

		return journalArticleLocalService.revertArticle(
			getUserId(), groupId, articleId, version);
	}

	@Override
	public void subscribe(long groupId, long articleId) throws PortalException {
		JournalArticle article = getLatestArticle(articleId);

		_journalArticleModelResourcePermission.check(
			getPermissionChecker(), article, ActionKeys.SUBSCRIBE);

		journalArticleLocalService.subscribe(getUserId(), groupId, articleId);
	}

	/**
	 * Subscribes the user to changes in elements that belong to the web content
	 * article's DDM structure.
	 *
	 * @param  groupId the primary key of the folder's group
	 * @param  userId the primary key of the user to be subscribed
	 * @param  ddmStructureId the primary key of the structure to subscribe to
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void subscribeStructure(
			long groupId, long userId, long ddmStructureId)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), groupId, ActionKeys.SUBSCRIBE);

		journalArticleLocalService.subscribeStructure(
			groupId, userId, ddmStructureId);
	}

	@Override
	public void unsubscribe(long groupId, long articleId)
		throws PortalException {

		JournalArticle article = getLatestArticle(articleId);

		_journalArticleModelResourcePermission.check(
			getPermissionChecker(), article, ActionKeys.SUBSCRIBE);

		journalArticleLocalService.unsubscribe(getUserId(), groupId, articleId);
	}

	/**
	 * Unsubscribes the user from changes in elements that belong to the web
	 * content article's DDM structure.
	 *
	 * @param  groupId the primary key of the folder's group
	 * @param  userId the primary key of the user to be subscribed
	 * @param  ddmStructureId the primary key of the structure to subscribe to
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void unsubscribeStructure(
			long groupId, long userId, long ddmStructureId)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), groupId, ActionKeys.SUBSCRIBE);

		journalArticleLocalService.unsubscribeStructure(
			groupId, userId, ddmStructureId);
	}

	/**
	 * Updates the web content article matching the version, replacing its
	 * folder, title, description, content, and layout UUID.
	 *
	 * @param  userId the primary key of the user updating the web content
	 *         article
	 * @param  groupId the primary key of the web content article's group
	 * @param  folderId the primary key of the web content article folder
	 * @param  articleId the primary key of the web content article
	 * @param  version the web content article's version
	 * @param  titleMap the web content article's locales and localized titles
	 * @param  descriptionMap the web content article's locales and localized
	 *         descriptions
	 * @param  content the HTML content wrapped in XML. For more information,
	 *         see the content example in the {@link #updateArticle(long, long,
	 *         String, double, String, ServiceContext)} description.
	 * @param  layoutUuid the unique string identifying the web content
	 *         article's display page
	 * @param  serviceContext the service context to be applied. Can set the
	 *         modification date, expando bridge attributes, asset category IDs,
	 *         asset tag names, asset link entry IDs, asset priority, workflow
	 *         actions, URL title, and can set whether to add the default
	 *         command update for the web content article. With respect to
	 *         social activities, by setting the service context's command to
	 *         {@link com.liferay.portal.kernel.util.Constants#UPDATE}, the
	 *         invocation is considered a web content update activity; otherwise
	 *         it is considered a web content add activity.
	 * @return the updated web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle updateArticle(
			long userId, long groupId, long folderId, String articleId,
			double version, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, String content,
			String layoutUuid, ServiceContext serviceContext)
		throws PortalException {

		JournalArticle article = journalArticlePersistence.findByG_A_V(
			groupId, articleId, version);

		_journalArticleModelResourcePermission.check(
			getPermissionChecker(), article, ActionKeys.UPDATE);

		return journalArticleLocalService.updateArticle(
			userId, groupId, folderId, articleId, version, titleMap,
			descriptionMap, content, layoutUuid, serviceContext);
	}

	/**
	 * Updates the web content article with additional parameters. All
	 * scheduling parameters (display date, expiration date, and review date)
	 * use the current user's timezone.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  folderId the primary key of the web content article folder
	 * @param  articleId the primary key of the web content article
	 * @param  version the web content article's version
	 * @param  titleMap the web content article's locales and localized titles
	 * @param  descriptionMap the web content article's locales and localized
	 *         descriptions
	 * @param  friendlyURLMap the web content article's locales and localized
	 *         friendly URLs
	 * @param  content the HTML content wrapped in XML. For more information,
	 *         see the content example in the {@link #updateArticle(long, long,
	 *         String, double, String, ServiceContext)} description.
	 * @param  ddmTemplateKey the primary key of the web content article's DDM
	 *         template
	 * @param  layoutUuid the unique string identifying the web content
	 *         article's display page
	 * @param  displayDateMonth the month the web content article is set to
	 *         display
	 * @param  displayDateDay the calendar day the web content article is set to
	 *         display
	 * @param  displayDateYear the year the web content article is set to
	 *         display
	 * @param  displayDateHour the hour the web content article is set to
	 *         display
	 * @param  displayDateMinute the minute the web content article is set to
	 *         display
	 * @param  expirationDateMonth the month the web content article is set to
	 *         expire
	 * @param  expirationDateDay the calendar day the web content article is set
	 *         to expire
	 * @param  expirationDateYear the year the web content article is set to
	 *         expire
	 * @param  expirationDateHour the hour the web content article is set to
	 *         expire
	 * @param  expirationDateMinute the minute the web content article is set to
	 *         expire
	 * @param  neverExpire whether the web content article is not set to auto
	 *         expire
	 * @param  reviewDateMonth the month the web content article is set for
	 *         review
	 * @param  reviewDateDay the calendar day the web content article is set for
	 *         review
	 * @param  reviewDateYear the year the web content article is set for review
	 * @param  reviewDateHour the hour the web content article is set for review
	 * @param  reviewDateMinute the minute the web content article is set for
	 *         review
	 * @param  neverReview whether the web content article is not set for review
	 * @param  indexable whether the web content is searchable
	 * @param  smallImage whether to update web content article's a small image.
	 *         A file must be passed in as <code>smallImageFile</code> value,
	 *         otherwise the current small image is deleted.
	 * @param  smallImageSource the web content article's small image source
	 *         (optionally <code>null</code>)
	 * @param  smallImageURL the web content article's small image URL
	 *         (optionally <code>null</code>)
	 * @param  smallFile the web content article's new small image file
	 *         (optionally <code>null</code>). Must pass in
	 *         <code>smallImage</code> value of <code>true</code> to replace the
	 *         article's small image file.
	 * @param  images the web content's images (optionally <code>null</code>)
	 * @param  articleURL the web content article's accessible URL (optionally
	 *         <code>null</code>)
	 * @param  serviceContext the service context to be applied. Can set the
	 *         modification date, expando bridge attributes, asset category IDs,
	 *         asset tag names, asset link entry IDs, asset priority, workflow
	 *         actions, URL title, and can set whether to add the default
	 *         command update for the web content article. With respect to
	 *         social activities, by setting the service context's command to
	 *         {@link com.liferay.portal.kernel.util.Constants#UPDATE}, the
	 *         invocation is considered a web content update activity; otherwise
	 *         it is considered a web content add activity.
	 * @return the updated web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle updateArticle(
			long groupId, long folderId, String articleId, double version,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			Map<Locale, String> friendlyURLMap, String content,
			String ddmTemplateKey, String layoutUuid, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, int reviewDateMonth, int reviewDateDay,
			int reviewDateYear, int reviewDateHour, int reviewDateMinute,
			boolean neverReview, boolean indexable, boolean smallImage,
			long smallImageId, int smallImageSource, String smallImageURL,
			File smallFile, Map<String, byte[]> images, String articleURL,
			ServiceContext serviceContext)
		throws PortalException {

		JournalArticle article = journalArticlePersistence.findByG_A_V(
			groupId, articleId, version);

		_journalArticleModelResourcePermission.check(
			getPermissionChecker(), article, ActionKeys.UPDATE);

		return journalArticleLocalService.updateArticle(
			getUserId(), groupId, folderId, articleId, version, titleMap,
			descriptionMap, friendlyURLMap, content, ddmTemplateKey, layoutUuid,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire, reviewDateMonth, reviewDateDay, reviewDateYear,
			reviewDateHour, reviewDateMinute, neverReview, indexable,
			smallImage, smallImageId, smallImageSource, smallImageURL,
			smallFile, images, articleURL, serviceContext);
	}

	/**
	 * Updates the web content article matching the version, replacing its
	 * folder and content.
	 *
	 * <p>
	 * The web content articles hold HTML content wrapped in XML. The XML lets
	 * you specify the article's default locale and available locales. Here is a
	 * content example:
	 * </p>
	 *
	 * <p>
	 * <pre>
	 * <code>
	 * &lt;?xml version='1.0' encoding='UTF-8'?&gt;
	 * &lt;root default-locale="en_US" available-locales="en_US"&gt;
	 * 	&lt;static-content language-id="en_US"&gt;
	 * 		&lt;![CDATA[&lt;p&gt;&lt;b&gt;&lt;i&gt;test&lt;i&gt; content&lt;b&gt;&lt;/p&gt;]]&gt;
	 * 	&lt;/static-content&gt;
	 * &lt;/root&gt;
	 * </code>
	 * </pre></p>
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  folderId the primary key of the web content article folder
	 * @param  articleId the primary key of the web content article
	 * @param  version the web content article's version
	 * @param  content the HTML content wrapped in XML.
	 * @param  serviceContext the service context to be applied. Can set the
	 *         modification date, expando bridge attributes, asset category IDs,
	 *         asset tag names, asset link entry IDs, asset priority, workflow
	 *         actions, URL title, and can set whether to add the default
	 *         command update for the web content article. With respect to
	 *         social activities, by setting the service context's command to
	 *         {@link com.liferay.portal.kernel.util.Constants#UPDATE}, the
	 *         invocation is considered a web content update activity; otherwise
	 *         it is considered a web content add activity.
	 * @return the updated web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle updateArticle(
			long groupId, long folderId, String articleId, double version,
			String content, ServiceContext serviceContext)
		throws PortalException {

		JournalArticle article = journalArticlePersistence.findByG_A_V(
			groupId, articleId, version);

		_journalArticleModelResourcePermission.check(
			getPermissionChecker(), article, ActionKeys.UPDATE);

		return journalArticleLocalService.updateArticle(
			getUserId(), groupId, folderId, articleId, version, content,
			serviceContext);
	}

	@Override
	public JournalArticle updateArticleDefaultValues(
			long groupId, String articleId, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, String content,
			String ddmTemplateKey, String layoutUuid, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, int reviewDateMonth, int reviewDateDay,
			int reviewDateYear, int reviewDateHour, int reviewDateMinute,
			boolean neverReview, boolean indexable, boolean smallImage,
			long smallImageId, int smallImageSource, String smallImageURL,
			File smallImageFile, ServiceContext serviceContext)
		throws PortalException {

		JournalArticle journalArticle = journalArticleLocalService.getArticle(
			groupId, articleId);

		DDMStructure ddmStructure = _ddmStructureService.getStructure(
			journalArticle.getDDMStructureId());

		_ddmStructureModelResourcePermission.contains(
			getPermissionChecker(), ddmStructure, ActionKeys.UPDATE);

		return journalArticleLocalService.updateArticleDefaultValues(
			getUserId(), groupId, articleId, titleMap, descriptionMap, content,
			ddmTemplateKey, layoutUuid, displayDateMonth, displayDateDay,
			displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			reviewDateMonth, reviewDateDay, reviewDateYear, reviewDateHour,
			reviewDateMinute, neverReview, indexable, smallImage, smallImageId,
			smallImageSource, smallImageURL, smallImageFile, serviceContext);
	}

	/**
	 * Updates the translation of the web content article.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  articleId the primary key of the web content article
	 * @param  version the web content article's version
	 * @param  locale the locale of the web content article's display template
	 * @param  title the translated web content article title
	 * @param  description the translated web content article description
	 * @param  content the HTML content wrapped in XML. For more information,
	 *         see the content example in the {@link #updateArticle(long, long,
	 *         String, double, String, ServiceContext)} description.
	 * @param  images the web content's images
	 * @param  serviceContext the service context to be applied. Can set the
	 *         modification date and URL title for the web content article.
	 * @return the updated web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle updateArticleTranslation(
			long groupId, String articleId, double version, Locale locale,
			String title, String description, String content,
			Map<String, byte[]> images, ServiceContext serviceContext)
		throws PortalException {

		JournalArticle article = journalArticlePersistence.findByG_A_V(
			groupId, articleId, version);

		_journalArticleModelResourcePermission.check(
			getPermissionChecker(), article, ActionKeys.UPDATE);

		return journalArticleLocalService.updateArticleTranslation(
			groupId, articleId, version, locale, title, description, content,
			images, serviceContext);
	}

	/**
	 * Updates the workflow status of the web content article matching the
	 * group, article ID, and version.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  articleId the primary key of the web content article
	 * @param  version the web content article's version
	 * @param  status the web content article's workflow status. For more
	 *         information see {@link WorkflowConstants} for constants starting
	 *         with the "STATUS_" prefix.
	 * @param  articleURL the web content article's accessible URL
	 * @param  serviceContext the service context to be applied. Can set the
	 *         modification date, portlet preferences, and can set whether to
	 *         add the default command update for the web content article.
	 * @return the updated web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle updateStatus(
			long groupId, String articleId, double version, int status,
			String articleURL, ServiceContext serviceContext)
		throws PortalException {

		JournalArticle article = journalArticlePersistence.findByG_A_V(
			groupId, articleId, version);

		_journalArticleModelResourcePermission.check(
			getPermissionChecker(), article, ActionKeys.UPDATE);

		return journalArticleLocalService.updateStatus(
			getUserId(), article, status, articleURL, serviceContext,
			new HashMap<String, Serializable>());
	}

	@Reference(
		target = "(model.class.name=com.liferay.dynamic.data.mapping.model.DDMStructure)"
	)
	private ModelResourcePermission<DDMStructure>
		_ddmStructureModelResourcePermission;

	@Reference
	private DDMStructureService _ddmStructureService;

	@Reference(
		target = "(model.class.name=com.liferay.journal.model.JournalArticle)"
	)
	private ModelResourcePermission<JournalArticle>
		_journalArticleModelResourcePermission;

	@Reference(
		target = "(model.class.name=com.liferay.journal.model.JournalFolder)"
	)
	private ModelResourcePermission<JournalFolder>
		_journalFolderModelResourcePermission;

	@Reference
	private JournalFolderService _journalFolderService;

	@Reference(
		target = "(resource.name=" + JournalConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}