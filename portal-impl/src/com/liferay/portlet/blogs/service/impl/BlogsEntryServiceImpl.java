/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portlet.blogs.service.impl;

import com.liferay.blogs.kernel.model.BlogsEntry;
import com.liferay.blogs.kernel.util.comparator.EntryDisplayDateComparator;
import com.liferay.blogs.kernel.util.comparator.EntryIdComparator;
import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.blogs.service.base.BlogsEntryServiceBaseImpl;
import com.liferay.portlet.blogs.service.permission.BlogsEntryPermission;
import com.liferay.portlet.blogs.service.permission.BlogsPermission;
import com.liferay.util.RSSUtil;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.feed.synd.SyndLink;
import com.sun.syndication.feed.synd.SyndLinkImpl;
import com.sun.syndication.io.FeedException;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Provides the remote service for accessing, adding, deleting, subscription
 * handling of, trash handling of, and updating blog entries. Its methods
 * include permission checks.
 *
 * @author Brian Wing Shun Chan
 * @author Mate Thurzo
 */
public class BlogsEntryServiceImpl extends BlogsEntryServiceBaseImpl {

	/**
	 * @deprecated As of 7.0.0, replaced by {@link #addEntry(String, String,
	 *             String, String, int, int, int, int, int, boolean, boolean,
	 *             String[], String, ImageSelector, ImageSelector,
	 *             ServiceContext)}
	 */
	@Deprecated
	@Override
	public BlogsEntry addEntry(
			String title, String description, String content,
			int displayDateMonth, int displayDateDay, int displayDateYear,
			int displayDateHour, int displayDateMinute, boolean allowPingbacks,
			boolean allowTrackbacks, String[] trackbacks, boolean smallImage,
			String smallImageURL, String smallImageFileName,
			InputStream smallImageInputStream, ServiceContext serviceContext)
		throws PortalException {

		BlogsPermission.check(
			getPermissionChecker(), serviceContext.getScopeGroupId(),
			ActionKeys.ADD_ENTRY);

		ImageSelector coverImageImageSelector = null;
		ImageSelector smallImageImageSelector = null;

		if (smallImage) {
			if (Validator.isNotNull(smallImageFileName) &&
				(smallImageInputStream != null)) {

				try {
					byte[] bytes = FileUtil.getBytes(smallImageInputStream);

					smallImageImageSelector = new ImageSelector(
						bytes, smallImageFileName,
						MimeTypesUtil.getContentType(smallImageFileName), null);
				}
				catch (IOException ioe) {
					_log.error("Unable to create image selector", ioe);
				}
			}
			else if (Validator.isNotNull(smallImageURL)) {
				smallImageImageSelector = new ImageSelector(smallImageURL);
			}
		}

		return addEntry(
			title, StringPool.BLANK, description, content, displayDateMonth,
			displayDateDay, displayDateYear, displayDateHour, displayDateMinute,
			allowPingbacks, allowTrackbacks, trackbacks, StringPool.BLANK,
			coverImageImageSelector, smallImageImageSelector, serviceContext);
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

		BlogsPermission.check(
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
	public void deleteEntry(long entryId) throws PortalException {
		BlogsEntryPermission.check(
			getPermissionChecker(), entryId, ActionKeys.DELETE);

		blogsEntryLocalService.deleteEntry(entryId);
	}

	@Override
	public List<BlogsEntry> getCompanyEntries(
			long companyId, Date displayDate, int status, int max)
		throws PortalException {

		List<BlogsEntry> entries = new ArrayList<>();

		boolean listNotExhausted = true;

		QueryDefinition<BlogsEntry> queryDefinition = new QueryDefinition<>(
			status, false, 0, 0, new EntryDisplayDateComparator());

		if (status == WorkflowConstants.STATUS_ANY) {
			queryDefinition.setStatus(WorkflowConstants.STATUS_IN_TRASH, true);
		}

		while ((entries.size() < max) && listNotExhausted) {
			queryDefinition.setEnd(queryDefinition.getStart() + max);

			List<BlogsEntry> entryList =
				blogsEntryLocalService.getCompanyEntries(
					companyId, displayDate, queryDefinition);

			queryDefinition.setStart(queryDefinition.getStart() + max);

			listNotExhausted = entryList.size() == max;

			for (BlogsEntry entry : entryList) {
				if (entries.size() >= max) {
					break;
				}

				if (BlogsEntryPermission.contains(
						getPermissionChecker(), entry, ActionKeys.VIEW)) {

					entries.add(entry);
				}
			}
		}

		return entries;
	}

	@Override
	public String getCompanyEntriesRSS(
			long companyId, Date displayDate, int status, int max, String type,
			double version, String displayStyle, String feedURL,
			String entryURL, ThemeDisplay themeDisplay)
		throws PortalException {

		Company company = companyPersistence.findByPrimaryKey(companyId);

		String name = company.getName();

		List<BlogsEntry> blogsEntries = getCompanyEntries(
			companyId, displayDate, status, max);

		return exportToRSS(
			name, name, type, version, displayStyle, feedURL, entryURL,
			blogsEntries, themeDisplay);
	}

	@Override
	public BlogsEntry[] getEntriesPrevAndNext(long entryId)
		throws PortalException {

		BlogsEntry entry = blogsEntryPersistence.findByPrimaryKey(entryId);

		BlogsEntryPermission.check(
			getPermissionChecker(), entry, ActionKeys.VIEW);

		BlogsEntry[] entries =
			blogsEntryPersistence.filterFindByG_D_S_PrevAndNext(
				entryId, entry.getGroupId(), entry.getDisplayDate(),
				WorkflowConstants.STATUS_APPROVED, new EntryIdComparator(true));

		if (entries[0] == null) {
			entries[0] = blogsEntryPersistence.fetchByG_LtD_S_Last(
				entry.getGroupId(), entry.getDisplayDate(),
				WorkflowConstants.STATUS_APPROVED,
				new EntryDisplayDateComparator(true));

			if ((entries[0] != null) &&
				!BlogsEntryPermission.contains(
					getPermissionChecker(), entries[0], ActionKeys.VIEW)) {

				entries[0] = null;
			}
		}

		if (entries[2] == null) {
			entries[2] = blogsEntryPersistence.fetchByG_GtD_S_First(
				entry.getGroupId(), entry.getDisplayDate(),
				WorkflowConstants.STATUS_APPROVED,
				new EntryDisplayDateComparator(true));

			if ((entries[2] != null) &&
				!BlogsEntryPermission.contains(
					getPermissionChecker(), entries[2], ActionKeys.VIEW)) {

				entries[2] = null;
			}
		}

		return entries;
	}

	@Override
	public BlogsEntry getEntry(long entryId) throws PortalException {
		BlogsEntry entry = blogsEntryLocalService.getEntry(entryId);

		BlogsEntryPermission.check(
			getPermissionChecker(), entry, ActionKeys.VIEW);

		return entry;
	}

	@Override
	public BlogsEntry getEntry(long groupId, String urlTitle)
		throws PortalException {

		BlogsEntry entry = blogsEntryLocalService.getEntry(groupId, urlTitle);

		BlogsEntryPermission.check(
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
		else {
			return blogsEntryPersistence.filterFindByG_LtD_S(
				groupId, displayDate, status, start, end);
		}
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
		else {
			return blogsEntryPersistence.filterFindByG_S(
				groupId, status, start, end);
		}
	}

	@Override
	public List<BlogsEntry> getGroupEntries(
		long groupId, int status, int start, int end,
		OrderByComparator<BlogsEntry> obc) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return blogsEntryPersistence.filterFindByG_NotS(
				groupId, WorkflowConstants.STATUS_IN_TRASH, start, end, obc);
		}
		else {
			return blogsEntryPersistence.filterFindByG_S(
				groupId, status, start, end, obc);
		}
	}

	@Override
	public int getGroupEntriesCount(
		long groupId, Date displayDate, int status) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return blogsEntryPersistence.filterCountByG_LtD_NotS(
				groupId, displayDate, WorkflowConstants.STATUS_IN_TRASH);
		}
		else {
			return blogsEntryPersistence.filterCountByG_LtD_S(
				groupId, displayDate, status);
		}
	}

	@Override
	public int getGroupEntriesCount(long groupId, int status) {
		if (status == WorkflowConstants.STATUS_ANY) {
			return blogsEntryPersistence.filterCountByG_NotS(
				groupId, WorkflowConstants.STATUS_IN_TRASH);
		}
		else {
			return blogsEntryPersistence.filterCountByG_S(groupId, status);
		}
	}

	@Override
	public String getGroupEntriesRSS(
			long groupId, Date displayDate, int status, int max, String type,
			double version, String displayStyle, String feedURL,
			String entryURL, ThemeDisplay themeDisplay)
		throws PortalException {

		Group group = groupPersistence.findByPrimaryKey(groupId);

		String name = group.getDescriptiveName();

		List<BlogsEntry> blogsEntries = getGroupEntries(
			groupId, displayDate, status, max);

		return exportToRSS(
			name, name, type, version, displayStyle, feedURL, entryURL,
			blogsEntries, themeDisplay);
	}

	@Override
	public List<BlogsEntry> getGroupsEntries(
			long companyId, long groupId, Date displayDate, int status, int max)
		throws PortalException {

		List<BlogsEntry> entries = new ArrayList<>();

		boolean listNotExhausted = true;

		QueryDefinition<BlogsEntry> queryDefinition = new QueryDefinition<>(
			status, false, 0, 0, new EntryDisplayDateComparator());

		if (status == WorkflowConstants.STATUS_ANY) {
			queryDefinition.setStatus(WorkflowConstants.STATUS_IN_TRASH, true);
		}

		while ((entries.size() < max) && listNotExhausted) {
			queryDefinition.setEnd(queryDefinition.getStart() + max);

			List<BlogsEntry> entryList =
				blogsEntryLocalService.getGroupsEntries(
					companyId, groupId, displayDate, queryDefinition);

			queryDefinition.setStart(queryDefinition.getStart() + max);

			listNotExhausted = entryList.size() == max;

			for (BlogsEntry entry : entryList) {
				if (entries.size() >= max) {
					break;
				}

				if (BlogsEntryPermission.contains(
						getPermissionChecker(), entry, ActionKeys.VIEW)) {

					entries.add(entry);
				}
			}
		}

		return entries;
	}

	@Override
	public List<BlogsEntry> getGroupUserEntries(
		long groupId, long userId, int status, int start, int end,
		OrderByComparator<BlogsEntry> obc) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return blogsEntryPersistence.filterFindByG_U_NotS(
				groupId, userId, WorkflowConstants.STATUS_IN_TRASH, start, end,
				obc);
		}
		else {
			return blogsEntryPersistence.filterFindByG_U_S(
				groupId, userId, status, start, end, obc);
		}
	}

	@Override
	public List<BlogsEntry> getGroupUserEntries(
		long groupId, long userId, int[] statuses, int start, int end,
		OrderByComparator<BlogsEntry> obc) {

		return blogsEntryPersistence.filterFindByG_U_S(
			groupId, userId, statuses, start, end, obc);
	}

	@Override
	public int getGroupUserEntriesCount(long groupId, long userId, int status) {
		if (status == WorkflowConstants.STATUS_ANY) {
			return blogsEntryPersistence.filterCountByG_U_NotS(
				groupId, userId, WorkflowConstants.STATUS_IN_TRASH);
		}
		else {
			return blogsEntryPersistence.filterCountByG_U_S(
				groupId, userId, status);
		}
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

		List<BlogsEntry> entries = new ArrayList<>();

		boolean listNotExhausted = true;

		QueryDefinition<BlogsEntry> queryDefinition = new QueryDefinition<>(
			status, false, 0, 0, new EntryDisplayDateComparator());

		if (status == WorkflowConstants.STATUS_ANY) {
			queryDefinition.setStatus(WorkflowConstants.STATUS_IN_TRASH, true);
		}

		while ((entries.size() < max) && listNotExhausted) {
			queryDefinition.setEnd(queryDefinition.getStart() + max);

			List<BlogsEntry> entryList = blogsEntryFinder.findByOrganizationId(
				organizationId, displayDate, queryDefinition);

			queryDefinition.setStart(queryDefinition.getStart() + max);

			listNotExhausted = entryList.size() == max;

			for (BlogsEntry entry : entryList) {
				if (entries.size() >= max) {
					break;
				}

				if (BlogsEntryPermission.contains(
						getPermissionChecker(), entry, ActionKeys.VIEW)) {

					entries.add(entry);
				}
			}
		}

		return entries;
	}

	@Override
	public String getOrganizationEntriesRSS(
			long organizationId, Date displayDate, int status, int max,
			String type, double version, String displayStyle, String feedURL,
			String entryURL, ThemeDisplay themeDisplay)
		throws PortalException {

		Organization organization = organizationPersistence.findByPrimaryKey(
			organizationId);

		String name = organization.getName();

		List<BlogsEntry> blogsEntries = getOrganizationEntries(
			organizationId, displayDate, status, max);

		return exportToRSS(
			name, name, type, version, displayStyle, feedURL, entryURL,
			blogsEntries, themeDisplay);
	}

	@Override
	public BlogsEntry moveEntryToTrash(long entryId) throws PortalException {
		BlogsEntryPermission.check(
			getPermissionChecker(), entryId, ActionKeys.DELETE);

		return blogsEntryLocalService.moveEntryToTrash(getUserId(), entryId);
	}

	@Override
	public void restoreEntryFromTrash(long entryId) throws PortalException {
		BlogsEntryPermission.check(
			getPermissionChecker(), entryId, ActionKeys.DELETE);

		blogsEntryLocalService.restoreEntryFromTrash(getUserId(), entryId);
	}

	@Override
	public void subscribe(long groupId) throws PortalException {
		BlogsPermission.check(
			getPermissionChecker(), groupId, ActionKeys.SUBSCRIBE);

		blogsEntryLocalService.subscribe(getUserId(), groupId);
	}

	@Override
	public void unsubscribe(long groupId) throws PortalException {
		BlogsPermission.check(
			getPermissionChecker(), groupId, ActionKeys.SUBSCRIBE);

		blogsEntryLocalService.unsubscribe(getUserId(), groupId);
	}

	/**
	 * @deprecated As of 7.0.0, replaced by {@link #updateEntry(long, String,
	 *             String, String, String, int, int, int, int, int, boolean,
	 *             boolean, String[], String, ImageSelector, ImageSelector,
	 *             ServiceContext)}
	 */
	@Deprecated
	@Override
	public BlogsEntry updateEntry(
			long entryId, String title, String description, String content,
			int displayDateMonth, int displayDateDay, int displayDateYear,
			int displayDateHour, int displayDateMinute, boolean allowPingbacks,
			boolean allowTrackbacks, String[] trackbacks, boolean smallImage,
			String smallImageURL, String smallImageFileName,
			InputStream smallImageInputStream, ServiceContext serviceContext)
		throws PortalException {

		BlogsPermission.check(
			getPermissionChecker(), serviceContext.getScopeGroupId(),
			ActionKeys.UPDATE);

		ImageSelector coverImageImageSelector = null;
		ImageSelector smallImageImageSelector = null;

		if (smallImage) {
			if (Validator.isNotNull(smallImageFileName) &&
				(smallImageInputStream != null)) {

				try {
					byte[] bytes = FileUtil.getBytes(smallImageInputStream);

					smallImageImageSelector = new ImageSelector(
						bytes, smallImageFileName,
						MimeTypesUtil.getContentType(smallImageFileName), null);
				}
				catch (IOException ioe) {
					_log.error("Unable to create image selector", ioe);
				}
			}
			else if (Validator.isNotNull(smallImageURL)) {
				smallImageImageSelector = new ImageSelector(smallImageURL);
			}
		}
		else {
			smallImageImageSelector = new ImageSelector();
		}

		return updateEntry(
			entryId, title, StringPool.BLANK, description, content,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, allowPingbacks, allowTrackbacks, trackbacks,
			StringPool.BLANK, coverImageImageSelector, smallImageImageSelector,
			serviceContext);
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

		BlogsEntryPermission.check(
			getPermissionChecker(), entryId, ActionKeys.UPDATE);

		return blogsEntryLocalService.updateEntry(
			getUserId(), entryId, title, subtitle, description, content,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, allowPingbacks, allowTrackbacks, trackbacks,
			coverImageCaption, coverImageImageSelector, smallImageImageSelector,
			serviceContext);
	}

	protected String exportToRSS(
		String name, String description, String type, double version,
		String displayStyle, String feedURL, String entryURL,
		List<BlogsEntry> blogsEntries, ThemeDisplay themeDisplay) {

		SyndFeed syndFeed = new SyndFeedImpl();

		syndFeed.setDescription(description);

		List<SyndEntry> syndEntries = new ArrayList<>();

		syndFeed.setEntries(syndEntries);

		for (BlogsEntry entry : blogsEntries) {
			SyndEntry syndEntry = new SyndEntryImpl();

			String author = PortalUtil.getUserName(entry);

			syndEntry.setAuthor(author);

			SyndContent syndContent = new SyndContentImpl();

			syndContent.setType(RSSUtil.ENTRY_TYPE_DEFAULT);

			String value = null;

			if (displayStyle.equals(RSSUtil.DISPLAY_STYLE_ABSTRACT)) {
				String summary = entry.getDescription();

				if (Validator.isNull(summary)) {
					summary = entry.getContent();
				}

				value = StringUtil.shorten(
					HtmlUtil.extractText(summary),
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

		SyndLink selfSyndLink = new SyndLinkImpl();

		syndLinks.add(selfSyndLink);

		selfSyndLink.setHref(feedURL);
		selfSyndLink.setRel("self");

		syndFeed.setPublishedDate(new Date());
		syndFeed.setTitle(name);
		syndFeed.setUri(feedURL);

		try {
			return RSSUtil.export(syndFeed);
		}
		catch (FeedException fe) {
			throw new SystemException(fe);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BlogsEntryServiceImpl.class);

}