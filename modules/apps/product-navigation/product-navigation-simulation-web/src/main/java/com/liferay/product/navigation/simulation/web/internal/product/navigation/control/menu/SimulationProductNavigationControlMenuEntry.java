/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.simulation.web.internal.product.navigation.control.menu;

import com.liferay.application.list.PanelApp;
import com.liferay.application.list.PanelAppRegistry;
import com.liferay.frontend.taglib.clay.servlet.taglib.ButtonTag;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletURLFactory;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.product.navigation.control.menu.BaseProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.ProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.constants.ProductNavigationControlMenuCategoryKeys;
import com.liferay.product.navigation.simulation.constants.ProductNavigationSimulationConstants;
import com.liferay.product.navigation.simulation.constants.ProductNavigationSimulationPortletKeys;
import com.liferay.taglib.aui.IconTag;
import com.liferay.taglib.aui.ScriptTag;
import com.liferay.taglib.ui.MessageTag;
import com.liferay.taglib.util.BodyBottomTag;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

import java.io.IOException;
import java.io.Writer;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Julio Camarero
 */
@Component(
	property = {
		"product.navigation.control.menu.category.key=" + ProductNavigationControlMenuCategoryKeys.USER,
		"product.navigation.control.menu.entry.order:Integer=300"
	},
	service = ProductNavigationControlMenuEntry.class
)
public class SimulationProductNavigationControlMenuEntry
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
	public boolean includeBody(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		BodyBottomTag bodyBottomTag = new BodyBottomTag();

		bodyBottomTag.setOutputKey("simulationMenu");

		try {
			bodyBottomTag.doBodyTag(
				httpServletRequest, httpServletResponse,
				this::_processBodyBottomTagBody);
		}
		catch (JspException jspException) {
			throw new IOException(jspException);
		}

		return true;
	}

	@Override
	public boolean includeIcon(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		Map<String, String> values = new HashMap<>();

		IconTag iconTag = new IconTag();

		iconTag.setCssClass("icon-monospaced");
		iconTag.setImage("simulation-menu-closed");

		try {
			values.put(
				"iconTag",
				iconTag.doTagAsString(httpServletRequest, httpServletResponse));
		}
		catch (JspException jspException) {
			ReflectionUtil.throwException(jspException);
		}

		values.put("portletNamespace", _portletNamespace);
		values.put(
			"simulationPanelURL",
			PortletURLBuilder.create(
				_portletURLFactory.create(
					httpServletRequest,
					ProductNavigationSimulationPortletKeys.
						PRODUCT_NAVIGATION_SIMULATION,
					PortletRequest.RENDER_PHASE)
			).setBackURL(
				_portal.getCurrentCompleteURL(httpServletRequest)
			).setWindowState(
				LiferayWindowState.EXCLUSIVE
			).buildString());
		values.put(
			"title",
			HtmlUtil.escape(_language.get(httpServletRequest, "simulation")));

		Writer writer = httpServletResponse.getWriter();

		writer.write(StringUtil.replace(_ICON_TMPL_CONTENT, "${", "}", values));

		return true;
	}

	@Override
	public boolean isShow(HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		if (layout.isEmbeddedPersonalApplication() ||
			layout.isTypeControlPanel()) {

			return false;
		}

		String layoutMode = ParamUtil.getString(
			httpServletRequest, "p_l_mode", Constants.VIEW);

		if (layoutMode.equals(Constants.EDIT)) {
			return false;
		}

		List<PanelApp> panelApps = _panelAppRegistry.getPanelApps(
			ProductNavigationSimulationConstants.SIMULATION_PANEL_CATEGORY_KEY,
			themeDisplay.getPermissionChecker(), themeDisplay.getScopeGroup());

		if (panelApps.isEmpty()) {
			return false;
		}

		return super.isShow(httpServletRequest);
	}

	@Activate
	protected void activate() {
		_portletNamespace = _portal.getPortletNamespace(
			ProductNavigationSimulationPortletKeys.
				PRODUCT_NAVIGATION_SIMULATION);
	}

	private void _processBodyBottomTagBody(PageContext pageContext) {
		try {
			MessageTag messageTag = new MessageTag();

			messageTag.setKey("simulation");

			Map<String, String> values = HashMapBuilder.put(
				"portletNamespace", _portletNamespace
			).put(
				"sidebarMessage", messageTag.doTagAsString(pageContext)
			).build();

			messageTag = new MessageTag();

			messageTag.setKey("simulation-panel");

			values.put(
				"simulationPanel", messageTag.doTagAsString(pageContext));

			ButtonTag buttonTag = new ButtonTag();

			buttonTag.setCssClass("close sidenav-close");
			buttonTag.setDisplayType("unstyled");
			buttonTag.setDynamicAttribute(
				StringPool.BLANK, "aria-label",
				_language.get(
					(HttpServletRequest)pageContext.getRequest(), "close"));
			buttonTag.setIcon("times");

			values.put("sidebarIcon", buttonTag.doTagAsString(pageContext));

			Writer writer = pageContext.getOut();

			writer.write(
				StringUtil.replace(_BODY_TMPL_CONTENT, "${", "}", values));

			ScriptTag scriptTag = new ScriptTag();

			scriptTag.setUse("liferay-store,io-request,parse-content");

			scriptTag.doBodyTag(pageContext, this::_processScriptTagBody);
		}
		catch (Exception exception) {
			ReflectionUtil.throwException(exception);
		}
	}

	private void _processScriptTagBody(PageContext pageContext) {
		Writer writer = pageContext.getOut();

		try {
			writer.write(
				StringUtil.replace(
					_BODY_SCRIPT_TMPL_CONTENT, "${", "}",
					Collections.singletonMap(
						"portletNamespace", _portletNamespace)));
		}
		catch (IOException ioException) {
			ReflectionUtil.throwException(ioException);
		}
	}

	private static final String _BODY_SCRIPT_TMPL_CONTENT = StringUtil.read(
		SimulationProductNavigationControlMenuEntry.class, "body_script.tmpl");

	private static final String _BODY_TMPL_CONTENT = StringUtil.read(
		SimulationProductNavigationControlMenuEntry.class, "body.tmpl");

	private static final String _ICON_TMPL_CONTENT = StringUtil.read(
		SimulationProductNavigationControlMenuEntry.class, "icon.tmpl");

	@Reference
	private Language _language;

	@Reference
	private PanelAppRegistry _panelAppRegistry;

	@Reference
	private Portal _portal;

	private String _portletNamespace;

	@Reference
	private PortletURLFactory _portletURLFactory;

}