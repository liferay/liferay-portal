/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutType;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletApp;
import com.liferay.portal.kernel.model.PortletPreferencesIds;
import com.liferay.portal.kernel.model.PublicRenderParameter;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.ActionResult;
import com.liferay.portal.kernel.portlet.InvokerPortlet;
import com.liferay.portal.kernel.portlet.LiferayActionRequest;
import com.liferay.portal.kernel.portlet.LiferayActionResponse;
import com.liferay.portal.kernel.portlet.LiferayEventRequest;
import com.liferay.portal.kernel.portlet.LiferayEventResponse;
import com.liferay.portal.kernel.portlet.LiferayPortletMode;
import com.liferay.portal.kernel.portlet.LiferayResourceRequest;
import com.liferay.portal.kernel.portlet.LiferayResourceResponse;
import com.liferay.portal.kernel.portlet.LiferayStateAwareResponse;
import com.liferay.portal.kernel.portlet.PortletConfigFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletContainer;
import com.liferay.portal.kernel.portlet.PortletContainerException;
import com.liferay.portal.kernel.portlet.PortletInstanceFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletModeFactory;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletQName;
import com.liferay.portal.kernel.portlet.PortletQNameUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.WindowStateFactory;
import com.liferay.portal.kernel.portlet.async.PortletAsyncScopeManager;
import com.liferay.portal.kernel.portlet.configuration.icon.PortletConfigurationIconMenu;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletPreferencesLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.servlet.BufferCacheServletResponse;
import com.liferay.portal.kernel.servlet.DirectRequestDispatcherFactoryUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.TransferHeadersHelperUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.util.comparator.PortletConfigurationIconComparator;
import com.liferay.portal.kernel.webdav.WebDAVStorage;
import com.liferay.portal.kernel.xml.QName;
import com.liferay.portal.theme.PortletDisplayFactory;
import com.liferay.portlet.ActionRequestFactory;
import com.liferay.portlet.ActionResponseFactory;
import com.liferay.portlet.EventRequestFactory;
import com.liferay.portlet.EventResponseFactory;
import com.liferay.portlet.InvokerPortletUtil;
import com.liferay.portlet.PublicRenderParametersPool;
import com.liferay.portlet.RenderParametersPool;
import com.liferay.portlet.ResourceRequestFactory;
import com.liferay.portlet.ResourceResponseFactory;
import com.liferay.util.SerializableUtil;

import jakarta.portlet.Event;
import jakarta.portlet.MimeResponse;
import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletContext;
import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.WindowState;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.Serializable;
import java.io.Writer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Shuyang Zhou
 * @author Raymond Augé
 * @author Neil Griffin
 */
public class PortletContainerImpl implements PortletContainer {

	@Override
	public void preparePortlet(
			HttpServletRequest httpServletRequest, Portlet portlet)
		throws PortletContainerException {

		try {
			_preparePortlet(httpServletRequest, portlet);
		}
		catch (Exception exception) {
			throw new PortletContainerException(exception);
		}
	}

	@Override
	public ActionResult processAction(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Portlet portlet)
		throws PortletContainerException {

		return _preserveGroupIds(
			httpServletRequest,
			() -> {
				if (portlet != null) {
					_processGroupId(httpServletRequest, portlet);
				}

				return _processAction(
					httpServletRequest, httpServletResponse, portlet);
			});
	}

	@Override
	public List<Event> processEvent(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Portlet portlet,
			Layout layout, Event event)
		throws PortletContainerException {

		return _preserveGroupIds(
			httpServletRequest,
			() -> {
				String portletId = ParamUtil.getString(
					httpServletRequest, "p_p_id");

				if ((portlet != null) &&
					portletId.equals(portlet.getPortletId())) {

					_processGroupId(httpServletRequest, portlet);
				}

				return _processEvent(
					httpServletRequest, httpServletResponse, portlet, layout,
					event);
			});
	}

	@Override
	public void processPublicRenderParameters(
		HttpServletRequest httpServletRequest, Layout layout) {

		processPublicRenderParameters(httpServletRequest, layout, null);
	}

	@Override
	public void processPublicRenderParameters(
		HttpServletRequest httpServletRequest, Layout layout, Portlet portlet) {

		LayoutType layoutType = layout.getLayoutType();

		if (!(layoutType instanceof LayoutTypePortlet)) {
			return;
		}

		LayoutTypePortlet layoutTypePortlet = (LayoutTypePortlet)layoutType;

		List<Portlet> portlets = layoutTypePortlet.getPortlets();

		portlets.remove(portlet);

		_processPublicRenderParameters(
			httpServletRequest, layout, portlets, false);
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Portlet portlet)
		throws PortletContainerException {

		_preserveGroupIds(
			httpServletRequest,
			() -> {
				String portletId = ParamUtil.getString(
					httpServletRequest, "p_p_id");

				if ((portlet != null) &&
					portletId.equals(portlet.getPortletId())) {

					_processGroupId(httpServletRequest, portlet);
				}

				_render(
					httpServletRequest, httpServletResponse, portlet, false);

				return null;
			});
	}

	@Override
	public void renderHeaders(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Portlet portlet)
		throws PortletContainerException {

		_preserveGroupIds(
			httpServletRequest,
			() -> {
				String portletId = ParamUtil.getString(
					httpServletRequest, "p_p_id");

				if ((portlet != null) &&
					portletId.equals(portlet.getPortletId())) {

					_processGroupId(httpServletRequest, portlet);
				}

				_render(httpServletRequest, httpServletResponse, portlet, true);

				return null;
			});
	}

	@Override
	public void serveResource(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Portlet portlet)
		throws PortletContainerException {

		_preserveGroupIds(
			httpServletRequest,
			() -> {
				if (portlet != null) {
					_processGroupId(httpServletRequest, portlet);
				}

				_serveResource(
					httpServletRequest, httpServletResponse, portlet);

				return null;
			});
	}

	protected long getScopeGroupId(
			HttpServletRequest httpServletRequest, Layout layout,
			String portletId)
		throws PortalException {

		long scopeGroupId = 0;

		Layout requestLayout = (Layout)httpServletRequest.getAttribute(
			WebKeys.LAYOUT);

		try {
			httpServletRequest.setAttribute(WebKeys.LAYOUT, layout);

			scopeGroupId = PortalUtil.getScopeGroupId(
				httpServletRequest, portletId);
		}
		finally {
			httpServletRequest.setAttribute(WebKeys.LAYOUT, requestLayout);
		}

		if (scopeGroupId <= 0) {
			scopeGroupId = PortalUtil.getScopeGroupId(layout, portletId);
		}

		return scopeGroupId;
	}

	protected Event serializeEvent(
		Event event, ClassLoader portletClassLoader) {

		Serializable value = event.getValue();

		if (value == null) {
			return event;
		}

		Class<?> valueClass = value.getClass();

		String valueClassName = valueClass.getName();

		try {
			Class<?> loadedValueClass = portletClassLoader.loadClass(
				valueClassName);

			if (loadedValueClass.equals(valueClass)) {
				return event;
			}
		}
		catch (ClassNotFoundException classNotFoundException) {
			throw new RuntimeException(classNotFoundException);
		}

		byte[] serializedValue = SerializableUtil.serialize(value);

		value = (Serializable)SerializableUtil.deserialize(
			serializedValue, portletClassLoader);

		return new EventImpl(event.getName(), event.getQName(), value);
	}

	private boolean _isPublishedContentPage(Layout layout) {
		if (!layout.isDraftLayout() && layout.isTypeContent()) {
			return true;
		}

		return false;
	}

	private void _preparePortlet(
			HttpServletRequest httpServletRequest, Portlet portlet)
		throws Exception {

		User user = PortalUtil.getUser(httpServletRequest);
		Layout layout = (Layout)httpServletRequest.getAttribute(WebKeys.LAYOUT);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (user != null) {
			InvokerPortletUtil.clearResponse(
				httpServletRequest.getSession(), layout.getPrimaryKey(),
				portlet.getPortletId(),
				LanguageUtil.getLanguageId(httpServletRequest));
		}

		_processPublicRenderParameters(
			httpServletRequest, layout, Arrays.asList(portlet),
			themeDisplay.isLifecycleAction());

		if (themeDisplay.isHubAction() || themeDisplay.isHubPartialAction() ||
			themeDisplay.isLifecycleAction() ||
			themeDisplay.isLifecycleRender() ||
			themeDisplay.isLifecycleResource()) {

			WindowState windowState = WindowStateFactory.getWindowState(
				ParamUtil.getString(httpServletRequest, "p_p_state"));

			if (layout.isTypeControlPanel() &&
				((windowState == null) ||
				 windowState.equals(WindowState.NORMAL) ||
				 Validator.isNull(windowState.toString()))) {

				windowState = WindowState.MAXIMIZED;
			}

			PortletMode portletMode = PortletModeFactory.getPortletMode(
				ParamUtil.getString(httpServletRequest, "p_p_mode"));

			PortalUtil.updateWindowState(
				portlet.getPortletId(), user, layout, windowState,
				httpServletRequest);

			PortalUtil.updatePortletMode(
				portlet.getPortletId(), user, layout, portletMode,
				httpServletRequest);
		}
	}

	private <T> T _preserveGroupIds(
			HttpServletRequest httpServletRequest,
			UnsafeSupplier<T, Exception> unsafeSupplier)
		throws PortletContainerException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		long previousScopeGroupId = 0;
		long previousSiteGroupId = 0;

		if (themeDisplay != null) {
			previousScopeGroupId = themeDisplay.getScopeGroupId();
			previousSiteGroupId = themeDisplay.getSiteGroupId();
		}

		try {
			return unsafeSupplier.get();
		}
		catch (Exception exception) {
			throw new PortletContainerException(exception);
		}
		finally {
			if (themeDisplay != null) {
				if (GroupLocalServiceUtil.fetchGroup(previousScopeGroupId) !=
						null) {

					themeDisplay.setScopeGroupId(previousScopeGroupId);
				}

				if (GroupLocalServiceUtil.fetchGroup(previousSiteGroupId) !=
						null) {

					themeDisplay.setSiteGroupId(previousSiteGroupId);
				}
			}
		}
	}

	private ActionResult _processAction(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Portlet portlet)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		portletDisplay.setId(portlet.getPortletId());

		Layout layout = (Layout)httpServletRequest.getAttribute(WebKeys.LAYOUT);

		WindowState windowState = WindowStateFactory.getWindowState(
			ParamUtil.getString(httpServletRequest, "p_p_state"));

		if (layout.isTypeControlPanel() &&
			((windowState == null) || windowState.equals(WindowState.NORMAL) ||
			 Validator.isNull(windowState.toString()))) {

			windowState = WindowState.MAXIMIZED;
		}

		PortletMode portletMode = PortletModeFactory.getPortletMode(
			ParamUtil.getString(httpServletRequest, "p_p_mode"));

		PortletPreferences portletPreferences =
			PortletPreferencesLocalServiceUtil.getStrictPreferences(
				PortletPreferencesFactoryUtil.getPortletPreferencesIds(
					httpServletRequest, portlet.getPortletId()));

		ServletContext servletContext =
			(ServletContext)httpServletRequest.getAttribute(WebKeys.CTX);

		InvokerPortlet invokerPortlet = PortletInstanceFactoryUtil.create(
			portlet, servletContext);

		PortletConfig portletConfig = PortletConfigFactoryUtil.create(
			portlet, servletContext);

		PortletContext portletContext = portletConfig.getPortletContext();

		if (_log.isDebugEnabled()) {
			String contentType = httpServletRequest.getHeader(
				HttpHeaders.CONTENT_TYPE);

			_log.debug("Content type " + contentType);
		}

		try {
			LiferayActionRequest liferayActionRequest =
				ActionRequestFactory.create(
					httpServletRequest, portlet, invokerPortlet, portletContext,
					windowState, portletMode, portletPreferences,
					layout.getPlid());

			LiferayActionResponse liferayActionResponse =
				ActionResponseFactory.create(
					liferayActionRequest, httpServletResponse,
					PortalUtil.getUser(httpServletRequest), layout);

			liferayActionRequest.defineObjects(
				portletConfig, liferayActionResponse);

			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				liferayActionRequest);

			ServiceContextThreadLocal.pushServiceContext(serviceContext);

			invokerPortlet.processAction(
				liferayActionRequest, liferayActionResponse);

			liferayActionResponse.transferHeaders(httpServletResponse);

			RenderParametersPool.clear(
				httpServletRequest, layout.getPlid(), portlet.getPortletId());

			PortletApp portletApp = portlet.getPortletApp();

			if (portletApp.getSpecMajorVersion() < 3) {
				RenderParametersPool.put(
					httpServletRequest, layout.getPlid(),
					portlet.getPortletId(),
					liferayActionResponse.getRenderParameterMap());
			}
			else {
				Layout requestLayout = (Layout)httpServletRequest.getAttribute(
					WebKeys.LAYOUT);

				_setAllRenderParameters(
					httpServletRequest, liferayActionResponse, portlet,
					requestLayout);
			}

			List<Event> events = liferayActionResponse.getEvents();

			String redirectLocation =
				liferayActionResponse.getRedirectLocation();

			if (Validator.isNotNull(redirectLocation)) {
				if (!GetterUtil.getBoolean(
						liferayActionResponse.getProperty(
							LiferayActionResponse.SKIP_ESCAPE_REDIRECT))) {

					redirectLocation = PortalUtil.escapeRedirect(
						redirectLocation);
				}

				return new ActionResult(events, redirectLocation);
			}

			if (!portlet.isActionURLRedirect()) {
				return new ActionResult(events, null);
			}

			PortletURL portletURL = null;

			if (portletApp.getSpecMajorVersion() < 3) {
				portletURL = PortletURLFactoryUtil.create(
					liferayActionRequest, portlet, layout,
					PortletRequest.RENDER_PHASE);

				Map<String, String[]> renderParameters =
					liferayActionResponse.getRenderParameterMap();

				for (Map.Entry<String, String[]> entry :
						renderParameters.entrySet()) {

					portletURL.setParameter(entry.getKey(), entry.getValue());
				}
			}
			else {
				portletURL = PortletURLFactoryUtil.create(
					liferayActionRequest, portlet, layout.getPlid(),
					PortletRequest.RENDER_PHASE, MimeResponse.Copy.ALL);
			}

			redirectLocation = portletURL.toString();

			return new ActionResult(events, redirectLocation);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	private List<Event> _processEvent(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Portlet portlet,
			Layout layout, Event event)
		throws Exception {

		ServletContext servletContext =
			(ServletContext)httpServletRequest.getAttribute(WebKeys.CTX);

		InvokerPortlet invokerPortlet = PortletInstanceFactoryUtil.create(
			portlet, servletContext);

		PortletConfig portletConfig = PortletConfigFactoryUtil.create(
			portlet, servletContext);

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		WindowState windowState = null;

		if (layoutTypePortlet.hasStateMaxPortletId(portlet.getPortletId())) {
			windowState = WindowState.MAXIMIZED;
		}
		else if (layoutTypePortlet.hasStateMinPortletId(
					portlet.getPortletId())) {

			windowState = WindowState.MINIMIZED;
		}
		else {
			windowState = WindowState.NORMAL;
		}

		PortletMode portletMode = null;

		if (layoutTypePortlet.hasModeAboutPortletId(portlet.getPortletId())) {
			portletMode = LiferayPortletMode.ABOUT;
		}
		else if (layoutTypePortlet.hasModeConfigPortletId(
					portlet.getPortletId())) {

			portletMode = LiferayPortletMode.CONFIG;
		}
		else if (layoutTypePortlet.hasModeEditPortletId(
					portlet.getPortletId())) {

			portletMode = PortletMode.EDIT;
		}
		else if (layoutTypePortlet.hasModeEditDefaultsPortletId(
					portlet.getPortletId())) {

			portletMode = LiferayPortletMode.EDIT_DEFAULTS;
		}
		else if (layoutTypePortlet.hasModeEditGuestPortletId(
					portlet.getPortletId())) {

			portletMode = LiferayPortletMode.EDIT_GUEST;
		}
		else if (layoutTypePortlet.hasModeHelpPortletId(
					portlet.getPortletId())) {

			portletMode = PortletMode.HELP;
		}
		else if (layoutTypePortlet.hasModePreviewPortletId(
					portlet.getPortletId())) {

			portletMode = LiferayPortletMode.PREVIEW;
		}
		else if (layoutTypePortlet.hasModePrintPortletId(
					portlet.getPortletId())) {

			portletMode = LiferayPortletMode.PRINT;
		}
		else {
			portletMode = PortletMode.VIEW;
		}

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.getPortletSetup(
				getScopeGroupId(
					httpServletRequest, layout, portlet.getPortletId()),
				layout, portlet.getPortletId(), null);

		LiferayEventRequest liferayEventRequest = EventRequestFactory.create(
			httpServletRequest, portlet, invokerPortlet,
			portletConfig.getPortletContext(), windowState, portletMode,
			portletPreferences, layout.getPlid());

		liferayEventRequest.setEvent(
			serializeEvent(event, invokerPortlet.getPortletClassLoader()));

		Layout requestLayout = (Layout)httpServletRequest.getAttribute(
			WebKeys.LAYOUT);

		LiferayEventResponse liferayEventResponse = EventResponseFactory.create(
			liferayEventRequest, httpServletResponse,
			PortalUtil.getUser(httpServletRequest), requestLayout);

		liferayEventRequest.defineObjects(portletConfig, liferayEventResponse);

		try {
			invokerPortlet.processEvent(
				liferayEventRequest, liferayEventResponse);

			liferayEventResponse.transferHeaders(httpServletResponse);

			if (liferayEventResponse.isCalledSetRenderParameter()) {
				PortletApp portletApp = portlet.getPortletApp();

				if (portletApp.getSpecMajorVersion() < 3) {
					RenderParametersPool.put(
						httpServletRequest, requestLayout.getPlid(),
						portlet.getPortletId(),
						new HashMap<>(
							liferayEventResponse.getRenderParameterMap()));
				}
				else {
					_setAllRenderParameters(
						httpServletRequest, liferayEventResponse, portlet,
						requestLayout);
				}
			}

			return liferayEventResponse.getEvents();
		}
		finally {
			liferayEventRequest.cleanUp();
		}
	}

	private void _processGroupId(
			HttpServletRequest httpServletRequest, Portlet portlet)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		long scopeGroupId = PortalUtil.getScopeGroupId(
			httpServletRequest, portlet.getPortletId());

		themeDisplay.setScopeGroupId(scopeGroupId);

		long siteGroupId = 0;

		Layout layout = (Layout)httpServletRequest.getAttribute(WebKeys.LAYOUT);

		if (layout.isTypeControlPanel()) {
			siteGroupId = PortalUtil.getSiteGroupId(scopeGroupId);
		}
		else {
			siteGroupId = PortalUtil.getSiteGroupId(layout.getGroupId());
		}

		themeDisplay.setSiteGroupId(siteGroupId);
	}

	private void _processPublicRenderParameters(
		HttpServletRequest httpServletRequest, Layout layout,
		List<Portlet> portlets, boolean lifecycleAction) {

		PortletQName portletQName = PortletQNameUtil.getPortletQName();
		Map<String, String[]> publicRenderParameters = null;
		Map<String, String[]> parameters = httpServletRequest.getParameterMap();

		for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
			String name = entry.getKey();

			QName qName = portletQName.getQName(name);

			if (qName == null) {
				continue;
			}

			for (Portlet portlet : portlets) {
				PublicRenderParameter publicRenderParameter =
					portlet.getPublicRenderParameter(
						qName.getNamespaceURI(), qName.getLocalPart());

				if (publicRenderParameter == null) {
					continue;
				}

				if (publicRenderParameters == null) {
					publicRenderParameters = PublicRenderParametersPool.get(
						httpServletRequest, layout.getPlid());
				}

				String publicRenderParameterName =
					portletQName.getPublicRenderParameterName(qName);

				if (name.startsWith(
						PortletQName.PUBLIC_RENDER_PARAMETER_NAMESPACE)) {

					String[] values = entry.getValue();

					if (lifecycleAction) {
						String[] oldValues = publicRenderParameters.get(
							publicRenderParameterName);

						if ((oldValues != null) && (oldValues.length != 0)) {
							values = ArrayUtil.append(values, oldValues);
						}
					}

					publicRenderParameters.put(
						publicRenderParameterName, values);
				}
				else {
					publicRenderParameters.remove(publicRenderParameterName);
				}
			}
		}
	}

	private void _render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Portlet portlet,
			boolean headerPhase)
		throws Exception {

		if ((portlet != null) && portlet.isInstanceable() &&
			!portlet.isAddDefaultResource() &&
			!Validator.isPassword(portlet.getInstanceId())) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Portlet ", portlet.getPortletId(),
						" is instanceable but does not have a valid instance ",
						"id"));
			}

			portlet = null;
		}

		if (portlet == null) {
			return;
		}

		// Capture the current portlet's settings to reset them once the child
		// portlet is rendered

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletPreferencesFactoryUtil.checkControlPanelPortletPreferences(
			themeDisplay, portlet);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		PortletConfigurationIconMenu.INSTANCE.setComparator(
			PortletConfigurationIconComparator.INSTANCE);

		PortletDisplay portletDisplayClone = PortletDisplayFactory.create();

		portletDisplay.copyTo(portletDisplayClone);

		PortletConfig portletConfig =
			(PortletConfig)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_CONFIG);

		PortletRequest portletRequest =
			(PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST);

		if (!(portletRequest instanceof RenderRequest)) {
			portletRequest = null;
		}

		PortletResponse portletResponse =
			(PortletResponse)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_RESPONSE);

		if (!(portletResponse instanceof RenderResponse)) {
			portletResponse = null;
		}

		String lifecycle = (String)httpServletRequest.getAttribute(
			PortletRequest.LIFECYCLE_PHASE);

		httpServletRequest.setAttribute(WebKeys.RENDER_PORTLET, portlet);

		String path = (String)httpServletRequest.getAttribute(
			WebKeys.RENDER_PATH);

		if (path == null) {
			path = "/html/portal/render_portlet.jsp";
		}

		if (headerPhase) {
			path = "/html/portal/header_portlet.jsp";
		}

		RequestDispatcher requestDispatcher =
			TransferHeadersHelperUtil.getTransferHeadersRequestDispatcher(
				DirectRequestDispatcherFactoryUtil.getRequestDispatcher(
					httpServletRequest, path));

		BufferCacheServletResponse bufferCacheServletResponse = null;

		boolean writeOutput = false;

		if (httpServletResponse instanceof BufferCacheServletResponse) {
			bufferCacheServletResponse =
				(BufferCacheServletResponse)httpServletResponse;
		}
		else {
			writeOutput = true;
			bufferCacheServletResponse = new BufferCacheServletResponse(
				httpServletResponse);
		}

		try {
			requestDispatcher.include(
				httpServletRequest, bufferCacheServletResponse);

			Boolean portletConfiguratorVisibility =
				(Boolean)httpServletRequest.getAttribute(
					WebKeys.PORTLET_CONFIGURATOR_VISIBILITY);

			if (portletConfiguratorVisibility != null) {
				httpServletRequest.removeAttribute(
					WebKeys.PORTLET_CONFIGURATOR_VISIBILITY);

				Layout layout = themeDisplay.getLayout();

				if (_isPublishedContentPage(layout) ||
					(!layout.isTypeControlPanel() &&
					 !LayoutPermissionUtil.contains(
						 themeDisplay.getPermissionChecker(), layout,
						 ActionKeys.UPDATE) &&
					 !PortletPermissionUtil.contains(
						 themeDisplay.getPermissionChecker(), layout,
						 portlet.getPortletId(), ActionKeys.CONFIGURATION))) {

					bufferCacheServletResponse.setCharBuffer(null);

					return;
				}
			}

			if (writeOutput) {
				Writer writer = httpServletResponse.getWriter();

				writer.write(bufferCacheServletResponse.getString());
			}
		}
		finally {
			portletDisplay.copyFrom(portletDisplayClone);

			portletDisplayClone.recycle();

			if (portletConfig != null) {
				httpServletRequest.setAttribute(
					JavaConstants.JAVAX_PORTLET_CONFIG, portletConfig);
			}

			if (portletRequest != null) {
				httpServletRequest.setAttribute(
					JavaConstants.JAVAX_PORTLET_REQUEST, portletRequest);
			}

			if (portletResponse != null) {
				httpServletRequest.setAttribute(
					JavaConstants.JAVAX_PORTLET_RESPONSE, portletResponse);
			}

			if (lifecycle != null) {
				httpServletRequest.setAttribute(
					PortletRequest.LIFECYCLE_PHASE, lifecycle);
			}

			httpServletRequest.removeAttribute(WebKeys.RENDER_PORTLET);
		}
	}

	private void _serveResource(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Portlet portlet)
		throws Exception {

		WindowState windowState = (WindowState)httpServletRequest.getAttribute(
			WebKeys.WINDOW_STATE);

		PortletApp portletApp = portlet.getPortletApp();

		int portletSpecMajorVersion = portletApp.getSpecMajorVersion();

		if (portletSpecMajorVersion >= 3) {
			WindowState requestWindowState = WindowStateFactory.getWindowState(
				ParamUtil.getString(httpServletRequest, "p_p_state"), 3);

			if (WindowState.UNDEFINED.equals(requestWindowState)) {
				windowState = requestWindowState;
			}
		}

		PortletPreferencesIds portletPreferencesIds =
			PortletPreferencesFactoryUtil.getPortletPreferencesIds(
				httpServletRequest, portlet.getPortletId());

		ServletContext servletContext =
			(ServletContext)httpServletRequest.getAttribute(WebKeys.CTX);

		InvokerPortlet invokerPortlet = PortletInstanceFactoryUtil.create(
			portlet, servletContext);

		PortletConfig portletConfig = PortletConfigFactoryUtil.create(
			portlet, servletContext);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		Layout layout = (Layout)httpServletRequest.getAttribute(WebKeys.LAYOUT);

		String portletPrimaryKey = PortletPermissionUtil.getPrimaryKey(
			layout.getPlid(), portlet.getPortletId());

		portletDisplay.setId(portlet.getPortletId());
		portletDisplay.setInstanceId(portlet.getInstanceId());
		portletDisplay.setNamespace(
			PortalUtil.getPortletNamespace(portlet.getPortletId()));
		portletDisplay.setPortletName(portletConfig.getPortletName());
		portletDisplay.setResourcePK(portletPrimaryKey);
		portletDisplay.setRootPortletId(portlet.getRootPortletId());

		WebDAVStorage webDAVStorage = portlet.getWebDAVStorageInstance();

		if (webDAVStorage != null) {
			portletDisplay.setWebDAVEnabled(true);
		}
		else {
			portletDisplay.setWebDAVEnabled(false);
		}

		LiferayResourceRequest liferayResourceRequest = null;

		PortletRequest portletRequest =
			(PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST);

		if (portletRequest instanceof LiferayResourceRequest) {
			liferayResourceRequest = (LiferayResourceRequest)portletRequest;
		}

		LiferayResourceResponse liferayResourceResponse = null;

		if (liferayResourceRequest == null) {
			PortletMode portletMode = PortletModeFactory.getPortletMode(
				ParamUtil.getString(httpServletRequest, "p_p_mode"),
				portletSpecMajorVersion);

			PortletPreferences portletPreferences =
				PortletPreferencesLocalServiceUtil.getStrictPreferences(
					portletPreferencesIds);

			liferayResourceRequest = ResourceRequestFactory.create(
				httpServletRequest, portlet, invokerPortlet,
				portletConfig.getPortletContext(), windowState, portletMode,
				portletPreferences, layout.getPlid());

			liferayResourceResponse = ResourceResponseFactory.create(
				liferayResourceRequest, httpServletResponse);
		}
		else {
			liferayResourceResponse =
				(LiferayResourceResponse)httpServletRequest.getAttribute(
					JavaConstants.JAVAX_PORTLET_RESPONSE);
		}

		liferayResourceRequest.defineObjects(
			portletConfig, liferayResourceResponse);

		try {
			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				liferayResourceRequest);

			ServiceContextThreadLocal.pushServiceContext(serviceContext);

			invokerPortlet.serveResource(
				liferayResourceRequest, liferayResourceResponse);

			liferayResourceResponse.transferHeaders(httpServletResponse);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();

			if (liferayResourceRequest.isAsyncStarted() &&
				liferayResourceRequest.isAsyncSupported()) {

				PortletAsyncContextImpl portletAsyncContextImpl =
					(PortletAsyncContextImpl)
						liferayResourceRequest.getPortletAsyncContext();

				if (themeDisplay.isAsync()) {
					PortletAsyncScopeManager portletAsyncScopeManager =
						portletAsyncContextImpl.getPortletAsyncScopeManager();

					portletAsyncScopeManager.deactivateScopeContexts(true);
				}

				portletAsyncContextImpl.setReturnedToContainer();
			}
		}
	}

	private void _setAllRenderParameters(
		HttpServletRequest httpServletRequest,
		LiferayStateAwareResponse liferayStateAwareResponse, Portlet portlet,
		Layout requestLayout) {

		MutableRenderParametersImpl mutableRenderParametersImpl =
			(MutableRenderParametersImpl)
				liferayStateAwareResponse.getRenderParameters();

		Map<String, String[]> mutableRenderParameterMap =
			mutableRenderParametersImpl.getParameterMap();

		Map<String, QName> supportedPublicRenderParameterMap = new HashMap<>();

		for (PublicRenderParameter supportedPublicRenderParameter :
				portlet.getPublicRenderParameters()) {

			supportedPublicRenderParameterMap.put(
				supportedPublicRenderParameter.getIdentifier(),
				supportedPublicRenderParameter.getQName());
		}

		Map<String, String[]> publicRenderParameterMap =
			PublicRenderParametersPool.get(
				httpServletRequest, requestLayout.getPlid());

		Map<String, String[]> privateRenderParameterMap = new HashMap<>();

		for (Map.Entry<String, String[]> entry :
				mutableRenderParameterMap.entrySet()) {

			String key = entry.getKey();

			QName qName = supportedPublicRenderParameterMap.get(key);

			if (qName != null) {
				publicRenderParameterMap.put(
					PortletQNameUtil.getPublicRenderParameterName(qName),
					entry.getValue());

				continue;
			}

			privateRenderParameterMap.put(key, entry.getValue());
		}

		if (!privateRenderParameterMap.isEmpty()) {
			RenderParametersPool.put(
				httpServletRequest, requestLayout.getPlid(),
				portlet.getPortletId(), privateRenderParameterMap);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortletContainerImpl.class);

}