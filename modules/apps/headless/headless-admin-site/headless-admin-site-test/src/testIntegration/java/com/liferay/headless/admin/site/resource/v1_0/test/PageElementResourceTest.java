/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.admin.site.client.dto.v1_0.CollectionItemPageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.CollectionPageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.ColumnPageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.ContainerPageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.DropZonePageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.FormPageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.FormStepContainerPageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.FormStepPageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentDropZonePageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentInstancePageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.HtmlProperties;
import com.liferay.headless.admin.site.client.dto.v1_0.PageElement;
import com.liferay.headless.admin.site.client.dto.v1_0.PageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.RowPageElementDefinition;
import com.liferay.headless.admin.site.client.problem.Problem;
import com.liferay.headless.admin.site.dto.v1_0.DefaultFragmentReference;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.segments.constants.SegmentsExperienceConstants;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceLocalService;

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
public class PageElementResourceTest extends BasePageElementResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_layout = LayoutTestUtil.addTypeContentLayout(testGroup);

		_draftLayout = _layout;
	}

	@Override
	@Test
	public void testDeleteSiteSiteByExternalReferenceCodePageElement()
		throws Exception {

		PageElement pageElement =
			testPostSiteSiteByExternalReferenceCodePageExperiencePageElement_addPageElement(
				randomPageElement());

		LayoutStructure layoutStructure = _getLayoutStructure();

		Assert.assertNotNull(
			layoutStructure.getLayoutStructureItem(
				pageElement.getExternalReferenceCode()));

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				testGroup.getGroupId(), SegmentsExperienceConstants.KEY_DEFAULT,
				_layout.getPlid());

		pageElementResource.deleteSiteSiteByExternalReferenceCodePageElement(
			testGroup.getExternalReferenceCode(),
			_draftLayout.getExternalReferenceCode(),
			segmentsExperience.getExternalReferenceCode(),
			pageElement.getExternalReferenceCode());

		_draftLayout = _layoutLocalService.fetchLayout(_draftLayout.getPlid());

		layoutStructure = _getLayoutStructure();

		Assert.assertNull(
			layoutStructure.getLayoutStructureItem(
				pageElement.getExternalReferenceCode()));

		try {
			pageElementResource.
				deleteSiteSiteByExternalReferenceCodePageElement(
					testGroup.getExternalReferenceCode(),
					_draftLayout.getExternalReferenceCode(),
					segmentsExperience.getExternalReferenceCode(),
					pageElement.getExternalReferenceCode());

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
	public void testGetSiteSiteByExternalReferenceCodePageElement()
		throws Exception {

		PageElement postPageElement =
			testPostSiteSiteByExternalReferenceCodePageExperiencePageElement_addPageElement(
				randomPageElement());

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				testGroup.getGroupId(), SegmentsExperienceConstants.KEY_DEFAULT,
				_layout.getPlid());

		PageElement getPageElement =
			pageElementResource.getSiteSiteByExternalReferenceCodePageElement(
				testGroup.getExternalReferenceCode(),
				_draftLayout.getExternalReferenceCode(),
				segmentsExperience.getExternalReferenceCode(),
				postPageElement.getExternalReferenceCode());

		assertEquals(postPageElement, getPageElement);
		assertValid(getPageElement);

		try {
			pageElementResource.getSiteSiteByExternalReferenceCodePageElement(
				testGroup.getExternalReferenceCode(),
				_draftLayout.getExternalReferenceCode(),
				segmentsExperience.getExternalReferenceCode(),
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
	public void testGraphQLGetSiteSiteByExternalReferenceCodePageElement()
		throws Exception {

		super.testGraphQLGetSiteSiteByExternalReferenceCodePageElement();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetSiteSiteByExternalReferenceCodePageElementNotFound()
		throws Exception {

		super.
			testGraphQLGetSiteSiteByExternalReferenceCodePageElementNotFound();
	}

	@Override
	@Test
	public void testPatchSiteSiteByExternalReferenceCodePageElement()
		throws Exception {

		PageElement postPageElement =
			testPostSiteSiteByExternalReferenceCodePageExperiencePageElement_addPageElement(
				randomPageElement());

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				testGroup.getGroupId(), SegmentsExperienceConstants.KEY_DEFAULT,
				_layout.getPlid());

		PageElement pathPageElement =
			pageElementResource.patchSiteSiteByExternalReferenceCodePageElement(
				testGroup.getExternalReferenceCode(),
				_draftLayout.getExternalReferenceCode(),
				segmentsExperience.getExternalReferenceCode(),
				postPageElement.getExternalReferenceCode(), postPageElement);

		assertEquals(postPageElement, pathPageElement);
		assertValid(pathPageElement);

		try {
			pageElementResource.patchSiteSiteByExternalReferenceCodePageElement(
				testGroup.getExternalReferenceCode(),
				_draftLayout.getExternalReferenceCode(),
				segmentsExperience.getExternalReferenceCode(),
				RandomTestUtil.randomString(), randomPageElement());

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
	public void testPostSiteSiteByExternalReferenceCodePageElementFragmentComposition()
		throws Exception {

		super.
			testPostSiteSiteByExternalReferenceCodePageElementFragmentComposition();
	}

	@Ignore
	@Override
	@Test
	public void testPostSiteSiteByExternalReferenceCodePageExperiencePageElement()
		throws Exception {

		super.
			testPostSiteSiteByExternalReferenceCodePageExperiencePageElement();

		_assertPostSiteSiteByExternalReferenceCodePageExperiencePageElement(
			_randomPageElement(
				new CollectionPageElementDefinition() {
					{
						setType(Type.COLLECTION);
					}
				}));
		_assertPostSiteSiteByExternalReferenceCodePageExperiencePageElement(
			_randomPageElement(
				new CollectionItemPageElementDefinition() {
					{
						setType(Type.COLLECTION_ITEM);
					}
				}));
		_assertPostSiteSiteByExternalReferenceCodePageExperiencePageElement(
			_randomPageElement(
				new ColumnPageElementDefinition() {
					{
						setType(Type.COLUMN);
					}
				}));
		_assertPostSiteSiteByExternalReferenceCodePageExperiencePageElement(
			_randomPageElement(
				new ContainerPageElementDefinition() {
					{
						setContentVisibility(StringPool.BLANK);
						setHtmlProperties(new HtmlProperties());
						setIndexed(Boolean.FALSE);
						setType(Type.CONTAINER);
					}
				}));
		_assertPostSiteSiteByExternalReferenceCodePageExperiencePageElement(
			_randomPageElement(
				new DropZonePageElementDefinition() {
					{
						setType(Type.DROP_ZONE);
					}
				}));
		_assertPostSiteSiteByExternalReferenceCodePageExperiencePageElement(
			_randomPageElement(
				new FormPageElementDefinition() {
					{
						setType(Type.FORM);
					}
				}));
		_assertPostSiteSiteByExternalReferenceCodePageExperiencePageElement(
			_randomPageElement(
				new FormStepPageElementDefinition() {
					{
						setType(Type.FORM_STEP);
					}
				}));
		_assertPostSiteSiteByExternalReferenceCodePageExperiencePageElement(
			_randomPageElement(
				new FormStepContainerPageElementDefinition() {
					{
						setType(Type.FORM_STEP_CONTAINER);
					}
				}));
		_assertPostSiteSiteByExternalReferenceCodePageExperiencePageElement(
			_randomPageElement(
				new FragmentDropZonePageElementDefinition() {
					{
						setType(Type.FRAGMENT_DROP_ZONE);
					}
				}));
		_assertPostSiteSiteByExternalReferenceCodePageExperiencePageElement(
			_randomPageElement(
				new FragmentInstancePageElementDefinition() {
					{
						setFragmentReference(
							new DefaultFragmentReference() {
								{
									setDefaultFragmentKey(
										() -> "BASIC_COMPONENT-heading");
								}
							});
						setType(Type.FRAGMENT);
					}
				}));
		_assertPostSiteSiteByExternalReferenceCodePageExperiencePageElement(
			_randomPageElement(
				new RowPageElementDefinition() {
					{
						setType(Type.ROW);
					}
				}));
	}

	@Override
	@Test
	public void testPutSiteSiteByExternalReferenceCodePageElement()
		throws Exception {

		PageElement pageElement = randomPageElement();

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				testGroup.getGroupId(), SegmentsExperienceConstants.KEY_DEFAULT,
				_layout.getPlid());

		PageElement putPageElement =
			pageElementResource.putSiteSiteByExternalReferenceCodePageElement(
				testGroup.getExternalReferenceCode(),
				_draftLayout.getExternalReferenceCode(),
				segmentsExperience.getExternalReferenceCode(),
				pageElement.getExternalReferenceCode(), pageElement);

		assertEquals(pageElement, putPageElement);
		assertValid(putPageElement);
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"externalReferenceCode", "pageElementDefinition",
			"parentExternalReferenceCode", "position"
		};
	}

	@Override
	protected PageElement randomPageElement() throws Exception {
		return _randomPageElement(
			new ContainerPageElementDefinition() {
				{
					setContentVisibility(StringPool.BLANK);
					setHtmlProperties(new HtmlProperties());
					setIndexed(Boolean.FALSE);
					setType(Type.CONTAINER);
				}
			});
	}

	@Override
	protected PageElement
			testGetSiteSiteByExternalReferenceCodePageElementPageElementsPage_addPageElement(
				String siteExternalReferenceCode,
				String pageSpecificationExternalReferenceCode,
				String pageExperienceExternalReferenceCode,
				String pageElementExternalReferenceCode,
				PageElement pageElement)
		throws Exception {

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				testGroup.getGroupId(), SegmentsExperienceConstants.KEY_DEFAULT,
				_layout.getPlid());

		return pageElementResource.
			postSiteSiteByExternalReferenceCodePageExperiencePageElement(
				testGroup.getExternalReferenceCode(),
				pageSpecificationExternalReferenceCode,
				segmentsExperience.getExternalReferenceCode(), pageElement);
	}

	@Override
	protected String
			testGetSiteSiteByExternalReferenceCodePageElementPageElementsPage_getPageElementExternalReferenceCode()
		throws Exception {

		LayoutStructure layoutStructure = _getLayoutStructure();

		return layoutStructure.getMainItemId();
	}

	protected String
			testGetSiteSiteByExternalReferenceCodePageElementPageElementsPage_getPageExperienceExternalReferenceCode()
		throws Exception {

		return testGetSiteSiteByExternalReferenceCodePageExperiencePageElementsPage_getPageExperienceExternalReferenceCode();
	}

	@Override
	protected String
			testGetSiteSiteByExternalReferenceCodePageElementPageElementsPage_getPageSpecificationExternalReferenceCode()
		throws Exception {

		return _draftLayout.getExternalReferenceCode();
	}

	@Override
	protected PageElement
			testGetSiteSiteByExternalReferenceCodePageExperiencePageElementsPage_addPageElement(
				String siteExternalReferenceCode,
				String sitePageExternalReferenceCode,
				String pageExperienceExternalReferenceCode,
				PageElement pageElement)
		throws Exception {

		return pageElementResource.
			postSiteSiteByExternalReferenceCodePageExperiencePageElement(
				testGroup.getExternalReferenceCode(),
				sitePageExternalReferenceCode,
				pageExperienceExternalReferenceCode, pageElement);
	}

	@Override
	protected String
			testGetSiteSiteByExternalReferenceCodePageExperiencePageElementsPage_getIrrelevantSiteExternalReferenceCode()
		throws Exception {

		return irrelevantGroup.getExternalReferenceCode();
	}

	@Override
	protected String
			testGetSiteSiteByExternalReferenceCodePageExperiencePageElementsPage_getPageExperienceExternalReferenceCode()
		throws Exception {

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				testGroup.getGroupId(), SegmentsExperienceConstants.KEY_DEFAULT,
				_layout.getPlid());

		return segmentsExperience.getExternalReferenceCode();
	}

	@Override
	protected String
			testGetSiteSiteByExternalReferenceCodePageExperiencePageElementsPage_getPageSpecificationExternalReferenceCode()
		throws Exception {

		return _draftLayout.getExternalReferenceCode();
	}

	@Override
	protected String
			testGetSiteSiteByExternalReferenceCodePageExperiencePageElementsPage_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	@Override
	protected PageElement
			testPostSiteSiteByExternalReferenceCodePageExperiencePageElement_addPageElement(
				PageElement pageElement)
		throws Exception {

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				testGroup.getGroupId(), SegmentsExperienceConstants.KEY_DEFAULT,
				_layout.getPlid());

		return pageElementResource.
			postSiteSiteByExternalReferenceCodePageExperiencePageElement(
				testGroup.getExternalReferenceCode(),
				_draftLayout.getExternalReferenceCode(),
				segmentsExperience.getExternalReferenceCode(), pageElement);
	}

	private void
			_assertPostSiteSiteByExternalReferenceCodePageExperiencePageElement(
				PageElement pageElement)
		throws Exception {

		PageElement postPageElement =
			testPostSiteSiteByExternalReferenceCodePageExperiencePageElement_addPageElement(
				pageElement);

		assertEquals(pageElement, postPageElement);
		assertValid(postPageElement);
	}

	private LayoutStructure _getLayoutStructure() {
		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					testGroup.getGroupId(), _draftLayout.getPlid());

		return LayoutStructure.of(
			layoutPageTemplateStructure.getDefaultSegmentsExperienceData());
	}

	private PageElement _randomPageElement(
			PageElementDefinition pageElementDefinition)
		throws Exception {

		PageElement pageElement = super.randomPageElement();

		pageElement.setPageElementDefinition(pageElementDefinition);
		pageElement.setPageElements(new PageElement[0]);
		pageElement.setParentExternalReferenceCode(StringPool.BLANK);
		pageElement.setPosition(_position++);

		return pageElement;
	}

	private Layout _draftLayout;
	private Layout _layout;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	private int _position;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}