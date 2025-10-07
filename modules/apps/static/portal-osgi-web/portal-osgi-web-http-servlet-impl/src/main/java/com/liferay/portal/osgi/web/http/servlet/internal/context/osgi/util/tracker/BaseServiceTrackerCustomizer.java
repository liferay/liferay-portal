/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.http.servlet.internal.context.osgi.util.tracker;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.osgi.web.http.servlet.internal.HttpServletEndpointController;
import com.liferay.portal.osgi.web.http.servlet.internal.constants.HttpServletConstants;
import com.liferay.portal.osgi.web.http.servlet.internal.context.LiferayContextController;
import com.liferay.portal.osgi.web.http.servlet.internal.context.ServletContextHelperDataContext;
import com.liferay.portal.osgi.web.http.servlet.internal.registration.Registration;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Dante Wang
 */
public abstract class BaseServiceTrackerCustomizer
	<S, R extends Registration<?, ?>, T extends AtomicReference<R>>
		implements ServiceTrackerCustomizer<S, T> {

	public BaseServiceTrackerCustomizer(
		BundleContext bundleContext,
		HttpServletEndpointController httpServletEndpointController,
		LiferayContextController liferayContextController,
		ServletContextHelperDataContext servletContextHelperDataContext,
		long servletContextHelperServiceId) {

		this.bundleContext = bundleContext;
		this.httpServletEndpointController = httpServletEndpointController;
		this.liferayContextController = liferayContextController;
		this.servletContextHelperDataContext = servletContextHelperDataContext;
		this.servletContextHelperServiceId = servletContextHelperServiceId;
	}

	@Override
	public void modifiedService(
		ServiceReference<S> serviceReference, T registrationAtomicReference) {

		removedService(serviceReference, registrationAtomicReference);

		T newRegistrationAtomicReference = addingService(serviceReference);

		registrationAtomicReference.set(newRegistrationAtomicReference.get());
	}

	@Override
	public void removedService(
		ServiceReference<S> serviceReference, T registrationAtomicReference) {

		Registration<?, ?> registration = registrationAtomicReference.get();

		if (registration != null) {
			registration.destroy();
		}
	}

	protected void checkPattern(String pattern) {
		if (pattern == null) {
			throw new IllegalArgumentException("Pattern must not be null");
		}
		else if (pattern.indexOf(HttpServletConstants.STAR_DOT) != 0) {
			if (!pattern.startsWith(StringPool.SLASH) ||
				(pattern.endsWith(StringPool.SLASH) &&
				 !pattern.equals(StringPool.SLASH))) {

				throw new IllegalArgumentException(
					"Invalid pattern: " + pattern);
			}
		}
	}

	protected String[] sort(String[] values) {
		if (values == null) {
			return null;
		}

		Arrays.sort(values);

		return values;
	}

	protected final BundleContext bundleContext;
	protected final HttpServletEndpointController httpServletEndpointController;
	protected final LiferayContextController liferayContextController;
	protected final ServletContextHelperDataContext
		servletContextHelperDataContext;
	protected final long servletContextHelperServiceId;

}