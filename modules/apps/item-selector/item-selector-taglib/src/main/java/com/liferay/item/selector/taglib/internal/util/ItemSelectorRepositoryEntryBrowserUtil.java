/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector.taglib.internal.util;

import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppServiceUtil;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorReturnTypeResolver;
import com.liferay.item.selector.taglib.ItemSelectorRepositoryEntryBrowserReturnTypeUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ClassUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author Ambrín Chaudhary
 * @author Roberto Díaz
 */
public class ItemSelectorRepositoryEntryBrowserUtil {

	public static void addPortletBreadcrumbEntries(
			long folderId, String displayStyle,
			HttpServletRequest httpServletRequest,
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse,
			PortletURL portletURL)
		throws Exception {

		_addGroupSelectorBreadcrumbEntry(
			httpServletRequest, liferayPortletResponse, portletURL);

		portletURL.setParameter("displayStyle", displayStyle);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Group scopeGroup = themeDisplay.getScopeGroup();

		if (scopeGroup.isControlPanel()) {
			Company company = themeDisplay.getCompany();

			scopeGroup = company.getGroup();
		}

		_addPortletBreadcrumbEntry(
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, httpServletRequest,
			scopeGroup.getDescriptiveName(httpServletRequest.getLocale()),
			EntryURLUtil.getGroupPortletURL(scopeGroup, liferayPortletRequest));

		if (folderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			Folder folder = DLAppServiceUtil.getFolder(folderId);

			List<Folder> ancestorFolders = folder.getAncestors();

			Collections.reverse(ancestorFolders);

			for (Folder ancestorFolder : ancestorFolders) {
				_addPortletBreadcrumbEntry(
					ancestorFolder.getFolderId(), httpServletRequest,
					ancestorFolder.getName(), portletURL);
			}

			_addPortletBreadcrumbEntry(
				folder.getFolderId(), httpServletRequest, folder.getName(),
				portletURL);
		}
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             #addPortletBreadcrumbEntries(long, String,
	 *             HttpServletRequest, LiferayPortletRequest,
	 *             LiferayPortletResponse, PortletURL)}
	 */
	@Deprecated
	public static void addPortletBreadcrumbEntries(
			long folderId, String displayStyle,
			HttpServletRequest httpServletRequest,
			LiferayPortletResponse liferayPortletResponse,
			PortletURL portletURL)
		throws Exception {

		PortletRequest portletRequest =
			(PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_REQUEST);

		addPortletBreadcrumbEntries(
			folderId, displayStyle, httpServletRequest,
			PortalUtil.getLiferayPortletRequest(portletRequest),
			liferayPortletResponse, portletURL);
	}

	public static JSONObject getItemMetadataJSONObject(
			FileEntry fileEntry, Locale locale)
		throws PortalException {

		FileVersion latestFileVersion = fileEntry.getLatestFileVersion();

		return JSONUtil.put(
			"groups",
			JSONUtil.putAll(
				JSONUtil.put(
					"data",
					() -> {
						Date modifiedDate = fileEntry.getModifiedDate();

						return JSONUtil.putAll(
							_createJSONObject(
								LanguageUtil.get(locale, "format"),
								HtmlUtil.escape(
									latestFileVersion.getExtension())),
							_createJSONObject(
								LanguageUtil.get(locale, "size"),
								LanguageUtil.formatStorageSize(
									fileEntry.getSize(), locale)),
							_createJSONObject(
								LanguageUtil.get(locale, "name"),
								HtmlUtil.escape(
									DLUtil.getTitleWithExtension(fileEntry))),
							_createJSONObject(
								LanguageUtil.get(locale, "modified"),
								LanguageUtil.format(
									locale, "x-ago-by-x",
									new Object[] {
										LanguageUtil.getTimeDescription(
											locale,
											System.currentTimeMillis() -
												modifiedDate.getTime(),
											true),
										HtmlUtil.escape(fileEntry.getUserName())
									})));
					}
				).put(
					"title", LanguageUtil.get(locale, "file-info")
				),
				JSONUtil.put(
					"data",
					JSONUtil.putAll(
						_createJSONObject(
							LanguageUtil.get(locale, "version"),
							HtmlUtil.escape(latestFileVersion.getVersion())),
						_createJSONObject(
							LanguageUtil.get(locale, "status"),
							WorkflowConstants.getStatusLabel(
								latestFileVersion.getStatus())))
				).put(
					"title", LanguageUtil.get(locale, "version")
				)));
	}

	public static String getItemSelectorReturnTypeClassName(
		ItemSelectorReturnTypeResolver
			<? extends ItemSelectorReturnType, FileEntry>
				itemSelectorReturnTypeResolver,
		ItemSelectorReturnType itemSelectorReturnType) {

		if (itemSelectorReturnTypeResolver != null) {
			Class<? extends ItemSelectorReturnType>
				itemSelectorReturnTypeClass =
					itemSelectorReturnTypeResolver.
						getItemSelectorReturnTypeClass();

			return itemSelectorReturnTypeClass.getName();
		}

		return ClassUtil.getClassName(itemSelectorReturnType);
	}

	public static String getValue(
			ItemSelectorReturnTypeResolver
				<? extends ItemSelectorReturnType, FileEntry>
					itemSelectorReturnTypeResolver,
			ItemSelectorReturnType itemSelectorReturnType, FileEntry fileEntry,
			ThemeDisplay themeDisplay)
		throws Exception {

		if (itemSelectorReturnTypeResolver != null) {
			return itemSelectorReturnTypeResolver.getValue(
				fileEntry, themeDisplay);
		}

		return ItemSelectorRepositoryEntryBrowserReturnTypeUtil.getValue(
			itemSelectorReturnType, fileEntry, themeDisplay);
	}

	private static void _addGroupSelectorBreadcrumbEntry(
			HttpServletRequest httpServletRequest,
			LiferayPortletResponse liferayPortletResponse,
			PortletURL portletURL)
		throws Exception {

		PortalUtil.addPortletBreadcrumbEntry(
			httpServletRequest,
			LanguageUtil.get(httpServletRequest, "sites-and-libraries"),
			PortletURLBuilder.create(
				PortletURLUtil.clone(portletURL, liferayPortletResponse)
			).setParameter(
				"groupType", "site"
			).setParameter(
				"showGroupSelector", true
			).buildString());
	}

	private static void _addPortletBreadcrumbEntry(
		long folderId, HttpServletRequest httpServletRequest, String title,
		PortletURL portletURL) {

		portletURL.setParameter("folderId", String.valueOf(folderId));

		PortalUtil.addPortletBreadcrumbEntry(
			httpServletRequest, title, portletURL.toString());
	}

	private static JSONObject _createJSONObject(String key, String value) {
		return JSONUtil.put(
			"key", key
		).put(
			"value", value
		);
	}

}