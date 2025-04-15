/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.search.web.internal.portlet.action;

import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.content.render.list.CPContentListRendererRegistry;
import com.liferay.commerce.product.content.render.list.entry.CPContentListEntryRendererRegistry;
import com.liferay.commerce.product.content.search.web.internal.display.context.CPSearchResultsDisplayContext;
import com.liferay.commerce.product.display.context.helper.CPRequestHelper;
import com.liferay.commerce.product.type.CPTypeRegistry;
import com.liferay.commerce.product.util.CPDefinitionHelper;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchRequest;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchResponse;
import com.liferay.portlet.display.template.portlet.action.BaseConfigurationAction;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "jakarta.portlet.name=" + CPPortletKeys.CP_SEARCH_RESULTS,
	service = ConfigurationAction.class
)
public class CPSearchResultsConfigurationAction
	extends BaseConfigurationAction {

	@Override
	public String getJspPath(HttpServletRequest httpServletRequest) {
		CPRequestHelper cpRequestHelper = new CPRequestHelper(
			httpServletRequest);

		PortletSharedSearchResponse portletSharedSearchResponse =
			_portletSharedSearchRequest.search(
				cpRequestHelper.getRenderRequest());

		try {
			CPSearchResultsDisplayContext cpSearchResultsDisplayContext =
				new CPSearchResultsDisplayContext(
					_configurationProvider, _cpContentListEntryRendererRegistry,
					_cpContentListRendererRegistry, _cpDefinitionHelper,
					_cpTypeRegistry, httpServletRequest,
					portletSharedSearchResponse);

			httpServletRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT, cpSearchResultsDisplayContext);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return "/search_results/configuration.jsp";
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPSearchResultsConfigurationAction.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

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
	private PortletSharedSearchRequest _portletSharedSearchRequest;

}