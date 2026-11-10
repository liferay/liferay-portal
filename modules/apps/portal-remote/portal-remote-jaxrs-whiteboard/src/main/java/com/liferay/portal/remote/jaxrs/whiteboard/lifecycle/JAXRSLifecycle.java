/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.jaxrs.whiteboard.lifecycle;

import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.remote.jaxrs.whiteboard.internal.JAXRSWhiteboard;

import java.util.Dictionary;
import java.util.Map;

import org.osgi.framework.BundleContext;
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
		_jaxrsWhiteboardDCLSingleton.getSingleton(
			() -> {
				if (_jaxrsReady) {
					return null;
				}

				_jaxrsReady = true;

				return new JAXRSWhiteboard(_bundleContext, _configuration);
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

		_jaxrsWhiteboardDCLSingleton.destroy(JAXRSWhiteboard::close);
	}

	private BundleContext _bundleContext;
	private final Dictionary<String, Object> _configuration =
		new HashMapDictionary<>();
	private boolean _jaxrsReady;
	private final DCLSingleton<JAXRSWhiteboard> _jaxrsWhiteboardDCLSingleton =
		new DCLSingleton<>();

}