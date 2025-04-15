/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.search.web.internal.portlet.action;

import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.content.search.web.internal.display.context.CPOptionsSearchFacetDisplayContext;
import com.liferay.commerce.product.content.search.web.internal.display.context.builder.CPOptionsSearchFacetDisplayContextBuilder;
import com.liferay.commerce.product.display.context.helper.CPRequestHelper;
import com.liferay.commerce.product.service.CPOptionLocalService;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropertiesParamUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchRequest;
import com.liferay.portlet.display.template.portlet.action.BaseConfigurationAction;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletConfig;
import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "jakarta.portlet.name=" + CPPortletKeys.CP_OPTION_FACETS,
	service = ConfigurationAction.class
)
public class CPOptionFacetsPortletConfigurationAction
	extends BaseConfigurationAction {

	@Override
	public String getJspPath(HttpServletRequest httpServletRequest) {
		CPRequestHelper cpRequestHelper = new CPRequestHelper(
			httpServletRequest);

		try {
			CPOptionsSearchFacetDisplayContext
				cpOptionsSearchFacetDisplayContext =
					_buildCPOptionsSearchFacetDisplayContext(
						cpRequestHelper.getRenderRequest());

			httpServletRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT,
				cpOptionsSearchFacetDisplayContext);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return "/option_facets/configuration.jsp";
	}

	@Override
	public void processAction(
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws Exception {

		UnicodeProperties unicodeProperties = PropertiesParamUtil.getProperties(
			actionRequest, "preferences--");

		String maxOptions = unicodeProperties.getProperty("maxOptions");

		if (Validator.isNumber(maxOptions) &&
			(GetterUtil.getInteger(maxOptions) > _MAX_SIZE_LIMIT)) {

			SessionErrors.add(actionRequest, "exceededMaxOptionsLimit");
		}

		String maxTerms = unicodeProperties.getProperty("maxTerms");

		if (Validator.isNumber(maxTerms) &&
			(GetterUtil.getInteger(maxTerms) > _MAX_SIZE_LIMIT)) {

			SessionErrors.add(actionRequest, "exceededMaxTermsLimit");
		}

		if (SessionErrors.isEmpty(actionRequest)) {
			super.processAction(portletConfig, actionRequest, actionResponse);
		}
	}

	private CPOptionsSearchFacetDisplayContext
		_buildCPOptionsSearchFacetDisplayContext(RenderRequest renderRequest) {

		CPOptionsSearchFacetDisplayContextBuilder
			cpOptionsSearchFacetDisplayContextBuilder =
				new CPOptionsSearchFacetDisplayContextBuilder(renderRequest);

		cpOptionsSearchFacetDisplayContextBuilder.configurationProvider(
			_configurationProvider);
		cpOptionsSearchFacetDisplayContextBuilder.cpOptionLocalService(
			_cpOptionLocalService);
		cpOptionsSearchFacetDisplayContextBuilder.groupLocalService(
			_groupLocalService);
		cpOptionsSearchFacetDisplayContextBuilder.portal(_portal);
		cpOptionsSearchFacetDisplayContextBuilder.portletSharedSearchRequest(
			_portletSharedSearchRequest);

		return cpOptionsSearchFacetDisplayContextBuilder.build();
	}

	private static final int _MAX_SIZE_LIMIT = 100;

	private static final Log _log = LogFactoryUtil.getLog(
		CPOptionFacetsPortletConfigurationAction.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private CPOptionLocalService _cpOptionLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private PortletSharedSearchRequest _portletSharedSearchRequest;

}