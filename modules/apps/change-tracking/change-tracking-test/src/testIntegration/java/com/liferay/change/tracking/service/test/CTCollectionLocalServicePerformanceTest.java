/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTProcessLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.performance.PerformanceTimer;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.initializer.SiteInitializer;
import com.liferay.site.initializer.SiteInitializerRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author David Truong
 */
@DataGuard(scope = DataGuard.Scope.NONE)
@RunWith(Arquillian.class)
public class CTCollectionLocalServicePerformanceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_ctCollection1 = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), null);
		_ctCollection2 = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), null);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setProductionModeWithSafeCloseable()) {

			_group = GroupTestUtil.addGroup();
		}
	}

	@After
	public void tearDown() throws Exception {
		_ctCollectionLocalService.deleteCTCollection(_ctCollection1);
		_ctCollectionLocalService.deleteCTCollection(_ctCollection2);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setProductionModeWithSafeCloseable()) {

			GroupTestUtil.deleteGroup(_group);
		}
	}

	@Test
	public void testCheckConflictsGroup() throws Exception {
		String groupName = RandomTestUtil.randomString();

		Group group = null;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection1.getCtCollectionId())) {

			group = GroupTestUtil.addGroup(
				GroupConstants.DEFAULT_PARENT_GROUP_ID, groupName,
				new ServiceContext());

			ServiceContextThreadLocal.pushServiceContext(
				ServiceContextTestUtil.getServiceContext(group.getGroupId()));

			try {
				SiteInitializer siteInitializer =
					_siteInitializerRegistry.getSiteInitializer(
						"com.liferay.site.initializer.welcome");

				siteInitializer.initialize(group.getGroupId());
			}
			finally {
				ServiceContextThreadLocal.popServiceContext();
			}
		}

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection2.getCtCollectionId())) {

			group = GroupTestUtil.addGroup(
				GroupConstants.DEFAULT_PARENT_GROUP_ID, groupName,
				new ServiceContext());

			ServiceContextThreadLocal.pushServiceContext(
				ServiceContextTestUtil.getServiceContext(group.getGroupId()));

			try {
				SiteInitializer siteInitializer =
					_siteInitializerRegistry.getSiteInitializer(
						"com.liferay.site.initializer.welcome");

				siteInitializer.initialize(group.getGroupId());
			}
			finally {
				ServiceContextThreadLocal.popServiceContext();
			}
		}

		try (PerformanceTimer performanceTimer = new PerformanceTimer(45000)) {
			_ctProcessLocalService.addCTProcess(
				_ctCollection2.getUserId(), _ctCollection2.getCtCollectionId());
		}

		try (PerformanceTimer performanceTimer = new PerformanceTimer(10000)) {
			_ctCollectionLocalService.checkConflicts(_ctCollection1);
		}

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setProductionModeWithSafeCloseable()) {

			GroupTestUtil.deleteGroup(group);
		}
	}

	@Test
	public void testCheckConflictsLayout() throws Exception {
		String layoutName = RandomTestUtil.randomString();

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection1.getCtCollectionId())) {

			LayoutTestUtil.addTypeContentLayout(_group, layoutName);
		}

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection2.getCtCollectionId())) {

			LayoutTestUtil.addTypeContentLayout(_group, layoutName);
		}

		try (PerformanceTimer performanceTimer = new PerformanceTimer(10000)) {
			_ctProcessLocalService.addCTProcess(
				_ctCollection2.getUserId(), _ctCollection2.getCtCollectionId());
		}

		try (PerformanceTimer performanceTimer = new PerformanceTimer(10000)) {
			_ctCollectionLocalService.checkConflicts(_ctCollection1);
		}
	}

	@Test
	public void testDiscardCTEntryLayout() throws Exception {
		Layout layout = null;

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		try {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						_ctCollection1.getCtCollectionId())) {

				SiteInitializer siteInitializer =
					_siteInitializerRegistry.getSiteInitializer(
						"com.liferay.site.initializer.welcome");

				siteInitializer.initialize(_group.getGroupId());

				layout = _layoutLocalService.fetchDefaultLayout(
					_group.getGroupId(), false);
			}
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}

		try (PerformanceTimer performanceTimer = new PerformanceTimer(10000)) {
			_ctCollectionLocalService.getRelatedCTEntriesMap(
				_ctCollection1.getCtCollectionId(),
				_portal.getClassNameId(Layout.class.getName()),
				layout.getPlid());
		}

		try (PerformanceTimer performanceTimer = new PerformanceTimer(10000)) {
			_ctCollectionLocalService.discardCTEntry(
				_ctCollection1.getCtCollectionId(),
				_portal.getClassNameId(Layout.class.getName()),
				layout.getPlid(), false);
		}
	}

	@Inject
	private static LayoutLocalService _layoutLocalService;

	private CTCollection _ctCollection1;
	private CTCollection _ctCollection2;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private CTProcessLocalService _ctProcessLocalService;

	private Group _group;

	@Inject
	private Portal _portal;

	@Inject
	private SiteInitializerRegistry _siteInitializerRegistry;

}