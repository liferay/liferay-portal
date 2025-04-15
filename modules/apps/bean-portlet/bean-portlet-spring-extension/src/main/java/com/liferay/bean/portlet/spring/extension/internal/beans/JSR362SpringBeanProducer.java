/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.spring.extension.internal.beans;

import com.liferay.bean.portlet.spring.extension.internal.scope.SpringScopedBeanManager;
import com.liferay.bean.portlet.spring.extension.internal.scope.SpringScopedBeanManagerThreadLocal;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import jakarta.portlet.ActionParameters;
import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.EventRequest;
import jakarta.portlet.EventResponse;
import jakarta.portlet.HeaderRequest;
import jakarta.portlet.HeaderResponse;
import jakarta.portlet.MutableRenderParameters;
import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletContext;
import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletSession;
import jakarta.portlet.RenderParameters;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceParameters;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;
import jakarta.portlet.StateAwareResponse;
import jakarta.portlet.WindowState;
import jakarta.portlet.annotations.ContextPath;
import jakarta.portlet.annotations.Namespace;
import jakarta.portlet.annotations.PortletName;
import jakarta.portlet.annotations.WindowId;

import jakarta.servlet.http.Cookie;

import java.lang.annotation.Annotation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

/**
 * @author Neil Griffin
 */
@Configuration
public class JSR362SpringBeanProducer {

	@Bean(name = "actionParams")
	@Scope(proxyMode = ScopedProxyMode.INTERFACES, value = "portletRequest")
	public ActionParameters getActionParameters() {
		ActionRequest actionRequest = _getActionRequest();

		if (actionRequest == null) {
			return null;
		}

		return actionRequest.getActionParameters();
	}

	@Bean(name = "actionRequest")
	@Scope(proxyMode = ScopedProxyMode.INTERFACES, value = "portletRequest")
	public ActionRequest getActionRequest() {
		return _getActionRequest();
	}

	@Bean(name = "actionResponse")
	@Scope(proxyMode = ScopedProxyMode.INTERFACES, value = "portletRequest")
	public ActionResponse getActionResponse() {
		PortletResponse portletResponse = _getPortletResponse();

		if (portletResponse instanceof ActionResponse) {
			return (ActionResponse)portletResponse;
		}

		return null;
	}

	@Bean(name = "contextPath")
	@ContextPath
	@Scope(
		proxyMode = ScopedProxyMode.NO,
		value = ConfigurableBeanFactory.SCOPE_PROTOTYPE
	)
	public String getContextPath() {
		PortletRequest portletRequest = _getPortletRequest();

		if (portletRequest == null) {
			_log.error(
				new IllegalStateException(
					_getDependentStringErrorMessage(ContextPath.class)));

			return null;
		}

		return portletRequest.getContextPath();
	}

	@Bean(name = "cookies")
	@Scope(proxyMode = ScopedProxyMode.INTERFACES, value = "portletRequest")
	public List<Cookie> getCookies() {
		PortletRequest portletRequest = _getPortletRequest();

		if (portletRequest == null) {
			return null;
		}

		Cookie[] cookies = portletRequest.getCookies();

		if (cookies == null) {
			return null;
		}

		return Arrays.asList(cookies);
	}

	@Bean(name = "eventRequest")
	@Scope(proxyMode = ScopedProxyMode.INTERFACES, value = "portletRequest")
	public EventRequest getEventRequest() {
		PortletRequest portletRequest = _getPortletRequest();

		if (portletRequest instanceof EventRequest) {
			return (EventRequest)portletRequest;
		}

		return null;
	}

	@Bean(name = "eventResponse")
	@Scope(proxyMode = ScopedProxyMode.INTERFACES, value = "portletRequest")
	public EventResponse getEventResponse() {
		PortletResponse portletResponse = _getPortletResponse();

		if (portletResponse instanceof EventResponse) {
			return (EventResponse)portletResponse;
		}

		return null;
	}

	@Bean(name = "headerRequest")
	@Scope(proxyMode = ScopedProxyMode.INTERFACES, value = "portletRequest")
	public HeaderRequest getHeaderRequest() {
		PortletRequest portletRequest = _getPortletRequest();

		if (portletRequest instanceof HeaderRequest) {
			return (HeaderRequest)portletRequest;
		}

		return null;
	}

	@Bean(name = "headerResponse")
	@Scope(proxyMode = ScopedProxyMode.INTERFACES, value = "portletRequest")
	public HeaderResponse getHeaderResponse() {
		PortletResponse portletResponse = _getPortletResponse();

		if (portletResponse instanceof HeaderResponse) {
			return (HeaderResponse)portletResponse;
		}

		return null;
	}

	@Bean(name = "locales")
	@Scope(proxyMode = ScopedProxyMode.INTERFACES, value = "portletRequest")
	public List<Locale> getLocales() {
		PortletRequest portletRequest = _getPortletRequest();

		if (portletRequest == null) {
			return null;
		}

		return Collections.list(portletRequest.getLocales());
	}

	@Bean(name = "mutableRenderParams")
	@Scope(proxyMode = ScopedProxyMode.INTERFACES, value = "portletRequest")
	public MutableRenderParameters getMutableRenderParameters() {
		StateAwareResponse stateAwareResponse = _getStateAwareResponse();

		if (stateAwareResponse == null) {
			return null;
		}

		return stateAwareResponse.getRenderParameters();
	}

	@Bean(name = "namespace")
	@Namespace
	@Scope(
		proxyMode = ScopedProxyMode.NO,
		value = ConfigurableBeanFactory.SCOPE_PROTOTYPE
	)
	public String getNamespace() {
		PortletResponse portletResponse = _getPortletResponse();

		if (portletResponse == null) {
			_log.error(
				new IllegalStateException(
					_getDependentStringErrorMessage(Namespace.class)));

			return null;
		}

		return portletResponse.getNamespace();
	}

	@Bean(name = "portletConfig")
	@Primary
	@Scope(proxyMode = ScopedProxyMode.INTERFACES, value = "portletRequest")
	public PortletConfig getPortletConfig() {
		return _getPortletConfig();
	}

	@Bean(name = "portletContext")
	@Scope(proxyMode = ScopedProxyMode.INTERFACES, value = "portletRequest")
	public PortletContext getPortletContext() {
		PortletConfig portletConfig = _getPortletConfig();

		if (portletConfig == null) {
			return null;
		}

		return portletConfig.getPortletContext();
	}

	@Bean(name = "portletMode")
	@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "portletRequest")
	public PortletMode getPortletMode() {
		PortletRequest portletRequest = _getPortletRequest();

		if (portletRequest == null) {
			return null;
		}

		return portletRequest.getPortletMode();
	}

	@Bean(name = "portletName")
	@PortletName
	@Scope(
		proxyMode = ScopedProxyMode.NO,
		value = ConfigurableBeanFactory.SCOPE_PROTOTYPE
	)
	public String getPortletName() {
		PortletConfig portletConfig = _getPortletConfig();

		if (portletConfig == null) {
			throw new IllegalStateException(
				_getDependentStringErrorMessage(PortletName.class));
		}

		return portletConfig.getPortletName();
	}

	@Bean(name = "portletPreferences")
	@Scope(proxyMode = ScopedProxyMode.INTERFACES, value = "portletRequest")
	public PortletPreferences getPortletPreferences() {
		PortletRequest portletRequest = _getPortletRequest();

		if (portletRequest == null) {
			return null;
		}

		return portletRequest.getPreferences();
	}

	@Bean(name = "portletSession")
	@Scope(proxyMode = ScopedProxyMode.INTERFACES, value = "portletRequest")
	public PortletSession getPortletSession() {
		PortletRequest portletRequest = _getPortletRequest();

		if (portletRequest == null) {
			return null;
		}

		return portletRequest.getPortletSession();
	}

	@Bean(name = "renderParams")
	@Primary
	@Scope(proxyMode = ScopedProxyMode.INTERFACES, value = "portletRequest")
	public RenderParameters getRenderParameters() {
		PortletRequest portletRequest = _getPortletRequest();

		if (portletRequest == null) {
			return null;
		}

		return portletRequest.getRenderParameters();
	}

	@Bean(name = "renderResponse")
	@Scope(proxyMode = ScopedProxyMode.INTERFACES, value = "portletRequest")
	public RenderResponse getRenderResponse() {
		PortletResponse portletResponse = _getPortletResponse();

		if (portletResponse instanceof RenderResponse) {
			return (RenderResponse)portletResponse;
		}

		return null;
	}

	@Bean(name = "resourceParams")
	@Scope(proxyMode = ScopedProxyMode.INTERFACES, value = "portletRequest")
	public ResourceParameters getResourceParameters() {
		ResourceRequest resourceRequest = _getResourceRequest();

		if (resourceRequest == null) {
			return null;
		}

		return resourceRequest.getResourceParameters();
	}

	@Bean(name = "resourceRequest")
	@Scope(proxyMode = ScopedProxyMode.INTERFACES, value = "portletRequest")
	public ResourceRequest getResourceRequest() {
		return _getResourceRequest();
	}

	@Bean(name = "resourceResponse")
	@Scope(proxyMode = ScopedProxyMode.INTERFACES, value = "portletRequest")
	public ResourceResponse getResourceResponse() {
		PortletResponse portletResponse = _getPortletResponse();

		if (portletResponse instanceof ResourceResponse) {
			return (ResourceResponse)portletResponse;
		}

		return null;
	}

	@Bean(name = "windowId")
	@Scope(
		proxyMode = ScopedProxyMode.NO,
		value = ConfigurableBeanFactory.SCOPE_PROTOTYPE
	)
	@WindowId
	public String getWindowID() {
		PortletRequest portletRequest = _getPortletRequest();

		if (portletRequest == null) {
			_log.error(
				new IllegalStateException(
					_getDependentStringErrorMessage(WindowId.class)));

			return null;
		}

		return portletRequest.getWindowID();
	}

	@Bean(name = "windowState")
	@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "portletRequest")
	public WindowState getWindowState() {
		PortletRequest portletRequest = _getPortletRequest();

		if (portletRequest == null) {
			return null;
		}

		return portletRequest.getWindowState();
	}

	private ActionRequest _getActionRequest() {
		PortletRequest portletRequest = _getPortletRequest();

		if (portletRequest instanceof ActionRequest) {
			return (ActionRequest)portletRequest;
		}

		return null;
	}

	private String _getDependentStringErrorMessage(
		Class<? extends Annotation> annotationClass) {

		return StringBundler.concat(
			"Unable to @Inject ", annotationClass, " into field because it is ",
			"a @Dependent String that can only be injected during a request. ",
			"Annotate the parent class with @PortletRequestScoped instead of ",
			"@ApplicationScoped.");
	}

	private PortletConfig _getPortletConfig() {
		SpringScopedBeanManager springScopedBeanManager =
			SpringScopedBeanManagerThreadLocal.getCurrentScopedBeanManager();

		if (springScopedBeanManager == null) {
			return null;
		}

		return springScopedBeanManager.getPortletConfig();
	}

	private PortletRequest _getPortletRequest() {
		SpringScopedBeanManager springScopedBeanManager =
			SpringScopedBeanManagerThreadLocal.getCurrentScopedBeanManager();

		if (springScopedBeanManager == null) {
			return null;
		}

		return springScopedBeanManager.getPortletRequest();
	}

	private PortletResponse _getPortletResponse() {
		SpringScopedBeanManager springScopedBeanManager =
			SpringScopedBeanManagerThreadLocal.getCurrentScopedBeanManager();

		if (springScopedBeanManager == null) {
			return null;
		}

		return springScopedBeanManager.getPortletResponse();
	}

	private ResourceRequest _getResourceRequest() {
		PortletRequest portletRequest = _getPortletRequest();

		if (portletRequest instanceof ResourceRequest) {
			return (ResourceRequest)portletRequest;
		}

		return null;
	}

	private StateAwareResponse _getStateAwareResponse() {
		PortletResponse portletResponse = _getPortletResponse();

		if (portletResponse instanceof StateAwareResponse) {
			return (StateAwareResponse)portletResponse;
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JSR362SpringBeanProducer.class);

}