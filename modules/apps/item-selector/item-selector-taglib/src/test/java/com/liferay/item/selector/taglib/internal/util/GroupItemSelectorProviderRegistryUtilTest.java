/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector.taglib.internal.util;

import com.liferay.item.selector.provider.GroupItemSelectorProvider;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Cristina González
 */
public class GroupItemSelectorProviderRegistryUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() throws Exception {
		_bundleContext = SystemBundleUtil.getBundleContext();

		Mockito.when(
			FrameworkUtil.getBundle(Mockito.any())
		).thenReturn(
			_bundleContext.getBundle()
		);
	}

	@AfterClass
	public static void tearDownClass() {
		_frameworkUtilMockedStatic.close();
	}

	@After
	public void tearDown() {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();

			_serviceRegistration = null;
		}
	}

	@Test
	public void testGetGroupItemSelectorProviderOptionalWithRegisteredGroupItemSelectorProvider() {
		_serviceRegistration = _bundleContext.registerService(
			GroupItemSelectorProvider.class,
			new MockGroupItemSelectorProvider(), null);

		GroupItemSelectorProvider groupItemSelectorProvider =
			GroupItemSelectorProviderRegistryUtil.getGroupItemSelectorProvider(
				"test");

		Assert.assertNotNull(groupItemSelectorProvider);
		Assert.assertEquals("icon", groupItemSelectorProvider.getIcon());

		_serviceRegistration.unregister();
	}

	@Test
	public void testGetGroupItemSelectorProviderOptionalWithRegisteredInactiveGroupItemSelectorProvider() {
		_serviceRegistration = _bundleContext.registerService(
			GroupItemSelectorProvider.class,
			new MockGroupItemSelectorProvider(false), null);

		Assert.assertNull(
			GroupItemSelectorProviderRegistryUtil.getGroupItemSelectorProvider(
				"test"));
		Assert.assertTrue(
			SetUtil.isEmpty(
				GroupItemSelectorProviderRegistryUtil.
					getGroupItemSelectorProviderTypes()));

		_serviceRegistration.unregister();
	}

	@Test
	public void testGetGroupItemSelectorProviderOptionalWithoutRegisteredGroupItemSelectorProvider() {
		Assert.assertNull(
			GroupItemSelectorProviderRegistryUtil.getGroupItemSelectorProvider(
				RandomTestUtil.randomString()));
	}

	@Test
	public void testGetGroupItemSelectorProviderTypesWithRegisteredGroupItemSelectorProvider() {
		_serviceRegistration = _bundleContext.registerService(
			GroupItemSelectorProvider.class,
			new MockGroupItemSelectorProvider(), null);

		Assert.assertEquals(
			Collections.singleton("test"),
			GroupItemSelectorProviderRegistryUtil.
				getGroupItemSelectorProviderTypes());

		_serviceRegistration.unregister();
	}

	@Test
	public void testGetGroupItemSelectorProviderTypesWithoutRegisteredGroupItemSelectorProvider() {
		Assert.assertEquals(
			Collections.emptySet(),
			GroupItemSelectorProviderRegistryUtil.
				getGroupItemSelectorProviderTypes());
	}

	private static BundleContext _bundleContext;
	private static final MockedStatic<FrameworkUtil>
		_frameworkUtilMockedStatic = Mockito.mockStatic(FrameworkUtil.class);

	private ServiceRegistration<GroupItemSelectorProvider> _serviceRegistration;

	private static class MockGroupItemSelectorProvider
		implements GroupItemSelectorProvider {

		public MockGroupItemSelectorProvider() {
			this(true);
		}

		public MockGroupItemSelectorProvider(boolean active) {
			_active = active;
		}

		@Override
		public String getEmptyResultsMessage() {
			return null;
		}

		@Override
		public String getGroupType() {
			return "test";
		}

		@Override
		public List<Group> getGroups(
			long companyId, long groupId, String keywords, int start, int end) {

			return Collections.singletonList(Mockito.mock(Group.class));
		}

		@Override
		public int getGroupsCount(
			long companyId, long groupId, String keywords) {

			return 3;
		}

		@Override
		public String getIcon() {
			return "icon";
		}

		@Override
		public String getLabel(Locale locale) {
			return RandomTestUtil.randomString();
		}

		@Override
		public boolean isEnabled() {
			return _active;
		}

		private boolean _active;

	}

}