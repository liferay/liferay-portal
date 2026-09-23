/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.display.context;

import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.model.FDSActionDropdownItemBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.pim.site.initializer.internal.util.PIMURLUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Andrea Sbarra
 * @author Stefano Motta
 */
public class ViewPIMConnectorsDisplayContext {

	public ViewPIMConnectorsDisplayContext(
		HttpServletRequest httpServletRequest,
		ObjectDefinition objectDefinition) {

		_httpServletRequest = httpServletRequest;
		_objectDefinition = objectDefinition;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public String getAPIURL() {
		if (_objectDefinition == null) {
			return StringPool.BLANK;
		}

		return "/o" + _objectDefinition.getRESTContextPath();
	}

	public CreationMenu getCreationMenu() {
		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					PIMURLUtil.getEditConnectorURL(
						StringPool.BLANK, _themeDisplay));
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "new"));
			}
		).build();
	}

	public Map<String, Object> getEmptyState() {
		return HashMapBuilder.<String, Object>put(
			"description",
			LanguageUtil.get(
				_httpServletRequest,
				"create-a-connector-to-link-the-pim-to-a-shopping-experience")
		).put(
			"title", LanguageUtil.get(_httpServletRequest, "no-connectors-yet")
		).build();
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems() {
		return ListUtil.fromArray(
			FDSActionDropdownItemBuilder.setHref(
				PIMURLUtil.getEditConnectorURL("{id}", _themeDisplay)
			).setIcon(
				"pencil"
			).setLabel(
				LanguageUtil.get(_httpServletRequest, "edit")
			).setMethod(
				"get"
			).setPermissionKey(
				"update"
			).build(
				"edit"
			),
			FDSActionDropdownItemBuilder.setHref(
				PIMURLUtil.getFieldMappingsURL("{id}", _themeDisplay)
			).setIcon(
				"sheets"
			).setLabel(
				LanguageUtil.get(_httpServletRequest, "map-fields")
			).setMethod(
				"get"
			).setPermissionKey(
				"update"
			).build(
				"fieldMappings"
			),
			FDSActionDropdownItemBuilder.setHref(
				PIMURLUtil.getExportToLiferayCommerceURL()
			).setIcon(
				"download"
			).setLabel(
				LanguageUtil.get(_httpServletRequest, "export")
			).setMethod(
				"get"
			).setTarget(
				"blank"
			).build(
				"export"
			),
			FDSActionDropdownItemBuilder.setConfirmationMessage(
				LanguageUtil.get(
					_httpServletRequest, "are-you-sure-you-want-to-delete-this")
			).setHref(
				"{actions.delete.href}"
			).setIcon(
				"trash"
			).setLabel(
				LanguageUtil.get(_httpServletRequest, "delete")
			).setMethod(
				"delete"
			).setPermissionKey(
				"delete"
			).setTarget(
				"headless"
			).build(
				"delete"
			));
	}

	private final HttpServletRequest _httpServletRequest;
	private final ObjectDefinition _objectDefinition;
	private final ThemeDisplay _themeDisplay;

}