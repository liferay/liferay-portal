/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
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
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
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
 * @author Lucas Marques
 */
@RunWith(Arquillian.class)
public class BookmarksFolderIndexerIndexedFieldsTest {

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

		_bookmarksFixture = bookmarksFixture;
		_bookmarksFolders = bookmarksFixture.getBookmarksFolders();

		_group = group;

		_groups = groupSearchFixture.getGroups();

		_indexedFieldsFixture = new IndexedFieldsFixture(
			resourcePermissionLocalService, searchEngineHelper, uidFactory);

		_users = userSearchFixture.getUsers();
	}

	@Test
	public void testIndexedFields() throws Exception {
		BookmarksFolder bookmarksFolder =
			_bookmarksFixture.createBookmarksFolder();

		String searchTerm = bookmarksFolder.getUserName();

		assertFieldValues(_expectedFieldValues(bookmarksFolder), searchTerm);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected void assertFieldValues(
		Map<String, String> map, String searchTerm) {

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
					BookmarksFolder.class
				).queryString(
					searchTerm
				).build()));
	}

	protected static final Class<?> MODEL_INDEXER_CLASS = BookmarksFolder.class;

	@Inject
	protected BookmarksEntryLocalService bookmarksEntryLocalService;

	@Inject
	protected BookmarksFolderService bookmarksFolderService;

	@Inject(
		filter = "indexer.class.name=com.liferay.bookmarks.model.BookmarksFolder"
	)
	protected Indexer<BookmarksFolder> indexer;

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
			BookmarksFolder bookmarksFolder)
		throws Exception {

		Map<String, String> map = HashMapBuilder.put(
			Field.ASSET_ENTRY_ID,
			String.valueOf(_getAssetEntryId(bookmarksFolder))
		).put(
			Field.COMPANY_ID, String.valueOf(bookmarksFolder.getCompanyId())
		).put(
			Field.DESCRIPTION, bookmarksFolder.getDescription()
		).put(
			Field.ENTRY_CLASS_NAME, BookmarksFolder.class.getName()
		).put(
			Field.ENTRY_CLASS_PK, String.valueOf(bookmarksFolder.getFolderId())
		).put(
			Field.FOLDER_ID, String.valueOf(bookmarksFolder.getParentFolderId())
		).put(
			Field.GROUP_ID, String.valueOf(bookmarksFolder.getGroupId())
		).put(
			Field.SCOPE_GROUP_ID, String.valueOf(bookmarksFolder.getGroupId())
		).put(
			Field.STAGING_GROUP, String.valueOf(_group.isStagingGroup())
		).put(
			Field.STATUS, String.valueOf(bookmarksFolder.getStatus())
		).put(
			Field.TITLE, bookmarksFolder.getName()
		).put(
			Field.USER_ID, String.valueOf(bookmarksFolder.getUserId())
		).put(
			Field.USER_NAME, StringUtil.lowerCase(bookmarksFolder.getUserName())
		).put(
			"assetEntryId_sortable",
			String.valueOf(_getAssetEntryId(bookmarksFolder))
		).put(
			"groupExternalReferenceCode", _group.getExternalReferenceCode()
		).put(
			"scopeGroupExternalReferenceCode", _group.getExternalReferenceCode()
		).put(
			"statusByUserId",
			String.valueOf(bookmarksFolder.getStatusByUserId())
		).put(
			"title_sortable", StringUtil.lowerCase(bookmarksFolder.getName())
		).put(
			"userExternalReferenceCode",
			() -> {
				User user = TestPropsValues.getUser();

				return user.getExternalReferenceCode();
			}
		).put(
			"visible", "true"
		).build();

		_bookmarksFixture.populateLocalizedTitles(
			bookmarksFolder.getName(), map);
		_bookmarksFixture.populateTreePath(bookmarksFolder.getTreePath(), map);

		_indexedFieldsFixture.populatePriority("0.0", map);
		_indexedFieldsFixture.populateUID(bookmarksFolder, map);
		_indexedFieldsFixture.populateViewCount(
			BookmarksFolder.class, bookmarksFolder.getFolderId(), map);

		_populateDates(bookmarksFolder, map);
		_populateRoles(bookmarksFolder, map);

		return map;
	}

	private long _getAssetEntryId(BookmarksFolder bookmarksFolder) {
		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			BookmarksFolder.class.getName(), bookmarksFolder.getFolderId());

		if (assetEntry == null) {
			return 0;
		}

		return assetEntry.getEntryId();
	}

	private void _populateDates(
		BookmarksFolder bookmarksFolder, Map<String, String> map) {

		_indexedFieldsFixture.populateDate(
			Field.CREATE_DATE, bookmarksFolder.getCreateDate(), map);
		_indexedFieldsFixture.populateDate(
			Field.MODIFIED_DATE, bookmarksFolder.getModifiedDate(), map);
		_indexedFieldsFixture.populateDate(
			Field.PUBLISH_DATE, bookmarksFolder.getCreateDate(), map);
		_indexedFieldsFixture.populateExpirationDateWithForever(map);
	}

	private void _populateRoles(
			BookmarksFolder bookmarksFolder, Map<String, String> map)
		throws Exception {

		_indexedFieldsFixture.populateRoleIdFields(
			bookmarksFolder.getCompanyId(), BookmarksFolder.class.getName(),
			bookmarksFolder.getFolderId(), bookmarksFolder.getGroupId(), null,
			map);
	}

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

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