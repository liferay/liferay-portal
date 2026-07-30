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
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;

import java.util.Collections;
import java.util.List;
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

		_assertRedactionFails(
			"(.*a){40}", "repeated-text", "Redaction exceeded the timeout of");
		_assertRedactionFails(
			"(a|aa)+$", "long-repeated-text", "Redaction overflowed the stack");
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
		@Path("/long-repeated-text")
		@Produces(MediaType.TEXT_PLAIN)
		public String longRepeatedText() {
			return _LONG_REPEATED_TEXT;
		}

		@GET
		@Path("/repeated-text")
		@Produces(MediaType.TEXT_PLAIN)
		public String repeatedText() {
			return _REPEATED_TEXT;
		}

		@GET
		@Path("/test")
		@Produces(MediaType.TEXT_PLAIN)
		public String test() {
			return "The value is secret.";
		}

	}

	private void _assertRedactionFails(
			String detectionRegex, String path, String expectedMessage)
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.headless.data.mask.internal.jaxrs.writer." +
					"interceptor.DataMaskWriterInterceptor",
				LoggerTestUtil.ERROR)) {

			ObjectEntry objectEntry = DataMaskTestUtil.addDataMaskObjectEntry(
				RandomTestUtil.randomString(), detectionRegex, "[REDACTED]");

			Assert.assertEquals(
				"",
				HTTPTestUtil.invokeToString(
					null, "test-data-mask/" + path,
					HashMapBuilder.put(
						"X-Liferay-Data-Masks",
						objectEntry.getExternalReferenceCode()
					).build(),
					Http.Method.GET));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			String message = logEntry.getMessage();

			Assert.assertTrue(message, message.contains(expectedMessage));
		}
	}

	private static final String _LONG_REPEATED_TEXT = "a".repeat(100000) + "b";

	private static final String _REPEATED_TEXT = "a".repeat(60);

	private ServiceRegistration<Application> _serviceRegistration;

}