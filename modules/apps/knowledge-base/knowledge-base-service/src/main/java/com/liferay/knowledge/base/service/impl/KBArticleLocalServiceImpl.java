/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.service.impl;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.link.constants.AssetLinkConstants;
import com.liferay.asset.link.model.AssetLink;
import com.liferay.asset.link.service.AssetLinkLocalService;
import com.liferay.diff.DiffHtml;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.knowledge.base.configuration.KBGroupServiceConfiguration;
import com.liferay.knowledge.base.constants.AdminActivityKeys;
import com.liferay.knowledge.base.constants.KBArticleConstants;
import com.liferay.knowledge.base.constants.KBConstants;
import com.liferay.knowledge.base.constants.KBFolderConstants;
import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.knowledge.base.exception.DuplicateKBArticleExternalReferenceCodeException;
import com.liferay.knowledge.base.exception.KBArticleContentException;
import com.liferay.knowledge.base.exception.KBArticleDisplayDateException;
import com.liferay.knowledge.base.exception.KBArticleExpirationDateException;
import com.liferay.knowledge.base.exception.KBArticleParentException;
import com.liferay.knowledge.base.exception.KBArticlePriorityException;
import com.liferay.knowledge.base.exception.KBArticleReviewDateException;
import com.liferay.knowledge.base.exception.KBArticleSourceURLException;
import com.liferay.knowledge.base.exception.KBArticleStatusException;
import com.liferay.knowledge.base.exception.KBArticleTitleException;
import com.liferay.knowledge.base.exception.KBArticleUrlTitleException;
import com.liferay.knowledge.base.exception.LockedKBArticleException;
import com.liferay.knowledge.base.exception.NoSuchArticleException;
import com.liferay.knowledge.base.internal.configuration.KBServiceConfiguration;
import com.liferay.knowledge.base.internal.helper.KBArticleLocalSiblingNavigationHelper;
import com.liferay.knowledge.base.internal.importer.KBArticleImporter;
import com.liferay.knowledge.base.internal.util.AdminSubscriptionSenderFactory;
import com.liferay.knowledge.base.internal.util.KBArticleDiffUtil;
import com.liferay.knowledge.base.internal.util.KBCommentUtil;
import com.liferay.knowledge.base.internal.util.KBSectionEscapeUtil;
import com.liferay.knowledge.base.internal.util.constants.KnowledgeBaseConstants;
import com.liferay.knowledge.base.markdown.converter.factory.MarkdownConverterFactory;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.model.KBArticleTable;
import com.liferay.knowledge.base.model.KBFolder;
import com.liferay.knowledge.base.service.base.KBArticleLocalServiceBaseImpl;
import com.liferay.knowledge.base.service.persistence.KBCommentPersistence;
import com.liferay.knowledge.base.service.persistence.KBFolderPersistence;
import com.liferay.knowledge.base.util.KnowledgeBaseUtil;
import com.liferay.knowledge.base.util.comparator.KBArticlePriorityComparator;
import com.liferay.knowledge.base.util.comparator.KBArticleVersionComparator;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.bean.BeanProperties;
import com.liferay.portal.kernel.dao.orm.Conjunction;
import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.Disjunction;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Junction;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.lock.DuplicateLockException;
import com.liferay.portal.kernel.lock.Lock;
import com.liferay.portal.kernel.lock.LockManager;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.notifications.UserNotificationDefinition;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.systemevent.SystemEventHierarchyEntryThreadLocal;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionCommitCallbackUtil;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.GroupThreadLocal;
import com.liferay.portal.kernel.util.HtmlParser;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.SubscriptionSender;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.view.count.ViewCountManager;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;
import com.liferay.portal.kernel.workflow.WorkflowThreadLocal;
import com.liferay.portal.kernel.zip.ZipReaderFactory;
import com.liferay.ratings.kernel.service.RatingsStatsLocalService;
import com.liferay.social.kernel.model.SocialActivityConstants;
import com.liferay.social.kernel.service.SocialActivityLocalService;
import com.liferay.subscription.model.Subscription;
import com.liferay.subscription.service.SubscriptionLocalService;
import com.liferay.trash.TrashHelper;
import com.liferay.trash.exception.RestoreEntryException;
import com.liferay.trash.exception.TrashEntryException;
import com.liferay.trash.model.TrashEntry;
import com.liferay.trash.model.TrashVersion;
import com.liferay.trash.service.TrashEntryLocalService;
import com.liferay.trash.service.TrashVersionLocalService;

import java.io.InputStream;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Peter Shin
 * @author Brian Wing Shun Chan
 * @author Edward Han
 */
@Component(
	configurationPid = "com.liferay.knowledge.base.internal.configuration.KBServiceConfiguration",
	property = "model.class.name=com.liferay.knowledge.base.model.KBArticle",
	service = AopService.class
)
public class KBArticleLocalServiceImpl extends KBArticleLocalServiceBaseImpl {

	@Override
	public FileEntry addAttachment(
			long userId, long resourcePrimKey, String fileName,
			InputStream inputStream, String mimeType)
		throws PortalException {

		KBArticle kbArticle = kbArticleLocalService.getLatestKBArticle(
			resourcePrimKey, WorkflowConstants.STATUS_ANY);

		return _portletFileRepository.addPortletFileEntry(
			null, kbArticle.getGroupId(), userId, KBArticle.class.getName(),
			resourcePrimKey, KBConstants.SERVICE_NAME,
			kbArticle.getAttachmentsFolderId(), inputStream, fileName, mimeType,
			true);
	}

	@Override
	public KBArticle addKBArticle(
			String externalReferenceCode, long userId,
			long parentResourceClassNameId, long parentResourcePrimKey,
			String title, String urlTitle, String content, String description,
			String[] sections, String sourceURL, Date displayDate,
			Date expirationDate, Date reviewDate, String[] selectedFileNames,
			ServiceContext serviceContext)
		throws PortalException {

		// KB article

		User user = _userLocalService.getUser(userId);

		long groupId = serviceContext.getScopeGroupId();
		urlTitle = _normalizeUrlTitle(urlTitle);
		double priority = _getPriority(groupId, parentResourcePrimKey);

		long kbArticleId = counterLocalService.increment();

		_validateExternalReferenceCode(externalReferenceCode, groupId);

		_validate(
			title, content, sourceURL, displayDate, expirationDate, reviewDate);
		_validateParent(parentResourceClassNameId, parentResourcePrimKey);

		long kbFolderId = KnowledgeBaseUtil.getKBFolderId(
			parentResourceClassNameId, parentResourcePrimKey);

		urlTitle = StringUtil.toLowerCase(urlTitle);

		_validateUrlTitle(groupId, kbFolderId, urlTitle);

		long resourcePrimKey = counterLocalService.increment();

		long rootResourcePrimKey = _getRootResourcePrimKey(
			resourcePrimKey, parentResourceClassNameId, parentResourcePrimKey);

		KBArticle kbArticle = kbArticlePersistence.create(kbArticleId);

		kbArticle.setUuid(serviceContext.getUuid());
		kbArticle.setResourcePrimKey(resourcePrimKey);
		kbArticle.setGroupId(groupId);
		kbArticle.setCompanyId(user.getCompanyId());
		kbArticle.setUserId(user.getUserId());
		kbArticle.setUserName(user.getFullName());
		kbArticle.setExternalReferenceCode(externalReferenceCode);
		kbArticle.setRootResourcePrimKey(rootResourcePrimKey);
		kbArticle.setParentResourceClassNameId(parentResourceClassNameId);
		kbArticle.setParentResourcePrimKey(parentResourcePrimKey);
		kbArticle.setKbFolderId(kbFolderId);
		kbArticle.setVersion(KBArticleConstants.DEFAULT_VERSION);
		kbArticle.setTitle(title);
		kbArticle.setUrlTitle(
			_getUniqueUrlTitle(
				groupId, kbFolderId, kbArticleId, title, urlTitle));
		kbArticle.setContent(content);
		kbArticle.setDescription(description);
		kbArticle.setPriority(priority);
		kbArticle.setSections(
			StringUtil.merge(KBSectionEscapeUtil.escapeSections(sections)));
		kbArticle.setLatest(true);
		kbArticle.setMain(false);
		kbArticle.setSourceURL(sourceURL);
		kbArticle.setDisplayDate(displayDate);
		kbArticle.setExpirationDate(expirationDate);
		kbArticle.setReviewDate(reviewDate);
		kbArticle.setStatus(WorkflowConstants.STATUS_DRAFT);
		kbArticle.setExpandoBridgeAttributes(serviceContext);

		kbArticle = kbArticlePersistence.update(kbArticle);

		// Resources

		if (serviceContext.isAddGroupPermissions() ||
			serviceContext.isAddGuestPermissions()) {

			addKBArticleResources(
				kbArticle, serviceContext.isAddGroupPermissions(),
				serviceContext.isAddGuestPermissions());
		}
		else {
			addKBArticleResources(
				kbArticle, serviceContext.getModelPermissions());
		}

		// Asset

		updateKBArticleAsset(
			userId, kbArticle, serviceContext.getAssetCategoryIds(),
			serviceContext.getAssetTagNames(),
			serviceContext.getAssetLinkEntryIds());

		// Attachments

		_addKBArticleAttachments(userId, kbArticle, selectedFileNames);

		// Workflow

		_startWorkflowInstance(userId, kbArticle, serviceContext);

		return kbArticle;
	}

	@Override
	public void addKBArticleResources(
			KBArticle kbArticle, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		_resourceLocalService.addResources(
			kbArticle.getCompanyId(), kbArticle.getGroupId(),
			kbArticle.getUserId(), KBArticle.class.getName(),
			kbArticle.getResourcePrimKey(), false, addGroupPermissions,
			addGuestPermissions);
	}

	@Override
	public void addKBArticleResources(
			KBArticle kbArticle, ModelPermissions modelPermissions)
		throws PortalException {

		_resourceLocalService.addModelResources(
			kbArticle.getCompanyId(), kbArticle.getGroupId(),
			kbArticle.getUserId(), KBArticle.class.getName(),
			kbArticle.getResourcePrimKey(), modelPermissions);
	}

	@Override
	public void addKBArticleResources(
			long kbArticleId, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		KBArticle kbArticle = kbArticlePersistence.findByPrimaryKey(
			kbArticleId);

		addKBArticleResources(
			kbArticle, addGroupPermissions, addGuestPermissions);
	}

	@Override
	public int addKBArticlesMarkdown(
			long userId, long groupId, long parentKbFolderId, String fileName,
			boolean prioritizeByNumericalPrefix, InputStream inputStream,
			ServiceContext serviceContext)
		throws PortalException {

		boolean workflowEnabled = WorkflowThreadLocal.isEnabled();

		try {
			WorkflowThreadLocal.setEnabled(false);

			KBArticleImporter kbArticleImporter = new KBArticleImporter(
				_configurationProvider, _markdownConverterFactory.create(),
				this, _portal, _dlURLHelper, _zipReaderFactory);

			return kbArticleImporter.processZipFile(
				userId, groupId, parentKbFolderId, prioritizeByNumericalPrefix,
				inputStream, serviceContext);
		}
		finally {
			WorkflowThreadLocal.setEnabled(workflowEnabled);
		}
	}

	@Override
	public void addTempAttachment(
			long groupId, long userId, String fileName, String tempFolderName,
			InputStream inputStream, String mimeType)
		throws PortalException {

		TempFileEntryUtil.addTempFileEntry(
			groupId, userId, tempFolderName, fileName, inputStream, mimeType);
	}

	@Override
	public void checkKBArticles(long companyId) throws PortalException {
		Company company = _companyLocalService.getCompany(companyId);

		Date date = new Date();

		long userId = _userLocalService.getGuestUserId(company.getCompanyId());

		_checkKBArticlesByDisplayDate(company, date, userId);

		_checkKBArticlesByExpirationDate(company, date, userId);

		_dates.computeIfAbsent(
			companyId,
			key -> new Date(
				date.getTime() - (_getKBArticleCheckInterval() * Time.MINUTE)));

		_checkKBArticlesByReviewDate(company, date, userId);

		_dates.put(companyId, date);
	}

	@Override
	public void deleteGroupKBArticles(long groupId) throws PortalException {

		// KB articles

		deleteKBArticles(groupId, KBFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		// Subscriptions

		Group group = _groupLocalService.getGroup(groupId);

		List<Subscription> subscriptions =
			_subscriptionLocalService.getSubscriptions(
				group.getCompanyId(), KBArticle.class.getName(), groupId);

		for (Subscription subscription : subscriptions) {
			unsubscribeGroupKBArticles(subscription.getUserId(), groupId);
		}
	}

	@Override
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public KBArticle deleteKBArticle(KBArticle kbArticle)
		throws PortalException {

		return kbArticleLocalService.deleteKBArticle(
			PrincipalThreadLocal.getUserId(), kbArticle.getResourcePrimKey(),
			kbArticle.getVersion());
	}

	@Override
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public KBArticle deleteKBArticle(long resourcePrimKey)
		throws PortalException {

		KBArticle kbArticle = getLatestKBArticle(
			resourcePrimKey, WorkflowConstants.STATUS_ANY);

		return kbArticleLocalService.deleteKBArticle(
			PrincipalThreadLocal.getUserId(), kbArticle.getResourcePrimKey(),
			kbArticle.getVersion());
	}

	@Override
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public KBArticle deleteKBArticle(
			long userId, long resourcePrimKey, int version)
		throws PortalException {

		KBArticle kbArticle = kbArticlePersistence.findByR_V(
			resourcePrimKey, version);

		if (!hasKBArticleLock(userId, resourcePrimKey)) {
			lockKBArticle(userId, resourcePrimKey);
		}

		try {

			// Child KB articles

			deleteKBArticles(
				kbArticle.getGroupId(), kbArticle.getResourcePrimKey());

			// Resources

			_resourceLocalService.deleteResource(
				kbArticle.getCompanyId(), KBArticle.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				kbArticle.getResourcePrimKey());

			// KB articles

			kbArticlePersistence.removeByResourcePrimKey(
				kbArticle.getResourcePrimKey());

			// KB comments

			KBCommentUtil.deleteKBComments(
				KBArticle.class.getName(), _classNameLocalService,
				kbArticle.getResourcePrimKey(), _kbCommentPersistence);

			// Asset

			_deleteAssets(kbArticle);

			// Expando

			_expandoRowLocalService.deleteRows(kbArticle.getKbArticleId());

			// Ratings

			_ratingsStatsLocalService.deleteStats(
				KBArticle.class.getName(), kbArticle.getResourcePrimKey());

			// Social

			_socialActivityLocalService.deleteActivities(
				KBArticle.class.getName(), kbArticle.getResourcePrimKey());

			// Indexer

			Indexer<KBArticle> indexer = _indexerRegistry.getIndexer(
				KBArticle.class);

			indexer.delete(kbArticle);

			// Attachments

			if (!GroupThreadLocal.isDeleteInProcess()) {
				_portletFileRepository.deletePortletFolder(
					kbArticle.getAttachmentsFolderId());
			}

			// Subscriptions

			_deleteSubscriptions(kbArticle);

			// Trash

			if (kbArticle.isInTrash()) {
				TrashEntry trashEntry = _trashEntryLocalService.deleteEntry(
					KBArticle.class.getName(), kbArticle.getResourcePrimKey());

				if (trashEntry == null) {
					_trashVersionLocalService.deleteTrashVersion(
						KBArticle.class.getName(),
						kbArticle.getResourcePrimKey());
				}
			}

			// View count

			_viewCountManager.deleteViewCount(
				kbArticle.getCompanyId(),
				_classNameLocalService.getClassNameId(KBArticle.class),
				kbArticle.getPrimaryKey());

			// Workflow

			_workflowInstanceLinkLocalService.deleteWorkflowInstanceLinks(
				kbArticle.getCompanyId(), kbArticle.getGroupId(),
				KBArticle.class.getName(), kbArticle.getResourcePrimKey());
		}
		finally {
			unlockKBArticle(userId, resourcePrimKey);
		}

		return kbArticle;
	}

	@Override
	public void deleteKBArticles(long groupId, long parentResourcePrimKey)
		throws PortalException {

		deleteKBArticles(groupId, parentResourcePrimKey, true);
	}

	@Override
	public void deleteKBArticles(
			long groupId, long parentResourcePrimKey,
			boolean includeTrashedEntries)
		throws PortalException {

		for (KBArticle kbArticle :
				getKBArticles(
					groupId, parentResourcePrimKey,
					WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			if (includeTrashedEntries ||
				!_trashHelper.isInTrashExplicitly(kbArticle)) {

				kbArticleLocalService.deleteKBArticle(kbArticle);
			}
		}

		for (KBArticle kbArticle :
				getKBArticles(
					groupId, parentResourcePrimKey,
					WorkflowConstants.STATUS_IN_TRASH, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			if (includeTrashedEntries ||
				!_trashHelper.isInTrashExplicitly(kbArticle)) {

				kbArticleLocalService.deleteKBArticle(kbArticle);
			}
		}
	}

	@Override
	public void deleteKBArticles(long[] resourcePrimKeys)
		throws PortalException {

		List<KBArticle> kbArticles = getKBArticles(
			resourcePrimKeys, WorkflowConstants.STATUS_ANY, null);

		for (KBArticle kbArticle : kbArticles) {
			kbArticleLocalService.deleteKBArticle(kbArticle);
		}
	}

	@Override
	public void deleteTempAttachment(
			long groupId, long userId, String fileName, String tempFolderName)
		throws PortalException {

		TempFileEntryUtil.deleteTempFileEntry(
			groupId, userId, tempFolderName, fileName);
	}

	@Override
	public KBArticle expireKBArticle(
			long userId, long resourcePrimKey, ServiceContext serviceContext)
		throws PortalException {

		if (!hasKBArticleLock(userId, resourcePrimKey)) {
			lockKBArticle(userId, resourcePrimKey);
		}

		try {
			KBArticle kbArticle = getLatestKBArticle(
				resourcePrimKey, WorkflowConstants.STATUS_ANY);

			if (kbArticle.isDraft() || kbArticle.isPending() ||
				kbArticle.isScheduled()) {

				return kbArticle;
			}

			kbArticle.setExpirationDate(new Date());

			kbArticleLocalService.updateKBArticle(kbArticle);

			return updateStatus(
				userId, resourcePrimKey, WorkflowConstants.STATUS_EXPIRED,
				serviceContext);
		}
		finally {
			unlockKBArticle(userId, resourcePrimKey);
		}
	}

	@Override
	public KBArticle fetchFirstChildKBArticle(
		long groupId, long parentResourcePrimKey) {

		return kbArticlePersistence.fetchByG_P_L_NotS_First(
			groupId, parentResourcePrimKey, true,
			WorkflowConstants.STATUS_IN_TRASH,
			KBArticlePriorityComparator.getInstance(true));
	}

	@Override
	public KBArticle fetchKBArticle(
		long resourcePrimKey, long groupId, int version) {

		return kbArticlePersistence.fetchByR_G_V(
			resourcePrimKey, groupId, version);
	}

	@Override
	public KBArticle fetchKBArticleByExternalReferenceCode(
		long groupId, String externalReferenceCode, int version) {

		return kbArticlePersistence.fetchByG_ERC_V(
			groupId, externalReferenceCode, version);
	}

	@Override
	public KBArticle fetchKBArticleByUrlTitle(
		long groupId, long kbFolderId, String urlTitle) {

		urlTitle = StringUtil.replaceFirst(
			urlTitle, CharPool.SLASH, StringPool.BLANK);

		KBArticle kbArticle = fetchLatestKBArticleByUrlTitle(
			groupId, kbFolderId, urlTitle, WorkflowConstants.STATUS_APPROVED);

		if (kbArticle == null) {
			kbArticle = fetchLatestKBArticleByUrlTitle(
				groupId, kbFolderId, urlTitle,
				WorkflowConstants.STATUS_PENDING);
		}

		return kbArticle;
	}

	@Override
	public KBArticle fetchKBArticleByUrlTitle(
		long groupId, String kbFolderUrlTitle, String urlTitle) {

		urlTitle = StringUtil.replaceFirst(
			urlTitle, CharPool.SLASH, StringPool.BLANK);

		List<KBArticle> kbArticles = kbArticleFinder.findByUrlTitle(
			groupId, kbFolderUrlTitle, urlTitle, _STATUSES, 0, 1);

		if (kbArticles.isEmpty()) {
			return null;
		}

		return kbArticles.get(0);
	}

	@Override
	public KBArticle fetchLatestKBArticle(long resourcePrimKey, int status) {
		if (status == WorkflowConstants.STATUS_ANY) {
			return kbArticlePersistence.fetchByResourcePrimKey_First(
				resourcePrimKey, KBArticleVersionComparator.getInstance(false));
		}

		return kbArticlePersistence.fetchByR_S_First(
			resourcePrimKey, status,
			KBArticleVersionComparator.getInstance(false));
	}

	@Override
	public KBArticle fetchLatestKBArticle(long resourcePrimKey, long groupId) {
		return kbArticlePersistence.fetchByR_G_L_First(
			resourcePrimKey, groupId, true, null);
	}

	@Override
	public KBArticle fetchLatestKBArticleByExternalReferenceCode(
		long groupId, String externalReferenceCode) {

		return kbArticlePersistence.fetchByG_ERC_Last(
			groupId, externalReferenceCode,
			KBArticleVersionComparator.getInstance(false));
	}

	@Override
	public KBArticle fetchLatestKBArticleByExternalReferenceCode(
		long groupId, String externalReferenceCode, int status) {

		KBArticle latestKBArticle = kbArticlePersistence.fetchByG_ERC_Last(
			groupId, externalReferenceCode,
			KBArticleVersionComparator.getInstance(false));

		if ((latestKBArticle == null) ||
			(status == WorkflowConstants.STATUS_ANY) ||
			(latestKBArticle.getStatus() == status)) {

			return latestKBArticle;
		}

		return kbArticlePersistence.fetchByG_ERC_S_First(
			groupId, externalReferenceCode, status,
			KBArticleVersionComparator.getInstance(false));
	}

	@Override
	public KBArticle fetchLatestKBArticleByUrlTitle(
		long groupId, long kbFolderId, String urlTitle, int status) {

		urlTitle = StringUtil.replaceFirst(
			urlTitle, CharPool.SLASH, StringPool.BLANK);

		List<KBArticle> kbArticles = null;

		OrderByComparator<KBArticle> orderByComparator =
			KBArticleVersionComparator.getInstance(false);

		if (status == WorkflowConstants.STATUS_ANY) {
			kbArticles = kbArticlePersistence.findByG_KBFI_UT_NotS(
				groupId, kbFolderId, urlTitle,
				WorkflowConstants.STATUS_IN_TRASH, 0, 1, orderByComparator);
		}
		else {
			kbArticles = kbArticlePersistence.findByG_KBFI_UT_S(
				groupId, kbFolderId, urlTitle, status, 0, 1, orderByComparator);
		}

		if (kbArticles.isEmpty()) {
			return null;
		}

		return kbArticles.get(0);
	}

	@Override
	public PersistedModel fetchPersistedModel(Serializable primaryKeyObj) {
		PersistedModel persistedModel = kbArticlePersistence.fetchByPrimaryKey(
			primaryKeyObj);

		if (persistedModel == null) {
			persistedModel = fetchLatestKBArticle(
				GetterUtil.getLong(primaryKeyObj),
				WorkflowConstants.STATUS_APPROVED);
		}

		return persistedModel;
	}

	@Override
	public List<KBArticle> getAllDescendantKBArticles(
		long resourcePrimKey, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return _getAllDescendantKBArticles(
			resourcePrimKey, status, orderByComparator, false);
	}

	@Override
	public List<KBArticle> getCompanyKBArticles(
		long companyId, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return kbArticlePersistence.findByC_L_NotS(
				companyId, true, WorkflowConstants.STATUS_IN_TRASH, start, end,
				orderByComparator);
		}
		else if (status == WorkflowConstants.STATUS_APPROVED) {
			return kbArticlePersistence.findByC_M_NotS(
				companyId, true, WorkflowConstants.STATUS_IN_TRASH, start, end,
				orderByComparator);
		}

		return kbArticlePersistence.findByC_S(
			companyId, status, start, end, orderByComparator);
	}

	@Override
	public int getCompanyKBArticlesCount(long companyId, int status) {
		if (status == WorkflowConstants.STATUS_ANY) {
			return kbArticlePersistence.countByC_L_NotS(
				companyId, true, WorkflowConstants.STATUS_IN_TRASH);
		}
		else if (status == WorkflowConstants.STATUS_APPROVED) {
			return kbArticlePersistence.countByC_M_NotS(
				companyId, true, WorkflowConstants.STATUS_IN_TRASH);
		}

		return kbArticlePersistence.countByC_S(companyId, status);
	}

	@Override
	public List<KBArticle> getGroupKBArticles(
		long groupId, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return kbArticlePersistence.findByG_L_NotS(
				groupId, true, WorkflowConstants.STATUS_IN_TRASH, start, end,
				orderByComparator);
		}
		else if (status == WorkflowConstants.STATUS_APPROVED) {
			return kbArticlePersistence.findByG_M_NotS(
				groupId, true, WorkflowConstants.STATUS_IN_TRASH, start, end,
				orderByComparator);
		}

		return kbArticlePersistence.findByG_S(
			groupId, status, start, end, orderByComparator);
	}

	@Override
	public int getGroupKBArticlesCount(long groupId, int status) {
		if (status == WorkflowConstants.STATUS_ANY) {
			return kbArticlePersistence.countByG_L_NotS(
				groupId, true, WorkflowConstants.STATUS_IN_TRASH);
		}
		else if (status == WorkflowConstants.STATUS_APPROVED) {
			return kbArticlePersistence.countByG_M_NotS(
				groupId, true, WorkflowConstants.STATUS_IN_TRASH);
		}

		return kbArticlePersistence.countByG_S(groupId, status);
	}

	@Override
	public KBArticle getKBArticle(long resourcePrimKey, int version)
		throws PortalException {

		return kbArticlePersistence.findByR_V(resourcePrimKey, version);
	}

	@Override
	public List<KBArticle> getKBArticleAndAllDescendantKBArticles(
		long resourcePrimKey, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return _getAllDescendantKBArticles(
			resourcePrimKey, status, orderByComparator, true);
	}

	@Override
	public KBArticle getKBArticleByUrlTitle(
			long groupId, long kbFolderId, String urlTitle)
		throws PortalException {

		urlTitle = StringUtil.replaceFirst(
			urlTitle, CharPool.SLASH, StringPool.BLANK);

		// Get the latest KB article that is approved, if none are approved, get
		// the latest unapproved KB article

		KBArticle kbArticle = fetchKBArticleByUrlTitle(
			groupId, kbFolderId, urlTitle);

		if (kbArticle == null) {
			throw new NoSuchArticleException(
				StringBundler.concat(
					"No KBArticle exists with the key {groupId=", groupId,
					", kbFolderId=", kbFolderId, ", urlTitle=", urlTitle, "}"));
		}

		return kbArticle;
	}

	@Override
	public KBArticle getKBArticleByUrlTitle(
			long groupId, String kbFolderUrlTitle, String urlTitle)
		throws PortalException {

		urlTitle = StringUtil.replaceFirst(
			urlTitle, CharPool.SLASH, StringPool.BLANK);

		KBArticle kbArticle = fetchKBArticleByUrlTitle(
			groupId, kbFolderUrlTitle, urlTitle);

		if (kbArticle == null) {
			throw new NoSuchArticleException(
				StringBundler.concat(
					"No KBArticle with the key {groupId=", groupId,
					", urlTitle=", urlTitle,
					"} found in a folder with URL title ", kbFolderUrlTitle));
		}

		return kbArticle;
	}

	@Override
	public List<KBArticle> getKBArticles(
		long groupId, long parentResourcePrimKey, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return kbArticlePersistence.findByG_P_L_NotS(
				groupId, parentResourcePrimKey, true,
				WorkflowConstants.STATUS_IN_TRASH, start, end,
				orderByComparator);
		}
		else if (status == WorkflowConstants.STATUS_APPROVED) {
			return kbArticlePersistence.findByG_P_M_NotS(
				groupId, parentResourcePrimKey, true,
				WorkflowConstants.STATUS_IN_TRASH, start, end,
				orderByComparator);
		}

		return kbArticlePersistence.findByG_P_S(
			groupId, parentResourcePrimKey, status, start, end,
			orderByComparator);
	}

	@Override
	public List<KBArticle> getKBArticles(
		long[] resourcePrimKeys, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		List<KBArticle> kbArticles = new ArrayList<>();

		Long[][] params = {ArrayUtil.toArray(resourcePrimKeys)};

		while ((params = KnowledgeBaseUtil.getParams(params[0])) != null) {
			List<KBArticle> curKBArticles = null;

			if (status == WorkflowConstants.STATUS_ANY) {
				curKBArticles = kbArticlePersistence.findByR_L_NotS(
					ArrayUtil.toArray(params[1]), true,
					WorkflowConstants.STATUS_IN_TRASH);
			}
			else if (status == WorkflowConstants.STATUS_APPROVED) {
				curKBArticles = kbArticlePersistence.findByR_M_NotS(
					ArrayUtil.toArray(params[1]), true,
					WorkflowConstants.STATUS_IN_TRASH);
			}
			else {
				curKBArticles = kbArticlePersistence.findByR_S(
					ArrayUtil.toArray(params[1]), new int[] {status});
			}

			kbArticles.addAll(curKBArticles);
		}

		if (orderByComparator != null) {
			kbArticles = ListUtil.sort(kbArticles, orderByComparator);
		}
		else {
			kbArticles = KnowledgeBaseUtil.sort(resourcePrimKeys, kbArticles);
		}

		return Collections.unmodifiableList(kbArticles);
	}

	@Override
	public int getKBArticlesCount(
		long groupId, long parentResourcePrimKey, int status) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return kbArticlePersistence.countByG_P_L_NotS(
				groupId, parentResourcePrimKey, true,
				WorkflowConstants.STATUS_IN_TRASH);
		}
		else if (status == WorkflowConstants.STATUS_APPROVED) {
			return kbArticlePersistence.countByG_P_M_NotS(
				groupId, parentResourcePrimKey, true,
				WorkflowConstants.STATUS_IN_TRASH);
		}

		return kbArticlePersistence.countByG_P_S(
			groupId, parentResourcePrimKey, status);
	}

	@Override
	public List<KBArticle> getKBArticleVersions(
		long resourcePrimKey, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return kbArticlePersistence.findByResourcePrimKey(
				resourcePrimKey, start, end, orderByComparator);
		}

		return kbArticlePersistence.findByR_S(
			resourcePrimKey, status, start, end, orderByComparator);
	}

	@Override
	public int getKBArticleVersionsCount(long resourcePrimKey, int status) {
		if (status == WorkflowConstants.STATUS_ANY) {
			return kbArticlePersistence.countByResourcePrimKey(resourcePrimKey);
		}

		return kbArticlePersistence.countByR_S(resourcePrimKey, status);
	}

	@Override
	public List<KBArticle> getKBFolderKBArticles(
		long groupId, long kbFolderId) {

		return kbArticlePersistence.findByG_KBFI_L_NotS(
			groupId, kbFolderId, true, WorkflowConstants.STATUS_IN_TRASH);
	}

	@Override
	public int getKBFolderKBArticlesCount(
		long groupId, long kbFolderId, int status) {

		return kbArticlePersistence.countByG_KBFI_S(
			groupId, kbFolderId, status);
	}

	@Override
	public KBArticle getLatestKBArticle(long resourcePrimKey)
		throws PortalException {

		return getLatestKBArticle(
			resourcePrimKey, WorkflowConstants.STATUS_ANY);
	}

	@Override
	public KBArticle getLatestKBArticle(long resourcePrimKey, int status)
		throws PortalException {

		if (status == WorkflowConstants.STATUS_ANY) {
			return kbArticlePersistence.findByResourcePrimKey_First(
				resourcePrimKey, KBArticleVersionComparator.getInstance(false));
		}

		return kbArticlePersistence.findByR_S_First(
			resourcePrimKey, status,
			KBArticleVersionComparator.getInstance(false));
	}

	@Override
	public KBArticle getLatestKBArticle(long resourcePrimKey, int[] statuses)
		throws PortalException {

		if (ArrayUtil.contains(statuses, WorkflowConstants.STATUS_ANY)) {
			return kbArticlePersistence.findByResourcePrimKey_First(
				resourcePrimKey, KBArticleVersionComparator.getInstance(false));
		}

		List<KBArticle> kbArticles = kbArticlePersistence.findByR_S(
			new long[] {resourcePrimKey}, statuses, 0, 1,
			KBArticleVersionComparator.getInstance(false));

		if (!kbArticles.isEmpty()) {
			return kbArticles.get(0);
		}

		return null;
	}

	@Override
	public KBArticle getLatestKBArticleByExternalReferenceCode(
			long groupId, String externalReferenceCode)
		throws PortalException {

		return kbArticlePersistence.findByG_ERC_First(
			groupId, externalReferenceCode,
			KBArticleVersionComparator.getInstance(false));
	}

	@Override
	public KBArticle getLatestKBArticleByUrlTitle(
			long groupId, long kbFolderId, String urlTitle, int status)
		throws PortalException {

		urlTitle = StringUtil.replaceFirst(
			urlTitle, CharPool.SLASH, StringPool.BLANK);

		KBArticle latestKBArticle = fetchLatestKBArticleByUrlTitle(
			groupId, kbFolderId, urlTitle, status);

		if (latestKBArticle == null) {
			throw new NoSuchArticleException(
				StringBundler.concat(
					"No KBArticle exists with the key {groupId=", groupId,
					", kbFolderId=", kbFolderId, ", urlTitle=", urlTitle,
					", status=", status, "}"));
		}

		return latestKBArticle;
	}

	@Override
	public KBArticle[] getPreviousAndNextKBArticles(long kbArticleId)
		throws PortalException {

		KBArticleLocalSiblingNavigationHelper
			kbArticleLocalSiblingNavigationHelper =
				new KBArticleLocalSiblingNavigationHelper(kbArticlePersistence);

		return kbArticleLocalSiblingNavigationHelper.
			getPreviousAndNextKBArticles(kbArticleId);
	}

	@Override
	public List<KBArticle> getSectionsKBArticles(
		long groupId, String[] sections, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		String[] array = KBSectionEscapeUtil.escapeSections(sections);

		for (int i = 0; i < array.length; i++) {
			array[i] = StringUtil.quote(array[i], StringPool.PERCENT);
		}

		if (status == WorkflowConstants.STATUS_ANY) {
			return kbArticlePersistence.findByG_LikeS_L_NotS(
				groupId, array, true, WorkflowConstants.STATUS_IN_TRASH, start,
				end, orderByComparator);
		}
		else if (status == WorkflowConstants.STATUS_APPROVED) {
			return kbArticlePersistence.findByG_LikeS_M_NotS(
				groupId, array, true, WorkflowConstants.STATUS_IN_TRASH, start,
				end, orderByComparator);
		}

		return kbArticlePersistence.findByG_LikeS_S(
			groupId, array, status, start, end, orderByComparator);
	}

	@Override
	public int getSectionsKBArticlesCount(
		long groupId, String[] sections, int status) {

		String[] array = KBSectionEscapeUtil.escapeSections(sections);

		for (int i = 0; i < array.length; i++) {
			array[i] = StringUtil.quote(array[i], StringPool.PERCENT);
		}

		if (status == WorkflowConstants.STATUS_ANY) {
			return kbArticlePersistence.countByG_LikeS_L_NotS(
				groupId, array, true, WorkflowConstants.STATUS_IN_TRASH);
		}
		else if (status == WorkflowConstants.STATUS_APPROVED) {
			return kbArticlePersistence.countByG_LikeS_M_NotS(
				groupId, array, true, WorkflowConstants.STATUS_IN_TRASH);
		}

		return kbArticlePersistence.countByG_LikeS_S(groupId, array, status);
	}

	@Override
	public String[] getTempAttachmentNames(
			long groupId, long userId, String tempFolderName)
		throws PortalException {

		return TempFileEntryUtil.getTempFileNames(
			groupId, userId, tempFolderName);
	}

	@Override
	public boolean hasKBArticleLock(long userId, long resourcePrimKey) {
		return _lockManager.hasLock(
			userId, KBArticleConstants.getClassName(), resourcePrimKey);
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public void incrementViewCount(
			long userId, long resourcePrimKey, int increment)
		throws PortalException {

		KBArticle kbArticle = getLatestKBArticle(
			resourcePrimKey, WorkflowConstants.STATUS_ANY);
		long classNameId = _classNameLocalService.getClassNameId(
			KBArticle.class);

		_viewCountManager.incrementViewCount(
			kbArticle.getCompanyId(), classNameId, kbArticle.getPrimaryKey(),
			increment);

		if (kbArticle.isApproved() || kbArticle.isFirstVersion()) {
			return;
		}

		kbArticle = getLatestKBArticle(
			resourcePrimKey, WorkflowConstants.STATUS_APPROVED);

		_viewCountManager.incrementViewCount(
			kbArticle.getCompanyId(), classNameId, kbArticle.getPrimaryKey(),
			increment);
	}

	@Override
	public Lock lockKBArticle(long userId, long resourcePrimKey)
		throws PortalException {

		long companyId = 0;

		try {
			companyId = _companyLocalService.getCompanyIdByUserId(userId);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		if (!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-11003")) {
			return null;
		}

		if (userId <= 0) {
			Lock lock = _lockManager.fetchLock(
				KBArticleConstants.getClassName(), resourcePrimKey);

			if (lock != null) {
				LockedKBArticleException lockedKBArticleException =
					new LockedKBArticleException();

				lockedKBArticleException.setLock(lock);

				throw lockedKBArticleException;
			}

			return null;
		}

		try {
			return _lockManager.lock(
				userId, KBArticleConstants.getClassName(), resourcePrimKey,
				String.valueOf(userId), false,
				KBArticleConstants.LOCK_EXPIRATION_TIME);
		}
		catch (DuplicateLockException duplicateLockException) {
			throw new LockedKBArticleException(duplicateLockException);
		}
	}

	@Override
	public void moveDependentKBArticlesToTrash(
			long parentResourcePrimKey, long trashEntryId)
		throws PortalException {

		List<KBArticle> allDescendantKBArticles = getAllDescendantKBArticles(
			parentResourcePrimKey, WorkflowConstants.STATUS_ANY, null);

		for (KBArticle descendantKBArticle : allDescendantKBArticles) {
			_moveDependentKBArticleToTrash(descendantKBArticle, trashEntryId);
		}
	}

	@Override
	public void moveDependentKBArticleToTrash(
			KBArticle kbArticle, long trashEntryId)
		throws PortalException {

		_moveDependentKBArticleToTrash(kbArticle, trashEntryId);

		moveDependentKBArticlesToTrash(
			kbArticle.getResourcePrimKey(), trashEntryId);
	}

	@Override
	public void moveKBArticle(
			long userId, long resourcePrimKey, long parentResourceClassNameId,
			long parentResourcePrimKey, double priority)
		throws PortalException {

		if (!hasKBArticleLock(userId, resourcePrimKey)) {
			lockKBArticle(userId, resourcePrimKey);
		}

		try {
			KBArticle kbArticle = getLatestKBArticle(
				resourcePrimKey, WorkflowConstants.STATUS_ANY);

			if (kbArticle.getResourcePrimKey() == parentResourcePrimKey) {
				return;
			}

			_validateParent(
				kbArticle, parentResourceClassNameId, parentResourcePrimKey);
			_validateParentStatus(
				parentResourceClassNameId, parentResourcePrimKey,
				kbArticle.getStatus());
			_validatePriority(priority);

			_updatePermissionFields(
				resourcePrimKey, parentResourceClassNameId,
				parentResourcePrimKey);

			long kbFolderClassNameId = _classNameLocalService.getClassNameId(
				KBFolderConstants.getClassName());

			long kbFolderId = KBFolderConstants.DEFAULT_PARENT_FOLDER_ID;

			if (parentResourceClassNameId == kbFolderClassNameId) {
				kbFolderId = parentResourcePrimKey;
			}
			else {
				KBArticle parentKBArticle = getLatestKBArticle(
					parentResourcePrimKey, WorkflowConstants.STATUS_ANY);

				kbFolderId = parentKBArticle.getKbFolderId();
			}

			List<KBArticle> kbArticles = getKBArticleVersions(
				resourcePrimKey, WorkflowConstants.STATUS_ANY,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				KBArticleVersionComparator.getInstance(false));

			for (KBArticle curKBArticle : kbArticles) {
				curKBArticle.setParentResourceClassNameId(
					parentResourceClassNameId);
				curKBArticle.setParentResourcePrimKey(parentResourcePrimKey);
				curKBArticle.setKbFolderId(kbFolderId);
				curKBArticle.setPriority(priority);

				curKBArticle = kbArticlePersistence.update(curKBArticle);

				_indexKBArticle(curKBArticle);
			}

			if (kbArticle.getKbFolderId() != kbFolderId) {
				List<KBArticle> descendantKBArticles =
					getAllDescendantKBArticles(
						resourcePrimKey, WorkflowConstants.STATUS_ANY, null);

				for (KBArticle curKBArticle : descendantKBArticles) {
					List<KBArticle> kbArticleVersions = getKBArticleVersions(
						curKBArticle.getResourcePrimKey(),
						WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS,
						QueryUtil.ALL_POS,
						KBArticleVersionComparator.getInstance(false));

					for (KBArticle kbArticleVersion : kbArticleVersions) {
						kbArticleVersion.setKbFolderId(kbFolderId);

						kbArticleVersion = kbArticlePersistence.update(
							kbArticleVersion);

						_indexKBArticle(kbArticleVersion);
					}
				}
			}

			KBArticle latestKBArticle = getLatestKBArticle(
				resourcePrimKey, WorkflowConstants.STATUS_ANY);

			JSONObject extraDataJSONObject = JSONUtil.put(
				"title", latestKBArticle.getTitle());

			if (latestKBArticle.isApproved() ||
				!latestKBArticle.isFirstVersion()) {

				_socialActivityLocalService.addActivity(
					userId, latestKBArticle.getGroupId(),
					KBArticle.class.getName(), resourcePrimKey,
					AdminActivityKeys.MOVE_KB_ARTICLE,
					extraDataJSONObject.toString(), 0);
			}

			_indexKBArticle(latestKBArticle);
		}
		finally {
			unlockKBArticle(userId, resourcePrimKey);
		}
	}

	@Override
	public void moveKBArticleFromTrash(
			long userId, long resourcePrimKey, long parentResourceClassNameId,
			long parentResourcePrimKey)
		throws PortalException {

		KBArticle kbArticle = getLatestKBArticle(resourcePrimKey);

		if (!kbArticle.isInTrash()) {
			throw new RestoreEntryException(
				RestoreEntryException.INVALID_STATUS);
		}

		if (_trashHelper.isInTrashExplicitly(kbArticle)) {
			restoreKBArticleFromTrash(userId, resourcePrimKey);
		}
		else {
			restoreDependentKBArticleFromTrash(kbArticle);
		}

		moveKBArticle(
			userId, kbArticle.getResourcePrimKey(), parentResourceClassNameId,
			parentResourcePrimKey, kbArticle.getPriority());
	}

	@Override
	public KBArticle moveKBArticleToTrash(long userId, long resourcePrimKey)
		throws PortalException {

		if (!hasKBArticleLock(userId, resourcePrimKey)) {
			lockKBArticle(userId, resourcePrimKey);
		}

		try {
			KBArticle kbArticle = getLatestKBArticle(resourcePrimKey);

			if (kbArticle.isInTrash()) {
				throw new TrashEntryException();
			}

			long classPK = kbArticle.getClassPK();
			int oldStatus = kbArticle.getStatus();

			kbArticle = _updateStatus(
				userId, kbArticle, WorkflowConstants.STATUS_IN_TRASH);

			_assetEntryLocalService.updateVisible(
				KBArticle.class.getName(), classPK, false);

			JSONObject extraDataJSONObject = JSONUtil.put(
				"title", kbArticle.getTitle());

			_socialActivityLocalService.addActivity(
				userId, kbArticle.getGroupId(), KBArticle.class.getName(),
				resourcePrimKey, SocialActivityConstants.TYPE_MOVE_TO_TRASH,
				extraDataJSONObject.toString(), 0);

			TrashEntry trashEntry = _trashEntryLocalService.addTrashEntry(
				userId, kbArticle.getGroupId(), KBArticle.class.getName(),
				resourcePrimKey, kbArticle.getUuid(), null, oldStatus, null,
				null);

			moveDependentKBArticlesToTrash(
				resourcePrimKey, trashEntry.getEntryId());

			return kbArticle;
		}
		finally {
			unlockKBArticle(userId, resourcePrimKey);
		}
	}

	@Override
	public void restoreDependentKBArticleFromTrash(KBArticle kbArticle)
		throws PortalException {

		_restoreDependentKBArticleFromTrash(kbArticle);
		restoreDependentKBArticlesFromTrash(kbArticle.getResourcePrimKey());
	}

	@Override
	public void restoreDependentKBArticlesFromTrash(long parentResourcePrimKey)
		throws PortalException {

		List<KBArticle> allDescendantKBArticles = getAllDescendantKBArticles(
			parentResourcePrimKey, WorkflowConstants.STATUS_IN_TRASH, null);

		for (KBArticle descendantKBArticle : allDescendantKBArticles) {
			_restoreDependentKBArticleFromTrash(descendantKBArticle);
		}
	}

	@Override
	public void restoreKBArticleFromTrash(long userId, long resourcePrimKey)
		throws PortalException {

		KBArticle kbArticle = getLatestKBArticle(resourcePrimKey);

		if (!kbArticle.isInTrash()) {
			throw new RestoreEntryException(
				RestoreEntryException.INVALID_STATUS);
		}

		TrashEntry trashEntry = _trashEntryLocalService.getEntry(
			KBArticle.class.getName(), resourcePrimKey);

		kbArticle = _updateStatus(userId, kbArticle, trashEntry.getStatus());

		if (kbArticle.isApproved()) {
			_assetEntryLocalService.updateVisible(
				KBArticle.class.getName(), resourcePrimKey, true);
		}

		JSONObject extraDataJSONObject = JSONUtil.put(
			"title", kbArticle.getTitle());

		_socialActivityLocalService.addActivity(
			userId, kbArticle.getGroupId(), KBArticle.class.getName(),
			resourcePrimKey, SocialActivityConstants.TYPE_RESTORE_FROM_TRASH,
			extraDataJSONObject.toString(), 0);

		restoreDependentKBArticlesFromTrash(resourcePrimKey);

		_trashEntryLocalService.deleteEntry(
			KBArticle.class.getName(), resourcePrimKey);
	}

	@Override
	public KBArticle revertKBArticle(
			long userId, long resourcePrimKey, int version,
			ServiceContext serviceContext)
		throws PortalException {

		if (!hasKBArticleLock(userId, resourcePrimKey)) {
			lockKBArticle(userId, resourcePrimKey);
		}

		try {
			KBArticle kbArticle = kbArticleLocalService.getKBArticle(
				resourcePrimKey, version);

			ExpandoBridge expandoBridge = kbArticle.getExpandoBridge();

			serviceContext.setExpandoBridgeAttributes(
				expandoBridge.getAttributes());

			return updateKBArticle(
				userId, resourcePrimKey, kbArticle.getTitle(),
				kbArticle.getContent(), kbArticle.getDescription(),
				StringUtil.split(kbArticle.getSections()),
				kbArticle.getSourceURL(), kbArticle.getDisplayDate(),
				kbArticle.getExpirationDate(), kbArticle.getReviewDate(), null,
				null, serviceContext);
		}
		finally {
			unlockKBArticle(userId, resourcePrimKey);
		}
	}

	@Override
	public List<KBArticle> search(
		long groupId, String title, String content, int status, Date startDate,
		Date endDate, boolean andOperator, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		DynamicQuery dynamicQuery = _buildDynamicQuery(
			groupId, title, content, status, startDate, endDate, andOperator);

		return dynamicQuery(dynamicQuery, start, end, orderByComparator);
	}

	@Override
	public void subscribeGroupKBArticles(long userId, long groupId)
		throws PortalException {

		_subscriptionLocalService.addSubscription(
			userId, groupId, KBArticle.class.getName(), groupId);
	}

	@Override
	public void subscribeKBArticle(
			long userId, long groupId, long resourcePrimKey)
		throws PortalException {

		_subscriptionLocalService.addSubscription(
			userId, groupId, KBArticle.class.getName(), resourcePrimKey);
	}

	@Override
	public void unlockKBArticle(long userId, long resourcePrimKey) {
		unlockKBArticle(userId, resourcePrimKey, false);
	}

	@Override
	public void unlockKBArticle(
		long userId, long resourcePrimKey, boolean force) {

		long companyId = 0;

		try {
			companyId = _companyLocalService.getCompanyIdByUserId(userId);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		if (!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-11003")) {
			return;
		}

		if (force ||
			_lockManager.hasLock(
				userId, KBArticleConstants.getClassName(), resourcePrimKey)) {

			_lockManager.unlock(
				KBArticleConstants.getClassName(), resourcePrimKey);
		}
	}

	@Override
	public void unsubscribeGroupKBArticles(long userId, long groupId)
		throws PortalException {

		_subscriptionLocalService.deleteSubscription(
			userId, KBArticle.class.getName(), groupId);
	}

	@Override
	public void unsubscribeKBArticle(long userId, long resourcePrimKey)
		throws PortalException {

		_subscriptionLocalService.deleteSubscription(
			userId, KBArticle.class.getName(), resourcePrimKey);
	}

	@Override
	public KBArticle updateAndUnlockKBArticle(
			long userId, long resourcePrimKey, String title, String content,
			String description, String[] sections, String sourceURL,
			Date displayDate, Date expirationDate, Date reviewDate,
			String[] selectedFileNames, long[] removeFileEntryIds,
			ServiceContext serviceContext)
		throws PortalException {

		try {
			return updateKBArticle(
				userId, resourcePrimKey, title, content, description, sections,
				sourceURL, displayDate, expirationDate, reviewDate,
				selectedFileNames, removeFileEntryIds, serviceContext);
		}
		finally {
			unlockKBArticle(userId, resourcePrimKey);
		}
	}

	@Override
	public KBArticle updateKBArticle(
			long userId, long resourcePrimKey, String title, String content,
			String description, String[] sections, String sourceURL,
			Date displayDate, Date expirationDate, Date reviewDate,
			String[] selectedFileNames, long[] removeFileEntryIds,
			ServiceContext serviceContext)
		throws PortalException {

		// KB article

		boolean autoLock = false;

		if (!hasKBArticleLock(userId, resourcePrimKey)) {
			lockKBArticle(userId, resourcePrimKey);

			autoLock = true;
		}

		try {
			User user = _userLocalService.getUser(userId);

			_validate(
				title, content, sourceURL, displayDate, expirationDate,
				reviewDate);

			KBArticle oldKBArticle = getLatestKBArticle(
				resourcePrimKey, WorkflowConstants.STATUS_ANY);

			int oldVersion = oldKBArticle.getVersion();

			KBArticle kbArticle = null;

			if (oldKBArticle.isApproved() || oldKBArticle.isExpired()) {
				long kbArticleId = counterLocalService.increment();

				kbArticle = kbArticlePersistence.create(kbArticleId);

				kbArticle.setUuid(serviceContext.getUuid());
				kbArticle.setResourcePrimKey(oldKBArticle.getResourcePrimKey());
				kbArticle.setGroupId(oldKBArticle.getGroupId());
				kbArticle.setCompanyId(user.getCompanyId());
				kbArticle.setUserId(user.getUserId());
				kbArticle.setUserName(user.getFullName());
				kbArticle.setCreateDate(oldKBArticle.getCreateDate());
				kbArticle.setExternalReferenceCode(
					oldKBArticle.getExternalReferenceCode());
				kbArticle.setRootResourcePrimKey(
					oldKBArticle.getRootResourcePrimKey());
				kbArticle.setParentResourceClassNameId(
					oldKBArticle.getParentResourceClassNameId());
				kbArticle.setParentResourcePrimKey(
					oldKBArticle.getParentResourcePrimKey());
				kbArticle.setKbFolderId(oldKBArticle.getKbFolderId());
				kbArticle.setVersion(oldVersion + 1);
				kbArticle.setUrlTitle(oldKBArticle.getUrlTitle());
				kbArticle.setPriority(oldKBArticle.getPriority());

				_viewCountManager.incrementViewCount(
					kbArticle.getCompanyId(),
					_classNameLocalService.getClassNameId(KBArticle.class),
					kbArticle.getPrimaryKey(),
					(int)oldKBArticle.getViewCount());

				// Indexer

				Indexer<KBArticle> indexer = _indexerRegistry.getIndexer(
					KBArticle.class);

				indexer.delete(oldKBArticle);
			}
			else {
				kbArticle = oldKBArticle;
			}

			if (oldKBArticle.isPending()) {
				kbArticle.setStatus(WorkflowConstants.STATUS_PENDING);
			}
			else {
				kbArticle.setStatus(WorkflowConstants.STATUS_DRAFT);
			}

			kbArticle.setTitle(title);
			kbArticle.setContent(content);
			kbArticle.setDescription(description);
			kbArticle.setSections(
				StringUtil.merge(KBSectionEscapeUtil.escapeSections(sections)));
			kbArticle.setLatest(true);
			kbArticle.setMain(false);
			kbArticle.setSourceURL(sourceURL);
			kbArticle.setDisplayDate(displayDate);
			kbArticle.setExpirationDate(expirationDate);
			kbArticle.setReviewDate(reviewDate);
			kbArticle.setExpandoBridgeAttributes(serviceContext);

			kbArticle = kbArticlePersistence.update(kbArticle);

			if (oldKBArticle.isApproved() || oldKBArticle.isExpired()) {
				oldKBArticle.setModifiedDate(oldKBArticle.getModifiedDate());
				oldKBArticle.setLatest(false);

				kbArticlePersistence.update(oldKBArticle);
			}

			// Asset

			updateKBArticleAsset(
				userId, kbArticle, serviceContext.getAssetCategoryIds(),
				serviceContext.getAssetTagNames(),
				serviceContext.getAssetLinkEntryIds());

			// Attachments

			_addKBArticleAttachments(userId, kbArticle, selectedFileNames);

			_removeKBArticleAttachments(removeFileEntryIds);

			// Indexer

			_indexKBArticle(kbArticle);

			// Workflow

			_startWorkflowInstance(userId, kbArticle, serviceContext);

			return kbArticle;
		}
		finally {
			if (autoLock && hasKBArticleLock(userId, resourcePrimKey)) {
				unlockKBArticle(userId, resourcePrimKey);
			}
		}
	}

	@Override
	public void updateKBArticleAsset(
			long userId, KBArticle kbArticle, long[] assetCategoryIds,
			String[] assetTagNames, long[] assetLinkEntryIds)
		throws PortalException {

		boolean visible = false;

		if (kbArticle.isApproved()) {
			visible = true;
		}

		String summary = _htmlParser.extractText(
			StringUtil.shorten(kbArticle.getContent(), 500));

		AssetEntry assetEntry = _assetEntryLocalService.updateEntry(
			userId, kbArticle.getGroupId(), kbArticle.getCreateDate(),
			kbArticle.getModifiedDate(), KBArticle.class.getName(),
			kbArticle.getClassPK(), kbArticle.getUuid(), 0, assetCategoryIds,
			assetTagNames, true, visible, null, null, null,
			kbArticle.getExpirationDate(), ContentTypes.TEXT_HTML,
			kbArticle.getTitle(), kbArticle.getDescription(), summary, null,
			null, 0, 0, null);

		_assetLinkLocalService.updateLinks(
			userId, assetEntry.getEntryId(), assetLinkEntryIds,
			AssetLinkConstants.TYPE_RELATED);
	}

	@Override
	public void updateKBArticleResources(
			KBArticle kbArticle, String[] groupPermissions,
			String[] guestPermissions)
		throws PortalException {

		_resourceLocalService.updateResources(
			kbArticle.getCompanyId(), kbArticle.getGroupId(),
			KBArticle.class.getName(), kbArticle.getResourcePrimKey(),
			groupPermissions, guestPermissions);
	}

	@Override
	public void updateKBArticlesPriorities(
			Map<Long, Double> resourcePrimKeyToPriorityMap)
		throws PortalException {

		for (double priority : resourcePrimKeyToPriorityMap.values()) {
			_validatePriority(priority);
		}

		long[] resourcePrimKeys = StringUtil.split(
			StringUtil.merge(resourcePrimKeyToPriorityMap.keySet()), 0L);

		List<KBArticle> kbArticles = getKBArticles(
			resourcePrimKeys, WorkflowConstants.STATUS_ANY, null);

		for (KBArticle kbArticle : kbArticles) {
			double priority = resourcePrimKeyToPriorityMap.get(
				kbArticle.getResourcePrimKey());

			updatePriority(kbArticle.getResourcePrimKey(), priority);
		}
	}

	@Override
	public void updatePriority(long resourcePrimKey, double priority) {
		List<KBArticle> kbArticleVersions = getKBArticleVersions(
			resourcePrimKey, WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		for (KBArticle kbArticle : kbArticleVersions) {
			kbArticle.setPriority(priority);

			kbArticlePersistence.update(kbArticle);
		}
	}

	@Override
	public KBArticle updateStatus(
			long userId, long resourcePrimKey, int status,
			ServiceContext serviceContext)
		throws PortalException {

		// KB article

		User user = _userLocalService.getUser(userId);
		boolean main = false;
		Date date = new Date();

		KBArticle kbArticle = getLatestKBArticle(
			resourcePrimKey, WorkflowConstants.STATUS_ANY);

		if (status == WorkflowConstants.STATUS_APPROVED) {
			main = true;

			if (date.before(kbArticle.getDisplayDate())) {
				status = WorkflowConstants.STATUS_SCHEDULED;
			}
		}

		_validateParentStatus(
			kbArticle.getParentResourceClassNameId(),
			kbArticle.getParentResourcePrimKey(), status);

		kbArticle.setMain(main);
		kbArticle.setStatus(status);
		kbArticle.setStatusByUserId(user.getUserId());
		kbArticle.setStatusByUserName(user.getFullName());
		kbArticle.setStatusDate(serviceContext.getModifiedDate(date));

		kbArticle = kbArticlePersistence.update(kbArticle);

		if ((status != WorkflowConstants.STATUS_APPROVED) &&
			(status != WorkflowConstants.STATUS_EXPIRED)) {

			return kbArticle;
		}

		if (!kbArticle.isFirstVersion()) {
			KBArticle oldKBArticle = kbArticlePersistence.findByR_V(
				resourcePrimKey, kbArticle.getVersion() - 1);

			oldKBArticle.setModifiedDate(oldKBArticle.getModifiedDate());
			oldKBArticle.setMain(false);

			oldKBArticle = kbArticlePersistence.update(oldKBArticle);

			// Indexer

			Indexer<KBArticle> indexer = _indexerRegistry.getIndexer(
				KBArticle.class);

			indexer.delete(oldKBArticle);
		}

		// Asset

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			KBArticle.class.getName(), kbArticle.getResourcePrimKey());

		if (status == WorkflowConstants.STATUS_APPROVED) {
			assetEntry = _assetEntryLocalService.getEntry(
				KBArticle.class.getName(), kbArticle.getKbArticleId());
		}

		List<AssetLink> assetLinks = _assetLinkLocalService.getDirectLinks(
			assetEntry.getEntryId(), AssetLinkConstants.TYPE_RELATED);

		long[] assetLinkEntryIds = StringUtil.split(
			ListUtil.toString(assetLinks, AssetLink.ENTRY_ID2_ACCESSOR), 0L);

		updateKBArticleAsset(
			userId, kbArticle, assetEntry.getCategoryIds(),
			assetEntry.getTagNames(), assetLinkEntryIds);

		SystemEventHierarchyEntryThreadLocal.push(KBArticle.class);

		try {
			_assetEntryLocalService.deleteEntry(
				KBArticle.class.getName(), kbArticle.getKbArticleId());
		}
		finally {
			SystemEventHierarchyEntryThreadLocal.pop(KBArticle.class);
		}

		boolean visible = false;

		if (kbArticle.isApproved()) {
			visible = true;
		}

		_assetEntryLocalService.updateVisible(
			KBArticle.class.getName(), kbArticle.getResourcePrimKey(), visible);

		// Social

		JSONObject extraDataJSONObject = JSONUtil.put(
			"title", kbArticle.getTitle());

		if (!kbArticle.isFirstVersion()) {
			_socialActivityLocalService.addActivity(
				userId, kbArticle.getGroupId(), KBArticle.class.getName(),
				resourcePrimKey, AdminActivityKeys.UPDATE_KB_ARTICLE,
				extraDataJSONObject.toString(), 0);
		}
		else {
			_socialActivityLocalService.addActivity(
				userId, kbArticle.getGroupId(), KBArticle.class.getName(),
				resourcePrimKey, AdminActivityKeys.ADD_KB_ARTICLE,
				extraDataJSONObject.toString(), 0);
		}

		// Indexer

		_indexKBArticle(kbArticle);

		// Subscriptions

		Set<String> receivers = SetUtil.fromArray(
			_NOTIFICATION_RECEIVER_SUBSCRIBER);

		if (status == WorkflowConstants.STATUS_EXPIRED) {
			receivers.add(_NOTIFICATION_RECEIVER_OWNER);
		}

		_notify(
			receivers, userId, kbArticle, _getAction(kbArticle, status),
			serviceContext);

		return kbArticle;
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_kbServiceConfiguration = ConfigurableUtil.createConfigurable(
			KBServiceConfiguration.class, properties);
	}

	private void _addKBArticleAttachment(
			long userId, long groupId, long resourcePrimKey,
			String selectedFileName)
		throws PortalException {

		FileEntry tempFileEntry = TempFileEntryUtil.getTempFileEntry(
			groupId, userId, KnowledgeBaseConstants.TEMP_FOLDER_NAME,
			selectedFileName);

		InputStream inputStream = tempFileEntry.getContentStream();

		addAttachment(
			userId, resourcePrimKey, selectedFileName, inputStream,
			tempFileEntry.getMimeType());

		if (tempFileEntry != null) {
			TempFileEntryUtil.deleteTempFileEntry(
				tempFileEntry.getFileEntryId());
		}
	}

	private void _addKBArticleAttachments(
			long userId, KBArticle kbArticle, String[] selectedFileNames)
		throws PortalException {

		if (ArrayUtil.isEmpty(selectedFileNames)) {
			return;
		}

		for (String selectedFileName : selectedFileNames) {
			_addKBArticleAttachment(
				userId, kbArticle.getGroupId(), kbArticle.getResourcePrimKey(),
				selectedFileName);
		}
	}

	private DynamicQuery _buildDynamicQuery(
		long groupId, String title, String content, int status, Date startDate,
		Date endDate, boolean andOperator) {

		Junction junction = null;

		if (andOperator) {
			junction = RestrictionsFactoryUtil.conjunction();
		}
		else {
			junction = RestrictionsFactoryUtil.disjunction();
		}

		Map<String, String> terms = new HashMap<>();

		if (Validator.isNotNull(title)) {
			terms.put("title", title);
		}

		if (Validator.isNotNull(content)) {
			terms.put("content", content);
		}

		for (Map.Entry<String, String> entry : terms.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();

			Disjunction disjunction = RestrictionsFactoryUtil.disjunction();

			for (String keyword : KnowledgeBaseUtil.splitKeywords(value)) {
				Criterion criterion = RestrictionsFactoryUtil.ilike(
					key, StringUtil.quote(keyword, StringPool.PERCENT));

				disjunction.add(criterion);
			}

			junction.add(disjunction);
		}

		if (status != WorkflowConstants.STATUS_ANY) {
			Property property = PropertyFactoryUtil.forName("status");

			junction.add(property.eq(status));
		}

		if ((endDate != null) && (startDate != null)) {
			Disjunction disjunction = RestrictionsFactoryUtil.disjunction();

			String[] propertyNames = {"createDate", "modifiedDate"};

			for (String propertyName : propertyNames) {
				Property property = PropertyFactoryUtil.forName(propertyName);

				Conjunction conjunction = RestrictionsFactoryUtil.conjunction();

				conjunction.add(property.gt(startDate));
				conjunction.add(property.lt(endDate));

				disjunction.add(conjunction);
			}

			junction.add(disjunction);
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			KBArticle.class, getClassLoader());

		if (status == WorkflowConstants.STATUS_ANY) {
			Property property = PropertyFactoryUtil.forName("latest");

			dynamicQuery.add(property.eq(Boolean.TRUE));
		}
		else if (status == WorkflowConstants.STATUS_APPROVED) {
			Property property = PropertyFactoryUtil.forName("main");

			dynamicQuery.add(property.eq(Boolean.TRUE));
		}

		if (groupId > 0) {
			Property property = PropertyFactoryUtil.forName("groupId");

			dynamicQuery.add(property.eq(groupId));
		}

		return dynamicQuery.add(junction);
	}

	private void _checkKBArticlesByDisplayDate(
			Company company, Date displayDate, long userId)
		throws PortalException {

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Publishing knowledge base articles with display date ",
					"prior to ", displayDate, " for company ",
					company.getCompanyId()));
		}

		List<KBArticle> kbArticles = _getKBArticlesByCompanyIdAndDisplayDate(
			company.getCompanyId(), displayDate);

		for (KBArticle kbArticle : kbArticles) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Publish knowledge base article ",
						kbArticle.getKbArticleId(), " with display date ",
						kbArticle.getDisplayDate()));
			}

			updateStatus(
				userId, kbArticle.getResourcePrimKey(),
				WorkflowConstants.STATUS_APPROVED,
				_getServiceContext(company, kbArticle));
		}
	}

	private void _checkKBArticlesByExpirationDate(
			Company company, Date expirationDate, long userId)
		throws PortalException {

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Expiring knowledge base articles with expiration date ",
					"prior to ", expirationDate, " for company ",
					company.getCompanyId()));
		}

		List<KBArticle> kbArticles = _getKBArticlesByCompanyIdAndExpirationDate(
			company.getCompanyId(), expirationDate);

		for (KBArticle kbArticle : kbArticles) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Expiring knowledge base article ",
						kbArticle.getKbArticleId(), " with expiration date ",
						kbArticle.getExpirationDate()));
			}

			updateStatus(
				userId, kbArticle.getResourcePrimKey(),
				WorkflowConstants.STATUS_EXPIRED,
				_getServiceContext(company, kbArticle));
		}
	}

	private void _checkKBArticlesByReviewDate(
			Company company, Date reviewDate, long userId)
		throws PortalException {

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Sending review notification for knowledge base articles ",
					"with review date between ",
					_dates.get(company.getCompanyId()), " and ", reviewDate,
					" for company ", company.getCompanyId()));
		}

		List<KBArticle> kbArticles = _getKBArticlesByCompanyIdAndReviewDate(
			company.getCompanyId(), _dates.get(company.getCompanyId()),
			reviewDate);

		for (KBArticle kbArticle : kbArticles) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Sending review notification for knowledge base ",
						"article ", kbArticle.getKbArticleId(),
						" with reviewDate ", kbArticle.getReviewDate()));
			}

			_notify(
				SetUtil.fromArray(
					_NOTIFICATION_RECEIVER_OWNER,
					_NOTIFICATION_RECEIVER_SUBSCRIBER),
				userId, kbArticle, _NOTIFICATION_ACTION_REVIEW,
				_getServiceContext(company, kbArticle));
		}
	}

	private void _deleteAssets(KBArticle kbArticle) throws PortalException {
		_assetEntryLocalService.deleteEntry(
			KBArticle.class.getName(), kbArticle.getClassPK());

		if (!kbArticle.isApproved()) {
			int kbArticleVersionsCount =
				kbArticleLocalService.getKBArticleVersionsCount(
					kbArticle.getResourcePrimKey(),
					WorkflowConstants.STATUS_ANY);

			if ((kbArticleVersionsCount == 0) || !kbArticle.isFirstVersion()) {
				_assetEntryLocalService.deleteEntry(
					KBArticle.class.getName(), kbArticle.getResourcePrimKey());
			}
		}
	}

	private void _deleteSubscriptions(KBArticle kbArticle)
		throws PortalException {

		List<Subscription> subscriptions =
			_subscriptionLocalService.getSubscriptions(
				kbArticle.getCompanyId(), KBArticle.class.getName(),
				kbArticle.getResourcePrimKey());

		for (Subscription subscription : subscriptions) {
			unsubscribeKBArticle(
				subscription.getUserId(), subscription.getClassPK());
		}
	}

	private String _getAction(KBArticle kbArticle, int status) {
		if (status == WorkflowConstants.STATUS_EXPIRED) {
			return Constants.EXPIRE;
		}

		if (kbArticle.isFirstVersion()) {
			return Constants.ADD;
		}

		return Constants.UPDATE;
	}

	private void _getAllDescendantKBArticles(
		List<KBArticle> kbArticles, long resourcePrimKey, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		List<KBArticle> curKBArticles = null;

		if (status == WorkflowConstants.STATUS_ANY) {
			curKBArticles = kbArticlePersistence.findByP_L_NotS(
				resourcePrimKey, true, WorkflowConstants.STATUS_IN_TRASH,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, orderByComparator);
		}
		else if (status == WorkflowConstants.STATUS_APPROVED) {
			curKBArticles = kbArticlePersistence.findByP_M_NotS(
				resourcePrimKey, true, WorkflowConstants.STATUS_IN_TRASH,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, orderByComparator);
		}
		else {
			curKBArticles = kbArticlePersistence.findByP_S(
				resourcePrimKey, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				orderByComparator);
		}

		for (KBArticle curKBArticle : curKBArticles) {
			kbArticles.add(curKBArticle);

			_getAllDescendantKBArticles(
				kbArticles, curKBArticle.getResourcePrimKey(), status,
				orderByComparator);
		}
	}

	private List<KBArticle> _getAllDescendantKBArticles(
		long resourcePrimKey, int status,
		OrderByComparator<KBArticle> orderByComparator,
		boolean includeParentArticle) {

		List<KBArticle> kbArticles = null;

		if (includeParentArticle) {
			kbArticles = getKBArticles(
				new long[] {resourcePrimKey}, status, null);

			kbArticles = ListUtil.copy(kbArticles);
		}
		else {
			kbArticles = new ArrayList<>();
		}

		_getAllDescendantKBArticles(
			kbArticles, resourcePrimKey, status, orderByComparator);

		return Collections.unmodifiableList(kbArticles);
	}

	private String _getBody(
		String action,
		KBGroupServiceConfiguration kbGroupServiceConfiguration) {

		if (Objects.equals(action, Constants.ADD)) {
			return kbGroupServiceConfiguration.emailKBArticleAddedBody();
		}

		if (Objects.equals(action, Constants.EXPIRE)) {
			return kbGroupServiceConfiguration.emailKBArticleExpiredBody();
		}

		if (Objects.equals(action, _NOTIFICATION_ACTION_REVIEW)) {
			return kbGroupServiceConfiguration.emailKBArticleReviewBody();
		}

		return kbGroupServiceConfiguration.emailKBArticleUpdatedBody();
	}

	private Map<String, String> _getEmailKBArticleDiffs(KBArticle kbArticle) {
		Map<String, String> emailKBArticleDiffs = new HashMap<>();

		for (String param : new String[] {"content", "title"}) {
			String value = _beanProperties.getString(kbArticle, param);

			try {
				value = KBArticleDiffUtil.getKBArticleDiff(
					version -> getKBArticle(
						kbArticle.getResourcePrimKey(), version),
					kbArticle.getVersion() - 1, kbArticle.getVersion(), param,
					_diffHtml);
			}
			catch (Exception exception) {
				_log.error(exception);
			}

			emailKBArticleDiffs.put(param, value);
		}

		return emailKBArticleDiffs;
	}

	private long _getKBArticleCheckInterval() {
		return _kbServiceConfiguration.checkInterval();
	}

	private List<KBArticle> _getKBArticlesByCompanyIdAndDisplayDate(
		long companyId, Date displayDate) {

		return kbArticlePersistence.dslQuery(
			DSLQueryFactoryUtil.select(
				KBArticleTable.INSTANCE
			).from(
				KBArticleTable.INSTANCE
			).where(
				KBArticleTable.INSTANCE.companyId.eq(
					companyId
				).and(
					KBArticleTable.INSTANCE.displayDate.lte(displayDate)
				).and(
					KBArticleTable.INSTANCE.latest.eq(Boolean.TRUE)
				).and(
					KBArticleTable.INSTANCE.status.eq(
						WorkflowConstants.STATUS_SCHEDULED)
				).and(
					KBArticleTable.INSTANCE.status.neq(
						WorkflowConstants.STATUS_DRAFT)
				).and(
					KBArticleTable.INSTANCE.status.neq(
						WorkflowConstants.STATUS_PENDING)
				)
			).orderBy(
				KBArticleTable.INSTANCE.createDate.ascending()
			));
	}

	private List<KBArticle> _getKBArticlesByCompanyIdAndExpirationDate(
		long companyId, Date expirationDate) {

		return kbArticlePersistence.dslQuery(
			DSLQueryFactoryUtil.select(
				KBArticleTable.INSTANCE
			).from(
				KBArticleTable.INSTANCE
			).where(
				KBArticleTable.INSTANCE.companyId.eq(
					companyId
				).and(
					KBArticleTable.INSTANCE.expirationDate.lte(expirationDate)
				).and(
					KBArticleTable.INSTANCE.latest.eq(Boolean.TRUE)
				).and(
					KBArticleTable.INSTANCE.status.neq(
						WorkflowConstants.STATUS_DRAFT)
				).and(
					KBArticleTable.INSTANCE.status.neq(
						WorkflowConstants.STATUS_EXPIRED)
				).and(
					KBArticleTable.INSTANCE.status.neq(
						WorkflowConstants.STATUS_PENDING)
				).and(
					KBArticleTable.INSTANCE.status.neq(
						WorkflowConstants.STATUS_SCHEDULED)
				)
			));
	}

	private List<KBArticle> _getKBArticlesByCompanyIdAndReviewDate(
		long companyId, Date reviewDateGT, Date reviewDateLTE) {

		return kbArticlePersistence.dslQuery(
			DSLQueryFactoryUtil.select(
				KBArticleTable.INSTANCE
			).from(
				KBArticleTable.INSTANCE
			).where(
				KBArticleTable.INSTANCE.companyId.eq(
					companyId
				).and(
					KBArticleTable.INSTANCE.latest.eq(Boolean.TRUE)
				).and(
					KBArticleTable.INSTANCE.reviewDate.gt(reviewDateGT)
				).and(
					KBArticleTable.INSTANCE.reviewDate.lte(reviewDateLTE)
				)
			));
	}

	private String _getKBArticleURL(KBArticle kbArticle)
		throws PortalException {

		String controlPanelFullURL = _portal.getControlPanelFullURL(
			kbArticle.getGroupId(), KBPortletKeys.KNOWLEDGE_BASE_ADMIN, null);
		String namespace = _portal.getPortletNamespace(
			KBPortletKeys.KNOWLEDGE_BASE_ADMIN);

		String kbArticleURL = HttpComponentsUtil.addParameter(
			controlPanelFullURL, namespace + "mvcRenderCommandName",
			"/knowledge_base/view_kb_article");

		kbArticleURL = HttpComponentsUtil.addParameter(
			kbArticleURL, namespace + "redirect",
			HttpComponentsUtil.addParameter(
				controlPanelFullURL, namespace + "mvcRenderCommandName",
				"/knowledge_base/view"));
		kbArticleURL = HttpComponentsUtil.addParameter(
			kbArticleURL, namespace + "resourceClassNameId",
			kbArticle.getClassNameId());
		kbArticleURL = HttpComponentsUtil.addParameter(
			kbArticleURL, namespace + "resourcePrimKey",
			kbArticle.getResourcePrimKey());
		kbArticleURL = HttpComponentsUtil.addParameter(
			kbArticleURL, namespace + "selectedItemId",
			kbArticle.getResourcePrimKey());

		return kbArticleURL;
	}

	private KBGroupServiceConfiguration _getKBGroupServiceConfiguration(
			long groupId)
		throws ConfigurationException {

		return _configurationProvider.getConfiguration(
			KBGroupServiceConfiguration.class,
			new GroupServiceSettingsLocator(groupId, KBConstants.SERVICE_NAME));
	}

	private int _getNotificationType(String action) {
		if (Objects.equals(action, Constants.ADD)) {
			return UserNotificationDefinition.NOTIFICATION_TYPE_ADD_ENTRY;
		}

		if (Objects.equals(action, Constants.EXPIRE)) {
			return UserNotificationDefinition.NOTIFICATION_TYPE_EXPIRED_ENTRY;
		}

		if (Objects.equals(action, _NOTIFICATION_ACTION_REVIEW)) {
			return UserNotificationDefinition.NOTIFICATION_TYPE_REVIEW_ENTRY;
		}

		return UserNotificationDefinition.NOTIFICATION_TYPE_UPDATE_ENTRY;
	}

	private double _getPriority(long groupId, long parentResourcePrimKey)
		throws PortalException {

		KBGroupServiceConfiguration kbGroupServiceConfiguration =
			_getKBGroupServiceConfiguration(groupId);

		if (!kbGroupServiceConfiguration.articleIncrementPriorityEnabled()) {
			return KBArticleConstants.DEFAULT_VERSION;
		}

		List<KBArticle> kbArticles = getKBArticles(
			groupId, parentResourcePrimKey, WorkflowConstants.STATUS_ANY, 0, 1,
			KBArticlePriorityComparator.getInstance(false));

		if (kbArticles.isEmpty()) {
			return KBArticleConstants.DEFAULT_PRIORITY;
		}

		KBArticle kbArticle = kbArticles.get(0);

		return Math.floor(kbArticle.getPriority()) + 1;
	}

	private long _getRootResourcePrimKey(
			long resourcePrimKey, long parentResourceClassNameId,
			long parentResourcePrimKey)
		throws PortalException {

		if (parentResourcePrimKey ==
				KBFolderConstants.DEFAULT_PARENT_FOLDER_ID) {

			return resourcePrimKey;
		}

		long classNameId = _classNameLocalService.getClassNameId(
			KBArticleConstants.getClassName());

		if (parentResourceClassNameId == classNameId) {
			KBArticle kbArticle = getLatestKBArticle(
				parentResourcePrimKey, WorkflowConstants.STATUS_ANY);

			return kbArticle.getRootResourcePrimKey();
		}

		return resourcePrimKey;
	}

	private ServiceContext _getServiceContext(
			Company company, KBArticle kbArticle)
		throws PortalException {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setPlid(LayoutConstants.DEFAULT_PLID);
		serviceContext.setPortalURL(
			company.getPortalURL(kbArticle.getGroupId()));
		serviceContext.setPortletId(KBPortletKeys.KNOWLEDGE_BASE_ADMIN);
		serviceContext.setScopeGroupId(kbArticle.getGroupId());

		return serviceContext;
	}

	private String _getSubject(
		String action,
		KBGroupServiceConfiguration kbGroupServiceConfiguration) {

		if (Objects.equals(action, Constants.ADD)) {
			return kbGroupServiceConfiguration.emailKBArticleAddedSubject();
		}

		if (Objects.equals(action, Constants.EXPIRE)) {
			return kbGroupServiceConfiguration.emailKBArticleExpiredSubject();
		}

		if (Objects.equals(action, _NOTIFICATION_ACTION_REVIEW)) {
			return kbGroupServiceConfiguration.emailKBArticleReviewSubject();
		}

		return kbGroupServiceConfiguration.emailKBArticleUpdatedSubject();
	}

	private String _getUniqueUrlTitle(
			long groupId, long kbFolderId, long kbArticleId, String title)
		throws PortalException {

		String urlTitle = KnowledgeBaseUtil.getUrlTitle(kbArticleId, title);

		String uniqueUrlTitle = urlTitle;

		if (kbFolderId == KBFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			int kbArticlesCount = kbArticlePersistence.countByG_KBFI_UT_S(
				groupId, kbFolderId, uniqueUrlTitle, _STATUSES);

			for (int i = 1; kbArticlesCount > 0; i++) {
				uniqueUrlTitle = _getUniqueUrlTitle(urlTitle, i);

				kbArticlesCount = kbArticlePersistence.countByG_KBFI_UT_S(
					groupId, kbFolderId, uniqueUrlTitle, _STATUSES);
			}

			return uniqueUrlTitle;
		}

		KBFolder kbFolder = _kbFolderPersistence.findByPrimaryKey(kbFolderId);

		int kbArticlesCount = kbArticleFinder.countByUrlTitle(
			groupId, kbFolder.getUrlTitle(), uniqueUrlTitle, _STATUSES);

		for (int i = 1; kbArticlesCount > 0; i++) {
			uniqueUrlTitle = _getUniqueUrlTitle(urlTitle, i);

			kbArticlesCount = kbArticleFinder.countByUrlTitle(
				groupId, kbFolder.getUrlTitle(), uniqueUrlTitle, _STATUSES);
		}

		return uniqueUrlTitle;
	}

	private String _getUniqueUrlTitle(
			long groupId, long kbFolderId, long kbArticleId, String title,
			String urlTitle)
		throws PortalException {

		if (Validator.isNull(urlTitle)) {
			return _getUniqueUrlTitle(groupId, kbFolderId, kbArticleId, title);
		}

		return urlTitle.substring(1);
	}

	private String _getUniqueUrlTitle(String urlTitle, int suffix) {
		String uniqueUrlTitle = urlTitle + StringPool.DASH + suffix;

		return StringUtil.shorten(
			uniqueUrlTitle,
			ModelHintsUtil.getMaxLength(KBArticle.class.getName(), "urlTitle"),
			StringPool.DASH + suffix);
	}

	private void _indexKBArticle(KBArticle kbArticle) {
		TransactionCommitCallbackUtil.registerCallback(
			() -> {
				Indexer<KBArticle> indexer = _indexerRegistry.getIndexer(
					KBArticle.class);

				indexer.reindex(kbArticle);

				return null;
			});
	}

	private void _moveDependentKBArticleToTrash(
			KBArticle kbArticle, long trashEntryId)
		throws PortalException {

		if (kbArticle.isInTrash()) {
			throw new TrashEntryException();
		}

		// KB article

		long classPK = kbArticle.getClassPK();
		int status = kbArticle.getStatus();

		kbArticle.setStatus(WorkflowConstants.STATUS_IN_TRASH);

		kbArticle = kbArticlePersistence.update(kbArticle);

		// Trash

		if (status == WorkflowConstants.STATUS_PENDING) {
			status = WorkflowConstants.STATUS_DRAFT;
		}

		if (status != WorkflowConstants.STATUS_APPROVED) {
			_trashVersionLocalService.addTrashVersion(
				trashEntryId, KBArticle.class.getName(),
				kbArticle.getResourcePrimKey(), status, null);
		}

		// Asset

		_assetEntryLocalService.updateVisible(
			KBArticle.class.getName(), classPK, false);

		// Indexer

		Indexer<KBArticle> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			KBArticle.class);

		indexer.reindex(kbArticle);
	}

	private String _normalizeUrlTitle(String urlTitle) {
		if (Validator.isNull(urlTitle)) {
			return null;
		}

		if (StringUtil.startsWith(urlTitle, CharPool.SLASH)) {
			return urlTitle;
		}

		return StringPool.SLASH + urlTitle;
	}

	private void _notify(
			Set<String> receivers, long userId, KBArticle kbArticle,
			String action, ServiceContext serviceContext)
		throws PortalException {

		if (receivers.isEmpty()) {
			return;
		}

		KBGroupServiceConfiguration kbGroupServiceConfiguration =
			_getKBGroupServiceConfiguration(kbArticle.getGroupId());

		if (Objects.equals(action, Constants.ADD) &&
			!kbGroupServiceConfiguration.emailKBArticleAddedEnabled()) {

			return;
		}

		if (Objects.equals(action, Constants.EXPIRE) &&
			!kbGroupServiceConfiguration.emailKBArticleExpiredEnabled()) {

			return;
		}

		if (Objects.equals(action, Constants.UPDATE) &&
			!kbGroupServiceConfiguration.emailKBArticleUpdatedEnabled()) {

			return;
		}

		if (Objects.equals(action, _NOTIFICATION_ACTION_REVIEW) &&
			!kbGroupServiceConfiguration.emailKBArticleReviewEnabled()) {

			return;
		}

		String fromAddress = kbGroupServiceConfiguration.emailFromAddress();

		String kbArticleContent = StringUtil.replace(
			kbArticle.getContent(), new String[] {"href=\"/", "src=\"/"},
			new String[] {
				"href=\"" + serviceContext.getPortalURL() + "/",
				"src=\"" + serviceContext.getPortalURL() + "/"
			});

		Map<String, String> kbArticleDiffs = _getEmailKBArticleDiffs(kbArticle);

		for (Map.Entry<String, String> entry : kbArticleDiffs.entrySet()) {
			String value = StringUtil.replace(
				entry.getValue(), new String[] {"href=\"/", "src=\"/"},
				new String[] {
					"href=\"" + serviceContext.getPortalURL() + "/",
					"src=\"" + serviceContext.getPortalURL() + "/"
				});

			kbArticleDiffs.put(entry.getKey(), value);
		}

		SubscriptionSender subscriptionSender =
			AdminSubscriptionSenderFactory.createSubscriptionSender(
				kbArticle, serviceContext);

		subscriptionSender.setBody(
			_getBody(action, kbGroupServiceConfiguration));
		subscriptionSender.setClassName(kbArticle.getModelClassName());
		subscriptionSender.setClassPK(kbArticle.getClassPK());
		subscriptionSender.setContextAttribute(
			"[$ARTICLE_CONTENT$]", kbArticleContent, false);
		subscriptionSender.setContextAttribute(
			"[$ARTICLE_CONTENT_DIFF$]", kbArticleDiffs.get("content"), false);
		subscriptionSender.setContextAttribute(
			"[$ARTICLE_TITLE_DIFF$]", kbArticleDiffs.get("title"), false);
		subscriptionSender.setContextCreatorUserPrefix("ARTICLE");
		subscriptionSender.setCreatorUserId(kbArticle.getUserId());
		subscriptionSender.setCurrentUserId(userId);
		subscriptionSender.setEntryTitle(kbArticle.getTitle());
		subscriptionSender.setEntryURL(_getKBArticleURL(kbArticle));
		subscriptionSender.setFrom(
			fromAddress, kbGroupServiceConfiguration.emailFromName());
		subscriptionSender.setHtmlFormat(true);
		subscriptionSender.setMailId("kb_article", kbArticle.getKbArticleId());
		subscriptionSender.setNotificationType(_getNotificationType(action));
		subscriptionSender.setPortletId(serviceContext.getPortletId());
		subscriptionSender.setReplyToAddress(fromAddress);
		subscriptionSender.setScopeGroupId(kbArticle.getGroupId());
		subscriptionSender.setSubject(
			_getSubject(action, kbGroupServiceConfiguration));

		if (receivers.contains(_NOTIFICATION_RECEIVER_SUBSCRIBER)) {
			subscriptionSender.addAssetEntryPersistedSubscribers(
				KBArticle.class.getName(), kbArticle.getResourcePrimKey());
			subscriptionSender.addPersistedSubscribers(
				KBArticle.class.getName(), kbArticle.getGroupId());
			subscriptionSender.addPersistedSubscribers(
				KBArticle.class.getName(), kbArticle.getResourcePrimKey());

			KBArticle parentKBArticle = kbArticle.getParentKBArticle();

			while (parentKBArticle != null) {
				subscriptionSender.addPersistedSubscribers(
					KBArticle.class.getName(),
					parentKBArticle.getResourcePrimKey());

				parentKBArticle = parentKBArticle.getParentKBArticle();
			}
		}

		if (receivers.contains(_NOTIFICATION_RECEIVER_OWNER)) {
			User user = _userLocalService.fetchUser(kbArticle.getUserId());

			if ((user == null) || !user.isActive()) {
				user = _userLocalService.fetchUser(userId);
			}

			subscriptionSender.addRuntimeSubscribers(
				user.getEmailAddress(), user.getFullName());
		}

		subscriptionSender.flushNotificationsAsync();
	}

	private void _removeKBArticleAttachments(long[] removeFileEntryIds)
		throws PortalException {

		if (ArrayUtil.isEmpty(removeFileEntryIds)) {
			return;
		}

		for (long removeFileEntryId : removeFileEntryIds) {
			_portletFileRepository.deletePortletFileEntry(removeFileEntryId);
		}
	}

	private void _restoreDependentKBArticleFromTrash(KBArticle kbArticle)
		throws PortalException {

		if (!kbArticle.isInTrash()) {
			throw new TrashEntryException();
		}

		if (_trashHelper.isInTrashExplicitly(kbArticle)) {
			return;
		}

		TrashVersion trashVersion = _trashVersionLocalService.fetchVersion(
			KBArticle.class.getName(), kbArticle.getResourcePrimKey());

		int oldStatus = WorkflowConstants.STATUS_APPROVED;

		if (trashVersion != null) {
			oldStatus = trashVersion.getStatus();
		}

		kbArticle.setStatus(oldStatus);

		kbArticle = kbArticlePersistence.update(kbArticle);

		// Trash

		if (trashVersion != null) {
			_trashVersionLocalService.deleteTrashVersion(trashVersion);
		}

		// Asset

		if (oldStatus == WorkflowConstants.STATUS_APPROVED) {
			_assetEntryLocalService.updateVisible(
				KBArticle.class.getName(), kbArticle.getResourcePrimKey(),
				true);
		}

		// Indexer

		Indexer<KBArticle> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			KBArticle.class);

		indexer.reindex(kbArticle);
	}

	private void _startWorkflowInstance(
			long userId, KBArticle kbArticle, ServiceContext serviceContext)
		throws PortalException {

		WorkflowHandlerRegistryUtil.startWorkflowInstance(
			kbArticle.getCompanyId(), kbArticle.getGroupId(), userId,
			KBArticle.class.getName(), kbArticle.getResourcePrimKey(),
			kbArticle, serviceContext, Collections.emptyMap());
	}

	private void _updatePermissionFields(
			long resourcePrimKey, long parentResourceClassNameId,
			long parentResourcePrimKey)
		throws PortalException {

		KBArticle kbArticle = getLatestKBArticle(
			resourcePrimKey, WorkflowConstants.STATUS_ANY);

		if (kbArticle.getParentResourcePrimKey() == parentResourcePrimKey) {
			return;
		}

		long rootResourcePrimKey = _getRootResourcePrimKey(
			resourcePrimKey, parentResourceClassNameId, parentResourcePrimKey);

		if (kbArticle.getRootResourcePrimKey() == rootResourcePrimKey) {
			return;
		}

		// Sync database

		List<KBArticle> kbArticles1 = getKBArticleAndAllDescendantKBArticles(
			resourcePrimKey, WorkflowConstants.STATUS_ANY, null);

		for (KBArticle curKBArticle1 : kbArticles1) {
			List<KBArticle> kbArticles2 = getKBArticleVersions(
				curKBArticle1.getResourcePrimKey(),
				WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

			for (KBArticle curKBArticle2 : kbArticles2) {
				curKBArticle2.setRootResourcePrimKey(rootResourcePrimKey);

				kbArticlePersistence.update(curKBArticle2);
			}
		}

		// Sync indexed permission fields

		_indexWriterHelper.updatePermissionFields(
			KBArticle.class.getName(), String.valueOf(resourcePrimKey));
	}

	private KBArticle _updateStatus(
			long userId, KBArticle kbArticle, int status)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		kbArticle.setStatus(status);
		kbArticle.setStatusByUserId(user.getUserId());
		kbArticle.setStatusByUserName(user.getFullName());
		kbArticle.setStatusDate(new Date());

		kbArticle = kbArticlePersistence.update(kbArticle);

		Indexer<KBArticle> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			KBArticle.class);

		indexer.reindex(kbArticle);

		return kbArticle;
	}

	private void _validate(
			String title, String content, String sourceURL, Date displayDate,
			Date expirationDate, Date reviewDate)
		throws PortalException {

		if (Validator.isNull(title)) {
			throw new KBArticleTitleException("Title is null");
		}

		if (Validator.isNull(content)) {
			throw new KBArticleContentException("Content is null");
		}

		_validateSourceURL(sourceURL);

		if (displayDate == null) {
			throw new KBArticleDisplayDateException("Display date is null");
		}

		Date now = new Date();

		if (expirationDate != null) {
			if (expirationDate.before(now)) {
				throw new KBArticleExpirationDateException(
					"Expiration date " + expirationDate + " is in the past");
			}

			if (expirationDate.before(displayDate)) {
				throw new KBArticleExpirationDateException(
					StringBundler.concat(
						"Expiration date ", expirationDate,
						" is prior to display date ", displayDate));
			}
		}

		if ((reviewDate != null) && reviewDate.before(now)) {
			throw new KBArticleReviewDateException(
				"Review date is " + reviewDate + " in the past");
		}
	}

	private void _validateExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		if (Validator.isNull(externalReferenceCode)) {
			return;
		}

		KBArticle kbArticle = fetchLatestKBArticleByExternalReferenceCode(
			groupId, externalReferenceCode);

		if (kbArticle != null) {
			throw new DuplicateKBArticleExternalReferenceCodeException(
				StringBundler.concat(
					"Duplicate knowledge base article external reference code ",
					externalReferenceCode, " in group ", groupId));
		}
	}

	private void _validateParent(
			KBArticle kbArticle, long parentResourceClassNameId,
			long parentResourcePrimKey)
		throws PortalException {

		_validateParent(parentResourceClassNameId, parentResourcePrimKey);

		long kbArticleClassNameId = _classNameLocalService.getClassNameId(
			KBArticleConstants.getClassName());

		if (parentResourceClassNameId == kbArticleClassNameId) {
			KBArticle parentKBArticle = getLatestKBArticle(
				parentResourcePrimKey, WorkflowConstants.STATUS_ANY);

			List<Long> ancestorResourcePrimaryKeys =
				parentKBArticle.getAncestorResourcePrimaryKeys();

			if (ancestorResourcePrimaryKeys.contains(
					kbArticle.getResourcePrimKey())) {

				throw new KBArticleParentException(
					String.format(
						"Cannot move KBArticle %s inside its descendant " +
							"KBArticle %s",
						kbArticle.getResourcePrimKey(),
						parentKBArticle.getResourcePrimKey()));
			}
		}
	}

	private void _validateParent(long resourceClassNameId, long resourcePrimKey)
		throws PortalException {

		long kbArticleClassNameId = _classNameLocalService.getClassNameId(
			KBArticleConstants.getClassName());
		long kbFolderClassNameId = _classNameLocalService.getClassNameId(
			KBFolderConstants.getClassName());

		if ((resourceClassNameId != kbArticleClassNameId) &&
			(resourceClassNameId != kbFolderClassNameId)) {

			throw new KBArticleParentException(
				String.format(
					"Invalid parent with resource class name ID %s and " +
						"resource primary key %s",
					resourceClassNameId, resourcePrimKey));
		}
	}

	private void _validateParentStatus(
			long parentResourceClassNameId, long parentResourcePrimKey,
			int status)
		throws PortalException {

		long kbFolderClassNameId = _classNameLocalService.getClassNameId(
			KBFolder.class);

		if (parentResourceClassNameId == kbFolderClassNameId) {
			return;
		}

		KBArticle kbArticle = fetchLatestKBArticle(
			parentResourcePrimKey, WorkflowConstants.STATUS_APPROVED);

		if ((kbArticle == null) &&
			(status == WorkflowConstants.STATUS_APPROVED)) {

			throw new KBArticleStatusException();
		}
	}

	private void _validatePriority(double priority) throws PortalException {
		if (priority <= 0) {
			throw new KBArticlePriorityException(
				"Invalid priority " + priority);
		}
	}

	private void _validateSourceURL(String sourceURL) throws PortalException {
		if (Validator.isNull(sourceURL)) {
			return;
		}

		if (!Validator.isUrl(sourceURL)) {
			throw new KBArticleSourceURLException(sourceURL);
		}
	}

	private void _validateUrlTitle(
			long groupId, long kbFolderId, String urlTitle)
		throws PortalException {

		if (Validator.isNull(urlTitle)) {
			return;
		}

		if (!KnowledgeBaseUtil.isValidUrlTitle(urlTitle)) {
			throw new KBArticleUrlTitleException.
				MustNotContainInvalidCharacters(urlTitle);
		}

		int urlTitleMaxSize = ModelHintsUtil.getMaxLength(
			KBArticle.class.getName(), "urlTitle");

		if (urlTitle.length() > (urlTitleMaxSize + 1)) {
			throw new KBArticleUrlTitleException.MustNotExceedMaximumSize(
				urlTitle, urlTitleMaxSize);
		}

		Collection<KBArticle> kbArticles =
			kbArticlePersistence.findByG_KBFI_UT_NotS(
				groupId, kbFolderId, urlTitle.substring(1),
				WorkflowConstants.STATUS_IN_TRASH);

		if (!kbArticles.isEmpty()) {
			throw new KBArticleUrlTitleException.MustNotBeDuplicate(urlTitle);
		}
	}

	private static final String _NOTIFICATION_ACTION_REVIEW = "review";

	private static final String _NOTIFICATION_RECEIVER_OWNER = "owner";

	private static final String _NOTIFICATION_RECEIVER_SUBSCRIBER =
		"subscriber";

	private static final int[] _STATUSES = {
		WorkflowConstants.STATUS_APPROVED, WorkflowConstants.STATUS_PENDING
	};

	private static final Log _log = LogFactoryUtil.getLog(
		KBArticleLocalServiceImpl.class);

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private AssetLinkLocalService _assetLinkLocalService;

	@Reference
	private BeanProperties _beanProperties;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	private final Map<Long, Date> _dates = new ConcurrentHashMap<>();

	@Reference
	private DiffHtml _diffHtml;

	@Reference
	private DLURLHelper _dlURLHelper;

	@Reference
	private ExpandoRowLocalService _expandoRowLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private HtmlParser _htmlParser;

	@Reference
	private IndexerRegistry _indexerRegistry;

	@Reference
	private IndexWriterHelper _indexWriterHelper;

	@Reference
	private KBCommentPersistence _kbCommentPersistence;

	@Reference
	private KBFolderPersistence _kbFolderPersistence;

	private KBServiceConfiguration _kbServiceConfiguration;

	@Reference
	private LockManager _lockManager;

	@Reference
	private MarkdownConverterFactory _markdownConverterFactory;

	@Reference
	private Portal _portal;

	@Reference
	private PortletFileRepository _portletFileRepository;

	@Reference
	private RatingsStatsLocalService _ratingsStatsLocalService;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private SocialActivityLocalService _socialActivityLocalService;

	@Reference
	private SubscriptionLocalService _subscriptionLocalService;

	@Reference
	private TrashEntryLocalService _trashEntryLocalService;

	@Reference
	private TrashHelper _trashHelper;

	@Reference
	private TrashVersionLocalService _trashVersionLocalService;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private ViewCountManager _viewCountManager;

	@Reference
	private WorkflowInstanceLinkLocalService _workflowInstanceLinkLocalService;

	@Reference
	private ZipReaderFactory _zipReaderFactory;

}