/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.web.internal.servlet.taglib.util;

import com.liferay.asset.list.constants.AssetListPortletKeys;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.service.AssetListEntryUsageLocalServiceUtil;
import com.liferay.asset.list.web.internal.security.permission.resource.AssetListEntryPermission;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.staging.StagingGroupHelperUtil;
import com.liferay.taglib.security.PermissionsURLTag;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class AssetListEntryActionDropdownItemsProvider {

	public AssetListEntryActionDropdownItemsProvider(
		AssetListEntry assetListEntry,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		_assetListEntry = assetListEntry;
		_liferayPortletResponse = liferayPortletResponse;

		_httpServletRequest = PortalUtil.getHttpServletRequest(
			liferayPortletRequest);
		_themeDisplay = (ThemeDisplay)liferayPortletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getActionDropdownItems() {
		Group group = _themeDisplay.getScopeGroup();
		boolean liveGroup = _isLiveGroup();

		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() ->
							!liveGroup &&
							AssetListEntryPermission.contains(
								_themeDisplay.getPermissionChecker(),
								_assetListEntry, ActionKeys.UPDATE),
						_getEditAssetListEntryActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() ->
							!liveGroup &&
							AssetListEntryPermission.contains(
								_themeDisplay.getPermissionChecker(),
								_assetListEntry, ActionKeys.UPDATE),
						_getRenameAssetListEntryActionUnsafeConsumer()
					).add(
						() -> group.getType() != GroupConstants.TYPE_DEPOT,
						_getViewAssetListEntryUsagesActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() ->
							!liveGroup &&
							AssetListEntryPermission.contains(
								_themeDisplay.getPermissionChecker(),
								_assetListEntry, ActionKeys.PERMISSIONS),
						_getPermissionsAssetListEntryActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() ->
							!liveGroup &&
							AssetListEntryPermission.contains(
								_themeDisplay.getPermissionChecker(),
								_assetListEntry, ActionKeys.DELETE),
						_getDeleteAssetListEntryActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).build();
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getDeleteAssetListEntryActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "deleteAssetListEntry");
			dropdownItem.putData(
				"deleteAssetListEntryURL",
				PortletURLBuilder.createActionURL(
					_liferayPortletResponse
				).setActionName(
					"/asset_list/delete_asset_list_entries"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"assetListEntryId", _assetListEntry.getAssetListEntryId()
				).buildString());
			dropdownItem.setIcon("trash");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "delete"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getEditAssetListEntryActionUnsafeConsumer() {

		PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

		return dropdownItem -> {
			dropdownItem.setHref(
				_liferayPortletResponse.createRenderURL(), "backURLTitle",
				portletDisplay.getPortletDisplayName(), "mvcPath",
				"/edit_asset_list_entry.jsp", "redirect",
				_themeDisplay.getURLCurrent(), "assetListEntryId",
				_assetListEntry.getAssetListEntryId());
			dropdownItem.setIcon("pencil");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "edit"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
			_getPermissionsAssetListEntryActionUnsafeConsumer()
		throws Exception {

		String permissionsAssetEntryListURL = PermissionsURLTag.doTag(
			StringPool.BLANK, AssetListEntry.class.getName(),
			_assetListEntry.getTitle(), null,
			String.valueOf(_assetListEntry.getAssetListEntryId()),
			LiferayWindowState.POP_UP.toString(), null, _httpServletRequest);

		return dropdownItem -> {
			dropdownItem.putData("action", "permissionsAssetEntryList");
			dropdownItem.putData(
				"permissionsAssetEntryListURL", permissionsAssetEntryListURL);
			dropdownItem.setIcon("password-policies");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "permissions"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getRenameAssetListEntryActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "renameAssetListEntry");
			dropdownItem.putData(
				"assetListEntryId",
				String.valueOf(_assetListEntry.getAssetListEntryId()));
			dropdownItem.putData(
				"assetListEntryTitle", _assetListEntry.getTitle());
			dropdownItem.putData(
				"renameAssetListEntryURL",
				PortletURLBuilder.createActionURL(
					_liferayPortletResponse
				).setActionName(
					"/asset_list/update_asset_list_entry"
				).setParameter(
					"assetListEntryId", _assetListEntry.getAssetListEntryId()
				).buildString());
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "rename"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getViewAssetListEntryUsagesActionUnsafeConsumer() {

		long count =
			AssetListEntryUsageLocalServiceUtil.getAssetListEntryUsagesCount(
				_themeDisplay.getScopeGroupId(),
				PortalUtil.getClassNameId(AssetListEntry.class),
				String.valueOf(_assetListEntry.getAssetListEntryId()));

		return dropdownItem -> {
			dropdownItem.setDisabled(count == 0);
			dropdownItem.setHref(
				_liferayPortletResponse.createRenderURL(), "mvcPath",
				"/view_asset_list_entry_usages.jsp", "redirect",
				_themeDisplay.getURLCurrent(), "assetListEntryId",
				_assetListEntry.getAssetListEntryId());
			dropdownItem.setIcon("list-ul");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "view-usages"));
		};
	}

	private boolean _isLiveGroup() {
		Group group = _themeDisplay.getScopeGroup();

		if (group.isLayout()) {
			group = group.getParentGroup();
		}

		StagingGroupHelper stagingGroupHelper =
			StagingGroupHelperUtil.getStagingGroupHelper();

		if (stagingGroupHelper.isLiveGroup(group) &&
			stagingGroupHelper.isStagedPortlet(
				group, AssetListPortletKeys.ASSET_LIST)) {

			return true;
		}

		return false;
	}

	private final AssetListEntry _assetListEntry;
	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final ThemeDisplay _themeDisplay;

}