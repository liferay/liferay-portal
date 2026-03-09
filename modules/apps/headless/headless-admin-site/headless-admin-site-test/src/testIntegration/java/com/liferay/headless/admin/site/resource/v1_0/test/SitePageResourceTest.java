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
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.test.util.DLTestUtil;
import com.liferay.exportimport.test.rule.LazyReferencingTestRule;
import com.liferay.exportimport.test.util.LazyReferencingTestUtil;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.helper.DefaultInputFragmentEntryConfigurationProvider;
import com.liferay.fragment.input.template.parser.FragmentEntryInputTemplateNodeContextHelper;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.headless.admin.site.client.custom.field.CustomField;
import com.liferay.headless.admin.site.client.dto.v1_0.BasicFragmentInstancePageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.ContentPageSettings;
import com.liferay.headless.admin.site.client.dto.v1_0.ContentPageSpecification;
import com.liferay.headless.admin.site.client.dto.v1_0.CustomMetaTag;
import com.liferay.headless.admin.site.client.dto.v1_0.EmbeddedPageSettings;
import com.liferay.headless.admin.site.client.dto.v1_0.FavIcon;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentEditableElement;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentImage;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentImageValue;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentInstance;
import com.liferay.headless.admin.site.client.dto.v1_0.FriendlyUrlHistory;
import com.liferay.headless.admin.site.client.dto.v1_0.ImageFragmentEditableElementValue;
import com.liferay.headless.admin.site.client.dto.v1_0.ItemExternalReference;
import com.liferay.headless.admin.site.client.dto.v1_0.LinkToPagePageSettings;
import com.liferay.headless.admin.site.client.dto.v1_0.LinkToURLPageSettings;
import com.liferay.headless.admin.site.client.dto.v1_0.OpenGraphSettings;
import com.liferay.headless.admin.site.client.dto.v1_0.PageElement;
import com.liferay.headless.admin.site.client.dto.v1_0.PageExperience;
import com.liferay.headless.admin.site.client.dto.v1_0.PageSetPageSettings;
import com.liferay.headless.admin.site.client.dto.v1_0.PageSettings;
import com.liferay.headless.admin.site.client.dto.v1_0.PageSpecification;
import com.liferay.headless.admin.site.client.dto.v1_0.ParentTaxonomyCategory;
import com.liferay.headless.admin.site.client.dto.v1_0.ParentTaxonomyVocabulary;
import com.liferay.headless.admin.site.client.dto.v1_0.SEOSettings;
import com.liferay.headless.admin.site.client.dto.v1_0.SitePage;
import com.liferay.headless.admin.site.client.dto.v1_0.SitePageNavigationSettings;
import com.liferay.headless.admin.site.client.dto.v1_0.SitemapSettings;
import com.liferay.headless.admin.site.client.dto.v1_0.TaxonomyCategoryBrief;
import com.liferay.headless.admin.site.client.dto.v1_0.WidgetPageSettings;
import com.liferay.headless.admin.site.client.dto.v1_0.WidgetPageSpecification;
import com.liferay.headless.admin.site.client.pagination.Page;
import com.liferay.headless.admin.site.client.pagination.Pagination;
import com.liferay.headless.admin.site.client.problem.Problem;
import com.liferay.headless.admin.site.client.resource.v1_0.SitePageResource;
import com.liferay.headless.admin.site.client.scope.Scope;
import com.liferay.headless.admin.site.client.serdes.v1_0.FragmentImageValueSerDes;
import com.liferay.headless.admin.site.resource.v1_0.test.util.AssetTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.ImageValueTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.LayoutPageTemplateEntryTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.LayoutUtilityPageEntryTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.PageElementsTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.PageExperiencesTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.PageSpecificationsTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.SettingsTestUtil;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.type.InfoFieldType;
import com.liferay.info.form.InfoForm;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFormProvider;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.provider.LayoutStructureProvider;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutFriendlyURLRandomizerBumper;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.list.type.entry.util.ListTypeEntryUtil;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.field.builder.AttachmentObjectFieldBuilder;
import com.liferay.object.field.builder.DateTimeObjectFieldBuilder;
import com.liferay.object.field.builder.MultiselectPicklistObjectFieldBuilder;
import com.liferay.object.field.builder.PicklistObjectFieldBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.function.UnsafeBiConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CustomizedPages;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTypePortletConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.segments.constants.SegmentsExperienceConstants;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Rubén Pulido
 */
@FeatureFlag("LPD-35443")
@RunWith(Arquillian.class)
public class SitePageResourceTest extends BaseSitePageResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			LazyReferencingTestRule.INSTANCE, new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Ignore
	@Override
	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		super.testBatchEngineDeleteImportTask();
	}

	@Override
	@Test
	public void testDeleteSiteSitePage() throws Exception {
		SitePage postSitePage = testGetSiteSitePagesPage_addSitePage(
			testGroup.getExternalReferenceCode(), randomSitePage());

		sitePageResource.deleteSiteSitePage(
			testGroup.getExternalReferenceCode(),
			postSitePage.getExternalReferenceCode());

		Assert.assertNull(
			_layoutLocalService.fetchLayoutByExternalReferenceCode(
				postSitePage.getExternalReferenceCode(),
				testGroup.getGroupId()));

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId());

		_testDeleteSiteSitePage(
			_addLayout(LayoutConstants.TYPE_CONTENT, null, serviceContext),
			_addLayout(
				LayoutConstants.TYPE_PORTLET,
				UnicodePropertiesBuilder.put(
					LayoutTypePortletConstants.LAYOUT_TEMPLATE_ID, "1_column"
				).buildString(),
				serviceContext));

		Layout layout = _addLayout(
			LayoutConstants.TYPE_CONTENT, null, serviceContext);

		_assertDeleteSiteSitePageProblemException(
			layout.fetchDraftLayout(),
			LayoutPageTemplateEntryTestUtil.
				getBasicLayoutPageTemplateEntryLayout(serviceContext),
			LayoutPageTemplateEntryTestUtil.
				getDisplayPageLayoutPageTemplateEntryLayout(serviceContext),
			LayoutPageTemplateEntryTestUtil.
				getMasterLayoutPageTemplateEntryLayout(serviceContext),
			LayoutUtilityPageEntryTestUtil.getLayoutUtilityPageEntryLayout(
				serviceContext));
	}

	@Override
	@Test
	@TestInfo("LPD-78718")
	public void testGetSiteSitePage() throws Exception {
		SitePage postSitePage = testGetSiteSitePagesPage_addSitePage(
			testGroup.getExternalReferenceCode(), randomSitePage());

		SitePage getSitePage = sitePageResource.getSiteSitePage(
			testGroup.getExternalReferenceCode(),
			postSitePage.getExternalReferenceCode());

		assertEquals(postSitePage, getSitePage);
		assertValid(getSitePage);

		_testGetSiteSitePageWithNestedFields(
			testGetSiteSitePagesPage_addSitePage(
				testGroup.getExternalReferenceCode(), randomSitePage()));

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId());

		Layout layout = _addLayout(
			LayoutConstants.TYPE_CONTENT, null, serviceContext);

		Assert.assertFalse(layout.isPublished());

		_testGetSiteSitePage(layout);

		ContentLayoutTestUtil.publishLayout(layout.fetchDraftLayout(), layout);

		Assert.assertTrue(layout.isPublished());

		_testGetSiteSitePage(layout);

		_testGetSiteSitePage(
			_addLayout(
				LayoutConstants.TYPE_PORTLET,
				UnicodePropertiesBuilder.put(
					LayoutTypePortletConstants.LAYOUT_TEMPLATE_ID, "1_column"
				).buildString(),
				serviceContext));

		_testGetSiteSitePage(
			LayoutTestUtil.addTypePortletLayout(
				testGroup.getGroupId(),
				UnicodePropertiesBuilder.put(
					LayoutTypePortletConstants.COLUMN_PREFIX + "1",
					RandomTestUtil.randomString()
				).buildString()));
	}

	@Override
	@Test
	public void testGetSiteSitePagesPage() throws Exception {
		super.testGetSiteSitePagesPage();

		_testGetSitePageSitePagesPage(false);
		_testGetSitePageSitePagesPage(true);
	}

	@Override
	@Test
	@TestInfo({"LPD-74225", "LPD-75413"})
	public void testPatchSiteSitePage() throws Exception {
		_testPatchSiteSitePage(SitePage.Type.CONTENT_PAGE);
		_testPatchSiteSitePage(SitePage.Type.WIDGET_PAGE);
		_testPatchSiteSitePageWithPageSpecifications();
		_testPatchSiteSitePageWithPriority();
		_testPatchSiteSitePageWithWidgetPageSettings();
		_testPatchSiteSitePageWithWidgetPageSettingsWithWidgetPageTemplate();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId());

		Layout layout = _addLayout(
			LayoutConstants.TYPE_CONTENT, null, serviceContext);

		_assertPatchSiteSitePageProblemException(
			serviceContext, layout.fetchDraftLayout(),
			LayoutPageTemplateEntryTestUtil.
				getBasicLayoutPageTemplateEntryLayout(serviceContext),
			LayoutPageTemplateEntryTestUtil.
				getDisplayPageLayoutPageTemplateEntryLayout(serviceContext),
			LayoutPageTemplateEntryTestUtil.
				getMasterLayoutPageTemplateEntryLayout(serviceContext),
			LayoutUtilityPageEntryTestUtil.getLayoutUtilityPageEntryLayout(
				serviceContext));
	}

	@FeatureFlag(enable = true, value = "LPD-38869")
	@Override
	@Test
	public void testPostSiteSitePage() throws Exception {
		super.testPostSiteSitePage();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup, TestPropsValues.getUserId());

		_testPostSiteParentSitePage(false, serviceContext);
		_testPostSiteParentSitePage(true, serviceContext);

		_testPostSiteSitePage(
			_getRandomSitePage(serviceContext, SitePage.Type.CONTENT_PAGE));
		_testPostSiteSitePage(
			_getRandomSitePage(serviceContext, SitePage.Type.WIDGET_PAGE));

		Layout layout = LayoutTestUtil.addTypePortletLayout(testGroup);

		_testPostSiteSitePage(
			_getRandomSitePage(
				StringUtil.toLowerCase(RandomTestUtil.randomString()),
				layout.getExternalReferenceCode(), serviceContext,
				SitePage.Type.CONTENT_PAGE,
				StringUtil.toLowerCase(RandomTestUtil.randomString())));

		_testPostSiteSitePageWithPageElements();
		_testPostSiteSitePageWithPageSpecifications();
		_testPostSiteSitePageWithWidgetPageSettings();
		_testPostSiteSitePageWithWidgetPageSettingsWithWidgetPageTemplate();
	}

	@Override
	@Test
	public void testPostSiteSitePagePageSpecification() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(testGroup);

		SitePageResource sitePageResource = _getSitePageResource(
			"pageSpecifications");

		SitePage sitePage = sitePageResource.getSiteSitePage(
			testGroup.getExternalReferenceCode(),
			layout.getExternalReferenceCode());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId());

		PageSpecificationsTestUtil.testPostSitePageSpecification(
			layout, sitePage.getPageSpecifications(), serviceContext,
			contentPageSpecification ->
				sitePageResource.postSiteSitePagePageSpecification(
					testGroup.getExternalReferenceCode(),
					layout.getExternalReferenceCode(),
					contentPageSpecification));

		_assertPostSiteSitePagePageSpecificationProblemException(
			LayoutTestUtil.addTypePortletLayout(testGroup));
		_assertPostSiteSitePagePageSpecificationProblemException(
			LayoutPageTemplateEntryTestUtil.
				getBasicLayoutPageTemplateEntryLayout(serviceContext));
		_assertPostSiteSitePagePageSpecificationProblemException(
			LayoutPageTemplateEntryTestUtil.
				getDisplayPageLayoutPageTemplateEntryLayout(serviceContext));
		_assertPostSiteSitePagePageSpecificationProblemException(
			LayoutPageTemplateEntryTestUtil.
				getMasterLayoutPageTemplateEntryLayout(serviceContext));
		_assertPostSiteSitePagePageSpecificationProblemException(
			LayoutUtilityPageEntryTestUtil.getLayoutUtilityPageEntryLayout(
				serviceContext));
	}

	@FeatureFlag(enable = true, value = "LPD-38869")
	@Override
	@Test
	@TestInfo(
		{
			"LPD-72013", "LPD-74331", "LPD-75450", "LPD-77124", "LPD-77505",
			"LPD-77576", "LPD-77852", "LPD-78667", "LPD-79415"
		}
	)
	public void testPutSiteSitePage() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId());

		_testPutSiteSitePage(serviceContext, SitePage.Type.CONTENT_PAGE);
		_testPutSiteSitePage(serviceContext, SitePage.Type.EMBEDDED_PAGE);
		_testPutSiteSitePage(serviceContext, SitePage.Type.LINK_TO_PAGE_PAGE);
		_testPutSiteSitePage(serviceContext, SitePage.Type.LINK_TO_URL_PAGE);
		_testPutSiteSitePage(serviceContext, SitePage.Type.PAGE_SET_PAGE);
		_testPutSiteSitePage(serviceContext, SitePage.Type.WIDGET_PAGE);

		_testPutSiteSitePage(true, serviceContext, SitePage.Type.CONTENT_PAGE);
		_testPutSiteSitePage(true, serviceContext, SitePage.Type.WIDGET_PAGE);

		_testPutSiteSitePageWithEmptyLayout(serviceContext);
		_testPutSiteSitePageWithExportedPageSetSitePageAsFirstPage(
			serviceContext);

		_testPutSiteSitePageWithExportedSitePage();
		_testPutSiteSitePageWithExportedSitePageWithLayoutIdFriendlyURL();
		_testPutSiteSitePageWithFormFragmentPageElements();
		LazyReferencingTestUtil.executeWithLazyReferencingSafeCloseable(
			this::_testPutSiteSitePageWithMissingTaxonomyCategories);
		_assertProblemException(
			"NOT_FOUND", null,
			this::_testPutSiteSitePageWithMissingTaxonomyCategories);
		_testPutSiteSitePageWithPageElements();
		_testPutSiteSitePageWithPageExperiences();
		_testPutSiteSitePageWithPageSpecifications();
		_testPutSiteSitePageWithPriority();
		_testPutSiteSitePageWithWidgetPageSettings();
		_testPutSiteSitePageWithWidgetPageSettingsWithWidgetPageTemplate();

		Layout layout = _addLayout(
			LayoutConstants.TYPE_CONTENT, null, serviceContext);

		_assertPutSiteSitePageProblemException(
			serviceContext, layout.fetchDraftLayout(),
			LayoutPageTemplateEntryTestUtil.
				getBasicLayoutPageTemplateEntryLayout(serviceContext),
			LayoutPageTemplateEntryTestUtil.
				getDisplayPageLayoutPageTemplateEntryLayout(serviceContext),
			LayoutPageTemplateEntryTestUtil.
				getMasterLayoutPageTemplateEntryLayout(serviceContext),
			LayoutUtilityPageEntryTestUtil.getLayoutUtilityPageEntryLayout(
				serviceContext));
	}

	@Override
	protected boolean equals(SitePage sitePage1, SitePage sitePage2) {
		if (!super.equals(sitePage1, sitePage2)) {
			return false;
		}

		PageSettings pageSettings1 = sitePage1.getPageSettings();
		PageSettings pageSettings2 = sitePage2.getPageSettings();

		try {
			PageSettings clonedPageSettings1 = pageSettings1.clone();
			PageSettings clonedPageSettings2 = pageSettings2.clone();

			clonedPageSettings1.setPriority((Integer)null);
			clonedPageSettings2.setPriority((Integer)null);

			return Objects.deepEquals(clonedPageSettings1, clonedPageSettings2);
		}
		catch (CloneNotSupportedException cloneNotSupportedException) {
			throw new RuntimeException(cloneNotSupportedException);
		}
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"externalReferenceCode", "friendlyUrlPath_i18n", "keywords",
			"name_i18n", "taxonomyCategoryBriefs", "type", "uuid"
		};
	}

	@Override
	protected SitePage randomIrrelevantSitePage() throws Exception {
		return _getRandomSitePage(
			ServiceContextTestUtil.getServiceContext(
				irrelevantGroup, TestPropsValues.getUserId()),
			_getRandomType(_types));
	}

	@Override
	protected SitePage randomSitePage() throws Exception {
		return _getRandomSitePage(_getRandomType(_types));
	}

	@Override
	protected SitePage testDeleteSiteSitePage_addSitePage() throws Exception {
		return sitePageResource.postSiteSitePage(
			testGroup.getExternalReferenceCode(), false, randomSitePage());
	}

	@Override
	protected SitePage testGetSiteSitePage_addSitePage() throws Exception {
		return sitePageResource.postSiteSitePage(
			testGroup.getExternalReferenceCode(), false, randomSitePage());
	}

	@Override
	protected SitePage testGetSiteSitePagePermissionsPage_addSitePage()
		throws Exception {

		return sitePageResource.postSiteSitePage(
			testGroup.getExternalReferenceCode(), false, randomSitePage());
	}

	@Override
	protected SitePage testGetSiteSitePagesPage_addSitePage(
			String siteExternalReferenceCode, SitePage sitePage)
		throws Exception {

		return sitePageResource.postSiteSitePage(
			siteExternalReferenceCode, false, sitePage);
	}

	@Override
	protected Map<String, Map<String, String>>
		testGetSiteSitePagesPage_getExpectedActions(
			String siteExternalReferenceCode) {

		return new HashMap<>();
	}

	@Override
	protected String
		testGetSiteSitePagesPage_getIrrelevantSiteExternalReferenceCode() {

		return irrelevantGroup.getExternalReferenceCode();
	}

	@Override
	protected String testGetSiteSitePagesPage_getSiteExternalReferenceCode() {
		return testGroup.getExternalReferenceCode();
	}

	@Override
	protected SitePage testPatchSiteSitePage_addSitePage() throws Exception {
		return sitePageResource.postSiteSitePage(
			testGroup.getExternalReferenceCode(), false, randomSitePage());
	}

	@Override
	protected SitePage testPostSiteSitePage_addPermissionsSitePage(
			SitePage sitePage)
		throws Exception {

		return permissionsSitePageResource.postSiteSitePage(
			testGetSiteSitePagesPage_getSiteExternalReferenceCode(), false,
			sitePage);
	}

	@Override
	protected SitePage testPostSiteSitePage_addSitePage(SitePage sitePage)
		throws Exception {

		return sitePageResource.postSiteSitePage(
			testGroup.getExternalReferenceCode(), false, sitePage);
	}

	@Override
	protected SitePage testPutSiteSitePage_addSitePage() throws Exception {
		return sitePageResource.postSiteSitePage(
			testGroup.getExternalReferenceCode(), false, randomSitePage());
	}

	@Override
	protected SitePage testPutSiteSitePagePermissionsPage_addSitePage()
		throws Exception {

		return sitePageResource.postSiteSitePage(
			testGroup.getExternalReferenceCode(), false, randomSitePage());
	}

	private void _addFormAndPublishLayout(
			String className, List<InfoField> infoFields, Layout layout)
		throws Exception {

		JSONObject defaultInputFragmentEntryKeysJSONObject =
			_defaultInputFragmentEntryConfigurationProvider.
				getDefaultInputFragmentEntryKeysJSONObject(layout.getGroupId());

		Layout draftLayout = layout.fetchDraftLayout();

		long segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid());

		JSONObject jsonObject = ContentLayoutTestUtil.addFormToLayout(
			false, String.valueOf(_portal.getClassNameId(className)), "0",
			draftLayout, _layoutStructureProvider, segmentsExperienceId);

		String parentItemId = jsonObject.getString("addedItemId");

		int position = 0;

		for (InfoField<?> infoField : infoFields) {
			InfoFieldType infoFieldType = infoField.getInfoFieldType();

			JSONObject defaultInputFragmentEntryJSONObject =
				defaultInputFragmentEntryKeysJSONObject.getJSONObject(
					infoFieldType.getName());

			FragmentEntry fragmentEntry =
				_fragmentCollectionContributorRegistry.getFragmentEntry(
					defaultInputFragmentEntryJSONObject.getString("key"));

			ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
				_getInputFragmentEntryLinkEditableValues(
					infoField.getUniqueId()),
				fragmentEntry.getCss(), fragmentEntry.getConfiguration(),
				fragmentEntry.getExternalReferenceCode(), null,
				fragmentEntry.getHtml(), fragmentEntry.getJs(), draftLayout,
				fragmentEntry.getFragmentEntryKey(), fragmentEntry.getType(),
				parentItemId, position, segmentsExperienceId);

			position++;
		}

		JSONObject defaultInputFragmentEntryJSONObject =
			defaultInputFragmentEntryKeysJSONObject.getJSONObject(
				DefaultInputFragmentEntryConfigurationProvider.
					FORM_INPUT_SUBMIT_BUTTON);

		FragmentEntry fragmentEntry =
			_fragmentCollectionContributorRegistry.getFragmentEntry(
				defaultInputFragmentEntryJSONObject.getString("key"));

		ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			"{}", fragmentEntry.getCss(), fragmentEntry.getConfiguration(),
			fragmentEntry.getExternalReferenceCode(), null,
			fragmentEntry.getHtml(), fragmentEntry.getJs(), draftLayout,
			fragmentEntry.getFragmentEntryKey(), fragmentEntry.getType(),
			parentItemId, position, segmentsExperienceId);

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);
	}

	private void _addFragmentEntryLinksAndPublishLayout(
			JSONObject fragmentEntryKeyJSONObject, Layout layout)
		throws Exception {

		Layout draftLayout = layout.fetchDraftLayout();

		long segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid());

		for (String fragmentEntryKey : fragmentEntryKeyJSONObject.keySet()) {
			FragmentEntry fragmentEntry =
				_fragmentCollectionContributorRegistry.getFragmentEntry(
					fragmentEntryKey);

			JSONArray jsonArray = fragmentEntryKeyJSONObject.getJSONArray(
				fragmentEntryKey);

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject editableFragmentEntryProcessorJSONObject =
					jsonArray.getJSONObject(i);

				ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
					JSONUtil.put(
						FragmentEntryProcessorConstants.
							KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
						editableFragmentEntryProcessorJSONObject
					).toString(),
					fragmentEntry.getCss(), fragmentEntry.getConfiguration(),
					fragmentEntry.getExternalReferenceCode(), null,
					fragmentEntry.getHtml(), fragmentEntry.getJs(), draftLayout,
					fragmentEntry.getFragmentEntryKey(),
					fragmentEntry.getType(), null, 0, segmentsExperienceId);
			}
		}

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);
	}

	private Layout _addLayout(
			String type, String typeSettings, ServiceContext serviceContext)
		throws Exception {

		return _layoutLocalService.addLayout(
			null, TestPropsValues.getUserId(), testGroup.getGroupId(), false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, 0, 0,
			RandomTestUtil.randomLocaleStringMap(), Collections.emptyMap(),
			Collections.emptyMap(), Collections.emptyMap(),
			Collections.emptyMap(), type, typeSettings, false, false,
			Collections.emptyMap(), null, serviceContext);
	}

	private void _assertContentPageSpecifications(
			PageSpecification[] pageSpecifications, SitePage sitePage)
		throws Exception {

		_assertPageSpecifications(
			(ContentPageSpecification)pageSpecifications[1],
			(ContentPageSpecification)pageSpecifications[0], sitePage);
	}

	private void _assertContentSitePage(SitePage sitePage) {
		Assert.assertEquals(SitePage.Type.CONTENT_PAGE, sitePage.getType());

		Assert.assertTrue(
			sitePage.getPageSettings() instanceof ContentPageSettings);
	}

	private void _assertDeleteSiteSitePageProblemException(Layout... layouts)
		throws Exception {

		for (Layout layout : layouts) {
			_assertProblemException(
				null,
				() -> sitePageResource.deleteSiteSitePage(
					testGroup.getExternalReferenceCode(),
					layout.getExternalReferenceCode()));
		}
	}

	private void _assertEmbeddedSitePage(SitePage sitePage) {
		Assert.assertTrue(
			sitePage.getPageSettings() instanceof EmbeddedPageSettings);
		Assert.assertEquals(SitePage.Type.EMBEDDED_PAGE, sitePage.getType());
	}

	private void _assertFragmentImageValue(
		BasicFragmentInstancePageElementDefinition
			basicFragmentInstancePageElementDefinition,
		FragmentImageValue fragmentImageValue) {

		FragmentInstance fragmentInstance =
			basicFragmentInstancePageElementDefinition.getFragmentInstance();

		FragmentEditableElement[] fragmentEditableElements =
			fragmentInstance.getFragmentEditableElements();

		Assert.assertEquals(
			fragmentEditableElements.toString(), 1,
			fragmentEditableElements.length);

		FragmentEditableElement fragmentEditableElement =
			fragmentEditableElements[0];

		ImageFragmentEditableElementValue imageFragmentEditableElementValue =
			(ImageFragmentEditableElementValue)
				fragmentEditableElement.getFragmentEditableElementValue();

		FragmentImage fragmentImage =
			imageFragmentEditableElementValue.getFragmentImage();

		Assert.assertEquals(
			fragmentImageValue, fragmentImage.getFragmentImageValue());
	}

	private void _assertFragmentImageValues(
		int count, FragmentImageValue fragmentImageValue, SitePage sitePage) {

		for (PageSpecification pageSpecification :
				sitePage.getPageSpecifications()) {

			ContentPageSpecification contentPageSpecification =
				(ContentPageSpecification)pageSpecification;

			PageExperience defaultPageExperience =
				PageExperiencesTestUtil.getDefaultPageExperience(
					contentPageSpecification.getPageExperiences());

			PageElement[] pageElements =
				defaultPageExperience.getPageElements();

			Assert.assertEquals(
				pageElements.toString(), count, pageElements.length);

			for (PageElement pageElement : pageElements) {
				_assertFragmentImageValue(
					(BasicFragmentInstancePageElementDefinition)
						pageElement.getPageElementDefinition(),
					fragmentImageValue);
			}
		}
	}

	private void _assertFriendlyUrlHistory(
			Map<Locale, String> friendlyURLMap,
			Map<String, String> newFriendlyURLsMap, boolean published,
			SitePage sitePage)
		throws Exception {

		FriendlyUrlHistory friendlyUrlHistory =
			sitePage.getFriendlyUrlHistory();

		JSONObject friendlyUrlHistoryJSONObject = _jsonFactory.createJSONObject(
			GetterUtil.getString(friendlyUrlHistory.getFriendlyUrlPath_i18n()));

		if (!published) {
			Assert.assertTrue(
				friendlyUrlHistoryJSONObject.toString(),
				JSONUtil.isEmpty(friendlyUrlHistoryJSONObject));

			return;
		}

		Map<String, List<String>> expectedFriendlyURLsMap = new HashMap<>();

		for (Map.Entry<Locale, String> entry : friendlyURLMap.entrySet()) {
			List<String> friendlyURLs = ListUtil.fromArray(entry.getValue());
			String languageId = LocaleUtil.toBCP47LanguageId(entry.getKey());

			if (newFriendlyURLsMap.containsKey(languageId)) {
				friendlyURLs.add(newFriendlyURLsMap.get(languageId));
			}

			expectedFriendlyURLsMap.put(languageId, friendlyURLs);
		}

		Assert.assertEquals(
			friendlyUrlHistoryJSONObject.toString(),
			expectedFriendlyURLsMap.size(),
			friendlyUrlHistoryJSONObject.length());

		for (String key : friendlyUrlHistoryJSONObject.keySet()) {
			JSONArray jsonArray = friendlyUrlHistoryJSONObject.getJSONArray(
				key);

			List<String> expectedFriendlyURLs = expectedFriendlyURLsMap.get(
				key);

			Assert.assertEquals(
				jsonArray.toString(), expectedFriendlyURLs.size(),
				jsonArray.length());
			Assert.assertTrue(
				jsonArray.toString(),
				expectedFriendlyURLs.containsAll(
					JSONUtil.toStringList(jsonArray)));
		}
	}

	private void _assertInputFragmentEntryLinks(
			List<FragmentEntryLink> expectedFragmentEntryLinks,
			InfoForm expectedInfoForm, InfoForm infoForm, Layout layout)
		throws Exception {

		Assert.assertEquals(
			expectedFragmentEntryLinks.size(),
			_fragmentEntryLinkLocalService.getFragmentEntryLinksCountByPlid(
				layout.getGroupId(), layout.getPlid()));

		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setScopeGroupId(layout.getGroupId());
		themeDisplay.setSiteGroupId(layout.getGroupId());

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		for (Locale locale :
				LanguageUtil.getAvailableLocales(layout.getGroupId())) {

			for (FragmentEntryLink fragmentEntryLink :
					expectedFragmentEntryLinks) {

				Assert.assertEquals(
					FragmentConstants.TYPE_INPUT, fragmentEntryLink.getType());

				FragmentEntryLink importedFragmentEntryLink =
					_fragmentEntryLinkLocalService.
						getFragmentEntryLinkByExternalReferenceCode(
							fragmentEntryLink.getExternalReferenceCode(),
							layout.getGroupId());

				Assert.assertEquals(
					layout.getPlid(), importedFragmentEntryLink.getPlid());
				Assert.assertEquals(
					fragmentEntryLink.getRendererKey(),
					importedFragmentEntryLink.getRendererKey());
				Assert.assertEquals(
					FragmentConstants.TYPE_INPUT,
					importedFragmentEntryLink.getType());

				String defaultInputLabel = RandomTestUtil.randomString();

				Assert.assertEquals(
					_fragmentEntryInputTemplateNodeContextHelper.
						toInputTemplateNode(
							Collections.emptyMap(), defaultInputLabel,
							fragmentEntryLink, httpServletRequest,
							expectedInfoForm, locale),
					_fragmentEntryInputTemplateNodeContextHelper.
						toInputTemplateNode(
							Collections.emptyMap(), defaultInputLabel,
							importedFragmentEntryLink, httpServletRequest,
							infoForm, locale));
			}
		}
	}

	private void _assertLinkToPageSitePage(SitePage sitePage) {
		Assert.assertTrue(
			sitePage.getPageSettings() instanceof LinkToPagePageSettings);

		Assert.assertEquals(
			SitePage.Type.LINK_TO_PAGE_PAGE, sitePage.getType());
	}

	private void _assertLinkToURLSitePage(SitePage sitePage) {
		Assert.assertTrue(
			sitePage.getPageSettings() instanceof LinkToURLPageSettings);
		Assert.assertEquals(SitePage.Type.LINK_TO_URL_PAGE, sitePage.getType());
	}

	private void _assertMapEquals(
		Map<String, String> expectedMap, Map<String, String> map) {

		Assert.assertEquals(
			MapUtil.toString(map), expectedMap.size(), map.size());

		for (Map.Entry<String, String> entry : expectedMap.entrySet()) {
			Assert.assertEquals(entry.getValue(), map.get(entry.getKey()));
		}
	}

	private void _assertNestedFields(SitePage sitePage) throws Exception {
		Layout layout = _layoutLocalService.getLayoutByExternalReferenceCode(
			sitePage.getExternalReferenceCode(), testGroup.getGroupId());

		_assertFriendlyUrlHistory(
			layout.getFriendlyURLMap(), Collections.emptyMap(),
			layout.isPublished(), sitePage);

		PageSpecificationsTestUtil.assertPageSpecifications(
			layout, sitePage.getPageSpecifications());
	}

	private void _assertPageElements(
		PageElement[] expectedPageElements, SitePage sitePage) {

		for (PageSpecification pageSpecification :
				sitePage.getPageSpecifications()) {

			ContentPageSpecification contentPageSpecification =
				(ContentPageSpecification)pageSpecification;

			for (PageExperience pageExperience :
					contentPageSpecification.getPageExperiences()) {

				Assert.assertArrayEquals(
					expectedPageElements, pageExperience.getPageElements());
			}
		}
	}

	private void _assertPageSetSitePage(SitePage sitePage) {
		Assert.assertTrue(
			sitePage.getPageSettings() instanceof PageSetPageSettings);

		Assert.assertEquals(SitePage.Type.PAGE_SET_PAGE, sitePage.getType());
	}

	private void _assertPageSpecifications(
			ContentPageSpecification draftContentPageSpecification,
			ContentPageSpecification publishedContentPageSpecification,
			SitePage sitePage)
		throws Exception {

		Layout layout = _layoutLocalService.getLayoutByExternalReferenceCode(
			sitePage.getExternalReferenceCode(), testGroup.getGroupId());

		PageSpecification.Status status = PageSpecification.Status.APPROVED;

		if (!layout.isPublished()) {
			status = PageSpecification.Status.DRAFT;
		}

		PageSpecificationsTestUtil.assertPageSpecifications(
			draftContentPageSpecification, publishedContentPageSpecification,
			sitePage.getPageSpecifications(), layout, status);
	}

	private void _assertParentAndPriority(
			String expectedParentSitePageExternalReferenceCode,
			int expectedPriority, SitePage sitePage)
		throws Exception {

		SitePage getSitePage = sitePageResource.getSiteSitePage(
			testGroup.getExternalReferenceCode(),
			sitePage.getExternalReferenceCode());

		Assert.assertEquals(
			expectedParentSitePageExternalReferenceCode,
			getSitePage.getParentSitePageExternalReferenceCode());

		PageSettings pageSettings = getSitePage.getPageSettings();

		Assert.assertEquals(expectedPriority, (int)pageSettings.getPriority());
	}

	private void _assertPatchSiteSitePageProblemException(
			ServiceContext serviceContext, Layout... layouts)
		throws Exception {

		for (Layout layout : layouts) {
			_assertPatchSiteSitePageProblemException(
				_getRandomSitePage(
					layout.getExternalReferenceCode(), null, serviceContext,
					SitePage.Type.CONTENT_PAGE, layout.getUuid()));
		}
	}

	private void _assertPatchSiteSitePageProblemException(SitePage sitePage)
		throws Exception {

		_assertProblemException(
			null,
			() -> sitePageResource.patchSiteSitePage(
				testGroup.getExternalReferenceCode(),
				sitePage.getExternalReferenceCode(), false, sitePage));
	}

	private void _assertPostSiteSitePagePageSpecificationProblemException(
			Layout layout)
		throws Exception {

		_assertProblemException(
			null,
			() -> sitePageResource.postSiteSitePagePageSpecification(
				testGroup.getExternalReferenceCode(),
				layout.getExternalReferenceCode(),
				new ContentPageSpecification() {
					{
						setExternalReferenceCode(
							layout::getExternalReferenceCode);
						setStatus(() -> Status.DRAFT);
						setType(() -> Type.CONTENT_PAGE_SPECIFICATION);
					}
				}));
	}

	private void _assertProblemException(
			String expectedStatus, String expectedTitle,
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		try {
			unsafeRunnable.run();
			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals(expectedStatus, problem.getStatus());
			Assert.assertEquals(expectedTitle, problem.getTitle());
		}
	}

	private void _assertProblemException(
			String expectedTitle, UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		_assertProblemException("BAD_REQUEST", expectedTitle, unsafeRunnable);
	}

	private void _assertPutSiteSitePageProblemException(
			ServiceContext serviceContext, Layout... layouts)
		throws Exception {

		for (Layout layout : layouts) {
			_assertPutSiteSitePageProblemException(
				_getRandomSitePage(
					layout.getExternalReferenceCode(), null, serviceContext,
					SitePage.Type.CONTENT_PAGE, layout.getUuid()));
		}
	}

	private void _assertPutSiteSitePageProblemException(SitePage sitePage)
		throws Exception {

		_assertProblemException(
			null,
			() -> sitePageResource.putSiteSitePage(
				testGroup.getExternalReferenceCode(),
				sitePage.getExternalReferenceCode(), false, sitePage));
	}

	private void _assertSitePage(Layout layout, SitePage sitePage)
		throws Exception {

		Assert.assertArrayEquals(
			LocaleUtil.toW3cLanguageIds(layout.getAvailableLanguageIds()),
			sitePage.getAvailableLanguages());
		Assert.assertEquals(
			layout.getExternalReferenceCode(),
			sitePage.getExternalReferenceCode());

		_assertMapEquals(
			LocalizedMapUtil.getI18nMap(true, layout.getFriendlyURLMap()),
			sitePage.getFriendlyUrlPath_i18n());
		_assertMapEquals(
			LocalizedMapUtil.getI18nMap(true, layout.getNameMap()),
			sitePage.getName_i18n());

		if (layout.getParentLayoutId() == 0) {
			Assert.assertTrue(
				Validator.isNull(
					sitePage.getParentSitePageExternalReferenceCode()));
		}
		else {
			Layout parentLayout = _layoutLocalService.getLayout(
				layout.getGroupId(), layout.isPrivateLayout(),
				layout.getParentLayoutId());

			Assert.assertEquals(
				parentLayout.getExternalReferenceCode(),
				sitePage.getParentSitePageExternalReferenceCode());
		}

		PageSettings pageSettings = sitePage.getPageSettings();

		Assert.assertEquals(
			layout.getPriority(), (int)pageSettings.getPriority());

		Assert.assertEquals(layout.getUuid(), sitePage.getUuid());

		if (Objects.equals(layout.getType(), LayoutConstants.TYPE_CONTENT)) {
			_assertContentSitePage(sitePage);
		}
		else if (Objects.equals(
					layout.getType(), LayoutConstants.TYPE_EMBEDDED)) {

			_assertEmbeddedSitePage(sitePage);
		}
		else if (Objects.equals(
					layout.getType(), LayoutConstants.TYPE_LINK_TO_LAYOUT)) {

			_assertLinkToPageSitePage(sitePage);
		}
		else if (Objects.equals(layout.getType(), LayoutConstants.TYPE_URL)) {
			_assertLinkToURLSitePage(sitePage);
		}
		else if (Objects.equals(layout.getType(), LayoutConstants.TYPE_NODE)) {
			_assertPageSetSitePage(sitePage);
		}
		else {
			_assertWidgetSitePage(layout, sitePage);
		}
	}

	private void _assertTaxonomyCategoryBrief(
		long groupId, TaxonomyCategoryBrief taxonomyCategoryBrief) {

		AssetCategory assetCategory =
			_assetCategoryLocalService.
				fetchAssetCategoryByExternalReferenceCode(
					taxonomyCategoryBrief.
						getTaxonomyCategoryExternalReferenceCode(),
					groupId);

		Assert.assertNotNull(assetCategory);

		ParentTaxonomyCategory parentTaxonomyCategory =
			taxonomyCategoryBrief.getParentTaxonomyCategory();

		if (parentTaxonomyCategory == null) {
			Assert.assertEquals(0L, assetCategory.getParentCategoryId());
		}
		else {
			AssetCategory parentAssetCategory =
				_assetCategoryLocalService.
					fetchAssetCategoryByExternalReferenceCode(
						parentTaxonomyCategory.getExternalReferenceCode(),
						groupId);

			Assert.assertNotNull(parentAssetCategory);
			Assert.assertEquals(
				assetCategory.getParentCategory(), parentAssetCategory);
		}

		ParentTaxonomyVocabulary parentTaxonomyVocabulary =
			taxonomyCategoryBrief.getParentTaxonomyVocabulary();

		Assert.assertNotNull(
			_assetVocabularyLocalService.
				fetchAssetVocabularyByExternalReferenceCode(
					parentTaxonomyVocabulary.getExternalReferenceCode(),
					groupId));
	}

	private void _assertWidgetSitePage(Layout layout, SitePage sitePage) {
		Assert.assertEquals(SitePage.Type.WIDGET_PAGE, sitePage.getType());

		WidgetPageSettings widgetPageSettings =
			(WidgetPageSettings)sitePage.getPageSettings();

		Assert.assertEquals(
			GetterUtil.getBoolean(
				layout.getTypeSettingsProperty(
					LayoutConstants.CUSTOMIZABLE_LAYOUT)),
			GetterUtil.getBoolean(widgetPageSettings.getCustomizable()));

		for (String customizableSectionId :
				widgetPageSettings.getCustomizableSectionIds()) {

			Assert.assertEquals(
				"true",
				layout.getTypeSettingsProperty(
					CustomizedPages.namespaceColumnId(customizableSectionId)));
		}

		Assert.assertEquals(
			layout.getTypeSettingsProperty(
				LayoutTypePortletConstants.LAYOUT_TEMPLATE_ID),
			widgetPageSettings.getLayoutTemplateId());
	}

	private ObjectFieldSetting _createObjectFieldSetting(
		String name, String value) {

		ObjectFieldSetting objectFieldSetting =
			_objectFieldSettingLocalService.createObjectFieldSetting(0L);

		objectFieldSetting.setName(name);
		objectFieldSetting.setValue(value);

		return objectFieldSetting;
	}

	private CustomMetaTag[] _getCustomMetaTags() {
		return new CustomMetaTag[] {
			new CustomMetaTag() {
				{
					setKey(RandomTestUtil::randomString);
					setValue_i18n(
						() -> HashMapBuilder.put(
							"en-US", RandomTestUtil.randomString()
						).put(
							"es-ES", RandomTestUtil.randomString()
						).build());
				}
			},
			new CustomMetaTag() {
				{
					setKey(RandomTestUtil::randomString);
					setValue_i18n(
						() -> HashMapBuilder.put(
							"en-US", RandomTestUtil.randomString()
						).put(
							"es-ES", RandomTestUtil.randomString()
						).build());
				}
			}
		};
	}

	private FragmentImageValue _getDirectFragmentImageValue(String url) {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		try {
			return FragmentImageValueSerDes.toDTO(
				objectMapper.writeValueAsString(
					ImageValueTestUtil.getDirectFragmentImageValue(null, url)));
		}
		catch (JsonProcessingException jsonProcessingException) {
			throw new RuntimeException(jsonProcessingException);
		}
	}

	private int _getExpectedPriority(
			String defaultParentSitePageExternalReferenceCode,
			String parentSitePageExternalReferenceCode, Integer priority)
		throws Exception {

		long parentLayoutId = LayoutConstants.DEFAULT_PARENT_LAYOUT_ID;

		Layout parentLayout = null;

		if ((parentSitePageExternalReferenceCode == null) &&
			Validator.isNotNull(defaultParentSitePageExternalReferenceCode)) {

			parentLayout = _layoutLocalService.getLayoutByExternalReferenceCode(
				defaultParentSitePageExternalReferenceCode,
				testGroup.getGroupId());
		}
		else if (Validator.isNotNull(parentSitePageExternalReferenceCode)) {
			parentLayout = _layoutLocalService.getLayoutByExternalReferenceCode(
				parentSitePageExternalReferenceCode, testGroup.getGroupId());
		}

		if (parentLayout != null) {
			parentLayoutId = parentLayout.getLayoutId();
		}

		int maxPriority = _layoutLocalService.getLayoutsCount(
			testGroup.getGroupId(), false, parentLayoutId);

		if (maxPriority == 0) {
			return 0;
		}

		if ((parentSitePageExternalReferenceCode == null) ||
			Objects.equals(
				defaultParentSitePageExternalReferenceCode,
				parentSitePageExternalReferenceCode)) {

			maxPriority = maxPriority - 1;
		}

		if (priority == null) {
			return maxPriority;
		}

		return Math.min(priority, maxPriority);
	}

	private String _getInputFragmentEntryLinkEditableValues(
		String inputFieldId) {

		return JSONUtil.put(
			FragmentEntryProcessorConstants.
				KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
			JSONUtil.put(
				"inputFieldId", inputFieldId
			).put(
				"inputHelpText", RandomTestUtil.randomLocaleStringMap()
			).put(
				"inputLabel", RandomTestUtil.randomLocaleStringMap()
			).put(
				"inputReadOnly", RandomTestUtil.randomBoolean()
			).put(
				"inputRequired", RandomTestUtil.randomBoolean()
			).put(
				"inputShowHelpText", RandomTestUtil.randomBoolean()
			).put(
				"inputShowLabel", RandomTestUtil.randomBoolean()
			)
		).toString();
	}

	private List<ObjectField> _getObjectFields() throws Exception {
		ListTypeDefinition listTypeDefinition =
			_listTypeDefinitionLocalService.addListTypeDefinition(
				null, TestPropsValues.getUserId(),
				Collections.singletonMap(
					LocaleUtil.US, RandomTestUtil.randomString()),
				false,
				ListUtil.fromArray(
					ListTypeEntryUtil.createListTypeEntry(
						RandomTestUtil.randomString(),
						Collections.singletonMap(
							LocaleUtil.US, RandomTestUtil.randomString())),
					ListTypeEntryUtil.createListTypeEntry(
						RandomTestUtil.randomString(),
						Collections.singletonMap(
							LocaleUtil.US, RandomTestUtil.randomString())),
					ListTypeEntryUtil.createListTypeEntry(
						RandomTestUtil.randomString(),
						Collections.singletonMap(
							LocaleUtil.US, RandomTestUtil.randomString()))),
				new ServiceContext());

		return ListUtil.fromArray(
			new AttachmentObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"attachment"
			).objectFieldSettings(
				Arrays.asList(
					_createObjectFieldSetting(
						ObjectFieldSettingConstants.
							NAME_ACCEPTED_FILE_EXTENSIONS,
						"txt"),
					_createObjectFieldSetting(
						ObjectFieldSettingConstants.NAME_FILE_SOURCE,
						ObjectFieldSettingConstants.
							VALUE_USER_COMPUTER_TO_DOCS_AND_MEDIA),
					_createObjectFieldSetting(
						ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE, "100"))
			).build(),
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_BOOLEAN,
				ObjectFieldConstants.DB_TYPE_BOOLEAN,
				RandomTestUtil.randomString(), "boolean"),
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_DATE,
				ObjectFieldConstants.DB_TYPE_DATE,
				RandomTestUtil.randomString(), "date"),
			new DateTimeObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"dateTime"
			).objectFieldSettings(
				Collections.singletonList(
					_createObjectFieldSetting(
						ObjectFieldSettingConstants.NAME_TIME_STORAGE,
						ObjectFieldSettingConstants.VALUE_CONVERT_TO_UTC))
			).build(),
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_DECIMAL,
				ObjectFieldConstants.DB_TYPE_DOUBLE,
				RandomTestUtil.randomString(), "decimal"),
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_INTEGER,
				ObjectFieldConstants.DB_TYPE_INTEGER,
				RandomTestUtil.randomString(), "integer"),
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_LONG_INTEGER,
				ObjectFieldConstants.DB_TYPE_LONG,
				RandomTestUtil.randomString(), "longInteger"),
			new MultiselectPicklistObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).listTypeDefinitionId(
				listTypeDefinition.getListTypeDefinitionId()
			).name(
				"multiselectPicklist"
			).build(),
			new PicklistObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).listTypeDefinitionId(
				listTypeDefinition.getListTypeDefinitionId()
			).name(
				"picklist"
			).build(),
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_PRECISION_DECIMAL,
				ObjectFieldConstants.DB_TYPE_BIG_DECIMAL,
				RandomTestUtil.randomString(), "precisionDecimal"),
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_RICH_TEXT,
				ObjectFieldConstants.DB_TYPE_STRING,
				RandomTestUtil.randomString(), "richText"),
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_TEXT,
				ObjectFieldConstants.DB_TYPE_STRING,
				RandomTestUtil.randomString(), "text"));
	}

	private OpenGraphSettings _getOpenGraphSettings() {
		return new OpenGraphSettings() {
			{
				setDescription_i18n(
					() -> HashMapBuilder.put(
						"en-US", RandomTestUtil.randomString()
					).put(
						"es-ES", RandomTestUtil.randomString()
					).build());
				setImage(
					() -> new ItemExternalReference() {
						{
							setClassName(FileEntry.class::getName);
							setExternalReferenceCode(
								() -> {
									Company company =
										CompanyLocalServiceUtil.getCompany(
											TestPropsValues.getCompanyId());

									DLFolder dlFolder = DLTestUtil.addDLFolder(
										company.getGroupId());

									DLFileEntry dlFileEntry =
										DLTestUtil.addDLFileEntry(
											dlFolder.getFolderId());

									return dlFileEntry.
										getExternalReferenceCode();
								});
							setScope(
								() -> new Scope() {
									{
										setExternalReferenceCode(
											() -> "L_GLOBAL");
										setType(() -> Type.SITE);
									}
								});
						}
					});
				setImageAlt_i18n(
					() -> HashMapBuilder.put(
						"en-US", RandomTestUtil.randomString()
					).put(
						"es-ES", RandomTestUtil.randomString()
					).build());
				setTitle_i18n(
					() -> HashMapBuilder.put(
						"en-US", RandomTestUtil.randomString()
					).put(
						"es-ES", RandomTestUtil.randomString()
					).build());
			}
		};
	}

	private PageSettings _getPageSettings(
			String parentSitePageExternalReferenceCode, SitePage.Type type)
		throws Exception {

		PageSettings pageSettings = null;

		if (type == SitePage.Type.CONTENT_PAGE) {
			pageSettings = new ContentPageSettings() {
				{
					setCustomMetaTags(() -> _getCustomMetaTags());
					setOpenGraphSettings(() -> _getOpenGraphSettings());
					setSeoSettings(() -> _getSeoSettings());
					setType(Type.CONTENT_PAGE_SETTINGS);
				}
			};
		}
		else if (type == SitePage.Type.EMBEDDED_PAGE) {
			pageSettings = new EmbeddedPageSettings() {
				{
					setPageURL(
						"http://www." + RandomTestUtil.randomString() + ".com");
					setType(Type.EMBEDDED_PAGE_SETTINGS);
				}
			};
		}
		else if (type == SitePage.Type.LINK_TO_PAGE_PAGE) {
			pageSettings = new LinkToPagePageSettings() {
				{
					setLinkToPageExternalReferenceCode(
						() -> {
							Layout layout = LayoutTestUtil.addTypeContentLayout(
								testGroup);

							return layout.getExternalReferenceCode();
						});
					setType(Type.LINK_TO_PAGE_PAGE_SETTINGS);
				}
			};
		}
		else if (type == SitePage.Type.LINK_TO_URL_PAGE) {
			pageSettings = new LinkToURLPageSettings() {
				{
					setPageURL(
						"http://www." + RandomTestUtil.randomString() + ".com");
					setType(Type.LINK_TO_URL_PAGE_SETTINGS);
				}
			};
		}
		else if (type == SitePage.Type.PAGE_SET_PAGE) {
			pageSettings = new PageSetPageSettings() {
				{
					setType(Type.PAGE_SET_PAGE_SETTINGS);
				}
			};
		}
		else {
			pageSettings = new WidgetPageSettings() {
				{
					setCustomizable(false);
					setCustomizableSectionIds(new String[0]);
					setCustomMetaTags(() -> _getCustomMetaTags());
					setLayoutTemplateId("1_column");
					setOpenGraphSettings(() -> _getOpenGraphSettings());
					setSeoSettings(() -> _getSeoSettings());
					setType(Type.WIDGET_PAGE_SETTINGS);
				}
			};
		}

		pageSettings.setHiddenFromNavigation(RandomTestUtil::randomBoolean);
		pageSettings.setNavigationSettings(
			() -> new SitePageNavigationSettings() {
				{
					setQueryString(RandomTestUtil::randomString);
					setTarget(RandomTestUtil::randomString);
					setTargetType(
						() -> RandomTestUtil.randomEnum(
							SitePageNavigationSettings.TargetType.class));
				}
			});
		pageSettings.setPriority(
			_priorities.merge(
				parentSitePageExternalReferenceCode, 0,
				(oldPriority, defaultPriority) -> oldPriority + 1));

		return pageSettings;
	}

	private String _getRandomFriendlyURL() {
		String urlTitle = StringUtil.toLowerCase(
			RandomTestUtil.randomString(
				LayoutFriendlyURLRandomizerBumper.INSTANCE));

		return StringPool.FORWARD_SLASH + urlTitle;
	}

	private SitePage _getRandomSitePage(
			ServiceContext serviceContext, SitePage.Type type)
		throws Exception {

		return _getRandomSitePage(
			StringUtil.toLowerCase(RandomTestUtil.randomString()), null,
			serviceContext, type,
			StringUtil.toLowerCase(RandomTestUtil.randomString()));
	}

	private SitePage _getRandomSitePage(SitePage.Type type) throws Exception {
		return _getRandomSitePage(
			ServiceContextTestUtil.getServiceContext(
				testGroup, TestPropsValues.getUserId()),
			type);
	}

	private SitePage _getRandomSitePage(
			String externalReferenceCode,
			String parentSitePageExternalReferenceCode,
			ServiceContext serviceContext, SitePage.Type type, String uuid)
		throws Exception {

		return _getRandomSitePage(
			externalReferenceCode, parentSitePageExternalReferenceCode,
			serviceContext,
			AssetTestUtil.randomTaxonomyCategoryBriefs(
				testCompany.getGroupId(), serviceContext),
			type, uuid);
	}

	private SitePage _getRandomSitePage(
			String externalReferenceCode,
			String parentSitePageExternalReferenceCode,
			ServiceContext serviceContext,
			TaxonomyCategoryBrief[] taxonomyCategoryBriefs, SitePage.Type type,
			String uuid)
		throws Exception {

		SitePage sitePage = new SitePage();

		sitePage.setAvailableLanguages(
			() -> LocaleUtil.toW3cLanguageIds(
				new Locale[] {LocaleUtil.US, LocaleUtil.SPAIN}));
		sitePage.setExternalReferenceCode(externalReferenceCode);
		sitePage.setFriendlyUrlPath_i18n(
			() -> HashMapBuilder.put(
				LocaleUtil.toBCP47LanguageId(LocaleUtil.SPAIN),
				_getRandomFriendlyURL()
			).put(
				LocaleUtil.toBCP47LanguageId(LocaleUtil.US),
				_getRandomFriendlyURL()
			).build());
		sitePage.setKeywords(AssetTestUtil.randomKeywords(serviceContext));
		sitePage.setName_i18n(
			() -> HashMapBuilder.put(
				LocaleUtil.toBCP47LanguageId(LocaleUtil.US),
				RandomTestUtil.randomString()
			).put(
				LocaleUtil.toBCP47LanguageId(LocaleUtil.SPAIN),
				RandomTestUtil.randomString()
			).build());
		sitePage.setPageSettings(
			_getPageSettings(parentSitePageExternalReferenceCode, type));
		sitePage.setParentSitePageExternalReferenceCode(
			parentSitePageExternalReferenceCode);
		sitePage.setTaxonomyCategoryBriefs(taxonomyCategoryBriefs);
		sitePage.setType(type);
		sitePage.setUuid(uuid);

		return sitePage;
	}

	private SitePage _getRandomSitePageWithWidgetPageTemplate(
			boolean globalPageTemplate)
		throws Exception {

		SitePage sitePage = _getRandomSitePage(SitePage.Type.WIDGET_PAGE);

		WidgetPageSettings widgetPageSettings =
			(WidgetPageSettings)sitePage.getPageSettings();

		widgetPageSettings.setInheritChanges(true);

		long groupId =
			globalPageTemplate ? testCompany.getGroupId() :
				testGroup.getGroupId();

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryTestUtil.
				getWidgetPageLayoutPageTemplateEntry(
					ServiceContextTestUtil.getServiceContext(groupId));

		ItemExternalReference itemExternalReference =
			new ItemExternalReference() {
				{
					setExternalReferenceCode(
						layoutPageTemplateEntry.getExternalReferenceCode());

					if (globalPageTemplate) {
						Group group = _groupLocalService.getGroup(groupId);

						setScope(
							() -> new Scope() {
								{
									setExternalReferenceCode(
										group::getExternalReferenceCode);
									setType(() -> Type.SITE);
								}
							});
					}
				}
			};

		widgetPageSettings.setWidgetPageTemplateReference(
			itemExternalReference);

		return sitePage;
	}

	private SitePage.Type _getRandomType(List<SitePage.Type> types) {
		return types.get(RandomTestUtil.randomInt(0, types.size() - 1));
	}

	private SEOSettings _getSeoSettings() {
		return new SEOSettings() {
			{
				setCustomCanonicalURL_i18n(
					() -> HashMapBuilder.put(
						"en-US", RandomTestUtil.randomString()
					).put(
						"es-ES", RandomTestUtil.randomString()
					).build());
				setDescription_i18n(
					() -> HashMapBuilder.put(
						"en-US", RandomTestUtil.randomString()
					).put(
						"es-ES", RandomTestUtil.randomString()
					).build());
				setHtmlTitle_i18n(
					() -> HashMapBuilder.put(
						"en-US", RandomTestUtil.randomString()
					).put(
						"es-ES", RandomTestUtil.randomString()
					).build());
				setRobots_i18n(
					() -> HashMapBuilder.put(
						"en-US", RandomTestUtil.randomString()
					).put(
						"es-ES", RandomTestUtil.randomString()
					).build());
				setSeoKeywords_i18n(
					() -> HashMapBuilder.put(
						"en-US", RandomTestUtil.randomString()
					).put(
						"es-ES", RandomTestUtil.randomString()
					).build());
				setSitemapSettings(
					() -> new SitemapSettings() {
						{
							setChangeFrequency(
								() -> RandomTestUtil.randomEnum(
									ChangeFrequency.class));
							setInclude(RandomTestUtil::randomBoolean);
							setIncludeChildSitePages(
								RandomTestUtil::randomBoolean);
							setPagePriority(RandomTestUtil::randomDouble);
						}
					});
			}
		};
	}

	private SitePageResource _getSitePageResource(String nestedFields)
		throws Exception {

		User user = UserTestUtil.getAdminUser(testCompany.getCompanyId());

		return SitePageResource.builder(
		).authentication(
			user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(), 8080, "http"
		).locale(
			LocaleUtil.getDefault()
		).parameters(
			"nestedFields", nestedFields
		).build();
	}

	private SitePage _getSitePageWithPageElements(PageElement[] pageElements)
		throws Exception {

		SitePage sitePage = _getRandomSitePage(SitePage.Type.CONTENT_PAGE);

		ContentPageSpecification draftContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				null, testGroup.getGroupId(),
				PageSpecification.Status.APPROVED);

		draftContentPageSpecification.setPageExperiences(
			PageExperiencesTestUtil.getDefaultPageExperiences(
				pageElements,
				draftContentPageSpecification.getExternalReferenceCode()));

		ContentPageSpecification publishedContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				draftContentPageSpecification.getExternalReferenceCode(),
				testGroup.getGroupId(), PageSpecification.Status.APPROVED);

		publishedContentPageSpecification.setExternalReferenceCode(
			sitePage.getExternalReferenceCode());

		publishedContentPageSpecification.setPageExperiences(
			PageExperiencesTestUtil.getDefaultPageExperiences(
				pageElements, sitePage.getExternalReferenceCode()));

		sitePage.setPageSpecifications(
			() -> new PageSpecification[] {
				publishedContentPageSpecification, draftContentPageSpecification
			});

		return sitePage;
	}

	private TaxonomyCategoryBrief _getTaxonomyCategoryBrief(
		String externalReferenceCode, Group group,
		String parentTaxonomyCategoryExternalReferenceCode) {

		return new TaxonomyCategoryBrief() {
			{
				if (parentTaxonomyCategoryExternalReferenceCode != null) {
					setParentTaxonomyCategory(
						() -> new ParentTaxonomyCategory() {
							{
								setExternalReferenceCode(
									parentTaxonomyCategoryExternalReferenceCode);
							}
						});
				}

				setParentTaxonomyVocabulary(
					() -> new ParentTaxonomyVocabulary() {
						{
							setExternalReferenceCode(
								RandomTestUtil::randomString);
						}
					});

				if (group != null) {
					setScope(
						() -> new Scope() {
							{
								setExternalReferenceCode(
									group::getExternalReferenceCode);
								setType(() -> Type.SITE);
							}
						});
				}

				setTaxonomyCategoryExternalReferenceCode(externalReferenceCode);
			}
		};
	}

	private SitePage _postSiteSitePageWithPageSpecificationsWithCustomFields(
			SitePage.Type type)
		throws Exception {

		SitePage randomSitePage = _getRandomSitePage(type);

		if (type == SitePage.Type.CONTENT_PAGE) {
			randomSitePage.setPageSpecifications(
				PageSpecificationsTestUtil.getContentPageSpecifications(
					randomSitePage.getExternalReferenceCode(),
					testGroup.getGroupId()));
		}
		else {
			randomSitePage.setPageSpecifications(
				PageSpecificationsTestUtil.getWidgetPageSpecifications(
					PageSpecificationsTestUtil.getCustomFields(), "1_column",
					randomSitePage.getExternalReferenceCode()));
		}

		SitePageResource sitePageResource = _getSitePageResource(
			"pageSpecifications");

		SitePage postSitePage = sitePageResource.postSiteSitePage(
			testGroup.getExternalReferenceCode(), false, randomSitePage);

		PageSpecificationsTestUtil.assertCustomFields(
			TransformUtil.transform(
				randomSitePage.getPageSpecifications(),
				pageSpecification -> pageSpecification.getCustomFields(),
				CustomField[].class),
			testGroup.getGroupId(), postSitePage.getPageSpecifications());

		return postSitePage;
	}

	private void _testDeleteSiteSitePage(Layout... layouts) throws Exception {
		for (Layout layout : layouts) {
			sitePageResource.deleteSiteSitePage(
				testGroup.getExternalReferenceCode(),
				layout.getExternalReferenceCode());

			Assert.assertNull(
				_layoutLocalService.fetchLayoutByExternalReferenceCode(
					layout.getExternalReferenceCode(), testGroup.getGroupId()));
		}
	}

	private void _testGetSitePageSitePagesPage(boolean privateLayout)
		throws Exception {

		String siteExternalReferenceCode =
			testGetSiteSitePagesPage_getSiteExternalReferenceCode();

		String sitePageExternalReferenceCode = StringUtil.toLowerCase(
			RandomTestUtil.randomString());

		SitePage sitePage = sitePageResource.postSiteSitePage(
			siteExternalReferenceCode, privateLayout,
			_getRandomSitePage(
				sitePageExternalReferenceCode, null,
				ServiceContextTestUtil.getServiceContext(
					testGroup, TestPropsValues.getUserId()),
				SitePage.Type.CONTENT_PAGE,
				StringUtil.toLowerCase(RandomTestUtil.randomString())));

		Page<SitePage> page = sitePageResource.getSiteSitePagesPage(
			siteExternalReferenceCode, privateLayout, null, null,
			"externalReferenceCode eq '" + sitePage.getExternalReferenceCode() +
				"'",
			Pagination.of(1, 10), null);

		Assert.assertEquals(1, page.getTotalCount());

		List<SitePage> pages = new ArrayList<>(page.getItems());

		Assert.assertEquals(sitePage, pages.get(0));

		Layout layout = _layoutLocalService.getLayoutByExternalReferenceCode(
			sitePageExternalReferenceCode, testGroup.getGroupId());

		Assert.assertEquals(privateLayout, layout.isPrivateLayout());
	}

	private void _testGetSiteSitePage(Layout layout) throws Exception {
		SitePage sitePage = sitePageResource.getSiteSitePage(
			testGroup.getExternalReferenceCode(),
			layout.getExternalReferenceCode());

		_assertSitePage(layout, sitePage);
		_testGetSiteSitePageWithNestedFields(sitePage);
	}

	private void _testGetSiteSitePageWithNestedFields(SitePage sitePage)
		throws Exception {

		SitePageResource sitePageResource = _getSitePageResource(
			"friendlyUrlHistory,pageSpecifications");

		_assertNestedFields(
			sitePageResource.getSiteSitePage(
				testGroup.getExternalReferenceCode(),
				sitePage.getExternalReferenceCode()));
	}

	private SitePage _testPatchSiteSitePage(
			SitePage expectedSitePage, SitePage sitePage)
		throws Exception {

		SitePage patchSitePage = sitePageResource.patchSiteSitePage(
			testGroup.getExternalReferenceCode(),
			sitePage.getExternalReferenceCode(), false, sitePage);

		assertEquals(expectedSitePage, patchSitePage);
		assertValid(patchSitePage);

		_assertSitePage(
			_layoutLocalService.getLayoutByExternalReferenceCode(
				sitePage.getExternalReferenceCode(), testGroup.getGroupId()),
			patchSitePage);

		return patchSitePage;
	}

	private void _testPatchSiteSitePage(SitePage.Type type) throws Exception {
		SitePage sitePage = testPostSiteSitePage_addSitePage(
			_getRandomSitePage(type));

		Layout layout = _layoutLocalService.getLayoutByExternalReferenceCode(
			sitePage.getExternalReferenceCode(), testGroup.getGroupId());

		_assertSitePage(layout, sitePage);

		_testPatchSiteSitePageWithFriendlyUrlPath(layout, sitePage);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup, TestPropsValues.getUserId());

		sitePage.setKeywords(
			() -> AssetTestUtil.randomKeywords(serviceContext));

		_testPatchSiteSitePage(
			sitePage,
			new SitePage() {
				{
					setExternalReferenceCode(
						sitePage::getExternalReferenceCode);
					setKeywords(sitePage::getKeywords);
					setType(sitePage::getType);
					setUuid(sitePage::getUuid);
				}
			});

		sitePage.setName_i18n(
			() -> HashMapBuilder.put(
				LocaleUtil.toBCP47LanguageId(LocaleUtil.US),
				RandomTestUtil.randomString()
			).put(
				LocaleUtil.toBCP47LanguageId(LocaleUtil.SPAIN),
				RandomTestUtil.randomString()
			).build());

		_testPatchSiteSitePage(
			sitePage,
			new SitePage() {
				{
					setExternalReferenceCode(
						sitePage::getExternalReferenceCode);
					setName_i18n(sitePage::getName_i18n);
					setType(sitePage::getType);
					setUuid(sitePage::getUuid);
				}
			});

		PageSettings pageSettings = sitePage.getPageSettings();

		pageSettings.setHiddenFromNavigation(
			() -> !pageSettings.getHiddenFromNavigation());

		_testPatchSiteSitePage(
			sitePage,
			new SitePage() {
				{
					setExternalReferenceCode(
						sitePage::getExternalReferenceCode);
					setPageSettings(sitePage::getPageSettings);
					setType(sitePage::getType);
					setUuid(sitePage::getUuid);
				}
			});

		Layout parentLayout = LayoutTestUtil.addTypePortletLayout(testGroup);

		sitePage.setParentSitePageExternalReferenceCode(
			parentLayout.getExternalReferenceCode());

		pageSettings.setPriority(0);

		_testPatchSiteSitePage(
			sitePage,
			new SitePage() {
				{
					setExternalReferenceCode(
						sitePage::getExternalReferenceCode);
					setParentSitePageExternalReferenceCode(
						parentLayout::getExternalReferenceCode);
					setType(sitePage::getType);
					setUuid(sitePage::getUuid);
				}
			});

		sitePage.setTaxonomyCategoryBriefs(
			AssetTestUtil.randomTaxonomyCategoryBriefs(
				testCompany.getGroupId(), serviceContext));

		_testPatchSiteSitePage(
			sitePage,
			new SitePage() {
				{
					setExternalReferenceCode(
						sitePage::getExternalReferenceCode);
					setTaxonomyCategoryBriefs(
						sitePage::getTaxonomyCategoryBriefs);
					setType(sitePage::getType);
					setUuid(sitePage::getUuid);
				}
			});

		_assertPatchSiteSitePageProblemException(
			_getRandomSitePage(
				sitePage.getExternalReferenceCode(), null, serviceContext,
				_getRandomType(
					ListUtil.filter(
						_types, curType -> !Objects.equals(curType, type))),
				sitePage.getUuid()));
	}

	private void _testPatchSiteSitePageWithFriendlyUrlPath(
			Layout layout, SitePage sitePage)
		throws Exception {

		SitePageResource sitePageResource = _getSitePageResource(
			"friendlyUrlHistory");

		Map<Locale, String> friendlyURLMap = layout.getFriendlyURLMap();

		_assertFriendlyUrlHistory(
			friendlyURLMap, Collections.emptyMap(), layout.isPublished(),
			sitePageResource.getSiteSitePage(
				testGroup.getExternalReferenceCode(),
				sitePage.getExternalReferenceCode()));

		Map<String, String> newFriendlyURLsMap = HashMapBuilder.put(
			LocaleUtil.toBCP47LanguageId(LocaleUtil.SPAIN),
			_getRandomFriendlyURL()
		).put(
			LocaleUtil.toBCP47LanguageId(LocaleUtil.US), _getRandomFriendlyURL()
		).build();

		sitePage.setFriendlyUrlPath_i18n(() -> newFriendlyURLsMap);

		_testPatchSiteSitePage(
			sitePage,
			new SitePage() {
				{
					setExternalReferenceCode(
						sitePage::getExternalReferenceCode);
					setFriendlyUrlPath_i18n(sitePage::getFriendlyUrlPath_i18n);
					setType(sitePage::getType);
					setUuid(sitePage::getUuid);
				}
			});

		_assertFriendlyUrlHistory(
			friendlyURLMap, newFriendlyURLsMap, layout.isPublished(),
			sitePageResource.getSiteSitePage(
				testGroup.getExternalReferenceCode(),
				sitePage.getExternalReferenceCode()));
	}

	private void _testPatchSiteSitePageWithPageSpecifications()
		throws Exception {

		_testPatchSiteSitePageWithPageSpecifications(
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT,
			PageSpecification.Status.APPROVED);
		_testPatchSiteSitePageWithPageSpecifications(
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT,
			PageSpecification.Status.DRAFT);
		_testPatchSiteSitePageWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED);
		_testPatchSiteSitePageWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.DRAFT,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT);
		_testPatchSiteSitePageWithPageSpecificationsWithCustomFields();
		_testPatchSiteSitePageWithPageSpecificationsWithWidgetPageSpecification();
	}

	private void _testPatchSiteSitePageWithPageSpecifications(
			PageSpecification.Status newDraftLayoutStatus,
			PageSpecification.Status newPublishedLayoutStatus,
			PageSpecification.Status oldDraftLayoutStatus,
			PageSpecification.Status oldPublishedLayoutStatus)
		throws Exception {

		SitePage sitePage = _getRandomSitePage(SitePage.Type.CONTENT_PAGE);

		ContentPageSpecification draftContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				null, testGroup.getGroupId(), oldDraftLayoutStatus);

		ContentPageSpecification publishedContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				draftContentPageSpecification.getExternalReferenceCode(),
				testGroup.getGroupId(), oldPublishedLayoutStatus);

		publishedContentPageSpecification.setExternalReferenceCode(
			sitePage.getExternalReferenceCode());

		sitePage.setPageSpecifications(
			() -> new PageSpecification[] {
				publishedContentPageSpecification, draftContentPageSpecification
			});

		SitePageResource sitePageResource = _getSitePageResource(
			"pageSpecifications");

		SitePage postSitePage = sitePageResource.postSiteSitePage(
			testGroup.getExternalReferenceCode(), false, sitePage);

		_assertPageSpecifications(
			draftContentPageSpecification, publishedContentPageSpecification,
			postSitePage);

		draftContentPageSpecification.setStatus(newDraftLayoutStatus);
		publishedContentPageSpecification.setStatus(newPublishedLayoutStatus);

		_assertPageSpecifications(
			draftContentPageSpecification, publishedContentPageSpecification,
			sitePageResource.patchSiteSitePage(
				testGroup.getExternalReferenceCode(),
				postSitePage.getExternalReferenceCode(), false,
				new SitePage() {
					{
						setPageSpecifications(
							() -> new PageSpecification[] {
								publishedContentPageSpecification,
								draftContentPageSpecification
							});
					}
				}));
	}

	private void _testPatchSiteSitePageWithPageSpecificationsWithCustomFields()
		throws Exception {

		try (PageSpecificationsTestUtil.ExpandoTableAutocloseable
				expandoTableAutoCloseable =
					PageSpecificationsTestUtil.getExpandoTableAutoCloseable()) {

			_testPatchSiteSitePageWithPageSpecificationsWithCustomFields(
				SitePage.Type.CONTENT_PAGE);
			_testPatchSiteSitePageWithPageSpecificationsWithCustomFields(
				SitePage.Type.WIDGET_PAGE);
		}
	}

	private void _testPatchSiteSitePageWithPageSpecificationsWithCustomFields(
			SitePage.Type type)
		throws Exception {

		SitePage postSitePage =
			_postSiteSitePageWithPageSpecificationsWithCustomFields(type);

		SitePageResource sitePageResource = _getSitePageResource(
			"pageSpecifications");

		PageSpecification[] patchPageSpecifications =
			PageSpecificationsTestUtil.getPatchPageSpecifications(
				postSitePage.getPageSpecifications(), testGroup.getGroupId());

		SitePage patchSitePage = sitePageResource.patchSiteSitePage(
			testGroup.getExternalReferenceCode(),
			postSitePage.getExternalReferenceCode(), false,
			new SitePage() {
				{
					setExternalReferenceCode(
						postSitePage.getExternalReferenceCode());
					setPageSpecifications(patchPageSpecifications);
					setType(postSitePage.getType());
				}
			});

		PageSpecificationsTestUtil.assertCustomFields(
			TransformUtil.transform(
				patchPageSpecifications,
				pageSpecification -> pageSpecification.getCustomFields(),
				CustomField[].class),
			testGroup.getGroupId(), patchSitePage.getPageSpecifications());
	}

	private void _testPatchSiteSitePageWithPageSpecificationsWithWidgetPageSpecification()
		throws Exception {

		SitePageResource sitePageResource = _getSitePageResource(
			"pageSpecifications");

		SitePage randomSitePage = _getRandomSitePage(SitePage.Type.WIDGET_PAGE);

		randomSitePage.setPageSpecifications(
			PageSpecificationsTestUtil.getWidgetPageSpecifications(
				null, "1_column", randomSitePage.getExternalReferenceCode()));

		SitePage sitePage = sitePageResource.postSiteSitePage(
			testGroup.getExternalReferenceCode(), false, randomSitePage);

		WidgetPageSettings widgetPageSettings =
			(WidgetPageSettings)sitePage.getPageSettings();

		widgetPageSettings.setLayoutTemplateId("2_columns_ii");

		PageSpecification[] widgetPageSpecifications =
			PageSpecificationsTestUtil.getWidgetPageSpecifications(
				null, "2_columns_ii",
				randomSitePage.getExternalReferenceCode());

		SitePage patchSitePage = sitePageResource.patchSiteSitePage(
			testGroup.getExternalReferenceCode(),
			sitePage.getExternalReferenceCode(), false,
			new SitePage() {
				{
					setExternalReferenceCode(
						sitePage.getExternalReferenceCode());
					setPageSettings(widgetPageSettings);
					setPageSpecifications(widgetPageSpecifications);
					setType(Type.WIDGET_PAGE);
				}
			});

		PageSpecificationsTestUtil.assertWidgetPageSpecifications(
			widgetPageSpecifications, patchSitePage.getPageSpecifications());
	}

	private void _testPatchSiteSitePageWithPriority() throws Exception {
		_testUpdateSiteSitePageWithPriority(
			(curParentSitePageExternalReferenceCode, curPriority, sitePage) -> {
				int expectedPriority = _getExpectedPriority(
					sitePage.getParentSitePageExternalReferenceCode(),
					curParentSitePageExternalReferenceCode, curPriority);

				sitePage.setParentSitePageExternalReferenceCode(
					() -> curParentSitePageExternalReferenceCode);

				PageSettings curPageSettings = sitePage.getPageSettings();

				curPageSettings.setPriority(() -> curPriority);

				SitePage patchSitePage = sitePageResource.patchSiteSitePage(
					testGroup.getExternalReferenceCode(),
					sitePage.getExternalReferenceCode(), false,
					new SitePage() {
						{
							setPageSettings(() -> curPageSettings);
							setParentSitePageExternalReferenceCode(
								() -> curParentSitePageExternalReferenceCode);
						}
					});

				PageSettings patchPageSettings =
					patchSitePage.getPageSettings();

				Assert.assertEquals(
					expectedPriority, (int)patchPageSettings.getPriority());

				assertEquals(sitePage, patchSitePage);
				assertValid(patchSitePage);

				_assertSitePage(
					_layoutLocalService.getLayoutByExternalReferenceCode(
						sitePage.getExternalReferenceCode(),
						testGroup.getGroupId()),
					patchSitePage);
			});
	}

	private void _testPatchSiteSitePageWithWidgetPageSettings()
		throws Exception {

		SitePage randomSitePage = _getRandomSitePage(SitePage.Type.WIDGET_PAGE);

		SitePage sitePage = _testPutSiteSitePage(
			randomSitePage, testGroup, randomSitePage);

		WidgetPageSettings widgetPageSettings =
			(WidgetPageSettings)sitePage.getPageSettings();

		widgetPageSettings.setCustomizable(true);
		widgetPageSettings.setCustomizableSectionIds(
			new String[] {"column-1", "column-3"});
		widgetPageSettings.setCustomMetaTags(() -> null);
		widgetPageSettings.setLayoutTemplateId("1_2_columns_i");
		widgetPageSettings.setNavigationSettings(
			new SitePageNavigationSettings() {
				{
					setQueryString(() -> null);
					setTarget(() -> null);
					setTargetType(TargetType.SPECIFIC_FRAME);
				}
			});
		widgetPageSettings.setOpenGraphSettings(() -> null);
		widgetPageSettings.setSeoSettings(
			new SEOSettings() {
				{
					setCustomCanonicalURL_i18n(new HashMap<>());
					setDescription_i18n(new HashMap<>());
					setHtmlTitle_i18n(new HashMap<>());
					setRobots_i18n(new HashMap<>());
					setSeoKeywords_i18n(new HashMap<>());
					setSitemapSettings(() -> null);
				}
			});

		String sitePageExternalReferenceCode =
			sitePage.getExternalReferenceCode();

		sitePage = _testPatchSiteSitePage(
			sitePage,
			new SitePage() {
				{
					setExternalReferenceCode(sitePageExternalReferenceCode);
					setPageSettings(
						new WidgetPageSettings() {
							{
								setCustomizable(true);
								setCustomizableSectionIds(
									new String[] {"column-1", "column-3"});
								setLayoutTemplateId("1_2_columns_i");
								setType(Type.WIDGET_PAGE_SETTINGS);
							}
						});
					setType(SitePage.Type.WIDGET_PAGE);
				}
			});

		widgetPageSettings = (WidgetPageSettings)sitePage.getPageSettings();

		widgetPageSettings.setCustomizable(true);
		widgetPageSettings.setCustomizableSectionIds(new String[] {"column-2"});
		widgetPageSettings.setLayoutTemplateId("2_columns_ii");

		sitePage = _testPatchSiteSitePage(
			sitePage,
			new SitePage() {
				{
					setExternalReferenceCode(sitePageExternalReferenceCode);
					setPageSettings(
						new WidgetPageSettings() {
							{
								setCustomizable(true);
								setCustomizableSectionIds(
									new String[] {"column-2"});
								setType(Type.WIDGET_PAGE_SETTINGS);
							}
						});
					setType(SitePage.Type.WIDGET_PAGE);
				}
			});

		widgetPageSettings = (WidgetPageSettings)sitePage.getPageSettings();

		widgetPageSettings.setCustomizable(false);
		widgetPageSettings.setCustomizableSectionIds(new String[0]);
		widgetPageSettings.setLayoutTemplateId("2_columns_ii");

		_testPatchSiteSitePage(
			sitePage,
			new SitePage() {
				{
					setExternalReferenceCode(sitePageExternalReferenceCode);
					setPageSettings(
						new WidgetPageSettings() {
							{
								setType(Type.WIDGET_PAGE_SETTINGS);
							}
						});
					setType(SitePage.Type.WIDGET_PAGE);
				}
			});
	}

	private void _testPatchSiteSitePageWithWidgetPageSettingsWithWidgetPageTemplate()
		throws Exception {

		SitePage sitePageWithWidgetPageTemplate =
			_getRandomSitePageWithWidgetPageTemplate(false);

		SitePage sitePage = _testPutSiteSitePage(
			sitePageWithWidgetPageTemplate, testGroup,
			sitePageWithWidgetPageTemplate);

		WidgetPageSettings widgetPageSettings =
			(WidgetPageSettings)sitePage.getPageSettings();

		widgetPageSettings.setCustomMetaTags(() -> null);
		widgetPageSettings.setInheritChanges(false);
		widgetPageSettings.setLayoutTemplateId("2_columns_ii");
		widgetPageSettings.setNavigationSettings(
			new SitePageNavigationSettings() {
				{
					setQueryString(() -> null);
					setTarget(() -> null);
					setTargetType(TargetType.SPECIFIC_FRAME);
				}
			});
		widgetPageSettings.setOpenGraphSettings(() -> null);
		widgetPageSettings.setSeoSettings(
			new SEOSettings() {
				{
					setCustomCanonicalURL_i18n(new HashMap<>());
					setDescription_i18n(new HashMap<>());
					setHtmlTitle_i18n(new HashMap<>());
					setRobots_i18n(new HashMap<>());
					setSeoKeywords_i18n(new HashMap<>());
					setSitemapSettings(() -> null);
				}
			});

		_testPatchSiteSitePage(
			sitePage,
			new SitePage() {
				{
					setExternalReferenceCode(
						sitePage.getExternalReferenceCode());
					setPageSettings(
						new WidgetPageSettings() {
							{
								setInheritChanges(false);
								setType(Type.WIDGET_PAGE_SETTINGS);
								setWidgetPageTemplateReference(
									widgetPageSettings.
										getWidgetPageTemplateReference());
							}
						});
					setType(SitePage.Type.WIDGET_PAGE);
				}
			});
	}

	private void _testPostSitePageWithPageSpecificationsWithSettings(
			SitePage.Type type)
		throws Exception {

		FavIcon.FavIconType favIconType =
			FavIcon.FavIconType.ITEM_EXTERNAL_REFERENCE;
		boolean optionalMasterPageReference = false;
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId());

		SitePage sitePage = _getRandomSitePage(type);

		PageSpecification[] pageSpecifications =
			PageSpecificationsTestUtil.getPageSpecifications(
				sitePage.getExternalReferenceCode(), testGroup.getGroupId(),
				type);

		for (PageSpecification pageSpecification : pageSpecifications) {
			if (type == SitePage.Type.CONTENT_PAGE) {
				ContentPageSpecification contentPageSpecification =
					(ContentPageSpecification)pageSpecification;

				contentPageSpecification.setSettings(
					SettingsTestUtil.getSettings(
						favIconType, optionalMasterPageReference,
						serviceContext));
			}
			else {
				WidgetPageSpecification widgetPageSpecification =
					(WidgetPageSpecification)pageSpecification;

				widgetPageSpecification.setSettings(
					SettingsTestUtil.getSettings(
						favIconType, optionalMasterPageReference,
						serviceContext));
			}

			favIconType = FavIcon.FavIconType.CLIENT_EXTENSION;
			optionalMasterPageReference = true;
		}

		sitePage.setPageSpecifications(pageSpecifications);

		SitePageResource sitePageResource = _getSitePageResource(
			"pageSpecifications");

		SitePage postSitePage = null;

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.headless.admin.site.internal.util.LogUtil",
				LoggerTestUtil.WARN)) {

			postSitePage = sitePageResource.postSiteSitePage(
				testGroup.getExternalReferenceCode(), false, sitePage);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			int count = 0;

			for (LogEntry logEntry : logEntries) {
				String message = logEntry.getMessage();

				if (message.contains("LayoutPageTemplateEntry")) {
					count++;
				}
			}

			Assert.assertEquals(
				"Unexpected log messages: " + count,
				(type == SitePage.Type.CONTENT_PAGE) ? 1 : 0, count);
		}

		PageSpecification[] postPageSpecifications =
			postSitePage.getPageSpecifications();

		if (type == SitePage.Type.CONTENT_PAGE) {
			_assertPageSpecifications(
				(ContentPageSpecification)pageSpecifications[1],
				(ContentPageSpecification)pageSpecifications[0], postSitePage);
		}
		else {
			PageSpecificationsTestUtil.assertWidgetPageSpecifications(
				postPageSpecifications,
				(WidgetPageSpecification)pageSpecifications[0]);
		}
	}

	private void _testPostSiteParentSitePage(
			boolean privateLayout, ServiceContext serviceContext)
		throws Exception {

		Layout parentLayout = LayoutTestUtil.addTypePortletLayout(
			testGroup, privateLayout);

		sitePageResource.postSiteSitePage(
			testGroup.getExternalReferenceCode(), privateLayout,
			_getRandomSitePage(
				StringUtil.toLowerCase(RandomTestUtil.randomString()),
				parentLayout.getExternalReferenceCode(), serviceContext,
				SitePage.Type.CONTENT_PAGE,
				StringUtil.toLowerCase(RandomTestUtil.randomString())));

		_assertProblemException(
			null,
			() -> sitePageResource.postSiteSitePage(
				testGroup.getExternalReferenceCode(), !privateLayout,
				_getRandomSitePage(
					StringUtil.toLowerCase(RandomTestUtil.randomString()),
					parentLayout.getExternalReferenceCode(), serviceContext,
					SitePage.Type.CONTENT_PAGE,
					StringUtil.toLowerCase(RandomTestUtil.randomString()))));
	}

	private void _testPostSiteSitePage(SitePage sitePage) throws Exception {
		_testPostSiteSitePage(sitePage, sitePage);
	}

	private void _testPostSiteSitePage(
			SitePage expectedSitePage, SitePage sitePage)
		throws Exception {

		SitePage postSitePage = testPostSiteSitePage_addSitePage(sitePage);

		assertEquals(expectedSitePage, postSitePage);
		assertValid(postSitePage);

		_assertSitePage(
			_layoutLocalService.getLayoutByExternalReferenceCode(
				sitePage.getExternalReferenceCode(), testGroup.getGroupId()),
			postSitePage);
	}

	private void _testPostSiteSitePageWithPageElements() throws Exception {
		PageElement[] pageElements = PageElementsTestUtil.getPageElements(
			testGroup.getGroupId());

		SitePage sitePage = _getSitePageWithPageElements(pageElements);

		SitePageResource sitePageResource = _getSitePageResource(
			"pageSpecifications");

		_assertPageElements(
			pageElements,
			sitePageResource.postSiteSitePage(
				testGroup.getExternalReferenceCode(), false, sitePage));
	}

	private void _testPostSiteSitePageWithPageSpecifications()
		throws Exception {

		_testPostSiteSitePageWithPageSpecifications(
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED);
		_testPostSiteSitePageWithPageSpecifications(
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT);
		_testPostSiteSitePageWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.APPROVED);
		_testPostSiteSitePageWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.DRAFT);
		_testPostSiteSitePageWithPageSpecificationsWithCustomFields();
		_testPostSitePageWithPageSpecificationsWithSettings(
			SitePage.Type.CONTENT_PAGE);
		_testPostSitePageWithPageSpecificationsWithSettings(
			SitePage.Type.WIDGET_PAGE);
		_testPostSiteSitePageWithPageSpecificationsWithWidgetPageSpecification(
			"1_column");
		_testPostSiteSitePageWithPageSpecificationsWithWidgetPageSpecification(
			"1_2_1_columns_i");

		SitePage sitePage = _getRandomSitePage(SitePage.Type.CONTENT_PAGE);

		ContentPageSpecification draftContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				null, testGroup.getGroupId(),
				PageSpecification.Status.APPROVED);

		ContentPageSpecification publishedContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				draftContentPageSpecification.getExternalReferenceCode(),
				testGroup.getGroupId(), PageSpecification.Status.APPROVED);

		sitePage.setPageSpecifications(
			() -> new PageSpecification[] {
				publishedContentPageSpecification, draftContentPageSpecification
			});

		_assertProblemException(
			StringBundler.concat(
				"Site page external reference code ",
				sitePage.getExternalReferenceCode(),
				" does not match published page specification external ",
				"reference code ",
				publishedContentPageSpecification.getExternalReferenceCode()),
			() -> sitePageResource.postSiteSitePage(
				testGroup.getExternalReferenceCode(), false, sitePage));
	}

	private void _testPostSiteSitePageWithPageSpecifications(
			PageSpecification.Status draftLayoutStatus,
			PageSpecification.Status publishedLayoutStatus)
		throws Exception {

		SitePage sitePage = _getRandomSitePage(SitePage.Type.CONTENT_PAGE);

		ContentPageSpecification draftContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				null, testGroup.getGroupId(), draftLayoutStatus);

		ContentPageSpecification publishedContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				draftContentPageSpecification.getExternalReferenceCode(),
				testGroup.getGroupId(), publishedLayoutStatus);

		publishedContentPageSpecification.setExternalReferenceCode(
			sitePage.getExternalReferenceCode());

		sitePage.setPageSpecifications(
			() -> new PageSpecification[] {
				publishedContentPageSpecification, draftContentPageSpecification
			});

		SitePageResource sitePageResource = _getSitePageResource(
			"pageSpecifications");

		_assertPageSpecifications(
			draftContentPageSpecification, publishedContentPageSpecification,
			sitePageResource.postSiteSitePage(
				testGroup.getExternalReferenceCode(), false, sitePage));
	}

	private void _testPostSiteSitePageWithPageSpecificationsWithCustomFields()
		throws Exception {

		try (PageSpecificationsTestUtil.ExpandoTableAutocloseable
				expandoTableAutoCloseable =
					PageSpecificationsTestUtil.getExpandoTableAutoCloseable()) {

			_postSiteSitePageWithPageSpecificationsWithCustomFields(
				SitePage.Type.CONTENT_PAGE);
			_postSiteSitePageWithPageSpecificationsWithCustomFields(
				SitePage.Type.WIDGET_PAGE);
		}
	}

	private void
			_testPostSiteSitePageWithPageSpecificationsWithWidgetPageSpecification(
				String layoutTemplateId)
		throws Exception {

		SitePageResource sitePageResource = _getSitePageResource(
			"pageSpecifications");

		SitePage randomSitePage = _getRandomSitePage(SitePage.Type.WIDGET_PAGE);

		WidgetPageSettings widgetPageSettings =
			(WidgetPageSettings)randomSitePage.getPageSettings();

		widgetPageSettings.setLayoutTemplateId(layoutTemplateId);

		randomSitePage.setPageSpecifications(
			PageSpecificationsTestUtil.getWidgetPageSpecifications(
				null, widgetPageSettings.getLayoutTemplateId(),
				randomSitePage.getExternalReferenceCode()));

		SitePage sitePage = sitePageResource.postSiteSitePage(
			testGroup.getExternalReferenceCode(), false, randomSitePage);

		PageSpecificationsTestUtil.assertWidgetPageSpecifications(
			randomSitePage.getPageSpecifications(),
			sitePage.getPageSpecifications());
	}

	private void _testPostSiteSitePageWithWidgetPageSettings()
		throws Exception {

		SitePage sitePage = _getRandomSitePage(SitePage.Type.WIDGET_PAGE);

		_testPostSiteSitePage(sitePage);

		sitePage = _getRandomSitePage(SitePage.Type.WIDGET_PAGE);

		WidgetPageSettings widgetPageSettings =
			(WidgetPageSettings)sitePage.getPageSettings();

		widgetPageSettings.setCustomizable(true);
		widgetPageSettings.setCustomizableSectionIds(
			new String[] {"column-1", "column-3"});
		widgetPageSettings.setLayoutTemplateId("1_2_columns_i");

		_testPostSiteSitePage(sitePage);

		sitePage = _getRandomSitePage(SitePage.Type.WIDGET_PAGE);

		widgetPageSettings = (WidgetPageSettings)sitePage.getPageSettings();

		widgetPageSettings.setCustomizable((Boolean)null);
		widgetPageSettings.setCustomizableSectionIds((String[])null);
		widgetPageSettings.setLayoutTemplateId((String)null);

		SitePage expectedSitePage = sitePage.clone();

		WidgetPageSettings expectedWidgetPageSettings =
			(WidgetPageSettings)sitePage.getPageSettings();

		expectedWidgetPageSettings.setCustomizable(false);
		expectedWidgetPageSettings.setCustomizableSectionIds(new String[0]);
		expectedWidgetPageSettings.setLayoutTemplateId("2_columns_ii");

		_testPostSiteSitePage(expectedSitePage, sitePage);
	}

	private void _testPostSiteSitePageWithWidgetPageSettingsWithWidgetPageTemplate()
		throws Exception {

		_testPostSiteSitePage(_getRandomSitePageWithWidgetPageTemplate(false));

		SitePage sitePageWithWidgetPageTemplate =
			_getRandomSitePageWithWidgetPageTemplate(false);

		WidgetPageSettings widgetPageSettings =
			(WidgetPageSettings)
				sitePageWithWidgetPageTemplate.getPageSettings();

		widgetPageSettings.setInheritChanges(false);

		_testPostSiteSitePage(sitePageWithWidgetPageTemplate);

		sitePageWithWidgetPageTemplate =
			_getRandomSitePageWithWidgetPageTemplate(false);

		widgetPageSettings =
			(WidgetPageSettings)
				sitePageWithWidgetPageTemplate.getPageSettings();

		widgetPageSettings.setWidgetPageTemplateReference(
			() -> new ItemExternalReference() {
				{
					setExternalReferenceCode(RandomTestUtil::randomString);
				}
			});

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.headless.admin.site.internal.util.LogUtil",
				LoggerTestUtil.WARN)) {

			_testPostSiteSitePage(sitePageWithWidgetPageTemplate);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			String message = logEntry.getMessage();

			Assert.assertTrue(message.contains("LayoutPageTemplateEntry"));
		}

		_testPostSiteSitePage(_getRandomSitePageWithWidgetPageTemplate(true));
	}

	private void _testPutSiteSitePage(
			boolean privateLayout, ServiceContext serviceContext,
			SitePage.Type type)
		throws Exception {

		SitePage sitePage = sitePageResource.postSiteSitePage(
			testGroup.getExternalReferenceCode(), privateLayout,
			_getRandomSitePage(type));

		_assertSitePage(
			_layoutLocalService.getLayoutByExternalReferenceCode(
				sitePage.getExternalReferenceCode(), testGroup.getGroupId()),
			sitePage);

		Layout layout = LayoutTestUtil.addTypePortletLayout(
			testGroup, privateLayout);

		sitePage = _getRandomSitePage(
			sitePage.getExternalReferenceCode(),
			layout.getExternalReferenceCode(), serviceContext, type,
			sitePage.getUuid());

		SitePage putSitePage = sitePageResource.putSiteSitePage(
			testGroup.getExternalReferenceCode(),
			sitePage.getExternalReferenceCode(), privateLayout, sitePage);

		assertEquals(sitePage, putSitePage);
		assertValid(putSitePage);

		_assertSitePage(
			_layoutLocalService.getLayoutByExternalReferenceCode(
				sitePage.getExternalReferenceCode(), testGroup.getGroupId()),
			putSitePage);

		_assertPutSiteSitePageProblemException(
			_getRandomSitePage(
				sitePage.getExternalReferenceCode(), null, serviceContext,
				_getRandomType(
					ListUtil.filter(
						_types, curType -> !Objects.equals(curType, type))),
				sitePage.getUuid()));
	}

	private void _testPutSiteSitePage(
			ServiceContext serviceContext, SitePage.Type type)
		throws Exception {

		_testPutSiteSitePage(false, serviceContext, type);
	}

	private SitePage _testPutSiteSitePage(
			SitePage expectedSitePage, Group group, SitePage sitePage)
		throws Exception {

		SitePage putSitePage = sitePageResource.putSiteSitePage(
			group.getExternalReferenceCode(),
			sitePage.getExternalReferenceCode(), false, sitePage);

		assertEquals(expectedSitePage, putSitePage);
		assertValid(putSitePage);

		_assertSitePage(
			_layoutLocalService.getLayoutByExternalReferenceCode(
				sitePage.getExternalReferenceCode(), group.getGroupId()),
			putSitePage);

		return putSitePage;
	}

	private void _testPutSiteSitePageWithEmptyLayout(
			ServiceContext serviceContext)
		throws Exception {

		PageElement[] pageElements = PageElementsTestUtil.getPageElements(
			testGroup.getGroupId());

		SitePage contentSitePage = _getSitePageWithPageElements(pageElements);

		_testPutSiteSitePageWithEmptyLayout(
			LayoutConstants.TYPE_CONTENT, serviceContext, contentSitePage,
			(layout, putSitePage) -> _assertPageElements(
				pageElements, putSitePage));

		SitePage linkToURLSitePage = _getRandomSitePage(
			SitePage.Type.LINK_TO_URL_PAGE);

		linkToURLSitePage.setPageSpecifications(
			PageSpecificationsTestUtil.getLinkToURLPageSpecifications(
				linkToURLSitePage.getExternalReferenceCode()));

		_testPutSiteSitePageWithEmptyLayout(
			LayoutConstants.TYPE_URL, serviceContext, linkToURLSitePage,
			(layout, putSitePage) ->
				PageSpecificationsTestUtil.assertPageSpecifications(
					putSitePage.getPageSpecifications(),
					linkToURLSitePage.getPageSpecifications()));

		SitePage pageSetSitePage = _getRandomSitePage(
			SitePage.Type.PAGE_SET_PAGE);

		pageSetSitePage.setPageSpecifications(
			PageSpecificationsTestUtil.getPageSetPageSpecifications(
				pageSetSitePage.getExternalReferenceCode()));

		_testPutSiteSitePageWithEmptyLayout(
			LayoutConstants.TYPE_NODE, serviceContext, pageSetSitePage,
			(layout, putSitePage) ->
				PageSpecificationsTestUtil.assertPageSpecifications(
					putSitePage.getPageSpecifications(),
					pageSetSitePage.getPageSpecifications()));

		SitePage widgetSitePage = _getRandomSitePage(SitePage.Type.WIDGET_PAGE);

		WidgetPageSettings widgetPageSettings =
			(WidgetPageSettings)widgetSitePage.getPageSettings();

		widgetSitePage.setPageSpecifications(
			PageSpecificationsTestUtil.getWidgetPageSpecifications(
				null, widgetPageSettings.getLayoutTemplateId(),
				widgetSitePage.getExternalReferenceCode()));

		_testPutSiteSitePageWithEmptyLayout(
			LayoutConstants.TYPE_PORTLET, serviceContext, widgetSitePage,
			(layout, putSitePage) ->
				PageSpecificationsTestUtil.assertWidgetPageSpecifications(
					widgetSitePage.getPageSpecifications(),
					putSitePage.getPageSpecifications()));
	}

	private void _testPutSiteSitePageWithEmptyLayout(
			String expectedType, ServiceContext serviceContext,
			SitePage sitePage,
			UnsafeBiConsumer<Layout, SitePage, Exception> unsafeBiConsumer)
		throws Exception {

		SitePageResource sitePageResource = _getSitePageResource(
			"pageSpecifications");

		Layout layout;

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			layout = _layoutLocalService.getOrAddEmptyLayout(
				sitePage.getExternalReferenceCode(),
				TestPropsValues.getUserId(), testGroup.getGroupId(), false,
				serviceContext);
		}

		Assert.assertEquals(WorkflowConstants.STATUS_EMPTY, layout.getStatus());

		SitePage putSitePage = sitePageResource.putSiteSitePage(
			testGroup.getExternalReferenceCode(),
			sitePage.getExternalReferenceCode(), false, sitePage);

		assertEquals(sitePage, putSitePage);
		assertValid(putSitePage);

		layout = _layoutLocalService.getLayoutByExternalReferenceCode(
			sitePage.getExternalReferenceCode(), testGroup.getGroupId());

		_assertSitePage(layout, putSitePage);

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, layout.getStatus());

		Assert.assertEquals(expectedType, layout.getType());

		unsafeBiConsumer.accept(layout, putSitePage);
	}

	private void _testPutSiteSitePageWithExportedPageSetSitePageAsFirstPage(
			ServiceContext serviceContext)
		throws Exception {

		LayoutTestUtil.addTypePortletLayout(irrelevantGroup);

		Layout layout = _layoutLocalService.addLayout(
			null, TestPropsValues.getUserId(), irrelevantGroup.getGroupId(),
			false, LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
			RandomTestUtil.randomString(), StringPool.BLANK, StringPool.BLANK,
			LayoutConstants.TYPE_NODE, false, StringPool.BLANK, serviceContext);

		SitePage sitePage = sitePageResource.getSiteSitePage(
			irrelevantGroup.getExternalReferenceCode(),
			layout.getExternalReferenceCode());

		_assertSitePage(layout, sitePage);

		PageSettings pageSettings = sitePage.getPageSettings();

		pageSettings.setPriority(0);

		_assertProblemException(
			"CONFLICT",
			_language.format(
				LocaleUtil.getDefault(), "the-first-page-cannot-be-of-type-x",
				_language.get(
					LocaleUtil.getDefault(),
					"layout.types." + LayoutConstants.TYPE_NODE)),
			() -> sitePageResource.putSiteSitePage(
				testGroup.getExternalReferenceCode(),
				sitePage.getExternalReferenceCode(), layout.isPrivateLayout(),
				sitePage));
	}

	private void _testPutSiteSitePageWithExportedSitePage() throws Exception {
		String languageId = LocaleUtil.toLanguageId(LocaleUtil.getDefault());
		Layout layout = LayoutTestUtil.addTypeContentLayout(irrelevantGroup);
		String url = RandomTestUtil.randomString();

		_addFragmentEntryLinksAndPublishLayout(
			JSONUtil.put(
				"BASIC_COMPONENT-image",
				JSONUtil.putAll(
					JSONUtil.put("image-square", JSONUtil.put(languageId, url)),
					JSONUtil.put(
						"image-square",
						JSONUtil.put(languageId, JSONUtil.put("url", url))))
			).put(
				"BASIC_COMPONENT-slider",
				JSONUtil.putAll(
					JSONUtil.put("01-01-image", JSONUtil.put(languageId, url)),
					JSONUtil.put(
						"02-01-image",
						JSONUtil.put(languageId, JSONUtil.put("url", url))),
					JSONUtil.put(
						"03-01-image",
						JSONUtil.put(languageId, JSONUtil.put("url", url))))
			),
			layout);

		layout = _updateImage(layout);

		SitePageResource sitePageResource = _getSitePageResource(
			"pageSpecifications");

		SitePage sitePage = sitePageResource.getSiteSitePage(
			irrelevantGroup.getExternalReferenceCode(),
			layout.getExternalReferenceCode());

		_assertSitePage(layout, sitePage);

		FragmentImageValue fragmentImageValue = _getDirectFragmentImageValue(
			url);

		_assertFragmentImageValues(
			5, fragmentImageValue,
			sitePageResource.getSiteSitePage(
				irrelevantGroup.getExternalReferenceCode(),
				layout.getExternalReferenceCode()));

		_testPutSiteSitePage(sitePage, testGroup, sitePage);

		SitePage importedSitePage = sitePageResource.getSiteSitePage(
			testGroup.getExternalReferenceCode(),
			layout.getExternalReferenceCode());

		_assertFragmentImageValues(5, fragmentImageValue, importedSitePage);

		_assertContentPageSpecifications(
			importedSitePage.getPageSpecifications(), sitePage);

		sitePage.setPageSettings(
			_getPageSettings(null, SitePage.Type.CONTENT_PAGE));

		_testPutSiteSitePage(sitePage, irrelevantGroup, sitePage);

		_assertFragmentImageValues(
			5, fragmentImageValue,
			sitePageResource.getSiteSitePage(
				irrelevantGroup.getExternalReferenceCode(),
				layout.getExternalReferenceCode()));

		_testPutSiteSitePage(sitePage, testGroup, sitePage);

		_assertFragmentImageValues(
			5, fragmentImageValue,
			sitePageResource.getSiteSitePage(
				testGroup.getExternalReferenceCode(),
				layout.getExternalReferenceCode()));
	}

	private void _testPutSiteSitePageWithExportedSitePageWithLayoutIdFriendlyURL()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(irrelevantGroup);

		ContentLayoutTestUtil.publishLayout(layout.fetchDraftLayout(), layout);

		String layoutIdFriendlyURL = StringPool.SLASH + layout.getLayoutId();

		layout = _updateFriendlyURL(
			HashMapBuilder.put(
				LocaleUtil.SPAIN, layoutIdFriendlyURL
			).put(
				LocaleUtil.US, layoutIdFriendlyURL
			).build(),
			layout);

		Layout testGroupLayout = LayoutTestUtil.addTypePortletLayout(testGroup);

		while (testGroupLayout.getLayoutId() < layout.getLayoutId()) {
			testGroupLayout = LayoutTestUtil.addTypePortletLayout(testGroup);
		}

		testGroupLayout = _layoutLocalService.fetchLayout(
			testGroup.getGroupId(), false, layout.getLayoutId());

		Assert.assertNotNull(testGroupLayout);
		Assert.assertNotEquals(
			layout.getExternalReferenceCode(),
			testGroupLayout.getExternalReferenceCode());

		SitePage sitePage = sitePageResource.getSiteSitePage(
			irrelevantGroup.getExternalReferenceCode(),
			layout.getExternalReferenceCode());

		Assert.assertTrue(MapUtil.isEmpty(sitePage.getFriendlyUrlPath_i18n()));

		SitePage importedSitePage = sitePageResource.putSiteSitePage(
			testGroup.getExternalReferenceCode(),
			layout.getExternalReferenceCode(), false, sitePage);

		Assert.assertTrue(
			MapUtil.isNotEmpty(importedSitePage.getFriendlyUrlPath_i18n()));

		String friendlyURL = _getRandomFriendlyURL();

		Layout importedLayout = _updateFriendlyURL(
			HashMapBuilder.put(
				LocaleUtil.US, friendlyURL
			).build(),
			_layoutLocalService.fetchLayoutByExternalReferenceCode(
				layout.getExternalReferenceCode(), testGroup.getGroupId()));

		importedSitePage = sitePageResource.putSiteSitePage(
			testGroup.getExternalReferenceCode(),
			importedLayout.getExternalReferenceCode(), false, sitePage);

		Map<String, String> friendlyUrlPathI18n =
			importedSitePage.getFriendlyUrlPath_i18n();

		Assert.assertEquals(
			friendlyURL,
			friendlyUrlPathI18n.get(
				LocaleUtil.toBCP47LanguageId(LocaleUtil.US)));

		layout = _updateFriendlyURL(
			HashMapBuilder.put(
				LocaleUtil.SPAIN, _getRandomFriendlyURL()
			).put(
				LocaleUtil.US, _getRandomFriendlyURL()
			).build(),
			layout);

		sitePage = sitePageResource.getSiteSitePage(
			irrelevantGroup.getExternalReferenceCode(),
			layout.getExternalReferenceCode());

		_assertSitePage(layout, sitePage);

		importedSitePage = sitePageResource.putSiteSitePage(
			testGroup.getExternalReferenceCode(),
			importedLayout.getExternalReferenceCode(), false, sitePage);

		Assert.assertTrue(
			equals(
				(Map)sitePage.getFriendlyUrlPath_i18n(),
				(Map)importedSitePage.getFriendlyUrlPath_i18n()));

		_assertSitePage(
			_layoutLocalService.getLayoutByExternalReferenceCode(
				importedSitePage.getExternalReferenceCode(),
				testGroup.getGroupId()),
			importedSitePage);
	}

	private void _testPutSiteSitePageWithFormFragmentPageElements()
		throws Exception {

		List<ObjectField> objectFields = _getObjectFields();

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				objectFields, false);

		InfoItemFormProvider<?> infoItemFormProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormProvider.class, objectDefinition.getClassName());

		InfoForm infoForm = infoItemFormProvider.getInfoForm(
			StringPool.BLANK, irrelevantGroup.getGroupId());

		Layout layout = LayoutTestUtil.addTypeContentLayout(irrelevantGroup);

		_addFormAndPublishLayout(
			objectDefinition.getClassName(),
			ListUtil.filter(
				infoForm.getAllInfoFields(),
				infoField -> infoField.isEditable()),
			layout);

		int expectedFragmentEntryLinkCount = objectFields.size() + 2;

		InfoForm expectedInfoForm = infoItemFormProvider.getInfoForm(
			StringPool.BLANK, testGroup.getGroupId());

		_testPutSiteSitePageWithFormFragmentPageElements(
			expectedFragmentEntryLinkCount, expectedInfoForm, infoForm, layout);

		_addFormAndPublishLayout(
			objectDefinition.getClassName(),
			ListUtil.filter(
				infoForm.getAllInfoFields(),
				infoField -> infoField.isEditable()),
			layout);

		_testPutSiteSitePageWithFormFragmentPageElements(
			2 * expectedFragmentEntryLinkCount, expectedInfoForm, infoForm,
			layout);
	}

	private void _testPutSiteSitePageWithFormFragmentPageElements(
			int expectedFragmentEntryLinkCount, InfoForm expectedInfoForm,
			InfoForm infoForm, Layout layout)
		throws Exception {

		List<FragmentEntryLink> fragmentEntryLinks =
			_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
				layout.getGroupId(), layout.getPlid());

		Assert.assertEquals(
			fragmentEntryLinks.toString(), expectedFragmentEntryLinkCount,
			fragmentEntryLinks.size());

		SitePageResource sitePageResource = _getSitePageResource(
			"pageSpecifications");

		SitePage sitePage = sitePageResource.getSiteSitePage(
			irrelevantGroup.getExternalReferenceCode(),
			layout.getExternalReferenceCode());

		_assertSitePage(layout, sitePage);
		_testPutSiteSitePage(sitePage, testGroup, sitePage);

		_assertInputFragmentEntryLinks(
			fragmentEntryLinks, infoForm, expectedInfoForm,
			_layoutLocalService.getLayoutByExternalReferenceCode(
				layout.getExternalReferenceCode(), testGroup.getGroupId()));
	}

	private void _testPutSiteSitePageWithMissingTaxonomyCategories()
		throws Exception {

		TaxonomyCategoryBrief taxonomyCategoryBrief1 =
			_getTaxonomyCategoryBrief(
				RandomTestUtil.randomString(),
				_groupLocalService.getGroup(testCompany.getGroupId()), null);
		TaxonomyCategoryBrief taxonomyCategoryBrief2 =
			_getTaxonomyCategoryBrief(
				RandomTestUtil.randomString(), null,
				RandomTestUtil.randomString());

		SitePage randomSitePage = _getRandomSitePage(
			RandomTestUtil.randomString(), null,
			ServiceContextTestUtil.getServiceContext(
				testGroup, TestPropsValues.getUserId()),
			new TaxonomyCategoryBrief[] {
				taxonomyCategoryBrief1, taxonomyCategoryBrief2
			},
			SitePage.Type.WIDGET_PAGE, RandomTestUtil.randomString());

		SitePage putSitePage = sitePageResource.putSiteSitePage(
			testGroup.getExternalReferenceCode(),
			randomSitePage.getExternalReferenceCode(), false, randomSitePage);

		Assert.assertTrue(
			Objects.deepEquals(
				randomSitePage.getTaxonomyCategoryBriefs(),
				putSitePage.getTaxonomyCategoryBriefs()));

		_assertTaxonomyCategoryBrief(
			testCompany.getGroupId(), taxonomyCategoryBrief1);
		_assertTaxonomyCategoryBrief(
			testGroup.getGroupId(), taxonomyCategoryBrief2);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(testGroup.getGroupId());

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), testGroup.getGroupId(),
				RandomTestUtil.randomString(), serviceContext);

		AssetCategory assetCategory = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), testGroup.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			serviceContext);

		TaxonomyCategoryBrief taxonomyCategoryBrief3 =
			_getTaxonomyCategoryBrief(
				assetCategory.getExternalReferenceCode(), null,
				RandomTestUtil.randomString());

		randomSitePage.setTaxonomyCategoryBriefs(
			new TaxonomyCategoryBrief[] {taxonomyCategoryBrief3});

		putSitePage = sitePageResource.putSiteSitePage(
			testGroup.getExternalReferenceCode(),
			randomSitePage.getExternalReferenceCode(), false, randomSitePage);

		TaxonomyCategoryBrief[] putTaxonomyCategoryBriefs =
			putSitePage.getTaxonomyCategoryBriefs();

		Assert.assertEquals(
			Arrays.toString(putTaxonomyCategoryBriefs), 3,
			putTaxonomyCategoryBriefs.length);

		TaxonomyCategoryBrief putTaxonomyCategoryBrief =
			putTaxonomyCategoryBriefs[0];

		Assert.assertNotEquals(
			taxonomyCategoryBrief3.getParentTaxonomyCategory(),
			putTaxonomyCategoryBrief.getParentTaxonomyCategory());
		Assert.assertNotEquals(
			taxonomyCategoryBrief3.getParentTaxonomyVocabulary(),
			putTaxonomyCategoryBrief.getParentTaxonomyVocabulary());
	}

	private void _testPutSiteSitePageWithPageElements() throws Exception {
		PageElement[] pageElements = PageElementsTestUtil.getPageElements(
			testGroup.getGroupId());

		SitePage sitePage = _getSitePageWithPageElements(pageElements);

		SitePageResource sitePageResource = _getSitePageResource(
			"pageSpecifications");

		SitePage postSitePage = sitePageResource.postSiteSitePage(
			testGroup.getExternalReferenceCode(), false, sitePage);

		sitePage.setDateModified(new Date());

		_assertPageElements(
			pageElements,
			sitePageResource.putSiteSitePage(
				testGroup.getExternalReferenceCode(),
				postSitePage.getExternalReferenceCode(), false, sitePage));
	}

	private void _testPutSiteSitePageWithPageExperiences() throws Exception {
		SitePageResource sitePageResource = _getSitePageResource(
			"pageSpecifications");

		SitePage postSitePage = sitePageResource.postSiteSitePage(
			testGroup.getExternalReferenceCode(), false,
			_getRandomSitePage(SitePage.Type.CONTENT_PAGE));

		ContentPageSpecification contentPageSpecification =
			(ContentPageSpecification)postSitePage.getPageSpecifications()[0];

		PageExperience defaultPageExperience =
			PageExperiencesTestUtil.getDefaultPageExperience(
				contentPageSpecification.getPageExperiences());

		SitePage putSitePage = _testPutSiteSitePageWithPageExperiences(
			ArrayUtil.append(
				PageExperiencesTestUtil.getPageExperiences(
					testCompany.getGroupId(), testGroup.getGroupId(),
					contentPageSpecification.getExternalReferenceCode()),
				defaultPageExperience),
			postSitePage, sitePageResource);

		putSitePage = _testPutSiteSitePageWithPageExperiences(
			ArrayUtil.append(
				PageExperiencesTestUtil.getPageExperiences(
					testCompany.getGroupId(), testGroup.getGroupId(),
					contentPageSpecification.getExternalReferenceCode()),
				defaultPageExperience),
			putSitePage, sitePageResource);

		contentPageSpecification =
			(ContentPageSpecification)putSitePage.getPageSpecifications()[0];

		PageExperience[] pageExperiences =
			contentPageSpecification.getPageExperiences();

		for (int i = 0; i < pageExperiences.length; i++) {
			PageExperience pageExperience = pageExperiences[i];

			if (!Objects.equals(
					pageExperience.getKey(),
					SegmentsExperienceConstants.KEY_DEFAULT)) {

				pageExperience.setPriority(i + 1);
			}
		}

		Comparator<PageExperience> comparator = Comparator.comparingInt(
			PageExperience::getPriority);

		Arrays.sort(pageExperiences, comparator.reversed());

		_testPutSiteSitePageWithPageExperiences(
			pageExperiences, putSitePage, sitePageResource);

		PageExperience[] putPageExperiences = ArrayUtil.append(
			PageExperiencesTestUtil.getPageExperiences(
				testCompany.getGroupId(), testGroup.getGroupId(),
				contentPageSpecification.getExternalReferenceCode()),
			defaultPageExperience);

		Arrays.sort(putPageExperiences, comparator);

		putPageExperiences = TransformUtil.transform(
			putPageExperiences,
			pageExperience -> {
				pageExperience.setPriority(() -> null);

				return pageExperience;
			},
			PageExperience.class);

		PageExperience[] expectedPageExperiences = Arrays.copyOf(
			putPageExperiences, putPageExperiences.length);

		AtomicInteger priority = new AtomicInteger();

		expectedPageExperiences = TransformUtil.transform(
			expectedPageExperiences,
			pageExperience -> {
				pageExperience.setPriority(priority::getAndDecrement);

				return pageExperience;
			},
			PageExperience.class);

		_testPutSiteSitePageWithPageExperiences(
			expectedPageExperiences, putPageExperiences, putSitePage,
			sitePageResource);
	}

	private SitePage _testPutSiteSitePageWithPageExperiences(
			PageExperience[] expectedPageExperiences,
			PageExperience[] putPageExperiences, SitePage sitePage,
			SitePageResource sitePageResource)
		throws Exception {

		PageSpecification[] pageSpecifications =
			sitePage.getPageSpecifications();

		ContentPageSpecification contentPageSpecification =
			(ContentPageSpecification)pageSpecifications[0];

		contentPageSpecification.setPageExperiences(putPageExperiences);

		SitePage putSitePage = sitePageResource.putSiteSitePage(
			testGroup.getExternalReferenceCode(),
			sitePage.getExternalReferenceCode(), false, sitePage);

		pageSpecifications = putSitePage.getPageSpecifications();

		contentPageSpecification =
			(ContentPageSpecification)pageSpecifications[0];

		Assert.assertArrayEquals(
			expectedPageExperiences,
			contentPageSpecification.getPageExperiences());

		return putSitePage;
	}

	private SitePage _testPutSiteSitePageWithPageExperiences(
			PageExperience[] pageExperiences, SitePage sitePage,
			SitePageResource sitePageResource)
		throws Exception {

		return _testPutSiteSitePageWithPageExperiences(
			pageExperiences, pageExperiences, sitePage, sitePageResource);
	}

	private void _testPutSiteSitePageWithPageSpecifications() throws Exception {
		_testPutSiteSitePageWithPageSpecifications(
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT,
			PageSpecification.Status.APPROVED);
		_testPutSiteSitePageWithPageSpecifications(
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT,
			PageSpecification.Status.DRAFT);
		_testPutSiteSitePageWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED);
		_testPutSiteSitePageWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.DRAFT,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT);
		_testPutSiteSitePageWithPageSpecifications(
			PageSpecificationsTestUtil::getEmbeddedPageSpecifications,
			SitePage.Type.EMBEDDED_PAGE);
		_testPutSiteSitePageWithPageSpecifications(
			PageSpecificationsTestUtil::getLinkToPagePageSpecifications,
			SitePage.Type.LINK_TO_PAGE_PAGE);
		_testPutSiteSitePageWithPageSpecifications(
			PageSpecificationsTestUtil::getLinkToURLPageSpecifications,
			SitePage.Type.LINK_TO_URL_PAGE);
		_testPutSiteSitePageWithPageSpecifications(
			PageSpecificationsTestUtil::getPageSetPageSpecifications,
			SitePage.Type.PAGE_SET_PAGE);
		_testPutSiteSitePageWithPageSpecificationsWithCustomFields();
		_testPutSiteSitePageWithPageSpecificationsWithWidgetPageSpecification(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());
		_testPutSiteSitePageWithPageSpecificationsWithWidgetPageSpecification(
			RandomTestUtil.randomString(), "1_2_1_columns_i");
		_testPutSiteSitePageWithPageSpecificationsWithWidgetPageSpecification(
			"1_2_1_columns_i", RandomTestUtil.randomString());
		_testPutSiteSitePageWithPageSpecificationsWithWidgetPageSpecification(
			"1_2_1_columns_i", "1_column");
		_testPutSiteSitePageWithPageSpecificationsWithWidgetPageSpecification(
			"1_column", "1_2_1_columns_i");
		_testPutSiteSitePageWithPageSpecificationsWithWidgetPageSpecification(
			"1_column", "2_columns_ii");
	}

	private void _testPutSiteSitePageWithPageSpecifications(
			PageSpecification.Status newDraftLayoutStatus,
			PageSpecification.Status newPublishedLayoutStatus,
			PageSpecification.Status oldDraftLayoutStatus,
			PageSpecification.Status oldPublishedLayoutStatus)
		throws Exception {

		SitePage sitePage = _getRandomSitePage(SitePage.Type.CONTENT_PAGE);

		ContentPageSpecification draftContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				null, testGroup.getGroupId(), oldDraftLayoutStatus);

		ContentPageSpecification publishedContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				draftContentPageSpecification.getExternalReferenceCode(),
				testGroup.getGroupId(), oldPublishedLayoutStatus);

		publishedContentPageSpecification.setExternalReferenceCode(
			sitePage.getExternalReferenceCode());

		sitePage.setPageSpecifications(
			() -> new PageSpecification[] {
				publishedContentPageSpecification, draftContentPageSpecification
			});

		SitePageResource sitePageResource = _getSitePageResource(
			"pageSpecifications");

		_assertPageSpecifications(
			draftContentPageSpecification, publishedContentPageSpecification,
			sitePageResource.putSiteSitePage(
				testGroup.getExternalReferenceCode(),
				sitePage.getExternalReferenceCode(), false, sitePage));

		draftContentPageSpecification.setStatus(newDraftLayoutStatus);
		publishedContentPageSpecification.setStatus(newPublishedLayoutStatus);

		_assertPageSpecifications(
			draftContentPageSpecification, publishedContentPageSpecification,
			sitePageResource.putSiteSitePage(
				testGroup.getExternalReferenceCode(),
				sitePage.getExternalReferenceCode(), false, sitePage));
	}

	private void _testPutSiteSitePageWithPageSpecifications(
			UnsafeFunction<String, PageSpecification[], Exception>
				pageSpecificationsUnsafeFunction,
			SitePage.Type sitePageType)
		throws Exception {

		SitePageResource sitePageResource = _getSitePageResource(
			"pageSpecifications");

		SitePage randomSitePage = _getRandomSitePage(sitePageType);

		randomSitePage.setPageSpecifications(
			pageSpecificationsUnsafeFunction.apply(
				randomSitePage.getExternalReferenceCode()));

		SitePage sitePage = sitePageResource.postSiteSitePage(
			testGroup.getExternalReferenceCode(), false, randomSitePage);

		sitePage.setPageSpecifications(
			pageSpecificationsUnsafeFunction.apply(
				randomSitePage.getExternalReferenceCode()));

		SitePage putSitePage = sitePageResource.putSiteSitePage(
			testGroup.getExternalReferenceCode(),
			sitePage.getExternalReferenceCode(), false, sitePage);

		PageSpecificationsTestUtil.assertPageSpecifications(
			putSitePage.getPageSpecifications(),
			sitePage.getPageSpecifications());

		sitePageResource.deleteSiteSitePage(
			testGroup.getExternalReferenceCode(),
			putSitePage.getExternalReferenceCode());
	}

	private void _testPutSiteSitePageWithPageSpecificationsWithCustomFields()
		throws Exception {

		try (PageSpecificationsTestUtil.ExpandoTableAutocloseable
				expandoTableAutoCloseable =
					PageSpecificationsTestUtil.getExpandoTableAutoCloseable()) {

			_testPutSiteSitePageWithPageSpecificationsWithCustomFields(
				SitePage.Type.CONTENT_PAGE);
			_testPutSiteSitePageWithPageSpecificationsWithCustomFields(
				SitePage.Type.WIDGET_PAGE);
		}
	}

	private void _testPutSiteSitePageWithPageSpecificationsWithCustomFields(
			SitePage.Type type)
		throws Exception {

		SitePage postSitePage =
			_postSiteSitePageWithPageSpecificationsWithCustomFields(type);

		SitePageResource sitePageResource = _getSitePageResource(
			"pageSpecifications");

		SitePage putSitePage = postSitePage;

		putSitePage.setPageSpecifications(
			() -> TransformUtil.transform(
				putSitePage.getPageSpecifications(),
				pageSpecification -> {
					pageSpecification.setCustomFields(
						PageSpecificationsTestUtil.getCustomFields());

					return pageSpecification;
				},
				PageSpecification.class));

		SitePage updateSitePage = sitePageResource.putSiteSitePage(
			testGroup.getExternalReferenceCode(),
			postSitePage.getExternalReferenceCode(), false, putSitePage);

		PageSpecificationsTestUtil.assertCustomFields(
			TransformUtil.transform(
				putSitePage.getPageSpecifications(),
				pageSpecification -> pageSpecification.getCustomFields(),
				CustomField[].class),
			testGroup.getGroupId(), updateSitePage.getPageSpecifications());
	}

	private void
			_testPutSiteSitePageWithPageSpecificationsWithWidgetPageSpecification(
				String layoutTemplateId1, String layoutTemplateId2)
		throws Exception {

		SitePageResource sitePageResource = _getSitePageResource(
			"pageSpecifications");

		SitePage randomSitePage = _getRandomSitePage(SitePage.Type.WIDGET_PAGE);

		WidgetPageSettings postWidgetPageSettings =
			(WidgetPageSettings)randomSitePage.getPageSettings();

		postWidgetPageSettings.setLayoutTemplateId(layoutTemplateId1);

		randomSitePage.setPageSpecifications(
			PageSpecificationsTestUtil.getWidgetPageSpecifications(
				null, layoutTemplateId1,
				randomSitePage.getExternalReferenceCode()));

		SitePage sitePage = sitePageResource.postSiteSitePage(
			testGroup.getExternalReferenceCode(), false, randomSitePage);

		WidgetPageSettings putWidgetPageSettings =
			(WidgetPageSettings)sitePage.getPageSettings();

		putWidgetPageSettings.setLayoutTemplateId(layoutTemplateId2);

		sitePage.setPageSpecifications(
			() -> PageSpecificationsTestUtil.getWidgetPageSpecifications(
				null, layoutTemplateId2, sitePage.getExternalReferenceCode()));

		SitePage putSitePage = sitePageResource.putSiteSitePage(
			testGroup.getExternalReferenceCode(),
			sitePage.getExternalReferenceCode(), false, sitePage);

		PageSpecificationsTestUtil.assertWidgetPageSpecifications(
			sitePage.getPageSpecifications(),
			putSitePage.getPageSpecifications());
	}

	private void _testPutSiteSitePageWithPriority() throws Exception {
		_testUpdateSiteSitePageWithPriority(
			(parentSitePageExternalReferenceCode, priority, sitePage) -> {
				int expectedPriority = _getExpectedPriority(
					sitePage.getParentSitePageExternalReferenceCode(),
					parentSitePageExternalReferenceCode, priority);

				sitePage.setParentSitePageExternalReferenceCode(
					parentSitePageExternalReferenceCode);

				PageSettings pageSettings = sitePage.getPageSettings();

				pageSettings.setPriority(priority);

				SitePage putSitePage = sitePageResource.putSiteSitePage(
					testGroup.getExternalReferenceCode(),
					sitePage.getExternalReferenceCode(), false, sitePage);

				PageSettings putPageSettings = putSitePage.getPageSettings();

				Assert.assertEquals(
					expectedPriority, (int)putPageSettings.getPriority());

				assertEquals(sitePage, putSitePage);
				assertValid(putSitePage);

				_assertSitePage(
					_layoutLocalService.getLayoutByExternalReferenceCode(
						sitePage.getExternalReferenceCode(),
						testGroup.getGroupId()),
					putSitePage);
			});
	}

	private void _testPutSiteSitePageWithWidgetPageSettings() throws Exception {
		SitePage randomSitePage = _getRandomSitePage(SitePage.Type.WIDGET_PAGE);

		SitePage sitePage = _testPutSiteSitePage(
			randomSitePage, testGroup, randomSitePage);

		WidgetPageSettings widgetPageSettings =
			(WidgetPageSettings)sitePage.getPageSettings();

		widgetPageSettings.setCustomizable(true);
		widgetPageSettings.setCustomizableSectionIds(
			new String[] {"column-1", "column-3"});
		widgetPageSettings.setLayoutTemplateId("1_2_columns_i");

		sitePage = _testPutSiteSitePage(sitePage, testGroup, sitePage);

		widgetPageSettings = (WidgetPageSettings)sitePage.getPageSettings();

		widgetPageSettings.setCustomizableSectionIds(new String[] {"column-2"});

		SitePage expectedSitePage = sitePage.clone();

		WidgetPageSettings expectedWidgetPageSettings =
			(WidgetPageSettings)expectedSitePage.getPageSettings();

		widgetPageSettings.setLayoutTemplateId((String)null);

		expectedWidgetPageSettings.setLayoutTemplateId("2_columns_ii");

		sitePage = _testPutSiteSitePage(expectedSitePage, testGroup, sitePage);

		widgetPageSettings = (WidgetPageSettings)sitePage.getPageSettings();

		widgetPageSettings.setCustomizable(false);

		expectedSitePage = sitePage.clone();

		expectedWidgetPageSettings =
			(WidgetPageSettings)expectedSitePage.getPageSettings();

		widgetPageSettings.setCustomizableSectionIds((String[])null);
		widgetPageSettings.setLayoutTemplateId((String)null);

		expectedWidgetPageSettings.setCustomizableSectionIds(new String[0]);
		expectedWidgetPageSettings.setLayoutTemplateId("2_columns_ii");

		_testPutSiteSitePage(expectedSitePage, testGroup, sitePage);
	}

	private void _testPutSiteSitePageWithWidgetPageSettingsWithWidgetPageTemplate()
		throws Exception {

		SitePage sitePageWithWidgetPageTemplate =
			_getRandomSitePageWithWidgetPageTemplate(false);

		SitePage sitePage = _testPutSiteSitePage(
			sitePageWithWidgetPageTemplate, testGroup,
			sitePageWithWidgetPageTemplate);

		WidgetPageSettings widgetPageSettings =
			(WidgetPageSettings)sitePage.getPageSettings();

		widgetPageSettings.setInheritChanges(false);

		_testPutSiteSitePage(sitePage, testGroup, sitePage);
	}

	private void _testUpdateSiteSitePageWithPriority(
			UnsafeTriConsumer<String, Integer, SitePage, Exception>
				unsafeTriConsumer)
		throws Exception {

		Page<SitePage> page = sitePageResource.getSiteSitePagesPage(
			testGroup.getExternalReferenceCode(), false, null, null, null,
			Pagination.of(0, 0), null);

		for (SitePage sitePage : page.getItems()) {
			if (Validator.isNotNull(
					sitePage.getParentSitePageExternalReferenceCode())) {

				continue;
			}

			sitePageResource.deleteSiteSitePage(
				testGroup.getExternalReferenceCode(),
				sitePage.getExternalReferenceCode());
		}

		_priorities.clear();

		SitePage sitePage1 = testPostSiteSitePage_addSitePage(randomSitePage());
		SitePage sitePage2 = testPostSiteSitePage_addSitePage(randomSitePage());
		SitePage sitePage3 = testPostSiteSitePage_addSitePage(randomSitePage());
		SitePage sitePage4 = testPostSiteSitePage_addSitePage(randomSitePage());
		SitePage sitePage5 = testPostSiteSitePage_addSitePage(randomSitePage());

		_assertParentAndPriority(null, 0, sitePage1);
		_assertParentAndPriority(null, 1, sitePage2);
		_assertParentAndPriority(null, 2, sitePage3);
		_assertParentAndPriority(null, 3, sitePage4);
		_assertParentAndPriority(null, 4, sitePage5);

		unsafeTriConsumer.accept(null, 1, sitePage4);

		_assertParentAndPriority(null, 0, sitePage1);
		_assertParentAndPriority(null, 1, sitePage4);
		_assertParentAndPriority(null, 2, sitePage2);
		_assertParentAndPriority(null, 3, sitePage3);
		_assertParentAndPriority(null, 4, sitePage5);

		unsafeTriConsumer.accept(null, 5, sitePage5);

		_assertParentAndPriority(null, 0, sitePage1);
		_assertParentAndPriority(null, 1, sitePage4);
		_assertParentAndPriority(null, 2, sitePage2);
		_assertParentAndPriority(null, 3, sitePage3);
		_assertParentAndPriority(null, 4, sitePage5);

		unsafeTriConsumer.accept(null, null, sitePage3);

		_assertParentAndPriority(null, 0, sitePage1);
		_assertParentAndPriority(null, 1, sitePage4);
		_assertParentAndPriority(null, 2, sitePage2);
		_assertParentAndPriority(null, 3, sitePage5);
		_assertParentAndPriority(null, 4, sitePage3);

		unsafeTriConsumer.accept(
			sitePage1.getExternalReferenceCode(), 2, sitePage2);

		_assertParentAndPriority(null, 0, sitePage1);
		_assertParentAndPriority(null, 1, sitePage4);
		_assertParentAndPriority(null, 3, sitePage5);
		_assertParentAndPriority(null, 4, sitePage3);
		_assertParentAndPriority(
			sitePage1.getExternalReferenceCode(), 0, sitePage2);

		unsafeTriConsumer.accept(
			sitePage1.getExternalReferenceCode(), 1, sitePage4);

		_assertParentAndPriority(null, 0, sitePage1);
		_assertParentAndPriority(null, 3, sitePage5);
		_assertParentAndPriority(null, 4, sitePage3);
		_assertParentAndPriority(
			sitePage1.getExternalReferenceCode(), 0, sitePage2);
		_assertParentAndPriority(
			sitePage1.getExternalReferenceCode(), 1, sitePage4);

		unsafeTriConsumer.accept(
			sitePage1.getExternalReferenceCode(), 3, sitePage3);

		_assertParentAndPriority(null, 0, sitePage1);
		_assertParentAndPriority(null, 3, sitePage5);
		_assertParentAndPriority(
			sitePage1.getExternalReferenceCode(), 0, sitePage2);
		_assertParentAndPriority(
			sitePage1.getExternalReferenceCode(), 1, sitePage4);
		_assertParentAndPriority(
			sitePage1.getExternalReferenceCode(), 2, sitePage3);

		unsafeTriConsumer.accept(
			sitePage1.getExternalReferenceCode(), 0, sitePage3);

		_assertParentAndPriority(null, 0, sitePage1);
		_assertParentAndPriority(null, 3, sitePage5);
		_assertParentAndPriority(
			sitePage1.getExternalReferenceCode(), 0, sitePage3);
		_assertParentAndPriority(
			sitePage1.getExternalReferenceCode(), 1, sitePage2);
		_assertParentAndPriority(
			sitePage1.getExternalReferenceCode(), 2, sitePage4);
	}

	private Layout _updateFriendlyURL(
			Map<Locale, String> friendlyURLMap, Layout layout)
		throws Exception {

		layout = LayoutTestUtil.updateFriendlyURL(layout, friendlyURLMap);

		for (Map.Entry<Locale, String> entry : friendlyURLMap.entrySet()) {
			Assert.assertEquals(
				entry.getValue(), layout.getFriendlyURL(entry.getKey()));
		}

		return layout;
	}

	private Layout _updateImage(Layout layout) throws Exception {
		Layout draftLayout = layout.fetchDraftLayout();

		_layoutLocalService.updateIconImage(
			draftLayout.getPlid(),
			FileUtil.getBytes(getClass(), "dependencies/liferay.jpg"));

		return _layoutLocalService.updateIconImage(
			layout.getPlid(),
			FileUtil.getBytes(getClass(), "dependencies/liferay.jpg"));
	}

	private static final List<SitePage.Type> _types = Arrays.asList(
		SitePage.Type.CONTENT_PAGE, SitePage.Type.WIDGET_PAGE);

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Inject
	private DefaultInputFragmentEntryConfigurationProvider
		_defaultInputFragmentEntryConfigurationProvider;

	@Inject
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

	@Inject
	private FragmentEntryInputTemplateNodeContextHelper
		_fragmentEntryInputTemplateNodeContextHelper;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private Language _language;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutStructureProvider _layoutStructureProvider;

	@Inject
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	@Inject
	private ObjectFieldSettingLocalService _objectFieldSettingLocalService;

	@Inject
	private Portal _portal;

	private final Map<String, Integer> _priorities = new HashMap<>();

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}