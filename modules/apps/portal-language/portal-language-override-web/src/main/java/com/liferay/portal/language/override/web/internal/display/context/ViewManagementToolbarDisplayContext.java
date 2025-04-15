/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.language.override.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItemList;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.language.override.web.internal.display.LanguageItemDisplay;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;

/**
 * @author Drew Brokke
 */
public class ViewManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public ViewManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		SearchContainer<LanguageItemDisplay> searchContainer,
		String displayStyle, boolean hasManageLanguageOverridesPermission) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			searchContainer);

		_displayStyle = displayStyle;
		_hasManageLanguageOverridesPermission =
			hasManageLanguageOverridesPermission;
	}

	@Override
	public String getClearResultsURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setKeywords(
			StringPool.BLANK
		).setNavigation(
			(String)null
		).buildString();
	}

	@Override
	public CreationMenu getCreationMenu() {
		if (!_hasManageLanguageOverridesPermission) {
			return null;
		}

		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					getPortletURL(), "mvcPath", "/edit_plo_entry.jsp",
					"backURL", String.valueOf(getPortletURL()), "key",
					StringPool.BLANK);
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "add-language-key"));
			}
		).build();
	}

	@Override
	public List<LabelItem> getFilterLabelItems() {
		LabelItemList labelItems = new LabelItemList();

		String navigation = getNavigation();

		if (Objects.equals(navigation, "all")) {
			return labelItems;
		}

		labelItems.add(
			labelItem -> {
				labelItem.putData(
					"removeLabelURL",
					PortletURLBuilder.create(
						getPortletURL()
					).setNavigation(
						(String)null
					).buildString());
				labelItem.setDismissible(true);
				labelItem.setLabel(
					LanguageUtil.get(httpServletRequest, navigation));
			});

		return labelItems;
	}

	@Override
	public String getSearchActionURL() {
		return String.valueOf(searchContainer.getIteratorURL());
	}

	@Override
	public Boolean isSelectable() {
		return false;
	}

	@Override
	protected String getDefaultDisplayStyle() {
		return "descriptive";
	}

	@Override
	protected String getDisplayStyle() {
		return _displayStyle;
	}

	@Override
	protected String[] getDisplayViews() {
		return new String[] {"list", "descriptive"};
	}

	@Override
	protected String getFilterNavigationDropdownItemsLabel() {
		return LanguageUtil.get(httpServletRequest, "filter-by-override");
	}

	@Override
	protected String[] getNavigationKeys() {
		return new String[] {"any-language", "selected-language"};
	}

	private final String _displayStyle;
	private final boolean _hasManageLanguageOverridesPermission;

}