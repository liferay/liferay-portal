/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.jaxrs.whiteboard.client.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.URLUtil;
import com.liferay.portal.remote.jaxrs.whiteboard.lifecycle.JAXRSLifecycle;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Application;

import java.net.URL;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
import org.osgi.framework.PrototypeServiceFactory;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class JaxRsPrototypeResourceRegistrationTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		_jaxrsLifecycle.ensureReady();

		_bundle = FrameworkUtil.getBundle(
			JaxRsPrototypeResourceRegistrationTest.class);

		_bundleContext = _bundle.getBundleContext();

		_serviceRegistrations.add(
			_bundleContext.registerService(
				Application.class, new Application(),
				HashMapDictionaryBuilder.<String, Object>put(
					"liferay.auth.verifier", false
				).put(
					"liferay.oauth2", false
				).put(
					"osgi.jaxrs.application.base", "/" + _APPLICATION_BASE
				).put(
					"osgi.jaxrs.name", _APPLICATION_NAME
				).build()));
	}

	@After
	public void tearDown() {
		for (ServiceRegistration<?> serviceRegistration :
				_serviceRegistrations) {

			serviceRegistration.unregister();
		}
	}

	@Test
	public void testRegisterPrototypeResourceWithComponentName()
		throws Exception {

		_registerGreeter(
			HashMapDictionaryBuilder.<String, Object>put(
				"component.name", Greeter.class.getName()
			).put(
				"osgi.jaxrs.application.select",
				"(osgi.jaxrs.name=" + _APPLICATION_NAME + ")"
			).put(
				"osgi.jaxrs.resource", true
			).build());

		Assert.assertEquals(0, _getServiceCount.get());

		Assert.assertEquals("Hello.", _sayHello());

		Assert.assertEquals(1, _getServiceCount.get());
	}

	@Test
	public void testRegisterPrototypeResourceWithoutComponentName()
		throws Exception {

		_registerGreeter(
			HashMapDictionaryBuilder.<String, Object>put(
				"osgi.jaxrs.application.select",
				"(osgi.jaxrs.name=" + _APPLICATION_NAME + ")"
			).put(
				"osgi.jaxrs.resource", true
			).build());

		Assert.assertEquals("Hello.", _sayHello());
	}

	public static class Greeter {

		@GET
		@Path("/sayHello")
		@Produces("text/plain")
		public String sayHello() {
			return "Hello.";
		}

	}

	private void _registerGreeter(Dictionary<String, Object> properties) {
		_serviceRegistrations.add(
			_bundleContext.registerService(
				Greeter.class,
				new PrototypeServiceFactory<Greeter>() {

					@Override
					public Greeter getService(
						Bundle bundle,
						ServiceRegistration<Greeter> serviceRegistration) {

						if (_bundle.equals(bundle)) {
							_getServiceCount.incrementAndGet();
						}

						return new Greeter();
					}

					@Override
					public void ungetService(
						Bundle bundle,
						ServiceRegistration<Greeter> serviceRegistration,
						Greeter greeter) {
					}

				},
				properties));
	}

	private String _sayHello() throws Exception {
		return URLUtil.toString(
			new URL(
				StringBundler.concat(
					"http://localhost:", PortalUtil.getPortalServerPort(false),
					"/o/", _APPLICATION_BASE, "/sayHello")));
	}

	private static final String _APPLICATION_BASE =
		RandomTestUtil.randomString();

	private static final String _APPLICATION_NAME =
		RandomTestUtil.randomString();

	private Bundle _bundle;
	private BundleContext _bundleContext;
	private final AtomicInteger _getServiceCount = new AtomicInteger();

	@Inject
	private JAXRSLifecycle _jaxrsLifecycle;

	private final List<ServiceRegistration<?>> _serviceRegistrations =
		new ArrayList<>();

}