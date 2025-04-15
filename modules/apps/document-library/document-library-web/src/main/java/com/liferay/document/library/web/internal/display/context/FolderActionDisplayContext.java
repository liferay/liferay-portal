/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.display.context;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppServiceUtil;
import com.liferay.document.library.util.DLURLHelperUtil;
import com.liferay.document.library.web.internal.display.context.helper.DLPortletInstanceSettingsHelper;
import com.liferay.document.library.web.internal.display.context.helper.DLRequestHelper;
import com.liferay.document.library.web.internal.helper.DLTrashHelper;
import com.liferay.document.library.web.internal.security.permission.resource.DLFolderPermission;
import com.liferay.document.library.web.internal.security.permission.resource.DLPermission;
import com.liferay.document.library.web.internal.util.DLFolderUtil;
import com.liferay.document.library.web.internal.util.DLSubscriptionUtil;
import com.liferay.document.library.web.internal.util.FolderItemSelectorURLProvider;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.item.selector.ItemSelector;
import com.liferay.learn.LearnMessage;
import com.liferay.learn.LearnMessageUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.ResultRow;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.repository.capabilities.TemporaryFileEntriesCapability;
import com.liferay.portal.kernel.repository.capabilities.TrashCapability;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowHandler;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;
import com.liferay.portal.util.RepositoryUtil;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.staging.StagingGroupHelperUtil;
import com.liferay.taglib.security.PermissionsURLTag;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.ResourceURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Adolfo Pérez
 */
public class FolderActionDisplayContext {

	public FolderActionDisplayContext(
		DLTrashHelper dlTrashHelper, HttpServletRequest httpServletRequest) {

		_dlTrashHelper = dlTrashHelper;
		_httpServletRequest = httpServletRequest;

		_dlRequestHelper = new DLRequestHelper(httpServletRequest);
		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						this::_isDownloadFolderActionVisible,
						dropdownItem -> {
							dropdownItem.setHref(_getDownloadFolderURL());
							dropdownItem.setIcon("download");
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, "download"));
						}
					).add(
						this::_isEditFolderActionVisible,
						dropdownItem -> {
							dropdownItem.setHref(_getEditFolderURL());
							dropdownItem.setIcon("pencil");
							dropdownItem.setLabel(
								LanguageUtil.get(_httpServletRequest, "edit"));
						}
					).add(
						this::_isSubscribeFolderActionVisible,
						dropdownItem -> {
							dropdownItem.setHref(_getSubscribeFolderURL());
							dropdownItem.setIcon("bell-on");
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, "subscribe"));
						}
					).add(
						this::_isUnsubscribeFolderActionVisible,
						_createUnsubscribeDropdownItem()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						this::_isMoveFolderActionVisible,
						dropdownItem -> {
							dropdownItem.setHref(_getMoveFolderURL());
							dropdownItem.setIcon("move-folder");
							dropdownItem.setLabel(
								LanguageUtil.get(_httpServletRequest, "move"));
						}
					).add(
						this::_isCopyActionVisible,
						dropdownItem -> {
							dropdownItem.setHref(_getCopyURL());
							dropdownItem.setIcon("copy");
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, "copy-to"));
						}
					).add(
						this::_isDeleteExpiredTemporaryFileEntriesActionVisible,
						dropdownItem -> {
							dropdownItem.setHref(
								_getDeleteExpiredTemporaryFileEntriesURL());
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest,
									"delete-expired-temporary-files"));
						}
					).add(
						this::_isAddFolderActionVisible,
						dropdownItem -> {
							dropdownItem.setHref(_getAddFolderURL());
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, "add-folder"));
						}
					).add(
						this::_isAddRepositoryActionVisible,
						dropdownItem -> {
							dropdownItem.setHref(_getAddRepositoryURL());
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, "add-repository"));
						}
					).add(
						this::_isAddMediaActionVisible,
						dropdownItem -> {
							dropdownItem.setHref(_getAddMediaURL());
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, "add-file-entry"));
						}
					).add(
						() ->
							_isAddMediaActionVisible() &&
							_isMultipleUploadSupported(),
						dropdownItem -> {
							dropdownItem.put(
								"class",
								"dropdown-item hide upload-multiple-documents");
							dropdownItem.setHref(_getAddMultipleMediaURL());
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, "multiple-media"));
						}
					).add(
						this::_isViewSlideShowActionVisible,
						dropdownItem -> {
							dropdownItem.putData("action", "slideShow");
							dropdownItem.putData(
								"viewSlideShowURL", _getViewSlideShowURL());
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, "view-slide-show"));
						}
					).add(
						this::_isAddFileShortcutActionVisible,
						dropdownItem -> {
							dropdownItem.setHref(_getAddFileShortcutURL());
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, "add-shortcut"));
						}
					).add(
						this::_isAccessFromDesktopActionVisible,
						dropdownItem -> {
							dropdownItem.putData("action", "accessFromDesktop");

							LearnMessage learnMessage =
								LearnMessageUtil.getLearnMessage(
									"webdav",
									LanguageUtil.getLanguageId(
										_httpServletRequest),
									"document-library-web");

							dropdownItem.putData(
								"learnMessage", learnMessage.getMessage());
							dropdownItem.putData(
								"learnURL", learnMessage.getURL());

							ThemeDisplay themeDisplay =
								(ThemeDisplay)_httpServletRequest.getAttribute(
									WebKeys.THEME_DISPLAY);

							dropdownItem.putData(
								"webDavURL",
								DLURLHelperUtil.getWebDavURL(
									themeDisplay, _getFolder(), null));

							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest,
									"access-from-desktop"));
						}
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						this::_isPermissionsActionVisible,
						dropdownItem -> {
							dropdownItem.putData("action", "permissions");

							String permissionsURL = PermissionsURLTag.doTag(
								StringPool.BLANK, _getModelResource(),
								HtmlUtil.escape(_getModelResourceDescription()),
								null, String.valueOf(_getResourcePrimKey()),
								LiferayWindowState.POP_UP.toString(), null,
								_httpServletRequest);

							dropdownItem.putData(
								"permissionsURL", permissionsURL);

							dropdownItem.setIcon("password-policies");
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, "permissions"));
						}
					).add(
						this::_isDeleteFolderActionVisible,
						dropdownItem -> {
							if (_isTrashEnabled()) {
								dropdownItem.setHref(_getDeleteFolderURL());
							}
							else {
								dropdownItem.putData("action", "delete");
								dropdownItem.putData(
									"deleteURL", _getDeleteFolderURL());
							}

							dropdownItem.setIcon("trash");
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, "delete"));
						}
					).add(
						this::_isPublishFolderActionVisible,
						dropdownItem -> {
							dropdownItem.putData("action", "publish");
							dropdownItem.putData(
								"publishURL", _getPublishFolderURL());
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, "publish-to-live"));
						}
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).build();
	}

	public boolean isShowActions() {
		DLPortletInstanceSettingsHelper dlPortletInstanceSettingsHelper =
			new DLPortletInstanceSettingsHelper(_dlRequestHelper);

		return dlPortletInstanceSettingsHelper.isShowActions();
	}

	private DropdownItem _createUnsubscribeDropdownItem()
		throws PortalException {

		long parentFolderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;

		Folder folder = _getFolder();

		if (folder != null) {
			parentFolderId = folder.getParentFolderId();
		}

		if ((folder == null) ||
			!DLSubscriptionUtil.isSubscribedToFolder(
				_themeDisplay.getCompanyId(), _themeDisplay.getScopeGroupId(),
				_themeDisplay.getUserId(), parentFolderId)) {

			return DropdownItemBuilder.setHref(
				_getUnsubscribeFolderURL()
			).setIcon(
				"bell-off"
			).setLabel(
				LanguageUtil.get(_httpServletRequest, "unsubscribe")
			).build();
		}

		return DropdownItemBuilder.setDisabled(
			true
		).setIcon(
			"bell-off"
		).setLabel(
			LanguageUtil.get(
				_httpServletRequest, "subscribed-to-a-parent-folder")
		).build();
	}

	private String _getAddFileShortcutURL() {
		ThemeDisplay themeDisplay = _dlRequestHelper.getThemeDisplay();

		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				_httpServletRequest, themeDisplay.getScopeGroup(),
				DLPortletKeys.DOCUMENT_LIBRARY_ADMIN, 0, 0,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/document_library/edit_file_shortcut"
		).setRedirect(
			_dlRequestHelper.getCurrentURL()
		).setParameter(
			"folderId", _getFolderId()
		).setParameter(
			"repositoryId", _getRepositoryId()
		).buildString();
	}

	private String _getAddFolderURL() {
		ThemeDisplay themeDisplay = _dlRequestHelper.getThemeDisplay();

		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				_httpServletRequest, themeDisplay.getScopeGroup(),
				DLPortletKeys.DOCUMENT_LIBRARY_ADMIN, 0, 0,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/document_library/edit_folder"
		).setRedirect(
			_dlRequestHelper.getCurrentURL()
		).setParameter(
			"ignoreRootFolder", true
		).setParameter(
			"parentFolderId", _getFolderId()
		).setParameter(
			"repositoryId", _getRepositoryId()
		).buildString();
	}

	private String _getAddMediaURL() {
		return PortletURLBuilder.createRenderURL(
			_dlRequestHelper.getLiferayPortletResponse()
		).setMVCRenderCommandName(
			"/document_library/edit_file_entry"
		).setRedirect(
			_dlRequestHelper.getCurrentURL()
		).setParameter(
			"folderId", _getFolderId()
		).setParameter(
			"repositoryId", _getRepositoryId()
		).buildString();
	}

	private String _getAddMultipleMediaURL() {
		return PortletURLBuilder.createRenderURL(
			_dlRequestHelper.getLiferayPortletResponse()
		).setMVCRenderCommandName(
			"/document_library/upload_multiple_file_entries"
		).setRedirect(
			_dlRequestHelper.getCurrentURL()
		).setBackURL(
			_dlRequestHelper.getCurrentURL()
		).setParameter(
			"folderId", _getFolderId()
		).setParameter(
			"repositoryId", _getRepositoryId()
		).buildString();
	}

	private String _getAddRepositoryURL() {
		ThemeDisplay themeDisplay = _dlRequestHelper.getThemeDisplay();

		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				_httpServletRequest, themeDisplay.getScopeGroup(),
				DLPortletKeys.DOCUMENT_LIBRARY_ADMIN, 0, 0,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/document_library/edit_repository"
		).setRedirect(
			_dlRequestHelper.getCurrentURL()
		).setParameter(
			"folderId", _getFolderId()
		).buildString();
	}

	private String _getCopyURL() {
		return PortletURLBuilder.createRenderURL(
			_dlRequestHelper.getLiferayPortletResponse()
		).setMVCRenderCommandName(
			"/document_library/copy_dl_objects"
		).setRedirect(
			_dlRequestHelper.getCurrentURL()
		).setParameter(
			"dlObjectIds", _getFolderId()
		).setParameter(
			"sourceFolderId",
			() -> {
				Folder folder = _getFolder();

				return folder.getParentFolderId();
			}
		).setParameter(
			"sourceRepositoryId", _getRepositoryId()
		).buildString();
	}

	private String _getDeleteExpiredTemporaryFileEntriesURL() {
		return PortletURLBuilder.createActionURL(
			_dlRequestHelper.getLiferayPortletResponse()
		).setActionName(
			"/document_library/edit_folder"
		).setCMD(
			"deleteExpiredTemporaryFileEntries"
		).setRedirect(
			_dlRequestHelper.getCurrentURL()
		).setParameter(
			"repositoryId", _getRepositoryId()
		).buildString();
	}

	private String _getDeleteFolderCommand() throws PortalException {
		if (DLFolderUtil.isRepositoryRoot(_getFolder())) {
			return Constants.DELETE;
		}

		if (_isTrashEnabled()) {
			return Constants.MOVE_TO_TRASH;
		}

		return Constants.DELETE;
	}

	private String _getDeleteFolderURL() throws PortalException {
		LiferayPortletResponse liferayPortletResponse =
			_dlRequestHelper.getLiferayPortletResponse();

		PortletURL portletURL = liferayPortletResponse.createActionURL();

		Folder folder = _getFolder();

		if (!DLFolderUtil.isRepositoryRoot(folder)) {
			portletURL.setParameter(
				ActionRequest.ACTION_NAME, "/document_library/edit_folder");
		}
		else {
			portletURL.setParameter(
				ActionRequest.ACTION_NAME, "/document_library/edit_repository");
		}

		portletURL.setParameter(Constants.CMD, _getDeleteFolderCommand());
		portletURL.setParameter("redirect", _getParentFolderURL());

		if (!DLFolderUtil.isRepositoryRoot(folder)) {
			portletURL.setParameter("folderId", String.valueOf(_getFolderId()));
		}
		else {
			portletURL.setParameter(
				"repositoryId", String.valueOf(_getRepositoryId()));
		}

		return portletURL.toString();
	}

	private String _getDownloadFolderURL() {
		LiferayPortletResponse liferayPortletResponse =
			_dlRequestHelper.getLiferayPortletResponse();

		ResourceURL resourceURL = liferayPortletResponse.createResourceURL();

		resourceURL.setParameter("folderId", String.valueOf(_getFolderId()));
		resourceURL.setParameter(
			"repositoryId", String.valueOf(_getRepositoryId()));
		resourceURL.setResourceID("/document_library/download_folder");

		return resourceURL.toString();
	}

	private String _getEditFolderURL() {
		Folder folder = _getFolder();

		ThemeDisplay themeDisplay = _dlRequestHelper.getThemeDisplay();

		PortletURL portletURL = PortalUtil.getControlPanelPortletURL(
			_httpServletRequest, themeDisplay.getScopeGroup(),
			DLPortletKeys.DOCUMENT_LIBRARY_ADMIN, 0, 0,
			PortletRequest.RENDER_PHASE);

		if ((folder == null) || !DLFolderUtil.isRepositoryRoot(folder)) {
			portletURL.setParameter(
				"mvcRenderCommandName", "/document_library/edit_folder");
		}
		else {
			portletURL.setParameter(
				"mvcRenderCommandName", "/document_library/edit_repository");
		}

		portletURL.setParameter("redirect", _dlRequestHelper.getCurrentURL());

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		portletURL.setParameter("portletResource", portletDisplay.getId());

		portletURL.setParameter("folderId", String.valueOf(_getFolderId()));
		portletURL.setParameter(
			"repositoryId", String.valueOf(_getRepositoryId()));

		if (folder == null) {
			portletURL.setParameter("rootFolder", Boolean.TRUE.toString());
		}

		return portletURL.toString();
	}

	private Folder _getFolder() {
		if (_folder != null) {
			return _folder;
		}

		ResultRow row = (ResultRow)_httpServletRequest.getAttribute(
			WebKeys.SEARCH_CONTAINER_RESULT_ROW);

		if (row == null) {
			_folder = (Folder)_httpServletRequest.getAttribute(
				"info_panel.jsp-folder");
		}
		else {
			if (row.getObject() instanceof Folder) {
				_folder = (Folder)row.getObject();
			}
		}

		return _folder;
	}

	private long _getFolderId() {
		Folder folder = _getFolder();

		if (folder == null) {
			return DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;
		}

		return folder.getFolderId();
	}

	private String _getModelResource() {
		Folder folder = _getFolder();

		if (folder != null) {
			return DLFolderConstants.getClassName();
		}

		return "com.liferay.document.library";
	}

	private String _getModelResourceDescription() throws PortalException {
		Folder folder = _getFolder();

		if (folder != null) {
			return folder.getName();
		}

		ThemeDisplay themeDisplay = _dlRequestHelper.getThemeDisplay();

		return themeDisplay.getScopeGroupName();
	}

	private String _getMoveFolderURL() throws PortalException {
		LiferayPortletResponse liferayPortletResponse =
			_dlRequestHelper.getLiferayPortletResponse();

		FolderItemSelectorURLProvider folderItemSelectorURLProvider =
			new FolderItemSelectorURLProvider(
				_httpServletRequest,
				(ItemSelector)_httpServletRequest.getAttribute(
					ItemSelector.class.getName()));

		return StringBundler.concat(
			"javascript:", liferayPortletResponse.getNamespace(),
			"move(1, 'rowIdsFolder', ", _getFolderId(), ", '",
			HtmlUtil.escapeJS(
				folderItemSelectorURLProvider.getSelectMoveToFolderURL(
					_getRepositoryId(), _getParentFolderId(), _getFolderId())),
			"');");
	}

	private long _getParentFolderId() {
		Folder folder = _getFolder();

		return folder.getParentFolderId();
	}

	private String _getParentFolderURL() {
		if (!_isView()) {
			return _dlRequestHelper.getCurrentURL();
		}

		String portletName = _dlRequestHelper.getPortletName();

		String mvcRenderCommandName = "/image_gallery_display/view";

		if (!portletName.equals(DLPortletKeys.MEDIA_GALLERY_DISPLAY)) {
			mvcRenderCommandName = "/document_library/view";

			Folder folder = _getFolder();

			if ((folder != null) && !DLFolderUtil.isRepositoryRoot(folder) &&
				(folder.getParentFolderId() !=
					DLFolderConstants.DEFAULT_PARENT_FOLDER_ID)) {

				mvcRenderCommandName = "/document_library/view_folder";
			}
		}

		Folder folder = _getFolder();

		if (folder == null) {
			return StringPool.BLANK;
		}

		return PortletURLBuilder.createRenderURL(
			_dlRequestHelper.getLiferayPortletResponse()
		).setMVCRenderCommandName(
			mvcRenderCommandName
		).setParameter(
			"folderId",
			() -> {
				if (DLFolderUtil.isRepositoryRoot(folder)) {
					return DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;
				}

				return folder.getParentFolderId();
			}
		).buildString();
	}

	private String _getPublishFolderURL() {
		return PortletURLBuilder.createActionURL(
			_dlRequestHelper.getLiferayPortletResponse()
		).setActionName(
			"/document_library/publish_folder"
		).setBackURL(
			_dlRequestHelper.getCurrentURL()
		).setParameter(
			"folderId", _getFolderId()
		).buildString();
	}

	private long _getRepositoryId() {
		if (_repositoryId != null) {
			return _repositoryId;
		}

		Folder folder = _getFolder();

		if (folder != null) {
			_repositoryId = folder.getRepositoryId();
		}
		else {
			_repositoryId = GetterUtil.getLong(
				(String)_httpServletRequest.getAttribute(
					"view.jsp-repositoryId"));
		}

		return _repositoryId;
	}

	private long _getResourcePrimKey() {
		Folder folder = _getFolder();

		if (folder != null) {
			return folder.getFolderId();
		}

		return _dlRequestHelper.getScopeGroupId();
	}

	private int _getStatus() {
		if (_status != null) {
			return _status;
		}

		ThemeDisplay themeDisplay = _dlRequestHelper.getThemeDisplay();

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		if (permissionChecker.isContentReviewer(
				_dlRequestHelper.getCompanyId(),
				_dlRequestHelper.getScopeGroupId())) {

			_status = WorkflowConstants.STATUS_ANY;
		}
		else {
			_status = WorkflowConstants.STATUS_APPROVED;
		}

		return _status;
	}

	private String _getSubscribeFolderURL() {
		return PortletURLBuilder.createActionURL(
			_dlRequestHelper.getLiferayPortletResponse()
		).setActionName(
			"/document_library/edit_folder"
		).setCMD(
			Constants.SUBSCRIBE
		).setRedirect(
			_dlRequestHelper.getCurrentURL()
		).setParameter(
			"folderId", _getFolderId()
		).buildString();
	}

	private String _getUnsubscribeFolderURL() {
		return PortletURLBuilder.createActionURL(
			_dlRequestHelper.getLiferayPortletResponse()
		).setActionName(
			"/document_library/edit_folder"
		).setCMD(
			Constants.UNSUBSCRIBE
		).setRedirect(
			_dlRequestHelper.getCurrentURL()
		).setParameter(
			"folderId", _getFolderId()
		).buildString();
	}

	private String _getViewSlideShowURL() {
		return PortletURLBuilder.createRenderURL(
			_dlRequestHelper.getLiferayPortletResponse()
		).setMVCRenderCommandName(
			"/image_gallery_display/view_slide_show"
		).setParameter(
			"folderId", _getFolderId()
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	private boolean _hasPermissionsPermission() throws PortalException {
		Folder folder = _getFolder();

		if (folder != null) {
			return DLFolderPermission.contains(
				_dlRequestHelper.getPermissionChecker(), folder,
				ActionKeys.PERMISSIONS);
		}

		return DLPermission.contains(
			_dlRequestHelper.getPermissionChecker(),
			_dlRequestHelper.getScopeGroupId(), ActionKeys.PERMISSIONS);
	}

	private boolean _hasSubscribePermission() throws PortalException {
		Folder folder = _getFolder();

		if (folder != null) {
			return DLFolderPermission.contains(
				_dlRequestHelper.getPermissionChecker(), folder,
				ActionKeys.SUBSCRIBE);
		}

		return DLPermission.contains(
			_dlRequestHelper.getPermissionChecker(),
			_dlRequestHelper.getScopeGroupId(), ActionKeys.SUBSCRIBE);
	}

	private boolean _hasViewPermission() throws PortalException {
		return DLFolderPermission.contains(
			_dlRequestHelper.getPermissionChecker(),
			_dlRequestHelper.getScopeGroupId(), _getFolderId(),
			ActionKeys.VIEW);
	}

	private boolean _isAccessFromDesktopActionVisible() throws PortalException {
		PortletDisplay portletDisplay = _dlRequestHelper.getPortletDisplay();

		if (!_hasViewPermission() || !portletDisplay.isWebDAVEnabled()) {
			return false;
		}

		Folder folder = _getFolder();

		if ((folder == null) ||
			(folder.getRepositoryId() == _dlRequestHelper.getScopeGroupId())) {

			return true;
		}

		return false;
	}

	private boolean _isAddFileShortcutActionVisible() throws PortalException {
		String portletName = _dlRequestHelper.getPortletName();

		if (!portletName.equals(DLPortletKeys.MEDIA_GALLERY_DISPLAY)) {
			return false;
		}

		Folder folder = _getFolder();

		if ((folder != null) &&
			(folder.isMountPoint() || !folder.isSupportsShortcuts())) {

			return false;
		}

		return DLFolderPermission.contains(
			_dlRequestHelper.getPermissionChecker(),
			_dlRequestHelper.getScopeGroupId(), _getFolderId(),
			ActionKeys.ADD_SHORTCUT);
	}

	private boolean _isAddFolderActionVisible() throws PortalException {
		return DLFolderPermission.contains(
			_dlRequestHelper.getPermissionChecker(),
			_dlRequestHelper.getScopeGroupId(), _getFolderId(),
			ActionKeys.ADD_FOLDER);
	}

	private boolean _isAddMediaActionVisible() throws PortalException {
		String portletName = _dlRequestHelper.getPortletName();

		if (!portletName.equals(DLPortletKeys.MEDIA_GALLERY_DISPLAY)) {
			return false;
		}

		Folder folder = _getFolder();

		if ((folder != null) && DLFolderUtil.isRepositoryRoot(folder)) {
			return false;
		}

		return DLFolderPermission.contains(
			_dlRequestHelper.getPermissionChecker(),
			_dlRequestHelper.getScopeGroupId(), _getFolderId(),
			ActionKeys.ADD_DOCUMENT);
	}

	private boolean _isAddRepositoryActionVisible() throws PortalException {
		Folder folder = _getFolder();

		if (folder != null) {
			return false;
		}

		return DLFolderPermission.contains(
			_dlRequestHelper.getPermissionChecker(),
			_dlRequestHelper.getScopeGroupId(), _getFolderId(),
			ActionKeys.ADD_REPOSITORY);
	}

	private Boolean _isCopyActionVisible() throws PortalException {
		Folder folder = _getFolder();

		User user = _dlRequestHelper.getUser();

		if ((folder == null) || user.isGuestUser() ||
			RepositoryUtil.isExternalRepository(_getRepositoryId())) {

			return false;
		}

		return _hasViewPermission();
	}

	private boolean _isDeleteExpiredTemporaryFileEntriesActionVisible() {
		try {
			Folder folder = _getFolder();

			if (folder == null) {
				return false;
			}

			if (DLFolderUtil.isRepositoryRoot(folder) &&
				folder.isRepositoryCapabilityProvided(
					TemporaryFileEntriesCapability.class)) {

				return true;
			}

			return false;
		}
		catch (Exception exception) {
			_log.error(exception);

			return false;
		}
	}

	private boolean _isDeleteFolderActionVisible() throws PortalException {
		Folder folder = _getFolder();

		if (folder == null) {
			return false;
		}

		return DLFolderPermission.contains(
			_dlRequestHelper.getPermissionChecker(),
			_dlRequestHelper.getScopeGroupId(), _getFolderId(),
			ActionKeys.DELETE);
	}

	private boolean _isDownloadFolderActionVisible() throws PortalException {
		Folder folder = _getFolder();

		if ((folder == null) ||
			RepositoryUtil.isExternalRepository(_getRepositoryId())) {

			return false;
		}

		return _hasViewPermission();
	}

	private boolean _isEditFolderActionVisible() throws PortalException {
		if (!_isWorkflowEnabled()) {
			return false;
		}

		if (DLFolderPermission.contains(
				_dlRequestHelper.getPermissionChecker(),
				_dlRequestHelper.getScopeGroupId(), _getFolderId(),
				ActionKeys.ADVANCED_UPDATE) ||
			DLFolderPermission.contains(
				_dlRequestHelper.getPermissionChecker(),
				_dlRequestHelper.getScopeGroupId(), _getFolderId(),
				ActionKeys.UPDATE)) {

			return true;
		}

		return false;
	}

	private boolean _isMoveFolderActionVisible() throws PortalException {
		Folder folder = _getFolder();

		if ((folder == null) || DLFolderUtil.isRepositoryRoot(folder)) {
			return false;
		}

		return DLFolderPermission.contains(
			_dlRequestHelper.getPermissionChecker(),
			_dlRequestHelper.getScopeGroupId(), _getFolderId(),
			ActionKeys.UPDATE);
	}

	private boolean _isMultipleUploadSupported() {
		Folder folder = _getFolder();

		if ((folder == null) || folder.isSupportsMultipleUpload()) {
			return true;
		}

		return false;
	}

	private boolean _isPermissionsActionVisible() throws PortalException {
		if (!_hasPermissionsPermission()) {
			return false;
		}

		Folder folder = _getFolder();

		if ((folder == null) || !DLFolderUtil.isRepositoryRoot(folder)) {
			return true;
		}

		return false;
	}

	private boolean _isPublishFolderActionVisible() throws PortalException {
		Folder folder = _getFolder();

		if (folder == null) {
			return false;
		}

		String portletName = _dlRequestHelper.getPortletName();

		if (!portletName.equals(DLPortletKeys.DOCUMENT_LIBRARY_ADMIN) ||
			!GroupPermissionUtil.contains(
				_dlRequestHelper.getPermissionChecker(),
				_dlRequestHelper.getScopeGroupId(),
				ActionKeys.EXPORT_IMPORT_PORTLET_INFO)) {

			return false;
		}

		StagingGroupHelper stagingGroupHelper =
			StagingGroupHelperUtil.getStagingGroupHelper();

		if (!stagingGroupHelper.isStagingGroup(
				_dlRequestHelper.getScopeGroupId()) ||
			!stagingGroupHelper.isStagedPortlet(
				_dlRequestHelper.getScopeGroupId(),
				DLPortletKeys.DOCUMENT_LIBRARY)) {

			return false;
		}

		return true;
	}

	private Boolean _isSubscribeFolderActionVisible() throws PortalException {
		if (!_hasSubscribePermission()) {
			return false;
		}

		return !_isUnsubscribeFolderActionVisible();
	}

	private boolean _isTrashEnabled() {
		try {
			Folder folder = _getFolder();

			if (((folder == null) ||
				 folder.isRepositoryCapabilityProvided(
					 TrashCapability.class)) &&
				_dlTrashHelper.isTrashEnabled(
					_dlRequestHelper.getScopeGroupId(), _getRepositoryId())) {

				return true;
			}

			return false;
		}
		catch (Exception exception) {
			_log.error(exception);

			return false;
		}
	}

	private Boolean _isUnsubscribeFolderActionVisible() throws PortalException {
		if (_subscribed != null) {
			return _subscribed;
		}

		if (_hasSubscribePermission()) {
			_subscribed = DLSubscriptionUtil.isSubscribedToFolder(
				_dlRequestHelper.getCompanyId(),
				_dlRequestHelper.getScopeGroupId(), _themeDisplay.getUserId(),
				_getFolderId());
		}
		else {
			_subscribed = false;
		}

		return _subscribed;
	}

	private boolean _isView() {
		if (_view != null) {
			return _view;
		}

		Folder folder = (Folder)_httpServletRequest.getAttribute(
			"info_panel.jsp-folder");

		if ((folder != null) && (folder.getFolderId() == _getFolderId())) {
			return true;
		}

		ResultRow row = (ResultRow)_httpServletRequest.getAttribute(
			WebKeys.SEARCH_CONTAINER_RESULT_ROW);

		String portletName = _dlRequestHelper.getPortletName();

		if ((row == null) &&
			portletName.equals(DLPortletKeys.MEDIA_GALLERY_DISPLAY)) {

			_view = true;
		}
		else {
			_view = false;
		}

		return _view;
	}

	private boolean _isViewSlideShowActionVisible() throws PortalException {
		String portletName = _dlRequestHelper.getPortletName();

		if (!portletName.equals(DLPortletKeys.MEDIA_GALLERY_DISPLAY) ||
			!_hasViewPermission()) {

			return false;
		}

		int fileEntriesAndFileShortcutsCount =
			DLAppServiceUtil.getFileEntriesAndFileShortcutsCount(
				_getRepositoryId(), _getFolderId(), _getStatus());

		if (fileEntriesAndFileShortcutsCount == 0) {
			return false;
		}

		return true;
	}

	private boolean _isWorkflowEnabled() {
		WorkflowHandler<Object> workflowHandler =
			WorkflowHandlerRegistryUtil.getWorkflowHandler(
				DLFileEntry.class.getName());

		if (workflowHandler == null) {
			return false;
		}

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FolderActionDisplayContext.class);

	private final DLRequestHelper _dlRequestHelper;
	private final DLTrashHelper _dlTrashHelper;
	private Folder _folder;
	private final HttpServletRequest _httpServletRequest;
	private Long _repositoryId;
	private Integer _status;
	private Boolean _subscribed;
	private final ThemeDisplay _themeDisplay;
	private Boolean _view;

}