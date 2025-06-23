/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.service.StagingLocalService;
import com.liferay.headless.admin.site.client.dto.v1_0.PageTemplateSet;
import com.liferay.headless.admin.site.client.pagination.Page;
import com.liferay.headless.admin.site.client.problem.Problem;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalService;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rubén Pulido
 */
@FeatureFlag("LPD-35443")
@RunWith(Arquillian.class)
public class PageTemplateSetResourceTest
	extends BasePageTemplateSetResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Override
	@Test
	public void testDeleteSiteSiteByExternalReferenceCodePageTemplateSet()
		throws Exception {

		PageTemplateSet pageTemplateSet =
			testGetSiteSiteByExternalReferenceCodePageTemplateSetsPage_addPageTemplateSet(
				testGroup.getExternalReferenceCode(), randomPageTemplateSet());

		pageTemplateSetResource.
			deleteSiteSiteByExternalReferenceCodePageTemplateSet(
				testGroup.getExternalReferenceCode(),
				pageTemplateSet.getExternalReferenceCode());

		Assert.assertNull(
			_layoutPageTemplateCollectionLocalService.
				fetchLayoutPageTemplateCollectionByExternalReferenceCode(
					pageTemplateSet.getExternalReferenceCode(),
					testGroup.getGroupId()));

		PageTemplateSet liveGroupPageTemplateSet =
			testGetSiteSiteByExternalReferenceCodePageTemplateSetsPage_addPageTemplateSet(
				testGroup.getExternalReferenceCode(), randomPageTemplateSet());

		_enableLocalStaging();

		_assertProblemException(
			"BAD_REQUEST", null,
			() ->
				pageTemplateSetResource.
					deleteSiteSiteByExternalReferenceCodePageTemplateSet(
						testGroup.getExternalReferenceCode(),
						liveGroupPageTemplateSet.getExternalReferenceCode()));
	}

	@Ignore
	@Override
	@Test
	public void testGetSitePageTemplateSetPermissionsPage() throws Exception {
		super.testGetSitePageTemplateSetPermissionsPage();
	}

	@Override
	@Test
	public void testGetSiteSiteByExternalReferenceCodePageTemplateSet()
		throws Exception {

		PageTemplateSet pageTemplateSet =
			testGetSiteSiteByExternalReferenceCodePageTemplateSetsPage_addPageTemplateSet(
				testGroup.getExternalReferenceCode(), randomPageTemplateSet());

		_testGetSiteSiteByExternalReferenceCodePageTemplateSet(pageTemplateSet);

		_enableLocalStaging();

		_testGetSiteSiteByExternalReferenceCodePageTemplateSet(pageTemplateSet);
	}

	@Override
	@Test
	public void testGetSiteSiteByExternalReferenceCodePageTemplateSetsPage()
		throws Exception {

		super.testGetSiteSiteByExternalReferenceCodePageTemplateSetsPage();

		String search = RandomTestUtil.randomString();

		Page<PageTemplateSet> page =
			pageTemplateSetResource.
				getSiteSiteByExternalReferenceCodePageTemplateSetsPage(
					testGroup.getExternalReferenceCode(), search, null, null,
					null, null);

		long searchTotalCount = page.getTotalCount();

		page =
			pageTemplateSetResource.
				getSiteSiteByExternalReferenceCodePageTemplateSetsPage(
					testGroup.getExternalReferenceCode(), null, null, null,
					null, null);

		long totalCount = page.getTotalCount();

		pageTemplateSetResource.
			postSiteSiteByExternalReferenceCodePageTemplateSet(
				testGroup.getExternalReferenceCode(), randomPageTemplateSet());

		page =
			pageTemplateSetResource.
				getSiteSiteByExternalReferenceCodePageTemplateSetsPage(
					testGroup.getExternalReferenceCode(), search, null, null,
					null, null);

		Assert.assertEquals(searchTotalCount, page.getTotalCount());

		PageTemplateSet pageTemplateSet = randomPageTemplateSet();

		pageTemplateSet.setName(
			RandomTestUtil.randomString() + search +
				RandomTestUtil.randomString());

		pageTemplateSetResource.
			postSiteSiteByExternalReferenceCodePageTemplateSet(
				testGroup.getExternalReferenceCode(), pageTemplateSet);

		page =
			pageTemplateSetResource.
				getSiteSiteByExternalReferenceCodePageTemplateSetsPage(
					testGroup.getExternalReferenceCode(), search, null, null,
					null, null);

		Assert.assertEquals(searchTotalCount + 1, page.getTotalCount());

		pageTemplateSetResource.
			postSiteSiteByExternalReferenceCodePageTemplateSet(
				testGroup.getExternalReferenceCode(), randomPageTemplateSet());

		page =
			pageTemplateSetResource.
				getSiteSiteByExternalReferenceCodePageTemplateSetsPage(
					testGroup.getExternalReferenceCode(), search, null, null,
					null, null);

		Assert.assertEquals(searchTotalCount + 1, page.getTotalCount());

		page =
			pageTemplateSetResource.
				getSiteSiteByExternalReferenceCodePageTemplateSetsPage(
					testGroup.getExternalReferenceCode(), null, null, null,
					null, null);

		Assert.assertEquals(totalCount + 3, page.getTotalCount());
	}

	@Ignore
	@Override
	@Test
	public void testGetSiteSiteByExternalReferenceCodePageTemplateSetsPageWithPagination()
		throws Exception {

		super.
			testGetSiteSiteByExternalReferenceCodePageTemplateSetsPageWithPagination();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetSiteSiteByExternalReferenceCodePageTemplateSet()
		throws Exception {

		super.testGraphQLGetSiteSiteByExternalReferenceCodePageTemplateSet();
	}

	@Override
	@Test
	public void testPatchSiteSiteByExternalReferenceCodePageTemplateSet()
		throws Exception {

		PageTemplateSet pageTemplateSet = randomPageTemplateSet();

		pageTemplateSetResource.
			putSiteSiteByExternalReferenceCodePageTemplateSet(
				testGroup.getExternalReferenceCode(),
				pageTemplateSet.getExternalReferenceCode(), pageTemplateSet);

		pageTemplateSet.setDescription(RandomTestUtil.randomString());

		PageTemplateSet patchPageTemplateSet =
			pageTemplateSetResource.
				patchSiteSiteByExternalReferenceCodePageTemplateSet(
					testGroup.getExternalReferenceCode(),
					pageTemplateSet.getExternalReferenceCode(),
					pageTemplateSet);

		assertEquals(pageTemplateSet, patchPageTemplateSet);
		assertValid(patchPageTemplateSet);

		pageTemplateSet.setName(RandomTestUtil.randomString());

		patchPageTemplateSet =
			pageTemplateSetResource.
				patchSiteSiteByExternalReferenceCodePageTemplateSet(
					testGroup.getExternalReferenceCode(),
					pageTemplateSet.getExternalReferenceCode(),
					pageTemplateSet);

		assertEquals(pageTemplateSet, patchPageTemplateSet);
		assertValid(patchPageTemplateSet);

		_enableLocalStaging();

		_assertProblemException(
			"BAD_REQUEST", null,
			() ->
				pageTemplateSetResource.
					patchSiteSiteByExternalReferenceCodePageTemplateSet(
						testGroup.getExternalReferenceCode(),
						pageTemplateSet.getExternalReferenceCode(),
						pageTemplateSet));
	}

	@Override
	@Test
	public void testPostSiteSiteByExternalReferenceCodePageTemplateSet()
		throws Exception {

		PageTemplateSet randomPageTemplateSet = randomPageTemplateSet();

		randomPageTemplateSet.setKey(StringPool.BLANK);

		PageTemplateSet postPageTemplateSet =
			_testPostSiteSiteByExternalReferenceCodePageTemplateSet(
				randomPageTemplateSet);

		Assert.assertTrue(Validator.isNotNull(postPageTemplateSet.getKey()));

		randomPageTemplateSet = randomPageTemplateSet();

		postPageTemplateSet =
			_testPostSiteSiteByExternalReferenceCodePageTemplateSet(
				randomPageTemplateSet);

		Assert.assertEquals(
			randomPageTemplateSet.getKey(), postPageTemplateSet.getKey());

		_postSiteSiteByExternalReferenceCodePageTemplateSetWithInvalidKey(
			postPageTemplateSet.getKey(),
			StringBundler.concat(
				"Duplicate page template set for group ",
				testGroup.getGroupId(), " with key ",
				postPageTemplateSet.getKey()));

		String key =
			RandomTestUtil.randomString() + StringPool.AMPERSAND +
				RandomTestUtil.randomString();

		_postSiteSiteByExternalReferenceCodePageTemplateSetWithInvalidKey(
			key,
			StringBundler.concat(
				"Key ", key,
				" must contain only alphanumeric characters, dashes, and ",
				"underscores"));

		key = RandomTestUtil.randomString(80);

		_postSiteSiteByExternalReferenceCodePageTemplateSetWithInvalidKey(
			key,
			StringBundler.concat(
				"Key ", key, " must have fewer than 75 characters"));
	}

	@Ignore
	@Override
	@Test
	public void testPutSitePageTemplateSetPermissionsPage() throws Exception {
		super.testPutSitePageTemplateSetPermissionsPage();
	}

	@Override
	@Test
	public void testPutSiteSiteByExternalReferenceCodePageTemplateSet()
		throws Exception {

		PageTemplateSet pageTemplateSet = randomPageTemplateSet();

		PageTemplateSet putPageTemplateSet =
			pageTemplateSetResource.
				putSiteSiteByExternalReferenceCodePageTemplateSet(
					testGroup.getExternalReferenceCode(),
					pageTemplateSet.getExternalReferenceCode(),
					pageTemplateSet);

		assertEquals(pageTemplateSet, putPageTemplateSet);
		assertValid(putPageTemplateSet);

		_enableLocalStaging();

		_assertProblemException(
			"BAD_REQUEST", null,
			() ->
				pageTemplateSetResource.
					putSiteSiteByExternalReferenceCodePageTemplateSet(
						testGroup.getExternalReferenceCode(),
						pageTemplateSet.getExternalReferenceCode(),
						pageTemplateSet));
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"description", "externalReferenceCode", "name"};
	}

	@Ignore
	@Override
	@Test
	protected PageTemplateSet
			testGetSitePageTemplateSetPermissionsPage_addPageTemplateSet()
		throws Exception {

		return super.
			testGetSitePageTemplateSetPermissionsPage_addPageTemplateSet();
	}

	@Override
	protected PageTemplateSet
			testGetSiteSiteByExternalReferenceCodePageTemplateSetsPage_addPageTemplateSet(
				String siteExternalReferenceCode,
				PageTemplateSet pageTemplateSet)
		throws Exception {

		return pageTemplateSetResource.
			putSiteSiteByExternalReferenceCodePageTemplateSet(
				siteExternalReferenceCode,
				pageTemplateSet.getExternalReferenceCode(), pageTemplateSet);
	}

	@Override
	protected PageTemplateSet
			testPostSiteSiteByExternalReferenceCodePageTemplateSet_addPageTemplateSet(
				PageTemplateSet pageTemplateSet)
		throws Exception {

		return pageTemplateSetResource.
			putSiteSiteByExternalReferenceCodePageTemplateSet(
				testGroup.getExternalReferenceCode(),
				pageTemplateSet.getExternalReferenceCode(), pageTemplateSet);
	}

	@Ignore
	@Override
	@Test
	protected PageTemplateSet
			testPutSitePageTemplateSetPermissionsPage_addPageTemplateSet()
		throws Exception {

		return super.
			testPutSitePageTemplateSetPermissionsPage_addPageTemplateSet();
	}

	private void _assertProblemException(
			String status, String title,
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		try {
			unsafeRunnable.run();

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals(status, problem.getStatus());
			Assert.assertEquals(title, problem.getTitle());
		}
	}

	private void _enableLocalStaging() throws Exception {
		_stagingLocalService.enableLocalStaging(
			TestPropsValues.getUserId(), testGroup, true, false,
			ServiceContextTestUtil.getServiceContext(
				testGroup, TestPropsValues.getUserId()));
	}

	private void
			_postSiteSiteByExternalReferenceCodePageTemplateSetWithInvalidKey(
				String key, String title)
		throws Exception {

		PageTemplateSet pageTemplateSet = randomPageTemplateSet();

		pageTemplateSet.setKey(key);

		_assertProblemException(
			"CONFLICT", title,
			() ->
				pageTemplateSetResource.
					postSiteSiteByExternalReferenceCodePageTemplateSet(
						testGroup.getExternalReferenceCode(), pageTemplateSet));
	}

	private void _testGetSiteSiteByExternalReferenceCodePageTemplateSet(
			PageTemplateSet pageTemplateSet)
		throws Exception {

		PageTemplateSet getPageTemplateSet =
			pageTemplateSetResource.
				getSiteSiteByExternalReferenceCodePageTemplateSet(
					testGroup.getExternalReferenceCode(),
					pageTemplateSet.getExternalReferenceCode());

		assertEquals(pageTemplateSet, getPageTemplateSet);
		assertValid(getPageTemplateSet);
	}

	private PageTemplateSet
			_testPostSiteSiteByExternalReferenceCodePageTemplateSet(
				PageTemplateSet pageTemplateSet)
		throws Exception {

		PageTemplateSet postPageTemplateSet =
			testPostSiteSiteByExternalReferenceCodePageTemplateSet_addPageTemplateSet(
				pageTemplateSet);

		assertEquals(pageTemplateSet, postPageTemplateSet);
		assertValid(postPageTemplateSet);

		return postPageTemplateSet;
	}

	@Inject
	private LayoutPageTemplateCollectionLocalService
		_layoutPageTemplateCollectionLocalService;

	@Inject
	private StagingLocalService _stagingLocalService;

}