/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.asset.publisher.test.util.AssetPublisherTestUtil;
import com.liferay.asset.publisher.util.AssetEntryResult;
import com.liferay.asset.publisher.util.AssetPublisherHelper;
import com.liferay.asset.publisher.util.AssetQueryRule;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.info.pagination.InfoPage;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.layout.page.template.test.util.LayoutPageTemplateTestUtil;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutPrototypeLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockPortletPreferences;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.view.count.ViewCountManager;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.ratings.test.util.RatingsTestUtil;
import com.liferay.segments.criteria.contributor.SegmentsCriteriaContributor;

import jakarta.portlet.PortletPreferences;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class AssetPublisherHelperTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_assetPublisherWebConfiguration = _configurationAdmin.getConfiguration(
			"com.liferay.asset.publisher.web.internal.configuration." +
				"AssetPublisherWebConfiguration",
			StringPool.QUESTION);

		ConfigurationTestUtil.saveConfiguration(
			_assetPublisherWebConfiguration,
			HashMapDictionaryBuilder.<String, Object>put(
				"searchWithIndex", false
			).build());
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		ConfigurationTestUtil.deleteConfiguration(
			_assetPublisherWebConfiguration);
	}

	@Before
	public void setUp() throws Exception {
		_group1 = GroupTestUtil.addGroup();
		_group2 = GroupTestUtil.addGroup();
	}

	@Test
	public void testGetAssetCategoryIdsContainsAllCategories()
		throws Exception {

		long assetCategoryId1 = RandomTestUtil.nextLong();
		long assetCategoryId2 = RandomTestUtil.nextLong();

		AssetQueryRule assetQueryRule = new AssetQueryRule(
			true, true, "assetCategories",
			new String[] {
				String.valueOf(assetCategoryId1),
				String.valueOf(assetCategoryId2)
			});

		PortletPreferences portletPreferences =
			getAssetPublisherPortletPreferences(
				ListUtil.fromArray(assetQueryRule));

		long[] assetCategoryIds = _assetPublisherHelper.getAssetCategoryIds(
			portletPreferences);

		Assert.assertEquals(
			Arrays.toString(assetCategoryIds), 2, assetCategoryIds.length);
		Assert.assertEquals(assetCategoryId1, assetCategoryIds[0]);
		Assert.assertEquals(assetCategoryId2, assetCategoryIds[1]);
	}

	@Test
	public void testGetAssetCategoryIdsContainsAllCategory() throws Exception {
		long assetCategoryId = RandomTestUtil.nextLong();

		AssetQueryRule assetQueryRule = new AssetQueryRule(
			true, true, "assetCategories",
			new String[] {String.valueOf(assetCategoryId)});

		PortletPreferences portletPreferences =
			getAssetPublisherPortletPreferences(
				ListUtil.fromArray(assetQueryRule));

		long[] assetCategoryIds = _assetPublisherHelper.getAssetCategoryIds(
			portletPreferences);

		Assert.assertEquals(
			Arrays.toString(assetCategoryIds), 1, assetCategoryIds.length);
		Assert.assertEquals(assetCategoryId, assetCategoryIds[0]);
	}

	@Test
	public void testGetAssetCategoryIdsContainsAnyCategories()
		throws Exception {

		long assetCategoryId1 = RandomTestUtil.nextLong();
		long assetCategoryId2 = RandomTestUtil.nextLong();

		AssetQueryRule assetQueryRule = new AssetQueryRule(
			true, false, "assetCategories",
			new String[] {
				String.valueOf(assetCategoryId1),
				String.valueOf(assetCategoryId2)
			});

		PortletPreferences portletPreferences =
			getAssetPublisherPortletPreferences(
				ListUtil.fromArray(assetQueryRule));

		long[] assetCategoryIds = _assetPublisherHelper.getAssetCategoryIds(
			portletPreferences);

		Assert.assertEquals(
			Arrays.toString(assetCategoryIds), 0, assetCategoryIds.length);
	}

	@Test
	public void testGetAssetCategoryIdsContainsAnyCategory() throws Exception {
		long assetCategoryId = RandomTestUtil.nextLong();

		AssetQueryRule assetQueryRule = new AssetQueryRule(
			true, false, "assetCategories",
			new String[] {String.valueOf(assetCategoryId)});

		PortletPreferences portletPreferences =
			getAssetPublisherPortletPreferences(
				ListUtil.fromArray(assetQueryRule));

		long[] assetCategoryIds = _assetPublisherHelper.getAssetCategoryIds(
			portletPreferences);

		Assert.assertEquals(
			Arrays.toString(assetCategoryIds), 1, assetCategoryIds.length);
		Assert.assertEquals(assetCategoryId, assetCategoryIds[0]);
	}

	@Test
	public void testGetAssetCategoryIdsNotContainsAllCategories()
		throws Exception {

		long assetCategoryId1 = RandomTestUtil.nextLong();
		long assetCategoryId2 = RandomTestUtil.nextLong();

		AssetQueryRule assetQueryRule = new AssetQueryRule(
			false, true, "assetCategories",
			new String[] {
				String.valueOf(assetCategoryId1),
				String.valueOf(assetCategoryId2)
			});

		PortletPreferences portletPreferences =
			getAssetPublisherPortletPreferences(
				ListUtil.fromArray(assetQueryRule));

		long[] assetCategoryIds = _assetPublisherHelper.getAssetCategoryIds(
			portletPreferences);

		Assert.assertEquals(
			Arrays.toString(assetCategoryIds), 0, assetCategoryIds.length);
	}

	@Test
	public void testGetAssetCategoryIdsNotContainsAllCategory()
		throws Exception {

		long assetCategoryId = RandomTestUtil.nextLong();

		AssetQueryRule assetQueryRule = new AssetQueryRule(
			false, true, "assetCategories",
			new String[] {String.valueOf(assetCategoryId)});

		PortletPreferences portletPreferences =
			getAssetPublisherPortletPreferences(
				ListUtil.fromArray(assetQueryRule));

		long[] assetCategoryIds = _assetPublisherHelper.getAssetCategoryIds(
			portletPreferences);

		Assert.assertEquals(
			Arrays.toString(assetCategoryIds), 0, assetCategoryIds.length);
	}

	@Test
	public void testGetAssetCategoryIdsNotContainsAnyCategories()
		throws Exception {

		long assetCategoryId1 = RandomTestUtil.nextLong();
		long assetCategoryId2 = RandomTestUtil.nextLong();

		AssetQueryRule assetQueryRule = new AssetQueryRule(
			false, false, "assetCategories",
			new String[] {
				String.valueOf(assetCategoryId1),
				String.valueOf(assetCategoryId2)
			});

		PortletPreferences portletPreferences =
			getAssetPublisherPortletPreferences(
				ListUtil.fromArray(assetQueryRule));

		long[] assetCategoryIds = _assetPublisherHelper.getAssetCategoryIds(
			portletPreferences);

		Assert.assertEquals(
			Arrays.toString(assetCategoryIds), 0, assetCategoryIds.length);
	}

	@Test
	public void testGetAssetCategoryIdsNotContainsAnyCategory()
		throws Exception {

		long assetCategoryId = RandomTestUtil.nextLong();

		AssetQueryRule assetQueryRule = new AssetQueryRule(
			false, false, "assetCategories",
			new String[] {String.valueOf(assetCategoryId)});

		PortletPreferences portletPreferences =
			getAssetPublisherPortletPreferences(
				ListUtil.fromArray(assetQueryRule));

		long[] assetCategoryIds = _assetPublisherHelper.getAssetCategoryIds(
			portletPreferences);

		Assert.assertEquals(
			Arrays.toString(assetCategoryIds), 0, assetCategoryIds.length);
	}

	@Test
	public void testGetAssetEntriesForManualCollectionWithPagination()
		throws Exception {

		JournalArticle journalArticle1 = JournalTestUtil.addArticle(
			_group1.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);
		JournalArticle journalArticle2 = JournalTestUtil.addArticle(
			_group1.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);
		JournalArticle journalArticle3 = JournalTestUtil.addArticle(
			_group1.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			ContentLayoutTestUtil.getMockLiferayPortletActionRequest(
				_companyLocalService.getCompany(TestPropsValues.getCompanyId()),
				_group1,
				LayoutTestUtil.addTypePortletLayout(_group1.getGroupId()));

		PortletPreferences portletPreferences = new MockPortletPreferences();

		portletPreferences.setValue("selectionStyle", "manual");

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				JournalArticle.class.getName());

		AssetEntry assetEntry1 = assetRendererFactory.getAssetEntry(
			JournalArticle.class.getName(),
			journalArticle1.getResourcePrimKey());
		AssetEntry assetEntry2 = assetRendererFactory.getAssetEntry(
			JournalArticle.class.getName(),
			journalArticle2.getResourcePrimKey());
		AssetEntry assetEntry3 = assetRendererFactory.getAssetEntry(
			JournalArticle.class.getName(),
			journalArticle3.getResourcePrimKey());

		portletPreferences.setValues(
			"assetEntryXml",
			AssetPublisherTestUtil.getAssetEntryXml(assetEntry1),
			AssetPublisherTestUtil.getAssetEntryXml(assetEntry2));

		InfoPage<AssetEntry> infoPage = _assetPublisherHelper.getInfoPage(
			mockLiferayPortletActionRequest, portletPreferences,
			PermissionThreadLocal.getPermissionChecker(),
			new long[] {_group1.getGroupId()}, null, null, false, false, 0, 2);

		Assert.assertEquals(2, infoPage.getTotalCount());

		List<AssetEntry> assetEntries =
			(List<AssetEntry>)infoPage.getPageItems();

		Assert.assertTrue(assetEntries.contains(assetEntry1));
		Assert.assertTrue(assetEntries.contains(assetEntry2));
		Assert.assertFalse(assetEntries.contains(assetEntry3));

		portletPreferences.setValues(
			"assetEntryXml",
			AssetPublisherTestUtil.getAssetEntryXml(assetEntry1),
			AssetPublisherTestUtil.getAssetEntryXml(assetEntry2),
			AssetPublisherTestUtil.getAssetEntryXml(assetEntry3));

		infoPage = _assetPublisherHelper.getInfoPage(
			mockLiferayPortletActionRequest, portletPreferences,
			PermissionThreadLocal.getPermissionChecker(),
			new long[] {_group1.getGroupId()}, null, null, false, false,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(3, infoPage.getTotalCount());

		assetEntries = (List<AssetEntry>)infoPage.getPageItems();

		Assert.assertTrue(assetEntries.contains(assetEntry1));
		Assert.assertTrue(assetEntries.contains(assetEntry2));
		Assert.assertTrue(assetEntries.contains(assetEntry3));
	}

	@Test
	public void testGetAssetTagNamesContainsAllTagName() throws Exception {
		String assetTagName = RandomTestUtil.randomString();

		AssetQueryRule assetQueryRule = new AssetQueryRule(
			true, true, "assetTags", new String[] {assetTagName});

		PortletPreferences portletPreferences =
			getAssetPublisherPortletPreferences(
				ListUtil.fromArray(assetQueryRule));

		String[] assetTagNames = _assetPublisherHelper.getAssetTagNames(
			portletPreferences);

		Assert.assertEquals(
			Arrays.toString(assetTagNames), 1, assetTagNames.length);
		Assert.assertEquals(assetTagName, assetTagNames[0]);
	}

	@Test
	public void testGetAssetTagNamesContainsAllTagNames() throws Exception {
		String assetTagName1 = RandomTestUtil.randomString();
		String assetTagName2 = RandomTestUtil.randomString();

		AssetQueryRule assetQueryRule = new AssetQueryRule(
			true, true, "assetTags",
			new String[] {assetTagName1, assetTagName2});

		PortletPreferences portletPreferences =
			getAssetPublisherPortletPreferences(
				ListUtil.fromArray(assetQueryRule));

		String[] assetTagNames = _assetPublisherHelper.getAssetTagNames(
			portletPreferences);

		Assert.assertEquals(
			Arrays.toString(assetTagNames), 2, assetTagNames.length);
		Assert.assertEquals(assetTagName1, assetTagNames[0]);
		Assert.assertEquals(assetTagName2, assetTagNames[1]);
	}

	@Test
	public void testGetAssetTagNamesContainsAnyTagName() throws Exception {
		String assetTagName = RandomTestUtil.randomString();

		AssetQueryRule assetQueryRule = new AssetQueryRule(
			true, false, "assetTags", new String[] {assetTagName});

		PortletPreferences portletPreferences =
			getAssetPublisherPortletPreferences(
				ListUtil.fromArray(assetQueryRule));

		String[] assetTagNames = _assetPublisherHelper.getAssetTagNames(
			portletPreferences);

		Assert.assertEquals(
			Arrays.toString(assetTagNames), 1, assetTagNames.length);
		Assert.assertEquals(assetTagName, assetTagNames[0]);
	}

	@Test
	public void testGetAssetTagNamesContainsAnyTagNames() throws Exception {
		String assetTagName1 = RandomTestUtil.randomString();
		String assetTagName2 = RandomTestUtil.randomString();

		AssetQueryRule assetQueryRule = new AssetQueryRule(
			true, false, "assetTags",
			new String[] {assetTagName1, assetTagName2});

		PortletPreferences portletPreferences =
			getAssetPublisherPortletPreferences(
				ListUtil.fromArray(assetQueryRule));

		String[] assetTagNames = _assetPublisherHelper.getAssetTagNames(
			portletPreferences);

		Assert.assertEquals(
			Arrays.toString(assetTagNames), 0, assetTagNames.length);
	}

	@Test
	public void testGetAssetTagNamesNotContainsAllTagName() throws Exception {
		String assetTagName = RandomTestUtil.randomString();

		AssetQueryRule assetQueryRule = new AssetQueryRule(
			false, true, "assetTags", new String[] {assetTagName});

		PortletPreferences portletPreferences =
			getAssetPublisherPortletPreferences(
				ListUtil.fromArray(assetQueryRule));

		String[] assetTagNames = _assetPublisherHelper.getAssetTagNames(
			portletPreferences);

		Assert.assertEquals(
			Arrays.toString(assetTagNames), 0, assetTagNames.length);
	}

	@Test
	public void testGetAssetTagNamesNotContainsAllTagNames() throws Exception {
		String assetTagName1 = RandomTestUtil.randomString();
		String assetTagName2 = RandomTestUtil.randomString();

		AssetQueryRule assetQueryRule = new AssetQueryRule(
			false, true, "assetTags",
			new String[] {assetTagName1, assetTagName2});

		PortletPreferences portletPreferences =
			getAssetPublisherPortletPreferences(
				ListUtil.fromArray(assetQueryRule));

		String[] assetTagNames = _assetPublisherHelper.getAssetTagNames(
			portletPreferences);

		Assert.assertEquals(
			Arrays.toString(assetTagNames), 0, assetTagNames.length);
	}

	@Test
	public void testGetAssetTagNamesNotContainsAnyTagName() throws Exception {
		String assetTagName = RandomTestUtil.randomString();

		AssetQueryRule assetQueryRule = new AssetQueryRule(
			false, false, "assetTags", new String[] {assetTagName});

		PortletPreferences portletPreferences =
			getAssetPublisherPortletPreferences(
				ListUtil.fromArray(assetQueryRule));

		String[] assetTagNames = _assetPublisherHelper.getAssetTagNames(
			portletPreferences);

		Assert.assertEquals(
			Arrays.toString(assetTagNames), 0, assetTagNames.length);
	}

	@Test
	public void testGetAssetTagNamesNotContainsAnyTagNames() throws Exception {
		String assetTagName1 = RandomTestUtil.randomString();
		String assetTagName2 = RandomTestUtil.randomString();

		AssetQueryRule assetQueryRule = new AssetQueryRule(
			false, false, "assetTags",
			new String[] {assetTagName1, assetTagName2});

		PortletPreferences portletPreferences =
			getAssetPublisherPortletPreferences(
				ListUtil.fromArray(assetQueryRule));

		String[] assetTagNames = _assetPublisherHelper.getAssetTagNames(
			portletPreferences);

		Assert.assertEquals(
			Arrays.toString(assetTagNames), 0, assetTagNames.length);
	}

	@Test
	public void testGetItemSelectorScopeGroupWithLayoutPrototype()
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				_group1.getGroupId(),
				LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE,
				WorkflowConstants.STATUS_APPROVED);

		LayoutPrototype layoutPrototype =
			_layoutPrototypeLocalService.getLayoutPrototype(
				layoutPageTemplateEntry.getLayoutPrototypeId());

		Assert.assertEquals(
			_group1, _assetPublisherHelper.getItemSelectorScopeGroup(_group1));
		Assert.assertEquals(
			_group1,
			_assetPublisherHelper.getItemSelectorScopeGroup(
				layoutPrototype.getGroup()));
	}

	@Test
	public void testHighestRatedAsset() throws Exception {
		JournalArticle journalArticle1 = JournalTestUtil.addArticle(
			_group1.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		JournalArticle journalArticle2 = JournalTestUtil.addArticle(
			_group1.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		Layout layout = LayoutTestUtil.addTypePortletLayout(
			_group1.getGroupId());

		PortletPreferences portletPreferences = new MockPortletPreferences();

		portletPreferences.setValue("orderByColumn1", "ratings");
		portletPreferences.setValue("orderByType1", "DESC");

		AssetEntryQuery assetEntryQuery =
			_assetPublisherHelper.getAssetEntryQuery(
				portletPreferences, _group1.getGroupId(), layout, new long[0],
				new String[0], new String[0]);

		assetEntryQuery.setClassNameIds(
			new long[] {
				_classNameLocalService.getClassNameId(
					JournalArticle.class.getName())
			});

		SearchContainer<AssetEntry> searchContainer = new SearchContainer<>();

		searchContainer.setResultsAndTotal(Collections::emptyList, 10);

		AssetEntry assetEntry2 = _assetEntryLocalService.fetchEntry(
			JournalArticle.class.getName(),
			journalArticle2.getResourcePrimKey());

		RatingsTestUtil.addStats(
			assetEntry2.getClassName(), assetEntry2.getClassPK(), 2000);

		_checkAssetEntryResults(
			_assetPublisherHelper.getAssetEntryResults(
				searchContainer, assetEntryQuery, layout, portletPreferences,
				StringPool.BLANK, null, null, TestPropsValues.getCompanyId(),
				_group1.getGroupId(), TestPropsValues.getUserId(),
				assetEntryQuery.getClassNameIds(), null),
			new long[] {
				journalArticle2.getResourcePrimKey(),
				journalArticle1.getResourcePrimKey()
			});

		AssetEntry assetEntry1 = _assetEntryLocalService.fetchEntry(
			JournalArticle.class.getName(),
			journalArticle1.getResourcePrimKey());

		RatingsTestUtil.addStats(
			assetEntry1.getClassName(), assetEntry1.getClassPK(), 4000);

		_checkAssetEntryResults(
			_assetPublisherHelper.getAssetEntryResults(
				searchContainer, assetEntryQuery, layout, portletPreferences,
				StringPool.BLANK, null, null, TestPropsValues.getCompanyId(),
				_group1.getGroupId(), TestPropsValues.getUserId(),
				assetEntryQuery.getClassNameIds(), null),
			new long[] {
				journalArticle1.getResourcePrimKey(),
				journalArticle2.getResourcePrimKey()
			});
	}

	@Test
	public void testMostViewedAsset() throws Exception {
		JournalArticle journalArticle1 = JournalTestUtil.addArticle(
			_group1.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		JournalArticle journalArticle2 = JournalTestUtil.addArticle(
			_group1.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		Layout layout = LayoutTestUtil.addTypePortletLayout(
			_group1.getGroupId());

		PortletPreferences portletPreferences = new MockPortletPreferences();

		portletPreferences.setValue("orderByColumn1", "viewCount");
		portletPreferences.setValue("orderByType1", "DESC");

		AssetEntryQuery assetEntryQuery =
			_assetPublisherHelper.getAssetEntryQuery(
				portletPreferences, _group1.getGroupId(), layout, new long[0],
				new String[0], new String[0]);

		assetEntryQuery.setClassNameIds(
			new long[] {
				_classNameLocalService.getClassNameId(
					JournalArticle.class.getName())
			});

		SearchContainer<AssetEntry> searchContainer = new SearchContainer<>();

		searchContainer.setResultsAndTotal(Collections::emptyList, 10);

		AssetEntry assetEntry2 = _assetEntryLocalService.fetchEntry(
			JournalArticle.class.getName(),
			journalArticle2.getResourcePrimKey());

		_viewCountManager.incrementViewCount(
			TestPropsValues.getCompanyId(),
			_portal.getClassNameId(AssetEntry.class), assetEntry2.getEntryId(),
			2);

		_checkAssetEntryResults(
			_assetPublisherHelper.getAssetEntryResults(
				searchContainer, assetEntryQuery, layout, portletPreferences,
				StringPool.BLANK, null, null, TestPropsValues.getCompanyId(),
				_group1.getGroupId(), TestPropsValues.getUserId(),
				assetEntryQuery.getClassNameIds(), null),
			new long[] {
				journalArticle2.getResourcePrimKey(),
				journalArticle1.getResourcePrimKey()
			});

		AssetEntry assetEntry1 = _assetEntryLocalService.fetchEntry(
			JournalArticle.class.getName(),
			journalArticle1.getResourcePrimKey());

		_viewCountManager.incrementViewCount(
			TestPropsValues.getCompanyId(),
			_portal.getClassNameId(AssetEntry.class), assetEntry1.getEntryId(),
			3);

		_checkAssetEntryResults(
			_assetPublisherHelper.getAssetEntryResults(
				searchContainer, assetEntryQuery, layout, portletPreferences,
				StringPool.BLANK, null, null, TestPropsValues.getCompanyId(),
				_group1.getGroupId(), TestPropsValues.getUserId(),
				assetEntryQuery.getClassNameIds(), null),
			new long[] {
				journalArticle1.getResourcePrimKey(),
				journalArticle2.getResourcePrimKey()
			});
	}

	@Test
	public void testNotGetAssetWithTagsFromDifferentSite() throws Exception {
		String assetTagName1 = RandomTestUtil.randomString();

		AssetTestUtil.addTag(_group1.getGroupId(), assetTagName1);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group1.getGroupId());

		serviceContext.setAssetTagNames(new String[] {assetTagName1});

		JournalTestUtil.addArticle(
			_group1.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, serviceContext);

		Layout layout = LayoutTestUtil.addTypePortletLayout(
			_group2.getGroupId());

		String assetTagName2 = RandomTestUtil.randomString();

		AssetTestUtil.addTag(_group2.getGroupId(), assetTagName2);

		AssetQueryRule assetQueryRule = new AssetQueryRule(
			true, true, "assetTags", new String[] {assetTagName2});

		PortletPreferences portletPreferences =
			getAssetPublisherPortletPreferences(
				ListUtil.fromArray(assetQueryRule));

		long[] overrideAllAssetCategoryIds = new long[0];
		String[] overrideAllAssetTagNames = {assetTagName2};
		String[] overrideAllKeywords = new String[0];

		AssetEntryQuery assetEntryQuery =
			_assetPublisherHelper.getAssetEntryQuery(
				portletPreferences, _group2.getGroupId(), layout,
				overrideAllAssetCategoryIds, overrideAllAssetTagNames,
				overrideAllKeywords);

		assetEntryQuery.setClassNameIds(
			new long[] {
				_classNameLocalService.getClassNameId(
					JournalArticle.class.getName())
			});

		long[] tagIds = assetEntryQuery.getAllTagIds();

		Assert.assertTrue(tagIds.length > 0);

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		SearchContainer<AssetEntry> searchContainer = new SearchContainer<>();

		searchContainer.setResultsAndTotal(Collections::emptyList, 10);

		List<AssetEntryResult> assetEntryResults =
			_assetPublisherHelper.getAssetEntryResults(
				searchContainer, assetEntryQuery, layout, portletPreferences,
				StringPool.BLANK, null, null, company.getCompanyId(),
				_group1.getGroupId(), TestPropsValues.getUserId(),
				assetEntryQuery.getClassNameIds(), null);

		Assert.assertTrue(assetEntryResults.isEmpty());
	}

	protected PortletPreferences getAssetPublisherPortletPreferences(
			List<AssetQueryRule> assetQueryRules)
		throws Exception {

		PortletPreferences portletPreferences = new MockPortletPreferences();

		for (int i = 0; i < assetQueryRules.size(); i++) {
			AssetQueryRule assetQueryRule = assetQueryRules.get(i);

			portletPreferences.setValue(
				"queryAndOperator" + i,
				String.valueOf(assetQueryRule.isAndOperator()));
			portletPreferences.setValue(
				"queryContains" + i,
				String.valueOf(assetQueryRule.isContains()));
			portletPreferences.setValue(
				"queryName" + i, assetQueryRule.getName());
			portletPreferences.setValues(
				"queryValues" + i, assetQueryRule.getValues());
		}

		return portletPreferences;
	}

	private void _checkAssetEntryResults(
		List<AssetEntryResult> assetEntryResults, long[] classPKs) {

		AssetEntryResult assetEntryResult = assetEntryResults.get(0);

		List<AssetEntry> assetEntries = assetEntryResult.getAssetEntries();

		AssetEntry assetEntry1 = assetEntries.get(0);

		Assert.assertEquals(assetEntry1.getClassPK(), classPKs[0]);

		AssetEntry assetEntry2 = assetEntries.get(1);

		Assert.assertEquals(assetEntry2.getClassPK(), classPKs[1]);
	}

	private static Configuration _assetPublisherWebConfiguration;

	@Inject
	private static ConfigurationAdmin _configurationAdmin;

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@Inject
	private AssetPublisherHelper _assetPublisherHelper;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group1;

	@DeleteAfterTestRun
	private Group _group2;

	@Inject
	private LayoutPageTemplateEntryService _layoutPageTemplateEntryService;

	@Inject
	private LayoutPrototypeLocalService _layoutPrototypeLocalService;

	@Inject
	private Portal _portal;

	@Inject(
		filter = "segments.criteria.contributor.key=user",
		type = SegmentsCriteriaContributor.class
	)
	private SegmentsCriteriaContributor _segmentsCriteriaContributor;

	@Inject
	private ViewCountManager _viewCountManager;

}