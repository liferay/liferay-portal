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
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

	@Ignore
	@Override
	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		super.testBatchEngineDeleteImportTask();
	}

	@Override
	@Test
	public void testDeleteSitePageTemplateSet() throws Exception {
		PageTemplateSet pageTemplateSet =
			testGetSitePageTemplateSetsPage_addPageTemplateSet(
				testGroup.getExternalReferenceCode(), randomPageTemplateSet());

		pageTemplateSetResource.deleteSitePageTemplateSet(
			testGroup.getExternalReferenceCode(),
			pageTemplateSet.getExternalReferenceCode());

		Assert.assertNull(
			_layoutPageTemplateCollectionLocalService.
				fetchLayoutPageTemplateCollectionByExternalReferenceCode(
					pageTemplateSet.getExternalReferenceCode(),
					testGroup.getGroupId()));

		PageTemplateSet liveGroupPageTemplateSet =
			testGetSitePageTemplateSetsPage_addPageTemplateSet(
				testGroup.getExternalReferenceCode(), randomPageTemplateSet());

		_enableLocalStaging();

		_assertProblemException(
			"BAD_REQUEST", null,
			() -> pageTemplateSetResource.deleteSitePageTemplateSet(
				testGroup.getExternalReferenceCode(),
				liveGroupPageTemplateSet.getExternalReferenceCode()));
	}

	@Override
	@Test
	public void testGetSitePageTemplateSet() throws Exception {
		PageTemplateSet pageTemplateSet =
			testGetSitePageTemplateSetsPage_addPageTemplateSet(
				testGroup.getExternalReferenceCode(), randomPageTemplateSet());

		_testGetSitePageTemplateSet(pageTemplateSet);

		_enableLocalStaging();

		_testGetSitePageTemplateSet(pageTemplateSet);
	}

	@Override
	@Test
	public void testGetSitePageTemplateSetsPage() throws Exception {
		super.testGetSitePageTemplateSetsPage();

		String search = RandomTestUtil.randomString();

		Page<PageTemplateSet> page =
			pageTemplateSetResource.getSitePageTemplateSetsPage(
				testGroup.getExternalReferenceCode(), search, null, null, null,
				null);

		long searchTotalCount = page.getTotalCount();

		page = pageTemplateSetResource.getSitePageTemplateSetsPage(
			testGroup.getExternalReferenceCode(), null, null, null, null, null);

		long totalCount = page.getTotalCount();

		pageTemplateSetResource.postSitePageTemplateSet(
			testGroup.getExternalReferenceCode(), randomPageTemplateSet());

		page = pageTemplateSetResource.getSitePageTemplateSetsPage(
			testGroup.getExternalReferenceCode(), search, null, null, null,
			null);

		Assert.assertEquals(searchTotalCount, page.getTotalCount());

		PageTemplateSet pageTemplateSet = randomPageTemplateSet();

		pageTemplateSet.setName(
			StringBundler.concat(
				RandomTestUtil.randomString(), StringPool.SPACE, search,
				StringPool.SPACE, RandomTestUtil.randomString()));

		pageTemplateSetResource.postSitePageTemplateSet(
			testGroup.getExternalReferenceCode(), pageTemplateSet);

		page = pageTemplateSetResource.getSitePageTemplateSetsPage(
			testGroup.getExternalReferenceCode(), search, null, null, null,
			null);

		Assert.assertEquals(searchTotalCount + 1, page.getTotalCount());

		pageTemplateSetResource.postSitePageTemplateSet(
			testGroup.getExternalReferenceCode(), randomPageTemplateSet());

		page = pageTemplateSetResource.getSitePageTemplateSetsPage(
			testGroup.getExternalReferenceCode(), search, null, null, null,
			null);

		Assert.assertEquals(searchTotalCount + 1, page.getTotalCount());

		page = pageTemplateSetResource.getSitePageTemplateSetsPage(
			testGroup.getExternalReferenceCode(), null, null, null, null, null);

		Assert.assertEquals(totalCount + 3, page.getTotalCount());

		pageTemplateSet = randomPageTemplateSet();

		pageTemplateSet.setDateModified(
			new Date(System.currentTimeMillis() - Time.DAY));

		pageTemplateSetResource.postSitePageTemplateSet(
			testGroup.getExternalReferenceCode(), pageTemplateSet);

		page = pageTemplateSetResource.getSitePageTemplateSetsPage(
			testGroup.getExternalReferenceCode(), null, null,
			"dateModified le " +
				new Date(
					System.currentTimeMillis() - Time.DAY
				).toInstant(),
			null, null);

		Assert.assertEquals(1, page.getTotalCount());
	}

	@Ignore
	@Override
	@Test
	public void testGetSitePageTemplateSetsPageWithPagination()
		throws Exception {

		super.testGetSitePageTemplateSetsPageWithPagination();
	}

	@Override
	@Test
	public void testPatchSitePageTemplateSet() throws Exception {
		PageTemplateSet pageTemplateSet = randomPageTemplateSet();

		pageTemplateSetResource.putSitePageTemplateSet(
			testGroup.getExternalReferenceCode(),
			pageTemplateSet.getExternalReferenceCode(), pageTemplateSet);

		pageTemplateSet.setDescription(RandomTestUtil.randomString());

		PageTemplateSet patchPageTemplateSet =
			pageTemplateSetResource.patchSitePageTemplateSet(
				testGroup.getExternalReferenceCode(),
				pageTemplateSet.getExternalReferenceCode(), pageTemplateSet);

		assertEquals(pageTemplateSet, patchPageTemplateSet);
		assertValid(patchPageTemplateSet);

		pageTemplateSet.setName(RandomTestUtil.randomString());

		patchPageTemplateSet = pageTemplateSetResource.patchSitePageTemplateSet(
			testGroup.getExternalReferenceCode(),
			pageTemplateSet.getExternalReferenceCode(), pageTemplateSet);

		assertEquals(pageTemplateSet, patchPageTemplateSet);
		assertValid(patchPageTemplateSet);

		_enableLocalStaging();

		_assertProblemException(
			"BAD_REQUEST", null,
			() -> pageTemplateSetResource.patchSitePageTemplateSet(
				testGroup.getExternalReferenceCode(),
				pageTemplateSet.getExternalReferenceCode(), pageTemplateSet));
	}

	@Override
	@Test
	public void testPostSitePageTemplateSet() throws Exception {
		PageTemplateSet randomPageTemplateSet = randomPageTemplateSet();

		randomPageTemplateSet.setKey(StringPool.BLANK);

		PageTemplateSet postPageTemplateSet = _testPostSitePageTemplateSet(
			randomPageTemplateSet);

		Assert.assertTrue(Validator.isNotNull(postPageTemplateSet.getKey()));

		randomPageTemplateSet = randomPageTemplateSet();

		postPageTemplateSet = _testPostSitePageTemplateSet(
			randomPageTemplateSet);

		Assert.assertEquals(
			randomPageTemplateSet.getKey(), postPageTemplateSet.getKey());

		_postSitePageTemplateSetWithInvalidKey(
			postPageTemplateSet.getKey(),
			StringBundler.concat(
				"Duplicate page template set for group ",
				testGroup.getGroupId(), " with key ",
				postPageTemplateSet.getKey()));

		String key =
			RandomTestUtil.randomString() + StringPool.AMPERSAND +
				RandomTestUtil.randomString();

		_postSitePageTemplateSetWithInvalidKey(
			key,
			StringBundler.concat(
				"Key ", key,
				" must contain only alphanumeric characters, dashes, and ",
				"underscores"));

		key = RandomTestUtil.randomString(80);

		_postSitePageTemplateSetWithInvalidKey(
			key,
			StringBundler.concat(
				"Key ", key, " must have fewer than 75 characters"));
	}

	@Override
	@Test
	public void testPutSitePageTemplateSet() throws Exception {
		PageTemplateSet pageTemplateSet = randomPageTemplateSet();

		PageTemplateSet putPageTemplateSet =
			pageTemplateSetResource.putSitePageTemplateSet(
				testGroup.getExternalReferenceCode(),
				pageTemplateSet.getExternalReferenceCode(), pageTemplateSet);

		assertEquals(pageTemplateSet, putPageTemplateSet);
		assertValid(putPageTemplateSet);

		_enableLocalStaging();

		_assertProblemException(
			"BAD_REQUEST", null,
			() -> pageTemplateSetResource.putSitePageTemplateSet(
				testGroup.getExternalReferenceCode(),
				pageTemplateSet.getExternalReferenceCode(), pageTemplateSet));
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"description", "externalReferenceCode", "name"};
	}

	@Override
	protected PageTemplateSet randomPageTemplateSet() throws Exception {
		PageTemplateSet pageTemplateSet = super.randomPageTemplateSet();

		pageTemplateSet.setDateCreated(new Date(System.currentTimeMillis()));
		pageTemplateSet.setDateModified(new Date(System.currentTimeMillis()));

		return pageTemplateSet;
	}

	@Override
	protected PageTemplateSet
			testGetSitePageTemplateSetsPage_addPageTemplateSet(
				String siteExternalReferenceCode,
				PageTemplateSet pageTemplateSet)
		throws Exception {

		return pageTemplateSetResource.putSitePageTemplateSet(
			siteExternalReferenceCode,
			pageTemplateSet.getExternalReferenceCode(), pageTemplateSet);
	}

	@Override
	protected Map<String, Map<String, String>>
		testGetSitePageTemplateSetsPage_getExpectedActions(
			String siteExternalReferenceCode) {

		return new HashMap<>();
	}

	@Override
	protected PageTemplateSet testPostSitePageTemplateSet_addPageTemplateSet(
			PageTemplateSet pageTemplateSet)
		throws Exception {

		return pageTemplateSetResource.putSitePageTemplateSet(
			testGroup.getExternalReferenceCode(),
			pageTemplateSet.getExternalReferenceCode(), pageTemplateSet);
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

	private void _postSitePageTemplateSetWithInvalidKey(
			String key, String title)
		throws Exception {

		PageTemplateSet pageTemplateSet = randomPageTemplateSet();

		pageTemplateSet.setKey(key);

		_assertProblemException(
			"CONFLICT", title,
			() -> pageTemplateSetResource.postSitePageTemplateSet(
				testGroup.getExternalReferenceCode(), pageTemplateSet));
	}

	private void _testGetSitePageTemplateSet(PageTemplateSet pageTemplateSet)
		throws Exception {

		PageTemplateSet getPageTemplateSet =
			pageTemplateSetResource.getSitePageTemplateSet(
				testGroup.getExternalReferenceCode(),
				pageTemplateSet.getExternalReferenceCode());

		assertEquals(pageTemplateSet, getPageTemplateSet);
		assertValid(getPageTemplateSet);
	}

	private PageTemplateSet _testPostSitePageTemplateSet(
			PageTemplateSet pageTemplateSet)
		throws Exception {

		PageTemplateSet postPageTemplateSet =
			testPostSitePageTemplateSet_addPageTemplateSet(pageTemplateSet);

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