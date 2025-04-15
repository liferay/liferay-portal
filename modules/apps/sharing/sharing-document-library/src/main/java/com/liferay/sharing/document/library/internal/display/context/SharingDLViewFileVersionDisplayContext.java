/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.document.library.internal.display.context;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.display.context.BaseDLViewFileVersionDisplayContext;
import com.liferay.document.library.display.context.DLUIItemKeys;
import com.liferay.document.library.display.context.DLViewFileVersionDisplayContext;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownGroupItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.settings.FallbackKeysSettingsUtil;
import com.liferay.portal.kernel.settings.PortletInstanceSettingsLocator;
import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.settings.TypedSettings;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.sharing.configuration.SharingConfiguration;
import com.liferay.sharing.display.context.util.SharingDropdownItemFactory;
import com.liferay.sharing.security.permission.SharingPermission;
import com.liferay.sharing.service.SharingEntryLocalService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Sergio González
 */
public class SharingDLViewFileVersionDisplayContext
	extends BaseDLViewFileVersionDisplayContext {

	public SharingDLViewFileVersionDisplayContext(
		DLViewFileVersionDisplayContext parentDLDisplayContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, FileEntry fileEntry,
		FileVersion fileVersion,
		SharingEntryLocalService sharingEntryLocalService,
		SharingDropdownItemFactory sharingDropdownItemFactory,
		SharingPermission sharingPermission,
		SharingConfiguration sharingConfiguration) {

		super(
			_UUID, parentDLDisplayContext, httpServletRequest,
			httpServletResponse, fileVersion);

		_httpServletRequest = httpServletRequest;
		_fileEntry = fileEntry;
		_sharingEntryLocalService = sharingEntryLocalService;
		_sharingDropdownItemFactory = sharingDropdownItemFactory;
		_sharingPermission = sharingPermission;
		_sharingConfiguration = sharingConfiguration;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() throws PortalException {
		List<DropdownItem> dropdownItems = super.getActionDropdownItems();

		if (dropdownItems == null) {
			dropdownItems = new ArrayList<>();
		}

		return _addSharingDropdownItem(dropdownItems);
	}

	@Override
	public boolean isShared() throws PortalException {
		if (_themeDisplay.isSignedIn() && isSharingLinkVisible()) {
			int sharingEntriesCount =
				_sharingEntryLocalService.getSharingEntriesCount(
					PortalUtil.getClassNameId(
						DLFileEntryConstants.getClassName()),
					_fileEntry.getFileEntryId());

			if (sharingEntriesCount > 0) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isSharingLinkVisible() throws PortalException {
		if (_sharingConfiguration.isEnabled() &&
			_sharingPermission.containsSharePermission(
				_themeDisplay.getPermissionChecker(),
				PortalUtil.getClassNameId(DLFileEntryConstants.getClassName()),
				_fileEntry.getFileEntryId(), _themeDisplay.getScopeGroupId())) {

			return true;
		}

		return false;
	}

	private List<DropdownItem> _addSharingDropdownItem(
			List<DropdownItem> dropdownItems)
		throws PortalException {

		int i = 1;

		for (DropdownItem dropdownItem : dropdownItems) {
			if (dropdownItem instanceof DropdownGroupItem) {
				DropdownGroupItem dropdownGroupItem =
					(DropdownGroupItem)dropdownItem;

				if (_addSharingDropdownItemGroup(
						(List<DropdownItem>)dropdownGroupItem.get("items"))) {

					return dropdownItems;
				}
			}
			else if (Objects.equals(
						DLUIItemKeys.DOWNLOAD, dropdownItem.get("key"))) {

				break;
			}

			i++;
		}

		if (FeatureFlagManagerUtil.isEnabled("LPS-197477")) {
			if (_isSharingEnabled()) {
				dropdownItems.addAll(
					Math.min(i, dropdownItems.size()),
					DropdownItemListBuilder.addContext(
						_sharingDropdownItemFactory.
							createShareActionUnsafeConsumer(
								DLFileEntryConstants.getClassName(),
								_fileEntry.getFileEntryId(),
								_httpServletRequest)
					).build());
			}
			else {
				dropdownItems.add(
					Math.min(i, dropdownItems.size()),
					_sharingDropdownItemFactory.createCopyLinkDropdownItem(
						DLFileEntryConstants.getClassName(),
						_fileEntry.getFileEntryId(), _httpServletRequest));
			}

			return dropdownItems;
		}

		if (_isSharingEnabled()) {
			dropdownItems.add(
				Math.min(i, dropdownItems.size()),
				_sharingDropdownItemFactory.createShareDropdownItem(
					DLFileEntryConstants.getClassName(),
					_fileEntry.getFileEntryId(), _httpServletRequest));
		}

		return dropdownItems;
	}

	private boolean _addSharingDropdownItemGroup(
			List<DropdownItem> dropdownItems)
		throws PortalException {

		int i = 1;

		for (DropdownItem dropdownItem : dropdownItems) {
			if (dropdownItem instanceof DropdownGroupItem) {
				DropdownGroupItem dropdownGroupItem =
					(DropdownGroupItem)dropdownItem;

				if (_addSharingDropdownItemGroup(
						(List<DropdownItem>)dropdownGroupItem.get("items"))) {

					return true;
				}
			}
			else if (Objects.equals(
						DLUIItemKeys.DOWNLOAD, dropdownItem.get("key"))) {

				break;
			}

			i++;
		}

		if (i < dropdownItems.size()) {
			if (FeatureFlagManagerUtil.isEnabled("LPS-197477")) {
				if (_isSharingEnabled()) {
					dropdownItems.addAll(
						i,
						DropdownItemListBuilder.addContext(
							_sharingDropdownItemFactory.
								createShareActionUnsafeConsumer(
									DLFileEntryConstants.getClassName(),
									_fileEntry.getFileEntryId(),
									_httpServletRequest)
						).build());
				}
				else {
					dropdownItems.add(
						i,
						_sharingDropdownItemFactory.createCopyLinkDropdownItem(
							DLFileEntryConstants.getClassName(),
							_fileEntry.getFileEntryId(), _httpServletRequest));
				}
			}
			else {
				if (_isSharingEnabled()) {
					dropdownItems.add(
						i,
						_sharingDropdownItemFactory.createShareDropdownItem(
							DLFileEntryConstants.getClassName(),
							_fileEntry.getFileEntryId(), _httpServletRequest));
				}
			}

			return true;
		}

		return false;
	}

	private boolean _isSharingEnabled() throws PortalException {
		if (!_isShowShareAction() || !_sharingConfiguration.isEnabled()) {
			return false;
		}

		return true;
	}

	private boolean _isShowActions() throws PortalException {
		PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

		String portletName = portletDisplay.getPortletName();

		if (portletName.equals(DLPortletKeys.DOCUMENT_LIBRARY_ADMIN)) {
			return true;
		}

		Settings settings = FallbackKeysSettingsUtil.getSettings(
			new PortletInstanceSettingsLocator(
				_themeDisplay.getLayout(), portletDisplay.getId()));

		TypedSettings typedSettings = new TypedSettings(settings);

		return typedSettings.getBooleanValue("showActions");
	}

	private boolean _isShowShareAction() throws PortalException {
		if (_showShareAction != null) {
			return _showShareAction;
		}

		_showShareAction = false;

		if (_themeDisplay.isSignedIn() && _isShowActions() &&
			_sharingPermission.containsSharePermission(
				_themeDisplay.getPermissionChecker(),
				PortalUtil.getClassNameId(DLFileEntryConstants.getClassName()),
				_fileEntry.getFileEntryId(), _themeDisplay.getScopeGroupId())) {

			_showShareAction = true;
		}

		return _showShareAction;
	}

	private static final UUID _UUID = UUID.fromString(
		"6d7d30de-01fa-49db-a422-d78748aa03a7");

	private final FileEntry _fileEntry;
	private final HttpServletRequest _httpServletRequest;
	private final SharingConfiguration _sharingConfiguration;
	private final SharingDropdownItemFactory _sharingDropdownItemFactory;
	private final SharingEntryLocalService _sharingEntryLocalService;
	private final SharingPermission _sharingPermission;
	private Boolean _showShareAction;
	private final ThemeDisplay _themeDisplay;

}