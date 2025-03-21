/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.template.web.internal.util;

import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.PortletPreferenceValueLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.display.template.PortletDisplayTemplate;
import com.liferay.taglib.security.PermissionsURLTag;
import com.liferay.template.constants.TemplatePortletKeys;
import com.liferay.template.web.internal.security.permissions.resource.DDMTemplatePermission;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Lourdes Fernández Besada
 */
public class DDMTemplateActionDropdownItemsProvider {

	public DDMTemplateActionDropdownItemsProvider(
		boolean addButtonEnabled, DDMTemplate ddmTemplate,
		HttpServletRequest httpServletRequest,
		LiferayPortletResponse liferayPortletResponse, String tabs1) {

		_addButtonEnabled = addButtonEnabled;
		_ddmTemplate = ddmTemplate;
		_httpServletRequest = httpServletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_tabs1 = tabs1;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getActionDropdownItems() {
		Group scopeGroup = _themeDisplay.getScopeGroup();

		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() ->
							(!scopeGroup.hasLocalOrRemoteStagingGroup() ||
							 !scopeGroup.isStagedPortlet(
								 TemplatePortletKeys.TEMPLATE)) &&
							DDMTemplatePermission.contains(
								_themeDisplay.getPermissionChecker(),
								_ddmTemplate, ActionKeys.UPDATE),
						_getEditDDMTemplateActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() ->
							_addButtonEnabled &&
							DDMTemplatePermission.containsAddTemplatePermission(
								_themeDisplay.getPermissionChecker(),
								_themeDisplay.getScopeGroupId(),
								_ddmTemplate.getClassNameId(),
								_ddmTemplate.getResourceClassNameId()),
						_getCopyDDMTemplateActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						_getViewDDMTemplateUsagesActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() ->
							(!scopeGroup.hasLocalOrRemoteStagingGroup() ||
							 !scopeGroup.isStagedPortlet(
								 TemplatePortletKeys.TEMPLATE)) &&
							DDMTemplatePermission.contains(
								_themeDisplay.getPermissionChecker(),
								_ddmTemplate, ActionKeys.PERMISSIONS),
						_getPermissionsDDMTemplateActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> DDMTemplatePermission.contains(
							_themeDisplay.getPermissionChecker(), _ddmTemplate,
							ActionKeys.DELETE),
						_getDeleteDDMTemplateActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).build();
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getCopyDDMTemplateActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.setHref(
				PortletURLBuilder.createRenderURL(
					_liferayPortletResponse
				).setMVCPath(
					"/copy_ddm_template.jsp"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"ddmTemplateId", _ddmTemplate.getTemplateId()
				).buildPortletURL());
			dropdownItem.setIcon("copy");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "make-a-copy"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getDeleteDDMTemplateActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "deleteDDMTemplate");
			dropdownItem.putData(
				"deleteDDMTemplateURL",
				PortletURLBuilder.createActionURL(
					_liferayPortletResponse
				).setActionName(
					"/template/delete_ddm_template"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"ddmTemplateId", _ddmTemplate.getTemplateId()
				).buildString());
			dropdownItem.putData(
				"usagesCount", String.valueOf(_getUsagesCount()));
			dropdownItem.setIcon("trash");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "delete"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getEditDDMTemplateActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.setHref(
				PortletURLBuilder.createRenderURL(
					_liferayPortletResponse
				).setMVCRenderCommandName(
					"/template/edit_ddm_template"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setTabs1(
					_tabs1
				).setParameter(
					"ddmTemplateId", _ddmTemplate.getTemplateId()
				).buildPortletURL());
			dropdownItem.setIcon("pencil");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "edit"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
			_getPermissionsDDMTemplateActionUnsafeConsumer()
		throws Exception {

		String permissionsDisplayPageURL = PermissionsURLTag.doTag(
			StringPool.BLANK, DDMTemplate.class.getName(),
			_ddmTemplate.getName(), null,
			String.valueOf(_ddmTemplate.getTemplateId()),
			LiferayWindowState.POP_UP.toString(), null, _httpServletRequest);

		return dropdownItem -> {
			dropdownItem.putData("action", "permissionsDDMTemplate");
			dropdownItem.putData(
				"permissionsDDMTemplateURL", permissionsDisplayPageURL);
			dropdownItem.setIcon("password-policies");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "permissions"));
		};
	}

	private int _getUsagesCount() {
		return PortletPreferenceValueLocalServiceUtil.
			getPortletPreferenceValuesCount(
				_ddmTemplate.getCompanyId(), "displayStyle",
				PortletDisplayTemplate.DISPLAY_STYLE_PREFIX +
					HtmlUtil.escape(_ddmTemplate.getTemplateKey()));
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getViewDDMTemplateUsagesActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.setDisabled(_getUsagesCount() == 0);
			dropdownItem.setHref(
				PortletURLBuilder.createRenderURL(
					_liferayPortletResponse
				).setMVCRenderCommandName(
					"/template/view_widget_templates_usages"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"ddmTemplateId", _ddmTemplate.getTemplateId()
				).buildPortletURL());
			dropdownItem.setIcon("list-ul");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "view-usages"));
		};
	}

	private final boolean _addButtonEnabled;
	private final DDMTemplate _ddmTemplate;
	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final String _tabs1;
	private final ThemeDisplay _themeDisplay;

}