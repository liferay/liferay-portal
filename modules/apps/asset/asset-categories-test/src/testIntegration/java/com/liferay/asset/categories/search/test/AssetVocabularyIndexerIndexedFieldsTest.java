/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyGroupRel;
import com.liferay.asset.kernel.service.AssetVocabularyGroupRelLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyService;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.model.uid.UIDFactory;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.search.test.util.IndexedFieldsFixture;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portlet.asset.util.AssetVocabularySettingsHelper;
import com.liferay.users.admin.test.util.search.GroupBlueprint;
import com.liferay.users.admin.test.util.search.GroupSearchFixture;
import com.liferay.users.admin.test.util.search.UserSearchFixture;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Igor Fabiano Nazar
 * @author Luan Maoski
 * @author Lucas Marques
 */
@RunWith(Arquillian.class)
public class AssetVocabularyIndexerIndexedFieldsTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		GroupSearchFixture groupSearchFixture = new GroupSearchFixture();

		Group group = groupSearchFixture.addGroup(new GroupBlueprint());

		AssetVocabularyFixture assetVocabularyFixture =
			new AssetVocabularyFixture(assetVocabularyService, group);

		_assetVocabularies = assetVocabularyFixture.getAssetVocabularies();
		_assetVocabularyFixture = assetVocabularyFixture;

		_group = group;

		_groups = groupSearchFixture.getGroups();

		_indexedFieldsFixture = new IndexedFieldsFixture(
			resourcePermissionLocalService, searchEngineHelper, uidFactory);
		_defaultLocale = LocaleThreadLocal.getDefaultLocale();
	}

	@After
	public void tearDown() throws Exception {
		LocaleThreadLocal.setDefaultLocale(_defaultLocale);
	}

	@Test
	public void testIndexedFields() throws Exception {
		Locale locale = LocaleUtil.JAPAN;

		setTestLocale(locale);

		AssetVocabulary assetVocabulary =
			_assetVocabularyFixture.createAssetVocabulary("新しい商品");

		Group group1 = GroupTestUtil.addGroup();
		Group group2 = GroupTestUtil.addGroup();

		_assetVocabularyGroupRelLocalService.setAssetVocabularyGroupRels(
			assetVocabulary.getVocabularyId(),
			new long[] {group1.getGroupId(), group2.getGroupId()},
			DepotConstants.TYPE_SPACE);

		String searchTerm = "新しい";

		assertFieldValues(
			_expectedFieldValues(assetVocabulary), locale, searchTerm);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected void assertFieldValues(
		Map<String, String> map, Locale locale, String searchTerm) {

		FieldValuesAssert.assertFieldValues(
			map,
			name ->
				!name.contains(StringPool.PERIOD) && !name.equals("score") &&
				!name.equals("timestamp"),
			searcher.search(
				searchRequestBuilderFactory.builder(
				).companyId(
					_group.getCompanyId()
				).fetchSourceIncludes(
					new String[] {"*_sortable"}
				).fields(
					StringPool.STAR
				).groupIds(
					_group.getGroupId()
				).locale(
					locale
				).modelIndexerClasses(
					AssetVocabulary.class
				).queryString(
					searchTerm
				).build()));
	}

	protected void setTestLocale(Locale locale) throws Exception {
		_assetVocabularyFixture.updateDisplaySettings(locale);

		LocaleThreadLocal.setDefaultLocale(locale);
	}

	@Inject
	protected AssetVocabularyService assetVocabularyService;

	@Inject(
		filter = "indexer.class.name=com.liferay.asset.kernel.model.AssetVocabulary"
	)
	protected Indexer<AssetVocabulary> indexer;

	@Inject
	protected ResourcePermissionLocalService resourcePermissionLocalService;

	@Inject
	protected SearchEngineHelper searchEngineHelper;

	@Inject
	protected SearchRequestBuilderFactory searchRequestBuilderFactory;

	@Inject
	protected Searcher searcher;

	@Inject
	protected UIDFactory uidFactory;

	protected UserSearchFixture userSearchFixture;

	private Map<String, String> _expectedFieldValues(
			AssetVocabulary assetVocabulary)
		throws Exception {

		Map<String, String> map = HashMapBuilder.put(
			Field.ASSET_VOCABULARY_ID,
			String.valueOf(assetVocabulary.getVocabularyId())
		).put(
			Field.COMPANY_ID, String.valueOf(assetVocabulary.getCompanyId())
		).put(
			Field.ENTRY_CLASS_NAME, AssetVocabulary.class.getName()
		).put(
			Field.ENTRY_CLASS_PK,
			String.valueOf(assetVocabulary.getVocabularyId())
		).put(
			Field.GROUP_ID, String.valueOf(assetVocabulary.getGroupId())
		).put(
			Field.NAME, assetVocabulary.getName()
		).put(
			Field.SCOPE_GROUP_ID, String.valueOf(assetVocabulary.getGroupId())
		).put(
			Field.STAGING_GROUP, String.valueOf(_group.isStagingGroup())
		).put(
			Field.TITLE, assetVocabulary.getName()
		).put(
			Field.USER_ID, String.valueOf(assetVocabulary.getUserId())
		).put(
			Field.USER_NAME, StringUtil.lowerCase(assetVocabulary.getUserName())
		).put(
			Field.VISIBILITY_TYPE,
			String.valueOf(assetVocabulary.getVisibilityType())
		).put(
			"categoriesCount",
			String.valueOf(assetVocabulary.getCategoriesCount())
		).put(
			"categoriesCount_sortable",
			String.valueOf(assetVocabulary.getCategoriesCount())
		).put(
			"classNameIds",
			() -> {
				AssetVocabularySettingsHelper assetVocabularySettingsHelper =
					new AssetVocabularySettingsHelper(
						assetVocabulary.getSettings());

				long[] classNameIds =
					assetVocabularySettingsHelper.getClassNameIds();

				if (classNameIds.length == 1) {
					return String.valueOf(classNameIds[0]);
				}

				return Arrays.toString(classNameIds);
			}
		).put(
			"externalReferenceCode", assetVocabulary.getExternalReferenceCode()
		).put(
			"groupExternalReferenceCode", _group.getExternalReferenceCode()
		).put(
			"groupIds",
			() -> {
				List<Long> groupIds = ListUtil.toList(
					_assetVocabularyGroupRelLocalService.
						getAssetVocabularyGroupRelsByVocabularyId(
							assetVocabulary.getVocabularyId()),
					AssetVocabularyGroupRel::getGroupId);

				Collections.sort(groupIds);

				return String.valueOf(groupIds);
			}
		).put(
			"name_sortable", StringUtil.lowerCase(assetVocabulary.getName())
		).put(
			"scopeGroupExternalReferenceCode", _group.getExternalReferenceCode()
		).put(
			"title_ja_JP", assetVocabulary.getName()
		).put(
			"title_sortable", StringUtil.lowerCase(assetVocabulary.getName())
		).put(
			"userExternalReferenceCode",
			() -> {
				User user = TestPropsValues.getUser();

				return user.getExternalReferenceCode();
			}
		).put(
			"visibilityType_sortable",
			String.valueOf(assetVocabulary.getVisibilityType())
		).build();

		_indexedFieldsFixture.populateUID(assetVocabulary, map);

		_populateDates(assetVocabulary, map);
		_populateRoles(assetVocabulary, map);
		_populateLocalizedTitles(assetVocabulary.getTitle(), map);

		return map;
	}

	private void _populateDates(
		AssetVocabulary assetVocabulary, Map<String, String> map) {

		_indexedFieldsFixture.populateDate(
			Field.CREATE_DATE, assetVocabulary.getCreateDate(), map);
		_indexedFieldsFixture.populateDate(
			Field.MODIFIED_DATE, assetVocabulary.getModifiedDate(), map);
	}

	private void _populateLocalizedTitles(
		String title, Map<String, String> map) {

		map.put("localized_title", title);

		for (Locale locale : LanguageUtil.getAvailableLocales()) {
			StringBundler sb = new StringBundler(5);

			sb.append("localized_title_");
			sb.append(locale.getLanguage());
			sb.append("_");
			sb.append(locale.getCountry());

			map.put(sb.toString(), title);

			sb.append("_sortable");

			map.put(sb.toString(), title);
		}
	}

	private void _populateRoles(
			AssetVocabulary assetVocabulary, Map<String, String> map)
		throws Exception {

		_indexedFieldsFixture.populateRoleIdFields(
			assetVocabulary.getCompanyId(), AssetVocabulary.class.getName(),
			assetVocabulary.getVocabularyId(), assetVocabulary.getGroupId(),
			null, map);
	}

	@DeleteAfterTestRun
	private List<AssetVocabulary> _assetVocabularies;

	private AssetVocabularyFixture _assetVocabularyFixture;

	@Inject
	private AssetVocabularyGroupRelLocalService
		_assetVocabularyGroupRelLocalService;

	private Locale _defaultLocale;
	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups;

	private IndexedFieldsFixture _indexedFieldsFixture;

}