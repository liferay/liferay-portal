/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTProcess;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTProcessLocalService;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalFolderLocalService;
import com.liferay.journal.test.util.JournalFolderFixture;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.background.task.model.BackgroundTask;
import com.liferay.portal.background.task.service.BackgroundTaskLocalService;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
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
 * @author Gislayne Vitorino
 */
@RunWith(Arquillian.class)
public class CTProcessLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_journalFolderClassNameId = _classNameLocalService.getClassNameId(
			JournalFolder.class);
	}

	@Test
	public void testCannotAddCTProcessWithEmptyCTCollection()
		throws PortalException {

		CTCollection ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, CTCollectionLocalServiceTest.class.getSimpleName(), null);

		try {
			_ctProcessLocalService.addCTProcess(
				ctCollection.getUserId(), ctCollection.getCtCollectionId());

			Assert.fail();
		}
		catch (IllegalStateException illegalStateException) {
			Assert.assertEquals(
				"Change tracking collection is empty " + ctCollection,
				illegalStateException.getMessage());
		}
	}

	@Test
	public void testDeleteCTCollectionWithCTProcess() throws Exception {
		CTCollection ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, CTCollectionLocalServiceTest.class.getSimpleName(), null);

		String conflictingFolderName = "conflictingFolderName";

		JournalFolder ctJournalFolder = null;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection.getCtCollectionId())) {

			ctJournalFolder = _journalFolderFixture.addFolder(
				_group.getGroupId(), conflictingFolderName);

			_journalFolderFixture.addFolder(
				_group.getGroupId(), RandomTestUtil.randomString());
		}

		_journalFolderFixture.addFolder(
			_group.getGroupId(), conflictingFolderName);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.background.task.internal.messaging." +
					"BackgroundTaskMessageListener",
				LoggerTestUtil.ERROR)) {

			CTProcess ctProcess = _ctProcessLocalService.addCTProcess(
				ctCollection.getUserId(), ctCollection.getCtCollectionId());

			BackgroundTask backgroundTask =
				_backgroundTaskLocalService.getBackgroundTask(
					ctProcess.getBackgroundTaskId());

			Assert.assertEquals(
				BackgroundTaskConstants.STATUS_FAILED,
				backgroundTask.getStatus());

			ctProcess = _ctProcessLocalService.deleteCTProcess(
				ctProcess.getCtProcessId());

			ctCollection = _ctCollectionLocalService.fetchCTCollection(
				ctProcess.getCtCollectionId());

			Assert.assertNotNull(ctCollection);

			_ctCollectionLocalService.discardCTEntry(
				ctCollection.getCtCollectionId(), _journalFolderClassNameId,
				ctJournalFolder.getFolderId(), false);

			ctProcess = _ctProcessLocalService.addCTProcess(
				ctCollection.getUserId(), ctCollection.getCtCollectionId());

			backgroundTask = _backgroundTaskLocalService.getBackgroundTask(
				ctProcess.getBackgroundTaskId());

			Assert.assertEquals(
				BackgroundTaskConstants.STATUS_SUCCESSFUL,
				backgroundTask.getStatus());

			ctProcess = _ctProcessLocalService.deleteCTProcess(
				ctProcess.getCtProcessId());

			ctCollection = _ctCollectionLocalService.fetchCTCollection(
				ctProcess.getCtCollectionId());

			Assert.assertNull(ctCollection);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"Unable to execute background task", logEntry.getMessage());
		}
	}

	@Inject
	private static JournalFolderLocalService _journalFolderLocalService;

	@Inject
	private BackgroundTaskLocalService _backgroundTaskLocalService;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private CTProcessLocalService _ctProcessLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private long _journalFolderClassNameId;
	private final JournalFolderFixture _journalFolderFixture =
		new JournalFolderFixture(_journalFolderLocalService);

}