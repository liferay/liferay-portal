/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.rest.resource.v1_0.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalServiceUtil;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
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
import com.liferay.portal.test.rule.Inject;
import com.liferay.seo.studio.rest.client.dto.v1_0.CrawledPage;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assume;
import org.junit.runner.RunWith;

/**
 * @author Brooke Dalton
 */
@RunWith(Arquillian.class)
public class CrawledPageResourceTest extends BaseCrawledPageResourceTestCase {

	@After
	public void tearDownDomainFixture() throws Exception {
		for (String indexName : _indexNames) {
			_searchEngineAdapter.execute(new DeleteIndexRequest(indexName));
		}
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"title", "url"};
	}

	@Override
	protected CrawledPage testGetCrawlHitsPage_addCrawledPage(
			Long seoStudioDomainId, CrawledPage crawledPage)
		throws Exception {

		String indexName = _indexNamesByDomainId.get(seoStudioDomainId);

		_indexCrawlDocument(
			indexName, crawledPage.getUrl(), crawledPage.getTitle());

		return crawledPage;
	}

	@Override
	protected Long testGetCrawlHitsPage_getIrrelevantSeoStudioDomainId()
		throws Exception {

		return _createDomain();
	}

	@Override
	protected Long testGetCrawlHitsPage_getSeoStudioDomainId()
		throws Exception {

		return _createDomain();
	}

	private Long _createDomain() throws Exception {
		long companyId = testCompany.getCompanyId();

		ObjectDefinition instanceObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_SEO_STUDIO_INSTANCE", companyId);
		ObjectDefinition domainObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_SEO_STUDIO_DOMAIN", companyId);

		Assume.assumeTrue(
			"SEO Studio site initializer has not run in this test company; " +
				"skipping integration test.",
			(instanceObjectDefinition != null) &&
			(domainObjectDefinition != null));

		long userId = UserTestUtil.getAdminUser(
			companyId
		).getUserId();

		if (_accountEntry == null) {
			_accountEntry = AccountEntryLocalServiceUtil.addAccountEntry(
				null, userId, AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
				"Test Account " + RandomTestUtil.randomString(), null,
				new String[0], null, null, null,
				AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
				WorkflowConstants.STATUS_APPROVED,
				ServiceContextTestUtil.getServiceContext());
		}

		ObjectEntry instanceObjectEntry =
			ObjectEntryLocalServiceUtil.addObjectEntry(
				0L, userId, instanceObjectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					"hostname", RandomTestUtil.randomString()
				).put(
					"name", "Test Instance"
				).put(
					"r_accountToSEOStudioInstances_accountEntryId",
					_accountEntry.getAccountEntryId()
				).put(
					"state", "active"
				).build(),
				ServiceContextTestUtil.getServiceContext());

		_createdObjectEntries.add(instanceObjectEntry);

		ObjectEntry domainObjectEntry =
			ObjectEntryLocalServiceUtil.addObjectEntry(
				0L, userId, domainObjectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					"defaultScanScope", "entireDomain"
				).put(
					"hostname",
					StringUtil.toLowerCase(RandomTestUtil.randomString())
				).put(
					"name", "Test Domain"
				).put(
					"r_accountToSEOStudioDomains_accountEntryId",
					_accountEntry.getAccountEntryId()
				).put(
					"r_seoStudioInstanceToSEOStudioDomains_seoStudioInstanceId",
					instanceObjectEntry.getObjectEntryId()
				).build(),
				ServiceContextTestUtil.getServiceContext());

		_createdObjectEntries.add(domainObjectEntry);

		String indexName = "seo_studio_" + domainObjectEntry.getObjectEntryId();

		_searchEngineAdapter.execute(new CreateIndexRequest(indexName));

		_indexNames.add(indexName);
		_indexNamesByDomainId.put(
			domainObjectEntry.getObjectEntryId(), indexName);

		return domainObjectEntry.getObjectEntryId();
	}

	private void _indexCrawlDocument(String indexName, String url, String title)
		throws Exception {

		DocumentBuilder documentBuilder = DocumentBuilderFactory.builder();

		documentBuilder.setString("url", url);
		documentBuilder.setString("title", title);

		Document document = documentBuilder.build();

		IndexDocumentRequest indexDocumentRequest = new IndexDocumentRequest(
			indexName, document);

		indexDocumentRequest.setRefresh(true);
		indexDocumentRequest.setType("_doc");

		_searchEngineAdapter.execute(indexDocumentRequest);
	}

	@DeleteAfterTestRun
	private AccountEntry _accountEntry;

	@DeleteAfterTestRun
	private final List<ObjectEntry> _createdObjectEntries = new ArrayList<>();

	private final List<String> _indexNames = new ArrayList<>();
	private final Map<Long, String> _indexNamesByDomainId = new HashMap<>();

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private SearchEngineAdapter _searchEngineAdapter;

}