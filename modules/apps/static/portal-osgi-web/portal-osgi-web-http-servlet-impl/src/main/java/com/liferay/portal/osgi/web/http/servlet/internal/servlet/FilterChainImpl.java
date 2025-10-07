/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.http.servlet.internal.servlet;

import com.liferay.portal.osgi.web.http.servlet.internal.registration.EndpointRegistration;
import com.liferay.portal.osgi.web.http.servlet.internal.registration.FilterRegistration;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.List;

/**
 * @author Dante Wang
 */
public class FilterChainImpl implements FilterChain {

	public FilterChainImpl(
		DispatcherType dispatcherType,
		EndpointRegistration<?> endpointRegistration,
		List<FilterRegistration> filterRegistrations) {

		_dispatcherType = dispatcherType;
		_endpointRegistration = endpointRegistration;
		_filterRegistrations = filterRegistrations;

		_filterCount = filterRegistrations.size();
	}

	@Override
	public void doFilter(
			ServletRequest servletRequest, ServletResponse servletResponse)
		throws IOException, ServletException {

		while (_filterIndex < _filterCount) {
			FilterRegistration filterRegistration = _filterRegistrations.get(
				_filterIndex++);

			if (filterRegistration.appliesTo(this)) {
				filterRegistration.doFilter(
					(HttpServletRequest)servletRequest,
					(HttpServletResponse)servletResponse, this);

				return;
			}
		}

		_endpointRegistration.service(
			(HttpServletRequest)servletRequest,
			(HttpServletResponse)servletResponse);
	}

	public DispatcherType getDispatcherType() {
		return _dispatcherType;
	}

	private final DispatcherType _dispatcherType;
	private final EndpointRegistration<?> _endpointRegistration;
	private final int _filterCount;
	private int _filterIndex;
	private final List<FilterRegistration> _filterRegistrations;

}