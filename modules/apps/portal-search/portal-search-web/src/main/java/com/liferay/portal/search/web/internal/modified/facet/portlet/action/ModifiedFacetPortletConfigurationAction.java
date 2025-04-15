/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.modified.facet.portlet.action;

import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.PropertiesParamUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.web.internal.modified.facet.constants.ModifiedFacetPortletKeys;
import com.liferay.portal.search.web.internal.modified.facet.display.context.builder.ModifiedFacetDisplayContextBuilder;
import com.liferay.portal.search.web.internal.util.DateRangeFactoryUtil;
import com.liferay.portlet.display.template.portlet.action.BaseConfigurationAction;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletConfig;
import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.text.ParseException;

import org.osgi.service.component.annotations.Component;

/**
 * @author Lino Alves
 */
@Component(
	property = "jakarta.portlet.name=" + ModifiedFacetPortletKeys.MODIFIED_FACET,
	service = ConfigurationAction.class
)
public class ModifiedFacetPortletConfigurationAction
	extends BaseConfigurationAction {

	@Override
	public String getJspPath(HttpServletRequest httpServletRequest) {
		return "/modified/facet/configuration.jsp";
	}

	@Override
	public void include(
			PortletConfig portletConfig, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		RenderRequest renderRequest =
			(RenderRequest)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST);

		ModifiedFacetDisplayContextBuilder modifiedFacetDisplayContextBuilder =
			_createModifiedFacetDisplayContextBuilder(renderRequest);

		httpServletRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT,
			modifiedFacetDisplayContextBuilder.build());

		super.include(portletConfig, httpServletRequest, httpServletResponse);
	}

	@Override
	public void processAction(
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws Exception {

		UnicodeProperties unicodeProperties = PropertiesParamUtil.getProperties(
			actionRequest, _PARAMETER_NAME_PREFIX);

		String ranges = unicodeProperties.getProperty("ranges");

		try {
			DateRangeFactoryUtil.validateRanges(ranges);
		}
		catch (JSONException | ParseException exception) {
			SessionErrors.add(actionRequest, "unparsableDate");

			_log.error(exception);
		}

		if (SessionErrors.isEmpty(actionRequest)) {
			super.processAction(portletConfig, actionRequest, actionResponse);
		}
	}

	private ModifiedFacetDisplayContextBuilder
		_createModifiedFacetDisplayContextBuilder(RenderRequest renderRequest) {

		try {
			ModifiedFacetDisplayContextBuilder
				modifiedFacetDisplayContextBuilder =
					new ModifiedFacetDisplayContextBuilder(renderRequest);

			ThemeDisplay themeDisplay =
				(ThemeDisplay)renderRequest.getAttribute(WebKeys.THEME_DISPLAY);

			modifiedFacetDisplayContextBuilder.setLocale(
				themeDisplay.getLocale());
			modifiedFacetDisplayContextBuilder.setTimeZone(
				themeDisplay.getTimeZone());

			return modifiedFacetDisplayContextBuilder;
		}
		catch (ConfigurationException configurationException) {
			throw new RuntimeException(configurationException);
		}
	}

	private static final String _PARAMETER_NAME_PREFIX = "preferences--";

	private static final Log _log = LogFactoryUtil.getLog(
		ModifiedFacetPortletConfigurationAction.class);

}