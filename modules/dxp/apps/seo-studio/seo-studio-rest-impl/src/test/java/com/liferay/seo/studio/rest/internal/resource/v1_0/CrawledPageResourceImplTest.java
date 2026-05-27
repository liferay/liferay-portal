/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.rest.internal.resource.v1_0;

import com.liferay.object.exception.NoSuchObjectEntryException;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHitBuilder;
import com.liferay.portal.search.hits.SearchHitsBuilder;
import com.liferay.portal.search.sort.FieldSort;
import com.liferay.portal.search.sort.Sorts;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.seo.studio.rest.dto.v1_0.CrawledPage;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @author Brooke Dalton
 */
public class CrawledPageResourceImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_crawledPageResourceImpl = new CrawledPageResourceImpl();

		Company company = Mockito.mock(Company.class);

		Mockito.doReturn(
			_TEST_COMPANY_ID
		).when(
			company
		).getCompanyId();

		ReflectionTestUtil.setFieldValue(
			_crawledPageResourceImpl, "contextCompany", company);

		ReflectionTestUtil.setFieldValue(
			_crawledPageResourceImpl, "_objectEntryService",
			_objectEntryService);
		ReflectionTestUtil.setFieldValue(
			_crawledPageResourceImpl, "_searchEngineAdapter",
			_searchEngineAdapter);
		ReflectionTestUtil.setFieldValue(
			_crawledPageResourceImpl, "_sorts", _sorts);

		Mockito.doReturn(
			Mockito.mock(FieldSort.class)
		).when(
			_sorts
		).field(
			Mockito.anyString()
		);
	}

	@Test
	public void testGetCrawlHitsPageLinkFiltersInvalidEntries()
		throws Exception {

		_mockObjectEntry(_TEST_COMPANY_ID, "liferay.com");
		_setUpSearchResponse(
			_hit(
				"https://liferay.com/o/page", "Mixed Links",
				Arrays.asList(
					"https://liferay.com/a", 42, "https://liferay.com/b", true,
					null)));

		Page<CrawledPage> page = _crawledPageResourceImpl.getCrawlHitsPage(
			_DOMAIN_ID, null);

		CrawledPage crawledPage = page.getItems(
		).iterator(
		).next();

		Assert.assertArrayEquals(
			new String[] {"https://liferay.com/a", "https://liferay.com/b"},
			crawledPage.getLinks());
	}

	@Test
	public void testGetCrawlHitsPageMapsTitleAndLinks() throws Exception {
		_mockObjectEntry(_TEST_COMPANY_ID, "liferay.com");
		_setUpSearchResponse(
			_hit(
				"https://liferay.com/o/page", "Mapped Title",
				Arrays.asList(
					"https://liferay.com/link-a",
					"https://liferay.com/link-b")));

		Page<CrawledPage> page = _crawledPageResourceImpl.getCrawlHitsPage(
			_DOMAIN_ID, null);

		Collection<CrawledPage> items = page.getItems();

		Assert.assertEquals(items.toString(), 1, items.size());

		CrawledPage crawledPage = items.iterator(
		).next();

		Assert.assertEquals("Mapped Title", crawledPage.getTitle());

		Assert.assertArrayEquals(
			new String[] {
				"https://liferay.com/link-a", "https://liferay.com/link-b"
			},
			crawledPage.getLinks());
	}

	@Test
	public void testGetCrawlHitsPageResolvesIndexNameFromDomainId()
		throws Exception {

		_mockObjectEntry(_TEST_COMPANY_ID, "liferay.com");
		_setUpSearchResponse();

		_crawledPageResourceImpl.getCrawlHitsPage(_DOMAIN_ID, null);

		Assert.assertEquals(
			"seo_studio_" + _DOMAIN_ID, _captureSearchedIndexName());
	}

	@Test
	public void testGetCrawlHitsPageResolvesIndexNameIndependentOfHostname()
		throws Exception {

		long otherDomainId = 7L;

		_mockObjectEntry(otherDomainId, _TEST_COMPANY_ID, "example.com");

		_setUpSearchResponse();

		_crawledPageResourceImpl.getCrawlHitsPage(otherDomainId, null);

		Assert.assertEquals(
			"seo_studio_" + otherDomainId, _captureSearchedIndexName());
	}

	@Test
	public void testGetCrawlHitsPageSkipsHitsWithoutURL() throws Exception {
		_mockObjectEntry(_TEST_COMPANY_ID, "liferay.com");
		_setUpSearchResponse(
			_hit(null, "No URL", null),
			_hit("https://liferay.com/o/with-url", "Has URL", null));

		Page<CrawledPage> page = _crawledPageResourceImpl.getCrawlHitsPage(
			_DOMAIN_ID, null);

		Collection<CrawledPage> items = page.getItems();

		Assert.assertEquals(items.toString(), 1, items.size());
		Assert.assertEquals(
			"https://liferay.com/o/with-url",
			items.iterator(
			).next(
			).getUrl());
	}

	@Test(expected = NoSuchObjectEntryException.class)
	public void testGetCrawlHitsPageThrowsErrorWithMissingObjectEntry()
		throws Exception {

		Mockito.doThrow(
			new NoSuchObjectEntryException()
		).when(
			_objectEntryService
		).getObjectEntry(
			_DOMAIN_ID
		);

		_crawledPageResourceImpl.getCrawlHitsPage(_DOMAIN_ID, null);
	}

	@Test(expected = NoSuchObjectEntryException.class)
	public void testGetCrawlHitsPageThrowsErrorWithWrongAccount()
		throws Exception {

		_mockObjectEntry(_TEST_COMPANY_ID, "liferay.com");

		Mockito.doThrow(
			new PrincipalException()
		).when(
			_objectEntryService
		).getObjectEntry(
			_INSTANCE_ID
		);

		_crawledPageResourceImpl.getCrawlHitsPage(_DOMAIN_ID, null);
	}

	private String _captureSearchedIndexName() throws Exception {
		SearchSearchRequest searchSearchRequest = _captureSearchRequest();

		String[] indexNames = searchSearchRequest.getIndexNames();

		Assert.assertEquals(Arrays.toString(indexNames), 1, indexNames.length);

		return indexNames[0];
	}

	private SearchSearchRequest _captureSearchRequest() throws Exception {
		ArgumentCaptor<SearchSearchRequest> argumentCaptor =
			ArgumentCaptor.forClass(SearchSearchRequest.class);

		Mockito.verify(
			_searchEngineAdapter
		).execute(
			argumentCaptor.capture()
		);

		return argumentCaptor.getValue();
	}

	private SearchHit _hit(String url, String title, List<?> links) {
		SearchHitBuilder searchHitBuilder = new SearchHitBuilder();

		searchHitBuilder.addSource("links", links);
		searchHitBuilder.addSource("title", title);
		searchHitBuilder.addSource("url", url);

		return searchHitBuilder.build();
	}

	private void _mockObjectEntry(
			long domainId, long companyId, String hostname)
		throws Exception {

		ObjectEntry objectEntry = Mockito.mock(ObjectEntry.class);

		Mockito.doReturn(
			companyId
		).when(
			objectEntry
		).getCompanyId();

		Mockito.doReturn(
			HashMapBuilder.<String, Serializable>put(
				"hostname", hostname
			).put(
				"r_seoStudioInstanceToSEOStudioDomains_seoStudioInstanceId",
				_INSTANCE_ID
			).build()
		).when(
			objectEntry
		).getValues();

		Mockito.doReturn(
			objectEntry
		).when(
			_objectEntryService
		).getObjectEntry(
			domainId
		);

		Mockito.doReturn(
			Mockito.mock(ObjectEntry.class)
		).when(
			_objectEntryService
		).getObjectEntry(
			_INSTANCE_ID
		);
	}

	private void _mockObjectEntry(long companyId, String hostname)
		throws Exception {

		_mockObjectEntry(_DOMAIN_ID, companyId, hostname);
	}

	private void _setUpSearchResponse(SearchHit... searchHits)
		throws Exception {

		SearchSearchResponse searchSearchResponse = new SearchSearchResponse();

		searchSearchResponse.setSearchHits(
			new SearchHitsBuilder(
			).addSearchHits(
				Arrays.asList(searchHits)
			).build());

		Mockito.doReturn(
			searchSearchResponse
		).when(
			_searchEngineAdapter
		).execute(
			Mockito.any(SearchSearchRequest.class)
		);
	}

	private static final long _DOMAIN_ID = 42L;

	private static final long _INSTANCE_ID = 99L;

	private static final long _TEST_COMPANY_ID = 123L;

	private CrawledPageResourceImpl _crawledPageResourceImpl;
	private final ObjectEntryService _objectEntryService = Mockito.mock(
		ObjectEntryService.class);
	private final SearchEngineAdapter _searchEngineAdapter = Mockito.mock(
		SearchEngineAdapter.class);
	private final Sorts _sorts = Mockito.mock(Sorts.class);

}