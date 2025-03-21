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
import com.liferay.layout.page.template.admin.web.internal.security.permission.resource.LayoutPageTemplateEntryPermission;
import com.liferay.layout.page.template.admin.web.internal.security.permission.resource.LayoutPageTemplatePermission;
import com.liferay.layout.page.template.constants.LayoutPageTemplateActionKeys;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ResourceURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class MasterLayoutManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public MasterLayoutManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		MasterLayoutDisplayContext masterLayoutDisplayContext) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			masterLayoutDisplayContext.getMasterLayoutsSearchContainer());

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						dropdownItem -> {
							dropdownItem.putData(
								"action", "exportMasterLayouts");
							dropdownItem.putData(
								"exportMasterLayoutURL",
								_getExportMasterLayoutURL());
							dropdownItem.setIcon("upload");
							dropdownItem.setLabel(
								LanguageUtil.get(httpServletRequest, "export"));
							dropdownItem.setQuickAction(true);
						}
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						dropdownItem -> {
							dropdownItem.putData(
								"action", "deleteSelectedMasterLayouts");
							dropdownItem.setIcon("trash");
							dropdownItem.setLabel(
								LanguageUtil.get(httpServletRequest, "delete"));
							dropdownItem.setQuickAction(true);
						}
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).build();
	}

	public String getAvailableActions(
			LayoutPageTemplateEntry layoutPageTemplateEntry)
		throws PortalException {

		List<String> availableActions = new ArrayList<>();

		if (LayoutPageTemplateEntryPermission.contains(
				_themeDisplay.getPermissionChecker(), layoutPageTemplateEntry,
				ActionKeys.DELETE)) {

			availableActions.add("deleteSelectedMasterLayouts");
		}

		if ((layoutPageTemplateEntry.getLayoutPrototypeId() == 0) &&
			!layoutPageTemplateEntry.isDraft()) {

			availableActions.add("exportMasterLayouts");
		}

		return StringUtil.merge(availableActions, StringPool.COMMA);
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
		return "masterLayoutsManagementToolbar";
	}

	@Override
	public CreationMenu getCreationMenu() {
		return CreationMenuBuilder.addDropdownItem(
			dropdownItem -> {
				dropdownItem.setData(
					HashMapBuilder.<String, Object>put(
						"action", "addMasterLayout"
					).put(
						"addMasterLayoutURL",
						PortletURLBuilder.createActionURL(
							liferayPortletResponse
						).setActionName(
							"/layout_page_template_admin/add_master_layout"
						).setBackURL(
							_themeDisplay.getURLCurrent()
						).setParameter(
							"type",
							LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT
						).buildString()
					).build());
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "add"));
			}
		).build();
	}

	@Override
	public String getDefaultEventHandler() {
		return "MASTER_LAYOUT_MANAGEMENT_TOOLBAR_DEFAULT_EVENT_HANDLER";
	}

	@Override
	public String getSearchContainerId() {
		return "masterLayouts";
	}

	@Override
	public Boolean isDisabled() {
		if (getItemsTotal() > 1) {
			return false;
		}

		return true;
	}

	@Override
	public Boolean isShowCreationMenu() {
		return LayoutPageTemplatePermission.contains(
			_themeDisplay.getPermissionChecker(),
			_themeDisplay.getScopeGroupId(),
			LayoutPageTemplateActionKeys.ADD_LAYOUT_PAGE_TEMPLATE_ENTRY);
	}

	@Override
	protected String[] getOrderByKeys() {
		return new String[] {"create-date", "name"};
	}

	private String _getExportMasterLayoutURL() {
		ResourceURL exportMasterLayoutURL =
			liferayPortletResponse.createResourceURL();

		exportMasterLayoutURL.setResourceID(
			"/layout_page_template_admin/export_master_layouts");

		return exportMasterLayoutURL.toString();
	}

	private final ThemeDisplay _themeDisplay;

}