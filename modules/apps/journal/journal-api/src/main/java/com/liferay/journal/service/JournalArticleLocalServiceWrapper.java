/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.service;

import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link JournalArticleLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see JournalArticleLocalService
 * @generated
 */
public class JournalArticleLocalServiceWrapper
	implements JournalArticleLocalService,
			   ServiceWrapper<JournalArticleLocalService> {

	public JournalArticleLocalServiceWrapper() {
		this(null);
	}

	public JournalArticleLocalServiceWrapper(
		JournalArticleLocalService journalArticleLocalService) {

		_journalArticleLocalService = journalArticleLocalService;
	}

	/**
	 * Adds a web content article with additional parameters. All scheduling
	 * parameters (display date, expiration date, and review date) use the
	 * current user's timezone.
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
	 * @param externalReferenceCode the external reference code of the web
	 content article
	 * @param userId the primary key of the web content article's creator/owner
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
	 * @param version the web content article's version
	 * @param titleMap the web content article's locales and localized titles
	 * @param descriptionMap the web content article's locales and localized
	 descriptions
	 * @param friendlyURLMap the web content article's locales and localized
	 friendly URLs
	 * @param content the HTML content wrapped in XML
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
	 * @param smallImageFile the web content article's small image file
	 * @param images the web content's images
	 * @param articleURL the web content article's accessible URL
	 * @param serviceContext the service context to be applied. Can set the
	 UUID, creation date, modification date, expando bridge
	 attributes, guest permissions, group permissions, asset category
	 IDs, asset tag names, asset link entry IDs, URL title, and
	 workflow actions for the web content article. Can also set
	 whether to add the default guest and group permissions.
	 * @return the web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle addArticle(
			String externalReferenceCode, long userId, long groupId,
			long folderId, long classNameId, long classPK, String articleId,
			boolean autoArticleId, double version,
			java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			java.util.Map<java.util.Locale, String> friendlyURLMap,
			String content, long ddmStructureId, String ddmTemplateKey,
			String layoutUuid, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute,
			int expirationDateMonth, int expirationDateDay,
			int expirationDateYear, int expirationDateHour,
			int expirationDateMinute, boolean neverExpire, int reviewDateMonth,
			int reviewDateDay, int reviewDateYear, int reviewDateHour,
			int reviewDateMinute, boolean neverReview, boolean indexable,
			boolean smallImage, long smallImageId, int smallImageSource,
			String smallImageURL, java.io.File smallImageFile,
			java.util.Map<String, byte[]> images, String articleURL,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.addArticle(
			externalReferenceCode, userId, groupId, folderId, classNameId,
			classPK, articleId, autoArticleId, version, titleMap,
			descriptionMap, friendlyURLMap, content, ddmStructureId,
			ddmTemplateKey, layoutUuid, displayDateMonth, displayDateDay,
			displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			reviewDateMonth, reviewDateDay, reviewDateYear, reviewDateHour,
			reviewDateMinute, neverReview, indexable, smallImage, smallImageId,
			smallImageSource, smallImageURL, smallImageFile, images, articleURL,
			serviceContext);
	}

	/**
	 * Adds a web content article.
	 *
	 * @param externalReferenceCode the external reference code of the web
	 content article.
	 * @param userId the primary key of the web content article's creator/owner
	 * @param groupId the primary key of the web content article's group
	 * @param folderId the primary key of the web content article folder
	 * @param titleMap the web content article's locales and localized titles
	 * @param descriptionMap the web content article's locales and localized
	 descriptions
	 * @param content the HTML content wrapped in XML. For more information,
	 see the content example in the {@link #addArticle(String, long,
	 long, long, long, long, String, boolean, double, Map, Map, Map,
	 String, long, String, String, int, int, int, int, int, int, int,
	 int, int, int, boolean, int, int, int, int, int, boolean,
	 boolean, boolean, long, int, String, File, Map, String,
	 ServiceContext)} description.
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
	@Override
	public JournalArticle addArticle(
			String externalReferenceCode, long userId, long groupId,
			long folderId, java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String content, long ddmStructureId, String ddmTemplateKey,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.addArticle(
			externalReferenceCode, userId, groupId, folderId, titleMap,
			descriptionMap, content, ddmStructureId, ddmTemplateKey,
			serviceContext);
	}

	@Override
	public JournalArticle addArticleDefaultValues(
			long userId, long groupId, long classNameId, long classPK,
			java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String content, long ddmStructureId, String ddmTemplateKey,
			String layoutUuid, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute,
			int expirationDateMonth, int expirationDateDay,
			int expirationDateYear, int expirationDateHour,
			int expirationDateMinute, boolean neverExpire, int reviewDateMonth,
			int reviewDateDay, int reviewDateYear, int reviewDateHour,
			int reviewDateMinute, boolean neverReview, boolean indexable,
			boolean smallImage, long smallImageId, int smallImageSource,
			String smallImageURL, java.io.File smallImageFile,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.addArticleDefaultValues(
			userId, groupId, classNameId, classPK, titleMap, descriptionMap,
			content, ddmStructureId, ddmTemplateKey, layoutUuid,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire, reviewDateMonth, reviewDateDay, reviewDateYear,
			reviewDateHour, reviewDateMinute, neverReview, indexable,
			smallImage, smallImageId, smallImageSource, smallImageURL,
			smallImageFile, serviceContext);
	}

	/**
	 * Adds the resources to the web content article.
	 *
	 * @param article the web content article
	 * @param addGroupPermissions whether to add group permissions
	 * @param addGuestPermissions whether to add guest permissions
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void addArticleResources(
			JournalArticle article, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_journalArticleLocalService.addArticleResources(
			article, addGroupPermissions, addGuestPermissions);
	}

	/**
	 * Adds the model resources with the permissions to the web content article.
	 *
	 * @param article the web content article to add resources to
	 * @param modelPermissions the model permissions
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void addArticleResources(
			JournalArticle article,
			com.liferay.portal.kernel.service.permission.ModelPermissions
				modelPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_journalArticleLocalService.addArticleResources(
			article, modelPermissions);
	}

	/**
	 * Adds the resources to the most recently created web content article.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param articleId the primary key of the web content article
	 * @param addGroupPermissions whether to add group permissions
	 * @param addGuestPermissions whether to add guest permissions
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void addArticleResources(
			long groupId, String articleId, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_journalArticleLocalService.addArticleResources(
			groupId, articleId, addGroupPermissions, addGuestPermissions);
	}

	/**
	 * Adds the journal article to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect JournalArticleLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param journalArticle the journal article
	 * @return the journal article that was added
	 */
	@Override
	public JournalArticle addJournalArticle(JournalArticle journalArticle) {
		return _journalArticleLocalService.addJournalArticle(journalArticle);
	}

	/**
	 * Returns the web content article with the group, article ID, and version.
	 * This method checks for the article's resource primary key and, if not
	 * found, creates a new one.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param articleId the primary key of the web content article
	 * @param version the web content article's version
	 * @return the matching web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle checkArticleResourcePrimKey(
			long groupId, String articleId, double version)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.checkArticleResourcePrimKey(
			groupId, articleId, version);
	}

	/**
	 * Checks all web content articles by handling their expirations and sending
	 * review notifications based on their current workflow.
	 */
	@Override
	public void checkArticles(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_journalArticleLocalService.checkArticles(companyId);
	}

	/**
	 * Copies the web content article matching the group, article ID, and
	 * version. This method creates a new article, extracting all the values
	 * from the old one and updating its article ID.
	 *
	 * @param userId the primary key of the web content article's creator/owner
	 * @param groupId the primary key of the web content article's group
	 * @param sourceArticleId the primary key of the old web content article
	 * @param targetArticleId the primary key of the new web content article
	 * @param autoArticleId whether to auto-generate the web content article ID
	 * @param version the web content article's version
	 * @return the new web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle copyArticle(
			long userId, long groupId, String sourceArticleId,
			String targetArticleId, boolean autoArticleId, double version)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.copyArticle(
			userId, groupId, sourceArticleId, targetArticleId, autoArticleId,
			version);
	}

	/**
	 * Creates a new journal article with the primary key. Does not add the journal article to the database.
	 *
	 * @param id the primary key for the new journal article
	 * @return the new journal article
	 */
	@Override
	public JournalArticle createJournalArticle(long id) {
		return _journalArticleLocalService.createJournalArticle(id);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the web content article and its resources.
	 *
	 * @param article the web content article
	 * @return the deleted web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle deleteArticle(JournalArticle article)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.deleteArticle(article);
	}

	/**
	 * Deletes the web content article and its resources, optionally sending
	 * email notifying denial of the article if it had not yet been approved.
	 *
	 * @param article the web content article
	 * @param articleURL the web content article's accessible URL to include in
	 email notifications (optionally <code>null</code>)
	 * @param serviceContext the service context to be applied (optionally
	 <code>null</code>). Can set the portlet preferences that include
	 email information to notify recipients of the unapproved web
	 content's denial.
	 * @return the deleted web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle deleteArticle(
			JournalArticle article, String articleURL,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.deleteArticle(
			article, articleURL, serviceContext);
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
	 * @return the deleted web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle deleteArticle(
			long groupId, String articleId, double version, String articleURL,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.deleteArticle(
			groupId, articleId, version, articleURL, serviceContext);
	}

	/**
	 * Deletes all web content articles and their resources matching the group
	 * and article ID, optionally sending email notifying denial of article if
	 * it had not yet been approved.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param articleId the primary key of the web content article
	 * @param serviceContext the service context to be applied. Can set the
	 portlet preferences that include email information to notify
	 recipients of the unapproved web content article's denial.
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void deleteArticle(
			long groupId, String articleId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_journalArticleLocalService.deleteArticle(
			groupId, articleId, serviceContext);
	}

	@Override
	public void deleteArticleDefaultValues(
			long groupId, String articleId, long ddmStructureId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_journalArticleLocalService.deleteArticleDefaultValues(
			groupId, articleId, ddmStructureId);
	}

	/**
	 * Deletes all the group's web content articles and resources.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void deleteArticles(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_journalArticleLocalService.deleteArticles(groupId);
	}

	/**
	 * Deletes all the group's web content articles and resources in the folder,
	 * including recycled articles.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param folderId the primary key of the web content article folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void deleteArticles(long groupId, long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_journalArticleLocalService.deleteArticles(groupId, folderId);
	}

	/**
	 * Deletes all the group's web content articles and resources in the folder,
	 * optionally including recycled articles.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param folderId the primary key of the web content article folder
	 * @param includeTrashedEntries whether to include recycled web content
	 articles
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void deleteArticles(
			long groupId, long folderId, boolean includeTrashedEntries)
		throws com.liferay.portal.kernel.exception.PortalException {

		_journalArticleLocalService.deleteArticles(
			groupId, folderId, includeTrashedEntries);
	}

	/**
	 * Deletes all the group's web content articles and resources matching the
	 * class name and class primary key.
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
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void deleteArticles(long groupId, String className, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		_journalArticleLocalService.deleteArticles(groupId, className, classPK);
	}

	/**
	 * Deletes the journal article from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect JournalArticleLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param journalArticle the journal article
	 * @return the journal article that was removed
	 */
	@Override
	public JournalArticle deleteJournalArticle(JournalArticle journalArticle) {
		return _journalArticleLocalService.deleteJournalArticle(journalArticle);
	}

	/**
	 * Deletes the journal article with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect JournalArticleLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param id the primary key of the journal article
	 * @return the journal article that was removed
	 * @throws PortalException if a journal article with the primary key could not be found
	 */
	@Override
	public JournalArticle deleteJournalArticle(long id)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.deleteJournalArticle(id);
	}

	/**
	 * Deletes the layout's association with the web content articles for the
	 * group.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param layoutUuid the unique string identifying the web content article's
	 display page
	 */
	@Override
	public void deleteLayoutArticleReferences(long groupId, String layoutUuid) {
		_journalArticleLocalService.deleteLayoutArticleReferences(
			groupId, layoutUuid);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _journalArticleLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _journalArticleLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _journalArticleLocalService.dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _journalArticleLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _journalArticleLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _journalArticleLocalService.dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _journalArticleLocalService.dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return _journalArticleLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	/**
	 * Expires the web content article matching the group, article ID, and
	 * version.
	 *
	 * @param userId the primary key of the user updating the web content
	 article
	 * @param groupId the primary key of the web content article's group
	 * @param articleId the primary key of the web content article
	 * @param version the web content article's version
	 * @param articleURL the web content article's accessible URL
	 * @param serviceContext the service context to be applied. Can set the
	 modification date, status date, portlet preferences, and can set
	 whether to add the default command update for the web content
	 article. With respect to social activities, by setting the
	 service context's command to {@link Constants#UPDATE}, the
	 invocation is considered a web content update activity; otherwise
	 it is considered a web content add activity.
	 * @return the web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle expireArticle(
			long userId, long groupId, String articleId, double version,
			String articleURL,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.expireArticle(
			userId, groupId, articleId, version, articleURL, serviceContext);
	}

	/**
	 * Expires the web content article matching the group and article ID,
	 * expiring all of its versions if the
	 * <code>journal.article.expire.all.versions</code> portal property is
	 * <code>true</code>, otherwise expiring only its latest approved version.
	 *
	 * @param userId the primary key of the user updating the web content
	 article
	 * @param groupId the primary key of the web content article's group
	 * @param articleId the primary key of the web content article
	 * @param articleURL the web content article's accessible URL
	 * @param serviceContext the service context to be applied. Can set the
	 modification date, status date, portlet preferences, and can set
	 whether to add the default command update for the web content
	 article. With respect to social activities, by setting the
	 service context's command to {@link Constants#UPDATE}, the
	 invocation is considered a web content update activity; otherwise
	 it is considered a web content add activity.
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void expireArticle(
			long userId, long groupId, String articleId, String articleURL,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_journalArticleLocalService.expireArticle(
			userId, groupId, articleId, articleURL, serviceContext);
	}

	/**
	 * Returns the web content article with the ID.
	 *
	 * @param id the primary key of the web content article
	 * @return the web content article with the ID
	 */
	@Override
	public JournalArticle fetchArticle(long id) {
		return _journalArticleLocalService.fetchArticle(id);
	}

	@Override
	public JournalArticle fetchArticle(long groupId, String articleId) {
		return _journalArticleLocalService.fetchArticle(groupId, articleId);
	}

	/**
	 * Returns the web content article matching the group, article ID, and
	 * version.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param articleId the primary key of the web content article
	 * @param version the web content article's version
	 * @return the web content article matching the group, article ID, and
	 version, or <code>null</code> if no web content article could be
	 found
	 */
	@Override
	public JournalArticle fetchArticle(
		long groupId, String articleId, double version) {

		return _journalArticleLocalService.fetchArticle(
			groupId, articleId, version);
	}

	@Override
	public JournalArticle fetchArticleByUrlTitle(
		long groupId, String urlTitle) {

		return _journalArticleLocalService.fetchArticleByUrlTitle(
			groupId, urlTitle);
	}

	@Override
	public JournalArticle fetchArticleByUrlTitle(
		long groupId, String urlTitle, double version) {

		return _journalArticleLocalService.fetchArticleByUrlTitle(
			groupId, urlTitle, version);
	}

	@Override
	public JournalArticle fetchDisplayArticle(long groupId, String articleId) {
		return _journalArticleLocalService.fetchDisplayArticle(
			groupId, articleId);
	}

	@Override
	public JournalArticle fetchJournalArticle(long id) {
		return _journalArticleLocalService.fetchJournalArticle(id);
	}

	/**
	 * Returns the journal article matching the UUID and group.
	 *
	 * @param uuid the journal article's UUID
	 * @param groupId the primary key of the group
	 * @return the matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	@Override
	public JournalArticle fetchJournalArticleByUuidAndGroupId(
		String uuid, long groupId) {

		return _journalArticleLocalService.fetchJournalArticleByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public JournalArticle fetchLatestArticle(long resourcePrimKey) {
		return _journalArticleLocalService.fetchLatestArticle(resourcePrimKey);
	}

	@Override
	public JournalArticle fetchLatestArticle(long resourcePrimKey, int status) {
		return _journalArticleLocalService.fetchLatestArticle(
			resourcePrimKey, status);
	}

	/**
	 * Returns the latest web content article matching the resource primary key
	 * and workflow status, optionally preferring articles with approved
	 * workflow status.
	 *
	 * @param resourcePrimKey the primary key of the resource instance
	 * @param status the web content article's workflow status. For more
	 information see {@link WorkflowConstants} for constants starting
	 with the "STATUS_" prefix.
	 * @param preferApproved whether to prefer returning the latest matching
	 article that has workflow status {@link
	 WorkflowConstants#STATUS_APPROVED} over returning one that has a
	 different status
	 * @return the latest web content article matching the resource primary key
	 and workflow status, optionally preferring articles with an
	 approved workflow status, or <code>null</code> if no matching web
	 content article could be found
	 */
	@Override
	public JournalArticle fetchLatestArticle(
		long resourcePrimKey, int status, boolean preferApproved) {

		return _journalArticleLocalService.fetchLatestArticle(
			resourcePrimKey, status, preferApproved);
	}

	@Override
	public JournalArticle fetchLatestArticle(
		long resourcePrimKey, int[] statuses) {

		return _journalArticleLocalService.fetchLatestArticle(
			resourcePrimKey, statuses);
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
	 * @return the latest matching web content article, or <code>null</code> if
	 no matching web content article could be found
	 */
	@Override
	public JournalArticle fetchLatestArticle(
		long groupId, String articleId, int status) {

		return _journalArticleLocalService.fetchLatestArticle(
			groupId, articleId, status);
	}

	/**
	 * Returns the latest web content article matching the group and the
	 * external reference code.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param externalReferenceCode the web content article's external
	 reference code
	 * @return the latest matching web content article, or <code>null</code> if
	 no matching web content article could be found
	 */
	@Override
	public JournalArticle fetchLatestArticleByExternalReferenceCode(
		long groupId, String externalReferenceCode) {

		return _journalArticleLocalService.
			fetchLatestArticleByExternalReferenceCode(
				groupId, externalReferenceCode);
	}

	@Override
	public JournalArticle fetchLatestArticleByExternalReferenceCode(
		long groupId, String externalReferenceCode, int status,
		boolean preferApproved) {

		return _journalArticleLocalService.
			fetchLatestArticleByExternalReferenceCode(
				groupId, externalReferenceCode, status, preferApproved);
	}

	@Override
	public JournalArticle fetchLatestArticleByExternalReferenceCode(
		long groupId, String externalReferenceCode, int[] statuses) {

		return _journalArticleLocalService.
			fetchLatestArticleByExternalReferenceCode(
				groupId, externalReferenceCode, statuses);
	}

	@Override
	public JournalArticle fetchLatestArticleByUrlTitle(
		long groupId, String urlTitle, int status) {

		return _journalArticleLocalService.fetchLatestArticleByUrlTitle(
			groupId, urlTitle, status);
	}

	/**
	 * Returns the latest indexable web content article matching the resource
	 * primary key.
	 *
	 * @param resourcePrimKey the primary key of the resource instance
	 * @return the latest indexable web content article matching the resource
	 primary key, or <code>null</code> if no matching web content
	 article could be found
	 */
	@Override
	public JournalArticle fetchLatestIndexableArticle(long resourcePrimKey) {
		return _journalArticleLocalService.fetchLatestIndexableArticle(
			resourcePrimKey);
	}

	@Override
	public com.liferay.portal.kernel.model.PersistedModel fetchPersistedModel(
		java.io.Serializable primaryKeyObj) {

		return _journalArticleLocalService.fetchPersistedModel(primaryKeyObj);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _journalArticleLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the web content article with the ID.
	 *
	 * @param id the primary key of the web content article
	 * @return the web content article with the ID
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle getArticle(long id)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.getArticle(id);
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
	@Override
	public JournalArticle getArticle(long groupId, String articleId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.getArticle(groupId, articleId);
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
	@Override
	public JournalArticle getArticle(
			long groupId, String articleId, double version)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.getArticle(
			groupId, articleId, version);
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
	@Override
	public JournalArticle getArticle(
			long groupId, String className, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.getArticle(
			groupId, className, classPK);
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
	@Override
	public JournalArticle getArticleByUrlTitle(long groupId, String urlTitle)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.getArticleByUrlTitle(
			groupId, urlTitle);
	}

	/**
	 * Returns the web content from the web content article associated with the
	 * portlet request model and the DDM template.
	 *
	 * @param article the web content article
	 * @param ddmTemplateKey the primary key of the web content article's DDM
	 template
	 * @param viewMode the mode in which the web content is being viewed
	 * @param languageId the primary key of the language translation to get
	 * @param portletRequestModel the portlet request model
	 * @param themeDisplay the theme display
	 * @return the web content from the web content article associated with the
	 portlet request model and the DDM template
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public String getArticleContent(
			JournalArticle article, String ddmTemplateKey, String viewMode,
			String languageId,
			com.liferay.portal.kernel.portlet.PortletRequestModel
				portletRequestModel,
			com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.getArticleContent(
			article, ddmTemplateKey, viewMode, languageId, portletRequestModel,
			themeDisplay);
	}

	/**
	 * Returns the web content from the web content article matching the group,
	 * article ID, and version, and associated with the portlet request model
	 * and the DDM template.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param articleId the primary key of the web content article
	 * @param version the web content article's version
	 * @param viewMode the mode in which the web content is being viewed
	 * @param ddmTemplateKey the primary key of the web content article's DDM
	 template
	 * @param languageId the primary key of the language translation to get
	 * @param portletRequestModel the portlet request model
	 * @param themeDisplay the theme display
	 * @return the web content from the matching web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public String getArticleContent(
			long groupId, String articleId, double version, String viewMode,
			String ddmTemplateKey, String languageId,
			com.liferay.portal.kernel.portlet.PortletRequestModel
				portletRequestModel,
			com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.getArticleContent(
			groupId, articleId, version, viewMode, ddmTemplateKey, languageId,
			portletRequestModel, themeDisplay);
	}

	/**
	 * Returns the latest web content from the web content article matching the
	 * group and article ID, and associated with the portlet request model and
	 * the DDM template.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param articleId the primary key of the web content article
	 * @param viewMode the mode in which the web content is being viewed
	 * @param ddmTemplateKey the primary key of the web content article's DDM
	 template
	 * @param languageId the primary key of the language translation to get
	 * @param portletRequestModel the portlet request model
	 * @param themeDisplay the theme display
	 * @return the latest web content from the matching web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public String getArticleContent(
			long groupId, String articleId, String viewMode,
			String ddmTemplateKey, String languageId,
			com.liferay.portal.kernel.portlet.PortletRequestModel
				portletRequestModel,
			com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.getArticleContent(
			groupId, articleId, viewMode, ddmTemplateKey, languageId,
			portletRequestModel, themeDisplay);
	}

	@Override
	public String getArticleDescription(
		long companyId, long articlePK, java.util.Locale locale) {

		return _journalArticleLocalService.getArticleDescription(
			companyId, articlePK, locale);
	}

	@Override
	public String getArticleDescription(
		long companyId, long articlePK, String languageId) {

		return _journalArticleLocalService.getArticleDescription(
			companyId, articlePK, languageId);
	}

	@Override
	public java.util.Map<java.util.Locale, String> getArticleDescriptionMap(
		long companyId, long articlePK) {

		return _journalArticleLocalService.getArticleDescriptionMap(
			companyId, articlePK);
	}

	/**
	 * Returns a web content article display for the specified page of the
	 * latest version of the web content article, based on the DDM template. Web
	 * content transformation tokens are added using the portlet request model
	 * and theme display.
	 *
	 * @param article the primary key of the web content article
	 * @param ddmTemplateKey the primary key of the web content article's DDM
	 template
	 * @param viewMode the mode in which the web content is being viewed
	 * @param languageId the primary key of the language translation to get
	 * @param page the web content article page to display
	 * @param portletRequestModel the portlet request model
	 * @param themeDisplay the theme display
	 * @return the web content article display, or <code>null</code> if the
	 article has expired or if article's display date/time is after
	 the current date/time
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public com.liferay.journal.model.JournalArticleDisplay getArticleDisplay(
			JournalArticle article, String ddmTemplateKey, String viewMode,
			String languageId, int page,
			com.liferay.portal.kernel.portlet.PortletRequestModel
				portletRequestModel,
			com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.getArticleDisplay(
			article, ddmTemplateKey, viewMode, languageId, page,
			portletRequestModel, themeDisplay);
	}

	/**
	 * Returns a web content article display for the specified page of the
	 * specified version of the web content article matching the group, article
	 * ID, and DDM template. Web content transformation tokens are added using
	 * the portlet request model and theme display.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param articleId the primary key of the web content article
	 * @param version the web content article's version
	 * @param ddmTemplateKey the primary key of the web content article's DDM
	 template
	 * @param viewMode the mode in which the web content is being viewed
	 * @param languageId the primary key of the language translation to get
	 * @param page the web content article page to display
	 * @param portletRequestModel the portlet request model
	 * @param themeDisplay the theme display
	 * @return the web content article display, or <code>null</code> if the
	 article has expired or if article's display date/time is after
	 the current date/time
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public com.liferay.journal.model.JournalArticleDisplay getArticleDisplay(
			long groupId, String articleId, double version,
			String ddmTemplateKey, String viewMode, String languageId, int page,
			com.liferay.portal.kernel.portlet.PortletRequestModel
				portletRequestModel,
			com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.getArticleDisplay(
			groupId, articleId, version, ddmTemplateKey, viewMode, languageId,
			page, portletRequestModel, themeDisplay);
	}

	/**
	 * Returns a web content article display for the first page of the specified
	 * version of the web content article matching the group, article ID, and
	 * DDM template. Web content transformation tokens are added from the theme
	 * display (if not <code>null</code>).
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param articleId the primary key of the web content article
	 * @param version the web content article's version
	 * @param ddmTemplateKey the primary key of the web content article's DDM
	 template
	 * @param viewMode the mode in which the web content is being viewed
	 * @param languageId the primary key of the language translation to get
	 * @param themeDisplay the theme display
	 * @return the web content article display, or <code>null</code> if the
	 article has expired or if article's display date/time is after
	 the current date/time
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public com.liferay.journal.model.JournalArticleDisplay getArticleDisplay(
			long groupId, String articleId, double version,
			String ddmTemplateKey, String viewMode, String languageId,
			com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.getArticleDisplay(
			groupId, articleId, version, ddmTemplateKey, viewMode, languageId,
			themeDisplay);
	}

	/**
	 * Returns a web content article display for the specified page of the
	 * latest version of the web content article matching the group and article
	 * ID. Web content transformation tokens are added from the theme display
	 * (if not <code>null</code>).
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param articleId the primary key of the web content article
	 * @param viewMode the mode in which the web content is being viewed
	 * @param languageId the primary key of the language translation to get
	 * @param page the web content article page to display
	 * @param portletRequestModel the portlet request model
	 * @param themeDisplay the theme display
	 * @return the web content article display, or <code>null</code> if the
	 article has expired or if article's display date/time is after
	 the current date/time
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public com.liferay.journal.model.JournalArticleDisplay getArticleDisplay(
			long groupId, String articleId, String viewMode, String languageId,
			int page,
			com.liferay.portal.kernel.portlet.PortletRequestModel
				portletRequestModel,
			com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.getArticleDisplay(
			groupId, articleId, viewMode, languageId, page, portletRequestModel,
			themeDisplay);
	}

	/**
	 * Returns a web content article display for the specified page of the
	 * latest version of the web content article matching the group, article ID,
	 * and DDM template. Web content transformation tokens are added using the
	 * portlet request model and theme display.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param articleId the primary key of the web content article
	 * @param ddmTemplateKey the primary key of the web content article's DDM
	 template
	 * @param viewMode the mode in which the web content is being viewed
	 * @param languageId the primary key of the language translation to get
	 * @param page the web content article page to display
	 * @param portletRequestModel the portlet request model
	 * @param themeDisplay the theme display
	 * @return the web content article display, or <code>null</code> if the
	 article has expired or if article's display date/time is after
	 the current date/time
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public com.liferay.journal.model.JournalArticleDisplay getArticleDisplay(
			long groupId, String articleId, String ddmTemplateKey,
			String viewMode, String languageId, int page,
			com.liferay.portal.kernel.portlet.PortletRequestModel
				portletRequestModel,
			com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.getArticleDisplay(
			groupId, articleId, ddmTemplateKey, viewMode, languageId, page,
			portletRequestModel, themeDisplay);
	}

	/**
	 * Returns a web content article display for the first page of the latest
	 * version of the web content article matching the group, article ID, and
	 * DDM template. Web content transformation tokens are added from the theme
	 * display (if not <code>null</code>).
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param articleId the primary key of the web content article
	 * @param ddmTemplateKey the primary key of the web content article's DDM
	 template
	 * @param viewMode the mode in which the web content is being viewed
	 * @param languageId the primary key of the language translation to get
	 * @param themeDisplay the theme display
	 * @return the web content article display, or <code>null</code> if the
	 article has expired or if article's display date/time is after
	 the current date/time
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public com.liferay.journal.model.JournalArticleDisplay getArticleDisplay(
			long groupId, String articleId, String ddmTemplateKey,
			String viewMode, String languageId,
			com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.getArticleDisplay(
			groupId, articleId, ddmTemplateKey, viewMode, languageId,
			themeDisplay);
	}

	/**
	 * Returns a web content article display for the first page of the latest
	 * version of the web content article matching the group and article ID. Web
	 * content transformation tokens are added from the theme display (if not
	 * <code>null</code>).
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param articleId the primary key of the web content article
	 * @param viewMode the mode in which the web content is being viewed
	 * @param languageId the primary key of the language translation to get
	 * @param themeDisplay the theme display
	 * @return the web content article display, or <code>null</code> if the
	 article has expired or if article's display date/time is after
	 the current date/time
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public com.liferay.journal.model.JournalArticleDisplay getArticleDisplay(
			long groupId, String articleId, String viewMode, String languageId,
			com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.getArticleDisplay(
			groupId, articleId, viewMode, languageId, themeDisplay);
	}

	@Override
	public java.util.List<String> getArticleLocalizationLanguageIds(
		long companyId, long articlePK) {

		return _journalArticleLocalService.getArticleLocalizationLanguageIds(
			companyId, articlePK);
	}

	/**
	 * Returns all the web content articles present in the system.
	 *
	 * @return the web content articles present in the system
	 */
	@Override
	public java.util.List<JournalArticle> getArticles() {
		return _journalArticleLocalService.getArticles();
	}

	/**
	 * Returns all the web content articles belonging to the group.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @return the web content articles belonging to the group
	 */
	@Override
	public java.util.List<JournalArticle> getArticles(long groupId) {
		return _journalArticleLocalService.getArticles(groupId);
	}

	/**
	 * Returns a range of all the web content articles belonging to the group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
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
	@Override
	public java.util.List<JournalArticle> getArticles(
		long groupId, int start, int end) {

		return _journalArticleLocalService.getArticles(groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the web content articles belonging to the
	 * group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param start the lower bound of the range of web content articles to
	 return
	 * @param end the upper bound of the range of web content articles to
	 return (not inclusive)
	 * @param orderByComparator the comparator to order the web content
	 articles
	 * @return the range of matching web content articles ordered by the
	 comparator
	 */
	@Override
	public java.util.List<JournalArticle> getArticles(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<JournalArticle>
			orderByComparator) {

		return _journalArticleLocalService.getArticles(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns all the web content articles matching the group and folder.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param folderId the primary key of the web content article folder
	 * @return the matching web content articles
	 */
	@Override
	public java.util.List<JournalArticle> getArticles(
		long groupId, long folderId) {

		return _journalArticleLocalService.getArticles(groupId, folderId);
	}

	/**
	 * Returns a range of all the web content articles matching the group and
	 * folder.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param folderId the primary key of the web content article's folder
	 * @param start the lower bound of the range of web content articles to
	 return
	 * @param end the upper bound of the range of web content articles to
	 return (not inclusive)
	 * @return the range of matching web content articles
	 */
	@Override
	public java.util.List<JournalArticle> getArticles(
		long groupId, long folderId, int start, int end) {

		return _journalArticleLocalService.getArticles(
			groupId, folderId, start, end);
	}

	/**
	 * Returns a range of all the web content articles matching the group,
	 * folder, and status.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param folderId the primary key of the web content article's folder
	 * @param status the web content article's workflow status. For more
	 information see {@link WorkflowConstants} for constants starting
	 with the "STATUS_" prefix.
	 * @param start the lower bound of the range of web content articles to
	 return
	 * @param end the upper bound of the range of web content articles to
	 return (not inclusive)
	 * @return the range of matching web content articles
	 */
	@Override
	public java.util.List<JournalArticle> getArticles(
		long groupId, long folderId, int status, int start, int end) {

		return _journalArticleLocalService.getArticles(
			groupId, folderId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the web content articles matching the
	 * group and folder.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param folderId the primary key of the web content article's folder
	 * @param start the lower bound of the range of web content articles to
	 return
	 * @param end the upper bound of the range of web content articles to
	 return (not inclusive)
	 * @param orderByComparator the comparator to order the web content
	 articles
	 * @return the range of matching web content articles ordered by the
	 comparator
	 */
	@Override
	public java.util.List<JournalArticle> getArticles(
		long groupId, long folderId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<JournalArticle>
			orderByComparator) {

		return _journalArticleLocalService.getArticles(
			groupId, folderId, start, end, orderByComparator);
	}

	/**
	 * Returns all the web content articles matching the group and article ID.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param articleId the primary key of the web content article
	 * @return the matching web content articles
	 */
	@Override
	public java.util.List<JournalArticle> getArticles(
		long groupId, String articleId) {

		return _journalArticleLocalService.getArticles(groupId, articleId);
	}

	@Override
	public java.util.List<JournalArticle> getArticles(
		long groupId, String articleId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<JournalArticle>
			orderByComparator) {

		return _journalArticleLocalService.getArticles(
			groupId, articleId, start, end, orderByComparator);
	}

	/**
	 * Returns all the web content articles matching the resource primary key.
	 *
	 * @param resourcePrimKey the primary key of the resource instance
	 * @return the web content articles matching the resource primary key
	 */
	@Override
	public java.util.List<JournalArticle> getArticlesByResourcePrimKey(
		long resourcePrimKey) {

		return _journalArticleLocalService.getArticlesByResourcePrimKey(
			resourcePrimKey);
	}

	@Override
	public java.util.List<JournalArticle> getArticlesByReviewDate(
		long companyId, java.util.Date previousCheckDate,
		java.util.Date reviewDate) {

		return _journalArticleLocalService.getArticlesByReviewDate(
			companyId, previousCheckDate, reviewDate);
	}

	/**
	 * Returns all the web content articles matching the small image ID.
	 *
	 * @param smallImageId the primary key of the web content article's small
	 image
	 * @return the web content articles matching the small image ID
	 */
	@Override
	public java.util.List<JournalArticle> getArticlesBySmallImageId(
		long smallImageId) {

		return _journalArticleLocalService.getArticlesBySmallImageId(
			smallImageId);
	}

	@Override
	public java.util.List<JournalArticle> getArticlesByStructureId(
		long groupId, long ddmStructureId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<JournalArticle>
			orderByComparator) {

		return _journalArticleLocalService.getArticlesByStructureId(
			groupId, ddmStructureId, status, start, end, orderByComparator);
	}

	@Override
	public java.util.List<JournalArticle> getArticlesByStructureId(
		long groupId, long ddmStructureId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<JournalArticle>
			orderByComparator) {

		return _journalArticleLocalService.getArticlesByStructureId(
			groupId, ddmStructureId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<JournalArticle> getArticlesByStructureId(
		long groupId, long ddmStructureId, java.util.Locale locale, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<JournalArticle>
			orderByComparator) {

		return _journalArticleLocalService.getArticlesByStructureId(
			groupId, ddmStructureId, locale, status, start, end,
			orderByComparator);
	}

	@Override
	public java.util.List<JournalArticle> getArticlesByStructureId(
		long groupId, long classNameId, long ddmStructureId, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<JournalArticle>
			orderByComparator) {

		return _journalArticleLocalService.getArticlesByStructureId(
			groupId, classNameId, ddmStructureId, status, start, end,
			orderByComparator);
	}

	@Override
	public java.util.List<JournalArticle> getArticlesByStructureId(
		long groupId, long classNameId, long ddmStructureId,
		java.util.Locale locale, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<JournalArticle>
			orderByComparator) {

		return _journalArticleLocalService.getArticlesByStructureId(
			groupId, classNameId, ddmStructureId, locale, status, start, end,
			orderByComparator);
	}

	@Override
	public java.util.List<Long> getArticlesClassPKsWithDefaultDisplayPage(
		long groupId, long classTypeId) {

		return _journalArticleLocalService.
			getArticlesClassPKsWithDefaultDisplayPage(groupId, classTypeId);
	}

	/**
	 * Returns the number of web content articles belonging to the group.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @return the number of web content articles belonging to the group
	 */
	@Override
	public int getArticlesCount(long groupId) {
		return _journalArticleLocalService.getArticlesCount(groupId);
	}

	/**
	 * Returns the number of web content articles matching the group and folder.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param folderId the primary key of the web content article's folder
	 * @return the number of matching web content articles
	 */
	@Override
	public int getArticlesCount(long groupId, long folderId) {
		return _journalArticleLocalService.getArticlesCount(groupId, folderId);
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
	@Override
	public int getArticlesCount(long groupId, long folderId, int status) {
		return _journalArticleLocalService.getArticlesCount(
			groupId, folderId, status);
	}

	@Override
	public int getArticlesCount(long groupId, String articleId) {
		return _journalArticleLocalService.getArticlesCount(groupId, articleId);
	}

	@Override
	public int getArticlesCountByResourcePrimKey(long resourcePrimKey) {
		return _journalArticleLocalService.getArticlesCountByResourcePrimKey(
			resourcePrimKey);
	}

	@Override
	public String getArticleTitle(
		long companyId, long articlePK, java.util.Locale locale) {

		return _journalArticleLocalService.getArticleTitle(
			companyId, articlePK, locale);
	}

	@Override
	public String getArticleTitle(
		long companyId, long articlePK, String languageId) {

		return _journalArticleLocalService.getArticleTitle(
			companyId, articlePK, languageId);
	}

	@Override
	public java.util.Map<java.util.Locale, String> getArticleTitleMap(
		long companyId, long articlePK) {

		return _journalArticleLocalService.getArticleTitleMap(
			companyId, articlePK);
	}

	/**
	 * Returns an ordered range of all the web content articles matching the
	 * company, version, and workflow status.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param companyId the primary key of the web content article's company
	 * @param version the web content article's version
	 * @param status the web content article's workflow status. For more
	 information see {@link WorkflowConstants} for constants starting
	 with the "STATUS_" prefix.
	 * @param start the lower bound of the range of web content articles to
	 return
	 * @param end the upper bound of the range of web content articles to
	 return (not inclusive)
	 * @return the range of matching web content articles ordered by article ID
	 */
	@Override
	public java.util.List<JournalArticle> getCompanyArticles(
		long companyId, double version, int status, int start, int end) {

		return _journalArticleLocalService.getCompanyArticles(
			companyId, version, status, start, end);
	}

	/**
	 * Returns an ordered range of all the web content articles matching the
	 * company and workflow status.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param companyId the primary key of the web content article's company
	 * @param status the web content article's workflow status. For more
	 information see {@link WorkflowConstants} for constants starting
	 with the "STATUS_" prefix.
	 * @param start the lower bound of the range of web content articles to
	 return
	 * @param end the upper bound of the range of web content articles to
	 return (not inclusive)
	 * @return the range of matching web content articles ordered by article ID
	 */
	@Override
	public java.util.List<JournalArticle> getCompanyArticles(
		long companyId, int status, int start, int end) {

		return _journalArticleLocalService.getCompanyArticles(
			companyId, status, start, end);
	}

	/**
	 * Returns the number of web content articles matching the company, version,
	 * and workflow status.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param companyId the primary key of the web content article's company
	 * @param version the web content article's version
	 * @param status the web content article's workflow status. For more
	 information see {@link WorkflowConstants} for constants starting
	 with the "STATUS_" prefix.
	 * @param start the lower bound of the range of web content articles to
	 return
	 * @param end the upper bound of the range of web content articles to
	 return (not inclusive)
	 * @return the number of matching web content articles
	 */
	@Override
	public int getCompanyArticlesCount(
		long companyId, double version, int status, int start, int end) {

		return _journalArticleLocalService.getCompanyArticlesCount(
			companyId, version, status, start, end);
	}

	/**
	 * Returns the number of web content articles matching the company and
	 * workflow status.
	 *
	 * @param companyId the primary key of the web content article's company
	 * @param status the web content article's workflow status. For more
	 information see {@link WorkflowConstants} for constants starting
	 with the "STATUS_" prefix.
	 * @return the number of matching web content articles
	 */
	@Override
	public int getCompanyArticlesCount(long companyId, int status) {
		return _journalArticleLocalService.getCompanyArticlesCount(
			companyId, status);
	}

	/**
	 * Returns the matching web content article currently displayed or next to
	 * be displayed if no article is currently displayed.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param articleId the primary key of the web content article
	 * @return the matching web content article currently displayed, or the next
	 one to be displayed if no version of the article is currently
	 displayed
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle getDisplayArticle(long groupId, String articleId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.getDisplayArticle(
			groupId, articleId);
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
	@Override
	public JournalArticle getDisplayArticleByUrlTitle(
			long groupId, String urlTitle)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.getDisplayArticleByUrlTitle(
			groupId, urlTitle);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _journalArticleLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public java.util.List<Long> getGroupIdsByUrlTitle(
		long companyId, String urlTitle) {

		return _journalArticleLocalService.getGroupIdsByUrlTitle(
			companyId, urlTitle);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _journalArticleLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the journal article with the primary key.
	 *
	 * @param id the primary key of the journal article
	 * @return the journal article
	 * @throws PortalException if a journal article with the primary key could not be found
	 */
	@Override
	public JournalArticle getJournalArticle(long id)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.getJournalArticle(id);
	}

	/**
	 * Returns the journal article matching the UUID and group.
	 *
	 * @param uuid the journal article's UUID
	 * @param groupId the primary key of the group
	 * @return the matching journal article
	 * @throws PortalException if a matching journal article could not be found
	 */
	@Override
	public JournalArticle getJournalArticleByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.getJournalArticleByUuidAndGroupId(
			uuid, groupId);
	}

	/**
	 * Returns a range of all the journal articles.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of journal articles
	 */
	@Override
	public java.util.List<JournalArticle> getJournalArticles(
		int start, int end) {

		return _journalArticleLocalService.getJournalArticles(start, end);
	}

	/**
	 * Returns all the journal articles matching the UUID and company.
	 *
	 * @param uuid the UUID of the journal articles
	 * @param companyId the primary key of the company
	 * @return the matching journal articles, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<JournalArticle> getJournalArticlesByUuidAndCompanyId(
		String uuid, long companyId) {

		return _journalArticleLocalService.getJournalArticlesByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of journal articles matching the UUID and company.
	 *
	 * @param uuid the UUID of the journal articles
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching journal articles, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<JournalArticle> getJournalArticlesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<JournalArticle>
			orderByComparator) {

		return _journalArticleLocalService.getJournalArticlesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of journal articles.
	 *
	 * @return the number of journal articles
	 */
	@Override
	public int getJournalArticlesCount() {
		return _journalArticleLocalService.getJournalArticlesCount();
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
	@Override
	public JournalArticle getLatestArticle(long resourcePrimKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.getLatestArticle(resourcePrimKey);
	}

	/**
	 * Returns the latest web content article matching the resource primary key
	 * and workflow status, preferring articles with approved workflow status.
	 *
	 * @param resourcePrimKey the primary key of the resource instance
	 * @param status the web content article's workflow status. For more
	 information see {@link WorkflowConstants} for constants starting
	 with the "STATUS_" prefix.
	 * @return the latest web content article matching the resource primary key
	 and workflow status, preferring articles with approved workflow
	 status
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle getLatestArticle(long resourcePrimKey, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.getLatestArticle(
			resourcePrimKey, status);
	}

	/**
	 * Returns the latest web content article matching the resource primary key
	 * and workflow status, optionally preferring articles with approved
	 * workflow status.
	 *
	 * @param resourcePrimKey the primary key of the resource instance
	 * @param status the web content article's workflow status. For more
	 information see {@link WorkflowConstants} for constants starting
	 with the "STATUS_" prefix.
	 * @param preferApproved whether to prefer returning the latest matching
	 article that has workflow status {@link
	 WorkflowConstants#STATUS_APPROVED} over returning one that has a
	 different status
	 * @return the latest web content article matching the resource primary key
	 and workflow status, optionally preferring articles with approved
	 workflow status
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle getLatestArticle(
			long resourcePrimKey, int status, boolean preferApproved)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.getLatestArticle(
			resourcePrimKey, status, preferApproved);
	}

	/**
	 * Returns the latest web content article with the group and article ID.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param articleId the primary key of the web content article
	 * @return the latest matching web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle getLatestArticle(long groupId, String articleId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.getLatestArticle(groupId, articleId);
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
	@Override
	public JournalArticle getLatestArticle(
			long groupId, String articleId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.getLatestArticle(
			groupId, articleId, status);
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
	@Override
	public JournalArticle getLatestArticle(
			long groupId, String className, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.getLatestArticle(
			groupId, className, classPK);
	}

	/**
	 * Returns the latest web content article matching the group and the
	 * external reference code.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param externalReferenceCode the web content article's external
	 reference code
	 * @return the latest matching web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle getLatestArticleByExternalReferenceCode(
			long groupId, String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.
			getLatestArticleByExternalReferenceCode(
				groupId, externalReferenceCode);
	}

	@Override
	public JournalArticle getLatestArticleByExternalReferenceCode(
			long groupId, String externalReferenceCode, int status,
			boolean preferApproved)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.
			getLatestArticleByExternalReferenceCode(
				groupId, externalReferenceCode, status, preferApproved);
	}

	/**
	 * Returns the latest web content article matching the group, URL title, and
	 * workflow status.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param urlTitle the web content article's accessible URL title
	 * @param status the web content article's workflow status. For more
	 information see {@link WorkflowConstants} for constants starting
	 with the "STATUS_" prefix.
	 * @return the latest matching web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle getLatestArticleByUrlTitle(
			long groupId, String urlTitle, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.getLatestArticleByUrlTitle(
			groupId, urlTitle, status);
	}

	/**
	 * Returns the latest version number of the web content with the group and
	 * article ID.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param articleId the primary key of the web content article
	 * @return the latest version number of the matching web content
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public double getLatestVersion(long groupId, String articleId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.getLatestVersion(groupId, articleId);
	}

	/**
	 * Returns the latest version number of the web content with the group,
	 * article ID, and workflow status.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param articleId the primary key of the web content article
	 * @param status the web content article's workflow status. For more
	 information see {@link WorkflowConstants} for constants starting
	 with the "STATUS_" prefix.
	 * @return the latest version number of the matching web content
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public double getLatestVersion(long groupId, String articleId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.getLatestVersion(
			groupId, articleId, status);
	}

	@Override
	public java.util.List<JournalArticle> getNoAssetArticles() {
		return _journalArticleLocalService.getNoAssetArticles();
	}

	@Override
	public java.util.List<JournalArticle> getNoPermissionArticles() {
		return _journalArticleLocalService.getNoPermissionArticles();
	}

	/**
	 * Returns the number of web content articles that are not recycled.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param folderId the primary key of the web content article folder
	 * @return the number of web content articles that are not recycled
	 */
	@Override
	public int getNotInTrashArticlesCount(long groupId, long folderId) {
		return _journalArticleLocalService.getNotInTrashArticlesCount(
			groupId, folderId);
	}

	/**
	 * Returns the oldest web content article with the group and article ID.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param articleId the primary key of the web content article
	 * @return the oldest matching web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle getOldestArticle(long groupId, String articleId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.getOldestArticle(groupId, articleId);
	}

	/**
	 * Returns the oldest web content article matching the group, article ID,
	 * and workflow status.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param articleId the primary key of the web content article
	 * @param status the web content article's workflow status. For more
	 information see {@link WorkflowConstants} for constants starting
	 with the "STATUS_" prefix.
	 * @return the oldest matching web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle getOldestArticle(
			long groupId, String articleId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.getOldestArticle(
			groupId, articleId, status);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _journalArticleLocalService.getOSGiServiceIdentifier();
	}

	@Override
	public java.util.List
		<? extends com.liferay.portal.kernel.model.PersistedModel>
				getPersistedModel(long resourcePrimKey)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.getPersistedModel(resourcePrimKey);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns the previously approved version of the web content article. For
	 * more information on the approved workflow status, see {@link
	 * WorkflowConstants#STATUS_APPROVED}.
	 *
	 * @param article the web content article
	 * @return the previously approved version of the web content article, or
	 the current web content article if there are no previously
	 approved web content articles
	 */
	@Override
	public JournalArticle getPreviousApprovedArticle(JournalArticle article) {
		return _journalArticleLocalService.getPreviousApprovedArticle(article);
	}

	/**
	 * Returns the web content articles matching the DDM structure keys.
	 *
	 * @param ddmStructureId the primary key of the web content article's DDM
	 structure
	 * @return the web content articles matching the DDM structure keys
	 */
	@Override
	public java.util.List<JournalArticle> getStructureArticles(
		long ddmStructureId) {

		return _journalArticleLocalService.getStructureArticles(ddmStructureId);
	}

	/**
	 * Returns the web content articles matching the group and DDM structure
	 * key.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param ddmStructureId the primary key of the web content article's DDM
	 structure
	 * @return the matching web content articles
	 */
	@Override
	public java.util.List<JournalArticle> getStructureArticles(
		long groupId, long ddmStructureId) {

		return _journalArticleLocalService.getStructureArticles(
			groupId, ddmStructureId);
	}

	/**
	 * Returns an ordered range of all the web content articles matching the
	 * group and DDM structure key.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
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
	@Override
	public java.util.List<JournalArticle> getStructureArticles(
		long groupId, long ddmStructureId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<JournalArticle>
			orderByComparator) {

		return _journalArticleLocalService.getStructureArticles(
			groupId, ddmStructureId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of web content articles matching the group and DDM
	 * structure key.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param ddmStructureId the primary key of the web content article's DDM
	 structure
	 * @return the number of matching web content articles
	 */
	@Override
	public int getStructureArticlesCount(long groupId, long ddmStructureId) {
		return _journalArticleLocalService.getStructureArticlesCount(
			groupId, ddmStructureId);
	}

	/**
	 * Returns the web content articles matching the group and DDM template key.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param ddmTemplateKey the primary key of the web content article's DDM
	 template
	 * @return the matching web content articles
	 */
	@Override
	public java.util.List<JournalArticle> getTemplateArticles(
		long groupId, String ddmTemplateKey) {

		return _journalArticleLocalService.getTemplateArticles(
			groupId, ddmTemplateKey);
	}

	/**
	 * Returns an ordered range of all the web content articles matching the
	 * group and DDM template key.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param ddmTemplateKey the primary key of the web content article's DDM
	 template
	 * @param start the lower bound of the range of web content articles to
	 return
	 * @param end the upper bound of the range of web content articles to
	 return (not inclusive)
	 * @param orderByComparator the comparator to order the web content
	 articles
	 * @return the range of matching web content articles ordered by the
	 comparator
	 */
	@Override
	public java.util.List<JournalArticle> getTemplateArticles(
		long groupId, String ddmTemplateKey, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<JournalArticle>
			orderByComparator) {

		return _journalArticleLocalService.getTemplateArticles(
			groupId, ddmTemplateKey, start, end, orderByComparator);
	}

	/**
	 * Returns the number of web content articles matching the group and DDM
	 * template key.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param ddmTemplateKey the primary key of the web content article's DDM
	 template
	 * @return the number of matching web content articles
	 */
	@Override
	public int getTemplateArticlesCount(long groupId, String ddmTemplateKey) {
		return _journalArticleLocalService.getTemplateArticlesCount(
			groupId, ddmTemplateKey);
	}

	/**
	 * Returns the web content article's unique URL title.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param articleId the primary key of the web content article
	 * @param urlTitle the web content article's accessible URL title
	 * @return the web content article's unique URL title
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public String getUniqueUrlTitle(
			long groupId, String articleId, String urlTitle)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.getUniqueUrlTitle(
			groupId, articleId, urlTitle);
	}

	/**
	 * Returns <code>true</code> if the specified web content article exists.
	 *
	 * @param groupId the primary key of the group
	 * @param articleId the primary key of the web content article
	 * @return <code>true</code> if the specified web content article exists;
	 <code>false</code> otherwise
	 */
	@Override
	public boolean hasArticle(long groupId, String articleId) {
		return _journalArticleLocalService.hasArticle(groupId, articleId);
	}

	/**
	 * Returns <code>true</code> if the web content article, specified by group
	 * and article ID, is the latest version.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param articleId the primary key of the web content article
	 * @param version the web content article's version
	 * @return <code>true</code> if the specified web content article is the
	 latest version; <code>false</code> otherwise
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public boolean isLatestVersion(
			long groupId, String articleId, double version)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.isLatestVersion(
			groupId, articleId, version);
	}

	/**
	 * Returns <code>true</code> if the web content article, specified by group,
	 * article ID, and workflow status, is the latest version.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param articleId the primary key of the web content article
	 * @param version the web content article's version
	 * @param status the web content article's workflow status. For more
	 information see {@link WorkflowConstants} for constants starting
	 with the "STATUS_" prefix.
	 * @return <code>true</code> if the specified web content article is the
	 latest version; <code>false</code> otherwise
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public boolean isLatestVersion(
			long groupId, String articleId, double version, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.isLatestVersion(
			groupId, articleId, version, status);
	}

	@Override
	public boolean isListable(JournalArticle article) {
		return _journalArticleLocalService.isListable(article);
	}

	@Override
	public boolean isRenderable(
		JournalArticle article,
		com.liferay.portal.kernel.portlet.PortletRequestModel
			portletRequestModel,
		com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay) {

		return _journalArticleLocalService.isRenderable(
			article, portletRequestModel, themeDisplay);
	}

	/**
	 * Moves the web content article matching the group and article ID to a new
	 * folder.
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
	 {@link Constants#UPDATE}, the invocation is considered a web
	 content update activity; otherwise it is considered a web content
	 add activity.
	 * @return the updated web content article, which was moved to a new folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle moveArticle(
			long groupId, String articleId, long newFolderId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.moveArticle(
			groupId, articleId, newFolderId, serviceContext);
	}

	/**
	 * Moves the web content article from the Recycle Bin to a new folder.
	 *
	 * @param userId the primary key of the user updating the web content
	 article
	 * @param groupId the primary key of the web content article's group
	 * @param article the web content article
	 * @param newFolderId the primary key of the web content article's new
	 folder
	 * @param serviceContext the service context to be applied. Can set the
	 modification date, portlet preferences, and can set whether to
	 add the default command update for the web content article. With
	 respect to social activities, by setting the service context's
	 command to {@link Constants#UPDATE}, the invocation is considered
	 a web content update activity; otherwise it is considered a web
	 content add activity.
	 * @return the updated web content article, which was moved from the Recycle
	 Bin to a new folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle moveArticleFromTrash(
			long userId, long groupId, JournalArticle article, long newFolderId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.moveArticleFromTrash(
			userId, groupId, article, newFolderId, serviceContext);
	}

	/**
	 * Moves the latest version of the web content article matching the group
	 * and article ID to the recycle bin.
	 *
	 * @param userId the primary key of the user updating the web content
	 article
	 * @param article the web content article
	 * @return the updated web content article, which was moved to the Recycle
	 Bin
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle moveArticleToTrash(
			long userId, JournalArticle article)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.moveArticleToTrash(userId, article);
	}

	/**
	 * Moves the latest version of the web content article matching the group
	 * and article ID to the recycle bin.
	 *
	 * @param userId the primary key of the user updating the web content
	 article
	 * @param groupId the primary key of the web content article's group
	 * @param articleId the primary key of the web content article
	 * @return the moved web content article or <code>null</code> if no matching
	 article was found
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle moveArticleToTrash(
			long userId, long groupId, String articleId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.moveArticleToTrash(
			userId, groupId, articleId);
	}

	/**
	 * Rebuilds the web content article's tree path using tree traversal.
	 *
	 * <p>
	 * For example, here is a conceptualization of a web content article tree
	 * path:
	 * </p>
	 *
	 * <p>
	 * <pre>
	 * <code>
	 * /(Folder's folderId)/(Subfolder's folderId)/(article's articleId)
	 * </code>
	 * </pre></p>
	 *
	 * @param companyId the primary key of the web content article's company
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void rebuildTree(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_journalArticleLocalService.rebuildTree(companyId);
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
	@Override
	public JournalArticle removeArticleLocale(
			long groupId, String articleId, double version, String languageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.removeArticleLocale(
			groupId, articleId, version, languageId);
	}

	/**
	 * Restores the web content article from the Recycle Bin.
	 *
	 * @param userId the primary key of the user restoring the web content
	 article
	 * @param article the web content article
	 * @return the restored web content article from the Recycle Bin
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle restoreArticleFromTrash(
			long userId, JournalArticle article)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.restoreArticleFromTrash(
			userId, article);
	}

	@Override
	public JournalArticle revertArticle(
			long userId, long groupId, String articleId, double version)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.revertArticle(
			userId, groupId, articleId, version);
	}

	@Override
	public void setTreePaths(long folderId, String treePath, boolean reindex)
		throws com.liferay.portal.kernel.exception.PortalException {

		_journalArticleLocalService.setTreePaths(folderId, treePath, reindex);
	}

	/**
	 * Subscribes the user to changes in elements that belong to the web content
	 * article.
	 *
	 * @param userId the primary key of the user to be subscribed
	 * @param groupId the primary key of the folder's group
	 * @param articleId the primary key of the article to subscribe to
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void subscribe(long userId, long groupId, long articleId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_journalArticleLocalService.subscribe(userId, groupId, articleId);
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
	@Override
	public void subscribeStructure(
			long groupId, long userId, long ddmStructureId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_journalArticleLocalService.subscribeStructure(
			groupId, userId, ddmStructureId);
	}

	/**
	 * Unsubscribes the user from changes in elements that belong to the web
	 * content article.
	 *
	 * @param userId the primary key of the user to be subscribed
	 * @param groupId the primary key of the folder's group
	 * @param articleId the primary key of the article to unsubscribe from
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void unsubscribe(long userId, long groupId, long articleId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_journalArticleLocalService.unsubscribe(userId, groupId, articleId);
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
	@Override
	public void unsubscribeStructure(
			long groupId, long userId, long ddmStructureId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_journalArticleLocalService.unsubscribeStructure(
			groupId, userId, ddmStructureId);
	}

	/**
	 * Updates the web content article with additional parameters. All
	 * scheduling parameters (display date, expiration date, and review date)
	 * use the current user's timezone.
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
	 * @param friendlyURLMap the web content article's locales and localized
	 friendly URLs
	 * @param content the HTML content wrapped in XML. For more information,
	 see the content example in the {@link #addArticle(String, long,
	 long, long, long, long, String, boolean, double, Map, Map, Map,
	 String, long, String, String, int, int, int, int, int, int, int,
	 int, int, int, boolean, int, int, int, int, int, boolean,
	 boolean, boolean, long, int, String, File, Map, String,
	 ServiceContext)} description.
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
	 * @param smallImageFile the web content article's new small image file
	 (optionally <code>null</code>). Must pass in
	 <code>smallImage</code> value of <code>true</code> to replace the
	 article's small image file.
	 * @param images the web content's images (optionally <code>null</code>)
	 * @param articleURL the web content article's accessible URL (optionally
	 <code>null</code>)
	 * @param serviceContext the service context to be applied. Can set the
	 modification date, expando bridge attributes, asset category IDs,
	 asset tag names, asset link entry IDs, asset priority, workflow
	 actions, URL title , and can set whether to add the default
	 command update for the web content article. With respect to
	 social activities, by setting the service context's command to
	 {@link Constants#UPDATE}, the invocation is considered a web
	 content update activity; otherwise it is considered a web content
	 add activity.
	 * @return the updated web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle updateArticle(
			long userId, long groupId, long folderId, String articleId,
			double version, java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			java.util.Map<java.util.Locale, String> friendlyURLMap,
			String content, String ddmTemplateKey, String layoutUuid,
			int displayDateMonth, int displayDateDay, int displayDateYear,
			int displayDateHour, int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, int reviewDateMonth, int reviewDateDay,
			int reviewDateYear, int reviewDateHour, int reviewDateMinute,
			boolean neverReview, boolean indexable, boolean smallImage,
			long smallImageId, int smallImageSource, String smallImageURL,
			java.io.File smallImageFile, java.util.Map<String, byte[]> images,
			String articleURL,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.updateArticle(
			userId, groupId, folderId, articleId, version, titleMap,
			descriptionMap, friendlyURLMap, content, ddmTemplateKey, layoutUuid,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire, reviewDateMonth, reviewDateDay, reviewDateYear,
			reviewDateHour, reviewDateMinute, neverReview, indexable,
			smallImage, smallImageId, smallImageSource, smallImageURL,
			smallImageFile, images, articleURL, serviceContext);
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
	 see the content example in the {@link #addArticle(String, long,
	 long, long, long, long, String, boolean, double, Map, Map, Map,
	 String, long, String, String, int, int, int, int, int, int, int,
	 int, int, int, boolean, int, int, int, int, int, boolean,
	 boolean, boolean, long, int, String, File, Map, String,
	 ServiceContext)} description.
	 * @param layoutUuid the unique string identifying the web content
	 article's display page
	 * @param serviceContext the service context to be applied. Can set the
	 modification date, expando bridge attributes, asset category IDs,
	 asset tag names, asset link entry IDs, asset priority, workflow
	 actions, URL title, and can set whether to add the default
	 command update for the web content article. With respect to
	 social activities, by setting the service context's command to
	 {@link Constants#UPDATE}, the invocation is considered a web
	 content update activity; otherwise it is considered a web content
	 add activity.
	 * @return the updated web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle updateArticle(
			long userId, long groupId, long folderId, String articleId,
			double version, java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String content, String layoutUuid,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.updateArticle(
			userId, groupId, folderId, articleId, version, titleMap,
			descriptionMap, content, layoutUuid, serviceContext);
	}

	/**
	 * Updates the web content article matching the version, replacing its
	 * folder and content.
	 *
	 * @param userId the primary key of the user updating the web content
	 article
	 * @param groupId the primary key of the web content article's group
	 * @param folderId the primary key of the web content article folder
	 * @param articleId the primary key of the web content article
	 * @param version the web content article's version
	 * @param content the HTML content wrapped in XML. For more information,
	 see the content example in the {@link #addArticle(String, long,
	 long, long, long, long, String, boolean, double, Map, Map, Map,
	 String, long, String, String, int, int, int, int, int, int, int,
	 int, int, int, boolean, int, int, int, int, int, boolean,
	 boolean, boolean, long, int, String, File, Map, String,
	 ServiceContext)} description.
	 * @param serviceContext the service context to be applied. Can set the
	 modification date, expando bridge attributes, asset category IDs,
	 asset tag names, asset link entry IDs, asset priority, workflow
	 actions, URL title, and can set whether to add the default
	 command update for the web content article. With respect to
	 social activities, by setting the service context's command to
	 {@link Constants#UPDATE}, the invocation is considered a web
	 content update activity; otherwise it is considered a web content
	 add activity.
	 * @return the updated web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle updateArticle(
			long userId, long groupId, long folderId, String articleId,
			double version, String content,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.updateArticle(
			userId, groupId, folderId, articleId, version, content,
			serviceContext);
	}

	/**
	 * Updates the URL title of the web content article.
	 *
	 * @param id the primary key of the web content article
	 * @param urlTitle the web content article's URL title
	 * @return the updated web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle updateArticle(long id, String urlTitle)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.updateArticle(id, urlTitle);
	}

	@Override
	public JournalArticle updateArticleDefaultValues(
			long userId, long groupId, String articleId,
			java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String content, String ddmTemplateKey, String layoutUuid,
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
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.updateArticleDefaultValues(
			userId, groupId, articleId, titleMap, descriptionMap, content,
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
	 see the content example in the {@link #addArticle(String, long,
	 long, long, long, long, String, boolean, double, Map, Map, Map,
	 String, long, String, String, int, int, int, int, int, int, int,
	 int, int, int, boolean, int, int, int, int, int, boolean,
	 boolean, boolean, long, int, String, File, Map, String,
	 ServiceContext)} description.
	 * @param images the web content's images
	 * @param serviceContext the service context to be applied. Can set the
	 modification date and URL title for the web content article.
	 * @return the updated web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle updateArticleTranslation(
			long groupId, String articleId, double version,
			java.util.Locale locale, String title, String description,
			String content, java.util.Map<String, byte[]> images,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.updateArticleTranslation(
			groupId, articleId, version, locale, title, description, content,
			images, serviceContext);
	}

	/**
	 * Updates the web content article's asset with the new asset categories,
	 * tag names, and link entries, removing and adding them as necessary.
	 *
	 * @param userId the primary key of the user updating the web content
	 article's asset
	 * @param article the web content article
	 * @param assetCategoryIds the primary keys of the new asset categories
	 * @param assetTagNames the new asset tag names
	 * @param assetLinkEntryIds the primary keys of the new asset link entries
	 * @param priority the priority of the asset
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void updateAsset(
			long userId, JournalArticle article, long[] assetCategoryIds,
			String[] assetTagNames, long[] assetLinkEntryIds, Double priority)
		throws com.liferay.portal.kernel.exception.PortalException {

		_journalArticleLocalService.updateAsset(
			userId, article, assetCategoryIds, assetTagNames, assetLinkEntryIds,
			priority);
	}

	/**
	 * Updates the web content articles matching the group, class name ID, and
	 * DDM template key, replacing the DDM template key with a new one.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param classNameId the primary key of the DDMStructure class if the web
	 content article is related to a DDM structure, the primary key of
	 the class name associated with the article, or
	 JournalArticleConstants.CLASS_NAME_ID_DEFAULT in the journal-api
	 module otherwise
	 * @param oldDDMTemplateKey the primary key of the web content article's old
	 DDM template
	 * @param newDDMTemplateKey the primary key of the web content article's new
	 DDM template
	 */
	@Override
	public void updateDDMTemplateKey(
		long groupId, long classNameId, String oldDDMTemplateKey,
		String newDDMTemplateKey) {

		_journalArticleLocalService.updateDDMTemplateKey(
			groupId, classNameId, oldDDMTemplateKey, newDDMTemplateKey);
	}

	/**
	 * Updates the journal article in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect JournalArticleLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param journalArticle the journal article
	 * @return the journal article that was updated
	 */
	@Override
	public JournalArticle updateJournalArticle(JournalArticle journalArticle) {
		return _journalArticleLocalService.updateJournalArticle(journalArticle);
	}

	/**
	 * Updates the workflow status of the web content article.
	 *
	 * @param userId the primary key of the user updating the web content
	 article's status
	 * @param article the web content article
	 * @param status the web content article's workflow status. For more
	 information see {@link WorkflowConstants} for constants starting
	 with the "STATUS_" prefix.
	 * @param articleURL the web content article's accessible URL
	 * @param serviceContext the service context to be applied. Can set the
	 modification date, status date, and portlet preferences. With
	 respect to social activities, by setting the service context's
	 command to {@link Constants#UPDATE}, the invocation is considered
	 a web content update activity; otherwise it is considered a web
	 content add activity.
	 * @param workflowContext the web content article's configured workflow
	 context
	 * @return the updated web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle updateStatus(
			long userId, JournalArticle article, int status, String articleURL,
			com.liferay.portal.kernel.service.ServiceContext serviceContext,
			java.util.Map<String, java.io.Serializable> workflowContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.updateStatus(
			userId, article, status, articleURL, serviceContext,
			workflowContext);
	}

	/**
	 * Updates the workflow status of the web content article matching the class
	 * PK.
	 *
	 * @param userId the primary key of the user updating the web content
	 article's status
	 * @param classPK the primary key of the DDM structure, if the web content
	 article is related to a DDM structure, the primary key of the
	 class associated with the article, or <code>0</code> otherwise
	 * @param status the web content article's workflow status. For more
	 information see {@link WorkflowConstants} for constants starting
	 with the "STATUS_" prefix.
	 * @param workflowContext the web content article's configured workflow
	 * @param serviceContext the service context to be applied. Can set the
	 modification date, portlet preferences, and can set whether to
	 add the default command update for the web content article.
	 * @return the updated web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle updateStatus(
			long userId, long classPK, int status,
			java.util.Map<String, java.io.Serializable> workflowContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.updateStatus(
			userId, classPK, status, workflowContext, serviceContext);
	}

	/**
	 * Updates the workflow status of the web content article matching the
	 * group, article ID, and version.
	 *
	 * @param userId the primary key of the user updating the web content
	 article's status
	 * @param groupId the primary key of the web content article's group
	 * @param articleId the primary key of the web content article
	 * @param version the web content article's version
	 * @param status the web content article's workflow status. For more
	 information see {@link WorkflowConstants} for constants starting
	 with the "STATUS_" prefix.
	 * @param articleURL the web content article's accessible URL
	 * @param workflowContext the web content article's configured workflow
	 * @param serviceContext the service context to be applied. Can set the
	 modification date, portlet preferences, and can set whether to
	 add the default command update for the web content article.
	 * @return the updated web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle updateStatus(
			long userId, long groupId, String articleId, double version,
			int status, String articleURL,
			java.util.Map<String, java.io.Serializable> workflowContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _journalArticleLocalService.updateStatus(
			userId, groupId, articleId, version, status, articleURL,
			workflowContext, serviceContext);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _journalArticleLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<JournalArticle> getCTPersistence() {
		return _journalArticleLocalService.getCTPersistence();
	}

	@Override
	public Class<JournalArticle> getModelClass() {
		return _journalArticleLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<JournalArticle>, R, E>
				updateUnsafeFunction)
		throws E {

		return _journalArticleLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public JournalArticleLocalService getWrappedService() {
		return _journalArticleLocalService;
	}

	@Override
	public void setWrappedService(
		JournalArticleLocalService journalArticleLocalService) {

		_journalArticleLocalService = journalArticleLocalService;
	}

	private JournalArticleLocalService _journalArticleLocalService;

}