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
import javax.servlet.Servlet;

import org.eclipse.equinox.http.servlet.internal.HttpServletEndpointController;
import org.eclipse.equinox.http.servlet.internal.context.ContextController;
import org.eclipse.equinox.http.servlet.internal.error.HttpWhiteboardFailureException;
import org.eclipse.equinox.http.servlet.internal.registration.ServletRegistration;
import org.osgi.framework.*;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

/**
 * @author Raymond Augé
 */
public class ContextServletTrackerCustomizer
	extends RegistrationServiceTrackerCustomizer<Servlet, AtomicReference<ServletRegistration>> {

	public ContextServletTrackerCustomizer(
		BundleContext bundleContext, HttpServletEndpointController httpServletEndpointController,
		ContextController contextController) {

		super(bundleContext, httpServletEndpointController);

		this.contextController = contextController;
	}

	@Override
	public AtomicReference<ServletRegistration> addingService(
		ServiceReference<Servlet> serviceReference) {

		if ((serviceReference.getProperty(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ERROR_PAGE) == null) &&
			(serviceReference.getProperty(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME) == null) &&
			(serviceReference.getProperty(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN) == null)) {

			return null;
		}

		if (!contextController.matches(serviceReference)) {
			return null;
		}

		if (!httpServletEndpointController.matches(serviceReference)) {
			return null;
		}

		AtomicReference<ServletRegistration> result = new AtomicReference<ServletRegistration>();

		try {
			result.set(contextController.addServletRegistration(serviceReference));
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
		ServiceReference<Servlet> serviceReference,
		AtomicReference<ServletRegistration> servletReference) {

		removedService(serviceReference, servletReference);
		AtomicReference<ServletRegistration> added = addingService(serviceReference);
		servletReference.set(added.get());
	}

	@Override
	public void removedService(
		ServiceReference<Servlet> serviceReference,
		AtomicReference<ServletRegistration> servletReference) {
		ServletRegistration registration = servletReference.get();
		if (registration != null) {
			// destroy will unget the service object we were using
			registration.destroy();
		}
	}

	private ContextController contextController;

}
/* @generated */