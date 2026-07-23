/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.exception.AssetCategoryException;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyGroupRelLocalService;
import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.view.count.ViewCountManager;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.ratings.test.util.RatingsTestUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alberto Chaparro
 */
@RunWith(Arquillian.class)
public class AssetEntryServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testGetEntriesCountNoFilters() throws Exception {
		AssetEntryQuery assetEntryQuery = new AssetEntryQuery();

		assetEntryQuery.setGroupIds(new long[] {_group.getGroupId()});

		int initialAssetEntriesCount = _assetEntryLocalService.getEntriesCount(
			assetEntryQuery);

		AssetTestUtil.addAssetEntry(_group.getGroupId());

		assetEntryQuery.setGroupIds(new long[] {_group.getGroupId()});

		int actualAssetEntriesCount = _assetEntryLocalService.getEntriesCount(
			assetEntryQuery);

		Assert.assertEquals(
			initialAssetEntriesCount + 1, actualAssetEntriesCount);
	}

	@Test
	public void testGetEntriesNoFilters() throws Exception {
		AssetEntryQuery assetEntryQuery = new AssetEntryQuery();

		assetEntryQuery.setGroupIds(new long[] {_group.getGroupId()});

		List<AssetEntry> initialAssetEntries =
			_assetEntryLocalService.getEntries(assetEntryQuery);

		AssetTestUtil.addAssetEntry(_group.getGroupId());

		assetEntryQuery.setGroupIds(new long[] {_group.getGroupId()});

		List<AssetEntry> assetEntries = _assetEntryLocalService.getEntries(
			assetEntryQuery);

		Assert.assertEquals(
			assetEntries.toString(), initialAssetEntries.size() + 1,
			assetEntries.size());

		AssetEntry assetEntry = assetEntries.get(0);

		Assert.assertTrue(
			assetEntries.toString(), assetEntries.contains(assetEntry));
	}

	@Test
	public void testGetEntriesOrderByPublishDateAndRatings() throws Exception {
		List<AssetEntry> expectedAssetEntries = createAssetEntries();

		AssetEntryQuery assetEntryQuery = new AssetEntryQuery();

		assetEntryQuery.setGroupIds(new long[] {_group.getGroupId()});
		assetEntryQuery.setOrderByCol1("publishDate");
		assetEntryQuery.setOrderByCol2("ratings");
		assetEntryQuery.setOrderByType1("DESC");
		assetEntryQuery.setOrderByType2("DESC");

		List<AssetEntry> actualAssetEntries =
			_assetEntryLocalService.getEntries(assetEntryQuery);

		validateAssetEntries(expectedAssetEntries, actualAssetEntries);
	}

	@Test
	public void testGetEntriesOrderByRatings() throws Exception {
		List<AssetEntry> expectedAssetEntries = createAssetEntries();

		AssetEntryQuery assetEntryQuery = new AssetEntryQuery();

		assetEntryQuery.setGroupIds(new long[] {_group.getGroupId()});
		assetEntryQuery.setOrderByCol1("ratings");
		assetEntryQuery.setOrderByType1("DESC");

		List<AssetEntry> actualAssetEntries =
			_assetEntryLocalService.getEntries(assetEntryQuery);

		validateAssetEntries(expectedAssetEntries, actualAssetEntries);
	}

	@Ignore
	@Test
	public void testGetEntriesOrderByRatingsAndViewCount() throws Exception {
		List<AssetEntry> expectedAssetEntries = new ArrayList<>(4);

		long classNameId = _portal.getClassNameId(AssetEntry.class);

		AssetEntry assetEntry1 = AssetTestUtil.addAssetEntry(
			_group.getGroupId());

		RatingsTestUtil.addStats(
			assetEntry1.getClassName(), assetEntry1.getClassPK(), 2000);

		_viewCountManager.incrementViewCount(
			assetEntry1.getCompanyId(), classNameId, assetEntry1.getEntryId(),
			2);

		AssetEntry assetEntry2 = AssetTestUtil.addAssetEntry(
			_group.getGroupId());

		RatingsTestUtil.addStats(
			assetEntry2.getClassName(), assetEntry2.getClassPK(), 2000);

		_viewCountManager.incrementViewCount(
			assetEntry2.getCompanyId(), classNameId, assetEntry2.getEntryId(),
			1);

		AssetEntry assetEntry3 = AssetTestUtil.addAssetEntry(
			_group.getGroupId());

		RatingsTestUtil.addStats(
			assetEntry3.getClassName(), assetEntry3.getClassPK(), 3000);

		AssetEntry assetEntry4 = AssetTestUtil.addAssetEntry(
			_group.getGroupId());

		RatingsTestUtil.addStats(
			assetEntry4.getClassName(), assetEntry4.getClassPK(), 1000);

		_viewCountManager.incrementViewCount(
			assetEntry4.getCompanyId(), classNameId, assetEntry4.getEntryId(),
			4);

		expectedAssetEntries.add(assetEntry3);
		expectedAssetEntries.add(assetEntry1);
		expectedAssetEntries.add(assetEntry2);
		expectedAssetEntries.add(assetEntry4);

		AssetEntryQuery assetEntryQuery = new AssetEntryQuery();

		assetEntryQuery.setGroupIds(new long[] {_group.getGroupId()});
		assetEntryQuery.setOrderByCol1("ratings");
		assetEntryQuery.setOrderByCol2("viewCount");
		assetEntryQuery.setOrderByType1("DESC");
		assetEntryQuery.setOrderByType2("DESC");

		List<AssetEntry> actualAssetEntries =
			_assetEntryLocalService.getEntries(assetEntryQuery);

		validateAssetEntries(expectedAssetEntries, actualAssetEntries);
	}

	@Test
	public void testGetEntriesOrderByViewCount() throws Exception {
		List<AssetEntry> expectedAssetEntries = createAssetEntries();

		AssetEntryQuery assetEntryQuery = new AssetEntryQuery();

		assetEntryQuery.setGroupIds(new long[] {_group.getGroupId()});
		assetEntryQuery.setOrderByCol1("viewCount");
		assetEntryQuery.setOrderByType1("DESC");

		List<AssetEntry> actualAssetEntries =
			_assetEntryLocalService.getEntries(assetEntryQuery);

		validateAssetEntries(expectedAssetEntries, actualAssetEntries);
	}

	@Test
	public void testGetTopViewedEntries() throws Exception {
		AssetEntry assetEntry = AssetTestUtil.addAssetEntry(
			_group.getGroupId(), new Date(), "testClass");

		_viewCountManager.incrementViewCount(
			assetEntry.getCompanyId(), _portal.getClassNameId(AssetEntry.class),
			assetEntry.getEntryId(), 2);

		List<AssetEntry> topViewedAssetEntries =
			_assetEntryLocalService.getTopViewedEntries(
				"testClass", false, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			topViewedAssetEntries.toString(), 1, topViewedAssetEntries.size());
	}

	@Test
	public void testSearch() throws Exception {
		JournalArticle journalArticle1 = JournalTestUtil.addArticle(
			_group.getGroupId(), 0);
		JournalArticle journalArticle2 = JournalTestUtil.addArticle(
			_group.getGroupId(), 0);

		Hits hits = _assetEntryLocalService.search(
			_group.getCompanyId(), new long[] {_group.getGroupId()},
			TestPropsValues.getUserId(),
			new long[] {_portal.getClassNameId(JournalArticle.class.getName())},
			-1L, StringPool.BLANK, false,
			new int[] {WorkflowConstants.STATUS_APPROVED}, 0, 50,
			new Sort[] {new Sort(Field.MODIFIED_DATE, Sort.LONG_TYPE, true)});

		Assert.assertTrue(hits.toString(), hits.getLength() >= 2);

		Document[] documents = hits.getDocs();

		Assert.assertEquals(
			journalArticle2.getResourcePrimKey(),
			GetterUtil.getLong(documents[0].get(Field.ENTRY_CLASS_PK)));
		Assert.assertEquals(
			journalArticle1.getResourcePrimKey(),
			GetterUtil.getLong(documents[1].get(Field.ENTRY_CLASS_PK)));
	}

	@Test(expected = AssetCategoryException.class)
	public void testValidate() throws Exception {
		AssetTestUtil.addVocabulary(
			_group.getGroupId(), _portal.getClassNameId(Group.class),
			AssetCategoryConstants.ALL_CLASS_TYPE_PK, true);

		Company company = _companyLocalService.getCompany(
			_group.getCompanyId());

		_assetEntryLocalService.validate(
			company.getGroupId(), Group.class.getName(), _group.getGroupId(), 0,
			new long[0], new String[0]);
	}

	@Test(expected = AssetCategoryException.class)
	public void testValidateCMS() throws Exception {
		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition();

		Group cmsGroup = _groupLocalService.getGroup(
			TestPropsValues.getCompanyId(), GroupConstants.CMS);

		_depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext(cmsGroup.getGroupId()));

		AssetEntry assetEntry = AssetTestUtil.addAssetEntry(
			_depotEntry.getGroupId(), new Date(),
			objectDefinition.getClassName());

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			cmsGroup.getGroupId(), AssetCategoryConstants.ALL_CLASS_NAME_ID,
			AssetCategoryConstants.ALL_CLASS_TYPE_PK, true);

		_assetVocabularyGroupRelLocalService.addAssetVocabularyGroupRel(
			_depotEntry.getGroupId(), assetVocabulary.getVocabularyId(),
			_depotEntry.getType());

		_assetEntryLocalService.validate(
			_depotEntry.getGroupId(), assetEntry.getClassName(),
			assetEntry.getClassPK(), 0, new long[0], new String[0]);
	}

	protected List<AssetEntry> createAssetEntries() throws Exception {
		Calendar calendar = CalendarFactoryUtil.getCalendar();

		calendar.add(Calendar.DAY_OF_MONTH, -1);

		Date yesterday = calendar.getTime();

		calendar.add(Calendar.DAY_OF_MONTH, -2);

		Date dayBeforeYesterday = calendar.getTime();

		long classNameId = _portal.getClassNameId(AssetEntry.class);

		AssetEntry assetEntry1 = AssetTestUtil.addAssetEntry(
			_group.getGroupId(), dayBeforeYesterday);

		RatingsTestUtil.addStats(
			assetEntry1.getClassName(), assetEntry1.getClassPK(), 2000);

		_viewCountManager.incrementViewCount(
			assetEntry1.getCompanyId(), classNameId, assetEntry1.getEntryId(),
			2);

		AssetEntry assetEntry2 = AssetTestUtil.addAssetEntry(
			_group.getGroupId(), dayBeforeYesterday);

		RatingsTestUtil.addStats(
			assetEntry2.getClassName(), assetEntry2.getClassPK(), 1000);

		_viewCountManager.incrementViewCount(
			assetEntry2.getCompanyId(), classNameId, assetEntry2.getEntryId(),
			1);

		AssetEntry assetEntry3 = AssetTestUtil.addAssetEntry(
			_group.getGroupId(), yesterday);

		RatingsTestUtil.addStats(
			assetEntry3.getClassName(), assetEntry3.getClassPK(), 3000);

		_viewCountManager.incrementViewCount(
			assetEntry3.getCompanyId(), classNameId, assetEntry3.getEntryId(),
			3);

		AssetEntry assetEntry4 = AssetTestUtil.addAssetEntry(
			_group.getGroupId(), dayBeforeYesterday);

		return ListUtil.fromArray(
			assetEntry3, assetEntry1, assetEntry2, assetEntry4);
	}

	protected void validateAssetEntries(
		List<AssetEntry> expectedAssetEntries,
		List<AssetEntry> actualAssetEntries) {

		Assert.assertEquals(
			actualAssetEntries.toString(), expectedAssetEntries.size(),
			actualAssetEntries.size());

		for (int i = 0; i < expectedAssetEntries.size(); i++) {
			AssetEntry expectedAssetEntry = expectedAssetEntries.get(i);
			AssetEntry actualAssetEntry = actualAssetEntries.get(i);

			Assert.assertEquals(
				expectedAssetEntry.getEntryId(), actualAssetEntry.getEntryId());
		}
	}

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@Inject
	private AssetVocabularyGroupRelLocalService
		_assetVocabularyGroupRelLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private ViewCountManager _viewCountManager;

}