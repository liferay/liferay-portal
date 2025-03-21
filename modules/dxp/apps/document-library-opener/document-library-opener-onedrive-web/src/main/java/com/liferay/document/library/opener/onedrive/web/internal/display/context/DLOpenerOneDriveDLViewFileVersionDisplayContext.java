/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.opener.onedrive.web.internal.display.context;

import com.liferay.document.library.display.context.BaseDLViewFileVersionDisplayContext;
import com.liferay.document.library.display.context.DLUIItemKeys;
import com.liferay.document.library.display.context.DLViewFileVersionDisplayContext;
import com.liferay.document.library.opener.constants.DLOpenerFileEntryReferenceConstants;
import com.liferay.document.library.opener.constants.DLOpenerMimeTypes;
import com.liferay.document.library.opener.model.DLOpenerFileEntryReference;
import com.liferay.document.library.opener.onedrive.web.internal.DLOpenerOneDriveManager;
import com.liferay.document.library.opener.onedrive.web.internal.constants.DLOpenerOneDriveConstants;
import com.liferay.document.library.opener.onedrive.web.internal.constants.DLOpenerOneDriveMimeTypes;
import com.liferay.document.library.opener.service.DLOpenerFileEntryReferenceLocalService;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemBuilder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.UUID;

/**
 * @author Cristina González
 */
public class DLOpenerOneDriveDLViewFileVersionDisplayContext
	extends BaseDLViewFileVersionDisplayContext {

	public DLOpenerOneDriveDLViewFileVersionDisplayContext(
		DLViewFileVersionDisplayContext parentDLDisplayContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, FileVersion fileVersion,
		ResourceBundle resourceBundle,
		ModelResourcePermission<FileEntry> fileEntryModelResourcePermission,
		DLOpenerFileEntryReferenceLocalService
			dlOpenerFileEntryReferenceLocalService,
		DLOpenerOneDriveManager dlOpenerOneDriveManager, Portal portal) {

		super(
			_UUID, parentDLDisplayContext, httpServletRequest,
			httpServletResponse, fileVersion);

		_resourceBundle = resourceBundle;
		_fileEntryModelResourcePermission = fileEntryModelResourcePermission;
		_dlOpenerFileEntryReferenceLocalService =
			dlOpenerFileEntryReferenceLocalService;
		_dlOpenerOneDriveManager = dlOpenerOneDriveManager;
		_portal = portal;

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		_permissionChecker = themeDisplay.getPermissionChecker();
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() throws PortalException {
		if (!isActionsVisible() ||
			!DLOpenerOneDriveMimeTypes.isOffice365MimeTypeSupported(
				fileVersion.getMimeType()) ||
			!_dlOpenerOneDriveManager.isConfigured(
				fileVersion.getCompanyId()) ||
			!_fileEntryModelResourcePermission.contains(
				_permissionChecker, fileVersion.getFileEntry(),
				ActionKeys.UPDATE)) {

			return super.getActionDropdownItems();
		}

		List<DropdownItem> dropdownItems = super.getActionDropdownItems();

		if (_isCheckedOutInOneDrive()) {
			FileEntry fileEntry = fileVersion.getFileEntry();

			if (fileEntry.hasLock()) {
				_updateCancelCheckoutAndCheckinDropdownItems(dropdownItems);

				_addEditInOffice365DropdownItem(
					dropdownItems,
					_createEditInOffice365DropdownItem(Constants.EDIT));
			}

			return dropdownItems;
		}

		_addEditInOffice365DropdownItem(
			dropdownItems,
			_createEditInOffice365DropdownItem(Constants.CHECKOUT));

		return dropdownItems;
	}

	private List<DropdownItem> _addEditInOffice365DropdownItem(
		List<DropdownItem> dropdownItems,
		DropdownItem editInOffice365DropdownItem) {

		if (_addEditInOffice365DropdownItemGroup(
				dropdownItems, editInOffice365DropdownItem)) {

			return dropdownItems;
		}

		dropdownItems.add(editInOffice365DropdownItem);

		return dropdownItems;
	}

	private boolean _addEditInOffice365DropdownItemGroup(
		List<DropdownItem> dropdownItems,
		DropdownItem editInOffice365DropdownItem) {

		int i = 1;

		for (DropdownItem dropdownItem : dropdownItems) {
			if (Objects.equals(dropdownItem.get("type"), "group")) {
				if (_addEditInOffice365DropdownItemGroup(
						(List<DropdownItem>)dropdownItem.get("items"),
						editInOffice365DropdownItem)) {

					return true;
				}
			}
			else if (Objects.equals(
						DLUIItemKeys.EDIT, dropdownItem.get("key"))) {

				break;
			}

			i++;
		}

		if (i < dropdownItems.size()) {
			dropdownItems.add(i, editInOffice365DropdownItem);

			return true;
		}

		return false;
	}

	private DropdownItem _createEditInOffice365DropdownItem(String cmd)
		throws PortalException {

		return DropdownItemBuilder.putData(
			"action", "editOfficeDocument"
		).putData(
			"editURL", _getEditURL(cmd, "/document_library/edit_in_one_drive")
		).setLabel(
			LanguageUtil.get(_resourceBundle, _getLabelKey())
		).build();
	}

	private String _getCancelCheckOutURL() throws PortalException {
		return PortletURLBuilder.create(
			PortletURLFactoryUtil.create(
				request, _portal.getPortletId(request),
				PortletRequest.ACTION_PHASE)
		).setActionName(
			"/document_library/cancel_check_out_in_one_drive"
		).setParameter(
			"fileEntryId", fileVersion.getFileEntryId()
		).setParameter(
			"folderId",
			() -> {
				FileEntry fileEntry = fileVersion.getFileEntry();

				return fileEntry.getFolderId();
			}
		).buildString();
	}

	private String _getCheckInURL() throws PortalException {
		return PortletURLBuilder.create(
			PortletURLFactoryUtil.create(
				request, _portal.getPortletId(request),
				PortletRequest.ACTION_PHASE)
		).setActionName(
			"/document_library/check_in_in_one_drive"
		).setParameter(
			"fileEntryId", fileVersion.getFileEntryId()
		).setParameter(
			"folderId",
			() -> {
				FileEntry fileEntry = fileVersion.getFileEntry();

				return fileEntry.getFolderId();
			}
		).buildString();
	}

	private String _getEditURL(String cmd, String url) throws PortalException {
		return PortletURLBuilder.create(
			PortletURLFactoryUtil.create(
				request, _portal.getPortletId(request),
				PortletRequest.ACTION_PHASE)
		).setActionName(
			url
		).setCMD(
			cmd
		).setParameter(
			"fileEntryId", fileVersion.getFileEntryId()
		).setParameter(
			"folderId",
			() -> {
				FileEntry fileEntry = fileVersion.getFileEntry();

				return fileEntry.getFolderId();
			}
		).buildString();
	}

	private String _getLabelKey() {
		String office365MimeType =
			DLOpenerOneDriveMimeTypes.getOffice365MimeType(
				fileVersion.getMimeType());

		if (DLOpenerMimeTypes.APPLICATION_VND_PPTX.equals(office365MimeType) ||
			DLOpenerMimeTypes.APPLICATION_VND_XLSX.equals(office365MimeType)) {

			return "edit-in-office365";
		}

		return "edit-in-office365";
	}

	private boolean _isCheckedOutInOneDrive() throws PortalException {
		FileEntry fileEntry = fileVersion.getFileEntry();

		if (fileEntry.isCheckedOut() &&
			_dlOpenerOneDriveManager.isOneDriveFile(fileEntry)) {

			return true;
		}

		return false;
	}

	private boolean _isCheckingInNewFile() throws PortalException {
		DLOpenerFileEntryReference dlOpenerFileEntryReference =
			_dlOpenerFileEntryReferenceLocalService.
				getDLOpenerFileEntryReference(
					DLOpenerOneDriveConstants.ONE_DRIVE_REFERENCE_TYPE,
					fileVersion.getFileEntry());

		if (dlOpenerFileEntryReference.getType() ==
				DLOpenerFileEntryReferenceConstants.TYPE_NEW) {

			return true;
		}

		return false;
	}

	private void _updateCancelCheckoutAndCheckinDropdownItems(
			List<DropdownItem> dropdownItems)
		throws PortalException {

		for (DropdownItem dropdownItem : dropdownItems) {
			if (Objects.equals(dropdownItem.get("type"), "group")) {
				_updateCancelCheckoutAndCheckinDropdownItems(
					(List<DropdownItem>)dropdownItem.get("items"));
			}
			else if (DLUIItemKeys.CHECKIN.equals(dropdownItem.get("key"))) {
				if (_isCheckingInNewFile()) {
					dropdownItem.setHref(_getCheckInURL());
				}
				else {
					dropdownItem.setData(
						HashMapBuilder.<String, Object>put(
							"action", "checkin"
						).put(
							"checkinURL", _getCheckInURL()
						).build());
				}
			}
			else if (DLUIItemKeys.CANCEL_CHECKOUT.equals(
						dropdownItem.get("key"))) {

				dropdownItem.setHref(_getCancelCheckOutURL());
			}
		}
	}

	private static final UUID _UUID = UUID.fromString(
		"9ad58b57-aa3c-44f9-91bd-9385f0a925bf");

	private final DLOpenerFileEntryReferenceLocalService
		_dlOpenerFileEntryReferenceLocalService;
	private final DLOpenerOneDriveManager _dlOpenerOneDriveManager;
	private final ModelResourcePermission<FileEntry>
		_fileEntryModelResourcePermission;
	private final PermissionChecker _permissionChecker;
	private final Portal _portal;
	private final ResourceBundle _resourceBundle;

}