/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.jaxrs.whiteboard.internal;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.MapUtil;

import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.ext.RuntimeDelegate;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import org.apache.aries.component.dsl.CachingServiceReference;
import org.apache.aries.component.dsl.OSGi;
import org.apache.aries.component.dsl.OSGiResult;
import org.apache.aries.jax.rs.whiteboard.internal.AriesJaxrsServiceRuntime;
import org.apache.aries.jax.rs.whiteboard.internal.DefaultApplication;
import org.apache.aries.jax.rs.whiteboard.internal.Registry;
import org.apache.aries.jax.rs.whiteboard.internal.Whiteboard;
import org.apache.cxf.jaxrs.impl.RuntimeDelegateImpl;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.jaxrs.runtime.JaxrsServiceRuntime;
import org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants;

/**
 * @author Shuyang Zhou
 */
public class JAXRSWhiteboard implements SafeCloseable {

	public JAXRSWhiteboard(
		BundleContext bundleContext, Dictionary<String, Object> configuration) {

		RuntimeDelegate.setInstance(new RuntimeDelegateImpl());

		try {
			Whiteboard whiteboard = Whiteboard.createWhiteboard(configuration);

			_bundleContextField.set(whiteboard, bundleContext);

			_jaxrsServiceTracker = new JAXRSServiceTracker(
				_runPipeline(bundleContext, _applicationsMethod, whiteboard),
				bundleContext,
				_runPipeline(
					bundleContext, _applicationExtensionsMethod, whiteboard),
				_runPipeline(
					bundleContext, _applicationResourcesMethod, whiteboard));

			_jaxrsServiceRuntimeServiceRegistration =
				bundleContext.registerService(
					JaxrsServiceRuntime.class,
					new JAXRSServiceRuntime(
						(AriesJaxrsServiceRuntime)_runtimeField.get(whiteboard),
						_jaxrsServiceTracker),
					configuration);

			_runtimeReferenceField.set(
				whiteboard,
				_jaxrsServiceRuntimeServiceRegistration.getReference());

			_defaultApplicationServiceRegistration =
				bundleContext.registerService(
					Application.class, new DefaultApplication(),
					_getDefaultApplicationProperties(configuration));

			_jaxrsServiceTracker.open(_jaxrsServiceRuntimeServiceRegistration);

			_registry = (Registry<?>)_applicationRegistryField.get(whiteboard);
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			throw ReflectionUtil.<RuntimeException>throwException(
				reflectiveOperationException);
		}

		_readyServiceRegistration = bundleContext.registerService(
			Object.class, new Object(),
			MapUtil.singletonDictionary(
				"liferay.jaxrs.whiteboard.ready", true));
	}

	@Override
	public void close() {
		_readyServiceRegistration.unregister();

		_jaxrsServiceTracker.close();

		for (OSGiResult osgiResult : _osgiResults) {
			osgiResult.close();
		}

		_defaultApplicationServiceRegistration.unregister();

		_jaxrsServiceRuntimeServiceRegistration.unregister();

		_registry.close();

		RuntimeDelegate.setInstance(null);
	}

	private Dictionary<String, Object> _getDefaultApplicationProperties(
		Dictionary<String, Object> configuration) {

		return HashMapDictionaryBuilder.<String, Object>putAll(
			configuration
		).put(
			Constants.SERVICE_RANKING, Integer.MIN_VALUE
		).put(
			JaxrsWhiteboardConstants.JAX_RS_APPLICATION_BASE,
			() -> {
				Object defaultApplicationBase = configuration.get(
					"default.application.base");

				if (defaultApplicationBase instanceof String) {
					return defaultApplicationBase;
				}

				return "/";
			}
		).put(
			JaxrsWhiteboardConstants.JAX_RS_NAME, ".default"
		).build();
	}

	private Function<CachingServiceReference<Object>, Runnable> _runPipeline(
			BundleContext bundleContext, Method method, Whiteboard whiteboard)
		throws ReflectiveOperationException {

		AtomicReference<Function<CachingServiceReference<Object>, Runnable>>
			functionReference = new AtomicReference<>();

		OSGi<?> osgi = (OSGi<?>)method.invoke(
			whiteboard,
			OSGi.fromOsgiRunnable(
				(curBundleContext, publisher) -> {
					functionReference.set(publisher::publish);

					return () -> {
					};
				}));

		_osgiResults.add(osgi.run(bundleContext));

		return functionReference.get();
	}

	private static final Method _applicationExtensionsMethod;
	private static final Field _applicationRegistryField;
	private static final Method _applicationResourcesMethod;
	private static final Method _applicationsMethod;
	private static final Field _bundleContextField;
	private static final Field _runtimeField;
	private static final Field _runtimeReferenceField;

	static {
		try {
			_applicationExtensionsMethod = ReflectionUtil.getDeclaredMethod(
				Whiteboard.class, "applicationExtensions", OSGi.class);
			_applicationRegistryField = ReflectionUtil.getDeclaredField(
				Whiteboard.class, "_applicationRegistry");
			_applicationResourcesMethod = ReflectionUtil.getDeclaredMethod(
				Whiteboard.class, "applicationResources", OSGi.class);
			_applicationsMethod = ReflectionUtil.getDeclaredMethod(
				Whiteboard.class, "applications", OSGi.class);
			_bundleContextField = ReflectionUtil.getDeclaredField(
				Whiteboard.class, "_bundleContext");
			_runtimeField = ReflectionUtil.getDeclaredField(
				Whiteboard.class, "_runtime");
			_runtimeReferenceField = ReflectionUtil.getDeclaredField(
				Whiteboard.class, "_runtimeReference");
		}
		catch (Exception exception) {
			throw new ExceptionInInitializerError(exception);
		}
	}

	private final ServiceRegistration<Application>
		_defaultApplicationServiceRegistration;
	private final ServiceRegistration<JaxrsServiceRuntime>
		_jaxrsServiceRuntimeServiceRegistration;
	private final JAXRSServiceTracker _jaxrsServiceTracker;
	private final List<OSGiResult> _osgiResults = new ArrayList<>();
	private final ServiceRegistration<?> _readyServiceRegistration;
	private final Registry<?> _registry;

}