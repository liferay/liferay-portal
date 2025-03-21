/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.item.selector.web.internal.display.context;

import com.liferay.blogs.configuration.BlogsFileUploadsConfiguration;
import com.liferay.blogs.item.selector.BlogsItemSelectorCriterion;
import com.liferay.blogs.item.selector.web.internal.BlogsItemSelectorView;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.item.selector.ItemSelectorReturnTypeResolver;
import com.liferay.item.selector.ItemSelectorReturnTypeResolverHandler;
import com.liferay.item.selector.taglib.servlet.taglib.util.RepositoryEntryBrowserTagUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PortletKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;

/**
 * @author Roberto Díaz
 */
public class BlogsItemSelectorViewDisplayContext {

	public BlogsItemSelectorViewDisplayContext(
		BlogsEntryLocalService blogsEntryLocalService,
		BlogsItemSelectorCriterion blogsItemSelectorCriterion,
		BlogsItemSelectorView blogsItemSelectorView,
		HttpServletRequest httpServletRequest, String itemSelectedEventName,
		ItemSelectorReturnTypeResolverHandler
			itemSelectorReturnTypeResolverHandler,
		PortletURL portletURL, boolean search) {

		_blogsEntryLocalService = blogsEntryLocalService;
		_blogsItemSelectorCriterion = blogsItemSelectorCriterion;
		_blogsItemSelectorView = blogsItemSelectorView;
		_httpServletRequest = httpServletRequest;
		_itemSelectedEventName = itemSelectedEventName;
		_itemSelectorReturnTypeResolverHandler =
			itemSelectorReturnTypeResolverHandler;
		_portletURL = portletURL;
		_search = search;

		_portalPreferences = PortletPreferencesFactoryUtil.getPortalPreferences(
			httpServletRequest);
	}

	public Folder fetchAttachmentsFolder(long userId, long groupId) {
		return _blogsEntryLocalService.fetchAttachmentsFolder(userId, groupId);
	}

	public Set<String> getAllowedCreationMenuUIItemKeys() {
		return Collections.emptySet();
	}

	public PortletURL getEditImageURL(
		LiferayPortletResponse liferayPortletResponse) {

		return PortletURLBuilder.createActionURL(
			liferayPortletResponse, PortletKeys.BLOGS
		).setActionName(
			"/blogs/image_editor"
		).buildPortletURL();
	}

	public String[] getImageExtensions() throws ConfigurationException {
		return _getBlogsFileUploadsConfiguration().imageExtensions();
	}

	public long getImageMaxSize() throws ConfigurationException {
		return _getBlogsFileUploadsConfiguration().imageMaxSize();
	}

	public String getItemSelectedEventName() {
		return _itemSelectedEventName;
	}

	public ItemSelectorReturnTypeResolver<?, ?>
		getItemSelectorReturnTypeResolver() {

		return _itemSelectorReturnTypeResolverHandler.
			getItemSelectorReturnTypeResolver(
				_blogsItemSelectorCriterion, _blogsItemSelectorView,
				FileEntry.class);
	}

	public String getMimeTypeRestriction() {
		return _blogsItemSelectorCriterion.getMimeTypeRestriction();
	}

	public OrderByComparator<FileEntry> getOrderByComparator() {
		return DLUtil.getRepositoryModelOrderByComparator(
			RepositoryEntryBrowserTagUtil.getOrderByCol(
				_httpServletRequest, _portalPreferences),
			RepositoryEntryBrowserTagUtil.getOrderByType(
				_httpServletRequest, _portalPreferences));
	}

	public PortletURL getPortletURL(
			HttpServletRequest httpServletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws PortletException {

		return PortletURLBuilder.create(
			PortletURLUtil.clone(_portletURL, liferayPortletResponse)
		).setParameter(
			"selectedTab", getTitle(httpServletRequest.getLocale())
		).buildPortletURL();
	}

	public String getTitle(Locale locale) {
		return _blogsItemSelectorView.getTitle(locale);
	}

	public PortletURL getUploadURL(
		LiferayPortletResponse liferayPortletResponse) {

		return PortletURLBuilder.createActionURL(
			liferayPortletResponse, PortletKeys.BLOGS
		).setActionName(
			"/blogs/upload_image"
		).buildPortletURL();
	}

	public boolean isSearch() {
		return _search;
	}

	public boolean showDragAndDropZone(ThemeDisplay themeDisplay) {
		if (FeatureFlagManagerUtil.isEnabled(
				themeDisplay.getCompanyId(), "LPD-29516")) {

			return true;
		}

		return !WorkflowDefinitionLinkLocalServiceUtil.
			hasWorkflowDefinitionLink(
				themeDisplay.getCompanyId(), themeDisplay.getScopeGroupId(),
				BlogsEntry.class.getName());
	}

	private BlogsFileUploadsConfiguration _getBlogsFileUploadsConfiguration()
		throws ConfigurationException {

		if (_blogsFileUploadsConfiguration == null) {
			_blogsFileUploadsConfiguration =
				ConfigurationProviderUtil.getSystemConfiguration(
					BlogsFileUploadsConfiguration.class);
		}

		return _blogsFileUploadsConfiguration;
	}

	private final BlogsEntryLocalService _blogsEntryLocalService;
	private BlogsFileUploadsConfiguration _blogsFileUploadsConfiguration;
	private final BlogsItemSelectorCriterion _blogsItemSelectorCriterion;
	private final BlogsItemSelectorView _blogsItemSelectorView;
	private final HttpServletRequest _httpServletRequest;
	private final String _itemSelectedEventName;
	private final ItemSelectorReturnTypeResolverHandler
		_itemSelectorReturnTypeResolverHandler;
	private final PortalPreferences _portalPreferences;
	private final PortletURL _portletURL;
	private final boolean _search;

}