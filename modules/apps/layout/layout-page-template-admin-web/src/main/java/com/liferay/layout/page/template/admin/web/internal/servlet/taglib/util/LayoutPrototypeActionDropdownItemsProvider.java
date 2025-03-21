/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.servlet.taglib.util;

import com.liferay.exportimport.constants.ExportImportPortletKeys;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalServiceUtil;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.service.permission.LayoutPrototypePermissionUtil;
import com.liferay.taglib.security.PermissionsURLTag;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class LayoutPrototypeActionDropdownItemsProvider {

	public LayoutPrototypeActionDropdownItemsProvider(
		LayoutPrototype layoutPrototype, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_layoutPrototype = layoutPrototype;
		_renderResponse = renderResponse;

		_httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);

		_themeDisplay = (ThemeDisplay)_httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getActionDropdownItems() throws Exception {
		boolean hasExportImportLayoutsPermission = GroupPermissionUtil.contains(
			_themeDisplay.getPermissionChecker(), _layoutPrototype.getGroup(),
			ActionKeys.EXPORT_IMPORT_LAYOUTS);
		boolean hasUpdatePermission = LayoutPrototypePermissionUtil.contains(
			_themeDisplay.getPermissionChecker(),
			_layoutPrototype.getLayoutPrototypeId(), ActionKeys.UPDATE);

		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> hasUpdatePermission,
						_getEditLayoutPrototypeActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> hasExportImportLayoutsPermission,
						_getExportLayoutPrototypeActionUnsafeConsumer()
					).add(
						() -> hasExportImportLayoutsPermission,
						_getImportLayoutPrototypeActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> hasUpdatePermission,
						_getConfigureLayoutPrototypeActionUnsafeConsumer()
					).add(
						() -> LayoutPrototypePermissionUtil.contains(
							_themeDisplay.getPermissionChecker(),
							_layoutPrototype.getLayoutPrototypeId(),
							ActionKeys.PERMISSIONS),
						_getPermissionsLayoutPrototypeActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> LayoutPrototypePermissionUtil.contains(
							_themeDisplay.getPermissionChecker(),
							_layoutPrototype.getLayoutPrototypeId(),
							ActionKeys.DELETE),
						_getDeleteLayoutPrototypeActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).build();
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getConfigureLayoutPrototypeActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.setHref(
				_renderResponse.createRenderURL(), "mvcPath",
				"/edit_layout_prototype.jsp", "backURLTitle",
				LanguageUtil.get(
					_themeDisplay.getLocale(), "widget-page-templates"),
				"layoutPrototypeId", _layoutPrototype.getLayoutPrototypeId());
			dropdownItem.setIcon("cog");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "configure"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getDeleteLayoutPrototypeActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "deleteLayoutPrototype");
			dropdownItem.putData(
				"deleteLayoutPrototypeURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/layout_page_template_admin/delete_layout_prototype"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"layoutPrototypeId", _layoutPrototype.getLayoutPrototypeId()
				).buildString());
			dropdownItem.setIcon("trash");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "delete"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
			_getEditLayoutPrototypeActionUnsafeConsumer()
		throws Exception {

		Group layoutPrototypeGroup = _layoutPrototype.getGroup();

		return dropdownItem -> {
			dropdownItem.setHref(
				_getLayoutPrototypeGroupHref(layoutPrototypeGroup));
			dropdownItem.setIcon("pencil");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "edit"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
			_getExportLayoutPrototypeActionUnsafeConsumer()
		throws Exception {

		return dropdownItem -> {
			dropdownItem.putData("action", "exportLayoutPrototype");
			dropdownItem.putData(
				"exportLayoutPrototypeURL",
				PortletURLBuilder.create(
					PortalUtil.getControlPanelPortletURL(
						_httpServletRequest, ExportImportPortletKeys.EXPORT,
						PortletRequest.RENDER_PHASE)
				).setMVCRenderCommandName(
					"/export_import/export_layouts"
				).setCMD(
					Constants.EXPORT
				).setParameter(
					"groupId", _layoutPrototype.getGroupId()
				).setParameter(
					"privateLayout", true
				).setParameter(
					"rootNodeName",
					_layoutPrototype.getName(_themeDisplay.getLocale())
				).setParameter(
					"showHeader", false
				).setWindowState(
					LiferayWindowState.POP_UP
				).buildString());
			dropdownItem.setIcon("upload");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "export"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
			_getImportLayoutPrototypeActionUnsafeConsumer()
		throws Exception {

		return dropdownItem -> {
			dropdownItem.putData("action", "importLayoutPrototype");
			dropdownItem.putData(
				"importLayoutPrototypeURL",
				PortletURLBuilder.create(
					PortalUtil.getControlPanelPortletURL(
						_httpServletRequest, ExportImportPortletKeys.IMPORT,
						PortletRequest.RENDER_PHASE)
				).setMVCRenderCommandName(
					"/export_import/import_layouts"
				).setCMD(
					Constants.IMPORT
				).setParameter(
					"groupId", _layoutPrototype.getGroupId()
				).setParameter(
					"privateLayout", true
				).setParameter(
					"rootNodeName",
					_layoutPrototype.getName(_themeDisplay.getLocale())
				).setParameter(
					"showHeader", false
				).setWindowState(
					LiferayWindowState.POP_UP
				).buildString());
			dropdownItem.setIcon("download");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "import"));
		};
	}

	private String _getLayoutPrototypeGroupHref(Group layoutPrototypeGroup) {
		String layoutFullURL = layoutPrototypeGroup.getDisplayURL(
			_themeDisplay, true);

		return HttpComponentsUtil.addParameters(
			layoutFullURL, "p_l_back_url", _themeDisplay.getURLCurrent(),
			"p_l_back_url_title",
			LanguageUtil.get(_httpServletRequest, "widget-page-templates"));
	}

	private UnsafeConsumer<DropdownItem, Exception>
			_getPermissionsLayoutPrototypeActionUnsafeConsumer()
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryLocalServiceUtil.
				fetchFirstLayoutPageTemplateEntry(
					_layoutPrototype.getLayoutPrototypeId());

		String permissionsLayoutPrototypeURL = PermissionsURLTag.doTag(
			StringPool.BLANK, LayoutPageTemplateEntry.class.getName(),
			_layoutPrototype.getName(_themeDisplay.getLocale()), null,
			String.valueOf(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId()),
			LiferayWindowState.POP_UP.toString(), null, _httpServletRequest);

		return dropdownItem -> {
			dropdownItem.putData("action", "permissionsLayoutPrototype");
			dropdownItem.putData(
				"permissionsLayoutPrototypeURL", permissionsLayoutPrototypeURL);
			dropdownItem.setIcon("password-policies");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "permissions"));
		};
	}

	private final HttpServletRequest _httpServletRequest;
	private final LayoutPrototype _layoutPrototype;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}