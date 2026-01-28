/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.analytics.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationFormRenderer;
import com.liferay.layout.utility.page.kernel.provider.LayoutUtilityPageEntryLayoutProvider;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.product.analytics.web.internal.configuration.ProductAnalyticsConfiguration;
import com.liferay.product.analytics.web.internal.constants.ProductAnalyticsWebKeys;
import com.liferay.product.analytics.web.internal.display.context.ProductAnalyticsConfigurationDisplayContext;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Date;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lucas Miranda
 */
@Component(service = ConfigurationFormRenderer.class)
public class ProductAnalyticsConfigurationFormRenderer
	implements ConfigurationFormRenderer {

	@Override
	public String getPid() {
		return ProductAnalyticsConfiguration.class.getName();
	}

	@Override
	public Map<String, Object> getRequestParameters(
		HttpServletRequest httpServletRequest) {

		return HashMapBuilder.<String, Object>put(
			"consentRenewalPeriod",
			ParamUtil.getInteger(httpServletRequest, "consentRenewalPeriod")
		).put(
			"enabled", ParamUtil.getBoolean(httpServletRequest, "enabled")
		).put(
			"lastModified",
			() -> {
				Date now = new Date();

				return ParamUtil.getLong(
					httpServletRequest, "lastModified", now.getTime());
			}
		).build();
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		try {
			_render(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			throw new IOException(
				"Unable to render /product_analytics_configuration/view.jsp",
				exception);
		}
	}

	private void _render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		RequestDispatcher requestDispatcher =
			_servletContext.getRequestDispatcher(
				"/product_analytics_configuration/view.jsp");

		httpServletRequest.setAttribute(
			ProductAnalyticsWebKeys.
				PRODUCT_ANALYTICS_CONFIGURATION_DISPLAY_CONTEXT,
			new ProductAnalyticsConfigurationDisplayContext(
				httpServletRequest, _layoutUtilityPageEntryLayoutProvider));

		requestDispatcher.include(httpServletRequest, httpServletResponse);
	}

	@Reference
	private LayoutUtilityPageEntryLayoutProvider
		_layoutUtilityPageEntryLayoutProvider;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.product.analytics.web)"
	)
	private ServletContext _servletContext;

}