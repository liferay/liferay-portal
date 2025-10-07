/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.test.util.DLTestUtil;
import com.liferay.headless.admin.site.client.custom.field.CustomField;
import com.liferay.headless.admin.site.client.dto.v1_0.ContentPageSettings;
import com.liferay.headless.admin.site.client.dto.v1_0.ContentPageSpecification;
import com.liferay.headless.admin.site.client.dto.v1_0.CustomMetaTag;
import com.liferay.headless.admin.site.client.dto.v1_0.FavIcon;
import com.liferay.headless.admin.site.client.dto.v1_0.FriendlyUrlHistory;
import com.liferay.headless.admin.site.client.dto.v1_0.ItemExternalReference;
import com.liferay.headless.admin.site.client.dto.v1_0.NavigationSettings;
import com.liferay.headless.admin.site.client.dto.v1_0.OpenGraphSettings;
import com.liferay.headless.admin.site.client.dto.v1_0.PageSettings;
import com.liferay.headless.admin.site.client.dto.v1_0.PageSpecification;
import com.liferay.headless.admin.site.client.dto.v1_0.SEOSettings;
import com.liferay.headless.admin.site.client.dto.v1_0.Scope;
import com.liferay.headless.admin.site.client.dto.v1_0.SitePage;
import com.liferay.headless.admin.site.client.dto.v1_0.SitemapSettings;
import com.liferay.headless.admin.site.client.dto.v1_0.WidgetPageSettings;
import com.liferay.headless.admin.site.client.dto.v1_0.WidgetPageSpecification;
import com.liferay.headless.admin.site.client.pagination.Page;
import com.liferay.headless.admin.site.client.pagination.Pagination;
import com.liferay.headless.admin.site.client.problem.Problem;
import com.liferay.headless.admin.site.client.resource.v1_0.SitePageResource;
import com.liferay.headless.admin.site.resource.v1_0.test.util.AssetTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.LayoutPageTemplateEntryTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.LayoutUtilityPageEntryTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.PageSpecificationsTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.SettingsTestUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
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
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

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
public class SitePageResourceTest extends BaseSitePageResourceTestCase {

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
	}

	@Override
	@Test
	public void testGetSiteSitePagesPage() throws Exception {
		super.testGetSiteSitePagesPage();

		String siteExternalReferenceCode =
			testGetSiteSitePagesPage_getSiteExternalReferenceCode();

		SitePage sitePage = sitePageResource.postSiteSitePage(
			siteExternalReferenceCode,
			_getRandomSitePage(
				testGroup.getExternalReferenceCode(), null,
				ServiceContextTestUtil.getServiceContext(
					testGroup, TestPropsValues.getUserId()),
				SitePage.Type.CONTENT_PAGE,
				StringUtil.toLowerCase(RandomTestUtil.randomString())));

		Page<SitePage> page = sitePageResource.getSiteSitePagesPage(
			siteExternalReferenceCode, null, null,
			"externalReferenceCode eq '" + sitePage.getExternalReferenceCode() +
				"'",
			Pagination.of(1, 10), null);

		Assert.assertEquals(1, page.getTotalCount());

		List<SitePage> pages = new ArrayList<>(page.getItems());

		Assert.assertEquals(sitePage, pages.get(0));
	}

	@Override
	@Test
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

	@Override
	@Test
	public void testPostSiteSitePage() throws Exception {
		super.testPostSiteSitePage();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup, TestPropsValues.getUserId());

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

	@Override
	@Test
	public void testPutSiteSitePage() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId());

		_testPutSiteSitePage(serviceContext, SitePage.Type.CONTENT_PAGE);
		_testPutSiteSitePage(serviceContext, SitePage.Type.WIDGET_PAGE);

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
		super.equals(sitePage1, sitePage2);

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
			"name_i18n", "taxonomyCategoryItemExternalReferences", "type",
			"uuid"
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
	protected SitePage testGetSiteSitePagesPage_addSitePage(
			String siteExternalReferenceCode, SitePage sitePage)
		throws Exception {

		return sitePageResource.postSiteSitePage(
			siteExternalReferenceCode, sitePage);
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
	protected SitePage testPostSiteSitePage_addSitePage(SitePage sitePage)
		throws Exception {

		return sitePageResource.postSiteSitePage(
			testGroup.getExternalReferenceCode(), sitePage);
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
			Collections.emptyMap(), 0L, serviceContext);
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

	private void _assertMapEquals(
		Map<String, String> expectedMap, Map<String, String> map) {

		Assert.assertEquals(
			MapUtil.toString(map), expectedMap.size(), map.size());

		for (Map.Entry<String, String> entry : expectedMap.entrySet()) {
			Assert.assertEquals(entry.getValue(), map.get(entry.getKey()));
		}
	}

	private void _assertNestedFields(SitePage sitePage) throws Exception {
		FriendlyUrlHistory friendlyUrlHistory =
			sitePage.getFriendlyUrlHistory();

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			GetterUtil.getString(friendlyUrlHistory.getFriendlyUrlPath_i18n()));

		Layout layout = _layoutLocalService.getLayoutByExternalReferenceCode(
			sitePage.getExternalReferenceCode(), testGroup.getGroupId());

		Map<Locale, String> friendlyURLMap = new HashMap<>();

		if (layout.isPublished()) {
			friendlyURLMap = layout.getFriendlyURLMap();
		}

		Assert.assertEquals(
			jsonObject.toString(), friendlyURLMap.size(), jsonObject.length());

		for (Map.Entry<Locale, String> entry : friendlyURLMap.entrySet()) {
			String key = LocaleUtil.toBCP47LanguageId(entry.getKey());

			JSONArray jsonArray = jsonObject.getJSONArray(key);

			Assert.assertEquals(jsonArray.toString(), 1, jsonArray.length());
			Assert.assertEquals(
				jsonArray.toString(), entry.getValue(), jsonArray.getString(0));
		}

		PageSpecificationsTestUtil.assertPageSpecifications(
			layout, sitePage.getPageSpecifications());
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
				sitePage.getExternalReferenceCode(), sitePage));
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
			String expectedTitle, UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		try {
			unsafeRunnable.run();
			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertEquals(expectedTitle, problem.getTitle());
		}
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
				sitePage.getExternalReferenceCode(), sitePage));
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
		else {
			_assertWidgetSitePage(layout, sitePage);
		}
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

	private PageSettings _getPageSettings(SitePage.Type type) throws Exception {
		PageSettings pageSettings = null;

		if (type == SitePage.Type.CONTENT_PAGE) {
			pageSettings = new ContentPageSettings() {
				{
					setType(Type.CONTENT_PAGE_SETTINGS);
				}
			};
		}
		else {
			pageSettings = new WidgetPageSettings() {
				{
					setCustomizable(false);
					setCustomizableSectionIds(new String[0]);
					setLayoutTemplateId("1_column");
					setType(Type.WIDGET_PAGE_SETTINGS);
				}
			};
		}

		pageSettings.setCustomMetaTags(
			() -> new CustomMetaTag[] {
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
			});
		pageSettings.setNavigationSettings(
			() -> new NavigationSettings() {
				{
					setTarget(RandomTestUtil::randomString);
					setTargetType(
						() -> RandomTestUtil.randomEnum(
							NavigationSettings.TargetType.class));
				}
			});
		pageSettings.setOpenGraphSettings(
			() -> new OpenGraphSettings() {
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

										DLFolder dlFolder =
											DLTestUtil.addDLFolder(
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
			});
		pageSettings.setSeoSettings(
			() -> new SEOSettings() {
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
										SitemapSettings.ChangeFrequency.class));
								setInclude(RandomTestUtil::randomBoolean);
								setIncludeChildSitePages(
									RandomTestUtil::randomBoolean);
								setPagePriority(RandomTestUtil::randomDouble);
							}
						});
				}
			});
		pageSettings.setQueryString(RandomTestUtil::randomString);

		return pageSettings;
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

		SitePage sitePage = new SitePage();

		sitePage.setAvailableLanguages(
			() -> LocaleUtil.toW3cLanguageIds(
				new Locale[] {LocaleUtil.US, LocaleUtil.SPAIN}));
		sitePage.setExternalReferenceCode(externalReferenceCode);
		sitePage.setFriendlyUrlPath_i18n(
			() -> HashMapBuilder.put(
				LocaleUtil.toBCP47LanguageId(LocaleUtil.SPAIN),
				StringPool.FORWARD_SLASH +
					StringUtil.toLowerCase(RandomTestUtil.randomString())
			).put(
				LocaleUtil.toBCP47LanguageId(LocaleUtil.US),
				StringPool.FORWARD_SLASH +
					StringUtil.toLowerCase(RandomTestUtil.randomString())
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

		PageSettings pageSettings = _getPageSettings(type);

		pageSettings.setHiddenFromNavigation(RandomTestUtil::randomBoolean);
		pageSettings.setPriority(
			_priorities.merge(
				parentSitePageExternalReferenceCode, 0,
				(oldPriority, defaultPriority) -> oldPriority + 1));

		sitePage.setPageSettings(pageSettings);

		sitePage.setParentSitePageExternalReferenceCode(
			parentSitePageExternalReferenceCode);
		sitePage.setTaxonomyCategoryItemExternalReferences(
			AssetTestUtil.randomTaxonomyCategoryItemExternalReferences(
				testCompany.getGroupId(), serviceContext));
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

	private SitePage _postSiteSitePageWithPageSpecificationsWithCustomFields(
			SitePage.Type type)
		throws Exception {

		SitePage randomSitePage = _getRandomSitePage(type);

		if (type == SitePage.Type.CONTENT_PAGE) {
			randomSitePage.setPageSpecifications(
				PageSpecificationsTestUtil.getContentPageSpecifications(
					randomSitePage.getExternalReferenceCode()));
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
			testGroup.getExternalReferenceCode(), randomSitePage);

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
			sitePage.getExternalReferenceCode(), sitePage);

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

		_assertSitePage(
			_layoutLocalService.getLayoutByExternalReferenceCode(
				sitePage.getExternalReferenceCode(), testGroup.getGroupId()),
			sitePage);

		sitePage.setFriendlyUrlPath_i18n(
			() -> HashMapBuilder.put(
				LocaleUtil.toBCP47LanguageId(LocaleUtil.SPAIN),
				StringPool.FORWARD_SLASH +
					StringUtil.toLowerCase(RandomTestUtil.randomString())
			).put(
				LocaleUtil.toBCP47LanguageId(LocaleUtil.US),
				StringPool.FORWARD_SLASH +
					StringUtil.toLowerCase(RandomTestUtil.randomString())
			).build());

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

		Layout layout = LayoutTestUtil.addTypePortletLayout(testGroup);

		sitePage.setParentSitePageExternalReferenceCode(
			layout.getExternalReferenceCode());

		pageSettings.setPriority(0);

		_testPatchSiteSitePage(
			sitePage,
			new SitePage() {
				{
					setExternalReferenceCode(
						sitePage::getExternalReferenceCode);
					setParentSitePageExternalReferenceCode(
						layout::getExternalReferenceCode);
					setType(sitePage::getType);
					setUuid(sitePage::getUuid);
				}
			});

		sitePage.setTaxonomyCategoryItemExternalReferences(
			AssetTestUtil.randomTaxonomyCategoryItemExternalReferences(
				testCompany.getGroupId(), serviceContext));

		_testPatchSiteSitePage(
			sitePage,
			new SitePage() {
				{
					setExternalReferenceCode(
						sitePage::getExternalReferenceCode);
					setTaxonomyCategoryItemExternalReferences(
						sitePage::getTaxonomyCategoryItemExternalReferences);
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
				null, oldDraftLayoutStatus);

		ContentPageSpecification publishedContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				draftContentPageSpecification.getExternalReferenceCode(),
				oldPublishedLayoutStatus);

		publishedContentPageSpecification.setExternalReferenceCode(
			sitePage.getExternalReferenceCode());

		sitePage.setPageSpecifications(
			() -> new PageSpecification[] {
				publishedContentPageSpecification, draftContentPageSpecification
			});

		SitePageResource sitePageResource = _getSitePageResource(
			"pageSpecifications");

		SitePage postSitePage = sitePageResource.postSiteSitePage(
			testGroup.getExternalReferenceCode(), sitePage);

		_assertPageSpecifications(
			draftContentPageSpecification, publishedContentPageSpecification,
			postSitePage);

		draftContentPageSpecification.setStatus(newDraftLayoutStatus);
		publishedContentPageSpecification.setStatus(newPublishedLayoutStatus);

		_assertPageSpecifications(
			draftContentPageSpecification, publishedContentPageSpecification,
			sitePageResource.patchSiteSitePage(
				testGroup.getExternalReferenceCode(),
				postSitePage.getExternalReferenceCode(),
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
				postSitePage.getPageSpecifications());

		SitePage patchSitePage = sitePageResource.patchSiteSitePage(
			testGroup.getExternalReferenceCode(),
			postSitePage.getExternalReferenceCode(),
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
			testGroup.getExternalReferenceCode(), randomSitePage);

		WidgetPageSettings widgetPageSettings =
			(WidgetPageSettings)sitePage.getPageSettings();

		widgetPageSettings.setLayoutTemplateId("2_columns_ii");

		PageSpecification[] widgetPageSpecifications =
			PageSpecificationsTestUtil.getWidgetPageSpecifications(
				null, "2_columns_ii",
				randomSitePage.getExternalReferenceCode());

		SitePage patchSitePage = sitePageResource.patchSiteSitePage(
			testGroup.getExternalReferenceCode(),
			sitePage.getExternalReferenceCode(),
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
					sitePage.getExternalReferenceCode(),
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
			randomSitePage, randomSitePage);

		WidgetPageSettings widgetPageSettings =
			(WidgetPageSettings)sitePage.getPageSettings();

		widgetPageSettings.setCustomizable(true);
		widgetPageSettings.setCustomizableSectionIds(
			new String[] {"column-1", "column-3"});
		widgetPageSettings.setCustomMetaTags(new CustomMetaTag[0]);
		widgetPageSettings.setLayoutTemplateId("1_2_columns_i");
		widgetPageSettings.setNavigationSettings(
			new NavigationSettings() {
				{
					setTarget(() -> null);
					setTargetType(TargetType.SPECIFIC_FRAME);
				}
			});
		widgetPageSettings.setOpenGraphSettings(
			new OpenGraphSettings() {
				{
					setDescription_i18n(new HashMap<>());
					setImageAlt_i18n(new HashMap<>());
					setTitle_i18n(new HashMap<>());
				}
			});
		widgetPageSettings.setQueryString(() -> null);
		widgetPageSettings.setSeoSettings(
			new SEOSettings() {
				{
					setCustomCanonicalURL_i18n(new HashMap<>());
					setDescription_i18n(new HashMap<>());
					setHtmlTitle_i18n(new HashMap<>());
					setRobots_i18n(new HashMap<>());
					setSeoKeywords_i18n(new HashMap<>());
					setSitemapSettings(
						new SitemapSettings() {
							{
								setChangeFrequency(ChangeFrequency.DAILY);
								setInclude(true);
								setIncludeChildSitePages(true);
								setPagePriority(0.0);
							}
						});
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
			sitePageWithWidgetPageTemplate, sitePageWithWidgetPageTemplate);

		WidgetPageSettings widgetPageSettings =
			(WidgetPageSettings)sitePage.getPageSettings();

		widgetPageSettings.setCustomMetaTags(new CustomMetaTag[0]);
		widgetPageSettings.setInheritChanges(false);
		widgetPageSettings.setLayoutTemplateId("2_columns_ii");
		widgetPageSettings.setNavigationSettings(
			new NavigationSettings() {
				{
					setTarget(() -> null);
					setTargetType(TargetType.SPECIFIC_FRAME);
				}
			});
		widgetPageSettings.setOpenGraphSettings(
			new OpenGraphSettings() {
				{
					setDescription_i18n(new HashMap<>());
					setImageAlt_i18n(new HashMap<>());
					setTitle_i18n(new HashMap<>());
				}
			});
		widgetPageSettings.setQueryString(() -> null);
		widgetPageSettings.setSeoSettings(
			new SEOSettings() {
				{
					setCustomCanonicalURL_i18n(new HashMap<>());
					setDescription_i18n(new HashMap<>());
					setHtmlTitle_i18n(new HashMap<>());
					setRobots_i18n(new HashMap<>());
					setSeoKeywords_i18n(new HashMap<>());
					setSitemapSettings(
						new SitemapSettings() {
							{
								setChangeFrequency(ChangeFrequency.DAILY);
								setInclude(true);
								setIncludeChildSitePages(true);
								setPagePriority(0.0);
							}
						});
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

		SitePage sitePage = _getRandomSitePage(type);

		PageSpecification[] pageSpecifications =
			PageSpecificationsTestUtil.getPageSpecifications(
				sitePage.getExternalReferenceCode(), type);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId());

		FavIcon.FavIconType favIconType =
			FavIcon.FavIconType.ITEM_EXTERNAL_REFERENCE;

		for (PageSpecification pageSpecification : pageSpecifications) {
			pageSpecification.setSettings(
				SettingsTestUtil.getSettings(favIconType, serviceContext));

			favIconType = FavIcon.FavIconType.CLIENT_EXTENSION;
		}

		sitePage.setPageSpecifications(pageSpecifications);

		SitePageResource sitePageResource = _getSitePageResource(
			"pageSpecifications");

		SitePage postSitePage = sitePageResource.postSiteSitePage(
			testGroup.getExternalReferenceCode(), sitePage);

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
		_testPostSiteSitePageWithPageSpecificationsWithWidgetPageSpecification();

		SitePage sitePage = _getRandomSitePage(SitePage.Type.CONTENT_PAGE);

		ContentPageSpecification draftContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				null, PageSpecification.Status.APPROVED);

		ContentPageSpecification publishedContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				draftContentPageSpecification.getExternalReferenceCode(),
				PageSpecification.Status.APPROVED);

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
				testGroup.getExternalReferenceCode(), sitePage));
	}

	private void _testPostSiteSitePageWithPageSpecifications(
			PageSpecification.Status draftLayoutStatus,
			PageSpecification.Status publishedLayoutStatus)
		throws Exception {

		SitePage sitePage = _getRandomSitePage(SitePage.Type.CONTENT_PAGE);

		ContentPageSpecification draftContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				null, draftLayoutStatus);

		ContentPageSpecification publishedContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				draftContentPageSpecification.getExternalReferenceCode(),
				publishedLayoutStatus);

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
				testGroup.getExternalReferenceCode(), sitePage));
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

	private void _testPostSiteSitePageWithPageSpecificationsWithWidgetPageSpecification()
		throws Exception {

		SitePageResource sitePageResource = _getSitePageResource(
			"pageSpecifications");

		SitePage randomSitePage = _getRandomSitePage(SitePage.Type.WIDGET_PAGE);

		randomSitePage.setPageSpecifications(
			PageSpecificationsTestUtil.getWidgetPageSpecifications(
				null, "1_column", randomSitePage.getExternalReferenceCode()));

		SitePage sitePage = sitePageResource.postSiteSitePage(
			testGroup.getExternalReferenceCode(), randomSitePage);

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

		_testPostSiteSitePage(_getRandomSitePageWithWidgetPageTemplate(true));
	}

	private void _testPutSiteSitePage(
			ServiceContext serviceContext, SitePage.Type type)
		throws Exception {

		SitePage sitePage = testPostSiteSitePage_addSitePage(
			_getRandomSitePage(type));

		_assertSitePage(
			_layoutLocalService.getLayoutByExternalReferenceCode(
				sitePage.getExternalReferenceCode(), testGroup.getGroupId()),
			sitePage);

		Layout layout = LayoutTestUtil.addTypePortletLayout(testGroup);

		sitePage = _getRandomSitePage(
			sitePage.getExternalReferenceCode(),
			layout.getExternalReferenceCode(), serviceContext, type,
			sitePage.getUuid());

		SitePage putSitePage = sitePageResource.putSiteSitePage(
			testGroup.getExternalReferenceCode(),
			sitePage.getExternalReferenceCode(), sitePage);

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

	private SitePage _testPutSiteSitePage(
			SitePage expectedSitePage, SitePage sitePage)
		throws Exception {

		SitePage putSitePage = sitePageResource.putSiteSitePage(
			testGroup.getExternalReferenceCode(),
			sitePage.getExternalReferenceCode(), sitePage);

		assertEquals(expectedSitePage, putSitePage);
		assertValid(putSitePage);

		_assertSitePage(
			_layoutLocalService.getLayoutByExternalReferenceCode(
				sitePage.getExternalReferenceCode(), testGroup.getGroupId()),
			putSitePage);

		return putSitePage;
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
		_testPutSiteSitePageWithPageSpecificationsWithCustomFields();
		_testPutSiteSitePageWithPageSpecificationsWithWidgetPageSpecification();
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
				null, oldDraftLayoutStatus);

		ContentPageSpecification publishedContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				draftContentPageSpecification.getExternalReferenceCode(),
				oldPublishedLayoutStatus);

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
				sitePage.getExternalReferenceCode(), sitePage));

		draftContentPageSpecification.setStatus(newDraftLayoutStatus);
		publishedContentPageSpecification.setStatus(newPublishedLayoutStatus);

		_assertPageSpecifications(
			draftContentPageSpecification, publishedContentPageSpecification,
			sitePageResource.putSiteSitePage(
				testGroup.getExternalReferenceCode(),
				sitePage.getExternalReferenceCode(), sitePage));
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
			postSitePage.getExternalReferenceCode(), putSitePage);

		PageSpecificationsTestUtil.assertCustomFields(
			TransformUtil.transform(
				putSitePage.getPageSpecifications(),
				pageSpecification -> pageSpecification.getCustomFields(),
				CustomField[].class),
			testGroup.getGroupId(), updateSitePage.getPageSpecifications());
	}

	private void _testPutSiteSitePageWithPageSpecificationsWithWidgetPageSpecification()
		throws Exception {

		SitePageResource sitePageResource = _getSitePageResource(
			"pageSpecifications");

		SitePage randomSitePage = _getRandomSitePage(SitePage.Type.WIDGET_PAGE);

		randomSitePage.setPageSpecifications(
			PageSpecificationsTestUtil.getWidgetPageSpecifications(
				null, "1_column", randomSitePage.getExternalReferenceCode()));

		SitePage sitePage = sitePageResource.postSiteSitePage(
			testGroup.getExternalReferenceCode(), randomSitePage);

		WidgetPageSettings widgetPageSettings =
			(WidgetPageSettings)sitePage.getPageSettings();

		widgetPageSettings.setLayoutTemplateId("2_columns_ii");

		sitePage.setPageSpecifications(
			() -> PageSpecificationsTestUtil.getWidgetPageSpecifications(
				null, "2_columns_ii", sitePage.getExternalReferenceCode()));

		SitePage putSitePage = sitePageResource.putSiteSitePage(
			testGroup.getExternalReferenceCode(),
			sitePage.getExternalReferenceCode(), sitePage);

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
					sitePage.getExternalReferenceCode(), sitePage);

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
			randomSitePage, randomSitePage);

		WidgetPageSettings widgetPageSettings =
			(WidgetPageSettings)sitePage.getPageSettings();

		widgetPageSettings.setCustomizable(true);
		widgetPageSettings.setCustomizableSectionIds(
			new String[] {"column-1", "column-3"});
		widgetPageSettings.setLayoutTemplateId("1_2_columns_i");

		sitePage = _testPutSiteSitePage(sitePage, sitePage);

		widgetPageSettings = (WidgetPageSettings)sitePage.getPageSettings();

		widgetPageSettings.setCustomizableSectionIds(new String[] {"column-2"});

		SitePage expectedSitePage = sitePage.clone();

		WidgetPageSettings expectedWidgetPageSettings =
			(WidgetPageSettings)expectedSitePage.getPageSettings();

		widgetPageSettings.setLayoutTemplateId((String)null);

		expectedWidgetPageSettings.setLayoutTemplateId("2_columns_ii");

		sitePage = _testPutSiteSitePage(expectedSitePage, sitePage);

		widgetPageSettings = (WidgetPageSettings)sitePage.getPageSettings();

		widgetPageSettings.setCustomizable(false);

		expectedSitePage = sitePage.clone();

		expectedWidgetPageSettings =
			(WidgetPageSettings)expectedSitePage.getPageSettings();

		widgetPageSettings.setCustomizableSectionIds((String[])null);
		widgetPageSettings.setLayoutTemplateId((String)null);

		expectedWidgetPageSettings.setCustomizableSectionIds(new String[0]);
		expectedWidgetPageSettings.setLayoutTemplateId("2_columns_ii");

		_testPutSiteSitePage(expectedSitePage, sitePage);
	}

	private void _testPutSiteSitePageWithWidgetPageSettingsWithWidgetPageTemplate()
		throws Exception {

		SitePage sitePageWithWidgetPageTemplate =
			_getRandomSitePageWithWidgetPageTemplate(false);

		SitePage sitePage = _testPutSiteSitePage(
			sitePageWithWidgetPageTemplate, sitePageWithWidgetPageTemplate);

		WidgetPageSettings widgetPageSettings =
			(WidgetPageSettings)sitePage.getPageSettings();

		widgetPageSettings.setInheritChanges(false);

		_testPutSiteSitePage(sitePage, sitePage);
	}

	private void _testUpdateSiteSitePageWithPriority(
			UnsafeTriConsumer<String, Integer, SitePage, Exception>
				unsafeTriConsumer)
		throws Exception {

		Page<SitePage> page = sitePageResource.getSiteSitePagesPage(
			testGroup.getExternalReferenceCode(), null, null, null,
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

	private static final List<SitePage.Type> _types = Arrays.asList(
		SitePage.Type.CONTENT_PAGE, SitePage.Type.WIDGET_PAGE);

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private LayoutLocalService _layoutLocalService;

	private final Map<String, Integer> _priorities = new HashMap<>();

}