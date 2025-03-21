/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.portlet.async.PortletAsyncListenerFactory;
import com.liferay.portal.kernel.portlet.async.PortletAsyncScopeManager;
import com.liferay.portal.kernel.portlet.async.PortletAsyncScopeManagerFactory;
import com.liferay.portlet.AsyncPortletServletRequest;
import com.liferay.portlet.PortletAsyncListenerAdapter;

import jakarta.portlet.PortletAsyncContext;
import jakarta.portlet.PortletAsyncListener;
import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletContext;
import jakarta.portlet.PortletException;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletRequestWrapper;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Neil Griffin
 * @author Dante Wang
 * @author Leon Chi
 */
public class PortletAsyncContextImpl implements PortletAsyncContext {

	@Override
	public void addListener(PortletAsyncListener portletAsyncListener)
		throws IllegalStateException {

		addListener(portletAsyncListener, null, null);
	}

	@Override
	public void addListener(
			PortletAsyncListener portletAsyncListener,
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws IllegalStateException {

		if (!_resourceRequest.isAsyncStarted() || _returnedToContainer) {
			throw new IllegalStateException();
		}

		_portletAsyncListenerAdapter.addListener(
			portletAsyncListener, resourceRequest, resourceResponse);
	}

	@Override
	public void complete() throws IllegalStateException {
		_asyncContext.complete();

		_calledComplete = true;
	}

	@Override
	public <T extends PortletAsyncListener> T createPortletAsyncListener(
			Class<T> listenerClass)
		throws PortletException {

		return _portletAsyncListenerFactory.getPortletAsyncListener(
			listenerClass);
	}

	@Override
	public void dispatch() throws IllegalStateException {
		if (!_resourceRequest.isAsyncStarted() || _calledComplete ||
			_calledDispatch) {

			throw new IllegalStateException();
		}

		HttpServletRequest originalHttpServletRequest =
			(HttpServletRequest)_getOriginalServletRequest();

		String path = StringBundler.concat(
			originalHttpServletRequest.getRequestURI(), "?p_p_async=1&",
			originalHttpServletRequest.getQueryString());

		ServletContext servletContext =
			originalHttpServletRequest.getServletContext();

		_asyncPortletServletRequest.update(
			servletContext.getContextPath(), path);

		_asyncContext.dispatch(servletContext, path);

		_calledDispatch = true;
	}

	@Override
	public void dispatch(String path) throws IllegalStateException {
		if (!_resourceRequest.isAsyncStarted() || _calledComplete ||
			_calledDispatch) {

			throw new IllegalStateException();
		}

		ServletRequest originalServletRequest = _getOriginalServletRequest();

		ServletContext servletContext =
			originalServletRequest.getServletContext();

		String contextPath = _resourceRequest.getContextPath();

		path = contextPath.concat(path);

		_asyncPortletServletRequest.update(
			servletContext.getContextPath(), path);

		_asyncContext.dispatch(servletContext, path);

		_calledDispatch = true;
	}

	public PortletAsyncScopeManager getPortletAsyncScopeManager() {
		return _portletAsyncScopeManager;
	}

	@Override
	public ResourceRequest getResourceRequest() throws IllegalStateException {
		if (_calledComplete ||
			(_calledDispatch && !_resourceRequest.isAsyncStarted())) {

			throw new IllegalStateException();
		}

		return _resourceRequest;
	}

	@Override
	public ResourceResponse getResourceResponse() throws IllegalStateException {
		if (_calledComplete ||
			(_calledDispatch && !_resourceRequest.isAsyncStarted())) {

			throw new IllegalStateException();
		}

		return _resourceResponse;
	}

	@Override
	public long getTimeout() {
		return _asyncContext.getTimeout();
	}

	@Override
	public boolean hasOriginalRequestAndResponse() {
		return _hasOriginalRequestAndResponse;
	}

	public boolean isCalledDispatch() {
		return _calledDispatch;
	}

	public void setReturnedToContainer() {
		_returnedToContainer = true;
	}

	@Override
	public void setTimeout(long timeout) {
		_asyncContext.setTimeout(timeout);
	}

	@Override
	public void start(Runnable runnable) throws IllegalStateException {
		_asyncContext.start(
			new PortletAsyncScopingRunnable(
				runnable, _portletAsyncListenerAdapter,
				_portletAsyncScopeManager));
	}

	protected void initialize(
		ResourceRequest resourceRequest, ResourceResponse resourceResponse,
		PortletConfig portletConfig, AsyncContext asyncContext,
		boolean hasOriginalRequestAndResponse) {

		_resourceRequest = resourceRequest;
		_resourceResponse = resourceResponse;
		_asyncContext = asyncContext;
		_hasOriginalRequestAndResponse = hasOriginalRequestAndResponse;

		PortletContext portletContext = portletConfig.getPortletContext();

		_servletContextName = portletContext.getPortletContextName();

		_portletAsyncScopeManager = _getPortletAsyncScopeManager(
			_servletContextName);

		// Activate scope contexts in the main thread so that
		// @PortletRequestScoped beans will not get destroyed when
		// ResourceRequest processing completes. Deactivatation takes place in
		// the async thread via the PortletAsyncScopingListener so that the
		// beans will be destroyed when async processing completes. If
		// dispatch() is called, then deactivation takes place in
		// PortletContainerImpl.serveResource(request,response,portlet)
		// during the subsequent dispatch request.

		_portletAsyncScopeManager.activateScopeContexts(
			resourceRequest, resourceResponse, portletConfig);

		_portletAsyncListenerFactory = _getPortletAsyncListenerFactory(
			_servletContextName);

		_calledDispatch = false;
		_calledComplete = false;
		_returnedToContainer = false;

		if (_portletAsyncListenerAdapter == null) {
			_portletAsyncListenerAdapter = new PortletAsyncScopingListener(
				this, _portletAsyncScopeManager);

			_asyncContext.addListener(_portletAsyncListenerAdapter);
		}

		if (_asyncPortletServletRequest == null) {
			_asyncPortletServletRequest =
				(AsyncPortletServletRequest)_asyncContext.getRequest();
		}
	}

	private ServletRequest _getOriginalServletRequest() {
		ServletRequest originalServletRequest = _asyncPortletServletRequest;

		while (originalServletRequest instanceof ServletRequestWrapper) {
			ServletRequestWrapper servletRequestWrapper =
				(ServletRequestWrapper)originalServletRequest;

			originalServletRequest = servletRequestWrapper.getRequest();
		}

		return originalServletRequest;
	}

	private PortletAsyncListenerFactory _getPortletAsyncListenerFactory(
		String servletContextName) {

		PortletAsyncListenerFactory portletAsyncListenerFactory =
			_portletAsyncListenerFactories.getService(servletContextName);

		if (portletAsyncListenerFactory == null) {
			portletAsyncListenerFactory = _dummyPortletAsyncListenerFactory;
		}

		return portletAsyncListenerFactory;
	}

	private PortletAsyncScopeManager _getPortletAsyncScopeManager(
		String servletContextName) {

		PortletAsyncScopeManagerFactory portletAsyncScopeManagerFactory =
			_portletAsyncScopeManagerFactories.getService(servletContextName);

		if (portletAsyncScopeManagerFactory == null) {
			return _dummyPortletAsyncScopeManager;
		}

		return portletAsyncScopeManagerFactory.getPortletAsyncScopeManager();
	}

	private static final PortletAsyncListenerFactory
		_dummyPortletAsyncListenerFactory = new PortletAsyncListenerFactory() {

			@Override
			public <T extends PortletAsyncListener> T getPortletAsyncListener(
					Class<T> clazz)
				throws PortletException {

				try {
					return clazz.newInstance();
				}
				catch (ReflectiveOperationException
							reflectiveOperationException) {

					throw new PortletException(reflectiveOperationException);
				}
			}

		};

	private static final PortletAsyncScopeManager
		_dummyPortletAsyncScopeManager = new PortletAsyncScopeManager() {

			@Override
			public void activateScopeContexts() {
			}

			@Override
			public void activateScopeContexts(
				ResourceRequest resourceRequest,
				ResourceResponse resourceResponse,
				PortletConfig portletConfig) {
			}

			@Override
			public void deactivateScopeContexts(boolean close) {
			}

		};

	private static final ServiceTrackerMap<String, PortletAsyncListenerFactory>
		_portletAsyncListenerFactories =
			ServiceTrackerMapFactory.openSingleValueMap(
				SystemBundleUtil.getBundleContext(),
				PortletAsyncListenerFactory.class, "servlet.context.name");
	private static final ServiceTrackerMap
		<String, PortletAsyncScopeManagerFactory>
			_portletAsyncScopeManagerFactories =
				ServiceTrackerMapFactory.openSingleValueMap(
					SystemBundleUtil.getBundleContext(),
					PortletAsyncScopeManagerFactory.class,
					"servlet.context.name");

	private AsyncContext _asyncContext;
	private AsyncPortletServletRequest _asyncPortletServletRequest;
	private boolean _calledComplete;
	private boolean _calledDispatch;
	private boolean _hasOriginalRequestAndResponse;
	private PortletAsyncListenerAdapter _portletAsyncListenerAdapter;
	private PortletAsyncListenerFactory _portletAsyncListenerFactory;
	private PortletAsyncScopeManager _portletAsyncScopeManager;
	private ResourceRequest _resourceRequest;
	private ResourceResponse _resourceResponse;
	private boolean _returnedToContainer;
	private String _servletContextName;

}