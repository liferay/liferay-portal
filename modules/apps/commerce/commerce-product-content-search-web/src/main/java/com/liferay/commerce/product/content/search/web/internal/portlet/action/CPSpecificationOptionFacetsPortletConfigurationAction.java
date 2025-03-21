/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.search.web.internal.portlet.action;

import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.content.search.web.internal.display.context.CPSpecificationOptionFacetsDisplayContext;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropertiesParamUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.display.template.portlet.action.BaseConfigurationAction;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletConfig;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Crescenzo Rega
 */
@Component(
	property = "jakarta.portlet.name=" + CPPortletKeys.CP_SPECIFICATION_OPTION_FACETS,
	service = ConfigurationAction.class
)
public class CPSpecificationOptionFacetsPortletConfigurationAction
	extends BaseConfigurationAction {

	@Override
	public String getJspPath(HttpServletRequest httpServletRequest) {
		try {
			httpServletRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT,
				new CPSpecificationOptionFacetsDisplayContext(
					_configurationProvider, _groupLocalService,
					httpServletRequest));
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return "/specification_option_facets/configuration.jsp";
	}

	@Override
	public void processAction(
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws Exception {

		UnicodeProperties unicodeProperties = PropertiesParamUtil.getProperties(
			actionRequest, "preferences--");

		String maxSpecifications = unicodeProperties.getProperty(
			"maxSpecifications");

		if (Validator.isNumber(maxSpecifications) &&
			(GetterUtil.getInteger(maxSpecifications) > _MAX_SIZE_LIMIT)) {

			SessionErrors.add(actionRequest, "exceededMaxSpecificationsLimit");
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

	private static final int _MAX_SIZE_LIMIT = 100;

	private static final Log _log = LogFactoryUtil.getLog(
		CPSpecificationOptionFacetsPortletConfigurationAction.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private GroupLocalService _groupLocalService;

}