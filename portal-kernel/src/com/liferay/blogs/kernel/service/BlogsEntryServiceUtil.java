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

package com.liferay.blogs.kernel.service;

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.kernel.bean.PortalBeanLocatorUtil;
import com.liferay.portal.kernel.util.ReferenceRegistry;

/**
 * Provides the remote service utility for BlogsEntry. This utility wraps
 * {@link com.liferay.portlet.blogs.service.impl.BlogsEntryServiceImpl} and is the
 * primary access point for service operations in application layer code running
 * on a remote server. Methods of this service are expected to have security
 * checks based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see BlogsEntryService
 * @see com.liferay.portlet.blogs.service.base.BlogsEntryServiceBaseImpl
 * @see com.liferay.portlet.blogs.service.impl.BlogsEntryServiceImpl
 * @generated
 */
@ProviderType
public class BlogsEntryServiceUtil {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to {@link com.liferay.portlet.blogs.service.impl.BlogsEntryServiceImpl} and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	* @deprecated As of 7.0.0, replaced by {@link #addEntry(String, String,
	String, String, int, int, int, int, int, boolean, boolean,
	String[], String, ImageSelector, ImageSelector,
	ServiceContext)}
	*/
	@Deprecated
	public static com.liferay.blogs.kernel.model.BlogsEntry addEntry(
		String title, String description, String content, int displayDateMonth,
		int displayDateDay, int displayDateYear, int displayDateHour,
		int displayDateMinute, boolean allowPingbacks, boolean allowTrackbacks,
		String[] trackbacks, boolean smallImage, String smallImageURL,
		String smallImageFileName, java.io.InputStream smallImageInputStream,
		com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService()
				   .addEntry(title, description, content, displayDateMonth,
			displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, allowPingbacks, allowTrackbacks, trackbacks,
			smallImage, smallImageURL, smallImageFileName,
			smallImageInputStream, serviceContext);
	}

	public static com.liferay.blogs.kernel.model.BlogsEntry addEntry(
		String title, String subtitle, String description, String content,
		int displayDateMonth, int displayDateDay, int displayDateYear,
		int displayDateHour, int displayDateMinute, boolean allowPingbacks,
		boolean allowTrackbacks, String[] trackbacks, String coverImageCaption,
		com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector coverImageImageSelector,
		com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector smallImageImageSelector,
		com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService()
				   .addEntry(title, subtitle, description, content,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, allowPingbacks, allowTrackbacks, trackbacks,
			coverImageCaption, coverImageImageSelector,
			smallImageImageSelector, serviceContext);
	}

	public static void deleteEntry(long entryId)
		throws com.liferay.portal.kernel.exception.PortalException {
		getService().deleteEntry(entryId);
	}

	public static java.util.List<com.liferay.blogs.kernel.model.BlogsEntry> getCompanyEntries(
		long companyId, java.util.Date displayDate, int status, int max)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService()
				   .getCompanyEntries(companyId, displayDate, status, max);
	}

	public static String getCompanyEntriesRSS(long companyId,
		java.util.Date displayDate, int status, int max, String type,
		double version, String displayStyle, String feedURL, String entryURL,
		com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService()
				   .getCompanyEntriesRSS(companyId, displayDate, status, max,
			type, version, displayStyle, feedURL, entryURL, themeDisplay);
	}

	public static com.liferay.blogs.kernel.model.BlogsEntry[] getEntriesPrevAndNext(
		long entryId)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService().getEntriesPrevAndNext(entryId);
	}

	public static com.liferay.blogs.kernel.model.BlogsEntry getEntry(
		long entryId)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService().getEntry(entryId);
	}

	public static com.liferay.blogs.kernel.model.BlogsEntry getEntry(
		long groupId, String urlTitle)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService().getEntry(groupId, urlTitle);
	}

	public static java.util.List<com.liferay.blogs.kernel.model.BlogsEntry> getGroupEntries(
		long groupId, java.util.Date displayDate, int status, int max) {
		return getService().getGroupEntries(groupId, displayDate, status, max);
	}

	public static java.util.List<com.liferay.blogs.kernel.model.BlogsEntry> getGroupEntries(
		long groupId, java.util.Date displayDate, int status, int start, int end) {
		return getService()
				   .getGroupEntries(groupId, displayDate, status, start, end);
	}

	public static java.util.List<com.liferay.blogs.kernel.model.BlogsEntry> getGroupEntries(
		long groupId, int status, int max) {
		return getService().getGroupEntries(groupId, status, max);
	}

	public static java.util.List<com.liferay.blogs.kernel.model.BlogsEntry> getGroupEntries(
		long groupId, int status, int start, int end) {
		return getService().getGroupEntries(groupId, status, start, end);
	}

	public static java.util.List<com.liferay.blogs.kernel.model.BlogsEntry> getGroupEntries(
		long groupId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<com.liferay.blogs.kernel.model.BlogsEntry> obc) {
		return getService().getGroupEntries(groupId, status, start, end, obc);
	}

	public static int getGroupEntriesCount(long groupId,
		java.util.Date displayDate, int status) {
		return getService().getGroupEntriesCount(groupId, displayDate, status);
	}

	public static int getGroupEntriesCount(long groupId, int status) {
		return getService().getGroupEntriesCount(groupId, status);
	}

	public static String getGroupEntriesRSS(long groupId,
		java.util.Date displayDate, int status, int max, String type,
		double version, String displayStyle, String feedURL, String entryURL,
		com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService()
				   .getGroupEntriesRSS(groupId, displayDate, status, max, type,
			version, displayStyle, feedURL, entryURL, themeDisplay);
	}

	public static java.util.List<com.liferay.blogs.kernel.model.BlogsEntry> getGroupsEntries(
		long companyId, long groupId, java.util.Date displayDate, int status,
		int max) throws com.liferay.portal.kernel.exception.PortalException {
		return getService()
				   .getGroupsEntries(companyId, groupId, displayDate, status,
			max);
	}

	public static java.util.List<com.liferay.blogs.kernel.model.BlogsEntry> getGroupUserEntries(
		long groupId, long userId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<com.liferay.blogs.kernel.model.BlogsEntry> obc) {
		return getService()
				   .getGroupUserEntries(groupId, userId, status, start, end, obc);
	}

	public static java.util.List<com.liferay.blogs.kernel.model.BlogsEntry> getGroupUserEntries(
		long groupId, long userId, int[] statuses, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<com.liferay.blogs.kernel.model.BlogsEntry> obc) {
		return getService()
				   .getGroupUserEntries(groupId, userId, statuses, start, end,
			obc);
	}

	public static int getGroupUserEntriesCount(long groupId, long userId,
		int status) {
		return getService().getGroupUserEntriesCount(groupId, userId, status);
	}

	public static int getGroupUserEntriesCount(long groupId, long userId,
		int[] statuses) {
		return getService().getGroupUserEntriesCount(groupId, userId, statuses);
	}

	public static java.util.List<com.liferay.blogs.kernel.model.BlogsEntry> getOrganizationEntries(
		long organizationId, java.util.Date displayDate, int status, int max)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService()
				   .getOrganizationEntries(organizationId, displayDate, status,
			max);
	}

	public static String getOrganizationEntriesRSS(long organizationId,
		java.util.Date displayDate, int status, int max, String type,
		double version, String displayStyle, String feedURL, String entryURL,
		com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService()
				   .getOrganizationEntriesRSS(organizationId, displayDate,
			status, max, type, version, displayStyle, feedURL, entryURL,
			themeDisplay);
	}

	/**
	* Returns the OSGi service identifier.
	*
	* @return the OSGi service identifier
	*/
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static com.liferay.blogs.kernel.model.BlogsEntry moveEntryToTrash(
		long entryId)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService().moveEntryToTrash(entryId);
	}

	public static void restoreEntryFromTrash(long entryId)
		throws com.liferay.portal.kernel.exception.PortalException {
		getService().restoreEntryFromTrash(entryId);
	}

	public static void subscribe(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {
		getService().subscribe(groupId);
	}

	public static void unsubscribe(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {
		getService().unsubscribe(groupId);
	}

	/**
	* @deprecated As of 7.0.0, replaced by {@link #updateEntry(long, String,
	String, String, String, int, int, int, int, int, boolean,
	boolean, String[], String, ImageSelector, ImageSelector,
	ServiceContext)}
	*/
	@Deprecated
	public static com.liferay.blogs.kernel.model.BlogsEntry updateEntry(
		long entryId, String title, String description, String content,
		int displayDateMonth, int displayDateDay, int displayDateYear,
		int displayDateHour, int displayDateMinute, boolean allowPingbacks,
		boolean allowTrackbacks, String[] trackbacks, boolean smallImage,
		String smallImageURL, String smallImageFileName,
		java.io.InputStream smallImageInputStream,
		com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService()
				   .updateEntry(entryId, title, description, content,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, allowPingbacks, allowTrackbacks, trackbacks,
			smallImage, smallImageURL, smallImageFileName,
			smallImageInputStream, serviceContext);
	}

	public static com.liferay.blogs.kernel.model.BlogsEntry updateEntry(
		long entryId, String title, String subtitle, String description,
		String content, int displayDateMonth, int displayDateDay,
		int displayDateYear, int displayDateHour, int displayDateMinute,
		boolean allowPingbacks, boolean allowTrackbacks, String[] trackbacks,
		String coverImageCaption,
		com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector coverImageImageSelector,
		com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector smallImageImageSelector,
		com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService()
				   .updateEntry(entryId, title, subtitle, description, content,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, allowPingbacks, allowTrackbacks, trackbacks,
			coverImageCaption, coverImageImageSelector,
			smallImageImageSelector, serviceContext);
	}

	public static BlogsEntryService getService() {
		if (_service == null) {
			_service = (BlogsEntryService)PortalBeanLocatorUtil.locate(BlogsEntryService.class.getName());

			ReferenceRegistry.registerReference(BlogsEntryServiceUtil.class,
				"_service");
		}

		return _service;
	}

	private static BlogsEntryService _service;
}