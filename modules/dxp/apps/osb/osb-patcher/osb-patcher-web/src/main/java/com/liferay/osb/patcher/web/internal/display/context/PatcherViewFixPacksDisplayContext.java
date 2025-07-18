/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.osb.patcher.constants.PatcherActionKeys;
import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.permission.resource.PatcherPermission;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class PatcherViewFixPacksDisplayContext {

	public PatcherViewFixPacksDisplayContext(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getDropdownItems(PatcherFix patcherFix) {
		return DropdownItemListBuilder.add(
			() ->
				patcherFix.isLatestFix() &&
				PatcherPermission.contains(
					_themeDisplay.getPermissionChecker(), patcherFix,
					PatcherActionKeys.EDIT, patcherFix.getUserId()),
			dropdownItem -> {
				dropdownItem.setHref(
					PortletURLBuilder.createRenderURL(
						_renderResponse
					).setMVCRenderCommandName(
						"/patcher/edit_fixes"
					).setRedirect(
						_themeDisplay.getURLCurrent()
					).setParameter(
						"patcherFixId", patcherFix.getPatcherFixId()
					).buildString());
				dropdownItem.setIcon("pencil");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "edit"));
			}
		).add(
			() -> PatcherPermission.contains(
				_themeDisplay.getPermissionChecker(), patcherFix,
				PatcherActionKeys.EDIT_FIX_PACK_FIELDS, patcherFix.getUserId()),
			dropdownItem -> {
				dropdownItem.putData("action", "openModal");
				dropdownItem.putData(
					"title",
					LanguageUtil.get(_httpServletRequest, "edit-fix-packs"));
				dropdownItem.putData(
					"url",
					PortletURLBuilder.createRenderURL(
						_renderResponse
					).setMVCRenderCommandName(
						"/patcher/edit_fix_pack_fields_fixes"
					).setRedirect(
						_themeDisplay.getURLCurrent()
					).setParameter(
						"patcherFixId", patcherFix.getPatcherFixId()
					).setWindowState(
						LiferayWindowState.POP_UP
					).buildString());
				dropdownItem.setIcon("pencil");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "edit-fix-packs"));
			}
		).add(
			() -> PatcherPermission.contains(
				_themeDisplay.getPermissionChecker(), patcherFix,
				PatcherActionKeys.SET_FIX_PACK_FIELDS, patcherFix.getUserId()),
			dropdownItem -> {
				dropdownItem.putData("action", "submitForm");
				dropdownItem.putData(
					"url",
					PortletURLBuilder.createActionURL(
						_renderResponse
					).setActionName(
						"/patcher/set_fix_pack_fields_fixes"
					).setRedirect(
						_themeDisplay.getURLCurrent()
					).setParameter(
						"patcherFixId", patcherFix.getPatcherFixId()
					).buildString());
				dropdownItem.setIcon("trash");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "remove-fix-packs"));
			}
		).build();
	}

	private final HttpServletRequest _httpServletRequest;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}