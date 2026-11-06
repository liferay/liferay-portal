/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.jaxrs.whiteboard.lifecycle;

import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.MapUtil;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.ext.RuntimeDelegate;
import jakarta.ws.rs.sse.SseEventSource;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.aries.component.dsl.OSGi;
import org.apache.aries.component.dsl.OSGiResult;
import org.apache.aries.jax.rs.whiteboard.internal.Whiteboard;
import org.apache.aries.jax.rs.whiteboard.internal.client.ClientBuilderFactory;
import org.apache.aries.jax.rs.whiteboard.internal.utils.Utils;
import org.apache.cxf.jaxrs.impl.RuntimeDelegateImpl;
import org.apache.cxf.jaxrs.sse.client.SseEventSourceBuilderImpl;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.http.runtime.HttpServiceRuntime;
import org.osgi.service.http.runtime.HttpServiceRuntimeConstants;
import org.osgi.service.jaxrs.client.SseEventSourceFactory;

/**
 * @author Stian Sigvartsen
 */
@Component(service = JAXRSLifecycle.class)
public class JAXRSLifecycle {

	public void ensureReady() {
		if (_jaxrsReady) {
			return;
		}

		_jaxrsReady = true;

		_serviceRegistrationDCLSingleton.getSingleton(
			() -> {
				_start();

				return _bundleContext.registerService(
					Object.class, new Object(),
					MapUtil.singletonDictionary(
						"liferay.jaxrs.whiteboard.ready", true));
			});
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	@Deactivate
	protected void deactivate() {
		_serviceRegistrationDCLSingleton.destroy(
			serviceRegistration -> {
				serviceRegistration.unregister();

				_stop();
			});
	}

	private String _endpointFilter() {
		return StringBundler.concat(
			"(&(objectClass=", HttpServiceRuntime.class.getName(),
			")(osgi.http.endpoint=*))");
	}

	private OSGi<?> _registerClient() {
		return OSGi.register(
			ClientBuilder.class, new ClientBuilderFactory(),
			(Map<String, Object>)null);
	}

	private OSGi<?> _registerSseEventSourceFactory() {
		return OSGi.register(
			SseEventSourceFactory.class,
			new SseEventSourceFactory() {

				@Override
				public SseEventSource.Builder newBuilder(WebTarget target) {
					return new SseEventSourceBuilderImpl() {
						{
							target(target);
						}
					};
				}

				@Override
				public SseEventSource newSource(WebTarget target) {
					return newBuilder(
						target
					).build();
				}

			},
			new Hashtable<>());
	}

	private OSGi<?> _runWhiteboard(Dictionary<String, ?> configuration) {
		OSGi<List<String>> endpoints = OSGi.serviceReferences(
			_endpointFilter()
		).map(
			r -> Arrays.asList(
				Utils.canonicalize(
					r.getProperty(
						HttpServiceRuntimeConstants.HTTP_SERVICE_ENDPOINT)))
		);

		return OSGi.once(
			OSGi.serviceReferences(
				_endpointFilter(), __ -> false
			).then(
				OSGi.just(
					Whiteboard.createWhiteboard(configuration)
				).flatMap(
					whiteboard -> OSGi.all(
						OSGi.effects(
							() -> whiteboard.start(_bundleContext),
							whiteboard::stop),
						OSGi.ignore(
							endpoints.effects(
								whiteboard::addHttpEndpoints,
								whiteboard::removeHttpEndpoints)))
				)
			));
	}

	private void _start() {
		RuntimeDelegate.setInstance(new RuntimeDelegateImpl());

		OSGi<?> runWhiteboards = OSGi.coalesce(
			OSGi.configuration("org.apache.aries.jax.rs.whiteboard.default"),
			OSGi.just(
				() -> {
					Dictionary<String, Object> properties = new Hashtable<>();

					properties.put(
						Constants.SERVICE_PID,
						"org.apache.aries.jax.rs.whiteboard.default");

					return properties;
				})
		).filter(
			c -> !Objects.equals(c.get("enabled"), "false")
		).flatMap(
			configuration -> _runWhiteboard(configuration)
		);

		_osgiResult = OSGi.all(
			OSGi.ignore(_registerClient()),
			OSGi.ignore(_registerSseEventSourceFactory()),
			OSGi.ignore(runWhiteboards)
		).run(
			_bundleContext
		);
	}

	private void _stop() {
		_osgiResult.close();

		RuntimeDelegate.setInstance(null);
	}

	private BundleContext _bundleContext;
	private boolean _jaxrsReady;
	private OSGiResult _osgiResult;
	private final DCLSingleton<ServiceRegistration<?>>
		_serviceRegistrationDCLSingleton = new DCLSingleton<>();

}