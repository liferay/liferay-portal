/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.jaxrs.whiteboard.lifecycle;

import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.MapUtil;

import jakarta.ws.rs.ext.RuntimeDelegate;

import java.util.Dictionary;
import java.util.Map;

import org.apache.aries.jax.rs.whiteboard.internal.Whiteboard;
import org.apache.cxf.jaxrs.impl.RuntimeDelegateImpl;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Stian Sigvartsen
 */
@Component(
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	service = JAXRSLifecycle.class
)
public class JAXRSLifecycle {

	public void ensureReady() {
		_safeCloseableDCLSingleton.getSingleton(
			() -> {
				if (_jaxrsReady) {
					return null;
				}

				_jaxrsReady = true;

				return _start();
			});
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_bundleContext = bundleContext;

		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			String key = entry.getKey();

			if (key.equals("component.id") || key.equals("component.name")) {
				continue;
			}

			_configuration.put(key, entry.getValue());
		}
	}

	@Deactivate
	protected void deactivate() {
		_jaxrsReady = true;

		_safeCloseableDCLSingleton.destroy(SafeCloseable::close);
	}

	private SafeCloseable _start() {
		RuntimeDelegate.setInstance(new RuntimeDelegateImpl());

		Whiteboard whiteboard = Whiteboard.createWhiteboard(_configuration);

		whiteboard.start(_bundleContext);

		ServiceRegistration<?> serviceRegistration =
			_bundleContext.registerService(
				Object.class, new Object(),
				MapUtil.singletonDictionary(
					"liferay.jaxrs.whiteboard.ready", true));

		return () -> {
			serviceRegistration.unregister();

			whiteboard.stop();

			RuntimeDelegate.setInstance(null);
		};
	}

	private BundleContext _bundleContext;
	private final Dictionary<String, Object> _configuration =
		new HashMapDictionary<>();
	private boolean _jaxrsReady;
	private final DCLSingleton<SafeCloseable> _safeCloseableDCLSingleton =
		new DCLSingleton<>();

}