/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.set.prototype.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class LayoutSetPrototypeManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public LayoutSetPrototypeManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LayoutSetPrototypeDisplayContext layoutSetPrototypeDisplayContext,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		SearchContainer<?> searchContainer) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			searchContainer);

		_layoutSetPrototypeDisplayContext = layoutSetPrototypeDisplayContext;
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.putData("action", "deleteLayoutSetPrototypes");
				dropdownItem.setIcon("times-circle");
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "delete"));
				dropdownItem.setQuickAction(true);
			}
		).build();
	}

	@Override
	public String getClearResultsURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setParameter(
			"orderByCol", getOrderByCol()
		).setParameter(
			"orderByType", getOrderByType()
		).buildString();
	}

	@Override
	public CreationMenu getCreationMenu() {
		if (!_layoutSetPrototypeDisplayContext.isShowAddButton()) {
			return null;
		}

		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					PortletURLBuilder.createRenderURL(
						liferayPortletResponse
					).setMVCPath(
						"/edit_layout_set_prototype.jsp"
					).setRedirect(
						PortalUtil.getCurrentURL(httpServletRequest)
					).buildString());
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "add"));
			}
		).build();
	}

	@Override
	public String getDisplayStyle() {
		return _layoutSetPrototypeDisplayContext.getDisplayStyle();
	}

	@Override
	public String getOrderByCol() {
		return _layoutSetPrototypeDisplayContext.getOrderByCol();
	}

	@Override
	public String getOrderByType() {
		return _layoutSetPrototypeDisplayContext.getOrderByType();
	}

	@Override
	public String getSearchContainerId() {
		return "layoutSetPrototype";
	}

	@Override
	public Boolean isShowSearch() {
		return false;
	}

	@Override
	protected String[] getDisplayViews() {
		return new String[] {"list", "descriptive", "icon"};
	}

	@Override
	protected String[] getNavigationKeys() {
		return new String[] {"all", "active", "inactive"};
	}

	@Override
	protected String getNavigationParam() {
		return "status";
	}

	@Override
	protected String[] getOrderByKeys() {
		return new String[] {"createDate"};
	}

	private final LayoutSetPrototypeDisplayContext
		_layoutSetPrototypeDisplayContext;

}