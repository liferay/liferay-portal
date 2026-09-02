/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.asset.list.constants.AssetListEntryTypeConstants;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.service.AssetListEntryLocalService;
import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.FragmentEntryProcessorRegistry;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.fragment.renderer.constants.FragmentRendererConstants;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.headless.admin.site.client.dto.v1_0.BackgroundImageValue;
import com.liferay.headless.admin.site.client.dto.v1_0.BasicFragmentInstancePageElementDefinition;
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
import com.liferay.headless.admin.site.client.dto.v1_0.ContextualMenuNavigationMenuValue;
import com.liferay.headless.admin.site.client.dto.v1_0.DefaultFragmentReference;
import com.liferay.headless.admin.site.client.dto.v1_0.DirectBackgroundImageValue;
import com.liferay.headless.admin.site.client.dto.v1_0.DisplayPageFormContainerSubmissionResult;
import com.liferay.headless.admin.site.client.dto.v1_0.EmbeddedMessageFormContainerSubmissionResult;
import com.liferay.headless.admin.site.client.dto.v1_0.EmptyCollectionConfig;
import com.liferay.headless.admin.site.client.dto.v1_0.FormContainerClassSubtypeReference;
import com.liferay.headless.admin.site.client.dto.v1_0.FormContainerConfig;
import com.liferay.headless.admin.site.client.dto.v1_0.FormContainerContextReference;
import com.liferay.headless.admin.site.client.dto.v1_0.FormContainerPageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.FormContainerReference;
import com.liferay.headless.admin.site.client.dto.v1_0.FormRelationshipConfig;
import com.liferay.headless.admin.site.client.dto.v1_0.FormRelationshipPageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.FormStepContainerPageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.FormStepPageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentEditableElement;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentEditableElementValue;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentEditableElementValueFragmentLink;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentInlineValue;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentInstance;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentLink;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentLinkInlineValue;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentLinkMappedValue;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentLinkValue;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentMappedValue;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentMappedValueItemContextReference;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentMappedValueItemExternalReference;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentMappedValueItemReference;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentReference;
import com.liferay.headless.admin.site.client.dto.v1_0.GridPageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.GridViewport;
import com.liferay.headless.admin.site.client.dto.v1_0.GridViewportDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.HTMLFragmentValue;
import com.liferay.headless.admin.site.client.dto.v1_0.HtmlProperties;
import com.liferay.headless.admin.site.client.dto.v1_0.ImageValue;
import com.liferay.headless.admin.site.client.dto.v1_0.ItemExternalReference;
import com.liferay.headless.admin.site.client.dto.v1_0.ItemImageValue;
import com.liferay.headless.admin.site.client.dto.v1_0.ListStyle;
import com.liferay.headless.admin.site.client.dto.v1_0.ListStyleDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.LocalizationConfig;
import com.liferay.headless.admin.site.client.dto.v1_0.MappedBackgroundImageValue;
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
import com.liferay.headless.admin.site.client.dto.v1_0.TextFragmentValue;
import com.liferay.headless.admin.site.client.dto.v1_0.URLFormContainerSubmissionResult;
import com.liferay.headless.admin.site.client.dto.v1_0.URLImageValue;
import com.liferay.headless.admin.site.client.dto.v1_0.WidgetInstance;
import com.liferay.headless.admin.site.client.dto.v1_0.WidgetInstancePageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.WidgetPermission;
import com.liferay.headless.admin.site.client.problem.Problem;
import com.liferay.headless.admin.site.client.scope.Scope;
import com.liferay.headless.admin.site.client.serdes.v1_0.PageElementSerDes;
import com.liferay.headless.admin.site.resource.v1_0.test.util.FragmentConfigurationFieldValueTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.FragmentConfigurationTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.FragmentEditableElementTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.FragmentMappedValueTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.FragmentViewportStyleTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.FragmentViewportTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.ImageValueTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.PageElementsTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.ReferencesTestUtil;
import com.liferay.journal.constants.JournalContentPortletKeys;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.layout.util.structure.StyledLayoutStructureItem;
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
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.GroupedModel;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ScopeUtil;
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
import com.liferay.site.navigation.menu.item.layout.constants.SiteNavigationMenuItemTypeConstants;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.service.SiteNavigationMenuItemLocalService;
import com.liferay.site.navigation.service.SiteNavigationMenuLocalService;

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
@FeatureFlag("LPD-74328")
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
	@TestInfo({"LPD-83090", "LPD-85565", "LPD-104316"})
	public void testPostSitePageSpecificationPageExperiencePageElement()
		throws Exception {

		super.testPostSitePageSpecificationPageExperiencePageElement();

		_testPostSitePageSpecificationPageExperiencePageElementWithCollectionDisplayPageElement();
		_testPostSitePageSpecificationPageExperiencePageElementWithContainerPageElement();

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING, "First Name",
						"firstName")));

		_testPostSitePageSpecificationPageExperiencePageElementWithFormContainerPageElement(
			objectDefinition);
		_testPostSitePageSpecificationPageExperiencePageElementWithFormRelationshipPageElement(
			objectDefinition);

		_testPostSitePageSpecificationPageExperiencePageElementWithFragmentPageElement();
		_testPostSitePageSpecificationPageExperiencePageElementWithGridPageElement();
		_testPostSitePageSpecificationPageExperiencePageElementWithNonexistentWidgetPermissionRole();
		_testPostSitePageSpecificationPageExperiencePageElementWithWidgetPageElement();
	}

	@Override
	@Test
	@TestInfo({"LPD-83090", "LPD-85565"})
	public void testPutSitePageSpecificationPageExperiencePageElement()
		throws Exception {

		_testPutSitePageSpecificationPageExperiencePageElementWithCollectionDisplayPageElement();
		_testPutSitePageSpecificationPageExperiencePageElementWithContainerPageElement();

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING, "First Name",
						"firstName")));

		_testPutSitePageSpecificationPageExperiencePageElementWithFormContainerPageElement(
			objectDefinition);
		_testPutSitePageSpecificationPageExperiencePageElementWithFormRelationshipPageElement(
			objectDefinition);

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

		return _addFragmentEntry(
			configuration, groupId,
			StringBundler.concat(
				"<button data-lfr-editable-id=\"element-action1\" ",
				"data-lfr-editable-type=\"action\">",
				RandomTestUtil.randomString(), "</button>",
				"<button data-lfr-editable-id=\"element-action2\" ",
				"data-lfr-editable-type=\"action\">",
				RandomTestUtil.randomString(), "</button>",
				"<div data-lfr-background-image-id=\"element-background-",
				"image1\">", RandomTestUtil.randomString(), "</div>",
				"<div data-lfr-background-image-id=\"element-background-",
				"image2\">", RandomTestUtil.randomString(), "</div>",
				"<time data-lfr-editable-id=\"element-date\" ",
				"data-lfr-editable-type=\"date-time\">",
				RandomTestUtil.randomString(), "</time>",
				"<div data-lfr-editable-id=\"element-html\" ",
				"data-lfr-editable-type=\"html\">",
				RandomTestUtil.randomString(), "</div>",
				"<img data-lfr-editable-id=\"element-image1\" ",
				"data-lfr-editable-type=\"image\">",
				"<img data-lfr-editable-id=\"element-image2\" ",
				"data-lfr-editable-type=\"image\">",
				"<img data-lfr-editable-id=\"element-image3\" ",
				"data-lfr-editable-type=\"image\">",
				RandomTestUtil.randomString(), "</div>",
				"<a data-lfr-editable-id=\"element-link\" ",
				"data-lfr-editable-type=\"link\"  href=\"\">",
				RandomTestUtil.randomString(), "</a>",
				"<div data-lfr-editable-id=\"element-rich-text\" ",
				"data-lfr-editable-type=\"rich-text\">",
				RandomTestUtil.randomString(), "</div>",
				"<h1 data-lfr-editable-id=\"element-text\" ",
				"data-lfr-editable-type=\"text\">",
				RandomTestUtil.randomString(), "</h1>"),
			serviceContext);
	}

	private FragmentEntry _addFragmentEntry(
			String configuration, long groupId, String html,
			ServiceContext serviceContext)
		throws Exception {

		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.addFragmentCollection(
				null, TestPropsValues.getUserId(), groupId,
				StringUtil.randomString(), StringPool.BLANK, serviceContext);

		return _fragmentEntryLocalService.addFragmentEntry(
			null, TestPropsValues.getUserId(), groupId,
			fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			StringPool.BLANK, html, StringPool.BLANK, false, configuration,
			null, 0, false, false, FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, serviceContext);
	}

	private void _addFragmentEntryLink(
			String externalReferenceCode, String namespace)
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(testGroup);

		_fragmentEntryLinkLocalService.addFragmentEntryLink(
			externalReferenceCode, TestPropsValues.getUserId(),
			testGroup.getGroupId(), null, null, null, 0, layout.getPlid(),
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, StringPool.BLANK, namespace, 0, null,
			FragmentConstants.TYPE_PORTLET, new ServiceContext());
	}

	private void _assertDefaultValues(
			BasicFragmentInstancePageElementDefinition
				basicFragmentInstancePageElementDefinition,
			String... keys)
		throws Exception {

		FragmentInstance fragmentInstance =
			basicFragmentInstancePageElementDefinition.getFragmentInstance();

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.
				getFragmentEntryLinkByExternalReferenceCode(
					fragmentInstance.getFragmentInstanceExternalReferenceCode(),
					testGroup.getGroupId());

		JSONObject defaultEditableValuesJSONObject =
			_fragmentEntryProcessorRegistry.getDefaultEditableValuesJSONObject(
				fragmentEntryLink.getHtml(),
				fragmentEntryLink.getConfigurationJSONObject());

		JSONObject defaultEditableJSONObject =
			defaultEditableValuesJSONObject.getJSONObject(
				FragmentEntryProcessorConstants.
					KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR);

		JSONObject editableValuesJSONObject =
			fragmentEntryLink.getEditableValuesJSONObject();

		JSONObject editableJSONObject = editableValuesJSONObject.getJSONObject(
			FragmentEntryProcessorConstants.
				KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR);

		for (String key : keys) {
			JSONObject defaultElementJSONObject =
				defaultEditableJSONObject.getJSONObject(key);

			Object defaultValue = defaultElementJSONObject.get("defaultValue");

			Assert.assertTrue(
				defaultElementJSONObject.toString(),
				Validator.isNotNull(defaultValue));

			JSONObject elementJSONObject = editableJSONObject.getJSONObject(
				key);

			Assert.assertEquals(
				defaultValue, elementJSONObject.get("defaultValue"));
		}
	}

	private void _assertDirectBackgroundImageJSONObject(
			long classPK, DirectBackgroundImageValue directBackgroundImageValue,
			FileEntry fileEntry, JSONObject jsonObject)
		throws Exception {

		ImageValue imageValue = directBackgroundImageValue.getImageValue();

		if (Objects.equals(ImageValue.Type.URL, imageValue.getType())) {
			URLImageValue urlImageValue = (URLImageValue)imageValue;

			Assert.assertEquals(
				urlImageValue.getUrl(), jsonObject.getString("url"));

			return;
		}

		ItemImageValue itemImageValue = (ItemImageValue)imageValue;

		ItemExternalReference itemExternalReference =
			itemImageValue.getItemExternalReference();

		_assertItemExternalReferenceJSONObject(
			FileEntry.class.getName(), classPK,
			itemExternalReference.getExternalReferenceCode(), fileEntry,
			jsonObject, itemExternalReference.getScope());

		if (fileEntry == null) {
			return;
		}

		Assert.assertEquals(
			fileEntry.getExternalReferenceCode(),
			jsonObject.getString("externalReferenceCode"));
		Assert.assertEquals(
			fileEntry.getFileEntryId(), jsonObject.getLong("fileEntryId"));
		Assert.assertEquals(
			fileEntry.getTitle(), jsonObject.getString("title"));
		Assert.assertEquals(
			_dlURLHelper.getPreviewURL(
				fileEntry, fileEntry.getFileVersion(), null, StringPool.BLANK,
				false, false),
			jsonObject.getString("url"));
	}

	private void _assertItemExternalReferenceJSONObject(
			String className, long classPK, String externalReferenceCode,
			GroupedModel groupedModel, JSONObject jsonObject, Scope scope)
		throws Exception {

		Assert.assertEquals(className, jsonObject.getString("className"));
		Assert.assertEquals(
			_portal.getClassNameId(className),
			jsonObject.getLong("classNameId"));
		Assert.assertEquals(classPK, jsonObject.getLong("classPK"));

		Assert.assertEquals(
			externalReferenceCode,
			jsonObject.getString("externalReferenceCode"));

		if (groupedModel != null) {
			Assert.assertEquals(
				GetterUtil.getString(
					ScopeUtil.getItemScopeExternalReferenceCode(
						groupedModel.getGroupId(), _layout.getGroupId())),
				jsonObject.getString("scopeExternalReferenceCode"));

			return;
		}

		if (scope != null) {
			Assert.assertEquals(
				scope.getExternalReferenceCode(),
				jsonObject.getString("scopeExternalReferenceCode"));

			return;
		}

		String scopeExternalReferenceCode = jsonObject.getString(
			"scopeExternalReferenceCode");

		Assert.assertTrue(
			scopeExternalReferenceCode,
			Validator.isNull(scopeExternalReferenceCode));
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

	private void _assertStyledLayoutStructureItemBackgroundImage(
			BackgroundImageValue backgroundImageValue, long classPK,
			GroupedModel groupedModel, PageElement pageElement)
		throws Exception {

		_assertStyledLayoutStructureItemBackgroundImage(
			backgroundImageValue, classPK, groupedModel,
			pageElement.getExternalReferenceCode());

		for (PageElement childPageElement : pageElement.getPageElements()) {
			_assertStyledLayoutStructureItemBackgroundImage(
				backgroundImageValue, classPK, groupedModel,
				childPageElement.getExternalReferenceCode());
		}
	}

	private void _assertStyledLayoutStructureItemBackgroundImage(
			BackgroundImageValue backgroundImageValue, long classPK,
			GroupedModel groupedModel, String itemId)
		throws Exception {

		LayoutStructure layoutStructure = _getLayoutStructure();

		LayoutStructureItem layoutStructureItem =
			layoutStructure.getLayoutStructureItem(itemId);

		Assert.assertTrue(
			layoutStructureItem instanceof StyledLayoutStructureItem);

		StyledLayoutStructureItem styledLayoutStructureItem =
			(StyledLayoutStructureItem)layoutStructureItem;

		JSONObject backgroundImageJSONObject =
			styledLayoutStructureItem.getBackgroundImageJSONObject();

		if (backgroundImageValue == null) {
			Assert.assertEquals(
				backgroundImageJSONObject.toString(), 0,
				backgroundImageJSONObject.length());

			return;
		}

		if (Objects.equals(
				BackgroundImageValue.Type.DIRECT,
				backgroundImageValue.getType())) {

			_assertDirectBackgroundImageJSONObject(
				classPK, (DirectBackgroundImageValue)backgroundImageValue,
				(FileEntry)groupedModel, backgroundImageJSONObject);

			return;
		}

		MappedBackgroundImageValue mappedBackgroundImageValue =
			(MappedBackgroundImageValue)backgroundImageValue;

		FragmentMappedValue fragmentMappedValue =
			mappedBackgroundImageValue.getFragmentMappedValue();

		Mapping mapping = fragmentMappedValue.getMapping();

		if (mapping == null) {
			Assert.assertEquals(
				backgroundImageJSONObject.toString(), 0,
				backgroundImageJSONObject.length());

			return;
		}

		FragmentMappedValueItemReference fragmentMappedValueItemReference =
			mapping.getItemReference();

		if (fragmentMappedValueItemReference == null) {
			Assert.assertEquals(
				backgroundImageJSONObject.toString(), 0,
				backgroundImageJSONObject.length());

			return;
		}

		String fieldKey = mapping.getFieldKey();

		if (Validator.isNull(fieldKey)) {
			Assert.assertEquals(
				backgroundImageJSONObject.toString(), 0,
				backgroundImageJSONObject.length());

			return;
		}

		if (Objects.equals(
				FragmentMappedValueItemReference.Type.ITEM_EXTERNAL_REFERENCE,
				fragmentMappedValueItemReference.getType())) {

			FragmentMappedValueItemExternalReference
				fragmentMappedValueItemExternalReference =
					(FragmentMappedValueItemExternalReference)
						fragmentMappedValueItemReference;

			_assertItemExternalReferenceJSONObject(
				fragmentMappedValueItemExternalReference.getClassName(),
				classPK,
				fragmentMappedValueItemExternalReference.
					getExternalReferenceCode(),
				groupedModel, backgroundImageJSONObject,
				fragmentMappedValueItemExternalReference.getScope());

			return;
		}

		String key = "collectionFieldId";

		FragmentMappedValueItemContextReference
			fragmentMappedValueItemContextReference =
				(FragmentMappedValueItemContextReference)
					fragmentMappedValueItemReference;

		if (Objects.equals(
				FragmentMappedValueItemContextReference.ContextSource.
					DISPLAY_PAGE_ITEM,
				fragmentMappedValueItemContextReference.getContextSource())) {

			key = "mappedField";
		}

		Assert.assertEquals(fieldKey, backgroundImageJSONObject.getString(key));
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

	private AssetCategory _getAssetCategory(long groupId) throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), groupId,
				RandomTestUtil.randomString(), serviceContext);

		return _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), groupId, RandomTestUtil.randomString(),
			assetVocabulary.getVocabularyId(), serviceContext);
	}

	private AssetListEntry _getAssetListEntry(long groupId) throws Exception {
		return _assetListEntryLocalService.addAssetListEntry(
			null, TestPropsValues.getUserId(), groupId,
			RandomTestUtil.randomString(),
			AssetListEntryTypeConstants.TYPE_DYNAMIC,
			ServiceContextTestUtil.getServiceContext(groupId));
	}

	private AssetVocabulary _getAssetVocabulary(long groupId) throws Exception {
		return _assetVocabularyLocalService.addVocabulary(
			TestPropsValues.getUserId(), groupId, RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(groupId));
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

		listStyleDefinition.setGutters(true);
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
			Map<String, String> emptyCollectionMessages, String name,
			Integer numberOfItems, Integer numberOfItemsPerPage,
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
			() -> {
				CollectionSettings collectionSettings =
					new CollectionSettings();

				collectionSettings.setCollectionReference(collectionReference);

				return collectionSettings;
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
								setAlign(Align.CENTER);
								setFlexWrap(FlexWrap.WRAP);
								setHidden(RandomTestUtil.randomBoolean());
								setJustify(Justify.SPACE_AROUND);
								setNumberOfColumns(
									RandomTestUtil.randomInt(1, 12));
							}
						});
					setId(Id.DESKTOP);
				}
			},
			new CollectionDisplayViewport() {
				{
					setCollectionDisplayViewportDefinition(
						() -> new CollectionDisplayViewportDefinition() {
							{
								setHidden(RandomTestUtil.randomBoolean());
								setNumberOfColumns(
									RandomTestUtil.randomInt(1, 12));
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
								setHidden(RandomTestUtil.randomBoolean());
								setNumberOfColumns(
									RandomTestUtil.randomInt(1, 12));
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
								setHidden(RandomTestUtil.randomBoolean());
								setNumberOfColumns(12);
							}
						});
					setId(Id.TABLET);
				}
			}
		};
	}

	private CollectionDisplayViewport[]
		_getCollectionDisplayViewportsDefaultValues() {

		return new CollectionDisplayViewport[] {
			new CollectionDisplayViewport() {
				{
					setCollectionDisplayViewportDefinition(
						() -> new CollectionDisplayViewportDefinition() {
							{
								setHidden(false);
								setNumberOfColumns(1);
							}
						});
					setId(Id.DESKTOP);
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
			BackgroundImageValue backgroundImageValue, String[] cssClasses,
			String fragmentLinkClassName,
			String fragmentLinkExternalReferenceCode,
			String fragmentLinkFieldKey,
			Map<String, String> fragmentLinkLocalizedValues, boolean indexed,
			String pageElementExternalReferenceCode)
		throws Exception {

		ContainerPageElementDefinition containerPageElementDefinition =
			new ContainerPageElementDefinition();

		containerPageElementDefinition.setBackgroundImageValue(
			() -> backgroundImageValue);
		containerPageElementDefinition.setContentVisibility(
			ContainerPageElementDefinition.ContentVisibility.AUTO);
		containerPageElementDefinition.setCssClasses(cssClasses);
		containerPageElementDefinition.setFragmentLink(
			() -> _getFragmentLink(
				fragmentLinkClassName, fragmentLinkExternalReferenceCode,
				fragmentLinkFieldKey, fragmentLinkLocalizedValues));
		containerPageElementDefinition.setFragmentViewports(
			FragmentViewportTestUtil.getFragmentViewports());
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

	private FileEntry _getFileEntry(long groupId) throws Exception {
		return _dlAppLocalService.addFileEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(), groupId,
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + StringPool.PERIOD +
				ContentTypes.IMAGE_JPEG,
			MimeTypesUtil.getExtensionContentType(ContentTypes.IMAGE_JPEG),
			new byte[0], null, null, null,
			ServiceContextTestUtil.getServiceContext(groupId));
	}

	private FormContainerConfig _getFormContainerConfig(
		String className,
		boolean formContainerSubmissionResultDefaultDisplayPage,
		String formContainerSubmissionResultType,
		FormContainerConfig.FormContainerType formContainerType,
		int numberOfSteps,
		LocalizationConfig.UnlocalizedFieldsState unlocalizedFieldsState) {

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
		formContainerConfig.setFormContainerType(formContainerType);
		formContainerConfig.setNumberOfSteps(numberOfSteps);
		formContainerConfig.setLocalizationConfig(
			() -> _getLocalizationConfig(unlocalizedFieldsState));
		formContainerConfig.setSuccessFormContainerSubmissionResult(
			() -> _getSuccessFormContainerSubmissionResult(
				className, formContainerSubmissionResultDefaultDisplayPage,
				formContainerSubmissionResultType));

		return formContainerConfig;
	}

	private PageElement _getFormContainerPageElement(
			BackgroundImageValue backgroundImageValue, String className,
			String[] cssClasses,
			boolean formContainerSubmissionResultDefaultDisplayPage,
			String formContainerSubmissionResultType,
			FormContainerConfig.FormContainerType formContainerType,
			boolean indexed, int numberOfSteps,
			String pageElementExternalReferenceCode,
			LocalizationConfig.UnlocalizedFieldsState unlocalizedFieldsState)
		throws Exception {

		FormContainerPageElementDefinition formContainerPageElementDefinition =
			new FormContainerPageElementDefinition();

		formContainerPageElementDefinition.setBackgroundImageValue(
			backgroundImageValue);
		formContainerPageElementDefinition.setCssClasses(cssClasses);
		formContainerPageElementDefinition.setFormContainerConfig(
			_getFormContainerConfig(
				className, formContainerSubmissionResultDefaultDisplayPage,
				formContainerSubmissionResultType, formContainerType,
				numberOfSteps, unlocalizedFieldsState));
		formContainerPageElementDefinition.setFragmentViewports(
			FragmentViewportTestUtil.getFragmentViewports());
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

		if (Objects.equals(
				formContainerType,
				FormContainerConfig.FormContainerType.MULTISTEP)) {

			PageElement[] pageElements = new PageElement[1];

			pageElements[0] = _getFormStepContainerPageElement(
				backgroundImageValue, numberOfSteps,
				pageElementExternalReferenceCode);

			return _getPageElement(
				formContainerPageElementDefinition,
				pageElementExternalReferenceCode, pageElements);
		}

		return _getPageElement(
			formContainerPageElementDefinition,
			pageElementExternalReferenceCode);
	}

	private PageElement _getFormRelationshipPageElement(
			BackgroundImageValue backgroundImageValue,
			FragmentInlineValue buttonLabelFragmentInlineValue,
			String contentType, String[] cssClasses, Boolean indexed,
			String name, String pageElementExternalReferenceCode,
			String parentExternalReferenceCode, Boolean repeatable)
		throws Exception {

		FormRelationshipPageElementDefinition
			formRelationshipPageElementDefinition =
				new FormRelationshipPageElementDefinition();

		formRelationshipPageElementDefinition.setBackgroundImageValue(
			backgroundImageValue);
		formRelationshipPageElementDefinition.setCssClasses(cssClasses);

		if ((buttonLabelFragmentInlineValue != null) || (contentType != null)) {
			FormRelationshipConfig formRelationshipConfig =
				new FormRelationshipConfig();

			formRelationshipConfig.setButtonLabelFragmentInlineValue(
				buttonLabelFragmentInlineValue);
			formRelationshipConfig.setContentType(contentType);

			formRelationshipPageElementDefinition.setFormRelationshipConfig(
				formRelationshipConfig);
		}

		formRelationshipPageElementDefinition.setFragmentViewports(
			FragmentViewportTestUtil.getFragmentViewports());
		formRelationshipPageElementDefinition.setIndexed(indexed);
		formRelationshipPageElementDefinition.setName(name);
		formRelationshipPageElementDefinition.setRepeatable(repeatable);
		formRelationshipPageElementDefinition.setType(
			PageElementDefinition.Type.FORM_RELATIONSHIP);

		return _getPageElement(
			formRelationshipPageElementDefinition,
			pageElementExternalReferenceCode, parentExternalReferenceCode, 0);
	}

	private PageElement _getFormStepContainerPageElement(
			BackgroundImageValue backgroundImageValue, int numberOfSteps,
			String parentExternalReferenceCode)
		throws Exception {

		FormStepContainerPageElementDefinition
			formStepContainerPageElementDefinition =
				new FormStepContainerPageElementDefinition();

		formStepContainerPageElementDefinition.setBackgroundImageValue(
			backgroundImageValue);
		formStepContainerPageElementDefinition.setCssClasses(
			RandomTestUtil.randomStrings(RandomTestUtil.randomInt(1, 10)));
		formStepContainerPageElementDefinition.setFragmentViewports(
			FragmentViewportTestUtil.getFragmentViewports());
		formStepContainerPageElementDefinition.setType(
			PageElementDefinition.Type.FORM_STEP_CONTAINER);

		String externalReferenceCode = RandomTestUtil.randomString();

		PageElement[] pageElements = new PageElement[numberOfSteps];

		for (int i = 0; i < numberOfSteps; i++) {
			pageElements[i] = _getFormStepPageElement(externalReferenceCode, i);
		}

		return _getPageElement(
			formStepContainerPageElementDefinition, externalReferenceCode,
			parentExternalReferenceCode, pageElements);
	}

	private PageElement _getFormStepPageElement(
			String parentExternalReferenceCode, int position)
		throws Exception {

		FormStepPageElementDefinition formStepPageElementDefinition =
			new FormStepPageElementDefinition();

		formStepPageElementDefinition.setType(
			PageElementDefinition.Type.FORM_STEP);

		return _getPageElement(
			formStepPageElementDefinition, StringUtil.randomString(),
			parentExternalReferenceCode, position);
	}

	private PageElement _getFragmentInstancePageElement(
			BasicFragmentInstancePageElementDefinition
				basicFragmentInstancePageElementDefinition,
			String externalReferenceCode)
		throws Exception {

		PageElement pageElement = super.randomPageElement();

		pageElement.setExternalReferenceCode(externalReferenceCode);

		pageElement.setPageElementDefinition(
			() -> basicFragmentInstancePageElementDefinition);

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

	private PageElement _getGridPageElement(
			BackgroundImageValue backgroundImageValue, String[] cssClasses,
			boolean gutters, boolean indexed, Integer numberOfModules)
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		return _getGridPageElement(
			backgroundImageValue, cssClasses, gutters, indexed, numberOfModules,
			externalReferenceCode,
			_getModulePageElements(externalReferenceCode, numberOfModules));
	}

	private PageElement _getGridPageElement(
			BackgroundImageValue backgroundImageValue, String[] cssClasses,
			boolean gutters, boolean indexed, Integer numberOfModules,
			String pageElementExternalReferenceCode, PageElement[] pageElements)
		throws Exception {

		GridPageElementDefinition gridPageElementDefinition =
			new GridPageElementDefinition();

		gridPageElementDefinition.setBackgroundImageValue(backgroundImageValue);
		gridPageElementDefinition.setCssClasses(cssClasses);
		gridPageElementDefinition.setGridViewports(this::_getGridViewports);
		gridPageElementDefinition.setGutters(gutters);
		gridPageElementDefinition.setIndexed(indexed);
		gridPageElementDefinition.setName(RandomTestUtil.randomString());
		gridPageElementDefinition.setNumberOfModules(numberOfModules);
		gridPageElementDefinition.setReverseOrder(Boolean.FALSE);
		gridPageElementDefinition.setType(PageElementDefinition.Type.GRID);

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
					setFragmentViewportStyle(
						FragmentViewportStyleTestUtil.
							getFragmentViewportStyle());
					setGridViewportDefinition(
						() -> new GridViewportDefinition() {
							{
								setModulesPerRow(1);
								setVerticalAlignment(VerticalAlignment.MIDDLE);
							}
						});
					setId(Id.DESKTOP);
				}
			},
			new GridViewport() {
				{
					setCustomCSS(RandomTestUtil.randomString());
					setFragmentViewportStyle(
						FragmentViewportStyleTestUtil.
							getFragmentViewportStyle());
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
					setFragmentViewportStyle(
						FragmentViewportStyleTestUtil.
							getFragmentViewportStyle());
					setGridViewportDefinition(
						() -> new GridViewportDefinition() {
							{
								setModulesPerRow(2);
								setVerticalAlignment(VerticalAlignment.BOTTOM);
							}
						});
					setId(Id.PORTRAIT_MOBILE);
				}
			},
			new GridViewport() {
				{
					setCustomCSS(RandomTestUtil.randomString());
					setFragmentViewportStyle(
						FragmentViewportStyleTestUtil.
							getFragmentViewportStyle());
					setGridViewportDefinition(
						() -> new GridViewportDefinition() {
							{
								setModulesPerRow(3);
								setVerticalAlignment(VerticalAlignment.TOP);
							}
						});
					setId(Id.TABLET);
				}
			}
		};
	}

	private GridViewport[] _getGridViewportsDefaultValues() {
		return new GridViewport[] {
			_getGridViewport(GridViewport.Id.DESKTOP, 1),
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

	private LocalizationConfig _getLocalizationConfig(
		LocalizationConfig.UnlocalizedFieldsState unlocalizedFieldsState) {

		if (unlocalizedFieldsState == null) {
			return null;
		}

		LocalizationConfig localizationConfig = new LocalizationConfig();

		localizationConfig.setUnlocalizedFieldsMessageFragmentInlineValue(
			this::_getRandomFragmentInlineValue);
		localizationConfig.setUnlocalizedFieldsState(
			() -> unlocalizedFieldsState);

		return localizationConfig;
	}

	private PageElement _getModulePageElement(
			String externalReferenceCode, String parentExternalReferenceCode,
			int position)
		throws Exception {

		return _getPageElement(
			new ModulePageElementDefinition() {
				{
					setModuleViewports(_getModuleViewports());
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
				externalReferenceCode + i, externalReferenceCode, i);
		}

		return pageElements;
	}

	private ModuleViewport[] _getModuleViewports() {
		return new ModuleViewport[] {
			new ModuleViewport() {
				{
					setId(Id.DESKTOP);
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
					setId(Id.DESKTOP);
					setModuleViewportDefinition(
						() -> new ModuleViewportDefinition() {
							{
								setSize(1);
							}
						});
				}
			},
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

		return _getPageElement(
			pageElementDefinition, pageElementExternalReferenceCode,
			StringPool.BLANK, new PageElement[0]);
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

	private PageElement _getPageElement(
			PageElementDefinition pageElementDefinition,
			String pageElementExternalReferenceCode,
			String parentExternalReferenceCode, PageElement[] pageElements)
		throws Exception {

		PageElement pageElement = super.randomPageElement();

		pageElement.setExternalReferenceCode(pageElementExternalReferenceCode);
		pageElement.setPageElementDefinition(pageElementDefinition);
		pageElement.setPageElements(pageElements);
		pageElement.setParentExternalReferenceCode(parentExternalReferenceCode);
		pageElement.setPosition(0);

		return pageElement;
	}

	private FragmentInlineValue _getRandomFragmentInlineValue() {
		return new FragmentInlineValue() {
			{
				setValue_i18n(
					HashMapBuilder.put(
						LocaleUtil.toBCP47LanguageId(LocaleUtil.SPAIN),
						RandomTestUtil.randomString()
					).put(
						LocaleUtil.toBCP47LanguageId(LocaleUtil.US),
						RandomTestUtil.randomString()
					).build());
			}
		};
	}

	private SiteNavigationMenu _getSiteNavigationMenu(long groupId)
		throws Exception {

		return _siteNavigationMenuLocalService.addSiteNavigationMenu(
			null, TestPropsValues.getUserId(), groupId,
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(groupId));
	}

	private SiteNavigationMenuItem _getSiteNavigationMenuItem(
			SiteNavigationMenu siteNavigationMenu)
		throws Exception {

		return _siteNavigationMenuItemLocalService.addSiteNavigationMenuItem(
			null, TestPropsValues.getUserId(), siteNavigationMenu.getGroupId(),
			siteNavigationMenu.getSiteNavigationMenuId(), 0,
			SiteNavigationMenuItemTypeConstants.NODE, 0, StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext(
				siteNavigationMenu.getGroupId()));
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
						_portal.getClassNameId(className), null, true,
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
			BackgroundImageValue backgroundImageValue, String[] cssClasses,
			String draftWidgetInstanceExternalReferenceCode, boolean indexed,
			String name, String pageElementExternalReferenceCode,
			Map<String, Object> widgetConfig,
			String widgetInstanceExternalReferenceCode, String widgetInstanceId,
			String widgetName, WidgetPermission[] widgetPermissions)
		throws Exception {

		WidgetInstancePageElementDefinition
			widgetInstancePageElementDefinition =
				new WidgetInstancePageElementDefinition();

		widgetInstancePageElementDefinition.setBackgroundImageValue(
			backgroundImageValue);
		widgetInstancePageElementDefinition.setCssClasses(cssClasses);
		widgetInstancePageElementDefinition.
			setDraftWidgetInstanceExternalReferenceCode(
				draftWidgetInstanceExternalReferenceCode);
		widgetInstancePageElementDefinition.setFragmentViewports(
			FragmentViewportTestUtil.getFragmentViewports());
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

	private WidgetPermission _getWidgetPermission(
		String[] actionIds, String roleName) {

		WidgetPermission widgetPermission = new WidgetPermission();

		widgetPermission.setActionIds(actionIds);
		widgetPermission.setRoleName(roleName);

		return widgetPermission;
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

				return _getWidgetPermission(actionIds, roleName);
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

	private ItemExternalReference _randomItemExternalReference() {
		return ReferencesTestUtil.getItemExternalReference(
			HashMapBuilder.put(
				"className", FileEntry.class.getName()
			).put(
				"externalReferenceCode", RandomTestUtil.randomString()
			).put(
				"scopeExternalReferenceCode", RandomTestUtil.randomString()
			).build(),
			testGroup.getGroupId());
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
			int count, UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.headless.admin.site.internal.util.LogUtil",
				LoggerTestUtil.WARN)) {

			unsafeRunnable.run();

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(
				logEntries.toString(), count, logEntries.size());

			for (LogEntry logEntry : logEntries) {
				String message = logEntry.getMessage();

				Assert.assertTrue(
					message,
					message.startsWith(
						"Optional reference generated for missing"));
			}
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
					LocaleUtil.toBCP47LanguageId(LocaleUtil.SPAIN),
					RandomTestUtil.randomString()
				).put(
					LocaleUtil.toBCP47LanguageId(LocaleUtil.US),
					RandomTestUtil.randomString()
				).build(),
				RandomTestUtil.randomString(), RandomTestUtil.randomInt(),
				RandomTestUtil.randomInt(), RandomTestUtil.randomInt(),
				CollectionDisplayPageElementDefinition.PaginationType.NONE,
				RandomTestUtil.randomString()));

		AssetListEntry assetListEntry = _getAssetListEntry(
			testGroup.getGroupId());

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
					true, true, null, RandomTestUtil.randomString(),
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
				null, null, null, null, "FileEntry_fileName", null, false,
				RandomTestUtil.randomString()));

		FileEntry fileEntry = _getFileEntry(testGroup.getGroupId());

		_testPostSitePageSpecificationPageExperiencePageElement(
			_getContainerPageElement(
				null, null, FileEntry.class.getName(),
				fileEntry.getExternalReferenceCode(), "FileEntry_fileName",
				null, false, RandomTestUtil.randomString()));

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			testGroup.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		_testPostSitePageSpecificationPageExperiencePageElement(
			_getContainerPageElement(
				null, null, JournalArticle.class.getName(),
				journalArticle.getExternalReferenceCode(),
				"JournalArticle_title", null, false,
				RandomTestUtil.randomString()));

		Layout layout = LayoutTestUtil.addTypeContentLayout(testGroup);

		_testPostSitePageSpecificationPageExperiencePageElement(
			_getContainerPageElement(
				null,
				RandomTestUtil.randomStrings(RandomTestUtil.randomInt(1, 10)),
				Layout.class.getName(), layout.getExternalReferenceCode(), null,
				null, true, RandomTestUtil.randomString()));

		_testPostSitePageSpecificationPageExperiencePageElement(
			_getContainerPageElement(
				null,
				RandomTestUtil.randomStrings(RandomTestUtil.randomInt(1, 10)),
				null, null, null,
				HashMapBuilder.put(
					LocaleUtil.toBCP47LanguageId(LocaleUtil.SPAIN),
					"https://www.liferay.es"
				).put(
					LocaleUtil.toBCP47LanguageId(LocaleUtil.US),
					"https://www.liferay.com"
				).build(),
				false, RandomTestUtil.randomString()));
	}

	private void
			_testPostSitePageSpecificationPageExperiencePageElementWithFormContainerPageElement(
				ObjectDefinition objectDefinition)
		throws Exception {

		_testPostSitePageSpecificationPageExperiencePageElement(
			_getFormContainerPageElement(
				null, objectDefinition.getClassName(),
				RandomTestUtil.randomStrings(RandomTestUtil.randomInt(1, 10)),
				true, "displayPage",
				FormContainerConfig.FormContainerType.SIMPLE, true, 1,
				RandomTestUtil.randomString(),
				LocalizationConfig.UnlocalizedFieldsState.READ_ONLY));
		_testPostSitePageSpecificationPageExperiencePageElement(
			_getFormContainerPageElement(
				null, objectDefinition.getClassName(), null, false,
				"displayPage", FormContainerConfig.FormContainerType.SIMPLE,
				false, 1, RandomTestUtil.randomString(), null));

		_testPostSitePageSpecificationPageExperiencePageElement(
			_getFormContainerPageElement(
				null, null,
				RandomTestUtil.randomStrings(RandomTestUtil.randomInt(1, 10)),
				false, "embedded", FormContainerConfig.FormContainerType.SIMPLE,
				false, 1, RandomTestUtil.randomString(),
				LocalizationConfig.UnlocalizedFieldsState.DISABLED));
		_testPostSitePageSpecificationPageExperiencePageElement(
			_getFormContainerPageElement(
				null, objectDefinition.getClassName(), null, false, "none",
				FormContainerConfig.FormContainerType.SIMPLE, false, 1,
				RandomTestUtil.randomString(), null));
		_testPostSitePageSpecificationPageExperiencePageElement(
			_getFormContainerPageElement(
				null, null,
				RandomTestUtil.randomStrings(RandomTestUtil.randomInt(1, 10)),
				false, "page", FormContainerConfig.FormContainerType.SIMPLE,
				false, 1, RandomTestUtil.randomString(),
				LocalizationConfig.UnlocalizedFieldsState.READ_ONLY));
		_testPostSitePageSpecificationPageExperiencePageElement(
			_getFormContainerPageElement(
				null, objectDefinition.getClassName(),
				RandomTestUtil.randomStrings(RandomTestUtil.randomInt(1, 10)),
				false, "url", FormContainerConfig.FormContainerType.SIMPLE,
				false, 1, RandomTestUtil.randomString(),
				LocalizationConfig.UnlocalizedFieldsState.DISABLED));
		_testPostSitePageSpecificationPageExperiencePageElement(
			_getFormContainerPageElement(
				null, objectDefinition.getClassName(),
				RandomTestUtil.randomStrings(RandomTestUtil.randomInt(1, 10)),
				false, "url", FormContainerConfig.FormContainerType.MULTISTEP,
				false, RandomTestUtil.randomInt(2, 10),
				RandomTestUtil.randomString(),
				LocalizationConfig.UnlocalizedFieldsState.DISABLED));
	}

	private void
			_testPostSitePageSpecificationPageExperiencePageElementWithFormRelationshipPageElement(
				ObjectDefinition objectDefinition)
		throws Exception {

		String formContainerExternalReferenceCode =
			RandomTestUtil.randomString();

		_testPostSitePageSpecificationPageExperiencePageElement(
			_getFormContainerPageElement(
				null, objectDefinition.getClassName(), null, false,
				"displayPage", FormContainerConfig.FormContainerType.SIMPLE,
				false, 1, formContainerExternalReferenceCode, null));

		_testPostSitePageSpecificationPageExperiencePageElement(
			_getFormRelationshipPageElement(
				null, _getRandomFragmentInlineValue(),
				RandomTestUtil.randomString(),
				RandomTestUtil.randomStrings(RandomTestUtil.randomInt(1, 10)),
				true, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				formContainerExternalReferenceCode, true));
		_testPostSitePageSpecificationPageExperiencePageElement(
			_getFormRelationshipPageElement(
				null, null, null, null, false, null,
				RandomTestUtil.randomString(),
				formContainerExternalReferenceCode, false));

		_assertProblemException(
			"BAD_REQUEST",
			"Form relationship can only be added inside of a form",
			() -> _testPostSitePageSpecificationPageExperiencePageElement(
				_getFormRelationshipPageElement(
					null, null, null, null, false, null,
					RandomTestUtil.randomString(), null, false)));
	}

	private void _testPostSitePageSpecificationPageExperiencePageElementWithFragmentPageElement()
		throws Exception {

		PageElement fragmentPageElement = _randomPageElement(
			PageElementDefinition.Type.BASIC_FRAGMENT, StringPool.BLANK);

		_testPostSitePageSpecificationPageExperiencePageElement(
			fragmentPageElement);

		_testPostSitePageSpecificationPageExperiencePageElement(
			_randomPageElement(
				PageElementDefinition.Type.FRAGMENT_DROP_ZONE,
				fragmentPageElement.getExternalReferenceCode()));
	}

	private void _testPostSitePageSpecificationPageExperiencePageElementWithGridPageElement()
		throws Exception {

		BackgroundImageValue backgroundImageValue =
			ImageValueTestUtil.getDirectBackgroundImageValue(
				null, RandomTestUtil.randomString());

		PageElement pageElement =
			_testPostSitePageSpecificationPageExperiencePageElement(
				_getGridPageElement(
					backgroundImageValue,
					RandomTestUtil.randomStrings(
						RandomTestUtil.randomInt(1, 10)),
					false, false, 6));

		_assertStyledLayoutStructureItemBackgroundImage(
			backgroundImageValue, 0, null,
			pageElement.getExternalReferenceCode());

		FileEntry fileEntry = _getFileEntry(testGroup.getGroupId());

		backgroundImageValue = ImageValueTestUtil.getDirectBackgroundImageValue(
			ReferencesTestUtil.getItemExternalReference(
				fileEntry, testGroup.getGroupId()),
			null);

		pageElement = _testPostSitePageSpecificationPageExperiencePageElement(
			_getGridPageElement(backgroundImageValue, null, true, true, 3));

		_assertStyledLayoutStructureItemBackgroundImage(
			backgroundImageValue, fileEntry.getFileEntryId(), fileEntry,
			pageElement.getExternalReferenceCode());

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			testGroup.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		backgroundImageValue = ImageValueTestUtil.getMappedBackgroundImageValue(
			JournalArticle.class.getName(),
			journalArticle.getExternalReferenceCode(),
			"JournalArticle_authorProfileImage", null);

		pageElement = _testPostSitePageSpecificationPageExperiencePageElement(
			_getGridPageElement(
				backgroundImageValue,
				RandomTestUtil.randomStrings(RandomTestUtil.randomInt(1, 10)),
				false, false, 12));

		_assertStyledLayoutStructureItemBackgroundImage(
			backgroundImageValue, journalArticle.getResourcePrimKey(),
			journalArticle, pageElement.getExternalReferenceCode());
	}

	private void _testPostSitePageSpecificationPageExperiencePageElementWithNonexistentWidgetPermissionRole()
		throws Exception {

		String draftWidgetInstanceExternalReferenceCode =
			RandomTestUtil.randomString();
		String namespace = RandomTestUtil.randomString();

		_addFragmentEntryLink(
			draftWidgetInstanceExternalReferenceCode, namespace);

		String roleName = RandomTestUtil.randomString();

		Assert.assertNull(
			_roleLocalService.fetchRole(
				TestPropsValues.getCompanyId(), roleName));

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				testGroup.getGroupId(), SegmentsExperienceConstants.KEY_DEFAULT,
				_layout.getPlid());

		PageElement pageElement =
			pageElementResource.
				postSitePageSpecificationPageExperiencePageElement(
					testGroup.getExternalReferenceCode(),
					_draftLayout.getExternalReferenceCode(),
					segmentsExperience.getExternalReferenceCode(),
					_getWidgetPageElement(
						null, null, draftWidgetInstanceExternalReferenceCode,
						false, RandomTestUtil.randomString(),
						RandomTestUtil.randomString(), _getWidgetConfig(),
						RandomTestUtil.randomString(), namespace,
						JournalContentPortletKeys.JOURNAL_CONTENT,
						new WidgetPermission[] {
							_getWidgetPermission(
								new String[] {ActionKeys.VIEW},
								RoleConstants.GUEST),
							_getWidgetPermission(
								new String[] {ActionKeys.VIEW}, roleName)
						}));

		assertValid(pageElement);

		WidgetInstancePageElementDefinition
			widgetInstancePageElementDefinition =
				(WidgetInstancePageElementDefinition)
					pageElement.getPageElementDefinition();

		WidgetInstance widgetInstance =
			widgetInstancePageElementDefinition.getWidgetInstance();

		WidgetPermission[] widgetPermissions =
			widgetInstance.getWidgetPermissions();

		Assert.assertEquals(
			Arrays.toString(widgetPermissions), 1, widgetPermissions.length);
		Assert.assertEquals(
			RoleConstants.GUEST, widgetPermissions[0].getRoleName());

		Role guestRole = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.GUEST);

		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				TestPropsValues.getCompanyId(),
				JournalContentPortletKeys.JOURNAL_CONTENT,
				ResourceConstants.SCOPE_INDIVIDUAL,
				PortletPermissionUtil.getPrimaryKey(
					_layout.getPlid(),
					PortletIdCodec.encode(
						JournalContentPortletKeys.JOURNAL_CONTENT, namespace)),
				guestRole.getRoleId(), ActionKeys.VIEW));
	}

	private void _testPostSitePageSpecificationPageExperiencePageElementWithWidgetPageElement()
		throws Exception {

		BackgroundImageValue backgroundImageValue =
			ImageValueTestUtil.getDirectBackgroundImageValue(
				null, RandomTestUtil.randomString());

		String draftWidgetInstanceExternalReferenceCode =
			RandomTestUtil.randomString();
		String namespace = RandomTestUtil.randomString();

		_addFragmentEntryLink(
			draftWidgetInstanceExternalReferenceCode, namespace);

		PageElement pageElement =
			_testPostSitePageSpecificationPageExperiencePageElement(
				_getWidgetPageElement(
					backgroundImageValue,
					RandomTestUtil.randomStrings(
						RandomTestUtil.randomInt(1, 10)),
					draftWidgetInstanceExternalReferenceCode, false,
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(), _getWidgetConfig(),
					RandomTestUtil.randomString(), namespace,
					JournalContentPortletKeys.JOURNAL_CONTENT,
					_getWidgetPermissions()));

		_assertStyledLayoutStructureItemBackgroundImage(
			backgroundImageValue, 0, null, pageElement);

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				testGroup.getGroupId(), SegmentsExperienceConstants.KEY_DEFAULT,
				_layout.getPlid());

		String undeployedPortletName = RandomTestUtil.randomString();

		pageElement =
			pageElementResource.
				postSitePageSpecificationPageExperiencePageElement(
					testGroup.getExternalReferenceCode(),
					_draftLayout.getExternalReferenceCode(),
					segmentsExperience.getExternalReferenceCode(),
					_getWidgetPageElement(
						null,
						RandomTestUtil.randomStrings(
							RandomTestUtil.randomInt(1, 10)),
						draftWidgetInstanceExternalReferenceCode, false,
						RandomTestUtil.randomString(),
						RandomTestUtil.randomString(), _getWidgetConfig(),
						RandomTestUtil.randomString(), namespace,
						undeployedPortletName, _getWidgetPermissions()));

		assertValid(pageElement);

		WidgetInstancePageElementDefinition
			widgetInstancePageElementDefinition =
				(WidgetInstancePageElementDefinition)
					pageElement.getPageElementDefinition();

		WidgetInstance widgetInstance =
			widgetInstancePageElementDefinition.getWidgetInstance();

		Assert.assertEquals(
			undeployedPortletName, widgetInstance.getWidgetName());
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
					LocaleUtil.toBCP47LanguageId(LocaleUtil.SPAIN),
					RandomTestUtil.randomString()
				).put(
					LocaleUtil.toBCP47LanguageId(LocaleUtil.US),
					RandomTestUtil.randomString()
				).build(),
				RandomTestUtil.randomString(), RandomTestUtil.randomInt(),
				RandomTestUtil.randomInt(), RandomTestUtil.randomInt(),
				CollectionDisplayPageElementDefinition.PaginationType.NONE,
				externalReferenceCode));

		AssetListEntry assetListEntry = _getAssetListEntry(
			testGroup.getGroupId());

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
				true, true, null, RandomTestUtil.randomString(),
				RandomTestUtil.randomInt(), RandomTestUtil.randomInt(),
				RandomTestUtil.randomInt(),
				CollectionDisplayPageElementDefinition.PaginationType.SIMPLE,
				externalReferenceCode));

		PageElement collectionDisplayPageElement =
			_testPutSitePageSpecificationPageExperiencePageElement(
				_getCollectionDisplayPageElement(
					_getCollectionDisplayListStyle(
						null, null, ListStyle.ListStyleType.GRID, null),
					_getCollectionDisplayViewportsDefaultValues(), null, true,
					true, null, RandomTestUtil.randomString(),
					RandomTestUtil.randomInt(), RandomTestUtil.randomInt(),
					RandomTestUtil.randomInt(),
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

	private void _testPutSitePageSpecificationPageExperiencePageElementWithCollectionFilterFragmentRendererConfiguration()
		throws Exception {

		JSONObject configurationJSONObject = JSONUtil.put(
			"fieldSets",
			JSONUtil.put(
				JSONUtil.put(
					"fields",
					JSONUtil.putAll(
						JSONUtil.put(
							"dataType", "array"
						).put(
							"defaultValue", JSONFactoryUtil.createJSONArray()
						).put(
							"name", "targetCollections"
						).put(
							"type", "targetCollectionDisplay"
						),
						JSONUtil.put(
							"defaultValue", ""
						).put(
							"name", "filterKey"
						).put(
							"type", "text"
						)))));

		for (Map<String, Object> configurationValuesMap :
				new Map[] {
					HashMapBuilder.<String, Object>put(
						"filterKey", RandomTestUtil.randomString()
					).put(
						"targetCollections",
						new String[] {
							RandomTestUtil.randomString(),
							RandomTestUtil.randomString()
						}
					).build(),
					HashMapBuilder.<String, Object>put(
						"filterKey", RandomTestUtil.randomString()
					).put(
						"targetCollections",
						new String[] {RandomTestUtil.randomString()}
					).build()
				}) {

			_testPutSitePageSpecificationPageExperiencePageElement(
				_getFragmentInstancePageElement(
					new BasicFragmentInstancePageElementDefinition() {
						{
							setFragmentInstance(
								new FragmentInstance() {
									{
										setConfiguration(
											() -> StringPool.BLANK);
										setCss(() -> StringPool.BLANK);
										setCssClasses(
											() -> new String[] {
												RandomTestUtil.randomString()
											});
										setDatePropagated(
											RandomTestUtil::nextDate);
										setFragmentConfigurationFieldValues(
											() ->
												FragmentConfigurationFieldValueTestUtil.
													getFragmentConfigurationFieldValuesMap(
														configurationJSONObject,
														configurationValuesMap,
														testGroup.
															getGroupId()));
										setFragmentEditableElements(
											() ->
												new FragmentEditableElement[0]);
										setFragmentInstanceExternalReferenceCode(
											RandomTestUtil::randomString);
										setFragmentReference(
											() ->
												new DefaultFragmentReference() {
													{
														setDefaultFragmentKey(
															() ->
																FragmentRendererConstants.FRAGMENT_RENDERER_CLASS_NAME_COLLECTION_FILTER);
														setFragmentReferenceType(
															() ->
																FragmentReference.FragmentReferenceType.DEFAULT_FRAGMENT_REFERENCE);
													}
												});
										setHtml(() -> StringPool.BLANK);
										setIndexed(
											RandomTestUtil::randomBoolean);
										setJs(() -> StringPool.BLANK);
										setName(RandomTestUtil::randomString);
										setNamespace(
											RandomTestUtil::randomString);
										setUuid(RandomTestUtil::randomString);
									}
								});
							setType(() -> Type.BASIC_FRAGMENT);
						}
					},
					RandomTestUtil.randomString()));
		}
	}

	private void _testPutSitePageSpecificationPageExperiencePageElementWithContainerPageElement()
		throws Exception {

		BackgroundImageValue backgroundImageValue =
			ImageValueTestUtil.getDirectBackgroundImageValue(
				null, RandomTestUtil.randomString());
		String externalReferenceCode = RandomTestUtil.randomString();

		_testPutSitePageSpecificationPageExperiencePageElement(
			_getContainerPageElement(
				backgroundImageValue, null, null, null, "FileEntry_fileName",
				null, false, externalReferenceCode));

		_assertStyledLayoutStructureItemBackgroundImage(
			backgroundImageValue, 0, null, externalReferenceCode);

		FileEntry fileEntry = _getFileEntry(testGroup.getGroupId());

		backgroundImageValue = ImageValueTestUtil.getDirectBackgroundImageValue(
			ReferencesTestUtil.getItemExternalReference(
				fileEntry, testGroup.getGroupId()),
			null);

		_testPutSitePageSpecificationPageExperiencePageElement(
			_getContainerPageElement(
				backgroundImageValue, null, FileEntry.class.getName(),
				fileEntry.getExternalReferenceCode(), "FileEntry_fileName",
				null, false, externalReferenceCode));

		_assertStyledLayoutStructureItemBackgroundImage(
			backgroundImageValue, fileEntry.getFileEntryId(), fileEntry,
			externalReferenceCode);

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			testGroup.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		backgroundImageValue = ImageValueTestUtil.getMappedBackgroundImageValue(
			JournalArticle.class.getName(),
			journalArticle.getExternalReferenceCode(),
			"JournalArticle_authorProfileImage", null);

		_testPutSitePageSpecificationPageExperiencePageElement(
			_getContainerPageElement(
				backgroundImageValue, null, JournalArticle.class.getName(),
				journalArticle.getExternalReferenceCode(),
				"JournalArticle_title", null, false, externalReferenceCode));

		_assertStyledLayoutStructureItemBackgroundImage(
			backgroundImageValue, journalArticle.getResourcePrimKey(),
			journalArticle, externalReferenceCode);

		backgroundImageValue = ImageValueTestUtil.getMappedBackgroundImageValue(
			FragmentMappedValueItemContextReference.ContextSource.
				DISPLAY_PAGE_ITEM,
			"JournalArticle_authorProfileImage",
			FragmentMappedValueItemReference.Type.CONTEXT_REFERENCE);

		Layout layout = LayoutTestUtil.addTypeContentLayout(testGroup);

		_testPutSitePageSpecificationPageExperiencePageElement(
			_getContainerPageElement(
				backgroundImageValue,
				RandomTestUtil.randomStrings(RandomTestUtil.randomInt(1, 10)),
				Layout.class.getName(), layout.getExternalReferenceCode(), null,
				null, true, externalReferenceCode));

		_assertStyledLayoutStructureItemBackgroundImage(
			backgroundImageValue, 0, null, externalReferenceCode);

		backgroundImageValue = ImageValueTestUtil.getMappedBackgroundImageValue(
			FragmentMappedValueItemContextReference.ContextSource.
				COLLECTION_ITEM,
			"AssetEntry_userProfileImage",
			FragmentMappedValueItemReference.Type.CONTEXT_REFERENCE);

		_testPutSitePageSpecificationPageExperiencePageElement(
			_getContainerPageElement(
				backgroundImageValue,
				RandomTestUtil.randomStrings(RandomTestUtil.randomInt(1, 10)),
				null, null, null,
				HashMapBuilder.put(
					LocaleUtil.toBCP47LanguageId(LocaleUtil.SPAIN),
					"https://www.liferay.es"
				).put(
					LocaleUtil.toBCP47LanguageId(LocaleUtil.US),
					"https://www.liferay.com"
				).build(),
				false, externalReferenceCode));

		_assertStyledLayoutStructureItemBackgroundImage(
			backgroundImageValue, 0, null, externalReferenceCode);

		BackgroundImageValue missingItemExternalReferenceBackgroundImageValue =
			ImageValueTestUtil.getDirectBackgroundImageValue(
				_randomItemExternalReference(), null);

		_testMissingOptionalReference(
			1,
			() -> _testPutSitePageSpecificationPageExperiencePageElement(
				_getPageElement(
					new ContainerPageElementDefinition() {
						{
							setBackgroundImageValue(
								() ->
									missingItemExternalReferenceBackgroundImageValue);
							setIndexed(false);
							setType(Type.CONTAINER);
						}
					},
					externalReferenceCode)));

		_assertStyledLayoutStructureItemBackgroundImage(
			missingItemExternalReferenceBackgroundImageValue, 0, null,
			externalReferenceCode);
	}

	private void
			_testPutSitePageSpecificationPageExperiencePageElementWithFormContainerPageElement(
				ObjectDefinition objectDefinition)
		throws Exception {

		BackgroundImageValue backgroundImageValue =
			ImageValueTestUtil.getDirectBackgroundImageValue(
				null, RandomTestUtil.randomString());
		String externalReferenceCode = RandomTestUtil.randomString();

		_testPutSitePageSpecificationPageExperiencePageElement(
			_getFormContainerPageElement(
				backgroundImageValue, objectDefinition.getClassName(),
				RandomTestUtil.randomStrings(RandomTestUtil.randomInt(1, 10)),
				true, "displayPage",
				FormContainerConfig.FormContainerType.SIMPLE, true, 1,
				externalReferenceCode,
				LocalizationConfig.UnlocalizedFieldsState.READ_ONLY));

		_assertStyledLayoutStructureItemBackgroundImage(
			backgroundImageValue, 0, null, externalReferenceCode);

		FileEntry fileEntry = _getFileEntry(testGroup.getGroupId());

		backgroundImageValue = ImageValueTestUtil.getDirectBackgroundImageValue(
			ReferencesTestUtil.getItemExternalReference(
				fileEntry, testGroup.getGroupId()),
			null);

		_testPutSitePageSpecificationPageExperiencePageElement(
			_getFormContainerPageElement(
				backgroundImageValue, objectDefinition.getClassName(), null,
				false, "displayPage",
				FormContainerConfig.FormContainerType.SIMPLE, false, 1,
				externalReferenceCode, null));

		_assertStyledLayoutStructureItemBackgroundImage(
			backgroundImageValue, fileEntry.getFileEntryId(), fileEntry,
			externalReferenceCode);

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			testGroup.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		backgroundImageValue = ImageValueTestUtil.getMappedBackgroundImageValue(
			JournalArticle.class.getName(),
			journalArticle.getExternalReferenceCode(),
			"JournalArticle_authorProfileImage", null);

		_testPutSitePageSpecificationPageExperiencePageElement(
			_getFormContainerPageElement(
				backgroundImageValue, null,
				RandomTestUtil.randomStrings(RandomTestUtil.randomInt(1, 10)),
				false, "embedded", FormContainerConfig.FormContainerType.SIMPLE,
				false, 1, externalReferenceCode,
				LocalizationConfig.UnlocalizedFieldsState.DISABLED));

		_assertStyledLayoutStructureItemBackgroundImage(
			backgroundImageValue, journalArticle.getResourcePrimKey(),
			journalArticle, externalReferenceCode);

		backgroundImageValue = ImageValueTestUtil.getMappedBackgroundImageValue(
			FragmentMappedValueItemContextReference.ContextSource.
				DISPLAY_PAGE_ITEM,
			"JournalArticle_authorProfileImage",
			FragmentMappedValueItemReference.Type.CONTEXT_REFERENCE);

		_testPutSitePageSpecificationPageExperiencePageElement(
			_getFormContainerPageElement(
				backgroundImageValue, objectDefinition.getClassName(), null,
				false, "none", FormContainerConfig.FormContainerType.SIMPLE,
				false, 1, externalReferenceCode,
				LocalizationConfig.UnlocalizedFieldsState.READ_ONLY));

		_assertStyledLayoutStructureItemBackgroundImage(
			backgroundImageValue, 0, null, externalReferenceCode);

		_testPutSitePageSpecificationPageExperiencePageElement(
			_getFormContainerPageElement(
				backgroundImageValue, null,
				RandomTestUtil.randomStrings(RandomTestUtil.randomInt(1, 10)),
				false, "page", FormContainerConfig.FormContainerType.SIMPLE,
				false, 1, externalReferenceCode, null));

		_testPutSitePageSpecificationPageExperiencePageElement(
			_getFormContainerPageElement(
				null, objectDefinition.getClassName(),
				RandomTestUtil.randomStrings(RandomTestUtil.randomInt(1, 10)),
				false, "url", FormContainerConfig.FormContainerType.SIMPLE,
				false, 1, externalReferenceCode,
				LocalizationConfig.UnlocalizedFieldsState.DISABLED));

		backgroundImageValue = ImageValueTestUtil.getMappedBackgroundImageValue(
			FragmentMappedValueItemContextReference.ContextSource.
				COLLECTION_ITEM,
			"AssetEntry_userProfileImage",
			FragmentMappedValueItemReference.Type.CONTEXT_REFERENCE);

		PageElement pageElement =
			_testPutSitePageSpecificationPageExperiencePageElement(
				_getFormContainerPageElement(
					backgroundImageValue, objectDefinition.getClassName(),
					RandomTestUtil.randomStrings(
						RandomTestUtil.randomInt(1, 10)),
					false, "url",
					FormContainerConfig.FormContainerType.MULTISTEP, false,
					RandomTestUtil.randomInt(2, 10), externalReferenceCode,
					LocalizationConfig.UnlocalizedFieldsState.DISABLED));

		_assertStyledLayoutStructureItemBackgroundImage(
			backgroundImageValue, 0, null, pageElement);

		BackgroundImageValue missingItemExternalReferenceBackgroundImageValue =
			ImageValueTestUtil.getDirectBackgroundImageValue(
				_randomItemExternalReference(), null);

		_testMissingOptionalReference(
			1,
			() -> _testPutSitePageSpecificationPageExperiencePageElement(
				_getPageElement(
					new FormContainerPageElementDefinition() {
						{
							setBackgroundImageValue(
								() ->
									missingItemExternalReferenceBackgroundImageValue);
							setIndexed(true);
							setType(Type.FORM_CONTAINER);
						}
					},
					externalReferenceCode)));

		_assertStyledLayoutStructureItemBackgroundImage(
			missingItemExternalReferenceBackgroundImageValue, 0, null,
			externalReferenceCode);
	}

	private void
			_testPutSitePageSpecificationPageExperiencePageElementWithFormRelationshipPageElement(
				ObjectDefinition objectDefinition)
		throws Exception {

		String formContainerExternalReferenceCode =
			RandomTestUtil.randomString();

		_testPutSitePageSpecificationPageExperiencePageElement(
			_getFormContainerPageElement(
				null, objectDefinition.getClassName(), null, false,
				"displayPage", FormContainerConfig.FormContainerType.SIMPLE,
				false, 1, formContainerExternalReferenceCode, null));

		BackgroundImageValue backgroundImageValue =
			ImageValueTestUtil.getDirectBackgroundImageValue(
				null, RandomTestUtil.randomString());
		String formRelationshipExternalReferenceCode =
			RandomTestUtil.randomString();

		_testPutSitePageSpecificationPageExperiencePageElement(
			_getFormRelationshipPageElement(
				backgroundImageValue, _getRandomFragmentInlineValue(),
				RandomTestUtil.randomString(),
				RandomTestUtil.randomStrings(RandomTestUtil.randomInt(1, 10)),
				true, RandomTestUtil.randomString(),
				formRelationshipExternalReferenceCode,
				formContainerExternalReferenceCode, true));

		_assertStyledLayoutStructureItemBackgroundImage(
			backgroundImageValue, 0, null,
			formRelationshipExternalReferenceCode);

		_testPutSitePageSpecificationPageExperiencePageElement(
			_getFormRelationshipPageElement(
				null, null, null, null, false, null,
				formRelationshipExternalReferenceCode,
				formContainerExternalReferenceCode, false));
	}

	private void _testPutSitePageSpecificationPageExperiencePageElementWithFragmentPageElement()
		throws Exception {

		_testPutSitePageSpecificationPageExperiencePageElement(
			_randomPageElement(
				PageElementDefinition.Type.BASIC_FRAGMENT, StringPool.BLANK));

		BackgroundImageValue backgroundImageValue =
			ImageValueTestUtil.getDirectBackgroundImageValue(
				null, RandomTestUtil.randomString());
		String externalReferenceCode = RandomTestUtil.randomString();

		_testPutSitePageSpecificationPageExperiencePageElement(
			_getFragmentInstancePageElement(
				PageElementsTestUtil.
					getBasicFragmentInstancePageElementDefinition(
						backgroundImageValue, Collections.emptyMap(),
						"BASIC_COMPONENT-button", testGroup.getGroupId()),
				externalReferenceCode));

		_assertStyledLayoutStructureItemBackgroundImage(
			backgroundImageValue, 0, null, externalReferenceCode);

		FileEntry fileEntry = _getFileEntry(testGroup.getGroupId());

		backgroundImageValue = ImageValueTestUtil.getDirectBackgroundImageValue(
			ReferencesTestUtil.getItemExternalReference(
				fileEntry, testGroup.getGroupId()),
			null);

		_testPutSitePageSpecificationPageExperiencePageElement(
			_getFragmentInstancePageElement(
				PageElementsTestUtil.
					getBasicFragmentInstancePageElementDefinition(
						backgroundImageValue, Collections.emptyMap(),
						"com.liferay.fragment.internal.renderer." +
							"ContentObjectFragmentRenderer",
						testGroup.getGroupId()),
				externalReferenceCode));

		_assertStyledLayoutStructureItemBackgroundImage(
			backgroundImageValue, fileEntry.getFileEntryId(), fileEntry,
			externalReferenceCode);

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			testGroup.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		backgroundImageValue = ImageValueTestUtil.getMappedBackgroundImageValue(
			JournalArticle.class.getName(),
			journalArticle.getExternalReferenceCode(),
			"JournalArticle_authorProfileImage", null);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(testGroup.getGroupId());

		_testPutSitePageSpecificationPageExperiencePageElement(
			_getFragmentInstancePageElement(
				PageElementsTestUtil.
					getBasicFragmentInstancePageElementDefinition(
						backgroundImageValue, Collections.emptyMap(),
						new FragmentEditableElement[0],
						_addFragmentEntry(
							null, irrelevantGroup.getGroupId(), serviceContext),
						testGroup.getGroupId()),
				externalReferenceCode));

		_assertStyledLayoutStructureItemBackgroundImage(
			backgroundImageValue, journalArticle.getResourcePrimKey(),
			journalArticle, externalReferenceCode);

		backgroundImageValue = ImageValueTestUtil.getMappedBackgroundImageValue(
			FragmentMappedValueItemContextReference.ContextSource.
				DISPLAY_PAGE_ITEM,
			"JournalArticle_authorProfileImage",
			FragmentMappedValueItemReference.Type.CONTEXT_REFERENCE);

		_testPutSitePageSpecificationPageExperiencePageElement(
			_getFragmentInstancePageElement(
				PageElementsTestUtil.
					getBasicFragmentInstancePageElementDefinition(
						backgroundImageValue, Collections.emptyMap(),
						new FragmentEditableElement[0],
						_addFragmentEntry(
							null, testGroup.getGroupId(), serviceContext),
						testGroup.getGroupId()),
				externalReferenceCode));

		_assertStyledLayoutStructureItemBackgroundImage(
			backgroundImageValue, 0, null, externalReferenceCode);

		backgroundImageValue = ImageValueTestUtil.getMappedBackgroundImageValue(
			FragmentMappedValueItemContextReference.ContextSource.
				COLLECTION_ITEM,
			"AssetEntry_userProfileImage",
			FragmentMappedValueItemReference.Type.CONTEXT_REFERENCE);

		_testPutSitePageSpecificationPageExperiencePageElement(
			_getFragmentInstancePageElement(
				PageElementsTestUtil.
					getBasicFragmentInstancePageElementDefinition(
						backgroundImageValue, Collections.emptyMap(),
						new FragmentEditableElement[0],
						_addFragmentEntry(
							null, testGroup.getGroupId(), serviceContext),
						testGroup.getGroupId()),
				externalReferenceCode));

		_assertStyledLayoutStructureItemBackgroundImage(
			backgroundImageValue, 0, null, externalReferenceCode);

		externalReferenceCode = RandomTestUtil.randomString();

		FragmentEntry fragmentEntry = _addFragmentEntry(
			null, testGroup.getGroupId(),
			"<lfr-widget-web-content></lfr-widget-web-content>",
			serviceContext);
		String fragmentInstanceExternalReferenceCode =
			RandomTestUtil.randomString();
		String namespace = RandomTestUtil.randomString();
		String uuid = RandomTestUtil.randomString();

		_testPutSitePageSpecificationPageExperiencePageElement(
			_getFragmentInstancePageElement(
				PageElementsTestUtil.
					getBasicFragmentInstancePageElementDefinition(
						null, Collections.emptyMap(),
						new FragmentEditableElement[0], fragmentEntry,
						fragmentInstanceExternalReferenceCode, namespace,
						testGroup.getGroupId(), uuid,
						new WidgetInstance[] {
							_getWidgetInstance(
								_getWidgetConfig(), namespace,
								JournalContentPortletKeys.JOURNAL_CONTENT,
								_getWidgetPermissions())
						}),
				externalReferenceCode));
		_testPutSitePageSpecificationPageExperiencePageElement(
			_getFragmentInstancePageElement(
				PageElementsTestUtil.
					getBasicFragmentInstancePageElementDefinition(
						null, Collections.emptyMap(),
						new FragmentEditableElement[0], fragmentEntry,
						fragmentInstanceExternalReferenceCode, namespace,
						testGroup.getGroupId(), uuid,
						new WidgetInstance[] {
							_getWidgetInstance(
								new HashMap<>(), namespace,
								JournalContentPortletKeys.JOURNAL_CONTENT,
								new WidgetPermission[0])
						}),
				externalReferenceCode));

		_testPutSitePageSpecificationPageExperiencePageElementWithCollectionFilterFragmentRendererConfiguration();
		_testPutSitePageSpecificationPageExperiencePageElementWithFragmentPageElementWithConfiguration();
		_testPutSitePageSpecificationPageExperiencePageElementWithFragmentPageElementWithFragmentEditableElements();

		_testPutSitePageSpecificationPageExperiencePageElementWithFragmentPageElementAndMissingOptionalReference(
			externalReferenceCode,
			_randomFragmentEntry(
				RandomTestUtil.randomLong(), StringPool.BLANK,
				irrelevantGroup.getGroupId()),
			_randomItemExternalReference());
		_testPutSitePageSpecificationPageExperiencePageElementWithFragmentPageElementAndMissingOptionalReference(
			externalReferenceCode,
			_randomFragmentEntry(
				RandomTestUtil.randomLong(), StringPool.BLANK,
				testGroup.getGroupId()),
			ReferencesTestUtil.getItemExternalReference(
				HashMapBuilder.put(
					"className", FileEntry.class.getName()
				).put(
					"externalReferenceCode", RandomTestUtil.randomString()
				).build(),
				testGroup.getGroupId()));
		_testPutSitePageSpecificationPageExperiencePageElementWithFragmentPageElementAndMissingOptionalReference(
			externalReferenceCode,
			_randomFragmentRenderer(RandomTestUtil.randomString()),
			_randomItemExternalReference());
	}

	private void
			_testPutSitePageSpecificationPageExperiencePageElementWithFragmentPageElementAndMissingOptionalReference(
				String externalReferenceCode, FragmentEntry fragmentEntry,
				ItemExternalReference itemExternalReference)
		throws Exception {

		_testMissingOptionalReference(
			2,
			() -> _testPutSitePageSpecificationPageExperiencePageElement(
				_getFragmentInstancePageElement(
					PageElementsTestUtil.
						getBasicFragmentInstancePageElementDefinition(
							ImageValueTestUtil.getDirectBackgroundImageValue(
								itemExternalReference, null),
							Collections.emptyMap(),
							new FragmentEditableElement[0], fragmentEntry,
							testGroup.getGroupId()),
					externalReferenceCode)));
	}

	private void
			_testPutSitePageSpecificationPageExperiencePageElementWithFragmentPageElementAndMissingOptionalReference(
				String externalReferenceCode, FragmentRenderer fragmentRenderer,
				ItemExternalReference itemExternalReference)
		throws Exception {

		_testMissingOptionalReference(
			2,
			() -> _testPutSitePageSpecificationPageExperiencePageElement(
				_getFragmentInstancePageElement(
					PageElementsTestUtil.
						getBasicFragmentInstancePageElementDefinition(
							ImageValueTestUtil.getDirectBackgroundImageValue(
								itemExternalReference, null),
							Collections.emptyMap(),
							new FragmentEditableElement[0], fragmentRenderer,
							testGroup.getGroupId()),
					externalReferenceCode)));
	}

	private void _testPutSitePageSpecificationPageExperiencePageElementWithFragmentPageElementWithConfiguration()
		throws Exception {

		String categoryFieldName = RandomTestUtil.randomString();
		String checkboxFieldName = RandomTestUtil.randomString();
		String collectionFieldName = RandomTestUtil.randomString();
		String colorPaletteFieldName = RandomTestUtil.randomString();
		String colorPickerFieldName = RandomTestUtil.randomString();
		String itemFieldName = RandomTestUtil.randomString();
		String lengthFieldName = RandomTestUtil.randomString();
		String navigationMenuFieldName = RandomTestUtil.randomString();
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

		String targetCollectionDisplayFieldName = RandomTestUtil.randomString();
		String textFieldName = RandomTestUtil.randomString();
		String urlFieldName = RandomTestUtil.randomString();
		String videoFieldName = RandomTestUtil.randomString();

		_testPutSitePageSpecificationPageExperiencePageElementWithFragmentPageElementWithConfiguration(
			FragmentConfigurationTestUtil.getConfiguration(
				HashMapBuilder.<String, Map<String, Object>>put(
					categoryFieldName,
					HashMapBuilder.<String, Object>put(
						"type", "categoryTreeNodeSelector"
					).build()
				).put(
					checkboxFieldName,
					HashMapBuilder.<String, Object>put(
						"defaultValue", RandomTestUtil.randomBoolean()
					).put(
						"type", "checkbox"
					).build()
				).put(
					collectionFieldName,
					HashMapBuilder.<String, Object>put(
						"type", "collectionSelector"
					).build()
				).put(
					colorPaletteFieldName,
					HashMapBuilder.<String, Object>put(
						"type", "colorPalette"
					).build()
				).put(
					colorPickerFieldName,
					HashMapBuilder.<String, Object>put(
						"type", "colorPicker"
					).build()
				).put(
					itemFieldName,
					HashMapBuilder.<String, Object>put(
						"type", "itemSelector"
					).build()
				).put(
					lengthFieldName,
					HashMapBuilder.<String, Object>put(
						"defaultValue", RandomTestUtil.randomString()
					).put(
						"type", "length"
					).build()
				).put(
					navigationMenuFieldName,
					HashMapBuilder.<String, Object>put(
						"type", "navigationMenuSelector"
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
					targetCollectionDisplayFieldName,
					HashMapBuilder.<String, Object>put(
						"type", "targetCollectionDisplay"
					).build()
				).put(
					textFieldName,
					HashMapBuilder.<String, Object>put(
						"defaultValue", RandomTestUtil.randomBoolean()
					).put(
						"type", "text"
					).build()
				).put(
					urlFieldName,
					HashMapBuilder.<String, Object>put(
						"type", "url"
					).build()
				).put(
					videoFieldName,
					HashMapBuilder.<String, Object>put(
						"type", "videoSelector"
					).build()
				).build()),
			HashMapBuilder.<String, Object>put(
				categoryFieldName, _getAssetCategory(testGroup.getGroupId())
			).put(
				checkboxFieldName, RandomTestUtil.randomBoolean()
			).put(
				collectionFieldName, _getAssetListEntry(testGroup.getGroupId())
			).put(
				colorPaletteFieldName,
				HashMapBuilder.put(
					"color", RandomTestUtil.randomString()
				).put(
					"cssClass", RandomTestUtil.randomString()
				).put(
					"rgbValue", RandomTestUtil.randomString()
				).build()
			).put(
				colorPickerFieldName, RandomTestUtil.randomString()
			).put(
				itemFieldName,
				HashMapBuilder.put(
					"item", _getFileEntry(testGroup.getGroupId())
				).build()
			).put(
				lengthFieldName, RandomTestUtil.randomString()
			).put(
				navigationMenuFieldName,
				() -> {
					SiteNavigationMenu siteNavigationMenu =
						_getSiteNavigationMenu(irrelevantGroup.getGroupId());

					SiteNavigationMenuItem siteNavigationMenuItem =
						_getSiteNavigationMenuItem(siteNavigationMenu);

					return HashMapBuilder.<String, Object>put(
						"siteNavigationMenu", siteNavigationMenu
					).put(
						"siteNavigationMenuItemExternalReferenceCode",
						siteNavigationMenuItem.getExternalReferenceCode()
					).build();
				}
			).put(
				selectFieldName, selectValue1
			).put(
				targetCollectionDisplayFieldName,
				new String[] {
					RandomTestUtil.randomString(), RandomTestUtil.randomString()
				}
			).put(
				textFieldName, RandomTestUtil.randomString()
			).put(
				urlFieldName,
				HashMapBuilder.put(
					"href", RandomTestUtil.randomString()
				).build()
			).put(
				videoFieldName,
				HashMapBuilder.put(
					"html", RandomTestUtil.randomString()
				).put(
					"title", RandomTestUtil.randomString()
				).build()
			).build(),
			HashMapBuilder.<String, Object>put(
				categoryFieldName,
				_getAssetVocabulary(irrelevantGroup.getGroupId())
			).put(
				checkboxFieldName, RandomTestUtil.randomBoolean()
			).put(
				collectionFieldName,
				"com.liferay.asset.internal.info.collection.provider." +
					"HighestRatedAssetsInfoCollectionProvider"
			).put(
				colorPaletteFieldName,
				HashMapBuilder.put(
					"color", RandomTestUtil.randomString()
				).put(
					"cssClass", RandomTestUtil.randomString()
				).put(
					"rgbValue", RandomTestUtil.randomString()
				).build()
			).put(
				colorPickerFieldName, RandomTestUtil.randomString()
			).put(
				itemFieldName,
				HashMapBuilder.put(
					"item", _getFileEntry(irrelevantGroup.getGroupId())
				).build()
			).put(
				lengthFieldName, RandomTestUtil.randomString()
			).put(
				navigationMenuFieldName,
				HashMapBuilder.put(
					"parentLayoutExternalReferenceCode",
					_layout.getExternalReferenceCode()
				).build()
			).put(
				selectFieldName, selectValue2
			).put(
				targetCollectionDisplayFieldName,
				new String[] {RandomTestUtil.randomString()}
			).put(
				textFieldName, RandomTestUtil.randomString()
			).put(
				urlFieldName,
				HashMapBuilder.put(
					"layout", _layout
				).build()
			).put(
				videoFieldName,
				HashMapBuilder.put(
					"html", RandomTestUtil.randomString()
				).put(
					"title", RandomTestUtil.randomString()
				).build()
			).build(),
			Collections.emptyMap());

		_testMissingOptionalReference(
			5,
			() ->
				_testPutSitePageSpecificationPageExperiencePageElementWithFragmentPageElementWithConfiguration(
					FragmentConfigurationTestUtil.getConfiguration(
						HashMapBuilder.<String, Map<String, Object>>put(
							categoryFieldName,
							HashMapBuilder.<String, Object>put(
								"localized", true
							).put(
								"type", "categoryTreeNodeSelector"
							).build()
						).put(
							checkboxFieldName,
							HashMapBuilder.<String, Object>put(
								"defaultValue", RandomTestUtil.randomBoolean()
							).put(
								"localized", true
							).put(
								"type", "checkbox"
							).build()
						).put(
							collectionFieldName,
							HashMapBuilder.<String, Object>put(
								"type", "collectionSelector"
							).build()
						).put(
							colorPaletteFieldName,
							HashMapBuilder.<String, Object>put(
								"localized", true
							).put(
								"type", "colorPalette"
							).build()
						).put(
							colorPickerFieldName,
							HashMapBuilder.<String, Object>put(
								"localized", true
							).put(
								"type", "colorPicker"
							).build()
						).put(
							itemFieldName,
							HashMapBuilder.<String, Object>put(
								"localized", true
							).put(
								"type", "itemSelector"
							).put(
								"typeOptions",
								JSONUtil.put("enableSelectTemplate", true)
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
							navigationMenuFieldName,
							HashMapBuilder.<String, Object>put(
								"localized", true
							).put(
								"type", "navigationMenuSelector"
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
						).put(
							urlFieldName,
							HashMapBuilder.<String, Object>put(
								"localized", true
							).put(
								"type", "url"
							).build()
						).put(
							videoFieldName,
							HashMapBuilder.<String, Object>put(
								"localized", true
							).put(
								"type", "videoSelector"
							).build()
						).build()),
					HashMapBuilder.<String, Object>put(
						categoryFieldName,
						_getAssetCategory(irrelevantGroup.getGroupId())
					).put(
						checkboxFieldName, RandomTestUtil.randomBoolean()
					).put(
						collectionFieldName,
						_getAssetListEntry(irrelevantGroup.getGroupId())
					).put(
						colorPaletteFieldName,
						HashMapBuilder.put(
							"color", RandomTestUtil.randomString()
						).put(
							"cssClass", RandomTestUtil.randomString()
						).put(
							"rgbValue", RandomTestUtil.randomString()
						).build()
					).put(
						colorPickerFieldName, RandomTestUtil.randomString()
					).put(
						itemFieldName,
						HashMapBuilder.<String, Object>put(
							"item",
							JournalTestUtil.addArticle(
								irrelevantGroup.getGroupId(),
								JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID)
						).put(
							"template",
							HashMapBuilder.put(
								"infoItemRendererKey",
								"com.liferay.journal.web.internal.info.item." +
									"renderer." +
										"JournalArticleTitleInfoItemRenderer"
							).build()
						).build()
					).put(
						lengthFieldName, RandomTestUtil.randomString()
					).put(
						navigationMenuFieldName,
						HashMapBuilder.put(
							"contextualMenu",
							ContextualMenuNavigationMenuValue.
								ContextualMenuType.CHILDREN
						).build()
					).put(
						selectFieldName, selectValue1
					).put(
						textFieldName, RandomTestUtil.randomString()
					).put(
						urlFieldName,
						HashMapBuilder.put(
							"layout",
							LayoutTestUtil.addTypeContentLayout(irrelevantGroup)
						).build()
					).put(
						videoFieldName,
						HashMapBuilder.put(
							"html", RandomTestUtil.randomString()
						).put(
							"title", RandomTestUtil.randomString()
						).build()
					).build(),
					HashMapBuilder.<String, Object>put(
						categoryFieldName,
						HashMapBuilder.put(
							"className", AssetCategory.class.getName()
						).put(
							"externalReferenceCode",
							RandomTestUtil.randomString()
						).put(
							"scopeExternalReferenceCode",
							RandomTestUtil.randomString()
						).build()
					).put(
						checkboxFieldName, RandomTestUtil.randomBoolean()
					).put(
						collectionFieldName,
						HashMapBuilder.put(
							"externalReferenceCode",
							RandomTestUtil.randomString()
						).put(
							"scopeExternalReferenceCode",
							RandomTestUtil.randomString()
						).build()
					).put(
						colorPaletteFieldName,
						HashMapBuilder.put(
							"color", RandomTestUtil.randomString()
						).put(
							"cssClass", RandomTestUtil.randomString()
						).put(
							"rgbValue", RandomTestUtil.randomString()
						).build()
					).put(
						colorPickerFieldName, RandomTestUtil.randomString()
					).put(
						itemFieldName,
						HashMapBuilder.put(
							"item",
							HashMapBuilder.put(
								"className", JournalArticle.class.getName()
							).put(
								"externalReferenceCode",
								RandomTestUtil.randomString()
							).put(
								"scopeExternalReferenceCode",
								RandomTestUtil.randomString()
							).build()
						).put(
							"template",
							HashMapBuilder.put(
								"templateKey", RandomTestUtil.randomString()
							).build()
						).build()
					).put(
						lengthFieldName, RandomTestUtil.randomString()
					).put(
						navigationMenuFieldName,
						HashMapBuilder.put(
							"siteNavigationMenu",
							HashMapBuilder.<String, Object>put(
								"className", SiteNavigationMenu.class.getName()
							).put(
								"externalReferenceCode",
								RandomTestUtil.randomString()
							).put(
								"scopeExternalReferenceCode",
								RandomTestUtil.randomString()
							).build()
						).build()
					).put(
						selectFieldName, selectValue2
					).put(
						textFieldName, RandomTestUtil.randomString()
					).put(
						urlFieldName,
						HashMapBuilder.put(
							"layout",
							HashMapBuilder.<String, Object>put(
								"className", Layout.class.getName()
							).put(
								"externalReferenceCode",
								RandomTestUtil.randomString()
							).put(
								"scopeExternalReferenceCode",
								RandomTestUtil.randomString()
							).build()
						).build()
					).put(
						videoFieldName,
						HashMapBuilder.put(
							"html", RandomTestUtil.randomString()
						).put(
							"title", RandomTestUtil.randomString()
						).build()
					).build(),
					Collections.emptyMap()));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.headless.admin.site.internal.resource.v1_0." +
					"layout.structure.item.importer.util." +
						"FragmentConfigurationFieldValuesUtil",
				LoggerTestUtil.DEBUG)) {

			String invalidSelectValue = RandomTestUtil.randomString();

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
					selectFieldName, invalidSelectValue
				).build());

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				StringBundler.concat(
					"Invalid configuration value \"", invalidSelectValue,
					"\" for field \"", selectFieldName, "\""),
				logEntry.getMessage());
		}
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
					PageElementsTestUtil.
						getBasicFragmentInstancePageElementDefinition(
							null, configurationValuesMap,
							new FragmentEditableElement[0], fragmentEntry,
							testGroup.getGroupId()),
					externalReferenceCode));
		}
	}

	private void _testPutSitePageSpecificationPageExperiencePageElementWithFragmentPageElementWithFragmentEditableElements()
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			testGroup.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		FileEntry fileEntry1 = _getFileEntry(testGroup.getGroupId());
		FileEntry fileEntry2 = _getFileEntry(irrelevantGroup.getGroupId());

		_testPutSitePageSpecificationPageExperiencePageElementWithFragmentPageElementWithFragmentEditableElements(
			FragmentEditableElementTestUtil.getActionFragmentEditableElement(
				FragmentEditableElementTestUtil.getNoneActionInteraction(),
				FragmentMappedValueTestUtil.getFragmentMappedValue(
					FileEntry.class.getName(),
					fileEntry1.getExternalReferenceCode(), "FileEntry_fileURL",
					null),
				"element-action1",
				FragmentEditableElementTestUtil.
					getDisplayPageActionInteraction(),
				FragmentEditableElementTestUtil.getTextFragmentMappedValue(
					FragmentMappedValueTestUtil.getFragmentMappedValue(
						JournalArticle.class.getName(),
						journalArticle.getExternalReferenceCode(),
						"JournalArticle_title", null))),
			FragmentEditableElementTestUtil.getActionFragmentEditableElement(
				FragmentEditableElementTestUtil.
					getNotificationActionInteraction(),
				FragmentMappedValueTestUtil.getFragmentMappedValue(
					FileEntry.class.getName(),
					fileEntry2.getExternalReferenceCode(), "FileEntry_fileURL",
					irrelevantGroup.getExternalReferenceCode()),
				"element-action2",
				FragmentEditableElementTestUtil.getNoneActionInteraction(),
				FragmentEditableElementTestUtil.getTextFragmentInlineValue()),
			FragmentEditableElementTestUtil.
				getBackgroundImageFragmentEditableElement(
					ImageValueTestUtil.getDirectFragmentImageValue(
						ReferencesTestUtil.getItemExternalReference(
							fileEntry1, testGroup.getGroupId()),
						RandomTestUtil.randomString()),
					"element-background-image1"),
			FragmentEditableElementTestUtil.
				getBackgroundImageFragmentEditableElement(
					ImageValueTestUtil.getDirectFragmentImageValue(
						ReferencesTestUtil.getItemExternalReference(
							fileEntry2, testGroup.getGroupId()),
						null),
					"element-background-image2"),
			FragmentEditableElementTestUtil.getDateFragmentEditableElement(
				FragmentMappedValueTestUtil.getFragmentMappedValue(
					JournalArticle.class.getName(),
					journalArticle.getExternalReferenceCode(),
					"JournalArticle_displayDate", null),
				new FragmentInlineValue() {
					{
						setValue_i18n(
							HashMapBuilder.put(
								"en-US", "d MMM yyyy"
							).put(
								"es-ES", "d MMM yyyy"
							).build());
					}
				},
				"element-date"),
			FragmentEditableElementTestUtil.getHTMLFragmentEditableElement(
				null, FragmentEditableElementValue.Type.HTML, null,
				HTMLFragmentValue.Type.INLINE, "element-html"),
			FragmentEditableElementTestUtil.getImageFragmentEditableElement(
				FragmentEditableElementTestUtil.getFragmentImage(
					HashMapBuilder.put(
						LocaleUtil.toBCP47LanguageId(
							LocaleUtil.getMostRelevantLocale()),
						RandomTestUtil.randomString()
					).build(),
					ImageValueTestUtil.getDirectFragmentImageValue(
						ReferencesTestUtil.getItemExternalReference(
							fileEntry1, testGroup.getGroupId()),
						RandomTestUtil.randomString()),
					Boolean.FALSE,
					HashMapBuilder.put(
						"desktop", "auto"
					).build()),
				"element-image1"),
			FragmentEditableElementTestUtil.getImageFragmentEditableElement(
				FragmentEditableElementTestUtil.getFragmentImage(
					HashMapBuilder.put(
						LocaleUtil.toBCP47LanguageId(
							LocaleUtil.getMostRelevantLocale()),
						RandomTestUtil.randomString()
					).put(
						LocaleUtil.toBCP47LanguageId(LocaleUtil.SPAIN),
						RandomTestUtil.randomString()
					).build(),
					ImageValueTestUtil.getDirectFragmentImageValue(
						ReferencesTestUtil.getItemExternalReference(
							fileEntry2, testGroup.getGroupId()),
						RandomTestUtil.randomString()),
					Boolean.TRUE,
					HashMapBuilder.put(
						"desktop", "auto"
					).put(
						"landscapeMobile", "preview-1000x0"
					).put(
						"tablet", "auto"
					).build()),
				"element-image2"),
			FragmentEditableElementTestUtil.getImageFragmentEditableElement(
				FragmentEditableElementTestUtil.getFragmentImage(
					HashMapBuilder.put(
						LocaleUtil.toBCP47LanguageId(
							LocaleUtil.getMostRelevantLocale()),
						RandomTestUtil.randomString()
					).put(
						LocaleUtil.toBCP47LanguageId(LocaleUtil.SPAIN),
						RandomTestUtil.randomString()
					).build(),
					ImageValueTestUtil.getDirectFragmentImageValue(
						null, RandomTestUtil.randomString()),
					null,
					HashMapBuilder.put(
						"desktop", "auto"
					).put(
						"landscapeMobile", "preview-1000x0"
					).put(
						"tablet", "auto"
					).build()),
				"element-image3"),
			FragmentEditableElementTestUtil.getLinkFragmentEditableElement(
				null,
				_getFragmentLink(
					JournalArticle.class.getName(),
					journalArticle.getExternalReferenceCode(),
					"JournalArticle_title", null),
				null, "element-link",
				FragmentEditableElementValueFragmentLink.Prefix.EMAIL,
				TextFragmentValue.Type.INLINE),
			FragmentEditableElementTestUtil.getHTMLFragmentEditableElement(
				null, FragmentEditableElementValue.Type.RICH_TEXT, null,
				HTMLFragmentValue.Type.INLINE, "element-rich-text"),
			FragmentEditableElementTestUtil.getTextFragmentEditableElement(
				null,
				_getFragmentLink(
					JournalArticle.class.getName(),
					journalArticle.getExternalReferenceCode(),
					"JournalArticle_title", null),
				null, FragmentEditableElementValueFragmentLink.Prefix.EMAIL,
				TextFragmentValue.Type.INLINE));

		Layout layout = LayoutTestUtil.addTypeContentLayout(testGroup);

		_testPutSitePageSpecificationPageExperiencePageElementWithFragmentPageElementWithFragmentEditableElements(
			FragmentEditableElementTestUtil.getActionFragmentEditableElement(
				FragmentEditableElementTestUtil.getPageActionInteraction(
					ReferencesTestUtil.getItemExternalReference(
						layout, testGroup.getGroupId())),
				FragmentMappedValueTestUtil.getFragmentMappedValue(
					JournalArticle.class.getName(),
					journalArticle.getExternalReferenceCode(),
					"JournalArticle_displayPageURL", null),
				"element-action1",
				FragmentEditableElementTestUtil.getNoneActionInteraction(),
				FragmentEditableElementTestUtil.getTextFragmentMappedValue(
					FragmentMappedValueTestUtil.getFragmentMappedValue(
						FileEntry.class.getName(),
						fileEntry2.getExternalReferenceCode(),
						"FileEntry_title",
						irrelevantGroup.getExternalReferenceCode()))),
			FragmentEditableElementTestUtil.getActionFragmentEditableElement(
				FragmentEditableElementTestUtil.getURLActionInteraction(),
				FragmentMappedValueTestUtil.getFragmentMappedValue(
					FileEntry.class.getName(),
					fileEntry1.getExternalReferenceCode(), "FileEntry_fileURL",
					null),
				"element-action2",
				FragmentEditableElementTestUtil.
					getNotificationActionInteraction(),
				null),
			FragmentEditableElementTestUtil.
				getBackgroundImageFragmentEditableElement(
					ImageValueTestUtil.getDirectFragmentImageValue(
						null, RandomTestUtil.randomString()),
					"element-background-image1"),
			FragmentEditableElementTestUtil.
				getBackgroundImageFragmentEditableElement(
					ImageValueTestUtil.getMappedFragmentImageValue(
						FragmentMappedValueItemContextReference.ContextSource.
							COLLECTION_ITEM,
						"JournalArticle_authorProfileImage",
						FragmentMappedValueItemReference.Type.
							CONTEXT_REFERENCE),
					"element-background-image2"),
			FragmentEditableElementTestUtil.getDateFragmentEditableElement(
				FragmentMappedValueTestUtil.getFragmentMappedValue(
					JournalArticle.class.getName(),
					journalArticle.getExternalReferenceCode(),
					"JournalArticle_modifiedDate", null),
				null, "element-date"),
			FragmentEditableElementTestUtil.getHTMLFragmentEditableElement(
				FragmentMappedValueItemContextReference.ContextSource.
					COLLECTION_ITEM,
				FragmentEditableElementValue.Type.HTML,
				FragmentMappedValueItemReference.Type.CONTEXT_REFERENCE,
				HTMLFragmentValue.Type.MAPPED, "element-html"),
			FragmentEditableElementTestUtil.getImageFragmentEditableElement(
				FragmentEditableElementTestUtil.getFragmentImage(
					HashMapBuilder.put(
						LocaleUtil.toBCP47LanguageId(
							LocaleUtil.getMostRelevantLocale()),
						RandomTestUtil.randomString()
					).build(),
					ImageValueTestUtil.getMappedFragmentImageValue(
						FragmentMappedValueItemContextReference.ContextSource.
							COLLECTION_ITEM,
						"JournalArticle_authorProfileImage",
						FragmentMappedValueItemReference.Type.
							CONTEXT_REFERENCE),
					Boolean.FALSE,
					HashMapBuilder.put(
						"tablet", "auto"
					).build()),
				"element-image1"),
			FragmentEditableElementTestUtil.getImageFragmentEditableElement(
				FragmentEditableElementTestUtil.getFragmentImage(
					HashMapBuilder.put(
						LocaleUtil.toBCP47LanguageId(
							LocaleUtil.getMostRelevantLocale()),
						RandomTestUtil.randomString()
					).put(
						LocaleUtil.toBCP47LanguageId(LocaleUtil.SPAIN),
						RandomTestUtil.randomString()
					).build(),
					ImageValueTestUtil.getMappedFragmentImageValue(
						FileEntry.class.getName(),
						fileEntry1.getExternalReferenceCode(),
						"FileEntry_fileURL", null),
					Boolean.TRUE,
					HashMapBuilder.put(
						"desktop", "auto"
					).put(
						"tablet", "preview-1000x0"
					).build()),
				"element-image2"),
			FragmentEditableElementTestUtil.getImageFragmentEditableElement(
				FragmentEditableElementTestUtil.getFragmentImage(
					HashMapBuilder.put(
						LocaleUtil.toBCP47LanguageId(
							LocaleUtil.getMostRelevantLocale()),
						RandomTestUtil.randomString()
					).build(),
					ImageValueTestUtil.getDirectFragmentImageValue(
						ReferencesTestUtil.getItemExternalReference(
							fileEntry2, testGroup.getGroupId()),
						RandomTestUtil.randomString()),
					Boolean.TRUE,
					HashMapBuilder.put(
						"landscapeMobile", "preview-1000x0"
					).put(
						"tablet", "auto"
					).build()),
				"element-image3"),
			FragmentEditableElementTestUtil.getLinkFragmentEditableElement(
				FragmentMappedValueItemContextReference.ContextSource.
					COLLECTION_ITEM,
				null, FragmentMappedValueItemReference.Type.CONTEXT_REFERENCE,
				"element-link", null, TextFragmentValue.Type.MAPPED),
			FragmentEditableElementTestUtil.getHTMLFragmentEditableElement(
				FragmentMappedValueItemContextReference.ContextSource.
					COLLECTION_ITEM,
				FragmentEditableElementValue.Type.RICH_TEXT,
				FragmentMappedValueItemReference.Type.CONTEXT_REFERENCE,
				HTMLFragmentValue.Type.MAPPED, "element-rich-text"),
			FragmentEditableElementTestUtil.getTextFragmentEditableElement(
				FragmentMappedValueItemContextReference.ContextSource.
					COLLECTION_ITEM,
				null, FragmentMappedValueItemReference.Type.CONTEXT_REFERENCE,
				null, TextFragmentValue.Type.MAPPED));

		_testPutSitePageSpecificationPageExperiencePageElementWithFragmentPageElementWithFragmentEditableElements(
			FragmentEditableElementTestUtil.getActionFragmentEditableElement(
				null,
				FragmentMappedValueTestUtil.getFragmentMappedValue(
					JournalArticle.class.getName(),
					journalArticle.getExternalReferenceCode(),
					"JournalArticle_displayPageURL", null),
				"element-action1",
				FragmentEditableElementTestUtil.getURLActionInteraction(),
				null),
			FragmentEditableElementTestUtil.getActionFragmentEditableElement(
				null, null, "element-action2", null,
				FragmentEditableElementTestUtil.getTextFragmentMappedValue(
					FragmentMappedValueTestUtil.getFragmentMappedValue(
						FileEntry.class.getName(),
						fileEntry1.getExternalReferenceCode(),
						"FileEntry_title", null))),
			FragmentEditableElementTestUtil.
				getBackgroundImageFragmentEditableElement(
					ImageValueTestUtil.getMappedFragmentImageValue(
						FragmentMappedValueItemContextReference.ContextSource.
							DISPLAY_PAGE_ITEM,
						"JournalArticle_authorProfileImage",
						FragmentMappedValueItemReference.Type.
							CONTEXT_REFERENCE),
					"element-background-image1"),
			FragmentEditableElementTestUtil.getDateFragmentEditableElement(
				FragmentMappedValueTestUtil.getFragmentMappedValue(
					FragmentMappedValueItemContextReference.ContextSource.
						DISPLAY_PAGE_ITEM,
					"JournalArticle_displayDate",
					FragmentMappedValueItemReference.Type.CONTEXT_REFERENCE),
				new FragmentInlineValue() {
					{
						setValue_i18n(
							HashMapBuilder.put(
								"en-US", "yyyy-MM-dd"
							).put(
								"es-ES", "dd/MM/yyyy"
							).build());
					}
				},
				"element-date"),
			FragmentEditableElementTestUtil.getHTMLFragmentEditableElement(
				FragmentMappedValueItemContextReference.ContextSource.
					DISPLAY_PAGE_ITEM,
				FragmentEditableElementValue.Type.HTML,
				FragmentMappedValueItemReference.Type.CONTEXT_REFERENCE,
				HTMLFragmentValue.Type.MAPPED, "element-html"),
			FragmentEditableElementTestUtil.getImageFragmentEditableElement(
				FragmentEditableElementTestUtil.getFragmentImage(
					HashMapBuilder.put(
						LocaleUtil.toBCP47LanguageId(
							LocaleUtil.getMostRelevantLocale()),
						RandomTestUtil.randomString()
					).build(),
					ImageValueTestUtil.getMappedFragmentImageValue(
						FragmentMappedValueItemContextReference.ContextSource.
							DISPLAY_PAGE_ITEM,
						"JournalArticle_authorProfileImage",
						FragmentMappedValueItemReference.Type.
							CONTEXT_REFERENCE),
					Boolean.FALSE,
					HashMapBuilder.put(
						"tablet", "auto"
					).build()),
				"element-image1"),
			FragmentEditableElementTestUtil.getLinkFragmentEditableElement(
				FragmentMappedValueItemContextReference.ContextSource.
					DISPLAY_PAGE_ITEM,
				_getFragmentLink(
					Layout.class.getName(), layout.getExternalReferenceCode(),
					null, null),
				FragmentMappedValueItemReference.Type.CONTEXT_REFERENCE,
				"element-link",
				FragmentEditableElementValueFragmentLink.Prefix.PHONE,
				TextFragmentValue.Type.MAPPED),
			FragmentEditableElementTestUtil.getHTMLFragmentEditableElement(
				FragmentMappedValueItemContextReference.ContextSource.
					DISPLAY_PAGE_ITEM,
				FragmentEditableElementValue.Type.RICH_TEXT,
				FragmentMappedValueItemReference.Type.CONTEXT_REFERENCE,
				HTMLFragmentValue.Type.MAPPED, "element-rich-text"),
			FragmentEditableElementTestUtil.getTextFragmentEditableElement(
				FragmentMappedValueItemContextReference.ContextSource.
					DISPLAY_PAGE_ITEM,
				_getFragmentLink(
					Layout.class.getName(), layout.getExternalReferenceCode(),
					null, null),
				FragmentMappedValueItemReference.Type.CONTEXT_REFERENCE,
				FragmentEditableElementValueFragmentLink.Prefix.PHONE,
				TextFragmentValue.Type.MAPPED));

		_testPutSitePageSpecificationPageExperiencePageElementWithFragmentPageElementWithFragmentEditableElements(
			new FragmentEditableElement[0]);

		_testMissingOptionalReference(
			11,
			() ->
				_testPutSitePageSpecificationPageExperiencePageElementWithFragmentPageElementWithFragmentEditableElements(
					FragmentEditableElementTestUtil.
						getActionFragmentEditableElement(
							null,
							FragmentMappedValueTestUtil.getFragmentMappedValue(
								JournalArticle.class.getName(),
								RandomTestUtil.randomString(),
								"JournalArticle_displayPageURL", null),
							"element-action1",
							FragmentEditableElementTestUtil.
								getPageActionInteraction(
									_getItemExternalReference(
										Layout.class.getName(),
										RandomTestUtil.randomString())),
							null),
					FragmentEditableElementTestUtil.
						getActionFragmentEditableElement(
							null, null, "element-action2", null,
							FragmentEditableElementTestUtil.
								getTextFragmentMappedValue(
									FragmentMappedValueTestUtil.
										getFragmentMappedValue(
											FileEntry.class.getName(),
											fileEntry1.
												getExternalReferenceCode(),
											"FileEntry_title",
											RandomTestUtil.randomString()))),
					FragmentEditableElementTestUtil.
						getBackgroundImageFragmentEditableElement(
							ImageValueTestUtil.getDirectFragmentImageValue(
								_randomItemExternalReference(), null),
							"element-background-image1"),
					FragmentEditableElementTestUtil.
						getBackgroundImageFragmentEditableElement(
							ImageValueTestUtil.getMappedFragmentImageValue(
								null, "FileEntry_authorProfileImage",
								FragmentMappedValueItemReference.Type.
									ITEM_EXTERNAL_REFERENCE),
							"element-background-image2"),
					FragmentEditableElementTestUtil.
						getHTMLFragmentEditableElement(
							null, FragmentEditableElementValue.Type.HTML,
							FragmentMappedValueItemReference.Type.
								ITEM_EXTERNAL_REFERENCE,
							HTMLFragmentValue.Type.MAPPED, "element-html"),
					FragmentEditableElementTestUtil.
						getImageFragmentEditableElement(
							FragmentEditableElementTestUtil.getFragmentImage(
								HashMapBuilder.put(
									LocaleUtil.toBCP47LanguageId(
										LocaleUtil.getMostRelevantLocale()),
									RandomTestUtil.randomString()
								).build(),
								ImageValueTestUtil.getDirectFragmentImageValue(
									_randomItemExternalReference(), null),
								Boolean.FALSE,
								HashMapBuilder.put(
									"tablet", "auto"
								).build()),
							"element-image1"),
					FragmentEditableElementTestUtil.
						getImageFragmentEditableElement(
							FragmentEditableElementTestUtil.getFragmentImage(
								HashMapBuilder.put(
									LocaleUtil.toBCP47LanguageId(
										LocaleUtil.getMostRelevantLocale()),
									RandomTestUtil.randomString()
								).build(),
								ImageValueTestUtil.getMappedFragmentImageValue(
									null, "FileEntry_authorProfileImage",
									FragmentMappedValueItemReference.Type.
										ITEM_EXTERNAL_REFERENCE),
								Boolean.FALSE,
								HashMapBuilder.put(
									"desktop", "auto"
								).build()),
							"element-image2"),
					FragmentEditableElementTestUtil.
						getLinkFragmentEditableElement(
							null,
							_getFragmentLink(
								null, null, null,
								HashMapBuilder.put(
									LocaleUtil.toBCP47LanguageId(
										LocaleUtil.SPAIN),
									RandomTestUtil.randomString()
								).put(
									LocaleUtil.toBCP47LanguageId(LocaleUtil.US),
									RandomTestUtil.randomString()
								).build()),
							FragmentMappedValueItemReference.Type.
								ITEM_EXTERNAL_REFERENCE,
							"element-link", null,
							TextFragmentValue.Type.MAPPED),
					FragmentEditableElementTestUtil.
						getHTMLFragmentEditableElement(
							null, FragmentEditableElementValue.Type.RICH_TEXT,
							FragmentMappedValueItemReference.Type.
								ITEM_EXTERNAL_REFERENCE,
							HTMLFragmentValue.Type.MAPPED, "element-rich-text"),
					FragmentEditableElementTestUtil.
						getTextFragmentEditableElement(
							null,
							_getFragmentLink(
								null, null, null,
								HashMapBuilder.put(
									LocaleUtil.toBCP47LanguageId(
										LocaleUtil.SPAIN),
									"https://www.liferay.es"
								).put(
									LocaleUtil.toBCP47LanguageId(LocaleUtil.US),
									"https://www.liferay.com"
								).build()),
							FragmentMappedValueItemReference.Type.
								ITEM_EXTERNAL_REFERENCE,
							null, TextFragmentValue.Type.MAPPED)));
	}

	private void
			_testPutSitePageSpecificationPageExperiencePageElementWithFragmentPageElementWithFragmentEditableElements(
				FragmentEditableElement... fragmentEditableElements)
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		FragmentEntry fragmentEntry = _addFragmentEntry(
			null, testGroup.getGroupId(),
			ServiceContextTestUtil.getServiceContext(testGroup.getGroupId()));

		PageElement pageElement =
			_testPutSitePageSpecificationPageExperiencePageElement(
				_getFragmentInstancePageElement(
					PageElementsTestUtil.
						getBasicFragmentInstancePageElementDefinition(
							null, null, fragmentEditableElements, fragmentEntry,
							testGroup.getGroupId()),
					externalReferenceCode));

		_assertDefaultValues(
			(BasicFragmentInstancePageElementDefinition)
				pageElement.getPageElementDefinition(),
			"element-html", "element-link", "element-rich-text",
			"element-text");
	}

	private void _testPutSitePageSpecificationPageExperiencePageElementWithGridPageElement()
		throws Exception {

		BackgroundImageValue backgroundImageValue =
			ImageValueTestUtil.getMappedBackgroundImageValue(
				FragmentMappedValueItemContextReference.ContextSource.
					DISPLAY_PAGE_ITEM,
				"JournalArticle_authorProfileImage",
				FragmentMappedValueItemReference.Type.CONTEXT_REFERENCE);
		String externalReferenceCode = RandomTestUtil.randomString();

		_testPutSitePageSpecificationPageExperiencePageElement(
			_getGridPageElement(
				backgroundImageValue,
				RandomTestUtil.randomStrings(RandomTestUtil.randomInt(1, 10)),
				false, false, 6, externalReferenceCode,
				_getModulePageElements(externalReferenceCode, 6)));

		_assertStyledLayoutStructureItemBackgroundImage(
			backgroundImageValue, 0, null, externalReferenceCode);

		backgroundImageValue = ImageValueTestUtil.getMappedBackgroundImageValue(
			FragmentMappedValueItemContextReference.ContextSource.
				COLLECTION_ITEM,
			"AssetEntry_userProfileImage",
			FragmentMappedValueItemReference.Type.CONTEXT_REFERENCE);

		_testPutSitePageSpecificationPageExperiencePageElement(
			_getGridPageElement(
				backgroundImageValue, null, true, true, 3,
				externalReferenceCode,
				_getModulePageElements(externalReferenceCode, 3)));

		_assertStyledLayoutStructureItemBackgroundImage(
			backgroundImageValue, 0, null, externalReferenceCode);

		_testMissingOptionalReference(
			1,
			() -> _testPutSitePageSpecificationPageExperiencePageElement(
				_getGridPageElement(
					ImageValueTestUtil.getDirectBackgroundImageValue(
						_randomItemExternalReference(), null),
					RandomTestUtil.randomStrings(
						RandomTestUtil.randomInt(1, 10)),
					false, false, 12, externalReferenceCode,
					_getModulePageElements(externalReferenceCode, 12))));

		_testPutSitePageSpecificationPageExperiencePageElement(
			_getGridPageElementDefaultValues(externalReferenceCode));
	}

	private void _testPutSitePageSpecificationPageExperiencePageElementWithWidgetPageElement()
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			testGroup.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		BackgroundImageValue backgroundImageValue =
			ImageValueTestUtil.getMappedBackgroundImageValue(
				JournalArticle.class.getName(),
				journalArticle.getExternalReferenceCode(),
				"JournalArticle_authorProfileImage", null);

		String externalReferenceCode = RandomTestUtil.randomString();
		String widgetInstanceExternalReferenceCode =
			RandomTestUtil.randomString();

		_testPutSitePageSpecificationPageExperiencePageElement(
			_getWidgetPageElement(
				backgroundImageValue,
				RandomTestUtil.randomStrings(RandomTestUtil.randomInt(1, 10)),
				null, false, RandomTestUtil.randomString(),
				externalReferenceCode, _getWidgetConfig(),
				widgetInstanceExternalReferenceCode,
				RandomTestUtil.randomString(),
				JournalContentPortletKeys.JOURNAL_CONTENT,
				_getWidgetPermissions()));

		_assertStyledLayoutStructureItemBackgroundImage(
			backgroundImageValue, journalArticle.getResourcePrimKey(),
			journalArticle, externalReferenceCode);

		backgroundImageValue = ImageValueTestUtil.getMappedBackgroundImageValue(
			FragmentMappedValueItemContextReference.ContextSource.
				DISPLAY_PAGE_ITEM,
			"JournalArticle_authorProfileImage",
			FragmentMappedValueItemReference.Type.CONTEXT_REFERENCE);

		String draftWidgetInstanceExternalReferenceCode =
			RandomTestUtil.randomString();
		String namespace = RandomTestUtil.randomString();

		_addFragmentEntryLink(
			draftWidgetInstanceExternalReferenceCode, namespace);

		_testPutSitePageSpecificationPageExperiencePageElement(
			_getWidgetPageElement(
				backgroundImageValue,
				RandomTestUtil.randomStrings(RandomTestUtil.randomInt(1, 10)),
				draftWidgetInstanceExternalReferenceCode, false,
				RandomTestUtil.randomString(), externalReferenceCode,
				_getWidgetConfig(), widgetInstanceExternalReferenceCode,
				namespace, AssetPublisherPortletKeys.ASSET_PUBLISHER,
				_getWidgetPermissions()));

		_assertStyledLayoutStructureItemBackgroundImage(
			backgroundImageValue, 0, null, externalReferenceCode);

		backgroundImageValue = ImageValueTestUtil.getMappedBackgroundImageValue(
			FragmentMappedValueItemContextReference.ContextSource.
				COLLECTION_ITEM,
			"AssetEntry_userProfileImage",
			FragmentMappedValueItemReference.Type.CONTEXT_REFERENCE);

		_testPutSitePageSpecificationPageExperiencePageElement(
			_getWidgetPageElement(
				backgroundImageValue, null, null, false,
				RandomTestUtil.randomString(), externalReferenceCode,
				new HashMap<>(), widgetInstanceExternalReferenceCode, namespace,
				AssetPublisherPortletKeys.ASSET_PUBLISHER,
				new WidgetPermission[0]));

		_assertStyledLayoutStructureItemBackgroundImage(
			backgroundImageValue, 0, null, externalReferenceCode);

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				testGroup.getGroupId(), SegmentsExperienceConstants.KEY_DEFAULT,
				_layout.getPlid());

		String undeployedPortletName = RandomTestUtil.randomString();

		PageElement pageElement =
			pageElementResource.
				putSitePageSpecificationPageExperiencePageElement(
					testGroup.getExternalReferenceCode(),
					_draftLayout.getExternalReferenceCode(),
					segmentsExperience.getExternalReferenceCode(),
					externalReferenceCode,
					_getWidgetPageElement(
						null,
						RandomTestUtil.randomStrings(
							RandomTestUtil.randomInt(1, 10)),
						draftWidgetInstanceExternalReferenceCode, false,
						RandomTestUtil.randomString(), externalReferenceCode,
						_getWidgetConfig(), widgetInstanceExternalReferenceCode,
						namespace, undeployedPortletName,
						_getWidgetPermissions()));

		assertValid(pageElement);

		WidgetInstancePageElementDefinition
			widgetInstancePageElementDefinition =
				(WidgetInstancePageElementDefinition)
					pageElement.getPageElementDefinition();

		WidgetInstance widgetInstance =
			widgetInstancePageElementDefinition.getWidgetInstance();

		Assert.assertEquals(
			undeployedPortletName, widgetInstance.getWidgetName());
	}

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetListEntryLocalService _assetListEntryLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLURLHelper _dlURLHelper;

	private Layout _draftLayout;

	@Inject
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Inject
	private FragmentEntryProcessorRegistry _fragmentEntryProcessorRegistry;

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

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	private int _position;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Inject
	private SiteNavigationMenuItemLocalService
		_siteNavigationMenuItemLocalService;

	@Inject
	private SiteNavigationMenuLocalService _siteNavigationMenuLocalService;

}