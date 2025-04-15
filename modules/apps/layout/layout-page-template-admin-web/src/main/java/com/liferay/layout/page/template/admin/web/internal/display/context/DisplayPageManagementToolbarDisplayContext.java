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
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.layout.page.template.admin.web.internal.constants.LayoutPageTemplateAdminWebKeys;
import com.liferay.layout.page.template.admin.web.internal.security.permission.resource.LayoutPageTemplateCollectionPermission;
import com.liferay.layout.page.template.admin.web.internal.security.permission.resource.LayoutPageTemplateEntryPermission;
import com.liferay.layout.page.template.admin.web.internal.security.permission.resource.LayoutPageTemplatePermission;
import com.liferay.layout.page.template.constants.LayoutPageTemplateActionKeys;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.item.selector.LayoutPageTemplateCollectionTreeNodeItemSelectorCriterion;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class DisplayPageManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public DisplayPageManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		DisplayPageDisplayContext displayPageDisplayContext) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			displayPageDisplayContext.getDisplayPagesSearchContainer());

		_displayPageDisplayContext = displayPageDisplayContext;

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
								"action", "copySelectedEntries");
							dropdownItem.putData(
								"copySelectedEntriesURL",
								PortletURLBuilder.createActionURL(
									liferayPortletResponse
								).setActionName(
									StringBundler.concat(
										"/layout_page_template_admin",
										"/copy_layout_page_template_entries",
										"_and_layout_page_template",
										"_collections")
								).setRedirect(
									_themeDisplay.getURLCurrent()
								).buildString());
							dropdownItem.putData(
								"itemSelectorURL", _getItemSelectorURL());
							dropdownItem.setIcon("copy");
							dropdownItem.setLabel(
								LanguageUtil.get(
									httpServletRequest, "copy-to"));
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
								"action", "exportSelectedEntries");
							dropdownItem.putData(
								"exportSelectedEntriesURL",
								_getExportDisplayPageURL());
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
								"action", "moveSelectedEntries");
							dropdownItem.putData(
								"itemSelectorURL", _getItemSelectorURL());
							dropdownItem.putData(
								"moveSelectedEntriesURL",
								PortletURLBuilder.createActionURL(
									liferayPortletResponse
								).setActionName(
									StringBundler.concat(
										"/layout_page_template_admin",
										"/move_layout_page_template_entries",
										"_and_layout_page_template",
										"_collections")
								).setRedirect(
									_themeDisplay.getURLCurrent()
								).buildString());
							dropdownItem.setIcon("move-folder");
							dropdownItem.setLabel(
								LanguageUtil.get(httpServletRequest, "move"));
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
								"action", "deleteSelectedEntries");
							dropdownItem.putData(
								"deleteSelectedEntriesURL",
								_getDeleteSelectedEntriesURL());
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

	public String getAvailableLayoutPageTemplateCollectionActions(
			LayoutPageTemplateCollection layoutPageTemplateCollection)
		throws PortalException {

		List<String> availableActions = new ArrayList<>();

		if (LayoutPageTemplateCollectionPermission.contains(
				_themeDisplay.getPermissionChecker(),
				layoutPageTemplateCollection, ActionKeys.DELETE)) {

			availableActions.add("deleteSelectedEntries");
		}

		if (LayoutPageTemplateCollectionPermission.contains(
				_themeDisplay.getPermissionChecker(),
				layoutPageTemplateCollection, ActionKeys.VIEW)) {

			availableActions.add("exportSelectedEntries");
		}

		if (LayoutPageTemplateCollectionPermission.contains(
				_themeDisplay.getPermissionChecker(),
				layoutPageTemplateCollection, ActionKeys.UPDATE)) {

			availableActions.add("copySelectedEntries");
			availableActions.add("moveSelectedEntries");
		}

		return StringUtil.merge(availableActions, StringPool.COMMA);
	}

	public String getAvailableLayoutPageTemplateEntryActions(
			LayoutPageTemplateEntry layoutPageTemplateEntry)
		throws PortalException {

		List<String> availableActions = new ArrayList<>();

		if (LayoutPageTemplateEntryPermission.contains(
				_themeDisplay.getPermissionChecker(), layoutPageTemplateEntry,
				ActionKeys.DELETE)) {

			availableActions.add("deleteSelectedEntries");
		}

		if ((layoutPageTemplateEntry.getLayoutPrototypeId() == 0) &&
			!layoutPageTemplateEntry.isDraft()) {

			availableActions.add("exportSelectedEntries");
		}

		if (LayoutPageTemplateEntryPermission.contains(
				_themeDisplay.getPermissionChecker(), layoutPageTemplateEntry,
				ActionKeys.UPDATE)) {

			availableActions.add("copySelectedEntries");
			availableActions.add("moveSelectedEntries");
		}

		return StringUtil.merge(availableActions, StringPool.COMMA);
	}

	@Override
	public String getClearResultsURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setKeywords(
			StringPool.BLANK
		).setParameter(
			"layoutPageTemplateCollectionId",
			LayoutPageTemplateConstants.
				PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT
		).buildString();
	}

	@Override
	public String getComponentId() {
		return "displayPagesManagementToolbar";
	}

	@Override
	public CreationMenu getCreationMenu() {
		return CreationMenuBuilder.addDropdownItem(
			dropdownItem -> {
				dropdownItem.putData("action", "addDisplayPageCollection");
				dropdownItem.putData(
					"addDisplayPageCollectionURL",
					PortletURLBuilder.createActionURL(
						liferayPortletResponse
					).setActionName(
						"/layout_page_template_admin" +
							"/add_display_page_collection"
					).setRedirect(
						_themeDisplay.getURLCurrent()
					).setParameter(
						"layoutPageTemplateCollectionId",
						ParamUtil.getLong(
							httpServletRequest,
							"layoutPageTemplateCollectionId")
					).buildString());
				dropdownItem.setIcon("folder");
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "folder"));
			}
		).addDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					PortletURLBuilder.createRenderURL(
						liferayPortletResponse
					).setMVCPath(
						"/select_display_page_master_layout.jsp"
					).setRedirect(
						_themeDisplay.getURLCurrent()
					).setParameter(
						"layoutPageTemplateCollectionId",
						ParamUtil.getLong(
							httpServletRequest,
							"layoutPageTemplateCollectionId")
					).buildString());
				dropdownItem.setLabel(
					LanguageUtil.get(
						httpServletRequest, "display-page-template"));
			}
		).build();
	}

	@Override
	public String getDefaultEventHandler() {
		return "DISPLAY_PAGE_MANAGEMENT_TOOLBAR_DEFAULT_EVENT_HANDLER";
	}

	@Override
	public String getInfoPanelId() {
		return "infoPanelId";
	}

	@Override
	public String getSearchActionURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setParameter(
			"layoutPageTemplateCollectionId", -1
		).buildString();
	}

	@Override
	public String getSearchContainerId() {
		return "displayPages" +
			_displayPageDisplayContext.getLayoutPageTemplateCollectionId();
	}

	@Override
	public Boolean isShowCreationMenu() {
		return LayoutPageTemplatePermission.contains(
			_themeDisplay.getPermissionChecker(),
			_themeDisplay.getSiteGroupId(),
			LayoutPageTemplateActionKeys.ADD_LAYOUT_PAGE_TEMPLATE_ENTRY);
	}

	@Override
	protected String[] getOrderByKeys() {
		return new String[] {"create-date", "modified-date", "name"};
	}

	private String _getDeleteSelectedEntriesURL() {
		return PortletURLBuilder.createActionURL(
			liferayPortletResponse
		).setActionName(
			"/layout_page_template_admin/delete_layout_page_template_" +
				"entries_and_layout_page_template_collections"
		).setTabs1(
			"display-page-templates"
		).setParameter(
			"layoutPageTemplateCollectionId",
			ParamUtil.getLong(
				httpServletRequest, "layoutPageTemplateCollectionId")
		).buildString();
	}

	private String _getExportDisplayPageURL() {
		return ResourceURLBuilder.createResourceURL(
			liferayPortletResponse
		).setResourceID(
			"/layout_page_template_admin/export_layout_page_template_entries_" +
				"and_layout_page_template_collections"
		).buildString();
	}

	private String _getItemSelectorURL() {
		if (_itemSelectorURL != null) {
			return _itemSelectorURL;
		}

		ItemSelector itemSelector =
			(ItemSelector)httpServletRequest.getAttribute(
				LayoutPageTemplateAdminWebKeys.ITEM_SELECTOR);

		LayoutPageTemplateCollectionTreeNodeItemSelectorCriterion
			layoutPageTemplateCollectionTreeNodeItemSelectorCriterion =
				new LayoutPageTemplateCollectionTreeNodeItemSelectorCriterion();

		layoutPageTemplateCollectionTreeNodeItemSelectorCriterion.
			setDesiredItemSelectorReturnTypes(new UUIDItemSelectorReturnType());

		_itemSelectorURL = String.valueOf(
			itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(
					liferayPortletRequest),
				liferayPortletResponse.getNamespace() + "selectFolder",
				layoutPageTemplateCollectionTreeNodeItemSelectorCriterion));

		return _itemSelectorURL;
	}

	private final DisplayPageDisplayContext _displayPageDisplayContext;
	private String _itemSelectorURL;
	private final ThemeDisplay _themeDisplay;

}