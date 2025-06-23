/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.admin.site.client.dto.v1_0.ContainerPageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.PageElement;
import com.liferay.headless.admin.site.client.dto.v1_0.PageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.PageExperience;
import com.liferay.headless.admin.site.client.problem.Problem;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.segments.test.util.SegmentsTestUtil;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rubén Pulido
 */
@FeatureFlag("LPD-35443")
@RunWith(Arquillian.class)
public class PageExperienceResourceTest
	extends BasePageExperienceResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_layout = LayoutTestUtil.addTypeContentLayout(testGroup);

		_draftLayout = _layout.fetchDraftLayout();
	}

	@Override
	@Test
	public void testDeleteSiteSiteByExternalReferenceCodePageExperience()
		throws Exception {

		PageExperience postPageExperience =
			testPostSiteSiteByExternalReferenceCodePageSpecificationPageExperience_addPageExperience(
				randomPageExperience());

		Assert.assertNotNull(
			_segmentsExperienceLocalService.
				fetchSegmentsExperienceByExternalReferenceCode(
					postPageExperience.getExternalReferenceCode(),
					testGroup.getGroupId()));

		pageExperienceResource.
			deleteSiteSiteByExternalReferenceCodePageExperience(
				testGroup.getExternalReferenceCode(),
				postPageExperience.getExternalReferenceCode());

		Assert.assertNull(
			_segmentsExperienceLocalService.
				fetchSegmentsExperienceByExternalReferenceCode(
					postPageExperience.getExternalReferenceCode(),
					testGroup.getGroupId()));

		try {
			pageExperienceResource.
				deleteSiteSiteByExternalReferenceCodePageExperience(
					testGroup.getExternalReferenceCode(),
					postPageExperience.getExternalReferenceCode());

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
			Assert.assertNull(problem.getTitle());
		}
	}

	@Override
	@Test
	public void testGetSiteSiteByExternalReferenceCodePageExperience()
		throws Exception {

		PageExperience postPageExperience =
			testPostSiteSiteByExternalReferenceCodePageSpecificationPageExperience_addPageExperience(
				randomPageExperience());

		PageExperience getPageExperience =
			pageExperienceResource.
				getSiteSiteByExternalReferenceCodePageExperience(
					testGroup.getExternalReferenceCode(),
					postPageExperience.getExternalReferenceCode());

		assertEquals(postPageExperience, getPageExperience);
		assertValid(getPageExperience);

		try {
			pageExperienceResource.
				getSiteSiteByExternalReferenceCodePageExperience(
					testGroup.getExternalReferenceCode(),
					RandomTestUtil.randomString());

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
			Assert.assertNull(problem.getTitle());
		}
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetSiteSiteByExternalReferenceCodePageExperience()
		throws Exception {

		super.testGraphQLGetSiteSiteByExternalReferenceCodePageExperience();
	}

	@Override
	@Test
	public void testPatchSiteSiteByExternalReferenceCodePageExperience()
		throws Exception {

		PageExperience postPageExperience =
			testPostSiteSiteByExternalReferenceCodePageSpecificationPageExperience_addPageExperience(
				randomPageExperience());

		PageExperience pathPageExperience =
			pageExperienceResource.
				patchSiteSiteByExternalReferenceCodePageExperience(
					testGroup.getExternalReferenceCode(),
					postPageExperience.getExternalReferenceCode(),
					postPageExperience);

		assertEquals(postPageExperience, pathPageExperience);
		assertValid(pathPageExperience);

		try {
			pageExperienceResource.
				patchSiteSiteByExternalReferenceCodePageExperience(
					testGroup.getExternalReferenceCode(),
					RandomTestUtil.randomString(), randomPageExperience());

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
			Assert.assertNull(problem.getTitle());
		}
	}

	@Override
	@Test
	public void testPostSiteSiteByExternalReferenceCodePageSpecificationPageExperience()
		throws Exception {

		super.
			testPostSiteSiteByExternalReferenceCodePageSpecificationPageExperience();
	}

	@Override
	@Test
	public void testPutSiteSiteByExternalReferenceCodePageExperience()
		throws Exception {

		PageExperience pageExperience = randomPageExperience();

		PageExperience putPageExperience =
			pageExperienceResource.
				putSiteSiteByExternalReferenceCodePageExperience(
					testGroup.getExternalReferenceCode(),
					pageExperience.getExternalReferenceCode(), pageExperience);

		assertEquals(pageExperience, putPageExperience);
		assertValid(putPageExperience);
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"externalReferenceCode", "name_i18n"};
	}

	@Override
	protected PageExperience randomPageExperience() throws Exception {
		PageExperience pageExperience = super.randomPageExperience();

		pageExperience.setName_i18n(
			Collections.singletonMap("en-US", RandomTestUtil.randomString()));

		pageExperience.setPageElements(
			new PageElement[] {
				new PageElement() {
					{
						setPageElementDefinition(
							new ContainerPageElementDefinition() {
								{
									setIndexed(true);
									setType(
										PageElementDefinition.Type.CONTAINER);
								}
							});
						setPageElements(new PageElement[0]);
						setPosition(0);
					}
				},
				new PageElement() {
					{
						setPageElementDefinition(
							new ContainerPageElementDefinition() {
								{
									setIndexed(true);
									setType(
										PageElementDefinition.Type.CONTAINER);
								}
							});
						setPageElements(new PageElement[0]);
						setPosition(1);
					}
				}
			});
		pageExperience.setPageSpecificationExternalReferenceCode(
			_draftLayout.getExternalReferenceCode());
		pageExperience.setSegmentExternalReferenceCode(
			SegmentsTestUtil.addSegmentsEntry(
				testGroup.getGroupId()
			).getSegmentsEntryKey());

		return pageExperience;
	}

	@Override
	protected PageExperience
			testGetSiteSiteByExternalReferenceCodePageSpecificationPageExperiencesPage_addPageExperience(
				String siteExternalReferenceCode,
				String pageSpecificationExternalReferenceCode,
				PageExperience pageExperience)
		throws Exception {

		return pageExperienceResource.
			postSiteSiteByExternalReferenceCodePageSpecificationPageExperience(
				siteExternalReferenceCode,
				pageSpecificationExternalReferenceCode, pageExperience);
	}

	@Override
	protected String
			testGetSiteSiteByExternalReferenceCodePageSpecificationPageExperiencesPage_getPageSpecificationExternalReferenceCode()
		throws Exception {

		return _draftLayout.getExternalReferenceCode();
	}

	@Override
	protected PageExperience
			testPostSiteSiteByExternalReferenceCodePageSpecificationPageExperience_addPageExperience(
				PageExperience pageExperience)
		throws Exception {

		return pageExperienceResource.
			postSiteSiteByExternalReferenceCodePageSpecificationPageExperience(
				testGroup.getExternalReferenceCode(),
				pageExperience.getPageSpecificationExternalReferenceCode(),
				pageExperience);
	}

	private Layout _draftLayout;
	private Layout _layout;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}