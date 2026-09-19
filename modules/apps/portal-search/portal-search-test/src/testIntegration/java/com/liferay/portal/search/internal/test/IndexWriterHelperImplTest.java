/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.blogs.test.util.BlogsTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.SearchEngine;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.search.CountSearchRequest;
import com.liferay.portal.search.engine.adapter.search.CountSearchResponse;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.QueriesUtil;
import com.liferay.portal.search.query.TermQuery;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Bryan Engler
 */
@RunWith(Arquillian.class)
public class IndexWriterHelperImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_reindex(null);

		Thread.sleep(10000);
	}

	@Test
	public void testReindexWithClassName() throws Exception {
		Map<Long, Long> originalCounts = new HashMap<>();

		_addBlogEntry();

		_populateOriginalCounts(originalCounts, _CLASS_NAME_BLOGS_ENTRY, false);

		_reindex(_CLASS_NAME_BLOGS_ENTRY);

		_assertReindexedCounts(originalCounts, _CLASS_NAME_BLOGS_ENTRY);
	}

	@Test
	public void testReindexWithSystemIndexerClassName() throws Exception {
		Map<Long, Long> originalConfigurationModelCounts = new HashMap<>();

		_populateOriginalCounts(
			originalConfigurationModelCounts, _CLASS_NAME_CONFIGURATION_MODEL,
			true);

		_reindex(_CLASS_NAME_CONFIGURATION_MODEL_INDEXER);

		_assertReindexedCounts(
			originalConfigurationModelCounts, _CLASS_NAME_CONFIGURATION_MODEL);
	}

	@Test
	public void testReindexWithoutClassName() throws Exception {
		Map<Long, Long> originalBlogEntryCounts = new HashMap<>();
		Map<Long, Long> originalConfigurationModelCounts = new HashMap<>();

		_addBlogEntry();

		_populateOriginalCounts(
			originalBlogEntryCounts, _CLASS_NAME_BLOGS_ENTRY, false);

		_populateOriginalCounts(
			originalConfigurationModelCounts, _CLASS_NAME_CONFIGURATION_MODEL,
			true);

		_reindex(null);

		_assertReindexedCounts(
			originalBlogEntryCounts, _CLASS_NAME_BLOGS_ENTRY);

		_assertReindexedCounts(
			originalConfigurationModelCounts, _CLASS_NAME_CONFIGURATION_MODEL);
	}

	private void _addBlogEntry() throws Exception {
		BlogsTestUtil.addEntryWithWorkflow(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(), false,
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId()));
	}

	private void _assertCountEqualsZero(
		String className, long companyId, long count) {

		Assert.assertTrue(
			StringBundler.concat(
				className, " companyId=", companyId, " count=", count),
			count == 0);
	}

	private void _assertCountGreaterThanZero(
		String className, long companyId, long count) {

		Assert.assertTrue(
			StringBundler.concat(
				className, " companyId=", companyId, " count=", count),
			count > 0);
	}

	private void _assertReindexedCounts(
		Map<Long, Long> originalCounts, String className) {

		for (long companyId : _getCompanyIds()) {
			Long newCount = Long.valueOf(_getCount(companyId, className));
			Long originalCount = originalCounts.get(companyId);

			Assert.assertEquals(
				className + " companyId=" + companyId, originalCount, newCount);
		}
	}

	private long[] _getCompanyIds() {
		long[] companyIds = PortalInstancePool.getCompanyIds();

		if (!ArrayUtil.contains(companyIds, CompanyConstants.SYSTEM)) {
			companyIds = ArrayUtil.append(
				new long[] {CompanyConstants.SYSTEM}, companyIds);
		}

		return companyIds;
	}

	private long _getCount(long companyId, String className) {
		CountSearchRequest countSearchRequest = new CountSearchRequest();

		if (_isSearchEngine("Solr")) {
			countSearchRequest.setIndexNames("liferay");
		}
		else {
			countSearchRequest.setIndexNames("liferay-" + companyId);
		}

		TermQuery classNameTermQuery = QueriesUtil.term(
			Field.ENTRY_CLASS_NAME, className);
		TermQuery companyTermQuery = QueriesUtil.term(
			Field.COMPANY_ID, companyId);

		BooleanQuery booleanQuery = QueriesUtil.booleanQuery();

		booleanQuery.addMustQueryClauses(classNameTermQuery, companyTermQuery);

		countSearchRequest.setQuery(booleanQuery);

		CountSearchResponse countSearchResponse = _searchEngineAdapter.execute(
			countSearchRequest);

		return countSearchResponse.getCount();
	}

	private boolean _isSearchEngine(String vendor) {
		SearchEngine searchEngine = _searchEngineHelper.getSearchEngine();

		return Objects.equals(searchEngine.getVendor(), vendor);
	}

	private void _populateOriginalCounts(
		Map<Long, Long> originalCounts, String className,
		boolean systemIndexer) {

		for (long companyId : _getCompanyIds()) {
			long count = _getCount(companyId, className);

			if (systemIndexer) {
				if (companyId == CompanyConstants.SYSTEM) {
					_assertCountGreaterThanZero(className, companyId, count);
				}
				else {
					_assertCountEqualsZero(className, companyId, count);
				}
			}
			else {
				if (companyId == CompanyConstants.SYSTEM) {
					_assertCountEqualsZero(className, companyId, count);
				}
				else {
					_assertCountGreaterThanZero(className, companyId, count);
				}
			}

			originalCounts.put(companyId, count);
		}
	}

	private void _reindex(String className) throws Exception {
		String jobName = "reindex-".concat(PortalUUIDUtil.generate());

		_indexWriterHelper.reindex(
			UserConstants.USER_ID_DEFAULT, jobName, _getCompanyIds(), className,
			null);
	}

	private static final String _CLASS_NAME_BLOGS_ENTRY =
		"com.liferay.blogs.model.BlogsEntry";

	private static final String _CLASS_NAME_CONFIGURATION_MODEL =
		"com.liferay.configuration.admin.web.internal.model.ConfigurationModel";

	private static final String _CLASS_NAME_CONFIGURATION_MODEL_INDEXER =
		"com.liferay.configuration.admin.web.internal.search." +
			"ConfigurationModelIndexer";

	@Inject
	private BlogsEntryLocalService _blogsEntryLocalService;

	@Inject
	private IndexWriterHelper _indexWriterHelper;

	@Inject
	private SearchEngineAdapter _searchEngineAdapter;

	@Inject
	private SearchEngineHelper _searchEngineHelper;

}