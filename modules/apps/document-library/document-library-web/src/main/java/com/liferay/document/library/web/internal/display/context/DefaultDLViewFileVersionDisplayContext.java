/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.display.context;

import com.liferay.document.library.display.context.DLMimeTypeDisplayContext;
import com.liferay.document.library.display.context.DLViewFileVersionDisplayContext;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.service.DLFileEntryMetadataLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFileVersionLocalServiceUtil;
import com.liferay.document.library.kernel.versioning.VersioningStrategy;
import com.liferay.document.library.preview.DLPreviewRenderer;
import com.liferay.document.library.preview.DLPreviewRendererProvider;
import com.liferay.document.library.preview.exception.DLFileEntryPreviewGenerationException;
import com.liferay.document.library.preview.exception.DLPreviewGenerationInProcessException;
import com.liferay.document.library.preview.exception.DLPreviewSizeException;
import com.liferay.document.library.util.DLFileEntryTypeUtil;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.document.library.web.internal.constants.DLWebKeys;
import com.liferay.document.library.web.internal.display.context.helper.DLPortletInstanceSettingsHelper;
import com.liferay.document.library.web.internal.display.context.helper.DLRequestHelper;
import com.liferay.document.library.web.internal.display.context.helper.FileEntryDisplayContextHelper;
import com.liferay.document.library.web.internal.display.context.helper.FileVersionDisplayContextHelper;
import com.liferay.document.library.web.internal.display.context.logic.UIItemsBuilder;
import com.liferay.document.library.web.internal.display.context.util.JSPRenderer;
import com.liferay.document.library.web.internal.helper.DLTrashHelper;
import com.liferay.dynamic.data.mapping.exception.StorageException;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.DDMStorageEngineManager;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileShortcut;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * @author Adolfo Pérez
 */
public class DefaultDLViewFileVersionDisplayContext
	implements DLViewFileVersionDisplayContext {

	public DefaultDLViewFileVersionDisplayContext(
			DDMStorageEngineManager ddmStorageEngineManager,
			DLMimeTypeDisplayContext dlMimeTypeDisplayContext,
			DLPreviewRendererProvider dlPreviewRendererProvider,
			DLTrashHelper dlTrashHelper, DLURLHelper dlURLHelper,
			FileShortcut fileShortcut, HttpServletRequest httpServletRequest,
			VersioningStrategy versioningStrategy)
		throws PortalException {

		this(
			httpServletRequest, fileShortcut.getFileVersion(), fileShortcut,
			ddmStorageEngineManager, dlMimeTypeDisplayContext, dlTrashHelper,
			dlPreviewRendererProvider, versioningStrategy, dlURLHelper);
	}

	public DefaultDLViewFileVersionDisplayContext(
		DDMStorageEngineManager ddmStorageEngineManager,
		DLMimeTypeDisplayContext dlMimeTypeDisplayContext,
		DLPreviewRendererProvider dlPreviewRendererProvider,
		DLTrashHelper dlTrashHelper, DLURLHelper dlURLHelper,
		FileVersion fileVersion, HttpServletRequest httpServletRequest,
		VersioningStrategy versioningStrategy) {

		this(
			httpServletRequest, fileVersion, null, ddmStorageEngineManager,
			dlMimeTypeDisplayContext, dlTrashHelper, dlPreviewRendererProvider,
			versioningStrategy, dlURLHelper);
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() throws PortalException {
		if (!isActionsVisible()) {
			return null;
		}

		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						_uiItemsBuilder::isDownloadActionAvailable,
						_uiItemsBuilder.createDownloadDropdownItem()
					).add(
						_uiItemsBuilder::isViewOriginalFileActionAvailable,
						_uiItemsBuilder.createViewOriginalFileDropdownItem()
					).add(
						_uiItemsBuilder::isEditActionAvailable,
						_uiItemsBuilder.createEditDropdownItem()
					).add(
						_uiItemsBuilder::isEditImageActionAvailable,
						_uiItemsBuilder.createEditImageDropdownItem()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						_uiItemsBuilder::isCheckoutActionAvailable,
						_uiItemsBuilder.createCheckoutDropdownItem()
					).add(
						_uiItemsBuilder::isCancelCheckoutActionAvailable,
						_uiItemsBuilder.createCancelCheckoutDropdownItem()
					).add(
						_uiItemsBuilder::isCheckinActionAvailable,
						_uiItemsBuilder.createCheckinDropdownItem()
					).add(
						_uiItemsBuilder::isSubscribeActionAvailable,
						_uiItemsBuilder.createSubscribeDropdownItem()
					).add(
						_uiItemsBuilder::isUnsubscribeActionAvailable,
						_uiItemsBuilder.createUnsubscribeDropdownItem()
					).add(
						_uiItemsBuilder::
							isCollectDigitalSignatureActionAvailable,
						_uiItemsBuilder.
							createCollectDigitalSignatureDropdownItem()
					).add(
						_uiItemsBuilder::isHistoryActionAvailable,
						_uiItemsBuilder.createHistoryDropdownItem()
					).add(
						_uiItemsBuilder.createViewUsagesDropdownItem()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						_uiItemsBuilder::isMoveActionAvailable,
						_uiItemsBuilder.createMoveDropdownItem()
					).add(
						_uiItemsBuilder::isCopyActionAvailable,
						_uiItemsBuilder.createCopyDropdownItem()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						_uiItemsBuilder::isPermissionsActionAvailable,
						_uiItemsBuilder.createPermissionsDropdownItem()
					).add(
						_uiItemsBuilder::isPublishActionAvailable,
						_uiItemsBuilder.createPublishDropdownItem()
					).add(
						_uiItemsBuilder::isDeleteActionAvailable,
						_uiItemsBuilder.createDeleteDropdownItem()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).build();
	}

	@Override
	public String getCssClassFileMimeType() {
		if (_dlMimeTypeDisplayContext == null) {
			return "file-icon-color-0";
		}

		return _dlMimeTypeDisplayContext.getCssClassFileMimeType(
			_fileVersion.getMimeType());
	}

	@Override
	public DDMFormValues getDDMFormValues(DDMStructure ddmStructure)
		throws PortalException {

		DLFileEntryMetadata dlFileEntryMetadata =
			DLFileEntryMetadataLocalServiceUtil.getFileEntryMetadata(
				ddmStructure.getStructureId(), _fileVersion.getFileVersionId());

		return _ddmStorageEngineManager.getDDMFormValues(
			dlFileEntryMetadata.getDDMStorageId());
	}

	@Override
	public DDMFormValues getDDMFormValues(long classPK)
		throws StorageException {

		try {
			return _ddmStorageEngineManager.getDDMFormValues(classPK);
		}
		catch (PortalException portalException) {
			throw new StorageException(portalException);
		}
	}

	@Override
	public List<DDMStructure> getDDMStructures() throws PortalException {
		if (_ddmStructures != null) {
			return _ddmStructures;
		}

		if (_fileVersionDisplayContextHelper.isDLFileVersion()) {
			_ddmStructures = _getVisibleDDMStructures();
		}
		else {
			_ddmStructures = Collections.emptyList();
		}

		return _ddmStructures;
	}

	@Override
	public int getDDMStructuresCount() throws PortalException {
		List<DDMStructure> ddmStructures = getDDMStructures();

		return ddmStructures.size();
	}

	@Override
	public String getDiscussionClassName() {
		return DLFileEntryConstants.getClassName();
	}

	@Override
	public long getDiscussionClassPK() {
		return _fileVersion.getFileEntryId();
	}

	@Override
	public String getDiscussionLabel(Locale locale) {
		return LanguageUtil.get(_httpServletRequest, "comments");
	}

	@Override
	public String getIconFileMimeType() {
		if (_dlMimeTypeDisplayContext == null) {
			return "document-default";
		}

		return _dlMimeTypeDisplayContext.getIconFileMimeType(
			_fileVersion.getMimeType());
	}

	@Override
	public UUID getUuid() {
		return _UUID;
	}

	@Override
	public boolean hasApprovedVersion() {
		DLFileVersion dlFileVersion =
			DLFileVersionLocalServiceUtil.fetchLatestFileVersion(
				_fileVersion.getFileEntryId(), false,
				WorkflowConstants.STATUS_APPROVED);

		if (dlFileVersion == null) {
			return false;
		}

		return true;
	}

	@Override
	public boolean hasCustomThumbnail() {
		if (_dlPreviewRendererProvider != null) {
			DLPreviewRenderer dlPreviewRenderer =
				_dlPreviewRendererProvider.getThumbnailDLPreviewRenderer(
					_fileVersion);

			if (dlPreviewRenderer != null) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean hasPreview() {
		if ((_dlPreviewRendererProvider == null) ||
			_isSystemDLFileEntryType()) {

			return false;
		}

		DLPreviewRenderer dlPreviewRenderer =
			_dlPreviewRendererProvider.getPreviewDLPreviewRenderer(
				_fileVersion);

		if (dlPreviewRenderer == null) {
			return false;
		}

		return true;
	}

	@Override
	public boolean isActionsVisible() {
		return _dlPortletInstanceSettingsHelper.isShowActions();
	}

	@Override
	public boolean isDownloadLinkVisible() throws PortalException {
		if (_isSystemDLFileEntryType() ||
			!_fileEntryDisplayContextHelper.isDownloadActionAvailable()) {

			return false;
		}

		return true;
	}

	@Override
	public boolean isSharingLinkVisible() {
		return false;
	}

	@Override
	public boolean isVersionInfoVisible() {
		return !_isSystemDLFileEntryType();
	}

	@Override
	public void renderCustomThumbnail(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		DLPreviewRenderer dlPreviewRenderer = null;

		if (_dlPreviewRendererProvider != null) {
			dlPreviewRenderer =
				_dlPreviewRendererProvider.getThumbnailDLPreviewRenderer(
					_fileVersion);
		}

		_renderPreview(
			httpServletRequest, httpServletResponse, dlPreviewRenderer);
	}

	@Override
	public void renderPreview(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		DLPreviewRenderer dlPreviewRenderer = null;

		if (_dlPreviewRendererProvider != null) {
			dlPreviewRenderer =
				_dlPreviewRendererProvider.getPreviewDLPreviewRenderer(
					_fileVersion);
		}

		_renderPreview(
			httpServletRequest, httpServletResponse, dlPreviewRenderer);
	}

	private DefaultDLViewFileVersionDisplayContext(
		HttpServletRequest httpServletRequest, FileVersion fileVersion,
		FileShortcut fileShortcut,
		DDMStorageEngineManager ddmStorageEngineManager,
		DLMimeTypeDisplayContext dlMimeTypeDisplayContext,
		DLTrashHelper dlTrashHelper,
		DLPreviewRendererProvider dlPreviewRendererProvider,
		VersioningStrategy versioningStrategy, DLURLHelper dlURLHelper) {

		try {
			_httpServletRequest = httpServletRequest;
			_fileVersion = fileVersion;
			_ddmStorageEngineManager = ddmStorageEngineManager;
			_dlMimeTypeDisplayContext = dlMimeTypeDisplayContext;
			_dlPreviewRendererProvider = dlPreviewRendererProvider;

			DLRequestHelper dlRequestHelper = new DLRequestHelper(
				httpServletRequest);

			_dlPortletInstanceSettingsHelper =
				new DLPortletInstanceSettingsHelper(dlRequestHelper);

			FileEntry fileEntry = _getFileEntry(fileVersion);

			_fileEntryDisplayContextHelper = new FileEntryDisplayContextHelper(
				dlRequestHelper.getPermissionChecker(), fileEntry);

			_fileVersionDisplayContextHelper =
				new FileVersionDisplayContextHelper(fileVersion);

			if (fileShortcut == null) {
				_uiItemsBuilder = new UIItemsBuilder(
					httpServletRequest, fileEntry, fileVersion, dlTrashHelper,
					versioningStrategy, dlURLHelper);
			}
			else {
				_uiItemsBuilder = new UIItemsBuilder(
					httpServletRequest, fileShortcut, dlTrashHelper,
					versioningStrategy, dlURLHelper);
			}
		}
		catch (PortalException portalException) {
			throw new SystemException(
				"Unable to build DefaultDLViewFileVersionDisplayContext for " +
					fileVersion,
				portalException);
		}
	}

	private DLFileEntryType _getDLFileEntryType() {
		try {
			if (_dlFileEntryType != null) {
				return _dlFileEntryType;
			}

			if (!_fileVersionDisplayContextHelper.isDLFileVersion()) {
				return null;
			}

			FileEntry fileEntry = _getFileEntry(_fileVersion);

			if (fileEntry == null) {
				return null;
			}

			DLFileEntry dlFileEntry = (DLFileEntry)fileEntry.getModel();

			_dlFileEntryType = dlFileEntry.getDLFileEntryType();

			return _dlFileEntryType;
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return null;
		}
	}

	private FileEntry _getFileEntry(FileVersion fileVersion)
		throws PortalException {

		if (fileVersion != null) {
			return fileVersion.getFileEntry();
		}

		return null;
	}

	private List<DDMStructure> _getVisibleDDMStructures()
		throws PortalException {

		DLFileEntryType dlFileEntryType = _getDLFileEntryType();

		if (dlFileEntryType == null) {
			return Collections.emptyList();
		}

		return ListUtil.filter(
			DLFileEntryTypeUtil.getDDMStructures(dlFileEntryType),
			ddmStructure ->
				(ddmStructure.getStructureId() !=
					dlFileEntryType.getDataDefinitionId()) ||
				(dlFileEntryType.getScope() !=
					DLFileEntryTypeConstants.FILE_ENTRY_TYPE_SCOPE_SYSTEM));
	}

	private void _handleError(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Exception exception)
		throws IOException, ServletException {

		JSPRenderer jspRenderer = new JSPRenderer(
			"/document_library/view_file_entry_preview_error.jsp");

		jspRenderer.setAttribute(
			WebKeys.DOCUMENT_LIBRARY_FILE_VERSION, _fileVersion);

		if (exception != null) {
			jspRenderer.setAttribute(
				DLWebKeys.DOCUMENT_LIBRARY_PREVIEW_EXCEPTION, exception);
		}

		jspRenderer.render(httpServletRequest, httpServletResponse);
	}

	private boolean _isSystemDLFileEntryType() {
		DLFileEntryType dlFileEntryType = _getDLFileEntryType();

		if ((dlFileEntryType == null) ||
			(dlFileEntryType.getScope() !=
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_SCOPE_SYSTEM)) {

			return false;
		}

		return true;
	}

	private void _renderPreview(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse,
			DLPreviewRenderer dlPreviewRenderer)
		throws IOException, ServletException {

		try {
			if (dlPreviewRenderer == null) {
				_handleError(httpServletRequest, httpServletResponse, null);

				return;
			}

			dlPreviewRenderer.render(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			if (exception instanceof DLFileEntryPreviewGenerationException ||
				exception instanceof DLPreviewGenerationInProcessException ||
				exception instanceof DLPreviewSizeException) {

				if (_log.isWarnEnabled()) {
					_log.warn(exception);
				}
			}
			else {
				_log.error(
					"Unable to render preview for file version: " +
						_fileVersion.getTitle(),
					exception);
			}

			_handleError(httpServletRequest, httpServletResponse, exception);
		}
	}

	private static final UUID _UUID = UUID.fromString(
		"85F6C50E-3893-4E32-9D63-208528A503FA");

	private static final Log _log = LogFactoryUtil.getLog(
		DefaultDLViewFileVersionDisplayContext.class);

	private final DDMStorageEngineManager _ddmStorageEngineManager;
	private List<DDMStructure> _ddmStructures;
	private DLFileEntryType _dlFileEntryType;
	private final DLMimeTypeDisplayContext _dlMimeTypeDisplayContext;
	private final DLPortletInstanceSettingsHelper
		_dlPortletInstanceSettingsHelper;
	private DLPreviewRendererProvider _dlPreviewRendererProvider;
	private final FileEntryDisplayContextHelper _fileEntryDisplayContextHelper;
	private final FileVersion _fileVersion;
	private final FileVersionDisplayContextHelper
		_fileVersionDisplayContextHelper;
	private final HttpServletRequest _httpServletRequest;
	private final UIItemsBuilder _uiItemsBuilder;

}