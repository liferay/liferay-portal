/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.web.internal.portlet.action;

import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.content.render.list.CPContentListRendererRegistry;
import com.liferay.commerce.product.content.render.list.entry.CPContentListEntryRendererRegistry;
import com.liferay.commerce.product.content.web.internal.display.context.CPCompareContentMiniDisplayContext;
import com.liferay.commerce.product.type.CPTypeRegistry;
import com.liferay.commerce.product.util.CPCompareHelper;
import com.liferay.commerce.product.util.CPDefinitionHelper;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.display.template.portlet.action.BaseConfigurationAction;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "jakarta.portlet.name=" + CPPortletKeys.CP_COMPARE_CONTENT_MINI_WEB,
	service = ConfigurationAction.class
)
public class CPCompareContentMiniConfigurationAction
	extends BaseConfigurationAction {

	@Override
	public String getJspPath(HttpServletRequest httpServletRequest) {
		try {
			CPCompareContentMiniDisplayContext
				cpCompareContentMiniDisplayContext =
					new CPCompareContentMiniDisplayContext(
						_configurationProvider, _cpCompareHelper,
						_cpContentListEntryRendererRegistry,
						_cpContentListRendererRegistry, _cpDefinitionHelper,
						_cpTypeRegistry, _groupLocalService,
						httpServletRequest);

			httpServletRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT,
				cpCompareContentMiniDisplayContext);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return "/compare_products_mini/configuration.jsp";
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPCompareContentMiniConfigurationAction.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private CPCompareHelper _cpCompareHelper;

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

}