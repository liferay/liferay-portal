/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.portlet.data.handler.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.lar.BasePortletDataHandler;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.portlet.data.handler.provider.PortletDataHandlerProvider;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.portlet.GenericPortlet;
import jakarta.portlet.Portlet;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Alejandro Tardín
 */
@RunWith(Arquillian.class)
public class PortletDataHandlerProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void test() throws Exception {
		long companyId1 = RandomTestUtil.randomLong();
		long companyId2 = RandomTestUtil.randomLong();
		BasePortletDataHandler portletDataHandler1 =
			new TestPortletDataHandler();
		BasePortletDataHandler portletDataHandler2 =
			new TestPortletDataHandler();
		BasePortletDataHandler portletDataHandler3 =
			new TestPortletDataHandler();
		BasePortletDataHandler portletDataHandler4 =
			new TestPortletDataHandler();
		BasePortletDataHandler portletDataHandler5 =
			new TestPortletDataHandler();
		String portletId1 = RandomTestUtil.randomString();
		String portletId2 = RandomTestUtil.randomString();
		String portletId3 = RandomTestUtil.randomString();
		String portletId4 = RandomTestUtil.randomString();

		try (SafeCloseable safeCloseable1 = _registerWithSafeCloseable(
				portletId1);
			SafeCloseable safeCloseable2 = _registerWithSafeCloseable(
				portletId2);
			SafeCloseable safeCloseable3 = _registerWithSafeCloseable(
				portletId3);
			SafeCloseable safeCloseable4 = _registerWithSafeCloseable(
				portletId4);
			SafeCloseable safeCloseable5 = _registerWithSafeCloseable(
				null, portletDataHandler1, portletId1);
			SafeCloseable safeCloseable6 = _registerWithSafeCloseable(
				null, portletDataHandler2, portletId2);
			SafeCloseable safeCloseable7 = _registerWithSafeCloseable(
				List.of(companyId1), portletDataHandler3, portletId1);
			SafeCloseable safeCloseable8 = _registerWithSafeCloseable(
				List.of(companyId2), portletDataHandler4, portletId2);
			SafeCloseable safeCloseable9 = _registerWithSafeCloseable(
				List.of(companyId1, companyId2), portletDataHandler5,
				portletId3)) {

			Thread.sleep(1000);

			Assert.assertEquals(
				portletDataHandler3,
				_portletDataHandlerProvider.provide(companyId1, portletId1));
			Assert.assertEquals(
				portletDataHandler2,
				_portletDataHandlerProvider.provide(companyId1, portletId2));
			Assert.assertEquals(
				portletDataHandler5,
				_portletDataHandlerProvider.provide(companyId1, portletId3));
			Assert.assertEquals(
				_defaultPortletDataHandler,
				_portletDataHandlerProvider.provide(companyId1, portletId4));
			Assert.assertEquals(
				portletDataHandler1,
				_portletDataHandlerProvider.provide(companyId2, portletId1));
			Assert.assertEquals(
				portletDataHandler4,
				_portletDataHandlerProvider.provide(companyId2, portletId2));
			Assert.assertEquals(
				portletDataHandler5,
				_portletDataHandlerProvider.provide(companyId2, portletId3));
			Assert.assertEquals(
				_defaultPortletDataHandler,
				_portletDataHandlerProvider.provide(companyId2, portletId4));
		}
	}

	private SafeCloseable _registerWithSafeCloseable(
		List<Long> companyIds, PortletDataHandler portletDataHandler,
		String portletId) {

		Bundle bundle = FrameworkUtil.getBundle(
			PortletDataHandlerProviderTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceRegistration<PortletDataHandler> serviceRegistration =
			bundleContext.registerService(
				PortletDataHandler.class, portletDataHandler,
				HashMapDictionaryBuilder.<String, Object>put(
					"companyId",
					() -> (companyIds == null) ? null :
						TransformUtil.transform(companyIds, String::valueOf)
				).put(
					"jakarta.portlet.name", portletId
				).build());

		return serviceRegistration::unregister;
	}

	private SafeCloseable _registerWithSafeCloseable(String portletId) {
		Bundle bundle = FrameworkUtil.getBundle(
			PortletDataHandlerProviderTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceRegistration<Portlet> serviceRegistration =
			bundleContext.registerService(
				Portlet.class,
				new GenericPortlet() {
				},
				HashMapDictionaryBuilder.<String, Object>put(
					"jakarta.portlet.name", portletId
				).build());

		return serviceRegistration::unregister;
	}

	@Inject(filter = "jakarta.portlet.name=ALL")
	private PortletDataHandler _defaultPortletDataHandler;

	@Inject
	private PortletDataHandlerProvider _portletDataHandlerProvider;

	private static class TestPortletDataHandler extends BasePortletDataHandler {
	}

}