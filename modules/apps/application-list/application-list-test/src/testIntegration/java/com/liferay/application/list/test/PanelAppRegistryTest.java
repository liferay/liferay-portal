/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.application.list.test;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.PanelAppRegistry;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.application.list.display.context.logic.test.application.list.ApplicationsMenuTestPanelApp;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Dictionary;
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
import org.osgi.service.component.annotations.Reference;

/**
 * @author Thiago Buarque
 */
@RunWith(Arquillian.class)
public class PanelAppRegistryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGetFirstAvailablePanelApp() throws PortalException {
		User user = TestPropsValues.getUser();

		PanelApp panelApp = _panelAppRegistry.getFirstAvailablePanelApp(
			PanelCategoryKeys.APPLICATIONS_MENU_APPLICATIONS,
			PermissionCheckerFactoryUtil.create(user), user.getGroup());

		Assert.assertTrue(panelApp instanceof ApplicationsMenuTestPanelApp);
	}

	@Test
	public void testGetServiceReferenceComparator() {
		ServiceRegistration<PanelApp> serviceRegistration1 = null;
		ServiceRegistration<PanelApp> serviceRegistration2 = null;

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.application.list.PanelAppRegistry",
				LoggerTestUtil.INFO)) {

			Bundle bundle = FrameworkUtil.getBundle(PanelAppRegistry.class);

			BundleContext bundleContext = bundle.getBundleContext();

			Dictionary<String, String> properties =
				HashMapDictionaryBuilder.put(
					"panel.app.order", "100"
				).put(
					"panel.category.key", "panelCategoryKey"
				).build();

			serviceRegistration1 = bundleContext.registerService(
				PanelApp.class, new TestPanelApp1(), properties);
			serviceRegistration2 = bundleContext.registerService(
				PanelApp.class, new TestPanelApp2(), properties);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				StringBundler.concat(
					"The panel apps \"com.liferay.application.list.test.",
					"PanelAppRegistryTest$TestPanelApp2\" and \"com.liferay.",
					"application.list.test.PanelAppRegistryTest$TestPanelApp1",
					"\" have the same order 100 and category key \"",
					"panelCategoryKey\""),
				logEntry.getMessage());
		}
		finally {
			if (serviceRegistration1 != null) {
				serviceRegistration1.unregister();
			}

			if (serviceRegistration2 != null) {
				serviceRegistration2.unregister();
			}
		}
	}

	@Inject
	private PanelAppRegistry _panelAppRegistry;

	private static class TestPanelApp1 extends BasePanelApp {

		@Override
		public Portlet getPortlet() {
			return _portlet;
		}

		@Override
		public String getPortletId() {
			return PortletKeys.ANNOUNCEMENTS;
		}

		@Reference(
			target = "(jakarta.portlet.name=" + PortletKeys.ANNOUNCEMENTS + ")"
		)
		private Portlet _portlet;

	}

	private static class TestPanelApp2 extends BasePanelApp {

		@Override
		public Portlet getPortlet() {
			return _portlet;
		}

		@Override
		public String getPortletId() {
			return PortletKeys.BLOGS;
		}

		@Reference(target = "(jakarta.portlet.name=" + PortletKeys.BLOGS + ")")
		private Portlet _portlet;

	}

}