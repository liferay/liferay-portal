/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.template.web.internal.util;

import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalServiceUtil;
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
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.security.PermissionsURLTag;
import com.liferay.template.constants.TemplatePortletKeys;
import com.liferay.template.model.TemplateEntry;
import com.liferay.template.web.internal.security.permissions.resource.TemplateEntryPermission;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class TemplateEntryActionDropdownItemsProvider {

	public TemplateEntryActionDropdownItemsProvider(
		boolean addButtonEnabled, HttpServletRequest httpServletRequest,
		LiferayPortletResponse liferayPortletResponse, String tabs1,
		TemplateEntry templateEntry) {

		_addButtonEnabled = addButtonEnabled;
		_httpServletRequest = httpServletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_tabs1 = tabs1;
		_templateEntry = templateEntry;

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
							TemplateEntryPermission.contains(
								_themeDisplay.getPermissionChecker(),
								_templateEntry, ActionKeys.UPDATE),
						_getEditTemplateEntryActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> _addButtonEnabled,
						_getCopyTemplateEntryActionUnsafeConsumer()
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
							TemplateEntryPermission.contains(
								_themeDisplay.getPermissionChecker(),
								_templateEntry, ActionKeys.PERMISSIONS),
						_getPermissionsTemplateEntryActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> TemplateEntryPermission.contains(
							_themeDisplay.getPermissionChecker(),
							_templateEntry, ActionKeys.DELETE),
						_getDeleteTemplateEntryActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).build();
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getCopyTemplateEntryActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.setHref(
				PortletURLBuilder.createRenderURL(
					_liferayPortletResponse
				).setMVCPath(
					"/copy_template_entry.jsp"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"templateEntryId", _templateEntry.getTemplateEntryId()
				).buildPortletURL());
			dropdownItem.setIcon("copy");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "make-a-copy"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getDeleteTemplateEntryActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "deleteTemplateEntry");
			dropdownItem.putData(
				"deleteTemplateEntryURL",
				PortletURLBuilder.createActionURL(
					_liferayPortletResponse
				).setActionName(
					"/template/delete_template_entry"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"templateEntryId", _templateEntry.getTemplateEntryId()
				).buildString());
			dropdownItem.setIcon("trash");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "delete"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getEditTemplateEntryActionUnsafeConsumer() {

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
					"ddmTemplateId", _templateEntry.getDDMTemplateId()
				).setParameter(
					"templateEntryId", _templateEntry.getTemplateEntryId()
				).buildPortletURL());
			dropdownItem.setIcon("pencil");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "edit"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
			_getPermissionsTemplateEntryActionUnsafeConsumer()
		throws Exception {

		DDMTemplate ddmTemplate = DDMTemplateLocalServiceUtil.fetchDDMTemplate(
			_templateEntry.getDDMTemplateId());

		String permissionsDisplayPageURL = PermissionsURLTag.doTag(
			StringPool.BLANK, DDMTemplate.class.getName(),
			ddmTemplate.getName(_themeDisplay.getLocale()), null,
			String.valueOf(_templateEntry.getDDMTemplateId()),
			LiferayWindowState.POP_UP.toString(), null, _httpServletRequest);

		return dropdownItem -> {
			dropdownItem.putData("action", "permissionsTemplateEntry");
			dropdownItem.putData(
				"permissionsTemplateEntryURL", permissionsDisplayPageURL);
			dropdownItem.setIcon("password-policies");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "permissions"));
		};
	}

	private final boolean _addButtonEnabled;
	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final String _tabs1;
	private final TemplateEntry _templateEntry;
	private final ThemeDisplay _themeDisplay;

}