/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.internal.messaging.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManager;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jose Luis Navarro
 */
@RunWith(Arquillian.class)
public class LaunchSetScheduledPublishMessageListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		_journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		_launchSetObjectEntry = _addObjectEntry(
			"LaunchSet",
			HashMapBuilder.<String, Serializable>put(
				"name", RandomTestUtil.randomString()
			).build());
	}

	@After
	public void tearDown() throws Exception {
		for (BackgroundTask backgroundTask : _getBackgroundTasks()) {
			_backgroundTaskManager.deleteBackgroundTask(
				backgroundTask.getBackgroundTaskId());
		}

		_objectEntryLocalService.deleteObjectEntry(
			_launchSetObjectEntry.getObjectEntryId());

		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testReceive() throws Exception {

		// Launch set that is not scheduled

		String title = RandomTestUtil.randomString();

		_updateLaunchDraft(
			_addLaunchEntryObjectEntry(
				JournalArticle.class.getName(),
				_journalArticle.getResourcePrimKey(),
				String.valueOf(_journalArticle.getVersion())),
			title);

		_receive(_launchSetObjectEntry.getObjectEntryId());

		_assertStatus(WorkflowConstants.STATUS_DRAFT);

		List<BackgroundTask> backgroundTasks = _getBackgroundTasks();

		Assert.assertEquals(
			backgroundTasks.toString(), 0, backgroundTasks.size());

		Assert.assertNotEquals(title, _getPublishedTitle());

		// Missing launch set, for example deleted after it was scheduled

		_receive(RandomTestUtil.randomLong());

		// Scheduled launch set

		_updateStatus(WorkflowConstants.STATUS_SCHEDULED);

		_receive(_launchSetObjectEntry.getObjectEntryId());

		_assertBackgroundTasks(1, BackgroundTaskConstants.STATUS_SUCCESSFUL);
		_assertStatus(WorkflowConstants.STATUS_APPROVED);

		Assert.assertEquals(title, _getPublishedTitle());

		// Scheduled launch set whose user is missing

		_updateStatus(WorkflowConstants.STATUS_SCHEDULED);

		_receiveWithError(RandomTestUtil.randomLong());

		_assertBackgroundTasks(2, BackgroundTaskConstants.STATUS_FAILED);
		_assertStatus(WorkflowConstants.STATUS_DRAFT);

		// Scheduled launch set with a publish error

		_addLaunchEntryObjectEntry(
			RandomTestUtil.randomString(), RandomTestUtil.randomInt(),
			RandomTestUtil.randomString());

		_updateStatus(WorkflowConstants.STATUS_SCHEDULED);

		_receiveWithError(TestPropsValues.getUserId());

		_assertBackgroundTasks(3, BackgroundTaskConstants.STATUS_FAILED);
		_assertStatus(WorkflowConstants.STATUS_DRAFT);
	}

	private ObjectEntry _addLaunchEntryObjectEntry(
			String className, long classPK, String baseVersionKey)
		throws Exception {

		return _addObjectEntry(
			"LaunchEntry",
			HashMapBuilder.<String, Serializable>put(
				"baseVersionKey", baseVersionKey
			).put(
				"className", className
			).put(
				"classPK", classPK
			).put(
				"r_launchSetToLaunchEntries_c_launchSetId",
				_launchSetObjectEntry.getObjectEntryId()
			).build());
	}

	private ObjectEntry _addObjectEntry(
			String objectDefinitionName, Map<String, Serializable> values)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), objectDefinitionName);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId());

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		return _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(), 0,
			LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()), values,
			serviceContext);
	}

	private void _assertBackgroundTasks(int expectedCount, int expectedStatus) {
		List<BackgroundTask> backgroundTasks = ListUtil.sort(
			_getBackgroundTasks(),
			Comparator.comparingLong(BackgroundTask::getBackgroundTaskId));

		Assert.assertEquals(
			backgroundTasks.toString(), expectedCount, backgroundTasks.size());

		BackgroundTask backgroundTask = backgroundTasks.get(
			backgroundTasks.size() - 1);

		Assert.assertEquals(expectedStatus, backgroundTask.getStatus());
	}

	private void _assertStatus(int expectedStatus) throws Exception {
		ObjectEntry launchSetObjectEntry =
			_objectEntryLocalService.getObjectEntry(
				_launchSetObjectEntry.getObjectEntryId());

		Assert.assertEquals(expectedStatus, launchSetObjectEntry.getStatus());
	}

	private List<BackgroundTask> _getBackgroundTasks() {
		return ListUtil.filter(
			_backgroundTaskManager.getBackgroundTasks(
				BackgroundTaskConstants.GROUP_ID_DEFAULT,
				"com.liferay.launch.internal.background.task." +
					"LaunchSetScheduledPublishBackgroundTaskExecutor"),
			backgroundTask -> Objects.equals(
				backgroundTask.getName(),
				String.valueOf(_launchSetObjectEntry.getObjectEntryId())));
	}

	private String _getPublishedTitle() throws Exception {
		JournalArticle journalArticle =
			_journalArticleLocalService.getLatestArticle(
				_journalArticle.getResourcePrimKey(),
				WorkflowConstants.STATUS_APPROVED);

		return journalArticle.getTitle(LocaleUtil.getSiteDefault());
	}

	private void _receive(long launchSetId) throws Exception {
		_receive(launchSetId, TestPropsValues.getUserId());
	}

	private void _receive(long launchSetId, long userId) throws Exception {

		// The scheduler delivers the message on a thread with no company,
		// user, permission checker, or service context

		Message message = new Message();

		message.put("companyId", TestPropsValues.getCompanyId());
		message.put("launchSetId", launchSetId);
		message.put("userId", userId);

		AtomicReference<Throwable> throwableReference = new AtomicReference<>();

		Thread thread = new Thread(
			() -> {
				try {
					_messageBus.sendMessage(
						"liferay/launch_set_scheduled_publish", message);
				}
				catch (Throwable throwable) {
					throwableReference.set(throwable);
				}
			});

		thread.start();

		thread.join();

		Throwable throwable = throwableReference.get();

		if (throwable != null) {
			throw new AssertionError(throwable);
		}
	}

	private void _receiveWithError(long userId) throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.launch.internal.background.task." +
					"LaunchSetScheduledPublishBackgroundTaskExecutor",
				LoggerTestUtil.ERROR)) {

			_receive(_launchSetObjectEntry.getObjectEntryId(), userId);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"Unable to publish launch set " +
					_launchSetObjectEntry.getObjectEntryId(),
				logEntry.getMessage());
		}
	}

	private void _updateLaunchDraft(
			ObjectEntry launchEntryObjectEntry, String title)
		throws Exception {

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			launchEntryObjectEntry.getObjectEntryId());

		JournalArticle launchDraftJournalArticle =
			_journalArticleLocalService.getLatestArticle(
				MapUtil.getLong(objectEntry.getValues(), "draftId"),
				WorkflowConstants.STATUS_ANY, false);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		_journalArticleLocalService.updateArticle(
			TestPropsValues.getUserId(), launchDraftJournalArticle.getGroupId(),
			launchDraftJournalArticle.getFolderId(),
			launchDraftJournalArticle.getArticleId(),
			launchDraftJournalArticle.getVersion(),
			HashMapBuilder.put(
				LocaleUtil.getSiteDefault(), title
			).build(),
			launchDraftJournalArticle.getDescriptionMap(),
			launchDraftJournalArticle.getContent(),
			launchDraftJournalArticle.getLayoutUuid(), serviceContext);
	}

	private void _updateStatus(int status) throws Exception {
		_objectEntryLocalService.updateStatus(
			TestPropsValues.getUserId(),
			_launchSetObjectEntry.getObjectEntryId(), status,
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId()));
	}

	@Inject
	private BackgroundTaskManager _backgroundTaskManager;

	@DeleteAfterTestRun
	private Group _group;

	private JournalArticle _journalArticle;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	private ObjectEntry _launchSetObjectEntry;

	@Inject
	private MessageBus _messageBus;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

}