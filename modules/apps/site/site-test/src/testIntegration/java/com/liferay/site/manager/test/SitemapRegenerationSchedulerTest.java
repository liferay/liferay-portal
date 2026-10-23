/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.manager.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.display.page.constants.AssetDisplayPageConstants;
import com.liferay.asset.display.page.service.AssetDisplayPageEntryLocalService;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.admin.kernel.model.LayoutTypePortletConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.lock.LockManager;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelper;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.scheduler.messaging.SchedulerResponse;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.test.util.IdempotentRetryAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.site.constants.SitemapConstants;
import com.liferay.site.manager.SitemapManager;
import com.liferay.site.model.SiteSitemapRegenerationEntry;
import com.liferay.site.service.SiteSitemapRegenerationEntryLocalService;
import com.liferay.site.storage.helper.SitemapStorageHelper;

import java.io.Serializable;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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
@Sync
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
					"cachedGenerationEnabled", true
				).put(
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
		_deleteSiteSitemapRegenerationEntries();

		_group = GroupTestUtil.addGroup();
	}

	@After
	public void tearDown() throws Exception {
		_deleteRegenerateSitemapScheduledJobs();

		_deleteSiteSitemapRegenerationEntries();

		_sitemapStorageHelper.deleteSitemaps(
			TestPropsValues.getCompanyId(), _group.getGroupId());
	}

	@Test
	public void testDrainDiscardsEntriesWithDeletedGroups() throws Exception {
		long companyId = TestPropsValues.getCompanyId();

		_siteSitemapRegenerationEntryLocalService.
			addSiteSitemapRegenerationEntry(
				SitemapConstants.ASSET_TYPE_KEY_PAGES, companyId,
				RandomTestUtil.randomLong());

		UnsafeConsumer<Long, Exception> unsafeConsumer =
			_schedulerJobConfiguration.getCompanyJobExecutorUnsafeConsumer();

		unsafeConsumer.accept(companyId);

		List<SiteSitemapRegenerationEntry> siteSitemapRegenerationEntries =
			_siteSitemapRegenerationEntryLocalService.
				getSiteSitemapRegenerationEntries(companyId);

		Assert.assertTrue(siteSitemapRegenerationEntries.isEmpty());

		Assert.assertFalse(
			_sitemapStorageHelper.hasSitemapFile(
				companyId, _group.getGroupId()));
	}

	@Test
	public void testDrainRegeneratesPageLayoutEntries() throws Exception {
		Group group = GroupTestUtil.addGroup();
		long companyId = TestPropsValues.getCompanyId();

		try {
			Layout layout1 = LayoutTestUtil.addTypePortletLayout(_group);
			Layout layout2 = LayoutTestUtil.addTypePortletLayout(group);

			_deleteSiteSitemapRegenerationEntries();

			_siteSitemapRegenerationEntryLocalService.
				addSiteSitemapRegenerationEntry(
					SitemapConstants.INDEX_MODE_PAGE_LAYOUT, companyId,
					_group.getGroupId());
			_siteSitemapRegenerationEntryLocalService.
				addSiteSitemapRegenerationEntry(
					_getPageLayoutChildSitemapKey(layout1), companyId,
					_group.getGroupId());
			_siteSitemapRegenerationEntryLocalService.
				addSiteSitemapRegenerationEntry(
					_getPageLayoutChildSitemapKey(layout2), companyId,
					group.getGroupId());

			try (CompanyConfigurationTemporarySwapper
					companyConfigurationTemporarySwapper =
						_getPageLayoutCompanyConfigurationTemporarySwapper()) {

				String key = StringBundler.concat(
					SitemapConstants.INDEX_MODE_PAGE_LAYOUT, StringPool.SLASH,
					_group.getGroupId(), StringPool.AT, companyId);

				_lockManager.lock(
					TestPropsValues.getUserId(),
					_CLASS_NAME_SITEMAP_MANAGER_IMPL, key,
					_CLASS_NAME_SITEMAP_MANAGER_IMPL, false, Time.MINUTE,
					false);

				try {
					UnsafeConsumer<Long, Exception> unsafeConsumer =
						_schedulerJobConfiguration.
							getCompanyJobExecutorUnsafeConsumer();

					unsafeConsumer.accept(companyId);
				}
				finally {
					_lockManager.unlock(_CLASS_NAME_SITEMAP_MANAGER_IMPL, key);
				}
			}

			List<SiteSitemapRegenerationEntry> siteSitemapRegenerationEntries =
				_siteSitemapRegenerationEntryLocalService.
					getSiteSitemapRegenerationEntries(companyId);

			Assert.assertEquals(
				siteSitemapRegenerationEntries.toString(), 1,
				siteSitemapRegenerationEntries.size());

			_assertSiteSitemapRegenerationEntry(
				SitemapConstants.INDEX_MODE_PAGE_LAYOUT);

			Assert.assertTrue(
				_sitemapStorageHelper.hasSitemapFile(
					companyId, group.getGroupId(),
					_getPageLayoutChildSitemapKey(layout2), 1));
		}
		finally {
			_sitemapStorageHelper.deleteSitemaps(companyId, group.getGroupId());

			GroupTestUtil.deleteGroup(group);
		}
	}

	@Test
	public void testDrainRegeneratesQueuedEntries() throws Exception {
		LayoutTestUtil.addTypePortletLayout(_group);

		long companyId = TestPropsValues.getCompanyId();

		_siteSitemapRegenerationEntryLocalService.
			addSiteSitemapRegenerationEntry(
				SitemapConstants.ASSET_TYPE_KEY_PAGES, companyId,
				_group.getGroupId());

		UnsafeConsumer<Long, Exception> unsafeConsumer =
			_schedulerJobConfiguration.getCompanyJobExecutorUnsafeConsumer();

		unsafeConsumer.accept(companyId);

		List<SiteSitemapRegenerationEntry> siteSitemapRegenerationEntries =
			_siteSitemapRegenerationEntryLocalService.
				getSiteSitemapRegenerationEntries(companyId);

		Assert.assertTrue(siteSitemapRegenerationEntries.isEmpty());

		Assert.assertTrue(
			_sitemapStorageHelper.hasSitemapFile(
				companyId, _group.getGroupId()));
	}

	@Test
	public void testGetNextRegenerateSitemapDate() throws Exception {
		long companyId = TestPropsValues.getCompanyId();

		_sitemapManager.scheduleRegenerateSitemap(
			SitemapConstants.ASSET_TYPE_KEY_WEB_CONTENT, companyId,
			_group.getGroupId(),
			new Date(System.currentTimeMillis() + Time.DAY));

		Assert.assertEquals(
			_getNextFireDate(SitemapConstants.ASSET_TYPE_KEY_WEB_CONTENT),
			_sitemapManager.getNextRegenerateSitemapDate(companyId));
	}

	@Test
	public void testGetNextRegenerateSitemapDateWithMultipleJobsScheduled()
		throws Exception {

		long companyId = TestPropsValues.getCompanyId();
		long groupId = _group.getGroupId();

		_sitemapManager.scheduleRegenerateSitemap(
			SitemapConstants.ASSET_TYPE_KEY_WEB_CONTENT, companyId, groupId,
			new Date(System.currentTimeMillis() + (2 * Time.DAY)));
		_sitemapManager.scheduleRegenerateSitemap(
			SitemapConstants.ASSET_TYPE_KEY_PAGES, companyId, groupId,
			new Date(System.currentTimeMillis() + Time.DAY));

		Date nextFireDate = _getNextFireDate(
			SitemapConstants.ASSET_TYPE_KEY_PAGES);

		Assert.assertTrue(
			nextFireDate.before(
				_getNextFireDate(SitemapConstants.ASSET_TYPE_KEY_WEB_CONTENT)));
		Assert.assertEquals(
			nextFireDate,
			_sitemapManager.getNextRegenerateSitemapDate(companyId));
	}

	@Test
	public void testGetNextRegenerateSitemapDateWithNoJobScheduled()
		throws Exception {

		_deleteRegenerateSitemapScheduledJobs();

		Assert.assertNull(
			_sitemapManager.getNextRegenerateSitemapDate(
				TestPropsValues.getCompanyId()));
	}

	@Test
	public void testGetNextRegenerateSitemapDateWithPendingEntries()
		throws Exception {

		Assert.assertNull(
			_sitemapManager.getNextRegenerateSitemapDate(
				TestPropsValues.getCompanyId()));

		_siteSitemapRegenerationEntryLocalService.
			addSiteSitemapRegenerationEntry(
				RandomTestUtil.randomString(), TestPropsValues.getCompanyId(),
				RandomTestUtil.randomLong());

		Assert.assertNotNull(
			_sitemapManager.getNextRegenerateSitemapDate(
				TestPropsValues.getCompanyId()));
	}

	@Test
	public void testGetSitemapInputStreamWithPageLayoutSitemapIndexNotStored()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					_getPageLayoutCompanyConfigurationTemporarySwapper()) {

			Assert.assertNull(
				_sitemapManager.getSitemapInputStream(
					null, null, _group.getGroupId(), 1, false,
					_getThemeDisplay()));

			IdempotentRetryAssert.retryAssert(
				30, TimeUnit.SECONDS, 1, TimeUnit.SECONDS,
				() -> {
					Assert.assertTrue(
						_sitemapStorageHelper.hasSitemapFile(
							TestPropsValues.getCompanyId(),
							_group.getGroupId()));
					Assert.assertTrue(
						_sitemapStorageHelper.hasSitemapFile(
							TestPropsValues.getCompanyId(), _group.getGroupId(),
							_getPageLayoutChildSitemapKey(layout), 1));

					return null;
				});
		}
	}

	@Test
	public void testGetSitemapInputStreamWithPageLayoutSitemapNotStored()
		throws Exception {

		long groupId = _group.getGroupId();

		Layout layout1 = LayoutTestUtil.addTypePortletLayout(groupId);
		Layout layout2 = LayoutTestUtil.addTypePortletLayout(groupId);
		Layout layout3 = LayoutTestUtil.addTypePortletLayout(
			groupId,
			UnicodePropertiesBuilder.put(
				LayoutTypePortletConstants.SITEMAP_INCLUDE, "0"
			).buildString());

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					_getPageLayoutCompanyConfigurationTemporarySwapper()) {

			Assert.assertNull(
				_sitemapManager.getSitemapInputStream(
					null, RandomTestUtil.randomString(), groupId, 1, false,
					_getThemeDisplay()));
			Assert.assertNull(
				_sitemapManager.getSitemapInputStream(
					null, layout1.getUuid(), groupId, 2, false,
					_getThemeDisplay()));
			Assert.assertNull(
				_sitemapManager.getSitemapInputStream(
					null, layout3.getUuid(), groupId, 1, false,
					_getThemeDisplay()));

			List<SchedulerResponse> schedulerResponses =
				_getRegenerateSitemapSchedulerResponses(null);

			Assert.assertEquals(
				schedulerResponses.toString(), 0, schedulerResponses.size());

			Assert.assertNull(
				_sitemapManager.getSitemapInputStream(
					null, layout1.getUuid(), groupId, 1, false,
					_getThemeDisplay()));

			long companyId = TestPropsValues.getCompanyId();

			IdempotentRetryAssert.retryAssert(
				30, TimeUnit.SECONDS, 1, TimeUnit.SECONDS,
				() -> {
					Assert.assertTrue(
						_sitemapStorageHelper.hasSitemapFile(
							companyId, groupId,
							_getPageLayoutChildSitemapKey(layout1), 1));
					Assert.assertFalse(
						_sitemapManager.isRegenerateSitemapInProgress(
							companyId));

					return null;
				});

			Assert.assertFalse(
				_sitemapStorageHelper.hasSitemapFile(
					companyId, groupId, _getPageLayoutChildSitemapKey(layout2),
					1));
		}
	}

	@Test
	public void testGetSitemapInputStreamWithRegenerationScheduled()
		throws Exception {

		long companyId = TestPropsValues.getCompanyId();

		_sitemapManager.scheduleRegenerateSitemap(
			SitemapConstants.ASSET_TYPE_KEY_PAGES, companyId,
			_group.getGroupId(),
			new Date(System.currentTimeMillis() + Time.DAY));

		Date nextFireDate = _getNextFireDate(
			SitemapConstants.ASSET_TYPE_KEY_PAGES);

		ThemeDisplay themeDisplay = _getThemeDisplay();

		for (int i = 0; i < 2; i++) {
			Assert.assertNull(
				_sitemapManager.getSitemapInputStream(
					SitemapConstants.ASSET_TYPE_KEY_PAGES, null,
					_group.getGroupId(), 1, false, themeDisplay));
		}

		Assert.assertEquals(
			nextFireDate,
			_getNextFireDate(SitemapConstants.ASSET_TYPE_KEY_PAGES));
		Assert.assertFalse(
			_sitemapStorageHelper.hasSitemapFile(
				companyId, _group.getGroupId(),
				SitemapConstants.ASSET_TYPE_KEY_PAGES, 1));
	}

	@Test
	public void testGetSitemapInputStreamWithSitemapIndexNotStored()
		throws Exception {

		LayoutTestUtil.addTypePortletLayout(_group);

		Assert.assertNull(
			_sitemapManager.getSitemapInputStream(
				null, null, _group.getGroupId(), 1, false, _getThemeDisplay()));

		IdempotentRetryAssert.retryAssert(
			30, TimeUnit.SECONDS, 1, TimeUnit.SECONDS,
			() -> {
				Assert.assertTrue(
					_sitemapStorageHelper.hasSitemapFile(
						TestPropsValues.getCompanyId(), _group.getGroupId()));

				return null;
			});
	}

	@Test
	public void testGetSitemapInputStreamWithSitemapNotStored()
		throws Exception {

		LayoutTestUtil.addTypePortletLayout(_group);

		Assert.assertNull(
			_sitemapManager.getSitemapInputStream(
				SitemapConstants.ASSET_TYPE_KEY_PAGES, null,
				_group.getGroupId(), 1, false, _getThemeDisplay()));

		IdempotentRetryAssert.retryAssert(
			30, TimeUnit.SECONDS, 1, TimeUnit.SECONDS,
			() -> {
				Assert.assertTrue(
					_sitemapStorageHelper.hasSitemapFile(
						TestPropsValues.getCompanyId(), _group.getGroupId(),
						SitemapConstants.ASSET_TYPE_KEY_PAGES, 1));

				return null;
			});
	}

	@Test
	public void testIsRegenerateSitemapInProgress() throws Exception {
		long companyId = TestPropsValues.getCompanyId();

		Assert.assertFalse(
			_sitemapManager.isRegenerateSitemapInProgress(companyId));

		String key = RandomTestUtil.randomString();

		_lockManager.lock(
			TestPropsValues.getUserId(), _CLASS_NAME_SITEMAP_MANAGER_IMPL, key,
			_CLASS_NAME_SITEMAP_MANAGER_IMPL, false, Time.MINUTE, false);

		try {
			Assert.assertTrue(
				_sitemapManager.isRegenerateSitemapInProgress(companyId));
		}
		finally {
			_lockManager.unlock(_CLASS_NAME_SITEMAP_MANAGER_IMPL, key);
		}

		Assert.assertFalse(
			_sitemapManager.isRegenerateSitemapInProgress(companyId));
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
	public void testRegenerateSitemapWithPageLayoutIndexMode()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		long companyId = TestPropsValues.getCompanyId();
		long groupId = _group.getGroupId();

		_sitemapManager.regenerateSitemap(
			SitemapConstants.INDEX_MODE_PAGE_LAYOUT, companyId, groupId);

		String pageLayoutChildSitemapKey = _getPageLayoutChildSitemapKey(
			layout);

		Assert.assertFalse(
			_sitemapStorageHelper.hasSitemapFile(
				companyId, groupId, pageLayoutChildSitemapKey, 1));

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					_getPageLayoutCompanyConfigurationTemporarySwapper()) {

			_sitemapManager.regenerateSitemap(
				SitemapConstants.INDEX_MODE_PAGE_LAYOUT, companyId, groupId);

			Assert.assertTrue(
				_sitemapStorageHelper.hasSitemapFile(
					companyId, groupId, pageLayoutChildSitemapKey, 1));
			Assert.assertFalse(
				_sitemapManager.isRegenerateSitemapInProgress(companyId));
			Assert.assertNotNull(
				_sitemapStorageHelper.getLastRegenerateSitemapDate(companyId));
		}
	}

	@Test
	public void testScheduleRegenerateSitemapWithAddAssetCategory()
		throws Exception {

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId());

		AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());

		_assertSiteSitemapRegenerationEntry(
			SitemapConstants.ASSET_TYPE_KEY_CATEGORIES);
	}

	@Test
	public void testScheduleRegenerateSitemapWithAddDepotJournalArticlePageLayoutIndexMode()
		throws Exception {

		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		try {
			_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
				depotEntry.getDepotEntryId(), _group.getGroupId());

			try (CompanyConfigurationTemporarySwapper
					companyConfigurationTemporarySwapper =
						_getPageLayoutCompanyConfigurationTemporarySwapper()) {

				JournalTestUtil.addArticle(
					depotEntry.getGroupId(),
					JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

				_assertSiteSitemapRegenerationEntry(
					SitemapConstants.INDEX_MODE_PAGE_LAYOUT);

				List<SiteSitemapRegenerationEntry>
					siteSitemapRegenerationEntries =
						_getSiteSitemapRegenerationEntries(
							SitemapConstants.INDEX_MODE_PAGE_LAYOUT,
							depotEntry.getGroupId());

				Assert.assertTrue(siteSitemapRegenerationEntries.isEmpty());
			}
		}
		finally {
			_depotEntryLocalService.deleteDepotEntry(
				depotEntry.getDepotEntryId());
		}
	}

	@Test
	public void testScheduleRegenerateSitemapWithAddJournalArticle()
		throws Exception {

		JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		_assertSiteSitemapRegenerationEntry(
			SitemapConstants.ASSET_TYPE_KEY_WEB_CONTENT);
	}

	@Test
	public void testScheduleRegenerateSitemapWithAddLayout() throws Exception {
		LayoutTestUtil.addTypePortletLayout(_group);

		_assertSiteSitemapRegenerationEntry(
			SitemapConstants.ASSET_TYPE_KEY_PAGES);
	}

	@Test
	public void testScheduleRegenerateSitemapWithAddLayoutCachedGenerationDisabled()
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						_PID_SITEMAP_COMPANY_CONFIGURATION,
						MapUtil.<String, Object>singletonDictionary(
							"cachedGenerationEnabled", false))) {

			LayoutTestUtil.addTypePortletLayout(_group);

			List<SiteSitemapRegenerationEntry> siteSitemapRegenerationEntries =
				_getSiteSitemapRegenerationEntries(
					SitemapConstants.ASSET_TYPE_KEY_PAGES, _group.getGroupId());

			Assert.assertTrue(siteSitemapRegenerationEntries.isEmpty());
		}
	}

	@Test
	public void testScheduleRegenerateSitemapWithAddObjectEntry()
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					_getObjectEntryCompanyConfigurationTemporarySwapper(
						ObjectDefinitionConstants.SCOPE_COMPANY)) {

			_addObjectEntry(0);

			_assertSiteSitemapRegenerationEntry(
				SitemapConstants.ASSET_TYPE_KEY_OBJECT_ENTRIES, 0);
		}
	}

	@Test
	public void testScheduleRegenerateSitemapWithAddObjectEntryPageLayoutIndexMode()
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					_getPageLayoutCompanyConfigurationTemporarySwapper()) {

			_addObjectEntry(_group.getGroupId());

			List<SiteSitemapRegenerationEntry> siteSitemapRegenerationEntries =
				_siteSitemapRegenerationEntryLocalService.
					getSiteSitemapRegenerationEntries(
						TestPropsValues.getCompanyId());

			Assert.assertTrue(siteSitemapRegenerationEntries.isEmpty());

			ObjectDefinition objectDefinition = _publishObjectDefinition(
				ObjectDefinitionConstants.SCOPE_SITE);

			LayoutPageTemplateEntry layoutPageTemplateEntry =
				DisplayPageTemplateTestUtil.addDisplayPageTemplate(
					_group.getGroupId(),
					_portal.getClassNameId(objectDefinition.getClassName()),
					null, true, WorkflowConstants.STATUS_APPROVED);

			_deleteSiteSitemapRegenerationEntries();

			_addObjectEntry(_group.getGroupId());

			_assertSiteSitemapRegenerationEntry(
				_getPageLayoutChildSitemapKey(
					_layoutLocalService.getLayout(
						layoutPageTemplateEntry.getPlid())));
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
	public void testScheduleRegenerateSitemapWithCachedGenerationDisabled()
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						_PID_SITEMAP_COMPANY_CONFIGURATION,
						HashMapDictionaryBuilder.<String, Object>put(
							"cachedGenerationEnabled", false
						).put(
							"xmlSitemapIndexEnabled", true
						).put(
							"xmlSitemapIndexMode",
							SitemapConstants.INDEX_MODE_ASSET_TYPE
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
	public void testScheduleRegenerateSitemapWithDeleteAssetCategory()
		throws Exception {

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId());

		AssetCategory assetCategory = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());

		_deleteSiteSitemapRegenerationEntries();

		_assetCategoryLocalService.deleteCategory(assetCategory);

		_assertSiteSitemapRegenerationEntry(
			SitemapConstants.ASSET_TYPE_KEY_CATEGORIES);
	}

	@Test
	public void testScheduleRegenerateSitemapWithDeleteJournalArticle()
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		_deleteSiteSitemapRegenerationEntries();

		_journalArticleLocalService.deleteArticle(journalArticle);

		_assertSiteSitemapRegenerationEntry(
			SitemapConstants.ASSET_TYPE_KEY_WEB_CONTENT);
	}

	@Test
	public void testScheduleRegenerateSitemapWithDeleteLayout()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		_deleteSiteSitemapRegenerationEntries();

		_layoutLocalService.deleteLayout(layout);

		_assertSiteSitemapRegenerationEntry(
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

			_deleteSiteSitemapRegenerationEntries();

			_objectEntryLocalService.deleteObjectEntry(objectEntry);

			_assertSiteSitemapRegenerationEntry(
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

		_deleteSiteSitemapRegenerationEntries();

		_assetCategoryLocalService.updateAssetCategory(assetCategory);

		_assertSiteSitemapRegenerationEntry(
			SitemapConstants.ASSET_TYPE_KEY_CATEGORIES);
	}

	@Test
	public void testScheduleRegenerateSitemapWithUpdateJournalArticle()
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		_deleteSiteSitemapRegenerationEntries();

		JournalTestUtil.updateArticle(journalArticle);

		_assertSiteSitemapRegenerationEntry(
			SitemapConstants.ASSET_TYPE_KEY_WEB_CONTENT);
	}

	@Test
	public void testScheduleRegenerateSitemapWithUpdateJournalArticlePageLayoutIndexMode()
		throws Exception {

		Layout layout1 = LayoutTestUtil.addTypePortletLayout(_group);
		Layout layout2 = LayoutTestUtil.addTypePortletLayout(_group);

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					_getPageLayoutCompanyConfigurationTemporarySwapper()) {

			journalArticle = _updateJournalArticle(
				journalArticle, layout1.getUuid());

			_assertSiteSitemapRegenerationEntry(
				_getPageLayoutChildSitemapKey(layout1));

			_deleteSiteSitemapRegenerationEntries();

			journalArticle = _updateJournalArticle(
				journalArticle, layout2.getUuid());

			_assertSiteSitemapRegenerationEntry(
				_getPageLayoutChildSitemapKey(layout1));
			_assertSiteSitemapRegenerationEntry(
				_getPageLayoutChildSitemapKey(layout2));

			long classNameId = _portal.getClassNameId(
				JournalArticle.class.getName());

			LayoutPageTemplateEntry layoutPageTemplateEntry1 =
				DisplayPageTemplateTestUtil.addDisplayPageTemplate(
					_group.getGroupId(), classNameId,
					journalArticle.getDDMStructureKey(), false,
					WorkflowConstants.STATUS_APPROVED);

			_assetDisplayPageEntryLocalService.addAssetDisplayPageEntry(
				TestPropsValues.getUserId(), _group.getGroupId(), classNameId,
				journalArticle.getResourcePrimKey(),
				layoutPageTemplateEntry1.getLayoutPageTemplateEntryId(),
				AssetDisplayPageConstants.TYPE_SPECIFIC,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

			_deleteSiteSitemapRegenerationEntries();

			journalArticle = _updateJournalArticle(
				journalArticle, layout2.getUuid());

			_assertSiteSitemapRegenerationEntry(
				_getPageLayoutChildSitemapKey(
					_layoutLocalService.getLayout(
						layoutPageTemplateEntry1.getPlid())));

			LayoutPageTemplateEntry layoutPageTemplateEntry2 =
				DisplayPageTemplateTestUtil.addDisplayPageTemplate(
					_group.getGroupId(), classNameId,
					journalArticle.getDDMStructureKey(), true,
					WorkflowConstants.STATUS_APPROVED);

			_deleteSiteSitemapRegenerationEntries();

			_updateJournalArticle(journalArticle, layout2.getUuid());

			_assertSiteSitemapRegenerationEntry(
				_getPageLayoutChildSitemapKey(
					_layoutLocalService.getLayout(
						layoutPageTemplateEntry2.getPlid())));
		}
	}

	@Test
	public void testScheduleRegenerateSitemapWithUpdateLayout()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		_deleteSiteSitemapRegenerationEntries();

		_layoutLocalService.updateLayout(layout);

		_assertSiteSitemapRegenerationEntry(
			SitemapConstants.ASSET_TYPE_KEY_PAGES);
	}

	@Test
	public void testScheduleRegenerateSitemapWithUpdateLayoutPageLayoutIndexMode()
		throws Exception {

		Layout parentLayout = LayoutTestUtil.addTypePortletLayout(_group);

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					_getPageLayoutCompanyConfigurationTemporarySwapper()) {

			Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

			_assertSiteSitemapRegenerationEntry(
				_getPageLayoutChildSitemapKey(layout));

			List<SiteSitemapRegenerationEntry> siteSitemapRegenerationEntries =
				_getSiteSitemapRegenerationEntries(
					SitemapConstants.INDEX_MODE_PAGE_LAYOUT,
					_group.getGroupId());

			Assert.assertTrue(siteSitemapRegenerationEntries.isEmpty());

			_layoutLocalService.updateParentLayoutId(
				layout.getPlid(), parentLayout.getPlid());

			_assertSiteSitemapRegenerationEntry(
				SitemapConstants.INDEX_MODE_PAGE_LAYOUT);

			_deleteSiteSitemapRegenerationEntries();

			UnicodeProperties typeSettingsUnicodeProperties =
				parentLayout.getTypeSettingsProperties();

			typeSettingsUnicodeProperties.setProperty(
				"sitemap-include-child-layouts", Boolean.FALSE.toString());

			_layoutLocalService.updateTypeSettings(
				parentLayout, typeSettingsUnicodeProperties.toString());

			_assertSiteSitemapRegenerationEntry(
				SitemapConstants.INDEX_MODE_PAGE_LAYOUT);
		}
	}

	@Test
	public void testScheduleRegenerateSitemapWithUpdateObjectEntry()
		throws Exception {

		ObjectEntry objectEntry = _addObjectEntry(_group.getGroupId());

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					_getObjectEntryCompanyConfigurationTemporarySwapper(
						ObjectDefinitionConstants.SCOPE_SITE)) {

			_deleteSiteSitemapRegenerationEntries();

			_objectEntryLocalService.updateObjectEntry(
				TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				Collections.emptyMap(),
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

			_assertSiteSitemapRegenerationEntry(
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

	private void _assertSiteSitemapRegenerationEntry(String childSitemapKey)
		throws Exception {

		_assertSiteSitemapRegenerationEntry(
			childSitemapKey, _group.getGroupId());
	}

	private void _assertSiteSitemapRegenerationEntry(
			String childSitemapKey, long groupId)
		throws Exception {

		List<SiteSitemapRegenerationEntry> siteSitemapRegenerationEntries =
			_getSiteSitemapRegenerationEntries(childSitemapKey, groupId);

		Assert.assertEquals(
			siteSitemapRegenerationEntries.toString(), 1,
			siteSitemapRegenerationEntries.size());
	}

	private void _deleteRegenerateSitemapScheduledJobs() throws Exception {
		_sitemapManager.deleteRegenerateSitemapScheduledJobs(
			TestPropsValues.getCompanyId());
	}

	private void _deleteSiteSitemapRegenerationEntries() throws Exception {
		_siteSitemapRegenerationEntryLocalService.
			deleteSiteSitemapRegenerationEntries(
				TestPropsValues.getCompanyId());
	}

	private Date _getNextFireDate(String assetTypeKey) throws Exception {
		List<SchedulerResponse> schedulerResponses =
			_getRegenerateSitemapSchedulerResponses(assetTypeKey);

		Assert.assertEquals(
			schedulerResponses.toString(), 1, schedulerResponses.size());

		return _schedulerEngineHelper.getNextFireDate(
			schedulerResponses.get(0));
	}

	private CompanyConfigurationTemporarySwapper
			_getObjectEntryCompanyConfigurationTemporarySwapper(String scope)
		throws Exception {

		ObjectDefinition objectDefinition = _publishObjectDefinition(scope);

		return new CompanyConfigurationTemporarySwapper(
			TestPropsValues.getCompanyId(), _PID_SITEMAP_COMPANY_CONFIGURATION,
			HashMapDictionaryBuilder.<String, Object>put(
				"cachedGenerationEnabled", true
			).put(
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

	private String _getPageLayoutChildSitemapKey(Layout layout) {
		return SitemapConstants.INDEX_MODE_PAGE_LAYOUT + StringPool.SLASH +
			layout.getUuid();
	}

	private CompanyConfigurationTemporarySwapper
			_getPageLayoutCompanyConfigurationTemporarySwapper()
		throws Exception {

		return new CompanyConfigurationTemporarySwapper(
			TestPropsValues.getCompanyId(), _PID_SITEMAP_COMPANY_CONFIGURATION,
			HashMapDictionaryBuilder.<String, Object>put(
				"cachedGenerationEnabled", true
			).put(
				"xmlSitemapIndexEnabled", true
			).put(
				"xmlSitemapIndexMode", SitemapConstants.INDEX_MODE_PAGE_LAYOUT
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
			String childSitemapKey)
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

				if ((childSitemapKey == null) ||
					Objects.equals(
						message.getString("childSitemapKey"),
						childSitemapKey)) {

					return schedulerResponse;
				}

				return null;
			});
	}

	private List<SiteSitemapRegenerationEntry>
			_getSiteSitemapRegenerationEntries(
				String childSitemapKey, long groupId)
		throws Exception {

		return TransformUtil.transform(
			_siteSitemapRegenerationEntryLocalService.
				getSiteSitemapRegenerationEntries(
					TestPropsValues.getCompanyId()),
			siteSitemapRegenerationEntry -> {
				if ((siteSitemapRegenerationEntry.getGroupId() != groupId) ||
					!Objects.equals(
						siteSitemapRegenerationEntry.getAssetTypeKey(),
						childSitemapKey)) {

					return null;
				}

				return siteSitemapRegenerationEntry;
			});
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));

		return themeDisplay;
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

	private JournalArticle _updateJournalArticle(
			JournalArticle journalArticle, String layoutUuid)
		throws Exception {

		return _journalArticleLocalService.updateArticle(
			journalArticle.getUserId(), journalArticle.getGroupId(),
			journalArticle.getFolderId(), journalArticle.getArticleId(),
			journalArticle.getVersion(), journalArticle.getTitleMap(),
			journalArticle.getDescriptionMap(), journalArticle.getContent(),
			layoutUuid,
			ServiceContextTestUtil.getServiceContext(
				journalArticle.getGroupId()));
	}

	private static final String _CLASS_NAME_SITEMAP_MANAGER_IMPL =
		"com.liferay.site.internal.manager.SitemapManagerImpl";

	private static final String _PID_SITEMAP_COMPANY_CONFIGURATION =
		"com.liferay.site.internal.configuration.SitemapCompanyConfiguration";

	private static CompanyConfigurationTemporarySwapper
		_companyConfigurationTemporarySwapper;

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetDisplayPageEntryLocalService
		_assetDisplayPageEntryLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private ObjectDefinition _companyObjectDefinition;

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LockManager _lockManager;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private SchedulerEngineHelper _schedulerEngineHelper;

	@Inject(
		filter = "component.name=com.liferay.site.internal.scheduler.XMLSitemapRegenerationSchedulerJobConfiguration"
	)
	private SchedulerJobConfiguration _schedulerJobConfiguration;

	@DeleteAfterTestRun
	private ObjectDefinition _siteObjectDefinition;

	@Inject
	private SiteSitemapRegenerationEntryLocalService
		_siteSitemapRegenerationEntryLocalService;

	@Inject
	private SitemapManager _sitemapManager;

	@Inject
	private SitemapStorageHelper _sitemapStorageHelper;

}