/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.builder.test.util;

import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;

import jakarta.ws.rs.core.Application;

import org.junit.Assert;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Carlos Correa
 */
public class APIApplicationTestUtil {

	public static void assertDeployedAPIApplication(String baseURL)
		throws Exception {

		ServiceTracker<Application, Application> serviceTracker =
			_getServiceTracker(baseURL);

		try {
			Assert.assertNotNull(
				"The API application is not deployed",
				serviceTracker.waitForService(10000));
			Assert.assertEquals(
				200,
				HTTPTestUtil.invokeToHttpCode(
					null, "c/" + baseURL + "/openapi.json", Http.Method.GET));
		}
		finally {
			serviceTracker.close();
		}
	}

	public static void assertNotDeployedAPIApplication(String baseURL)
		throws Exception {

		ServiceTracker<Application, Application> serviceTracker =
			_getServiceTracker(baseURL);

		try {
			Assert.assertEquals(
				"The API application is deployed", 0, serviceTracker.size());

			try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					"portal_web.docroot.errors.code_jsp",
					LoggerTestUtil.WARN)) {

				Assert.assertEquals(
					404,
					HTTPTestUtil.invokeToHttpCode(
						null, "c/" + baseURL + "/openapi.json",
						Http.Method.GET));
			}
		}
		finally {
			serviceTracker.close();
		}
	}

	private static ServiceTracker<Application, Application> _getServiceTracker(
		String baseURL) {

		Bundle testBundle = FrameworkUtil.getBundle(
			APIApplicationTestUtil.class);

		return ServiceTrackerFactory.open(
			testBundle.getBundleContext(),
			StringBundler.concat(
				"(&(objectClass=", Application.class.getName(),
				")(liferay.headless.builder.application=true)",
				"(osgi.jaxrs.application.base=/c/", baseURL, "))"));
	}

}