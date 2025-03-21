/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector.taglib.servlet.taglib;

import com.liferay.document.library.display.context.DLMimeTypeDisplayContext;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorReturnTypeResolver;
import com.liferay.item.selector.constants.ItemSelectorPortletKeys;
import com.liferay.item.selector.taglib.ItemSelectorRepositoryEntryBrowserReturnTypeUtil;
import com.liferay.item.selector.taglib.internal.servlet.ServletContextUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.repository.model.RepositoryEntry;
import com.liferay.portal.kernel.upload.configuration.UploadServletRequestConfigurationProviderUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.taglib.util.IncludeTag;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Roberto Díaz
 */
public class RepositoryEntryBrowserTag extends IncludeTag {

	public static final String[] DISPLAY_STYLES = {
		"icon", "descriptive", "list"
	};

	public Set<String> getAllowedCreationMenuUIItemKeys() {
		return _allowedCreationMenuUIItemKeys;
	}

	public DLMimeTypeDisplayContext getDlMimeTypeDisplayContext() {
		return _dlMimeTypeDisplayContext;
	}

	public PortletURL getEditImageURL() {
		return _editImageURL;
	}

	public String getEmptyResultsMessage() {
		return _emptyResultsMessage;
	}

	public List<String> getExtensions() {
		return _extensions;
	}

	public long getFolderId() {
		return _folderId;
	}

	public String getItemSelectedEventName() {
		return _itemSelectedEventName;
	}

	public ItemSelectorReturnTypeResolver<?, ?>
		getItemSelectorReturnTypeResolver() {

		return _itemSelectorReturnTypeResolver;
	}

	public long getMaxFileSize() {
		return _maxFileSize;
	}

	public String getMimeTypeRestriction() {
		return _mimeTypeRestriction;
	}

	public PortletURL getPortletURL() {
		return _portletURL;
	}

	public List<RepositoryEntry> getRepositoryEntries() {
		return _repositoryEntries;
	}

	public int getRepositoryEntriesCount() {
		return _repositoryEntriesCount;
	}

	public String getTabName() {
		return _tabName;
	}

	public PortletURL getUploadURL() {
		return _uploadURL;
	}

	public boolean isShowBreadcrumb() {
		return _showBreadcrumb;
	}

	public boolean isShowDragAndDropZone() {
		return _showDragAndDropZone;
	}

	public boolean isShowSearch() {
		return _showSearch;
	}

	public void setAllowedCreationMenuUIItemKeys(
		Set<String> allowedCreationMenuUIItemKeys) {

		_allowedCreationMenuUIItemKeys = allowedCreationMenuUIItemKeys;
	}

	/**
	 * @deprecated As of Judson (7.1.x), with no direct replacement
	 */
	@Deprecated
	public void setDesiredItemSelectorReturnTypes(
		List<ItemSelectorReturnType> desiredItemSelectorReturnTypes) {

		_desiredItemSelectorReturnTypes = desiredItemSelectorReturnTypes;
	}

	public void setDisplayStyle(String displayStyle) {
		_displayStyle = displayStyle;
	}

	/**
	 * @deprecated As of Mueller (7.2.x), with no direct replacement
	 */
	@Deprecated
	public void setDlMimeTypeDisplayContext(
		DLMimeTypeDisplayContext dlMimeTypeDisplayContext) {

		_dlMimeTypeDisplayContext = dlMimeTypeDisplayContext;
	}

	public void setEditImageURL(PortletURL editImageURL) {
		_editImageURL = editImageURL;
	}

	public void setEmptyResultsMessage(String emptyResultsMessage) {
		_emptyResultsMessage = emptyResultsMessage;
	}

	public void setExtensions(List<String> extensions) {
		_extensions = extensions;
	}

	public void setFolderId(long folderId) {
		_folderId = folderId;
	}

	public void setItemSelectedEventName(String itemSelectedEventName) {
		_itemSelectedEventName = itemSelectedEventName;
	}

	public void setItemSelectorReturnTypeResolver(
		ItemSelectorReturnTypeResolver<?, ?> itemSelectorReturnTypeResolver) {

		_itemSelectorReturnTypeResolver = itemSelectorReturnTypeResolver;
	}

	public void setMaxFileSize(long maxFileSize) {
		_maxFileSize = maxFileSize;
	}

	public void setMimeTypeRestriction(String mimeTypeRestriction) {
		_mimeTypeRestriction = mimeTypeRestriction;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setPortletURL(PortletURL portletURL) {
		_portletURL = portletURL;
	}

	public void setRepositoryEntries(List<RepositoryEntry> repositoryEntries) {
		_repositoryEntries = repositoryEntries;
	}

	public void setRepositoryEntriesCount(int repositoryEntriesCount) {
		_repositoryEntriesCount = repositoryEntriesCount;
	}

	public void setShowBreadcrumb(boolean showBreadcrumb) {
		_showBreadcrumb = showBreadcrumb;
	}

	public void setShowDragAndDropZone(boolean showDragAndDropZone) {
		_showDragAndDropZone = showDragAndDropZone;
	}

	public void setShowSearch(boolean showSearch) {
		_showSearch = showSearch;
	}

	public void setTabName(String tabName) {
		_tabName = tabName;
	}

	public void setUploadURL(PortletURL uploadURL) {
		_uploadURL = uploadURL;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_allowedCreationMenuUIItemKeys = null;
		_desiredItemSelectorReturnTypes = null;
		_displayStyle = null;
		_dlMimeTypeDisplayContext = null;
		_editImageURL = null;
		_emptyResultsMessage = null;
		_extensions = new ArrayList<>();
		_folderId = 0;
		_itemSelectedEventName = null;
		_itemSelectorReturnTypeResolver = null;
		_maxFileSize =
			UploadServletRequestConfigurationProviderUtil.getMaxSize();
		_mimeTypeRestriction = null;
		_portletURL = null;
		_repositoryEntries = new ArrayList<>();
		_repositoryEntriesCount = 0;
		_showBreadcrumb = false;
		_showDragAndDropZone = true;
		_showSearch = true;
		_tabName = null;
		_uploadURL = null;
	}

	protected String getDisplayStyle() {
		HttpServletRequest httpServletRequest = getRequest();

		PortalPreferences portalPreferences =
			PortletPreferencesFactoryUtil.getPortalPreferences(
				httpServletRequest);

		String displayStyle = ParamUtil.getString(
			httpServletRequest, "displayStyle");

		if (Validator.isNotNull(displayStyle)) {
			displayStyle = getSafeDisplayStyle(displayStyle);

			portalPreferences.setValue(
				ItemSelectorPortletKeys.ITEM_SELECTOR, "display-style",
				displayStyle);

			return displayStyle;
		}

		if (Validator.isNotNull(_displayStyle)) {
			return getSafeDisplayStyle(_displayStyle);
		}

		return portalPreferences.getValue(
			ItemSelectorPortletKeys.ITEM_SELECTOR, "display-style",
			DISPLAY_STYLES[0]);
	}

	@Override
	protected String getPage() {
		return "/repository_entry_browser/page.jsp";
	}

	protected String getSafeDisplayStyle(String displayStyle) {
		if (ArrayUtil.contains(DISPLAY_STYLES, displayStyle)) {
			return displayStyle;
		}

		return DISPLAY_STYLES[0];
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-item-selector:repository-entry-browser:" +
				"allowedCreationMenuUIItemKeys",
			_allowedCreationMenuUIItemKeys);
		httpServletRequest.setAttribute(
			"liferay-item-selector:repository-entry-browser:displayStyle",
			getDisplayStyle());
		httpServletRequest.setAttribute(
			"liferay-item-selector:repository-entry-browser:" +
				"dlMimeTypeDisplayContext",
			_dlMimeTypeDisplayContext);
		httpServletRequest.setAttribute(
			"liferay-item-selector:repository-entry-browser:" +
				"emptyResultsMessage",
			_getEmptyResultsMessage(httpServletRequest));

		if (_desiredItemSelectorReturnTypes != null) {
			httpServletRequest.setAttribute(
				"liferay-item-selector:repository-entry-browser:" +
					"existingFileEntryReturnType",
				ItemSelectorRepositoryEntryBrowserReturnTypeUtil.
					getFirstAvailableExistingFileEntryReturnType(
						_desiredItemSelectorReturnTypes));
		}

		httpServletRequest.setAttribute(
			"liferay-item-selector:repository-entry-browser:editImageURL",
			_editImageURL);
		httpServletRequest.setAttribute(
			"liferay-item-selector:repository-entry-browser:extensions",
			_extensions);

		if (_mimeTypeRestriction != null) {
			httpServletRequest.setAttribute(
				"liferay-item-selector:repository-entry-browser:" +
					"mimeTypeRestriction",
				_mimeTypeRestriction);
		}

		httpServletRequest.setAttribute(
			"liferay-item-selector:repository-entry-browser:folderId",
			_folderId);
		httpServletRequest.setAttribute(
			"liferay-item-selector:repository-entry-browser:" +
				"itemSelectedEventName",
			_itemSelectedEventName);
		httpServletRequest.setAttribute(
			"liferay-item-selector:repository-entry-browser:" +
				"itemSelectorReturnTypeResolver",
			_itemSelectorReturnTypeResolver);
		httpServletRequest.setAttribute(
			"liferay-item-selector:repository-entry-browser:maxFileSize",
			_maxFileSize);
		httpServletRequest.setAttribute(
			"liferay-item-selector:repository-entry-browser:portletURL",
			_portletURL);
		httpServletRequest.setAttribute(
			"liferay-item-selector:repository-entry-browser:repositoryEntries",
			_repositoryEntries);
		httpServletRequest.setAttribute(
			"liferay-item-selector:repository-entry-browser:" +
				"repositoryEntriesCount",
			_repositoryEntriesCount);
		httpServletRequest.setAttribute(
			"liferay-item-selector:repository-entry-browser:showBreadcrumb",
			_showBreadcrumb);
		httpServletRequest.setAttribute(
			"liferay-item-selector:repository-entry-browser:" +
				"showDragAndDropZone",
			_isShownDragAndDropZone());
		httpServletRequest.setAttribute(
			"liferay-item-selector:repository-entry-browser:showSearch",
			_showSearch);
		httpServletRequest.setAttribute(
			"liferay-item-selector:repository-entry-browser:tabName", _tabName);
		httpServletRequest.setAttribute(
			"liferay-item-selector:repository-entry-browser:uploadURL",
			_uploadURL);
	}

	private String _getEmptyResultsMessage(
		HttpServletRequest httpServletRequest) {

		if (Validator.isNotNull(_emptyResultsMessage)) {
			return _emptyResultsMessage;
		}

		return LanguageUtil.get(httpServletRequest, "no-results-were-found");
	}

	private boolean _isShownDragAndDropZone() {
		if (_uploadURL == null) {
			return false;
		}

		return _showDragAndDropZone;
	}

	private Set<String> _allowedCreationMenuUIItemKeys;
	private List<ItemSelectorReturnType> _desiredItemSelectorReturnTypes;
	private String _displayStyle;
	private DLMimeTypeDisplayContext _dlMimeTypeDisplayContext;
	private PortletURL _editImageURL;
	private String _emptyResultsMessage;
	private List<String> _extensions = new ArrayList<>();
	private long _folderId;
	private String _itemSelectedEventName;
	private ItemSelectorReturnTypeResolver<?, ?>
		_itemSelectorReturnTypeResolver;
	private long _maxFileSize =
		UploadServletRequestConfigurationProviderUtil.getMaxSize();
	private String _mimeTypeRestriction;
	private PortletURL _portletURL;
	private List<RepositoryEntry> _repositoryEntries = new ArrayList<>();
	private int _repositoryEntriesCount;
	private boolean _showBreadcrumb;
	private boolean _showDragAndDropZone = true;
	private boolean _showSearch = true;
	private String _tabName;
	private PortletURL _uploadURL;

}