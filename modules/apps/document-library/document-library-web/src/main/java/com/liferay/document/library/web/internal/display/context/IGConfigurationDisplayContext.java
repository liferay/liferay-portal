/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.display.context;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.exception.NoSuchFolderException;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.web.internal.display.context.helper.DLPortletInstanceSettingsHelper;
import com.liferay.document.library.web.internal.display.context.helper.IGRequestHelper;
import com.liferay.document.library.web.internal.util.DLFolderUtil;
import com.liferay.document.library.web.internal.util.FolderItemSelectorURLProvider;
import com.liferay.item.selector.ItemSelector;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.repository.capabilities.TrashCapability;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.trash.TrashHelper;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Adolfo Pérez
 */
public class IGConfigurationDisplayContext {

	public IGConfigurationDisplayContext(
		ItemSelector itemSelector, HttpServletRequest httpServletRequest,
		PortletPreferencesLocalService portletPreferencesLocalService,
		TrashHelper trashHelper) {

		_itemSelector = itemSelector;
		_httpServletRequest = httpServletRequest;
		_portletPreferencesLocalService = portletPreferencesLocalService;
		_trashHelper = trashHelper;

		IGRequestHelper igRequestHelper = new IGRequestHelper(
			_httpServletRequest);

		_dlPortletInstanceSettingsHelper = new DLPortletInstanceSettingsHelper(
			igRequestHelper);
		_igRequestHelper = igRequestHelper;

		_renderRequest = (RenderRequest)httpServletRequest.getAttribute(
			JavaConstants.JAKARTA_PORTLET_REQUEST);
		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<KeyValuePair> getAvailableMimeTypes() {
		return _dlPortletInstanceSettingsHelper.getAvailableMimeTypes();
	}

	public List<KeyValuePair> getCurrentMimeTypes() {
		return _dlPortletInstanceSettingsHelper.getCurrentMimeTypes();
	}

	public String getDisplayStyle() {
		PortletPreferences portletPreferences = _getPortletPreferences();

		return portletPreferences.getValue("displayStyle", StringPool.BLANK);
	}

	public long getDisplayStyleGroupId() {
		PortletPreferences portletPreferences = _getPortletPreferences();

		return GetterUtil.getLong(
			portletPreferences.getValue("displayStyleGroupId", null),
			_themeDisplay.getScopeGroupId());
	}

	public String getItemSelectedEventName() {
		PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

		return portletDisplay.getNamespace() + "folderSelected";
	}

	public long getRootFolderId() throws PortalException {
		_initFolder();

		return _folderId;
	}

	public String getRootFolderName() throws PortalException {
		_initFolder();

		return _folderName;
	}

	public long getSelectedRepositoryId() throws PortalException {
		_initRepository();

		return _selectedRepositoryId;
	}

	public String getSelectRootFolderURL() throws PortalException {
		FolderItemSelectorURLProvider folderItemSelectorURLProvider =
			new FolderItemSelectorURLProvider(
				_httpServletRequest, _itemSelector);

		return folderItemSelectorURLProvider.getSelectRootFolderURL(
			getSelectedRepositoryId(), getRootFolderId());
	}

	public boolean isRootFolderInTrash() throws PortalException {
		_initFolder();

		return _folderInTrash;
	}

	public boolean isRootFolderNotFound() throws PortalException {
		_initFolder();

		return _folderNotFound;
	}

	public boolean isShowActions() {
		return _dlPortletInstanceSettingsHelper.isShowActions();
	}

	private PortletPreferences _getPortletPreferences() {
		if (_portletPreferences != null) {
			return _portletPreferences;
		}

		Layout layout = _themeDisplay.getLayout();

		if (layout.isTypeControlPanel()) {
			_portletPreferences =
				_portletPreferencesLocalService.getPreferences(
					_themeDisplay.getCompanyId(),
					_themeDisplay.getScopeGroupId(),
					PortletKeys.PREFS_OWNER_TYPE_GROUP, 0,
					DLPortletKeys.DOCUMENT_LIBRARY, null);
		}
		else {
			_portletPreferences = _renderRequest.getPreferences();
		}

		return _portletPreferences;
	}

	private void _initFolder() throws PortalException {
		if (_folderId != null) {
			return;
		}

		_folderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;
		_folderInTrash = false;
		_folderName = StringPool.BLANK;

		_folderNotFound = false;

		try {
			_folder = _dlPortletInstanceSettingsHelper.getRootFolder();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			_folderNotFound = true;

			return;
		}

		if (_folder == null) {
			_folderName = LanguageUtil.get(_httpServletRequest, "home");

			return;
		}

		_folderId = _folder.getFolderId();

		_folderName = _folder.getName();

		if (_folder.isRepositoryCapabilityProvided(TrashCapability.class)) {
			TrashCapability trashCapability = _folder.getRepositoryCapability(
				TrashCapability.class);

			_folderInTrash = trashCapability.isInTrash(_folder);

			if (_folderInTrash) {
				_folderName = _trashHelper.getOriginalTitle(_folder.getName());
			}
		}

		try {
			DLFolderUtil.validateDepotFolder(
				_folderId, _folder.getGroupId(),
				_themeDisplay.getScopeGroupId());
		}
		catch (NoSuchFolderException noSuchFolderException) {
			if (_log.isWarnEnabled()) {
				_log.warn(noSuchFolderException);
			}

			_folderNotFound = true;
		}
	}

	private void _initRepository() throws PortalException {
		if (_selectedRepositoryId != 0) {
			return;
		}

		_selectedRepositoryId =
			_dlPortletInstanceSettingsHelper.getSelectedRepositoryId();

		if (_selectedRepositoryId != 0) {
			return;
		}

		_initFolder();

		if (_folder != null) {
			_selectedRepositoryId = _folder.getRepositoryId();
		}
		else {
			_selectedRepositoryId = ParamUtil.getLong(
				_httpServletRequest, "repositoryId",
				_themeDisplay.getScopeGroupId());
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		IGConfigurationDisplayContext.class);

	private final DLPortletInstanceSettingsHelper
		_dlPortletInstanceSettingsHelper;
	private Folder _folder;
	private Long _folderId;
	private boolean _folderInTrash;
	private String _folderName;
	private boolean _folderNotFound;
	private final HttpServletRequest _httpServletRequest;
	private final IGRequestHelper _igRequestHelper;
	private final ItemSelector _itemSelector;
	private PortletPreferences _portletPreferences;
	private final PortletPreferencesLocalService
		_portletPreferencesLocalService;
	private final RenderRequest _renderRequest;
	private long _selectedRepositoryId;
	private final ThemeDisplay _themeDisplay;
	private final TrashHelper _trashHelper;

}