/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItemListBuilder;
import com.liferay.layout.page.template.admin.web.internal.security.permission.resource.LayoutPageTemplatePermission;
import com.liferay.layout.page.template.constants.LayoutPageTemplateActionKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.permission.PortalPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.service.permission.LayoutPrototypePermissionUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class LayoutPrototypeManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public LayoutPrototypeManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		LayoutPrototypeDisplayContext layoutPrototypeDisplayContext) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			layoutPrototypeDisplayContext.getSearchContainer());

		_layoutPrototypeDisplayContext = layoutPrototypeDisplayContext;
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.putData(
					"action", "deleteSelectedLayoutPrototypes");
				dropdownItem.setIcon("trash");
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "delete"));
				dropdownItem.setQuickAction(true);
			}
		).build();
	}

	public String getAvailableActions(LayoutPrototype layoutPrototype) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (LayoutPrototypePermissionUtil.contains(
				themeDisplay.getPermissionChecker(),
				layoutPrototype.getLayoutPrototypeId(), ActionKeys.DELETE)) {

			return "deleteSelectedLayoutPrototypes";
		}

		return StringPool.BLANK;
	}

	@Override
	public String getClearResultsURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setNavigation(
			StringPool.BLANK
		).buildString();
	}

	@Override
	public String getComponentId() {
		return "layoutPrototypeManagementToolbar";
	}

	@Override
	public CreationMenu getCreationMenu() {
		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					liferayPortletResponse.createRenderURL(), "mvcPath",
					"/edit_layout_prototype.jsp");
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "add"));
			}
		).build();
	}

	@Override
	public String getDefaultEventHandler() {
		return "LAYOUT_PROTOTYPE_MANAGEMENT_TOOLBAR_DEFAULT_EVENT_HANDLER";
	}

	@Override
	public List<LabelItem> getFilterLabelItems() {
		Boolean active = _layoutPrototypeDisplayContext.getActive();

		return LabelItemListBuilder.add(
			() -> (active != null) && active,
			labelItem -> {
				labelItem.putData(
					"removeLabelURL",
					PortletURLBuilder.create(
						PortletURLUtil.clone(
							currentURLObj, liferayPortletResponse)
					).setNavigation(
						(String)null
					).buildString());

				labelItem.setCloseable(true);
				labelItem.setLabel(
					LanguageUtil.get(httpServletRequest, "active"));
			}
		).add(
			() -> (active != null) && !active,
			labelItem -> {
				labelItem.putData(
					"removeLabelURL",
					PortletURLBuilder.create(
						PortletURLUtil.clone(
							currentURLObj, liferayPortletResponse)
					).setNavigation(
						(String)null
					).buildString());

				labelItem.setCloseable(true);
				labelItem.setLabel(
					LanguageUtil.get(httpServletRequest, "inactive"));
			}
		).build();
	}

	@Override
	public String getSearchContainerId() {
		return "layoutPrototype";
	}

	@Override
	public Boolean isShowCreationMenu() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (PortalPermissionUtil.contains(
				themeDisplay.getPermissionChecker(),
				ActionKeys.ADD_LAYOUT_PROTOTYPE) ||
			LayoutPageTemplatePermission.contains(
				themeDisplay.getPermissionChecker(),
				themeDisplay.getScopeGroupId(),
				LayoutPageTemplateActionKeys.ADD_LAYOUT_PAGE_TEMPLATE_ENTRY)) {

			return true;
		}

		return false;
	}

	@Override
	protected String[] getNavigationKeys() {
		return new String[] {"all", "active", "inactive"};
	}

	@Override
	protected String[] getOrderByKeys() {
		return new String[] {"create-date"};
	}

	private final LayoutPrototypeDisplayContext _layoutPrototypeDisplayContext;

}