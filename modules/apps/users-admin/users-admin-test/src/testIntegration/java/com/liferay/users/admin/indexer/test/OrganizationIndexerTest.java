/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.indexer.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.search.test.util.AssertUtils;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Bryan Engler
 */
@RunWith(Arquillian.class)
public class OrganizationIndexerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testEmptyQuery() throws Exception {
		List<String> names = getNames(StringPool.BLANK);

		String name = RandomTestUtil.randomString();

		addOrganization(name);

		names.add(name);

		assertSearch(StringPool.BLANK, names);
	}

	@Test
	public void testName() throws Exception {
		addOrganization("Abcd");
		addOrganization("cdef");

		assertHits("Abcd", 1);
		assertHits("abcd", 1);
		assertHits("Ab", 1);
		assertHits("ab", 1);
		assertHits("bc", 0);
		assertHits("cd", 1);
		assertHits("Cd", 1);
		assertHits("cD", 1);
		assertHits("Abcde", 0);
		assertHits("bcde", 0);
		assertHits("cde", 1);
	}

	@Test
	public void testNameMultiword() throws Exception {
		addOrganization("Abcd Efgh Ijkl Mnop");
		addOrganization("qrst UVWX yz");

		assertHits("abcd", 1);
		assertHits("efgh", 1);
		assertHits("ij", 1);
		assertHits("kl", 0);

		assertHits("abcd efgh", 1);
		assertHits("abcd ef", 1);
		assertHits("abcd ijkl", 1);
		assertHits("efgh ijkl", 1);

		assertHits("uvwx", 1);
		assertHits("YZ QRST", 1);
		assertHits("YZ AAA", 1);
		assertHits("AAA uVwX", 1);
		assertHits("qrs UVWX", 1);
		assertHits("qrst UVW", 1);
		assertHits("qrs UVW", 0);

		assertHits("\"abcd\"", 1);
		assertHits("\"Abcd\"", 1);
		assertHits("\"efgh\"", 1);
		assertHits("\"eFgh\"", 1);
		assertHits("\"abcd efgh\"", 1);
		assertHits("\"abcd ef\"", 0);
		assertHits("\"abcd ijkl\"", 0);
		assertHits("\"efgh ijkl\"", 1);
		assertHits("\"efgh ij\"", 0);
		assertHits("\"gh ij\"", 0);

		assertHits("ab EFGH ij mn qr uv YZ", 2);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected Organization addOrganization(String name) throws Exception {
		long parentOrganizationId = 0;
		boolean site = false;

		Organization organization = organizationLocalService.addOrganization(
			TestPropsValues.getUserId(), parentOrganizationId, name, site);

		_organizations.add(organization);

		return organization;
	}

	protected void assertHits(String keywords, int length) throws Exception {
		SearchResponse searchResponse = search(keywords);

		AssertUtils.assertEquals(
			() -> StringBundler.concat(
				keywords, "->", searchResponse.getDocuments()),
			length, searchResponse.getTotalHits());
	}

	protected void assertSearch(String keywords, List<String> names)
		throws Exception {

		Assert.assertEquals(toString(names), toString(getNames(keywords)));
	}

	protected List<String> getNames(String keywords) throws Exception {
		SearchResponse searchResponse = search(keywords);

		return TransformUtil.transform(
			searchResponse.getDocuments(),
			document -> document.getString(Field.NAME));
	}

	protected SearchResponse search(String keywords) throws Exception {
		return searcher.search(
			searchRequestBuilderFactory.builder(
			).companyId(
				TestPropsValues.getCompanyId()
			).emptySearchEnabled(
				true
			).fields(
				Field.NAME
			).modelIndexerClasses(
				Organization.class
			).queryString(
				keywords
			).build());
	}

	protected String toString(List<String> list) {
		Collections.sort(list);

		return list.toString();
	}

	@Inject(
		filter = "indexer.class.name=com.liferay.portal.kernel.model.Organization"
	)
	protected Indexer<Organization> indexer;

	@Inject
	protected OrganizationLocalService organizationLocalService;

	@Inject
	protected SearchRequestBuilderFactory searchRequestBuilderFactory;

	@Inject
	protected Searcher searcher;

	@DeleteAfterTestRun
	private final List<Organization> _organizations = new ArrayList<>();

}