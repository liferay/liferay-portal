/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.test.util;

import com.liferay.bookmarks.constants.BookmarksFolderConstants;
import com.liferay.bookmarks.model.BookmarksEntry;
import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.bookmarks.service.BookmarksEntryLocalServiceUtil;
import com.liferay.bookmarks.service.BookmarksEntryServiceUtil;
import com.liferay.bookmarks.service.BookmarksFolderServiceUtil;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowThreadLocal;

/**
 * @author Brian Wing Shun Chan
 * @author Manuel de la Peña
 */
public class BookmarksTestUtil {

	public static BookmarksEntry addEntry(boolean approved) throws Exception {
		return addEntry(TestPropsValues.getGroupId(), approved);
	}

	public static BookmarksEntry addEntry(long groupId, boolean approved)
		throws Exception {

		return addEntry(
			BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID, approved,
			ServiceContextTestUtil.getServiceContext(groupId));
	}

	public static BookmarksEntry addEntry(
			long folderId, boolean approved, ServiceContext serviceContext)
		throws Exception {

		return addEntry("Test Entry", folderId, approved, serviceContext);
	}

	public static BookmarksEntry addEntry(
			String name, long folderId, boolean approved,
			ServiceContext serviceContext)
		throws Exception {

		boolean workflowEnabled = WorkflowThreadLocal.isEnabled();

		try {
			WorkflowThreadLocal.setEnabled(true);

			String url = "http://www.liferay.com";
			String description = "This is a test entry.";

			serviceContext = (ServiceContext)serviceContext.clone();

			serviceContext.setWorkflowAction(
				WorkflowConstants.ACTION_SAVE_DRAFT);

			BookmarksEntry entry = BookmarksEntryServiceUtil.addEntry(
				serviceContext.getScopeGroupId(), folderId, name, url,
				description, serviceContext);

			serviceContext.setCommand(Constants.ADD);
			serviceContext.setLayoutFullURL("http://localhost");

			if (approved) {
				entry.setStatus(WorkflowConstants.STATUS_APPROVED);

				entry = BookmarksEntryServiceUtil.updateEntry(
					entry.getEntryId(), serviceContext.getScopeGroupId(),
					entry.getFolderId(), entry.getName(), entry.getUrl(),
					entry.getUrl(), serviceContext);
			}

			return entry;
		}
		finally {
			WorkflowThreadLocal.setEnabled(workflowEnabled);
		}
	}

	public static BookmarksFolder addFolder(
			long groupId, long parentFolderId, String name)
		throws Exception {

		return addFolder(
			parentFolderId, name,
			ServiceContextTestUtil.getServiceContext(groupId));
	}

	public static BookmarksFolder addFolder(long groupId, String name)
		throws Exception {

		return addFolder(
			groupId, BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID, name);
	}

	public static BookmarksFolder addFolder(
			long parentFolderId, String name, ServiceContext serviceContext)
		throws Exception {

		String description = "This is a test folder.";

		return BookmarksFolderServiceUtil.addFolder(
			parentFolderId, name, description, serviceContext);
	}

	public static BookmarksFolder addFolder(String name) throws Exception {
		return addFolder(TestPropsValues.getGroupId(), name);
	}

	public static SearchContext getSearchContext(
		long companyId, long groupId, long folderId, String keywords) {

		return getSearchContext(
			companyId, groupId, folderId, keywords, false, false);
	}

	public static SearchContext getSearchContext(
		long companyId, long groupId, long folderId, String keywords,
		boolean highlight, boolean score) {

		SearchContext searchContext = new SearchContext();

		searchContext.setCompanyId(companyId);
		searchContext.setFolderIds(new long[] {folderId});
		searchContext.setGroupIds(new long[] {groupId});
		searchContext.setKeywords(keywords);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(highlight);
		queryConfig.setScoreEnabled(score);

		return searchContext;
	}

	public static void populateNotificationsServiceContext(
			ServiceContext serviceContext, String command)
		throws Exception {

		serviceContext.setAttribute("entryURL", "http://localhost");

		if (Validator.isNotNull(command)) {
			serviceContext.setCommand(command);
		}

		serviceContext.setLayoutFullURL("http://localhost");
	}

	public static BookmarksEntry updateEntry(BookmarksEntry entry)
		throws Exception {

		return updateEntry(entry, RandomTestUtil.randomString());
	}

	public static BookmarksEntry updateEntry(BookmarksEntry entry, String name)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(entry.getGroupId());

		serviceContext.setCommand(Constants.UPDATE);
		serviceContext.setLayoutFullURL("http://localhost");

		return BookmarksEntryLocalServiceUtil.updateEntry(
			TestPropsValues.getUserId(), entry.getEntryId(), entry.getGroupId(),
			entry.getFolderId(), name, entry.getUrl(), entry.getDescription(),
			serviceContext);
	}

}