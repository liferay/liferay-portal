/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.web.internal.portlet;

import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.content.constants.CPContentWebKeys;
import com.liferay.commerce.product.content.helper.CPCompareContentHelper;
import com.liferay.commerce.product.content.helper.CPContentHelper;
import com.liferay.commerce.product.content.render.list.CPContentListRendererRegistry;
import com.liferay.commerce.product.content.render.list.entry.CPContentListEntryRendererRegistry;
import com.liferay.commerce.product.content.web.internal.display.context.CPCompareContentMiniDisplayContext;
import com.liferay.commerce.product.type.CPTypeRegistry;
import com.liferay.commerce.product.util.CPCompareHelper;
import com.liferay.commerce.product.util.CPDefinitionHelper;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-commerce-product-compare-content-mini",
		"com.liferay.portlet.display-category=commerce",
		"com.liferay.portlet.layout-cacheable=true",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.scopeable=true",
		"jakarta.portlet.display-name=Product Comparison Bar",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.view-template=/compare_products_mini/view.jsp",
		"jakarta.portlet.name=" + CPPortletKeys.CP_COMPARE_CONTENT_MINI_WEB,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class CPCompareContentMiniPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		renderRequest.setAttribute(
			CPContentWebKeys.CP_COMPARE_CONTENT_HELPER,
			_cpCompareContentHelper);

		try {
			CPCompareContentMiniDisplayContext
				cpCompareContentMiniDisplayContext =
					new CPCompareContentMiniDisplayContext(
						_configurationProvider, _cpCompareHelper,
						_cpContentListEntryRendererRegistry,
						_cpContentListRendererRegistry, _cpDefinitionHelper,
						_cpTypeRegistry, _groupLocalService,
						_portal.getHttpServletRequest(renderRequest));

			renderRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT,
				cpCompareContentMiniDisplayContext);
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		renderRequest.setAttribute(
			CPContentWebKeys.CP_CONTENT_HELPER, _cpContentHelper);

		super.render(renderRequest, renderResponse);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPCompareContentMiniPortlet.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private CPCompareContentHelper _cpCompareContentHelper;

	@Reference
	private CPCompareHelper _cpCompareHelper;

	@Reference
	private CPContentHelper _cpContentHelper;

	@Reference
	private CPContentListEntryRendererRegistry
		_cpContentListEntryRendererRegistry;

	@Reference
	private CPContentListRendererRegistry _cpContentListRendererRegistry;

	@Reference
	private CPDefinitionHelper _cpDefinitionHelper;

	@Reference
	private CPTypeRegistry _cpTypeRegistry;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

}