/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.repository.cmis.search;

import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.repository.search.internal.LuceneRepositorySearchQueryTermBuilder;
import com.liferay.document.library.repository.search.internal.RepositorySearchQueryBuilderImpl;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.RepositoryEntry;
import com.liferay.portal.kernel.repository.search.RepositorySearchQueryBuilder;
import com.liferay.portal.kernel.repository.search.RepositorySearchQueryTermBuilder;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.service.RepositoryEntryLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.apache.chemistry.opencmis.commons.enums.CapabilityQuery;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Mika Koivisto
 * @author André de Oliveira
 */
public class BaseCmisSearchQueryBuilderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_cmisSearchQueryBuilder = new BaseCmisSearchQueryBuilder(
			createRepositoryEntryLocalService(),
			Mockito.mock(UserLocalService.class));
	}

	@Test
	public void testBooleanQuery() throws Exception {
		SearchContext searchContext = getSearchContext();

		searchContext.setKeywords("+test* -test.doc");

		String cmisQuery = buildQuery(searchContext);

		assertQueryEquals(
			"((cmis:name LIKE 'test%' AND NOT(cmis:name = 'test.doc')) OR " +
				"(cmis:createdBy LIKE 'test%' AND NOT(cmis:createdBy = " +
					"'test.doc')))",
			cmisQuery);
	}

	@Test
	public void testContainsCombinedSupportedQuery() throws Exception {
		SearchContext searchContext = getSearchContext();

		searchContext.setKeywords("test");

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setAttribute(
			"capabilityQuery", CapabilityQuery.BOTHCOMBINED.value());

		String cmisQuery = buildQuery(searchContext);

		assertQueryEquals(
			"((cmis:name = 'test' OR cmis:createdBy = 'test') OR " +
				"CONTAINS('test'))",
			cmisQuery);
	}

	@Test
	public void testContainsCombinedSupportedWildcardQuery() throws Exception {
		SearchContext searchContext = getSearchContext();

		searchContext.setKeywords("test*.jpg");

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setAttribute(
			"capabilityQuery", CapabilityQuery.BOTHCOMBINED.value());

		String cmisQuery = buildQuery(searchContext);

		assertQueryEquals(
			"((cmis:name LIKE 'test%.jpg' OR cmis:createdBy LIKE " +
				"'test%.jpg') OR CONTAINS('(test AND .jpg)'))",
			cmisQuery);
	}

	@Test
	public void testContainsOnlySupportedQuery() throws Exception {
		SearchContext searchContext = getSearchContext();

		searchContext.setKeywords("test");

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setAttribute(
			"capabilityQuery", CapabilityQuery.FULLTEXTONLY.value());

		String cmisQuery = buildQuery(searchContext);

		assertQueryEquals("CONTAINS('test')", cmisQuery);
	}

	@Test
	public void testContainsOnlySupportedQueryMultipleKeywords()
		throws Exception {

		SearchContext searchContext = getSearchContext();

		searchContext.setKeywords("test multiple");

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setAttribute(
			"capabilityQuery", CapabilityQuery.FULLTEXTONLY.value());

		String cmisQuery = buildQuery(searchContext);

		assertQueryEquals("CONTAINS('(test OR multiple)')", cmisQuery);
	}

	@Test
	public void testContainsOnlySupportedQueryWithConjunction()
		throws Exception {

		SearchContext searchContext = getSearchContext();

		searchContext.setKeywords("+test +multiple");

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setAttribute(
			"capabilityQuery", CapabilityQuery.FULLTEXTONLY.value());

		String cmisQuery = buildQuery(searchContext);

		assertQueryEquals("CONTAINS('(test multiple)')", cmisQuery);
	}

	@Test
	public void testContainsOnlySupportedQueryWithNegation() throws Exception {
		SearchContext searchContext = getSearchContext();

		searchContext.setKeywords("test -multiple");

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setAttribute(
			"capabilityQuery", CapabilityQuery.FULLTEXTONLY.value());

		String cmisQuery = buildQuery(searchContext);

		assertQueryEquals("CONTAINS('(-multiple OR test)')", cmisQuery);
	}

	@Test
	public void testContainsOnlySupportedQueryWithNegationPhrase()
		throws Exception {

		SearchContext searchContext = getSearchContext();

		searchContext.setKeywords("test -\"multiple words\"");

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setAttribute(
			"capabilityQuery", CapabilityQuery.FULLTEXTONLY.value());

		String cmisQuery = buildQuery(searchContext);

		assertQueryEquals(
			"CONTAINS('(-\\'multiple words\\' OR test)')", cmisQuery);
	}

	@Test
	public void testContainsOnlySupportedWithApostrophe() throws Exception {
		SearchContext searchContext = getSearchContext();

		searchContext.setKeywords("test's");

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setAttribute(
			"capabilityQuery", CapabilityQuery.FULLTEXTONLY.value());

		String cmisQuery = buildQuery(searchContext);

		assertQueryEquals("CONTAINS('test\\'s')", cmisQuery);
	}

	@Test
	public void testExactFileNameQuery() throws Exception {
		SearchContext searchContext = getSearchContext();

		searchContext.setKeywords("test.jpg");

		String cmisQuery = buildQuery(searchContext);

		assertQueryEquals(
			"(cmis:name = 'test.jpg' OR cmis:createdBy = 'test.jpg')",
			cmisQuery);
	}

	@Test
	public void testFolderQuery() throws Exception {
		String folderQuery = buildFolderQuery(false);

		assertQueryEquals(
			StringBundler.concat(
				"((IN_FOLDER('", _MAPPED_ID, "') AND (cmis:name = 'test' OR ",
				"cmis:createdBy = 'test')) OR CONTAINS('test'))"),
			folderQuery);
	}

	@Test
	public void testFuzzyQuery() throws Exception {
		SearchContext searchContext = getSearchContext();

		searchContext.setKeywords("test~");

		String cmisQuery = buildQuery(searchContext);

		assertQueryEquals(
			"(cmis:name LIKE 'test%' OR cmis:createdBy LIKE 'test%')",
			cmisQuery);
	}

	@Test
	public void testPhraseQuery() throws Exception {
		SearchContext searchContext = getSearchContext();

		searchContext.setKeywords("\"My test document.jpg\"");

		String cmisQuery = buildQuery(searchContext);

		assertQueryEquals(
			"(cmis:name = 'My test document.jpg' OR cmis:createdBy = 'My " +
				"test document.jpg')",
			cmisQuery);
	}

	@Test
	public void testPrefixQuery() throws Exception {
		SearchContext searchContext = getSearchContext();

		searchContext.setKeywords("test*");

		String cmisQuery = buildQuery(searchContext);

		assertQueryEquals(
			"(cmis:name LIKE 'test%' OR cmis:createdBy LIKE 'test%')",
			cmisQuery);
	}

	@Test
	public void testProximityQuery() throws Exception {
		SearchContext searchContext = getSearchContext();

		searchContext.setKeywords("\"test document\"~10");

		String cmisQuery = buildQuery(searchContext);

		assertQueryEquals(
			"(cmis:name = 'test document' OR cmis:createdBy = 'test document')",
			cmisQuery);
	}

	@Test
	public void testRangeQuery() throws Exception {
		SearchContext searchContext = getSearchContext();

		searchContext.setKeywords(
			"createDate:[20091011000000 TO 20091110235959]");

		String cmisQuery = buildQuery(searchContext);

		assertQueryEquals(
			"cmis:creationDate >= 2009-10-11T00:00:00.000Z AND " +
				"cmis:creationDate <= 2009-11-10T23:59:59.000Z",
			cmisQuery);
	}

	@Test
	public void testSubfolderQuery() throws Exception {
		String folderQuery = buildFolderQuery(true);

		assertQueryEquals(
			StringBundler.concat(
				"((IN_TREE('", _MAPPED_ID, "') AND (cmis:name = 'test' OR ",
				"cmis:createdBy = 'test')) OR CONTAINS('test'))"),
			folderQuery);
	}

	@Test
	public void testWildcardFieldQuery() throws Exception {
		SearchContext searchContext = getSearchContext();

		searchContext.setKeywords("+title:test*.jpg +userName:bar*");

		String cmisQuery = buildQuery(searchContext);

		assertQueryEquals(
			"(cmis:name LIKE 'test%.jpg' AND cmis:createdBy LIKE 'bar%')",
			cmisQuery);
	}

	@Test
	public void testWildcardQuery() throws Exception {
		SearchContext searchContext = getSearchContext();

		searchContext.setKeywords("test*.jpg");

		String cmisQuery = buildQuery(searchContext);

		assertQueryEquals(
			"(cmis:name LIKE 'test%.jpg' OR cmis:createdBy LIKE 'test%.jpg')",
			cmisQuery);
	}

	protected void assertQueryEquals(String where, String query) {
		Assert.assertEquals(_QUERY_PREFIX + where + _QUERY_POSTFIX, query);
	}

	protected String buildFolderQuery(boolean searchSubfolders)
		throws Exception {

		SearchContext searchContext = getSearchContext();

		searchContext.setFolderIds(new long[] {_DL_FOLDER_ID});
		searchContext.setKeywords("test");

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setAttribute(
			"capabilityQuery", CapabilityQuery.BOTHCOMBINED.value());
		queryConfig.setSearchSubfolders(searchSubfolders);

		return buildQuery(searchContext);
	}

	protected String buildQuery(SearchContext searchContext) throws Exception {
		return _cmisSearchQueryBuilder.buildQuery(
			searchContext, getFullQuery(searchContext));
	}

	protected RepositoryEntry createRepositoryEntry() {
		RepositoryEntry repositoryEntry = Mockito.mock(RepositoryEntry.class);

		Mockito.doReturn(
			_MAPPED_ID
		).when(
			repositoryEntry
		).getMappedId();

		return repositoryEntry;
	}

	protected RepositoryEntryLocalService createRepositoryEntryLocalService() {
		RepositoryEntryLocalService repositoryEntryLocalService = Mockito.mock(
			RepositoryEntryLocalService.class);

		Mockito.doReturn(
			createRepositoryEntry()
		).when(
			repositoryEntryLocalService
		).fetchRepositoryEntry(
			Mockito.anyLong()
		);

		return repositoryEntryLocalService;
	}

	protected RepositorySearchQueryBuilder
		createRepositorySearchQueryBuilder() {

		RepositorySearchQueryBuilderImpl repositorySearchQueryBuilderImpl =
			new RepositorySearchQueryBuilderImpl();

		ReflectionTestUtil.setFieldValue(
			repositorySearchQueryBuilderImpl, "_dlAppService",
			Mockito.mock(DLAppService.class));
		ReflectionTestUtil.setFieldValue(
			repositorySearchQueryBuilderImpl,
			"_repositorySearchQueryTermBuilder",
			createRepositorySearchQueryTermBuilder());

		return repositorySearchQueryBuilderImpl;
	}

	protected RepositorySearchQueryTermBuilder
		createRepositorySearchQueryTermBuilder() {

		return new LuceneRepositorySearchQueryTermBuilder() {
			{
				activate(null);
			}
		};
	}

	protected BooleanQuery getFullQuery(SearchContext searchContext)
		throws Exception {

		RepositorySearchQueryBuilder repositorySearchQueryBuilder =
			createRepositorySearchQueryBuilder();

		return repositorySearchQueryBuilder.getFullQuery(searchContext);
	}

	protected SearchContext getSearchContext() {
		SearchContext searchContext = new SearchContext();

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setScoreEnabled(true);

		return searchContext;
	}

	private static final long _DL_FOLDER_ID = RandomTestUtil.randomLong();

	private static final String _MAPPED_ID = "1000";

	private static final String _QUERY_POSTFIX = " ORDER BY HITS DESC";

	private static final String _QUERY_PREFIX =
		"SELECT cmis:objectId, SCORE() AS HITS FROM cmis:document WHERE ";

	private CMISSearchQueryBuilder _cmisSearchQueryBuilder;

}