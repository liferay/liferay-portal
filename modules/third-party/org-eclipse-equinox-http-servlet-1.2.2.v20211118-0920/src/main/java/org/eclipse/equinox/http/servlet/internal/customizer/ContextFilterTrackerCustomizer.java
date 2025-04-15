/*******************************************************************************
 * Copyright (c) 2014 Raymond Augé and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Raymond Augé <raymond.auge@liferay.com> - Bug 436698
 ******************************************************************************/

package org.eclipse.equinox.http.servlet.internal.customizer;

import java.util.concurrent.atomic.AtomicReference;
import javax.servlet.Filter;

import org.eclipse.equinox.http.servlet.internal.HttpServletEndpointController;
import org.eclipse.equinox.http.servlet.internal.context.ContextController;
import org.eclipse.equinox.http.servlet.internal.error.HttpWhiteboardFailureException;
import org.eclipse.equinox.http.servlet.internal.registration.FilterRegistration;
import org.osgi.framework.*;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

/**
 * @author Raymond Augé
 */
public class ContextFilterTrackerCustomizer
	extends RegistrationServiceTrackerCustomizer<Filter, AtomicReference<FilterRegistration>> {

	public ContextFilterTrackerCustomizer(
		BundleContext bundleContext, HttpServletEndpointController httpServletEndpointController,
		ContextController contextController) {

		super(bundleContext, httpServletEndpointController);

		this.contextController = contextController;
	}

	@Override
	public AtomicReference<FilterRegistration> addingService(
		ServiceReference<Filter> serviceReference) {

		if ((serviceReference.getProperty(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN) == null) &&
			(serviceReference.getProperty(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_REGEX) == null) &&
			(serviceReference.getProperty(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_SERVLET) == null)) {

			return null;
		}

		if (!contextController.matches(serviceReference)) {
			return null;
		}

		if (!httpServletEndpointController.matches(serviceReference)) {
			return null;
		}

		AtomicReference<FilterRegistration> result = new AtomicReference<FilterRegistration>();

		try {
			result.set(contextController.addFilterRegistration(serviceReference));
		}
		catch (HttpWhiteboardFailureException hwfe) {
			httpServletEndpointController.log(hwfe.getMessage(), hwfe);
		}
		catch (Exception e) {
			httpServletEndpointController.log(e.getMessage(), e);
		}

		return result;
	}

	@Override
	public void modifiedService(
		ServiceReference<Filter> serviceReference,
		AtomicReference<FilterRegistration> filterReference) {

		removedService(serviceReference, filterReference);
		AtomicReference<FilterRegistration> added = addingService(serviceReference);
		filterReference.set(added.get());
	}

	@Override
	public void removedService(
		ServiceReference<Filter> serviceReference,
		AtomicReference<FilterRegistration> filterReference) {
		FilterRegistration registration = filterReference.get();
		if (registration != null) {
			// Destroy now ungets the object we are using
			registration.destroy();
		}
	}

	private ContextController contextController;

}
/* @generated */