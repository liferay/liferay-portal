/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.service.impl;

import com.liferay.asset.display.page.model.AssetDisplayPageEntry;
import com.liferay.asset.display.page.model.AssetDisplayPageEntryTable;
import com.liferay.asset.display.page.portlet.AssetDisplayPageEntryFormProcessor;
import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.asset.display.page.service.AssetDisplayPageEntryLocalService;
import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetEntryTable;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.link.constants.AssetLinkConstants;
import com.liferay.asset.link.model.AssetLink;
import com.liferay.asset.link.service.AssetLinkLocalService;
import com.liferay.diff.DiffHtml;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMStorageLink;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureLink;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.service.DDMFieldLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStorageLinkLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLinkLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureVersionLocalService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLinkLocalService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.Fields;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesToFieldsConverter;
import com.liferay.dynamic.data.mapping.util.FieldsToDDMFormValuesConverter;
import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.expando.kernel.util.ExpandoBridgeUtil;
import com.liferay.exportimport.kernel.exception.ExportImportContentValidationException;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.friendly.url.exception.NoSuchFriendlyURLEntryLocalizationException;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.model.FriendlyURLEntryLocalization;
import com.liferay.friendly.url.model.FriendlyURLEntryLocalizationTable;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.journal.configuration.JournalGroupServiceConfiguration;
import com.liferay.journal.configuration.JournalServiceConfiguration;
import com.liferay.journal.constants.JournalActivityKeys;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.content.compatibility.converter.JournalContentCompatibilityConverter;
import com.liferay.journal.exception.ArticleExpirationDateException;
import com.liferay.journal.exception.ArticleFriendlyURLException;
import com.liferay.journal.exception.ArticleReviewDateException;
import com.liferay.journal.exception.ArticleVersionException;
import com.liferay.journal.exception.DuplicateArticleIdException;
import com.liferay.journal.exception.NoSuchArticleException;
import com.liferay.journal.exception.RequiredArticleLocalizationException;
import com.liferay.journal.internal.transformer.JournalTransformer;
import com.liferay.journal.internal.util.JournalTreePathUtil;
import com.liferay.journal.internal.util.JournalUtil;
import com.liferay.journal.internal.validation.JournalArticleModelValidator;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleDisplay;
import com.liferay.journal.model.JournalArticleLocalization;
import com.liferay.journal.model.JournalArticleResource;
import com.liferay.journal.model.JournalArticleTable;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.model.impl.JournalArticleDisplayImpl;
import com.liferay.journal.model.impl.JournalArticleImpl;
import com.liferay.journal.service.JournalArticleResourceLocalService;
import com.liferay.journal.service.base.JournalArticleLocalServiceBaseImpl;
import com.liferay.journal.service.persistence.JournalArticleLocalizationPersistence;
import com.liferay.journal.service.persistence.JournalArticleResourcePersistence;
import com.liferay.journal.service.persistence.JournalFolderPersistence;
import com.liferay.journal.util.JournalConverter;
import com.liferay.journal.util.JournalDefaultTemplateProvider;
import com.liferay.journal.util.JournalHelper;
import com.liferay.journal.util.comparator.ArticleIDComparator;
import com.liferay.journal.util.comparator.ArticleVersionComparator;
import com.liferay.layout.display.page.LayoutDisplayPageProviderRegistry;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.comment.CommentManager;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DefaultActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Image;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermissionTable;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.notifications.UserNotificationDefinition;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.PortletRequestModel;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.capabilities.TemporaryFileEntriesCapability;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ImageLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.SystemEventLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.social.SocialActivityManagerUtil;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.systemevent.SystemEventHierarchyEntryThreadLocal;
import com.liferay.portal.kernel.templateparser.TransformerListener;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.transaction.TransactionCommitCallbackUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.EscapableLocalizableFunction;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.FriendlyURLNormalizer;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.GroupSubscriptionCheckSubscriptionSender;
import com.liferay.portal.kernel.util.GroupThreadLocal;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.MathUtil;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.SubscriptionSender;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowHandler;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.validation.ModelValidator;
import com.liferay.portal.validation.ModelValidatorRegistryUtil;
import com.liferay.ratings.kernel.service.RatingsStatsLocalService;
import com.liferay.social.kernel.model.SocialActivityConstants;
import com.liferay.subscription.service.SubscriptionLocalService;
import com.liferay.trash.TrashHelper;
import com.liferay.trash.exception.RestoreEntryException;
import com.liferay.trash.exception.TrashEntryException;
import com.liferay.trash.model.TrashEntry;
import com.liferay.trash.model.TrashVersion;
import com.liferay.trash.service.TrashEntryLocalService;
import com.liferay.trash.service.TrashVersionLocalService;
import com.liferay.upload.AttachmentContentUpdater;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * Provides the local service for accessing, adding, deleting, and updating web
 * content articles.
 *
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 * @author Bruno Farache
 * @author Juan Fernández
 * @author Sergio González
 */
@Component(
	property = "model.class.name=com.liferay.journal.model.JournalArticle",
	service = AopService.class
)
public class JournalArticleLocalServiceImpl
	extends JournalArticleLocalServiceBaseImpl {

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
	 * @param  externalReferenceCode the external reference code of the web
	 *         content article
	 * @param  userId the primary key of the web content article's creator/owner
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
	 * @param  version the web content article's version
	 * @param  titleMap the web content article's locales and localized titles
	 * @param  descriptionMap the web content article's locales and localized
	 *         descriptions
	 * @param  friendlyURLMap the web content article's locales and localized
	 *         friendly URLs
	 * @param  content the HTML content wrapped in XML
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
	 * @param  smallImageFile the web content article's small image file
	 * @param  images the web content's images
	 * @param  articleURL the web content article's accessible URL
	 * @param  serviceContext the service context to be applied. Can set the
	 *         UUID, creation date, modification date, expando bridge
	 *         attributes, guest permissions, group permissions, asset category
	 *         IDs, asset tag names, asset link entry IDs, URL title, and
	 *         workflow actions for the web content article. Can also set
	 *         whether to add the default guest and group permissions.
	 * @return the web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public JournalArticle addArticle(
			String externalReferenceCode, long userId, long groupId,
			long folderId, long classNameId, long classPK, String articleId,
			boolean autoArticleId, double version, Map<Locale, String> titleMap,
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
			File smallImageFile, Map<String, byte[]> images, String articleURL,
			ServiceContext serviceContext)
		throws PortalException {

		// Article

		User user = _userLocalService.getUser(userId);

		articleId = StringUtil.toUpperCase(StringUtil.trim(articleId));

		Date displayDate = _portal.getDate(
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, user.getTimeZone(), null);

		if (displayDate == null) {
			Calendar calendar = CalendarFactoryUtil.getCalendar(
				user.getTimeZone());

			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			displayDate = calendar.getTime();
		}

		Date expirationDate = null;
		Date reviewDate = null;

		if (!neverExpire) {
			expirationDate = _portal.getDate(
				expirationDateMonth, expirationDateDay, expirationDateYear,
				expirationDateHour, expirationDateMinute, user.getTimeZone(),
				ArticleExpirationDateException.class);
		}

		if (!neverReview) {
			reviewDate = _portal.getDate(
				reviewDateMonth, reviewDateDay, reviewDateYear, reviewDateHour,
				reviewDateMinute, user.getTimeZone(),
				ArticleReviewDateException.class);
		}

		byte[] smallImageBytes = null;

		try {
			smallImageBytes = FileUtil.getBytes(smallImageFile);
		}
		catch (IOException ioException) {
			if (_log.isDebugEnabled()) {
				_log.debug(ioException);
			}
		}

		DDMStructure ddmStructure = _ddmStructureLocalService.getStructure(
			ddmStructureId);

		boolean validate = !ExportImportThreadLocal.isImportInProcess();

		if (validate) {
			validateDDMStructureId(
				groupId, folderId, ddmStructure.getStructureId());
		}

		if (autoArticleId) {
			articleId = String.valueOf(counterLocalService.increment());
		}

		sanitize(user.getCompanyId(), groupId, userId, classPK, descriptionMap);

		if (validate) {
			validate(
				externalReferenceCode, user.getCompanyId(), groupId,
				classNameId, articleId, autoArticleId, version, titleMap,
				content, ddmStructure.getStructureId(), ddmTemplateKey,
				displayDate, expirationDate, smallImage, smallImageURL,
				smallImageFile, smallImageBytes, serviceContext);

			try {
				validateReferences(
					groupId, folderId, ddmStructure.getStructureId(),
					ddmTemplateKey, layoutUuid, smallImage, smallImageURL,
					smallImageBytes, smallImageId, smallImageSource, content);
			}
			catch (ExportImportContentValidationException
						exportImportContentValidationException) {

				exportImportContentValidationException.setStagedModelClassName(
					JournalArticle.class.getName());
				exportImportContentValidationException.
					setStagedModelPrimaryKeyObj(articleId);

				throw exportImportContentValidationException;
			}
		}

		serviceContext.setAttribute("articleId", articleId);

		long id = counterLocalService.increment();

		String articleResourceUuid = GetterUtil.getString(
			serviceContext.getAttribute("articleResourceUuid"));

		long resourcePrimKey =
			_journalArticleResourceLocalService.getArticleResourcePrimKey(
				articleResourceUuid, groupId, articleId);

		JournalArticle article = journalArticlePersistence.create(id);

		Locale locale = getArticleDefaultLocale(content);

		friendlyURLMap = _checkFriendlyURLMap(locale, friendlyURLMap, titleMap);

		Map<String, String> urlTitleMap = _getURLTitleMap(
			groupId, resourcePrimKey, friendlyURLMap, titleMap);

		article.setUuid(serviceContext.getUuid());
		article.setResourcePrimKey(resourcePrimKey);
		article.setGroupId(groupId);
		article.setCompanyId(user.getCompanyId());
		article.setUserId(user.getUserId());
		article.setUserName(user.getFullName());
		article.setExternalReferenceCode(externalReferenceCode);
		article.setFolderId(folderId);
		article.setClassNameId(classNameId);
		article.setClassPK(classPK);
		article.setTreePath(article.buildTreePath());
		article.setArticleId(articleId);
		article.setVersion(version);
		article.setUrlTitle(urlTitleMap.get(LocaleUtil.toLanguageId(locale)));
		article.setDDMStructureId(ddmStructure.getStructureId());
		article.setDDMTemplateKey(ddmTemplateKey);
		article.setDefaultLanguageId(LocaleUtil.toLanguageId(locale));
		article.setLayoutUuid(layoutUuid);
		article.setDisplayDate(displayDate);
		article.setExpirationDate(expirationDate);
		article.setReviewDate(reviewDate);
		article.setIndexable(indexable);
		article.setSmallImage(smallImage);

		if (smallImage) {
			if (smallImageSource ==
					JournalArticleConstants.
						SMALL_IMAGE_SOURCE_DOCUMENTS_AND_MEDIA) {

				article.setSmallImageId(smallImageId);
			}
			else {
				article.setSmallImageId(counterLocalService.increment());
			}
		}
		else {
			article.setSmallImageId(0);
		}

		if (smallImageSource <= 0) {
			smallImageSource = _getSmallImageSource(smallImage, smallImageURL);
		}

		article.setSmallImageSource(smallImageSource);

		article.setSmallImageURL(smallImageURL);

		Date date = new Date();

		if ((expirationDate == null) || expirationDate.after(date)) {
			article.setStatus(WorkflowConstants.STATUS_DRAFT);
		}
		else {
			article.setStatus(WorkflowConstants.STATUS_EXPIRED);
		}

		article.setStatusByUserId(userId);
		article.setStatusByUserName(user.getFullName());
		article.setStatusDate(serviceContext.getModifiedDate(date));
		article.setExpandoBridgeAttributes(serviceContext);

		article = journalArticlePersistence.update(article);

		// Friendly URLs

		updateFriendlyURLs(article, urlTitleMap, serviceContext);

		// Article localization

		_addArticleLocalizedFields(
			user.getCompanyId(), article.getId(), titleMap, descriptionMap);

		// Resources

		if (serviceContext.isAddGroupPermissions() ||
			serviceContext.isAddGuestPermissions()) {

			addArticleResources(
				article, serviceContext.isAddGroupPermissions(),
				serviceContext.isAddGuestPermissions());
		}
		else {
			addArticleResources(article, serviceContext.getModelPermissions());
		}

		// Small image

		saveImages(
			article.getCompanyId(), smallImage, article.getSmallImageId(),
			smallImageFile, smallImageBytes);

		// Asset

		updateAsset(
			userId, article, serviceContext.getAssetCategoryIds(),
			serviceContext.getAssetTagNames(),
			serviceContext.getAssetLinkEntryIds(),
			serviceContext.getAssetPriority());

		_assetDisplayPageEntryFormProcessor.process(
			JournalArticle.class.getName(), article.getResourcePrimKey(),
			serviceContext);

		// Dynamic data mapping

		updateDDMFields(article, content, groupId, user);

		if (_classNameLocalService.getClassNameId(DDMStructure.class) !=
				classNameId) {

			updateDDMLinks(
				id, groupId, article.getDDMStructureId(), ddmTemplateKey, true);
		}

		// Email

		articleURL = buildArticleURL(articleURL, groupId, folderId, articleId);

		serviceContext.setAttribute("articleURL", articleURL);

		sendEmail(article, articleURL, "requested", serviceContext);

		// Workflow

		startWorkflowInstance(userId, article, serviceContext);

		return article;
	}

	/**
	 * Adds a web content article.
	 *
	 * @param  externalReferenceCode the external reference code of the web
	 *         content article.
	 * @param  userId the primary key of the web content article's creator/owner
	 * @param  groupId the primary key of the web content article's group
	 * @param  folderId the primary key of the web content article folder
	 * @param  titleMap the web content article's locales and localized titles
	 * @param  descriptionMap the web content article's locales and localized
	 *         descriptions
	 * @param  content the HTML content wrapped in XML. For more information,
	 *         see the content example in the {@link #addArticle(String, long,
	 *         long, long, long, long, String, boolean, double, Map, Map, Map,
	 *         String, long, String, String, int, int, int, int, int, int, int,
	 *         int, int, int, boolean, int, int, int, int, int, boolean,
	 *         boolean, boolean, long, int, String, File, Map, String,
	 *         ServiceContext)} description.
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
			String externalReferenceCode, long userId, long groupId,
			long folderId, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, String content,
			long ddmStructureId, String ddmTemplateKey,
			ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		Calendar calendar = CalendarFactoryUtil.getCalendar(user.getTimeZone());

		int displayDateMonth = calendar.get(Calendar.MONTH);
		int displayDateDay = calendar.get(Calendar.DAY_OF_MONTH);
		int displayDateYear = calendar.get(Calendar.YEAR);
		int displayDateHour = calendar.get(Calendar.HOUR_OF_DAY);
		int displayDateMinute = calendar.get(Calendar.MINUTE);

		return journalArticleLocalService.addArticle(
			externalReferenceCode, userId, groupId, folderId,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, 0, StringPool.BLANK,
			true, 1, titleMap, descriptionMap, titleMap, content,
			ddmStructureId, ddmTemplateKey, null, displayDateMonth,
			displayDateDay, displayDateYear, displayDateHour, displayDateMinute,
			0, 0, 0, 0, 0, true, 0, 0, 0, 0, 0, true, true, false, 0, 0, null,
			null, null, null, serviceContext);
	}

	@Override
	public JournalArticle addArticleDefaultValues(
			long userId, long groupId, long classNameId, long classPK,
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

		// Article

		User user = _userLocalService.getUser(userId);

		Date displayDate = _portal.getDate(
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, user.getTimeZone(), null);

		if ((displayDateMonth == displayDateDay) &&
			(displayDateDay == displayDateYear) && (displayDateYear == 0)) {

			displayDate = null;
		}

		Date expirationDate = null;
		Date reviewDate = null;

		if (!neverExpire) {
			expirationDate = _portal.getDate(
				expirationDateMonth, expirationDateDay, expirationDateYear,
				expirationDateHour, expirationDateMinute, user.getTimeZone(),
				ArticleExpirationDateException.class);
		}

		if (!neverReview) {
			reviewDate = _portal.getDate(
				reviewDateMonth, reviewDateDay, reviewDateYear, reviewDateHour,
				reviewDateMinute, user.getTimeZone(),
				ArticleReviewDateException.class);
		}

		byte[] smallImageBytes = null;

		try {
			smallImageBytes = FileUtil.getBytes(smallImageFile);
		}
		catch (IOException ioException) {
			if (_log.isDebugEnabled()) {
				_log.debug(ioException);
			}
		}

		String articleId = String.valueOf(counterLocalService.increment());

		String externalReferenceCode = articleId;

		sanitize(user.getCompanyId(), groupId, userId, classPK, descriptionMap);

		DDMStructure ddmStructure = _ddmStructureLocalService.getStructure(
			ddmStructureId);

		validate(
			externalReferenceCode, user.getCompanyId(), groupId, classNameId,
			articleId, true, 0, titleMap, content,
			ddmStructure.getStructureId(), ddmTemplateKey, displayDate,
			expirationDate, smallImage, smallImageURL, smallImageFile,
			smallImageBytes, serviceContext);

		serviceContext.setAttribute("articleId", articleId);

		long id = counterLocalService.increment();

		JournalArticle article = journalArticlePersistence.create(id);

		article.setUuid(serviceContext.getUuid());

		String articleResourceUuid = GetterUtil.getString(
			serviceContext.getAttribute("articleResourceUuid"));

		article.setResourcePrimKey(
			_journalArticleResourceLocalService.getArticleResourcePrimKey(
				articleResourceUuid, groupId, articleId));

		article.setGroupId(groupId);
		article.setCompanyId(user.getCompanyId());
		article.setUserId(user.getUserId());
		article.setUserName(user.getFullName());
		article.setExternalReferenceCode(externalReferenceCode);
		article.setClassNameId(classNameId);
		article.setClassPK(classPK);
		article.setArticleId(articleId);
		article.setDDMStructureId(ddmStructure.getStructureId());
		article.setDDMTemplateKey(ddmTemplateKey);

		Locale locale = getArticleDefaultLocale(content);

		article.setDefaultLanguageId(LocaleUtil.toLanguageId(locale));

		article.setLayoutUuid(layoutUuid);
		article.setDisplayDate(displayDate);
		article.setExpirationDate(expirationDate);
		article.setReviewDate(reviewDate);
		article.setIndexable(indexable);
		article.setSmallImage(smallImage);

		if (smallImageSource ==
				JournalArticleConstants.
					SMALL_IMAGE_SOURCE_DOCUMENTS_AND_MEDIA) {

			article.setSmallImageId(smallImageId);
		}
		else {
			article.setSmallImageId(counterLocalService.increment());
		}

		if (smallImageSource <= 0) {
			smallImageSource = _getSmallImageSource(smallImage, smallImageURL);
		}

		article.setSmallImageSource(smallImageSource);

		article.setSmallImageURL(smallImageURL);
		article.setStatus(WorkflowConstants.STATUS_APPROVED);
		article.setStatusByUserId(userId);
		article.setStatusDate(serviceContext.getModifiedDate(new Date()));
		article.setExpandoBridgeAttributes(serviceContext);

		article = journalArticlePersistence.update(article);

		// Article localization

		_addArticleLocalizedFields(
			user.getCompanyId(), article.getId(), titleMap, descriptionMap);

		// Resources

		addArticleResources(article, serviceContext.getModelPermissions());

		// Small image

		saveImages(
			article.getCompanyId(), smallImage, article.getSmallImageId(),
			smallImageFile, smallImageBytes);

		// Asset

		updateAsset(
			userId, article, serviceContext.getAssetCategoryIds(),
			serviceContext.getAssetTagNames(),
			serviceContext.getAssetLinkEntryIds(),
			serviceContext.getAssetPriority());

		// Dynamic data mapping

		DDMFormValues ddmFormValues = updateDDMFields(
			article, content, groupId, user);

		_updateDDMStructurePredefinedValues(
			userId, article.getDDMStructureId(), ddmFormValues, serviceContext);

		return journalArticlePersistence.findByPrimaryKey(article.getId());
	}

	/**
	 * Adds the resources to the web content article.
	 *
	 * @param  article the web content article
	 * @param  addGroupPermissions whether to add group permissions
	 * @param  addGuestPermissions whether to add guest permissions
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void addArticleResources(
			JournalArticle article, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		_resourceLocalService.addResources(
			article.getCompanyId(), article.getGroupId(), article.getUserId(),
			JournalArticle.class.getName(), article.getResourcePrimKey(), false,
			addGroupPermissions, addGuestPermissions);
	}

	/**
	 * Adds the model resources with the permissions to the web content article.
	 *
	 * @param  article the web content article to add resources to
	 * @param  modelPermissions the model permissions
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void addArticleResources(
			JournalArticle article, ModelPermissions modelPermissions)
		throws PortalException {

		_resourceLocalService.addModelResources(
			article.getCompanyId(), article.getGroupId(), article.getUserId(),
			JournalArticle.class.getName(), article.getResourcePrimKey(),
			modelPermissions);
	}

	/**
	 * Adds the resources to the most recently created web content article.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  articleId the primary key of the web content article
	 * @param  addGroupPermissions whether to add group permissions
	 * @param  addGuestPermissions whether to add guest permissions
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void addArticleResources(
			long groupId, String articleId, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		JournalArticle article = getLatestArticle(groupId, articleId);

		addArticleResources(article, addGroupPermissions, addGuestPermissions);
	}

	/**
	 * Returns the web content article with the group, article ID, and version.
	 * This method checks for the article's resource primary key and, if not
	 * found, creates a new one.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  articleId the primary key of the web content article
	 * @param  version the web content article's version
	 * @return the matching web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle checkArticleResourcePrimKey(
			long groupId, String articleId, double version)
		throws PortalException {

		JournalArticle article = journalArticlePersistence.findByG_A_V(
			groupId, articleId, version);

		if (article.getResourcePrimKey() > 0) {
			return article;
		}

		article.setResourcePrimKey(
			_journalArticleResourceLocalService.getArticleResourcePrimKey(
				groupId, articleId));

		return journalArticlePersistence.update(article);
	}

	/**
	 * Checks all web content articles by handling their expirations and sending
	 * review notifications based on their current workflow.
	 */
	@Override
	public void checkArticles(long companyId) throws PortalException {
		Date date = new Date();

		long checkInterval = getArticleCheckInterval(companyId);

		checkArticlesByExpirationDate(companyId, date, checkInterval);

		checkArticlesByReviewDate(companyId, date);

		checkArticlesByDisplayDate(date, checkInterval);

		_companyIdPreviousCheckDate.put(companyId, date);
	}

	/**
	 * Copies the web content article matching the group, article ID, and
	 * version. This method creates a new article, extracting all the values
	 * from the old one and updating its article ID.
	 *
	 * @param  userId the primary key of the web content article's creator/owner
	 * @param  groupId the primary key of the web content article's group
	 * @param  sourceArticleId the primary key of the old web content article
	 * @param  targetArticleId the primary key of the new web content article
	 * @param  autoArticleId whether to auto-generate the web content article ID
	 * @param  version the web content article's version
	 * @return the new web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public JournalArticle copyArticle(
			long userId, long groupId, String sourceArticleId,
			String targetArticleId, boolean autoArticleId, double version)
		throws PortalException {

		if (autoArticleId) {
			targetArticleId = String.valueOf(counterLocalService.increment());
		}
		else {
			validate(targetArticleId);

			if (journalArticlePersistence.countByG_A(groupId, targetArticleId) >
					0) {

				throw new DuplicateArticleIdException(
					StringBundler.concat(
						"{groupId=", groupId, ", articleId=", targetArticleId,
						"}"));
			}
		}

		return _copyArticle(
			userId, groupId, sourceArticleId, targetArticleId, version, true);
	}

	/**
	 * Deletes the web content article and its resources.
	 *
	 * @param  article the web content article
	 * @return the deleted web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP, send = false,
		type = SystemEventConstants.TYPE_DELETE
	)
	public JournalArticle deleteArticle(JournalArticle article)
		throws PortalException {

		return journalArticleLocalService.deleteArticle(
			article, StringPool.BLANK, null);
	}

	/**
	 * Deletes the web content article and its resources, optionally sending
	 * email notifying denial of the article if it had not yet been approved.
	 *
	 * @param  article the web content article
	 * @param  articleURL the web content article's accessible URL to include in
	 *         email notifications (optionally <code>null</code>)
	 * @param  serviceContext the service context to be applied (optionally
	 *         <code>null</code>). Can set the portlet preferences that include
	 *         email information to notify recipients of the unapproved web
	 *         content's denial.
	 * @return the deleted web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP, send = false,
		type = SystemEventConstants.TYPE_DELETE
	)
	public JournalArticle deleteArticle(
			JournalArticle article, String articleURL,
			ServiceContext serviceContext)
		throws PortalException {

		JournalArticleResource articleResource =
			_journalArticleResourceLocalService.fetchArticleResource(
				article.getGroupId(), article.getArticleId());

		if (article.isApproved() &&
			isLatestVersion(
				article.getGroupId(), article.getArticleId(),
				article.getVersion(), WorkflowConstants.STATUS_APPROVED) &&
			!GroupThreadLocal.isDeleteInProcess()) {

			updatePreviousApprovedArticle(article);
		}

		// Article localization

		_journalArticleLocalizationPersistence.removeByC_A(
			article.getCompanyId(), article.getId());

		// Asset

		if (article.isDraft() || article.isInTrash()) {
			_assetEntryLocalService.deleteEntry(
				JournalArticle.class.getName(), article.getPrimaryKey());
		}

		// Email

		if ((serviceContext != null) && Validator.isNotNull(articleURL) &&
			!article.isApproved() &&
			isLatestVersion(
				article.getGroupId(), article.getArticleId(),
				article.getVersion()) &&
			!GroupThreadLocal.isDeleteInProcess()) {

			articleURL = buildArticleURL(
				articleURL, article.getGroupId(), article.getFolderId(),
				article.getArticleId());

			sendEmail(article, articleURL, "denied", serviceContext);
		}

		// Dynamic data mapping

		_ddmFieldLocalService.deleteDDMFormValues(article.getId());

		if (article.getClassNameId() != _classNameLocalService.getClassNameId(
				DDMStructure.class)) {

			ddmStorageLinkLocalService.deleteClassStorageLink(article.getId());

			ddmStructureLinkLocalService.deleteStructureLinks(
				_classNameLocalService.getClassNameId(JournalArticle.class),
				article.getId());

			ddmTemplateLinkLocalService.deleteTemplateLink(
				_classNameLocalService.getClassNameId(JournalArticle.class),
				article.getId());

			String compositeClassName =
				ResourceActionsUtil.getCompositeModelName(
					JournalArticle.class.getName(),
					DDMTemplate.class.getName());

			ddmTemplateLinkLocalService.deleteTemplateLink(
				_classNameLocalService.getClassNameId(compositeClassName),
				article.getId());
		}

		// Expando

		_expandoRowLocalService.deleteRows(
			article.getCompanyId(),
			_classNameLocalService.getClassNameId(JournalArticle.class),
			article.getId());

		// Trash

		if (article.isInTrash() &&
			(_trashHelper.getTrashEntry(article) != null)) {

			_trashVersionLocalService.deleteTrashVersion(
				JournalArticle.class.getName(), article.getId());
		}

		// Workflow

		if (!article.isDraft()) {
			_workflowInstanceLinkLocalService.deleteWorkflowInstanceLink(
				article.getCompanyId(), article.getGroupId(),
				JournalArticle.class.getName(), article.getId());
		}

		int articlesCount = journalArticlePersistence.countByG_A(
			article.getGroupId(), article.getArticleId());

		if (articlesCount == 1) {

			// Asset

			_assetEntryLocalService.deleteEntry(
				JournalArticle.class.getName(), article.getResourcePrimKey());

			// Comment

			_commentManager.deleteDiscussion(
				JournalArticle.class.getName(), article.getResourcePrimKey());

			// Friendly URL

			List<FriendlyURLEntry> friendlyURLEntries =
				friendlyURLEntryLocalService.getFriendlyURLEntries(
					article.getGroupId(),
					_classNameLocalService.getClassNameId(JournalArticle.class),
					article.getResourcePrimKey());

			if (!friendlyURLEntries.isEmpty()) {
				friendlyURLEntryLocalService.deleteFriendlyURLEntry(
					article.getGroupId(), JournalArticle.class,
					article.getResourcePrimKey());
			}

			// Images

			long folderId = article.getImagesFolderId();

			if (folderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
				_portletFileRepository.deletePortletFolder(folderId);
			}

			// Ratings

			_ratingsStatsLocalService.deleteStats(
				JournalArticle.class.getName(), article.getResourcePrimKey());

			// Small image

			_imageLocalService.deleteImage(article.getSmallImageId());

			// Trash

			_trashEntryLocalService.deleteEntry(
				JournalArticle.class.getName(), article.getResourcePrimKey());

			// Resources

			_resourceLocalService.deleteResource(
				article.getCompanyId(), JournalArticle.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				article.getResourcePrimKey());

			// Resource

			if (articleResource != null) {
				_journalArticleResourceLocalService.
					deleteJournalArticleResource(articleResource);
			}
		}

		// Article

		journalArticlePersistence.remove(article);

		// System event

		if (articleResource != null) {
			_systemEventLocalService.addSystemEvent(
				0, article.getGroupId(), StringPool.BLANK,
				article.getModelClassName(), article.getPrimaryKey(),
				articleResource.getUuid(), null,
				SystemEventConstants.TYPE_DELETE,
				JSONUtil.put(
					"assetTitle",
					article.getTitle(article.getDefaultLanguageId())
				).put(
					"uuid", article.getUuid()
				).put(
					"version", article.getVersion()
				).toString());
		}

		return article;
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
	 * @return the deleted web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle deleteArticle(
			long groupId, String articleId, double version, String articleURL,
			ServiceContext serviceContext)
		throws PortalException {

		JournalArticle article = journalArticlePersistence.findByG_A_V(
			groupId, articleId, version);

		return journalArticleLocalService.deleteArticle(
			article, articleURL, serviceContext);
	}

	/**
	 * Deletes all web content articles and their resources matching the group
	 * and article ID, optionally sending email notifying denial of article if
	 * it had not yet been approved.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  articleId the primary key of the web content article
	 * @param  serviceContext the service context to be applied. Can set the
	 *         portlet preferences that include email information to notify
	 *         recipients of the unapproved web content article's denial.
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void deleteArticle(
			long groupId, String articleId, ServiceContext serviceContext)
		throws PortalException {

		SystemEventHierarchyEntryThreadLocal.push(JournalArticle.class);

		JournalArticleResource articleResource =
			_journalArticleResourceLocalService.fetchArticleResource(
				groupId, articleId);

		String assetTitle = StringPool.BLANK;

		try {
			List<JournalArticle> articles = journalArticlePersistence.findByG_A(
				groupId, articleId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				ArticleVersionComparator.getInstance(true));

			for (JournalArticle article : articles) {
				assetTitle = article.getTitle(article.getDefaultLanguageId());

				journalArticleLocalService.deleteArticle(
					article, null, serviceContext);
			}
		}
		finally {
			SystemEventHierarchyEntryThreadLocal.pop(JournalArticle.class);
		}

		if (articleResource != null) {
			_systemEventLocalService.addSystemEvent(
				0, groupId, StringPool.BLANK, JournalArticle.class.getName(),
				articleResource.getResourcePrimKey(), articleResource.getUuid(),
				null, SystemEventConstants.TYPE_DELETE,
				JSONUtil.put(
					"assetTitle", assetTitle
				).toString());
		}
	}

	@Override
	public void deleteArticleDefaultValues(
			long groupId, String articleId, long ddmStructureId)
		throws PortalException {

		// Dynamic data mapping

		_deleteDDMStructurePredefinedValues(ddmStructureId);

		journalArticleLocalService.deleteArticle(groupId, articleId, null);
	}

	/**
	 * Deletes all the group's web content articles and resources.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void deleteArticles(long groupId) throws PortalException {
		SystemEventHierarchyEntryThreadLocal.push(JournalArticle.class);

		List<JournalArticleResource> articleResources = new ArrayList<>();

		try {
			JournalArticleResource articleResource = null;

			for (JournalArticle article :
					journalArticlePersistence.findByGroupId(groupId)) {

				if ((articleResource == null) ||
					(articleResource.getPrimaryKey() !=
						article.getResourcePrimKey())) {

					articleResources.add(
						_journalArticleResourceLocalService.getArticleResource(
							article.getResourcePrimKey()));
				}

				journalArticleLocalService.deleteArticle(article, null, null);
			}
		}
		finally {
			SystemEventHierarchyEntryThreadLocal.pop(JournalArticle.class);
		}

		for (JournalArticleResource articleResource : articleResources) {
			_systemEventLocalService.addSystemEvent(
				0, groupId, StringPool.BLANK, JournalArticle.class.getName(),
				articleResource.getResourcePrimKey(), articleResource.getUuid(),
				null, SystemEventConstants.TYPE_DELETE, StringPool.BLANK);
		}
	}

	/**
	 * Deletes all the group's web content articles and resources in the folder,
	 * including recycled articles.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  folderId the primary key of the web content article folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void deleteArticles(long groupId, long folderId)
		throws PortalException {

		deleteArticles(groupId, folderId, true);
	}

	/**
	 * Deletes all the group's web content articles and resources in the folder,
	 * optionally including recycled articles.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  folderId the primary key of the web content article folder
	 * @param  includeTrashedEntries whether to include recycled web content
	 *         articles
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void deleteArticles(
			long groupId, long folderId, boolean includeTrashedEntries)
		throws PortalException {

		SystemEventHierarchyEntryThreadLocal.push(JournalArticle.class);

		List<JournalArticleResource> articleResources = new ArrayList<>();

		try {
			JournalArticleResource articleResource = null;

			for (JournalArticle article :
					journalArticlePersistence.findByG_F(groupId, folderId)) {

				if ((articleResource == null) ||
					(articleResource.getPrimaryKey() !=
						article.getResourcePrimKey())) {

					articleResource =
						_journalArticleResourceLocalService.getArticleResource(
							article.getResourcePrimKey());

					articleResources.add(articleResource);
				}

				if (includeTrashedEntries ||
					!_trashHelper.isInTrashExplicitly(article)) {

					journalArticleLocalService.deleteArticle(
						article, null, null);
				}
				else {
					articleResources.remove(articleResource);
				}
			}
		}
		finally {
			SystemEventHierarchyEntryThreadLocal.pop(JournalArticle.class);
		}

		for (JournalArticleResource articleResource : articleResources) {
			_systemEventLocalService.addSystemEvent(
				0, groupId, StringPool.BLANK, JournalArticle.class.getName(),
				articleResource.getResourcePrimKey(), articleResource.getUuid(),
				null, SystemEventConstants.TYPE_DELETE, StringPool.BLANK);
		}
	}

	/**
	 * Deletes all the group's web content articles and resources matching the
	 * class name and class primary key.
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
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void deleteArticles(long groupId, String className, long classPK)
		throws PortalException {

		List<JournalArticle> articles = journalArticlePersistence.findByG_C_C(
			groupId, _classNameLocalService.getClassNameId(className), classPK);

		for (JournalArticle article : articles) {
			journalArticleLocalService.deleteArticle(article, null, null);
		}
	}

	/**
	 * Deletes the layout's association with the web content articles for the
	 * group.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param layoutUuid the unique string identifying the web content article's
	 *        display page
	 */
	@Override
	public void deleteLayoutArticleReferences(long groupId, String layoutUuid) {
		List<JournalArticle> articles = journalArticlePersistence.findByG_L(
			groupId, layoutUuid);

		for (JournalArticle article : articles) {
			article.setLayoutUuid(StringPool.BLANK);

			journalArticlePersistence.update(article);
		}
	}

	/**
	 * Expires the web content article matching the group, article ID, and
	 * version.
	 *
	 * @param  userId the primary key of the user updating the web content
	 *         article
	 * @param  groupId the primary key of the web content article's group
	 * @param  articleId the primary key of the web content article
	 * @param  version the web content article's version
	 * @param  articleURL the web content article's accessible URL
	 * @param  serviceContext the service context to be applied. Can set the
	 *         modification date, status date, portlet preferences, and can set
	 *         whether to add the default command update for the web content
	 *         article. With respect to social activities, by setting the
	 *         service context's command to {@link Constants#UPDATE}, the
	 *         invocation is considered a web content update activity; otherwise
	 *         it is considered a web content add activity.
	 * @return the web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public JournalArticle expireArticle(
			long userId, long groupId, String articleId, double version,
			String articleURL, ServiceContext serviceContext)
		throws PortalException {

		return updateStatus(
			userId, groupId, articleId, version,
			WorkflowConstants.STATUS_EXPIRED, articleURL, new HashMap<>(),
			serviceContext);
	}

	/**
	 * Expires the web content article matching the group and article ID,
	 * expiring all of its versions if the
	 * <code>journal.article.expire.all.versions</code> portal property is
	 * <code>true</code>, otherwise expiring only its latest approved version.
	 *
	 * @param  userId the primary key of the user updating the web content
	 *         article
	 * @param  groupId the primary key of the web content article's group
	 * @param  articleId the primary key of the web content article
	 * @param  articleURL the web content article's accessible URL
	 * @param  serviceContext the service context to be applied. Can set the
	 *         modification date, status date, portlet preferences, and can set
	 *         whether to add the default command update for the web content
	 *         article. With respect to social activities, by setting the
	 *         service context's command to {@link Constants#UPDATE}, the
	 *         invocation is considered a web content update activity; otherwise
	 *         it is considered a web content add activity.
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void expireArticle(
			long userId, long groupId, String articleId, String articleURL,
			ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		if (isExpireAllArticleVersions(user.getCompanyId())) {
			List<JournalArticle> articles = journalArticlePersistence.findByG_A(
				groupId, articleId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				ArticleVersionComparator.getInstance(true));

			for (JournalArticle article : articles) {
				if (!article.isExpired()) {
					journalArticleLocalService.expireArticle(
						userId, groupId, article.getArticleId(),
						article.getVersion(), articleURL, serviceContext);
				}
			}
		}
		else {
			JournalArticle article = getLatestArticle(
				groupId, articleId, WorkflowConstants.STATUS_APPROVED);

			journalArticleLocalService.expireArticle(
				userId, groupId, article.getArticleId(), article.getVersion(),
				articleURL, serviceContext);
		}
	}

	/**
	 * Returns the web content article with the ID.
	 *
	 * @param  id the primary key of the web content article
	 * @return the web content article with the ID
	 */
	@Override
	public JournalArticle fetchArticle(long id) {
		return journalArticlePersistence.fetchByPrimaryKey(id);
	}

	@Override
	public JournalArticle fetchArticle(long groupId, String articleId) {

		// Get the latest article that is approved, if none are approved, get
		// the latest unapproved article

		JournalArticle article = fetchLatestArticle(
			groupId, articleId, WorkflowConstants.STATUS_APPROVED);

		if (article != null) {
			return article;
		}

		return fetchLatestArticle(
			groupId, articleId, WorkflowConstants.STATUS_ANY);
	}

	/**
	 * Returns the web content article matching the group, article ID, and
	 * version.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  articleId the primary key of the web content article
	 * @param  version the web content article's version
	 * @return the web content article matching the group, article ID, and
	 *         version, or <code>null</code> if no web content article could be
	 *         found
	 */
	@Override
	public JournalArticle fetchArticle(
		long groupId, String articleId, double version) {

		return journalArticlePersistence.fetchByG_A_V(
			groupId, articleId, version);
	}

	@Override
	public JournalArticle fetchArticleByUrlTitle(
		long groupId, String urlTitle) {

		JournalArticle article = fetchLatestArticleByUrlTitle(
			groupId, urlTitle, WorkflowConstants.STATUS_APPROVED);

		if (article != null) {
			return article;
		}

		return fetchLatestArticleByUrlTitle(
			groupId, urlTitle, WorkflowConstants.STATUS_ANY);
	}

	@Override
	public JournalArticle fetchArticleByUrlTitle(
		long groupId, String urlTitle, double version) {

		FriendlyURLEntry friendlyURLEntry =
			friendlyURLEntryLocalService.fetchFriendlyURLEntry(
				groupId, JournalArticle.class, urlTitle);

		if (friendlyURLEntry != null) {
			JournalArticle article = fetchLatestArticle(
				friendlyURLEntry.getClassPK(), WorkflowConstants.STATUS_ANY,
				true);

			if (article.getVersion() == version) {
				return article;
			}
		}

		JournalArticle article = fetchLatestArticleByUrlTitle(
			groupId, urlTitle, WorkflowConstants.STATUS_APPROVED);

		if ((article != null) && (article.getVersion() == version)) {
			return article;
		}

		article = fetchLatestArticleByUrlTitle(
			groupId, urlTitle, WorkflowConstants.STATUS_ANY);

		if ((article != null) && (article.getVersion() == version)) {
			return article;
		}
		else if (article != null) {
			return fetchArticle(
				article.getGroupId(), article.getArticleId(), version);
		}

		return null;
	}

	@Override
	public JournalArticle fetchDisplayArticle(long groupId, String articleId) {
		List<JournalArticle> articles = journalArticlePersistence.findByG_A_ST(
			groupId, articleId, WorkflowConstants.STATUS_APPROVED);

		if (articles.isEmpty()) {
			return null;
		}

		Date date = new Date();

		for (JournalArticle article : articles) {
			Date displayDate = article.getDisplayDate();
			Date expirationDate = article.getExpirationDate();

			if (((displayDate == null) || displayDate.before(date)) &&
				((expirationDate == null) || expirationDate.after(date))) {

				return article;
			}
		}

		return articles.get(0);
	}

	@Override
	public JournalArticle fetchLatestArticle(long resourcePrimKey) {
		return fetchLatestArticle(
			resourcePrimKey, WorkflowConstants.STATUS_ANY);
	}

	@Override
	public JournalArticle fetchLatestArticle(long resourcePrimKey, int status) {
		return fetchLatestArticle(resourcePrimKey, status, true);
	}

	/**
	 * Returns the latest web content article matching the resource primary key
	 * and workflow status, optionally preferring articles with approved
	 * workflow status.
	 *
	 * @param  resourcePrimKey the primary key of the resource instance
	 * @param  status the web content article's workflow status. For more
	 *         information see {@link WorkflowConstants} for constants starting
	 *         with the "STATUS_" prefix.
	 * @param  preferApproved whether to prefer returning the latest matching
	 *         article that has workflow status {@link
	 *         WorkflowConstants#STATUS_APPROVED} over returning one that has a
	 *         different status
	 * @return the latest web content article matching the resource primary key
	 *         and workflow status, optionally preferring articles with an
	 *         approved workflow status, or <code>null</code> if no matching web
	 *         content article could be found
	 */
	@Override
	public JournalArticle fetchLatestArticle(
		long resourcePrimKey, int status, boolean preferApproved) {

		JournalArticle article = null;

		OrderByComparator<JournalArticle> orderByComparator =
			ArticleVersionComparator.getInstance(false);

		if (status == WorkflowConstants.STATUS_ANY) {
			if (preferApproved) {
				article = journalArticlePersistence.fetchByR_ST_First(
					resourcePrimKey, WorkflowConstants.STATUS_APPROVED,
					orderByComparator);
			}

			if (article == null) {
				article =
					journalArticlePersistence.fetchByResourcePrimKey_First(
						resourcePrimKey, orderByComparator);
			}
		}
		else {
			article = journalArticlePersistence.fetchByR_ST_First(
				resourcePrimKey, status, orderByComparator);
		}

		return article;
	}

	@Override
	public JournalArticle fetchLatestArticle(
		long resourcePrimKey, int[] statuses) {

		OrderByComparator<JournalArticle> orderByComparator =
			ArticleVersionComparator.getInstance(false);

		List<JournalArticle> articles = journalArticlePersistence.findByR_ST(
			resourcePrimKey, statuses, 0, 1, orderByComparator);

		if (!articles.isEmpty()) {
			return articles.get(0);
		}

		return null;
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
	 * @return the latest matching web content article, or <code>null</code> if
	 *         no matching web content article could be found
	 */
	@Override
	public JournalArticle fetchLatestArticle(
		long groupId, String articleId, int status) {

		OrderByComparator<JournalArticle> orderByComparator =
			ArticleVersionComparator.getInstance(false);

		if (status == WorkflowConstants.STATUS_ANY) {
			return journalArticlePersistence.fetchByG_A_NotST_First(
				groupId, articleId, WorkflowConstants.STATUS_IN_TRASH,
				orderByComparator);
		}

		return journalArticlePersistence.fetchByG_A_ST_First(
			groupId, articleId, status, orderByComparator);
	}

	/**
	 * Returns the latest web content article matching the group and the
	 * external reference code.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  externalReferenceCode the web content article's external
	 *         reference code
	 * @return the latest matching web content article, or <code>null</code> if
	 *         no matching web content article could be found
	 */
	@Override
	public JournalArticle fetchLatestArticleByExternalReferenceCode(
		long groupId, String externalReferenceCode) {

		OrderByComparator<JournalArticle> orderByComparator =
			ArticleVersionComparator.getInstance(false);

		return journalArticlePersistence.fetchByG_ERC_First(
			groupId, externalReferenceCode, orderByComparator);
	}

	@Override
	public JournalArticle fetchLatestArticleByExternalReferenceCode(
		long groupId, String externalReferenceCode, int status,
		boolean preferApproved) {

		JournalArticle article = null;

		OrderByComparator<JournalArticle> orderByComparator =
			ArticleVersionComparator.getInstance(false);

		if (status == WorkflowConstants.STATUS_ANY) {
			if (preferApproved) {
				article = journalArticlePersistence.fetchByG_ERC_ST_First(
					groupId, externalReferenceCode,
					WorkflowConstants.STATUS_APPROVED, orderByComparator);
			}

			if (article == null) {
				article = journalArticlePersistence.fetchByG_ERC_First(
					groupId, externalReferenceCode, orderByComparator);
			}
		}
		else {
			article = journalArticlePersistence.fetchByG_ERC_ST_First(
				groupId, externalReferenceCode, status, orderByComparator);
		}

		return article;
	}

	@Override
	public JournalArticle fetchLatestArticleByExternalReferenceCode(
		long groupId, String externalReferenceCode, int[] statuses) {

		OrderByComparator<JournalArticle> orderByComparator =
			ArticleVersionComparator.getInstance(false);

		List<JournalArticle> articles =
			journalArticlePersistence.findByG_ERC_ST(
				groupId, externalReferenceCode, statuses, 0, 1,
				orderByComparator);

		if (!articles.isEmpty()) {
			return articles.get(0);
		}

		return null;
	}

	@Override
	public JournalArticle fetchLatestArticleByUrlTitle(
		long groupId, String urlTitle, int status) {

		FriendlyURLEntry friendlyURLEntry =
			friendlyURLEntryLocalService.fetchFriendlyURLEntry(
				groupId, JournalArticle.class, urlTitle);

		if (friendlyURLEntry != null) {
			JournalArticle article = fetchLatestArticle(
				friendlyURLEntry.getClassPK(), status);

			if ((article != null) && (article.getGroupId() != groupId)) {
				article = fetchLatestArticle(
					groupId, article.getArticleId(), status);
			}

			return article;
		}

		List<JournalArticle> articles = null;

		OrderByComparator<JournalArticle> orderByComparator =
			ArticleVersionComparator.getInstance(false);

		if (status == WorkflowConstants.STATUS_ANY) {
			articles = journalArticlePersistence.findByG_UT(
				groupId, _friendlyURLNormalizer.normalizeWithEncoding(urlTitle),
				0, 1, orderByComparator);
		}
		else {
			articles = journalArticlePersistence.findByG_UT_ST(
				groupId, _friendlyURLNormalizer.normalizeWithEncoding(urlTitle),
				status, 0, 1, orderByComparator);
		}

		if (articles.isEmpty()) {
			return null;
		}

		return articles.get(0);
	}

	/**
	 * Returns the latest indexable web content article matching the resource
	 * primary key.
	 *
	 * @param  resourcePrimKey the primary key of the resource instance
	 * @return the latest indexable web content article matching the resource
	 *         primary key, or <code>null</code> if no matching web content
	 *         article could be found
	 */
	@Override
	public JournalArticle fetchLatestIndexableArticle(long resourcePrimKey) {
		OrderByComparator<JournalArticle> orderByComparator =
			ArticleVersionComparator.getInstance(false);

		List<JournalArticle> articles = journalArticlePersistence.findByR_I_S(
			resourcePrimKey, true,
			new int[] {
				WorkflowConstants.STATUS_APPROVED,
				WorkflowConstants.STATUS_IN_TRASH
			},
			0, 1, orderByComparator);

		if (articles.isEmpty()) {
			return null;
		}

		return articles.get(0);
	}

	@Override
	public PersistedModel fetchPersistedModel(Serializable primaryKeyObj) {
		PersistedModel persistedModel =
			journalArticlePersistence.fetchByPrimaryKey(primaryKeyObj);

		if (persistedModel == null) {
			persistedModel = fetchLatestArticle(
				GetterUtil.getLong(primaryKeyObj),
				WorkflowConstants.STATUS_APPROVED, true);
		}

		return persistedModel;
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
		return journalArticlePersistence.findByPrimaryKey(id);
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

		// Get the latest article that is approved, if none are approved, get
		// the latest unapproved article

		JournalArticle article = fetchLatestArticle(
			groupId, articleId, WorkflowConstants.STATUS_APPROVED);

		if (article != null) {
			return article;
		}

		return getLatestArticle(
			groupId, articleId, WorkflowConstants.STATUS_ANY);
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

		return journalArticlePersistence.findByG_A_V(
			groupId, articleId, version);
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

		List<JournalArticle> articles = journalArticlePersistence.findByG_C_C(
			groupId, _classNameLocalService.getClassNameId(className), classPK);

		if (articles.isEmpty()) {
			throw new NoSuchArticleException(
				StringBundler.concat(
					"No approved JournalArticle exists with the key {groupId=",
					groupId, ", className=", className, ", classPK=", classPK,
					"}"));
		}

		return articles.get(0);
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

		FriendlyURLEntry friendlyURLEntry =
			friendlyURLEntryLocalService.fetchFriendlyURLEntry(
				groupId, JournalArticle.class, urlTitle);

		if (friendlyURLEntry != null) {
			return getLatestArticle(
				friendlyURLEntry.getClassPK(), WorkflowConstants.STATUS_ANY,
				true);
		}

		// Get the latest article that is approved, if none are approved, get
		// the latest unapproved article

		JournalArticle article = fetchLatestArticleByUrlTitle(
			groupId, urlTitle, WorkflowConstants.STATUS_APPROVED);

		if (article != null) {
			return article;
		}

		return getLatestArticleByUrlTitle(
			groupId, urlTitle, WorkflowConstants.STATUS_ANY);
	}

	/**
	 * Returns the web content from the web content article associated with the
	 * portlet request model and the DDM template.
	 *
	 * @param  article the web content article
	 * @param  ddmTemplateKey the primary key of the web content article's DDM
	 *         template
	 * @param  viewMode the mode in which the web content is being viewed
	 * @param  languageId the primary key of the language translation to get
	 * @param  portletRequestModel the portlet request model
	 * @param  themeDisplay the theme display
	 * @return the web content from the web content article associated with the
	 *         portlet request model and the DDM template
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public String getArticleContent(
			JournalArticle article, String ddmTemplateKey, String viewMode,
			String languageId, PortletRequestModel portletRequestModel,
			ThemeDisplay themeDisplay)
		throws PortalException {

		JournalArticleDisplay articleDisplay = getArticleDisplay(
			article, ddmTemplateKey, viewMode, languageId, 1,
			portletRequestModel, themeDisplay);

		if (articleDisplay == null) {
			return StringPool.BLANK;
		}

		return articleDisplay.getContent();
	}

	/**
	 * Returns the web content from the web content article matching the group,
	 * article ID, and version, and associated with the portlet request model
	 * and the DDM template.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  articleId the primary key of the web content article
	 * @param  version the web content article's version
	 * @param  viewMode the mode in which the web content is being viewed
	 * @param  ddmTemplateKey the primary key of the web content article's DDM
	 *         template
	 * @param  languageId the primary key of the language translation to get
	 * @param  portletRequestModel the portlet request model
	 * @param  themeDisplay the theme display
	 * @return the web content from the matching web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public String getArticleContent(
			long groupId, String articleId, double version, String viewMode,
			String ddmTemplateKey, String languageId,
			PortletRequestModel portletRequestModel, ThemeDisplay themeDisplay)
		throws PortalException {

		JournalArticleDisplay articleDisplay = getArticleDisplay(
			groupId, articleId, version, ddmTemplateKey, viewMode, languageId,
			1, portletRequestModel, themeDisplay);

		if (articleDisplay == null) {
			return StringPool.BLANK;
		}

		return articleDisplay.getContent();
	}

	/**
	 * Returns the latest web content from the web content article matching the
	 * group and article ID, and associated with the portlet request model and
	 * the DDM template.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  articleId the primary key of the web content article
	 * @param  viewMode the mode in which the web content is being viewed
	 * @param  ddmTemplateKey the primary key of the web content article's DDM
	 *         template
	 * @param  languageId the primary key of the language translation to get
	 * @param  portletRequestModel the portlet request model
	 * @param  themeDisplay the theme display
	 * @return the latest web content from the matching web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public String getArticleContent(
			long groupId, String articleId, String viewMode,
			String ddmTemplateKey, String languageId,
			PortletRequestModel portletRequestModel, ThemeDisplay themeDisplay)
		throws PortalException {

		JournalArticleDisplay articleDisplay = getArticleDisplay(
			groupId, articleId, ddmTemplateKey, viewMode, languageId, 1,
			portletRequestModel, themeDisplay);

		return articleDisplay.getContent();
	}

	@Override
	public String getArticleDescription(
		long companyId, long articlePK, Locale locale) {

		String languageId = LocaleUtil.toLanguageId(locale);

		return getArticleDescription(companyId, articlePK, languageId);
	}

	@Override
	public String getArticleDescription(
		long companyId, long articlePK, String languageId) {

		JournalArticleLocalization journalArticleLocalization =
			_journalArticleLocalizationPersistence.fetchByC_A_L(
				companyId, articlePK, languageId);

		if (journalArticleLocalization == null) {
			return null;
		}

		return journalArticleLocalization.getDescription();
	}

	@Override
	public Map<Locale, String> getArticleDescriptionMap(
		long companyId, long articlePK) {

		Map<Locale, String> journalArticleLocalizationDescriptionMap =
			new HashMap<>();

		List<JournalArticleLocalization> journalArticleLocalizationList =
			_journalArticleLocalizationPersistence.findByC_A(
				companyId, articlePK);

		for (JournalArticleLocalization journalArticleLocalization :
				journalArticleLocalizationList) {

			Locale locale = LocaleUtil.fromLanguageId(
				journalArticleLocalization.getLanguageId(), true, false);

			if (locale != null) {
				journalArticleLocalizationDescriptionMap.put(
					locale, journalArticleLocalization.getDescription());
			}
		}

		return journalArticleLocalizationDescriptionMap;
	}

	/**
	 * Returns a web content article display for the specified page of the
	 * latest version of the web content article, based on the DDM template. Web
	 * content transformation tokens are added using the portlet request model
	 * and theme display.
	 *
	 * @param  article the primary key of the web content article
	 * @param  ddmTemplateKey the primary key of the web content article's DDM
	 *         template
	 * @param  viewMode the mode in which the web content is being viewed
	 * @param  languageId the primary key of the language translation to get
	 * @param  page the web content article page to display
	 * @param  portletRequestModel the portlet request model
	 * @param  themeDisplay the theme display
	 * @return the web content article display, or <code>null</code> if the
	 *         article has expired or if article's display date/time is after
	 *         the current date/time
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticleDisplay getArticleDisplay(
			JournalArticle article, String ddmTemplateKey, String viewMode,
			String languageId, int page,
			PortletRequestModel portletRequestModel, ThemeDisplay themeDisplay)
		throws PortalException {

		return getArticleDisplay(
			article, ddmTemplateKey, viewMode, languageId, page,
			portletRequestModel, themeDisplay, false);
	}

	/**
	 * Returns a web content article display for the specified page of the
	 * specified version of the web content article matching the group, article
	 * ID, and DDM template. Web content transformation tokens are added using
	 * the portlet request model and theme display.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  articleId the primary key of the web content article
	 * @param  version the web content article's version
	 * @param  ddmTemplateKey the primary key of the web content article's DDM
	 *         template
	 * @param  viewMode the mode in which the web content is being viewed
	 * @param  languageId the primary key of the language translation to get
	 * @param  page the web content article page to display
	 * @param  portletRequestModel the portlet request model
	 * @param  themeDisplay the theme display
	 * @return the web content article display, or <code>null</code> if the
	 *         article has expired or if article's display date/time is after
	 *         the current date/time
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticleDisplay getArticleDisplay(
			long groupId, String articleId, double version,
			String ddmTemplateKey, String viewMode, String languageId, int page,
			PortletRequestModel portletRequestModel, ThemeDisplay themeDisplay)
		throws PortalException {

		Date date = new Date();

		JournalArticle article = journalArticlePersistence.findByG_A_V(
			groupId, articleId, version);

		boolean preview = Objects.equals(viewMode, Constants.PREVIEW);

		if (article.isExpired() && !preview) {
			Date expirationDate = article.getExpirationDate();

			if ((expirationDate != null) && expirationDate.before(date)) {
				return null;
			}
		}

		Date displayDate = article.getDisplayDate();

		if ((displayDate != null) && displayDate.after(date) && !preview) {
			return null;
		}

		return getArticleDisplay(
			article, ddmTemplateKey, viewMode, languageId, page,
			portletRequestModel, themeDisplay);
	}

	/**
	 * Returns a web content article display for the first page of the specified
	 * version of the web content article matching the group, article ID, and
	 * DDM template. Web content transformation tokens are added from the theme
	 * display (if not <code>null</code>).
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  articleId the primary key of the web content article
	 * @param  version the web content article's version
	 * @param  ddmTemplateKey the primary key of the web content article's DDM
	 *         template
	 * @param  viewMode the mode in which the web content is being viewed
	 * @param  languageId the primary key of the language translation to get
	 * @param  themeDisplay the theme display
	 * @return the web content article display, or <code>null</code> if the
	 *         article has expired or if article's display date/time is after
	 *         the current date/time
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticleDisplay getArticleDisplay(
			long groupId, String articleId, double version,
			String ddmTemplateKey, String viewMode, String languageId,
			ThemeDisplay themeDisplay)
		throws PortalException {

		return getArticleDisplay(
			groupId, articleId, version, ddmTemplateKey, viewMode, languageId,
			1, null, themeDisplay);
	}

	/**
	 * Returns a web content article display for the specified page of the
	 * latest version of the web content article matching the group and article
	 * ID. Web content transformation tokens are added from the theme display
	 * (if not <code>null</code>).
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  articleId the primary key of the web content article
	 * @param  viewMode the mode in which the web content is being viewed
	 * @param  languageId the primary key of the language translation to get
	 * @param  page the web content article page to display
	 * @param  portletRequestModel the portlet request model
	 * @param  themeDisplay the theme display
	 * @return the web content article display, or <code>null</code> if the
	 *         article has expired or if article's display date/time is after
	 *         the current date/time
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticleDisplay getArticleDisplay(
			long groupId, String articleId, String viewMode, String languageId,
			int page, PortletRequestModel portletRequestModel,
			ThemeDisplay themeDisplay)
		throws PortalException {

		JournalArticle article = getDisplayArticle(groupId, articleId);

		return getArticleDisplay(
			groupId, articleId, article.getDDMTemplateKey(), viewMode,
			languageId, page, portletRequestModel, themeDisplay);
	}

	/**
	 * Returns a web content article display for the specified page of the
	 * latest version of the web content article matching the group, article ID,
	 * and DDM template. Web content transformation tokens are added using the
	 * portlet request model and theme display.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  articleId the primary key of the web content article
	 * @param  ddmTemplateKey the primary key of the web content article's DDM
	 *         template
	 * @param  viewMode the mode in which the web content is being viewed
	 * @param  languageId the primary key of the language translation to get
	 * @param  page the web content article page to display
	 * @param  portletRequestModel the portlet request model
	 * @param  themeDisplay the theme display
	 * @return the web content article display, or <code>null</code> if the
	 *         article has expired or if article's display date/time is after
	 *         the current date/time
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticleDisplay getArticleDisplay(
			long groupId, String articleId, String ddmTemplateKey,
			String viewMode, String languageId, int page,
			PortletRequestModel portletRequestModel, ThemeDisplay themeDisplay)
		throws PortalException {

		JournalArticle article = getDisplayArticle(groupId, articleId);

		return getArticleDisplay(
			groupId, articleId, article.getVersion(), ddmTemplateKey, viewMode,
			languageId, page, portletRequestModel, themeDisplay);
	}

	/**
	 * Returns a web content article display for the first page of the latest
	 * version of the web content article matching the group, article ID, and
	 * DDM template. Web content transformation tokens are added from the theme
	 * display (if not <code>null</code>).
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  articleId the primary key of the web content article
	 * @param  ddmTemplateKey the primary key of the web content article's DDM
	 *         template
	 * @param  viewMode the mode in which the web content is being viewed
	 * @param  languageId the primary key of the language translation to get
	 * @param  themeDisplay the theme display
	 * @return the web content article display, or <code>null</code> if the
	 *         article has expired or if article's display date/time is after
	 *         the current date/time
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticleDisplay getArticleDisplay(
			long groupId, String articleId, String ddmTemplateKey,
			String viewMode, String languageId, ThemeDisplay themeDisplay)
		throws PortalException {

		JournalArticle article = getDisplayArticle(groupId, articleId);

		return getArticleDisplay(
			groupId, articleId, article.getVersion(), ddmTemplateKey, viewMode,
			languageId, themeDisplay);
	}

	/**
	 * Returns a web content article display for the first page of the latest
	 * version of the web content article matching the group and article ID. Web
	 * content transformation tokens are added from the theme display (if not
	 * <code>null</code>).
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  articleId the primary key of the web content article
	 * @param  viewMode the mode in which the web content is being viewed
	 * @param  languageId the primary key of the language translation to get
	 * @param  themeDisplay the theme display
	 * @return the web content article display, or <code>null</code> if the
	 *         article has expired or if article's display date/time is after
	 *         the current date/time
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticleDisplay getArticleDisplay(
			long groupId, String articleId, String viewMode, String languageId,
			ThemeDisplay themeDisplay)
		throws PortalException {

		JournalArticle article = getDisplayArticle(groupId, articleId);

		return getArticleDisplay(
			groupId, articleId, article.getDDMTemplateKey(), viewMode,
			languageId, themeDisplay);
	}

	@Override
	public List<String> getArticleLocalizationLanguageIds(
		long companyId, long articlePK) {

		return TransformUtil.transform(
			_journalArticleLocalizationPersistence.findByC_A(
				companyId, articlePK),
			journalArticleLocalization ->
				journalArticleLocalization.getLanguageId());
	}

	/**
	 * Returns all the web content articles present in the system.
	 *
	 * @return the web content articles present in the system
	 */
	@Override
	public List<JournalArticle> getArticles() {
		return journalArticlePersistence.findAll();
	}

	/**
	 * Returns all the web content articles belonging to the group.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @return the web content articles belonging to the group
	 */
	@Override
	public List<JournalArticle> getArticles(long groupId) {
		return journalArticlePersistence.findByGroupId(groupId);
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
	 * @param  groupId the primary key of the web content article's group
	 * @param  start the lower bound of the range of web content articles to
	 *         return
	 * @param  end the upper bound of the range of web content articles to
	 *         return (not inclusive)
	 * @return the range of matching web content articles
	 */
	@Override
	public List<JournalArticle> getArticles(long groupId, int start, int end) {
		return journalArticlePersistence.findByGroupId(groupId, start, end);
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
	 * @param  groupId the primary key of the web content article's group
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
	public List<JournalArticle> getArticles(
		long groupId, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return journalArticlePersistence.findByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns all the web content articles matching the group and folder.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  folderId the primary key of the web content article folder
	 * @return the matching web content articles
	 */
	@Override
	public List<JournalArticle> getArticles(long groupId, long folderId) {
		return journalArticlePersistence.findByG_F(groupId, folderId);
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
	 * @param  groupId the primary key of the web content article's group
	 * @param  folderId the primary key of the web content article's folder
	 * @param  start the lower bound of the range of web content articles to
	 *         return
	 * @param  end the upper bound of the range of web content articles to
	 *         return (not inclusive)
	 * @return the range of matching web content articles
	 */
	@Override
	public List<JournalArticle> getArticles(
		long groupId, long folderId, int start, int end) {

		return journalArticlePersistence.findByG_F(
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
	 * @param  groupId the primary key of the web content article's group
	 * @param  folderId the primary key of the web content article's folder
	 * @param  status the web content article's workflow status. For more
	 *         information see {@link WorkflowConstants} for constants starting
	 *         with the "STATUS_" prefix.
	 * @param  start the lower bound of the range of web content articles to
	 *         return
	 * @param  end the upper bound of the range of web content articles to
	 *         return (not inclusive)
	 * @return the range of matching web content articles
	 */
	@Override
	public List<JournalArticle> getArticles(
		long groupId, long folderId, int status, int start, int end) {

		return journalArticlePersistence.findByG_F_ST(
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
	 * @param  groupId the primary key of the web content article's group
	 * @param  folderId the primary key of the web content article's folder
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
	public List<JournalArticle> getArticles(
		long groupId, long folderId, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return journalArticlePersistence.findByG_F(
			groupId, folderId, start, end, orderByComparator);
	}

	/**
	 * Returns all the web content articles matching the group and article ID.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  articleId the primary key of the web content article
	 * @return the matching web content articles
	 */
	@Override
	public List<JournalArticle> getArticles(long groupId, String articleId) {
		return journalArticlePersistence.findByG_A(groupId, articleId);
	}

	@Override
	public List<JournalArticle> getArticles(
		long groupId, String articleId, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return journalArticlePersistence.findByG_A(
			groupId, articleId, start, end, orderByComparator);
	}

	/**
	 * Returns all the web content articles matching the resource primary key.
	 *
	 * @param  resourcePrimKey the primary key of the resource instance
	 * @return the web content articles matching the resource primary key
	 */
	@Override
	public List<JournalArticle> getArticlesByResourcePrimKey(
		long resourcePrimKey) {

		return journalArticlePersistence.findByResourcePrimKey(resourcePrimKey);
	}

	@Override
	public List<JournalArticle> getArticlesByReviewDate(
		long companyId, Date previousCheckDate, Date reviewDate) {

		return journalArticlePersistence.dslQuery(
			DSLQueryFactoryUtil.select(
				JournalArticleTable.INSTANCE
			).from(
				JournalArticleTable.INSTANCE
			).where(
				JournalArticleTable.INSTANCE.companyId.eq(
					companyId
				).and(
					JournalArticleTable.INSTANCE.classNameId.eq(
						JournalArticleConstants.CLASS_NAME_ID_DEFAULT)
				).and(
					JournalArticleTable.INSTANCE.reviewDate.gte(
						previousCheckDate)
				).and(
					JournalArticleTable.INSTANCE.reviewDate.lte(reviewDate)
				)
			));
	}

	/**
	 * Returns all the web content articles matching the small image ID.
	 *
	 * @param  smallImageId the primary key of the web content article's small
	 *         image
	 * @return the web content articles matching the small image ID
	 */
	@Override
	public List<JournalArticle> getArticlesBySmallImageId(long smallImageId) {
		return journalArticlePersistence.findBySmallImageId(smallImageId);
	}

	@Override
	public List<JournalArticle> getArticlesByStructureId(
		long groupId, long ddmStructureId, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		QueryDefinition<JournalArticle> queryDefinition = new QueryDefinition<>(
			status, start, end, orderByComparator);

		return journalArticleFinder.findByG_F_C_S_L(
			groupId, Collections.emptyList(),
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, ddmStructureId,
			LocaleUtil.getMostRelevantLocale(), queryDefinition);
	}

	@Override
	public List<JournalArticle> getArticlesByStructureId(
		long groupId, long ddmStructureId, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getArticlesByStructureId(
			groupId, ddmStructureId, WorkflowConstants.STATUS_ANY, start, end,
			orderByComparator);
	}

	@Override
	public List<JournalArticle> getArticlesByStructureId(
		long groupId, long ddmStructureId, Locale locale, int status, int start,
		int end, OrderByComparator<JournalArticle> orderByComparator) {

		QueryDefinition<JournalArticle> queryDefinition = new QueryDefinition<>(
			status, start, end, orderByComparator);

		return journalArticleFinder.findByG_F_C_S_L(
			groupId, Collections.emptyList(),
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, ddmStructureId,
			locale, queryDefinition);
	}

	@Override
	public List<JournalArticle> getArticlesByStructureId(
		long groupId, long classNameId, long ddmStructureId, int status,
		int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		QueryDefinition<JournalArticle> queryDefinition = new QueryDefinition<>(
			status, start, end, orderByComparator);

		return journalArticleFinder.findByG_F_C_S_L(
			groupId, Collections.emptyList(), classNameId, ddmStructureId,
			LocaleUtil.getMostRelevantLocale(), queryDefinition);
	}

	@Override
	public List<JournalArticle> getArticlesByStructureId(
		long groupId, long classNameId, long ddmStructureId, Locale locale,
		int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		QueryDefinition<JournalArticle> queryDefinition = new QueryDefinition<>(
			status, start, end, orderByComparator);

		return journalArticleFinder.findByG_F_C_S_L(
			groupId, Collections.emptyList(), classNameId, ddmStructureId,
			locale, queryDefinition);
	}

	@Override
	public List<Long> getArticlesClassPKsWithDefaultDisplayPage(
		long groupId, long classTypeId) {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				fetchDefaultLayoutPageTemplateEntry(
					groupId,
					_portal.getClassNameId(JournalArticle.class.getName()),
					classTypeId);

		if (layoutPageTemplateEntry == null) {
			return Collections.emptyList();
		}

		JournalArticleTable tempJournalArticleTable =
			JournalArticleTable.INSTANCE.as("tempJournalArticleTable");

		DDMStructure ddmStructure = _ddmStructureLocalService.fetchDDMStructure(
			layoutPageTemplateEntry.getClassTypeId());

		return journalArticlePersistence.dslQuery(
			DSLQueryFactoryUtil.selectDistinct(
				JournalArticleTable.INSTANCE.resourcePrimKey
			).from(
				JournalArticleTable.INSTANCE
			).where(
				JournalArticleTable.INSTANCE.groupId.eq(
					groupId
				).and(
					JournalArticleTable.INSTANCE.DDMStructureId.eq(
						ddmStructure.getStructureId())
				).and(
					JournalArticleTable.INSTANCE.layoutUuid.isNull()
				).and(
					JournalArticleTable.INSTANCE.status.eq(
						WorkflowConstants.STATUS_APPROVED)
				).and(
					JournalArticleTable.INSTANCE.version.in(
						DSLQueryFactoryUtil.select(
							DSLFunctionFactoryUtil.max(
								tempJournalArticleTable.version)
						).from(
							tempJournalArticleTable
						).where(
							tempJournalArticleTable.resourcePrimKey.eq(
								JournalArticleTable.INSTANCE.resourcePrimKey
							).and(
								tempJournalArticleTable.status.eq(
									WorkflowConstants.STATUS_APPROVED)
							)
						))
				).and(
					JournalArticleTable.INSTANCE.resourcePrimKey.notIn(
						DSLQueryFactoryUtil.select(
							AssetDisplayPageEntryTable.INSTANCE.classPK
						).from(
							AssetDisplayPageEntryTable.INSTANCE
						).where(
							AssetDisplayPageEntryTable.INSTANCE.groupId.eq(
								groupId
							).and(
								AssetDisplayPageEntryTable.INSTANCE.classNameId.
									eq(
										_portal.getClassNameId(
											JournalArticle.class.getName()))
							)
						))
				)
			));
	}

	/**
	 * Returns the number of web content articles belonging to the group.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @return the number of web content articles belonging to the group
	 */
	@Override
	public int getArticlesCount(long groupId) {
		return journalArticlePersistence.countByGroupId(groupId);
	}

	/**
	 * Returns the number of web content articles matching the group and folder.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  folderId the primary key of the web content article's folder
	 * @return the number of matching web content articles
	 */
	@Override
	public int getArticlesCount(long groupId, long folderId) {
		return journalArticlePersistence.countByG_F(groupId, folderId);
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
		return journalArticlePersistence.countByG_F_ST(
			groupId, folderId, status);
	}

	@Override
	public int getArticlesCount(long groupId, String articleId) {
		return journalArticlePersistence.countByG_A(groupId, articleId);
	}

	@Override
	public int getArticlesCountByResourcePrimKey(long resourcePrimKey) {
		return journalArticlePersistence.countByResourcePrimKey(
			resourcePrimKey);
	}

	@Override
	public String getArticleTitle(
		long companyId, long articlePK, Locale locale) {

		String languageId = LocaleUtil.toLanguageId(locale);

		return getArticleTitle(companyId, articlePK, languageId);
	}

	@Override
	public String getArticleTitle(
		long companyId, long articlePK, String languageId) {

		JournalArticleLocalization journalArticleLocalization =
			_journalArticleLocalizationPersistence.fetchByC_A_L(
				companyId, articlePK, languageId);

		if (journalArticleLocalization == null) {
			return null;
		}

		return journalArticleLocalization.getTitle();
	}

	@Override
	public Map<Locale, String> getArticleTitleMap(
		long companyId, long articlePK) {

		Map<Locale, String> journalArticleLocalizationTitleMap =
			new HashMap<>();

		List<JournalArticleLocalization> journalArticleLocalizationList =
			_journalArticleLocalizationPersistence.findByC_A(
				companyId, articlePK);

		for (JournalArticleLocalization journalArticleLocalization :
				journalArticleLocalizationList) {

			Locale locale = LocaleUtil.fromLanguageId(
				journalArticleLocalization.getLanguageId(), true, false);

			if (locale != null) {
				journalArticleLocalizationTitleMap.put(
					locale, journalArticleLocalization.getTitle());
			}
		}

		return journalArticleLocalizationTitleMap;
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
	 * @param  companyId the primary key of the web content article's company
	 * @param  version the web content article's version
	 * @param  status the web content article's workflow status. For more
	 *         information see {@link WorkflowConstants} for constants starting
	 *         with the "STATUS_" prefix.
	 * @param  start the lower bound of the range of web content articles to
	 *         return
	 * @param  end the upper bound of the range of web content articles to
	 *         return (not inclusive)
	 * @return the range of matching web content articles ordered by article ID
	 */
	@Override
	public List<JournalArticle> getCompanyArticles(
		long companyId, double version, int status, int start, int end) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return journalArticlePersistence.findByC_V(
				companyId, version, start, end,
				ArticleIDComparator.getInstance(true));
		}

		return journalArticlePersistence.findByC_V_ST(
			companyId, version, status, start, end,
			ArticleIDComparator.getInstance(true));
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
	 * @param  companyId the primary key of the web content article's company
	 * @param  status the web content article's workflow status. For more
	 *         information see {@link WorkflowConstants} for constants starting
	 *         with the "STATUS_" prefix.
	 * @param  start the lower bound of the range of web content articles to
	 *         return
	 * @param  end the upper bound of the range of web content articles to
	 *         return (not inclusive)
	 * @return the range of matching web content articles ordered by article ID
	 */
	@Override
	public List<JournalArticle> getCompanyArticles(
		long companyId, int status, int start, int end) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return journalArticlePersistence.findByCompanyId(
				companyId, start, end, ArticleIDComparator.getInstance(true));
		}

		return journalArticlePersistence.findByC_ST(
			companyId, status, start, end,
			ArticleIDComparator.getInstance(true));
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
	 * @param  companyId the primary key of the web content article's company
	 * @param  version the web content article's version
	 * @param  status the web content article's workflow status. For more
	 *         information see {@link WorkflowConstants} for constants starting
	 *         with the "STATUS_" prefix.
	 * @param  start the lower bound of the range of web content articles to
	 *         return
	 * @param  end the upper bound of the range of web content articles to
	 *         return (not inclusive)
	 * @return the number of matching web content articles
	 */
	@Override
	public int getCompanyArticlesCount(
		long companyId, double version, int status, int start, int end) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return journalArticlePersistence.countByC_V(companyId, version);
		}

		return journalArticlePersistence.countByC_V_ST(
			companyId, version, status);
	}

	/**
	 * Returns the number of web content articles matching the company and
	 * workflow status.
	 *
	 * @param  companyId the primary key of the web content article's company
	 * @param  status the web content article's workflow status. For more
	 *         information see {@link WorkflowConstants} for constants starting
	 *         with the "STATUS_" prefix.
	 * @return the number of matching web content articles
	 */
	@Override
	public int getCompanyArticlesCount(long companyId, int status) {
		if (status == WorkflowConstants.STATUS_ANY) {
			return journalArticlePersistence.countByCompanyId(companyId);
		}

		return journalArticlePersistence.countByC_ST(companyId, status);
	}

	/**
	 * Returns the matching web content article currently displayed or next to
	 * be displayed if no article is currently displayed.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  articleId the primary key of the web content article
	 * @return the matching web content article currently displayed, or the next
	 *         one to be displayed if no version of the article is currently
	 *         displayed
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle getDisplayArticle(long groupId, String articleId)
		throws PortalException {

		JournalArticle article = fetchDisplayArticle(groupId, articleId);

		if (article == null) {
			throw new NoSuchArticleException(
				StringBundler.concat(
					"No approved JournalArticle exists with the key {groupId=",
					groupId, ", articleId=", articleId, "}"));
		}

		return article;
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

		List<JournalArticle> articles = null;

		FriendlyURLEntry friendlyURLEntry =
			friendlyURLEntryLocalService.fetchFriendlyURLEntry(
				groupId, JournalArticle.class, urlTitle);

		if (friendlyURLEntry != null) {
			articles = journalArticlePersistence.findByR_ST(
				friendlyURLEntry.getClassPK(),
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, ArticleVersionComparator.getInstance(false));
		}
		else {
			articles = journalArticlePersistence.findByG_UT_ST(
				groupId, _friendlyURLNormalizer.normalizeWithEncoding(urlTitle),
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, ArticleVersionComparator.getInstance(false));
		}

		if (articles.isEmpty()) {
			throw new NoSuchArticleException(
				StringBundler.concat(
					"No JournalArticle exists with the key {groupId=", groupId,
					", urlTitle=", urlTitle, "}"));
		}

		Date date = new Date();

		for (JournalArticle article : articles) {
			Date displayDate = article.getDisplayDate();
			Date expirationDate = article.getExpirationDate();

			if ((displayDate != null) && displayDate.before(date) &&
				((expirationDate == null) || expirationDate.after(date))) {

				return article;
			}
		}

		return articles.get(0);
	}

	@Override
	public List<Long> getGroupIdsByUrlTitle(long companyId, String urlTitle) {
		return journalArticlePersistence.dslQuery(
			DSLQueryFactoryUtil.selectDistinct(
				FriendlyURLEntryLocalizationTable.INSTANCE.groupId
			).from(
				FriendlyURLEntryLocalizationTable.INSTANCE
			).where(
				FriendlyURLEntryLocalizationTable.INSTANCE.companyId.eq(
					companyId
				).and(
					FriendlyURLEntryLocalizationTable.INSTANCE.classNameId.eq(
						_portal.getClassNameId(JournalArticle.class.getName())
					).and(
						FriendlyURLEntryLocalizationTable.INSTANCE.urlTitle.eq(
							urlTitle)
					)
				)
			));
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

		return getLatestArticle(resourcePrimKey, WorkflowConstants.STATUS_ANY);
	}

	/**
	 * Returns the latest web content article matching the resource primary key
	 * and workflow status, preferring articles with approved workflow status.
	 *
	 * @param  resourcePrimKey the primary key of the resource instance
	 * @param  status the web content article's workflow status. For more
	 *         information see {@link WorkflowConstants} for constants starting
	 *         with the "STATUS_" prefix.
	 * @return the latest web content article matching the resource primary key
	 *         and workflow status, preferring articles with approved workflow
	 *         status
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle getLatestArticle(long resourcePrimKey, int status)
		throws PortalException {

		return getLatestArticle(resourcePrimKey, status, true);
	}

	/**
	 * Returns the latest web content article matching the resource primary key
	 * and workflow status, optionally preferring articles with approved
	 * workflow status.
	 *
	 * @param  resourcePrimKey the primary key of the resource instance
	 * @param  status the web content article's workflow status. For more
	 *         information see {@link WorkflowConstants} for constants starting
	 *         with the "STATUS_" prefix.
	 * @param  preferApproved whether to prefer returning the latest matching
	 *         article that has workflow status {@link
	 *         WorkflowConstants#STATUS_APPROVED} over returning one that has a
	 *         different status
	 * @return the latest web content article matching the resource primary key
	 *         and workflow status, optionally preferring articles with approved
	 *         workflow status
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle getLatestArticle(
			long resourcePrimKey, int status, boolean preferApproved)
		throws PortalException {

		List<JournalArticle> articles = null;

		OrderByComparator<JournalArticle> orderByComparator =
			ArticleVersionComparator.getInstance(false);

		if (status == WorkflowConstants.STATUS_ANY) {
			if (preferApproved) {
				articles = journalArticlePersistence.findByR_ST(
					resourcePrimKey, WorkflowConstants.STATUS_APPROVED, 0, 1,
					orderByComparator);
			}

			if (ListUtil.isEmpty(articles)) {
				articles = journalArticlePersistence.findByResourcePrimKey(
					resourcePrimKey, 0, 1, orderByComparator);
			}
		}
		else {
			articles = journalArticlePersistence.findByR_ST(
				resourcePrimKey, status, 0, 1, orderByComparator);
		}

		if (articles.isEmpty()) {
			throw new NoSuchArticleException(
				"No JournalArticle exists with the key {resourcePrimKey=" +
					resourcePrimKey + "}");
		}

		return articles.get(0);
	}

	/**
	 * Returns the latest web content article with the group and article ID.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  articleId the primary key of the web content article
	 * @return the latest matching web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle getLatestArticle(long groupId, String articleId)
		throws PortalException {

		return getLatestArticle(
			groupId, articleId, WorkflowConstants.STATUS_ANY);
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

		return getFirstArticle(
			groupId, articleId, status,
			ArticleVersionComparator.getInstance(false));
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

		List<JournalArticle> articles = journalArticlePersistence.findByG_C_C(
			groupId, _classNameLocalService.getClassNameId(className), classPK,
			0, 1, ArticleVersionComparator.getInstance(false));

		if (articles.isEmpty()) {
			throw new NoSuchArticleException(
				StringBundler.concat(
					"No JournalArticle exists with the key {groupId=", groupId,
					", className=", className, ", classPK =", classPK, "}"));
		}

		return articles.get(0);
	}

	/**
	 * Returns the latest web content article matching the group and the
	 * external reference code.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  externalReferenceCode the web content article's external
	 *         reference code
	 * @return the latest matching web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle getLatestArticleByExternalReferenceCode(
			long groupId, String externalReferenceCode)
		throws PortalException {

		OrderByComparator<JournalArticle> orderByComparator =
			ArticleVersionComparator.getInstance(false);

		return journalArticlePersistence.findByG_ERC_First(
			groupId, externalReferenceCode, orderByComparator);
	}

	@Override
	public JournalArticle getLatestArticleByExternalReferenceCode(
			long groupId, String externalReferenceCode, int status,
			boolean preferApproved)
		throws PortalException {

		List<JournalArticle> articles = null;

		OrderByComparator<JournalArticle> orderByComparator =
			ArticleVersionComparator.getInstance(false);

		if (status == WorkflowConstants.STATUS_ANY) {
			if (preferApproved) {
				articles = journalArticlePersistence.findByG_ERC_ST(
					groupId, externalReferenceCode,
					WorkflowConstants.STATUS_APPROVED, 0, 1, orderByComparator);
			}

			if (ListUtil.isEmpty(articles)) {
				articles = journalArticlePersistence.findByG_ERC(
					groupId, externalReferenceCode, 0, 1, orderByComparator);
			}
		}
		else {
			articles = journalArticlePersistence.findByG_ERC_ST(
				groupId, externalReferenceCode, status, 0, 1,
				orderByComparator);
		}

		if (articles.isEmpty()) {
			StringBundler sb = new StringBundler(7);

			sb.append("No JournalArticle exists with the key {groupId=");
			sb.append(groupId);
			sb.append(", externalReferenceCode=");
			sb.append(externalReferenceCode);
			sb.append(", status=");
			sb.append(status);
			sb.append("}");

			throw new NoSuchArticleException(sb.toString());
		}

		return articles.get(0);
	}

	/**
	 * Returns the latest web content article matching the group, URL title, and
	 * workflow status.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  urlTitle the web content article's accessible URL title
	 * @param  status the web content article's workflow status. For more
	 *         information see {@link WorkflowConstants} for constants starting
	 *         with the "STATUS_" prefix.
	 * @return the latest matching web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle getLatestArticleByUrlTitle(
			long groupId, String urlTitle, int status)
		throws PortalException {

		JournalArticle article = null;

		FriendlyURLEntry friendlyURLEntry =
			friendlyURLEntryLocalService.fetchFriendlyURLEntry(
				groupId, JournalArticle.class, urlTitle);

		if (friendlyURLEntry != null) {
			article = fetchLatestArticle(friendlyURLEntry.getClassPK(), status);
		}
		else {
			article = fetchLatestArticleByUrlTitle(groupId, urlTitle, status);
		}

		if (article == null) {
			throw new NoSuchArticleException(
				StringBundler.concat(
					"No JournalArticle exists with the key {groupId=", groupId,
					", urlTitle=", urlTitle, ", status=", status, "}"));
		}

		return article;
	}

	/**
	 * Returns the latest version number of the web content with the group and
	 * article ID.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  articleId the primary key of the web content article
	 * @return the latest version number of the matching web content
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public double getLatestVersion(long groupId, String articleId)
		throws PortalException {

		JournalArticle article = getLatestArticle(groupId, articleId);

		return article.getVersion();
	}

	/**
	 * Returns the latest version number of the web content with the group,
	 * article ID, and workflow status.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  articleId the primary key of the web content article
	 * @param  status the web content article's workflow status. For more
	 *         information see {@link WorkflowConstants} for constants starting
	 *         with the "STATUS_" prefix.
	 * @return the latest version number of the matching web content
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public double getLatestVersion(long groupId, String articleId, int status)
		throws PortalException {

		JournalArticle article = getLatestArticle(groupId, articleId, status);

		return article.getVersion();
	}

	@Override
	public List<JournalArticle> getNoAssetArticles() {
		return journalArticlePersistence.dslQuery(
			DSLQueryFactoryUtil.select(
				JournalArticleTable.INSTANCE
			).from(
				JournalArticleTable.INSTANCE
			).leftJoinOn(
				AssetEntryTable.INSTANCE,
				AssetEntryTable.INSTANCE.classNameId.eq(
					_portal.getClassNameId(JournalArticle.class)
				).and(
					AssetEntryTable.INSTANCE.classPK.eq(
						JournalArticleTable.INSTANCE.resourcePrimKey)
				)
			).where(
				AssetEntryTable.INSTANCE.classPK.isNull()
			));
	}

	@Override
	public List<JournalArticle> getNoPermissionArticles() {
		return journalArticlePersistence.dslQuery(
			DSLQueryFactoryUtil.select(
				JournalArticleTable.INSTANCE
			).from(
				JournalArticleTable.INSTANCE
			).leftJoinOn(
				ResourcePermissionTable.INSTANCE,
				ResourcePermissionTable.INSTANCE.companyId.eq(
					JournalArticleTable.INSTANCE.companyId
				).and(
					ResourcePermissionTable.INSTANCE.name.eq(
						JournalArticle.class.getName())
				).and(
					ResourcePermissionTable.INSTANCE.primKeyId.eq(
						JournalArticleTable.INSTANCE.resourcePrimKey)
				).and(
					ResourcePermissionTable.INSTANCE.scope.eq(
						ResourceConstants.SCOPE_INDIVIDUAL)
				)
			).where(
				ResourcePermissionTable.INSTANCE.primKey.isNull()
			));
	}

	/**
	 * Returns the number of web content articles that are not recycled.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  folderId the primary key of the web content article folder
	 * @return the number of web content articles that are not recycled
	 */
	@Override
	public int getNotInTrashArticlesCount(long groupId, long folderId) {
		QueryDefinition<JournalArticle> queryDefinition = new QueryDefinition<>(
			WorkflowConstants.STATUS_ANY);

		return journalArticleFinder.countByG_F(
			groupId, ListUtil.fromArray(folderId), queryDefinition);
	}

	/**
	 * Returns the oldest web content article with the group and article ID.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  articleId the primary key of the web content article
	 * @return the oldest matching web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle getOldestArticle(long groupId, String articleId)
		throws PortalException {

		return getOldestArticle(
			groupId, articleId, WorkflowConstants.STATUS_ANY);
	}

	/**
	 * Returns the oldest web content article matching the group, article ID,
	 * and workflow status.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  articleId the primary key of the web content article
	 * @param  status the web content article's workflow status. For more
	 *         information see {@link WorkflowConstants} for constants starting
	 *         with the "STATUS_" prefix.
	 * @return the oldest matching web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle getOldestArticle(
			long groupId, String articleId, int status)
		throws PortalException {

		return getFirstArticle(
			groupId, articleId, status,
			ArticleVersionComparator.getInstance(true));
	}

	/**
	 * Returns the previously approved version of the web content article. For
	 * more information on the approved workflow status, see {@link
	 * WorkflowConstants#STATUS_APPROVED}.
	 *
	 * @param  article the web content article
	 * @return the previously approved version of the web content article, or
	 *         the current web content article if there are no previously
	 *         approved web content articles
	 */
	@Override
	public JournalArticle getPreviousApprovedArticle(JournalArticle article) {
		List<JournalArticle> approvedArticles =
			journalArticlePersistence.findByG_A_ST(
				article.getGroupId(), article.getArticleId(),
				WorkflowConstants.STATUS_APPROVED, 0, 2);

		if (approvedArticles.isEmpty() ||
			((approvedArticles.size() == 1) &&
			 (article.getStatus() == WorkflowConstants.STATUS_APPROVED))) {

			return article;
		}

		JournalArticle previousApprovedArticle = approvedArticles.get(0);

		if ((approvedArticles.size() > 1) &&
			(previousApprovedArticle.getVersion() == article.getVersion())) {

			previousApprovedArticle = approvedArticles.get(1);
		}

		return previousApprovedArticle;
	}

	/**
	 * Returns the web content articles matching the DDM structure keys.
	 *
	 * @param  ddmStructureId the primary key of the web content article's DDM
	 *         structure
	 * @return the web content articles matching the DDM structure keys
	 */
	@Override
	public List<JournalArticle> getStructureArticles(long ddmStructureId) {
		return journalArticlePersistence.findByDDMStructureId(ddmStructureId);
	}

	/**
	 * Returns the web content articles matching the group and DDM structure
	 * key.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  ddmStructureId the primary key of the web content article's DDM
	 *         structure
	 * @return the matching web content articles
	 */
	@Override
	public List<JournalArticle> getStructureArticles(
		long groupId, long ddmStructureId) {

		return journalArticlePersistence.findByG_DDMSI(groupId, ddmStructureId);
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
	public List<JournalArticle> getStructureArticles(
		long groupId, long ddmStructureId, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return journalArticlePersistence.findByG_DDMSI(
			groupId, ddmStructureId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of web content articles matching the group and DDM
	 * structure key.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  ddmStructureId the primary key of the web content article's DDM
	 *         structure
	 * @return the number of matching web content articles
	 */
	@Override
	public int getStructureArticlesCount(long groupId, long ddmStructureId) {
		return journalArticlePersistence.countByG_DDMSI(
			groupId, ddmStructureId);
	}

	/**
	 * Returns the web content articles matching the group and DDM template key.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  ddmTemplateKey the primary key of the web content article's DDM
	 *         template
	 * @return the matching web content articles
	 */
	@Override
	public List<JournalArticle> getTemplateArticles(
		long groupId, String ddmTemplateKey) {

		return journalArticlePersistence.findByG_DDMTK(groupId, ddmTemplateKey);
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
	 * @param  groupId the primary key of the web content article's group
	 * @param  ddmTemplateKey the primary key of the web content article's DDM
	 *         template
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
	public List<JournalArticle> getTemplateArticles(
		long groupId, String ddmTemplateKey, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return journalArticlePersistence.findByG_DDMTK(
			groupId, ddmTemplateKey, start, end, orderByComparator);
	}

	/**
	 * Returns the number of web content articles matching the group and DDM
	 * template key.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  ddmTemplateKey the primary key of the web content article's DDM
	 *         template
	 * @return the number of matching web content articles
	 */
	@Override
	public int getTemplateArticlesCount(long groupId, String ddmTemplateKey) {
		return journalArticlePersistence.countByG_DDMTK(
			groupId, ddmTemplateKey);
	}

	/**
	 * Returns the web content article's unique URL title.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  articleId the primary key of the web content article
	 * @param  urlTitle the web content article's accessible URL title
	 * @return the web content article's unique URL title
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public String getUniqueUrlTitle(
			long groupId, String articleId, String urlTitle)
		throws PortalException {

		for (int i = 1;; i++) {
			JournalArticle article = fetchArticleByUrlTitle(groupId, urlTitle);

			if ((article == null) || articleId.equals(article.getArticleId())) {
				break;
			}

			String suffix = StringPool.DASH + i;

			String prefix = urlTitle;

			if (urlTitle.length() > suffix.length()) {
				prefix = urlTitle.substring(
					0, urlTitle.length() - suffix.length());
			}

			urlTitle = prefix + suffix;
		}

		return urlTitle;
	}

	/**
	 * Returns <code>true</code> if the specified web content article exists.
	 *
	 * @param  groupId the primary key of the group
	 * @param  articleId the primary key of the web content article
	 * @return <code>true</code> if the specified web content article exists;
	 *         <code>false</code> otherwise
	 */
	@Override
	public boolean hasArticle(long groupId, String articleId) {
		JournalArticle article = fetchArticle(groupId, articleId);

		if (article != null) {
			return true;
		}

		return false;
	}

	/**
	 * Returns <code>true</code> if the web content article, specified by group
	 * and article ID, is the latest version.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  articleId the primary key of the web content article
	 * @param  version the web content article's version
	 * @return <code>true</code> if the specified web content article is the
	 *         latest version; <code>false</code> otherwise
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public boolean isLatestVersion(
			long groupId, String articleId, double version)
		throws PortalException {

		if (getLatestVersion(groupId, articleId) == version) {
			return true;
		}

		return false;
	}

	/**
	 * Returns <code>true</code> if the web content article, specified by group,
	 * article ID, and workflow status, is the latest version.
	 *
	 * @param  groupId the primary key of the web content article's group
	 * @param  articleId the primary key of the web content article
	 * @param  version the web content article's version
	 * @param  status the web content article's workflow status. For more
	 *         information see {@link WorkflowConstants} for constants starting
	 *         with the "STATUS_" prefix.
	 * @return <code>true</code> if the specified web content article is the
	 *         latest version; <code>false</code> otherwise
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public boolean isLatestVersion(
			long groupId, String articleId, double version, int status)
		throws PortalException {

		if (getLatestVersion(groupId, articleId, status) == version) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isListable(JournalArticle article) {
		if ((article != null) && article.isIndexable()) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isRenderable(
		JournalArticle article, PortletRequestModel portletRequestModel,
		ThemeDisplay themeDisplay) {

		try {
			getArticleDisplay(
				article, article.getDDMTemplateKey(), Constants.VIEW,
				article.getDefaultLanguageId(), 0, portletRequestModel,
				themeDisplay, true);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return false;
		}

		return true;
	}

	/**
	 * Moves the web content article matching the group and article ID to a new
	 * folder.
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
	 *         {@link Constants#UPDATE}, the invocation is considered a web
	 *         content update activity; otherwise it is considered a web content
	 *         add activity.
	 * @return the updated web content article, which was moved to a new folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public JournalArticle moveArticle(
			long groupId, String articleId, long newFolderId,
			ServiceContext serviceContext)
		throws PortalException {

		JournalArticle latestArticle = getLatestArticle(groupId, articleId);

		validateDDMStructureId(
			groupId, newFolderId, latestArticle.getDDMStructureId());

		List<JournalArticle> articles = journalArticlePersistence.findByG_A(
			groupId, articleId);

		String treePath = null;

		for (JournalArticle article : articles) {
			article.setFolderId(newFolderId);

			if (treePath == null) {
				treePath = article.buildTreePath();
			}

			article.setTreePath(treePath);

			journalArticlePersistence.update(article);
		}

		if (serviceContext != null) {
			notifySubscribers(
				serviceContext.getUserId(), latestArticle, "move_from",
				serviceContext);

			latestArticle.setFolderId(newFolderId);
			latestArticle.setTreePath(latestArticle.buildTreePath());

			notifySubscribers(
				serviceContext.getUserId(), latestArticle, "move_to",
				serviceContext);
		}

		return getArticle(groupId, articleId);
	}

	/**
	 * Moves the web content article from the Recycle Bin to a new folder.
	 *
	 * @param  userId the primary key of the user updating the web content
	 *         article
	 * @param  groupId the primary key of the web content article's group
	 * @param  article the web content article
	 * @param  newFolderId the primary key of the web content article's new
	 *         folder
	 * @param  serviceContext the service context to be applied. Can set the
	 *         modification date, portlet preferences, and can set whether to
	 *         add the default command update for the web content article. With
	 *         respect to social activities, by setting the service context's
	 *         command to {@link Constants#UPDATE}, the invocation is considered
	 *         a web content update activity; otherwise it is considered a web
	 *         content add activity.
	 * @return the updated web content article, which was moved from the Recycle
	 *         Bin to a new folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public JournalArticle moveArticleFromTrash(
			long userId, long groupId, JournalArticle article, long newFolderId,
			ServiceContext serviceContext)
		throws PortalException {

		if (!article.isInTrash()) {
			throw new RestoreEntryException(
				RestoreEntryException.INVALID_STATUS);
		}

		if (_trashHelper.isInTrashExplicitly(article)) {
			article = restoreArticleFromTrash(userId, article);
		}
		else {

			// Article

			TrashVersion trashVersion = _trashVersionLocalService.fetchVersion(
				JournalArticle.class.getName(), article.getResourcePrimKey());

			int status = WorkflowConstants.STATUS_APPROVED;

			if (trashVersion != null) {
				status = trashVersion.getStatus();
			}

			article = updateStatus(
				userId, article, status, null, serviceContext, new HashMap<>());

			// Attachments

			for (FileEntry fileEntry : article.getImagesFileEntries()) {
				_portletFileRepository.restorePortletFileEntryFromTrash(
					userId, fileEntry.getFileEntryId());
			}

			// Trash

			if (trashVersion != null) {
				_trashVersionLocalService.deleteTrashVersion(trashVersion);
			}
		}

		return moveArticle(
			groupId, article.getArticleId(), newFolderId, serviceContext);
	}

	/**
	 * Moves the latest version of the web content article matching the group
	 * and article ID to the recycle bin.
	 *
	 * @param  userId the primary key of the user updating the web content
	 *         article
	 * @param  article the web content article
	 * @return the updated web content article, which was moved to the Recycle
	 *         Bin
	 * @throws PortalException if a portal exception occurred
	 */
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public JournalArticle moveArticleToTrash(
			long userId, JournalArticle article)
		throws PortalException {

		// Article

		if (article.isInTrash()) {
			throw new TrashEntryException();
		}

		int oldStatus = article.getStatus();

		List<JournalArticle> articleVersions =
			journalArticlePersistence.findByG_A(
				article.getGroupId(), article.getArticleId());

		articleVersions = ListUtil.sort(
			articleVersions, ArticleVersionComparator.getInstance(false));

		List<ObjectValuePair<Long, Integer>> articleVersionStatusOVPs =
			new ArrayList<>();

		if ((articleVersions != null) && !articleVersions.isEmpty()) {
			articleVersionStatusOVPs = getArticleVersionStatuses(
				articleVersions);
		}

		article = updateStatus(
			userId, article.getId(), WorkflowConstants.STATUS_IN_TRASH,
			new HashMap<>(), new ServiceContext());

		// Trash

		JournalArticleResource articleResource =
			_journalArticleResourceLocalService.getArticleResource(
				article.getResourcePrimKey());

		TrashEntry trashEntry = _trashEntryLocalService.addTrashEntry(
			userId, article.getGroupId(), JournalArticle.class.getName(),
			article.getResourcePrimKey(), articleResource.getUuid(), null,
			oldStatus, articleVersionStatusOVPs,
			UnicodePropertiesBuilder.put(
				"title", article.getArticleId()
			).build());

		String trashArticleId = _trashHelper.getTrashTitle(
			trashEntry.getEntryId());

		for (JournalArticle articleVersion : articleVersions) {
			articleVersion.setArticleId(trashArticleId);
			articleVersion.setStatus(WorkflowConstants.STATUS_IN_TRASH);

			journalArticlePersistence.update(articleVersion);
		}

		articleResource.setArticleId(trashArticleId);

		_journalArticleResourcePersistence.update(articleResource);

		article.setArticleId(trashArticleId);

		article = journalArticlePersistence.update(article);

		// Asset

		_assetEntryLocalService.updateVisible(
			JournalArticle.class.getName(), article.getResourcePrimKey(),
			false);

		// Attachments

		for (FileEntry fileEntry : article.getImagesFileEntries()) {
			_portletFileRepository.movePortletFileEntryToTrash(
				userId, fileEntry.getFileEntryId());
		}

		// Comment

		if (isArticleCommentsEnabled(article.getCompanyId())) {
			_commentManager.moveDiscussionToTrash(
				JournalArticle.class.getName(), article.getResourcePrimKey());
		}

		// Social

		JSONObject extraDataJSONObject = JSONUtil.put(
			"title", article.getTitleMapAsXML());

		SocialActivityManagerUtil.addActivity(
			userId, article, SocialActivityConstants.TYPE_MOVE_TO_TRASH,
			extraDataJSONObject.toString(), 0);

		if (oldStatus == WorkflowConstants.STATUS_PENDING) {
			_workflowInstanceLinkLocalService.deleteWorkflowInstanceLink(
				article.getCompanyId(), article.getGroupId(),
				JournalArticle.class.getName(), article.getId());
		}

		return article;
	}

	/**
	 * Moves the latest version of the web content article matching the group
	 * and article ID to the recycle bin.
	 *
	 * @param  userId the primary key of the user updating the web content
	 *         article
	 * @param  groupId the primary key of the web content article's group
	 * @param  articleId the primary key of the web content article
	 * @return the moved web content article or <code>null</code> if no matching
	 *         article was found
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle moveArticleToTrash(
			long userId, long groupId, String articleId)
		throws PortalException {

		List<JournalArticle> articles = journalArticlePersistence.findByG_A(
			groupId, articleId, 0, 1,
			ArticleVersionComparator.getInstance(false));

		if (articles.isEmpty()) {
			return null;
		}

		return journalArticleLocalService.moveArticleToTrash(
			userId, articles.get(0));
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
	 * @param  companyId the primary key of the web content article's company
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void rebuildTree(long companyId) throws PortalException {
		JournalTreePathUtil.rebuildTree(
			companyId, JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringPool.SLASH, _journalFolderPersistence, this);
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
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public JournalArticle removeArticleLocale(
			long groupId, String articleId, double version, String languageId)
		throws PortalException {

		JournalArticle article = journalArticlePersistence.findByG_A_V(
			groupId, articleId, version);

		if (Objects.equals(languageId, article.getDefaultLanguageId())) {
			throw new RequiredArticleLocalizationException(
				"Default article localization is required");
		}

		JournalArticleLocalization journalArticleLocalization =
			_journalArticleLocalizationPersistence.fetchByC_A_L(
				article.getCompanyId(), article.getId(), languageId);

		if (journalArticleLocalization != null) {
			_journalArticleLocalizationPersistence.removeByC_A_L(
				article.getCompanyId(), article.getId(), languageId);
		}

		_removeArticleLocale(article, languageId);

		FriendlyURLEntry friendlyURLEntry =
			friendlyURLEntryLocalService.fetchFriendlyURLEntry(
				article.getGroupId(), JournalArticle.class,
				article.getUrlTitle());

		if (friendlyURLEntry == null) {
			return article;
		}

		FriendlyURLEntryLocalization friendlyURLEntryLocalization =
			friendlyURLEntryLocalService.fetchFriendlyURLEntryLocalization(
				friendlyURLEntry.getFriendlyURLEntryId(), languageId);

		if (friendlyURLEntryLocalization != null) {
			friendlyURLEntryLocalService.deleteFriendlyURLLocalizationEntry(
				friendlyURLEntry.getFriendlyURLEntryId(), languageId);
		}

		return article;
	}

	/**
	 * Restores the web content article from the Recycle Bin.
	 *
	 * @param  userId the primary key of the user restoring the web content
	 *         article
	 * @param  article the web content article
	 * @return the restored web content article from the Recycle Bin
	 * @throws PortalException if a portal exception occurred
	 */
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public JournalArticle restoreArticleFromTrash(
			long userId, JournalArticle article)
		throws PortalException {

		// Article

		if (!article.isInTrash()) {
			throw new RestoreEntryException(
				RestoreEntryException.INVALID_STATUS);
		}

		String trashArticleId = _trashHelper.getOriginalTitle(
			article.getArticleId());

		List<JournalArticle> articleVersions =
			journalArticlePersistence.findByG_A(
				article.getGroupId(), article.getArticleId());

		for (JournalArticle articleVersion : articleVersions) {
			articleVersion.setArticleId(trashArticleId);

			articleVersion = journalArticlePersistence.update(articleVersion);

			if (article.equals(articleVersion)) {
				article = articleVersion;
			}
		}

		article.setArticleId(trashArticleId);

		article = journalArticlePersistence.update(article);

		JournalArticleResource articleResource =
			_journalArticleResourcePersistence.fetchByPrimaryKey(
				article.getResourcePrimKey());

		articleResource.setArticleId(trashArticleId);

		_journalArticleResourcePersistence.update(articleResource);

		TrashEntry trashEntry = _trashEntryLocalService.getEntry(
			JournalArticle.class.getName(), article.getResourcePrimKey());

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setScopeGroupId(article.getGroupId());

		article = updateStatus(
			userId, article, trashEntry.getStatus(), null, serviceContext,
			new HashMap<>());

		// Trash

		List<TrashVersion> trashVersions =
			_trashVersionLocalService.getVersions(trashEntry.getEntryId());

		boolean visible = false;

		for (TrashVersion trashVersion : trashVersions) {
			JournalArticle trashArticleVersion =
				journalArticlePersistence.findByPrimaryKey(
					trashVersion.getClassPK());

			trashArticleVersion.setStatus(trashVersion.getStatus());

			if (trashEntry.getStatus() == WorkflowConstants.STATUS_APPROVED) {
				visible = true;
			}

			journalArticlePersistence.update(trashArticleVersion);
		}

		_trashEntryLocalService.deleteEntry(
			JournalArticle.class.getName(), article.getResourcePrimKey());

		if (visible) {
			_assetEntryLocalService.updateVisible(
				JournalArticle.class.getName(), article.getResourcePrimKey(),
				true);
		}

		// Comment

		if (isArticleCommentsEnabled(article.getCompanyId())) {
			_commentManager.restoreDiscussionFromTrash(
				JournalArticle.class.getName(), article.getResourcePrimKey());
		}

		// Social

		JSONObject extraDataJSONObject = JSONUtil.put(
			"title", article.getTitleMapAsXML());

		SocialActivityManagerUtil.addActivity(
			userId, article, SocialActivityConstants.TYPE_RESTORE_FROM_TRASH,
			extraDataJSONObject.toString(), 0);

		return article;
	}

	@Override
	public JournalArticle revertArticle(
			long userId, long groupId, String articleId, double version)
		throws PortalException {

		return _copyArticle(
			userId, groupId, articleId, articleId, version, false);
	}

	@Override
	public void setTreePaths(long folderId, String treePath, boolean reindex)
		throws PortalException {

		if (treePath == null) {
			throw new IllegalArgumentException("Tree path is null");
		}

		IndexableActionableDynamicQuery indexableActionableDynamicQuery =
			getIndexableActionableDynamicQuery();

		indexableActionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				Property folderIdProperty = PropertyFactoryUtil.forName(
					"folderId");

				dynamicQuery.add(folderIdProperty.eq(folderId));

				Property treePathProperty = PropertyFactoryUtil.forName(
					"treePath");

				dynamicQuery.add(
					RestrictionsFactoryUtil.or(
						treePathProperty.isNull(),
						treePathProperty.ne(treePath)));
			});

		Indexer<JournalArticle> indexer = IndexerRegistryUtil.getIndexer(
			JournalArticle.class.getName());

		indexableActionableDynamicQuery.setPerformActionMethod(
			(JournalArticle article) -> {
				article.setTreePath(treePath);

				updateJournalArticle(article);

				if (!reindex) {
					return;
				}

				indexableActionableDynamicQuery.setCompanyId(
					article.getCompanyId());

				indexableActionableDynamicQuery.addDocuments(
					indexer.getDocument(article));
			});

		indexableActionableDynamicQuery.performActions();
	}

	/**
	 * Subscribes the user to changes in elements that belong to the web content
	 * article.
	 *
	 * @param  userId the primary key of the user to be subscribed
	 * @param  groupId the primary key of the folder's group
	 * @param  articleId the primary key of the article to subscribe to
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void subscribe(long userId, long groupId, long articleId)
		throws PortalException {

		_subscriptionLocalService.addSubscription(
			userId, groupId, JournalArticle.class.getName(), articleId);
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

		_subscriptionLocalService.addSubscription(
			userId, groupId, DDMStructure.class.getName(), ddmStructureId);
	}

	/**
	 * Unsubscribes the user from changes in elements that belong to the web
	 * content article.
	 *
	 * @param  userId the primary key of the user to be subscribed
	 * @param  groupId the primary key of the folder's group
	 * @param  articleId the primary key of the article to unsubscribe from
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void unsubscribe(long userId, long groupId, long articleId)
		throws PortalException {

		_subscriptionLocalService.deleteSubscription(
			userId, JournalArticle.class.getName(), articleId);
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

		_subscriptionLocalService.deleteSubscription(
			userId, DDMStructure.class.getName(), ddmStructureId);
	}

	/**
	 * Updates the web content article with additional parameters. All
	 * scheduling parameters (display date, expiration date, and review date)
	 * use the current user's timezone.
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
	 * @param  friendlyURLMap the web content article's locales and localized
	 *         friendly URLs
	 * @param  content the HTML content wrapped in XML. For more information,
	 *         see the content example in the {@link #addArticle(String, long,
	 *         long, long, long, long, String, boolean, double, Map, Map, Map,
	 *         String, long, String, String, int, int, int, int, int, int, int,
	 *         int, int, int, boolean, int, int, int, int, int, boolean,
	 *         boolean, boolean, long, int, String, File, Map, String,
	 *         ServiceContext)} description.
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
	 * @param  smallImageFile the web content article's new small image file
	 *         (optionally <code>null</code>). Must pass in
	 *         <code>smallImage</code> value of <code>true</code> to replace the
	 *         article's small image file.
	 * @param  images the web content's images (optionally <code>null</code>)
	 * @param  articleURL the web content article's accessible URL (optionally
	 *         <code>null</code>)
	 * @param  serviceContext the service context to be applied. Can set the
	 *         modification date, expando bridge attributes, asset category IDs,
	 *         asset tag names, asset link entry IDs, asset priority, workflow
	 *         actions, URL title , and can set whether to add the default
	 *         command update for the web content article. With respect to
	 *         social activities, by setting the service context's command to
	 *         {@link Constants#UPDATE}, the invocation is considered a web
	 *         content update activity; otherwise it is considered a web content
	 *         add activity.
	 * @return the updated web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public JournalArticle updateArticle(
			long userId, long groupId, long folderId, String articleId,
			double version, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap,
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
			File smallImageFile, Map<String, byte[]> images, String articleURL,
			ServiceContext serviceContext)
		throws PortalException {

		// Article

		articleId = StringUtil.toUpperCase(StringUtil.trim(articleId));

		byte[] smallImageBytes = null;

		try {
			smallImageBytes = FileUtil.getBytes(smallImageFile);
		}
		catch (IOException ioException) {
			if (_log.isDebugEnabled()) {
				_log.debug(ioException);
			}
		}

		JournalArticle latestArticle = getLatestArticle(
			groupId, articleId, WorkflowConstants.STATUS_ANY);

		JournalArticle article = latestArticle;

		if (friendlyURLMap == null) {
			friendlyURLMap = new HashMap<>();

			for (Map.Entry<Locale, String> entry : titleMap.entrySet()) {
				String title = entry.getValue();

				if (Validator.isNull(title)) {
					continue;
				}

				String urlTitle =
					friendlyURLEntryLocalService.getUniqueUrlTitle(
						groupId,
						_classNameLocalService.getClassNameId(
							JournalArticle.class),
						article.getResourcePrimKey(), title,
						_language.getLanguageId(entry.getKey()));

				friendlyURLMap.put(entry.getKey(), urlTitle);
			}
		}

		boolean imported = ExportImportThreadLocal.isImportInProcess();

		boolean addNewVersion = false;

		if (imported) {
			article = getArticle(groupId, articleId, version);
		}
		else {
			double latestArticleVersion = latestArticle.getVersion();

			if ((version > 0) && (version != latestArticleVersion)) {
				throw new ArticleVersionException(
					StringBundler.concat(
						"Version ", version, " is not the same as ",
						latestArticleVersion));
			}

			serviceContext.validateModifiedDate(
				latestArticle, ArticleVersionException.class);

			if (latestArticle.isApproved() || latestArticle.isExpired() ||
				latestArticle.isScheduled()) {

				addNewVersion = true;

				version = getNextVersion(article);
			}
		}

		User user = _userLocalService.getUser(userId);

		Date displayDate = _portal.getDate(
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, user.getTimeZone(), null);

		if (displayDate == null) {
			displayDate = article.getDisplayDate();

			if ((displayDate != null) && displayDate.before(new Date())) {
				displayDate = article.getDisplayDate();
			}
			else {
				Calendar calendar = CalendarFactoryUtil.getCalendar(
					user.getTimeZone());

				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);

				displayDate = calendar.getTime();
			}
		}

		Date expirationDate = null;
		Date reviewDate = null;

		if (!neverExpire) {
			expirationDate = _portal.getDate(
				expirationDateMonth, expirationDateDay, expirationDateYear,
				expirationDateHour, expirationDateMinute, user.getTimeZone(),
				ArticleExpirationDateException.class);
		}

		if (!neverReview) {
			reviewDate = _portal.getDate(
				reviewDateMonth, reviewDateDay, reviewDateYear, reviewDateHour,
				reviewDateMinute, user.getTimeZone(),
				ArticleReviewDateException.class);
		}

		Date date = new Date();

		boolean expired = false;

		if ((expirationDate != null) && expirationDate.before(date)) {
			expired = true;
		}

		sanitize(
			user.getCompanyId(), groupId, userId, article.getClassPK(),
			descriptionMap);

		boolean validate = !ExportImportThreadLocal.isImportInProcess();

		if (validate) {
			validate(
				user.getCompanyId(), groupId, latestArticle.getClassNameId(),
				titleMap, content, latestArticle.getDDMStructureId(),
				ddmTemplateKey, displayDate, expirationDate, smallImage,
				smallImageURL, smallImageFile, smallImageBytes, serviceContext);

			try {
				validateReferences(
					groupId, folderId, latestArticle.getDDMStructureId(),
					ddmTemplateKey, layoutUuid, smallImage, smallImageURL,
					smallImageBytes, smallImageId, smallImageSource, content);
			}
			catch (ExportImportContentValidationException
						exportImportContentValidationException) {

				exportImportContentValidationException.setStagedModelClassName(
					JournalArticle.class.getName());
				exportImportContentValidationException.
					setStagedModelPrimaryKeyObj(articleId);

				throw exportImportContentValidationException;
			}
		}

		if (addNewVersion) {
			long id = counterLocalService.increment();

			article = journalArticlePersistence.create(id);

			article.setResourcePrimKey(latestArticle.getResourcePrimKey());
			article.setGroupId(latestArticle.getGroupId());
			article.setCompanyId(latestArticle.getCompanyId());
			article.setUserId(latestArticle.getUserId());
			article.setUserName(latestArticle.getUserName());
			article.setCreateDate(latestArticle.getCreateDate());
			article.setExternalReferenceCode(
				latestArticle.getExternalReferenceCode());
			article.setClassNameId(latestArticle.getClassNameId());
			article.setClassPK(latestArticle.getClassPK());
			article.setArticleId(articleId);
			article.setVersion(version);
			article.setDDMStructureId(latestArticle.getDDMStructureId());
			article.setSmallImageId(latestArticle.getSmallImageId());

			serviceContext.setAttribute("version", version);
		}

		Locale locale = getArticleDefaultLocale(content);

		Map<String, String> urlTitleMap = _getURLTitleMap(
			groupId, article.getResourcePrimKey(), friendlyURLMap, titleMap);

		String urlTitle = urlTitleMap.get(LocaleUtil.toLanguageId(locale));

		if (Validator.isNull(urlTitle) &&
			(_classNameLocalService.getClassNameId(DDMStructure.class) !=
				article.getClassNameId())) {

			urlTitle = urlTitleMap.get(
				LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()));

			if (Validator.isNull(urlTitle)) {
				urlTitle = ParamUtil.getString(serviceContext, "urlTitle");

				if (!imported || Validator.isNull(urlTitle)) {
					throw new ArticleFriendlyURLException();
				}
			}
		}

		article.setFolderId(folderId);
		article.setTreePath(article.buildTreePath());
		article.setUrlTitle(urlTitle);
		article.setDDMTemplateKey(ddmTemplateKey);
		article.setDefaultLanguageId(LocaleUtil.toLanguageId(locale));
		article.setLayoutUuid(layoutUuid);
		article.setDisplayDate(displayDate);
		article.setExpirationDate(expirationDate);
		article.setReviewDate(reviewDate);
		article.setIndexable(indexable);
		article.setSmallImage(smallImage);

		if (smallImage) {
			if (smallImageSource ==
					JournalArticleConstants.
						SMALL_IMAGE_SOURCE_DOCUMENTS_AND_MEDIA) {

				article.setSmallImageId(smallImageId);
			}
			else if ((smallImageFile != null) && (smallImageBytes != null) &&
					 (article.getSmallImageId() <= 0)) {

				article.setSmallImageId(counterLocalService.increment());
			}
		}
		else {
			article.setSmallImageId(0);
		}

		if (smallImageSource <= 0) {
			smallImageSource = _getSmallImageSource(smallImage, smallImageURL);
		}

		article.setSmallImageSource(smallImageSource);

		article.setSmallImageURL(smallImageURL);

		if (latestArticle.isPending()) {
			article.setStatus(latestArticle.getStatus());
		}
		else if (!expired) {
			article.setStatus(WorkflowConstants.STATUS_DRAFT);
		}
		else {
			article.setStatus(WorkflowConstants.STATUS_EXPIRED);
		}

		ExpandoBridgeUtil.setExpandoBridgeAttributes(
			latestArticle.getExpandoBridge(), article.getExpandoBridge(),
			serviceContext);

		article.setModifiedDate(serviceContext.getModifiedDate(date));
		article.setStatusByUserId(user.getUserId());
		article.setStatusByUserName(user.getFullName());
		article.setStatusDate(serviceContext.getModifiedDate(date));

		article = journalArticlePersistence.update(article);

		// Friendly URLs

		updateFriendlyURLs(article, urlTitleMap, serviceContext);

		// Article localization

		if (addNewVersion) {
			_addArticleLocalizedFields(
				article.getCompanyId(), article.getId(), titleMap,
				descriptionMap);
		}
		else {
			_updateArticleLocalizedFields(
				article.getCompanyId(), article.getId(), titleMap,
				descriptionMap);
		}

		// Resources

		if (serviceContext.isAddGroupPermissions() ||
			serviceContext.isAddGuestPermissions()) {

			addArticleResources(
				article, serviceContext.isAddGroupPermissions(),
				serviceContext.isAddGuestPermissions());
		}
		else {
			addArticleResources(article, serviceContext.getModelPermissions());
		}

		// Asset

		if (hasModifiedLatestApprovedVersion(groupId, articleId, version)) {
			updateAsset(
				userId, article, serviceContext.getAssetCategoryIds(),
				serviceContext.getAssetTagNames(),
				serviceContext.getAssetLinkEntryIds(),
				serviceContext.getAssetPriority());

			_assetDisplayPageEntryFormProcessor.process(
				JournalArticle.class.getName(), article.getResourcePrimKey(),
				serviceContext);
		}

		// Dynamic data mapping

		updateDDMFields(article, content, groupId, user);

		if (_classNameLocalService.getClassNameId(DDMStructure.class) !=
				article.getClassNameId()) {

			updateDDMLinks(
				article.getId(), groupId, article.getDDMStructureId(),
				ddmTemplateKey, addNewVersion);
		}

		// Small image

		saveImages(
			article.getCompanyId(), smallImage, article.getSmallImageId(),
			smallImageFile, smallImageBytes);

		// Email and workflow

		if (expired && imported) {
			article = updateStatus(
				userId, article, article.getStatus(), articleURL,
				serviceContext, new HashMap<>());
		}

		if (serviceContext.getWorkflowAction() ==
				WorkflowConstants.ACTION_PUBLISH) {

			articleURL = buildArticleURL(
				articleURL, groupId, folderId, articleId);

			serviceContext.setAttribute("articleURL", articleURL);

			serviceContext.setCommand(Constants.UPDATE);

			sendEmail(article, articleURL, "requested", serviceContext);

			startWorkflowInstance(userId, article, serviceContext);
		}

		return article;
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
	 *         see the content example in the {@link #addArticle(String, long,
	 *         long, long, long, long, String, boolean, double, Map, Map, Map,
	 *         String, long, String, String, int, int, int, int, int, int, int,
	 *         int, int, int, boolean, int, int, int, int, int, boolean,
	 *         boolean, boolean, long, int, String, File, Map, String,
	 *         ServiceContext)} description.
	 * @param  layoutUuid the unique string identifying the web content
	 *         article's display page
	 * @param  serviceContext the service context to be applied. Can set the
	 *         modification date, expando bridge attributes, asset category IDs,
	 *         asset tag names, asset link entry IDs, asset priority, workflow
	 *         actions, URL title, and can set whether to add the default
	 *         command update for the web content article. With respect to
	 *         social activities, by setting the service context's command to
	 *         {@link Constants#UPDATE}, the invocation is considered a web
	 *         content update activity; otherwise it is considered a web content
	 *         add activity.
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

		User user = _userLocalService.getUser(userId);

		JournalArticle article = journalArticlePersistence.findByG_A_V(
			groupId, articleId, version);

		Date displayDate = article.getDisplayDate();

		int displayDateMonth = 0;
		int displayDateDay = 0;
		int displayDateYear = 0;
		int displayDateHour = 0;
		int displayDateMinute = 0;

		if (displayDate != null) {
			Calendar displayCal = CalendarFactoryUtil.getCalendar(
				user.getTimeZone());

			displayCal.setTime(displayDate);

			displayDateMonth = displayCal.get(Calendar.MONTH);
			displayDateDay = displayCal.get(Calendar.DATE);
			displayDateYear = displayCal.get(Calendar.YEAR);
			displayDateHour = displayCal.get(Calendar.HOUR);
			displayDateMinute = displayCal.get(Calendar.MINUTE);

			if (displayCal.get(Calendar.AM_PM) == Calendar.PM) {
				displayDateHour += 12;
			}
		}

		Date expirationDate = article.getExpirationDate();

		int expirationDateMonth = 0;
		int expirationDateDay = 0;
		int expirationDateYear = 0;
		int expirationDateHour = 0;
		int expirationDateMinute = 0;
		boolean neverExpire = true;

		if (expirationDate != null) {
			Calendar expirationCal = CalendarFactoryUtil.getCalendar(
				user.getTimeZone());

			expirationCal.setTime(expirationDate);

			expirationDateMonth = expirationCal.get(Calendar.MONTH);
			expirationDateDay = expirationCal.get(Calendar.DATE);
			expirationDateYear = expirationCal.get(Calendar.YEAR);
			expirationDateHour = expirationCal.get(Calendar.HOUR);
			expirationDateMinute = expirationCal.get(Calendar.MINUTE);

			neverExpire = false;

			if (expirationCal.get(Calendar.AM_PM) == Calendar.PM) {
				expirationDateHour += 12;
			}
		}

		Date reviewDate = article.getReviewDate();

		int reviewDateMonth = 0;
		int reviewDateDay = 0;
		int reviewDateYear = 0;
		int reviewDateHour = 0;
		int reviewDateMinute = 0;
		boolean neverReview = true;

		if (reviewDate != null) {
			Calendar reviewCal = CalendarFactoryUtil.getCalendar(
				user.getTimeZone());

			reviewCal.setTime(reviewDate);

			reviewDateMonth = reviewCal.get(Calendar.MONTH);
			reviewDateDay = reviewCal.get(Calendar.DATE);
			reviewDateYear = reviewCal.get(Calendar.YEAR);
			reviewDateHour = reviewCal.get(Calendar.HOUR);
			reviewDateMinute = reviewCal.get(Calendar.MINUTE);

			neverReview = false;

			if (reviewCal.get(Calendar.AM_PM) == Calendar.PM) {
				reviewDateHour += 12;
			}
		}

		return journalArticleLocalService.updateArticle(
			userId, groupId, folderId, articleId, version, titleMap,
			descriptionMap, article.getFriendlyURLMap(), content,
			article.getDDMTemplateKey(), layoutUuid, displayDateMonth,
			displayDateDay, displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			reviewDateMonth, reviewDateDay, reviewDateYear, reviewDateHour,
			reviewDateMinute, neverReview, article.isIndexable(),
			article.isSmallImage(), article.getSmallImageId(),
			article.getSmallImageSource(), article.getSmallImageURL(), null,
			null, null, serviceContext);
	}

	/**
	 * Updates the web content article matching the version, replacing its
	 * folder and content.
	 *
	 * @param  userId the primary key of the user updating the web content
	 *         article
	 * @param  groupId the primary key of the web content article's group
	 * @param  folderId the primary key of the web content article folder
	 * @param  articleId the primary key of the web content article
	 * @param  version the web content article's version
	 * @param  content the HTML content wrapped in XML. For more information,
	 *         see the content example in the {@link #addArticle(String, long,
	 *         long, long, long, long, String, boolean, double, Map, Map, Map,
	 *         String, long, String, String, int, int, int, int, int, int, int,
	 *         int, int, int, boolean, int, int, int, int, int, boolean,
	 *         boolean, boolean, long, int, String, File, Map, String,
	 *         ServiceContext)} description.
	 * @param  serviceContext the service context to be applied. Can set the
	 *         modification date, expando bridge attributes, asset category IDs,
	 *         asset tag names, asset link entry IDs, asset priority, workflow
	 *         actions, URL title, and can set whether to add the default
	 *         command update for the web content article. With respect to
	 *         social activities, by setting the service context's command to
	 *         {@link Constants#UPDATE}, the invocation is considered a web
	 *         content update activity; otherwise it is considered a web content
	 *         add activity.
	 * @return the updated web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle updateArticle(
			long userId, long groupId, long folderId, String articleId,
			double version, String content, ServiceContext serviceContext)
		throws PortalException {

		JournalArticle article = journalArticlePersistence.findByG_A_V(
			groupId, articleId, version);

		return journalArticleLocalService.updateArticle(
			userId, groupId, folderId, articleId, version,
			article.getTitleMap(), article.getDescriptionMap(), content,
			article.getLayoutUuid(), serviceContext);
	}

	/**
	 * Updates the URL title of the web content article.
	 *
	 * @param  id the primary key of the web content article
	 * @param  urlTitle the web content article's URL title
	 * @return the updated web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public JournalArticle updateArticle(long id, String urlTitle)
		throws PortalException {

		JournalArticle article = journalArticlePersistence.findByPrimaryKey(id);

		article.setUrlTitle(urlTitle);

		return journalArticlePersistence.update(article);
	}

	@Override
	public JournalArticle updateArticleDefaultValues(
			long userId, long groupId, String articleId,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			String content, String ddmTemplateKey, String layoutUuid,
			int displayDateMonth, int displayDateDay, int displayDateYear,
			int displayDateHour, int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, int reviewDateMonth, int reviewDateDay,
			int reviewDateYear, int reviewDateHour, int reviewDateMinute,
			boolean neverReview, boolean indexable, boolean smallImage,
			long smallImageId, int smallImageSource, String smallImageURL,
			File smallImageFile, ServiceContext serviceContext)
		throws PortalException {

		// Article

		User user = _userLocalService.getUser(userId);
		articleId = StringUtil.toUpperCase(StringUtil.trim(articleId));

		byte[] smallImageBytes = null;

		try {
			smallImageBytes = FileUtil.getBytes(smallImageFile);
		}
		catch (IOException ioException) {
			if (_log.isDebugEnabled()) {
				_log.debug(ioException);
			}
		}

		Date displayDate = _portal.getDate(
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, user.getTimeZone(), null);

		Date expirationDate = null;
		Date reviewDate = null;

		if (!neverExpire) {
			expirationDate = _portal.getDate(
				expirationDateMonth, expirationDateDay, expirationDateYear,
				expirationDateHour, expirationDateMinute, user.getTimeZone(),
				ArticleExpirationDateException.class);
		}

		if (!neverReview) {
			reviewDate = _portal.getDate(
				reviewDateMonth, reviewDateDay, reviewDateYear, reviewDateHour,
				reviewDateMinute, user.getTimeZone(),
				ArticleReviewDateException.class);
		}

		JournalArticle article = getArticle(groupId, articleId);

		if ((article.getClassNameId() > 0) &&
			(displayDateMonth == displayDateDay) &&
			(displayDateDay == displayDateYear) && (displayDateYear == 0)) {

			displayDate = null;
		}

		serviceContext.validateModifiedDate(
			article, ArticleVersionException.class);

		sanitize(
			user.getCompanyId(), groupId, userId, article.getClassPK(),
			descriptionMap);

		validate(
			user.getCompanyId(), groupId, article.getClassNameId(), titleMap,
			content, article.getDDMStructureId(), ddmTemplateKey, displayDate,
			expirationDate, smallImage, smallImageURL, smallImageFile,
			smallImageBytes, serviceContext);

		_updateArticleLocalizedFields(
			article.getCompanyId(), article.getId(), titleMap, descriptionMap);

		article.setDDMTemplateKey(ddmTemplateKey);

		Locale locale = getArticleDefaultLocale(content);

		article.setDefaultLanguageId(LocaleUtil.toLanguageId(locale));

		article.setLayoutUuid(layoutUuid);
		article.setDisplayDate(displayDate);
		article.setExpirationDate(expirationDate);
		article.setReviewDate(reviewDate);
		article.setIndexable(indexable);
		article.setSmallImage(smallImage);

		if (smallImage) {
			if (smallImageSource ==
					JournalArticleConstants.
						SMALL_IMAGE_SOURCE_DOCUMENTS_AND_MEDIA) {

				article.setSmallImageId(smallImageId);
			}
			else if ((smallImageFile != null) && (smallImageBytes != null) &&
					 (article.getSmallImageId() <= 0)) {

				article.setSmallImageId(counterLocalService.increment());
			}
		}
		else {
			article.setSmallImageId(0);
		}

		if (smallImageSource <= 0) {
			smallImageSource = _getSmallImageSource(smallImage, smallImageURL);
		}

		article.setSmallImageSource(smallImageSource);

		article.setSmallImageURL(smallImageURL);
		article.setStatus(WorkflowConstants.STATUS_APPROVED);

		ExpandoBridgeUtil.setExpandoBridgeAttributes(
			article.getExpandoBridge(), article.getExpandoBridge(),
			serviceContext);

		article = journalArticlePersistence.update(article);

		// Asset

		updateAsset(
			userId, article, serviceContext.getAssetCategoryIds(),
			serviceContext.getAssetTagNames(),
			serviceContext.getAssetLinkEntryIds(),
			serviceContext.getAssetPriority());

		// Dynamic data mapping

		DDMFormValues ddmFormValues = updateDDMFields(
			article, content, groupId, user);

		_updateDDMStructurePredefinedValues(
			userId, article.getDDMStructureId(), ddmFormValues, serviceContext);

		// Small image

		saveImages(
			article.getCompanyId(), smallImage, article.getSmallImageId(),
			smallImageFile, smallImageBytes);

		return journalArticlePersistence.findByPrimaryKey(article.getId());
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
	 *         see the content example in the {@link #addArticle(String, long,
	 *         long, long, long, long, String, boolean, double, Map, Map, Map,
	 *         String, long, String, String, int, int, int, int, int, int, int,
	 *         int, int, int, boolean, int, int, int, int, int, boolean,
	 *         boolean, boolean, long, int, String, File, Map, String,
	 *         ServiceContext)} description.
	 * @param  images the web content's images
	 * @param  serviceContext the service context to be applied. Can set the
	 *         modification date and URL title for the web content article.
	 * @return the updated web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public JournalArticle updateArticleTranslation(
			long groupId, String articleId, double version, Locale locale,
			String title, String description, String content,
			Map<String, byte[]> images, ServiceContext serviceContext)
		throws PortalException {

		validateContent(content);

		JournalArticle oldArticle = getLatestArticle(
			groupId, articleId, WorkflowConstants.STATUS_ANY);

		double oldVersion = oldArticle.getVersion();

		if ((version > 0) && (version != oldVersion)) {
			throw new ArticleVersionException(
				StringBundler.concat(
					"Version ", version, " is not the same as ", oldVersion));
		}

		boolean incrementVersion = false;

		if (oldArticle.isApproved() || oldArticle.isExpired()) {
			incrementVersion = true;
		}

		if (serviceContext != null) {
			serviceContext.validateModifiedDate(
				oldArticle, ArticleVersionException.class);
		}

		JournalArticle article = null;

		User user = _userLocalService.fetchUser(oldArticle.getUserId());

		if (user == null) {
			user = _userLocalService.getGuestUser(oldArticle.getCompanyId());
		}

		if (incrementVersion) {
			double newVersion = getNextVersion(oldArticle);

			long id = counterLocalService.increment();

			article = journalArticlePersistence.create(id);

			article.setResourcePrimKey(oldArticle.getResourcePrimKey());
			article.setGroupId(oldArticle.getGroupId());
			article.setCompanyId(oldArticle.getCompanyId());
			article.setUserId(oldArticle.getUserId());
			article.setUserName(user.getFullName());
			article.setCreateDate(oldArticle.getCreateDate());
			article.setExternalReferenceCode(
				oldArticle.getExternalReferenceCode());
			article.setFolderId(oldArticle.getFolderId());
			article.setClassNameId(oldArticle.getClassNameId());
			article.setClassPK(oldArticle.getClassPK());
			article.setArticleId(articleId);
			article.setVersion(newVersion);
			article.setUrlTitle(
				getUniqueUrlTitle(
					id, groupId, articleId, title, oldArticle.getUrlTitle(),
					serviceContext));
			article.setDDMStructureId(oldArticle.getDDMStructureId());
			article.setDDMTemplateKey(oldArticle.getDDMTemplateKey());
			article.setDefaultLanguageId(
				LocaleUtil.toLanguageId(getArticleDefaultLocale(content)));
			article.setLayoutUuid(oldArticle.getLayoutUuid());
			article.setDisplayDate(oldArticle.getDisplayDate());
			article.setExpirationDate(oldArticle.getExpirationDate());
			article.setReviewDate(oldArticle.getReviewDate());
			article.setIndexable(oldArticle.isIndexable());
			article.setSmallImage(oldArticle.isSmallImage());
			article.setSmallImageId(oldArticle.getSmallImageId());

			if (article.getSmallImageId() == 0) {
				article.setSmallImageId(counterLocalService.increment());
			}

			article.setSmallImageSource(oldArticle.getSmallImageSource());
			article.setSmallImageURL(oldArticle.getSmallImageURL());
			article.setStatus(WorkflowConstants.STATUS_DRAFT);

			User statusUser = _userLocalService.fetchUser(
				serviceContext.getUserId());

			if (statusUser == null) {
				statusUser = _userLocalService.getGuestUser(
					oldArticle.getCompanyId());
			}

			article.setStatusByUserId(statusUser.getUserId());
			article.setStatusByUserName(statusUser.getFullName());
			article.setStatusDate(new Date());

			ExpandoBridgeUtil.copyExpandoBridgeAttributes(
				oldArticle.getExpandoBridge(), article.getExpandoBridge());

			// Article localization

			_addArticleLocalizedFields(
				article.getCompanyId(), article.getId(),
				oldArticle.getTitleMap(), oldArticle.getDescriptionMap());
		}
		else {
			article = oldArticle;
		}

		_updateArticleLocalizedFields(
			article.getCompanyId(), article.getId(), title, description,
			LocaleUtil.toLanguageId(locale));

		article = journalArticlePersistence.update(article);

		// Dynamic data mapping

		updateDDMFields(article, content, groupId, user);

		if (incrementVersion) {
			updateDDMLinks(
				article.getId(), groupId, oldArticle.getDDMStructureId(),
				oldArticle.getDDMTemplateKey(), true);
		}

		return article;
	}

	/**
	 * Updates the web content article's asset with the new asset categories,
	 * tag names, and link entries, removing and adding them as necessary.
	 *
	 * @param  userId the primary key of the user updating the web content
	 *         article's asset
	 * @param  article the web content article
	 * @param  assetCategoryIds the primary keys of the new asset categories
	 * @param  assetTagNames the new asset tag names
	 * @param  assetLinkEntryIds the primary keys of the new asset link entries
	 * @param  priority the priority of the asset
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void updateAsset(
			long userId, JournalArticle article, long[] assetCategoryIds,
			String[] assetTagNames, long[] assetLinkEntryIds, Double priority)
		throws PortalException {

		boolean visible = article.isApproved();

		if (article.getClassNameId() !=
				JournalArticleConstants.CLASS_NAME_ID_DEFAULT) {

			visible = false;
		}

		boolean addDraftAssetEntry = _addDraftAssetEntry(article);

		AssetEntry assetEntry = null;

		String title = article.getTitleMapAsXML();
		String description = article.getDescriptionMapAsXML();

		if (addDraftAssetEntry) {
			assetEntry = _assetEntryLocalService.updateEntry(
				userId, article.getGroupId(), article.getCreateDate(),
				article.getModifiedDate(), JournalArticle.class.getName(),
				article.getPrimaryKey(), article.getUuid(),
				article.getDDMStructureId(), assetCategoryIds, assetTagNames,
				isListable(article), false, null, null, null,
				article.getExpirationDate(), ContentTypes.TEXT_HTML, title,
				description, description, null, article.getLayoutUuid(), 0, 0,
				priority);
		}
		else {
			JournalArticleResource journalArticleResource =
				_journalArticleResourceLocalService.getArticleResource(
					article.getResourcePrimKey());

			Date publishDate = null;

			if (article.isApproved()) {
				publishDate = article.getDisplayDate();
			}

			assetEntry = _assetEntryLocalService.updateEntry(
				userId, article.getGroupId(), article.getCreateDate(),
				article.getModifiedDate(), JournalArticle.class.getName(),
				journalArticleResource.getResourcePrimKey(),
				journalArticleResource.getUuid(), article.getDDMStructureId(),
				assetCategoryIds, assetTagNames, isListable(article), visible,
				null, null, publishDate, article.getExpirationDate(),
				ContentTypes.TEXT_HTML, title, description, description, null,
				article.getLayoutUuid(), 0, 0, priority);
		}

		_assetLinkLocalService.updateLinks(
			userId, assetEntry.getEntryId(), assetLinkEntryIds,
			AssetLinkConstants.TYPE_RELATED);
	}

	/**
	 * Updates the web content articles matching the group, class name ID, and
	 * DDM template key, replacing the DDM template key with a new one.
	 *
	 * @param groupId the primary key of the web content article's group
	 * @param classNameId the primary key of the DDMStructure class if the web
	 *        content article is related to a DDM structure, the primary key of
	 *        the class name associated with the article, or
	 *        JournalArticleConstants.CLASS_NAME_ID_DEFAULT in the journal-api
	 *        module otherwise
	 * @param oldDDMTemplateKey the primary key of the web content article's old
	 *        DDM template
	 * @param newDDMTemplateKey the primary key of the web content article's new
	 *        DDM template
	 */
	@Override
	public void updateDDMTemplateKey(
		long groupId, long classNameId, String oldDDMTemplateKey,
		String newDDMTemplateKey) {

		List<JournalArticle> articles =
			journalArticlePersistence.findByG_C_DDMTK(
				groupId, classNameId, oldDDMTemplateKey);

		for (JournalArticle article : articles) {
			article.setDDMTemplateKey(newDDMTemplateKey);

			journalArticlePersistence.update(article);
		}
	}

	/**
	 * Updates the workflow status of the web content article.
	 *
	 * @param  userId the primary key of the user updating the web content
	 *         article's status
	 * @param  article the web content article
	 * @param  status the web content article's workflow status. For more
	 *         information see {@link WorkflowConstants} for constants starting
	 *         with the "STATUS_" prefix.
	 * @param  articleURL the web content article's accessible URL
	 * @param  serviceContext the service context to be applied. Can set the
	 *         modification date, status date, and portlet preferences. With
	 *         respect to social activities, by setting the service context's
	 *         command to {@link Constants#UPDATE}, the invocation is considered
	 *         a web content update activity; otherwise it is considered a web
	 *         content add activity.
	 * @param  workflowContext the web content article's configured workflow
	 *         context
	 * @return the updated web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public JournalArticle updateStatus(
			long userId, JournalArticle article, int status, String articleURL,
			ServiceContext serviceContext,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		// Article

		User user = _userLocalService.getUser(userId);

		Date date = new Date();

		if ((status == WorkflowConstants.STATUS_APPROVED) &&
			(article.getDisplayDate() != null) &&
			date.before(article.getDisplayDate())) {

			status = WorkflowConstants.STATUS_SCHEDULED;
		}

		int oldStatus = article.getStatus();

		Date modifiedDate = serviceContext.getModifiedDate(date);

		article.setModifiedDate(modifiedDate);

		Date expirationDate = article.getExpirationDate();

		if ((status == WorkflowConstants.STATUS_APPROVED) &&
			(expirationDate != null) && expirationDate.before(date)) {

			article.setExpirationDate(null);
		}

		if ((status == WorkflowConstants.STATUS_EXPIRED) &&
			(expirationDate == null)) {

			article.setExpirationDate(date);
		}

		article.setStatus(status);
		article.setStatusByUserId(user.getUserId());
		article.setStatusByUserName(user.getFullName());
		article.setStatusDate(modifiedDate);

		article = journalArticlePersistence.update(article);

		if (isExpireAllArticleVersions(article.getCompanyId()) &&
			(expirationDate != null) && expirationDate.before(date)) {

			article = setArticlesExpirationDate(article);
		}

		if (hasModifiedLatestApprovedVersion(
				article.getGroupId(), article.getArticleId(),
				article.getVersion())) {

			if (status == WorkflowConstants.STATUS_APPROVED) {
				updateUrlTitles(
					article.getGroupId(), article.getArticleId(),
					article.getUrlTitle());

				// Asset

				String title = article.getTitleMapAsXML();

				if ((oldStatus != WorkflowConstants.STATUS_APPROVED) &&
					(article.getVersion() !=
						JournalArticleConstants.VERSION_DEFAULT)) {

					AssetEntry draftAssetEntry =
						_assetEntryLocalService.fetchEntry(
							JournalArticle.class.getName(),
							article.getPrimaryKey());

					if (draftAssetEntry != null) {
						long[] assetCategoryIds =
							draftAssetEntry.getCategoryIds();
						String[] assetTagNames = draftAssetEntry.getTagNames();

						List<AssetLink> assetLinks =
							_assetLinkLocalService.getDirectLinks(
								draftAssetEntry.getEntryId(),
								AssetLinkConstants.TYPE_RELATED, false);

						long[] assetLinkEntryIds = ListUtil.toLongArray(
							assetLinks, AssetLink.ENTRY_ID2_ACCESSOR);

						String description = article.getDescriptionMapAsXML();

						AssetEntry assetEntry =
							_assetEntryLocalService.updateEntry(
								userId, article.getGroupId(),
								article.getCreateDate(),
								article.getModifiedDate(),
								JournalArticle.class.getName(),
								article.getResourcePrimKey(), article.getUuid(),
								article.getDDMStructureId(), assetCategoryIds,
								assetTagNames, isListable(article), false, null,
								null, null, null, ContentTypes.TEXT_HTML, title,
								description, description, null,
								article.getLayoutUuid(), 0, 0,
								draftAssetEntry.getPriority());

						_assetLinkLocalService.updateLinks(
							userId, assetEntry.getEntryId(), assetLinkEntryIds,
							AssetLinkConstants.TYPE_RELATED);

						_assetEntryLocalService.deleteEntry(
							draftAssetEntry.getEntryId());
					}
				}

				_assetEntryLocalService.updateEntry(
					JournalArticle.class.getName(),
					article.getResourcePrimKey(), article.getDisplayDate(),
					article.getExpirationDate(), isListable(article), true);

				expireMaxVersionArticles(
					article, user.getUserId(), serviceContext, articleURL);

				// Social

				JSONObject extraDataJSONObject = JSONUtil.put("title", title);

				if (serviceContext.isCommandUpdate()) {
					SocialActivityManagerUtil.addActivity(
						user.getUserId(), article,
						JournalActivityKeys.UPDATE_ARTICLE,
						extraDataJSONObject.toString(), 0);
				}
				else {
					SocialActivityManagerUtil.addUniqueActivity(
						user.getUserId(), article,
						JournalActivityKeys.ADD_ARTICLE,
						extraDataJSONObject.toString(), 0);
				}
			}
			else if ((oldStatus == WorkflowConstants.STATUS_APPROVED) &&
					 (status != WorkflowConstants.STATUS_IN_TRASH)) {

				updatePreviousApprovedArticle(article);
			}
		}

		String action = "update";

		if ((oldStatus != WorkflowConstants.STATUS_IN_TRASH) &&
			(status != WorkflowConstants.STATUS_IN_TRASH)) {

			// Email

			if ((oldStatus == WorkflowConstants.STATUS_PENDING) &&
				((status == WorkflowConstants.STATUS_APPROVED) ||
				 (status == WorkflowConstants.STATUS_DENIED))) {

				String msg = "granted";

				if (status == WorkflowConstants.STATUS_DENIED) {
					msg = "denied";
				}

				try {
					articleURL = buildArticleURL(
						articleURL, article.getGroupId(), article.getFolderId(),
						article.getArticleId());

					sendEmail(article, articleURL, msg, serviceContext);
				}
				catch (Exception exception) {
					_log.error(
						StringBundler.concat(
							"Unable to send email to notify the change of ",
							"status to ", msg, " for article ", article.getId(),
							": ", exception.getMessage()));
				}
			}

			// Subscriptions

			if (status == WorkflowConstants.STATUS_EXPIRED) {
				action = "expired";
			}
			else if (article.equals(
						getOldestArticle(
							article.getGroupId(), article.getArticleId()))) {

				action = "add";
			}
		}

		if ((oldStatus != WorkflowConstants.STATUS_IN_TRASH) &&
			(status == WorkflowConstants.STATUS_IN_TRASH)) {

			action = "move_to_trash";
		}
		else if ((oldStatus == WorkflowConstants.STATUS_IN_TRASH) &&
				 (status != WorkflowConstants.STATUS_IN_TRASH)) {

			action = "move_from_trash";
		}

		Group group = _groupLocalService.getGroup(article.getGroupId());

		if ((!group.isStaged() || group.isStagingGroup()) &&
			!((oldStatus == WorkflowConstants.STATUS_PENDING) &&
			  action.equals("move_to_trash"))) {

			notifySubscribers(
				user.getUserId(), article, action, serviceContext);
		}

		return article;
	}

	/**
	 * Updates the workflow status of the web content article matching the class
	 * PK.
	 *
	 * @param  userId the primary key of the user updating the web content
	 *         article's status
	 * @param  classPK the primary key of the DDM structure, if the web content
	 *         article is related to a DDM structure, the primary key of the
	 *         class associated with the article, or <code>0</code> otherwise
	 * @param  status the web content article's workflow status. For more
	 *         information see {@link WorkflowConstants} for constants starting
	 *         with the "STATUS_" prefix.
	 * @param  workflowContext the web content article's configured workflow
	 * @param  serviceContext the service context to be applied. Can set the
	 *         modification date, portlet preferences, and can set whether to
	 *         add the default command update for the web content article.
	 * @return the updated web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle updateStatus(
			long userId, long classPK, int status,
			Map<String, Serializable> workflowContext,
			ServiceContext serviceContext)
		throws PortalException {

		return journalArticleLocalService.updateStatus(
			userId, getArticle(classPK), status, null, serviceContext,
			workflowContext);
	}

	/**
	 * Updates the workflow status of the web content article matching the
	 * group, article ID, and version.
	 *
	 * @param  userId the primary key of the user updating the web content
	 *         article's status
	 * @param  groupId the primary key of the web content article's group
	 * @param  articleId the primary key of the web content article
	 * @param  version the web content article's version
	 * @param  status the web content article's workflow status. For more
	 *         information see {@link WorkflowConstants} for constants starting
	 *         with the "STATUS_" prefix.
	 * @param  articleURL the web content article's accessible URL
	 * @param  workflowContext the web content article's configured workflow
	 * @param  serviceContext the service context to be applied. Can set the
	 *         modification date, portlet preferences, and can set whether to
	 *         add the default command update for the web content article.
	 * @return the updated web content article
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public JournalArticle updateStatus(
			long userId, long groupId, String articleId, double version,
			int status, String articleURL,
			Map<String, Serializable> workflowContext,
			ServiceContext serviceContext)
		throws PortalException {

		JournalArticle article = journalArticlePersistence.findByG_A_V(
			groupId, articleId, version);

		return journalArticleLocalService.updateStatus(
			userId, article, status, articleURL, serviceContext,
			workflowContext);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		JournalArticleImpl.setDDMFormValuesToFieldsConverter(
			_ddmFormValuesToFieldsConverter);
		JournalArticleImpl.setJournalConverter(_journalConverter);

		_serviceTrackerList = ServiceTrackerListFactory.open(
			bundleContext, TransformerListener.class,
			"(jakarta.portlet.name=" + JournalPortletKeys.JOURNAL + ")");
	}

	protected String addImageFileEntries(JournalArticle article, String value)
		throws PortalException {

		if (ExportImportThreadLocal.isImportInProcess()) {
			return value;
		}

		JSONObject valueJSONObject = _jsonFactory.createJSONObject(value);

		FileEntry fileEntry = _getFileEntry(valueJSONObject);

		if (fileEntry == null) {
			return value;
		}

		FileEntry tempFileEntry = null;

		try {
			boolean tempFile = fileEntry.isRepositoryCapabilityProvided(
				TemporaryFileEntriesCapability.class);

			if (tempFile) {
				tempFileEntry = fileEntry;

				Folder folder = article.addImagesFolder();

				FileEntry portletFileEntry =
					_portletFileRepository.
						fetchPortletFileEntryByExternalReferenceCode(
							tempFileEntry.getUuid(), folder.getGroupId());

				if (portletFileEntry == null) {
					String fileEntryName = DLUtil.getUniqueFileName(
						folder.getGroupId(), folder.getFolderId(),
						tempFileEntry.getFileName(), false);

					// See LPD-52357

					fileEntry = _portletFileRepository.addPortletFileEntry(
						tempFileEntry.getUuid(), folder.getGroupId(),
						tempFileEntry.getUserId(),
						JournalArticle.class.getName(),
						article.getResourcePrimKey(),
						JournalConstants.SERVICE_NAME, folder.getFolderId(),
						tempFileEntry.getContentStream(), fileEntryName,
						tempFileEntry.getMimeType(), false);
				}
				else {
					fileEntry = portletFileEntry;
				}
			}

			String previewURL = _dlURLHelper.getPreviewURL(
				fileEntry, fileEntry.getFileVersion(), null, StringPool.BLANK,
				false, true);

			return _toJSON(
				valueJSONObject.getString("alt"), article, fileEntry,
				valueJSONObject.getString("height"),
				valueJSONObject.getString("type"), previewURL,
				valueJSONObject.getString("width"));
		}
		finally {
			FileEntry finalTempFileEntry = tempFileEntry;

			TransactionCommitCallbackUtil.registerCallback(
				() -> {
					if (finalTempFileEntry != null) {
						FileEntry persistedFileEntry =
							_portletFileRepository.fetchPortletFileEntry(
								finalTempFileEntry.getGroupId(),
								finalTempFileEntry.getFolderId(),
								finalTempFileEntry.getFileName());

						if (persistedFileEntry != null) {
							TempFileEntryUtil.deleteTempFileEntry(
								finalTempFileEntry.getFileEntryId());
						}
					}

					return null;
				});
		}
	}

	protected String buildArticleURL(
		String articleURL, long groupId, long folderId, String articleId) {

		String namespace = _portal.getPortletNamespace(
			PortletProviderUtil.getPortletId(
				JournalArticle.class.getName(), PortletProvider.Action.EDIT));

		articleURL = HttpComponentsUtil.addParameter(
			articleURL, namespace + "groupId", groupId);
		articleURL = HttpComponentsUtil.addParameter(
			articleURL, namespace + "folderId", folderId);
		articleURL = HttpComponentsUtil.addParameter(
			articleURL, namespace + "articleId", articleId);

		return articleURL;
	}

	protected void checkArticlesByCompanyIdAndExpirationDate(
			long companyId, Date expirationDate, Date nextExpirationDate)
		throws PortalException {

		IndexableActionableDynamicQuery indexableActionableDynamicQuery =
			getIndexableActionableDynamicQuery();

		Indexer<JournalArticle> indexer = IndexerRegistryUtil.getIndexer(
			JournalArticle.class);

		indexableActionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				Property classNameIdProperty = PropertyFactoryUtil.forName(
					"classNameId");

				dynamicQuery.add(
					classNameIdProperty.eq(
						JournalArticleConstants.CLASS_NAME_ID_DEFAULT));

				Property expirationDateProperty = PropertyFactoryUtil.forName(
					"expirationDate");

				dynamicQuery.add(expirationDateProperty.le(nextExpirationDate));

				dynamicQuery.add(
					RestrictionsFactoryUtil.or(
						RestrictionsFactoryUtil.eq(
							"status", WorkflowConstants.STATUS_APPROVED),
						RestrictionsFactoryUtil.and(
							RestrictionsFactoryUtil.le(
								"displayDate", expirationDate),
							RestrictionsFactoryUtil.eq(
								"status",
								WorkflowConstants.STATUS_SCHEDULED))));
			});
		indexableActionableDynamicQuery.setCompanyId(companyId);
		indexableActionableDynamicQuery.setPerformActionMethod(
			(JournalArticle article) -> {
				try {
					if (_log.isDebugEnabled()) {
						_log.debug("Expiring article " + article.getId());
					}

					if (isExpireAllArticleVersions(companyId)) {
						List<JournalArticle> currentArticles =
							journalArticleLocalService.getArticles(
								article.getGroupId(), article.getArticleId(),
								QueryUtil.ALL_POS, QueryUtil.ALL_POS,
								ArticleVersionComparator.getInstance(true));

						for (JournalArticle currentArticle : currentArticles) {
							if (currentArticle.getVersion() >=
									article.getVersion()) {

								continue;
							}

							currentArticle.setExpirationDate(
								article.getExpirationDate());
							currentArticle.setStatus(
								WorkflowConstants.STATUS_EXPIRED);

							currentArticle = journalArticlePersistence.update(
								currentArticle);

							notifySubscribers(
								0, currentArticle, "expired",
								new ServiceContext());
						}
					}

					article.setStatus(WorkflowConstants.STATUS_EXPIRED);

					article = journalArticleLocalService.updateJournalArticle(
						article);

					sendEmail(
						article, _getArticleURL(article), "expired",
						new ServiceContext());

					notifySubscribers(
						0, article, "expired", new ServiceContext());

					updatePreviousApprovedArticle(article);

					if (indexer != null) {
						indexableActionableDynamicQuery.addDocuments(
							indexer.getDocument(article));
					}
				}
				catch (PortalException portalException) {
					if (_log.isDebugEnabled()) {
						_log.debug(
							"Unable to expire article " + article.getId(),
							portalException);
					}
				}
			});
		indexableActionableDynamicQuery.setTransactionConfig(
			DefaultActionableDynamicQuery.REQUIRES_NEW_TRANSACTION_CONFIG);

		indexableActionableDynamicQuery.performActions();
	}

	protected void checkArticlesByDisplayDate(
			Date displayDate, long checkInterval)
		throws PortalException {

		Date nextExpirationDate = new Date(
			displayDate.getTime() + checkInterval);

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Publishing articles with display date less than ",
					displayDate, " and status ",
					WorkflowConstants.STATUS_SCHEDULED));
		}

		ActionableDynamicQuery actionableDynamicQuery =
			getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				Property displayDateProperty = PropertyFactoryUtil.forName(
					"displayDate");

				dynamicQuery.add(displayDateProperty.lt(displayDate));

				dynamicQuery.add(
					RestrictionsFactoryUtil.or(
						RestrictionsFactoryUtil.isNull("expirationDate"),
						RestrictionsFactoryUtil.ge(
							"expirationDate", nextExpirationDate)));

				Property statusProperty = PropertyFactoryUtil.forName("status");

				dynamicQuery.add(
					statusProperty.eq(WorkflowConstants.STATUS_SCHEDULED));
			});
		actionableDynamicQuery.setPerformActionMethod(
			(JournalArticle article) -> {
				try {
					if (_log.isDebugEnabled()) {
						_log.debug("Publishing article " + article.getId());
					}

					long userId = _portal.getValidUserId(
						article.getCompanyId(), article.getStatusByUserId());

					ServiceContext serviceContext = new ServiceContext();

					serviceContext.setCommand(Constants.UPDATE);
					serviceContext.setScopeGroupId(article.getGroupId());

					journalArticleLocalService.updateStatus(
						userId, article.getId(),
						WorkflowConstants.STATUS_APPROVED, new HashMap<>(),
						serviceContext);
				}
				catch (PortalException portalException) {
					if (_log.isDebugEnabled()) {
						_log.debug(
							"Unable to publish article " + article.getId(),
							portalException);
					}
				}
			});
		actionableDynamicQuery.setTransactionConfig(
			DefaultActionableDynamicQuery.REQUIRES_NEW_TRANSACTION_CONFIG);

		actionableDynamicQuery.performActions();
	}

	protected void checkArticlesByExpirationDate(
			long companyId, Date expirationDate, long checkInterval)
		throws PortalException {

		Date nextExpirationDate = new Date(
			expirationDate.getTime() + checkInterval);

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Expiring articles with expiration date less than or ",
					"equal to ", nextExpirationDate, " and status ",
					WorkflowConstants.STATUS_APPROVED));
		}

		checkArticlesByCompanyIdAndExpirationDate(
			companyId, expirationDate, nextExpirationDate);

		_companyIdPreviousCheckDate.computeIfAbsent(
			companyId,
			key -> new Date(expirationDate.getTime() - checkInterval));
	}

	protected void checkArticlesByReviewDate(long companyId, Date reviewDate)
		throws PortalException {

		Date previousCheckDate = _companyIdPreviousCheckDate.get(companyId);

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Sending review notification for articles with reviewDate ",
					"between ", previousCheckDate, " and ", reviewDate));
		}

		List<JournalArticle> articles = getArticlesByReviewDate(
			companyId, previousCheckDate, reviewDate);

		for (JournalArticle article : articles) {
			if (article.isInTrash() ||
				!journalArticleLocalService.isLatestVersion(
					article.getGroupId(), article.getArticleId(),
					article.getVersion())) {

				continue;
			}

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Sending review notification for article " +
						article.getId());
			}

			sendEmail(
				article, _getArticleURL(article), "review",
				new ServiceContext());

			notifySubscribers(0, article, "review", new ServiceContext());
		}
	}

	protected DDMFormValues copyArticleImages(
		JournalArticle sourceArticle, JournalArticle targetArticle) {

		DDMFormValues ddmFormValues = sourceArticle.getDDMFormValues();

		try {
			Folder folder = targetArticle.addImagesFolder();

			_copyArticleImages(
				targetArticle, ddmFormValues.getDDMFormFieldValues(),
				sourceArticle.getGroupId(), folder.getFolderId());
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return ddmFormValues;
	}

	@Deactivate
	@Override
	protected void deactivate() {
		super.deactivate();

		JournalArticleImpl.setDDMFormValuesToFieldsConverter(null);
		JournalArticleImpl.setJournalConverter(null);

		_serviceTrackerList.close();
	}

	protected void expireMaxVersionArticles(
			JournalArticle article, long userId, ServiceContext serviceContext,
			String articleURL)
		throws PortalException {

		int journalArticleMaxVersionCount = getJournalArticleMaxVersionCount();

		if (journalArticleMaxVersionCount <= 0) {
			return;
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Expiring oldest articles past the maximum version limit");
		}

		List<JournalArticle> articles = journalArticlePersistence.findByG_A_ST(
			article.getGroupId(), article.getArticleId(),
			WorkflowConstants.STATUS_APPROVED);

		for (int i = journalArticleMaxVersionCount; i < articles.size(); i++) {
			JournalArticle curArticle = articles.get(i);

			expireArticle(
				userId, curArticle.getGroupId(), curArticle.getArticleId(),
				curArticle.getVersion(), articleURL, serviceContext);
		}
	}

	protected JournalArticle fetchLatestLiveArticle(JournalArticle article)
		throws PortalException {

		Group group = _groupLocalService.getGroup(article.getGroupId());

		long liveGroupId = group.getLiveGroupId();

		if (liveGroupId == 0) {
			return null;
		}

		JournalArticleResource articleResource =
			_journalArticleResourceLocalService.
				fetchJournalArticleResourceByUuidAndGroupId(
					article.getArticleResourceUuid(), liveGroupId);

		if (articleResource == null) {
			return null;
		}

		return journalArticleLocalService.fetchLatestArticle(
			articleResource.getResourcePrimKey(), WorkflowConstants.STATUS_ANY,
			false);
	}

	protected void format(
			User user, long groupId, JournalArticle article,
			List<DDMFormFieldValue> ddmFormFieldValues)
		throws PortalException {

		for (DDMFormFieldValue ddmFormFieldValue : ddmFormFieldValues) {
			if (ListUtil.isNotEmpty(
					ddmFormFieldValue.getNestedDDMFormFieldValues())) {

				format(
					user, groupId, article,
					ddmFormFieldValue.getNestedDDMFormFieldValues());
			}

			if (Objects.equals(
					ddmFormFieldValue.getType(),
					DDMFormFieldTypeConstants.FIELDSET)) {

				continue;
			}

			Value oldValue = ddmFormFieldValue.getValue();

			if (oldValue == null) {
				continue;
			}

			Value newValue = new LocalizedValue();

			newValue.setDefaultLocale(oldValue.getDefaultLocale());

			for (Locale locale : oldValue.getAvailableLocales()) {
				String content = oldValue.getString(locale);

				if (Validator.isNull(content)) {
					newValue.addString(locale, content);

					continue;
				}

				if (Objects.equals(
						ddmFormFieldValue.getType(),
						DDMFormFieldTypeConstants.IMAGE)) {

					content = addImageFileEntries(article, content);
				}
				else if (Objects.equals(
							ddmFormFieldValue.getType(),
							DDMFormFieldTypeConstants.RICH_TEXT) ||
						 Objects.equals(
							 ddmFormFieldValue.getType(),
							 DDMFormFieldTypeConstants.TEXT)) {

					String contentType = ContentTypes.TEXT_PLAIN;

					if (Objects.equals(
							ddmFormFieldValue.getType(),
							DDMFormFieldTypeConstants.RICH_TEXT)) {

						contentType = ContentTypes.TEXT_HTML;
					}

					content = SanitizerUtil.sanitize(
						user.getCompanyId(), groupId, user.getUserId(),
						JournalArticle.class.getName(), 0, contentType,
						content);
				}

				newValue.addString(locale, content);
			}

			ddmFormFieldValue.setValue(newValue);
		}
	}

	protected long getArticleCheckInterval(long companyId) {
		try {
			JournalServiceConfiguration journalServiceConfiguration =
				configurationProvider.getCompanyConfiguration(
					JournalServiceConfiguration.class, companyId);

			return journalServiceConfiguration.checkInterval() * Time.MINUTE;
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}
	}

	protected Locale getArticleDefaultLocale(String content) {
		String defaultLanguageId = _localization.getDefaultLanguageId(content);

		if (Validator.isNotNull(defaultLanguageId)) {
			return LocaleUtil.fromLanguageId(defaultLanguageId);
		}

		return LocaleUtil.getSiteDefault();
	}

	protected JournalArticleDisplay getArticleDisplay(
			JournalArticle article, String ddmTemplateKey, String viewMode,
			String languageId, int page,
			PortletRequestModel portletRequestModel, ThemeDisplay themeDisplay,
			boolean propagateException)
		throws PortalException {

		String content = null;

		if (page < 1) {
			page = 1;
		}

		int numberOfPages = 1;
		boolean paginate = false;

		boolean cacheable = true;

		try {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Transforming ", article.getArticleId(),
						StringPool.SPACE, article.getVersion(),
						StringPool.SPACE, languageId));
			}

			// Try with specified template first (in the current group and the
			// global group). If a template is not specified, use the default
			// one. If the specified template does not exist, use the default
			// one. If the default one does not exist, throw an exception.

			long groupId = _portal.getSiteGroupId(article.getGroupId());

			Group group = _groupLocalService.fetchGroup(groupId);

			if ((group != null) && group.isCompany()) {
				groupId = themeDisplay.getScopeGroupId();
			}

			DDMTemplate ddmTemplate = ddmTemplateLocalService.fetchTemplate(
				groupId,
				_classNameLocalService.getClassNameId(DDMStructure.class),
				ddmTemplateKey, true);

			if ((ddmTemplate == null) &&
				!Objects.equals(article.getDDMTemplateKey(), ddmTemplateKey)) {

				ddmTemplate = ddmTemplateLocalService.fetchTemplate(
					_portal.getSiteGroupId(article.getGroupId()),
					_classNameLocalService.getClassNameId(DDMStructure.class),
					article.getDDMTemplateKey(), true);
			}

			String script = StringPool.BLANK;

			if (ddmTemplate != null) {
				script = ddmTemplate.getScript();
				cacheable = ddmTemplate.isCacheable();
			}
			else {
				DDMStructure ddmStructure = article.getDDMStructure();

				script = _journalDefaultTemplateProvider.getScript(
					ddmStructure.getStructureId());

				cacheable = _journalDefaultTemplateProvider.isCacheable();
			}

			content = _journalTransformer.transform(
				article, ddmTemplate, _journalHelper, languageId,
				_layoutDisplayPageProviderRegistry,
				ListUtil.filter(
					_serviceTrackerList.toList(),
					TransformerListener::isEnabled),
				portletRequestModel, propagateException, script, themeDisplay,
				viewMode);

			JournalServiceConfiguration journalServiceConfiguration =
				configurationProvider.getCompanyConfiguration(
					JournalServiceConfiguration.class, article.getCompanyId());

			String[] pieces = StringUtil.split(
				content,
				journalServiceConfiguration.journalArticlePageBreakToken());

			if (pieces.length > 1) {
				if (page > pieces.length) {
					page = 1;
				}

				content = pieces[page - 1];
				numberOfPages = pieces.length;
				paginate = true;
			}
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}

		return new JournalArticleDisplayImpl(
			article.getCompanyId(), article.getExternalReferenceCode(),
			article.getId(), article.getResourcePrimKey(), article.getGroupId(),
			article.getUserId(), article.getArticleId(), article.getVersion(),
			article.getTitle(languageId), article.getUrlTitle(),
			article.getDescription(languageId),
			article.getAvailableLanguageIds(), content,
			article.getDDMStructureId(), ddmTemplateKey, article.isSmallImage(),
			article.getSmallImageId(), article.getSmallImageURL(),
			article.getArticleImageURL(themeDisplay), numberOfPages, page,
			paginate, cacheable);
	}

	protected List<ObjectValuePair<Long, Integer>> getArticleVersionStatuses(
		List<JournalArticle> articles) {

		return TransformUtil.transform(
			articles,
			article -> {
				int status = article.getStatus();

				if (status == WorkflowConstants.STATUS_PENDING) {
					status = WorkflowConstants.STATUS_DRAFT;
				}

				return new ObjectValuePair<>(article.getId(), status);
			});
	}

	protected JournalArticle getFirstArticle(
			long groupId, String articleId, int status,
			OrderByComparator<JournalArticle> orderByComparator)
		throws PortalException {

		if (status == WorkflowConstants.STATUS_ANY) {
			return journalArticlePersistence.findByG_A_NotST_First(
				groupId, articleId, WorkflowConstants.STATUS_IN_TRASH,
				orderByComparator);
		}

		return journalArticlePersistence.findByG_A_ST_First(
			groupId, articleId, status, orderByComparator);
	}

	protected int getJournalArticleMaxVersionCount() throws PortalException {
		JournalServiceConfiguration journalServiceConfiguration =
			configurationProvider.getCompanyConfiguration(
				JournalServiceConfiguration.class,
				CompanyThreadLocal.getCompanyId());

		return journalServiceConfiguration.journalArticleMaxVersionCount();
	}

	protected JournalGroupServiceConfiguration
			getJournalGroupServiceConfiguration(long groupId)
		throws ConfigurationException {

		return configurationProvider.getConfiguration(
			JournalGroupServiceConfiguration.class,
			new GroupServiceSettingsLocator(
				groupId, JournalConstants.SERVICE_NAME));
	}

	protected double getNextVersion(JournalArticle article)
		throws PortalException {

		double nextVersion = article.getVersion();

		// The next version must be greater than the version of the latest live
		// article

		JournalArticle latestLiveArticle = fetchLatestLiveArticle(article);

		if ((latestLiveArticle != null) &&
			(latestLiveArticle.getVersion() > nextVersion)) {

			nextVersion = latestLiveArticle.getVersion();
		}

		return MathUtil.format(nextVersion + 0.1, 1, 1);
	}

	protected String getUniqueUrlTitle(
			long id, long groupId, String articleId, String title)
		throws PortalException {

		return getUniqueUrlTitle(
			groupId, articleId, JournalUtil.getUrlTitle(id, title));
	}

	protected String getUniqueUrlTitle(
			long id, long groupId, String articleId, String title,
			String oldUrlTitle, ServiceContext serviceContext)
		throws PortalException {

		String serviceContextUrlTitle = ParamUtil.getString(
			serviceContext, "urlTitle");

		String urlTitle = null;

		if (Validator.isNotNull(serviceContextUrlTitle)) {
			urlTitle = JournalUtil.getUrlTitle(id, serviceContextUrlTitle);
		}
		else if (Validator.isNotNull(oldUrlTitle)) {
			return oldUrlTitle;
		}
		else {
			urlTitle = getUniqueUrlTitle(id, groupId, articleId, title);
		}

		JournalArticle urlTitleArticle = fetchArticleByUrlTitle(
			groupId, urlTitle);

		if ((urlTitleArticle != null) &&
			!Objects.equals(urlTitleArticle.getArticleId(), articleId)) {

			urlTitle = getUniqueUrlTitle(id, groupId, articleId, urlTitle);
		}

		return urlTitle;
	}

	protected boolean hasModifiedLatestApprovedVersion(
		long groupId, String articleId, double version) {

		JournalArticle article = fetchLatestArticle(
			groupId, articleId, WorkflowConstants.STATUS_APPROVED);

		if ((article == null) || (article.getVersion() <= version)) {
			return true;
		}

		return false;
	}

	protected boolean isArticleCommentsEnabled(long companyId)
		throws PortalException {

		JournalServiceConfiguration journalServiceConfiguration =
			configurationProvider.getCompanyConfiguration(
				JournalServiceConfiguration.class, companyId);

		return journalServiceConfiguration.articleCommentsEnabled();
	}

	protected boolean isExpireAllArticleVersions(long companyId)
		throws PortalException {

		JournalServiceConfiguration journalServiceConfiguration =
			configurationProvider.getCompanyConfiguration(
				JournalServiceConfiguration.class, companyId);

		return journalServiceConfiguration.expireAllArticleVersionsEnabled();
	}

	protected boolean isReindexAllArticleVersions() {
		try {
			JournalServiceConfiguration journalServiceConfiguration =
				configurationProvider.getCompanyConfiguration(
					JournalServiceConfiguration.class,
					CompanyThreadLocal.getCompanyId());

			return journalServiceConfiguration.indexAllArticleVersionsEnabled();
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return false;
	}

	protected void notifySubscribers(
			long userId, JournalArticle article, String action,
			ServiceContext serviceContext)
		throws PortalException {

		if (article.isPending()) {
			return;
		}

		JournalGroupServiceConfiguration journalGroupServiceConfiguration =
			getJournalGroupServiceConfiguration(article.getGroupId());

		if (action.equals("add") &&
			journalGroupServiceConfiguration.emailArticleAddedEnabled()) {
		}
		else if (action.equals("expired") &&
				 journalGroupServiceConfiguration.
					 emailArticleExpiredEnabled()) {
		}
		else if (action.equals("move_to") &&
				 journalGroupServiceConfiguration.
					 emailArticleMovedToFolderEnabled()) {
		}
		else if (action.equals("move_to_trash") &&
				 journalGroupServiceConfiguration.
					 emailArticleMovedToTrashEnabled()) {
		}
		else if (action.equals("move_from") &&
				 journalGroupServiceConfiguration.
					 emailArticleMovedFromFolderEnabled()) {
		}
		else if (action.equals("move_from_trash") &&
				 journalGroupServiceConfiguration.
					 emailArticleMovedToTrashEnabled()) {
		}
		else if (action.equals("review") &&
				 journalGroupServiceConfiguration.emailArticleReviewEnabled()) {
		}
		else if (action.equals("update") &&
				 journalGroupServiceConfiguration.
					 emailArticleUpdatedEnabled()) {
		}
		else {
			return;
		}

		SubscriptionSender subscriptionSender =
			new GroupSubscriptionCheckSubscriptionSender(
				JournalConstants.RESOURCE_NAME);

		subscriptionSender.setClassName(article.getModelClassName());
		subscriptionSender.setClassPK(article.getId());

		JournalFolder folder = _journalFolderPersistence.fetchByPrimaryKey(
			article.getFolderId());

		subscriptionSender.addPersistedSubscribers(
			JournalFolder.class.getName(), article.getGroupId());

		Group group = _groupLocalService.getGroup(article.getGroupId());

		long liveGroupId = group.getLiveGroupId();

		subscriptionSender.addAssetEntryPersistedSubscribers(
			JournalArticle.class.getName(), article.getResourcePrimKey());

		if (liveGroupId > 0) {
			subscriptionSender.addPersistedSubscribers(
				JournalFolder.class.getName(), liveGroupId, false);
		}

		if (folder != null) {
			subscriptionSender.addPersistedSubscribers(
				JournalFolder.class.getName(), folder.getFolderId());

			for (Long ancestorFolderId : folder.getAncestorFolderIds()) {
				subscriptionSender.addPersistedSubscribers(
					JournalFolder.class.getName(), ancestorFolderId);
			}

			if (liveGroupId > 0) {
				folder = _journalFolderPersistence.fetchByUUID_G(
					folder.getUuid(), liveGroupId);

				if (folder != null) {
					subscriptionSender.addPersistedSubscribers(
						JournalFolder.class.getName(), folder.getFolderId(),
						false);

					for (Long ancestorFolderId :
							folder.getAncestorFolderIds()) {

						subscriptionSender.addPersistedSubscribers(
							JournalFolder.class.getName(), ancestorFolderId,
							false);
					}
				}
			}
		}

		DDMStructure ddmStructure = ddmStructureLocalService.fetchStructure(
			article.getDDMStructureId());

		if (ddmStructure != null) {
			subscriptionSender.addPersistedSubscribers(
				DDMStructure.class.getName(), ddmStructure.getStructureId());
		}

		subscriptionSender.addPersistedSubscribers(
			JournalArticle.class.getName(), article.getResourcePrimKey());

		if (liveGroupId > 0) {
			JournalArticle liveGroupArticle =
				journalArticlePersistence.fetchByUUID_G(
					article.getUuid(), liveGroupId);

			if (liveGroupArticle != null) {
				subscriptionSender.addPersistedSubscribers(
					JournalArticle.class.getName(),
					liveGroupArticle.getResourcePrimKey(), false);
			}
		}

		if (!subscriptionSender.hasSubscribers()) {
			return;
		}

		String fromAddress =
			journalGroupServiceConfiguration.emailFromAddress();

		_populateSubscriptionSender(
			article,
			JournalUtil.getJournalControlPanelLink(
				article.getFolderId(), article.getGroupId(), null),
			action, fromAddress,
			journalGroupServiceConfiguration.emailFromName(),
			journalGroupServiceConfiguration, serviceContext,
			subscriptionSender);

		subscriptionSender.setCurrentUserId(userId);
		subscriptionSender.setEntryTitle(
			article.getTitle(serviceContext.getLanguageId()));
		subscriptionSender.setLocalizedContextAttribute(
			"[$ARTICLE_CONTENT$]",
			new EscapableLocalizableFunction(
				locale -> _getArticleContent(article, locale), false));
		subscriptionSender.setLocalizedContextAttribute(
			"[$ARTICLE_STATUS$]",
			new EscapableLocalizableFunction(
				locale -> _language.get(
					locale,
					WorkflowConstants.getStatusLabel(article.getStatus()))));

		JournalFolder finalFolder = folder;

		subscriptionSender.setLocalizedContextAttribute(
			"[$FOLDER_NAME$]",
			new EscapableLocalizableFunction(
				locale -> _getFolderName(article, locale, finalFolder)));

		subscriptionSender.setNotificationType(_getNotificationType(action));
		subscriptionSender.setReplyToAddress(fromAddress);

		if ((action.equals("expired") || action.equals("review")) &&
			(serviceContext.getUserId() == 0)) {

			subscriptionSender.setSendToCurrentUser(true);
		}

		subscriptionSender.flushNotificationsAsync();
	}

	protected void sanitize(
			long companyId, long groupId, long userId, long classPK,
			Map<Locale, String> descriptionMap)
		throws PortalException {

		if (descriptionMap == null) {
			return;
		}

		for (Map.Entry<Locale, String> entry : descriptionMap.entrySet()) {
			String description = SanitizerUtil.sanitize(
				companyId, groupId, userId, JournalArticle.class.getName(),
				classPK, ContentTypes.TEXT_HTML, entry.getValue());

			descriptionMap.put(entry.getKey(), description);
		}
	}

	protected void saveImages(
			long companyId, boolean smallImage, long smallImageId,
			File smallImageFile, byte[] smallImageBytes)
		throws PortalException {

		if (smallImage) {
			if ((smallImageFile != null) && (smallImageBytes != null)) {
				_imageLocalService.updateImage(
					companyId, smallImageId, smallImageBytes);
			}
		}
		else {
			_imageLocalService.deleteImage(smallImageId);
		}
	}

	protected BaseModelSearchResult<JournalArticle> searchJournalArticles(
			SearchContext searchContext)
		throws PortalException {

		Indexer<JournalArticle> indexer =
			IndexerRegistryUtil.nullSafeGetIndexer(JournalArticle.class);

		for (int i = 0; i < 10; i++) {
			Hits hits = indexer.search(
				searchContext, JournalUtil.SELECTED_FIELD_NAMES);

			List<JournalArticle> articles = _journalHelper.getArticles(hits);

			if (articles != null) {
				return new BaseModelSearchResult<>(articles, hits.getLength());
			}
		}

		throw new SearchException(
			"Unable to fix the search index after 10 attempts");
	}

	protected void sendEmail(
			JournalArticle article, String articleURL, String emailType,
			ServiceContext serviceContext)
		throws PortalException {

		JournalGroupServiceConfiguration journalGroupServiceConfiguration =
			getJournalGroupServiceConfiguration(article.getGroupId());

		if (emailType.equals("denied") &&
			journalGroupServiceConfiguration.
				emailArticleApprovalDeniedEnabled()) {
		}
		else if (emailType.equals("expired") &&
				 journalGroupServiceConfiguration.
					 emailArticleExpiredEnabled()) {
		}
		else if (emailType.equals("granted") &&
				 journalGroupServiceConfiguration.
					 emailArticleApprovalGrantedEnabled()) {
		}
		else if (emailType.equals("requested") &&
				 journalGroupServiceConfiguration.
					 emailArticleApprovalRequestedEnabled()) {
		}
		else if (emailType.equals("review") &&
				 journalGroupServiceConfiguration.emailArticleReviewEnabled()) {
		}
		else {
			return;
		}

		User user = _userLocalService.fetchUser(article.getUserId());

		if (user == null) {
			return;
		}

		String fromName = journalGroupServiceConfiguration.emailFromName();
		String fromAddress =
			journalGroupServiceConfiguration.emailFromAddress();

		String toName = user.getFullName();
		String toAddress = user.getEmailAddress();

		if (emailType.equals("requested")) {
			String tempToName = fromName;
			String tempToAddress = fromAddress;

			fromName = toName;
			fromAddress = toAddress;

			toName = tempToName;
			toAddress = tempToAddress;
		}

		SubscriptionSender subscriptionSender = new SubscriptionSender();

		_populateSubscriptionSender(
			article, articleURL, emailType, fromAddress, fromName,
			journalGroupServiceConfiguration, serviceContext,
			subscriptionSender);

		subscriptionSender.setClassPK(article.getPrimaryKey());
		subscriptionSender.setContextAttribute(
			"[$ARTICLE_USER_NAME$]", article.getUserName());
		subscriptionSender.setEntryTitle(article.getTitle(user.getLocale()));
		subscriptionSender.setNotificationType(_getNotificationType(emailType));

		if ((emailType.equals("expired") || emailType.equals("review")) &&
			(serviceContext.getUserId() == 0)) {

			subscriptionSender.setSendToCurrentUser(true);
		}

		subscriptionSender.addRuntimeSubscribers(toAddress, toName);

		subscriptionSender.flushNotificationsAsync();
	}

	protected JournalArticle setArticlesExpirationDate(JournalArticle article) {
		if (ExportImportThreadLocal.isImportInProcess() ||
			!article.isApproved() || (article.getExpirationDate() == null)) {

			return article;
		}

		List<JournalArticle> articles = journalArticlePersistence.findByG_A(
			article.getGroupId(), article.getArticleId());

		Date expirationDate = article.getExpirationDate();

		for (JournalArticle curArticle : articles) {
			curArticle.setExpirationDate(expirationDate);

			curArticle = journalArticleLocalService.updateJournalArticle(
				curArticle);

			if (article.equals(curArticle)) {
				article = curArticle;
			}
		}

		return article;
	}

	protected void startWorkflowInstance(
			long userId, JournalArticle article, ServiceContext serviceContext)
		throws PortalException {

		String userPortraitURL = StringPool.BLANK;
		String userURL = StringPool.BLANK;

		if (serviceContext.getThemeDisplay() != null) {
			User user = _userLocalService.getUser(userId);

			userPortraitURL = user.getPortraitURL(
				serviceContext.getThemeDisplay());
			userURL = user.getDisplayURL(serviceContext.getThemeDisplay());
		}

		Map<String, Serializable> workflowContext =
			HashMapBuilder.<String, Serializable>put(
				WorkflowConstants.CONTEXT_URL,
				_getArticlePreviewURL(article, serviceContext)
			).put(
				WorkflowConstants.CONTEXT_USER_PORTRAIT_URL, userPortraitURL
			).put(
				WorkflowConstants.CONTEXT_USER_URL, userURL
			).build();

		WorkflowHandlerRegistryUtil.startWorkflowInstance(
			article.getCompanyId(), article.getGroupId(), userId,
			JournalArticle.class.getName(), article.getId(), article,
			serviceContext, workflowContext);
	}

	protected void updateDDMFields(
			JournalArticle article, String content, DDMFormValues ddmFormValues)
		throws PortalException {

		DDMStructure ddmStructure = _ddmStructureLocalService.getStructure(
			article.getDDMStructureId());

		_ddmFieldLocalService.updateDDMFormValues(
			ddmStructure.getStructureId(), article.getId(), ddmFormValues);

		// Document Cache

		try {
			if (Validator.isNull(content)) {
				Fields fields = _ddmFormValuesToFieldsConverter.convert(
					ddmStructure, ddmFormValues);

				content = _journalConverter.getContent(
					ddmStructure, fields, article.getGroupId());
			}

			article.setDocument(SAXReaderUtil.read(content));

			journalArticlePersistence.cacheResult(article);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}
	}

	protected DDMFormValues updateDDMFields(
			JournalArticle article, String content, long groupId, User user)
		throws PortalException {

		content = _journalContentCompatibilityConverter.convert(content);

		content = _replaceTempImages(article, content);

		DDMStructure ddmStructure = article.getDDMStructure();

		DDMFormValues ddmFormValues = _fieldsToDDMFormValuesConverter.convert(
			ddmStructure,
			_journalConverter.getDDMFields(ddmStructure, content));

		format(user, groupId, article, ddmFormValues.getDDMFormFieldValues());

		updateDDMFields(article, content, ddmFormValues);

		return ddmFormValues;
	}

	protected void updateDDMLinks(
			long id, long groupId, long ddmStructureId, String ddmTemplateKey,
			boolean incrementVersion)
		throws PortalException {

		DDMStructure ddmStructure = ddmStructureLocalService.getStructure(
			ddmStructureId);

		DDMTemplate ddmTemplate = ddmTemplateLocalService.fetchTemplate(
			_portal.getSiteGroupId(groupId),
			_classNameLocalService.getClassNameId(DDMStructure.class),
			ddmTemplateKey, true);

		if (incrementVersion) {
			DDMStructureVersion ddmStructureVersion =
				_ddmStructureVersionLocalService.getStructureVersion(
					ddmStructure.getStructureId(), ddmStructure.getVersion());

			ddmStorageLinkLocalService.addStorageLink(
				ddmStructure.getClassNameId(), id,
				ddmStructureVersion.getStructureVersionId(),
				new ServiceContext());

			ddmStructureLinkLocalService.addStructureLink(
				_classNameLocalService.getClassNameId(JournalArticle.class), id,
				ddmStructure.getStructureId());

			if (ddmTemplate != null) {
				ddmTemplateLinkLocalService.addTemplateLink(
					_classNameLocalService.getClassNameId(JournalArticle.class),
					id, ddmTemplate.getTemplateId());
			}
		}
		else {
			DDMStorageLink ddmStorageLink =
				ddmStorageLinkLocalService.getClassStorageLink(id);

			ddmStorageLink.setStructureId(ddmStructure.getStructureId());

			ddmStorageLinkLocalService.updateDDMStorageLink(ddmStorageLink);

			int count = ddmStructureLinkLocalService.getStructureLinksCount(
				_classNameLocalService.getClassNameId(JournalArticle.class),
				id);

			if (count == 0) {
				ddmStructureLinkLocalService.addStructureLink(
					_classNameLocalService.getClassNameId(JournalArticle.class),
					id, ddmStructure.getStructureId());
			}
			else {
				DDMStructureLink ddmStructureLink =
					ddmStructureLinkLocalService.getUniqueStructureLink(
						_classNameLocalService.getClassNameId(
							JournalArticle.class),
						id);

				ddmStructureLink.setStructureId(ddmStructure.getStructureId());

				ddmStructureLinkLocalService.updateDDMStructureLink(
					ddmStructureLink);
			}

			if (ddmTemplate != null) {
				ddmTemplateLinkLocalService.updateTemplateLink(
					_classNameLocalService.getClassNameId(JournalArticle.class),
					id, ddmTemplate.getTemplateId());
			}
		}
	}

	protected void updateFriendlyURLs(
			JournalArticle article, Map<String, String> urlTitleMap,
			ServiceContext serviceContext)
		throws PortalException {

		if (ExportImportThreadLocal.isImportInProcess() ||
			ExportImportThreadLocal.isStagingInProcess()) {

			return;
		}

		List<FriendlyURLEntry> friendlyURLEntries =
			friendlyURLEntryLocalService.getFriendlyURLEntries(
				article.getGroupId(),
				_classNameLocalService.getClassNameId(JournalArticle.class),
				article.getResourcePrimKey());

		friendlyURLEntryLocalService.addFriendlyURLEntry(
			article.getGroupId(),
			_classNameLocalService.getClassNameId(JournalArticle.class),
			article.getResourcePrimKey(), urlTitleMap, serviceContext);

		for (Map.Entry<String, String> entry : urlTitleMap.entrySet()) {
			if (Validator.isNull(entry.getValue())) {
				for (FriendlyURLEntry friendlyURLEntry : friendlyURLEntries) {
					try {
						friendlyURLEntryLocalService.
							deleteFriendlyURLLocalizationEntry(
								friendlyURLEntry.getFriendlyURLEntryId(),
								entry.getKey());
					}
					catch (NoSuchFriendlyURLEntryLocalizationException
								noSuchFriendlyURLEntryLocalizationException) {

						if (_log.isDebugEnabled()) {
							_log.debug(
								noSuchFriendlyURLEntryLocalizationException);
						}
					}
				}
			}
		}
	}

	protected void updatePreviousApprovedArticle(JournalArticle article)
		throws PortalException {

		JournalArticle previousApprovedArticle = getPreviousApprovedArticle(
			article);

		if (previousApprovedArticle.getVersion() == article.getVersion()) {
			AssetEntry assetEntry = _assetEntryLocalService.updateVisible(
				JournalArticle.class.getName(), article.getResourcePrimKey(),
				false);

			if (article.getStatus() == WorkflowConstants.STATUS_EXPIRED) {
				assetEntry.setExpirationDate(article.getExpirationDate());

				_assetEntryLocalService.updateAssetEntry(assetEntry);
			}
		}
		else {
			AssetEntry assetEntry = _assetEntryLocalService.updateEntry(
				JournalArticle.class.getName(), article.getResourcePrimKey(),
				previousApprovedArticle.getDisplayDate(),
				previousApprovedArticle.getExpirationDate(),
				isListable(article), true);

			assetEntry.setModifiedDate(
				previousApprovedArticle.getModifiedDate());
			assetEntry.setTitle(previousApprovedArticle.getTitleMapAsXML());

			_assetEntryLocalService.updateAssetEntry(assetEntry);
		}
	}

	protected void updateUrlTitles(
			long groupId, String articleId, String urlTitle)
		throws PortalException {

		JournalArticle firstArticle = journalArticlePersistence.findByG_A_First(
			groupId, articleId, ArticleVersionComparator.getInstance(false));

		String firstArticleUrlTitle = firstArticle.getUrlTitle();

		if (firstArticleUrlTitle.equals(urlTitle)) {
			return;
		}

		List<JournalArticle> articles = journalArticlePersistence.findByG_A(
			groupId, articleId);

		for (JournalArticle article : articles) {
			String curArticleUrlTitle = article.getUrlTitle();

			if (!curArticleUrlTitle.equals(urlTitle)) {
				article.setUrlTitle(urlTitle);

				journalArticlePersistence.update(article);
			}
		}
	}

	protected void validate(
			long companyId, long groupId, long classNameId,
			Map<Locale, String> titleMap, String content, long ddmStructureId,
			String ddmTemplateKey, Date displayDate, Date expirationDate,
			boolean smallImage, String smallImageURL, File smallImageFile,
			byte[] smallImageBytes, ServiceContext serviceContext)
		throws PortalException {

		_getModelValidator().validate(
			companyId, groupId, classNameId, titleMap, content, ddmStructureId,
			ddmTemplateKey, displayDate, expirationDate, smallImage,
			smallImageURL, smallImageFile, smallImageBytes, serviceContext);
	}

	protected void validate(String articleId) throws PortalException {
		_getModelValidator().validate(articleId);
	}

	protected void validate(
			String externalReferenceCode, long companyId, long groupId,
			long classNameId, String articleId, boolean autoArticleId,
			double version, Map<Locale, String> titleMap, String content,
			long ddmStructureId, String ddmTemplateKey, Date displayDate,
			Date expirationDate, boolean smallImage, String smallImageURL,
			File smallImageFile, byte[] smallImageBytes,
			ServiceContext serviceContext)
		throws PortalException {

		_getModelValidator().validate(
			externalReferenceCode, companyId, groupId, classNameId, articleId,
			autoArticleId, version, titleMap, content, ddmStructureId,
			ddmTemplateKey, displayDate, expirationDate, smallImage,
			smallImageURL, smallImageFile, smallImageBytes, serviceContext);
	}

	protected void validateContent(String content) throws PortalException {
		_getModelValidator().validateContent(content);
	}

	protected void validateDDMStructureId(
			long groupId, long folderId, long ddmStructureId)
		throws PortalException {

		_getModelValidator().validateDDMStructureId(
			groupId, folderId, ddmStructureId);
	}

	protected void validateReferences(
			long groupId, long folderId, long ddmStructureId,
			String ddmTemplateKey, String layoutUuid, boolean smallImage,
			String smallImageURL, byte[] smallImageBytes, long smallImageId,
			int smallImageSource, String content)
		throws PortalException {

		_getModelValidator().validateReferences(
			groupId, folderId, ddmStructureId, ddmTemplateKey, layoutUuid,
			smallImage, smallImageURL, smallImageBytes, smallImageId,
			smallImageSource, content);
	}

	@Reference
	protected ConfigurationProvider configurationProvider;

	@Reference
	protected DDMStorageLinkLocalService ddmStorageLinkLocalService;

	@Reference
	protected DDMStructureLinkLocalService ddmStructureLinkLocalService;

	@Reference
	protected DDMStructureLocalService ddmStructureLocalService;

	@Reference
	protected DDMTemplateLinkLocalService ddmTemplateLinkLocalService;

	@Reference
	protected DDMTemplateLocalService ddmTemplateLocalService;

	@Reference
	protected FriendlyURLEntryLocalService friendlyURLEntryLocalService;

	private List<JournalArticleLocalization> _addArticleLocalizedFields(
		long companyId, long articlePK, Map<Locale, String> titleMap,
		Map<Locale, String> descriptionMap) {

		Set<Locale> locales = new HashSet<>();

		locales.addAll(titleMap.keySet());

		if (descriptionMap != null) {
			locales.addAll(descriptionMap.keySet());
		}

		return TransformUtil.transform(
			locales,
			locale -> {
				String title = titleMap.get(locale);

				String description = null;

				if (descriptionMap != null) {
					description = descriptionMap.get(locale);
				}

				if (Validator.isNull(title) && Validator.isNull(description)) {
					return null;
				}

				return _addArticleLocalizedFields(
					companyId, articlePK, title, description,
					LocaleUtil.toLanguageId(locale));
			});
	}

	private JournalArticleLocalization _addArticleLocalizedFields(
		long companyId, long articlePK, String title, String description,
		String languageId) {

		JournalArticleLocalization journalArticleLocalization =
			_journalArticleLocalizationPersistence.fetchByC_A_L(
				companyId, articlePK, languageId);

		if (journalArticleLocalization == null) {
			long journalArticleLocalizationId = counterLocalService.increment();

			journalArticleLocalization =
				_journalArticleLocalizationPersistence.create(
					journalArticleLocalizationId);

			journalArticleLocalization.setCompanyId(companyId);
			journalArticleLocalization.setArticlePK(articlePK);
			journalArticleLocalization.setTitle(title);
			journalArticleLocalization.setDescription(description);
			journalArticleLocalization.setLanguageId(languageId);
		}
		else {
			journalArticleLocalization.setTitle(title);
			journalArticleLocalization.setDescription(description);
		}

		return _journalArticleLocalizationPersistence.update(
			journalArticleLocalization);
	}

	private boolean _addDraftAssetEntry(JournalArticle journalArticle) {
		if (journalArticle.isApproved() ||
			(journalArticle.getVersion() ==
				JournalArticleConstants.VERSION_DEFAULT)) {

			return false;
		}

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			JournalArticle.class.getName(),
			journalArticle.getResourcePrimKey());

		if (assetEntry == null) {
			return false;
		}

		int approvedArticlesCount = journalArticlePersistence.countByG_A_ST(
			journalArticle.getGroupId(), journalArticle.getArticleId(),
			JournalArticleConstants.ASSET_ENTRY_CREATION_STATUSES);

		if (approvedArticlesCount == 0) {
			return false;
		}

		return true;
	}

	private Map<Locale, String> _checkFriendlyURLMap(
		Locale defaultLocale, Map<Locale, String> friendlyURLMap,
		Map<Locale, String> titleMap) {

		if (friendlyURLMap.containsKey(defaultLocale) &&
			Validator.isNotNull(friendlyURLMap.get(defaultLocale))) {

			return friendlyURLMap;
		}

		return HashMapBuilder.putAll(
			friendlyURLMap
		).put(
			defaultLocale, _removeTrailingSlashes(titleMap.get(defaultLocale))
		).build();
	}

	private JournalArticle _copyArticle(
			long userId, long groupId, String sourceArticleId,
			String targetArticleId, double version, boolean newArticle)
		throws PortalException {

		// Article

		sourceArticleId = StringUtil.toUpperCase(
			StringUtil.trim(sourceArticleId));
		targetArticleId = StringUtil.toUpperCase(
			StringUtil.trim(targetArticleId));

		JournalArticle sourceArticle = journalArticlePersistence.findByG_A_V(
			groupId, sourceArticleId, version);

		User user = _userLocalService.getUser(userId);

		long id = counterLocalService.increment();

		long resourcePrimKey = _getResourcePrimKey(
			groupId, targetArticleId, newArticle);

		JournalArticle targetArticle = journalArticlePersistence.create(id);

		targetArticle.setResourcePrimKey(resourcePrimKey);
		targetArticle.setGroupId(groupId);
		targetArticle.setCompanyId(user.getCompanyId());
		targetArticle.setUserId(user.getUserId());
		targetArticle.setUserName(user.getFullName());

		Date modifiedDate = new Date();

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext == null) {
			serviceContext = new ServiceContext();
		}

		modifiedDate = serviceContext.getModifiedDate(modifiedDate);

		targetArticle.setModifiedDate(modifiedDate);

		targetArticle.setExternalReferenceCode(targetArticleId);
		targetArticle.setFolderId(sourceArticle.getFolderId());
		targetArticle.setTreePath(sourceArticle.getTreePath());
		targetArticle.setArticleId(targetArticleId);

		if (newArticle) {
			version = JournalArticleConstants.VERSION_DEFAULT;
		}
		else {
			version = getNextVersion(
				getLatestArticle(
					groupId, sourceArticleId, WorkflowConstants.STATUS_ANY));
		}

		targetArticle.setVersion(version);
		targetArticle.setDDMStructureId(sourceArticle.getDDMStructureId());
		targetArticle.setDDMTemplateKey(sourceArticle.getDDMTemplateKey());
		targetArticle.setDefaultLanguageId(
			sourceArticle.getDefaultLanguageId());
		targetArticle.setLayoutUuid(sourceArticle.getLayoutUuid());
		targetArticle.setDisplayDate(sourceArticle.getDisplayDate());
		targetArticle.setExpirationDate(sourceArticle.getExpirationDate());
		targetArticle.setReviewDate(sourceArticle.getReviewDate());
		targetArticle.setIndexable(sourceArticle.isIndexable());
		targetArticle.setSmallImage(sourceArticle.isSmallImage());

		if (newArticle) {
			targetArticle.setSmallImageId(counterLocalService.increment());
		}
		else {
			JournalArticle latestArticle = getLatestArticle(
				groupId, targetArticleId, WorkflowConstants.STATUS_ANY);

			targetArticle.setSmallImageId(latestArticle.getSmallImageId());
		}

		targetArticle.setSmallImageSource(sourceArticle.getSmallImageSource());
		targetArticle.setSmallImageURL(sourceArticle.getSmallImageURL());

		WorkflowHandler<?> workflowHandler =
			WorkflowHandlerRegistryUtil.getWorkflowHandler(
				JournalArticle.class.getName());

		WorkflowDefinitionLink workflowDefinitionLink =
			workflowHandler.getWorkflowDefinitionLink(
				sourceArticle.getCompanyId(), sourceArticle.getGroupId(),
				sourceArticle.getId());

		if (sourceArticle.isPending() || (workflowDefinitionLink != null)) {
			targetArticle.setStatus(WorkflowConstants.STATUS_DRAFT);
		}
		else {
			targetArticle.setStatus(sourceArticle.getStatus());
		}

		targetArticle.setStatusByUserId(user.getUserId());
		targetArticle.setStatusByUserName(user.getFullName());
		targetArticle.setStatusDate(modifiedDate);

		Map<Locale, String> titleMap = sourceArticle.getTitleMap();
		Map<Locale, String> uniqueURLTitleMap = new HashMap<>();

		if (newArticle) {
			titleMap = new HashMap<>(sourceArticle.getTitleMap());

			for (Map.Entry<Locale, String> entry : titleMap.entrySet()) {
				Locale locale = entry.getKey();

				String uniqueUrlTitle = _getUniqueUrlTitle(
					groupId, targetArticleId, entry.getValue());

				titleMap.put(locale, uniqueUrlTitle);

				uniqueURLTitleMap.put(
					locale, JournalUtil.getUrlTitle(id, uniqueUrlTitle));
			}
		}

		DDMFormValues ddmFormValues = sourceArticle.getDDMFormValues();

		Locale locale = ddmFormValues.getDefaultLocale();

		String newURLTitle = sourceArticle.getUrlTitle();

		if (newArticle) {
			newURLTitle = uniqueURLTitleMap.get(locale);

			while (fetchArticleByUrlTitle(groupId, newURLTitle) != null) {
				newURLTitle = getUniqueUrlTitle(
					id, groupId, targetArticleId, newURLTitle);
			}
		}

		targetArticle.setUrlTitle(newURLTitle);

		ExpandoBridgeUtil.copyExpandoBridgeAttributes(
			sourceArticle.getExpandoBridge(), targetArticle.getExpandoBridge());

		targetArticle = journalArticlePersistence.update(targetArticle);

		// Article localization

		Map<Locale, String> friendlyURLMap = _checkFriendlyURLMap(
			locale, new HashMap<>(), uniqueURLTitleMap);

		Map<String, String> newUrlTitleMap = _getURLTitleMap(
			groupId, resourcePrimKey, friendlyURLMap, uniqueURLTitleMap);

		updateFriendlyURLs(targetArticle, newUrlTitleMap, serviceContext);

		_addArticleLocalizedFields(
			targetArticle.getCompanyId(), targetArticle.getId(), titleMap,
			sourceArticle.getDescriptionMap());

		// Resources

		if (newArticle) {
			_resourceLocalService.copyModelResources(
				sourceArticle.getCompanyId(), JournalArticle.class.getName(),
				sourceArticle.getResourcePrimKey(), resourcePrimKey);
		}

		// Small image

		if (sourceArticle.isSmallImage()) {
			Image image = _imageLocalService.fetchImage(
				sourceArticle.getSmallImageId());

			if (image != null) {
				byte[] smallImageBytes = image.getTextObj();

				_imageLocalService.updateImage(
					targetArticle.getCompanyId(),
					targetArticle.getSmallImageId(), smallImageBytes);
			}
		}

		// Asset

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			JournalArticle.class.getName(), sourceArticle.getId());

		if (assetEntry == null) {
			assetEntry = _assetEntryLocalService.getEntry(
				JournalArticle.class.getName(),
				sourceArticle.getResourcePrimKey());
		}

		long[] assetCategoryIds = _assetCategoryLocalService.getCategoryIds(
			JournalArticle.class.getName(), assetEntry.getClassPK());
		String[] assetTagNames = _assetTagLocalService.getTagNames(
			JournalArticle.class.getName(), assetEntry.getClassPK());

		List<AssetLink> assetLinks = _assetLinkLocalService.getDirectLinks(
			assetEntry.getEntryId(), false);

		long[] assetLinkEntryIds = ListUtil.toLongArray(
			assetLinks, AssetLink.ENTRY_ID2_ACCESSOR);

		updateAsset(
			userId, targetArticle, assetCategoryIds, assetTagNames,
			assetLinkEntryIds, assetEntry.getPriority());

		AssetDisplayPageEntry assetDisplayPageEntry =
			_assetDisplayPageEntryLocalService.fetchAssetDisplayPageEntry(
				groupId, _portal.getClassNameId(JournalArticle.class.getName()),
				sourceArticle.getResourcePrimKey());

		if (assetDisplayPageEntry != null) {
			_assetDisplayPageEntryLocalService.addAssetDisplayPageEntry(
				userId, groupId,
				_portal.getClassNameId(JournalArticle.class.getName()),
				targetArticle.getResourcePrimKey(),
				assetDisplayPageEntry.getLayoutPageTemplateEntryId(),
				assetDisplayPageEntry.getType(), serviceContext);
		}

		// Dynamic data mapping

		if (newArticle) {
			updateDDMFields(
				targetArticle, null,
				copyArticleImages(sourceArticle, targetArticle));
		}
		else {
			updateDDMFields(
				targetArticle, null, sourceArticle.getDDMFormValues());
		}

		updateDDMLinks(
			id, groupId, sourceArticle.getDDMStructureId(),
			sourceArticle.getDDMTemplateKey(), true);

		return targetArticle;
	}

	private void _copyArticleImages(
		JournalArticle article, List<DDMFormFieldValue> ddmFormFieldValues,
		long groupId, long folderId) {

		Repository portletRepository = _getRepository(groupId);

		if (portletRepository == null) {
			return;
		}

		Map<Long, FileEntry> fileEntryMap = new HashMap<>();

		for (DDMFormFieldValue ddmFormFieldValue : ddmFormFieldValues) {
			if (ListUtil.isNotEmpty(
					ddmFormFieldValue.getNestedDDMFormFieldValues())) {

				_copyArticleImages(
					article, ddmFormFieldValue.getNestedDDMFormFieldValues(),
					groupId, folderId);
			}

			if (Objects.equals(
					ddmFormFieldValue.getType(),
					DDMFormFieldTypeConstants.FIELDSET) ||
				!Objects.equals(
					DDMFormFieldTypeConstants.IMAGE,
					ddmFormFieldValue.getType())) {

				continue;
			}

			Value oldValue = ddmFormFieldValue.getValue();

			if (oldValue == null) {
				continue;
			}

			Value newValue = new LocalizedValue();

			newValue.setDefaultLocale(oldValue.getDefaultLocale());

			for (Locale locale : oldValue.getAvailableLocales()) {
				try {
					JSONObject valueJSONObject = _jsonFactory.createJSONObject(
						oldValue.getString(locale));

					FileEntry oldFileEntry = _getFileEntry(valueJSONObject);

					if (oldFileEntry == null) {
						continue;
					}

					FileEntry newFileEntry = oldFileEntry;

					if (oldFileEntry.getRepositoryId() ==
							portletRepository.getRepositoryId()) {

						newFileEntry = fileEntryMap.computeIfAbsent(
							oldFileEntry.getFileEntryId(),
							key -> {
								try {
									return _portletFileRepository.
										addPortletFileEntry(
											null, groupId, article.getUserId(),
											JournalArticle.class.getName(),
											article.getResourcePrimKey(),
											JournalConstants.SERVICE_NAME,
											folderId,
											oldFileEntry.getContentStream(),
											oldFileEntry.getFileName(),
											oldFileEntry.getMimeType(), false);
								}
								catch (PortalException portalException) {
									if (_log.isDebugEnabled()) {
										_log.debug(portalException);
									}

									throw new RuntimeException(portalException);
								}
							});
					}

					String previewURL = _dlURLHelper.getPreviewURL(
						newFileEntry, newFileEntry.getFileVersion(), null,
						StringPool.BLANK, false, true);

					newValue.addString(
						locale,
						_toJSON(
							valueJSONObject.getString("alt"), article,
							newFileEntry, valueJSONObject.getString("height"),
							valueJSONObject.getString("type"), previewURL,
							valueJSONObject.getString("width")));
				}
				catch (Exception exception) {
					if (_log.isDebugEnabled()) {
						_log.debug(exception);
					}
				}
			}

			ddmFormFieldValue.setValue(newValue);
		}
	}

	private void _deleteDDMFormFieldPredefinedValues(
		List<DDMFormField> ddmFormFields) {

		for (DDMFormField ddmFormField : ddmFormFields) {
			ddmFormField.setPredefinedValue(new LocalizedValue());

			_deleteDDMFormFieldPredefinedValues(
				ddmFormField.getNestedDDMFormFields());
		}
	}

	private void _deleteDDMStructurePredefinedValues(long ddmStructureId)
		throws PortalException {

		DDMStructure ddmStructure = ddmStructureLocalService.fetchStructure(
			ddmStructureId);

		if (ddmStructure == null) {
			return;
		}

		DDMForm ddmForm = ddmStructure.getDDMForm();

		_deleteDDMFormFieldPredefinedValues(ddmForm.getDDMFormFields());

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		boolean indexingEnabled = serviceContext.isIndexingEnabled();

		try {
			serviceContext.setIndexingEnabled(false);

			ddmStructureLocalService.updateStructure(
				serviceContext.getUserId(), ddmStructure.getStructureId(),
				ddmForm, ddmStructure.getDDMFormLayout(), serviceContext);
		}
		finally {
			serviceContext.setIndexingEnabled(indexingEnabled);
		}
	}

	private boolean _equals(
		LocalizedValue localizedValue1, LocalizedValue localizedValue2,
		String fieldType) {

		Predicate<String> emptyValuePredicate = _getEmptyValuePredicate(
			fieldType);

		if ((_isEmpty(localizedValue1, emptyValuePredicate) &&
			 _isEmpty(localizedValue2, emptyValuePredicate)) ||
			Objects.equals(localizedValue1, localizedValue2)) {

			return true;
		}

		return false;
	}

	private String _getArticleContent(JournalArticle article, Locale locale) {
		String articleContent = StringPool.BLANK;

		try {
			JournalArticleDisplay articleDisplay = getArticleDisplay(
				article, article.getDDMTemplateKey(), Constants.VIEW,
				LocaleUtil.toLanguageId(locale), 1, null, null);

			articleContent = articleDisplay.getContent();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return articleContent;
	}

	private String _getArticleDiffs(JournalArticle article, Locale locale) {
		JournalArticle previousApprovedArticle =
			journalArticleLocalService.getPreviousApprovedArticle(article);

		try {
			String articleDiffs = _journalHelper.diffHtml(
				article.getGroupId(), article.getArticleId(),
				previousApprovedArticle.getVersion(), article.getVersion(),
				LocaleUtil.toLanguageId(locale), null, null);

			return _diffHtml.replaceStyles(articleDiffs);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return StringPool.BLANK;
	}

	private String _getArticlePreviewURL(JournalArticle article)
		throws PortalException {

		String portletId = PortletProviderUtil.getPortletId(
			JournalArticle.class.getName(), PortletProvider.Action.EDIT);

		String previewURL = _portal.getControlPanelFullURL(
			article.getGroupId(), portletId, null);

		String namespace = _portal.getPortletNamespace(portletId);

		previewURL = HttpComponentsUtil.addParameter(
			previewURL, namespace + "mvcPath", "/preview_article_content.jsp");
		previewURL = HttpComponentsUtil.addParameter(
			previewURL, namespace + "groupId", article.getGroupId());
		previewURL = HttpComponentsUtil.addParameter(
			previewURL, namespace + "articleId", article.getArticleId());
		previewURL = HttpComponentsUtil.addParameter(
			previewURL, namespace + "version", article.getVersion());

		return previewURL;
	}

	private String _getArticlePreviewURL(
			JournalArticle article, ServiceContext serviceContext)
		throws PortalException {

		AssetRendererFactory<JournalArticle> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClass(
				JournalArticle.class);

		try {
			ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

			if (themeDisplay != null) {
				AssetRenderer<JournalArticle> assetRenderer =
					assetRendererFactory.getAssetRenderer(
						article, AssetRendererFactory.TYPE_LATEST);

				String previewURL = assetRenderer.getURLViewInContext(
					themeDisplay, null);

				if (Validator.isNotNull(previewURL)) {
					return previewURL;
				}
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return _getArticlePreviewURL(article);
	}

	private String _getArticleURL(JournalArticle article)
		throws PortalException {

		String portletId = PortletProviderUtil.getPortletId(
			JournalArticle.class.getName(), PortletProvider.Action.EDIT);

		String articleURL = _portal.getControlPanelFullURL(
			article.getGroupId(), portletId, null);

		articleURL = HttpComponentsUtil.addParameter(
			articleURL,
			_portal.getPortletNamespace(portletId) + "mvcRenderCommandName",
			"/journal/edit_article");

		articleURL = buildArticleURL(
			articleURL, article.getGroupId(), article.getFolderId(),
			article.getArticleId());

		return articleURL;
	}

	private Predicate<String> _getEmptyValuePredicate(String fieldType) {
		if (fieldType.equals("checkbox_multiple")) {
			return string -> string.equals("[]") || string.isEmpty();
		}

		if (fieldType.equals("document_library") ||
			fieldType.equals("journal_article") ||
			fieldType.equals("link_to_layout")) {

			return string -> string.equals("{}") || string.isEmpty();
		}

		if (fieldType.equals("image")) {
			return string ->
				string.equals("{}") || string.equals("{\"alt\":\"\"}");
		}

		if (fieldType.equals("radio")) {
			return string -> string.equals("[]");
		}

		if (fieldType.equals("select")) {
			return string -> string.equals("[]") || string.equals("[\"\"]");
		}

		return String::isEmpty;
	}

	private FileEntry _getFileEntry(JSONObject valueJSONObject) {
		if ((valueJSONObject == null) || (valueJSONObject.length() <= 0)) {
			return null;
		}

		try {
			return _dlAppLocalService.getFileEntryByUuidAndGroupId(
				valueJSONObject.getString("uuid"),
				valueJSONObject.getLong("groupId"));
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to get file entry", portalException);
			}

			return null;
		}
	}

	private String _getFolderName(
		JournalArticle article, Locale locale, JournalFolder folder) {

		String folderName = StringPool.BLANK;

		if (folder != null) {
			folderName = folder.getName();
		}
		else if (article.getFolderId() ==
					JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID) {

			folderName = _language.get(locale, "home");
		}

		return folderName;
	}

	private Map<Locale, String> _getLocalizedBodyMap(
		String emailType,
		JournalGroupServiceConfiguration journalGroupServiceConfiguration) {

		if (emailType.equals("add")) {
			return _localization.getMap(
				journalGroupServiceConfiguration.emailArticleAddedBody());
		}

		if (emailType.equals("denied")) {
			return _localization.getMap(
				journalGroupServiceConfiguration.
					emailArticleApprovalDeniedBody());
		}

		if (emailType.equals("expired")) {
			return _localization.getMap(
				journalGroupServiceConfiguration.emailArticleExpiredBody());
		}

		if (emailType.equals("granted")) {
			return _localization.getMap(
				journalGroupServiceConfiguration.
					emailArticleApprovalGrantedBody());
		}

		if (emailType.equals("move_to")) {
			return _localization.getMap(
				journalGroupServiceConfiguration.
					emailArticleMovedToFolderBody());
		}

		if (emailType.equals("move_to_trash")) {
			return _localization.getMap(
				journalGroupServiceConfiguration.
					emailArticleMovedToTrashBody());
		}
		else if (emailType.equals("move_from")) {
			return _localization.getMap(
				journalGroupServiceConfiguration.
					emailArticleMovedFromFolderBody());
		}

		if (emailType.equals("move_from_trash")) {
			return _localization.getMap(
				journalGroupServiceConfiguration.
					emailArticleMovedFromTrashBody());
		}

		if (emailType.equals("requested")) {
			return _localization.getMap(
				journalGroupServiceConfiguration.
					emailArticleApprovalRequestedBody());
		}

		if (emailType.equals("review")) {
			return _localization.getMap(
				journalGroupServiceConfiguration.emailArticleReviewBody());
		}

		if (!emailType.equals("update")) {
			return null;
		}

		return _localization.getMap(
			journalGroupServiceConfiguration.emailArticleUpdatedBody());
	}

	private Map<Locale, String> _getLocalizedSubjectMap(
		String emailType,
		JournalGroupServiceConfiguration journalGroupServiceConfiguration) {

		if (emailType.equals("add")) {
			return _localization.getMap(
				journalGroupServiceConfiguration.emailArticleAddedSubject());
		}

		if (emailType.equals("denied")) {
			return _localization.getMap(
				journalGroupServiceConfiguration.
					emailArticleApprovalDeniedSubject());
		}

		if (emailType.equals("expired")) {
			return _localization.getMap(
				journalGroupServiceConfiguration.emailArticleExpiredSubject());
		}

		if (emailType.equals("granted")) {
			return _localization.getMap(
				journalGroupServiceConfiguration.
					emailArticleApprovalGrantedSubject());
		}

		if (emailType.equals("move_to")) {
			return _localization.getMap(
				journalGroupServiceConfiguration.
					emailArticleMovedToFolderSubject());
		}

		if (emailType.equals("move_to_trash")) {
			return _localization.getMap(
				journalGroupServiceConfiguration.
					emailArticleMovedToTrashSubject());
		}

		if (emailType.equals("move_from")) {
			return _localization.getMap(
				journalGroupServiceConfiguration.
					emailArticleMovedFromFolderSubject());
		}

		if (emailType.equals("move_from_trash")) {
			return _localization.getMap(
				journalGroupServiceConfiguration.
					emailArticleMovedFromTrashSubject());
		}

		if (emailType.equals("requested")) {
			return _localization.getMap(
				journalGroupServiceConfiguration.
					emailArticleApprovalRequestedSubject());
		}

		if (emailType.equals("review")) {
			return _localization.getMap(
				journalGroupServiceConfiguration.emailArticleReviewSubject());
		}

		if (!emailType.equals("update")) {
			return null;
		}

		return _localization.getMap(
			journalGroupServiceConfiguration.emailArticleUpdatedSubject());
	}

	private JournalArticleModelValidator _getModelValidator() {
		ModelValidator<JournalArticle> modelValidator =
			ModelValidatorRegistryUtil.getModelValidator(JournalArticle.class);

		return (JournalArticleModelValidator)modelValidator;
	}

	private int _getNotificationType(String emailType) {
		if (emailType.equals("expired")) {
			return UserNotificationDefinition.NOTIFICATION_TYPE_EXPIRED_ENTRY;
		}

		if (emailType.equals("move_from")) {
			return JournalArticleConstants.
				NOTIFICATION_TYPE_MOVE_ENTRY_FROM_FOLDER;
		}

		if (emailType.equals("move_from_trash")) {
			return JournalArticleConstants.
				NOTIFICATION_TYPE_MOVE_ENTRY_FROM_TRASH;
		}

		if (emailType.equals("move_to")) {
			return JournalArticleConstants.
				NOTIFICATION_TYPE_MOVE_ENTRY_TO_FOLDER;
		}

		if (emailType.equals("move_to_trash")) {
			return JournalArticleConstants.
				NOTIFICATION_TYPE_MOVE_ENTRY_TO_TRASH;
		}

		if (emailType.equals("review")) {
			return UserNotificationDefinition.NOTIFICATION_TYPE_REVIEW_ENTRY;
		}

		if (emailType.equals("update")) {
			return UserNotificationDefinition.NOTIFICATION_TYPE_UPDATE_ENTRY;
		}

		return UserNotificationDefinition.NOTIFICATION_TYPE_ADD_ENTRY;
	}

	private Repository _getRepository(long groupId) {
		try {
			Repository repository =
				_portletFileRepository.fetchPortletRepository(
					groupId, JournalConstants.SERVICE_NAME);

			if (repository != null) {
				return repository;
			}

			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setAddGroupPermissions(true);
			serviceContext.setAddGuestPermissions(true);

			return _portletFileRepository.addPortletRepository(
				groupId, JournalConstants.SERVICE_NAME, serviceContext);
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return null;
	}

	private long _getResourcePrimKey(
			long groupId, String targetArticleId, boolean newArticle)
		throws PortalException {

		if (newArticle) {
			return _journalArticleResourceLocalService.
				getArticleResourcePrimKey(groupId, targetArticleId);
		}

		JournalArticle latestArticle = getLatestArticle(
			groupId, targetArticleId, WorkflowConstants.STATUS_ANY);

		return latestArticle.getResourcePrimKey();
	}

	private int _getSmallImageSource(boolean smallImage, String smallImageURL) {
		if (!smallImage) {
			return JournalArticleConstants.SMALL_IMAGE_SOURCE_NONE;
		}

		if (Validator.isNotNull(smallImageURL)) {
			return JournalArticleConstants.SMALL_IMAGE_SOURCE_URL;
		}

		return JournalArticleConstants.SMALL_IMAGE_SOURCE_USER_COMPUTER;
	}

	private String _getUniqueUrlTitle(
		long groupId, String articleId, String urlTitle) {

		String copy = _language.get(LocaleUtil.getSiteDefault(), "copy");
		String prefix = urlTitle;
		String title = urlTitle;

		for (int i = 1;; i++) {
			JournalArticle article = fetchArticleByUrlTitle(groupId, urlTitle);

			if ((article == null) ||
				Objects.equals(articleId, article.getArticleId())) {

				return title;
			}

			if (i == 1) {
				title = StringBundler.concat(
					prefix, StringPool.SPACE, StringPool.OPEN_PARENTHESIS, copy,
					StringPool.CLOSE_PARENTHESIS);
				urlTitle = StringBundler.concat(
					prefix, StringPool.DASH, copy, StringPool.DASH);

				continue;
			}

			title = StringBundler.concat(
				prefix, StringPool.SPACE, StringPool.OPEN_PARENTHESIS, copy,
				StringPool.SPACE, i - 1, StringPool.CLOSE_PARENTHESIS);
			urlTitle = StringBundler.concat(
				prefix, StringPool.DASH, copy, StringPool.DASH, i - 1,
				StringPool.DASH);
		}
	}

	private Map<String, String> _getURLTitleMap(
		long groupId, long resourcePrimKey, Map<Locale, String> friendlyURLMap,
		Map<Locale, String> titleMap) {

		Map<String, String> urlTitleMap = new HashMap<>();

		for (Map.Entry<Locale, String> entry : titleMap.entrySet()) {
			String friendlyURL = friendlyURLMap.get(entry.getKey());

			if (friendlyURL == null) {
				continue;
			}

			if (Validator.isNull(friendlyURL)) {
				urlTitleMap.put(
					_language.getLanguageId(entry.getKey()), StringPool.BLANK);

				continue;
			}

			String languageId = _language.getLanguageId(entry.getKey());

			if (friendlyURL.startsWith(StringPool.SLASH)) {
				friendlyURL = friendlyURL.replaceAll("^/+", StringPool.BLANK);
			}

			if (friendlyURL.contains(StringPool.SLASH)) {
				friendlyURL = friendlyURL.replaceAll("/+", StringPool.SLASH);
			}

			String urlTitle = friendlyURLEntryLocalService.getUniqueUrlTitle(
				groupId,
				_classNameLocalService.getClassNameId(JournalArticle.class),
				resourcePrimKey, friendlyURL, languageId);

			urlTitleMap.put(languageId, urlTitle);
		}

		for (Map.Entry<Locale, String> entry : friendlyURLMap.entrySet()) {
			String languageId = LocaleUtil.toLanguageId(entry.getKey());
			String value = entry.getValue();

			if (!urlTitleMap.containsKey(languageId) &&
				Validator.isNotNull(value)) {

				String urlTitle =
					friendlyURLEntryLocalService.getUniqueUrlTitle(
						groupId,
						_classNameLocalService.getClassNameId(
							JournalArticle.class),
						resourcePrimKey, value, languageId);

				urlTitleMap.put(languageId, urlTitle);
			}
		}

		return urlTitleMap;
	}

	private boolean _isEmpty(
		LocalizedValue localizedValue, Predicate<String> emptyValuePredicate) {

		if (localizedValue == null) {
			return true;
		}

		Map<Locale, String> values = localizedValue.getValues();

		for (String string : values.values()) {
			if ((string != null) && !emptyValuePredicate.test(string)) {
				return false;
			}
		}

		return true;
	}

	private void _populateSubscriptionSender(
		JournalArticle article, String articleURL, String emailType,
		String fromAddress, String fromName,
		JournalGroupServiceConfiguration journalGroupServiceConfiguration,
		ServiceContext serviceContext, SubscriptionSender subscriptionSender) {

		subscriptionSender.setClassName(article.getModelClassName());
		subscriptionSender.setContextAttributes(
			"[$ARTICLE_ID$]", article.getArticleId(), "[$ARTICLE_URL$]",
			articleURL, "[$ARTICLE_VERSION$]", article.getVersion());
		subscriptionSender.setContextCreatorUserPrefix("ARTICLE");
		subscriptionSender.setCreatorUserId(article.getUserId());
		subscriptionSender.setEntryURL(articleURL);
		subscriptionSender.setFrom(fromAddress, fromName);
		subscriptionSender.setHtmlFormat(true);
		subscriptionSender.setLocalizedBodyMap(
			_getLocalizedBodyMap(emailType, journalGroupServiceConfiguration));
		subscriptionSender.setLocalizedContextAttribute(
			"[$ARTICLE_DIFFS$]",
			new EscapableLocalizableFunction(
				locale -> _getArticleDiffs(article, locale), false));
		subscriptionSender.setLocalizedContextAttribute(
			"[$ARTICLE_TITLE$]",
			new EscapableLocalizableFunction(
				locale -> article.getTitle(LocaleUtil.toLanguageId(locale))));
		subscriptionSender.setLocalizedSubjectMap(
			_getLocalizedSubjectMap(
				emailType, journalGroupServiceConfiguration));
		subscriptionSender.setMailId("journal_article", article.getId());
		subscriptionSender.setPortletId(
			PortletProviderUtil.getPortletId(
				JournalArticle.class.getName(), PortletProvider.Action.EDIT));
		subscriptionSender.setScopeGroupId(article.getGroupId());
		subscriptionSender.setServiceContext(serviceContext);
	}

	private void _removeArticleLocale(JournalArticle article, String languageId)
		throws PortalException {

		DDMFormValues ddmFormValues = article.getDDMFormValues();

		Set<Locale> availableLocales = ddmFormValues.getAvailableLocales();

		Locale locale = LocaleUtil.fromLanguageId(languageId);

		if (!availableLocales.contains(locale)) {
			return;
		}

		DDMStructure ddmStructure = article.getDDMStructure();

		availableLocales.remove(locale);

		_removeDDMFormFieldValues(
			ddmFormValues.getDDMFormFieldValues(), locale);

		_ddmFieldLocalService.updateDDMFormValues(
			ddmStructure.getStructureId(), article.getId(), ddmFormValues);
	}

	private void _removeDDMFormFieldValues(
		List<DDMFormFieldValue> ddmFormFieldValues, Locale locale) {

		for (DDMFormFieldValue ddmFormFieldValue : ddmFormFieldValues) {
			Value value = ddmFormFieldValue.getValue();

			if ((value != null) && value.isLocalized()) {
				value.removeLocale(locale);
			}

			if (ListUtil.isNotEmpty(
					ddmFormFieldValue.getNestedDDMFormFieldValues())) {

				_removeDDMFormFieldValues(
					ddmFormFieldValue.getNestedDDMFormFieldValues(), locale);
			}
		}
	}

	private String _removeTrailingSlashes(String title) {
		if (Validator.isNull(title) || !title.endsWith(StringPool.SLASH)) {
			return title;
		}

		int end = title.length();

		while ((end > 0) && (title.charAt(end - 1) == CharPool.SLASH)) {
			end--;
		}

		return title.substring(0, end);
	}

	private String _replaceTempImages(JournalArticle article, String content)
		throws PortalException {

		return _attachmentContentUpdater.updateContent(
			content, ContentTypes.TEXT_HTML,
			fileEntry -> {
				String fileEntryName = DLUtil.getUniqueFileName(
					fileEntry.getGroupId(), fileEntry.getFolderId(),
					fileEntry.getFileName(), false);

				Folder folder = article.addImagesFolder();

				_portletFileRepository.addPortletFileEntry(
					null, article.getGroupId(), fileEntry.getUserId(),
					JournalArticle.class.getName(),
					article.getResourcePrimKey(), JournalConstants.SERVICE_NAME,
					folder.getFolderId(), fileEntry.getContentStream(),
					fileEntryName, fileEntry.getMimeType(), false);

				return null;
			});
	}

	private String _toJSON(
		String alt, JournalArticle article, FileEntry fileEntry, String height,
		String type, String url, String width) {

		return JSONUtil.put(
			"alt", alt
		).put(
			"description", alt
		).put(
			"fileEntryId", fileEntry.getFileEntryId()
		).put(
			"groupId", fileEntry.getGroupId()
		).put(
			"height", height
		).put(
			"name", fileEntry.getFileName()
		).put(
			"resourcePrimKey", article.getResourcePrimKey()
		).put(
			"title", fileEntry.getTitle()
		).put(
			"type", type
		).put(
			"url", url
		).put(
			"uuid", fileEntry.getUuid()
		).put(
			"width", width
		).toString();
	}

	private List<JournalArticleLocalization> _updateArticleLocalizedFields(
		long companyId, long articleId, Map<Locale, String> titleMap,
		Map<Locale, String> descriptionMap) {

		List<JournalArticleLocalization> oldJournalArticleLocalizations =
			new ArrayList<>(
				_journalArticleLocalizationPersistence.findByC_A(
					companyId, articleId));

		List<JournalArticleLocalization> newJournalArticleLocalizations =
			_addArticleLocalizedFields(
				companyId, articleId, titleMap, descriptionMap);

		oldJournalArticleLocalizations.removeAll(
			newJournalArticleLocalizations);

		for (JournalArticleLocalization oldJournalArticleLocalization :
				oldJournalArticleLocalizations) {

			_journalArticleLocalizationPersistence.remove(
				oldJournalArticleLocalization);
		}

		return newJournalArticleLocalizations;
	}

	private JournalArticleLocalization _updateArticleLocalizedFields(
		long companyId, long articleId, String title, String description,
		String languageId) {

		JournalArticleLocalization journalArticleLocalization =
			_journalArticleLocalizationPersistence.fetchByC_A_L(
				companyId, articleId, languageId);

		if (journalArticleLocalization == null) {
			return _addArticleLocalizedFields(
				companyId, articleId, title, description, languageId);
		}

		journalArticleLocalization.setTitle(title);
		journalArticleLocalization.setDescription(description);

		return _journalArticleLocalizationPersistence.update(
			journalArticleLocalization);
	}

	private void _updateDDMStructurePredefinedValues(
			long userId, long ddmStructureId, DDMFormValues ddmFormValues,
			ServiceContext serviceContext)
		throws PortalException {

		boolean update = false;

		DDMStructure ddmStructure = _ddmStructureLocalService.getStructure(
			ddmStructureId);

		DDMForm ddmForm = ddmStructure.getDDMForm();

		Map<String, DDMFormField> ddmFormFieldsMap =
			ddmForm.getDDMFormFieldsMap(true);

		Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap =
			ddmFormValues.getDDMFormFieldValuesMap(true);

		for (Map.Entry<String, List<DDMFormFieldValue>> entry :
				ddmFormFieldValuesMap.entrySet()) {

			DDMFormField ddmFormField = ddmFormFieldsMap.get(entry.getKey());

			if (ddmFormField != null) {
				List<DDMFormFieldValue> ddmFormFieldValues = entry.getValue();

				DDMFormFieldValue ddmFormFieldValue = ddmFormFieldValues.get(0);

				LocalizedValue localizedValue =
					(LocalizedValue)ddmFormFieldValue.getValue();

				if (!_equals(
						ddmFormField.getPredefinedValue(), localizedValue,
						ddmFormField.getType())) {

					ddmFormField.setPredefinedValue(localizedValue);

					update = true;
				}
			}
		}

		if (update) {
			_ddmStructureLocalService.updateStructure(
				userId, ddmStructureId, ddmForm,
				ddmStructure.getDDMFormLayout(), serviceContext);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JournalArticleLocalServiceImpl.class);

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetDisplayPageEntryFormProcessor
		_assetDisplayPageEntryFormProcessor;

	@Reference
	private AssetDisplayPageEntryLocalService
		_assetDisplayPageEntryLocalService;

	@Reference
	private AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private AssetLinkLocalService _assetLinkLocalService;

	@Reference
	private AssetTagLocalService _assetTagLocalService;

	@Reference
	private AttachmentContentUpdater _attachmentContentUpdater;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CommentManager _commentManager;

	private final Map<Long, Date> _companyIdPreviousCheckDate =
		new ConcurrentHashMap<>();

	@Reference
	private DDMFieldLocalService _ddmFieldLocalService;

	@Reference
	private DDMFormValuesToFieldsConverter _ddmFormValuesToFieldsConverter;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private DDMStructureVersionLocalService _ddmStructureVersionLocalService;

	@Reference
	private DiffHtml _diffHtml;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DLURLHelper _dlURLHelper;

	@Reference
	private ExpandoRowLocalService _expandoRowLocalService;

	@Reference
	private FieldsToDDMFormValuesConverter _fieldsToDDMFormValuesConverter;

	@Reference
	private FriendlyURLNormalizer _friendlyURLNormalizer;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ImageLocalService _imageLocalService;

	@Reference
	private JournalArticleLocalizationPersistence
		_journalArticleLocalizationPersistence;

	@Reference
	private JournalArticleResourceLocalService
		_journalArticleResourceLocalService;

	@Reference
	private JournalArticleResourcePersistence
		_journalArticleResourcePersistence;

	@Reference
	private JournalContentCompatibilityConverter
		_journalContentCompatibilityConverter;

	@Reference
	private JournalConverter _journalConverter;

	@Reference
	private JournalDefaultTemplateProvider _journalDefaultTemplateProvider;

	@Reference
	private JournalFolderPersistence _journalFolderPersistence;

	@Reference
	private JournalHelper _journalHelper;

	private final JournalTransformer _journalTransformer =
		new JournalTransformer();

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private LayoutDisplayPageProviderRegistry
		_layoutDisplayPageProviderRegistry;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private Localization _localization;

	@Reference
	private Portal _portal;

	@Reference
	private PortletFileRepository _portletFileRepository;

	@Reference
	private RatingsStatsLocalService _ratingsStatsLocalService;

	@Reference
	private ResourceLocalService _resourceLocalService;

	private ServiceTrackerList<TransformerListener> _serviceTrackerList;

	@Reference
	private SubscriptionLocalService _subscriptionLocalService;

	@Reference
	private SystemEventLocalService _systemEventLocalService;

	@Reference
	private TrashEntryLocalService _trashEntryLocalService;

	@Reference
	private TrashHelper _trashHelper;

	@Reference
	private TrashVersionLocalService _trashVersionLocalService;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private WorkflowInstanceLinkLocalService _workflowInstanceLinkLocalService;

}