/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.indexer.post.processor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BaseIndexerPostProcessor;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerPostProcessor;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.SearchContextTestUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.users.admin.test.util.search.UserSearchFixture;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Wade Cao
 */
@RunWith(Arquillian.class)
public class IndexerPostProcessorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_userSearchFixture.setUp();

		_users = _userSearchFixture.getUsers();

		_group = _userSearchFixture.addGroup();

		_indexer = indexerRegistry.getIndexer(User.class);
	}

	@After
	public void tearDown() throws Exception {
		unregisterIndexerPostProcessor();
	}

	@Test
	public void testNeverRegister() throws Exception {
		indexUser("postAlpha");
		indexUser("postBeta");

		assertSearch("post", Arrays.asList(StringPool.BLANK, StringPool.BLANK));
	}

	@Test
	public void testRegister() throws Exception {
		registerIndexerPostProcessor();

		indexUser("postAlpha");
		indexUser("postBeta");

		assertSearch("post", Arrays.asList(_TEST_VALUE, _TEST_VALUE));
	}

	@Test
	public void testUnregister() throws Exception {
		indexUser("postAlpha");

		registerIndexerPostProcessor();

		indexUser("postBeta");

		unregisterIndexerPostProcessor();

		indexUser("postCharlie");

		assertSearch(
			"post",
			Arrays.asList(StringPool.BLANK, _TEST_VALUE, StringPool.BLANK));
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected static IndexerPostProcessor createIndexerPostProcessor() {
		return new BaseIndexerPostProcessor() {

			@Override
			public void postProcessDocument(Document document, Object object)
				throws Exception {

				document.addText(_TEST_FIELD, _TEST_VALUE);
			}

		};
	}

	protected void assertSearch(
			String keywords, List<String> expectedFieldValues)
		throws Exception {

		SearchContext searchContext = buildSearchContext(keywords);

		Hits results = _indexer.search(searchContext);

		DocumentsAssert.assertValuesIgnoreRelevance(
			(String)searchContext.getAttribute("queryString"),
			results.getDocs(), _TEST_FIELD, expectedFieldValues);
	}

	protected SearchContext buildSearchContext(String keywords)
		throws Exception {

		SearchContext searchContext = SearchContextTestUtil.getSearchContext();

		searchContext.setAttribute(Field.STATUS, WorkflowConstants.STATUS_ANY);
		searchContext.setGroupIds(new long[0]);
		searchContext.setKeywords(keywords);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setSelectedFieldNames("screenName", _TEST_FIELD);

		return searchContext;
	}

	protected void indexUser(String screenName) throws Exception {
		_indexer.reindex(
			_userSearchFixture.addUser(
				screenName, _group, RandomTestUtil.randomString()));
	}

	protected void registerIndexerPostProcessor() {
		Bundle bundle = FrameworkUtil.getBundle(IndexerPostProcessorTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			IndexerPostProcessor.class, _indexerPostProcessor,
			MapUtil.singletonDictionary(
				"indexer.class.name", _indexer.getClassName()));
	}

	protected void unregisterIndexerPostProcessor() {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();

			_serviceRegistration = null;
		}
	}

	@Inject
	protected IndexerRegistry indexerRegistry;

	private static final String _TEST_FIELD = "testField";

	private static final String _TEST_VALUE = "DO_NOT_MATCH_THIS";

	@DeleteAfterTestRun
	private Group _group;

	private Indexer<User> _indexer;
	private final IndexerPostProcessor _indexerPostProcessor =
		createIndexerPostProcessor();
	private ServiceRegistration<?> _serviceRegistration;
	private final UserSearchFixture _userSearchFixture =
		new UserSearchFixture();

	@DeleteAfterTestRun
	private List<User> _users;

}