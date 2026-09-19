/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery.PerformActionMethod;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.QueriesUtil;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.users.admin.test.util.search.GroupBlueprint;
import com.liferay.users.admin.test.util.search.GroupSearchFixture;
import com.liferay.users.admin.test.util.search.OrganizationSearchFixture;
import com.liferay.users.admin.test.util.search.UserGroupSearchFixture;
import com.liferay.users.admin.test.util.search.UserSearchFixture;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author André de Oliveira
 */
@RunWith(Arquillian.class)
public class UserGroupCascadeReindexUsersTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		if (_STRESS_MODE_10_MIN_TO_RUN_ALL_TESTS) {
			_groupsCount = 5;
			_usersCount = 100;
		}
		else {
			_groupsCount = 2;
			_usersCount = 3;
		}

		groupSearchFixture = new GroupSearchFixture();

		organizationSearchFixture = new OrganizationSearchFixture(
			_organizationLocalService);

		userGroupSearchFixture = new UserGroupSearchFixture(
			_userGroupLocalService);

		userSearchFixture = new UserSearchFixture(
			_userLocalService, groupSearchFixture, organizationSearchFixture,
			userGroupSearchFixture);

		userSearchFixture.setUp();
	}

	@Test
	public void testAddUsersOnly() throws Exception {
		addUsers(_usersCount);
	}

	@Test
	public void testAddUsersThenRetrieveBulk() throws Exception {
		List<User> users = addUsers(_usersCount);

		doTraverseWithActionableDynamicQuery(
			users,
			user -> {
			});
	}

	@Test
	public void testAddUsersThenRetrieveNonbulk() throws Exception {
		List<User> users = addUsers(_usersCount);

		doTraverseWithIndividualFetches(
			users,
			user -> {
			});
	}

	@Test
	public void testAddUsersThenTranslateBulk() throws Exception {
		List<User> users = addUsers(_usersCount);

		doTraverseWithActionableDynamicQuery(users, this::_translate);
	}

	@Test
	public void testAddUsersThenTranslateNonbulk() {
		List<User> users = addUsers(_usersCount);

		doTraverseWithIndividualFetches(users, this::_translate);
	}

	@Test
	public void testPerformanceOfMultiSiteLargeUserGroup() throws Exception {
		UserGroup userGroup = addUserGroup();

		long userGroupId = userGroup.getUserGroupId();

		List<User> users = addUsers(_usersCount);

		_userLocalService.addUserGroupUsers(userGroupId, users);

		SearchResponse searchResponse = searchUsersInUserGroup(userGroup);

		DocumentsAssert.assertValues(
			searchResponse.getRequestString(), searchResponse.getDocuments(),
			"userGroupIds", _repeat(String.valueOf(userGroupId), users.size()));

		List<Group> groups = addGroups(_groupsCount);

		for (Group group : groups) {
			_userGroupLocalService.addGroupUserGroup(
				group.getGroupId(), userGroup);
		}

		searchResponse = searchUsersInGroup(groups.get(0));

		DocumentsAssert.assertValues(
			searchResponse.getRequestString(), searchResponse.getDocuments(),
			Field.GROUP_ID,
			_repeat(_getAllGroupIdsString(groups), users.size()));
	}

	protected Group addGroup() {
		return groupSearchFixture.addGroup(new GroupBlueprint());
	}

	protected List<Group> addGroups(int groupsCount) {
		List<Group> groups = new ArrayList<>(groupsCount);

		for (int i = 0; i < groupsCount; i++) {
			groups.add(addGroup());
		}

		return groups;
	}

	protected User addUser() {
		return userSearchFixture.addUser(
			userSearchFixture.getTestUserBlueprintBuilder());
	}

	protected UserGroup addUserGroup() {
		return userGroupSearchFixture.addUserGroup(
			UserGroupSearchFixture.getTestUserGroupBlueprintBuilder());
	}

	protected List<User> addUsers(int count) {
		List<User> users = new ArrayList<>(count);

		for (int i = 0; i < count; i++) {
			users.add(addUser());
		}

		return users;
	}

	protected void doTraverseWithActionableDynamicQuery(
			List<User> users, PerformActionMethod<User> performActionMethod)
		throws Exception {

		long[] userIds = TransformUtil.transformToLongArray(
			users, User::getUserId);

		User user = users.get(0);

		long companyId = user.getCompanyId();

		ActionableDynamicQuery actionableDynamicQuery =
			_userLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				Property userId = PropertyFactoryUtil.forName("userId");

				dynamicQuery.add(userId.in(userIds));
			});
		actionableDynamicQuery.setCompanyId(companyId);
		actionableDynamicQuery.setPerformActionMethod(performActionMethod);

		actionableDynamicQuery.performActions();
	}

	protected void doTraverseWithIndividualFetches(
		List<User> users, Consumer<User> consumer) {

		for (long userId :
				TransformUtil.transformToLongArray(users, User::getUserId)) {

			consumer.accept(_userLocalService.fetchUser(userId));
		}
	}

	protected SearchRequestBuilder getSearchRequestBuilder(long companyId) {
		return _searchRequestBuilderFactory.builder(
		).withSearchContext(
			searchContext -> searchContext.setCompanyId(companyId)
		);
	}

	protected long getTestUserId() {
		try {
			return TestPropsValues.getUserId();
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}
	}

	protected SearchResponse searchUsersInGroup(Group group) {
		BooleanQuery booleanQuery = QueriesUtil.booleanQuery();

		booleanQuery.addMustQueryClauses(
			QueriesUtil.term(Field.GROUP_ID, group.getGroupId()));

		booleanQuery.addMustNotQueryClauses(
			QueriesUtil.term(Field.USER_ID, getTestUserId()));

		return _searcher.search(
			getSearchRequestBuilder(
				group.getCompanyId()
			).emptySearchEnabled(
				true
			).fields(
				Field.GROUP_ID
			).modelIndexerClasses(
				User.class
			).query(
				booleanQuery
			).build());
	}

	protected SearchResponse searchUsersInUserGroup(UserGroup userGroup) {
		return _searcher.search(
			getSearchRequestBuilder(
				userGroup.getCompanyId()
			).emptySearchEnabled(
				true
			).fields(
				"userGroupIds"
			).modelIndexerClasses(
				User.class
			).query(
				QueriesUtil.term("userGroupIds", userGroup.getUserGroupId())
			).build());
	}

	protected GroupSearchFixture groupSearchFixture;
	protected OrganizationSearchFixture organizationSearchFixture;
	protected UserGroupSearchFixture userGroupSearchFixture;
	protected UserSearchFixture userSearchFixture;

	private String _getAllGroupIdsString(List<Group> groups) {
		List<String> sortedGroupsIds = TransformUtil.transform(
			groups, group -> String.valueOf(group.getGroupId()));

		sortedGroupsIds.sort(String::compareTo);

		return sortedGroupsIds.toString();
	}

	private void _getDocument(Indexer<User> indexer, User user) {
		try {
			indexer.getDocument(user);
		}
		catch (SearchException searchException) {
			throw new RuntimeException(searchException);
		}
	}

	private String _repeat(String s, int times) {
		List<String> strings = new ArrayList<>(times);

		for (int i = 0; i < times; i++) {
			strings.add(s);
		}

		return strings.toString();
	}

	private void _translate(User user) {
		if (!user.isGuestUser()) {
			_getDocument(_indexer, user);
		}
	}

	private static final boolean _STRESS_MODE_10_MIN_TO_RUN_ALL_TESTS = false;

	private int _groupsCount;

	@Inject(filter = "indexer.class.name=com.liferay.portal.kernel.model.User")
	private Indexer<User> _indexer;

	@Inject
	private OrganizationLocalService _organizationLocalService;

	@Inject
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Inject
	private Searcher _searcher;

	@Inject
	private UserGroupLocalService _userGroupLocalService;

	@Inject
	private UserLocalService _userLocalService;

	private int _usersCount;

}