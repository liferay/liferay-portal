/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.http.servlet.internal.servlet;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.osgi.web.http.servlet.internal.Match;
import com.liferay.portal.osgi.web.http.servlet.internal.context.LiferayContextController;
import com.liferay.portal.osgi.web.http.servlet.internal.context.LiferayDispatchTargets;
import com.liferay.portal.osgi.web.http.servlet.internal.registration.EndpointRegistration;
import com.liferay.portal.osgi.web.http.servlet.internal.registration.EventListeners;
import com.liferay.portal.osgi.web.http.servlet.internal.registration.FilterRegistration;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequestEvent;
import jakarta.servlet.ServletRequestListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.IOException;

import java.util.Collections;
import java.util.List;

import org.osgi.service.http.context.ServletContextHelper;

/**
 * @author Dante Wang
 */
public class ResponseStateHandler {

	public ResponseStateHandler(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse,
		LiferayDispatchTargets liferayDispatchTargets) {

		_httpServletRequest = httpServletRequest;
		_httpServletResponse = httpServletResponse;
		_liferayDispatchTargets = liferayDispatchTargets;
	}

	public void processRequest() throws IOException, ServletException {
		LiferayContextController liferayContextController =
			_liferayDispatchTargets.getContextController();

		EventListeners eventListeners =
			liferayContextController.getEventListeners();

		List<ServletRequestListener> servletRequestListeners =
			eventListeners.get(ServletRequestListener.class);

		EndpointRegistration<?> endpointRegistration =
			_liferayDispatchTargets.getServletRegistration();

		endpointRegistration.addReference();

		List<FilterRegistration> matchingFilterRegistrations =
			_liferayDispatchTargets.getMatchingFilterRegistrations();

		for (FilterRegistration filterRegistration :
				matchingFilterRegistrations) {

			filterRegistration.addReference();
		}

		ServletRequestEvent servletRequestEvent = null;

		try {
			if ((_liferayDispatchTargets.getDispatcherType() ==
					DispatcherType.REQUEST) &&
				!servletRequestListeners.isEmpty()) {

				servletRequestEvent = new ServletRequestEvent(
					endpointRegistration.getServletContext(),
					_httpServletRequest);

				for (ServletRequestListener servletRequestListener :
						servletRequestListeners) {

					servletRequestListener.requestInitialized(
						servletRequestEvent);
				}
			}

			ServletContextHelper servletContextHelper =
				endpointRegistration.getServletContextHelper();

			if (servletContextHelper.handleSecurity(
					_httpServletRequest, _httpServletResponse)) {

				if (matchingFilterRegistrations.isEmpty()) {
					endpointRegistration.service(
						_httpServletRequest, _httpServletResponse);
				}
				else {
					Collections.sort(matchingFilterRegistrations);

					FilterChain filterChain = new FilterChainImpl(
						_liferayDispatchTargets.getDispatcherType(),
						endpointRegistration, matchingFilterRegistrations);

					filterChain.doFilter(
						_httpServletRequest, _httpServletResponse);
				}
			}
		}
		catch (Exception exception) {
			setException(exception);

			if (_liferayDispatchTargets.getDispatcherType() !=
					DispatcherType.REQUEST) {

				ReflectionUtil.throwException(exception);
			}
		}
		finally {
			endpointRegistration.removeReference();

			for (FilterRegistration filterRegistration :
					matchingFilterRegistrations) {

				filterRegistration.removeReference();
			}

			if (_liferayDispatchTargets.getDispatcherType() ==
					DispatcherType.REQUEST) {

				_handleErrors();

				for (ServletRequestListener servletRequestListener :
						servletRequestListeners) {

					servletRequestListener.requestDestroyed(
						servletRequestEvent);
				}
			}
		}
	}

	public void setException(Exception exception) {
		_exception = exception;
	}

	private void _handleErrors() throws IOException, ServletException {
		if (_exception != null) {
			_handleException();
		}
		else {
			_handleResponseCode();
		}
	}

	private void _handleException() throws IOException, ServletException {
		if (!(_httpServletResponse instanceof HttpServletResponseWrapper)) {
			throw new IllegalStateException(
				"Response is not an instance of HttpServletResponseWrapper");
		}

		LiferayHttpServletResponseWrapper liferayHttpServletResponseWrapper =
			LiferayHttpServletResponseWrapper.find(_httpServletResponse);

		if (liferayHttpServletResponseWrapper == null) {
			throw new IllegalStateException(
				"Unable to get LiferayHttpServletResponseWrapper");
		}

		HttpServletResponse wrappedHttpServletResponse =
			(HttpServletResponse)
				liferayHttpServletResponseWrapper.getResponse();

		if (wrappedHttpServletResponse.isCommitted()) {
			ReflectionUtil.throwException(_exception);
		}

		LiferayContextController liferayContextController =
			_liferayDispatchTargets.getContextController();

		Class<? extends Exception> clazz = _exception.getClass();

		String className = clazz.getName();

		LiferayDispatchTargets errorLiferayDispatchTargets =
			liferayContextController.getDispatchTargets(
				className, null, null, null, null, null, Match.EXACT);

		if (errorLiferayDispatchTargets == null) {
			ReflectionUtil.throwException(_exception);
		}

		LiferayHttpServletRequestWrapper liferayHttpServletRequestWrapper =
			LiferayHttpServletRequestWrapper.find(_httpServletRequest);

		try {
			errorLiferayDispatchTargets.setDispatcherType(DispatcherType.ERROR);

			liferayHttpServletRequestWrapper.push(errorLiferayDispatchTargets);

			HttpServletRequest httpServletRequest =
				new HttpServletRequestWrapper(_httpServletRequest) {

					public Object getAttribute(String attributeName) {
						if (getDispatcherType() != DispatcherType.ERROR) {
							return super.getAttribute(attributeName);
						}

						if (attributeName.equals(
								RequestDispatcher.ERROR_EXCEPTION)) {

							return _exception;
						}

						if (attributeName.equals(
								RequestDispatcher.ERROR_EXCEPTION_TYPE)) {

							return className;
						}

						if (attributeName.equals(
								RequestDispatcher.ERROR_MESSAGE)) {

							return _exception.getMessage();
						}

						if (attributeName.equals(
								RequestDispatcher.ERROR_REQUEST_URI)) {

							return _httpServletRequest.getRequestURI();
						}

						if (attributeName.equals(
								RequestDispatcher.ERROR_SERVLET_NAME)) {

							EndpointRegistration<?> endpointRegistration =
								_liferayDispatchTargets.
									getServletRegistration();

							return endpointRegistration.getName();
						}

						if (attributeName.equals(
								RequestDispatcher.ERROR_STATUS_CODE)) {

							return 500;
						}

						return super.getAttribute(attributeName);
					}

					public DispatcherType getDispatcherType() {
						return DispatcherType.ERROR;
					}

				};

			HttpServletResponse httpServletResponse =
				new LiferayHttpServletResponseWrapper(
					wrappedHttpServletResponse);

			ResponseStateHandler responseStateHandler =
				new ResponseStateHandler(
					httpServletRequest, httpServletResponse,
					errorLiferayDispatchTargets);

			responseStateHandler.processRequest();

			wrappedHttpServletResponse.setStatus(500);
		}
		finally {
			liferayHttpServletRequestWrapper.pop();
		}
	}

	private void _handleResponseCode() throws IOException, ServletException {
		if (!(_httpServletResponse instanceof HttpServletResponseWrapper)) {
			throw new IllegalStateException(
				"Response is not an instance of HttpServletResponseWrapper");
		}

		LiferayHttpServletResponseWrapper liferayHttpServletResponseWrapper =
			LiferayHttpServletResponseWrapper.find(_httpServletResponse);

		if (liferayHttpServletResponseWrapper == null) {
			throw new IllegalStateException(
				"Unable to get LiferayHttpServletResponseWrapper");
		}

		int status = liferayHttpServletResponseWrapper.getInternalStatus();

		if (status < 400) {
			return;
		}

		HttpServletResponse wrappedHttpServletResponse =
			(HttpServletResponse)
				liferayHttpServletResponseWrapper.getResponse();

		if (wrappedHttpServletResponse.isCommitted()) {
			return;
		}

		LiferayContextController liferayContextController =
			_liferayDispatchTargets.getContextController();

		LiferayDispatchTargets errorLiferayDispatchTargets =
			liferayContextController.getDispatchTargets(
				String.valueOf(status), null, null, null, null, null,
				Match.EXACT);

		if (errorLiferayDispatchTargets == null) {
			wrappedHttpServletResponse.sendError(
				status, liferayHttpServletResponseWrapper.getMessage());

			return;
		}

		LiferayHttpServletRequestWrapper liferayHttpServletRequestWrapper =
			LiferayHttpServletRequestWrapper.find(_httpServletRequest);

		try {
			errorLiferayDispatchTargets.setDispatcherType(DispatcherType.ERROR);

			liferayHttpServletRequestWrapper.push(errorLiferayDispatchTargets);

			HttpServletRequest httpServletRequest =
				new HttpServletRequestWrapper(_httpServletRequest) {

					public Object getAttribute(String attributeName) {
						if (getDispatcherType() != DispatcherType.ERROR) {
							return super.getAttribute(attributeName);
						}

						if (attributeName.equals(
								RequestDispatcher.ERROR_MESSAGE)) {

							return liferayHttpServletResponseWrapper.
								getMessage();
						}

						if (attributeName.equals(
								RequestDispatcher.ERROR_REQUEST_URI)) {

							return _httpServletRequest.getRequestURI();
						}

						if (attributeName.equals(
								RequestDispatcher.ERROR_SERVLET_NAME)) {

							EndpointRegistration<?> endpointRegistration =
								_liferayDispatchTargets.
									getServletRegistration();

							return endpointRegistration.getName();
						}

						if (attributeName.equals(
								RequestDispatcher.ERROR_STATUS_CODE)) {

							return status;
						}

						return super.getAttribute(attributeName);
					}

					public DispatcherType getDispatcherType() {
						return DispatcherType.ERROR;
					}

				};

			HttpServletResponse httpServletResponse =
				new LiferayHttpServletResponseWrapper(
					wrappedHttpServletResponse);

			ResponseStateHandler responseStateHandler =
				new ResponseStateHandler(
					httpServletRequest, httpServletResponse,
					errorLiferayDispatchTargets);

			wrappedHttpServletResponse.setStatus(status);

			responseStateHandler.processRequest();
		}
		finally {
			liferayHttpServletRequestWrapper.pop();
		}
	}

	private Exception _exception;
	private final HttpServletRequest _httpServletRequest;
	private final HttpServletResponse _httpServletResponse;
	private final LiferayDispatchTargets _liferayDispatchTargets;

}