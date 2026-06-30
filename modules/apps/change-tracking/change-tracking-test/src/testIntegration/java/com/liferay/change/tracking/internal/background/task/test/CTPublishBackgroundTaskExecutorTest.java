/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.background.task.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTCollectionService;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.test.util.DLTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.cluster.ClusterExecutorUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author David Truong
 */
@RunWith(Arquillian.class)
public class CTPublishBackgroundTaskExecutorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, CTPublishBackgroundTaskExecutorTest.class.getSimpleName(), null);

		_group = GroupTestUtil.addGroup();
	}

	@After
	public void tearDown() throws Exception {
		ConfigurationTestUtil.deleteConfiguration(_CONFIGURATION_PID);

		GroupTestUtil.deleteGroup(_group);
	}

	@Test
	public void testExecuteDoesNotClearEntityCacheWhenNotClustered()
		throws Exception {

		Assert.assertFalse(ClusterExecutorUtil.isEnabled());

		_addDLFileEntries(5);

		ConfigurationTestUtil.saveConfiguration(
			_CONFIGURATION_PID,
			HashMapDictionaryBuilder.<String, Object>put(
				"entityCacheThreshold", 2
			).build());

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.change.tracking.internal.background.task." +
					"CTPublishBackgroundTaskExecutor",
				LoggerTestUtil.DEBUG)) {

			_ctCollectionService.publishCTCollection(
				TestPropsValues.getUserId(), _ctCollection.getCtCollectionId());

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertFalse(
				logEntries.toString(),
				_hasClearEntityCacheLogEntry(logEntries));
		}
	}

	private void _addDLFileEntries(int count) throws Exception {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			DLFolder dlFolder = DLTestUtil.addDLFolder(_group.getGroupId());

			for (int i = 0; i < count; i++) {
				DLTestUtil.addDLFileEntry(dlFolder.getFolderId());
			}
		}
	}

	private boolean _hasClearEntityCacheLogEntry(List<LogEntry> logEntries) {
		for (LogEntry logEntry : logEntries) {
			String message = logEntry.getMessage();

			if (message.startsWith("Clearing the entity cache")) {
				return true;
			}
		}

		return false;
	}

	private static final String _CONFIGURATION_PID =
		"com.liferay.change.tracking.internal.configuration." +
			"CTEntityCacheConfiguration";

	@DeleteAfterTestRun
	private CTCollection _ctCollection;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private CTCollectionService _ctCollectionService;

	private Group _group;

}