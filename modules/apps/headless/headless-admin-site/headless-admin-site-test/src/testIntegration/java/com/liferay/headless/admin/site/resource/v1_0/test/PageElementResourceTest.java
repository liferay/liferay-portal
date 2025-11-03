/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.list.constants.AssetListEntryTypeConstants;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.service.AssetListEntryLocalServiceUtil;
import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentEntryLinkLocalServiceUtil;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.headless.admin.site.client.dto.v1_0.ClassNameReference;
import com.liferay.headless.admin.site.client.dto.v1_0.CollectionDisplayListStyle;
import com.liferay.headless.admin.site.client.dto.v1_0.CollectionDisplayPageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.CollectionDisplayViewport;
import com.liferay.headless.admin.site.client.dto.v1_0.CollectionDisplayViewportDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.CollectionItemExternalReference;
import com.liferay.headless.admin.site.client.dto.v1_0.CollectionItemPageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.CollectionReference;
import com.liferay.headless.admin.site.client.dto.v1_0.CollectionSettings;
import com.liferay.headless.admin.site.client.dto.v1_0.ContainerPageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.DisplayPageFormContainerSubmissionResult;
import com.liferay.headless.admin.site.client.dto.v1_0.EmbeddedMessageFormContainerSubmissionResult;
import com.liferay.headless.admin.site.client.dto.v1_0.EmptyCollectionConfig;
import com.liferay.headless.admin.site.client.dto.v1_0.FormContainerClassSubtypeReference;
import com.liferay.headless.admin.site.client.dto.v1_0.FormContainerConfig;
import com.liferay.headless.admin.site.client.dto.v1_0.FormContainerContextReference;
import com.liferay.headless.admin.site.client.dto.v1_0.FormContainerPageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.FormContainerReference;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentInlineValue;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentInstancePageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentLink;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentLinkInlineValue;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentLinkMappedValue;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentLinkValue;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentMappedValueItemContextReference;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentMappedValueItemExternalReference;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentMappedValueItemReference;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentViewport;
import com.liferay.headless.admin.site.client.dto.v1_0.GridPageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.GridViewport;
import com.liferay.headless.admin.site.client.dto.v1_0.GridViewportDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.HtmlProperties;
import com.liferay.headless.admin.site.client.dto.v1_0.ItemExternalReference;
import com.liferay.headless.admin.site.client.dto.v1_0.ListStyle;
import com.liferay.headless.admin.site.client.dto.v1_0.ListStyleDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.Mapping;
import com.liferay.headless.admin.site.client.dto.v1_0.ModulePageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.ModuleViewport;
import com.liferay.headless.admin.site.client.dto.v1_0.ModuleViewportDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.PageElement;
import com.liferay.headless.admin.site.client.dto.v1_0.PageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.SitePageFormContainerSubmissionResult;
import com.liferay.headless.admin.site.client.dto.v1_0.StayInPageFormContainerSubmissionResult;
import com.liferay.headless.admin.site.client.dto.v1_0.SuccessFormContainerSubmissionResult;
import com.liferay.headless.admin.site.client.dto.v1_0.SuccessNotificationMessage;
import com.liferay.headless.admin.site.client.dto.v1_0.TemplateListStyle;
import com.liferay.headless.admin.site.client.dto.v1_0.URLFormContainerSubmissionResult;
import com.liferay.headless.admin.site.client.dto.v1_0.WidgetInstance;
import com.liferay.headless.admin.site.client.dto.v1_0.WidgetInstancePageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.WidgetPermission;
import com.liferay.headless.admin.site.client.problem.Problem;
import com.liferay.headless.admin.site.client.serdes.v1_0.PageElementSerDes;
import com.liferay.headless.admin.site.resource.v1_0.test.util.FragmentConfigurationTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.PageElementsTestUtil;
import com.liferay.journal.constants.JournalContentPortletKeys;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.layout.responsive.ViewportSize;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.segments.constants.SegmentsExperienceConstants;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

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

		_testPostSitePageSpecificationPageExperiencePageElementWithCollectionDisplayPageElement();
		_testPostSitePageSpecificationPageExperiencePageElementWithContainerPageElement();
		_testPostSitePageSpecificationPageExperiencePageElementWithDropZonePageElement();
		_testPostSitePageSpecificationPageExperiencePageElementWithFormContainerPageElement();
		_testPostSitePageSpecificationPageExperiencePageElementWithFragmentPageElement();
		_testPostSitePageSpecificationPageExperiencePageElementWithGridPageElement();
		_testPostSitePageSpecificationPageExperiencePageElementWithWidgetPageElement();
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

		_testPutSitePageSpecificationPageExperiencePageElementWithCollectionDisplayPageElement();
		_testPutSitePageSpecificationPageExperiencePageElementWithContainerPageElement();
		_testPutSitePageSpecificationPageExperiencePageElementWithFormContainerPageElement();

		_testPutSitePageSpecificationPageExperiencePageElementWithFragmentPageElement();

		_testPutSitePageSpecificationPageExperiencePageElementWithGridPageElement();

		_testPutSitePageSpecificationPageExperiencePageElementWithWidgetPageElement();
	}

	@Override
	protected void assertEquals(
		PageElement pageElement1, PageElement pageElement2) {

		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		try {
			super.assertEquals(
				PageElementSerDes.toDTO(
					objectMapper.writeValueAsString(pageElement1)),
				pageElement2);
		}
		catch (JsonProcessingException jsonProcessingException) {
			throw new RuntimeException(jsonProcessingException);
		}

		PageElementDefinition pageElementDefinition =
			pageElement1.getPageElementDefinition();

		Assert.assertNotNull(pageElementDefinition);

		if ((pageElementDefinition.getType() ==
				PageElementDefinition.Type.COLLECTION_DISPLAY) ||
			(pageElementDefinition.getType() ==
				PageElementDefinition.Type.GRID)) {

			return;
		}

		Assert.assertTrue(
			Objects.deepEquals(
				pageElement1.getPageElements(),
				pageElement2.getPageElements()));
	}

	@Override
	protected void assertValid(PageElement pageElement) throws Exception {
		super.assertValid(pageElement);

		PageElementDefinition pageElementDefinition =
			pageElement.getPageElementDefinition();

		Assert.assertNotNull(pageElementDefinition);

		if (pageElementDefinition.getType() !=
				PageElementDefinition.Type.COLLECTION_DISPLAY) {

			return;
		}

		PageElement[] collectionDisplayChildPageElements =
			pageElement.getPageElements();

		Assert.assertEquals(
			Arrays.toString(collectionDisplayChildPageElements), 1,
			collectionDisplayChildPageElements.length);
		Assert.assertNotNull(
			collectionDisplayChildPageElements[0].getExternalReferenceCode());

		PageElementDefinition collectionItemPageElementDefinition =
			collectionDisplayChildPageElements[0].getPageElementDefinition();

		Assert.assertNotNull(collectionItemPageElementDefinition);

		Assert.assertEquals(
			PageElementDefinition.Type.COLLECTION_ITEM,
			collectionItemPageElementDefinition.getType());
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

	private FragmentEntry _addFragmentEntry(
			String configuration, long groupId, ServiceContext serviceContext)
		throws Exception {

		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.addFragmentCollection(
				null, TestPropsValues.getUserId(), groupId,
				StringUtil.randomString(), StringPool.BLANK, serviceContext);

		return _fragmentEntryLocalService.addFragmentEntry(
			null, TestPropsValues.getUserId(), groupId,
			fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			StringPool.BLANK,
			StringBundler.concat(
				"<h1 data-lfr-editable-id=\"element-text\" ",
				"data-lfr-editable-type=\"text\">",
				RandomTestUtil.randomString(), "</h1>"),
			StringPool.BLANK, false, configuration, null, 0, false, false,
			FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, serviceContext);
	}

	private void _addFragmentEntryLink(
			String externalReferenceCode, String namespace)
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(testGroup);

		FragmentEntryLinkLocalServiceUtil.addFragmentEntryLink(
			externalReferenceCode, TestPropsValues.getUserId(),
			testGroup.getGroupId(), null, null, null, 0, layout.getPlid(),
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, StringPool.BLANK, namespace, 0, null,
			FragmentConstants.TYPE_PORTLET, new ServiceContext());
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

	private String[] _getActionIds(String roleName) {
		if (Objects.equals(RoleConstants.GUEST, roleName)) {
			if (RandomTestUtil.randomBoolean()) {
				return null;
			}

			return new String[] {ActionKeys.VIEW};
		}

		int random = RandomTestUtil.randomInt(0, 3);

		if (random == 0) {
			return null;
		}

		if (random == 1) {
			return new String[] {ActionKeys.VIEW};
		}

		if (random == 2) {
			return new String[] {ActionKeys.CONFIGURATION, ActionKeys.VIEW};
		}

		return new String[] {
			ActionKeys.ADD_TO_PAGE, ActionKeys.CONFIGURATION, ActionKeys.VIEW
		};
	}

	private CollectionDisplayListStyle _getCollectionDisplayListStyle(
		String listItemStyleClassName, String listStyleClassName,
		ListStyle.ListStyleType listStyleType, String templateKey) {

		if (Validator.isNotNull(templateKey)) {
			TemplateListStyle templateListStyle = new TemplateListStyle();

			templateListStyle.setCollectionDisplayListStyleType(
				CollectionDisplayListStyle.CollectionDisplayListStyleType.
					TEMPLATE);
			templateListStyle.setListItemStyleClassName(listItemStyleClassName);
			templateListStyle.setListStyleClassName(listStyleClassName);
			templateListStyle.setTemplateKey(templateKey);

			return templateListStyle;
		}

		ListStyle listStyle = new ListStyle();

		listStyle.setCollectionDisplayListStyleType(
			CollectionDisplayListStyle.CollectionDisplayListStyleType.
				LIST_STYLE);

		ListStyleDefinition listStyleDefinition = new ListStyleDefinition();

		listStyleDefinition.setAlign(ListStyleDefinition.Align.CENTER);
		listStyleDefinition.setFlexWrap(ListStyleDefinition.FlexWrap.WRAP);
		listStyleDefinition.setGutters(true);
		listStyleDefinition.setJustify(
			ListStyleDefinition.Justify.SPACE_AROUND);
		listStyleDefinition.setNumberOfColumns(12);
		listStyleDefinition.setVerticalAlignment(
			ListStyleDefinition.VerticalAlignment.TOP);

		listStyle.setListStyleDefinition(listStyleDefinition);

		listStyle.setListStyleType(listStyleType);

		return listStyle;
	}

	private PageElement _getCollectionDisplayPageElement(
			CollectionDisplayListStyle collectionDisplayListStyle,
			CollectionDisplayViewport[] collectionDisplayViewports,
			CollectionReference collectionReference, Boolean displayAllItems,
			Boolean displayAllPages,
			Map<String, String> emptyCollectionMessages, Boolean hidden,
			String name, Integer numberOfItems, Integer numberOfItemsPerPage,
			Integer numberOfPages,
			CollectionDisplayPageElementDefinition.PaginationType
				paginationType,
			String pageElementExternalReferenceCode)
		throws Exception {

		CollectionDisplayPageElementDefinition
			collectionDisplayPageElementDefinition =
				new CollectionDisplayPageElementDefinition();

		collectionDisplayPageElementDefinition.setCollectionDisplayListStyle(
			() -> collectionDisplayListStyle);
		collectionDisplayPageElementDefinition.setCollectionDisplayViewports(
			() -> collectionDisplayViewports);
		collectionDisplayPageElementDefinition.setCollectionSettings(
			() -> new CollectionSettings() {
				{
					setCollectionReference(() -> collectionReference);
				}
			});
		collectionDisplayPageElementDefinition.setDisplayAllItems(
			() -> displayAllItems);
		collectionDisplayPageElementDefinition.setDisplayAllPages(
			() -> displayAllPages);
		collectionDisplayPageElementDefinition.setEmptyCollectionConfig(
			() -> {
				if (MapUtil.isEmpty(emptyCollectionMessages)) {
					return null;
				}

				EmptyCollectionConfig emptyCollectionConfig =
					new EmptyCollectionConfig();

				emptyCollectionConfig.setDisplayMessage(true);
				emptyCollectionConfig.setMessage_i18n(
					() -> emptyCollectionMessages);

				return emptyCollectionConfig;
			});
		collectionDisplayPageElementDefinition.setHidden(() -> hidden);
		collectionDisplayPageElementDefinition.setName(() -> name);
		collectionDisplayPageElementDefinition.setNumberOfItems(
			() -> numberOfItems);
		collectionDisplayPageElementDefinition.setNumberOfItemsPerPage(
			() -> numberOfItemsPerPage);
		collectionDisplayPageElementDefinition.setNumberOfPages(
			() -> numberOfPages);
		collectionDisplayPageElementDefinition.setPaginationType(
			() -> paginationType);
		collectionDisplayPageElementDefinition.setType(
			PageElementDefinition.Type.COLLECTION_DISPLAY);

		return _getPageElement(
			collectionDisplayPageElementDefinition,
			pageElementExternalReferenceCode);
	}

	private CollectionDisplayViewport[] _getCollectionDisplayViewports() {
		return new CollectionDisplayViewport[] {
			new CollectionDisplayViewport() {
				{
					setCollectionDisplayViewportDefinition(
						() -> new CollectionDisplayViewportDefinition() {
							{
								setAlign(Align.START);
								setFlexWrap(FlexWrap.WRAP_REVERSE);
								setHidden(false);
								setJustify(Justify.CENTER);
								setNumberOfColumns(1);
							}
						});
					setId(Id.LANDSCAPE_MOBILE);
				}
			},
			new CollectionDisplayViewport() {
				{
					setCollectionDisplayViewportDefinition(
						() -> new CollectionDisplayViewportDefinition() {
							{
								setAlign(Align.CENTER);
								setFlexWrap(FlexWrap.NO_WRAP);
								setHidden(true);
								setJustify(Justify.SPACE_AROUND);
								setNumberOfColumns(4);
							}
						});
					setId(Id.PORTRAIT_MOBILE);
				}
			},
			new CollectionDisplayViewport() {
				{
					setCollectionDisplayViewportDefinition(
						() -> new CollectionDisplayViewportDefinition() {
							{
								setAlign(Align.STRETCH);
								setFlexWrap(FlexWrap.WRAP);
								setHidden(false);
								setJustify(Justify.SPACE_BETWEEN);
								setNumberOfColumns(12);
							}
						});
					setId(Id.TABLET);
				}
			}
		};
	}

	private PageElement _getCollectionItemPageElement(
			String pageElementExternalReferenceCode,
			String parentPageElementExternalReferenceCode,
			PageElement[] pageElements)
		throws Exception {

		CollectionItemPageElementDefinition
			collectionItemPageElementDefinition =
				new CollectionItemPageElementDefinition();

		collectionItemPageElementDefinition.setType(
			PageElementDefinition.Type.COLLECTION_ITEM);

		PageElement pageElement = _getPageElement(
			collectionItemPageElementDefinition,
			pageElementExternalReferenceCode, pageElements);

		pageElement.setParentExternalReferenceCode(
			parentPageElementExternalReferenceCode);

		return pageElement;
	}

	private CollectionReference _getCollectionReference(
		String className, String externalReferenceCode) {

		if (Validator.isNotNull(className)) {
			ClassNameReference classNameReference = new ClassNameReference();

			classNameReference.setClassName(className);
			classNameReference.setCollectionType(
				CollectionReference.CollectionType.COLLECTION_PROVIDER);

			return classNameReference;
		}

		CollectionItemExternalReference collectionItemExternalReference =
			new CollectionItemExternalReference();

		collectionItemExternalReference.setCollectionType(
			CollectionReference.CollectionType.COLLECTION);
		collectionItemExternalReference.setExternalReferenceCode(
			externalReferenceCode);

		return collectionItemExternalReference;
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

	private FormContainerConfig _getFormContainerConfig(
		String className,
		boolean formContainerSubmissionResultDefaultDisplayPage,
		String formContainerSubmissionResultType) {

		FormContainerConfig formContainerConfig = new FormContainerConfig();

		formContainerConfig.setFormContainerReference(
			() -> {
				if (Validator.isNotNull(className)) {
					FormContainerClassSubtypeReference
						formContainerClassSubtypeReference =
							new FormContainerClassSubtypeReference();

					formContainerClassSubtypeReference.setClassName(className);
					formContainerClassSubtypeReference.setType(
						FormContainerReference.Type.
							FORM_CONTAINER_CLASS_SUBTYPE_REFERENCE);

					return formContainerClassSubtypeReference;
				}

				FormContainerContextReference formContainerContextReference =
					new FormContainerContextReference();

				formContainerContextReference.setContextSource(
					() ->
						FormContainerContextReference.ContextSource.
							DISPLAY_PAGE_ITEM);
				formContainerContextReference.setType(
					FormContainerReference.Type.
						FORM_CONTAINER_CONTEXT_REFERENCE);

				return formContainerContextReference;
			});
		formContainerConfig.setFormContainerType(
			FormContainerConfig.FormContainerType.SIMPLE);
		formContainerConfig.setNumberOfSteps(1);
		formContainerConfig.setSuccessFormContainerSubmissionResult(
			() -> _getSuccessFormContainerSubmissionResult(
				className, formContainerSubmissionResultDefaultDisplayPage,
				formContainerSubmissionResultType));

		return formContainerConfig;
	}

	private PageElement _getFormContainerPageElement(
			String className, String[] cssClasses, String customCss,
			boolean formContainerSubmissionResultDefaultDisplayPage,
			String formContainerSubmissionResultType, boolean indexed,
			String pageElementExternalReferenceCode)
		throws Exception {

		FormContainerPageElementDefinition formContainerPageElementDefinition =
			new FormContainerPageElementDefinition();

		formContainerPageElementDefinition.setCssClasses(cssClasses);
		formContainerPageElementDefinition.setCustomCSS(customCss);
		formContainerPageElementDefinition.setFormContainerConfig(
			_getFormContainerConfig(
				className, formContainerSubmissionResultDefaultDisplayPage,
				formContainerSubmissionResultType));
		formContainerPageElementDefinition.setFragmentViewports(
			_getFragmentViewports());
		formContainerPageElementDefinition.setIndexed(indexed);
		formContainerPageElementDefinition.setLayout(
			() -> new com.liferay.headless.admin.site.client.dto.v1_0.Layout() {
				{
					setAlign(Align.END);
					setContentDisplay(ContentDisplay.FLEX_ROW);
					setFlexWrap(FlexWrap.WRAP_REVERSE);
					setJustify(Justify.CENTER);
					setWidthType(WidthType.FIXED);
				}
			});
		formContainerPageElementDefinition.setName(
			RandomTestUtil.randomString());
		formContainerPageElementDefinition.setType(
			PageElementDefinition.Type.FORM_CONTAINER);

		return _getPageElement(
			formContainerPageElementDefinition,
			pageElementExternalReferenceCode);
	}

	private PageElement _getFragmentInstancePageElement(
			String externalReferenceCode,
			FragmentInstancePageElementDefinition
				fragmentInstancePageElementDefinition)
		throws Exception {

		PageElement pageElement = super.randomPageElement();

		pageElement.setExternalReferenceCode(externalReferenceCode);

		pageElement.setPageElementDefinition(
			() -> fragmentInstancePageElementDefinition);

		pageElement.setPageElements(new PageElement[0]);
		pageElement.setParentExternalReferenceCode(StringPool.BLANK);
		pageElement.setPosition(_position);

		return pageElement;
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

	private FragmentMappedValueItemExternalReference
		_getFragmentMappedValueItemExternalReference(
			String className, String externalReferenceCode) {

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

		return _getFragmentMappedValueItemExternalReference(
			className, externalReferenceCode);
	}

	private FragmentViewport[] _getFragmentViewports() {
		return new FragmentViewport[] {
			new FragmentViewport() {
				{
					setCustomCSS(RandomTestUtil.randomString());
					setId(ViewportSize.MOBILE_LANDSCAPE::getViewportSizeId);
				}
			},
			new FragmentViewport() {
				{
					setCustomCSS(RandomTestUtil.randomString());
					setId(ViewportSize.TABLET::getViewportSizeId);
				}
			}
		};
	}

	private PageElement _getGridPageElement(
			String[] cssClasses, String customCss, boolean gutters,
			boolean indexed, Integer modulesPerRow, Integer numberOfModules,
			GridPageElementDefinition.VerticalAlignment verticalAlignment)
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		return _getGridPageElement(
			cssClasses, customCss, gutters, indexed, modulesPerRow,
			numberOfModules, verticalAlignment, externalReferenceCode,
			_getModulePageElements(externalReferenceCode, numberOfModules));
	}

	private PageElement _getGridPageElement(
			String[] cssClasses, String customCss, boolean gutters,
			boolean indexed, Integer modulesPerRow, Integer numberOfModules,
			GridPageElementDefinition.VerticalAlignment verticalAlignment,
			String pageElementExternalReferenceCode, PageElement[] pageElements)
		throws Exception {

		GridPageElementDefinition gridPageElementDefinition =
			new GridPageElementDefinition();

		gridPageElementDefinition.setCssClasses(cssClasses);
		gridPageElementDefinition.setCustomCSS(customCss);
		gridPageElementDefinition.setGridViewports(this::_getGridViewports);
		gridPageElementDefinition.setGutters(gutters);
		gridPageElementDefinition.setIndexed(indexed);
		gridPageElementDefinition.setModulesPerRow(modulesPerRow);
		gridPageElementDefinition.setName(RandomTestUtil.randomString());
		gridPageElementDefinition.setNumberOfModules(numberOfModules);
		gridPageElementDefinition.setReverseOrder(Boolean.FALSE);
		gridPageElementDefinition.setType(PageElementDefinition.Type.GRID);
		gridPageElementDefinition.setVerticalAlignment(verticalAlignment);

		return _getPageElement(
			gridPageElementDefinition, pageElementExternalReferenceCode,
			pageElements);
	}

	private PageElement _getGridPageElementDefaultValues(
			String externalReferenceCode)
		throws Exception {

		return _getPageElement(
			new GridPageElementDefinition() {
				{
					setGridViewports(_getGridViewportsDefaultValues());
					setGutters(Boolean.TRUE);
					setIndexed(Boolean.TRUE);
					setModulesPerRow(1);
					setNumberOfModules(1);
					setReverseOrder(Boolean.FALSE);
					setType(Type.GRID);
				}
			},
			externalReferenceCode,
			new PageElement[] {
				_getPageElement(
					new ModulePageElementDefinition() {
						{
							setModuleViewports(
								_getModuleViewportsDefaultValues());
							setSize(1);
							setType(Type.MODULE);
						}
					},
					externalReferenceCode)
			});
	}

	private GridViewport _getGridViewport(
		GridViewport.Id id, Integer modulesPerRow) {

		GridViewport gridViewport = new GridViewport();

		gridViewport.setCustomCSS(RandomTestUtil.randomString());

		GridViewportDefinition gridViewportDefinition =
			new GridViewportDefinition();

		gridViewportDefinition.setModulesPerRow(modulesPerRow);

		gridViewport.setGridViewportDefinition(() -> gridViewportDefinition);

		gridViewport.setId(id);

		return gridViewport;
	}

	private GridViewport[] _getGridViewports() {
		return new GridViewport[] {
			new GridViewport() {
				{
					setCustomCSS(RandomTestUtil.randomString());
					setGridViewportDefinition(
						() -> new GridViewportDefinition() {
							{
								setModulesPerRow(1);
								setVerticalAlignment(
									GridViewportDefinition.VerticalAlignment.
										TOP);
							}
						});
					setId(Id.LANDSCAPE_MOBILE);
				}
			},
			new GridViewport() {
				{
					setCustomCSS(RandomTestUtil.randomString());
					setGridViewportDefinition(
						() -> new GridViewportDefinition() {
							{
								setModulesPerRow(2);
								setVerticalAlignment(
									GridViewportDefinition.VerticalAlignment.
										TOP);
							}
						});
					setId(Id.PORTRAIT_MOBILE);
				}
			},
			new GridViewport() {
				{
					setCustomCSS(RandomTestUtil.randomString());
					setGridViewportDefinition(
						() -> new GridViewportDefinition() {
							{
								setModulesPerRow(3);
								setVerticalAlignment(
									GridViewportDefinition.VerticalAlignment.
										TOP);
							}
						});
					setId(Id.TABLET);
				}
			}
		};
	}

	private GridViewport[] _getGridViewportsDefaultValues() {
		return new GridViewport[] {
			_getGridViewport(GridViewport.Id.LANDSCAPE_MOBILE, 1),
			_getGridViewport(GridViewport.Id.PORTRAIT_MOBILE, null),
			_getGridViewport(GridViewport.Id.TABLET, null)
		};
	}

	private ItemExternalReference _getItemExternalReference(
		String className, String externalReferenceCode) {

		ItemExternalReference itemExternalReference =
			new ItemExternalReference();

		itemExternalReference.setClassName(className);
		itemExternalReference.setExternalReferenceCode(externalReferenceCode);

		return itemExternalReference;
	}

	private LayoutStructure _getLayoutStructure() {
		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					testGroup.getGroupId(), _draftLayout.getPlid());

		return LayoutStructure.of(
			layoutPageTemplateStructure.getDefaultSegmentsExperienceData());
	}

	private PageElement _getModulePageElement(
			String externalReferenceCode, int numberOfModules,
			String parentExternalReferenceCode, int position)
		throws Exception {

		return _getPageElement(
			new ModulePageElementDefinition() {
				{
					setModuleViewports(_getModuleViewports());
					setSize(12 / numberOfModules);
					setType(Type.MODULE);
				}
			},
			externalReferenceCode, parentExternalReferenceCode, position);
	}

	private PageElement[] _getModulePageElements(
			String externalReferenceCode, int numberOfModules)
		throws Exception {

		PageElement[] pageElements = new PageElement[numberOfModules];

		for (int i = 0; i < numberOfModules; i++) {
			pageElements[i] = _getModulePageElement(
				externalReferenceCode + i, numberOfModules,
				externalReferenceCode, i);
		}

		return pageElements;
	}

	private ModuleViewport[] _getModuleViewports() {
		return new ModuleViewport[] {
			new ModuleViewport() {
				{
					setId(Id.LANDSCAPE_MOBILE);
					setModuleViewportDefinition(
						() -> new ModuleViewportDefinition() {
							{
								setSize(RandomTestUtil.randomInt(1, 12));
							}
						});
				}
			},
			new ModuleViewport() {
				{
					setId(Id.PORTRAIT_MOBILE);
					setModuleViewportDefinition(
						() -> new ModuleViewportDefinition() {
							{
								setSize(RandomTestUtil.randomInt(1, 12));
							}
						});
				}
			},
			new ModuleViewport() {
				{
					setId(Id.TABLET);
					setModuleViewportDefinition(
						() -> new ModuleViewportDefinition() {
							{
								setSize(RandomTestUtil.randomInt(1, 12));
							}
						});
				}
			}
		};
	}

	private ModuleViewport[] _getModuleViewportsDefaultValues() {
		return new ModuleViewport[] {
			new ModuleViewport() {
				{
					setId(Id.LANDSCAPE_MOBILE);
					setModuleViewportDefinition(
						() -> new ModuleViewportDefinition() {
							{
								setSize(12);
							}
						});
				}
			}
		};
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

	private PageElement _getPageElement(
			PageElementDefinition pageElementDefinition,
			String pageElementExternalReferenceCode, PageElement[] pageElements)
		throws Exception {

		PageElement pageElement = _getPageElement(
			pageElementDefinition, pageElementExternalReferenceCode);

		pageElement.setPageElements(pageElements);

		return pageElement;
	}

	private PageElement _getPageElement(
			PageElementDefinition pageElementDefinition,
			String pageElementExternalReferenceCode,
			String parentExternalReferenceCode, int position)
		throws Exception {

		PageElement pageElement = _getPageElement(
			pageElementDefinition, pageElementExternalReferenceCode);

		pageElement.setParentExternalReferenceCode(parentExternalReferenceCode);
		pageElement.setPosition(position);

		return pageElement;
	}

	private FragmentInlineValue _getRandomFragmentInlineValue() {
		return new FragmentInlineValue() {
			{
				setValue_i18n(
					HashMapBuilder.put(
						LocaleUtil.SPAIN.toString(),
						RandomTestUtil.randomString()
					).put(
						LocaleUtil.US.toString(), RandomTestUtil.randomString()
					).build());
			}
		};
	}

	private SuccessFormContainerSubmissionResult
			_getSuccessFormContainerSubmissionResult(
				String className,
				boolean formContainerSubmissionResultDefaultDisplayPage,
				String formContainerSubmissionResultType)
		throws Exception {

		if (StringUtil.equals(
				formContainerSubmissionResultType, "displayPage")) {

			DisplayPageFormContainerSubmissionResult
				displayPageFormContainerSubmissionResult =
					new DisplayPageFormContainerSubmissionResult();

			if (formContainerSubmissionResultDefaultDisplayPage) {
				displayPageFormContainerSubmissionResult.setDefaultDisplayPage(
					() -> Boolean.TRUE);
			}
			else {
				LayoutPageTemplateEntry layoutPageTemplateEntry =
					DisplayPageTemplateTestUtil.addDisplayPageTemplate(
						testGroup.getGroupId(),
						_portal.getClassNameId(className), 0, true,
						WorkflowConstants.STATUS_APPROVED);

				displayPageFormContainerSubmissionResult.
					setItemExternalReference(
						() -> _getItemExternalReference(
							LayoutPageTemplateEntry.class.getName(),
							layoutPageTemplateEntry.
								getExternalReferenceCode()));
			}

			displayPageFormContainerSubmissionResult.
				setSuccessNotificationMessage(
					this::_getSuccessNotificationMessage);
			displayPageFormContainerSubmissionResult.setType(
				SuccessFormContainerSubmissionResult.Type.DISPLAY_PAGE);

			return displayPageFormContainerSubmissionResult;
		}
		else if (StringUtil.equals(
					formContainerSubmissionResultType, "embedded")) {

			EmbeddedMessageFormContainerSubmissionResult
				embeddedMessageFormContainerSubmissionResult =
					new EmbeddedMessageFormContainerSubmissionResult();

			embeddedMessageFormContainerSubmissionResult.setMessage(
				this::_getRandomFragmentInlineValue);
			embeddedMessageFormContainerSubmissionResult.
				setSuccessNotificationMessage(
					this::_getSuccessNotificationMessage);
			embeddedMessageFormContainerSubmissionResult.setType(
				SuccessFormContainerSubmissionResult.Type.EMBEDDED_MESSAGE);

			return embeddedMessageFormContainerSubmissionResult;
		}
		else if (StringUtil.equals(formContainerSubmissionResultType, "none")) {
			StayInPageFormContainerSubmissionResult
				stayInPageFormContainerSubmissionResult =
					new StayInPageFormContainerSubmissionResult();

			stayInPageFormContainerSubmissionResult.
				setSuccessNotificationMessage(
					this::_getSuccessNotificationMessage);
			stayInPageFormContainerSubmissionResult.setType(
				SuccessFormContainerSubmissionResult.Type.STAY_IN_PAGE);

			return stayInPageFormContainerSubmissionResult;
		}
		else if (StringUtil.equals(formContainerSubmissionResultType, "page")) {
			SitePageFormContainerSubmissionResult
				sitePageFormContainerSubmissionResult =
					new SitePageFormContainerSubmissionResult();

			Layout layout = LayoutTestUtil.addTypeContentLayout(testGroup);

			sitePageFormContainerSubmissionResult.setItemExternalReference(
				() -> _getItemExternalReference(
					Layout.class.getName(), layout.getExternalReferenceCode()));

			sitePageFormContainerSubmissionResult.setSuccessNotificationMessage(
				this::_getSuccessNotificationMessage);
			sitePageFormContainerSubmissionResult.setType(
				SuccessFormContainerSubmissionResult.Type.SITE_PAGE);

			return sitePageFormContainerSubmissionResult;
		}
		else if (StringUtil.equals(formContainerSubmissionResultType, "url")) {
			URLFormContainerSubmissionResult urlFormContainerSubmissionResult =
				new URLFormContainerSubmissionResult();

			urlFormContainerSubmissionResult.setType(
				SuccessFormContainerSubmissionResult.Type.URL);
			urlFormContainerSubmissionResult.setUrl(
				this::_getRandomFragmentInlineValue);

			return urlFormContainerSubmissionResult;
		}

		return null;
	}

	private SuccessNotificationMessage _getSuccessNotificationMessage() {
		SuccessNotificationMessage successNotificationMessage =
			new SuccessNotificationMessage();

		successNotificationMessage.setMessage(
			this::_getRandomFragmentInlineValue);
		successNotificationMessage.setShowNotification(true);

		return successNotificationMessage;
	}

	private TreeMap<String, Object> _getWidgetConfig() {
		int random = RandomTestUtil.randomInt(1, 10);

		TreeMap<String, Object> widgetConfig = new TreeMap<>();

		for (int i = 0; i < random; i++) {
			widgetConfig.put(
				RandomTestUtil.randomString(), RandomTestUtil.randomString());
		}

		return widgetConfig;
	}

	private WidgetInstance _getWidgetInstance(
		Map<String, Object> widgetConfig, String widgetInstanceId,
		String widgetName, WidgetPermission[] widgetPermissions) {

		WidgetInstance widgetInstance = new WidgetInstance();

		widgetInstance.setWidgetConfig(widgetConfig);
		widgetInstance.setWidgetInstanceId(widgetInstanceId);
		widgetInstance.setWidgetName(widgetName);
		widgetInstance.setWidgetPermissions(widgetPermissions);

		return widgetInstance;
	}

	private PageElement _getWidgetPageElement(
			String[] cssClasses, String customCss,
			String draftWidgetInstanceExternalReferenceCode, boolean indexed,
			String name, String pageElementExternalReferenceCode,
			Map<String, Object> widgetConfig,
			String widgetInstanceExternalReferenceCode, String widgetInstanceId,
			String widgetName, WidgetPermission[] widgetPermissions)
		throws Exception {

		WidgetInstancePageElementDefinition
			widgetInstancePageElementDefinition =
				new WidgetInstancePageElementDefinition();

		widgetInstancePageElementDefinition.setCssClasses(cssClasses);
		widgetInstancePageElementDefinition.setCustomCSS(customCss);
		widgetInstancePageElementDefinition.
			setDraftWidgetInstanceExternalReferenceCode(
				draftWidgetInstanceExternalReferenceCode);
		widgetInstancePageElementDefinition.setIndexed(indexed);
		widgetInstancePageElementDefinition.setName(name);
		widgetInstancePageElementDefinition.setType(
			PageElementDefinition.Type.WIDGET);
		widgetInstancePageElementDefinition.setWidgetInstance(
			() -> _getWidgetInstance(
				widgetConfig, widgetInstanceId, widgetName, widgetPermissions));
		widgetInstancePageElementDefinition.
			setWidgetInstanceExternalReferenceCode(
				widgetInstanceExternalReferenceCode);

		return _getPageElement(
			widgetInstancePageElementDefinition,
			pageElementExternalReferenceCode);
	}

	private WidgetPermission[] _getWidgetPermissions() {
		return TransformUtil.transformToArray(
			ListUtil.fromArray(
				RoleConstants.GUEST, RoleConstants.SITE_CONTENT_REVIEWER,
				RoleConstants.SITE_MEMBER),
			roleName -> {
				String[] actionIds = _getActionIds(roleName);

				if (actionIds == null) {
					return null;
				}

				WidgetPermission widgetPermission = new WidgetPermission();

				widgetPermission.setActionIds(actionIds);
				widgetPermission.setRoleName(roleName);

				return widgetPermission;
			},
			WidgetPermission.class);
	}

	private FragmentEntry _randomFragmentEntry(
		long fragmentEntryId, String fragmentEntryKey, long groupId) {

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.createFragmentEntry(fragmentEntryId);

		fragmentEntry.setExternalReferenceCode(RandomTestUtil.randomString());
		fragmentEntry.setGroupId(groupId);
		fragmentEntry.setCompanyId(testCompany.getCompanyId());
		fragmentEntry.setFragmentEntryKey(fragmentEntryKey);
		fragmentEntry.setName(RandomTestUtil.randomString());
		fragmentEntry.setCss(StringPool.BLANK);
		fragmentEntry.setHtml(StringPool.BLANK);
		fragmentEntry.setJs(StringPool.BLANK);
		fragmentEntry.setCacheable(RandomTestUtil.randomBoolean());
		fragmentEntry.setConfiguration("{}");
		fragmentEntry.setType(FragmentConstants.TYPE_COMPONENT);
		fragmentEntry.setTypeOptions(StringPool.BLANK);

		return fragmentEntry;
	}

	private FragmentRenderer _randomFragmentRenderer(String key) {
		return new FragmentRenderer() {

			@Override
			public String getCollectionKey() {
				return "content-display";
			}

			@Override
			public String getKey() {
				return key;
			}

			@Override
			public void render(
					FragmentRendererContext fragmentRendererContext,
					HttpServletRequest httpServletRequest,
					HttpServletResponse httpServletResponse)
				throws IOException {
			}

		};
	}

	private PageElement _randomPageElement(
			PageElementDefinition.Type pageElementDefinitionType,
			String parentExternalReferenceCode, PageElement... pageElements)
		throws Exception {

		PageElement pageElement = super.randomPageElement();

		pageElement.setPageElementDefinition(
			() -> PageElementsTestUtil.getPageElementDefinition(
				pageElementDefinitionType, testGroup.getGroupId()));
		pageElement.setPageElements(pageElements);
		pageElement.setParentExternalReferenceCode(parentExternalReferenceCode);

		int position = 0;

		if (Validator.isNull(parentExternalReferenceCode)) {
			position = _position++;
		}

		pageElement.setPosition(position);

		return pageElement;
	}

	private void _testMissingOptionalReference(
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.headless.admin.site.internal.util.LogUtil",
				LoggerTestUtil.WARN)) {

			unsafeRunnable.run();

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			String message = logEntry.getMessage();

			Assert.assertTrue(
				message,
				message.startsWith("Optional reference generated for missing"));
		}
	}

	private PageElement _testPostSitePageSpecificationPageExperiencePageElement(
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

		return postPageElement;
	}

	private void _testPostSitePageSpecificationPageExperiencePageElementWithCollectionDisplayPageElement()
		throws Exception {

		_testPostSitePageSpecificationPageExperiencePageElement(
			_getCollectionDisplayPageElement(
				_getCollectionDisplayListStyle(
					null, null, ListStyle.ListStyleType.FLEX_COLUMN, null),
				_getCollectionDisplayViewports(),
				_getCollectionReference(
					"com.liferay.asset.internal.info.collection.provider." +
						"RecentContentInfoCollectionProvider",
					null),
				true, true,
				HashMapBuilder.put(
					LocaleUtil.SPAIN.toString(), RandomTestUtil.randomString()
				).put(
					LocaleUtil.US.toString(), RandomTestUtil.randomString()
				).build(),
				true, RandomTestUtil.randomString(), RandomTestUtil.randomInt(),
				RandomTestUtil.randomInt(), RandomTestUtil.randomInt(),
				CollectionDisplayPageElementDefinition.PaginationType.NONE,
				RandomTestUtil.randomString()));

		AssetListEntry assetListEntry =
			AssetListEntryLocalServiceUtil.addAssetListEntry(
				null, TestPropsValues.getUserId(), testGroup.getGroupId(),
				RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_DYNAMIC, new ServiceContext());

		PageElement postCollectionDisplayPageElement =
			_testPostSitePageSpecificationPageExperiencePageElement(
				_getCollectionDisplayPageElement(
					_getCollectionDisplayListStyle(
						"com.liferay.asset.internal.info.renderer." +
							"AssetEntryFullContentInfoItemRenderer",
						"com.liferay.asset.info.internal.list.renderer." +
							"NumberedAssetEntryBasicInfoListRenderer",
						ListStyle.ListStyleType.FLEX_ROW,
						RandomTestUtil.randomString()),
					_getCollectionDisplayViewports(),
					_getCollectionReference(
						null, assetListEntry.getExternalReferenceCode()),
					true, true, null, true, RandomTestUtil.randomString(),
					RandomTestUtil.randomInt(), RandomTestUtil.randomInt(),
					RandomTestUtil.randomInt(),
					CollectionDisplayPageElementDefinition.PaginationType.
						SIMPLE,
					RandomTestUtil.randomString()));

		PageElement[] pageElements =
			postCollectionDisplayPageElement.getPageElements();

		PageElement collectionItemPageElement = _getCollectionItemPageElement(
			pageElements[0].getExternalReferenceCode(),
			postCollectionDisplayPageElement.getExternalReferenceCode(),
			new PageElement[0]);

		collectionItemPageElement.setExternalReferenceCode(
			pageElements[0].getExternalReferenceCode());

		_assertProblemException(
			"BAD_REQUEST", null,
			() -> _testPostSitePageSpecificationPageExperiencePageElement(
				collectionItemPageElement));
	}

	private void _testPostSitePageSpecificationPageExperiencePageElementWithContainerPageElement()
		throws Exception {

		_testPostSitePageSpecificationPageExperiencePageElement(
			_getContainerPageElement(
				null, RandomTestUtil.randomString(), null, null,
				"FileEntry_fileName", null, false,
				RandomTestUtil.randomString()));

		FileEntry fileEntry = DLAppLocalServiceUtil.addFileEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			testGroup.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + StringPool.PERIOD +
				ContentTypes.IMAGE_JPEG,
			MimeTypesUtil.getExtensionContentType(ContentTypes.IMAGE_JPEG),
			new byte[0], null, null, null,
			ServiceContextTestUtil.getServiceContext(testGroup.getGroupId()));

		_testPostSitePageSpecificationPageExperiencePageElement(
			_getContainerPageElement(
				null, RandomTestUtil.randomString(), FileEntry.class.getName(),
				fileEntry.getExternalReferenceCode(), "FileEntry_fileName",
				null, false, RandomTestUtil.randomString()));

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			testGroup.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		_testPostSitePageSpecificationPageExperiencePageElement(
			_getContainerPageElement(
				null, RandomTestUtil.randomString(),
				JournalArticle.class.getName(),
				journalArticle.getExternalReferenceCode(),
				"JournalArticle_title", null, false,
				RandomTestUtil.randomString()));

		Layout layout = LayoutTestUtil.addTypeContentLayout(testGroup);

		_testPostSitePageSpecificationPageExperiencePageElement(
			_getContainerPageElement(
				RandomTestUtil.randomStrings(RandomTestUtil.randomInt(1, 10)),
				null, Layout.class.getName(), layout.getExternalReferenceCode(),
				null, null, true, RandomTestUtil.randomString()));

		_testPostSitePageSpecificationPageExperiencePageElement(
			_getContainerPageElement(
				RandomTestUtil.randomStrings(RandomTestUtil.randomInt(1, 10)),
				RandomTestUtil.randomString(), null, null, null,
				HashMapBuilder.put(
					LocaleUtil.SPAIN.toString(), "https://www.liferay.es"
				).put(
					LocaleUtil.US.toString(), "https://www.liferay.com"
				).build(),
				false, RandomTestUtil.randomString()));
	}

	private void _testPostSitePageSpecificationPageExperiencePageElementWithDropZonePageElement()
		throws Exception {

		_testPostSitePageSpecificationPageExperiencePageElement(
			_randomPageElement(
				PageElementDefinition.Type.DROP_ZONE, StringPool.BLANK));
	}

	private void _testPostSitePageSpecificationPageExperiencePageElementWithFormContainerPageElement()
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING, "First Name",
						"firstName")));

		_testPostSitePageSpecificationPageExperiencePageElement(
			_getFormContainerPageElement(
				objectDefinition.getClassName(),
				RandomTestUtil.randomStrings(RandomTestUtil.randomInt(1, 10)),
				RandomTestUtil.randomString(), true, "displayPage", true,
				RandomTestUtil.randomString()));
		_testPostSitePageSpecificationPageExperiencePageElement(
			_getFormContainerPageElement(
				objectDefinition.getClassName(), null, null, false,
				"displayPage", false, RandomTestUtil.randomString()));

		_testPostSitePageSpecificationPageExperiencePageElement(
			_getFormContainerPageElement(
				null,
				RandomTestUtil.randomStrings(RandomTestUtil.randomInt(1, 10)),
				RandomTestUtil.randomString(), false, "embedded", false,
				RandomTestUtil.randomString()));
		_testPostSitePageSpecificationPageExperiencePageElement(
			_getFormContainerPageElement(
				objectDefinition.getClassName(), null, null, false, "none",
				false, RandomTestUtil.randomString()));
		_testPostSitePageSpecificationPageExperiencePageElement(
			_getFormContainerPageElement(
				null,
				RandomTestUtil.randomStrings(RandomTestUtil.randomInt(1, 10)),
				RandomTestUtil.randomString(), false, "page", false,
				RandomTestUtil.randomString()));
		_testPostSitePageSpecificationPageExperiencePageElement(
			_getFormContainerPageElement(
				objectDefinition.getClassName(),
				RandomTestUtil.randomStrings(RandomTestUtil.randomInt(1, 10)),
				RandomTestUtil.randomString(), false, "url", false,
				RandomTestUtil.randomString()));
	}

	private void _testPostSitePageSpecificationPageExperiencePageElementWithFragmentPageElement()
		throws Exception {

		PageElement fragmentPageElement = _randomPageElement(
			PageElementDefinition.Type.FRAGMENT, StringPool.BLANK);

		_testPostSitePageSpecificationPageExperiencePageElement(
			fragmentPageElement);

		_testPostSitePageSpecificationPageExperiencePageElement(
			_randomPageElement(
				PageElementDefinition.Type.FRAGMENT_DROP_ZONE,
				fragmentPageElement.getExternalReferenceCode()));
	}

	private void _testPostSitePageSpecificationPageExperiencePageElementWithGridPageElement()
		throws Exception {

		_testPostSitePageSpecificationPageExperiencePageElement(
			_getGridPageElement(
				RandomTestUtil.randomStrings(RandomTestUtil.randomInt(1, 10)),
				RandomTestUtil.randomString(), false, false, 2, 6,
				GridPageElementDefinition.VerticalAlignment.MIDDLE));
		_testPostSitePageSpecificationPageExperiencePageElement(
			_getGridPageElement(
				null, RandomTestUtil.randomString(), true, true, 1, 3,
				GridPageElementDefinition.VerticalAlignment.TOP));
		_testPostSitePageSpecificationPageExperiencePageElement(
			_getGridPageElement(
				RandomTestUtil.randomStrings(RandomTestUtil.randomInt(1, 10)),
				null, false, false, 6, 12,
				GridPageElementDefinition.VerticalAlignment.BOTTOM));
	}

	private void _testPostSitePageSpecificationPageExperiencePageElementWithWidgetPageElement()
		throws Exception {

		String draftWidgetInstanceExternalReferenceCode =
			RandomTestUtil.randomString();
		String namespace = RandomTestUtil.randomString();

		_addFragmentEntryLink(
			draftWidgetInstanceExternalReferenceCode, namespace);

		_testPostSitePageSpecificationPageExperiencePageElement(
			_getWidgetPageElement(
				RandomTestUtil.randomStrings(RandomTestUtil.randomInt(1, 10)),
				RandomTestUtil.randomString(),
				draftWidgetInstanceExternalReferenceCode, false,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				_getWidgetConfig(), RandomTestUtil.randomString(), namespace,
				JournalContentPortletKeys.JOURNAL_CONTENT,
				_getWidgetPermissions()));
	}

	private PageElement _testPutSitePageSpecificationPageExperiencePageElement(
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

		return putPageElement;
	}

	private void _testPutSitePageSpecificationPageExperiencePageElementWithCollectionDisplayPageElement()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		_testPutSitePageSpecificationPageExperiencePageElement(
			_getCollectionDisplayPageElement(
				_getCollectionDisplayListStyle(
					null, null, ListStyle.ListStyleType.FLEX_COLUMN, null),
				_getCollectionDisplayViewports(),
				_getCollectionReference(
					"com.liferay.asset.internal.info.collection.provider." +
						"RecentContentInfoCollectionProvider",
					null),
				true, true,
				HashMapBuilder.put(
					LocaleUtil.SPAIN.toString(), RandomTestUtil.randomString()
				).put(
					LocaleUtil.US.toString(), RandomTestUtil.randomString()
				).build(),
				true, RandomTestUtil.randomString(), RandomTestUtil.randomInt(),
				RandomTestUtil.randomInt(), RandomTestUtil.randomInt(),
				CollectionDisplayPageElementDefinition.PaginationType.NONE,
				externalReferenceCode));

		AssetListEntry assetListEntry =
			AssetListEntryLocalServiceUtil.addAssetListEntry(
				null, TestPropsValues.getUserId(), testGroup.getGroupId(),
				RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_DYNAMIC, new ServiceContext());

		_testPutSitePageSpecificationPageExperiencePageElement(
			_getCollectionDisplayPageElement(
				_getCollectionDisplayListStyle(
					"com.liferay.asset.internal.info.renderer." +
						"AssetEntryFullContentInfoItemRenderer",
					"com.liferay.asset.info.internal.list.renderer." +
						"NumberedAssetEntryBasicInfoListRenderer",
					ListStyle.ListStyleType.FLEX_ROW,
					RandomTestUtil.randomString()),
				_getCollectionDisplayViewports(),
				_getCollectionReference(
					null, assetListEntry.getExternalReferenceCode()),
				true, true, null, true, RandomTestUtil.randomString(),
				RandomTestUtil.randomInt(), RandomTestUtil.randomInt(),
				RandomTestUtil.randomInt(),
				CollectionDisplayPageElementDefinition.PaginationType.SIMPLE,
				externalReferenceCode));

		PageElement collectionDisplayPageElement =
			_testPutSitePageSpecificationPageExperiencePageElement(
				_getCollectionDisplayPageElement(
					_getCollectionDisplayListStyle(
						null, null, ListStyle.ListStyleType.GRID, null),
					null, null, true, true, null, true,
					RandomTestUtil.randomString(), RandomTestUtil.randomInt(),
					RandomTestUtil.randomInt(), RandomTestUtil.randomInt(),
					CollectionDisplayPageElementDefinition.PaginationType.
						SIMPLE,
					externalReferenceCode));

		PageElement[] collectionDisplayChildPageElements =
			collectionDisplayPageElement.getPageElements();

		_testPutSitePageSpecificationPageExperiencePageElement(
			_getCollectionItemPageElement(
				collectionDisplayChildPageElements[0].
					getExternalReferenceCode(),
				collectionDisplayPageElement.getExternalReferenceCode(),
				new PageElement[0]));
	}

	private void _testPutSitePageSpecificationPageExperiencePageElementWithContainerPageElement()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		_testPutSitePageSpecificationPageExperiencePageElement(
			_getContainerPageElement(
				null, RandomTestUtil.randomString(), null, null,
				"FileEntry_fileName", null, false, externalReferenceCode));

		FileEntry fileEntry = DLAppLocalServiceUtil.addFileEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			testGroup.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + StringPool.PERIOD +
				ContentTypes.IMAGE_JPEG,
			MimeTypesUtil.getExtensionContentType(ContentTypes.IMAGE_JPEG),
			new byte[0], null, null, null,
			ServiceContextTestUtil.getServiceContext(testGroup.getGroupId()));

		_testPutSitePageSpecificationPageExperiencePageElement(
			_getContainerPageElement(
				null, RandomTestUtil.randomString(), FileEntry.class.getName(),
				fileEntry.getExternalReferenceCode(), "FileEntry_fileName",
				null, false, externalReferenceCode));

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			testGroup.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		_testPutSitePageSpecificationPageExperiencePageElement(
			_getContainerPageElement(
				null, RandomTestUtil.randomString(),
				JournalArticle.class.getName(),
				journalArticle.getExternalReferenceCode(),
				"JournalArticle_title", null, false, externalReferenceCode));

		Layout layout = LayoutTestUtil.addTypeContentLayout(testGroup);

		_testPutSitePageSpecificationPageExperiencePageElement(
			_getContainerPageElement(
				RandomTestUtil.randomStrings(RandomTestUtil.randomInt(1, 10)),
				null, Layout.class.getName(), layout.getExternalReferenceCode(),
				null, null, true, externalReferenceCode));

		_testPutSitePageSpecificationPageExperiencePageElement(
			_getContainerPageElement(
				RandomTestUtil.randomStrings(RandomTestUtil.randomInt(1, 10)),
				RandomTestUtil.randomString(), null, null, null,
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

	private void _testPutSitePageSpecificationPageExperiencePageElementWithFormContainerPageElement()
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING, "First Name",
						"firstName")));

		String externalReferenceCode = RandomTestUtil.randomString();

		_testPutSitePageSpecificationPageExperiencePageElement(
			_getFormContainerPageElement(
				objectDefinition.getClassName(),
				RandomTestUtil.randomStrings(RandomTestUtil.randomInt(1, 10)),
				RandomTestUtil.randomString(), true, "displayPage", true,
				externalReferenceCode));
		_testPutSitePageSpecificationPageExperiencePageElement(
			_getFormContainerPageElement(
				objectDefinition.getClassName(), null, null, false,
				"displayPage", false, externalReferenceCode));
		_testPutSitePageSpecificationPageExperiencePageElement(
			_getFormContainerPageElement(
				null,
				RandomTestUtil.randomStrings(RandomTestUtil.randomInt(1, 10)),
				RandomTestUtil.randomString(), false, "embedded", false,
				externalReferenceCode));
		_testPutSitePageSpecificationPageExperiencePageElement(
			_getFormContainerPageElement(
				objectDefinition.getClassName(), null, null, false, "none",
				false, externalReferenceCode));
		_testPutSitePageSpecificationPageExperiencePageElement(
			_getFormContainerPageElement(
				null,
				RandomTestUtil.randomStrings(RandomTestUtil.randomInt(1, 10)),
				RandomTestUtil.randomString(), false, "page", false,
				externalReferenceCode));
		_testPutSitePageSpecificationPageExperiencePageElement(
			_getFormContainerPageElement(
				objectDefinition.getClassName(),
				RandomTestUtil.randomStrings(RandomTestUtil.randomInt(1, 10)),
				RandomTestUtil.randomString(), false, "url", false,
				externalReferenceCode));
		_testPutSitePageSpecificationPageExperiencePageElement(
			_getPageElement(
				new FormContainerPageElementDefinition() {
					{
						setIndexed(true);
						setType(Type.FORM_CONTAINER);
					}
				},
				externalReferenceCode));
	}

	private void _testPutSitePageSpecificationPageExperiencePageElementWithFragmentPageElement()
		throws Exception {

		_testPutSitePageSpecificationPageExperiencePageElement(
			_randomPageElement(
				PageElementDefinition.Type.FRAGMENT, StringPool.BLANK));

		String externalReferenceCode = RandomTestUtil.randomString();

		_testPutSitePageSpecificationPageExperiencePageElement(
			_getFragmentInstancePageElement(
				externalReferenceCode,
				PageElementsTestUtil.getFragmentInstancePageElementDefinition(
					Collections.emptyMap(), "BASIC_COMPONENT-button",
					testGroup.getGroupId())));
		_testPutSitePageSpecificationPageExperiencePageElement(
			_getFragmentInstancePageElement(
				externalReferenceCode,
				PageElementsTestUtil.getFragmentInstancePageElementDefinition(
					Collections.emptyMap(),
					"com.liferay.fragment.internal.renderer." +
						"ContentObjectFragmentRenderer",
					testGroup.getGroupId())));

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(testGroup.getGroupId());

		_testPutSitePageSpecificationPageExperiencePageElement(
			_getFragmentInstancePageElement(
				externalReferenceCode,
				PageElementsTestUtil.getFragmentInstancePageElementDefinition(
					Collections.emptyMap(),
					_addFragmentEntry(
						null, irrelevantGroup.getGroupId(), serviceContext),
					testGroup.getGroupId())));
		_testPutSitePageSpecificationPageExperiencePageElement(
			_getFragmentInstancePageElement(
				externalReferenceCode,
				PageElementsTestUtil.getFragmentInstancePageElementDefinition(
					Collections.emptyMap(),
					_addFragmentEntry(
						null, testGroup.getGroupId(), serviceContext),
					testGroup.getGroupId())));

		_testPutSitePageSpecificationPageExperiencePageElementWithFragmentPageElementWithConfiguration();

		_testPutSitePageSpecificationPageExperiencePageElementWithFragmentPageElementAndMissingOptionalReference(
			externalReferenceCode,
			_randomFragmentEntry(
				RandomTestUtil.randomLong(), StringPool.BLANK,
				irrelevantGroup.getGroupId()));
		_testPutSitePageSpecificationPageExperiencePageElementWithFragmentPageElementAndMissingOptionalReference(
			externalReferenceCode,
			_randomFragmentEntry(
				RandomTestUtil.randomLong(), StringPool.BLANK,
				testGroup.getGroupId()));
		_testPutSitePageSpecificationPageExperiencePageElementWithFragmentPageElementAndMissingOptionalReference(
			externalReferenceCode,
			_randomFragmentRenderer(RandomTestUtil.randomString()));
	}

	private void
			_testPutSitePageSpecificationPageExperiencePageElementWithFragmentPageElementAndMissingOptionalReference(
				String externalReferenceCode, FragmentEntry fragmentEntry)
		throws Exception {

		_testMissingOptionalReference(
			() -> _testPutSitePageSpecificationPageExperiencePageElement(
				_getFragmentInstancePageElement(
					externalReferenceCode,
					PageElementsTestUtil.
						getFragmentInstancePageElementDefinition(
							Collections.emptyMap(), fragmentEntry,
							testGroup.getGroupId()))));
	}

	private void
			_testPutSitePageSpecificationPageExperiencePageElementWithFragmentPageElementAndMissingOptionalReference(
				String externalReferenceCode, FragmentRenderer fragmentRenderer)
		throws Exception {

		_testMissingOptionalReference(
			() -> _testPutSitePageSpecificationPageExperiencePageElement(
				_getFragmentInstancePageElement(
					externalReferenceCode,
					PageElementsTestUtil.
						getFragmentInstancePageElementDefinition(
							Collections.emptyMap(), fragmentRenderer))));
	}

	private void _testPutSitePageSpecificationPageExperiencePageElementWithFragmentPageElementWithConfiguration()
		throws Exception {

		String checkboxFieldName = RandomTestUtil.randomString();
		String lengthFieldName = RandomTestUtil.randomString();
		String selectFieldName = RandomTestUtil.randomString();

		String selectValue1 = RandomTestUtil.randomString();
		String selectValue2 = RandomTestUtil.randomString();
		String selectValue3 = RandomTestUtil.randomString();

		JSONObject typeOptionsJSONObject = JSONUtil.put(
			"validValues",
			JSONUtil.putAll(
				JSONUtil.put(
					"label", RandomTestUtil.randomString()
				).put(
					"value", selectValue1
				),
				JSONUtil.put(
					"label", RandomTestUtil.randomString()
				).put(
					"value", selectValue2
				),
				JSONUtil.put(
					"label", RandomTestUtil.randomString()
				).put(
					"value", selectValue3
				)));

		String textFieldName = RandomTestUtil.randomString();

		_testPutSitePageSpecificationPageExperiencePageElementWithFragmentPageElementWithConfiguration(
			FragmentConfigurationTestUtil.getConfiguration(
				HashMapBuilder.<String, Map<String, Object>>put(
					checkboxFieldName,
					HashMapBuilder.<String, Object>put(
						"defaultValue", RandomTestUtil.randomBoolean()
					).put(
						"type", "checkbox"
					).build()
				).put(
					lengthFieldName,
					HashMapBuilder.<String, Object>put(
						"defaultValue", RandomTestUtil.randomString()
					).put(
						"type", "length"
					).build()
				).put(
					selectFieldName,
					HashMapBuilder.<String, Object>put(
						"defaultValue", selectValue3
					).put(
						"type", "select"
					).put(
						"typeOptions", typeOptionsJSONObject
					).build()
				).put(
					textFieldName,
					HashMapBuilder.<String, Object>put(
						"defaultValue", RandomTestUtil.randomBoolean()
					).put(
						"type", "text"
					).build()
				).build()),
			HashMapBuilder.<String, Object>put(
				checkboxFieldName, RandomTestUtil.randomBoolean()
			).put(
				lengthFieldName, RandomTestUtil.randomString()
			).put(
				selectFieldName, selectValue1
			).put(
				textFieldName, RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.<String, Object>put(
				checkboxFieldName, RandomTestUtil.randomBoolean()
			).put(
				lengthFieldName, RandomTestUtil.randomString()
			).put(
				selectFieldName, selectValue2
			).put(
				textFieldName, RandomTestUtil.randomString()
			).build(),
			Collections.emptyMap());

		_testPutSitePageSpecificationPageExperiencePageElementWithFragmentPageElementWithConfiguration(
			FragmentConfigurationTestUtil.getConfiguration(
				HashMapBuilder.<String, Map<String, Object>>put(
					checkboxFieldName,
					HashMapBuilder.<String, Object>put(
						"defaultValue", RandomTestUtil.randomBoolean()
					).put(
						"localized", true
					).put(
						"type", "checkbox"
					).build()
				).put(
					lengthFieldName,
					HashMapBuilder.<String, Object>put(
						"defaultValue", RandomTestUtil.randomString()
					).put(
						"localized", true
					).put(
						"type", "length"
					).build()
				).put(
					selectFieldName,
					HashMapBuilder.<String, Object>put(
						"defaultValue", selectValue3
					).put(
						"localized", true
					).put(
						"type", "select"
					).put(
						"typeOptions", typeOptionsJSONObject
					).build()
				).put(
					textFieldName,
					HashMapBuilder.<String, Object>put(
						"defaultValue", RandomTestUtil.randomBoolean()
					).put(
						"localized", true
					).put(
						"type", "text"
					).build()
				).build()),
			HashMapBuilder.<String, Object>put(
				checkboxFieldName, RandomTestUtil.randomBoolean()
			).put(
				lengthFieldName, RandomTestUtil.randomString()
			).put(
				selectFieldName, selectValue1
			).put(
				textFieldName, RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.<String, Object>put(
				checkboxFieldName, RandomTestUtil.randomBoolean()
			).put(
				lengthFieldName, RandomTestUtil.randomString()
			).put(
				selectFieldName, selectValue2
			).put(
				textFieldName, RandomTestUtil.randomString()
			).build(),
			Collections.emptyMap());

		_assertProblemException(
			"BAD_REQUEST", null,
			() ->
				_testPutSitePageSpecificationPageExperiencePageElementWithFragmentPageElementWithConfiguration(
					FragmentConfigurationTestUtil.getConfiguration(
						HashMapBuilder.<String, Map<String, Object>>put(
							selectFieldName,
							HashMapBuilder.<String, Object>put(
								"defaultValue", selectValue3
							).put(
								"type", "select"
							).put(
								"typeOptions", typeOptionsJSONObject
							).build()
						).build()),
					HashMapBuilder.<String, Object>put(
						selectFieldName, RandomTestUtil.randomString()
					).build()));
	}

	private void
			_testPutSitePageSpecificationPageExperiencePageElementWithFragmentPageElementWithConfiguration(
				String configuration,
				Map<String, Object>... configurationValuesMaps)
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		FragmentEntry fragmentEntry = _addFragmentEntry(
			configuration, testGroup.getGroupId(),
			ServiceContextTestUtil.getServiceContext(testGroup.getGroupId()));

		for (Map<String, Object> configurationValuesMap :
				configurationValuesMaps) {

			_testPutSitePageSpecificationPageExperiencePageElement(
				_getFragmentInstancePageElement(
					externalReferenceCode,
					PageElementsTestUtil.
						getFragmentInstancePageElementDefinition(
							configurationValuesMap, fragmentEntry,
							testGroup.getGroupId())));
		}
	}

	private void _testPutSitePageSpecificationPageExperiencePageElementWithGridPageElement()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		_testPutSitePageSpecificationPageExperiencePageElement(
			_getGridPageElement(
				RandomTestUtil.randomStrings(RandomTestUtil.randomInt(1, 10)),
				RandomTestUtil.randomString(), false, false, 2, 6,
				GridPageElementDefinition.VerticalAlignment.MIDDLE,
				externalReferenceCode,
				_getModulePageElements(externalReferenceCode, 6)));
		_testPutSitePageSpecificationPageExperiencePageElement(
			_getGridPageElement(
				null, RandomTestUtil.randomString(), true, true, 1, 3,
				GridPageElementDefinition.VerticalAlignment.TOP,
				externalReferenceCode,
				_getModulePageElements(externalReferenceCode, 3)));
		_testPutSitePageSpecificationPageExperiencePageElement(
			_getGridPageElement(
				RandomTestUtil.randomStrings(RandomTestUtil.randomInt(1, 10)),
				null, false, false, 6, 12,
				GridPageElementDefinition.VerticalAlignment.BOTTOM,
				externalReferenceCode,
				_getModulePageElements(externalReferenceCode, 12)));
		_testPutSitePageSpecificationPageExperiencePageElement(
			_getGridPageElementDefaultValues(externalReferenceCode));
	}

	private void _testPutSitePageSpecificationPageExperiencePageElementWithWidgetPageElement()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		String widgetInstanceExternalReferenceCode =
			RandomTestUtil.randomString();

		_testPutSitePageSpecificationPageExperiencePageElement(
			_getWidgetPageElement(
				RandomTestUtil.randomStrings(RandomTestUtil.randomInt(1, 10)),
				RandomTestUtil.randomString(), null, false,
				RandomTestUtil.randomString(), externalReferenceCode,
				_getWidgetConfig(), widgetInstanceExternalReferenceCode,
				RandomTestUtil.randomString(),
				JournalContentPortletKeys.JOURNAL_CONTENT,
				_getWidgetPermissions()));

		String draftWidgetInstanceExternalReferenceCode =
			RandomTestUtil.randomString();
		String namespace = RandomTestUtil.randomString();

		_addFragmentEntryLink(
			draftWidgetInstanceExternalReferenceCode, namespace);

		_testPutSitePageSpecificationPageExperiencePageElement(
			_getWidgetPageElement(
				RandomTestUtil.randomStrings(RandomTestUtil.randomInt(1, 10)),
				RandomTestUtil.randomString(),
				draftWidgetInstanceExternalReferenceCode, false,
				RandomTestUtil.randomString(), externalReferenceCode,
				_getWidgetConfig(), widgetInstanceExternalReferenceCode,
				namespace, AssetPublisherPortletKeys.ASSET_PUBLISHER,
				_getWidgetPermissions()));
		_testPutSitePageSpecificationPageExperiencePageElement(
			_getWidgetPageElement(
				null, null, null, false, RandomTestUtil.randomString(),
				externalReferenceCode, new HashMap<>(),
				widgetInstanceExternalReferenceCode, namespace,
				AssetPublisherPortletKeys.ASSET_PUBLISHER,
				new WidgetPermission[0]));
	}

	private Layout _draftLayout;

	@Inject
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Inject
	private JSONFactory _jsonFactory;

	private Layout _layout;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private Portal _portal;

	private int _position;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}