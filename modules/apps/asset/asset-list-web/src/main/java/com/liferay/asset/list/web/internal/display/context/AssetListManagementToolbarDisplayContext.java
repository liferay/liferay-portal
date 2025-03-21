/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.web.internal.display.context;

import com.liferay.asset.list.constants.AssetListActionKeys;
import com.liferay.asset.list.constants.AssetListEntryTypeConstants;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.web.internal.security.permission.resource.AssetListEntryPermission;
import com.liferay.asset.list.web.internal.security.permission.resource.AssetListPermission;
import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class AssetListManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public AssetListManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		AssetListDisplayContext assetListDisplayContext) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			assetListDisplayContext.getAssetListEntriesSearchContainer());

		_assetListDisplayContext = assetListDisplayContext;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		if (_assetListDisplayContext.isLiveGroup()) {
			return null;
		}

		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.putData(
					"action", "deleteSelectedAssetListEntries");
				dropdownItem.setIcon("trash");
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "delete"));
				dropdownItem.setQuickAction(true);
			}
		).build();
	}

	public String getAvailableActions(AssetListEntry assetListEntry)
		throws PortalException {

		if (!_assetListDisplayContext.isLiveGroup() &&
			AssetListEntryPermission.contains(
				_themeDisplay.getPermissionChecker(), assetListEntry,
				ActionKeys.DELETE)) {

			return "deleteSelectedAssetListEntries";
		}

		return StringPool.BLANK;
	}

	@Override
	public String getClearResultsURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setKeywords(
			StringPool.BLANK
		).buildString();
	}

	@Override
	public String getComponentId() {
		return "assetListEntriesEntriesManagementToolbar";
	}

	@Override
	public CreationMenu getCreationMenu() {
		PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.putData("action", "addAssetListEntry");
				dropdownItem.putData(
					"addAssetListEntryURL",
					PortletURLBuilder.createActionURL(
						liferayPortletResponse
					).setActionName(
						"/asset_list/add_asset_list_entry"
					).setParameter(
						"backURLTitle", portletDisplay.getPortletDisplayName()
					).setParameter(
						"type", AssetListEntryTypeConstants.TYPE_MANUAL
					).buildString());
				dropdownItem.putData(
					"title",
					LanguageUtil.format(
						httpServletRequest, "add-x-collection",
						AssetListEntryTypeConstants.TYPE_MANUAL_LABEL, true));
				dropdownItem.setHref("#");
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "manual-collection"));
			}
		).addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.putData("action", "addAssetListEntry");
				dropdownItem.putData(
					"addAssetListEntryURL",
					PortletURLBuilder.createActionURL(
						liferayPortletResponse
					).setActionName(
						"/asset_list/add_asset_list_entry"
					).setParameter(
						"backURLTitle", portletDisplay.getPortletDisplayName()
					).setParameter(
						"type", AssetListEntryTypeConstants.TYPE_DYNAMIC
					).buildString());
				dropdownItem.putData(
					"title",
					LanguageUtil.format(
						httpServletRequest, "add-x-collection",
						AssetListEntryTypeConstants.TYPE_DYNAMIC_LABEL, true));
				dropdownItem.setHref("#");
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "dynamic-collection"));
			}
		).build();
	}

	@Override
	public String getSearchContainerId() {
		return "assetListEntries";
	}

	@Override
	public Boolean isSelectable() {
		if (_assetListDisplayContext.isLiveGroup()) {
			return false;
		}

		return super.isSelectable();
	}

	@Override
	public Boolean isShowCreationMenu() {
		if (_assetListDisplayContext.isLiveGroup()) {
			return false;
		}

		return AssetListPermission.contains(
			_themeDisplay.getPermissionChecker(),
			_themeDisplay.getScopeGroupId(),
			AssetListActionKeys.ADD_ASSET_LIST_ENTRY);
	}

	@Override
	protected String[] getDisplayViews() {
		return new String[] {"list", "descriptive", "icon"};
	}

	@Override
	protected String[] getOrderByKeys() {
		return new String[] {"title", "create-date"};
	}

	private final AssetListDisplayContext _assetListDisplayContext;
	private final ThemeDisplay _themeDisplay;

}