/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package org.apache.aries.jax.rs.whiteboard;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.ext.RuntimeDelegate;
import javax.ws.rs.sse.SseEventSource;

import org.apache.aries.jax.rs.whiteboard.internal.client.ClientBuilderFactory;
import org.apache.aries.jax.rs.whiteboard.internal.utils.PropertyHolder;
import org.apache.aries.component.dsl.OSGi;
import org.apache.aries.component.dsl.OSGiResult;
import org.apache.cxf.jaxrs.impl.RuntimeDelegateImpl;
import org.apache.cxf.jaxrs.sse.client.SseEventSourceBuilderImpl;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.http.runtime.HttpServiceRuntime;
import org.osgi.service.jaxrs.client.SseEventSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;
import static org.apache.aries.component.dsl.OSGi.coalesce;
import static org.apache.aries.component.dsl.OSGi.configuration;
import static org.apache.aries.jax.rs.whiteboard.internal.utils.LogUtils.ifInfoEnabled;
import static org.apache.aries.jax.rs.whiteboard.internal.utils.LogUtils.debugTracking;
import static org.apache.aries.jax.rs.whiteboard.internal.utils.Utils.canonicalize;
import static org.apache.aries.jax.rs.whiteboard.internal.Whiteboard.createWhiteboard;
import static org.apache.aries.component.dsl.OSGi.all;
import static org.apache.aries.component.dsl.OSGi.configurations;
import static org.apache.aries.component.dsl.OSGi.effects;
import static org.apache.aries.component.dsl.OSGi.ignore;
import static org.apache.aries.component.dsl.OSGi.just;
import static org.apache.aries.component.dsl.OSGi.once;
import static org.apache.aries.component.dsl.OSGi.register;
import static org.apache.aries.component.dsl.OSGi.serviceReferences;
import static org.osgi.service.http.runtime.HttpServiceRuntimeConstants.HTTP_SERVICE_ENDPOINT;
import static org.osgi.service.http.whiteboard.HttpWhiteboardConstants.HTTP_WHITEBOARD_TARGET;

/**
 * @author Shuyang Zhou
 */
public class WhiteboardUtil {

	public static void start() {
		Bundle bundle = FrameworkUtil.getBundle(WhiteboardUtil.class);

		BundleContext bundleContext = bundle.getBundleContext();

		RuntimeDelegate.setInstance(new RuntimeDelegateImpl());

		if (_log.isDebugEnabled()) {
			_log.debug("Starting the whiteboard factory");
		}

		OSGi<?> runWhiteboards =
			all(
				configurations("org.apache.aries.jax.rs.whiteboard"),
				coalesce(
					configuration("org.apache.aries.jax.rs.whiteboard.default"),
					just(() -> {
						Dictionary<String, Object> properties =
							new Hashtable<>();

						properties.put(
							Constants.SERVICE_PID,
							"org.apache.aries.jax.rs.whiteboard.default");

						return properties;
					})
			)
		).filter(
			c -> !Objects.equals(c.get("enabled"), "false")
		).
		effects(
			debugTracking(_log, () -> "whiteboard configuration")
		).flatMap(configuration ->
			runWhiteboard(bundleContext, configuration)
		);

		_defaultOSGiResult =
			all(
				ignore(registerClient()),
				ignore(registerSseEventSourceFactory()),
				ignore(runWhiteboards)
			)
		.run(bundleContext);

		if (_log.isDebugEnabled()) {
			_log.debug("Whiteboard factory started");
		}
	}

	public static void stop() {
		if (_log.isDebugEnabled()) {
			_log.debug("Stopping whiteboard factory");
		}

		_defaultOSGiResult.close();

		if (_log.isDebugEnabled()) {
			_log.debug("Stopped whiteboard factory");
		}

		RuntimeDelegate.setInstance(null);
	}

	    private static String endpointFilter(PropertyHolder configuration) {
        Object whiteBoardTargetProperty = configuration.get(
            HTTP_WHITEBOARD_TARGET);

        String targetFilter =
            whiteBoardTargetProperty != null ?
                whiteBoardTargetProperty.toString() :
                "(osgi.http.endpoint=*)";


        return format(
            "(&(objectClass=%s)%s)", HttpServiceRuntime.class.getName(),
            targetFilter);
    }

    private static OSGi<?> registerClient() {
        return register(
            ClientBuilder.class, new ClientBuilderFactory(),
            (Map<String, Object>) null).
            effects(
                ifInfoEnabled(_log, () -> "Registered ClientBuilder"),
                ifInfoEnabled(_log, () -> "Unregistered ClientBuilder")
            );
    }

    private static OSGi<?> registerSseEventSourceFactory() {
        return register(
            SseEventSourceFactory.class, new SseEventSourceFactory() {
                @Override
                public SseEventSource.Builder newBuilder(WebTarget target) {
                    return new SseEventSourceBuilderImpl(){{target(target);}};
                }

                @Override
                public SseEventSource newSource(WebTarget target) {
                    return newBuilder(target).build();
                }
            },
            new Hashtable<>()).
            effects(
                ifInfoEnabled(_log, () -> "Registered SseEventSourceFactory"),
                ifInfoEnabled(_log, () -> "Unregistered SseEventSourceFactory")
            );
    }

    private static OSGi<?> runWhiteboard(
        BundleContext bundleContext, Dictionary<String, ?> configuration) {

        OSGi<List<String>> endpoints =
            serviceReferences(endpointFilter(configuration::get)
            ).map(
                r -> Arrays.asList(
                    canonicalize(r.getProperty(HTTP_SERVICE_ENDPOINT)))
            );

        return
            once(
                serviceReferences(
                    endpointFilter(configuration::get),
                    __ -> false //never reload
                ).then(
                    just(createWhiteboard(configuration)).
                    effects(
                        ifInfoEnabled(
                            _log,
                            () -> "created whiteboard from configuration: " +
                                configuration),
                        ifInfoEnabled(
                            _log,
                            () -> "destroyed whiteboard from configuration: " +
                                configuration)
                    )
                .flatMap(
                    whiteboard ->
                        all(
                            effects(
                                () -> whiteboard.start(bundleContext),
                                whiteboard::stop),
                            ignore(
                                endpoints.effects(
                                    whiteboard::addHttpEndpoints,
                                    whiteboard::removeHttpEndpoints)
                            ).
                            effects(
                                debugTracking(
                                    _log,
                                    () -> "endpoint for whiteboard: " +
                                        whiteboard)
                            )
                        )
                ))
            );
    }

	private static final Logger _log = LoggerFactory.getLogger(
        WhiteboardUtil.class);

	private static OSGiResult _defaultOSGiResult;

}
/* @generated */