/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.headless.admin.site.client.dto.v1_0.ContainerPageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentLink;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentLinkInlineValue;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentLinkMappedValue;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentLinkValue;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentMappedValueItemContextReference;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentMappedValueItemExternalReference;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentMappedValueItemReference;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentViewport;
import com.liferay.headless.admin.site.client.dto.v1_0.HtmlProperties;
import com.liferay.headless.admin.site.client.dto.v1_0.Mapping;
import com.liferay.headless.admin.site.client.dto.v1_0.PageElement;
import com.liferay.headless.admin.site.client.dto.v1_0.PageElementDefinition;
import com.liferay.headless.admin.site.client.problem.Problem;
import com.liferay.headless.admin.site.resource.v1_0.test.util.PageElementsTestUtil;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.responsive.ViewportSize;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.segments.constants.SegmentsExperienceConstants;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.util.Map;

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

	@Ignore
	@Override
	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		super.testBatchEngineDeleteImportTask();
	}

	@Override
	@Test
	public void testDeleteSitePageSpecificationPageExperiencePageElement()
		throws Exception {

		PageElement pageElement =
			testPostSitePageSpecificationPageExperiencePageElement_addPageElement(
				randomPageElement());

		LayoutStructure layoutStructure = _getLayoutStructure();

		Assert.assertNotNull(
			layoutStructure.getLayoutStructureItem(
				pageElement.getExternalReferenceCode()));

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				testGroup.getGroupId(), SegmentsExperienceConstants.KEY_DEFAULT,
				_layout.getPlid());

		pageElementResource.
			deleteSitePageSpecificationPageExperiencePageElement(
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
				deleteSitePageSpecificationPageExperiencePageElement(
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
	public void testGetSitePageSpecificationPageExperiencePageElement()
		throws Exception {

		PageElement postPageElement =
			testPostSitePageSpecificationPageExperiencePageElement_addPageElement(
				randomPageElement());

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				testGroup.getGroupId(), SegmentsExperienceConstants.KEY_DEFAULT,
				_layout.getPlid());

		PageElement getPageElement =
			pageElementResource.
				getSitePageSpecificationPageExperiencePageElement(
					testGroup.getExternalReferenceCode(),
					_draftLayout.getExternalReferenceCode(),
					segmentsExperience.getExternalReferenceCode(),
					postPageElement.getExternalReferenceCode());

		assertEquals(postPageElement, getPageElement);
		assertValid(getPageElement);

		try {
			pageElementResource.
				getSitePageSpecificationPageExperiencePageElement(
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

	@Override
	@Test
	public void testPatchSitePageSpecificationPageExperiencePageElement()
		throws Exception {

		PageElement postPageElement =
			testPostSitePageSpecificationPageExperiencePageElement_addPageElement(
				randomPageElement());

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				testGroup.getGroupId(), SegmentsExperienceConstants.KEY_DEFAULT,
				_layout.getPlid());

		PageElement pathPageElement =
			pageElementResource.
				patchSitePageSpecificationPageExperiencePageElement(
					testGroup.getExternalReferenceCode(),
					_draftLayout.getExternalReferenceCode(),
					segmentsExperience.getExternalReferenceCode(),
					postPageElement.getExternalReferenceCode(),
					postPageElement);

		assertEquals(postPageElement, pathPageElement);
		assertValid(pathPageElement);

		try {
			pageElementResource.
				patchSitePageSpecificationPageExperiencePageElement(
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

	@Override
	@Test
	public void testPostSitePageSpecificationPageExperiencePageElement()
		throws Exception {

		_testPostSitePageSpecificationPageExperiencePageElement(
			_randomPageElement(
				PageElementDefinition.Type.COLLECTION, StringPool.BLANK,
				_randomPageElement(
					PageElementDefinition.Type.COLLECTION_ITEM,
					StringPool.BLANK)));
		_testPostSitePageSpecificationPageExperiencePageElement(
			_randomPageElement(
				PageElementDefinition.Type.COLLECTION_ITEM, StringPool.BLANK));
		_testPostSitePageSpecificationPageExperiencePageElement(
			_randomPageElement(
				PageElementDefinition.Type.COLUMN, StringPool.BLANK));
		_testPostSitePageSpecificationPageExperiencePageElement(
			_getContainerPageElement(
				null, "custom css 1", null, null, "FileEntry_fileName", null,
				false, RandomTestUtil.randomString()));

		FileEntry fileEntry = DLAppLocalServiceUtil.addFileEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			testGroup.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + "." + ContentTypes.IMAGE_JPEG,
			MimeTypesUtil.getExtensionContentType(ContentTypes.IMAGE_JPEG),
			new byte[0], null, null, null,
			ServiceContextTestUtil.getServiceContext(testGroup.getGroupId()));

		_testPostSitePageSpecificationPageExperiencePageElement(
			_getContainerPageElement(
				null, "custom css 1", FileEntry.class.getName(),
				fileEntry.getExternalReferenceCode(), "FileEntry_fileName",
				null, false, RandomTestUtil.randomString()));

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			testGroup.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		_testPutSitePageSpecificationPageExperiencePageElement(
			_getContainerPageElement(
				null, "custom css 1", JournalArticle.class.getName(),
				journalArticle.getExternalReferenceCode(),
				"JournalArticle_title", null, false,
				RandomTestUtil.randomString()));

		Layout layout = LayoutTestUtil.addTypeContentLayout(testGroup);

		_getContainerPageElement(
			new String[] {"1", "2", "3"}, null, Layout.class.getName(),
			layout.getExternalReferenceCode(), null, null, true,
			RandomTestUtil.randomString());

		_testPutSitePageSpecificationPageExperiencePageElement(
			_getContainerPageElement(
				new String[] {"cssClass1", "cssClass2"}, "custom css 2", null,
				null, null,
				HashMapBuilder.put(
					LocaleUtil.SPAIN.toString(), "https://www.liferay.es"
				).put(
					LocaleUtil.US.toString(), "https://www.liferay.com"
				).build(),
				false, RandomTestUtil.randomString()));
		_testPostSitePageSpecificationPageExperiencePageElement(
			_randomPageElement(
				PageElementDefinition.Type.DROP_ZONE, StringPool.BLANK));

		PageElement formPageElement = _randomPageElement(
			PageElementDefinition.Type.FORM, StringPool.BLANK);

		_testPostSitePageSpecificationPageExperiencePageElement(
			formPageElement);

		PageElement formStepContainerPageElement = _randomPageElement(
			PageElementDefinition.Type.FORM_STEP_CONTAINER,
			formPageElement.getExternalReferenceCode());

		_testPostSitePageSpecificationPageExperiencePageElement(
			formStepContainerPageElement);

		_testPostSitePageSpecificationPageExperiencePageElement(
			_randomPageElement(
				PageElementDefinition.Type.FORM_STEP,
				formStepContainerPageElement.getExternalReferenceCode()));

		PageElement fragmentPageElement = _randomPageElement(
			PageElementDefinition.Type.FRAGMENT, StringPool.BLANK);

		_testPostSitePageSpecificationPageExperiencePageElement(
			fragmentPageElement);

		_testPostSitePageSpecificationPageExperiencePageElement(
			_randomPageElement(
				PageElementDefinition.Type.FRAGMENT_DROP_ZONE,
				fragmentPageElement.getExternalReferenceCode()));

		_testPostSitePageSpecificationPageExperiencePageElement(
			_randomPageElement(
				PageElementDefinition.Type.ROW, StringPool.BLANK));
	}

	@Ignore
	@Override
	@Test
	public void testPostSitePageSpecificationPageExperiencePageElementFragmentComposition()
		throws Exception {

		super.
			testPostSitePageSpecificationPageExperiencePageElementFragmentComposition();
	}

	@Override
	@Test
	public void testPutSitePageSpecificationPageExperiencePageElement()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		_testPutSitePageSpecificationPageExperiencePageElement(
			_getContainerPageElement(
				null, "custom css 1", null, null, "FileEntry_fileName", null,
				false, externalReferenceCode));

		FileEntry fileEntry = DLAppLocalServiceUtil.addFileEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			testGroup.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + "." + ContentTypes.IMAGE_JPEG,
			MimeTypesUtil.getExtensionContentType(ContentTypes.IMAGE_JPEG),
			new byte[0], null, null, null,
			ServiceContextTestUtil.getServiceContext(testGroup.getGroupId()));

		_testPutSitePageSpecificationPageExperiencePageElement(
			_getContainerPageElement(
				null, "custom css 1", FileEntry.class.getName(),
				fileEntry.getExternalReferenceCode(), "FileEntry_fileName",
				null, false, externalReferenceCode));

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			testGroup.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		_testPutSitePageSpecificationPageExperiencePageElement(
			_getContainerPageElement(
				null, "custom css 1", JournalArticle.class.getName(),
				journalArticle.getExternalReferenceCode(),
				"JournalArticle_title", null, false, externalReferenceCode));

		Layout layout = LayoutTestUtil.addTypeContentLayout(testGroup);

		_testPutSitePageSpecificationPageExperiencePageElement(
			_getContainerPageElement(
				new String[] {"1", "2", "3"}, null, Layout.class.getName(),
				layout.getExternalReferenceCode(), null, null, true,
				externalReferenceCode));

		_testPutSitePageSpecificationPageExperiencePageElement(
			_getContainerPageElement(
				new String[] {"cssClass1", "cssClass2"}, "custom css 2", null,
				null, null,
				HashMapBuilder.put(
					LocaleUtil.SPAIN.toString(), "https://www.liferay.es"
				).put(
					LocaleUtil.US.toString(), "https://www.liferay.com"
				).build(),
				false, externalReferenceCode));
		_testPutSitePageSpecificationPageExperiencePageElement(
			_getPageElement(
				new ContainerPageElementDefinition() {
					{
						setIndexed(false);
						setType(Type.CONTAINER);
					}
				},
				externalReferenceCode));
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
			PageElementDefinition.Type.CONTAINER, StringPool.BLANK);
	}

	@Override
	protected String
			testGetSitePageSpecificationPageExperiencePageElement_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	@Override
	protected PageElement
			testGetSitePageSpecificationPageExperiencePageElementPageElementsPage_addPageElement(
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
			postSitePageSpecificationPageExperiencePageElement(
				testGroup.getExternalReferenceCode(),
				pageSpecificationExternalReferenceCode,
				segmentsExperience.getExternalReferenceCode(), pageElement);
	}

	@Override
	protected String
			testGetSitePageSpecificationPageExperiencePageElementPageElementsPage_getPageElementExternalReferenceCode()
		throws Exception {

		LayoutStructure layoutStructure = _getLayoutStructure();

		return layoutStructure.getMainItemId();
	}

	@Override
	protected String
			testGetSitePageSpecificationPageExperiencePageElementPageElementsPage_getPageExperienceExternalReferenceCode()
		throws Exception {

		return testGetSitePageSpecificationPageExperiencePageElementsPage_getPageExperienceExternalReferenceCode();
	}

	@Override
	protected String
			testGetSitePageSpecificationPageExperiencePageElementPageElementsPage_getPageSpecificationExternalReferenceCode()
		throws Exception {

		return _draftLayout.getExternalReferenceCode();
	}

	@Override
	protected PageElement
			testGetSitePageSpecificationPageExperiencePageElementsPage_addPageElement(
				String siteExternalReferenceCode,
				String sitePageExternalReferenceCode,
				String pageExperienceExternalReferenceCode,
				PageElement pageElement)
		throws Exception {

		return pageElementResource.
			postSitePageSpecificationPageExperiencePageElement(
				testGroup.getExternalReferenceCode(),
				sitePageExternalReferenceCode,
				pageExperienceExternalReferenceCode, pageElement);
	}

	@Override
	protected String
			testGetSitePageSpecificationPageExperiencePageElementsPage_getIrrelevantPageSpecificationExternalReferenceCode()
		throws Exception {

		return irrelevantGroup.getExternalReferenceCode();
	}

	@Override
	protected String
			testGetSitePageSpecificationPageExperiencePageElementsPage_getPageExperienceExternalReferenceCode()
		throws Exception {

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				testGroup.getGroupId(), SegmentsExperienceConstants.KEY_DEFAULT,
				_layout.getPlid());

		return segmentsExperience.getExternalReferenceCode();
	}

	@Override
	protected String
			testGetSitePageSpecificationPageExperiencePageElementsPage_getPageSpecificationExternalReferenceCode()
		throws Exception {

		return _draftLayout.getExternalReferenceCode();
	}

	@Override
	protected PageElement
			testPostSitePageSpecificationPageExperiencePageElement_addPageElement(
				PageElement pageElement)
		throws Exception {

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				testGroup.getGroupId(), SegmentsExperienceConstants.KEY_DEFAULT,
				_layout.getPlid());

		return pageElementResource.
			postSitePageSpecificationPageExperiencePageElement(
				testGroup.getExternalReferenceCode(),
				_draftLayout.getExternalReferenceCode(),
				segmentsExperience.getExternalReferenceCode(), pageElement);
	}

	private PageElement _getContainerPageElement(
			String[] cssClasses, String customCss, String fragmentLinkClassName,
			String fragmentLinkExternalReferenceCode,
			String fragmentLinkFieldKey,
			Map<String, String> fragmentLinkLocalizedValues, boolean indexed,
			String pageElementExternalReferenceCode)
		throws Exception {

		ContainerPageElementDefinition containerPageElementDefinition =
			new ContainerPageElementDefinition();

		containerPageElementDefinition.setContentVisibility(
			ContainerPageElementDefinition.ContentVisibility.AUTO);
		containerPageElementDefinition.setCssClasses(cssClasses);
		containerPageElementDefinition.setCustomCSS(customCss);
		containerPageElementDefinition.setFragmentLink(
			() -> _getFragmentLink(
				fragmentLinkClassName, fragmentLinkExternalReferenceCode,
				fragmentLinkFieldKey, fragmentLinkLocalizedValues));
		containerPageElementDefinition.setFragmentViewports(
			_getFragmentViewports());
		containerPageElementDefinition.setHtmlProperties(
			() -> new HtmlProperties() {
				{
					setHtmlTag(HtmlTag.DIV);
				}
			});
		containerPageElementDefinition.setIndexed(indexed);
		containerPageElementDefinition.setLayout(
			() -> new com.liferay.headless.admin.site.client.dto.v1_0.Layout() {
				{
					setAlign(Align.END);
					setContentDisplay(ContentDisplay.FLEX_ROW);
					setFlexWrap(FlexWrap.WRAP_REVERSE);
					setJustify(Justify.CENTER);
					setWidthType(WidthType.FIXED);
				}
			});
		containerPageElementDefinition.setType(
			PageElementDefinition.Type.CONTAINER);

		return _getPageElement(
			containerPageElementDefinition, pageElementExternalReferenceCode);
	}

	private FragmentLink _getFragmentLink(
		String className, String externalReferenceCode, String fieldKey,
		Map<String, String> localizedValues) {

		return new FragmentLink() {
			{
				setTarget(Target.BLANK);
				setValue(
					() -> {
						if (localizedValues != null) {
							return _getFragmentLinkInlineValue(localizedValues);
						}

						return _getFragmentLinkMappedValue(
							className, externalReferenceCode, fieldKey);
					});
			}
		};
	}

	private FragmentLinkInlineValue _getFragmentLinkInlineValue(
		Map<String, String> localizedValues) {

		return new FragmentLinkInlineValue() {
			{
				setType(Type.FRAGMENT_INLINE_VALUE);
				setValue_i18n(localizedValues);
			}
		};
	}

	private FragmentLinkMappedValue _getFragmentLinkMappedValue(
		String className, String externalReferenceCode, String fieldKey) {

		FragmentLinkMappedValue fragmentLinkMappedValue =
			new FragmentLinkMappedValue();

		Mapping mapping = new Mapping();

		mapping.setFieldKey(() -> fieldKey);
		mapping.setItemReference(
			() -> _getFragmentMappedValueItemReference(
				className, externalReferenceCode));

		fragmentLinkMappedValue.setMapping(mapping);

		fragmentLinkMappedValue.setType(
			FragmentLinkValue.Type.FRAGMENT_MAPPED_VALUE);

		return fragmentLinkMappedValue;
	}

	private FragmentMappedValueItemReference
		_getFragmentMappedValueItemReference(
			String className, String externalReferenceCode) {

		if (Validator.isNull(className)) {
			return new FragmentMappedValueItemContextReference() {
				{
					setContextSource(() -> ContextSource.DISPLAY_PAGE_ITEM);
					setType(Type.CONTEXT_REFERENCE);
				}
			};
		}

		FragmentMappedValueItemExternalReference
			fragmentMappedValueItemExternalReference =
				new FragmentMappedValueItemExternalReference();

		fragmentMappedValueItemExternalReference.setClassName(className);
		fragmentMappedValueItemExternalReference.setExternalReferenceCode(
			externalReferenceCode);
		fragmentMappedValueItemExternalReference.setType(
			FragmentMappedValueItemReference.Type.ITEM_EXTERNAL_REFERENCE);

		return fragmentMappedValueItemExternalReference;
	}

	private FragmentViewport[] _getFragmentViewports() {
		return new FragmentViewport[] {
			new FragmentViewport() {
				{
					setCustomCSS("mobile custom css");
					setId(ViewportSize.MOBILE_LANDSCAPE::getViewportSizeId);
				}
			},
			new FragmentViewport() {
				{
					setCustomCSS("tablet custom css");
					setId(ViewportSize.TABLET::getViewportSizeId);
				}
			}
		};
	}

	private LayoutStructure _getLayoutStructure() {
		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					testGroup.getGroupId(), _draftLayout.getPlid());

		return LayoutStructure.of(
			layoutPageTemplateStructure.getDefaultSegmentsExperienceData());
	}

	private PageElement _getPageElement(
			PageElementDefinition pageElementDefinition,
			String pageElementExternalReferenceCode)
		throws Exception {

		PageElement pageElement = super.randomPageElement();

		pageElement.setExternalReferenceCode(pageElementExternalReferenceCode);
		pageElement.setPageElementDefinition(pageElementDefinition);
		pageElement.setPageElements(new PageElement[0]);
		pageElement.setParentExternalReferenceCode(StringPool.BLANK);
		pageElement.setPosition(0);

		return pageElement;
	}

	private PageElement _randomPageElement(
			PageElementDefinition.Type pageElementDefinitionType,
			String parentExternalReferenceCode, PageElement... pageElements)
		throws Exception {

		PageElement pageElement = super.randomPageElement();

		pageElement.setPageElementDefinition(
			() -> PageElementsTestUtil.getPageElementDefinition(
				pageElementDefinitionType));
		pageElement.setPageElements(pageElements);
		pageElement.setParentExternalReferenceCode(parentExternalReferenceCode);

		int position = 0;

		if (Validator.isNull(parentExternalReferenceCode)) {
			position = _position++;
		}

		pageElement.setPosition(position);

		return pageElement;
	}

	private void _testPostSitePageSpecificationPageExperiencePageElement(
			PageElement pageElement)
		throws Exception {

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				testGroup.getGroupId(), SegmentsExperienceConstants.KEY_DEFAULT,
				_layout.getPlid());

		PageElement postPageElement =
			pageElementResource.
				postSitePageSpecificationPageExperiencePageElement(
					testGroup.getExternalReferenceCode(),
					_draftLayout.getExternalReferenceCode(),
					segmentsExperience.getExternalReferenceCode(), pageElement);

		assertEquals(pageElement, postPageElement);
		assertValid(postPageElement);
	}

	private void _testPutSitePageSpecificationPageExperiencePageElement(
			PageElement pageElement)
		throws Exception {

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				testGroup.getGroupId(), SegmentsExperienceConstants.KEY_DEFAULT,
				_layout.getPlid());

		PageElement putPageElement =
			pageElementResource.
				putSitePageSpecificationPageExperiencePageElement(
					testGroup.getExternalReferenceCode(),
					_draftLayout.getExternalReferenceCode(),
					segmentsExperience.getExternalReferenceCode(),
					pageElement.getExternalReferenceCode(), pageElement);

		assertEquals(pageElement, putPageElement);
		assertValid(putPageElement);
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