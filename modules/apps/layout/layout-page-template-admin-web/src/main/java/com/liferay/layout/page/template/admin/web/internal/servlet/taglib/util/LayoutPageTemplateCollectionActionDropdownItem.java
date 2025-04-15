/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.servlet.taglib.util;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.layout.page.template.admin.web.internal.constants.LayoutPageTemplateAdminWebKeys;
import com.liferay.layout.page.template.admin.web.internal.security.permission.resource.LayoutPageTemplateCollectionPermission;
import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.layout.page.template.item.selector.LayoutPageTemplateCollectionTreeNodeItemSelectorCriterion;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.security.PermissionsURLTag;

import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Víctor Galán
 */
public class LayoutPageTemplateCollectionActionDropdownItem {

	public LayoutPageTemplateCollectionActionDropdownItem(
		HttpServletRequest httpServletRequest,
		LayoutPageTemplateCollection layoutPageTemplateCollection,
		RenderResponse renderResponse, String tabs1) {

		_httpServletRequest = httpServletRequest;
		_layoutPageTemplateCollection = layoutPageTemplateCollection;
		_renderResponse = renderResponse;
		_tabs1 = tabs1;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() ->
							(_layoutPageTemplateCollection.getType() ==
								LayoutPageTemplateCollectionTypeConstants.
									BASIC) &&
							LayoutPageTemplateCollectionPermission.contains(
								_themeDisplay.getPermissionChecker(),
								_layoutPageTemplateCollection,
								ActionKeys.UPDATE),
						dropdownItem -> {
							dropdownItem.setHref(
								_getEditLayoutPageTemplateCollectionURL());
							dropdownItem.setIcon("pencil");
							dropdownItem.setLabel(
								LanguageUtil.get(_httpServletRequest, "edit"));
						}
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() ->
							(_layoutPageTemplateCollection.getType() ==
								LayoutPageTemplateCollectionTypeConstants.
									DISPLAY_PAGE) &&
							LayoutPageTemplateCollectionPermission.contains(
								_themeDisplay.getPermissionChecker(),
								_layoutPageTemplateCollection,
								ActionKeys.UPDATE),
						dropdownItem -> {
							dropdownItem.putData(
								"action", "updateLayoutPageTemplateCollection");
							dropdownItem.putData(
								"dialogTitle", _getRenameDialogTitle());
							dropdownItem.putData(
								"layoutPageTemplateCollectionDescription",
								_layoutPageTemplateCollection.getDescription());
							dropdownItem.putData(
								"layoutPageTemplateCollectionName",
								_layoutPageTemplateCollection.getName());
							dropdownItem.putData(
								"updateLayoutPageTemplateCollectionURL",
								_getUpdateLayoutPageTemplateCollectionURL());
							dropdownItem.setIcon("pencil");
							dropdownItem.setLabel(
								LanguageUtil.get(_httpServletRequest, "edit"));
						}
					).add(
						() ->
							(_layoutPageTemplateCollection.getType() ==
								LayoutPageTemplateCollectionTypeConstants.
									DISPLAY_PAGE) &&
							LayoutPageTemplateCollectionPermission.contains(
								_themeDisplay.getPermissionChecker(),
								_layoutPageTemplateCollection,
								ActionKeys.UPDATE),
						dropdownItem -> {
							dropdownItem.putData(
								"action", "copyLayoutPageTemplateCollection");
							dropdownItem.putData(
								"copySelectedEntriesURL",
								PortletURLBuilder.createActionURL(
									_renderResponse
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
							dropdownItem.putData(
								"layoutPageTemplateCollectionId",
								String.valueOf(
									_layoutPageTemplateCollection.
										getLayoutPageTemplateCollectionId()));
							dropdownItem.putData(
								"layoutPageTemplateCollectionName",
								_layoutPageTemplateCollection.getName());
							dropdownItem.setIcon("copy");
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, "copy-to"));
						}
					).add(
						() ->
							(_layoutPageTemplateCollection.getType() ==
								LayoutPageTemplateCollectionTypeConstants.
									DISPLAY_PAGE) &&
							LayoutPageTemplateCollectionPermission.contains(
								_themeDisplay.getPermissionChecker(),
								_layoutPageTemplateCollection, ActionKeys.VIEW),
						dropdownItem -> {
							dropdownItem.setHref(
								_getExportLayoutPageTemplateCollectionURL());
							dropdownItem.setIcon("export");
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, "export"));
						}
					).add(
						() ->
							(_layoutPageTemplateCollection.getType() ==
								LayoutPageTemplateCollectionTypeConstants.
									DISPLAY_PAGE) &&
							LayoutPageTemplateCollectionPermission.contains(
								_themeDisplay.getPermissionChecker(),
								_layoutPageTemplateCollection,
								ActionKeys.UPDATE),
						dropdownItem -> {
							dropdownItem.putData(
								"action", "moveLayoutPageTemplateCollection");
							dropdownItem.putData(
								"itemSelectorURL", _getItemSelectorURL());
							dropdownItem.putData(
								"layoutPageTemplateCollectionId",
								String.valueOf(
									_layoutPageTemplateCollection.
										getLayoutPageTemplateCollectionId()));
							dropdownItem.putData(
								"layoutPageTemplateCollectionName",
								_layoutPageTemplateCollection.getName());
							dropdownItem.putData(
								"moveSelectedEntriesURL",
								PortletURLBuilder.createActionURL(
									_renderResponse
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
								LanguageUtil.get(_httpServletRequest, "move"));
						}
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> LayoutPageTemplateCollectionPermission.contains(
							_themeDisplay.getPermissionChecker(),
							_layoutPageTemplateCollection,
							ActionKeys.PERMISSIONS),
						dropdownItem -> {
							dropdownItem.putData(
								"action",
								"permissionsLayoutPageTemplateCollection");
							dropdownItem.putData(
								"permissionsLayoutPageTemplateCollectionURL",
								_getPermissionsLayoutPageTemplateCollectionURL());
							dropdownItem.setIcon("password-policies");
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, "permissions"));
						}
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> LayoutPageTemplateCollectionPermission.contains(
							_themeDisplay.getPermissionChecker(),
							_layoutPageTemplateCollection, ActionKeys.DELETE),
						dropdownItem -> {
							dropdownItem.putData(
								"action", "deleteLayoutPageTemplateCollection");
							dropdownItem.putData(
								"deleteLayoutPageTemplateCollectionURL",
								_getDeleteLayoutPageTemplateCollectionURL());
							dropdownItem.putData(
								"dialogTitle", _getDeleteDialogTitle());
							dropdownItem.setIcon("trash");
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, "delete"));
						}
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).build();
	}

	private String _getDeleteDialogTitle() {
		if (_layoutPageTemplateCollection.getType() ==
				LayoutPageTemplateCollectionTypeConstants.BASIC) {

			return LanguageUtil.get(_httpServletRequest, "page-template-set");
		}

		return LanguageUtil.get(_httpServletRequest, "folder");
	}

	private String _getDeleteLayoutPageTemplateCollectionURL() {
		return PortletURLBuilder.createActionURL(
			_renderResponse
		).setActionName(
			"/layout_page_template_admin/delete_layout_page_template_collection"
		).setRedirect(
			PortletURLBuilder.createRenderURL(
				_renderResponse
			).setTabs1(
				_tabs1
			).setParameter(
				"layoutPageTemplateCollectionId",
				_layoutPageTemplateCollection.
					getParentLayoutPageTemplateCollectionId()
			).buildString()
		).setParameter(
			"layoutPageTemplateCollectionId",
			_layoutPageTemplateCollection.getLayoutPageTemplateCollectionId()
		).buildString();
	}

	private String _getEditLayoutPageTemplateCollectionURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCRenderCommandName(
			"/layout_page_template_admin/edit_layout_page_template_collection"
		).setRedirect(
			_themeDisplay.getURLCurrent()
		).setTabs1(
			_tabs1
		).setParameter(
			"layoutPageTemplateCollectionId",
			_layoutPageTemplateCollection.getLayoutPageTemplateCollectionId()
		).buildString();
	}

	private String _getExportLayoutPageTemplateCollectionURL() {
		return ResourceURLBuilder.createResourceURL(
			_renderResponse
		).setParameter(
			"layoutPageTemplateCollectionsIds",
			_layoutPageTemplateCollection.getLayoutPageTemplateCollectionId()
		).setResourceID(
			"/layout_page_template_admin/export_layout_page_template_" +
				"entries_and_layout_page_template_collections"
		).buildString();
	}

	private String _getItemSelectorURL() {
		if (_itemSelectorURL != null) {
			return _itemSelectorURL;
		}

		ItemSelector itemSelector =
			(ItemSelector)_httpServletRequest.getAttribute(
				LayoutPageTemplateAdminWebKeys.ITEM_SELECTOR);

		LayoutPageTemplateCollectionTreeNodeItemSelectorCriterion
			layoutPageTemplateCollectionTreeNodeItemSelectorCriterion =
				new LayoutPageTemplateCollectionTreeNodeItemSelectorCriterion();

		layoutPageTemplateCollectionTreeNodeItemSelectorCriterion.
			setDesiredItemSelectorReturnTypes(new UUIDItemSelectorReturnType());
		layoutPageTemplateCollectionTreeNodeItemSelectorCriterion.
			setLayoutPageTemplateCollectionIds(
				new long[] {
					_layoutPageTemplateCollection.
						getLayoutPageTemplateCollectionId()
				});

		_itemSelectorURL = PortletURLBuilder.create(
			itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(_httpServletRequest),
				"selectFolder",
				layoutPageTemplateCollectionTreeNodeItemSelectorCriterion)
		).buildString();

		return _itemSelectorURL;
	}

	private String _getPermissionsLayoutPageTemplateCollectionURL()
		throws Exception {

		return PermissionsURLTag.doTag(
			StringPool.BLANK, LayoutPageTemplateCollection.class.getName(),
			_layoutPageTemplateCollection.getName(), null,
			String.valueOf(
				_layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId()),
			LiferayWindowState.POP_UP.toString(), null, _httpServletRequest);
	}

	private String _getRenameDialogTitle() {
		if (_layoutPageTemplateCollection.getType() ==
				LayoutPageTemplateCollectionTypeConstants.BASIC) {

			return LanguageUtil.get(
				_httpServletRequest, "rename-page-template-set");
		}

		return LanguageUtil.get(_httpServletRequest, "edit-folder");
	}

	private String _getUpdateLayoutPageTemplateCollectionURL() {
		return PortletURLBuilder.createActionURL(
			_renderResponse
		).setActionName(
			"/layout_page_template_admin/update_layout_page_template_collection"
		).setRedirect(
			PortletURLBuilder.createRenderURL(
				_renderResponse
			).setTabs1(
				_tabs1
			).buildString()
		).setParameter(
			"layoutPageTemplateCollectionId",
			_layoutPageTemplateCollection.getLayoutPageTemplateCollectionId()
		).buildString();
	}

	private final HttpServletRequest _httpServletRequest;
	private String _itemSelectorURL;
	private final LayoutPageTemplateCollection _layoutPageTemplateCollection;
	private final RenderResponse _renderResponse;
	private final String _tabs1;
	private final ThemeDisplay _themeDisplay;

}