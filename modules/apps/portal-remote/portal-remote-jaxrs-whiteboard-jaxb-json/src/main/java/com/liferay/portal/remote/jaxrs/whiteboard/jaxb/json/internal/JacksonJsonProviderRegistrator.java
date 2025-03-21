/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.jaxrs.whiteboard.jaxb.json.internal;

import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants;

/**
 * @author Shuyang Zhou
 */
@Component(
	configurationPid = "org.apache.aries.jax.rs.jackson",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	property = {
		JaxrsWhiteboardConstants.JAX_RS_EXTENSION + "=true",
		JaxrsWhiteboardConstants.JAX_RS_MEDIA_TYPE + "=" + MediaType.APPLICATION_JSON,
		JaxrsWhiteboardConstants.JAX_RS_NAME + "=jaxb-json",
		"service.ranking:Integer=" + Integer.MIN_VALUE
	},
	service = {}
)
public class JacksonJsonProviderRegistrator {

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_serviceRegistration = bundleContext.registerService(
			new String[] {
				MessageBodyReader.class.getName(),
				MessageBodyWriter.class.getName()
			},
			new JacksonJsonProviderPrototypeServiceFactory(),
			HashMapDictionaryBuilder.create(
				properties
			).put(
				"jackson.jaxb.version",
				String.valueOf(
					com.fasterxml.jackson.module.jakarta.xmlbind.PackageVersion.
						VERSION)
			).put(
				"jackson.jaxrs.json.version",
				String.valueOf(
					com.fasterxml.jackson.jakarta.rs.json.PackageVersion.
						VERSION)
			).build());
	}

	@Deactivate
	protected void deactivate() {
		_serviceRegistration.unregister();
	}

	private ServiceRegistration<?> _serviceRegistration;

}