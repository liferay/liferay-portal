/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.configuration.admin.display.ConfigurationFormRenderer;
import com.liferay.document.library.configuration.DLFileEntryConfigurationProvider;
import com.liferay.document.library.web.internal.display.context.DLFileEntryConfigurationDisplayContext;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(service = ConfigurationFormRenderer.class)
public class DLFileEntryConfigurationFormRenderer
	implements ConfigurationFormRenderer {

	@Override
	public String getPid() {
		return "com.liferay.document.library.configuration." +
			"DLFileEntryConfiguration";
	}

	@Override
	public Map<String, Object> getRequestParameters(
		HttpServletRequest httpServletRequest) {

		return HashMapBuilder.<String, Object>put(
			"maxNumberOfPages",
			ParamUtil.getInteger(httpServletRequest, "maxNumberOfPages")
		).put(
			"previewableProcessorMaxSize",
			ParamUtil.getLong(httpServletRequest, "previewableProcessorMaxSize")
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
				"Unable to render dl_file_entry_configuration.jsp", exception);
		}
	}

	private ConfigurationScope _getConfigurationScope(
		HttpServletRequest httpServletRequest) {

		String portletId = PortalUtil.getPortletId(
			(PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_REQUEST));
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (Objects.equals(
				ConfigurationAdminPortletKeys.INSTANCE_SETTINGS, portletId)) {

			return new ConfigurationScope(
				ExtendedObjectClassDefinition.Scope.COMPANY,
				themeDisplay.getCompanyId());
		}

		if (Objects.equals(
				ConfigurationAdminPortletKeys.SITE_SETTINGS, portletId)) {

			return new ConfigurationScope(
				ExtendedObjectClassDefinition.Scope.GROUP,
				themeDisplay.getScopeGroupId());
		}

		return new ConfigurationScope(
			ExtendedObjectClassDefinition.Scope.SYSTEM, 0);
	}

	private void _render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		ConfigurationScope configurationScope = _getConfigurationScope(
			httpServletRequest);

		httpServletRequest.setAttribute(
			DLFileEntryConfigurationDisplayContext.class.getName(),
			new DLFileEntryConfigurationDisplayContext(
				_dlFileEntryConfigurationProvider, httpServletRequest,
				configurationScope.getScope(),
				configurationScope.getScopePK()));

		RequestDispatcher requestDispatcher =
			_servletContext.getRequestDispatcher(
				"/document_library_settings/dl_file_entry_configuration.jsp");

		requestDispatcher.include(httpServletRequest, httpServletResponse);
	}

	@Reference
	private DLFileEntryConfigurationProvider _dlFileEntryConfigurationProvider;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.document.library.web)"
	)
	private ServletContext _servletContext;

	private static class ConfigurationScope {

		public ExtendedObjectClassDefinition.Scope getScope() {
			return _scope;
		}

		public long getScopePK() {
			return _scopePK;
		}

		private ConfigurationScope(
			ExtendedObjectClassDefinition.Scope scope, long scopePK) {

			_scope = scope;
			_scopePK = scopePK;
		}

		private final ExtendedObjectClassDefinition.Scope _scope;
		private final long _scopePK;

	}

}