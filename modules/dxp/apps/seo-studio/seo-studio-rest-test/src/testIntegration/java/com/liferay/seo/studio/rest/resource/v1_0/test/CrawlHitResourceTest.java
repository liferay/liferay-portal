/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.rest.resource.v1_0.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.document.DocumentBuilder;
import com.liferay.portal.search.document.DocumentBuilderFactory;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.document.IndexDocumentRequest;
import com.liferay.portal.search.engine.adapter.index.CreateIndexRequest;
import com.liferay.portal.search.engine.adapter.index.DeleteIndexRequest;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.seo.studio.rest.client.dto.v1_0.CrawlHit;
import com.liferay.seo.studio.rest.client.http.HttpInvoker;
import com.liferay.seo.studio.rest.client.pagination.Page;
import com.liferay.site.initializer.SiteInitializer;
import com.liferay.site.initializer.SiteInitializerRegistry;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brooke Dalton
 */
@FeatureFlag("LPD-44511")
@RunWith(Arquillian.class)
public class CrawlHitResourceTest extends BaseCrawlHitResourceTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		_originalName = PrincipalThreadLocal.getName();

		PrincipalThreadLocal.setName(TestPropsValues.getUserId());

		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId()));

		SiteInitializer siteInitializer =
			_siteInitializerRegistry.getSiteInitializer(
				"com.liferay.seo.studio.site.initializer");

		siteInitializer.initialize(TestPropsValues.getGroupId());
	}

	@AfterClass
	public static void tearDownClass() {
		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);

		PrincipalThreadLocal.setName(_originalName);

		ServiceContextThreadLocal.popServiceContext();
	}

	@After
	@Override
	public void tearDown() throws Exception {
		for (String indexName : _indexNames) {
			_searchEngineAdapter.execute(new DeleteIndexRequest(indexName));
		}

		super.tearDown();
	}

	@Override
	@Test
	public void testGetSeoStudioDomainCrawlHitsPage() throws Exception {
		super.testGetSeoStudioDomainCrawlHitsPage();

		_testGetSeoStudioDomainCrawlHitsPageNotFound();
		_testGetSeoStudioDomainCrawlHitsPageWithLastURL();
		_testGetSeoStudioDomainCrawlHitsPageWithLinks();
		_testGetSeoStudioDomainCrawlHitsPageWithoutURL();
		_testGetSeoStudioDomainCrawlHitsPageWithReservedAndExcludedParameters();
		_testGetSeoStudioDomainCrawlHitsPageWithSkippedHits();
		_testGetSeoStudioDomainCrawlHitsPageWithSpaceInURL();
		_testGetSeoStudioDomainCrawlHitsPageWithTrailingSlashInURL();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"title", "url"};
	}

	@Override
	protected CrawlHit testGetSeoStudioDomainCrawlHitsPage_addCrawlHit(
			Long seoStudioDomainId, CrawlHit crawlHit)
		throws Exception {

		_indexDocument(
			_indexNamesByDomainIdMap.get(seoStudioDomainId),
			crawlHit.getTitle(), crawlHit.getUrl());

		return crawlHit;
	}

	@Override
	protected Long
			testGetSeoStudioDomainCrawlHitsPage_getIrrelevantSeoStudioDomainId()
		throws Exception {

		return _addSEOStudioDomainObjectEntry();
	}

	@Override
	protected Long testGetSeoStudioDomainCrawlHitsPage_getSeoStudioDomainId()
		throws Exception {

		return _addSEOStudioDomainObjectEntry();
	}

	private long _addSEOStudioDomainObjectEntry() throws Exception {
		long companyId = testCompany.getCompanyId();

		User user = UserTestUtil.getAdminUser(companyId);

		if (_accountEntry == null) {
			_accountEntry = _accountEntryLocalService.addAccountEntry(
				null, user.getUserId(),
				AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
				RandomTestUtil.randomString(), null, new String[0], null, null,
				null, AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
				WorkflowConstants.STATUS_APPROVED,
				ServiceContextTestUtil.getServiceContext());
		}

		if (_seoStudioInstanceObjectEntry == null) {
			ObjectDefinition instanceObjectDefinition =
				_objectDefinitionLocalService.
					fetchObjectDefinitionByExternalReferenceCode(
						"L_SEO_STUDIO_INSTANCE", companyId);

			_seoStudioInstanceObjectEntry =
				_objectEntryLocalService.addObjectEntry(
					0L, user.getUserId(),
					instanceObjectDefinition.getObjectDefinitionId(),
					ObjectEntryFolderConstants.
						PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
					null,
					HashMapBuilder.<String, Serializable>put(
						"hostname", RandomTestUtil.randomString()
					).put(
						"name", RandomTestUtil.randomString()
					).put(
						"r_accountToSEOStudioInstances_accountEntryId",
						_accountEntry.getAccountEntryId()
					).put(
						"state", "active"
					).build(),
					ServiceContextTestUtil.getServiceContext());

			_objectEntries.add(_seoStudioInstanceObjectEntry);
		}

		ObjectDefinition domainObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_SEO_STUDIO_DOMAIN", companyId);

		ObjectEntry domainObjectEntry = _objectEntryLocalService.addObjectEntry(
			0L, user.getUserId(),
			domainObjectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"hostname",
				StringUtil.toLowerCase(RandomTestUtil.randomString())
			).put(
				"name", RandomTestUtil.randomString()
			).put(
				"r_accountToSEOStudioDomains_accountEntryId",
				_accountEntry.getAccountEntryId()
			).put(
				"r_seoStudioInstanceToSEOStudioDomains_seoStudioInstanceId",
				_seoStudioInstanceObjectEntry.getObjectEntryId()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		_objectEntries.add(domainObjectEntry);

		String indexName = "seo_studio_" + domainObjectEntry.getObjectEntryId();

		_searchEngineAdapter.execute(new CreateIndexRequest(indexName));

		_indexNames.add(indexName);
		_indexNamesByDomainIdMap.put(
			domainObjectEntry.getObjectEntryId(), indexName);

		return domainObjectEntry.getObjectEntryId();
	}

	private CrawlHit _getOnlyCrawlHit(Long seoStudioDomainId) throws Exception {
		Page<CrawlHit> page = crawlHitResource.getSeoStudioDomainCrawlHitsPage(
			seoStudioDomainId, null, null);

		List<CrawlHit> crawlHits = (List<CrawlHit>)page.getItems();

		Assert.assertEquals(crawlHits.toString(), 1, crawlHits.size());

		return crawlHits.get(0);
	}

	private void _indexDocument(
			String indexName, String title, String url, String... links)
		throws Exception {

		DocumentBuilder documentBuilder = DocumentBuilderFactory.builder();

		if (ArrayUtil.isNotEmpty(links)) {
			documentBuilder.setStrings("links", links);
		}

		documentBuilder.setString("title", title);

		if (url != null) {
			documentBuilder.setString("url", url);
		}

		Document document = documentBuilder.build();

		IndexDocumentRequest indexDocumentRequest = new IndexDocumentRequest(
			indexName, document);

		indexDocumentRequest.setRefresh(true);
		indexDocumentRequest.setType("_doc");

		_searchEngineAdapter.execute(indexDocumentRequest);
	}

	private void _testGetSeoStudioDomainCrawlHitsPageNotFound()
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			crawlHitResource.getSeoStudioDomainCrawlHitsPageHttpResponse(
				RandomTestUtil.randomLong(), null, null);

		assertHttpResponseStatusCode(404, httpResponse);
	}

	private void _testGetSeoStudioDomainCrawlHitsPageWithLastURL()
		throws Exception {

		long seoStudioDomainId = _addSEOStudioDomainObjectEntry();

		String indexName = _indexNamesByDomainIdMap.get(seoStudioDomainId);

		String url1 = "https://liferay.com/1?utm_source=newsletter";
		String url2 = "https://liferay.com/2";
		String url3 = "https://liferay.com/3";

		_indexDocument(indexName, RandomTestUtil.randomString(), url1);
		_indexDocument(indexName, RandomTestUtil.randomString(), url2);
		_indexDocument(indexName, RandomTestUtil.randomString(), url3);

		Page<CrawlHit> page = crawlHitResource.getSeoStudioDomainCrawlHitsPage(
			seoStudioDomainId, null, 5);

		List<CrawlHit> crawlHits = (List<CrawlHit>)page.getItems();

		Assert.assertEquals(crawlHits.toString(), 3, crawlHits.size());

		CrawlHit crawlHit1 = crawlHits.get(0);

		Assert.assertEquals(url1, crawlHit1.getUrl());
		Assert.assertEquals(
			"https://liferay.com/1", crawlHit1.getCanonicalUrl());

		page = crawlHitResource.getSeoStudioDomainCrawlHitsPage(
			seoStudioDomainId, crawlHit1.getUrl(), 5);

		crawlHits = (List<CrawlHit>)page.getItems();

		Assert.assertEquals(crawlHits.toString(), 2, crawlHits.size());

		CrawlHit crawlHit2 = crawlHits.get(0);
		CrawlHit crawlHit3 = crawlHits.get(1);

		Assert.assertEquals(url2, crawlHit2.getUrl());
		Assert.assertEquals(url3, crawlHit3.getUrl());
	}

	private void _testGetSeoStudioDomainCrawlHitsPageWithLinks()
		throws Exception {

		long seoStudioDomainId = _addSEOStudioDomainObjectEntry();

		String title = RandomTestUtil.randomString();

		String link1 = RandomTestUtil.randomString();
		String link2 = RandomTestUtil.randomString();

		_indexDocument(
			_indexNamesByDomainIdMap.get(seoStudioDomainId), title,
			RandomTestUtil.randomString(), link1, link2);

		CrawlHit crawlHit = _getOnlyCrawlHit(seoStudioDomainId);

		Assert.assertArrayEquals(
			new String[] {link1, link2}, crawlHit.getLinks());
		Assert.assertEquals(title, crawlHit.getTitle());
	}

	private void _testGetSeoStudioDomainCrawlHitsPageWithoutURL()
		throws Exception {

		long seoStudioDomainId = _addSEOStudioDomainObjectEntry();

		String indexName = _indexNamesByDomainIdMap.get(seoStudioDomainId);

		String url = RandomTestUtil.randomString();

		_indexDocument(indexName, RandomTestUtil.randomString(), null);
		_indexDocument(indexName, RandomTestUtil.randomString(), url);

		CrawlHit crawlHit = _getOnlyCrawlHit(seoStudioDomainId);

		Assert.assertEquals(url, crawlHit.getUrl());
	}

	private void _testGetSeoStudioDomainCrawlHitsPageWithReservedAndExcludedParameters()
		throws Exception {

		long seoStudioDomainId = _addSEOStudioDomainObjectEntry();

		String url =
			"https://liferay.com/o/page?p_l_back_url=/search&delta=20" +
				"&highlight=x&filter_category_1=2&utm_cid=7014u&id=5";

		_indexDocument(
			_indexNamesByDomainIdMap.get(seoStudioDomainId),
			RandomTestUtil.randomString(), url);

		CrawlHit crawlHit = _getOnlyCrawlHit(seoStudioDomainId);

		Assert.assertEquals(url, crawlHit.getUrl());
		Assert.assertEquals(
			"https://liferay.com/o/page?id=5", crawlHit.getCanonicalUrl());
	}

	private void _testGetSeoStudioDomainCrawlHitsPageWithSkippedHits()
		throws Exception {

		long seoStudioDomainId = _addSEOStudioDomainObjectEntry();

		String indexName = _indexNamesByDomainIdMap.get(seoStudioDomainId);

		_indexDocument(
			indexName, RandomTestUtil.randomString(), StringPool.BLANK);
		_indexDocument(
			indexName, RandomTestUtil.randomString(), StringPool.BLANK);
		_indexDocument(
			indexName, RandomTestUtil.randomString(), StringPool.BLANK);

		String url = RandomTestUtil.randomString();

		_indexDocument(indexName, RandomTestUtil.randomString(), url);

		Page<CrawlHit> page = crawlHitResource.getSeoStudioDomainCrawlHitsPage(
			seoStudioDomainId, null, 2);

		List<CrawlHit> crawlHits = (List<CrawlHit>)page.getItems();

		Assert.assertEquals(crawlHits.toString(), 1, crawlHits.size());

		CrawlHit crawlHit = crawlHits.get(0);

		Assert.assertEquals(url, crawlHit.getUrl());
	}

	private void _testGetSeoStudioDomainCrawlHitsPageWithSpaceInURL()
		throws Exception {

		long seoStudioDomainId = _addSEOStudioDomainObjectEntry();

		_indexDocument(
			_indexNamesByDomainIdMap.get(seoStudioDomainId),
			RandomTestUtil.randomString(), "https://liferay.com/de/page foo");

		CrawlHit crawlHit = _getOnlyCrawlHit(seoStudioDomainId);

		Assert.assertEquals(
			"https://liferay.com/de/page%20foo", crawlHit.getCanonicalUrl());
	}

	private void _testGetSeoStudioDomainCrawlHitsPageWithTrailingSlashInURL()
		throws Exception {

		long seoStudioDomainId = _addSEOStudioDomainObjectEntry();

		String url = "https://liferay.com/commerce/";

		_indexDocument(
			_indexNamesByDomainIdMap.get(seoStudioDomainId),
			RandomTestUtil.randomString(), url);

		CrawlHit crawlHit = _getOnlyCrawlHit(seoStudioDomainId);

		Assert.assertEquals(
			"https://liferay.com/commerce", crawlHit.getCanonicalUrl());
		Assert.assertEquals(url, crawlHit.getUrl());
	}

	private static String _originalName;
	private static PermissionChecker _originalPermissionChecker;

	@Inject
	private static SiteInitializerRegistry _siteInitializerRegistry;

	@DeleteAfterTestRun
	private AccountEntry _accountEntry;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	private final List<String> _indexNames = new ArrayList<>();
	private final Map<Long, String> _indexNamesByDomainIdMap = new HashMap<>();

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@DeleteAfterTestRun
	private final List<ObjectEntry> _objectEntries = new ArrayList<>();

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private SearchEngineAdapter _searchEngineAdapter;

	private ObjectEntry _seoStudioInstanceObjectEntry;

}