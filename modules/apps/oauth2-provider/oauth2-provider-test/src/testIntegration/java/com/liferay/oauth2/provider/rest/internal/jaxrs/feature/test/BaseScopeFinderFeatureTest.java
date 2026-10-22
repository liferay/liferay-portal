/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.jaxrs.feature.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.scope.RequiresScope;
import com.liferay.oauth2.provider.scope.spi.scope.finder.ScopeFinder;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.JAXRSWhiteboardTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Application;

import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class BaseScopeFinderFeatureTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		JAXRSWhiteboardTestUtil.ensureReady();
	}

	@Test
	public void testStartServerWithAnnotations() throws Exception {
		_testStartServer(
			HashMapDictionaryBuilder.<String, Object>put(
				"oauth2.scope.checker.type", "annotations"
			).build(),
			"[everything.read, everything.write]",
			"[everything.read, everything.update, everything.write]");
	}

	@Test
	public void testStartServerWithHttpMethods() throws Exception {
		_testStartServer(
			new HashMapDictionary<>(), "[GET, POST]", "[GET, POST, PUT]");
	}

	@Path("/get-post")
	public static class TestGetPostResource {

		@GET
		@RequiresScope("everything.read")
		public String get() {
			return RandomTestUtil.randomString();
		}

		@POST
		@RequiresScope("everything.write")
		public String post() {
			return RandomTestUtil.randomString();
		}

	}

	@Path("/put")
	public static class TestPutResource {

		@PUT
		@RequiresScope("everything.update")
		public String put() {
			return RandomTestUtil.randomString();
		}

	}

	private void _testStartServer(
			Dictionary<String, Object> properties, String scopes1,
			String scopes2)
		throws Exception {

		Bundle bundle = FrameworkUtil.getBundle(
			BaseScopeFinderFeatureTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		String name = RandomTestUtil.randomString();
		List<String> scopesStrings = new CopyOnWriteArrayList<>();

		ServiceTracker<ScopeFinder, ScopeFinder> serviceTracker =
			new ServiceTracker<>(
				bundleContext,
				bundleContext.createFilter(
					StringBundler.concat(
						"(&(objectClass=", ScopeFinder.class.getName(),
						")(osgi.jaxrs.name=", name, "))")),
				new ServiceTrackerCustomizer<ScopeFinder, ScopeFinder>() {

					@Override
					public ScopeFinder addingService(
						ServiceReference<ScopeFinder> serviceReference) {

						ScopeFinder scopeFinder = bundleContext.getService(
							serviceReference);

						Set<String> scopes = new TreeSet<>(
							scopeFinder.findScopes());

						scopesStrings.add(scopes.toString());

						return scopeFinder;
					}

					@Override
					public void modifiedService(
						ServiceReference<ScopeFinder> serviceReference,
						ScopeFinder scopeFinder) {
					}

					@Override
					public void removedService(
						ServiceReference<ScopeFinder> serviceReference,
						ScopeFinder scopeFinder) {

						scopesStrings.add("removed");

						bundleContext.ungetService(serviceReference);
					}

				});

		serviceTracker.open();

		ServiceRegistration<Application> applicationServiceRegistration = null;
		ServiceRegistration<Object> resourceServiceRegistration = null;

		try {
			properties.put("osgi.jaxrs.application.base", "/" + name);
			properties.put(
				"osgi.jaxrs.extension.select", "(liferay.extension=OAuth2)");
			properties.put("osgi.jaxrs.name", name);

			applicationServiceRegistration = bundleContext.registerService(
				Application.class, new TestApplication(), properties);

			Assert.assertEquals(Arrays.asList(scopes1), scopesStrings);

			resourceServiceRegistration = bundleContext.registerService(
				Object.class, new TestPutResource(),
				HashMapDictionaryBuilder.<String, Object>put(
					"osgi.jaxrs.application.select",
					"(osgi.jaxrs.name=" + name + ")"
				).put(
					"osgi.jaxrs.resource", true
				).build());

			Assert.assertEquals(
				Arrays.asList(scopes1, "removed", scopes2), scopesStrings);

			applicationServiceRegistration.unregister();

			applicationServiceRegistration = null;

			Assert.assertEquals(
				Arrays.asList(scopes1, "removed", scopes2, "removed"),
				scopesStrings);
		}
		finally {
			if (applicationServiceRegistration != null) {
				applicationServiceRegistration.unregister();
			}

			if (resourceServiceRegistration != null) {
				resourceServiceRegistration.unregister();
			}

			serviceTracker.close();
		}
	}

	private static class TestApplication extends Application {

		@Override
		public Set<Object> getSingletons() {
			return Collections.singleton(new TestGetPostResource());
		}

	}

}