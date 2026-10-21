/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.linkback;

import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.security.xml.SecureXMLFactoryProviderUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.portal.kernel.xmlrpc.Response;
import com.liferay.portal.security.xml.SecureXMLFactoryProviderImpl;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.xmlrpc.XmlRpcUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Mikel Lorza
 */
public class LinkbackProducerUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		SecureXMLFactoryProviderUtil secureXMLFactoryProviderUtil =
			new SecureXMLFactoryProviderUtil();

		secureXMLFactoryProviderUtil.setSecureXMLFactoryProvider(
			new SecureXMLFactoryProviderImpl());
	}

	@Test
	@TestInfo("LPD-110195")
	public void testSendQueuedPingbacks() throws Exception {
		Http http = Mockito.mock(Http.class);

		Mockito.doAnswer(
			invocation -> {
				Http.Options options = invocation.getArgument(0);

				Http.Response response = new Http.Response();

				String location = options.getLocation();

				if (location.endsWith("1")) {
					response.addHeader("X-Pingback", "http://10.0.0.1/xmlrpc");
				}
				else {
					response.addHeader("X-Pingback", "http://1.2.3.5/xmlrpc");
				}

				options.setResponse(response);

				return null;
			}
		).when(
			http
		).URLtoByteArray(
			Mockito.any(Http.Options.class)
		);

		ServiceRegistration<Http> serviceRegistration =
			_bundleContext.registerService(Http.class, http, null);

		try (MockedStatic<XmlRpcUtil> xmlRpcUtilMockedStatic =
				Mockito.mockStatic(XmlRpcUtil.class)) {

			xmlRpcUtilMockedStatic.when(
				() -> XmlRpcUtil.executeMethod(
					Mockito.anyString(), Mockito.anyString(),
					Mockito.any(Object[].class))
			).thenReturn(
				Mockito.mock(Response.class)
			);

			_addPingback("http://127.0.0.1/target");
			_addPingback("http://1.2.3.4/target1");
			_addPingback("http://1.2.3.4/target2");

			LinkbackProducerUtil.sendQueuedPingbacks();

			Mockito.verify(
				http, Mockito.times(2)
			).URLtoByteArray(
				Mockito.<Http.Options>argThat(
					options -> !options.isFollowRedirects())
			);

			Mockito.verify(
				http, Mockito.never()
			).URLtoByteArray(
				Mockito.<Http.Options>argThat(
					options -> {
						String location = options.getLocation();

						return location.startsWith("http://127.0.0.1");
					})
			);

			xmlRpcUtilMockedStatic.verify(
				() -> XmlRpcUtil.executeMethod(
					Mockito.eq("http://1.2.3.5/xmlrpc"), Mockito.anyString(),
					Mockito.any(Object[].class)));
			xmlRpcUtilMockedStatic.verify(
				() -> XmlRpcUtil.executeMethod(
					Mockito.eq("http://10.0.0.1/xmlrpc"), Mockito.anyString(),
					Mockito.any(Object[].class)),
				Mockito.never());
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	@Test
	@TestInfo("LPD-110195")
	public void testSendTrackback() throws Exception {
		Http http = Mockito.mock(Http.class);

		Mockito.when(
			http.URLtoString(Mockito.any(Http.Options.class))
		).thenReturn(
			"<?xml version=\"1.0\" encoding=\"utf-8\"?><response><error>0" +
				"</error></response>"
		);

		ServiceRegistration<Http> serviceRegistration =
			_bundleContext.registerService(Http.class, http, null);

		try {
			Assert.assertFalse(
				LinkbackProducerUtil.sendTrackback(
					"http://127.0.0.1/trackback", new HashMap<>()));

			Mockito.verify(
				http, Mockito.never()
			).URLtoString(
				Mockito.any(Http.Options.class)
			);

			Assert.assertTrue(
				LinkbackProducerUtil.sendTrackback(
					"http://1.2.3.4/trackback", new HashMap<>()));

			Mockito.verify(
				http
			).URLtoString(
				Mockito.<Http.Options>argThat(
					options -> !options.isFollowRedirects())
			);
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	private void _addPingback(String targetURI) {
		List<Tuple> pingbackQueue = ReflectionTestUtil.getFieldValue(
			LinkbackProducerUtil.class, "_pingbackQueue");

		pingbackQueue.add(
			new Tuple(
				new Date(System.currentTimeMillis() - Time.HOUR),
				RandomTestUtil.randomString(), targetURI));
	}

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();

}