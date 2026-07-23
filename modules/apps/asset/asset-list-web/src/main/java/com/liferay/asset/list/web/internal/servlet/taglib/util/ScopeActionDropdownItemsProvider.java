/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.web.internal.servlet.taglib.util;

import com.liferay.asset.list.web.internal.display.context.EditAssetListDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.List;

/**
 * @author Diego Hu
 */
public class ScopeActionDropdownItemsProvider {

	public ScopeActionDropdownItemsProvider(
		EditAssetListDisplayContext editAssetListDisplayContext,
		LiferayPortletRequest liferayPortletRequest) {

		_editAssetListDisplayContext = editAssetListDisplayContext;

		_themeDisplay = (ThemeDisplay)liferayPortletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getActionDropdownItems() throws Exception {
		DropdownItemList dropdownItemList = new DropdownItemList();

		List<Group> selectedGroups =
			_editAssetListDisplayContext.getSelectedGroups();

		for (Group group : _editAssetListDisplayContext.getAvailableGroups()) {
			if (!selectedGroups.contains(group)) {
				dropdownItemList.add(
					dropdownItem -> {
						dropdownItem.putData("action", "addRow");
						dropdownItem.putData(
							"groupId", String.valueOf(group.getGroupId()));
						dropdownItem.setLabel(
							group.getScopeDescriptiveName(_themeDisplay));
					});
			}
		}

		dropdownItemList.add(
			dropdownItem -> {
				dropdownItem.putData("action", "selectManageableGroup");
				dropdownItem.putData(
					"groupItemSelectorURL",
					_editAssetListDisplayContext.getGroupItemSelectorURL());
				dropdownItem.putData(
					"selectEventName",
					_editAssetListDisplayContext.getSelectGroupEventName());

				String otherSiteLabelLocalized = LanguageUtil.get(
					_themeDisplay.getLocale(),
					"other-site-asset-library-or-space");

				dropdownItem.setLabel(
					otherSiteLabelLocalized + StringPool.TRIPLE_PERIOD);
			});

		return dropdownItemList;
	}

	private final EditAssetListDisplayContext _editAssetListDisplayContext;
	private final ThemeDisplay _themeDisplay;

}