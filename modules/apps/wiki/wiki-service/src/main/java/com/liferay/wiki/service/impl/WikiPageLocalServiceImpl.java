/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.service.impl;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.link.constants.AssetLinkConstants;
import com.liferay.asset.link.model.AssetLink;
import com.liferay.asset.link.service.AssetLinkLocalService;
import com.liferay.diff.DiffHtml;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.expando.kernel.util.ExpandoBridgeUtil;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.comment.CommentManager;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.increment.BufferedIncrement;
import com.liferay.portal.kernel.increment.DateOverrideIncrement;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.notifications.UserNotificationDefinition;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.SystemEventLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;
import com.liferay.portal.kernel.social.SocialActivityManagerUtil;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.systemevent.SystemEventHierarchyEntryThreadLocal;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.MathUtil;
import com.liferay.portal.kernel.util.MimeTypes;
import com.liferay.portal.kernel.util.NotificationThreadLocal;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.SubscriptionSender;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;
import com.liferay.portal.kernel.workflow.WorkflowThreadLocal;
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
import com.liferay.wiki.configuration.WikiFileUploadConfiguration;
import com.liferay.wiki.configuration.WikiGroupServiceOverriddenConfiguration;
import com.liferay.wiki.constants.WikiConstants;
import com.liferay.wiki.constants.WikiPageConstants;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.engine.WikiEngine;
import com.liferay.wiki.engine.WikiEngineRenderer;
import com.liferay.wiki.escape.WikiEscapeUtil;
import com.liferay.wiki.exception.DuplicatePageException;
import com.liferay.wiki.exception.DuplicateWikiPageExternalReferenceCodeException;
import com.liferay.wiki.exception.NoSuchPageException;
import com.liferay.wiki.exception.PageContentException;
import com.liferay.wiki.exception.PageTitleException;
import com.liferay.wiki.exception.PageVersionException;
import com.liferay.wiki.exception.WikiAttachmentMimeTypeException;
import com.liferay.wiki.internal.util.WikiCacheThreadLocal;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.model.WikiPageDisplay;
import com.liferay.wiki.model.WikiPageResource;
import com.liferay.wiki.model.WikiPageTable;
import com.liferay.wiki.model.impl.WikiPageDisplayImpl;
import com.liferay.wiki.model.impl.WikiPageImpl;
import com.liferay.wiki.processor.WikiPageRenameContentProcessor;
import com.liferay.wiki.service.WikiPageResourceLocalService;
import com.liferay.wiki.service.base.WikiPageLocalServiceBaseImpl;
import com.liferay.wiki.service.persistence.WikiNodePersistence;
import com.liferay.wiki.service.persistence.WikiPageResourcePersistence;
import com.liferay.wiki.social.WikiActivityKeys;
import com.liferay.wiki.util.comparator.PageCreateDateComparator;
import com.liferay.wiki.util.comparator.PageVersionComparator;
import com.liferay.wiki.validator.WikiPageTitleValidator;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.commons.lang.time.StopWatch;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * Provides the local service for accessing, adding, deleting, moving,
 * subscription handling of, trash handling of, updating, and validating wiki
 * pages.
 *
 * @author Brian Wing Shun Chan
 * @author Jorge Ferrer
 * @author Raymond Augé
 * @author Bruno Farache
 * @author Julio Camarero
 * @author Wesley Gong
 * @author Marcellus Tavares
 * @author Zsigmond Rab
 * @author Zsolt Berentey
 * @author Roberto Díaz
 */
@Component(
	configurationPid = "com.liferay.wiki.configuration.WikiFileUploadConfiguration",
	property = "model.class.name=com.liferay.wiki.model.WikiPage",
	service = AopService.class
)
public class WikiPageLocalServiceImpl extends WikiPageLocalServiceBaseImpl {

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link #addPage(String,
	 *             long, long, String, double, String, String, boolean, String,
	 *             boolean, String, String, ServiceContext)}
	 */
	@Deprecated
	@Override
	public WikiPage addPage(
			long userId, long nodeId, String title, double version,
			String content, String summary, boolean minorEdit, String format,
			boolean head, String parentTitle, String redirectTitle,
			ServiceContext serviceContext)
		throws PortalException {

		return addPage(
			null, userId, nodeId, title, version, content, summary, minorEdit,
			format, head, parentTitle, redirectTitle, serviceContext);
	}

	@Override
	public WikiPage addPage(
			long userId, long nodeId, String title, String content,
			String summary, boolean minorEdit, ServiceContext serviceContext)
		throws PortalException {

		double version = WikiPageConstants.VERSION_DEFAULT;

		WikiNode node = _wikiNodePersistence.findByPrimaryKey(nodeId);

		WikiGroupServiceOverriddenConfiguration
			wikiGroupServiceOverriddenConfiguration =
				_configurationProvider.getConfiguration(
					WikiGroupServiceOverriddenConfiguration.class,
					new GroupServiceSettingsLocator(
						node.getGroupId(), WikiConstants.SERVICE_NAME));

		String format = wikiGroupServiceOverriddenConfiguration.defaultFormat();

		boolean head = false;
		String parentTitle = null;
		String redirectTitle = null;

		return addPage(
			userId, nodeId, title, version, content, summary, minorEdit, format,
			head, parentTitle, redirectTitle, serviceContext);
	}

	@Override
	public WikiPage addPage(
			String externalReferenceCode, long userId, long nodeId,
			String title, double version, String content, String summary,
			boolean minorEdit, String format, boolean head, String parentTitle,
			String redirectTitle, ServiceContext serviceContext)
		throws PortalException {

		// Page

		User user = _userLocalService.getUser(userId);
		WikiNode node = _wikiNodePersistence.findByPrimaryKey(nodeId);
		Date date = new Date();

		long pageId = counterLocalService.increment();

		_validateExternalReferenceCode(
			externalReferenceCode, node.getGroupId());

		content = SanitizerUtil.sanitize(
			user.getCompanyId(), node.getGroupId(), userId,
			WikiPage.class.getName(), pageId, "text/" + format, content);

		title = _normalizeSpace(title);

		_validate(title, nodeId, content, format);

		long resourcePrimKey =
			_wikiPageResourceLocalService.getPageResourcePrimKey(
				node.getGroupId(), nodeId, title);

		WikiPage page = wikiPagePersistence.create(pageId);

		page.setUuid(serviceContext.getUuid());
		page.setResourcePrimKey(resourcePrimKey);
		page.setGroupId(node.getGroupId());
		page.setCompanyId(user.getCompanyId());
		page.setUserId(user.getUserId());
		page.setUserName(user.getFullName());
		page.setExternalReferenceCode(externalReferenceCode);
		page.setNodeId(nodeId);
		page.setTitle(title);
		page.setVersion(version);
		page.setMinorEdit(minorEdit);
		page.setContent(content);
		page.setSummary(summary);
		page.setFormat(format);
		page.setHead(head);
		page.setParentTitle(parentTitle);
		page.setRedirectTitle(redirectTitle);
		page.setStatus(WorkflowConstants.STATUS_DRAFT);
		page.setStatusByUserId(userId);
		page.setStatusDate(serviceContext.getModifiedDate(date));
		page.setExpandoBridgeAttributes(serviceContext);

		page = wikiPagePersistence.update(page);

		// Resources

		if (serviceContext.isAddGroupPermissions() ||
			serviceContext.isAddGuestPermissions()) {

			addPageResources(
				page, serviceContext.isAddGroupPermissions(),
				serviceContext.isAddGuestPermissions());
		}
		else {
			addPageResources(page, serviceContext.getModelPermissions());
		}

		// Node

		_updateLastPostDate(serviceContext.getModifiedDate(date), nodeId);

		// Asset

		updateAsset(
			userId, page, serviceContext.getAssetCategoryIds(),
			serviceContext.getAssetTagNames(),
			serviceContext.getAssetLinkEntryIds(),
			serviceContext.getAssetPriority());

		// Workflow

		return _startWorkflowInstance(userId, page, serviceContext);
	}

	@Override
	public FileEntry addPageAttachment(
			long userId, long nodeId, String title, String fileName, File file,
			String mimeType)
		throws PortalException {

		List<String> wikiAttachmentMimeTypes = ListUtil.fromArray(
			_wikiFileUploadConfiguration.attachmentMimeTypes());

		if (ListUtil.isNull(wikiAttachmentMimeTypes) ||
			(!wikiAttachmentMimeTypes.contains(StringPool.STAR) &&
			 !wikiAttachmentMimeTypes.contains(mimeType))) {

			throw new WikiAttachmentMimeTypeException();
		}

		WikiPage page = getPage(nodeId, title);

		Folder folder = page.addAttachmentsFolder();

		fileName = _portletFileRepository.getUniqueFileName(
			page.getGroupId(), folder.getFolderId(), fileName);

		FileEntry fileEntry = _portletFileRepository.addPortletFileEntry(
			null, page.getGroupId(), userId, WikiPage.class.getName(),
			page.getResourcePrimKey(), WikiConstants.SERVICE_NAME,
			folder.getFolderId(), file, fileName, mimeType, true);

		if (userId == 0) {
			userId = page.getUserId();
		}

		JSONObject extraDataJSONObject = JSONUtil.put(
			"fileEntryId", fileEntry.getFileEntryId()
		).put(
			"fileEntryTitle", fileEntry.getTitle()
		).put(
			"title", page.getTitle()
		).put(
			"version", page.getVersion()
		);

		SocialActivityManagerUtil.addActivity(
			userId, page, SocialActivityConstants.TYPE_ADD_ATTACHMENT,
			extraDataJSONObject.toString(), 0);

		return fileEntry;
	}

	@Override
	public FileEntry addPageAttachment(
			long userId, long nodeId, String title, String fileName,
			InputStream inputStream, String mimeType)
		throws PortalException {

		WikiPage page = getPage(nodeId, title);

		Folder folder = page.addAttachmentsFolder();

		fileName = _portletFileRepository.getUniqueFileName(
			page.getGroupId(), folder.getFolderId(), fileName);

		FileEntry fileEntry = _portletFileRepository.addPortletFileEntry(
			null, page.getGroupId(), userId, WikiPage.class.getName(),
			page.getResourcePrimKey(), WikiConstants.SERVICE_NAME,
			folder.getFolderId(), inputStream, fileName, mimeType, true);

		if (userId == 0) {
			userId = page.getUserId();
		}

		JSONObject extraDataJSONObject = JSONUtil.put(
			"fileEntryId", fileEntry.getFileEntryId()
		).put(
			"fileEntryTitle", fileEntry.getTitle()
		).put(
			"title", page.getTitle()
		).put(
			"version", page.getVersion()
		);

		SocialActivityManagerUtil.addActivity(
			userId, page, SocialActivityConstants.TYPE_ADD_ATTACHMENT,
			extraDataJSONObject.toString(), 0);

		return fileEntry;
	}

	@Override
	public List<FileEntry> addPageAttachments(
			long userId, long nodeId, String title,
			List<ObjectValuePair<String, InputStream>> inputStreamOVPs)
		throws PortalException {

		if (inputStreamOVPs.isEmpty()) {
			return Collections.emptyList();
		}

		List<FileEntry> fileEntries = new ArrayList<>();

		for (ObjectValuePair<String, InputStream> inputStreamOVP :
				inputStreamOVPs) {

			String fileName = inputStreamOVP.getKey();
			InputStream inputStream = inputStreamOVP.getValue();

			File file = null;

			try {
				file = FileUtil.createTempFile(inputStream);

				String mimeType = _mimeTypes.getContentType(file, fileName);

				FileEntry fileEntry = addPageAttachment(
					userId, nodeId, title, fileName, file, mimeType);

				fileEntries.add(fileEntry);
			}
			catch (IOException ioException) {
				throw new SystemException(
					"Unable to write temporary file", ioException);
			}
			finally {
				FileUtil.delete(file);
			}
		}

		return fileEntries;
	}

	@Override
	public void addPageResources(
			long nodeId, String title, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		addPageResources(
			getPage(nodeId, title), addGroupPermissions, addGuestPermissions);
	}

	@Override
	public void addPageResources(
			WikiPage page, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		_resourceLocalService.addResources(
			page.getCompanyId(), page.getGroupId(), page.getUserId(),
			WikiPage.class.getName(), page.getResourcePrimKey(), false,
			addGroupPermissions, addGuestPermissions);
	}

	@Override
	public void addPageResources(
			WikiPage page, ModelPermissions modelPermissions)
		throws PortalException {

		_resourceLocalService.addModelResources(
			page.getCompanyId(), page.getGroupId(), page.getUserId(),
			WikiPage.class.getName(), page.getResourcePrimKey(),
			modelPermissions);
	}

	@Override
	public FileEntry addTempFileEntry(
			long groupId, long userId, String folderName, String fileName,
			InputStream inputStream, String mimeType)
		throws PortalException {

		return TempFileEntryUtil.addTempFileEntry(
			groupId, userId, folderName, fileName, inputStream, mimeType);
	}

	@Override
	public WikiPage changeParent(
			long userId, long nodeId, String title, String newParentTitle,
			ServiceContext serviceContext)
		throws PortalException {

		if (Validator.isNotNull(newParentTitle)) {
			WikiPage parentPage = getPage(nodeId, newParentTitle);

			if (Validator.isNotNull(parentPage.getRedirectTitle())) {
				newParentTitle = parentPage.getRedirectTitle();
			}
		}

		WikiPage page = getPage(nodeId, title);

		String originalParentTitle = page.getParentTitle();

		double version = page.getVersion();
		String content = page.getContent();

		String summary = serviceContext.translate(
			"changed-parent-from-x", originalParentTitle);
		boolean minorEdit = false;
		String format = page.getFormat();
		String redirectTitle = page.getRedirectTitle();

		_populateServiceContext(serviceContext, page);

		serviceContext.setCommand(Constants.CHANGE_PARENT);

		return updatePage(
			userId, nodeId, title, version, content, summary, minorEdit, format,
			newParentTitle, redirectTitle, serviceContext);
	}

	@Override
	public void copyPageAttachments(
			long userId, long templateNodeId, String templateTitle, long nodeId,
			String title)
		throws PortalException {

		WikiPage templatePage = getPage(templateNodeId, templateTitle);

		List<FileEntry> templateFileEntries =
			templatePage.getAttachmentsFileEntries();

		for (FileEntry templateFileEntry : templateFileEntries) {
			addPageAttachment(
				userId, nodeId, title, templateFileEntry.getTitle(),
				templateFileEntry.getContentStream(),
				templateFileEntry.getMimeType());
		}
	}

	@Override
	public void deletePage(long nodeId, String title) throws PortalException {
		List<WikiPage> pages = wikiPagePersistence.findByN_T_H(
			nodeId, title, true, 0, 1);

		if (!pages.isEmpty()) {
			wikiPageLocalService.deletePage(pages.get(0));
		}
	}

	@Override
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP, send = false,
		type = SystemEventConstants.TYPE_DELETE
	)
	public void deletePage(WikiPage page) throws PortalException {

		// Child pages

		List<WikiPage> childPages = wikiPagePersistence.findByN_P(
			page.getNodeId(), page.getTitle());

		for (WikiPage childPage : childPages) {
			if (childPage.isApproved() ||
				_trashHelper.isInTrashImplicitly(childPage)) {

				wikiPageLocalService.deletePage(childPage);
			}
			else {
				childPage.setParentTitle(StringPool.BLANK);

				wikiPagePersistence.update(childPage);
			}
		}

		List<WikiPage> redirectorPages = getRedirectorPages(
			page.getNodeId(), page.getTitle());

		for (WikiPage redirectorPage : redirectorPages) {
			if (redirectorPage.isApproved() ||
				_trashHelper.isInTrashImplicitly(redirectorPage)) {

				wikiPageLocalService.deletePage(redirectorPage);
			}
			else {
				redirectorPage.setRedirectTitle(StringPool.BLANK);

				wikiPagePersistence.update(redirectorPage);
			}
		}

		List<WikiPage> versionPages = wikiPagePersistence.findByN_T(
			page.getNodeId(), page.getTitle());

		wikiPagePersistence.removeByN_T(page.getNodeId(), page.getTitle());

		// References

		wikiPagePersistence.removeByN_R(page.getNodeId(), page.getTitle());

		// Resources

		_resourceLocalService.deleteResource(
			page.getCompanyId(), WikiPage.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL, page.getResourcePrimKey());

		// Resource

		WikiPageResource pageResource = _wikiPageResourcePersistence.fetchByN_T(
			page.getNodeId(), page.getTitle());

		if (pageResource != null) {
			_wikiPageResourcePersistence.remove(pageResource);
		}

		// Attachments

		long folderId = page.getAttachmentsFolderId();

		if (folderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			_portletFileRepository.deletePortletFolder(folderId);
		}

		// Subscriptions

		_subscriptionLocalService.deleteSubscriptions(
			page.getCompanyId(), WikiPage.class.getName(),
			page.getResourcePrimKey());

		// Asset

		SystemEventHierarchyEntryThreadLocal.pop(
			page.getModelClass(), page.getPageId());

		try {
			for (WikiPage versionPage : versionPages) {
				_assetEntryLocalService.deleteEntry(
					WikiPage.class.getName(), versionPage.getPrimaryKey());
			}
		}
		finally {
			SystemEventHierarchyEntryThreadLocal.push(
				page.getModelClass(), page.getPageId());
		}

		_assetEntryLocalService.deleteEntry(
			WikiPage.class.getName(), page.getResourcePrimKey());

		// Comment

		_commentManager.deleteDiscussion(
			WikiPage.class.getName(), page.getResourcePrimKey());

		// Expando

		_expandoRowLocalService.deleteRows(page.getPrimaryKey());

		// Ratings

		_ratingsStatsLocalService.deleteStats(
			WikiPage.class.getName(), page.getResourcePrimKey());

		// Trash

		if (page.isInTrash()) {
			if (_trashHelper.isInTrashExplicitly(page)) {
				page.setTitle(_trashHelper.getOriginalTitle(page.getTitle()));

				_trashEntryLocalService.deleteEntry(
					WikiPage.class.getName(), page.getResourcePrimKey());
			}
			else {
				for (WikiPage versionPage : versionPages) {
					_trashVersionLocalService.deleteTrashVersion(
						WikiPage.class.getName(), versionPage.getPageId());
				}
			}
		}

		// Indexer

		Indexer<WikiPage> indexer = _indexerRegistry.nullSafeGetIndexer(
			WikiPage.class);

		indexer.delete(page);

		// Cache

		_clearPageCache();

		// Version pages

		for (WikiPage versionPage : versionPages) {

			// Workflow

			_workflowInstanceLinkLocalService.deleteWorkflowInstanceLinks(
				versionPage.getCompanyId(), versionPage.getGroupId(),
				WikiPage.class.getName(), versionPage.getPageId());
		}

		if (pageResource != null) {
			_systemEventLocalService.addSystemEvent(
				0, page.getGroupId(), StringPool.BLANK,
				page.getModelClassName(), page.getPrimaryKey(),
				pageResource.getUuid(), null, SystemEventConstants.TYPE_DELETE,
				StringPool.BLANK);
		}
	}

	@Override
	public void deletePageAttachment(long nodeId, String title, String fileName)
		throws PortalException {

		WikiPage page = getPage(nodeId, title);

		long folderId = page.getAttachmentsFolderId();

		if (folderId == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return;
		}

		FileEntry fileEntry = _portletFileRepository.getPortletFileEntry(
			page.getGroupId(), folderId, fileName);

		_deletePageAttachment(fileEntry.getFileEntryId());
	}

	@Override
	public void deletePageAttachments(long nodeId, String title)
		throws PortalException {

		WikiPage page = getPage(nodeId, title);

		long folderId = page.getAttachmentsFolderId();

		if (folderId == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return;
		}

		_portletFileRepository.deletePortletFileEntries(
			page.getGroupId(), folderId);
	}

	@Override
	public void deletePages(long nodeId) throws PortalException {
		List<WikiPage> pages = wikiPagePersistence.findByN_H_P(
			nodeId, true, StringPool.BLANK);

		for (WikiPage page : pages) {
			wikiPageLocalService.deletePage(page);
		}

		pages = wikiPagePersistence.findByN_H_P(
			nodeId, false, StringPool.BLANK);

		for (WikiPage page : pages) {
			wikiPageLocalService.deletePage(page);
		}
	}

	@Override
	public void deleteTempFileEntry(
			long groupId, long userId, String folderName, String fileName)
		throws PortalException {

		TempFileEntryUtil.deleteTempFileEntry(
			groupId, userId, folderName, fileName);
	}

	@Override
	public void deleteTrashPageAttachments(long nodeId, String title)
		throws PortalException {

		WikiPage page = getPage(nodeId, title);

		long folderId = page.getAttachmentsFolderId();

		if (folderId == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return;
		}

		_portletFileRepository.deletePortletFileEntries(
			page.getGroupId(), folderId, WorkflowConstants.STATUS_IN_TRASH);
	}

	@Override
	public void discardDraft(long nodeId, String title, double version)
		throws PortalException {

		wikiPagePersistence.removeByN_T_V(nodeId, title, version);
	}

	@Override
	public WikiPage fetchLatestPage(
		long resourcePrimKey, int status, boolean preferApproved) {

		OrderByComparator<WikiPage> orderByComparator =
			PageVersionComparator.getInstance(false);

		if (status != WorkflowConstants.STATUS_ANY) {
			return wikiPagePersistence.fetchByR_S_First(
				resourcePrimKey, status, orderByComparator);
		}

		WikiPage page = null;

		if (preferApproved) {
			page = wikiPagePersistence.fetchByR_S_First(
				resourcePrimKey, WorkflowConstants.STATUS_APPROVED,
				orderByComparator);
		}

		if (page != null) {
			return page;
		}

		return wikiPagePersistence.fetchByResourcePrimKey_First(
			resourcePrimKey, orderByComparator);
	}

	@Override
	public WikiPage fetchLatestPage(
		long resourcePrimKey, long nodeId, int status, boolean preferApproved) {

		OrderByComparator<WikiPage> orderByComparator =
			PageVersionComparator.getInstance(false);

		if (status != WorkflowConstants.STATUS_ANY) {
			return wikiPagePersistence.fetchByR_N_S_First(
				resourcePrimKey, nodeId, status, orderByComparator);
		}

		WikiPage page = null;

		if (preferApproved) {
			page = wikiPagePersistence.fetchByR_N_S_First(
				resourcePrimKey, nodeId, WorkflowConstants.STATUS_APPROVED,
				orderByComparator);
		}

		if (page != null) {
			return page;
		}

		return wikiPagePersistence.fetchByR_N_First(
			resourcePrimKey, nodeId, orderByComparator);
	}

	@Override
	public WikiPage fetchLatestPage(
		long nodeId, String title, int status, boolean preferApproved) {

		OrderByComparator<WikiPage> orderByComparator =
			PageVersionComparator.getInstance(false);

		if (status != WorkflowConstants.STATUS_ANY) {
			return wikiPagePersistence.fetchByN_T_S_First(
				nodeId, title, status, orderByComparator);
		}

		WikiPage page = null;

		if (preferApproved) {
			page = wikiPagePersistence.fetchByN_T_S_First(
				nodeId, title, WorkflowConstants.STATUS_APPROVED,
				orderByComparator);
		}

		if (page != null) {
			return page;
		}

		return wikiPagePersistence.fetchByN_T_First(
			nodeId, title, orderByComparator);
	}

	/**
	 * Returns the latest wiki page matching the group and the external
	 * reference code
	 *
	 * @param  groupId the primary key of the group
	 * @param  externalReferenceCode the wiki page's external reference code
	 * @return the latest matching wiki page, or <code>null</code> if no
	 *         matching wiki page could be found
	 */
	@Override
	public WikiPage fetchLatestPageByExternalReferenceCode(
		long groupId, String externalReferenceCode) {

		return wikiPagePersistence.fetchByG_ERC_First(
			groupId, externalReferenceCode,
			PageVersionComparator.getInstance(false));
	}

	@Override
	public WikiPage fetchPage(long resourcePrimKey) {
		WikiPageResource pageResource =
			_wikiPageResourcePersistence.fetchByPrimaryKey(resourcePrimKey);

		if (pageResource == null) {
			return null;
		}

		return fetchPage(pageResource.getNodeId(), pageResource.getTitle());
	}

	@Override
	public WikiPage fetchPage(long resourcePrimKey, Boolean head) {
		WikiPageResource pageResource =
			_wikiPageResourcePersistence.fetchByPrimaryKey(resourcePrimKey);

		if (pageResource == null) {
			return null;
		}

		if (head == null) {
			return wikiPagePersistence.fetchByN_T_First(
				pageResource.getNodeId(), pageResource.getTitle(), null);
		}

		return wikiPagePersistence.fetchByN_T_H_First(
			pageResource.getNodeId(), pageResource.getTitle(), head, null);
	}

	@Override
	public WikiPage fetchPage(long nodeId, String title) {
		return wikiPagePersistence.fetchByN_T_H_First(
			nodeId, title, true, null);
	}

	@Override
	public WikiPage fetchPage(long nodeId, String title, double version) {
		if (version == 0) {
			return fetchPage(nodeId, title);
		}

		return wikiPagePersistence.fetchByN_T_V(nodeId, title, version);
	}

	@Override
	public PersistedModel fetchPersistedModel(Serializable primaryKeyObj) {
		PersistedModel persistedModel = wikiPagePersistence.fetchByPrimaryKey(
			primaryKeyObj);

		if (persistedModel == null) {
			persistedModel = fetchLatestPage(
				GetterUtil.getLong(primaryKeyObj),
				WorkflowConstants.STATUS_APPROVED, false);
		}

		return persistedModel;
	}

	@Override
	public List<WikiPage> getChildren(
		long nodeId, boolean head, String parentTitle) {

		return getChildren(
			nodeId, head, parentTitle, WorkflowConstants.STATUS_APPROVED);
	}

	@Override
	public List<WikiPage> getChildren(
		long nodeId, boolean head, String parentTitle, int status) {

		return wikiPagePersistence.findByN_H_P_S(
			nodeId, head, parentTitle, status);
	}

	@Override
	public List<WikiPage> getChildren(
		long nodeId, boolean head, String parentTitle, int start, int end) {

		return wikiPagePersistence.findByN_H_P_S(
			nodeId, head, parentTitle, WorkflowConstants.STATUS_APPROVED, start,
			end);
	}

	@Override
	public List<WikiPage> getChildren(
		long nodeId, boolean head, String parentTitle, int status, int start,
		int end, OrderByComparator<WikiPage> orderByComparator) {

		return wikiPagePersistence.findByN_H_P_S(
			nodeId, head, parentTitle, status, start, end, orderByComparator);
	}

	@Override
	public int getChildrenCount(long nodeId, boolean head, String parentTitle) {
		return getChildrenCount(
			nodeId, head, parentTitle, WorkflowConstants.STATUS_APPROVED);
	}

	@Override
	public int getChildrenCount(
		long nodeId, boolean head, String parentTitle, int status) {

		return wikiPagePersistence.countByN_H_P_S(
			nodeId, head, parentTitle, status);
	}

	@Override
	public List<WikiPage> getDependentPages(
		long nodeId, boolean head, String title, int status) {

		List<WikiPage> dependentPages = new ArrayList<>(
			getChildren(nodeId, head, title, status));

		dependentPages.addAll(getRedirectorPages(nodeId, head, title, status));

		return dependentPages;
	}

	@Override
	public WikiPageDisplay getDisplay(
			long nodeId, String title, PortletURL viewPageURL,
			Supplier<PortletURL> editPageURLSupplier,
			String attachmentURLPrefix)
		throws PortalException {

		StopWatch stopWatch = new StopWatch();

		stopWatch.start();

		WikiPageDisplay pageDisplay = null;

		if (CTCollectionThreadLocal.isProductionMode()) {
			String key = _encodeKey(nodeId, title, viewPageURL.toString());

			pageDisplay = (WikiPageDisplay)_portalCache.get(key);

			if (pageDisplay == null) {
				pageDisplay = getPageDisplay(
					nodeId, title, viewPageURL, editPageURLSupplier.get(),
					attachmentURLPrefix);

				_portalCache.put(key, pageDisplay);
			}
		}
		else {
			pageDisplay = getPageDisplay(
				nodeId, title, viewPageURL, editPageURLSupplier.get(),
				attachmentURLPrefix);
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"getDisplay for {", nodeId, ", ", title, ", ", viewPageURL,
					", ", editPageURLSupplier.get(), "} takes ",
					stopWatch.getTime(), " ms"));
		}

		return pageDisplay;
	}

	@Override
	public WikiPage getDraftPage(long nodeId, String title)
		throws PortalException {

		WikiPage page = wikiPagePersistence.fetchByN_T_S_First(
			nodeId, title, WorkflowConstants.STATUS_DRAFT, null);

		if (page != null) {
			return page;
		}

		return wikiPagePersistence.findByN_T_S_First(
			nodeId, title, WorkflowConstants.STATUS_PENDING, null);
	}

	@Override
	public List<WikiPage> getIncomingLinks(long nodeId, String title)
		throws PortalException {

		Set<WikiPage> links = new HashSet<>();

		List<WikiPage> pages = wikiPagePersistence.findByN_H(nodeId, true);

		for (WikiPage page : pages) {
			if (_isLinkedTo(page, title)) {
				links.add(page);
			}
		}

		List<WikiPage> redirectorPages = getRedirectorPages(nodeId, title);

		for (WikiPage redirectorPage : redirectorPages) {
			for (WikiPage page : pages) {
				if (_isLinkedTo(page, redirectorPage.getTitle())) {
					links.add(page);
				}
			}
		}

		return ListUtil.sort(new ArrayList<>(links));
	}

	@Override
	public WikiPage getLatestPage(
			long resourcePrimKey, int status, boolean preferApproved)
		throws PortalException {

		WikiPage page = fetchLatestPage(
			resourcePrimKey, status, preferApproved);

		if (page != null) {
			return page;
		}

		throw new NoSuchPageException(
			StringBundler.concat(
				"{resourcePrimKey=", resourcePrimKey, ", status=", status,
				"}"));
	}

	@Override
	public WikiPage getLatestPage(
			long resourcePrimKey, long nodeId, int status,
			boolean preferApproved)
		throws PortalException {

		WikiPage page = fetchLatestPage(
			resourcePrimKey, nodeId, status, preferApproved);

		if (page != null) {
			return page;
		}

		throw new NoSuchPageException(
			StringBundler.concat(
				"{resourcePrimKey=", resourcePrimKey, ", nodeId=", nodeId,
				", status=", status, "}"));
	}

	@Override
	public WikiPage getLatestPage(
			long nodeId, String title, int status, boolean preferApproved)
		throws PortalException {

		WikiPage page = fetchLatestPage(nodeId, title, status, preferApproved);

		if (page != null) {
			return page;
		}

		throw new NoSuchPageException(
			StringBundler.concat(
				"{nodeId=", nodeId, ", title=", title, ", status=", status,
				"}"));
	}

	/**
	 * Returns the latest wiki page matching the group and the external
	 * reference code
	 *
	 * @param  groupId the primary key of the group
	 * @param  externalReferenceCode the wiki page's external reference code
	 * @return the latest matching wiki page
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public WikiPage getLatestPageByExternalReferenceCode(
			long groupId, String externalReferenceCode)
		throws PortalException {

		return wikiPagePersistence.findByG_ERC_First(
			groupId, externalReferenceCode,
			PageVersionComparator.getInstance(false));
	}

	@Override
	public List<WikiPage> getOrphans(List<WikiPage> pages)
		throws PortalException {

		List<Map<String, Boolean>> pageTitles = new ArrayList<>();

		for (WikiPage page : pages) {
			pageTitles.add(_getOutgoingLinks(page));
		}

		Set<WikiPage> notOrphans = new HashSet<>();

		for (WikiPage page : pages) {
			for (Map<String, Boolean> pageTitle : pageTitles) {
				String pageTitleLowerCase = page.getTitle();

				pageTitleLowerCase = StringUtil.toLowerCase(pageTitleLowerCase);

				if (pageTitle.get(pageTitleLowerCase) != null) {
					notOrphans.add(page);

					break;
				}
			}
		}

		List<WikiPage> orphans = new ArrayList<>();

		for (WikiPage page : pages) {
			if (!notOrphans.contains(page)) {
				orphans.add(page);
			}
		}

		return ListUtil.sort(orphans);
	}

	@Override
	public List<WikiPage> getOrphans(long nodeId) throws PortalException {
		List<WikiPage> pages = wikiPagePersistence.findByN_H_S(
			nodeId, true, WorkflowConstants.STATUS_APPROVED);

		return getOrphans(pages);
	}

	@Override
	public List<WikiPage> getOutgoingLinks(long nodeId, String title)
		throws PortalException {

		WikiPage page = getPage(nodeId, title);

		Map<String, WikiPage> pages = new LinkedHashMap<>();

		Map<String, Boolean> links = _getOutgoingLinks(page);

		for (Map.Entry<String, Boolean> entry : links.entrySet()) {
			String curTitle = entry.getKey();
			Boolean exists = entry.getValue();

			if (exists) {
				WikiPage curPage = getPage(nodeId, curTitle);

				if (!pages.containsKey(curPage.getTitle())) {
					pages.put(curPage.getTitle(), curPage);
				}
			}
			else {
				WikiPageImpl wikiPageImpl = new WikiPageImpl();

				wikiPageImpl.setNew(true);
				wikiPageImpl.setNodeId(nodeId);
				wikiPageImpl.setTitle(curTitle);

				if (!pages.containsKey(curTitle)) {
					pages.put(curTitle, wikiPageImpl);
				}
			}
		}

		return ListUtil.fromMapValues(pages);
	}

	@Override
	public WikiPage getPage(long resourcePrimKey) throws PortalException {
		return getPage(resourcePrimKey, Boolean.TRUE);
	}

	@Override
	public WikiPage getPage(long resourcePrimKey, Boolean head)
		throws PortalException {

		WikiPageResource pageResource =
			_wikiPageResourcePersistence.findByPrimaryKey(resourcePrimKey);

		return getPage(pageResource.getNodeId(), pageResource.getTitle(), head);
	}

	@Override
	public WikiPage getPage(long nodeId, String title) throws PortalException {
		return wikiPagePersistence.findByN_T_H_First(nodeId, title, true, null);
	}

	@Override
	public WikiPage getPage(long nodeId, String title, Boolean head)
		throws PortalException {

		if (head == null) {
			return wikiPagePersistence.findByN_T_First(nodeId, title, null);
		}

		return wikiPagePersistence.findByN_T_H_First(nodeId, title, head, null);
	}

	@Override
	public WikiPage getPage(long nodeId, String title, double version)
		throws PortalException {

		if (version == 0) {
			return getPage(nodeId, title);
		}

		return wikiPagePersistence.findByN_T_V(nodeId, title, version);
	}

	@Override
	public WikiPage getPageByPageId(long pageId) throws PortalException {
		return wikiPagePersistence.findByPrimaryKey(pageId);
	}

	@Override
	public WikiPageDisplay getPageDisplay(
			long nodeId, String title, PortletURL viewPageURL,
			PortletURL editPageURL, String attachmentURLPrefix)
		throws PortalException {

		return getPageDisplay(
			getPage(nodeId, title), viewPageURL, editPageURL,
			attachmentURLPrefix);
	}

	@Override
	public WikiPageDisplay getPageDisplay(
			WikiPage page, PortletURL viewPageURL, PortletURL editPageURL,
			String attachmentURLPrefix)
		throws PortalException {

		String formattedContent = _wikiEngineRenderer.convert(
			page, viewPageURL, editPageURL, attachmentURLPrefix);

		return new WikiPageDisplayImpl(
			page.getUserId(), page.getNodeId(), page.getTitle(),
			page.getVersion(), page.getContent(), formattedContent,
			page.getFormat(), page.isHead(), page.getAttachmentsFileEntries());
	}

	@Override
	public WikiPageDisplay getPageDisplay(
			WikiPage page, PortletURL viewPageURL, PortletURL editPageURL,
			String attachmentURLPrefix, ServiceContext serviceContext)
		throws PortalException {

		return getPageDisplay(
			page, viewPageURL, () -> editPageURL, attachmentURLPrefix,
			serviceContext);
	}

	@Override
	public WikiPageDisplay getPageDisplay(
			WikiPage page, PortletURL viewPageURL,
			Supplier<PortletURL> editPageURLSupplier,
			String attachmentURLPrefix, ServiceContext serviceContext)
		throws PortalException {

		HttpServletRequest httpServletRequest = serviceContext.getRequest();

		boolean workflowAssetPreview = false;

		if (httpServletRequest != null) {
			workflowAssetPreview = GetterUtil.getBoolean(
				httpServletRequest.getAttribute(
					WebKeys.WORKFLOW_ASSET_PREVIEW));
		}

		if (!workflowAssetPreview && page.isApproved()) {
			return getDisplay(
				page.getNodeId(), page.getTitle(), viewPageURL,
				editPageURLSupplier, attachmentURLPrefix);
		}

		return getPageDisplay(
			page, viewPageURL, editPageURLSupplier.get(), attachmentURLPrefix);
	}

	@Override
	public List<WikiPage> getPages(
		long nodeId, boolean head, int start, int end) {

		return getPages(
			nodeId, head, start, end,
			PageCreateDateComparator.getInstance(false));
	}

	@Override
	public List<WikiPage> getPages(
		long nodeId, boolean head, int status, int start, int end) {

		return getPages(
			nodeId, head, status, start, end,
			PageCreateDateComparator.getInstance(false));
	}

	@Override
	public List<WikiPage> getPages(
		long nodeId, boolean head, int status, int start, int end,
		OrderByComparator<WikiPage> orderByComparator) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return wikiPagePersistence.findByN_H(
				nodeId, head, start, end, orderByComparator);
		}

		return wikiPagePersistence.findByN_H_S(
			nodeId, head, status, start, end, orderByComparator);
	}

	@Override
	public List<WikiPage> getPages(
		long nodeId, boolean head, int start, int end,
		OrderByComparator<WikiPage> orderByComparator) {

		return getPages(
			nodeId, head, WorkflowConstants.STATUS_APPROVED, start, end,
			orderByComparator);
	}

	@Override
	public List<WikiPage> getPages(long nodeId, int start, int end) {
		return getPages(
			nodeId, start, end, PageCreateDateComparator.getInstance(false));
	}

	@Override
	public List<WikiPage> getPages(
		long nodeId, int start, int end,
		OrderByComparator<WikiPage> orderByComparator) {

		return wikiPagePersistence.findByNodeId(
			nodeId, start, end, orderByComparator);
	}

	@Override
	public List<WikiPage> getPages(
		long resourcePrimKey, long nodeId, int status) {

		return wikiPagePersistence.findByR_N_S(resourcePrimKey, nodeId, status);
	}

	@Override
	public List<WikiPage> getPages(
		long userId, long nodeId, int status, int start, int end) {

		if (userId > 0) {
			return wikiPagePersistence.findByU_N_S(
				userId, nodeId, status, start, end,
				PageCreateDateComparator.getInstance(false));
		}

		return wikiPagePersistence.findByN_S(
			nodeId, status, start, end,
			PageCreateDateComparator.getInstance(false));
	}

	@Override
	public List<WikiPage> getPages(
		long groupId, long nodeId, int status, long statusByUserId) {

		return dslQuery(
			DSLQueryFactoryUtil.select(
				WikiPageTable.INSTANCE
			).from(
				WikiPageTable.INSTANCE
			).where(
				WikiPageTable.INSTANCE.groupId.eq(
					groupId
				).and(
					WikiPageTable.INSTANCE.nodeId.eq(nodeId)
				).and(
					WikiPageTable.INSTANCE.status.eq(status)
				).and(
					WikiPageTable.INSTANCE.statusByUserId.eq(statusByUserId)
				)
			));
	}

	@Override
	public List<WikiPage> getPages(
		long nodeId, String title, boolean head, int start, int end) {

		return wikiPagePersistence.findByN_T_H(
			nodeId, title, head, start, end,
			PageCreateDateComparator.getInstance(false));
	}

	@Override
	public List<WikiPage> getPages(
		long nodeId, String title, int start, int end) {

		return wikiPagePersistence.findByN_T(
			nodeId, title, start, end,
			PageCreateDateComparator.getInstance(false));
	}

	@Override
	public List<WikiPage> getPages(
		long nodeId, String title, int start, int end,
		OrderByComparator<WikiPage> orderByComparator) {

		return wikiPagePersistence.findByN_T(
			nodeId, title, start, end, orderByComparator);
	}

	@Override
	public List<WikiPage> getPages(String format) {
		return wikiPagePersistence.findByFormat(format);
	}

	@Override
	public int getPagesCount(long nodeId) {
		return wikiPagePersistence.countByNodeId(nodeId);
	}

	@Override
	public int getPagesCount(long nodeId, boolean head) {
		return wikiPagePersistence.countByN_H_S(
			nodeId, head, WorkflowConstants.STATUS_APPROVED);
	}

	@Override
	public int getPagesCount(long nodeId, boolean head, int status) {
		if (status == WorkflowConstants.STATUS_ANY) {
			return wikiPagePersistence.countByN_H_NotS(
				nodeId, head, WorkflowConstants.STATUS_IN_TRASH);
		}

		return wikiPagePersistence.countByN_H_S(nodeId, head, status);
	}

	@Override
	public int getPagesCount(long nodeId, int status) {
		return wikiPagePersistence.countByN_S(nodeId, status);
	}

	@Override
	public int getPagesCount(long userId, long nodeId, int status) {
		if (userId > 0) {
			return wikiPagePersistence.countByU_N_S(userId, nodeId, status);
		}

		return wikiPagePersistence.countByN_S(nodeId, status);
	}

	@Override
	public int getPagesCount(long nodeId, String title) {
		return wikiPagePersistence.countByN_T(nodeId, title);
	}

	@Override
	public int getPagesCount(long nodeId, String title, boolean head) {
		return wikiPagePersistence.countByN_T_H(nodeId, title, head);
	}

	@Override
	public int getPagesCount(String format) {
		return wikiPagePersistence.countByFormat(format);
	}

	@Override
	public WikiPage getPreviousVersionPage(WikiPage page)
		throws PortalException {

		double previousVersion = MathUtil.format(page.getVersion() - 0.1, 1, 1);

		if (previousVersion < 1) {
			return null;
		}

		return getPage(page.getNodeId(), page.getTitle(), previousVersion);
	}

	@Override
	public List<WikiPage> getRecentChanges(
		long groupId, long nodeId, int start, int end) {

		Calendar cal = CalendarFactoryUtil.getCalendar();

		cal.add(Calendar.WEEK_OF_YEAR, -1);

		return wikiPageFinder.findByModifiedDate(
			groupId, nodeId, new Timestamp(cal.getTimeInMillis()), false, start,
			end);
	}

	@Override
	public int getRecentChangesCount(long groupId, long nodeId) {
		Calendar cal = CalendarFactoryUtil.getCalendar();

		cal.add(Calendar.WEEK_OF_YEAR, -1);

		return wikiPageFinder.countByModifiedDate(
			groupId, nodeId, cal.getTime(), false);
	}

	@Override
	public List<WikiPage> getRedirectorPages(
		long nodeId, boolean head, String redirectTitle, int status) {

		return wikiPagePersistence.findByN_H_R_S(
			nodeId, head, redirectTitle, status);
	}

	@Override
	public List<WikiPage> getRedirectorPages(
		long nodeId, String redirectTitle) {

		return wikiPagePersistence.findByN_R(nodeId, redirectTitle);
	}

	@Override
	public String[] getTempFileNames(
			long groupId, long userId, String folderName)
		throws PortalException {

		return TempFileEntryUtil.getTempFileNames(groupId, userId, folderName);
	}

	@Override
	public boolean hasDraftPage(long nodeId, String title) {
		int count = wikiPagePersistence.countByN_T_S(
			nodeId, title, WorkflowConstants.STATUS_DRAFT);

		if (count > 0) {
			return true;
		}

		return false;
	}

	@Override
	public void moveDependentToTrash(WikiPage page, long trashEntryId)
		throws PortalException {

		_moveDependentToTrash(page, trashEntryId, false);
	}

	@Override
	public FileEntry movePageAttachmentToTrash(
			long userId, long nodeId, String title, String fileName)
		throws PortalException {

		WikiPage page = getPage(nodeId, title);

		FileEntry fileEntry = _portletFileRepository.getPortletFileEntry(
			page.getGroupId(), page.getAttachmentsFolderId(), fileName);

		fileEntry = _portletFileRepository.movePortletFileEntryToTrash(
			userId, fileEntry.getFileEntryId());

		JSONObject extraDataJSONObject = JSONUtil.put(
			"fileEntryId", fileEntry.getFileEntryId()
		).put(
			"fileEntryTitle",
			_trashHelper.getOriginalTitle(fileEntry.getTitle())
		).put(
			"title", page.getTitle()
		).put(
			"version", page.getVersion()
		);

		SocialActivityManagerUtil.addActivity(
			userId, page, SocialActivityConstants.TYPE_MOVE_ATTACHMENT_TO_TRASH,
			extraDataJSONObject.toString(), 0);

		return fileEntry;
	}

	@Override
	public WikiPage movePageFromTrash(
			long userId, long nodeId, String title, long newNodeId,
			String newParentTitle)
		throws PortalException {

		WikiPage page = getPage(nodeId, title);

		if (!page.isInTrash()) {
			throw new RestoreEntryException(
				RestoreEntryException.INVALID_STATUS);
		}

		if (_trashHelper.isInTrashExplicitly(page)) {
			_movePageFromTrash(userId, page, newNodeId, newParentTitle);
		}
		else {
			_moveDependentFromTrash(page, newNodeId, newParentTitle);
		}

		return page;
	}

	@Override
	public WikiPage movePageToTrash(long userId, long nodeId, String title)
		throws PortalException {

		WikiPage page = wikiPagePersistence.fetchByN_T_H_First(
			nodeId, title, true, null);

		if (page != null) {
			return movePageToTrash(userId, page);
		}

		return null;
	}

	@Override
	public WikiPage movePageToTrash(
			long userId, long nodeId, String title, double version)
		throws PortalException {

		WikiPage page = wikiPagePersistence.findByN_T_V(nodeId, title, version);

		return movePageToTrash(userId, page);
	}

	@Override
	public WikiPage movePageToTrash(long userId, WikiPage page)
		throws PortalException {

		if (page.isInTrash()) {
			throw new TrashEntryException();
		}

		// Page

		int oldStatus = page.getStatus();
		String oldTitle = page.getTitle();

		if (oldStatus == WorkflowConstants.STATUS_PENDING) {
			page.setStatus(WorkflowConstants.STATUS_DRAFT);

			page = wikiPagePersistence.update(page);
		}

		List<WikiPage> pageVersions = wikiPagePersistence.findByR_N_H(
			page.getResourcePrimKey(), page.getNodeId(), false);

		pageVersions = ListUtil.sort(
			pageVersions, PageVersionComparator.getInstance(false));

		List<ObjectValuePair<Long, Integer>> pageVersionStatusOVPs =
			new ArrayList<>();

		if ((pageVersions != null) && !pageVersions.isEmpty()) {
			pageVersionStatusOVPs = _getPageVersionStatuses(pageVersions);
		}

		page = updateStatus(
			userId, page, WorkflowConstants.STATUS_IN_TRASH,
			new ServiceContext(), new HashMap<>());

		// Trash

		WikiPageResource pageResource =
			_wikiPageResourcePersistence.fetchByPrimaryKey(
				page.getResourcePrimKey());

		TrashEntry trashEntry = _trashEntryLocalService.addTrashEntry(
			userId, page.getGroupId(), WikiPage.class.getName(),
			page.getResourcePrimKey(), page.getUuid(), null, oldStatus,
			pageVersionStatusOVPs,
			UnicodePropertiesBuilder.put(
				"title", page.getTitle()
			).build());

		String trashTitle = _trashHelper.getTrashTitle(trashEntry.getEntryId());

		for (WikiPage pageVersion : pageVersions) {
			pageVersion.setTitle(trashTitle);
			pageVersion.setStatus(WorkflowConstants.STATUS_IN_TRASH);

			wikiPagePersistence.update(pageVersion);
		}

		pageResource.setTitle(trashTitle);

		_wikiPageResourcePersistence.update(pageResource);

		page.setTitle(trashTitle);

		page = wikiPagePersistence.update(page);

		// Child pages

		_moveDependentChildPagesToTrash(
			page.getNodeId(), oldTitle, trashTitle, trashEntry.getEntryId(),
			true);

		// Redirect pages

		_moveDependentRedirectorPagesToTrash(
			page.getNodeId(), oldTitle, trashTitle, trashEntry.getEntryId(),
			true);

		// Asset

		_assetEntryLocalService.updateVisible(
			WikiPage.class.getName(), page.getResourcePrimKey(), false);

		// Attachments

		WikiPage latestPage = getLatestPage(
			page.getResourcePrimKey(), WorkflowConstants.STATUS_ANY, false);

		for (FileEntry fileEntry : latestPage.getAttachmentsFileEntries()) {
			_portletFileRepository.movePortletFileEntryToTrash(
				userId, fileEntry.getFileEntryId());
		}

		// Comment

		_commentManager.moveDiscussionToTrash(
			WikiPage.class.getName(), page.getResourcePrimKey());

		// Social

		JSONObject extraDataJSONObject = JSONUtil.put(
			"title", _trashHelper.getOriginalTitle(page.getTitle())
		).put(
			"version", page.getVersion()
		);

		SocialActivityManagerUtil.addActivity(
			userId, page, SocialActivityConstants.TYPE_MOVE_TO_TRASH,
			extraDataJSONObject.toString(), 0);

		// Indexer

		Indexer<WikiPage> indexer = _indexerRegistry.nullSafeGetIndexer(
			WikiPage.class);

		indexer.reindex(page);

		// Workflow

		if (oldStatus == WorkflowConstants.STATUS_PENDING) {
			_workflowInstanceLinkLocalService.deleteWorkflowInstanceLink(
				page.getCompanyId(), page.getGroupId(),
				WikiPage.class.getName(), page.getPageId());
		}

		return page;
	}

	@Override
	public void renamePage(
			long userId, long nodeId, String title, String newTitle,
			boolean strict, ServiceContext serviceContext)
		throws PortalException {

		WikiPage latestWikiPage = fetchLatestPage(
			nodeId, title, WorkflowConstants.STATUS_ANY, false);

		if ((latestWikiPage != null) && !latestWikiPage.isApproved()) {
			throw new PageVersionException();
		}

		newTitle = StringUtil.replace(
			newTitle, CharPool.NO_BREAK_SPACE, CharPool.SPACE);

		_wikiPageTitleValidator.validate(newTitle);

		if (StringUtil.equalsIgnoreCase(title, newTitle)) {
			throw new DuplicatePageException(newTitle);
		}

		if (_isUsedTitle(nodeId, newTitle)) {

			// Support moving back to a previously moved title

			WikiPage page = getPage(nodeId, newTitle);

			String content = page.getContent();

			if (((page.getVersion() == WikiPageConstants.VERSION_DEFAULT) &&
				 (content.length() < 200)) ||
				!strict) {

				deletePage(nodeId, newTitle);
			}
			else {
				throw new DuplicatePageException(newTitle);
			}
		}

		WikiPage page = getPage(nodeId, title);

		String summary = page.getSummary();

		if (Validator.isNotNull(page.getRedirectTitle())) {
			page.setRedirectTitle(StringPool.BLANK);

			summary = StringPool.BLANK;
		}

		_populateServiceContext(serviceContext, page);

		serviceContext.setCommand(Constants.RENAME);

		WikiPageRenameContentProcessor wikiPageRenameContentProcessor =
			_serviceTrackerMap.getService(page.getFormat());

		String content = page.getContent();

		if (wikiPageRenameContentProcessor != null) {
			List<WikiPage> versionPages = wikiPagePersistence.findByN_T_H(
				nodeId, title, false);

			for (WikiPage curPage : versionPages) {
				curPage.setTitle(newTitle);
				curPage.setContent(
					wikiPageRenameContentProcessor.processContent(
						curPage.getNodeId(), title, newTitle,
						curPage.getContent()));

				wikiPagePersistence.update(curPage);
			}

			content = wikiPageRenameContentProcessor.processContent(
				page.getNodeId(), title, newTitle, content);
		}

		_updatePage(
			userId, page, newTitle, content, summary, page.isMinorEdit(),
			page.getFormat(), page.getParentTitle(), page.getRedirectTitle(),
			serviceContext);
	}

	@Override
	public void renamePage(
			long userId, long nodeId, String title, String newTitle,
			ServiceContext serviceContext)
		throws PortalException {

		renamePage(userId, nodeId, title, newTitle, true, serviceContext);
	}

	@Override
	public void restorePageAttachmentFromTrash(
			long userId, long nodeId, String title, String fileName)
		throws PortalException {

		WikiPage page = getPage(nodeId, title);

		FileEntry fileEntry = _portletFileRepository.getPortletFileEntry(
			page.getGroupId(), page.getAttachmentsFolderId(), fileName);

		JSONObject extraDataJSONObject = JSONUtil.put(
			"fileEntryId", fileEntry.getFileEntryId()
		).put(
			"fileEntryTitle",
			_trashHelper.getOriginalTitle(fileEntry.getTitle())
		).put(
			"title", page.getTitle()
		).put(
			"version", page.getVersion()
		);

		_portletFileRepository.restorePortletFileEntryFromTrash(
			userId, fileEntry.getFileEntryId());

		SocialActivityManagerUtil.addActivity(
			userId, page,
			SocialActivityConstants.TYPE_RESTORE_ATTACHMENT_FROM_TRASH,
			extraDataJSONObject.toString(), 0);
	}

	@Override
	public void restorePageFromTrash(long userId, WikiPage page)
		throws PortalException {

		if (!page.isInTrash()) {
			throw new RestoreEntryException(
				RestoreEntryException.INVALID_STATUS);
		}

		if (_trashHelper.isInTrashExplicitly(page)) {
			_movePageFromTrash(
				userId, page, page.getNodeId(), page.getParentTitle());
		}
		else {
			_moveDependentFromTrash(
				page, page.getNodeId(), page.getParentTitle());
		}
	}

	@Override
	public WikiPage revertPage(
			long userId, long nodeId, String title, double version,
			ServiceContext serviceContext)
		throws PortalException {

		WikiPage oldPage = getPage(nodeId, title, version);

		_populateServiceContext(serviceContext, oldPage);

		return updatePage(
			userId, nodeId, title, 0, oldPage.getContent(),
			WikiPageConstants.REVERTED + " to " + version, false,
			oldPage.getFormat(), _getParentPageTitle(oldPage),
			oldPage.getRedirectTitle(), serviceContext);
	}

	@Override
	public void subscribePage(long userId, long nodeId, String title)
		throws PortalException {

		WikiPage page = getPage(nodeId, title);

		_subscriptionLocalService.addSubscription(
			userId, page.getGroupId(), WikiPage.class.getName(),
			page.getResourcePrimKey());
	}

	@Override
	public void unsubscribePage(long userId, long nodeId, String title)
		throws PortalException {

		WikiPage page = getPage(nodeId, title);

		_subscriptionLocalService.deleteSubscription(
			userId, WikiPage.class.getName(), page.getResourcePrimKey());
	}

	@Override
	public void updateAsset(
			long userId, WikiPage page, long[] assetCategoryIds,
			String[] assetTagNames, long[] assetLinkEntryIds, Double priority)
		throws PortalException {

		boolean addDraftAssetEntry = false;

		if (!page.isApproved() &&
			(page.getVersion() != WikiPageConstants.VERSION_DEFAULT)) {

			int approvedPagesCount = wikiPagePersistence.countByN_T_S(
				page.getNodeId(), page.getTitle(),
				WorkflowConstants.STATUS_APPROVED);

			if (approvedPagesCount > 0) {
				addDraftAssetEntry = true;
			}
		}

		AssetEntry assetEntry = null;

		Date publishDate = null;

		if (addDraftAssetEntry) {
			assetEntry = _assetEntryLocalService.updateEntry(
				userId, page.getGroupId(), page.getCreateDate(),
				page.getModifiedDate(), WikiPage.class.getName(),
				page.getPrimaryKey(), page.getUuid(), 0, assetCategoryIds,
				assetTagNames, true, false, null, null, publishDate, null,
				ContentTypes.TEXT_HTML, page.getTitle(), null, null, null, null,
				0, 0, priority);
		}
		else {
			if (page.isApproved()) {
				publishDate = page.getCreateDate();
			}

			assetEntry = _assetEntryLocalService.updateEntry(
				userId, page.getGroupId(), page.getCreateDate(),
				page.getModifiedDate(), WikiPage.class.getName(),
				page.getResourcePrimKey(), page.getUuid(), 0, assetCategoryIds,
				assetTagNames, true, page.isApproved(), null, null, publishDate,
				null, ContentTypes.TEXT_HTML, page.getTitle(), null, null, null,
				null, 0, 0, priority);
		}

		_assetLinkLocalService.updateLinks(
			userId, assetEntry.getEntryId(), assetLinkEntryIds,
			AssetLinkConstants.TYPE_RELATED);
	}

	@BufferedIncrement(
		configuration = "WikiNode", incrementClass = DateOverrideIncrement.class
	)
	@Override
	public void updateLastPostDate(long nodeId, Date lastPostDate) {
		WikiNode node = _wikiNodePersistence.fetchByPrimaryKey(nodeId);

		Date oldLastPostDate = node.getLastPostDate();

		if ((node == null) ||
			((oldLastPostDate != null) &&
			 lastPostDate.before(oldLastPostDate))) {

			return;
		}

		node.setLastPostDate(lastPostDate);

		try {
			_wikiNodePersistence.update(node);
		}
		catch (SystemException systemException) {
			if (_log.isWarnEnabled()) {
				_log.warn(systemException);
			}
		}
	}

	@Override
	public WikiPage updatePage(
			long userId, long nodeId, String title, double version,
			String content, String summary, boolean minorEdit, String format,
			String parentTitle, String redirectTitle,
			ServiceContext serviceContext)
		throws PortalException {

		try {
			WikiPage oldPage = wikiPagePersistence.findByN_T_First(
				nodeId, title, null);

			if ((version > 0) && (version != oldPage.getVersion())) {
				throw new PageVersionException();
			}

			return _updatePage(
				userId, oldPage, StringPool.BLANK, content, summary, minorEdit,
				format, parentTitle, redirectTitle, serviceContext);
		}
		catch (NoSuchPageException noSuchPageException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchPageException);
			}

			return addPage(
				userId, nodeId, title, WikiPageConstants.VERSION_DEFAULT,
				content, summary, minorEdit, format, true, parentTitle,
				redirectTitle, serviceContext);
		}
	}

	@Override
	public WikiPage updateStatus(
			long userId, long resourcePrimKey, int status,
			ServiceContext serviceContext)
		throws PortalException {

		WikiPageResource pageResource =
			_wikiPageResourcePersistence.findByPrimaryKey(resourcePrimKey);

		WikiPage page = wikiPagePersistence.findByN_T_First(
			pageResource.getNodeId(), pageResource.getTitle(),
			PageVersionComparator.getInstance(false));

		return updateStatus(
			userId, page, status, serviceContext, new HashMap<>());
	}

	@Override
	public WikiPage updateStatus(
			long userId, WikiPage page, int status,
			ServiceContext serviceContext,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		// Page

		User user = _userLocalService.getUser(userId);

		int oldStatus = page.getStatus();

		if (status == WorkflowConstants.STATUS_APPROVED) {
			String cmd = GetterUtil.getString(
				workflowContext.get(WorkflowConstants.CONTEXT_COMMAND));

			if (cmd.equals(Constants.CHANGE_PARENT)) {
				page = _updatePageStatus(user, page, status);

				List<WikiPage> pageVersions = wikiPagePersistence.findByN_T(
					page.getNodeId(), page.getTitle());

				for (WikiPage pageVersion : pageVersions) {
					pageVersion.setParentTitle(page.getParentTitle());

					pageVersion = wikiPagePersistence.update(pageVersion);

					if (pageVersion.equals(page)) {
						page = pageVersion;
					}
				}
			}
			else if (cmd.equals(Constants.RENAME)) {
				WikiPage oldPage = getPage(page.getResourcePrimKey(), true);

				WikiPage renamedPage = _renamePage(
					userId, page.getNodeId(), oldPage.getTitle(),
					page.getTitle(), serviceContext);

				_updatePageStatus(user, page, status);

				page = renamedPage;
			}
			else {
				page = _updatePageStatus(user, page, status);
			}

			// Asset

			if ((oldStatus != WorkflowConstants.STATUS_APPROVED) &&
				(page.getVersion() != WikiPageConstants.VERSION_DEFAULT)) {

				AssetEntry draftAssetEntry = _assetEntryLocalService.fetchEntry(
					WikiPage.class.getName(), page.getPrimaryKey());

				if (draftAssetEntry != null) {
					long[] assetCategoryIds = draftAssetEntry.getCategoryIds();
					String[] assetTagNames = draftAssetEntry.getTagNames();

					List<AssetLink> assetLinks =
						_assetLinkLocalService.getDirectLinks(
							draftAssetEntry.getEntryId(),
							AssetLinkConstants.TYPE_RELATED, false);

					long[] assetLinkEntryIds = ListUtil.toLongArray(
						assetLinks, AssetLink.ENTRY_ID2_ACCESSOR);

					AssetEntry assetEntry = _assetEntryLocalService.updateEntry(
						userId, page.getGroupId(), page.getCreateDate(),
						page.getModifiedDate(), WikiPage.class.getName(),
						page.getResourcePrimKey(), page.getUuid(), 0,
						assetCategoryIds, assetTagNames, true, true, null, null,
						page.getCreateDate(), null, ContentTypes.TEXT_HTML,
						page.getTitle(), null, null, null, null, 0, 0, null);

					// Asset Links

					_assetLinkLocalService.updateLinks(
						userId, assetEntry.getEntryId(), assetLinkEntryIds,
						AssetLinkConstants.TYPE_RELATED);

					SystemEventHierarchyEntryThreadLocal.push(WikiPage.class);

					try {
						_assetEntryLocalService.deleteEntry(
							draftAssetEntry.getEntryId());
					}
					finally {
						SystemEventHierarchyEntryThreadLocal.pop(
							WikiPage.class);
					}
				}
			}

			_assetEntryLocalService.updateVisible(
				WikiPage.class.getName(), page.getResourcePrimKey(), true);

			// Social

			WikiGroupServiceOverriddenConfiguration
				wikiGroupServiceOverriddenConfiguration =
					_configurationProvider.getConfiguration(
						WikiGroupServiceOverriddenConfiguration.class,
						new GroupServiceSettingsLocator(
							page.getGroupId(), WikiConstants.SERVICE_NAME));

			if ((oldStatus != WorkflowConstants.STATUS_IN_TRASH) &&
				(!page.isMinorEdit() ||
				 wikiGroupServiceOverriddenConfiguration.
					 pageMinorEditAddSocialActivity())) {

				JSONObject extraDataJSONObject = JSONUtil.put(
					"title", page.getTitle()
				).put(
					"version", page.getVersion()
				);

				int type = WikiActivityKeys.UPDATE_PAGE;

				if (serviceContext.isCommandAdd()) {
					type = WikiActivityKeys.ADD_PAGE;
				}

				SocialActivityManagerUtil.addActivity(
					userId, page, type, extraDataJSONObject.toString(), 0);
			}

			// Subscriptions

			if (NotificationThreadLocal.isEnabled() &&
				(!page.isMinorEdit() ||
				 wikiGroupServiceOverriddenConfiguration.
					 pageMinorEditSendEmail())) {

				_notifySubscribers(
					userId, page,
					(String)workflowContext.get(WorkflowConstants.CONTEXT_URL),
					serviceContext);
			}

			// Cache

			_clearPageCache();
		}
		else {
			page = _updatePageStatus(user, page, status);
		}

		// Head

		if (status == WorkflowConstants.STATUS_APPROVED) {
			page.setHead(true);

			List<WikiPage> pages = wikiPagePersistence.findByN_T_H(
				page.getNodeId(), page.getTitle(), true);

			for (WikiPage curPage : pages) {
				if (!curPage.equals(page)) {
					curPage.setHead(false);

					wikiPagePersistence.update(curPage);
				}
			}
		}
		else if (status != WorkflowConstants.STATUS_IN_TRASH) {
			page.setHead(false);

			List<WikiPage> pages = wikiPagePersistence.findByN_T_S(
				page.getNodeId(), page.getTitle(),
				WorkflowConstants.STATUS_APPROVED);

			for (WikiPage curPage : pages) {
				if (!curPage.equals(page)) {
					curPage.setHead(true);

					wikiPagePersistence.update(curPage);

					break;
				}
			}
		}

		// Indexer

		Indexer<WikiPage> indexer = _indexerRegistry.nullSafeGetIndexer(
			WikiPage.class);

		indexer.reindex(page);

		return wikiPagePersistence.update(page);
	}

	@Override
	public WikiPage updateWikiPage(
		WikiPage wikiPage, ServiceContext serviceContext) {

		return wikiPagePersistence.update(wikiPage, serviceContext);
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, WikiPageRenameContentProcessor.class,
			"wiki.format.name");

		_portalCache =
			(PortalCache<String, Serializable>)_multiVMPool.getPortalCache(
				WikiPageDisplay.class.getName());

		_wikiFileUploadConfiguration = ConfigurableUtil.createConfigurable(
			WikiFileUploadConfiguration.class, properties);
	}

	@Deactivate
	@Override
	protected void deactivate() {
		super.deactivate();

		_serviceTrackerMap.close();

		_portalCache.removeAll();
	}

	private void _clearPageCache() {
		if (!WikiCacheThreadLocal.isClearCache()) {
			return;
		}

		_portalCache.removeAll();
	}

	private void _deletePageAttachment(long fileEntryId)
		throws PortalException {

		_portletFileRepository.deletePortletFileEntry(fileEntryId);
	}

	private String _encodeKey(long nodeId, String title, String postfix) {
		StringBundler sb = new StringBundler(5);

		sb.append(StringUtil.toHexString(nodeId));
		sb.append(StringPool.POUND);
		sb.append(title);

		if (postfix != null) {
			sb.append(StringPool.POUND);
			sb.append(postfix);
		}

		return sb.toString();
	}

	private String _formatContent(String content) {
		return StringUtil.replace(
			content, new String[] {"</p>", "</br>", "</div>"},
			new String[] {"</p>\n", "</br>\n", "</div>\n"});
	}

	private String _getDiffsURL(
		WikiPage page, WikiPage previousVersionPage,
		ServiceContext serviceContext) {

		if (previousVersionPage == null) {
			return StringPool.BLANK;
		}

		String baseDiffsURL = (String)serviceContext.getAttribute(
			"baseDiffsURL");

		if (baseDiffsURL == null) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler();

		sb.append(baseDiffsURL);

		_setParameter(
			"mvcRenderCommandName", sb, serviceContext,
			"/wiki/compare_versions");
		_setParameter(
			"nodeId", sb, serviceContext, String.valueOf(page.getNodeId()));
		_setParameter(
			"sourceVersion", sb, serviceContext,
			String.valueOf(previousVersionPage.getVersion()));
		_setParameter(
			"targetVersion", sb, serviceContext,
			String.valueOf(page.getVersion()));
		_setParameter("title", sb, serviceContext, page.getTitle());
		_setParameter("type", sb, serviceContext, "html");

		return sb.toString();
	}

	private Map<String, Boolean> _getOutgoingLinks(WikiPage page)
		throws PageContentException {

		String key = _encodeKey(
			page.getNodeId(), page.getTitle(), _OUTGOING_LINKS);

		Map<String, Boolean> links = (Map<String, Boolean>)_portalCache.get(
			key);

		if (links == null) {
			WikiEngine wikiEngine = _wikiEngineRenderer.fetchWikiEngine(
				page.getFormat());

			if (wikiEngine != null) {
				links = wikiEngine.getOutgoingLinks(page);
			}
			else {
				links = Collections.emptyMap();
			}

			_portalCache.put(key, (Serializable)links);
		}

		return links;
	}

	private String _getPageURL(WikiPage page, ServiceContext serviceContext)
		throws PortalException {

		HttpServletRequest httpServletRequest = serviceContext.getRequest();

		if (httpServletRequest == null) {
			return StringPool.BLANK;
		}

		String layoutFullURL = _portal.getLayoutFullURL(
			page.getGroupId(), WikiPortletKeys.WIKI);

		if (Validator.isNotNull(layoutFullURL)) {
			return StringBundler.concat(
				layoutFullURL, Portal.FRIENDLY_URL_SEPARATOR, "wiki/",
				page.getNodeId(), StringPool.SLASH,
				URLCodec.encodeURL(WikiEscapeUtil.escapeName(page.getTitle())));
		}

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, WikiPortletKeys.WIKI_ADMIN,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/wiki/view_page_activities"
		).setParameter(
			"nodeId", page.getNodeId()
		).setParameter(
			"title", page.getTitle()
		).buildString();
	}

	private List<ObjectValuePair<Long, Integer>> _getPageVersionStatuses(
		List<WikiPage> pages) {

		return TransformUtil.transform(
			pages,
			page -> {
				int status = page.getStatus();

				if (status == WorkflowConstants.STATUS_PENDING) {
					status = WorkflowConstants.STATUS_DRAFT;
				}

				return new ObjectValuePair<>(page.getPageId(), status);
			});
	}

	private String _getParentPageTitle(WikiPage page) {

		// LPS-4586

		try {
			WikiPage parentPage = getPage(
				page.getNodeId(), page.getParentTitle());

			return parentPage.getTitle();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return null;
		}
	}

	private boolean _isLinkedTo(WikiPage page, String targetTitle)
		throws PortalException {

		Map<String, Boolean> links = _getOutgoingLinks(page);

		Boolean link = links.get(StringUtil.toLowerCase(targetTitle));

		if (link != null) {
			return true;
		}

		return false;
	}

	private boolean _isUsedTitle(long nodeId, String title) {
		if (getPagesCount(nodeId, title) > 0) {
			return true;
		}

		return false;
	}

	private void _moveDependentChildPagesFromTrash(
			WikiPage newParentPage, long oldParentPageNodeId,
			String oldParentPageTitle)
		throws PortalException {

		List<WikiPage> childPages = getChildren(
			oldParentPageNodeId, true, oldParentPageTitle,
			WorkflowConstants.STATUS_IN_TRASH);

		for (WikiPage childPage : childPages) {
			childPage = getPageByPageId(childPage.getPageId());

			childPage.setParentTitle(newParentPage.getTitle());

			childPage = wikiPagePersistence.update(childPage);

			if (_trashHelper.isInTrashImplicitly(childPage)) {
				_moveDependentFromTrash(
					childPage, newParentPage.getNodeId(),
					newParentPage.getTitle());
			}
		}
	}

	private void _moveDependentChildPagesToTrash(
			long parentNodeId, String parentTitle, String parentTrashTitle,
			long trashEntryId, boolean createTrashVersion)
		throws PortalException {

		List<WikiPage> childPages = wikiPagePersistence.findByN_H_P(
			parentNodeId, true, parentTitle);

		for (WikiPage childPage : childPages) {
			childPage = getPageByPageId(childPage.getPageId());

			childPage.setParentTitle(parentTrashTitle);

			childPage = wikiPagePersistence.update(childPage);

			if (!childPage.isInTrash()) {
				_moveDependentToTrash(
					childPage, trashEntryId, createTrashVersion);
			}
		}
	}

	private void _moveDependentFromTrash(
			WikiPage page, long newNodeId, String newParentTitle)
		throws PortalException {

		// Page

		String trashTitle = page.getTitle();

		TrashVersion trashVersion = _trashVersionLocalService.fetchVersion(
			WikiPage.class.getName(), page.getPageId());

		long oldNodeId = page.getNodeId();

		if (newNodeId == 0) {
			newNodeId = oldNodeId;
		}

		page.setNodeId(newNodeId);

		WikiPageResource pageResource =
			_wikiPageResourcePersistence.findByPrimaryKey(
				page.getResourcePrimKey());

		pageResource.setNodeId(newNodeId);

		if (trashVersion != null) {
			String originalTitle = _trashHelper.getOriginalTitle(
				page.getTitle());

			pageResource.setTitle(originalTitle);

			page.setTitle(originalTitle);
		}

		_wikiPageResourcePersistence.update(pageResource);

		page.setParentTitle(newParentTitle);

		page = wikiPagePersistence.update(page);

		int oldStatus = WorkflowConstants.STATUS_APPROVED;

		if (trashVersion != null) {
			oldStatus = trashVersion.getStatus();
		}

		// Version pages

		List<WikiPage> versionPages = wikiPagePersistence.findByR_N(
			page.getResourcePrimKey(), page.getNodeId());

		for (WikiPage versionPage : versionPages) {

			// Version page

			if (versionPage.equals(page)) {
				versionPage = page;
			}

			versionPage.setNodeId(newNodeId);
			versionPage.setTitle(page.getTitle());
			versionPage.setParentTitle(newParentTitle);

			trashVersion = _trashVersionLocalService.fetchVersion(
				WikiPage.class.getName(), versionPage.getPageId());

			int versionPageOldStatus = WorkflowConstants.STATUS_APPROVED;

			if (trashVersion != null) {
				versionPageOldStatus = trashVersion.getStatus();
			}

			versionPage.setStatus(versionPageOldStatus);

			wikiPagePersistence.update(versionPage);

			// Trash

			if (trashVersion != null) {
				_trashVersionLocalService.deleteTrashVersion(trashVersion);
			}
		}

		// Asset

		if (oldStatus == WorkflowConstants.STATUS_APPROVED) {
			_assetEntryLocalService.updateVisible(
				WikiPage.class.getName(), page.getResourcePrimKey(), true);
		}

		// Attachments

		WikiNode node = page.getNode();

		for (FileEntry fileEntry : page.getAttachmentsFileEntries()) {
			_portletFileRepository.restorePortletFileEntryFromTrash(
				node.getStatusByUserId(), fileEntry.getFileEntryId());
		}

		// Indexer

		Indexer<WikiPage> indexer = _indexerRegistry.nullSafeGetIndexer(
			WikiPage.class);

		indexer.reindex(page);

		// Child pages

		_moveDependentChildPagesFromTrash(page, oldNodeId, trashTitle);

		// Redirect pages

		_moveDependentRedirectorPagesFromTrash(page, oldNodeId, trashTitle);
	}

	private void _moveDependentRedirectorPagesFromTrash(
			WikiPage newRedirectPage, long oldRedirectPageNodeId,
			String oldRedirectPageTrashTitle)
		throws PortalException {

		List<WikiPage> redirectorPages = getRedirectorPages(
			oldRedirectPageNodeId, true, oldRedirectPageTrashTitle,
			WorkflowConstants.STATUS_IN_TRASH);

		for (WikiPage redirectorPage : redirectorPages) {
			redirectorPage = getPageByPageId(redirectorPage.getPageId());

			redirectorPage.setRedirectTitle(newRedirectPage.getTitle());

			redirectorPage = wikiPagePersistence.update(redirectorPage);

			if (_trashHelper.isInTrashImplicitly(redirectorPage)) {
				_moveDependentFromTrash(
					redirectorPage, newRedirectPage.getNodeId(),
					redirectorPage.getParentTitle());
			}
		}
	}

	private void _moveDependentRedirectorPagesToTrash(
			long redirectPageNodeId, String redirectPageTitle,
			String redirectPageTrashTitle, long trashEntryId,
			boolean createTrashVersion)
		throws PortalException {

		List<WikiPage> redirectorPages = wikiPagePersistence.findByN_H_R(
			redirectPageNodeId, true, redirectPageTitle);

		for (WikiPage redirectorPage : redirectorPages) {
			redirectorPage = getPageByPageId(redirectorPage.getPageId());

			redirectorPage.setRedirectTitle(redirectPageTrashTitle);

			redirectorPage = wikiPagePersistence.update(redirectorPage);

			if (!redirectorPage.isInTrash()) {
				_moveDependentToTrash(
					redirectorPage, trashEntryId, createTrashVersion);
			}
		}
	}

	private void _moveDependentToTrash(
			WikiPage page, long trashEntryId, boolean createTrashVersion)
		throws PortalException {

		// Page

		String oldTitle = page.getTitle();

		String trashTitle = oldTitle;

		if (createTrashVersion) {
			TrashVersion trashVersion =
				_trashVersionLocalService.addTrashVersion(
					trashEntryId, WikiPage.class.getName(), page.getPageId(),
					page.getStatus(),
					UnicodePropertiesBuilder.put(
						"title", oldTitle
					).build());

			trashTitle = _trashHelper.getTrashTitle(
				trashVersion.getVersionId());

			WikiPageResource pageResource =
				_wikiPageResourcePersistence.findByPrimaryKey(
					page.getResourcePrimKey());

			pageResource.setTitle(trashTitle);

			_wikiPageResourcePersistence.update(pageResource);

			page.setTitle(trashTitle);

			page = wikiPagePersistence.update(page);
		}

		int oldStatus = page.getStatus();

		if (oldStatus == WorkflowConstants.STATUS_IN_TRASH) {
			return;
		}

		// Version pages

		List<WikiPage> versionPages = wikiPagePersistence.findByR_N(
			page.getResourcePrimKey(), page.getNodeId());

		for (WikiPage versionPage : versionPages) {

			// Version page

			versionPage.setTitle(page.getTitle());

			int versionPageOldStatus = versionPage.getStatus();

			versionPage.setStatus(WorkflowConstants.STATUS_IN_TRASH);

			versionPage = wikiPagePersistence.update(versionPage);

			// Trash

			int status = versionPageOldStatus;

			if (versionPageOldStatus == WorkflowConstants.STATUS_PENDING) {
				status = WorkflowConstants.STATUS_DRAFT;
			}

			if (versionPageOldStatus != WorkflowConstants.STATUS_APPROVED) {
				_trashVersionLocalService.addTrashVersion(
					trashEntryId, WikiPage.class.getName(),
					versionPage.getPageId(), status, null);
			}
		}

		// Asset

		if (oldStatus == WorkflowConstants.STATUS_APPROVED) {
			_assetEntryLocalService.updateVisible(
				WikiPage.class.getName(), page.getResourcePrimKey(), false);
		}

		// Attachments

		WikiNode node = page.getNode();

		for (FileEntry fileEntry : page.getAttachmentsFileEntries()) {
			_portletFileRepository.movePortletFileEntryToTrash(
				node.getStatusByUserId(), fileEntry.getFileEntryId());
		}

		// Indexer

		Indexer<WikiPage> indexer = _indexerRegistry.nullSafeGetIndexer(
			WikiPage.class);

		indexer.reindex(page);

		// Cache

		if (WikiCacheThreadLocal.isClearCache()) {
			_portalCache.removeAll();
		}

		// Workflow

		if (oldStatus == WorkflowConstants.STATUS_PENDING) {
			_workflowInstanceLinkLocalService.deleteWorkflowInstanceLink(
				page.getCompanyId(), page.getGroupId(),
				WikiPage.class.getName(), page.getResourcePrimKey());
		}

		// Child pages

		_moveDependentChildPagesToTrash(
			page.getNodeId(), oldTitle, trashTitle, trashEntryId,
			createTrashVersion);

		// Redirect pages

		_moveDependentRedirectorPagesToTrash(
			page.getNodeId(), oldTitle, trashTitle, trashEntryId,
			createTrashVersion);
	}

	private void _movePageFromTrash(
			long userId, WikiPage page, long newNodeId, String newParentTitle)
		throws PortalException {

		// Page

		String trashTitle = page.getTitle();

		String originalTitle = _trashHelper.getOriginalTitle(trashTitle);

		long oldNodeId = page.getNodeId();

		if (newNodeId == 0) {
			newNodeId = oldNodeId;
		}

		List<WikiPage> pageVersions = wikiPagePersistence.findByR_N_H(
			page.getResourcePrimKey(), oldNodeId, false);

		for (WikiPage pageVersion : pageVersions) {
			pageVersion.setNodeId(newNodeId);
			pageVersion.setTitle(originalTitle);
			pageVersion.setParentTitle(newParentTitle);

			wikiPagePersistence.update(pageVersion);
		}

		WikiPageResource pageResource =
			_wikiPageResourcePersistence.fetchByPrimaryKey(
				page.getResourcePrimKey());

		pageResource.setNodeId(newNodeId);
		pageResource.setTitle(originalTitle);

		_wikiPageResourcePersistence.update(pageResource);

		page.setNodeId(newNodeId);
		page.setTitle(originalTitle);

		WikiPage parentPage = page.getParentPage();

		if ((parentPage != null) && parentPage.isInTrash()) {
			page.setParentTitle(StringPool.BLANK);
		}

		if (Validator.isNotNull(newParentTitle)) {
			WikiPage newParentPage = getPage(newNodeId, newParentTitle);

			if (!newParentPage.isInTrash()) {
				page.setParentTitle(newParentTitle);
			}
		}

		WikiPage redirectPage = page.getRedirectPage();

		if ((redirectPage != null) && redirectPage.isInTrash()) {
			page.setRedirectTitle(StringPool.BLANK);
		}

		page = wikiPagePersistence.update(page);

		TrashEntry trashEntry = _trashEntryLocalService.getEntry(
			WikiPage.class.getName(), page.getResourcePrimKey());

		updateStatus(
			userId, page, trashEntry.getStatus(), new ServiceContext(),
			new HashMap<>());

		// Attachments

		for (FileEntry fileEntry : page.getDeletedAttachmentsFileEntries()) {
			_portletFileRepository.restorePortletFileEntryFromTrash(
				userId, fileEntry.getFileEntryId());
		}

		// Child pages

		_moveDependentChildPagesFromTrash(page, oldNodeId, trashTitle);

		// Redirect pages

		_moveDependentRedirectorPagesFromTrash(page, oldNodeId, trashTitle);

		// Trash

		List<TrashVersion> trashVersions =
			_trashVersionLocalService.getVersions(trashEntry.getEntryId());

		for (TrashVersion trashVersion : trashVersions) {
			WikiPage trashArticleVersion = wikiPagePersistence.findByPrimaryKey(
				trashVersion.getClassPK());

			trashArticleVersion.setStatus(trashVersion.getStatus());

			wikiPagePersistence.update(trashArticleVersion);
		}

		_trashEntryLocalService.deleteEntry(
			WikiPage.class.getName(), page.getResourcePrimKey());

		// Comment

		_commentManager.restoreDiscussionFromTrash(
			WikiPage.class.getName(), page.getResourcePrimKey());

		// Social

		JSONObject extraDataJSONObject = JSONUtil.put(
			"title", page.getTitle()
		).put(
			"version", page.getVersion()
		);

		SocialActivityManagerUtil.addActivity(
			userId, page, SocialActivityConstants.TYPE_RESTORE_FROM_TRASH,
			extraDataJSONObject.toString(), 0);

		Indexer<WikiPage> indexer = _indexerRegistry.nullSafeGetIndexer(
			WikiPage.class);

		indexer.reindex(page);
	}

	private String _normalizeSpace(String title) {
		if (title == null) {
			return null;
		}

		title = StringUtil.replace(
			title, CharPool.NO_BREAK_SPACE, CharPool.SPACE);

		title = title.trim();

		title = title.replaceAll("\\s+", " ");

		return title;
	}

	private void _notifySubscribers(
			long userId, WikiPage page, String pageURL,
			ServiceContext serviceContext)
		throws PortalException {

		if (!page.isApproved() || Validator.isNull(pageURL)) {
			return;
		}

		WikiGroupServiceOverriddenConfiguration
			wikiGroupServiceOverriddenConfiguration =
				_configurationProvider.getConfiguration(
					WikiGroupServiceOverriddenConfiguration.class,
					new GroupServiceSettingsLocator(
						page.getGroupId(), WikiConstants.SERVICE_NAME));

		boolean update = false;

		if (page.getVersion() > WikiPageConstants.VERSION_DEFAULT) {
			update = true;
		}

		if (!update &&
			wikiGroupServiceOverriddenConfiguration.emailPageAddedEnabled()) {
		}
		else if (update &&
				 wikiGroupServiceOverriddenConfiguration.
					 emailPageUpdatedEnabled()) {
		}
		else {
			return;
		}

		WikiPage previousVersionPage = getPreviousVersionPage(page);

		String attachmentURLPrefix = StringBundler.concat(
			serviceContext.getPortalURL(), serviceContext.getPathMain(),
			"/wiki/get_page_attachment?p_l_id=", serviceContext.getPlid(),
			"&nodeId=", page.getNodeId(), "&title=",
			URLCodec.encodeURL(page.getTitle()), "&fileName=");

		String pageDiffs = StringPool.BLANK;

		try {
			pageDiffs = _wikiEngineRenderer.diffHtml(
				previousVersionPage, page, null, null, attachmentURLPrefix);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		String pageContent = null;

		if (Objects.equals(page.getFormat(), "creole")) {
			pageContent = _wikiEngineRenderer.convert(
				page, null, null, attachmentURLPrefix);
		}
		else {
			pageContent = _formatContent(page.getContent());
		}

		String pageTitle = page.getTitle();

		String fromName =
			wikiGroupServiceOverriddenConfiguration.emailFromName();
		String fromAddress =
			wikiGroupServiceOverriddenConfiguration.emailFromAddress();

		LocalizedValuesMap subjectLocalizedValuesMap = null;
		LocalizedValuesMap bodyLocalizedValuesMap = null;

		if (update) {
			subjectLocalizedValuesMap =
				wikiGroupServiceOverriddenConfiguration.
					emailPageUpdatedSubject();
			bodyLocalizedValuesMap =
				wikiGroupServiceOverriddenConfiguration.emailPageUpdatedBody();
		}
		else {
			subjectLocalizedValuesMap =
				wikiGroupServiceOverriddenConfiguration.emailPageAddedSubject();
			bodyLocalizedValuesMap =
				wikiGroupServiceOverriddenConfiguration.emailPageAddedBody();
		}

		SubscriptionSender subscriptionSender = new SubscriptionSender();

		subscriptionSender.setClassName(page.getModelClassName());
		subscriptionSender.setClassPK(page.getPageId());
		subscriptionSender.setContextAttribute(
			"[$PAGE_CONTENT$]", pageContent, false);
		subscriptionSender.setContextAttribute(
			"[$PAGE_DIFFS$]", _diffHtml.replaceStyles(pageDiffs), false);

		WikiNode node = page.getNode();

		subscriptionSender.setContextAttributes(
			"[$DIFFS_URL$]",
			_getDiffsURL(page, previousVersionPage, serviceContext),
			"[$NODE_NAME$]", node.getName(), "[$PAGE_DATE_UPDATE$]",
			page.getModifiedDate(), "[$PAGE_ID$]", page.getPageId(),
			"[$PAGE_STATUS_BY_USER_NAME$]", page.getStatusByUserName(),
			"[$PAGE_SUMMARY$]", page.getSummary(), "[$PAGE_TITLE$]", pageTitle,
			"[$PAGE_URL$]", pageURL);

		subscriptionSender.setContextCreatorUserPrefix("PAGE");
		subscriptionSender.setCreatorUserId(page.getUserId());
		subscriptionSender.setCurrentUserId(userId);
		subscriptionSender.setEntryTitle(pageTitle);
		subscriptionSender.setEntryURL(pageURL);
		subscriptionSender.setFrom(fromAddress, fromName);
		subscriptionSender.setHtmlFormat(true);

		if (bodyLocalizedValuesMap != null) {
			subscriptionSender.setLocalizedBodyMap(
				_localization.getMap(bodyLocalizedValuesMap));
		}

		if (subjectLocalizedValuesMap != null) {
			subscriptionSender.setLocalizedSubjectMap(
				_localization.getMap(subjectLocalizedValuesMap));
		}

		subscriptionSender.setMailId(
			"wiki_page", page.getNodeId(), page.getPageId());

		int notificationType =
			UserNotificationDefinition.NOTIFICATION_TYPE_ADD_ENTRY;

		if (update) {
			notificationType =
				UserNotificationDefinition.NOTIFICATION_TYPE_UPDATE_ENTRY;
		}

		subscriptionSender.setNotificationType(notificationType);

		subscriptionSender.setPortletId(WikiPortletKeys.WIKI);
		subscriptionSender.setReplyToAddress(fromAddress);
		subscriptionSender.setScopeGroupId(page.getGroupId());
		subscriptionSender.setServiceContext(serviceContext);

		subscriptionSender.addAssetEntryPersistedSubscribers(
			WikiPage.class.getName(), page.getResourcePrimKey());
		subscriptionSender.addPersistedSubscribers(
			WikiNode.class.getName(), page.getNodeId());
		subscriptionSender.addPersistedSubscribers(
			WikiPage.class.getName(), page.getResourcePrimKey());

		subscriptionSender.flushNotificationsAsync();
	}

	private void _populateServiceContext(
			ServiceContext serviceContext, WikiPage page)
		throws PortalException {

		serviceContext.setAssetCategoryIds(
			_assetCategoryLocalService.getCategoryIds(
				WikiPage.class.getName(), page.getResourcePrimKey()));

		AssetEntry assetEntry = _assetEntryLocalService.getEntry(
			WikiPage.class.getName(), page.getResourcePrimKey());

		List<AssetLink> assetLinks = _assetLinkLocalService.getLinks(
			assetEntry.getEntryId());

		serviceContext.setAssetLinkEntryIds(
			ListUtil.toLongArray(assetLinks, AssetLink.ENTRY_ID2_ACCESSOR));

		serviceContext.setAssetTagNames(
			_assetTagLocalService.getTagNames(
				WikiPage.class.getName(), page.getResourcePrimKey()));

		ExpandoBridge expandoBridge = page.getExpandoBridge();

		serviceContext.setExpandoBridgeAttributes(
			expandoBridge.getAttributes());
	}

	private WikiPage _renamePage(
			long userId, long nodeId, String title, String newTitle,
			ServiceContext serviceContext)
		throws PortalException {

		// Version pages

		List<WikiPage> versionPages = wikiPagePersistence.findByN_T(
			nodeId, title, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			PageVersionComparator.getInstance(false));

		WikiPage page = fetchLatestPage(
			nodeId, newTitle, WorkflowConstants.STATUS_ANY, false);

		if (page == null) {
			page = getLatestPage(
				nodeId, title, WorkflowConstants.STATUS_ANY, false);
		}

		for (WikiPage versionPage : versionPages) {
			versionPage.setTitle(newTitle);
			versionPage.setRedirectTitle(page.getRedirectTitle());

			versionPage = wikiPagePersistence.update(versionPage);

			if (versionPage.equals(page)) {
				page = versionPage;
			}
		}

		// Page resource

		WikiPageResource pageResource =
			_wikiPageResourcePersistence.findByPrimaryKey(
				page.getResourcePrimKey());

		pageResource.setTitle(newTitle);

		_wikiPageResourcePersistence.update(pageResource);

		// Create stub page at the old location

		double version = WikiPageConstants.VERSION_DEFAULT;
		String summary = _language.format(
			serviceContext.getLocale(), "renamed-as-x", newTitle);
		String format = page.getFormat();
		boolean head = true;
		String parentTitle = page.getParentTitle();

		String redirectTitle = page.getTitle();

		String content =
			StringPool.DOUBLE_OPEN_BRACKET + redirectTitle +
				StringPool.DOUBLE_CLOSE_BRACKET;

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);

		_populateServiceContext(serviceContext, page);

		boolean workflowEnabled = WorkflowThreadLocal.isEnabled();

		WorkflowThreadLocal.setEnabled(false);

		serviceContext.setCommand(Constants.ADD);

		WikiPage newPage = addPage(
			userId, nodeId, title, version, content, summary, false, format,
			head, parentTitle, redirectTitle, serviceContext);

		_resourceLocalService.deleteResource(
			page.getCompanyId(), WikiPage.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL, newPage.getResourcePrimKey());

		_resourceLocalService.copyModelResources(
			page.getCompanyId(), WikiPage.class.getName(),
			page.getResourcePrimKey(), newPage.getResourcePrimKey());

		WorkflowThreadLocal.setEnabled(workflowEnabled);

		// Child pages

		List<WikiPage> childPages = wikiPagePersistence.findByN_P(
			nodeId, title);

		for (WikiPage childPage : childPages) {
			childPage.setParentTitle(newTitle);

			wikiPagePersistence.update(childPage);
		}

		// Redirect pages

		List<WikiPage> redirectorPages = getRedirectorPages(nodeId, title);

		for (WikiPage redirectorPage : redirectorPages) {
			redirectorPage.setRedirectTitle(newTitle);

			wikiPagePersistence.update(redirectorPage);
		}

		// Asset

		updateAsset(
			userId, page, serviceContext.getAssetCategoryIds(),
			serviceContext.getAssetTagNames(),
			serviceContext.getAssetLinkEntryIds(),
			serviceContext.getAssetPriority());

		return page;
	}

	private void _setParameter(
		String name, StringBundler sb, ServiceContext serviceContext,
		String value) {

		sb.append(StringPool.AMPERSAND);
		sb.append(serviceContext.getPortletId());
		sb.append(StringPool.UNDERLINE);
		sb.append(URLCodec.encodeURL(name));
		sb.append(StringPool.EQUAL);
		sb.append(URLCodec.encodeURL(value));
	}

	private WikiPage _startWorkflowInstance(
			long userId, WikiPage page, ServiceContext serviceContext)
		throws PortalException {

		Map<String, Serializable> workflowContext =
			HashMapBuilder.<String, Serializable>put(
				WorkflowConstants.CONTEXT_COMMAND, serviceContext.getCommand()
			).put(
				WorkflowConstants.CONTEXT_URL, _getPageURL(page, serviceContext)
			).build();

		return WorkflowHandlerRegistryUtil.startWorkflowInstance(
			page.getCompanyId(), page.getGroupId(), userId,
			WikiPage.class.getName(), page.getPageId(), page, serviceContext,
			workflowContext);
	}

	private void _updateLastPostDate(Date date, long nodeId) {
		if (!ExportImportThreadLocal.isImportInProcess() &&
			!ExportImportThreadLocal.isStagingInProcess()) {

			wikiPageLocalService.updateLastPostDate(nodeId, date);
		}
	}

	private WikiPage _updatePage(
			long userId, WikiPage oldPage, String newTitle, String content,
			String summary, boolean minorEdit, String format,
			String parentTitle, String redirectTitle,
			ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		long pageId = 0;

		if (oldPage.isApproved()) {
			pageId = counterLocalService.increment();
		}
		else {
			pageId = oldPage.getPageId();
		}

		content = SanitizerUtil.sanitize(
			user.getCompanyId(), oldPage.getGroupId(), userId,
			WikiPage.class.getName(), pageId, "text/" + format, content);

		long nodeId = oldPage.getNodeId();

		if (!format.equals(oldPage.getFormat())) {
			_validate(nodeId, content, format);
		}

		serviceContext.validateModifiedDate(
			oldPage, PageVersionException.class);

		long resourcePrimKey =
			_wikiPageResourceLocalService.getPageResourcePrimKey(
				oldPage.getGroupId(), oldPage.getNodeId(), oldPage.getTitle());

		Date date = new Date();

		WikiPage page = oldPage;

		double newVersion = oldPage.getVersion();

		if (oldPage.isApproved()) {
			newVersion = MathUtil.format(oldPage.getVersion() + 0.1, 1, 1);

			page = wikiPagePersistence.create(pageId);

			page.setUuid(serviceContext.getUuid());
		}

		page.setResourcePrimKey(resourcePrimKey);
		page.setGroupId(oldPage.getGroupId());
		page.setCompanyId(user.getCompanyId());
		page.setUserId(oldPage.getUserId());
		page.setUserName(user.getFullName());
		page.setCreateDate(oldPage.getCreateDate());
		page.setExternalReferenceCode(oldPage.getExternalReferenceCode());
		page.setNodeId(nodeId);
		page.setTitle(
			Validator.isNull(newTitle) ? oldPage.getTitle() : newTitle);
		page.setVersion(newVersion);
		page.setMinorEdit(minorEdit);
		page.setContent(content);
		page.setSummary(summary);
		page.setFormat(format);

		if (Validator.isNotNull(parentTitle)) {
			page.setParentTitle(parentTitle);
		}

		if (Validator.isNotNull(redirectTitle)) {
			page.setRedirectTitle(redirectTitle);
		}

		if (oldPage.isPending()) {
			page.setStatus(oldPage.getStatus());
		}
		else {
			page.setStatus(WorkflowConstants.STATUS_DRAFT);
		}

		page.setStatusByUserId(user.getUserId());
		page.setStatusByUserName(user.getFullName());
		page.setStatusDate(serviceContext.getModifiedDate(date));

		ExpandoBridgeUtil.setExpandoBridgeAttributes(
			oldPage.getExpandoBridge(), page.getExpandoBridge(),
			serviceContext);

		page = wikiPagePersistence.update(page);

		// Node

		_updateLastPostDate(serviceContext.getModifiedDate(date), nodeId);

		// Asset

		updateAsset(
			userId, page, serviceContext.getAssetCategoryIds(),
			serviceContext.getAssetTagNames(),
			serviceContext.getAssetLinkEntryIds(),
			serviceContext.getAssetPriority());

		// Workflow

		return _startWorkflowInstance(userId, page, serviceContext);
	}

	private WikiPage _updatePageStatus(User user, WikiPage page, int status) {
		page.setStatus(status);
		page.setStatusByUserId(user.getUserId());
		page.setStatusByUserName(user.getFullName());
		page.setStatusDate(new Date());

		return wikiPagePersistence.update(page);
	}

	private void _validate(long nodeId, String content, String format)
		throws PortalException {

		WikiEngine wikiEngine = _wikiEngineRenderer.fetchWikiEngine(format);

		if (!wikiEngine.validate(nodeId, content)) {
			throw new PageContentException();
		}
	}

	private void _validate(
			String title, long nodeId, String content, String format)
		throws PortalException {

		if (Validator.isNull(title)) {
			throw new PageTitleException();
		}

		if (_isUsedTitle(nodeId, title)) {
			throw new DuplicatePageException("{nodeId=" + nodeId + "}");
		}

		_wikiPageTitleValidator.validate(title);

		_validate(nodeId, content, format);
	}

	private void _validateExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		if (Validator.isNull(externalReferenceCode)) {
			return;
		}

		WikiPage wikiPage = fetchLatestPageByExternalReferenceCode(
			groupId, externalReferenceCode);

		if (wikiPage != null) {
			throw new DuplicateWikiPageExternalReferenceCodeException(
				StringBundler.concat(
					"Duplicate wiki page external reference code ",
					externalReferenceCode, " in group ", groupId));
		}
	}

	private static final String _OUTGOING_LINKS = "OUTGOING_LINKS";

	private static final Log _log = LogFactoryUtil.getLog(
		WikiPageLocalServiceImpl.class);

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private AssetLinkLocalService _assetLinkLocalService;

	@Reference
	private AssetTagLocalService _assetTagLocalService;

	@Reference
	private CommentManager _commentManager;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private DiffHtml _diffHtml;

	@Reference
	private ExpandoRowLocalService _expandoRowLocalService;

	@Reference
	private IndexerRegistry _indexerRegistry;

	@Reference
	private Language _language;

	@Reference
	private Localization _localization;

	@Reference
	private MimeTypes _mimeTypes;

	@Reference
	private MultiVMPool _multiVMPool;

	@Reference
	private Portal _portal;

	private PortalCache<String, Serializable> _portalCache;

	@Reference
	private PortletFileRepository _portletFileRepository;

	@Reference
	private RatingsStatsLocalService _ratingsStatsLocalService;

	@Reference
	private ResourceLocalService _resourceLocalService;

	private ServiceTrackerMap<String, WikiPageRenameContentProcessor>
		_serviceTrackerMap;

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
	private WikiEngineRenderer _wikiEngineRenderer;

	private WikiFileUploadConfiguration _wikiFileUploadConfiguration;

	@Reference
	private WikiNodePersistence _wikiNodePersistence;

	@Reference
	private WikiPageResourceLocalService _wikiPageResourceLocalService;

	@Reference
	private WikiPageResourcePersistence _wikiPageResourcePersistence;

	@Reference
	private WikiPageTitleValidator _wikiPageTitleValidator;

	@Reference
	private WorkflowInstanceLinkLocalService _workflowInstanceLinkLocalService;

}