/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.toolbar.contributor.helper;

import com.liferay.ai.creator.openai.display.context.factory.AICreatorOpenAIMenuItemFactory;
import com.liferay.depot.group.provider.SiteConnectedGroupGroupProvider;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.display.context.DLUIItemKeys;
import com.liferay.document.library.icon.provider.DLFileEntryTypeIconProvider;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeService;
import com.liferay.document.library.visibility.controller.DLFileEntryTypeVisibilityController;
import com.liferay.document.library.web.internal.security.permission.resource.DLFolderPermission;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.servlet.taglib.ui.MenuItem;
import com.liferay.portal.kernel.servlet.taglib.ui.URLMenuItem;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowStateException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(service = MenuItemProvider.class)
public class MenuItemProvider {

	public List<MenuItem> getAddDocumentTypesMenuItems(
		Folder folder, ThemeDisplay themeDisplay,
		PortletRequest portletRequest) {

		if (!_hasPermission(
				themeDisplay.getPermissionChecker(),
				themeDisplay.getScopeGroupId(), _getFolderId(folder),
				ActionKeys.ADD_DOCUMENT)) {

			return Collections.emptyList();
		}

		List<MenuItem> menuItems = new ArrayList<>();

		long repositoryId = _getRepositoryId(folder, themeDisplay);

		if (themeDisplay.getScopeGroupId() == repositoryId) {
			menuItems.addAll(
				_getPortletTitleAddDocumentTypeMenuItems(
					folder, themeDisplay, portletRequest));
		}

		return menuItems;
	}

	public MenuItem getAddFileMenuItem(
		Folder folder, ThemeDisplay themeDisplay,
		PortletRequest portletRequest) {

		long folderId = _getFolderId(folder);

		if (!_hasPermission(
				themeDisplay.getPermissionChecker(),
				themeDisplay.getScopeGroupId(), folderId,
				ActionKeys.ADD_DOCUMENT)) {

			return null;
		}

		URLMenuItem urlMenuItem = new URLMenuItem();

		urlMenuItem.setIcon("upload");
		urlMenuItem.setKey(DLUIItemKeys.UPLOAD);
		urlMenuItem.setLabel(
			_language.get(
				_portal.getHttpServletRequest(portletRequest), "file-upload"));
		urlMenuItem.setURL(
			PortletURLBuilder.create(
				_getPortletURL(themeDisplay, portletRequest)
			).setMVCRenderCommandName(
				"/document_library/edit_file_entry"
			).setCMD(
				Constants.ADD
			).setRedirect(
				_portal.getCurrentURL(portletRequest)
			).setPortletResource(
				() -> {
					PortletDisplay portletDisplay =
						themeDisplay.getPortletDisplay();

					return portletDisplay.getId();
				}
			).setParameter(
				"fileEntryTypeId", _getDefaultFileEntryTypeId(folderId)
			).setParameter(
				"folderId", folderId
			).setParameter(
				"repositoryId", _getRepositoryId(folder, themeDisplay)
			).buildString());

		return urlMenuItem;
	}

	public MenuItem getAddFolderMenuItem(
		Folder folder, ThemeDisplay themeDisplay,
		PortletRequest portletRequest) {

		long folderId = _getFolderId(folder);

		if (!_hasPermission(
				themeDisplay.getPermissionChecker(),
				themeDisplay.getScopeGroupId(), folderId,
				ActionKeys.ADD_FOLDER)) {

			return null;
		}

		URLMenuItem urlMenuItem = new URLMenuItem();

		urlMenuItem.setIcon("folder");
		urlMenuItem.setKey(DLUIItemKeys.ADD_FOLDER);
		urlMenuItem.setLabel(
			_language.get(
				_portal.getHttpServletRequest(portletRequest), "folder"));
		urlMenuItem.setURL(
			PortletURLBuilder.create(
				_getPortletURL(themeDisplay, portletRequest)
			).setMVCRenderCommandName(
				"/document_library/edit_folder"
			).setRedirect(
				_portal.getCurrentURL(portletRequest)
			).setPortletResource(
				() -> {
					PortletDisplay portletDisplay =
						themeDisplay.getPortletDisplay();

					return portletDisplay.getId();
				}
			).setParameter(
				"ignoreRootFolder", true
			).setParameter(
				"parentFolderId", folderId
			).setParameter(
				"repositoryId", _getRepositoryId(folder, themeDisplay)
			).buildString());

		return urlMenuItem;
	}

	public MenuItem getAddMultipleFilesMenuItem(
		Folder folder, ThemeDisplay themeDisplay,
		PortletRequest portletRequest) {

		if ((folder != null) && !folder.isSupportsMultipleUpload()) {
			return null;
		}

		long folderId = _getFolderId(folder);

		if (!_hasPermission(
				themeDisplay.getPermissionChecker(),
				themeDisplay.getScopeGroupId(), folderId,
				ActionKeys.ADD_DOCUMENT)) {

			return null;
		}

		URLMenuItem urlMenuItem = new URLMenuItem();

		urlMenuItem.setIcon("upload-multiple");

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", themeDisplay.getLocale(), getClass());

		urlMenuItem.setLabel(
			_language.get(resourceBundle, "multiple-files-upload"));

		urlMenuItem.setURL(
			PortletURLBuilder.create(
				_getPortletURL(themeDisplay, portletRequest)
			).setMVCRenderCommandName(
				"/document_library/upload_multiple_file_entries"
			).setRedirect(
				_portal.getCurrentURL(portletRequest)
			).setPortletResource(
				() -> {
					PortletDisplay portletDisplay =
						themeDisplay.getPortletDisplay();

					return portletDisplay.getId();
				}
			).setParameter(
				"folderId", folderId
			).setParameter(
				"repositoryId", _getRepositoryId(folder, themeDisplay)
			).buildString());

		return urlMenuItem;
	}

	public URLMenuItem getAddRepositoryMenuItem(
		Folder folder, ThemeDisplay themeDisplay,
		PortletRequest portletRequest) {

		if ((folder != null) ||
			!_hasPermission(
				themeDisplay.getPermissionChecker(),
				themeDisplay.getScopeGroupId(),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				ActionKeys.ADD_REPOSITORY)) {

			return null;
		}

		URLMenuItem urlMenuItem = new URLMenuItem();

		urlMenuItem.setIcon("repository");
		urlMenuItem.setLabel(
			_language.get(
				_portal.getHttpServletRequest(portletRequest), "repository"));
		urlMenuItem.setURL(
			PortletURLBuilder.create(
				_getPortletURL(themeDisplay, portletRequest)
			).setMVCRenderCommandName(
				"/document_library/edit_repository"
			).setRedirect(
				_portal.getCurrentURL(portletRequest)
			).buildString());

		return urlMenuItem;
	}

	public URLMenuItem getAddShortcutMenuItem(
		Folder folder, ThemeDisplay themeDisplay,
		PortletRequest portletRequest) {

		if ((folder != null) && !folder.isSupportsShortcuts()) {
			return null;
		}

		long folderId = _getFolderId(folder);

		if (!_hasPermission(
				themeDisplay.getPermissionChecker(),
				themeDisplay.getScopeGroupId(), folderId,
				ActionKeys.ADD_SHORTCUT)) {

			return null;
		}

		URLMenuItem urlMenuItem = new URLMenuItem();

		urlMenuItem.setIcon("shortcut");
		urlMenuItem.setLabel(
			_language.get(
				_portal.getHttpServletRequest(portletRequest), "shortcut"));
		urlMenuItem.setURL(
			PortletURLBuilder.create(
				_getPortletURL(themeDisplay, portletRequest)
			).setMVCRenderCommandName(
				"/document_library/edit_file_shortcut"
			).setRedirect(
				_portal.getCurrentURL(portletRequest)
			).setPortletResource(
				() -> {
					PortletDisplay portletDisplay =
						themeDisplay.getPortletDisplay();

					return portletDisplay.getId();
				}
			).setParameter(
				"folderId", folderId
			).setParameter(
				"repositoryId", _getRepositoryId(folder, themeDisplay)
			).buildString());

		return urlMenuItem;
	}

	public MenuItem getAICreatorMenuItem(
		Folder folder, ThemeDisplay themeDisplay,
		PortletRequest portletRequest) {

		long folderId = _getFolderId(folder);

		if (!_hasPermission(
				themeDisplay.getPermissionChecker(),
				themeDisplay.getScopeGroupId(), folderId,
				ActionKeys.ADD_DOCUMENT)) {

			return null;
		}

		return _aiCreatorOpenAIMenuItemFactory.
			createAICreatorCreateImageMenuItem(
				_getRepositoryId(folder, themeDisplay), folderId,
				_getDefaultFileEntryTypeId(folderId), themeDisplay);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openMultiValueMap(
			bundleContext, DLFileEntryTypeVisibilityController.class,
			"dl.file.entry.type.key");

		_dlFileEntryTypeIconProviderServiceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext, DLFileEntryTypeIconProvider.class,
				"file.entry.type.key");
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();

		_dlFileEntryTypeIconProviderServiceTrackerMap.close();

		_serviceTrackerMap = null;

		_dlFileEntryTypeLocalService = null;
	}

	private long _getDefaultFileEntryTypeId(long folderId) {
		try {
			return _dlFileEntryTypeLocalService.getDefaultFileEntryTypeId(
				folderId);
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get default file entry type ID for folder " +
						folderId,
					portalException);
			}

			return DLFileEntryTypeConstants.COMPANY_ID_BASIC_DOCUMENT;
		}
	}

	private MenuItem _getFileEntryTypeMenuItem(
			Folder folder, List<DLFileEntryType> fileEntryTypes,
			DLFileEntryType fileEntryType, ThemeDisplay themeDisplay,
			PortletRequest portletRequest)
		throws PortalException {

		URLMenuItem urlMenuItem = new URLMenuItem();

		urlMenuItem.setIcon(_getIcon(fileEntryType));
		urlMenuItem.setKey(
			DLFileEntryType.class.getSimpleName() +
				fileEntryType.getFileEntryTypeKey());
		urlMenuItem.setLabel(
			fileEntryType.getUnambiguousName(
				fileEntryTypes, themeDisplay.getScopeGroupId(),
				themeDisplay.getLocale()));
		urlMenuItem.setURL(
			PortletURLBuilder.create(
				_getPortletURL(themeDisplay, portletRequest)
			).setMVCRenderCommandName(
				"/document_library/edit_file_entry"
			).setCMD(
				Constants.ADD
			).setRedirect(
				_portal.getCurrentURL(portletRequest)
			).setPortletResource(
				() -> {
					PortletDisplay portletDisplay =
						themeDisplay.getPortletDisplay();

					return portletDisplay.getId();
				}
			).setParameter(
				"fileEntryTypeId", fileEntryType.getFileEntryTypeId()
			).setParameter(
				"folderId", _getFolderId(folder)
			).setParameter(
				"repositoryId", _getRepositoryId(folder, themeDisplay)
			).buildString());

		return urlMenuItem;
	}

	private List<DLFileEntryType> _getFileEntryTypes(
		long groupId, Folder folder) {

		if ((folder != null) && !folder.isSupportsMetadata()) {
			return Collections.emptyList();
		}

		long folderId = _getFolderId(folder);

		boolean inherited = true;

		if ((folder != null) && (folder.getModel() instanceof DLFolder)) {
			DLFolder dlFolder = (DLFolder)folder.getModel();

			if (dlFolder.getRestrictionType() ==
					DLFolderConstants.
						RESTRICTION_TYPE_FILE_ENTRY_TYPES_AND_WORKFLOW) {

				inherited = false;
			}
		}

		try {
			return _dlFileEntryTypeService.getFolderFileEntryTypes(
				_siteConnectedGroupGroupProvider.
					getCurrentAndAncestorSiteAndDepotGroupIds(
						groupId, false, true),
				folderId, inherited);
		}
		catch (PortalException portalException) {
			_log.error(
				StringBundler.concat(
					"Unable to get file entry types for group ", groupId,
					" and folder ", folderId),
				portalException);

			return Collections.emptyList();
		}
	}

	private long _getFolderId(Folder folder) {
		if (folder == null) {
			return DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;
		}

		return folder.getFolderId();
	}

	private String _getIcon(DLFileEntryType fileEntryType) {
		DLFileEntryTypeIconProvider dlFileEntryTypeIconProvider =
			_dlFileEntryTypeIconProviderServiceTrackerMap.getService(
				fileEntryType.getFileEntryTypeKey());

		if (dlFileEntryTypeIconProvider != null) {
			return dlFileEntryTypeIconProvider.getIcon();
		}

		return "file-template";
	}

	private List<MenuItem> _getPortletTitleAddDocumentTypeMenuItems(
		Folder folder, ThemeDisplay themeDisplay,
		PortletRequest portletRequest) {

		List<DLFileEntryType> fileEntryTypes = _getFileEntryTypes(
			themeDisplay.getScopeGroupId(), folder);

		return TransformUtil.transform(
			fileEntryTypes,
			fileEntryType -> {
				try {
					if ((fileEntryType.getFileEntryTypeId() !=
							DLFileEntryTypeConstants.
								COMPANY_ID_BASIC_DOCUMENT) &&
						_isFileEntryTypeVisible(
							themeDisplay.getUserId(), fileEntryType)) {

						return _getFileEntryTypeMenuItem(
							folder, fileEntryTypes, fileEntryType, themeDisplay,
							portletRequest);
					}
				}
				catch (PortalException portalException) {
					_log.error(
						"Unable to add menu item for file entry type " +
							fileEntryType.getName(),
						portalException);
				}

				return null;
			});
	}

	private PortletURL _getPortletURL(
		ThemeDisplay themeDisplay, PortletRequest portletRequest) {

		PortletURL portletURL = _portal.getControlPanelPortletURL(
			portletRequest, themeDisplay.getScopeGroup(),
			DLPortletKeys.DOCUMENT_LIBRARY_ADMIN, 0, 0,
			PortletRequest.RENDER_PHASE);

		try {
			portletURL.setWindowState(portletRequest.getWindowState());
		}
		catch (WindowStateException windowStateException) {
			_log.error(windowStateException);
		}

		return portletURL;
	}

	private long _getRepositoryId(Folder folder, ThemeDisplay themeDisplay) {
		if (folder == null) {
			return themeDisplay.getScopeGroupId();
		}

		return folder.getRepositoryId();
	}

	private boolean _hasPermission(
		PermissionChecker permissionChecker, long groupId, long folderId,
		String actionId) {

		try {
			return DLFolderPermission.contains(
				permissionChecker, groupId, folderId, actionId);
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return false;
		}
	}

	private boolean _isFileEntryTypeVisible(
		long userId, DLFileEntryType fileEntryType) {

		List<DLFileEntryTypeVisibilityController>
			dlFileEntryTypeVisibilityControllers =
				_serviceTrackerMap.getService(
					fileEntryType.getFileEntryTypeKey());

		if (dlFileEntryTypeVisibilityControllers == null) {
			return true;
		}

		for (DLFileEntryTypeVisibilityController
				dlFileEntryTypeVisibilityController :
					dlFileEntryTypeVisibilityControllers) {

			if (!dlFileEntryTypeVisibilityController.isVisible(
					userId, fileEntryType)) {

				return false;
			}
		}

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MenuItemProvider.class);

	private static ServiceTrackerMap<String, DLFileEntryTypeIconProvider>
		_dlFileEntryTypeIconProviderServiceTrackerMap;

	@Reference
	private AICreatorOpenAIMenuItemFactory _aiCreatorOpenAIMenuItemFactory;

	@Reference
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@Reference
	private DLFileEntryTypeService _dlFileEntryTypeService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	private ServiceTrackerMap<String, List<DLFileEntryTypeVisibilityController>>
		_serviceTrackerMap;

	@Reference
	private SiteConnectedGroupGroupProvider _siteConnectedGroupGroupProvider;

}