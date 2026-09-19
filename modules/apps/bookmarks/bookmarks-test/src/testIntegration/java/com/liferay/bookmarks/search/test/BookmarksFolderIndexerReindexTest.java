/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.bookmarks.service.BookmarksEntryLocalService;
import com.liferay.bookmarks.service.BookmarksFolderService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.model.uid.UIDFactory;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.users.admin.test.util.search.GroupBlueprint;
import com.liferay.users.admin.test.util.search.GroupSearchFixture;
import com.liferay.users.admin.test.util.search.UserSearchFixture;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
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
public class BookmarksFolderIndexerReindexTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		Assert.assertEquals(
			MODEL_INDEXER_CLASS.getName(), indexer.getClassName());

		GroupSearchFixture groupSearchFixture = new GroupSearchFixture();

		Group group = groupSearchFixture.addGroup(new GroupBlueprint());

		UserSearchFixture userSearchFixture = new UserSearchFixture(
			userLocalService, groupSearchFixture, null, null);

		userSearchFixture.setUp();

		User user = userSearchFixture.addUser(
			RandomTestUtil.randomString(), group);

		BookmarksFixture bookmarksFixture = new BookmarksFixture(
			bookmarksEntryLocalService, bookmarksFolderService, group, user);

		_group = group;

		_groups = groupSearchFixture.getGroups();

		_users = userSearchFixture.getUsers();

		_bookmarksFixture = bookmarksFixture;
		_bookmarksFolders = bookmarksFixture.getBookmarksFolders();
	}

	@Test
	public void testReindex() throws Exception {
		BookmarksFolder bookmarksFolder =
			_bookmarksFixture.createBookmarksFolder();

		String searchTerm = StringUtil.toLowerCase(
			bookmarksFolder.getUserName());

		assertFieldValue(Field.USER_NAME, searchTerm, searchTerm);

		deleteDocument(
			bookmarksFolder.getCompanyId(), uidFactory.getUID(bookmarksFolder));

		assertNoHits(searchTerm);

		reindexAllIndexerModels();

		assertFieldValue(Field.USER_NAME, searchTerm, searchTerm);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected void assertFieldValue(
		String fieldName, String fieldValue, String searchTerm) {

		FieldValuesAssert.assertFieldValue(
			fieldName, fieldValue, search(searchTerm));
	}

	protected void assertNoHits(String searchTerm) {
		FieldValuesAssert.assertFieldValues(
			Collections.emptyMap(), search(searchTerm));
	}

	protected void deleteDocument(long companyId, String uid) throws Exception {
		indexWriterHelper.deleteDocument(companyId, uid, true);
	}

	protected void reindexAllIndexerModels() throws Exception {
		indexer.reindexCompany(_group.getCompanyId());
	}

	protected SearchResponse search(String searchTerm) {
		return searcher.search(
			searchRequestBuilderFactory.builder(
			).companyId(
				_group.getCompanyId()
			).groupIds(
				_group.getGroupId()
			).fields(
				StringPool.STAR
			).modelIndexerClasses(
				BookmarksFolder.class
			).queryString(
				searchTerm
			).build());
	}

	protected static final Class<?> MODEL_INDEXER_CLASS = BookmarksFolder.class;

	@Inject
	protected BookmarksEntryLocalService bookmarksEntryLocalService;

	@Inject
	protected BookmarksFolderService bookmarksFolderService;

	@Inject
	protected IndexWriterHelper indexWriterHelper;

	@Inject(
		filter = "indexer.class.name=com.liferay.bookmarks.model.BookmarksFolder"
	)
	protected Indexer<BookmarksFolder> indexer;

	@Inject
	protected SearchRequestBuilderFactory searchRequestBuilderFactory;

	@Inject
	protected Searcher searcher;

	@Inject
	protected UIDFactory uidFactory;

	@Inject
	protected UserLocalService userLocalService;

	private BookmarksFixture _bookmarksFixture;

	@DeleteAfterTestRun
	private List<BookmarksFolder> _bookmarksFolders;

	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups;

	@DeleteAfterTestRun
	private List<User> _users;

}