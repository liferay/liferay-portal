/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.opener.google.drive.web.internal.display.context;

import com.liferay.document.library.display.context.BaseDLViewFileVersionDisplayContext;
import com.liferay.document.library.display.context.DLUIItemKeys;
import com.liferay.document.library.display.context.DLViewFileVersionDisplayContext;
import com.liferay.document.library.opener.constants.DLOpenerFileEntryReferenceConstants;
import com.liferay.document.library.opener.google.drive.web.internal.DLOpenerGoogleDriveManager;
import com.liferay.document.library.opener.google.drive.web.internal.constants.DLOpenerGoogleDriveConstants;
import com.liferay.document.library.opener.google.drive.web.internal.constants.DLOpenerGoogleDriveMimeTypes;
import com.liferay.document.library.opener.model.DLOpenerFileEntryReference;
import com.liferay.document.library.opener.service.DLOpenerFileEntryReferenceLocalService;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemBuilder;
import com.liferay.petra.string.StringPool;
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

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.UUID;

/**
 * @author Adolfo Pérez
 */
public class DLOpenerGoogleDriveDLViewFileVersionDisplayContext
	extends BaseDLViewFileVersionDisplayContext {

	public DLOpenerGoogleDriveDLViewFileVersionDisplayContext(
		DLViewFileVersionDisplayContext parentDLDisplayContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, FileVersion fileVersion,
		ResourceBundle resourceBundle,
		ModelResourcePermission<FileEntry> fileEntryModelResourcePermission,
		DLOpenerFileEntryReferenceLocalService
			dlOpenerFileEntryReferenceLocalService,
		DLOpenerGoogleDriveManager dlOpenerGoogleDriveManager, Portal portal) {

		super(
			_UUID, parentDLDisplayContext, httpServletRequest,
			httpServletResponse, fileVersion);

		_resourceBundle = resourceBundle;
		_fileEntryModelResourcePermission = fileEntryModelResourcePermission;
		_dlOpenerFileEntryReferenceLocalService =
			dlOpenerFileEntryReferenceLocalService;
		_dlOpenerGoogleDriveManager = dlOpenerGoogleDriveManager;
		_portal = portal;

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		_permissionChecker = themeDisplay.getPermissionChecker();
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() throws PortalException {
		if (!isActionsVisible() ||
			!DLOpenerGoogleDriveMimeTypes.isGoogleMimeTypeSupported(
				fileVersion.getMimeType()) ||
			!_dlOpenerGoogleDriveManager.isConfigured(
				fileVersion.getCompanyId()) ||
			!_fileEntryModelResourcePermission.contains(
				_permissionChecker, fileVersion.getFileEntry(),
				ActionKeys.UPDATE)) {

			return super.getActionDropdownItems();
		}

		List<DropdownItem> dropdownItems = super.getActionDropdownItems();

		FileEntry fileEntry = fileVersion.getFileEntry();

		if (_isCheckedOutInGoogleDrive()) {
			if (fileEntry.hasLock()) {
				_updateCancelCheckoutAndCheckinDropdownItems(dropdownItems);

				_addEditInGoogleDocsDropdownItem(
					dropdownItems,
					_createEditInGoogleDocsDropdownItem(Constants.EDIT));
			}

			return dropdownItems;
		}

		if (!_isCheckedOutByAnotherUser(fileEntry)) {
			_addEditInGoogleDocsDropdownItem(
				dropdownItems,
				_createEditInGoogleDocsDropdownItem(Constants.CHECKOUT));
		}

		return dropdownItems;
	}

	private List<DropdownItem> _addEditInGoogleDocsDropdownItem(
		List<DropdownItem> dropdownItems,
		DropdownItem editInGoogleDocsDropdownItem) {

		if (_addEditInGoogleDocsDropdownItemGroup(
				dropdownItems, editInGoogleDocsDropdownItem)) {

			return dropdownItems;
		}

		dropdownItems.add(editInGoogleDocsDropdownItem);

		return dropdownItems;
	}

	private boolean _addEditInGoogleDocsDropdownItemGroup(
		List<DropdownItem> dropdownItems,
		DropdownItem editInGoogleDocsDropdownItem) {

		int i = 1;

		for (DropdownItem dropdownItem : dropdownItems) {
			if (Objects.equals(dropdownItem.get("type"), "group")) {
				if (_addEditInGoogleDocsDropdownItemGroup(
						(List<DropdownItem>)dropdownItem.get("items"),
						editInGoogleDocsDropdownItem)) {

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
			dropdownItems.add(i, editInGoogleDocsDropdownItem);

			return true;
		}

		return false;
	}

	private DropdownItem _createEditInGoogleDocsDropdownItem(String cmd)
		throws PortalException {

		return DropdownItemBuilder.setHref(
			_getActionURL(cmd)
		).setKey(
			"#edit-in-google-drive"
		).setLabel(
			LanguageUtil.get(_resourceBundle, _getLabelKey())
		).build();
	}

	private String _getActionURL(String cmd) throws PortalException {
		return PortletURLBuilder.create(
			PortletURLFactoryUtil.create(
				request, _portal.getPortletId(request),
				PortletRequest.ACTION_PHASE)
		).setActionName(
			"/document_library/edit_in_google_docs"
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
		).setParameter(
			"googleDocsRedirect", _portal.getCurrentURL(request)
		).buildString();
	}

	private String _getLabelKey() {
		String googleDocsMimeType =
			DLOpenerGoogleDriveMimeTypes.getGoogleDocsMimeType(
				fileVersion.getMimeType());

		if (DLOpenerGoogleDriveMimeTypes.
				APPLICATION_VND_GOOGLE_APPS_PRESENTATION.equals(
					googleDocsMimeType)) {

			return "edit-in-google-slides";
		}

		if (DLOpenerGoogleDriveMimeTypes.
				APPLICATION_VND_GOOGLE_APPS_SPREADSHEET.equals(
					googleDocsMimeType)) {

			return "edit-in-google-sheets";
		}

		return "edit-in-google-docs";
	}

	private boolean _isCheckedOutByAnotherUser(FileEntry fileEntry) {
		if (fileEntry.isCheckedOut() && !fileEntry.hasLock()) {
			return true;
		}

		return false;
	}

	private boolean _isCheckedOutInGoogleDrive() throws PortalException {
		FileEntry fileEntry = fileVersion.getFileEntry();

		if (fileEntry.isCheckedOut() &&
			_dlOpenerGoogleDriveManager.isGoogleDriveFile(fileEntry)) {

			return true;
		}

		return false;
	}

	private boolean _isCheckingInNewFile() throws PortalException {
		DLOpenerFileEntryReference dlOpenerFileEntryReference =
			_dlOpenerFileEntryReferenceLocalService.
				getDLOpenerFileEntryReference(
					DLOpenerGoogleDriveConstants.GOOGLE_DRIVE_REFERENCE_TYPE,
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
					dropdownItem.setData(new HashMap<>());
					dropdownItem.setHref(_getActionURL(Constants.CHECKIN));
				}
				else {
					dropdownItem.setData(
						HashMapBuilder.<String, Object>put(
							"action", "checkin"
						).put(
							"checkinURL", _getActionURL(Constants.CHECKIN)
						).build());
					dropdownItem.setHref(StringPool.BLANK);
				}
			}
			else if (DLUIItemKeys.CANCEL_CHECKOUT.equals(
						dropdownItem.get("key"))) {

				dropdownItem.setHref(_getActionURL(Constants.CANCEL_CHECKOUT));
			}
		}
	}

	private static final UUID _UUID = UUID.fromString(
		"c3a385d0-7551-11e8-9798-186590d14d8f");

	private final DLOpenerFileEntryReferenceLocalService
		_dlOpenerFileEntryReferenceLocalService;
	private final DLOpenerGoogleDriveManager _dlOpenerGoogleDriveManager;
	private final ModelResourcePermission<FileEntry>
		_fileEntryModelResourcePermission;
	private final PermissionChecker _permissionChecker;
	private final Portal _portal;
	private final ResourceBundle _resourceBundle;

}