/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.launch.entry.manager.LaunchEntryManager;
import com.liferay.launch.entry.manager.LaunchEntryManagerRegistry;
import com.liferay.object.exception.ObjectValidationRuleEngineException;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

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
 */
@RunWith(Arquillian.class)
public class LaunchEntryRelevantObjectEntryModelListenerTest {

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
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testOnAfterCreate() throws Exception {
		ObjectEntry launchEntryObjectEntry1 = _addLaunchEntryObjectEntry(
			JournalArticle.class.getName(),
			_journalArticle.getResourcePrimKey());

		long draftId1 = _getDraftId(launchEntryObjectEntry1);

		LaunchEntryManager.Draft draft1 = _getDraft(draftId1);

		Assert.assertEquals(WorkflowConstants.STATUS_DRAFT, draft1.getStatus());

		ObjectEntry launchEntryObjectEntry2 = _addLaunchEntryObjectEntry(
			JournalArticle.class.getName(),
			_journalArticle.getResourcePrimKey());

		long draftId2 = _getDraftId(launchEntryObjectEntry2);

		Assert.assertNotEquals(draftId1, draftId2);

		LaunchEntryManager.Draft draft2 = _getDraft(draftId2);

		Assert.assertEquals(WorkflowConstants.STATUS_DRAFT, draft2.getStatus());

		try {
			_addLaunchEntryObjectEntry(
				JournalArticle.class.getName(),
				_journalArticle.getResourcePrimKey(),
				MapUtil.getLong(
					launchEntryObjectEntry1.getValues(),
					"r_launchSetToLaunchEntries_c_launchSetId"));

			Assert.fail();
		}
		catch (ModelListenerException modelListenerException) {
			Assert.assertTrue(
				modelListenerException.getCause() instanceof
					ObjectValidationRuleEngineException);
		}

		ObjectEntry unknownLaunchEntryObjectEntry = _addLaunchEntryObjectEntry(
			RandomTestUtil.randomString(), RandomTestUtil.randomInt());

		Assert.assertEquals(0, _getDraftId(unknownLaunchEntryObjectEntry));
	}

	@Test
	public void testOnAfterUpdate() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.updateArticle(
			_journalArticle, RandomTestUtil.randomString());

		ObjectEntry launchEntryObjectEntry = _addLaunchEntryObjectEntry(
			JournalArticle.class.getName(),
			_journalArticle.getResourcePrimKey());

		long draftId1 = _getDraftId(launchEntryObjectEntry);

		Assert.assertEquals(
			_journalArticle.getTitle(LocaleUtil.getSiteDefault()),
			_getDraftTitle(draftId1));

		_objectEntryLocalService.partialUpdateObjectEntry(
			TestPropsValues.getUserId(),
			launchEntryObjectEntry.getObjectEntryId(),
			launchEntryObjectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"baseVersionKey", String.valueOf(_journalArticle.getVersion())
			).build(),
			ServiceContextThreadLocal.getServiceContext());

		Assert.assertEquals(draftId1, _getDraftId(launchEntryObjectEntry));

		_objectEntryLocalService.partialUpdateObjectEntry(
			TestPropsValues.getUserId(),
			launchEntryObjectEntry.getObjectEntryId(),
			launchEntryObjectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"baseVersionKey", String.valueOf(journalArticle.getVersion())
			).put(
				"className", JournalArticle.class.getName()
			).put(
				"classPK", _journalArticle.getResourcePrimKey()
			).put(
				"draftId", 0
			).build(),
			ServiceContextThreadLocal.getServiceContext());

		long draftId2 = _getDraftId(launchEntryObjectEntry);

		Assert.assertNotEquals(draftId1, draftId2);

		LaunchEntryManager launchEntryManager = _getLaunchEntryManager();

		Assert.assertNull(
			launchEntryManager.fetchDraft(
				_journalArticle.getResourcePrimKey(), draftId1));

		Assert.assertEquals(
			journalArticle.getTitle(LocaleUtil.getSiteDefault()),
			_getDraftTitle(draftId2));
	}

	@Test
	public void testOnBeforeRemove() throws Exception {
		ObjectEntry launchEntryObjectEntry1 = _addLaunchEntryObjectEntry(
			JournalArticle.class.getName(),
			_journalArticle.getResourcePrimKey());
		ObjectEntry launchEntryObjectEntry2 = _addLaunchEntryObjectEntry(
			JournalArticle.class.getName(),
			_journalArticle.getResourcePrimKey());

		long draftId1 = _getDraftId(launchEntryObjectEntry1);
		long draftId2 = _getDraftId(launchEntryObjectEntry2);

		_objectEntryLocalService.deleteObjectEntry(
			launchEntryObjectEntry1.getObjectEntryId());

		LaunchEntryManager launchEntryManager = _getLaunchEntryManager();

		Assert.assertNull(
			launchEntryManager.fetchDraft(
				_journalArticle.getResourcePrimKey(), draftId1));
		Assert.assertNotNull(
			launchEntryManager.fetchDraft(
				_journalArticle.getResourcePrimKey(), draftId2));
	}

	private ObjectEntry _addLaunchEntryObjectEntry(
			String className, long classPK)
		throws Exception {

		ObjectEntry launchSetObjectEntry = _addObjectEntry(
			"LaunchSet",
			HashMapBuilder.<String, Serializable>put(
				"description", RandomTestUtil.randomString()
			).put(
				"name", RandomTestUtil.randomString()
			).build(),
			WorkflowConstants.ACTION_SAVE_DRAFT);

		return _addLaunchEntryObjectEntry(
			className, classPK, launchSetObjectEntry.getObjectEntryId());
	}

	private ObjectEntry _addLaunchEntryObjectEntry(
			String className, long classPK, long launchSetId)
		throws Exception {

		return _addObjectEntry(
			"LaunchEntry",
			HashMapBuilder.<String, Serializable>put(
				"baseVersionKey", String.valueOf(_journalArticle.getVersion())
			).put(
				"className", className
			).put(
				"classPK", classPK
			).put(
				"r_launchSetToLaunchEntries_c_launchSetId", launchSetId
			).build(),
			WorkflowConstants.ACTION_PUBLISH);
	}

	private ObjectEntry _addObjectEntry(
			String objectDefinitionName, Map<String, Serializable> values,
			int workflowAction)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), objectDefinitionName);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId());

		serviceContext.setWorkflowAction(workflowAction);

		return _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(), 0,
			LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()), values,
			serviceContext);
	}

	private LaunchEntryManager.Draft _getDraft(long draftId) throws Exception {
		LaunchEntryManager launchEntryManager = _getLaunchEntryManager();

		LaunchEntryManager.Draft draft = launchEntryManager.fetchDraft(
			_journalArticle.getResourcePrimKey(), draftId);

		Assert.assertNotNull(draft);

		return draft;
	}

	private long _getDraftId(ObjectEntry objectEntry) throws Exception {
		ObjectEntry reloadedObjectEntry =
			_objectEntryLocalService.getObjectEntry(
				objectEntry.getObjectEntryId());

		return MapUtil.getLong(reloadedObjectEntry.getValues(), "draftId");
	}

	private String _getDraftTitle(long draftId) throws Exception {
		JournalArticle journalArticle =
			_journalArticleLocalService.getLatestArticle(
				draftId, WorkflowConstants.STATUS_ANY, false);

		return journalArticle.getTitle(LocaleUtil.getSiteDefault());
	}

	private LaunchEntryManager _getLaunchEntryManager() {
		return _launchEntryManagerRegistry.getLaunchEntryManager(
			JournalArticle.class.getName());
	}

	private Group _group;
	private JournalArticle _journalArticle;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@Inject
	private LaunchEntryManagerRegistry _launchEntryManagerRegistry;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

}