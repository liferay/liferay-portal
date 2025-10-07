/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.analytics.web.internal.portlet;

import com.liferay.layout.utility.page.kernel.provider.LayoutUtilityPageEntryLayoutProvider;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.product.analytics.web.internal.constants.ProductAnalyticsPortletKeys;
import com.liferay.product.analytics.web.internal.constants.ProductAnalyticsWebKeys;
import com.liferay.product.analytics.web.internal.display.context.ProductAnalyticsConsentPanelDisplayContext;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Christopher Kian
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-product-analytics-consent-panel",
		"com.liferay.portlet.display-category=category.tools",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.show-portlet-access-denied=false",
		"com.liferay.portlet.show-portlet-inactive=false",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Product Analytics Consent Panel",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/product_analytics_consent_panel/view.jsp",
		"jakarta.portlet.name=" + ProductAnalyticsPortletKeys.PRODUCT_ANALYTICS_CONSENT_PANEL,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class ProductAnalyticsConsentPanelPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		if (!FeatureFlagManagerUtil.isEnabled(
				_portal.getCompanyId(renderRequest), "LPD-51356")) {

			return;
		}

		ProductAnalyticsConsentPanelDisplayContext
			productAnalyticsConsentPanelDisplayContext =
				new ProductAnalyticsConsentPanelDisplayContext(
					_layoutUtilityPageEntryLayoutProvider,
					_portal.getHttpServletRequest(renderRequest));

		renderRequest.setAttribute(
			ProductAnalyticsWebKeys.
				PRODUCT_ANALYTICS_CONSENT_PANEL_DISPLAY_CONTEXT,
			productAnalyticsConsentPanelDisplayContext);

		super.render(renderRequest, renderResponse);
	}

	@Reference
	private LayoutUtilityPageEntryLayoutProvider
		_layoutUtilityPageEntryLayoutProvider;

	@Reference
	private Portal _portal;

}