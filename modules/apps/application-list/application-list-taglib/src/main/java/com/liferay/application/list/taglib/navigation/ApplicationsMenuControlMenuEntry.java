/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.application.list.taglib.navigation;

import com.liferay.application.list.PanelAppRegistry;
import com.liferay.application.list.constants.ApplicationListWebKeys;
import com.liferay.application.list.display.context.logic.PanelCategoryHelper;
import com.liferay.application.list.taglib.internal.display.context.ApplicationsMenuDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.IconTag;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
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
public class ApplicationsMenuControlMenuEntry
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

		ApplicationsMenuDisplayContext applicationsMenuDisplayContext =
			new ApplicationsMenuDisplayContext(httpServletRequest);

		try {
			PrintWriter printWriter = httpServletResponse.getWriter();

			printWriter.write("<li class=\"control-menu-nav-item");

			if (applicationsMenuDisplayContext.isVisible()) {
				printWriter.write(" active");
			}

			printWriter.write("\"><div role=\"tablist\">");
			printWriter.write(
				StringBundler.concat(
					"<button class=\"btn btn-monospaced btn-sm ",
					"control-menu-nav-link lfr-portal-tooltip ",
					"product-menu-toast-toggle\">"));

			if (applicationsMenuDisplayContext.isVisible()) {
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
					"{ApplicationsMenuToggle} from application-list-taglib"),
				HashMapBuilder.<String, Object>put(
					"initialState", applicationsMenuDisplayContext.isVisible()
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

		if (FeatureFlagManagerUtil.isEnabled(
				themeDisplay.getCompanyId(), "LPD-36105")) {

			PanelAppRegistry panelAppRegistry =
				(PanelAppRegistry)httpServletRequest.getAttribute(
					ApplicationListWebKeys.PANEL_APP_REGISTRY);

			PanelCategoryHelper panelCategoryHelper = new PanelCategoryHelper(
				panelAppRegistry);

			return panelCategoryHelper.containsPortlet(
				themeDisplay.getPpid(), "applications_menu");
		}

		return false;
	}

	@Reference
	private ReactRenderer _reactRenderer;

}