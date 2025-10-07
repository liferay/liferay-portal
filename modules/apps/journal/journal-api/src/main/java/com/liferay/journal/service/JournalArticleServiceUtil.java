/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.service;

import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;
import java.util.Map;

/**
 * Provides the remote service utility for JournalArticle. This utility wraps
 * <code>com.liferay.journal.service.impl.JournalArticleServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see JournalArticleService
 * @generated
 */
public class JournalArticleServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.journal.service.impl.JournalArticleServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds a web content article with additional parameters. All scheduling
	 * parameters (display date, expiration date, and review date) use the
	 * current user's timezone.
	 *
	 * @param externalReferenceCode the external reference code of the web
	 content article
	 * @param groupId the primary key of the web content article's group
	 * @param folderId the primary key of the web content article folder
	 * @param classNameId the primary key of the DDMStructure class if the web
	 content article is related to a DDM structure, the primary key of
	 the class name associated with the article, or
	 JournalArticleConstants.CLASS_NAME_ID_DEFAULT in the journal-api
	 module otherwise
	 * @param classPK the primary key of the DDM structure, if the primary key
	 of the DDMStructure class is given as the
	 <code>classNameId</code> parameter, the primary key of the class
	 associated with the web content article, or <code>0</code>
	 otherwise
	 * @param articleId the primary key of the web content article
	 * @param autoArticleId whether to auto generate the web content article ID
	 * @param titleMap the web content article's locales and localized titles
	 * @param descriptionMap the web content article's locales and localized
	 descriptions
	 * @param friendlyURLMap the web content article's locales and localized
	 friendly URLs
	 * @param content the HTML content wrapped in XML. For more information,
	 see the content example in the {@link #updateArticle(long, long,
	 String, double, String, ServiceContext)} description.
	 * @param ddmStructureId the primary key of the web content article's DDM
	 structure, if the article is related to a DDM structure, or
	 <code>0</code> otherwise
	 * @param ddmTemplateKey the primary key of the web content article's DDM
	 template
	 * @param layoutUuid the unique string identifying the web content
	 article's display page
	 * @param displayDateMonth the month the web content article is set to
	 display
	 * @param displayDateDay the calendar day the web content article is set to
	 display
	 * @param displayDateYear the year the web content article is set to
	 display
	 * @param displayDateHour the hour the web content article is set to
	 display
	 * @param displayDateMinute the minute the web content article is set to
	 display
	 * @param expirationDateMonth the month the web content article is set to
	 expire
	 * @param expirationDateDay the calendar day the web content article is set
	 to expire
	 * @param expirationDateYear the year the web content article is set to
	 expire
	 * @param expirationDateHour the hour the web content article is set to
	 expire
	 * @param expirationDateMinute the minute the web content article is set to
	 expire
	 * @param neverExpire whether the web content article is not set to auto
	 expire
	 * @param reviewDateMonth the month the web content article is set for
	 review
	 * @param reviewDateDay the calendar day the web content article is set for
	 review
	 * @param reviewDateYear the year the web content article is set for review
	 * @param reviewDateHour the hour the web content article is set for review
	 * @param reviewDateMinute the minute the web content article is set for
	 review
	 * @param neverReview whether the web content article is not set for review
	 * @param indexable whether the web content article is searchable
	 * @param smallImage whether the web content article has a small image
	 * @param smallImageSource the web content article's small image source
	 * @param smallImageURL the web content article's small image URL
	 * @param smallFile the web content article's small image file
	 * @param images the web content's images
	 * @param articleURL the web content article's accessible URL
	 * @param serviceContext the service context to be applied. Can set the
	 UUID, creation date, modification date, expando bridge
	 attributes, guest permissions, group permissions, asset category
	 IDs, asset tag names, asset link entry IDs, asset priority, URL
	 title, and workflow actions for the web content article. Can also
	 set whether to add the default guest and group permissions.
	 * @return the web content article
	 * @throws PortalException if a portal exception occurred
	 */
	public static JournalArticle addArticle(
			String externalReferenceCode, long groupId, long folderId,
			long classNameId, long classPK, String articleId,
			boolean autoArticleId, Map<java.util.Locale, String> titleMap,
			Map<java.util.Locale, String> descriptionMap,
			Map<java.util.Locale, String> friendlyURLMap, String content,
			long ddmStructureId, String ddmTemplateKey, String layoutUuid,
			int displayDateMonth, int displayDateDay, int displayDateYear,
			int displayDateHour, int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, int reviewDateMonth, int reviewDateDay,
			int reviewDateYear, int reviewDateHour, int reviewDateMinute,
			boolean neverReview, boolean indexable, boolean smallImage,
			long smallImageId, int smallImageSource, String smallImageURL,
			java.io.File smallFile, Map<String, byte[]> images,
			String articleURL,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addArticle(
			externalReferenceCode, groupId, folderId, classNameId, classPK,
			articleId, autoArticleId, titleMap, descriptionMap, friendlyURLMap,
			content, ddmStructureId, ddmTemplateKey, layoutUuid,
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
	 * @param externalReferenceCode the external reference code of the web
	 content article
	 * @param groupId the primary key of the web content article's group
	 * @param folderId the primary key of the web content article folder
	 * @param titleMap the web content article's locales and localized titles
	 * @param descriptionMap the web content article's locales and localized
	 descriptions
	 * @param content the HTML content wrapped in XML. For more information,
	 see the content example in the {@link #updateArticle(long, long,
	 String, double, String, ServiceContext)} description.
	 * @param ddmStructureId the primary key of the web content article's DDM
	 structure, if the article is related to a DDM structure, or
	 <code>0</code> otherwise
	 * @param ddmTemplateKey the primary key of the web content article's DDM
	 template
	 * @param serviceContext the service context to be applied. Can set the
	 UUID, creation date, modification date, expando bridge
	 attributes, guest permissions, group permissions, asset category
	 IDs, asset tag names, asset link entry IDs, asset priority, URL
	 title, and workflow actions for the web content article. Can also
	 set whether to add the default guest and group permissions.
	 * @return the web content article
	 * @throws PortalException if a portal exception occurred
	 */
	public static JournalArticle addArticle(
			String externalReferenceCode, long groupId, long folderId,
			Map<java.util.Locale, String> titleMap,
			Map<java.util.Locale, String> descriptionMap, String content,
			long ddmStructureId, String ddmTemplateKey,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addArticle(
			externalReferenceCode, groupId, folderId, titleMap, descriptionMap,
			content, ddmStructureId, ddmTemplateKey, serviceContext);
	}

	public static JournalArticle addArticleDefaultValues(
			long groupId, long classNameId, long classPK,
			Map<java.util.Locale, String> titleMap,
			Map<java.util.Locale, String> descriptionMap, String content,
			long ddmStructureId, String ddmTemplateKey, String layoutUuid,
			int displayDateMonth, int displayDateDay, int displayDateYear,
			int displayDateHour, int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, int reviewDateMonth, int reviewDateDay,
			int reviewDateYear, int reviewDateHour, int reviewDateMinute,
			boolean neverReview, boolean indexable, boolean smallImage,
			long smallImageId, int smallImageSource, String smallImageURL,
			java.io.File smallImageFile,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addArticleDefaultValues(
			groupId, classNameId, classPK, titleMap, descriptionMap, content,
			ddmStructureId, ddmTemplateKey, layoutUuid, displayDateMonth,
			displayDateDay, displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			reviewDateMonth, reviewDateDay, reviewDateYear, reviewDateHour,
			reviewDateMinute, neverReview, indexable, smallImage, smallImageId,
			smallImageSource, smallImageURL, smallImageFile, serviceContext);
	}

	/**
	 * Copies the web content article matching the group, article ID, and
	 * version. This method creates a new article, extracting all the values
	 * from the old one and updating its article ID.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param sourceArticleId the primary key of the old web content article
	 * @param targetArticleId the primary key of the new web content article
	 * @param autoArticleId whether to auto-generate the web content article ID
	 * @param version the web content article's version
	 * @return the new web content article
	 * @throws PortalException if a portal exception occurred
	 */
	public static JournalArticle copyArticle(
			long groupId, String sourceArticleId, String targetArticleId,
			boolean autoArticleId, double version)
		throws PortalException {

		return getService().copyArticle(
			groupId, sourceArticleId, targetArticleId, autoArticleId, version);
	}

	/**
	 * Deletes the web content article and its resources matching the group,
	 * article ID, and version, optionally sending email notifying denial of the
	 * web content article if it had not yet been approved.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param articleId the primary key of the web content article
	 * @param version the web content article's version
	 * @param articleURL the web content article's accessible URL
	 * @param serviceContext the service context to be applied. Can set the
	 portlet preferences that include email information to notify
	 recipients of the unapproved web content article's denial.
	 * @throws PortalException if a portal exception occurred
	 */
	public static void deleteArticle(
			long groupId, String articleId, double version, String articleURL,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		getService().deleteArticle(
			groupId, articleId, version, articleURL, serviceContext);
	}

	/**
	 * Deletes all web content articles and their resources matching the group
	 * and article ID, optionally sending email notifying denial of article if
	 * it had not yet been approved.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param articleId the primary key of the web content article
	 * @param articleURL the web content article's accessible URL
	 * @param serviceContext the service context to be applied. Can set the
	 portlet preferences that include email information to notify
	 recipients of the unapproved web content article's denial.
	 * @throws PortalException if a portal exception occurred
	 */
	public static void deleteArticle(
			long groupId, String articleId, String articleURL,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		getService().deleteArticle(
			groupId, articleId, articleURL, serviceContext);
	}

	public static void deleteArticleDefaultValues(
			long groupId, String articleId, long ddmStructureId)
		throws PortalException {

		getService().deleteArticleDefaultValues(
			groupId, articleId, ddmStructureId);
	}

	/**
	 * Expires the web content article matching the group, article ID, and
	 * version.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param articleId the primary key of the web content article
	 * @param version the web content article's version
	 * @param articleURL the web content article's accessible URL
	 * @param serviceContext the service context to be applied. Can set the
	 modification date, status date, portlet preferences, and can set
	 whether to add the default command update for the web content
	 article. With respect to social activities, by setting the
	 service context's command to {@link
	 com.liferay.portal.kernel.util.Constants#UPDATE}, the invocation
	 is considered a web content update activity; otherwise it is
	 considered a web content add activity.
	 * @return the web content article
	 * @throws PortalException if a portal exception occurred
	 */
	public static JournalArticle expireArticle(
			long groupId, String articleId, double version, String articleURL,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().expireArticle(
			groupId, articleId, version, articleURL, serviceContext);
	}

	/**
	 * Expires the web content article matching the group and article ID,
	 * expiring all of its versions if the
	 * <code>journal.article.expire.all.versions</code> portal property is
	 * <code>true</code>, otherwise expiring only its latest approved version.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param articleId the primary key of the web content article
	 * @param articleURL the web content article's accessible URL
	 * @param serviceContext the service context to be applied. Can set the
	 modification date, status date, portlet preferences, and can set
	 whether to add the default command update for the web content
	 article. With respect to social activities, by setting the
	 service context's command to {@link
	 com.liferay.portal.kernel.util.Constants#UPDATE}, the invocation
	 is considered a web content update activity; otherwise it is
	 considered a web content add activity.
	 * @throws PortalException if a portal exception occurred
	 */
	public static void expireArticle(
			long groupId, String articleId, String articleURL,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		getService().expireArticle(
			groupId, articleId, articleURL, serviceContext);
	}

	public static JournalArticle fetchArticle(long groupId, String articleId)
		throws PortalException {

		return getService().fetchArticle(groupId, articleId);
	}

	/**
	 * Returns the latest web content article matching the group and the
	 * external reference code.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param externalReferenceCode the external reference code of the web
	 content article
	 * @return the latest matching web content article, or <code>null</code> if
	 no matching web content article could be found
	 */
	public static JournalArticle fetchLatestArticleByExternalReferenceCode(
			long groupId, String externalReferenceCode)
		throws PortalException {

		return getService().fetchLatestArticleByExternalReferenceCode(
			groupId, externalReferenceCode);
	}

	/**
	 * Returns the web content article with the ID.
	 *
	 * @param id the primary key of the web content article
	 * @return the web content article with the ID
	 * @throws PortalException if a portal exception occurred
	 */
	public static JournalArticle getArticle(long id) throws PortalException {
		return getService().getArticle(id);
	}

	/**
	 * Returns the latest approved web content article, or the latest unapproved
	 * article if none are approved. Both approved and unapproved articles must
	 * match the group and article ID.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param articleId the primary key of the web content article
	 * @return the matching web content article
	 * @throws PortalException if a portal exception occurred
	 */
	public static JournalArticle getArticle(long groupId, String articleId)
		throws PortalException {

		return getService().getArticle(groupId, articleId);
	}

	/**
	 * Returns the web content article matching the group, article ID, and
	 * version.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param articleId the primary key of the web content article
	 * @param version the web content article's version
	 * @return the matching web content article
	 * @throws PortalException if a portal exception occurred
	 */
	public static JournalArticle getArticle(
			long groupId, String articleId, double version)
		throws PortalException {

		return getService().getArticle(groupId, articleId, version);
	}

	/**
	 * Returns the web content article matching the group, class name, and class
	 * PK.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param className the DDMStructure class name if the web content article
	 is related to a DDM structure, the primary key of the class name
	 associated with the article, or
	 JournalArticleConstants.CLASS_NAME_ID_DEFAULT in the journal-api
	 module otherwise
	 * @param classPK the primary key of the DDM structure, if the DDMStructure
	 class name is given as the <code>className</code> parameter, the
	 primary key of the class associated with the web content article,
	 or <code>0</code> otherwise
	 * @return the matching web content article
	 * @throws PortalException if a portal exception occurred
	 */
	public static JournalArticle getArticle(
			long groupId, String className, long classPK)
		throws PortalException {

		return getService().getArticle(groupId, className, classPK);
	}

	/**
	 * Returns the latest web content article that is approved, or the latest
	 * unapproved article if none are approved. Both approved and unapproved
	 * articles must match the group and URL title.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param urlTitle the web content article's accessible URL title
	 * @return the matching web content article
	 * @throws PortalException if a portal exception occurred
	 */
	public static JournalArticle getArticleByUrlTitle(
			long groupId, String urlTitle)
		throws PortalException {

		return getService().getArticleByUrlTitle(groupId, urlTitle);
	}

	/**
	 * Returns the web content from the web content article matching the group,
	 * article ID, and version.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param articleId the primary key of the web content article
	 * @param version the web content article's version
	 * @param languageId the primary key of the language translation to get
	 * @param portletRequestModel the portlet request model
	 * @param themeDisplay the theme display
	 * @return the matching web content
	 * @throws PortalException if a portal exception occurred
	 */
	public static String getArticleContent(
			long groupId, String articleId, double version, String languageId,
			com.liferay.portal.kernel.portlet.PortletRequestModel
				portletRequestModel,
			com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay)
		throws PortalException {

		return getService().getArticleContent(
			groupId, articleId, version, languageId, portletRequestModel,
			themeDisplay);
	}

	/**
	 * Returns the latest web content from the web content article matching the
	 * group and article ID.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param articleId the primary key of the web content article
	 * @param languageId the primary key of the language translation to get
	 * @param portletRequestModel the portlet request model
	 * @param themeDisplay the theme display
	 * @return the matching web content
	 * @throws PortalException if a portal exception occurred
	 */
	public static String getArticleContent(
			long groupId, String articleId, String languageId,
			com.liferay.portal.kernel.portlet.PortletRequestModel
				portletRequestModel,
			com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay)
		throws PortalException {

		return getService().getArticleContent(
			groupId, articleId, languageId, portletRequestModel, themeDisplay);
	}

	public static List<JournalArticle> getArticles(
		long groupId, long folderId, java.util.Locale locale) {

		return getService().getArticles(groupId, folderId, locale);
	}

	public static List<JournalArticle> getArticles(
		long groupId, long folderId, java.util.Locale locale, int start,
		int end, OrderByComparator<JournalArticle> orderByComparator) {

		return getService().getArticles(
			groupId, folderId, locale, start, end, orderByComparator);
	}

	public static List<JournalArticle> getArticlesByArticleId(
		long groupId, String articleId, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getService().getArticlesByArticleId(
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
	 * @param groupId the primary key of the web content article's group
	 * @param articleId the primary key of the web content article
	 * @param start the lower bound of the range of web content articles to
	 return
	 * @param end the upper bound of the range of web content articles to
	 return (not inclusive)
	 * @param orderByComparator the comparator to order the web content
	 articles
	 * @return the range of matching web content articles ordered by the
	 comparator
	 */
	public static List<JournalArticle> getArticlesByArticleId(
		long groupId, String articleId, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getService().getArticlesByArticleId(
			groupId, articleId, start, end, orderByComparator);
	}

	/**
	 * Returns all the web content articles matching the group and layout UUID.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param layoutUuid the unique string identifying the web content
	 article's display page
	 * @return the matching web content articles
	 */
	public static List<JournalArticle> getArticlesByLayoutUuid(
		long groupId, String layoutUuid) {

		return getService().getArticlesByLayoutUuid(groupId, layoutUuid);
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
	 * @param groupId the primary key of the web content article's group
	 * @param layoutUuid the unique string identifying the web content
	 article's display page
	 * @param start the lower bound of the range of web content articles to
	 return
	 * @param end the upper bound of the range of web content articles to
	 return (not inclusive)
	 * @return the range of matching web content articles
	 */
	public static List<JournalArticle> getArticlesByLayoutUuid(
		long groupId, String layoutUuid, int start, int end) {

		return getService().getArticlesByLayoutUuid(
			groupId, layoutUuid, start, end);
	}

	/**
	 * Returns the number of web content articles that the user has permission
	 * to view matching the group and layout UUID.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param layoutUuid the unique string identifying the web content
	 article's display page
	 * @return the matching web content articles
	 */
	public static int getArticlesByLayoutUuidCount(
		long groupId, String layoutUuid) {

		return getService().getArticlesByLayoutUuidCount(groupId, layoutUuid);
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
	 * @param groupId the primary key of the web content article's group
	 * @param ddmStructureId the primary key of the web content article's DDM
	 structure
	 * @param status the web content article's workflow status. For more
	 information see {@link WorkflowConstants} for constants starting
	 with the "STATUS_" prefix.
	 * @param start the lower bound of the range of web content articles to
	 return
	 * @param end the upper bound of the range of web content articles to
	 return (not inclusive)
	 * @param orderByComparator the comparator to order the web content
	 articles
	 * @return the range of matching web content articles ordered by the
	 comparator
	 */
	public static List<JournalArticle> getArticlesByStructureId(
		long groupId, long ddmStructureId, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getService().getArticlesByStructureId(
			groupId, ddmStructureId, status, start, end, orderByComparator);
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
	 * @param groupId the primary key of the web content article's group
	 * @param ddmStructureId the primary key of the web content article's DDM
	 structure
	 * @param start the lower bound of the range of web content articles to
	 return
	 * @param end the upper bound of the range of web content articles to
	 return (not inclusive)
	 * @param orderByComparator the comparator to order the web content
	 articles
	 * @return the range of matching web content articles ordered by the
	 comparator
	 */
	public static List<JournalArticle> getArticlesByStructureId(
		long groupId, long ddmStructureId, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getService().getArticlesByStructureId(
			groupId, ddmStructureId, start, end, orderByComparator);
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
	 * @param groupId the primary key of the web content article's group
	 * @param ddmStructureId the primary key of the web content article's DDM
	 structure
	 * @param locale web content articles locale
	 * @param status the web content article's workflow status. For more
	 information see {@link WorkflowConstants} for constants starting
	 with the "STATUS_" prefix.
	 * @param start the lower bound of the range of web content articles to
	 return
	 * @param end the upper bound of the range of web content articles to
	 return (not inclusive)
	 * @param orderByComparator the comparator to order the web content
	 articles
	 * @return the range of matching web content articles ordered by the
	 comparator
	 */
	public static List<JournalArticle> getArticlesByStructureId(
		long groupId, long ddmStructureId, java.util.Locale locale, int status,
		int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getService().getArticlesByStructureId(
			groupId, ddmStructureId, locale, status, start, end,
			orderByComparator);
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
	 * @param groupId the primary key of the web content article's group
	 * @param classNameId the primary key of the DDMStructure class if the web
	 content article is related to a DDM structure, the primary key of
	 the class name associated with the article, or
	 JournalArticleConstants.CLASS_NAME_ID_DEFAULT in the journal-api
	 module otherwise
	 * @param ddmStructureId the primary key of the web content article's DDM
	 structure
	 * @param status the web content article's workflow status. For more
	 information see {@link WorkflowConstants} for constants starting
	 with the "STATUS_" prefix.
	 * @param start the lower bound of the range of web content articles to
	 return
	 * @param end the upper bound of the range of web content articles to
	 return (not inclusive)
	 * @param orderByComparator the comparator to order the web content
	 articles
	 * @return the range of matching web content articles ordered by the
	 comparator
	 */
	public static List<JournalArticle> getArticlesByStructureId(
		long groupId, long classNameId, long ddmStructureId, int status,
		int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getService().getArticlesByStructureId(
			groupId, classNameId, ddmStructureId, status, start, end,
			orderByComparator);
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
	 * @param groupId the primary key of the web content article's group
	 * @param classNameId the primary key of the DDMStructure class if the web
	 content article is related to a DDM structure, the primary key of
	 the class name associated with the article, or
	 JournalArticleConstants.CLASS_NAME_ID_DEFAULT in the journal-api
	 module otherwise
	 * @param ddmStructureId the primary key of the web content article's DDM
	 structure
	 * @param status the web content article's workflow status. For more
	 information see {@link WorkflowConstants} for constants starting
	 with the "STATUS_" prefix.
	 * @param start the lower bound of the range of web content articles to
	 return
	 * @param end the upper bound of the range of web content articles to
	 return (not inclusive)
	 * @param orderByComparator the comparator to order the web content
	 articles
	 * @return the range of matching web content articles ordered by the
	 comparator
	 */
	public static List<JournalArticle> getArticlesByStructureId(
		long groupId, long classNameId, long ddmStructureId,
		java.util.Locale locale, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getService().getArticlesByStructureId(
			groupId, classNameId, ddmStructureId, locale, status, start, end,
			orderByComparator);
	}

	public static List<JournalArticle> getArticlesByStructureId(
		long groupId, long folderId, long classNameId, long ddmStructureId,
		int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getService().getArticlesByStructureId(
			groupId, folderId, classNameId, ddmStructureId, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns the number of web content articles matching the group and folder.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param folderId the primary key of the web content article folder
	 * @return the number of matching web content articles
	 */
	public static int getArticlesCount(long groupId, long folderId) {
		return getService().getArticlesCount(groupId, folderId);
	}

	/**
	 * Returns the number of web content articles matching the group, folder,
	 * and status.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param folderId the primary key of the web content article's folder
	 * @param status the web content article's workflow status. For more
	 information see {@link WorkflowConstants} for constants starting
	 with the "STATUS_" prefix.
	 * @return the number of matching web content articles
	 */
	public static int getArticlesCount(
		long groupId, long folderId, int status) {

		return getService().getArticlesCount(groupId, folderId, status);
	}

	/**
	 * Returns the number of web content articles matching the group and article
	 * ID.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param articleId the primary key of the web content article
	 * @return the number of matching web content articles
	 */
	public static int getArticlesCountByArticleId(
		long groupId, String articleId) {

		return getService().getArticlesCountByArticleId(groupId, articleId);
	}

	public static int getArticlesCountByArticleId(
		long groupId, String articleId, int status) {

		return getService().getArticlesCountByArticleId(
			groupId, articleId, status);
	}

	/**
	 * Returns the number of web content articles matching the group, default
	 * class name ID, and DDM structure key.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param ddmStructureId the primary key of the web content article's DDM
	 structure
	 * @return the number of matching web content articles
	 */
	public static int getArticlesCountByStructureId(
		long groupId, long ddmStructureId) {

		return getService().getArticlesCountByStructureId(
			groupId, ddmStructureId);
	}

	/**
	 * Returns the number of web content articles matching the group, default
	 * class name ID, and DDM structure key.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param ddmStructureId the primary key of the web content article's DDM
	 structure
	 * @param status the web content article's workflow status. For more
	 information see {@link WorkflowConstants} for constants starting
	 with the "STATUS_" prefix.
	 * @return the number of matching web content articles
	 */
	public static int getArticlesCountByStructureId(
		long groupId, long ddmStructureId, int status) {

		return getService().getArticlesCountByStructureId(
			groupId, ddmStructureId, status);
	}

	/**
	 * Returns the number of web content articles matching the group, class name
	 * ID, DDM structure key, and workflow status.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param classNameId the primary key of the DDMStructure class if the web
	 content article is related to a DDM structure, the primary key of
	 the class name associated with the article, or
	 JournalArticleConstants.CLASS_NAME_ID_DEFAULT in the journal-api
	 module otherwise
	 * @param ddmStructureId the primary key of the web content article's DDM
	 structure
	 * @param status the web content article's workflow status. For more
	 information see {@link WorkflowConstants} for constants starting
	 with the "STATUS_" prefix.
	 * @return the number of matching web content articles
	 */
	public static int getArticlesCountByStructureId(
		long groupId, long classNameId, long ddmStructureId, int status) {

		return getService().getArticlesCountByStructureId(
			groupId, classNameId, ddmStructureId, status);
	}

	public static int getArticlesCountByStructureId(
		long groupId, long folderId, long classNameId, long ddmStructureId,
		int status) {

		return getService().getArticlesCountByStructureId(
			groupId, folderId, classNameId, ddmStructureId, status);
	}

	/**
	 * Returns the web content article matching the URL title that is currently
	 * displayed or next to be displayed if no article is currently displayed.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param urlTitle the web content article's accessible URL title
	 * @return the web content article matching the URL title that is currently
	 displayed, or next one to be displayed if no version of the
	 article is currently displayed
	 * @throws PortalException if a portal exception occurred
	 */
	public static JournalArticle getDisplayArticleByUrlTitle(
			long groupId, String urlTitle)
		throws PortalException {

		return getService().getDisplayArticleByUrlTitle(groupId, urlTitle);
	}

	/**
	 * Returns the number of folders containing web content articles belonging
	 * to the group.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param folderIds the primary keys of the web content article folders
	 (optionally {@link Collections#EMPTY_LIST})
	 * @return the number of matching folders containing web content articles
	 */
	public static int getFoldersAndArticlesCount(
		long groupId, List<Long> folderIds) {

		return getService().getFoldersAndArticlesCount(groupId, folderIds);
	}

	/**
	 * Returns an ordered range of all the web content articles matching the
	 * group, user, the root folder or any of its subfolders.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param userId the primary key of the user (optionally <code>0</code>)
	 * @param rootFolderId the primary key of the root folder to begin the
	 search
	 * @param status the web content article's workflow status. For more
	 information see {@link WorkflowConstants} for constants starting
	 with the "STATUS_" prefix.
	 * @param includeOwner whether to include the user's web content
	 * @param start the lower bound of the range of web content articles to
	 return
	 * @param end the upper bound of the range of web content articles to
	 return (not inclusive)
	 * @param orderByComparator the comparator to order the web content
	 articles
	 * @return the range of matching web content articles ordered by the
	 comparator
	 * @throws PortalException if a portal exception occurred
	 */
	public static List<JournalArticle> getGroupArticles(
			long groupId, long userId, long rootFolderId, int status,
			boolean includeOwner, int start, int end,
			OrderByComparator<JournalArticle> orderByComparator)
		throws PortalException {

		return getService().getGroupArticles(
			groupId, userId, rootFolderId, status, includeOwner, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the web content articles matching the
	 * group, user, the root folder or any of its subfolders.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param userId the primary key of the user (optionally <code>0</code>)
	 * @param rootFolderId the primary key of the root folder to begin the
	 search
	 * @param status the web content article's workflow status. For more
	 information see {@link WorkflowConstants} for constants starting
	 with the "STATUS_" prefix.
	 * @param includeOwner whether to include the user's web content
	 * @param locale web content articles locale
	 * @param start the lower bound of the range of web content articles to
	 return
	 * @param end the upper bound of the range of web content articles to
	 return (not inclusive)
	 * @param orderByComparator the comparator to order the web content
	 articles
	 * @return the range of matching web content articles ordered by the
	 comparator
	 * @throws PortalException if a portal exception occurred
	 */
	public static List<JournalArticle> getGroupArticles(
			long groupId, long userId, long rootFolderId, int status,
			boolean includeOwner, java.util.Locale locale, int start, int end,
			OrderByComparator<JournalArticle> orderByComparator)
		throws PortalException {

		return getService().getGroupArticles(
			groupId, userId, rootFolderId, status, includeOwner, locale, start,
			end, orderByComparator);
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
	 * @param groupId the primary key of the web content article's group
	 * @param userId the primary key of the user (optionally <code>0</code>)
	 * @param rootFolderId the primary key of the root folder to begin the
	 search
	 * @param status the web content article's workflow status. For more
	 information see {@link WorkflowConstants} for constants starting
	 with the "STATUS_" prefix.
	 * @param start the lower bound of the range of web content articles to
	 return
	 * @param end the upper bound of the range of web content articles to
	 return (not inclusive)
	 * @param orderByComparator the comparator to order the web content
	 articles
	 * @return the range of matching web content articles ordered by the
	 comparator
	 * @throws PortalException if a portal exception occurred
	 */
	public static List<JournalArticle> getGroupArticles(
			long groupId, long userId, long rootFolderId, int status, int start,
			int end, OrderByComparator<JournalArticle> orderByComparator)
		throws PortalException {

		return getService().getGroupArticles(
			groupId, userId, rootFolderId, status, start, end,
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
	 * @param groupId the primary key of the web content article's group
	 * @param userId the primary key of the user (optionally <code>0</code>)
	 * @param rootFolderId the primary key of the root folder to begin the
	 search
	 * @param start the lower bound of the range of web content articles to
	 return
	 * @param end the upper bound of the range of web content articles to
	 return (not inclusive)
	 * @param orderByComparator the comparator to order the web content
	 articles
	 * @return the range of matching web content articles ordered by the
	 comparator
	 * @throws PortalException if a portal exception occurred
	 */
	public static List<JournalArticle> getGroupArticles(
			long groupId, long userId, long rootFolderId, int start, int end,
			OrderByComparator<JournalArticle> orderByComparator)
		throws PortalException {

		return getService().getGroupArticles(
			groupId, userId, rootFolderId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of web content articles matching the group, user, and
	 * the root folder or any of its subfolders.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param userId the primary key of the user (optionally <code>0</code>)
	 * @param rootFolderId the primary key of the root folder to begin the
	 search
	 * @return the number of matching web content articles
	 * @throws PortalException if a portal exception occurred
	 */
	public static int getGroupArticlesCount(
			long groupId, long userId, long rootFolderId)
		throws PortalException {

		return getService().getGroupArticlesCount(
			groupId, userId, rootFolderId);
	}

	/**
	 * Returns the number of web content articles matching the group, user, and
	 * the root folder or any of its subfolders.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param userId the primary key of the user (optionally <code>0</code>)
	 * @param rootFolderId the primary key of the root folder to begin the
	 search
	 * @param status the web content article's workflow status. For more
	 information see {@link WorkflowConstants} for constants starting
	 with the "STATUS_" prefix.
	 * @return the number of matching web content articles
	 * @throws PortalException if a portal exception occurred
	 */
	public static int getGroupArticlesCount(
			long groupId, long userId, long rootFolderId, int status)
		throws PortalException {

		return getService().getGroupArticlesCount(
			groupId, userId, rootFolderId, status);
	}

	/**
	 * Returns the number of web content articles matching the group, user, the
	 * root folder or any of its subfolders.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param userId the primary key of the user (optionally <code>0</code>)
	 * @param rootFolderId the primary key of the root folder to begin the
	 search
	 * @param status the web content article's workflow status. For more
	 information see {@link WorkflowConstants} for constants starting
	 with the "STATUS_" prefix.
	 * @param includeOwner whether to include the user's web content
	 * @return the range of matching web content articles ordered by the
	 comparator
	 * @throws PortalException if a portal exception occurred
	 */
	public static int getGroupArticlesCount(
			long groupId, long userId, long rootFolderId, int status,
			boolean includeOwner)
		throws PortalException {

		return getService().getGroupArticlesCount(
			groupId, userId, rootFolderId, status, includeOwner);
	}

	/**
	 * Returns the latest web content article matching the resource primary key,
	 * preferring articles with approved workflow status.
	 *
	 * @param resourcePrimKey the primary key of the resource instance
	 * @return the latest web content article matching the resource primary key,
	 preferring articles with approved workflow status
	 * @throws PortalException if a portal exception occurred
	 */
	public static JournalArticle getLatestArticle(long resourcePrimKey)
		throws PortalException {

		return getService().getLatestArticle(resourcePrimKey);
	}

	/**
	 * Returns the latest web content article matching the group, article ID,
	 * and workflow status.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param articleId the primary key of the web content article
	 * @param status the web content article's workflow status. For more
	 information see {@link WorkflowConstants} for constants starting
	 with the "STATUS_" prefix.
	 * @return the latest matching web content article
	 * @throws PortalException if a portal exception occurred
	 */
	public static JournalArticle getLatestArticle(
			long groupId, String articleId, int status)
		throws PortalException {

		return getService().getLatestArticle(groupId, articleId, status);
	}

	/**
	 * Returns the latest web content article matching the group, class name ID,
	 * and class PK.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param className the DDMStructure class name if the web content article
	 is related to a DDM structure, the class name associated with the
	 article, or JournalArticleConstants.CLASS_NAME_ID_DEFAULT in the
	 journal-api module otherwise
	 * @param classPK the primary key of the DDM structure, if the DDMStructure
	 class name is given as the <code>className</code> parameter, the
	 primary key of the class associated with the web content article,
	 or <code>0</code> otherwise
	 * @return the latest matching web content article
	 * @throws PortalException if a portal exception occurred
	 */
	public static JournalArticle getLatestArticle(
			long groupId, String className, long classPK)
		throws PortalException {

		return getService().getLatestArticle(groupId, className, classPK);
	}

	/**
	 * Returns the latest web content article matching the group and the
	 * external reference code.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param externalReferenceCode the external reference code of the web
	 content article
	 * @return the latest matching web content article
	 * @throws PortalException if a portal exception occurred
	 */
	public static JournalArticle getLatestArticleByExternalReferenceCode(
			long groupId, String externalReferenceCode)
		throws PortalException {

		return getService().getLatestArticleByExternalReferenceCode(
			groupId, externalReferenceCode);
	}

	public static List<JournalArticle> getLatestArticles(
		long groupId, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getService().getLatestArticles(
			groupId, status, start, end, orderByComparator);
	}

	public static int getLatestArticlesCount(long groupId, int status) {
		return getService().getLatestArticlesCount(groupId, status);
	}

	/**
	 * Returns all the web content articles that the user has permission to view
	 * matching the group.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @return The matching web content articles
	 */
	public static List<JournalArticle> getLayoutArticles(long groupId) {
		return getService().getLayoutArticles(groupId);
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
	 * @param groupId the primary key of the web content article's group
	 * @param start the lower bound of the range of web content articles to
	 return
	 * @param end the upper bound of the range of web content articles to
	 return (not inclusive)
	 * @return the range of matching web content articles
	 */
	public static List<JournalArticle> getLayoutArticles(
		long groupId, int start, int end) {

		return getService().getLayoutArticles(groupId, start, end);
	}

	/**
	 * Returns the number of web content articles that the user has permission
	 * to view matching the group.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @return the number of matching web content articles
	 */
	public static int getLayoutArticlesCount(long groupId) {
		return getService().getLayoutArticlesCount(groupId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	/**
	 * Moves all versions of the web content article matching the group and
	 * article ID to the folder.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param articleId the primary key of the web content article
	 * @param newFolderId the primary key of the web content article's new
	 folder
	 * @param serviceContext the service context to be applied. Can set the
	 user ID, language ID, portlet preferences, portlet request,
	 portlet response, theme display, and can set whether to add the
	 default command update for the web content article. With respect
	 to social activities, by setting the service context's command to
	 {@link com.liferay.portal.kernel.util.Constants#UPDATE}, the
	 invocation is considered a web content update activity; otherwise
	 it is considered a web content add activity.
	 * @throws PortalException if a portal exception occurred
	 */
	public static void moveArticle(
			long groupId, String articleId, long newFolderId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		getService().moveArticle(
			groupId, articleId, newFolderId, serviceContext);
	}

	/**
	 * Moves the web content article from the Recycle Bin to the folder.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param resourcePrimKey the primary key of the resource instance
	 * @param newFolderId the primary key of the web content article's new
	 folder
	 * @param serviceContext the service context to be applied. Can set the
	 modification date, portlet preferences, and can set whether to
	 add the default command update for the web content article. With
	 respect to social activities, by setting the service context's
	 command to {@link
	 com.liferay.portal.kernel.util.Constants#UPDATE}, the invocation
	 is considered a web content update activity; otherwise it is
	 considered a web content add activity.
	 * @return the updated web content article, which was moved from the Recycle
	 Bin to the folder
	 * @throws PortalException if a portal exception occurred
	 */
	public static JournalArticle moveArticleFromTrash(
			long groupId, long resourcePrimKey, long newFolderId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().moveArticleFromTrash(
			groupId, resourcePrimKey, newFolderId, serviceContext);
	}

	/**
	 * Moves the web content article from the Recycle Bin to the folder.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param articleId the primary key of the web content article
	 * @param newFolderId the primary key of the web content article's new
	 folder
	 * @param serviceContext the service context to be applied. Can set the
	 modification date, portlet preferences, and can set whether to
	 add the default command update for the web content article. With
	 respect to social activities, by setting the service context's
	 command to {@link
	 com.liferay.portal.kernel.util.Constants#UPDATE}, the invocation
	 is considered a web content update activity; otherwise it is
	 considered a web content add activity.
	 * @return the updated web content article, which was moved from the Recycle
	 Bin to the folder
	 * @throws PortalException if a portal exception occurred
	 */
	public static JournalArticle moveArticleFromTrash(
			long groupId, String articleId, long newFolderId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().moveArticleFromTrash(
			groupId, articleId, newFolderId, serviceContext);
	}

	/**
	 * Moves the latest version of the web content article matching the group
	 * and article ID to the recycle bin.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param articleId the primary key of the web content article
	 * @return the moved web content article or <code>null</code> if no matching
	 article was found
	 * @throws PortalException if a portal exception occurred
	 */
	public static JournalArticle moveArticleToTrash(
			long groupId, String articleId)
		throws PortalException {

		return getService().moveArticleToTrash(groupId, articleId);
	}

	/**
	 * Removes the web content of all the company's web content articles
	 * matching the language.
	 *
	 * @param companyId the primary key of the web content article's company
	 * @param languageId the primary key of the language locale to remove
	 * @throws PortalException if a portal exception occurred
	 */
	public static void removeArticleLocale(long companyId, String languageId)
		throws PortalException {

		getService().removeArticleLocale(companyId, languageId);
	}

	/**
	 * Removes the web content of the web content article matching the group,
	 * article ID, and version, and language.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param articleId the primary key of the web content article
	 * @param version the web content article's version
	 * @param languageId the primary key of the language locale to remove
	 * @return the updated web content article with the locale removed
	 * @throws PortalException if a portal exception occurred
	 */
	public static JournalArticle removeArticleLocale(
			long groupId, String articleId, double version, String languageId)
		throws PortalException {

		return getService().removeArticleLocale(
			groupId, articleId, version, languageId);
	}

	/**
	 * Restores the web content article associated with the resource primary key
	 * from the Recycle Bin.
	 *
	 * @param resourcePrimKey the primary key of the resource instance
	 * @throws PortalException if a portal exception occurred
	 */
	public static void restoreArticleFromTrash(long resourcePrimKey)
		throws PortalException {

		getService().restoreArticleFromTrash(resourcePrimKey);
	}

	/**
	 * Restores the web content article from the Recycle Bin.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param articleId the primary key of the web content article
	 * @throws PortalException if a portal exception occurred
	 */
	public static void restoreArticleFromTrash(long groupId, String articleId)
		throws PortalException {

		getService().restoreArticleFromTrash(groupId, articleId);
	}

	public static JournalArticle revertArticle(
			long groupId, String articleId, double version)
		throws PortalException {

		return getService().revertArticle(groupId, articleId, version);
	}

	public static void subscribe(long groupId, long articleId)
		throws PortalException {

		getService().subscribe(groupId, articleId);
	}

	/**
	 * Subscribes the user to changes in elements that belong to the web content
	 * article's DDM structure.
	 *
	 * @param groupId the primary key of the folder's group
	 * @param userId the primary key of the user to be subscribed
	 * @param ddmStructureId the primary key of the structure to subscribe to
	 * @throws PortalException if a portal exception occurred
	 */
	public static void subscribeStructure(
			long groupId, long userId, long ddmStructureId)
		throws PortalException {

		getService().subscribeStructure(groupId, userId, ddmStructureId);
	}

	public static void unsubscribe(long groupId, long articleId)
		throws PortalException {

		getService().unsubscribe(groupId, articleId);
	}

	/**
	 * Unsubscribes the user from changes in elements that belong to the web
	 * content article's DDM structure.
	 *
	 * @param groupId the primary key of the folder's group
	 * @param userId the primary key of the user to be subscribed
	 * @param ddmStructureId the primary key of the structure to subscribe to
	 * @throws PortalException if a portal exception occurred
	 */
	public static void unsubscribeStructure(
			long groupId, long userId, long ddmStructureId)
		throws PortalException {

		getService().unsubscribeStructure(groupId, userId, ddmStructureId);
	}

	/**
	 * Updates the web content article matching the version, replacing its
	 * folder, title, description, content, and layout UUID.
	 *
	 * @param userId the primary key of the user updating the web content
	 article
	 * @param groupId the primary key of the web content article's group
	 * @param folderId the primary key of the web content article folder
	 * @param articleId the primary key of the web content article
	 * @param version the web content article's version
	 * @param titleMap the web content article's locales and localized titles
	 * @param descriptionMap the web content article's locales and localized
	 descriptions
	 * @param content the HTML content wrapped in XML. For more information,
	 see the content example in the {@link #updateArticle(long, long,
	 String, double, String, ServiceContext)} description.
	 * @param layoutUuid the unique string identifying the web content
	 article's display page
	 * @param serviceContext the service context to be applied. Can set the
	 modification date, expando bridge attributes, asset category IDs,
	 asset tag names, asset link entry IDs, asset priority, workflow
	 actions, URL title, and can set whether to add the default
	 command update for the web content article. With respect to
	 social activities, by setting the service context's command to
	 {@link com.liferay.portal.kernel.util.Constants#UPDATE}, the
	 invocation is considered a web content update activity; otherwise
	 it is considered a web content add activity.
	 * @return the updated web content article
	 * @throws PortalException if a portal exception occurred
	 */
	public static JournalArticle updateArticle(
			long userId, long groupId, long folderId, String articleId,
			double version, Map<java.util.Locale, String> titleMap,
			Map<java.util.Locale, String> descriptionMap, String content,
			String layoutUuid,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateArticle(
			userId, groupId, folderId, articleId, version, titleMap,
			descriptionMap, content, layoutUuid, serviceContext);
	}

	/**
	 * Updates the web content article with additional parameters. All
	 * scheduling parameters (display date, expiration date, and review date)
	 * use the current user's timezone.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param folderId the primary key of the web content article folder
	 * @param articleId the primary key of the web content article
	 * @param version the web content article's version
	 * @param titleMap the web content article's locales and localized titles
	 * @param descriptionMap the web content article's locales and localized
	 descriptions
	 * @param friendlyURLMap the web content article's locales and localized
	 friendly URLs
	 * @param content the HTML content wrapped in XML. For more information,
	 see the content example in the {@link #updateArticle(long, long,
	 String, double, String, ServiceContext)} description.
	 * @param ddmTemplateKey the primary key of the web content article's DDM
	 template
	 * @param layoutUuid the unique string identifying the web content
	 article's display page
	 * @param displayDateMonth the month the web content article is set to
	 display
	 * @param displayDateDay the calendar day the web content article is set to
	 display
	 * @param displayDateYear the year the web content article is set to
	 display
	 * @param displayDateHour the hour the web content article is set to
	 display
	 * @param displayDateMinute the minute the web content article is set to
	 display
	 * @param expirationDateMonth the month the web content article is set to
	 expire
	 * @param expirationDateDay the calendar day the web content article is set
	 to expire
	 * @param expirationDateYear the year the web content article is set to
	 expire
	 * @param expirationDateHour the hour the web content article is set to
	 expire
	 * @param expirationDateMinute the minute the web content article is set to
	 expire
	 * @param neverExpire whether the web content article is not set to auto
	 expire
	 * @param reviewDateMonth the month the web content article is set for
	 review
	 * @param reviewDateDay the calendar day the web content article is set for
	 review
	 * @param reviewDateYear the year the web content article is set for review
	 * @param reviewDateHour the hour the web content article is set for review
	 * @param reviewDateMinute the minute the web content article is set for
	 review
	 * @param neverReview whether the web content article is not set for review
	 * @param indexable whether the web content is searchable
	 * @param smallImage whether to update web content article's a small image.
	 A file must be passed in as <code>smallImageFile</code> value,
	 otherwise the current small image is deleted.
	 * @param smallImageSource the web content article's small image source
	 (optionally <code>null</code>)
	 * @param smallImageURL the web content article's small image URL
	 (optionally <code>null</code>)
	 * @param smallFile the web content article's new small image file
	 (optionally <code>null</code>). Must pass in
	 <code>smallImage</code> value of <code>true</code> to replace the
	 article's small image file.
	 * @param images the web content's images (optionally <code>null</code>)
	 * @param articleURL the web content article's accessible URL (optionally
	 <code>null</code>)
	 * @param serviceContext the service context to be applied. Can set the
	 modification date, expando bridge attributes, asset category IDs,
	 asset tag names, asset link entry IDs, asset priority, workflow
	 actions, URL title, and can set whether to add the default
	 command update for the web content article. With respect to
	 social activities, by setting the service context's command to
	 {@link com.liferay.portal.kernel.util.Constants#UPDATE}, the
	 invocation is considered a web content update activity; otherwise
	 it is considered a web content add activity.
	 * @return the updated web content article
	 * @throws PortalException if a portal exception occurred
	 */
	public static JournalArticle updateArticle(
			long groupId, long folderId, String articleId, double version,
			Map<java.util.Locale, String> titleMap,
			Map<java.util.Locale, String> descriptionMap,
			Map<java.util.Locale, String> friendlyURLMap, String content,
			String ddmTemplateKey, String layoutUuid, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, int reviewDateMonth, int reviewDateDay,
			int reviewDateYear, int reviewDateHour, int reviewDateMinute,
			boolean neverReview, boolean indexable, boolean smallImage,
			long smallImageId, int smallImageSource, String smallImageURL,
			java.io.File smallFile, Map<String, byte[]> images,
			String articleURL,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateArticle(
			groupId, folderId, articleId, version, titleMap, descriptionMap,
			friendlyURLMap, content, ddmTemplateKey, layoutUuid,
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
	 * @param groupId the primary key of the web content article's group
	 * @param folderId the primary key of the web content article folder
	 * @param articleId the primary key of the web content article
	 * @param version the web content article's version
	 * @param content the HTML content wrapped in XML.
	 * @param serviceContext the service context to be applied. Can set the
	 modification date, expando bridge attributes, asset category IDs,
	 asset tag names, asset link entry IDs, asset priority, workflow
	 actions, URL title, and can set whether to add the default
	 command update for the web content article. With respect to
	 social activities, by setting the service context's command to
	 {@link com.liferay.portal.kernel.util.Constants#UPDATE}, the
	 invocation is considered a web content update activity; otherwise
	 it is considered a web content add activity.
	 * @return the updated web content article
	 * @throws PortalException if a portal exception occurred
	 */
	public static JournalArticle updateArticle(
			long groupId, long folderId, String articleId, double version,
			String content,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateArticle(
			groupId, folderId, articleId, version, content, serviceContext);
	}

	public static JournalArticle updateArticleDefaultValues(
			long groupId, String articleId,
			Map<java.util.Locale, String> titleMap,
			Map<java.util.Locale, String> descriptionMap, String content,
			String ddmTemplateKey, String layoutUuid, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, int reviewDateMonth, int reviewDateDay,
			int reviewDateYear, int reviewDateHour, int reviewDateMinute,
			boolean neverReview, boolean indexable, boolean smallImage,
			long smallImageId, int smallImageSource, String smallImageURL,
			java.io.File smallImageFile,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateArticleDefaultValues(
			groupId, articleId, titleMap, descriptionMap, content,
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
	 * @param groupId the primary key of the web content article's group
	 * @param articleId the primary key of the web content article
	 * @param version the web content article's version
	 * @param locale the locale of the web content article's display template
	 * @param title the translated web content article title
	 * @param description the translated web content article description
	 * @param content the HTML content wrapped in XML. For more information,
	 see the content example in the {@link #updateArticle(long, long,
	 String, double, String, ServiceContext)} description.
	 * @param images the web content's images
	 * @param serviceContext the service context to be applied. Can set the
	 modification date and URL title for the web content article.
	 * @return the updated web content article
	 * @throws PortalException if a portal exception occurred
	 */
	public static JournalArticle updateArticleTranslation(
			long groupId, String articleId, double version,
			java.util.Locale locale, String title, String description,
			String content, Map<String, byte[]> images,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateArticleTranslation(
			groupId, articleId, version, locale, title, description, content,
			images, serviceContext);
	}

	/**
	 * Updates the workflow status of the web content article matching the
	 * group, article ID, and version.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param articleId the primary key of the web content article
	 * @param version the web content article's version
	 * @param status the web content article's workflow status. For more
	 information see {@link WorkflowConstants} for constants starting
	 with the "STATUS_" prefix.
	 * @param articleURL the web content article's accessible URL
	 * @param serviceContext the service context to be applied. Can set the
	 modification date, portlet preferences, and can set whether to
	 add the default command update for the web content article.
	 * @return the updated web content article
	 * @throws PortalException if a portal exception occurred
	 */
	public static JournalArticle updateStatus(
			long groupId, String articleId, double version, int status,
			String articleURL,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateStatus(
			groupId, articleId, version, status, articleURL, serviceContext);
	}

	public static JournalArticleService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<JournalArticleService> _serviceSnapshot =
		new Snapshot<>(
			JournalArticleServiceUtil.class, JournalArticleService.class);

}