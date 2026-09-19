/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.users.admin.test.util.search.GroupBlueprint;
import com.liferay.users.admin.test.util.search.GroupSearchFixture;

import java.util.HashMap;
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
public class BlogsEntryMultiLanguageSearchTest {

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

		BlogsEntryFixture blogsEntryFixture = new BlogsEntryFixture(
			blogsEntryLocalService, group);

		_blogsEntries = blogsEntryFixture.getBlogsEntries();
		_blogsEntryFixture = blogsEntryFixture;

		_defaultLocale = LocaleThreadLocal.getDefaultLocale();
		_group = group;
		_groups = groupSearchFixture.getGroups();
	}

	@After
	public void tearDown() {
		LocaleThreadLocal.setDefaultLocale(_defaultLocale);
	}

	@Test
	public void testChineseTitle() throws Exception {
		_testLocaleKeywords(LocaleUtil.CHINA, "你好");
	}

	@Test
	public void testEnglishTitle() throws Exception {
		_testLocaleKeywords(LocaleUtil.US, "title");
	}

	@Test
	public void testJapaneseTitle() throws Exception {
		_testLocaleKeywords(LocaleUtil.JAPAN, "東京");
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
		_blogsEntryFixture.updateDisplaySettings(locale);

		LocaleThreadLocal.setDefaultLocale(locale);
	}

	protected static final Class<?> MODEL_INDEXER_CLASS = BlogsEntry.class;

	@Inject
	protected BlogsEntryLocalService blogsEntryLocalService;

	@Inject(filter = "indexer.class.name=com.liferay.blogs.model.BlogsEntry")
	protected Indexer<BlogsEntry> indexer;

	@Inject
	protected SearchRequestBuilderFactory searchRequestBuilderFactory;

	@Inject
	protected Searcher searcher;

	private Map<String, String> _getMapResult(BlogsEntry blogsEntry) {
		String title = blogsEntry.getTitle();

		HashMap<String, String> map = HashMapBuilder.put(
			Field.TITLE, title
		).put(
			Field.getSortableFieldName(Field.TITLE), title
		).build();

		for (Locale locale :
				LanguageUtil.getAvailableLocales(blogsEntry.getGroupId())) {

			String languageId = LocaleUtil.toLanguageId(locale);

			map.put(
				LocalizationUtil.getLocalizedName(Field.TITLE, languageId),
				title);
		}

		return map;
	}

	private void _testLocaleKeywords(Locale locale, String keywords)
		throws Exception {

		setTestLocale(locale);

		BlogsEntry blogsEntry = _blogsEntryFixture.addEntry(keywords);

		assertFieldValues(
			Field.TITLE, locale, _getMapResult(blogsEntry), keywords);
	}

	@DeleteAfterTestRun
	private List<BlogsEntry> _blogsEntries;

	private BlogsEntryFixture _blogsEntryFixture;
	private Locale _defaultLocale;
	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups;

}