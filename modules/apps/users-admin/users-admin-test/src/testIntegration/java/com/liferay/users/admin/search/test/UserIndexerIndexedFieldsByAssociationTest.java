/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.model.uid.UIDFactory;
import com.liferay.portal.search.query.QueriesUtil;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.search.test.util.IndexedFieldsFixture;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.users.admin.test.util.search.GroupBlueprint;
import com.liferay.users.admin.test.util.search.GroupSearchFixture;
import com.liferay.users.admin.test.util.search.OrganizationBlueprint.OrganizationBlueprintBuilder;
import com.liferay.users.admin.test.util.search.OrganizationSearchFixture;
import com.liferay.users.admin.test.util.search.UserGroupSearchFixture;
import com.liferay.users.admin.test.util.search.UserSearchFixture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author André de Oliveira
 */
@RunWith(Arquillian.class)
public class UserIndexerIndexedFieldsByAssociationTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		groupSearchFixture = new GroupSearchFixture();

		organizationSearchFixture = new OrganizationSearchFixture(
			_organizationLocalService);

		userGroupSearchFixture = new UserGroupSearchFixture(
			_userGroupLocalService);

		userSearchFixture = new UserSearchFixture(
			_userLocalService, groupSearchFixture, organizationSearchFixture,
			userGroupSearchFixture);

		userSearchFixture.setUp();

		_addresses = userSearchFixture.getAddresses();

		_groups = groupSearchFixture.getGroups();

		_organizations = organizationSearchFixture.getOrganizations();

		_users = userSearchFixture.getUsers();

		_userGroups = userGroupSearchFixture.getUserGroups();

		indexedFieldsFixture = new IndexedFieldsFixture(
			_resourcePermissionLocalService, _searchEngineHelper, _uidFactory);
	}

	@Test
	public void testAssociationsThatDoNotIndexGroupIdFields() {
		String[] fieldNames = {
			_CT_COLLECTION_ID, Field.GROUP_ID, Field.SCOPE_GROUP_ID, Field.UID
		};

		UserGroup userGroup = addUserGroup();

		long userGroupId = userGroup.getUserGroupId();

		User user = addUser();

		Map<String, String> map = new HashMap<>();

		indexedFieldsFixture.populateUID(user, map);

		assertFieldValues(user, fieldNames, map);

		_userLocalService.addUserGroupUser(userGroupId, user);

		assertFieldValues(user, fieldNames, map);

		Group group = addGroup();

		_groupLocalService.addUserGroupGroup(userGroupId, group);

		assertFieldValues(user, fieldNames, map);
	}

	@Test
	public void testAssociationsThatIndexMoreFields() throws Exception {
		String[] fieldNames = {
			"ancestorOrganizationIds", Field.COMPANY_ID, _CT_COLLECTION_ID,
			Field.ENTRY_CLASS_NAME, Field.ENTRY_CLASS_PK, Field.GROUP_ID,
			"groupIds", "organizationIds", "organizationCount",
			Field.SCOPE_GROUP_ID, Field.UID, "userGroupIds", "userGroupRoleIds",
			"userGroupRoleNames", Field.USER_ID
		};

		UserGroup userGroup = addUserGroup();

		User user = addUser();

		Map<String, String> map1 = HashMapBuilder.put(
			Field.COMPANY_ID, String.valueOf(user.getCompanyId())
		).put(
			Field.ENTRY_CLASS_NAME, user.getModelClassName()
		).put(
			Field.ENTRY_CLASS_PK, String.valueOf(user.getPrimaryKeyObj())
		).put(
			Field.USER_ID, String.valueOf(user.getPrimaryKeyObj())
		).put(
			"organizationCount", "0"
		).build();

		indexedFieldsFixture.populateUID(user, map1);

		assertFieldValues(user, fieldNames, map1);

		OrganizationBlueprintBuilder organizationBlueprintBuilder =
			OrganizationSearchFixture.getTestOrganizationBlueprintBuilder();

		Organization organization = organizationSearchFixture.addOrganization(
			organizationBlueprintBuilder.build());

		long organizationId = organization.getOrganizationId();

		_userLocalService.addOrganizationUser(organizationId, user);

		Map<String, String> map2 = HashMapBuilder.putAll(
			map1
		).put(
			"organizationCount", "1"
		).put(
			"organizationIds", String.valueOf(organizationId)
		).build();

		assertFieldValues(user, fieldNames, map2);

		long userGroupId = userGroup.getUserGroupId();

		_userLocalService.addUserGroupUser(userGroupId, user);

		Map<String, String> map3 = HashMapBuilder.putAll(
			map2
		).put(
			"userGroupIds", String.valueOf(userGroupId)
		).build();

		assertFieldValues(user, fieldNames, map3);

		Group group = addGroup();

		long groupId = group.getGroupId();

		_userGroupLocalService.addGroupUserGroup(groupId, userGroup);

		HashMap<String, String> map4 = HashMapBuilder.putAll(
			map3
		).put(
			Field.GROUP_ID, String.valueOf(groupId)
		).put(
			Field.SCOPE_GROUP_ID, String.valueOf(groupId)
		).build();

		assertFieldValues(user, fieldNames, map4);

		Role groupRole = RoleTestUtil.addRole(RoleConstants.TYPE_SITE);

		_userGroupRoleLocalService.addUserGroupRole(
			user.getUserId(), group.getGroupId(), groupRole.getRoleId());

		assertFieldValues(
			user, fieldNames,
			HashMapBuilder.putAll(
				map4
			).put(
				"userGroupRoleIds", String.valueOf(groupRole.getRoleId())
			).put(
				"userGroupRoleNames",
				StringUtil.toLowerCase(groupRole.getName())
			).build());
	}

	@Test
	public void testNewGroupsIncludeTestUser() throws Exception {
		Group group = addGroup();

		SearchRequestBuilder searchRequestBuilder = getSearchRequestBuilder(
			group.getCompanyId()
		).fields(
			_CT_COLLECTION_ID, Field.GROUP_ID, Field.UID, Field.USER_ID
		).modelIndexerClasses(
			User.class
		);

		SearchResponse searchResponse1 = _searcher.search(
			searchRequestBuilder.emptySearchEnabled(
				true
			).query(
				QueriesUtil.term(Field.USER_ID, TestPropsValues.getUserId())
			).build());

		List<Document> documents = searchResponse1.getDocuments();

		Document document = documents.get(
			RandomTestUtil.randomInt(0, documents.size() - 1));

		List<Long> groupIds = document.getLongs(Field.GROUP_ID);

		long groupId = group.getGroupId();

		List<Long> sortedGroupIds = new ArrayList<>(groupIds);

		sortedGroupIds.sort(Comparator.comparing(String::valueOf));

		if (!groupIds.contains(groupId)) {
			DocumentsAssert.assertValuesIgnoreRelevance(
				searchResponse1.getRequestString(), documents, Field.GROUP_ID,
				_toSingletonListString(sortedGroupIds.toString()));
		}

		SearchResponse searchResponse2 = _searcher.search(
			searchRequestBuilder.query(
				QueriesUtil.term(Field.GROUP_ID, groupId)
			).build());

		DocumentsAssert.assertValuesIgnoreRelevance(
			searchResponse2.getRequestString(), searchResponse2.getDocuments(),
			Field.GROUP_ID, _toSingletonListString(sortedGroupIds.toString()));
	}

	protected Group addGroup() {
		return groupSearchFixture.addGroup(new GroupBlueprint());
	}

	protected User addUser() {
		return userSearchFixture.addUser(
			userSearchFixture.getTestUserBlueprintBuilder());
	}

	protected UserGroup addUserGroup() {
		return userGroupSearchFixture.addUserGroup(
			UserGroupSearchFixture.getTestUserGroupBlueprintBuilder());
	}

	protected void assertFieldValues(
		User user, String[] fieldNames, Map<String, String> map) {

		FieldValuesAssert.assertFieldValues(
			String.valueOf(user), searchUser(user, fieldNames),
			name -> !name.contains(StringPool.PERIOD), map);
	}

	protected SearchRequestBuilder getSearchRequestBuilder(long companyId) {
		return _searchRequestBuilderFactory.builder(
		).companyId(
			companyId
		);
	}

	protected Document searchUser(User user, String[] fieldNames) {
		SearchResponse searchResponse = _searcher.search(
			getSearchRequestBuilder(
				user.getCompanyId()
			).emptySearchEnabled(
				true
			).fields(
				fieldNames
			).modelIndexerClasses(
				user.getModelClass()
			).query(
				QueriesUtil.term(Field.ENTRY_CLASS_PK, user.getPrimaryKeyObj())
			).build());

		List<Document> documents = searchResponse.getDocuments();

		Document document = documents.get(0);

		return indexedFieldsFixture.postProcessDocument(document);
	}

	protected GroupSearchFixture groupSearchFixture;
	protected IndexedFieldsFixture indexedFieldsFixture;
	protected OrganizationSearchFixture organizationSearchFixture;
	protected UserGroupSearchFixture userGroupSearchFixture;
	protected UserSearchFixture userSearchFixture;

	private String _toSingletonListString(String string) {
		return String.valueOf(Collections.singletonList(string));
	}

	private static final String _CT_COLLECTION_ID = "ctCollectionId";

	@DeleteAfterTestRun
	private List<Address> _addresses = new ArrayList<>();

	@Inject
	private GroupLocalService _groupLocalService;

	@DeleteAfterTestRun
	private List<Group> _groups;

	@Inject
	private OrganizationLocalService _organizationLocalService;

	@DeleteAfterTestRun
	private List<Organization> _organizations;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private SearchEngineHelper _searchEngineHelper;

	@Inject
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Inject
	private Searcher _searcher;

	@Inject
	private UIDFactory _uidFactory;

	@Inject
	private UserGroupLocalService _userGroupLocalService;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@DeleteAfterTestRun
	private List<UserGroup> _userGroups;

	@Inject
	private UserLocalService _userLocalService;

	@DeleteAfterTestRun
	private List<User> _users;

}