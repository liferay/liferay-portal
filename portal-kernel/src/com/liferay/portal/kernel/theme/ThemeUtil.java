/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.theme;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.servlet.PipingServletResponse;
import com.liferay.portal.kernel.servlet.PluginContextListener;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.template.Template;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateContextContributor;
import com.liferay.portal.kernel.template.TemplateManagerUtil;
import com.liferay.portal.kernel.template.TemplateResourceLoaderUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.Writer;

/**
 * @author Brian Wing Shun Chan
 * @author Brian Myunghun Kim
 * @author Raymond Augé
 * @author Mika Koivisto
 * @author Shuyang Zhou
 */
public class ThemeUtil {

	public static String getPortletId(HttpServletRequest httpServletRequest) {
		String portletId = null;

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (themeDisplay != null) {
			PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

			portletId = portletDisplay.getId();
		}

		return portletId;
	}

	public static void include(
			ServletContext servletContext,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String path, Theme theme)
		throws Exception {

		include(
			servletContext, httpServletRequest, httpServletResponse, path,
			theme, true);
	}

	public static String include(
			ServletContext servletContext,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String path, Theme theme,
			boolean write)
		throws Exception {

		String pluginServletContextName = GetterUtil.getString(
			theme.getServletContextName());

		ServletContext pluginServletContext = ServletContextPool.get(
			pluginServletContextName);

		ClassLoader pluginClassLoader = null;

		if (pluginServletContext != null) {
			pluginClassLoader = (ClassLoader)pluginServletContext.getAttribute(
				PluginContextListener.PLUGIN_CLASS_LOADER);
		}

		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				pluginClassLoader)) {

			return doIncludeFTL(
				servletContext, httpServletRequest, httpServletResponse, path,
				theme, false, write);
		}
	}

	protected static String doIncludeFTL(
			ServletContext servletContext,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String path, Theme theme,
			boolean restricted, boolean write)
		throws Exception {

		// The servlet context name will be null when the theme is deployed to
		// the root directory in Tomcat. See
		// com.liferay.portal.servlet.MainServlet and
		// com.liferay.portlet.PortletContextImpl for other cases where a null
		// servlet context name is also converted to an empty string.

		String servletContextName = GetterUtil.getString(
			theme.getServletContextName());

		if (ServletContextPool.get(servletContextName) == null) {

			// This should only happen if the FreeMarker template is the first
			// page to be accessed in the system

			ServletContextPool.put(servletContextName, servletContext);
		}

		String portletId = getPortletId(httpServletRequest);

		String resourcePath = theme.getResourcePath(
			servletContext, portletId, path);

		if (Validator.isNotNull(portletId) &&
			PortletIdCodec.hasInstanceId(portletId) &&
			!TemplateResourceLoaderUtil.hasTemplateResource(
				TemplateConstants.LANG_TYPE_FTL, resourcePath)) {

			String rootPortletId = PortletIdCodec.decodePortletName(portletId);

			resourcePath = theme.getResourcePath(
				servletContext, rootPortletId, path);
		}

		if (Validator.isNotNull(portletId) &&
			!TemplateResourceLoaderUtil.hasTemplateResource(
				TemplateConstants.LANG_TYPE_FTL, resourcePath)) {

			resourcePath = theme.getResourcePath(servletContext, null, path);
		}

		if (!TemplateResourceLoaderUtil.hasTemplateResource(
				TemplateConstants.LANG_TYPE_FTL, resourcePath)) {

			_log.error(resourcePath + " does not exist");

			return null;
		}

		Template template = TemplateManagerUtil.getTemplate(
			TemplateConstants.LANG_TYPE_FTL,
			TemplateResourceLoaderUtil.getTemplateResource(
				TemplateConstants.LANG_TYPE_FTL, resourcePath),
			restricted);

		// FreeMarker variables

		template.prepare(httpServletRequest);

		// Custom theme variables

		for (TemplateContextContributor templateContextContributor :
				_templateContextContributors) {

			templateContextContributor.prepare(template, httpServletRequest);
		}

		// Theme servlet context

		ServletContext themeServletContext = ServletContextPool.get(
			servletContextName);

		template.put("themeServletContext", themeServletContext);

		Writer writer = null;

		if (write) {
			writer = httpServletResponse.getWriter();
		}
		else {
			writer = new UnsyncStringWriter();

			httpServletResponse = new PipingServletResponse(
				httpServletResponse, writer);
		}

		template.prepareTaglib(httpServletRequest, httpServletResponse);

		template.put(TemplateConstants.WRITER, writer);

		// Merge templates

		template.processTemplate(writer);

		if (write) {
			return null;
		}

		return writer.toString();
	}

	private static final Log _log = LogFactoryUtil.getLog(ThemeUtil.class);

	private static final ServiceTrackerList<TemplateContextContributor>
		_templateContextContributors = ServiceTrackerListFactory.open(
			SystemBundleUtil.getBundleContext(),
			TemplateContextContributor.class,
			"(type=" + TemplateContextContributor.TYPE_THEME + ")");

}