/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.search.test.util.IndexerFixture;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.users.admin.test.util.search.GroupBlueprint;
import com.liferay.users.admin.test.util.search.UserSearchFixture;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Vagner B.C
 */
@RunWith(Arquillian.class)
public class UserMultiLanguageSearchTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		setUpIndexerFixture();

		setUpUserSearchFixture();

		_defaultLocale = LocaleThreadLocal.getDefaultLocale();
	}

	@After
	public void tearDown() {
		LocaleThreadLocal.setDefaultLocale(_defaultLocale);
	}

	@Test
	public void testChineseValue() throws Exception {
		Locale locale = LocaleUtil.CHINA;

		setTestLocale(locale);

		String keywords = "你好";

		userSearchFixture.addUser(
			null, keywords, RandomTestUtil.randomString(), locale, group);

		Map<String, String> map = _getMapResult(keywords);

		assertFieldValues(_PREFIX, locale, map, keywords);
	}

	@Test
	public void testEnglishValue() throws Exception {
		Locale locale = LocaleUtil.US;

		setTestLocale(locale);

		String keywords = StringUtil.toLowerCase(RandomTestUtil.randomString());

		userSearchFixture.addUser(
			null, keywords, RandomTestUtil.randomString(), locale, group);

		Map<String, String> map = _getMapResult(keywords);

		assertFieldValues(_PREFIX, locale, map, keywords);
	}

	@Test
	public void testJapaneseValue() throws Exception {
		Locale locale = LocaleUtil.JAPAN;

		setTestLocale(locale);

		String keywords = "東京";

		userSearchFixture.addUser(
			null, keywords, RandomTestUtil.randomString(), locale, group);

		Map<String, String> map = _getMapResult(keywords);

		assertFieldValues(_PREFIX, locale, map, keywords);

		String localizedName = "abc_123_ユーザー管理者";

		userSearchFixture.addUser(
			null, localizedName, RandomTestUtil.randomString(), locale, group);

		map = _getMapResult(localizedName);

		assertFieldValues(_PREFIX, locale, map, "\"ユーザー管理者\"");
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected void assertFieldValues(
		String prefix, Locale locale, Map<String, String> map,
		String searchTerm) {

		Document document = indexerFixture.searchOnlyOne(searchTerm, locale);

		FieldValuesAssert.assertFieldValues(map, prefix, document, searchTerm);
	}

	protected void setTestLocale(Locale locale) throws Exception {
		group = userSearchFixture.addGroup(
			new GroupBlueprint() {
				{
					setDefaultLocale(locale);
				}
			});

		LocaleThreadLocal.setDefaultLocale(locale);
	}

	protected void setUpIndexerFixture() {
		indexerFixture = new IndexerFixture<>(User.class);
	}

	protected void setUpUserSearchFixture() throws Exception {
		userSearchFixture = new UserSearchFixture();

		userSearchFixture.setUp();

		_groups = userSearchFixture.getGroups();

		_users = userSearchFixture.getUsers();

		group = userSearchFixture.addGroup();
	}

	protected Group group;
	protected IndexerFixture<User> indexerFixture;
	protected UserSearchFixture userSearchFixture;

	private Map<String, String> _getMapResult(String keywords) {
		Map<String, String> map = HashMapBuilder.put(
			_PREFIX, keywords
		).put(
			_PREFIX + "_sortable", keywords
		).build();

		_populateLocalizedNameFieldValues(map, keywords);

		return map;
	}

	private void _populateLocalizedNameFieldValues(
		Map<String, String> map, String name) {

		for (Locale locale : LanguageUtil.getAvailableLocales()) {
			map.put(
				LocalizationUtil.getLocalizedName(
					_PREFIX, LocaleUtil.toLanguageId(locale)),
				name);
		}
	}

	private static final String _PREFIX = "firstName";

	private Locale _defaultLocale;

	@DeleteAfterTestRun
	private List<Group> _groups;

	@DeleteAfterTestRun
	private List<User> _users;

}