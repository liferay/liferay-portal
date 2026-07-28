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
import com.liferay.seo.studio.rest.client.dto.v1_0.PageContent;
import com.liferay.seo.studio.rest.client.http.HttpInvoker;
import com.liferay.site.initializer.SiteInitializer;
import com.liferay.site.initializer.SiteInitializerRegistry;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

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
public class PageContentResourceTest extends BasePageContentResourceTestCase {

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
	public void testGetPageContent() throws Exception {
		_testGetPageContentNotFoundWhenDomainNotRegistered();
		_testGetPageContentNotFoundWhenIndexMissing();
		_testGetPageContentNotFoundWhenURLNotIndexed();
		_testGetPageContentWithFullHTML();
	}

	private ObjectEntry _addSEOStudioDomainObjectEntry(String hostname)
		throws Exception {

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

		ObjectEntry seoStudioDomainObjectEntry =
			_objectEntryLocalService.addObjectEntry(
				0L, user.getUserId(),
				domainObjectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					"hostname", hostname
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

		_objectEntries.add(seoStudioDomainObjectEntry);

		return seoStudioDomainObjectEntry;
	}

	private String _createIndex(long seoStudioDomainId) throws Exception {
		String indexName = "seo_studio_" + seoStudioDomainId;

		_searchEngineAdapter.execute(new CreateIndexRequest(indexName));

		_indexNames.add(indexName);

		return indexName;
	}

	private void _indexDocument(String indexName, String url, String fullHTML)
		throws Exception {

		DocumentBuilder documentBuilder = DocumentBuilderFactory.builder();

		documentBuilder.setString("full_html", fullHTML);
		documentBuilder.setString("url", url);

		Document document = documentBuilder.build();

		IndexDocumentRequest indexDocumentRequest = new IndexDocumentRequest(
			indexName, document);

		indexDocumentRequest.setRefresh(true);
		indexDocumentRequest.setType("_doc");

		_searchEngineAdapter.execute(indexDocumentRequest);
	}

	private String _randomHostname() {
		return StringUtil.toLowerCase(RandomTestUtil.randomString());
	}

	private void _testGetPageContentNotFoundWhenDomainNotRegistered()
		throws Exception {

		String pageURL = "https://" + _randomHostname() + "/page";

		HttpInvoker.HttpResponse httpResponse =
			pageContentResource.getPageContentHttpResponse(pageURL);

		assertHttpResponseStatusCode(404, httpResponse);
	}

	private void _testGetPageContentNotFoundWhenIndexMissing()
		throws Exception {

		String hostname = _randomHostname();

		_addSEOStudioDomainObjectEntry(hostname);

		String pageURL = "https://" + hostname + "/page";

		HttpInvoker.HttpResponse httpResponse =
			pageContentResource.getPageContentHttpResponse(pageURL);

		assertHttpResponseStatusCode(404, httpResponse);
	}

	private void _testGetPageContentNotFoundWhenURLNotIndexed()
		throws Exception {

		String hostname = _randomHostname();

		ObjectEntry seoStudioDomainObjectEntry = _addSEOStudioDomainObjectEntry(
			hostname);

		String indexName = _createIndex(
			seoStudioDomainObjectEntry.getObjectEntryId());

		_indexDocument(
			indexName, "https://" + hostname + "/other-page",
			RandomTestUtil.randomString());

		String pageURL = "https://" + hostname + "/page";

		HttpInvoker.HttpResponse httpResponse =
			pageContentResource.getPageContentHttpResponse(pageURL);

		assertHttpResponseStatusCode(404, httpResponse);
	}

	private void _testGetPageContentWithFullHTML() throws Exception {
		String hostname = _randomHostname();

		ObjectEntry seoStudioDomainObjectEntry = _addSEOStudioDomainObjectEntry(
			hostname);

		String indexName = _createIndex(
			seoStudioDomainObjectEntry.getObjectEntryId());

		String pageURL = "https://" + hostname + "/page";
		String fullHTML = RandomTestUtil.randomString();

		_indexDocument(indexName, pageURL, fullHTML);

		PageContent pageContent = pageContentResource.getPageContent(pageURL);

		Assert.assertEquals(fullHTML, pageContent.getContent());
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