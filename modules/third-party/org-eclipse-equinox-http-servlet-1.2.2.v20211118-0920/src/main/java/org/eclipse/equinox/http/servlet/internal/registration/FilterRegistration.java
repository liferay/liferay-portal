/*******************************************************************************
 * Copyright (c) 2011, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Raymond Augé <raymond.auge@liferay.com> - Bug 436698
 *******************************************************************************/
package org.eclipse.equinox.http.servlet.internal.registration;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.equinox.http.servlet.internal.context.ContextController;
import org.eclipse.equinox.http.servlet.internal.context.ContextController.ServiceHolder;
import org.eclipse.equinox.http.servlet.internal.servlet.FilterChainImpl;
import org.eclipse.equinox.http.servlet.internal.servlet.Match;
import org.eclipse.equinox.http.servlet.internal.util.Const;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.http.runtime.dto.FilterDTO;

//This class wraps the filter object registered in the HttpService.registerFilter call, to manage the context classloader when handleRequests are being asked.
public class FilterRegistration
	extends MatchableRegistration<Filter, FilterDTO>
	implements Comparable<FilterRegistration> {

	private final ServiceHolder<Filter> filterHolder;
	private final ClassLoader classLoader;
	private final int priority;
	private final ContextController contextController;
	private final boolean initDestoyWithContextController;
	private final Pattern[] compiledRegexs;

	public FilterRegistration(
		ServiceHolder<Filter> filterHolder, FilterDTO filterDTO, int priority,
		ContextController contextController, ClassLoader legacyTCCL) {

		super(filterHolder.get(), filterDTO);
		this.filterHolder = filterHolder;
		this.priority = priority;
		this.contextController = contextController;
		this.compiledRegexs = getCompiledRegex(filterDTO);
		if (legacyTCCL != null) {
			// legacy filter registrations used the current TCCL at registration time
			classLoader = legacyTCCL;
		} else {
			classLoader = filterHolder.getBundle().adapt(BundleWiring.class).getClassLoader();
		}
		String legacyContextFilter = (String) filterHolder.getServiceReference().getProperty(Const.EQUINOX_LEGACY_CONTEXT_SELECT);
		if (legacyContextFilter != null) {
			// This is a legacy Filter registration.
			// This filter tells us the real context controller,
			// backed by an HttpContext that should be used to init/destroy this Filter
			org.osgi.framework.Filter f = null;
			try {
				 f = FrameworkUtil.createFilter(legacyContextFilter);
			}
			catch (InvalidSyntaxException e) {
				// nothing
			}
			initDestoyWithContextController = f == null || contextController.matches(f);
		} else {
			initDestoyWithContextController = true;
		}
	}

	public int compareTo(FilterRegistration otherFilterRegistration) {
		int priorityDifference = priority - otherFilterRegistration.priority;
		if (priorityDifference != 0)
			return -priorityDifference;

		// Note that we use abs here because the DTO service ID may have been negated for legacy filters.
		// We always compare with the positive id values and we know the positive values are unique.
		long thisId = Math.abs(getD().serviceId);
		long otherId = Math.abs(otherFilterRegistration.getD().serviceId);
		return (thisId < otherId) ? -1 : ((thisId == otherId) ? 0 : 1);
	}

	public void destroy() {
		if (!initDestoyWithContextController) {
			return;
		}
		ClassLoader original = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(classLoader);
			contextController.getHttpServletEndpointController().getRegisteredObjects().remove(this.getT());
			contextController.getFilterRegistrations().remove(this);
			contextController.ungetServletContextHelper(filterHolder.getBundle());
			super.destroy();
			getT().destroy();
		}
		finally {
			Thread.currentThread().setContextClassLoader(original);
			filterHolder.release();
		}
	}

	public boolean appliesTo(FilterChainImpl filterChainImpl) {
		return (Arrays.binarySearch(
			getD().dispatcher, filterChainImpl.getDispatcherType().name()) >= 0);
	}

	//Delegate the handling of the request to the actual filter
	public void doFilter(
			HttpServletRequest request, HttpServletResponse response,
			FilterChain chain)
		throws IOException, ServletException {

		ClassLoader original = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(classLoader);
			getT().doFilter(request, response, chain);
		}
		finally {
			Thread.currentThread().setContextClassLoader(original);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof FilterRegistration)) {
			return false;
		}

		FilterRegistration filterRegistration = (FilterRegistration)obj;

		return getT().equals(filterRegistration.getT());
	}

	@Override
	public int hashCode() {
		return Long.valueOf(getD().serviceId).hashCode();
	}

	//Delegate the init call to the actual filter
	public void init(FilterConfig filterConfig) throws ServletException {
		if (!initDestoyWithContextController) {
			return;
		}
		ClassLoader original = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(classLoader);

			getT().init(filterConfig);
		}
		finally {
			Thread.currentThread().setContextClassLoader(original);
		}
	}

	public String match(
		String name, String requestURI, String extension, Match match) {
		if ((name != null) && (getD().servletNames != null)) {
			for (String servletName : getD().servletNames) {
				if (servletName.equals(name)) {
					return name;
				}
			}
		}

		if (requestURI == null || requestURI.isEmpty()) {
			return null;
		}

		for (String pattern : getD().patterns) {
			if (doPatternMatch(pattern, requestURI, extension)) {
				return pattern;
			}
		}

		for (Pattern regex : compiledRegexs) {
			if (regex.matcher(requestURI).matches()) {
				return regex.toString();
			}
		}

		return null;
	}

	@Override
	public String match(
		String name, String servletPath, String pathInfo, String extension, Match match) {
		// TODO need to rework match for filters to remove this method
		throw new UnsupportedOperationException("Should not be calling this method on FilterRegistration"); //$NON-NLS-1$
	}

	protected boolean isPathWildcardMatch(String pattern, String path) {
		if (path == null) {
			return false;
		}
		// first try wild card matching if the pattern requests it
		if (pattern.endsWith("/*")) { //$NON-NLS-1$
			int pathPatternLength = pattern.length() - 2;
			if (path.regionMatches(0, pattern, 0, pathPatternLength)) {
				return path.length() <= pathPatternLength || path.charAt(pathPatternLength) == '/';
			}
			return false;
		}
		// now do exact matching
		return pattern.equals(path);
	}

	protected boolean doPatternMatch(String pattern, String path, String extension)
		throws IllegalArgumentException {

		if (pattern.indexOf(Const.SLASH_STAR_DOT) == 0) {
			pattern = pattern.substring(1);
		}
		int extensionMatchIndex = pattern.indexOf(Const.SLASH_STAR_DOT);
		String extensionWithPrefixMatch = null;
		if (extensionMatchIndex >= 0 && pattern.lastIndexOf('/') == extensionMatchIndex) {
			extensionWithPrefixMatch = pattern.substring(extensionMatchIndex + 3);
			pattern = pattern.substring(0, extensionMatchIndex + 2);
		}

		// first try prefix path matching; taking into account wild cards if necessary
		if ((pattern.charAt(0) == '/')) {
			if (isPathWildcardMatch(pattern, path)) {
				if (extensionWithPrefixMatch != null) {
					return extensionWithPrefixMatch.equals(extension);
				}
				return true;
			}
			return false;
		}

		// next try extension matching if requested
		if (pattern.charAt(0) == '*') {
			return pattern.substring(2).equals(extension);
		}

		// this is really an invalid case that should have gotten caught at registration time
		return false;
	}

	private Pattern[] getCompiledRegex(FilterDTO filterDTO) {
		if (filterDTO.regexs == null) {
			return new Pattern[0];
		}

		Pattern[] patterns = new Pattern[filterDTO.regexs.length];

		for (int i = 0; i < filterDTO.regexs.length; i++) {
			patterns[i] = Pattern.compile(filterDTO.regexs[i]);
		}

		return patterns;
	}

}
/* @generated */