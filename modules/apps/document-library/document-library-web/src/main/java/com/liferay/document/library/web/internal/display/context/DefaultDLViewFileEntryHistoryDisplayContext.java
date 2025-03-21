/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.display.context;

import com.liferay.document.library.display.context.DLViewFileEntryHistoryDisplayContext;
import com.liferay.document.library.kernel.versioning.VersioningStrategy;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.document.library.web.internal.display.context.helper.DLPortletInstanceSettingsHelper;
import com.liferay.document.library.web.internal.display.context.helper.DLRequestHelper;
import com.liferay.document.library.web.internal.display.context.logic.UIItemsBuilder;
import com.liferay.document.library.web.internal.helper.DLTrashHelper;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileVersion;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.UUID;

/**
 * @author Mauro Mariuzzo
 */
public class DefaultDLViewFileEntryHistoryDisplayContext
	implements DLViewFileEntryHistoryDisplayContext {

	public DefaultDLViewFileEntryHistoryDisplayContext(
		DLTrashHelper dlTrashHelper, DLURLHelper dlURLHelper,
		FileVersion fileVersion, HttpServletRequest httpServletRequest,
		VersioningStrategy versioningStrategy) {

		DLRequestHelper dlRequestHelper = new DLRequestHelper(
			httpServletRequest);

		_dlPortletInstanceSettingsHelper = new DLPortletInstanceSettingsHelper(
			dlRequestHelper);

		_uiItemsBuilder = new UIItemsBuilder(
			httpServletRequest, fileVersion, dlTrashHelper, versioningStrategy,
			dlURLHelper);
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() throws PortalException {
		if (!_dlPortletInstanceSettingsHelper.isShowActions()) {
			return null;
		}

		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						_uiItemsBuilder::isDownloadActionAvailable,
						_uiItemsBuilder.createDownloadDropdownItem()
					).add(
						_uiItemsBuilder::isViewVersionActionAvailable,
						_uiItemsBuilder.createViewVersionDropdownItem()
					).add(
						_uiItemsBuilder::isRevertToVersionActionAvailable,
						_uiItemsBuilder.createRevertVersionDropdownItem()
					).add(
						_uiItemsBuilder::isCompareToActionAvailable,
						_uiItemsBuilder.createCompareToDropdownItem()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						_uiItemsBuilder::isDeleteVersionActionAvailable,
						_uiItemsBuilder.createDeleteVersionDropdownItem()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).build();
	}

	@Override
	public UUID getUuid() {
		return _UUID;
	}

	private static final UUID _UUID = UUID.fromString(
		"8f4f3c55-3e93-41c5-a363-57d00161f274");

	private final DLPortletInstanceSettingsHelper
		_dlPortletInstanceSettingsHelper;
	private final UIItemsBuilder _uiItemsBuilder;

}