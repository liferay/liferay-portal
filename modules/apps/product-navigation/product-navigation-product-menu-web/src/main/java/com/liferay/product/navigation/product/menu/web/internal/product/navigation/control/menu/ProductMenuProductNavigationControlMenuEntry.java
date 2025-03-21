/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.product.menu.web.internal.product.navigation.control.menu;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyNonceProviderUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.SessionClicks;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.product.navigation.control.menu.BaseProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.ProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.constants.ProductNavigationControlMenuCategoryKeys;
import com.liferay.product.navigation.product.menu.constants.ProductNavigationProductMenuPortletKeys;
import com.liferay.product.navigation.product.menu.helper.ProductNavigationProductMenuHelper;
import com.liferay.taglib.aui.IconTag;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspException;

import java.io.IOException;
import java.io.Writer;

import java.util.Locale;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Julio Camarero
 */
@Component(
	property = {
		"product.navigation.control.menu.category.key=" + ProductNavigationControlMenuCategoryKeys.SITES,
		"product.navigation.control.menu.entry.order:Integer=100"
	},
	service = ProductNavigationControlMenuEntry.class
)
public class ProductMenuProductNavigationControlMenuEntry
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

		try {
			Writer writer = httpServletResponse.getWriter();

			IconTag closedIconTag = new IconTag();

			closedIconTag.setCssClass(
				"icon-monospaced icon-product-menu-closed");
			closedIconTag.setImage("product-menu-closed");

			IconTag openIconTag = new IconTag();

			openIconTag.setCssClass("icon-monospaced icon-product-menu-open");
			openIconTag.setImage("product-menu-open");

			String productMenuState = SessionClicks.get(
				httpServletRequest,
				"com.liferay.product.navigation.product.menu." +
					"web_productMenuState",
				"closed");
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			writer.write(
				StringUtil.replace(
					_TMPL_CONTENT, "${", "}",
					HashMapBuilder.put(
						"closedIcon",
						closedIconTag.doTagAsString(
							httpServletRequest, httpServletResponse)
					).put(
						"closeProductMenuTitle",
						HtmlUtil.escape(
							_language.get(
								httpServletRequest, "close-product-menu"))
					).put(
						"cssClass",
						() -> {
							if (Objects.equals(productMenuState, "open")) {
								return "active";
							}

							return StringPool.BLANK;
						}
					).put(
						"dataURL",
						() -> {
							PortletURL portletURL = PortletURLBuilder.create(
								PortletURLFactoryUtil.create(
									httpServletRequest,
									ProductNavigationProductMenuPortletKeys.
										PRODUCT_NAVIGATION_PRODUCT_MENU,
									RenderRequest.RENDER_PHASE)
							).setMVCPath(
								"/portlet/product_menu.jsp"
							).setRedirect(
								themeDisplay.getURLCurrent()
							).setBackURL(
								themeDisplay.getURLCurrent()
							).setParameter(
								"selPpid",
								() -> {
									PortletDisplay portletDisplay =
										themeDisplay.getPortletDisplay();

									return portletDisplay.getId();
								}
							).setWindowState(
								LiferayWindowState.EXCLUSIVE
							).buildPortletURL();

							return "data-url='" + portletURL + "'";
						}
					).put(
						"isOpen",
						() -> {
							if (Objects.equals(productMenuState, "open")) {
								return StringPool.TRUE;
							}

							return StringPool.FALSE;
						}
					).put(
						"nonceAttribute",
						ContentSecurityPolicyNonceProviderUtil.
							getNonceAttribute(httpServletRequest)
					).put(
						"openIcon",
						openIconTag.doTagAsString(
							httpServletRequest, httpServletResponse)
					).put(
						"openProductMenuTitle",
						HtmlUtil.escape(
							_language.get(
								httpServletRequest, "open-product-menu"))
					).put(
						"portletNamespace",
						_portal.getPortletNamespace(
							ProductNavigationProductMenuPortletKeys.
								PRODUCT_NAVIGATION_PRODUCT_MENU)
					).put(
						"title",
						() -> {
							if (Objects.equals(productMenuState, "open")) {
								return HtmlUtil.escape(
									_language.get(
										httpServletRequest,
										"close-product-menu"));
							}

							return HtmlUtil.escape(
								_language.get(
									httpServletRequest, "open-product-menu"));
						}
					).build()));
		}
		catch (JspException jspException) {
			ReflectionUtil.throwException(jspException);
		}

		return true;
	}

	@Override
	public boolean isShow(HttpServletRequest httpServletRequest)
		throws PortalException {

		return _productNavigationProductMenuHelper.isShowProductMenu(
			httpServletRequest);
	}

	private static final String _TMPL_CONTENT = StringUtil.read(
		ProductMenuProductNavigationControlMenuEntry.class, "icon.tmpl");

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private ProductNavigationProductMenuHelper
		_productNavigationProductMenuHelper;

}