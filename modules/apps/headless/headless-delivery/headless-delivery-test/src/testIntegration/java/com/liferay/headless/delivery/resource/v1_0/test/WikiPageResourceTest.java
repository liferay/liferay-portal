/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.headless.delivery.client.dto.v1_0.TaxonomyCategoryBrief;
import com.liferay.headless.delivery.client.dto.v1_0.TaxonomyCategoryReference;
import com.liferay.headless.delivery.client.dto.v1_0.WikiPage;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.service.WikiNodeLocalService;

import java.util.Arrays;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Gamarra
 */
@RunWith(Arquillian.class)
public class WikiPageResourceTest extends BaseWikiPageResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setScopeGroupId(testGroup.getGroupId());

		_wikiNode = _wikiNodeLocalService.addNode(
			UserLocalServiceUtil.getGuestUserId(testGroup.getCompanyId()),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			serviceContext);

		WikiNode parentWikiNode = _wikiNodeLocalService.addNode(
			UserLocalServiceUtil.getGuestUserId(testGroup.getCompanyId()),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			serviceContext);

		_wikiPage = _addWikiPage(parentWikiNode.getNodeId());
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLPostWikiNodeWikiPage() throws Exception {
		super.testGraphQLPostWikiNodeWikiPage();
	}

	@Test
	public void testPutSiteWikiPageWithoutNodeId() throws Exception {
		WikiPage randomWikiPage = randomWikiPage();

		randomWikiPage.setWikiNodeId((Long)null);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.ERROR)) {

			assertHttpResponseStatusCode(
				400,
				wikiPageResource.
					putSiteWikiPageByExternalReferenceCodeHttpResponse(
						testGroup.getGroupId(),
						randomWikiPage.getExternalReferenceCode(),
						randomWikiPage));
		}
	}

	@Override
	@Test
	public void testPutWikiPage() throws Exception {
		super.testPutWikiPage();

		_testPutWikiPageSuccessTaxonomyCategoryBriefWithoutAssetCategory();
		_testPutWikiPageSuccessTaxonomyCategoryBriefWithAssetCategory();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"headline"};
	}

	@Override
	protected WikiPage randomWikiPage() throws Exception {
		WikiPage wikiPage = super.randomWikiPage();

		wikiPage.setEncodingFormat("html");

		return wikiPage;
	}

	@Override
	protected WikiPage
			testDeleteSiteWikiPageByExternalReferenceCode_addWikiPage()
		throws Exception {

		return _addWikiPage(testGetWikiNodeWikiPagesPage_getWikiNodeId());
	}

	@Override
	protected WikiPage testDeleteWikiPage_addWikiPage() throws Exception {
		return _addWikiPage(testGetWikiNodeWikiPagesPage_getWikiNodeId());
	}

	@Override
	protected WikiPage testGetSiteWikiPageByExternalReferenceCode_addWikiPage()
		throws Exception {

		return _addWikiPage(testGetWikiNodeWikiPagesPage_getWikiNodeId());
	}

	@Override
	protected Long testGetWikiNodeWikiPagesPage_getWikiNodeId() {
		return _wikiNode.getNodeId();
	}

	@Override
	protected WikiPage testGetWikiPage_addWikiPage() throws Exception {
		return _addWikiPage(testGetWikiNodeWikiPagesPage_getWikiNodeId());
	}

	@Override
	protected WikiPage testGetWikiPageWikiPagesPage_addWikiPage(
			Long parentWikiPageId, WikiPage wikiPage)
		throws Exception {

		return wikiPageResource.postWikiPageWikiPage(
			parentWikiPageId, randomWikiPage());
	}

	@Override
	protected Long testGetWikiPageWikiPagesPage_getParentWikiPageId()
		throws Exception {

		return _wikiPage.getId();
	}

	@Override
	protected WikiPage testGraphQLSiteWikiPage_addWikiPage() throws Exception {
		return _addWikiPage(testGetWikiNodeWikiPagesPage_getWikiNodeId());
	}

	@Override
	protected WikiPage testGraphQLWikiPage_addWikiPage() throws Exception {
		return _addWikiPage(testGetWikiNodeWikiPagesPage_getWikiNodeId());
	}

	@Override
	protected WikiPage testPutSiteWikiPageByExternalReferenceCode_addWikiPage()
		throws Exception {

		return _addWikiPage(testGetWikiNodeWikiPagesPage_getWikiNodeId());
	}

	@Override
	protected WikiPage
			testPutSiteWikiPageByExternalReferenceCode_createWikiPage()
		throws Exception {

		WikiPage wikiPage =
			super.testPutSiteWikiPageByExternalReferenceCode_createWikiPage();

		wikiPage.setWikiNodeId(_wikiNode.getNodeId());

		return wikiPage;
	}

	@Override
	protected WikiPage testPutWikiPage_addWikiPage() throws Exception {
		return _addWikiPage(testGetWikiNodeWikiPagesPage_getWikiNodeId());
	}

	@Override
	protected WikiPage testPutWikiPagePermissionsPage_addWikiPage()
		throws Exception {

		return _addWikiPage(testGetWikiNodeWikiPagesPage_getWikiNodeId());
	}

	@Override
	protected WikiPage testPutWikiPageSubscribe_addWikiPage() throws Exception {
		return _addWikiPage(testGetWikiNodeWikiPagesPage_getWikiNodeId());
	}

	@Override
	protected WikiPage testPutWikiPageUnsubscribe_addWikiPage()
		throws Exception {

		return _addWikiPage(testGetWikiNodeWikiPagesPage_getWikiNodeId());
	}

	private WikiPage _addWikiPage() throws Exception {
		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setScopeGroupId(testGroup.getGroupId());

		WikiNode parentWikiNode = _wikiNodeLocalService.addNode(
			UserLocalServiceUtil.getGuestUserId(testGroup.getCompanyId()),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			serviceContext);

		return _addWikiPage(parentWikiNode.getNodeId());
	}

	private WikiPage _addWikiPage(Long wikiNodeId) throws Exception {
		return wikiPageResource.postWikiNodeWikiPage(
			wikiNodeId, randomWikiPage());
	}

	private void _assertEqualsIgnoringOrder(
		TaxonomyCategoryBrief[] taxonomyCategoryBriefs1,
		TaxonomyCategoryBrief[] taxonomyCategoryBriefs2) {

		Assert.assertEquals(
			Arrays.toString(taxonomyCategoryBriefs2),
			taxonomyCategoryBriefs1.length, taxonomyCategoryBriefs2.length);

		for (TaxonomyCategoryBrief taxonomyCategoryBrief1 :
				taxonomyCategoryBriefs1) {

			boolean contains = false;

			for (TaxonomyCategoryBrief taxonomyCategoryBrief2 :
					taxonomyCategoryBriefs2) {

				if (taxonomyCategoryBrief1.equals(taxonomyCategoryBrief2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(contains);
		}
	}

	private void _testPutWikiPageSuccessTaxonomyCategoryBriefs(
			TaxonomyCategoryBrief[] expectedTaxonomyCategoryBriefs,
			Long assetCategoryId)
		throws Exception {

		WikiPage randomWikiPage = _addWikiPage();

		randomWikiPage.setTaxonomyCategoryIds(new Long[] {assetCategoryId});

		WikiPage wikiPage = wikiPageResource.putWikiPage(
			randomWikiPage.getId(), randomWikiPage);

		long[] wikiPageAssetCategoryIds =
			_assetCategoryLocalService.getCategoryIds(
				com.liferay.wiki.model.WikiPage.class.getName(),
				wikiPage.getId());

		Assert.assertEquals(
			Arrays.toString(wikiPageAssetCategoryIds),
			expectedTaxonomyCategoryBriefs.length,
			wikiPageAssetCategoryIds.length);

		for (long wikiPageAssetCategoryId : wikiPageAssetCategoryIds) {
			AssetCategory assetCategory =
				_assetCategoryLocalService.fetchAssetCategory(
					wikiPageAssetCategoryId);

			TaxonomyCategoryBrief[] filteredTaxonomyCategoryBriefs =
				ArrayUtil.filter(
					expectedTaxonomyCategoryBriefs,
					taxonomyCategoryBrief -> {
						TaxonomyCategoryReference taxonomyCategoryReference =
							taxonomyCategoryBrief.
								getTaxonomyCategoryReference();

						Group group = _groupLocalService.fetchGroup(
							assetCategory.getGroupId());

						if (Objects.equals(
								taxonomyCategoryReference.
									getExternalReferenceCode(),
								assetCategory.getExternalReferenceCode()) &&
							(((taxonomyCategoryReference.getSiteKey() ==
								null) &&
							  (wikiPage.getSiteId() ==
								  assetCategory.getGroupId())) ||
							 ((taxonomyCategoryReference.getSiteKey() !=
								 null) &&
							  Objects.equals(
								  taxonomyCategoryReference.getSiteKey(),
								  group.getGroupKey())))) {

							return true;
						}

						return false;
					});

			Assert.assertEquals(
				Arrays.toString(filteredTaxonomyCategoryBriefs), 1,
				filteredTaxonomyCategoryBriefs.length);
		}

		_assertEqualsIgnoringOrder(
			expectedTaxonomyCategoryBriefs,
			wikiPage.getTaxonomyCategoryBriefs());
	}

	private void _testPutWikiPageSuccessTaxonomyCategoryBriefWithAssetCategory()
		throws Exception {

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				testGroup.getCreatorUserId(), testGroup.getGroupId(),
				RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext(
					testGroup.getGroupId()));

		AssetCategory assetCategory = _assetCategoryLocalService.addCategory(
			RandomTestUtil.randomString(), testGroup.getCreatorUserId(),
			testGroup.getGroupId(), 0, RandomTestUtil.randomLocaleStringMap(),
			null, assetVocabulary.getVocabularyId(), null,
			ServiceContextTestUtil.getServiceContext(testGroup.getGroupId()));

		TaxonomyCategoryBrief[] expectedTaxonomyCategoryBriefs = {
			new TaxonomyCategoryBrief() {
				{
					taxonomyCategoryId = assetCategory.getCategoryId();
					taxonomyCategoryName = assetCategory.getName();
					taxonomyCategoryReference =
						new TaxonomyCategoryReference() {
							{
								externalReferenceCode =
									assetCategory.getExternalReferenceCode();
								siteKey = testGroup.getGroupKey();
							}
						};
				}
			}
		};

		_testPutWikiPageSuccessTaxonomyCategoryBriefs(
			expectedTaxonomyCategoryBriefs, assetCategory.getCategoryId());
	}

	private void _testPutWikiPageSuccessTaxonomyCategoryBriefWithoutAssetCategory()
		throws Exception {

		WikiPage wikiPage = _addWikiPage();

		_assertEqualsIgnoringOrder(
			new TaxonomyCategoryBrief[0], wikiPage.getTaxonomyCategoryBriefs());
	}

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@DeleteAfterTestRun
	private WikiNode _wikiNode;

	@Inject
	private WikiNodeLocalService _wikiNodeLocalService;

	private WikiPage _wikiPage;

}