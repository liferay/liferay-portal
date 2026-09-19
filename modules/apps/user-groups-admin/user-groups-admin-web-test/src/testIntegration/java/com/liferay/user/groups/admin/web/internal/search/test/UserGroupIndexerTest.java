/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.groups.admin.web.internal.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.test.randomizerbumpers.NumericStringRandomizerBumper;
import com.liferay.portal.kernel.test.randomizerbumpers.UniqueStringRandomizerBumper;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.users.admin.test.util.search.GroupBlueprint;
import com.liferay.users.admin.test.util.search.GroupSearchFixture;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author André de Oliveira
 */
@RunWith(Arquillian.class)
public class UserGroupIndexerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		GroupSearchFixture groupSearchFixture = new GroupSearchFixture();

		Group group = groupSearchFixture.addGroup(new GroupBlueprint());

		UserGroupFixture userGroupFixture = new UserGroupFixture(
			group, userGroupLocalService);

		_group = group;

		_groups = groupSearchFixture.getGroups();

		_userGroupFixture = userGroupFixture;
		_userGroups = userGroupFixture.getUserGroups();
	}

	@Test
	public void testSearchUserGroups() throws Exception {
		Role role = _addRole();

		long companyId = role.getCompanyId();

		groupLocalService.addRoleGroup(role.getRoleId(), _group.getGroupId());

		int originalUserGroupCount = userGroupLocalService.searchCount(
			companyId, null, new LinkedHashMap<String, Object>());

		String baseName = RandomTestUtil.randomString();

		int newUserGroupCount = 2;

		List<String> userGroupNames = new ArrayList<>();

		for (int i = 0; i < newUserGroupCount; i++) {
			UserGroup userGroup = addUserGroup(baseName);

			userGroupNames.add(userGroup.getName());
		}

		SearchRequestBuilder searchRequestBuilder1 = _getSearchRequestBuilder(
			companyId);

		SearchResponse searchResponse1 = searcher.search(
			searchRequestBuilder1.queryString(
				baseName
			).build());

		DocumentsAssert.assertValuesIgnoreRelevance(
			searchResponse1.getRequestString(), searchResponse1.getDocuments(),
			Field.NAME, userGroupNames);

		SearchRequestBuilder searchRequestBuilder2 = _getSearchRequestBuilder(
			companyId);

		SearchResponse searchResponse2 = searcher.search(
			searchRequestBuilder2.emptySearchEnabled(
				true
			).size(
				0
			).build());

		Assert.assertEquals(
			originalUserGroupCount + newUserGroupCount,
			searchResponse2.getCount());
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected UserGroup addUserGroup(String baseName) {
		return _userGroupFixture.createUserGroup(
			baseName + StringPool.SPACE + RandomTestUtil.randomString());
	}

	@Inject
	protected GroupLocalService groupLocalService;

	@Inject(
		filter = "indexer.class.name=com.liferay.portal.kernel.model.UserGroup"
	)
	protected Indexer<UserGroup> indexer;

	@Inject
	protected RoleLocalService roleLocalService;

	@Inject
	protected SearchRequestBuilderFactory searchRequestBuilderFactory;

	@Inject
	protected Searcher searcher;

	@Inject
	protected UserGroupLocalService userGroupLocalService;

	private Role _addRole() throws Exception {
		Role role = roleLocalService.addRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(), null, 0,
			RandomTestUtil.randomString(
				NumericStringRandomizerBumper.INSTANCE,
				UniqueStringRandomizerBumper.INSTANCE),
			null, null, RoleConstants.TYPE_REGULAR, null, null);

		_roles.add(role);

		return role;
	}

	private SearchRequestBuilder _getSearchRequestBuilder(long companyId) {
		return searchRequestBuilderFactory.builder(
		).companyId(
			companyId
		).fields(
			StringPool.STAR
		).modelIndexerClasses(
			UserGroup.class
		);
	}

	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups;

	@DeleteAfterTestRun
	private List<Role> _roles = new ArrayList<>();

	private UserGroupFixture _userGroupFixture;

	@DeleteAfterTestRun
	private List<UserGroup> _userGroups;

}