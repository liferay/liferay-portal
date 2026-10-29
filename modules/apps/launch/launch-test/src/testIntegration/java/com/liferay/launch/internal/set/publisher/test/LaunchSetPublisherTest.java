/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.internal.set.publisher.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.launch.entry.manager.LaunchEntryManager;
import com.liferay.launch.entry.manager.LaunchEntryManagerRegistry;
import com.liferay.launch.set.publisher.LaunchSetPublisher;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelper;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.scheduler.messaging.SchedulerResponse;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.portlet.MockActionResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.PortletException;

import java.io.Serializable;

import java.util.Date;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alejandro Tardín
 * @author Jose Luis Navarro
 */
@RunWith(Arquillian.class)
public class LaunchSetPublisherTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), TestPropsValues.getUserId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);

		_launchSetObjectEntry = _addLaunchSetObjectEntry();
	}

	@After
	public void tearDown() throws Exception {
		_launchSetPublisher.unschedulePublish(
			_launchSetObjectEntry.getObjectEntryId());

		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testPublish() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		ObjectEntry launchEntryObjectEntry = _addLaunchEntryObjectEntry(
			_launchSetObjectEntry.getObjectEntryId(),
			JournalArticle.class.getName(), journalArticle.getResourcePrimKey(),
			String.valueOf(journalArticle.getVersion()));

		String title = RandomTestUtil.randomString();

		long draftId = _updateLaunchDraft(launchEntryObjectEntry, title);

		String unknownClassName = RandomTestUtil.randomString();

		ObjectEntry unknownLaunchEntryObjectEntry = _addLaunchEntryObjectEntry(
			_launchSetObjectEntry.getObjectEntryId(), unknownClassName,
			RandomTestUtil.randomInt(), RandomTestUtil.randomString());

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setParameter(
			"launchSetId",
			String.valueOf(_launchSetObjectEntry.getObjectEntryId()));

		try {
			_mvcActionCommand.processAction(
				mockLiferayPortletActionRequest, new MockActionResponse());

			Assert.fail();
		}
		catch (PortletException portletException) {
			Throwable throwable = portletException.getCause();

			Assert.assertEquals(
				"No launch entry manager exists for " + unknownClassName,
				throwable.getMessage());
		}

		LaunchEntryManager launchEntryManager =
			_getJournalArticleLaunchEntryManager();

		LaunchEntryManager.Version version =
			launchEntryManager.fetchPublishedVersion(
				journalArticle.getResourcePrimKey());

		Assert.assertEquals(
			String.valueOf(journalArticle.getVersion()),
			version.getVersionKey());

		ObjectEntry launchSetObjectEntry =
			_objectEntryLocalService.getObjectEntry(
				_launchSetObjectEntry.getObjectEntryId());

		Assert.assertNotEquals(
			WorkflowConstants.STATUS_APPROVED,
			launchSetObjectEntry.getStatus());

		_objectEntryLocalService.deleteObjectEntry(
			unknownLaunchEntryObjectEntry);

		_launchSetPublisher.publish(_launchSetObjectEntry.getObjectEntryId());

		LaunchEntryManager.Version publishedVersion =
			launchEntryManager.fetchPublishedVersion(
				journalArticle.getResourcePrimKey());

		Assert.assertEquals(title, _getTitle(journalArticle));
		Assert.assertNotEquals(
			String.valueOf(journalArticle.getVersion()),
			publishedVersion.getVersionKey());

		_assertPublished(
			draftId, launchEntryObjectEntry, launchEntryManager,
			publishedVersion);

		launchSetObjectEntry = _objectEntryLocalService.getObjectEntry(
			_launchSetObjectEntry.getObjectEntryId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED,
			launchSetObjectEntry.getStatus());

		try {
			_launchSetPublisher.publish(
				_launchSetObjectEntry.getObjectEntryId());

			Assert.fail();
		}
		catch (PortalException portalException) {
			Assert.assertTrue(
				portalException.getMessage(),
				StringUtil.endsWith(
					portalException.getMessage(), "was already published"));
		}

		ObjectEntry otherLaunchSetObjectEntry = _addLaunchSetObjectEntry();

		ObjectEntry otherLaunchEntryObjectEntry = _addLaunchEntryObjectEntry(
			otherLaunchSetObjectEntry.getObjectEntryId(),
			JournalArticle.class.getName(), journalArticle.getResourcePrimKey(),
			String.valueOf(journalArticle.getVersion()));

		String otherTitle = RandomTestUtil.randomString();

		long otherDraftId = _updateLaunchDraft(
			otherLaunchEntryObjectEntry, otherTitle);

		JournalTestUtil.updateArticle(
			_journalArticleLocalService.getLatestArticle(
				journalArticle.getResourcePrimKey(),
				WorkflowConstants.STATUS_APPROVED),
			RandomTestUtil.randomString());

		_launchSetPublisher.publish(
			otherLaunchSetObjectEntry.getObjectEntryId());

		LaunchEntryManager.Version otherPublishedVersion =
			launchEntryManager.fetchPublishedVersion(
				journalArticle.getResourcePrimKey());

		Assert.assertEquals(otherTitle, _getTitle(journalArticle));
		Assert.assertNotEquals(
			publishedVersion.getVersionKey(),
			otherPublishedVersion.getVersionKey());

		_assertPublished(
			otherDraftId, otherLaunchEntryObjectEntry, launchEntryManager,
			otherPublishedVersion);
	}

	@Test
	public void testSchedulePublish() throws Exception {
		long launchSetId = _launchSetObjectEntry.getObjectEntryId();

		ObjectEntry launchEntryObjectEntry = _addLaunchEntryObjectEntry(
			launchSetId, RandomTestUtil.randomString(),
			RandomTestUtil.randomInt(), RandomTestUtil.randomString());

		Assert.assertThrows(
			PortalException.class,
			() -> _launchSetPublisher.schedulePublish(
				new Date(System.currentTimeMillis() + Time.DAY),
				launchEntryObjectEntry.getObjectEntryId(),
				TestPropsValues.getUserId()));

		Assert.assertThrows(
			PortalException.class,
			() -> _launchSetPublisher.schedulePublish(
				new Date(System.currentTimeMillis() - Time.MINUTE), launchSetId,
				TestPropsValues.getUserId()));
		Assert.assertThrows(
			PortalException.class,
			() -> _launchSetPublisher.schedulePublish(
				null, launchSetId, TestPropsValues.getUserId()));

		Date date = new Date(System.currentTimeMillis() + Time.DAY);

		_launchSetPublisher.schedulePublish(
			date, launchSetId, TestPropsValues.getUserId());

		_assertScheduledDateAndStatus(date, WorkflowConstants.STATUS_SCHEDULED);

		Assert.assertThrows(
			PortalException.class,
			() -> _launchSetPublisher.schedulePublish(
				new Date(System.currentTimeMillis() + Time.DAY), launchSetId,
				TestPropsValues.getUserId()));
		_assertScheduledDateAndStatus(date, WorkflowConstants.STATUS_SCHEDULED);
	}

	@Test
	public void testUnschedulePublish() throws Exception {
		long launchSetId = _launchSetObjectEntry.getObjectEntryId();

		ObjectEntry launchEntryObjectEntry = _addLaunchEntryObjectEntry(
			launchSetId, RandomTestUtil.randomString(),
			RandomTestUtil.randomInt(), RandomTestUtil.randomString());

		Assert.assertThrows(
			PortalException.class,
			() -> _launchSetPublisher.unschedulePublish(
				launchEntryObjectEntry.getObjectEntryId()));

		_launchSetPublisher.unschedulePublish(launchSetId);

		_assertScheduledDateAndStatus(null, WorkflowConstants.STATUS_DRAFT);

		_launchSetPublisher.schedulePublish(
			new Date(System.currentTimeMillis() + Time.DAY), launchSetId,
			TestPropsValues.getUserId());

		_launchSetPublisher.unschedulePublish(launchSetId);

		_assertScheduledDateAndStatus(null, WorkflowConstants.STATUS_DRAFT);

		Date date = new Date(System.currentTimeMillis() + Time.WEEK);

		_launchSetPublisher.schedulePublish(
			date, launchSetId, TestPropsValues.getUserId());

		_assertScheduledDateAndStatus(date, WorkflowConstants.STATUS_SCHEDULED);
	}

	private ObjectEntry _addLaunchEntryObjectEntry(
			long launchSetId, String className, long classPK,
			String baseVersionKey)
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
				"r_launchSetToLaunchEntries_c_launchSetId", launchSetId
			).build());
	}

	private ObjectEntry _addLaunchSetObjectEntry() throws Exception {
		return _addObjectEntry(
			"LaunchSet",
			HashMapBuilder.<String, Serializable>put(
				"description", RandomTestUtil.randomString()
			).put(
				"name", RandomTestUtil.randomString()
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

	private void _assertPublished(
			long draftId, ObjectEntry launchEntryObjectEntry,
			LaunchEntryManager launchEntryManager,
			LaunchEntryManager.Version publishedVersion)
		throws Exception {

		Map<String, Serializable> values = _getValues(launchEntryObjectEntry);

		Assert.assertEquals(
			publishedVersion.getVersionKey(),
			MapUtil.getString(values, "versionKey"));
		Assert.assertEquals(0, MapUtil.getLong(values, "draftId"));

		Assert.assertNull(
			launchEntryManager.fetchDraft(
				MapUtil.getLong(values, "classPK"), draftId));
	}

	private void _assertScheduledDateAndStatus(
			Date expectedDate, int expectedStatus)
		throws Exception {

		long launchSetId = _launchSetObjectEntry.getObjectEntryId();

		ObjectEntry launchSetObjectEntry =
			_objectEntryLocalService.getObjectEntry(launchSetId);

		Assert.assertEquals(expectedStatus, launchSetObjectEntry.getStatus());

		SchedulerResponse schedulerResponse =
			_schedulerEngineHelper.getScheduledJob(
				StringBundler.concat(
					launchSetId, StringPool.AT,
					launchSetObjectEntry.getCompanyId()),
				"liferay/launch_set_scheduled_publish", StorageType.PERSISTED);

		Date scheduledDate = null;

		if (schedulerResponse != null) {
			scheduledDate = _schedulerEngineHelper.getStartDate(
				schedulerResponse);
		}

		Map<String, Serializable> values = launchSetObjectEntry.getValues();

		Date scheduledDateValue = (Date)values.get("scheduledDate");

		if (expectedDate == null) {
			Assert.assertNull(scheduledDate);
			Assert.assertNull(scheduledDateValue);

			return;
		}

		Assert.assertEquals(expectedDate, scheduledDate);
		Assert.assertEquals(
			expectedDate.getTime() / Time.SECOND,
			scheduledDateValue.getTime() / Time.SECOND);
	}

	private LaunchEntryManager _getJournalArticleLaunchEntryManager() {
		return _launchEntryManagerRegistry.getLaunchEntryManager(
			JournalArticle.class.getName());
	}

	private String _getTitle(JournalArticle journalArticle) throws Exception {
		JournalArticle latestJournalArticle =
			_journalArticleLocalService.getLatestArticle(
				journalArticle.getResourcePrimKey(),
				WorkflowConstants.STATUS_APPROVED);

		return latestJournalArticle.getTitle(LocaleUtil.getSiteDefault());
	}

	private Map<String, Serializable> _getValues(ObjectEntry objectEntry)
		throws Exception {

		ObjectEntry reloadedObjectEntry =
			_objectEntryLocalService.getObjectEntry(
				objectEntry.getObjectEntryId());

		return reloadedObjectEntry.getValues();
	}

	private long _updateLaunchDraft(
			ObjectEntry launchEntryObjectEntry, String title)
		throws Exception {

		long draftId = MapUtil.getLong(
			_getValues(launchEntryObjectEntry), "draftId");

		JournalArticle launchDraftJournalArticle =
			_journalArticleLocalService.getLatestArticle(
				draftId, WorkflowConstants.STATUS_ANY, false);

		ServiceContext launchDraftServiceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		launchDraftServiceContext.setWorkflowAction(
			WorkflowConstants.ACTION_SAVE_DRAFT);

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
			launchDraftJournalArticle.getLayoutUuid(),
			launchDraftServiceContext);

		return draftId;
	}

	private Group _group;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@Inject
	private LaunchEntryManagerRegistry _launchEntryManagerRegistry;

	private ObjectEntry _launchSetObjectEntry;

	@Inject
	private LaunchSetPublisher _launchSetPublisher;

	@Inject(filter = "mvc.command.name=/launch/publish_launch")
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private SchedulerEngineHelper _schedulerEngineHelper;

	private ServiceContext _serviceContext;

}