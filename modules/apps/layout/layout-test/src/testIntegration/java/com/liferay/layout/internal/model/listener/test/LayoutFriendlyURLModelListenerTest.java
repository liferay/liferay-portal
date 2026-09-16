/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.thread.local.BatchEngineThreadLocal;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.service.StagingLocalService;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.layout.friendly.url.LayoutFriendlyURLEntryHelper;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.FriendlyURLNormalizer;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class LayoutFriendlyURLModelListenerTest {

	@ClassRule
	@Rule
	public static AggregateTestRule aggregateTestRule = new AggregateTestRule(
		new LiferayIntegrationTestRule(),
		PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_classNameId = _layoutFriendlyURLEntryHelper.getClassNameId(false);

		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group, TestPropsValues.getUserId());
	}

	@Test
	@TestInfo("LPD-74225")
	public void testCreateLayoutFriendlyURL() throws Exception {
		_testCreateLayoutFriendlyURL(1);

		ExportImportThreadLocal.setLayoutImportInProcess(true);

		try {
			_testCreateLayoutFriendlyURL(0);

			try (SafeCloseable safeCloseable =
					BatchEngineThreadLocal.
						setBatchImportInProcessWithSafeCloseable(true)) {

				_testCreateLayoutFriendlyURL(1);
			}
		}
		finally {
			ExportImportThreadLocal.setLayoutImportInProcess(false);
		}

		_stagingLocalService.enableLocalStaging(
			TestPropsValues.getUserId(), _group, false, false, _serviceContext);

		_testCreateLayoutFriendlyURL(0);

		try (SafeCloseable safeCloseable =
				BatchEngineThreadLocal.setBatchImportInProcessWithSafeCloseable(
					true)) {

			_testCreateLayoutFriendlyURL(1);
		}
	}

	@Test
	@TestInfo("LPD-74225")
	public void testUpdateLayoutFriendlyURL() throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		_testUpdateLayoutFriendlyURL(1, layout);

		ExportImportThreadLocal.setLayoutImportInProcess(true);

		try {
			_testUpdateLayoutFriendlyURL(0, layout);

			try (SafeCloseable safeCloseable =
					BatchEngineThreadLocal.
						setBatchImportInProcessWithSafeCloseable(true)) {

				_testUpdateLayoutFriendlyURL(1, layout);
			}
		}
		finally {
			ExportImportThreadLocal.setLayoutImportInProcess(false);
		}

		_stagingLocalService.enableLocalStaging(
			TestPropsValues.getUserId(), _group, false, false, _serviceContext);

		_testUpdateLayoutFriendlyURL(0, layout);

		try (SafeCloseable safeCloseable =
				BatchEngineThreadLocal.setBatchImportInProcessWithSafeCloseable(
					true)) {

			_testUpdateLayoutFriendlyURL(1, layout);
		}
	}

	private void _testCreateLayoutFriendlyURL(int count) throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		List<FriendlyURLEntry> friendlyURLEntries =
			_friendlyURLEntryLocalService.getFriendlyURLEntries(
				_group.getGroupId(), _classNameId, layout.getPlid());

		Assert.assertEquals(
			friendlyURLEntries.toString(), count, friendlyURLEntries.size());
	}

	private void _testUpdateLayoutFriendlyURL(int count, Layout layout)
		throws Exception {

		List<FriendlyURLEntry> friendlyURLEntries =
			_friendlyURLEntryLocalService.getFriendlyURLEntries(
				_group.getGroupId(), _classNameId, layout.getPlid());

		layout = _layoutLocalService.updateFriendlyURL(
			TestPropsValues.getUserId(), layout.getPlid(),
			StringPool.SLASH +
				_friendlyURLNormalizer.normalize(RandomTestUtil.randomString()),
			layout.getDefaultLanguageId());

		List<FriendlyURLEntry> actualFriendlyURLEntries =
			_friendlyURLEntryLocalService.getFriendlyURLEntries(
				_group.getGroupId(), _classNameId, layout.getPlid());

		Assert.assertEquals(
			actualFriendlyURLEntries.toString(),
			friendlyURLEntries.size() + count, actualFriendlyURLEntries.size());
	}

	private long _classNameId;

	@Inject
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Inject
	private FriendlyURLNormalizer _friendlyURLNormalizer;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutFriendlyURLEntryHelper _layoutFriendlyURLEntryHelper;

	@Inject
	private LayoutLocalService _layoutLocalService;

	private ServiceContext _serviceContext;

	@Inject
	private StagingLocalService _stagingLocalService;

}