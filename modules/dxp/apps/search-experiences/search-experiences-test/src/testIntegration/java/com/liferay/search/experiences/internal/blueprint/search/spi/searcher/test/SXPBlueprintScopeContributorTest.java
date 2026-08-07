/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.internal.blueprint.search.spi.searcher.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.model.DepotEntryGroupRel;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.spi.searcher.SearchRequestContributor;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.search.experiences.model.SXPBlueprint;
import com.liferay.search.experiences.service.SXPBlueprintLocalService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Joshua Cords
 */
@RunWith(Arquillian.class)
public class SXPBlueprintScopeContributorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_group1 = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group1, TestPropsValues.getUserId());

		_addJournalArticle(_group1, _GROUP_1_TITLE);

		_group2 = GroupTestUtil.addGroup();

		_addJournalArticle(_group2, _GROUP_2_TITLE);

		_assetLibraryDepotEntry = _addDepotEntry(
			DepotConstants.TYPE_ASSET_LIBRARY);

		_assetLibraryGroup = _assetLibraryDepotEntry.getGroup();

		_addJournalArticle(_assetLibraryGroup, _ASSET_LIBRARY_TITLE);

		_spaceDepotEntry = _addDepotEntry(DepotConstants.TYPE_SPACE);

		_spaceGroup = _spaceDepotEntry.getGroup();

		_addJournalArticle(_spaceGroup, _SPACE_TITLE);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_depotEntryLocalService.deleteDepotEntry(_assetLibraryDepotEntry);
		_depotEntryLocalService.deleteDepotEntry(_spaceDepotEntry);
		_sxpBlueprintLocalService.deleteSXPBlueprint(_sxpBlueprint);

		GroupTestUtil.deleteGroup(_group1);
		GroupTestUtil.deleteGroup(_group2);
	}

	@Test
	public void testAssetLibraryScope() throws Exception {
		_configureSXPBlueprint(_assetLibraryGroup.getExternalReferenceCode());

		_assertSearch(Arrays.asList(_ASSET_LIBRARY_TITLE));
	}

	@Test
	public void testNoScope() throws Exception {
		_configureSXPBlueprint();

		SearchResponse searchResponse = _search();

		Assert.assertTrue(
			searchResponse.getRequestString(),
			searchResponse.getTotalHits() >= 4);
	}

	@Test
	public void testSiteScope() throws Exception {
		_configureSXPBlueprint(_group1.getExternalReferenceCode());

		_assertSearch(Arrays.asList(_GROUP_1_TITLE));

		_configureSXPBlueprint(
			_group1.getExternalReferenceCode(),
			_group2.getExternalReferenceCode());

		_assertSearch(Arrays.asList(_GROUP_1_TITLE, _GROUP_2_TITLE));
	}

	@Test
	public void testSiteScopeWithConnectedAssetLibraryExcludesAssetLibrary()
		throws Exception {

		DepotEntryGroupRel depotEntryGroupRel = null;

		try {
			depotEntryGroupRel =
				_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
					_assetLibraryDepotEntry.getDepotEntryId(),
					_group1.getGroupId());

			_configureSXPBlueprint(_group1.getExternalReferenceCode());

			_assertSearch(ListUtil.toList(_GROUP_1_TITLE));
		}
		finally {
			if (depotEntryGroupRel != null) {
				_depotEntryGroupRelLocalService.deleteDepotEntryGroupRel(
					depotEntryGroupRel);
			}
		}
	}

	@Test
	public void testSpaceScope() throws Exception {
		_configureSXPBlueprint(_spaceGroup.getExternalReferenceCode());

		_assertSearch(Arrays.asList("contents", "files", _SPACE_TITLE));
	}

	private static DepotEntry _addDepotEntry(int type) throws Exception {
		return _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			type, _serviceContext);
	}

	private static void _addJournalArticle(Group group, String title)
		throws Exception {

		JournalTestUtil.addArticle(
			group.getGroupId(), JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
			HashMapBuilder.put(
				LocaleUtil.US, title
			).build(),
			HashMapBuilder.put(
				LocaleUtil.US, StringPool.BLANK
			).build(),
			HashMapBuilder.put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			LocaleUtil.US, null, false, true, _serviceContext);
	}

	private void _assertSearch(List<String> expected) throws Exception {
		SearchResponse searchResponse = _search();

		DocumentsAssert.assertValuesIgnoreRelevance(
			searchResponse.getRequestString(), searchResponse.getDocuments(),
			_LOCALIZED_TITLE_FIELD, expected);
	}

	private void _configureSXPBlueprint(
			String... scopeGroupExternalReferenceCodes)
		throws Exception {

		JSONObject configurationJSONObject = JSONUtil.put(
			"emptySearchEnabled", true);

		if (ArrayUtil.isNotEmpty(scopeGroupExternalReferenceCodes)) {
			configurationJSONObject.put(
				"scope",
				JSONUtil.putAll((Object[])scopeGroupExternalReferenceCodes));
		}

		JSONObject generalConfigurationJSONObject = JSONUtil.put(
			"generalConfiguration", configurationJSONObject);

		if (_sxpBlueprint == null) {
			_sxpBlueprint = _sxpBlueprintLocalService.addSXPBlueprint(
				null, TestPropsValues.getUserId(),
				generalConfigurationJSONObject.toString(),
				Collections.singletonMap(LocaleUtil.US, StringPool.BLANK),
				StringPool.BLANK, StringPool.BLANK,
				Collections.singletonMap(
					LocaleUtil.US, RandomTestUtil.randomString()),
				_serviceContext);
		}
		else {
			_sxpBlueprint.setConfigurationJSON(
				generalConfigurationJSONObject.toString());

			_sxpBlueprint = _sxpBlueprintLocalService.updateSXPBlueprint(
				_sxpBlueprint);
		}
	}

	private SearchResponse _search() throws Exception {
		return _searcher.search(
			_searchRequestContributor.contribute(
				_searchRequestBuilderFactory.builder(
				).companyId(
					TestPropsValues.getCompanyId()
				).withSearchContext(
					searchContext -> searchContext.setAttribute(
						"search.experiences.blueprint.external.reference.code",
						_sxpBlueprint.getExternalReferenceCode())
				).build()));
	}

	private static final String _ASSET_LIBRARY_TITLE = "asset library title";

	private static final String _GROUP_1_TITLE = "group 1 title";

	private static final String _GROUP_2_TITLE = "group 2 title";

	private static final String _LOCALIZED_TITLE_FIELD =
		"localized_title_en_US";

	private static final String _SPACE_TITLE = "space title";

	private static DepotEntry _assetLibraryDepotEntry;
	private static Group _assetLibraryGroup;

	@Inject
	private static DepotEntryLocalService _depotEntryLocalService;

	private static Group _group1;
	private static Group _group2;
	private static ServiceContext _serviceContext;
	private static DepotEntry _spaceDepotEntry;
	private static Group _spaceGroup;
	private static SXPBlueprint _sxpBlueprint;

	@Inject
	private static SXPBlueprintLocalService _sxpBlueprintLocalService;

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private Searcher _searcher;

	@Inject
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Inject
	private SearchRequestContributor _searchRequestContributor;

}