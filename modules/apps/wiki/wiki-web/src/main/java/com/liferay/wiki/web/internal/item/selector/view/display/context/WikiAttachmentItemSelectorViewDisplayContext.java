/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.item.selector.view.display.context;

import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.document.library.kernel.util.DLValidatorUtil;
import com.liferay.item.selector.ItemSelectorReturnTypeResolver;
import com.liferay.item.selector.ItemSelectorReturnTypeResolverHandler;
import com.liferay.item.selector.taglib.servlet.taglib.util.RepositoryEntryBrowserTagUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.wiki.configuration.WikiFileUploadConfiguration;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.item.selector.WikiAttachmentItemSelectorCriterion;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiPageLocalServiceUtil;
import com.liferay.wiki.web.internal.item.selector.view.WikiAttachmentItemSelectorView;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;

/**
 * @author Roberto Díaz
 */
public class WikiAttachmentItemSelectorViewDisplayContext {

	public WikiAttachmentItemSelectorViewDisplayContext(
		HttpServletRequest httpServletRequest, String itemSelectedEventName,
		ItemSelectorReturnTypeResolverHandler
			itemSelectorReturnTypeResolverHandler,
		PortletURL portletURL, boolean search,
		WikiAttachmentItemSelectorCriterion wikiAttachmentItemSelectorCriterion,
		WikiAttachmentItemSelectorView wikiAttachmentItemSelectorView) {

		_httpServletRequest = httpServletRequest;
		_itemSelectedEventName = itemSelectedEventName;
		_itemSelectorReturnTypeResolverHandler =
			itemSelectorReturnTypeResolverHandler;
		_portletURL = portletURL;
		_search = search;
		_wikiAttachmentItemSelectorCriterion =
			wikiAttachmentItemSelectorCriterion;
		_wikiAttachmentItemSelectorView = wikiAttachmentItemSelectorView;

		_portalPreferences = PortletPreferencesFactoryUtil.getPortalPreferences(
			httpServletRequest);
	}

	public Set<String> getAllowedCreationMenuUIItemKeys() {
		return Collections.emptySet();
	}

	public PortletURL getEditImageURL(
		LiferayPortletResponse liferayPortletResponse) {

		return PortletURLBuilder.createActionURL(
			liferayPortletResponse, WikiPortletKeys.WIKI
		).setActionName(
			"/wiki/image_editor"
		).setParameter(
			"mimeTypes", _wikiAttachmentItemSelectorCriterion.getMimeTypes()
		).setParameter(
			"resourcePrimKey",
			_wikiAttachmentItemSelectorCriterion.getWikiPageResourceId()
		).buildPortletURL();
	}

	public String getItemSelectedEventName() {
		return _itemSelectedEventName;
	}

	public ItemSelectorReturnTypeResolver<?, ?>
		getItemSelectorReturnTypeResolver() {

		return _itemSelectorReturnTypeResolverHandler.
			getItemSelectorReturnTypeResolver(
				_wikiAttachmentItemSelectorCriterion,
				_wikiAttachmentItemSelectorView, FileEntry.class);
	}

	public String[] getMimeTypes() throws ConfigurationException {
		String[] mimeTypes =
			_wikiAttachmentItemSelectorCriterion.getMimeTypes();

		if (mimeTypes != null) {
			return mimeTypes;
		}

		WikiFileUploadConfiguration wikiFileUploadConfiguration =
			_getWikiFileUploadConfiguration();

		return wikiFileUploadConfiguration.attachmentMimeTypes();
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
		return _wikiAttachmentItemSelectorView.getTitle(locale);
	}

	public PortletURL getUploadURL(
		LiferayPortletResponse liferayPortletResponse) {

		return PortletURLBuilder.createActionURL(
			liferayPortletResponse, WikiPortletKeys.WIKI
		).setActionName(
			"/wiki/upload_page_attachment"
		).setParameter(
			"mimeTypes", _wikiAttachmentItemSelectorCriterion.getMimeTypes()
		).setParameter(
			"resourcePrimKey",
			_wikiAttachmentItemSelectorCriterion.getWikiPageResourceId()
		).buildPortletURL();
	}

	public WikiAttachmentItemSelectorCriterion
		getWikiAttachmentItemSelectorCriterion() {

		return _wikiAttachmentItemSelectorCriterion;
	}

	public long getWikiAttachmentMaxSize() throws ConfigurationException {
		WikiFileUploadConfiguration wikiFileUploadConfiguration =
			_getWikiFileUploadConfiguration();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return Math.min(
			wikiFileUploadConfiguration.attachmentMaxSize(),
			DLValidatorUtil.getMaxAllowableSize(
				themeDisplay.getScopeGroupId(), null));
	}

	public WikiPage getWikiPage() throws PortalException {
		return WikiPageLocalServiceUtil.getPage(
			_wikiAttachmentItemSelectorCriterion.getWikiPageResourceId());
	}

	public boolean isSearch() {
		return _search;
	}

	private WikiFileUploadConfiguration _getWikiFileUploadConfiguration()
		throws ConfigurationException {

		if (_wikiFileUploadConfiguration == null) {
			_wikiFileUploadConfiguration =
				ConfigurationProviderUtil.getSystemConfiguration(
					WikiFileUploadConfiguration.class);
		}

		return _wikiFileUploadConfiguration;
	}

	private final HttpServletRequest _httpServletRequest;
	private final String _itemSelectedEventName;
	private final ItemSelectorReturnTypeResolverHandler
		_itemSelectorReturnTypeResolverHandler;
	private final PortalPreferences _portalPreferences;
	private final PortletURL _portletURL;
	private final boolean _search;
	private final WikiAttachmentItemSelectorCriterion
		_wikiAttachmentItemSelectorCriterion;
	private final WikiAttachmentItemSelectorView
		_wikiAttachmentItemSelectorView;
	private WikiFileUploadConfiguration _wikiFileUploadConfiguration;

}