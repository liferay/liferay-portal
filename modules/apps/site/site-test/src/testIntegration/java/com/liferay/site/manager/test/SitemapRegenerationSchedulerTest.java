/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.manager.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelper;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.scheduler.messaging.SchedulerResponse;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.site.constants.SitemapConstants;
import com.liferay.site.manager.SitemapManager;
import com.liferay.site.storage.helper.SitemapStorageHelper;

import java.io.Serializable;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Cheryl Tang
 */
@RunWith(Arquillian.class)
public class SitemapRegenerationSchedulerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_companyConfigurationTemporarySwapper =
			new CompanyConfigurationTemporarySwapper(
				TestPropsValues.getCompanyId(),
				_PID_SITEMAP_COMPANY_CONFIGURATION,
				HashMapDictionaryBuilder.<String, Object>put(
					"xmlSitemapIndexEnabled", true
				).put(
					"xmlSitemapIndexMode",
					SitemapConstants.INDEX_MODE_ASSET_TYPE
				).build());
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_companyConfigurationTemporarySwapper.close();
	}

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@After
	public void tearDown() throws Exception {
		_deleteRegenerateSitemapScheduledJobs();

		_sitemapStorageHelper.deleteSitemaps(
			TestPropsValues.getCompanyId(), _group.getGroupId());
	}

	@Test
	public void testRegenerateSitemap() throws Exception {
		LayoutTestUtil.addTypePortletLayout(_group);

		long companyId = TestPropsValues.getCompanyId();
		long groupId = _group.getGroupId();

		_sitemapManager.regenerateSitemap(
			SitemapConstants.ASSET_TYPE_KEY_PAGES, companyId, groupId);

		Assert.assertTrue(
			_sitemapStorageHelper.hasSitemapFile(companyId, groupId));

		int pagesSitemapLocCount = _getPagesSitemapLocCount(companyId, groupId);

		Assert.assertTrue(pagesSitemapLocCount > 0);

		LayoutTestUtil.addTypePortletLayout(_group);

		Assert.assertEquals(
			pagesSitemapLocCount, _getPagesSitemapLocCount(companyId, groupId));

		LayoutTestUtil.addTypePortletLayout(_group);

		_sitemapManager.regenerateSitemap(
			SitemapConstants.ASSET_TYPE_KEY_PAGES, companyId, groupId);

		Assert.assertEquals(
			pagesSitemapLocCount + 2,
			_getPagesSitemapLocCount(companyId, groupId));
	}

	@Test
	public void testScheduleRegenerateSitemapDebouncesDuplicateRequests()
		throws Exception {

		_sitemapManager.scheduleRegenerateSitemap(
			SitemapConstants.ASSET_TYPE_KEY_WEB_CONTENT,
			TestPropsValues.getCompanyId(), _group.getGroupId(), null);
		_sitemapManager.scheduleRegenerateSitemap(
			SitemapConstants.ASSET_TYPE_KEY_WEB_CONTENT,
			TestPropsValues.getCompanyId(), _group.getGroupId(), null);

		List<SchedulerResponse> schedulerResponses =
			_getRegenerateSitemapSchedulerResponses(
				SitemapConstants.ASSET_TYPE_KEY_WEB_CONTENT);

		Assert.assertEquals(
			schedulerResponses.toString(), 1, schedulerResponses.size());
	}

	@Test
	public void testScheduleRegenerateSitemapDelay() throws Exception {
		_sitemapManager.scheduleRegenerateSitemap(
			SitemapConstants.ASSET_TYPE_KEY_WEB_CONTENT,
			TestPropsValues.getCompanyId(), _group.getGroupId(), null);

		List<SchedulerResponse> schedulerResponses =
			_getRegenerateSitemapSchedulerResponses(
				SitemapConstants.ASSET_TYPE_KEY_WEB_CONTENT);

		Assert.assertEquals(
			schedulerResponses.toString(), 1, schedulerResponses.size());

		Date startTime = _schedulerEngineHelper.getStartTime(
			schedulerResponses.get(0));

		Assert.assertTrue(
			startTime.toString(),
			startTime.getTime() >
				(System.currentTimeMillis() + (12 * Time.HOUR)));
	}

	@Test
	public void testScheduleRegenerateSitemapWithAddAssetCategory()
		throws Exception {

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId());

		AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());

		_assertRegenerateSitemapScheduledJob(
			SitemapConstants.ASSET_TYPE_KEY_CATEGORIES);
	}

	@Test
	public void testScheduleRegenerateSitemapWithAddJournalArticle()
		throws Exception {

		JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		_assertRegenerateSitemapScheduledJob(
			SitemapConstants.ASSET_TYPE_KEY_WEB_CONTENT);
	}

	@Test
	public void testScheduleRegenerateSitemapWithAddLayout() throws Exception {
		LayoutTestUtil.addTypePortletLayout(_group);

		_assertRegenerateSitemapScheduledJob(
			SitemapConstants.ASSET_TYPE_KEY_PAGES);
	}

	@Test
	public void testScheduleRegenerateSitemapWithAddObjectEntry()
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					_getObjectEntryCompanyConfigurationTemporarySwapper(
						ObjectDefinitionConstants.SCOPE_COMPANY)) {

			_addObjectEntry(0);

			_assertRegenerateSitemapScheduledJob(
				SitemapConstants.ASSET_TYPE_KEY_OBJECT_ENTRIES);
		}
	}

	@Test
	public void testScheduleRegenerateSitemapWithAssetTypeSitemapDisabled()
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						_PID_SITEMAP_COMPANY_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"xmlSitemapIndexEnabled", false
						).build())) {

			_sitemapManager.scheduleRegenerateSitemap(
				SitemapConstants.ASSET_TYPE_KEY_WEB_CONTENT,
				TestPropsValues.getCompanyId(), _group.getGroupId(), null);

			List<SchedulerResponse> schedulerResponses =
				_getRegenerateSitemapSchedulerResponses(
					SitemapConstants.ASSET_TYPE_KEY_WEB_CONTENT);

			Assert.assertEquals(
				schedulerResponses.toString(), 0, schedulerResponses.size());
		}
	}

	@Test
	public void testScheduleRegenerateSitemapWithDelayConfigured()
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						_PID_SITEMAP_COMPANY_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"xmlSitemapIndexEnabled", true
						).put(
							"xmlSitemapIndexMode",
							SitemapConstants.INDEX_MODE_ASSET_TYPE
						).put(
							"xmlSitemapRegenerationDelay", 60L
						).build())) {

			_sitemapManager.scheduleRegenerateSitemap(
				SitemapConstants.ASSET_TYPE_KEY_WEB_CONTENT,
				TestPropsValues.getCompanyId(), _group.getGroupId(), null);

			List<SchedulerResponse> schedulerResponses =
				_getRegenerateSitemapSchedulerResponses(
					SitemapConstants.ASSET_TYPE_KEY_WEB_CONTENT);

			Assert.assertEquals(
				schedulerResponses.toString(), 1, schedulerResponses.size());

			Date startTime = _schedulerEngineHelper.getStartTime(
				schedulerResponses.get(0));

			Assert.assertTrue(
				startTime.toString(),
				startTime.getTime() <
					(System.currentTimeMillis() + (5 * Time.MINUTE)));
		}
	}

	@Test
	public void testScheduleRegenerateSitemapWithDeleteAssetCategory()
		throws Exception {

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId());

		AssetCategory assetCategory = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());

		_deleteRegenerateSitemapScheduledJobs();

		_assetCategoryLocalService.deleteCategory(assetCategory);

		_assertRegenerateSitemapScheduledJob(
			SitemapConstants.ASSET_TYPE_KEY_CATEGORIES);
	}

	@Test
	public void testScheduleRegenerateSitemapWithDeleteJournalArticle()
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		_deleteRegenerateSitemapScheduledJobs();

		_journalArticleLocalService.deleteArticle(journalArticle);

		_assertRegenerateSitemapScheduledJob(
			SitemapConstants.ASSET_TYPE_KEY_WEB_CONTENT);
	}

	@Test
	public void testScheduleRegenerateSitemapWithDeleteLayout()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		_deleteRegenerateSitemapScheduledJobs();

		_layoutLocalService.deleteLayout(layout);

		_assertRegenerateSitemapScheduledJob(
			SitemapConstants.ASSET_TYPE_KEY_PAGES);
	}

	@Test
	public void testScheduleRegenerateSitemapWithDeleteObjectEntry()
		throws Exception {

		ObjectEntry objectEntry = _addObjectEntry(_group.getGroupId());

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					_getObjectEntryCompanyConfigurationTemporarySwapper(
						ObjectDefinitionConstants.SCOPE_SITE)) {

			_deleteRegenerateSitemapScheduledJobs();

			_objectEntryLocalService.deleteObjectEntry(objectEntry);

			_assertRegenerateSitemapScheduledJob(
				SitemapConstants.ASSET_TYPE_KEY_OBJECT_ENTRIES);
		}
	}

	@Test
	public void testScheduleRegenerateSitemapWithUpdateAssetCategory()
		throws Exception {

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId());

		AssetCategory assetCategory = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());

		_deleteRegenerateSitemapScheduledJobs();

		_assetCategoryLocalService.updateAssetCategory(assetCategory);

		_assertRegenerateSitemapScheduledJob(
			SitemapConstants.ASSET_TYPE_KEY_CATEGORIES);
	}

	@Test
	public void testScheduleRegenerateSitemapWithUpdateJournalArticle()
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		_deleteRegenerateSitemapScheduledJobs();

		JournalTestUtil.updateArticle(journalArticle);

		_assertRegenerateSitemapScheduledJob(
			SitemapConstants.ASSET_TYPE_KEY_WEB_CONTENT);
	}

	@Test
	public void testScheduleRegenerateSitemapWithUpdateLayout()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		_deleteRegenerateSitemapScheduledJobs();

		_layoutLocalService.updateLayout(layout);

		_assertRegenerateSitemapScheduledJob(
			SitemapConstants.ASSET_TYPE_KEY_PAGES);
	}

	@Test
	public void testScheduleRegenerateSitemapWithUpdateObjectEntry()
		throws Exception {

		ObjectEntry objectEntry = _addObjectEntry(_group.getGroupId());

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					_getObjectEntryCompanyConfigurationTemporarySwapper(
						ObjectDefinitionConstants.SCOPE_SITE)) {

			_deleteRegenerateSitemapScheduledJobs();

			_objectEntryLocalService.updateObjectEntry(
				TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				Collections.emptyMap(),
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

			_assertRegenerateSitemapScheduledJob(
				SitemapConstants.ASSET_TYPE_KEY_OBJECT_ENTRIES);
		}
	}

	private ObjectEntry _addObjectEntry(long groupId) throws Exception {
		String scope = ObjectDefinitionConstants.SCOPE_SITE;

		if (groupId == 0) {
			scope = ObjectDefinitionConstants.SCOPE_COMPANY;
		}

		ObjectDefinition objectDefinition = _publishObjectDefinition(scope);

		return _objectEntryLocalService.addObjectEntry(
			groupId, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"textObjectField", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));
	}

	private void _assertRegenerateSitemapScheduledJob(String assetTypeKey)
		throws Exception {

		List<SchedulerResponse> schedulerResponses =
			_getRegenerateSitemapSchedulerResponses(assetTypeKey);

		Assert.assertEquals(
			schedulerResponses.toString(), 1, schedulerResponses.size());
	}

	private void _deleteRegenerateSitemapScheduledJobs() throws Exception {
		for (SchedulerResponse schedulerResponse :
				_getRegenerateSitemapSchedulerResponses(null)) {

			_schedulerEngineHelper.delete(
				schedulerResponse.getJobName(),
				schedulerResponse.getGroupName(), StorageType.PERSISTED);
		}
	}

	private CompanyConfigurationTemporarySwapper
			_getObjectEntryCompanyConfigurationTemporarySwapper(String scope)
		throws Exception {

		ObjectDefinition objectDefinition = _publishObjectDefinition(scope);

		return new CompanyConfigurationTemporarySwapper(
			TestPropsValues.getCompanyId(), _PID_SITEMAP_COMPANY_CONFIGURATION,
			HashMapDictionaryBuilder.<String, Object>put(
				"companySitemapObjectDefinitionIds",
				new String[] {
					String.valueOf(objectDefinition.getObjectDefinitionId())
				}
			).put(
				"xmlSitemapIndexEnabled", true
			).put(
				"xmlSitemapIndexMode", SitemapConstants.INDEX_MODE_ASSET_TYPE
			).build());
	}

	private int _getPagesSitemapLocCount(long companyId, long groupId)
		throws Exception {

		String xml = StringUtil.read(
			_sitemapStorageHelper.getSitemapInputStream(
				companyId, groupId, SitemapConstants.ASSET_TYPE_KEY_PAGES, 1));

		return StringUtil.count(xml, "<loc>");
	}

	private List<SchedulerResponse> _getRegenerateSitemapSchedulerResponses(
			String assetTypeKey)
		throws Exception {

		return TransformUtil.transform(
			_schedulerEngineHelper.getScheduledJobs(StorageType.PERSISTED),
			schedulerResponse -> {
				Message message = schedulerResponse.getMessage();

				if ((message == null) ||
					(message.getLong("companyId") !=
						TestPropsValues.getCompanyId()) ||
					(message.getLong("groupId") != _group.getGroupId())) {

					return null;
				}

				if ((assetTypeKey == null) ||
					Objects.equals(
						message.getString("assetTypeKey"), assetTypeKey)) {

					return schedulerResponse;
				}

				return null;
			});
	}

	private ObjectDefinition _publishObjectDefinition(String scope)
		throws Exception {

		if (Objects.equals(scope, ObjectDefinitionConstants.SCOPE_COMPANY) &&
			(_companyObjectDefinition != null)) {

			return _companyObjectDefinition;
		}

		if (Objects.equals(scope, ObjectDefinitionConstants.SCOPE_SITE) &&
			(_siteObjectDefinition != null)) {

			return _siteObjectDefinition;
		}

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"textObjectField"
					).objectFieldSettings(
						Collections.emptyList()
					).build()),
				scope);

		if (Objects.equals(scope, ObjectDefinitionConstants.SCOPE_COMPANY)) {
			_companyObjectDefinition = objectDefinition;
		}
		else {
			_siteObjectDefinition = objectDefinition;
		}

		return objectDefinition;
	}

	private static final String _PID_SITEMAP_COMPANY_CONFIGURATION =
		"com.liferay.site.internal.configuration.SitemapCompanyConfiguration";

	private static CompanyConfigurationTemporarySwapper
		_companyConfigurationTemporarySwapper;

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@DeleteAfterTestRun
	private ObjectDefinition _companyObjectDefinition;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private SchedulerEngineHelper _schedulerEngineHelper;

	@Inject
	private SitemapManager _sitemapManager;

	@Inject
	private SitemapStorageHelper _sitemapStorageHelper;

	@DeleteAfterTestRun
	private ObjectDefinition _siteObjectDefinition;

}