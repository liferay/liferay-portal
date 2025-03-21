/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.web.internal.item.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.content.dashboard.item.action.ContentDashboardItemVersionAction;
import com.liferay.content.dashboard.item.action.ContentDashboardItemVersionActionProviderRegistry;
import com.liferay.content.dashboard.item.action.provider.ContentDashboardItemVersionActionProvider;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Locale;

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
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class ContentDashboardItemVersionActionProviderRegistryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testInvalidContentDashboardItemVersionActions()
		throws Exception {

		_withTestItemContentDashboardItemVersionActionProviderRegistered(
			() -> {
				List<ContentDashboardItemVersionActionProvider>
					contentDashboardItemVersionActionProviders =
						_contentDashboardItemVersionActionProviderRegistry.
							getContentDashboardItemVersionActionProviders(
								RandomTestUtil.randomString());

				Assert.assertNotNull(
					contentDashboardItemVersionActionProviders);
				Assert.assertEquals(
					contentDashboardItemVersionActionProviders.toString(), 0,
					contentDashboardItemVersionActionProviders.size());
			});
	}

	@Test
	public void testTestItemContentDashboardItemVersionActions()
		throws Exception {

		_withTestItemContentDashboardItemVersionActionProviderRegistered(
			() -> {
				List<ContentDashboardItemVersionActionProvider>
					contentDashboardItemVersionActionProviders =
						_contentDashboardItemVersionActionProviderRegistry.
							getContentDashboardItemVersionActionProviders(
								TestItem.class.getName());

				Assert.assertNotNull(
					contentDashboardItemVersionActionProviders);
				Assert.assertEquals(
					contentDashboardItemVersionActionProviders.toString(), 1,
					contentDashboardItemVersionActionProviders.size());

				ContentDashboardItemVersionActionProvider
					contentDashboardItemVersionActionProvider =
						contentDashboardItemVersionActionProviders.get(0);

				Assert.assertNotNull(contentDashboardItemVersionActionProvider);
			});
	}

	private void
			_withTestItemContentDashboardItemVersionActionProviderRegistered(
				UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		Bundle bundle = FrameworkUtil.getBundle(
			ContentDashboardItemVersionActionProviderRegistryTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceRegistration<ContentDashboardItemVersionActionProvider>
			serviceRegistration = bundleContext.registerService(
				ContentDashboardItemVersionActionProvider.class,
				new TestItemContentDashboardItemVersionActionProvider(), null);

		try {
			unsafeRunnable.run();
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	@Inject
	private ContentDashboardItemVersionActionProviderRegistry
		_contentDashboardItemVersionActionProviderRegistry;

	private static class TestContentDashboardItemVersionAction
		implements ContentDashboardItemVersionAction {

		@Override
		public String getIcon() {
			return null;
		}

		@Override
		public String getLabel(Locale locale) {
			return null;
		}

		@Override
		public String getName() {
			return null;
		}

		@Override
		public String getURL() {
			return null;
		}

	}

	private static class TestItem {
	}

	private static class TestItemContentDashboardItemVersionActionProvider
		implements ContentDashboardItemVersionActionProvider<TestItem> {

		@Override
		public ContentDashboardItemVersionAction
			getContentDashboardItemVersionAction(
				TestItem testItem, HttpServletRequest httpServletRequest) {

			return new TestContentDashboardItemVersionAction();
		}

		@Override
		public boolean isShow(
			TestItem testItem, HttpServletRequest httpServletRequest) {

			return true;
		}

	}

}