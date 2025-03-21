/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.util;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.LayoutTemplate;
import com.liferay.portal.kernel.servlet.PluginContextListener;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.template.Template;
import com.liferay.portal.kernel.template.TemplateManagerUtil;
import com.liferay.portal.kernel.template.TemplateResource;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.layoutconfiguration.util.RuntimePageUtil;
import com.liferay.taglib.util.DummyVelocityTaglib;
import com.liferay.taglib.util.VelocityTaglib;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Shuyang Zhou
 */
public class CustomizationSettingsUtil {

	public static void processCustomizationSettings(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse,
			TemplateResource templateResource, String langType)
		throws Exception {

		ClassLoader pluginClassLoader = null;

		LayoutTemplate layoutTemplate = RuntimePageUtil.getLayoutTemplate(
			templateResource.getTemplateId());

		if (layoutTemplate != null) {
			String pluginServletContextName = GetterUtil.getString(
				layoutTemplate.getServletContextName());

			ServletContext pluginServletContext = ServletContextPool.get(
				pluginServletContextName);

			if (pluginServletContext != null) {
				pluginClassLoader =
					(ClassLoader)pluginServletContext.getAttribute(
						PluginContextListener.PLUGIN_CLASS_LOADER);
			}
		}

		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				pluginClassLoader)) {

			_processCustomizationSettings(
				httpServletRequest, httpServletResponse, templateResource,
				langType);
		}
	}

	private static void _processCustomizationSettings(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse,
			TemplateResource templateResource, String langType)
		throws Exception {

		CustomizationSettingsProcessor processor =
			new CustomizationSettingsProcessor(
				httpServletRequest, httpServletResponse);

		Template template = TemplateManagerUtil.getTemplate(
			langType, templateResource, false);

		template.put("processor", processor);

		// Velocity variables

		template.prepare(httpServletRequest);

		// liferay:include tag library

		VelocityTaglib velocityTaglib = new DummyVelocityTaglib();

		template.put("taglibLiferay", velocityTaglib);
		template.put("theme", velocityTaglib);

		try {
			template.processTemplate(httpServletResponse.getWriter());
		}
		catch (Exception exception) {
			_log.error(exception);

			throw exception;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CustomizationSettingsUtil.class);

}