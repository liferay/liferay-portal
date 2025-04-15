/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.service.impl;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.link.constants.AssetLinkConstants;
import com.liferay.asset.link.service.AssetLinkLocalService;
import com.liferay.blogs.configuration.BlogsFileUploadsConfiguration;
import com.liferay.blogs.configuration.BlogsGroupServiceConfiguration;
import com.liferay.blogs.constants.BlogsConstants;
import com.liferay.blogs.exception.EntryContentException;
import com.liferay.blogs.exception.EntryCoverImageCropException;
import com.liferay.blogs.exception.EntryDisplayDateException;
import com.liferay.blogs.exception.EntrySmallImageNameException;
import com.liferay.blogs.exception.EntrySmallImageScaleException;
import com.liferay.blogs.exception.EntryTitleException;
import com.liferay.blogs.exception.EntryUrlTitleException;
import com.liferay.blogs.internal.image.ImageSelectorProcessor;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.base.BlogsEntryLocalServiceBaseImpl;
import com.liferay.blogs.settings.BlogsGroupServiceSettings;
import com.liferay.blogs.social.BlogsActivityKeys;
import com.liferay.blogs.util.comparator.EntryDisplayDateComparator;
import com.liferay.blogs.util.comparator.EntryIdComparator;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.friendly.url.exception.DuplicateFriendlyURLEntryException;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.image.ImageMagick;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.comment.CommentManager;
import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.notifications.UserNotificationDefinition;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ImageLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;
import com.liferay.portal.kernel.social.SocialActivityManagerUtil;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FriendlyURLNormalizer;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.GroupSubscriptionCheckSubscriptionSender;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlParser;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.SubscriptionSender;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;
import com.liferay.portal.linkback.LinkbackProducerUtil;
import com.liferay.portal.util.PropsValues;
import com.liferay.ratings.kernel.service.RatingsStatsLocalService;
import com.liferay.social.kernel.model.SocialActivityConstants;
import com.liferay.subscription.service.SubscriptionLocalService;
import com.liferay.subscription.util.UnsubscribeHelper;
import com.liferay.trash.exception.RestoreEntryException;
import com.liferay.trash.exception.TrashEntryException;
import com.liferay.trash.model.TrashEntry;
import com.liferay.trash.service.TrashEntryLocalService;
import com.liferay.upload.UniqueFileNameProvider;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * Provides the local service for accessing, adding, checking, deleting,
 * subscription handling of, trash handling of, and updating blog entries.
 *
 * @author Brian Wing Shun Chan
 * @author Wilson S. Man
 * @author Raymond Augé
 * @author Thiago Moreira
 * @author Juan Fernández
 * @author Zsolt Berentey
 */
@Component(
	configurationPid = "com.liferay.blogs.configuration.BlogsFileUploadsConfiguration",
	property = "model.class.name=com.liferay.blogs.model.BlogsEntry",
	service = AopService.class
)
public class BlogsEntryLocalServiceImpl extends BlogsEntryLocalServiceBaseImpl {

	@Override
	public FileEntry addAttachmentFileEntry(
			String externalReferenceCode, long userId, long groupId,
			String fileName, String mimeType, InputStream inputStream)
		throws PortalException {

		Folder folder = addAttachmentsFolder(userId, groupId);

		return _portletFileRepository.addPortletFileEntry(
			externalReferenceCode, groupId, userId, null, 0,
			BlogsConstants.SERVICE_NAME, folder.getFolderId(), inputStream,
			_getUniqueFileName(groupId, fileName, folder.getFolderId()),
			mimeType, true);
	}

	@Override
	public Folder addAttachmentsFolder(long userId, long groupId)
		throws PortalException {

		return _addFolder(userId, groupId, BlogsConstants.SERVICE_NAME);
	}

	@Override
	public void addCoverImage(long entryId, ImageSelector imageSelector)
		throws PortalException {

		if (imageSelector == null) {
			return;
		}

		BlogsEntry entry = blogsEntryPersistence.findByPrimaryKey(entryId);

		String coverImageURL = StringPool.BLANK;
		long coverImageFileEntryId = 0;

		if (Validator.isNotNull(imageSelector.getImageURL())) {
			coverImageURL = imageSelector.getImageURL();
		}
		else if (imageSelector.getImageBytes() != null) {
			coverImageFileEntryId = _addCoverImageFileEntry(
				entry.getUserId(), entry.getGroupId(), entryId, imageSelector);
		}

		entry.setCoverImageFileEntryId(coverImageFileEntryId);
		entry.setCoverImageURL(coverImageURL);

		blogsEntryPersistence.update(entry);
	}

	@Override
	public BlogsEntry addEntry(
			long userId, String title, String content, Date displayDate,
			ServiceContext serviceContext)
		throws PortalException {

		return blogsEntryLocalService.addEntry(
			userId, title, StringPool.BLANK, StringPool.BLANK, content,
			displayDate, true, true, new String[0], StringPool.BLANK, null,
			null, serviceContext);
	}

	@Override
	public BlogsEntry addEntry(
			long userId, String title, String content,
			ServiceContext serviceContext)
		throws PortalException {

		return blogsEntryLocalService.addEntry(
			userId, title, StringPool.BLANK, StringPool.BLANK, content,
			new Date(), true, true, new String[0], StringPool.BLANK, null, null,
			serviceContext);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public BlogsEntry addEntry(
			long userId, String title, String subtitle, String description,
			String content, Date displayDate, boolean allowPingbacks,
			boolean allowTrackbacks, String[] trackbacks,
			String coverImageCaption, ImageSelector coverImageImageSelector,
			ImageSelector smallImageImageSelector,
			ServiceContext serviceContext)
		throws PortalException {

		return blogsEntryLocalService.addEntry(
			null, userId, title, subtitle, StringPool.BLANK, description,
			content, displayDate, allowPingbacks, allowTrackbacks, trackbacks,
			coverImageCaption, coverImageImageSelector, smallImageImageSelector,
			serviceContext);
	}

	@Override
	public BlogsEntry addEntry(
			long userId, String title, String subtitle, String description,
			String content, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute,
			boolean allowPingbacks, boolean allowTrackbacks,
			String[] trackbacks, String coverImageCaption,
			ImageSelector coverImageImageSelector,
			ImageSelector smallImageImageSelector,
			ServiceContext serviceContext)
		throws PortalException {

		return blogsEntryLocalService.addEntry(
			null, userId, title, subtitle, StringPool.BLANK, description,
			content, displayDateMonth, displayDateDay, displayDateYear,
			displayDateHour, displayDateMinute, allowPingbacks, allowTrackbacks,
			trackbacks, coverImageCaption, coverImageImageSelector,
			smallImageImageSelector, serviceContext);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public BlogsEntry addEntry(
			String externalReferenceCode, long userId, String title,
			String subtitle, String urlTitle, String description,
			String content, Date displayDate, boolean allowPingbacks,
			boolean allowTrackbacks, String[] trackbacks,
			String coverImageCaption, ImageSelector coverImageImageSelector,
			ImageSelector smallImageImageSelector,
			ServiceContext serviceContext)
		throws PortalException {

		// Entry

		User user = _userLocalService.getUser(userId);
		long groupId = serviceContext.getScopeGroupId();

		int status = WorkflowConstants.STATUS_DRAFT;

		_validate(title, urlTitle, content, status);

		long entryId = counterLocalService.increment();

		if (Validator.isNotNull(urlTitle)) {
			urlTitle = _validateURLTitle(groupId, urlTitle, serviceContext);
		}

		BlogsEntry entry = blogsEntryPersistence.create(entryId);

		entry.setUuid(serviceContext.getUuid());
		entry.setExternalReferenceCode(externalReferenceCode);
		entry.setGroupId(groupId);
		entry.setCompanyId(user.getCompanyId());
		entry.setUserId(user.getUserId());
		entry.setUserName(user.getFullName());
		entry.setTitle(title);
		entry.setSubtitle(subtitle);

		if (Validator.isNull(urlTitle)) {
			urlTitle = _getUniqueUrlTitle(entry);
		}

		if (!ExportImportThreadLocal.isImportInProcess()) {
			FriendlyURLEntry friendlyURLEntry =
				_friendlyURLEntryLocalService.addFriendlyURLEntry(
					groupId, BlogsEntry.class, entryId, urlTitle,
					serviceContext);

			urlTitle = friendlyURLEntry.getUrlTitle();
		}

		urlTitle = _sanitizeUrlTitle(urlTitle);

		entry.setUrlTitle(urlTitle);

		entry.setDescription(description);
		entry.setContent(content);
		entry.setDisplayDate(displayDate);
		entry.setAllowPingbacks(allowPingbacks);
		entry.setAllowTrackbacks(allowTrackbacks);
		entry.setStatus(status);
		entry.setStatusByUserId(userId);
		entry.setStatusDate(serviceContext.getModifiedDate(null));
		entry.setExpandoBridgeAttributes(serviceContext);

		entry = blogsEntryPersistence.update(entry);

		// Resources

		if (serviceContext.isAddGroupPermissions() ||
			serviceContext.isAddGuestPermissions()) {

			addEntryResources(
				entry, serviceContext.isAddGroupPermissions(),
				serviceContext.isAddGuestPermissions());
		}
		else {
			addEntryResources(entry, serviceContext.getModelPermissions());
		}

		// Asset

		updateAsset(
			userId, entry, serviceContext.getAssetCategoryIds(),
			serviceContext.getAssetTagNames(),
			serviceContext.getAssetLinkEntryIds(),
			serviceContext.getAssetPriority());

		// Comment

		BlogsGroupServiceConfiguration blogsGroupServiceConfiguration =
			_getBlogsGroupServiceConfiguration(entry.getGroupId());

		if (blogsGroupServiceConfiguration.
				subscribeBlogsEntryCreatorToComments()) {

			_commentManager.subscribeDiscussion(
				entry.getUserId(), entry.getGroupId(),
				BlogsEntry.class.getName(), entry.getEntryId());
		}

		// Images

		long coverImageFileEntryId = 0;
		String coverImageURL = null;

		if (coverImageImageSelector != null) {
			coverImageURL = coverImageImageSelector.getImageURL();

			if (coverImageImageSelector.getImageBytes() != null) {
				coverImageFileEntryId = _addCoverImageFileEntry(
					userId, groupId, entryId, coverImageImageSelector);
			}
		}

		long smallImageFileEntryId = 0;
		String smallImageURL = null;

		if (smallImageImageSelector != null) {
			smallImageURL = smallImageImageSelector.getImageURL();

			if (smallImageImageSelector.getImageBytes() != null) {
				smallImageFileEntryId = _addSmallImageFileEntry(
					userId, groupId, entryId, smallImageImageSelector);
			}
		}

		_validate(smallImageFileEntryId);

		entry.setCoverImageCaption(coverImageCaption);
		entry.setCoverImageFileEntryId(coverImageFileEntryId);
		entry.setCoverImageURL(coverImageURL);

		if ((smallImageFileEntryId != 0) ||
			Validator.isNotNull(smallImageURL)) {

			entry.setSmallImage(true);
		}

		entry.setSmallImageFileEntryId(smallImageFileEntryId);
		entry.setSmallImageURL(smallImageURL);

		entry = blogsEntryPersistence.update(entry);

		// Workflow

		if (ArrayUtil.isNotEmpty(trackbacks)) {
			serviceContext.setAttribute("trackbacks", trackbacks);
		}
		else {
			serviceContext.setAttribute("trackbacks", null);
		}

		return _startWorkflowInstance(userId, entry, serviceContext);
	}

	@Override
	public BlogsEntry addEntry(
			String externalReferenceCode, long userId, String title,
			String subtitle, String urlTitle, String description,
			String content, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute,
			boolean allowPingbacks, boolean allowTrackbacks,
			String[] trackbacks, String coverImageCaption,
			ImageSelector coverImageImageSelector,
			ImageSelector smallImageImageSelector,
			ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		Date displayDate = _portal.getDate(
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, user.getTimeZone(),
			EntryDisplayDateException.class);

		return blogsEntryLocalService.addEntry(
			externalReferenceCode, userId, title, subtitle, urlTitle,
			description, content, displayDate, allowPingbacks, allowTrackbacks,
			trackbacks, coverImageCaption, coverImageImageSelector,
			smallImageImageSelector, serviceContext);
	}

	@Override
	public void addEntryResources(
			BlogsEntry entry, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		_resourceLocalService.addResources(
			entry.getCompanyId(), entry.getGroupId(), entry.getUserId(),
			BlogsEntry.class.getName(), entry.getEntryId(), false,
			addGroupPermissions, addGuestPermissions);
	}

	@Override
	public void addEntryResources(
			BlogsEntry entry, ModelPermissions modelPermissions)
		throws PortalException {

		_resourceLocalService.addModelResources(
			entry.getCompanyId(), entry.getGroupId(), entry.getUserId(),
			BlogsEntry.class.getName(), entry.getEntryId(), modelPermissions);
	}

	@Override
	public void addEntryResources(
			long entryId, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		BlogsEntry entry = blogsEntryPersistence.findByPrimaryKey(entryId);

		addEntryResources(entry, addGroupPermissions, addGuestPermissions);
	}

	@Override
	public void addEntryResources(
			long entryId, ModelPermissions modelPermissions)
		throws PortalException {

		BlogsEntry entry = blogsEntryPersistence.findByPrimaryKey(entryId);

		addEntryResources(entry, modelPermissions);
	}

	@Override
	public long addOriginalImageFileEntry(
			long userId, long groupId, long entryId,
			ImageSelector imageSelector)
		throws PortalException {

		byte[] imageBytes = imageSelector.getImageBytes();

		if (imageBytes == null) {
			return 0;
		}

		Folder folder = addAttachmentsFolder(userId, groupId);

		FileEntry originalFileEntry =
			_portletFileRepository.addPortletFileEntry(
				groupId, userId, null, 0, BlogsConstants.SERVICE_NAME,
				folder.getFolderId(), imageBytes,
				_getUniqueFileName(
					groupId, imageSelector.getImageTitle(),
					folder.getFolderId()),
				imageSelector.getImageMimeType(), true);

		return originalFileEntry.getFileEntryId();
	}

	@Override
	public void addSmallImage(long entryId, ImageSelector imageSelector)
		throws PortalException {

		if (imageSelector == null) {
			return;
		}

		BlogsEntry entry = blogsEntryPersistence.findByPrimaryKey(entryId);

		boolean smallImage = false;
		long smallImageFileEntryId = 0;
		String smallImageURL = StringPool.BLANK;

		if (Validator.isNotNull(imageSelector.getImageURL())) {
			smallImage = true;

			smallImageURL = imageSelector.getImageURL();
		}
		else if (imageSelector.getImageBytes() != null) {
			smallImage = true;

			smallImageFileEntryId = _addSmallImageFileEntry(
				entry.getUserId(), entry.getGroupId(), entryId, imageSelector);
		}

		_validate(smallImageFileEntryId);

		entry.setSmallImage(smallImage);
		entry.setSmallImageFileEntryId(smallImageFileEntryId);
		entry.setSmallImageURL(smallImageURL);

		blogsEntryPersistence.update(entry);
	}

	@Override
	public void checkEntries() throws PortalException {
		Date date = new Date();

		int count = blogsEntryPersistence.countByLtD_S(
			date, WorkflowConstants.STATUS_SCHEDULED);

		if (count == 0) {
			return;
		}

		List<BlogsEntry> blogsEntries = blogsEntryPersistence.findByLtD_S(
			date, WorkflowConstants.STATUS_SCHEDULED);

		for (BlogsEntry blogsEntry : blogsEntries) {
			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setAttribute(
				_INVOKED_BY_CHECK_ENTRIES, Boolean.TRUE);

			String[] trackbacks = StringUtil.split(blogsEntry.getTrackbacks());

			serviceContext.setAttribute("trackbacks", trackbacks);

			serviceContext.setCommand(Constants.UPDATE);

			String portletId = PortletProviderUtil.getPortletId(
				BlogsEntry.class.getName(), PortletProvider.Action.VIEW);

			if (Validator.isNotNull(portletId)) {
				serviceContext.setLayoutFullURL(
					_portal.getLayoutFullURL(
						blogsEntry.getGroupId(), portletId));
			}

			serviceContext.setScopeGroupId(blogsEntry.getGroupId());

			blogsEntryLocalService.updateStatus(
				blogsEntry.getStatusByUserId(), blogsEntry.getEntryId(),
				WorkflowConstants.STATUS_APPROVED, serviceContext,
				new HashMap<>());
		}
	}

	@Override
	public void deleteAttachmentFileEntry(long fileEntryId)
		throws PortalException {

		FileEntry fileEntry = _portletFileRepository.getPortletFileEntry(
			fileEntryId);

		_validateAttachmentFileEntry(fileEntry);

		_portletFileRepository.deletePortletFileEntry(
			fileEntry.getFileEntryId());
	}

	@Override
	public BlogsEntry deleteBlogsEntry(BlogsEntry blogsEntry) {
		try {
			return blogsEntryLocalService.deleteEntry(blogsEntry);
		}
		catch (PortalException portalException) {
			return ReflectionUtil.throwException(portalException);
		}
	}

	@Override
	public void deleteEntries(long groupId) throws PortalException {
		for (BlogsEntry entry : blogsEntryPersistence.findByGroupId(groupId)) {
			blogsEntryLocalService.deleteEntry(entry);
		}
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public BlogsEntry deleteEntry(BlogsEntry entry) throws PortalException {

		// Order is important. See LPS-81826.

		// Ratings

		_ratingsStatsLocalService.deleteStats(
			BlogsEntry.class.getName(), entry.getEntryId());

		// Entry

		blogsEntryPersistence.remove(entry);

		// Resources

		_resourceLocalService.deleteResource(
			entry.getCompanyId(), BlogsEntry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL, entry.getEntryId());

		// Image

		_imageLocalService.deleteImage(entry.getSmallImageId());

		// Subscriptions

		_subscriptionLocalService.deleteSubscriptions(
			entry.getCompanyId(), BlogsEntry.class.getName(),
			entry.getEntryId());

		// Asset

		_assetEntryLocalService.deleteEntry(
			BlogsEntry.class.getName(), entry.getEntryId());

		// Attachments

		long coverImageFileEntryId = entry.getCoverImageFileEntryId();

		if (coverImageFileEntryId != 0) {
			_portletFileRepository.deletePortletFileEntry(
				coverImageFileEntryId);
		}

		long smallImageFileEntryId = entry.getSmallImageFileEntryId();

		if (smallImageFileEntryId != 0) {
			_portletFileRepository.deletePortletFileEntry(
				smallImageFileEntryId);
		}

		// Comment

		_deleteDiscussion(entry);

		// Expando

		_expandoRowLocalService.deleteRows(
			entry.getCompanyId(),
			_classNameLocalService.getClassNameId(BlogsEntry.class.getName()),
			entry.getEntryId());

		// Friendly URL

		_friendlyURLEntryLocalService.deleteFriendlyURLEntry(
			entry.getGroupId(), BlogsEntry.class, entry.getEntryId());

		// Trash

		_trashEntryLocalService.deleteEntry(
			BlogsEntry.class.getName(), entry.getEntryId());

		// Workflow

		_workflowInstanceLinkLocalService.deleteWorkflowInstanceLinks(
			entry.getCompanyId(), entry.getGroupId(),
			BlogsEntry.class.getName(), entry.getEntryId());

		return entry;
	}

	@Override
	public void deleteEntry(long entryId) throws PortalException {
		BlogsEntry entry = blogsEntryPersistence.findByPrimaryKey(entryId);

		blogsEntryLocalService.deleteEntry(entry);
	}

	@Override
	public Folder fetchAttachmentsFolder(long userId, long groupId) {
		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);

		Repository repository = _portletFileRepository.fetchPortletRepository(
			groupId, BlogsConstants.SERVICE_NAME);

		try {
			return _portletFileRepository.getPortletFolder(
				repository.getRepositoryId(),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				BlogsConstants.SERVICE_NAME);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	@Override
	public BlogsEntry fetchEntry(long groupId, String urlTitle) {
		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.fetchFriendlyURLEntry(
				groupId, BlogsEntry.class, urlTitle);

		if (friendlyURLEntry != null) {
			return blogsEntryPersistence.fetchByPrimaryKey(
				friendlyURLEntry.getClassPK());
		}

		return blogsEntryPersistence.fetchByG_UT(groupId, urlTitle);
	}

	@Override
	public FileEntry getAttachmentFileEntry(long fileEntryId)
		throws PortalException {

		FileEntry fileEntry = _portletFileRepository.getPortletFileEntry(
			fileEntryId);

		_validateAttachmentFileEntry(fileEntry);

		return fileEntry;
	}

	@Override
	public FileEntry getAttachmentFileEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		FileEntry fileEntry =
			_portletFileRepository.getPortletFileEntryByExternalReferenceCode(
				externalReferenceCode, groupId);

		_validateAttachmentFileEntry(fileEntry);

		return fileEntry;
	}

	@Override
	public List<BlogsEntry> getCompanyEntries(
		long companyId, Date displayDate,
		QueryDefinition<BlogsEntry> queryDefinition) {

		if (queryDefinition.isExcludeStatus()) {
			return blogsEntryPersistence.findByC_LtD_NotS(
				companyId, displayDate, queryDefinition.getStatus(),
				queryDefinition.getStart(), queryDefinition.getEnd(),
				queryDefinition.getOrderByComparator());
		}

		return blogsEntryPersistence.findByC_LtD_S(
			companyId, displayDate, queryDefinition.getStatus(),
			queryDefinition.getStart(), queryDefinition.getEnd(),
			queryDefinition.getOrderByComparator());
	}

	@Override
	public int getCompanyEntriesCount(
		long companyId, Date displayDate,
		QueryDefinition<BlogsEntry> queryDefinition) {

		if (queryDefinition.isExcludeStatus()) {
			return blogsEntryPersistence.countByC_LtD_NotS(
				companyId, displayDate, queryDefinition.getStatus());
		}

		return blogsEntryPersistence.countByC_LtD_S(
			companyId, displayDate, queryDefinition.getStatus());
	}

	@Override
	public BlogsEntry[] getEntriesPrevAndNext(long entryId)
		throws PortalException {

		BlogsEntry entry = blogsEntryPersistence.findByPrimaryKey(entryId);

		BlogsEntry[] entries = blogsEntryPersistence.findByG_D_S_PrevAndNext(
			entryId, entry.getGroupId(), entry.getDisplayDate(),
			WorkflowConstants.STATUS_APPROVED,
			EntryIdComparator.getInstance(true));

		if (entries[0] == null) {
			entries[0] = blogsEntryPersistence.fetchByG_LtD_S_Last(
				entry.getGroupId(), entry.getDisplayDate(),
				WorkflowConstants.STATUS_APPROVED,
				EntryDisplayDateComparator.getInstance(true));
		}

		if (entries[2] == null) {
			entries[2] = blogsEntryPersistence.fetchByG_GtD_S_First(
				entry.getGroupId(), entry.getDisplayDate(),
				WorkflowConstants.STATUS_APPROVED,
				EntryDisplayDateComparator.getInstance(true));
		}

		return entries;
	}

	@Override
	public BlogsEntry getEntry(long entryId) throws PortalException {
		return blogsEntryPersistence.findByPrimaryKey(entryId);
	}

	@Override
	public BlogsEntry getEntry(long groupId, String urlTitle)
		throws PortalException {

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.fetchFriendlyURLEntry(
				groupId, BlogsEntry.class, urlTitle);

		if (friendlyURLEntry != null) {
			return blogsEntryPersistence.findByPrimaryKey(
				friendlyURLEntry.getClassPK());
		}

		return blogsEntryPersistence.findByG_UT(groupId, urlTitle);
	}

	@Override
	public List<BlogsEntry> getGroupEntries(
		long groupId, Date displayDate,
		QueryDefinition<BlogsEntry> queryDefinition) {

		if (queryDefinition.isExcludeStatus()) {
			return blogsEntryPersistence.findByG_LtD_NotS(
				groupId, displayDate, queryDefinition.getStatus(),
				queryDefinition.getStart(), queryDefinition.getEnd(),
				queryDefinition.getOrderByComparator());
		}

		return blogsEntryPersistence.findByG_LtD_S(
			groupId, displayDate, queryDefinition.getStatus(),
			queryDefinition.getStart(), queryDefinition.getEnd(),
			queryDefinition.getOrderByComparator());
	}

	@Override
	public List<BlogsEntry> getGroupEntries(
		long groupId, QueryDefinition<BlogsEntry> queryDefinition) {

		if (queryDefinition.isExcludeStatus()) {
			return blogsEntryPersistence.findByG_NotS(
				groupId, queryDefinition.getStatus(),
				queryDefinition.getStart(), queryDefinition.getEnd(),
				queryDefinition.getOrderByComparator());
		}

		return blogsEntryPersistence.findByG_S(
			groupId, queryDefinition.getStatus(), queryDefinition.getStart(),
			queryDefinition.getEnd(), queryDefinition.getOrderByComparator());
	}

	@Override
	public int getGroupEntriesCount(
		long groupId, Date displayDate,
		QueryDefinition<BlogsEntry> queryDefinition) {

		if (queryDefinition.isExcludeStatus()) {
			return blogsEntryPersistence.countByG_LtD_NotS(
				groupId, displayDate, queryDefinition.getStatus());
		}

		return blogsEntryPersistence.countByG_LtD_S(
			groupId, displayDate, queryDefinition.getStatus());
	}

	@Override
	public int getGroupEntriesCount(
		long groupId, QueryDefinition<BlogsEntry> queryDefinition) {

		if (queryDefinition.isExcludeStatus()) {
			return blogsEntryPersistence.countByG_NotS(
				groupId, queryDefinition.getStatus());
		}

		return blogsEntryPersistence.countByG_S(
			groupId, queryDefinition.getStatus());
	}

	@Override
	public List<BlogsEntry> getGroupsEntries(
		long companyId, long groupId, Date displayDate,
		QueryDefinition<BlogsEntry> queryDefinition) {

		return blogsEntryFinder.findByGroupIds(
			companyId, groupId, displayDate, queryDefinition);
	}

	@Override
	public List<BlogsEntry> getGroupUserEntries(
		long groupId, long userId, Date displayDate,
		QueryDefinition<BlogsEntry> queryDefinition) {

		if (queryDefinition.isExcludeStatus()) {
			return blogsEntryPersistence.findByG_U_NotS(
				groupId, userId, queryDefinition.getStatus(),
				queryDefinition.getStart(), queryDefinition.getEnd(),
				queryDefinition.getOrderByComparator());
		}

		return blogsEntryPersistence.findByG_U_S(
			groupId, userId, queryDefinition.getStatus(),
			queryDefinition.getStart(), queryDefinition.getEnd(),
			queryDefinition.getOrderByComparator());
	}

	@Override
	public int getGroupUserEntriesCount(
		long groupId, long userId, Date displayDate,
		QueryDefinition<BlogsEntry> queryDefinition) {

		if (queryDefinition.isExcludeStatus()) {
			return blogsEntryPersistence.countByG_U_LtD_NotS(
				groupId, userId, displayDate, queryDefinition.getStatus());
		}

		return blogsEntryPersistence.countByG_U_LtD_S(
			groupId, userId, displayDate, queryDefinition.getStatus());
	}

	@Override
	public List<BlogsEntry> getOrganizationEntries(
		long organizationId, Date displayDate,
		QueryDefinition<BlogsEntry> queryDefinition) {

		return blogsEntryFinder.findByOrganizationId(
			organizationId, displayDate, queryDefinition);
	}

	@Override
	public int getOrganizationEntriesCount(
		long organizationId, Date displayDate,
		QueryDefinition<BlogsEntry> queryDefinition) {

		return blogsEntryFinder.countByOrganizationId(
			organizationId, displayDate, queryDefinition);
	}

	@Override
	public String getUniqueUrlTitle(BlogsEntry entry) {
		return _getUniqueUrlTitle(entry);
	}

	@Override
	public void moveEntriesToTrash(long groupId, long userId)
		throws PortalException {

		List<BlogsEntry> blogsEntries = blogsEntryPersistence.findByGroupId(
			groupId);

		for (BlogsEntry blogsEntry : blogsEntries) {
			blogsEntryLocalService.moveEntryToTrash(userId, blogsEntry);
		}
	}

	/**
	 * Moves the blogs entry to the recycle bin. Social activity counters for
	 * this entry get disabled.
	 *
	 * @param  userId the primary key of the user moving the blogs entry
	 * @param  entry the blogs entry to be moved
	 * @return the moved blogs entry
	 */
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public BlogsEntry moveEntryToTrash(long userId, BlogsEntry entry)
		throws PortalException {

		// Entry

		if (entry.isInTrash()) {
			throw new TrashEntryException();
		}

		int oldStatus = entry.getStatus();

		if (oldStatus == WorkflowConstants.STATUS_PENDING) {
			entry.setStatus(WorkflowConstants.STATUS_DRAFT);

			entry = blogsEntryPersistence.update(entry);
		}

		entry = updateStatus(
			userId, entry.getEntryId(), WorkflowConstants.STATUS_IN_TRASH,
			new ServiceContext(), new HashMap<>());

		// Social

		JSONObject extraDataJSONObject = JSONUtil.put(
			"title", entry.getTitle());

		SocialActivityManagerUtil.addActivity(
			userId, entry, SocialActivityConstants.TYPE_MOVE_TO_TRASH,
			extraDataJSONObject.toString(), 0);

		// Workflow

		if (oldStatus == WorkflowConstants.STATUS_PENDING) {
			_workflowInstanceLinkLocalService.deleteWorkflowInstanceLink(
				entry.getCompanyId(), entry.getGroupId(),
				BlogsEntry.class.getName(), entry.getEntryId());
		}

		return entry;
	}

	/**
	 * Moves the blogs entry with the ID to the recycle bin.
	 *
	 * @param  userId the primary key of the user moving the blogs entry
	 * @param  entryId the primary key of the blogs entry to be moved
	 * @return the moved blogs entry
	 */
	@Override
	public BlogsEntry moveEntryToTrash(long userId, long entryId)
		throws PortalException {

		BlogsEntry entry = blogsEntryPersistence.findByPrimaryKey(entryId);

		return blogsEntryLocalService.moveEntryToTrash(userId, entry);
	}

	/**
	 * Restores the blogs entry with the ID from the recycle bin. Social
	 * activity counters for this entry get activated.
	 *
	 * @param  userId the primary key of the user restoring the blogs entry
	 * @param  entryId the primary key of the blogs entry to be restored
	 * @return the restored blogs entry from the recycle bin
	 */
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public BlogsEntry restoreEntryFromTrash(long userId, long entryId)
		throws PortalException {

		// Entry

		BlogsEntry entry = blogsEntryPersistence.findByPrimaryKey(entryId);

		if (!entry.isInTrash()) {
			throw new RestoreEntryException(
				RestoreEntryException.INVALID_STATUS);
		}

		TrashEntry trashEntry = _trashEntryLocalService.getEntry(
			BlogsEntry.class.getName(), entryId);

		entry = updateStatus(
			userId, entryId, trashEntry.getStatus(), new ServiceContext(),
			new HashMap<>());

		// Social

		JSONObject extraDataJSONObject = JSONUtil.put(
			"title", entry.getTitle());

		SocialActivityManagerUtil.addActivity(
			userId, entry, SocialActivityConstants.TYPE_RESTORE_FROM_TRASH,
			extraDataJSONObject.toString(), 0);

		return entry;
	}

	@Override
	public void subscribe(long userId, long groupId) throws PortalException {
		_subscriptionLocalService.addSubscription(
			userId, groupId, BlogsEntry.class.getName(), groupId);
	}

	@Override
	public void unsubscribe(long userId, long groupId) throws PortalException {
		_subscriptionLocalService.deleteSubscription(
			userId, BlogsEntry.class.getName(), groupId);
	}

	@Override
	public void updateAsset(
			long userId, BlogsEntry entry, long[] assetCategoryIds,
			String[] assetTagNames, long[] assetLinkEntryIds, Double priority)
		throws PortalException {

		boolean visible = false;

		if (entry.isApproved()) {
			visible = true;
		}

		String summary = StringUtil.shorten(
			_htmlParser.extractText(entry.getContent()), 500);

		AssetEntry assetEntry = _assetEntryLocalService.updateEntry(
			userId, entry.getGroupId(), entry.getCreateDate(),
			entry.getModifiedDate(), BlogsEntry.class.getName(),
			entry.getEntryId(), entry.getUuid(), 0, assetCategoryIds,
			assetTagNames, true, visible, null, null, null, null,
			ContentTypes.TEXT_HTML, entry.getTitle(), entry.getDescription(),
			summary, null, null, 0, 0, priority);

		_assetLinkLocalService.updateLinks(
			userId, assetEntry.getEntryId(), assetLinkEntryIds,
			AssetLinkConstants.TYPE_RELATED);
	}

	@Override
	public BlogsEntry updateEntry(
			long userId, long entryId, String title, String content,
			ServiceContext serviceContext)
		throws PortalException {

		BlogsEntry entry = blogsEntryPersistence.findByPrimaryKey(entryId);

		return blogsEntryLocalService.updateEntry(
			userId, entryId, title, entry.getSubtitle(), entry.getDescription(),
			content, entry.getDisplayDate(), entry.isAllowPingbacks(),
			entry.isAllowTrackbacks(), StringUtil.split(entry.getTrackbacks()),
			StringPool.BLANK, null, null, serviceContext);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public BlogsEntry updateEntry(
			long userId, long entryId, String title, String subtitle,
			String description, String content, Date displayDate,
			boolean allowPingbacks, boolean allowTrackbacks,
			String[] trackbacks, String coverImageCaption,
			ImageSelector coverImageImageSelector,
			ImageSelector smallImageImageSelector,
			ServiceContext serviceContext)
		throws PortalException {

		return blogsEntryLocalService.updateEntry(
			userId, entryId, title, subtitle, _getURLTitle(entryId),
			description, content, displayDate, allowPingbacks, allowTrackbacks,
			trackbacks, coverImageCaption, coverImageImageSelector,
			smallImageImageSelector, serviceContext);
	}

	@Override
	public BlogsEntry updateEntry(
			long userId, long entryId, String title, String subtitle,
			String description, String content, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, boolean allowPingbacks,
			boolean allowTrackbacks, String[] trackbacks,
			String coverImageCaption, ImageSelector coverImageImageSelector,
			ImageSelector smallImageImageSelector,
			ServiceContext serviceContext)
		throws PortalException {

		return blogsEntryLocalService.updateEntry(
			userId, entryId, title, subtitle, _getURLTitle(entryId),
			description, content, displayDateMonth, displayDateDay,
			displayDateYear, displayDateHour, displayDateMinute, allowPingbacks,
			allowTrackbacks, trackbacks, coverImageCaption,
			coverImageImageSelector, smallImageImageSelector, serviceContext);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public BlogsEntry updateEntry(
			long userId, long entryId, String title, String subtitle,
			String urlTitle, String description, String content,
			Date displayDate, boolean allowPingbacks, boolean allowTrackbacks,
			String[] trackbacks, String coverImageCaption,
			ImageSelector coverImageImageSelector,
			ImageSelector smallImageImageSelector,
			ServiceContext serviceContext)
		throws PortalException {

		// Entry

		BlogsEntry entry = blogsEntryPersistence.findByPrimaryKey(entryId);

		int status = entry.getStatus();

		if (!entry.isPending() && !entry.isDraft()) {
			status = WorkflowConstants.STATUS_DRAFT;
		}

		_validate(title, urlTitle, content, status);

		if (Validator.isNotNull(urlTitle) &&
			!urlTitle.equals(entry.getUrlTitle()) &&
			!ExportImportThreadLocal.isImportInProcess()) {

			urlTitle = _validateURLTitle(
				entry.getGroupId(), urlTitle, serviceContext);
		}

		if (Validator.isNull(urlTitle)) {
			urlTitle = _getUniqueUrlTitle(entry, title);
		}

		urlTitle = _sanitizeUrlTitle(urlTitle);

		String oldUrlTitle = entry.getUrlTitle();

		entry.setTitle(title);
		entry.setSubtitle(subtitle);

		if (Validator.isNotNull(urlTitle) &&
			(!urlTitle.equals(entry.getUrlTitle()) ||
			 _isUpdatedAssetCategories(entry, serviceContext)) &&
			!ExportImportThreadLocal.isImportInProcess()) {

			FriendlyURLEntry friendlyURLEntry =
				_friendlyURLEntryLocalService.addFriendlyURLEntry(
					entry.getGroupId(), BlogsEntry.class, entry.getEntryId(),
					urlTitle, serviceContext);

			entry.setUrlTitle(friendlyURLEntry.getUrlTitle());
		}

		entry.setDescription(description);
		entry.setContent(content);
		entry.setDisplayDate(displayDate);
		entry.setAllowPingbacks(allowPingbacks);
		entry.setAllowTrackbacks(allowTrackbacks);
		entry.setStatus(status);
		entry.setExpandoBridgeAttributes(serviceContext);

		entry = blogsEntryPersistence.update(entry);

		// Asset

		updateAsset(
			userId, entry, serviceContext.getAssetCategoryIds(),
			serviceContext.getAssetTagNames(),
			serviceContext.getAssetLinkEntryIds(),
			serviceContext.getAssetPriority());

		// Images

		long coverImageFileEntryId = entry.getCoverImageFileEntryId();
		String coverImageURL = entry.getCoverImageURL();

		long deletePreviousCoverImageFileEntryId = 0;

		if (coverImageImageSelector != null) {
			coverImageURL = coverImageImageSelector.getImageURL();

			if (coverImageImageSelector.getImageBytes() != null) {
				coverImageFileEntryId = _addCoverImageFileEntry(
					userId, entry.getGroupId(), entryId,
					coverImageImageSelector);
			}
			else {
				coverImageFileEntryId = 0;
			}

			deletePreviousCoverImageFileEntryId =
				entry.getCoverImageFileEntryId();
		}

		long smallImageFileEntryId = entry.getSmallImageFileEntryId();
		String smallImageURL = entry.getSmallImageURL();

		long deletePreviousSmallImageFileEntryId = 0;
		long deletePreviousSmallImageId = 0;

		if (smallImageImageSelector != null) {
			smallImageURL = smallImageImageSelector.getImageURL();

			if (smallImageImageSelector.getImageBytes() != null) {
				smallImageFileEntryId = _addSmallImageFileEntry(
					userId, entry.getGroupId(), entryId,
					smallImageImageSelector);
			}
			else {
				smallImageFileEntryId = 0;
			}

			deletePreviousSmallImageFileEntryId =
				entry.getSmallImageFileEntryId();

			deletePreviousSmallImageId = entry.getSmallImageId();
		}

		_validate(smallImageFileEntryId);

		entry.setCoverImageCaption(coverImageCaption);
		entry.setCoverImageFileEntryId(coverImageFileEntryId);
		entry.setCoverImageURL(coverImageURL);

		if ((smallImageFileEntryId != 0) ||
			Validator.isNotNull(smallImageURL)) {

			entry.setSmallImage(true);
		}
		else {
			entry.setSmallImage(false);
		}

		entry.setSmallImageFileEntryId(smallImageFileEntryId);
		entry.setSmallImageURL(smallImageURL);

		entry = blogsEntryPersistence.update(entry);

		// Workflow

		boolean pingOldTrackbacks = false;

		if (!oldUrlTitle.equals(entry.getUrlTitle())) {
			pingOldTrackbacks = true;
		}

		serviceContext.setAttribute(
			"pingOldTrackbacks", String.valueOf(pingOldTrackbacks));

		if (ArrayUtil.isNotEmpty(trackbacks)) {
			serviceContext.setAttribute("trackbacks", trackbacks);
		}
		else {
			serviceContext.setAttribute("trackbacks", null);
		}

		entry = _startWorkflowInstance(userId, entry, serviceContext);

		if (deletePreviousCoverImageFileEntryId != 0) {
			_portletFileRepository.deletePortletFileEntry(
				deletePreviousCoverImageFileEntryId);
		}

		if (deletePreviousSmallImageFileEntryId != 0) {
			_portletFileRepository.deletePortletFileEntry(
				deletePreviousSmallImageFileEntryId);
		}

		if (deletePreviousSmallImageId != 0) {
			_imageLocalService.deleteImage(deletePreviousSmallImageId);
		}

		return entry;
	}

	@Override
	public BlogsEntry updateEntry(
			long userId, long entryId, String title, String subtitle,
			String urlTitle, String description, String content,
			int displayDateMonth, int displayDateDay, int displayDateYear,
			int displayDateHour, int displayDateMinute, boolean allowPingbacks,
			boolean allowTrackbacks, String[] trackbacks,
			String coverImageCaption, ImageSelector coverImageImageSelector,
			ImageSelector smallImageImageSelector,
			ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		Date displayDate = _portal.getDate(
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, user.getTimeZone(),
			EntryDisplayDateException.class);

		return blogsEntryLocalService.updateEntry(
			userId, entryId, title, subtitle, urlTitle, description, content,
			displayDate, allowPingbacks, allowTrackbacks, trackbacks,
			coverImageCaption, coverImageImageSelector, smallImageImageSelector,
			serviceContext);
	}

	@Override
	public void updateEntryResources(
			BlogsEntry entry, ModelPermissions modelPermissions)
		throws PortalException {

		_resourceLocalService.updateResources(
			entry.getCompanyId(), entry.getGroupId(),
			BlogsEntry.class.getName(), entry.getEntryId(), modelPermissions);
	}

	@Override
	public void updateEntryResources(
			BlogsEntry entry, String[] groupPermissions,
			String[] guestPermissions)
		throws PortalException {

		_resourceLocalService.updateResources(
			entry.getCompanyId(), entry.getGroupId(),
			BlogsEntry.class.getName(), entry.getEntryId(), groupPermissions,
			guestPermissions);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public BlogsEntry updateStatus(
			long userId, long entryId, int status,
			ServiceContext serviceContext,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		// Entry

		User user = _userLocalService.getUser(userId);
		Date date = new Date();

		BlogsEntry entry = blogsEntryPersistence.findByPrimaryKey(entryId);

		_validate(
			entry.getTitle(), entry.getUrlTitle(), entry.getContent(), status);

		int oldStatus = entry.getStatus();

		if ((status == WorkflowConstants.STATUS_APPROVED) &&
			date.before(entry.getDisplayDate())) {

			status = WorkflowConstants.STATUS_SCHEDULED;
		}

		entry.setStatus(status);
		entry.setStatusByUserId(user.getUserId());
		entry.setStatusByUserName(user.getFullName());
		entry.setStatusDate(serviceContext.getModifiedDate(date));

		if ((status == WorkflowConstants.STATUS_APPROVED) &&
			Validator.isNull(entry.getUrlTitle())) {

			String uniqueUrlTitle = _getUniqueUrlTitle(entry);

			FriendlyURLEntry friendlyURLEntry =
				_friendlyURLEntryLocalService.addFriendlyURLEntry(
					entry.getGroupId(), BlogsEntry.class, entry.getEntryId(),
					uniqueUrlTitle, serviceContext);

			entry.setUrlTitle(friendlyURLEntry.getUrlTitle());
		}

		entry = blogsEntryPersistence.update(entry);

		// Statistics

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			BlogsEntry.class.getName(), entryId);

		if ((assetEntry == null) || (assetEntry.getPublishDate() == null)) {
			serviceContext.setCommand(Constants.ADD);
		}

		JSONObject extraDataJSONObject = JSONUtil.put(
			"title", entry.getTitle());

		if (status == WorkflowConstants.STATUS_APPROVED) {

			// Resources

			if ((oldStatus == WorkflowConstants.STATUS_DRAFT) &&
				GetterUtil.getBoolean(
					serviceContext.getAttribute("addEntryResources"))) {

				if (serviceContext.isAddGroupPermissions() ||
					serviceContext.isAddGuestPermissions()) {

					addEntryResources(
						entry, serviceContext.isAddGroupPermissions(),
						serviceContext.isAddGuestPermissions());
				}
				else {
					addEntryResources(
						entry, serviceContext.getModelPermissions());
				}
			}

			// Asset

			_assetEntryLocalService.updateEntry(
				BlogsEntry.class.getName(), entryId, entry.getDisplayDate(),
				null, true, true);

			// Social

			if ((oldStatus != WorkflowConstants.STATUS_IN_TRASH) &&
				(oldStatus != WorkflowConstants.STATUS_SCHEDULED)) {

				if (serviceContext.isCommandUpdate()) {
					SocialActivityManagerUtil.addActivity(
						user.getUserId(), entry, BlogsActivityKeys.UPDATE_ENTRY,
						extraDataJSONObject.toString(), 0);
				}
				else {
					SocialActivityManagerUtil.addUniqueActivity(
						user.getUserId(), entry, BlogsActivityKeys.ADD_ENTRY,
						extraDataJSONObject.toString(), 0);
				}
			}

			// Trash

			if (oldStatus == WorkflowConstants.STATUS_IN_TRASH) {
				if (PropsValues.BLOGS_ENTRY_COMMENTS_ENABLED) {
					_commentManager.restoreDiscussionFromTrash(
						BlogsEntry.class.getName(), entryId);
				}

				_trashEntryLocalService.deleteEntry(
					BlogsEntry.class.getName(), entryId);
			}

			if (oldStatus != WorkflowConstants.STATUS_IN_TRASH) {

				// Subscriptions

				_notifySubscribers(
					userId, entry, serviceContext, workflowContext);

				// Ping

				String[] trackbacks = (String[])serviceContext.getAttribute(
					"trackbacks");
				Boolean pingOldTrackbacks = ParamUtil.getBoolean(
					serviceContext, "pingOldTrackbacks");

				_pingGoogle(entry, serviceContext);
				_pingPingback(entry, serviceContext);
				_pingTrackbacks(
					entry, trackbacks, pingOldTrackbacks, serviceContext);
			}
		}
		else {

			// Asset

			_assetEntryLocalService.updateVisible(
				BlogsEntry.class.getName(), entryId, false);

			// Social

			if ((status == WorkflowConstants.STATUS_SCHEDULED) &&
				(oldStatus != WorkflowConstants.STATUS_IN_TRASH)) {

				if (serviceContext.isCommandUpdate()) {
					SocialActivityManagerUtil.addActivity(
						user.getUserId(), entry, BlogsActivityKeys.UPDATE_ENTRY,
						extraDataJSONObject.toString(), 0);
				}
				else {
					SocialActivityManagerUtil.addUniqueActivity(
						user.getUserId(), entry, BlogsActivityKeys.ADD_ENTRY,
						extraDataJSONObject.toString(), 0);
				}
			}

			// Trash

			if (status == WorkflowConstants.STATUS_IN_TRASH) {
				if (PropsValues.BLOGS_ENTRY_COMMENTS_ENABLED) {
					_commentManager.moveDiscussionToTrash(
						BlogsEntry.class.getName(), entryId);
				}

				_trashEntryLocalService.addTrashEntry(
					userId, entry.getGroupId(), BlogsEntry.class.getName(),
					entry.getEntryId(), entry.getUuid(), null, oldStatus, null,
					null);
			}
			else if (oldStatus == WorkflowConstants.STATUS_IN_TRASH) {
				if (PropsValues.BLOGS_ENTRY_COMMENTS_ENABLED) {
					_commentManager.restoreDiscussionFromTrash(
						BlogsEntry.class.getName(), entryId);
				}

				_trashEntryLocalService.deleteEntry(
					BlogsEntry.class.getName(), entryId);
			}
		}

		return entry;
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_blogsFileUploadsConfiguration = ConfigurableUtil.createConfigurable(
			BlogsFileUploadsConfiguration.class, properties);
	}

	private long _addCoverImageFileEntry(
			long userId, long groupId, long entryId,
			ImageSelector imageSelector)
		throws PortalException {

		byte[] imageBytes = imageSelector.getImageBytes();

		if (imageBytes == null) {
			return 0;
		}

		try {
			ImageSelectorProcessor imageSelectorProcessor =
				new ImageSelectorProcessor(
					imageSelector.getImageBytes(), _imageMagick);

			imageBytes = imageSelectorProcessor.cropImage(
				imageSelector.getImageCropRegion());

			if (imageBytes == null) {
				throw new EntryCoverImageCropException();
			}

			Folder folder = _addCoverImageFolder(userId, groupId);

			return _addProcessedImageFileEntry(
				userId, groupId, entryId, folder.getFolderId(),
				imageSelector.getImageTitle(), imageSelector.getImageMimeType(),
				imageBytes);
		}
		catch (IOException ioException) {
			throw new EntryCoverImageCropException(ioException);
		}
	}

	private Folder _addCoverImageFolder(long userId, long groupId)
		throws PortalException {

		return _addFolder(userId, groupId, _COVER_IMAGE_FOLDER_NAME);
	}

	private Folder _addFolder(long userId, long groupId, String folderName)
		throws PortalException {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);

		Repository repository = _portletFileRepository.addPortletRepository(
			groupId, BlogsConstants.SERVICE_NAME, serviceContext);

		return _portletFileRepository.addPortletFolder(
			userId, repository.getRepositoryId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, folderName,
			serviceContext);
	}

	private long _addProcessedImageFileEntry(
			long userId, long groupId, long entryId, long folderId,
			String title, String mimeType, byte[] bytes)
		throws PortalException {

		if (Validator.isNull(title)) {
			title = StringUtil.randomString() + "_processedImage_" + entryId;
		}

		FileEntry processedImageFileEntry =
			_portletFileRepository.addPortletFileEntry(
				groupId, userId, BlogsEntry.class.getName(), entryId,
				BlogsConstants.SERVICE_NAME, folderId, bytes,
				_getUniqueFileName(groupId, title, folderId), mimeType, true);

		return processedImageFileEntry.getFileEntryId();
	}

	private long _addSmallImageFileEntry(
			long userId, long groupId, long entryId,
			ImageSelector imageSelector)
		throws PortalException {

		byte[] imageBytes = imageSelector.getImageBytes();

		if (imageBytes == null) {
			return 0;
		}

		try {
			BlogsGroupServiceSettings blogsGroupServiceSettings =
				BlogsGroupServiceSettings.getInstance(groupId);

			ImageSelectorProcessor imageSelectorProcessor =
				new ImageSelectorProcessor(
					imageSelector.getImageBytes(), _imageMagick);

			imageBytes = imageSelectorProcessor.scaleImage(
				blogsGroupServiceSettings.getSmallImageWidth());

			if (imageBytes == null) {
				throw new EntrySmallImageScaleException();
			}

			Folder folder = _addSmallImageFolder(userId, groupId);

			return _addProcessedImageFileEntry(
				userId, groupId, entryId, folder.getFolderId(),
				imageSelector.getImageTitle(), imageSelector.getImageMimeType(),
				imageBytes);
		}
		catch (Exception exception) {
			throw new EntrySmallImageScaleException(exception);
		}
	}

	private Folder _addSmallImageFolder(long userId, long groupId)
		throws PortalException {

		return _addFolder(userId, groupId, _SMALL_IMAGE_FOLDER_NAME);
	}

	private void _deleteDiscussion(BlogsEntry entry) throws PortalException {
		_commentManager.deleteDiscussion(
			BlogsEntry.class.getName(), entry.getEntryId());
	}

	private BlogsGroupServiceConfiguration _getBlogsGroupServiceConfiguration(
			long groupId)
		throws PortalException {

		return _configurationProvider.getConfiguration(
			BlogsGroupServiceConfiguration.class,
			new GroupServiceSettingsLocator(
				groupId, BlogsConstants.SERVICE_NAME,
				BlogsGroupServiceConfiguration.class.getName()));
	}

	private String _getEntryURL(BlogsEntry entry, ServiceContext serviceContext)
		throws PortalException {

		String entryURL = GetterUtil.getString(
			serviceContext.getAttribute("entryURL"));

		if (Validator.isNotNull(entryURL)) {
			return entryURL;
		}

		HttpServletRequest httpServletRequest = serviceContext.getRequest();

		if (httpServletRequest == null) {
			return StringPool.BLANK;
		}

		String portletId = PortletProviderUtil.getPortletId(
			BlogsEntry.class.getName(), PortletProvider.Action.VIEW);

		if (Validator.isNotNull(portletId)) {
			String layoutURL = _portal.getLayoutFullURL(
				entry.getGroupId(), portletId);

			if (Validator.isNotNull(layoutURL)) {
				return StringBundler.concat(
					layoutURL, Portal.FRIENDLY_URL_SEPARATOR, "blogs/",
					entry.getEntryId());
			}
		}

		portletId = PortletProviderUtil.getPortletId(
			BlogsEntry.class.getName(), PortletProvider.Action.MANAGE);

		if (Validator.isNull(portletId) ||
			(serviceContext.getThemeDisplay() == null)) {

			return StringPool.BLANK;
		}

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, portletId, PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/blogs/view_entry"
		).setParameter(
			"entryId", entry.getEntryId()
		).buildString();
	}

	private String _getGroupDescriptiveName(Group group, Locale locale) {
		try {
			return group.getDescriptiveName(locale);
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to get descriptive name for group " +
					group.getGroupId(),
				portalException);
		}

		return StringPool.BLANK;
	}

	private String _getLayoutFullURL(
			ThemeDisplay themeDisplay, ServiceContext serviceContext)
		throws PortalException {

		if (themeDisplay == null) {
			return serviceContext.getLayoutFullURL();
		}

		if (themeDisplay.getRefererPlid() == 0) {
			return _portal.getLayoutFullURL(themeDisplay);
		}

		return _portal.getLayoutFullURL(
			_layoutLocalService.getLayout(themeDisplay.getRefererPlid()),
			themeDisplay);
	}

	private String _getUniqueFileName(
			long groupId, String fileName, long folderId)
		throws PortalException {

		return _uniqueFileNameProvider.provide(
			fileName,
			curFileName -> _hasFileEntry(groupId, folderId, curFileName));
	}

	private String _getUniqueUrlTitle(BlogsEntry entry) {
		return _getUniqueUrlTitle(entry, entry.getTitle());
	}

	private String _getUniqueUrlTitle(BlogsEntry entry, String newTitle) {
		long entryId = entry.getEntryId();

		String urlTitle = null;

		if (newTitle == null) {
			urlTitle = String.valueOf(entryId);
		}
		else {
			urlTitle = StringUtil.toLowerCase(newTitle.trim());

			if (Validator.isNull(urlTitle) || Validator.isNumber(urlTitle) ||
				urlTitle.equals("rss")) {

				urlTitle = String.valueOf(entryId);
			}
			else {
				urlTitle =
					_friendlyURLNormalizer.normalizeWithPeriodsAndSlashes(
						urlTitle);
			}

			urlTitle = ModelHintsUtil.trimString(
				BlogsEntry.class.getName(), "urlTitle", urlTitle);
		}

		return _friendlyURLEntryLocalService.getUniqueUrlTitle(
			entry.getGroupId(),
			_classNameLocalService.getClassNameId(BlogsEntry.class),
			entry.getEntryId(), urlTitle, null);
	}

	private String _getURLTitle(long entryId) {
		BlogsEntry entry = blogsEntryPersistence.fetchByPrimaryKey(entryId);

		if (entry != null) {
			return entry.getUrlTitle();
		}

		return StringPool.BLANK;
	}

	private boolean _hasFileEntry(
		long groupId, long folderId, String fileName) {

		FileEntry fileEntry = _portletFileRepository.fetchPortletFileEntry(
			groupId, folderId, fileName);

		if (fileEntry == null) {
			return false;
		}

		return true;
	}

	private boolean _isUpdatedAssetCategories(
		BlogsEntry entry, ServiceContext serviceContext) {

		if (serviceContext == null) {
			return false;
		}

		FriendlyURLEntry mainFriendlyURLEntry =
			_friendlyURLEntryLocalService.fetchMainFriendlyURLEntry(
				_portal.getClassNameId(BlogsEntry.class.getName()),
				entry.getEntryId());

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			_portal.getClassNameId(FriendlyURLEntry.class),
			mainFriendlyURLEntry.getFriendlyURLEntryId());

		long[] friendlyURLAssetCategoryIds = GetterUtil.getLongValues(
			serviceContext.getAttribute("friendlyURLAssetCategoryIds"));

		if (assetEntry == null) {
			return ArrayUtil.isNotEmpty(friendlyURLAssetCategoryIds);
		}

		List<AssetCategory> assetCategories = assetEntry.getCategories();

		return !assetCategories.containsAll(
			ListUtil.toList(friendlyURLAssetCategoryIds));
	}

	private boolean _isValidImageMimeType(FileEntry fileEntry) {
		if (ArrayUtil.contains(
				_blogsFileUploadsConfiguration.imageExtensions(),
				StringPool.STAR)) {

			return true;
		}

		Set<String> extensions = MimeTypesUtil.getExtensions(
			fileEntry.getMimeType());

		for (String extension :
				_blogsFileUploadsConfiguration.imageExtensions()) {

			if (extension.equals(StringPool.STAR) ||
				extensions.contains(extension)) {

				return true;
			}
		}

		return false;
	}

	private void _notifySubscribers(
			long userId, BlogsEntry entry, ServiceContext serviceContext,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		if (!entry.isApproved()) {
			return;
		}

		String entryURL = (String)workflowContext.get(
			WorkflowConstants.CONTEXT_URL);

		if (Validator.isNull(entryURL)) {
			entryURL = StringBundler.concat(
				serviceContext.getLayoutFullURL(),
				Portal.FRIENDLY_URL_SEPARATOR, "blogs/", entry.getEntryId());
		}

		BlogsGroupServiceSettings blogsGroupServiceSettings =
			BlogsGroupServiceSettings.getInstance(entry.getGroupId());

		boolean invokedByCheckEntries = GetterUtil.getBoolean(
			serviceContext.getAttribute(_INVOKED_BY_CHECK_ENTRIES));
		boolean sendEmailEntryUpdated = GetterUtil.getBoolean(
			serviceContext.getAttribute("sendEmailEntryUpdated"));

		if (serviceContext.isCommandAdd() &&
			blogsGroupServiceSettings.isEmailEntryAddedEnabled()) {
		}
		else if ((invokedByCheckEntries || sendEmailEntryUpdated) &&
				 serviceContext.isCommandUpdate() &&
				 blogsGroupServiceSettings.isEmailEntryUpdatedEnabled()) {
		}
		else {
			return;
		}

		Group group = _groupLocalService.getGroup(entry.getGroupId());

		String entryTitle = entry.getTitle();

		String fromName = blogsGroupServiceSettings.getEmailFromName();
		String fromAddress = blogsGroupServiceSettings.getEmailFromAddress();

		LocalizedValuesMap subjectLocalizedValuesMap;
		LocalizedValuesMap bodyLocalizedValuesMap;

		if (serviceContext.isCommandUpdate()) {
			subjectLocalizedValuesMap =
				blogsGroupServiceSettings.getEmailEntryUpdatedSubject();
			bodyLocalizedValuesMap =
				blogsGroupServiceSettings.getEmailEntryUpdatedBody();
		}
		else {
			subjectLocalizedValuesMap =
				blogsGroupServiceSettings.getEmailEntryAddedSubject();
			bodyLocalizedValuesMap =
				blogsGroupServiceSettings.getEmailEntryAddedBody();
		}

		SubscriptionSender subscriptionSender =
			new GroupSubscriptionCheckSubscriptionSender(
				BlogsConstants.RESOURCE_NAME);

		subscriptionSender.setClassPK(entry.getEntryId());
		subscriptionSender.setClassName(entry.getModelClassName());
		subscriptionSender.setContextAttribute(
			"[$BLOGS_ENTRY_CONTENT$]",
			StringUtil.shorten(HtmlUtil.stripHtml(entry.getContent()), 500),
			false);

		String description = entry.getDescription();

		if (Validator.isNotNull(description)) {
			subscriptionSender.setContextAttribute(
				"[$BLOGS_ENTRY_DESCRIPTION$]", description, false);
		}
		else {
			subscriptionSender.setContextAttribute(
				"[$BLOGS_ENTRY_DESCRIPTION$]",
				StringUtil.shorten(HtmlUtil.stripHtml(entry.getContent()), 400),
				false);
		}

		subscriptionSender.setContextAttributes(
			"[$BLOGS_ENTRY_CREATE_DATE$]",
			Time.getSimpleDate(entry.getCreateDate(), "yyyy/MM/dd"),
			"[$BLOGS_ENTRY_STATUS_BY_USER_NAME$]", entry.getStatusByUserName(),
			"[$BLOGS_ENTRY_TITLE$]", entryTitle,
			"[$BLOGS_ENTRY_UPDATE_COMMENT$]",
			HtmlUtil.replaceNewLine(
				GetterUtil.getString(
					serviceContext.getAttribute("emailEntryUpdatedComment"))),
			"[$BLOGS_ENTRY_URL$]", entryURL,
			"[$BLOGS_ENTRY_USER_PORTRAIT_URL$]",
			workflowContext.get(WorkflowConstants.CONTEXT_USER_PORTRAIT_URL),
			"[$BLOGS_ENTRY_USER_URL$]",
			workflowContext.get(WorkflowConstants.CONTEXT_USER_URL));
		subscriptionSender.setContextCreatorUserPrefix("BLOGS_ENTRY");
		subscriptionSender.setCreatorUserId(entry.getUserId());
		subscriptionSender.setCurrentUserId(userId);
		subscriptionSender.setEntryTitle(entryTitle);
		subscriptionSender.setEntryURL(entryURL);
		subscriptionSender.setFrom(fromAddress, fromName);
		subscriptionSender.setHtmlFormat(true);

		if (bodyLocalizedValuesMap != null) {
			subscriptionSender.setLocalizedBodyMap(
				_localization.getMap(bodyLocalizedValuesMap));
		}

		subscriptionSender.setLocalizedContextAttributeWithFunction(
			"[$BLOGS_ENTRY_SITE_NAME$]",
			locale -> _getGroupDescriptiveName(group, locale));

		if (subjectLocalizedValuesMap != null) {
			subscriptionSender.setLocalizedSubjectMap(
				_localization.getMap(subjectLocalizedValuesMap));
		}

		subscriptionSender.setMailId("blogs_entry", entry.getEntryId());

		int notificationType =
			UserNotificationDefinition.NOTIFICATION_TYPE_ADD_ENTRY;

		if (serviceContext.isCommandUpdate()) {
			notificationType =
				UserNotificationDefinition.NOTIFICATION_TYPE_UPDATE_ENTRY;
		}

		subscriptionSender.setNotificationType(notificationType);
		subscriptionSender.setPortletId(
			PortletProviderUtil.getPortletId(
				BlogsEntry.class.getName(), PortletProvider.Action.VIEW));
		subscriptionSender.setReplyToAddress(fromAddress);
		subscriptionSender.setScopeGroupId(entry.getGroupId());

		User user = _userLocalService.getUser(userId);

		BlogsGroupServiceConfiguration blogsGroupServiceConfiguration =
			_getBlogsGroupServiceConfiguration(user.getGroupId());

		subscriptionSender.setSendToCurrentUser(
			blogsGroupServiceConfiguration.
				sendNotificationsToBlogsEntryCreator());

		subscriptionSender.setServiceContext(serviceContext);

		_unsubscribeHelper.registerHooks(subscriptionSender);

		subscriptionSender.addAssetEntryPersistedSubscribers(
			BlogsEntry.class.getName(), entry.getEntryId());
		subscriptionSender.addPersistedSubscribers(
			BlogsEntry.class.getName(), entry.getGroupId());
		subscriptionSender.addPersistedSubscribers(
			BlogsEntry.class.getName(), entry.getEntryId());

		subscriptionSender.flushNotificationsAsync();
	}

	private void _pingGoogle(BlogsEntry entry, ServiceContext serviceContext)
		throws PortalException {

		if (!PropsValues.BLOGS_PING_GOOGLE_ENABLED || !entry.isApproved()) {
			return;
		}

		String portletId = PortletProviderUtil.getPortletId(
			BlogsEntry.class.getName(), PortletProvider.Action.MANAGE);

		if (Validator.isNull(portletId)) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Not pinging Google because there is no blogs portlet " +
						"provider");
			}

			return;
		}

		String layoutFullURL = _portal.getLayoutFullURL(
			serviceContext.getScopeGroupId(), portletId);

		if (Validator.isNull(layoutFullURL)) {
			return;
		}

		if (layoutFullURL.contains("://localhost")) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Not pinging Google because of localhost URL " +
						layoutFullURL);
			}

			return;
		}

		Group group = _groupLocalService.getGroup(entry.getGroupId());

		StringBundler sb = new StringBundler(6);

		String name = group.getDescriptiveName();
		String url = layoutFullURL + Portal.FRIENDLY_URL_SEPARATOR + "blogs";
		String changesURL = serviceContext.getPathMain() + "/blogs/rss";

		sb.append("http://blogsearch.google.com/ping?name=");
		sb.append(URLCodec.encodeURL(name));
		sb.append("&url=");
		sb.append(URLCodec.encodeURL(url));
		sb.append("&changesURL=");
		sb.append(URLCodec.encodeURL(changesURL));

		String location = sb.toString();

		if (_log.isInfoEnabled()) {
			_log.info("Pinging Google at " + location);
		}

		try {
			String response = _http.URLtoString(sb.toString());

			if (_log.isInfoEnabled()) {
				_log.info("Google ping response: " + response);
			}
		}
		catch (IOException ioException) {
			_log.error("Unable to ping Google at " + location, ioException);
		}
	}

	private void _pingPingback(BlogsEntry entry, ServiceContext serviceContext)
		throws PortalException {

		if (!PropsValues.BLOGS_PINGBACK_ENABLED || !entry.isAllowPingbacks() ||
			!entry.isApproved()) {

			return;
		}

		HttpServletRequest httpServletRequest = serviceContext.getRequest();

		if (httpServletRequest == null) {
			return;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String layoutFullURL = _getLayoutFullURL(themeDisplay, serviceContext);

		if (Validator.isNull(layoutFullURL)) {
			return;
		}

		String sourceUri = StringBundler.concat(
			layoutFullURL, Portal.FRIENDLY_URL_SEPARATOR, "blogs/",
			entry.getUrlTitle());

		Source source = new Source(entry.getContent());

		List<StartTag> startTags = source.getAllStartTags("a");

		for (StartTag startTag : startTags) {
			String targetUri = startTag.getAttributeValue("href");

			if (Validator.isNotNull(targetUri)) {
				try {
					LinkbackProducerUtil.sendPingback(sourceUri, targetUri);
				}
				catch (Exception exception) {
					_log.error(
						"Error while sending pingback " + targetUri, exception);
				}
			}
		}
	}

	private void _pingTrackbacks(
			BlogsEntry entry, String[] trackbacks, boolean pingOldTrackbacks,
			ServiceContext serviceContext)
		throws PortalException {

		if (!PropsValues.BLOGS_TRACKBACK_ENABLED ||
			!entry.isAllowTrackbacks() || !entry.isApproved()) {

			return;
		}

		HttpServletRequest httpServletRequest = serviceContext.getRequest();

		if (httpServletRequest == null) {
			return;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String layoutFullURL = _getLayoutFullURL(themeDisplay, serviceContext);

		if (Validator.isNull(layoutFullURL)) {
			return;
		}

		Map<String, String> parts = HashMapBuilder.put(
			"blog_name", entry.getUserName()
		).put(
			"excerpt",
			StringUtil.shorten(
				_htmlParser.extractText(entry.getContent()),
				PropsValues.BLOGS_LINKBACK_EXCERPT_LENGTH)
		).put(
			"title", entry.getTitle()
		).put(
			"url",
			StringBundler.concat(
				layoutFullURL, Portal.FRIENDLY_URL_SEPARATOR, "blogs/",
				entry.getUrlTitle())
		).build();

		Set<String> newTrackbacks;

		if (ArrayUtil.isNotEmpty(trackbacks)) {
			newTrackbacks = SetUtil.fromArray(trackbacks);
		}
		else {
			newTrackbacks = new HashSet<>();
		}

		if (pingOldTrackbacks) {
			newTrackbacks.addAll(
				SetUtil.fromArray(StringUtil.split(entry.getTrackbacks())));

			entry.setTrackbacks(StringPool.BLANK);

			entry = blogsEntryPersistence.update(entry);
		}

		Set<String> oldTrackbacks = SetUtil.fromArray(
			StringUtil.split(entry.getTrackbacks()));

		Set<String> validTrackbacks = new HashSet<>();

		for (String newTrackback : newTrackbacks) {
			if (oldTrackbacks.contains(newTrackback)) {
				continue;
			}

			try {
				if (LinkbackProducerUtil.sendTrackback(newTrackback, parts)) {
					validTrackbacks.add(newTrackback);
				}
			}
			catch (Exception exception) {
				_log.error(
					"Error while sending trackback at " + newTrackback,
					exception);
			}
		}

		if (validTrackbacks.isEmpty()) {
			return;
		}

		String mergedTrackbacks = StringUtil.merge(validTrackbacks);

		if (Validator.isNotNull(entry.getTrackbacks())) {
			mergedTrackbacks += StringPool.COMMA + entry.getTrackbacks();
		}

		entry.setTrackbacks(mergedTrackbacks);

		blogsEntryPersistence.update(entry);
	}

	private String _sanitizeUrlTitle(String urlTitle) {
		while (urlTitle.startsWith(StringPool.SLASH)) {
			urlTitle = urlTitle.substring(1);
		}

		return urlTitle;
	}

	private BlogsEntry _startWorkflowInstance(
			long userId, BlogsEntry entry, ServiceContext serviceContext)
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
				_getEntryURL(entry, serviceContext)
			).put(
				WorkflowConstants.CONTEXT_USER_PORTRAIT_URL, userPortraitURL
			).put(
				WorkflowConstants.CONTEXT_USER_URL, userURL
			).build();

		return WorkflowHandlerRegistryUtil.startWorkflowInstance(
			entry.getCompanyId(), entry.getGroupId(), userId,
			BlogsEntry.class.getName(), entry.getEntryId(), entry,
			serviceContext, workflowContext);
	}

	private void _validate(long smallImageFileEntryId) throws PortalException {
		if (smallImageFileEntryId == 0) {
			return;
		}

		FileEntry fileEntry = _portletFileRepository.getPortletFileEntry(
			smallImageFileEntryId);

		if (!_isValidImageMimeType(fileEntry)) {
			throw new EntrySmallImageNameException(
				"Invalid small image for file entry " + smallImageFileEntryId);
		}
	}

	private void _validate(
			String title, String urlTitle, String content, int status)
		throws PortalException {

		if ((status == WorkflowConstants.STATUS_APPROVED) &&
			Validator.isNull(title)) {

			throw new EntryTitleException("Title is null");
		}

		if (Validator.isNotNull(title)) {
			int titleMaxLength = ModelHintsUtil.getMaxLength(
				BlogsEntry.class.getName(), "title");

			if (title.length() > titleMaxLength) {
				throw new EntryTitleException(
					"Title has more than " + titleMaxLength + " characters");
			}
		}

		if (Validator.isNotNull(urlTitle)) {
			int urlTitleMaxLength = ModelHintsUtil.getMaxLength(
				BlogsEntry.class.getName(), "urlTitle");

			if (urlTitle.length() > urlTitleMaxLength) {
				throw new EntryUrlTitleException(
					"URL title has more than " + urlTitleMaxLength +
						" characters");
			}
		}

		if (Validator.isNull(content)) {
			throw new EntryContentException("Content is null");
		}

		int contentMaxLength = ModelHintsUtil.getMaxLength(
			BlogsEntry.class.getName(), "content");

		if (content.length() > contentMaxLength) {
			throw new EntryContentException(
				"Content has more than " + contentMaxLength + " characters");
		}
	}

	private void _validateAttachmentFileEntry(FileEntry fileEntry)
		throws PortalException {

		Repository repository = _portletFileRepository.getPortletRepository(
			fileEntry.getGroupId(), BlogsConstants.SERVICE_NAME);

		Folder folder = _portletFileRepository.getPortletFolder(
			repository.getRepositoryId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			BlogsConstants.SERVICE_NAME);

		if (fileEntry.getFolderId() != folder.getFolderId()) {
			throw new NoSuchFileEntryException(
				StringBundler.concat(
					"File entry ", fileEntry.getFileEntryId(),
					" does not belong to folder ", folder.getFolderId()));
		}
	}

	private String _validateURLTitle(
			long groupId, String urlTitle, ServiceContext serviceContext)
		throws PortalException {

		long classNameId = _classNameLocalService.getClassNameId(
			BlogsEntry.class);

		try {
			_friendlyURLEntryLocalService.validate(
				groupId, classNameId, urlTitle);

			return urlTitle;
		}
		catch (DuplicateFriendlyURLEntryException
					duplicateFriendlyURLEntryException) {

			if (serviceContext.getWorkflowAction() ==
					WorkflowConstants.ACTION_SAVE_DRAFT) {

				return null;
			}

			throw duplicateFriendlyURLEntryException;
		}
	}

	private static final String _COVER_IMAGE_FOLDER_NAME = "Cover Image";

	private static final String _INVOKED_BY_CHECK_ENTRIES =
		BlogsEntry.class.getName() + "#INVOKED_BY_CHECK_ENTRIES";

	private static final String _SMALL_IMAGE_FOLDER_NAME = "Small Image";

	private static final Log _log = LogFactoryUtil.getLog(
		BlogsEntryLocalServiceImpl.class);

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private AssetLinkLocalService _assetLinkLocalService;

	private volatile BlogsFileUploadsConfiguration
		_blogsFileUploadsConfiguration;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CommentManager _commentManager;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private ExpandoRowLocalService _expandoRowLocalService;

	@Reference
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Reference
	private FriendlyURLNormalizer _friendlyURLNormalizer;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private HtmlParser _htmlParser;

	@Reference
	private Http _http;

	@Reference
	private ImageLocalService _imageLocalService;

	@Reference
	private ImageMagick _imageMagick;

	@Reference
	private LayoutLocalService _layoutLocalService;

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

	@Reference
	private SubscriptionLocalService _subscriptionLocalService;

	@Reference
	private TrashEntryLocalService _trashEntryLocalService;

	@Reference
	private UniqueFileNameProvider _uniqueFileNameProvider;

	@Reference
	private UnsubscribeHelper _unsubscribeHelper;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private WorkflowInstanceLinkLocalService _workflowInstanceLinkLocalService;

}