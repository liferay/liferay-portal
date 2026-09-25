/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.product.navigation.control.menu;

import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.product.navigation.control.menu.BaseProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.ProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.constants.ProductNavigationControlMenuCategoryKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.Writer;

import java.util.Locale;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(
	property = {
		"product.navigation.control.menu.category.key=" + ProductNavigationControlMenuCategoryKeys.USER,
		"product.navigation.control.menu.entry.order:Integer=50"
	},
	service = ProductNavigationControlMenuEntry.class
)
public class ElementVariationsSimulationProductNavigationControlMenuEntry
	extends BaseProductNavigationControlMenuEntry {

	@Override
	public String getLabel(Locale locale) {
		return null;
	}

	@Override
	public String getURL(HttpServletRequest httpServletRequest) {
		return null;
	}

	@Override
	public boolean includeIcon(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		Writer writer = httpServletResponse.getWriter();

		writer.write(_TMPL_CONTENT);

		return true;
	}

	@Override
	public boolean isShow(HttpServletRequest httpServletRequest) {
		HttpServletRequest originalHttpServletRequest =
			_portal.getOriginalServletRequest(httpServletRequest);

		String portletId = ParamUtil.getString(
			originalHttpServletRequest, "p_p_id");

		if (!Objects.equals(
				portletId,
				ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET)) {

			return false;
		}

		String mvcRenderCommandName = ParamUtil.getString(
			originalHttpServletRequest,
			_portal.getPortletNamespace(portletId) + "mvcRenderCommandName");

		return Objects.equals(
			mvcRenderCommandName,
			"/layout_content_page_editor/edit_element_variations");
	}

	private static final String _TMPL_CONTENT = StringUtil.read(
		ElementVariationsSimulationProductNavigationControlMenuEntry.class,
		"/META-INF/resources/control/menu" +
			"/element_variations_simulation_control_menu_entry_icon.tmpl");

	@Reference
	private Portal _portal;

}