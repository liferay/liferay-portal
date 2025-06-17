/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.display.context.logic;

import com.liferay.digital.signature.configuration.DigitalSignatureConfiguration;
import com.liferay.digital.signature.configuration.DigitalSignatureConfigurationUtil;
import com.liferay.digital.signature.constants.DigitalSignatureConstants;
import com.liferay.digital.signature.constants.DigitalSignaturePortletKeys;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.display.context.DLUIItemKeys;
import com.liferay.document.library.kernel.document.conversion.DocumentConversionUtil;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFileShortcutConstants;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.versioning.VersioningStrategy;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.document.library.web.internal.display.context.helper.FileEntryDisplayContextHelper;
import com.liferay.document.library.web.internal.display.context.helper.FileShortcutDisplayContextHelper;
import com.liferay.document.library.web.internal.helper.DLTrashHelper;
import com.liferay.document.library.web.internal.util.DLSubscriptionUtil;
import com.liferay.document.library.web.internal.util.FolderItemSelectorURLProvider;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerRegistryUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemBuilder;
import com.liferay.item.selector.ItemSelector;
import com.liferay.layout.service.LayoutClassedModelUsageLocalServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.repository.capabilities.TrashCapability;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileShortcut;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.util.PropsValues;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.staging.StagingGroupHelperUtil;
import com.liferay.taglib.security.PermissionsURLTag;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowStateException;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Objects;

/**
 * @author Iván Zaera
 */
public class UIItemsBuilder {

	public UIItemsBuilder(
		HttpServletRequest httpServletRequest, FileEntry fileEntry,
		FileVersion fileVersion, DLTrashHelper dlTrashHelper,
		VersioningStrategy versioningStrategy, DLURLHelper dlURLHelper) {

		this(
			httpServletRequest, fileEntry, null, fileVersion, dlTrashHelper,
			versioningStrategy, dlURLHelper);
	}

	public UIItemsBuilder(
			HttpServletRequest httpServletRequest, FileShortcut fileShortcut,
			DLTrashHelper dlTrashHelper, VersioningStrategy versioningStrategy,
			DLURLHelper dlURLHelper)
		throws PortalException {

		this(
			httpServletRequest, null, fileShortcut,
			fileShortcut.getFileVersion(), dlTrashHelper, versioningStrategy,
			dlURLHelper);
	}

	public UIItemsBuilder(
		HttpServletRequest httpServletRequest, FileVersion fileVersion,
		DLTrashHelper dlTrashHelper, VersioningStrategy versioningStrategy,
		DLURLHelper dlURLHelper) {

		this(
			httpServletRequest, null, null, fileVersion, dlTrashHelper,
			versioningStrategy, dlURLHelper);
	}

	public DropdownItem createCancelCheckoutDropdownItem() {
		return DropdownItemBuilder.setHref(
			PortletURLBuilder.create(
				_getActionURL(
					"/document_library/edit_file_entry",
					Constants.CANCEL_CHECKOUT)
			).setParameter(
				"fileEntryId", _fileEntry.getFileEntryId()
			).setParameter(
				"folderId", _fileEntry.getFolderId()
			).buildString()
		).setKey(
			DLUIItemKeys.CANCEL_CHECKOUT
		).setLabel(
			LanguageUtil.get(_httpServletRequest, "cancel-checkout[document]")
		).build();
	}

	public DropdownItem createCheckinDropdownItem() throws PortalException {
		PortletURL portletURL = PortletURLBuilder.create(
			_getActionURL(
				"/document_library/edit_file_entry", Constants.CHECKIN)
		).setParameter(
			"fileEntryId", _fileEntry.getFileEntryId()
		).setParameter(
			"folderId", _fileEntry.getFolderId()
		).buildPortletURL();

		if (!_versioningStrategy.isOverridable()) {
			return DropdownItemBuilder.setHref(
				portletURL.toString()
			).setIcon(
				"unlock"
			).setKey(
				DLUIItemKeys.CHECKIN
			).setLabel(
				LanguageUtil.get(_httpServletRequest, "checkin")
			).build();
		}

		return DropdownItemBuilder.putData(
			"action", "checkin"
		).putData(
			"checkinURL", portletURL.toString()
		).setIcon(
			"unlock"
		).setKey(
			DLUIItemKeys.CHECKIN
		).setLabel(
			LanguageUtil.get(_httpServletRequest, "checkin")
		).build();
	}

	public DropdownItem createCheckoutDropdownItem() {
		return DropdownItemBuilder.setHref(
			PortletURLBuilder.create(
				_getActionURL(
					"/document_library/edit_file_entry", Constants.CHECKOUT)
			).setParameter(
				"fileEntryId", _fileEntry.getFileEntryId()
			).setParameter(
				"folderId", _fileEntry.getFolderId()
			).buildString()
		).setIcon(
			"lock"
		).setKey(
			DLUIItemKeys.CHECKOUT
		).setLabel(
			LanguageUtil.get(_httpServletRequest, "checkout[document]")
		).build();
	}

	public DropdownItem createCollectDigitalSignatureDropdownItem() {
		return DropdownItemBuilder.setHref(
			() -> {
				RequestBackedPortletURLFactory requestBackedPortletURLFactory =
					RequestBackedPortletURLFactoryUtil.create(
						_httpServletRequest);

				return PortletURLBuilder.create(
					requestBackedPortletURLFactory.createActionURL(
						DigitalSignaturePortletKeys.COLLECT_DIGITAL_SIGNATURE)
				).setBackURL(
					_getCurrentURL()
				).setParameter(
					"fileEntryId", _fileEntry.getFileEntryId()
				).buildString();
			}
		).setKey(
			DLUIItemKeys.COLLECT_DIGITAL_SIGNATURE
		).setLabel(
			LanguageUtil.get(_httpServletRequest, "collect-digital-signature")
		).build();
	}

	public DropdownItem createCompareToDropdownItem() throws PortalException {
		PortletURL viewFileEntryURL = _getRenderURL(
			"/document_library/view_file_entry", _getRedirect());

		return DropdownItemBuilder.putData(
			"action", "compareTo"
		).putData(
			"selectFileVersionURL",
			() -> {
				PortletURL selectFileVersionURL = _getRenderURL(
					"/document_library/select_file_version",
					viewFileEntryURL.toString());

				try {
					selectFileVersionURL.setWindowState(
						LiferayWindowState.POP_UP);
				}
				catch (WindowStateException windowStateException) {
					throw new PortalException(windowStateException);
				}

				selectFileVersionURL.setParameter(
					"version", _fileVersion.getVersion());

				return selectFileVersionURL.toString();
			}
		).putData(
			"compareVersionURL",
			PortletURLBuilder.create(
				_getRenderURL("/document_library/compare_versions", null)
			).setBackURL(
				_getCurrentURL()
			).buildString()
		).putData(
			"namespace", _getNamespace()
		).putData(
			"jsNamespace", _getNamespace() + _fileVersion.getFileVersionId()
		).putData(
			"dialogTitle",
			LanguageUtil.get(_httpServletRequest, "compare-versions")
		).setLabel(
			LanguageUtil.get(_httpServletRequest, "compare-to")
		).build();
	}

	public DropdownItem createCopyDropdownItem() {
		return DropdownItemBuilder.setHref(
			_getCopyEntryURL()
		).setIcon(
			"copy"
		).setKey(
			DLUIItemKeys.COPY
		).setLabel(
			LanguageUtil.get(_httpServletRequest, "copy-to")
		).build();
	}

	public DropdownItem createDeleteDropdownItem() throws PortalException {
		String cmd = null;

		if (_isDeleteActionAvailable()) {
			cmd = Constants.DELETE;
		}
		else if (_isMoveToTheRecycleBinActionAvailable()) {
			cmd = Constants.MOVE_TO_TRASH;
		}
		else {
			return null;
		}

		String mvcActionCommandName = "/document_library/edit_file_entry";

		if (_fileShortcut != null) {
			mvcActionCommandName = "/document_library/edit_file_shortcut";
		}

		PortletURL portletURL = _getDeleteActionURL(mvcActionCommandName, cmd);

		if (_fileShortcut == null) {
			portletURL.setParameter(
				"fileEntryId", String.valueOf(_fileEntry.getFileEntryId()));
		}
		else {
			portletURL.setParameter(
				"fileShortcutId",
				String.valueOf(_fileShortcut.getFileShortcutId()));
		}

		DropdownItem dropdownItem = DropdownItemBuilder.setIcon(
			"trash"
		).setKey(
			DLUIItemKeys.DELETE
		).setLabel(
			LanguageUtil.get(_httpServletRequest, "delete")
		).build();

		if (cmd.equals(Constants.DELETE)) {
			dropdownItem.putData("action", "delete");
			dropdownItem.putData("deleteURL", portletURL.toString());
		}
		else {
			dropdownItem.setHref(portletURL.toString());
		}

		return dropdownItem;
	}

	public DropdownItem createDeleteVersionDropdownItem() {
		return DropdownItemBuilder.putData(
			"action", "deleteVersion"
		).putData(
			"deleteURL",
			PortletURLBuilder.create(
				_getActionURL(
					"/document_library/edit_file_entry", Constants.DELETE)
			).setParameter(
				"fileEntryId", _fileEntry.getFileEntryId()
			).setParameter(
				"version", _fileVersion.getVersion()
			).buildString()
		).setIcon(
			"trash"
		).setLabel(
			LanguageUtil.get(_httpServletRequest, "delete")
		).build();
	}

	public DropdownItem createDownloadDropdownItem() {
		boolean appendVersion = true;

		if (StringUtil.equalsIgnoreCase(
				_fileEntry.getVersion(), _fileVersion.getVersion())) {

			appendVersion = false;
		}

		return DropdownItemBuilder.setData(
			HashMapBuilder.<String, Object>put(
				"analytics-file-entry-id", _fileEntry.getFileEntryId()
			).put(
				"analytics-file-entry-title", _fileEntry.getTitle()
			).put(
				"analytics-file-entry-version", _fileEntry.getVersion()
			).put(
				"senna-off", "true"
			).build()
		).setHref(
			_addDoAsUserIdParameter(
				_dlURLHelper.getDownloadURL(
					_fileEntry, _fileVersion, _themeDisplay, StringPool.BLANK,
					appendVersion, true))
		).setIcon(
			"download"
		).setKey(
			DLUIItemKeys.DOWNLOAD
		).setLabel(
			StringBundler.concat(
				_themeDisplay.translate("download"), " (",
				LanguageUtil.formatStorageSize(
					_fileVersion.getSize(), _themeDisplay.getLocale()),
				")")
		).build();
	}

	public DropdownItem createEditDropdownItem() {
		PortletURL portletURL = null;

		if (_fileShortcut == null) {
			portletURL = _getControlPanelRenderURL(
				"/document_library/edit_file_entry");
		}
		else {
			portletURL = _getControlPanelRenderURL(
				"/document_library/edit_file_shortcut");
		}

		portletURL.setParameter("backURL", _getCurrentURL());

		return DropdownItemBuilder.setHref(
			portletURL.toString()
		).setIcon(
			"pencil"
		).setKey(
			DLUIItemKeys.EDIT
		).setLabel(
			LanguageUtil.get(_httpServletRequest, "edit")
		).build();
	}

	public DropdownItem createEditImageDropdownItem() {
		return DropdownItemBuilder.putData(
			"action", "editImage"
		).putData(
			"fileEntryId", String.valueOf(_fileEntry.getFileEntryId())
		).putData(
			"imageURL",
			_dlURLHelper.getPreviewURL(
				_fileEntry, _fileVersion, _themeDisplay, StringPool.BLANK)
		).setKey(
			DLUIItemKeys.EDIT_IMAGE
		).setLabel(
			LanguageUtil.get(_httpServletRequest, "edit-image")
		).build();
	}

	public DropdownItem createHistoryDropdownItem() {
		return DropdownItemBuilder.setHref(
			_getControlPanelRenderURL(
				"/document_library/view_file_entry_history", _getCurrentURL())
		).setIcon(
			"date-time"
		).setKey(
			DLUIItemKeys.VIEW_HISTORY
		).setLabel(
			LanguageUtil.get(_httpServletRequest, "view-history")
		).build();
	}

	public DropdownItem createMoveDropdownItem() {
		return DropdownItemBuilder.putData(
			"action", "move"
		).putData(
			"parameterName",
			(_fileShortcut != null) ? "rowIdsDLFileShortcut" : "rowIdsFileEntry"
		).putData(
			"parameterValue",
			(_fileShortcut != null) ?
				String.valueOf(_fileShortcut.getFileShortcutId()) :
					String.valueOf(_fileEntry.getFileEntryId())
		).putData(
			"selectFolderURL",
			() -> {
				FolderItemSelectorURLProvider folderItemSelectorURLProvider =
					new FolderItemSelectorURLProvider(
						_httpServletRequest,
						(ItemSelector)_httpServletRequest.getAttribute(
							ItemSelector.class.getName()));

				if (_fileShortcut != null) {
					return folderItemSelectorURLProvider.
						getSelectMoveToFolderURL(
							_fileShortcut.getRepositoryId(),
							_fileShortcut.getFolderId(),
							DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);
				}

				return folderItemSelectorURLProvider.getSelectMoveToFolderURL(
					_fileEntry.getRepositoryId(), _fileEntry.getFolderId(),
					DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);
			}
		).setIcon(
			"move-folder"
		).setKey(
			DLUIItemKeys.MOVE
		).setLabel(
			LanguageUtil.get(_httpServletRequest, "move")
		).build();
	}

	public DropdownItem createPermissionsDropdownItem() {
		String url = null;

		try {
			if (_fileShortcut != null) {
				url = PermissionsURLTag.doTag(
					null, DLFileShortcutConstants.getClassName(),
					HtmlUtil.unescape(_fileShortcut.getToTitle()), null,
					String.valueOf(_fileShortcut.getFileShortcutId()),
					LiferayWindowState.POP_UP.toString(), null,
					_httpServletRequest);
			}
			else {
				url = PermissionsURLTag.doTag(
					null, DLFileEntryConstants.getClassName(),
					HtmlUtil.unescape(_fileEntry.getTitle()), null,
					String.valueOf(_fileEntry.getFileEntryId()),
					LiferayWindowState.POP_UP.toString(), null,
					_httpServletRequest);
			}
		}
		catch (Exception exception) {
			throw new SystemException(
				"Unable to create permissions URL", exception);
		}

		return DropdownItemBuilder.putData(
			"action", "permissions"
		).putData(
			"permissionsURL", url
		).setIcon(
			"password-policies"
		).setKey(
			DLUIItemKeys.PERMISSIONS
		).setLabel(
			LanguageUtil.get(_httpServletRequest, "permissions")
		).build();
	}

	public DropdownItem createPublishDropdownItem() {
		PortletURL portletURL = null;

		if (_fileShortcut == null) {
			portletURL = PortletURLBuilder.create(
				_getActionURL("/document_library/publish_file_entry")
			).setParameter(
				"fileEntryId", _fileEntry.getFileEntryId()
			).buildPortletURL();
		}
		else {
			portletURL = PortletURLBuilder.create(
				_getActionURL("/document_library/publish_file_shortcut")
			).setParameter(
				"fileShortcutId", _fileShortcut.getFileShortcutId()
			).buildPortletURL();
		}

		portletURL.setParameter("redirect", StringPool.BLANK);
		portletURL.setParameter("backURL", _getCurrentURL());

		return DropdownItemBuilder.putData(
			"action", "publish"
		).putData(
			"publishURL", portletURL.toString()
		).setKey(
			DLUIItemKeys.PUBLISH
		).setLabel(
			LanguageUtil.get(_httpServletRequest, "publish-to-live")
		).build();
	}

	public DropdownItem createRevertVersionDropdownItem() {
		return DropdownItemBuilder.setHref(
			PortletURLBuilder.create(
				_getActionURL(
					"/document_library/edit_file_entry", Constants.REVERT)
			).setParameter(
				"fileEntryId", _fileEntry.getFileEntryId()
			).setParameter(
				"version", _fileVersion.getVersion()
			).buildString()
		).setLabel(
			LanguageUtil.get(_httpServletRequest, "revert")
		).build();
	}

	public DropdownItem createSubscribeDropdownItem() {
		return DropdownItemBuilder.putData(
			"action", "subscribeFileEntry"
		).putData(
			"subscribeFileEntryURL",
			PortletURLBuilder.createActionURL(
				_getLiferayPortletResponse()
			).setActionName(
				"/document_library/subscribe_file_entry"
			).setRedirect(
				_getCurrentURL()
			).setParameter(
				"fileEntryId", _fileEntry.getFileEntryId()
			).buildString()
		).setIcon(
			"bell-on"
		).setLabel(
			LanguageUtil.get(_httpServletRequest, "subscribe")
		).build();
	}

	public DropdownItem createUnsubscribeDropdownItem() throws PortalException {
		if (!DLSubscriptionUtil.isSubscribedToFolder(
				_themeDisplay.getCompanyId(), _themeDisplay.getScopeGroupId(),
				_themeDisplay.getUserId(), _fileEntry.getFolderId())) {

			return DropdownItemBuilder.putData(
				"action", "unsubscribeFileEntry"
			).putData(
				"unsubscribeFileEntryURL",
				PortletURLBuilder.createActionURL(
					_getLiferayPortletResponse()
				).setActionName(
					"/document_library/unsubscribe_file_entry"
				).setRedirect(
					_getCurrentURL()
				).setParameter(
					"fileEntryId", _fileEntry.getFileEntryId()
				).buildString()
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

	public DropdownItem createViewOriginalFileDropdownItem() {
		if (_fileShortcut == null) {
			return null;
		}

		return DropdownItemBuilder.setHref(
			PortletURLBuilder.create(
				_getRenderURL("/document_library/view_file_entry")
			).setParameter(
				"fileEntryId", _fileShortcut.getToFileEntryId()
			).buildString()
		).setKey(
			DLUIItemKeys.VIEW_ORIGINAL_FILE
		).setLabel(
			LanguageUtil.get(_httpServletRequest, "view-original-file")
		).build();
	}

	public DropdownItem createViewUsagesDropdownItem() {
		return DropdownItemBuilder.setDisabled(
			() -> {
				int count =
					LayoutClassedModelUsageLocalServiceUtil.
						getLayoutClassedModelUsagesCount(
							PortalUtil.getClassNameId(FileEntry.class),
							_fileEntry.getFileEntryId());

				if (count == 0) {
					return true;
				}

				return false;
			}
		).setHref(
			PortletURLBuilder.create(
				_getRenderURL("/document_library/view_file_entry_usages")
			).setParameter(
				"fileEntryId", _fileEntry.getFileEntryId()
			).buildString()
		).setIcon(
			"list-ul"
		).setLabel(
			LanguageUtil.get(_httpServletRequest, "view-usages")
		).build();
	}

	public DropdownItem createViewVersionDropdownItem() {
		return DropdownItemBuilder.setHref(
			PortletURLBuilder.create(
				_getRenderURL("/document_library/view_file_entry")
			).setBackURL(
				_getCurrentURL()
			).setParameter(
				"version", _fileVersion.getVersion()
			).buildString()
		).setIcon(
			"view"
		).setLabel(
			LanguageUtil.get(_httpServletRequest, "view[action]")
		).build();
	}

	public boolean isCancelCheckoutActionAvailable() throws PortalException {
		if ((_fileShortcut != null) ||
			!_fileEntryDisplayContextHelper.
				isCancelCheckoutDocumentActionAvailable() ||
			!_isFileVersionContentTypeAllowed()) {

			return false;
		}

		return true;
	}

	public boolean isCheckinActionAvailable() throws PortalException {
		if ((_fileShortcut != null) ||
			!_fileEntryDisplayContextHelper.isCheckinActionAvailable() ||
			!_isFileVersionContentTypeAllowed()) {

			return false;
		}

		return true;
	}

	public boolean isCheckoutActionAvailable() throws PortalException {
		if ((_fileShortcut != null) ||
			!_fileEntryDisplayContextHelper.
				isCheckoutDocumentActionAvailable() ||
			!_isFileVersionContentTypeAllowed()) {

			return false;
		}

		return true;
	}

	public boolean isCollectDigitalSignatureActionAvailable()
		throws PortalException {

		DigitalSignatureConfiguration digitalSignatureConfiguration =
			DigitalSignatureConfigurationUtil.getDigitalSignatureConfiguration(
				_themeDisplay.getCompanyId(), _themeDisplay.getSiteGroupId());

		if (!digitalSignatureConfiguration.enabled() ||
			!ArrayUtil.contains(
				DigitalSignatureConstants.ALLOWED_FILE_EXTENSIONS,
				_fileEntry.getExtension())) {

			return false;
		}

		return true;
	}

	public boolean isCompareToActionAvailable() {
		return DocumentConversionUtil.isComparableVersion(
			_fileVersion.getExtension());
	}

	public boolean isCopyActionAvailable() throws PortalException {
		if (((_fileShortcut != null) &&
			 !_fileShortcutDisplayContextHelper.isCopyActionAvailable()) ||
			((_fileShortcut == null) &&
			 !_fileEntryDisplayContextHelper.isCopyActionAvailable())) {

			return false;
		}

		return true;
	}

	public boolean isDeleteActionAvailable() throws PortalException {
		if (_isDeleteActionAvailable() ||
			_isMoveToTheRecycleBinActionAvailable()) {

			return true;
		}

		return false;
	}

	public boolean isDeleteVersionActionAvailable() throws PortalException {
		if ((_fileEntry == null) ||
			((_fileVersion.getStatus() != WorkflowConstants.STATUS_APPROVED) &&
			 (_fileVersion.getStatus() != WorkflowConstants.STATUS_EXPIRED) &&
			 (_fileVersion.getStatus() !=
				 WorkflowConstants.STATUS_SCHEDULED)) ||
			!_fileEntryDisplayContextHelper.hasDeletePermission() ||
			!(_fileEntry.getModel() instanceof DLFileEntry)) {

			return false;
		}

		int fileVersionsCount = _fileEntry.getFileVersionsCount(
			WorkflowConstants.STATUS_APPROVED);

		fileVersionsCount += _fileEntry.getFileVersionsCount(
			WorkflowConstants.STATUS_SCHEDULED);

		int fileVersionsExpired = _fileEntry.getFileVersionsCount(
			WorkflowConstants.STATUS_EXPIRED);

		if ((fileVersionsCount > 1) ||
			((_fileVersion.getStatus() == WorkflowConstants.STATUS_EXPIRED) &&
			 ((fileVersionsCount == 1) || (fileVersionsExpired > 1)))) {

			return true;
		}

		return false;
	}

	public boolean isDownloadActionAvailable() throws PortalException {
		if (!_isFileVersionContentTypeAllowed()) {
			return false;
		}

		return _fileEntryDisplayContextHelper.isDownloadActionAvailable();
	}

	public boolean isEditActionAvailable() throws PortalException {
		if (((_fileShortcut != null) &&
			 !_fileShortcutDisplayContextHelper.isEditActionAvailable()) ||
			((_fileShortcut == null) &&
			 !_fileEntryDisplayContextHelper.isEditActionAvailable())) {

			return false;
		}

		return true;
	}

	public boolean isEditImageActionAvailable() throws PortalException {
		if ((_fileShortcut != null) ||
			!_fileEntryDisplayContextHelper.
				isCheckoutDocumentActionAvailable() ||
			!ArrayUtil.contains(
				PropsValues.DL_FILE_ENTRY_PREVIEW_IMAGE_MIME_TYPES,
				_fileVersion.getMimeType())) {

			return false;
		}

		return true;
	}

	public boolean isHistoryActionAvailable() throws PortalException {
		if (_fileShortcut == null) {
			return true;
		}

		return false;
	}

	public boolean isMoveActionAvailable() throws PortalException {
		if (((_fileShortcut != null) &&
			 !_fileShortcutDisplayContextHelper.isMoveActionAvailable()) ||
			((_fileShortcut == null) &&
			 !_fileEntryDisplayContextHelper.isMoveActionAvailable())) {

			return false;
		}

		return true;
	}

	public boolean isPermissionsActionAvailable() throws PortalException {
		if (((_fileShortcut != null) &&
			 !_fileShortcutDisplayContextHelper.isPermissionsButtonVisible()) ||
			((_fileShortcut == null) &&
			 !_fileEntryDisplayContextHelper.isPermissionsButtonVisible())) {

			return false;
		}

		return true;
	}

	public boolean isPublishActionAvailable() throws PortalException {
		if (!_isFileVersionExportable(true)) {
			return false;
		}

		StagingGroupHelper stagingGroupHelper =
			StagingGroupHelperUtil.getStagingGroupHelper();

		if (!GroupPermissionUtil.contains(
				_themeDisplay.getPermissionChecker(),
				_themeDisplay.getScopeGroupId(),
				ActionKeys.EXPORT_IMPORT_PORTLET_INFO) ||
			!stagingGroupHelper.isStagingGroup(
				_themeDisplay.getScopeGroupId()) ||
			!stagingGroupHelper.isStagedPortlet(
				_themeDisplay.getScopeGroupId(),
				DLPortletKeys.DOCUMENT_LIBRARY)) {

			return false;
		}

		PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

		String portletName = portletDisplay.getPortletName();

		return portletName.equals(DLPortletKeys.DOCUMENT_LIBRARY_ADMIN);
	}

	public boolean isRevertToVersionActionAvailable() throws PortalException {
		if (((_fileVersion.getStatus() != WorkflowConstants.STATUS_APPROVED) &&
			 (_fileVersion.getStatus() !=
				 WorkflowConstants.STATUS_SCHEDULED)) ||
			!_fileEntryDisplayContextHelper.hasUpdatePermission()) {

			return false;
		}

		FileVersion latestFileVersion = _fileEntry.getLatestFileVersion();

		return !Objects.equals(
			latestFileVersion.getVersion(), _fileVersion.getVersion());
	}

	public boolean isSubscribeActionAvailable() throws PortalException {
		if (!_fileEntryDisplayContextHelper.hasSubscribePermission()) {
			return false;
		}

		return !isUnsubscribeActionAvailable();
	}

	public boolean isUnsubscribeActionAvailable() throws PortalException {
		if (_subscribed != null) {
			return _subscribed;
		}

		_subscribed = false;

		if (_fileEntryDisplayContextHelper.hasSubscribePermission()) {
			_subscribed = DLSubscriptionUtil.isSubscribedToFileEntry(
				_fileEntry.getCompanyId(), _themeDisplay.getScopeGroupId(),
				_themeDisplay.getUserId(), _fileEntry.getFileEntryId());
		}

		return _subscribed;
	}

	public boolean isViewOriginalFileActionAvailable() {
		if (_fileShortcut != null) {
			return true;
		}

		return false;
	}

	public boolean isViewVersionActionAvailable() {
		if (_fileShortcut == null) {
			return true;
		}

		return false;
	}

	private UIItemsBuilder(
		HttpServletRequest httpServletRequest, FileEntry fileEntry,
		FileShortcut fileShortcut, FileVersion fileVersion,
		DLTrashHelper dlTrashHelper, VersioningStrategy versioningStrategy,
		DLURLHelper dlURLHelper) {

		try {
			_httpServletRequest = httpServletRequest;

			if ((fileEntry == null) && (fileVersion != null)) {
				fileEntry = fileVersion.getFileEntry();
			}

			_fileEntry = fileEntry;

			_fileShortcut = fileShortcut;
			_fileVersion = fileVersion;
			_dlTrashHelper = dlTrashHelper;
			_versioningStrategy = versioningStrategy;
			_dlURLHelper = dlURLHelper;

			_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

			_fileEntryDisplayContextHelper = new FileEntryDisplayContextHelper(
				_themeDisplay.getPermissionChecker(), _fileEntry);

			_fileShortcutDisplayContextHelper =
				new FileShortcutDisplayContextHelper(
					_themeDisplay.getPermissionChecker(), _fileShortcut);
		}
		catch (PortalException portalException) {
			throw new SystemException(
				"Unable to build UIItemsBuilder for " + fileVersion,
				portalException);
		}
	}

	private String _addDoAsUserIdParameter(String url) {
		if (Validator.isNull(_themeDisplay.getDoAsUserId()) ||
			Validator.isNull(url)) {

			return url;
		}

		return HttpComponentsUtil.setParameter(
			url, "doAsUserId", _themeDisplay.getDoAsUserId());
	}

	private PortletURL _getActionURL(String mvcActionCommandName) {
		return _getActionURL(mvcActionCommandName, null);
	}

	private PortletURL _getActionURL(String mvcActionCommandName, String cmd) {
		return _getActionURL(mvcActionCommandName, cmd, _getCurrentURL());
	}

	private PortletURL _getActionURL(
		String mvcActionCommandName, String cmd, String redirect) {

		return PortletURLBuilder.createActionURL(
			_getLiferayPortletResponse()
		).setActionName(
			mvcActionCommandName
		).setCMD(
			() -> {
				if (Validator.isNotNull(cmd)) {
					return cmd;
				}

				return null;
			}
		).setRedirect(
			redirect
		).buildPortletURL();
	}

	private PortletURL _getControlPanelRenderURL(String mvcRenderCommandName) {
		return _getControlPanelRenderURL(
			mvcRenderCommandName, _getCurrentURL());
	}

	private PortletURL _getControlPanelRenderURL(
		String mvcRenderCommandName, String redirect) {

		PortletURL portletURL = PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				_getLiferayPortletRequest(), _themeDisplay.getScopeGroup(),
				DLPortletKeys.DOCUMENT_LIBRARY_ADMIN, 0, 0,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			mvcRenderCommandName
		).setRedirect(
			() -> {
				if (Validator.isNotNull(redirect)) {
					return redirect;
				}

				return null;
			}
		).buildPortletURL();

		if (_fileShortcut != null) {
			portletURL.setParameter(
				"fileShortcutId",
				String.valueOf(_fileShortcut.getFileShortcutId()));
		}
		else {
			portletURL.setParameter(
				"fileEntryId", String.valueOf(_fileEntry.getFileEntryId()));
		}

		PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

		String portletResource = portletDisplay.getId();

		if (!portletResource.equals(DLPortletKeys.DOCUMENT_LIBRARY_ADMIN)) {
			portletURL.setParameter("portletResource", portletResource);
		}

		return portletURL;
	}

	private PortletURL _getCopyEntryURL() {
		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				_getLiferayPortletRequest(), _themeDisplay.getScopeGroup(),
				DLPortletKeys.DOCUMENT_LIBRARY_ADMIN, 0, 0,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/document_library/copy_dl_objects"
		).setRedirect(
			_getCurrentURL()
		).setParameter(
			"dlObjectIds",
			() -> {
				if (_fileShortcut != null) {
					return _fileShortcut.getFileShortcutId();
				}

				return _fileEntry.getFileEntryId();
			}
		).setParameter(
			"sourceFolderId",
			() -> {
				if (_fileShortcut != null) {
					return _fileShortcut.getFolderId();
				}

				return _fileEntry.getFolderId();
			}
		).setParameter(
			"sourceRepositoryId",
			() -> {
				if (_fileShortcut != null) {
					return _fileShortcut.getRepositoryId();
				}

				return _fileEntry.getRepositoryId();
			}
		).buildPortletURL();
	}

	private String _getCurrentURL() {
		if (_currentURL != null) {
			return _currentURL;
		}

		PortletURL portletURL = PortletURLUtil.getCurrent(
			_getLiferayPortletRequest(), _getLiferayPortletResponse());

		_currentURL = portletURL.toString();

		return _currentURL;
	}

	private PortletURL _getDeleteActionURL(
		String mvcActionCommandName, String cmd) {

		String currentMVCRenderCommandName = ParamUtil.getString(
			_httpServletRequest, "mvcRenderCommandName");

		if (currentMVCRenderCommandName.equals(
				"/document_library/view_file_entry")) {

			String redirect = ParamUtil.getString(
				_httpServletRequest, "redirect");

			if (Validator.isNull(redirect)) {
				LiferayPortletResponse liferayPortletResponse =
					_getLiferayPortletResponse();

				redirect = String.valueOf(
					liferayPortletResponse.createRenderURL());
			}

			return _getActionURL(mvcActionCommandName, cmd, redirect);
		}

		return _getActionURL(mvcActionCommandName, cmd);
	}

	private LiferayPortletRequest _getLiferayPortletRequest() {
		PortletRequest portletRequest =
			(PortletRequest)_httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_REQUEST);

		return PortalUtil.getLiferayPortletRequest(portletRequest);
	}

	private LiferayPortletResponse _getLiferayPortletResponse() {
		PortletResponse portletResponse =
			(PortletResponse)_httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_RESPONSE);

		return PortalUtil.getLiferayPortletResponse(portletResponse);
	}

	private String _getNamespace() {
		LiferayPortletResponse liferayPortletResponse =
			_getLiferayPortletResponse();

		return liferayPortletResponse.getNamespace();
	}

	private String _getRedirect() {
		if (_redirect == null) {
			_redirect = ParamUtil.getString(_httpServletRequest, "redirect");
		}

		return _redirect;
	}

	private PortletURL _getRenderURL(String mvcRenderCommandName) {
		return _getRenderURL(mvcRenderCommandName, _getCurrentURL());
	}

	private PortletURL _getRenderURL(
		String mvcRenderCommandName, String redirect) {

		PortletURL portletURL = PortletURLBuilder.createRenderURL(
			_getLiferayPortletResponse()
		).setMVCRenderCommandName(
			mvcRenderCommandName
		).setRedirect(
			() -> {
				if (Validator.isNotNull(redirect)) {
					return redirect;
				}

				return null;
			}
		).buildPortletURL();

		if (_fileShortcut != null) {
			portletURL.setParameter(
				"fileShortcutId",
				String.valueOf(_fileShortcut.getFileShortcutId()));
		}
		else {
			portletURL.setParameter(
				"fileEntryId", String.valueOf(_fileEntry.getFileEntryId()));
		}

		return portletURL;
	}

	private boolean _isDeleteActionAvailable() throws PortalException {
		if (((_fileShortcut != null) &&
			 _fileShortcutDisplayContextHelper.isFileShortcutDeletable() &&
			 !_isFileShortcutTrashable()) ||
			((_fileShortcut == null) &&
			 _fileEntryDisplayContextHelper.isFileEntryDeletable() &&
			 !_isFileEntryTrashable())) {

			return true;
		}

		return false;
	}

	private boolean _isFileEntryTrashable() throws PortalException {
		if (_fileEntry.isRepositoryCapabilityProvided(TrashCapability.class) &&
			_isTrashEnabled()) {

			return true;
		}

		return false;
	}

	private boolean _isFileShortcutTrashable() throws PortalException {
		if (_fileShortcutDisplayContextHelper.isDLFileShortcut() &&
			_isTrashEnabled()) {

			return true;
		}

		return false;
	}

	private boolean _isFileVersionContentTypeAllowed() {
		return !Objects.equals(
			_fileVersion.getMimeType(),
			ContentTypes.APPLICATION_VND_LIFERAY_VIDEO_EXTERNAL_SHORTCUT_HTML);
	}

	private boolean _isFileVersionExportable(boolean latestVersion) {
		try {
			FileVersion fileVersion = _fileVersion;

			if (latestVersion) {
				if (_fileEntry == null) {
					return false;
				}

				fileVersion = _fileEntry.getLatestFileVersion();
			}

			if (fileVersion == null) {
				return false;
			}

			StagedModelDataHandler<FileEntry> stagedModelDataHandler =
				(StagedModelDataHandler<FileEntry>)
					StagedModelDataHandlerRegistryUtil.
						getStagedModelDataHandler(FileEntry.class.getName());

			return ArrayUtil.contains(
				stagedModelDataHandler.getExportableStatuses(),
				fileVersion.getStatus());
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return false;
		}
	}

	private boolean _isMoveToTheRecycleBinActionAvailable()
		throws PortalException {

		if (!_isDeleteActionAvailable() &&
			(((_fileShortcut != null) &&
			  _fileShortcutDisplayContextHelper.isFileShortcutDeletable()) ||
			 ((_fileShortcut == null) &&
			  _fileEntryDisplayContextHelper.isFileEntryDeletable()))) {

			return true;
		}

		return false;
	}

	private boolean _isTrashEnabled() throws PortalException {
		if (_trashEnabled != null) {
			return _trashEnabled;
		}

		_trashEnabled = false;

		if (_dlTrashHelper == null) {
			return _trashEnabled;
		}

		_trashEnabled = _dlTrashHelper.isTrashEnabled(
			_themeDisplay.getScopeGroupId(), _fileEntry.getRepositoryId());

		return _trashEnabled;
	}

	private static final Log _log = LogFactoryUtil.getLog(UIItemsBuilder.class);

	private String _currentURL;
	private final DLTrashHelper _dlTrashHelper;
	private final DLURLHelper _dlURLHelper;
	private final FileEntry _fileEntry;
	private final FileEntryDisplayContextHelper _fileEntryDisplayContextHelper;
	private final FileShortcut _fileShortcut;
	private final FileShortcutDisplayContextHelper
		_fileShortcutDisplayContextHelper;
	private final FileVersion _fileVersion;
	private final HttpServletRequest _httpServletRequest;
	private String _redirect;
	private Boolean _subscribed;
	private final ThemeDisplay _themeDisplay;
	private Boolean _trashEnabled;
	private final VersioningStrategy _versioningStrategy;

}