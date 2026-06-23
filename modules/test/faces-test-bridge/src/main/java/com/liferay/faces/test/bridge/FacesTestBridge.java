/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.faces.test.bridge;

import com.liferay.faces.test.bridge.configuration.FacesTestBridgeConfiguration;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.cookies.CookiesManager;
import com.liferay.portal.kernel.cookies.constants.CookiesConstants;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;

import java.lang.reflect.Field;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Dante Wang
 */
@Component(
	configurationPid = "com.liferay.faces.test.bridge.configuration.FacesTestBridgeConfiguration",
	service = {}
)
public class FacesTestBridge {

	@Activate
	protected void activate(Map<String, Object> properties) throws Exception {
		FacesTestBridgeConfiguration facesTestBridgeConfiguration =
			ConfigurableUtil.createConfigurable(
				FacesTestBridgeConfiguration.class, properties);

		try {
			Field field = ReflectionUtil.getDeclaredField(
				_cookiesManager.getClass(), "_internalCookies");

			Map<String, Integer> internalCookies =
				(Map<String, Integer>)field.get(_cookiesManager);

			for (String cookieName :
					facesTestBridgeConfiguration.cookieNames()) {

				if (_log.isInfoEnabled()) {
					_log.info("Added cookie " + cookieName);
				}

				internalCookies.put(
					cookieName, CookiesConstants.CONSENT_TYPE_NECESSARY);
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FacesTestBridge.class);

	@Reference
	private CookiesManager _cookiesManager;

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED, unbind = "-")
	private ModuleServiceLifecycle _moduleServiceLifecycle;

}