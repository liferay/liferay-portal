/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.upgrade.v1_4_3.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.layout.model.LayoutClassedModelUsage;
import com.liferay.layout.service.LayoutClassedModelUsageLocalService;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Gergely Szalay
 */
@RunWith(Arquillian.class)
public class LayoutClassedModelUsageUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_dlFileEntryClassNameId = _classNameLocalService.getClassNameId(
			DLFileEntry.class.getName());
		_fileEntryClassNameId = _classNameLocalService.getClassNameId(
			FileEntry.class);

		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());
	}

	@Test
	public void testUpgrade() throws Exception {
		_layoutClassedModelUsage = addLayoutClassedModelUsage();

		_runUpgrade();

		LayoutClassedModelUsage newLayoutClassedModelUsage =
			_layoutClassedModelUsageLocalService.getLayoutClassedModelUsage(
				_layoutClassedModelUsage.getLayoutClassedModelUsageId());

		Assert.assertEquals(
			_fileEntryClassNameId, newLayoutClassedModelUsage.getClassNameId());
	}

	protected LayoutClassedModelUsage addLayoutClassedModelUsage()
		throws Exception {

		return _layoutClassedModelUsageLocalService.addLayoutClassedModelUsage(
			_group.getGroupId(), RandomTestUtil.randomString(),
			_dlFileEntryClassNameId, RandomTestUtil.nextLong(),
			RandomTestUtil.randomString(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong(), _serviceContext);
	}

	private void _runUpgrade() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.OFF)) {

			UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
				_upgradeStepRegistrator, _CLASS_NAME);

			upgradeProcess.upgrade();

			_multiVMPool.clear();
		}
	}

	private static final String _CLASS_NAME =
		"com.liferay.layout.internal.upgrade.v1_4_3." +
			"LayoutClassedModelUsageUpgradeProcess";

	@Inject(
		filter = "(&(component.name=com.liferay.layout.internal.upgrade.registry.LayoutServiceUpgradeStepRegistrator))"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	private long _dlFileEntryClassNameId;
	private long _fileEntryClassNameId;

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private LayoutClassedModelUsage _layoutClassedModelUsage;

	@Inject
	private LayoutClassedModelUsageLocalService
		_layoutClassedModelUsageLocalService;

	@Inject
	private MultiVMPool _multiVMPool;

	private ServiceContext _serviceContext;

}