/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.service.impl;

import com.liferay.blogs.constants.BlogsConstants;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.base.BlogsEntryServiceBaseImpl;
import com.liferay.blogs.util.comparator.EntryDisplayDateComparator;
import com.liferay.blogs.util.comparator.EntryIdComparator;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlParser;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.rss.export.RSSExporter;
import com.liferay.rss.model.SyndContent;
import com.liferay.rss.model.SyndEntry;
import com.liferay.rss.model.SyndFeed;
import com.liferay.rss.model.SyndLink;
import com.liferay.rss.model.SyndModelFactory;
import com.liferay.rss.util.RSSUtil;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * Provides the remote service for accessing, adding, deleting, subscription
 * handling of, trash handling of, and updating blog entries. Its methods
 * include permission checks.
 *
 * @author Brian Wing Shun Chan
 * @author Máté Thurzó
 */
@Component(
	property = {
		"json.web.service.context.name=blogs",
		"json.web.service.context.path=BlogsEntry"
	},
	service = AopService.class
)
public class BlogsEntryServiceImpl extends BlogsEntryServiceBaseImpl {

	@Override
	public FileEntry addAttachmentFileEntry(
			String externalReferenceCode, long groupId, String fileName,
			String mimeType, InputStream inputStream)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), groupId, ActionKeys.ADD_ENTRY);

		return blogsEntryLocalService.addAttachmentFileEntry(
			externalReferenceCode, getUserId(), groupId, fileName, mimeType,
			inputStream);
	}

	@Override
	public Folder addAttachmentsFolder(long groupId) throws PortalException {
		_portletResourcePermission.check(
			getPermissionChecker(), groupId, ActionKeys.ADD_ENTRY);

		return blogsEntryLocalService.addAttachmentsFolder(
			getUserId(), groupId);
	}

	@Override
	public BlogsEntry addEntry(
			String title, String subtitle, String description, String content,
			int displayDateMonth, int displayDateDay, int displayDateYear,
			int displayDateHour, int displayDateMinute, boolean allowPingbacks,
			boolean allowTrackbacks, String[] trackbacks,
			String coverImageCaption, ImageSelector coverImageImageSelector,
			ImageSelector smallImageImageSelector,
			ServiceContext serviceContext)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), serviceContext.getScopeGroupId(),
			ActionKeys.ADD_ENTRY);

		return blogsEntryLocalService.addEntry(
			getUserId(), title, subtitle, description, content,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, allowPingbacks, allowTrackbacks, trackbacks,
			coverImageCaption, coverImageImageSelector, smallImageImageSelector,
			serviceContext);
	}

	@Override
	public BlogsEntry addEntry(
			String externalReferenceCode, String title, String subtitle,
			String urlTitle, String description, String content,
			int displayDateMonth, int displayDateDay, int displayDateYear,
			int displayDateHour, int displayDateMinute, boolean allowPingbacks,
			boolean allowTrackbacks, String[] trackbacks,
			String coverImageCaption, ImageSelector coverImageImageSelector,
			ImageSelector smallImageImageSelector,
			ServiceContext serviceContext)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), serviceContext.getScopeGroupId(),
			ActionKeys.ADD_ENTRY);

		return blogsEntryLocalService.addEntry(
			externalReferenceCode, getUserId(), title, subtitle, urlTitle,
			description, content, displayDateMonth, displayDateDay,
			displayDateYear, displayDateHour, displayDateMinute, allowPingbacks,
			allowTrackbacks, trackbacks, coverImageCaption,
			coverImageImageSelector, smallImageImageSelector, serviceContext);
	}

	@Override
	public void deleteAttachmentFileEntry(long fileEntryId)
		throws PortalException {

		FileEntry fileEntry = blogsEntryLocalService.getAttachmentFileEntry(
			fileEntryId);

		_fileEntryModelResourcePermission.check(
			getPermissionChecker(), fileEntry, ActionKeys.DELETE);

		blogsEntryLocalService.deleteAttachmentFileEntry(fileEntryId);
	}

	@Override
	public void deleteEntry(long entryId) throws PortalException {
		_blogsEntryModelResourcePermission.check(
			getPermissionChecker(), entryId, ActionKeys.DELETE);

		blogsEntryLocalService.deleteEntry(entryId);
	}

	@Override
	public BlogsEntry fetchBlogsEntryByExternalReferenceCode(
			long groupId, String externalReferenceCode)
		throws PortalException {

		BlogsEntry blogsEntry = blogsEntryPersistence.fetchByERC_G(
			externalReferenceCode, groupId);

		if (blogsEntry != null) {
			_blogsEntryModelResourcePermission.check(
				getPermissionChecker(), blogsEntry, ActionKeys.VIEW);
		}

		return blogsEntry;
	}

	@Override
	public FileEntry getAttachmentFileEntry(long fileEntryId)
		throws PortalException {

		FileEntry fileEntry = blogsEntryLocalService.getAttachmentFileEntry(
			fileEntryId);

		_fileEntryModelResourcePermission.check(
			getPermissionChecker(), fileEntry, ActionKeys.VIEW);

		return fileEntry;
	}

	@Override
	public FileEntry getAttachmentFileEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		FileEntry fileEntry =
			blogsEntryLocalService.
				getAttachmentFileEntryByExternalReferenceCode(
					externalReferenceCode, groupId);

		_fileEntryModelResourcePermission.check(
			getPermissionChecker(), fileEntry, ActionKeys.VIEW);

		return fileEntry;
	}

	@Override
	public BlogsEntry getBlogsEntryByExternalReferenceCode(
			long groupId, String externalReferenceCode)
		throws PortalException {

		BlogsEntry entry =
			blogsEntryLocalService.getBlogsEntryByExternalReferenceCode(
				externalReferenceCode, groupId);

		_blogsEntryModelResourcePermission.check(
			getPermissionChecker(), entry, ActionKeys.VIEW);

		return entry;
	}

	@Override
	public List<BlogsEntry> getCompanyEntries(
			long companyId, Date displayDate, int status, int max)
		throws PortalException {

		List<BlogsEntry> blogsEntries1 = new ArrayList<>();

		boolean listNotExhausted = true;

		QueryDefinition<BlogsEntry> queryDefinition = new QueryDefinition<>(
			status, false, 0, 0, EntryDisplayDateComparator.getInstance(false));

		if (status == WorkflowConstants.STATUS_ANY) {
			queryDefinition.setStatus(WorkflowConstants.STATUS_IN_TRASH, true);
		}

		while ((blogsEntries1.size() < max) && listNotExhausted) {
			queryDefinition.setEnd(queryDefinition.getStart() + max);

			List<BlogsEntry> blogsEntries2 =
				blogsEntryLocalService.getCompanyEntries(
					companyId, displayDate, queryDefinition);

			queryDefinition.setStart(queryDefinition.getStart() + max);

			listNotExhausted = blogsEntries2.size() == max;

			for (BlogsEntry blogsEntry : blogsEntries2) {
				if (blogsEntries1.size() >= max) {
					break;
				}

				if (!_blogsEntryModelResourcePermission.contains(
						getPermissionChecker(), blogsEntry, ActionKeys.VIEW)) {

					continue;
				}

				blogsEntries1.add(blogsEntry);
			}
		}

		return blogsEntries1;
	}

	@Override
	public String getCompanyEntriesRSS(
			long companyId, Date displayDate, int status, int max, String type,
			double version, String displayStyle, String feedURL,
			String entryURL, ThemeDisplay themeDisplay)
		throws PortalException {

		Company company = _companyLocalService.getCompany(companyId);

		String name = company.getName();

		List<BlogsEntry> blogsEntries = getCompanyEntries(
			companyId, displayDate, status, max);

		return _exportToRSS(
			name, name, type, version, displayStyle, feedURL, entryURL,
			blogsEntries, themeDisplay);
	}

	@Override
	public BlogsEntry[] getEntriesPrevAndNext(long entryId)
		throws PortalException {

		BlogsEntry entry = blogsEntryPersistence.findByPrimaryKey(entryId);

		_blogsEntryModelResourcePermission.check(
			getPermissionChecker(), entry, ActionKeys.VIEW);

		BlogsEntry[] entries =
			blogsEntryPersistence.filterFindByG_D_S_PrevAndNext(
				entryId, entry.getGroupId(), entry.getDisplayDate(),
				WorkflowConstants.STATUS_APPROVED,
				EntryIdComparator.getInstance(true));

		if (entries[0] == null) {
			entries[0] = blogsEntryPersistence.fetchByG_LtD_S_Last(
				entry.getGroupId(), entry.getDisplayDate(),
				WorkflowConstants.STATUS_APPROVED,
				EntryDisplayDateComparator.getInstance(true));

			if ((entries[0] != null) &&
				!_blogsEntryModelResourcePermission.contains(
					getPermissionChecker(), entries[0], ActionKeys.VIEW)) {

				entries[0] = null;
			}
		}

		if (entries[2] == null) {
			entries[2] = blogsEntryPersistence.fetchByG_GtD_S_First(
				entry.getGroupId(), entry.getDisplayDate(),
				WorkflowConstants.STATUS_APPROVED,
				EntryDisplayDateComparator.getInstance(true));

			if ((entries[2] != null) &&
				!_blogsEntryModelResourcePermission.contains(
					getPermissionChecker(), entries[2], ActionKeys.VIEW)) {

				entries[2] = null;
			}
		}

		return entries;
	}

	@Override
	public BlogsEntry getEntry(long entryId) throws PortalException {
		BlogsEntry entry = blogsEntryLocalService.getEntry(entryId);

		_blogsEntryModelResourcePermission.check(
			getPermissionChecker(), entry, ActionKeys.VIEW);

		return entry;
	}

	@Override
	public BlogsEntry getEntry(long groupId, String urlTitle)
		throws PortalException {

		BlogsEntry entry = blogsEntryLocalService.getEntry(groupId, urlTitle);

		_blogsEntryModelResourcePermission.check(
			getPermissionChecker(), entry, ActionKeys.VIEW);

		return entry;
	}

	@Override
	public List<BlogsEntry> getGroupEntries(
		long groupId, Date displayDate, int status, int max) {

		return getGroupEntries(groupId, displayDate, status, 0, max);
	}

	@Override
	public List<BlogsEntry> getGroupEntries(
		long groupId, Date displayDate, int status, int start, int end) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return blogsEntryPersistence.filterFindByG_LtD_NotS(
				groupId, displayDate, WorkflowConstants.STATUS_IN_TRASH, start,
				end);
		}

		return blogsEntryPersistence.filterFindByG_LtD_S(
			groupId, displayDate, status, start, end);
	}

	@Override
	public List<BlogsEntry> getGroupEntries(long groupId, int status, int max) {
		return getGroupEntries(groupId, status, 0, max);
	}

	@Override
	public List<BlogsEntry> getGroupEntries(
		long groupId, int status, int start, int end) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return blogsEntryPersistence.filterFindByG_NotS(
				groupId, WorkflowConstants.STATUS_IN_TRASH, start, end);
		}

		return blogsEntryPersistence.filterFindByG_S(
			groupId, status, start, end);
	}

	@Override
	public List<BlogsEntry> getGroupEntries(
		long groupId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return blogsEntryPersistence.filterFindByG_NotS(
				groupId, WorkflowConstants.STATUS_IN_TRASH, start, end,
				orderByComparator);
		}

		return blogsEntryPersistence.filterFindByG_S(
			groupId, status, start, end, orderByComparator);
	}

	@Override
	public int getGroupEntriesCount(
		long groupId, Date displayDate, int status) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return blogsEntryPersistence.filterCountByG_LtD_NotS(
				groupId, displayDate, WorkflowConstants.STATUS_IN_TRASH);
		}

		return blogsEntryPersistence.filterCountByG_LtD_S(
			groupId, displayDate, status);
	}

	@Override
	public int getGroupEntriesCount(long groupId, int status) {
		if (status == WorkflowConstants.STATUS_ANY) {
			return blogsEntryPersistence.filterCountByG_NotS(
				groupId, WorkflowConstants.STATUS_IN_TRASH);
		}

		return blogsEntryPersistence.filterCountByG_S(groupId, status);
	}

	@Override
	public String getGroupEntriesRSS(
			long groupId, Date displayDate, int status, int max, String type,
			double version, String displayStyle, String feedURL,
			String entryURL, ThemeDisplay themeDisplay)
		throws PortalException {

		Group group = _groupLocalService.getGroup(groupId);

		String name = group.getDescriptiveName();

		List<BlogsEntry> blogsEntries = getGroupEntries(
			groupId, displayDate, status, max);

		return _exportToRSS(
			name, name, type, version, displayStyle, feedURL, entryURL,
			blogsEntries, themeDisplay);
	}

	@Override
	public List<BlogsEntry> getGroupsEntries(
			long companyId, long groupId, Date displayDate, int status, int max)
		throws PortalException {

		List<BlogsEntry> blogsEntries1 = new ArrayList<>();

		boolean listNotExhausted = true;

		QueryDefinition<BlogsEntry> queryDefinition = new QueryDefinition<>(
			status, false, 0, 0, EntryDisplayDateComparator.getInstance(false));

		if (status == WorkflowConstants.STATUS_ANY) {
			queryDefinition.setStatus(WorkflowConstants.STATUS_IN_TRASH, true);
		}

		while ((blogsEntries1.size() < max) && listNotExhausted) {
			queryDefinition.setEnd(queryDefinition.getStart() + max);

			List<BlogsEntry> blogsEntries2 =
				blogsEntryLocalService.getGroupsEntries(
					companyId, groupId, displayDate, queryDefinition);

			queryDefinition.setStart(queryDefinition.getStart() + max);

			listNotExhausted = blogsEntries2.size() == max;

			for (BlogsEntry blogsEntry : blogsEntries2) {
				if (blogsEntries1.size() >= max) {
					break;
				}

				if (!_blogsEntryModelResourcePermission.contains(
						getPermissionChecker(), blogsEntry, ActionKeys.VIEW)) {

					continue;
				}

				blogsEntries1.add(blogsEntry);
			}
		}

		return blogsEntries1;
	}

	@Override
	public List<BlogsEntry> getGroupUserEntries(
		long groupId, long userId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return blogsEntryPersistence.filterFindByG_U_NotS(
				groupId, userId, WorkflowConstants.STATUS_IN_TRASH, start, end,
				orderByComparator);
		}

		return blogsEntryPersistence.filterFindByG_U_S(
			groupId, userId, status, start, end, orderByComparator);
	}

	@Override
	public List<BlogsEntry> getGroupUserEntries(
		long groupId, long userId, int[] statuses, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return blogsEntryPersistence.filterFindByG_U_S(
			groupId, userId, statuses, start, end, orderByComparator);
	}

	@Override
	public int getGroupUserEntriesCount(long groupId, long userId, int status) {
		if (status == WorkflowConstants.STATUS_ANY) {
			return blogsEntryPersistence.filterCountByG_U_NotS(
				groupId, userId, WorkflowConstants.STATUS_IN_TRASH);
		}

		return blogsEntryPersistence.filterCountByG_U_S(
			groupId, userId, status);
	}

	@Override
	public int getGroupUserEntriesCount(
		long groupId, long userId, int[] statuses) {

		return blogsEntryPersistence.filterCountByG_U_S(
			groupId, userId, statuses);
	}

	@Override
	public List<BlogsEntry> getOrganizationEntries(
			long organizationId, Date displayDate, int status, int max)
		throws PortalException {

		List<BlogsEntry> blogsEntries1 = new ArrayList<>();

		boolean listNotExhausted = true;

		QueryDefinition<BlogsEntry> queryDefinition = new QueryDefinition<>(
			status, false, 0, 0, EntryDisplayDateComparator.getInstance(false));

		if (status == WorkflowConstants.STATUS_ANY) {
			queryDefinition.setStatus(WorkflowConstants.STATUS_IN_TRASH, true);
		}

		while ((blogsEntries1.size() < max) && listNotExhausted) {
			queryDefinition.setEnd(queryDefinition.getStart() + max);

			List<BlogsEntry> blogsEntries2 =
				blogsEntryFinder.findByOrganizationId(
					organizationId, displayDate, queryDefinition);

			queryDefinition.setStart(queryDefinition.getStart() + max);

			listNotExhausted = blogsEntries2.size() == max;

			for (BlogsEntry blogsEntry : blogsEntries2) {
				if (blogsEntries1.size() >= max) {
					break;
				}

				if (!_blogsEntryModelResourcePermission.contains(
						getPermissionChecker(), blogsEntry, ActionKeys.VIEW)) {

					continue;
				}

				blogsEntries1.add(blogsEntry);
			}
		}

		return blogsEntries1;
	}

	@Override
	public String getOrganizationEntriesRSS(
			long organizationId, Date displayDate, int status, int max,
			String type, double version, String displayStyle, String feedURL,
			String entryURL, ThemeDisplay themeDisplay)
		throws PortalException {

		Organization organization = _organizationLocalService.getOrganization(
			organizationId);

		String name = organization.getName();

		List<BlogsEntry> blogsEntries = getOrganizationEntries(
			organizationId, displayDate, status, max);

		return _exportToRSS(
			name, name, type, version, displayStyle, feedURL, entryURL,
			blogsEntries, themeDisplay);
	}

	@Override
	public BlogsEntry moveEntryToTrash(long entryId) throws PortalException {
		_blogsEntryModelResourcePermission.check(
			getPermissionChecker(), entryId, ActionKeys.DELETE);

		return blogsEntryLocalService.moveEntryToTrash(getUserId(), entryId);
	}

	@Override
	public void restoreEntryFromTrash(long entryId) throws PortalException {
		_blogsEntryModelResourcePermission.check(
			getPermissionChecker(), entryId, ActionKeys.DELETE);

		blogsEntryLocalService.restoreEntryFromTrash(getUserId(), entryId);
	}

	@Override
	public void subscribe(long groupId) throws PortalException {
		_portletResourcePermission.check(
			getPermissionChecker(), groupId, ActionKeys.SUBSCRIBE);

		blogsEntryLocalService.subscribe(getUserId(), groupId);
	}

	@Override
	public void unsubscribe(long groupId) throws PortalException {
		_portletResourcePermission.check(
			getPermissionChecker(), groupId, ActionKeys.SUBSCRIBE);

		blogsEntryLocalService.unsubscribe(getUserId(), groupId);
	}

	@Override
	public BlogsEntry updateEntry(
			long entryId, String title, String subtitle, String description,
			String content, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute,
			boolean allowPingbacks, boolean allowTrackbacks,
			String[] trackbacks, String coverImageCaption,
			ImageSelector coverImageImageSelector,
			ImageSelector smallImageImageSelector,
			ServiceContext serviceContext)
		throws PortalException {

		return updateEntry(
			entryId, title, subtitle, StringPool.BLANK, description, content,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, allowPingbacks, allowTrackbacks, trackbacks,
			coverImageCaption, coverImageImageSelector, smallImageImageSelector,
			serviceContext);
	}

	@Override
	public BlogsEntry updateEntry(
			long entryId, String title, String subtitle, String urlTitle,
			String description, String content, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, boolean allowPingbacks,
			boolean allowTrackbacks, String[] trackbacks,
			String coverImageCaption, ImageSelector coverImageImageSelector,
			ImageSelector smallImageImageSelector,
			ServiceContext serviceContext)
		throws PortalException {

		_blogsEntryModelResourcePermission.check(
			getPermissionChecker(), entryId, ActionKeys.UPDATE);

		return blogsEntryLocalService.updateEntry(
			getUserId(), entryId, title, subtitle, urlTitle, description,
			content, displayDateMonth, displayDateDay, displayDateYear,
			displayDateHour, displayDateMinute, allowPingbacks, allowTrackbacks,
			trackbacks, coverImageCaption, coverImageImageSelector,
			smallImageImageSelector, serviceContext);
	}

	private String _exportToRSS(
		String name, String description, String type, double version,
		String displayStyle, String feedURL, String entryURL,
		List<BlogsEntry> blogsEntries, ThemeDisplay themeDisplay) {

		SyndFeed syndFeed = _syndModelFactory.createSyndFeed();

		syndFeed.setDescription(description);

		List<SyndEntry> syndEntries = new ArrayList<>();

		syndFeed.setEntries(syndEntries);

		for (BlogsEntry entry : blogsEntries) {
			SyndEntry syndEntry = _syndModelFactory.createSyndEntry();

			syndEntry.setAuthor(_portal.getUserName(entry));

			SyndContent syndContent = _syndModelFactory.createSyndContent();

			syndContent.setType(RSSUtil.ENTRY_TYPE_DEFAULT);

			String value = null;

			if (displayStyle.equals(RSSUtil.DISPLAY_STYLE_ABSTRACT)) {
				String summary = entry.getDescription();

				if (Validator.isNull(summary)) {
					summary = entry.getContent();
				}

				value = StringUtil.shorten(
					_htmlParser.extractText(summary),
					PropsValues.BLOGS_RSS_ABSTRACT_LENGTH, StringPool.BLANK);
			}
			else if (displayStyle.equals(RSSUtil.DISPLAY_STYLE_TITLE)) {
				value = StringPool.BLANK;
			}
			else {
				value = StringUtil.replace(
					entry.getContent(), new String[] {"href=\"/", "src=\"/"},
					new String[] {
						"href=\"" + themeDisplay.getURLPortal() + "/",
						"src=\"" + themeDisplay.getURLPortal() + "/"
					});
			}

			syndContent.setValue(value);

			syndEntry.setDescription(syndContent);

			StringBundler sb = new StringBundler(4);

			sb.append(entryURL);

			if (!entryURL.endsWith(StringPool.QUESTION)) {
				sb.append(StringPool.AMPERSAND);
			}

			sb.append("entryId=");
			sb.append(entry.getEntryId());

			String link = sb.toString();

			syndEntry.setLink(link);

			syndEntry.setPublishedDate(entry.getDisplayDate());
			syndEntry.setTitle(entry.getTitle());
			syndEntry.setUpdatedDate(entry.getModifiedDate());
			syndEntry.setUri(link);

			syndEntries.add(syndEntry);
		}

		syndFeed.setFeedType(RSSUtil.getFeedType(type, version));

		List<SyndLink> syndLinks = new ArrayList<>();

		syndFeed.setLinks(syndLinks);

		SyndLink selfSyndLink = _syndModelFactory.createSyndLink();

		syndLinks.add(selfSyndLink);

		selfSyndLink.setHref(feedURL);
		selfSyndLink.setRel("self");

		syndFeed.setPublishedDate(new Date());
		syndFeed.setTitle(name);
		syndFeed.setUri(feedURL);

		return _rssExporter.export(syndFeed);
	}

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(model.class.name=com.liferay.blogs.model.BlogsEntry)"
	)
	private volatile ModelResourcePermission<BlogsEntry>
		_blogsEntryModelResourcePermission;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.repository.model.FileEntry)"
	)
	private ModelResourcePermission<FileEntry>
		_fileEntryModelResourcePermission;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private HtmlParser _htmlParser;

	@Reference
	private OrganizationLocalService _organizationLocalService;

	@Reference
	private Portal _portal;

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(resource.name=" + BlogsConstants.RESOURCE_NAME + ")"
	)
	private volatile PortletResourcePermission _portletResourcePermission;

	@Reference
	private RSSExporter _rssExporter;

	@Reference
	private SyndModelFactory _syndModelFactory;

}