/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.configuration.web.internal.portlet.action;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PublicRenderParameter;
import com.liferay.portal.kernel.portlet.PortletConfigurationLayoutUtil;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletQNameUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.configuration.kernel.util.PortletConfigurationUtil;
import com.liferay.portlet.portletconfiguration.util.ConfigurationActionRequest;
import com.liferay.portlet.portletconfiguration.util.ConfigurationPortletRequest;
import com.liferay.portlet.portletconfiguration.util.ConfigurationRenderRequest;
import com.liferay.portlet.portletconfiguration.util.ConfigurationResourceRequest;
import com.liferay.portlet.portletconfiguration.util.PublicRenderParameterConfiguration;
import com.liferay.portlet.portletconfiguration.util.PublicRenderParameterIdentifierComparator;
import com.liferay.portlet.portletconfiguration.util.PublicRenderParameterIdentifierConfigurationComparator;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.ResourceRequest;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Jorge Ferrer
 * @author Raymond Augé
 */
public class ActionUtil {

	public static final String ACTION = "_ACTION_";

	public static final String PRESELECTED = "_PRESELECTED_";

	public static PortletPreferences getLayoutPortletSetup(
		PortletRequest portletRequest, Portlet portlet) {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return PortletPreferencesFactoryUtil.getLayoutPortletSetup(
			themeDisplay.getLayout(), portlet.getPortletId());
	}

	public static void getLayoutPublicRenderParameters(
		PortletRequest portletRequest) {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Set<String> identifiers = new HashSet<>();

		Set<PublicRenderParameter> publicRenderParameters = new TreeSet<>(
			new PublicRenderParameterIdentifierComparator());

		LayoutTypePortlet layoutTypePortlet =
			themeDisplay.getLayoutTypePortlet();

		for (Portlet portlet : layoutTypePortlet.getAllPortlets()) {
			for (PublicRenderParameter publicRenderParameter :
					portlet.getPublicRenderParameters()) {

				if (!identifiers.contains(
						publicRenderParameter.getIdentifier())) {

					identifiers.add(publicRenderParameter.getIdentifier());

					publicRenderParameters.add(publicRenderParameter);
				}
			}
		}

		portletRequest.setAttribute(
			WebKeys.PUBLIC_RENDER_PARAMETERS, publicRenderParameters);
	}

	public static Portlet getPortlet(PortletRequest portletRequest)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		String portletId = ParamUtil.getString(
			portletRequest, "portletResource");

		if (!PortletPermissionUtil.contains(
				permissionChecker, themeDisplay.getScopeGroupId(),
				PortletConfigurationLayoutUtil.getLayout(themeDisplay),
				portletId, ActionKeys.CONFIGURATION)) {

			throw new PrincipalException.MustHavePermission(
				permissionChecker, Portlet.class.getName(), portletId,
				ActionKeys.CONFIGURATION);
		}

		return PortletLocalServiceUtil.getPortletById(
			themeDisplay.getCompanyId(), portletId);
	}

	public static void getPublicRenderParameterConfigurationList(
		PortletRequest portletRequest, Portlet portlet) {

		PortletPreferences portletPreferences = null;

		if (portletRequest instanceof ConfigurationPortletRequest) {
			portletPreferences = portletRequest.getPreferences();
		}
		else {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)portletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			portletPreferences =
				PortletPreferencesFactoryUtil.getLayoutPortletSetup(
					themeDisplay.getLayout(), portlet.getPortletId());
		}

		List<PublicRenderParameterConfiguration>
			publicRenderParameterConfigurations = new ArrayList<>();

		for (PublicRenderParameter publicRenderParameter :
				portlet.getPublicRenderParameters()) {

			String publicRenderParameterName =
				PortletQNameUtil.getPublicRenderParameterName(
					publicRenderParameter.getQName());

			String mappingKey =
				PublicRenderParameterConfiguration.getMappingKey(
					publicRenderParameterName);
			String ignoreKey = PublicRenderParameterConfiguration.getIgnoreKey(
				publicRenderParameterName);

			String mappingValue = null;
			boolean ignoreValue = false;

			if (SessionErrors.isEmpty(portletRequest)) {
				mappingValue = portletPreferences.getValue(mappingKey, null);
				ignoreValue = GetterUtil.getBoolean(
					portletPreferences.getValue(ignoreKey, null));
			}
			else {
				mappingValue = ParamUtil.getString(portletRequest, mappingKey);
				ignoreValue = GetterUtil.getBoolean(
					ParamUtil.getString(portletRequest, ignoreKey));
			}

			publicRenderParameterConfigurations.add(
				new PublicRenderParameterConfiguration(
					publicRenderParameter, mappingValue, ignoreValue));
		}

		Collections.sort(
			publicRenderParameterConfigurations,
			new PublicRenderParameterIdentifierConfigurationComparator());

		portletRequest.setAttribute(
			WebKeys.PUBLIC_RENDER_PARAMETER_CONFIGURATIONS,
			publicRenderParameterConfigurations);
	}

	public static String getTitle(Portlet portlet, RenderRequest renderRequest)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PortletPreferences portletPreferences = null;

		if (renderRequest instanceof ConfigurationPortletRequest) {
			portletPreferences = renderRequest.getPreferences();
		}
		else {
			portletPreferences = getLayoutPortletSetup(renderRequest, portlet);

			portletPreferences = getPortletSetup(
				PortalUtil.getHttpServletRequest(renderRequest),
				renderRequest.getPreferences(), portletPreferences);
		}

		String title = PortletConfigurationUtil.getPortletTitle(
			portlet.getPortletId(), portletPreferences,
			themeDisplay.getLanguageId());

		if (Validator.isNull(title)) {
			ServletContext servletContext =
				(ServletContext)renderRequest.getAttribute(WebKeys.CTX);

			title = PortalUtil.getPortletTitle(
				portlet, servletContext, themeDisplay.getLocale());
		}

		return title;
	}

	public static ActionRequest getWrappedActionRequest(
			ActionRequest actionRequest, PortletPreferences portletPreferences)
		throws PortalException {

		portletPreferences = getPortletPreferences(
			PortalUtil.getHttpServletRequest(actionRequest),
			actionRequest.getPreferences(), portletPreferences);

		return new ConfigurationActionRequest(
			actionRequest, portletPreferences);
	}

	public static RenderRequest getWrappedRenderRequest(
			RenderRequest renderRequest, PortletPreferences portletPreferences)
		throws PortalException {

		HttpServletRequest httpServletRequest =
			PortalUtil.getHttpServletRequest(renderRequest);

		portletPreferences = getPortletPreferences(
			httpServletRequest, renderRequest.getPreferences(),
			portletPreferences);

		renderRequest = new ConfigurationRenderRequest(
			renderRequest, portletPreferences);

		httpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_REQUEST, renderRequest);

		return renderRequest;
	}

	public static ResourceRequest getWrappedResourceRequest(
			ResourceRequest resourceRequest,
			PortletPreferences portletPreferences)
		throws PortalException {

		portletPreferences = getPortletPreferences(
			PortalUtil.getHttpServletRequest(resourceRequest),
			resourceRequest.getPreferences(), portletPreferences);

		return new ConfigurationResourceRequest(
			resourceRequest, portletPreferences);
	}

	protected static PortletPreferences getPortletPreferences(
			HttpServletRequest httpServletRequest,
			PortletPreferences portletConfigPortletPreferences,
			PortletPreferences portletPreferences)
		throws PortalException {

		String portletResource = ParamUtil.getString(
			httpServletRequest, "portletResource");

		if (Validator.isNull(portletResource)) {
			return portletConfigPortletPreferences;
		}

		if (portletPreferences != null) {
			return portletPreferences;
		}

		return PortletPreferencesFactoryUtil.getPortletPreferences(
			httpServletRequest, portletResource);
	}

	protected static PortletPreferences getPortletSetup(
			HttpServletRequest httpServletRequest,
			PortletPreferences portletConfigPortletPreferences,
			PortletPreferences portletPreferences)
		throws PortalException {

		String portletResource = ParamUtil.getString(
			httpServletRequest, "portletResource");

		if (Validator.isNull(portletResource)) {
			return portletConfigPortletPreferences;
		}

		if (portletPreferences != null) {
			return portletPreferences;
		}

		return PortletPreferencesFactoryUtil.getPortletSetup(
			httpServletRequest, portletResource);
	}

}