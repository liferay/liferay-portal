/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.fragment.renderer;

import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.document.library.configuration.DLConfiguration;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.cms.site.initializer.internal.display.context.ViewDashboardDisplayContext;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adriano Interaminense
 */
@Component(
	configurationPid = "com.liferay.document.library.configuration.DLConfiguration",
	service = FragmentRenderer.class
)
public class ViewDashboardJSPSectionFragmentRenderer
	extends BaseJSPSectionFragmentRenderer<ViewDashboardDisplayContext> {

	@Override
	public String getLabelKey() {
		return "dashboard";
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_dlConfiguration = ConfigurableUtil.createConfigurable(
			DLConfiguration.class, properties);
	}

	@Override
	protected ViewDashboardDisplayContext getDisplayContext(
		HttpServletRequest httpServletRequest) {

		return new ViewDashboardDisplayContext(
			_analyticsSettingsManager, _dlConfiguration, groupLocalService,
			httpServletRequest, _objectDefinitionService, _roleLocalService,
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY));
	}

	@Override
	protected String getJSPPath() {
		return "/view_dashboard.jsp";
	}

	@Reference
	private AnalyticsSettingsManager _analyticsSettingsManager;

	private volatile DLConfiguration _dlConfiguration;

	@Reference
	private ObjectDefinitionService _objectDefinitionService;

	@Reference
	private RoleLocalService _roleLocalService;

}