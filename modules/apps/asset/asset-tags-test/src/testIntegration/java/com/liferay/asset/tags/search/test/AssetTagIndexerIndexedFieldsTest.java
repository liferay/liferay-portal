/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.tags.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.model.AssetTagGroupRel;
import com.liferay.asset.kernel.service.AssetTagGroupRelLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.SearchEngine;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.filter.ComplexQueryPart;
import com.liferay.portal.search.filter.ComplexQueryPartBuilderFactory;
import com.liferay.portal.search.model.uid.UIDFactory;
import com.liferay.portal.search.query.QueriesUtil;
import com.liferay.portal.search.query.Query;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.search.test.util.IndexedFieldsFixture;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.users.admin.test.util.search.GroupBlueprint;
import com.liferay.users.admin.test.util.search.GroupSearchFixture;
import com.liferay.users.admin.test.util.search.UserSearchFixture;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assume;
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
public class AssetTagIndexerIndexedFieldsTest {

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

		UserSearchFixture userSearchFixture = new UserSearchFixture(
			userLocalService, groupSearchFixture, null, null);

		userSearchFixture.setUp();

		User user = userSearchFixture.addUser(
			RandomTestUtil.randomString(), group);

		AssetTagFixture assetTagFixture = new AssetTagFixture(
			assetTagLocalService, group, user);

		_assetTagFixture = assetTagFixture;
		_assetTags = assetTagFixture.getAssetTags();

		_group = group;

		_groups = groupSearchFixture.getGroups();

		_indexedFieldsFixture = new IndexedFieldsFixture(
			resourcePermissionLocalService, searchEngineHelper, uidFactory);

		_users = userSearchFixture.getUsers();
	}

	@Test
	public void testIndexedFields() throws Exception {
		Assume.assumeFalse(
			isNumberSortableImplementedAsDoubleForSearchEngine());

		AssetTag assetTag = _assetTagFixture.createAssetTag();

		Group group1 = GroupTestUtil.addGroup();
		Group group2 = GroupTestUtil.addGroup();

		_assetTagGroupRelLocalService.setAssetTagGroupRels(
			assetTag.getTagId(),
			new long[] {group1.getGroupId(), group2.getGroupId()});

		String searchTerm = String.valueOf(assetTag.getPrimaryKey());

		assertFieldValues(_expectedFieldValues(assetTag), searchTerm);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected void assertFieldValues(Map<String, String> map, String classPK) {
		FieldValuesAssert.assertFieldValues(
			map,
			name ->
				!name.contains(StringPool.PERIOD) && !name.equals("score") &&
				!name.equals("timestamp"),
			searcher.search(
				searchRequestBuilderFactory.builder(
				).companyId(
					_group.getCompanyId()
				).emptySearchEnabled(
					true
				).groupIds(
					_group.getGroupId()
				).fields(
					StringPool.STAR
				).modelIndexerClasses(
					AssetTag.class
				).addComplexQueryPart(
					_getComplexQueryPart(
						QueriesUtil.term("entryClassPK", classPK))
				).build()));
	}

	protected boolean isNumberSortableImplementedAsDoubleForSearchEngine() {
		SearchEngine searchEngine = searchEngineHelper.getSearchEngine();

		String vendor = searchEngine.getVendor();

		return vendor.equals("Solr");
	}

	@Inject
	protected AssetTagLocalService assetTagLocalService;

	@Inject(
		filter = "indexer.class.name=com.liferay.asset.kernel.model.AssetTag"
	)
	protected Indexer<AssetTag> indexer;

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

	@Inject
	protected UserLocalService userLocalService;

	private Map<String, String> _expectedFieldValues(AssetTag assetTag)
		throws Exception {

		Map<String, String> map = HashMapBuilder.put(
			Field.COMPANY_ID, String.valueOf(assetTag.getCompanyId())
		).put(
			Field.ENTRY_CLASS_NAME, AssetTag.class.getName()
		).put(
			Field.ENTRY_CLASS_PK, String.valueOf(assetTag.getTagId())
		).put(
			Field.GROUP_ID, String.valueOf(assetTag.getGroupId())
		).put(
			Field.NAME, assetTag.getName()
		).put(
			Field.SCOPE_GROUP_ID, String.valueOf(assetTag.getGroupId())
		).put(
			Field.STAGING_GROUP, String.valueOf(_group.isStagingGroup())
		).put(
			Field.USER_ID, String.valueOf(assetTag.getUserId())
		).put(
			Field.USER_NAME, StringUtil.lowerCase(assetTag.getUserName())
		).put(
			"assetCount", String.valueOf(assetTag.getAssetCount())
		).put(
			"assetCount_Number_sortable",
			String.valueOf(assetTag.getAssetCount())
		).put(
			"externalReferenceCode", assetTag.getExternalReferenceCode()
		).put(
			"groupExternalReferenceCode", _group.getExternalReferenceCode()
		).put(
			"groupIds",
			() -> {
				List<Long> groupIds = ListUtil.toList(
					_assetTagGroupRelLocalService.getAssetTagGroupRelsByTagId(
						assetTag.getTagId()),
					AssetTagGroupRel::getGroupId);

				Collections.sort(groupIds);

				return String.valueOf(groupIds);
			}
		).put(
			"name_String_sortable", StringUtil.toLowerCase(assetTag.getName())
		).put(
			"scopeGroupExternalReferenceCode", _group.getExternalReferenceCode()
		).put(
			"subscribed", "false"
		).put(
			"userExternalReferenceCode",
			() -> {
				User user = _users.get(0);

				return user.getExternalReferenceCode();
			}
		).build();

		_indexedFieldsFixture.populateUID(assetTag, map);

		_populateDates(assetTag, map);
		_populateRoles(assetTag, map);

		return map;
	}

	private ComplexQueryPart _getComplexQueryPart(Query query) {
		return _complexQueryPartBuilderFactory.builder(
		).query(
			query
		).build();
	}

	private void _populateDates(AssetTag assetTag, Map<String, String> map) {
		_indexedFieldsFixture.populateDate(
			Field.CREATE_DATE, assetTag.getCreateDate(), map);
		_indexedFieldsFixture.populateDate(
			Field.MODIFIED_DATE, assetTag.getModifiedDate(), map);
	}

	private void _populateRoles(AssetTag assetTag, Map<String, String> map)
		throws Exception {

		_indexedFieldsFixture.populateRoleIdFields(
			assetTag.getCompanyId(), AssetTag.class.getName(),
			assetTag.getTagId(), assetTag.getGroupId(), null, map);
	}

	private AssetTagFixture _assetTagFixture;

	@Inject
	private AssetTagGroupRelLocalService _assetTagGroupRelLocalService;

	@DeleteAfterTestRun
	private List<AssetTag> _assetTags;

	@Inject
	private ComplexQueryPartBuilderFactory _complexQueryPartBuilderFactory;

	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups;

	private IndexedFieldsFixture _indexedFieldsFixture;

	@DeleteAfterTestRun
	private List<User> _users;

}