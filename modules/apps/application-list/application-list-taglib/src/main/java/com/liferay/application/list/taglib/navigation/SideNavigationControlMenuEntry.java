/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.application.list.taglib.navigation;

import com.liferay.application.list.PanelAppRegistry;
import com.liferay.application.list.display.context.logic.PanelCategoryHelper;
import com.liferay.application.list.taglib.internal.display.context.SideNavigationDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.IconTag;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.template.react.renderer.ComponentDescriptor;
import com.liferay.portal.template.react.renderer.ReactRenderer;
import com.liferay.product.navigation.control.menu.BaseProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.ProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.constants.ProductNavigationControlMenuCategoryKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gabriel Lima
 */
@Component(
	property = {
		"product.navigation.control.menu.category.key=" + ProductNavigationControlMenuCategoryKeys.SITES,
		"product.navigation.control.menu.entry.order:Integer=100"
	},
	service = ProductNavigationControlMenuEntry.class
)
public class SideNavigationControlMenuEntry
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

		SideNavigationDisplayContext sideNavigationDisplayContext =
			new SideNavigationDisplayContext(
				httpServletRequest, _panelAppRegistry);

		try {
			PrintWriter printWriter = httpServletResponse.getWriter();

			printWriter.write("<li class=\"control-menu-nav-item");

			if (sideNavigationDisplayContext.isVisible()) {
				printWriter.write(" active");
			}

			printWriter.write("\"><div role=\"tablist\">");
			printWriter.write(
				StringBundler.concat(
					"<button class=\"btn btn-monospaced btn-sm ",
					"control-menu-nav-link lfr-portal-tooltip ",
					"product-menu-toast-toggle\">"));

			if (sideNavigationDisplayContext.isVisible()) {
				IconTag openIconTag = new IconTag();

				openIconTag.setCssClass(
					"icon-monospaced icon-product-menu-open");
				openIconTag.setSymbol("product-menu-open");

				openIconTag.doTag(httpServletRequest, httpServletResponse);
			}
			else {
				IconTag closedIconTag = new IconTag();

				closedIconTag.setCssClass(
					"icon-monospaced icon-product-menu-closed");
				closedIconTag.setSymbol("product-menu-closed");

				closedIconTag.doTag(httpServletRequest, httpServletResponse);
			}

			printWriter.write("</button>");

			_reactRenderer.renderReact(
				new ComponentDescriptor(
					"{UnifiedSidebarToggler} from frontend-js-components-web",
					null, null, true),
				HashMapBuilder.<String, Object>put(
					"navigationId",
					"com_liferay_application_list_taglib_side_navigation"
				).put(
					"stateChangedEventName", "sideNavigationStateChanged"
				).put(
					"stateRequestedEventName", "sideNavigationStateRequested"
				).put(
					"visible", sideNavigationDisplayContext.isVisible()
				).build(),
				httpServletRequest, printWriter);

			printWriter.write("</div></li>");
		}
		catch (Exception exception) {
			throw new IOException(exception);
		}

		return true;
	}

	@Override
	public boolean isShow(HttpServletRequest httpServletRequest) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		if ((layout != null) && !layout.isTypeControlPanel()) {
			return false;
		}

		PanelCategoryHelper panelCategoryHelper = new PanelCategoryHelper(
			_panelAppRegistry);

		return panelCategoryHelper.containsPortlet(
			themeDisplay.getPpid(), "applications_menu");
	}

	@Reference
	private PanelAppRegistry _panelAppRegistry;

	@Reference
	private ReactRenderer _reactRenderer;

}