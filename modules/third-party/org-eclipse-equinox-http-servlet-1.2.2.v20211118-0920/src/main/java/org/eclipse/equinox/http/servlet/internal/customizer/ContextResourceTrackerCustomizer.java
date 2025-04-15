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

import org.eclipse.equinox.http.servlet.internal.HttpServletEndpointController;
import org.eclipse.equinox.http.servlet.internal.context.ContextController;
import org.eclipse.equinox.http.servlet.internal.error.HttpWhiteboardFailureException;
import org.eclipse.equinox.http.servlet.internal.registration.ResourceRegistration;
import org.osgi.framework.*;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

/**
 * @author Raymond Augé
 */
public class ContextResourceTrackerCustomizer
	extends RegistrationServiceTrackerCustomizer<Object, AtomicReference<ResourceRegistration>> {

	public ContextResourceTrackerCustomizer(
		BundleContext bundleContext, HttpServletEndpointController httpServletEndpointController,
		ContextController contextController) {

		super(bundleContext, httpServletEndpointController);

		this.contextController = contextController;
	}

	@Override
	public AtomicReference<ResourceRegistration> addingService(
		ServiceReference<Object> serviceReference) {

		if ((serviceReference.getProperty(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PREFIX) == null) &&
			(serviceReference.getProperty(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PATTERN) == null)) {

			return null;
		}

		if (!contextController.matches(serviceReference)) {
			return null;
		}

		if (!httpServletEndpointController.matches(serviceReference)) {
			return null;
		}

		AtomicReference<ResourceRegistration> result = new AtomicReference<ResourceRegistration>();

		try {
			result.set(contextController.addResourceRegistration(serviceReference));
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
		ServiceReference<Object> serviceReference,
		AtomicReference<ResourceRegistration> resourceReference) {

		removedService(serviceReference, resourceReference);
		AtomicReference<ResourceRegistration> added = addingService(serviceReference);
		resourceReference.set(added.get());
	}

	@Override
	public void removedService(
		ServiceReference<Object> serviceReference,
		AtomicReference<ResourceRegistration> resourceReference) {
		ResourceRegistration registration = resourceReference.get();
		if (registration != null) {
			// destroy will unget the service object we were using
			registration.destroy();
		}
	}

	private ContextController contextController;

}
/* @generated */