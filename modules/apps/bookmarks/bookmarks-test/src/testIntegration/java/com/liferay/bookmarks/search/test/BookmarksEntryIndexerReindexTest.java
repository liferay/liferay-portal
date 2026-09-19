/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.bookmarks.constants.BookmarksFolderConstants;
import com.liferay.bookmarks.model.BookmarksEntry;
import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.bookmarks.service.BookmarksEntryLocalService;
import com.liferay.bookmarks.service.BookmarksFolderService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
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
import com.liferay.portal.search.test.util.DocumentsAssert;
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
 */
@RunWith(Arquillian.class)
public class BookmarksEntryIndexerReindexTest {

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

		_bookmarksEntries = bookmarksFixture.getBookmarksEntries();
		_bookmarksFixture = bookmarksFixture;
		_bookmarksFolders = bookmarksFixture.getBookmarksFolders();

		_group = group;
		_groups = groupSearchFixture.getGroups();
		_user = user;
		_users = userSearchFixture.getUsers();
	}

	@Test
	public void testReindex() throws Exception {
		BookmarksEntry bookmarksEntry =
			_bookmarksFixture.createBookmarksEntry();

		String searchTerm = StringUtil.toLowerCase(
			bookmarksEntry.getUserName());

		assertFieldValue(Field.USER_NAME, searchTerm, searchTerm);

		deleteDocument(
			bookmarksEntry.getCompanyId(), uidFactory.getUID(bookmarksEntry));

		assertNoHits(searchTerm);

		reindexAllIndexerModels();

		assertFieldValue(Field.USER_NAME, searchTerm, searchTerm);
	}

	@Test
	public void testReindexEntriesInTheRootFolder() throws Exception {
		long folderId = BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID;

		testReindexEntriesInFolder(folderId);
	}

	@Test
	public void testReindexEntriesInTheSameFolder() throws Exception {
		BookmarksFolder bookmarksFolder =
			_bookmarksFixture.createBookmarksFolder();

		testReindexEntriesInFolder(bookmarksFolder.getFolderId());
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
			).fields(
				StringPool.STAR
			).groupIds(
				_group.getGroupId()
			).modelIndexerClasses(
				MODEL_INDEXER_CLASS
			).ownerUserId(
				_user.getUserId()
			).queryString(
				searchTerm
			).build());
	}

	protected void searchAndAssertLength(String searchTerm, int expected) {
		SearchResponse searchResponse = search(searchTerm);

		List<Document> documents = searchResponse.getDocuments71();

		DocumentsAssert.assertCount(
			searchResponse.getRequestString(),
			documents.toArray(new Document[0]), Field.USER_NAME, expected);
	}

	protected void testReindexEntriesInFolder(long folderId) throws Exception {
		BookmarksEntry bookmarksEntry = _bookmarksFixture.createBookmarksEntry(
			folderId);

		_bookmarksFixture.createBookmarksEntry(folderId);

		List<String> searchTerms = TransformUtil.transform(
			_bookmarksEntries, curBookmarkEntry -> curBookmarkEntry.getName());

		searchAndAssertLength(searchTerms.toString(), 2);

		deleteDocument(
			bookmarksEntry.getCompanyId(), uidFactory.getUID(bookmarksEntry));

		reindexAllIndexerModels();

		searchAndAssertLength(searchTerms.toString(), 2);
	}

	protected static final Class<?> MODEL_INDEXER_CLASS = BookmarksEntry.class;

	@Inject
	protected BookmarksEntryLocalService bookmarksEntryLocalService;

	@Inject
	protected BookmarksFolderService bookmarksFolderService;

	@Inject
	protected IndexWriterHelper indexWriterHelper;

	@Inject(
		filter = "indexer.class.name=com.liferay.bookmarks.model.BookmarksEntry"
	)
	protected Indexer<BookmarksEntry> indexer;

	@Inject
	protected SearchRequestBuilderFactory searchRequestBuilderFactory;

	@Inject
	protected Searcher searcher;

	@Inject
	protected UIDFactory uidFactory;

	@Inject
	protected UserLocalService userLocalService;

	@DeleteAfterTestRun
	private List<BookmarksEntry> _bookmarksEntries;

	private BookmarksFixture _bookmarksFixture;

	@DeleteAfterTestRun
	private List<BookmarksFolder> _bookmarksFolders;

	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups;

	private User _user;

	@DeleteAfterTestRun
	private List<User> _users;

}