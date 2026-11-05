/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.auth.verifier.internal;

import com.liferay.portal.kernel.security.auth.verifier.AuthVerifier;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifierConfiguration;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.security.auth.AuthVerifierPipeline;

import java.util.Map;
import java.util.Properties;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Tomas Polesovsky
 * @author Arthur Chan
 */
public abstract class BaseAuthVerifierPipelineConfigurator {

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		boolean enabled = GetterUtil.getBoolean(properties.get("enabled"));

		if (!enabled) {
			return;
		}

		Class<?> clazz = getAuthVerifierClass();

		String authVerifierPropertyName =
			AuthVerifierPipeline.getAuthVerifierPropertyName(clazz.getName());

		Properties translatedProperties = new Properties();

		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			translatedProperties.setProperty(
				translateKey(authVerifierPropertyName, entry.getKey()),
				String.valueOf(entry.getValue()));
		}

		AuthVerifierConfiguration authVerifierConfiguration =
			new AuthVerifierConfiguration();

		authVerifierConfiguration.setAuthVerifierClassName(clazz.getName());
		authVerifierConfiguration.setProperties(translatedProperties);

		_serviceRegistration = bundleContext.registerService(
			AuthVerifierConfiguration.class, authVerifierConfiguration, null);
	}

	@Deactivate
	protected void deactivate() {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	protected abstract Class<? extends AuthVerifier> getAuthVerifierClass();

	protected String translateKey(String authVerifierPropertyName, String key) {
		if (key.equals("hostsAllowed")) {
			key = "hosts.allowed";
		}
		else if (key.equals("urlsExcludes")) {
			key = "urls.excludes";
		}
		else if (key.equals("urlsIncludes")) {
			key = "urls.includes";
		}

		return key;
	}

	private ServiceRegistration<AuthVerifierConfiguration> _serviceRegistration;

}