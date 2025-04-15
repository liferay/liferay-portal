/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.layoutconfiguration.util;

import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.LayoutTemplate;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.service.LayoutTemplateLocalServiceUtil;
import com.liferay.portal.kernel.servlet.PipingServletResponse;
import com.liferay.portal.kernel.servlet.PluginContextListener;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.template.StringTemplateResource;
import com.liferay.portal.kernel.template.Template;
import com.liferay.portal.kernel.template.TemplateManager;
import com.liferay.portal.kernel.template.TemplateManagerUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.layoutconfiguration.util.velocity.TemplateProcessor;
import com.liferay.portlet.internal.PortletBagUtil;
import com.liferay.portlet.internal.PortletTypeUtil;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.StopWatch;

/**
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 * @author Shuyang Zhou
 */
public class RuntimePageUtil {

	public static LayoutTemplate getLayoutTemplate(String templateId) {
		LayoutTemplateLocator layoutTemplateLocator = new LayoutTemplateLocator(
			templateId);

		return LayoutTemplateLocalServiceUtil.getLayoutTemplate(
			layoutTemplateLocator.getLayoutTemplateId(),
			layoutTemplateLocator.isStandard(),
			layoutTemplateLocator.getThemeId());
	}

	public static StringBundler getProcessedTemplate(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String portletId,
			String templateId, String content)
		throws Exception {

		return doDispatch(
			httpServletRequest, httpServletResponse, portletId, templateId,
			content, null);
	}

	public static void processTemplate(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String portletId,
			String templateId, String content)
		throws Exception {

		StringBundler sb = doDispatch(
			httpServletRequest, httpServletResponse, portletId, templateId,
			content, null);

		sb.writeTo(httpServletResponse.getWriter());
	}

	public static void processTemplate(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String portletId,
			String templateId, String content, String langType)
		throws Exception {

		StringBundler sb = doDispatch(
			httpServletRequest, httpServletResponse, portletId, templateId,
			content, langType);

		sb.writeTo(httpServletResponse.getWriter());
	}

	protected static StringBundler doDispatch(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String portletId,
			String templateId, String content, String langType)
		throws Exception {

		ClassLoader pluginClassLoader = null;

		LayoutTemplateLocator layoutTemplateLocator = new LayoutTemplateLocator(
			templateId);

		if (langType == null) {
			langType = LayoutTemplateLocalServiceUtil.getLangType(
				layoutTemplateLocator.getLayoutTemplateId(),
				layoutTemplateLocator.isStandard(),
				layoutTemplateLocator.getThemeId());
		}

		LayoutTemplate layoutTemplate =
			LayoutTemplateLocalServiceUtil.getLayoutTemplate(
				layoutTemplateLocator.getLayoutTemplateId(),
				layoutTemplateLocator.isStandard(),
				layoutTemplateLocator.getThemeId());

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

			return doProcessTemplate(
				httpServletRequest, httpServletResponse, portletId, templateId,
				content, langType, false);
		}
	}

	protected static StringBundler doProcessTemplate(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String portletId,
			String templateId, String content, String langType,
			boolean restricted)
		throws Exception {

		TemplateProcessor processor = new TemplateProcessor(
			httpServletRequest, httpServletResponse, portletId);

		TemplateManager templateManager =
			TemplateManagerUtil.getTemplateManager(langType);

		Template template = templateManager.getTemplate(
			new StringTemplateResource(templateId, content), restricted);

		template.put("processor", processor);

		// Velocity variables

		template.prepare(httpServletRequest);

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		template.prepareTaglib(
			httpServletRequest,
			new PipingServletResponse(httpServletResponse, unsyncStringWriter));

		try {
			template.processTemplate(unsyncStringWriter);
		}
		catch (Exception exception) {
			_log.error(exception);

			throw exception;
		}

		Map<Integer, List<PortletRenderer>> portletRenderersMap =
			processor.getPortletRenderers();

		Map<String, Map<String, Object>> portletHeaderRequestMap =
			new HashMap<>();

		for (Map.Entry<Integer, List<PortletRenderer>> entry :
				portletRenderersMap.entrySet()) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Processing portlets with render weight " + entry.getKey());
			}

			List<PortletRenderer> portletRenderers = entry.getValue();

			StopWatch stopWatch = new StopWatch();

			stopWatch.start();

			if (_log.isDebugEnabled()) {
				_log.debug("Start serial header phase");
			}

			for (PortletRenderer portletRenderer : portletRenderers) {
				Portlet portletModel = portletRenderer.getPortlet();

				if (!portletModel.isReady()) {
					continue;
				}

				jakarta.portlet.Portlet portlet =
					PortletBagUtil.getPortletInstance(
						httpServletRequest.getServletContext(), portletModel,
						portletModel.getRootPortletId());

				if (!PortletTypeUtil.isHeaderPortlet(portlet)) {
					continue;
				}

				Map<String, Object> headerRequestMap =
					portletRenderer.renderHeaders(
						httpServletRequest, httpServletResponse,
						portletModel.getHeaderRequestAttributePrefixes());

				String rendererPortletId = portletModel.getPortletId();

				portletHeaderRequestMap.put(
					rendererPortletId, headerRequestMap);

				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"Serially rendered headers for portlet ",
							rendererPortletId, " in ", stopWatch.getTime(),
							" ms"));
				}
			}

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Finished serial header phase in " + stopWatch.getTime() +
						" ms");
			}
		}

		Map<String, StringBundler> contentsMap = new HashMap<>();

		for (Map.Entry<Integer, List<PortletRenderer>> entry :
				portletRenderersMap.entrySet()) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Processing portlets with render weight " + entry.getKey());
			}

			List<PortletRenderer> portletRenderers = entry.getValue();

			StopWatch stopWatch = new StopWatch();

			stopWatch.start();

			if (_log.isDebugEnabled()) {
				_log.debug("Start serial rendering");
			}

			for (PortletRenderer portletRenderer : portletRenderers) {
				Portlet portlet = portletRenderer.getPortlet();

				String rendererPortletId = portlet.getPortletId();

				contentsMap.put(
					rendererPortletId,
					portletRenderer.render(
						httpServletRequest, httpServletResponse,
						portletHeaderRequestMap.get(rendererPortletId)));

				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"Serially rendered portlet ", rendererPortletId,
							" in ", stopWatch.getTime(), " ms"));
				}
			}

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Finished serial rendering in " + stopWatch.getTime() +
						" ms");
			}
		}

		return StringUtil.replaceWithStringBundler(
			unsyncStringWriter.toString(), "[$TEMPLATE_PORTLET_", "$]",
			contentsMap);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RuntimePageUtil.class);

}