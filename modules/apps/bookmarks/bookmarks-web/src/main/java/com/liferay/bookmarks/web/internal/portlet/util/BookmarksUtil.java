/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.web.internal.portlet.util;

import com.liferay.bookmarks.constants.BookmarksFolderConstants;
import com.liferay.bookmarks.model.BookmarksEntry;
import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.bookmarks.service.BookmarksEntryLocalServiceUtil;
import com.liferay.bookmarks.service.BookmarksFolderLocalServiceUtil;
import com.liferay.bookmarks.util.comparator.EntryCreateDateComparator;
import com.liferay.bookmarks.util.comparator.EntryModifiedDateComparator;
import com.liferay.bookmarks.util.comparator.EntryNameComparator;
import com.liferay.bookmarks.util.comparator.EntryPriorityComparator;
import com.liferay.bookmarks.util.comparator.EntryURLComparator;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 */
public class BookmarksUtil {

	public static void addPortletBreadcrumbEntries(
			BookmarksEntry entry, HttpServletRequest httpServletRequest,
			RenderResponse renderResponse)
		throws Exception {

		BookmarksFolder folder = entry.getFolder();

		if (folder.getFolderId() !=
				BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID) {

			addPortletBreadcrumbEntries(
				folder, httpServletRequest, renderResponse);
		}
	}

	public static void addPortletBreadcrumbEntries(
			BookmarksFolder folder, HttpServletRequest httpServletRequest,
			RenderResponse renderResponse)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String mvcRenderCommandName = ParamUtil.getString(
			httpServletRequest, "mvcRenderCommandName");

		PortletURL portletURL = renderResponse.createRenderURL();

		if (mvcRenderCommandName.equals("/bookmarks/select_folder")) {
			portletURL.setParameter(
				"mvcRenderCommandName", "/bookmarks/select_folder");
			portletURL.setWindowState(LiferayWindowState.POP_UP);
		}
		else {
			portletURL.setParameter("mvcRenderCommandName", "/bookmarks/view");
		}

		PortalUtil.addPortletBreadcrumbEntry(
			httpServletRequest, themeDisplay.translate("home"),
			portletURL.toString());

		if (folder == null) {
			return;
		}

		if (!mvcRenderCommandName.equals("/bookmarks/select_folder")) {
			portletURL.setParameter(
				"mvcRenderCommandName", "/bookmarks/view_folder");
		}

		List<BookmarksFolder> ancestorFolders = folder.getAncestors();

		Collections.reverse(ancestorFolders);

		for (BookmarksFolder ancestorFolder : ancestorFolders) {
			portletURL.setParameter(
				"folderId", String.valueOf(ancestorFolder.getFolderId()));

			PortalUtil.addPortletBreadcrumbEntry(
				httpServletRequest, ancestorFolder.getName(),
				portletURL.toString());
		}

		portletURL.setParameter(
			"folderId", String.valueOf(folder.getFolderId()));

		if (folder.getFolderId() !=
				BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID) {

			BookmarksFolder unescapedFolder = folder.toUnescapedModel();

			PortalUtil.addPortletBreadcrumbEntry(
				httpServletRequest, unescapedFolder.getName(),
				portletURL.toString());
		}
	}

	public static void addPortletBreadcrumbEntries(
			long folderId, HttpServletRequest httpServletRequest,
			RenderResponse renderResponse)
		throws Exception {

		if (folderId == BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return;
		}

		addPortletBreadcrumbEntries(
			BookmarksFolderLocalServiceUtil.getFolder(folderId),
			httpServletRequest, renderResponse);
	}

	public static Map<String, String> getEmailDefinitionTerms(
		PortletRequest portletRequest, String emailFromAddress,
		String emailFromName) {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return LinkedHashMapBuilder.put(
			"[$BOOKMARKS_ENTRY_USER_NAME$]",
			LanguageUtil.get(
				themeDisplay.getLocale(),
				"the-user-who-added-the-bookmark-entry")
		).put(
			"[$BOOKMARKS_ENTRY_STATUS_BY_USER_NAME$]",
			LanguageUtil.get(
				themeDisplay.getLocale(),
				"the-user-who-updated-the-bookmark-entry")
		).put(
			"[$BOOKMARKS_ENTRY_URL$]",
			LanguageUtil.get(themeDisplay.getLocale(), "the-bookmark-entry-url")
		).put(
			"[$FROM_ADDRESS$]", HtmlUtil.escape(emailFromAddress)
		).put(
			"[$FROM_NAME$]", HtmlUtil.escape(emailFromName)
		).put(
			"[$PORTAL_URL$]",
			() -> {
				Company company = themeDisplay.getCompany();

				return company.getVirtualHostname();
			}
		).put(
			"[$PORTLET_NAME$]",
			() -> {
				PortletDisplay portletDisplay =
					themeDisplay.getPortletDisplay();

				return HtmlUtil.escape(portletDisplay.getTitle());
			}
		).put(
			"[$TO_ADDRESS$]",
			LanguageUtil.get(
				themeDisplay.getLocale(), "the-address-of-the-email-recipient")
		).put(
			"[$TO_NAME$]",
			LanguageUtil.get(
				themeDisplay.getLocale(), "the-name-of-the-email-recipient")
		).build();
	}

	public static List<Object> getEntries(Hits hits) {
		List<Object> entries = new ArrayList<>();

		for (Document document : hits.getDocs()) {
			String entryClassName = document.get(Field.ENTRY_CLASS_NAME);
			long entryClassPK = GetterUtil.getLong(
				document.get(Field.ENTRY_CLASS_PK));

			try {
				Object object = null;

				if (entryClassName.equals(BookmarksEntry.class.getName())) {
					object = BookmarksEntryLocalServiceUtil.getEntry(
						entryClassPK);
				}
				else if (entryClassName.equals(
							BookmarksFolder.class.getName())) {

					object = BookmarksFolderLocalServiceUtil.getFolder(
						entryClassPK);
				}

				entries.add(object);
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Bookmarks search index is stale and contains entry " +
							entryClassPK,
						exception);
				}
			}
		}

		return entries;
	}

	public static OrderByComparator<BookmarksEntry> getEntryOrderByComparator(
		String orderByCol, String orderByType) {

		boolean orderByAsc = false;

		if (orderByType.equals("asc")) {
			orderByAsc = true;
		}

		OrderByComparator<BookmarksEntry> orderByComparator = null;

		if (orderByCol.equals("create-date")) {
			orderByComparator = EntryCreateDateComparator.getInstance(
				orderByAsc);
		}
		else if (orderByCol.equals("modified-date")) {
			orderByComparator = new EntryModifiedDateComparator(orderByAsc);
		}
		else if (orderByCol.equals("name")) {
			orderByComparator = EntryNameComparator.getInstance(orderByAsc);
		}
		else if (orderByCol.equals("priority")) {
			orderByComparator = EntryPriorityComparator.getInstance(orderByAsc);
		}
		else if (orderByCol.equals("url")) {
			orderByComparator = EntryURLComparator.getInstance(orderByAsc);
		}

		return orderByComparator;
	}

	private static final Log _log = LogFactoryUtil.getLog(BookmarksUtil.class);

}