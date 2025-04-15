/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.rest.extender.client.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.URLUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.ws.rs.core.Application;

import java.net.URL;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.endpoint.ServerRegistry;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Carlos Sierra Andrés
 */
@RunWith(Arquillian.class)
public class JaxRsComponentRegistrationTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(
			JaxRsComponentRegistrationTest.class);

		_bundleContext = bundle.getBundleContext();

		ServiceReference<ConfigurationAdmin> serviceReference =
			_bundleContext.getServiceReference(ConfigurationAdmin.class);

		try {
			ConfigurationAdmin configurationAdmin = _bundleContext.getService(
				serviceReference);

			_cxfConfiguration = configurationAdmin.createFactoryConfiguration(
				"com.liferay.portal.remote.cxf.common.configuration." +
					"CXFEndpointPublisherConfiguration",
				null);

			Dictionary<String, Object> properties = new Hashtable<>();

			properties.put("contextPath", "/rest-test");

			_cxfConfiguration.update(properties);

			_restConfiguration = configurationAdmin.createFactoryConfiguration(
				"com.liferay.portal.remote.rest.extender.configuration." +
					"RestExtenderConfiguration",
				null);

			properties = new Hashtable<>();

			properties.put("contextPaths", new String[] {"/rest-test"});
			properties.put(
				"jaxRsApplicationFilterStrings",
				new String[] {"(jaxrs.application=true)"});

			_restConfiguration.update(properties);

			properties = new Hashtable<>();

			properties.put("jaxrs.application", true);

			_serviceRegistration = _bundleContext.registerService(
				Application.class, new Greeter(), properties);

			ServiceTracker<Bus, Bus> serviceTracker =
				ServiceTrackerFactory.open(
					_bundleContext,
					StringBundler.concat(
						"(&(objectClass=", Bus.class.getName(), ")(",
						HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH,
						"=/rest-test))"));

			Bus bus = serviceTracker.waitForService(10000L);

			if (bus == null) {
				throw new IllegalStateException(
					"Bus was not registered within 10 seconds");
			}

			ServerRegistry serverRegistry = bus.getExtension(
				ServerRegistry.class);

			List<Server> servers = null;

			for (int i = 0; i <= 100; i++) {
				servers = serverRegistry.getServers();

				if (!servers.isEmpty()) {
					break;
				}

				Thread.sleep(100);
			}

			if (servers.isEmpty()) {
				_cleanUp();

				throw new IllegalStateException(
					"Endpoint was not registered within 10 seconds");
			}
		}
		finally {
			_bundleContext.ungetService(serviceReference);
		}
	}

	@After
	public void tearDown() throws Exception {
		_cleanUp();
	}

	@Test
	public void testIsRegistered() throws Exception {
		URL url = new URL("http://localhost:8080/o/rest-test/testApp/sayHello");

		Assert.assertEquals("Hello.", URLUtil.toString(url));
	}

	@Test(expected = Exception.class)
	public void testServiceListIsUnavailable() throws Exception {
		URL url = new URL("http://localhost:8080/o/rest-test/services");

		URLUtil.toString(url);
	}

	private void _cleanUp() throws Exception {
		final CountDownLatch countDownLatch = new CountDownLatch(1);

		ServiceTracker<ServletContextHelper, ServletContextHelper>
			serviceTracker =
				new ServiceTracker<ServletContextHelper, ServletContextHelper>(
					_bundleContext, ServletContextHelper.class, null) {

					@Override
					public void removedService(
						ServiceReference<ServletContextHelper> serviceReference,
						ServletContextHelper service) {

						Object contextName = serviceReference.getProperty(
							HttpWhiteboardConstants.
								HTTP_WHITEBOARD_CONTEXT_NAME);

						if (Objects.equals(contextName, "rest-test")) {
							countDownLatch.countDown();

							close();
						}
					}

				};

		serviceTracker.open();

		try {
			_serviceRegistration.unregister();
		}
		catch (Exception exception) {
		}

		try {
			_restConfiguration.delete();
		}
		catch (Exception exception) {
		}

		try {
			_cxfConfiguration.delete();
		}
		catch (Exception exception) {
		}

		if (!countDownLatch.await(10, TimeUnit.MINUTES)) {
			throw new TimeoutException("Service unregister waiting timeout");
		}
	}

	private BundleContext _bundleContext;
	private Configuration _cxfConfiguration;
	private Configuration _restConfiguration;
	private ServiceRegistration<Application> _serviceRegistration;

}