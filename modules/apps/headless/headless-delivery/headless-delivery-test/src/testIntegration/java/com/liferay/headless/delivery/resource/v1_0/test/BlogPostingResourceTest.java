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
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.headless.delivery.client.dto.v1_0.BlogPosting;
import com.liferay.headless.delivery.client.dto.v1_0.TaxonomyCategoryBrief;
import com.liferay.headless.delivery.client.dto.v1_0.TaxonomyCategoryReference;
import com.liferay.headless.delivery.client.pagination.Page;
import com.liferay.headless.delivery.client.pagination.Pagination;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.Inject;

import java.util.Arrays;
import java.util.Objects;

import org.hamcrest.CoreMatchers;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Gamarra
 */
@RunWith(Arquillian.class)
public class BlogPostingResourceTest extends BaseBlogPostingResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_originalName = PrincipalThreadLocal.getName();

		PrincipalThreadLocal.setName(TestPropsValues.getUserId());
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		PrincipalThreadLocal.setName(_originalName);
	}

	@Override
	@Test
	public void testDeleteBlogPostingMyRating() throws Exception {
		super.testDeleteBlogPostingMyRating();

		BlogPosting blogPosting =
			testDeleteBlogPostingMyRating_addBlogPosting();

		assertHttpResponseStatusCode(
			204,
			blogPostingResource.deleteBlogPostingMyRatingHttpResponse(
				blogPosting.getId()));
		assertHttpResponseStatusCode(
			404,
			blogPostingResource.deleteBlogPostingMyRatingHttpResponse(
				blogPosting.getId()));

		BlogPosting irrelevantBlogPosting = randomIrrelevantBlogPosting();

		assertHttpResponseStatusCode(
			404,
			blogPostingResource.deleteBlogPostingMyRatingHttpResponse(
				irrelevantBlogPosting.getId()));
	}

	@Override
	@Test
	public void testGetBlogPostingRenderedContentByDisplayPageDisplayPageKey()
		throws Exception {
	}

	@Override
	@Test
	public void testGetSiteBlogPostingsPage() throws Exception {
		super.testGetSiteBlogPostingsPage();

		BlogPosting blogPosting = randomBlogPosting();

		blogPosting.setKeywords(new String[] {"TaG"});

		blogPosting = testGetSiteBlogPostingsPage_addBlogPosting(
			testGroup.getGroupId(), blogPosting);

		Page<BlogPosting> siteBlogPostingsPage =
			blogPostingResource.getSiteBlogPostingsPage(
				testGroup.getGroupId(), null, null,
				"keywords/any(k:k eq 'tag')", Pagination.of(1, 10), null);

		Assert.assertThat(
			siteBlogPostingsPage.getItems(), CoreMatchers.hasItem(blogPosting));
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteBlogPostingMyRating() throws Exception {
		super.testGraphQLDeleteBlogPostingMyRating();
	}

	@Override
	@Test
	public void testPutBlogPosting() throws Exception {
		super.testPutBlogPosting();

		_testPutBlogPostingSuccessTaxonomyCategoryBriefNonexisting();
		_testPutBlogPostingSuccessTaxonomyCategoryBriefCategorySiteKeyNull();
		_testPutBlogPostingSuccessTaxonomyCategoryBriefCategorySiteKeyNonnull();
		_testPutBlogPostingSuccessTaxonomyCategoryBriefCategoryNamei18n();
		_testPutBlogPostingSuccessTaxonomyCategoryBriefNonsiteBlog();
	}

	@Override
	@Test
	public void testPutSiteBlogPostingSubscribe() throws Exception {
		BlogPosting blogPosting =
			testPutSiteBlogPostingSubscribe_addBlogPosting();

		assertHttpResponseStatusCode(
			204,
			blogPostingResource.putSiteBlogPostingSubscribeHttpResponse(
				blogPosting.getSiteId()));
	}

	@Override
	@Test
	public void testPutSiteBlogPostingUnsubscribe() throws Exception {
		BlogPosting blogPosting =
			testPutSiteBlogPostingUnsubscribe_addBlogPosting();

		assertHttpResponseStatusCode(
			204,
			blogPostingResource.putSiteBlogPostingUnsubscribeHttpResponse(
				blogPosting.getSiteId()));
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"articleBody", "description", "headline"};
	}

	@Override
	protected String[] getIgnoredEntityFieldNames() {
		return new String[] {"creatorId"};
	}

	@Override
	protected BlogPosting testDeleteBlogPostingMyRating_addBlogPosting()
		throws Exception {

		BlogPosting blogPosting =
			super.testDeleteBlogPostingMyRating_addBlogPosting();

		blogPostingResource.putBlogPostingMyRating(
			blogPosting.getId(), randomRating());

		return blogPosting;
	}

	private AssetCategory _addAssetCategory(Group group) throws Exception {
		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				group.getCreatorUserId(), group.getGroupId(),
				RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		return _assetCategoryLocalService.addCategory(
			RandomTestUtil.randomString(), group.getCreatorUserId(),
			group.getGroupId(), 0, RandomTestUtil.randomLocaleStringMap(), null,
			assetVocabulary.getVocabularyId(), null,
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));
	}

	private void _assertContainedIgnoringOrder(
		TaxonomyCategoryBrief[] taxonomyCategoryBriefs1,
		TaxonomyCategoryBrief[] taxonomyCategoryBriefs2) {

		Assert.assertEquals(
			Arrays.toString(taxonomyCategoryBriefs2),
			taxonomyCategoryBriefs1.length, taxonomyCategoryBriefs2.length);

		for (TaxonomyCategoryBrief taxonomyCategoryBrief1 :
				taxonomyCategoryBriefs1) {

			boolean contains = _isTaxonomyCategoryBriefsContained(
				taxonomyCategoryBriefs2, taxonomyCategoryBrief1);

			Assert.assertTrue(
				Arrays.toString(taxonomyCategoryBriefs2) +
					" does not contain " + taxonomyCategoryBrief1,
				contains);
		}
	}

	private void _assertNotContainedIgnoringOrder(
		TaxonomyCategoryBrief[] taxonomyCategoryBriefs1,
		TaxonomyCategoryBrief[] taxonomyCategoryBriefs2) {

		Assert.assertEquals(
			Arrays.toString(taxonomyCategoryBriefs2),
			taxonomyCategoryBriefs1.length, taxonomyCategoryBriefs2.length);

		for (TaxonomyCategoryBrief taxonomyCategoryBrief1 :
				taxonomyCategoryBriefs1) {

			boolean contains = _isTaxonomyCategoryBriefsContained(
				taxonomyCategoryBriefs2, taxonomyCategoryBrief1);

			Assert.assertFalse(
				Arrays.toString(taxonomyCategoryBriefs2) + " contains " +
					taxonomyCategoryBrief1,
				contains);
		}
	}

	private boolean _isTaxonomyCategoryBriefsContained(
		TaxonomyCategoryBrief[] taxonomyCategoryBriefs2,
		TaxonomyCategoryBrief taxonomyCategoryBrief1) {

		boolean contains = false;

		for (TaxonomyCategoryBrief taxonomyCategoryBrief2 :
				taxonomyCategoryBriefs2) {

			if (taxonomyCategoryBrief1.equals(taxonomyCategoryBrief2)) {
				contains = true;

				break;
			}
		}

		return contains;
	}

	private BlogPosting _testGetSiteBlogPostingsPage_addBlogPosting(
			Long[] inputTaxonomyCategoryIds)
		throws Exception {

		BlogPosting randomBlogPosting = randomBlogPosting();

		randomBlogPosting.setTaxonomyCategoryIds(inputTaxonomyCategoryIds);

		return testGetSiteBlogPostingsPage_addBlogPosting(
			testGroup.getGroupId(), randomBlogPosting);
	}

	private void _testPutBlogPostingSuccessTaxonomyCategoryBriefCategoryNamei18n()
		throws Exception {

		AssetCategory assetCategory = _addAssetCategory(testGroup);

		TaxonomyCategoryBrief[] expectedTaxonomyCategoryBriefs = {
			new TaxonomyCategoryBrief() {
				{
					taxonomyCategoryId = assetCategory.getCategoryId();
					taxonomyCategoryName = assetCategory.getName();
					taxonomyCategoryName_i18n = HashMapBuilder.put(
						"en-US", assetCategory.getName()
					).build();
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

		BlogPosting blogPosting = _testGetSiteBlogPostingsPage_addBlogPosting(
			new Long[] {assetCategory.getCategoryId()});

		long[] assetCategoryIds = _assetCategoryLocalService.getCategoryIds(
			BlogsEntry.class.getName(), blogPosting.getId());

		Assert.assertEquals(
			Arrays.toString(assetCategoryIds),
			expectedTaxonomyCategoryBriefs.length, assetCategoryIds.length);

		_testPutBlogPostingSuccessTaxonomyCategoryBriefs(
			expectedTaxonomyCategoryBriefs, assetCategoryIds, blogPosting);

		_assertNotContainedIgnoringOrder(
			expectedTaxonomyCategoryBriefs,
			blogPosting.getTaxonomyCategoryBriefs());
	}

	private void _testPutBlogPostingSuccessTaxonomyCategoryBriefCategorySiteKeyNonnull()
		throws Exception {

		AssetCategory assetCategory = _addAssetCategory(testGroup);

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

		BlogPosting blogPosting = _testGetSiteBlogPostingsPage_addBlogPosting(
			new Long[] {assetCategory.getCategoryId()});

		long[] assetCategoryIds = _assetCategoryLocalService.getCategoryIds(
			BlogsEntry.class.getName(), blogPosting.getId());

		Assert.assertEquals(
			Arrays.toString(assetCategoryIds),
			expectedTaxonomyCategoryBriefs.length, assetCategoryIds.length);

		_testPutBlogPostingSuccessTaxonomyCategoryBriefs(
			expectedTaxonomyCategoryBriefs, assetCategoryIds, blogPosting);

		_assertContainedIgnoringOrder(
			expectedTaxonomyCategoryBriefs,
			blogPosting.getTaxonomyCategoryBriefs());
	}

	private void _testPutBlogPostingSuccessTaxonomyCategoryBriefCategorySiteKeyNull()
		throws Exception {

		AssetCategory assetCategory = _addAssetCategory(testGroup);

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
							}
						};
				}
			}
		};

		BlogPosting blogPosting = _testGetSiteBlogPostingsPage_addBlogPosting(
			new Long[] {assetCategory.getCategoryId()});

		long[] assetCategoryIds = _assetCategoryLocalService.getCategoryIds(
			BlogsEntry.class.getName(), blogPosting.getId());

		Assert.assertEquals(
			Arrays.toString(assetCategoryIds),
			expectedTaxonomyCategoryBriefs.length, assetCategoryIds.length);

		_testPutBlogPostingSuccessTaxonomyCategoryBriefs(
			expectedTaxonomyCategoryBriefs, assetCategoryIds, blogPosting);

		_assertNotContainedIgnoringOrder(
			expectedTaxonomyCategoryBriefs,
			blogPosting.getTaxonomyCategoryBriefs());
	}

	private void _testPutBlogPostingSuccessTaxonomyCategoryBriefNonexisting()
		throws Exception {

		TaxonomyCategoryBrief[] expectedTaxonomyCategoryBriefs =
			new TaxonomyCategoryBrief[0];

		BlogPosting blogPosting = _testGetSiteBlogPostingsPage_addBlogPosting(
			new Long[] {RandomTestUtil.randomLong()});

		long[] assetCategoryIds = _assetCategoryLocalService.getCategoryIds(
			BlogsEntry.class.getName(), blogPosting.getId());

		Assert.assertEquals(
			Arrays.toString(assetCategoryIds),
			expectedTaxonomyCategoryBriefs.length, assetCategoryIds.length);

		_testPutBlogPostingSuccessTaxonomyCategoryBriefs(
			expectedTaxonomyCategoryBriefs, assetCategoryIds, blogPosting);

		_assertContainedIgnoringOrder(
			expectedTaxonomyCategoryBriefs,
			blogPosting.getTaxonomyCategoryBriefs());
	}

	private void _testPutBlogPostingSuccessTaxonomyCategoryBriefNonsiteBlog()
		throws Exception {

		AssetCategory assetCategory = _addAssetCategory(irrelevantGroup);

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
								siteKey = irrelevantGroup.getGroupKey();
							}
						};
				}
			}
		};

		BlogPosting blogPosting = _testGetSiteBlogPostingsPage_addBlogPosting(
			new Long[] {assetCategory.getCategoryId()});

		long[] assetCategoryIds = _assetCategoryLocalService.getCategoryIds(
			BlogsEntry.class.getName(), blogPosting.getId());

		Assert.assertEquals(
			Arrays.toString(assetCategoryIds),
			expectedTaxonomyCategoryBriefs.length, assetCategoryIds.length);

		_testPutBlogPostingSuccessTaxonomyCategoryBriefs(
			expectedTaxonomyCategoryBriefs, assetCategoryIds, blogPosting);

		_assertContainedIgnoringOrder(
			expectedTaxonomyCategoryBriefs,
			blogPosting.getTaxonomyCategoryBriefs());
	}

	private void _testPutBlogPostingSuccessTaxonomyCategoryBriefs(
		TaxonomyCategoryBrief[] expectedTaxonomyCategoryBriefs,
		long[] assetCategoryIds, BlogPosting blogPosting) {

		for (long assetCategoryId : assetCategoryIds) {
			AssetCategory assetCategory =
				_assetCategoryLocalService.fetchAssetCategory(assetCategoryId);

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
							  (blogPosting.getSiteId() ==
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
	}

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	private String _originalName;

}