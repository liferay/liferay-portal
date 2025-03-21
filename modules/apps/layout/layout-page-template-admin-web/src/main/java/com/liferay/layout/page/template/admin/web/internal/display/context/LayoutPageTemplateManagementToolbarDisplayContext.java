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
import com.liferay.layout.page.template.constants.LayoutPageTemplateActionKeys;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class LayoutPageTemplateManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public LayoutPageTemplateManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		LayoutPageTemplateDisplayContext layoutPageTemplateDisplayContext) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			layoutPageTemplateDisplayContext.
				getLayoutPageTemplateEntriesSearchContainer());

		_layoutPageTemplateDisplayContext = layoutPageTemplateDisplayContext;

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
								"action", "exportLayoutPageTemplateEntries");
							dropdownItem.putData(
								"exportLayoutPageTemplateEntryURL",
								_getExportLayoutPageTemplateEntryURL());
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
								"action", "deleteLayoutPageTemplateEntries");
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

			availableActions.add("deleteLayoutPageTemplateEntries");
		}

		if ((layoutPageTemplateEntry.getLayoutPrototypeId() == 0) &&
			!layoutPageTemplateEntry.isDraft()) {

			availableActions.add("exportLayoutPageTemplateEntries");
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
		return "layoutPageTemplateEntriesManagementToolbar";
	}

	@Override
	public CreationMenu getCreationMenu() {
		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(_getSelectMasterLayoutURL());
				dropdownItem.setLabel(
					LanguageUtil.get(
						httpServletRequest, "content-page-template"));
			}
		).addPrimaryDropdownItem(
			() -> {
				Group scopeGroup = _themeDisplay.getScopeGroup();

				return !scopeGroup.isLayoutSetPrototype();
			},
			dropdownItem -> {
				dropdownItem.putData("action", "addLayoutPageTemplateEntry");
				dropdownItem.putData(
					"addPageTemplateURL", _getAddLayoutPrototypeURL());
				dropdownItem.setLabel(
					LanguageUtil.get(
						httpServletRequest, "widget-page-template"));
			}
		).build();
	}

	@Override
	public String getDefaultEventHandler() {
		return "LAYOUT_PAGE_TEMPLATE_ENTRY_MANAGEMENT_TOOLBAR_DEFAULT_EVENT_" +
			"HANDLER";
	}

	@Override
	public String getSearchContainerId() {
		return "layoutPageTemplateEntries";
	}

	@Override
	public Boolean isShowCreationMenu() {
		return _layoutPageTemplateDisplayContext.isShowAddButton(
			LayoutPageTemplateActionKeys.ADD_LAYOUT_PAGE_TEMPLATE_ENTRY);
	}

	@Override
	protected String[] getOrderByKeys() {
		return new String[] {"create-date", "name"};
	}

	private String _getAddLayoutPrototypeURL() {
		return PortletURLBuilder.createActionURL(
			liferayPortletResponse
		).setActionName(
			"/layout_page_template_admin/add_layout_prototype"
		).setBackURL(
			_themeDisplay.getURLCurrent()
		).setParameter(
			"layoutPageTemplateCollectionId",
			_layoutPageTemplateDisplayContext.
				getLayoutPageTemplateCollectionId()
		).buildString();
	}

	private String _getExportLayoutPageTemplateEntryURL() {
		String.valueOf(
			_layoutPageTemplateDisplayContext.
				getLayoutPageTemplateCollectionId());

		return ResourceURLBuilder.createResourceURL(
			liferayPortletResponse
		).setParameter(
			"layoutPageTemplateCollectionId",
			_layoutPageTemplateDisplayContext.
				getLayoutPageTemplateCollectionId()
		).setResourceID(
			"/layout_page_template_admin/export_layout_page_template_entries"
		).buildString();
	}

	private String _getSelectMasterLayoutURL() {
		return PortletURLBuilder.createRenderURL(
			liferayPortletResponse
		).setMVCPath(
			"/select_layout_page_template_entry_master_layout.jsp"
		).setRedirect(
			_themeDisplay.getURLCurrent()
		).setParameter(
			"layoutPageTemplateCollectionId",
			_layoutPageTemplateDisplayContext.
				getLayoutPageTemplateCollectionId()
		).buildString();
	}

	private final LayoutPageTemplateDisplayContext
		_layoutPageTemplateDisplayContext;
	private final ThemeDisplay _themeDisplay;

}