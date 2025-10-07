/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.http.servlet.internal.registration;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.osgi.web.http.servlet.internal.HttpServletEndpointController;
import com.liferay.portal.osgi.web.http.servlet.internal.Match;
import com.liferay.portal.osgi.web.http.servlet.internal.constants.HttpServletConstants;
import com.liferay.portal.osgi.web.http.servlet.internal.context.LiferayContextController;
import com.liferay.portal.osgi.web.http.servlet.internal.servlet.FilterChainImpl;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.runtime.dto.FilterDTO;

/**
 * @author Dante Wang
 */
public class FilterRegistration
	extends MatchableRegistration<Filter, FilterDTO>
	implements Comparable<FilterRegistration> {

	public FilterRegistration(
		FilterDTO filterDTO, LiferayContextController liferayContextController,
		int priority, ServiceHolder<Filter> serviceHolder) {

		super(filterDTO, serviceHolder.get());

		_classLoader = serviceHolder.getBundleClassLoader();

		ServiceReference<Filter> serviceReference =
			serviceHolder.getServiceReference();

		String legacyContextFilter = (String)serviceReference.getProperty(
			"equinox.context.select");

		if (legacyContextFilter == null) {
			_initDestroyWithContextController = true;
		}
		else {
			org.osgi.framework.Filter filter = null;

			try {
				filter = FrameworkUtil.createFilter(legacyContextFilter);
			}
			catch (InvalidSyntaxException invalidSyntaxException) {
				if (_log.isDebugEnabled()) {
					_log.debug(invalidSyntaxException);
				}
			}

			_initDestroyWithContextController =
				(filter == null) || liferayContextController.matches(filter);
		}

		_liferayContextController = liferayContextController;
		_priority = priority;
		_serviceHolder = serviceHolder;

		_patterns = _getPatterns(filterDTO);
	}

	public boolean appliesTo(FilterChainImpl filterChainImpl) {
		FilterDTO filterDTO = getDTO();

		DispatcherType dispatcherType = filterChainImpl.getDispatcherType();

		if (Arrays.binarySearch(filterDTO.dispatcher, dispatcherType.name()) >=
				0) {

			return true;
		}

		return false;
	}

	@Override
	public int compareTo(FilterRegistration filterRegistration) {
		int priorityDifference = _priority - filterRegistration._priority;

		if (priorityDifference != 0) {
			return -priorityDifference;
		}

		FilterDTO filterDTO = getDTO();

		FilterDTO otherFilterDTO = filterRegistration.getDTO();

		return Long.compare(
			Math.abs(filterDTO.serviceId), Math.abs(otherFilterDTO.serviceId));
	}

	@Override
	public void destroy() {
		if (!_initDestroyWithContextController) {
			return;
		}

		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				_classLoader)) {

			HttpServletEndpointController httpServletEndpointController =
				_liferayContextController.getHttpServletEndpointController();

			Set<Object> registeredObjects =
				httpServletEndpointController.getRegisteredObjects();

			Filter filter = getService();

			registeredObjects.remove(filter);

			Set<FilterRegistration> filterRegistrations =
				_liferayContextController.getFilterRegistrations();

			filterRegistrations.remove(this);

			_liferayContextController.ungetServletContextHelper(
				_serviceHolder.getBundle());

			super.destroy();

			filter.destroy();
		}
		finally {
			_serviceHolder.release();
		}
	}

	public void doFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws IOException, ServletException {

		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				_classLoader)) {

			Filter filter = getService();

			filter.doFilter(
				httpServletRequest, httpServletResponse, filterChain);
		}
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof FilterRegistration filterRegistration) {
			return Objects.equals(
				getService(), filterRegistration.getService());
		}

		return false;
	}

	@Override
	public int hashCode() {
		FilterDTO filterDTO = getDTO();

		return Long.hashCode(filterDTO.serviceId);
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		if (!_initDestroyWithContextController) {
			return;
		}

		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				_classLoader)) {

			Filter filter = getService();

			filter.init(filterConfig);
		}
	}

	@Override
	public String match(
		String extension, Match match, String name, String pathInfo,
		String servletPath) {

		throw new UnsupportedOperationException();
	}

	public String match(String extension, String name, String requestURI) {
		if ((name != null) && (getDTO().servletNames != null)) {
			for (String servletName : getDTO().servletNames) {
				if (servletName.equals(name)) {
					return name;
				}
			}
		}

		if ((requestURI == null) || requestURI.isEmpty()) {
			return null;
		}

		for (String pattern : getDTO().patterns) {
			if (doPatternMatch(extension, requestURI, pattern)) {
				return pattern;
			}
		}

		for (Pattern pattern : _patterns) {
			Matcher matcher = pattern.matcher(requestURI);

			if (matcher.matches()) {
				return pattern.toString();
			}
		}

		return null;
	}

	protected boolean doPatternMatch(
			String extension, String path, String pattern)
		throws IllegalArgumentException {

		if (pattern.indexOf(HttpServletConstants.SLASH_STAR_DOT) == 0) {
			pattern = pattern.substring(1);
		}

		int extensionMatchIndex = pattern.indexOf(
			HttpServletConstants.SLASH_STAR_DOT);

		String extensionWithPrefixMatch = null;

		if ((extensionMatchIndex >= 0) &&
			(pattern.lastIndexOf(CharPool.SLASH) == extensionMatchIndex)) {

			extensionWithPrefixMatch = pattern.substring(
				extensionMatchIndex + 3);

			pattern = pattern.substring(0, extensionMatchIndex + 2);
		}

		if (pattern.charAt(0) == CharPool.SLASH) {
			if (isPathWildcardMatch(path, pattern)) {
				if (extensionWithPrefixMatch != null) {
					return extensionWithPrefixMatch.equals(extension);
				}

				return true;
			}

			return false;
		}

		if (pattern.charAt(0) == CharPool.STAR) {
			return Objects.equals(pattern.substring(2), extension);
		}

		return false;
	}

	protected boolean isPathWildcardMatch(String path, String pattern) {
		if (path == null) {
			return false;
		}
		else if (pattern.endsWith(HttpServletConstants.SLASH_STAR)) {
			int pathPatternLength = pattern.length() - 2;

			if (!path.regionMatches(0, pattern, 0, pathPatternLength)) {
				return false;
			}

			if ((path.length() <= pathPatternLength) ||
				(path.charAt(pathPatternLength) == CharPool.SLASH)) {

				return true;
			}

			return false;
		}

		return pattern.equals(path);
	}

	private Pattern[] _getPatterns(FilterDTO filterDTO) {
		if (filterDTO.regexs == null) {
			return new Pattern[0];
		}

		return TransformUtil.transform(
			filterDTO.regexs, Pattern::compile, Pattern.class);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FilterRegistration.class);

	private final ClassLoader _classLoader;
	private final boolean _initDestroyWithContextController;
	private final LiferayContextController _liferayContextController;
	private final Pattern[] _patterns;
	private final int _priority;
	private final ServiceHolder<Filter> _serviceHolder;

}