/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.InvokerFilterContainer;
import com.liferay.portal.kernel.portlet.InvokerPortlet;
import com.liferay.portal.kernel.portlet.LiferayPortletConfig;
import com.liferay.portal.kernel.portlet.LiferayPortletContext;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletFilterUtil;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.servlet.BufferCacheServletResponse;
import com.liferay.portal.kernel.servlet.PluginContextListener;
import com.liferay.portal.kernel.servlet.PortletServlet;
import com.liferay.portal.kernel.util.ClassUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.InvokerPortletResponse;
import com.liferay.portlet.InvokerPortletUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.EventRequest;
import jakarta.portlet.EventResponse;
import jakarta.portlet.HeaderRequest;
import jakarta.portlet.HeaderResponse;
import jakarta.portlet.Portlet;
import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletContext;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;
import jakarta.portlet.UnavailableException;
import jakarta.portlet.filter.ActionFilter;
import jakarta.portlet.filter.EventFilter;
import jakarta.portlet.filter.FilterChain;
import jakarta.portlet.filter.HeaderFilter;
import jakarta.portlet.filter.PortletFilter;
import jakarta.portlet.filter.RenderFilter;
import jakarta.portlet.filter.ResourceFilter;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.StopWatch;

/**
 * @author Brian Wing Shun Chan
 * @author Brian Myunghun Kim
 * @author Raymond Augé
 * @author Neil Griffin
 */
public class InvokerPortletImpl
	implements InvokerFilterContainer, InvokerPortlet {

	public InvokerPortletImpl(
		com.liferay.portal.kernel.model.Portlet portletModel, Portlet portlet,
		PortletConfig portletConfig, PortletContext portletContext,
		InvokerFilterContainer invokerFilterContainer, boolean checkAuthToken,
		boolean facesPortlet, boolean headerPortlet) {

		_initialize(
			portletModel, portlet, portletConfig, portletContext,
			invokerFilterContainer, checkAuthToken, facesPortlet,
			headerPortlet);
	}

	public InvokerPortletImpl(
		com.liferay.portal.kernel.model.Portlet portletModel, Portlet portlet,
		PortletContext portletContext,
		InvokerFilterContainer invokerFilterContainer) {

		Map<String, String> initParams = portletModel.getInitParams();

		boolean checkAuthToken = GetterUtil.getBoolean(
			initParams.get("check-auth-token"), true);

		boolean facesPortlet = false;

		Class<? extends Portlet> portletClass = portlet.getClass();

		if (ClassUtil.isSubclass(
				portletClass, "jakarta.portlet.faces.GenericFacesPortlet")) {

			facesPortlet = true;
		}
		else if (portlet instanceof InvokerPortlet) {
			InvokerPortlet invokerPortlet = (InvokerPortlet)portlet;

			facesPortlet = invokerPortlet.isFacesPortlet();
		}

		boolean headerPortlet = PortletTypeUtil.isHeaderPortlet(portlet);

		_initialize(
			portletModel, portlet, null, portletContext, invokerFilterContainer,
			checkAuthToken, facesPortlet, headerPortlet);
	}

	@Override
	public void destroy() {
		if (PortletIdCodec.hasInstanceId(_portletModel.getPortletId())) {
			if (_log.isWarnEnabled()) {
				_log.warn("Destroying an instanced portlet is not allowed");
			}

			return;
		}

		ClassLoader classLoader = _portletClassLoader;

		if (classLoader == null) {
			Thread currentThread = Thread.currentThread();

			classLoader = currentThread.getContextClassLoader();
		}

		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				classLoader)) {

			cleanUp();

			_portlet.destroy();
		}
	}

	@Override
	public List<ActionFilter> getActionFilters() {
		return _invokerFilterContainer.getActionFilters();
	}

	@Override
	public List<EventFilter> getEventFilters() {
		return _invokerFilterContainer.getEventFilters();
	}

	@Override
	public Integer getExpCache() {
		return _expCache;
	}

	@Override
	public List<HeaderFilter> getHeaderFilters() {
		return _invokerFilterContainer.getHeaderFilters();
	}

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public ClassLoader getPortletClassLoader() {
		if (_portlet instanceof InvokerPortlet) {
			InvokerPortlet invokerPortlet = (InvokerPortlet)_portlet;

			return invokerPortlet.getPortletClassLoader();
		}

		ClassLoader classLoader =
			(ClassLoader)_liferayPortletContext.getAttribute(
				PluginContextListener.PLUGIN_CLASS_LOADER);

		if (classLoader != null) {
			return classLoader;
		}

		Class<?> portletClass = _portlet.getClass();

		return portletClass.getClassLoader();
	}

	@Override
	public PortletConfig getPortletConfig() {
		return _liferayPortletConfig;
	}

	@Override
	public PortletContext getPortletContext() {
		return _liferayPortletContext;
	}

	@Override
	public Portlet getPortletInstance() {
		return _portlet;
	}

	@Override
	public List<RenderFilter> getRenderFilters() {
		return _invokerFilterContainer.getRenderFilters();
	}

	@Override
	public List<ResourceFilter> getResourceFilters() {
		return _invokerFilterContainer.getResourceFilters();
	}

	@Override
	public void init(PortletConfig portletConfig) throws PortletException {
		_liferayPortletConfig = (LiferayPortletConfig)portletConfig;

		_portletClassLoader = getPortletClassLoader();

		ClassLoader classLoader = _portletClassLoader;

		if (classLoader == null) {
			Thread currentThread = Thread.currentThread();

			classLoader = currentThread.getContextClassLoader();
		}

		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				classLoader)) {

			_portlet.init(portletConfig);
		}
		catch (Throwable throwable) {
			cleanUp();

			throw throwable;
		}
	}

	@Override
	public boolean isCheckAuthToken() {
		return _checkAuthToken;
	}

	@Override
	public boolean isFacesPortlet() {
		return _facesPortlet;
	}

	@Override
	public boolean isHeaderPortlet() {
		return _headerPortlet;
	}

	@Override
	public void processAction(
		ActionRequest actionRequest, ActionResponse actionResponse) {

		StopWatch stopWatch = new StopWatch();

		stopWatch.start();

		try {
			invokeAction(actionRequest, actionResponse);
		}
		catch (Exception exception) {
			processException(exception, actionRequest, actionResponse);
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"processAction for ", _portletId, " takes ",
					stopWatch.getTime(), " ms"));
		}
	}

	@Override
	public void processEvent(
		EventRequest eventRequest, EventResponse eventResponse) {

		StopWatch stopWatch = new StopWatch();

		stopWatch.start();

		try {
			invokeEvent(eventRequest, eventResponse);
		}
		catch (Exception exception) {
			processException(exception, eventRequest, eventResponse);
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"processEvent for ", _portletId, " takes ",
					stopWatch.getTime(), " ms"));
		}
	}

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		PortletException portletException =
			(PortletException)renderRequest.getAttribute(_errorKey);

		if (portletException != null) {
			throw portletException;
		}

		StopWatch stopWatch = new StopWatch();

		stopWatch.start();

		String remoteUser = renderRequest.getRemoteUser();

		if ((remoteUser == null) || (_expCache == null) ||
			(_expCache.intValue() == 0)) {

			invokeRender(renderRequest, renderResponse);
		}
		else {
			RenderResponseImpl renderResponseImpl =
				(RenderResponseImpl)renderResponse;

			BufferCacheServletResponse bufferCacheServletResponse =
				(BufferCacheServletResponse)
					renderResponseImpl.getHttpServletResponse();

			long now = System.currentTimeMillis();

			Layout layout = (Layout)renderRequest.getAttribute(WebKeys.LAYOUT);

			Map<String, InvokerPortletResponse> sessionResponses =
				InvokerPortletUtil.getResponses(
					renderRequest.getPortletSession());

			String sessionResponseId = InvokerPortletUtil.encodeResponseKey(
				layout.getPlid(), _portletId,
				LanguageUtil.getLanguageId(renderRequest));

			InvokerPortletResponse response = sessionResponses.get(
				sessionResponseId);

			if (response == null) {
				String title = invokeRender(renderRequest, renderResponse);

				response = new InvokerPortletResponse(
					title, bufferCacheServletResponse.getString(),
					now + (Time.SECOND * _expCache.intValue()));

				sessionResponses.put(sessionResponseId, response);
			}
			else if ((response.getTime() < now) && (_expCache.intValue() > 0)) {
				response.setTitle(invokeRender(renderRequest, renderResponse));
				response.setContent(bufferCacheServletResponse.getString());
				response.setTime(now + (Time.SECOND * _expCache.intValue()));
			}
			else {
				renderResponseImpl.setTitle(response.getTitle());

				PrintWriter printWriter =
					bufferCacheServletResponse.getWriter();

				printWriter.print(response.getContent());
			}
		}

		RenderResponseImpl renderResponseImpl =
			(RenderResponseImpl)renderResponse;

		Map<String, String[]> properties = renderResponseImpl.getProperties();

		if (properties.containsKey("clear-request-parameters")) {
			RenderRequestImpl renderRequestImpl =
				(RenderRequestImpl)renderRequest;

			renderRequestImpl.clearRenderParameters();
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"render for ", _portletId, " takes ", stopWatch.getTime(),
					" ms"));
		}
	}

	@Override
	public void renderHeaders(
			HeaderRequest headerRequest, HeaderResponse headerResponse)
		throws IOException, PortletException {

		PortletException portletException =
			(PortletException)headerRequest.getAttribute(_errorKey);

		if (portletException != null) {
			throw portletException;
		}

		StopWatch stopWatch = new StopWatch();

		stopWatch.start();

		String remoteUser = headerRequest.getRemoteUser();

		if ((remoteUser == null) || (_expCache == null) || (_expCache == 0)) {
			invokeHeader(headerRequest, headerResponse);
		}
		else {
			HeaderResponseImpl headerResponseImpl =
				(HeaderResponseImpl)headerResponse;

			BufferCacheServletResponse bufferCacheServletResponse =
				(BufferCacheServletResponse)
					headerResponseImpl.getHttpServletResponse();

			long now = System.currentTimeMillis();

			Layout layout = (Layout)headerRequest.getAttribute(WebKeys.LAYOUT);

			Map<String, InvokerPortletResponse> sessionResponses =
				InvokerPortletUtil.getResponses(
					headerRequest.getPortletSession());

			String sessionResponseId = InvokerPortletUtil.encodeResponseKey(
				layout.getPlid(), _portletId,
				LanguageUtil.getLanguageId(headerRequest));

			InvokerPortletResponse response = sessionResponses.get(
				sessionResponseId);

			if (response == null) {
				invokeHeader(headerRequest, headerResponse);
			}
			else if ((response.getTime() < now) && (_expCache > 0)) {
				invokeHeader(headerRequest, headerResponse);

				response.setContent(bufferCacheServletResponse.getString());
			}
			else {
				headerResponseImpl.setTitle(response.getTitle());

				PrintWriter printWriter =
					bufferCacheServletResponse.getWriter();

				printWriter.print(response.getContent());
			}
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"header for", _portletId, " takes ", stopWatch.getTime(),
					" ms"));
		}
	}

	@Override
	public void serveResource(
		ResourceRequest resourceRequest, ResourceResponse resourceResponse) {

		StopWatch stopWatch = new StopWatch();

		stopWatch.start();

		try {
			invokeResource(resourceRequest, resourceResponse);
		}
		catch (Exception exception) {
			processException(exception, resourceRequest, resourceResponse);
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"serveResource for ", _portletId, " takes ",
					stopWatch.getTime(), " ms"));
		}
	}

	@Override
	public void setPortletFilters() {
	}

	protected void cleanUp() {
		try {
			Closeable closeable = (Closeable)_invokerFilterContainer;

			closeable.close();
		}
		catch (IOException ioException) {
			_log.error("Unable to close invoker filter container", ioException);
		}
	}

	protected void invoke(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse, String lifecycle,
			List<? extends PortletFilter> filters)
		throws IOException, PortletException {

		FilterChain filterChain = new FilterChainImpl(_portlet, filters);

		if (_liferayPortletConfig.isWARFile()) {
			String invokerPortletName = _liferayPortletConfig.getInitParameter(
				INIT_INVOKER_PORTLET_NAME);

			if (invokerPortletName == null) {
				invokerPortletName = PortalUtil.getJsSafePortletId(
					_liferayPortletConfig.getPortletName());
			}

			String path = StringPool.SLASH + invokerPortletName + "/invoke";

			ServletContext servletContext =
				_liferayPortletContext.getServletContext();

			RequestDispatcher requestDispatcher =
				servletContext.getRequestDispatcher(path);

			HttpServletRequest httpServletRequest =
				liferayPortletRequest.getHttpServletRequest();
			HttpServletResponse httpServletResponse =
				liferayPortletResponse.getHttpServletResponse();

			httpServletRequest.setAttribute(
				JavaConstants.JAVAX_PORTLET_PORTLET, _portlet);
			httpServletRequest.setAttribute(
				PortletRequest.LIFECYCLE_PHASE, lifecycle);
			httpServletRequest.setAttribute(
				PortletServlet.PORTLET_SERVLET_FILTER_CHAIN, filterChain);

			try {

				// Resource phase must be a forward because includes do not
				// allow you to specify the content type or headers

				if (lifecycle.equals(PortletRequest.RESOURCE_PHASE)) {
					requestDispatcher.forward(
						httpServletRequest, httpServletResponse);
				}
				else {
					requestDispatcher.include(
						httpServletRequest, httpServletResponse);
				}
			}
			catch (ServletException servletException) {
				Throwable throwable = servletException.getRootCause();

				if (throwable instanceof PortletException) {
					throw (PortletException)throwable;
				}

				throw new PortletException(throwable);
			}
		}
		else {
			PortletFilterUtil.doFilter(
				liferayPortletRequest, liferayPortletResponse, lifecycle,
				filterChain);
		}

		liferayPortletResponse.transferMarkupHeadElements();

		Map<String, String[]> properties =
			liferayPortletResponse.getProperties();

		if (MapUtil.isNotEmpty(properties) && (_expCache != null)) {
			String[] expCache = properties.get(RenderResponse.EXPIRATION_CACHE);

			if ((expCache != null) && (expCache.length > 0) &&
				(expCache[0] != null)) {

				_expCache = Integer.valueOf(GetterUtil.getInteger(expCache[0]));
			}
		}
	}

	protected void invokeAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws IOException, PortletException {

		invoke(
			PortalUtil.getLiferayPortletRequest(actionRequest),
			PortalUtil.getLiferayPortletResponse(actionResponse),
			PortletRequest.ACTION_PHASE,
			_invokerFilterContainer.getActionFilters());
	}

	protected void invokeEvent(
			EventRequest eventRequest, EventResponse eventResponse)
		throws IOException, PortletException {

		invoke(
			PortalUtil.getLiferayPortletRequest(eventRequest),
			PortalUtil.getLiferayPortletResponse(eventResponse),
			PortletRequest.EVENT_PHASE,
			_invokerFilterContainer.getEventFilters());
	}

	protected void invokeHeader(
			HeaderRequest headerRequest, HeaderResponse headerResponse)
		throws IOException, PortletException {

		LiferayPortletRequest liferayPortletRequest =
			PortalUtil.getLiferayPortletRequest(headerRequest);
		LiferayPortletResponse liferayPortletResponse =
			PortalUtil.getLiferayPortletResponse(headerResponse);

		try {
			invoke(
				liferayPortletRequest, liferayPortletResponse,
				PortletRequest.HEADER_PHASE,
				_invokerFilterContainer.getHeaderFilters());
		}
		catch (Exception exception) {
			processException(exception, headerRequest, headerResponse);

			throw exception;
		}
	}

	protected String invokeRender(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		LiferayPortletRequest liferayPortletRequest =
			PortalUtil.getLiferayPortletRequest(renderRequest);
		LiferayPortletResponse liferayPortletResponse =
			PortalUtil.getLiferayPortletResponse(renderResponse);

		try {
			invoke(
				liferayPortletRequest, liferayPortletResponse,
				PortletRequest.RENDER_PHASE,
				_invokerFilterContainer.getRenderFilters());
		}
		catch (Exception exception) {
			processException(exception, renderRequest, renderResponse);

			throw exception;
		}

		RenderResponseImpl renderResponseImpl =
			(RenderResponseImpl)renderResponse;

		return renderResponseImpl.getTitle();
	}

	protected void invokeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws IOException, PortletException {

		invoke(
			PortalUtil.getLiferayPortletRequest(resourceRequest),
			PortalUtil.getLiferayPortletResponse(resourceResponse),
			PortletRequest.RESOURCE_PHASE,
			_invokerFilterContainer.getResourceFilters());
	}

	protected void processException(
		Exception exception, PortletRequest liferayPortletRequest,
		PortletResponse liferayPortletResponse) {

		if (liferayPortletResponse instanceof StateAwareResponseImpl) {

			// PLT.5.4.7, TCK xxiii and PLT.15.2.6, cxlvi

			StateAwareResponseImpl stateAwareResponseImpl =
				(StateAwareResponseImpl)liferayPortletResponse;

			stateAwareResponseImpl.reset();
		}

		if (exception instanceof RuntimeException) {

			// PLT.5.4.7, TCK xxv

			exception = new PortletException(exception);
		}

		if (exception instanceof UnavailableException) {

			// PLT.5.4.7, TCK xxiv

			destroy();

			PortletLocalServiceUtil.deletePortlet(_portletModel);
		}

		if (exception instanceof PortletException) {
			if ((liferayPortletResponse instanceof StateAwareResponseImpl) &&
				!(exception instanceof UnavailableException)) {

				return;
			}

			if (!(liferayPortletRequest instanceof RenderRequest)) {
				liferayPortletRequest.setAttribute(_errorKey, exception);
			}
		}
		else {
			ReflectionUtil.throwException(exception);
		}
	}

	private void _initialize(
		com.liferay.portal.kernel.model.Portlet portletModel, Portlet portlet,
		PortletConfig portletConfig, PortletContext portletContext,
		InvokerFilterContainer invokerFilterContainer, boolean checkAuthToken,
		boolean facesPortlet, boolean headerPortlet) {

		_portletModel = portletModel;
		_portlet = portlet;
		_invokerFilterContainer = invokerFilterContainer;
		_checkAuthToken = checkAuthToken;
		_facesPortlet = facesPortlet;
		_headerPortlet = headerPortlet;

		_expCache = portletModel.getExpCache();
		_liferayPortletConfig = (LiferayPortletConfig)portletConfig;
		_liferayPortletContext = (LiferayPortletContext)portletContext;

		_portletId = _portletModel.getPortletId();

		_errorKey = _portletId.concat(PortletException.class.getName());

		if (_log.isDebugEnabled()) {
			com.liferay.portal.kernel.model.Portlet portletContextPortlet =
				_liferayPortletContext.getPortlet();

			_log.debug(
				"Create instance cache wrapper for " +
					portletContextPortlet.getPortletId());
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		InvokerPortletImpl.class);

	private boolean _checkAuthToken;
	private String _errorKey;
	private Integer _expCache;
	private boolean _facesPortlet;
	private boolean _headerPortlet;
	private InvokerFilterContainer _invokerFilterContainer;
	private LiferayPortletConfig _liferayPortletConfig;
	private LiferayPortletContext _liferayPortletContext;
	private Portlet _portlet;
	private ClassLoader _portletClassLoader;
	private String _portletId;
	private com.liferay.portal.kernel.model.Portlet _portletModel;

}