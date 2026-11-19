/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.internal.osgi.util.tracker.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.launch.entry.manager.LaunchEntryManager;
import com.liferay.launch.entry.manager.LaunchEntryManagerRegistry;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.TestInfo;
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
public class LaunchEntryManagerServiceTrackerCustomizerTest {

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
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	@TestInfo("LPD-116519")
	public void testOnAfterRemove() throws Exception {

		// Journal article

		_testOnAfterRemove(
			JournalArticle.class.getName(), this::_addJournalArticle,
			this::_deleteJournalArticle);

		// Journal article with other versions

		long classPK = _addJournalArticle();

		JournalArticle journalArticle =
			_journalArticleLocalService.getLatestArticle(
				classPK, WorkflowConstants.STATUS_ANY);

		JournalTestUtil.updateArticle(
			journalArticle, RandomTestUtil.randomString());

		ObjectEntry launchEntryObjectEntry = _addLaunchEntryObjectEntry(
			JournalArticle.class.getName(), classPK);

		_journalArticleLocalService.deleteArticle(journalArticle);

		_assertLaunchEntryObjectEntryExists(
			JournalArticle.class.getName(), classPK, launchEntryObjectEntry);

		// Layout

		_testOnAfterRemove(
			Layout.class.getName(),
			() -> {
				Layout layout = LayoutTestUtil.addTypeContentPublishedLayout(
					_group, RandomTestUtil.randomString(),
					WorkflowConstants.STATUS_APPROVED);

				return layout.getPlid();
			},
			_layoutLocalService::deleteLayout);
	}

	private long _addJournalArticle() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		return journalArticle.getResourcePrimKey();
	}

	private ObjectEntry _addLaunchEntryObjectEntry(
			String className, long classPK)
		throws Exception {

		LaunchEntryManager launchEntryManager =
			_launchEntryManagerRegistry.getLaunchEntryManager(className);

		LaunchEntryManager.Version publishedVersion =
			launchEntryManager.fetchPublishedVersion(classPK);

		ObjectEntry launchSetObjectEntry = _addObjectEntry(
			"LaunchSet",
			HashMapBuilder.<String, Serializable>put(
				"name", RandomTestUtil.randomString()
			).build(),
			WorkflowConstants.ACTION_SAVE_DRAFT);

		return _addObjectEntry(
			"LaunchEntry",
			HashMapBuilder.<String, Serializable>put(
				"baseVersionKey", publishedVersion.getVersionKey()
			).put(
				"className", className
			).put(
				"classPK", classPK
			).put(
				"r_launchSetToLaunchEntries_c_launchSetId",
				launchSetObjectEntry.getObjectEntryId()
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

	private void _assertLaunchEntryObjectEntryDeleted(
		String className, long classPK, long draftId,
		ObjectEntry launchEntryObjectEntry) {

		Assert.assertNull(
			_objectEntryLocalService.fetchObjectEntry(
				launchEntryObjectEntry.getObjectEntryId()));

		LaunchEntryManager launchEntryManager =
			_launchEntryManagerRegistry.getLaunchEntryManager(className);

		Assert.assertNull(launchEntryManager.fetchDraft(classPK, draftId));
	}

	private void _assertLaunchEntryObjectEntryExists(
			String className, long classPK, ObjectEntry launchEntryObjectEntry)
		throws Exception {

		LaunchEntryManager launchEntryManager =
			_launchEntryManagerRegistry.getLaunchEntryManager(className);

		Assert.assertNotNull(
			launchEntryManager.fetchDraft(
				classPK, _getDraftId(launchEntryObjectEntry)));
	}

	private void _deleteJournalArticle(long classPK) throws Exception {
		JournalArticle journalArticle =
			_journalArticleLocalService.getLatestArticle(
				classPK, WorkflowConstants.STATUS_ANY);

		_journalArticleLocalService.deleteArticle(
			journalArticle.getGroupId(), journalArticle.getArticleId(),
			ServiceContextThreadLocal.getServiceContext());
	}

	private long _getDraftId(ObjectEntry launchEntryObjectEntry)
		throws Exception {

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			launchEntryObjectEntry.getObjectEntryId());

		return MapUtil.getLong(objectEntry.getValues(), "draftId");
	}

	private void _testOnAfterRemove(
			String className,
			UnsafeSupplier<Long, Exception> addItemUnsafeSupplier,
			UnsafeConsumer<Long, Exception> deleteItemUnsafeConsumer)
		throws Exception {

		long classPK1 = addItemUnsafeSupplier.get();

		ObjectEntry launchEntryObjectEntry1 = _addLaunchEntryObjectEntry(
			className, classPK1);
		ObjectEntry launchEntryObjectEntry2 = _addLaunchEntryObjectEntry(
			className, classPK1);

		long classPK2 = addItemUnsafeSupplier.get();

		ObjectEntry launchEntryObjectEntry3 = _addLaunchEntryObjectEntry(
			className, classPK2);

		long draftId1 = _getDraftId(launchEntryObjectEntry1);
		long draftId2 = _getDraftId(launchEntryObjectEntry2);

		deleteItemUnsafeConsumer.accept(classPK1);

		_assertLaunchEntryObjectEntryDeleted(
			className, classPK1, draftId1, launchEntryObjectEntry1);
		_assertLaunchEntryObjectEntryDeleted(
			className, classPK1, draftId2, launchEntryObjectEntry2);

		_assertLaunchEntryObjectEntryExists(
			className, classPK2, launchEntryObjectEntry3);
	}

	private Group _group;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@Inject
	private LaunchEntryManagerRegistry _launchEntryManagerRegistry;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

}