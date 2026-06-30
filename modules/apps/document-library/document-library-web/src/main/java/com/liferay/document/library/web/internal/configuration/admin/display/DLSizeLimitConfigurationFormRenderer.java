/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.configuration.admin.display.ConfigurationFormRenderer;
import com.liferay.document.library.configuration.DLSizeLimitConfigurationProvider;
import com.liferay.document.library.web.internal.display.context.DLSizeLimitConfigurationDisplayContext;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.LinkedHashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(service = ConfigurationFormRenderer.class)
public class DLSizeLimitConfigurationFormRenderer
	implements ConfigurationFormRenderer {

	@Override
	public String getPid() {
		return "com.liferay.document.library.internal.configuration." +
			"DLSizeLimitConfiguration";
	}

	@Override
	public Map<String, Object> getRequestParameters(
		HttpServletRequest httpServletRequest) {

		return HashMapBuilder.<String, Object>put(
			"fileMaxSize", ParamUtil.getLong(httpServletRequest, "fileMaxSize")
		).put(
			"maxSizeToCopy",
			ParamUtil.getLong(httpServletRequest, "maxSizeToCopy")
		).put(
			"mimeTypeSizeLimit", _getMimeTypeSizeLimit(httpServletRequest)
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
			throw new IOException("Unable to render size_limit.jsp", exception);
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

		if (ConfigurationAdminPortletKeys.INSTANCE_SETTINGS.equals(portletId)) {
			return new ConfigurationScope(
				ExtendedObjectClassDefinition.Scope.COMPANY,
				themeDisplay.getCompanyId());
		}

		if (ConfigurationAdminPortletKeys.SITE_SETTINGS.equals(portletId)) {
			return new ConfigurationScope(
				ExtendedObjectClassDefinition.Scope.GROUP,
				themeDisplay.getScopeGroupId());
		}

		return new ConfigurationScope(
			ExtendedObjectClassDefinition.Scope.SYSTEM, 0);
	}

	private String[] _getMimeTypeSizeLimit(
		HttpServletRequest httpServletRequest) {

		Map<String, Long> mimeTypeSizeLimits = new LinkedHashMap<>();

		Map<String, String[]> parameterMap =
			httpServletRequest.getParameterMap();

		for (int i = 0; parameterMap.containsKey("mimeType_" + i); i++) {
			String mimeType = null;

			String[] mimeTypes = parameterMap.get("mimeType_" + i);

			if ((mimeTypes.length != 0) && Validator.isNotNull(mimeTypes[0])) {
				mimeType = mimeTypes[0];
			}

			Long size = null;

			String[] sizes = parameterMap.get("size_" + i);

			if ((sizes != null) && (sizes.length != 0) &&
				Validator.isNotNull(sizes[0])) {

				size = GetterUtil.getLong(sizes[0]);
			}

			if ((mimeType != null) || (size != null)) {
				mimeTypeSizeLimits.put(mimeType, size);
			}
		}

		if (mimeTypeSizeLimits.isEmpty()) {
			return new String[0];
		}

		String[] mimeTypeSizeLimitArray = new String[mimeTypeSizeLimits.size()];

		int i = 0;

		for (Map.Entry<String, Long> entry : mimeTypeSizeLimits.entrySet()) {
			mimeTypeSizeLimitArray[i] =
				entry.getKey() + StringPool.COLON + entry.getValue();

			i++;
		}

		return mimeTypeSizeLimitArray;
	}

	private void _render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		ConfigurationScope configurationScope = _getConfigurationScope(
			httpServletRequest);

		httpServletRequest.setAttribute(
			DLSizeLimitConfigurationDisplayContext.class.getName(),
			new DLSizeLimitConfigurationDisplayContext(
				_dlSizeLimitConfigurationProvider, httpServletRequest,
				PortalUtil.getLiferayPortletResponse(
					(PortletResponse)httpServletRequest.getAttribute(
						JavaConstants.JAKARTA_PORTLET_RESPONSE)),
				configurationScope.getScope(
				).getValue(),
				configurationScope.getScopePK()));

		RequestDispatcher requestDispatcher =
			_servletContext.getRequestDispatcher(
				"/document_library_settings/size_limit.jsp");

		requestDispatcher.include(httpServletRequest, httpServletResponse);
	}

	@Reference
	private DLSizeLimitConfigurationProvider _dlSizeLimitConfigurationProvider;

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