/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.bookmarks.model.BookmarksEntry;
import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.bookmarks.service.BookmarksEntryLocalService;
import com.liferay.bookmarks.service.BookmarksFolderService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.users.admin.test.util.search.GroupBlueprint;
import com.liferay.users.admin.test.util.search.GroupSearchFixture;
import com.liferay.users.admin.test.util.search.UserSearchFixture;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Vagner B.C
 */
@RunWith(Arquillian.class)
public class BookmarksEntryMultiLanguageSearchTest {

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

		_defaultLocale = LocaleThreadLocal.getDefaultLocale();
		_group = group;
		_groups = groupSearchFixture.getGroups();
		_users = userSearchFixture.getUsers();
	}

	@After
	public void tearDown() {
		LocaleThreadLocal.setDefaultLocale(_defaultLocale);
	}

	@Test
	public void testChineseTitle() throws Exception {
		Locale locale = LocaleUtil.CHINA;

		setTestLocale(locale);

		String keyWords = "你好";

		_bookmarksFixture.createBookmarksEntry(keyWords);

		assertFieldValues(
			_PREFIX, locale,
			HashMapBuilder.put(
				_PREFIX, keyWords
			).put(
				_PREFIX + "_sortable", keyWords
			).build(),
			keyWords);
	}

	@Test
	public void testEnglishTitle() throws Exception {
		Locale locale = LocaleUtil.US;

		setTestLocale(locale);

		String keyWords = StringUtil.toLowerCase(RandomTestUtil.randomString());

		_bookmarksFixture.createBookmarksEntry(keyWords);

		assertFieldValues(
			_PREFIX, locale,
			HashMapBuilder.put(
				_PREFIX, keyWords
			).put(
				_PREFIX + "_sortable", keyWords
			).build(),
			keyWords);
	}

	@Test
	public void testJapaneseTitle() throws Exception {
		Locale locale = LocaleUtil.JAPAN;

		setTestLocale(locale);

		String keyWords = "東京";

		_bookmarksFixture.createBookmarksEntry(keyWords);

		assertFieldValues(
			_PREFIX, locale,
			HashMapBuilder.put(
				_PREFIX, keyWords
			).put(
				_PREFIX + "_sortable", keyWords
			).build(),
			keyWords);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected void assertFieldValues(
		String prefix, Locale locale, Map<String, String> map,
		String searchTerm) {

		FieldValuesAssert.assertFieldValues(
			map, name -> name.startsWith(prefix),
			searcher.search(
				searchRequestBuilderFactory.builder(
				).companyId(
					_group.getCompanyId()
				).fields(
					StringPool.STAR
				).groupIds(
					_group.getGroupId()
				).locale(
					locale
				).modelIndexerClasses(
					MODEL_INDEXER_CLASS
				).queryString(
					searchTerm
				).build()));
	}

	protected void setTestLocale(Locale locale) throws Exception {
		_bookmarksFixture.updateDisplaySettings(locale);

		LocaleThreadLocal.setDefaultLocale(locale);
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
	protected SearchRequestBuilderFactory searchRequestBuilderFactory;

	@Inject
	protected Searcher searcher;

	@Inject
	protected UserLocalService userLocalService;

	private static final String _PREFIX = "title";

	@DeleteAfterTestRun
	private List<BookmarksEntry> _bookmarksEntries;

	private BookmarksFixture _bookmarksFixture;

	@DeleteAfterTestRun
	private List<BookmarksFolder> _bookmarksFolders;

	private Locale _defaultLocale;
	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups;

	@DeleteAfterTestRun
	private List<User> _users;

}