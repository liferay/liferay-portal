/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.servlet.taglib.util;

import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.journal.web.internal.security.permission.resource.DDMTemplatePermission;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.security.PermissionsURLTag;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class JournalDDMTemplateActionDropdownItemsProvider {

	public JournalDDMTemplateActionDropdownItemsProvider(
		DDMTemplate ddmTemplate, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_ddmTemplate = ddmTemplate;
		_renderResponse = renderResponse;

		_httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		_themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getActionDropdownItems() throws Exception {
		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> DDMTemplatePermission.contains(
							_themeDisplay.getPermissionChecker(), _ddmTemplate,
							ActionKeys.UPDATE),
						_getEditDDMTemplateActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> {
							Group scopeGroup = _themeDisplay.getScopeGroup();

							if ((!scopeGroup.hasLocalOrRemoteStagingGroup() ||
								 scopeGroup.isStagingGroup()) &&
								DDMTemplatePermission.
									containsAddTemplatePermission(
										_themeDisplay.getPermissionChecker(),
										_themeDisplay.getScopeGroupId(),
										_ddmTemplate.getClassNameId(),
										_ddmTemplate.
											getResourceClassNameId())) {

								return true;
							}

							return false;
						},
						_getCopyDDMTemplateActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> DDMTemplatePermission.contains(
							_themeDisplay.getPermissionChecker(), _ddmTemplate,
							ActionKeys.PERMISSIONS),
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
				_renderResponse.createRenderURL(), "mvcPath",
				"/copy_ddm_template.jsp", "redirect",
				_themeDisplay.getURLCurrent(), "ddmTemplateId",
				_ddmTemplate.getTemplateId());
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
					_renderResponse
				).setActionName(
					"/journal/delete_ddm_template"
				).setMVCPath(
					"/view_ddm_templates.jsp"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"ddmTemplateId", _ddmTemplate.getTemplateId()
				).buildString());
			dropdownItem.setIcon("trash");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "delete"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getEditDDMTemplateActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.setHref(
				_renderResponse.createRenderURL(), "mvcPath",
				"/edit_ddm_template.jsp", "redirect",
				_themeDisplay.getURLCurrent(), "ddmTemplateId",
				_ddmTemplate.getTemplateId());
			dropdownItem.setIcon("pencil");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "edit"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
			_getPermissionsDDMTemplateActionUnsafeConsumer()
		throws Exception {

		String permissionsDDMTemplateURL = PermissionsURLTag.doTag(
			StringPool.BLANK,
			DDMTemplatePermission.getTemplateModelResourceName(
				_ddmTemplate.getResourceClassNameId()),
			_ddmTemplate.getName(_themeDisplay.getLocale()), null,
			String.valueOf(_ddmTemplate.getTemplateId()),
			LiferayWindowState.POP_UP.toString(), null, _httpServletRequest);

		return dropdownItem -> {
			dropdownItem.putData("action", "permissionsDDMTemplate");
			dropdownItem.putData(
				"permissionsDDMTemplateURL", permissionsDDMTemplateURL);
			dropdownItem.setIcon("password-policies");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "permissions"));
		};
	}

	private final DDMTemplate _ddmTemplate;
	private final HttpServletRequest _httpServletRequest;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}