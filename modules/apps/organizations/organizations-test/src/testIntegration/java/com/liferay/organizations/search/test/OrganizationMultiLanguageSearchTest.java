/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.organizations.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.service.CountryService;
import com.liferay.portal.kernel.service.ListTypeService;
import com.liferay.portal.kernel.service.OrganizationService;
import com.liferay.portal.kernel.service.RegionService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.users.admin.test.util.search.GroupBlueprint;
import com.liferay.users.admin.test.util.search.GroupSearchFixture;

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
public class OrganizationMultiLanguageSearchTest {

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

		Group group = groupSearchFixture.addGroup(new GroupBlueprint());

		OrganizationFixture organizationFixture = new OrganizationFixture(
			countryService, language, listTypeService, organizationService,
			regionService);

		organizationFixture.setUp();

		organizationFixture.setGroup(group);

		_defaultLocale = LocaleThreadLocal.getDefaultLocale();

		_group = group;

		_groups = groupSearchFixture.getGroups();

		_organizationFixture = organizationFixture;
		_organizations = organizationFixture.getOrganizations();
	}

	@After
	public void tearDown() {
		LocaleThreadLocal.setDefaultLocale(_defaultLocale);
	}

	@Test
	public void testChineseName() throws Exception {
		Locale locale = LocaleUtil.CHINA;

		setLocale(locale);

		String keywords = "你好";

		_organizationFixture.createOrganization(keywords);

		assertFieldValues(_PREFIX, locale, _getMapResult(keywords), keywords);
	}

	@Test
	public void testEnglishName() throws Exception {
		Locale locale = LocaleUtil.US;

		setLocale(locale);

		String keywords = "liferay";

		_organizationFixture.createOrganization(keywords);

		assertFieldValues(_PREFIX, locale, _getMapResult(keywords), keywords);
	}

	@Test
	public void testJapaneseName() throws Exception {
		Locale locale = LocaleUtil.JAPAN;

		setLocale(locale);

		String keywords = "東京";

		_organizationFixture.createOrganization(keywords);

		assertFieldValues(_PREFIX, locale, _getMapResult(keywords), keywords);
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
				).locale(
					locale
				).modelIndexerClasses(
					Organization.class
				).queryString(
					searchTerm
				).build()));
	}

	protected void setLocale(Locale locale) throws Exception {
		_organizationFixture.updateDisplaySettings(locale);

		LocaleThreadLocal.setDefaultLocale(locale);
	}

	@Inject
	protected CountryService countryService;

	@Inject(
		filter = "indexer.class.name=com.liferay.portal.kernel.model.Organization"
	)
	protected Indexer<Organization> indexer;

	@Inject
	protected Language language;

	@Inject
	protected ListTypeService listTypeService;

	@Inject
	protected OrganizationService organizationService;

	@Inject
	protected RegionService regionService;

	@Inject
	protected SearchRequestBuilderFactory searchRequestBuilderFactory;

	@Inject
	protected Searcher searcher;

	private Map<String, String> _getMapResult(String keywords) {
		return HashMapBuilder.put(
			"name", keywords
		).put(
			"name_sortable", keywords
		).put(
			"nameTreePath", keywords
		).put(
			"nameTreePath_String_sortable", keywords
		).build();
	}

	private static final String _PREFIX = "name";

	private Locale _defaultLocale;
	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups;

	private OrganizationFixture _organizationFixture;

	@DeleteAfterTestRun
	private List<Organization> _organizations;

}