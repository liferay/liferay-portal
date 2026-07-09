/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.data.mask.internal.jaxrs.writer.interceptor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.data.mask.test.util.DataMaskTestUtil;
import com.liferay.object.model.ObjectEntry;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;

import java.util.Collections;
import java.util.Set;

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
import org.osgi.framework.ServiceRegistration;

/**
 * @author Jose Luis Navarro
 */
@FeatureFlags(featureFlags = @FeatureFlag("LPD-63311"))
@RunWith(Arquillian.class)
public class DataMaskWriterInterceptorTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		DataMaskTestUtil.processBatchEngineUnits();

		Bundle bundle = FrameworkUtil.getBundle(
			DataMaskWriterInterceptorTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			Application.class, new TestApplication(),
			HashMapDictionaryBuilder.<String, Object>put(
				"liferay.auth.verifier", true
			).put(
				"liferay.oauth2", false
			).put(
				"osgi.jaxrs.application.base", "/test-data-mask"
			).put(
				"osgi.jaxrs.extension.select",
				"(osgi.jaxrs.name=Liferay.Vulcan)"
			).build());
	}

	@After
	public void tearDown() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testAroundWriteTo() throws Exception {
		Assert.assertEquals(
			"The value is secret.",
			HTTPTestUtil.invokeToString(
				null, "test-data-mask/test", Http.Method.GET));
		Assert.assertEquals(
			"The value is [REDACTED].",
			HTTPTestUtil.invokeToString(
				null, "test-data-mask/test",
				HashMapBuilder.put(
					"X-Liferay-Data-Masks",
					() -> {
						ObjectEntry objectEntry =
							DataMaskTestUtil.addDataMaskObjectEntry(
								RandomTestUtil.randomString(), "secret",
								"[REDACTED]");

						return objectEntry.getExternalReferenceCode();
					}
				).build(),
				Http.Method.GET));
		Assert.assertEquals(
			"The value is secret.",
			HTTPTestUtil.invokeToString(
				null, "test-data-mask/test",
				HashMapBuilder.put(
					"X-Liferay-Data-Masks", RandomTestUtil.randomString()
				).build(),
				Http.Method.GET));

		DataMaskTestUtil.addDataMaskObjectEntry(
			RandomTestUtil.randomString(), "value", "[HIDDEN]");

		Assert.assertEquals(
			"The value is [REDACTED].",
			HTTPTestUtil.invokeToString(
				null, "test-data-mask/test",
				HashMapBuilder.put(
					"X-Liferay-Data-Masks",
					() -> {
						ObjectEntry objectEntry =
							DataMaskTestUtil.addDataMaskObjectEntry(
								RandomTestUtil.randomString(), "secret",
								"[REDACTED]");

						return objectEntry.getExternalReferenceCode();
					}
				).build(),
				Http.Method.GET));
	}

	@FeatureFlags(
		featureFlags = @FeatureFlag(enable = false, value = "LPD-63311")
	)
	@Test
	public void testAroundWriteToWhenFeatureFlagIsDisabled() throws Exception {
		Assert.assertEquals(
			"The value is secret.",
			HTTPTestUtil.invokeToString(
				null, "test-data-mask/test",
				HashMapBuilder.put(
					"X-Liferay-Data-Masks",
					() -> {
						ObjectEntry objectEntry =
							DataMaskTestUtil.addDataMaskObjectEntry(
								RandomTestUtil.randomString(), "secret",
								"[REDACTED]");

						return objectEntry.getExternalReferenceCode();
					}
				).build(),
				Http.Method.GET));
	}

	public static class TestApplication extends Application {

		@Override
		public Set<Object> getSingletons() {
			return Collections.singleton(this);
		}

		@GET
		@Path("/test")
		@Produces(MediaType.TEXT_PLAIN)
		public String test() {
			return "The value is secret.";
		}

	}

	private ServiceRegistration<Application> _serviceRegistration;

}