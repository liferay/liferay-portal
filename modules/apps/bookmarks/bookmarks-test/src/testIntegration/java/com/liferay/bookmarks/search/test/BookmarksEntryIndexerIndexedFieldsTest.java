/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.bookmarks.model.BookmarksEntry;
import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.bookmarks.service.BookmarksEntryLocalService;
import com.liferay.bookmarks.service.BookmarksFolderService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
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
import com.liferay.users.admin.test.util.search.GroupBlueprint;
import com.liferay.users.admin.test.util.search.GroupSearchFixture;
import com.liferay.users.admin.test.util.search.UserSearchFixture;

import java.util.List;
import java.util.Map;

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
@Sync
public class BookmarksEntryIndexerIndexedFieldsTest {

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
		_bookmarksFolders = bookmarksFixture.getBookmarksFolders();
		_bookmarksFixture = bookmarksFixture;

		_group = group;
		_groups = groupSearchFixture.getGroups();
		_indexedFieldsFixture = new IndexedFieldsFixture(
			resourcePermissionLocalService, searchEngineHelper, uidFactory);
		_users = userSearchFixture.getUsers();
	}

	@Test
	public void testIndexedFields() throws Exception {
		BookmarksEntry bookmarksEntry =
			_bookmarksFixture.createBookmarksEntry();

		String searchTerm = bookmarksEntry.getUserName();

		assertFieldValues(_expectedFieldValues(bookmarksEntry), searchTerm);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected void assertFieldValues(Map<String, ?> map, String searchTerm) {
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
				).modelIndexerClasses(
					MODEL_INDEXER_CLASS
				).queryString(
					searchTerm
				).build()));
	}

	protected static final Class<?> MODEL_INDEXER_CLASS = BookmarksEntry.class;

	@Inject
	protected BookmarksEntryLocalService bookmarksEntryLocalService;

	@Inject
	protected BookmarksFolderService bookmarksFolderService;

	@Inject(
		filter = "indexer.class.name=com.liferay.bookmarks.model.BookmarksEntry"
	)
	protected Indexer<BookmarksEntry> indexer;

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

	private Map<String, String> _expectedFieldValues(
			BookmarksEntry bookmarksEntry)
		throws Exception {

		User user = _users.get(0);

		Map<String, String> map = HashMapBuilder.put(
			Field.ASSET_ENTRY_ID,
			String.valueOf(_getAssetEntryId(bookmarksEntry))
		).put(
			Field.COMPANY_ID, String.valueOf(bookmarksEntry.getCompanyId())
		).put(
			Field.DESCRIPTION, bookmarksEntry.getDescription()
		).put(
			Field.ENTRY_CLASS_NAME, BookmarksEntry.class.getName()
		).put(
			Field.ENTRY_CLASS_PK, String.valueOf(bookmarksEntry.getEntryId())
		).put(
			Field.FOLDER_ID, String.valueOf(bookmarksEntry.getFolderId())
		).put(
			Field.GROUP_ID, String.valueOf(bookmarksEntry.getGroupId())
		).put(
			Field.SCOPE_GROUP_ID, String.valueOf(bookmarksEntry.getGroupId())
		).put(
			Field.STAGING_GROUP, String.valueOf(_group.isStagingGroup())
		).put(
			Field.STATUS, String.valueOf(bookmarksEntry.getStatus())
		).put(
			Field.TITLE, bookmarksEntry.getName()
		).put(
			Field.URL, bookmarksEntry.getUrl()
		).put(
			Field.USER_ID, String.valueOf(bookmarksEntry.getUserId())
		).put(
			Field.USER_NAME, StringUtil.lowerCase(bookmarksEntry.getUserName())
		).put(
			"assetEntryId_sortable",
			String.valueOf(_getAssetEntryId(bookmarksEntry))
		).put(
			"groupExternalReferenceCode", _group.getExternalReferenceCode()
		).put(
			"scopeGroupExternalReferenceCode", _group.getExternalReferenceCode()
		).put(
			"statusByUserExternalReferenceCode", user.getExternalReferenceCode()
		).put(
			"statusByUserId", String.valueOf(bookmarksEntry.getStatusByUserId())
		).put(
			"title_sortable", StringUtil.lowerCase(bookmarksEntry.getName())
		).put(
			"userExternalReferenceCode", user.getExternalReferenceCode()
		).put(
			"visible", "true"
		).build();

		_bookmarksFixture.populateLocalizedTitles(
			bookmarksEntry.getName(), map);
		_bookmarksFixture.populateTreePath(bookmarksEntry.getTreePath(), map);

		_indexedFieldsFixture.populatePriority("0.0", map);
		_indexedFieldsFixture.populateUID(bookmarksEntry, map);
		_indexedFieldsFixture.populateViewCount(
			BookmarksEntry.class, bookmarksEntry.getEntryId(), map);

		_populateDates(bookmarksEntry, map);
		_populateRoles(bookmarksEntry, map);

		return map;
	}

	private long _getAssetEntryId(BookmarksEntry bookmarksEntry) {
		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			BookmarksEntry.class.getName(), bookmarksEntry.getEntryId());

		if (assetEntry == null) {
			return 0;
		}

		return assetEntry.getEntryId();
	}

	private void _populateDates(
		BookmarksEntry bookmarksEntry, Map<String, String> map) {

		_indexedFieldsFixture.populateDate(
			Field.CREATE_DATE, bookmarksEntry.getCreateDate(), map);
		_indexedFieldsFixture.populateDate(
			Field.MODIFIED_DATE, bookmarksEntry.getModifiedDate(), map);
		_indexedFieldsFixture.populateDate(
			Field.PUBLISH_DATE, bookmarksEntry.getCreateDate(), map);
		_indexedFieldsFixture.populateExpirationDateWithForever(map);
	}

	private void _populateRoles(
			BookmarksEntry bookmarksEntry, Map<String, String> map)
		throws Exception {

		_indexedFieldsFixture.populateRoleIdFields(
			bookmarksEntry.getCompanyId(), BookmarksEntry.class.getName(),
			bookmarksEntry.getEntryId(), bookmarksEntry.getGroupId(), null,
			map);
	}

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@DeleteAfterTestRun
	private List<BookmarksEntry> _bookmarksEntries;

	private BookmarksFixture _bookmarksFixture;

	@DeleteAfterTestRun
	private List<BookmarksFolder> _bookmarksFolders;

	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups;

	private IndexedFieldsFixture _indexedFieldsFixture;

	@DeleteAfterTestRun
	private List<User> _users;

}