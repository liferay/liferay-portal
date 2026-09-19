/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.search.test.util.IndexerFixture;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.users.admin.test.util.search.GroupBlueprint;
import com.liferay.users.admin.test.util.search.GroupSearchFixture;
import com.liferay.users.admin.test.util.search.UserSearchFixture;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Igor Fabiano Nazar
 * @author Luan Maoski
 */
@RunWith(Arquillian.class)
public class UserIndexerReindexTest {

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

		UserSearchFixture userSearchFixture = new UserSearchFixture(
			userLocalService, groupSearchFixture, null, null);

		_groups = groupSearchFixture.getGroups();

		_groupSearchFixture = groupSearchFixture;

		_indexerFixture = new IndexerFixture<>(User.class);

		_users = userSearchFixture.getUsers();

		_userSearchFixture = userSearchFixture;

		_userSearchFixture.setUp();
	}

	@After
	public void tearDown() {
		_userSearchFixture.tearDown();
	}

	@Test
	public void testReindex() throws Exception {
		Group group = _groupSearchFixture.addGroup(new GroupBlueprint());

		String screenName = RandomTestUtil.randomString();

		User user = _userSearchFixture.addUser(screenName, group);

		String searchTerm = user.getFirstName();

		Document document = _indexerFixture.searchOnlyOne(searchTerm);

		_indexerFixture.deleteDocument(document);

		_indexerFixture.searchNoOne(searchTerm);

		_indexerFixture.reindexCompany(user.getCompanyId());

		_indexerFixture.searchOnlyOne(searchTerm);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	@Inject
	protected UserLocalService userLocalService;

	private GroupSearchFixture _groupSearchFixture;

	@DeleteAfterTestRun
	private List<Group> _groups;

	private IndexerFixture<User> _indexerFixture;
	private UserSearchFixture _userSearchFixture;

	@DeleteAfterTestRun
	private List<User> _users;

}