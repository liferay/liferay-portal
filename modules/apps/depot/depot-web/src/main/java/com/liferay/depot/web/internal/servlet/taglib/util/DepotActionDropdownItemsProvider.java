/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.servlet.taglib.util;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.web.internal.roles.admin.group.type.contributor.DepotEntryPermission;
import com.liferay.depot.web.internal.util.DepotEntryURLUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Alicia García
 */
public class DepotActionDropdownItemsProvider {

	public DepotActionDropdownItemsProvider(
		DepotEntry depotEntry, LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		_depotEntry = depotEntry;
		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;

		_httpServletRequest = PortalUtil.getHttpServletRequest(
			liferayPortletRequest);

		_themeDisplay = (ThemeDisplay)_httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getActionDropdownItems() {
		List<DropdownItem> dropdownItems = DropdownItemListBuilder.add(
			() -> _hasUpdatePermission(),
			dropdownItem -> {
				dropdownItem.setHref(
					DepotEntryURLUtil.getEditDepotEntryPortletURL(
						_depotEntry, _themeDisplay.getURLCurrent(),
						_liferayPortletRequest));
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "edit"));
			}
		).add(
			() -> _hasDeletePermission(),
			dropdownItem -> {
				ActionURL deleteDepotEntryActionURL =
					DepotEntryURLUtil.getDeleteDepotEntryActionURL(
						_depotEntry.getDepotEntryId(),
						_themeDisplay.getURLCurrent(), _liferayPortletResponse);

				dropdownItem.putData("action", "deleteDepotEntry");

				dropdownItem.putData(
					"deleteDepotEntryURL",
					deleteDepotEntryActionURL.toString());

				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "delete"));
			}
		).add(
			() -> _hasPermissionsPermission(),
			dropdownItem -> {
				dropdownItem.putData("action", "permissionsDepotEntry");
				dropdownItem.putData(
					"permissionsDepotEntryURL",
					DepotEntryURLUtil.getDepotEntryPermissionsURL(
						_depotEntry, _liferayPortletRequest));
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "permissions"));
			}
		).build();

		if (ListUtil.isEmpty(dropdownItems)) {
			return null;
		}

		return dropdownItems;
	}

	private boolean _hasDeletePermission() {
		try {
			return DepotEntryPermission.contains(
				_themeDisplay.getPermissionChecker(),
				_depotEntry.getDepotEntryId(), ActionKeys.DELETE);
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}
	}

	private boolean _hasPermissionsPermission() {
		try {
			return DepotEntryPermission.contains(
				_themeDisplay.getPermissionChecker(),
				_depotEntry.getDepotEntryId(), ActionKeys.PERMISSIONS);
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}
	}

	private boolean _hasUpdatePermission() {
		try {
			return DepotEntryPermission.contains(
				_themeDisplay.getPermissionChecker(),
				_depotEntry.getDepotEntryId(), ActionKeys.UPDATE);
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}
	}

	private final DepotEntry _depotEntry;
	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final ThemeDisplay _themeDisplay;

}