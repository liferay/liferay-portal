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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.util.Dictionary;

import org.apache.aries.component.dsl.OSGi;
import org.apache.aries.component.dsl.OSGiResult;
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

			_jaxrsServiceRuntimeServiceRegistration =
				bundleContext.registerService(
					JaxrsServiceRuntime.class,
					(JaxrsServiceRuntime)_runtimeField.get(whiteboard),
					configuration);

			_runtimeReferenceField.set(
				whiteboard,
				_jaxrsServiceRuntimeServiceRegistration.getReference());

			_counterField.set(
				whiteboard,
				_serviceRegistrationChangeCounterConstructor.newInstance(
					_jaxrsServiceRuntimeServiceRegistration));

			_defaultApplicationServiceRegistration =
				bundleContext.registerService(
					Application.class, new DefaultApplication(),
					_getDefaultApplicationProperties(configuration));

			OSGi<?> osgi = (OSGi<?>)_getAllServicesMethod.invoke(whiteboard);

			_osgiResult = osgi.run(bundleContext);

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

		_osgiResult.close();

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
		).put(
			JaxrsWhiteboardConstants.JAX_RS_WHITEBOARD_TARGET,
			"(service.pid=" + configuration.get(Constants.SERVICE_PID) + ")"
		).build();
	}

	private static final Field _applicationRegistryField;
	private static final Field _bundleContextField;
	private static final Field _counterField;
	private static final Method _getAllServicesMethod;
	private static final Field _runtimeField;
	private static final Field _runtimeReferenceField;
	private static final Constructor<?>
		_serviceRegistrationChangeCounterConstructor;

	static {
		try {
			_applicationRegistryField = ReflectionUtil.getDeclaredField(
				Whiteboard.class, "_applicationRegistry");
			_bundleContextField = ReflectionUtil.getDeclaredField(
				Whiteboard.class, "_bundleContext");
			_counterField = ReflectionUtil.getDeclaredField(
				Whiteboard.class, "_counter");
			_getAllServicesMethod = ReflectionUtil.getDeclaredMethod(
				Whiteboard.class, "getAllServices");
			_runtimeField = ReflectionUtil.getDeclaredField(
				Whiteboard.class, "_runtime");
			_runtimeReferenceField = ReflectionUtil.getDeclaredField(
				Whiteboard.class, "_runtimeReference");

			Class<?> serviceRegistrationChangeCounterClass =
				_counterField.getType();

			_serviceRegistrationChangeCounterConstructor =
				serviceRegistrationChangeCounterClass.getDeclaredConstructor(
					ServiceRegistration.class);

			_serviceRegistrationChangeCounterConstructor.setAccessible(true);
		}
		catch (Exception exception) {
			throw new ExceptionInInitializerError(exception);
		}
	}

	private final ServiceRegistration<Application>
		_defaultApplicationServiceRegistration;
	private final ServiceRegistration<JaxrsServiceRuntime>
		_jaxrsServiceRuntimeServiceRegistration;
	private final OSGiResult _osgiResult;
	private final ServiceRegistration<?> _readyServiceRegistration;
	private final Registry<?> _registry;

}