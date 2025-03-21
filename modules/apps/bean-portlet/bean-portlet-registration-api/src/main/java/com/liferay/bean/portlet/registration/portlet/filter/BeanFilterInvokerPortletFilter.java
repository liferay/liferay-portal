/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.registration.portlet.filter;

import com.liferay.bean.portlet.extension.BeanFilterMethod;
import com.liferay.bean.portlet.extension.BeanFilterMethodFactory;
import com.liferay.bean.portlet.extension.BeanFilterMethodInvoker;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.EventRequest;
import jakarta.portlet.EventResponse;
import jakarta.portlet.HeaderRequest;
import jakarta.portlet.HeaderResponse;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;
import jakarta.portlet.filter.ActionFilter;
import jakarta.portlet.filter.EventFilter;
import jakarta.portlet.filter.FilterChain;
import jakarta.portlet.filter.FilterConfig;
import jakarta.portlet.filter.HeaderFilter;
import jakarta.portlet.filter.HeaderFilterChain;
import jakarta.portlet.filter.PortletFilter;
import jakarta.portlet.filter.RenderFilter;
import jakarta.portlet.filter.ResourceFilter;

import java.io.IOException;

import java.lang.reflect.Method;

/**
 * @author Neil Griffin
 */
public class BeanFilterInvokerPortletFilter
	implements ActionFilter, EventFilter, HeaderFilter, RenderFilter,
			   ResourceFilter {

	public BeanFilterInvokerPortletFilter(
		BeanFilterMethodFactory beanFilterMethodFactory,
		BeanFilterMethodInvoker beanFilterMethodInvoker,
		Class<? extends PortletFilter> filterClass) {

		_beanFilterMethodFactory = beanFilterMethodFactory;
		_beanFilterMethodInvoker = beanFilterMethodInvoker;
		_filterClass = filterClass;
	}

	@Override
	public void destroy() {
		try {
			_invokeMethod(_destroyMethod);
		}
		catch (PortletException portletException) {
			_log.error(portletException);
		}
	}

	@Override
	public void doFilter(
			ActionRequest actionRequest, ActionResponse actionResponse,
			FilterChain filterChain)
		throws IOException, PortletException {

		if (ActionFilter.class.isAssignableFrom(_filterClass)) {
			_invokeMethodWithActiveScopes(
				_actionDoFilterMethod, actionRequest, actionResponse,
				filterChain);
		}
		else {
			filterChain.doFilter(actionRequest, actionResponse);
		}
	}

	@Override
	public void doFilter(
			EventRequest eventRequest, EventResponse eventResponse,
			FilterChain filterChain)
		throws IOException, PortletException {

		if (EventFilter.class.isAssignableFrom(_filterClass)) {
			_invokeMethodWithActiveScopes(
				_eventDoFilterMethod, eventRequest, eventResponse, filterChain);
		}
		else {
			filterChain.doFilter(eventRequest, eventResponse);
		}
	}

	@Override
	public void doFilter(
			HeaderRequest headerRequest, HeaderResponse headerResponse,
			HeaderFilterChain headerFilterChain)
		throws IOException, PortletException {

		if (HeaderFilter.class.isAssignableFrom(_filterClass)) {
			_invokeMethodWithActiveScopes(
				_headerDoFilterMethod, headerRequest, headerResponse,
				headerFilterChain);
		}
		else {
			headerFilterChain.doFilter(headerRequest, headerResponse);
		}
	}

	@Override
	public void doFilter(
			RenderRequest renderRequest, RenderResponse renderResponse,
			FilterChain filterChain)
		throws IOException, PortletException {

		if (RenderFilter.class.isAssignableFrom(_filterClass)) {
			_invokeMethodWithActiveScopes(
				_renderDoFilterMethod, renderRequest, renderResponse,
				filterChain);
		}
		else {
			filterChain.doFilter(renderRequest, renderResponse);
		}
	}

	@Override
	public void doFilter(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse,
			FilterChain filterChain)
		throws IOException, PortletException {

		if (ResourceFilter.class.isAssignableFrom(_filterClass)) {
			_invokeMethodWithActiveScopes(
				_resourceDoFilterMethod, resourceRequest, resourceResponse,
				filterChain);
		}
		else {
			filterChain.doFilter(resourceRequest, resourceResponse);
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws PortletException {
		_invokeMethod(_initMethod, filterConfig);
	}

	private void _invokeMethod(Method method, Object... arguments)
		throws PortletException {

		BeanFilterMethod beanFilterMethod = _beanFilterMethodFactory.create(
			_filterClass, method);

		try {
			beanFilterMethod.invoke(arguments);
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			Throwable throwable = reflectiveOperationException.getCause();

			if (throwable instanceof PortletException) {
				throw (PortletException)throwable;
			}

			throw new PortletException(throwable);
		}
	}

	private void _invokeMethodWithActiveScopes(
			Method method, PortletRequest portletRequest,
			PortletResponse portletResponse, Object filterChain)
		throws PortletException {

		BeanFilterMethod beanFilterMethod = _beanFilterMethodFactory.create(
			_filterClass, method);

		_beanFilterMethodInvoker.invokeWithActiveScopes(
			beanFilterMethod, filterChain, portletRequest, portletResponse);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BeanFilterInvokerPortletFilter.class);

	private static final Method _actionDoFilterMethod;
	private static final Method _destroyMethod;
	private static final Method _eventDoFilterMethod;
	private static final Method _headerDoFilterMethod;
	private static final Method _initMethod;
	private static final Method _renderDoFilterMethod;
	private static final Method _resourceDoFilterMethod;

	static {
		try {
			_actionDoFilterMethod = ActionFilter.class.getMethod(
				"doFilter", ActionRequest.class, ActionResponse.class,
				FilterChain.class);
			_destroyMethod = PortletFilter.class.getMethod("destroy");
			_eventDoFilterMethod = EventFilter.class.getMethod(
				"doFilter", EventRequest.class, EventResponse.class,
				FilterChain.class);
			_headerDoFilterMethod = HeaderFilter.class.getMethod(
				"doFilter", HeaderRequest.class, HeaderResponse.class,
				HeaderFilterChain.class);
			_initMethod = PortletFilter.class.getMethod(
				"init", FilterConfig.class);
			_renderDoFilterMethod = RenderFilter.class.getMethod(
				"doFilter", RenderRequest.class, RenderResponse.class,
				FilterChain.class);
			_resourceDoFilterMethod = ResourceFilter.class.getMethod(
				"doFilter", ResourceRequest.class, ResourceResponse.class,
				FilterChain.class);
		}
		catch (NoSuchMethodException noSuchMethodException) {
			throw new ExceptionInInitializerError(noSuchMethodException);
		}
	}

	private final BeanFilterMethodFactory _beanFilterMethodFactory;
	private final BeanFilterMethodInvoker _beanFilterMethodInvoker;
	private final Class<? extends PortletFilter> _filterClass;

}